<?php
require 'connection.php';

// Check if userid is set in the URL
if (!isset($_GET['userid'])) {
    die("No user specified");
}

$userid = intval($_GET['userid']);

// Fetch landlord details
$queryUser = "SELECT fullname, location, property_type, phone FROM landlord_details WHERE userid = ?";
$stmtUser = mysqli_prepare($conn, $queryUser);
mysqli_stmt_bind_param($stmtUser, "i", $userid);
mysqli_stmt_execute($stmtUser);
$resultUser = mysqli_stmt_get_result($stmtUser);
$landlordDetails = mysqli_fetch_assoc($resultUser);

// Fetch detailed landlord info
$queryDetailedInfo = "SELECT * FROM landlord_details WHERE userid = ?";
$stmtDetailedInfo = mysqli_prepare($conn, $queryDetailedInfo);
mysqli_stmt_bind_param($stmtDetailedInfo, "i", $userid);
mysqli_stmt_execute($stmtDetailedInfo);
$resultDetailedInfo = mysqli_stmt_get_result($stmtDetailedInfo);
$detailedInfo = mysqli_fetch_assoc($resultDetailedInfo);

// Fetch user status from tableuser
$queryStatus = "SELECT status, status FROM tableuser WHERE id = ?";
$stmtStatus = mysqli_prepare($conn, $queryStatus);
mysqli_stmt_bind_param($stmtStatus, "i", $userid);
mysqli_stmt_execute($stmtStatus);
$resultStatus = mysqli_stmt_get_result($stmtStatus);
$userStatus = mysqli_fetch_assoc($resultStatus);

