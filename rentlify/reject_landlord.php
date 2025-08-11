<?php
include 'connection.php';

if (isset($_GET['id'])) {
    $id = $_GET['id'];
    $sql = "DELETE FROM users WHERE id='$id'";
    if ($conn->query($sql) === TRUE) {
        echo "Landlord rejected and removed!";
        header("Location: admin_dashboard.php");
    } else {
        echo "Error deleting record: " . $conn->error;
    }
}
?>
