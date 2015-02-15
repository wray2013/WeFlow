package com.cmmobi.railwifi.service;

import com.cmmobi.railwifi.activity.CallForHelpActivity;
import com.cmmobi.railwifi.activity.MainActivity;
import com.cmmobi.railwifi.dialog.PromptDialog;
import com.cmmobi.railwifi.event.DialogEvent;
import com.cmmobi.railwifi.utils.ConStant;

import de.greenrobot.event.EventBus;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.view.WindowManager;

public class PromtService extends Service {
	public static final String KEY_TYPE_LOADING_START = "KEY_TYPE_LOADING_START";
	public static final String KEY_TYPE_LOADING_END = "KEY_TYPE_LOADING_END";
	public static final String KEY_TYPE_CALLHELP_DIALOG = "KEY_TYPE_LOADING_DIALOG";
//	public static final String KEY_TYPE_PROMPT_DIALOG = "KEY_TYPE_PROMPT_DIALOG";
	public static final String KEY_TYPE_PROMPT_DOWNLOAD = "KEY_TYPE_PROMPT_DOWNLOAD";
	public static final String KEY_TYPE_UPDATE_FORCE_DIALOG_DISMISS = "KEY_TYPE_UPDATE_FORCE_DIALOG_DISMISS";
	public static final String KEY_TYPE_UPDATE_FORCE_DIALOG_ALWAYS = "KEY_TYPE_UPDATE_FORCE_DIALOG_ALWAYS";
	public static final String KEY_TYPE_UPDATE_NORMAL_DIALOG = "KEY_TYPE_UPDATE_NORMAL_DIALOG";
	
