package com.cmmobi.looklook.v31;

import java.io.IOException;

import com.cmmobi.looklook.R;
import com.cmmobi.looklook.httpproxy.HttpProxy;
import com.cmmobi.looklook.info.profile.ActiveAccount;

import cn.zipper.framwork.core.ZApplication;
import cn.zipper.framwork.utils.ZByteToSize;

import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnInfoListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;
import android.app.Activity;
import android.app.ProgressDialog;

public class PlayerActivity extends Activity implements OnClickListener, Callback {
	private final static String TAG = "HttpVideoPlayer";
	
	public static final int GET_LOCAL_URL   = 0xfffffff1;
	public static final int UPDATE_UI       = 0xfffffff2;
	
	private Handler handler;
	private VideoView mVideoView;
	private MediaController mediaController;

	private EditText etVideoUrl;
	private EditText etLocalVideoUrl;

	private TextView tvTotal;
	private TextView tvDownloaded;
	private TextView tvPlayed;

	private Button btOK;
	private Button btPlay;
	
	private ProgressDialog loadingDialog;
	//代理服务器
	private HttpProxy proxy;
	
	private long startTimeMills = 0;
	private long lastCacheSize = 0;
	
	private String proxyUrl;
	
	private String oriVideoUrl = 
//			"http://1s.looklook.cn:8082/pub/looklook/video_pub/transcode/2013/10/13/175338dd4635cdd15d45b2900d2c74bd5ed7c4.mp4";

//			"http://ks.tarotme.net/data/" +
//			"%E5%93%86%E5%95%A6A%E6%A2%A6/%5Btarotme.net%5D%5B%E9%93" +
//			"%B6%E5%85%89%E5%AD%97%E5%B9%95%E7%BB%84%5D%5B%E5%93%86%E5" +
//			"%95%A6A%E6%A2%A6%E6%96%B0%E7%95%AADoraemon%5D%5B340%5D%5BGB" +
//			"%5D%5B2013.08.16%5D%E5%9F%B9%E8%82%B2%E7%84%B0%E7%81%AB%E5%90" +
//			"%A7%20%EF%BC%81%26%E5%88%86%E8%BA%AB%E9%94%A4%E5%AD%90%5B720P%5D%5BMP4%5D_convert.mp4";
			
//			"http://test.looklook.cn:380/looklook/video_pub/original/2013/10/21/160344376e6983c30f4c7ebe1dcf36aae61eab.mp4";//128M
			
//			"http://1s.looklook.cn:8082/pub/looklook/video_pub/original/2013/10/17/16451524058d1a23dd4120b670ca832fcfba83.mp4";
//			"http://1s.looklook.cn:8082/pub/looklook/video_pub/transcode/2013/11/04/000821d67bff36a85b459eaec89a749b28a8eb.mp4";//7.1M
			
			"http://1s.looklook.cn:8082/pub/looklook/video_pub/original/2013/10/11/095806db5b5072c74a4a2fa1a93df1b506d148.mp4";//24M
//	"http://192.168.100.111:8080/looklook/audio_pub/audio_source/2013/12/03/095712aa0e0f14932a47d9a6a71b20f86f4283.mp3" +
//	"?os=android:4.0.4&browsetype=&cn=0&source=3&internettype=wifi&clientversion=3.0.3001&userid=3535fe2c0a5f804415088130507fbd7dc7a4" +
//	"&gps=:&contentid=585615&imei=353975054841933&imsi=460012710505555&mobiletype=GT-I9300&channelid=&ip=-33484628&contenttype=3&pagetype=4";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_proxy_player);
		handler = new Handler(this);
		// 初始化VideoView
		mediaController = new MediaController(this);
		mVideoView = (VideoView) findViewById(R.id.videoView);
		mVideoView.setMediaController(mediaController);
		mVideoView.setOnErrorListener(new OnErrorListener() {
			
			@Override
			public boolean onError(MediaPlayer mp, int what, int extra) {
				// TODO Auto-generated method stub
				Log.e(TAG, "Error occured [" + what + "]");
				return false;
			}
		});
		mVideoView.setOnCompletionListener(new OnCompletionListener() {
			@Override
			public void onCompletion(MediaPlayer mp) {
				// TODO Auto-generated method stub
				proxy.stopProxy();
			}
		});
		/*
		mVideoView.setOnInfoListener(new OnInfoListener() {
			
			@Override
			public boolean onInfo(MediaPlayer mp, int what, int extra) {
				// TODO Auto-generated method stub
				switch(what) {
				case MediaPlayer.MEDIA_INFO_BUFFERING_START:
					loadingDialog = PrintAlertUtil.showDialog(ZApplication.getInstance(), "正在加载...");
					break;
				case MediaPlayer.MEDIA_INFO_BUFFERING_END:
					if(loadingDialog != null) {
						loadingDialog.dismiss();
					}
					break;
				}
				return false;
			}
		});*/
		// mVideoView.setOnPreparedListener(mOnPreparedListener);

