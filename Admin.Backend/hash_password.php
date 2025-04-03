<?php
//Manually a one-time hashed password para sa admin creation account since ang finefetch ng log in natin ay yung hashed password na nasa database
$password = "admin1234"; 
$hashed_password = password_hash($password, PASSWORD_DEFAULT);
echo "Hashed Password: " . $hashed_password;
?>
