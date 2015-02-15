package com.cmmobi.looklook.activity;

import java.util.List;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;
import android.widget.ToggleButton;
import cn.zipper.framwork.core.ZActivity;
import cn.zipper.framwork.core.ZApplication;

import com.cmmobi.looklook.R;
import com.cmmobi.looklook.common.gson.GsonResponse3.MyDiary;
import com.cmmobi.looklook.common.listener.MyWaveformListener;
import com.cmmobi.looklook.common.utils.BitmapUtils;
import com.cmmobi.looklook.common.web.TackView;
import com.cmmobi.looklook.info.location.POIAddressInfo;
import com.cmmobi.looklook.info.profile.AccountInfo;
import com.cmmobi.looklook.info.profile.ActiveAccount;
import com.cmmobi.looklook.info.profile.CommonInfo;
import com.cmmobi.looklook.info.profile.DiaryManager;
import com.cmmobi.looklook.info.profile.MediaValue;
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

public class EditPositionActivity extends ZActivity {
private final String TAG = "EditPositionActivity";
	
	private FrameLayout contentLayout = null;
	private DisplayMetrics dm = new DisplayMetrics();
	private ToggleButton toggleBtn = null;
	private TextView tvPosition = null;
	private ImageView ivWeather = null;
	private boolean isPositionVisiable = true;
	private ImageView ivDone = null;
	private ImageView ivBack = null;
	public static final int TAGSELECTED = 0x0013;
	private String userID = "";
	private String diaryUUID = "";
	private MyDiary myDiary = null;
	private FrameLayout videoLayout = null;
	private ImageView photoView = null;
	private RelativeLayout noteLayout = null;
	private RelativeLayout audioLayout = null;
	
	// 视频
	private RelativeLayout rlVideoContent = null;
	private XMediaPlayer mMediaPlayer = null;
	private XEffects mEffects = null;
	public EffectUtils effectUtils;
	private String videoPath = null;
	private String audioPath = null;
	private ImageView ivPlay = null;
	private boolean isPlayerNeedToPlay;
	private boolean isPlayerPrepared;
	private ImageView ivVideoCover = null;
	private RelativeLayout waveLayout = null;
	private RelativeLayout posRelativeLayout = null;
	private Bitmap videoCoverBmp = null;
	
	// 图片
	private String picPath = "";
	private Bitmap originBitmap = null;
	
	// 便签
	private String textContent = "";
	private String noteAudioPath = "";
	private String defaultPosStr = "";
	private String defaultPosStatus = "";
	private TackView tackView = null;
	
	private int[] SOUND_ICONS = {R.drawable.yuyinbiaoqian_3,R.drawable.yuyinbiaoqian_4,R.drawable.yuyinbiaoqian_2};

