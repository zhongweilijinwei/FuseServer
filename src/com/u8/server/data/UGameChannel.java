package com.u8.server.data;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import net.sf.json.JSONObject;

import com.u8.server.cache.CacheManager;
/**
 * 游戏渠道管理
 * @author Administrator
 *
 */

@Entity
@Table(name = "ugamechannel")
public class UGameChannel implements Serializable{
	  @Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	/**
	 * 登录渠道编号
	 */
	private Integer loginChId;
	/**
	 * 编号
	 */
	private Integer code;
	


	/**
	 * 支付渠道编号
	 */
	private Integer payChId;
	/**
	 * 游戏编号
	 */
	private Integer appId;
	
    public UGame getGame(){

        return CacheManager.getInstance().getGame(appId);
    }
    
    
    public UChannel getUChannel(int channelID){
    	return CacheManager.getInstance().getChannel(channelID);
    }
    
    public JSONObject toApiJSON(){
    	   JSONObject json = new JSONObject();
           json.put("id", id);
           json.put("code", code);
           UChannel pUChannel=this.getUChannel(payChId);
           if(null!=pUChannel){
        	   UChannelMaster pUChannelMaster = pUChannel.getMaster();
        	   JSONObject pJson = new JSONObject();
        	   if(null != pUChannelMaster){
        		   
        		   pJson.put("sdkName", pUChannelMaster.getSdkName());
        	   }
        	   json.put("payChannel", pJson);
           }
           UChannel lUChannel=this.getUChannel(loginChId);
           if(null!=lUChannel){
        	   UChannelMaster lUChannelMaster = lUChannel.getMaster();
        	   JSONObject lJson = new JSONObject();
        	   if(null != lUChannelMaster){
        		   
        		   lJson.put("sdkName", lUChannelMaster.getSdkName());
        	   }
        	   json.put("loginChannel", lJson);
           }
           return json;
    }
    
    public JSONObject toListJSON(){

        JSONObject json = new JSONObject();
        json.put("id", id);
        json.put("loginChId", loginChId);
        json.put("payChId", payChId);
    
        json.put("appId", appId);
        json.put("code", code);
        json.put("gameName", getGame().getName());
        
        UChannel pUChannel=this.getUChannel(payChId);
        if(null!=pUChannel){
            json.put("payMaster", pUChannel.getMaster().getMasterName());
        }
        UChannel lUChannel=this.getUChannel(loginChId);
        if(null!=lUChannel){
            json.put("loginMaster", lUChannel.getMaster().getMasterName());
        }

        return json;
    }
    
    public JSONObject toJSON(){

        JSONObject json = new JSONObject();
        json.put("id", id);
        json.put("loginChId", loginChId);
        json.put("payChId", payChId);
        json.put("appId", appId);
        json.put("code", code);
     
        json.put("gameName", getGame().getName());

        return json;
    }
    public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getLoginChId() {
		return loginChId;
	}

	public void setLoginChId(Integer loginChId) {
		this.loginChId = loginChId;
	}

	public Integer getPayChId() {
		return payChId;
	}

	public void setPayChId(Integer payChId) {
		this.payChId = payChId;
	}

	public Integer getAppId() {
		return appId;
	}

	public void setAppId(Integer appId) {
		this.appId = appId;
	}

	public Integer getCode() {
		return code;
	}

	public void setCode(Integer code) {
		this.code = code;
	}
}
