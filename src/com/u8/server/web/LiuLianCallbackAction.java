package com.u8.server.web;

import com.u8.server.common.UActionSupport;
import com.u8.server.constants.PayState;
import com.u8.server.data.UChannel;
import com.u8.server.data.UOrder;
import com.u8.server.log.Log;
import com.u8.server.sdk.baidu.BaiduPayResult;
import com.u8.server.service.UOrderManager;
import com.u8.server.utils.Base64;
import com.u8.server.utils.EncryptUtils;
import com.u8.server.utils.JsonUtils;
import com.u8.server.utils.TimeFormater;
import net.sf.json.JSONObject;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;

/**
 * 榴莲SDK充值回调接口
 * Created by ant on 2015/2/28.
 */

@Controller
@Namespace("/pay/liulian")
public class LiuLianCallbackAction extends UActionSupport{
	//	http://14.23.156.2:8089/YTServer/pay/liulian/payCallback
	private String appid;//最会玩游戏ID
	private String orderId;//最会玩支会订单ID（2015.06.11新加）
	private String userId;//商品ID
	private String serverId;//游戏服务器ID
	private String roleId;//游戏服务器名称
	private String roleName;//商品名称
	private String money;//商品总价格
	private String extInfo;//在游戏方下单时，游戏方提供的扩展字段
	private String status;//充值状态
	private String sign;//签名

	@Autowired
	private UOrderManager orderManager;

	@Action("payCallback")
	public void payCallback(){

		try{

			long orderID = Long.parseLong(extInfo);
			UOrder uorder = orderManager.getOrder(orderID);
			UChannel uChannel=uorder.getChannel();

			int resultCode = 1;
			String resultMsg = "成功";

			if(uorder == null || uorder.getChannel() == null){
				Log.d("The order is null or the channel is null.");
				return;
			}

			if(uorder.getState() == PayState.STATE_COMPLETE){
				Log.d("The state of the order is complete. The state is "+uorder.getState());
				this.renderState(uorder.getChannel(), resultCode);
				return;
			}

			//            appId  ext final_price id ip num order price product productName server serverName


			StringBuilder strSign = new StringBuilder();
			String appSecret= uChannel.getCpAppSecret();


			strSign.append(appid).append(appSecret).append(orderId)
			.append(userId).append(serverId).append(roleId)
			.append(roleName).append(money).append(extInfo)
			.append(status);





			Log.e("The CpAppSecret is "+  uorder.getChannel().getCpAppSecret());
			Log.e("The new sign is "+strSign.toString());
			String newsign=EncryptUtils.md5(strSign.toString()).toLowerCase();
			Log.e("The newsign1 is "+newsign);
			//签名验证
			if(!newsign.equals(sign)){
				resultCode= 3; //sign无效
				resultMsg="Sign无效";
			}

			if(resultCode == 1){
				uorder.setRealMoney((int)(Double.parseDouble(money) * 100));
				uorder.setSdkOrderTime(String.valueOf(new Date().getTime()));
				uorder.setCompleteTime(new Date());
				uorder.setChannelOrderID(extInfo);
				uorder.setState(PayState.STATE_SUC);

				orderManager.saveOrder(uorder);

				SendAgent.sendCallbackToServer(this.orderManager, uorder);

			}else{
				uorder.setChannelOrderID(extInfo);
				uorder.setState(PayState.STATE_FAILED);
				orderManager.saveOrder(uorder);
			}

			renderState(uorder.getChannel(), resultCode);

		}catch (Exception e){
			e.printStackTrace();
		}

	}

	private void renderState(UChannel channel, int resultCode) throws IOException {

		PrintWriter out = this.response.getWriter();
		if(resultCode==1){
			out.write("SUCCESS");
		}else{
			out.write("FAILURE");
		}
		out.flush();

	}


	public String getAppid() {
		return appid;
	}

	public void setAppid(String appid) {
		this.appid = appid;
	}

	public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getServerId() {
		return serverId;
	}

	public void setServerId(String serverId) {
		this.serverId = serverId;
	}

	public String getRoleId() {
		return roleId;
	}

	public void setRoleId(String roleId) {
		this.roleId = roleId;
	}

	public String getRoleName() {
		return roleName;
	}

	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}

	public String getMoney() {
		return money;
	}

	public void setMoney(String money) {
		this.money = money;
	}

	public String getExtInfo() {
		return extInfo;
	}

	public void setExtInfo(String extInfo) {
		this.extInfo = extInfo;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getSign() {
		return sign;
	}

	public void setSign(String sign) {
		this.sign = sign;
	}

	public UOrderManager getOrderManager() {
		return orderManager;
	}

	public void setOrderManager(UOrderManager orderManager) {
		this.orderManager = orderManager;
	}

}
