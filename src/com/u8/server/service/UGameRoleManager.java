package com.u8.server.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.u8.server.dao.UGameRoleDao;
import com.u8.server.dao.ULaunchLogDao;
import com.u8.server.data.UGameRole;
import com.u8.server.data.UInvokeLog;
import com.u8.server.data.ULaunchLog;

@Service("UGameRoleManager")
public class UGameRoleManager {
	@Autowired
	private UGameRoleDao uGameRoleDao;
	public UGameRole findById(int id){
		return uGameRoleDao.findById(id);
	}
	public void saveLog(UGameRole log){
		uGameRoleDao.saveGameRole(log);
	}
}
