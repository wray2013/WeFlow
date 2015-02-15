package com.cmmobi.looklook.activity_test;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;

import com.cmmobi.looklook.common.view.MediaPlayerUI;
import com.cmmobivideo.utils.ContextHolder;
import com.cmmobivideo.workers.XMediaPlayer;

import effect.XEffectMediaPlayer;
import effect.XEffects;

public class MediaPlayerActivity extends Activity implements View.OnClickListener{
	private static final String TAG = "ZC_MediaPlayerActivity";
	
	private XMediaPlayer mMediaPlayer;
	private boolean isToStop = false;
	
	private String mFilePath = "/mnt/sdcard/worldofwarcraft_china.mp4";
//	private String mFilePath = "/mnt/sdcard/test0001.mp4";
//	private String mFilePath = "/mnt/sdcard/rrrr.mp4";
//	private String mFilePath = "/mnt/sdcard/aaaa.mp4";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		ContextHolder.setContext(this);
		
		RelativeLayout shell = new RelativeLayout(this);
		shell.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
		shell.setBackgroundColor(0xff000099);
		
		setContentView(shell);
		
		
		LayoutParams params = new LayoutParams(RelativeLayout.LayoutParams.FILL_PARENT,RelativeLayout.LayoutParams.FILL_PARENT);
		params.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
		
		XEffects mEffects = new XEffects();
		mMediaPlayer = new XMediaPlayer(this, mEffects);
		View view = (View)mMediaPlayer.getGlLayer();
		view.setLayoutParams(params);
		
		shell.addView(view, params);
		
		MediaPlayerUI mediaPlayerUI = new MediaPlayerUI(this, mMediaPlayer);
		shell.addView(mediaPlayerUI.getLayout(), 555,555);
		mediaPlayerUI.setFilePath(mFilePath);
		
		Button button = new Button(this);
		button.setText("播放/停止");
		
		
		button.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (mMediaPlayer.getStatus() == XEffectMediaPlayer.STATUS_UNKNOW || mMediaPlayer.getStatus() == XEffectMediaPlayer.STATUS_STOP) {
					mMediaPlayer.open(mFilePath);
//					Log.i(TAG, "[play] mTotalTime:"+mMediaPlayer.getTotalTime()+",width:"+mMediaPlayer.getVideoWith()+",height:"+mMediaPlayer.getVideoHeight());
					mMediaPlayer.play();
					isToStop = false;
					
//				} else if (mMediaPlayer.getStatus() == XEffectMediaPlayer.STATUS_PALYING && isToStop == false) {
//					mMediaPlayer.pause();
//					isToStop = true;
//				}else if (mMediaPlayer.getStatus() == XEffectMediaPlayer.STATUS_PAUSE) {
//					mMediaPlayer.resume();
//				}else if (mMediaPlayer.getStatus() == XEffectMediaPlayer.STATUS_PALYING && isToStop) {
//					mMediaPlayer.stop();
				}
				else if (mMediaPlayer.getStatus() == XEffectMediaPlayer.STATUS_PALYING) {
					mMediaPlayer.stop();
				}
			}
		});
		shell.addView(button, 211, 100);
		
	}
	
	@Override
	protected void onResume() {
		super.onResume();
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		mMediaPlayer.pause();
	}

	@Override
	public void onClick(View view) {
		
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		mMediaPlayer.release();
	}
}