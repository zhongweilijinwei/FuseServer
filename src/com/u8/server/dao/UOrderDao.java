package com.u8.server.dao;

import java.util.Date;
import java.util.List;

import com.u8.server.common.UHibernateTemplate;
import com.u8.server.data.UOrder;
import com.u8.server.data.UUser;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

/**
 * 订单数据访问类
 */
@Repository("orderDao")
public class UOrderDao extends UHibernateTemplate<UOrder, Long>{

	public int getOrderCount(Date startDate, Date endDate,int channelID,long orderID){
		Session session = this.getSession();
		Criteria criteria=this.createCriteria(startDate, endDate,channelID,orderID, session);
	//	Transaction t=	session.beginTransaction();
		Object o=	criteria.setProjection(Projections.rowCount()).uniqueResult();
	//	t.commit();
		if(null!=o){
			int totalRow = Integer.parseInt(o.toString());
			return totalRow;
		}
		return 0;
		
	}
	
	public List<UOrder> search(Date startDate, Date endDate,int channelID,long orderID,int page,int rows) {
		//String hql = "";
		Session session = this.getSession();

		Criteria criteria=this.createCriteria(startDate, endDate,channelID,orderID, session);
		criteria.setFirstResult(Math.max(0, (page-1)*rows));
		criteria.setMaxResults(rows);
		criteria.addOrder(Order.desc("createdTime"));
		return criteria.list();
	}
	
	public List<UOrder> searchAll(Date startDate, Date endDate,int channelID,long orderID) {
		Session session = this.getSession();

		Criteria criteria=this.createCriteria(startDate, endDate,channelID,orderID, session);
		criteria.addOrder(Order.desc("createdTime"));
		return criteria.list();
	}

	private Criteria createCriteria(Date startDate, Date endDate,int channelID,long orderID,Session session){
		
		Criteria criteria = session.createCriteria(UOrder.class);
		// 查询指定时间之后的记录
		if (null != startDate) {
			criteria.add(Restrictions.ge("createdTime", startDate));
		}

		if (endDate != null) {
			criteria.add(Restrictions.le("createdTime", endDate));
		} // 查询指定时间之前的记录
		
		
		if(channelID>-1){
			criteria.add(Restrictions.eq("channelID", channelID));
		}
		
		if(orderID>-1){
			criteria.add(Restrictions.eq("orderID", orderID));
			
		}
		//if(masterID>-1){
		//	criteria.add(Restrictions.le("createTime", endDate));
		//}
		
		return criteria;
		
	}

}
