package com.etoc.weflow.activity;

import com.etoc.weflow.R;
import com.etoc.weflow.WeFlowApplication;
import com.etoc.weflow.activity.login.LoginActivity;
import com.etoc.weflow.dao.AccountInfo;
import com.etoc.weflow.dialog.PromptDialog;
import com.etoc.weflow.net.GsonResponseObject.orderGameResp;
import com.etoc.weflow.net.GsonResponseObject.queryGameParamResp;
import com.etoc.weflow.net.Requester;
import com.etoc.weflow.web.MyWebViewClient;
import com.google.gson.Gson;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Handler.Callback;
import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Toast;

/**
 * Html5游戏页
 * 2015-04-21
 * @author Ray
 *
 */
public class Html5GameWebViewActivity extends TitleRootActivity implements Callback {

	public static final int ACTION_LOAD_GAME = 0x19861024;

	private String pageurl, pagetitle, gameid;
	private queryGameParamResp gameparams = null;
	
	protected DisplayMetrics dm = new DisplayMetrics();

//	private Handler handler;

	// WebView
	private WebView webview;
	private MyWebViewClient myWebClient;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//		handler = new Handler(this);
		// userID =
		// ActiveAccount.getInstance(MainApplication.getAppInstance()).getUID();

		Bundle bundle = getIntent().getExtras();
		if(bundle != null) {
			String url = bundle.getString("pageurl");
			if (!TextUtils.isEmpty(url)) {
				pageurl = url;
			}
			
			String title = bundle.getString("pagetitle");
			if (!TextUtils.isEmpty(title)) {
				pagetitle = title;
			}
			
			String id = bundle.getString("gameid");
			if (!TextUtils.isEmpty(id)) {
				gameid = id;
			}
			
			String params = bundle.getString("gameparam");
			if (!TextUtils.isEmpty(params)) {
				try {
					gameparams = new Gson().fromJson(params, queryGameParamResp.class);
				} catch(Exception e) {
					e.printStackTrace();
				}
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
		
		//参数错误
		if(gameparams == null) {
			
		}
		
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
		
		handler.sendEmptyMessage(ACTION_LOAD_GAME);
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
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
//	    wv.loadUrl("http://www.jiessie.net/series/index.html");
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
			float gold = 0;
			int b = 0;
			int c = 0;
			int d = 0;
			try {
				gold = Float.parseFloat(WeFlowApplication.getAppInstance().getAccountInfo().getFlowcoins());
				b = Integer.parseInt(gameparams.rangea);
				c = Integer.parseInt(gameparams.rangeb);
				d = Integer.parseInt(gameparams.amendment);
			} catch(Exception e) {
				e.printStackTrace();
			}
			uinfo.gold = (int) gold;
			uinfo.B = b;
			uinfo.C = c;
			uinfo.D = d;
			uinfo.type = 3;
			String jsonStr = new Gson().toJson(uinfo);
			
			//发送xxx给h5接口javacalljswithargs
			webview.loadUrl("javascript:calBackAppUserInfo('" + jsonStr + "')");
		}
		
		public void updateAppUserGold(final String gold) {
			handler.post(new Runnable() {
				
				@Override
				public void run() {
					
					String[] infos = gold.split("=");
					int type = 0;
					int money = 0;
					try {
						type = Integer.parseInt(infos[0]);
						money = Integer.parseInt(infos[1]);
						Requester.orderGame(false, handler, WeFlowApplication.getAppInstance().getAccountInfo().getUserid(), gameid, type + "", money + "");
						Toast.makeText(Html5GameWebViewActivity.this, "UpdateAppUserGold type = " + type + ", money = " + money, Toast.LENGTH_LONG).show();
					} catch (Exception e) {
						Toast.makeText(Html5GameWebViewActivity.this, "Something unexpected occured!", Toast.LENGTH_LONG).show();
						e.printStackTrace();
					}
				}
			});
		}
		
		public void closeGame() {
			handler.post(new Runnable() {
				
				@Override
				public void run() {
					// TODO Auto-generated method stub
					Toast.makeText(Html5GameWebViewActivity.this, "You want to close webview ?", Toast.LENGTH_LONG).show();
					finish();
				}
			});
		}
		
	};
	
	@Override
	protected void onDestroy() {
		webview.loadUrl("");//规避退出游戏时音频设备为关闭的BUG
		super.onDestroy();
	};

	@Override
	public boolean handleMessage(Message msg) {
		// TODO Auto-generated method stub
		switch (msg.what) {
		case ACTION_LOAD_GAME:
			//判断gameurl、gameid、gameparam合法性
			if(TextUtils.isEmpty(pageurl) ||
					!pageurl.startsWith("http") ||
					TextUtils.isEmpty(gameid) ||
					gameparams == null) {
				Toast.makeText(this, "游戏参数读取失败，请尝试刷新", Toast.LENGTH_LONG).show();
				break;
			}
			//判断当前是否登录
			if (checkUserid()) {
				//开始加载游戏
				webview.loadUrl(pageurl);
			}

			break;
		case Requester.RESPONSE_TYPE_ORDER_GAME:
			if(msg.obj != null) {
				orderGameResp resp = (orderGameResp) msg.obj;
				if("0000".equals(resp.status) || "0".equals(resp.status)) {
					String coins = resp.flowcoins;
					WeFlowApplication.getAppInstance().setFlowCoins(coins);
					Toast.makeText(this, "成功获取" + coins + "流量币", Toast.LENGTH_LONG).show();
				}
			}
			break;
		}
		return false;
	}
	
	private boolean checkUserid() {
		AccountInfo info = WeFlowApplication.getAppInstance().getAccountInfo();
		if (info == null || info.getUserid() == null
				|| info.getUserid().equals("")) {
			PromptDialog.Dialog(this, true, "温馨提示", "您未登录，无法获取流量币，是否继续游戏？",
					"继续游戏", "现在登录", new OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int arg1) {
							// TODO Auto-generated method stub
							webview.loadUrl(pageurl);
							dialog.dismiss();
						}
					}, new OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int arg1) {
							// TODO Auto-generated method stub
							startActivity(new Intent(
									Html5GameWebViewActivity.this,
									LoginActivity.class));
							dialog.dismiss();
							finish();
						}
					});
			return false;
		} else {
			return true;
		}
	}

	@Override
	public int subContentViewId() {
		// TODO Auto-generated method stub
		return R.layout.activity_webview;
	}

}
