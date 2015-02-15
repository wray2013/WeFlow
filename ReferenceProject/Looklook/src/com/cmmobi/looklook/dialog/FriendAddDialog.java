package com.cmmobi.looklook.dialog;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.widget.TextView;
import cn.zipper.framwork.core.ZActivity;
import cn.zipper.framwork.core.ZViewFinder;

import com.cmmobi.looklook.R;

public class FriendAddDialog extends ZActivity {
	
	TextView title;
	String snstype;

	@Override
	public boolean handleMessage(Message msg) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		Bundle bundle = null;
		switch (v.getId()) {
		case R.id.btn_friend_add_bindweibo_cancel:
			setResult(RESULT_CANCELED);
			finish();			
			break;	
		case R.id.btn_friend_add_bindweibo_sure:
			bundle = new Bundle();
			bundle.putString("weibo_type", snstype);
			//给bundle 写入数据
			Intent mIntent = new Intent();
			mIntent.putExtras(bundle);
			setResult(RESULT_OK, mIntent);
			finish();		
			break;	
		}

	}
	
	@Override
	public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_friend_add_bind_weibo);
        
        Bundle bundle = getIntent().getExtras();
        snstype = bundle.getString( "weibo_type" );


    	//ZDialog.show(R.layout.dialog_login_main_looklook, true, true, this);
		//ZDialog.getZViewFinder().findButton(id);
		ZViewFinder finder = getZViewFinder();
		title = finder.findTextView(R.id.tv_friend_add_bindweibo_title);
		finder.setOnClickListener(R.id.btn_friend_add_bindweibo_cancel, this);
		finder.setOnClickListener(R.id.btn_friend_add_bindweibo_sure, this);
		
	}

}
