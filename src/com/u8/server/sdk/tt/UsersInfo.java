package com.u8.server.sdk.tt;

public class UsersInfo {

	private Long userId;
	private String sid;
	private String gameId;
	private String apikey;
	private String url;

	/**
	 * 
	 * @param userId
	 * @param sid
	 * @param gameId
	 * @param apikey
	 * @param url
	 */
	
	public UsersInfo(Long userId, String sid, String gameId, String apikey,
			String url) {
		super();
		this.userId = userId;
		this.sid = sid;
		this.gameId = gameId;
		this.apikey = apikey;
		this.url = url;
	}

	public String getGameId() {
		return gameId;
	}

	public void setGameId(String gameId) {
		this.gameId = gameId;
	}

	public String getApikey() {
		return apikey;
	}

	public void setApikey(String apikey) {
		this.apikey = apikey;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getSid() {
		return sid;
	}

	public void setSid(String sid) {
		this.sid = sid;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

}
