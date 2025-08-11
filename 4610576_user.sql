-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1:3308
-- Generation Time: May 13, 2025 at 04:43 PM
-- Server version: 10.4.32-MariaDB
-- PHP Version: 8.0.30

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `4610576_user`
--

-- --------------------------------------------------------

--
-- Table structure for table `landlord_details`
--

CREATE TABLE `landlord_details` (
  `id` int(11) NOT NULL,
  `userid` int(11) NOT NULL,
  `fullname` varchar(255) NOT NULL,
  `sex` varchar(10) DEFAULT NULL,
  `birthday` date DEFAULT NULL,
  `email` varchar(255) DEFAULT NULL,
  `address` text DEFAULT NULL,
  `location` varchar(255) NOT NULL,
  `property_type` varchar(50) NOT NULL,
  `phone` varchar(20) NOT NULL,
  `facebook` varchar(255) DEFAULT NULL,
  `instagram` varchar(255) DEFAULT NULL,
  `twitter` varchar(255) DEFAULT NULL,
  `linkedin` varchar(255) DEFAULT NULL,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `landlord_details`
--

INSERT INTO `landlord_details` (`id`, `userid`, `fullname`, `sex`, `birthday`, `email`, `address`, `location`, `property_type`, `phone`, `facebook`, `instagram`, `twitter`, `linkedin`, `created_at`) VALUES
(5, 32, 'letlet sibayan', NULL, NULL, NULL, NULL, 'angeles', 'House', '09157546752', NULL, NULL, NULL, NULL, '2025-03-26 14:18:14'),
(6, 36, 'Yoshiken', 'Male', '2007-02-11', 'Yoshitensei101@gmail.com', 'STI College Angeles, Central Luzon, Philippines', 'Angeles', 'Apartment', '09692327721', 'adasdas', '', 'asdasd', '', '2025-03-27 05:50:21'),
(7, 35, 'Aj Boy', NULL, NULL, NULL, NULL, 'pa', 'Apartment', '09692327721', NULL, NULL, NULL, NULL, '2025-03-27 06:01:31'),
(8, 40, 'Mama mo', NULL, NULL, NULL, NULL, 'hehhe', 'House', '09457213561', NULL, NULL, NULL, NULL, '2025-03-27 13:50:05'),
(9, 41, 'Banina', NULL, NULL, NULL, NULL, 'ipil ipil', 'House', '09457213561', NULL, NULL, NULL, NULL, '2025-03-27 14:14:03'),
(10, 42, 'samsung a20s', NULL, NULL, NULL, NULL, 'Angeles', 'Apartment', '09154237546', NULL, NULL, NULL, NULL, '2025-03-27 14:31:25'),
(11, 45, 'Testing user', NULL, NULL, NULL, NULL, 'Angeles', 'Apartment', '09267846642', NULL, NULL, NULL, NULL, '2025-03-27 18:03:57'),
(13, 47, 'lester', NULL, NULL, NULL, NULL, 'skibidi', 'House', '09643721533', NULL, NULL, NULL, NULL, '2025-03-28 00:02:46'),
(14, 59, 'yoshyikeennn', NULL, NULL, NULL, NULL, 'angeles', 'House', '09692327721', NULL, NULL, NULL, NULL, '2025-04-06 03:25:14'),
(0, 91, 'asdasdsa', 'Male', '2025-04-10', 'Yoshitensei101@gmail.com', 'STI College Angeles, Central Luzon, Philippines', 'Angeles', 'House', '09692327721', 'facebook', 'IG', 'Twitter', 'asd', '2025-04-09 10:42:00'),
(0, 34, 'Yoahhw', NULL, NULL, NULL, NULL, 'chhee', 'Apartment', '09692327721', NULL, NULL, NULL, NULL, '2025-04-09 10:42:30'),
(0, 92, 'Jeff Barzaga', 'Female', '1998-04-01', 'Jeff123@gmail.com', 'Sti Angeles Pampanga  mc Arthur highway', 'Angeles City, Pampanga', 'House', '09185467981', 'facebook', 'ig', 'twitter', 'linkend', '2025-04-10 08:35:15'),
(0, 95, 'jm gragra', 'Male', '2012-08-17', 'Yoshitensei101@gmail.com', 'ipil ipul', 'angeles', 'Other', '09281234567', 'asd', 'asd', 'asd', 'asd', '2025-04-15 06:40:17'),
(0, 93, 'Rina G', 'Female', '2025-03-30', 'rina@gmel.com', 'STI College Angeles, Central Luzon, Philippines', 'Angeles', 'Apartment', '09484131484', '123', '123', '123', '1231', '2025-04-17 05:32:07'),
(0, 97, 'Yoshiro kenshin', 'null', '0000-00-00', 'Yoshiro123@gmail.com', 'null', 'sad', 'House', '09598754453', 'fb', 'lg', 'X', 'lnx', '2025-05-12 10:55:14'),
(0, 99, 'Sung Jin Woo', 'Male', '2025-04-08', 'falserank@gmai.com', 'JEJU', 'JEJU', 'Condo', '0841451', 'Jinwoo', 'Jinwoo', 'Jinwoo', 'Jinwoo', '2025-05-13 13:28:50'),
(0, 101, 'Chahae', 'Male', '2025-05-01', 'cha@gmail.com', 'nul23l', 'da', 'Apartment', '12564416464', 'null', 'null', 'null', 'null', '2025-05-13 13:56:24');

-- --------------------------------------------------------

--
-- Table structure for table `landlord_files`
--

CREATE TABLE `landlord_files` (
  `file_id` int(11) NOT NULL,
  `userid` int(11) NOT NULL,
  `file_name` varchar(255) NOT NULL,
  `file_path` varchar(255) NOT NULL,
  `file_type` varchar(50) DEFAULT NULL,
  `upload_date` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `landlord_files`
--

INSERT INTO `landlord_files` (`file_id`, `userid`, `file_name`, `file_path`, `file_type`, `upload_date`) VALUES
(10, 32, 'IMG_20250325_135250.jpg', 'uploads/landlord_docs/32_67e40ca65f6458.02298889_IMG_20250325_135250.jpg', NULL, '2025-03-26 14:18:14'),
(11, 36, 'Leonardo_Phoenix_10_Design_a_modern_clean_and_professional_log_3 (1).jpg', 'uploads/landlord_docs/36_67e4e71d88b061.64035691_Leonardo_Phoenix_10_Design_a_modern_clean_and_professional_log_3 (1).jpg', NULL, '2025-03-27 05:50:21'),
(12, 36, 'Leonardo_Phoenix_10_Design_a_modern_clean_and_professional_log_3 (1).jpg', 'uploads/landlord_docs/36_67e4e94bf00047.17078851_Leonardo_Phoenix_10_Design_a_modern_clean_and_professional_log_3 (1).jpg', NULL, '2025-03-27 05:59:39'),
(13, 36, 'Leonardo_Phoenix_10_Design_a_modern_clean_and_professional_log_3 (1).jpg', 'uploads/landlord_docs/36_67e4e954ed5ef1.64426477_Leonardo_Phoenix_10_Design_a_modern_clean_and_professional_log_3 (1).jpg', NULL, '2025-03-27 05:59:48'),
(14, 35, 'Leonardo_Phoenix_10_Design_a_modern_clean_and_professional_log_3 (1).jpg', 'uploads/landlord_docs/35_67e4e9bbd59b63.11050961_Leonardo_Phoenix_10_Design_a_modern_clean_and_professional_log_3 (1).jpg', NULL, '2025-03-27 06:01:31'),
(15, 35, 'Leonardo_Phoenix_10_Design_a_modern_clean_and_professional_log_3 (1).jpg', 'uploads/landlord_docs/35_67e4e9d88940c3.75656280_Leonardo_Phoenix_10_Design_a_modern_clean_and_professional_log_3 (1).jpg', NULL, '2025-03-27 06:02:00'),
(16, 40, '1743000906084.jpg', 'uploads/landlord_docs/40_67e5578d1a4553.14327575_1743000906084.jpg', NULL, '2025-03-27 13:50:05'),
(17, 41, 'Messenger_creation_D30B4864-5389-47C6-8D5D-47626C4EBE8E.webp', 'uploads/landlord_docs/41_67e55d2b110455.26618860_Messenger_creation_D30B4864-5389-47C6-8D5D-47626C4EBE8E.webp', NULL, '2025-03-27 14:14:03'),
(18, 41, 'Messenger_creation_720457B4-A9F1-495F-828B-090BB60DB2F8.jpeg', 'uploads/landlord_docs/41_67e55d8a28a0c8.88104823_Messenger_creation_720457B4-A9F1-495F-828B-090BB60DB2F8.jpeg', NULL, '2025-03-27 14:15:38'),
(19, 42, '20250312_082313.jpg', 'uploads/landlord_docs/42_67e5613d5b8735.62201485_20250312_082313.jpg', NULL, '2025-03-27 14:31:25'),
(20, 45, 'IMG_20250325_135249.jpg', 'uploads/landlord_docs/45_67e5930ddae026.41018505_IMG_20250325_135249.jpg', NULL, '2025-03-27 18:03:57'),
(21, 47, 'Screenshot_20250328-080119.jpg', 'uploads/landlord_docs/47_67e5e726c43660.70708543_Screenshot_20250328-080119.jpg', NULL, '2025-03-28 00:02:46'),
(22, 47, 'Screenshot_20250328-080119.jpg', 'uploads/landlord_docs/47_67e5e7271935c9.59761339_Screenshot_20250328-080119.jpg', NULL, '2025-03-28 00:02:47'),
(23, 59, 'Rentlify-modified.png', 'uploads/landlord_docs/59_67f1f41a44e581.96111758_Rentlify-modified.png', NULL, '2025-04-06 03:25:14'),
(24, 59, 'Rentlify-modified.png', 'uploads/landlord_docs/59_67f234a6b01d47.51182009_Rentlify-modified.png', NULL, '2025-04-06 08:00:38'),
(0, 59, 'IMG_20250325_135250.jpg', 'uploads/landlord_docs/59_67f249b8223b99.21760890_IMG_20250325_135250.jpg', NULL, '2025-04-06 09:30:32'),
(0, 91, 'Rentlify.png', 'uploads/landlord_docs/91_67f64ef8d21853.68821752_Rentlify.png', NULL, '2025-04-09 10:42:00'),
(0, 34, 'Rentlify.png', 'uploads/landlord_docs/34_67f64f167b0f65.21055611_Rentlify.png', NULL, '2025-04-09 10:42:30'),
(0, 34, 'Rentlify.png', 'uploads/landlord_docs/34_67f6504d4ff009.39711584_Rentlify.png', NULL, '2025-04-09 10:47:41'),
(0, 91, 'fef1849647d9ef67b83345cc4828c479.jpg', 'uploads/landlord_docs/91_doc_67f77a4e45e710.65537658_fef1849647d9ef67b83345cc4828c479.jpg', 'document', '2025-04-10 07:59:10'),
(0, 91, 'f8953f88f4062c8fc42810b1a557aaed.jpg', 'uploads/landlord_docs/91_face_67f77a4e479de4.44205275_f8953f88f4062c8fc42810b1a557aaed.jpg', 'facial_verification', '2025-04-10 07:59:10'),
(0, 91, 'fef1849647d9ef67b83345cc4828c479.jpg', 'uploads/landlord_docs/91_doc_67f77b6a866694.84659070_fef1849647d9ef67b83345cc4828c479.jpg', 'document', '2025-04-10 08:03:54'),
(0, 91, 'f8953f88f4062c8fc42810b1a557aaed.jpg', 'uploads/landlord_docs/91_face_67f77b6a89bad0.22245453_f8953f88f4062c8fc42810b1a557aaed.jpg', 'facial_verification', '2025-04-10 08:03:54'),
(0, 92, '17442741043499148287663793927849.jpg', 'uploads/landlord_docs/92_doc_67f782c3394c95.80317598_17442741043499148287663793927849.jpg', 'document', '2025-04-10 08:35:15'),
(0, 92, 'IMG_20250325_135249.jpg', 'uploads/landlord_docs/92_face_67f782c33a7351.82746314_IMG_20250325_135249.jpg', 'facial_verification', '2025-04-10 08:35:15'),
(0, 95, '490490687_1328240908457086_3771810264957105556_n.png', 'uploads/landlord_docs/95_doc_67fdff51a1d315.58497718_490490687_1328240908457086_3771810264957105556_n.png', 'document', '2025-04-15 06:40:17'),
(0, 95, '490490687_1328240908457086_3771810264957105556_n.png', 'uploads/landlord_docs/95_face_67fdff51a56e12.98994496_490490687_1328240908457086_3771810264957105556_n.png', 'facial_verification', '2025-04-15 06:40:17'),
(0, 36, 'c6d43075-2148-462f-ba16-266a55eea96e.png', 'uploads/landlord_docs/36_doc_6800616116cf97.77069229_c6d43075-2148-462f-ba16-266a55eea96e.png', 'document', '2025-04-17 02:03:13'),
(0, 36, 'fef1849647d9ef67b83345cc4828c479.jpg', 'uploads/landlord_docs/36_face_680061613a5ef1.25709822_fef1849647d9ef67b83345cc4828c479.jpg', 'facial_verification', '2025-04-17 02:03:13'),
(0, 93, 'c6d43075-2148-462f-ba16-266a55eea96e.png', 'uploads/landlord_docs/93_doc_68009257924a18.05238444_c6d43075-2148-462f-ba16-266a55eea96e.png', 'document', '2025-04-17 05:32:07'),
(0, 93, 'fef1849647d9ef67b83345cc4828c479.jpg', 'uploads/landlord_docs/93_face_68009257935a01.16943924_fef1849647d9ef67b83345cc4828c479.jpg', 'facial_verification', '2025-04-17 05:32:07'),
(0, 97, '97_face_6821d364cdeb80.40916591.jpg', 'uploads/landlord_docs/97_face_6821d364cdeb80.40916591.jpg', 'facial_verification', '2025-05-12 10:54:28'),
(0, 97, 'IMG_20250512_105351.jpg', 'uploads/landlord_docs/97_doc_6821d364ce9275.78897681_IMG_20250512_105351.jpg', 'document', '2025-05-12 10:54:28'),
(0, 97, '97_face_6821d392bd1aa2.39897830.jpg', 'uploads/landlord_docs/97_face_6821d392bd1aa2.39897830.jpg', 'facial_verification', '2025-05-12 10:55:14'),
(0, 97, 'IMG_20250512_105351.jpg', 'uploads/landlord_docs/97_doc_6821d392bdac73.13634932_IMG_20250512_105351.jpg', 'document', '2025-05-12 10:55:14'),
(0, 99, '99_face_682342dd946749.19089547.jpg', 'uploads/landlord_docs/99_face_682342dd946749.19089547.jpg', 'facial_verification', '2025-05-13 13:02:21'),
(0, 99, 'IMG_20250512_105351_3.jpg', 'uploads/landlord_docs/99_doc_682342dd952bd8.65938921_IMG_20250512_105351_3.jpg', 'document', '2025-05-13 13:02:21'),
(0, 99, '99_face_682343eb484f23.97327914.jpg', '../../uploads/landlord_docs/99_face_682343eb484f23.97327914.jpg', 'facial_verification', '2025-05-13 13:06:51'),
(0, 99, 'IMG_20250512_105351_3.jpg', '../../uploads/landlord_docs/99_doc_682343eb4912c4.35075731_IMG_20250512_105351_3.jpg', 'document', '2025-05-13 13:06:51'),
(0, 99, '99_face_682344484a6cd3.43247532.jpg', '../../../uploads/landlord_docs/99_face_682344484a6cd3.43247532.jpg', 'facial_verification', '2025-05-13 13:08:24'),
(0, 99, 'IMG_20250512_105351_3.jpg', '../../../uploads/landlord_docs/99_doc_682344484b3ca7.78753564_IMG_20250512_105351_3.jpg', 'document', '2025-05-13 13:08:24'),
(0, 99, '99_face_682344febb8a09.48630960.jpg', '../../../uploads/landlord_docs/99_face_682344febb8a09.48630960.jpg', 'facial_verification', '2025-05-13 13:11:26'),
(0, 99, 'IMG_20250512_105351_3.jpg', '../../../uploads/landlord_docs/99_doc_682344fec21317.33026157_IMG_20250512_105351_3.jpg', 'document', '2025-05-13 13:11:26'),
(0, 99, '99_face_682346775a55a1.70349062.jpg', '../../../uploads/landlord_docs/99_face_682346775a55a1.70349062.jpg', 'facial_verification', '2025-05-13 13:17:43'),
(0, 99, 'IMG_20250512_105351_3.jpg', '../../../uploads/landlord_docs/99_doc_682346775b43f8.21466681_IMG_20250512_105351_3.jpg', 'document', '2025-05-13 13:17:43'),
(0, 99, '99_face_682349129ab379.94734263.jpg', '../../../uploads/landlord_docs/99_face_682349129ab379.94734263.jpg', 'facial_verification', '2025-05-13 13:28:50'),
(0, 99, 'IMG_20250512_105351_3.jpg', '../../../uploads/landlord_docs/99_doc_682349129bb3a4.03796330_IMG_20250512_105351_3.jpg', 'document', '2025-05-13 13:28:50'),
(0, 101, '101_face_68234f1d1270b5.79657571.jpg', 'uploads/landlord_docs/101_face_68234f1d1270b5.79657571.jpg', 'facial_verification', '2025-05-13 13:54:37'),
(0, 101, 'IMG_20250512_105351_3.jpg', 'uploads/landlord_docs/101_doc_68234f1d138ad0.72007397_IMG_20250512_105351_3.jpg', 'document', '2025-05-13 13:54:37'),
(0, 101, '101_face_68234f28bc9546.24154058.jpg', 'uploads/landlord_docs/101_face_68234f28bc9546.24154058.jpg', 'facial_verification', '2025-05-13 13:54:48'),
(0, 101, 'IMG_20250512_105351_3.jpg', 'uploads/landlord_docs/101_doc_68234f28bda6e4.19910576_IMG_20250512_105351_3.jpg', 'document', '2025-05-13 13:54:48'),
(0, 101, '101_face_68234f3364bb17.18924088.jpg', 'uploads/landlord_docs/101_face_68234f3364bb17.18924088.jpg', 'facial_verification', '2025-05-13 13:54:59'),
(0, 101, 'IMG_20250512_105351_3.jpg', 'uploads/landlord_docs/101_doc_68234f3368fc70.16508215_IMG_20250512_105351_3.jpg', 'document', '2025-05-13 13:54:59'),
(0, 101, '101_face_68234f448e5310.23368788.jpg', 'uploads/landlord_docs/101_face_68234f448e5310.23368788.jpg', 'facial_verification', '2025-05-13 13:55:16'),
(0, 101, 'IMG_20250512_105351_3.jpg', 'uploads/landlord_docs/101_doc_68234f448ff7b4.72545943_IMG_20250512_105351_3.jpg', 'document', '2025-05-13 13:55:16'),
(0, 101, '101_face_68234f70507ae8.61981297.jpg', 'uploads/landlord_docs/101_face_68234f70507ae8.61981297.jpg', 'facial_verification', '2025-05-13 13:56:00'),
(0, 101, 'IMG_20250512_105351_3.jpg', 'uploads/landlord_docs/101_doc_68234f70514870.69895858_IMG_20250512_105351_3.jpg', 'document', '2025-05-13 13:56:00'),
(0, 101, '101_face_68234f883a84e5.23279425.jpg', 'uploads/landlord_docs/101_face_68234f883a84e5.23279425.jpg', 'facial_verification', '2025-05-13 13:56:24'),
(0, 101, 'IMG_20250512_105351_3.jpg', 'uploads/landlord_docs/101_doc_68234f883c7386.09489960_IMG_20250512_105351_3.jpg', 'document', '2025-05-13 13:56:24');

-- --------------------------------------------------------

--
-- Table structure for table `messages`
--

CREATE TABLE `messages` (
  `id` int(11) NOT NULL,
  `sender_id` int(11) DEFAULT NULL,
  `receiver_id` int(11) DEFAULT NULL,
  `property_id` int(11) DEFAULT NULL,
  `message` text DEFAULT NULL,
  `message_type` varchar(50) DEFAULT NULL,
  `timestamp` datetime DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `notifications`
--

CREATE TABLE `notifications` (
  `id` int(11) NOT NULL,
  `status` enum('unread','read') DEFAULT 'unread',
  `request` enum('rent','visit') NOT NULL,
  `user_id` int(11) DEFAULT NULL,
  `property_id` int(11) DEFAULT NULL,
  `message` text DEFAULT NULL,
  `notification_for` enum('tenant','landlord') DEFAULT NULL,
  `read_status` tinyint(1) DEFAULT 0,
  `created_at` datetime DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `notifications`
--

INSERT INTO `notifications` (`id`, `status`, `request`, `user_id`, `property_id`, `message`, `notification_for`, `read_status`, `created_at`) VALUES
(72, 'read', 'rent', 36, 54, 'You have a new rental application from retret for Condo #2 in balibago.', 'landlord', 1, '2025-05-08 21:53:58'),
(73, 'read', 'rent', 31, 54, 'Your rental application for Condo #2 in balibago has been submitted and is awaiting review.', 'tenant', 1, '2025-05-08 21:53:58'),
(74, 'read', 'visit', 36, 54, 'You have a new Visit Request from retret for Property #54 on 2025-04-22.', 'landlord', 1, '2025-05-08 21:54:42'),
(75, 'read', 'visit', 31, 54, 'Your visit request for Property #54 on 2025-04-22 has been submitted and is awaiting approval.', 'tenant', 1, '2025-05-08 21:54:42'),
(76, 'read', 'visit', 36, 54, 'You have a new Visit Request from retret for Property #54 on 2025-04-26.', 'landlord', 1, '2025-05-08 21:54:46'),
(77, 'read', 'visit', 31, 54, 'Your visit request for Property #54 on 2025-04-26 has been submitted and is awaiting approval.', 'tenant', 1, '2025-05-08 21:54:46'),
(78, 'read', 'visit', 36, 54, 'You have a new Visit Request from retret for Property #54 on 2025-05-12.', 'landlord', 1, '2025-05-08 21:56:06'),
(79, 'read', 'visit', 31, 54, 'Your visit request for Property #54 on 2025-05-12 has been submitted and is awaiting approval.', 'tenant', 1, '2025-05-08 21:56:06'),
(81, 'unread', 'visit', 31, NULL, 'Your visit request for Property #51 on 2025-04-24 has been submitted and is awaiting approval.', NULL, 0, '2025-05-10 09:22:16'),
(82, 'unread', 'visit', 93, NULL, 'You have a new Visit Request from retret for Property #55 on 2025-04-26.', NULL, 0, '2025-05-10 09:22:31'),
(83, 'unread', 'visit', 31, NULL, 'Your visit request for Property #55 on 2025-04-26 has been submitted and is awaiting approval.', NULL, 0, '2025-05-10 09:22:31'),
(84, 'unread', 'rent', 93, 22, 'New rental application from retret for asd in asd', 'landlord', 0, '2025-05-10 09:28:41'),
(85, 'read', 'rent', 31, 22, 'Your rental application for asd in asd has been submitted and is awaiting approval', 'tenant', 1, '2025-05-10 09:28:41'),
(86, 'read', 'visit', 36, 52, 'You have a new Visit Request from retret for property 1 in cebu on 2025-04-24.', 'landlord', 1, '2025-05-10 09:54:10'),
(87, 'read', 'visit', 31, 52, 'Your visit request for property 1 in cebu on 2025-04-24 has been submitted and is awaiting approval.', 'tenant', 1, '2025-05-10 09:54:10'),
(90, 'unread', 'rent', 93, 55, 'New rental application from retret for Condo #3 in Pulung Bulu', 'landlord', 0, '2025-05-12 10:42:39'),
(91, 'unread', 'rent', 31, 55, 'Your rental application for Condo #3 in Pulung Bulu has been submitted and is awaiting approval', 'tenant', 0, '2025-05-12 10:42:39'),
(92, 'read', 'rent', 97, 56, 'New rental application from Ajboy for House #10 in pampanga', 'landlord', 1, '2025-05-12 18:59:47'),
(93, 'read', 'rent', 98, 56, 'Your rental application for House #10 in pampanga has been submitted and is awaiting approval', 'tenant', 1, '2025-05-12 18:59:47'),
(98, 'unread', 'visit', 36, NULL, 'You have a new Visit Request from retret for Property #53 on 2025-04-28.', NULL, 0, '2025-05-12 19:06:17'),
(99, 'unread', 'visit', 31, NULL, 'Your visit request for Property #53 on 2025-04-28 has been submitted and is awaiting approval.', NULL, 0, '2025-05-12 19:06:17'),
(100, 'unread', 'visit', 36, 53, 'New Visit Request for Property #53 on 2025-04-25.', 'landlord', 0, '2025-05-12 19:10:35'),
(101, 'unread', 'visit', 31, 53, 'Your visit request for Property #53 on 2025-04-25 is pending.', 'tenant', 0, '2025-05-12 19:10:35'),
(102, 'unread', 'visit', 93, 55, 'New Visit Request for Property #55 on 2025-04-27.', 'landlord', 0, '2025-05-12 19:11:25'),
(103, 'unread', 'visit', 31, 55, 'Your visit request for Property #55 on 2025-04-27 is pending.', 'tenant', 0, '2025-05-12 19:11:25'),
(104, 'unread', 'visit', 36, 53, 'New Visit Request from retret for Property #53 on 2025-04-27.', 'landlord', 0, '2025-05-12 19:13:17'),
(105, 'unread', 'visit', 31, 53, 'Your visit request for Property #53 on 2025-04-27 has been submitted.', 'tenant', 0, '2025-05-12 19:13:17'),
(106, 'unread', 'visit', 31, 53, 'New visit request for property #53 on 2025-04-27', 'landlord', 0, '2025-05-12 19:33:44'),
(107, 'unread', 'visit', 31, 55, 'New visit request for property #55 on 2025-04-28', 'landlord', 0, '2025-05-12 19:34:14'),
(108, 'unread', 'visit', 93, NULL, 'You have a new Visit Request from retret for Property #55 on 2025-04-27.', NULL, 0, '2025-05-12 20:08:16'),
(109, 'unread', 'visit', 31, NULL, 'Your visit request for Property #55 on 2025-04-27 has been submitted and is awaiting approval.', NULL, 0, '2025-05-12 20:08:16'),
(110, 'unread', 'visit', 36, NULL, 'You have a new Visit Request from retret for Property #53 on 2025-04-29.', NULL, 0, '2025-05-12 20:11:19'),
(111, 'unread', 'visit', 31, NULL, 'Your visit request for Property #53 on 2025-04-29 has been submitted and is awaiting approval.', NULL, 0, '2025-05-12 20:11:19'),
(112, 'unread', 'rent', 36, 36, 'New rental application from retret for arigato1 in aritago1', 'landlord', 0, '2025-05-12 20:13:30'),
(113, 'unread', 'rent', 31, 36, 'Your rental application for arigato1 in aritago1 has been submitted and is awaiting approval', 'tenant', 0, '2025-05-12 20:13:30'),
(114, 'unread', 'visit', 36, 51, 'You have a new Visit Request from retret for 123 on 2025-04-24.', 'landlord', 0, '2025-05-12 20:13:57'),
(115, 'unread', 'visit', 31, 51, 'Your visit request for 123 on 2025-04-24 has been submitted and is awaiting approval.', 'tenant', 0, '2025-05-12 20:13:57'),
(116, 'read', 'visit', 93, 55, 'You have a new Visit Request from Ajboy for Condo #3 on 2025-04-27.', 'landlord', 1, '2025-05-12 20:14:42'),
(117, 'read', 'visit', 98, 55, 'Your visit request for Condo #3 on 2025-04-27 has been submitted and is awaiting approval.', 'tenant', 1, '2025-05-12 20:14:42'),
(118, 'read', 'visit', 98, 55, 'Your visit request for Condo #3 on 2025-04-27 has been approved.', 'tenant', 1, '2025-05-12 20:18:57'),
(119, 'read', 'visit', 98, 55, 'Your visit request for Condo #3 on 2025-04-27 has been approved.', 'tenant', 1, '2025-05-12 20:25:47'),
(120, 'read', 'visit', 98, 55, 'Your visit request for Condo #3 on 2025-04-27 has been rejected.', 'tenant', 1, '2025-05-12 20:39:13'),
(121, 'read', 'rent', 93, 55, 'New rental application from Ajboy for Condo #3 in Pulung Bulu', 'landlord', 1, '2025-05-13 14:40:47'),
(122, 'read', 'rent', 98, 55, 'Your rental application for Condo #3 in Pulung Bulu has been submitted and is awaiting approval', 'tenant', 1, '2025-05-13 14:40:47'),
(123, 'unread', 'rent', 36, 53, 'New rental application from Ajboy for condo 1 in balibago', 'landlord', 0, '2025-05-13 15:31:09'),
(124, 'unread', 'rent', 98, 53, 'Your rental application for condo 1 in balibago has been submitted and is awaiting approval', 'tenant', 0, '2025-05-13 15:31:09'),
(125, 'unread', 'visit', 36, 52, 'You have a new Visit Request from Ajboy for property 1 on 2025-04-24.', 'landlord', 0, '2025-05-13 15:31:16'),
(126, 'unread', 'visit', 98, 52, 'Your visit request for property 1 on 2025-04-24 has been submitted and is awaiting approval.', 'tenant', 0, '2025-05-13 15:31:16'),
(127, 'unread', 'rent', 98, 53, 'Your rental application for condo 1 in balibago has been APPROVED! You can now move forward with the rental process.', 'tenant', 0, '2025-05-13 15:31:44'),
(128, 'unread', 'rent', 36, 53, 'You have approved the rental application from Ajboy for condo 1 in balibago.', 'landlord', 0, '2025-05-13 15:31:44'),
(129, 'read', 'visit', 98, 52, 'Your visit request for property 1 on 2025-04-24 has been approved.', 'tenant', 1, '2025-05-13 15:32:02'),
(130, 'unread', 'rent', 36, 37, 'New rental application from Ajboy for 21012025 in as', 'landlord', 0, '2025-05-13 16:08:51'),
(131, 'unread', 'rent', 98, 37, 'Your rental application for 21012025 in as has been submitted and is awaiting approval', 'tenant', 0, '2025-05-13 16:08:51'),
(132, 'read', 'visit', 36, 51, 'You have a new Visit Request from Ajboy for 123 on 2025-04-24.', 'landlord', 1, '2025-05-13 16:09:00'),
(133, 'read', 'visit', 98, 51, 'Your visit request for 123 on 2025-04-24 has been submitted and is awaiting approval.', 'tenant', 1, '2025-05-13 16:09:00'),
(134, 'unread', 'rent', 94, NULL, 'New rental application from Ajboy for jim bu in adasd', 'landlord', 0, '2025-05-13 16:19:43'),
(135, 'read', 'rent', 98, NULL, 'Your rental application for jim bu in adasd has been submitted and is awaiting approval', 'tenant', 1, '2025-05-13 16:19:43'),
(136, 'unread', 'rent', 93, 24, 'New rental application from Ajboy for wad in adew', 'landlord', 0, '2025-05-13 16:21:48'),
(137, 'unread', 'rent', 98, 24, 'Your rental application for wad in adew has been submitted and is awaiting approval', 'tenant', 0, '2025-05-13 16:21:48'),
(138, 'unread', 'rent', 36, 52, 'New rental application from Ajboy for property 1 in cebu', 'landlord', 0, '2025-05-13 16:40:07'),
(139, 'unread', 'rent', 98, 52, 'Your rental application for property 1 in cebu has been submitted and is awaiting approval', 'tenant', 0, '2025-05-13 16:40:07'),
(140, 'unread', 'rent', 98, 52, 'Your rental application for property 1 in cebu has been APPROVED! You can now move forward and check My Rental pages.', 'tenant', 0, '2025-05-13 16:40:26'),
(141, 'unread', 'rent', 36, 52, 'You have approved the rental application from Ajboy for property 1 in cebu.', 'landlord', 0, '2025-05-13 16:40:26'),
(142, 'unread', 'rent', 36, 26, 'New rental application from Ajboy for Tano in angeles lang', 'landlord', 0, '2025-05-13 17:25:07'),
(143, 'unread', 'rent', 98, 26, 'Your rental application for Tano in angeles lang has been submitted and is awaiting approval', 'tenant', 0, '2025-05-13 17:25:07'),
(144, 'unread', 'rent', 95, 35, 'New rental application from retret2 for asd in asd', 'landlord', 0, '2025-05-13 18:19:20'),
(145, 'unread', 'rent', 31, 35, 'Your rental application for asd in asd has been submitted and is awaiting approval', 'tenant', 0, '2025-05-13 18:19:20'),
(146, 'unread', 'visit', 36, 51, 'You have a new Visit Request from retret2 for 123 on 2025-04-24.', 'landlord', 0, '2025-05-13 18:19:59'),
(147, 'unread', 'visit', 31, 51, 'Your visit request for 123 on 2025-04-24 has been submitted and is awaiting approval.', 'tenant', 0, '2025-05-13 18:19:59'),
(148, 'unread', 'rent', 36, 58, 'New rental application from retret2 for House 5 in Jeju Island', 'landlord', 0, '2025-05-13 19:11:31'),
(149, 'read', 'rent', 31, 58, 'Your rental application for House 5 in Jeju Island has been submitted and is awaiting approval', 'tenant', 1, '2025-05-13 19:11:31'),
(150, 'read', 'visit', 36, 58, 'You have a new Visit Request from retret2 for House 5 on 2025-05-18.', 'landlord', 1, '2025-05-13 19:11:40'),
(151, 'read', 'visit', 31, 58, 'Your visit request for House 5 on 2025-05-18 has been submitted and is awaiting approval.', 'tenant', 1, '2025-05-13 19:11:40'),
(152, 'read', 'rent', 36, 57, 'New rental application from retret2 for guagua in san andreas', 'landlord', 1, '2025-05-13 19:11:43'),
(153, 'read', 'rent', 31, 57, 'Your rental application for guagua in san andreas has been submitted and is awaiting approval', 'tenant', 1, '2025-05-13 19:11:43'),
(154, 'read', 'rent', 99, 65, 'New rental application from Beru for sad in asd', 'landlord', 1, '2025-05-13 21:49:11'),
(155, 'unread', 'rent', 100, 65, 'Your rental application for sad in asd has been submitted and is awaiting approval', 'tenant', 0, '2025-05-13 21:49:11'),
(156, 'read', 'visit', 99, 65, 'You have a new Visit Request from Beru for sad on 2025-05-30.', 'landlord', 1, '2025-05-13 21:49:23'),
(157, 'unread', 'visit', 100, 65, 'Your visit request for sad on 2025-05-30 has been submitted and is awaiting approval.', 'tenant', 0, '2025-05-13 21:49:23'),
(158, 'read', 'visit', 100, 65, 'Your visit request for sad on 2025-05-30 has been approved.', 'tenant', 1, '2025-05-13 21:49:54'),
(159, 'unread', 'rent', 100, 65, 'Your rental application for sad in asd has been APPROVED! You can now move forward and check My Rental pages.', 'tenant', 0, '2025-05-13 21:52:02'),
(160, 'read', 'rent', 99, 65, 'You have approved the rental application from Beru for sad in asd.', 'landlord', 1, '2025-05-13 21:52:02'),
(161, 'unread', 'rent', 36, 45, 'New rental application from Beru for 13 in 12312', 'landlord', 0, '2025-05-13 22:36:59'),
(162, 'unread', 'rent', 100, 45, 'Your rental application for 13 in 12312 has been submitted and is awaiting approval', 'tenant', 0, '2025-05-13 22:36:59'),
(163, 'unread', 'rent', 36, 51, 'New rental application from Beru for 123 in 123', 'landlord', 0, '2025-05-13 22:37:29'),
(164, 'unread', 'rent', 100, 51, 'Your rental application for 123 in 123 has been submitted and is awaiting approval', 'tenant', 0, '2025-05-13 22:37:29');

-- --------------------------------------------------------

--
-- Table structure for table `property_availability`
--

CREATE TABLE `property_availability` (
  `id` int(11) NOT NULL,
  `property_id` int(11) NOT NULL,
  `available_date` date NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `property_availability`
--

INSERT INTO `property_availability` (`id`, `property_id`, `available_date`) VALUES
(1, 45, '2025-04-23'),
(2, 45, '2025-04-25'),
(3, 51, '2025-04-24'),
(4, 52, '2025-04-24'),
(5, 53, '2025-04-25'),
(6, 53, '2025-04-26'),
(7, 53, '2025-04-27'),
(8, 53, '2025-04-28'),
(9, 53, '2025-04-29'),
(10, 54, '2025-04-26'),
(11, 54, '2025-05-12'),
(12, 54, '2025-04-22'),
(13, 54, '2025-04-29'),
(14, 55, '2025-04-26'),
(15, 55, '2025-04-27'),
(16, 55, '2025-04-28'),
(17, 56, '2025-05-20'),
(18, 56, '2025-05-21'),
(19, 56, '2025-05-22'),
(20, 56, '2025-05-23'),
(21, 57, '2025-05-28'),
(22, 57, '2025-05-29'),
(23, 58, '2025-05-15'),
(24, 58, '2025-05-16'),
(25, 58, '2025-05-18'),
(32, 65, '2025-05-30'),
(33, 66, '2025-05-28');

-- --------------------------------------------------------

--
-- Table structure for table `property_posts`
--

CREATE TABLE `property_posts` (
  `id` int(11) NOT NULL,
  `landlord_id` int(11) NOT NULL,
  `username` varchar(50) DEFAULT NULL,
  `title` varchar(255) NOT NULL,
  `description` text NOT NULL,
  `location` varchar(255) DEFAULT NULL,
  `bedroom` varchar(50) NOT NULL,
  `bathroom` varchar(50) NOT NULL,
  `area` varchar(100) NOT NULL,
  `property_type` varchar(50) NOT NULL,
  `availability` enum('Available','Rented') NOT NULL,
  `price` decimal(10,2) DEFAULT NULL,
  `image_path` varchar(255) DEFAULT NULL,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp(),
  `date_posted` datetime DEFAULT current_timestamp(),
  `profile_image` varchar(255) DEFAULT NULL,
  `year_built` varchar(10) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `property_posts`
--

INSERT INTO `property_posts` (`id`, `landlord_id`, `username`, `title`, `description`, `location`, `bedroom`, `bathroom`, `area`, `property_type`, `availability`, `price`, `image_path`, `created_at`, `date_posted`, `profile_image`, `year_built`) VALUES
(22, 93, 'rina', 'asd', 'asd', 'asd', '', '', '', '0', 'Available', 123.00, 'uploads/property_images/1744358946.jpg', '2025-04-11 08:09:06', '2025-04-11 00:00:00', 'uploads/user_profiles/user_93_1744867804.jpg', NULL),
(23, 93, 'rina', 'asd', 'asd', 'asd', '', '', '', '0', 'Available', 123.00, 'uploads/property_images/1744358950.jpg', '2025-04-11 08:09:10', '2025-04-11 00:00:00', 'uploads/user_profiles/user_93_1744867804.jpg', NULL),
(24, 93, 'rina', 'wad', 'adas', 'adew', '', '', '', '0', 'Available', 555555.00, 'uploads/property_images/1744359657.jpg', '2025-04-11 08:20:57', '2025-04-11 00:00:00', 'uploads/user_profiles/user_93_1744867804.jpg', NULL),
(26, 36, 'Yoshiken', 'Tano', 'hehehhe', 'angeles lang', '', '', '', '0', 'Available', 9994545.00, 'uploads/property_images/1744420219.jpg', '2025-04-12 01:10:19', '2025-04-12 00:00:00', 'uploads/user_profiles/user_36_1744853297.jpg', NULL),
(35, 95, 'JmGravoso', 'asd', 'asd', 'asd', '', '', '', '0', 'Available', 1254.00, 'uploads/property_images/1744699432.jpg', '2025-04-15 06:43:52', '2025-04-15 14:43:52', 'uploads/user_profiles/user_95_1747129797.jpg', NULL),
(36, 36, 'Yoshiken', 'arigato1', 'arigato123', 'aritago1', '13', '22', '34', 'Condo', 'Available', 1.00, 'uploads/property_images/1744858096.jpg', '2025-04-17 02:48:16', '2025-04-17 10:48:16', 'uploads/user_profiles/user_36_1744853297.jpg', '4'),
(37, 36, 'Yoshiken', '21012025', '221222', 'as', '123', '423', '323', 'Condo', 'Available', 200220.00, 'uploads/property_images/1745231495.jpg', '2025-04-21 10:31:35', '2025-04-21 18:31:35', 'uploads/user_profiles/user_36_1744853297.jpg', '345'),
(38, 36, 'Yoshiken', '123123213123', '12321312312', '3213123123', '13213', '12312', '12321', 'Condo', 'Available', 99999999.99, 'uploads/property_images/1745231910.jpg', '2025-04-21 10:38:30', '2025-04-21 18:38:30', 'uploads/user_profiles/user_36_1744853297.jpg', '21312'),
(45, 36, 'Yoshiken', '13', '13', '12312', '21', '123', '232', 'Apartment', 'Available', 123.00, 'uploads/property_images/1745313982.jpg', '2025-04-22 09:26:22', '2025-04-22 17:26:22', 'uploads/user_profiles/user_36_1744853297.jpg', '323'),
(51, 36, 'Yoshiken', '123', '123', '123', '123', '1232', '213', 'Condo', 'Available', 123.00, 'uploads/property_images/1745330662.jpg', '2025-04-22 14:04:22', '2025-04-22 22:04:22', 'uploads/user_profiles/user_36_1744853297.jpg', '123'),
(52, 36, 'Yoshiken', 'property 1', 'wd', 'cebu', '12312', '123', '12312', 'Condo', 'Rented', 123123.00, 'uploads/property_images/1745330980.jpg', '2025-04-22 14:09:40', '2025-04-22 22:09:40', 'uploads/user_profiles/user_36_1744853297.jpg', '12312'),
(53, 36, 'Yoshiken', 'condo 1', 'condo 1', 'balibago', '3', '3', '30', 'Condo', 'Rented', 6000.00, 'uploads/property_images/1745487305.jpg', '2025-04-24 09:35:05', '2025-04-24 17:35:05', 'uploads/user_profiles/user_36_1744853297.jpg', '2020'),
(54, 36, 'Yoshiken', 'Condo #2', 'Condo', 'balibago', '4', '2', '25', 'Condo', 'Rented', 8000.00, 'uploads/property_images/1745577055.jpg', '2025-04-25 10:30:55', '2025-04-25 18:30:55', 'uploads/user_profiles/user_36_1744853297.jpg', '2020'),
(55, 93, 'rina', 'Condo #3', 'condo', 'Pulung Bulu', '2', '1', '20', 'Condo', 'Rented', 4000.00, 'uploads/property_images/1745577235.jpg', '2025-04-25 10:33:55', '2025-04-25 18:33:55', 'uploads/user_profiles/user_93_1744867804.jpg', '2023'),
(56, 97, 'Yoshiro123', 'House #10', 'bahay bahay', 'pampanga', '1', '1', '32', 'House', 'Rented', 1000.00, 'uploads/property_images/1747047489.jpg', '2025-05-12 10:58:09', '2025-05-12 18:58:09', 'uploads/user_profiles/default_profile.jpg', '2026'),
(57, 36, 'Yoshiken', 'guagua', 'malapit medyo lakad', 'san andreas', '1', '2', '3', 'Villa', 'Available', 699.00, 'uploads/property_images/1747131781.jpg', '2025-05-13 10:23:01', '2025-05-13 18:23:01', 'uploads/user_profiles/user_36_1744853297.jpg', '2001'),
(58, 36, 'Yoshiken', 'House 5', 'House 5', 'Jeju Island', '4', '4', '23', 'House', 'Available', 999.00, 'uploads/property_images/1747131898.jpg', '2025-05-13 10:24:58', '2025-05-13 18:24:58', 'uploads/user_profiles/user_36_1744853297.jpg', '2023'),
(65, 99, 'Igris', 'sad', 'asd', 'asd', '1231', '213', '21', 'Villa', 'Rented', 213.00, 'uploads/property_images/1747144129.jpg', '2025-05-13 13:48:49', '2025-05-13 21:48:49', 'uploads/user_profiles/default_profile.jpg', '213'),
(66, 101, 'Chae', 'testingq', 'testing', 'testing', '312', '213', '23', 'House', 'Available', 1215.00, 'uploads/property_images/1747144637.jpg', '2025-05-13 13:57:17', '2025-05-13 21:57:17', 'uploads/user_profiles/default_profile.jpg', '213');

-- --------------------------------------------------------

--
-- Table structure for table `property_rented`
--

CREATE TABLE `property_rented` (
  `id` int(11) NOT NULL,
  `property_id` int(11) DEFAULT NULL,
  `tenant_id` int(11) DEFAULT NULL,
  `rented_at` datetime DEFAULT current_timestamp(),
  `title` varchar(255) DEFAULT NULL,
  `price` double DEFAULT NULL,
  `location` varchar(255) DEFAULT NULL,
  `tenant_name` varchar(255) DEFAULT NULL,
  `landlord_name` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `property_rented`
--

INSERT INTO `property_rented` (`id`, `property_id`, `tenant_id`, `rented_at`, `title`, `price`, `location`, `tenant_name`, `landlord_name`) VALUES
(4, 53, 98, '2025-05-13 15:31:44', 'condo 1', 6000, 'balibago', 'Ajboy', 'Yoshiken'),
(5, 52, 98, '2025-05-13 16:40:26', 'property 1', 123123, 'cebu', 'Ajboy', 'Yoshiken'),
(6, 65, 100, '2025-05-13 21:52:02', 'sad', 213, 'asd', 'Beru', 'Sung Jin Woo');

-- --------------------------------------------------------

--
-- Table structure for table `rental_applications`
--

CREATE TABLE `rental_applications` (
  `id` int(11) NOT NULL,
  `tenant_id` int(11) DEFAULT NULL,
  `property_id` int(11) DEFAULT NULL,
  `contact_number` varchar(20) DEFAULT NULL,
  `status` enum('pending','approved','rejected') DEFAULT 'pending',
  `created_at` datetime DEFAULT current_timestamp(),
  `updated_at` datetime DEFAULT current_timestamp() ON UPDATE current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `rental_applications`
--

INSERT INTO `rental_applications` (`id`, `tenant_id`, `property_id`, `contact_number`, `status`, `created_at`, `updated_at`) VALUES
(53, 98, 53, 'Not provided', 'approved', '2025-05-13 15:31:09', '2025-05-13 15:31:44'),
(57, 98, 52, 'Not provided', 'approved', '2025-05-13 16:40:07', '2025-05-13 16:40:26'),
(58, 98, 26, 'Not provided', 'pending', '2025-05-13 17:25:07', '2025-05-13 17:25:07'),
(60, 31, 58, 'Not provided', 'pending', '2025-05-13 19:11:31', '2025-05-13 19:11:31'),
(62, 100, 65, 'Not provided', 'approved', '2025-05-13 21:49:11', '2025-05-13 21:52:02'),
(63, 100, 45, 'Not provided', 'pending', '2025-05-13 22:36:59', '2025-05-13 22:36:59'),
(64, 100, 51, 'Not provided', 'pending', '2025-05-13 22:37:29', '2025-05-13 22:37:29');

-- --------------------------------------------------------

--
-- Table structure for table `tableuser`
--

CREATE TABLE `tableuser` (
  `id` int(11) NOT NULL,
  `username` varchar(100) NOT NULL,
  `role` enum('tenant','landlord') NOT NULL,
  `password` varchar(100) NOT NULL,
  `status` enum('Pending','Verified','Rejected') NOT NULL DEFAULT 'Pending',
  `profile` varchar(255) NOT NULL,
  `profile_image` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `tableuser`
--

INSERT INTO `tableuser` (`id`, `username`, `role`, `password`, `status`, `profile`, `profile_image`) VALUES
(30, 'User', 'tenant', '$2y$10$RL0W8VzuWbvT6GJhY3hJ6.kDdK7AJfBAH1U39Mo2uw1dOmcRTE.w6', 'Verified', '', NULL),
(31, 'retret', 'tenant', '$2y$10$.d7HMoL.V8RF/eolXfQjMOdk6qzPBTKFvitmM2ngKw80JYk79E1qC', 'Verified', '', 'uploads/user_profiles/tenant_31_1746546413.jpg'),
(32, 'letlet', 'landlord', '$2y$10$Y..1zrKBETgXPVwWOa0zzeSTFLPy0ixsHtW5jHRuMstnUcriihWpa', 'Verified', '', NULL),
(33, 'erich', 'tenant', '$2y$10$KWyzjL9gUngRmAUTuzA17ujupMBkeIbjpS.Al2qH3VrWDiyT8dLzy', 'Verified', '', NULL),
(34, 'letlet1', 'landlord', '$2y$10$z3DXlR/mYG0Jfexun0g8z.WrW.ogrcgnIrsEkEENvmau/.cC/K6FO', 'Verified', '', NULL),
(36, 'Yoshiken', 'landlord', '$2y$10$/pdsYg66O87WuhCVqhTGiOyLI/BGNvlBgvQ1m4Zik3Qy4jc39KMpK', 'Verified', '', 'uploads/user_profiles/user_36_1744853297.jpg'),
(39, '1234', 'tenant', '$2y$10$UddVmLt47kZpyRgT8EL7u.JMttdbvMmguOun564pMavM5lBMS2HRK', 'Verified', '', NULL),
(40, 'Jimmy', 'landlord', '$2y$10$ydCc.Jkr4k0H0vKAQQfSfe78DAc.pNIn7A4dUM77Tud10qfBG8t72', 'Verified', '', NULL),
(41, 'nina', 'landlord', '$2y$10$Jsqp2eU/RHUqst9esx.u4eUllvypxbdybVXUYXUklEeo/s7THOk5a', 'Verified', '', NULL),
(42, 'samsung', 'landlord', '$2y$10$yZ4BWPyb1P6kQEdkYJjPe.Wtzw5IiTdg7.2Y3NVs8ZVUC6waHhznm', 'Verified', '', NULL),
(44, 'Nice', 'landlord', '$2y$10$.t1VHREPTQz/byCPJ.5mKO.ExHdJqvSh/1hWM3yI8tZmOjrA7OV1.', 'Rejected', '', NULL),
(45, 'admin123', 'landlord', '$2y$10$hCTNJ1G9m3jUROsTUSFIGuxCR7oxE0BNO9yNk8ey2TgS6lL2t1BwK', 'Verified', '', NULL),
(47, 'Lester', 'landlord', '$2y$10$PVmxxbBj3BGbpUkkOeIyNeTeeH7dpiThCv9w2u6DFCIK5m12D5M0W', 'Rejected', '', NULL),
(48, 'ga', 'tenant', '$2y$10$9G9hcojvXeaL4G9enaerP.pfeoqNNlhwUwyEDgVADCNAQuS.IHxOe', 'Verified', '', NULL),
(50, 'hehe', 'landlord', '$2y$10$EToB08t52goYXYt1KKaATuC8GU2s/.s2HwL.Rd84p8zUpuSHORFqC', 'Rejected', '', NULL),
(52, 'asd', 'tenant', '$2y$10$1YhZwXnlFjN0u5ZxBWZIee5a/DRlL/nWnRF75K8.p6jc.Fgb8a7iu', 'Verified', '', NULL),
(53, 'lan', 'landlord', '$2y$10$HMC3vs3AnBdB441U6DpkSurs19G9KQPMT.7KF0uQg1YJjR2fFgvI6', 'Verified', '', NULL),
(54, 'nick', 'tenant', '$2y$10$qmhYLK1mklaWJ0s4PnXgoOlAeekfyJmfHd.lf7rX7xoDCiIQ7CMrO', 'Verified', '', NULL),
(56, 'kesug', 'tenant', '$2y$10$E8gQPi2n9IDxLe3QbJS2ZuHS6yYb7PrVxGMo3fw3AvbVU7rsPGYEC', 'Verified', '', NULL),
(57, 'adminkesug', 'landlord', '$2y$10$rPKQiHeykVfgCP.9/wfiGOXaMpaIzCW77o.bcpD9qSZ/1HRpSHH1K', 'Verified', '', NULL),
(58, 'admin11', 'landlord', '$2y$10$/kHZnnqdb6moKANivxix3u/xvshWQfWaflK2VdBllsUBBmPjMa5KG', 'Rejected', '', NULL),
(59, 'yosss', 'landlord', '$2y$10$bM0KJifkdSQbAEJNV4tr/upg08H6AHQK5btmG2Td96ZhT1d/pj3DS', 'Verified', '', NULL),
(60, 'Yoshiroken', 'tenant', '$2y$10$ii35HLMBFsJm5AvnKfdGi.JtpF6FAYMiCsyp8/qmhwUuPIDAnTUV2', 'Verified', '', NULL),
(61, 'yoshiadmin', 'tenant', '$2y$10$uHFV5fAVb6N5n8teeBDP8O7DwdDIUvhfdRJYHgl8vLJepQ/4CR09a', 'Verified', '', NULL),
(62, 'Yoshikenadmin', 'landlord', '$2y$10$qzu45q2cQh9oHPgVFnM2P.MeCX0dsA52TRoXsN70KLoxrgKYDBz/e', 'Pending', '', NULL),
(91, 'yoshikesug', 'landlord', '$2y$10$D74xB05Tws218HSwog7VCuLrCFJQOOjLVxPkUqoiWVMfOfCaRX.NW', 'Verified', '', NULL),
(92, 'Jeff', 'landlord', '$2y$10$ShoRNUhmtVTqPt.sqLK7ceOXuyzeRlZtCQCEitBSWlhv3Sx.n.lFm', 'Verified', '', NULL),
(93, 'rina', 'landlord', '$2y$10$UNJq.aamwYOuGry.KDUjxOEh0sME8YCUxXhPfwEv50TgRd2mRY04y', 'Verified', '', 'uploads/user_profiles/user_93_1744867804.jpg'),
(95, 'JmGravoso', 'landlord', '$2y$10$.7zrtcbY1euHkp1NmjNiI.UH6mlAnW3fzb9sZ5wbUxxW6Q3dG.i3W', 'Verified', '', 'uploads/user_profiles/user_95_1747129797.jpg'),
(96, 'josh', 'tenant', '$2y$10$LuQYqkrNta9HAzWdk8CFl.IoD6fJnzOgAtdhcNOaFJWUyTACIy6Qm', 'Verified', '', NULL),
(97, 'Yoshiro123', 'landlord', '$2y$10$6hIMNn48ijZxpNLXP1KfOOu08FmmL..4hcK9GAxmDEXuxla3yYItG', 'Verified', '', NULL),
(98, 'Aj123', 'tenant', '$2y$10$iX1g5DsNUJwrKK5mxmky7.uAk2zp8ItD0ZZnlpoGjb7JpFS85y5qq', 'Verified', '', 'uploads/user_profiles/tenant_98_1747118276.jpg'),
(99, 'Igris', 'landlord', '$2y$10$NCKjSpe5V0uPjCyIPsbeu.98qbbJrPzvcbNm1mi8S3ObnvmUgVTme', 'Verified', '', NULL),
(100, 'Beru', 'tenant', '$2y$10$iRL0LYiNlRiiiO36GPQIA.6wkQEOl7.dZq2JgJ7zLEisHaLm04Pn2', 'Verified', '', 'uploads/user_profiles/tenant_100_1747144054.jpg'),
(101, 'Chae', 'landlord', '$2y$10$fikQKXGV0K7h1QGq8L8N.eGOgD4XA1iFy4kCjunXb5GGwuVoVNbHu', 'Verified', '', NULL);

-- --------------------------------------------------------

--
-- Table structure for table `tenant_details`
--

CREATE TABLE `tenant_details` (
  `id` int(11) NOT NULL,
  `user_id` int(11) NOT NULL,
  `fullname` varchar(255) NOT NULL,
  `sex` varchar(10) DEFAULT NULL,
  `email` varchar(255) DEFAULT NULL,
  `phone` varchar(20) NOT NULL,
  `birthday` date DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `tenant_details`
--

INSERT INTO `tenant_details` (`id`, `user_id`, `fullname`, `sex`, `email`, `phone`, `birthday`) VALUES
(0, 31, 'retret', 'Female', 'retret1@gmail.com', '096912345679', '1999-04-24'),
(0, 96, 'josh', 'Male', 'asd', '0212556315664', '2025-04-15'),
(0, 98, 'Ajboy', 'Male', 'ajboy@github.com', '094546452131', '2025-05-01'),
(0, 100, 'Beru', 'Male', 'Beru@gmail.com', '1234', '2025-05-01');

-- --------------------------------------------------------

--
-- Table structure for table `visit_requests`
--

CREATE TABLE `visit_requests` (
  `id` int(11) NOT NULL,
  `tenant_id` int(11) NOT NULL,
  `property_id` int(11) NOT NULL,
  `visit_date` varchar(20) NOT NULL,
  `status` enum('pending','approved','rejected') NOT NULL DEFAULT 'pending',
  `created_at` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `visit_requests`
--

INSERT INTO `visit_requests` (`id`, `tenant_id`, `property_id`, `visit_date`, `status`, `created_at`) VALUES
(56, 98, 52, '2025-04-24', 'approved', '2025-05-13 07:31:16'),
(59, 31, 58, '2025-05-18', 'pending', '2025-05-13 11:11:40'),
(60, 100, 65, '2025-05-30', 'approved', '2025-05-13 13:49:23');

--
-- Indexes for dumped tables
--

--
-- Indexes for table `messages`
--
ALTER TABLE `messages`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `notifications`
--
ALTER TABLE `notifications`
  ADD PRIMARY KEY (`id`),
  ADD KEY `fk_notifications_property` (`property_id`);

--
-- Indexes for table `property_availability`
--
ALTER TABLE `property_availability`
  ADD PRIMARY KEY (`id`),
  ADD KEY `property_id` (`property_id`);

--
-- Indexes for table `property_posts`
--
ALTER TABLE `property_posts`
  ADD PRIMARY KEY (`id`),
  ADD KEY `fk_landlord_id` (`landlord_id`);

--
-- Indexes for table `property_rented`
--
ALTER TABLE `property_rented`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `rental_applications`
--
ALTER TABLE `rental_applications`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `tableuser`
--
ALTER TABLE `tableuser`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `visit_requests`
--
ALTER TABLE `visit_requests`
  ADD PRIMARY KEY (`id`),
  ADD KEY `tenant_id` (`tenant_id`),
  ADD KEY `property_id` (`property_id`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `messages`
--
ALTER TABLE `messages`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `notifications`
--
ALTER TABLE `notifications`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=165;

--
-- AUTO_INCREMENT for table `property_availability`
--
ALTER TABLE `property_availability`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=34;

--
-- AUTO_INCREMENT for table `property_posts`
--
ALTER TABLE `property_posts`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=67;

--
-- AUTO_INCREMENT for table `property_rented`
--
ALTER TABLE `property_rented`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=7;

--
-- AUTO_INCREMENT for table `rental_applications`
--
ALTER TABLE `rental_applications`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=65;

--
-- AUTO_INCREMENT for table `tableuser`
--
ALTER TABLE `tableuser`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=102;

--
-- AUTO_INCREMENT for table `visit_requests`
--
ALTER TABLE `visit_requests`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=61;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `notifications`
--
ALTER TABLE `notifications`
  ADD CONSTRAINT `fk_notifications_property` FOREIGN KEY (`property_id`) REFERENCES `property_posts` (`id`) ON DELETE SET NULL;

--
-- Constraints for table `property_availability`
--
ALTER TABLE `property_availability`
  ADD CONSTRAINT `property_availability_ibfk_1` FOREIGN KEY (`property_id`) REFERENCES `property_posts` (`id`) ON DELETE CASCADE;

--
-- Constraints for table `property_posts`
--
ALTER TABLE `property_posts`
  ADD CONSTRAINT `fk_landlord_id` FOREIGN KEY (`landlord_id`) REFERENCES `tableuser` (`id`) ON DELETE CASCADE;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
