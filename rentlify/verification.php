<?php
// Enable error reporting for debugging
// Comment this out in production
error_reporting(E_ALL);
ini_set('display_errors', 1);

// Include database connection
include 'connection.php';

// Initialize response array
$response = array(
    'error' => false,
    'message' => '',
    'status' => '',
    'data' => null
);

// Check if request method is POST
if ($_SERVER['REQUEST_METHOD'] == 'POST') {
    // Get user ID from request
    $userid = isset($_POST['userid']) ? $_POST['userid'] : null;
    
    // Check if userid is provided
    if (!$userid) {
        $response['error'] = true;
        $response['message'] = 'User ID is required';
        echo json_encode($response);
        exit;
    }
    
    // Get user status
    $status_query = "SELECT status FROM tableuser WHERE id = ?";
    $status_stmt = mysqli_prepare($conn, $status_query);
    mysqli_stmt_bind_param($status_stmt, "i", $userid);
    mysqli_stmt_execute($status_stmt);
    $status_result = mysqli_stmt_get_result($status_stmt);
    
    if ($status_row = mysqli_fetch_assoc($status_result)) {
        $response['status'] = $status_row['status'];
        
        // Get existing user details if any
        $details_query = "SELECT * FROM landlord_details WHERE userid = ?";
        $details_stmt = mysqli_prepare($conn, $details_query);
        mysqli_stmt_bind_param($details_stmt, "i", $userid);
        mysqli_stmt_execute($details_stmt);
        $details_result = mysqli_stmt_get_result($details_stmt);
        
        if ($details_row = mysqli_fetch_assoc($details_result)) {
            $response['data'] = array(
                'fullname' => $details_row['fullname'],
                'sex' => $details_row['sex'],
                'birthday' => $details_row['birthday'],
                'email' => $details_row['email'],
                'address' => $details_row['address'],
                'phone' => $details_row['phone'],
                'location' => $details_row['location'],
                'property_type' => $details_row['property_type'],
                'facebook' => $details_row['facebook'],
                'instagram' => $details_row['instagram'],
                'twitter' => $details_row['twitter'],
                'linkedin' => $details_row['linkedin']
            );
        }
        
        $response['message'] = 'Status retrieved successfully';
    } else {
        $response['error'] = true;
        $response['message'] = 'User not found';
    }
} else {
    $response['error'] = true;
    $response['message'] = 'Invalid request method';
}

// Return JSON response
header('Content-Type: application/json');
echo json_encode($response);