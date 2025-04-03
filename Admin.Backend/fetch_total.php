<?php
session_start();

// Database connection
$conn = new mysqli("localhost", "root", "", "pcx_db");

// Check connection
if ($conn->connect_error) {
    die("Connection failed: " . $conn->connect_error);
}

// Initialize totals
$totals = [
    'users' => 0,
    'orders' => 0,
    'customers' => 0,
    'products' => 0,
    'total_sales' => 0,
    'total_revenue' => 0,
    'sales_by_month' => []
];

// Query total users
$userQuery = $conn->query("SELECT COUNT(*) AS total FROM users");
$totals['users'] = $userQuery ? $userQuery->fetch_assoc()['total'] : 0;

// Query total orders
$ordersQuery = $conn->query("SELECT COUNT(*) AS total FROM orders");
$totals['orders'] = $ordersQuery ? $ordersQuery->fetch_assoc()['total'] : 0;

// Query total customers
$customerQuery = $conn->query("SELECT COUNT(*) AS total FROM customers");
$totals['customers'] = $customerQuery ? $customerQuery->fetch_assoc()['total'] : 0;

// Query total products
$productQuery = $conn->query("SELECT COUNT(*) AS total FROM products");
$totals['products'] = $productQuery ? $productQuery->fetch_assoc()['total'] : 0;

// Fetch total sales
$sql_sales = "SELECT COUNT(*) AS total_sales FROM orders WHERE LOWER(order_status) = 'completed'";
$result_sales = $conn->query($sql_sales);
$totals['total_sales'] = $result_sales ? $result_sales->fetch_assoc()['total_sales'] : 0;

// Fetch total revenue
$sql_revenue = "SELECT SUM(COALESCE(total, 0)) AS total_revenue FROM orders WHERE LOWER(order_status) = 'completed'";
$result_revenue = $conn->query($sql_revenue);
$totals['total_revenue'] = $result_revenue ? $result_revenue->fetch_assoc()['total_revenue'] : 0;

// Fetch completed sales grouped by month for the current year
$current_year = date('Y');
$sql_monthly_sales = "SELECT DATE_FORMAT(order_date, '%M') AS month, SUM(COALESCE(total, 0)) AS monthly_sales
                      FROM orders
                      WHERE LOWER(order_status) = 'completed'
                      AND YEAR(order_date) = ?
                      GROUP BY MONTH(order_date)
                      ORDER BY MONTH(order_date)";

$stmt_monthly_sales = $conn->prepare($sql_monthly_sales);
$stmt_monthly_sales->bind_param("i", $current_year);
$stmt_monthly_sales->execute();
$result_monthly_sales = $stmt_monthly_sales->get_result();

while ($row = $result_monthly_sales->fetch_assoc()) {
    $totals['sales_by_month'][$row['month']] = (float)$row['monthly_sales'];
}
$stmt_monthly_sales->close();

$conn->close();

// Return totals and sales by month as JSON
echo json_encode($totals);
?>