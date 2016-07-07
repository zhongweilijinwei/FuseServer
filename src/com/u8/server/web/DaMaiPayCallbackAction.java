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
 * 大麦SDK充值回调接口
 * Created by ant on 2015/2/28.
 */

@Controller
@Namespace("/pay/damai")
public class DaMaiPayCallbackAction extends UActionSupport{

	//	http://14.23.156.2:8089/YTServer/pay/damai/payCallback
	private String username;//sdk登陆账号
	private int appid;//游戏id
	private String roleid;//游戏角色
	private int serverid;//服务器ID
	private String productname;//充值类型
	private String amount;//充值金额
	private int paytime;//充值时的时间戳
	private String orderid;//如果是先在游戏方下单，这个就是游戏方订单ID，否则为空
	private String attach;//在游戏方下单时，游戏方提供的扩展字段
	private String sign;//签名
	
	private String appkey;
	@Autowired
	private UOrderManager orderManager;

	@Action("payCallback")
	public void payCallback(){

		try{
			
			username=URLEncoder.encode(username, "UTF-8");
			roleid=URLEncoder.encode(roleid, "UTF-8");
			productname=URLEncoder.encode(productname, "UTF-8");
			amount=URLEncoder.encode(amount, "UTF-8");
			orderid=URLEncoder.encode(orderid, "UTF-8");
			attach=URLEncoder.encode(attach, "UTF-8");
			
			long orderID = Long.parseLong(attach);
			UOrder uorder = orderManager.getOrder(orderID);
			UChannel uChannel=uorder.getChannel();
			
			appkey=URLEncoder.encode(uChannel.getCpAppKey(), "UTF-8");
			
			
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

			

			StringBuilder strSign = new StringBuilder();
			strSign.append("orderid").append("=").append(orderid).append("&")
			.append("username").append("=").append(username).append("&")
			.append("appid").append("=").append(appid).append("&")
			.append("roleid").append("=").append(roleid).append("&")
			.append("serverid").append("=").append(serverid).append("&")
			.append("amount").append("=").append(amount).append("&")
			.append("paytime").append("=").append(paytime).append("&")
			.append("attach").append("=").append(attach).append("&")
			.append("productname").append("=").append(productname).append("&")
			.append("appkey").append("=").append(appkey);

			String newsign=EncryptUtils.md5(strSign.toString());
			//签名验证
			if(!newsign.equals(sign)){
				resultCode= 3; //sign无效
				resultMsg="Sign无效";
			}



			if(resultCode == 1){
				uorder.setRealMoney((int)(Double.parseDouble(amount) * 100));
				uorder.setSdkOrderTime(String.valueOf(new Date().getTime()));
				uorder.setCompleteTime(new Date());
				uorder.setChannelOrderID(attach);
				uorder.setState(PayState.STATE_SUC);

				orderManager.saveOrder(uorder);

				SendAgent.sendCallbackToServer(this.orderManager, uorder);

			}else{
				uorder.setChannelOrderID(attach);
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
			out.write("success");
		}else{
			out.write("errorSign");
		}
		out.flush();

	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public int getAppid() {
		return appid;
	}

	public void setAppid(int appid) {
		this.appid = appid;
	}

	public String getRoleid() {
		return roleid;
	}

	public void setRoleid(String roleid) {
		this.roleid = roleid;
	}

	public int getServerid() {
		return serverid;
	}

	public void setServerid(int serverid) {
		this.serverid = serverid;
	}

	public String getProductname() {
		return productname;
	}

	public void setProductname(String productname) {
		this.productname = productname;
	}

	public String getAmount() {
		return amount;
	}

	public void setAmount(String amount) {
		this.amount = amount;
	}

	public int getPaytime() {
		return paytime;
	}

	public void setPaytime(int paytime) {
		this.paytime = paytime;
	}

	public String getOrderid() {
		return orderid;
	}

	public void setOrderid(String orderid) {
		this.orderid = orderid;
	}

	public String getAttach() {
		return attach;
	}

	public void setAttach(String attach) {
		this.attach = attach;
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
