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
 * 手游村SDK充值回调接口
 * Created by ant on 2015/2/28.
 */

@Controller
@Namespace("/pay/shouyoucun")
public class ShouYouCunPayCallbackAction extends UActionSupport{

	//	http://14.23.156.2:8089/YTServer/pay/shouyoucun/payCallback
	private String order_id;
	private String mem_id;//玩家ID
	private String app_id;//游戏ID
	private String money;//充值金额
	private String order_status;//如果是先在游戏方下单，这个就是游戏方订单ID，否则为空
	private String attach;//在游戏方下单时，游戏方提供的扩展字段
	private String paytime;//时间戳, Unix timestamp
    private String sign;//签名
	@Autowired
	private UOrderManager orderManager;

	@Action("payCallback")
	public void payCallback(){
		
		String requests = null;
		try {
			requests=readFromNetStream(request.getInputStream());
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		
		JSONObject jr = JSONObject.fromObject(requests);
		order_id=(String)jr.get("order_id");
		mem_id=(String)jr.get("mem_id");
		app_id=(String)jr.get("app_id");
		money=(String)jr.get("money");
		order_status=(String)jr.get("order_status");
		attach=(String)jr.get("attach");
		paytime=(String)jr.get("paytime");
		sign=(String) jr.get("sign");
		
		
		try{
			long orderID = Long.parseLong(attach);
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
//			md5(order_id=...&mem_id=...&app_id=...&money=...&order_status=...&paytime=...&attach=...&app_key=901f6984e638c2f96ef48675b6a32a73s)
			
			StringBuilder strSign = new StringBuilder();
			
			
			strSign.append("order_id").append("=").append(order_id).append("&")
			.append("mem_id").append("=").append(mem_id).append("&")
			.append("app_id").append("=").append(app_id).append("&")
			.append("money").append("=").append(money).append("&")
			.append("order_status").append("=").append(order_status).append("&")
			.append("paytime").append("=").append(paytime).append("&")
			.append("attach").append("=").append(attach).append("&")
			.append("app_key").append("=").append(uChannel.getCpAppKey());



			String newsign=EncryptUtils.md5(strSign.toString());
			//签名验证
			if(!newsign.equals(sign)){
				resultCode= 3; //sign无效
				resultMsg="Sign无效";
			}



			if(resultCode == 1){
				uorder.setRealMoney((int)(Double.parseDouble(money) * 100));
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
			out.write("SUCCESS");
		}else{
			out.write("FAILURE");
		}
		out.flush();

	}

	public String getOrder_id() {
		return order_id;
	}

	public void setOrder_id(String order_id) {
		this.order_id = order_id;
	}

	public String getMem_id() {
		return mem_id;
	}

	public void setMem_id(String mem_id) {
		this.mem_id = mem_id;
	}

	public String getApp_id() {
		return app_id;
	}

	public void setApp_id(String app_id) {
		this.app_id = app_id;
	}

	public String getMoney() {
		return money;
	}

	public void setMoney(String money) {
		this.money = money;
	}

	public String getOrder_status() {
		return order_status;
	}

	public void setOrder_status(String order_status) {
		this.order_status = order_status;
	}

	public String getAttach() {
		return attach;
	}

	public void setAttach(String attach) {
		this.attach = attach;
	}

	public String getPaytime() {
		return paytime;
	}

	public void setPaytime(String paytime) {
		this.paytime = paytime;
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
}
