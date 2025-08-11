<?php
// Add these at the top of manage_property.php
error_reporting(E_ALL);
ini_set('display_errors', 0); // Don't display to client
ini_set('log_errors', 1);     // Log errors instead
ini_set('error_log', 'php_error.log'); // Log file

header("Content-Type: application/json");

// Log the request method and parameters
$requestMethod = $_SERVER['REQUEST_METHOD'];
file_put_contents('debug.log', date('Y-m-d H:i:s') . " - Request method: $requestMethod\n", FILE_APPEND);
file_put_contents('debug.log', date('Y-m-d H:i:s') . " - POST data: " . print_r($_POST, true) . "\n", FILE_APPEND);
file_put_contents('debug.log', date('Y-m-d H:i:s') . " - GET data: " . print_r($_GET, true) . "\n", FILE_APPEND);
file_put_contents('debug.log', date('Y-m-d H:i:s') . " - Raw input: " . file_get_contents('php://input') . "\n", FILE_APPEND);

try {
    include 'connection.php';

    // Get the action and property ID
    $action = '';
    $property_id = 0;
    
    // Check request method and get parameters accordingly
    if ($requestMethod === 'GET') {
        $action = isset($_GET['action']) ? $_GET['action'] : '';
        $property_id = isset($_GET['property_id']) ? intval($_GET['property_id']) : 0;
    } else if ($requestMethod === 'POST') {
        $action = isset($_POST['action']) ? $_POST['action'] : '';
        $property_id = isset($_POST['property_id']) ? intval($_POST['property_id']) : 0;
    }
    
    // Check for JSON input
    $input = file_get_contents('php://input');
    if (!empty($input) && isset($_SERVER['CONTENT_TYPE']) && $_SERVER['CONTENT_TYPE'] === 'application/json') {
        $jsonData = json_decode($input, true);
        if ($jsonData && isset($jsonData['action'])) {
            $action = $jsonData['action'];
        }
        if ($jsonData && isset($jsonData['property_id'])) {
            $property_id = intval($jsonData['property_id']);
        }
    }
    
    file_put_contents('debug.log', date('Y-m-d H:i:s') . " - Action: $action, Property ID: $property_id\n", FILE_APPEND);

    // Validate inputs
    if (empty($action)) {
        echo json_encode([
            "status" => "error",
            "message" => "Action is required"
        ]);
        exit;
    }

    // Process based on action
    switch ($action) {
        case 'delete':
            // Property ID is required for delete
            if ($property_id === 0) {
                echo json_encode([
                    "status" => "error",
                    "message" => "Property ID is required for delete action"
                ]);
                exit;
            }
            
            // Delete the property
            $sql = "DELETE FROM property_posts WHERE id = $property_id";
            file_put_contents('debug.log', date('Y-m-d H:i:s') . " - Executing SQL: $sql\n", FILE_APPEND);
            
            if (mysqli_query($conn, $sql)) {
                echo json_encode([
                    "status" => "success",
                    "message" => "Property deleted successfully"
                ]);
            } else {
                echo json_encode([
                    "status" => "error",
                    "message" => "Error deleting property: " . mysqli_error($conn)
                ]);
                file_put_contents('debug.log', date('Y-m-d H:i:s') . " - MySQL error: " . mysqli_error($conn) . "\n", FILE_APPEND);
            }
            break;
        
        case 'update':
            // Property ID is required for update
            if ($property_id === 0) {
                echo json_encode([
                    "status" => "error",
                    "message" => "Property ID is required for update action"
                ]);
                exit;
            }
            
            // Sanitize inputs
            $title = mysqli_real_escape_string($conn, $_POST['title']);
            $location = mysqli_real_escape_string($conn, $_POST['location']);
            $price = floatval($_POST['price']);
            $description = mysqli_real_escape_string($conn, isset($_POST['description']) ? $_POST['description'] : '');
            $bedrooms = mysqli_real_escape_string($conn, isset($_POST['bedrooms']) ? $_POST['bedrooms'] : '');
            $bathrooms = mysqli_real_escape_string($conn, isset($_POST['bathrooms']) ? $_POST['bathrooms'] : '');
            $area = mysqli_real_escape_string($conn, isset($_POST['area']) ? $_POST['area'] : '');
            $availability = mysqli_real_escape_string($conn, isset($_POST['availability']) ? $_POST['availability'] : 'Available');
            
            // Update query - Fixed column name from property_name to title
            $sql = "UPDATE property_posts SET 
                    title = '$title', 
                    location = '$location', 
                    price = '$price', 
                    description = '$description',
                    bedroom = '$bedrooms',
                    bathroom = '$bathrooms',
                    area = '$area',
                    availability = '$availability'
                    WHERE id = $property_id";
                    
            file_put_contents('debug.log', date('Y-m-d H:i:s') . " - Executing SQL: $sql\n", FILE_APPEND);
            
            if (mysqli_query($conn, $sql)) {
                echo json_encode([
                    "status" => "success",
                    "message" => "Property updated successfully"
                ]);
            } else {
                echo json_encode([
                    "status" => "error",
                    "message" => "Error updating property: " . mysqli_error($conn)
                ]);
                file_put_contents('debug.log', date('Y-m-d H:i:s') . " - MySQL error: " . mysqli_error($conn) . "\n", FILE_APPEND);
            }
            break;

        case 'get':
            // Property ID is required for get
            if ($property_id === 0) {
                echo json_encode([
                    "status" => "error",
                    "message" => "Property ID is required for get action"
                ]);
                exit;
            }
            
            // Get property details - Fixed query to use the correct column name 'title'
            $sql = "SELECT 
                    title, 
                    location, 
                    price, 
                    description,
                    bedroom as bedrooms,
                    bathroom as bathrooms,
                    area,
                    availability
                    FROM property_posts 
                    WHERE id = $property_id";
                    
            file_put_contents('debug.log', date('Y-m-d H:i:s') . " - Executing SQL: $sql\n", FILE_APPEND);
            
            $result = mysqli_query($conn, $sql);
            
            if ($result && mysqli_num_rows($result) > 0) {
                $property = mysqli_fetch_assoc($result);
                echo json_encode([
                    "status" => "success",
                    "property" => $property
                ]);
            } else {
                echo json_encode([
                    "status" => "error",
                    "message" => "Property not found or database error: " . mysqli_error($conn)
                ]);
                file_put_contents('debug.log', date('Y-m-d H:i:s') . " - MySQL error or no property found: " . mysqli_error($conn) . "\n", FILE_APPEND);
            }
            break;
            
        default:
            echo json_encode([
                "status" => "error",
                "message" => "Invalid action: $action"
            ]);
    }

    // Close the database connection
    mysqli_close($conn);
    
} catch (Exception $e) {
    file_put_contents('debug.log', date('Y-m-d H:i:s') . " - Exception: " . $e->getMessage() . "\n", FILE_APPEND);
    echo json_encode([
        "status" => "error",
        "message" => "Server error: " . $e->getMessage()
    ]);
}
?>