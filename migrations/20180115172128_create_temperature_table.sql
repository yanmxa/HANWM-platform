DROP TABLE IF EXISTS `temperatures`;

CREATE TABLE `temperatures` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `county` varchar(225) DEFAULT NULL COMMENT '县级区域',
  `date` date DEFAULT NULL COMMENT '日期',
  `degree` float DEFAULT NULL COMMENT '温度值',
  `created_at` datetime DEFAULT NULL COMMENT '创建时间',
  `updated_at` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;