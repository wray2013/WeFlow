package com.cmmobi.looklook.activity;

import java.util.ArrayList;
import java.util.Calendar;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;
import cn.zipper.framwork.core.ZActivity;
import cn.zipper.framwork.core.ZBroadcastReceiver;
import cn.zipper.framwork.core.ZDialog;
import cn.zipper.framwork.core.ZToast;
import cn.zipper.framwork.core.ZViewFinder;
import cn.zipper.framwork.utils.ZThread;

import com.cmmobi.looklook.R;
import com.cmmobi.looklook.common.gson.DiaryController;
import com.cmmobi.looklook.common.gson.DiaryController.DiaryWrapper;
import com.cmmobi.looklook.common.gson.GsonProtocol;
import com.cmmobi.looklook.common.gson.GsonResponse3.MyDiaryList;
import com.cmmobi.looklook.common.utils.CmmobiClickAgentWrapper;
import com.cmmobi.looklook.info.profile.CommonInfo;
import com.cmmobi.looklook.info.profile.DiaryManager;
import com.cmmobivideo.workers.XAudioRecorder;
import com.cmmobivideo.workers.XAudioRecorder.XAudioReaderInfoListener;

import effect.XMediaRecorderInterface;

public class AudioRecorderActivity extends ZActivity implements XAudioReaderInfoListener<Object>, OnLongClickListener {
	
	private static final int MESSAGE_TIMING = 0x00ff00AA;
	private static final int MESSAGE_LEAVE_AUDIO_RECORD_PAGE_WHEN_DIARY_OK = 0x00ff00BB;
	private static final int TIMING_GAP_MILLIS = 100;
	
	private static final String CDR_KEY_START_RECORD = "start_record";
	private static final String CDR_KEY_RECORD_PAUSE = "record_pause";
	private static final String CDR_KEY_RECORD_SUCCEED = "record_succeed";
	
	private ZViewFinder finder;
	private ImageView recordButton;
	private ImageButton backButton;
	private TextView doneButton;
	private TextView audioTime;
	private TextView audioName;
	private RelativeLayout shell;
	private XAudioRecorder recorder;
	
	private String diaryUUID;
	private String attachUUID;
	
