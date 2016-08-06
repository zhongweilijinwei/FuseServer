package com.u8.server.dao;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.u8.server.common.UHibernateTemplate;
import com.u8.server.data.UGameRole;
import com.u8.server.data.ULaunchLog;

@Repository("UGameRoleDao")
public class UGameRoleDao extends UHibernateTemplate<UGameRole,Integer>{

	
	public UGameRole findById(int id){
		
		String hql="from UGameRole where id  = ?";
		
		List<UGameRole>  ls=find(hql, new Object[] { id }, null);
		
		if(null!=ls && !ls.isEmpty()){
			return ls.get(0);
		}
		return null;
	}
	
	public void saveGameRole(UGameRole log){
		super.save(log);
	}
}
