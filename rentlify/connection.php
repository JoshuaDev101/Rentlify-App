<?php
$conn = mysqli_connect("127.0.0.1:3308  ", "root", "", "4610576_user");

if (!$conn) {
    die(json_encode(["success" => "0", "message" => "Database connection failed: " . mysqli_connect_error()]));
}
    ?>
