package com.cmmobi.looklook.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import cn.zipper.framwork.core.ZActivity;

import com.cmmobi.looklook.R;
import com.cmmobi.looklook.common.utils.CmmobiClickAgentWrapper;
import com.cmmobi.looklook.common.utils.UmengclickAgentWrapper;

public class SettingRemarkNameActivity extends ZActivity{

	private final String TAG = this.getClass().getSimpleName();
	private ImageView clearBtn = null;
	private EditText editText = null;
	private ImageView confirmBtn = null;
	private ImageView backBtn = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_alter_remarkname);
	
		clearBtn = (ImageView) findViewById(R.id.iv_setting_remarkname_clear);
		clearBtn.setOnClickListener(this);
		
		editText = (EditText) findViewById(R.id.ed_remark_name);
		confirmBtn = (ImageView) findViewById(R.id.iv_edit_done);
		backBtn = (ImageView) findViewById(R.id.iv_back);
		confirmBtn.setOnClickListener(this);
		backBtn.setOnClickListener(this);
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
	public void onStop() {
		super.onStop();
		CmmobiClickAgentWrapper.onStop(this);
	}

	@Override
	public boolean handleMessage(Message arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch(arg0.getId()) {
		case R.id.iv_setting_remarkname_clear:
			Log.d(TAG,"iv_setting_remarkname_clear");
			editText.setText("");
			break;
		case R.id.iv_back:
			Log.d(TAG,"iv_back");
			finish();
			break;
		case R.id.iv_edit_done:
			Log.d(TAG,"iv_edit_done");
			if (editText.getText() != null && !"".equals(editText.getText().toString())) {
				Intent intent=new Intent();  
	            intent.putExtra("remarkname", editText.getText().toString());  
	            setResult(RESULT_OK, intent);  
			}
			
			finish();
			break;
		}
	}

}
