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

// Ensure required POST fields exist
$required_fields = ['user_id', 'product_id', 'product_name', 'product_price', 'product_image_url', 'quantity', 'product_stock'];
foreach ($required_fields as $field) {
    if (!isset($_POST[$field]) || empty($_POST[$field])) {
        echo json_encode(["success" => false, "message" => "Missing required field: $field"]);
        exit;
    }
}

// Retrieve and validate input data
$user_id = filter_var($_POST['user_id'], FILTER_VALIDATE_INT);
$product_id = filter_var($_POST['product_id'], FILTER_VALIDATE_INT);
$product_name = trim($_POST['product_name']);
$product_price = filter_var($_POST['product_price'], FILTER_VALIDATE_FLOAT);
$product_image_url = trim($_POST['product_image_url']);
$quantity = filter_var($_POST['quantity'], FILTER_VALIDATE_INT);
$product_stock = filter_var($_POST['product_stock'], FILTER_VALIDATE_INT);

if (!$user_id || !$product_id || !$quantity || !$product_stock || !$product_price) {
    echo json_encode(["success" => false, "message" => "Invalid input values"]);
    exit;
}

$checkUserQuery = "SELECT user_id FROM customers WHERE user_id = ?";
$stmt = $conn->prepare($checkUserQuery);
$stmt->bind_param("i", $user_id);
$stmt->execute();
$result = $stmt->get_result();

if ($result->num_rows === 0) {
    echo json_encode(["success" => false, "message" => "Invalid user_id: No such user found"]);
    exit;
}

// Check if item already exists in cart
$query = "SELECT quantity FROM cart_items WHERE user_id = ? AND product_id = ?";
$stmt = $conn->prepare($query);
$stmt->bind_param("ii", $user_id, $product_id);
$stmt->execute();
$result = $stmt->get_result();

if ($result->num_rows > 0) {
    // Update quantity if item exists
    $existing = $result->fetch_assoc();
    $new_quantity = $existing['quantity'] + $quantity;

    // Ensure stock is not exceeded
    if ($new_quantity > $product_stock) {
        echo json_encode(["success" => false, "message" => "Exceeds available stock"]);
        exit;
    }

    $update = "UPDATE cart_items SET quantity = ? WHERE user_id = ? AND product_id = ?";
    $stmt = $conn->prepare($update);
    $stmt->bind_param("iii", $new_quantity, $user_id, $product_id);
} else {
    // Insert new item
    $insert = "INSERT INTO cart_items (user_id, product_id, product_name, product_price, product_image_url, quantity, product_stock) 
               VALUES (?, ?, ?, ?, ?, ?, ?)";
    $stmt = $conn->prepare($insert);
    $stmt->bind_param("iisdsii", $user_id, $product_id, $product_name, $product_price, $product_image_url, $quantity, $product_stock);
}

if ($stmt->execute()) {
    echo json_encode(["success" => true, "message" => "Item added to cart"]);
} else {
    echo json_encode(["success" => false, "message" => "Failed to add item"]);
}

$stmt->close();
$conn->close();
?>