	private final int REQUEST_POSITION_INTENT = 0x0019; 
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_position);
		
		userID =  ActiveAccount.getInstance(ZApplication.getInstance()).getUID();
		String diaryString = getIntent().getStringExtra("diarystring");
		myDiary = new Gson().fromJson(diaryString,MyDiary.class);
				
		ivDone = (ImageView) findViewById(R.id.iv_edit_diary_save);
		ivBack = (ImageView) findViewById(R.id.iv_edit_diary_back);
		
		ivDone.setOnClickListener(this);
		ivBack.setOnClickListener(this);
		
		ivDone.setEnabled(false);
		
		videoLayout = (FrameLayout) findViewById(R.id.fl_edit_diary_video);
		photoView = (ImageView) findViewById(R.id.iv_edit_diary_photo);
		noteLayout = (RelativeLayout) findViewById(R.id.rl_edit_diary_note);
		audioLayout = (RelativeLayout) findViewById(R.id.rl_edit_diary_audio);
		
		posRelativeLayout = (RelativeLayout) findViewById(R.id.rl_edit_diary_position_weather_visiable);
		posRelativeLayout.setOnClickListener(this);
		
		String diaryType = myDiary.getDiaryMainType();
		if ("1".equals(diaryType)) {
			videoLayout.setVisibility(View.VISIBLE);
			photoView.setVisibility(View.GONE);
			noteLayout.setVisibility(View.GONE);
			audioLayout.setVisibility(View.GONE);
			loadVideoData();
		} else if ("2".equals(diaryType)) {
			videoLayout.setVisibility(View.GONE);
			audioLayout.setVisibility(View.VISIBLE);
			photoView.setVisibility(View.GONE);
			noteLayout.setVisibility(View.GONE);
			loadAudioData();
		} else if ("3".equals(diaryType)) {
			videoLayout.setVisibility(View.GONE);
			photoView.setVisibility(View.VISIBLE);
			noteLayout.setVisibility(View.GONE);
			audioLayout.setVisibility(View.GONE);
			loadPicture();
		} else if ("4".equals(diaryType) || "5".equals(diaryType) || "6".equals(diaryType)) {
			videoLayout.setVisibility(View.GONE);
			photoView.setVisibility(View.GONE);
			noteLayout.setVisibility(View.VISIBLE);
			audioLayout.setVisibility(View.GONE);
			loadNote();
		}
		
		contentLayout = (FrameLayout) findViewById(R.id.fl_edit_diary_content);
		dm = getResources().getDisplayMetrics();
		Log.d(TAG,"width = " + dm.widthPixels + " height = " + dm.heightPixels);
		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(dm.widthPixels,dm.widthPixels);
		params.addRule(RelativeLayout.BELOW,R.id.rl_edit_diary_info_top);
		contentLayout.setLayoutParams(params);
		
		tvPosition = (TextView) findViewById(R.id.tv_edit_diary_info_position);
		ivWeather = (ImageView) findViewById(R.id.iv_edit_diary_info_weather);
		
		toggleBtn = (ToggleButton) findViewById(R.id.tb_edit_diary_position_slip);
		defaultPosStatus = myDiary.position_status;
		defaultPosStr = myDiary.position_view;
		if ("1".equals(myDiary.position_status)) {
			toggleBtn.setChecked(true);
			ivWeather.setImageResource(R.drawable.tianqi_qing);
			tvPosition.setTextColor(getResources().getColor(R.color.black));
			tvPosition.setText(myDiary.position_view);
			myDiary.position_status = "1";
			posRelativeLayout.setEnabled(true);
		} else {
			toggleBtn.setChecked(false);
			ivWeather.setImageResource(R.drawable.tianqi_weizhi);
			tvPosition.setTextColor(getResources().getColor(R.color.gray));
			tvPosition.setText(R.string.position_info_invisiable);
			myDiary.position_status = "0";
			posRelativeLayout.setEnabled(false);
		}
		
		toggleBtn.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton arg0, boolean isChecked) {
				// TODO Auto-generated method stub
				Log.d(TAG,"isChecked = " + isChecked);
				if (isChecked) {
					CmmobiClickAgent.onEvent(EditPositionActivity.this, "show_location");
					ivWeather.setImageResource(R.drawable.tianqi_qing);
					tvPosition.setTextColor(getResources().getColor(R.color.black));
					tvPosition.setText(myDiary.position_view);
					myDiary.position_status = "1";
					posRelativeLayout.setEnabled(true);
					if ("1".equals(defaultPosStatus)) {
						if (defaultPosStr != null && defaultPosStr.equals(myDiary.position_view)) {
							ivDone.setEnabled(false);
						}
					} else {
						ivDone.setEnabled(true);
					}
				} else {
					//CmmobiClickAgent.onEvent(EditPositionActivity.this, "unshow_location");
					ivWeather.setImageResource(R.drawable.tianqi_weizhi);
					tvPosition.setTextColor(getResources().getColor(R.color.gray));
					tvPosition.setText(R.string.position_info_invisiable);
					myDiary.position_status = "0";
					posRelativeLayout.setEnabled(false);
					if ("0".equals(defaultPosStatus)) {
						ivDone.setEnabled(false);
					} else {
						ivDone.setEnabled(true);
					}
				}
			}
		});
	}
	
	private void loadVideoData() {
		rlVideoContent=(RelativeLayout) findViewById(R.id.video_view);
		rlVideoContent.setOnClickListener(this);
		
		effectUtils = new EffectUtils(this);
		if (PluginUtils.isPluginMounted()) {
			mEffects = new XEffects();
		}
		
		ivPlay = (ImageView) findViewById(R.id.iv_video_play);
		ivPlay.setOnClickListener(this);
		
		ivVideoCover = (ImageView) findViewById(R.id.iv_video_preview);
		
		
		videoPath = myDiary.getMainPath();
		Log.d(TAG,"videoPath = " + videoPath);
		if(null==mMediaPlayer) {
			mMediaPlayer = new XMediaPlayer(this, mEffects, true);
			mMediaPlayer.setUpdateTimePeriod(0.1f);
			mMediaPlayer.setListener(new MyOnInfoListener());
			rlVideoContent.addView((View)mMediaPlayer.getXSurfaceView());
			if (videoPath != null) {
				mMediaPlayer.open(videoPath);
			}
		}
		
		final String videoCoverUrl = myDiary.getVideoCoverUrl();
		ViewTreeObserver vto2 = ivVideoCover.getViewTreeObserver();   
		vto2.addOnGlobalLayoutListener(new OnGlobalLayoutListener() { 
			@Override   
			public void onGlobalLayout() { 
				if (videoCoverUrl != null) {
					Log.d(TAG,"loadVideoData videoFrontCoverUrl = " + videoCoverUrl);
					MediaValue mediaValue = AccountInfo.getInstance(userID).mediamapping.getMedia(userID, videoCoverUrl);
					if (mediaValue != null) {
						ivVideoCover.setVisibility(View.VISIBLE);
				    	int height = ivVideoCover.getMeasuredHeight(); 
				    	int width  = ivVideoCover.getMeasuredWidth(); 
				    	Log.d(TAG,"measuredwidth = " + width + " measuredheight = " + height);
				    	videoCoverBmp = BitmapUtils.readBitmapAutoSize(LookLookActivity.SDCARD_PATH + mediaValue.localpath, width, height);
						if (videoCoverBmp != null) {
							Log.d(TAG,"width = " + videoCoverBmp.getWidth() + " height = " + videoCoverBmp.getHeight());
						}
					} else {
						ivVideoCover.setImageBitmap(null);
					}
				} 
				
				if (videoCoverBmp != null) {
					ivVideoCover.setImageBitmap(videoCoverBmp);
					ivVideoCover.setVisibility(View.VISIBLE);
				}
				ivVideoCover.getViewTreeObserver().removeGlobalOnLayoutListener(this);         
			}   
		}); 
	}
	
	private void loadAudioData() {
		audioPath = myDiary.getMainPath();
		effectUtils = new EffectUtils(this);
		if (PluginUtils.isPluginMounted()) {
			mEffects = new XEffects();
		}
		
		ivPlay = (ImageView) findViewById(R.id.iv_play);
		ivPlay.setOnClickListener(this);
		waveLayout = (RelativeLayout) findViewById(R.id.rl_wave_layout);
		
		if(null==mMediaPlayer) {
			mMediaPlayer = new XMediaPlayer(this, mEffects, true);
			mMediaPlayer.setEnableWaveform(true,XEffectDefine.JAVA_A_PLAY_WAVEFORM);//启用波形图
			mMediaPlayer.setWaveformListener(new MyWaveformListener(mMediaPlayer,waveLayout));
			mMediaPlayer.setUpdateTimePeriod(0.1f);
			mMediaPlayer.setListener(new MyAudioOnInfoListener());
			if (audioPath != null) {
				mMediaPlayer.open(audioPath);
			}
		}
	}
	
	
	@Override
		protected void onDestroy() {
			// TODO Auto-generated method stub
		try {
			if(mMediaPlayer!=null){
				mMediaPlayer.release();
				mEffects.release();
				mMediaPlayer=null;
				mEffects = null;
				isPlayerNeedToPlay = false;
				isPlayerPrepared = false;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
			super.onDestroy();
		}
	
	private class MyOnInfoListener implements OnInfoListener {

		@Override
		public void onStartPlayer(XMediaPlayer player) {
			Log.e(TAG, "onStartPlayer");
			ivPlay.setImageResource(R.drawable.zanting);
			ivVideoCover.setVisibility(View.GONE);
		}

		@Override
		public void onStopPlayer(XMediaPlayer player) {
			Log.e(TAG, "onStopPlayer");
			ivPlay.setImageResource(R.drawable.play);
		}

		@Override
		public void OnFinishPlayer(XMediaPlayer player) {
			Log.e(TAG, "OnFinishPlayer");
			ivPlay.setImageResource(R.drawable.play);
			onClick(rlVideoContent);
			ivVideoCover.setVisibility(View.VISIBLE);
		}

		@Override
		public void onUpdateTime(XMediaPlayer player, double time) {
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
	
	private class MyAudioOnInfoListener implements OnInfoListener {

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
			ivPlay.setImageResource(R.drawable.yinpin_play);
		}

		@Override
		public void onUpdateTime(XMediaPlayer player, double time) {
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
	
	private void loadPicture() {
		picPath =  myDiary.getMainPath();
		
		ViewTreeObserver vto1 = photoView.getViewTreeObserver();   
		vto1.addOnGlobalLayoutListener(new OnGlobalLayoutListener() { 
			@Override   
			public void onGlobalLayout() {
				int height = photoView.getMeasuredHeight(); 
		    	int width  = photoView.getMeasuredWidth(); 
				originBitmap = BitmapUtils.readBitmapAutoSize(picPath, width, height);
				if (originBitmap == null) {
					Prompt.Alert(EditPositionActivity.this, "图片不存在");
					EditPositionActivity.this.finish();
					return;
				}
				photoView.setImageBitmap(originBitmap);
				photoView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
			}   
		});
	}
	
	private void loadNote() {
		tackView = (TackView) findViewById(R.id.ll_tackview);
		
		textContent = myDiary.getMainTextContent();
		noteAudioPath = myDiary.getMainPath();
		
		TextView contentView = (TextView) findViewById(R.id.tv_text_content);
		contentView.setText(textContent);
		
		if (noteAudioPath != null) {
			tackView.setVisibility(View.VISIBLE);
			tackView.setLocalAudio(noteAudioPath);
			tackView.setBackground(R.drawable.btn_yuyinbiaoqian);
			tackView.setSoundIcons(SOUND_ICONS);
			tackView.setOnClickListener(this);
		} else {
			tackView.setVisibility(View.GONE);
		}
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
		case R.id.iv_video_play:
			int status = mMediaPlayer.getStatus();
			if (status == XEffectMediaPlayer.STATUS_UNKNOW || status == XEffectMediaPlayer.STATUS_STOP || status == XEffectMediaPlayer.STATUS_OPENED) {
				if (status == XEffectMediaPlayer.STATUS_UNKNOW) {
					if (videoPath != null) {
						isPlayerPrepared = false;
						mMediaPlayer.open(videoPath);
					}
				}
				if (isPlayerPrepared) {
					mMediaPlayer.play();
					hidePlayBtn();
					ivVideoCover.setVisibility(View.GONE);
				} else {
					isPlayerNeedToPlay = true;
				}
			} else if (mMediaPlayer.getStatus() == XEffectMediaPlayer.STATUS_PALYING) {
				mMediaPlayer.pause();
				ivPlay.setImageResource(R.drawable.play);
				hidePlayBtn();
			} else if (mMediaPlayer.getStatus() == XEffectMediaPlayer.STATUS_PAUSE) {
				mMediaPlayer.resume();
				ivPlay.setImageResource(R.drawable.zanting);
				hidePlayBtn();
			}
			break;
		case R.id.iv_play:
			int statu = mMediaPlayer.getStatus();
			if (statu == XEffectMediaPlayer.STATUS_UNKNOW || statu == XEffectMediaPlayer.STATUS_STOP || statu == XEffectMediaPlayer.STATUS_OPENED) {
				if (statu == XEffectMediaPlayer.STATUS_UNKNOW) {
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
		case R.id.video_view:
			ivPlay.setVisibility(View.VISIBLE);
			hidePlayBtn();
			break;
		case R.id.ll_tackview:
			tackView.playAudio(noteAudioPath);
			break;
		case R.id.iv_edit_diary_save:
			String diaryString = new Gson().toJson(myDiary);
			Intent tagIntent = new Intent();
			tagIntent.putExtra(DiaryEditPreviewActivity.INTENT_ACTION_DIARY_STRING, diaryString);
			setResult(RESULT_OK, tagIntent);
			finish();
			break;
		case R.id.iv_edit_diary_back:
			finish();
			break;
		case R.id.rl_edit_diary_position_weather_visiable:
			if (!toggleBtn.isChecked()) {
				return;
			}
			Intent intent = new Intent();
			intent.setClass(this, PositionSelectActivity.class);
			startActivityForResult(intent, REQUEST_POSITION_INTENT);
			break;
		}
	}
	
	private void hidePlayBtn() {
		handler.removeCallbacks(hidePlayBtn);
		handler.postDelayed(hidePlayBtn, 2000);
	}
	
	Runnable hidePlayBtn = new Runnable() {
		
		@Override
		public void run() {
			// TODO Auto-generated method stub
			ivPlay.setVisibility(View.GONE);
		}
	};
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == REQUEST_POSITION_INTENT && data != null) {
			
			String positionStr = data.getStringExtra("position");
			String longitude = data.getStringExtra("longitude");
			String latitude = data.getStringExtra("latitude");
			POIAddressInfo addInfo = new POIAddressInfo();
			addInfo.position = positionStr;
			addInfo.longitude = longitude;
			addInfo.latitude = latitude;
			Log.d(TAG,"freqPosList = " + positionStr + " longitude = " + longitude + " latitude = " + latitude);
			
			if (positionStr == null) {
				return;
			}
			
			if (positionStr.equals(getString(R.string.position_not_visiable))) {
				toggleBtn.setChecked(false);
				ivWeather.setImageResource(R.drawable.tianqi_weizhi);
				tvPosition.setTextColor(getResources().getColor(R.color.gray));
				tvPosition.setText(R.string.position_info_invisiable);
				myDiary.position_status = "0";
				posRelativeLayout.setEnabled(false);
				if ("0".equals(defaultPosStatus)) {
					ivDone.setEnabled(false);
				}
				return;
			}
			
			myDiary.position_view = positionStr;
			myDiary.longitude_view = longitude;
			myDiary.latitude_view = latitude;
			
			if (defaultPosStr != null && defaultPosStr.endsWith(myDiary.position_view)) {
				ivDone.setEnabled(false);
			} else {
				ivDone.setEnabled(true);
			}
			
			tvPosition.setTextColor(getResources().getColor(R.color.black));
			tvPosition.setText(myDiary.position_view);
			toggleBtn.setChecked(true);
			
			List<POIAddressInfo> freqPosList = CommonInfo.getInstance().frequentpos;
			
			if (freqPosList.contains(addInfo)) {
				Log.d(TAG,"onActivityResult if");
				freqPosList.remove(addInfo);
				freqPosList.add(0, addInfo);
			} else {
				Log.d(TAG,"onActivityResult else " + positionStr);
				freqPosList.add(0,addInfo);
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
}
