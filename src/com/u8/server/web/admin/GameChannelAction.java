package com.u8.server.web.admin;

import java.util.List;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.opensymphony.xwork2.ModelDriven;
import com.u8.server.common.UActionSupport;
import com.u8.server.data.UChannel;
import com.u8.server.data.UChannelMaster;
import com.u8.server.data.UGameChannel;
import com.u8.server.data.UUser;
import com.u8.server.log.Log;
import com.u8.server.service.UGameChannelManager;

/**
 * 游戏渠道管理
 * 
 * @author Administrator
 * 
 */
@Controller
@Namespace("/admin/gamechannels")
public class GameChannelAction extends UActionSupport implements
		ModelDriven<UGameChannel> {
	@Autowired
	private UGameChannelManager gameChannelManager;
	private int page; // 当前请求的页码
	private int rows; // 当前每页显示的行数
	private UGameChannel gameChannel;
	private int currGameChannelID;
	
	/**
	 * 游戏编号
	 */
	private int gameId;

	@Action(value = "gameChannelManage", results = { @Result(name = "success", location = "/WEB-INF/admin/gamechannels.jsp") })
	public String ganemChannelManage() {

		
		this.request.setAttribute("mustMasterID", Config.getMustMasterID());
		return "success";
	}
	
	
    @Action("getAllGameChannels")
    public void getAllGameChannels(){
   try{

       int count = this.gameChannelManager.getGameChannelCount();

       List<UGameChannel> lst = this.gameChannelManager.queryPage(page, rows);


       JSONObject json = new JSONObject();
       json.put("total", count);
       if(null!=lst){
    	   JSONArray masterArray = new JSONArray();
           
           for(UGameChannel m : lst){
               masterArray.add(m.toListJSON());
           }
           json.put("rows", masterArray);
       }
      


       renderJson(json.toString());


   }catch(Exception e){
       e.printStackTrace();
   }
}
	
	
	/**
	 * 获取游戏渠道
	 */

	@Action("getChannelByGame")
	public void getChannelByGame() {
		try {
			List<UChannel> lst = gameChannelManager.getUChannelList(gameId);

		
		
			JSONArray channelArray = new JSONArray();

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
    //添加或者编辑
    @Action("addGameChannel")
    public void addChannel(){

        try{
            Log.d("add.channel.info." + this.gameChannel.toJSON().toString());

            UGameChannel exists = gameChannelManager.queryGameChannelByCode(this.gameChannel.getCode());
            if(exists != null){
                renderState(false, "操作失败,当前游戏渠道号已经存在");
                return;
            }

            gameChannelManager.saveUGameChannel(this.gameChannel);
            renderState(true);

            return;

        }catch(Exception e){
            e.printStackTrace();
        }

        renderState(false);
    }
    //添加或者编辑
    @Action("saveGameChannel")
    public void saveChannel(){

        try{
            Log.d("save.channel.info." + this.gameChannel.toJSON().toString());
            gameChannelManager.saveUGameChannel(gameChannel);
            renderState(true);

            return;

        }catch(Exception e){
            e.printStackTrace();
        }

        renderState(false);
    }
	/**
	 * 删除游戏渠道
	 */
	 @Action("removeGameChannel")
	    public void removeChannel(){
	        try{

	            Log.d("Curr game channelID is "+this.currGameChannelID);
	            UGameChannel c = this.gameChannelManager.queryUGameChannel(currGameChannelID);
	            if(c == null){
	                renderState(false);
	                return;
	            }

	        

	            this.gameChannelManager.deleteChannel(c);

	            renderState(true);
	            return;

	        }catch(Exception e){
	            e.printStackTrace();
	        }

	        renderState(false);
	    }
	/**
	 * 获取编号
	 */
    @Action("recommendGameChannelID")
    public void recommendChannelID(){
        try{

            int channelID = gameChannelManager.getValidGameChannelID();

            JSONObject json = new JSONObject();
            json.put("state", 1);
            json.put("data", channelID);
            renderJson(json.toString());

        }catch (Exception e){
            renderState(false);
            e.printStackTrace();
        }
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
	public UGameChannel getModel() {
		if(null==this.gameChannel){
			this.gameChannel=new UGameChannel();
		}
		return gameChannel;
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


	public int getCurrGameChannelID() {
		return currGameChannelID;
	}


	public void setCurrGameChannelID(int currGameChannelID) {
		this.currGameChannelID = currGameChannelID;
	}


	public int getGameId() {
		return gameId;
	}


	public void setGameId(int gameId) {
		this.gameId = gameId;
	}

}
