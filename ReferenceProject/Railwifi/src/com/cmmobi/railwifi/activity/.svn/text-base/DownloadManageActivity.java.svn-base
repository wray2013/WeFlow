package com.cmmobi.railwifi.activity;

import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;

import com.cmmobi.railwifi.R;

public class DownloadManageActivity extends TitleRootActivity {

	private RelativeLayout rlDownloadingTop = null;
	private RelativeLayout rlDownloadDoneTop = null;
	private RelativeLayout rlDownloadingLayout = null;
	private RelativeLayout rlDownloadDoneLayout = null;
	private ListView lvDownloading = null;
	private ListView lvDownloadDone = null;
	private View lineBottom = null;
	private View viewTopSpace = null;
	private boolean showDownloading = true;
	
	@Override
	public boolean handleMessage(Message msg) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int subContentViewId() {
		// TODO Auto-generated method stub
		return R.layout.activity_download_manage;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		initViews();
	}
	
	private void initViews() {
		setTitleText("下载管理");
		setRightButtonText("清理");
		rlDownloadDoneLayout = (RelativeLayout) findViewById(R.id.rl_download_done);
		rlDownloadingLayout = (RelativeLayout) findViewById(R.id.rl_downloading);
		rlDownloadingTop = (RelativeLayout) findViewById(R.id.rl_top_downloading);
		rlDownloadDoneTop = (RelativeLayout) findViewById(R.id.rl_top_download_done);
		lvDownloading = (ListView) findViewById(R.id.lv_donwloading);
		lvDownloadDone = (ListView) findViewById(R.id.lv_donwload_done);
		lineBottom = findViewById(R.id.view_bottom_donwloading_line);
		viewTopSpace = findViewById(R.id.view_top_space);
		rlDownloadDoneTop.setOnClickListener(this);
		rlDownloadingTop.setOnClickListener(this);
		
		switchDownloadList();
	}
	
	private void switchDownloadList() {
		if (showDownloading) {
			lvDownloadDone.setVisibility(View.GONE);
			lvDownloading.setVisibility(View.VISIBLE);
			lineBottom.setVisibility(View.VISIBLE);
			RelativeLayout.LayoutParams params = (LayoutParams) rlDownloadDoneLayout.getLayoutParams();
			params.addRule(RelativeLayout.BELOW, -1);
			rlDownloadDoneLayout.setLayoutParams(params);
		} else {
			lvDownloadDone.setVisibility(View.VISIBLE);
			lvDownloading.setVisibility(View.GONE);
			lineBottom.setVisibility(View.GONE);
			RelativeLayout.LayoutParams params = (LayoutParams) rlDownloadDoneLayout.getLayoutParams();
			params.addRule(RelativeLayout.BELOW,R.id.view_top_space);
			rlDownloadDoneLayout.setLayoutParams(params);
		}
		showDownloading = !showDownloading;
	}
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.rl_top_downloading:
			if (showDownloading) {
				switchDownloadList();
			}
			break;
		case R.id.rl_top_download_done:
			if (!showDownloading) {
				switchDownloadList();
			}
			break;
		default:
			break;
		}
		super.onClick(v);
	}

}
