
-- 记录设备启动日志
DROP TABLE IF EXISTS `ulaunchog`;
CREATE TABLE `ulaunchlog` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `iccid` varchar(255) DEFAULT NULL COMMENT 'sim卡卡号',
  `imei` varchar(255) DEFAULT NULL COMMENT '移动设备标识号',
  `imsi` varchar(255) DEFAULT NULL COMMENT '移动用户识别码',
  `ip` varchar(255) DEFAULT NULL COMMENT '用户ip地址',
  `model` varchar(255) DEFAULT NULL COMMENT '用户手机型号',
  `plugins` varchar(255) DEFAULT NULL COMMENT '当前sdk已安装插件信息列表，json形式保存',
  `sdkver` varchar(255) DEFAULT NULL COMMENT '用户手机系统版本',
  `uappid` varchar(255) DEFAULT NULL COMMENT 'sdk appid',
  `uappkey` varchar(255) DEFAULT NULL COMMENT 'sdk appkey',
  `uchannel` varchar(255) DEFAULT NULL COMMENT '渠道id',
  `version` varchar(255) DEFAULT NULL COMMENT 'sdk版本号', 
  `position` varchar(32) DEFAULT NULL COMMENT '经纬度，精度与纬度都留小数点后四位，用英文逗号分隔拼接成字符串,如 62.5352,136.4526',
  `netype` varchar(6) DEFAULT NULL COMMENT '网络类型,wifi或3G或4G',
  `isp` varchar(32) DEFAULT NULL COMMENT '运营商',
  `dpi` varchar(32) DEFAULT NULL COMMENT '屏幕分辨率,示例：1080*1920*480',
  `launchtime` datetime NOT NULL COMMENT '启动时间，即记录时间',
  PRIMARY KEY (`id`),
  INDEX `imei`(`imei`)
) ENGINE=InnoDB AUTO_INCREMENT=121 DEFAULT CHARSET=utf8;

-- 记录游戏内角色数据
DROP TABLE IF EXISTS `ugamerole`;
CREATE TABLE `ugamerole`(
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `userID` int(11) NOT NULL COMMENT 'U8内部userid',
  `appID` int(11) NOT NULL COMMENT '应用id',
  `channelID` int(11) NOT NULL COMMENT '渠道id',
  `roleID` varchar(32) COMMENT '游戏内角色ID',
  `roleName` varchar(32) COMMENT '游戏内角色名称',
  `roleLevel` int(11) COMMENT '游戏内角色等级',
  `serverID` varchar(32) COMMENT '游戏服ID',
  `serverName` varchar(32) COMMENT '游戏服名称',
  `moneyNum` int(11) COMMENT '角色游戏币数量。游戏内有多种游戏币，则返回只能用人民币充值换取的那种游戏币；若有多种游戏币都能用人民币充值换取，则选价值最小的那种，同时把其它种类游戏币按照游戏内换算比例折算成价值最小游戏币，并返回总数。',
  `onlineLength` bigint COMMENT '角色总在线时长(秒)',
  `createTime` datetime NOT NULL COMMENT '记录时间',
  PRIMARY KEY(`id`),
  INDEX `ctime`(`createTime`),
  INDEX `user`(`userID`),
  INDEX `appid`(`appID`),
  INDEX `channel`(`channelID`),
  INDEX `rolelv`(`roleLevel`)
)ENGINE=InnoDB AUTO_INCREMENT=121 DEFAULT CHARSET=utf8;