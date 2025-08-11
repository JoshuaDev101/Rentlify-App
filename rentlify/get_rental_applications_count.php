<?php
// Enable detailed error reporting
error_reporting(E_ALL);
ini_set('display_errors', 1);

// For logging errors to a file (uncomment for production)
// ini_set('log_errors', 1);
// ini_set('error_log', '/path/to/your/logfile.log');

include 'connection.php';  // Include database connection

// Start output buffering
ob_start();

$response = [];

if (isset($_GET['landlord_id'])) {
    $landlord_id = $_GET['landlord_id'];

    try {
        // Prepare SQL query to count pending rental applications for the landlord
        $stmt = $conn->prepare(
            "SELECT COUNT(*) as total_applications
             FROM rental_applications ra
             JOIN property_posts p ON p.id = ra.property_id
             WHERE p.landlord_id = ? AND ra.status = 'pending'"
        );

        // Check if query preparation was successful
        if (!$stmt) {
            throw new Exception("Prepare failed: " . $conn->error);
        }

        // Log the landlord ID being used for debugging
        error_log("Landlord ID for applications count: " . $landlord_id);

        // Bind the landlord_id parameter to the query
        $stmt->bind_param("i", $landlord_id);

        // Execute the query
        if (!$stmt->execute()) {
            throw new Exception("Execute failed: " . $stmt->error);
        }

        // Get the result of the query
        $result = $stmt->get_result();
        
        // Fetch the result and send it in the response
        if ($row = $result->fetch_assoc()) {
            $response['status'] = "success";
            $response['total_applications'] = $row['total_applications'];
        } else {
            $response['status'] = "error";
            $response['message'] = "No data found for landlord ID: " . $landlord_id;
        }

        // Close the statement
        $stmt->close();
    } catch (Exception $e) {
        // Log the error
        error_log("Database error: " . $e->getMessage());

        // Send an error response
        $response['status'] = "error";
        $response['message'] = "Database error occurred. Please contact support. " . $e->getMessage();
    }
} else {
    // If landlord_id is missing in the request
    $response['status'] = "error";
    $response['message'] = "Missing landlord_id in the request.";
}

// Clear any output and return the response as JSON
ob_end_clean();
header('Content-Type: application/json');
echo json_encode($response);

// Close the database connection
$conn->close();
?>