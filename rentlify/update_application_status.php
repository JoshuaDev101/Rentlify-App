<?php
include 'connection.php';

// Check if request is POST
if ($_SERVER['REQUEST_METHOD'] !== 'POST') {
    echo json_encode(['status' => 'error', 'message' => 'Invalid request method']);
    exit;
}

// Get parameters from POST data
$application_id = isset($_POST['application_id']) ? $_POST['application_id'] : 0;
$new_status = isset($_POST['status']) ? $_POST['status'] : '';

if ($application_id <= 0 || empty($new_status)) {
    echo json_encode(['status' => 'error', 'message' => 'Missing required parameters']);
    exit;
}

// Start transaction
$conn->begin_transaction();

try {
    // Update application status
    $update_sql = "UPDATE rental_applications SET status = ? WHERE id = ?";
    $stmt = $conn->prepare($update_sql);
    $stmt->bind_param("si", $new_status, $application_id);
    $stmt->execute();
    
    // Get application details for notifications
    $get_app_sql = "SELECT property_id, tenant_id FROM rental_applications WHERE id = ?";
    $stmt = $conn->prepare($get_app_sql);
    $stmt->bind_param("i", $application_id);
    $stmt->execute();
    $result = $stmt->get_result();
    
    if ($result->num_rows > 0) {
        $application = $result->fetch_assoc();
        $property_id = $application['property_id'];
        $tenant_id = $application['tenant_id'];
        
        // Get property details
        $get_property_sql = "SELECT p.title, p.price, p.location, p.landlord_id 
                           FROM property_posts p 
                           WHERE p.id = ?";
        $stmt = $conn->prepare($get_property_sql);
        $stmt->bind_param("i", $property_id);
        $stmt->execute();
        $property_result = $stmt->get_result();
        $property_details = $property_result->fetch_assoc();
        
        // Get tenant name
        $get_tenant_sql = "SELECT fullname FROM tenant_details WHERE user_id = ?";
        $stmt = $conn->prepare($get_tenant_sql);
        $stmt->bind_param("i", $tenant_id);
        $stmt->execute();
        $tenant_result = $stmt->get_result();
        $tenant_details = $tenant_result->fetch_assoc();
        
        $title = $property_details['title'];
        $location = $property_details['location'];
        $landlord_id = $property_details['landlord_id'];
        $tenant_name = $tenant_details['fullname'];
        
        // Create notification messages based on status
        if ($new_status === "approved") {
            // Notification for tenant
            $tenant_message = "Your rental application for $title in $location has been APPROVED! You can now move forward and check My Rental pages.";
            
            // Insert notification for tenant
            $insert_tenant_notif = "INSERT INTO notifications (status, request, user_id, property_id, message, notification_for, read_status) 
                                  VALUES ('unread', 'rent', ?, ?, ?, 'tenant', 0)";
            $stmt = $conn->prepare($insert_tenant_notif);
            $stmt->bind_param("iis", $tenant_id, $property_id, $tenant_message);
            $stmt->execute();
            
            // Insert into property_rented table with all details
            insertRentedProperty($conn, $property_id, $tenant_id, $property_details, $tenant_details);
            
            // Update property availability to "Rented" in property_posts table
            $update_property_sql = "UPDATE property_posts SET availability = 'Rented' WHERE id = ?";
            $stmt = $conn->prepare($update_property_sql);
            $stmt->bind_param("i", $property_id);
            $stmt->execute();
            
        } else if ($new_status === "rejected") {
            // Notification for tenant
            $tenant_message = "Your rental application for $title in $location has been declined.";
            
            // Insert notification for tenant
            $insert_tenant_notif = "INSERT INTO notifications (status, request, user_id, property_id, message, notification_for, read_status) 
                                  VALUES ('unread', 'rent', ?, ?, ?, 'tenant', 0)";
            $stmt = $conn->prepare($insert_tenant_notif);
            $stmt->bind_param("iis", $tenant_id, $property_id, $tenant_message);
            $stmt->execute();
        }
        
        // Notification for landlord - confirmation of action
        $action_status = $new_status === "approved" ? "approved" : "rejected";
        $landlord_message = "You have $action_status the rental application from $tenant_name for $title in $location.";
        
        // Insert notification for landlord
        $insert_landlord_notif = "INSERT INTO notifications (status, request, user_id, property_id, message, notification_for, read_status) 
                                VALUES ('unread', 'rent', ?, ?, ?, 'landlord', 0)";
        $stmt = $conn->prepare($insert_landlord_notif);
        $stmt->bind_param("iis", $landlord_id, $property_id, $landlord_message);
        $stmt->execute();
    }
    
    // Commit transaction
    $conn->commit();
    
    echo json_encode(['status' => 'success', 'message' => 'Application status updated']);
    
} catch (Exception $e) {
    // Rollback transaction on error
    $conn->rollback();
    echo json_encode(['status' => 'error', 'message' => 'Database error: ' . $e->getMessage()]);
}

$conn->close();

// Helper function to insert property into rented table
function insertRentedProperty($conn, $property_id, $tenant_id, $property_details, $tenant_details) {
    // Get landlord name
    $get_landlord_sql = "SELECT fullname FROM landlord_details WHERE userid = ?";
    $stmt = $conn->prepare($get_landlord_sql);
    $stmt->bind_param("i", $property_details['landlord_id']);
    $stmt->execute();
    $landlord_result = $stmt->get_result();
    $landlord_details = $landlord_result->fetch_assoc();
    
    // Prepare variables for binding
    $title = $property_details['title'];
    $price = $property_details['price'];
    $location = $property_details['location'];
    $tenant_name = $tenant_details['fullname'];
    $landlord_name = $landlord_details['fullname'];
    
    // Insert into property_rented table with all details
    $insert_sql = "INSERT INTO property_rented (property_id, tenant_id, title, price, location, tenant_name, landlord_name) 
                 VALUES (?, ?, ?, ?, ?, ?, ?)";
    $stmt = $conn->prepare($insert_sql);
    if (!$stmt) {
        throw new Exception("Prepare failed for insert_sql: " . $conn->error);
    }
    $stmt->bind_param("iisdsss", 
        $property_id, 
        $tenant_id, 
        $title, 
        $price, 
        $location,
        $tenant_name,
        $landlord_name
    );
    $stmt->execute();
}
?>