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
import java.text.DecimalFormat;
import java.util.Date;

/**
 * 爱拍SDK充值回调接口
 * Created by ant on 2015/2/28.
 */

@Controller
@Namespace("/pay/heiyazi")
public class HeiYaZiCallbackAction extends UActionSupport{

	//	http://14.23.156.2:8089/YTServer/pay/heiyazi/payCallback
	private String Orderid;//Bd订单id
	private int user;//BD用户id
	private int server;//游戏服务器id
	private String Role;//游戏角色id
	private int Paytype;//支付方式
	private long Paytime;//支付时间
	private String Cporder;//Cp订单号
	private Float Amount;//充值金额
	private long Time;//请求时间
	private String sign;//sign
	@Autowired
	private UOrderManager orderManager;

	@Action("payCallback")
	public void payCallback(){

		try{

			long orderID = Long.parseLong(Cporder);
			UOrder uorder = orderManager.getOrder(orderID);

			Log.d("The uorder==="+uorder.toString());

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
			
			DecimalFormat fnum=new DecimalFormat("##0.00");    

			String  dd=fnum.format(Amount);  

			String gameid=uorder.getChannel().getCpAppID();
			String appkey=uorder.getChannel().getCpAppKey();
			
			StringBuilder strSign = new StringBuilder();

			strSign.append(gameid)
			.append("amount").append("=").append(dd).append("&")
			.append("cporder").append("=").append(Cporder).append("&")
			.append("orderid").append("=").append(Orderid).append("&")
			.append("paytime").append("=").append(Paytime).append("&")
			.append("paytype").append("=").append(Paytype).append("&")
			.append("role").append("=").append(Role).append("&")
			.append("server").append("=").append(server).append("&")
			.append("time").append("=").append(Time).append("&")
			.append("user").append("=").append(user)
			.append(appkey);

			String encoded = URLEncoder.encode(strSign.toString(), "UTF-8");


			Log.e("The CpAppSecret is "+  uorder.getChannel().getCpAppSecret());
			Log.e("The new sign is "+strSign.toString());
			String newsign=EncryptUtils.md5(strSign.toString());
			Log.e("The newsign1 is "+newsign);
			//签名验证
			if(!newsign.equals(sign)){
				resultCode= 3; //sign无效
				resultMsg="Sign无效";
			}

			if(resultCode == 1){
				uorder.setRealMoney((int)(Amount * 100));
				uorder.setSdkOrderTime(String.valueOf(new Date().getTime()));
				uorder.setCompleteTime(new Date());
				uorder.setChannelOrderID(Cporder);
				uorder.setState(PayState.STATE_SUC);

				orderManager.saveOrder(uorder);

				SendAgent.sendCallbackToServer(this.orderManager, uorder);

			}else{
				uorder.setChannelOrderID(Cporder);
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
			out.write("fail");
		}
		
		out.flush();

	}

	
	public String getSign() {
		return sign;
	}

	public void setSign(String sign) {
		this.sign = sign;
	}


	public String getOrderid() {
		return Orderid;
	}

	public void setOrderid(String orderid) {
		Orderid = orderid;
	}

	public int getUser() {
		return user;
	}

	public void setUser(int user) {
		this.user = user;
	}

	public int getServer() {
		return server;
	}

	public void setServer(int server) {
		this.server = server;
	}

	public String getRole() {
		return Role;
	}

	public void setRole(String role) {
		Role = role;
	}

	public int getPaytype() {
		return Paytype;
	}

	public void setPaytype(int paytype) {
		Paytype = paytype;
	}

	public long getPaytime() {
		return Paytime;
	}

	public void setPaytime(long paytime) {
		Paytime = paytime;
	}

	public String getCporder() {
		return Cporder;
	}

	public void setCporder(String cporder) {
		Cporder = cporder;
	}

	public Float getAmount() {
		return Amount;
	}

	public void setAmount(Float amount) {
		Amount = amount;
	}

	public long getTime() {
		return Time;
	}

	public void setTime(long time) {
		Time = time;
	}

	public UOrderManager getOrderManager() {
		return orderManager;
	}

	public void setOrderManager(UOrderManager orderManager) {
		this.orderManager = orderManager;
	}

}
