package com.simope.yzvideo.base;


import nativeInterface.SimopeVideoView;
import nativeInterface.SmoAdMultiPlay;
import nativeInterface.YzVideoView;
import nativeInterface.SmoAdMultiPlay.ADDesc;
import nativeInterface.SmoAdMultiPlay.MultiPlayObj;
import nativeInterface.YzVideoView.YzOnCompletionListener;
import nativeInterface.YzVideoView.YzOnErrorListener;
import nativeInterface.YzVideoView.YzOnInfoListener;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnBufferingUpdateListener;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnInfoListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.media.MediaPlayer.OnSeekCompleteListener;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Display;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.simope.yzvideo.R;
import com.simope.yzvideo.control.TvMediaController;
import com.simope.yzvideo.control.listener.ControllerPlayListener;
import com.simope.yzvideo.entity.Video;
import com.simope.yzvideo.net.ConnectionDetector;
import com.simope.yzvideo.util.StringUtils;

public class TvPlayActivity extends BaseActivity implements
		OnCompletionListener, OnInfoListener, OnPreparedListener,
		OnBufferingUpdateListener, OnErrorListener,
		YzOnCompletionListener, YzOnErrorListener, YzOnInfoListener,
		ControllerPlayListener, OnSeekCompleteListener {
	
	private String mTitle;
	private View mVolumeBrightnessLayout;
	private ImageView mOperationBg;
	private ImageView playpause;
	private ImageView mOperationPercent;
	private AudioManager mAudioManager;
	/** 最大声音 */
	private int mMaxVolume;
	/** 当前声音 */
	private int mVolume = -1;
	/** 当前亮度 */
	private float mBrightness = -1f;
	/** 当前缩放模式 */
	private int mLayout = SimopeVideoView.VIDEO_LAYOUT_ZOOM;
	private int tmpc_scale = YzVideoView.VIDEO_SCALE_FILL;
	private GestureDetector mGestureDetector;
	private TvMediaController mMediaController;
	private TextView load_show;
	private View mLoadingView;
	private ProgressBar mLoadingprg;
	private boolean isBackPlay = false;
	private boolean live;
	private Video video;
	private static final int INIT = 0x81;
	private static final int BUFFRING_PLAY = 0x82;
	private static final int CHANGE_CHANNEL = 0x83;
	private static final int LOAD_PLAYADDRESS = 0x84;
	public static final int PLAY_TIME_OUT = 0x85;
//	public static final int UI_STOP = 0x86;
	public static final int NO_PLAYADDRESS = 0x87;
	public static final int NO_PLAYOBJECT = 0x88;
	public static final int BUFF_TIME = 3000;
	public static final int VIDEOVIEW_BUFF_STATE = 0x89;
	public static final int LARGE_SEEK_PLAY = 0x90;
	private static final int CHANGE_CHANNEL_TIMO_OUT = 0x91;
	/** 定时隐藏 */
	public Handler _mhandler;
//	private int ui_stopTime = 0;
//	private int history_time = 0;
	private SimopeVideoView mVideoView;
	private YzVideoView yzVideoView;

	private View aboutty_view;
	private PopupWindow abouttywindow;
	private Uri uri;

	private boolean show_change_quantity = false;
	private boolean power_off = false;
	private View tv_playview_showtime_ll;
	private TextView show_current_time, show_totaltime;

	public void onCreate(Bundle bundle) {
		super.onCreate(bundle);

		setContentView(R.layout.tv_playview);

		mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
		mMaxVolume = mAudioManager
				.getStreamMaxVolume(AudioManager.STREAM_MUSIC);

		getIntentUrl();
		initView();
		initMediaController();
		initGestureAndListener();
		startPlay();
	}

	private void getIntentUrl() {	
		Intent intent = getIntent();
		uri = intent.getData();
		if (uri != null) {
			String uriScheme = uri.getScheme();
			String uriPath = uri.getPath();
			String[] dd = uriPath.split("/");
			mTitle = dd[dd.length - 1];
			Log.e("UI", "uriScheme="+uriScheme);
			if (uriScheme.equals("file")) {
//				playString = new String[] { "本地=" + uri.toString() };	
				path=uri.toString();
				video = new Video();
				video.setPlayAddress(uri.toString());
				video.setNative_video(true);
				path=uri.toString();
			} else {
				path=uri.toString();
				video = new Video();
				video.setPlayAddress(uri.toString());
				video.setNative_video(false);			
			}
		} else {
			Bundle bundle = intent.getExtras();
			video = (Video) bundle.getSerializable("video");	
			mTitle = video.getTitle();
		}	
	}

	private void initView() {
		_mhandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case LARGE_SEEK_PLAY:
					int curr = (Integer) msg.obj;
					showBuffText(curr);
					mMediaController.mPlayer.seekTo(curr);
					mMediaController.mHandler
							.sendEmptyMessage(mMediaController.SHOW_PROGRESS);
					break;
				case VIDEOVIEW_BUFF_STATE:
					onBuffPlay();
					sendEmptyMessageDelayed(VIDEOVIEW_BUFF_STATE, 1000);
					break;
				case NO_PLAYOBJECT:
					if (!video.isNative_video()) {
						_mhandler.removeMessages(PLAY_TIME_OUT);
						if (mMediaController.mPlayer != null) {
							mMediaController.mPlayer.release(true);
						}
						showAlertDialog(TvPlayActivity.this, "  无法播放该影片",
								"播放对象不存在!!!", false);
					}
					break;
				case NO_PLAYADDRESS:
					if (mMediaController != null
					&& mMediaController.mPlayer != null) {
						mMediaController.mPlayer.release(true);
					}
					if (ConnectionDetector.getConnectionDetector(
							getApplication()).isConnectingToInternet()) {
						if (uri == null) {
							switch (video.getState()) {
							case 0:
								showAlertDialog(TvPlayActivity.this,
										"  错误提示", "连接服务器失败!", false);
								break;
							case 1:
								showAlertDialog(TvPlayActivity.this,
										"  错误提示", "请求地址超时,请确认您的网络之后重试!", false);
								break;
							case 2:
								showAlertDialog(TvPlayActivity.this,
										"  错误提示", "请确认您的网络或数据连接是否正确!", false);
								break;
							case 3:
								showAlertDialog(TvPlayActivity.this,
										"  错误提示", "无法获取播放对象!", false);
								break;
							case 4:
								showAlertDialog(TvPlayActivity.this,
										"  错误提示", "SMO地址格式解析错误!", false);
								break;
							default:
								showAlertDialog(TvPlayActivity.this,
										"  错误提示",
										"获取播放地址失败:" + video.getState(), false);
								break;
							}
						} else {
							showAlertDialog(TvPlayActivity.this, "  错误提示",
									"本地视频加载出错!", false);
						}
					} else {
						showAlertDialog(TvPlayActivity.this, "  网络连接异常",
								"请检查您的网络连接是否正常!", false);
					}
					_mhandler.removeMessages(VIDEOVIEW_BUFF_STATE);
					break;
				case LOAD_PLAYADDRESS:
					startAddressPlay();
					break;

				case INIT:
					mVolumeBrightnessLayout.setVisibility(View.GONE);
					tv_playview_showtime_ll.setVisibility(View.GONE);
					break;

				case BUFFRING_PLAY:
					if (needResume) {
						startPlayer();
					}
					needResume = false;
					// play_buff_ll.setVisibility(View.GONE);
					mLoadingView.setVisibility(View.GONE);
					if (abouttywindow.isShowing()) {
						abouttywindow.dismiss();
					}
					break;
				case CHANGE_CHANNEL:				
					if (uri == null && mLoadingView.isShown()) {
						if (mLoadingprg.isShown()) {
							mLoadingprg.setVisibility(View.GONE);
							load_show.setText("网络不给力,可以尝试切换低码率观看!");
						} else {
							mLoadingprg.setVisibility(View.VISIBLE);
							load_show.setText("正在缓冲视频...");
						}
						_mhandler
								.sendEmptyMessageDelayed(CHANGE_CHANNEL, 10000);
					}
					break;
				case CHANGE_CHANNEL_TIMO_OUT:
					if (mMediaController.mPlayer != null
							&& mMediaController.mPlayer.isPlaying()) {
						if (ConnectionDetector.getConnectionDetector(
								getApplication()).isConnectingToInternet()) {

							showAlertDialog(TvPlayActivity.this, "  对不起",
									"网络环境不好,加载超时!", false);
							mMediaController.mPlayer.release(true);
						} else {
							showAlertDialog(TvPlayActivity.this, "  网络连接异常",
									"请检查您的网络连接是否正常!", false);
						}
					}
					break;
				case PLAY_TIME_OUT:
					if (video.isNative_video()) {
						_mhandler.removeMessages(PLAY_TIME_OUT);
					} else {
						synchronized (TvPlayActivity.this) {
							if (mMediaController.mPlayer != null) {
								mMediaController.mPlayer.release(true);
							}
							showAlertDialog(TvPlayActivity.this, "  错误提示",
									"加载路径超时,请重试!", false);
						}
					}
					break;
				}

			}
		};
		mVolumeBrightnessLayout = findViewById(R.id.tv_playview_operation_volume_brightness);
		playpause = (ImageView) findViewById(R.id.tv_playview_operation_play_pause);
		mOperationBg = (ImageView) findViewById(R.id.tv_playview_operation_bg);
		mOperationPercent = (ImageView) findViewById(R.id.tv_playview_operation_percent);
		mLoadingView = findViewById(R.id.tv_playview_video_loading);
		mLoadingprg = (ProgressBar) findViewById(R.id.tv_playview_load_prg);
		load_show = (TextView) findViewById(R.id.tv_playview_load_show);

		tv_playview_showtime_ll = findViewById(R.id.tv_playview_showtime_ll);
		show_current_time = (TextView) findViewById(R.id.tv_playview_show_current_time);
		show_totaltime = (TextView) findViewById(R.id.tv_playview_show_totaltime);

		aboutty_view = this.getLayoutInflater().inflate(
				R.layout.about_player_dilg_pop, null);
		abouttywindow = new PopupWindow(aboutty_view,
				android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
				android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
		abouttywindow.setFocusable(false);
		abouttywindow.setBackgroundDrawable(null);
		abouttywindow.setOutsideTouchable(true);
	}

	private void initMediaController() {
		mMediaController = new TvMediaController(this);
		mMediaController.setPlayName(mTitle);
		mMediaController.setLive(live);
		mMediaController.setControllerPlayListener(this);
		initVideoView();
		initYzVideoView();
	}

	private void initGestureAndListener() {
		mGestureDetector = new GestureDetector(this, new MyGestureListener());
	}

	private void startPlay() {			
		IntentFilter filter = new IntentFilter();
		filter.addAction(Intent.ACTION_SCREEN_OFF);
		filter.addAction(Intent.ACTION_SCREEN_ON);
		filter.addAction(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
		filter.setPriority(999);
		registerReceiver(mBatInfoReceiver, filter);
		if (uri != null) {
			Log.e("UI", "播放有路径视频文件"+path);
			startAddressPlay();
		} else {
			//多码率地址，如果有,就不需解析.smo获取地址了
			if (video != null&&!video.getPlayAddress().endsWith(".smo")) {
				startAddressPlay();	
			} else {
				smoAdMultiPlay=new SmoAdMultiPlay(video.getPlayAddress(),this);
				smoAdMultiPlay.setParseSmoCompleteListener(this);
			}
		}
	}

	private SmoAdMultiPlay smoAdMultiPlay;
	//s== 1,成功
	//s==-2,获取到smo，但是解析不了数据
	//s==-1,无法连接服务器
	//s==0,服务器返回错误，404
	public void parSmoComplete(int result) {
		if(result==SmoAdMultiPlay.PARSE_SMO_SUCCESS){
			MultiPlayObj multiPlayObj=smoAdMultiPlay.multiPlayObj;
			ADDesc[] addescs=smoAdMultiPlay.ad;
			if(multiPlayObj!=null){
				video.setMultiAddress(SubNullArrItems(multiPlayObj.levelurl));
				mMediaController.setBtn_arr(SubNullArrItems(multiPlayObj.levelname));
				mMediaController.setPlayAddress_arr(SubNullArrItems(multiPlayObj.levelurl));
				mMediaController.initBtn();
			}			
			startAddressPlay();	
		}else{
			if (mMediaController != null
					&& mMediaController.mPlayer != null) {
				mMediaController.mPlayer.release(true);
			}
			if (ConnectionDetector.getConnectionDetector(
					getApplication()).isConnectingToInternet()) {
				if (uri == null) {
					switch (result) {
						case SmoAdMultiPlay.PARSE_SMO_ERROR:
							showAlertDialog(TvPlayActivity.this, "  错误提示",
									"无法解析SMO数据!", false);
							break;
						case SmoAdMultiPlay.PARSE_SMO_SERVER_ERROR:
							showAlertDialog(TvPlayActivity.this, "  错误提示",
									"无法连接服务器!", false);
							break;
						case SmoAdMultiPlay.PARSE_SMO_GET_ERROR:
							showAlertDialog(TvPlayActivity.this, "  错误提示",
									"服务器返回错误!", false);
							break;
					}
				}
			} else {
				showAlertDialog(TvPlayActivity.this, "  网络连接异常",
						"请检查您的网络连接是否正常!", false);
			}
			_mhandler.removeMessages(VIDEOVIEW_BUFF_STATE);
		}
	}
	
	
	

	protected void initVideoView() {
		if (mVideoView == null) {
			mVideoView = (SimopeVideoView) findViewById(R.id.tv_playview_surface_view);
			mVideoView.setOnCompletionListener(TvPlayActivity.this);
//			mVideoView.setOnInfoListener(this);
			mVideoView.setOnPreparedListener(this);
			mVideoView.setOnBufferingUpdateListener(this);
			mVideoView.setOnErrorListener(this);
			mVideoView.setControllerPlayListener(this);
			mVideoView.setOnSeekCompleteListener(this);
			mVideoView.setMediaController(mMediaController);
		} else {
			mVideoView.sfDestroy = false;
			mVideoView.setVisibility(View.VISIBLE);
		}

	}

	private void initYzVideoView() {
		if (yzVideoView == null) {
			yzVideoView = (YzVideoView) findViewById(R.id.tv_playview_yzVideoView);
			yzVideoView.setmYzOnCompletionListener(this);
			yzVideoView.setmYzOnErrorListener(this);
			yzVideoView.setmYzOnInfoListener(this);
			yzVideoView.setControllerPlayListener(this);
			yzVideoView.setMediaController(mMediaController);
		} else {
			yzVideoView.sfDestroy = false;
			yzVideoView.setVisibility(View.VISIBLE);
		}
	}

	


	
	private String path;

	private void startAddressPlay() {
		if (mMediaController != null) {
			parseError = 2;
			if (path == null) {
				if (!is_wifi) {
					path = video.getMultiAddress()[video.getMultiAddress().length - 1];
				} else {
					path = video.getMultiAddress()[0];
				}
			}
			if(mMediaController.currentPath==null){
				mMediaController.currentPath = path;
			}else{
				path=mMediaController.currentPath;
			}		
			if (path.contains("index.hsm")||live) {
				yzVideoView.setVisibility(View.VISIBLE);
				mVideoView.setVisibility(View.INVISIBLE);
				yzVideoView.setMediaController(mMediaController);
				mMediaController.current_video = false;
				yzVideoView.setFocusable(true);
				if (video.getPlayedTime() < 1000||video.getPlayedTime()>video.getTotalTime()) {
						yzVideoView.start(path, 0, BUFF_TIME);
				} else {
					mMediaController.mCurrent = video.getPlayedTime();
						yzVideoView.start(path,
								(int) mMediaController.mCurrent, BUFF_TIME);
				}
			} else {
				mVideoView.setVisibility(View.VISIBLE);
				yzVideoView.setVisibility(View.INVISIBLE);
				mVideoView.setMediaController(mMediaController);
				mMediaController.current_video = true;
				mVideoView.setFocusable(true);
				if (uri != null) {
					mVideoView.setVideoURI(uri);
				} else {
					if (video.getPlayedTime() < 1000||video.getPlayedTime()>video.getTotalTime()) {
						mVideoView.setVideoPath(path);
					} else {
						mMediaController.mCurrent =video.getPlayedTime();
						mVideoView.setQuality(path,
								(int) mMediaController.mCurrent);
					}
				}
			}
		}
	}

	private void togMediaControlsVisiblity() {
		if (mMediaController.isShowing()) {
			Log.v("UI", "mMediaController.isShowing(),hide()");
			mMediaController.hide();
		} else {
			// if (play_buff_ll.isShown()) {
			// play_buff_ll.setVisibility(View.GONE);
			// }
			mMediaController.show();
			Log.v("UI", "mMediaController.ishide(),show()");
		}
	}

	protected void onResume() {
		super.onResume();
		if (isBackPlay) {
			if (mVideoView != null && mMediaController.current_video) {
				if (mMediaController != null
						&& mMediaController.mProgress != null && !live) {
					int duration = mVideoView.getDuration();
					if (duration > 1000) {
						long pos = 1000L * mMediaController.mCurrent  / duration;
						mMediaController.mProgress.setProgress((int) pos);
					}
				}
				if (mVideoView.sfDestroy) {
					mMediaController.mHandler
							.removeMessages(mMediaController.FADE_OUT);
					mMediaController.mHandler
							.removeMessages(mMediaController.SHOW_PROGRESS);
					// play_buff_ll.setVisibility(View.VISIBLE);
					load_show.setText("正在恢复播放...");
					mLoadingprg.setVisibility(View.VISIBLE);
					mLoadingView.setVisibility(View.VISIBLE);
					playpause.setVisibility(View.INVISIBLE);
					Log.e("mark", "" + 4);
					mVideoView.sfDestroy = !mVideoView.sfDestroy;
				}
			} else {
				if (yzVideoView != null && !mMediaController.current_video) {
					if (mMediaController != null
							&& mMediaController.mProgress != null && !live) {
						int duration = mVideoView.getDuration();
						if (duration > 1000) {
							long pos = 1000L * mMediaController.mCurrent / duration;
							mMediaController.mProgress.setProgress((int) pos);
						}
					}
					if (yzVideoView.sfDestroy) {
						yzVideoView.setVisibility(View.INVISIBLE);
						yzVideoView.release(true);
						yzVideoView.setVisibility(View.VISIBLE);
						mMediaController.mHandler
								.removeMessages(mMediaController.FADE_OUT);
						mMediaController.mHandler
								.removeMessages(mMediaController.SHOW_PROGRESS);
						// play_buff_ll.setVisibility(View.VISIBLE);
						load_show.setText("正在恢复播放...");
						mLoadingprg.setVisibility(View.VISIBLE);
						mLoadingView.setVisibility(View.VISIBLE);
						playpause.setVisibility(View.INVISIBLE);
						if (live) {
							yzVideoView.start(mMediaController.currentPath, 0,
									BUFF_TIME);
						} else {
							yzVideoView.start(mMediaController.currentPath,
									(int) mMediaController.mCurrent, BUFF_TIME);
						}
						isBackPlay = !isBackPlay;
						yzVideoView.sfDestroy = !yzVideoView.sfDestroy;
					}
				}
			}
		}
		power_off = false;
	}

	protected void onPause() {
		super.onPause();
		if (mMediaController.mPlayer != null) {
			mMediaController.mCurrent = mMediaController.mPlayer
					.getCurrentPosition();
			parseError = 2;
		}
//		canclePlayView();
	}

	@Override
	protected void onStop() {
		super.onStop();
		Log.e("UI", "Activity----------------->>>>>>onStop() ");
		_mhandler.removeMessages(VIDEOVIEW_BUFF_STATE);
		_mhandler.removeMessages(CHANGE_CHANNEL);
		_mhandler.removeMessages(CHANGE_CHANNEL_TIMO_OUT);
		if (mMediaController.mPlayer != null) {
			mMediaController.mPlayer.pause();
		}

		if (needResume) {
			if (abouttywindow.isShowing()) {
				abouttywindow.dismiss();
			}
			// play_buff_ll.setVisibility(View.GONE);
			mLoadingView.setVisibility(View.GONE);
			_mhandler.removeMessages(CHANGE_CHANNEL);
		}
		isBackPlay = true;
		if (mMediaController != null) {
			mMediaController.hide();
		}
	}

	protected void onDestroy() {
		super.onDestroy();
		Log.e("UI", "onDestroy");
		synchronized (TvPlayActivity.this) {
			_mhandler.removeMessages(VIDEOVIEW_BUFF_STATE);
			_mhandler.removeMessages(LOAD_PLAYADDRESS);
			_mhandler.removeMessages(NO_PLAYOBJECT);
			_mhandler.removeMessages(NO_PLAYADDRESS);
			_mhandler.removeMessages(PLAY_TIME_OUT);
			_mhandler.removeMessages(CHANGE_CHANNEL_TIMO_OUT);
			if (mMediaController != null) {
				mMediaController.mHandler
						.removeMessages(mMediaController.SHOW_PROGRESS);
				_mhandler.removeMessages(0);
				_mhandler.removeMessages(CHANGE_CHANNEL);

				if (mVideoView != null) {
					mVideoView.setVisibility(View.VISIBLE);
					mVideoView.release(true);
					mVideoView = null;
				} else {
					if (yzVideoView != null) {
						yzVideoView.setVisibility(View.VISIBLE);
						yzVideoView.release(true);
						yzVideoView = null;
					}
				}
				mMediaController = null;
			}
			if(smoAdMultiPlay!=null){
				smoAdMultiPlay.releaseSMOClientFromFjni();
			}
			if (mBatInfoReceiver != null) {
				try {
					unregisterReceiver(mBatInfoReceiver);

				} catch (Exception e) {
					Log.e("UI", "unregisterReceiver mBatInfoReceiver failure :"
							+ e.getCause());
				}
			}
		}
	}

	public boolean onTouchEvent(MotionEvent event) {
		Log.e("UPLOAD", "onTouchEvent");
		if (mGestureDetector != null) {
			if (mGestureDetector.onTouchEvent(event))
				return true;
		}
		// 处理手势结束
		switch (event.getAction() & MotionEvent.ACTION_MASK) {
		case MotionEvent.ACTION_UP:
			endGesture();
			break;
		}
		return super.onTouchEvent(event);
	}

	/** 手势结束 */
	private void endGesture() {
		mVolume = -1;
		mBrightness = -1f;
		if (touchseek && !live) {
			if (mVideoView != null && mVideoView.isShown()
					&& !mMediaController.is_complete) {
				mVideoView.seekTo((int) index);
			} else {
				if (yzVideoView != null && yzVideoView.isShown()
						&& !mMediaController.is_complete) {
					yzVideoView.seekTo((int) index);
				}
			}
			showBuffText(index);
			time = 0;
			index = 0;
			touchseek = false;
		}
		// 隐藏
		_mhandler.removeMessages(INIT);
		_mhandler.sendEmptyMessageDelayed(INIT, 500);

	}

	private class MyGestureListener extends SimpleOnGestureListener {
		public boolean onSingleTapConfirmed(MotionEvent e) {
			if (mMediaController != null) {
				togMediaControlsVisiblity();
			}
			return true;
		}

		/** 双击 */
		@Override
		public boolean onDoubleTap(MotionEvent e) {
			ViewScalse();
			return true;
		}

		/** 滑动 */
		@Override
		public boolean onScroll(MotionEvent e1, MotionEvent e2,
				float distanceX, float distanceY) {

			float mOldX = e1.getX(), mOldY = e1.getY();
			int y = (int) e2.getRawY();
			int x = (int) e2.getRawX();
			Display disp = getWindowManager().getDefaultDisplay();
			int windowWidth = disp.getWidth();
			int windowHeight = disp.getHeight();
			_mhandler.removeMessages(INIT);
			if (time == 0 && !live) {
				if (mVideoView != null && mVideoView.isShown()) {
					time = mVideoView.getCurrentPosition();
					seekTotal = mVideoView.getDuration();
				} else {
					if (yzVideoView != null && yzVideoView.isShown()) {
						time = yzVideoView.getCurrentPosition();
						seekTotal = yzVideoView.getDuration();
					}
				}
				if (show_current_time != null) {
					show_current_time.setText(StringUtils.generateTime(time));
				}
			}
			Log.v("UI", " onScroll,e1.getX()=" + mOldX + ",e1.getY()=" + mOldY
					+ ",e2.getRawY()=" + e2.getRawY());
			if (mOldX > windowWidth * 3.0 / 4) {// 右边滑动
				Log.v("UI", " 右边滑动");
				onBrightnessSlide((mOldY - y) / windowHeight);
			} else if (mOldX < windowWidth / 4.0) {// 左边滑动
				Log.v("UI", " 右边滑动");
				onVolumeSlide((mOldY - y) / windowHeight);
			} else if (-50 < mOldY - y && mOldY - y < 50
					&& mOldX < windowWidth * 4.0 / 5
					&& mOldX > windowWidth / 5.0) {
				Log.v("UI", " onSeekSlise");
				touchseek = true;
				onSeekSlise((x - mOldX) / windowWidth);
			}
			return super.onScroll(e1, e2, distanceX, distanceY);
		}
	}

	private void ViewScalse() {
		if (mVideoView != null && mVideoView.isShown()
				&& !yzVideoView.isShown()) {
			if (mLayout == SimopeVideoView.VIDEO_LAYOUT_ZOOM)
				mLayout = SimopeVideoView.VIDEO_LAYOUT_ORIGIN;
			else
				mLayout++;
			if (mVideoView != null)
				mVideoView.setVideoLayout(mLayout, 0);
		} else {
			if (yzVideoView != null && yzVideoView.isShown()
					&& !mVideoView.isShown()) {
				if (tmpc_scale == YzVideoView.VIDEO_SCALE_FILL) {
					tmpc_scale = YzVideoView.VIDEO_LAYOUT_ORIGIN;
				} else {
					tmpc_scale++;
				}
				if (yzVideoView != null) {
					yzVideoView.setVideoScale(tmpc_scale);
				}
			}
		}

	}

	private int time = 0;
	private long seekTotal = 0;
	private long index;
	private boolean touchseek = false;

	private void onSeekSlise(float percent) {

		mVolumeBrightnessLayout.setVisibility(View.GONE);
		tv_playview_showtime_ll.setVisibility(View.VISIBLE);
		index = (long) ((percent * 120000) + time);
		if (index > seekTotal)
			index = seekTotal;
		else if (index < 1000)
			index = 0;
		if (show_current_time != null) {
			show_current_time.setText(StringUtils.generateTime(index));
		}

	}

	/**
	 * 滑动改变声音大小
	 * 
	 * @param percent
	 */
	private void onVolumeSlide(float percent) {
		Log.v("UI", " percent=" + percent);
		if (mVolume == -1) {
			mVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
			if (mVolume < 0)
				mVolume = 0;

			// 显示
			mOperationBg.setImageResource(R.drawable.video_volumn_bg);
			// mVolumeBrightnessLayout.bringToFront();
			mVolumeBrightnessLayout.setVisibility(View.VISIBLE);
			tv_playview_showtime_ll.setVisibility(View.GONE);

		}

		int index = (int) (percent * mMaxVolume) + mVolume;
		if (index > mMaxVolume)
			index = mMaxVolume;
		else if (index < 0)
			index = 0;

		// 变更声音
		mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, index, 0);

		// 变更进度条
		ViewGroup.LayoutParams lp = mOperationPercent.getLayoutParams();
		lp.width = findViewById(R.id.tv_playview_operation_full)
				.getLayoutParams().width * index / mMaxVolume;
		mOperationPercent.setLayoutParams(lp);
	}

	/**
	 * 滑动改变亮度
	 * 
	 * @param percent
	 */
	private void onBrightnessSlide(float percent) {
		if (mBrightness < 0) {
			mBrightness = getWindow().getAttributes().screenBrightness;
			if (mBrightness <= 0.00f)
				mBrightness = 0.50f;
			if (mBrightness < 0.01f)
				mBrightness = 0.01f;

			// 显示
			mOperationBg.setImageResource(R.drawable.video_brightness_bg);
			// mVolumeBrightnessLayout.bringToFront();
			mVolumeBrightnessLayout.setVisibility(View.VISIBLE);
			tv_playview_showtime_ll.setVisibility(View.GONE);
		}
		WindowManager.LayoutParams lpa = getWindow().getAttributes();
		lpa.screenBrightness = mBrightness + percent;
		if (lpa.screenBrightness > 1.0f)
			lpa.screenBrightness = 1.0f;
		else if (lpa.screenBrightness < 0.01f)
			lpa.screenBrightness = 0.01f;
		getWindow().setAttributes(lpa);

		ViewGroup.LayoutParams lp = mOperationPercent.getLayoutParams();
		lp.width = (int) (findViewById(R.id.tv_playview_operation_full)
				.getLayoutParams().width * lpa.screenBrightness);
		mOperationPercent.setLayoutParams(lp);
	}

	@Override
	public void onCompletion(MediaPlayer player) {
		Log.e("UI", "MediaPlayer>>>>>>>>>>>>>>>onCompletion");
		mMediaController.is_complete = true;
		mLoadingView.setVisibility(View.GONE);
		_mhandler.removeMessages(CHANGE_CHANNEL_TIMO_OUT);
		_mhandler.removeMessages(VIDEOVIEW_BUFF_STATE);
		mMediaController.closePlay();

	}

	private void startPlayer() {
		if (mVideoView != null)
			mVideoView.start();
	}

	/** 是否需要自动恢复播放，用于自动暂停，恢复播放 */
	private boolean needResume;
	private long bufferTime;

	public boolean onInfo(MediaPlayer arg0, int arg1, int arg2) {
		switch (arg1) {
		// =======================================================
		case MediaPlayer.MEDIA_INFO_BUFFERING_START:
			// 开始缓存，暂停播放
			Log.e("UI", "执行onInfo缓冲");
			_mhandler.removeMessages(BUFFRING_PLAY);
			_mhandler.removeMessages(CHANGE_CHANNEL);
			_mhandler.sendEmptyMessageDelayed(CHANGE_CHANNEL, 15000);
			needResume = true;
			// mLoadingView.bringToFront();
			// mLoadingView.setVisibility(View.VISIBLE);
			bufferTime = mVideoView.getCurrentPosition();
			mMediaController.setEnabled(false);
			if (!show_change_quantity) {
				load_show.setText("正在缓冲视频...");
				playpause.setVisibility(View.INVISIBLE);
				Log.e("mark", "" + 12);
				mLoadingView.setVisibility(View.VISIBLE);
			}
			break;
		case MediaPlayer.MEDIA_INFO_BUFFERING_END:
			// 缓存完成，继续播放
			Log.e("UI", "执行onInfo播放");
			mMediaController.setEnabled(true);
			_mhandler.removeMessages(CHANGE_CHANNEL);
			_mhandler.sendEmptyMessage(BUFFRING_PLAY);
			break;
		case MediaPlayer.MEDIA_INFO_BAD_INTERLEAVING:
			Log.e("UI", "MEDIA_INFO_BAD_INTERLEAVING");
			break;
		}
		return false;
	}

