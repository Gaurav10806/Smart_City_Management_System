-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: Aug 16, 2025 at 03:09 PM
-- Server version: 10.4.32-MariaDB
-- PHP Version: 8.2.12
    
SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `smart_city`
--

-- --------------------------------------------------------

--
-- Table structure for table `officer`
--

CREATE TABLE `officer` (
  `username` varchar(50) NOT NULL,
  `password` varchar(50) NOT NULL,
  `category` varchar(50) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `officer`
--

INSERT INTO `officer` (`username`, `password`, `category`) VALUES
('ajoshi_wat', 'pass', 'water'),
('amehta_roa', 'pass', 'road maintanence'),
('ayadav_dra', 'pass', 'drainage'),
('ayadav_ele', 'pass', 'electricity'),
('bsingh_roa', 'pass', 'road maintanence'),
('cdesai_roa', 'pass', 'road maintanence'),
('dev', 'dev123', 'drainage'),
('dghosh_wat', 'pass', 'water'),
('drao_oth', 'pass', 'other'),
('dyadav_oth', 'pass', 'other'),
('ggupta_wat', 'pass', 'water'),
('nkulkarni_ele', 'pass', 'electricity'),
('nmehta_wat', 'pass', 'water'),
('pmehta_oth', 'pass', 'other'),
('prao_dra', 'pass', 'drainage'),
('psharma_oth', 'pass', 'other'),
('rdesai_roa', 'pass', 'road maintanence'),
('riyer_oth', 'pass', 'other'),
('rkulkarni_dra', 'pass', 'drainage'),
('rverma_dra', 'pass', 'drainage'),
('sghosh_dra', 'pass', 'drainage'),
('spatel_wat', 'pass', 'water'),
('syadav_ele', 'pass', 'electricity'),
('vthakur_ele', 'pass', 'electricity');

-- --------------------------------------------------------

--
-- Table structure for table `user`
--

CREATE TABLE `user` (
  `username` varchar(50) NOT NULL,
  `password` varchar(50) NOT NULL,
  `email` varchar(100) NOT NULL,
  `address` varchar(300) DEFAULT NULL,
  `area` varchar(50) NOT NULL,
  `category` varchar(50) DEFAULT NULL,
  `complain` varchar(255) DEFAULT NULL,
  `complain_id` int(11) NOT NULL,
  `status` varchar(50) DEFAULT NULL,
  `assigned_officer` varchar(50) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `user`
--

INSERT INTO `user` 
(`username`, `password`, `email`, `address`, `area`, `category`, `complain`, `complain_id`, `status`, `assigned_officer`) VALUES
('user1', 'pass1001', 'user1@example.com', '6 Arjun Nivas, Near V S Hospital, Ellisbridge, Ahmedabad, 380006', 'Ellisbridge', 'electricity', 'Complaint 1001 about electricity in Ellisbridge', 1001, 'Pending', 'ayadav_ele'),
('user2', 'pass1002', 'user2@example.com', 'Maninagar Post Office, Ahmedabad, 380008', 'Maninagar', 'road maintanence', 'Complaint 1002 about road maintanence in Maninagar', 1002, 'Pending', 'rdesai_roa'),
('user3', 'pass1003', 'user3@example.com', '6 Arjun Nivas, Near V S Hospital, Ellisbridge, Ahmedabad, 380006', 'Ellisbridge', 'drainage', 'Complaint 1003 about drainage in Ellisbridge', 1003, 'Pending', 'ayadav_dra'),
('user4', 'pass1004', 'user4@example.com', 'Paldi Post Office, Ahmedabad, 380007', 'Paldi', 'other', 'Complaint 1004 about other in Paldi', 1004, 'Pending', 'drao_oth'),
('user5', 'pass1005', 'user5@example.com', 'Paldi Post Office, Ahmedabad, 380007', 'Paldi', 'electricity', 'Complaint 1005 about electricity in Paldi', 1005, 'Pending', 'vthakur_ele'),
('user6', 'pass1006', 'user6@example.com', 'Paldi Post Office, Ahmedabad, 380007', 'Paldi', 'other', 'Complaint 1006 about other in Paldi', 1006, 'Pending', 'drao_oth'),
('user7', 'pass1007', 'user7@example.com', '6 Arjun Nivas, Near V S Hospital, Ellisbridge, Ahmedabad, 380006', 'Ellisbridge', 'road maintanence', 'Complaint 1007 about road maintanence in Ellisbridge', 1007, 'Pending', 'amehta_roa'),
('user8', 'pass1008', 'user8@example.com', 'Paldi Post Office, Ahmedabad, 380007', 'Paldi', 'drainage', 'Complaint 1008 about drainage in Paldi', 1008, 'Pending', 'rkulkarni_dra'),
('user9', 'pass1009', 'user9@example.com', 'Maninagar Post Office, Ahmedabad, 380008', 'Maninagar', 'road maintanence', 'Complaint 1009 about road maintanence in Maninagar', 1009, 'Pending', 'rdesai_roa'),
('user10', 'pass1010', 'user10@example.com', '6 Arjun Nivas, Near V S Hospital, Ellisbridge, Ahmedabad, 380006', 'Ellisbridge', 'drainage', 'Complaint 1010 about drainage in Ellisbridge', 1010, 'Pending', 'ayadav_dra'),
('user11', 'pass1011', 'user11@example.com', 'Paldi Post Office, Ahmedabad, 380007', 'Paldi', 'other', 'Complaint 1011 about other in Paldi', 1011, 'Pending', 'drao_oth'),
('user12', 'pass1012', 'user12@example.com', '6 Arjun Nivas, Near V S Hospital, Ellisbridge, Ahmedabad, 380006', 'Ellisbridge', 'water', 'Complaint 1012 about water in Ellisbridge', 1012, 'Pending', 'ggupta_wat'),
('user13', 'pass1013', 'user13@example.com', 'Maninagar Post Office, Ahmedabad, 380008', 'Maninagar', 'water', 'Complaint 1013 about water in Maninagar', 1013, 'Pending', 'dghosh_wat'),
('user14', 'pass1014', 'user14@example.com', 'Navrangpura Head Post Office, Ahmedabad, 380009', 'Navrangpura', 'water', 'Complaint 1014 about water in Navrangpura', 1014, 'Pending', 'spatel_wat'),
('user15', 'pass1015', 'user15@example.com', 'Post Office, Bapunagar, Ahmedabad, 380024', 'Bapunagar', 'electricity', 'Complaint 1015 about electricity in Bapunagar', 1015, 'Pending', 'nkulkarni_ele'),
('user16', 'pass1016', 'user16@example.com', 'Post Office, Bapunagar, Ahmedabad, 380024', 'Bapunagar', 'drainage', 'Complaint 1016 about drainage in Bapunagar', 1016, 'Pending', 'prao_dra'),
('user17', 'pass1017', 'user17@example.com', 'Navrangpura Head Post Office, Ahmedabad, 380009', 'Navrangpura', 'road maintanence', 'Complaint 1017 about road maintanence in Navrangpura', 1017, 'Pending', 'bsingh_roa'),
('user18', 'pass1018', 'user18@example.com', 'Maninagar Post Office, Ahmedabad, 380008', 'Maninagar', 'other', 'Complaint 1018 about other in Maninagar', 1018, 'Pending', 'psharma_oth'),
('user19', 'pass1019', 'user19@example.com', 'Paldi Post Office, Ahmedabad, 380007', 'Paldi', 'electricity', 'Complaint 1019 about electricity in Paldi', 1019, 'Pending', 'vthakur_ele'),
('user20', 'pass1020', 'user20@example.com', 'Post Office, Bapunagar, Ahmedabad, 380024', 'Bapunagar', 'drainage', 'Complaint 1020 about drainage in Bapunagar', 1020, 'Pending', 'prao_dra'),
('user21', 'pass1021', 'user21@example.com', 'Navrangpura Head Post Office, Ahmedabad, 380009', 'Navrangpura', 'road maintanence', 'Complaint 1021 about road maintanence in Navrangpura', 1021, 'Pending', 'bsingh_roa'),
('user22', 'pass1022', 'user22@example.com', 'Maninagar Post Office, Ahmedabad, 380008', 'Maninagar', 'drainage', 'Complaint 1022 about drainage in Maninagar', 1022, 'Pending', 'rverma_dra'),
('user23', 'pass1023', 'user23@example.com', 'Post Office, Bapunagar, Ahmedabad, 380024', 'Bapunagar', 'electricity', 'Complaint 1023 about electricity in Bapunagar', 1023, 'Pending', 'nkulkarni_ele'),
('user24', 'pass1024', 'user24@example.com', 'Post Office, Bapunagar, Ahmedabad, 380024', 'Bapunagar', 'electricity', 'Complaint 1024 about electricity in Bapunagar', 1024, 'Pending', 'nkulkarni_ele'),
('user25', 'pass1025', 'user25@example.com', 'Paldi Post Office, Ahmedabad, 380007', 'Paldi', 'drainage', 'Complaint 1025 about drainage in Paldi', 1025, 'Pending', 'rkulkarni_dra'),
('user26', 'pass1026', 'user26@example.com', 'Maninagar Post Office, Ahmedabad, 380008', 'Maninagar', 'electricity', 'Complaint 1026 about electricity in Maninagar', 1026, 'Pending', 'vthakur_ele'),
('user27', 'pass1027', 'user27@example.com', 'Maninagar Post Office, Ahmedabad, 380008', 'Maninagar', 'road maintanence', 'Complaint 1027 about road maintanence in Maninagar', 1027, 'Pending', 'rdesai_roa'),
('user28', 'pass1028', 'user28@example.com', 'Navrangpura Head Post Office, Ahmedabad, 380009', 'Navrangpura', 'other', 'Complaint 1028 about other in Navrangpura', 1028, 'Pending', 'riyer_oth'),
('user29', 'pass1029', 'user29@example.com', 'Maninagar Post Office, Ahmedabad, 380008', 'Maninagar', 'drainage', 'Complaint 1029 about drainage in Maninagar', 1029, 'Pending', 'rverma_dra'),
('user30', 'pass1030', 'user30@example.com', '6 Arjun Nivas, Near V S Hospital, Ellisbridge, Ahmedabad, 380006', 'Ellisbridge', 'other', 'Complaint 1030 about other in Ellisbridge', 1030, 'Pending', 'pmehta_oth'),
('user31', 'pass1031', 'user31@example.com', '6 Arjun Nivas, Near V S Hospital, Ellisbridge, Ahmedabad, 380006', 'Ellisbridge', 'drainage', 'Complaint 1031 about drainage in Ellisbridge', 1031, 'Pending', 'ayadav_dra'),
('user32', 'pass1032', 'user32@example.com', 'Maninagar Post Office, Ahmedabad, 380008', 'Maninagar', 'other', 'Complaint 1032 about other in Maninagar', 1032, 'Pending', 'psharma_oth'),
('user33', 'pass1033', 'user33@example.com', 'Navrangpura Head Post Office, Ahmedabad, 380009', 'Navrangpura', 'drainage', 'Complaint 1033 about drainage in Navrangpura', 1033, 'Pending', 'sghosh_dra'),
('user34', 'pass1034', 'user34@example.com', '6 Arjun Nivas, Near V S Hospital, Ellisbridge, Ahmedabad, 380006', 'Ellisbridge', 'road maintanence', 'Complaint 1034 about road maintanence in Ellisbridge', 1034, 'Resolved', 'amehta_roa'),
('user35', 'pass1035', 'user35@example.com', 'Navrangpura Head Post Office, Ahmedabad, 380009', 'Navrangpura', 'water', 'Complaint 1035 about water in Navrangpura', 1035, 'Pending', 'spatel_wat'),
('user36', 'pass1036', 'user36@example.com', 'Paldi Post Office, Ahmedabad, 380007', 'Paldi', 'electricity', 'Complaint 1036 about electricity in Paldi', 1036, 'Pending', 'vthakur_ele'),
('user37', 'pass1037', 'user37@example.com', '6 Arjun Nivas, Near V S Hospital, Ellisbridge, Ahmedabad, 380006', 'Ellisbridge', 'electricity', 'Complaint 1037 about electricity in Ellisbridge', 1037, 'Pending', 'ayadav_ele'),
('user38', 'pass1038', 'user38@example.com', '6 Arjun Nivas, Near V S Hospital, Ellisbridge, Ahmedabad, 380006', 'Ellisbridge', 'other', 'Complaint 1038 about other in Ellisbridge', 1038, 'Pending', 'pmehta_oth'),
('user39', 'pass1039', 'user39@example.com', 'Post Office, Bapunagar, Ahmedabad, 380024', 'Bapunagar', 'road maintanence', 'Complaint 1039 about road maintanence in Bapunagar', 1039, 'Pending', 'cdesai_roa'),
('user40', 'pass1040', 'user40@example.com', 'Post Office, Bapunagar, Ahmedabad, 380024', 'Bapunagar', 'drainage', 'Complaint 1040 about drainage in Bapunagar', 1040, 'Pending', 'prao_dra'),
('user41', 'pass1041', 'user41@example.com', '6 Arjun Nivas, Near V S Hospital, Ellisbridge, Ahmedabad, 380006', 'Ellisbridge', 'other', 'Complaint 1041 about other in Ellisbridge', 1041, 'Pending', 'pmehta_oth'),
('user42', 'pass1042', 'user42@example.com', 'Paldi Post Office, Ahmedabad, 380007', 'Paldi', 'road maintanence', 'Complaint 1042 about road maintanence in Paldi', 1042, 'Pending', 'amehta_roa'),
('user43', 'pass1043', 'user43@example.com', 'Paldi Post Office, Ahmedabad, 380007', 'Paldi', 'other', 'Complaint 1043 about other in Paldi', 1043, 'Pending', 'drao_oth'),
('user44', 'pass1044', 'user44@example.com', 'Paldi Post Office, Ahmedabad, 380007', 'Paldi', 'electricity', 'Complaint 1044 about electricity in Paldi', 1044, 'Pending', 'vthakur_ele'),
('user45', 'pass1045', 'user45@example.com', '6 Arjun Nivas, Near V S Hospital, Ellisbridge, Ahmedabad, 380006', 'Ellisbridge', 'electricity', 'Complaint 1045 about electricity in Ellisbridge', 1045, 'Pending', 'ayadav_ele'),
('user46', 'pass1046', 'user46@example.com', '6 Arjun Nivas, Near V S Hospital, Ellisbridge, Ahmedabad, 380006', 'Ellisbridge', 'drainage', 'Complaint 1046 about drainage in Ellisbridge', 1046, 'Pending', 'ayadav_dra'),
('user47', 'pass1047', 'user47@example.com', '6 Arjun Nivas, Near V S Hospital, Ellisbridge, Ahmedabad, 380006', 'Ellisbridge', 'drainage', 'Complaint 1047 about drainage in Ellisbridge', 1047, 'Pending', 'ayadav_dra'),
('user48', 'pass1048', 'user48@example.com', '6 Arjun Nivas, Near V S Hospital, Ellisbridge, Ahmedabad, 380006', 'Ellisbridge', 'road maintanence', 'Complaint 1048 about road maintanence in Ellisbridge', 1048, 'Resolved', 'amehta_roa'),
('user49', 'pass1049', 'user49@example.com', 'Navrangpura Head Post Office, Ahmedabad, 380009', 'Navrangpura', 'other', 'Complaint 1049 about other in Navrangpura', 1049, 'Pending', 'riyer_oth'),
('user50', 'pass1050', 'user50@example.com', 'Navrangpura Head Post Office, Ahmedabad, 380009', 'Navrangpura', 'water', 'Complaint 1050 about water in Navrangpura', 1050, 'Pending', 'spatel_wat'),
('gaurav3', 'gau123', 'gaurav@gmail.com', 'Paldi Post Office, Ahmedabad, 380007', 'Paldi', NULL, NULL, 1051, NULL, NULL),
('gg', 'gg', 'gg', 'Memnagar, Ahmedabad', 'Memnagar', NULL, NULL, 1053, NULL, NULL),
('dev', 'dev123', 'dev@1234', 'Post Office, Bapunagar, Ahmedabad, 380024', 'bapunagar', 'road maintanence', 'road ma khaaha bau che , varsaad ma bau takleef pade che ', 2069, 'Pending', 'cdesai_roa'),
('Daksh', 'pass1055', 'daksh@gmail.com', 'Paldi Post Office, Ahmedabad, 380007', 'Paldi', 'drainage', 'Bad smell', 2143, 'Pending', 'prao_dra'),
('Gaurav', 'gau123', 'gaurav@gmail.com', 'Post Office, Bapunagar, Ahmedabad, 380024', 'Bapunagar', 'electricity', 'not working', 4168, 'Pending', 'nkulkarni_ele'),
('parth', 'parth123', 'asaasd', 'Post Office, Bapunagar, Ahmedabad, 380024', 'Bapunagar', 'water', 'water', 4879, 'Resolved', 'ajoshi_wat'),
('gaurav1', 'gau123', 'gaurav@gmail.com', 'Post Office, Bapunagar, Ahmedabad, 380024', 'Bapunagar', 'drainage', 'Bad smell', 6536, 'Pending', 'prao_dra'),
('Gaurav2', 'gau123', 'gaurav@gmail.com', 'Paldi Post Office, Ahmedabad, 380007', 'Paldi', 'drainage', 'pipe broken', 7314, 'Pending', 'rkulkarni_dra'),
('pp', 'pp', 'pp', 'Post Office, Bapunagar, Ahmedabad, 380024', 'Bapumagar', 'water', 'water', 7996, 'Pending', 'Unassigned'),
('dd', 'dd123', 'dd@gmail.com', 'Paldi Post Office, Ahmedabad, 380007', 'Bapunagar', 'Drainage', 'Drainage chokes', 9756, 'In Progress', 'prao_dra');

-- --------------------------------------------------------

--
-- Table structure for table `complaints`
--

CREATE TABLE `complaints` (
  `complain_id` int(11) NOT NULL,
  `user_username` varchar(50) NOT NULL,
  `officer_username` varchar(50) DEFAULT NULL,
  `area` varchar(50) NOT NULL,
  `category` varchar(50) DEFAULT NULL,
  `description` varchar(255) DEFAULT NULL,
  `status` varchar(50) DEFAULT 'Pending',
  `created_at` timestamp NOT NULL DEFAULT current_timestamp(),
  `due_by` datetime DEFAULT (DATE_ADD(current_timestamp(), INTERVAL 7 DAY)),
  `resolved_at` datetime DEFAULT NULL,
  `sla_met` tinyint(1) DEFAULT 1
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `complaints`
--

INSERT INTO `complaints` (`complain_id`, `user_username`, `officer_username`, `area`, `category`, `description`, `status`, `created_at`) VALUES
(1001, 'user1', 'ayadav_ele', 'Ellisbridge', 'electricity', 'Complaint 1001 about electricity in Ellisbridge', 'Pending', '2025-08-08 11:34:22'),
(1002, 'user2', 'rdesai_roa', 'Maninagar', 'road maintanence', 'Complaint 1002 about road maintanence in Maninagar', 'Pending', '2025-08-08 11:34:22'),
(1003, 'user3', 'ayadav_dra', 'Ellisbridge', 'drainage', 'Complaint 1003 about drainage in Ellisbridge', 'Pending', '2025-08-08 11:34:22'),
(1004, 'user4', 'drao_oth', 'Paldi', 'other', 'Complaint 1004 about other in Paldi', 'Pending', '2025-08-08 11:34:22'),
(1005, 'user5', 'vthakur_ele', 'Paldi', 'electricity', 'Complaint 1005 about electricity in Paldi', 'Pending', '2025-08-08 11:34:22'),
(1006, 'user6', 'drao_oth', 'Paldi', 'other', 'Complaint 1006 about other in Paldi', 'Pending', '2025-08-08 11:34:22'),
(1007, 'user7', 'amehta_roa', 'Ellisbridge', 'road maintanence', 'Complaint 1007 about road maintanence in Ellisbridge', 'Pending', '2025-08-08 11:34:22'),
(1008, 'user8', 'rkulkarni_dra', 'Paldi', 'drainage', 'Complaint 1008 about drainage in Paldi', 'Pending', '2025-08-08 11:34:22'),
(1009, 'user9', 'rdesai_roa', 'Maninagar', 'road maintanence', 'Complaint 1009 about road maintanence in Maninagar', 'Pending', '2025-08-08 11:34:22'),
(1010, 'user10', 'ayadav_dra', 'Ellisbridge', 'drainage', 'Complaint 1010 about drainage in Ellisbridge', 'Pending', '2025-08-08 11:34:22'),
(1011, 'user11', 'drao_oth', 'Paldi', 'other', 'Complaint 1011 about other in Paldi', 'Pending', '2025-08-08 11:34:22'),
(1012, 'user12', 'ggupta_wat', 'Ellisbridge', 'water', 'Complaint 1012 about water in Ellisbridge', 'Pending', '2025-08-08 11:34:22'),
(1013, 'user13', 'dghosh_wat', 'Maninagar', 'water', 'Complaint 1013 about water in Maninagar', 'Pending', '2025-08-08 11:34:22'),
(1014, 'user14', 'spatel_wat', 'Navrangpura', 'water', 'Complaint 1014 about water in Navrangpura', 'Pending', '2025-08-08 11:34:22'),
(1015, 'user15', 'nkulkarni_ele', 'Bapunagar', 'electricity', 'Complaint 1015 about electricity in Bapunagar', 'Pending', '2025-08-08 11:34:22'),
(1016, 'user16', 'prao_dra', 'Bapunagar', 'drainage', 'Complaint 1016 about drainage in Bapunagar', 'Pending', '2025-08-08 11:34:22'),
(1017, 'user17', 'bsingh_roa', 'Navrangpura', 'road maintanence', 'Complaint 1017 about road maintanence in Navrangpura', 'Pending', '2025-08-08 11:34:22'),
(1018, 'user18', 'psharma_oth', 'Maninagar', 'other', 'Complaint 1018 about other in Maninagar', 'Pending', '2025-08-08 11:34:22'),
(1019, 'user19', 'vthakur_ele', 'Paldi', 'electricity', 'Complaint 1019 about electricity in Paldi', 'Pending', '2025-08-08 11:34:22'),
(1020, 'user20', 'prao_dra', 'Bapunagar', 'drainage', 'Complaint 1020 about drainage in Bapunagar', 'Pending', '2025-08-08 11:34:22'),
(1021, 'user21', 'bsingh_roa', 'Navrangpura', 'road maintanence', 'Complaint 1021 about road maintanence in Navrangpura', 'Pending', '2025-08-08 11:34:22'),
(1022, 'user22', 'rverma_dra', 'Maninagar', 'drainage', 'Complaint 1022 about drainage in Maninagar', 'Pending', '2025-08-08 11:34:22'),
(1023, 'user23', 'nkulkarni_ele', 'Bapunagar', 'electricity', 'Complaint 1023 about electricity in Bapunagar', 'Pending', '2025-08-08 11:34:22'),
(1024, 'user24', 'nkulkarni_ele', 'Bapunagar', 'electricity', 'Complaint 1024 about electricity in Bapunagar', 'Pending', '2025-08-08 11:34:22'),
(1025, 'user25', 'rkulkarni_dra', 'Paldi', 'drainage', 'Complaint 1025 about drainage in Paldi', 'Pending', '2025-08-08 11:34:22'),
(1026, 'user26', 'vthakur_ele', 'Maninagar', 'electricity', 'Complaint 1026 about electricity in Maninagar', 'Pending', '2025-08-08 11:34:22'),
(1027, 'user27', 'rdesai_roa', 'Maninagar', 'road maintanence', 'Complaint 1027 about road maintanence in Maninagar', 'Pending', '2025-08-08 11:34:22'),
(1028, 'user28', 'riyer_oth', 'Navrangpura', 'other', 'Complaint 1028 about other in Navrangpura', 'Pending', '2025-08-08 11:34:22'),
(1029, 'user29', 'rverma_dra', 'Maninagar', 'drainage', 'Complaint 1029 about drainage in Maninagar', 'Pending', '2025-08-08 11:34:22'),
(1030, 'user30', 'pmehta_oth', 'Ellisbridge', 'other', 'Complaint 1030 about other in Ellisbridge', 'Pending', '2025-08-08 11:34:22'),
(1031, 'user31', 'ayadav_dra', 'Ellisbridge', 'drainage', 'Complaint 1031 about drainage in Ellisbridge', 'Pending', '2025-08-08 11:34:22'),
(1032, 'user32', 'psharma_oth', 'Maninagar', 'other', 'Complaint 1032 about other in Maninagar', 'Pending', '2025-08-08 11:34:22'),
(1033, 'user33', 'sghosh_dra', 'Navrangpura', 'drainage', 'Complaint 1033 about drainage in Navrangpura', 'Pending', '2025-08-08 11:34:22'),
(1034, 'user34', 'amehta_roa', 'Ellisbridge', 'road maintanence', 'Complaint 1034 about road maintanence in Ellisbridge', 'Pending', '2025-08-08 11:34:22'),
(1035, 'user35', 'spatel_wat', 'Navrangpura', 'water', 'Complaint 1035 about water in Navrangpura', 'Pending', '2025-08-08 11:34:22'),
(1036, 'user36', 'vthakur_ele', 'Paldi', 'electricity', 'Complaint 1036 about electricity in Paldi', 'Pending', '2025-08-08 11:34:22'),
(1037, 'user37', 'ayadav_ele', 'Ellisbridge', 'electricity', 'Complaint 1037 about electricity in Ellisbridge', 'Pending', '2025-08-08 11:34:22'),
(1038, 'user38', 'pmehta_oth', 'Ellisbridge', 'other', 'Complaint 1038 about other in Ellisbridge', 'Pending', '2025-08-08 11:34:22'),
(1039, 'user39', 'cdesai_roa', 'Bapunagar', 'road maintanence', 'Complaint 1039 about road maintanence in Bapunagar', 'Pending', '2025-08-08 11:34:22'),
(1040, 'user40', 'prao_dra', 'Bapunagar', 'drainage', 'Complaint 1040 about drainage in Bapunagar', 'Pending', '2025-08-08 11:34:22'),
(1041, 'user41', 'pmehta_oth', 'Ellisbridge', 'other', 'Complaint 1041 about other in Ellisbridge', 'Pending', '2025-08-08 11:34:22'),
(1042, 'user42', 'amehta_roa', 'Paldi', 'road maintanence', 'Complaint 1042 about road maintanence in Paldi', 'Pending', '2025-08-08 11:34:22'),
(1043, 'user43', 'drao_oth', 'Paldi', 'other', 'Complaint 1043 about other in Paldi', 'Pending', '2025-08-08 11:34:22'),
(1044, 'user44', 'vthakur_ele', 'Paldi', 'electricity', 'Complaint 1044 about electricity in Paldi', 'Pending', '2025-08-08 11:34:22'),
(1045, 'user45', 'ayadav_ele', 'Ellisbridge', 'electricity', 'Complaint 1045 about electricity in Ellisbridge', 'Pending', '2025-08-08 11:34:22'),
(1046, 'user46', 'ayadav_dra', 'Ellisbridge', 'drainage', 'Complaint 1046 about drainage in Ellisbridge', 'Pending', '2025-08-08 11:34:22'),
(1047, 'user47', 'ayadav_dra', 'Ellisbridge', 'drainage', 'Complaint 1047 about drainage in Ellisbridge', 'Pending', '2025-08-08 11:34:22'),
(1048, 'user48', 'amehta_roa', 'Ellisbridge', 'road maintanence', 'Complaint 1048 about road maintanence in Ellisbridge', 'Pending', '2025-08-08 11:34:22'),
(1049, 'user49', 'riyer_oth', 'Navrangpura', 'other', 'Complaint 1049 about other in Navrangpura', 'Pending', '2025-08-08 11:34:22'),
(1050, 'user50', 'spatel_wat', 'Navrangpura', 'water', 'Complaint 1050 about water in Navrangpura', 'Pending', '2025-08-08 11:34:22'),
(1126, 'Gaurav2', 'rkulkarni_dra', 'Paldi', 'drainage', 'not working pipe', 'Evidence Provided', '2025-08-15 19:33:42'),
(2018, 'Gaurav2', 'drao_oth', 'Paldi', 'other', 'water quality', 'Evidence Provided', '2025-08-15 19:34:32'),
(3784, 'gaurav3', 'vthakur_ele', 'Paldi', 'electricity', 'no light', 'Evidence Provided', '2025-08-16 10:33:58');

-- --------------------------------------------------------

--
-- Table structure for table `evidence`
--

CREATE TABLE `evidence` (
  `evidence_id` int(11) NOT NULL,
  `complain_id` int(11) NOT NULL,
  `user_username` varchar(50) NOT NULL,
  `evidence_type` varchar(50) DEFAULT NULL,
  `evidence_url` text DEFAULT NULL,
  `description` text DEFAULT NULL,
  `uploaded_at` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `evidence`
--

INSERT INTO `evidence` (`evidence_id`, `complain_id`, `user_username`, `evidence_type`, `evidence_url`, `description`, `uploaded_at`) VALUES
(1, 3784, 'gaurav3', 'url', 'https://abc.com/', 'url', '2025-08-16 10:44:57');

-- --------------------------------------------------------

--
-- Indexes for dumped tables
--

--
-- Indexes for table `complaints`
--
ALTER TABLE `complaints`
  ADD PRIMARY KEY (`complain_id`),
  ADD KEY `user_fk` (`user_username`),
  ADD KEY `officer_fk` (`officer_username`),
  ADD KEY `idx_complaints_status` (`status`),
  ADD KEY `idx_complaints_category` (`category`),
  ADD KEY `idx_complaints_area` (`area`),
  ADD KEY `idx_complaints_officer` (`officer_username`),
  ADD KEY `idx_complaints_created` (`created_at`),
  ADD KEY `idx_complaints_due_by` (`due_by`),
  ADD KEY `idx_complaints_user` (`user_username`);

--
-- Indexes for table `evidence`
--
ALTER TABLE `evidence`
  ADD PRIMARY KEY (`evidence_id`),
  ADD KEY `complain_id` (`complain_id`),
  ADD KEY `user_username` (`user_username`),
  ADD KEY `idx_evidence_complain_id` (`complain_id`),
  ADD KEY `idx_evidence_user` (`user_username`);

--
-- Indexes for table `officer`
--
ALTER TABLE `officer`
  ADD PRIMARY KEY (`username`),
  ADD KEY `idx_officer_category` (`category`);

--
-- Indexes for table `user`
--
ALTER TABLE `user`
  ADD PRIMARY KEY (`complain_id`),
  ADD UNIQUE KEY `username` (`username`),
  ADD KEY `idx_user_complain_id` (`complain_id`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `evidence`
--
ALTER TABLE `evidence`
  MODIFY `evidence_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=2;

--
-- STORED PROCEDURES
--

DELIMITER //

-- 1. Procedure to insert a new complaint (original version)
CREATE PROCEDURE InsertComplaintOriginal(
    IN p_user_username VARCHAR(50),
    IN p_area VARCHAR(50),
    IN p_category VARCHAR(50),
    IN p_description VARCHAR(255)
)
BEGIN
    DECLARE next_id INT;
    DECLARE assigned_officer VARCHAR(50);
    
    -- Get next complaint ID
    SELECT COALESCE(MAX(complain_id), 0) + 1 INTO next_id FROM complaints;
    
    -- Find available officer for the category
    SELECT username INTO assigned_officer 
    FROM officer 
    WHERE category = p_category 
    ORDER BY RAND() 
    LIMIT 1;
    
    -- Insert the complaint
    INSERT INTO complaints (complain_id, user_username, officer_username, area, category, description, status, created_at)
    VALUES (next_id, p_user_username, assigned_officer, p_area, p_category, p_description, 'Pending', NOW());
    
    SELECT next_id as complaint_id, assigned_officer as assigned_officer;
END //

-- 2. Enhanced InsertComplaint procedure (matches Java calls)
CREATE PROCEDURE InsertComplaint(
    IN p_complain_id INT,
    IN p_user_username VARCHAR(50),
    IN p_area VARCHAR(50),
    IN p_category VARCHAR(50),
    IN p_description VARCHAR(255),
    IN p_status VARCHAR(50)
)
BEGIN
    -- Do NOT auto-assign officers. Leave officer_username NULL so admin can assign.
    INSERT INTO complaints (complain_id, user_username, officer_username, area, category, description, status, created_at)
    VALUES (p_complain_id, p_user_username, NULL, p_area, p_category, p_description, p_status, NOW());

    -- Update user record with complaint details, clear assigned_officer
    UPDATE user 
    SET category = p_category, 
        complain = p_description, 
        status = p_status, 
        assigned_officer = NULL
    WHERE username = p_user_username;

    SELECT p_complain_id as complaint_id, NULL as assigned_officer;
END //

-- 3. Procedure to update complaint status
CREATE PROCEDURE UpdateComplaintStatus(
    IN p_complaint_id INT,
    IN p_new_status VARCHAR(50),
    IN p_officer_username VARCHAR(50)
)
BEGIN
    UPDATE complaints 
    SET status = p_new_status 
    WHERE complain_id = p_complaint_id 
    AND officer_username = p_officer_username;
    
    SELECT ROW_COUNT() as affected_rows;
END //

-- 4. Procedure to get complaint analytics
CREATE PROCEDURE GetComplaintAnalytics()
BEGIN
    SELECT 
        COUNT(*) as total_complaints,
        COUNT(CASE WHEN status = 'Pending' THEN 1 END) as pending_complaints,
        COUNT(CASE WHEN status = 'In Progress' THEN 1 END) as in_progress_complaints,
        COUNT(CASE WHEN status = 'Resolved' THEN 1 END) as resolved_complaints,
        COUNT(CASE WHEN status = 'Evidence Provided' THEN 1 END) as evidence_provided,
        (SELECT COUNT(DISTINCT username) FROM user) as total_users,
        (SELECT COUNT(*) FROM officer) as total_officers,
        area,
        category,
        AVG(CASE WHEN status = 'Resolved' THEN 1 ELSE 0 END) * 100 as resolution_rate
    FROM complaints
    WHERE area IS NOT NULL AND category IS NOT NULL
    GROUP BY area, category
    WITH ROLLUP;
END //

-- 5. Procedure to get officer performance metrics
CREATE PROCEDURE GetOfficerPerformance(IN p_officer_username VARCHAR(50))
BEGIN
    SELECT 
        o.username as officer_username,
        o.category,
        COUNT(c.complain_id) as active_complaints,
        COUNT(CASE WHEN c.status = 'Resolved' THEN 1 END) as resolved_complaints,
        RANK() OVER (ORDER BY COUNT(CASE WHEN c.status != 'Resolved' THEN 1 END)) as workload_rank,
        CASE 
            WHEN COUNT(CASE WHEN c.status != 'Resolved' THEN 1 END) > 10 THEN 'High Load'
            WHEN COUNT(CASE WHEN c.status != 'Resolved' THEN 1 END) > 5 THEN 'Medium Load'
            ELSE 'Low Load'
        END as load_status
    FROM officer o
    LEFT JOIN complaints c ON o.username = c.officer_username
    WHERE o.username = p_officer_username
    GROUP BY o.username, o.category;
END //

-- 6. Procedure to add evidence
CREATE PROCEDURE AddEvidence(
    IN p_complain_id INT,
    IN p_user_username VARCHAR(50),
    IN p_evidence_type VARCHAR(50),
    IN p_evidence_url TEXT,
    IN p_description TEXT
)
BEGIN
    DECLARE next_evidence_id INT;
    
    -- Get next evidence ID
    SELECT COALESCE(MAX(evidence_id), 0) + 1 INTO next_evidence_id FROM evidence;
    
    -- Insert evidence
    INSERT INTO evidence (evidence_id, complain_id, user_username, evidence_type, evidence_url, description, uploaded_at)
    VALUES (next_evidence_id, p_complain_id, p_user_username, p_evidence_type, p_evidence_url, p_description, NOW());
    
    -- Update complaint status to indicate evidence provided
    UPDATE complaints SET status = 'Evidence Provided' WHERE complain_id = p_complain_id;
    
    -- Also update user table status
    UPDATE user SET status = 'Evidence Provided' WHERE complain_id = p_complain_id;
    
    SELECT next_evidence_id as evidence_id, 'Evidence added successfully' as message;
END //

-- 7. InsertUser procedure (called by User.java)
CREATE PROCEDURE InsertUser(
    IN p_username VARCHAR(50),
    IN p_password VARCHAR(50),
    IN p_email VARCHAR(100),
    IN p_address VARCHAR(300),
    IN p_area VARCHAR(50)
)
BEGIN
    DECLARE next_complain_id INT;
    
    -- Generate next complain_id
    SELECT COALESCE(MAX(complain_id), 1050) + 1 INTO next_complain_id FROM user;
    
    -- Insert new user with provided address
    INSERT INTO user (username, password, email, address, area, complain_id)
    VALUES (p_username, p_password, p_email, p_address, p_area, next_complain_id);
    
    SELECT ROW_COUNT() as affected_rows;
END //

-- 8. InsertOfficer procedure (called by Admin.java)
CREATE PROCEDURE InsertOfficer(
    IN p_username VARCHAR(50),
    IN p_password VARCHAR(50),
    IN p_area VARCHAR(50),
    IN p_category VARCHAR(50)
)
BEGIN
    INSERT INTO officer (username, password, category)
    VALUES (p_username, p_password, p_category);
    
    SELECT ROW_COUNT() as affected_rows;
END //

-- 9. DeleteUser procedure (called by Admin.java)
CREATE PROCEDURE DeleteUser(IN p_username VARCHAR(50))
BEGIN
    -- First delete related complaints
    DELETE FROM complaints WHERE user_username = p_username;
    
    -- Then delete the user
    DELETE FROM user WHERE username = p_username;
    
    SELECT ROW_COUNT() as affected_rows;
END //

-- 10. DeleteOfficer procedure (called by Admin.java)
CREATE PROCEDURE DeleteOfficer(IN p_username VARCHAR(50))
BEGIN
    -- Update complaints to unassign this officer (set NULL)
    UPDATE complaints SET officer_username = NULL
    WHERE officer_username = p_username;
    
    -- Delete the officer
    DELETE FROM officer WHERE username = p_username;
    
    SELECT ROW_COUNT() as affected_rows;
END //

-- 11. CreateEvidenceTable procedure (called by ProcedureHelper.java)
CREATE PROCEDURE CreateEvidenceTable()
BEGIN
    CREATE TABLE IF NOT EXISTS evidence (
        evidence_id INT PRIMARY KEY AUTO_INCREMENT,
        complain_id INT NOT NULL,
        user_username VARCHAR(50) NOT NULL,
        evidence_type VARCHAR(50),
        evidence_url TEXT,
        description TEXT,
        uploaded_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
        FOREIGN KEY (complain_id) REFERENCES complaints(complain_id),
        FOREIGN KEY (user_username) REFERENCES user(username)
    );
    SELECT 'Evidence table created successfully' as message;
END //

-- 12. ResetEvidenceTable procedure (called by ProcedureHelper.java)
CREATE PROCEDURE ResetEvidenceTable()
BEGIN
    DROP TABLE IF EXISTS evidence;
    CALL CreateEvidenceTable();
    SELECT 'Evidence table reset successfully' as message;
END //

-- 13. Procedure to insert sample data
CREATE PROCEDURE InsertSampleData()
BEGIN
    -- Insert sample officers if not exists
    INSERT IGNORE INTO officer (username, password, category) VALUES
    ('ayadav_ele', 'pass', 'electricity'),
    ('rdesai_roa', 'pass', 'road maintanence'),
    ('ayadav_dra', 'pass', 'drainage'),
    ('drao_oth', 'pass', 'other'),
    ('vthakur_ele', 'pass', 'electricity'),
    ('rkulkarni_dra', 'pass', 'drainage'),
    ('amehta_roa', 'pass', 'road maintanence'),
    ('spatel_wat', 'pass', 'water'),
    ('nkulkarni_ele', 'pass', 'electricity'),
    ('prao_dra', 'pass', 'drainage'),
    ('bsingh_roa', 'pass', 'road maintanence'),
    ('psharma_oth', 'pass', 'other'),
    ('riyer_oth', 'pass', 'other'),
    ('rverma_dra', 'pass', 'drainage'),
    ('sghosh_dra', 'pass', 'drainage'),
    ('cdesai_roa', 'pass', 'road maintanence'),
    ('pmehta_oth', 'pass', 'other'),
    ('ggupta_wat', 'pass', 'water'),
    ('dghosh_wat', 'pass', 'water'),
    ('syadav_ele', 'pass', 'electricity');
    
    -- Insert sample users if not exists
    INSERT IGNORE INTO user (username, password, email, address, area, category, complain, complain_id, status, assigned_officer) VALUES
    ('user1', 'pass1001', 'user1@example.com', '6 Arjun Nivas, Ellisbridge, Ahmedabad', 'Ellisbridge', 'electricity', 'Power outage in area', 1001, 'Pending', 'ayadav_ele'),
    ('user2', 'pass1002', 'user2@example.com', 'Maninagar, Ahmedabad', 'Maninagar', 'road maintanence', 'Potholes on main road', 1002, 'Pending', 'rdesai_roa'),
    ('user3', 'pass1003', 'user3@example.com', 'Ellisbridge, Ahmedabad', 'Ellisbridge', 'drainage', 'Drainage blockage', 1003, 'Pending', 'ayadav_dra');
END //

DELIMITER ;

--
-- TRIGGERS (Fixed syntax)
--

DROP TRIGGER IF EXISTS trg_evidence_added;
DELIMITER $
CREATE TRIGGER trg_evidence_added
AFTER INSERT ON evidence
FOR EACH ROW
BEGIN
    UPDATE complaints 
    SET status = 'Evidence Provided' 
    WHERE complain_id = NEW.complain_id;
END$
DELIMITER ;

DROP TRIGGER IF EXISTS trg_officer_assigned;
DELIMITER $
CREATE TRIGGER trg_officer_assigned
AFTER UPDATE ON complaints
FOR EACH ROW
BEGIN
    IF NEW.officer_username != OLD.officer_username THEN
        UPDATE user 
        SET assigned_officer = NEW.officer_username 
        WHERE complain_id = NEW.complain_id;
    END IF;
END$
DELIMITER ;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `complaints`
--
ALTER TABLE `complaints`
  ADD CONSTRAINT `complaints_ibfk_1` FOREIGN KEY (`user_username`) REFERENCES `user` (`username`),
  ADD CONSTRAINT `complaints_ibfk_2` FOREIGN KEY (`officer_username`) REFERENCES `officer` (`username`);

--
-- Constraints for table `evidence`
--
ALTER TABLE `evidence`
  ADD CONSTRAINT `evidence_ibfk_1` FOREIGN KEY (`complain_id`) REFERENCES `complaints` (`complain_id`),
  ADD CONSTRAINT `evidence_ibfk_2` FOREIGN KEY (`user_username`) REFERENCES `user` (`username`);

-- Update any existing data to ensure consistency
-- Update NULL SLA fields with default values
UPDATE complaints 
SET due_by = DATE_ADD(created_at, INTERVAL 7 DAY) 
WHERE due_by IS NULL;

UPDATE complaints 
SET sla_met = 1 
WHERE sla_met IS NULL;

UPDATE complaints 
SET status = 'Pending' 
WHERE status IS NULL;

-- Keep officer_username as NULL when unassigned. Do not coerce to 'Unassigned'.

COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;