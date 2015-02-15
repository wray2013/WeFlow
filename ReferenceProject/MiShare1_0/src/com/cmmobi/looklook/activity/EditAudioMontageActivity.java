package com.cmmobi.looklook.activity;

import java.util.LinkedList;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;
import cn.zipper.framwork.core.ZActivity;
import cn.zipper.framwork.core.ZApplication;
import cn.zipper.framwork.core.ZDialog;
import cn.zipper.framwork.io.file.ZFileSystem;

import com.cmmobi.looklook.R;
import com.cmmobi.looklook.common.constant.Constant;
import com.cmmobi.looklook.common.gson.DiaryController;
import com.cmmobi.looklook.common.gson.GsonResponse3.MyDiary;
import com.cmmobi.looklook.common.listener.MyWaveformListener;
import com.cmmobi.looklook.common.utils.DiaryEditNote;
import com.cmmobi.looklook.info.profile.ActiveAccount;
import com.cmmobi.looklook.prompt.Prompt;
import com.cmmobivideo.utils.EffectUtils;
import com.cmmobivideo.utils.Mp4InfoUtils;
import com.cmmobivideo.utils.PluginUtils;
import com.cmmobivideo.wedget.XWgWaveformView;
import com.cmmobivideo.workers.XMediaPlayer;
import com.cmmobivideo.workers.XMediaPlayer.OnInfoListener;
import com.cmmobivideo.workers.XMediaPlayer.XPlayerWaveformListener;
import com.google.gson.Gson;

import effect.XEffectDefine;
import effect.XEffectMediaPlayer;
import effect.XEffects;
import effect.XMp4Box;

public class EditAudioMontageActivity extends ZActivity {

	private final String TAG = "EditAudioMontageActivity";
	private XMediaPlayer mMediaPlayer = null;
	private XEffects mEffects = null;
	private String audioPath = null;
	private ImageView ivPlay = null;
	private boolean isPlayerNeedToPlay;
	private boolean isPlayerPrepared;
	private MyDiary myDiary = null;
	private String userID = null;
	private ImageView ivDone = null;
	private ImageView ivUndo = null;
	private TextView tvTime = null;
	private TextView tvDate = null;
	
	private RelativeLayout audioLayout = null;
	private DisplayMetrics dm = new DisplayMetrics();
	public EffectUtils effectUtils;
	
	private RelativeLayout waveLayout = null;
	
//	private String outAudioPath = null;
	private String curAudioPath = null;
	private double curAudioDuration = 0;
	private double audioDuration = 0;
	public TextView dialogTitle;
	
