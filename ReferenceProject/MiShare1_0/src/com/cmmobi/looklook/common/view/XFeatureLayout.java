package com.cmmobi.looklook.common.view;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.os.SystemClock;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.LocalBroadcastManager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import cn.zipper.framwork.core.ZLog;
import cn.zipper.framwork.utils.ZStringUtils;

import com.cmmobi.looklook.R;
import com.cmmobi.looklook.activity.AudioRecorderActivity;
import com.cmmobi.looklook.activity.CreateNoteActivity;
import com.cmmobi.looklook.activity.DiaryPreviewActivity;
import com.cmmobi.looklook.activity.LookLookActivity;
import com.cmmobi.looklook.activity.MediaScanActivity;
import com.cmmobi.looklook.activity.VideoShootActivity2;
import com.cmmobi.looklook.arcmenu.ArcMenu;
import com.cmmobi.looklook.common.constant.Constant;
import com.cmmobi.looklook.common.gson.DiaryController;
import com.cmmobi.looklook.common.gson.DiaryController.DiaryWrapper;
import com.cmmobi.looklook.common.gson.DiaryController.FileOperate;
import com.cmmobi.looklook.common.gson.GsonProtocol;
import com.cmmobi.looklook.common.gson.GsonResponse3.MyDiary;
import com.cmmobi.looklook.common.gson.GsonResponse3.MyDiaryList;
import com.cmmobi.looklook.common.utils.CmmobiClickAgentWrapper;
import com.cmmobi.looklook.common.utils.EffectsDownloadUtil;
import com.cmmobi.looklook.fragment.FragmentHelper;
import com.cmmobi.looklook.fragment.MyZoneFragment;
import com.cmmobi.looklook.fragment.VShareFragment;
import com.cmmobi.looklook.fragment.XFragment;
import com.cmmobi.looklook.fragment.ZoneBaseFragment;
import com.cmmobi.looklook.info.profile.ActiveAccount;
import com.cmmobi.looklook.info.profile.CommonInfo;
import com.cmmobi.looklook.info.profile.DiaryManager;
import com.cmmobi.looklook.prompt.Prompt;
import com.cmmobi.looklook.prompt.TickUpHelper;
import com.cmmobi.statistics.CmmobiClickAgent;
import com.iflytek.msc.ExtAudioRecorder;
import com.iflytek.msc.QISR_TASK;

/**
 * @author wuxiang
 * @email wuxiang@cmmobi.com
 * @date  2013-10-23
 */
public class XFeatureLayout extends RelativeLayout implements OnClickListener, Callback{

	private static final String TAG=XFeatureLayout.class.getSimpleName();
	private Handler handler;
	private ArcMenu menu;
	public XFeatureLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		if(isInEditMode()) {
			return;
		}
		ear = ExtAudioRecorder.getInstanse(false, 2, 1);
		
		LocalBroadcastManager.getInstance(getContext()).registerReceiver(
				mMessageReceiver,
				new IntentFilter(ExtAudioRecorder.AUDIO_RECORDER_MSG));
		
		LocalBroadcastManager.getInstance(getContext()).registerReceiver(mMessageReceiver,
			      new IntentFilter(QISR_TASK.QISR_RESULT_MSG));
	}
	
