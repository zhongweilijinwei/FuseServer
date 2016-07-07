package com.u8.server.cache;

import com.u8.server.data.UChannel;
import com.u8.server.data.UChannelMaster;
import com.u8.server.data.UGame;
import com.u8.server.data.UGameChannel;
import com.u8.server.log.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/***
 * 将常用的数据进行缓存。包含game,master,channel等对象
 */
public class CacheManager{

    private static CacheManager instance;

    private Map<Integer, UGame> games;
    private Map<Integer, UChannelMaster> masters;
    private Map<Integer, UChannel> channels;

    private Map<Integer, UGameChannel> gameChannels;
    
    public CacheManager(){

    }

    public static CacheManager getInstance(){
        if(instance == null){
            instance = new CacheManager();
        }
        return instance;
    }

    public List<UGame> getGameList(){

        return new ArrayList<UGame>(games.values());
    }

    /**
     * 根据游戏编号查找
     * @param gameId
     * @return
     */
    public List<UChannel> getChannelList(int gameId,int currChannelID){

    	Set<Integer> keys=channels.keySet();
    	List<UChannel> channelList=new ArrayList();
    	for(Integer key:keys){
    		UChannel channel=channels.get(key);
    		
    		if(channel.getAppID()==gameId && channel.getId()!=currChannelID){
    			channelList.add(channel);
    		}
    	}
        return channelList;
    }
    public List<UChannel> getChannelList(){

        return new ArrayList<UChannel>(channels.values());
    }

    public List<UChannelMaster> getMasterList(){

        return new ArrayList<UChannelMaster>(masters.values());
    }

    public List<UGameChannel> getUGameChannelList(){
    	return new ArrayList<UGameChannel>(this.gameChannels.values());
    }
    /**
     * 获取最大编号
     * @return
     */
    public int getUGameChannelMaxId(){
    	
    	Set<Integer> keys=this.gameChannels.keySet();
    	int max = 0;
    	for(Integer key:keys){
    		if(gameChannels.get(key).getCode()>max){
    			max=gameChannels.get(key).getCode();
    		}
    	}
    	return max;
    }
    public int getUGameChannelCount(){
    	return gameChannels.size();
    }
    public UGame getGame(int appID){
        if(this.games.containsKey(appID)){
            return this.games.get(appID);
        }
        return null;
    }

    public UChannelMaster getMaster(int masterID){
        if(this.masters.containsKey(masterID)){
            return this.masters.get(masterID);
        }
        return null;
    }

    public UChannel getChannel(int channelID){
        if(this.channels.containsKey(channelID)){
            return this.channels.get(channelID);
        }
        return null;
    }
/**
 * 根据游戏编号获取渠道
 * @param gameId
 * @return
 */
    public List<UChannel> getUChannelList(Integer gameId){
    	Set<Integer> keys=this.channels.keySet();
    	List<UChannel> channelList=new ArrayList<UChannel>();
    	
    	for(Integer key:keys){
    		UChannel uChannel=channels.get(key);
    		if(uChannel.getAppID()==(gameId.intValue())){
    			channelList.add(uChannel);
    		}
    	}
    	return channelList;
    }
    public UChannel getChannelByID(Integer id){

        if(id == null){
            return null;
        }

        for(UChannel c : this.channels.values()){
            if(c.getId() == id){
                return c;
            }
        }
        return null;
    }
    
    public UGameChannel getGameChannelByCode(Integer code){
    	Set<Integer> keys=this.gameChannels.keySet();
    
    	for(Integer key:keys){
    		if(gameChannels.get(key).getCode().equals(code)){
    			return gameChannels.get(key);
    		}
    	}
    	return null;
    }
    
    public UGameChannel getGameChannelByID(Integer id){

        if(id == null){
            return null;
        }

        return this.gameChannels.get(id);
    
    }
    public void addGame(UGame game){
        if(games.containsKey(game.getAppID())){
            Log.e("The appID is already is exists. add game failed."+game.getAppID());
            return;
        }

        games.put(game.getAppID(), game);
    }

