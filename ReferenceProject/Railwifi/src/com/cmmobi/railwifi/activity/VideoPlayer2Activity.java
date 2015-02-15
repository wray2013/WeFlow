package com.cmmobi.railwifi.activity;

import java.io.IOException;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnBufferingUpdateListener;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnSeekCompleteListener;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
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

public class VideoPlayer2Activity extends TitleRootActivity implements OnClickListener, OnBufferingUpdateListener,  OnCompletionListener,MediaPlayer.OnErrorListener, MediaPlayer.OnPreparedListener, OnSeekCompleteListener, SurfaceHolder.Callback {
	private static final String TAG = "VideoPlayer2Activity";
	// VideoView
	private SurfaceView mSurfaceView;
	private MediaPlayer mediaPlayer;
	// 视频播放拖动条
	private SeekBar mSeekBar;
	private Timer mTimer=new Timer();  
	
	// 头部View
	private View mTopView;

	// 底部View
	private View mBottomView;

	private ImageView mPlay;
	private TextView mPlayTime;
	private TextView mDurationTime;

	// 音频管理器
	private AudioManager mAudioManager;

	// 屏幕宽高
	private float width;
	private float height;

	// 视频播放时间
	private String percent;
//	private String url_str = "http://test1.mishare.cn:8080/html_game/The_Journey_Away.iphone.mp4";
//	private String url_str = "http://testvd.iluokuang.cn/video/The_Journey_Away.iphone.mp4"; 
	
	private String videoUrl;
	// 自动隐藏顶部和底部View的时间
	private static final int HIDE_TIME = 3000;
	private static final int HANDLER_FLAG_PLAY_PRGRRESS_STEP = 0x17839711;
	private static final int HADNDLER_FLAG_HIDE_DELAY = 0x17839712;

	
	private View mVolumeBrightnessLayout;
	private ImageView mOperationBg;
	private ImageView mOperationPercent;
	/** 最大声音 */
	private int mMaxVolume;
	/** 当前声音 */
	private int mVolume = -1;
	/** 当前亮度 */
	private float mBrightness = -1f;
	
	/** 当前进度 */
	private int mPosition = 0;
	private int mDuration = 0;
	
	private GestureDetector mGestureDetector;
	
	private TextView mtvTitle;
	private ImageButton mBtnBack;
	private TextView mtvLock;
	private TextView mtvUnlock;
	private Boolean mLockStatus = false;

	private Gson gson = new Gson();

	private PlayHistory playhistory;

	private SurfaceHolder surface_holder;

		
	public static String KEY_NAME = "name";
	public static String KEY_PATH = "path";
	public static String KEY_PLAYHISTORY = "playhistory";
	public static String KEY_MEDIA_ID = "key_media_id";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		hideTitlebar();

		mSurfaceView = (SurfaceView) findViewById(R.id.videoview);

		mPlayTime = (TextView) findViewById(R.id.play_time);
		mDurationTime = (TextView) findViewById(R.id.total_time);
		mPlay = (ImageView) findViewById(R.id.play_btn);
		mSeekBar = (SeekBar) findViewById(R.id.seekbar);
		mTopView = findViewById(R.id.top_layout);
		mBottomView = findViewById(R.id.bottom_layout);

		mVolumeBrightnessLayout = findViewById(R.id.operation_volume_brightness);
		mOperationBg = (ImageView) findViewById(R.id.operation_bg);
		mOperationPercent = (ImageView) findViewById(R.id.operation_percent);
		mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
		mMaxVolume = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
		

		
		width = DisplayUtil.getScreenWidth(this);
		height = DisplayUtil.getScreenHeight(this);

		mPlay.setOnClickListener(this);
		mSeekBar.setOnSeekBarChangeListener(mSeekBarChangeListener);
		mGestureDetector = new GestureDetector(this, new MyGestureListener());

//		List<String> paths = Uri.parse(videoUrl).getPathSegments();
		mtvTitle = ((TextView)findViewById(R.id.tv_name));
		mtvLock = (TextView)findViewById(R.id.tv_lock);
		mtvLock.setOnClickListener(this);
		
