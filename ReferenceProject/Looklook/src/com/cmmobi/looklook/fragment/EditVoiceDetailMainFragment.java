package com.cmmobi.looklook.fragment;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baidu.platform.comapi.basestruct.GeoPoint;
import com.cmmobi.looklook.R;
import com.cmmobi.looklook.activity.EditVoiceDetailActivity;
import com.cmmobi.looklook.activity.HomeActivity;
import com.cmmobi.looklook.common.constant.Constant;
import com.cmmobi.looklook.common.gson.GsonResponse2.diaryAttach;
import com.cmmobi.looklook.common.utils.CmmobiClickAgentWrapper;
import com.cmmobi.looklook.common.utils.EffectsDownloadUtil;
import com.cmmobi.looklook.common.view.CountDownView;
import com.cmmobi.looklook.info.profile.ActiveAccount;
import com.cmmobi.looklook.info.profile.TimeHelper;
import com.cmmobi.looklook.prompt.Prompt;
import com.cmmobivideo.utils.PluginUtils;
import com.iflytek.msc.ExtAudioRecorder;
import com.iflytek.msc.QISR_TASK;

import effect.XEffectMediaPlayer;
import effect.XEffects;

public class EditVoiceDetailMainFragment extends Fragment implements OnClickListener, OnLongClickListener{

	private static final String TAG = "EditVoiceDetailMainFragment";
	private EditVoiceDetailActivity mActivity;
    private RelativeLayout mTagLayout = null;
    private RelativeLayout mPosLayout = null;
    
	private LinearLayout editVoiceLayout = null;
	public RelativeLayout mBeautiSoundLayout = null;
	
	private LinearLayout editLongRecLayout = null;
	private LinearLayout editShortRecLayout = null;
	
	private ImageView recordBtn = null;
    private Message msg;
    private boolean isOnRecorderButton = false;
    public static final int SEND_SOUND_RECORD_MSG = 0;
	public static final int STOP_SOUND_RECORD_MSG = 1;
	private final int HANDLER_RECORD_DURATION_UPDATE_MSG = 0x87654002;
//	private VolumeStateView vsv;.
	private CountDownView shortRecView;
	private ExtAudioRecorder ear;
	private String soundText = "";
	private long recordDuration = 0;
	private boolean isRecording = false;
	private TextView tvBeautifulSound = null;
	private String beautifulSoundText = "";
	private final int HANDLER_RECORD_STOP_TIME_DELAY = 0x87654008;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view = inflater.inflate(R.layout.fragment_edit_voice_detail_main, container, false);
		mTagLayout = (RelativeLayout) view.findViewById(R.id.rl_edit_voice_detail_tag);
		mTagLayout.setOnClickListener(this);
		
		mPosLayout = (RelativeLayout) view.findViewById(R.id.rl_edit_voice_detail_position);
		mPosLayout.setOnClickListener(this);
		
		editVoiceLayout = (LinearLayout) view.findViewById(R.id.ll_edit_voice);
		recordBtn = (ImageView) view.findViewById(R.id.iv_edit_media_record_btn);
		
		editLongRecLayout = (LinearLayout) view.findViewById(R.id.ll_edit_long_rec_detail);
		editShortRecLayout = (LinearLayout) view.findViewById(R.id.ll_edit_short_rec_detail);
		
		boolean isLongRecord = false;
		if (mActivity.getMyDiary() != null) {
			diaryAttach[] attachs = mActivity.getMyDiary().attachs;
			for (diaryAttach attach:attachs) {
				if ("2".equals(attach.attachtype) && "1".equals(attach.attachlevel)) {
					isLongRecord = true;
				} else if ("2".equals(attach.attachtype) && "0".equals(attach.attachlevel)) {
					recordBtn.setImageResource(R.drawable.btn_activity_edit_photo_detail_rerecode);
				}
			}
		}
		if (isLongRecord) {
			editVoiceLayout.setOnClickListener(this);
		} else {
			editShortRecLayout.setVisibility(View.VISIBLE);
			editVoiceLayout.setClickable(false);
		}
		
		mBeautiSoundLayout = (RelativeLayout)view.findViewById(R.id.rl_edit_media_beautifysound);
		mBeautiSoundLayout.setOnClickListener(this);
		tvBeautifulSound = (TextView) view.findViewById(R.id.tv_edit_media_beautifysound);
		if (!"".equals(beautifulSoundText)) {
			tvBeautifulSound.setText(beautifulSoundText);
		}
		
