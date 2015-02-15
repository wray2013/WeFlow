package com.cmmobi.railwifi.parallel;

import java.io.File;
import java.io.FilenameFilter;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;

import com.cmmobi.railwifi.MainApplication;
import com.cmmobi.railwifi.event.ParallelEvent;
import com.cmmobi.railwifi.utils.ConStant;
import com.nostra13.universalimageloader.utils.StorageUtils;

/**
 * 升级策略：
 * 1. 用户主动点击版本检查，
 *      若无新版本则提示Toast提示当前是最新版本，
 *      若有版本则弹出对话框让用户确认。用户确认后，弹出Toast提示程序后台下载中，下载完毕弹出对话框提示用户安装。
 *      
 * 2. 非主动方式，程序每天启动时检测一次，
 *      若有新版本且是wifi直接下载，下载完毕后提示用户有新版本且已下载直接安装
 *      
 * 该组件完成
 * 0. 清理目录中小于或等于当前程序版本号的apk临时文件
 * 1. 若sp里有待安装的版本及文件path直接提示安装，否则进行版本检查
 * 2. 版本下载，下载到目录中，以版本号命名的apk文件。写入sp里待安装的版本按，描述信息，文件path
 * 3. 提示用户安装
 * 
 * 1）如果用户一直不安装新版本，若更新的版本发布，则下载直接覆盖原有的临时包
 * 2）
 * */
public class UpdateTask extends IYTask {
	private final String APK_PREFIX_STR = "version_";
	private final String APK_POSTFIX_STR = ".apk";
	private File downloadDir = null;
	private Context context;

	public UpdateTask(long event_id) {
		super(ParallelEvent.APP_UPDATE, event_id);
		// TODO Auto-generated constructor stub
		context = MainApplication.getAppInstance();
		downloadDir = StorageUtils.getOwnCacheDirectory(context, ConStant.getApkCachePath());
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		if(!canRun()){
			return;
		}
		
		beginRun();
		
		//run
		cleanOldVersion(downloadDir);
		
		super.processTask("ok str");
		endRun();

	}
	
	private String getVersionStr(){
        PackageManager manager = MainApplication.getAppInstance().getPackageManager();
        String packagename = MainApplication.getAppInstance().getPackageName();
        PackageInfo info;
		try {
			info = manager.getPackageInfo(packagename, 0);
	        String version = info.versionName;
	        return version.replace('.', '_');
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return "1_0_0";
	}
	
	//step0
	private void cleanOldVersion(File dir){
		final String curVersionApkStr = APK_PREFIX_STR + getVersionStr() + APK_POSTFIX_STR;
		File[] fs = dir.listFiles(new FilenameFilter() {
			
			@Override
			public boolean accept(File dir, String filename) {
				// TODO Auto-generated method stub
				if(filename.endsWith(APK_POSTFIX_STR) && filename.startsWith(curVersionApkStr)){
					return true;
				}
				
				return false;
			}
		});
		
		if(fs==null || fs.length<=0){
			return;
		}
		
		for (int i = 0; i < fs.length; i++) {
			if (fs[i].isFile() && fs[i].getName().compareTo(curVersionApkStr)<=0) {
				fs[i].delete();
			} 
		}
	}

}
