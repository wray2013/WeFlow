package com.etoc.weflow.activity;

import com.etoc.weflow.R;

import android.os.Message;

public class MakeFlowActivity extends TitleRootActivity {

	@Override
	public boolean handleMessage(Message msg) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int subContentViewId() {
		// TODO Auto-generated method stub
		return R.layout.activity_make_flow;
	}
	
	@Override
	protected int graviteType() {
		// TODO Auto-generated method stub
		return GRAVITE_LEFT;
	}
	
}