		if (!mActivity.hasShortSound) {
			mBeautiSoundLayout.setBackgroundResource(R.color.black_mask_transparent);
		}
		
		mActivity.positionView = (TextView) view.findViewById(R.id.tv_edit_voice_position);
		if (mActivity.getMyDiary() != null && mActivity.getMyDiary().position != null && !"".equals(mActivity.getMyDiary().position)) {
			mActivity.positionStr = mActivity.getMyDiary().position;
		}
		if (mActivity.positionStr != null) {
			mActivity.positionView.setText(mActivity.positionStr);
		}
		
		mActivity.tagTextView = (TextView) view.findViewById(R.id.tv_edit_voice_detail_tag);
		mActivity.tagStr = "";
		if (mActivity.getMyDiary() != null && mActivity.getMyDiary().tags != null && mActivity.getMyDiary().tags.length > 0) {
			for (int i = 0; i < mActivity.getMyDiary().tags.length; i++) {
				mActivity.tagStr +=  mActivity.getMyDiary().tags[i].name;
				if (i != mActivity.getMyDiary().tags.length - 1) {
					mActivity.tagStr += "，";
				}
			}
		}
		
		if (mActivity.tagStr != null) {
			mActivity.tagTextView.setText(mActivity.tagStr);
		}
		
		mActivity.mainLayout.setVisibility(View.GONE);
		mActivity.detailMainLayout.setVisibility(View.VISIBLE);
		shortRecView = new CountDownView(mActivity);
		
		recordBtn.setOnLongClickListener(this);
		
