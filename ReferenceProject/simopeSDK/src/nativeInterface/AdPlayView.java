package nativeInterface;

import nativeInterface.SmoAdMultiPlay.ADDesc;
import nativeInterface.YzVideoView.YzOnCompletionListener;
import nativeInterface.YzVideoView.YzOnInfoListener;
import com.simope.yzvideo.R;
import com.simope.yzvideo.control.MediaController.MediaPlayerControl;
import com.simope.yzvideo.control.listener.ScreenListener;
import com.simope.yzvideo.control.listener.ScreenListener.ScreenStateListener;
import com.simope.yzvideo.util.StringUtils;
import android.content.Context;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnInfoListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class AdPlayView extends FrameLayout implements OnInfoListener,
		YzOnInfoListener, OnPreparedListener, OnCompletionListener,
		YzOnCompletionListener {

	public static final int AUTO_FRESH_TIME_REMAIN = 0x55;

	private static final int DEFAULT_BUTT_TIME = 3000;

	private Context mContext;
	private RelativeLayout parentContains;
	private ImageView bgBackground;
	private SimopeVideoView smvv;
	private YzVideoView yzvv;
	public MediaPlayerControl mPlayer;
	private ImageView adCancleBtn;
	private TextView timeRemaining;
	private Button adLinkBtn;
	private LinearLayout pgb;
	private String mPath;
	private Handler mHandler;
	private int start_time = 0;
	private int current_video = 0;
	private ScreenListener screenListener;
	private static final int SIMOPE_VIDEOVIEW = 1;
	private static final int YZ_VIDEOVIEW = 2;
	private boolean LOCK_Screen = false;
	private SendVideoStateListener sendVideoStateListener;
	private ImageView tuceng;
	private boolean isPaused=false;;
	private ADDesc ad;
	
	public AdPlayView(Context context, AttributeSet attrs) {
		super(context, attrs);
		LayoutInflater inflater = LayoutInflater.from(context);
		inflater.inflate(R.layout.ad_playview, this, true);
		mContext = context;
		mHandler = new Handler() {
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case AUTO_FRESH_TIME_REMAIN:
					timeRemaining.setVisibility(View.VISIBLE);
					adLinkBtn.setVisibility(View.VISIBLE);
					tuceng.setVisibility(View.GONE);
					int pos = setRemainTime();
					if (timeRemaining.isShown()) {
						msg = obtainMessage(AUTO_FRESH_TIME_REMAIN);
						sendMessageDelayed(msg, 1000 - (pos % 1000));
					}
					if (sendVideoStateListener != null) {
						sendVideoStateListener.sendVideoBegintoBuff();
					}
					break;
				}
			}
		};
	}

	protected void onFinishInflate() {
		super.onFinishInflate();
		parentContains = (RelativeLayout) findViewById(R.id.ad_contain);
		bgBackground = (ImageView) findViewById(R.id.ad_background);
		timeRemaining = (TextView) (findViewById(R.id.ad_playview_include)
				.findViewById(R.id.ad_time_remaining));
		pgb = (LinearLayout) (findViewById(R.id.ad_playview_include)
				.findViewById(R.id.ad_load_prg));
		pgb.setVisibility(View.VISIBLE);
		tuceng = (ImageView) findViewById(R.id.ad_tuceng);

		smvv = (SimopeVideoView) findViewById(R.id.ad_mp4_play);
		smvv.setOnPreparedListener(this);
		smvv.setOnCompletionListener(this);
		smvv.setOnInfoListener(this);
		yzvv = (YzVideoView) findViewById(R.id.ad_hsm_play);
		yzvv.setmYzOnCompletionListener(this);
		yzvv.setmYzOnInfoListener(this);

		adCancleBtn = (ImageView) (findViewById(R.id.ad_playview_include)
				.findViewById(R.id.ad_cancle_btn));
		adCancleBtn.setOnClickListener(ad_cancle_btn);
		adCancleBtn.setVisibility(View.VISIBLE);
		adLinkBtn = (Button) (findViewById(R.id.ad_playview_include)
				.findViewById(R.id.ad_link_btn));
		adLinkBtn.setOnClickListener(show_ad_detail);

		screenListener = new ScreenListener(mContext);
		screenListener.begin(new ScreenStateListener() {
			@Override
			public void onUserPresent() {
				Log.e("UI", "onUserPresent");
				if (parentContains.isShown()) {
					if(!isPaused){
						start();
					}
					if (timeRemaining.isShown()) {
						mHandler.sendEmptyMessage(AUTO_FRESH_TIME_REMAIN);
					}
				}
			}
			@Override
			public void onScreenOn() {
				Log.e("UI", "onScreenOn");
			}
			@Override
			public void onScreenOff() {
				if (parentContains.isShown()) {
					Log.e("UI", "onScreenOff");
					LOCK_Screen = true;
					mHandler.removeMessages(AUTO_FRESH_TIME_REMAIN);
				}
			}
		});
	}
	
	private OnClickListener ad_cancle_btn = new OnClickListener() {
		@Override
		public void onClick(View v) {
			if (sendVideoStateListener != null) {
				sendVideoStateListener.cancleAct();
			}
		}
	};
	
	private OnClickListener show_ad_detail = new OnClickListener() {
		@Override
		public void onClick(View v) {
			pause();
			if (sendVideoStateListener != null) {
				isPaused=true;
				sendVideoStateListener.showAdWebView(ad);
			}
		}
	};

	public void setVideoPath(ADDesc ad) {
		this.ad=ad;
		if (ad.multiPlayAddress.levelurl[0].endsWith(".hsm")) {
			if(ad.ad_show_mode!= SmoAdMultiPlay.AD_SHOW_FULL){
				yzvv.mVideoLayout=YzVideoView.VIDEO_LAYOUT_ORIGIN;
			}
			mPlayer = yzvv;
			current_video = YZ_VIDEOVIEW;
			yzvv.setVisibility(View.VISIBLE);
		} else {
			if(ad.ad_show_mode!= SmoAdMultiPlay.AD_SHOW_FULL){
				smvv.mVideoLayout=SimopeVideoView.VIDEO_LAYOUT_ORIGIN;
			}
			mPlayer = smvv;
			current_video = SIMOPE_VIDEOVIEW;
			smvv.setVisibility(View.VISIBLE);
		}
		adCancleBtn.setVisibility(View.VISIBLE);
		mPath = ad.multiPlayAddress.levelurl[0];
	}

	private int setRemainTime() {
		int currentPositon = mPlayer.getCurrentPosition();
		int totalTime = mPlayer.getDuration();
		if (currentPositon == 0 || !mPlayer.isPlaying()) {
			return 0;
		}
		timeRemaining.setText(StringUtils.generateADTime(totalTime
				- currentPositon));
		return currentPositon;
	}

	
	public void pause() {
		if(ad==null){
			return;
		}
		if (mPlayer != null) {
			start_time = mPlayer.getCurrentPosition();		
			mPlayer.pause();
			mHandler.removeMessages(AUTO_FRESH_TIME_REMAIN);
		}
	}

	public void start() {
		if(ad==null){
			return;
		}
		if (mPlayer != null) {
			mPlayer.start();
			if (timeRemaining.isShown()) {
				mHandler.sendEmptyMessage(AUTO_FRESH_TIME_REMAIN);
			}
			tuceng.setVisibility(View.GONE);
			isPaused=false;
		}
	}

	public void stop() {
		if(ad==null){
			return;
		}
		if (mPlayer != null) {
			mHandler.removeMessages(AUTO_FRESH_TIME_REMAIN);
			mPlayer.pause();
			// if(current_video==YZ_VIDEOVIEW&&!LOCK_Screen){
			// yzvv.release(true);
			// }
			tuceng.setVisibility(View.VISIBLE);
		}
	}

	public void startAdPlay() {
		if(ad==null){
			return;
		}
		if (current_video == SIMOPE_VIDEOVIEW) {
			smvv.setVideoPath(mPath);
			smvv.start();
		}
		if (current_video == YZ_VIDEOVIEW) {
			yzvv.start(mPath, 0, DEFAULT_BUTT_TIME);
		}
	}

	public void resume() {
		if(ad==null){
			return;
		}
		if (LOCK_Screen) {
			LOCK_Screen = false;
			tuceng.setVisibility(View.GONE);
			return;
		}
		if (mPlayer == null) {
			return;
		}
		if (yzvv.sfDestroy || smvv.sfDestroy) {
			yzvv.release(true);
			smvv.release(true);
			pgb.setVisibility(View.VISIBLE);
			if (yzvv.sfDestroy && current_video == YZ_VIDEOVIEW) {
				yzvv.start(mPath, start_time, DEFAULT_BUTT_TIME);
			}else{
				if (smvv.sfDestroy && current_video == SIMOPE_VIDEOVIEW) {
					smvv.setQuality(mPath, start_time);
				}
			}
		}
	}

	public void release() {
		if (mPlayer != null) {
			mPlayer.release(true);
		}
		screenListener.unregisterListener();
	}

	@Override
	public void onPrepared(MediaPlayer mp) {
		if (smvv.sfDestroy) {
			smvv.sfDestroy = false;
			smvv.seekTo(start_time);
		}
		if(isPaused){
			smvv.pause();
		}
		pgb.setVisibility(View.GONE);
		mHandler.sendEmptyMessage(AUTO_FRESH_TIME_REMAIN);
	}

	public void setBackGround(int Rsid) {
		bgBackground.setBackgroundDrawable(mContext.getResources().getDrawable(
				Rsid));
	}

	@Override
	public boolean onInfo(MediaPlayer mp, int what, int extra) {
		switch (what) {
		// =======================================================
		case MediaPlayer.MEDIA_INFO_BUFFERING_START:
			// 开始缓存，暂停播放
			pgb.setVisibility(View.VISIBLE);
			break;
		case MediaPlayer.MEDIA_INFO_BUFFERING_END:
			// 缓存完成，继续播放
			pgb.setVisibility(View.GONE);
			if(isPaused){
				smvv.pause();
			}
			break;
		case MediaPlayer.MEDIA_INFO_BAD_INTERLEAVING:
			break;
		}
		return false;
	}

	@Override
	public boolean onInfo(YzVideoView yzVideoView, int what) {
		if (this.isShown()) {
			switch (what) {
			case YzVideoView.TMPC_OUT_OF_MEMORY:
				break;
			case YzVideoView.TMPC_NO_SOURCE_DEMUX:
				break;
			case YzVideoView.TMPC_NO_PLAY_OBJECT:
				break;
			case YzVideoView.TMPC_TEMOBI_TIME_OUT:
				break;
			case YzVideoView.TMPC_NOTIFY_MEDIA_INFO:
				yzvv.sfDestroy = true;
				// 准备就绪
				break;
			case YzVideoView.TMPC_START_BUFFER_DATA:
				pgb.setVisibility(View.VISIBLE);
				break;
			case YzVideoView.TMPC_START_PLAY:
				if (yzvv.sfDestroy) {
					mHandler.sendEmptyMessage(AUTO_FRESH_TIME_REMAIN);
					yzvv.sfDestroy = false;
				}
				if(isPaused){
					yzVideoView.pause();
				}
				pgb.setVisibility(View.GONE);
				// 开始播放
				break;
			}
		}
		return false;
	}


	@Override
	public void onCompletion(YzVideoView yzVideoView) {
		mHandler.removeMessages(AUTO_FRESH_TIME_REMAIN);
		yzVideoView.release(true);
		setVideoViewHide();
		screenListener.unregisterListener();
	}

	@Override
	public void onCompletion(MediaPlayer mp) {
		mHandler.removeMessages(AUTO_FRESH_TIME_REMAIN);
		smvv.release(true);
		setVideoViewHide();
		screenListener.unregisterListener();
	}

	private void setVideoViewHide() {
		yzvv.mVideoLayout=YzVideoView.VIDEO_SCALE_FILL;
		smvv.mVideoLayout=SimopeVideoView.VIDEO_LAYOUT_ZOOM;
		smvv.sfDestroy=false;
		yzvv.sfDestroy=false;
		tuceng.setVisibility(View.VISIBLE);
		yzvv.setVisibility(View.GONE);
		smvv.setVisibility(View.GONE);
		pgb.setVisibility(View.GONE);
		adCancleBtn.setVisibility(View.GONE);
		this.setVisibility(View.GONE);
		start_time=0;
		ad=null;
		mHandler.removeMessages(AUTO_FRESH_TIME_REMAIN);
		if (sendVideoStateListener != null) {
			sendVideoStateListener.sendVideoStartPlay();
		}
	}

	public interface SendVideoStateListener {
		
		void sendVideoBegintoBuff();

		void sendVideoStartPlay();

		void cancleAct();
		
		void showAdWebView(ADDesc ad);
		
		void imgvCloseAd();
	}

	public void setSendVideoStateListener(
			SendVideoStateListener sendVideoStateListener) {
		this.sendVideoStateListener = sendVideoStateListener;
	}
}
