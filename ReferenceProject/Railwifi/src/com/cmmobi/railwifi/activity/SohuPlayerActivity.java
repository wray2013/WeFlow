package com.cmmobi.railwifi.activity;

import java.io.IOException;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnBufferingUpdateListener;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnSeekCompleteListener;
import android.net.Uri;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.Message;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.text.TextUtils;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.cmmobi.railwifi.R;
import com.cmmobi.railwifi.dao.PlayHistory;
import com.cmmobi.railwifi.dialog.PromptDialog;
import com.cmmobi.railwifi.network.Requester;
import com.cmmobi.railwifi.sql.HistoryManager;
import com.cmmobi.railwifi.utils.DateUtils;
import com.cmmobi.railwifi.utils.DisplayUtil;
import com.google.gson.Gson;
import com.sohu.sohuvideo.ui.view.BatteryView;

public class SohuPlayerActivity extends TitleRootActivity implements OnClickListener, OnBufferingUpdateListener,  OnCompletionListener,MediaPlayer.OnErrorListener, MediaPlayer.OnPreparedListener,MediaPlayer.OnInfoListener, OnSeekCompleteListener, SurfaceHolder.Callback {
	private static final String TAG = "SohuPlayerActivity";
	// VideoView
	private SurfaceView mSurfaceView;
	private MediaPlayer mediaPlayer;
	// 视频播放拖动条
	private SeekBar mSeekBar;
	private BatteryView battery_view_fcc;
//	private Timer mTimer; 
	
	// 头部RelativeLayout
	private RelativeLayout mTopView;

	// 底部RelativeLayout
	private RelativeLayout mBottomView;

	private ImageView ib_back_video;
	private ImageView mPlay;
	private TextView mPlayTime;
	private TextView mDurationTime;
	private TextView textview_time_fcc;
	
	//loading 
	private LinearLayout progress_layout_fcc;
	private TextView progress_title_fcc;
	
	//fortward/backward
	private LinearLayout gesture_layout_progress;
	private ImageView gesture_backward_progress;
	private ImageView gesture_forward_progress;
	private TextView gesture_cur_progress;
	private TextView gesture_total_progress;
	
	// 音频管理器
	private AudioManager mAudioManager;

	// 屏幕宽高
	private float width;
	private float height;

	// 视频播放时间
	private String percent;
	
	private String videoUrl;
	// 自动隐藏顶部和底部View的时间
	private static final int HIDE_TIME = 5000;
	private static final int HANDLER_FLAG_PLAY_PRGRRESS_STEP = 0x17839711;
	private static final int HADNDLER_FLAG_HIDE_DELAY = 0x17839712;
	private static final int HADNDLER_VOLUME_HIDE_DELAY = 0x17839713;
	private static final int HADNDLER_BRIGHT_HIDE_DELAY = 0x17839714;
	private static final int HADNDLER_SEEK_PLAY = 0x17839715;
	private static final int HADNDLER_LOCK_HIDE_DELAY = 0x17839716;
	private static final int HANDLER_FLAG_PLAY_URL = 0x17839717;
	
	private BroadcastReceiver batteryLevelRcvr;
	private IntentFilter batteryLevelFilter;
	private int batteryPercent = 50;
	private boolean isCharging = false;

	private LinearLayout mVolumeLayout;
	private LinearLayout mBrightnessLayout;
	
	private ImageView mVolumeOperationBg;
	private TextView mVolumeOperationPercent;
	
	private ImageView mBrightnessOperationBg;
	private TextView mBrightnessOperationPercent;
	
	/** 最大声音 */
	private int mMaxVolume;
	/** 当前声音 */
	private int mVolume = -1;
	/** 当前亮度 */
	private float mBrightness = -1f;
	
	/** 当前进度 */
	private int mPosition = 0;
	private int mDuration = 0;
	
	private int toSeekTime;
	private int gestureType;//0 unit , 1 volume, 2, bright, 3 left and right
	
	private GestureDetector mGestureDetector;
	
	private TextView mtvTitle;
	private ImageView tvLock;
	private boolean mLockStatus = false;
	private boolean userPause = false;

