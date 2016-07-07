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

import javax.servlet.http.HttpServletRequest;

/**
 * 爱拍SDK充值回调接口
 * Created by ant on 2015/2/28.
 */

@Controller
@Namespace("/pay/qkqk")
public class QkQkPayCallbackAction extends UActionSupport{

	//	uid: 用户身份统一标识识别码
	//	cporder: Cp方订单号
	//	money:	玩家充值金额，以元单位
	//	order:	sdk方的订单号
	//	cpappid:	cp应用ID
	//	sign:	    数据加密验证字符串
	//	sign构成：md5(uid + cporder + money + order + cpappid + app_secret);

	private String uid;//用户身份统一标识识别码
	private String cporder;//Cp方订单号
	private String money;//玩家充值金额，以元单位
	private String cpappid;//cp应用ID
	private String order;	//sdk方的订单号; 
	private String sign;//数据加密验证字符串wuq
	
	
	@Autowired
	private UOrderManager orderManager;

	@Action("payCallback")
	public void payCallback(){
		
		
		try{
			long orderID = Long.parseLong(cporder);
			UOrder uorder = orderManager.getOrder(orderID);

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

			//            (uid + cporder + money + order + cpappid + app_secret);
			StringBuilder strSign = new StringBuilder();
			strSign.append(uid).append(cporder).append(money).append(order).
			append(uorder.getChannel().getCpAppID()).append(uorder.getChannel().getCpAppSecret());

			String newsign=EncryptUtils.md5(strSign.toString());
			Log.e("The newsign1 is "+newsign);
			//签名验证
			if(!newsign.equals(sign)){
				resultCode= 3; //sign无效
				resultMsg="Sign无效";
			}

			if(resultCode == 1){
				uorder.setRealMoney((int)(Float.parseFloat(money) * 100));
				uorder.setSdkOrderTime(String.valueOf(new Date().getTime()));
				uorder.setCompleteTime(new Date());
				uorder.setChannelOrderID(order);
				uorder.setState(PayState.STATE_SUC);

				orderManager.saveOrder(uorder);

				SendAgent.sendCallbackToServer(this.orderManager, uorder);

			}else{
				uorder.setChannelOrderID(order);
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

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public String getCporder() {
		return cporder;
	}

	public void setCporder(String cporder) {
		this.cporder = cporder;
	}

	public String getMoney() {
		return money;
	}

	public void setMoney(String money) {
		this.money = money;
	}

	public String getCpappid() {
		return cpappid;
	}

	public void setCpappid(String cpappid) {
		this.cpappid = cpappid;
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

	public String getOrder() {
		return order;
	}

	public void setOrder(String order) {
		this.order = order;
	}

}
