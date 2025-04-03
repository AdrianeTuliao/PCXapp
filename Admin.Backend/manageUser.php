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
                <h2><?php echo htmlspecialchars ($_SESSION['full_name'])?></h2>
                <a href="editProfile.php" class="edit-profile-btn">Edit Profile</a>
            </div>
        </div>

        <ul class="menu">
            <li><a href="homepage.php"><i class="fas fa-home"></i> Dashboard</a></li>
            <li><a href="manageProducts.php"><i class="fas fa-box"></i> Products</a></li>
            <li><a href="manageOrder.php"><i class="fas fa-shopping-cart"></i> Orders</a></li>
            <li><a href="customer.php"><i class="fas fa-users"></i> Customers</a></li>
            <li class="active"><i class="fa-solid fa-user"></i> Users</li>
            <li><a href="reports.php"><i class="fas fa-chart-bar"></i> Reports</a></li>
            
            <li class="logout">
                <a href="login.php"><i class="fas fa-sign-out-alt"></i> Logout</a>
            </li>
        </ul>
    </div>

    <div class="content">
    <div class="animateds">
    <h2 class="animated-text1">Users</h2>
    <div class="user-stats">
      <div class="stat">
        <span>Total Users</span>
        <span class="stat-value" id="totalUsers">0</span>
      </div>
    </div>
    <table class="user-table">
      <thead>
        <tr>
          <th>Username</th>
          <th>Fullname</th>
          <th>Email</th>
          <th>Status</th>
        </tr>
      </thead>
      <tbody id="userTableBody">
        <!-- User rows will be inserted here by JavaScript -->
      </tbody>
    </table>
  </div>
  </div>

  <script>
    const userData = [

    ];
    
    fetch('fetch_user.php') 
  .then(response => response.json())
  .then(data => {
    const userTableBody = document.getElementById("userTableBody");
    const totalUsersElement = document.getElementById("totalUsers");

    userTableBody.innerHTML = ""; 
    let totalUsers = 0;

    data.forEach(user => {
      const row = userTableBody.insertRow();
      row.innerHTML = `
        <td>${user.username}</td>
        <td>${user.full_name}</td>
        <td>${user.email}</td>
        <td><span class="user-status ${user.status.toLowerCase()}">${user.status}</span></td>
      `;
      totalUsers++;
    });

    totalUsersElement.textContent = totalUsers;
  })
  .catch(error => console.error('Error fetching users:', error));

    const sortableColumns = document.querySelectorAll("th i");
    sortableColumns.forEach(icon => {
      icon.addEventListener("click", () => {
        console.log("Sorting column clicked!");
      });
    });
  </script>
</body>
</html>


