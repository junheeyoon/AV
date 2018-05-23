-- --------------------------------------------------------
-- 호스트:                          127.0.0.1
-- 서버 버전:                        5.5.59 - MySQL Community Server (GPL)
-- 서버 OS:                        Linux
-- HeidiSQL 버전:                  9.5.0.5196
-- --------------------------------------------------------

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET NAMES utf8 */;
/*!50503 SET NAMES utf8mb4 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;


-- voice 데이터베이스 구조 내보내기
DROP DATABASE IF EXISTS `voice`;
CREATE DATABASE IF NOT EXISTS `voice` /*!40100 DEFAULT CHARACTER SET utf8 */;
USE `voice`;

-- 테이블 voice.family 구조 내보내기
DROP TABLE IF EXISTS `family`;
CREATE TABLE IF NOT EXISTS `family` (
  `family_id` int(11) NOT NULL,
  `family_adress` int(11) NOT NULL,
  `family_number` varchar(50) COLLATE utf8_unicode_ci NOT NULL,
  PRIMARY KEY (`family_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

-- 테이블 데이터 voice.family:~4 rows (대략적) 내보내기
DELETE FROM `family`;
/*!40000 ALTER TABLE `family` DISABLE KEYS */;
INSERT INTO `family` (`family_id`, `family_adress`, `family_number`) VALUES
	(1, 23, '가족번호1'),
	(2, 25, '가족번호2'),
	(3, 27, '가족번호3'),
	(4, 29, '가족번호4');
/*!40000 ALTER TABLE `family` ENABLE KEYS */;

-- 테이블 voice.object 구조 내보내기
DROP TABLE IF EXISTS `object`;
CREATE TABLE IF NOT EXISTS `object` (
  `object_id` int(11) NOT NULL,
  `obiect_f-id` int(11) NOT NULL,
  `object_state` int(11) NOT NULL,
  `object_name` varchar(50) COLLATE utf8_unicode_ci NOT NULL,
  `object_auth` varchar(50) COLLATE utf8_unicode_ci NOT NULL,
  `object_blue_id` varchar(50) COLLATE utf8_unicode_ci NOT NULL,
  KEY `FK_object_family` (`obiect_f-id`),
  CONSTRAINT `FK_object_family` FOREIGN KEY (`obiect_f-id`) REFERENCES `family` (`family_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

-- 테이블 데이터 voice.object:~1 rows (대략적) 내보내기
DELETE FROM `object`;
/*!40000 ALTER TABLE `object` DISABLE KEYS */;
INSERT INTO `object` (`object_id`, `obiect_f-id`, `object_state`, `object_name`, `object_auth`, `object_blue_id`) VALUES
	(1, 3, 0, '전등', '있음', '전등1');
/*!40000 ALTER TABLE `object` ENABLE KEYS */;

-- 테이블 voice.Persons 구조 내보내기
DROP TABLE IF EXISTS `Persons`;
CREATE TABLE IF NOT EXISTS `Persons` (
  `id` int(11) NOT NULL,
  `name` varchar(50) CHARACTER SET latin1 NOT NULL,
  `age` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

-- 테이블 데이터 voice.Persons:~1 rows (대략적) 내보내기
DELETE FROM `Persons`;
/*!40000 ALTER TABLE `Persons` DISABLE KEYS */;
INSERT INTO `Persons` (`id`, `name`, `age`) VALUES
	(1, '\'kim\'', 28);
/*!40000 ALTER TABLE `Persons` ENABLE KEYS */;

-- 테이블 voice.user 구조 내보내기
DROP TABLE IF EXISTS `user`;
CREATE TABLE IF NOT EXISTS `user` (
  `user_id` int(11) NOT NULL,
  `f-id` int(11) NOT NULL,
  `user_pw` int(11) NOT NULL,
  KEY `FK_user_family` (`f-id`),
  CONSTRAINT `FK_user_family` FOREIGN KEY (`f-id`) REFERENCES `family` (`family_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

-- 테이블 데이터 voice.user:~1 rows (대략적) 내보내기
DELETE FROM `user`;
/*!40000 ALTER TABLE `user` DISABLE KEYS */;
INSERT INTO `user` (`user_id`, `f-id`, `user_pw`) VALUES
	(1, 3, 12);
/*!40000 ALTER TABLE `user` ENABLE KEYS */;

/*!40101 SET SQL_MODE=IFNULL(@OLD_SQL_MODE, '') */;
/*!40014 SET FOREIGN_KEY_CHECKS=IF(@OLD_FOREIGN_KEY_CHECKS IS NULL, 1, @OLD_FOREIGN_KEY_CHECKS) */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
