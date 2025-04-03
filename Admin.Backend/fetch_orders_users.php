<?php
session_start();

// Database connection
$conn = new mysqli("localhost", "root", "", "pcx_db");

// Check connection
if ($conn->connect_error) {
    die("Connection failed: " . $conn->connect_error);
}

// Define base URL for images (modify as needed)
$base_url = "http://192.168.18.127/PCXadmin/uploads/"; // Replace with the actual URL to your images

// Fetch all orders
$sql = "SELECT id_order AS id, total, payment_status, items, order_status, quantity, image FROM orders";

// Check if query is successful
if ($stmt = $conn->prepare($sql)) {
    $stmt->execute();
    $result = $stmt->get_result();

    $orders = [];

    while ($row = $result->fetch_assoc()) {
        // Set image URL (if empty, set a default image or null)
        $imageUrl = !empty($row['image']) 
    ? (strpos($row['image'], "http") === 0 ? $row['image'] : $base_url . $row['image']) 
    : "default_image_url_here.jpg";


        $orders[] = array(
            'id' => $row['id'],
            'total' => $row['total'],
            'payment_status' => $row['payment_status'],
            'items' => $row['items'],
            'order_status' => $row['order_status'],
            'quantity' => $row['quantity'],
            'imageUrl' => $imageUrl
        );
    }

    // Set the Content-Type to JSON
    header('Content-Type: application/json');

    // Output the orders in JSON format
    echo json_encode($orders);

    $stmt->close();
}

$conn->close();
?>
