package com.etoc.weflow.version;



import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.net.Uri;
import android.os.Binder;
import android.os.ConditionVariable;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.widget.Toast;

public class VersionUpdater {

	
	private NotificationCompat.Builder mBuilder;
	private NotificationManager mNotiManager;
	private ConditionVariable mCondition;
	private int downLoadSize = 0; // 当前下载�?
	private int fileSize = -1;
	private final int MSG_SUCESS = 1; // 下载成功
	private final int MSG_DOWNLOAD = 2;// 下载
	private final int MSG_ERROR = 3; // 下载出错
	private final int MSG_CANCEL = 4;// 取消下载
	private final int MSG_PAUSE = 5;// 暂停下载
	private final int MSG_CONTINUATION = 6;// 继续下载
	private final int MSG_REFESH_NOFI = 10;// 刷新notify
	private final int MSG_SDCARD_ERROR = 11;// 刷新notify
	private String path = "";
	private Activity mContext;
	private String apkName = "";
	private int NOTIFY_ID = 20;
	public VersionUpdater(String downPath, Activity mContext) {
		path = downPath;
		this.mContext = mContext;
	}

	public void setApkName(String name){
		this.apkName = name;
	}
	
	public void startDownLoad() {
		showNotify();
		new Thread() {
			public void run() {
				if(!hasSDcard()){
					handler.sendEmptyMessage(MSG_SDCARD_ERROR);
					return;
				}
				URLConnection conn = null;
				try {
					String fileName = Environment.getExternalStorageDirectory()
							+ "/" + apkName + ".apk";
					URL url = new URL(path);
					conn = url.openConnection();
					fileSize = conn.getContentLength();
					int s = fileSize;
					File file = new File(fileName);
					if(file.exists()&&file.length()==fileSize){
						downLoadSize = fileSize;
						handler.sendEmptyMessage(MSG_SUCESS);
					}else{
					if (fileSize == -1) {
						handler.sendEmptyMessage(MSG_ERROR);
						return;
					}
					handler.sendEmptyMessage(MSG_REFESH_NOFI);
					InputStream in = conn.getInputStream();
//					getInputStream(
//							mContext, path);
					OutputStream os = new FileOutputStream(fileName);
					byte[] b = new byte[1024 * 10];
					if (in != null) {
						int i = 0;
						while ((i = in.read(b)) != -1) {
							os.write(b, 0, i);
							downLoadSize += i;
						}
					}
				}
				}catch (Exception e) {
					e.printStackTrace();
				}
			}
		}.start();
	}

	public class DownLoadServiceBinder extends Binder {
		public VersionUpdater getService() {
			return VersionUpdater.this;
		}
	}

	private void showNotify() {
		mCondition = new ConditionVariable(false);
		ApplicationInfo info = mContext.getApplicationInfo();
		CharSequence lable = mContext.getPackageManager().getApplicationLabel(
				info);
		if(TextUtils.isEmpty(apkName)){
			apkName  = lable+"";
		}
		int icon = info.icon;
		CharSequence title = "正在下载...";

		mBuilder = new NotificationCompat.Builder(mContext);
		mBuilder.setSmallIcon(icon);
        mBuilder.setContentTitle(apkName);
        mBuilder.setContentText("0%");
        mBuilder.setTicker(title);//第一次提示消息的时�?显示在�?知栏�?
        mBuilder.setAutoCancel(true);
        mBuilder.setProgress(0, 0, true);//设置为true，表示流�?
        
		mNotiManager = (NotificationManager) mContext
				.getSystemService(Context.NOTIFICATION_SERVICE);
		mNotiManager.notify(NOTIFY_ID, mBuilder.build());
	}

	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case MSG_ERROR:
				
				Toast.makeText(mContext, "请检查sdcard.", 0).show();
				mNotiManager.cancelAll();
				break;
			case MSG_SDCARD_ERROR:
				Toast.makeText(mContext, "无法获取文件大小.", 0).show();
				mNotiManager.cancelAll();
				break;
			case MSG_REFESH_NOFI:
				new Thread(new Runnable() {

					@Override
					public void run() {
						while (downLoadSize < fileSize) {
							handler.sendEmptyMessage(MSG_DOWNLOAD);
							mCondition.block(500);
						}
						handler.sendEmptyMessage(MSG_SUCESS);
					}
				}).start();
				break;
			case MSG_SUCESS:
				Intent intent1 = installApk();
				PendingIntent contentIntent = PendingIntent.getActivity(mContext, 0,intent1, 0);
				mBuilder.setContentIntent(contentIntent);
				
			case MSG_DOWNLOAD:
				mBuilder.setProgress(fileSize, downLoadSize, false);
				mBuilder.setContentText((int)(( Double.valueOf(downLoadSize)/Double.valueOf(fileSize))*100)+"%");
				mNotiManager.notify(NOTIFY_ID, mBuilder.build());
				break;
			default:
				break;
			}

		};
	};

	public static boolean hasSDcard() {
		boolean b = false;
		try {
			b = Environment.getExternalStorageState().equals(
					Environment.MEDIA_MOUNTED);
		} catch (Exception e) {
		}
		return b;
	}
	private Intent installApk(){
		Intent intent1 = new Intent(Intent.ACTION_VIEW);
		try {
			String fileName = Environment.getExternalStorageDirectory()
					+ "/" + apkName + ".apk";
			intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); 
			intent1.setDataAndType(Uri.fromFile(new File(fileName)),
					"application/vnd.android.package-archive");
		} catch (Exception e) {
			e.printStackTrace();
		}
		mContext.startActivity(intent1);
		return intent1;
	}
	
}
