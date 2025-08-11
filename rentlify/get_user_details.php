<?php
// Include database connection
//get_user_details.php
include 'connection.php';

// Initialize response array
$response = array();

// Process the request based on action
if (isset($_POST['action']) && isset($_POST['id'])) {
    $action = $_POST['action'];
    $user_id = $_POST['id'];
    
    // Get user details
    if ($action == "get_details") {
        // Get user data from both tableuser and landlord_details tables
        $sql = "SELECT u.username, u.role, u.status, u.profile_image, 
                ld.fullname, ld.sex, ld.birthday, ld.email, ld.phone 
                FROM tableuser u
                LEFT JOIN landlord_details ld ON u.id = ld.userid
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
            
            // Check if landlord record exists
            $check_sql = "SELECT * FROM landlord_details WHERE userid = ?";
            $check_stmt = $conn->prepare($check_sql);
            $check_stmt->bind_param("i", $user_id);
            $check_stmt->execute();
            $check_result = $check_stmt->get_result();
            
            if ($check_result->num_rows > 0) {
                // Update existing record
                $update_sql = "UPDATE landlord_details SET 
                              fullname = ?, 
                              sex = ?, 
                              birthday = ?,
                              email = ?, 
                              phone = ? 
                              WHERE userid = ?";
                              
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
                $insert_sql = "INSERT INTO landlord_details 
                              (userid, fullname, sex, birthday, email, phone) 
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
                $new_file_name = "user_" . $user_id . "_" . time() . "." . $file_ext;
                $file_destination = $upload_dir . $new_file_name;

                if (move_uploaded_file($file_tmp, $file_destination)) {
                    $image_path = $file_destination;

                    $conn->begin_transaction();

                    try {
                        // Get current profile image to delete it later
                        $sql_old = "SELECT profile_image FROM tableuser WHERE id = ?";
                        $stmt_old = $conn->prepare($sql_old);
                        $stmt_old->bind_param("i", $user_id);
                        $stmt_old->execute();
                        $result_old = $stmt_old->get_result();
                        $old_image = "";
                        if ($result_old->num_rows > 0) {
                            $row_old = $result_old->fetch_assoc();
                            $old_image = $row_old['profile_image'];
                        }
                        $stmt_old->close();

                        // Update tableuser
                        $sql1 = "UPDATE tableuser SET profile_image = ? WHERE id = ?";
                        $stmt1 = $conn->prepare($sql1);
                        $stmt1->bind_param("si", $image_path, $user_id);
                        $stmt1->execute();
                        $stmt1->close();

                        // Update property_posts
                        $sql2 = "UPDATE property_posts SET profile_image = ? WHERE landlord_id = ?";
                        $stmt2 = $conn->prepare($sql2);
                        $stmt2->bind_param("si", $image_path, $user_id);
                        $stmt2->execute();

                        if ($stmt2->affected_rows === 0) {
                            $sql_insert = "INSERT INTO property_posts (landlord_id, profile_image) VALUES (?, ?)";
                            $stmt_insert = $conn->prepare($sql_insert);
                            $stmt_insert->bind_param("is", $user_id, $image_path);
                            $stmt_insert->execute();
                            $stmt_insert->close();
                        }

                        $stmt2->close();
                        $conn->commit();

                        // Delete old image from server (optional cleanup)
                        if (!empty($old_image) && file_exists($old_image)) {
                            unlink($old_image);
                        }

                        $response["upload_success"] = "1";
                        $response["upload_message"] = "Profile image updated successfully";
                        $response["image_path"] = $image_path;

                    } catch (Exception $e) {
                        $conn->rollback();
                        $response["upload_success"] = "0";
                        $response["upload_message"] = "Database sync failed: " . $e->getMessage();
                    }
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