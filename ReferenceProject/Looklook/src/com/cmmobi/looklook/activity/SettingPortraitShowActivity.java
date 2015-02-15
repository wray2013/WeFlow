package com.cmmobi.looklook.activity;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.content.LocalBroadcastManager;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import cn.zipper.framwork.core.ZActivity;

import com.cmmobi.looklook.R;
import com.cmmobi.looklook.common.utils.BitmapUtils;
import com.cmmobi.looklook.common.utils.CmmobiClickAgentWrapper;
import com.cmmobi.looklook.common.utils.UmengclickAgentWrapper;
import com.cmmobi.looklook.info.profile.AccountInfo;
import com.cmmobi.looklook.info.profile.ActiveAccount;
import com.nostra13.universalimageloader.api.AnimateFirstDisplayListener;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;
import com.nostra13.universalimageloader.core.display.CircularBitmapDisplayer;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;


/**
 * 放大显示头像界面
 * 
 * @author youtian
 * 
 */

public class SettingPortraitShowActivity extends ZActivity {
	private ImageView iv_photo;
	private DisplayImageOptions options;
	protected ImageLoader imageLoader;
	private ImageLoadingListener animateFirstListener;
	
	private LinearLayout ll_portraitshow;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setting_portraitshow);
		iv_photo = (ImageView)findViewById(R.id.iv_photo);
		animateFirstListener = new AnimateFirstDisplayListener();
		imageLoader = ImageLoader.getInstance();
		imageLoader.init(ImageLoaderConfiguration.createDefault(SettingPortraitShowActivity.this));
		options = new DisplayImageOptions.Builder()
		.showStubImage(R.drawable.moren_touxiang_1)
		.showImageForEmptyUri(R.drawable.moren_touxiang_1)
		.showImageOnFail(R.drawable.moren_touxiang_1)
		.cacheInMemory(true)
		.cacheOnDisc(true)
		.displayer(new SimpleBitmapDisplayer())
		.build();
		
		ll_portraitshow = (LinearLayout)findViewById(R.id.ll_portraitshow);
		ll_portraitshow.setOnClickListener(this);
		AccountInfo accountInfo = AccountInfo.getInstance(ActiveAccount.getInstance(this).getUID());
		if(this.getIntent().getExtras() !=null){
			FileInputStream fis;
			try {
				fis = new FileInputStream(this.getIntent().getExtras().getString("path"));
				Bitmap bitmap  = BitmapFactory.decodeStream(fis);		
				iv_photo.setImageBitmap(bitmap);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}else if (accountInfo != null) {
			imageLoader.displayImage(accountInfo.headimageurl, iv_photo, options, animateFirstListener, ActiveAccount.getInstance(this).getUID(), 0);
		}
		
	}

	
	@Override
	public void onResume() {
		super.onResume();
		UmengclickAgentWrapper.onResume(this);
		CmmobiClickAgentWrapper.onResume(this);
	}

	@Override
	public void onPause() {
		super.onPause();
		UmengclickAgentWrapper.onPause(this);
		CmmobiClickAgentWrapper.onPause(this);
	}

	@Override
	public void onStop(){
		super.onStop();
		CmmobiClickAgentWrapper.onStop(this);
	}



	@Override
	public boolean handleMessage(Message msg) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		this.finish();
		overridePendingTransition(R.anim.zoomin, R.anim.zoomout);
	}

	

	
}
