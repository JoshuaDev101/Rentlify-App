<?php
// Include database connection
//tenant_get_user_details.php
include 'connection.php';

// Initialize response array
$response = array();

// Process the request based on action
if (isset($_POST['action']) && isset($_POST['id'])) {
    $action = $_POST['action'];
    $user_id = $_POST['id'];
    
    // Get user details
    if ($action == "get_details") {
        // Get user data from both tableuser and tenant_details tables
        $sql = "SELECT u.username, u.role, u.status, u.profile_image, 
                td.fullname, td.sex, td.birthday, td.email, td.phone 
                FROM tableuser u
                LEFT JOIN tenant_details td ON u.id = td.user_id
                WHERE u.id = ?";
                
        $stmt = $conn->prepare($sql);
        $stmt->bind_param("i", $user_id);
        $stmt->execute();
        $result = $stmt->get_result();

        if ($result->num_rows > 0) {
            $user = $result->fetch_assoc();
            $response["success"] = "1";
            $response["user"] = array($user);
        } else {
            $response["success"] = "0";
            $response["message"] = "User not found";
        }

        $stmt->close();
    }
    // Update profile
    else if ($action == "update_profile") {
        // Validate required fields
        if (!isset($_POST['fullname']) || empty($_POST['fullname']) ||
            !isset($_POST['email']) || empty($_POST['email']) ||
            !isset($_POST['phone']) || empty($_POST['phone']) ||
            !isset($_POST['birthday']) || empty($_POST['birthday']) ||
            !isset($_POST['sex']) || empty($_POST['sex'])) {
            
            $response["success"] = "0";
            $response["message"] = "All fields are required";
        } else {
            $fullname = $_POST['fullname'];
            $sex = $_POST['sex'];
            $birthday = $_POST['birthday'];
            $email = $_POST['email'];
            $phone = $_POST['phone'];
            
            // Check if tenant record exists
            $check_sql = "SELECT * FROM tenant_details WHERE user_id = ?";
            $check_stmt = $conn->prepare($check_sql);
            $check_stmt->bind_param("i", $user_id);
            $check_stmt->execute();
            $check_result = $check_stmt->get_result();
            
            if ($check_result->num_rows > 0) {
                // Update existing record
                $update_sql = "UPDATE tenant_details SET 
                              fullname = ?, 
                              sex = ?, 
                              birthday = ?,
                              email = ?, 
                              phone = ? 
                              WHERE user_id = ?";
                              
                $update_stmt = $conn->prepare($update_sql);
                $update_stmt->bind_param("sssssi", $fullname, $sex, $birthday, $email, $phone, $user_id);
                
                if ($update_stmt->execute()) {
                    $response["success"] = "1";
                    $response["message"] = "Profile updated successfully";
                } else {
                    $response["success"] = "0";
                    $response["message"] = "Failed to update profile: " . $conn->error;
                }
                
                $update_stmt->close();
            } else {
                // Insert new record
                $insert_sql = "INSERT INTO tenant_details 
                              (user_id, fullname, sex, birthday, email, phone) 
                              VALUES (?, ?, ?, ?, ?, ?)";
                              
                $insert_stmt = $conn->prepare($insert_sql);
                $insert_stmt->bind_param("isssss", $user_id, $fullname, $sex, $birthday, $email, $phone);
                
                if ($insert_stmt->execute()) {
                    $response["success"] = "1";
                    $response["message"] = "Profile created successfully";
                } else {
                    $response["success"] = "0";
                    $response["message"] = "Failed to create profile: " . $conn->error;
                }
                
                $insert_stmt->close();
            }
            
            $check_stmt->close();
        }
    } else {
        $response["success"] = "0";
        $response["message"] = "Invalid action";
    }
} else {
    $response["success"] = "0";
    $response["message"] = "Missing required parameters";
}

// Handle profile image upload if files are present
if (isset($_FILES['profile_image']) && isset($_POST['id'])) {
    $user_id = $_POST['id'];
    $upload_dir = "uploads/user_profiles/";

    if (!file_exists($upload_dir)) {
        mkdir($upload_dir, 0777, true);
    }

    $file = $_FILES['profile_image'];
    $file_name = $file['name'];
    $file_tmp = $file['tmp_name'];
    $file_size = $file['size'];
    $file_error = $file['error'];
    $file_ext = strtolower(pathinfo($file_name, PATHINFO_EXTENSION));
    $allowed_ext = array('jpg', 'jpeg', 'png');

    if (in_array($file_ext, $allowed_ext)) {
        if ($file_error === 0) {
            if ($file_size <= 5242880) { // 5MB
                $new_file_name = "tenant_" . $user_id . "_" . time() . "." . $file_ext;
                $file_destination = $upload_dir . $new_file_name;

                if (move_uploaded_file($file_tmp, $file_destination)) {
                    $image_path = $file_destination;

                    // Update tableuser with new profile image
                    $sql = "UPDATE tableuser SET profile_image = ? WHERE id = ?";
                    $stmt = $conn->prepare($sql);
                    $stmt->bind_param("si", $image_path, $user_id);
                    
                    if ($stmt->execute()) {
                        $response["upload_success"] = "1";
                        $response["upload_message"] = "Profile image updated successfully";
                        $response["image_path"] = $image_path;
                    } else {
                        $response["upload_success"] = "0";
                        $response["upload_message"] = "Database update failed: " . $conn->error;
                    }
                    
                    $stmt->close();
                } else {
                    $response["upload_success"] = "0";
                    $response["upload_message"] = "Failed to upload image";
                }
            } else {
                $response["upload_success"] = "0";
                $response["upload_message"] = "File too large (max 5MB)";
            }
        } else {
            $response["upload_success"] = "0";
            $response["upload_message"] = "Error uploading file: " . $file_error;
        }
    } else {
        $response["upload_success"] = "0";
        $response["upload_message"] = "Invalid file type. Only JPG, JPEG, and PNG allowed";
    }
}

$conn->close();

// Output JSON response
echo json_encode($response);
?>