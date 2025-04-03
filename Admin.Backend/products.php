<?php
header("Access-Control-Allow-Origin: *");
header("Content-Type: application/json; charset=UTF-8");
header("Access-Control-Allow-Methods: POST, GET");
header("Access-Control-Allow-Headers: Content-Type");

session_start();

// Database connection
$conn = new mysqli("localhost", "root", "", "pcx_db");

// Check connection
if ($conn->connect_error) {
    die(json_encode(["error" => "Database connection failed: " . $conn->connect_error]));
}


$method = $_SERVER['REQUEST_METHOD'];

if ($method === 'POST') {
    $action = isset($_POST['action']) ? $_POST['action'] : '';

    if ($action === 'add') {
        // Add Product
        $name = $_POST['name'];
        $description = $_POST['description'];
        $price = $_POST['price'];
        $stock = $_POST['stock'];
        $image_url = $_POST['image_url'];

        $sql = "INSERT INTO products (name, description, price, stock, image_url) VALUES (?, ?, ?, ?, ?)";
        $stmt = $conn->prepare($sql);
        $stmt->bind_param("ssdis", $name, $description, $price, $stock, $image_url);

        if ($stmt->execute()) {
            echo json_encode(["success" => true, "message" => "Product added successfully!"]);
        } else {
            echo json_encode(["success" => false, "message" => "Failed to add product"]);
        }

    } elseif ($action === 'delete') {
        // Delete Product
        $id = $_POST['id'];

        $sql = "DELETE FROM products WHERE id = ?";
        $stmt = $conn->prepare($sql);
        $stmt->bind_param("i", $id);

        if ($stmt->execute()) {
            echo json_encode(["success" => true, "message" => "Product deleted successfully!"]);
        } else {
            echo json_encode(["success" => false, "message" => "Failed to delete product"]);
        }
    }

} elseif ($method === 'GET') {
    // Fetch All Products
    $sql = "SELECT * FROM products ORDER BY created_at DESC";
    $result = $conn->query($sql);

    $products = array();
    while ($row = $result->fetch_assoc()) {
        $products[] = $row;
    }

    echo json_encode($products);
} else {
    echo json_encode(["success" => false, "message" => "Invalid request method"]);
}
?>
