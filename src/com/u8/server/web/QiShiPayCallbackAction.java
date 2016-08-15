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

import org.apache.http.util.TextUtils;
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
 * 骑士SDK充值回调接口
 * Created by ant on 2015/2/28.
 */

@Controller
@Namespace("/pay/qishi")
public class QiShiPayCallbackAction extends UActionSupport{
//	http://14.23.156.2:8089/YTServer/pay/qishi/payCallback
	

    private String paystatus;//充值状态:paystatus: 1支付成功
    private String paymoney;//充值金额:paymoney:3000[分]
    private String payorder;//SDK服务器订单号
    private String paygameorder;//游戏端订单号
    private String paygameid;//游戏ID
    private String playerid;//玩家id
    private String gamestring;//游戏服务器端自定义字符串,原
    private String appkey;//md5值[对接游戏的验签值]
    @Autowired
    private UOrderManager orderManager;

    @Action("payCallback")
    public void payCallback(){

        try{
        	if(TextUtils.isEmpty(paygameorder)){
        		renderState(null, 2);
        		return;
        	}

            long orderID = Long.parseLong(paygameorder);
            UOrder uorder = orderManager.getOrder(orderID);
            String cpappkey=uorder.getChannel().getCpAppKey();
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

           long lpayorder=Long.decode(payorder);
           long lpaymoney=Long.decode(paymoney);
           String newsign1=EncryptUtils.md5(String.valueOf(lpayorder-lpaymoney));
           String newsign2=EncryptUtils.md5(newsign1+cpappkey);
           
           
                //签名验证
                if(!newsign2.equals(appkey)){
                    resultCode= 3; //sign无效
                    resultMsg="Sign无效";
                }
            
            if(resultCode == 1){
                    uorder.setRealMoney(Integer.decode(paymoney));
                    uorder.setSdkOrderTime(String.valueOf(new Date().getTime()));
                    uorder.setCompleteTime(new Date());
                    uorder.setChannelOrderID(paygameorder);
                    uorder.setState(PayState.STATE_SUC);
                    
                    orderManager.saveOrder(uorder);
                    
                    SendAgent.sendCallbackToServer(this.orderManager, uorder);

            }else{
                uorder.setChannelOrderID(paygameorder);
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
        	 out.write("1");
        }else{
        	 out.write("2");
        }
       
        out.flush();

    }

	public String getPaystatus() {
		return paystatus;
	}

	public void setPaystatus(String paystatus) {
		this.paystatus = paystatus;
	}

	public String getPaymoney() {
		return paymoney;
	}

	public void setPaymoney(String paymoney) {
		this.paymoney = paymoney;
	}

	public String getPayorder() {
		return payorder;
	}

	public void setPayorder(String payorder) {
		this.payorder = payorder;
	}

	public String getPaygameorder() {
		return paygameorder;
	}

	public void setPaygameorder(String paygameorder) {
		this.paygameorder = paygameorder;
	}

	public String getPaygameid() {
		return paygameid;
	}

	public void setPaygameid(String paygameid) {
		this.paygameid = paygameid;
	}

	public String getPlayerid() {
		return playerid;
	}

	public void setPlayerid(String playerid) {
		this.playerid = playerid;
	}

	public String getGamestring() {
		return gamestring;
	}

	public void setGamestring(String gamestring) {
		this.gamestring = gamestring;
	}

	public String getAppkey() {
		return appkey;
	}

	public void setAppkey(String appkey) {
		this.appkey = appkey;
	}

	public UOrderManager getOrderManager() {
		return orderManager;
	}

	public void setOrderManager(UOrderManager orderManager) {
		this.orderManager = orderManager;
	}

}
