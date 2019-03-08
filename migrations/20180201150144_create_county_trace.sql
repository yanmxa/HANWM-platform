DROP TABLE IF EXISTS `country_traces`;

CREATE TABLE `country_traces` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `sim_id` int(11) DEFAULT NULL COMMENT '模拟序号',
  `time` datetime DEFAULT NULL COMMENT '模拟年份',
  `precipitation` double(11, 2) DEFAULT NULL COMMENT '降雨量(mm)',
  `crop_irrigation` double(16, 2) DEFAULT NULL COMMENT '作物水量消耗(m^3)',
  `rice_irrigation` double(16, 2) DEFAULT NULL COMMENT '水稻水量消耗(m^3)',
  `maize_irrigation` double(16, 2) DEFAULT NULL COMMENT '玉米水量消耗(m^3)',
  `crop_income` double(16, 2) DEFAULT NULL COMMENT '作物收入(yuan)',
  `rice_income` double(16, 2) DEFAULT NULL COMMENT '水稻收入(yuan)',
  `maize_income` double(16, 2) DEFAULT NULL COMMENT '玉米收入(yuan)',
  `created_at` datetime DEFAULT NULL COMMENT '创建时间',
  `updated_at` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;