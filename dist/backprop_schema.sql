CREATE DATABASE  IF NOT EXISTS `bp_train` /*!40100 DEFAULT CHARACTER SET utf8 COLLATE utf8_bin */;
USE `bp_train`;
-- MySQL dump 10.13  Distrib 5.5.31, for debian-linux-gnu (x86_64)
--
-- Host: localhost    Database: backprop_trainer
-- ------------------------------------------------------
-- Server version	5.5.31-0+wheezy1

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `training_mse_readout`
--

DROP TABLE IF EXISTS `training_mse_readout`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `training_mse_readout` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `readout_number` int(11) NOT NULL,
  `value` double NOT NULL,
  `test_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `id` (`id`),
  KEY `FK31173553EF857B6D` (`test_id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 COLLATE=utf8_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `training_mse_readout`
--

LOCK TABLES `training_mse_readout` WRITE;
/*!40000 ALTER TABLE `training_mse_readout` DISABLE KEYS */;
/*!40000 ALTER TABLE `training_mse_readout` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `validation_mse_readout`
--

DROP TABLE IF EXISTS `validation_mse_readout`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `validation_mse_readout` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `readout_number` int(11) NOT NULL,
  `value` double NOT NULL,
  `test_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `id` (`id`),
  KEY `FKF9E8FE72EF857B6D` (`test_id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 COLLATE=utf8_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `validation_mse_readout`
--

LOCK TABLES `validation_mse_readout` WRITE;
/*!40000 ALTER TABLE `validation_mse_readout` DISABLE KEYS */;
/*!40000 ALTER TABLE `validation_mse_readout` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `peceptron_test`
--

DROP TABLE IF EXISTS `peceptron_test`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `peceptron_test` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `bp_epochs` int(11) NOT NULL,
  `confusion_matrix` mediumblob NOT NULL,
  `dataset_label` text COLLATE utf8_bin NOT NULL,
  `date_finished` datetime NOT NULL,
  `date_started` datetime NOT NULL,
  `efficiency_factor_result` double NOT NULL,
  `goal_training_mse` double NOT NULL,
  `perceptron_label` text COLLATE utf8_bin NOT NULL,
  `total_perceptron_rate` double NOT NULL,
  `training_mse_result` double NOT NULL,
  `trf` double NOT NULL,
  `validation_mse_result` double NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `id` (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 COLLATE=utf8_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `peceptron_test`
--

LOCK TABLES `peceptron_test` WRITE;
/*!40000 ALTER TABLE `peceptron_test` DISABLE KEYS */;
/*!40000 ALTER TABLE `peceptron_test` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2013-10-18 15:02:16
