package com.cmmobi.looklook;

import cn.zipper.framwork.core.ZConfig;

public final class Config {

	
	/*
	 *  生产环境，勿启用
	 **/
/*	public static final String SERVER_URL_TEST    = "http://v.mishare.cn:8701/vs/api"; 
	public static final String SERVER_URL_CRM     = "http://channel.looklook.cn:8091/download"; //升级及推荐, 生产地址
	public static final String UDP_HOST = "v.mishare.cn";
	public static final int UDP_PORT = 8765;*/

	/*
	 *  外网测试环境，用于android和ios上线前调试
	 */

	public static final String SERVER_URL_TEST    = "http://test1.mishare.cn:8701/vs/api"; 
	public static final String SERVER_URL_CRM     = "http://test1.mishare.cn:18000/download"; //升级及推荐, 生产地址
	public static final String UDP_HOST = "test1.mishare.cn";
	public static final int UDP_PORT = 8765;
	
	public static final String TCP_HOST = "test1.mishare.cn";

	public static final int TCP_PORT = 18669;
	public static final int NO_NETWORK_SLEEP_PERIOD_SECONDS = 10;
	public static final int TCP_READ_INTERVAL = 10;
	
	/*
	 *  114  测试环境，内网地址，用于和服务器调试
	 */
	/*public static final String SERVER_URL_TEST    = "http://192.168.100.114:19094/vs/api"; 
	public static final String SERVER_URL_CRM     = "http://channel.looklook.cn:8091/download"; //升级及推荐, 生产地址
	public static final String UDP_HOST = "192.168.100.114";、
	public static final int UDP_PORT = 8668;*/
	/*
	 *  白雪涛  测试环境，内网地址，用于和服务器调试
	 */
//	public static final String SERVER_URL_TEST    = "http://172.16.1.90:8080/vs/api"; 
//	public static final String SERVER_URL_CRM     = "http://channel.looklook.cn:8091/download"; //升级及推荐, 生产地址
//	public static final String UDP_HOST = "172.16.1.90";
//	public static final int UDP_PORT = 8668;
	
//	public static final String SERVER_URL_TEST    = "http://172.16.1.100:7074/vs/api"; 

	
	
	/*
	 *  wwb测试环境，用于android和ios上线前调试
	 */
//	public static final String SERVER_URL_TEST    = "http://172.16.1.90:8080/vs/api"; 
//	public static final String SERVER_URL_CRM     = "http://channel.looklook.cn:8091/download"; //升级及推荐, 生产地址
//	public static final String UDP_HOST = "test.looklook.cn";
//	public static final int UDP_PORT = 18668;
	
	public static final int MOSAIC_INDEX = 6;
	public static final int MOSAIC_SIZE = 10;
	public static final int MAX_NOTE_TEXT_LENGTH = 500;


	
	public static void loadConfig() {
		ZConfig.setDebugState(true);
	}

	private Config() {
	}
}
