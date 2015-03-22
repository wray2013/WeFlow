package com.etoc.weflow.activity;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.MediaController;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;
import android.widget.VideoView;

import com.etoc.weflow.R;
import com.etoc.weflow.WeFlowApplication;
import com.etoc.weflow.dao.AccountInfo;
import com.etoc.weflow.dialog.PromptDialog;
import com.etoc.weflow.net.GsonResponseObject.AdvFlowResp;
import com.etoc.weflow.net.GsonResponseObject.AdverInfo;
import com.etoc.weflow.net.Requester;
import com.etoc.weflow.utils.DateUtils;
import com.etoc.weflow.utils.VNetworkStateDetector;
import com.etoc.weflow.utils.ViewUtils;
import com.google.gson.Gson;

public class AdDetailActivity extends TitleRootActivity {

	private ImageButton ibPlay;
	private VideoView vvAdvVideo;
	private TextView tvSecRemains;
	
	private MediaController mediaController;
	
	private boolean hasVideoInitialized = false;
	
	private DisplayMetrics dm = new DisplayMetrics();
	AdverInfo adInfo = null;
	
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
//		setLeftButtonBackground(R.drawable.btn_back);
		hideRightButton();
		setTitleText("看广告");
		
		ibPlay = (ImageButton) findViewById(R.id.btn_play);
		ibPlay.setOnClickListener(this);
		
		tvSecRemains = (TextView) findViewById(R.id.tv_sec_remains);
		tvSecRemains.setVisibility(View.GONE);
		
		mediaController=new MediaController(this);
		vvAdvVideo = (VideoView) findViewById(R.id.vv_ad_video);
		vvAdvVideo.setOnPreparedListener(mOnPreparedListener);
		vvAdvVideo.setOnErrorListener(mOnErrorListener);
		vvAdvVideo.setOnCompletionListener(mOnCompletionListener);
//		vvAdvVideo.setMediaController(mediaController);
		
//		showController.sendEmptyMessageDelayed(0, 1000);
		
		String adInfoStr = getIntent().getStringExtra("adinfo");
		adInfo = new Gson().fromJson(adInfoStr, AdverInfo.class);
//		TextView tvTitle = (TextView) findViewById(R.id.tv_ad_title);
//		tvTitle.setText(adInfo.title);
		setTitleText(adInfo.title);
		AdUrl = adInfo.video;
		TextView tvContent = (TextView) findViewById(R.id.tv_ad_content);
		tvContent.setText(adInfo.content);
		
		TextView tvCoins = (TextView) findViewById(R.id.tv_content);
		float coins = 0;
		try {
			coins = Float.parseFloat(adInfo.flowcoins);
		} catch(Exception e) {
			e.printStackTrace();
		}
		tvCoins.setText((int)coins + "");
		
		ViewUtils.setHeight(findViewById(R.id.rl_mid), 122);
		ViewUtils.setWidth(findViewById(R.id.rl_ad_flow), 62);
		ViewUtils.setHeight(findViewById(R.id.rl_ad_flow), 85);
		
		ViewUtils.setMarginLeft(findViewById(R.id.rl_mid), 32);
		ViewUtils.setMarginRight(findViewById(R.id.rl_mid), 32);
		
		ViewUtils.setMarginTop(findViewById(R.id.rl_bottom), 22);
		ViewUtils.setMarginLeft(findViewById(R.id.rl_bottom), 32);
		ViewUtils.setMarginRight(findViewById(R.id.rl_bottom), 32);
		
		ViewUtils.setTextSize(findViewById(R.id.tv_ad_content), 32);
		ViewUtils.setTextSize(findViewById(R.id.tv_ad_time), 28);
		ViewUtils.setTextSize(findViewById(R.id.tv_ad_ins), 28);
		ViewUtils.setTextSize(findViewById(R.id.tv_content), 23);
		ViewUtils.setTextSize(findViewById(R.id.tv_ad_flow_label), 18);
		
