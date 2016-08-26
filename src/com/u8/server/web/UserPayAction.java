package com.u8.server.web;

import com.u8.server.common.UActionSupport;
import com.u8.server.constants.StateCode;
import com.u8.server.log.Log;
import com.u8.server.sdk.UHttpAgent;
import com.u8.server.sdk.UHttpFutureCallback;
import com.u8.server.utils.JsonUtils;

import net.sf.json.JSONObject;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.springframework.stereotype.Controller;

import java.util.HashMap;
import java.util.Map;

/**
 * 这个类是模拟游戏服
 * 客户端-》第三方SDK登录成功之后，访问u8server获取token。
 * 当token获取成功之后，就会开始连接游戏服。这个类就是模拟
 * 登录流程的最后一步操作
 *
 * 客户端拿着userID,token等信息连接游戏服务器，游戏服需要去u8server
 * 验证token，验证成功，则登录合法，否则登录失败
 *
 * Created by ant on 2015/4/17.
 */
@Controller
@Namespace("/user")
public class UserPayAction extends UActionSupport{

//	private static final String GETORDER_URL = "http://192.168.16.3:8080/YTServer/pay/getOrderID";
//	private static final String GETORDER_URL = "http://14.152.59.179:8082/APYXServer/pay/getOrderID";//线上backup版本
  private static final String GETORDER_URL = "http://14.152.59.179:8080/APYXServer/pay/getOrderID";//线上版本接口

//    private int roleID;
//    private String userID;
//    private String productName;
//    private String productDesc;
//    private String money;
//    private String roleName;
//    private String serverID;
//    private String serverName;
//    private String extension;
//    private String sign;

    
    
    private String userID;
    private String productID;  //当前商品ID
    private String productName;
    private String productDesc;
    private String money;          //单位 分
    private String roleID;      //玩家在游戏服中的角色ID
    private String roleName;    //玩家在游戏服中的角色名称
    private String serverID;    //玩家所在的服务器ID
    private String serverName;  //玩家所在的服务器名称
    private String extension;

    private String sign;        //RSA签名

    @Action("payServer")
    public void payRequest(){

        try{

            Log.d("The roleID is "+roleID);

            Map<String, String> params = new HashMap<String, String>();
            params.put("roleID", "" + roleID);
            params.put("productID", productID);
            params.put("userID", userID);//登录认证成功之后，u8server返回的userID
            params.put("productName", productName);//商品名称
            params.put("productDesc", productDesc);//商品描述 
            params.put("money", money);//商品价格，单位分 
            params.put("roleName", roleName);//角色名称 
            params.put("serverID", serverID);//服务器ID 
            params.put("serverName", serverName);//服务器名称 
            params.put("extension", extension);//服务器名称
            params.put("sign", sign);//rsa签名，用于u8server服务器验证
            

            UHttpAgent.newInstance().post(GETORDER_URL, params, new UHttpFutureCallback() {
            	/**
            	 * 如果成功 返回值是 {state:1,data:{orderID:"4432423423",extension:"orderID是"4432423423""}}
            	 * 
            	 * 如果返回失败状态值不为1 ，具体值参考文档说明
            	 */
                @Override
                public void completed(String content) {
                    Log.d("The payServer getOrderID result is " + content);
                    
//                    PayResult result = (PayResult) JsonUtils.decodeJson(content, PayResult.class);
                   
                     JSONObject jsonObj =JSONObject.fromObject(content);
        			int state = jsonObj.getInt("state");
                    if (state==1) {//状态 1 说明成功
                    
                        renderState(content);
                    } else {
                        renderState(content);
                    }
                }

                @Override
                public void failed(String e) {
                    Log.e(e);
                    renderState(e);
                }

            });

        }catch(Exception e){
            e.printStackTrace();
        }

    }

    private void renderState(String content){
        try{
            super.renderJson(content);

        }catch(Exception e){
            e.printStackTrace();
            Log.e(e.getMessage());
        }


    }

	

	public String getProductID() {
		return productID;
	}

	public void setProductID(String productID) {
		this.productID = productID;
	}

	public String getRoleID() {
		return roleID;
	}

	public void setRoleID(String roleID) {
		this.roleID = roleID;
	}

	public String getUserID() {
		return userID;
	}

	public void setUserID(String userID) {
		this.userID = userID;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public String getProductDesc() {
		return productDesc;
	}

	public void setProductDesc(String productDesc) {
		this.productDesc = productDesc;
	}

	public String getMoney() {
		return money;
	}

	public void setMoney(String money) {
		this.money = money;
	}

	public String getRoleName() {
		return roleName;
	}

	public void setRoleName(String roleName) {
		this.roleName = roleName;
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

	public String getExtension() {
		return extension;
	}

	public void setExtension(String extension) {
		this.extension = extension;
	}

	public String getSign() {
		return sign;
	}

	public void setSign(String sign) {
		this.sign = sign;
	}

    static class PayResult{
    	
		private String state;
        private JSONObject data;
		public String getState() {
			return state;
		}
		public void setState(String state) {
			this.state = state;
		}
		public JSONObject getData() {
			return data;
		}
		public void setData(JSONObject data) {
			this.data = data;
		}
		

      
    }

}
