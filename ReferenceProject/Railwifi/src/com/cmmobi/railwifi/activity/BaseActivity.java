package com.cmmobi.railwifi.activity;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.DownloadManager;
import android.app.NotificationManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.Toast;

import com.cmmobi.common.tools.SpHelper;
import com.cmmobi.railwifi.dialog.PromptDialog;
import com.cmmobi.railwifi.download.DownloadType;
import com.cmmobi.railwifi.event.DialogEvent;
import com.cmmobi.railwifi.receiver.PushReceiver;
import com.cmmobi.railwifi.utils.CmmobiClickAgentWrapper;
import com.cmmobi.railwifi.utils.ConStant;
import com.cmmobi.railwifi.utils.DateUtils;

import de.greenrobot.event.EventBus;

public abstract class BaseActivity extends FragmentActivity {

	public abstract int rootViewId();
	
	@Override
	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		super.onCreate(arg0);
		
		if(rootViewId() != 0){
			setContentView(rootViewId());
		}
		
		EventBus.getDefault().register(this);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		CmmobiClickAgentWrapper.onResume(this);
	}

	@Override
	protected void onPause() {
		super.onPause();
		CmmobiClickAgentWrapper.onPause(this);
	}
	

	@Override
	protected void onStop(){
		super.onStop();
		CmmobiClickAgentWrapper.onStop(this);
	}
	
	@Override
	protected void onDestroy(){
		super.onDestroy();
		EventBus.getDefault().unregister(this);
		PromptDialog.dismissDialog();
		PromptDialog.dimissProgressDialog();
	}
	
	public static void ProcessDialogEvent(final Context context, final DialogEvent evt, String className){
		if(!isTopActivity(context, className)){
			return;
		}
		
		switch(evt){
		case LOADING_START:
			PromptDialog.showProgressDialog(context);
			break;
		case LOADING_END:
			PromptDialog.dimissProgressDialog();
			break;
		case CALL_HELP_DIALOG:
			String title = evt.getTitle();
			String content = evt.getContent();
			String ok = evt.getOK();
			final int notifyId = evt.getNotifyID();
			Log.v("======", "titleRoot - KEY_TYPE_DIALOG - title:" + title + ", content:" + content + ", notifyId:" + notifyId);
			if(title!=null && content!=null && !title.equals("") && !content.equals("")){
				PromptDialog.Dialog(context, true, true, false, title,
						content, "求助", "取消",
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog, int which) {
								// TODO Auto-generated method stub
								NotificationManager mNotificationManager =
									    (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
									
								if(notifyId>0){
									mNotificationManager.cancel(notifyId);
								}
								
								Intent intent = new Intent(context,
										CallForHelpActivity.class);
								intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
								context.startActivity(intent);
							}
						}, new DialogInterface.OnClickListener() {
							
							@Override
							public void onClick(DialogInterface dialog, int which) {
								// TODO Auto-generated method stub
								NotificationManager mNotificationManager =
									    (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
									
								if(notifyId>0){
									mNotificationManager.cancel(notifyId);
								}
							}
						}, true, MainActivity.railway_name);
			}
			
			break;
		case JUMP_TO:
			final String title2 = evt.getTitle();
			final String content2 = evt.getContent();
			String ok2 = evt.getOK();
			final String type = evt.getType();
			final String object_id = evt.getObjectId();
			final int notifyId2 = evt.getNotifyID();
			PromptDialog.Dialog(context, false, title2, content2, "去看看", null, new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					NotificationManager mNotificationManager =
						    (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
						
					if(notifyId2>0){
						mNotificationManager.cancel(notifyId2);
					}
					
					if(type==null){
						return;
					}
					
					Intent intent = new Intent();
					if(type.equals(PushReceiver.ALUMB)){
						intent.setClass(context, AlbumDetailActivity.class);
					}else if(type.equals(PushReceiver.JOKE)){
						intent.setClass(context, JokeDetailActivity.class);
					}else if(type.equals(PushReceiver.MOVIE)){
						intent.setClass(context, MovieDetailActivity.class);
					}else if(type.equals(PushReceiver.MUSIC)){
						intent.setClass(context, MusicDetailActivity.class);
					}else if(type.equals(PushReceiver.NEWS)){
						intent.setClass(context, NewsDetailActivity.class);
					}else if(type.equals(PushReceiver.SHOP)){
						intent.setClass(context, OrderShoppingActivity.class);
					}else{
						return;
					}
					
					if(object_id==null && !type.equals(PushReceiver.SHOP)){
						return;
					}
					
					intent.putExtra(ConStant.INTENT_TITLE, title2);
					intent.putExtra(ConStant.INTENT_CONTENT, content2);
					intent.putExtra(ConStant.INTENT_MEDIA_ID, object_id);

					intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					context.startActivity(intent);
				}
			}, null);
			break;
		case DOWNLOAD:
			final String title3 = evt.getTitle();
			final String content3 = evt.getContent();
			final String url3 = evt.getUrl();
			final String packageName = evt.getData();
			PromptDialog.Dialog(context, true, true, title3, content3, new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
//					DownloadManager downloadManager = (DownloadManager)context.getSystemService(Activity.DOWNLOAD_SERVICE);
					String apkUrl = url3;
					if(apkUrl==null || apkUrl.equals("")){
						Toast.makeText(context, "下载链接无效", Toast.LENGTH_LONG).show();
						return;
					}
					
					Log.d("=AAA=","PromptDialog download apkUrl = " + apkUrl + " title3 = " + title3);
					com.cmmobi.railwifi.download.DownloadManager.getInstance().addDownloadTask(apkUrl, "0", title3, "", "",  DownloadType.APP, ConStant.SOHU_SOURCE_NAME, packageName);
					/*try{
						DownloadManager.Request request = new DownloadManager.Request(Uri.parse(apkUrl));
						request.setDestinationInExternalPublicDir(ConStant.SD_STORAGE_ROOT + "/apks", title3 + ".apk");
						request.setVisibleInDownloadsUi(false);
						request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
						request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI);
						long id = downloadManager.enqueue(request);						
					}catch(Exception e){
						e.printStackTrace();
					}*/

				}
			});
			break;
		case UPDATE_FORCE_DIALOG_ALWAYS:
			final String title4 = evt.getTitle();
			final String content4 = evt.getContent();
			final String url4 = evt.getUrl();
			PromptDialog.Dialog(context, false, false, true, title4,
					content4, "立即下载", null, 
			new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					SettingActivity.getApkUrl(context, url4);
				}
			}, null, true, null);
			break;
		case UPDATE_FORCE_DIALOG_DISMISS:
			final String title5 = evt.getTitle();
			final String content5 = evt.getContent();
			final String url5 = evt.getUrl();
			PromptDialog.Dialog(context, false, true, false, title5,
					content5, "立即下载", null, 
			new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					SettingActivity.getApkUrl(context, url5);
				}
			}, null, true, null);
			break;
		case UPDATE_NORMAL_DIALOG:
			final String title6 = evt.getTitle();
			final String content6 = evt.getContent();
			final String url6 = evt.getUrl();
			PromptDialog.Dialog(context, true, true, false, title6, content6,
			"立即下载", "忽略", new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					SettingActivity.getApkUrl(context, url6);
				}
			}, null, true, null);
			String curDate = DateUtils.getDayString();
	        SpHelper.setEditor(context, SettingActivity.PROMT_DATE, curDate);
			break;
		}
	
	}
	
	public void onEventMainThread(Object event) {
		if(event instanceof DialogEvent){
			DialogEvent evt  =(DialogEvent) event;
			ProcessDialogEvent(this, evt, this.getClass().getName());
		}
	}
	
	public static boolean isTopActivity(Context context, String className)  
    {  
        boolean isTop = false;  
        ActivityManager am = (ActivityManager)context.getSystemService(ACTIVITY_SERVICE);  
        ComponentName cn = am.getRunningTasks(1).get(0).topActivity;  
        Log.d("TAG", "isTopActivity = " + cn.getClassName() + ", cur:" + className);  
        if (cn.getClassName().contains(className))  
        {  
            isTop = true;  
        }  
        Log.d("TAG", "isTop = " + isTop);  
        return isTop;  
    } 
}