    public void saveGame(UGame game){
        if(games.containsKey(game.getAppID())){
            games.remove(game.getAppID());
        }
        games.put(game.getAppID(), game);
    }

    public void addMaster(UChannelMaster master){
        if(masters.containsKey(master.getMasterID())){
            Log.e("The channel master ID is already is exists. add channel master faild."+master.getMasterID());
            return;
        }

        masters.put(master.getMasterID(), master);
    }

    public void saveMaster(UChannelMaster master){
        if(masters.containsKey(master.getMasterID())){
            masters.remove(master.getMasterID());
        }
        masters.put(master.getMasterID(), master);
    }

    public void removeMaster(int masterID){
        if(masters.containsKey(masterID)){
            masters.remove(masterID);
        }
    }

    public void addChannel(UChannel channel){
        if(channels.containsKey(channel.getChannelID())){
            Log.e("The channelID is already is exists. add channel faild."+channel.getChannelID());
            return;
        }

        channels.put(channel.getChannelID(), channel);
    }

    //添加或者修改渠道
    public void saveChannel(UChannel channel){
        if(channels.containsKey(channel.getChannelID())){
            channels.remove(channel.getChannelID());
        }

        Log.d("the channel is "+channel);
        UChannel c = getChannelByID(channel.getId());
        if(c != null){
            channels.remove(c.getChannelID());
        }

        channels.put(channel.getChannelID(), channel);
    }
    
    /**
     * 添加游戏渠道
     * @param channel
     */
    public void addGameChannel(UGameChannel channel){
        if(gameChannels.containsKey(channel.getId())){
            Log.e("The game channel  ID is already is exists. add game channel master faild."+channel.getId());
            return;
        }

        gameChannels.put(channel.getId(), channel);
    }
    
    public void saveGameChannel(UGameChannel channel){
        if(this.gameChannels.containsKey(channel.getId())){
        	gameChannels.remove(channel.getId());
        }

        Log.d("the game channel is "+channel);
        UGameChannel c = getGameChannelByID(channel.getId());
        if(c != null){
        	gameChannels.remove(c.getId());
        }

        gameChannels.put(channel.getId(), channel);
    }

    
    public void removeChannel(int channelID){
        if(channels.containsKey(channelID)){
            channels.remove(channelID);
        }
    }

    public void removeGame(int appID){
        if(games.containsKey(appID)){
            games.remove(appID);
        }
    }
    /**
     * 删除游戏渠道
     * @param appID
     */
    public void removeGameChannel(int id){
        if(this.gameChannels.containsKey(id)){
        	gameChannels.remove(id);
        }
    }
    public void loadGameData(List<UGame> gameLst){
        games = new HashMap<Integer, UGame>();
        for(UGame game : gameLst){
            games.put(game.getAppID(), game);
        }
        Log.i("Load games :"+ games.size());
    }

    public void loadMasterData(List<UChannelMaster> masterLst){
        masters = new HashMap<Integer, UChannelMaster>();

        for(UChannelMaster master : masterLst){
            masters.put(master.getMasterID(), master);
        }
        Log.i("Load masters:"+ masters.size());
    }

    public void loadChannelData(List<UChannel> channelLst){
        channels = new HashMap<Integer, UChannel>();
        for(UChannel channel : channelLst){
            channels.put(channel.getChannelID(), channel);
        }
        Log.i("Load channels:"+channels.size());
    }
    /**
     * 加载游戏 渠道
     * @param channelLst
     */
    public void loadGameChannelData(List<UGameChannel> channelLst){
    	gameChannels = new HashMap<Integer, UGameChannel>();
        for(UGameChannel channel : channelLst){
            this.gameChannels.put(channel.getId(), channel);
        }
        Log.i("Load game channels:"+gameChannels.size());
    }

}
