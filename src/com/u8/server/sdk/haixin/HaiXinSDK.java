package com.u8.server.sdk.haixin;

import net.sf.json.JSONObject;

import com.u8.server.data.UChannel;
import com.u8.server.data.UOrder;
import com.u8.server.data.UUser;
import com.u8.server.sdk.*;

public class HaiXinSDK implements ISDKScript {
//	jsonObject.put("token",token);
//	jsonObject.put("customerId", String.valueOf(customerId));
//	jsonObject.put("loginName", loginName);
    @Override
    public void verify(final UChannel channel, String extension, final ISDKVerifyListener callback) {

    	if(callback != null){
    		JSONObject objcet=JSONObject.fromObject(extension);
    		String uid=(String) objcet.get("customerId");
    		callback.onSuccess(new SDKVerifyResult(true, uid + "", "", ""));
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
