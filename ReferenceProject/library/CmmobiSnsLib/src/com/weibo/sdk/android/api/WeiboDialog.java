package com.weibo.sdk.android.api;

import java.io.IOException;
import java.io.InputStream;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.NinePatchDrawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Xml;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.cmmobi.sns.oauth.OAuth;
import com.cmmobi.sns.oauthv2.OAuthV2;
import com.cmmobi.sns.oauthv2.OAuthV2Client;
import com.cmmobi.sns.utils.ConfigUtil.SHARE_TO;
import com.cmmobi.sns.utils.Utility;
/**
 * 用来显示用户认证界面的dialog，封装了一个webview，通过redirect地址中的参数来获取accesstoken
 * @author xudongsheng
 *
 */
public class WeiboDialog extends Dialog {
    
	static  FrameLayout.LayoutParams FILL = new FrameLayout.LayoutParams(
			ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.FILL_PARENT);
	private String mUrl;
	private WeiboAuthListener mListener;
	private ProgressDialog mSpinner;
	private WebView mWebView;
	private RelativeLayout webViewContainer;
	private RelativeLayout mContent;
	private Context mContext;
	private  OAuthV2 oAuth;
	private int mCurWeiboIndex;

	private final static String TAG = "Weibo-WebView";
	
	private static int theme=android.R.style.Theme_Translucent_NoTitleBar;
	private  static int left_margin=0;
    private  static int top_margin=0;
    private  static int right_margin=0;
    private  static int bottom_margin=0;
	public WeiboDialog(Context context, String url,  OAuth oAuth, WeiboAuthListener listener) {
		super(context,theme);
		this.mContext = context;
		this.oAuth = (OAuthV2) oAuth;
		this.mCurWeiboIndex = oAuth.getCurWeiboIndex();
		mUrl = url;
		this.mListener = listener;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mSpinner = new ProgressDialog(getContext());
		mSpinner.requestWindowFeature(Window.FEATURE_NO_TITLE);
		mSpinner.setMessage("Loading...");
		mSpinner.setOnKeyListener(new OnKeyListener() {

			@Override
			public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
				onBack();
				return false;
			}

		});
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.getWindow().setFeatureDrawableAlpha(Window.FEATURE_OPTIONS_PANEL, 0);  
		mContent = new RelativeLayout(getContext());
		setUpWebView();

