<?php
session_start();

if (!isset($_SESSION['username'])) {
    header("Location: login.php");
    exit();
}
?>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link rel="icon" type="image/png" href="pcxlogo.png">
    <title>PCX Admin</title>
    <link rel="stylesheet" href="reports.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.2/css/all.min.css">
    <script src="https://cdn.jsdelivr.net/npm/chart.js"></script>
    <script src="js/pageloader.js"></script>
</head>

<body>
    <div class="sidebar">
        <div class="logo">
            <img src="pcxlogo.png" alt="PCX Logo">
        </div>
        <div class="profile">
            <img src="profile.png" alt="Profile Picture" class="profile-pic">
            <div class="profile-info">
                <h2><?php echo htmlspecialchars($_SESSION['full_name']); ?></h2>
                <a href="editProfile.php" class="edit-profile-btn">Edit Profile</a>
            </div>
        </div>

        <ul class="menu">
            <li><a href="homepage.php"><i class="fas fa-home"></i> Dashboard</a></li>
            <li><a href="manageProducts.php"><i class="fas fa-box"></i> Products</a></li>
            <li><a href="manageOrder.php"><i class="fas fa-shopping-cart"></i> Orders</a></li>
            <li><a href="customer.php"><i class="fas fa-users"></i> Customers</a></li>
            <li><a href="manageUser.php"><i class="fa-solid fa-user"></i> Users</a></li>
            <a href="reports.php"></a> 
            <li class="active"><i class="fas fa-chart-bar"></i> Reports</li>

            <li class="logout">
                <a href="login.php"><i class="fas fa-sign-out-alt"></i> Logout</a>
            </li>
        </ul>
    </div>

    <div class="content">
        <h2 class="animated-text1">Reports</h2>


        <div class="charts">
            <div class="combinedChart-container">
                <canvas id="combinedChart"></canvas>
            </div>
        </div>

        <div class="charts">
            <div class="large-chart-container">
                <canvas id="salesChart"></canvas>
            </div>
            <div class="large-chart-container">
                <canvas id="revenueChart"></canvas>
            </div>
        </div>
    </div>

    <script>
        // Fetch totals from the backend
        fetch('fetch_total.php')
    .then(response => response.json())
    .then(data => {
        if (data.error) {
            alert(data.error);
            return;
        }

        const totalUsers = data.users;
        const totalCustomers = data.customers;
        const totalProducts = data.products;
        const totalOrders = data.orders;
        const totalSales = data.sales_by_month;  
        const totalRevenue = data.total_revenue;  

        // Chart options
        const chartOptions = {
            responsive: true,
            maintainAspectRatio: false,
            scales: {
                y: {
                    beginAtZero: true
                }
            }
        };

        // Initialize combined chart
        const combinedCtx = document.getElementById('combinedChart').getContext('2d');
        const combinedChart = new Chart(combinedCtx, {
            type: 'bar',
            data: {
                labels: ['Users', 'Orders', 'Products', 'Customers'],
                datasets: [{
                    label: 'Total Count',
                    data: [totalUsers, totalOrders, totalProducts, totalCustomers],
                    backgroundColor: [
                        'rgba(75, 192, 192, 0.2)',
                        'rgba(54, 162, 235, 0.2)',
                        'rgba(255, 206, 86, 0.2)',
                        'rgba(153, 102, 255, 0.2)'
                    ],
                    borderColor: [
                        'rgba(75, 192, 192, 1)',
                        'rgba(54, 162, 235, 1)',
                        'rgba(255, 206, 86, 1)',
                        'rgba(153, 102, 255, 1)'
                    ],
                    borderWidth: 1
                }]
            },
            options: chartOptions
        });

        const salesLabels = Object.keys(totalSales);  // Get the months from the backend sales data
        const salesData = salesLabels.map(month => totalSales[month]);

        // Sales chart
        const salesCtx = document.getElementById('salesChart').getContext('2d');
        const salesChart = new Chart(salesCtx, {
            type: 'line',
            data: {
                labels: salesLabels, 
                datasets: [{
                    label: 'Total Sales',
                    data: salesData, 
                    backgroundColor: 'rgba(255, 99, 132, 0.2)',
                    borderColor: 'rgba(255, 99, 132, 1)',
                    borderWidth: 3,
                    fill: true,
                    tension: 0.4
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                scales: {
                    y: {
                        beginAtZero: true
                    }
                }
            }
        });


    }).catch(error => console.error('Error fetching totals:', error));


    </script>
</body>
</html>
