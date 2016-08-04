package com.u8.server.data;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "ULaunchLog")
public class ULaunchLog implements Serializable{


	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getUchannel() {
		return uchannel;
	}
	public void setUchannel(String uchannel) {
		this.uchannel = uchannel;
	}
	public String getUappkey() {
		return uappkey;
	}
	public void setUappkey(String uappkey) {
		this.uappkey = uappkey;
	}
	public String getUappid() {
		return uappid;
	}
	public void setUappid(String uappid) {
		this.uappid = uappid;
	}
	public String getIccid() {
		return iccid;
	}
	public void setIccid(String iccid) {
		this.iccid = iccid;
	}
	public String getImei() {
		return imei;
	}
	public void setImei(String imei) {
		this.imei = imei;
	}
	public String getImsi() {
		return imsi;
	}
	public void setImsi(String imsi) {
		this.imsi = imsi;
	}
	public String getSdkver() {
		return sdkver;
	}
	public void setSdkver(String sdkver) {
		this.sdkver = sdkver;
	}
	public String getModel() {
		return model;
	}
	public void setModel(String model) {
		this.model = model;
	}
	public String getPlugins() {
		return plugins;
	}
	public void setPlugins(String plugins) {
		this.plugins = plugins;
	}
//	public String getName() {
//		return name;
//	}
//	public void setName(String name) {
//		this.name = name;
//	}
//	public String getType() {
//		return type;
//	}
//	public void setType(String type) {
//		this.type = type;
//	}
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}
	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	public String getNetype() {
		return netype;
	}
	public void setNetype(String netype) {
		this.netype = netype;
	}
	public String getIsp() {
		return isp;
	}
	public void setIsp(String isp) {
		this.isp = isp;
	}
	public String getDpi() {
		return dpi;
	}
	public void setDpi(String dpi) {
		this.dpi = dpi;
	}
	public Date getLaunchtime() {
		return launchtime;
	}
	public void setLaunchtime(Date launchtime) {
		this.launchtime = launchtime;
	}
	public String getPosition() {
		return position;
	}
	public void setPosition(String position) {
		this.position = position;
	}
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	private String uchannel;
	private String uappkey;
	private String uappid;
	private String iccid;
	private String imei;
	private String imsi;
	private String sdkver;
	private String model;
	private String plugins;
//	private String name;
//	private String type;
	private String version;
	private String ip;
	private String netype;
	private String isp;
	private String dpi;
	private String position;
	private Date launchtime;


	
	
}
