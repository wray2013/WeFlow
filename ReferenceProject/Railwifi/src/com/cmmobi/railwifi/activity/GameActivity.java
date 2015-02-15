package com.cmmobi.railwifi.activity;


import com.cmmobi.railwifi.R;
import com.cmmobi.railwifi.dialog.PromptDialog;
import com.cmmobi.railwifi.dialog.XProcessDialog;
import com.cmmobi.railwifi.utils.CmmobiClickAgentWrapper;

import android.os.Bundle;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.webkit.WebSettings.PluginState;

public class GameActivity extends TitleRootActivity {
	WebView wv_game;
	
//	XProcessDialog dialog;
	
	@Override
	public void onCreate(Bundle savedInstanceState){
//		requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		super.onCreate(savedInstanceState);
		CmmobiClickAgentWrapper.onEvent(this, "empty_1", "1");
		setTitleText("玩游戏");
		hideRightButton();
		
//		dialog = new XProcessDialog(this);
//		dialog.show();
		PromptDialog.showProgressDialog(this);
		wv_game = (WebView) findViewById(R.id.wv_game);
		wv_game.getSettings().setJavaScriptEnabled(true);  
		
		wv_game.setLayerType(WebView.LAYER_TYPE_SOFTWARE, null);
		wv_game.getSettings().setPluginState(PluginState.ON);
		wv_game.setWebViewClient(new WebViewClient(){       
            public boolean shouldOverrideUrlLoading(WebView view, String url) {       
                view.loadUrl(url);       
                return true;       
                
            }
            @Override
            public void onPageFinished(WebView view, String url) {
            	// TODO Auto-generated method stub
//            	Log.d("=AAA=","onPageFinished ");
//            	hideSmoothProgressBar();
//            	dialog.dismiss();
            	PromptDialog.dimissProgressDialog();
            	super.onPageFinished(view, url);
            }
            
            @Override
            public void onReceivedError(WebView view, int errorCode,
            		String description, String failingUrl) {
            	// TODO Auto-generated method stub
//            	hideSmoothProgressBar();
//            	dialog.dismiss();
            	PromptDialog.dimissProgressDialog();
            	super.onReceivedError(view, errorCode, description, failingUrl);
            }
            
		});
		wv_game.loadUrl("http://gm.iluokuang.cn:8080/games/index.html");
		
	}

	@Override
	public boolean handleMessage(Message msg) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int subContentViewId() {
		// TODO Auto-generated method stub
		return R.layout.activity_game;
	}
	
	public boolean onKeyDown(int keyCode, KeyEvent event) {       
        if ((keyCode == KeyEvent.KEYCODE_BACK) && wv_game.canGoBack()) {       
        	wv_game.goBack();       
        	return true;       
        }       
        return super.onKeyDown(keyCode, event);       
    }
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_title_left:
			if(wv_game.canGoBack()){
				wv_game.goBack(); 
			}else{
				this.finish();
			}

			break;
		default:
			break;
		}
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

}
