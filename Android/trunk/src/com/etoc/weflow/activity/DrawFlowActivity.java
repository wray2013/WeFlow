package com.etoc.weflow.activity;

import com.etoc.weflow.R;

import android.os.Bundle;
import android.os.Message;

/**
 * 取流量币
 * @author Ray
 *
 */
public class DrawFlowActivity extends TitleRootActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		initView();
	}
	
	private void initView() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean handleMessage(Message msg) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int subContentViewId() {
		// TODO Auto-generated method stub
		return R.layout.activity_draw_flow;
	}

}
