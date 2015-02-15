package com.cmmobi.looklook.activity;

import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;

import com.cmmobi.looklook.R;

public class LoginAgreementActivity extends TitleRootActivity {
	
	@Override
	public int subContentViewId() {
		return R.layout.activity_login_agreement;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        hideRightButton();
        setTitle("协议");
        
        WebView wv_agreement = (WebView) this.findViewById(R.id.wv_activity_login_register_agreement);
        wv_agreement.loadUrl("file:///android_asset/agreement.html");
	}
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()){
		case R.id.btn_title_left:
			finish();
			break;
		}

	}

}
