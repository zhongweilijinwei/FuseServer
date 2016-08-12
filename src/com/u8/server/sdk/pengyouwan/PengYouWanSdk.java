package com.u8.server.sdk.pengyouwan;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.params.HttpParams;
import org.apache.http.util.TextUtils;

import net.sf.json.JSONObject;

import com.u8.server.data.UChannel;
import com.u8.server.data.UOrder;
import com.u8.server.data.UUser;
import com.u8.server.log.Log;
import com.u8.server.sdk.*;
import com.u8.server.utils.EncryptUtils;

public class PengYouWanSdk implements ISDKScript {

	//	jsonObject.put("userId", userId);
	//	jsonObject.put("userName", userName);
	//	jsonObject.put("token", token);
	@Override
	public void verify(final UChannel channel, String extension, final ISDKVerifyListener callback) {

		JSONObject json = JSONObject.fromObject(extension);

		final String userId = json.getString("userId");
		final String userName = json.getString("userName");
		final String token = json.getString("token");

		//		String msg = jr.getString("data");
		if (!TextUtils.isEmpty(userId)) {// 表明响应成功
			callback.onSuccess(new SDKVerifyResult(true, userId,"", ""));
		} else {// 响应失败
			callback.onSuccess(new SDKVerifyResult(false, "", "", ""));
		}

	}

	/**
	 * 获取订单号
	 * 
	 * @return
	 */
	private String getOutTradeNo() {
		// ''.time().rand(100000,999999);
		long timestamp = System.currentTimeMillis();
		Random random = new Random();
		int s = random.nextInt(9999) % (9999 - 1000 + 1) + 1000;
		return "" + System.currentTimeMillis() + s;
	}

	@Override
	public void onGetOrderID(UUser user, UOrder order,
			ISDKOrderListener callback) {
		if(callback != null){
			callback.onSuccess("");
		}

	}


}
