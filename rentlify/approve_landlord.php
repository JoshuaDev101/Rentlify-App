<?php
include 'connection.php';

if (isset($_GET['id'])) {
    $id = $_GET['id'];
    $sql = "UPDATE users SET status='approved' WHERE id='$id'";
    if ($conn->query($sql) === TRUE) {
        echo "Landlord approved successfully!";
        header("Location: admin_dashboard.php");
    } else {
        echo "Error updating record: " . $conn->error;
    }
}
?>
