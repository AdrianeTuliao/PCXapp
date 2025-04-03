<?php
error_reporting(E_ALL);
ini_set('display_errors', 1);
header('Content-Type: application/json');

session_start();

// Database connection
$conn = new mysqli("localhost", "root", "", "pcx_db");

// Check connection
if ($conn->connect_error) {
    echo json_encode(["success" => false, "message" => "Database connection failed"]);
    exit;
}

// Check if user_id and product_id are provided
if (!isset($_POST['user_id']) || !isset($_POST['product_id'])) {
    echo json_encode(["success" => false, "message" => "User ID and Product ID are required"]);
    exit;
}

$user_id = filter_var($_POST['user_id'], FILTER_VALIDATE_INT);
$product_id = filter_var($_POST['product_id'], FILTER_VALIDATE_INT);

if (!$user_id || !$product_id) {
    echo json_encode(["success" => false, "message" => "Invalid User ID or Product ID"]);
    exit;
}

// Delete the cart item
$query = "DELETE FROM cart_items WHERE user_id = ? AND product_id = ?";
$stmt = $conn->prepare($query);
$stmt->bind_param("ii", $user_id, $product_id);

if ($stmt->execute()) {
    echo json_encode(["success" => true, "message" => "Item removed from cart"]);
} else {
    echo json_encode(["success" => false, "message" => "DB Error:" . $stmt->error]);
}

// Clean up
$stmt->close();
$conn->close();
?>
