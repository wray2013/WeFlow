package com.cmmobi.looklook.activity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import cn.zipper.framwork.core.ZActivity;
import cn.zipper.framwork.core.ZApplication;
import cn.zipper.framwork.core.ZDialog;
import cn.zipper.framwork.core.ZLayoutInflater;
import cn.zipper.framwork.io.file.ZFileSystem;

import com.cmmobi.looklook.R;
import com.cmmobi.looklook.common.constant.Constant;
import com.cmmobi.looklook.common.gson.GsonResponse3.MyDiary;
import com.cmmobi.looklook.common.listener.MyWaveformListener;
import com.cmmobi.looklook.common.utils.AudioSoundTrackBean;
import com.cmmobi.looklook.common.utils.DiaryEditNote;
import com.cmmobi.looklook.common.utils.EffectTransCodeUtil;
import com.cmmobi.looklook.info.profile.ActiveAccount;
import com.cmmobi.looklook.prompt.Prompt;
import com.cmmobi.statistics.CmmobiClickAgent;
import com.cmmobivideo.utils.EffectUtils;
import com.cmmobivideo.utils.PluginUtils;
import com.cmmobivideo.workers.XMediaPlayer;
import com.cmmobivideo.workers.XMediaPlayer.OnInfoListener;
import com.cmmobivideo.workers.XMediaPlayer.XPlayerWaveformListener;
import com.google.gson.Gson;

import effect.XEffectDefine;
import effect.XEffectMediaPlayer;
import effect.XEffects;

public class EditAudioSoundTrackActivity extends ZActivity {

	private final String TAG = "EditAudioSoundTrackActivity";
	private XMediaPlayer mMediaPlayer = null;
	private XEffects mEffects = null;
	private String audioPath = null;
	private ImageView ivPlay = null;
	private boolean isPlayerNeedToPlay;
	private boolean isPlayerPrepared;
	private MyDiary myDiary = null;
	private String userID = null;
	private ImageView ivDone = null;
	private TextView tvTime = null;
	private TextView tvDate = null;
	
	private RelativeLayout audioLayout = null;
	private DisplayMetrics dm = new DisplayMetrics();
	
	private SeekBar volumeSeek;
	private double percent = 0.5;
	public EffectUtils effectUtils;
	private ArrayList<AudioSoundTrackBean> trackBeanList = new ArrayList<AudioSoundTrackBean>();
	private AudioSoundTrackBean trackBean = null;
	private RadioGroup soundTrackGroup = null;
	public DiaryEditNote diaryEditNote = null;
	public String soundTrackPath = "";
	
	private RelativeLayout waveLayout = null;
	
