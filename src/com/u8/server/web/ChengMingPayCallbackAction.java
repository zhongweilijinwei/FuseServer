package com.u8.server.web;

import com.u8.server.common.UActionSupport;
import com.u8.server.constants.PayState;
import com.u8.server.data.UChannel;
import com.u8.server.data.UOrder;
import com.u8.server.log.Log;
import com.u8.server.sdk.baidu.BaiduPayResult;
import com.u8.server.sdk.chengming.SignHelper;
import com.u8.server.service.UOrderManager;
import com.u8.server.utils.Base64;
import com.u8.server.utils.EncryptUtils;
import com.u8.server.utils.JsonUtils;
import com.u8.server.utils.TimeFormater;
import net.sf.json.JSONObject;

import org.apache.http.util.TextUtils;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;

/**
 * 诚铭充值回调接口
 * Created by ant on 2015/2/28.
 */

@Controller
@Namespace("/pay/chengming")
public class ChengMingPayCallbackAction extends UActionSupport{

	//	http://14.23.156.2:8089/YTServer/pay/chengming/payCallback

	@Autowired
	private UOrderManager orderManager;

	@Action("payCallback")
	public void payCallback(){

		try{


			String transdata = request.getParameter("transdata"); 
			String sign = (String) request.getParameter("sign"); 
			String signtype = request.getParameter("signtype"); 

			JSONObject datajson=JSONObject.fromObject(transdata);
			String cporderid = datajson.getString("cporderid"); 
			String money=datajson.getString("money");

			if(TextUtils.isEmpty(transdata)){
				renderState(null,3);
				return;
			}



			long orderID = Long.parseLong(cporderid);
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

			UChannel uChannel=uorder.getChannel();
			String publickey=uChannel.getCpPayKey();
			boolean issign=SignHelper.verify(transdata, sign,publickey);
			//签名验证
			if(!issign){
				resultCode= 3; //sign无效
				resultMsg="Sign无效";
			}



			if(resultCode == 1){
				uorder.setRealMoney((int)(Double.parseDouble(money) * 100));
				uorder.setSdkOrderTime(String.valueOf(new Date().getTime()));

				uorder.setCompleteTime(new Date());
				uorder.setChannelOrderID(cporderid);
				uorder.setState(PayState.STATE_SUC);

				orderManager.saveOrder(uorder);

				SendAgent.sendCallbackToServer(this.orderManager, uorder);

			}else{
				uorder.setChannelOrderID(cporderid);
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


	public UOrderManager getOrderManager() {
		return orderManager;
	}

	public void setOrderManager(UOrderManager orderManager) {
		this.orderManager = orderManager;
	}


}
