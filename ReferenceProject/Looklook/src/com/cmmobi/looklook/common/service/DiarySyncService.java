package com.cmmobi.looklook.common.service;

import java.util.ArrayList;
import java.util.List;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;
import cn.zipper.framwork.service.ZService;

import com.cmmobi.looklook.common.gson.GsonResponse2.MyDiary;
import com.cmmobi.looklook.common.gson.GsonResponse2.listMessageResponse;
import com.cmmobi.looklook.common.gson.Requester2;
import com.cmmobi.looklook.info.profile.DiaryManager;

/**
 *  每次打开客户端启动心跳
 *
 *  若有需要检查状态的日记列表，定时心跳请求RIA获取状态，更新状态到本地日记结构
 *  
 *  @author Ray
 * */
public class DiarySyncService extends ZService {
	
	private final String TAG = "DiarySyncService";
	private final int HANDLER_FLAG_HEARTBEAT =  0x19861214;
	
	private final long heartbeat_interval = 5 * 1000;  //5 sec
	
	private String timemilli;
	private List<String> diaryids;
	private int diaryWidth;
	
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		Log.e(TAG, "DiarySyncService onCreate");
		
//		PendingIntent.getBroadcast(this, 0, new Intent(
//				DiarySyncHeartbeatReceiver.ACTION_HEARTBEAT_DIARY_SYNC),
//				PendingIntent.FLAG_UPDATE_CURRENT);	
		Display display =((WindowManager)getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
		diaryWidth = display.getWidth();
		diaryWidth=diaryWidth/2-20;
		if(diaryids == null) {
			diaryids = new ArrayList<String>();
		}
		
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		if (intent != null) {
			String id = intent.getStringExtra("diaryid");
			if (id != null && !id.equals("")) {
				this.diaryids.add(id);
			}
			// Log.e(TAG, "DiarySyncService start (diaryid:" + id + ")");
			handler.removeMessages(HANDLER_FLAG_HEARTBEAT);
			handler.sendEmptyMessageDelayed(HANDLER_FLAG_HEARTBEAT,
					heartbeat_interval);

			if (diaryids != null && diaryids.size() > 0) {
				// 有待查询日记
				String requestDiaries = combineRequest(diaryids);
				// 查询
				Log.d(TAG, "requestDiaries = " + requestDiaries);
				Requester2.listMessage(handler, timemilli,
						diaryids.size() + "", "0", "", requestDiaries,diaryWidth+"");
			}
		}
        
		return Service.START_STICKY;
	}
	
	private String combineRequest(List<String> diaryids) {
		String combine = "";
		for(int i = 0 ; i < diaryids.size(); i++) {
			String diaryid = diaryids.get(i);
			if(!diaryid.equals("")) {
				if(i==0) {
					combine = diaryid;
				} else {
					combine = combine + "," + diaryid;
				}
			}
		}
		return combine;
	}

	private void updateBySameId(List<String> diaryids, MyDiary[] diaries) {
		List<String> dellist = new ArrayList<String>();
		for(String id : diaryids) {
			for(int i = 0; i < diaries.length; i++) {
				String desId = diaries[i].diaryid;
				if(id != null && id.equals(desId)) {
					DiaryManager diarymanager = DiaryManager.getInstance();
					MyDiary myLocalDiary = diarymanager.findLocalDiary(desId);
					if(myLocalDiary != null) {
//						myLocalDiary=diaries[i];
						diaries[i].sync_status = 6;
						diarymanager.saveDiaries(diaries[i], false);
						Log.d(TAG, diaries[i].diaryid+" 已同步完成");
						dellist.add(id);
						//直接对比下一个id
						break;
					}
				}
			}
		}
		this.diaryids.removeAll(dellist);
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
	}
	
	@Override
	public boolean handleMessage(Message msg) {
		// TODO Auto-generated method stub
		switch (msg.what) {
		case HANDLER_FLAG_HEARTBEAT:
			Intent intent = new Intent("ACTION_HEARTBEAT_DIARY_SYNC");
			sendBroadcast(intent);
			break;
		case Requester2.RESPONSE_TYPE_LIST_MESSAGE:
			listMessageResponse obj = (listMessageResponse) msg.obj;
			if (obj != null && "0".equals(obj.status)) {
				timemilli = obj.last_timemilli;
				if(obj.diaries != null && obj.diaries.length > 0) {
					updateBySameId(this.diaryids, obj.diaries);
				}
			}
		}
		return false;
	}
}