		addContentView(mContent, new LayoutParams(LayoutParams.FILL_PARENT,
				LayoutParams.FILL_PARENT));
	}

	protected void onBack() {
		try {
			mSpinner.dismiss();
			if (null != mWebView) {
				mWebView.stopLoading();
				mWebView.destroy();
			}
		} catch (Exception e) {
		}
		dismiss();
	}

	private void setUpWebView() {
		webViewContainer = new RelativeLayout(getContext());
		mWebView = new WebView(getContext());
		mWebView.setVerticalScrollBarEnabled(false);
		mWebView.setHorizontalScrollBarEnabled(false);
		mWebView.getSettings().setJavaScriptEnabled(true);
		mWebView.setWebViewClient(new WeiboDialog.WeiboWebViewClient());
		mWebView.loadUrl(mUrl);
		mWebView.setLayoutParams(FILL);
		mWebView.setVisibility(View.INVISIBLE);
		
		RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(LayoutParams.FILL_PARENT,
				LayoutParams.FILL_PARENT);
		
		RelativeLayout.LayoutParams lp0 = new RelativeLayout.LayoutParams(LayoutParams.FILL_PARENT,
                LayoutParams.FILL_PARENT);
		
        mContent.setBackgroundColor(Color.TRANSPARENT);
        AssetManager asseets=WeiboDialog.this.getContext().getAssets();
        InputStream is=null;
        try {
             try {
               is=asseets.open("weibosdk_dialog_bg.9.png");
               DisplayMetrics dm = this.getContext().getResources()
                       .getDisplayMetrics();
               float density=dm.density;
               lp0.leftMargin =(int) (10*density);
               lp0.topMargin = (int) (10*density);
               lp0.rightMargin =(int) (10*density);
               lp0.bottomMargin = (int) (10*density);
           } catch (Exception e) {
               e.printStackTrace();
           }
//             if(is==null){
//                     webViewContainer.setBackgroundResource(R.drawable.weibosdk_dialog_bg);
//             }
//             else{
                   Bitmap bitmap = BitmapFactory.decodeStream(is);
                   NinePatchDrawable npd=new NinePatchDrawable(bitmap, bitmap.getNinePatchChunk(), new Rect(0,0,0,0), null); 
                   webViewContainer.setBackgroundDrawable(npd);
            // }
        } catch (Exception e) {
            e.printStackTrace();
        }finally{
            if(is!=null){
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
       
		
        webViewContainer.addView(mWebView,lp0);
		webViewContainer.setGravity(Gravity.CENTER);
		
		if(parseDimens()){
		    lp.leftMargin = left_margin;
	        lp.topMargin = top_margin;
	        lp.rightMargin =right_margin;
	        lp.bottomMargin = bottom_margin;
		}
//		else{
//		    Resources resources = getContext().getResources();
//		    lp.leftMargin=resources.getDimensionPixelSize(R.dimen.weibosdk_dialog_left_margin);
//		    lp.rightMargin=resources.getDimensionPixelSize(R.dimen.weibosdk_dialog_right_margin);
//		    lp.topMargin=resources.getDimensionPixelSize(R.dimen.weibosdk_dialog_top_margin);
//		    lp.bottomMargin=resources.getDimensionPixelSize(R.dimen.weibosdk_dialog_bottom_margin);
//		}
        mContent.addView(webViewContainer, lp);
	}

	private class WeiboWebViewClient extends WebViewClient {

		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			Log.d(TAG, "Redirect URL: " + url);
			 if (url.startsWith("sms:")) {  //针对webview里的短信注册流程，需要在此单独处理sms协议
	                Intent sendIntent = new Intent(Intent.ACTION_VIEW);  
	                sendIntent.putExtra("address", url.replace("sms:", ""));  
	                sendIntent.setType("vnd.android-dir/mms-sms");  
	                WeiboDialog.this.getContext().startActivity(sendIntent);  
	                return true;  
	            }  
			return super.shouldOverrideUrlLoading(view, url);
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
             if (url.indexOf("access_token=") != -1) {            	 
                 int start=url.indexOf("access_token=");
                 String responseData=url.substring(start);
                 Boolean isSuccess = OAuthV2Client.parseAccessTokenAndOpenId(mContext,responseData, oAuth);
                 if(isSuccess){
                	 mListener.onComplete(mCurWeiboIndex);
                 }else{
                	 mListener.onWeiboException(new WeiboException("认证出错", 0), mCurWeiboIndex);
                 }
                 view.stopLoading();
 				 WeiboDialog.this.dismiss();
             }
             super.onPageStarted(view, url, favicon);
			 mSpinner.show();
		}

		@Override
		public void onPageFinished(WebView view, String url) {
			Log.d(TAG, "onPageFinished URL: " + url);
			super.onPageFinished(view, url);
			if (mSpinner.isShowing()) {
				mSpinner.dismiss();
			}
			mWebView.setVisibility(View.VISIBLE);
		}

//		public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
//			handler.proceed();
//		}

	}
	private boolean parseDimens(){
	    boolean ret=false;
        AssetManager asseets=this.getContext().getAssets();
        DisplayMetrics dm = this.getContext().getResources()
                .getDisplayMetrics();
        float density=dm.density;
        InputStream is=null;
        try {
            is=asseets.open("values/dimens.xml");
            XmlPullParser xmlpull = Xml.newPullParser();  
            try {
                xmlpull.setInput(is,"utf-8");
                int eventCode = xmlpull.getEventType();  
                ret=true;
                while(eventCode!=XmlPullParser.END_DOCUMENT)  {
                    switch (eventCode)  
                    {  
                    case XmlPullParser.START_TAG:
                        if(xmlpull.getName().equals("dimen")){
                            String name=xmlpull.getAttributeValue(null, "name");
                            if("weibosdk_dialog_left_margin".equals(name)){
                                    String value=xmlpull.nextText();
                                    left_margin=(int)(Integer.parseInt(value)*density);
                            }
                            else if("weibosdk_dialog_top_margin".equals(name)){
                                String value=xmlpull.nextText();
                                top_margin=(int)(Integer.parseInt(value)*density);
                            }
                            else if("weibosdk_dialog_right_margin".equals(name)){
                                String value=xmlpull.nextText();
                                right_margin=(int)(Integer.parseInt(value)*density);
                            }
                            else if("weibosdk_dialog_bottom_margin".equals(name)){
                                String value=xmlpull.nextText();
                                bottom_margin=(int)(Integer.parseInt(value)*density);
                            }
                        }
                        break;
                    }
                    eventCode = xmlpull.next();//没有结束xml文件就推到下个进行解析  
                }
                
            } catch (XmlPullParserException e) {
                e.printStackTrace();
            }
            
        } catch (IOException e) {
            e.printStackTrace();
        }finally{
            if(is!=null){
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return ret;
    }
	
}