	private boolean isHomeKeyPressed;
	private boolean isStartedDiaryPreviewActivity;
	private DiaryWrapper wrapper;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
//		setToNoTitle();
		setToPortrait();
	}
	
	private void init() {
		setContentView(R.layout.activity_audio_recorder);
		
		recorder = new XAudioRecorder(this, this);
		recorder.setEnableWaveform(true);//是否启用波形图
		
		finder = getZViewFinder();
		
		TextView textView = (TextView) finder.findView(R.id.title).findViewById(R.id.iv_title_text);
		textView.setText(R.string.audio_record);
		
		recordButton = finder.findImageView(R.id.record_button);
		recordButton.setOnClickListener(this);
		
		backButton = finder.findImageButton(R.id.ib_title_back);
		backButton.setOnClickListener(this);
		backButton.setOnLongClickListener(this);
		
		doneButton = finder.findTextView(R.id.done_button);
		doneButton.setOnClickListener(this);
		doneButton.setEnabled(false);
		
		audioTime = finder.findTextView(R.id.audio_time);
		audioName = finder.findTextView(R.id.audio_name);
		
		shell = finder.findRelativeLayout(R.id.shell);
		
		LayoutParams params = new LayoutParams(
				LayoutParams.MATCH_PARENT, 
				LayoutParams.MATCH_PARENT);
		
		shell.addView(recorder.getWaveformView(), params);
		setAudioName();
	}
	
	private void setAudioName() {
		Calendar calendar = Calendar.getInstance();
		String year = String.valueOf(calendar.get(Calendar.YEAR));
		String month = String.format("%02d", calendar.get(Calendar.MONTH) + 1);
		String date = String.format("%02d", calendar.get(Calendar.DAY_OF_MONTH));
		String string = year + "." + month + "." + date;
		audioName.setText(string);
	}
	
	@Override
	protected void onResume() {
		getZReceiverManager().registerZReceiver(new HomeKeyPressedReceiver());
		if (!isHomeKeyPressed) {
			init();
		}
		isHomeKeyPressed = false;
		super.onResume();
	}
	
	@Override
	protected void onPause() {
		getZReceiverManager().unregisterAllZReceiver(true);
		stopRecord();
		super.onPause();
	}
	
	private void release() {
		ZThread.sleep(500);
		recorder.release();
		recorder = null;
	}
	
	@Override
	protected void onDestroy() {
		release();
		super.onDestroy();
	}
	
	@Override
	public boolean handleMessage(Message msg) {
		switch (msg.what) {
		case MESSAGE_TIMING:
			setAudioTime();
			setAudioName();
			break;
			
		case MESSAGE_LEAVE_AUDIO_RECORD_PAGE_WHEN_DIARY_OK:
			gotoDiaryPreviewActivity();
			break;
		}
		return false;
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.record_button:
			switchRecordState();
			break;
			
		case R.id.done_button:
			stopRecord();
			break;
			
		case R.id.ib_title_back:
			switch (recorder.getStatus()) {
			case XMediaRecorderInterface.STATUS_UNKNOW:
			case XMediaRecorderInterface.STATUS_STOP:
				this.finish();
				this.overridePendingTransition(
						R.anim.animation_slide_in_from_left, 
						R.anim.animation_slide_out_to_right);
				break;
				
//			case XMediaRecorderInterface.STATUS_RECORDING:
//			case XMediaRecorderInterface.STATUS_PAUSE:
//				stopRecord();
//				break;
				
				default:
					showShortToastAtCenter(R.string.audio_recording_canot_back);
					break;
			}
			break;
		}
	}
	
	@Override
	public boolean onLongClick(View v) {
		
		switch (recorder.getStatus()) {
		case XMediaRecorderInterface.STATUS_UNKNOW:
		case XMediaRecorderInterface.STATUS_STOP:
			Intent intent = new Intent(this, LookLookActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
			this.finish();
			this.overridePendingTransition(
					R.anim.animation_slide_in_from_left, 
					R.anim.animation_slide_out_to_right);
			break;
			
			default:
				showShortToastAtCenter(R.string.audio_recording_canot_back);
				break;
		}
		
		return true;
	}
	
	private void switchRecordState() {
		if (recorder.getStatus() == XMediaRecorderInterface.STATUS_UNKNOW 
				|| recorder.getStatus() == XMediaRecorderInterface.STATUS_STOP) {
			
			diaryUUID = DiaryController.getNextUUID();
			attachUUID = DiaryController.getNextUUID();
			
			String path = DiaryController.getInstanse().getFolderByType(GsonProtocol.ATTACH_TYPE_AUDIO);
			
			recorder.enableReadCallback(false);
			recorder.start(attachUUID, path);
			recordButton.setImageResource(R.drawable.btn_record_start_button_selector);
			doneButton.setEnabled(true);
			startTiming(false);
			
			wrapper = DiaryController.getInstanse().requestNewDiary(
					handler, 
					diaryUUID, 
					attachUUID, 
					GsonProtocol.ATTACH_TYPE_AUDIO, 
					GsonProtocol.SUFFIX_MP4, 
					GsonProtocol.EMPTY_VALUE,
					GsonProtocol.EMPTY_VALUE,
					GsonProtocol.EMPTY_VALUE,
					CommonInfo.getInstance().getLongitude(),
					CommonInfo.getInstance().getLatitude(),
					DiaryController.getPositionString1(),
					GsonProtocol.EMPTY_VALUE, 
					String.valueOf(System.currentTimeMillis()));
			
			CmmobiClickAgentWrapper.onEvent(this, CDR_KEY_START_RECORD);
			
		} else if (recorder.getStatus() == XMediaRecorderInterface.STATUS_RECORDING) {
			pauseRecord();
			
		} else if (recorder.getStatus() == XMediaRecorderInterface.STATUS_PAUSE) {
			recorder.resume();
			recordButton.setImageResource(R.drawable.btn_record_start_button_selector);
			startTiming(false);
		}
	}
	
	private void pauseRecord() {
		if (recorder.getStatus() == XMediaRecorderInterface.STATUS_RECORDING) {
			recorder.pause();
			recordButton.setImageResource(R.drawable.btn_activity_audio_record_button_selector);
			stopTiming();
			CmmobiClickAgentWrapper.onEvent(this, CDR_KEY_RECORD_PAUSE);
		}
	}
	
	private void stopRecord() {
		if (recorder.getStatus() == XMediaRecorderInterface.STATUS_RECORDING
				|| recorder.getStatus() == XMediaRecorderInterface.STATUS_PAUSE) {
			
			showProgressDialog();
			
			stopTiming();
			recorder.stop();
			
			recordButton.setImageResource(R.drawable.btn_activity_audio_record_button_selector);
			doneButton.setEnabled(false);
			gotoDiaryPreviewActivity();
			
			CmmobiClickAgentWrapper.onEvent(this, CDR_KEY_RECORD_SUCCEED);
		}
	}
	
	private void gotoDiaryPreviewActivity() {
		if (!isStartedDiaryPreviewActivity) {
			
			if (wrapper.isDiaryOK) {
				isStartedDiaryPreviewActivity = true;
				ArrayList<MyDiaryList> list = new ArrayList<MyDiaryList>();
				list.add(DiaryManager.getInstance().findDiaryGroupByUUID(diaryUUID));
				DiaryManager.getInstance().setDetailDiaryList(list, 0);
				
				Intent intent = new Intent(this, DiaryPreviewActivity.class);
				intent.putExtra(DiaryPreviewActivity.INTENT_ACTION_DIARY_UUID, diaryUUID);
				startActivity(intent);
				dismissProgressDialog();
				finish();
				this.overridePendingTransition(R.anim.animation_slide_in_from_left, R.anim.animation_slide_out_to_right);
				
			} else {
				handler.removeMessages(MESSAGE_LEAVE_AUDIO_RECORD_PAGE_WHEN_DIARY_OK);
				handler.sendEmptyMessageDelayed(MESSAGE_LEAVE_AUDIO_RECORD_PAGE_WHEN_DIARY_OK, TIMING_GAP_MILLIS);
			}
		}
	}
	
	private void startTiming(boolean delay) {
		if (delay) {
			handler.sendEmptyMessageDelayed(MESSAGE_TIMING, TIMING_GAP_MILLIS);
		} else {
			handler.sendEmptyMessage(MESSAGE_TIMING);
		}
	}
	
	private void stopTiming() {
		handler.removeMessages(MESSAGE_TIMING);
	}
	
	@Override
	public void onBackPressed() {
		
		switch (recorder.getStatus()) {
		case XMediaRecorderInterface.STATUS_UNKNOW:
		case XMediaRecorderInterface.STATUS_STOP:
			this.finish();
			this.overridePendingTransition(
					R.anim.animation_slide_in_from_left, 
					R.anim.animation_slide_out_to_right);
			break;
			
		case XMediaRecorderInterface.STATUS_RECORDING:
		case XMediaRecorderInterface.STATUS_PAUSE:
			stopRecord();
			break;
			
			default:
				showShortToastAtCenter(R.string.audio_recording_canot_back);
				break;
		}
	}
	
	public static void startSelf(Activity activity) {
		Intent intent = new Intent(activity, AudioRecorderActivity.class);
		activity.startActivity(intent);
		activity.overridePendingTransition(
				R.anim.animation_slide_in_from_right, 
				R.anim.animation_slide_out_to_left);
	}
	
	private void setAudioTime() {
		long totalRecordMillis = recorder.getRecordingTime();
		int hours = (int) (totalRecordMillis / 3600000);
		int minutes = (int) ((totalRecordMillis - (hours * 3600000))/ 60000);
		int seconds = (int) ((totalRecordMillis - (hours * 3600000) - (minutes * 60000))/ 1000);
		
		String time = String.format("%02d:%02d:%02d", hours, minutes, seconds);
		audioTime.setText(time);
		startTiming(true);
	}

	@Override
	public void onStartRecorder(Object r, String path) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onStopRecorder(Object r, String path) {
		DiaryController.getInstanse().diaryContentIsReady(diaryUUID);
	}

	@Override
	public void onSmallBoxComplete(Object r, String path) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onPauseRecorder(Object r) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onResumeRecorder(Object r) {
		// TODO Auto-generated method stub
		
	}
	
	private void showProgressDialog() {
		dismissProgressDialog();
		ZDialog.show(R.layout.progressdialog, true, true, AudioRecorderActivity.this, true);
	}
	
	private void dismissProgressDialog() {
		if (ZDialog.getDialog() != null) {
			if (ZDialog.getDialog().isShowing()) {
				ZDialog.dismiss();
			}
		}
	}
	
	private void showShortToastAtCenter(int id) {
		String string = getString(id);
		ZToast.show(string, Toast.LENGTH_SHORT, Gravity.CENTER, 0, 0);
	}
	
	private class HomeKeyPressedReceiver extends ZBroadcastReceiver {
		
		public HomeKeyPressedReceiver() {
			super(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
		}
		
		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equals(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)) {
				String reason = intent.getStringExtra("reason");
				if (reason != null && reason.equals("homekey")) {
					isHomeKeyPressed = true;
				}
			}
		}
	}

}
