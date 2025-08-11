<?php
include 'connection.php';

$response = array();

if (isset($_POST['property_id'])) {
    $propertyId = $_POST['property_id'];

    $stmt = $conn->prepare("SELECT available_date FROM property_availability WHERE property_id = ?");
    $stmt->bind_param("i", $propertyId);
    $stmt->execute();
    $result = $stmt->get_result();

    $dates = array();
    while ($row = $result->fetch_assoc()) {
        $dates[] = $row['available_date'];
    }

    $response['status'] = "success";
    $response['available_dates'] = $dates;
} else {
    $response['status'] = "error";
    $response['message'] = "Missing property_id";
}

echo json_encode($response);
$conn->close();
?>
