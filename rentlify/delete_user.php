<?php
require 'connection.php';

if (isset($_GET['id'])) {
    $id = $_GET['id'];
    $sql = "DELETE FROM tableuser WHERE id = $id";

    if (mysqli_query($conn, $sql)) {
        header("Location: admin_dashboard.php");
    } else {
        echo "Error deleting user: " . mysqli_error($conn);
    }
}
?>
