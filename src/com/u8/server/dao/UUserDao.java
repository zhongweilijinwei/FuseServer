package com.u8.server.dao;

import java.util.Date;
import java.util.List;

import javax.transaction.Transaction;

import com.u8.server.common.UHibernateTemplate;
import com.u8.server.data.UUser;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

/**
 * 用户数据访问类
 */
@Repository("userDao")
public class UUserDao extends UHibernateTemplate<UUser, Integer> {

	public int getUserCount(Date startDate, Date endDate,int channelID,String channelUserName){
		Session session = this.getSession();
		Criteria criteria=this.createCriteria(startDate, endDate,channelID,channelUserName, session);
	//	Transaction t=	session.beginTransaction();
		Object o=	criteria.setProjection(Projections.rowCount()).uniqueResult();
	//	t.commit();
		if(null!=o){
			int totalRow = Integer.parseInt(o.toString());
			return totalRow;
		}
		return 0;
		
	}
	
	public List<UUser> search(Date startDate, Date endDate,int channelID,String channelUserName,int page,int rows) {
		//String hql = "";
		Session session = this.getSession();

		Criteria criteria=this.createCriteria(startDate, endDate,channelID,channelUserName, session);
		criteria.setFirstResult(Math.max(0, (page-1)*rows));
		criteria.setMaxResults(rows);
		criteria.addOrder(Order.desc("createTime"));
		return criteria.list();
	}

	private Criteria createCriteria(Date startDate, Date endDate,int channelID,String channelUserName,Session session){
		
		Criteria criteria = session.createCriteria(UUser.class);
		// 查询制定时间之后的记录
		if (null != startDate) {
			criteria.add(Restrictions.ge("createTime", startDate));
		}

		if (endDate != null) {
			criteria.add(Restrictions.le("createTime", endDate));
		} // 查询指定时间之前的记录
		
		
		if(channelID>-1){
			criteria.add(Restrictions.eq("channelID", channelID));
		}
		
		if(null!=channelUserName &&!"".equals(channelUserName)){
			criteria.add(Restrictions.eq("channelUserName", channelUserName));
			
		}
		//if(masterID>-1){
		//	criteria.add(Restrictions.le("createTime", endDate));
		//}
		
		return criteria;
		
	}
}
