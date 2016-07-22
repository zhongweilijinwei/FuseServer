CREATE TABLE `udayuser`(
    `loginDate` date NOT NULL COMMENT '登陆日期',
    `regDate` date NOT NULL COMMENT '注册日期',
    `userID` int(11) NOT NULL COMMENT 'U8内用户ID',
    `agentUserID` varchar(32) NOT NULL COMMENT '渠道内用户id',
    `appID` int(11) NOT NULL COMMENT '游戏id',
    `agentID` int(11) NOT NULL COMMENT '渠道id',
    UNIQUE INDEX(`loginDate`,`userID`)
)ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=12;