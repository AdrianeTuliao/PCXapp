<?php
session_start();

// Database connection
$conn = new mysqli("localhost", "root", "", "pcx_db");

// Check connection
if ($conn->connect_error) {
    die("Connection failed: " . $conn->connect_error);
}

// Get POST data from the app
$customer_name = $_POST['customer_name'];
$items = $_POST['items'];
$total = $_POST['total'];
$quantity = $_POST['quantity'];
$product_id = $_POST['product_id'];
$imageUrl = $_POST['imageUrl'];

// Validate input
if (!$customer_name || !$items || !$total || !$quantity || !$product_id || !$imageUrl) {
    echo json_encode([
        "success" => false,
        "message" => "Missing required fields"
    ]);
    exit();
}

// Start transaction
$conn->begin_transaction();

try {
    // Insert order
    $order_date = date('Y-m-d H:i:s');
    $payment_status = "Pending";
    $order_status = "Pending Orders";

$stmt = $conn->prepare("INSERT INTO orders (order_date, customer_name, total, payment_status, items, order_status, quantity, image) VALUES (?, ?, ?, ?, ?, ?, ?, ?)");

$stmt->bind_param("ssdsssis", $order_date, $customer_name, $total, $payment_status, $items, $order_status, $quantity, $imageUrl);

    
    if (!$stmt->execute()) {
        throw new Exception("Failed to insert order: " . $stmt->error);
    }

    // Update product stocks
    $updateStockQuery = "UPDATE products SET stocks = stocks - ? WHERE id = ? AND stocks >= ?";
    $stmtStock = $conn->prepare($updateStockQuery);
    $stmtStock->bind_param("iii", $quantity, $product_id, $quantity);

    if (!$stmtStock->execute()) {
        throw new Exception("Failed to update stock: " . $stmtStock->error);
    }

    if ($stmtStock->affected_rows === 0) {
        throw new Exception("Not enough stock available for this product.");
    }

    // Commit transaction
    $conn->commit();

    echo json_encode([
        "success" => true,
        "message" => "Order placed successfully"
    ]);

    // Close the prepared statements
} catch (Exception $e) {
    $conn->rollback(); 

    echo json_encode([
        "success" => false,
        "message" => $e->getMessage()
    ]);
}

$conn->close();
?>
