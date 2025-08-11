<?php
header('Content-Type: application/json');
ini_set('display_errors', 0);

include 'connection.php';

if (!isset($_GET['tenant_id']) || empty($_GET['tenant_id'])) {
    echo json_encode(['status' => 'error', 'message' => 'Missing tenant_id parameter']);
    exit;
}

$tenant_id = $_GET['tenant_id'];

try {
    // Step 1: Get all properties rented by this tenant
    $sql = "SELECT 
                pr.id AS rental_id,
                pr.property_id,
                pr.rented_at,
                pp.title AS property_title,
                pp.location AS address,
                pp.price AS rental_price,
                pp.description,
                pp.landlord_id
            FROM property_rented pr
            JOIN property_posts pp ON pr.property_id = pp.id
            WHERE pr.tenant_id = ?
            ORDER BY pr.rented_at DESC";
    
    $stmt = $conn->prepare($sql);
    $stmt->bind_param("i", $tenant_id);
    $stmt->execute();
    $result = $stmt->get_result();
    
    $rentals = [];
    while ($row = $result->fetch_assoc()) {
        // Step 2: For each rental, fetch landlord details
        $landlord_id = $row['landlord_id'];

        $landlord_sql = "SELECT fullname, email, phone FROM landlord_details WHERE userid = ?";
        $landlord_stmt = $conn->prepare($landlord_sql);
        $landlord_stmt->bind_param("i", $landlord_id);
        $landlord_stmt->execute();
        $landlord_result = $landlord_stmt->get_result();
        $landlord = $landlord_result->fetch_assoc();

        $rentals[] = [
            'rental_id' => $row['rental_id'],
            'property_id' => $row['property_id'],
            'property_title' => $row['property_title'],
            'address' => $row['address'],
            'rental_price' => $row['rental_price'],
            'description' => $row['description'],
            'landlord_id' => $landlord_id, // Changed from 'userid' to 'landlord_id'
            'landlord_name' => $landlord['fullname'] ?? null,
            'landlord_email' => $landlord['email'] ?? null,
            'landlord_phone' => $landlord['phone'] ?? null,
            'rented_at' => $row['rented_at']
        ];
        
        $landlord_stmt->close(); // Always close after use
    }
    
    echo json_encode(['status' => 'success', 'rentals' => $rentals]);
} catch (Exception $e) {
    echo json_encode(['status' => 'error', 'message' => $e->getMessage()]);
}

$conn->close();
?>  