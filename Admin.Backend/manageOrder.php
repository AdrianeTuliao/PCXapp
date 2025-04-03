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
<meta name="viewport" content="width=device-width, initial-scale=1">
<link rel="icon" type="image/png" href="pcxlogo.png">
<title>PCX Admin</title>
<link rel="stylesheet" href="manageOrder.css">
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
            <a href="manageOrder.php"><li class="active"><i class="fas fa-shopping-cart"></i> Orders</li>
            <li><a href="customer.php"><i class="fas fa-users"></i> Customers</a></li>
            <li><a href="manageUser.php"><i class="fa-solid fa-user"></i> Users</a></li>
            <li><a href="reports.php"><i class="fas fa-chart-bar"></i> Reports</a></li>
            <li class="logout">
                <a href="login.php"><i class="fas fa-sign-out-alt"></i> Logout</a>
            </li>  
        </ul>
    </div>

    <div class="content">
        <div class="animateds">
            <h2 class="animated-text1">Orders</h2>
            <div class="order-stats">
                <div class="stat">
                    <span>Total Orders</span>
                    <span class="stat-value" id="totalOrders">0</span>
                </div>
            </div>

            <!-- Tabs Navigation -->
            <div class="order-tabs">
                <button class="tab-btn" data-status="Pending Orders" onclick="showOrders('Pending Orders', this)">Pending Orders</button>
                <button class="tab-btn" data-status="Preparing Stage" onclick="showOrders('Preparing Stage', this)">Preparing Stage</button>
                <button class="tab-btn" data-status="Ready for Pick Up" onclick="showOrders('Ready for Pick Up', this)">Ready for Pick Up</button>
                <button class="tab-btn" data-status="Completed" onclick="showOrders('Completed', this)">Completed</button>
                <button class="tab-btn" data-status="Cancelled" onclick="showOrders('Cancelled', this)">Cancelled</button>
            </div>

            <!-- Orders Table -->
            <table class="order-table">
                <thead>
                    <tr>
                        <th>ID Order</th>
                        <th>Image</th>
                        <th>Date</th>
                        <th>Customer</th>
                        <th>Total</th>
                        <th>Payment Status</th>
                        <th>Items</th>
                        <th>Quantity</th>
                        <th>Actions</th>
                    </tr>
                </thead>
                <tbody id="orderTableBody">
                    <!-- Orders will load here dynamically -->
                </tbody>
            </table>
        </div>
    </div>

<script>
let currentStatus = 'Pending Orders';

// DOMContentLoaded
document.addEventListener("DOMContentLoaded", function () {
    setupTabs();

    // Find the first tab button and click it automatically
    const defaultButton = document.querySelector('.tab-btn[data-status="Pending Orders"]');
    if (defaultButton) {
        defaultButton.click();
    }
});

// Setup tabs + click listeners
function setupTabs() {
    const tabButtons = document.querySelectorAll(".tab-btn");
    tabButtons.forEach(button => {
        button.addEventListener("click", () => {
            tabButtons.forEach(btn => btn.classList.remove("active"));
            button.classList.add("active");

            const status = button.getAttribute('data-status');
            currentStatus = status;
            showOrders(status, button);
        });
    });
}

// Fetch orders + display filtered by currentStatus
function showOrders(status, btn) {
    // Safety check: btn should not be undefined
    if (!btn) {
        console.error('No button element received!');
        return;
    }

    // Remove 'active' class from all tab buttons
    document.querySelectorAll('.tab-btn').forEach(button => {
        button.classList.remove('active');
    });

    btn.classList.add('active');

    console.log('Selected status:', status);

    // Fetch orders filtered by status
    fetch('fetch_orders.php?order_status=' + encodeURIComponent(status))
        .then(response => response.json())
        .then(orderData => {
            console.log("Fetched data:", orderData);

            const orderTableBody = document.getElementById("orderTableBody");
            const totalOrdersElement = document.getElementById("totalOrders");

            // Clear the table content before appending new data
            orderTableBody.innerHTML = "";

            let totalOrders = 0;
            orderData.forEach(order => {
                let imageUrl = order.image ? order.image : 'default.png'; // Ensure a default image if none exists

                const row = orderTableBody.insertRow();
                row.setAttribute('data-order-id', order.id_order);

                let actions = "";

                if (status === "Pending Orders") {
                    actions += `
                        <button class="accept-btn" onclick="updateOrderStatus(${order.id_order}, 'Preparing Stage')">Accept</button>
                        <button class="reject-btn" onclick="updateOrderStatus(${order.id_order}, 'Cancelled')">Reject</button>
                    `;
                } else if (status === "Preparing Stage") {
                    actions += `
                        <button class="next-stage-btn" onclick="updateOrderStatus(${order.id_order}, 'Ready for Pick Up')">Ready for Pick Up</button>
                    `;
                } else if (status === "Ready for Pick Up") {
                    actions += `
                        <button class="next-stage-btn" onclick="updateOrderStatus(${order.id_order}, 'Completed')">Complete</button>
                    `;
                }

                row.innerHTML = `
                    <td>${order.id_order}</td>
                    <td><img src="${imageUrl}" alt="Product Image" class="product-image"></td>
                    <td>${order.order_date}</td>
                    <td>${order.customer_name}</td>
                    <td>₱${parseFloat(order.total).toFixed(2)}</td>
                    <td><span class="paid-status ${order.payment_status === 'Pending' ? 'unpaid' : 'paid'}">${order.payment_status}</span></td>
                    <td>${order.items}</td>
                    <td>${order.quantity}</td>
                    <td>${actions}</td>
                `;

                totalOrders++;
            });

            // Update the total orders count
            totalOrdersElement.textContent = totalOrders;
        })
        .catch(error => {
            console.error('Error fetching orders:', error);
            alert('An error occurred while loading orders.');
        });
}

// Update order status without opening modal
function updateOrderStatus(orderId, newStatus) {
    let newPaymentStatus = "Pending";

    if (newStatus === "Completed") {
        newPaymentStatus = "Paid";
    }

    const formData = new URLSearchParams();
    formData.append('id_order', orderId);
    formData.append('payment_status', newPaymentStatus);
    formData.append('order_status', newStatus);

    fetch("update_order.php", {
        method: "POST",
        headers: {
            "Content-Type": "application/x-www-form-urlencoded"
        },
        body: formData.toString()
    })
    .then(response => response.json())
    .then(data => {
        if (data.success) {
            alert(`Order updated to "${newStatus}"`);

            // REMOVE THE ROW DIRECTLY!
            const rowToRemove = document.querySelector(`tr[data-order-id="${orderId}"]`);
            if (rowToRemove) {
                rowToRemove.remove();
            }

            // Update total orders count
            const totalOrdersElement = document.getElementById("totalOrders");
            const newTotal = parseInt(totalOrdersElement.textContent) - 1;
            totalOrdersElement.textContent = newTotal >= 0 ? newTotal : 0;

        } else {
            alert("Failed to update order: " + data.message);
        }
    })
    .catch(error => {
        console.error(error);
        alert("An error occurred while updating the order.");
    });
}
</script>
</body>
</html>
