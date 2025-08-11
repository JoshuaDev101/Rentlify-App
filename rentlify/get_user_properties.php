<?php
header('Content-Type: application/json');

include 'connection.php';

if (!$conn) {
    echo json_encode(["error" => "Database connection failed"]);
    exit();
}

// Debug output
error_log("Database connection successful");

// Updated query to include bedroom, bathroom, area, and year_built columns
// Added WHERE clause to filter for available properties only
$sql = "SELECT id, title, description, location, property_type, price, date_posted, image_path, 
        username, profile_image, bedroom, bathroom, area, year_built 
        FROM property_posts 
        WHERE availability = 'Available'";
error_log("Executing query: " . $sql);

$result = $conn->query($sql);

if (!$result) {
    error_log("Query failed: " . $conn->error);
    echo json_encode(["error" => "Query failed: " . $conn->error]);
    exit();
}

$properties = [];

if ($result->num_rows > 0) {
    error_log("Found " . $result->num_rows . " available properties");
    while ($row = $result->fetch_assoc()) {
        // Add the full URL to the image path
        if (!empty($row["image_path"])) {
            // Change to your actual server URL 
            $serverUrl = "http://10.0.2.2/rentlify/";
            
            $row["image_url"] = $serverUrl . $row["image_path"];
        } else {
            $row["image_url"] = "";
        }

        // Add the full URL to the profile image path (if available)
        if (!empty($row["profile_image"])) {
            $row["profile_image"] = $serverUrl . $row["profile_image"];
        } else {
            $row["profile_image"] = ""; // Default empty if no profile image
        }
        
        // Add debugging for the numeric values
        error_log("Property ID " . $row["id"] . ": Bedroom: " . $row["bedroom"] . 
                 ", Bathroom: " . $row["bathroom"] . ", Area: " . $row["area"]);
        
        $properties[] = $row;
    }
} else {
    error_log("No available properties found");
}

echo json_encode(["properties" => $properties]);
$conn->close();
?>