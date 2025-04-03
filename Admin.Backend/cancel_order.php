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

if ($_SERVER['REQUEST_METHOD'] === 'POST') {

    $id_order = $_POST['id_order'] ?? '';
    $quantity = $_POST['quantity'] ?? '';
    $items = $_POST['items'] ?? '';

    // Log POST data for debugging
    file_put_contents('log.txt', print_r($_POST, true), FILE_APPEND);

    // Validate fields
    if (empty($id_order) || empty($quantity) || empty($items)) {
        echo json_encode(['status' => 'error', 'message' => 'Missing fields']);
        exit;
    }

    // Update order status to 'Cancelled'
    $sql = "UPDATE orders SET order_status = 'Cancelled' WHERE id_order = ?";
    $stmt = $conn->prepare($sql);
    
    if (!$stmt) {
        echo json_encode(['status' => 'error', 'message' => 'Prepare failed', 'error' => $conn->error]);
        $conn->close();
        exit;
    }

    $stmt->bind_param("i", $id_order);

    if (!$stmt->execute()) {
        echo json_encode([
            'status' => 'error',
            'message' => 'Failed to cancel order',
            'error' => $stmt->error
        ]);
        $stmt->close();
        $conn->close();
        exit;
    }
    $stmt->close();

    // Update product stock
    $updateStockSql = "UPDATE products SET stocks = stocks + ? WHERE name = ?";
    $updateStockStmt = $conn->prepare($updateStockSql);

    if (!$updateStockStmt) {
        echo json_encode(['status' => 'error', 'message' => 'Prepare failed', 'error' => $conn->error]);
        $conn->close();
        exit;
    }

    $updateStockStmt->bind_param("ii", $quantity, $items);

    if (!$updateStockStmt->execute()) {
        echo json_encode([
            'status' => 'error',
            'message' => 'Order cancelled but failed to update stock',
            'error' => $updateStockStmt->error
        ]);
        $updateStockStmt->close();
        $conn->close();
        exit;
    }

    // Success
    echo json_encode(['status' => 'success', 'message' => 'Order cancelled and stock updated']);
    $updateStockStmt->close();
    $conn->close();
    exit;

} else {
    echo json_encode(['status' => 'error', 'message' => 'Invalid request method']);
    $conn->close();
    exit;
}
?>
