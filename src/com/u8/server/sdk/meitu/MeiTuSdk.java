package com.u8.server.sdk.meitu;

import java.util.HashMap;
import java.util.Map;

import org.apache.http.util.TextUtils;

import net.sf.json.JSONObject;

import com.u8.server.data.UChannel;
import com.u8.server.data.UOrder;
import com.u8.server.data.UUser;
import com.u8.server.log.Log;
import com.u8.server.sdk.*;
import com.u8.server.utils.EncryptUtils;

public class MeiTuSdk implements ISDKScript {

//	jsonObject.put("sessionid", sessionid);
//	jsonObject.put("uid", uid);
//	jsonObject.put("name", name);
	@Override
	public void verify(final UChannel channel, String extension, final ISDKVerifyListener callback) {

		JSONObject json = JSONObject.fromObject(extension);
		
		 String sessionid = json.getString("sessionid");
		 final String uid = json.getString("uid");
		 String name = json.getString("name");
		String Appid=channel.getCpAppID();
		String Appkey=channel.getCpAppKey();//做login_key
		
		StringBuilder sb = new StringBuilder();
		sb.append(Appid).append(uid).append(sessionid).append(Appkey);
		String sign = EncryptUtils.md5(sb.toString());

		
		 Map<String,String> params = new HashMap<String, String>();
         params.put("appid", Appid);
         params.put("uid", uid);
         params.put("state", sessionid);
		 params.put("flag", sign);

		String url = channel.getChannelAuthUrl();

		UHttpAgent.getInstance().get(url, params, new UHttpFutureCallback() {
			@Override
			public void completed(String result) {

				try {
					if (!TextUtils.isEmpty(result)) {
						JSONObject jr = JSONObject.fromObject(result);
						
						int code = jr.getInt("ret");
                        //成功
						if(code==100){
							callback.onSuccess(new SDKVerifyResult(true, uid,"", ""));
							return;
							//失败
						}else{
							callback.onSuccess(new SDKVerifyResult(false, "", "", ""));
							return;
							
						}
					}

				} catch (Exception e) {
					e.printStackTrace();
				}

				callback.onFailed(channel.getMaster().getSdkName() + " verify failed. the get result is " + result);
			}

			@Override
			public void failed(String err) {
				callback.onFailed(channel.getMaster().getSdkName() + " verify failed. " + err);
			}

		});
	}

	@Override
	public void onGetOrderID(UUser user, UOrder order,
			ISDKOrderListener callback) {
		if(callback != null){
			callback.onSuccess("");
		}

	}


}
