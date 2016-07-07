package com.u8.server.web;

import java.io.UnsupportedEncodingException;
import java.util.List;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.opensymphony.xwork2.ModelDriven;
import com.u8.server.common.UActionSupport;
import com.u8.server.data.UGameChannel;
import com.u8.server.data.UInvokeLog;
import com.u8.server.service.UGameChannelManager;
import com.u8.server.service.UInvokeLogManager;

/**
 * 接口api
 * @author Administrator
 *
 */
@Controller
@Namespace("/uapi/gamechannels")
public class GameChannelApiAction  extends UActionSupport implements
ModelDriven<UGameChannel> {
	@Autowired
	private UGameChannelManager gameChannelManager;
	@Autowired
	private UInvokeLogManager invokeLogManager;
	private UGameChannel gameChannel;
	
	private int gameId;
	
	private int ugCode;







	/**
	 * 错误状态
	 */
	private static final int ERROR_STATE=201;
	/**
	 * 没有找到
	 */
	private static final int NOTFOUND_STATE=404;
	/**
	 * 成功状态
	 */
	private static final int SUC_STATE=200;
	

	/**
	 * 记录日志
	 */
	private void log(){
		
		try{
			String uchannel=this.request.getParameter("u_channel");
			String uappkey=this.request.getParameter("u_appkey");
			String uappid=this.request.getParameter("u_appid");
			
			String iccid=this.request.getParameter("iccid");
			
			String imei=this.request.getParameter("imei");
			String imsi=this.request.getParameter("imsi");
			String sdkver=this.request.getParameter("sdkver");
			String model=this.request.getParameter("model");
			String plugins=this.request.getParameter("plugins");
			String name=this.request.getParameter("name");
			String type=this.request.getParameter("type");
			String version=this.request.getParameter("version");
			
			UInvokeLog log=new UInvokeLog();
			
			log.setIccid(iccid);
			log.setImei(imei);
			log.setImsi(imsi);
			log.setIp(request.getRemoteAddr());
			log.setModel(model);
			log.setName(name);
			log.setPlugins(plugins);
			log.setSdkver(sdkver);
			log.setType(type);
			log.setUappid(uappid);
			log.setUappkey(uappkey);
			log.setUchannel(uchannel);
			log.setVersion(version);
			invokeLogManager.saveLog(log);
			
		}catch(Exception e){
			
			System.out.println("日志记录错误.....");
			e.printStackTrace();
			
		}
		
	
	}
	
	
	@Action("getGameChannelByCode")
	public void getGameChannelByCode(){
		int state=0;
		String message=null;
		JSONObject content=null;
		
		try{
			UGameChannel uc=gameChannelManager.queryGameChannelByCode(ugCode);
			
			if(null!=uc){
				content = uc.toApiJSON();
			
				state=SUC_STATE;
			}else{
				state=NOTFOUND_STATE;
			}
			
		}catch(Exception e){
			state=ERROR_STATE;
			message=e.getLocalizedMessage();
			e.printStackTrace();
		}
		renderState(state,message,content);
		
		log();
	}
	
	/**
	 * 根据游戏编号获取
	 */
	@Action("getGameChannelByGame")
	public void getGameChannelByGame(){
		
		JSONArray content=null;
		int state=0;
		String message=null;
		try{
			List<UGameChannel> uList=gameChannelManager.queryGameChannelByGame(gameId);
			
			if(null!=uList&& uList.size()>0){
				content = new JSONArray();
				for(UGameChannel uC:uList){
					content.add(uC.toApiJSON());
				}
				state=SUC_STATE;
			}else{
				state=NOTFOUND_STATE;
			}
			
		}catch(Exception e){
			state=ERROR_STATE;
			message=e.getLocalizedMessage();
			e.printStackTrace();
		}
		renderState(state,message,content);
		log();
		
	}
    private void renderState(int state,String message,Object content){
        JSONObject json = new JSONObject();
        json.put("state", state);
      
        json.put("msg", null==message? "" : encode(message)  );
        json.put("content", null==content?"":content.toString());
        renderText(json.toString());
    }
    
    private String encode(String message){
    	try {
			return java.net.URLEncoder.encode(message, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	return message;
    }
	@Override
	public UGameChannel getModel() {
		if(null==this.gameChannel){
			this.gameChannel=new UGameChannel();
		}
		return gameChannel;
	}
	   public int getGameId() {
		return gameId;
	}

	public void setGameId(int gameId) {
		this.gameId = gameId;
	}
	public int getUgCode() {
		return ugCode;
	}

	public void setUgCode(int ugCode) {
		this.ugCode = ugCode;
	}
}
