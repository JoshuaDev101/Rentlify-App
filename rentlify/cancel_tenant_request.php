<?php
// Enable error reporting for debugging
error_reporting(E_ALL);
ini_set('display_errors', 1);

header('Content-Type: application/json');

// Log incoming request for debugging
file_put_contents('cancel_request_log.txt', 
    date('Y-m-d H:i:s') . " - Cancel Request: " . 
    print_r($_POST, true) . "\n", 
    FILE_APPEND);

include 'connection.php';

if (!isset($_POST['request_id']) || !isset($_POST['request_type']) || !isset($_POST['tenant_id'])) {
    echo json_encode(["success" => false, "message" => "Missing required parameters"]);
    exit;
}

$request_id = $_POST['request_id'];
$request_type = $_POST['request_type'];
$tenant_id = $_POST['tenant_id'];

try {
    // First verify this request belongs to this tenant
    if ($request_type == 'rental') {
        $check_sql = "SELECT id FROM rental_applications WHERE id = ? AND tenant_id = ?";
    } else {
        $check_sql = "SELECT id FROM visit_requests WHERE id = ? AND tenant_id = ?";
    }
    
    $check_stmt = $conn->prepare($check_sql);
    $check_stmt->bind_param("ss", $request_id, $tenant_id);
    $check_stmt->execute();
    $check_result = $check_stmt->get_result();
    
    if ($check_result->num_rows == 0) {
        echo json_encode(["success" => false, "message" => "Request not found or unauthorized"]);
        exit;
    }
    
    // Delete the request from the database
    if ($request_type == 'rental') {
        $sql = "DELETE FROM rental_applications WHERE id = ?";
    } else {
        $sql = "DELETE FROM visit_requests WHERE id = ?";
    }
    
    $stmt = $conn->prepare($sql);
    $stmt->bind_param("s", $request_id);
    $result = $stmt->execute();
    
    if ($result) {
        echo json_encode(["success" => true, "message" => "Request deleted successfully"]);
    } else {
        echo json_encode(["success" => false, "message" => "Failed to delete request"]);
    }
} catch (Exception $e) {
    echo json_encode(["success" => false, "message" => "Database error: " . $e->getMessage()]);
}

$conn->close();
?>