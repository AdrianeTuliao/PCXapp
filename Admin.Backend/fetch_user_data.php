<?php
session_start();

// Database connection
$conn = new mysqli("localhost", "root", "", "pcx_db");

// Check connection
if ($conn->connect_error) {
    die("Connection failed: " . $conn->connect_error);
}

// Fetch user_id stored in session
$user_id = $_SESSION['user_id'];

// Fetch user data from database
if ($stmt = $conn->prepare("SELECT full_name, email, contact_number FROM users WHERE id = ?")) { 
    $stmt->bind_param("i", $user_id); // Prevents sql injection (i because user_id is integer)
    $stmt->execute();
    $stmt->bind_result($full_name, $email, $contact_number);

    if ($stmt->fetch()) { // Check if info is stored in data
        $user_data = [
            'full_name' => $full_name,
            'email' => $email,
            'contact_number' => $contact_number
        ];
    }
    echo json_encode($user_data); // Puts user info in JSON format, to display the user's info

    $stmt->close();
}
$conn->close();
?>
