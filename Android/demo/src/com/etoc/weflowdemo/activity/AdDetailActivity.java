package com.etoc.weflowdemo.activity;

import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

import com.etoc.weflowdemo.MainApplication;
import com.etoc.weflowdemo.R;
import com.etoc.weflowdemo.dialog.PromptDialog;
import com.etoc.weflowdemo.net.GsonResponseObject.AdvInfo;
import com.etoc.weflowdemo.net.GsonResponseObject.commonResponse;
import com.etoc.weflowdemo.net.Requester;
import com.google.gson.Gson;

public class AdDetailActivity extends TitleRootActivity {

	private ImageButton ibPlay;
	private VideoView vvAdvVideo;
	private MediaController mediaController;
	
	private boolean hasVideoInitialized = false;
	
	private DisplayMetrics dm = new DisplayMetrics();
	
	private String AdUrl = "http://1s.looklook.cn:8082/pub/looklook/video_pub" +
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
		
		String adInfoStr = getIntent().getStringExtra("adinfo");
		AdvInfo adInfo = new Gson().fromJson(adInfoStr, AdvInfo.class);
		TextView tvTitle = (TextView) findViewById(R.id.tv_ad_title);
		tvTitle.setText(adInfo.title);
		AdUrl = adInfo.videourl;
		TextView tvContent = (TextView) findViewById(R.id.tv_ad_content);
		tvContent.setText(adInfo.content);
	}
	
	private OnCompletionListener mOnCompletionListener = new OnCompletionListener() {

		@Override
		public void onCompletion(MediaPlayer mp) {
			// TODO Auto-generated method stub
			Log.d("=AAA=","onCompletion in");
			vvAdvVideo.stopPlayback();
			ibPlay.setVisibility(View.VISIBLE);
			if (hasVideoInitialized) {
				Requester.orderLargess(handler, MainApplication.accountPhone, "C", "prod_in_charge_10");
			}
		}
		
	};
	
	private OnErrorListener mOnErrorListener = new OnErrorListener() {

		@Override
		public boolean onError(MediaPlayer mp, int arg1, int arg2) {
			// TODO Auto-generated method stub
			Log.d("=AAA=","onError in");
//			vvAdvVideo.stopPlayback();
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
				Log.d("=AAA=","url = " + AdUrl);
				vvAdvVideo.setVideoURI(Uri.parse(AdUrl));
				hasVideoInitialized = true;
			} else {
				vvAdvVideo.resume();
			}
			ibPlay.setVisibility(View.GONE);
//			vvAdvVideo.start();
			break;
		}
		super.onClick(v);
	}
	
	@Override
	public boolean handleMessage(Message msg) {
		// TODO Auto-generated method stub
		switch (msg.what) {
		case Requester.RESPONSE_TYPE_ORDER_LARGESS:
			if (msg.obj != null) {
				commonResponse resp = (commonResponse) msg.obj;
				if (resp.isSucceed()) {
					PromptDialog.Alert(PayPhoneBillActivity.class, "成功获取20流量币");
				} else {
					PromptDialog.Alert(PayPhoneBillActivity.class, "获取流量币失败");
				}
			}
			break;
		}
		return false;
	}

	@Override
	public int subContentViewId() {
		// TODO Auto-generated method stub
		return R.layout.activity_addetail;
	}

}
