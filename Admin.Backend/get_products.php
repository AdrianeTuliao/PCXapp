<?php
header('Content-Type: application/json');
session_start();

// Database connection
$conn = new mysqli("localhost", "root", "", "pcx_db");

// Check connection
if ($conn->connect_error) {
    die("Connection failed: " . $conn->connect_error);
}

$category = isset($_GET['category']) ? $_GET['category'] : '';

$sql = "SELECT * FROM products";
if (!empty($category)) {
    $sql .= " WHERE category = '" . $conn->real_escape_string($category) . "'";
}

$result = $conn->query($sql);

$products = [];
$imageBaseUrl = "http://192.168.18.127/PCXadmin/uploads/";

while ($row = $result->fetch_assoc()) {
    // Assuming your image column is called 'image'
    $row['imageUrl'] = $imageBaseUrl . $row['image'];

    $products[] = $row;
}

echo json_encode($products);

$conn->close();
?>
