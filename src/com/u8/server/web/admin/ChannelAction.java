package com.u8.server.web.admin;

import com.opensymphony.xwork2.ModelDriven;
import com.u8.server.common.UActionSupport;
import com.u8.server.data.UChannel;
import com.u8.server.data.UChannelMaster;
import com.u8.server.data.UGame;
import com.u8.server.data.UUser;
import com.u8.server.log.Log;
import com.u8.server.service.UChannelManager;
import com.u8.server.service.UChannelMasterManager;
import com.u8.server.service.UGameManager;
import com.u8.server.service.UUserManager;
import com.u8.server.utils.IDGenerator;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * 渠道管理
 * Created by ant on 2015/8/22.
 */
@Controller
@Namespace("/admin/channels")
public class ChannelAction extends UActionSupport implements ModelDriven<UChannel>{

    private int page;           //当前请求的页码
    private int rows;           //当前每页显示的行数

    private UChannel channel;

    private int currChannelID;

    @Autowired
    private UChannelManager channelManager;

    @Autowired
    private UUserManager userManager;
    @Autowired
    private UChannelMasterManager channelMasterManager;
    /**
     * 游戏编号
     */
    private int gameId;

 

	@Action(value = "channelManage", results = {@Result(name = "success", location = "/WEB-INF/admin/channels.jsp")})
    public String channelManage(){

        return "success";
    }

	/**
	 * 根据游戏编号返回
	 */
	 @Action("getMasterByGame")
    public void getMasterByGame(){
    	//首先根据游戏查找渠道
		 List<UChannel> uList=	 channelManager.getChannelList(gameId,currChannelID);
		   List<UChannelMaster> masters = null;
		 if(null==uList || uList.isEmpty()){
			 //如果该游戏没有 建立渠道 返回所有的
			 masters = this.channelMasterManager.queryAll();
			 
		 }else{
			 //如果已经建立了 那么判断是否已经有 指定的渠道商
			 
			 boolean has=false;
			 for(UChannel uChannel:uList){
				 if(uChannel.getMasterID()==Config.getMustMasterID()){
					 has=true;
					 break;
				 }
			 }
			 
			 if(has){
				//如果有 那么返回所有的
				 masters = this.channelMasterManager.queryAll();
			 }else{
				 //如果没有 那么只返回指定的
				 masters =new ArrayList<UChannelMaster>();
				 masters.add(channelMasterManager.getMustMaster());
			 }
			 
		 }
		 
		    try{

	          

	            JSONArray masterArray = new JSONArray();
	            for(UChannelMaster m : masters){
	                JSONObject item = new JSONObject();
	                item.put("masterID", m.getMasterID());
	                item.put("masterName", m.getMasterName());
	                masterArray.add(item);
	            }

	            renderJson(masterArray.toString());

	        }catch(Exception e){
	            e.printStackTrace();
	        }
    }
    
		@Action("getAllChannelsSimple")
		public void getAllChannelsSimple() {
			try {
			     int count = this.channelManager.getChannelCount();

		         List<UChannel> lst = this.channelManager.queryPage(1, count);

			
			
				JSONArray channelArray = new JSONArray();
				 JSONObject allItem = new JSONObject();
				 allItem.put("channelID","");
				 allItem.put("name", "全部");
			     channelArray.add(allItem);
				 
				if (null != lst) {
					for (UChannel m : lst) {
						 JSONObject item = new JSONObject();
			                item.put("channelID", m.getChannelID());
			                
			                UChannelMaster master=m.getMaster();
			                item.put("name",m.getChannelID()+" [ "+master.getMasterName()+" ]");
			                
			                item.put("masterName", master.getMasterName());
			                
			                item.put("masterID", master.getMasterID());
			                channelArray.add(item);
					}
				
				}

				 renderJson(channelArray.toString());

			} catch (Exception e) {
				e.printStackTrace();
			}

		}
    @Action("getAllChannels")
         public void getAllChannels(){
        try{

            int count = this.channelManager.getChannelCount();

            List<UChannel> lst = this.channelManager.queryPage(page, rows);


            JSONObject json = new JSONObject();
            json.put("total", count);
            JSONArray masterArray = new JSONArray();
            for(UChannel m : lst){
                masterArray.add(m.toJSON());
            }
            json.put("rows", masterArray);


            renderJson(json.toString());


        }catch(Exception e){
            e.printStackTrace();
        }
    }

    @Action("recommendChannelID")
    public void recommendChannelID(){
        try{

            int channelID = channelManager.getValidChannelID();

            JSONObject json = new JSONObject();
            json.put("state", 1);
            json.put("data", channelID);
            renderJson(json.toString());

        }catch (Exception e){
            renderState(false);
            e.printStackTrace();
        }
    }

    //添加或者编辑
    @Action("addChannel")
    public void addChannel(){

        try{
            Log.d("add.channel.info." + this.channel.toJSON().toString());

            UChannel exists = channelManager.queryChannel(this.channel.getChannelID());
            if(exists != null){
                renderState(false, "操作失败,当前渠道号已经存在");
                return;
            }

            channelManager.saveChannel(this.channel);
            renderState(true);

            return;

        }catch(Exception e){
            e.printStackTrace();
        }

        renderState(false);
    }

    //添加或者编辑
    @Action("saveChannel")
    public void saveChannel(){

        try{
            Log.d("save.channel.info." + this.channel.toJSON().toString());
            channelManager.saveChannel(this.channel);
            renderState(true);

            return;

        }catch(Exception e){
            e.printStackTrace();
        }

        renderState(false);
    }

    @Action("removeChannel")
    public void removeChannel(){
        try{

            Log.d("Curr channelID is "+this.currChannelID);
            UChannel c = this.channelManager.queryChannel(this.currChannelID);
            if(c == null){
                renderState(false);
                return;
            }

            List<UUser> lst = this.userManager.getUsersByChannel(this.currChannelID);
            if(lst.size() > 0){
                renderState(false, "请先删除该渠道下面的所有用户数据");
                return;
            }

            this.channelManager.deleteChannel(c);

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

    private void renderState(boolean suc, String msg){
        JSONObject json = new JSONObject();
        json.put("state", suc? 1 : 0);
        json.put("msg", msg);
        renderText(json.toString());
    }


    @Override
    public UChannel getModel() {

        if(this.channel == null){
            this.channel = new UChannel();
        }

        return this.channel;
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

    public UChannel getChannel() {
        return channel;
    }

    public void setChannel(UChannel channel) {
        this.channel = channel;
    }

    public int getCurrChannelID() {
        return currChannelID;
    }

    public void setCurrChannelID(int currChannelID) {
        this.currChannelID = currChannelID;
    }
    public int getGameId() {
 		return gameId;
 	}

 	public void setGameId(int gameId) {
 		this.gameId = gameId;
 	}
}
