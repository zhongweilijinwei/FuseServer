package com.u8.server.data;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "UGameRole")
public class UGameRole implements Serializable{

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public Integer getUserID() {
		return userID;
	}
	public void setUserID(Integer userID) {
		this.userID = userID;
	}
	public Integer getAppID() {
		return appID;
	}
	public void setAppID(Integer appID) {
		this.appID = appID;
	}
	public Integer getChannelID() {
		return channelID;
	}
	public void setChannelID(Integer channelID) {
		this.channelID = channelID;
	}
	public String getRoleID() {
		return roleID;
	}
	public void setRoleID(String roleID) {
		this.roleID = roleID;
	}
	public String getRoleName() {
		return roleName;
	}
	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}
	public Integer getRoleLevel() {
		return roleLevel;
	}
	public void setRoleLevel(Integer roleLevel) {
		this.roleLevel = roleLevel;
	}
	public String getServerID() {
		return serverID;
	}
	public void setServerID(String serverID) {
		this.serverID = serverID;
	}
	public String getServerName() {
		return serverName;
	}
	public void setServerName(String serverName) {
		this.serverName = serverName;
	}
	public Integer getMoneyNum() {
		return moneyNum;
	}
	public void setMoneyNum(Integer moneyNum) {
		this.moneyNum = moneyNum;
	}
	public Long getOnlineLength() {
		return onlineLength;
	}
	public void setOnlineLength(Long onlineLength) {
		this.onlineLength = onlineLength;
	}
	public Date getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	private Integer id;
	private Integer appID;
	private Integer channelID;
	private Integer userID;
	private String roleID;
	private String roleName;
	private Integer roleLevel;
	private String serverID;
	private String serverName;
	private Integer moneyNum;
	private Long onlineLength;
	private Date createTime;
	
}
