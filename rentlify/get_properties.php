<?php
// Add these at the top
error_reporting(E_ALL);
ini_set('display_errors', 1); // Display errors to the client for debugging (change to 0 in production)
ini_set('log_errors', 1);     
ini_set('error_log', 'php_error.log');

// Make sure content type is set before any output
header("Content-Type: application/json");

// Log request details for debugging
file_put_contents('debug.log', date('Y-m-d H:i:s') . " - GET properties request: " . 
    print_r($_GET, true) . "\n", FILE_APPEND);

try {
    // Include connection file
    include 'connection.php';

    // Check if connection was successful
    if (!$conn) {
        throw new Exception("Database connection failed: " . mysqli_connect_error());
    }
    
    // Get the landlord ID from the request
    $landlord_id = isset($_GET['landlord_id']) ? intval($_GET['landlord_id']) : 0;
    file_put_contents('debug.log', date('Y-m-d H:i:s') . " - Filtering for landlord_id: $landlord_id\n", FILE_APPEND);

    // If no landlord_id provided, return error
    if ($landlord_id === 0) {
        echo json_encode([
            "status" => "error",
            "message" => "Landlord ID required"
        ]);
        exit;
    }

    // Define base URL for images
    $image_base_url = "http://10.0.2.2/rentlify/";

    // Use prepared statements for property posts query
    $sql = "SELECT id, title as property_name, location, price as rent, image_path, 
                   bedroom as bedroom, bathroom as bathroom, area, availability
            FROM property_posts
            WHERE landlord_id = ?";
    
    file_put_contents('debug.log', date('Y-m-d H:i:s') . " - SQL: $sql\n", FILE_APPEND);

    $stmt = mysqli_prepare($conn, $sql);
    
    if (!$stmt) {
        throw new Exception("Prepared statement failed: " . mysqli_error($conn));
    }

    // Bind the landlord_id parameter to the query
    mysqli_stmt_bind_param($stmt, "i", $landlord_id);

    // Execute the query
    if (!mysqli_stmt_execute($stmt)) {
        throw new Exception("Query execution failed: " . mysqli_error($conn));
    }

    // Get result
    $result = mysqli_stmt_get_result($stmt);

    $properties = array();

    if (mysqli_num_rows($result) > 0) {
        while ($row = mysqli_fetch_assoc($result)) {
            // Ensure the image_path is a full URL
            $image_url = $image_base_url . $row['image_path'];
            
            $properties[] = array(
                "id" => $row['id'],
                "property_name" => $row['property_name'],
                "location" => $row['location'],
                "rent" => $row['rent'],
                "image_path" => $image_url,
                "bedroom" => $row['bedroom'],
                "bathroom" => $row['bathroom'],
                "area" => $row['area'],
                "availability" => $row['availability']
            );
        }

        // Get total income for landlord
        $sql_income = "SELECT SUM(price) as total_income FROM property_posts WHERE landlord_id = ?";
        $stmt_income = mysqli_prepare($conn, $sql_income);

        if (!$stmt_income) {
            throw new Exception("Income query prepared statement failed: " . mysqli_error($conn));
        }

        // Bind the landlord_id parameter to the income query
        mysqli_stmt_bind_param($stmt_income, "i", $landlord_id);

        // Execute the income query
        if (!mysqli_stmt_execute($stmt_income)) {
            throw new Exception("Income query execution failed: " . mysqli_error($conn));
        }

        // Get the income result
        $income_result = mysqli_stmt_get_result($stmt_income);
        $income_data = mysqli_fetch_assoc($income_result);

        // Prepare the response
        $response = [
            "status" => "success",
            "total_properties" => count($properties),
            "total_income" => $income_data['total_income'] ? $income_data['total_income'] : 0,
            "properties" => $properties
        ];

        file_put_contents('debug.log', date('Y-m-d H:i:s') . " - Success! Found " . count($properties) . " properties\n", FILE_APPEND);
        echo json_encode($response);
    } else {
        // Return empty result set if no properties found
        file_put_contents('debug.log', date('Y-m-d H:i:s') . " - No properties found for landlord ID: $landlord_id\n", FILE_APPEND);
        echo json_encode([
            "status" => "success",
            "total_properties" => 0,
            "total_income" => 0,
            "properties" => []
        ]);
    }

    // Close the database connection
    mysqli_stmt_close($stmt);
    mysqli_stmt_close($stmt_income);
    mysqli_close($conn);

} catch (Exception $e) {
    // Log the error message
    file_put_contents('debug.log', date('Y-m-d H:i:s') . " - ERROR: " . $e->getMessage() . "\n", FILE_APPEND);
    
    // Return a proper JSON error response
    echo json_encode([
        "status" => "error",
        "message" => "Server error occurred. Please try again later."
    ]);
}
?>
