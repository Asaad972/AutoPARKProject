CREATE DATABASE  IF NOT EXISTS `bpark` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;
USE `bpark`;
-- MySQL dump 10.13  Distrib 8.0.42, for Win64 (x86_64)
--
-- Host: localhost    Database: bpark
-- ------------------------------------------------------
-- Server version	8.0.42

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `monthly_parking_time_report`
--

DROP TABLE IF EXISTS `monthly_parking_time_report`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `monthly_parking_time_report` (
  `year` int NOT NULL,
  `month` int NOT NULL,
  `normal_hours` int DEFAULT '0',
  `extended_hours` int DEFAULT '0',
  `delayed_hours` int DEFAULT '0',
  PRIMARY KEY (`year`,`month`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `monthly_parking_time_report`
--

LOCK TABLES `monthly_parking_time_report` WRITE;
/*!40000 ALTER TABLE `monthly_parking_time_report` DISABLE KEYS */;
INSERT INTO `monthly_parking_time_report` VALUES (2025,6,1,42,5),(2025,7,410,60,20);
/*!40000 ALTER TABLE `monthly_parking_time_report` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `monthly_subscriber_report`
--

DROP TABLE IF EXISTS `monthly_subscriber_report`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `monthly_subscriber_report` (
  `year` int NOT NULL,
  `month` int NOT NULL,
  `daily_subscribers` text,
  PRIMARY KEY (`year`,`month`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `monthly_subscriber_report`
--

LOCK TABLES `monthly_subscriber_report` WRITE;
/*!40000 ALTER TABLE `monthly_subscriber_report` DISABLE KEYS */;
INSERT INTO `monthly_subscriber_report` VALUES (2025,6,'3,0,0,0,0,0,0,0,0,5,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0'),(2025,7,'8,9,10,11,9,8,10,12,9,10,8,9,8,9,10,11,10,12,13,9,8,10,9,11,12,10,9,11,12,13,14');
/*!40000 ALTER TABLE `monthly_subscriber_report` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `parking_history`
--

DROP TABLE IF EXISTS `parking_history`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `parking_history` (
  `history_id` int NOT NULL AUTO_INCREMENT,
  `subscriber_code` varchar(20) NOT NULL,
  `parking_space_id` int NOT NULL,
  `entry_time` datetime NOT NULL,
  `exit_time` datetime DEFAULT NULL,
  `extended` tinyint(1) DEFAULT '0',
  `extended_hours` int DEFAULT '0',
  `was_late` tinyint(1) DEFAULT '0',
  `picked_up` tinyint(1) DEFAULT '0',
  PRIMARY KEY (`history_id`),
  KEY `subscriber_code` (`subscriber_code`),
  KEY `parking_space_id` (`parking_space_id`),
  CONSTRAINT `parking_history_ibfk_1` FOREIGN KEY (`subscriber_code`) REFERENCES `subscriber` (`subscriber_code`),
  CONSTRAINT `parking_history_ibfk_2` FOREIGN KEY (`parking_space_id`) REFERENCES `parking_space` (`parking_space_id`)
) ENGINE=InnoDB AUTO_INCREMENT=35 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `parking_history`
--

LOCK TABLES `parking_history` WRITE;
/*!40000 ALTER TABLE `parking_history` DISABLE KEYS */;
INSERT INTO `parking_history` VALUES (13,'SUB5',6,'2025-05-30 22:27:38','2025-05-31 02:27:38',0,0,0,0),(14,'SUB5',5,'2025-05-30 18:27:38','2025-05-30 21:27:38',0,0,0,0),(15,'SUB5',7,'2025-05-30 15:27:38','2025-05-30 19:27:38',0,0,1,0),(16,'SUB5',9,'2025-05-31 01:27:38','2025-05-31 05:27:38',0,0,1,0),(17,'SUB6',7,'2025-05-30 23:50:20','2025-05-31 03:50:20',0,0,1,0),(18,'SUB3',5,'2025-05-30 23:50:48','2025-05-31 03:50:48',0,0,1,0),(19,'SUB7',1,'2025-05-31 00:06:50','2025-06-01 15:24:44',1,4,1,1),(20,'SUB3',2,'2025-06-01 00:36:34','2025-06-01 12:36:34',1,4,1,0),(21,'SUB5',3,'2025-06-01 12:32:33','2025-06-01 15:20:19',1,4,0,1),(22,'SUB3',10,'2025-06-01 14:06:15','2025-06-01 15:22:56',0,0,0,1),(23,'SUB7',1,'2025-06-01 14:32:16','2025-06-01 15:23:20',1,4,0,1),(24,'SUB7',4,'2025-06-01 15:24:29','2025-06-01 15:24:35',0,0,0,1),(25,'SUB5',8,'2025-06-01 15:28:02','2025-06-01 15:28:05',0,0,0,1),(26,'SUB5',1,'2025-06-01 15:28:23','2025-06-01 15:28:37',0,0,0,1),(27,'SUB7',1,'2025-06-01 11:16:29','2025-06-01 16:17:30',1,4,0,1),(29,'SUB3',1,'2025-06-01 16:32:37','2025-06-01 16:33:08',0,0,0,1),(32,'SUB3',3,'2025-06-10 10:00:00','2025-06-10 15:30:00',0,0,1,1),(33,'SUB4',4,'2025-06-10 07:30:00','2025-06-10 13:50:00',1,4,1,1),(34,'SUB5',5,'2025-06-10 06:00:00','2025-06-10 12:30:00',1,4,1,1);
/*!40000 ALTER TABLE `parking_history` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `parking_space`
--

DROP TABLE IF EXISTS `parking_space`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `parking_space` (
  `parking_space_id` int NOT NULL AUTO_INCREMENT,
  `is_available` tinyint(1) DEFAULT '1',
  PRIMARY KEY (`parking_space_id`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `parking_space`
--

LOCK TABLES `parking_space` WRITE;
/*!40000 ALTER TABLE `parking_space` DISABLE KEYS */;
INSERT INTO `parking_space` VALUES (1,1),(2,1),(3,1),(4,1),(5,1),(6,1),(7,1),(8,1),(9,1),(10,1);
/*!40000 ALTER TABLE `parking_space` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `reservation`
--

DROP TABLE IF EXISTS `reservation`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `reservation` (
  `reservation_id` int NOT NULL AUTO_INCREMENT,
  `subscriber_code` varchar(20) NOT NULL,
  `parking_space_id` int NOT NULL,
  `reservation_date` datetime NOT NULL,
  `confirmation_code` int DEFAULT NULL,
  `status` enum('active','cancelled','expired') DEFAULT 'active',
  PRIMARY KEY (`reservation_id`),
  KEY `subscriber_code` (`subscriber_code`),
  KEY `parking_space_id` (`parking_space_id`),
  CONSTRAINT `reservation_ibfk_1` FOREIGN KEY (`subscriber_code`) REFERENCES `subscriber` (`subscriber_code`),
  CONSTRAINT `reservation_ibfk_2` FOREIGN KEY (`parking_space_id`) REFERENCES `parking_space` (`parking_space_id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `reservation`
--

LOCK TABLES `reservation` WRITE;
/*!40000 ALTER TABLE `reservation` DISABLE KEYS */;
INSERT INTO `reservation` VALUES (1,'SUB4',10,'2024-05-13 08:47:58',8710,'cancelled'),(2,'SUB6',10,'2024-05-12 13:14:19',9510,'expired'),(3,'SUB5',3,'2024-05-05 11:35:23',2249,'cancelled');
/*!40000 ALTER TABLE `reservation` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `subscriber`
--

DROP TABLE IF EXISTS `subscriber`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `subscriber` (
  `subscriber_code` varchar(20) NOT NULL,
  `subscriber_id` int NOT NULL,
  `email` varchar(100) DEFAULT NULL,
  `phone_number` varchar(20) DEFAULT NULL,
  PRIMARY KEY (`subscriber_code`),
  UNIQUE KEY `subscriber_id` (`subscriber_id`),
  CONSTRAINT `subscriber_ibfk_1` FOREIGN KEY (`subscriber_id`) REFERENCES `users` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `subscriber`
--

LOCK TABLES `subscriber` WRITE;
/*!40000 ALTER TABLE `subscriber` DISABLE KEYS */;
INSERT INTO `subscriber` VALUES ('SUB1',1,'ibrahem@gmail.com','052-644-3213'),('SUB2',2,'danel@hotmail.com','077-432-1132'),('SUB3',3,'treynolds@example.net','092-775-9193'),('SUB4',4,'knelson@example.com','041-867-9384'),('SUB5',5,'brandonsheppard@example.org','053-350-5156'),('SUB6',6,'ashley85@example.org','075-205-1387'),('SUB7',7,'colelisa@example.com','093-783-2760');
/*!40000 ALTER TABLE `subscriber` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `system_log`
--

DROP TABLE IF EXISTS `system_log`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `system_log` (
  `log_id` int NOT NULL AUTO_INCREMENT,
  `action` varchar(100) NOT NULL,
  `target` varchar(100) DEFAULT NULL,
  `by_user` int DEFAULT NULL,
  `log_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `note` text,
  PRIMARY KEY (`log_id`),
  KEY `by_user` (`by_user`),
  CONSTRAINT `system_log_ibfk_1` FOREIGN KEY (`by_user`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=31 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `system_log`
--

LOCK TABLES `system_log` WRITE;
/*!40000 ALTER TABLE `system_log` DISABLE KEYS */;
INSERT INTO `system_log` VALUES (1,'Add User','Target-1',1,'2025-05-27 12:12:03','Head color international artist situation.'),(2,'Add User','Target-2',2,'2025-05-27 12:12:03','And off travel move quite.'),(3,'Deposit','Spot 6',5,'2025-05-30 20:15:09','Car Deposit'),(4,'Deposit','Spot 10',5,'2025-05-30 20:16:15','Car Deposit'),(5,'Deposit','Spot 8',5,'2025-05-30 20:23:37','Car Deposit'),(6,'Deposit','Spot 7',5,'2025-05-30 20:23:49','Car Deposit'),(7,'Deposit','Spot 3',5,'2025-05-30 20:23:52','Car Deposit'),(8,'Deposit','Spot 7',6,'2025-05-30 20:50:19','Car Deposit'),(9,'Deposit','Spot 5',3,'2025-05-30 20:50:48','Car Deposit'),(10,'Deposit','Spot 1',7,'2025-05-30 21:06:49','Car Deposit'),(11,'Deposit','Spot 2',3,'2025-05-31 21:36:34','Car Deposit'),(12,'Deposit','Spot 3',5,'2025-06-01 09:32:32','Car Deposit'),(13,'Deposit','Spot 10',3,'2025-06-01 11:06:15','Car Deposit'),(14,'Pickup','Spot 3',5,'2025-06-01 11:17:05','Car Deposit'),(15,'Pickup (Late)','Spot 3',5,'2025-06-01 12:20:18','Car Deposit'),(16,'Deposit','Spot 3',5,'2025-06-01 12:20:35','Car Deposit'),(17,'Pickup','Spot 10',3,'2025-06-01 12:22:56','Car Deposit'),(18,'Pickup (Late)','Spot 1',7,'2025-06-01 12:23:20','Car Deposit'),(19,'Deposit','Spot 10',7,'2025-06-01 12:23:31','Car Deposit'),(20,'Deposit','Spot 4',7,'2025-06-01 12:24:28','Car Deposit'),(21,'Pickup','Spot 4',7,'2025-06-01 12:24:35','Car Deposit'),(22,'Pickup (Late)','Spot 1',7,'2025-06-01 12:24:43','Car Deposit'),(23,'Deposit','Spot 8',5,'2025-06-01 12:28:01','Car Deposit'),(24,'Pickup','Spot 8',5,'2025-06-01 12:28:05','Car Deposit'),(25,'Deposit','Spot 1',5,'2025-06-01 12:28:23','Car Deposit'),(26,'Pickup','Spot 1',5,'2025-06-01 12:28:37','Car Deposit'),(27,'Pickup','Spot 1',7,'2025-06-01 12:32:09','Car Deposit'),(28,'Pickup','Spot 1',7,'2025-06-01 13:17:29','Car Deposit'),(29,'Deposit','Spot 1',3,'2025-06-01 13:32:36','Car Deposit'),(30,'Pickup','Spot 1',3,'2025-06-01 13:33:08','Car Deposit');
/*!40000 ALTER TABLE `system_log` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `users` (
  `id` int NOT NULL AUTO_INCREMENT,
  `username` varchar(50) NOT NULL,
  `password` varchar(255) NOT NULL,
  `first_name` varchar(50) NOT NULL,
  `last_name` varchar(50) NOT NULL,
  `role` enum('admin','supervisor','subscriber') NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `username` (`username`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `users`
--

LOCK TABLES `users` WRITE;
/*!40000 ALTER TABLE `users` DISABLE KEYS */;
INSERT INTO `users` VALUES (1,'admin1','admin1','Admin','User','admin'),(2,'supervisor1','supervisor1','Supervisor','User','supervisor'),(3,'SUB3','pass3','Lori','Ryan','subscriber'),(4,'SUB4','pass4','Ricky','Garcia','subscriber'),(5,'SUB5','pass5','Stacy','Brown','subscriber'),(6,'SUB6','pass6','Brittany','Howard','subscriber'),(7,'SUB7','pass7','Ashley','Bell','subscriber');
/*!40000 ALTER TABLE `users` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-07-08 11:58:52
