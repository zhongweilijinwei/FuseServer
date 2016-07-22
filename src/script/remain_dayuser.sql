BEGIN
ALTER TABLE udayuser DISABLE KEYS;
REPLACE INTO `udayuser`
 (`loginDate`,`regDate`,`userID`,`agentUserID`,`appID`,`agentID`)
 SELECT
 FROM_UNIXTIME(`lastLoginTime`/1000,"%Y-%m-%d") AS `loginDate`,
 DATE_FORMAT(`createTime`,"%Y-%m-%d") AS `regDate`,
 `id` AS `userID`,
 `channelUserID` AS `agentUserID`,
 `appID`,
 `channelID` AS `agentID`
 FROM uuser
 WHERE FROM_UNIXTIME(`lastLoginTime`/1000,"%Y-%m-%d")=CURDATE();
ALTER TABLE udayuser ENABLE KEYS;
END