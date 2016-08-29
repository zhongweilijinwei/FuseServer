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
 * 征游SDK充值回调接口
 * Created by ant on 2015/2/28.
 */

@Controller
@Namespace("/pay/zhengyou")
public class ZhengYouPayCallbackAction extends UActionSupport{
//	http://14.23.156.2:8089/YTServer/pay/zhengyou/payCallback
    private String notifyid;//
    private String orderno;//sdk 订单id
    private String agentorderno;//游戏订单ID
    private String amount;//商品金额
    private String subject;//商品名称
    private String playcode;//会员账号
    private String sign;//签名
   
    @Autowired
    private UOrderManager orderManager;

    @Action("payCallback")
    public void payCallback(){

        try{

            long orderID = Long.parseLong(agentorderno);
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
        
                strSign.append(notifyid)
                .append(uorder.getChannel().getCpAppKey()) //自己生成的 下单时传入的sign
                .append(uorder.getChannel().getCpAppSecret());//sdk提供的加密密匙
       
         
         
            
                String newsign=EncryptUtils.md5(strSign.toString());
                Log.e("The newsign1 is "+newsign);
                //签名验证
                if(!newsign.equals(sign)){
                    resultCode= 3; //sign无效
                    resultMsg="Sign无效";
                }
            
            if(resultCode == 1){
                    uorder.setRealMoney((int)(Double.parseDouble(amount) * 100));
                    uorder.setSdkOrderTime(String.valueOf(new Date().getTime()));
                    uorder.setCompleteTime(new Date());
                    uorder.setChannelOrderID(agentorderno);
                    uorder.setState(PayState.STATE_SUC);
                    
                    orderManager.saveOrder(uorder);
                    
                    SendAgent.sendCallbackToServer(this.orderManager, uorder);

            }else{
                uorder.setChannelOrderID(agentorderno);
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


	public String getNotifyid() {
		return notifyid;
	}

	public void setNotifyid(String notifyid) {
		this.notifyid = notifyid;
	}

	public String getOrderno() {
		return orderno;
	}

	public void setOrderno(String orderno) {
		this.orderno = orderno;
	}

	public String getAgentorderno() {
		return agentorderno;
	}

	public void setAgentorderno(String agentorderno) {
		this.agentorderno = agentorderno;
	}

	public String getAmount() {
		return amount;
	}

	public void setAmount(String amount) {
		this.amount = amount;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getPlaycode() {
		return playcode;
	}

	public void setPlaycode(String playcode) {
		this.playcode = playcode;
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
