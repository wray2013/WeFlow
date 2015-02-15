package com.cmmobi.looklook.info.weather;

import com.cmmobi.looklook.common.gson.GsonResponse3.MyWeatherDescription;


public class MyWeather {
	public String addrCode;    //地址国际码
	public String city;    //城市 （武汉市）
	public String district;//区域 （汉阳区）
	public MyWeatherDescription[] desc; //天气描述
	public long lastModify;    //minutes of ms
}
