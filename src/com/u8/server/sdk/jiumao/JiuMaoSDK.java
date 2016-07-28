package com.u8.server.sdk.jiumao;

import net.sf.json.JSONObject;

import com.u8.server.data.UChannel;
import com.u8.server.data.UOrder;
import com.u8.server.data.UUser;
import com.u8.server.sdk.*;

public class JiuMaoSDK implements ISDKScript {
//	extension={accountNo:"dgsg4t43tgrg5"}
    @Override
    public void verify(final UChannel channel, String extension, final ISDKVerifyListener callback) {

    	if(callback != null){
    		JSONObject objcet=JSONObject.fromObject(extension);
    		String uid=(String) objcet.get("accountNo");
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
