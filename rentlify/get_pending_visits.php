<?php
error_reporting(0);
ini_set('display_errors', 0);

include 'connection.php';
ob_start();

$response = [];

if (isset($_POST['landlord_id'])) {
    $landlord_id = $_POST['landlord_id'];
    
    try {
        // First, get landlord's username from their ID
        $stmtUser = $conn->prepare("SELECT username FROM tableuser WHERE id = ?");
        $stmtUser->bind_param("i", $landlord_id);
        $stmtUser->execute();
        $resultUser = $stmtUser->get_result();
        
        if ($rowUser = $resultUser->fetch_assoc()) {
            $landlordUsername = $rowUser['username'];
        } else {
            throw new Exception("Landlord not found");
        }
        $stmtUser->close();

        // Now get the visits for properties posted by this landlord
        $stmt = $conn->prepare(
            "SELECT vr.id, vr.tenant_id, vr.property_id, vr.visit_date, vr.status, 
                    p.title AS property_name, u.username AS tenant_name
             FROM visit_requests vr
             JOIN property_posts p ON vr.property_id = p.id
             JOIN tableuser u ON vr.tenant_id = u.id
             WHERE p.username = ? AND vr.status = 'pending'
             ORDER BY vr.visit_date ASC"
        );

        $stmt->bind_param("s", $landlordUsername);
        $stmt->execute();
        $result = $stmt->get_result();
        
        $visits = [];
        while ($row = $result->fetch_assoc()) {
            $visits[] = $row;
        }
        
        $response['status'] = "success";
        $response['visits'] = $visits;
        $stmt->close();
    } catch (Exception $e) {
        error_log("Database error in get_pending_visits.php: " . $e->getMessage(), 0);
        $response['status'] = "error";
        $response['message'] = "Database error: " . $e->getMessage();
    }
} else {
    $response['status'] = "error";
    $response['message'] = "Missing landlord_id";
}

ob_end_clean();
header('Content-Type: application/json');
echo json_encode($response);
$conn->close();
