<?php
header('Content-Type: application/json');
error_reporting(0); // Suppress PHP warnings and notices
session_start();

// Database connection
$conn = new mysqli("localhost", "root", "", "pcx_db");

if ($conn->connect_error) {
    echo json_encode([
        "success" => false,
        "message" => "Database connection failed"
    ]);
    exit();
}

// Get POST data safely with fallback values
$username = isset($_POST['username']) ? $_POST['username'] : '';
$contact_number = isset($_POST['contact_number']) ? $_POST['contact_number'] : '';
$city = isset($_POST['city']) ? $_POST['city'] : '';
$email = isset($_POST['email']) ? $_POST['email'] : '';
$password_raw = isset($_POST['password']) ? $_POST['password'] : '';

if (empty($username) || empty($contact_number) || empty($city) || empty($email) || empty($password_raw)) {
    echo json_encode([
        "success" => false,
        "message" => "All fields are required"
    ]);
    exit();
}

$password = password_hash($password_raw, PASSWORD_DEFAULT);

// Check if email already exists (Prepared statement)
$checkEmailSql = "SELECT * FROM customers WHERE email = ?";
$stmt = $conn->prepare($checkEmailSql);
$stmt->bind_param("s", $email);
$stmt->execute();
$result = $stmt->get_result();

if ($result->num_rows > 0) {
    echo json_encode([
        "success" => false,
        "message" => "Email already exists"
    ]);
    exit();
}

// Insert query (Prepared statement)
$insertSql = "INSERT INTO customers (username, contact_number, city, email, password) VALUES (?, ?, ?, ?, ?)";
$insertStmt = $conn->prepare($insertSql);
$insertStmt->bind_param("sssss", $username, $contact_number, $city, $email, $password);

if ($insertStmt->execute()) {
    echo json_encode([
        "success" => true,
        "message" => "Account created successfully"
    ]);
} else {
    echo json_encode([
        "success" => false,
        "message" => "Error creating account"
    ]);
}

$insertStmt->close();
$stmt->close();
$conn->close();
?>
