package com.u8.server.web;

import com.u8.server.common.UActionSupport;
import com.u8.server.constants.PayState;
import com.u8.server.data.UChannel;
import com.u8.server.data.UOrder;
import com.u8.server.log.Log;
import com.u8.server.sdk.baidu.BaiduPayResult;
import com.u8.server.sdk.chubaodianhua.Base64;
import com.u8.server.sdk.chubaodianhua.Constants;
import com.u8.server.sdk.chubaodianhua.StreamUtils;
import com.u8.server.sdk.chubaodianhua.StringUtils;
import com.u8.server.service.UOrderManager;
import com.u8.server.utils.EncryptUtils;
import com.u8.server.utils.JsonUtils;
import com.u8.server.utils.RSAUtils;
import com.u8.server.utils.TimeFormater;
import net.sf.json.JSONObject;
import org.apache.http.util.TextUtils;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.KeyFactory;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * 爱拍SDK充值回调接口
 * Created by ant on 2015/2/28.
 */

@Controller
@Namespace("/pay/chubao")
public class ChuBaoDianHuaCallbackAction extends UActionSupport{
	//	http://14.23.156.2:8089/YTServer/pay/chubao/payCallback
	private String apiVersion;//API版本	NO
	private String appkey;//创建订单appkey	NO
	private String sellerId;//创建订单sellerId	NO
	private  int timestamp;//时间戳，防回放攻击	NO
	private String charset;//编码方式，仅支持UTF-8	NO
	private String signType;//签名类型RSA	NO
	private String sign;//签名字符串	NO
	private String userId;//用户身份唯一标识符 NO
	private String result;//返回状态码: SUCCESS/FAIL	NO
	private int errorCode;//业务结果码，当returnCode为FAIL
	private String errorMessage;//业务结果信息，errorCode的具体描述	YES
	private String paymentType;//支付方式,"alipay"或者"weipay"
	private String tradeNo;//输入参数orderId
	private String tradeService;//服务identifier,如com.cootek.recharge
	private String tradeName;//交易主题
	private String tradeDesc;//交易描述
	private int totalFee;//交易金额[1~100000000], 正整数,单位为分	NO
	private int cashFee;//用户需要支付的现金额度	NO
	private int settleFee;//商户结算时应该收到的现金额度
	private String transactionId;//触宝开放平台交易号
	private String notifyTime;//通知发送时间
	private String notifyId;//通知id，防止重复通知
	private String tradeCreateTime;//交易创建时间
	private String tradeSuccessTime;//付款成功时间
	private String tradeStatus;//交易状态，详见定义
	private String attach;//附加数据，订单创建时的数据


	@Autowired
	private UOrderManager orderManager;

