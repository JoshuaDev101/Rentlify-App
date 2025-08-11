<?php
// Enable error reporting for debugging
error_reporting(E_ALL);
ini_set('display_errors', 1);

header('Content-Type: application/json');

// Log incoming request for debugging
file_put_contents('request_log.txt', 
    date('Y-m-d H:i:s') . " - Request: " . 
    print_r($_POST, true) . "\n", 
    FILE_APPEND);

include 'connection.php';

if (!isset($_POST['tenant_id'])) {
    echo json_encode(["error" => "Tenant ID is required"]);
    exit;
}

$tenant_id = $_POST['tenant_id'];

// Query both rental applications and visit requests
$sql = "SELECT 
            'rental' as request_type,
            p.title as title, 
            ra.status, 
            ra.id as request_id,
            ra.created_at as requested_date
        FROM rental_applications ra
        JOIN property_posts p ON ra.property_id = p.id
        WHERE ra.tenant_id = ?
        UNION ALL
        SELECT 
            'visit' as request_type,
            p.title as title, 
            vr.status, 
            vr.id as request_id,
            vr.created_at as requested_date
        FROM visit_requests vr
        JOIN property_posts p ON vr.property_id = p.id
        WHERE vr.tenant_id = ?
        ORDER BY requested_date DESC";

try {
    $stmt = $conn->prepare($sql);
    $stmt->bind_param("ss", $tenant_id, $tenant_id);
    $stmt->execute();
    $result = $stmt->get_result();

    $requests = [];

    if ($result->num_rows > 0) {
        while ($row = $result->fetch_assoc()) {
            $requests[] = $row;
        }
    }

    echo json_encode($requests);
} catch (Exception $e) {
    echo json_encode(["error" => "Database error: " . $e->getMessage()]);
}

$conn->close();
?>