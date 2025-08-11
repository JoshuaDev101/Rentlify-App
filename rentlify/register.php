<?php
if ($_SERVER['REQUEST_METHOD'] == 'POST') {
    require_once 'connection.php'; // Database connection

    // Get all form data
    $username = isset($_POST['username']) ? $_POST['username'] : "";
    $password = isset($_POST['password']) ? $_POST['password'] : "";
    $role = isset($_POST['role']) ? $_POST['role'] : "";
    $fullname = isset($_POST['fullname']) ? $_POST['fullname'] : "";
    $email = isset($_POST['email']) ? $_POST['email'] : "";
    $contact = isset($_POST['contact']) ? $_POST['contact'] : "";

    // Check if required fields are empty
    if (empty($username) || empty($password) || empty($role) || empty($fullname) || empty($email) || empty($contact)) {
        echo json_encode(["success" => "0", "message" => "All fields are required"]);
        exit();
    }

    // Validate email format
    if (!filter_var($email, FILTER_VALIDATE_EMAIL)) {
        echo json_encode(["success" => "0", "message" => "Invalid email format"]);
        exit();
    }

    // Validate role input
    if (!in_array($role, ['tenant', 'landlord'])) {
        echo json_encode(["success" => "0", "message" => "Invalid role"]);
        exit();
    }

    // Check if username already exists
    $checkUser = "SELECT * FROM tableuser WHERE username = ?";
    $stmt = mysqli_prepare($conn, $checkUser);
    mysqli_stmt_bind_param($stmt, "s", $username);
    mysqli_stmt_execute($stmt);
    $result = mysqli_stmt_get_result($stmt);
    
    if (mysqli_num_rows($result) > 0) {
        echo json_encode(["success" => "0", "message" => "Username already exists"]);
        exit();
    }

    // Start transaction to ensure data consistency across tables
    mysqli_begin_transaction($conn);
    
    try {
        // Hash the password for security
        $hashedPassword = password_hash($password, PASSWORD_DEFAULT);

        // Set verification status: landlords need approval, tenants are auto-approved
        $status = ($role == 'landlord') ? 'Pending' : 'Verified';

        // Insert data into the tableuser
        $sql = "INSERT INTO tableuser (username, password, role, status) VALUES (?, ?, ?, ?)";
        $stmt = mysqli_prepare($conn, $sql);
        mysqli_stmt_bind_param($stmt, "ssss", $username, $hashedPassword, $role, $status);
        
        if (!mysqli_stmt_execute($stmt)) {
            throw new Exception("Error creating user account: " . mysqli_error($conn));
        }
        
        // Get the newly inserted user ID
        $userId = mysqli_insert_id($conn);
        
        // Insert additional details into either landlord_details or tenant_details based on role
        if ($role == 'landlord') {
            $detailsSql = "INSERT INTO landlord_details (userid, fullname, email, phone) VALUES (?, ?, ?, ?)";
        } else {
            $detailsSql = "INSERT INTO tenant_details (user_id, fullname, email, phone) VALUES (?, ?, ?, ?)";
        }
        
        $detailsStmt = mysqli_prepare($conn, $detailsSql);
        mysqli_stmt_bind_param($detailsStmt, "isss", $userId, $fullname, $email, $contact);
        
        if (!mysqli_stmt_execute($detailsStmt)) {
            throw new Exception("Error saving user details: " . mysqli_error($conn));
        }
        
        // Commit the transaction
        mysqli_commit($conn);
        
        echo json_encode([
            "success" => "1", 
            "message" => "User registered successfully", 
            "role" => $role,
            "user_id" => $userId
        ]);
        
    } catch (Exception $e) {
        // Rollback the transaction on error
        mysqli_rollback($conn);
        echo json_encode(["success" => "0", "message" => $e->getMessage()]);
    }

    mysqli_close($conn);
}
?>