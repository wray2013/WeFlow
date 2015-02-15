package com.nostra13.universalimageloader.api;

import java.io.File;

import com.cmmobi.looklook.common.constant.Constant;
import com.cmmobi.looklook.info.profile.AccountInfo;
import com.cmmobi.looklook.info.profile.MediaValue;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;
import com.nostra13.universalimageloader.cache.disc.naming.FileNameGenerator;

class LookLookDiscCache extends UnlimitedDiscCache{

	public LookLookDiscCache(File cacheDir,
			FileNameGenerator fileNameGenerator) {
		super(cacheDir, fileNameGenerator);
		// TODO Auto-generated constructor stub
	}
	
	
	@Override
	public void put(String key, File file, String uid,  int belong) {
		// TODO Auto-generated method stub
		super.put(key, file, uid, belong);
		//08-08 01:51:10.937: E/zhw(1598): zhw 
		// key:https://lh5.googleusercontent.com/-LaKXAn4Kr1c/T3R4yc5b4lI/AAAAAAAAAIY/fMgcOVQfmD0/s1024/sample_image_30.jpg, 
		// file:/mnt/sdcard/looklook/uid_zhw/pic/rv7650bw1c3u6nx6wo0ssmcu

		//Log.e("zhw", "zhw key:" + key + ", file:" + file.getAbsolutePath());
		
		MediaValue result = new MediaValue();
		result.UID = uid;
		result.Belong = belong;
		result.Direction = 1;
		result.MediaType = 2;
		result.url = key;
		result.path = Constant.SD_STORAGE_ROOT + "/" + result.UID + "/pic/" + file.getName();
		result.realSize = file.length();
		result.Sync = 1;
		result.SyncSize = result.realSize;
		result.totalSize = result.realSize;
		AccountInfo.getInstance(result.UID).mediamapping.setMedia(result.UID, result.url, result);
	}
	
}
