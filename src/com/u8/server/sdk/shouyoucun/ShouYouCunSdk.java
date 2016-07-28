package com.u8.server.sdk.shouyoucun;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

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
import com.u8.server.sdk.ISDKOrderListener;
import com.u8.server.sdk.ISDKScript;
import com.u8.server.sdk.ISDKVerifyListener;
import com.u8.server.sdk.SDKVerifyResult;
import com.u8.server.sdk.UHttpAgent;
import com.u8.server.sdk.UHttpFutureCallback;
import com.u8.server.utils.EncryptUtils;

public class ShouYouCunSdk implements ISDKScript{

	
	/**
	 * @param request
	 *            网络的输入流
	 * @缘由 不需要解压缩 zip 直接用json格式
	 * 
	 * */
	public String readFromNetStream(InputStream request) {
		try {
			byte[] buffer = new byte[1024];
			int num = 0;
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			while (-1 != (num = request.read(buffer))) {
				bos.write(buffer, 0, num);
			}
			return bos.toString("utf-8");
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public InputStream doRequest(String url, String str,ISDKVerifyListener callback) {

		HttpClient client = new DefaultHttpClient();
		HttpParams params = client.getParams();
		params.setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 30 * 1000);
		params.setParameter(CoreConnectionPNames.SO_TIMEOUT, 30 * 1000);

		HttpPost post = new HttpPost(url);
		post.setHeader("content-type", "text/html");
		if (str != null) {
			HttpEntity entity = new ByteArrayEntity(str.getBytes());
			post.setEntity(entity);
		}
		int count = 0;
		// 等待3秒在请求2次
		while (count < 2) {
			try {
				HttpResponse response = client.execute(post);
				int code = response.getStatusLine().getStatusCode();
				if (code == HttpStatus.SC_OK) {
					return response.getEntity().getContent();
				}
			} catch (ClientProtocolException e) {
				System.out.println("网络异常");
				callback.onFailed("网络异常");
				e.printStackTrace();
			} catch (IOException e) {
				System.out.println("网络异常");
				callback.onFailed("网络异常");
				e.printStackTrace();
			}
			count++;
			try {
				// 请求失败，在请求一次
				Thread.currentThread().sleep(3000);
			} catch (InterruptedException e) {
				System.out.println("网络异常");
				e.printStackTrace();
			}
		}
		return null;
	}
	
//	extension={user_id:"43254",user_token:"dshfdhbcvwaegargs"}
	@Override
	public void verify(final UChannel channel, String extension,
			final ISDKVerifyListener callback) {
		
		try{
		JSONObject jsonObject=JSONObject.fromObject(extension);
		String appid=channel.getCpAppID();
		String appkey=channel.getCpAppKey();
		String user_id=jsonObject.getString("user_id");
		String user_token=jsonObject.getString("user_token");
		
//		sign=md5(app_id=1&mem_id=23&user_token=rkmi2huqu9dv6750g5os11ilv2&
//				app_key=de933fdbede098c62cb309443c3cf251)
		
		StringBuilder strSign = new StringBuilder();
        strSign.append("app_id").append("=").append(appid).append("&")
        .append("mem_id").append("=").append(user_id).append("&")
        .append("user_token").append("=").append(user_token).append("&")
        .append("app_key").append("=").append(appkey);
        
		String newsign=EncryptUtils.md5(strSign.toString());
		
		JSONObject jo = new JSONObject();
		jo.put("app_id", appid);
		jo.put("mem_id", user_id);
		jo.put("user_token", user_token);
		jo.put("sign", newsign);
		
		String url = channel.getChannelAuthUrl();
		
		InputStream is = doRequest(url, jo.toString(),callback);
		
		String responseStr=readFromNetStream(is);
		
		JSONObject jr = JSONObject.fromObject(responseStr);
		String status = jr.getString("status");
//		String msg = jr.getString("data");
		if ("1".equals(status)) {// 表明响应成功
			callback.onSuccess(new SDKVerifyResult(true, user_id,"", ""));
		} else {// 响应失败
			callback.onSuccess(new SDKVerifyResult(false, "", "", ""));
		}
		
		
//		UHttpAgent.getInstance().post(url, params, new UHttpFutureCallback() {
//			@Override
//			public void completed(String result) {
//				try {
//					if (!TextUtils.isEmpty(result)) {
//						JSONObject jr = JSONObject.fromObject(result);
//						String status = jr.getString("status");
//
//						//成功
//						if(status.equals("1")){
//							callback.onSuccess(new SDKVerifyResult(true, uid,"", ""));
//							return;
//							//失败
//						}else{
//							callback.onSuccess(new SDKVerifyResult(false, "", "", ""));
//							return;
//
//						}
//					}
//
//				} catch (Exception e) {
//					e.printStackTrace();
//				}
//
//				callback.onFailed(channel.getMaster().getSdkName() + " verify failed. the get result is " + result);
//			}
//
//			@Override
//			public void failed(String err) {
//				callback.onFailed(channel.getMaster().getSdkName() + " verify failed. " + err);
//			}
//
//		});
		
		}catch(Exception e){e.printStackTrace();}
		
	}

	@Override
	public void onGetOrderID(UUser user, UOrder order,
			ISDKOrderListener callback) {
		 if(callback != null){
	            callback.onSuccess("");
	        }
		
	}

}
