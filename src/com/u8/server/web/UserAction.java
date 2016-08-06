package com.u8.server.web;

import com.u8.server.cache.SDKCacheManager;
import com.u8.server.common.UActionSupport;
import com.u8.server.constants.StateCode;
import com.u8.server.data.UChannelMaster;
import com.u8.server.data.UUser;
import com.u8.server.log.Log;
import com.u8.server.sdk.ISDKScript;
import com.u8.server.sdk.ISDKVerifyListener;
import com.u8.server.sdk.SDKVerifyResult;
import com.u8.server.service.UChannelManager;
import com.u8.server.service.UGameManager;
import com.u8.server.service.UUserManager;
import com.u8.server.utils.EncryptUtils;
import com.u8.server.utils.UGenerator;
import com.u8.server.data.UChannel;
import com.u8.server.data.UGame;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;


/***
 * 用户登录
 */
@Controller
@Namespace("/user")
public class UserAction extends UActionSupport{

    private int appID;
    private int channelID;

    private String extension;

    private int userID;
    private String token;

    private String sign;            //签名

    @Autowired
    private UGameManager gameManager;

    @Autowired
    private UChannelManager channelManager;

    @Autowired
    private UUserManager userManager;



    @Action("getToken")
    public void getLoginToken(){

        Log.d("getToken...");

        try{

            final UGame game = gameManager.queryGame(this.appID);
            if(game == null){
                renderState(StateCode.CODE_GAME_NONE, null);
                return;
            }

            final UChannel channel = channelManager.queryChannel(this.channelID);
            if(channel == null){

                renderState(StateCode.CODE_CHANNEL_NONE, null);
                return;
            }

            UChannelMaster master = channel.getMaster();
            if(master == null){
                renderState(StateCode.CODE_CHANNEL_NONE, null);
                return;
            }

            StringBuilder sb = new StringBuilder();
            sb.append("appID=").append(this.appID)
                    .append("channelID=").append(this.channelID)
                    .append("extension=").append(this.extension).append(game.getAppkey());


            if(!userManager.isSignOK(sb.toString(), sign)){
                Log.e("the sign is invalid. sign:"+sign);
                renderState(StateCode.CODE_SIGN_ERROR, null);
                return;
            }

            ISDKScript verifier = SDKCacheManager.getInstance().getSDKScript(channel);

            if(verifier == null){
                Log.e("the ISDKScript is not found . channelID:"+channelID);
                renderState(StateCode.CODE_VERIFY_FAILED, null);
                return;
            }

            Log.d("The auth url is "+channel.getChannelAuthUrl());
            Log.d("channel is "+channel.getChannelID()+";extension is "+extension);

            verifier.verify(channel, extension, new ISDKVerifyListener() {
                @Override
                public void onSuccess(SDKVerifyResult sdkResult) {

                    try{
                        Log.d("user verify success. result:"+sdkResult.getUserID());
                        if(sdkResult.isSuccess() && !StringUtils.isEmpty(sdkResult.getUserID())){

                            UUser user = userManager.getUserByCpID(appID, channelID, sdkResult.getUserID());

                            if(user == null){
                                user = userManager.generateUser(channel, sdkResult);
                            }else{
                                user.setLastLoginTime(new Date().getTime() + "");
                            }

                            user.setToken(UGenerator.generateToken(user, game.getAppSecret()));

                            userManager.saveUser(user);

                            JSONObject data = new JSONObject();
                            data.put("userID", user.getId());
                            data.put("sdkUserID", user.getChannelUserID());
                            data.put("username", user.getName());
                            data.put("sdkUserName", user.getChannelUserName());
                            data.put("token", user.getToken());
                            data.put("extension", sdkResult.getExtension());
                            data.put("timestamp", user.getLastLoginTime());
                            renderState(StateCode.CODE_SUCCESS, data);

                        }else{
                            renderState(StateCode.CODE_AUTH_FAILED, null);
                        }

                    }catch (Exception e){
                        renderState(StateCode.CODE_AUTH_FAILED, null);
                        e.printStackTrace();
                    }

                }

                @Override
                public void onFailed(String errorMsg) {
                    Log.e("The user verify failed. errorMsg:"+errorMsg);
                    renderState(StateCode.CODE_AUTH_FAILED, null);
                }
            });


        }catch (Exception e){
            Log.e(e.getMessage());
            renderState(StateCode.CODE_AUTH_FAILED, null);
        }
    }
    
    private void renderState(int state, JSONObject data){
        try{


            JSONObject json = new JSONObject();
            json.put("state", state);
            json.put("data", data);

            super.renderJson(json.toString());

        }catch(Exception e){
            e.printStackTrace();
            Log.e(e.getMessage());
        }


    }


    /***
     * 上面协议返回客户端之后，开始连接登录游戏服。游戏服可以调用该协议进行再次登录认证。
     * 但是，该步骤是可选的。游戏服务器也可以自己验证token以及token的时效性，这样就不用来
     * U8Server进行再次登录认证了。
     *
     * 服务器自己验证token，根据U8Server分配给每个游戏参数中的AppSecret，按照生成token的
     * 规则，进行验证。同时，需要验证timestamp的时效性
     *
     */
    @Action("verifyAccount")
    public void loginVerify(){

        try{

            UUser user = userManager.getUser(this.userID);
            if(user == null){
                renderState(StateCode.CODE_USER_NONE, null);
                return;
            }


            if(StringUtils.isEmpty(this.token)){
                renderState(StateCode.CODE_VERIFY_FAILED, null);
                return;
            }

            
           String appkey=user.getGame().getAppkey();
            
            StringBuilder sb = new StringBuilder();
            sb.append("userID=").append(this.userID)
                    .append("token=").append(this.token)
                    .append(appkey);


            if(!userManager.isSignOK(sb.toString(), sign)){
                renderState(StateCode.CODE_SIGN_ERROR, null);
                return;
            }

            long now = System.currentTimeMillis();
            if(!userManager.checkUser(user, token)){
                renderState(StateCode.CODE_TOKEN_ERROR, null);
                return;
            }

            JSONObject data = new JSONObject();
            data.put("userID", user.getId());
            data.put("username", user.getName());

            renderState(StateCode.CODE_SUCCESS, data);
            return;

        }catch (Exception e){
            Log.e(e.getMessage());
        }

        renderState(StateCode.CODE_VERIFY_FAILED, null);
    }

    public int getAppID() {
        return appID;
    }

    public void setAppID(int appID) {
        this.appID = appID;
    }

    public int getChannelID() {
        return channelID;
    }

    public void setChannelID(int channelID) {
        this.channelID = channelID;
    }

    public String getExtension() {
        return extension;
    }

    public void setExtension(String extension) {
        this.extension = extension;
    }

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }
}
