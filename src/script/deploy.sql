
-- 记录设备启动日志
DROP TABLE IF EXISTS `ulaunchog`;
CREATE TABLE `ulaunchlog` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `iccid` varchar(255) DEFAULT NULL,
  `imei` varchar(255) DEFAULT NULL,
  `imsi` varchar(255) DEFAULT NULL,
  `ip` varchar(255) DEFAULT NULL,
  `model` varchar(255) DEFAULT NULL COMMENT '手机型号',
  `name` varchar(255) DEFAULT NULL,
  `plugins` varchar(255) DEFAULT NULL,
  `sdkver` varchar(255) DEFAULT NULL COMMENT '操作系统版本',
  `type` varchar(255) DEFAULT NULL,
  `uappid` varchar(255) DEFAULT NULL,
  `uappkey` varchar(255) DEFAULT NULL,
  `uchannel` varchar(255) DEFAULT NULL,
  `version` varchar(255) DEFAULT NULL,
  `position` varchar(32) DEFAULT NULL COMMENT '经纬度，精度与纬度都留小数点后四位，用英文逗号分隔拼接成字符串,如 62.5352,136.4526',
  `netype` varchar(6) DEFAULT NULL COMMENT '网络类型,wifi或3G或4G',
  `isp` varchar(32) DEFAULT NULL COMMENT '运营商',
  `dpi` varchar(32) DEFAULT NULL COMMENT '屏幕分辨率,示例：1080*1920*480',
  `launchtime` bigint NOT NULL COMMENT '启动时间，即记录时间',
  PRIMARY KEY (`id`),
  INDEX `imei`(`imei`)
) ENGINE=InnoDB AUTO_INCREMENT=121 DEFAULT CHARSET=utf8;

-- 记录游戏内角色数据
DROP TABLE IF EXISTS `ugamerole`;
CREATE TABLE `ugamerole`(
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `userID` int(11) NOT NULL COMMENT 'U8内部userid',
  `roleID` int(11) COMMENT '游戏内角色ID',
  `roleName` varchar(32) COMMENT '游戏内角色名称',
  `roleLevel` tinyint(2) COMMENT '游戏内角色等级',
  `serverID` int(11) COMMENT '游戏服ID',
  `serverName` int(11) COMMENT '游戏服名称',
  `moneyNum` int(11) COMMENT '角色游戏币数量。游戏内有多种游戏币，则返回只能用人民币充值换取的那种游戏币；若有多种游戏币都能用人民币充值换取，则选价值最小的那种，同时把其它种类游戏币按照游戏内换算比例折算成价值最小游戏币，并返回总数。',
  `onlineLength` int(11) COMMENT '角色总在线时长',
  `createTime` int(11) NOT NULL COMMENT '记录时间',
  PRIMARY KEY(`id`),
  INDEX `user`(`userID`),
  INDEX `role`(`roleName`),
  INDEX `server`(`serverName`),
  UNIQUE INDEX `ui`(`userID`,`roleID`,`serverID`)
)ENGINE=InnoDB AUTO_INCREMENT=121 DEFAULT CHARSET=utf8;