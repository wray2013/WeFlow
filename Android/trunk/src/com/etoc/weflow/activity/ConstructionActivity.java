package com.etoc.weflow.activity;

import com.etoc.weflow.R;

import android.os.Bundle;
import android.os.Message;

public class ConstructionActivity extends TitleRootActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		hideRightButton();
	}
	
	@Override
	public boolean handleMessage(Message arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int subContentViewId() {
		// TODO Auto-generated method stub
		return R.layout.activity_construction;
	}

}
