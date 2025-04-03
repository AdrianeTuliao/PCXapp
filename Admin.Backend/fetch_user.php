<?php
session_start();

// Database connection
$conn = new mysqli("localhost", "root", "", "pcx_db");

// Check connection
if ($conn->connect_error) {
    die("Connection failed: " . $conn->connect_error);
}

// Fetch all users
if ($stmt = $conn ->prepare("SELECT username, full_name, email, status FROM users"));
    $stmt -> execute();
    $result = $stmt->get_result();

    $users = []; // Users

    // Check kung may madidisplay
    while ($row = $result->fetch_assoc()) {  // Loops and store it in $users
        $users[] = $row;
    }
    echo json_encode($users); // Puts users info in JSON format to display in Users
    
$stmt->close();
$conn->close();
?>