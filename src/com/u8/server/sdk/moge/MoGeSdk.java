package com.u8.server.sdk.moge;

import net.sf.json.JSONObject;

import com.u8.server.data.UChannel;
import com.u8.server.data.UOrder;
import com.u8.server.data.UUser;
import com.u8.server.log.Log;
import com.u8.server.sdk.ISDKOrderListener;
import com.u8.server.sdk.ISDKScript;
import com.u8.server.sdk.ISDKVerifyListener;
import com.u8.server.sdk.SDKVerifyResult;
import com.u8.server.sdk.UHttpAgent;
import com.u8.server.sdk.UHttpFutureCallback;
import com.u8.server.utils.EncryptUtils;

public class MoGeSdk implements ISDKScript{

	
//	jsonObject.put("sign",sign);
//	jsonObject.put("loginTime", loginTime);
//	jsonObject.put("username",username);
//	jsonObject.put("memkey",memkey);
	@Override
	public void verify(UChannel channel, String extension,
			ISDKVerifyListener callback) {
		
		
		try{
		JSONObject jsonObject=JSONObject.fromObject(extension);
		String AppKey=channel.getCpAppKey();
		
		String sign=jsonObject.getString("sign");
		String uid=jsonObject.getString("username");
		String loginTime=jsonObject.getString("loginTime");
		
		StringBuilder stringBuilder=new StringBuilder();
		
		
		stringBuilder.append("username").append("=").append(uid).append("&")
		.append("appkey").append("=").append(AppKey).append("&")
		.append("logintime").append("=").append(loginTime);
		
		
		  String newsign=EncryptUtils.md5(stringBuilder.toString());
          Log.e("The newsign1 is "+newsign);
          //签名验证
          if(!newsign.equals(sign)){
        	  callback.onSuccess(new SDKVerifyResult(false, "", "", ""));
          }else{
        	  callback.onSuccess(new SDKVerifyResult(true, uid, "", ""));
          }
      
		}catch (Exception e) {
			callback.onSuccess(new SDKVerifyResult(false, "", "", ""));
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
