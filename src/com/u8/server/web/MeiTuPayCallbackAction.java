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
import java.text.DecimalFormat;
import java.util.Date;

/**
 * meituSDK充值回调接口
 * Created by ant on 2015/2/28.
 */

@Controller
@Namespace("/pay/meitu")
public class MeiTuPayCallbackAction extends UActionSupport{
	//	http://14.23.156.2:8089/YTServer/pay/meitu/payCallback
	private int uid;//
	private float money;// 元
	private int time;//
	private int sid;//
	private String orderid;//
	private String ext;//
	private String flag;//

	
	@Autowired
	private UOrderManager orderManager;

	@Action("payCallback")
	public void payCallback(){

		try{

			long orderID = Long.parseLong(ext);
			UOrder uorder = orderManager.getOrder(orderID);
			String appsecret=uorder.getChannel().getCpAppSecret();//做pay_key

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
			strSign.append(uid).append((int)money).append(time)
			.append(sid).append(orderid).append(ext)
			.append(appsecret);

			String newsign=EncryptUtils.md5(strSign.toString());
			Log.e("The newsign1 is "+newsign);
			//签名验证
			if(!newsign.equals(flag)){
				resultCode= 3; //sign无效
				resultMsg="Sign无效";
			}
			

			if(resultCode == 1){
				
				uorder.setRealMoney((int)(money * 100f));
				uorder.setSdkOrderTime(String.valueOf(new Date().getTime()));
				uorder.setCompleteTime(new Date());
				uorder.setChannelOrderID(ext);
				uorder.setState(PayState.STATE_SUC);

				orderManager.saveOrder(uorder);

				SendAgent.sendCallbackToServer(this.orderManager, uorder);

			}else{
				uorder.setChannelOrderID(ext);
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


	public int getUid() {
		return uid;
	}

	public void setUid(int uid) {
		this.uid = uid;
	}

	public float getMoney() {
		return money;
	}

	public void setMoney(float money) {
		this.money = money;
	}

	public int getTime() {
		return time;
	}

	public void setTime(int time) {
		this.time = time;
	}

	public int getSid() {
		return sid;
	}

	public void setSid(int sid) {
		this.sid = sid;
	}

	public String getOrderid() {
		return orderid;
	}

	public void setOrderid(String orderid) {
		this.orderid = orderid;
	}

	public String getExt() {
		return ext;
	}

	public void setExt(String ext) {
		this.ext = ext;
	}


	public String getFlag() {
		return flag;
	}

	public void setFlag(String flag) {
		this.flag = flag;
	}

	public UOrderManager getOrderManager() {
		return orderManager;
	}

	public void setOrderManager(UOrderManager orderManager) {
		this.orderManager = orderManager;
	}

}
