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
    <link rel="stylesheet" href="manageUser.css">
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
        <li class="active"><i class="fas fa-users"></i> Customers</li>
        <li><a href="manageUser.php"><i class="fa-solid fa-user"></i> Users</a></li>
        <li><a href="reports.php"><i class="fas fa-chart-bar"></i> Reports</a></li>
        <li class="logout">
            <a href="login.php"><i class="fas fa-sign-out-alt"></i> Logout</a>
        </li>
    </ul>
</div>

<div class="content">
    <div class="animateds">
        <h2 class="animated-text1">Customers</h2>

        <!-- Stats Section -->
        <div class="user-stats">
            <div class="stat">
                <span>Total Customers</span>
                <span class="stat-value" id="totalCustomers">0</span>
            </div>
        </div>

        <!-- Table Section -->
        <table class="user-table">
            <thead>
                <tr>
                    <th>Username</th>
                    <th>Email</th>
                    <th>Contact Number</th>
                    <th>Status</th>
                </tr>
            </thead>
            <tbody id="customerTableBody">
                <!-- Customer rows will be inserted here by JavaScript -->
            </tbody>
        </table>
    </div>
</div>

<script>
    fetch('fetch_customers.php')
        .then(response => response.json())
        .then(data => {
            const customerTableBody = document.getElementById("customerTableBody");
            const totalCustomersElement = document.getElementById("totalCustomers");

            customerTableBody.innerHTML = "";
            let totalCustomers = 0;

            data.forEach(customer => {
                const row = customerTableBody.insertRow();
                row.innerHTML = `
                    <td>${customer.username}</td>
                    <td>${customer.email}</td>
                    <td>${customer.contact_number}</td>
                    <td><span class="user-status ${customer.status.toLowerCase()}">${customer.status}</span></td>
                `;
                totalCustomers++;
            });

            totalCustomersElement.textContent = totalCustomers;
        })
        .catch(error => console.error('Error fetching customers:', error));
</script>

</body>
</html>