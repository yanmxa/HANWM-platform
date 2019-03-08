DROP TABLE IF EXISTS `farmer_anchors`;

CREATE TABLE `farmer_anchors` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `sim_id` int(11) unsigned DEFAULT NULL COMMENT '模拟编号',
  `farmer_id` int(11) unsigned DEFAULT NULL COMMENT '农民序号',
  `time` datetime DEFAULT NULL COMMENT '模拟时间',
  `previous_mu` float(6, 2) DEFAULT NULL COMMENT '上一年旱地占比',
  `current_mu` float(6, 2) DEFAULT NULL COMMENT '本年度旱地占比',
  `precipitation` double(16, 2) DEFAULT NULL COMMENT '年度降雨量(mm)',
  `rice_precipitation` double(16, 2) DEFAULT NULL COMMENT '水稻生长期降雨量(mm)',
  `maize_precipitation` double(16, 2) DEFAULT NULL COMMENT '玉米生长期降雨量(mm)',
  `consumer_water` double(16, 2) DEFAULT NULL COMMENT '水量消耗(m^3)',
  `rice_consumer_water` double(16, 2) DEFAULT NULL COMMENT '水稻水量消耗(m^3)',
  `maize_consumer_water` double(16, 2) DEFAULT NULL COMMENT '玉米水量消耗(m^3)',
  `rice_yield` double(16, 2) DEFAULT NULL COMMENT '水稻产量(kg/mu)',
  `maize_yield` double(16, 2) DEFAULT NULL COMMENT '玉米产量(kg/mu)',
  `rice_area` double(16, 2) DEFAULT NULL COMMENT '水稻面积(mu)',
  `maize_area` double(16, 2) DEFAULT NULL COMMENT '玉米面积(mu)',
  `crop_income` double(16, 2) DEFAULT NULL COMMENT '作物收入(yuan)',
  `rice_income` double(16, 2) DEFAULT NULL COMMENT '水稻收入(yuan)',
  `maize_income` double(16, 2) DEFAULT NULL COMMENT '玉米收入(yuan)',
  `water_limit` double(16, 2) DEFAULT NULL COMMENT '年度水量限制(m^3)',
  `water_remaining` double(16, 2) DEFAULT NULL COMMENT '剩余水量(m^3)',
  `created_at` datetime DEFAULT NULL COMMENT '创建时间',
  `updated_at` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;