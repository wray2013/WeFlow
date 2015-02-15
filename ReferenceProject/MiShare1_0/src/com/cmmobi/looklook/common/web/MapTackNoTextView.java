package com.cmmobi.looklook.common.web;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import android.content.Context;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import cn.zipper.framwork.io.network.ZHttp2;
import cn.zipper.framwork.io.network.ZHttpResponse;

import com.cmmobi.looklook.MainApplication;
import com.cmmobi.looklook.R;
import com.cmmobi.looklook.common.constant.Constant;
import com.cmmobi.looklook.common.utils.MD5;
import com.cmmobi.looklook.info.profile.AccountInfo;
import com.cmmobi.looklook.info.profile.ActiveAccount;
import com.cmmobi.looklook.info.profile.MediaValue;

public class MapTackNoTextView extends LinearLayout implements OnCompletionListener,
		OnErrorListener {

	private static final String TAG = "TackView";
	private String localSounds = "";

	public MapTackNoTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context, R.layout.del_map_tackview);

	}

	public MapTackNoTextView(Context context) {
		super(context);
		init(context, R.layout.del_map_tackview);
	}

	private View llContainer;
	private ImageView ivBiezhen;
	private TextView tvBiezhen;
	private LayoutInflater inflater;

	private void init(Context context, int layoutId) {
		inflater = LayoutInflater.from(context);
		View v = inflater.inflate(layoutId, null);
		llContainer = v.findViewById(R.id.map_sound_ll);
		ivBiezhen = (ImageView) v.findViewById(R.id.map_sound);// 别针中的声音图片
		tvBiezhen = (TextView) v.findViewById(R.id.map_sound_text);// 别针中的文字
		tvBiezhen.setVisibility(View.GONE);
		addView(v);
	}

	/**
	 * 设置背景图 如果resID=0 背景图为空
	 */
	public void setBackground(int resID) {
		if (0 == resID) {
			llContainer.setBackgroundDrawable(null);
		} else {
			llContainer.setBackgroundResource(resID);
		}
	}

	/**
	 * 设置录音播放总时长
	 */
	public void setSoundText(String soundText) {
		Log.d(TAG, "playtime=" + soundText);
		tvBiezhen.setText(soundText);
	}

	public void setLocalAudio(String audioPath) {
		localSounds = audioPath;
		if (mp != null) {
			mp.release();
			mp = null;
			if (tackView != null)
				tackView.stop();
		}
		if (isPlaying) {
			stop();
		} else {
			try {
				mp = new MediaPlayer();
				mp.setOnCompletionListener(this);
				mp.setOnErrorListener(this);
				mp.setDataSource(audioPath);
				mp.prepare();
				mp.setOnPreparedListener(new OnPreparedListener() {
					@Override
					public void onPrepared(MediaPlayer arg0) {
						// TODO Auto-generated method stub
						int duration = mp.getDuration();
						if (duration < 1000) {
							duration = 1000;
						}
						tvBiezhen.setText("" + duration / 1000 + "″");
						Log.d(TAG, "duration = " + duration);
					}
				});

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
	}

	/**
	 * url 录音的网络地址 playtime
	 */
	public void setAudio(String url, int belong) {
		Log.d(TAG, "url=" + url);
		if (mp != null) {
			mp.release();
			mp = null;
			if (tackView != null)
				tackView.stop();
		}
		if (isPlaying) {
			stop();
		} else {
			getAudio(url, belong);
		}
	}

	private void getAudio(String url, int belong) {
		if (null == url || 0 == url.length()) {
			Log.e(TAG, "url is null");
			return;
		}
		String uid = ActiveAccount
				.getInstance(MainApplication.getAppInstance()).getUID();
		MediaValue mv = AccountInfo.getInstance(uid).mediamapping.getMedia(uid,
				url);
		if (!MediaValue.checkMediaAvailable(mv, belong)) {
			// not hit:
			Log.e(TAG, "not hit, try to loadNetPicture");
			loadAudio(url, belong);
		} else {
			// hit:
			Log.e(TAG, "hit, try to load from local cache");
			playAudio(Environment.getExternalStorageDirectory() + mv.localpath);
		}
	}

	// 下载录音
	private void loadAudio(String url, int belong) {
		new DownloadTask().execute(url, belong);
	}

	private static boolean isPlaying;
	private static MapTackNoTextView tackView;
	private static MediaPlayer mp;

	// 播放录音
	public void playAudio(String audioPath) {
		if (isPlaying)
			return;
		isPlaying = true;
		tackView = this;
		myHandler.post(timeTask);
		try {
			// audioPath="/storage/sdcard0/looklook/e135f0650bccd044ed0a1450f9d6d445b456/audio/taisha";
			mp = new MediaPlayer();
			mp.setOnCompletionListener(this);
			mp.setOnErrorListener(this);
			mp.setDataSource(audioPath);
			mp.prepare();
			mp.start();
		} catch (Exception e) {
			stop();
			e.printStackTrace();
		}
	}

	@Override
	public boolean onError(MediaPlayer mp, int what, int extra) {
		Log.d(TAG, "onError");
		stop();
		return false;
	}

	@Override
	public void onCompletion(MediaPlayer mp) {
		Log.d(TAG, "onCompletion");
		stop();
	}

	private static Handler myHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case HANDLER_UPDATE_AUDIO_ICON:
				int resId = (Integer) msg.obj;
				tackView.ivBiezhen.setImageResource(resId);
				break;

			default:
				break;
			}
			super.handleMessage(msg);
		}

	};

	private void stop() {
		isPlaying = false;
		if (mp != null) {
			mp.release();
			mp = null;
		}
		ivBiezhen.setImageResource(SOUND_ICONS[2]);
		myHandler.removeCallbacks(timeTask, null);
	}

	private static final int HANDLER_UPDATE_AUDIO_ICON = 0x110011;
	private static final int LOOP_PERIOD = 1000;

	private int y = -1;//
	private static Runnable timeTask = new Runnable() {

		@Override
		public void run() {
			if (tackView.index >= tackView.SOUND_ICONS.length)
				tackView.index = 0;
			int[] location = new int[2];
			tackView.getLocationInWindow(location);
			if (-1 == tackView.y) {
				tackView.y = location[1];
			}
			if (tackView.y != location[1]) {
				tackView.y = location[1];
			} else {
				myHandler.obtainMessage(HANDLER_UPDATE_AUDIO_ICON,
						tackView.SOUND_ICONS[tackView.index]).sendToTarget();
			}
			myHandler.postDelayed(this, LOOP_PERIOD);
			tackView.index++;
		}
	};

	int index = 0;
	private int[] SOUND_ICONS = { R.drawable.del_shengyin1,
			R.drawable.del_shengyin2, R.drawable.del_shengyin3};

	public void setSoundIcons(int[] icons) {
		SOUND_ICONS = icons;
	}

	public void setSoundIconBackground(int resId) {
		ivBiezhen.setBackgroundResource(resId);
	}

	class DownloadTask extends AsyncTask<Object, Void, MediaValue> {
		String url;
		int belong;

		@Override
		protected MediaValue doInBackground(Object... param) {
			String audioPath = null;
			MediaValue result = null;
			try {
				url = (String) param[0];
				belong = (Integer) param[1];
				ZHttp2 http2 = new ZHttp2();
				url = url.substring(url.indexOf("http"), url.length());
				Log.e(TAG, "url=" + url);
				ZHttpResponse httpResponse = http2.get(url);
				InputStream inputStream = httpResponse.getInputStream();
				String uid = ActiveAccount.getInstance(
						MainApplication.getAppInstance()).getUID();
				String Key = MD5.encode((uid + url).getBytes());
				audioPath = Environment.getExternalStorageDirectory()
						+ Constant.SD_STORAGE_ROOT + "/" + uid + "/audio/"
						+ Key;
				File audioFile = new File(
						Environment.getExternalStorageDirectory() + audioPath);
				if (!audioFile.getParentFile().exists()) {
					audioFile.getParentFile().mkdirs();
				}
				FileOutputStream os = new FileOutputStream(audioFile);
				byte buffer[] = new byte[1024];
				Log.d(TAG, "available=" + inputStream.available());
				long size = inputStream.available();
				while (inputStream.read(buffer) != -1) {
					Log.d(TAG, "writing");
					os.write(buffer);
				}
				os.flush();

				result = new MediaValue();
				result.UID = uid;
				result.Belong = belong;
				result.Direction = 1;
				result.MediaType = 3;
				result.url = url;
				result.localpath = audioPath;
				result.realSize = size;
				result.Sync = 1;
				result.SyncSize = size;
				result.totalSize = size;
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			return result;
		}

		@Override
		protected void onPostExecute(MediaValue result) {
			if (!MediaValue.checkMediaAvailable(result, 3)) {
				Log.e(TAG, "checkMediaAvailable is false");
				return;
			}
			AccountInfo.getInstance(result.UID).mediamapping.setMedia(
					result.UID, url, result);
			playAudio(Environment.getExternalStorageDirectory() + result.localpath);
		}
	}

	@Override
	protected void onDetachedFromWindow() {
		if (isPlaying) {
			stop();
		}
		super.onDetachedFromWindow();
	}

}
