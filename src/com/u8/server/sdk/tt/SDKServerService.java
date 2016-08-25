package com.u8.server.sdk.tt;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONObject;



public class SDKServerService {
	private static Logger log = LoggerFactory.getLogger(SDKServerService.class);

	/*----登录验证------------------*/
	public static String verifySession(UsersInfo usersInfo) throws SDKException, IOException {
		String httpStr = null;
		
		/**************************** 组合报文 *****************************/
		Map<String, Object> urldata = new HashMap<String, Object>();		
		urldata.put("gameId", usersInfo.getGameId());
		urldata.put("uid", usersInfo.getUserId());
		String jsonBody = JSONObject.toJSONString(urldata);

		/**************************** 使用密钥进行签名 **********************/
		
		String sign = SignUtils.sign(jsonBody, usersInfo.getApikey());


		/**************************** 组合headers *****************************/
		Map<String, Object> header = new HashMap<String, Object>();
		header.put("sid", usersInfo.getSid());
		header.put("sign", sign);

		httpStr = HttpUtil.doPost(usersInfo.getUrl(), jsonBody, header);

		return httpStr;
	}

	/*----充值回调验证------------------*/
	public static String verifyNotify(String ttsign, String urldata,String appSecret) throws SDKException, IOException {
		String httpStr = null;
		Map<String, Object> head = new HashMap<String, Object>();
		Map<String, Object> map = new HashMap<String, Object>();
		
		/**************************** 使用密钥进行签名 **********************/
		
		String sign = SignUtils.sign(urldata, appSecret);

		log.debug("服务器sign=" + ttsign);
		log.debug("本地签名=" + sign);

		/**************************** 验证签名 *****************************/
		if (sign.equals(ttsign)) {
			/*----------- 将报文解析到model,再进行自定义操作-------------------
			PayCallback payModel = JSON.parseObject(urldata, PayCallback.class);
			------*/

			/********** 验签成功自定义代码 *****************/
			map.put("result", "0");
			map.put("message", "验签成功");
			head.put("head", map);
			httpStr = JSONObject.toJSONString(head);
		} else {
			/********** 验签失败自定义代码 *****************/
			map.put("result", "-1");
			map.put("message", "验签失败");
			head.put("head", map);
			httpStr = JSONObject.toJSONString(head);
		}

		return httpStr;
	}

}
