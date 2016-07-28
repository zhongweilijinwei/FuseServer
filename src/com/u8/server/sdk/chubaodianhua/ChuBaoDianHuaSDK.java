package com.u8.server.sdk.chubaodianhua;

import net.sf.json.JSONObject;

import com.u8.server.data.UChannel;
import com.u8.server.data.UOrder;
import com.u8.server.data.UUser;
import com.u8.server.sdk.*;

public class ChuBaoDianHuaSDK implements ISDKScript {
//	extension：{"code":"2000","errmsg":"OK","cootek_uid":"17325894720070762117"}
    @Override
    public void verify(final UChannel channel, String extension, final ISDKVerifyListener callback) {

    	if(callback != null){
    		JSONObject objcet=JSONObject.fromObject(extension);
    		String cootek_uid=(String) objcet.get("cootek_uid");
    		callback.onSuccess(new SDKVerifyResult(true, cootek_uid, "", ""));
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
