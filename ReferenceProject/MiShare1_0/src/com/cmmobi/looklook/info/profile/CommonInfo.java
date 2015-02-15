package com.cmmobi.looklook.info.profile;


import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import cn.zipper.framwork.core.ZApplication;

import com.baidu.mapapi.map.LocationData;
import com.cmmobi.looklook.common.gson.GsonProtocol;
import com.cmmobi.looklook.common.gson.GsonResponse3.ConfigHeart;
import com.cmmobi.looklook.common.gson.GsonResponse3.ConfigPromptMsg;
import com.cmmobi.looklook.common.storage.AsyncCommonInfoSaver;
import com.cmmobi.looklook.common.storage.StorageManager;
import com.cmmobi.looklook.info.location.POIAddressInfo;
import com.cmmobi.looklook.info.weather.MyWeather;



/**
 * 公用数据固化, 用来固化应用程序中与账户无关的公用数据 (主要是从服务器获取的);
 * @author Sunshine
 * @author zhangwei
 *
 */
public final class CommonInfo {
	private static transient final String CommonInfoKey = "CommonInfoKey_LookLook";//需要确保全局唯一性
	private static transient CommonInfo ins = null;
	private static transient Context context = null;
	

	
	
	/*********************/
	public String equipmentid;
	public String ip;

/*	public MyLocation myLoc;*/
	public LocationData myLoc;
	public MyWeather myWeather;
	public String jpush_reg_id;
	public String positionStr;
	public String addressCode; //地址码，配置文件在assets中
	public ArrayList<POIAddressInfo> frequentpos = new ArrayList<POIAddressInfo>();// 常用位置列表
	public ConfigHeart[] heart;
	public ConfigPromptMsg[] promptmsg;
	
	public Boolean isShootPromt = false; // 是否现在记录你的人生

	
	/***********************/
	
	private CommonInfo() {

	}
	
	
	/**
	 *  @author zhangwei
	 *  {@literal 得到CommonInfo实例，会尝试从磁盘中加载数据信息}
	 * */
	public static CommonInfo getInstance(){
		
		context = ZApplication.getInstance();
		if(ins==null){
			CommonInfo a = (CommonInfo) StorageManager.getInstance()
                                                      .getItem(CommonInfoKey, CommonInfo.class);
			
			if(a!=null){
				ins = a;
			}else{
				ins = new CommonInfo();
			}
		}
		return ins;
	}
	
	public String getLongitude() {
		if (myLoc != null) {
			return String.valueOf(myLoc.longitude);
		}
		return GsonProtocol.EMPTY_VALUE;
	}
	
	public String getLatitude() {
		if (myLoc != null) {
			return String.valueOf(myLoc.latitude);
			
		}
		return GsonProtocol.EMPTY_VALUE;
	}
	
	public String getPositionStr() {
		if (CommonInfo.getInstance().positionStr != null) {
			return positionStr;
		}
		return GsonProtocol.EMPTY_VALUE;
	}
	
	
	/**
	 *  @author zhangwei
	 *  {@literal 将location数据保存到CommonInfo}
	 * */
/*	public void updateLocation(MyLocation myLoc){
		this.myLoc = myLoc;
	}*/
	
	/**
	 *  @author zhangwei
	 *  {@literal 从CommonInfo中获取location数据}
	 * */
/*	public void getLocation(MyLocation myLoc){
		this.myLoc = myLoc;
	}*/
	
	
	/**
	 *  @author zhangwei
	 *  {@literal 将CommonInfo数据保存磁盘，一般在onPause中调用}
	 * */
	public void persist(){
		AsyncCommonInfoSaver.getInstance().save(CommonInfoKey, ins, CommonInfo.class);
		//StorageManager.getInstance().putItem(CommonInfoKey, ins, CommonInfo.class);
	}
	
	public void reload() {
		// TODO Auto-generated method stub
		CommonInfo a = (CommonInfo) StorageManager.getInstance()
				.getItem(CommonInfoKey, CommonInfo.class);
		ins = a;
	}
	
	/**
	 *  @author zhangwei
	 *  {@literal 清空CommonInfo数据，一般不会使用}
	 * */
	public void clean(String uid){
		StorageManager.getInstance().deleteItem(CommonInfoKey);
	}
}
