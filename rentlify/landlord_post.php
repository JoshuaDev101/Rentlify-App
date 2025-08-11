<?php include 'connection.php'; ?>

<!DOCTYPE html>
<html>
<head>
    <title>Post Property</title>
</head>
<body>
    <h2>Post New Property</h2>
    <form action="submit_post.php" method="post" enctype="multipart/form-data">
        <input type="hidden" name="landlord_id" value="<!-- YOUR LOGGED-IN LANDLORD ID HERE -->">
        <label>Title:</label><br>
        <input type="text" name="title" required><br><br>

        <label>Description:</label><br>
        <textarea name="description" required></textarea><br><br>

        <label>Location:</label><br>
        <input type="text" name="location" required><br><br>

        <label>Type:</label><br>
        <select name="property_type" required>
            <option>House</option>
            <option>Apartment</option>
            <option>Studio</option>
        </select><br><br>

        <label>Price:</label><br>
        <input type="number" name="price" step="0.01" required><br><br>

        <label>Upload Image (optional):</label><br>
        <input type="file" name="image"><br><br>

        <button type="submit">Post</button>
    </form>
</body>
</html>
