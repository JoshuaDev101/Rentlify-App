<?php
    include 'connection.php';

    $landlord_id = $_POST['landlord_id'];
    $username = $_POST['username'];
    $title = $_POST['title'];
    $description = $_POST['description'];
    $location = $_POST['location'];
    $property_type = $_POST['property_type'];
    $price = $_POST['price'];

    // Get the new fields
    $bedroom = $_POST['bedroom'];
    $bathroom = $_POST['bathroom'];
    $area = $_POST['area'];
    $year_built = isset($_POST['year_built']) ? $_POST['year_built'] : null;

    // Get availability dates (comma-separated)
    $available_dates = isset($_POST['available_dates']) ? $_POST['available_dates'] : '';

    // Initialize image_path
    $image_path = '';

    // Handle image upload if an image is provided
    if (!empty($_POST['image'])) {
        $imgData = base64_decode($_POST['image']);
        $fileName = time() . '.jpg';
        $filePath = 'uploads/property_images/' . $fileName;
        file_put_contents($filePath, $imgData);
        $image_path = $filePath;
    }

    // Fetch the profile_image from tableuser for the landlord_id
    $sql_user = "SELECT profile_image FROM tableuser WHERE id = ?";
    $stmt_user = $conn->prepare($sql_user);
    $stmt_user->bind_param("i", $landlord_id);
    $stmt_user->execute();
    $stmt_user->bind_result($profile_image);
    $stmt_user->fetch();
    $stmt_user->close();

    // If profile_image is found for the landlord, use it; otherwise, use a default value
    if ($profile_image) {
        // Profile image exists for the landlord, use it
        $profile_image_path = $profile_image;
    } else {
        // If no profile image exists, use a default placeholder
        $profile_image_path = 'uploads/user_profiles/default_profile.jpg';  // Set a default profile image if none exists
    }

    // Start transaction
    $conn->begin_transaction();

    try {
        // Insert the property details along with the landlord_id, username, and profile_image into the property_posts table
        $sql = "INSERT INTO property_posts (landlord_id, username, title, description, location, bedroom, bathroom, area, property_type, price, image_path, profile_image, year_built) 
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        $stmt = $conn->prepare($sql);
        $stmt->bind_param("issssssssdsss", $landlord_id, $username, $title, $description, $location, $bedroom, $bathroom, $area, $property_type, $price, $image_path, $profile_image_path, $year_built);
        // Execute the query
        $stmt->execute();

        // Get the ID of the newly inserted property
        $property_id = $conn->insert_id;

        // If there are availability dates, insert them into the property_availability table
        if (!empty($available_dates)) {
            // Split the comma-separated dates
            $dates_array = explode(',', $available_dates);

            // Prepare statement for inserting dates
            $date_stmt = $conn->prepare("INSERT INTO property_availability (property_id, available_date) VALUES (?, ?)");
            $date_stmt->bind_param("is", $property_id, $date);

            // Insert each date
            foreach ($dates_array as $date) {
                $date_stmt->execute();
            }

            $date_stmt->close();
        }

        // Commit transaction
        $conn->commit();

        echo json_encode(["success" => "Property posted successfully!", "property_id" => $property_id]);

    } catch (Exception $e) {
        // Rollback transaction on error
        $conn->rollback();
        echo json_encode(["error" => "Failed to post property. Error: " . $e->getMessage()]);
    }

    $stmt->close();
    $conn->close();
    ?>