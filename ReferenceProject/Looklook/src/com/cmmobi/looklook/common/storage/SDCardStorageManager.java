package com.cmmobi.looklook.common.storage;

import android.util.Log;

import com.google.gson.Gson;

/**
 * @author zhangwei
 */
public class SDCardStorageManager {
	private transient final String storage_dir_in_sdcard = ".looklook";

	private transient final static String ID_SDCardStorageManager = "ID_SDCardStorageManager_";
	private transient final static  String TAG = "SDCardStorageManager";
	private transient static SDCardStorageManager instance = null;
	private transient Gson gson;
	
	private String app_data_dir;
	
	/*******************************/
	
	/********************************/

	private SDCardStorageManager(String account) {
		gson = new Gson();

		instance = (SDCardStorageManager) StorageManager.getInstance().getItem(ID_SDCardStorageManager+account, SDCardStorageManager.class);

	}


	
	private void load(String fileDir) {
		Log.e(TAG, "StorageManager load -  fileDir:" + fileDir);

		
	}

	public static SDCardStorageManager getInstance(String account) {

		if(account==null){
			return null;
		}
		
		if(instance==null){
			SDCardStorageManager a =  (SDCardStorageManager) StorageManager.getInstance().getItem(ID_SDCardStorageManager+account, SDCardStorageManager.class);
		}
		

		return instance;
	}

	

}