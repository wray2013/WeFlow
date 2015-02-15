package com.cmmobi.looklook.activity;


import java.util.ArrayList;
import java.util.HashMap;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.os.Message;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.support.v4.content.LocalBroadcastManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import cn.zipper.framwork.core.ZActivity;
import cn.zipper.framwork.core.ZDialog;
import cn.zipper.framwork.io.file.ZFileSystem;

import com.cmmobi.looklook.Config;
import com.cmmobi.looklook.R;
import com.cmmobi.looklook.common.constant.Constant;
import com.cmmobi.looklook.common.gson.DiaryController;
import com.cmmobi.looklook.common.gson.DiaryController.DiaryWrapper;
import com.cmmobi.looklook.common.gson.DiaryController.FileOperate;
import com.cmmobi.looklook.common.gson.GsonProtocol;
import com.cmmobi.looklook.common.gson.GsonResponse3.MyDiaryList;
import com.cmmobi.looklook.common.utils.CmmobiClickAgentWrapper;
import com.cmmobi.looklook.common.utils.LowStorageChecker;
import com.cmmobi.looklook.common.view.AddExpressionView;
import com.cmmobi.looklook.common.view.CountDownPopupWindow;
import com.cmmobi.looklook.common.view.FriendsExpressionView;
import com.cmmobi.looklook.common.web.TackView;
import com.cmmobi.looklook.info.profile.ActiveAccount;
import com.cmmobi.looklook.info.profile.CommonInfo;
import com.cmmobi.looklook.info.profile.DiaryManager;
import com.cmmobi.looklook.prompt.Prompt;
import com.cmmobi.looklook.prompt.TickUpHelper;
import com.cmmobi.statistics.CmmobiClickAgent;
import com.cmmobivideo.utils.Mp4InfoUtils;
import com.iflytek.msc.ExtAudioRecorder;
import com.iflytek.msc.QISR_TASK;

public class CreateNoteActivity extends ZActivity implements OnItemClickListener, OnTouchListener, OnLongClickListener {
	
	private final String TAG = "CreateNoteActivity";
	private ImageView ivExpressionBtn = null;
	private AddExpressionView expressionView = null;
	private EditText edView = null;
	private TackView tackView = null;
	private ImageView ivRecorder = null;
	private boolean isLongPressed = false;
	private boolean isRecording = false;
	private String audioPath = null;
	private CountDownPopupWindow shortRecView;
	private ExtAudioRecorder ear;
	private Message msg;
	private TextView tvSaveBtn = null;
	private ImageView ivBackBtn = null;
	private TextView focusTextView = null;
	private FrameLayout translucentLayout = null;
	private boolean isOnRecorderButton = false;
	private int[] SOUND_ICONS = {R.drawable.yuyinbiaoqian_3,R.drawable.yuyinbiaoqian_4,R.drawable.yuyinbiaoqian_2};
	private boolean hasVoice = false;
	private boolean hasText = false;
	private boolean isDoneBtnClick = false;
	private LinearLayout bottomLayout = null;
	private DisplayMetrics dm = new DisplayMetrics();
	
	private String audioID = "";
	private boolean hasVoiceBefor = false;
	private boolean isRecogniseChanged = false;
	/*private HashMap<String, String> audioTextMap = new HashMap<String, String>();// audioID-识别出来的文字
	private HashMap<String, Boolean> audioDoneMap = new HashMap<String, Boolean>();// audioID-是否录音完成
	private HashMap<String, Boolean> audioRecogniseMap = new HashMap<String,Boolean>();// audioID-是否语音识别完成
	private HashMap<String, Boolean> audioDiaryBeDone = new HashMap<String, Boolean>();// 该便签是否需创建
	private HashMap<String, String> audioIdPathMap = new HashMap<String, String>();// audioID-audioPath
*/	
	private class AudioWrapper {
		String audioID;
		String audioPath;
		boolean recogniseDone;
		boolean audioDone;
		boolean audioDiaryOK;
		String audioContent = "";
	}
	
