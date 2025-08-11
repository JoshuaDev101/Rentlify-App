<?php
include 'connection.php';

$tenant_id = $_POST['tenant_id'];
$amount = $_POST['amount'];
$type = $_POST['type'];
$date = date('Y-m-d H:i:s');

$sql = "INSERT INTO payments (tenant_id, amount, type, date) VALUES (?, ?, ?, ?)";
$stmt = $conn->prepare($sql);
$stmt->bind_param("idss", $tenant_id, $amount, $type, $date);

if ($stmt->execute()) {
    echo "Success";
} else {
    echo "Error";
}
?>