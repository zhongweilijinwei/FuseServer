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
 * 爱拍SDK充值回调接口
 * Created by ant on 2015/2/28.
 */

@Controller
@Namespace("/pay/aipai")
public class AiPaiPayCallbackAction extends UActionSupport{

    private String appId;//最会玩游戏ID
    private String id;//最会玩支会订单ID（2015.06.11新加）
    private String product;//商品ID
    private String productName;//商品名称
    private String price;//商品总价格
    private String final_price;//价格x数量X折扣后的总价，单位：分
    private String num;//购买数量
    private String server;//游戏服务器ID
    private String serverName;//游戏服务器名称
    private String ip;//用户ip
    private String order;//如果是先在游戏方下单，这个就是游戏方订单ID，否则为空
    private String ext;//在游戏方下单时，游戏方提供的扩展字段
    private String sign;//签名

    @Autowired
    private UOrderManager orderManager;

    @Action("payCallback")
    public void payCallback(){

        try{

            long orderID = Long.parseLong(order);
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
            
//            appId  ext final_price id ip num order price product productName server serverName

            
                StringBuilder strSign = new StringBuilder();
         strSign.append("appId").append("=").append(appId)
                .append("ext").append("=").append(ext)
                .append("final_price").append("=").append(final_price)
                .append("id").append("=").append(id)
                .append("ip").append("=").append(ip)
                .append("num").append("=").append(num)
                .append("order").append("=").append(order)
                .append("price").append("=").append(price)
                .append("product").append("=").append(product)
                .append("productName").append("=").append(productName)
                .append("server").append("=").append(server)
                .append("serverName").append("=").append(serverName)
                .append(uorder.getChannel().getCpAppSecret());
       
         
         
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
                    uorder.setRealMoney((int)(Integer.decode(price) * 100));
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

        JSONObject json = new JSONObject();
        
        if(resultCode==1){
        json.put("code",1);
        }else{
        	json.put("code",-1);
        }
        Log.d("The result to sdk is "+json.toString());

        PrintWriter out = this.response.getWriter();
        out.write(json.toString());
        out.flush();

    }

	public String getAppId() {
		return appId;
	}

	public void setAppId(String appId) {
		this.appId = appId;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getProduct() {
		return product;
	}

	public void setProduct(String product) {
		this.product = product;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public String getPrice() {
		return price;
	}

	public void setPrice(String price) {
		this.price = price;
	}

	public String getFinal_price() {
		return final_price;
	}

	public void setFinal_price(String final_price) {
		this.final_price = final_price;
	}

	public String getNum() {
		return num;
	}

	public void setNum(String num) {
		this.num = num;
	}

	public String getServer() {
		return server;
	}

	public void setServer(String server) {
		this.server = server;
	}

	public String getServerName() {
		return serverName;
	}

	public void setServerName(String serverName) {
		this.serverName = serverName;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public String getOrder() {
		return order;
	}

	public void setOrder(String order) {
		this.order = order;
	}

	public String getExt() {
		return ext;
	}

	public void setExt(String ext) {
		this.ext = ext;
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
