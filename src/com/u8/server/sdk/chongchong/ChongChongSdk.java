package com.u8.server.sdk.chongchong;

import java.util.HashMap;
import java.util.Map;

import org.apache.http.util.TextUtils;

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

public class ChongChongSdk implements ISDKScript{


	//	extension={uid:"dgsg4t43tgrg5",token:"39752375878328",userName:"魔王"}
	@Override
	public void verify(final UChannel channel, String extension,
			final ISDKVerifyListener callback) {


		try{
			JSONObject jsonObject=JSONObject.fromObject(extension);
			String AppKey=channel.getCpAppKey();

			final String uid=jsonObject.getString("uid");
			String token=jsonObject.getString("token");
			final String userName=jsonObject.getString("userName");


			Map<String,String> params = new HashMap<String, String>();
			params.put("token", token);

			String url = channel.getChannelAuthUrl();

			UHttpAgent.getInstance().get(url, params, new UHttpFutureCallback() {
				@Override
				public void completed(String result) {

					try {

						Log.d("The result of lewan verify is :" + result);
						if (!TextUtils.isEmpty(result)) {
							if(result.equals("success")){
								callback.onSuccess(new SDKVerifyResult(true, uid, userName, ""));
								return;
							}else{
								callback.onSuccess(new SDKVerifyResult(false, "", "", ""));
							}

						}else{
							callback.onSuccess(new SDKVerifyResult(false, "", "", ""));
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

		}catch (Exception e){
			e.printStackTrace();
			callback.onFailed(channel.getMaster().getSdkName() + " verify execute failed. the exception is "+e.getMessage());
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
