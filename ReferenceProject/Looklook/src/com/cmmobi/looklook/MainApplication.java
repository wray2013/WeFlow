package com.cmmobi.looklook;


import java.util.ArrayList;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;
import cn.jpush.android.api.JPushInterface;
import cn.zipper.framwork.core.ZApplication;
import cn.zipper.framwork.core.ZLog;

import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.MKGeneralListener;
import com.baidu.mapapi.map.MKEvent;
import com.cmmobi.looklook.common.gson.GsonResponse2.MyDiary;
import com.cmmobi.looklook.common.storage.AsyncAccountInfoSaver;
import com.cmmobi.looklook.common.storage.AsyncActiveAccountSaver;
import com.cmmobi.statistics.CmmobiClickAgent;
import com.cmmobivideo.utils.ContextHolder;
import com.cmmobivideo.utils.PluginUtils;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.umeng.newxp.controller.XpListenersCenter;

import effect.EffectType.LOGLevel;
import effect.XEffectJniUtils;

public class MainApplication extends ZApplication {
    private static MainApplication mInstance = null;
    //private LinkedList<Activity> activityList = new LinkedList<Activity>(); 
    
    public boolean m_bKeyRight = true;
    public BMapManager mBMapManager = null;

    public static final String strKey = "B89BBC6A56D16E84D33D78DE1749F9C48CAC172D";

    private ArrayList<MyDiary> timeLineList;
    
	@Override
	public void onCreate() {
		super.onCreate();
		CrashHandler crashHandler = CrashHandler.getInstance();
		crashHandler.init(getApplicationContext());
		//jpush
        JPushInterface.setDebugMode(true);
        JPushInterface.init(this);     		// init JPush
        //JPushInterface.stopPush(this);
		Config.loadConfig();
		mInstance = this;
		initEngineManager(this);
		
		ImageLoader.getInstance().init(ImageLoaderConfiguration.createDefault(this));
		
		//cdr
        CmmobiClickAgent.setDebugMode(true);
        CmmobiClickAgent.onError(this);
		
        ContextHolder.setContext(this);
        
        PluginUtils.setMainLibraryPath(this.getFilesDir().getAbsolutePath());
        PluginUtils.setSecondaryLibraryPath(this.getCacheDir().getAbsolutePath());
        
        if (PluginUtils.isPluginMounted()) {
            XEffectJniUtils.getInstance().setLogLevel(LOGLevel.LOG_DEBUG); // LOGLevel.LOG_NONE
        }
        
        AsyncAccountInfoSaver.getInstance();
        AsyncActiveAccountSaver.getInstance();
	}
	
	public static MainApplication getAppInstance() {
		return mInstance;
	}
	
	public void initEngineManager(Context context) {
        if (mBMapManager == null) {
            mBMapManager = new BMapManager(context);
        }

        if (!mBMapManager.init(strKey, new MyGeneralListener())) {
            Toast.makeText(getAppInstance().getApplicationContext(), 
                    "BMapManager  初始化错误!", Toast.LENGTH_LONG).show();
        }
        
	}
	
	// 常用事件监听，用来处理通常的网络错误，授权验证错误等
    public static class MyGeneralListener implements MKGeneralListener {
        
        @Override
        public void onGetNetworkState(int iError) {
            if (iError == MKEvent.ERROR_NETWORK_CONNECT) {
                /*Toast.makeText(getAppInstance().getApplicationContext(), "您的网络出错啦！",
                    Toast.LENGTH_LONG).show();*/
            }
            else if (iError == MKEvent.ERROR_NETWORK_DATA) {
                Toast.makeText(getAppInstance().getApplicationContext(), "输入正确的检索条件！",
                        Toast.LENGTH_LONG).show();
            }
            // ...
        }

        @Override
        public void onGetPermissionState(int iError) {
            if (iError ==  MKEvent.ERROR_PERMISSION_DENIED) {
                //授权Key错误：
                Toast.makeText(getAppInstance().getApplicationContext(), 
                        "请输入正确的授权Key！", Toast.LENGTH_LONG).show();
                getAppInstance().m_bKeyRight = false;
            }
        }
    }
    public ArrayList<MyDiary> getTimeLineList() {
		return timeLineList;
	}

	public void setTimeLineList(ArrayList<MyDiary> timeLineList) {
		this.timeLineList = timeLineList;
	}
	
	public static String getAppVersionName() {
		String versionName = "";
		try {
			PackageManager packageManager = ZApplication.getInstance().getPackageManager();
			PackageInfo packageInfo = packageManager.getPackageInfo("com.cmmobi.looklook", 0);
			versionName = packageInfo.versionName;
			if (TextUtils.isEmpty(versionName)) {
				return "";
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return versionName;
	}

    
/*    // 添加Activity到容器中 
    public void addActivity(Activity activity) { 
         activityList.add(activity); 
    } 
    
    // 遍历所有Activity并finish 
    public void cleanAllActivity() { 
	    for (Activity activity : activityList) { 
	    	if(activity!=null){
	    		activity.finish(); 
	    	}
	        
	    } 
    } */

}
