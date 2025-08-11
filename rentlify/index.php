<?php
session_start();
// Check if admin is already logged in
if(isset($_SESSION['admin_logged_in']) && $_SESSION['admin_logged_in'] === true) {
    header("Location: admin_dashboard.php");
    exit();
}

// Hardcoded admin credentials
define('ADMIN_USERNAME', 'admin');
define('ADMIN_PASSWORD', '1234!');

$error_message = "";
if ($_SERVER["REQUEST_METHOD"] == "POST") {
    // Sanitize input
    $username = htmlspecialchars($_POST['username']);
    $password = $_POST['password'];

    // Check against hardcoded credentials
    if ($username === ADMIN_USERNAME && $password === ADMIN_PASSWORD) {
        // Login successful
        $_SESSION['admin_logged_in'] = true;
        $_SESSION['admin_id'] = 1; // Fixed admin ID
        $_SESSION['admin_username'] = ADMIN_USERNAME;

        // Redirect to admin dashboard
        header("Location: admin_dashboard.php");
        exit();
    } else {
        $error_message = "Invalid username or password";
    }
}
?>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Admin Login - Rentlify</title>
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
            display: flex;
            align-items: center;
            justify-content: center;
            height: 100vh;
            margin: 0;
            color: var(--text-color);
        }

        .login-container {
            background: var(--card-bg);
            border-radius: var(--border-radius);
            box-shadow: 0 20px 25px -5px rgba(0, 0, 0, 0.1);
            width: 100%;
            max-width: 400px;
            padding: 2.5rem;
            transition: var(--transition);
        }

        .login-container:hover {
            transform: translateY(-10px);
            box-shadow: 0 30px 35px -5px rgba(0, 0, 0, 0.15);
        }

        .login-header {
            text-align: center;
            margin-bottom: 2rem;
        }

        .login-header h2 {
            color: var(--primary-color);
            font-weight: 700;
            display: flex;
            align-items: center;
            justify-content: center;
            gap: 0.5rem;
        }

        .login-header h2 i {
            font-size: 2rem;
        }

        .form-label {
            font-weight: 500;
            color: var(--text-color);
        }

        .form-control {
            border-radius: 0.5rem;
            padding: 0.75rem 1rem;
            border-color: #e5e7eb;
            transition: var(--transition);
        }

        .form-control:focus {
            border-color: var(--primary-color);
            box-shadow: 0 0 0 0.2rem rgba(67, 56, 202, 0.25);
        }

        .btn-primary {
            background: linear-gradient(90deg, var(--primary-color), var(--secondary-color));
            border: none;
            border-radius: 0.5rem;
            padding: 0.75rem 1rem;
            font-weight: 600;
            transition: var(--transition);
            display: flex;
            align-items: center;
            justify-content: center;
            gap: 0.5rem;
        }

        .btn-primary:hover {
            transform: translateY(-3px);
            box-shadow: 0 10px 15px -3px rgba(67, 56, 202, 0.2);
        }

        .alert {
            border-radius: 0.5rem;
            padding: 0.75rem 1rem;
        }
    </style>
</head>
<body>
    <div class="login-container">
        <div class="login-header">
            <h2>
                <i class="bi bi-building"></i>
                Rentlify Admin
            </h2>
        </div>
        
        <?php if(!empty($error_message)): ?>
            <div class="alert alert-danger" role="alert">
                <i class="bi bi-exclamation-triangle me-2"></i>
                <?php echo $error_message; ?>
            </div>
        <?php endif; ?>
        
        <form method="POST" action="">
            <div class="mb-3">
                <label for="username" class="form-label">Username</label>
                <div class="input-group">
                    <span class="input-group-text bg-light border-end-0">
                        <i class="bi bi-person"></i>
                    </span>
                    <input type="text" class="form-control ps-0" id="username" name="username" required placeholder="Enter your username">
                </div>
            </div>
            <div class="mb-4">
                <label for="password" class="form-label">Password</label>
                <div class="input-group">
                    <span class="input-group-text bg-light border-end-0">
                        <i class="bi bi-lock"></i>
                    </span>
                    <input type="password" class="form-control ps-0" id="password" name="password" required placeholder="Enter your password">
                </div>
            </div>
            <div class="d-grid">
                <button type="submit" class="btn btn-primary">
                    <i class="bi bi-box-arrow-in-right"></i>
                    Login
                </button>
            </div>
        </form>
    </div>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>