	private Gson gson = new Gson();

	private PlayHistory playhistory;

	private SurfaceHolder surface_holder;
	private boolean isPlaying = false;
	private PowerManager pManager;
	private WakeLock mWakeLock;

		
	public static String KEY_NAME = "name";
	public static String KEY_PATH = "path";
	public static String KEY_PLAYHISTORY = "playhistory";
	public static String KEY_MEDIA_ID = "key_media_id";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		hideTitlebar();

		mSurfaceView = (SurfaceView) findViewById(R.id.videoview);
		battery_view_fcc = (BatteryView)findViewById(R.id.battery_view_fcc);
		ib_back_video = (ImageView)findViewById(R.id.top_back_fcc); //ib_back_video
		textview_time_fcc = (TextView)findViewById(R.id.textview_time_fcc);
		mPlayTime = (TextView) findViewById(R.id.textview_currenttime_fcc);//R.id.play_time
		mDurationTime = (TextView) findViewById(R.id.textview_duration_fcc); //R.id.total_time
		mPlay = (ImageView) findViewById(R.id.button_playorpause_fcc); //R.id.play_btn
		mSeekBar = (SeekBar) findViewById(R.id.seekbar_progress_fcc);//R.id.seekbar
		mTopView = (RelativeLayout)findViewById(R.id.layout_title_fcc); //R.id.top_layout
		mBottomView = (RelativeLayout)findViewById(R.id.relalay_bottom_fcc); //R.id.bottom_layout

		mVolumeLayout = (LinearLayout)findViewById(R.id.gesture_layout_volumn); //R.id.operation_volume_brightness
		mBrightnessLayout = (LinearLayout)findViewById(R.id.gesture_layout_light); //R.id.operation_volume_brightness
		
		mVolumeOperationBg = (ImageView) findViewById(R.id.gesture_icon_volumn); //R.id.operation_bg
		mVolumeOperationPercent = (TextView) findViewById(R.id.gesture_percent_volumn); //R.id.operation_percent
		
		mBrightnessOperationBg = (ImageView) findViewById(R.id.gesture_icon_light); //R.id.operation_bg
		mBrightnessOperationPercent = (TextView) findViewById(R.id.gesture_percent_light); //R.id.operation_percent
		
		mtvTitle = ((TextView)findViewById(R.id.textview_title_fcc));
		tvLock = (ImageView)findViewById(R.id.lock_image_fcc);
		
		progress_layout_fcc = (LinearLayout)findViewById(R.id.progress_layout_fcc);
		progress_title_fcc = (TextView)findViewById(R.id.progress_title_fcc);
		
		gesture_layout_progress = (LinearLayout)findViewById(R.id.gesture_layout_progress);
		gesture_backward_progress = (ImageView)findViewById(R.id.gesture_backward_progress);
		gesture_forward_progress = (ImageView)findViewById(R.id.gesture_forward_progress);
		gesture_cur_progress = (TextView)findViewById(R.id.gesture_cur_progress);
		gesture_total_progress = (TextView)findViewById(R.id.gesture_total_progress);
		
		mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
		mMaxVolume = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
		
		width = DisplayUtil.getScreenWidth(this);
		height = DisplayUtil.getScreenHeight(this);

		ib_back_video.setOnClickListener(this);
		mPlay.setOnClickListener(this);
		mSeekBar.setOnSeekBarChangeListener(mSeekBarChangeListener);
		mGestureDetector = new GestureDetector(this, new MyGestureListener());

