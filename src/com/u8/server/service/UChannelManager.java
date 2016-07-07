package com.u8.server.service;

import com.u8.server.cache.CacheManager;
import com.u8.server.dao.UChannelDao;
import com.u8.server.data.UChannel;
import com.u8.server.data.UChannelMaster;
import com.u8.server.utils.IDGenerator;
import com.u8.server.web.admin.Config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 */
@Service("channelManager")
public class UChannelManager {

    @Autowired
    private UChannelDao channelDao;

    public UChannel generateChannel(int appID, int masterID, String cpID, String cpAppID, String cpAppKey, String cpAppSecret, String cpPayKey){

        return generateChannel(appID, masterID, cpID, cpAppID, cpAppKey, cpAppSecret, cpPayKey, "", "");
    }

    public UChannel generateChannel(int appID, int masterID, String cpID, String cpAppID, String cpAppKey, String cpAppSecret, String cpPayKey, String cpPayPriKey, String cpPayID){
        UChannel channel = new UChannel();
        channel.setAppID(appID);
        channel.setMasterID(masterID);

        channel.setCpID(cpID);
        channel.setCpAppID(cpAppID);
        channel.setCpAppKey(cpAppKey);
        channel.setCpAppSecret(cpAppSecret);
        channel.setCpPayKey(cpPayKey);
        channel.setCpPayPriKey(cpPayPriKey);
        channel.setCpPayID(cpPayID);



        saveChannel(channel);

        return channel;
    }
    


    /**
     * 根据游戏编号查找
     * @param gameId
     * @return
     */
    public List<UChannel> getChannelList(int gameId,int currChannelID){
    	return CacheManager.getInstance().getChannelList(gameId,currChannelID);
    }

    public int getChannelCount(){

        return CacheManager.getInstance().getChannelList().size();
    }

    //获取当前一个可用的渠道号，默认算法是获取一个当前最大渠道号+1
    public int getValidChannelID(){

        List<UChannel> lst = CacheManager.getInstance().getChannelList();

        int max = 0;

        for(UChannel c : lst){
            if(c.getChannelID() > max){
                max = c.getChannelID();
            }
        }

        return max+1;
    }

    //分页查找
    public List<UChannel> queryPage(int currPage, int num){

        List<UChannel> channels = CacheManager.getInstance().getChannelList();

        Collections.sort(channels, new Comparator<UChannel>() {
            @Override
            public int compare(UChannel o1, UChannel o2) {
                return o1.getChannelID() - o2.getChannelID();
            }
        });

        int fromIndex = (currPage-1) * num;

        if(fromIndex >= channels.size()){

            return null;
        }

        int endIndex = Math.min(fromIndex + num, channels.size());

        return channels.subList(fromIndex, endIndex);
    }

    //添加或者修改channel
    public void saveChannel(UChannel channel){

        if(channel.getChannelID() <= 0){
            channel.setChannelID(getValidChannelID());
        }

        CacheManager.getInstance().saveChannel(channel);
        channelDao.save(channel);
    }

    public UChannel queryChannel(int id){

        return CacheManager.getInstance().getChannel(id);
    }

    public void deleteChannel(UChannel channel){
        if(channel == null){
            return;
        }

        CacheManager.getInstance().removeChannel(channel.getChannelID());
        channelDao.delete(channel);
    }


}
