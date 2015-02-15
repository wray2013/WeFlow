package com.cmmobi.railwifi.activity;

import com.cmmobi.railwifi.R;
import com.cmmobi.railwifi.network.GsonResponseObject;
import com.cmmobi.railwifi.network.Requester;

import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class TipActivity extends TitleRootActivity{
	TextView tv_tip_title;
	TextView tv_content;
	private RelativeLayout rlNoNet;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setTitleText("温馨提示");
		
		rightButton.setVisibility(View.GONE);
		rlNoNet = (RelativeLayout) findViewById(R.id.rl_no_network);
		tv_tip_title = (TextView)findViewById(R.id.tv_tip_title);
		tv_content = (TextView)findViewById(R.id.tv_tip_content);
		
		Requester.requestPrompt(handler);
		
//		tv_tip_title.setText(R.string.tip_title);
//		tv_content.setText(R.string.tip_content);
		
	}

	@Override
	public boolean handleMessage(Message msg) {
		// TODO Auto-generated method stub
		switch(msg.what){
		case Requester.RESPONSE_TYPE_PROMPT:
			GsonResponseObject.commonContent r6 = (GsonResponseObject.commonContent)msg.obj;
			if(r6!=null && "0".equals(r6.status)){
				tv_tip_title.setText(r6.title);
				tv_content.setText(r6.content);
				rlNoNet.setVisibility(View.GONE);
			}else{
				rlNoNet.setVisibility(View.VISIBLE);
			}
			break;
		}
		return false;
	}

	@Override
	public int subContentViewId() {
		// TODO Auto-generated method stub
		return R.layout.activity_railservice_tip;
	}
	
	

}
