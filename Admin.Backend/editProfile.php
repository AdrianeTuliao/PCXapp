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
    <title>PCX Admin, Edit Profile</title>
    <link rel="stylesheet" href="editProfile.css">
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
            <li><a href="manageUser.php"><i class="fas fa-users"></i> Users</a></li>
            <li><a href="reports.php"><i class="fas fa-chart-bar"></i> Reports</a></li>
            <li class="logout">
                <a href="login.php"><i class="fas fa-sign-out-alt"></i> Logout</a>
            </li>
        </ul>
    </div>

    <div class="content">
    <div class="animateds">
        <h2 class="animated-text1">Profile</h2>
        <div class="profile-form">
            <div class="profile-pic-container">
                <img src="profile.png" alt="Profile Picture" id="profilePic">
                <input type="file" id="profilePicInput" accept="image/*">
                <label for="profilePicInput">Change Profile Picture</label>
            </div>
            <div class="personal-info-container">
                <form id="profileForm">
                    <div class="form-group">
                        <label for="fullName">Full Name</label>
                        <input type="text" id="fullName" placeholder="">
                    </div>
                    <div class="form-group">
                        <label for="email">Email Address</label>
                        <input type="email" id="email" placeholder="">
                    </div>
                    <div class="form-group">
                        <label for="contactNumber">Contact Number</label>
                        <input type="tel" id="contactNumber" placeholder="">
                    </div>
                    <div class="button-group">
                        <button class="btn save-changes-btn" type="button">Save Changes</button>
                        <button class="btn discard-changes-btn" type="button" onclick="discardChanges()">Discard Changes</button>
                    </div>
                </form>
            </div>
        </div>

    <button class="btn toggle-password-btn" onclick="togglePasswordSection()">Change Password</button>
    
    <div class="change-password-container" id="changePasswordContainer" style="display: none;">
    <form id="changePasswordForm" method="POST" action="change_password.php">
    <div class="form-group">
        <label for="currentPassword">Current Password</label>
        <input type="password" id="currentPassword" name="currentPassword" required>
    </div>
    <div class="form-group">
        <label for="newPassword">New Password</label>
        <input type="password" id="newPassword" name="newPassword" required>
    </div>
    <div class="form-group">
        <label for="confirmPassword">Confirm New Password</label>
        <input type="password" id="confirmPassword" name="confirmPassword" required>
    </div>
    <div class="button-group">
        <button class="btn save-changes-btn" type="submit">Save Password</button>
    </div>
</form>
</div>
    </div>
    </div>
    
    <script>
    function discardPasswordChanges() {
        document.getElementById("changePasswordForm").reset();
    }


        function togglePasswordSection() {
        let passwordContainer = document.getElementById("changePasswordContainer");
        passwordContainer.style.display = (passwordContainer.style.display === "none" || passwordContainer.style.display === "") ? "block" : "none";
    }

    function discardPasswordChanges() {
        document.getElementById("changePasswordForm").reset();
    }


        // Fetch user data and populate the form
        document.addEventListener('DOMContentLoaded', function() {
            fetch('fetch_user_data.php')
                .then(response => response.json())
                .then(data => {
                    document.getElementById('fullName').value = data.full_name;
                    document.getElementById('email').value = data.email;
                    document.getElementById('contactNumber').value = data.contact_number;

                    // Update sidebar name
                    document.getElementById('sidebarAdminName').textContent = data.full_name;
                })
                .catch(error => console.error('Error fetching user data:', error));
        });

        // JavaScript for saving changes (you'll need to implement the actual saving logic)
        const saveChangesBtn = document.querySelector('.save-changes-btn');

        saveChangesBtn.addEventListener('click', () => {
            // Get values from form inputs
            const fullName = document.getElementById('fullName').value;
            const email = document.getElementById('email').value;
            const contactNumber = document.getElementById('contactNumber').value;

            // Update sidebar name
            document.getElementById('sidebarAdminName').textContent = fullName;

            // Placeholder: You would send this data to your server for saving
            console.log('Saving changes:', { fullName, email, contactNumber });

            // Optional: Display a success message or update the UI
            alert('Changes saved successfully!');
        });

        // JavaScript for changing profile picture
        const profilePicInput = document.getElementById('profilePicInput');
        const profilePic = document.getElementById('profilePic');
        const sidebarProfilePic = document.getElementById('sidebarProfilePic');

        profilePicInput.addEventListener('change', (event) => {
            const file = event.target.files[0];
            if (file) {
                const reader = new FileReader();
                reader.onload = (e) => {
                    profilePic.src = e.target.result;
                    sidebarProfilePic.src = e.target.result; // Update sidebar profile picture
                };
                reader.readAsDataURL(file);
            }
        });

        // JavaScript for discarding changes
        function discardChanges() {
            document.getElementById('profileForm').reset();
            profilePic.src = 'profile.jpg'; // Reset profile picture to default
            sidebarProfilePic.src = 'profile.jpg'; // Reset sidebar profile picture to default
        }

        // For change password
        document.getElementById("changePasswordForm").addEventListener("submit", function(event) {
    event.preventDefault(); // Prevent form from reloading page

    let formData = new FormData(this);

    fetch("change_password.php", {
        method: "POST",
        body: formData
    })
    .then(response => response.json())
    .then(data => {
        alert(data.message); // Show success/error message
        if (data.status === "success") {
            document.getElementById("changePasswordForm").reset(); // Reset form
        }
    })
    .catch(error => console.error("Error:", error));
});

        
    </script>

</body>
</html>