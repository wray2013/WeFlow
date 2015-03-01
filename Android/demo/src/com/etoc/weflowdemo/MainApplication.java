package com.etoc.weflowdemo;


import java.io.File;
import java.io.IOException;
import java.util.LinkedList;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.TextUtils;
import android.view.WindowManager;
import android.widget.Toast;

import com.etoc.weflowdemo.activity.MainActivity;
import com.etoc.weflowdemo.event.DialogUtils;
import com.etoc.weflowdemo.event.RequestEvent;
import com.etoc.weflowdemo.net.GsonResponseObject.UserInfo;
import com.etoc.weflowdemo.util.ConStant;
import com.nostra13.universalimageloader.api.MyImageLoader;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;
import com.nostra13.universalimageloader.cache.memory.impl.LRULimitedMemoryCache;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.utils.StorageUtils;

import de.greenrobot.event.EventBus;


public class MainApplication extends Application {
    private static MainApplication mInstance = null;
    private LinkedList<Activity> activityList = new LinkedList<Activity>(); 
    private long lastRespNullTs = 0;
    public static String accountPhone = null;
    public static int totalFlow = 0;
    public static UserInfo userInfo;
    
	@Override
	public void onCreate() {
		super.onCreate();
		
		CrashHandler crashHandler = CrashHandler.getInstance();
		crashHandler.init(getApplicationContext());
		
		mInstance = this;
	
		
		File cacheDir = StorageUtils.getOwnCacheDirectory(getApplicationContext(), ConStant.getImageLoaderCachePath());
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this)
			.threadPriority(Thread.NORM_PRIORITY - 2)
//			.memoryCache(new WeakMemoryCache())
			.memoryCache(new LRULimitedMemoryCache(5*1024*1024))
			.denyCacheImageMultipleSizesInMemory()
			.diskCache(new UnlimitedDiscCache(cacheDir))
			.tasksProcessingOrder(QueueProcessingType.FIFO)
			.build();

		ImageLoader.getInstance().init(config);
		MyImageLoader.getInstance();
		
//		ImageLoader.getInstance().clearDiskCache();
		EventBus.getDefault().register(this);
	}
	
	/**
	  * 创建全局变量
	  * 全局变量一般都比较倾向于创建一个单独的数据类文件，并使用static静态变量
	  * 
	  * 这里使用了在Application中添加数据的方法实现全局变量
	  * 注意在AndroidManifest.xml中的Application节点添加android:name=".MyApplication"属性
	  * 
	  */ 
	 private WindowManager.LayoutParams wmParams=new WindowManager.LayoutParams(); 
	   
	   
	 public WindowManager.LayoutParams getMywmParams(){ 
	  return wmParams; 
	 }
	
	
	public static void chmod(String permission, String path) {
	    try {
	        String command = "chmod +x " + permission + " " + path;
	        
	        Runtime runtime = Runtime.getRuntime();
	        runtime.exec(command);
	        
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
	}
	
	public static MainApplication getAppInstance() {
		return mInstance;
	}
	
	public static String getAppVersionName() {
		String versionName = "";
		try {
			PackageManager packageManager = MainApplication.getAppInstance().getPackageManager();
			PackageInfo packageInfo = packageManager.getPackageInfo("com.cmmobi.railwifi", 0);
			versionName = packageInfo.versionName;
			if (TextUtils.isEmpty(versionName)) {
				return "";
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return versionName;
	}

    
    // 添加Activity到容器中 
    public void addActivity(Activity activity) { 
         activityList.add(activity); 
    } 
    
    public void removeActivity(Activity activity){
    	activityList.remove(activity); 
    }
    
    public Activity getTopActivity() {
    	if(activityList != null && activityList.size() > 0) {
    		return activityList.getLast();
    	}
    	return null;
    }
    
    // 遍历所有Activity并finish 
    public void cleanAllActivity() { 
	    for (Activity activity : activityList) { 
	    	if(activity!=null){
	    		activity.finish(); 
	    	}
	        
	    } 
    } 
    
    public void onEventMainThread(RequestEvent event) {
		switch(event) {
		case RESP_NULL:
	    	ConnectivityManager manager = (ConnectivityManager) MainApplication.getAppInstance().getSystemService(Context.CONNECTIVITY_SERVICE);  
	        NetworkInfo mobileInfo = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);  
	        NetworkInfo wifiInfo = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);  
	        
	        boolean isConnected = wifiInfo.isConnected()|mobileInfo.isConnected();

	        long curRespNullTs = System.currentTimeMillis();
	        if(curRespNullTs - lastRespNullTs > 3000){
		        if(isConnected){
			    	Toast.makeText(MainApplication.getAppInstance(), "网络不佳，请检查网络连接", Toast.LENGTH_LONG).show();
		        }else{
		        	Toast.makeText(MainApplication.getAppInstance(), "没有网络，请检查网络连接", Toast.LENGTH_LONG).show();
		        }
		        lastRespNullTs = curRespNullTs;
	        }

	        break;
		case LOADING_START:
			DialogUtils.SendLoadingDialogStart(this);
			break;
		case LOADING_END:
			DialogUtils.SendLoadingDialogEnd(this);
			break;
		default:
			break;
		}
	}

}
