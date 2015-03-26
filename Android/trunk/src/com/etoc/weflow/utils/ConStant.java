package com.etoc.weflow.utils;

import java.util.UUID;

import android.os.Environment;


public class ConStant {
	public final static String SD_STORAGE_ROOT = "/.weflow"; //不包括sdcard的路径
	public static final long MEDIA_CACHE_LIMIT = 100;
	public static final int LOAIND_DISSMISS_DALAY = 10000;
	public static final String REQUEST_SUCCESS = "0000";
	public static final String ORDER_PROCESSED = "0002";
	public static final String LOW_FLOW_COINS = "2012";
	public static final String MAX_LIMIT = "2016";// 已达到最大限制？
	public static final String TIP_MAX_LIMIT = "订购已达到最大上限";
	public static final String ORDER_FAIL = "订购失败";
	public static final String LOW_FLOW = "流量币不足";
	
	public static final String INTENT_SOFT_DETAIL = "intent_sort_detail";
	public static final String INTENT_MAKE_FLOW = "intent_make_flow";
	public static final String INTENT_EXPENSE_FLOW = "intent_expense_flow";
	
	public static final String INTENT_BILL_ALL     = "intent_bill_all";
	public static final String INTENT_BILL_MAKE    = "intent_bill_make";
	public static final String INTENT_BILL_EXPENSE = "intent_bill_expense";
	
	public static String getApkCachePath(){
		return SD_STORAGE_ROOT + "/" + "apk";
	}
	
	public static String getAbsolutePath() {
		return Environment.getExternalStorageDirectory() + SD_STORAGE_ROOT;
	}
	
	public static String getImageLoaderCachePath() {
		return SD_STORAGE_ROOT + "/" + "imageloader/cache";
	}
	
	public static String getPicAbsoluteDirPath() {
		return getAbsolutePath() + "/pic/";
	}
	
	public static String getDownloadCachePath() {
		return SD_STORAGE_ROOT + "/" + "downloads";
	}
	
	/**
	 * 获取一个新的UUID;
	 * @return
	 */
	public static String getNextUUID() {
		return UUID.randomUUID().toString().replace("-", "");
	}
	
}
