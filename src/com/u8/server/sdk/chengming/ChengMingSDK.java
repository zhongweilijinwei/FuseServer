package com.u8.server.sdk.chengming;

import net.sf.json.JSONObject;

import com.u8.server.data.UChannel;
import com.u8.server.data.UOrder;
import com.u8.server.data.UUser;
import com.u8.server.sdk.*;

public class ChengMingSDK implements ISDKScript {
//	{"userID":XXXXX,"loginName":"XXXXX","loginToken":"XXXXX"}
    @Override
    public void verify(final UChannel channel, String extension, final ISDKVerifyListener callback) {

    	if(callback != null){
    		JSONObject objcet=JSONObject.fromObject(extension);
    		String logintoken=(String) objcet.get("loginToken");
    		Long userID=objcet.getLong("userID");
    		String appid=channel.getCpAppID();
    		String privatekey=channel.getCpPayPriKey();
    		String publickey=channel.getCpPayKey();
    		String url=channel.getChannelAuthUrl();
    		boolean isloged=CheckLogin.CheckToken(url,appid, logintoken, privatekey,publickey);
    		if(isloged){
    			callback.onSuccess(new SDKVerifyResult(true,Long.toString(userID), "", ""));
    		}else{
    			callback.onSuccess(new SDKVerifyResult(false,  "", "", ""));
    		}
    		
    	}
       
    }

	@Override
	public void onGetOrderID(UUser user, UOrder order,
			ISDKOrderListener callback) {
        if(callback != null){
            callback.onSuccess("");
        }
		
	}

   
}
