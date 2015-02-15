package com.cmmobi.looklook.activity;

import java.util.ArrayList;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import cn.zipper.framwork.core.ZActivity;

import com.cmmobi.looklook.R;
import com.cmmobi.looklook.common.utils.CmmobiClickAgentWrapper;
import com.cmmobi.looklook.common.utils.UmengclickAgentWrapper;

public class AlsoPublishToActivity extends ZActivity {
	
	public static final int ACTIVITY_RESULT = 10010;
	
	private String names;

//	private ImageView sinaFriends;
//	private ImageView tencentFriends;
//	private ImageView renrenFriends;
	private ImageView altFriends;
	private EditText edit;
	
	@Override
	public boolean handleMessage(Message msg) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_also_publish_to);
		names = new String();
//		sinaFriends = (ImageView)findViewById(R.id.iv_sina);
//		tencentFriends = (ImageView)findViewById(R.id.iv_tencent);
//		renrenFriends = (ImageView)findViewById(R.id.iv_renren);
		altFriends = (ImageView)findViewById(R.id.alt_friends);
		edit = (EditText) findViewById(R.id.edit);
		
//		sinaFriends.setOnClickListener(this);
//		tencentFriends.setOnClickListener(this);
//		renrenFriends.setOnClickListener(this);
		altFriends.setOnClickListener(this);
		
	}

	@Override
	public void onClick(View v) {
		Intent inte = new Intent();
		switch (v.getId()) {
//		case R.id.iv_sina:
//			inte.setClass(AlsoPublishToActivity.this, SinaFriendWeiboActivity.class);
//			startActivity(inte);
//			break;
//		case R.id.iv_tencent:
//			inte.setClass(AlsoPublishToActivity.this, TencentFriendWeiboActivity.class);
//			startActivity(inte);
//			break;
//		case R.id.iv_renren:
//			inte.setClass(AlsoPublishToActivity.this, RenrenFriendWeiboActivity.class);
//			startActivity(inte);
//			break;
		case R.id.alt_friends:
			inte.setClass(AlsoPublishToActivity.this, AltFriendActivity.class);			
			startActivityForResult(inte, ACTIVITY_RESULT);
			break;
		default:
			break;
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(resultCode == 10000){
			ArrayList<String> name_list = data.getStringArrayListExtra("FRIEDS_NAME_LIST");
			if (name_list != null) {
				for (int i = 0; i < name_list.size(); i++) {
					names += "@" + name_list.get(i);
				}
			}
			edit.setText(edit.getText().toString() + names);
		}
	}
	
	@Override
	public void onResume() {
		super.onResume();
		UmengclickAgentWrapper.onResume(this);
		CmmobiClickAgentWrapper.onResume(this);
	}
	
	@Override
	public void onPause() {
		super.onPause();
		UmengclickAgentWrapper.onPause(this);
		CmmobiClickAgentWrapper.onPause(this);
	}
	
	@Override
	public void onStop(){
		super.onStop();
		CmmobiClickAgentWrapper.onStop(this);
	}

}
