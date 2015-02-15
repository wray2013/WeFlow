package com.simope.yzvideo.base;

import java.util.List;

import nativeInterface.AdPlayView;
import nativeInterface.AdPlayView.SendVideoStateListener;
import nativeInterface.AdGifImageView;
import nativeInterface.MobilePlayInterface;
import nativeInterface.SimopeVideoView;
import nativeInterface.SmoAdMultiPlay;
import nativeInterface.SmoAdMultiPlay.ADDesc;
import nativeInterface.SmoAdMultiPlay.MultiPlayObj;
import nativeInterface.YzVideoView;
import nativeInterface.YzVideoView.YzOnCompletionListener;
import nativeInterface.YzVideoView.YzOnErrorListener;
import nativeInterface.YzVideoView.YzOnInfoListener;
import org.cybergarage.upnp.Device;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnBufferingUpdateListener;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.media.MediaPlayer.OnSeekCompleteListener;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.webkit.WebSettings.RenderPriority;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;
import com.simope.yzvideo.R;
import com.simope.yzvideo.control.MobileMediaController;
import com.simope.yzvideo.control.listener.ControllerPlayListener;
import com.simope.yzvideo.control.listener.ScreenListener;
import com.simope.yzvideo.control.listener.ScreenListener.ScreenStateListener;
import com.simope.yzvideo.dlna.DLNAContainer;
import com.simope.yzvideo.dlna.DLNAService;
import com.simope.yzvideo.dlna.IController;
import com.simope.yzvideo.dlna.MultiPointController;
import com.simope.yzvideo.entity.Video;
import com.simope.yzvideo.net.ConnectionDetector;
import com.simope.yzvideo.util.StringUtils;

