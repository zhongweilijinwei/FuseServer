package com.u8.server.sdk.qkqk;

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

public class QkSdk implements ISDKScript {

	@Override
	public void verify(final UChannel channel, String extension, final ISDKVerifyListener callback) {

		JSONObject json = JSONObject.fromObject(extension);
		final String sid = json.getString("sid");
		final String userid = json.getString("userid");


		String AppSecret=channel.getCpAppSecret();
		StringBuilder sb = new StringBuilder();
		sb.append(userid).append(sid).append(channel.getCpAppID()).append(AppSecret);
		String sign = EncryptUtils.md5(sb.toString());

		Log.d("the sign is : " + sign);
		
		 Map<String,String> params = new HashMap<String, String>();
         params.put("uid", userid);
         params.put("vkey", sid);
         params.put("appid", channel.getCpAppID());
		 params.put("sign", sign);

		String url = channel.getChannelAuthUrl();

		UHttpAgent.getInstance().post(url, params, new UHttpFutureCallback() {
			@Override
			public void completed(String result) {

				try {
					if (!TextUtils.isEmpty(result)) {
						JSONObject jr = JSONObject.fromObject(result);
						
						String status = jr.getString("status");
                        //成功
						if(status.equals("0")){
							callback.onSuccess(new SDKVerifyResult(true, userid,"", ""));
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
