package com.u8.server.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.u8.server.dao.ULaunchLogDao;
import com.u8.server.data.UInvokeLog;
import com.u8.server.data.ULaunchLog;

@Service("ULaunchLogManager")
public class ULaunchLogManager {
	@Autowired
	private ULaunchLogDao uLaunchLogDao;
	public ULaunchLog findByImei(String imei){
		return uLaunchLogDao.findByImei(imei);
	}
	public void saveLog(ULaunchLog log){
		uLaunchLogDao.saveLaunchLog(log);
	}
}
