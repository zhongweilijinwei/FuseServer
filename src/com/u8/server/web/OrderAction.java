package com.u8.server.web;

import com.u8.server.cache.SDKCacheManager;
import com.u8.server.common.UActionSupport;
import com.u8.server.constants.PayState;
import com.u8.server.constants.StateCode;
import com.u8.server.data.UOrder;
import com.u8.server.data.UUser;
import com.u8.server.log.Log;
import com.u8.server.sdk.ISDKOrderListener;
import com.u8.server.sdk.ISDKScript;
import com.u8.server.service.UOrderManager;
import com.u8.server.service.UUserManager;
import com.u8.server.utils.RSAUtils;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/***
 * 查询订单号信息接口
 */
@Controller
@Namespace("/order")
public class OrderAction extends UActionSupport{

//	private static final int SET_STATE_CANCEL = 0;//订单已取消支付
//	private static final int SET_STATE_PAYING = 1;//订单在支付中
//	private static final int SET_STATE_FAIL = 2;//订单支付失败
    private int state;
    private long orderID;

    @Autowired
    private UOrderManager orderManager;
   
    @Action("orderState")
    public void getOrderState(){
        try{

            if(orderID <= 0){
                renderState(StateCode.CODE_PARAM_ERROR, null);
                return;
            }

            final UOrder order = orderManager.getOrder(orderID);

            if(order != null){

                JSONObject data = new JSONObject();
                data.put("appID", order.getAppID());
                data.put("state", order.getState());//当前订单的状态
                data.put("channelID", order.getChannelID());
                data.put("productName", order.getProductName());
                data.put("money", order.getMoney());
                data.put("roleID", order.getRoleID());
                
                //更新支付状态
                switch (state) {
				case 0:
					order.setState(PayState.STATE_CANCEL);
					orderManager.saveOrder(order);
					break;
				case 1:
					order.setState(PayState.STATE_PAYING);
					orderManager.saveOrder(order);
					break;
				case 2:
					order.setState(PayState.STATE_FAILED);
					orderManager.saveOrder(order);
					break;
				}
                renderState(StateCode.CODE_SUCCESS, data);
            }else{
            	
            	renderState(StateCode.CODE_PARAM_ERROR, null);
            }


        }catch (Exception e){
            renderState(StateCode.CODE_ORDER_ERROR, null);
            Log.e(e.getMessage());
        }


    }


    private void renderState(int state, JSONObject data){
        JSONObject json = new JSONObject();
        json.put("state", state);
        json.put("data", data);

        super.renderJson(json.toString());
    }


	public long getOrderID() {
		return orderID;
	}


	public void setOrderID(long orderID) {
		this.orderID = orderID;
	}


	public int getState() {
		return state;
	}


	public void setState(int state) {
		this.state = state;
	}
}
