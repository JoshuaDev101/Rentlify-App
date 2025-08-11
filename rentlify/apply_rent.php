<?php
// For debugging - comment this out when debugging is done
error_reporting(E_ALL);
ini_set('display_errors', 1);

// Include database connection
include 'connection.php';

// Log incoming request data for debugging
$log_file = 'rental_application_log.txt';
file_put_contents($log_file, date('Y-m-d H:i:s') . " - POST data: " . print_r($_POST, true) . "\n", FILE_APPEND);

// Check if required fields exist
if (isset($_POST['tenant_id']) && isset($_POST['property_id'])) {
    // Validate that values are not empty
    if (empty($_POST['tenant_id']) || empty($_POST['property_id'])) {
        file_put_contents($log_file, date('Y-m-d H:i:s') . " - Error: Values are empty\n", FILE_APPEND);
        echo json_encode(['status' => 'error', 'message' => 'Required fields cannot be empty']);
        exit;
    }
    
    // Sanitize inputs to prevent SQL injection
    $tenant_id = mysqli_real_escape_string($conn, $_POST['tenant_id']);
    $property_id = mysqli_real_escape_string($conn, $_POST['property_id']);
    
    // Log sanitized values
    file_put_contents($log_file, date('Y-m-d H:i:s') . " - Sanitized values: tenant_id = $tenant_id, property_id = $property_id\n", FILE_APPEND);
    
    // Get contact number from tenant details or use a default
    $contact_number = null;
    $contact_stmt = mysqli_prepare($conn, "SELECT contact_number FROM tenant_details WHERE user_id = ?");
    
    if ($contact_stmt) {
        mysqli_stmt_bind_param($contact_stmt, "i", $tenant_id);
        mysqli_stmt_execute($contact_stmt);
        mysqli_stmt_store_result($contact_stmt);
        
        if (mysqli_stmt_num_rows($contact_stmt) > 0) {
            mysqli_stmt_bind_result($contact_stmt, $contact_number);
            mysqli_stmt_fetch($contact_stmt);
        }
        mysqli_stmt_close($contact_stmt);
    } else {
        file_put_contents($log_file, date('Y-m-d H:i:s') . " - Error preparing contact statement: " . mysqli_error($conn) . "\n", FILE_APPEND);
    }
    
    // If no contact number found, use a default
    if ($contact_number === null) {
        $contact_number = "Not provided";
    }
    
    file_put_contents($log_file, date('Y-m-d H:i:s') . " - Contact number: $contact_number\n", FILE_APPEND);
    
    // Check if tenant has already applied for this property
    $check_stmt = mysqli_prepare($conn, "SELECT id FROM rental_applications WHERE tenant_id = ? AND property_id = ?");
    
    if ($check_stmt) {
        mysqli_stmt_bind_param($check_stmt, "ii", $tenant_id, $property_id);
        mysqli_stmt_execute($check_stmt);
        mysqli_stmt_store_result($check_stmt);
        
        if (mysqli_stmt_num_rows($check_stmt) > 0) {
            file_put_contents($log_file, date('Y-m-d H:i:s') . " - Already applied\n", FILE_APPEND);
            echo json_encode(['status' => 'error', 'message' => 'You have already applied for this property']);
            mysqli_stmt_close($check_stmt);
            exit;
        }
        mysqli_stmt_close($check_stmt);
    } else {
        file_put_contents($log_file, date('Y-m-d H:i:s') . " - Error preparing check statement: " . mysqli_error($conn) . "\n", FILE_APPEND);
    }
    
    // Insert rental application
    $insert_stmt = mysqli_prepare($conn, "INSERT INTO rental_applications (tenant_id, property_id, contact_number, status) VALUES (?, ?, ?, 'pending')");
    
    if ($insert_stmt) {
        mysqli_stmt_bind_param($insert_stmt, "iis", $tenant_id, $property_id, $contact_number);
        
        if (mysqli_stmt_execute($insert_stmt)) {
            file_put_contents($log_file, date('Y-m-d H:i:s') . " - Application inserted successfully\n", FILE_APPEND);
            
            // Create notifications
            try {
                // Get property and tenant details
                $property_query = "SELECT p.landlord_id, p.title, p.location, t.fullname 
                                FROM property_posts p, tenant_details t 
                                WHERE p.id = ? AND t.user_id = ?";
                $property_stmt = mysqli_prepare($conn, $property_query);
                
                if ($property_stmt) {
                    mysqli_stmt_bind_param($property_stmt, "ii", $property_id, $tenant_id);
                    mysqli_stmt_execute($property_stmt);
                    $result = mysqli_stmt_get_result($property_stmt);
                    
                    if ($row = mysqli_fetch_assoc($result)) {
                        $landlord_id = $row['landlord_id'];
                        $property_title = $row['title'];
                        $property_location = $row['location'];
                        $tenant_name = $row['fullname'];
                        
                        // Create notification for landlord
                        $landlord_message = "New rental application from $tenant_name for $property_title in $property_location";
                        $landlord_notif_stmt = mysqli_prepare($conn, "INSERT INTO notifications (user_id, property_id, message, notification_for, request) VALUES (?, ?, ?, 'landlord', 'rent')");
                        
                        if ($landlord_notif_stmt) {
                            mysqli_stmt_bind_param($landlord_notif_stmt, "iis", $landlord_id, $property_id, $landlord_message);
                            mysqli_stmt_execute($landlord_notif_stmt);
                            mysqli_stmt_close($landlord_notif_stmt);
                        }
                        
                        // Create notification for tenant
                        $tenant_message = "Your rental application for $property_title in $property_location has been submitted and is awaiting approval";
                        $tenant_notif_stmt = mysqli_prepare($conn, "INSERT INTO notifications (user_id, property_id, message, notification_for, request) VALUES (?, ?, ?, 'tenant', 'rent')");
                        
                        if ($tenant_notif_stmt) {
                            mysqli_stmt_bind_param($tenant_notif_stmt, "iis", $tenant_id, $property_id, $tenant_message);
                            mysqli_stmt_execute($tenant_notif_stmt);
                            mysqli_stmt_close($tenant_notif_stmt);
                        }
                    }
                    mysqli_stmt_close($property_stmt);
                }
            } catch (Exception $e) {
                file_put_contents($log_file, date('Y-m-d H:i:s') . " - Error in notifications: " . $e->getMessage() . "\n", FILE_APPEND);
                // Continue even if notifications fail
            }
            
            echo json_encode(['status' => 'success', 'message' => 'Application submitted successfully']);
        } else {
            $error = mysqli_error($conn);
            file_put_contents($log_file, date('Y-m-d H:i:s') . " - Insert error: $error\n", FILE_APPEND);
            echo json_encode(['status' => 'error', 'message' => 'Database error: ' . $error]);
        }
        mysqli_stmt_close($insert_stmt);
    } else {
        $error = mysqli_error($conn);
        file_put_contents($log_file, date('Y-m-d H:i:s') . " - Error preparing insert statement: $error\n", FILE_APPEND);
        echo json_encode(['status' => 'error', 'message' => 'Database error: ' . $error]);
    }
} else {
    file_put_contents($log_file, date('Y-m-d H:i:s') . " - Missing required fields\n", FILE_APPEND);
    echo json_encode(['status' => 'error', 'message' => 'Missing required fields tenant_id and/or property_id']);
}

mysqli_close($conn);
?>