@SuppressLint({ "InlinedApi", "NewApi" })
public class MobilePlayActivity extends BaseActivity implements
		OnCompletionListener, OnPreparedListener, OnBufferingUpdateListener,
		OnErrorListener, YzOnCompletionListener, YzOnErrorListener,
		YzOnInfoListener, ControllerPlayListener, OnSeekCompleteListener,
		SendVideoStateListener {
	private String mTitle;
	private View mVolumeBrightnessLayout;
	private ImageView mOperationBg;
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
	private int tmpc_scale = YzVideoView.VIDEO_LAYOUT_ORIGIN;
	private GestureDetector mGestureDetector;
	private MobileMediaController mMediaController;
	private TextView load_show;
	private View mLoadingView;
	private ProgressBar mLoadingprg;
	private boolean live;
	private Video video;
	private static final int INIT = 0x81;
	private static final int BUFFRING_PLAY = 0x82;
	private static final int CHANGE_CHANNEL = 0x83;
	private static final int LOAD_PLAYADDRESS = 0x84;
	public static final int PLAY_TIME_OUT = 0x85;
	public static final int NO_PLAYOBJECT = 0x88;
	public static final int BUFF_TIME = 3000;
	public static final int VIDEOVIEW_BUFF_STATE = 0x89;
	public static final int LARGE_SEEK_PLAY = 0x90;
	private static final int CHANGE_CHANNEL_TIMO_OUT = 0x91;
	/** 定时隐藏 */
	public Handler _mhandler;
	private SimopeVideoView mVideoView;
	private YzVideoView yzVideoView;
	private boolean LOCK_Screen = false;

	private View aboutty_view;
	private PopupWindow abouttywindow;
	private Uri uri;
	private boolean show_change_quantity = false;
	private View mobile_playview_showtime_ll;
	private TextView show_current_time, show_totaltime;
	private List<Device> mDevices;
	private View dlna_ctr_view;
	private PopupWindow dlna_window;
	private ScreenListener screenListener;
	
	/*客户端参数*/
	private String srcPath;
	private String srcName;
	private long srcPlayedTime = 0;
	private long srcTotalTime = 0;
	private long mTotaltime = 0;
	
	public void onCreate(Bundle bundle) {
		super.onCreate(bundle);

		setContentView(R.layout.mobile_playview);

		mDevices = DLNAContainer.getInstance().getDevices();
		mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
		mMaxVolume = mAudioManager
				.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
		if(TextUtils.isEmpty(srcPath)){
			return;
		}
		getIntentUrl();
		initView();
		initMediaController();
		initGestureAndListener();
		startGetSmo();
		initWbView();
	}

	private TextView mobile_webview_id_WebTitle;
	private Button mobile_webview_id_BackBtn, mobile_webview_id_RefreshBtn;
	private WebView mobile_webview_id_WebView;
	private View mobile_webview_contain;
	private TranslateAnimation showAction, hideAction;

	private void initWbView() {
		showAction = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f,
				Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
				1.0f, Animation.RELATIVE_TO_SELF, 0.0f);
		showAction.setDuration(400);
		hideAction = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f,
				Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
				0.0f, Animation.RELATIVE_TO_SELF, 1.0f);
		hideAction.setDuration(400);
		mobile_webview_id_WebView = (WebView) findViewById(R.id.mobile_webview_id_WebView);
		mobile_webview_id_WebTitle = (TextView) findViewById(R.id.mobile_webview_id_WebTitle);
		mobile_webview_id_BackBtn = (Button) findViewById(R.id.mobile_webview_id_BackBtn);
		mobile_webview_contain = findViewById(R.id.mobile_webview_contain);
		mobile_webview_id_BackBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mobile_webview_contain.startAnimation(hideAction);
				mobile_webview_id_WebView.clearCache(true);
				mobile_webview_contain.setVisibility(View.GONE);
				if (!ADisOver) {
					adview.start();
					adimgview.start();
				}
			}
		});
		mobile_webview_id_RefreshBtn = (Button) findViewById(R.id.mobile_webview_id_RefreshBtn);
		mobile_webview_id_RefreshBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mobile_webview_id_WebView.reload();
			}
		});

		mobile_webview_id_WebView.setWebViewClient(new WebViewClient() {
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				view.loadUrl(url);
				return true;
			}

			public void onPageFinished(WebView view, String url) {
				super.onPageFinished(view, url);
				if (view.getTitle() != null)
					mobile_webview_id_WebTitle.setText(view.getTitle());
			}

			@Override
			public void onPageStarted(WebView view, String url, Bitmap favicon) {
				super.onPageStarted(view, url, favicon);
				mobile_webview_id_WebTitle.setText("正在载入...");
			}
		 });
		mobile_webview_id_WebView.setWebChromeClient(new WebChromeClient() {
			@Override
			public void onProgressChanged(WebView view, int newProgress) {

			}

			public void onReceivedTitle(WebView view, String title) {
				super.onReceivedTitle(view, title);
			}
		});
		WebSettings webSettings = mobile_webview_id_WebView.getSettings();
		webSettings.setDefaultTextEncodingName("utf-8");
		webSettings.setJavaScriptEnabled(true);
		webSettings.setBuiltInZoomControls(true);
		webSettings.setUseWideViewPort(true);
		webSettings.setRenderPriority(RenderPriority.HIGH);
		mobile_webview_id_WebView.loadData("", "text/html", null);
	}

	private void showAdWeb(ADDesc ad) {
		if (ad == null||ad.action_url==null||TextUtils.isEmpty(ad.action_url.trim())) {
			return;
		}
		mMediaController.hide();
		mobile_webview_contain.startAnimation(showAction);
		mobile_webview_contain.setVisibility(View.VISIBLE);
		mobile_webview_id_WebView.loadUrl(ad.action_url);
	}

	public void setVideoInfo(String name, String path, long playedTime, long totaltime){
		srcName = name;
		srcPath = path;
		srcPlayedTime = playedTime;
		srcTotalTime = totaltime;
	}
	
	private void getIntentUrl() {
		Intent intent = getIntent();
		uri = intent.getData();
		if (uri != null) {
			String uriScheme = uri.getScheme();
			String uriPath = uri.getPath();
			String[] dd = uriPath.split("/");
			mTitle = dd[dd.length - 1];
			Log.e("UI", "uriScheme=" + uriScheme);
			if (uriScheme.equals("file")) {
				path = uri.toString();
				video = new Video();
				video.setPlayAddress(uri.toString());
				video.setNative_video(true);
			} else {
				path = uri.toString();
				video = new Video();
				video.setPlayAddress(uri.toString());
				video.setNative_video(false);
			}
		} else {
			/*Bundle bundle = intent.getExtras();
			video = (Video) bundle.getSerializable("video");*/	
			/*srcPath = "http://testria.iluokuang.cn:8888/rw/media/movieplay.html?requestapp={\"media_id\":70,\"movie_type\":1}&70.smo";
			//srcPath = "http://192.168.2.55:8088/ad_test.smo";
			srcName = "xxxx";
			srcPlayedTime = 0;
			srcTotalTime = 0;*/
			video=new Video();		
			video.setPlayAddress(srcPath);
			System.out.println("srcPath ===" + srcPath);
			video.setTitle(srcName);
			mTitle = video.getTitle();
			video.setPlayedTime(srcPlayedTime);
			video.setTotalTime(srcTotalTime);	
		}
	}

	private void startDLNAService() {
		Intent intent = new Intent(getApplicationContext(), DLNAService.class);
		startService(intent);
	}

	private void initView() {
		_mhandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case LARGE_SEEK_PLAY:
					int curr = (Integer) msg.obj;
					mMediaController.mPlayer.seekTo(curr);
					mMediaController.mHandler
							.sendEmptyMessage(mMediaController.SHOW_PROGRESS);
					break;
				case AUTO_INCREASING:
					stopAutoIncreasing();
					getPositionInfo();
					startAutoIncreasing();
					break;
				case VIDEOVIEW_BUFF_STATE:
					onBuffPlay();
					mDevices = DLNAContainer.getInstance().getDevices();
					if (mDevices != null && mDevices.size() >= 1) {
						mMediaController.initDlnaBtn(true);
					} else {
						mMediaController.initDlnaBtn(false);
					}
					if (dlna_window.isShowing()) {
						mMediaController.mPlayer.pause();
					}
					_mhandler
							.sendMessageDelayed(_mhandler
									.obtainMessage(VIDEOVIEW_BUFF_STATE), 1000);
					break;
				case NO_PLAYOBJECT:
					if (!video.isNative_video()) {
						_mhandler.removeMessages(PLAY_TIME_OUT);
						if (mMediaController.mPlayer != null) {
							mMediaController.mPlayer.release(true);
						}
						showAlertDialog(MobilePlayActivity.this, "  无法播放该影片",
								"播放对象不存在!!!", false);
					}
					break;
				case LOAD_PLAYADDRESS:
					startAddressPlay();
					break;
				case INIT:
					mVolumeBrightnessLayout.setVisibility(View.GONE);
					mobile_playview_showtime_ll.setVisibility(View.GONE);
					break;
				case BUFFRING_PLAY:
					mVideoView.start();
					needResume = false;
					break;
				case CHANGE_CHANNEL:
					if (uri == null && mLoadingView.isShown()) {
						if (mLoadingprg.isShown()) {
							//by youtian-wuhan fix 11851
							/*mLoadingprg.setVisibility(View.GONE);
							load_show.setText("网络不给力,可以尝试切换低码率观看!");*/
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

							showAlertDialog(MobilePlayActivity.this, "  对不起",
									"网络环境不好,加载超时!", false);
							mMediaController.mPlayer.release(true);
						} else {
							showAlertDialog(MobilePlayActivity.this,
									"  网络连接异常", "请检查您的网络连接是否正常!", false);
						}
					}
					break;
				case PLAY_TIME_OUT:
					if (video.isNative_video()) {
						_mhandler.removeMessages(PLAY_TIME_OUT);
					} else {
						synchronized (MobilePlayActivity.this) {
							if (mMediaController.mPlayer != null) {
								mMediaController.mPlayer.release(true);
							}
							showAlertDialog(MobilePlayActivity.this, "  错误提示",
									"加载路径超时,请重试!", false);
						}
					}
					break;
				}
			}
		};
		mVolumeBrightnessLayout = findViewById(R.id.mobile_playview_operation_volume_brightness);
		mOperationBg = (ImageView) findViewById(R.id.mobile_playview_operation_bg);
		mOperationPercent = (ImageView) findViewById(R.id.mobile_playview_operation_percent);
		mLoadingView = findViewById(R.id.mobile_playview_video_loading);
		mLoadingprg = (ProgressBar) findViewById(R.id.mobile_playview_load_prg);
		load_show = (TextView) findViewById(R.id.mobile_playview_load_show);
		// play_buff_ll = (LinearLayout)
		// findViewById(R.id.mobile_playview_play_buff_ll);

		mobile_playview_showtime_ll = findViewById(R.id.mobile_playview_showtime_ll);
		show_current_time = (TextView) findViewById(R.id.mobile_playview_show_current_time);
		show_totaltime = (TextView) findViewById(R.id.mobile_playview_show_totaltime);

		// TextView mtitle = (TextView)
		// findViewById(R.id.mobile_playview_title);
		// mtitle.setText(mTitle);

		aboutty_view = this.getLayoutInflater().inflate(
				R.layout.about_player_dilg_pop, null);

		abouttywindow = new PopupWindow(aboutty_view,
				android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
				android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
		abouttywindow.setFocusable(false);
		abouttywindow.setBackgroundDrawable(null);
		abouttywindow.setOutsideTouchable(true);

		dlna_ctr_view = this.getLayoutInflater().inflate(
				R.layout.yzvideo_media_dlna, null);
		dlna_window = new PopupWindow(dlna_ctr_view,
				android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
				android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
		dlna_window.setFocusable(false);
		dlna_window.setBackgroundDrawable(null);
		dlna_window.setOutsideTouchable(true);

		adview = (AdPlayView) findViewById(R.id.ad_playview);
		adview.setSendVideoStateListener(this);

		adimgview = (AdGifImageView) findViewById(R.id.ad_imgplayview);
		adimgview.setSendVideoStateListener(this);

		screenListener = new ScreenListener(this);
		screenListener.begin(new ScreenStateListener() {

			@Override
			public void onUserPresent() {
				Log.e("UI", "ACTIVITY>>>>>>onUserPresent");
			}

			@Override
			public void onScreenOn() {
				Log.e("UI", "ACTIVITY>>>>>>onScreenOn");
			}

			@Override
			public void onScreenOff() {
				Log.e("UI", "ACTIVITY>>>>>>onScreenOff");
				LOCK_Screen = true;
				_mhandler.removeMessages(PLAY_TIME_OUT);
			}
		});
	}

	@Override
	public void showAdWebView(ADDesc ad) {
		showAdWeb(ad);
	}


	private AdPlayView adview;
	private AdGifImageView adimgview;

	private void initMediaController() {
		mMediaController = new MobileMediaController(this);
		mMediaController.setPlayName(mTitle);
		mMediaController.setLive(live);
		mMediaController.setControllerPlayListener(this);
		initVideoView();
		initYzVideoView();
	}

	private void initGestureAndListener() {
		mGestureDetector = new GestureDetector(this, new MyGestureListener());
	}

	private void startGetSmo() {
		if (uri != null) {
			Log.e("UI", "播放有路径视频文件" + path);
			ADisOver = true;
			startAddressPlay();
		} else {
			// 多码率地址，如果有,就不需解析.smo获取地址了
			if (video != null && !video.getPlayAddress().endsWith(".smo")) {
				path = video.getPlayAddress().trim();
				ADisOver = true;
				startAddressPlay();
			} else {
				smoAdMultiPlay = new SmoAdMultiPlay(video.getPlayAddress(),
						this);
				smoAdMultiPlay.setParseSmoCompleteListener(this);
			}
		}
	}

	private SmoAdMultiPlay smoAdMultiPlay;
	private MultiPlayObj multiPlayObj;
	private ADDesc[] addescs;
	private ADDesc  adStart,adPause;
	private boolean ADisOver = false;
	private boolean start_tag=false;

	// s== 1,成功
	// s==-2,获取到smo，但是解析不了数据
	// s==-1,无法连接服务器
	// s==0,服务器返回错误，404
	// 没有广告 请设置 ADisOver=true;
	public void parSmoComplete(int result) {
		if (result == SmoAdMultiPlay.PARSE_SMO_SUCCESS) {
			multiPlayObj = smoAdMultiPlay.multiPlayObj;
			addescs = smoAdMultiPlay.ad;
			if (multiPlayObj != null) {
				video.setMultiAddress(SubNullArrItems(multiPlayObj.levelurl));
				mMediaController
						.setBtn_arr(SubNullArrItems(multiPlayObj.levelname));
				mMediaController
						.setPlayAddress_arr(SubNullArrItems(multiPlayObj.levelurl));
				mMediaController.initBtn();
			}
			if (addescs != null) {
				for (ADDesc ad : addescs) {
					if (ad.ad_type == SmoAdMultiPlay.AD_TYPE_START) {
						adStart = ad;
						start_tag=true;
						mMediaController.hide();
						startAd(adStart);
						return;
					}
				}
			}
			ADisOver = true;
			startAddressPlay();
		} else {
			if (mMediaController != null && mMediaController.mPlayer != null) {
				mMediaController.mPlayer.release(true);
			}
			if (ConnectionDetector.getConnectionDetector(getApplication())
					.isConnectingToInternet()) {
				if (uri == null) {
					switch (result) {
					case SmoAdMultiPlay.PARSE_SMO_ERROR:
						showAlertDialog(MobilePlayActivity.this, "  错误提示",
								"无法解析SMO数据!", false);
						break;
					case SmoAdMultiPlay.PARSE_SMO_SERVER_ERROR:
						showAlertDialog(MobilePlayActivity.this, "  错误提示",
								"无法连接服务器!", false);
						break;
					case SmoAdMultiPlay.PARSE_SMO_GET_ERROR:
						showAlertDialog(MobilePlayActivity.this, "  错误提示",
								"服务器返回错误!", false);
						break;
					}
				}
			} else {
				showAlertDialog(MobilePlayActivity.this, "  网络连接异常",
						"请检查您的网络连接是否正常!", false);
			}
		}
	}

	private void startAd(ADDesc ad) {
		if (ad != null) {
			mLoadingView.setVisibility(View.GONE);
			if(ad.ad_type==smoAdMultiPlay.AD_TYPE_START){
				yzVideoView.setVisibility(View.GONE);
				mVideoView.setVisibility(View.GONE);
				begintoBuff=false;
				begintoPlay=false;
				start_tag=true;	
			}
			ADisOver = false;
			if (ad.ad_show_mode == SmoAdMultiPlay.AD_SHOW_FULL||ad.ad_type==smoAdMultiPlay.AD_TYPE_START) {
				mMediaController.hide();
			}
			if (ad.multiPlayAddress.levelurl[0].endsWith(".jpg")
					|| ad.multiPlayAddress.levelurl[0].endsWith(".png")
					|| ad.multiPlayAddress.levelurl[0].endsWith(".gif")) {
				adimgview.setVisibility(View.VISIBLE);
				adimgview.init(ad);
			} else {
				adview.setVisibility(View.VISIBLE);
				adview.setVideoPath(ad);
				adview.startAdPlay();
			}
			
			this.mobilePlayInterface.onStartADEvent(ad.multiPlayAddress.levelurl[0], ad.ad_type + "");
		}
	}
	
	private boolean begintoBuff=false;
	private boolean begintoPlay=false;
	@Override
	public void sendVideoBegintoBuff() {
		if(!begintoBuff){
			startAddressPlay();
			begintoBuff=true;
		}
	}

	@Override
	public void sendVideoStartPlay() {
		if(begintoPlay){
			return;
		}
		if (mMediaController != null && mMediaController.mPlayer != null) {
			mMediaController.mPlayer.start();
			mMediaController.setEnabled(true);
		}
		if (LOCK_Screen) {
			LOCK_Screen = false;
		}
		ADisOver = true;
		begintoPlay=true;
		start_tag=false;
	}

	@Override
	public void imgvCloseAd() {
		if (!ADisOver) {
			ADisOver = true;
			adimgview.release();
		}
	}
	
	
	@Override
	public void cancleAct() {
		mMediaController.is_complete = false;
		mMediaController.closePlay();
	}

	@Override
	public void pauseShowAD() {
		if (addescs == null) {
			return;
		}
		for (ADDesc ad : addescs) {
			if (ad.ad_type == SmoAdMultiPlay.AD_TYPE_PAUSE) {
				adPause = ad;
				startAd(adPause);
			}
		}
	}

	@Override
	public void startAfterAD() {
		if (!ADisOver) {
			ADisOver = true;
			adimgview.release();
		}
	}

	protected void initVideoView() {
		if (mVideoView == null) {
			mVideoView = (SimopeVideoView) findViewById(R.id.mobile_playview_surface_view);
			mVideoView.setOnCompletionListener(MobilePlayActivity.this);
			// mVideoView.setOnInfoListener(this);
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
			yzVideoView = (YzVideoView) findViewById(R.id.mobile_playview_yzVideoView);
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
	public void startAddressPlay() {
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
			if (path.contains("index.hsm") || live) {
				yzVideoView.setVisibility(View.VISIBLE);
				mVideoView.setVisibility(View.GONE);
				yzVideoView.setMediaController(mMediaController);
				mMediaController.current_video = false;
				if (video.getPlayedTime() < 1000||video.getPlayedTime()>video.getTotalTime()) {
					if (ADisOver) {
						yzVideoView.start(path, 0, BUFF_TIME);
					} else {
						yzVideoView.load(path, 0, BUFF_TIME);
					}
				} else {
					mMediaController.mCurrent = video.getPlayedTime();
					if (!start_tag) {
						yzVideoView.start(path,
								(int) mMediaController.mCurrent, BUFF_TIME);
					} else {
						yzVideoView.load(path, (int) mMediaController.mCurrent,
								BUFF_TIME);
					}
				}
			} else {
				mVideoView.setVisibility(View.VISIBLE);
				yzVideoView.setVisibility(View.GONE);
				mVideoView.setMediaController(mMediaController);
				mMediaController.current_video = true;
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
			if (mDevices != null && mDevices.size() >= 1) {
				mMediaController.initDlnaBtn(true);
			}
		}
		startDLNAService();
	}

	private void togMediaControlsVisiblity() {
		if (mMediaController.isShowing()) {
			Log.v("UI", "mMediaController.isShowing(),hide()");
			mMediaController.hide();
		} else {
			mMediaController.show();
			Log.v("UI", "mMediaController.ishide(),show()");
		}
	}

	protected void onResume() {
		super.onResume();
		if (!ADisOver) {
			adview.resume();
			adimgview.start();
		}
		if (LOCK_Screen) {
			LOCK_Screen = false;
			return;
		}
		if (mVideoView != null && mMediaController.current_video&& mVideoView.sfDestroy) {
			mVideoView.release(true);
			int duration = (int) mMediaController.mDuration;
			if (duration > 1000) {
				long pos = 1000L * mMediaController.mCurrent / duration;
				mMediaController.mProgress.setProgress((int) pos);
			}
			mMediaController.mHandler.removeMessages(mMediaController.FADE_OUT);
			mMediaController.mHandler
					.removeMessages(mMediaController.SHOW_PROGRESS);
			if (ADisOver) {
				load_show.setText("正在恢复播放...");
				mLoadingprg.setVisibility(View.VISIBLE);
				mLoadingView.setVisibility(View.VISIBLE);
			}
			mVideoView.setQuality(mMediaController.currentPath, (int) mMediaController.mCurrent);
//			_mhandler.sendEmptyMessageDelayed(VIDEOVIEW_BUFF_STATE, 1000);
		} else if (yzVideoView != null && !mMediaController.current_video
				&& yzVideoView.sfDestroy) {
			yzVideoView.release(true);
			int duration = (int) mMediaController.mDuration;
			if (duration > 1000) {
				long pos = 1000L * mMediaController.mCurrent / duration;
				mMediaController.mProgress.setProgress((int) pos);
			}
			yzVideoView.setVisibility(View.VISIBLE);
			mMediaController.mHandler.removeMessages(mMediaController.FADE_OUT);
			mMediaController.mHandler
					.removeMessages(mMediaController.SHOW_PROGRESS);
			if (ADisOver) {
				load_show.setText("正在恢复播放...");
				mLoadingprg.setVisibility(View.VISIBLE);
				mLoadingView.setVisibility(View.VISIBLE);
			}
			if (live) {
				if (!start_tag) {
					yzVideoView.start(mMediaController.currentPath, 0,
							BUFF_TIME);
				} else {
					yzVideoView
							.load(mMediaController.currentPath, 0, BUFF_TIME);
				}
			} else {
				if (!start_tag) {
					yzVideoView.start(mMediaController.currentPath,
							(int) mMediaController.mCurrent, BUFF_TIME);
				} else {
					yzVideoView.load(mMediaController.currentPath,
							(int) mMediaController.mCurrent, BUFF_TIME);
				}
			}
			yzVideoView.sfDestroy = !yzVideoView.sfDestroy;
		}
	}

	protected void onPause() {
		super.onPause();
		if(TextUtils.isEmpty(srcPath)){
			return;
		}
		if (!ADisOver) {
			adview.pause();
			adimgview.stop();
		}
		if (mMediaController.mPlayer != null) {
			mMediaController.mCurrent = mMediaController.mPlayer
					.getCurrentPosition();
			parseError = 2;
		}
	}

	@Override
	protected void onStop() {
		super.onStop();		
		if(TextUtils.isEmpty(srcPath)){
			return;
		}
		if (!ADisOver) {
			adview.stop();
			adimgview.stop();
		}
		Log.e("UI", "Activity----------------->>>>>>onStop() ");
		_mhandler.removeMessages(VIDEOVIEW_BUFF_STATE);
		_mhandler.removeMessages(CHANGE_CHANNEL);
		_mhandler.removeMessages(CHANGE_CHANNEL_TIMO_OUT);
		if (mMediaController.mPlayer != null) {
			mMediaController.mPlayer.pause();
		}
		if (needResume && mLoadingView.isShown()) {
			if (abouttywindow.isShowing()) {
				abouttywindow.dismiss();
			}
			mLoadingView.setVisibility(View.GONE);
		}
		if (mMediaController != null) {
			mMediaController.hide();
		}
	}

	private void stopDLNAService() {
		Intent intent = new Intent(getApplicationContext(), DLNAService.class);
		stopService(intent);
		DLNAContainer.getInstance().clear();
	}

	protected void onDestroy() {
		super.onDestroy();
		if(TextUtils.isEmpty(srcPath)){
			return;
		}
		stopDLNAService();
		if (dlna_window.isShowing()) {
			stop();
			dlna_window.dismiss();
		}
		if (adview != null) {
			adview.release();
		}
		if (adimgview != null) {
			adimgview.onDestroy();
		}
		synchronized (MobilePlayActivity.this) {
			_mhandler.removeMessages(VIDEOVIEW_BUFF_STATE);
			_mhandler.removeMessages(LOAD_PLAYADDRESS);
			_mhandler.removeMessages(NO_PLAYOBJECT);
			_mhandler.removeMessages(PLAY_TIME_OUT);
			_mhandler.removeMessages(CHANGE_CHANNEL_TIMO_OUT);
			if (mMediaController != null) {
				mMediaController.mHandler
						.removeMessages(mMediaController.SHOW_PROGRESS);
				_mhandler.removeMessages(0);
				// _mhandler.removeMessages(BUFFRING_PLAY);
				_mhandler.removeMessages(CHANGE_CHANNEL);
				if (mMediaController.isRegister) {
					mMediaController.unregister();
				}
				if (mVideoView != null) {
					mVideoView.setVisibility(View.VISIBLE);
					mVideoView.release(true);
					mVideoView = null;
				} else {
					if (yzVideoView != null) {
						yzVideoView.release(true);
						yzVideoView.nativeOnDelete();
						yzVideoView.setVisibility(View.VISIBLE);
						yzVideoView = null;
					}
				}
				mMediaController = null;
			}
			if (smoAdMultiPlay != null) {
				smoAdMultiPlay.releaseSMOClientFromFjni();
			}
			screenListener.unregisterListener();
		}
	}


	
	public boolean onTouchEvent(MotionEvent event) {
		Log.e("UPLOAD", "onTouchEvent");
		if (dlna_window.isShowing()||mobile_webview_contain.isShown()||start_tag) {
			return true;
		}
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
			if (mMediaController.mPlayer != null
					&& !mMediaController.is_complete) {
				bufferTime = index;
				mMediaController.mPlayer.seekTo((int) index);
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
				if (mMediaController.mPlayer != null) {
					time = mMediaController.mPlayer.getCurrentPosition();
					seekTotal = mMediaController.mPlayer.getDuration();
				}
				if (show_current_time != null) {
					show_current_time.setText(StringUtils.generateTime(time));
				}
					scrolltag = true;
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
				imgvCloseAd();
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
	private boolean scrolltag = false;

	private void onSeekSlise(float percent) {
		if (scrolltag) {
			mVolumeBrightnessLayout.setVisibility(View.GONE);
			mobile_playview_showtime_ll.setVisibility(View.VISIBLE);
			index = (long) ((percent * 120000) + time);
			if (index > seekTotal)
				index = seekTotal;
			else if (index < 1000)
				index = 0;
			if (show_current_time != null) {
				show_current_time.setText(StringUtils.generateTime(index));
			}
		}
		scrolltag = !scrolltag;
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
			mobile_playview_showtime_ll.setVisibility(View.GONE);
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
		lp.width = findViewById(R.id.mobile_playview_operation_full)
				.getLayoutParams().width * index / mMaxVolume;
		mOperationPercent.setLayoutParams(lp);
		mMediaController.changeVolumeSeek();
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
			mobile_playview_showtime_ll.setVisibility(View.GONE);
		}
		WindowManager.LayoutParams lpa = getWindow().getAttributes();
		lpa.screenBrightness = mBrightness + percent;
		if (lpa.screenBrightness > 1.0f)
			lpa.screenBrightness = 1.0f;
		else if (lpa.screenBrightness < 0.01f)
			lpa.screenBrightness = 0.01f;
		getWindow().setAttributes(lpa);
		ViewGroup.LayoutParams lp = mOperationPercent.getLayoutParams();
		lp.width = (int) (findViewById(R.id.mobile_playview_operation_full)
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
			_mhandler.sendEmptyMessageDelayed(CHANGE_CHANNEL, 17000);
			needResume = true;
			// mLoadingView.bringToFront();
			// mLoadingView.setVisibility(View.VISIBLE);
			bufferTime = mVideoView.getCurrentPosition();
			mMediaController.setEnabled(false);
			if (!show_change_quantity && ADisOver) {
				load_show.setText("正在缓冲视频...");
				mLoadingprg.setVisibility(View.VISIBLE);
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

	// private int isPlayed = 0;

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		Log.e("UPLOAD", "onKeyDown:" + keyCode);
		if (keyCode == KeyEvent.KEYCODE_BACK
				&& event.getAction() == KeyEvent.ACTION_DOWN) {
			if (mobile_webview_contain.isShown()) {
				if (mobile_webview_id_WebView != null
						&& mobile_webview_id_WebView.canGoBack()) {
					mobile_webview_id_WebView.goBack();
				} else {
					mobile_webview_contain.startAnimation(hideAction);
					mobile_webview_contain.setVisibility(View.GONE);
					mobile_webview_id_WebView.clearCache(true);
					if (!ADisOver) {
						adview.start();
						adimgview.start();
					}
				}
				return true;
			}
			mMediaController.is_complete = false;
			mMediaController.closePlay();
			return true;
		}

		if (keyCode == KeyEvent.KEYCODE_ENTER) {
			togMediaControlsVisiblity();
			return true;
		}

		if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
			int mVolumeUp = mAudioManager
					.getStreamVolume(AudioManager.STREAM_MUSIC);
			int index = 1 + mVolumeUp;
			if (index > mMaxVolume)
				index = mMaxVolume;
			// 变更声音
			mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, index, 0);
			if (mMediaController != null) {
				mMediaController.changeVolumeSeek();
			}
			return true;
		}

		if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
			int mVolumeDown = mAudioManager
					.getStreamVolume(AudioManager.STREAM_MUSIC);
			int index = mVolumeDown - 1;
			if (index < 0)
				index = 0;
			// 变更声音
			mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, index, 0);
			if (mMediaController != null) {
				mMediaController.changeVolumeSeek();
			}
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	boolean isprepard = false;
	boolean is_start = false;
	private long totalTime;

	private  void onBuffPlay() {
		long currentTime = mVideoView.getCurrentPosition();
		Log.e("UI", "onBuffPlayd_current"+currentTime);
		if (mVideoView.isPlaying()) {
			if (isprepard) {
				// mMediaController.show();
				isprepard = false;
				mLoadingView.setVisibility(View.GONE);
			} else {
				if (currentTime != bufferTime && mLoadingprg.isShown()
						&& needResume) {// 对应有缓冲好的
					mMediaController.setEnabled(true);
					_mhandler.removeMessages(CHANGE_CHANNEL);
					_mhandler.removeMessages(CHANGE_CHANNEL_TIMO_OUT);
					if (abouttywindow.isShowing()) {
						abouttywindow.dismiss();
					}
					Log.e("mark", "onBuffPlay关闭缓冲");
					mLoadingView.setVisibility(View.GONE);
					needResume = false;
					seek_show_buff = false;
					mMediaController.mDragging = false;
					if (LOCK_Screen || !ADisOver) {
						mVideoView.pause();
					}
				} else if (currentTime == bufferTime && !mLoadingView.isShown()) {
					Log.e("mark", "onBuffPlay缓冲");
					_mhandler.removeMessages(CHANGE_CHANNEL);
					_mhandler.removeMessages(CHANGE_CHANNEL_TIMO_OUT);
					_mhandler.sendEmptyMessageDelayed(CHANGE_CHANNEL, 17000);
					_mhandler.sendEmptyMessageDelayed(CHANGE_CHANNEL_TIMO_OUT,
							100000);
					needResume = true;
					mMediaController.setEnabled(false);
					if (!show_change_quantity && ADisOver) {
						load_show.setText("正在缓冲视频...");
						mLoadingprg.setVisibility(View.VISIBLE);
						mLoadingView.setVisibility(View.VISIBLE);
					}
				}else{
					bufferTime = 0;
				}
			}
			bufferTime = currentTime;
			if (!ADisOver) {
				mVideoView.pause();
			}
		} else {
			if (mLoadingView.isShown()) {
				mVideoView.start();
			}
		}
	}

	public void onBufferingUpdate(MediaPlayer mp, int percent) {
		if (!live) {
			// play_buff_ll.setVisibility(View.GONE);
		}
	}

	public boolean onError(MediaPlayer mp, int what, int extra) {
		synchronized (MobilePlayActivity.this) {
			_mhandler.removeMessages(PLAY_TIME_OUT);
			if (mVideoView != null) {
				mVideoView.pause();
				mVideoView.stopPlayback();
			}
			if (LOCK_Screen) {
				is_start = false;
			}
			showError();
			_mhandler.removeMessages(CHANGE_CHANNEL_TIMO_OUT);
			_mhandler.removeMessages(VIDEOVIEW_BUFF_STATE);
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.simope.yzvideo.control.listener.ControllerPlayListener#changerChannel
	 * (java.lang.String)
	 */
	public void changerChannel(String which) {
		abouttywindow.dismiss();
		_mhandler.removeMessages(VIDEOVIEW_BUFF_STATE);
		imgvCloseAd();
		mLoadingView.setVisibility(View.VISIBLE);
		if (live) {
			mMediaController.mCurrent = 0;
			load_show.setText("直播!正在努力加载...");
		} else {
			show_change_quantity = true;
			if (mMediaController.is_complete) {
//				load_show.setText("正在从头加载视频...");
				mMediaController.is_complete = false;
				if(adStart!=null){
					startAd(adStart);
					return;
				}
			} else {
				if (show_change_quantity) {
					load_show.setText("正在切换视频清晰度...");
				} else {
					load_show.setText("正在缓冲视频...");
				}
			}
		}
		
		if (which.contains("index.hsm") || live) {
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
			showAlertDialog(MobilePlayActivity.this, "  错误提示", "无法播放该视频!",
					false);
		} else {
			if (isError) {
				if (ConnectionDetector.getConnectionDetector(getApplication())
						.isConnectingToInternet()) {
					if (parseError == 2) {
						showAlertDialog(MobilePlayActivity.this, "  网络异常",
								"请确认您的网络或数据连接是否正确!", false);
					} else {
						showAlertDialog(MobilePlayActivity.this, "  错误提示",
								"对不起,无法播放该视频!", false);
					}
				} else {
					showAlertDialog(MobilePlayActivity.this, "  网络连接异常",
							"请检查您的网络连接是否正常!", false);
				}
				isError = !isError;
			}
		}
	}

	public void showAlertDialog(Context context, String title, String message,
			Boolean status) {
		Log.e("mark", "title=" + title + ",message=" + message);
		if (!isFinishing()) {
			// play_buff_ll.setVisibility(View.GONE);
			adimgview.release();
			adview.release();
			mLoadingView.setVisibility(View.GONE);
			new AlertDialog.Builder(MobilePlayActivity.this)
					.setTitle(title)
					.setMessage(message)
					.setPositiveButton("OK",
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									_mhandler.removeMessages(NO_PLAYOBJECT);
									_mhandler.removeMessages(PLAY_TIME_OUT);
									_mhandler
											.removeMessages(CHANGE_CHANNEL_TIMO_OUT);
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
					showAlertDialog(MobilePlayActivity.this, "  错误提示",
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
		// _mhandler.removeMessages(BUFFRING_PLAY);
		_mhandler.removeMessages(CHANGE_CHANNEL);
		_mhandler.removeMessages(CHANGE_CHANNEL_TIMO_OUT);
		needResume = true;
		// mLoadingView.bringToFront();
		if (ADisOver) {
			mLoadingprg.setVisibility(View.VISIBLE);
			mLoadingView.setVisibility(View.VISIBLE);
		}
		if (!show_change_quantity && ADisOver) {
			load_show.setText("正在缓冲视频...");
		}
		if (no_net) {
			showAlertDialog(MobilePlayActivity.this, "  网络连接异常",
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
		seek_show_buff = false;

		parseError = 0;
		if (abouttywindow.isShowing()) {
			abouttywindow.dismiss();
		}
		mLoadingView.setVisibility(View.GONE);
		// play_buff_ll.setVisibility(View.GONE);
		// 对应onBuffer之后
		_mhandler.removeMessages(CHANGE_CHANNEL);
		_mhandler.removeMessages(CHANGE_CHANNEL_TIMO_OUT);
		needResume = false;
		if (TMPC_FIRST) {
			// mMediaController.show();
			TMPC_FIRST = false;
		}
		mMediaController.mDragging = false;
		if (LOCK_Screen || !ADisOver) {
			yzVideoView.pause();
		} else {
			yzVideoView.start();
		}
		mMediaController.setEnabled(true);
		// mMediaController.mHandler.sendEmptyMessageDelayed(mMediaController.CLICK,1500);
	}

	boolean TMPC_FIRST = false;
	private boolean isResumePlayed = true;

	public void onPrepared(MediaPlayer arg0) {
		synchronized (MobilePlayActivity.this) {
			_mhandler.removeMessages(PLAY_TIME_OUT);
			is_start = true;
			mMediaController.is_complete = false;
			if (mVideoView.getDuration() == 0) {
				live = true;
				mMediaController.setLive(live);
			}
			if (video.getPlayedTime() > 0 && isResumePlayed) {
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
				}
				isResumePlayed = false;
			}
			if(mVideoView.sfDestroy&&!live){
					mVideoView.seekTo((int) mMediaController.mCurrent);
					mVideoView.sfDestroy=false;
			}
					
			if (show_change_quantity) {
				show_change_quantity = false;
			}
			if (LOCK_Screen || !ADisOver) {
				mVideoView.pause();
			}
			parseError = 0;
			if (video.isNative_video()) {
				mMediaController.show();
			}
			isprepard = true;
			_mhandler.sendEmptyMessageDelayed(VIDEOVIEW_BUFF_STATE, 1000);
		}
	}

	private void tmpcIsprepare() {
		synchronized (MobilePlayActivity.this) {
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
						mMediaController.mCurrentTime.setText(StringUtils
								.generateTime(currentTime));
					}
				}
				isResumePlayed = false;
			}
		}
	}

	@Override
	public boolean onError(YzVideoView yzVideoView) {
		synchronized (MobilePlayActivity.this) {
			_mhandler.removeMessages(PLAY_TIME_OUT);
			if (yzVideoView != null) {
				yzVideoView.pause();
				yzVideoView.release(true);
			}
			if (LOCK_Screen) {
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


	/*返回一个当前退出的播放时间*/
	@Override
	public void canclePlayView(long currentTime,long totalTime) {
		abouttywindow.dismiss();
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
			mTotaltime = totalTime;
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
			aboutty_view.findViewById(R.id.about_player_dilg_btn)
					.setOnClickListener(mClickListener);
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

	private boolean seek_show_buff = false;

	@Override
	public void showBuffText(long newsposit) {
		if (!video.isNative_video() && !mMediaController.is_complete && !live) {
			imgvCloseAd();
			seek_show_buff = true;
			mMediaController.mCurrent = newsposit;
			_mhandler.removeMessages(CHANGE_CHANNEL);
			_mhandler.removeMessages(CHANGE_CHANNEL_TIMO_OUT);
			_mhandler.sendEmptyMessageDelayed(CHANGE_CHANNEL, 17000);
			_mhandler.sendEmptyMessageDelayed(CHANGE_CHANNEL_TIMO_OUT, 100000);
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
		if (LOCK_Screen || !ADisOver) {
			mVideoView.pause();
		}
	}

	public void setActTime(long current, long totaltime) {
		if(mMediaController!=null&&mMediaController.is_complete){
			video.setPlayedTime(0);
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

	private String[] deviceName;
	private IController mController;
	private Device mDevice;
	private TextView currenttv;
	private TextView totaltv;
	private SeekBar seek_bar;
	private ImageView pause_start;
	private ImageView volume_icon;
	private int mMediaDuration;
	private boolean mPaused;
	private int preOffVoice;
	private boolean mPlaying;
	private boolean slient;
	private static final String NOT_IMPLEMENTED = "NOT_IMPLEMENTED";
	private static final int AUTO_INCREASING = 8001;

	private void startAutoIncreasing() {
		_mhandler.sendEmptyMessageDelayed(AUTO_INCREASING, 1000);
	}

	private void stopAutoIncreasing() {
		_mhandler.removeMessages(AUTO_INCREASING);
	}

	private void setController(IController controller) {
		this.mController = controller;
	}

	private void changDevicetoStr(List<Device> l) {
		deviceName = new String[mDevices.size()];
		for (int i = 0; i < mDevices.size(); i++) {
			Device d = mDevices.get(i);
			deviceName[i] = d.getFriendlyName();
			Log.e("DLNAService", "" + d.getFriendlyName());
		}
	}

	public void showDlnaDial() {
		if (mMediaController.currentPath.contains("index.hsm")) {
			Toast.makeText(MobilePlayActivity.this, "不支持此格式", 0).show();
			return;
		}
		if (mDevices != null && mDevices.size() > 0) {
			changDevicetoStr(mDevices);
			Dialog schDia = new AlertDialog.Builder(this)
					.setTitle("请选择下面列表中的设备")
					.setItems(deviceName,
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dia,
										int which) {
									DLNAContainer.getInstance()
											.setSelectedDevice(
													mDevices.get(which));
									mDevice = DLNAContainer.getInstance()
											.getSelectedDevice();
									setController(new MultiPointController());
									if (mController == null || mDevice == null) {
										// usually can't reach here.
										Toast.makeText(getApplicationContext(),
												"Invalidate operation",
												Toast.LENGTH_SHORT).show();
										dia.dismiss();
										return;
									}
									initDlnaView(dlna_ctr_view);
									if (!dlna_window.isShowing()) {
										imgvCloseAd();
										int dlg_anim = R.style.DlgAnimSlide;
										dlna_window.setAnimationStyle(dlg_anim);
										dlna_window.showAtLocation(
												MobilePlayActivity.this
														.getWindow()
														.getDecorView(),
												Gravity.CENTER, 0, 0);
										mMediaController.mPlayer.pause();
										mMediaController.hide();
									}
									play(video.getMultiAddress()[0]);
								}
							}).create();
			schDia.setCanceledOnTouchOutside(true);
			schDia.show();
		}
	}

	private synchronized void stop() {
		stopAutoIncreasing();
		new Thread() {
			@Override
			public void run() {
				final boolean isSuccess = mController.stop(mDevice);
				runOnUiThread(new Runnable() {
					public void run() {
						showPlay(isSuccess);
					}
				});
			}
		}.start();
	}

	private void initDlnaView(View v) {
		ImageButton back = (ImageButton) v.findViewById(R.id.btn_return);
		back.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if (dlna_window != null && dlna_window.isShowing()) {
					dlna_window.dismiss();
					stop();
					if (live) {
						mMediaController.mPauseButton.performClick();
					} else {
						mMediaController.mPlayer.start();
					}
				}
			}
		});
		TextView live_show = (TextView) v.findViewById(R.id.live_show);
		if (live) {
			live_show.setVisibility(View.VISIBLE);
		}
		TextView renderer_name = (TextView) v.findViewById(R.id.renderer_name);
		renderer_name.setText(mDevice.getFriendlyName());
		TextView videotitle = (TextView) v.findViewById(R.id.videotitle);
		videotitle.setText(video.getTitle());
		currenttv = (TextView) v.findViewById(R.id.start_time);
		totaltv = (TextView) v.findViewById(R.id.end_time);
		seek_bar = (SeekBar) v.findViewById(R.id.seek_bar);
		pause_start = (ImageView) v.findViewById(R.id.av_play);
		pause_start.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (mPlaying) {
					pause();
					return;
				}
				if (mPaused) {
					String pausePosition = currenttv.getText().toString()
							.trim();
					goon(pausePosition);
				} else {
					play(video.getMultiAddress()[0]);
				}
			}
		});
		seek_bar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			public void onStopTrackingTouch(SeekBar seekBar) {
				startAutoIncreasing();
				int progress = seekBar.getProgress();
				seek(secToTime(progress));
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				stopAutoIncreasing();
			}

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				currenttv.setText(secToTime(progress));
				if (fromUser) {
					stopAutoIncreasing();
				}
			}
		});
		volume_icon = (ImageView) v.findViewById(R.id.volume_icon);

		volume_icon.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (slient) {
					onVoice();
				} else {
					offVoice();
				}
			}
		});
	}

	private void showVoice(boolean s) {
		slient = s;
		if (s) {
			volume_icon.setBackgroundDrawable(MobilePlayActivity.this
					.getResources().getDrawable(
							R.drawable.dlna_speaker_off_drawable));
		} else {
			volume_icon.setBackgroundDrawable(MobilePlayActivity.this
					.getResources().getDrawable(
							R.drawable.dlna_speaker_on_drawable));
		}
	}

	private synchronized void offVoice() {
		new Thread() {
			@Override
			public void run() {
				int cur = mController.getVoice(mDevice);
				boolean isSuccess = mController.setVoice(mDevice, 0);
				if (isSuccess) {
					preOffVoice = cur;
					runOnUiThread(new Runnable() {
						public void run() {
							showVoice(true);
						}
					});
				}
			}
		}.start();
	}

	private synchronized void onVoice() {
		new Thread() {
			@Override
			public void run() {
				if (preOffVoice == 0) {
					preOffVoice = 36;
				}
				boolean isSuccess = mController.setVoice(mDevice, preOffVoice);
				if (isSuccess) {
					preOffVoice = 0;
					runOnUiThread(new Runnable() {
						public void run() {
							showVoice(false);
						}
					});
				}
			}
		}.start();
	}

	private synchronized void seek(final String targetPosition) {
		new Thread() {
			@Override
			public void run() {
				boolean isSuccess = mController.seek(mDevice, targetPosition);
				if (isSuccess) {
					seek_bar.setProgress(getIntLength(targetPosition));
				}
				runOnUiThread(new Runnable() {
					public void run() {
						if (mPlaying) {
							startAutoIncreasing();
						} else {
							stopAutoIncreasing();
						}
					}
				});
			}
		}.start();
	}

	public static String secToTime(int time) {
		String timeStr = null;
		int hour = 0;
		int minute = 0;
		int second = 0;
		if (time <= 0)
			return "00:00:00";
		else {
			minute = time / 60;
			if (minute < 60) {
				second = time % 60;
				timeStr = "00:" + unitFormat(minute) + ":" + unitFormat(second);
			} else {
				hour = minute / 60;
				if (hour > 99)
					return "99:59:59";
				minute = minute % 60;
				second = time - hour * 3600 - minute * 60;
				timeStr = unitFormat(hour) + ":" + unitFormat(minute) + ":"
						+ unitFormat(second);
			}
		}
		return timeStr;
	}

	public static String unitFormat(int i) {
		String retStr = null;
		if (i >= 0 && i < 10)
			retStr = "0" + Integer.toString(i);
		else if (i >= 10 && i <= 60) {
			retStr = "" + i;
		} else {
			retStr = "00";
		}
		return retStr;
	}

	private synchronized void goon(final String pausePosition) {
		new Thread() {
			@Override
			public void run() {
				final boolean isSuccess = mController.goon(mDevice,
						pausePosition);
				if (isSuccess) {
					mPlaying = true;
				} else {
					mPlaying = false;
				}
				runOnUiThread(new Runnable() {
					public void run() {
						Log.e("MainActivity", "goon()" + isSuccess);
						showPlay(!isSuccess);
						if (isSuccess) {
							startAutoIncreasing();
						}
					}
				});
			}
		}.start();
	}

	private synchronized void play(final String path) {
		mPaused = false;
		showPlay(false);
		setTitle(path);
		stopAutoIncreasing();
		new Thread() {
			public void run() {
				final boolean isSuccess = mController.play(mDevice, path);
				runOnUiThread(new Runnable() {
					public void run() {
						Log.e("MainActivity", "play()" + isSuccess);
						if (isSuccess) {
							mPlaying = true;
							startAutoIncreasing();
						}
						showPlay(!isSuccess);
						getMediaDuration();
					}
				});

			};
		}.start();
	}

	private synchronized void pause() {
		stopAutoIncreasing();
		showPlay(true);
		new Thread() {
			public void run() {
				final boolean isSuccess = mController.pause(mDevice);
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						Log.e("MainActivity", "pause()" + isSuccess);
						showPlay(isSuccess);
						if (isSuccess) {
							mPaused = true;
							mPlaying = false;
						} else {
							startAutoIncreasing();
						}
					}
				});
			};
		}.start();
	}

	private void showPlay(boolean showPlay) {
		if (showPlay) {
			pause_start.setBackgroundDrawable(MobilePlayActivity.this
					.getResources().getDrawable(
							R.drawable.dlna_controller_play_drawable));
		} else {
			pause_start.setBackgroundDrawable(MobilePlayActivity.this
					.getResources().getDrawable(
							R.drawable.dlna_controller_pause_drawable));

		}
	}

	private synchronized void getMediaDuration() {
		new Thread() {
			@Override
			public void run() {
				final String mediaDuration = mController
						.getMediaDuration(mDevice);
				mMediaDuration = getIntLength(mediaDuration);

				runOnUiThread(new Runnable() {
					public void run() {
						if (TextUtils.isEmpty(mediaDuration)
								|| NOT_IMPLEMENTED.equals(mediaDuration)
								|| mMediaDuration <= 0) {
							_mhandler.postDelayed(new Runnable() {

								@Override
								public void run() {
									getMediaDuration();
								}
							}, 1000);
							return;
						}
						totaltv.setText(mediaDuration);
						seek_bar.setMax(mMediaDuration);
					}
				});
			}
		}.start();
	}

	private int getIntLength(String length) {
		if (TextUtils.isEmpty(length)) {
			return 0;
		}
		String[] split = length.split(":");
		int count = 0;
		try {
			if (split.length == 3) {
				count += (Integer.parseInt(split[0])) * 60 * 60;
				count += Integer.parseInt(split[1]) * 60;
				count += Integer.parseInt(split[2]);
			} else if (split.length == 2) {
				count += Integer.parseInt(split[0]) * 60;
				count += Integer.parseInt(split[1]);
			}
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
		return count;
	}

	private synchronized void getPositionInfo() {
		new Thread() {
			@Override
			public void run() {
				String position = mController.getPositionInfo(mDevice);
				if (TextUtils.isEmpty(position)
						|| NOT_IMPLEMENTED.equals(position)) {
					return;
				}
				final int currentPosition = getIntLength(position);
				if (currentPosition <= 0 || currentPosition > mMediaDuration) {
					return;
				}
				seek_bar.setProgress(getIntLength(position));
			}
		}.start();
	}

	public long getLocationLong(){
		if(video == null) return 0;
		return video.getPlayedTime();
	}
	
	public long getTotalTimeLong(){
		return mTotaltime;
	}
	
	public MobilePlayInterface mobilePlayInterface; 
		 
	public void setCallFunc(MobilePlayInterface callInterface) {  
		this.mobilePlayInterface = callInterface;  
	}  

}
