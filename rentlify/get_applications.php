<?php
// get_applications.php

header('Content-Type: application/json');
include 'connection.php'; // make sure this connects to your DB

if (!isset($_GET['property_id'])) {
    echo json_encode(['success' => false, 'message' => 'Missing property_id']);
    exit();
}

$property_id = intval($_GET['property_id']);
$response = [];

$sql = "SELECT 
            ra.id, ra.property_id, ra.tenant_id, ra.contact_number, ra.status, ra.created_at,
            u.username AS tenant_name
        FROM rental_applications ra
        JOIN users u ON ra.tenant_id = u.id
        WHERE ra.property_id = ?";
        
$stmt = $conn->prepare($sql);
$stmt->bind_param("i", $property_id);
$stmt->execute();
$result = $stmt->get_result();

$applications = [];

while ($row = $result->fetch_assoc()) {
    $applications[] = [
        'id' => $row['id'],
        'tenant_name' => $row['tenant_name'],
        'contact_number' => $row['contact_number'],
        'status' => $row['status'],
        'created_at' => $row['created_at']
    ];
}

echo json_encode(['success' => true, 'applications' => $applications]);
