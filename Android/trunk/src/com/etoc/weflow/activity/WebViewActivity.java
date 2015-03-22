package com.etoc.weflow.activity;

import com.etoc.weflow.R;
import com.etoc.weflow.WeFlowApplication;
import com.etoc.weflow.web.MyWebViewClient;

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

public class WebViewActivity extends TitleRootActivity implements
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
		String url = bundle.getString("pageurl");
		if (url != null && !url.equals("")) {
			pageurl = url;
		}
		
		String title = bundle.getString("pagetitle");
		if (title != null && !title.equals("")) {
			pagetitle = title;
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
		webview.getSettings().setBuiltInZoomControls(true);
		webview.getSettings().setLayoutAlgorithm(LayoutAlgorithm.SINGLE_COLUMN);
		webview.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
		webview.getSettings().setJavaScriptEnabled(true);
		webview.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
		webview.loadUrl(pageurl);
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
