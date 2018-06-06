DROP TABLE IF EXISTS `stations`;

CREATE TABLE `stations` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `station_id` int(11) unsigned NOT NULL COMMENT '站点编号',
  `station_name` varchar(255) DEFAULT NULL COMMENT '站点名称',
  `lat` double(11, 4) DEFAULT NULL COMMENT '纬度',
  `lon` double(11, 4) DEFAULT NULL COMMENT '经度',
  `province` varchar(255) DEFAULT NULL COMMENT '省',
  `city` varchar(255) DEFAULT NULL COMMENT '市',
  `district` varchar(255) DEFAULT NULL COMMENT '区',
  `startyear` date DEFAULT NULL COMMENT '监测开始日期',
  `endyear` date DEFAULT NULL COMMENT '监测结束日期',
  `created_at` datetime DEFAULT NULL COMMENT '创建时间',
  `updated_at` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
