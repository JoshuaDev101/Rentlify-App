<?php
// Ensure admin is logged in
if (!isset($_SESSION['admin_logged_in']) || $_SESSION['admin_logged_in'] !== true) {
    header("Location: index.php");
    exit();
}
?>
<!DOCTYPE html>
<html lang="en">
<head>
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
            grid-template-columns: repeat(5, 1fr);
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
                grid-template-columns: repeat(3, 1fr);
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

            .stats-grid {
                grid-template-columns: repeat(2, 1fr);
            }
        }
        .sidebar-divider {
        border-top: 1px solid rgba(255,255,255,0.1);
        margin: 1rem 0;
    }

    .sidebar-menu a.text-danger {
        color: #dc3545 !important;
    }

    .sidebar-menu a.text-danger:hover {
        background-color: rgba(220, 53, 69, 0.1) !important;
    }
    </style>
</head>
<body>
<div class="sidebar">
    <div class="sidebar-logo">
        <i class="bi bi-building"></i> Rentlify Admin
    </div>
    <ul class="sidebar-menu">
        <li>
            <a href="admin_dashboard.php" class="<?php echo (basename($_SERVER['PHP_SELF']) == 'admin_dashboard.php') ? 'active' : ''; ?>">
                <i class="bi bi-speedometer2 me-2"></i>Dashboard
            </a>
        </li>
        <li>
            <a href="landlord_management.php" class="<?php echo (basename($_SERVER['PHP_SELF']) == 'landlord_management.php') ? 'active' : ''; ?>">
                <i class="bi bi-person-check me-2"></i>Landlord Management
            </a>
        </li>
        <li>
            <a href="tenant_management.php" class="<?php echo (basename($_SERVER['PHP_SELF']) == 'tenant_management.php') ? 'active' : ''; ?>">
                <i class="bi bi-people me-2"></i>Tenant Management
            </a>
        </li>
   
        <li>
            <a href="reports.php" class="<?php echo (basename($_SERVER['PHP_SELF']) == 'reports.php') ? 'active' : ''; ?>">
                <i class="bi bi-file-earmark-text me-2"></i>Reports
            </a>
        </li>
    
     
        <li class="sidebar-divider"></li>
       
        <li>
            <a href="admin_logout.php" class="text-danger">
                <i class="bi bi-box-arrow-right me-2"></i>Logout
            </a>
        </li>
    </ul>
</div>
</body>
</html>