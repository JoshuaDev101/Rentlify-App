<?php
include 'connection.php';

// Check if request has landlord_id
if (!isset($_GET['landlord_id']) || empty($_GET['landlord_id'])) {
    echo json_encode(['status' => 'error', 'message' => 'Missing landlord_id parameter']);
    exit;
}

$landlord_id = $_GET['landlord_id'];

// Get all properties rented by tenants of this landlord
$sql = "SELECT 
            pr.id AS rental_id,
            pr.property_id,
            pr.tenant_id,
            pr.rented_at,
            pp.title AS property_title,
            pp.location,
            pp.price,
            td.fullname AS tenant_name,
            td.email AS tenant_email,
            td.phone AS tenant_phone
        FROM property_rented pr
        JOIN property_posts pp ON pr.property_id = pp.id
        JOIN tenant_details td ON pr.tenant_id = td.user_id         
        WHERE pp.landlord_id = ?
        ORDER BY pr.rented_at DESC";

$stmt = $conn->prepare($sql);
$stmt->bind_param("i", $landlord_id);
$stmt->execute();
$result = $stmt->get_result();

$rentals = [];
while ($row = $result->fetch_assoc()) {
    $rentals[] = [
        'rental_id' => $row['rental_id'],
        'property_id' => $row['property_id'],
        'user_id' => $row['tenant_id'],
        'property_title' => $row['property_title'],
        'location' => $row['location'],
        'price' => $row['price'],
        'tenant_name' => $row['tenant_name'],
        'tenant_email' => $row['tenant_email'],
        'tenant_phone' => $row['tenant_phone'],
        'rented_at' => $row['rented_at']
    ];
}

echo json_encode(['status' => 'success', 'rentals' => $rentals]);

$conn->close();
?>