		recordBtn.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (mActivity.isLongPressed) {
					int action = event.getAction();
					msg = handler.obtainMessage();
					if (action == MotionEvent.ACTION_DOWN) {
						Log.d(TAG,"ACTION_DOWN");
					} else if (action == MotionEvent.ACTION_UP) {
						Log.d(TAG,"ACTION_UP");
						msg.what = STOP_SOUND_RECORD_MSG;
						handler.sendMessage(msg);
					} else if(action == MotionEvent.ACTION_MOVE){
						Log.d(TAG,"ACTION_MOVE");
	//					recordBtn.setImageResource(R.drawable.luyin_2);
						if (event.getY() < 0) {
							isOnRecorderButton = false;
						} else {
							isOnRecorderButton = true;
						}
						Log.e("isOnRecorderButton", isOnRecorderButton + "");
					}
				}
				return false;
			}
		});
		
		if (mActivity.hasShortSound) {
			recordBtn.setImageResource(R.drawable.btn_activity_edit_photo_detail_rerecode);
		}
		return view;
	}
	
	@Override
	public void onStart() {
		// TODO Auto-generated method stub
		
		super.onStart();
		
	}

	@Override
	public void onStop() {
		// TODO Auto-generated method stub
		msg = handler.obtainMessage();
		msg.what = STOP_SOUND_RECORD_MSG;
		handler.sendMessage(msg);
		
		super.onStop();
	}
	
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		LocalBroadcastManager.getInstance(mActivity).unregisterReceiver(mMessageReceiver);
		super.onDestroy();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		LocalBroadcastManager.getInstance(mActivity).registerReceiver(mMessageReceiver,
			      new IntentFilter(QISR_TASK.QISR_RESULT_MSG));
		LocalBroadcastManager.getInstance(mActivity).registerReceiver(
				mMessageReceiver,
				new IntentFilter(ExtAudioRecorder.AUDIO_RECORDER_MSG));
		ear = ExtAudioRecorder.getInstanse(false, 2, 1);
		
		super.onCreate(savedInstanceState);
	}
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()) {
		case R.id.rl_edit_voice_detail_tag:
			if (mActivity.isLongPressed) {
				break;
			}
			mActivity.goToPage(EditVoiceDetailActivity.FRAGMENT_TAG_VIEW, false);
			break;
		case R.id.rl_edit_voice_detail_position:
			if (mActivity.isLongPressed) {
				break;
			}
			mActivity.setMapViewVisibility(View.VISIBLE);
			mActivity.myPositionLayout.setVisibility(View.VISIBLE);
			mActivity.goToPage(EditVoiceDetailActivity.FRAGMENT_POS_VIEW, false);
			mActivity.getMapView().getController().setZoom(16);
			GeoPoint pt = new GeoPoint((int)(mActivity.myLoc.latitude* 1e6), (int)(mActivity.myLoc.longitude *  1e6));
			
			mActivity.getMapView().getController().setCenter(pt);
			mActivity.getMapView().refresh();
			Log.d(TAG,"rl_edit_photo_detail_position latitude = " + mActivity.myLoc.latitude + " longtitude = " + mActivity.myLoc.longitude);
			break;
		case R.id.rl_edit_media_beautifysound:
			if (mActivity.isLongPressed) {
				break;
			}
			if (mActivity.hasShortSound) {
				if (!EffectsDownloadUtil.getInstance(mActivity).checkEffects()) {
					if (mActivity.shortRecEffect == null) {
						mActivity.shortRecEffect = new XEffects();
					}
					mActivity.goToPage(EditVoiceDetailActivity.FRAGMENT_SOUND_VIEW, false);
				}
			}
			break;
		case R.id.ll_edit_voice:
			if (mActivity.isLongPressed) {
				break;
			}
			/*if (mActivity.mMediaPlayer.getStatus() == XEffectMediaPlayer.STATUS_PAUSE) {
				if (mActivity.audioMontageStartTime > 0.1) {
					mActivity.mMediaPlayer.seek(mActivity.audioMontageStartTime);
				}
				mActivity.mMediaPlayer.resume();
			} else {
				mActivity.mMediaPlayer.play();
				if (mActivity.audioMontageStartTime > 0.1) {
					mActivity.mMediaPlayer.seek(mActivity.audioMontageStartTime);
				}
			}
			mActivity.ivPlay.setImageResource(R.drawable.btn_edit_audio_pause);*/
			mActivity.goToPage(EditVoiceDetailActivity.FRAGMENT_MAIN_VIEW, false);
			break;
		}
	}
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);

		Log.e(TAG, "NearByFragment - onAttach");

		try {
			mActivity = (EditVoiceDetailActivity) activity;
			// onGotoPageListener = (OnGotoPageListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString() + " must implement OnGotoPageListener");
		}
	}
	
	public void processDelShortSoundMsg() {
		if (recordBtn != null) {
			recordBtn.setImageResource(R.drawable.btn_activity_edit_photo_detail_recode);
			mBeautiSoundLayout.setBackgroundResource(R.color.black_mask_transparent);
		}
	}
	
	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			int vol = 0;
			switch (msg.what) {
			case SEND_SOUND_RECORD_MSG:
				CmmobiClickAgentWrapper.onEvent(mActivity, "my_edit_de", "1");
				CmmobiClickAgentWrapper.onEventBegin(mActivity, "my_edit_de", "1");
				long currentTime = TimeHelper.getInstance().now();
				String audioID = String.valueOf(currentTime);
				String RECORD_FILE_DIR =  Constant.SD_STORAGE_ROOT + "/" + ActiveAccount.getInstance(mActivity).getLookLookID() + "/audio";
				mActivity.audioPath = Environment.getExternalStorageDirectory() + RECORD_FILE_DIR + "/" + audioID +  "/" + audioID + ".mp4" ;
				Log.d(TAG,"audioPath = " + mActivity.audioPath);
				mActivity.startAudioTime = currentTime;
				ear.start(mActivity, audioID,RECORD_FILE_DIR, false, 3, true);
//				vsv.showVolume(editVoiceLayout);
				isRecording = true;
				recordDuration = 0;
				handler.sendEmptyMessage(HANDLER_RECORD_DURATION_UPDATE_MSG);
				shortRecView.show(editVoiceLayout);
				
				break;
			case STOP_SOUND_RECORD_MSG:
				CmmobiClickAgentWrapper.onEventEnd(mActivity, "my_edit_de", "1");
				Log.d(TAG,"STOP_SOUND_RECORD_MSG in");
				long stoptime = TimeHelper.getInstance().now();
				long stopduration = stoptime - mActivity.startAudioTime;
				if (stopduration >= 2000) {
					stopShortRecord();
				} else {
					handler.sendEmptyMessageDelayed(HANDLER_RECORD_STOP_TIME_DELAY, 2000 - stopduration);
				}
				
				break;
			case HANDLER_RECORD_STOP_TIME_DELAY:
				stopShortRecord();
				break;
			case ExtAudioRecorder.HANDLER_AUDIO_RECORDER_DONE:
				Log.d(TAG,"HANDLER_AUDIO_RECORDER_DONE path = " + mActivity.audioPath);
				
				recordDuration = 0;
				long endTime = TimeHelper.getInstance().now();
				mActivity.audioDuration = endTime - mActivity.startAudioTime;
				Bundle b = msg.getData();
				long duration = 0;
				if(b!=null) {
					duration = b.getLong("audioduration");
					Log.d(TAG,"HANDLER_AUDIO_RECORDER_DONE audioduration = " + duration);
				}
				
				if (duration > 30000) {
					duration = 30000;
				}
				
				if (duration != 0) {
					mActivity.isAddSoundAttach = true;
					mActivity.isDeleteSound = false;
					mActivity.hasShortSound = true;
					mActivity.audioDuration = duration;
					if(mActivity.tackView!=null){
						mActivity.tackView.setVisibility(View.VISIBLE);
						mActivity.tackView.setPlaytime((duration + 999) / 1000 + "\"");
					}
					
					mActivity.setRecord(true);
					mActivity.edit.setHint("");
					recordBtn.setImageResource(R.drawable.btn_activity_edit_photo_detail_rerecode);
					
					mBeautiSoundLayout.setBackgroundResource(R.drawable.bg_activity_edit_photo_dark);
					mActivity.checkAuxiliaryAttachEmpty();
				} 
				break;
			case QISR_TASK.HANDLER_QISR_RESULT_CLEAN:
				Log.d(TAG,"HANDLER_QISR_RESULT_CLEAN");
				soundText = "";
				break;
			case QISR_TASK.HANDLER_QISR_RESULT_ADD:
				soundText += (String)msg.obj;
				Log.d(TAG,"HANDLER_QISR_RESULT_ADD");
				break;
			case QISR_TASK.HANDLER_QISR_RESULT_DONE:
				mActivity.addSoundText(soundText);
//				mActivity.distinguishLayout.setBackgroundResource(R.drawable.btn_edit_media_record_textinput);
//				mActivity.distinguishTv.setText(R.string.edit_media_record_hand_input);
				Log.d(TAG,"HANDLER_QISR_RESULT_DONE");
				break;
			case HANDLER_RECORD_DURATION_UPDATE_MSG:
				shortRecView.updateTime(30 - recordDuration);
				if (recordDuration >= 30) {
					isRecording = false;
					ear.stop();
					shortRecView.dismiss();
				}
				recordDuration++;
				if (isRecording) {
					handler.sendEmptyMessageDelayed(HANDLER_RECORD_DURATION_UPDATE_MSG, 1000);
				} else {
					recordDuration = 0;
				}
				break;
			default:
				break;
			}
		}
	};
	
	private void stopShortRecord() {
		ear.stop();
		isRecording = false;
		mActivity.isLongPressed = false;
		handler.removeMessages(HANDLER_RECORD_DURATION_UPDATE_MSG);
		shortRecView.dismiss();
		
		if (mActivity.hasShortSound) {
			recordBtn.setImageResource(R.drawable.btn_activity_edit_photo_detail_rerecode);
		} else {
			recordBtn.setImageResource(R.drawable.btn_activity_edit_photo_detail_recode);
		}
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
					Log.d(TAG,"onReceive content = " + duration);
				} else {
					content = intent.getStringExtra("content");
				}
				
				msg.what = type;
				msg.obj = content;
			} else if (QISR_TASK.QISR_RESULT_MSG.equals(intent.getAction())){
				type = intent.getIntExtra("type", 0);
			    String message = intent.getStringExtra("content");
			    Log.d(TAG, "Got message: " + message);
			    msg.what = type;
			    msg.obj = message;
			}

			handler.sendMessage(msg);
		}
	};

	@Override
	public boolean onLongClick(View view) {
		// TODO Auto-generated method stub
		switch(view.getId()) {
		case R.id.iv_edit_media_record_btn:
			if(!HomeActivity.isSdcardMountedAndWritable()) {
				Prompt.Alert(mActivity, "Sdcard不可用，无法录音");
				break;
			}
			mActivity.tackView.stopAudio(mActivity.shortRecMediaPlayer);
			msg = handler.obtainMessage();
			mActivity.isLongPressed = true;
			isOnRecorderButton = true;
			msg.what = SEND_SOUND_RECORD_MSG;
			handler.sendMessage(msg);
			recordBtn.setImageResource(R.drawable.loosen_end);
			mActivity.mainLayout.setVisibility(View.GONE);
			mActivity.detailMainLayout.setVisibility(View.VISIBLE);
			if (mActivity.mMediaPlayer != null && mActivity.mMediaPlayer.getStatus() == XEffectMediaPlayer.STATUS_PALYING) {
				mActivity.mMediaPlayer.pause();
			}
			break;
		}
		return false;
	}
	
	public void setBeautifulSoundText(String text) {
		beautifulSoundText = text;
		tvBeautifulSound.setText(beautifulSoundText);
	}

}