//	private int isPlayed = 0;
	boolean isprepard = false;
	boolean is_start = false;
	private long totalTime;


	private synchronized void onBuffPlay() {
		long currentTime = mVideoView.getCurrentPosition();
		if (mVideoView.isPlaying()) {
			if (isprepard) {
				mMediaController.show();
				isprepard = false;
				mLoadingView.setVisibility(View.GONE);
			}else{
				if (currentTime != bufferTime 
					&& mLoadingprg.isShown()
					&&needResume
						) {// 对应有缓冲好的
					mMediaController.setEnabled(true);
					_mhandler.removeMessages(CHANGE_CHANNEL);
					_mhandler.removeMessages(CHANGE_CHANNEL_TIMO_OUT);					
					if (abouttywindow.isShowing()) {
						abouttywindow.dismiss();
					}
					Log.e("mark", "onBuffPlay关闭缓冲");
					mLoadingView.setVisibility(View.GONE);
					playpause.setVisibility(View.GONE);
					needResume = false;
					mMediaController.mDragging = false;
				}  else if (currentTime == bufferTime 
						&& !mLoadingView.isShown()
						) {
					Log.e("mark", "onBuffPlay缓冲");
					_mhandler.removeMessages(CHANGE_CHANNEL);
					_mhandler.removeMessages(CHANGE_CHANNEL_TIMO_OUT);
					_mhandler.sendEmptyMessageDelayed(CHANGE_CHANNEL, 17000);
					_mhandler.sendEmptyMessageDelayed(CHANGE_CHANNEL_TIMO_OUT,
							100000);
					needResume = true;
					mMediaController.setEnabled(false);
					if (!show_change_quantity) {
						load_show.setText("正在缓冲视频...");
						mLoadingprg.setVisibility(View.VISIBLE);
						mLoadingView.setVisibility(View.VISIBLE);
					}
				} 
			}		
			bufferTime = currentTime;
			if (power_off) {
				mVideoView.pause();
			}
		} else {
			if (mLoadingView.isShown()) {
				startPlayer();
			}
		}
	}

	public void onBufferingUpdate(MediaPlayer mp, int percent) {
	
	}

	public boolean onError(MediaPlayer mp, int what, int extra) {
		synchronized (TvPlayActivity.this) {
			_mhandler.removeMessages(PLAY_TIME_OUT);
			
			if (mVideoView != null) {
				mVideoView.pause();
				mVideoView.stopPlayback();
			}
			if (power_off) {
				is_start = false;
			}
			showError();
			_mhandler.removeMessages(CHANGE_CHANNEL_TIMO_OUT);
			_mhandler.removeMessages(VIDEOVIEW_BUFF_STATE);
		}	
		return false;
	}

	public void changerChannel(String which) {
		abouttywindow.dismiss();
		_mhandler.removeMessages(VIDEOVIEW_BUFF_STATE);
		if (live) {
			mMediaController.mCurrent = 0;
			load_show.setText("直播!正在努力加载...");
		} else {
			show_change_quantity = true;
			if (mMediaController.is_complete) {
				load_show.setText("正在从头加载视频...");
				mMediaController.is_complete = false;
			} else {
				if (show_change_quantity) {
					load_show.setText("正在切换视频清晰度...");
				} else {
					load_show.setText("正在缓冲视频...");
				}
			}

		}
		playpause.setVisibility(View.INVISIBLE);
		mLoadingprg.setVisibility(View.VISIBLE);
		mLoadingView.setVisibility(View.VISIBLE);
		if (which.contains("index.hsm")||live) {
			if (mVideoView != null && mVideoView.isShown()) {
				mVideoView.release(true);
				mVideoView.setVisibility(View.GONE);
				initYzVideoView();
			} else {
				yzVideoView.release(true);
				yzVideoView.setVisibility(View.GONE);
				initYzVideoView();
			}
			yzVideoView.setQuality(which, (int) mMediaController.mCurrent);
			mMediaController.current_video = false;
		} else {
			if (yzVideoView != null && yzVideoView.isShown()) {
				yzVideoView.release(true);
				yzVideoView.setVisibility(View.GONE);
				initVideoView();
			} else {
				mVideoView.release(true);
				mVideoView.setVisibility(View.GONE);
				initVideoView();
			}
			mMediaController.current_video = true;
			mVideoView.setQuality(which, (int) mMediaController.mCurrent);
		}
		_mhandler.removeMessages(CHANGE_CHANNEL_TIMO_OUT);
	}

	private boolean isError = true;
	private int parseError = 0;

	private void showError() {
		Log.e("WIFI_S", parseError + "=parseError");
		if (uri != null) {
			showAlertDialog(TvPlayActivity.this, "  错误提示", "无法播放该视频!", false);
		} else {
			if (isError) {
				if (ConnectionDetector.getConnectionDetector(getApplication())
						.isConnectingToInternet()) {

					if (parseError == 2) {
						showAlertDialog(TvPlayActivity.this, "  网络异常",
								"请确认您的网络或数据连接是否正确!", false);
					} else {
						new AlertDialog.Builder(TvPlayActivity.this)
								.setTitle(R.string.VideoView_error_title)
								.setMessage(
										R.string.VideoView_error_text_unknown)
								.setPositiveButton(
										R.string.VideoView_error_button,
										new DialogInterface.OnClickListener() {
											public void onClick(
													DialogInterface dialog,
													int whichButton) {
												mMediaController.closePlay();
											}
										}).setCancelable(false).show();

					}
				} else {
					showAlertDialog(TvPlayActivity.this, "  网络连接异常",
							"请检查您的网络连接是否正常!", false);
				}
				isError = !isError;
			}
		}

	}

	public void showAlertDialog(Context context, String title, String message,
			Boolean status) {
		if (!isFinishing()) {
//			play_buff_ll.setVisibility(View.GONE);
			mLoadingView.setVisibility(View.GONE);
			new AlertDialog.Builder(TvPlayActivity.this)
					.setTitle(title)
					.setMessage(message)
					.setPositiveButton("OK",
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									_mhandler.removeMessages(NO_PLAYOBJECT);
									_mhandler.removeMessages(NO_PLAYADDRESS);
									_mhandler.removeMessages(PLAY_TIME_OUT);
									_mhandler.removeMessages(CHANGE_CHANNEL_TIMO_OUT);
									mMediaController.is_complete = false;
									mMediaController.closePlay();
								}
							}).setCancelable(false).show();
		}
	}

	
	
	
	
	
	
	@Override
	public boolean onInfo(YzVideoView yzVideoView, int what) {
		if (!isFinishing()) {
			switch (what) {
			case YzVideoView.TMPC_OUT_OF_MEMORY:
				break;
			case YzVideoView.TMPC_NO_SOURCE_DEMUX:
				_mhandler.sendEmptyMessage(NO_PLAYOBJECT);
				break;
			case YzVideoView.TMPC_NO_PLAY_OBJECT:
				_mhandler.sendEmptyMessage(NO_PLAYOBJECT);
				break;
			case YzVideoView.TMPC_TEMOBI_TIME_OUT:
				if (!video.isNative_video()) {
					showAlertDialog(TvPlayActivity.this, "  错误提示",
							"播放加载超时,请重试!", false);
				}
				break;
			case YzVideoView.TMPC_NOTIFY_MEDIA_INFO:
				tmpcIsprepare();
				break;
			case YzVideoView.TMPC_START_BUFFER_DATA:
				tmpcBuffer();
				break;
			case YzVideoView.TMPC_START_PLAY:
				tmpcPlay();
				break;

			}
		}

		return false;
	}

	private void tmpcBuffer() {
//		_mhandler.removeMessages(BUFFRING_PLAY);
		_mhandler.removeMessages(CHANGE_CHANNEL);
		_mhandler.removeMessages(CHANGE_CHANNEL_TIMO_OUT);
		needResume = true;
		// mLoadingView.bringToFront();
		playpause.setVisibility(View.INVISIBLE);
		Log.e("mark", "" + 9);
		mLoadingprg.setVisibility(View.VISIBLE);
		mLoadingView.setVisibility(View.VISIBLE);
		if (!show_change_quantity) {
			load_show.setText("正在缓冲视频...");
		}
		if (no_net) {
			showAlertDialog(TvPlayActivity.this, "  网络连接异常",
					"请检查您的网络连接是否正常！", false);
		} else {
			_mhandler.sendEmptyMessageDelayed(CHANGE_CHANNEL, 17000);
			_mhandler.sendEmptyMessageDelayed(CHANGE_CHANNEL_TIMO_OUT, 100000);
		}

	}

	private void tmpcPlay() {
		totalTime = yzVideoView.getDuration();
		if (show_change_quantity) {
			show_change_quantity = false;
		}
		if (yzVideoView.getDuration() == 0) {
			live = true;
			mMediaController.setLive(live);
		}
		if (power_off) {
			yzVideoView.pause();
		}
		parseError = 0;
		// play_buff_ll.setVisibility(View.GONE);
		if (abouttywindow.isShowing()) {
			abouttywindow.dismiss();
		}
		mLoadingView.setVisibility(View.GONE);

		// 对应onBuffer之后
		_mhandler.removeMessages(CHANGE_CHANNEL);
		_mhandler.removeMessages(CHANGE_CHANNEL_TIMO_OUT);
		needResume = false;
		if (TMPC_FIRST) {
			mMediaController.show();
			TMPC_FIRST = false;
		}
		if (playpause.isShown()) {
			playpause.setVisibility(View.INVISIBLE);

			Log.e("mark", "" + 10);
			mMediaController.hide();
		}
		mMediaController.mDragging = false;
		// mMediaController.mHandler.sendEmptyMessageDelayed(mMediaController.CLICK,1500);
	}

	boolean TMPC_FIRST = false;
	private boolean isResumePlayed = true;

	public void onPrepared(MediaPlayer arg0) {
		synchronized (TvPlayActivity.this) {
			_mhandler.removeMessages(PLAY_TIME_OUT);		
			is_start = true;
			mMediaController.is_complete = false;
			if (mVideoView.getDuration() == 0) {
				live = true;
				mMediaController.setLive(live);
			}
			if (video.getPlayedTime() > 0 && isResumePlayed && !isBackPlay) {
				if (mMediaController != null) {
					long currentTime = video.getPlayedTime();
					long duration = video.getTotalTime();
					if (duration != 0) {
						long pos = 1000L * currentTime / duration;
						if (mMediaController.mProgress != null) {
							mMediaController.mProgress.setProgress((int) pos);
						}
					}
					if (mMediaController.mEndTime != null) {
						mMediaController.mEndTime.setText(StringUtils
								.generateTime(duration));
					}
					if (mMediaController.mCurrentTime != null) {
						mMediaController.mCurrentTime.setText(StringUtils
								.generateTime(currentTime));
					}
					if (duration - currentTime < 1000) {
						Toast.makeText(getApplication(), "已观看完毕,将重新播放!",
								Toast.LENGTH_SHORT).show();
					} else {
						Toast.makeText(getApplication(), "恢复历史播放!",
								Toast.LENGTH_SHORT).show();
					}
				}
				isResumePlayed = false;
			}
			if (isBackPlay) {
				if (!live) {
					mVideoView.seekTo((int) mMediaController.mCurrent);
				}
				isBackPlay = !isBackPlay;
			}

			if (show_change_quantity) {
				show_change_quantity = false;
			}

			if (power_off) {
				mVideoView.pause();
			}
			parseError = 0;
			if (video.isNative_video()
					|| mMediaController.currentPath.contains(".m3u8")) {
				mLoadingView.setVisibility(View.GONE);
				// play_buff_ll.setVisibility(View.GONE);
				mMediaController.show();
			}
			isprepard = true;
			_mhandler.sendEmptyMessageDelayed(VIDEOVIEW_BUFF_STATE, 1200);
		}
	}

	private void tmpcIsprepare() {
		synchronized (TvPlayActivity.this) {
			_mhandler.removeMessages(PLAY_TIME_OUT);
			is_start = true;
			mMediaController.is_complete = false;
			TMPC_FIRST = true;
			if (video.getPlayedTime() > 0 && isResumePlayed) {
				if (mMediaController != null
						&& mMediaController.mProgress != null) {
					long currentTime = video.getPlayedTime();
					long duration = video.getTotalTime();
					if (duration != 0) {
						long pos = 1000L * currentTime / duration;
						mMediaController.mProgress.setProgress((int) pos);
					}

					if (mMediaController.mEndTime != null) {
						Log.e("UI", "duration" + duration);
						mMediaController.mEndTime.setText(StringUtils
								.generateTime(duration));
					}
					if (mMediaController.mCurrentTime != null) {
						Log.e("UI", "currentTime" + currentTime);
						mMediaController.mCurrentTime.setText(StringUtils
								.generateTime(currentTime));
					}

					if (duration - currentTime < 1000) {
						Toast.makeText(getApplication(), "已观看完毕,将重新播放!",
								Toast.LENGTH_SHORT).show();
					} else {
						Toast.makeText(getApplication(), "恢复历史播放!",
								Toast.LENGTH_SHORT).show();
					}
				}
				isResumePlayed = false;
			}
		
		}
	}

	@Override
	public boolean onError(YzVideoView yzVideoView) {
		synchronized (TvPlayActivity.this) {
			_mhandler.removeMessages(PLAY_TIME_OUT);
			
			if (yzVideoView != null) {
				yzVideoView.pause();
				yzVideoView.release(true);
			}
			if (power_off) {
				is_start = false;
			}
			showError();
			_mhandler.removeMessages(CHANGE_CHANNEL_TIMO_OUT);
		}	
		return false;
	}

	@Override
	public void onCompletion(YzVideoView yzVideoView) {
		Log.e("UI", "YzVideoView>>>>>>>>>>>>>>>onCompletion");
		mMediaController.is_complete = true;
		mLoadingView.setVisibility(View.GONE);
		_mhandler.removeMessages(CHANGE_CHANNEL_TIMO_OUT);
		mMediaController.closePlay();
	}
	
	public int px2dip(Context context, float pxValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (pxValue / scale + 0.5f);
	}

	@Override
	public void sendPlayTimeOutMessage() {
		_mhandler.sendEmptyMessageDelayed(PLAY_TIME_OUT, 30000);
	}


	@Override
	public void canclePlayView(long currentTime,long totalTime) {
		abouttywindow.dismiss();
//		if (is_start && !live && !video.isNative_video()) {
//			if(currentTime<=1000||totalTime-currentTime<=1000)
//				currentTime=0;
//				video.setLastPlayTime(System.currentTimeMillis());
//				video.setPlayedTime(currentTime);
//		}
		if(video.isNative_video()){
			  finish();
		}else{
			if (is_start && !live) {	
				if(currentTime<=1000||totalTime-currentTime<=1000)
					currentTime=0;
				video.setLastPlayTime(System.currentTimeMillis());
				video.setPlayedTime(currentTime);
				video.setTotalTime(totalTime);
			}
			Intent intent=new Intent();  
	        intent.putExtra(KEY_USER_CURRENT_TIME, currentTime);  
	        intent.putExtra(KEY_USER_TOTAL_TIME, totalTime); 
	        Bundle b=new Bundle();
	        b.putSerializable("video", video);
	        intent.putExtras(b);
	        setResult(RESULT_OK, intent);  
	        finish();
		}
	}

	@Override
	public void showHideAboutYzDialog(boolean isShow) {
		if (isShow) {
			OnClickListener mClickListener = new OnClickListener() {
				public void onClick(View v) {
					abouttywindow.dismiss();
				}
			};

			if (!abouttywindow.isShowing()) {
				int dlg_anim = R.style.DlgAnimBottom;
				abouttywindow.setAnimationStyle(dlg_anim);
				abouttywindow.showAtLocation(this.getWindow().getDecorView(),
						Gravity.CENTER, 0, 0);
			} else {
				abouttywindow.dismiss();
			}
		} else {
			abouttywindow.dismiss();
		}
	}

	@Override
	public void showBuffText(long newsposit) {
		if (!video.isNative_video() && !mMediaController.is_complete && !live) {
			playpause.setVisibility(View.INVISIBLE);
			mMediaController.mCurrent = newsposit;
			_mhandler.removeMessages(CHANGE_CHANNEL);
			_mhandler.removeMessages(CHANGE_CHANNEL_TIMO_OUT);
			_mhandler.sendEmptyMessageDelayed(CHANGE_CHANNEL, 17000);
			_mhandler.sendEmptyMessageDelayed(CHANGE_CHANNEL_TIMO_OUT,
					100000);
			needResume = true;
			mMediaController.setEnabled(false);
			if (!show_change_quantity) {
				load_show.setText("正在缓冲视频...");
				mLoadingprg.setVisibility(View.VISIBLE);
				mLoadingView.setVisibility(View.VISIBLE);
			}						
		}
	
	}

	@Override
	public boolean notControllPlay() {

		return false;
	}

	@Override
	public void onSeekComplete(MediaPlayer mp) {
		if (power_off) {
			mVideoView.pause();
		}
	}

	long rcurrent = 0;

	@Override
	public void setActTime(long current, long totaltime) {
		if (current != 0) {
			rcurrent = current;
		}
		if (this.totalTime == 0) {
			this.totalTime = totaltime;
		}
		if (show_current_time != null
				&& show_current_time.getText().equals("00:00:00") && !live) {
			show_current_time.setText(StringUtils.generateTime(current));
		}
		if (show_totaltime != null && !live) {
			show_totaltime.setText(StringUtils.generateTime(totaltime));
		}
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		Log.e("mark", "" + keyCode);

		if (keyCode == KeyEvent.KEYCODE_BACK
				&& event.getAction() == KeyEvent.ACTION_DOWN) {
			if (mMediaController.mBtnShowing) {
				mMediaController.hideBtn();
				return true;
			}
			mMediaController.is_complete = false;
			mMediaController.closePlay();
			return true;
		}
		switch (keyCode) {
		case KeyEvent.KEYCODE_MENU:
			mMediaController.showButtonWindow();
			return true;
		case KeyEvent.KEYCODE_DPAD_CENTER:
			if (mMediaController.is_complete) {
				mMediaController.mCurrent = 0;
				mMediaController.completeWindow.dismiss();
				changerChannel(mMediaController.currentPath);
			} else {
				doPauseResume();
			}
			return true;
		case KeyEvent.KEYCODE_ENTER:
			if (mMediaController.is_complete) {
				mMediaController.mCurrent = 0;
				mMediaController.completeWindow.dismiss();
				changerChannel(mMediaController.currentPath);
			} else {
				doPauseResume();
			}
			return true;
		case KeyEvent.KEYCODE_DPAD_LEFT:
			Log.e("UPLOAD", "onKeyLEFT");
			mMediaController.show();
			_mhandler.removeMessages(LARGE_SEEK_PLAY);
			mMediaController.mHandler
					.removeMessages(mMediaController.SHOW_PROGRESS);
			if (mMediaController.mPlayer != null
					&& mMediaController.mPlayer != null) {
				long duration = mMediaController.mPlayer.getDuration();
				long currentposition = (duration * mMediaController.mProgress
						.getProgress()) / 1000L - 10000;
				mMediaController.newposition = currentposition;
				if (currentposition < 0) {
					currentposition = 500;
				}
				mMediaController.newposition = currentposition;
				if (duration > 1000) {
					long pos = 1000L * currentposition / duration;
					mMediaController.mProgress.setProgress((int) pos);
				}
				if (mMediaController.mCurrentTime != null)
					mMediaController.mCurrentTime.setText(StringUtils
							.generateTime(currentposition));
				Message m = new Message();
				m.what = LARGE_SEEK_PLAY;
				m.obj = (int) currentposition;
				_mhandler.sendMessageDelayed(m, 700);
			}
			return true;
		case KeyEvent.KEYCODE_DPAD_RIGHT:
			Log.e("UPLOAD", "onKeyRIGHT");
			mMediaController.show();
			_mhandler.removeMessages(LARGE_SEEK_PLAY);
			mMediaController.mHandler
					.removeMessages(mMediaController.SHOW_PROGRESS);
			if (mMediaController.mPlayer != null
					&& mMediaController.mPlayer != null) {
				long duration = mMediaController.mPlayer.getDuration();
				long currentposition = (duration * mMediaController.mProgress
						.getProgress()) / 1000L + 10000;
				if (currentposition > duration) {
					currentposition = duration;
				}
				mMediaController.newposition = currentposition;
				if (duration > 1000) {
					long pos = 1000L * currentposition / duration;
					mMediaController.mProgress.setProgress((int) pos);
				}
				if (mMediaController.mCurrentTime != null)
					mMediaController.mCurrentTime.setText(StringUtils
							.generateTime(currentposition));
				Message m = new Message();
				m.what = LARGE_SEEK_PLAY;
				m.obj = (int) currentposition;
				_mhandler.sendMessageDelayed(m, 700);
			}
			return true;
		case KeyEvent.KEYCODE_VOLUME_UP:
			_mhandler.removeMessages(INIT);
			_mhandler.sendEmptyMessageDelayed(INIT, 800);
			// 显示
			mOperationBg.setImageResource(R.drawable.video_volumn_bg);
			// mVolumeBrightnessLayout.bringToFront();
			mVolumeBrightnessLayout.setVisibility(View.VISIBLE);
			int mVolumeUp = mAudioManager
					.getStreamVolume(AudioManager.STREAM_MUSIC);
			int index = 1 + mVolumeUp;
			if (index > mMaxVolume)
				index = mMaxVolume;
			// 变更声音
			mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, index, 0);
			// 变更进度条
			ViewGroup.LayoutParams lp = mOperationPercent.getLayoutParams();
			lp.width = findViewById(R.id.tv_playview_operation_full)
					.getLayoutParams().width * index / mMaxVolume;
			mOperationPercent.setLayoutParams(lp);
			return true;
		case KeyEvent.KEYCODE_VOLUME_DOWN:
			_mhandler.removeMessages(INIT);
			_mhandler.sendEmptyMessageDelayed(INIT, 800);
			// 显示
			mOperationBg.setImageResource(R.drawable.video_volumn_bg);
			// mVolumeBrightnessLayout.bringToFront();
			mVolumeBrightnessLayout.setVisibility(View.VISIBLE);
			int mVolumeDown = mAudioManager
					.getStreamVolume(AudioManager.STREAM_MUSIC);
			int index1 = mVolumeDown - 1;
			if (index1 < 0)
				index1 = 0;
			// 变更声音
			mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, index1, 0);
			// 变更进度条
			ViewGroup.LayoutParams lp1 = mOperationPercent.getLayoutParams();
			lp1.width = findViewById(R.id.tv_playview_operation_full)
					.getLayoutParams().width * index1 / mMaxVolume;
			mOperationPercent.setLayoutParams(lp1);
			return true;

		}
		return super.onKeyDown(keyCode, event);
	}

	private void doPauseResume() {
		Log.e("mark", "" + 3);
		if (mMediaController.mPlayer == null) {
			return;
		}
		if (mMediaController.mPlayer.isPlaying()) {
			mMediaController.mPlayer.pause();
			if (mMediaController.isLive()) {
				mMediaController.mPlayer.release(true);
			}
		} else {
			if (mMediaController.isLive()) {
				mMediaController.mCurrent = 0;
				changerChannel(mMediaController.currentPath);
			} else {
				mMediaController.mPlayer.start();
			}
		}
		updatePausePlay();
	}

	private void updatePausePlay() {
		if (mMediaController.mPlayer != null
				&& mMediaController.mPlayer.isPlaying()) {
			Log.e("mark", "" + 1);
			playpause.setImageResource(R.drawable.large_pause);
			playpause.setVisibility(View.INVISIBLE);
			mMediaController.hide();
		} else {
			if (mMediaController.mPlayer != null && is_start&&!mLoadingView.isShown()) {
				mMediaController.superShow();
				playpause.setImageResource(R.drawable.large_play);
				playpause.setVisibility(View.VISIBLE);
			}
		}
	}

	public void showDlnaDial() {

	}

	// 监听电源键
	private final BroadcastReceiver mBatInfoReceiver = new BroadcastReceiver() {
		public void onReceive(final Context context, final Intent intent) {
			final String action = intent.getAction();
			if (Intent.ACTION_SCREEN_OFF.equals(action)) {
				Log.e("WIFI_S", "ScreenOFF");
				_mhandler.removeMessages(PLAY_TIME_OUT);
				power_off = true;
				}
			if (Intent.ACTION_SCREEN_ON.equals(action)) {
				Log.e("WIFI_S", "ScreenOn");
				}
			}
		};

	@Override
	public void pauseShowAD() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void startAfterAD() {
		// TODO Auto-generated method stub
		
	}
}