// Fetch uploaded files for this landlord, sorted by most recent first
$queryFiles = "SELECT * FROM landlord_files WHERE userid = ? ORDER BY upload_date DESC";
$stmtFiles = mysqli_prepare($conn, $queryFiles);
mysqli_stmt_bind_param($stmtFiles, "i", $userid);
mysqli_stmt_execute($stmtFiles);
$resultFiles = mysqli_stmt_get_result($stmtFiles);
?>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Landlord Verification</title>
    <link rel="icon" type="image/png" href="Rentlify_icon.png">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.0/font/bootstrap-icons.css" rel="stylesheet">
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@300;400;500;600;700&display=swap" rel="stylesheet">
    <style>
        :root {
            --clr-primary: #4a6cf7;
            --clr-secondary: #6a7aed;
            --clr-background: #f4f7ff;
            --clr-text-dark: #2c3e50;
            --clr-text-light: #6a7aed;
            --clr-white: #ffffff;
            --clr-gray: #f5f5f5;
            --clr-success: #28a745;
            --clr-danger: #dc3545;
            --clr-warning: #ffc107;
        }

        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
        }

        body {
            font-family: 'Inter', sans-serif;
            background-color: var(--clr-background);
            color: var(--clr-text-dark);
            line-height: 1.4;
            font-size: 0.9rem;
        }

        .verification-container {
            max-width: 1800px; /* Increased max-width */
            margin: 1rem auto; /* Increased margin */
            padding: 0 1rem; /* Increased padding */
        }

        /* Back Button At Top */
        .back-button-container {
            position: sticky;
            top: 10px;
            z-index: 100;
            margin-bottom: 15px; /* Increased margin */
        }
        
        .btn-back {
            background: var(--clr-primary);
            color: var(--clr-white);
            border: none;
            transition: all 0.3s ease;
            font-weight: 600;
            padding: 8px 15px; /* Increased padding */
            border-radius: 5px;
            box-shadow: 0 2px 5px rgba(0,0,0,0.1);
            font-size: 0.95rem; /* Increased font size */
        }

        .btn-back:hover {
            background: var(--clr-secondary);
            color: var(--clr-white);
        }

        .profile-card, .documents-section, .verification-form-section {
            background: var(--clr-white);
            border-radius: 12px; /* Increased border radius */
            box-shadow: 0 5px 15px rgba(75, 108, 247, 0.1);
            height: 100%;
            margin-bottom: 1.5rem; /* Increased margin */
        }

        .profile-header {
            background: linear-gradient(135deg, var(--clr-primary), var(--clr-secondary));
            color: var(--clr-white);
            padding: 1.5rem; /* Increased padding */
            text-align: center;
            position: relative;
            border-radius: 12px 12px 0 0; /* Increased border radius */
        }

        .profile-header h2 {
            font-size: 1.4rem; /* Increased font size */
            margin-bottom: 0.75rem; /* Increased margin */
        }

        .profile-avatar {
            width: 90px; /* Increased size */
            height: 90px; /* Increased size */
            border-radius: 50%;
            border: 3px solid var(--clr-white);
            background-color: rgba(255,255,255,0.2);
            display: flex;
            align-items: center;
            justify-content: center;
            margin: 0 auto 0.75rem; /* Increased margin */
        }

        .profile-avatar i {
            font-size: 3rem; /* Increased size */
            color: var(--clr-white);
        }

        .profile-details {
            padding: 1.25rem; /* Increased padding */
        }

        .detail-item {
            display: flex;
            align-items: center;
            margin-bottom: 1rem; /* Increased margin */
            background-color: var(--clr-gray);
            padding: 0.75rem; /* Increased padding */
            border-radius: 8px; /* Increased border radius */
        }

        .detail-item i {
            color: var(--clr-primary);
            margin-right: 0.75rem; /* Increased margin */
            font-size: 1.25rem; /* Increased font size */
        }

        .detail-item h6 {
            font-size: 0.85rem; /* Increased font size */
            margin-bottom: 0.2rem; /* Increased margin */
        }

        .detail-item p {
            margin-bottom: 0;
            font-size: 1rem; /* Increased font size */
        }

        /* Combined Section Styles */
        .combined-section {
            background: var(--clr-white);
            border-radius: 12px; /* Increased border radius */
            box-shadow: 0 5px 15px rgba(75, 108, 247, 0.1);
            margin-bottom: 1.5rem; /* Increased margin */
        }

        .combined-section .section-header {
            background: linear-gradient(135deg, var(--clr-primary), var(--clr-secondary));
            color: var(--clr-white);
            padding: 1.25rem; /* Increased padding */
            border-radius: 12px 12px 0 0; /* Increased border radius */
        }

        .combined-section .section-header h2 {
            font-size: 1.3rem; /* Increased font size */
            margin-bottom: 0;
        }

        .combined-section .section-body {
            padding: 1.25rem; /* Increased padding */
        }

        .info-grid {
            display: grid;
            grid-template-columns: 1fr 1fr;
            gap: 1rem; /* Increased gap */
        }

        .info-group {
            margin-bottom: 1rem; /* Increased margin */
        }

        .info-label {
            font-weight: 600;
            font-size: 0.85rem; /* Increased font size */
            color: var(--clr-text-dark);
            margin-bottom: 0.3rem; /* Increased margin */
            display: flex;
            align-items: center;
        }

        .info-label i {
            color: var(--clr-primary);
            margin-right: 0.5rem; /* Increased margin */
            font-size: 1rem; /* Increased font size */
        }

        .info-value {
            background-color: var(--clr-gray);
            padding: 0.75rem 1rem; /* Increased padding */
            border-radius: 6px; /* Increased border radius */
            font-size: 0.95rem; /* Increased font size */
            word-break: break-word;
        }

        /* Social media icons within info values */
        .social-link {
            display: inline-flex;
            align-items: center;
            color: var(--clr-primary);
            text-decoration: none;
        }

        .social-link i {
            margin-right: 0.5rem; /* Increased margin */
        }

        /* Document Grid Layout with more space */
        .document-grid {
            display: grid;
            grid-template-columns: repeat(auto-fill, minmax(200px, 1fr)); /* Increased size */
            gap: 20px; /* Increased gap */
            padding: 0.75rem; /* Added padding */
        }

        .document-card {
            background-color: var(--clr-gray);
            border-radius: 10px; /* Increased border radius */
            overflow: hidden;
            transition: all 0.2s ease;
            height: 100%;
            display: flex;
            flex-direction: column;
            box-shadow: 0 2px 8px rgba(0,0,0,0.05); /* Added shadow */
        }

        .document-card:hover {
            transform: translateY(-5px); /* Increased movement */
            box-shadow: 0 5px 15px rgba(75, 108, 247, 0.2); /* Increased shadow */
        }

        .document-header {
            background-color: var(--clr-primary);
            color: var(--clr-white);
            padding: 0.75rem; /* Increased padding */
            display: flex;
            justify-content: space-between;
            align-items: center;
            font-size: 0.9rem; /* Increased font size */
        }

        .document-header h5 {
            font-size: 0.9rem; /* Increased font size */
            margin: 0;
            white-space: nowrap;
            overflow: hidden;
            text-overflow: ellipsis;
            max-width: 140px; /* Increased width */
        }

        .document-preview {
            flex: 1;
            padding: 10px; /* Increased padding */
            display: flex;
            align-items: center;
            justify-content: center;
            max-height: 180px; /* Increased height */
            overflow: hidden;
        }

        .document-preview img {
            max-width: 100%;
            max-height: 170px; /* Increased height */
            object-fit: contain;
            border-radius: 6px; /* Increased border radius */
        }

        .document-preview embed {
            width: 100%;
            height: 170px; /* Increased height */
            border-radius: 6px; /* Increased border radius */
        }

        .status-badge {
            padding: 0.4rem 0.75rem; /* Increased padding */
            border-radius: 50px;
            font-weight: 600;
            display: inline-block;
            margin-bottom: 0.75rem; /* Increased margin */
            font-size: 0.9rem; /* Increased font size */
        }

        .status-pending {
            background-color: rgba(255, 193, 7, 0.2);
            color: #856404;
        }

        .status-approved {
            background-color: rgba(40, 167, 69, 0.2);
            color: #155724;
        }

        .status-rejected {
            background-color: rgba(220, 53, 69, 0.2);
            color: #721c24;
        }

        .alert {
            border-radius: 8px; /* Increased border radius */
            padding: 1rem; /* Increased padding */
            margin-bottom: 1rem; /* Increased margin */
            font-size: 1rem; /* Increased font size */
        }

        .section-title {
            color: var(--clr-primary);
            margin-bottom: 1.25rem; /* Increased margin */
            padding-bottom: 0.5rem; /* Increased padding */
            border-bottom: 1px solid var(--clr-secondary);
            font-weight: 700;
            font-size: 1.3rem; /* Increased font size */
            padding-left: 0.75rem; /* Added padding */
        }

        /* Quick View Buttons */
        .doc-actions {
            padding: 8px; /* Increased padding */
            text-align: center;
            background-color: #f8f9fa;
            border-top: 1px solid #eee;
        }

        .view-full-btn {
            color: var(--clr-primary);
            text-decoration: none;
            font-weight: 600;
            transition: all 0.3s;
            font-size: 0.85rem; /* Increased font size */
            display: block;
            padding: 4px 0; /* Added padding */
        }

        .view-full-btn:hover {
            color: var(--clr-secondary);
            text-decoration: underline;
        }

        /* Modified Modal Styles for original size images */
        .modal-body img {
            max-width: none; /* Remove max-width constraint */
            width: auto; /* Allow image to show at its native width */
            height: auto; /* Allow image to show at its native height */
        }

        .modal-body {
            overflow: auto; /* Enable scrolling when image is larger than viewport */
            max-height: 85vh; /* Set a maximum height for the modal body */
            text-align: center; /* Center the image */
            padding: 0; /* Remove padding to maximize space */
        }

        .modal-body embed {
            width: 100%;
            height: 85vh; /* Increased height for PDFs */
        }
        
        /* Image container within modal */
        .original-image-container {
            overflow: auto;
            max-height: 85vh;
        }

        /* Spacing adjustments */
        .row {
            margin-right: -10px; /* Increased margin */
            margin-left: -10px; /* Increased margin */
        }

        .row > [class^="col-"] {
            padding-right: 10px; /* Increased padding */
            padding-left: 10px; /* Increased padding */
        }

        .mb-4 {
            margin-bottom: 1.5rem !important; /* Increased margin */
        }

        .g-3 {
            --bs-gutter-x: 20px; /* Increased gutter */
            --bs-gutter-y: 20px; /* Increased gutter */
        }

        /* Image controls for modal view */
        .image-controls {
            position: absolute;
            bottom: 15px;
            right: 15px;
            background: rgba(0,0,0,0.5);
            padding: 8px;
            border-radius: 5px;
            color: white;
            z-index: 1050;
        }

        .image-controls button {
            background: transparent;
            border: none;
            color: white;
            margin: 0 5px;
            cursor: pointer;
        }

        @media (max-width: 1200px) {
            .document-grid {
                grid-template-columns: repeat(auto-fill, minmax(180px, 1fr));
            }
        }

        @media (max-width: 992px) {
            .document-grid {
                grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
            }
            
            .info-grid {
                grid-template-columns: 1fr;
            }
        }

        @media (max-width: 768px) {
            .document-grid {
                grid-template-columns: repeat(auto-fill, minmax(160px, 1fr));
            }
        }

        @media (max-width: 576px) {
            .document-grid {
                grid-template-columns: 1fr 1fr;
                gap: 10px;
            }
        }
    </style>
