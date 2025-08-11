<?php
// Enable detailed error reporting
error_reporting(E_ALL);
ini_set('display_errors', 1);

// For logging errors to a file (useful for production debugging)
// ini_set('log_errors', 1);
// ini_set('error_log', '/path/to/your/logfile.log');  // Specify a path to save the log file

include 'connection.php';  // Make sure this file correctly connects to the database

// Start output buffering (clear any output before setting headers)
ob_start();

$response = [];

if (isset($_GET['landlord_id'])) {
    $landlord_id = $_GET['landlord_id'];

    try {
        // Prepare SQL query to count pending visit requests for the landlord
        $stmt = $conn->prepare(
            "SELECT COUNT(*) as total_visits
             FROM visit_requests vr
             JOIN property_posts pp ON vr.property_id = pp.id
             WHERE pp.landlord_id = ? AND vr.status = 'pending'"
        );

        // Check if query preparation was successful
        if (!$stmt) {
            throw new Exception("Prepare failed: " . $conn->error);
        }

        // Log the landlord ID being used for debugging
        error_log("Landlord ID: " . $landlord_id);  // This logs to the PHP error log or file specified in php.ini

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
            $response['total_visits'] = $row['total_visits'];
        } else {
            $response['status'] = "error";
            $response['message'] = "No data found for landlord ID: " . $landlord_id;
        }

        // Close the statement
        $stmt->close();
    } catch (Exception $e) {
        // Log the error to a file or output for debugging
        error_log("Database error: " . $e->getMessage());  // Logs the error to the PHP error log

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
