<?php
session_start();

// Database connection
$conn = new mysqli("localhost", "root", "", "pcx_db");

//Checn connection
if ($conn->connect_error) {
    die("Connection failed: " . $conn->connect_error);
}

// Login
if ($_SERVER["REQUEST_METHOD"] == "POST") {
    $username = $_POST['username']; 
    $password = $_POST['password'];

    // Prepare statement to fetch user by username only
    if ($stmt = $conn->prepare("SELECT id, username, full_name, password FROM users WHERE username = ?")) {
        $stmt->bind_param("s", $username); // To prevent sql injection
        $stmt->execute();
        $stmt->bind_result($id, $username, $full_name, $hashed_password);
        
        if ($stmt->fetch()) { // Check if user exists
            if (password_verify($password, $hashed_password)) { 

                // Store user data in session
                $_SESSION['user_id'] = $id;
                $_SESSION['username'] = $username;
                $_SESSION['full_name'] = $full_name; // dinagdag ko para ma fetch niya yung full name para madispay sa dashboard
                header("Location: homepage.php");
                exit();
            } else {
                echo "<script>alert('Incorrect password. Please try again.'); window.location.href='login.php';</script>";
            }
        } else {
            echo "<script>alert('Username not found.'); window.location.href='login.php';</script>";
        }
        $stmt->close();
    }
    $conn->close();
}
?>


<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link rel="icon" type="image/png" href="pcxlogo.png">
    <title>Admin Login</title>
    <link rel="stylesheet" href="style.css">
    <link href="https://fonts.googleapis.com/css2?family=Roboto:wght@500;700&display=swap" rel="stylesheet">
    <link href='https://unpkg.com/boxicons@2.1.4/css/boxicons.min.css' rel='stylesheet'>
</head>
<body>

    <div class="container popup">
        <div class="form-box login">
            <form action="login.php" method="post">
                <h1>Login</h1>
                <div class="input-box">
                    <input type="text" name="username" placeholder="Username" required>
                    <i class='bx bxs-user'></i>
                </div>
                <div class="input-box">
                    <input type="password" id="login-password" name="password" placeholder="Password" required>
                    <i class='bx bxs-lock-alt' onclick="togglePassword('login-password', this)"></i>
                </div>
            
                <button type="submit" class="btn">Login</button>
            </form>
        </div>

        <div class="toggle-box">
            <div class="toggle-panel toggle-left">
                <img class="loogo" src="pcxlogo.png">
            </div>
        </div>
    </div>

    <script>
        document.addEventListener("DOMContentLoaded", function () {
            document.querySelector(".container").classList.add("show");
        });

        function togglePassword(id, el) {
            const input = document.getElementById(id);
            if (input.type === "password") {
                input.type = "text";
                el.classList.remove('bxs-lock-alt');
                el.classList.add('bxs-lock-open-alt');
            } else {
                input.type = "password";
                el.classList.remove('bxs-lock-open-alt');
                el.classList.add('bxs-lock-alt');
            }
        }
    </script>

</body>
</html>
