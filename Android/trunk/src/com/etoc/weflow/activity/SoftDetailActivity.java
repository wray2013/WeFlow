package com.etoc.weflow.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.dd.processbutton.iml.SubmitProcessButton;
import com.etoc.weflow.R;
import com.etoc.weflow.WeFlowApplication;
import com.etoc.weflow.download.DownloadEvent;
import com.etoc.weflow.download.DownloadManager;
import com.etoc.weflow.download.DownloadType;
import com.etoc.weflow.net.Requester;
import com.etoc.weflow.net.GsonResponseObject.SoftInfoResp;
import com.etoc.weflow.utils.ConStant;
import com.etoc.weflow.utils.NumberUtils;
import com.etoc.weflow.utils.ProgressGenerator;
import com.etoc.weflow.utils.ProgressGenerator.OnCompleteListener;
import com.etoc.weflow.utils.ViewUtils;
import com.google.gson.Gson;
import com.nostra13.universalimageloader.api.MyImageLoader;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.sothree.slidinguppanel.SlidingUpPanelLayout.PanelState;

import de.greenrobot.event.EventBus;

public class SoftDetailActivity extends TitleRootActivity implements OnCompleteListener {

	private HorizontalScrollView hscrollView;
	private TextView tvTitle;
	private TextView tvSize;
	private TextView tvVersion;
	private TextView tvFlow;
	private TextView tvDesc;
	private ImageView ivIcon;
	private TextView tvFlowDesc;
	private SoftInfoResp softDetailResp;
	MyImageLoader imageLoader = null;
	DisplayImageOptions imageLoaderOptions = null;
	LinearLayout llPicIntro = null;
	SubmitProcessButton btnDownload;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		String detailStr = getIntent().getStringExtra(ConStant.INTENT_SOFT_DETAIL);
		if (detailStr != null) {
			softDetailResp = new Gson().fromJson(detailStr, SoftInfoResp.class);
		}
		
		imageLoader = MyImageLoader.getInstance();

		imageLoaderOptions = new DisplayImageOptions.Builder()
				.cacheInMemory(true)
				.cacheOnDisc(true)
				.imageScaleType(ImageScaleType.IN_SAMPLE_POWER_OF_2)
				.bitmapConfig(Bitmap.Config.RGB_565)
				.showImageForEmptyUri(R.drawable.content_pic_default)
				.showImageOnFail(R.drawable.content_pic_default)
				.showImageOnLoading(R.drawable.content_pic_default)
				.build();
		
