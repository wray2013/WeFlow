package com.etoc.weflow.web;

import com.etoc.weflow.dialog.PromptDialog;

import android.app.Activity;
import android.graphics.Bitmap;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

public class MyWebViewClient extends WebViewClient {
	
	public static final String TAG = "MyWebViewClient";
	private Activity activity;
	
	public MyWebViewClient(Activity activity) {
		this.activity = activity;
	}
	
	@Override
	public boolean shouldOverrideUrlLoading(WebView view, String url) {
		view.loadUrl(url);
		Log.d(TAG, "shouldOverrideUrlLoading url=" + url);
		return true;
	}

	// 开始加载网页时要做的工作
	@Override
	public void onPageStarted(WebView view, String url, Bitmap favicon) {
//		super.onPageStarted(view, url, favicon);
		Log.d(TAG, "onPageStarted");
		PromptDialog.showProgressDialog(activity);
//		PrintAlertUtil.showDialog(activity, "正在加载...");
	}

	// 加载完成时要做的工作
	@Override
	public void onPageFinished(WebView view, String url) {
//		super.onPageFinished(view, url);
		Log.d(TAG, "onPageFinished");
		PromptDialog.dismissDialog();
//		PrintAlertUtil.dismissDialog();
	}

	// 加载错误时要做的工作
	@Override
	public void onReceivedError(WebView view, int errorCode,
			String description, String failingUrl) {
		Log.d(TAG, "error=" + description);
//		PrintAlertUtil.dismissDialog();
		Toast.makeText(activity, errorCode + "/" + description,
				Toast.LENGTH_LONG).show();
	}

}
