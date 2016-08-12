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
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;

/**
 * 笨手机SDK充值回调接口
 * Created by ant on 2015/2/28.
 */

@Controller
@Namespace("/pay/benshouji")
public class BenShouJiPayCallbackAction extends UActionSupport{
	//	http://14.23.156.2:8089/YTServer/pay/benshouji/payCallback
	private int code;//结果码
	private String orderseq;//SDK订单流水号
	private String orderId;//游戏方订单号
	private String totalAmount;//订单金额
	private String theAmount;//首充卡抵用金额
	private String PaymentAmount;//实际支付金额 
	private String message;//消息内容
	private String sign;//游戏服务器ID

	//    MD5签名大写，签名的字段为：
	//    appSecKey+code+orderseq+orderId+totalAmount+theAmount+PaymentAmount+message
	@Autowired
	private UOrderManager orderManager;

	@Action("payCallback")
	public void payCallback(){

		try{

			long orderID = Long.parseLong(orderId);
			UOrder uorder = orderManager.getOrder(orderID);
			String appsecret=uorder.getChannel().getCpAppSecret();

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
			strSign.append(appsecret).append(code).append(orderseq)
			.append(orderId).append(totalAmount).append(theAmount)
			.append(PaymentAmount).append(message);

			String newsign=EncryptUtils.md5(strSign.toString()).toUpperCase();
			Log.e("The newsign1 is "+newsign);
			//签名验证
			if(!newsign.equals(sign)){
				resultCode= 3; //sign无效
				resultMsg="Sign无效";
			}
			

			if(resultCode == 1){
				float picer=Float.parseFloat(totalAmount);
				
				uorder.setRealMoney((int)(picer * 100f));
				uorder.setSdkOrderTime(String.valueOf(new Date().getTime()));
				uorder.setCompleteTime(new Date());
				uorder.setChannelOrderID(orderId);
				uorder.setState(PayState.STATE_SUC);

				orderManager.saveOrder(uorder);

				SendAgent.sendCallbackToServer(this.orderManager, uorder);

			}else{
				uorder.setChannelOrderID(orderId);
				uorder.setState(PayState.STATE_FAILED);
				orderManager.saveOrder(uorder);
			}

			renderState(uorder.getChannel(), resultCode);

		}catch (Exception e){
			e.printStackTrace();
		}

	}

	private void renderState(UChannel channel, int resultCode) throws IOException {

		JSONObject json = new JSONObject();
		if(resultCode==1){
			json.put("code",0);
			json.put("succeed",true);
		}else{
			json.put("code",1);
			json.put("succeed",false);
		}

		PrintWriter out = this.response.getWriter();
		out.write(json.toString());
		out.flush();

	}


	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getOrderseq() {
		return orderseq;
	}

	public void setOrderseq(String orderseq) {
		this.orderseq = orderseq;
	}

	public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

	public String getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(String totalAmount) {
		this.totalAmount = totalAmount;
	}

	public String getTheAmount() {
		return theAmount;
	}

	public void setTheAmount(String theAmount) {
		this.theAmount = theAmount;
	}

	public String getPaymentAmount() {
		return PaymentAmount;
	}

	public void setPaymentAmount(String paymentAmount) {
		PaymentAmount = paymentAmount;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
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
