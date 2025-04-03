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

// Check if user_id is provided
if (!isset($_POST['user_id']) || empty($_POST['user_id'])) {
    echo json_encode(["success" => false, "message" => "User ID is required"]);
    exit;
}

$user_id = filter_var($_POST['user_id'], FILTER_VALIDATE_INT);
if (!$user_id) {
    echo json_encode(["success" => false, "message" => "Invalid User ID"]);
    exit;
}

$query = "SELECT id, user_id, product_id, product_name, product_price, product_image_url, quantity, product_stock 
          FROM cart_items 
          WHERE user_id = ?";

$stmt = $conn->prepare($query);
$stmt->bind_param("i", $user_id);
$stmt->execute();
$result = $stmt->get_result();

$cart_items = [];
while ($row = $result->fetch_assoc()) {
    $cart_items[] = [
        "product_id" => $row["product_id"] ?? 0,
        "product_name" => $row["product_name"] ?? "",
        "product_price" => $row["product_price"] ?? 0.0,
        "product_image_url" => $row["product_image_url"] ?? "",
        "quantity" => $row["quantity"] ?? 1,
        "product_stock" => $row["product_stock"] ?? 0
    ];
}

// Debugging: Log output to server logs
error_log("Fetched cart items for user_id $user_id: " . print_r($cart_items, true));

echo json_encode(["success" => true, "cart_items" => $cart_items]);

// Clean up
$stmt->close();
$conn->close();
?>
