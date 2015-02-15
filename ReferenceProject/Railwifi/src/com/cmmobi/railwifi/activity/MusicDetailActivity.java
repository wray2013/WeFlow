package com.cmmobi.railwifi.activity;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo.State;
import android.os.Bundle;
import android.os.Message;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.text.TextUtils.TruncateAt;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.cmmobi.railwifi.R;
import com.cmmobi.railwifi.music.MusicPlayListener;
import com.cmmobi.railwifi.music.MusicService;
import com.cmmobi.railwifi.network.GsonResponseObject;
import com.cmmobi.railwifi.network.GsonResponseObject.MusicElem;
import com.cmmobi.railwifi.utils.DownloadApkUtils;
import com.cmmobi.railwifi.utils.ViewUtils;
import com.cmmobi.railwifi.view.MusicControllerView;
import com.nostra13.universalimageloader.api.BlurBitmapDisplayer;
import com.nostra13.universalimageloader.api.BlurBitmapDisplayer.BlurType;
import com.nostra13.universalimageloader.api.FadeInAnimateDisplayListener;
import com.nostra13.universalimageloader.api.MyImageLoader;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

public class MusicDetailActivity extends TitleRootActivity implements MusicPlayListener{
	private static final int HANDLER_FLAG_NEXT_PIC = 0x173972;
	private static final int HANDLER_FLAG_HIDE_CTRL = 0x173973;
	public static final String KEY_MUSIC_ID = "KEY_MUSIC_ID";
	public static final String KEY_FROM_WHERE = "KEY_FROM_WHERE";
	public static final String VALUE_FROM_HOMEPAGE = "HOME_PAGE";
	private static final String TAG = "MusicDetailActivity";
	private static final long HIDE_TIME = 3000;
	
	private RelativeLayout rl_ctrl;
	private MusicControllerView musicControllerView;
	private ImageView iv_pic;
	private MyImageLoader imageLoader;
	private DisplayImageOptions imageLoaderOptions;
	private ImageLoadingListener animateFirstListener;
	private boolean isProcessingCall = false;
	
	private static boolean preiousPlayState = false;
	
	 private BroadcastReceiver myReceiver = new BroadcastReceiver(){

		@Override
		public void onReceive(Context context, Intent intent) {
			System.out.println("action" + intent.getAction());
			// 如果是去电
			if (intent.getAction().equals(Intent.ACTION_NEW_OUTGOING_CALL)) {
				String phoneNumber = intent
						.getStringExtra(Intent.EXTRA_PHONE_NUMBER);
				Log.d(TAG, "call OUT:" + phoneNumber);
			} else {
				// 查了下android文档，貌似没有专门用于接收来电的action,所以，非去电即来电.
				// 如果我们想要监听电话的拨打状况，需要这么几步 :
				/*
				 * 第一：获取电话服务管理器TelephonyManager manager =
				 * this.getSystemService(TELEPHONY_SERVICE);
				 * 第二：通过TelephonyManager注册我们要监听的电话状态改变事件。manager.listen(new
				 * MyPhoneStateListener(),
				 * PhoneStateListener.LISTEN_CALL_STATE);这里的PhoneStateListener
				 * .LISTEN_CALL_STATE就是我们想要 监听的状态改变事件，初次之外，还有很多其他事件哦。 第三步：通过extends
				 * PhoneStateListener来定制自己的规则。将其对象传递给第二步作为参数。
				 * 第四步：这一步很重要，那就是给应用添加权限。android.permission.READ_PHONE_STATE
				 */
				TelephonyManager tm = (TelephonyManager) context
						.getSystemService(Service.TELEPHONY_SERVICE);
				tm.listen(listener, PhoneStateListener.LISTEN_CALL_STATE);
				// 设置一个监听器
			}
		}
	};

