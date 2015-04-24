package com.etoc.weflow.activity;

import com.etoc.weflow.R;
import com.etoc.weflow.WeFlowApplication;
import com.etoc.weflow.web.MyWebViewClient;
import com.google.gson.Gson;

import android.os.Handler.Callback;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.View.OnClickListener;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.webkit.WebView;
import android.widget.Toast;

/**
 * Html5游戏页
 * 2015-04-21
 * @author Ray
 *
 */
public class Html5GameWebViewActivity extends TitleRootActivity implements
		OnClickListener, Callback {

	public static final int ACTION_REFRESH_DOWNLOAD = 0x19861024;

	private String pageurl, pagetitle;
	protected DisplayMetrics dm = new DisplayMetrics();

	private Handler myHandler;

	// WebView
	private WebView webview;
	private MyWebViewClient myWebClient;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		myHandler = new Handler(this);
		// userID =
		// ActiveAccount.getInstance(MainApplication.getAppInstance()).getUID();

		Bundle bundle = getIntent().getExtras();
		if(bundle != null) {
			String url = bundle.getString("pageurl");
			if (url != null && !url.equals("")) {
				pageurl = url;
			}
			
			String title = bundle.getString("pagetitle");
			if (title != null && !title.equals("")) {
				pagetitle = title;
			}
		}
		initView();

		WeFlowApplication.getAppInstance().addActivity(this);
	}

	private void initView() {
		if(pagetitle != null && !pagetitle.equals("")) {
			setTitleText(pagetitle);
		}
		hideRightButton();
		// TODO Auto-generated method stub
		getWindowManager().getDefaultDisplay().getMetrics(dm);

		webview = (WebView) findViewById(R.id.wv_page);

		WebChromeClient wvcc = new WebChromeClient() {
			@Override
			public void onReceivedTitle(WebView view, String title) {
				super.onReceivedTitle(view, title);
				if(pagetitle == null || pagetitle.equals("")) {
					setTitleText(title);
				}
			}

		};
		// 设置setWebChromeClient对象
		webview.setWebChromeClient(wvcc);

		myWebClient = new MyWebViewClient(this);
		webview.setWebViewClient(myWebClient);
		webview.addJavascriptInterface(interfaceObj, "h5test");
		setConfig(webview);
		/*webview.getSettings().setBuiltInZoomControls(true);
		webview.getSettings().setLayoutAlgorithm(LayoutAlgorithm.SINGLE_COLUMN);
//		webview.getSettings().setUseWideViewPort(true); 
//		webview.getSettings().setLoadWithOverviewMode(true);
		webview.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
		webview.getSettings().setJavaScriptEnabled(true);
		webview.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
		webview.loadUrl("http://www.jiessie.net/series/index.html""file:///sdcard/EToCDownload/html5test.html");*/
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		myHandler.sendEmptyMessage(ACTION_REFRESH_DOWNLOAD);
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
	}
	
	private void setConfig(WebView wv) {
		// Configure the webview
	    WebSettings s = wv.getSettings();
	    s.setBuiltInZoomControls(true);
	    s.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
	    s.setSavePassword(true);
	    s.setSaveFormData(true);
	    s.setJavaScriptEnabled(true);
	    wv.loadUrl("http://www.jiessie.net/series/index.html");
//	    wv.loadUrl("file:///sdcard/EToCDownload/html5test.html");
	    // enable navigator.geolocation 
//	    s.setGeolocationEnabled(true);
//	    s.setGeolocationDatabasePath("http://www.jiessie.net/series/index.html");
	    
	    // enable Web Storage: localStorage, sessionStorage
	    s.setDomStorageEnabled(true);
	}
	
	private static class gameUserinfo {
		public int gold;
		public int B;
		public int C;
		public int D;
		public int type;
	}
	
	private Object interfaceObj = new Object() {
		
		public void getAppUserInfo() {
			
			gameUserinfo uinfo = new gameUserinfo();
			uinfo.gold = 8888;
			uinfo.B = 5000;
			uinfo.C = 10000;
			uinfo.D = 2;
			uinfo.type = 3;
			String jsonStr = new Gson().toJson(uinfo);
			
			//发送xxx给h5接口javacalljswithargs
			webview.loadUrl("javascript:calBackAppUserInfo('" + jsonStr + "')");
		}
		
		public void updateAppUserGold(final String gold) {
			myHandler.post(new Runnable() {
				
				@Override
				public void run() {
					
					String[] infos = gold.split("=");
					int type = 0;
					int money = 0;
					try {
						type = Integer.parseInt(infos[0]);
						money = Integer.parseInt(infos[1]);
					} catch (Exception e) {
						
					}
					// TODO Auto-generated method stub
					Toast.makeText(Html5GameWebViewActivity.this, "updateAppUserGold type = " + type + ", money = " + money, Toast.LENGTH_LONG).show();
				}
			});
		}
		
		public void closeGame() {
			myHandler.post(new Runnable() {
				
				@Override
				public void run() {
					// TODO Auto-generated method stub
					Toast.makeText(Html5GameWebViewActivity.this, "You want to close webview ? No way!", Toast.LENGTH_LONG).show();
				}
			});
		}
		
	};

	@Override
	public boolean handleMessage(Message msg) {
		// TODO Auto-generated method stub
		switch (msg.what) {

		}
		return false;
	}

	@Override
	public int subContentViewId() {
		// TODO Auto-generated method stub
		return R.layout.activity_webview;
	}

}
