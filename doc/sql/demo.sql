
CREATE DATABASE /*!32312 IF NOT EXISTS*/`demo` /*!40100 DEFAULT CHARACTER SET utf8mb4 */;

USE `demo`;

/*Table structure for table `user` */

DROP TABLE IF EXISTS `user`;

CREATE TABLE `user` (
  `id` bigint(20) DEFAULT NULL,
  `username` varchar(255) DEFAULT NULL,
  `password` varchar(128) DEFAULT NULL,
  `status` tinyint(4) DEFAULT NULL,
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `login_redirect_url` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

/*Data for the table `user` */

insert  into `user`(`id`,`username`,`password`,`status`,`create_time`,`update_time`,`login_redirect_url`) values
(1,'dushitaoyuan','$2a$10$wLtPgQMz0APAXLsTBkY.SOg2V1EyFZsPiYHlvqMEsoOvzjeL6aoSy',1,'2021-01-01 15:26:44','2021-01-01 15:26:44',NULL);

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
