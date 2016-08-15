package com.u8.server.sdk.htc_jvle;

import net.sf.json.JSONObject;

import com.u8.server.data.UChannel;
import com.u8.server.data.UOrder;
import com.u8.server.data.UUser;
import com.u8.server.log.Log;
import com.u8.server.sdk.*;
import com.u8.server.utils.EncryptUtils;

public class HtcJvLeSDK implements ISDKScript {
	//	jsonObject.put("userName",userName);
	//	jsonObject.put("userId", userId);
	//	jsonObject.put("session", session);
	//	jsonObject.put("accountSign", accountSign);
	//	jsonObject.put("account", account);

	@Override
	public void verify(final UChannel channel, String extension, final ISDKVerifyListener callback) {

		if(callback != null){
			JSONObject objcet=JSONObject.fromObject(extension);
			String userName=objcet.getString("userName");
			String userId=objcet.getString("userId");
			String session=objcet.getString("session");
			String accountSign=objcet.getString("accountSign");
			String account=objcet.getString("account");
			if(account.startsWith("\"") && account.endsWith("\"")){
				account=account.substring(1, account.length()-1);
			}
			String publickey=channel.getCpPayKey();
			

			boolean isdo=RsaSign.doCheck(account, accountSign, publickey);
			//签名验证
			if(isdo){
				callback.onSuccess(new SDKVerifyResult(true, userId, "", ""));
			}else{
				callback.onSuccess(new SDKVerifyResult(false, "", "", ""));
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
