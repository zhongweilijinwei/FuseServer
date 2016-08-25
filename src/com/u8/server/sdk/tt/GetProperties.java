package com.u8.server.sdk.tt;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class GetProperties {
	/**
	 * 获取配置信息
	 */
	public String getProperties(String sign) {
		InputStream in = getClass().getResourceAsStream("/config.properties");
		Properties prop = new Properties();
		String properties = "";
		try {
			prop.load(in);
			properties = prop.getProperty(sign).toString();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (null != in) {
					in.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return properties;
	}

}