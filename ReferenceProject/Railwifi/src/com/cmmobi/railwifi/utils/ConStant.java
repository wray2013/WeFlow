package com.cmmobi.railwifi.utils;

import android.os.Environment;


public class ConStant {
	public final static String SD_STORAGE_ROOT = "/.railwifi"; //不包括sdcard的路径
	public static final long MEDIA_CACHE_LIMIT = 100;
	public static final int LOAIND_DISSMISS_DALAY = 10000;
	public static final String INTENT_MEDIA_ID = "mediaid";
	public static final String INTENT_SOURCE_ID = "sourceid";
	public static final String INTENT_MEDIA_SRC_PATH = "srcpath";
	public static final String INTENT_LINE_ID = "lineid";
	public static final String SOHU_SOURCE_NAME = "搜狐TV";
	
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
