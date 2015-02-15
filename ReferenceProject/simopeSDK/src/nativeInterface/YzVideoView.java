package nativeInterface;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import com.simope.yzvideo.control.MediaController;
import com.simope.yzvideo.control.listener.ControllerPlayListener;





public class YzVideoView extends SurfaceView implements SurfaceHolder.Callback,
	MediaController.MediaPlayerControl {
	private static final String TAG = "YzVideoView";
	private int mDuration;
	private Context mContext;
	private Handler handler;
	public int mInstance;
	
	private MediaController mMediaController;
	private YzOnErrorListener errorListener;
	private YzOnInfoListener infoListener;
	private YzOnCompletionListener completionListener;
	private boolean mCanPause = true;
	private boolean mCanSeekBack = true;
	private boolean mCanSeekForward = true;
	private int current_state = STATE_IDLE;
	public int TMPC_STATE = 0;
	
	private static final int STATE_IDLE = 1000;
	private static final int STATE_PREPARING = 1001;
	private static final int STATE_PREPARED = 1002;
	private static final int STATE_PLAYING = 1003;
	private static final int STATE_PAUSED = 1004;
	private static final int STATE_ERROR = 1005;
	private static final int STATE_BUFF = 1006;
	private static final int STATE_PLAYBACK_COMPLETED = 1007;

	public static final int TMPC_OUT_OF_MEMORY = 0;
	public static final int TMPC_NO_SOURCE_DEMUX = 1;
	public static final int TMPC_MEDIA_SPEC_ERROR = 23;
	public static final int TMPC_NO_PLAY_OBJECT = 26;
	public static final int TMPC_TEMOBI_TIME_OUT = 28;
	public static final int TMPC_NOTIFY_MEDIA_INFO = 51;
	public static final int TMPC_START_BUFFER_DATA = 52;
	public static final int TMPC_PRE_START = 53;
	public static final int TMPC_PLAY_FINISH = 55;
	public static final int TMPC_START_PLAY = 54;
	public static final int BUFFER_TIME =0X111;

	private float mVideoAspectRatio = (float) 1.6667;
	private float mAspectRatio = 0;
	private int mSurfaceHeight = 0;
	private int mSurfaceWidth = 0;
	private int mVideoHeight = 0;
	private int mVideoWidth = 0;
	
	//默认全屏 by youtian@cmmobi.com
	public int mVideoLayout = VIDEO_SCALE_FIT;
	
	public static final int VIDEO_LAYOUT_ORIGIN = 0;
	public static final int VIDEO_SCALE_FIT = 1;
	public static final int VIDEO_SCALE_FILL = 2;
	public boolean sfDestroy = false;
	private boolean is_TimeOut = false;
	/*
	 * #define VIDEO_SCALE_ORIGINAL 0 原始大小 #define VIDEO_SCALE_FIT 1 适合屏幕保持宽高比
	 * #define VIDEO_SCALE_FILL 2 填充屏幕，图像可能会变形
	 */
	
	static {
		System.loadLibrary("tivc7dec");
		System.loadLibrary("FFMPEG");
		System.loadLibrary("rmh265dec");
		System.loadLibrary("hsmplayerjni");
	}

	public YzVideoView(Context context) {
		super(context);
		initVideoView(context);
	}

	public YzVideoView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public YzVideoView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initVideoView(context);
	}

	private void initVideoView(Context ctx) {
		handler = new Handler() {
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case TMPC_OUT_OF_MEMORY:
					TMPC_STATE = TMPC_OUT_OF_MEMORY;
					if (mOnInfoListener != null) {
						mOnInfoListener.onInfo(YzVideoView.this,
								TMPC_OUT_OF_MEMORY);
					}
					break;
				case TMPC_NO_SOURCE_DEMUX:
					TMPC_STATE = TMPC_NO_SOURCE_DEMUX;
					if (mOnInfoListener != null) {
						mOnInfoListener.onInfo(YzVideoView.this,
								TMPC_NO_SOURCE_DEMUX);
					}
					break;
				case TMPC_MEDIA_SPEC_ERROR:
					TMPC_STATE = TMPC_MEDIA_SPEC_ERROR;
					current_state = STATE_ERROR;
					if (mOnErrorListener != null) {
						mOnErrorListener.onError(YzVideoView.this);
					}
					break;
				case TMPC_NO_PLAY_OBJECT:
					TMPC_STATE = TMPC_NO_PLAY_OBJECT;
					if (mOnInfoListener != null) {
						mOnInfoListener.onInfo(YzVideoView.this,
								TMPC_NO_PLAY_OBJECT);
					}
					break;
				case TMPC_TEMOBI_TIME_OUT:
					if (!is_TimeOut) {
						TMPC_STATE = TMPC_TEMOBI_TIME_OUT;
						if (mOnInfoListener != null) {
							mOnInfoListener.onInfo(YzVideoView.this,
									TMPC_TEMOBI_TIME_OUT);
						}
						is_TimeOut = true;
					}
					break;
				case TMPC_NOTIFY_MEDIA_INFO:
					current_state= STATE_PREPARED;
					if (mOnInfoListener != null) {
						mOnInfoListener.onInfo(YzVideoView.this,
								TMPC_NOTIFY_MEDIA_INFO);
					}
					break;
				case TMPC_START_BUFFER_DATA:
					current_state= STATE_BUFF;
					if (mOnInfoListener != null) {
						mOnInfoListener.onInfo(YzVideoView.this,
								TMPC_START_BUFFER_DATA);
					}
					break;
				case TMPC_START_PLAY:
					TMPC_STATE = TMPC_START_PLAY;
					current_state = STATE_PLAYING;
					if (mOnInfoListener != null) {
						mOnInfoListener.onInfo(YzVideoView.this,
								TMPC_START_PLAY);
					}
					break;
				case TMPC_PLAY_FINISH:
					stop();
					if (mOnCompletionListener != null) {
						mOnCompletionListener.onCompletion(YzVideoView.this);
					}
					break;
				case TMPC_PRE_START:
					break;

				}
			}
		};
		nativeOnCreate();
		mContext = ctx;
		getHolder().addCallback(this);
		getHolder().setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		attachMediaController();
		if (ctx instanceof Activity)
			((Activity) ctx).setVolumeControlStream(AudioManager.STREAM_MUSIC);
	}

	ControllerPlayListener mControllerPlayListener;
	
    public void setControllerPlayListener(ControllerPlayListener controllerPlayListener) {
		this.mControllerPlayListener = controllerPlayListener;
	}


	public void start(String uri, int seekTime, int buffTime) {
			if (uri == null)
				return;
			Intent i = new Intent("com.android.music.musicservicecommand");
			i.putExtra("command", "pause");
			mContext.sendBroadcast(i);
			release(true);
			Log.e(TAG, "播放地址=" + uri);
			if(this.mControllerPlayListener!=null){
		       this.mControllerPlayListener.sendPlayTimeOutMessage();
		    }
		    attachMediaController();
			nativeTmpcStart(uri, seekTime, buffTime,mVideoLayout);
		}
	
	public void load(String uri, int seekTime, int buffTime) {
		if (uri == null)
			return;
		Intent i = new Intent("com.android.music.musicservicecommand");
		i.putExtra("command", "pause");
		mContext.sendBroadcast(i);
		release(true);
		Log.e(TAG, "播放地址=" + uri);
		if(this.mControllerPlayListener!=null){
	       this.mControllerPlayListener.sendPlayTimeOutMessage();
	    }
		nativeTmpcLoad(uri, seekTime, buffTime,mVideoLayout);
	}
	
	public void setMediaController(MediaController controller) {
//		if (mMediaController != null) {
//			mMediaController.hide();
//		} else {
			mMediaController = controller;
//		}
		attachMediaController();
	}

	private void attachMediaController() {
		if (mMediaController != null) {
			mMediaController.setMediaPlayer(this);
			View anchorView = this.getParent() instanceof View ? (View) this
					.getParent() : this;
			mMediaController.setAnchorView(anchorView);
			mMediaController.setEnabled(isPlaying());
		}
	}

	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
	
		int width = getDefaultSize(mVideoWidth, widthMeasureSpec);
		int height = getDefaultSize(mVideoHeight, heightMeasureSpec);
		if (mVideoWidth > 0 && mVideoHeight > 0) {

			int widthSpecMode = MeasureSpec.getMode(widthMeasureSpec);
			int widthSpecSize = MeasureSpec.getSize(widthMeasureSpec);
			int heightSpecMode = MeasureSpec.getMode(heightMeasureSpec);
			int heightSpecSize = MeasureSpec.getSize(heightMeasureSpec);

			if (widthSpecMode == MeasureSpec.EXACTLY
					&& heightSpecMode == MeasureSpec.EXACTLY) {
				// the size is fixed
				width = widthSpecSize;
				height = heightSpecSize;

				// for compatibility, we adjust size based on aspect ratio
				if (mVideoWidth * height < width * mVideoHeight) {
					// Log.i("@@@", "image too wide, correcting");
					width = height * mVideoWidth / mVideoHeight;
				} else if (mVideoWidth * height > width * mVideoHeight) {
					// Log.i("@@@", "image too tall, correcting");
					height = width * mVideoHeight / mVideoWidth;
				}
			} else if (widthSpecMode == MeasureSpec.EXACTLY) {
				// only the width is fixed, adjust the height to match aspect
				// ratio if possible
				width = widthSpecSize;
				height = width * mVideoHeight / mVideoWidth;
				if (heightSpecMode == MeasureSpec.AT_MOST
						&& height > heightSpecSize) {
					// couldn't match aspect ratio within the constraints
					height = heightSpecSize;
				}
			} else if (heightSpecMode == MeasureSpec.EXACTLY) {
				// only the height is fixed, adjust the width to match aspect
				// ratio if possible
				height = heightSpecSize;
				width = height * mVideoWidth / mVideoHeight;
				if (widthSpecMode == MeasureSpec.AT_MOST
						&& width > widthSpecSize) {
					// couldn't match aspect ratio within the constraints
					width = widthSpecSize;
				}
			} else {
				// neither the width nor the height are fixed, try to use actual
				// video size
				width = mVideoWidth;
				height = mVideoHeight;
				if (heightSpecMode == MeasureSpec.AT_MOST
						&& height > heightSpecSize) {
					// too tall, decrease both width and height
					height = heightSpecSize;
					width = height * mVideoWidth / mVideoHeight;
				}
				if (widthSpecMode == MeasureSpec.AT_MOST
						&& width > widthSpecSize) {
					// too wide, decrease both width and height
					width = widthSpecSize;
					height = width * mVideoHeight / mVideoWidth;
				}
			}
		} else {
			mVideoHeight=height;
			mVideoWidth=width;
		}
		setMeasuredDimension(width, height);
	}
	
	
	
	public void JNI_Callback(int i) {
		Message message = new Message();
		// message.obj=i;
		message.what = i;
		handler.sendMessage(message);
	}

	public native void nativeOnCreate();

	public native void nativeOnResume();

	public native void nativeOnPause();

	public native void nativeOnDelete();//必须在stop之后调用

	public native void nativeSetSurface(Surface surface);

	// player interface
	public native void nativeTmpcStart(String url, int start, int buftime,int videoscale); /*
																			 * 开始播放，
																			 * 开始时间和缓冲时间单位为毫秒
																			 * videoscale播放时尺寸
																			 */
	
	public native void nativeTmpcLoad(String url, int start, int buftime,int videoscale); /*加载视频 暂停*/
		
	public native void nativeTmpcPause(int wait);/* 暂停播放 */

	public native void nativeTmpcResume();/* 暂停后恢复播放 */

	public native void nativeTmpcStop();/* 停止播放，释放播放器资源 */

	public native int nativeTmpcGetBufTime();/* 时间单位毫秒 */

	public native int nativeTmpcGetPlayPos();/* 时间单位毫秒 */

	public native int nativeTmpcGetLength();/*
											 * 时间单位毫秒,
											 * 在收到TMPC_NOTIFY_MEDIA_INFO消息呼叫该函数获取文件总时长
											 */

	public native void nativeTmpcSeek(int ms);/* 时间单位毫秒 */

	/*
	 * #define VIDEO_SCALE_ORIGINAL 0 原始大小 #define VIDEO_SCALE_FIT 1 适合屏幕保持宽高比
	 * #define VIDEO_SCALE_FILL 2 填充屏幕，图像可能会变形
	 */
	public native int nativeTmpcVideoScale(int scale);/* 图像scale方式 */

	public void setVideoScale(int scale) {
		nativeTmpcVideoScale(scale);
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		Log.e(TAG, "YzVideoView_surfaceCreated"+holder.getSurface());
		nativeSetSurface(holder.getSurface());
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {	
		Log.e(TAG, "YzVideoView_surfaceChanged");
		holder.setFixedSize(width, height);
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		Log.e(TAG, "YzVideoView_surfaceDestroyed");
		sfDestroy = true;
		nativeSetSurface(null);
		release(true);
	}
	

	public interface YzOnCompletionListener {
		void onCompletion(YzVideoView yzVideoView);
	}

	public void setmYzOnCompletionListener(
			YzOnCompletionListener mYzOnCompletionListener) {
		this.completionListener = mYzOnCompletionListener;
	}

	public interface YzOnErrorListener {
		boolean onError(YzVideoView yzVideoView);
	}

	public void setmYzOnErrorListener(YzOnErrorListener mYzOnErrorListener) {
		this.errorListener = mYzOnErrorListener;
	}

	public interface YzOnInfoListener {
		boolean onInfo(YzVideoView yzVideoView, int what);
	}

	public void setmYzOnInfoListener(YzOnInfoListener mYzOnInfoListener) {
		this.infoListener = mYzOnInfoListener;
	}

	private YzOnInfoListener mOnInfoListener = new YzOnInfoListener() {

		@Override
		public boolean onInfo(YzVideoView yzVideoView, int what) {
			switch (what) {
			case TMPC_OUT_OF_MEMORY:
				Log.e(TAG, "TMPC_OUT_OF_MEMORY");
				break;
			case TMPC_NO_SOURCE_DEMUX:
				Log.e(TAG, "TMPC_NO_SOURCE_DEMUX");
				break;
			case TMPC_NO_PLAY_OBJECT:
				Log.e(TAG, "TMPC_NO_PLAY_OBJECT");
				break;
			case TMPC_TEMOBI_TIME_OUT:
				Log.e(TAG, "TMPC 播放超时");
				break;
			case TMPC_NOTIFY_MEDIA_INFO:
				Log.e(TAG, "TMPC_NOTIFY_MEDIA_INFO");
				break;
			case TMPC_START_BUFFER_DATA:
				Log.e(TAG, "TMPC_缓冲");
				mCanPause=false;
				break;
			case TMPC_START_PLAY:
				Log.e(TAG, "TMPC_播放");
				mCanPause=true;
				break;

			}
			if (infoListener != null) {
				infoListener.onInfo(yzVideoView, what);
			}
			return false;
		}
	};

	private YzOnErrorListener mOnErrorListener = new YzOnErrorListener() {

		@Override
		public boolean onError(YzVideoView yzVideoView) {
			if (errorListener != null) {
				errorListener.onError(yzVideoView);
			}
			return false;
		}
	};
	private YzOnCompletionListener mOnCompletionListener = new YzOnCompletionListener() {

		@Override
		public void onCompletion(YzVideoView yzVideoView) {
			if (completionListener != null) {
				completionListener.onCompletion(yzVideoView);
			}

		}
	};

	public void start() {
		if (isInPlaybackState()) {
			nativeTmpcResume();
			current_state = STATE_PLAYING;
			TMPC_STATE = TMPC_START_PLAY;
		}
	}

	
	public void pause() {
		if (isInPlaybackState()) {
			nativeTmpcPause(0);
			current_state = STATE_PAUSED;
		}
	}

	public void stop() {
		nativeTmpcStop();
		TMPC_STATE = TMPC_PLAY_FINISH;		
	}
	
	public int getDuration() {
		if (isInPlaybackState()) {
			if (mDuration > 0) {
				return mDuration;
			}
			mDuration = nativeTmpcGetLength();
			return mDuration;
		}
		mDuration = -1;
		return mDuration;
	}

	public int getCurrentPosition() {
		if (isInPlaybackState()) {
			return nativeTmpcGetPlayPos();
		}
		return 0;
	}

	public void seekTo(int pos) {
		if (isInPlaybackState()) {
			nativeTmpcSeek(pos);
		}
	}

	public int getBufTime() {
		if (TMPC_STATE == TMPC_START_PLAY) {
			return nativeTmpcGetBufTime();
		}
		return 0;
	}

	public boolean isPlaying() {
		return (isInPlaybackState() && TMPC_STATE == TMPC_START_PLAY && current_state != STATE_PAUSED);
	}

	public int getBufferPercentage() {
		double mPercent = ((double) (getBufTime() + getCurrentPosition()) / mDuration);
		if (mPercent > 0.99444) {
			mPercent = 1;
		}
		int m = (int) (100 * mPercent);
		return mDuration == 0 ? 0 : m;
	}

	public boolean canPause() {

		return mCanPause;
	}

	public boolean canSeekBackward() {

		return mCanSeekBack;
	}

	public boolean canSeekForward() {

		return mCanSeekForward;
	}

	public int getAnyDuration() {
		if (isInPlaybackState()) {
			if (mDuration > 0) {
				return mDuration;
			}
			mDuration = nativeTmpcGetLength();
			return mDuration;
		}
		mDuration = -1;
		return mDuration;
	}

	

	@Override
	public void setQuality(String path, int time) {
		if (isInPlaybackState()) {
			release(true);
		}
		start(path, time, BUFFER_TIME);
	}

	public void release(boolean cleartargetstate) {
		nativeTmpcStop();
		TMPC_STATE = 999;
		current_state = STATE_IDLE;
	}

	@Override
	public boolean isInPlaybackState() {
		return (current_state != STATE_ERROR && current_state != STATE_IDLE && current_state != STATE_PREPARING);
	}

}