		mtvUnlock = (TextView) findViewById(R.id.tv_unlock);
		mtvUnlock.setOnClickListener(this);
		
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
		mSurfaceView.setOnClickListener(this);
		initView();
	}
	
	@Override
	public void onResume(){
		super.onResume();
		surface_holder = mSurfaceView.getHolder(); 
		surface_holder.addCallback(this);  
		surface_holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);/* 设置视频类型 */
		mTimer.schedule(mTimerTask, 0, 1000);  
		playUrl(videoUrl);
	}
	
	TimerTask mTimerTask = new TimerTask() {
		@Override
		public void run() {
			if(mediaPlayer==null)
				return;
			if (mediaPlayer.isPlaying() && mSeekBar.isPressed() == false) {
				handler.sendEmptyMessage(HANDLER_FLAG_PLAY_PRGRRESS_STEP);
			}
		}
	};
	
	
	public void playUrl(String videoUrl)
	{
		Log.v(TAG, "playUrl : " + videoUrl + ", mediaPlayer:" + mediaPlayer);
		if(mediaPlayer==null){
			mediaPlayer = new MediaPlayer();
			mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
			mediaPlayer.setOnBufferingUpdateListener(this);
			mediaPlayer.setOnPreparedListener(this);
			mediaPlayer.setOnSeekCompleteListener(this);
		}else{
			mediaPlayer.reset();
			mediaPlayer.release();
			mediaPlayer = new MediaPlayer();
			mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
			mediaPlayer.setOnBufferingUpdateListener(this);
			mediaPlayer.setOnPreparedListener(this);
			mediaPlayer.setOnSeekCompleteListener(this);
		}
		try {
			mediaPlayer.setDataSource(videoUrl);
//			mediaPlayer.prepare();//prepare之后自动播放
			mediaPlayer.prepareAsync();
			//mediaPlayer.start();
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
		
//		mediaPlayer.start();

		

	}
	
	public void pause()
	{
		if(mediaPlayer!=null){
			mediaPlayer.pause();
			mTimer.cancel();
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
		
		RelativeLayout upperLayout = (RelativeLayout) findViewById(R.id.top_layout_second);
		RelativeLayout.LayoutParams lParams = (RelativeLayout.LayoutParams) upperLayout.getLayoutParams();
		lParams.height = DisplayUtil.getSizeLandscape(this, 96);
		upperLayout.setLayoutParams(lParams);
		
		
		lParams = (RelativeLayout.LayoutParams) mTopView.getLayoutParams();
		lParams.height = DisplayUtil.getSizeLandscape(this, 96);
		mTopView.setLayoutParams(lParams);
		
		mBtnBack = (ImageButton)findViewById(R.id.ib_back_video);
		mBtnBack.setPadding(DisplayUtil.getSizeLandscape(this, 12), DisplayUtil.getSizeLandscape(this, 10), DisplayUtil.getSizeLandscape(this, 12), DisplayUtil.getSizeLandscape(this, 10));
		mBtnBack.setOnClickListener(this);
		
		mtvTitle.setTextSize(DisplayUtil.textGetSizeSp(this, 40));
		
		RelativeLayout bottomLayout = (RelativeLayout) findViewById(R.id.bottom_layout);
		lParams = (RelativeLayout.LayoutParams) bottomLayout.getLayoutParams();
		lParams.height = DisplayUtil.getSizeLandscape(this, 246);
		bottomLayout.setLayoutParams(lParams);
		
		lParams = (RelativeLayout.LayoutParams) mSeekBar.getLayoutParams();
		lParams.topMargin = DisplayUtil.getSizeLandscape(this, 24);
		lParams.bottomMargin = DisplayUtil.getSizeLandscape(this, 12);
		mSeekBar.setLayoutParams(lParams);
		
		mPlayTime.setTextSize(DisplayUtil.textGetSizeSp(this, 24));
		mPlayTime.setPadding(DisplayUtil.getSizeLandscape(this, 18), 0, 0, 0);
		mDurationTime.setTextSize(DisplayUtil.textGetSizeSp(this, 24));
		mDurationTime.setPadding(0, 0, DisplayUtil.getSizeLandscape(this, 18), 0);
		
		lParams = (RelativeLayout.LayoutParams)mPlay.getLayoutParams();
		lParams.height = DisplayUtil.getSizeLandscape(this, 140);
		lParams.width = lParams.height;
		mPlay.setLayoutParams(lParams);
		
		lParams = (RelativeLayout.LayoutParams)mtvLock.getLayoutParams();
		lParams.bottomMargin = DisplayUtil.getSizeLandscape(this, 21);
		mtvLock.setLayoutParams(lParams);
				
		lParams = (RelativeLayout.LayoutParams)mtvUnlock.getLayoutParams();
		lParams.leftMargin = DisplayUtil.getSizeLandscape(this, 30);
		mtvUnlock.setLayoutParams(lParams);
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

			}
		}
	};

	private class MyGestureListener extends SimpleOnGestureListener {

		@Override
		public boolean onSingleTapUp(MotionEvent e) {
			// TODO Auto-generated method stub
			showOrHide();
			return super.onSingleTapUp(e);
		}
		
		/** 滑动 */
		@Override
		public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
					
			float mOldX = e1.getRawX(), mOldY = e1.getRawY();
			int deltaX = (int) (mOldX - e2.getRawX());
			int deltaY = (int) (mOldY - e2.getRawY());
			
			if(Math.abs(deltaX) > Math.abs(deltaY)){
				onSeekSlide(-deltaX);
			}else{
				if (mOldX > width * 1/2)// 右边滑动
					onVolumeSlide(deltaY);
				else if (mOldX < width /2)// 左边滑动
					onBrightnessSlide(deltaY);
			}
			
			return super.onScroll(e1, e2, distanceX, distanceY);
		}
	}
	
	/** 手势结束 */
	private void endGesture() {
		mVolume = -1;
		mBrightness = -1f;

		// 隐藏
		handler.removeMessages(HADNDLER_FLAG_HIDE_DELAY);
		handler.sendEmptyMessageDelayed(HADNDLER_FLAG_HIDE_DELAY, 500);
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

			// 显示
			mOperationBg.setImageResource(R.drawable.video_volumn_bg_app);
			mVolumeBrightnessLayout.setVisibility(View.VISIBLE);
		}

		int index = (int) (deltaY/height * mMaxVolume) + mVolume;
		if (index > mMaxVolume)
			index = mMaxVolume;
		else if (index < 0)
			index = 0;

		// 变更声音
		mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, index, 0);

		// 变更进度条
		ViewGroup.LayoutParams lp = mOperationPercent.getLayoutParams();
		lp.width = findViewById(R.id.operation_full).getLayoutParams().width * index / mMaxVolume;
		mOperationPercent.setLayoutParams(lp);
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
			mOperationBg.setImageResource(R.drawable.video_brightness_bg_app);
			mVolumeBrightnessLayout.setVisibility(View.VISIBLE);
		}
		WindowManager.LayoutParams lpa = getWindow().getAttributes();
		lpa.screenBrightness = mBrightness + deltaY/height;
		if (lpa.screenBrightness > 1.0f)
			lpa.screenBrightness = 1.0f;
		else if (lpa.screenBrightness < 0.01f)
			lpa.screenBrightness = 0.01f;
		getWindow().setAttributes(lpa);

		ViewGroup.LayoutParams lp = mOperationPercent.getLayoutParams();
		lp.width = (int) (findViewById(R.id.operation_full).getLayoutParams().width * lpa.screenBrightness);
		mOperationPercent.setLayoutParams(lp);
	}
	
	/**
	 * 滑动改变进度 
	 * 
	 * @param percent
	 */
	private void onSeekSlide(float deltaX) {
		show();
		long current = mPosition;
		long forwardTime = (long)((deltaX / width) * mediaPlayer.getDuration());
		long currentTime = current + forwardTime;
		mediaPlayer.seekTo((int)(currentTime));
		mSeekBar.setProgress((int)(currentTime*100/mediaPlayer.getDuration()));
		mPlayTime.setText(DateUtils.getFormatTime(mediaPlayer.getCurrentPosition()));
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		handler.removeMessages(0);
		handler.removeCallbacksAndMessages(null);
	}


	public static boolean isNumeric(String str){ 
		   Pattern pattern = Pattern.compile("[0-9]*"); 
		   Matcher isNum = pattern.matcher(str);
		   if( !isNum.matches() ){
		       return false; 
		   } 
		   return true; 
		}

	

	private Runnable hideRunnable = new Runnable() {

		@Override
		public void run() {
			showOrHide();
		}
	};
	
	private Runnable hideunlockRunnable = new Runnable() {

		@Override
		public void run() {
			if(mtvUnlock.getVisibility() == mtvUnlock.VISIBLE){
				mtvUnlock.setVisibility(View.GONE);
			}
		}
	};
	

	public boolean onTouchEvent(MotionEvent event) {
		System.out.println("==== ontouch ====");
		if(mLockStatus){
			if(mtvUnlock.getVisibility() != mtvUnlock.VISIBLE){
					mtvUnlock.setVisibility(View.VISIBLE);
					handler.removeCallbacks(hideunlockRunnable);
					handler.postDelayed(hideunlockRunnable, HIDE_TIME);
			}
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
	};
	
	
	@Override
	public void onClick(View v) {
		System.out.println("==== onclick ====");
		switch (v.getId()) {
		case R.id.videoview:
			showOrHide();
			break;
		case R.id.play_btn:
			if (mediaPlayer.isPlaying()) {
				mediaPlayer.pause();
				mPlay.setImageResource(R.drawable.mediacontroller_play_button);
			} else {
				mediaPlayer.start();
				mPlay.setImageResource(R.drawable.mediacontroller_pause_button);
			}
			break;
		case R.id.ib_back_video:
			this.finish();
			break;
		case R.id.tv_lock:
			mLockStatus = true;
			break;
		case R.id.tv_unlock:
			mLockStatus = false;
			break;
		default:
			break;
		}
	}
	
	private void showOrHide() {
		if (mTopView.getVisibility() == View.VISIBLE) {
			mTopView.clearAnimation();
			Animation animation = AnimationUtils.loadAnimation(this,
					R.anim.option_leave_from_top);
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
		} else {
			show();
		}
		
	}

	
	private void show() {
		if (mTopView.getVisibility() == View.VISIBLE) {
			
		} else {
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
			handler.removeCallbacks(hideRunnable);
			handler.postDelayed(hideRunnable, HIDE_TIME);
		}
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
		case HADNDLER_FLAG_HIDE_DELAY:
			/** 定时隐藏 */
			mVolumeBrightnessLayout.setVisibility(View.GONE);
			break;
		case HANDLER_FLAG_PLAY_PRGRRESS_STEP:
			if (mediaPlayer.getCurrentPosition() > 0) {
				mPlayTime.setText(DateUtils.getFormatTime(mediaPlayer.getCurrentPosition()));
				int progress = (int)(mediaPlayer.getCurrentPosition() * 100 / mediaPlayer.getDuration());
				mSeekBar.setProgress(progress);
				if (mediaPlayer.getCurrentPosition() > mediaPlayer.getDuration() - 100) {
					mPlayTime.setText("00:00:00");
					mSeekBar.setProgress(0);
				}

			} else {
				mPlayTime.setText("00:00:00");
				mSeekBar.setProgress(0);
			}
			
			mPosition = mediaPlayer.getCurrentPosition();
			mDuration = mediaPlayer.getDuration();
			
			if (mDuration > 0) {
				long pos = mSeekBar.getMax() * mPosition / mDuration;
				mSeekBar.setProgress((int) pos);	
			}
			break;
		}
		return false;
	}


	@Override
	public int subContentViewId() {
		// TODO Auto-generated method stub
		return R.layout.video_player_activity;
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
			Timer timer = new Timer();
			timer.schedule(new TimerTask() {

				@Override
				public void run() {
					handler.sendEmptyMessage(HANDLER_FLAG_PLAY_PRGRRESS_STEP);
				}
			}, 0, 1000);
		}
		Log.e("mediaPlayer", "onPrepared");
	}


	@Override
	public void onCompletion(MediaPlayer mp) {
		// TODO Auto-generated method stub
		mPlay.setImageResource(R.drawable.mediacontroller_play_button);
		mPlayTime.setText("00:00:00");
		mSeekBar.setProgress(0);
	}


	@Override
	public void onBufferingUpdate(MediaPlayer mp, int bufferingProgress) {
		// TODO Auto-generated method stub
		mSeekBar.setSecondaryProgress(bufferingProgress);
		int currentProgress = mSeekBar.getMax()*mediaPlayer.getCurrentPosition()/mediaPlayer.getDuration();
		Log.i(TAG, "onBufferingUpdate - " + currentProgress+"% play:" + bufferingProgress + "% buffer");
	}

	@Override
	public void onSeekComplete(MediaPlayer mp) {
		// TODO Auto-generated method stub
		Log.i(TAG, "onSeekComplete - mp:" + mp);
		mp.start(); 
		//isPlaying = true;
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
				playUrl(videoUrl);
			}
		});
		return true;
	}

}
