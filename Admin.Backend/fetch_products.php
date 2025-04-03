<?php
session_start();

// Database connection
$conn = new mysqli("localhost", "root", "", "pcx_db");

// Check connection
if ($conn->connect_error) {
    die("Connection failed: " . $conn->connect_error);
}

// Fetch all products 
if ($stmt = $conn ->prepare("SELECT id, name, description, category, price, stocks, status, image FROM products"));
    $stmt -> execute();
    $result = $stmt->get_result();

    $products = []; // Products

    // Check kung may madidisplay
    while ($row = $result->fetch_assoc()) { // Loops and store it in $products
        $products[] = $row;
    }
    echo json_encode($products); // Puts products info in JSON format to display in products

$stmt->close();
$conn->close();
?>