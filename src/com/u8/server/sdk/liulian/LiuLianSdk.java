package com.u8.server.sdk.liulian;

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

public class LiuLianSdk implements ISDKScript {
//	{"msg":"登录成功","sid":"a190392b618b587f038f86869d77b0af"}
	@Override
	public void verify(final UChannel channel, String extension, final ISDKVerifyListener callback) {

		JSONObject json = JSONObject.fromObject(extension);
		String sid = json.getString("sid");


		String AppSecret=channel.getCpAppSecret();
		String appid=channel.getCpAppID();
		String appkey=channel.getCpAppKey();
		
		StringBuilder sb = new StringBuilder();
		sb.append(appid).append(appkey).append(AppSecret).append(sid);
		
		String sign = EncryptUtils.md5(sb.toString()).toLowerCase();

		Log.d("the sign is : " + sign);
		
		 Map<String,String> params = new HashMap<String, String>();
         params.put("appid", appid);
         params.put("appkey", appkey);
         params.put("sid", sid);
		 params.put("sign", sign);

		String url = channel.getChannelAuthUrl();
//		http://user.61yx.com/sdk/game/verifylogin      http://test.user.61yx.com:8081/sdk/game/verifylogin
		UHttpAgent.getInstance().post(url, params, new UHttpFutureCallback() {
			@Override
			public void completed(String result) {

				try {
					if (!TextUtils.isEmpty(result)) {
						JSONObject jr = JSONObject.fromObject(result);
						
						String status = jr.getString("status");
						String userid = jr.getString("userid");
                        //成功
						if(status.equals("1")){
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
