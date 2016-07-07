package com.u8.server.dao;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.u8.server.common.UHibernateTemplate;

import com.u8.server.data.UGameChannel;

@Repository("uGameChannelDao")
public class UGameChannelDao extends UHibernateTemplate<UGameChannel, Integer> {

	public void saveGameChannel(UGameChannel gameChannel) {
		super.save(gameChannel);
	}

	public List<UGameChannel> queryGameChannelByGame(int gameId) {
		String hql = "from UGameChannel where appId  = ?";

		return find(hql, new Object[] { gameId }, null);
	}

	public UGameChannel queryGameChannelByCode(int code) {
		String hql = "from UGameChannel where code  = ?";
		List<UGameChannel> uGameChannelList = find(hql, new Object[] { code },
				null);
		if (null != uGameChannelList && uGameChannelList.size() > 0) {
			return uGameChannelList.get(0);
		}
		return null;
	}

	public UGameChannel queryGameChannel(int id) {

		return super.get(id);
	}
}
