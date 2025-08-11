<?php
require_once 'connection.php';

// Set headers to prevent caching and allow all origins
header('Access-Control-Allow-Origin: *');
header('Content-Type: application/json');
header('Cache-Control: no-cache, no-store, must-revalidate');
header('Pragma: no-cache');
header('Expires: 0');

// Get tenant ID from request
$tenant_id = isset($_POST['tenant_id']) ? $_POST['tenant_id'] : '';

if (empty($tenant_id)) {
    echo json_encode(array('success' => false, 'message' => 'Tenant ID required'));
    exit;
}

// Query to get notifications for the tenant
$query = "SELECT * FROM notifications 
          WHERE notification_for = 'tenant' 
          AND user_id = ? 
          ORDER BY created_at DESC";

$stmt = $conn->prepare($query);
$stmt->bind_param('i', $tenant_id);
$stmt->execute();
$result = $stmt->get_result();

$notifications = array();

if ($result->num_rows > 0) {
    while ($row = $result->fetch_assoc()) {
        $notifications[] = $row;
    }
    echo json_encode(array('success' => true, 'notifications' => $notifications));
} else {
    echo json_encode(array('success' => true, 'notifications' => array(), 'message' => 'No notifications found'));
}

$stmt->close();
$conn->close();
?>