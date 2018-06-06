DROP TABLE IF EXISTS `country_inits`;

CREATE TABLE `country_inits` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `farmer_id` int(11) DEFAULT NULL COMMENT '农户编号',
  `sim_id` int(11) DEFAULT NULL COMMENT '模拟序号',
  `location` varchar(225) DEFAULT NULL COMMENT '位置',
  `area` double(11, 2) DEFAULT NULL COMMENT '面积',
  `mu` float(8, 2) DEFAULT NULL COMMENT '旱地占比',
  `learn` float(8, 2) DEFAULT NULL COMMENT '学习因子',
  `radius` float(8, 2) DEFAULT NULL COMMENT '信息半径',
  `sense` float(8, 2) DEFAULT NULL COMMENT '信息敏感度',
  `created_at` datetime DEFAULT NULL COMMENT '创建时间',
  `updated_at` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
