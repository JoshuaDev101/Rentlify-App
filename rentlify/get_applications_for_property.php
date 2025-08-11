<?php
include 'connection.php';

$property_id = $_POST['property_id'];
$sql = "SELECT a.id, t.name, a.status FROM applications a JOIN tenants t ON a.tenant_id = t.id WHERE a.property_id = ?";
$stmt = $conn->prepare($sql);
$stmt->bind_param("i", $property_id);
$stmt->execute();
$result = $stmt->get_result();

$applications = array();
while($row = $result->fetch_assoc()) {
    $applications[] = $row;
}
echo json_encode($applications);
?>