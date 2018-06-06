DROP TABLE IF EXISTS `river_flows`;

CREATE TABLE `river_flows` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `date` date DEFAULT NULL COMMENT '日期',
  `river_name` varchar(225) DEFAULT NULL COMMENT '河流名称',
  `flow` float DEFAULT NULL COMMENT '流量(m^3/s)',
  `created_at` datetime DEFAULT NULL COMMENT '创建时间',
  `updated_at` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;