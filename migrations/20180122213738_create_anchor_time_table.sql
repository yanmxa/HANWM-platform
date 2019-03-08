DROP TABLE IF EXISTS `anchor_times`;

CREATE TABLE `anchor_times` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `sim_id` int(11) DEFAULT NULL COMMENT '模拟序号',
  `year` int(11) DEFAULT NULL COMMENT '年份',
  `area` double(16, 2) DEFAULT NULL COMMENT '面积',
  `maize_area` double(16, 2) DEFAULT NULL COMMENT '玉米面积',
  `maize_subsidy` double(16, 2) DEFAULT NULL COMMENT '玉米补贴',
  `income` double(16, 2) DEFAULT NULL COMMENT '收入',
  `precipition` double(16, 2) DEFAULT NULL COMMENT '年度降雨',
  `eto` double(16, 2) DEFAULT NULL COMMENT '参考作物腾发量',
  `mu` float(8, 2) DEFAULT NULL COMMENT '旱地占比',
  `learn` float(8, 2) DEFAULT NULL COMMENT '学习因子',
  `radius` float(8, 2) DEFAULT NULL COMMENT '信息半径',
  `sense` float(8, 2) DEFAULT NULL COMMENT '信息敏感度',
  `created_at` datetime DEFAULT NULL COMMENT '创建时间',
  `updated_at` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
