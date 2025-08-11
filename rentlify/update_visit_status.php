<?php
// Enable error reporting for debugging
error_reporting(E_ALL);
ini_set('display_errors', 1);

include 'connection.php';

// Enable error logging
function logError($message) {
    error_log('[UPDATE_VISIT_STATUS] ' . $message);
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
    
    header('Content-Type: application/json');
    echo json_encode($response);
    exit;
}

// Validate input parameters
if (!isset($_POST['visit_id']) || !isset($_POST['status'])) {
    logError('Missing required parameters');
    sendResponse('error', 'Missing required fields');
}

// Sanitize inputs
$visit_id = mysqli_real_escape_string($conn, $_POST['visit_id']);
$status = mysqli_real_escape_string($conn, $_POST['status']); 

// Verify status is valid
if ($status != 'approved' && $status != 'rejected') {
    logError('Invalid status: ' . $status);
    sendResponse('error', 'Invalid status value');
}

// Begin transaction
mysqli_begin_transaction($conn);

try {
    // Retrieve visit request details
    $visit_stmt = mysqli_prepare($conn, "
        SELECT vr.tenant_id, vr.property_id, vr.visit_date, 
               pp.title AS property_name, 
               td.fullname AS tenant_name
        FROM visit_requests vr
        JOIN property_posts pp ON vr.property_id = pp.id
        JOIN tenant_details td ON vr.tenant_id = td.user_id
        WHERE vr.id = ?
    ");
    mysqli_stmt_bind_param($visit_stmt, "i", $visit_id);
    mysqli_stmt_execute($visit_stmt);
    $visit_result = mysqli_stmt_get_result($visit_stmt);

    if (mysqli_num_rows($visit_result) == 0) {
        throw new Exception("Visit request not found");
    }

    $visit_details = mysqli_fetch_assoc($visit_result);
    mysqli_stmt_close($visit_stmt);

    // Update visit request status
    $update_stmt = mysqli_prepare($conn, "UPDATE visit_requests SET status = ? WHERE id = ?");
    mysqli_stmt_bind_param($update_stmt, "si", $status, $visit_id);
    
    if (!mysqli_stmt_execute($update_stmt)) {
        throw new Exception("Failed to update visit status");
    }
    mysqli_stmt_close($update_stmt);

    // Prepare notification message for tenant
    $tenant_message = "Your visit request for {$visit_details['property_name']} on {$visit_details['visit_date']} has been {$status}.";
    
    // Insert notification for tenant
    $tenant_notif_stmt = mysqli_prepare($conn, 
        "INSERT INTO notifications (user_id, property_id, message, request, notification_for, status) 
         VALUES (?, ?, ?, 'visit', 'tenant', 'unread')");
    mysqli_stmt_bind_param($tenant_notif_stmt, "iis", 
        $visit_details['tenant_id'], 
        $visit_details['property_id'], 
        $tenant_message
    );
    
    if (!mysqli_stmt_execute($tenant_notif_stmt)) {
        throw new Exception("Failed to insert tenant notification");
    }
    mysqli_stmt_close($tenant_notif_stmt);

    // Commit transaction
    mysqli_commit($conn);

    // Log success
    logError("Visit status updated successfully. Status: " . $status);
    sendResponse('success', 'Visit status updated successfully', [
        'visit_details' => $visit_details
    ]);

} catch (Exception $e) {
    // Rollback transaction on error
    mysqli_rollback($conn);
    
    // Log detailed error
    logError("Transaction failed: " . $e->getMessage());
    sendResponse('error', 'Failed to update visit status: ' . $e->getMessage());
}

// Close database connection
mysqli_close($conn);
?>