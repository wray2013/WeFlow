package com.etoc.weflow.activity;

import java.io.File;

import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.etoc.weflow.R;
import com.etoc.weflow.dialog.PromptDialog;
import com.etoc.weflow.event.ParallelEvent;
import com.etoc.weflow.parallel.DiscScanTask;
import com.etoc.weflow.parallel.DiskCleanTask;
import com.etoc.weflow.parallel.ParallelManager;
import com.etoc.weflow.utils.VMobileInfo;
import com.etoc.weflow.utils.ViewUtils;
import com.etoc.weflow.view.SwitchButton;
import com.etoc.weflow.view.SwitchButton.OnChangeListener;
import com.nostra13.universalimageloader.api.MyImageLoader;

public class SettingsActivity extends TitleRootActivity {

	private TextView tvPush, tvCache, tvVersion;
	private SwitchButton switchPush;
	private static final long SETTING_CACHE_SCAN_DU = 0x12468321;
	private static final long SETTING_CACHE_CLEAR = 0x12468322;
	File cacheFold;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		setTitleText("设置");
		hideRightButton();
		
		initView();
		
		cacheFold = MyImageLoader.getInstance().getDiskCache().getDirectory();
		PromptDialog.showProgressDialog(this);
		ParallelManager.getInstance().submitTask(new DiscScanTask(SETTING_CACHE_SCAN_DU, cacheFold));
	}
	
	private void initView() {
		// TODO Auto-generated method stub
		tvPush = (TextView) findViewById(R.id.tv_settings_push);
		
		tvCache   = (TextView) findViewById(R.id.tv_cache);
		tvVersion = (TextView) findViewById(R.id.tv_upgrade_version);
		
		switchPush = (SwitchButton) findViewById(R.id.switch_push);
		switchPush.setOnChangeListener(new OnChangeListener() {
			
			@Override
			public void onChange(SwitchButton sb, boolean state) {
				// TODO Auto-generated method stub
				Toast.makeText(SettingsActivity.this, state ? "开启推送":"关闭推送", Toast.LENGTH_SHORT).show();
			}
		});
		
		findViewById(R.id.rl_settings_cache).setOnClickListener(this);  //清缓存
		findViewById(R.id.rl_settings_upgrade).setOnClickListener(this);//检查更新
		findViewById(R.id.rl_settings_about).setOnClickListener(this);// 关于
		
		/*File cacheFile = MyImageLoader.getInstance().getDiskCache().getDirectory();
		long size = FileUtils.getFolderSize(cacheFile);
		String cache = FileUtils.getFormatSize(size);
		
		tvCache.setText(cache);*/
		
		String ver = VMobileInfo.getAppVersionName(this);
		tvVersion.setText("v" + ver);
		
		ViewUtils.setHeight(findViewById(R.id.rl_settings_top), 113);
		ViewUtils.setHeight(findViewById(R.id.rl_settings_cache), 113);
		ViewUtils.setHeight(findViewById(R.id.rl_settings_upgrade), 113);
		ViewUtils.setHeight(findViewById(R.id.rl_settings_about), 113);
		
		ViewUtils.setTextSize(findViewById(R.id.tv_settings_push), 32);
		ViewUtils.setTextSize(findViewById(R.id.tv_cache_hint), 32);
		ViewUtils.setTextSize(findViewById(R.id.tv_cache), 32);
		ViewUtils.setTextSize(findViewById(R.id.tv_upgrade_hint), 32);
		ViewUtils.setTextSize(findViewById(R.id.tv_upgrade_version), 32);
		ViewUtils.setTextSize(findViewById(R.id.tv_about_hint), 32);
		
		ViewUtils.setMarginTop(findViewById(R.id.rl_settings_center_a), 18);
		
		ViewUtils.setMarginLeft(findViewById(R.id.tv_settings_push), 32);
		ViewUtils.setMarginLeft(findViewById(R.id.tv_cache_hint), 32);
		ViewUtils.setMarginLeft(findViewById(R.id.tv_upgrade_hint), 32);
		ViewUtils.setMarginLeft(findViewById(R.id.tv_about_hint), 32);
		
		ViewUtils.setMarginRight(findViewById(R.id.tv_cache), 32);
		ViewUtils.setMarginRight(findViewById(R.id.tv_upgrade_version), 32);
		ViewUtils.setMarginRight(findViewById(R.id.switch_push), 32);
	}
	
	public void onEventMainThread(Object _event) {
		if(_event instanceof ParallelEvent){
			ParallelEvent event = (ParallelEvent)_event;
			switch(event) {
			case CACHE_SCAN_DU:
				PromptDialog.dimissProgressDialog();
				tvCache.setText(event.getValue());
				break;
			case CACHE_CLEAR:
				PromptDialog.dimissProgressDialog();
				Toast.makeText(this, R.string.endCleanCache, Toast.LENGTH_LONG).show();
				ParallelManager.getInstance().submitTask(new DiscScanTask(SETTING_CACHE_SCAN_DU, cacheFold));
				break;
			default:
				break;
			}
		} 

		super.onEventMainThread(_event);
	}
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()) {
		case R.id.rl_settings_cache:
			PromptDialog.showProgressDialog(this);
			ParallelManager.getInstance().submitTask(new DiskCleanTask(SETTING_CACHE_CLEAR, cacheFold));
			break;
		}
		super.onClick(v);
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
	public int subContentViewId() {
		// TODO Auto-generated method stub
		return R.layout.activity_settings;
	}

}
