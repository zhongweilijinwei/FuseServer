package com.u8.server.web;

import com.u8.server.common.UActionSupport;
import com.u8.server.constants.PayState;
import com.u8.server.data.UChannel;
import com.u8.server.data.UOrder;
import com.u8.server.log.Log;
import com.u8.server.sdk.baidu.BaiduPayResult;
import com.u8.server.sdk.chubaodianhua.Base64;
import com.u8.server.sdk.chubaodianhua.Constants;
import com.u8.server.sdk.chubaodianhua.StreamUtils;
import com.u8.server.sdk.chubaodianhua.StringUtils;
import com.u8.server.service.UOrderManager;
import com.u8.server.utils.EncryptUtils;
import com.u8.server.utils.JsonUtils;
import com.u8.server.utils.RSAUtils;
import com.u8.server.utils.TimeFormater;
import net.sf.json.JSONObject;
import org.apache.http.util.TextUtils;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.security.KeyFactory;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * 爱拍SDK充值回调接口
 * Created by ant on 2015/2/28.
 */

@Controller
@Namespace("/pay/pengyouwan")
public class PengYouWanCallbackAction extends UActionSupport{
	//	http://14.23.156.2:8089/YTServer/pay/pengyouwan/payCallback

	@Autowired
	private UOrderManager orderManager;
//	private String ver;
//	private String tid;
//	private String gamekey;
//	private String channel;
//	private String cp_param;
//
//	private String cp_oderid;
//	private String ch_oderid;
//	private float amount;
//	private String sign;

	@Action("payCallback")
	public void payCallback(){

		try{
			String jsonstring = null;
			Object[] requests=request.getParameterMap().keySet().toArray();
			if(requests.length>0){
				jsonstring=(String) requests[0];
			}
			
			 
			JSONObject jr = JSONObject.fromObject(jsonstring);
			
			HashMap<String, Object> params=toHashMap(jr);

			String cp_oderid=(String) params.get("cp_orderid");
			String ch_oderid=(String) params.get("ch_orderid");
			String amount=(String) params.get("amount");
			String sign=(String) params.get("sign");

			long orderID = Long.parseLong(cp_oderid);
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


			String AppSecret=uorder.getChannel().getCpAppSecret();

			StringBuilder strSign = new StringBuilder();
			strSign.append(AppSecret).append(cp_oderid).append(ch_oderid).append(amount);

			String newsign=EncryptUtils.md5(strSign.toString());

			//签名验证
			if(!newsign.equals(sign)){
				resultCode= 3; //sign无效
				resultMsg="Sign无效";
			}

			if(resultCode == 1){
				BigDecimal b=new BigDecimal(amount);  
				float  f1=b.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
				
				uorder.setRealMoney((int)(f1*100));
				uorder.setSdkOrderTime(String.valueOf(new Date().getTime()));
				uorder.setCompleteTime(new Date());
				uorder.setChannelOrderID(cp_oderid);
				uorder.setState(PayState.STATE_SUC);

				orderManager.saveOrder(uorder);

				SendAgent.sendCallbackToServer(this.orderManager, uorder);

			}else{
				uorder.setChannelOrderID(cp_oderid);
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
			json.put("ack",200);
			json.put("msg","ok");
		}else{
			json.put("ack",1);
			json.put("msg","fail");
		}

		PrintWriter out = this.response.getWriter();
		out.write(json.toString());
		out.flush();

	}


	public UOrderManager getOrderManager() {
		return orderManager;
	}

	public void setOrderManager(UOrderManager orderManager) {
		this.orderManager = orderManager;
	}



	public String readFromNetStream(InputStream request) {
		try {
			byte[] buffer = new byte[1024];
			int num = 0;
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			while (-1 != (num = request.read(buffer))) {
				bos.write(buffer, 0, num);
			}
			return bos.toString("utf-8");
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	/** 
	 * 将json格式的字符串解析成Map对象 <li> 
	 * json格式：{"name":"admin","retries":"3fff","testname" 
	 * :"ddd","testretries":"fffffffff"} 
	 */  
	private static HashMap<String, Object> toHashMap(JSONObject js)  
	{  
		HashMap<String, Object> data = new HashMap<String, Object>();  
		// 将json字符串转换成jsonObject  
		Iterator it = js.keys();  
		// 遍历jsonObject数据，添加到Map对象  
		while (it.hasNext())  
		{  
			String key = String.valueOf(it.next());  
			Object value = js.get(key);  
			data.put(key, value);  
		}  
		return data;  
	}

//	public String getCp_oderid() {
//		return cp_oderid;
//	}
//
//	public void setCp_oderid(String cp_oderid) {
//		this.cp_oderid = cp_oderid;
//	}
//
//	public String getCh_oderid() {
//		return ch_oderid;
//	}
//
//	public void setCh_oderid(String ch_oderid) {
//		this.ch_oderid = ch_oderid;
//	}
//
//	public float getAmount() {
//		return amount;
//	}
//
//	public void setAmount(float amount) {
//		this.amount = amount;
//	}
//
//	public String getSign() {
//		return sign;
//	}
//
//	public void setSign(String sign) {
//		this.sign = sign;
//	}
//
//	public String getVer() {
//		return ver;
//	}
//
//	public void setVer(String ver) {
//		this.ver = ver;
//	}
//
//	public String getTid() {
//		return tid;
//	}
//
//	public void setTid(String tid) {
//		this.tid = tid;
//	}
//
//	public String getGamekey() {
//		return gamekey;
//	}
//
//	public void setGamekey(String gamekey) {
//		this.gamekey = gamekey;
//	}
//
//	public String getChannel() {
//		return channel;
//	}
//
//	public void setChannel(String channel) {
//		this.channel = channel;
//	}
//
//	public String getCp_param() {
//		return cp_param;
//	}
//
//	public void setCp_param(String cp_param) {
//		this.cp_param = cp_param;
//	}  

}
