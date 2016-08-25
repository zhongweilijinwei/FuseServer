package com.u8.server.sdk.tt;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import org.apache.commons.codec.binary.Base64;

/**
 * 签名工具类
 * 
 * @author TT
 *
 */

public class SignUtils {

	public static String sign(String data, String key) throws SDKException {
		if (data == null) {
			throw new SDKException("源串为null");
		}

		if (key == null) {
			throw new SDKException("key为null");
		}
		String sign;
		try {
			sign = encodeBASE64(digestMD5((data + key).getBytes("UTF-8")));
			return sign;
		} catch (Exception e) {
			throw new SDKException("签名异常");
		}
	}

	public static String encodeBASE64(byte[] key) {
		return Base64.encodeBase64String(key);
	}

	public static byte[] digestMD5(byte[] data) throws NoSuchAlgorithmException {
		MessageDigest md5 = MessageDigest.getInstance("MD5");
		md5.update(data);
		return md5.digest();
	}

}