	private CountDownLatch processSignal = new CountDownLatch(1);
	private String outAudioPath = null;
	public TextView dialogTitle;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_audio_sound_track);
		findViewById(R.id.iv_edit_diary_back).setOnClickListener(this);
		
		audioLayout = (RelativeLayout) findViewById(R.id.rl_audio_content_view);
		dm = getResources().getDisplayMetrics();
		Log.d(TAG,"width = " + dm.widthPixels + " height = " + dm.heightPixels);
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(dm.widthPixels,dm.widthPixels);
		audioLayout.setLayoutParams(params);
		
		ivDone = (ImageView) findViewById(R.id.iv_edit_diary_save);
		ivDone.setOnClickListener(this);
		ivDone.setEnabled(false);
		
		ivPlay = (ImageView) findViewById(R.id.iv_play);
		ivPlay.setOnClickListener(this);
		
		waveLayout = (RelativeLayout) findViewById(R.id.rl_wave_layout);
		
		tvTime = (TextView) findViewById(R.id.tv_time);
		tvDate = (TextView) findViewById(R.id.tv_date);
		
		loadAudio();
		
		soundTrackGroup = (RadioGroup) findViewById(R.id.rg_sound_tracks_group);
		init();
		
		volumeSeek = (SeekBar) findViewById(R.id.sk_edit_voice_seekbar);
        final int max = volumeSeek.getMax();
        volumeSeek.setProgress((int)(max * percent));
        volumeSeek.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			
			@Override
			public void onStopTrackingTouch(SeekBar arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onStartTrackingTouch(SeekBar arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onProgressChanged(SeekBar arg0, int arg1, boolean arg2) {
				// TODO Auto-generated method stub
				if (trackBean != null) {
					percent = ((double) volumeSeek.getProgress()/ max);
					effectUtils.addMusic(mEffects, "effectcfg/audio/addmusic.cfg", trackBean.filePath, 
							mMediaPlayer.getChannels() , 
							mMediaPlayer.getSampleRate(), 
							mMediaPlayer.getBitsPerChannel(), 
							percent, 
							1);
					Log.d(TAG,"precent = " + percent);
				}
			}
		});
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
	
	private void init() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				createFile("dianzi.mp3");
				createFile("diqu.mp3");
				createFile("dianzigangqin.mp3");
				createFile("shuqinggangqin.mp3");
			}
		}).start();
		String preffix = LookLookActivity.SDCARD_PATH +Constant.SD_STORAGE_ROOT + "/" + "sound/";
		trackBeanList.clear();
		trackBeanList.add(new AudioSoundTrackBean(0, "", "原音"));
		trackBeanList.add(new AudioSoundTrackBean(1, preffix + "diqu.mp3","迪曲"));
		trackBeanList.add(new AudioSoundTrackBean(2, preffix + "dianzi.mp3","电子"));
		trackBeanList.add(new AudioSoundTrackBean(3, preffix + "dianzigangqin.mp3","电子钢琴"));
		trackBeanList.add(new AudioSoundTrackBean(4, preffix + "shuqinggangqin.mp3","抒情钢琴"));
		int size = trackBeanList.size();
		soundTrackGroup.removeAllViews();
		
		for (int i = 0;i < size;i++) {
			AudioSoundTrackBean trackBeanTemp = trackBeanList.get(i);
			LinearLayout layout = (LinearLayout) ZLayoutInflater.inflate(R.layout.include_soundtrack_buttons_stub);
			RadioButton radioButton = (RadioButton) layout.findViewById(R.id.radio_button);
			
			radioButton.setId(trackBeanTemp.trackID);
			radioButton.setText(trackBeanTemp.trackName);
			radioButton.setTag(trackBeanTemp);
			radioButton.setOnClickListener(this);
			radioButton.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));

			layout.removeAllViewsInLayout();
			soundTrackGroup.addView(radioButton);
			
			if (diaryEditNote.isAddSoundTrack && trackBeanTemp.filePath.equals(soundTrackPath)) {
				soundTrackGroup.check(i);
				trackBean = trackBeanTemp;
			}
		}
		
		if (!diaryEditNote.isAddSoundTrack) {
			soundTrackGroup.check(0);
		}
	}
	
	private void createFile(String fileName) {  
		String fileDirPath = LookLookActivity.SDCARD_PATH +Constant.SD_STORAGE_ROOT + "/" + "sound/";
        String filePath = fileDirPath + "/" + fileName;// 文件路径 
        if (ZFileSystem.isFileExists(filePath)) {
        	return;
        }
        try {
            File dir = new File(fileDirPath);// 目录路径  
            if (!dir.exists()) {// 如果不存在，则创建路径名  
                System.out.println("要存储的目录不存在");  
                if (dir.mkdirs()) {// 创建该路径名，返回true则表示创建成功  
                    System.out.println("已经创建文件存储目录");  
                } else {  
                    System.out.println("创建目录失败");  
                }  
            }  
            // 目录存在，则将apk中raw中的需要的文档复制到该目录下  
            File file = new File(filePath);  
            if (!file.exists()) {// 文件不存在  
                System.out.println("要打开的文件不存在");  
                String filename = "effectcfg/audio/" + fileName;
                InputStream ins = getAssets().open(filename);// 通过raw得到数据资源  
                System.out.println("开始读入");  
                FileOutputStream fos = new FileOutputStream(file);  
                System.out.println("开始写出");  
                byte[] buffer = new byte[8192];  
                int count = 0;// 循环写出  
                while ((count = ins.read(buffer)) > 0) {  
                    fos.write(buffer, 0, count);  
                }  
                System.out.println("已经创建该文件");  
                fos.close();// 关闭流  
                ins.close();  
            }  
        } catch (Exception e) {  
            e.printStackTrace();  
        }  
    } 
	
	@Override
	public boolean handleMessage(Message msg) {
		// TODO Auto-generated method stub.
		switch(msg.what) {
		case EffectTransCodeUtil.HANDLER_PROCESS_EFFECTS_FINISH:
			Log.d(TAG,"VideoEffectTransCodeUtil.HANDLER_PROCESS_EFFECTS_FINISH arg1 = " + msg.arg1);
			if (msg.arg1 == EffectTransCodeUtil.MAIN_AUDIO) {
				if (msg.obj != null) {
					outAudioPath = (String) msg.obj;
				}
				processSignal.countDown();
			}
			break;
		case EffectTransCodeUtil.HANDLER_PROCESS_EFFECTS_SCHEDULE:
			int percent = msg.arg2;
			Log.d(TAG,"VideoEffectTransCodeUtil.HANDLER_PROCESS_EFFECTS_SCHEDULE percent = " + percent);
			if (dialogTitle != null) {
				dialogTitle.setText(getString(R.string.process_audio_effect) + " " + percent + "%");
			}
			break;
		}
		return false;
	}

	@Override
	public void onClick(View view) {
		// TODO Auto-generated method stub
		switch(view.getId()) {
		case R.id.iv_edit_diary_back:
			finish();
			break;
		case R.id.iv_edit_diary_save:
			if (mMediaPlayer.getStatus() == XEffectMediaPlayer.STATUS_PALYING) {
				mMediaPlayer.stop();
			}
			modifyMainAttach();
			break;
		case R.id.iv_play:
			int status = mMediaPlayer.getStatus();
			if (status == XEffectMediaPlayer.STATUS_UNKNOW || status == XEffectMediaPlayer.STATUS_STOP || status == XEffectMediaPlayer.STATUS_OPENED) {
				if (status == XEffectMediaPlayer.STATUS_UNKNOW) {
					if (audioPath != null) {
						isPlayerPrepared = false;
						mMediaPlayer.open(audioPath);
					}
				}
				if (isPlayerPrepared) {
					mMediaPlayer.play();
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
		default:
			int viewId = view.getId();
			if (viewId >= 0 && viewId < trackBeanList.size()) {
				AudioSoundTrackBean trackBeanTemp = null;
				if (view.getTag() != null) {
					trackBeanTemp = (AudioSoundTrackBean) view.getTag();
				}
				
				if (viewId == 0) {
					mEffects.cleanEffects();
					ivDone.setEnabled(false);
					trackBean = null;
				} else if (effectUtils != null && trackBeanTemp != null 
						&& ZFileSystem.isFileExists(trackBeanTemp.filePath)) {
					trackBean = trackBeanTemp;
					effectUtils.addMusic(mEffects, "effectcfg/audio/addmusic.cfg", trackBeanTemp.filePath, 
						mMediaPlayer.getChannels(), 
						mMediaPlayer.getSampleRate(), 
						mMediaPlayer.getBitsPerChannel(), 
						percent, 
						1);
					ivDone.setEnabled(true);
					CmmobiClickAgent.onEvent(this, "background_muisc",String.valueOf(viewId));
					Log.d(TAG,"onclick use soundtrack");
				}
				
				if (diaryEditNote.isAddSoundTrack && !TextUtils.isEmpty(diaryEditNote.soundtrackPath)) {
					if (trackBeanTemp.filePath.equals(diaryEditNote.soundtrackPath)) {
						ivDone.setEnabled(false);
					} else {
						ivDone.setEnabled(true);
					}
				}
				soundTrackPath = trackBeanTemp.filePath;
			}
			break;
		}
	}
	
	private void modifyMainAttach() {
		Dialog dialog = ZDialog.show(R.layout.progressdialog, false, true, this,true);
		dialogTitle = (TextView) dialog.findViewById(R.id.progress_dialog_tv);
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				if (mEffects != null && mEffects.getEffectsCount() > 0) {
					String infilename = audioPath;
					Log.d(TAG,"infilename =  " + infilename);
					if (infilename != null) {
						EffectTransCodeUtil transCode = new EffectTransCodeUtil(handler, mEffects, infilename, EditAudioSoundTrackActivity.this,EffectTransCodeUtil.MAIN_AUDIO);
						transCode.start("audio");
						try {
							processSignal.await();
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
				
				myDiary.modifyMainAttach(outAudioPath, "");
				
				EditAudioSoundTrackActivity.this.runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						// TODO Auto-generated method stub
						ZDialog.dismiss();
						String diaryString = new Gson().toJson(myDiary);
						Intent intent = new Intent();
						intent.putExtra(DiaryEditPreviewActivity.INTENT_ACTION_DIARY_STRING, diaryString);
						intent.putExtra(DiaryEditPreviewActivity.INTENT_ACTION_SOUNDTRACK, soundTrackPath);
						intent.putExtra(DiaryEditPreviewActivity.INTENT_ACTION_SOUND_PERCENT, percent);
						setResult(RESULT_OK, intent);
						finish();
					}
				});
				
				Log.d(TAG,"updateDiary in");
			}
		}).start();
	}
	
	private void loadAudio() {
		userID =  ActiveAccount.getInstance(ZApplication.getInstance()).getUID();
		String diaryString = getIntent().getStringExtra("diarystring");
		myDiary = new Gson().fromJson(diaryString,MyDiary.class);
		
		String diaryEditString = getIntent().getStringExtra(DiaryEditPreviewActivity.INTENT_ACTION_EDIT_NOTELIST);
		diaryEditNote = new Gson().fromJson(diaryEditString, DiaryEditNote.class);
		
		soundTrackPath = diaryEditNote.soundtrackPath;
		
		audioPath = diaryEditNote.mediaPath;
		outAudioPath = audioPath;
		
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
		
		if(null==mMediaPlayer) {
			mMediaPlayer = new XMediaPlayer(this, mEffects, true);
//			mMediaPlayer.setEnableWaveform(true);
			mMediaPlayer.setUpdateTimePeriod(0.1f);
			mMediaPlayer.setEnableWaveform(true,XEffectDefine.JAVA_A_PLAY_WAVEFORM);//启用波形图
			mMediaPlayer.setWaveformListener(new MyWaveformListener(mMediaPlayer,waveLayout));
			mMediaPlayer.setListener(new MyOnInfoListener());
			if (audioPath != null) {
				mMediaPlayer.open(audioPath);
			}
		}
		
		if (diaryEditNote.isAddSoundTrack && !TextUtils.isEmpty(soundTrackPath)) {
			percent = diaryEditNote.percent;
			effectUtils.addMusic(mEffects, "effectcfg/audio/addmusic.cfg", soundTrackPath, 
					mMediaPlayer.getChannels(), 
					mMediaPlayer.getSampleRate(), 
					mMediaPlayer.getBitsPerChannel(), 
					percent, 
					1);
		}
		
	}
	
	/*private boolean isAddWaveformView = false;
	class MyWaveformListener implements XPlayerWaveformListener{
		@Override
		public void onWaveformDataPrepared() {
			// TODO Auto-generated method stub
			if(isAddWaveformView){
				mMediaPlayer.reloadWaveform();
				return;
			}
			isAddWaveformView = true;
			View view = mMediaPlayer.getWaveformView();
			if(view == null){
				Log.e(TAG, "[addWaveformView]getWaveformView is null");
				return;
			}
			RelativeLayout.LayoutParams ViewParams = new RelativeLayout.LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.FILL_PARENT);
			view.setLayoutParams(ViewParams);
			
			waveLayout.addView(view);
		}
	}*/
	
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
			tvTime.setText(convertTime(0));
			ivPlay.setImageResource(R.drawable.yinpin_play);
		}

		@Override
		public void onUpdateTime(XMediaPlayer player, double time) {
			final double time1 = time;
			runOnUiThread(new Runnable() {
				
				@Override
				public void run() {
					// TODO Auto-generated method stub
					tvTime.setText(convertTime(time1));
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
			Log.d(TAG,"onPreparedPlayer in");
			if (isPlayerNeedToPlay) {
				player.play();
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
	
	public static String convertTime(double time) {
		String minStr = "00";
		String secStr = "00";
		String milStr = "00";
		int second = (int) time;
		int minite = second / 60;
		int milSecond = (int) ((time - second) * 100);
		minStr = convertNum(minite);
		secStr = convertNum(second % 60);
		milStr = convertNum(milSecond);
		return minStr + ":" + secStr + "." + milStr;
	}
	
	private static String convertNum(int num) {
		String ret = "";
		if (num < 10) {
			ret = "0" + num;
		} else {
			ret = "" + num;
		}
		return ret;
	}
}
