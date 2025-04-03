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

// Get status filter from GET or POST request
$statusFilter = isset($_GET['order_status']) ? $_GET['order_status'] : '';

// If a status is provided, filter by that status
if (!empty($statusFilter)) {
    $sql = "SELECT id_order, order_date, customer_name, total, payment_status, items, order_status, image, quantity FROM orders 
            WHERE LOWER(order_status) = LOWER(?)";
    $stmt = $conn->prepare($sql);
    $stmt->bind_param("s", $statusFilter);
} else {
    // No status filter, get all orders
    $sql = "SELECT id_order, order_date, customer_name, total, payment_status, items, order_status, image, quantity FROM orders";
    $stmt = $conn->prepare($sql);
}

if ($stmt) {
    $stmt->execute();
    $result = $stmt->get_result();

    $orders = [];

    while ($row = $result->fetch_assoc()) {
        $orders[] = $row;
    }

    echo json_encode($orders);

    $stmt->close();
} else {
    http_response_code(500);
    echo json_encode(["error" => "Failed to prepare statement."]);
}

$conn->close();
?>
