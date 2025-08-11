<?php
require 'connection.php';

if ($_SERVER['REQUEST_METHOD'] == 'POST') {
    if (!isset($_FILES['file']) || !isset($_POST['userid'])) {
        echo json_encode(["success" => 0, "message" => "Missing file or user ID"]);
        exit();
    }

    $userid = $_POST['userid'];
    $file = $_FILES['file'];
    $uploadDir = "uploads/";  // Folder where files will be saved

    if (!is_dir($uploadDir)) {
        mkdir($uploadDir, 0777, true);
    }

    $fileName = basename($file['name']);
    $filePath = $uploadDir . $fileName;

    // Move the uploaded file to the uploads folder
    if (move_uploaded_file($file['tmp_name'], $filePath)) {
        // Store file path in database
        $stmt = $conn->prepare("INSERT INTO landlord_files (userid, file_name, file_path, status) VALUES (?, ?, ?, 'Pending')");
        $stmt->bind_param("iss", $userid, $fileName, $filePath);

        if ($stmt->execute()) {
            echo json_encode(["success" => 1, "message" => "File uploaded successfully", "file_path" => $filePath]);
        } else {
            echo json_encode(["success" => 0, "message" => "Database error"]);
        }
    } else {
        echo json_encode(["success" => 0, "message" => "Failed to upload file"]);
    }

    $stmt->close();
    $conn->close();
} else {
    echo json_encode(["success" => 0, "message" => "Invalid request"]);
}
?>