//	private static final int[] ITEM_DRAWABLES = { R.drawable.btn_picture, R.drawable.btn_video,
//		R.drawable.btn_record, R.drawable.btn_note, R.drawable.btn_import};
	
	private void init(){
		if(getChildCount()==0){
			Log.e(TAG,"init error");
			return;
		}
		handler = new Handler(this);
		if(!isInEditMode()) {
			ear.setHandler(handler);
		}
		 getChildAt(0).setOnClickListener(this);
		/*featureList=(XFeatureList) contentView.findViewById(R.id.x_feature_list);
		featureList.setOnFeatureClicklistener(this);
		featureList.setOnFeatureLongClicklistener(this);
		featureList.setOnFeatureTouchlistener(this);
		featureList.setOnHiddenChangedListener(this);
		setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				featureList.hide();
			}
		});*/
//		menu = (ArcMenu) findViewById(R.id.x_feature_list);
		/*List<SatelliteMenuItem> items = new ArrayList<SatelliteMenuItem>();
		items.add(new SatelliteMenuItem(5, R.drawable.btn_import));
		items.add(new SatelliteMenuItem(4, R.drawable.btn_note));
		items.add(new SatelliteMenuItem(3, R.drawable.btn_record));
		items.add(new SatelliteMenuItem(2, R.drawable.btn_video));
		items.add(new SatelliteMenuItem(1, R.drawable.btn_picture));
        menu.addItems(items);     
        menu.setMainImage(R.drawable.btn_feature_1);
        menu.setOnItemClickedListener(this);
        menu.setOnItemLongClickListener(this);
        menu.setOnItemTouchListener(this);*/
//		initArcMenu(menu, ITEM_DRAWABLES);
		
		
		}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.iv_video:{
			if (!EffectsDownloadUtil.getInstance((Activity)getContext()).checkEffects()) {
				VideoShootActivity2.startOnShortShootMode((Activity)getContext(), false);
				CmmobiClickAgentWrapper.onEvent(getContext(), "video");
				/*ZoneBaseFragment fragment = FragmentHelper.getInstance((FragmentActivity)getContext()).getZoneBaseFragment();
				XFragment xFragment = fragment.getXFragment();
				if (xFragment instanceof MyZoneFragment) {
					CmmobiClickAgent.onEvent(getContext(), "video", "1");
				} else if (xFragment instanceof VShareFragment) {
					CmmobiClickAgent.onEvent(getContext(), "video", "2");
				}*/
			}
			break;}

		default:
			break;
		}
	}



	private void initArcMenu(ArcMenu menu, int[] itemDrawables) {
        final int itemCount = itemDrawables.length;
        for (int i = 0; i < itemCount; i++) {
            ImageView item = new ImageView(getContext());
            item.setImageResource(itemDrawables[i]);
            final int position = i;
            menu.addItem(item, new OnClickListener() {
                @Override
                public void onClick(View v) {
                    switch (position) {
                    case 0:{
                    	if (!EffectsDownloadUtil.getInstance((Activity)getContext()).checkEffects()) {
            				VideoShootActivity2.startOnCaptureMode((Activity)getContext());
            				
            			}
                    	break;}
                    case 1:{
                    	if (!EffectsDownloadUtil.getInstance((Activity)getContext()).checkEffects()) {
            				VideoShootActivity2.startOnShortShootMode((Activity)getContext(), false);
            				CmmobiClickAgentWrapper.onEvent(getContext(), "video");
            			}
                    	break;}
                    case 2:{
                    	if (!EffectsDownloadUtil.getInstance((Activity)getContext()).checkEffects()) {
            				AudioRecorderActivity.startSelf((Activity)getContext());
            				ZoneBaseFragment fragment = FragmentHelper.getInstance((FragmentActivity)getContext()).getZoneBaseFragment();
            				XFragment xFragment = fragment.getXFragment();
            				if (xFragment instanceof MyZoneFragment) {
            					CmmobiClickAgent.onEvent(getContext(), "record", "1");
            				} else if (xFragment instanceof VShareFragment) {
            					CmmobiClickAgent.onEvent(getContext(), "record", "2");
            				}
            			}
                    	break;}
                    case 3:{
                    	Intent intent = new Intent();
            			intent.setClass(getContext(), CreateNoteActivity.class);
            			getContext().startActivity(intent);
            			ZoneBaseFragment fragment = FragmentHelper.getInstance((FragmentActivity)getContext()).getZoneBaseFragment();
            			XFragment xFragment = fragment.getXFragment();
            			HashMap<String,String> map = new HashMap<String, String>();
            			if (xFragment instanceof MyZoneFragment) {
            				map.put("label", "1");
            				map.put("label2", "2");
            				CmmobiClickAgent.onEvent(getContext(), "voice_note", map);
            			} else if (xFragment instanceof VShareFragment) {
            				map.put("label", "3");
            				map.put("label2", "2");
            				CmmobiClickAgent.onEvent(getContext(), "voice_note", map);
            			}
                    	break;}
					case 4:{
						Intent intent = new Intent();
						intent.setClass(getContext(), MediaScanActivity.class);
						intent.putExtra(MediaScanActivity.INTENT_SCAN_MODE, MediaScanActivity.MODE_PIC_NORMAL);
						getContext().startActivity(intent);
						ZoneBaseFragment fragment = FragmentHelper.getInstance((FragmentActivity)getContext()).getZoneBaseFragment();
						XFragment xFragment = fragment.getXFragment();
						if (xFragment instanceof MyZoneFragment) {
							CmmobiClickAgent.onEvent(getContext(), "import", "1");
						} else if (xFragment instanceof VShareFragment) {
							CmmobiClickAgent.onEvent(getContext(), "import", "2");
						}
						break;
					}
					default:
						break;
					}
                }
            });
            
            if(3==i){
            	item.setOnLongClickListener(new OnLongClickListener() {
            		
            		@Override
            		public boolean onLongClick(View v) {
            			startRecoder();
            			ZoneBaseFragment fragment = FragmentHelper.getInstance((FragmentActivity)getContext()).getZoneBaseFragment();
            			XFragment xFragment = fragment.getXFragment();
            			HashMap<String,String> map = new HashMap<String, String>();
            			if (xFragment instanceof MyZoneFragment) {
            				map.put("label", "1");
            				map.put("label2", "1");
            				CmmobiClickAgent.onEvent(getContext(), "voice_note", map);
            			} else if (xFragment instanceof VShareFragment) {
            				map.put("label", "3");
            				map.put("label2", "1");
            				CmmobiClickAgent.onEvent(getContext(), "voice_note", map);
            			}
            			return true;
            		}
            	});
            	item.setOnTouchListener(menu.getItemTouchListener(new OnTouchListener() {
					
					@Override
					public boolean onTouch(View v, MotionEvent event) {
						onNoteBtnTouch(v,event);
						return false;
					}
				}));
            }
        }
        
    }
	
	/*@Override
	public void onClick(int id) {
		switch (id) {
		case 1://图片
			if (!EffectsDownloadUtil.getInstance((Activity)getContext()).checkEffects()) {
				VideoShootActivity2.startOnCaptureMode((Activity)getContext());
				ZoneBaseFragment fragment = FragmentHelper.getInstance((FragmentActivity)getContext()).getZoneBaseFragment();
				XFragment xFragment = fragment.getXFragment();
				if (xFragment instanceof MyZoneFragment) {
					CmmobiClickAgent.onEvent(getContext(), "photo", "1");
				} else if (xFragment instanceof VShareFragment) {
					CmmobiClickAgent.onEvent(getContext(), "photo", "2");
				}
			}
			
			break;
		case 2://视频
			if (!EffectsDownloadUtil.getInstance((Activity)getContext()).checkEffects()) {
				VideoShootActivity2.startOnShootMode((Activity)getContext());
				ZoneBaseFragment fragment = FragmentHelper.getInstance((FragmentActivity)getContext()).getZoneBaseFragment();
				XFragment xFragment = fragment.getXFragment();
				if (xFragment instanceof MyZoneFragment) {
					CmmobiClickAgent.onEvent(getContext(), "video", "1");
				} else if (xFragment instanceof VShareFragment) {
					CmmobiClickAgent.onEvent(getContext(), "video", "2");
				}
			}
			break;
		case 3://录音
			if (!EffectsDownloadUtil.getInstance((Activity)getContext()).checkEffects()) {
				AudioRecorderActivity.startSelf((Activity)getContext());
				ZoneBaseFragment fragment = FragmentHelper.getInstance((FragmentActivity)getContext()).getZoneBaseFragment();
				XFragment xFragment = fragment.getXFragment();
				if (xFragment instanceof MyZoneFragment) {
					CmmobiClickAgent.onEvent(getContext(), "record", "1");
				} else if (xFragment instanceof VShareFragment) {
					CmmobiClickAgent.onEvent(getContext(), "record", "2");
				}
			}
			break;
		case 4:{//便签
			Intent intent = new Intent();
			intent.setClass(getContext(), CreateNoteActivity.class);
			getContext().startActivity(intent);
			ZoneBaseFragment fragment = FragmentHelper.getInstance((FragmentActivity)getContext()).getZoneBaseFragment();
			XFragment xFragment = fragment.getXFragment();
			HashMap<String,String> map = new HashMap<String, String>();
			if (xFragment instanceof MyZoneFragment) {
				map.put("label", "1");
				map.put("label2", "2");
				CmmobiClickAgent.onEvent(getContext(), "voice_note", map);
			} else if (xFragment instanceof VShareFragment) {
				map.put("label", "3");
				map.put("label2", "2");
				CmmobiClickAgent.onEvent(getContext(), "voice_note", map);
			}
			break;}
		case 5:{
			Intent intent = new Intent();
			intent.setClass(getContext(), MediaScanActivity.class);
			intent.putExtra(MediaScanActivity.INTENT_SCAN_MODE, MediaScanActivity.MODE_PIC_NORMAL);
			getContext().startActivity(intent);
			ZoneBaseFragment fragment = FragmentHelper.getInstance((FragmentActivity)getContext()).getZoneBaseFragment();
			XFragment xFragment = fragment.getXFragment();
			if (xFragment instanceof MyZoneFragment) {
				CmmobiClickAgent.onEvent(getContext(), "import", "1");
			} else if (xFragment instanceof VShareFragment) {
				CmmobiClickAgent.onEvent(getContext(), "import", "2");
			}
			break;
			}
		default:
			break;
		}
	}*/
	
	/*@Override
	public void onLongClick(int id, View view) {
		Log.d(TAG, "onLongClick id="+id);
		switch (id) {
		case 1://图片
			break;
		case 2://视频
			break;
		case 3://录音
			break;
		case 4:{//便签
			startRecoder();
			ZoneBaseFragment fragment = FragmentHelper.getInstance((FragmentActivity)getContext()).getZoneBaseFragment();
			XFragment xFragment = fragment.getXFragment();
			HashMap<String,String> map = new HashMap<String, String>();
			if (xFragment instanceof MyZoneFragment) {
				map.put("label", "1");
				map.put("label2", "1");
				CmmobiClickAgent.onEvent(getContext(), "voice_note", map);
			} else if (xFragment instanceof VShareFragment) {
				map.put("label", "3");
				map.put("label2", "1");
				CmmobiClickAgent.onEvent(getContext(), "voice_note", map);
			}
			break;}
		case 5:{
			break;
			}
		default:
			break;
		}
		
	}

	@Override
	public void onTouch(int id, View view, MotionEvent event) {
		switch (id) {
		case 4:{//便签
			onNoteBtnTouch(view,event);
			break;}
		default:
			break;
		}
		
	}*/

	@Override
	protected void onDetachedFromWindow() {
		// TODO Auto-generated method stub
		super.onDetachedFromWindow();
		Log.d(TAG,"onDetachedFromWindow in");
		LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(mMessageReceiver);
	}

	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		init();
	}

	public void onHiddenChanged(boolean isHidden) {
		if(isHidden){
			setClickable(false);
		}else{
			setClickable(true);
		}
	}
	
	private ExtAudioRecorder ear;
	private boolean isLongPressed = false;
	private boolean isOnRecorderButton = false;
	private CountDownPopupWindow shortRecView;
	private boolean isRecogniseDone = false;
	private HashMap<String, String> audioTextMap = new HashMap<String, String>();// audioID-识别出来的文字
	private HashMap<String, Boolean> audioDoneMap = new HashMap<String, Boolean>();// audioID-是否录音完成
	private HashMap<String, Boolean> audioRecogniseMap = new HashMap<String,Boolean>();// audioID-是否语音识别完成
	private HashMap<String, Boolean> audioCreatedMap = new HashMap<String,Boolean>();// audioID-是否语音开始
	
	private void startRecoder() {
		if(!LookLookActivity.isSdcardMountedAndWritable()) {
			Prompt.Alert(getContext(), "Sdcard不可用，无法录音");
			return;
		}
		
		String audioID = DiaryController.getNextUUID();
		String RECORD_FILE_DIR =  Constant.SD_STORAGE_ROOT + "/" + ActiveAccount.getInstance(getContext()).getLookLookID() + "/audio";
		ear.setHandler(handler);
		ear.start(audioID,RECORD_FILE_DIR, false, 3, true);
		audioCreatedMap.put(audioID, true);
//		isRecording = true;
		isLongPressed = true;
		isOnRecorderButton = true;
		isRecogniseDone = false;
		
//		menu.setEnableMenuClick(true);
		if (shortRecView == null) {
			shortRecView = new CountDownPopupWindow((Activity)getContext());
		}
		shortRecView.show();
		acquireWakeLock();
	}

	@Override
	public boolean handleMessage(Message msg) {
		// TODO Auto-generated method stub
		switch(msg.what) {
		case ExtAudioRecorder.HANDLER_AUDIO_RECORDER_DONE:
			String audioID = (String)msg.obj;
			audioDoneMap.put(audioID, true);
			boolean isRecogniseDone = (audioRecogniseMap.get(audioID) != null);
			Log.d(TAG,"isRecogniseDong = " + isRecogniseDone + " isOnRecorderButton = " + isOnRecorderButton);
			if ((!ExtAudioRecorder.CheckPlugin() || isRecogniseDone)) {
				addVoiceNote(audioID);
			}
			break;
		case QISR_TASK.HANDLER_QISR_RESULT_ADD:
			if (msg != null && msg.obj != null) {
				Bundle data = msg.getData();
				String audioid = data.getString("audioid");
				audioTextMap.put(audioid, ZStringUtils.nullToEmpty(audioTextMap.get(audioid)) + (String)msg.obj);
			}
			break;
		case QISR_TASK.HANDLER_QISR_RESULT_CLEAN:
			if (msg != null && msg.obj != null) {
				Bundle data = msg.getData();
				String audioid = data.getString("audioid");
				audioTextMap.put(audioid, "");
			}
			break;
		case QISR_TASK.HANDLER_QISR_RESULT_DONE:
			Bundle data = msg.getData();
			String audioid = data.getString("audioid");
			audioRecogniseMap.put(audioid, true);
			Log.d(TAG,"HANDLER_QISR_RESULT_DONE isLongClick = " + isLongPressed + " isOnRecorderButton = " + isOnRecorderButton);
			if (isLongPressed) {
				return false;
			}
			if (audioDoneMap.get(audioid) != null) {
				addVoiceNote(audioid);
			}
			break;
		case DiaryController.DIARY_REQUEST_DONE:
			if (msg != null) {
				Log.d(TAG,"DiaryController.DIARY_REQUEST_DONE in");
				MyDiary diary = ((DiaryWrapper) msg.obj).diary;
				MyDiary localDiary = DiaryManager.getInstance().findMyDiaryByUUID(diary.diaryuuid);
				ZLog.printObject(diary);
				ZLog.e("**********************************");
				ZLog.printObject(diary.attachs.levelattach);
				ZLog.e("**********************************");
				ZLog.printObject(diary.attachs.attach);
//				setCurrent();
				String diaryUUID = diary.diaryuuid;
				ArrayList<MyDiaryList> diaryList = new ArrayList<MyDiaryList>();
				
				MyDiaryList myDiaryList = DiaryManager.getInstance().findDiaryGroupByUUID(diaryUUID);
				diaryList.add(myDiaryList);
				
				//  日记已经全部生成完成，后面做界面跳转相关操作。
				DiaryManager.getInstance().setDetailDiaryList(diaryList, 0);
				Intent intent = new Intent(getContext(), DiaryPreviewActivity.class);
				intent.putExtra(DiaryPreviewActivity.INTENT_ACTION_DIARY_UUID, diaryUUID);
				getContext().startActivity(intent);

			}
			break;
		case TickUpHelper.HANDLER_FLAG_TICK_UP:
			int curTime = (Integer)msg.obj;
			Log.d(TAG,"TickUpHelper.HANDLER_FLAG_TICK_UP + curTime = " + curTime);
			if (isLongPressed) {
				shortRecView.updateTime(30 - curTime);
			}
			
			break;
		case TickUpHelper.HANDLER_FLAG_TICK_STOP:
			Log.d(TAG,"TickUpHelper.HANDLER_FLAG_TICK_STOP");
			stopShortRecoder();
			break;
		}
		return false;
	}
	
	public void stopShortRecoder() {
		isLongPressed = false;
//		menu.setEnableMenuClick(false);
		if (ear != null && ear.isRecording) {
			ear.stop();
		}
		if (shortRecView != null) {
			shortRecView.dismiss();
		}
		releaseWakeLock();
	}

	public void onNoteBtnTouch(View view, MotionEvent event) {
		// TODO Auto-generated method stub
		if (isLongPressed) {
			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				Log.d(TAG,"ACTION_DOWN");
				break;
			case MotionEvent.ACTION_UP:
				Log.d(TAG,"ACTION_UP");
//				menu.hide();
				TickUpHelper.getInstance(handler).stop(2);
				break;
			case MotionEvent.ACTION_MOVE:
				Log.d(TAG,"ACTION_MOVE");
				if (event.getY() < 0) {
					isOnRecorderButton = false;
				} else {
					isOnRecorderButton = true;
				}
				break;
			}
		}
	}

	private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			int type;
			String content = "";
			Message msg = new Message();
			// Get extra data included in the Intent
			if (ExtAudioRecorder.AUDIO_RECORDER_MSG.equals(intent
					.getAction())) {
				type = intent.getIntExtra("type", 0);
				Log.d(TAG,"onReceive type = " + type);
				if (type == ExtAudioRecorder.HANDLER_AUDIO_RECORDER_DONE) {
					content = intent.getStringExtra("content");
				}
				if (audioCreatedMap.get(content) == null) {// 在主页界面未创建该语音
					return;
				}
				
				msg.what = type;
				msg.obj = content;
			} else if (QISR_TASK.QISR_RESULT_MSG.equals(intent.getAction())) {
				type = intent.getIntExtra("type", 0);
			    String message = intent.getStringExtra("content");
			    String audioID = intent.getStringExtra("audioID");
			    if (audioCreatedMap.get(audioID) == null) {// 在主页界面未创建该语音
					return;
				}
			    
			    Bundle b = new  Bundle();
			    b.putString("msg", message);
			    b.putString("audioid", audioID);
			    msg.setData(b);
			    Log.d(TAG, "Got message: " + message);
			    msg.what = type;
			    msg.obj = message;
			}

			handler.sendMessage(msg);
		}
	};
	
	private void addVoiceNote(String attachUUID) {
		String content = ZStringUtils.nullToEmpty(audioTextMap.get(attachUUID));
		String diaryUUID = DiaryController.getNextUUID();
		String audioPath = DiaryController.getAudioPath(attachUUID);
		if ("".equals(ZStringUtils.nullToEmpty(content))) {
			DiaryController.getInstanse().includeDiary(handler, diaryUUID, attachUUID, audioPath, GsonProtocol.ATTACH_TYPE_VOICE, GsonProtocol.SUFFIX_MP4, content,"",GsonProtocol.EMPTY_VALUE,CommonInfo.getInstance().getLongitude(),CommonInfo.getInstance().getLatitude(),DiaryController.getPositionString1(),"", false,FileOperate.RENAME,"");
		} else {
			DiaryController.getInstanse().includeDiary(handler, diaryUUID, attachUUID, audioPath, GsonProtocol.ATTACH_TYPE_VOICE_TEXT, GsonProtocol.SUFFIX_MP4, content,"",GsonProtocol.EMPTY_VALUE,CommonInfo.getInstance().getLongitude(),CommonInfo.getInstance().getLatitude(),DiaryController.getPositionString1(),"", false,FileOperate.RENAME,"");
		}
		
	}
	
	WakeLock wakeLock = null;  
	   //获取电源锁，保持该服务在屏幕熄灭时仍然获取CPU时，保持运行  
	   private void acquireWakeLock()  
	   {  
	       if (null == wakeLock)  
	       {  
	    	   wakeLock = ((PowerManager) getContext().getSystemService(getContext().POWER_SERVICE))
	                   .newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK
	                           | PowerManager.ON_AFTER_RELEASE, TAG);
	    	   
	           if (null != wakeLock)  
	           {  
	               wakeLock.acquire();  
	           }  
	       }  
	   }
	   
	 //释放设备电源锁  
	   private void releaseWakeLock()  
	   {  
	       if (null != wakeLock)  
	       {  
	           wakeLock.release();  
	           wakeLock = null;  
	       }  
	   }  
	
}
