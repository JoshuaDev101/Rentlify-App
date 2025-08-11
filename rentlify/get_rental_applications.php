<?php
include 'connection.php';

if (!isset($_GET['landlord_id'])) {
    echo json_encode([
        'success' => false,
        'message' => 'Missing landlord_id parameter.'
    ]);
    exit;
}

$landlord_id = $_GET['landlord_id']; // received from Android

$sql = "SELECT 
            ra.id AS application_id,
            ra.contact_number,
            ra.status,
            ra.created_at,
            p.title AS property_title,
            u.username AS tenant_name
        FROM rental_applications ra
        JOIN property_posts p ON p.id = ra.property_id
        JOIN tableuser u ON u.id = ra.tenant_id
        WHERE p.landlord_id = ?";

$stmt = $conn->prepare($sql);
$stmt->bind_param("i", $landlord_id);
$stmt->execute();
$result = $stmt->get_result();

$response = ['applications' => []];

while ($row = $result->fetch_assoc()) {
    $response['applications'][] = [
        'application_id' => $row['application_id'],
        'contact_number' => $row['contact_number'],
        'status' => $row['status'],
        'created_at' => $row['created_at'],
        'property_title' => $row['property_title'],
        'tenant_name' => $row['tenant_name']
    ];
}

$response['success'] = true;
echo json_encode($response);

$conn->close();
?>
