<?php
require 'connection.php';

if (isset($_GET['id']) && isset($_GET['action'])) {
    $id = intval($_GET['id']); // Prevent SQL Injection
    $action = $_GET['action'];

    if ($action == "approve") {
        $status = "Verified";
    } elseif ($action == "reject") {
        $status = "Rejected";
    } else {
        die("Invalid action.");
    }

    // Use prepared statements for security
    $stmt = $conn->prepare("UPDATE tableuser SET status = ? WHERE id = ?");
    $stmt->bind_param("si", $status, $id);

    if ($stmt->execute()) {
        echo "<script>
                alert('User status updated to $status');
                window.location.href='admin_dashboard.php';
              </script>";
    } else {
        echo "Error updating user: " . $conn->error;
    }

    $stmt->close();
    $conn->close();
} else {
    echo "Invalid request.";
}
?>
