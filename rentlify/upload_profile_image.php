<?php
// Include database connection
include 'connection.php';

// Initialize response array
$response = array();

// Define upload directory
$upload_dir = "uploads/user_profiles/";

// Create directory if it doesn't exist
if (!file_exists($upload_dir)) {
    mkdir($upload_dir, 0777, true);
}

// Check if user ID and image are provided
if (isset($_POST['user_id']) && isset($_FILES['profile_image'])) {
    $user_id = $_POST['user_id'];
    $file = $_FILES['profile_image'];
    
    // Get file information
    $file_name = $file['name'];
    $file_tmp = $file['tmp_name'];
    $file_size = $file['size'];
    $file_error = $file['error'];
    
    // Get file extension
    $file_ext = strtolower(pathinfo($file_name, PATHINFO_EXTENSION));
    
    // Allowed extensions
    $allowed_ext = array('jpg', 'jpeg', 'png');
    
    // Check if extension is allowed
    if (in_array($file_ext, $allowed_ext)) {
        // Check for errors
        if ($file_error === 0) {
            // Check file size (5MB max)
            if ($file_size <= 5242880) {
                // Create unique filename
                $new_file_name = "user_" . $user_id . "_" . time() . "." . $file_ext;
                $file_destination = $upload_dir . $new_file_name;
                
                // Move file to destination
                if (move_uploaded_file($file_tmp, $file_destination)) {
                    // Update database with new profile image path
                    $image_path = $file_destination;
                    
                    // Prepare SQL statement
                    $sql = "UPDATE tableuser SET profile_image = ? WHERE id = ?";
                    $stmt = $conn->prepare($sql);
                    $stmt->bind_param("si", $image_path, $user_id);
                    
                    // Execute query
                    if ($stmt->execute()) {
                        $response["success"] = "1";
                        $response["message"] = "Profile image updated successfully";
                        $response["image_path"] = $image_path;
                    } else {
                        $response["success"] = "0";
                        $response["message"] = "Database update failed: " . $stmt->error;
                    }
                    
                    $stmt->close();
                } else {
                    $response["success"] = "0";
                    $response["message"] = "Failed to upload image";
                }
            } else {
                $response["success"] = "0";
                $response["message"] = "File too large (max 5MB)";
            }
        } else {
            $response["success"] = "0";
            $response["message"] = "Error uploading file: " . $file_error;
        }
    } else {
        $response["success"] = "0";
        $response["message"] = "Invalid file type. Only JPG, JPEG, and PNG allowed";
    }
} else {
    $response["success"] = "0";
    $response["message"] = "Missing user ID or profile image";
}

// Close connection
$conn->close();

// Send JSON response
echo json_encode($response);
?>