<?php
session_start();

// Database connection
$conn = new mysqli("localhost", "root", "", "pcx_db");

// Check connection
if ($conn->connect_error) {
    die("Connection failed: " . $conn->connect_error);
}
// Get JSON input
$data = json_decode(file_get_contents("php://input"), true);

$user_id = $_SESSION['user_id']; // Assuming user_id is stored in session
$response = ['success' => false];

// Handle email & contact number update
if (isset($data['email']) && isset($data['contact_number'])) {
    $email = $data['email'];
    $contact_number = $data['contact_number'];

    $stmt = $conn->prepare("UPDATE users SET email = ?, contact_number = ? WHERE user_id = ?");
    $stmt->bind_param("ssi", $email, $contact_number, $user_id);

    if ($stmt->execute()) {
        $response['success'] = true;
    }
    $stmt->close();
}

// Handle profile picture update if file exists
if ($_FILES['profile_pic']['error'] === 0) {
    $upload_dir = "uploads/";
    $file_name = $user_id . "_" . basename($_FILES['profile_pic']['name']);
    $upload_path = $upload_dir . $file_name;

    if (move_uploaded_file($_FILES['profile_pic']['tmp_name'], $upload_path)) {
        // Update profile picture in DB
        $stmt = $conn->prepare("UPDATE users SET profile_pic = ? WHERE user_id = ?");
        $stmt->bind_param("si", $file_name, $user_id);
        $stmt->execute();
        $stmt->close();

        $response['success'] = true;
    }
}

echo json_encode($response);
?>
