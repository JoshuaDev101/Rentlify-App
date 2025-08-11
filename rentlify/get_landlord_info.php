<?php
// For debugging - comment this out when debugging is done
error_reporting(E_ALL);
ini_set('display_errors', 1);

// Include database connection
include 'connection.php';

// Log incoming request data for debugging
$log_file = 'landlord_info_log.txt';
file_put_contents($log_file, date('Y-m-d H:i:s') . " - POST data: " . print_r($_POST, true) . "\n", FILE_APPEND);

// Check if property_id exists
if (isset($_POST['property_id'])) {
    // Validate that property_id is not empty
    if (empty($_POST['property_id'])) {
        file_put_contents($log_file, date('Y-m-d H:i:s') . " - Error: property_id is empty\n", FILE_APPEND);
        echo json_encode(['status' => 'error', 'message' => 'Property ID cannot be empty']);
        exit;
    }
    
    // Sanitize input to prevent SQL injection
    $property_id = mysqli_real_escape_string($conn, $_POST['property_id']);
    
    // Log sanitized value
    file_put_contents($log_file, date('Y-m-d H:i:s') . " - Sanitized value: property_id = $property_id\n", FILE_APPEND);
    
    // Query to get landlord details for the property including avatar from tableuser
    $query = "SELECT l.fullname, l.phone, l.email, l.address, l.location, 
                     l.facebook, l.instagram, l.twitter, l.linkedin, 
                     p.title, p.description, p.price, p.location as property_location,
                     u.profile_image as avatar
              FROM property_posts p
              JOIN landlord_details l ON p.landlord_id = l.userid
              JOIN tableuser u ON l.userid = u.id
              WHERE p.id = ?";
    
    $stmt = mysqli_prepare($conn, $query);
    
    if ($stmt) {
        mysqli_stmt_bind_param($stmt, "i", $property_id);
        mysqli_stmt_execute($stmt);
        $result = mysqli_stmt_get_result($stmt);
        
        if ($row = mysqli_fetch_assoc($result)) {
            // Format data for response
            $landlord_info = [
                'fullname' => $row['fullname'] ?? 'Not provided',
                'phone' => $row['phone'] ?? 'Not provided',
                'email' => $row['email'] ?? 'Not provided',
                'address' => $row['address'] ?? 'Not provided',
                'location' => $row['location'] ?? 'Not provided',
                'avatar' => $row['avatar'] ?? null,
                'social_media' => [
                    'facebook' => $row['facebook'] ?? '',
                    'instagram' => $row['instagram'] ?? '',
                    'twitter' => $row['twitter'] ?? '',
                    'linkedin' => $row['linkedin'] ?? ''
                ],
                'property' => [
                    'title' => $row['title'] ?? 'Not provided',
                    'description' => $row['description'] ?? 'Not provided',
                    'price' => $row['price'] ?? 'Not provided',
                    'location' => $row['property_location'] ?? 'Not provided'
                ]
            ];
            
            file_put_contents($log_file, date('Y-m-d H:i:s') . " - Landlord info retrieved successfully\n", FILE_APPEND);
            echo json_encode(['status' => 'success', 'landlord_info' => $landlord_info]);
        } else {
            file_put_contents($log_file, date('Y-m-d H:i:s') . " - No landlord info found for property ID: $property_id\n", FILE_APPEND);
            echo json_encode(['status' => 'error', 'message' => 'No landlord information found for this property']);
        }
        mysqli_stmt_close($stmt);
    } else {
        $error = mysqli_error($conn);
        file_put_contents($log_file, date('Y-m-d H:i:s') . " - Error preparing statement: $error\n", FILE_APPEND);
        echo json_encode(['status' => 'error', 'message' => 'Database error: ' . $error]);
    }
} else {
    file_put_contents($log_file, date('Y-m-d H:i:s') . " - Missing required field: property_id\n", FILE_APPEND);
    echo json_encode(['status' => 'error', 'message' => 'Missing required field: property_id']);
}

mysqli_close($conn);
?>