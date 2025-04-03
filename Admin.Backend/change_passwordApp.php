<?php
session_start();

// Database connection
$conn = new mysqli("localhost", "root", "", "pcx_db");

// Check connection
if ($conn->connect_error) {
    die("Connection failed: " . $conn->connect_error);
}

if ($_SERVER['REQUEST_METHOD'] == 'POST') {
    $userId = $_POST['user_id'];  // Get user ID
    $newPassword = $_POST['new_password'];  // Get new password

    // Hash the password before storing it
    $hashedPassword = password_hash($newPassword, PASSWORD_DEFAULT);

    // Update password in the database
    $stmt = $conn->prepare("UPDATE customers SET password = ? WHERE user_id = ?");
    $stmt->bind_param("si", $hashedPassword, $userId);  // Bind parameters: 's' for string (password), 'i' for integer (user_id)
    
    // Execute the query
    if ($stmt->execute()) {
        echo json_encode(["status" => "success", "message" => "Password updated successfully."]);
    } else {
        echo json_encode(["status" => "error", "message" => "Failed to update password."]);
    }

    $stmt->close();
    $conn->close();
} else {
    echo json_encode(["status" => "error", "message" => "Invalid request."]);
}
?>
