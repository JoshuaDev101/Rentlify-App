<?php
require 'connection.php';

if (!isset($_GET['id'])) {
    die("Missing file ID.");
}

$fileId = $_GET['id'];

// Fetch file from the database
$stmt = $conn->prepare("SELECT file_name, file_data FROM landlord_files WHERE id = ?");
$stmt->bind_param("i", $fileId);
$stmt->execute();
$stmt->store_result();
$stmt->bind_result($fileName, $fileData);
$stmt->fetch();

if ($stmt->num_rows > 0) {
    // Send correct headers
    header("Content-Type: application/octet-stream");
    header("Content-Disposition: attachment; filename=" . $fileName);
    echo $fileData;
} else {
    echo "File not found.";
}

$stmt->close();
$conn->close();
?>
