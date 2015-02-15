package com.cmmobi.looklook.activity;

import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import cn.zipper.framwork.core.ZActivity;

import com.cmmobi.looklook.R;
import com.cmmobi.looklook.common.utils.CmmobiClickAgentWrapper;
import com.cmmobi.looklook.info.profile.AccountInfo;
import com.cmmobi.looklook.info.profile.ActiveAccount;
import com.nostra13.universalimageloader.api.AnimateFirstDisplayListener;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;
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
		//imageLoader.init(ImageLoaderConfiguration.createDefault(SettingPortraitShowActivity.this));
		String cacheMemoryKey = getIntent().getStringExtra("cachememorykey");
		options = new DisplayImageOptions.Builder()
		.showStubImage(0)
		.setCacheMemoryKey(cacheMemoryKey)
		.showImageForEmptyUri(R.drawable.moren_touxiang)
		.showImageOnFail(R.drawable.moren_touxiang)
		.cacheInMemory(true)
		.cacheOnDisc(true)
		.displayer(new SimpleBitmapDisplayer())
		.build();
		
		ll_portraitshow = (LinearLayout)findViewById(R.id.ll_portraitshow);
		ll_portraitshow.setOnClickListener(this);
		String imageUrl=getIntent().getStringExtra("imageUrl");
		if(TextUtils.isEmpty(imageUrl)){
			AccountInfo accountInfo = AccountInfo.getInstance(ActiveAccount.getInstance(this).getUID());
			imageUrl=accountInfo.headimageurl;
		}
		imageLoader.displayImageEx(imageUrl, iv_photo, options, animateFirstListener, ActiveAccount.getInstance(this).getUID(), 0);
		
	}

	
	@Override
	public void onResume() {
		super.onResume();
		CmmobiClickAgentWrapper.onResume(this);
	}

	@Override
	public void onPause() {
		super.onPause();
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
		overridePendingTransition(R.anim.zoomin, R.anim.del_zoomout);
	}

	

	
}