	PhoneStateListener listener = new PhoneStateListener() {
		@Override
		public void onCallStateChanged(int state, String incomingNumber) {
			// 注意，方法必须写在super方法后面，否则incomingNumber无法获取到值。
			super.onCallStateChanged(state, incomingNumber);
			switch (state) {
			case TelephonyManager.CALL_STATE_IDLE:
				System.out.println("挂断");
				resumePreiousState();
				isProcessingCall = false;
				break;
//			case TelephonyManager.CALL_STATE_OFFHOOK:
//				System.out.println("接听");
//				if(MusicService.getInstance().isPlaying()){
//					MusicService.getInstance().pausePlay();
//					interuptFromCall = true;
//				}
//				break;
			case TelephonyManager.CALL_STATE_RINGING:
				System.out.println("响铃:来电号码" + incomingNumber);
				if(isProcessingCall==false){
					preiousPlayState = MusicService.getInstance().isPlaying();
					if(MusicService.getInstance().isPlaying()){
						musicControllerView.pause();
					}
					isProcessingCall = true;
				}

				// 输出来电号码
				break;
			}
		}
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		title.setEllipsize(TruncateAt.MARQUEE);
		title.setMarqueeRepeatLimit(-1);
		title.setHorizontallyScrolling(true);
		title.setFocusableInTouchMode(true);
		hideRightButton();

		Intent intent = getIntent();
		String music_id = intent.getStringExtra(KEY_MUSIC_ID);
		
		rl_ctrl = (RelativeLayout)findViewById(R.id.rl_ctrl);
		musicControllerView = (MusicControllerView)findViewById(R.id.mcv_music);
		iv_pic = (ImageView) findViewById(R.id.iv_pic);
		
		int width = musicControllerView.setDrawableAndGetWidth(true);
		RelativeLayout.LayoutParams pm = (RelativeLayout.LayoutParams)musicControllerView.getLayoutParams();
		pm.width = width;
		musicControllerView.setLayoutParams(pm);
		if(VALUE_FROM_HOMEPAGE.equals(intent.getStringExtra(KEY_FROM_WHERE))){
			musicControllerView.setIsList(false);
		}else{
			musicControllerView.setIsList(true);
		}

		
		imageLoader = MyImageLoader.getInstance();

		animateFirstListener = new FadeInAnimateDisplayListener(2000);
		imageLoaderOptions = new DisplayImageOptions.Builder()
				.cacheInMemory(true)
				.cacheOnDisc()
				.imageScaleType(ImageScaleType.IN_SAMPLE_POWER_OF_2)
				.bitmapConfig(Bitmap.Config.RGB_565)
				.showImageOnFail(R.drawable.homepage_default)
				.showImageForEmptyUri(R.drawable.homepage_default)
				.showImageOnLoading(R.drawable.homepage_default)
//				.displayer(new RoundedBitmapDisplayer(12))// 圆角图片
//				.displayer(new BlurBitmapDisplayer(BlurType.Bottom, 0, 0.2))
				.displayer(new SimpleBitmapDisplayer())
				.build();
		
//		Intent intent = getIntent();
		MusicService.getInstance().setListener(this);
		MusicService.getInstance().stopPlay();
		MusicService.getInstance().setCurMusicId(music_id);
		musicControllerView.startPlay();
		
		handler.sendEmptyMessage(HANDLER_FLAG_NEXT_PIC);
		handler.sendEmptyMessageDelayed(HANDLER_FLAG_HIDE_CTRL, 5000);
		
		preiousPlayState = false;
		
		iv_pic.setOnClickListener(this);
		musicControllerView.setOnClickListener(this);
		
		IntentFilter filter = new IntentFilter();
		filter.addAction("android.intent.action.PHONE_STATE");  
//		filter.addAction("android.intent.action.NEW_OUTGOING_CALL"); 
		registerReceiver(myReceiver, filter);
	}
	
	protected void resumePreiousState() {
		// TODO Auto-generated method stub
		if(preiousPlayState){
			if(!MusicService.getInstance().isPlaying()){
				musicControllerView.startPlay();
			}
		}else{
			if(MusicService.getInstance().isPlaying()){
				musicControllerView.pause();
			}
		}
	}

	@Override
	protected void onPause(){
		super.onPause();

//		if(!userPause){
//			if(MusicService.getInstance().isPlaying()){
//				MusicService.getInstance().pausePlay();
//			}
//			userPause = true;
//		}else{
//			userPause = false;
//		}

	}
	
	@Override
	protected void onResume(){
		super.onResume();
//		if(userPause){
//			MusicService.getInstance().startPlay();
//			userPause = false;
//		}

	}

	@Override
	public boolean handleMessage(Message msg) {
		// TODO Auto-generated method stub
		switch(msg.what){
		case HANDLER_FLAG_NEXT_PIC:
			handler.removeMessages(HANDLER_FLAG_NEXT_PIC);
			setTitleText(MusicService.getInstance().curSongName());
			String url_pic = MusicService.getInstance().nextPic();
			Log.v(TAG, "musicDetail - url_pic:" + url_pic);
			imageLoader.displayImage(url_pic, iv_pic, imageLoaderOptions, animateFirstListener);
			handler.sendEmptyMessageDelayed(HANDLER_FLAG_NEXT_PIC, 10000);
			break;
		case HANDLER_FLAG_HIDE_CTRL:
			handler.removeMessages(HANDLER_FLAG_HIDE_CTRL);
			hideTitlebar();
			rl_ctrl.clearAnimation();
			Animation animation1 = AnimationUtils.loadAnimation(this,
					R.anim.option_leave_from_bottom);
			animation1.setAnimationListener(new AnimationListener() {
				@Override
				public void onAnimationEnd(Animation animation) {
					rl_ctrl.setVisibility(View.GONE);
				}

				@Override
				public void onAnimationStart(Animation animation) {
					// TODO Auto-generated method stub
					
				}

				@Override
				public void onAnimationRepeat(Animation animation) {
					// TODO Auto-generated method stub
					
				}
			});
			rl_ctrl.startAnimation(animation1);
			break;
		}
		return false;
	}

