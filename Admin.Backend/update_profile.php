<?php
session_start();

if (!isset($_SESSION['username'])) {
    header("Location: login.php");
    exit();
}

$conn = new mysqli("localhost", "root", "", "pcx_db");

if ($conn->connect_error) {
    die("Connection failed: " . $conn->connect_error);
}

$response = ["status" => "error", "message" => "An error occurred."];

if ($_SERVER["REQUEST_METHOD"] == "POST") {
    $fullName = $_POST['fullName'];
    $email = $_POST['email'];
    $contactNumber = $_POST['contactNumber'];
    $username = $_SESSION['username'];

    // Update profile information
    $stmt = $conn->prepare("UPDATE users SET full_name=?, email=?, contact_number=? WHERE username=?");
    $stmt->bind_param("ssss", $fullName, $email, $contactNumber, $username);

    if ($stmt->execute()) {
        $response["status"] = "success";
        $response["message"] = "Profile updated successfully.";
        $_SESSION['full_name'] = $fullName; // Update session full name
    } else {
        $response["message"] = $stmt->error;
    }

    $stmt->close();

    // Handle profile picture upload
    if (isset($_FILES['profilePic']) && $_FILES['profilePic']['error'] == UPLOAD_ERR_OK) {
        $targetDir = "uploads/";
        $targetFile = $targetDir . basename($_FILES["profilePic"]["name"]);
        $imageFileType = strtolower(pathinfo($targetFile, PATHINFO_EXTENSION));

        // Check if image file is a actual image or fake image
        $check = getimagesize($_FILES["profilePic"]["tmp_name"]);
        if ($check !== false) {
            if (move_uploaded_file($_FILES["profilePic"]["tmp_name"], $targetFile)) {
                // Update profile picture path in the database
                $stmt = $conn->prepare("UPDATE users SET profile_pic=? WHERE username=?");
                $stmt->bind_param("ss", $targetFile, $username);

                if ($stmt->execute()) {
                    $response["status"] = "success";
                    $response["message"] = "Profile updated successfully.";
                    $_SESSION['profile_pic'] = $targetFile; // Update session profile picture
                } else {
                    $response["message"] = $stmt->error;
                }

                $stmt->close();
            } else {
                $response["message"] = "Sorry, there was an error uploading your file.";
            }
        } else {
            $response["message"] = "File is not an image.";
        }
    }
}

$conn->close();
echo json_encode($response);
?>