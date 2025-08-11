<?php
// Place this at the top of each admin page that requires authentication
session_start();

// Check if admin is logged in
if (!isset($_SESSION['admin_logged_in']) || $_SESSION['admin_logged_in'] !== true) {
    // Not logged in, redirect to login page
    header("Location: index.php");
    exit();
}

// Optional: Add additional checks like session expiration
$inactive = 1800; // 30 minutes
if (isset($_SESSION['last_activity']) && (time() - $_SESSION['last_activity'] > $inactive)) {
    // Last request was more than 30 minutes ago
    session_unset();     // unset $_SESSION variable for the run-time 
    session_destroy();   // destroy session data 
    header("Location: admin.php"); // redirect to login page
    exit();
}

// Update last activity time
$_SESSION['last_activity'] = time();
?>