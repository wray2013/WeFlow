package com.etoc.weflowdemo.service;

import com.etoc.weflowdemo.dialog.PromptDialog;
import com.etoc.weflowdemo.event.DialogEvent;

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
			}
		}else{
			if(type.equals(KEY_TYPE_LOADING_START)){
				PromptDialog.showProgressDialog(this);
			}else if(type.equals(KEY_TYPE_LOADING_END)){
				PromptDialog.dimissProgressDialog();
			}
		}
		

		return Service.START_NOT_STICKY;
	}
	
	
	private void showPromptDialog(final Context mContext, final String title, final String content,final String ok) {
		PromptDialog.Dialog(mContext, false, title, content, ok, null, null, null);
	}

}
