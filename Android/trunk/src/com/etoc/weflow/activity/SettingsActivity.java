package com.etoc.weflow.activity;

import java.io.File;

import com.etoc.weflow.R;
import com.etoc.weflow.utils.ConStant;
import com.etoc.weflow.utils.FileUtils;
import com.etoc.weflow.utils.VMobileInfo;
import com.etoc.weflow.utils.ViewUtils;
import com.etoc.weflow.view.SwitchButton;
import com.etoc.weflow.view.SwitchButton.OnChangeListener;

import android.os.Bundle;
import android.os.Message;
import android.widget.TextView;
import android.widget.Toast;

public class SettingsActivity extends TitleRootActivity {

	private TextView tvPush, tvCache, tvVersion;
	private SwitchButton switchPush;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		setTitleText("设置");
		hideRightButton();
		
		initView();
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
		
		/*File cacheFile = new File(ConStant.getImageLoaderCachePath());
		long size = FileUtils.getFolderSize(cacheFile);
		String cache = FileUtils.getFormatSize(size);*/
		
		tvCache.setText("24M");
		
		String ver = VMobileInfo.getAppVersionName(this);
		tvVersion.setText("Version " + ver);
		
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
