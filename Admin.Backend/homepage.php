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
    <link rel="stylesheet" href="homepage.css">
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
            <a href="homepage.php"></a><li class="active"><i class="fas fa-home"></i> Dashboard</li>
            <li><a href="manageProducts.php"><i class="fas fa-box"></i> Products</a></li>
            <li><a href="manageOrder.php"><i class="fas fa-shopping-cart"></i> Orders</a></li>
            <li><a href="customer.php"><i class="fas fa-users"></i> Customers</a></li>
            <li><a href="manageUser.php"><i class="fa-solid fa-user"></i> Users</a></li>
            <li><a href="reports.php"><i class="fas fa-chart-bar"></i> Reports</a></li>
            <li class="logout">
                <a href="login.php"><i class="fas fa-sign-out-alt"></i> Logout</a>
            </li>
        </ul>
    </div>

    <div class="content">
        <h2 class="animated-text1">Dashboard</h2>
        <h1 class="animated-text">Welcome, Admin</h1>
        <p class="animated-text2">Manage your PCX store efficiently.</p>
        <br><br>

        <div class="animateds">
            

        <div class="chart-container" style="width: 100%; max-width: 800px; height: 400px; margin: auto;">
    <canvas id="myBarChart"></canvas>
</div>
<br><br>
        <div class="card">
            <h3>Recent Orders: <span id="totalOrders">0</span></h3>
        </div>
        <div class="table-container">
            <table id="productTable">
                <thead>
                    <tr>
                        <th>ID</th>
                        <th>Product</th>
                        <th>Price</th>
                        <th>Date</th>
                    </tr>
                </thead>
                <tbody></tbody>
            </table>
        </div>
        <br><br>
        <div class="chart-container" style="width: 100%; max-width: 800px; height: 400px; margin: auto;">
    <canvas id="myBarChart"></canvas>
</div>
    </div>
    </div>
    <script>
document.addEventListener("DOMContentLoaded", function () {
    fetchOrders();
});

function fetchOrders() {
    fetch('fetch_orders1.php')
        .then(response => response.json())
        .then(data => {
            if (data.error) {
                console.error(data.error);
                return;
            }

            const productTableBody = document.querySelector('#productTable tbody');
            productTableBody.innerHTML = ""; // Clear existing table data

            // 5 recent orders
            data.recent_orders.forEach(order => {
                const row = document.createElement('tr');
                row.innerHTML = `
                    <td>${order.id_order}</td>
                    <td>${order.items}</td>
                    <td>${order.total}</td>
                    <td>${order.order_date}</td>
                `;
                productTableBody.appendChild(row);
            });

            document.getElementById('totalOrders').textContent = data.recent_orders.length;

            updateChart(data.all_orders);
        })
        .catch(error => console.error('Error fetching orders:', error));
}

function updateChart(orders) {
    const salesByDate = {};

    orders.forEach(order => {
        const date = order.order_date.split(' ')[0]; 
        salesByDate[date] = (salesByDate[date] || 0) + parseFloat(order.total); 
    });

    const dateLabels = Object.keys(salesByDate).sort(); 
    const salesData = Object.values(salesByDate);

    if (window.myBarChart instanceof Chart) {
        window.myBarChart.destroy();
    }

    const ctx = document.getElementById('myBarChart').getContext('2d');
    window.myBarChart = new Chart(ctx, {
        type: 'bar',
        data: {
            labels: dateLabels,
            datasets: [{
                label: 'Total Sales (PHP)',
                data: salesData,
                backgroundColor: 'rgba(54, 162, 235, 0.5)',
                borderColor: 'rgba(54, 162, 235, 1)',
                borderWidth: 1
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            scales: {
                y: {
                    beginAtZero: true,
                    title: {
                        display: true,
                        text: 'Sales Amount (PHP)'
                    }
                },
                x: {
                    title: {
                        display: true,
                        text: 'Date'
                    }
                }
            }
        }
    });
}

</script>
</body>
</html> 