		initViews();
		
		
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}
	
	public void onEventMainThread(DownloadEvent event) {
		// TODO Auto-generated method stub
		Log.d("=AAA=","onEventMainThread eventType = " + event + " type = " + event.getType() 
				+ " wholeSize = " + event.getWholeSize() + " downloadSize = " + event.getDownloadSize()
				+ " apkUrl = " + softDetailResp.soft + " downlaodUrl = " + event.getUrl());
		
		
		switch (event) {
		case STATUS_CHANGED:
		case PROGRESS_CHANGED:
			if (softDetailResp.soft.equals(event.getUrl())) {
				long progress = 0;
				if (event.getWholeSize() > 0) {
					progress = (long)(event.getDownloadSize()) * 100 / event.getWholeSize();
					btnDownload.setProgress((int)progress);
				}
			}
			break;
		}
		
	}
	
	private void initViews() {
		hideRightButton();
		tvTitle = (TextView) findViewById(R.id.tv_app_name);
		tvSize = (TextView) findViewById(R.id.tv_app_size);
		tvVersion = (TextView) findViewById(R.id.tv_app_vision);
		tvFlow = (TextView) findViewById(R.id.tv_app_flow);
		tvDesc = (TextView) findViewById(R.id.tv_app_desc);
		ivIcon = (ImageView) findViewById(R.id.iv_app_icon);
		tvFlowDesc = (TextView) findViewById(R.id.tv_app_flow_desc);
		hscrollView = (HorizontalScrollView) findViewById(R.id.hsv_pic_intro);
		
		btnDownload = (SubmitProcessButton) findViewById(R.id.btnSubmit);
		
		btnDownload.setOnClickListener(this);
		
		llPicIntro = (LinearLayout) findViewById(R.id.ll_pic_intro);
		
		ViewUtils.setHeight(findViewById(R.id.rl_bottom), 114);
		ViewUtils.setHeight(findViewById(R.id.rl_app_base_info), 150);
		ViewUtils.setMarginLeft(findViewById(R.id.rl_app_base_info), 32);
		ViewUtils.setMarginRight(findViewById(R.id.rl_app_base_info), 32);
		ViewUtils.setSize(ivIcon, 100, 100);
		ViewUtils.setMarginTop(tvTitle, 40);
		ViewUtils.setMarginBottom(tvSize, 24);
		ViewUtils.setMarginLeft(tvVersion, 24);
		ViewUtils.setSize(findViewById(R.id.rl_app_flow), 72, 96);
		ViewUtils.setMarginTop(tvFlow, 12);
		ViewUtils.setHeight(findViewById(R.id.rl_app_flow_desc), 100);
		ViewUtils.setHeight(findViewById(R.id.tv_pic_intro_label), 66);
		ViewUtils.setHeight(findViewById(R.id.hsv_pic_intro), 398);
		ViewUtils.setMarginTop(findViewById(R.id.tv_app_desc_label), 48);
		ViewUtils.setMarginTop(tvDesc, 24);
		ViewUtils.setSize(btnDownload, 360,72);
		ViewUtils.setMarginTop(btnDownload, 36);
		
		ViewUtils.setTextSize(tvTitle, 36);
		ViewUtils.setTextSize(tvSize, 26);
		ViewUtils.setTextSize(tvVersion, 26);
		ViewUtils.setTextSize(tvFlow, 30);
		ViewUtils.setTextSize(findViewById(R.id.tv_app_flow_label), 20);
		ViewUtils.setTextSize(tvFlowDesc, 26);
		ViewUtils.setTextSize(findViewById(R.id.tv_pic_intro_label), 30);
		ViewUtils.setTextSize(findViewById(R.id.tv_app_desc_label), 30);
		ViewUtils.setTextSize(tvDesc, 30);
		ViewUtils.setTextSize(btnDownload, 32);
		
		
		if (softDetailResp != null) {
			tvTitle.setText(softDetailResp.title);
			imageLoader.displayImage(softDetailResp.appicon, ivIcon);
			tvSize.setText(softDetailResp.size);
			tvVersion.setText("版本：" + softDetailResp.version);
			tvFlow.setText(NumberUtils.convert2IntStr(softDetailResp.flowcoins));
			tvDesc.setText(softDetailResp.introduction);
			tvFlowDesc.setText(softDetailResp.instruction);
			setTitleText(softDetailResp.title);
			
			if (softDetailResp.apppreview != null) {
				String [] urls = softDetailResp.apppreview.split(",");
				if (urls != null && urls.length > 0) {
					for (final String url:urls) {
						ImageView ivPre = new ImageView(this);
						ivPre.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT));
						ViewUtils.setWidth(ivPre, 214);
						ViewUtils.setMarginRight(ivPre, 12);
						ivPre.setScaleType(ScaleType.FIT_XY);
						ivPre.setOnClickListener(new OnClickListener() {
							
							@Override
							public void onClick(View v) {
								// TODO Auto-generated method stub
								Intent intent = new Intent(SoftDetailActivity.this, PicShowActivity.class);
								intent.putExtra("imageUrl", url);
								//EventBus.getDefault().post(RequestEvent.LOADING_END);
								startActivity(intent);
							}
						});
						
						
						imageLoader.displayImage(url, ivPre,imageLoaderOptions);
						llPicIntro.addView(ivPre);
					}
				}
			}
			
			if (DownloadManager.getInstance().isDownlaoded(softDetailResp.soft)) {
				btnDownload.setProgress(100);
				btnDownload.setEnabled(false);
			} else if (DownloadManager.getInstance().isDownlaoding(softDetailResp.soft)){
				btnDownload.setEnabled(false);
			}
		}
		
	}
	
	@Override
	protected int graviteType() {
		// TODO Auto-generated method stub
		return GRAVITE_LEFT;
	}
	
	@Override
	public boolean handleMessage(Message msg) {
		// TODO Auto-generated method stub
		return false;
	}
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()) {
		case R.id.btnSubmit:
			DownloadManager.getInstance().addDownloadTask(softDetailResp.soft, "0", softDetailResp.title, softDetailResp.appicon, "",  DownloadType.APP, "","","","");
            btnDownload.setEnabled(false);
			
//			Requester.getAppFlow(true, handler, WeFlowApplication.getAppInstance().getAccountInfo().getUserid(), softDetailResp.appid, "0");
	        break;
		}
		super.onClick(v);
	}

	@Override
	public int subContentViewId() {
		// TODO Auto-generated method stub
		return R.layout.activity_soft_detail;
	}

	@Override
	public void onComplete() {
		// TODO Auto-generated method stub
		Toast.makeText(this, "Loading Complete, button is disabled", Toast.LENGTH_LONG).show();
	}

}
