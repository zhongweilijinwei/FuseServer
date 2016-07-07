package com.u8.server.web.admin;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.opensymphony.xwork2.ModelDriven;
import com.u8.server.common.Page;
import com.u8.server.common.UActionSupport;
import com.u8.server.data.UChannelMaster;
import com.u8.server.data.UUser;
import com.u8.server.log.Log;
import com.u8.server.service.UUserManager;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

/**
 * 用户管理
 * Created by ant on 2015/8/28.
 */
@Controller
@Namespace("/admin/users")
public class UserManAction extends UActionSupport implements ModelDriven<UUser>{


	private static java.text.SimpleDateFormat DATE_FORMAT=new SimpleDateFormat("yyyy-MM-dd hh:mm");
    private int page;           //当前请求的页码
    private int rows;           //当前每页显示的行数

    private UUser user;

    private int currUserID;

    private int appID;          //当前游戏ID
    






	@Autowired
    private UUserManager userManager;

    @Action(value = "showUsers",
            results = {@Result(name = "success", location = "/WEB-INF/admin/users.jsp")})
    public String showUsers(){

        return "success";
    }

    @Action(value = "showUserAnalytics",
            results = {@Result(name = "success", location = "/WEB-INF/admin/user_analytics.jsp")})
    public String showUserAnalytics(){

        return "success";
    }

    @Action("search")
    public void search(){
    	  try{
    		  
    		  
    		  String startDate=request.getParameter("startDate");
    		  String endDate=request.getParameter("endDate");
    		  String channelUserName=request.getParameter("channelUserName");
    		  String channelID=request.getParameter("channelID");
    		  
    		  Date start=null;
    		  Date end=null;
    		  if(null!=startDate &&!"".equals(startDate.trim())){
    			  start=DATE_FORMAT.parse(startDate);
    		  }
    		  if(null!=endDate && !"".equals(endDate)){
    			  end=DATE_FORMAT.parse(endDate.trim());
    		  }
    		  
    		  int channelIntID=-1;
    		  
    		  try{
    			  channelIntID=Integer.parseInt(channelID);
    		  }catch(Exception e){
    			  channelIntID=-1;
    		  }
    		 // String c=request.getParameter("channelID");
    		  int count=userManager.getUserCount(start, end,channelIntID,channelUserName);
    		  
    		  List<UUser> uList = userManager.search(start, end,channelIntID,channelUserName,this.page,this.rows);
    		  JSONObject json = new JSONObject();
              json.put("total", count);
              JSONArray users = new JSONArray();
              if(null!=uList){
            	  for(UUser user:uList){
            		  users.add(user.toJSON());
            	  }
            	  
              }
              json.put("rows", users);

              renderJson(json.toString());
              
    	   }catch(Exception e){
              e.printStackTrace();
          }
    	
    }



	@Action("getAllUsers")
    public void getAllUsers(){
        try{

            long count = this.userManager.getUserCount();
            Page<UUser> currPage = this.userManager.queryPage(page, rows);

            JSONObject json = new JSONObject();
            json.put("total", count);
            JSONArray users = new JSONArray();
            for(UUser m : currPage.getResultList()){
                users.add(m.toJSON());
            }
            json.put("rows", users);

            renderJson(json.toString());

        }catch(Exception e){
            e.printStackTrace();
        }
    }

    @Action("removeUser")
    public void removeUser(){
        try{
            Log.d("Curr userID is " + this.currUserID);

            UUser user = userManager.getUser(this.currUserID);

            if(user == null){
                renderState(false);
                return;
            }

            userManager.deleteUser(user);

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
    public UUser getModel() {
        if(this.user == null){
            this.user = new UUser();
        }
        return this.user;
    }

    public UUser getUser() {
        return user;
    }

    public void setUser(UUser user) {
        this.user = user;
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

    public int getCurrUserID() {
        return currUserID;
    }

    public void setCurrUserID(int currUserID) {
        this.currUserID = currUserID;
    }

    public int getAppID() {
        return appID;
    }

    public void setAppID(int appID) {
        this.appID = appID;
    }
   
}
