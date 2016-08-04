package com.u8.server.dao;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.u8.server.common.UHibernateTemplate;
import com.u8.server.data.ULaunchLog;

@Repository("ULaunchLogDao")
public class ULaunchLogDao extends UHibernateTemplate<ULaunchLog,Integer>{

	
	public ULaunchLog findByImei(String imei){
		
		String hql="from ULaunchLog where imei  = ?";
		
		List<ULaunchLog>  ls=find(hql, new Object[] { imei }, null);
		
		if(null!=ls && !ls.isEmpty()){
			return ls.get(0);
		}
		return null;
	}
	
	public void saveInvokeLog(ULaunchLog log){
		super.save(log);
	}
}
