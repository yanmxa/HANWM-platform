DROP TABLE IF EXISTS `farmer_inits`;

CREATE TABLE `farmer_inits` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT COMMENT '农民序号',
  `sim_id` int(11) unsigned DEFAULT NULL COMMENT '模拟序号',
  `location` varchar(225) DEFAULT NULL COMMENT '农民地点',
  `farmer_no` int DEFAULT NULL COMMENT '农户编号(以村为单)',
  `mu` float(8, 2) DEFAULT NULL COMMENT '旱地占比',
  `learn` float(8, 2) DEFAULT NULL COMMENT '学习因子',
  `radius` float(8, 2) DEFAULT NULL COMMENT '信息半径',
  `sense` float(8, 2) DEFAULT NULL COMMENT '信息敏感度',
  `farmer_number` int(11) DEFAULT NULL COMMENT '农户数量(所在村农户数量)',
  `crop_area` double(8, 2) DEFAULT NULL COMMENT '耕地面积(mu)',
  `water_permit` double(8, 2) DEFAULT NULL COMMENT '用水限制(m^3/year)',
  `created_at` datetime DEFAULT NULL COMMENT '创建时间',
  `updated_at` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
