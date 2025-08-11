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
    'data' => null
);

// Check if request method is POST
if ($_SERVER['REQUEST_METHOD'] == 'POST') {
    // Get user identification data
    $userid = isset($_POST['userid']) ? $_POST['userid'] : null;
    $username = isset($_POST['username']) ? $_POST['username'] : null;
    
    // Check if userid is provided
    if (!$userid) {
        $response['error'] = true;
        $response['message'] = 'User ID is required';
        echo json_encode($response);
        exit;
    }

    // Personal information
    $fullname = isset($_POST['fullname']) ? trim($_POST['fullname']) : '';
    $sex = isset($_POST['sex']) ? trim($_POST['sex']) : '';
    $birthday = isset($_POST['birthday']) ? trim($_POST['birthday']) : '';
    $email = isset($_POST['email']) ? trim($_POST['email']) : '';
    $address = isset($_POST['address']) ? trim($_POST['address']) : '';
    $phone = isset($_POST['phone']) ? trim($_POST['phone']) : '';
    
    // Property information
    $location = isset($_POST['location']) ? trim($_POST['location']) : '';
    $property_type = isset($_POST['property_type']) ? trim($_POST['property_type']) : '';
    
    // Social media information (optional)
    $facebook = isset($_POST['facebook']) ? trim($_POST['facebook']) : '';
    $instagram = isset($_POST['instagram']) ? trim($_POST['instagram']) : '';
    $twitter = isset($_POST['twitter']) ? trim($_POST['twitter']) : '';
    $linkedin = isset($_POST['linkedin']) ? trim($_POST['linkedin']) : '';
    
    // Validate required fields
    if (empty($fullname) || empty($sex) || empty($birthday) || empty($email) || 
        empty($address) || empty($phone) || empty($location) || empty($property_type)) {
        $response['error'] = true;
        $response['message'] = 'All required fields must be filled';
        echo json_encode($response);
        exit;
    }
    
    // Create directories if they don't exist
    $upload_directory = "uploads/landlord_docs/";
    if (!is_dir($upload_directory)) {
        mkdir($upload_directory, 0755, true);
    }
    
    // Handle facial verification image
    $facial_image = isset($_POST['facial_image']) ? $_POST['facial_image'] : null;
    $facial_file_path = null;
    
    if ($facial_image) {
        // Generate unique filename
        $facial_file_name = uniqid($userid . '_face_', true) . '.jpg';
        $facial_file_path = $upload_directory . $facial_file_name;
        
        // Decode and save image
        $facial_image_data = base64_decode($facial_image);
        if (file_put_contents($facial_file_path, $facial_image_data)) {
            // Save file reference to database
            $insert_facial_query = "INSERT INTO landlord_files (userid, file_name, file_path, file_type, upload_date) 
                VALUES (?, ?, ?, 'facial_verification', NOW())";
            
            $stmt = mysqli_prepare($conn, $insert_facial_query);
            mysqli_stmt_bind_param($stmt, "iss", $userid, $facial_file_name, $facial_file_path);
            
            if (!mysqli_stmt_execute($stmt)) {
                $response['error'] = true;
                $response['message'] = 'Error saving facial verification to database: ' . mysqli_error($conn);
                echo json_encode($response);
                exit;
            }
        } else {
            $response['error'] = true;
            $response['message'] = 'Failed to save facial verification image';
            echo json_encode($response);
            exit;
        }
    } else {
        $response['error'] = true;
        $response['message'] = 'Facial verification image is required';
        echo json_encode($response);
        exit;
    }
    
    // Handle document file
    $document_file = isset($_POST['document_file']) ? $_POST['document_file'] : null;
    $document_name = isset($_POST['document_name']) ? $_POST['document_name'] : 'document';
    $document_file_path = null;
    
    if ($document_file) {
        // Generate unique filename
        $document_file_name = uniqid($userid . '_doc_', true) . '_' . $document_name;
        $document_file_path = $upload_directory . $document_file_name;
        
        // Decode and save document
        $document_data = base64_decode($document_file);
        if (file_put_contents($document_file_path, $document_data)) {
            // Save file reference to database
            $insert_document_query = "INSERT INTO landlord_files (userid, file_name, file_path, file_type, upload_date) 
                VALUES (?, ?, ?, 'document', NOW())";
            
            $stmt = mysqli_prepare($conn, $insert_document_query);
            mysqli_stmt_bind_param($stmt, "iss", $userid, $document_name, $document_file_path);
            
            if (!mysqli_stmt_execute($stmt)) {
                $response['error'] = true;
                $response['message'] = 'Error saving document to database: ' . mysqli_error($conn);
                echo json_encode($response);
                exit;
            }
        } else {
            $response['error'] = true;
            $response['message'] = 'Failed to save document file';
            echo json_encode($response);
            exit;
        }
    } else {
        $response['error'] = true;
        $response['message'] = 'Verification document is required';
        echo json_encode($response);
        exit;
    }
    
    // Check if user details already exist
    $check_query = "SELECT * FROM landlord_details WHERE userid = ?";
    $check_stmt = mysqli_prepare($conn, $check_query);
    mysqli_stmt_bind_param($check_stmt, "i", $userid);
    mysqli_stmt_execute($check_stmt);
    $check_result = mysqli_stmt_get_result($check_stmt);
    $existing_details = mysqli_fetch_assoc($check_result);
    
    if ($existing_details) {
        // Update existing record
        $update_query = "UPDATE landlord_details 
            SET fullname = ?, location = ?, property_type = ?, phone = ?,
            sex = ?, birthday = ?, email = ?, address = ?,
            facebook = ?, instagram = ?, twitter = ?, linkedin = ?,
            created_at = NOW()
            WHERE userid = ?";
        
        $stmt = mysqli_prepare($conn, $update_query);
        mysqli_stmt_bind_param($stmt, "ssssssssssssi", 
            $fullname, $location, $property_type, $phone,
            $sex, $birthday, $email, $address,
            $facebook, $instagram, $twitter, $linkedin,
            $userid);
    } else {
        // Insert new record
        $insert_query = "INSERT INTO landlord_details 
            (userid, fullname, location, property_type, phone, 
            sex, birthday, email, address,
            facebook, instagram, twitter, linkedin, created_at, created_at) 
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, NOW(), NOW())";
        
        $stmt = mysqli_prepare($conn, $insert_query);
        mysqli_stmt_bind_param($stmt, "issssssssssss", 
            $userid, $fullname, $location, $property_type, $phone,
            $sex, $birthday, $email, $address,
            $facebook, $instagram, $twitter, $linkedin);
    }
    
    // Execute the query
    if (mysqli_stmt_execute($stmt)) {
        // Update user status to pending
        $status_update_query = "UPDATE tableuser SET status = 'Pending' WHERE id = ?";
        $status_stmt = mysqli_prepare($conn, $status_update_query);
        mysqli_stmt_bind_param($status_stmt, "i", $userid);
        
        if (mysqli_stmt_execute($status_stmt)) {
            $response['error'] = false;
            $response['message'] = 'Verification submitted successfully. Please wait for admin approval.';
        } else {
            $response['error'] = true;
            $response['message'] = 'Error updating user status: ' . mysqli_error($conn);
        }
    } else {
        $response['error'] = true;
        $response['message'] = 'Error saving personal details: ' . mysqli_error($conn);
    }
} else {
    $response['error'] = true;
    $response['message'] = 'Invalid request method';
}

// Return JSON response
header('Content-Type: application/json');
echo json_encode($response);