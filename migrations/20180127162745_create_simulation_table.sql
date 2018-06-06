DROP TABLE IF EXISTS `simulations`;

CREATE TABLE `simulations` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT COMMENT '模拟序号',
  `thread_id` int(11) unsigned NOT NULL COMMENT '进程ID/线程ID',
  `time_step` varchar(225) DEFAULT NULL COMMENT '模拟步长',
  `start_time` varchar(255) DEFAULT NULL COMMENT '起始时间',
  `end_time` varchar(255) DEFAULT NULL COMMENT '结束时间',
  `anchor_time` varchar(255) DEFAULT NULL COMMENT '锚点时间',
  `mu` float(8, 2) DEFAULT NULL COMMENT '旱地占比',
  `learn` float(8, 2) DEFAULT NULL COMMENT '学习因子',
  `radius` float(8, 2) DEFAULT NULL COMMENT '信息半径',
  `sense` float(8, 2) DEFAULT NULL COMMENT '信息敏感度',
  `cv` float(8, 2) DEFAULT NULL COMMENT '变异系数',
  `components` TEXT(2000) DEFAULT NULL COMMENT '模拟模块',
  `farmer_number` TEXT(2000) DEFAULT NULL COMMENT '农户数量',
  `crop_area` TEXT(2000) DEFAULT NULL COMMENT '人均面积',
  `water_limit` TEXT(2000) DEFAULT NULL COMMENT '用水限制',
  `created_at` datetime DEFAULT NULL COMMENT '创建时间',
  `updated_at` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;