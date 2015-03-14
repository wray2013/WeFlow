package com.etoc.weflow.utils;

import android.os.Environment;


public class ConStant {
	public final static String SD_STORAGE_ROOT = "/.weflow"; //不包括sdcard的路径
	public static final long MEDIA_CACHE_LIMIT = 100;
	public static final int LOAIND_DISSMISS_DALAY = 10000;
	
	public static final String INTENT_SOFT_DETAIL = "intent_sort_detail";
	
	public static String getApkCachePath(){
		return SD_STORAGE_ROOT + "/" + "apk";
	}
	
	public static String getAbsolutePath() {
		return Environment.getExternalStorageDirectory() + SD_STORAGE_ROOT;
	}
	
	public static String getImageLoaderCachePath() {
		return SD_STORAGE_ROOT + "/" + "imageloader/cache";
	}
}
