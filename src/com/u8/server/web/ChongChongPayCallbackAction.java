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

/**
 * 雁门SDK充值回调接口
 * Created by ant on 2015/2/28.
 */

@Controller
@Namespace("/pay/chongchong")
public class ChongChongPayCallbackAction extends UActionSupport{

	//	http://14.23.156.2:8089/YTServer/pay/chongchong/payCallback
	private String transactionNo;//虫虫订单号
	private String partnerTransactionNo;//商户订单号
	private String statusCode;//订单状态
	private String productId;//服务器ID
	private String orderPrice;//订单金额
	private String packageId;//游戏Id
	private String sign;//签名

	@Autowired
	private UOrderManager orderManager;

	@Action("payCallback")
	public void payCallback(){

		try{

			long orderID = Long.parseLong(partnerTransactionNo);
			UOrder uorder = orderManager.getOrder(orderID);

			int resultCode = 1;
			String resultMsg = "成功";

			if(uorder == null || uorder.getChannel() == null){
				Log.d("The order is null or the channel is null.");
				return;
			}

			if(uorder.getState() == PayState.STATE_COMPLETE){
				Log.d("The state of the order is complete. The state is "+uorder.getState());
//				this.renderState(uorder.getChannel(), resultCode);
				return;
			}

			UChannel uChannel=uorder.getChannel();

			StringBuilder strSign = new StringBuilder();
			if(null!=orderPrice&&orderPrice!=""){
				strSign.append("orderPrice").append("=").append(orderPrice).append("&");
			}
			
			if(null!=packageId&&packageId!=""){
				strSign.append("packageId").append("=").append(packageId).append("&");
			}
			if(null!=partnerTransactionNo&&partnerTransactionNo!=""){
				strSign.append("partnerTransactionNo").append("=").append(partnerTransactionNo).append("&");
			}
			if(null!=productId&&productId!=""){
				strSign.append("productId").append("=").append(productId).append("&");
			}
			if(null!=statusCode&&statusCode!=""){
				strSign.append("statusCode").append("=").append(statusCode).append("&");
			}
			if(null!=transactionNo&&transactionNo!=""){
				strSign.append("transactionNo").append("=").append(transactionNo).append("&");
			}
			String appSecret=uChannel.getCpAppSecret();
			strSign.append(appSecret);
			
			String newsign=EncryptUtils.md5(strSign.toString());
			//签名验证
			if(!newsign.equals(sign)){
				resultCode= 3; //sign无效
				resultMsg="Sign无效";
			}



			if(resultCode == 1){
				uorder.setRealMoney((int)(Double.parseDouble(orderPrice) * 100));
				uorder.setSdkOrderTime(String.valueOf(new Date().getTime()));
				uorder.setCompleteTime(new Date());
				uorder.setChannelOrderID(partnerTransactionNo);
				uorder.setState(PayState.STATE_SUC);

				orderManager.saveOrder(uorder);

				SendAgent.sendCallbackToServer(this.orderManager, uorder);

			}else{
				uorder.setChannelOrderID(partnerTransactionNo);
				uorder.setState(PayState.STATE_FAILED);
				orderManager.saveOrder(uorder);
			}

//			renderState(uorder.getChannel(), resultCode);

		}catch (Exception e){
			e.printStackTrace();
		}

	}

	private void renderState(UChannel channel, int resultCode) throws IOException {

		PrintWriter out = this.response.getWriter();

		if(resultCode==1){
			out.write("success");
		}else{
			out.write("errorSign");
		}
		out.flush();

	}




	public String getTransactionNo() {
		return transactionNo;
	}

	public void setTransactionNo(String transactionNo) {
		this.transactionNo = transactionNo;
	}

	public String getPartnerTransactionNo() {
		return partnerTransactionNo;
	}

	public void setPartnerTransactionNo(String partnerTransactionNo) {
		this.partnerTransactionNo = partnerTransactionNo;
	}

	public String getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(String statusCode) {
		this.statusCode = statusCode;
	}

	public String getProductId() {
		return productId;
	}

	public void setProductId(String productId) {
		this.productId = productId;
	}

	public String getOrderPrice() {
		return orderPrice;
	}

	public void setOrderPrice(String orderPrice) {
		this.orderPrice = orderPrice;
	}

	public String getPackageId() {
		return packageId;
	}

	public void setPackageId(String packageId) {
		this.packageId = packageId;
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
