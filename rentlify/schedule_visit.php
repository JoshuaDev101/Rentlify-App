<?php
// Enable error reporting for debugging
error_reporting(E_ALL);
ini_set('display_errors', 1);

// Include database connection
include 'connection.php';

// Enable error logging
function logError($message) {
    error_log('[SCHEDULE_VISIT] ' . $message);
}

// Function to send JSON response
function sendResponse($status, $message, $extra = []) {
    $response = [
        'status' => $status,
        'message' => $message
    ];
    
    // Merge any additional data
    if (!empty($extra)) {
        $response = array_merge($response, $extra);
    }
    
    echo json_encode($response);
    exit;
}

// Validate input parameters
if (!isset($_POST['tenant_id']) || !isset($_POST['property_id']) || !isset($_POST['visit_date'])) {
    logError('Missing required parameters');
    sendResponse('error', 'Missing required fields');
}

// Sanitize and validate inputs
$tenant_id = mysqli_real_escape_string($conn, $_POST['tenant_id']);
$property_id = mysqli_real_escape_string($conn, $_POST['property_id']);
$visit_date = mysqli_real_escape_string($conn, $_POST['visit_date']);

// Log received parameters for debugging
logError("Received Parameters:");
logError("Tenant ID: " . $tenant_id);
logError("Property ID: " . $property_id);
logError("Visit Date: " . $visit_date);

// Validate input values
if (empty($tenant_id) || empty($property_id) || empty($visit_date)) {
    logError('Empty input parameters');
    sendResponse('error', 'Invalid input data');
}

// Verify tenant exists and get full name
$tenant_check_stmt = mysqli_prepare($conn, "SELECT user_id, fullname FROM tenant_details WHERE user_id = ?");
mysqli_stmt_bind_param($tenant_check_stmt, "i", $tenant_id);
mysqli_stmt_execute($tenant_check_stmt);
$tenant_result = mysqli_stmt_get_result($tenant_check_stmt);

if (mysqli_num_rows($tenant_result) == 0) {
    logError("Tenant not found: " . $tenant_id);
    sendResponse('error', 'Tenant not found');
}

$tenant_row = mysqli_fetch_assoc($tenant_result);
$tenant_name = $tenant_row['fullname'];
mysqli_stmt_close($tenant_check_stmt);

// Verify property exists and get landlord ID
$property_stmt = mysqli_prepare($conn, "SELECT landlord_id, title FROM property_posts WHERE id = ?");
mysqli_stmt_bind_param($property_stmt, "i", $property_id);
mysqli_stmt_execute($property_stmt);
$property_result = mysqli_stmt_get_result($property_stmt);

if (mysqli_num_rows($property_result) == 0) {
    logError("Property not found: " . $property_id);
    sendResponse('error', 'Property not found');
}

$property_row = mysqli_fetch_assoc($property_result);
$landlord_id = $property_row['landlord_id'];
$property_title = $property_row['title'];
mysqli_stmt_close($property_stmt);

// Log additional verification details
logError("Verified Tenant Name: " . $tenant_name);
logError("Verified Landlord ID: " . $landlord_id);
logError("Property Title: " . $property_title);

// Check for existing visit request
$check_stmt = mysqli_prepare($conn, "SELECT id FROM visit_requests WHERE tenant_id = ? AND property_id = ? AND visit_date = ?");
mysqli_stmt_bind_param($check_stmt, "iis", $tenant_id, $property_id, $visit_date);
mysqli_stmt_execute($check_stmt);
mysqli_stmt_store_result($check_stmt);

if (mysqli_stmt_num_rows($check_stmt) > 0) {
    logError("Duplicate visit request");
    sendResponse('error', 'A visit request for this property on the selected date already exists.');
}
mysqli_stmt_close($check_stmt);

// Begin transaction for atomic operations
mysqli_begin_transaction($conn);

try {
    // Insert visit request
    $insert_stmt = mysqli_prepare($conn, "INSERT INTO visit_requests (tenant_id, property_id, visit_date, status) VALUES (?, ?, ?, 'pending')");
    mysqli_stmt_bind_param($insert_stmt, "iis", $tenant_id, $property_id, $visit_date);
    
    if (!mysqli_stmt_execute($insert_stmt)) {
        throw new Exception("Failed to insert visit request: " . mysqli_stmt_error($insert_stmt));
    }
    $visit_request_id = mysqli_insert_id($conn);
    mysqli_stmt_close($insert_stmt);

    // Insert landlord notification
    $landlord_message = "You have a new Visit Request from $tenant_name for {$property_title} on $visit_date.";
    $landlord_notif_stmt = mysqli_prepare($conn, 
        "INSERT INTO notifications (user_id, property_id, message, request, notification_for, status) 
         VALUES (?, ?, ?, 'visit', 'landlord', 'unread')");
    mysqli_stmt_bind_param($landlord_notif_stmt, "iis", $landlord_id, $property_id, $landlord_message);
    
    if (!mysqli_stmt_execute($landlord_notif_stmt)) {
        throw new Exception("Failed to insert landlord notification: " . mysqli_stmt_error($landlord_notif_stmt));
    }
    mysqli_stmt_close($landlord_notif_stmt);

    // Insert tenant notification
    $tenant_message = "Your visit request for {$property_title} on $visit_date has been submitted and is awaiting approval.";
    $tenant_notif_stmt = mysqli_prepare($conn, 
        "INSERT INTO notifications (user_id, property_id, message, request, notification_for, status) 
         VALUES (?, ?, ?, 'visit', 'tenant', 'unread')");
    mysqli_stmt_bind_param($tenant_notif_stmt, "iis", $tenant_id, $property_id, $tenant_message);
    
    if (!mysqli_stmt_execute($tenant_notif_stmt)) {
        throw new Exception("Failed to insert tenant notification: " . mysqli_stmt_error($tenant_notif_stmt));
    }
    mysqli_stmt_close($tenant_notif_stmt);

    // Commit transaction
    mysqli_commit($conn);

    // Log success
    logError("Visit request and notifications successfully created");
    sendResponse('success', 'Visit scheduled successfully. Waiting for landlord verification.', [
        'visit_request_id' => $visit_request_id
    ]);

} catch (Exception $e) {
    // Rollback transaction on error
    mysqli_rollback($conn);
    
    // Log detailed error
    logError("Transaction failed: " . $e->getMessage());
    sendResponse('error', 'Database error: Please try again later.');
}

// Close database connection
mysqli_close($conn);
?>