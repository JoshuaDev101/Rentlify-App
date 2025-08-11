<?php
require_once 'connection.php';

// Set headers to prevent caching and allow all origins
header('Access-Control-Allow-Origin: *');
header('Content-Type: application/json');
header('Cache-Control: no-cache, no-store, must-revalidate');
header('Pragma: no-cache');
header('Expires: 0');

// Get notification ID from request
$notification_id = isset($_POST['notification_id']) ? $_POST['notification_id'] : '';

if (empty($notification_id)) {
    echo json_encode(array('success' => false, 'message' => 'Notification ID required'));
    exit;
}

// Update notification status to read
$query = "UPDATE notifications SET read_status = 1, status = 'read' WHERE id = ?";
$stmt = $conn->prepare($query);
$stmt->bind_param('i', $notification_id);
$success = $stmt->execute();

if ($success) {
    echo json_encode(array('success' => true, 'message' => 'Notification updated successfully'));
} else {
    echo json_encode(array('success' => false, 'message' => 'Failed to update notification: ' . $conn->error));
}

$stmt->close();
$conn->close();
?>