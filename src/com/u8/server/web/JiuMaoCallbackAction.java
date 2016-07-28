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
 * 玖毛SDK充值回调接口
 * Created by ant on 2015/2/28.
 */

@Controller
@Namespace("/pay/jiumao")
public class JiuMaoCallbackAction extends UActionSupport{

	//	http://14.23.156.2:8089/YTServer/pay/jiumao/payCallback
	private String out_trade_no;//订单号
	private String price;//价格
	private String pay_status;//1; 0:失败，1:成功
	private String extend;//扩展参数
	private String signType;//签名类型 MD5, RSA
	private String sign;//sign
	@Autowired
	private UOrderManager orderManager;

	@Action("payCallback")
	public void payCallback(){

		try{

			long orderID = Long.parseLong(extend);
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
			
		

			String appkey=uorder.getChannel().getCpAppKey();
			
			StringBuilder strSign = new StringBuilder();

			strSign.append(out_trade_no).append(price).append(pay_status).append(extend)
			.append(appkey);
			

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
				uorder.setRealMoney((int)(Double.parseDouble(price)*100d));
				uorder.setSdkOrderTime(String.valueOf(new Date().getTime()));
				uorder.setCompleteTime(new Date());
				uorder.setChannelOrderID(extend);
				uorder.setState(PayState.STATE_SUC);

				orderManager.saveOrder(uorder);

				SendAgent.sendCallbackToServer(this.orderManager, uorder);

			}else{
				uorder.setChannelOrderID(extend);
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

	public String getOut_trade_no() {
		return out_trade_no;
	}


	public void setOut_trade_no(String out_trade_no) {
		this.out_trade_no = out_trade_no;
	}


	public String getPrice() {
		return price;
	}


	public void setPrice(String price) {
		this.price = price;
	}


	public String getPay_status() {
		return pay_status;
	}


	public void setPay_status(String pay_status) {
		this.pay_status = pay_status;
	}


	public String getExtend() {
		return extend;
	}


	public void setExtend(String extend) {
		this.extend = extend;
	}


	public String getSignType() {
		return signType;
	}


	public void setSignType(String signType) {
		this.signType = signType;
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
