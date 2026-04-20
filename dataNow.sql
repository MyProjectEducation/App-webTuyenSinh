-- MySQL dump 10.13  Distrib 8.0.45, for Win64 (x86_64)
--
-- Host: 127.0.0.1    Database: xettuyen2026
-- ------------------------------------------------------
-- Server version	5.5.5-10.4.32-MariaDB

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
-- Table structure for table `sys_users`
--

DROP TABLE IF EXISTS `sys_users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_users` (
  `user_id` int(11) NOT NULL AUTO_INCREMENT,
  `username` varchar(50) NOT NULL,
  `password` varchar(255) NOT NULL,
  `full_name` varchar(100) DEFAULT NULL,
  `role` varchar(10) DEFAULT 'user',
  `is_active` tinyint(1) DEFAULT 1,
  PRIMARY KEY (`user_id`),
  UNIQUE KEY `username` (`username`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_users`
--

LOCK TABLES `sys_users` WRITE;
/*!40000 ALTER TABLE `sys_users` DISABLE KEYS */;
INSERT INTO `sys_users` VALUES (1,'admin','$2a$10$XQvE...','Quản trị viên Hệ thống','admin',1),(2,'user1','$2a$10$Y1zA...','Nhân viên Tuyển sinh 1','user',1);
/*!40000 ALTER TABLE `sys_users` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `xt_bangquydoi`
--

DROP TABLE IF EXISTS `xt_bangquydoi`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `xt_bangquydoi` (
  `idqd` int(11) NOT NULL AUTO_INCREMENT,
  `d_phuongthuc` varchar(45) DEFAULT NULL,
  `d_tohop` varchar(45) DEFAULT NULL,
  `d_mon` varchar(45) DEFAULT NULL,
  `d_diema` decimal(6,2) DEFAULT NULL,
  `d_diemb` decimal(6,2) DEFAULT NULL,
  `d_diemc` decimal(6,2) DEFAULT NULL,
  `d_diemd` decimal(6,2) DEFAULT NULL,
  `d_maquydoi` varchar(45) DEFAULT NULL,
  `d_phanvi` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`idqd`),
  UNIQUE KEY `d_maquydoi_UNIQUE` (`d_maquydoi`)
) ENGINE=InnoDB AUTO_INCREMENT=79 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `xt_bangquydoi`
--

LOCK TABLES `xt_bangquydoi` WRITE;
/*!40000 ALTER TABLE `xt_bangquydoi` DISABLE KEYS */;
INSERT INTO `xt_bangquydoi` VALUES (5,'VSAT',NULL,'TO',132.00,150.00,8.50,10.00,'VSAT_TO_3','3'),(6,'VSAT',NULL,'TO',128.50,132.00,8.10,8.50,'VSAT_TO_5','5'),(7,'VSAT',NULL,'TO',122.50,128.50,7.75,8.10,'VSAT_TO_10','10'),(8,'VSAT',NULL,'TO',114.50,122.50,7.00,7.75,'VSAT_TO_20','20'),(9,'VSAT',NULL,'TO',108.00,114.50,6.60,7.00,'VSAT_TO_30','30'),(10,'VSAT',NULL,'TO',102.50,108.00,6.25,6.60,'VSAT_TO_40','40'),(11,'VSAT',NULL,'TO',97.00,102.50,6.00,6.25,'VSAT_TO_50','50'),(12,'VSAT',NULL,'TO',91.00,97.00,5.60,6.00,'VSAT_TO_60','60'),(13,'VSAT',NULL,'TO',85.00,91.00,5.25,5.60,'VSAT_TO_70','70'),(14,'VSAT',NULL,'TO',77.00,85.00,5.00,5.25,'VSAT_TO_80','80'),(15,'VSAT',NULL,'TO',68.00,77.00,4.50,5.00,'VSAT_TO_90','90'),(16,'VSAT',NULL,'TO',6.00,68.00,1.50,4.50,'VSAT_TO_95','95'),(17,'VSAT',NULL,'LI',123.00,147.00,9.50,10.00,'VSAT_LI_3','3'),(18,'VSAT',NULL,'LI',118.50,123.00,9.25,9.50,'VSAT_LI_5','5'),(19,'VSAT',NULL,'LI',112.50,118.50,9.00,9.25,'VSAT_LI_10','10'),(20,'VSAT',NULL,'LI',105.00,112.50,8.50,9.00,'VSAT_LI_20','20'),(21,'VSAT',NULL,'LI',99.50,105.00,8.00,8.50,'VSAT_LI_30','30'),(22,'VSAT',NULL,'LI',94.50,99.50,7.75,8.00,'VSAT_LI_40','40'),(23,'VSAT',NULL,'LI',90.00,94.50,7.50,7.75,'VSAT_LI_50','50'),(25,'VSAT',NULL,'LI',85.00,90.00,7.25,7.50,'VSAT_LI_60','60'),(26,'VSAT',NULL,'LI',80.00,85.00,6.75,7.25,'VSAT_LI_70','70'),(27,'VSAT',NULL,'LI',74.00,80.00,6.35,6.75,'VSAT_LI_80','80'),(28,'VSAT',NULL,'LI',66.50,74.00,5.75,6.35,'VSAT_LI_90','90'),(29,'VSAT',NULL,'LI',17.00,66.50,3.05,5.75,'VSAT_LI_95','95'),(30,'VSAT',NULL,'HO',129.00,150.00,9.50,10.00,'VSAT_HO_3','3'),(31,'VSAT',NULL,'HO',124.50,129.00,9.25,9.50,'VSAT_HO_5','5'),(32,'VSAT',NULL,'HO',117.00,124.50,8.75,9.25,'VSAT_HO_10','10'),(33,'VSAT',NULL,'HO',107.50,117.00,8.25,8.75,'VSAT_HO_20','20'),(34,'VSAT',NULL,'HO',100.50,107.50,7.75,8.25,'VSAT_HO_30','30'),(35,'VSAT',NULL,'HO',94.00,100.50,7.25,7.75,'VSAT_HO_40','40'),(36,'VSAT',NULL,'HO',88.00,94.00,6.75,7.25,'VSAT_HO_50','50'),(37,'VSAT',NULL,'HO',81.50,88.00,6.25,6.75,'VSAT_HO_60','60'),(38,'VSAT',NULL,'HO',75.50,81.50,5.75,6.25,'VSAT_HO_70','70'),(39,'VSAT',NULL,'HO',68.50,75.50,5.25,5.75,'VSAT_HO_80','80'),(40,'VSAT',NULL,'HO',59.50,68.50,4.60,5.25,'VSAT_HO_90','90'),(41,'VSAT',NULL,'HO',20.00,59.50,1.35,4.60,'VSAT_HO_95','95'),(42,'VSAT',NULL,'SI',130.50,150.00,9.00,9.75,'VSAT_SI_3','3'),(43,'VSAT',NULL,'SI',126.50,130.50,8.75,9.00,'VSAT_SI_5','5'),(44,'VSAT',NULL,'SI',120.50,126.50,8.34,8.75,'VSAT_SI_10','10'),(45,'VSAT',NULL,'SI',112.50,120.50,7.85,8.34,'VSAT_SI_20','20'),(47,'VSAT',NULL,'SI',105.50,112.50,7.50,7.85,'VSAT_SI_30','30'),(48,'VSAT',NULL,'SI',100.00,105.50,7.25,7.50,'VSAT_SI_40','40'),(49,'VSAT',NULL,'SI',94.50,100.00,6.85,7.25,'VSAT_SI_50','50'),(50,'VSAT',NULL,'SI',88.50,94.50,6.50,6.85,'VSAT_SI_60','60'),(51,'VSAT',NULL,'SI',82.50,88.50,6.25,6.50,'VSAT_SI_70','70'),(52,'VSAT',NULL,'SI',76.00,82.50,5.85,6.25,'VSAT_SI_80','80'),(53,'VSAT',NULL,'SI',66.50,76.00,5.25,5.85,'VSAT_SI_90','90'),(54,'VSAT',NULL,'SI',26.50,66.50,2.80,5.25,'VSAT_SI_95','95'),(55,'VSAT',NULL,'VA',129.50,146.00,9.25,9.75,'VSAT_VA_3','3'),(56,'VSAT',NULL,'VA',127.50,129.50,9.00,9.25,'VSAT_VA_5','5'),(57,'VSAT',NULL,'VA',124.00,127.50,9.00,9.00,'VSAT_VA_10','10'),(58,'VSAT',NULL,'VA',119.50,124.00,8.75,9.00,'VSAT_VA_20','20'),(59,'VSAT',NULL,'VA',115.50,119.50,8.50,8.75,'VSAT_VA_30','30'),(60,'VSAT',NULL,'VA',112.50,115.50,8.25,8.50,'VSAT_VA_40','40'),(61,'VSAT',NULL,'VA',109.00,112.50,8.00,8.25,'VSAT_VA_50','50'),(62,'VSAT',NULL,'VA',106.00,109.00,7.75,8.00,'VSAT_VA_60','60'),(63,'VSAT',NULL,'VA',102.00,106.00,7.50,7.75,'VSAT_VA_70','70'),(64,'VSAT',NULL,'VA',97.00,102.00,7.25,7.50,'VSAT_VA_80','80'),(65,'VSAT',NULL,'VA',90.00,97.00,6.75,7.25,'VSAT_VA_90','90'),(66,'VSAT',NULL,'VA',5.00,90.00,3.50,6.75,'VSAT_VA_95','95'),(67,'VSAT',NULL,'SU',133.50,150.00,9.75,10.00,'VSAT_SU_3','3'),(68,'VSAT',NULL,'SU',131.00,133.50,9.50,9.75,'VSAT_SU_5','5'),(69,'VSAT',NULL,'SU',126.50,131.00,9.25,9.50,'VSAT_SU_10','10'),(70,'VSAT',NULL,'SU',120.50,126.50,9.00,9.25,'VSAT_SU_20','20'),(71,'VSAT',NULL,'SU',115.00,120.50,8.50,9.00,'VSAT_SU_30','30'),(72,'VSAT',NULL,'SU',110.00,115.00,8.25,8.50,'VSAT_SU_40','40'),(73,'VSAT',NULL,'SU',105.50,110.00,8.00,8.25,'VSAT_SU_50','50'),(74,'VSAT',NULL,'SU',101.00,105.50,7.75,8.00,'VSAT_SU_60','60'),(75,'VSAT',NULL,'SU',95.50,101.00,7.50,7.75,'VSAT_SU_70','70'),(76,'VSAT',NULL,'SU',88.50,95.50,7.00,7.50,'VSAT_SU_80','80'),(77,'VSAT',NULL,'SU',79.50,88.50,6.35,7.00,'VSAT_SU_90','90'),(78,'VSAT',NULL,'SU',36.50,79.50,2.95,6.35,'VSAT_SU_95','95');
/*!40000 ALTER TABLE `xt_bangquydoi` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `xt_diemcongxetuyen`
--

DROP TABLE IF EXISTS `xt_diemcongxetuyen`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `xt_diemcongxetuyen` (
  `iddiemcong` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `ts_cccd` varchar(45) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `manganh` varchar(20) DEFAULT '0.00',
  `matohop` varchar(10) DEFAULT '0.00',
  `phuongthuc` varchar(45) DEFAULT NULL,
  `diemCC` decimal(6,2) DEFAULT NULL,
  `diemUtxt` decimal(6,2) DEFAULT NULL,
  `diemTong` decimal(6,2) DEFAULT 0.00,
  `ghichu` text DEFAULT NULL,
  `dc_keys` varchar(45) NOT NULL,
  PRIMARY KEY (`iddiemcong`),
  UNIQUE KEY `dc_keys_UNIQUE` (`dc_keys`)
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `xt_diemcongxetuyen`
--

LOCK TABLES `xt_diemcongxetuyen` WRITE;
/*!40000 ALTER TABLE `xt_diemcongxetuyen` DISABLE KEYS */;
INSERT INTO `xt_diemcongxetuyen` VALUES (12,'062205007194','7480201','A00','VSAT',30.00,11.25,41.25,'KV: KV1 (0.75) + ĐT: 02 (0) + CC: IELTS 7.0 (30). Mức Trần: 41.25','062205007194_7480201_A00');
/*!40000 ALTER TABLE `xt_diemcongxetuyen` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `xt_diemthixettuyen`
--

DROP TABLE IF EXISTS `xt_diemthixettuyen`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `xt_diemthixettuyen` (
  `iddiemthi` int(11) NOT NULL AUTO_INCREMENT,
  `cccd` varchar(20) NOT NULL,
  `sobaodanh` varchar(45) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL,
  `d_phuongthuc` varchar(10) DEFAULT NULL,
  `TO` decimal(8,2) DEFAULT 0.00,
  `LI` decimal(8,2) DEFAULT 0.00,
  `HO` decimal(8,2) DEFAULT 0.00,
  `SI` decimal(8,2) DEFAULT 0.00,
  `SU` decimal(8,2) DEFAULT 0.00,
  `DI` decimal(8,2) DEFAULT 0.00,
  `VA` decimal(8,2) DEFAULT 0.00,
  `N1_THI` decimal(8,2) DEFAULT NULL COMMENT 'Điểm thi gốc',
  `N1_CC` decimal(8,2) DEFAULT 0.00 COMMENT 'max(N1_Thi, N1_QD)',
  `CNCN` decimal(8,2) DEFAULT 0.00,
  `CNNN` decimal(8,2) DEFAULT 0.00,
  `TI` decimal(8,2) DEFAULT 0.00,
  `KTPL` decimal(8,2) DEFAULT 0.00,
  `NL1` decimal(8,2) DEFAULT NULL,
  `NK1` decimal(8,2) DEFAULT NULL,
  `NK2` decimal(8,2) DEFAULT NULL,
  PRIMARY KEY (`iddiemthi`),
  UNIQUE KEY `cccd_UNIQUE` (`cccd`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `xt_diemthixettuyen`
--

LOCK TABLES `xt_diemthixettuyen` WRITE;
/*!40000 ALTER TABLE `xt_diemthixettuyen` DISABLE KEYS */;
INSERT INTO `xt_diemthixettuyen` VALUES (1,'079204000111','02000001','THPT',7.00,7.00,7.00,9.00,10.00,9.00,6.00,7.00,7.00,10.00,10.00,8.00,7.00,NULL,8.00,9.00),(2,'079204000222','02000002','THPT',9.00,8.00,7.00,0.00,0.00,0.00,8.50,6.50,9.00,0.00,0.00,0.00,0.00,850.00,NULL,NULL),(3,'079204000333','02000003','VSAT',7.50,8.50,9.00,0.00,0.00,0.00,5.50,8.00,8.00,0.00,0.00,0.00,0.00,NULL,NULL,NULL),(5,'062205007194',NULL,'VSAT',132.00,147.00,150.00,150.00,150.00,150.00,150.00,150.00,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL);
/*!40000 ALTER TABLE `xt_diemthixettuyen` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `xt_nganh`
--

DROP TABLE IF EXISTS `xt_nganh`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `xt_nganh` (
  `idnganh` int(11) NOT NULL AUTO_INCREMENT,
  `manganh` varchar(45) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `tennganh` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `n_tohopgoc` varchar(3) DEFAULT NULL,
  `n_chitieu` int(11) NOT NULL DEFAULT 0,
  `n_diemsan` decimal(10,2) DEFAULT NULL,
  `n_diemtrungtuyen` decimal(10,2) DEFAULT NULL,
  `n_tuyenthang` varchar(1) DEFAULT NULL,
  `n_dgnl` varchar(1) DEFAULT NULL,
  `n_thpt` varchar(1) DEFAULT NULL,
  `n_vsat` varchar(1) DEFAULT NULL,
  `sl_xtt` int(11) DEFAULT NULL,
  `sl_dgnl` int(11) DEFAULT NULL,
  `sl_vsat` int(11) DEFAULT NULL,
  `sl_thpt` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`idnganh`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `xt_nganh`
--

LOCK TABLES `xt_nganh` WRITE;
/*!40000 ALTER TABLE `xt_nganh` DISABLE KEYS */;
INSERT INTO `xt_nganh` VALUES (1,'7480201','Công nghệ thông tin','A00',300,18.00,22.00,'1','1','1','1',10,50,100,'100'),(2,'7140209','Sư phạm Toán học','A00',100,20.00,21.70,'1','0','1','1',100,0,50,'150'),(3,'7340101','Quản trị kinh doanh','A01',250,17.00,18.50,'1','1','1','0',50,50,0,'100'),(4,'7340102','Kinh tế','A00',250,20.00,24.00,'0','1','0','0',0,100,0,'0'),(5,'7340103','Logic','B00',150,18.00,21.00,'1','0','1','0',10,0,0,'100');
/*!40000 ALTER TABLE `xt_nganh` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `xt_nganh_tohop`
--

DROP TABLE IF EXISTS `xt_nganh_tohop`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `xt_nganh_tohop` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `manganh` varchar(45) NOT NULL,
  `matohop` varchar(45) NOT NULL,
  `th_mon1` varchar(10) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL,
  `hsmon1` tinyint(4) DEFAULT NULL,
  `th_mon2` varchar(10) DEFAULT NULL,
  `hsmon2` tinyint(4) DEFAULT NULL,
  `th_mon3` varchar(10) DEFAULT NULL,
  `hsmon3` tinyint(4) DEFAULT NULL,
  `tb_keys` varchar(45) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT 'manganh_matohop',
  `N1` tinyint(1) DEFAULT NULL,
  `TO` tinyint(1) DEFAULT NULL,
  `LI` tinyint(1) DEFAULT NULL,
  `HO` tinyint(1) DEFAULT NULL,
  `SI` tinyint(1) DEFAULT NULL,
  `VA` tinyint(1) DEFAULT NULL,
  `SU` tinyint(1) DEFAULT NULL,
  `DI` tinyint(1) DEFAULT NULL,
  `TI` tinyint(1) DEFAULT NULL,
  `KHAC` tinyint(1) DEFAULT NULL,
  `KTPL` tinyint(1) DEFAULT NULL,
  `dolech` decimal(6,2) DEFAULT 0.00,
  PRIMARY KEY (`id`),
  UNIQUE KEY `key_UNIQUE` (`tb_keys`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `xt_nganh_tohop`
--

LOCK TABLES `xt_nganh_tohop` WRITE;
/*!40000 ALTER TABLE `xt_nganh_tohop` DISABLE KEYS */;
INSERT INTO `xt_nganh_tohop` VALUES (1,'7480201','A00','TO',1,'LI',1,'HO',1,'7480201_A00',0,1,1,1,NULL,0,NULL,NULL,NULL,NULL,NULL,0.00),(2,'7480201','A01','TO',1,'LI',1,'N1',1,'7480201_A01',1,1,1,0,NULL,0,NULL,NULL,NULL,NULL,NULL,-0.50),(3,'7140209','A00','TO',2,'LI',1,'HO',1,'7140209_A00',0,1,1,1,NULL,0,NULL,NULL,NULL,NULL,NULL,0.00),(4,'7340101','D01','TO',1,'VA',1,'N1',1,'7340101_D01',1,1,0,0,NULL,1,NULL,NULL,NULL,NULL,NULL,0.00),(6,'7340102','A00','TO',1,'LI',1,'HO',1,'7340102_A00',0,1,1,1,0,0,0,0,0,0,0,0.00),(9,'7340103','A00','TO',1,'LI',1,'HO',1,'7340103_A00',0,1,1,1,0,0,0,0,0,0,0,1.21),(10,'7340103','B00','TO',1,'HO',1,'SI',1,'7340103_B00',0,1,0,1,1,0,0,0,0,0,0,0.00);
/*!40000 ALTER TABLE `xt_nganh_tohop` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `xt_nguyenvongxettuyen`
--

DROP TABLE IF EXISTS `xt_nguyenvongxettuyen`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `xt_nguyenvongxettuyen` (
  `idnv` int(11) NOT NULL AUTO_INCREMENT,
  `nn_cccd` varchar(45) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `nv_manganh` varchar(45) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `nv_tt` int(11) NOT NULL,
  `diem_thxt` decimal(10,5) DEFAULT NULL COMMENT 'đã cộng điểm môn chính',
  `diem_utqd` decimal(10,5) DEFAULT NULL COMMENT 'Điểm UTQD theo tổ họp sẽ khác nhau.',
  `diem_cong` decimal(6,2) DEFAULT NULL COMMENT 'Tong 3 mon chua tinh mon chinh + diem uu tien\\\\\\\\n',
  `diem_xettuyen` decimal(10,5) DEFAULT NULL COMMENT 'đã cộng điểm ưu tiên',
  `nv_ketqua` varchar(45) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL,
  `nv_keys` varchar(45) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL,
  `tt_phuongthuc` varchar(45) DEFAULT NULL,
  `tt_thm` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`idnv`),
  UNIQUE KEY `nv_keys_UNIQUE` (`nv_keys`)
) ENGINE=InnoDB AUTO_INCREMENT=20 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `xt_nguyenvongxettuyen`
--

LOCK TABLES `xt_nguyenvongxettuyen` WRITE;
/*!40000 ALTER TABLE `xt_nguyenvongxettuyen` DISABLE KEYS */;
INSERT INTO `xt_nguyenvongxettuyen` VALUES (12,'079204000111','7480201',1,NULL,NULL,NULL,NULL,NULL,'079204000111_1',NULL,'A00'),(19,'062205007194','7480201',1,NULL,NULL,NULL,NULL,NULL,'062205007194_1',NULL,'A00');
/*!40000 ALTER TABLE `xt_nguyenvongxettuyen` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `xt_thisinhxettuyen25`
--

DROP TABLE IF EXISTS `xt_thisinhxettuyen25`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `xt_thisinhxettuyen25` (
  `idthisinh` int(11) NOT NULL AUTO_INCREMENT,
  `cccd` varchar(20) DEFAULT NULL,
  `sobaodanh` varchar(45) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL,
  `ho` varchar(100) CHARACTER SET utf8 COLLATE utf8_unicode_ci DEFAULT NULL,
  `ten` varchar(100) CHARACTER SET utf8 COLLATE utf8_unicode_ci DEFAULT NULL,
  `ngay_sinh` varchar(45) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL,
  `dien_thoai` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL,
  `password` varchar(100) CHARACTER SET utf8 COLLATE utf8_unicode_ci DEFAULT NULL,
  `gioi_tinh` varchar(10) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL,
  `email` varchar(100) CHARACTER SET utf8 COLLATE utf8_unicode_ci DEFAULT NULL,
  `noi_sinh` varchar(45) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL,
  `updated_at` date DEFAULT NULL,
  `doi_tuong` varchar(45) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL,
  `khu_vuc` varchar(45) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL,
  PRIMARY KEY (`idthisinh`),
  UNIQUE KEY `cccd_UNIQUE` (`cccd`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `xt_thisinhxettuyen25`
--

LOCK TABLES `xt_thisinhxettuyen25` WRITE;
/*!40000 ALTER TABLE `xt_thisinhxettuyen25` DISABLE KEYS */;
INSERT INTO `xt_thisinhxettuyen25` VALUES (1,'079204000111','02000001','Nguyễn Văn','An','2026-04-17','0901234567','123456','Nam','ttt@gmail.com','Kon Tum',NULL,'01','KV1'),(2,'079204000222','02000002','Trần Thị','Bảo','2000-12-24','0987654321',NULL,'Nữ','pttttt@gmail.com','Kon Tum','2026-04-20','01','KV2'),(3,'079204000333','02000003','Lê Hoàng','Cường','2005-11-12','0911222333',NULL,'Nam','ttty@gmail.com','Kon Tum','2026-04-20','01','KV3'),(4,'062205007194','02000004','Phạm Thanh','Tuấn','2005-12-17','0942375609','123456','Nam','ptt@gmail.com','Kon Tum','2026-04-20','02','KV1');
/*!40000 ALTER TABLE `xt_thisinhxettuyen25` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `xt_tohop_monthi`
--

DROP TABLE IF EXISTS `xt_tohop_monthi`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `xt_tohop_monthi` (
  `idtohop` int(11) NOT NULL AUTO_INCREMENT,
  `matohop` varchar(45) NOT NULL,
  `mon1` varchar(10) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `mon2` varchar(10) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `mon3` varchar(10) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `tentohop` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL,
  PRIMARY KEY (`idtohop`),
  UNIQUE KEY `matohop_UNIQUE` (`matohop`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `xt_tohop_monthi`
--

LOCK TABLES `xt_tohop_monthi` WRITE;
/*!40000 ALTER TABLE `xt_tohop_monthi` DISABLE KEYS */;
INSERT INTO `xt_tohop_monthi` VALUES (1,'A00','TO','LI','HO','Toán, Vật lí, Hóa học'),(2,'A01','TO','LI','N1','Toán, Vật lí, Tiếng Anh'),(3,'D01','TO','VA','N1','Toán, Ngữ văn, Tiếng Anh'),(4,'B00','TO','HO','SI','Toán, Hóa học, Sinh học'),(5,'C00','VA','SU','DI','Văn, Sử, Địa');
/*!40000 ALTER TABLE `xt_tohop_monthi` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-04-20 16:48:27
