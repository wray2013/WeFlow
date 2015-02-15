package com.cmmobi.looklook.activity;

import com.cmmobi.looklook.fragment.CollectionsFragment;

import android.os.Bundle;
import android.os.Message;

public class CollectionsActivity extends TitleRootFragmentActivity {

	@Override
	public boolean handleMessage(Message msg) {
		return false;
	}

	@Override
	public int subContentViewId() {
		return 0;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		 getSupportFragmentManager().beginTransaction()
	         .replace(android.R.id.content, new CollectionsFragment())
	         .commit();
	}

}
