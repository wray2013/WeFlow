package com.cmmobi.looklook.activity;

import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import cn.zipper.framwork.core.ZActivity;
import cn.zipper.framwork.core.ZViewFinder;

import com.cmmobi.looklook.R;
import com.cmmobi.looklook.common.utils.CmmobiClickAgentWrapper;
import com.cmmobi.looklook.common.utils.UmengclickAgentWrapper;

public class LoginAgreementActivity extends ZActivity {
	private WebView wv_agreement; 
	private Button btn_back;
	
	@Override
	public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_agreement);
        
        ZViewFinder finder = getZViewFinder();
        btn_back = finder.findButton(R.id.btn_login_reg_back);
        wv_agreement = (WebView) this.findViewById(R.id.wv_activity_login_register_agreement);
        
        btn_back.setOnClickListener(this);
        
        wv_agreement.loadUrl("file:///android_asset/agreement.html");
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
	public boolean handleMessage(Message msg) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()){
		case R.id.btn_login_reg_back:
			finish();
			break;
		}

	}

}
