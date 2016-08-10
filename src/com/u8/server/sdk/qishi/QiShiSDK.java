package com.u8.server.sdk.qishi;

import net.sf.json.JSONObject;

import com.u8.server.data.UChannel;
import com.u8.server.data.UOrder;
import com.u8.server.data.UUser;
import com.u8.server.sdk.*;
import com.u8.server.utils.EncryptUtils;

public class QiShiSDK implements ISDKScript {
//	extension={userid:"dgsg4t43tgrg5",username:"39752375878328",logintime:"魔王",sgin:""}
	
    @Override
    public void verify(final UChannel channel, String extension, final ISDKVerifyListener callback) {

    	if(callback != null){
    		JSONObject objcet=JSONObject.fromObject(extension);
    		String uid=(String) objcet.get("userid");
    		String username=(String) objcet.get("username");
    		String logintime=(String) objcet.get("logintime");
    		String sgin=(String) objcet.get("sgin");
    		String AppKey=channel.getCpAppKey();
    		
    		StringBuilder stringBuilder=new StringBuilder();
    		
    		
    		stringBuilder.append("username").append("=").append(uid).append("&")
    		.append("appkey").append("=").append(AppKey).append("&")
    		.append("logintime").append("=").append(logintime);
    		
    		  String newsign=EncryptUtils.md5(stringBuilder.toString()).toLowerCase();
    		  //签名验证
              if(!newsign.equals(sgin)){
            	  callback.onSuccess(new SDKVerifyResult(false, "", "", ""));
              }else{
            	  callback.onSuccess(new SDKVerifyResult(true, uid, "", ""));
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