		etVideoUrl = (EditText) findViewById(R.id.et_videourl);
		etLocalVideoUrl = (EditText) findViewById(R.id.et_localvideourl);

		etVideoUrl.setText(oriVideoUrl);
		
		tvTotal = (TextView) findViewById(R.id.tv_total);
		tvDownloaded = (TextView) findViewById(R.id.tv_download);
		tvPlayed = (TextView) findViewById(R.id.tv_played);

		btOK = (Button) findViewById(R.id.bt_ok);
		btOK.setOnClickListener(this);
		
		btPlay = (Button) findViewById(R.id.bt_play);
		btPlay.setOnClickListener(this);
		
		// 一直显示MediaController
		showController.sendEmptyMessageDelayed(0, 1000);
		
		init();
	}

	private void init() {
		
		proxy = HttpProxy.getInstance(ActiveAccount.getInstance(this).getUID());
//		proxyServer.asynStartProxy();
		
	}
	
	/**
	 * 异步获取本地地址
	 * 
	 * @throws IOException
	 */
	public void asynGetProxyUrl(final Handler handler) {
		new Thread() {
			public void run() {
//				proxyUrl = proxy.getLocalURL(oriVideoUrl);
				proxyUrl = proxy.prepare(oriVideoUrl);
				
//				try {
//					String prebufferFilePath = proxyServer.prebuffer(
//							proxyUrls[0], 5 * 1024);
//					Log.e(TAG, "预加载文件：" + prebufferFilePath);
//				} catch (Exception ex) {
//					Log.e(TAG, ex.toString());
//					Log.e(TAG, ProxyUtils.getExceptionMessage(ex));
//				}
				
				Message message = handler.obtainMessage(GET_LOCAL_URL);
				handler.sendMessage(message);
			}
		}.start();
	}
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.bt_ok:
			// Step 1. get input Http Address
			String url = etVideoUrl.getText().toString();
			if(url == null || url.equals("")) {
				Toast.makeText(this, "Invalid Input Url", Toast.LENGTH_LONG).show();
				return;
			}
			
			// Step 2. get local Http Address
			btOK.setText("请稍后……");
			btOK.setClickable(false);
			asynGetProxyUrl(handler);
//			etLocalVideoUrl.setText(proxyUrl);
			
			// Step 3. Refresh Total Size Result Area
//			RefreshSize();
			break;
			
		case R.id.bt_play:
			Log.d(TAG, "playurl = " + proxyUrl);
			mVideoView.setVideoPath(proxyUrl);
			mVideoView.start();
			startTimeMills = System.currentTimeMillis();
			UpdateUI();
			break;
		default:
			break;
		}
	}
	
	private Handler showController = new Handler() {
		public void handleMessage(Message msg) {
			mediaController.show(0);
		}
	};
	
	private void UpdateUI() {
		float costTimeInSec = (System.currentTimeMillis() - startTimeMills) / 1000;
		if (proxy != null) {
			String totalsize = ZByteToSize.smartSize(proxy.getTotalSize());
			int cachesize = proxy.getCachedSize();
			String curDownSize = ZByteToSize.smartSize(cachesize);
			tvTotal.setText("总大小:" + totalsize);
			tvDownloaded.setText("已下载:" + curDownSize +
					"(" + ZByteToSize.smartSize(cachesize - lastCacheSize / costTimeInSec) + "/s)");
			tvPlayed.setText("已播放:" + mVideoView.getCurrentPosition());
			lastCacheSize = cachesize;
		}
		Message message = handler.obtainMessage(UPDATE_UI);
		handler.sendMessageDelayed(message, 2000);
	}

	@Override
	public boolean handleMessage(Message msg) {
		switch (msg.what) {
		case GET_LOCAL_URL:
			btOK.setText("确 定");
			btOK.setClickable(true);
			btPlay.setClickable(true);
			etLocalVideoUrl.setText(proxyUrl);
			break;
		case UPDATE_UI:
			UpdateUI();
		}
		return false;
	}
	
	@Override
	protected void onPause() {
		if(proxy != null) {
			proxy.stopProxy();
		}
		super.onPause();
	}
	
}
