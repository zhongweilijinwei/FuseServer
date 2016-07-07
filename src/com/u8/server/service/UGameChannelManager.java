package com.u8.server.service;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.u8.server.cache.CacheManager;
import com.u8.server.dao.UGameChannelDao;

import com.u8.server.data.UChannel;
import com.u8.server.data.UGameChannel;

@Service("gameChannelManager")
public class UGameChannelManager {
	@Autowired
	private UGameChannelDao uGameChannelDao;

	/**
	 * 保存
	 * 
	 * @param appID
	 * @param loginChId
	 * @param payChId
	 * @return
	 */
	public UGameChannel generateGameChannel(int appID,int code, int loginChId,
			int payChId) {
		UGameChannel uGameChannel = new UGameChannel();
		uGameChannel.setAppId(appID);
		uGameChannel.setLoginChId(loginChId);
		uGameChannel.setPayChId(payChId);
		uGameChannel.setCode(code);
		uGameChannelDao.save(uGameChannel);
		return uGameChannel;
	}

	/**
	 * 从缓存获取
	 * 
	 * @return
	 */

	public int getGameChannelCount() {

		return CacheManager.getInstance().getUGameChannelCount();
	}

	// 获取当前一个可用的渠道号，默认算法是获取一个当前最大渠道号+1
	public int getValidGameChannelID() {
		return CacheManager.getInstance().getUGameChannelMaxId()+1;
	}
	/**
	 * 根据游戏编号查找
	 * @param gameId
	 * @return
	 */
	public List<UGameChannel> queryGameChannelByGame(int gameId) {
		return uGameChannelDao.queryGameChannelByGame(gameId);
	}
	/**
	 * 根据编号查找
	 * @param code
	 * @return
	 */
	public UGameChannel queryGameChannelByCode(int code) {
		return uGameChannelDao.queryGameChannelByCode(code);
	}
	/**
	 * 分页查找
	 * @param currPage
	 * @param num
	 * @return
	 */
	public List<UGameChannel> queryPage(int currPage, int num){
		List<UGameChannel> allGameChannels=CacheManager.getInstance().getUGameChannelList();
		
		Collections.sort(allGameChannels, new Comparator<UGameChannel>(){
			
	            public int compare(UGameChannel o1, UGameChannel o2) {
	                return o1.getId() - o2.getId();
	            }
		});
		
	      int fromIndex = (currPage-1) * num;

	        if(fromIndex >= allGameChannels.size()){

	            return null;
	        }

	        int endIndex = Math.min(fromIndex + num, allGameChannels.size());

	        return allGameChannels.subList(fromIndex, endIndex);
	}
	
	//添加或者修改channel
    public void saveUGameChannel(UGameChannel channel){

        if(channel.getCode()==null ||channel.getCode() <= 0){
            channel.setCode(this.getValidGameChannelID());
        }

   
        uGameChannelDao.save(channel);
        CacheManager.getInstance().saveGameChannel(channel);
    }
    /**
     * 根据游戏编号获取 渠道
     * @param gameChannelId
     * @return
     */
    public List<UChannel> getUChannelList(int gameId){
    	return CacheManager.getInstance().getUChannelList(gameId);
    }
    public UGameChannel queryUGameChannel(int id){

        return CacheManager.getInstance().getGameChannelByID(id);
    }

    public void deleteChannel(UGameChannel channel){
        if(channel == null){
            return;
        }

      
        uGameChannelDao.delete(channel);
        CacheManager.getInstance().removeGameChannel(channel.getId());
    }

}
