package com.u8.server.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.u8.server.dao.UInvokeLogDao;
import com.u8.server.data.UInvokeLog;

@Service("invokeLogManager")
public class UInvokeLogManager {
	@Autowired
	private UInvokeLogDao uInvokeLogDao;
	public UInvokeLog findByImei(String imei){
		return uInvokeLogDao.findByImei(imei);
	}
	public void saveLog(UInvokeLog log){
		uInvokeLogDao.saveInvokeLog(log);
	}
}
