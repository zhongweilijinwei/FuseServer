package com.u8.server.web;

import com.u8.server.common.UActionSupport;
import com.u8.server.constants.PayState;
import com.u8.server.data.UChannel;
import com.u8.server.data.UOrder;
import com.u8.server.log.Log;
import com.u8.server.sdk.baidu.BaiduPayResult;
import com.u8.server.sdk.htc_jvle.RsaSign;
import com.u8.server.service.UOrderManager;
import com.u8.server.utils.Base64;
import com.u8.server.utils.EncryptUtils;
import com.u8.server.utils.JsonUtils;
import com.u8.server.utils.TimeFormater;
import net.sf.json.JSONObject;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
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
import java.util.HashMap;
import java.util.Map;

/**
 * meituSDK充值回调接口
 * Created by ant on 2015/2/28.
 */

@Controller
@Namespace("/pay/htc")
public class HtcPayCallbackAction extends UActionSupport{
	//	http://14.23.156.2:8089/YTServer/pay/htc/payCallback
	
	@Autowired
	private UOrderManager orderManager;

	@Action("payCallback")
	public void payCallback(){

		try{

			String payContent = new String(IOUtils.toByteArray(request
					.getInputStream()), "utf-8");
			
			Map<String, String> paramters = changeToParamters(payContent);
			
			String signType = paramters.get("sign_type");//签名类型，一般是RSA
			String sign = java.net.URLDecoder.decode(paramters.get("sign"),"utf-8");// 签名
			String order = paramters.get("order");
		
			// 订单真实数据
			String orderDecoderToJson = java.net.URLDecoder.decode(order, "utf-8");// urlDecoder
			//json解析，仅供参考
			JSONObject jsonObject = JSONObject.fromObject(orderDecoderToJson);   
			int resultCode=jsonObject.getInt("result_code");//1成功 0失败
			String resultMsg=(String)jsonObject.get("result_msg");//支付信息
			String gameCode=(String)jsonObject.get("game_code");//游戏编号
			int realAmount=(int)jsonObject.getInt("real_amount");//付款成功金额，单位人民币分
			String cpOrderId=(String)jsonObject.get("game_order_id");//cp自身的订单号
			String joloOrderId=(String)jsonObject.get("jolo_order_id");//jolo订单
			String createTime=(String)jsonObject.get("gmt_create");//创建时间 订单创建时间 yyyy-MM-dd  HH:mm:ss
			String payTime=(String)jsonObject.get("gmt_payment");//支付时间 订单支付时间  yyyy-MM-dd  HH:mm:ss
			
			
			
			
			long orderID = Long.parseLong(cpOrderId);
			UOrder uorder = orderManager.getOrder(orderID);
			
			String publickey=uorder.getChannel().getCpPayKey();//做pay_key

			int aipairesultCode = 1;
			String aipairesultMsg = "成功";

			if(uorder == null || uorder.getChannel() == null){
				Log.d("The order is null or the channel is null.");
				return;
			}

			if(uorder.getState() == PayState.STATE_COMPLETE){
				Log.d("The state of the order is complete. The state is "+uorder.getState());
				this.renderState(uorder.getChannel(), aipairesultCode);
				return;
			}

			// step1 先校验订单
			boolean isOk = RsaSign.doCheck(orderDecoderToJson, sign, publickey);
			
			//签名验证
			if(!isOk){
				aipairesultCode= 3; //sign无效
				aipairesultMsg="Sign无效";
			}
			

			if(aipairesultCode == 1){
				
				uorder.setRealMoney(realAmount);
				uorder.setSdkOrderTime(String.valueOf(new Date().getTime()));
				uorder.setCompleteTime(new Date());
				uorder.setChannelOrderID(cpOrderId);
				uorder.setState(PayState.STATE_SUC);

				orderManager.saveOrder(uorder);

				SendAgent.sendCallbackToServer(this.orderManager, uorder);

			}else{
				uorder.setChannelOrderID(cpOrderId);
				uorder.setState(PayState.STATE_FAILED);
				orderManager.saveOrder(uorder);
			}

			renderState(uorder.getChannel(), aipairesultCode);

		}catch (Exception e){
			e.printStackTrace();
		}

	}

	private void renderState(UChannel channel, int aipairesultCode) throws IOException {
		
		PrintWriter out = this.response.getWriter();
		if(aipairesultCode==1){
			out.write("success");
		}else{
			out.write("success");
		}
		out.flush();

	}

	
	private Map<String, String> changeToParamters(String payContent) {
		Map<String, String> map = new HashMap<String, String>();
		if (StringUtils.isNotBlank(payContent)) {
			String[] paramertes = payContent.split("&");
			for (String parameter : paramertes) {
				String[] p = parameter.split("=");
				map.put(p[0], p[1].replaceAll("\"", ""));
			}
		}
		return map;
	}


	public UOrderManager getOrderManager() {
		return orderManager;
	}

	public void setOrderManager(UOrderManager orderManager) {
		this.orderManager = orderManager;
	}

}
