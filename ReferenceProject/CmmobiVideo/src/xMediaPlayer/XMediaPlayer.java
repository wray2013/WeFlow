package xMediaPlayer;

import java.text.DecimalFormat;

import android.content.Context;
import android.media.AudioFormat;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import effect.EffectType;
import effect.Effects;
import effect.GLLayer;
import effect.transCode;
import effect.transCode.OnScheduleListener;

/**
 * 媒体播放器，暂时只支持本地播放。
 * 
 * @author xpg-2007
 * 
 */

public class XMediaPlayer {

	static {
		System.loadLibrary("effect_Effects");
	}

	private static final String tag = "XMediaPlayer";

	// the player status.****begin.
	private static final int STATUS_UNKNOW = 0;
	private static final int STATUS_OPENED = 1;
	private static final int STATUS_PALYING = 2;
	private static final int STATUS_STOP = 3;
	private static final int STATUS_PAUSE = 4;
	// the player status.****end.

	// stream type ********begin.
	private static final int STREAM_TYPE_UNKNOW = 0;
	private static final int STREAM_TYPE_AUDIO = 1;
	private static final int STREAM_TYPE_VIDEO = 2;
	// stream type ********end.

	// audio SampBit *******begin.
	private static final int AUDIO_SAMPBIT_PCM_16BIT = AudioFormat.ENCODING_PCM_16BIT;
	private static final int AUDIO_SAMPBIT_PCM_8BIT = AudioFormat.ENCODING_PCM_8BIT;
	// audio SampBit *******end.

	// audio channals ********begin.
	private static final int AUDIO_OUT_MONO = AudioFormat.CHANNEL_OUT_MONO;
	private static final int AUDIO_OUT_STEREO = AudioFormat.CHANNEL_OUT_STEREO;
	// audio channals ********end.

	// audio buffer numbers.********begin.
	private static final int AUDIO_MAX_BUFFER = 8;
	// audio buffer numbers.********end.

	// media handler type.**********begin.
	private static final int MEDIA_MSG_ID_SHOWVIDEO = 1;
	private static final int MEDIA_MSG_ID_FILEEND = 2;
	private static final int MEDIA_MSG_ID_CHANGEBTN = 3;
	// media handler type.**********end.

	// audio play.************begin.
	private XAudioPlayer mAudioPlayer = null;
	private Boolean mCloseHadler = false;
	// audio play.************end.

	// native change.*********begin.
	private long mFileId = 0;
	private int mWidth = 0;//视频的宽高;
	private int mHeight = 0;

	private int mChannals = 0;
	private int mAFmt = 0;
	private int mSampleRate = 0;

	private int mCurrStreamType;
	private double mvCurrTimestamp;
	private double maCurrTimestamp;

	private double mvLastTimestamp;
	private double maLastTimestamp;

	private double mTotalTime; // 总时间
	// native change.*********end.
	
	private GLLayer mGlView;
	private int mPlayStatus = STATUS_UNKNOW;

	// 先定义一个存放数据的数组。
	private byte[] mvData = null;
	private byte[] maData = null;
	private int maOffset = 0;
	private int malenght = 0;
	private Boolean mvReady = false;
	private Boolean maReady = false;
	private long maPlayTime = 0;
	private long maBuffsize = 16;
	private long mvPlayTime = 0;
	
	// 布局相关变量
	private int mSetSize = 0;
	private Context mContext = null;
	private String mfileName;
	private Boolean mIsEof = false;
	private Boolean mUpdataSeek = true;
	
	// manage effect list and make file. have insert point paramer the c.
	public Effects mEffectsManager = null;
	private transCode mtransCode = null;
	
	private OnXMediaPlayerStateChangedListener listener;
	private int percent;
	private String progressTimeText;
	private String totalTimeText;
	
	public static interface OnXMediaPlayerStateChangedListener {
		public void OnPlayStateChanged(XMediaPlayer player);
	}
	
	
	
	public XMediaPlayer(Context context) {
		mContext = context;
		// show video
		mWidth = EffectType.VIDEO_WIDTH;
		mHeight = EffectType.VIDEO_HEIGHT;
		mGlView = new GLLayer(context, mWidth, mHeight);
		mGlView.changePlayCoords();

		// play audio
		mAudioPlayer = new XAudioPlayer(mNotify_handler);

		// about media effects.
		mEffectsManager = new Effects();
		Log.i(tag, "mEffectsManager.mList=" + mEffectsManager.mList);

		// transcode.
		mtransCode = new transCode();
	}

	public GLLayer getGLLayer() {
		return mGlView;
	}