	private HashMap<String,AudioWrapper> audioMap = new HashMap<String, AudioWrapper>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_create_note);
		ivExpressionBtn = (ImageView) findViewById(R.id.iv_biaoqing_input);
		ivExpressionBtn.setOnClickListener(this);
		
		edView = (EditText) findViewById(R.id.et_word_input);
		expressionView = new AddExpressionView(this,edView);
		expressionView.setOnclickListener(this);
		edView.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View arg0, MotionEvent arg1) {
				// TODO Auto-generated method stub
				Log.d(TAG,"edView onTouch in");
				switch (arg1.getAction()) {
				case MotionEvent.ACTION_DOWN:
					if (isLongPressed) {
						return true;
					}
					break;
				case MotionEvent.ACTION_UP:
					expressionView.hideExpressionView();
					ivExpressionBtn.setImageResource(R.drawable.btn_biaoqing_input);
				}
				return false;
			}
		});
		
		edView.addTextChangedListener(mTextWatcher);
		
		tvSaveBtn = (TextView) findViewById(R.id.tv_save);
		ivBackBtn = (ImageView) findViewById(R.id.iv_back);
		tvSaveBtn.setOnClickListener(this);
		ivBackBtn.setOnClickListener(this);
		ivBackBtn.setOnLongClickListener(this);
		
		changeDoneBtnState();
		
		tackView = (TackView) findViewById(R.id.ll_tackview);
		tackView.setBackground(R.drawable.btn_yuyinbiaoqian);
		tackView.setSoundIcons(SOUND_ICONS);
		tackView.setOnLongClickListener(this);
		tackView.setOnClickListener(this);
		
		ivRecorder = (ImageView) findViewById(R.id.iv_recorder);
		ivRecorder.setOnTouchListener(this);
		ivRecorder.setOnLongClickListener(this);
		
		shortRecView = new CountDownPopupWindow(this);
		
		LocalBroadcastManager.getInstance(this).registerReceiver(
				mMessageReceiver,
				new IntentFilter(ExtAudioRecorder.AUDIO_RECORDER_MSG));
		LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
			      new IntentFilter(QISR_TASK.QISR_RESULT_MSG));
		ear = ExtAudioRecorder.getInstanse(false, 2, 1);
		
		translucentLayout = (FrameLayout) findViewById(R.id.fl_translucent_layout);
		
		bottomLayout = (LinearLayout) findViewById(R.id.ll_activity_create_note);
		dm = getResources().getDisplayMetrics();
		// 隐藏键盘
		focusTextView = (TextView) findViewById(R.id.text_notuse);
		focusTextView.requestFocus();
	}
	
	
	@Override
	public boolean handleMessage(Message msg) {
		// TODO Auto-generated method stub
		switch (msg.what) {
		case ExtAudioRecorder.HANDLER_AUDIO_RECORDER_DONE:
			String audioID = (String)msg.obj;
			AudioWrapper audioWrapper = audioMap.get(audioID);
			boolean isRecogniseDone = audioWrapper.recogniseDone;
			audioWrapper.audioDone = true;
			
			tackView.setVisibility(View.VISIBLE);
			tackView.setLocalAudio(audioWrapper.audioPath);
			Log.d(TAG,"isRecogniseDong = " + isRecogniseDone);
			Log.d(TAG,"duration = " + new Mp4InfoUtils(audioWrapper.audioPath).totaltime);
			if ((!ExtAudioRecorder.CheckPlugin() || isRecogniseDone)) {
				if (audioWrapper.audioDiaryOK) {
					createNewNote(audioID);
				}
			} else {
				tvSaveBtn.setEnabled(false);
			}
			
			break;
		case QISR_TASK.HANDLER_QISR_RESULT_ADD:
			if (msg != null && msg.obj != null) {
				Bundle data = msg.getData();
				String audioid = data.getString("audioid");
				AudioWrapper audioWrapper1 = audioMap.get(audioid);
				audioWrapper1.audioContent += (String)msg.obj;
			}
			break;
		case QISR_TASK.HANDLER_QISR_RESULT_CLEAN:
			if (msg != null && msg.obj != null) {
				Bundle data = msg.getData();
				String audioid = data.getString("audioid");
				AudioWrapper audioWrapper2 = audioMap.get(audioid);
				audioWrapper2.audioContent = "";
			}
			break;
		case QISR_TASK.HANDLER_QISR_RESULT_DONE:
			Bundle data = msg.getData();
			String audioid = data.getString("audioid");
			AudioWrapper audioWrapper3 = audioMap.get(audioid);
			audioWrapper3.recogniseDone = true;
			if (hasVoiceBefor) {
				edView.setText(audioWrapper3.audioContent);
				edView.setSelection(audioWrapper3.audioContent.length());
			} else {
				insertText(audioWrapper3.audioContent);
			}
		
			Log.d(TAG,"HANDLER_QISR_RESULT_DONE isLongClick = " + isLongPressed + " isOnRecorderButton = " + isOnRecorderButton);
			if (isLongPressed) {
				return false;
			}
			if (audioWrapper3.audioDone) {
				if (audioWrapper3.audioDiaryOK) {
					createNewNote(audioid);
				}
				tvSaveBtn.setEnabled(true);
			}
			break;
		case DiaryController.DIARY_REQUEST_DONE:
			Log.d(TAG,"DiaryController.DIARY_REQUEST_DONE in");
			ZDialog.dismiss();
			if (!isDoneBtnClick) {
				break;
			}
			
			String diaryUUID = "";
			if (msg != null) {
				DiaryWrapper diaryWrapper = (DiaryWrapper) msg.obj;
				if (diaryWrapper == null) {
					Log.d(TAG,"日记返回为空");
					return false;
				}
				diaryUUID = diaryWrapper.diary.diaryuuid;
			}
			ArrayList<MyDiaryList> diaryList = new ArrayList<MyDiaryList>();
			
			MyDiaryList myDiaryList = DiaryManager.getInstance().findDiaryGroupByUUID(diaryUUID);
			diaryList.add(myDiaryList);
			
			//  日记已经全部生成完成，后面做界面跳转相关操作。
			DiaryManager.getInstance().setDetailDiaryList(diaryList, 0);
			Intent intent = new Intent(this, DiaryPreviewActivity.class);
			intent.putExtra(DiaryPreviewActivity.INTENT_ACTION_DIARY_UUID, diaryUUID);
			startActivity(intent);
			
			finish();
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
			stopShortRecord();
			break;
		}
		return false;
	}
	
	private void insertText(String text) {
		edView.getText().insert(edView.getSelectionStart(), text);
		edView.requestFocus();
	}
	
	private void createNewNote(String audioid) {
		if (isRecogniseChanged) {
			CmmobiClickAgent.onEvent(this, "edit_content");
		}
		AudioWrapper audioWrapper = audioMap.get(audioid);
		String audioPath = audioWrapper.audioPath;
		String diaryUUID = DiaryController.getNextUUID();
		String attachUUID = DiaryController.getNextUUID();
		String attachType = "";
		if ("".equals(edView.getText().toString())) {
			attachType = GsonProtocol.ATTACH_TYPE_VOICE;
		} else {
			attachType = GsonProtocol.ATTACH_TYPE_VOICE_TEXT;
		}
		String content = new String(edView.getText().toString());
		DiaryController.getInstanse().includeDiary(handler, diaryUUID, attachUUID, audioPath, attachType, GsonProtocol.SUFFIX_MP4, content,"",GsonProtocol.EMPTY_VALUE,CommonInfo.getInstance().getLongitude(),CommonInfo.getInstance().getLatitude(),DiaryController.getPositionString1(),"", false,FileOperate.RENAME,"");
	}
	
	private void stopShortRecord() {
		ear.stop();
		isRecording = false;
		isLongPressed = false;
		shortRecView.dismiss();
		
		translucentLayout.setVisibility(View.GONE);
		releaseWakeLock();
		edView.setEnabled(true);
	
		if (hasVoice) {
			hasVoiceBefor = true;
		}
		
		hasVoice = true;
		changeDoneBtnState();
	}
	
	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		if (ear != null && ear.isRecording) {
			isLongPressed = false;
			TickUpHelper.getInstance(handler).stop(0);
			ear.stop();
			CmmobiClickAgent.onEventEnd(this, "record_voice");
			if (shortRecView != null) {
				shortRecView.dismiss();
			}
		}
		super.onStop();
	}
	
	private void changeDoneBtnState() {
		if ((!hasVoice && ((!hasText) || edView.getText().toString().replace(" ", "").replace("\n", "").length() == 0)) || edView.getText().length() > Config.MAX_NOTE_TEXT_LENGTH ) {
			tvSaveBtn.setClickable(false);
			tvSaveBtn.setEnabled(false);
		} else {
			tvSaveBtn.setClickable(true);
			tvSaveBtn.setEnabled(true);
		}
		
		if (hasVoice && isKeyBoradShown()) {
			isRecogniseChanged = true;
		}
	}
	

	@Override
	public void onClick(View view) {
		// TODO Auto-generated method stub
		Log.d(TAG,"viewid = " + view.getId());
		switch(view.getId()) {
		case R.id.iv_biaoqing_input:
			View exview = expressionView.getExpressionView();
			if (View.GONE != exview.getVisibility()) {
				expressionView.hideExpressionView();
				ivExpressionBtn.setImageResource(R.drawable.btn_biaoqing_input);
			} else {
				expressionView.showExpressionView();
				ivExpressionBtn.setImageResource(R.drawable.btn_wenzi_input);
			}
			break;
		case R.id.ll_tackview:
			Log.d(TAG,"ll_biezhen click");
			tackView.playAudio(audioPath);
			break;
		case R.id.iv_back:
			expressionView.hideExpressionViewOnly();
			expressionView.hideKeyboard();
			handler.postDelayed(new Runnable() {
				
				@Override
				public void run() {
					// TODO Auto-generated method stub
					finish();
				}
			}, 100);
			break;
		case R.id.tv_save:
			Log.d(TAG,"tv_save click");
			if (isDoneBtnClick) {
				break;
			}
			isDoneBtnClick = true;
			
			ZDialog.show(R.layout.progressdialog, false, true, this,false);
			String diaryUUID = DiaryController.getNextUUID();
			String attachUUID = DiaryController.getNextUUID();
			if (hasVoice) {
				AudioWrapper audioWrapper = audioMap.get(audioID);
				audioWrapper.audioDiaryOK = true;
				String attachType = "";
				if ("".equals(edView.getText().toString())) {
					attachType = GsonProtocol.ATTACH_TYPE_VOICE;
				} else {
					attachType = GsonProtocol.ATTACH_TYPE_VOICE_TEXT;
				}
				if (audioWrapper.audioDone) {
					DiaryController.getInstanse().includeDiary(handler, diaryUUID, attachUUID, audioPath, attachType, GsonProtocol.SUFFIX_MP4, edView.getText().toString(),"",GsonProtocol.EMPTY_VALUE,CommonInfo.getInstance().getLongitude(),CommonInfo.getInstance().getLatitude(),DiaryController.getPositionString1(),"", false,FileOperate.RENAME,"");
				}
				if (isRecogniseChanged) {
					CmmobiClickAgent.onEvent(this, "edit_content");
				}
			} else {
				DiaryController.getInstanse().includeDiary(handler, diaryUUID, attachUUID, "", GsonProtocol.ATTACH_TYPE_TEXT, "", edView.getText().toString(),"",GsonProtocol.EMPTY_VALUE,CommonInfo.getInstance().getLongitude(),CommonInfo.getInstance().getLatitude(),DiaryController.getPositionString1(),"", false,FileOperate.RENAME,"");
			}
			break;
		}
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub
		insertText(arg1.getTag().toString());
		CmmobiClickAgentWrapper.onEvent(this, "edit_emote", ""+FriendsExpressionView.getExpIndex(arg1.getTag().toString()));
		if (!edView.isFocused()) {
			edView.requestFocus();
		}
	}
	

	@Override
	public boolean onTouch(View view, MotionEvent event) {
		// TODO Auto-generated method stub
		switch(view.getId()) {
		case R.id.iv_recorder:
		if (isLongPressed) {
			int action = event.getAction();
			msg = handler.obtainMessage();
			if (action == MotionEvent.ACTION_DOWN) {
				Log.d(TAG,"ACTION_DOWN");
			} else if (action == MotionEvent.ACTION_UP) {
				Log.d(TAG,"ACTION_UP");
				TickUpHelper.getInstance(handler).stop(2);
				CmmobiClickAgent.onEventEnd(this, "record_voice");
			} else if(action == MotionEvent.ACTION_MOVE){
				if (event.getY() < 0) {
					isOnRecorderButton = false;
				} else {
					isOnRecorderButton = true;
				}
			}
		} else {
			int[] location = new int[2];
			bottomLayout.getLocationOnScreen(location);
			int y = location[1];
			Log.d(TAG,".... onTouch in y = " + y + " height = " + bottomLayout.getHeight() + " screenHeight = " + dm.heightPixels);
			if (y < dm.heightPixels - bottomLayout.getHeight()) {
				expressionView.hideExpressionViewOnly();
				expressionView.hideKeyboard();
				return true;
			}
		}
		break;
		}
		return false;
	}
	
	private boolean isKeyBoradShown() {
		int[] location = new int[2];
		bottomLayout.getLocationOnScreen(location);
		int y = location[1];
		if (y < dm.heightPixels - bottomLayout.getHeight()) {
			if (!expressionView.isExpressionShown()) {
				return true;
			}
		}
		return false;
	}
	
	@Override
	public boolean onLongClick(View v) {
		// TODO Auto-generated method stub
		Log.d(TAG,"onLongClick viewId = " + v.getId());
		switch(v.getId()) {
		case R.id.iv_recorder:
			
			if (audioPath != null) {
				AudioWrapper audioWrapper = audioMap.get(audioID);
				boolean isRecogniseDone = audioWrapper.recogniseDone;
				boolean isRecordDone = audioWrapper.audioDone;
				audioWrapper.audioDiaryOK = true;
				if (isRecordDone) {
					createNewNote(audioID);
				}
				isRecogniseChanged = false;
			}
			if (LowStorageChecker.check(this, null, R.string.cancel_record_audio)) {
				expressionView.hideKeyboard();
				if(!LookLookActivity.isSdcardMountedAndWritable()) {
					Prompt.Alert(this, "Sdcard不可用，无法录音");
					break;
				}
				
				audioID = DiaryController.getNextUUID();
				
				String RECORD_FILE_DIR =  Constant.SD_STORAGE_ROOT + "/" + ActiveAccount.getInstance(this).getLookLookID() + "/audio";
				audioPath = Environment.getExternalStorageDirectory() + RECORD_FILE_DIR + "/" + audioID + ".mp4" ;
				AudioWrapper audioWrapper = new AudioWrapper();
				audioWrapper.audioID = audioID;
				audioWrapper.audioPath = audioPath;
				audioMap.put(audioID, audioWrapper);
//				handler.sendEmptyMessage(HANDLER_RECORD_DURATION_UPDATE_MSG);
				ear.setHandler(handler);
				ear.start(audioID,RECORD_FILE_DIR, false, 3, true);
				
				CmmobiClickAgent.onEventBegin(this, "record_voice");
				
				isRecording = true;
				isLongPressed = true;
				
				shortRecView.show();
				translucentLayout.setVisibility(View.VISIBLE);
				translucentLayout.requestFocus();
				edView.setEnabled(false);
				
				acquireWakeLock();
			}
			break;
		case R.id.ll_tackview:
			Log.d(TAG,"ll_biezhen onLongClick in");
			View view = LayoutInflater.from(this).inflate(
					R.layout.delete_short_audio,
					null);
			final PopupWindow popupWindow = new PopupWindow(view,
					LayoutParams.WRAP_CONTENT,
					LayoutParams.WRAP_CONTENT);
			popupWindow.setBackgroundDrawable(new BitmapDrawable());
			popupWindow.setFocusable(true);
			popupWindow.setTouchable(true);
			popupWindow.setOutsideTouchable(true);

			int[] location = new int[2];
			v.getLocationInWindow(location);
			
			int w = View.MeasureSpec.makeMeasureSpec(0,View.MeasureSpec.UNSPECIFIED);  
	    	int h = View.MeasureSpec.makeMeasureSpec(0,View.MeasureSpec.UNSPECIFIED);  
	    	view.measure(w, h);  
	    	int popWidth = view.getMeasuredWidth();
	    	int popHeight = view.getMeasuredHeight();
	    	
	    	int vw = View.MeasureSpec.makeMeasureSpec(0,View.MeasureSpec.UNSPECIFIED);  
	    	int vh = View.MeasureSpec.makeMeasureSpec(0,View.MeasureSpec.UNSPECIFIED);
	    	v.measure(vw, vh);
	    	int viewWidth = v.getMeasuredWidth();
	    	
			popupWindow.showAtLocation(v, 0, location[0] + (viewWidth - popWidth)/2, location[1] - popHeight);
			view.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Log.d(TAG,"tackview sound delete onclick in");
					popupWindow.dismiss();
					tackView.setVisibility(View.INVISIBLE);
					ZFileSystem.delFile(audioPath);
					tackView.stop();
					hasVoice = false;
					changeDoneBtnState();
				}
			});
			break;
		case R.id.iv_back:
			startActivity(new Intent(this,LookLookActivity.class));
			finish();
			break;
		}
		
		return false;
	}
	
	private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			int type;
			Message msg = new Message();
			String content = "";
			// Get extra data included in the Intent
			if (ExtAudioRecorder.AUDIO_RECORDER_MSG.equals(intent
					.getAction())) {
				type = intent.getIntExtra("type", 0);
				if (type == ExtAudioRecorder.HANDLER_AUDIO_RECORDER_DONE) {
					long duration = intent.getLongExtra("audioduration",0);
					Bundle b = new  Bundle();
				    b.putLong("audioduration", duration);
				    msg.setData(b);
				    content = intent.getStringExtra("content");
					Log.d(TAG,"onReceive content = " + duration);
				}
				msg.what = type;
				msg.obj = content;
			} else if (QISR_TASK.QISR_RESULT_MSG.equals(intent.getAction())){
				type = intent.getIntExtra("type", 0);
			    String message = intent.getStringExtra("content");
			    String audioID = intent.getStringExtra("audioID");
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
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
		super.onDestroy();
	}
	
	
	private TextWatcher mTextWatcher = new TextWatcher() {  
		
		private CharSequence temp;
        private boolean isEdit = true;
        private int selectionStart ;
        private int selectionEnd ; 
 
        public void afterTextChanged(Editable s) { 
	       	if (s == null || "".equals(s.toString())) {
	       		hasText = false;
	        } else {
	           	hasText = true;
	        }
	       	changeDoneBtnState();
       }  
 
       public void beforeTextChanged(CharSequence s, int start, int count,  
               int after) {
       }  
 
       public void onTextChanged(CharSequence s, int start, int before,  
               int count) { 
    	   Log.d(TAG,"onTextChanged in");
       }  
   };
   
   WakeLock wakeLock = null;  
   //获取电源锁，保持该服务在屏幕熄灭时仍然获取CPU时，保持运行  
   private void acquireWakeLock()  
   {  
       if (null == wakeLock)  
       {  
    	   wakeLock = ((PowerManager) getSystemService(POWER_SERVICE))
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
