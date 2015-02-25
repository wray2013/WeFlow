package com.etoc.weflowdemo.activity;

import com.etoc.weflowdemo.R;

import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageButton;
import android.widget.MediaController;
import android.widget.VideoView;

public class AdDetailActivity extends TitleRootActivity {

	private ImageButton ibPlay;
	private VideoView vvAdvVideo;
	private MediaController mediaController;
	
	private boolean hasVideoInitialized = false;
	
	private DisplayMetrics dm = new DisplayMetrics();
	
	private static final String AdUrl = "http://1s.looklook.cn:8082/pub/looklook/video_pub" +
			"/original/2013/10/11/111343a1958778779745b19b2afca35a891b5d.mp4";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		dm = getResources().getDisplayMetrics();
		
		initViews();
	}
	
	public void initViews() {
		setLeftButtonBackground(R.drawable.btn_back);
		hideRightButton();
		setTitleText("看广告");
		
		ibPlay = (ImageButton) findViewById(R.id.btn_play);
		ibPlay.setOnClickListener(this);
		
		mediaController=new MediaController(this);
		vvAdvVideo = (VideoView) findViewById(R.id.vv_ad_video);
//		vvAdvVideo.setMediaController(mediaController);
		vvAdvVideo.setOnPreparedListener(mOnPreparedListener);
		vvAdvVideo.setOnErrorListener(mOnErrorListener);
		vvAdvVideo.setOnCompletionListener(mOnCompletionListener);
	}
	
	private OnCompletionListener mOnCompletionListener = new OnCompletionListener() {

		@Override
		public void onCompletion(MediaPlayer mp) {
			// TODO Auto-generated method stub
			vvAdvVideo.stopPlayback();
			ibPlay.setVisibility(View.VISIBLE);
		}
		
	};
	
	private OnErrorListener mOnErrorListener = new OnErrorListener() {

		@Override
		public boolean onError(MediaPlayer mp, int arg1, int arg2) {
			// TODO Auto-generated method stub
			vvAdvVideo.stopPlayback();
			hasVideoInitialized = false;
			ibPlay.setVisibility(View.VISIBLE);
			return false;
		}
		
	};
	
	private OnPreparedListener mOnPreparedListener = new OnPreparedListener() {

		@Override
		public void onPrepared(MediaPlayer mp) {
			vvAdvVideo.start();
		}
	};
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()) {
		case R.id.btn_play:
			if(!hasVideoInitialized) {
				vvAdvVideo.setVideoURI(Uri.parse(AdUrl));
				hasVideoInitialized = true;
			} else {
				vvAdvVideo.resume();
			}
			ibPlay.setVisibility(View.GONE);
//			vvAdvVideo.start();
			break;
		case R.id.ib_title_left:
			super.onClick(v);
			break;
		}
	}
	
	@Override
	public boolean handleMessage(Message arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int subContentViewId() {
		// TODO Auto-generated method stub
		return R.layout.activity_addetail;
	}

}
