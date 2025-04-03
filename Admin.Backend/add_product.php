<?php
session_start();

// Database connection
$conn = new mysqli("localhost", "root", "", "pcx_db");

// Check connection
if ($conn->connect_error) {
    die("Connection failed: " . $conn->connect_error);
}

if ($_SERVER['REQUEST_METHOD'] == 'POST') {
    // Get input from form
    $productName = $_POST['productName'];
    $productDescription = $_POST['productDescription'];
    $productPrice = $_POST['productPrice'];
    $productCategory = $_POST['productCategory'];
    $productStocks = $_POST['productStocks'];
    $productImage = $_FILES['productImage']['name'];

    // Upload the image
    $target_dir = "uploads/";
    $target_file = $target_dir . basename($_FILES["productImage"]["name"]);
    move_uploaded_file($_FILES["productImage"]["tmp_name"], $target_file);

    // Insert product into database
    $stmt = $conn->prepare("INSERT INTO products (name, description, category, price, stocks, image) VALUES (?, ?, ?, ?, ?, ?)");
    $stmt->bind_param("sssdis", $productName, $productDescription, $productCategory, $productPrice, $productStocks, $productImage);

    if ($stmt->execute()) {
        echo json_encode(['success' => true]);
    } else {
        echo json_encode(['success' => false, 'error' => 'Failed to add product: ' . $stmt->error]);
    }

    $stmt->close();
}

$conn->close();
?>