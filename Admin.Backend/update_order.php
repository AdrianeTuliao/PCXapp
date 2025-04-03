<?php
session_start();
header('Content-Type: application/json');

// Check if the user is logged in
if (!isset($_SESSION['username'])) {
    echo json_encode(['success' => false, 'message' => 'Unauthorized access.']);
    exit();
}

// Only allow POST requests
if ($_SERVER['REQUEST_METHOD'] !== 'POST') {
    echo json_encode(['success' => false, 'message' => 'Invalid request method.']);
    exit();
}

// Database connection
$conn = new mysqli("localhost", "root", "", "pcx_db");

// Check connection
if ($conn->connect_error) {
    http_response_code(500);
    echo json_encode(["success" => false, "message" => "Database connection failed."]);
    exit();
}

// Get and validate POST data
$id_order = $_POST['id_order'] ?? '';
$payment_status = $_POST['payment_status'] ?? '';
$order_status = $_POST['order_status'] ?? '';

if (empty($id_order) || empty($payment_status) || empty($order_status)) {
    echo json_encode(['success' => false, 'message' => 'Missing fields.']);
    exit();
}

// Prepare the SQL query
$stmt = $conn->prepare("UPDATE orders SET payment_status = ?, order_status = ? WHERE id_order = ?");
if ($stmt === false) {
    echo json_encode(['success' => false, 'message' => 'Prepare failed: ' . $conn->error]);
    exit();
}

$stmt->bind_param("ssi", $payment_status, $order_status, $id_order);

// Execute and return response
if ($stmt->execute()) {
    echo json_encode(['success' => true]);
} else {
    echo json_encode(['success' => false, 'message' => 'Execute failed: ' . $stmt->error]);
}

$stmt->close();
$conn->close();
?>
