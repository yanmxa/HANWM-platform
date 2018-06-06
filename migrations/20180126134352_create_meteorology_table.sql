DROP TABLE IF EXISTS `meteorologies`;

CREATE TABLE `meteorologies` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `station_id` int(11) unsigned NOT NULL COMMENT '站点编号',
  `date` date DEFAULT NULL COMMENT '日期',
  `avg_temp` double(11, 2) DEFAULT NULL COMMENT '日均温度(摄氏度)',
  `max_temp` double(11, 2) DEFAULT NULL COMMENT '日最高温(摄氏度)',
  `min_temp` double(11, 2) DEFAULT NULL COMMENT '日最低温度(摄氏度)',
  `slp` double(11, 2) DEFAULT NULL COMMENT '海平面气压(帕)',
  `stp` double(11, 2) DEFAULT NULL COMMENT '监测站气压(帕)',
  `precipition` double(11, 2) DEFAULT NULL COMMENT '日降雨量(毫米)',
  `sndp` double(11, 2) DEFAULT NULL COMMENT '降雪深度(毫米)',
  `created_at` datetime DEFAULT NULL COMMENT '创建时间',
  `updated_at` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
