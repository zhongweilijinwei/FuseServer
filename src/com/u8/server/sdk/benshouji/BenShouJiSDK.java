package com.u8.server.sdk.benshouji;

import com.u8.server.data.UChannel;
import com.u8.server.data.UOrder;
import com.u8.server.data.UUser;
import com.u8.server.log.Log;
import com.u8.server.sdk.*;
import com.u8.server.utils.EncryptUtils;
import net.sf.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * 笨手机 还没有验证通过
 * Created by ant on 2015/4/30.
 */
public class BenShouJiSDK implements ISDKScript{
    @Override
    public void verify(final UChannel channel, String extension, final ISDKVerifyListener callback) {
        try{

            JSONObject json = JSONObject.fromObject(extension);
            final String uid = json.getString("uid");
            String token = json.getString("token");
           
            Map<String,String> params = new HashMap<String, String>();
            params.put("token", token);
            String appsecret=channel.getCpAppSecret();
            StringBuilder sb = new StringBuilder();
            sb.append(appsecret).append(token);
            String sign = EncryptUtils.md5(sb.toString()).toUpperCase();
            params.put("sign", sign);


            String url = channel.getChannelAuthUrl();

            UHttpAgent.getInstance().get(url, params, new UHttpFutureCallback() {
                @Override
                public void completed(String result) {
                	JSONObject json = JSONObject.fromObject(result);
                	boolean iscued=json.getBoolean("succeed");
                    try {

                        if(iscued){

                            callback.onSuccess(new SDKVerifyResult(true, uid, "", ""));
                            return;

                        }else{
                        	callback.onSuccess(new SDKVerifyResult(false, "", "", ""));
                            return;
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
    public void onGetOrderID(UUser user, UOrder order, ISDKOrderListener callback) {
        if(callback != null){
            callback.onSuccess("");
        }
    }
}
