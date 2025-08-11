<?php
header("Content-Type: application/json; charset=UTF-8"); 
error_reporting(0); 

if ($_SERVER['REQUEST_METHOD'] == 'POST') {
    if (!isset($_POST['username'], $_POST['password'])) {
        echo json_encode(["success" => "0", "message" => "Missing required fields", "login" => []]);
        exit();
    }

    $username = trim($_POST['username']);
    $password = trim($_POST['password']);

    require_once 'connection.php';

    $sql = "SELECT * FROM tableuser WHERE username = ?";
    $stmt = mysqli_prepare($conn, $sql);
    
    if (!$stmt) {
        echo json_encode(["success" => "0", "message" => "Database query failed", "login" => []]);
        exit();
    }

    mysqli_stmt_bind_param($stmt, "s", $username);
    mysqli_stmt_execute($stmt);
    $response = mysqli_stmt_get_result($stmt);

    $result = ["success" => "0", "message" => "User not found", "login" => []];

    if ($row = mysqli_fetch_assoc($response)) {
        if (password_verify($password, $row['password'])) {
            $index['userid'] = $row['id'];
            $index['username'] = $row['username'];
            $index['role'] = isset($row['role']) ? $row['role'] : "unknown";
            $index['status'] = isset($row['status']) ? $row['status'] : "unknown";

            $result["login"][] = $index;
            $result["success"] = "1";
            $result["message"] = "Login successful";
        } else {
            $result["message"] = "Incorrect password";
        }
    }

    echo json_encode($result);
    mysqli_close($conn);
} else {
    echo json_encode(["success" => "0", "message" => "Invalid request method", "login" => []]);
}
?>
