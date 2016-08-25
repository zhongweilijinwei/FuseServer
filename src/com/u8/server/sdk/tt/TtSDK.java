package com.u8.server.sdk.tt;

import java.io.IOException;

import net.sf.json.JSONObject;

import com.u8.server.data.UChannel;
import com.u8.server.data.UOrder;
import com.u8.server.data.UUser;
import com.u8.server.sdk.*;

public class TtSDK implements ISDKScript {
//	jsonObject.put("gameid",gameid);
//	jsonObject.put("uid", uid);
//	jsonObject.put("session", session);
    @Override
    public void verify(final UChannel channel, String extension, final ISDKVerifyListener callback) {

    	if(callback != null){
    		JSONObject objcet=JSONObject.fromObject(extension);
    		
    		String gameid1=(String) objcet.get("gameid");
    		String uid=(String) objcet.get("uid");
    		String session=(String) objcet.get("session");
    		String authurl=channel.getChannelAuthUrl();
    		String gameid2=channel.getCpID();
    		String apikey=channel.getCpAppKey();
    		
    		UsersInfo info=new UsersInfo(Long.parseLong(uid),session,gameid2,apikey,authurl);
    		
    		try {
//    			{"head":{"result":"0","message":"SUCCESS"},"body":null}
				String restr = SDKServerService.verifySession(info);
				JSONObject rejson=JSONObject.fromObject(restr);
				JSONObject reheas=rejson.optJSONObject("head");
				String result=reheas.optString("result");
				if(result.equals("0")){
				callback.onSuccess(new SDKVerifyResult(true, uid, "", ""));
				}else{
					callback.onSuccess(new SDKVerifyResult(false,"", "", ""));
				}
				
				
			} catch (Exception e) {
				e.printStackTrace();
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
