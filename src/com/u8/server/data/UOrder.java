package com.u8.server.data;

import com.u8.server.cache.CacheManager;
import com.u8.server.utils.TimeFormater;
import net.sf.json.JSONObject;

import javax.persistence.*;

import java.text.DecimalFormat;
import java.util.Date;

/**
 * 订单对象
 */

@Entity
@Table(name = "uorder")
public class UOrder {

	private static DecimalFormat fnum = new DecimalFormat("##0.00"); 
	
    @Id
    private Long orderID;       //订单号
    private Integer appID;          //当前所属游戏ID
    private Integer channelID;      //当前所属渠道ID
    private Integer userID;         //U8Server这边对应的用户ID
    private String username;    //U8Server这边生成的用户名
    private String productName; //游戏中商品名称
    private String productDesc; //游戏中商品描述
    private Integer money;  //单位 分, 下单时收到的金额，实际充值的金额以这个为准
    private Integer realMoney;  //单位 分，渠道SDK支付回调通知返回的金额，记录，留作查账
    private String currency; //币种
    private String roleID;      //游戏中角色ID
    private String roleName;    //游戏中角色名称
    private String serverID;    //服务器ID
    private String serverName;  //服务器名称
    private Integer state;          //订单状态
    private String channelOrderID;  //渠道SDK对应的订单号
    private String extension;       //扩展数据
    private Date createdTime;       //订单创建时间
    private String sdkOrderTime;          //渠道SDK那边订单交易时间
    private Date completeTime;          //订单完成时间

    public JSONObject toJSON(){
        JSONObject json = new JSONObject();
        json.put("orderID", orderID+"");
        json.put("appID", appID);

        UGame game = getGame();

        json.put("appName", game == null ? "":game.getName());
        json.put("channelID", channelID);

        UChannel channel = getChannel();
        json.put("channelName", channel == null ? "":channel.getMaster().getMasterName());
        json.put("userID", userID);
        json.put("username", username);
        json.put("productName", productName);
        json.put("productDesc", productDesc);
        
        float moneyY=((float)(money))/100f;
        
        json.put("money", fnum.format(moneyY));
        json.put("realMoney", fnum.format(moneyY));
        json.put("currency", currency);
        json.put("roleID", roleID);
        json.put("roleName", roleName);
        json.put("serverID", serverID);
        json.put("serverName", serverName);
        json.put("state", state==1?"未支付":"已支付");
        json.put("channelOrderID", channelOrderID);
        json.put("extension", extension);
        json.put("createdTime", createdTime == null ? "" : TimeFormater.format_default(createdTime));
        json.put("sdkOrderTime", sdkOrderTime);
        json.put("completeTime", completeTime);

        return json;
    }

    public UChannel getChannel(){

        return CacheManager.getInstance().getChannel(this.channelID);
    }

    public UGame getGame(){

        return CacheManager.getInstance().getGame(this.appID);
    }

    public Long getOrderID() {
        return orderID;
    }

    public void setOrderID(Long orderID) {
        this.orderID = orderID;
    }

    public Integer getAppID() {
        return appID;
    }

    public void setAppID(Integer appID) {
        this.appID = appID;
    }

    public Integer getChannelID() {
        return channelID;
    }

    public void setChannelID(Integer channelID) {
        this.channelID = channelID;
    }

    public Integer getUserID() {
        return userID;
    }

    public void setUserID(Integer userID) {
        this.userID = userID;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductDesc() {
        return productDesc;
    }

    public void setProductDesc(String productDesc) {
        this.productDesc = productDesc;
    }

    public Integer getMoney() {
        return money;
    }

    public void setMoney(Integer money) {
        this.money = money;
    }

    public Integer getRealMoney() {
        return realMoney;
    }

    public void setRealMoney(Integer realMoney) {
        this.realMoney = realMoney;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getRoleID() {
        return roleID;
    }

    public void setRoleID(String roleID) {
        this.roleID = roleID;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public String getServerID() {
        return serverID;
    }

    public void setServerID(String serverID) {
        this.serverID = serverID;
    }

    public String getServerName() {
        return serverName;
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    public String getChannelOrderID() {
        return channelOrderID;
    }

    public void setChannelOrderID(String channelOrderID) {
        this.channelOrderID = channelOrderID;
    }

    public String getExtension() {
        return extension;
    }

    public void setExtension(String extension) {
        this.extension = extension;
    }

    public Date getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(Date createdTime) {
        this.createdTime = createdTime;
    }

    public String getSdkOrderTime() {
        return sdkOrderTime;
    }

    public void setSdkOrderTime(String sdkOrderTime) {
        this.sdkOrderTime = sdkOrderTime;
    }

    public Date getCompleteTime() {
        return completeTime;
    }

    public void setCompleteTime(Date completeTime) {
        this.completeTime = completeTime;
    }

	@Override
	public String toString() {
		return "UOrder [orderID=" + orderID + ", appID=" + appID
				+ ", channelID=" + channelID + ", userID=" + userID
				+ ", username=" + username + ", productName=" + productName
				+ ", productDesc=" + productDesc + ", money=" + money
				+ ", realMoney=" + realMoney + ", currency=" + currency
				+ ", roleID=" + roleID + ", roleName=" + roleName
				+ ", serverID=" + serverID + ", serverName=" + serverName
				+ ", state=" + state + ", channelOrderID=" + channelOrderID
				+ ", extension=" + extension + ", createdTime=" + createdTime
				+ ", sdkOrderTime=" + sdkOrderTime + ", completeTime="
				+ completeTime + "]";
	}
    
    
}
