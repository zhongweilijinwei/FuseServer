package com.u8.server.web;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.URLDecoder;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import net.sf.json.JSONObject;

import org.apache.http.util.TextUtils;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.u8.server.common.UActionSupport;
import com.u8.server.constants.PayState;
import com.u8.server.data.UChannel;
import com.u8.server.data.UOrder;
import com.u8.server.log.Log;
import com.u8.server.sdk.tt.SignUtils;
import com.u8.server.service.UOrderManager;

/**
 * TTSDK充值回调接口
 * Created by ant on 2015/2/28.
 */

@Controller
@Namespace("/pay/tt")
public class TTPayCallbackAction extends UActionSupport{


	private String cpOrderId;
	private String payFee;
	private String ttsign;
	@Autowired
	private UOrderManager orderManager;

	
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
	
	@Action("payCallback")
	public void payCallback(){

		try{
			
			// 获取签名
			ttsign = request.getHeader("sign");
			
			String data=readFromNetStream(request.getInputStream());
			if(TextUtils.isBlank(data)){
				this.renderState(null, 3);
				return;
			}
//			// 获取请求报文
//			BufferedReader in = null;
//			
//			in = new BufferedReader(new InputStreamReader(request.getInputStream(), "utf-8"));
//			String ln;
//			StringBuilder stringBuilder = new StringBuilder();
//			while ((ln = in.readLine()) != null) {
//				stringBuilder.append(ln);
//				stringBuilder.append("\r\n");
//			}
			//    		{"cpOrderId":"1023105284861591553","exInfo":"1023105284861591553","gameId":207708150,"payDate":"2016-08-25 12:51:25","payFee":0.01,"payResult":"1","sdkOrderId":"201608251251241533705842867314","uid":15337058}
			// 将报文进行urldecode
			String urldata = URLDecoder.decode(data, "utf-8");
			JSONObject rejson=JSONObject.fromObject(urldata);
			cpOrderId=rejson.optString("cpOrderId");
			payFee=rejson.optString("payFee");


			long orderID = Long.parseLong(cpOrderId);
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


			String appsecret=uorder.getChannel().getCpAppSecret();

			String newsign = SignUtils.sign(urldata, appsecret);
			//签名验证
			if(!newsign.equals(ttsign)){
				resultCode= 3; //sign无效
				resultMsg="Sign无效";
			}

			if(resultCode == 1){
				uorder.setRealMoney((int)(Double.parseDouble(payFee) * 100));
				uorder.setSdkOrderTime(String.valueOf(new Date().getTime()));
				uorder.setCompleteTime(new Date());
				uorder.setChannelOrderID(cpOrderId);
				uorder.setState(PayState.STATE_SUC);

				orderManager.saveOrder(uorder);

//				SendAgent.sendCallbackToServer(this.orderManager, uorder);

			}else{
				uorder.setChannelOrderID(cpOrderId);
				uorder.setState(PayState.STATE_FAILED);
				orderManager.saveOrder(uorder);
			}

			renderState(uorder.getChannel(), resultCode);

		}catch (Exception e){
			e.printStackTrace();
		}

	}

	private void renderState(UChannel channel, int resultCode) throws IOException {
		String httpStr = null;
		JSONObject head = new JSONObject();
		JSONObject map = new JSONObject();
		
		PrintWriter out = this.response.getWriter();
		if(resultCode==1){
			map.put("result", "0");
			map.put("message", "验签成功");
			head.put("head", map);
			httpStr = head.toString();
		}else{
			map.put("result", "-1");
			map.put("message", "验签失败");
			head.put("head", map);
			httpStr = head.toString();
		}
//		out.println(httpStr);
		
		
		out.write(httpStr);
		out.flush();

	}


	public UOrderManager getOrderManager() {
		return orderManager;
	}

	public void setOrderManager(UOrderManager orderManager) {
		this.orderManager = orderManager;
	}

}