	// about media play control.*********begin.
	public boolean open(String filename) {
		boolean ok = true;
		
		try {
			synchronized (this) {
				if (mPlayStatus != STATUS_UNKNOW) {
					Log.i(tag, "this file has opened.");
					return false;
				}

				Log.i(tag, "open:" + filename);
				int nret = native_open(filename);
				Log.i(tag, "mFileId=" + mFileId + " mVideoWidth = " + mWidth + " mVideoHeight = " + mHeight + "\r\nmChannals=" + mChannals + " mAFmt=" + mAFmt
						+ " mSampleRate=" + mSampleRate);

				if (nret != 0) {
					Log.i(tag, "native_open error.");
					return false;
				}

				mfileName = filename;
				mPlayStatus = STATUS_OPENED;
				mGlView.resetVideoSize(mWidth, mHeight);

				// init param.
				mvReady = false;
				maReady = false;
				mvLastTimestamp = 0;
				maLastTimestamp = 0;
				maPlayTime = 0;
				maBuffsize = 0;

				// open audio player.********begin.
				int nsize = mAudioPlayer.open(mSampleRate, mChannals, mAFmt);
				mAudioPlayer.createPlayerBuffers(AUDIO_MAX_BUFFER, nsize);
				int nnum = mAudioPlayer.getBufferNum();
				for (int i = 0; i < nnum; i++) {
					fullDataToAudioPlayer(i);
				}

				mCloseHadler = false;
				mIsEof = false;
			}
		} catch (Exception e) {
			ok = false;
		}
		
		return ok;
	}
	
	public void play() {
		synchronized (this) {
			if (isOpen() || isPause()) {
				// 先让audioplay开始工作
				if (mAudioPlayer != null && mAudioPlayer.getAudioPlayStatus() == XAudioPlayer.A_STATUS_OPENED) {
					mAudioPlayer.start();
					mPlayStatus = STATUS_PALYING;
				} else if (mAudioPlayer != null && mAudioPlayer.getAudioPlayStatus() == XAudioPlayer.A_STATUS_PAUSE) {
					mAudioPlayer.restart();
					mPlayStatus = STATUS_PALYING;
				}

				mNotify_media.sendEmptyMessage(MEDIA_MSG_ID_CHANGEBTN);
			}
		}
	}

	public void pause() {
		synchronized (this) {
			if (mAudioPlayer != null && mAudioPlayer.getAudioPlayStatus() == XAudioPlayer.A_STATUS_PALYING) {
				mAudioPlayer.pause();
				mPlayStatus = STATUS_PAUSE;
			}
			mNotify_media.sendEmptyMessage(MEDIA_MSG_ID_CHANGEBTN);
		}
	}

	public void seek(SeekBar seekBar) {
		if (isPlaying() || isOpen() || isPause()) {
			
			float offset = (float) mTotalTime * (((float) seekBar.getProgress()) / seekBar.getMax());
			
			synchronized (this) {
				native_seek(offset);
			}
		}
	}
	
	public void onDragSeekBar(SeekBar seekBar) {
		float offset = (float) mTotalTime * (((float) seekBar.getProgress()) / seekBar.getMax());
		ChangePlaySeek(offset);
	}

	public void stop() {
		synchronized (this) {
			if (mAudioPlayer != null) {
				mAudioPlayer.stop();
			}

			mCloseHadler = true;
			mNotify_media.sendEmptyMessage(MEDIA_MSG_ID_CHANGEBTN);
			native_close();
		}
	}

	// the function to cleaned the memery space by c languge.
	public void Release() {
		if (mPlayStatus != STATUS_UNKNOW && mPlayStatus != STATUS_STOP) {
			stop();
		}

		if (mtransCode != null && mtransCode.IsWriteing()) {
			mtransCode.StopSave();
		}

		if (mEffectsManager != null)
			mEffectsManager.Release();
	}

	public Boolean isPlaying() {
		return (mPlayStatus == STATUS_PALYING) ? true : false;
	}

	public Boolean isOpen() {
		return (mPlayStatus != STATUS_UNKNOW) ? true : false;
	}

	public Boolean isPause() {
		return (mPlayStatus == STATUS_PAUSE) ? true : false;
	}

