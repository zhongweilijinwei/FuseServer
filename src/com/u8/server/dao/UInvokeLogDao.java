package com.u8.server.dao;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.u8.server.common.UHibernateTemplate;
import com.u8.server.data.UInvokeLog;

@Repository("uInvokeLogDao")
public class UInvokeLogDao extends UHibernateTemplate<UInvokeLog,Integer>{

	
	public UInvokeLog findByImei(String imei){
		
		String hql="from UInvokeLog where imei  = ?";
		
		List<UInvokeLog>  ls=find(hql, new Object[] { imei }, null);
		
		if(null!=ls && !ls.isEmpty()){
			return ls.get(0);
		}
		return null;
	}
	
	public void saveInvokeLog(UInvokeLog log){
		super.save(log);
	}
}
