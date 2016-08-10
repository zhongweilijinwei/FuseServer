package com.u8.server.sdk.shendeng;

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

public class ShenDengSdk implements ISDKScript {
	//	{mobile=13826088717, mid=a29b3f7afdd840c5a4c72a7d0cf15174, token=d99ec81ccb3c476a9b81ce8a6235fb36}
	@Override
	public void verify(final UChannel channel, String extension, final ISDKVerifyListener callback) {

		JSONObject json = JSONObject.fromObject(extension);
		String mobile=json.getString("mobile");
		final String mid=json.getString("mid");
		String token=json.getString("token");

		String AppSecret=channel.getCpAppSecret();
		String appid=channel.getCpAppID();
		String cpid=channel.getCpID();

		Map<String,String> params = new HashMap<String, String>();
		
		params.put("act", "userInfo");
		params.put("appId", appid);
		params.put("cpId", cpid);
		params.put("token", token);
		params.put("mid", mid);
		
		String signstring=Sign.assembly(params,AppSecret);
		String sign=Sign.preSign(signstring);
		params.put("sign", sign);
		
		String url = channel.getChannelAuthUrl();
		
		//		http://user.61yx.com/sdk/game/verifylogin      http://test.user.61yx.com:8081/sdk/game/verifylogin
		UHttpAgent.getInstance().get(url, params, new UHttpFutureCallback() {
			@Override
			public void completed(String result) {

				try {
					if (!TextUtils.isEmpty(result)) {
						JSONObject jr = JSONObject.fromObject(result);

						String status = jr.getString("status");
						//成功
						if(status.equals("0")){
							callback.onSuccess(new SDKVerifyResult(true, mid,"", ""));
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