	@Override
	public int subContentViewId() {
		// TODO Auto-generated method stub
		return R.layout.activity_music_detail;
	}
	
	@Override
	public void onDestroy(){
		MusicService.getInstance().stopPlay();
		unregisterReceiver(myReceiver);
		ViewUtils.releasePicture(iv_pic);
		super.onDestroy();
	}

	@Override
	public boolean onPlaySong(GsonResponseObject.MusicElem music) {
		// TODO Auto-generated method stub
		handler.removeMessages(HANDLER_FLAG_NEXT_PIC);
		handler.sendEmptyMessage(HANDLER_FLAG_NEXT_PIC);
//		setTitleText(MusicService.getInstance().curSongName());
//		imageLoader.displayImage(MusicService.getInstance().nextPic(), iv_pic, imageLoaderOptions, animateFirstListener);
//		handler.sendEmptyMessageDelayed(HANDLER_FLAG_NEXT_PIC, 10000);
		
		String source = MusicService.getInstance().curSong().source_logo;
		if(source!=null && !"".equals(source)){
			showIvRightButton();
			setRightButtonUri(MusicService.getInstance().curSong().source_logo);
		}else{
			hideRightButton();
		}
		
		return true;
	}

	@Override
	public boolean onResumeSong(GsonResponseObject.MusicElem music) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean onPauseSong(GsonResponseObject.MusicElem music) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean onStopSong(GsonResponseObject.MusicElem music) {
		// TODO Auto-generated method stub
		if(getIntent() !=null && VALUE_FROM_HOMEPAGE.equals(getIntent().getStringExtra(KEY_FROM_WHERE))){
			musicControllerView.setButtonPlay();
		}
		return false;
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.mcv_music:
			handler.removeMessages(HANDLER_FLAG_HIDE_CTRL);
			handler.sendEmptyMessageDelayed(HANDLER_FLAG_HIDE_CTRL, HIDE_TIME);
			break;
		case R.id.iv_pic:
			if(rl_ctrl.getVisibility()==View.GONE){
				showTitlebar();
				rl_ctrl.clearAnimation();
				Animation animation1 = AnimationUtils.loadAnimation(this,
						R.anim.option_entry_from_bottom);
				animation1.setAnimationListener(new AnimationListener() {
					@Override
					public void onAnimationEnd(Animation animation) {
						rl_ctrl.setVisibility(View.VISIBLE);
					}

					@Override
					public void onAnimationStart(Animation animation) {
						// TODO Auto-generated method stub
						
					}

					@Override
					public void onAnimationRepeat(Animation animation) {
						// TODO Auto-generated method stub
						
					}
				});
				rl_ctrl.startAnimation(animation1);
			}
			handler.removeMessages(HANDLER_FLAG_HIDE_CTRL);
			handler.sendEmptyMessageDelayed(HANDLER_FLAG_HIDE_CTRL, HIDE_TIME);
			break;
		case R.id.btn_title_left:
			this.finish();
			break;
		case R.id.iv_title_right:
			String source_id = MusicService.getInstance().curSong().source_id;
			DownloadApkUtils downApk = new DownloadApkUtils(MusicDetailActivity.this, source_id);
			downApk.download();
			break;
		default:
			break;
		}
	}

	@Override
	public boolean onError(MusicElem music, int what, int extra) {
		// TODO Auto-generated method stub
		Log.v(TAG, "onError - music:" +music.name + ", what:" + what + ", extra:" + extra);
//		if(what==MediaPlayer.MEDIA_ERROR_SERVER_DIED){
//			findViewById(R.id.rl_empty).setVisibility(View.VISIBLE);
//			findViewById(R.id.rl_content).setVisibility(View.GONE);
//		}
		if(checkNetworkInfo()){
			return false; //has network, maybe only current one not work
		}else{
			findViewById(R.id.rl_empty).setVisibility(View.VISIBLE);
			findViewById(R.id.rl_content).setVisibility(View.GONE);
			return true; //no network, don't next play
		}

	}
	
	public boolean checkNetworkInfo() {
		ConnectivityManager conMan = (ConnectivityManager) this
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		State mobile = conMan.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)
				.getState();
		State wifi = conMan.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
				.getState();
		if (mobile == State.CONNECTED || mobile == State.CONNECTING)
			return true;
		if (wifi == State.CONNECTED || wifi == State.CONNECTING)
			return true;
		return false;

	}

}
