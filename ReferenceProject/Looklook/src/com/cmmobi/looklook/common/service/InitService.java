package com.cmmobi.looklook.common.service;

import java.util.ArrayList;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import cn.zipper.framwork.core.ZApplication;
import cn.zipper.framwork.service.ZService;

import com.cmmobi.looklook.common.gson.GsonResponse2.taglistResponse;
import com.cmmobi.looklook.common.gson.Requester2;
import com.cmmobi.looklook.info.profile.ActiveAccount;
import com.cmmobi.looklook.info.profile.DiaryManager;

public class InitService extends ZService {

	private static final String TAG = "InitService";
	private DiaryManager diaryManager;
	public static boolean ISINIT = false;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		if (!ISINIT) {
			ISINIT = true;
			
			startService(new Intent(this, CommonService.class));
			startService(new Intent(this, PrivateMessageService.class));
			startService(new Intent(this, DiarySyncService.class));
			
			diaryManager = DiaryManager.getInstance();
			//请求标签列表
			Requester2.requestTagList(handler, "");
		}
	}

	@Override
	public boolean handleMessage(Message msg) {
		// TODO Auto-generated method stub
		switch (msg.what) {
		case Requester2.RESPONSE_TYPE_TAGLIST:
			if(null==msg.obj){
				Log.e(TAG, "msg.obj  is null what="+msg.what);
				return false;
			}
			taglistResponse res=(taglistResponse) msg.obj;
			if("0".equals(res.status)){
				if(res.tags!=null&&res.tags.length>0){
					diaryManager.addTagList(res.tags);
				}else{
					Log.e(TAG, "res.diaries is null");
				}
			}else{
				Log.e(TAG, "RESPONSE_TYPE_TAGLIST status is "
						+ res.status);
			}
			ISINIT = false;
			stopSelf();
			break;
		}
		return false;
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}
}
