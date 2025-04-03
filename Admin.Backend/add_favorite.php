<?php
session_start();
ini_set('display_errors', 1);
ini_set('log_errors', 1);
error_reporting(E_ALL);

header('Content-Type: application/json');

// Database connection
$conn = new mysqli("localhost", "root", "", "pcx_db");

// Check connection
if ($conn->connect_error) {
    echo json_encode(['status' => 'error', 'message' => 'Connection failed', 'error' => $conn->connect_error]);
    exit;
}

// Get JSON POST body
$raw = file_get_contents('php://input');
$input = json_decode($raw, true);

// Validate input
$required_fields = ['user_id', 'product_id', 'product_name', 'imageUrl', 'price'];
foreach ($required_fields as $field) {
    if (!isset($input[$field]) || $input[$field] === '' || $input[$field] === null) {
        echo json_encode(["success" => false, "message" => "Missing or empty field: $field"]);
        exit;
    }
}

// Extract input
$user_id = intval($input['user_id']);
$product_id = intval($input['product_id']);
$product_name = $conn->real_escape_string($input['product_name']);
$product_image = $conn->real_escape_string($input['imageUrl']);
$price = floatval($input['price']);

// Check if the favorite already exists
$check_sql = "SELECT * FROM favorites WHERE user_id = ? AND product_id = ?";
$check_stmt = $conn->prepare($check_sql);
$check_stmt->bind_param("ii", $user_id, $product_id);
$check_stmt->execute();
$result = $check_stmt->get_result();

if ($result->num_rows > 0) {
    echo json_encode(["success" => false, "message" => "Product is already in favorites"]);
    exit;
}
$check_stmt->close();

// Fetch the stock of the product
$stock_sql = "SELECT stocks FROM products WHERE id = ?";
$stock_stmt = $conn->prepare($stock_sql);
$stock_stmt->bind_param("i", $product_id);
$stock_stmt->execute();
$stock_result = $stock_stmt->get_result();
$stock_row = $stock_result->fetch_assoc();
$stocks = $stock_row ? intval($stock_row['stocks']) : 0; 
$stock_stmt->close();

// Insert the new favorite with stocks
$sql = "INSERT INTO favorites (user_id, product_id, product_name, product_image, price, stocks) 
        VALUES (?, ?, ?, ?, ?, ?)";
$stmt = $conn->prepare($sql);
$stmt->bind_param("iissdi", $user_id, $product_id, $product_name, $product_image, $price, $stocks);

if ($stmt->execute()) {
    echo json_encode(["success" => true, "message" => "Favorite added successfully!", "inserted_product_id" => $product_id]);
} else {
    echo json_encode(["success" => false, "message" => "Execute failed: " . $stmt->error]);
}

$stmt->close();
$conn->close();
?>