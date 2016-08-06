package com.u8.server.web;

import java.util.Date;

import net.sf.json.JSONObject;

import org.apache.http.util.TextUtils;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.u8.server.common.UActionSupport;
import com.u8.server.constants.StateCode;
import com.u8.server.data.UGameRole;
import com.u8.server.log.Log;
import com.u8.server.service.UGameManager;
import com.u8.server.service.UGameRoleManager;
import com.u8.server.service.UUserManager;

@Controller
@Namespace("/role")
public class RoleAction extends UActionSupport{

	private int userID;
	private int appID;
	private int channelID;
	private String roleID;
	private String roleName;
	private int roleLevel;
	private String serverID;
	private String serverName;
	private int moneyNum;
	private Long onlineLength;
	
    @Autowired
    private UGameManager gameManager;
    
    @Autowired
    private UUserManager userManager;
    
    @Autowired
    private UGameRoleManager gameRoleManager;
    
    @Action("submitData")
    public void submitRoleData(){
    	
    	Log.d("submitRoleData...");
    	
    	if(userID < 0 || TextUtils.isEmpty(roleID) || TextUtils.isEmpty(serverID)){
    		
    		renderState(StateCode.CODE_SUBMIT_PARAMS_ERROR, null);
    		return;
    	}
    	
    	try {
			UGameRole role = new UGameRole();
			role.setMoneyNum(moneyNum);
			role.setRoleID(roleID);
			role.setAppID(appID);
			role.setChannelID(channelID);
			role.setRoleLevel(roleLevel);
			role.setRoleName(roleName);
			role.setServerID(serverID);
			role.setServerName(serverName);
			role.setUserID(userID);
			role.setOnlineLength(onlineLength);
			role.setCreateTime(new Date());
			gameRoleManager.saveLog(role);
		} catch (Exception e) {
			
			renderState(StateCode.CODE_SUBMIT_PARAMS_FAILED, null);
			Log.e("角色数据保存出错： " + e.getMessage());
		}
    	
    	renderState(StateCode.CODE_SUCCESS, null);
    }
    
    private void renderState(int state, JSONObject data){
        try{


            JSONObject json = new JSONObject();
            json.put("state", state);
            json.put("data", data);

            super.renderJson(json.toString());

        }catch(Exception e){
            e.printStackTrace();
            Log.e(e.getMessage());
        }
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

	public int getRoleLevel() {
		return roleLevel;
	}

	public void setRoleLevel(int roleLevel) {
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

	public int getMoneyNum() {
		return moneyNum;
	}

	public void setMoneyNum(int moneyNum) {
		this.moneyNum = moneyNum;
	}

	public Long getOnlineLength() {
		return onlineLength;
	}

	public void setOnlineLength(Long onlineLength) {
		this.onlineLength = onlineLength;
	}

	public int getUserID() {
		return userID;
	}

	public void setUserID(int userID) {
		this.userID = userID;
	}

	public int getAppID() {
		return appID;
	}

	public void setAppID(int appID) {
		this.appID = appID;
	}

	public int getChannelID() {
		return channelID;
	}

	public void setChannelID(int channelID) {
		this.channelID = channelID;
	}
    
}
