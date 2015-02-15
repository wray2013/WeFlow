package com.cmmobi.looklook;

import cn.zipper.framwork.core.ZConfig;

public final class Config {

	
	/*
	 *  生产环境，勿启用
	 **/
/*	public static final String SERVER_URL_TEST = "http://aria.looklook.cn:7064/vs/api"; 
	public static final String FRONTCOVER_URL = "http://imgupload.looklook.cn:7087/pa/api";
	public static final String SERVER_URL_CRM = "http://channel.looklook.cn:8091/download/";
	public static final String SERVER_URL_PICTURE = "http://imgupload.looklook.cn:7087/pa/api";*/
	
	/*
	 *  何树营  测试环境
	 */
/*	public static final String SERVER_URL_TEST    = "http://172.16.1.66:8080/vs/api";
	public static final String FRONTCOVER_URL     = "http://192.168.100.114:7077/pa/api";
	public static final String SERVER_URL_CRM     = "http://channel.looklook.cn:8091/download"; //升级及推荐, 生产地址
	public static final String SERVER_URL_PICTURE = "http://192.168.100.114:7077/pa/api";
	*/
	
	/*
	 *  114  测试环境，内网地址，用于和服务器调试
	 */
/*	public static final String SERVER_URL_TEST = "http://192.168.100.114:7074/vs/api";
	public static final String FRONTCOVER_URL     = "http://125.39.224.101:17077/pa/api";
	public static final String SERVER_URL_CRM     = "http://channel.looklook.cn:8091/download"; //升级及推荐, 生产地址
	public static final String SERVER_URL_PICTURE = "http://192.168.100.114:7077/pa/api";*/
	
	/*
	 *  外网测试环境，用于android和ios上线前调试
	 */
	public static final String SERVER_URL_TEST    = "http://test.looklook.cn:19094/vs/api";
	public static final String FRONTCOVER_URL     = "http://125.39.224.101:17077/pa/api";
	public static final String SERVER_URL_CRM     = "http://channel.looklook.cn:8091/download"; //升级及推荐, 生产地址
	public static final String SERVER_URL_PICTURE = "http://125.39.224.101:17077/pa/api";
	
	
	
	public static final String WX_APP_ID = "wxe194363d947f9529"; //产品环境looklook-懂你的生活记录
	//public static final String WX_APP_ID = "wx87ae10b9a792a445";  //zhangwei测试环境
	
	
	public static final int MOSAIC_INDEX = 6;
	public static final int MOSAIC_SIZE = 10;
	public static final int VIDEO_SHOOT_BUTTON_BLOCKING_MILLIS = 700;
	
	public static void loadConfig() {
		ZConfig.setDebugState(true);
	}

	private Config() {
	}
}
