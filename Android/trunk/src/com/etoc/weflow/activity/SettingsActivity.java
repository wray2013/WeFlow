package com.etoc.weflow.activity;

import java.io.File;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
import cn.jpush.android.api.JPushInterface;

import com.etoc.weflow.Config;
import com.etoc.weflow.R;
import com.etoc.weflow.WeFlowApplication;
import com.etoc.weflow.dialog.PromptDialog;
import com.etoc.weflow.download.DownloadManager;
import com.etoc.weflow.download.DownloadType;
import com.etoc.weflow.event.ParallelEvent;
import com.etoc.weflow.net.GsonResponseObject.UpdateResp;
import com.etoc.weflow.net.Requester;
import com.etoc.weflow.parallel.DiscScanTask;
import com.etoc.weflow.parallel.DiskCleanTask;
import com.etoc.weflow.parallel.ParallelManager;
import com.etoc.weflow.utils.StringUtils;
import com.etoc.weflow.utils.VMobileInfo;
import com.etoc.weflow.utils.ViewUtils;
import com.nostra13.universalimageloader.api.MyImageLoader;

public class SettingsActivity extends TitleRootActivity {

	private TextView tvPush, tvCache, tvVersion;
	private ToggleButton switchPush;
	private static final long SETTING_CACHE_SCAN_DU = 0x12468321;
	private static final long SETTING_CACHE_CLEAR = 0x12468322;
	File cacheFold;
	
	public static String SP_NAME = "push";
	public static String SP_KEY  = "enabled";
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
		
		WeFlowApplication.getAppInstance().addActivity(this);
	}
	
	private void initView() {
		// TODO Auto-generated method stub
		tvPush = (TextView) findViewById(R.id.tv_settings_push);
		
		tvCache   = (TextView) findViewById(R.id.tv_cache);
		tvVersion = (TextView) findViewById(R.id.tv_upgrade_version);
		
		switchPush = (ToggleButton) findViewById(R.id.toggle_push);
		
		SharedPreferences sp = getSharedPreferences(SP_NAME, MODE_PRIVATE);
		if(sp.getBoolean(SP_KEY, true)) {
			switchPush.setChecked(true);
		} else {
			switchPush.setChecked(false);
		}
		/*switchPush.setOnChangeListener(new OnChangeListener() {
			
			@Override
			public void onChange(SwitchButton sb, boolean state) {
				// TODO Auto-generated method stub
				Toast.makeText(SettingsActivity.this, state ? "开启推送":"关闭推送", Toast.LENGTH_SHORT).show();
			}
		});*/
		switchPush.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				// TODO Auto-generated method stub
//				switchPush.setChecked(isChecked);
				Toast.makeText(SettingsActivity.this, isChecked ? "开启推送":"关闭推送", Toast.LENGTH_SHORT).show();
				SharedPreferences sp = getSharedPreferences(SP_NAME, MODE_PRIVATE);
				sp.edit().putBoolean(SP_KEY, isChecked).commit();
				
				if(isChecked) {
					JPushInterface.resumePush(WeFlowApplication.getAppInstance());
				} else {
					JPushInterface.stopPush(WeFlowApplication.getAppInstance());
				}
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
		case R.id.rl_settings_about:
			Intent aboutIntent = new Intent(this, WebViewActivity.class);
			aboutIntent.putExtra("pageurl", Config.ABOUTPAGE_URL);
			aboutIntent.putExtra("pagetitle", "关于");
			startActivity(aboutIntent);
			break;
		case R.id.rl_settings_upgrade:
			Requester.update(true, handler);
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
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		WeFlowApplication.getAppInstance().removeActivity(this);
	}

	@Override
	public boolean handleMessage(Message msg) {
		// TODO Auto-generated method stub
		switch (msg.what) {
		case Requester.RESPONSE_TYPE_UPDATE:
			if (msg.obj != null) {
				final UpdateResp resp = (UpdateResp) msg.obj;
				if (Requester.isSuccessed(resp.status)) {
					try {
						//已经最新
						if ("0".equals(resp.type)) {
							PromptDialog.Dialog(this, "版本升级", "当前已经是最新版本", "确定");
							//普通升级
						} else if ("1".equals(resp.type)) {
							if(!StringUtils.isEmpty(resp.filepath) && resp.filepath.startsWith("http://")) {
								PromptDialog.Dialog(this, true, true, false, "发现新版本", resp.description, "下载", "取消", new OnClickListener() {
									
									@Override
									public void onClick(DialogInterface dialog, int which) {
										// TODO Auto-generated method stub
										DownloadManager.getInstance().addDownloadTask(resp.filepath, "0", resp.description, "", resp.description,  DownloadType.APP, "", "","","com.etoc.weflow");
									}
								}, null, false, null);
							} else {
								Toast.makeText(this, "下载链接无效", Toast.LENGTH_LONG).show();
							}
							//强制升级
						} else if ("2".equals(resp.type)) {
							if(!StringUtils.isEmpty(resp.filepath) && resp.filepath.startsWith("http://")) {
								PromptDialog.Dialog(this, true, false, false, "发现新版本", resp.description, "下载", "取消", new OnClickListener() {
									
									@Override
									public void onClick(DialogInterface dialog, int which) {
										// TODO Auto-generated method stub
										DownloadManager.getInstance().addDownloadTask(resp.filepath, "0", resp.description, "", resp.description,  DownloadType.APP, "", "","","com.etoc.weflow");
									}
								}, new OnClickListener() {
									
									@Override
									public void onClick(DialogInterface dialog, int which) {
										// TODO Auto-generated method stub
										WeFlowApplication.getAppInstance().cleanAllActivity();
										return;
									}
								}, false, null);
							} else {
								Toast.makeText(this, "下载链接无效", Toast.LENGTH_LONG).show();
							}
						}
					} catch(Exception e) {
						e.printStackTrace();
					}
				}
			}
			break;
		
		}
		return false;
	}

	@Override
	public int subContentViewId() {
		// TODO Auto-generated method stub
		return R.layout.activity_settings;
	}

}