</head>
<body>
    <div class="verification-container">
        <!-- Fixed Back Button -->
        <div class="back-button-container">
            <a href="admin_dashboard.php" class="btn btn-back">
                <i class="bi bi-arrow-left me-2"></i>Back to Dashboard
            </a>
        </div>

        <!-- Main Content in a 3-column layout with improved spacing -->
        <div class="row g-3">
            <!-- Profile Column -->
            <div class="col-lg-3">
                <div class="profile-card">
                    <div class="profile-header">
                        <div class="profile-avatar">
                            <i class="bi bi-person-circle"></i>
                        </div>
                        <h2>Landlord Profile</h2>
                        <?php if ($userStatus): ?>
                            <div class="status-badge status-<?php echo strtolower($userStatus['status'] ?? 'pending'); ?>">
                                <i class="bi bi-check-circle me-1"></i>
                                Status: <?php echo htmlspecialchars($userStatus['status'] ?? 'Pending'); ?>
                            </div>
                        <?php endif; ?>
                    </div>
                    <div class="profile-details">
                        <div class="detail-item">
                            <i class="bi bi-person"></i>
                            <div>
                                <h6>Full Name</h6>
                                <p><?php echo htmlspecialchars($landlordDetails['fullname'] ?? 'N/A'); ?></p>
                            </div>
                        </div>

                        <div class="detail-item">
                            <i class="bi bi-geo-alt"></i>
                            <div>
                                <h6>Location</h6>
                                <p><?php echo htmlspecialchars($landlordDetails['location'] ?? 'N/A'); ?></p>
                            </div>
                        </div>

                        <div class="detail-item">
                            <i class="bi bi-house"></i>
                            <div>
                                <h6>Property Type</h6>
                                <p><?php echo htmlspecialchars($landlordDetails['property_type'] ?? 'N/A'); ?></p>
                            </div>
                        </div>

                        <div class="detail-item">
                            <i class="bi bi-telephone"></i>
                            <div>
                                <h6>Phone Number</h6>
                                <p><?php echo htmlspecialchars($landlordDetails['phone'] ?? 'N/A'); ?></p>
                            </div>
                        </div>
                    </div>
                </div>
            </div>

            <!-- Combined Information Column -->
            <div class="col-lg-3">
                <div class="combined-section">
                    <div class="section-header">
                        <h2><i class="bi bi-card-list me-2"></i>Landlord Information</h2>
                    </div>
                    <div class="section-body">
                        <div class="info-grid">
                            <!-- Personal Information -->
                            <div class="info-group">
                                <p class="info-label"><i class="bi bi-person"></i>Full Name:</p>
                                <p class="info-value"><?php echo htmlspecialchars($detailedInfo['fullname'] ?? 'N/A'); ?></p>
                            </div>
                            
                            <div class="info-group">
                                <p class="info-label"><i class="bi bi-gender-ambiguous"></i>Sex:</p>
                                <p class="info-value"><?php echo htmlspecialchars($detailedInfo['sex'] ?? 'N/A'); ?></p>
                            </div>
                            
                            <div class="info-group">
                                <p class="info-label"><i class="bi bi-calendar"></i>Date of Birth:</p>
                                <p class="info-value"><?php echo htmlspecialchars($detailedInfo['birthday'] ?? 'N/A'); ?></p>
                            </div>
                            
                            <div class="info-group">
                                <p class="info-label"><i class="bi bi-envelope"></i>Email Address:</p>
                                <p class="info-value"><?php echo htmlspecialchars($detailedInfo['email'] ?? 'N/A'); ?></p>
                            </div>
                            
                            <div class="info-group">
                                <p class="info-label"><i class="bi bi-telephone"></i>Phone Number:</p>
                                <p class="info-value"><?php echo htmlspecialchars($detailedInfo['phone'] ?? 'N/A'); ?></p>
                            </div>
                            
                            <div class="info-group">
                                <p class="info-label"><i class="bi bi-geo-alt"></i>Complete Address:</p>
                                <p class="info-value"><?php echo htmlspecialchars($detailedInfo['address'] ?? 'N/A'); ?></p>
                            </div>
                            
                            <!-- Property Information -->
                            <div class="info-group">
                                <p class="info-label"><i class="bi bi-geo-alt"></i>Property Location:</p>
                                <p class="info-value"><?php echo htmlspecialchars($detailedInfo['location'] ?? 'N/A'); ?></p>
                            </div>
                            
                            <div class="info-group">
                                <p class="info-label"><i class="bi bi-house"></i>Property Type:</p>
                                <p class="info-value"><?php echo htmlspecialchars($detailedInfo['property_type'] ?? 'N/A'); ?></p>
                            </div>
                            
                            <!-- Social Media Information -->
                            <div class="info-group">
                                <p class="info-label"><i class="bi bi-facebook"></i>Facebook:</p>
                                <p class="info-value">
                                    <?php if(!empty($detailedInfo['facebook']) && $detailedInfo['facebook'] != 'N/A'): ?>
                                        <span class="social-link"><?php echo htmlspecialchars($detailedInfo['facebook'] ?? 'N/A'); ?></span>
                                    <?php else: ?>
                                        N/A
                                    <?php endif; ?>
                                </p>
                            </div>
                            
                            <div class="info-group">
                                <p class="info-label"><i class="bi bi-instagram"></i>Instagram:</p>
                                <p class="info-value">
                                    <?php if(!empty($detailedInfo['instagram']) && $detailedInfo['instagram'] != 'N/A'): ?>
                                        <span class="social-link"><?php echo htmlspecialchars($detailedInfo['instagram'] ?? 'N/A'); ?></span>
                                    <?php else: ?>
                                        N/A
                                    <?php endif; ?>
                                </p>
                            </div>
                            
                            <div class="info-group">
                                <p class="info-label"><i class="bi bi-twitter"></i>Twitter:</p>
                                <p class="info-value">
                                    <?php if(!empty($detailedInfo['twitter']) && $detailedInfo['twitter'] != 'N/A'): ?>
                                        <span class="social-link"><?php echo htmlspecialchars($detailedInfo['twitter'] ?? 'N/A'); ?></span>
                                    <?php else: ?>
                                        N/A
                                    <?php endif; ?>
                                </p>
                            </div>
                            
                            <div class="info-group">
                                <p class="info-label"><i class="bi bi-linkedin"></i>LinkedIn:</p>
                                <p class="info-value">
                                    <?php if(!empty($detailedInfo['linkedin']) && $detailedInfo['linkedin'] != 'N/A'): ?>
                                        <span class="social-link"><?php echo htmlspecialchars($detailedInfo['linkedin'] ?? 'N/A'); ?></span>
                                    <?php else: ?>
                                        N/A
                                    <?php endif; ?>
                                </p>
                            </div>
                        </div>
                    </div>
                </div>
            </div>

            <!-- Documents Column -->
            <div class="col-lg-6">
                <div class="documents-section">
                    <h2 class="section-title"><i class="bi bi-file-earmark-text me-2"></i>Verification Documents</h2>
                    
                    <?php if (mysqli_num_rows($resultFiles) > 0): ?>
                        <div class="document-grid">
                        <?php 
                        // Reset the pointer
                        mysqli_data_seek($resultFiles, 0);
                        while ($row = mysqli_fetch_assoc($resultFiles)): 
                        ?>
                            <div class="document-card">
                                <div class="document-header">
                                    <h5><?php echo htmlspecialchars($row['file_name']); ?></h5>
                                    <small><i class="bi bi-clock"></i> <?php echo date('d/m/y', strtotime($row['upload_date'])); ?></small>
                                </div>
                                <div class="document-preview">
                                    <?php 
                                    $file_extension = strtolower(pathinfo($row['file_path'], PATHINFO_EXTENSION));
                                    $file_id = 'doc_' . md5($row['file_path']);
                                    if (in_array($file_extension, ['jpg', 'jpeg', 'png', 'gif'])) {
                                        echo "<img src='" . htmlspecialchars($row['file_path']) . "' alt='Document' class='img-fluid'>";
                                    } elseif ($file_extension == 'pdf') {
                                        echo "<embed src='" . htmlspecialchars($row['file_path']) . "' type='application/pdf'>";
                                    } else {
                                        echo "<p class='text-danger'>Unsupported file type</p>";
                                    }
                                    ?>
                                </div>
                                <div class="doc-actions">
                                    <a href="#" class="view-full-btn" data-bs-toggle="modal" data-bs-target="#<?php echo $file_id; ?>">
                                        <i class="bi bi-arrows-fullscreen me-1"></i>View Full Size
                                    </a>
                                </div>
                            </div>
                            
                            <!-- Modal for full-size view -->
                            <div class="modal fade" id="<?php echo $file_id; ?>" tabindex="-1" aria-hidden="true">
                                <div class="modal-dialog modal-xl modal-dialog-centered">
                                    <div class="modal-content">
                                        <div class="modal-header">
                                            <h5 class="modal-title"><?php echo htmlspecialchars($row['file_name']); ?></h5>
                                            <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                                        </div>
                                        <div class="modal-body">
                                            <?php 
                                            if (in_array($file_extension, ['jpg', 'jpeg', 'png', 'gif'])) {
                                                echo "<div class='original-image-container'>";
                                                echo "<img src='" . htmlspecialchars($row['file_path']) . "' alt='Document' class='original-size-img'>";
                                                echo "</div>";
                                                echo "<div class='image-controls'>";
                                                echo "<button id='zoom-in-{$file_id}' title='Zoom In'><i class='bi bi-zoom-in'></i></button>";
                                                echo "<button id='zoom-out-{$file_id}' title='Zoom Out'><i class='bi bi-zoom-out'></i></button>";
                                                echo "<button id='reset-zoom-{$file_id}' title='Reset Zoom'><i class='bi bi-arrows-angle-contract'></i></button>";
                                                echo "</div>";
                                            } elseif ($file_extension == 'pdf') {
                                                echo "<embed src='" . htmlspecialchars($row['file_path']) . "' type='application/pdf' width='100%' height='700px'>";
                                            }
                                            ?>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        <?php endwhile; ?>
                        </div>
                    <?php else: ?>
                        <div class="alert alert-info text-center" role="alert">
                            <i class="bi bi-info-circle me-2"></i>No documents uploaded yet
                        </div>
                    <?php endif; ?>
                </div>
            </div>
        </div>
    </div>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
    
    <!-- Added JavaScript for handling image zoom functionality -->
    <script>
    document.addEventListener('DOMContentLoaded', function() {
        // Find all modals with images
        const imageModals = document.querySelectorAll('.modal');
        
        imageModals.forEach(modal => {
            const modalId = modal.id;
            const img = modal.querySelector('.original-size-img');
            
            if (img) {
                // Initialize zoom level
                let zoomLevel = 1;
                const zoomInBtn = document.getElementById(`zoom-in-${modalId}`);
                const zoomOutBtn = document.getElementById(`zoom-out-${modalId}`);
                const resetZoomBtn = document.getElementById(`reset-zoom-${modalId}`);
                
                if (zoomInBtn && zoomOutBtn && resetZoomBtn) {
                    // Zoom in button
                   // Zoom in button
                   zoomInBtn.addEventListener('click', function() {
                        zoomLevel += 0.25;
                        updateZoom();
                    });
                    
                    // Zoom out button
                    zoomOutBtn.addEventListener('click', function() {
                        if (zoomLevel > 0.5) {
                            zoomLevel -= 0.25;
                            updateZoom();
                        }
                    });
                    
                    // Reset zoom button
                    resetZoomBtn.addEventListener('click', function() {
                        zoomLevel = 1;
                        updateZoom();
                    });
                    
                    // Update image zoom
                    function updateZoom() {
                        img.style.transform = `scale(${zoomLevel})`;
                        img.style.transformOrigin = 'center center';
                        img.style.transition = 'transform 0.2s ease';
                    }
                    
                    // Additional code to allow dragging of zoomed images
                    let isDragging = false;
                    let startX, startY, scrollLeft, scrollTop;
                    const container = modal.querySelector('.original-image-container');
                    
                    // Mouse events for dragging
                    container.addEventListener('mousedown', function(e) {
                        if (zoomLevel > 1) {
                            isDragging = true;
                            startX = e.pageX - container.offsetLeft;
                            startY = e.pageY - container.offsetTop;
                            scrollLeft = container.scrollLeft;
                            scrollTop = container.scrollTop;
                            container.style.cursor = 'grabbing';
                        }
                    });
                    
                    container.addEventListener('mouseleave', function() {
                        isDragging = false;
                        container.style.cursor = 'default';
                    });
                    
                    container.addEventListener('mouseup', function() {
                        isDragging = false;
                        container.style.cursor = 'default';
                    });
                    
                    container.addEventListener('mousemove', function(e) {
                        if (!isDragging) return;
                        e.preventDefault();
                        
                        const x = e.pageX - container.offsetLeft;
                        const y = e.pageY - container.offsetTop;
                        
                        const walkX = (x - startX) * 2; // Adjust scrolling speed
                        const walkY = (y - startY) * 2;
                        
                        container.scrollLeft = scrollLeft - walkX;
                        container.scrollTop = scrollTop - walkY;
                    });
                }
                
                // Ensure image is displayed at original size when modal opens
                modal.addEventListener('shown.bs.modal', function() {
                    if (img) {
                        // Reset any previous transformations
                        img.style.transform = 'scale(1)';
                        img.style.maxWidth = 'none';
                        img.style.maxHeight = 'none';
                        
                        // Add some additional styles for better viewing
                        const container = modal.querySelector('.original-image-container');
                        if (container) {
                            container.style.display = 'flex';
                            container.style.justifyContent = 'center';
                            container.style.alignItems = 'center';
                            
                            // If image is smaller than viewport, center it
                            if (img.naturalWidth < container.clientWidth && 
                                img.naturalHeight < container.clientHeight) {
                                container.style.overflow = 'hidden';
                            } else {
                                container.style.overflow = 'auto';
                            }
                        }
                    }
                });
                
                // Reset zoom when modal is closed
                modal.addEventListener('hidden.bs.modal', function() {
                    if (img) {
                        zoomLevel = 1;
                        img.style.transform = 'scale(1)';
                    }
                });
            }
        });
        
        // Add additional CSS for improved image viewing
        const style = document.createElement('style');
        style.textContent = `
            .original-image-container {
                display: flex;
                justify-content: center;
                align-items: center;
                width: 100%;
                height: 85vh;
                overflow: auto;
                position: relative;
            }
            
            .original-size-img {
                transform-origin: center;
                cursor: move;
                transition: transform 0.2s ease;
            }
            
            .modal-content {
                height: 90vh;
                display: flex;
                flex-direction: column;
            }
            
            .modal-body {
                flex: 1;
                overflow: hidden;
                position: relative;
            }
            
            .image-controls {
                position: fixed;
                bottom: 20px;
                right: 20px;
                background: rgba(0, 0, 0, 0.6);
                border-radius: 50px;
                padding: 8px 15px;
                z-index: 1060;
                display: flex;
                align-items: center;
                justify-content: center;
                gap: 10px;
                box-shadow: 0 2px 10px rgba(0, 0, 0, 0.3);
            }
            
            .image-controls button {
                background: transparent;
                border: none;
                color: white;
                font-size: 18px;
                width: 30px;
                height: 30px;
                display: flex;
                align-items: center;
                justify-content: center;
                cursor: pointer;
                border-radius: 50%;
                transition: background 0.2s;
            }
            
            .image-controls button:hover {
                background: rgba(255, 255, 255, 0.2);
            }
            
            /* Additional info text to display image dimensions */
            .image-info {
                position: fixed;
                top: 70px;
                left: 20px;
                background: rgba(0, 0, 0, 0.6);
                color: white;
                padding: 5px 10px;
                border-radius: 4px;
                font-size: 0.8rem;
                z-index: 1060;
                opacity: 0.8;
                pointer-events: none;
            }
        `;
        document.head.appendChild(style);
        
        // Add image info display for each modal
        imageModals.forEach(modal => {
            const img = modal.querySelector('.original-size-img');
            if (img) {
                img.onload = function() {
                    // Create an element to display image dimensions
                    const infoDiv = document.createElement('div');
                    infoDiv.className = 'image-info';
                    infoDiv.textContent = `Original Size: ${img.naturalWidth} × ${img.naturalHeight}px`;
                    
                    // Append to modal body
                    const modalBody = modal.querySelector('.modal-body');
                    if (modalBody && !modalBody.querySelector('.image-info')) {
                        modalBody.appendChild(infoDiv);
                        
                        // Hide info after 3 seconds
                        setTimeout(() => {
                            infoDiv.style.opacity = '0';
                            infoDiv.style.transition = 'opacity 0.5s';
                            
                            // Remove after fade out
                            setTimeout(() => {
                                if (infoDiv.parentNode) {
                                    infoDiv.parentNode.removeChild(infoDiv);
                                }
                            }, 500);
                        }, 3000);
                    }
                };
            }
        });
        
        // Add keyboard shortcuts for zoom controls
        document.addEventListener('keydown', function(e) {
            // Find active modal
            const activeModal = document.querySelector('.modal.show');
            if (!activeModal) return;
            
            const modalId = activeModal.id;
            const zoomInBtn = document.getElementById(`zoom-in-${modalId}`);
            const zoomOutBtn = document.getElementById(`zoom-out-${modalId}`);
            const resetZoomBtn = document.getElementById(`reset-zoom-${modalId}`);
            
            // + key to zoom in
            if (e.key === '+' || e.key === '=') {
                if (zoomInBtn) zoomInBtn.click();
            }
            // - key to zoom out
            else if (e.key === '-' || e.key === '_') {
                if (zoomOutBtn) zoomOutBtn.click();
            }
            // 0 key to reset zoom
            else if (e.key === '0') {
                if (resetZoomBtn) resetZoomBtn.click();
            }
        });
    });
    </script>
</body>
</html>