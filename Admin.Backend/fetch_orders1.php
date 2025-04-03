<?php
session_start();

// Database connection
$conn = new mysqli("localhost", "root", "", "pcx_db");

// Check connection
if ($conn->connect_error) {
    http_response_code(500);
    echo json_encode(["error" => "Database connection failed."]);
    exit();
}

// Fetch the 5 most recent non-cancelled orders
$sql_recent = "SELECT id_order, order_date, customer_name, total, payment_status, items, order_status, image, quantity 
               FROM orders 
               WHERE LOWER(order_status) != 'cancelled' 
               ORDER BY order_date DESC 
               LIMIT 5";
$stmt_recent = $conn->prepare($sql_recent);
$stmt_recent->execute();
$result_recent = $stmt_recent->get_result();

$recent_orders = [];
while ($row = $result_recent->fetch_assoc()) {
    $recent_orders[] = $row;
}
$stmt_recent->close();

// Fetch **all** completed orders (instead of all non-cancelled orders) for the graph
$sql_all = "SELECT order_date, total FROM orders WHERE LOWER(order_status) = 'completed'";  // Filter for completed orders
$stmt_all = $conn->prepare($sql_all);
$stmt_all->execute();
$result_all = $stmt_all->get_result();

$all_orders = [];
while ($row = $result_all->fetch_assoc()) {
    $all_orders[] = $row;
}
$stmt_all->close();

$conn->close();

// Return both recent and all orders as JSON
echo json_encode([
    "recent_orders" => $recent_orders,
    "all_orders" => $all_orders
]);
?>
