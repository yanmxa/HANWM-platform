DROP TABLE IF EXISTS `rains`;

CREATE TABLE `rains` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `date` date DEFAULT NULL COMMENT '日期',
  `station` varchar(225) DEFAULT NULL COMMENT '检测站点',
  `rainfall` float DEFAULT NULL COMMENT '雨量值',
  `created_at` datetime DEFAULT NULL COMMENT '创建时间',
  `updated_at` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
