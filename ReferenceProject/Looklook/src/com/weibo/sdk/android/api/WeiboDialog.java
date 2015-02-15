package com.weibo.sdk.android.api;


import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.http.SslError;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.webkit.SslErrorHandler;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import cn.zipper.framwork.core.ZDialog;

import com.cmmobi.looklook.R;
import com.cmmobi.sns.oauth.OAuth;
import com.cmmobi.sns.oauthv2.OAuthV2;
import com.cmmobi.sns.oauthv2.OAuthV2Client;
import com.cmmobi.sns.utils.ConfigUtil;
import com.cmmobi.sns.utils.Utility;


/**
 * 用来显示用户认证界面的dialog，封装了一个webview，通过redirect地址中的参数来获取accesstoken
 * @author zhangwei
 *
 */
public class WeiboDialog extends Dialog {
    
	static  FrameLayout.LayoutParams FILL = new FrameLayout.LayoutParams(
			ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.FILL_PARENT);
	private static boolean sendOnComplete = false;
	private String mUrl;
	private WeiboAuthListener mListener;
	private WebView mWebView;
	private RelativeLayout webViewContainer;
	private RelativeLayout mContent;
	private Context mContext;
	private  OAuthV2 oAuth;
	private int mCurWeiboIndex;

	private final static String TAG = "Weibo-WebView";
	
	public WeiboDialog(Context context, String url,  OAuth oAuth, WeiboAuthListener listener) {
		//super(context,theme);
		super(context, R.style.ContentOverlay);
		this.mContext = context;
		this.oAuth = (OAuthV2) oAuth;
		this.mCurWeiboIndex = oAuth.getCurWeiboIndex();
		mUrl = url;
		this.mListener = listener;
		sendOnComplete = false;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ZDialog.show(R.layout.progressdialog, false, true, getContext());

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		//this.getWindow().setFeatureDrawableAlpha(Window.FEATURE_OPTIONS_PANEL, 0);  
		mContent = new RelativeLayout(getContext());
		setUpWebView();

		addContentView(mContent, new LayoutParams(LayoutParams.FILL_PARENT,
				LayoutParams.FILL_PARENT));
	}

	protected void onBack() {
		try {
			ZDialog.dismiss();
			if (null != mWebView) {
				mWebView.stopLoading();
				mWebView.destroy();
			}
		} catch (Exception e) {
		}
		dismiss();
	}

	@SuppressLint("SetJavaScriptEnabled")
	private void setUpWebView() {
		webViewContainer = new RelativeLayout(getContext());
		mWebView = new WebView(getContext());
		mWebView.setVerticalScrollBarEnabled(false);
		mWebView.setHorizontalScrollBarEnabled(false);
		mWebView.getSettings().setJavaScriptEnabled(true);
		mWebView.getSettings().setSaveFormData(true);
		mWebView.getSettings().setSavePassword(true);
		mWebView.setWebViewClient(new WeiboDialog.WeiboWebViewClient());
		mWebView.loadUrl(mUrl);
		mWebView.setLayoutParams(FILL);
		mWebView.setVisibility(View.INVISIBLE);
		webViewContainer.addView(mWebView);
		RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(LayoutParams.FILL_PARENT,
				LayoutParams.FILL_PARENT);
		
        mContent.addView(webViewContainer, lp);
	}

	private class WeiboWebViewClient extends WebViewClient {

		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			Log.d(TAG, "Redirect URL: " + url);
			if (url.startsWith(ConfigUtil.getInstance().getRedirectUrl())) {
				handleRedirectUrl(view, url);
				try{
					WeiboDialog.this.dismiss();
				}catch(Exception e){
					e.printStackTrace();
				}

				return true;
			}
			view.loadUrl(url);
			return true;
		}
		
		
		private void handleRedirectUrl(WebView view, String url) {
			Bundle values = Utility.parseUrl(url);

			String error = values.getString("error");
			String error_code = values.getString("error_code");

			if (error == null && error_code == null) {
				//mListener.onComplete(mCurWeiboIndex);
	            if (url.indexOf("access_token=") != -1) {            	 
	                int start=url.indexOf("access_token=");
	                String responseData=url.substring(start);
	                Log.e(TAG, "responseData:" + responseData);
	                Boolean isSuccess = OAuthV2Client.parseAccessTokenAndOpenId(mContext,responseData, oAuth);
	                if(isSuccess){
	                	if(!sendOnComplete){
	 	               	   mListener.onComplete(mCurWeiboIndex);
		               	   sendOnComplete = true;
	                	}

	                }else{
	               	   mListener.onWeiboException(new WeiboException("认证出错", 0), mCurWeiboIndex);
	                }
					 
					return;
	            }
			} else if (error.equals("access_denied")) {

				mListener.onCancel(mCurWeiboIndex);
			} else if (error.equals("login_denied")) {

				mListener.onCancel(mCurWeiboIndex);
			} else {
				if(error_code!=null){
					mListener.onWeiboException(new WeiboException(error, Integer.parseInt(error_code)), mCurWeiboIndex);
				}else{
					mListener.onCancel(mCurWeiboIndex);
				}
				
			}
		}
		

		@Override
		public void onReceivedError(WebView view, int errorCode, String description,
				String failingUrl) {
			super.onReceivedError(view, errorCode, description, failingUrl);
			mListener.onError(new WeiboDialogError(description, errorCode, failingUrl),mCurWeiboIndex);
			WeiboDialog.this.dismiss();
		}
		
		
		@Override
		public void onPageStarted(WebView view, String url, Bitmap favicon) {
			 Log.d(TAG, "onPageStarted URL: " + url);
			 ZDialog.show(R.layout.progressdialog, false, true, getContext());
             if (url.indexOf("access_token=") != -1) {            	 
                 int start=url.indexOf("access_token=");
                 String responseData=url.substring(start);
                 Log.e(TAG, "responseData:" + responseData);
                 Boolean isSuccess = OAuthV2Client.parseAccessTokenAndOpenId(mContext,responseData, oAuth);
                 if(isSuccess){
                	 if(!sendOnComplete){
                    	 mListener.onComplete(mCurWeiboIndex);
                    	 sendOnComplete = true;
                	 }

                 }else{
                	 mListener.onWeiboException(new WeiboException("认证出错", 0), mCurWeiboIndex);
                 }
                 view.stopLoading();
 				 WeiboDialog.this.dismiss();
 				 
 				 return;
             } 
             
             if (url.startsWith(ConfigUtil.getInstance().getRedirectUrl())) {
				handleRedirectUrl(view, url);
				view.stopLoading();
				WeiboDialog.this.dismiss();
				return;
			 }
 			
             super.onPageStarted(view, url, favicon);
		}

		@Override
		public void onPageFinished(WebView view, String url) {
			Log.d(TAG, "onPageFinished URL: " + url);
			ZDialog.dismiss();
			super.onPageFinished(view, url);

			mContent.setBackgroundColor(Color.TRANSPARENT);
			webViewContainer.setBackgroundResource(android.R.color.white);
			mWebView.setVisibility(View.VISIBLE);
		}

		public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
			handler.proceed();
		}

	}

}
