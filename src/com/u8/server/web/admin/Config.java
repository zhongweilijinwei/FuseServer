package com.u8.server.web.admin;

import java.io.IOException;
import java.util.Properties;

public class Config {

	/**
	 * 必须的渠道编号
	 */
	private  static int mustMasterID=-1;
	

	/**
	 * 必须的渠道商
	 * @return
	 */
	
	public static int getMustMasterID() {
		return mustMasterID;
	}



	/**
	 * 初始化配置文件
	 */
	public static void init(){
		java.util.Properties pro=new Properties();
		try {
			pro.load(Config.class.getClassLoader().getResourceAsStream("config.properties"));
			mustMasterID=Integer.parseInt(pro.getProperty("must-masterid-id"));
			
		} catch (IOException e) {
			mustMasterID=-1;
			e.printStackTrace();
		}
	}


	
}
