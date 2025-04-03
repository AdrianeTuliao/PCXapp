<?php
header('Content-Type: application/json');

error_reporting(E_ALL);
ini_set('display_errors', 1);

session_start();

// Database connection
$conn = new mysqli("localhost", "root", "", "pcx_db");

// Check connection
if ($conn->connect_error) {
    die("Connection failed: " . $conn->connect_error);
}

// Check request method
if ($_SERVER["REQUEST_METHOD"] == "POST") {

    $username = $_POST['username'] ?? '';
    $password = $_POST['password'] ?? '';

    // Check if username or password is empty
    if (empty($username) || empty($password)) {
        echo json_encode([
            "success" => false,
            "message" => "Username or Password is missing."
        ]);
        exit;
    }

    // Fetch all necessary user data
    $stmt = $conn->prepare("SELECT user_id, username, password, email, contact_number, city FROM customers WHERE username = ?");
    
    // Check if the prepare statement failed
    if (!$stmt) {
        echo json_encode([
            "success" => false,
            "message" => "Prepare failed: " . $conn->error
        ]);
        exit;
    }

    // Bind the parameters and execute the statement
    $stmt->bind_param("s", $username);
    $stmt->execute();
    $stmt->store_result();

    // Check if the user is not found
    if ($stmt->num_rows === 0) {
        echo json_encode([
            "success" => false,
            "message" => "User not found."
        ]);
    } else {
        $stmt->bind_result($user_id, $username, $hashed_password, $email, $contact_number, $city);
        $stmt->fetch();

        // Check if the password is correct
        if (password_verify($password, $hashed_password)) {
            echo json_encode([
                "success" => true,
                "message" => "Login successful",
                "user" => [
                    "id" => $user_id,
                    "username" => $username,
                    "email" => $email,
                    "contactNumber" => $contact_number,
                    "city" => $city
                ]
            ]);
        } else {
            echo json_encode([
                "success" => false,
                "message" => "Incorrect password."
            ]);
        }
    }

    // Close the statement and connection
    $stmt->close();
    $conn->close();
} else {
    echo json_encode([
        "success" => false,
        "message" => "Invalid request method."
    ]);
}
?>
