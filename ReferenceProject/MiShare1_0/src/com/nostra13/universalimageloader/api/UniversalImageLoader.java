package com.nostra13.universalimageloader.api;

import java.io.File;

import android.content.Context;

import com.cmmobi.looklook.common.constant.Constant;
import com.nostra13.universalimageloader.cache.memory.impl.LRULimitedMemoryCache;
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.utils.StorageUtils;

public class UniversalImageLoader {
	public static void initImageLoader(Context context, String uid) {
		// This configuration tuning is custom. You can tune every option, you may tune some of them,
		// or you can create default configuration by
		//  ImageLoaderConfiguration.createDefault(this);
		// method.
		if(ImageLoader.getInstance().isInited()){
			return;
		}
		
		File cacheDir = StorageUtils.getOwnCacheDirectory(context, Constant.SD_STORAGE_ROOT + "/" + uid + "/pic");
		
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context)
				.threadPriority(Thread.NORM_PRIORITY - 2)
				.memoryCache(new WeakMemoryCache())
				.denyCacheImageMultipleSizesInMemory()
				.discCache(new LookLookDiscCache(cacheDir, new LooklookFileNameGenerator()))
				.tasksProcessingOrder(QueueProcessingType.FIFO)
				/*.writeDebugLogs()*/
			    //.memoryCache(new WeakMemoryCache())
				.build();
		//ImageLoaderConfiguration config = ImageLoaderConfiguration.createDefault(context);
		// Initialize ImageLoader with configuration.
		ImageLoader.getInstance().init(config);
		
		
	}
}
