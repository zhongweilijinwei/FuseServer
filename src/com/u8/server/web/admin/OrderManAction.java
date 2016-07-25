package com.u8.server.web.admin;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.opensymphony.xwork2.ModelDriven;
import com.u8.server.common.Page;
import com.u8.server.common.UActionSupport;
import com.u8.server.data.UOrder;
import com.u8.server.data.UUser;
import com.u8.server.log.Log;
import com.u8.server.service.UOrderManager;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

/**
 * 订单管理
 * Created by ant on 2015/8/29.
 */
@Controller
@Namespace("/admin/orders")
public class OrderManAction extends UActionSupport implements ModelDriven<UOrder>{

	private static java.text.SimpleDateFormat DATE_FORMAT=new SimpleDateFormat("yyyy-MM-dd hh:mm");
    private int page;           //当前请求的页码
    private int rows;           //当前每页显示的行数

    private UOrder order;

    private long currOrderID;

    @Autowired
    private UOrderManager orderManager;

    @Action(value = "showOrders",
            results = {@Result(name = "success", location = "/WEB-INF/admin/orders.jsp")})
    public String showOrders(){

        return "success";
    }

    @Action(value = "showOrderAnalytics",
            results = {@Result(name = "success", location = "/WEB-INF/admin/order_analytics.jsp")})
    public String showOrderAnalytics(){

        return "success";
    }
    @Action("search")
    public void search(){
    	  try{
    		  
    		  
    		  String startDate=request.getParameter("startDate");
    		  String endDate=request.getParameter("endDate");
    		  String orderID=request.getParameter("orderID");
    		  String channelID=request.getParameter("channelID");
    		  String state=request.getParameter("state");
    		  
    		  Date start=null;
    		  Date end=null;
    		  if(null!=startDate &&!"".equals(startDate.trim())){
    			  start=DATE_FORMAT.parse(startDate);
    		  }
    		  if(null!=endDate && !"".equals(endDate)){
    			  end=DATE_FORMAT.parse(endDate.trim());
    		  }
    		  
    		  int channelIntID=-1;
    		  long orderLongID=-1;
    		  
    		  int stateInt = -1;
    		  
    		  try{
    			  channelIntID=Integer.parseInt(channelID);
    		  }catch(Exception e){
    			  channelIntID=-1;
    		  }
    		  try{
    			  orderLongID=Long.parseLong(orderID);
    		  }catch(Exception e){
    			  orderLongID=-1;
    		  }
    		  
    		  try {
				stateInt = Integer.parseInt(state);
			} catch (Exception e) {
				stateInt = -1;
			}
    		 // String c=request.getParameter("channelID");
    		  int count=orderManager.getOrderCount(start, end,channelIntID,orderLongID, stateInt);
    		  
    		  List<UOrder> uList = orderManager.search(start, end,channelIntID,orderLongID,stateInt, this.page,this.rows);

    		  JSONObject json = new JSONObject();
              json.put("total", count);
              JSONArray users = new JSONArray();
              if(null!=uList){
            	  for(UOrder order:uList){
            		  users.add(order.toJSON());
            	  }
              }
              List<UOrder> uListAll = orderManager.searchAll(start, end,channelIntID,orderLongID, stateInt);
              if(null!=uListAll){
            	  int payed = 0;
            	  int unpayed = 0;
            	  for(UOrder order:uListAll){
            		  if(order.getState()==1){
            			  unpayed += order.getMoney();
            		  }
            		  else {
            			  payed += order.getMoney();
            		  }
            	  }
            	  json.put("payed", payed);
            	  json.put("unpayed", unpayed);
              }
              json.put("rows", users);

              renderJson(json.toString());
              
    	   }catch(Exception e){
              e.printStackTrace();
          }
    	
    }



    @Action("getAllOrders")
    public void getAllOrders(){
        try{

            Page<UOrder> currPage = this.orderManager.queryPage(page, rows);

            JSONObject json = new JSONObject();
            json.put("total", currPage.getTotalCount());
            JSONArray orders = new JSONArray();
            for(UOrder m : currPage.getResultList()){
                orders.add(m.toJSON());
            }
            json.put("rows", orders);

            renderJson(json.toString());

        }catch(Exception e){
            e.printStackTrace();
        }
    }

    @Action("removeOrder")
    public void removeOrder(){
        try{
            Log.d("Curr orderID is " + this.currOrderID);

            UOrder order = orderManager.getOrder(this.currOrderID);

            if(order == null){
                renderState(false);
                return;
            }

            orderManager.deleteOrder(order);

            renderState(true);

            return;

        }catch(Exception e){
            e.printStackTrace();
        }

        renderState(false);
    }



    private void renderState(boolean suc){
        JSONObject json = new JSONObject();
        json.put("state", suc? 1 : 0);
        json.put("msg", suc? "操作成功" : "操作失败");
        renderText(json.toString());
    }


    @Override
    public UOrder getModel() {

        if(this.order == null){
            this.order = new UOrder();
        }

        return this.order;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getRows() {
        return rows;
    }

    public void setRows(int rows) {
        this.rows = rows;
    }

    public UOrder getOrder() {
        return order;
    }

    public void setOrder(UOrder order) {
        this.order = order;
    }

    public long getCurrOrderID() {
        return currOrderID;
    }

    public void setCurrOrderID(long currOrderID) {
        this.currOrderID = currOrderID;
    }
}
