package com.u8.server.sdk.shendeng;


import java.io.UnsupportedEncodingException;
import java.util.*;

/**
 * Created by jihongtang on 5/9/16.
 */
public class Sign {
    /**
     * 将所有请求参数封装成 Map<String,String>()
     * @param requestParams
     * @return
     */
    public static Map<String,String> processData(Map<String,String[]> requestParams){
        Map<String,String> params = new HashMap<String,String>();
        for (Iterator iter = requestParams.keySet().iterator(); iter.hasNext();) {
            String name = (String) iter.next();
            String[] values = (String[]) requestParams.get(name);
            String valueStr = "";
            for (int i = 0; i < values.length; i++) {
                valueStr = (i == values.length - 1) ? valueStr + values[i]
                        : valueStr + values[i] + ",";
            }
            params.put(name, valueStr);
        }
        return params;
    }

    /**
     * 除去签名参数
     * @param sArray 签名参数组
     * @return 去掉签名参数后的新签名参数组
     */
    public static Map<String, String> paraFilter(Map<String, String> sArray) {

        Map<String, String> result = new HashMap<String, String>();

        if (sArray == null || sArray.size() <= 0) {
            return result;
        }

        for (String key : sArray.keySet()) {
            String value = sArray.get(key);
            if (key.equalsIgnoreCase("sign")) {
                continue;
            }
            result.put(key, value);
        }
        return result;
    }





    //按照ASCII码排序后 前后加secret 拼装字符串
    public static String assembly(Map<String,String> params,String secret){
        StringBuffer sb = new StringBuffer();

        sb.append(secret);
        //按照key的ascii码排序
        List<String> keys = new ArrayList<String>(params.keySet());
        Collections.sort(keys);

        for (String key : keys) {
            sb.append(key + params.get(key));
        }

        sb.append(secret);
        return sb.toString();
    }

    /**
     * 加密指定字符串
     * @param preStr 准备加密的字符串
     * @return 加密后字符串
     */
    public static String preSign(String preStr){
    	String bytestring = null;
		try {
			bytestring = Byte2Hex.byte2hex(preStr.getBytes("utf-8"));
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        return MD5.MD5(bytestring);
    }
//    /**
//     * 将所有参数对所有API请求参数（包括公共参数和业务参数，但除去sign参数），根据参数名称的ASCII码表的顺序排序
//     * 除去sign并按ASCII码进行排序（除去’＝’，’&’）并在字符串后添加secret
//     * 将字符串采用utf-8编码进行十六进制摘要
//     * 最后将字符串进行MD5加密
//     * @param requestParams 待签名集合
//     * @return 签名
//     */
//    public static String sign(Map<String,String[]> requestParams){
//        return preSign(         //加密
//                assembly(           //排序
//                   paraFilter(         //过滤sign参数
//                      processData(         //处理request参数封装Map
//                         requestParams
//                                ))));
//    }
//
//    /**
//     * 把数组所有元素排序，并按照“参数=参数值”的模式用“&”字符拼接成字符串
//     * @param params 需要排序并参与字符拼接的参数组
//     * @return 拼接后字符串
//     */
//    public static String paraCreateLinkString(Map<String, String> params) {
//        String prestr = "";
//        int i = 0;
//        for (String s : params.keySet()) {
//            String key = s;
//            String value = params.get(key);
//            if (i == params.size() - 1) {//拼接时，不包括最后一个&字符
//                prestr = prestr + key + "=" + value;
//            } else {
//                prestr = prestr + key + "=" + value + "&";
//            }
//            i++;
//        }
//
//        return prestr;
//    }
//    public static String createLinkString(Map<String, String[]> requestParams){
//        String sign = sign(requestParams);
//        String linkString = paraCreateLinkString(paraFilter(processData(requestParams)));
//        String url = Config.callBackUrl + "?" + linkString + "&sign=" + sign;
//        return url;
//    }
}