		tvLock.setOnClickListener(this);

		
		Intent intent = getIntent();
		String movietype = intent.getStringExtra(VideoPlayerActivity.KEY_MOVIE_TYPE);
		String play_history_str = intent.getStringExtra(KEY_PLAYHISTORY);
	    if(!TextUtils.isEmpty(play_history_str)){
	    	playhistory = gson.fromJson(play_history_str, PlayHistory.class);
	    	percent = playhistory.getPercent();
	    	mtvTitle.setText(playhistory.getName());
	    	
	    	if(!TextUtils.isEmpty(playhistory.getMedia_id())){
	    		Requester.requestMoviePlay(handler, playhistory.getMedia_id(), movietype);
	    		//videoUrl = Config.SERVER_RIA_URL + Requester.RIA_INTERFACE_MOVIE_PLAY + "?requestapp={\"media_id\":" + playhistory.getMedia_id() + "}&" + playhistory.getMedia_id() + ".smo";
	    	}
	    	if(!TextUtils.isEmpty(playhistory.getSrc_url())){
	    		videoUrl = playhistory.getSrc_url();
	    	}else{
	    		PromptDialog.Dialog(this, false, "播放地址错误", "对不起，播放地址错误", "稍候再试", null, new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						finish();
					}
				}, null);
	    	}
	    }else{
	    	String namekey = intent.getStringExtra(KEY_NAME);
	    	String pathkey = intent.getStringExtra(KEY_PATH);
	    	String mediaidkey = intent.getStringExtra(KEY_MEDIA_ID);
	    	if(!TextUtils.isEmpty(namekey)){
	    		mtvTitle.setText(namekey);
	    	}
	    	if(!TextUtils.isEmpty(mediaidkey)){
	    		Requester.requestMoviePlay(handler, mediaidkey, movietype);
	    		//videoUrl = Config.SERVER_RIA_URL + Requester.RIA_INTERFACE_MOVIE_PLAY + "?requestapp={\"media_id\":" + mediaidkey + "}&" + mediaidkey + ".smo";
	    	}
	    	if(!TextUtils.isEmpty(pathkey)){
	    		videoUrl = pathkey;
	    	}else{
	    		PromptDialog.Dialog(this, false, "播放地址错误", "对不起，播放地址错误", "稍候再试", null, new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						finish();
					}
				}, null);
	    	}	
	    }	
	    
		initView();
		
		monitorBatteryState();
		
		isPlaying = false;
		

	}
	
	@Override
	public void onResume(){
		super.onResume();
		surface_holder = mSurfaceView.getHolder(); 
		surface_holder.addCallback(this);  
		surface_holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);/* 设置视频类型 */

		if (mediaPlayer!=null && mediaPlayer.isPlaying() && mSeekBar.isPressed() == false) {
			handler.sendEmptyMessage(HANDLER_FLAG_PLAY_PRGRRESS_STEP);
		}

		playUrl(videoUrl, isPlaying);
		
		pManager = ((PowerManager) getSystemService(POWER_SERVICE));
		mWakeLock = pManager.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK | PowerManager.ON_AFTER_RELEASE, TAG);
		mWakeLock.acquire();

	}
	
	
	public void playUrl(String videoUrl, boolean beResume)
	{
		Log.v(TAG, "playUrl : " + videoUrl + ", mediaPlayer:" + mediaPlayer + ", beResume:" + beResume);
		if(beResume){
			if(!userPause){
				mediaPlayer.start();
			}

			return;
		}
		
		if(mediaPlayer==null){
			mediaPlayer = new MediaPlayer();
		}else{
			mediaPlayer.reset();
			mediaPlayer.release();
			mediaPlayer = new MediaPlayer();
		}
		
		mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
		mediaPlayer.setOnBufferingUpdateListener(this);
		mediaPlayer.setOnPreparedListener(this);
		mediaPlayer.setOnSeekCompleteListener(this);
		mediaPlayer.setOnInfoListener(this);
		
		try {
			mediaPlayer.setDataSource(videoUrl);
			mediaPlayer.prepareAsync();
			progress_layout_fcc.setVisibility(View.VISIBLE);
			progress_title_fcc.setText(R.string.loading);
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	public void pause()
	{
		if(mediaPlayer!=null){
			mediaPlayer.pause();
		}
	}
	
	public void stop()
	{
		if (mediaPlayer != null) { 
			mediaPlayer.stop();
            mediaPlayer.release(); 
            mediaPlayer = null; 
        } 
	}

	
	private void initView(){
		textview_time_fcc.setText(DateUtils.getStringFromMilli(String.valueOf(System.currentTimeMillis()), "HH:mm"));
	}

	@Override
	protected void onPause() {
		super.onPause();
		if(playhistory!=null){
			playhistory.setLocation(getLocation());
			playhistory.setPercent(getPercent());
			HistoryManager.getInstance().putPlayHistoryItem(playhistory);
		}
		
		pause();
		
		if(null != mWakeLock){
			mWakeLock.release();
		}
	}

	public String getPercent(){
		if(mediaPlayer == null) return "0";
		return mediaPlayer.getCurrentPosition()+"";
	}
	
	public String getLocation(){
		if(mediaPlayer == null) return "";
		return DateUtils.getFormatTime(mediaPlayer.getCurrentPosition());
	}
	
	private OnSeekBarChangeListener mSeekBarChangeListener = new OnSeekBarChangeListener() {

		private long lastSeekTS = 0;

		@Override
		public void onStopTrackingTouch(SeekBar seekBar) {
			handler.postDelayed(hideRunnable, HIDE_TIME);
		}

		@Override
		public void onStartTrackingTouch(SeekBar seekBar) {
			handler.removeCallbacks(hideRunnable);
		}

		@Override
		public void onProgressChanged(SeekBar seekBar, int progress,
				boolean fromUser) {
			if (fromUser) {
				long nowTs = System.currentTimeMillis();
				if(nowTs - lastSeekTS>1000){
					int time = (int)(progress * mediaPlayer.getDuration() / 100);
					mediaPlayer.pause();
					mediaPlayer.seekTo(time);
				}
				
				lastSeekTS = nowTs;
				handler.removeCallbacks(hideRunnable);
				handler.postDelayed(hideRunnable, HIDE_TIME);
			}
		}
	};

	private class MyGestureListener extends SimpleOnGestureListener {

		@Override
		public boolean onSingleTapUp(MotionEvent e) {
			// TODO Auto-generated method stub
			showOrHide();
			gestureType = 0;
			return super.onSingleTapUp(e);
		}
		
		/** 滑动 */
		@Override
		public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
					
			float mOldX = e1.getRawX(), mOldY = e1.getRawY();
			int deltaX = (int) (mOldX - e2.getRawX());
			int deltaY = (int) (mOldY - e2.getRawY());
			
			//gestureType : 0 unit , 1 volume, 2, bright, 3 left and right
			if(Math.abs(deltaX) > Math.abs(deltaY)){
				if(gestureType==0 || gestureType==3){
					gestureType = 3;
					onSeekSlide(-deltaX);
				}

			}else{
				if (mOldX > width * 1/2){// volume 右边滑动
					if(gestureType==0 || gestureType==1){
						gestureType = 1;
						onVolumeSlide(deltaY);
					}
				}

				else if (mOldX < width /2){// 左边滑动
					if(gestureType==0 || gestureType==2){
						gestureType = 2;
						onBrightnessSlide(deltaY);
					}
				}

			}
			
			return super.onScroll(e1, e2, distanceX, distanceY);
		}
	}
	
	/** 手势结束 */
	private void endGesture() {
		if(gestureType==1){
			handler.sendEmptyMessageDelayed(HADNDLER_VOLUME_HIDE_DELAY, 1000);
		}else if(gestureType==2){
			handler.sendEmptyMessageDelayed(HADNDLER_BRIGHT_HIDE_DELAY, 1000);
		}else if(gestureType==3){
			handler.obtainMessage(HADNDLER_SEEK_PLAY).sendToTarget();
		}
		gestureType = 0;
		
	}
	
	
	/**
	 * 滑动改变声音大小
	 * 
	 * @param percent
	 */
	private void onVolumeSlide(float deltaY) {
		if (mVolume == -1) {
			mVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
			if (mVolume < 0)
				mVolume = 0;

			mVolumeLayout.setVisibility(View.VISIBLE);
			mBrightnessLayout.setVisibility(View.GONE);
			gesture_layout_progress.setVisibility(View.GONE);
		}

		int index = (int) (deltaY*1.5/height * mMaxVolume) + mVolume;
		if (index > mMaxVolume)
			index = mMaxVolume;
		else if (index < 0)
			index = 0;

		// 变更声音
		mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, index, 0);
		
		// 显示
		if(index==0){
			mVolumeOperationBg.setImageResource(R.drawable.player_silence);	
		}else{
			mVolumeOperationBg.setImageResource(R.drawable.player_volume);	
		}
		
		int percent = index * 100 / mMaxVolume;

		// 变更进度条
		mVolumeLayout.setVisibility(View.VISIBLE);
		mVolumeOperationPercent.setText(percent + " %");
	}

	/**
	 * 滑动改变亮度
	 * 
	 * @param percent
	 */
	private void onBrightnessSlide(float deltaY) {
		if (mBrightness < 0) {
			mBrightness = getWindow().getAttributes().screenBrightness;
			if (mBrightness <= 0.00f)
				mBrightness = 0.50f;
			if (mBrightness < 0.01f)
				mBrightness = 0.01f;

			// 显示
			mBrightnessLayout.setVisibility(View.VISIBLE);
			mVolumeLayout.setVisibility(View.GONE);
			gesture_layout_progress.setVisibility(View.GONE);
		}
		WindowManager.LayoutParams lpa = getWindow().getAttributes();
		lpa.screenBrightness = mBrightness + deltaY/height;
		if (lpa.screenBrightness > 1.0f)
			lpa.screenBrightness = 1.0f;
		else if (lpa.screenBrightness < 0.01f)
			lpa.screenBrightness = 0.01f;
		getWindow().setAttributes(lpa);
		
		int percent = (int) (lpa.screenBrightness*100);

		// 变更进度条
		mBrightnessLayout.setVisibility(View.VISIBLE);
		mBrightnessOperationPercent.setText(percent + " %");
	}
	
	/**
	 * 滑动改变进度 
	 * 
	 * @param percent
	 */
	private void onSeekSlide(float deltaX) {
		mPosition = mediaPlayer.getCurrentPosition();
		mDuration = mediaPlayer.getDuration();

		int current = mPosition;
		int forwardTime = (int) ((deltaX / width) * 840000);
		int currentTime = current + forwardTime;
		if(currentTime<0){
			currentTime = 0;
		}
		
		if(currentTime>mDuration){
			currentTime = mDuration;
		}
		
		gesture_layout_progress.setVisibility(View.VISIBLE);
		mVolumeLayout.setVisibility(View.GONE);
		mBrightnessLayout.setVisibility(View.GONE);
		if(forwardTime>0){
			gesture_forward_progress.setVisibility(View.VISIBLE);
			gesture_backward_progress.setVisibility(View.GONE);
		}else{
			gesture_forward_progress.setVisibility(View.GONE);
			gesture_backward_progress.setVisibility(View.VISIBLE);
		}
		
		String strCurrentTime = DateUtils.getFormatTime(currentTime);
		String strDuration = DateUtils.getFormatTime(mDuration);
		gesture_cur_progress.setText(strCurrentTime);
		gesture_total_progress.setText(strDuration);
		toSeekTime = currentTime;
		Log.v(TAG, "onSeekSlide - forwardTime:" +forwardTime+", toSeekTime:"+toSeekTime+", strDuration:"+ strDuration);
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		handler.removeMessages(0);
		handler.removeCallbacksAndMessages(null);
		unregisterReceiver(batteryLevelRcvr);
	}


	public static boolean isNumeric(String str) {
		Pattern pattern = Pattern.compile("[0-9]*");
		Matcher isNum = pattern.matcher(str);
		if (!isNum.matches()) {
			return false;
		}
		return true;
	}
	
	private void monitorBatteryState() {
		batteryLevelRcvr = new BroadcastReceiver() {

			public void onReceive(Context context, Intent intent) {
				int rawlevel = intent.getIntExtra("level", -1);
				int scale = intent.getIntExtra("scale", -1);
				int status = intent.getIntExtra("status", -1);
				int health = intent.getIntExtra("health", -1);
				int level = -1; // percentage, or -1 for unknown
				if (rawlevel >= 0 && scale > 0) {
					level = (rawlevel * 100) / scale;
					batteryPercent = level;
				}

				switch (status) {
				case BatteryManager.BATTERY_STATUS_UNKNOWN:
					//sb.append("no battery.");
					isCharging = true;
					break;
				case BatteryManager.BATTERY_STATUS_CHARGING:
					isCharging = true;
					break;
				case BatteryManager.BATTERY_STATUS_DISCHARGING:
				case BatteryManager.BATTERY_STATUS_NOT_CHARGING:
					isCharging = false;
					break;
				case BatteryManager.BATTERY_STATUS_FULL:
					isCharging = true;
					batteryPercent = 100;
					break;
				default:
					break;
				}
				
				battery_view_fcc.setPower(batteryPercent, isCharging);
			}
		};
		batteryLevelFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
		registerReceiver(batteryLevelRcvr, batteryLevelFilter);
	}

	

	private Runnable hideRunnable = new Runnable() {

		@Override
		public void run() {
			hide();
		}
	};
	

	public boolean onTouchEvent(MotionEvent event) {
		System.out.println("==== ontouch ====");
		if(mLockStatus){
			tvLock.setVisibility(View.VISIBLE);
			handler.sendEmptyMessageDelayed(HADNDLER_LOCK_HIDE_DELAY, HIDE_TIME);
			return super.onTouchEvent(event);
		}
		
		if(event.getAction() == MotionEvent.ACTION_DOWN){
			mPosition = mediaPlayer.getCurrentPosition();
		}
		
		if (mGestureDetector.onTouchEvent(event))
			return true;

		// 处理手势结束
		switch (event.getAction() & MotionEvent.ACTION_MASK) {
		case MotionEvent.ACTION_UP:
			endGesture();
			break;
		}

		return super.onTouchEvent(event);
	}
	
	
	@Override
	public void onClick(View v) {
		System.out.println("==== onclick ====");
		switch (v.getId()) {
		case R.id.button_playorpause_fcc: //R.id.play_btn
			if (mediaPlayer.isPlaying()) {
				mediaPlayer.pause();
				userPause = true;
				mPlay.setImageResource(R.drawable.player_icon_play);
				
			} else {
				mediaPlayer.start();
				userPause = false;
				mPlay.setImageResource(R.drawable.player_icon_pause);
			}
			handler.removeCallbacks(hideRunnable);
			handler.postDelayed(hideRunnable, HIDE_TIME);
			break;
		case R.id.top_back_fcc: //R.id.ib_back_video
			exit();
			break;
		case R.id.lock_image_fcc:
			if(mLockStatus){
				mLockStatus = false;
				tvLock.setImageResource(R.drawable.btn_unlock_screen_bg);
				show();
			}else{
				mLockStatus = true;
				tvLock.setImageResource(R.drawable.player_locked);
			}
			handler.removeCallbacks(hideRunnable);
			handler.postDelayed(hideRunnable, HIDE_TIME);
			handler.removeMessages(HADNDLER_LOCK_HIDE_DELAY);
			handler.sendEmptyMessageDelayed(HADNDLER_LOCK_HIDE_DELAY, HIDE_TIME);
			break;
		default:
			break;
		}
	}
	
	
	private void showOrHide() {
		if (mTopView.getVisibility() == View.VISIBLE) {
			hide();
		} else {
			show();
			handler.removeCallbacks(hideRunnable);
			handler.postDelayed(hideRunnable, HIDE_TIME);
			handler.removeMessages(HADNDLER_LOCK_HIDE_DELAY);
			handler.sendEmptyMessageDelayed(HADNDLER_LOCK_HIDE_DELAY, HIDE_TIME);
		}

	}

	
	private void show() {
		mTopView.setVisibility(View.VISIBLE);
		mTopView.clearAnimation();
		Animation animation = AnimationUtils.loadAnimation(this,
				R.anim.option_entry_from_top);
		mTopView.startAnimation(animation);

		mBottomView.setVisibility(View.VISIBLE);
		mBottomView.clearAnimation();
		Animation animation1 = AnimationUtils.loadAnimation(this,
				R.anim.option_entry_from_bottom);
		mBottomView.startAnimation(animation1);
		tvLock.setVisibility(View.VISIBLE);
		
		textview_time_fcc.setText(DateUtils.getStringFromMilli(String.valueOf(System.currentTimeMillis()), "HH:mm"));
	}
	
	private void hide(){
		mTopView.clearAnimation();
		Animation animation = AnimationUtils.loadAnimation(this, R.anim.option_leave_from_top);
		animation.setAnimationListener(new AnimationImp() {
			@Override
			public void onAnimationEnd(Animation animation) {
				super.onAnimationEnd(animation);
				mTopView.setVisibility(View.GONE);
			}
		});
		mTopView.startAnimation(animation);

		mBottomView.clearAnimation();
		Animation animation1 = AnimationUtils.loadAnimation(this,
				R.anim.option_leave_from_bottom);
		animation1.setAnimationListener(new AnimationImp() {
			@Override
			public void onAnimationEnd(Animation animation) {
				super.onAnimationEnd(animation);
				mBottomView.setVisibility(View.GONE);
			}
		});
		mBottomView.startAnimation(animation1);
		tvLock.setVisibility(View.GONE);
		handler.removeCallbacks(hideRunnable);
	
	
	}

	
	private class AnimationImp implements AnimationListener {

		@Override
		public void onAnimationEnd(Animation animation) {

		}

		@Override
		public void onAnimationRepeat(Animation animation) {
		}

		@Override
		public void onAnimationStart(Animation animation) {
		}

	}

	@Override
	public boolean handleMessage(Message msg) {
		// TODO Auto-generated method stub
		switch(msg.what){
		case HADNDLER_LOCK_HIDE_DELAY:
			tvLock.setVisibility(View.GONE);
			break;
		case HADNDLER_SEEK_PLAY:
			if(toSeekTime>=0 && toSeekTime<=mDuration){
				mediaPlayer.pause();
				mediaPlayer.seekTo((int)(toSeekTime));
				mSeekBar.setProgress((int)(toSeekTime*100/mediaPlayer.getDuration()));
				mPlayTime.setText(DateUtils.getFormatTime(mediaPlayer.getCurrentPosition()));
			}
			toSeekTime = -1;
			gesture_layout_progress.setVisibility(View.GONE);
			break;
		case HADNDLER_BRIGHT_HIDE_DELAY:
			mBrightnessLayout.setVisibility(View.GONE);
			break;
		case HADNDLER_VOLUME_HIDE_DELAY:
			mVolumeLayout.setVisibility(View.GONE);
			break;
		case HADNDLER_FLAG_HIDE_DELAY:
			/** 定时隐藏 */
			mVolumeLayout.setVisibility(View.GONE);
			break;
		case HANDLER_FLAG_PLAY_PRGRRESS_STEP:
			if (mediaPlayer!=null && mediaPlayer.getCurrentPosition() > 0) {
				mPosition = mediaPlayer.getCurrentPosition();
				mDuration = mediaPlayer.getDuration();

				int progress = (int)(mPosition * mSeekBar.getMax() / mDuration);
				mSeekBar.setProgress(progress);
				mPlayTime.setText(DateUtils.getFormatTime(mediaPlayer.getCurrentPosition()));
				
				if (mPosition > mDuration - 100) {
					playOver();
				}

			} else {
				playOver();
			}
			

			
//			if (mDuration > 0) {
//				long pos = mSeekBar.getMax() * mPosition / mDuration;
//				mSeekBar.setProgress((int) pos);	
//			}
			
			if (mediaPlayer!=null && mediaPlayer.isPlaying() && mSeekBar.isPressed() == false) {
				progress_layout_fcc.setVisibility(View.GONE) ;
				handler.sendEmptyMessage(HANDLER_FLAG_PLAY_PRGRRESS_STEP);
			}
			break;
		}
		return false;
	}


	@Override
	public int subContentViewId() {
		// TODO Auto-generated method stub
		return R.layout.vw_full_media_controller;
	}


	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		System.out.println("SurfaceHolder.Callback.surfaceCreated - mediaPlayer:" + mediaPlayer);
		try {
			if(mediaPlayer==null){
				mediaPlayer = new MediaPlayer();
				mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
				mediaPlayer.setOnBufferingUpdateListener(this);
				mediaPlayer.setOnPreparedListener(this);
				mediaPlayer.setOnSeekCompleteListener(this);
				mediaPlayer.setOnInfoListener(this);
			}
			
			mediaPlayer.setDisplay(surface_holder);
			
		} catch (Exception e) {
			Log.e("mediaPlayer", "error", e);
		}
	}


	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		// TODO Auto-generated method stub
		System.out.println("SurfaceHolder.Callback.surfaceChanged : Surface 大小发生改变");
	}


	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		System.out.println("SurfaceHolder.Callback.surfaceDestroyed : Surface 销毁");
	}


	@Override
	public void onPrepared(MediaPlayer mp) {
		// TODO Auto-generated method stub
		mediaPlayer = mp;
		int videoWidth = mediaPlayer.getVideoWidth();
		int videoHeight = mediaPlayer.getVideoHeight();
		if (videoHeight != 0 && videoWidth != 0) {
			mediaPlayer.start();
			isPlaying = true;
			if(mPosition!=0){
				mediaPlayer.seekTo(mPosition);
				mSeekBar.setProgress((int)(mPosition*100.0f/mediaPlayer.getDuration()));
				mPlayTime.setText(DateUtils.getFormatTime(mediaPlayer.getCurrentPosition()));
			}else if (!TextUtils.isEmpty(percent)&& !"0".equals(percent)&&isNumeric(percent)) {
				long currentTime = Long.parseLong(percent);
				mediaPlayer.seekTo((int)(currentTime));
				mSeekBar.setProgress((int)(currentTime*100.0f/mediaPlayer.getDuration()));
				mPlayTime.setText(DateUtils.getFormatTime(mediaPlayer.getCurrentPosition()));
			}

			handler.removeCallbacks(hideRunnable);
			handler.postDelayed(hideRunnable, HIDE_TIME);
			mDurationTime.setText(DateUtils.getFormatTime(mediaPlayer.getDuration()));
		}
		Log.e("mediaPlayer", "onPrepared");
		progress_layout_fcc.setVisibility(View.GONE);
	}


	@Override
	public void onCompletion(MediaPlayer mp) {
		// TODO Auto-generated method stub
		playOver();
	}


	@Override
	public void onBufferingUpdate(MediaPlayer mp, int bufferingProgress) {
		// TODO Auto-generated method stub

		mSeekBar.setSecondaryProgress(bufferingProgress);
		progress_title_fcc.setText(String.valueOf(bufferingProgress) + "%") ;
	}

	@Override
	public void onSeekComplete(MediaPlayer mp) {
		// TODO Auto-generated method stub
		Log.i(TAG, "onSeekComplete - mp:" + mp);
		if(!userPause){
			mp.start(); 
		}
		handler.sendEmptyMessage(HANDLER_FLAG_PLAY_PRGRRESS_STEP);
		progress_layout_fcc.setVisibility(View.GONE);
	}

	@Override
	public boolean onError(MediaPlayer mp, int what, int extra) {
		// TODO Auto-generated method stub
		handler.post(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				mediaPlayer.release();
				mediaPlayer = null;
				playUrl(videoUrl, false);
			}
		});
		return true;
	}
	
	@Override
	public void onBackPressed(){
		exit();
	}
	
	private void playOver(){
		mPlay.setImageResource(R.drawable.player_icon_play);
		mPlayTime.setText("00:00");
		mSeekBar.setProgress(0);
		mPosition = 0;
	}
	
	private void exit(){
		if(mediaPlayer!=null){
			mediaPlayer.stop();
			mediaPlayer.release();
			mediaPlayer = null;
			finish();
		}
	}

	@Override
	public boolean onInfo(MediaPlayer mp, int what, int extra) {
		// TODO Auto-generated method stub
		if(what==MediaPlayer.MEDIA_INFO_BUFFERING_START){
			progress_layout_fcc.setVisibility(View.VISIBLE) ;
		}else if(what==MediaPlayer.MEDIA_INFO_BUFFERING_END){
			progress_layout_fcc.setVisibility(View.GONE) ;
		}
		return false;
	}

}