		TextView tvTime = (TextView) findViewById(R.id.tv_ad_time);
		if(adInfo.publishtime != null) {
			String date = DateUtils.getStringFromMilli(adInfo.publishtime, DateUtils.DATE_FORMAT_NORMAL_1);
			tvTime.setText(date);
		}
	}
	
	@Override
	protected int graviteType() {
		// TODO Auto-generated method stub
		return GRAVITE_LEFT;
	}
	
	/*private Handler showController = new Handler() {
		public void handleMessage(Message msg) {
			mediaController.show(0);
		}
	};*/
	
	private OnCompletionListener mOnCompletionListener = new OnCompletionListener() {

		@Override
		public void onCompletion(MediaPlayer mp) {
			// TODO Auto-generated method stub
			Log.d("=AAA=","onCompletion in");
			vvAdvVideo.stopPlayback();
			ibPlay.setVisibility(View.VISIBLE);
			tvSecRemains.setVisibility(View.GONE);
			handler.removeCallbacks(run);
			if (hasVideoInitialized) {
//				Requester.orderLargess(handler, MainApplication.accountPhone, "C", "prod_in_charge_10");
				AccountInfo accountInfo = WeFlowApplication.getAppInstance().getAccountInfo();
				if (accountInfo != null) {
					Requester.getAdvFlow(true, handler, accountInfo.getUserid(), adInfo.videoid);
				} 
				
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
			tvSecRemains.setVisibility(View.GONE);
			handler.removeCallbacks(run);
			return false;
		}
		
	};
	
	private OnPreparedListener mOnPreparedListener = new OnPreparedListener() {

		@Override
		public void onPrepared(MediaPlayer mp) {
//			vvAdvVideo.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
			vvAdvVideo.start();
			tvSecRemains.setVisibility(View.VISIBLE);
			handler.post(run);
		}
	};
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()) {
		case R.id.btn_play:
			/*if(!hasVideoInitialized) {
				Log.d("=AAA=","url = " + AdUrl);
				vvAdvVideo.setVideoURI(Uri.parse(AdUrl));
				hasVideoInitialized = true;
			} else {
				vvAdvVideo.resume();
			}
			ibPlay.setVisibility(View.GONE);*/
			
			checkNetwork();
			break;
		}
		super.onClick(v);
	}
	
	private void checkNetwork() {
		if (VNetworkStateDetector.isAvailable()) {
			if(VNetworkStateDetector.isMobile()) {
			PromptDialog.Dialog(this, true, "温馨提示", "您当前使用的流量上网，是否继续播放？",
					"继续", "取消",
					new OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int arg1) {
							// TODO Auto-generated method stub
							if(!hasVideoInitialized) {
								Log.d("=AAA=","url = " + AdUrl);
								vvAdvVideo.setVideoURI(Uri.parse(AdUrl));
								hasVideoInitialized = true;
							} else {
								vvAdvVideo.resume();
							}
							ibPlay.setVisibility(View.GONE);
							dialog.dismiss();
						}
					}, new OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int arg1) {
							// TODO Auto-generated method stub
							dialog.dismiss();
						}
					});
			} else {
				if(!hasVideoInitialized) {
					Log.d("=AAA=","url = " + AdUrl);
					vvAdvVideo.setVideoURI(Uri.parse(AdUrl));
					hasVideoInitialized = true;
				} else {
					vvAdvVideo.resume();
				}
				ibPlay.setVisibility(View.GONE);
			}
		} else {
			PromptDialog.Dialog(this, "温馨提示", "当前网络不可用", "确定");
		}
	}
	
	@Override
	public boolean handleMessage(Message msg) {
		// TODO Auto-generated method stub
		switch (msg.what) {
		case Requester.RESPONSE_TYPE_ADV_FLOW:
			if (msg.obj != null) {
				AdvFlowResp resp = (AdvFlowResp) msg.obj;
				if("0".equals(resp.status) || "0000".equals(resp.status)) {
					PromptDialog.Alert(AdDetailActivity.class, "成功获取" + adInfo.flowcoins + "流量币");
					AccountInfo accountInfo = WeFlowApplication.getAppInstance().getAccountInfo();
					accountInfo.setFlowcoins(resp.flowcoins);
					WeFlowApplication.getAppInstance().PersistAccountInfo(accountInfo);
				}
			}
			break;
		/*case Requester.RESPONSE_TYPE_ORDER_LARGESS:
			if (msg.obj != null) {
				commonResponse resp = (commonResponse) msg.obj;
				if (resp.isSucceed()) {
					PromptDialog.Alert(PayPhoneBillActivity.class, "成功获取10流量币");
				} else {
					PromptDialog.Alert(PayPhoneBillActivity.class, "获取流量币失败");
				}
			}
			break;*/
		}
		return false;
	}

	@Override
	public int subContentViewId() {
		// TODO Auto-generated method stub
		return R.layout.activity_addetail;
	}
	
	private Runnable run = new Runnable() {
		int buffer, currentPosition, duration;

		public void run() {
			// 获得当前播放时间和当前视频的长度
			currentPosition = vvAdvVideo.getCurrentPosition();
			if(currentPosition > 0) {
				vvAdvVideo.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
			}
			duration = vvAdvVideo.getDuration();
//			int time = ((currentPosition * 100) / duration);
			tvSecRemains.setText((duration - currentPosition) / 1000 + "s");
//			buffer = vvAdvVideo.getBufferPercentage();
//			seekBar.setSecondaryProgress(percent);

			handler.postDelayed(run, 1000);
		}
	};

}
