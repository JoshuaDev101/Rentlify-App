<?php
session_start();

// Check if admin is logged in
if (!isset($_SESSION['admin_logged_in']) || $_SESSION['admin_logged_in'] !== true) {
    // If not logged in, redirect to login page
    header("Location: admin.php");
    exit();
}

require 'connection.php';

// Fetch and categorize landlords
$sqlVerifiedLandlords = "SELECT * FROM tableuser WHERE role = 'landlord' AND status = 'Verified'";
$sqlPendingLandlords = "SELECT * FROM tableuser WHERE role = 'landlord' AND status = 'Pending'";
$sqlRejectedLandlords = "SELECT * FROM tableuser WHERE role = 'landlord' AND status = 'Rejected'";

$resultVerifiedLandlords = mysqli_query($conn, $sqlVerifiedLandlords);
$resultPendingLandlords = mysqli_query($conn, $sqlPendingLandlords);
$resultRejectedLandlords = mysqli_query($conn, $sqlRejectedLandlords);

// Count statistics
$totalLandlords = mysqli_num_rows(mysqli_query($conn, "SELECT * FROM tableuser WHERE role = 'landlord'"));
$verifiedLandlords = mysqli_num_rows(mysqli_query($conn, "SELECT * FROM tableuser WHERE role = 'landlord' AND status = 'Verified'"));
$pendingLandlords = mysqli_num_rows(mysqli_query($conn, "SELECT * FROM tableuser WHERE role = 'landlord' AND status = 'Pending'"));
$rejectedLandlords = mysqli_num_rows(mysqli_query($conn, "SELECT * FROM tableuser WHERE role = 'landlord' AND status = 'Rejected'"));
?>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Landlord Management - Rentlify Admin</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.1/font/bootstrap-icons.css" rel="stylesheet">
    <link href="https://fonts.googleapis.com/css2?family=Poppins:wght@300;400;500;600;700&display=swap" rel="stylesheet">
    <style>
        :root {
            --primary-color: #4338ca;
            --secondary-color: #6366f1;
            --background-color: #f4f4f7;
            --card-bg: #ffffff;
            --text-color: #1f2937;
            --border-radius: 1rem;
            --transition: all 0.3s ease;
        }

        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
        }

        body {
            font-family: 'Poppins', sans-serif;
            background-color: var(--background-color);
            color: var(--text-color);
            line-height: 1.6;
        }

        .sidebar {
            height: 100vh;
            background: linear-gradient(135deg, var(--primary-color), var(--secondary-color));
            position: fixed;
            left: 0;
            top: 0;
            width: 250px;
            padding: 2rem 0;
            transition: var(--transition);
            z-index: 1000;
        }

        .sidebar-logo {
            text-align: center;
            color: white;
            font-size: 1.5rem;
            font-weight: 700;
            margin-bottom: 2rem;
        }

        .sidebar-menu {
            list-style: none;
        }

        .sidebar-menu li {
            margin-bottom: 0.5rem;
        }

        .sidebar-menu a {
            color: rgba(255,255,255,0.7);
            text-decoration: none;
            display: block;
            padding: 0.75rem 1.5rem;
            transition: var(--transition);
            border-radius: 0.5rem;
        }

        .sidebar-menu a:hover, .sidebar-menu a.active {
            background-color: rgba(255,255,255,0.1);
            color: white;
        }

        .main-content {
            margin-left: 250px;
            padding: 2rem;
            transition: var(--transition);
        }

        .stats-grid {
            display: grid;
            grid-template-columns: repeat(4, 1fr);
            gap: 1.5rem;
            margin-bottom: 2rem;
        }

        .stat-card {
            background: var(--card-bg);
            border-radius: var(--border-radius);
            padding: 1.5rem;
            text-align: center;
            box-shadow: 0 10px 15px -3px rgba(0, 0, 0, 0.1);
            position: relative;
            overflow: hidden;
            transition: var(--transition);
            cursor: pointer;
        }

        .stat-card:hover {
            transform: translateY(-10px);
            box-shadow: 0 20px 25px -5px rgba(0, 0, 0, 0.1);
        }

        .stat-card::before {
            content: '';
            position: absolute;
            top: 0;
            left: 0;
            width: 100%;
            height: 5px;
            background: linear-gradient(90deg, var(--primary-color), var(--secondary-color));
        }

        .stat-icon {
            font-size: 2.5rem;
            margin-bottom: 1rem;
            color: var(--primary-color);
        }

        .stat-number {
            font-size: 2rem;
            font-weight: 700;
            color: var(--primary-color);
        }

        .user-table {
            background: var(--card-bg);
            border-radius: var(--border-radius);
            box-shadow: 0 10px 15px -3px rgba(0, 0, 0, 0.1);
            margin-bottom: 2rem;
            overflow: hidden;
        }

        .table-header {
            background: linear-gradient(90deg, var(--primary-color), var(--secondary-color));
            color: white;
            padding: 1rem;
            display: flex;
            justify-content: space-between;
            align-items: center;
        }

        .table-header h3 {
            margin: 0;
            display: flex;
            align-items: center;
            gap: 0.5rem;
        }

        .user-table .table {
            margin-bottom: 0;
        }

        .user-table .table th {
            background-color: #f8f9fa;
            color: var(--text-color);
            font-weight: 600;
        }

        .btn-action {
            display: inline-flex;
            align-items: center;
            gap: 0.25rem;
            transition: var(--transition);
        }

        .btn-action:hover {
            transform: scale(1.05);
        }

        @media (max-width: 1200px) {
            .stats-grid {
                grid-template-columns: repeat(2, 1fr);
            }
        }

        @media (max-width: 768px) {
            .sidebar {
                width: 0;
                overflow: hidden;
            }

            .main-content {
                margin-left: 0;
            }
        }
    </style>