	private ImageView ivMontageSave = null;
	private ImageView ivMontageDelete = null;
	private LinkedList<String> montageStrList = new LinkedList<String>();
	private DiaryEditNote diaryEditNote = null;
	private String curOriginalVideoPath = null;
	private LinkedList<String> originalPathList = new LinkedList<String>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_audio_montage);
		
		findViewById(R.id.iv_edit_diary_back).setOnClickListener(this);
		
		audioLayout = (RelativeLayout) findViewById(R.id.rl_audio_content_view);
		dm = getResources().getDisplayMetrics();
		Log.d(TAG,"width = " + dm.widthPixels + " height = " + dm.heightPixels);
		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(dm.widthPixels,dm.widthPixels);
		params.addRule(RelativeLayout.BELOW, R.id.rl_edit_audio_montage_top);
		audioLayout.setLayoutParams(params);
		
		ivDone = (ImageView) findViewById(R.id.iv_edit_diary_save);
		ivDone.setOnClickListener(this);
		ivDone.setEnabled(false);
		
		ivUndo = (ImageView) findViewById(R.id.iv_edit_diary_undo);
		ivUndo.setOnClickListener(this);
		ivUndo.setEnabled(false);
		
		ivMontageSave = (ImageView) findViewById(R.id.iv_edit_audio_save);
		ivMontageDelete = (ImageView) findViewById(R.id.iv_edit_audio_delete);
		
		ivMontageSave.setOnClickListener(this);
		ivMontageDelete.setOnClickListener(this);
		
		ivPlay = (ImageView) findViewById(R.id.iv_play);
		ivPlay.setOnClickListener(this);
		
		waveLayout = (RelativeLayout) findViewById(R.id.rl_wave_layout);
		
		tvTime = (TextView) findViewById(R.id.tv_time);
		tvDate = (TextView) findViewById(R.id.tv_date);
		
		enableMontage(false);
		
		loadAudio();
	}
	
	@Override
	public boolean handleMessage(Message arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onClick(View view) {
		// TODO Auto-generated method stub
		switch(view.getId()) {
		case R.id.iv_edit_diary_back:
			deleteAllCacheFile();
			finish();
			break;
		case R.id.iv_edit_diary_save:
			modifyDiary();
			break;
		case R.id.iv_edit_diary_undo:
			processUndo();
			break;
		case R.id.iv_play:
			int status = mMediaPlayer.getStatus();
			if (status == XEffectMediaPlayer.STATUS_UNKNOW || status == XEffectMediaPlayer.STATUS_STOP || status == XEffectMediaPlayer.STATUS_OPENED) {
				if (status == XEffectMediaPlayer.STATUS_UNKNOW) {
					if (curAudioPath != null) {
						isPlayerPrepared = false;
						ZDialog.show(R.layout.progressdialog, false, true, this,false);
						mMediaPlayer.open(curAudioPath);
					}
				}
				if (isPlayerPrepared) {
					mMediaPlayer.play();
					Log.d(TAG,"iv_play getEditStartTime = " + mMediaPlayer.getEditStartTime());
					if (mMediaPlayer.getEditStartTime() > 0.1) {
						
						mMediaPlayer.seek(mMediaPlayer.getEditStartTime());
					}
				} else {
					isPlayerNeedToPlay = true;
				}
				
			} else if (mMediaPlayer.getStatus() == XEffectMediaPlayer.STATUS_PALYING) {
				mMediaPlayer.pause();
				ivPlay.setImageResource(R.drawable.yinpin_play);
			} else if (mMediaPlayer.getStatus() == XEffectMediaPlayer.STATUS_PAUSE) {
				mMediaPlayer.resume();
				ivPlay.setImageResource(R.drawable.yinpin_zanting);
			}
			break;
		case R.id.iv_edit_audio_delete:
			montageAudio(curAudioPath, true);
			montageOriginalAudio(curOriginalVideoPath, true);
			enableMontage(false);
			ivDone.setEnabled(true);
			break;
		case R.id.iv_edit_audio_save:
			montageAudio(curAudioPath, false);
			montageOriginalAudio(curOriginalVideoPath, false);
			enableMontage(false);
			ivDone.setEnabled(true);
			break;
		default:
			break;
		}
	}
	
	private void processUndo() {
		if (montageStrList.size() < 1) {
			ivUndo.setEnabled(false);
			ivDone.setEnabled(false);
			return;
		}
		
		String lastPath = montageStrList.removeLast();
		
		String path = "";
		if (montageStrList.size() < 1) {
			ivUndo.setEnabled(false);
			ivDone.setEnabled(false);
			path = audioPath;
		} else {
			path = montageStrList.getLast();
		}
		processOriginalUndo();
		
		reloadAudio(path);
		
		if (lastPath != null && !lastPath.equals(audioPath) && !lastPath.equals(curAudioPath)) {
			ZFileSystem.delFile(lastPath);
		}
	}
	
	private void processOriginalUndo() {
		if (audioPath.equals(diaryEditNote.mediaPath)) {
			return;
		}
		String originalPath = originalPathList.removeLast();
		
		String path = "";
		if (originalPathList.size() < 1) {
			path = diaryEditNote.mediaPath;
		} else {
			path = originalPathList.getLast();
		}
		curOriginalVideoPath = path;
		
		if (!originalPath.equals(curOriginalVideoPath) && originalPath.equals(diaryEditNote.mediaPath)) {
			ZFileSystem.delFile(originalPath);
		}
	}
	
	private void modifyDiary() {
		myDiary.modifyMainAttach(curAudioPath, "");
		Intent intent = new Intent();
		if (!curAudioPath.equals(audioPath)) {
			myDiary.modifyMainAttach(curAudioPath, "");
			if (audioPath.equals(diaryEditNote.mediaPath)) {
				intent.putExtra(DiaryEditPreviewActivity.INTENT_ACTION_MEDIA_PATH, curAudioPath);
			} else {
				intent.putExtra(DiaryEditPreviewActivity.INTENT_ACTION_MEDIA_PATH, curOriginalVideoPath);
			}
		}
		deleteCacheFile();
		String diaryString = new Gson().toJson(myDiary);
		
		intent.putExtra(DiaryEditPreviewActivity.INTENT_ACTION_DIARY_STRING, diaryString);
		setResult(RESULT_OK, intent);
		finish();
	}
	
	private void deleteCacheFile() {
		for(String path:montageStrList) {
			if (path != null && !path.equals(audioPath) && !path.equals(curAudioPath)) {
				ZFileSystem.delFile(path);
			}
		}
		
		for (String path:originalPathList) {
			if (path != null && !path.equals(diaryEditNote.mediaPath) && path.equals(curOriginalVideoPath)) {
				ZFileSystem.delFile(path);
			}
		}
	}
	
	private void deleteAllCacheFile() {
		for(String path:montageStrList) {
			if (path != null && !path.equals(audioPath)) {
				ZFileSystem.delFile(path);
			}
		}
		
		for (String path:originalPathList) {
			if (path != null && !path.equals(diaryEditNote.mediaPath)) {
				ZFileSystem.delFile(path);
			}
		}
	}
	
	private void enableMontage(boolean enabled) {
		ivMontageDelete.setEnabled(enabled);
		ivMontageSave.setEnabled(enabled);
	}
	
	private void  montageOriginalAudio(String inputFile,boolean isSelectedDelete) {
		if (audioPath.equals(diaryEditNote.mediaPath)) {
			return;
		}
		XMp4Box mMp4Box = new XMp4Box();
		String audioID = DiaryController.getNextUUID();
		String RECORD_FILE_DIR =  Constant.SD_STORAGE_ROOT + "/" + ActiveAccount.getInstance(this).getLookLookID() + "/audio";
		String outputFile = Environment.getExternalStorageDirectory() + RECORD_FILE_DIR + "/" + audioID + ".mp4";
		if (isSelectedDelete) {
			String outputFile1 = Environment.getExternalStorageDirectory() + RECORD_FILE_DIR + "/" + audioID + "_temp1.mp4";
			int ret1 = mMp4Box.splitFile(inputFile,outputFile1,0,(float) mMediaPlayer.getEditStartTime());
			String outputFile2 = Environment.getExternalStorageDirectory() + RECORD_FILE_DIR + "/" + audioID + "_temp2.mp4";
			int ret2 = mMp4Box.splitFile(inputFile,outputFile2,(float)mMediaPlayer.getEditEndTime(),(float)curAudioDuration);
			Log.d(TAG,"ret1 = " + ret1 + " ret2 = " + ret2 + " startTime = " + mMediaPlayer.getEditStartTime() + " endTime = " + mMediaPlayer.getEditEndTime() + " duration = " + curAudioDuration);
			mMp4Box.appendOpen(outputFile);
			if (ret1 == 0) {
				mMp4Box.appendFile(outputFile1);
				ZFileSystem.delFile(outputFile1);
			} 
			if (ret2 == 0) {
				mMp4Box.appendFile(outputFile2);
				ZFileSystem.delFile(outputFile2);
			}
			mMp4Box.appendClose();
		} else {
			int ret = mMp4Box.splitFile(inputFile,outputFile,(float)mMediaPlayer.getEditStartTime(),(float)mMediaPlayer.getEditEndTime());//1s
			Log.d(TAG,"ret = " + ret + " startMontageTime = " + mMediaPlayer.getEditStartTime() + " endMontageTime = " + mMediaPlayer.getEditEndTime());
		}
		originalPathList.add(outputFile);
		curOriginalVideoPath = outputFile;
		
	}
	
	private void montageAudio(String inputFile,boolean isSelectedDelete) {
		XMp4Box mMp4Box = new XMp4Box();
		String videoID = DiaryController.getNextUUID();
		String RECORD_FILE_DIR =  Constant.SD_STORAGE_ROOT + "/" + ActiveAccount.getInstance(this).getLookLookID() + "/audio";
		String outputFile = Environment.getExternalStorageDirectory() + RECORD_FILE_DIR + "/" + videoID + ".mp4";
		if (isSelectedDelete) {
			String outputFile1 = Environment.getExternalStorageDirectory() + RECORD_FILE_DIR + "/" + videoID + "_temp1.mp4";
			int ret1 = mMp4Box.splitFile(inputFile,outputFile1,0,(float) mMediaPlayer.getEditStartTime());
			String outputFile2 = Environment.getExternalStorageDirectory() + RECORD_FILE_DIR + "/" + videoID + "_temp2.mp4";
			int ret2 = mMp4Box.splitFile(inputFile,outputFile2,(float)mMediaPlayer.getEditEndTime(),(float)curAudioDuration);
			Log.d(TAG,"ret1 = " + ret1 + " ret2 = " + ret2 + " startTime = " + mMediaPlayer.getEditStartTime() + " endTime = " + mMediaPlayer.getEditEndTime() + " duration = " + curAudioDuration);
			mMp4Box.appendOpen(outputFile);
			if (ret1 == 0) {
				mMp4Box.appendFile(outputFile1);
				ZFileSystem.delFile(outputFile1);
			} 
			if (ret2 == 0) {
				mMp4Box.appendFile(outputFile2);
				ZFileSystem.delFile(outputFile2);
			}
			mMp4Box.appendClose();
		} else {
			int ret = mMp4Box.splitFile(inputFile,outputFile,(float)mMediaPlayer.getEditStartTime(),(float)mMediaPlayer.getEditEndTime());//1s
			Log.d(TAG,"ret = " + ret + " startMontageTime = " + mMediaPlayer.getEditStartTime() + " endMontageTime = " + mMediaPlayer.getEditEndTime());
		}
		
		montageStrList.add(outputFile);
		
		ivDone.setEnabled(true);
		ivUndo.setEnabled(true);
		
		reloadAudio(outputFile);
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		try {
			if(mMediaPlayer!=null){
				mMediaPlayer.release();
				mEffects.release();
				mMediaPlayer=null;
				isPlayerNeedToPlay = false;
				isPlayerPrepared = false;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		super.onDestroy();
	}
	
	public void reloadAudio(String path) {
		mMediaPlayer.endEdit();
		mMediaPlayer.stop();
		mMediaPlayer.open(path);
		ZDialog.show(R.layout.progressdialog, false, true, this,false);
		mMediaPlayer.reloadWaveform();
		curAudioPath = path;
		curAudioDuration = new Mp4InfoUtils(curAudioPath).totaltime;
		Log.d(TAG,"reloadAudio curAudioDuration = " + curAudioDuration);
	}
	
	private void loadAudio() {
		userID =  ActiveAccount.getInstance(ZApplication.getInstance()).getUID();
		String diaryString = getIntent().getStringExtra("diarystring");
		myDiary = new Gson().fromJson(diaryString,MyDiary.class);
		
		String diaryEditString = getIntent().getStringExtra(DiaryEditPreviewActivity.INTENT_ACTION_EDIT_NOTELIST);
		diaryEditNote = new Gson().fromJson(diaryEditString, DiaryEditNote.class);
		curOriginalVideoPath = diaryEditNote.mediaPath;
		
		audioPath = myDiary.getMainPath();
		curAudioPath = audioPath;
		if (audioPath == null) {
			Prompt.Alert(this, "未找到音频文件");
			this.finish();
			return;
		}
		
		tvDate.setText(myDiary.getDiaryDate());
		effectUtils = new EffectUtils(this);
		if (PluginUtils.isPluginMounted()) {
			mEffects = new XEffects();
		}
		
		audioDuration = new Mp4InfoUtils(audioPath).totaltime;
		curAudioDuration = audioDuration;
		
		if(null==mMediaPlayer) {
			mMediaPlayer = new XMediaPlayer(this, mEffects, true);
			mMediaPlayer.setEnableWaveform(true,XEffectDefine.JAVA_A_EDIT_WAVEFORM);//启用波形图
			MyWaveformListener listener = new MyWaveformListener(mMediaPlayer,waveLayout);
			listener.setNeedWait(true);
			mMediaPlayer.setWaveformListener(listener);
//			mMediaPlayer.setEnableEditAudio(true);
			mMediaPlayer.setUpdateTimePeriod(0.1f);
			mMediaPlayer.setListener(new MyOnInfoListener());
			
			mMediaPlayer.setWaveformCutListener(new XWgWaveformView.XWgWaveformCutListener() {
				@Override
				public void onChange() {
					// TODO Auto-generated method stub
					double startTime = mMediaPlayer.getEditStartTime();
					double endTime = mMediaPlayer.getEditEndTime();
					Log.i(TAG, "[onChange] startTime:"+startTime+",endTime:"+endTime + " audioDuration = " + curAudioDuration);
					
					double laveLen = startTime + (curAudioDuration - endTime);
					if (Math.abs(startTime - 0) < 0.01f && Math.abs(endTime - curAudioDuration) < 0.01f) {
						ivMontageSave.setEnabled(false);
						ivMontageDelete.setEnabled(false);
					} else if (laveLen < 2) {
						ivMontageSave.setEnabled(true);
						ivMontageDelete.setEnabled(false);
					} else {
						ivMontageSave.setEnabled(true);
						ivMontageDelete.setEnabled(true);
					}
				}

				@Override
				public void onStop() {
					// TODO Auto-generated method stub
					if (mMediaPlayer != null) {
//						mMediaPlayer.stop();
						mMediaPlayer.waveformStop();
					}
				}
			});
			
			if (audioPath != null) {
				ZDialog.show(R.layout.progressdialog, false, true, this,false);
				mMediaPlayer.open(audioPath);
			}
		}
	}
	
	private class MyOnInfoListener implements OnInfoListener {

		@Override
		public void onStartPlayer(XMediaPlayer player) {
			Log.e(TAG, "onStartPlayer");
			ivPlay.setImageResource(R.drawable.yinpin_zanting);
		}

		@Override
		public void onStopPlayer(XMediaPlayer player) {
			Log.e(TAG, "onStopPlayer");
			ivPlay.setImageResource(R.drawable.yinpin_play);
		}

		@Override
		public void OnFinishPlayer(XMediaPlayer player) {
			Log.e(TAG, "OnFinishPlayer");
			tvTime.setText(EditAudioSoundTrackActivity.convertTime(0));
			ivPlay.setImageResource(R.drawable.yinpin_play);
		}

		@Override
		public void onUpdateTime(XMediaPlayer player, double time) {
			final double time1 = time;
			runOnUiThread(new Runnable() {
				
				@Override
				public void run() {
					// TODO Auto-generated method stub
					tvTime.setText(EditAudioSoundTrackActivity.convertTime(time1));
				}
			});
		}

		@Override
		public void onSurfaceCreated(XMediaPlayer player) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onPreparedPlayer(XMediaPlayer player) {
			isPlayerPrepared = true;
			if (isPlayerNeedToPlay) {
				player.play();
				Log.d(TAG,"iv_play getEditStartTime = " + mMediaPlayer.getEditStartTime());
				if (mMediaPlayer.getEditStartTime() > 0.1) {
					mMediaPlayer.seek(mMediaPlayer.getEditStartTime());
				}
			} 
		}

		@Override
		public void onVideoSizeChanged(XMediaPlayer player, int w, int h) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onError(XMediaPlayer player, int what, int extra) {
			// TODO Auto-generated method stub
			
		}
	}

}