	@Action("payCallback")
	public void payCallback(){

		String requests = null;
		try {
			requests=readFromNetStream(request.getInputStream());
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		JSONObject jr = JSONObject.fromObject(requests);
		HashMap<String, Object> params=toHashMap(jr);
		
		tradeNo=(String) params.get("tradeNo");
		totalFee=(Integer) params.get("totalFee");
		try{
			long orderID = Long.parseLong(tradeNo);
			UOrder uorder = orderManager.getOrder(orderID);

			int resultCode = 1;
			String resultMsg = "成功";

			if(uorder == null || uorder.getChannel() == null){
				Log.d("The order is null or the channel is null.");
				return;
			}

			if(uorder.getState() == PayState.STATE_COMPLETE){
				Log.d("The state of the order is complete. The state is "+uorder.getState());
				this.renderState(uorder.getChannel(), resultCode);
				return;
			}


			String AppSecret=uorder.getChannel().getCpAppSecret();
			String publicKey=uorder.getChannel().getCpPayKey();

			
			
			boolean isgo=verifySign(params, AppSecret, publicKey);
			
			//签名验证
			if(!isgo){
				resultCode= 3; //sign无效
				resultMsg="Sign无效";
			}

			if(resultCode == 1){
				uorder.setRealMoney(totalFee);
				uorder.setSdkOrderTime(String.valueOf(new Date().getTime()));
				uorder.setCompleteTime(new Date());
				uorder.setChannelOrderID(tradeNo);
				uorder.setState(PayState.STATE_SUC);

				orderManager.saveOrder(uorder);

				SendAgent.sendCallbackToServer(this.orderManager, uorder);

			}else{
				uorder.setChannelOrderID(tradeNo);
				uorder.setState(PayState.STATE_FAILED);
				orderManager.saveOrder(uorder);
			}

			renderState(uorder.getChannel(), resultCode);

		}catch (Exception e){
			e.printStackTrace();
		}

	}

	private void renderState(UChannel channel, int resultCode) throws IOException {

		PrintWriter out = this.response.getWriter();

		if(resultCode==1){
			out.write("SUCCESS");
		}else{
			out.write("FAIL");
		}
		
		out.flush();

	}

	
	public UOrderManager getOrderManager() {
		return orderManager;
	}

	public void setOrderManager(UOrderManager orderManager) {
		this.orderManager = orderManager;
	}



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
    /** 
     * 将json格式的字符串解析成Map对象 <li> 
     * json格式：{"name":"admin","retries":"3fff","testname" 
     * :"ddd","testretries":"fffffffff"} 
     */  
    private static HashMap<String, Object> toHashMap(JSONObject js)  
    {  
        HashMap<String, Object> data = new HashMap<String, Object>();  
        // 将json字符串转换成jsonObject  
        Iterator it = js.keys();  
        // 遍历jsonObject数据，添加到Map对象  
        while (it.hasNext())  
        {  
            String key = String.valueOf(it.next());  
            Object value = js.get(key);  
            data.put(key, value);  
        }  
        return data;  
    }  

    
    public static boolean verifySign(Map<String, Object> params, String secret, String publicKey) throws Exception {
        String stringToSigned = paramsToString(params, secret);
        String signType = (String)params.get("signType");
        String sign = (String)params.get("sign");

        boolean res = false;
        if (Constants.SIGN_TYPE_MD5.equals(signType)) {
        	
//            res = StringUtils.equals(sign, signWithMD5(stringToSigned));
        	
        	String newsign=EncryptUtils.md5(stringToSigned);
            //签名验证
            if(newsign.equals(sign)){
            	res=true;
            }
        } else if (Constants.SIGN_TYPE_RSA.equals(signType)) {
        	
//            res = verifyWithRSA(stringToSigned, sign, publicKey);
        	res =verifyWithRSA(stringToSigned, sign, publicKey);
        }

        return res;
    }

    public static final String SIGN = "sign";
    public static final String SIGN_TYPE = "signType";
    public static final String KEY_ALGORITHM = "RSA";
    public static final String SIGNATURE_ALGORITHM_MD5 = "MD5withRSA";
    private static String paramsToString(Map<String, Object> params, String secret) {
    	
      
		String[] keys = params.keySet().toArray(new String[0]);
		Arrays.sort(keys);
		StringBuilder query = new StringBuilder();
		for (String key : keys) {
            if (!SIGN.equals(key) && !SIGN_TYPE.equals(key)) {
                Object value = params.get(key);
                String strValue = StringUtils.objectToString(value);
		        if (!(strValue == null || (strValue.length()) == 0)) {
                    query.append(key).append("=").append(value).append("&");
                }
            }
		}
		query.append("appsecret=").append(secret);

        return query.toString();
    }
    
    
    public static boolean verifyWithRSA(String content, String sign, String publicKey) throws Exception {
		try {
            InputStream ins = new ByteArrayInputStream(publicKey.getBytes());
		    KeyFactory keyFactory = KeyFactory.getInstance(Constants.SIGN_TYPE_RSA);
		    byte[] encodedKey = StreamUtils.readText(ins).getBytes();
		    encodedKey = Base64.decode(encodedKey);
		    PublicKey pubKey = keyFactory.generatePublic(new X509EncodedKeySpec(encodedKey));
			Signature signature = Signature.getInstance(Constants.SIGN_ALGORITHMS);
			signature.initVerify(pubKey);
			signature.update(content.getBytes(Constants.CHARSET_UTF8));
            return signature.verify(Base64.decode(sign));
		} catch (Exception e) {
			throw e;
		}
	}

//    
//    /**
//     * 使用公钥对RSA签名有效性进行检查
//     * @param content 待签名数据
//     * @param sign 签名值
//     * @param publicKey  爱贝公钥
//     * @param input_charset 编码格式
//     * @return 布尔值
//     */
//    public static boolean verify(String content, String sign, String publicKey)
//    {
//        try
//        {
//            KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
//            byte[] encodedKey = Base64.decode2Bytes(publicKey);
//            PublicKey pubKey = keyFactory.generatePublic(new X509EncodedKeySpec(encodedKey));
//
//
//            java.security.Signature signature = java.security.Signature
//                    .getInstance(SIGNATURE_ALGORITHM_MD5);
//
//            signature.initVerify(pubKey);
//            signature.update( content.getBytes("UTF-8"));
//
//            return signature.verify(Base64.decode(sign));
//
//        }
//        catch (Exception e)
//        {
//            e.printStackTrace();
//        }
//
//        return false;
//    }
//    
}