</head>
<body>
    <!-- Sidebar -->
    <?php include 'sidebar.php'; ?>

    <!-- Main Content -->
    <div class="main-content">
        <div class="container-fluid">
            <h1 class="mb-4">Landlord Management</h1>

            <!-- Statistics Grid -->
            <div class="stats-grid">
                <div class="stat-card">
                    <div class="stat-icon"><i class="bi bi-person-check"></i></div>
                    <div class="stat-number"><?php echo $totalLandlords; ?></div>
                    <h4 class="h6 text-muted">Total Landlords</h4>
                </div>
                <div class="stat-card">
                    <div class="stat-icon"><i class="bi bi-shield-check"></i></div>
                    <div class="stat-number"><?php echo $verifiedLandlords; ?></div>
                    <h4 class="h6 text-muted">Verified Landlords</h4>
                </div>
                <div class="stat-card">
                    <div class="stat-icon"><i class="bi bi-clock"></i></div>
                    <div class="stat-number"><?php echo $pendingLandlords; ?></div>
                    <h4 class="h6 text-muted">Pending Landlords</h4>
                </div>
                <div class="stat-card">
                    <div class="stat-icon"><i class="bi bi-x-circle"></i></div>
                    <div class="stat-number"><?php echo $rejectedLandlords; ?></div>
                    <h4 class="h6 text-muted">Rejected Landlords</h4>
                </div>
            </div>

            <!-- Landlords Sections -->
            <div class="row">
                <!-- Pending Landlords Section -->
                <div class="col-md-12 mb-4">
                    <div class="user-table">
                        <div class="table-header">
                            <h3><i class="bi bi-clock me-2"></i>Pending Landlords</h3>
                            <button class="btn btn-sm btn-light">
                                <i class="bi bi-filter me-1"></i>Filter
                            </button>
                        </div>
                        <div class="table-responsive">
                            <table class="table table-hover mb-0">
                                <thead>
                                    <tr>
                                        <th>ID</th>
                                        <th>Username</th>
                                        <th>Email</th>
                                        <th>Actions</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <?php 
                                    // Reset the pointer to beginning of result set
                                    mysqli_data_seek($resultPendingLandlords, 0);
                                    while ($row = mysqli_fetch_assoc($resultPendingLandlords)) { 
                                        // Check for uploaded files
                                        $landlordId = $row['id'];
                                        $fileQuery = "SELECT * FROM landlord_files WHERE userid = '$landlordId'";
                                        $fileResult = mysqli_query($conn, $fileQuery);
                                        $hasFiles = mysqli_num_rows($fileResult) > 0;
                                    ?>
                                        <tr>
                                            <td><?php echo $row['id']; ?></td>
                                            <td><?php echo $row['username']; ?></td>
                                            <td><?php echo $row['email'] ?? 'N/A'; ?></td>
                                            <td>
                                                <?php if ($hasFiles) { ?>
                                                    <a href="view_uploaded_files.php?userid=<?php echo $row['id']; ?>" class="btn btn-sm btn-outline-primary btn-action me-1">
                                                        <i class="bi bi-file-earmark-text me-1"></i>View Files
                                                    </a>
                                                <?php } ?>
                                                <a href="verify_user.php?id=<?php echo $row['id']; ?>&action=approve" class="btn btn-sm btn-success btn-action me-1">
                                                    <i class="bi bi-check-circle me-1"></i>Approve
                                                </a>
                                                <a href="verify_user.php?id=<?php echo $row['id']; ?>&action=reject" class="btn btn-sm btn-danger btn-action">
                                                    <i class="bi bi-x-circle me-1"></i>Reject
                                                </a>
                                            </td>
                                        </tr>
                                    <?php } ?>
                                </tbody>
                            </table>
                        </div>
                    </div>
                </div>

                <!-- Verified Landlords Section -->
                <div class="col-md-12 mb-4">
                    <div class="user-table">
                        <div class="table-header">
                            <h3><i class="bi bi-shield-check me-2"></i>Verified Landlords</h3>
                            <button class="btn btn-sm btn-light">
                                <i class="bi bi-filter me-1"></i>Filter
                            </button>
                        </div>
                        <div class="table-responsive">
                            <table class="table table-hover mb-0">
                                <thead>
                                    <tr>
                                        <th>ID</th>
                                        <th>Username</th>
                                        <th>Email</th>
                                        <th>Actions</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <?php 
                                    // Reset the pointer to beginning of result set
                                    mysqli_data_seek($resultVerifiedLandlords, 0);
                                    while ($row = mysqli_fetch_assoc($resultVerifiedLandlords)) { 
                                        // Check for uploaded files
                                        $landlordId = $row['id'];
                                        $fileQuery = "SELECT * FROM landlord_files WHERE userid = '$landlordId'";
                                        $fileResult = mysqli_query($conn, $fileQuery);
                                        $hasFiles = mysqli_num_rows($fileResult) > 0;
                                    ?>
                                        <tr>
                                            <td><?php echo $row['id']; ?></td>
                                            <td><?php echo $row['username']; ?></td>
                                            <td><?php echo $row['email'] ?? 'N/A'; ?></td>
                                            <td>
                                                <?php if ($hasFiles) { ?>
                                                    <a href="view_uploaded_files.php?userid=<?php echo $row['id']; ?>" class="btn btn-sm btn-outline-primary btn-action me-1">
                                                        <i class="bi bi-file-earmark-text me-1"></i>View Files
                                                    </a>
                                                <?php } ?>
                                                <a href="delete_user.php?id=<?php echo $row['id']; ?>" class="btn btn-sm btn-outline-dark btn-action">
                                                    <i class="bi bi-trash me-1"></i>Delete
                                                </a>
                                            </td>
                                        </tr>
                                    <?php } ?>
                                </tbody>
                            </table>
                        </div>
                    </div>
                </div>

                <!-- Rejected Landlords Section -->
                <div class="col-md-12">
                    <div class="user-table">
                        <div class="table-header">
                            <h3><i class="bi bi-x-circle me-2"></i>Rejected Landlords</h3>
                            <button class="btn btn-sm btn-light">
                                <i class="bi bi-filter me-1"></i>Filter
                            </button>
                        </div>
                        <div class="table-responsive">
                            <table class="table table-hover mb-0">
                                <thead>
                                    <tr>
                                        <th>ID</th>
                                        <th>Username</th>
                                        <th>Email</th>
                                        <th>Actions</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <?php 
                                    // Reset the pointer to beginning of result set
                                    mysqli_data_seek($resultRejectedLandlords, 0);
                                    while ($row = mysqli_fetch_assoc($resultRejectedLandlords)) { 
                                        // Check for uploaded files
                                        $landlordId = $row['id'];
                                        $fileQuery = "SELECT * FROM landlord_files WHERE userid = '$landlordId'";
                                        $fileResult = mysqli_query($conn, $fileQuery);
                                        $hasFiles = mysqli_num_rows($fileResult) > 0;
                                    ?>
                                        <tr>
                                            <td><?php echo $row['id']; ?></td>
                                            <td><?php echo $row['username']; ?></td>
                                            <td><?php echo $row['email'] ?? 'N/A'; ?></td>
                                            <td>
                                                <?php if ($hasFiles) { ?>
                                                    <a href="view_uploaded_files.php?userid=<?php echo $row['id']; ?>" class="btn btn-sm btn-outline-primary btn-action me-1">
                                                        <i class="bi bi-file-earmark-text me-1"></i>View Files
                                                    </a>
                                                <?php } ?>
                                                <a href="verify_user.php?id=<?php echo $row['id']; ?>&action=approve" class="btn btn-sm btn-success btn-action me-1">
                                                    <i class="bi bi-check-circle me-1"></i>Approve
                                                </a>
                                                <a href="delete_user.php?id=<?php echo $row['id']; ?>" class="btn btn-sm btn-outline-dark btn-action">
                                                    <i class="bi bi-trash me-1"></i>Delete
                                                </a>
                                            </td>
                                        </tr>
                                    <?php } ?>
                                </tbody>
                            </table>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>