	private static final String TAG = "PromtService";
	public static final String KEY_TYPE = "key_type";
	public static final String KEY_TITLE = "key_title";
	public static final String KEY_CONTENT = "key_content";
	public static final String KEY_BTN_OK = "key_btn_ok";
	public static final String KEY_URL = "key_url";
	public static final String KEY_MSGID = "key_msgid";
	public static final String KEY_NOTIFYID = "key_notifyid";

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {

		
		if(intent==null){
			Log.e(TAG, "onStartCommand, flags:" + flags + " startId" + startId + ", intent:" + intent);
			return Service.START_NOT_STICKY;
		}
		
		String type = intent.getStringExtra(KEY_TYPE);
		String title = intent.getStringExtra(KEY_TITLE);
		String content = intent.getStringExtra(KEY_CONTENT);
		String ok = intent.getStringExtra(KEY_BTN_OK);
		String url = intent.getStringExtra(KEY_URL);
		int notifyId = intent.getIntExtra(KEY_NOTIFYID, -1);
		
		Log.e(TAG, "onStartCommand, flags:" + flags + " startId" + startId + ", title:" + title + ", content:" + content + ", notifyId:" + notifyId);
		
		if (ok == null) {
			ok = "确定";
		}

		if(type==null){
			return Service.START_NOT_STICKY;
		}
		
		boolean flag = true;
		if(flag){
			if(type.equals(KEY_TYPE_LOADING_START)){
				Log.v(TAG, "service - KEY_TYPE_LOADING_START");
				EventBus.getDefault().post(DialogEvent.LOADING_START);
			}else if(type.equals(KEY_TYPE_LOADING_END)){
				Log.v(TAG, "service - KEY_TYPE_LOADING_END");
				EventBus.getDefault().post(DialogEvent.LOADING_END);
			}else if(type.equals(KEY_TYPE_CALLHELP_DIALOG)){
				Log.v(TAG, "service - KEY_TYPE_DIALOG");
				DialogEvent e = DialogEvent.CALL_HELP_DIALOG;
				e.setTitle(title);
				e.setContent(content);
				e.setNotifyID(notifyId);
				EventBus.getDefault().post(e);
			} /*else if (type.equals(KEY_TYPE_PROMPT_DIALOG)) {
				Log.v(TAG, "service - KEY_TYPE_PROMPT_DIALOG");
				DialogEvent e = DialogEvent.PROMPT;
				e.setTitle(title);
				e.setContent(content);
				e.setOK(ok);
				EventBus.getDefault().post(e);
			}*/else if (type.equals(KEY_TYPE_PROMPT_DOWNLOAD)) {
				Log.v(TAG, "service - KEY_TYPE_PROMPT_DOWNLOAD");
				DialogEvent e = DialogEvent.DOWNLOAD;
				e.setTitle(title);
				e.setContent(content);
				e.setUrl(url);
				EventBus.getDefault().post(e);
			}else if (type.equals(KEY_TYPE_UPDATE_FORCE_DIALOG_ALWAYS)) {
				Log.v(TAG, "service - KEY_TYPE_UPDATE_FORCE_DIALOG_ALWAYS");
				DialogEvent e = DialogEvent.UPDATE_FORCE_DIALOG_ALWAYS;
				e.setTitle(title);
				e.setContent(content);
				e.setUrl(url);
				EventBus.getDefault().post(e);
			}else if (type.equals(KEY_TYPE_UPDATE_FORCE_DIALOG_DISMISS)) {
				Log.v(TAG, "service - KEY_TYPE_UPDATE_FORCE_DIALOG_DISMISS");
				DialogEvent e = DialogEvent.UPDATE_FORCE_DIALOG_DISMISS;
				e.setTitle(title);
				e.setContent(content);
				e.setUrl(url);
				EventBus.getDefault().post(e);
			}else if (type.equals(KEY_TYPE_UPDATE_NORMAL_DIALOG)) {
				Log.v(TAG, "service - KEY_TYPE_UPDATE_NORMAL_DIALOG");
				DialogEvent e = DialogEvent.UPDATE_NORMAL_DIALOG;
				e.setTitle(title);
				e.setContent(content);
				e.setUrl(url);
				EventBus.getDefault().post(e);
			}
		}else{
			if(type.equals(KEY_TYPE_LOADING_START)){
				PromptDialog.showProgressDialog(this);
			}else if(type.equals(KEY_TYPE_LOADING_END)){
				PromptDialog.dimissProgressDialog();
			}else if(type.equals(KEY_TYPE_CALLHELP_DIALOG)){
				showXDialog(this, title, content);
			} /*else if (type.equals(KEY_TYPE_PROMPT_DIALOG)) {
				showPromptDialog(this, title, content, ok);
			}*/else if (type.equals(KEY_TYPE_PROMPT_DOWNLOAD)) {
				showDownloadDialog(this, title, content, url);
			}
		}
		

		return Service.START_NOT_STICKY;
	}
	
	private void showXDialog(final Context mContext, final String title, final String content) {
		try {
			PromptDialog.Dialog(this, true, true, false, title/*"紧急通知"*/,
					content/*"15车有险情请注意安全,15车有险情请注意安全"*/, "求助", "取消",
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							Intent intent = new Intent(mContext,
									CallForHelpActivity.class);
							intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
							startActivity(intent);
						}
					}, null, true, MainActivity.railway_name/*"北京铁路局"*/);

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
	
	private void showPromptDialog(final Context mContext, final String title, final String content,final String ok) {
		PromptDialog.Dialog(mContext, false, title, content, ok, null, null, null);
	}
	
	private void showDownloadDialog(final Context mContext, final String title, final String content, final String url) {
		PromptDialog.Dialog(mContext, true, true, title, content, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				DownloadManager downloadManager = (DownloadManager)mContext.getSystemService(Activity.DOWNLOAD_SERVICE);
				String apkUrl = url;
				DownloadManager.Request request = new DownloadManager.Request(Uri.parse(apkUrl));
				request.setDestinationInExternalPublicDir(ConStant.SD_STORAGE_ROOT + "/apks", title + ".apk");
				request.setVisibleInDownloadsUi(false);
				request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
				request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI);
				long id = downloadManager.enqueue(request);
			}
		});
	}

}
