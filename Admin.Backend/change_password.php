<?php
session_start();

// Database connection
$conn = new mysqli("localhost", "root", "", "pcx_db");

//Check connection
if ($conn->connect_error) {
    die("Connection failed: " . $conn->connect_error);
}


$username = $_SESSION['username'];

if ($_SERVER["REQUEST_METHOD"] === "POST") {
    $currentPassword = $_POST["currentPassword"];
    $newPassword = $_POST["newPassword"];
    $confirmPassword = $_POST["confirmPassword"];

    // Validate new password
    if ($newPassword !== $confirmPassword) {
        echo json_encode(["status" => "error", "message" => "Passwords do not match."]);
        exit();
    }
    if (strlen($newPassword) < 8) {
        echo json_encode(["status" => "error", "message" => "Password must be at least 8 characters long."]);
        exit();
    }

    // Fetch current hashed password sa database
    $query = "SELECT password FROM users WHERE username = ?";
    $stmt = $conn->prepare($query);
    $stmt->bind_param("s", $username);
    $stmt->execute();
    $stmt->bind_result($hashedPassword);
    $stmt->fetch();
    $stmt->close();

    // Verify yung current password
    if (!password_verify($currentPassword, $hashedPassword)) {
        echo json_encode(["status" => "error", "message" => "Current password is incorrect."]);
        exit();
    }

    // Hash yung new password
    $newHashedPassword = password_hash($newPassword, PASSWORD_DEFAULT);

    // Update password sa database
    $updateQuery = "UPDATE users SET password = ? WHERE username = ?";
    $updateStmt = $conn->prepare($updateQuery);
    $updateStmt->bind_param("ss", $newHashedPassword, $username);

    if ($updateStmt->execute()) {
        echo json_encode(["status" => "success", "message" => "Password updated successfully."]);
    } else {
        echo json_encode(["status" => "error", "message" => "Failed to update password."]);
    }
    $updateStmt->close();
}
?>