	private void fullDataToAudioPlayer(int index) {
		if (mAudioPlayer == null)
			return;
		int nsize = mAudioPlayer.getBufferSize();
		int tsize = 0;
		byte[] tBuffer = new byte[nsize];

		for (; tsize < nsize && !mCloseHadler;) {
			int copylen = 0;
			if (malenght <= 0) {
				maData = native_getaudiopacket();
				if (maData == null) {
					// Log.d(tag, "maData is null.");
					// Log.d(tag, "mEnd=" + mEnd);
					if (mIsEof)
						break;

					if (native_eof() > 0) {
						mIsEof = true;
						mNotify_media.sendEmptyMessage(MEDIA_MSG_ID_FILEEND);
						break;
					}

					try {
						Thread.sleep(5);
					} catch (Exception e) {
						e.printStackTrace();
					}
					continue;
				}

				mNotify_media.sendEmptyMessage(MEDIA_MSG_ID_SHOWVIDEO);
				maOffset = 0;
				malenght = maData.length;
				// Log.d(tag, "maData.length=" + maData.length);
			}

			if (malenght > 0) {
				copylen = nsize - tsize >= malenght ? malenght : nsize - tsize;
				System.arraycopy(maData, maOffset, tBuffer, tsize, copylen);
				tsize += copylen;

				malenght -= copylen;
				maOffset += copylen;
			} else {
				break;
			}
		}

		// Log.d(tag, "tsize = " + tsize + " || nsize = " + nsize);
		mAudioPlayer.fullBufferWithIndex(tBuffer, index, maCurrTimestamp);

	}

	private Handler mNotify_handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (msg.what == XAudioPlayer.A_MSG_ID_PLAY_DONE) {
				XAudioPlayer.PlayerBuffer tobj = (XAudioPlayer.PlayerBuffer) (msg.obj);
				fullDataToAudioPlayer(tobj.mIndex);
			}
			super.handleMessage(msg);
		}
	};

	// the handler to show video use opengl.

	private void ChangePlaySeek(double timestamp) {

		int hours, minute, second;
		int thours, tminute, tsecond;
		int temp;
		
		hours = (int) (timestamp / 3600);
		temp = (int) (timestamp % 3600);
		minute = (int) (temp / 60);
		second = (int) (temp % 60);
		
		thours = (int) (mTotalTime / 3600);
		temp = (int) (mTotalTime % 3600);
		tminute = (int) (temp / 60);
		tsecond = (int) (temp % 60);

		DecimalFormat df = new DecimalFormat("00");

		progressTimeText = df.format(hours) + ":" + df.format(minute) + ":" + df.format(second);
		totalTimeText = df.format(thours) + ":" + df.format(tminute) + ":" + df.format(tsecond);
		percent = (int) ((timestamp / mTotalTime) * 100);

		notifyXMediaPlayerStateChanged();
	}

	

	public String getProgressTimeText() {
		return progressTimeText;
	}

	public String getTotalTimeText() {
		return totalTimeText;
	}

	public int getPercent() {
		return percent;
	}

	public void setOnXMediaPlayerStateChangedListener(OnXMediaPlayerStateChangedListener listener) {
		this.listener = listener;
	}

	private void notifyXMediaPlayerStateChanged() {
		if (listener != null) {
			listener.OnPlayStateChanged(this);
		}
	}

	private Handler mNotify_media = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (msg.what == MEDIA_MSG_ID_SHOWVIDEO) {
				if (mvData == null || !mvReady) {
					mvData = native_getvideopacket();
					if (mvData != null)
						mvReady = true;
				}

				if (mAudioPlayer.maCurrTimestamp >= mvCurrTimestamp) {
					if (mvReady && mGlView != null && mvData != null && mGlView.glCameraFrame.length >= mvData.length) {
						mEffectsManager.native_ProcessVideoRGBA(mvData, mWidth, mHeight, mGlView.glCameraFrame);
						// System.arraycopy(mvData, 0, mGlView.glCameraFrame, 0,
						// mvData.length);
						mGlView.requestRender();
						mvReady = false;
						ChangePlaySeek(mAudioPlayer.maCurrTimestamp);
					}
				}
			} else if (msg.what == MEDIA_MSG_ID_FILEEND) {
				stop();
				try {
					open(mfileName);
				} catch (Exception e) {
					e.printStackTrace();
				}
				Log.i(tag, "the file is end.****");
			} else if (msg.what == MEDIA_MSG_ID_CHANGEBTN) {
				notifyXMediaPlayerStateChanged();
			}
			super.handleMessage(msg);
		}
	};

	// transcode media file.***********begin.
	public void StartTransCode(String input, String output, OnScheduleListener listener) {
		Log.i(tag, input + "--->>>>");
		Log.i(tag, output);

		if (mtransCode != null) {
			mtransCode.StartSave(input, output, mEffectsManager, listener);
		}
	}

	public void StopTransCode() {
		if (mtransCode != null) {
			mtransCode.StopSave();
		}
	}

	// transcode media file.***********end.

	// native function.
	public native int native_open(String filepath);

	public native int native_seek(float pos);

	public native int native_close();

	public native byte[] native_getnextpaket();

	public native byte[] native_getvideopacket();

	public native byte[] native_getaudiopacket();

	public native int native_eof();

	// native test function.
	public native int native_test01(int v0);

}
