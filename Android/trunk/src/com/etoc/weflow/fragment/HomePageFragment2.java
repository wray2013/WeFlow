package com.etoc.weflow.fragment;

import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.etoc.weflow.R;
import com.etoc.weflow.WeFlowApplication;
import com.etoc.weflow.activity.ExpenseFlowActivity;
import com.etoc.weflow.activity.MainActivity;
import com.etoc.weflow.activity.MakeFlowActivity;
import com.etoc.weflow.activity.ScratchCardActivity;
import com.etoc.weflow.activity.ShakeShakeActivity;
import com.etoc.weflow.activity.login.LoginActivity;
import com.etoc.weflow.dao.AccountInfo;
import com.etoc.weflow.dao.AccountInfoDao;
import com.etoc.weflow.dialog.PromptDialog;
import com.etoc.weflow.net.GsonResponseObject.AccountInfoResp;
import com.etoc.weflow.net.GsonResponseObject.SignInResp;
import com.etoc.weflow.net.Requester;
import com.etoc.weflow.utils.ConStant;
import com.etoc.weflow.utils.FileUtils;
import com.etoc.weflow.utils.NumberUtils;
import com.etoc.weflow.utils.PushMsgUtil;
import com.etoc.weflow.utils.ViewUtils;
import com.etoc.weflow.view.MagicTextView;
import com.etoc.weflow.view.autoscrollviewpager.AutoScrollViewPager;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshScrollView;
import com.viewpagerindicator.PageIndicator;

public class HomePageFragment2 extends XFragment<Object>/*TitleRootFragment*/implements OnClickListener, OnRefreshListener2<ScrollView> {

	private final String TAG = "HomePageFragment";
	
	private AccountInfo currentAccount = null;
	
	private boolean isLogin = false;
	
	private LayoutInflater inflater;
	
	//UI Component
	private PullToRefreshScrollView ptrScrollView;
	private MagicTextView mtvFlow;//流量币余额
	private TextView tvPlain = null;//套餐
	private TextView tvInFlow = null;//剩余流量
	private LinearLayout llLogin    = null;
	private LinearLayout llNotLogin = null;
	private ImageView btnLogin = null;//登录按钮
	private ImageView ivSignIn = null;//签到按钮
	
//	private AutoScrollViewPager viewPager = null;
//	private PageIndicator mIndicator;
	
	private RelativeLayout rlGridVideo, rlGridSoft, rlGridActivity, rlGridExchange, rlGridFlow, rlGridGame;
	
	private MainActivity mainActivity = null;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if(getActivity() instanceof MainActivity) {
			mainActivity = (MainActivity) getActivity();
		} else {
			Log.e("XXX", "wrong attached activity " + getActivity().getClass().getName());
		}
		View v = inflater.inflate(R.layout.fragment_homepage2, null);
		initView(v);
		return v;
	}
	
	private void initView(View view) {
		Log.d(TAG, "initView");
		inflater = LayoutInflater.from(getActivity());
		ptrScrollView = (PullToRefreshScrollView) view.findViewById(R.id.ptr_scroll_view);
		ptrScrollView.setPullLabel("下拉加载最新");
		ptrScrollView.setReleaseLabel("释放加载");
		ptrScrollView.setOnRefreshListener(this);
		
		mtvFlow = (MagicTextView) view.findViewById(R.id.mtv_flow);
		
		ivSignIn = (ImageView) view.findViewById(R.id.iv_sign_in);
		ivSignIn.setOnClickListener(this);
		
		tvPlain = (TextView) view.findViewById(R.id.tv_pkg_type);
		tvInFlow = (TextView) view.findViewById(R.id.tv_pkg_flow_left);
		
		llNotLogin = (LinearLayout) view.findViewById(R.id.ll_account_not_login);
		llLogin = (LinearLayout) view.findViewById(R.id.ll_account);
		btnLogin = (ImageView) view.findViewById(R.id.iv_login);
		btnLogin.setOnClickListener(this);
		
		rlGridVideo    = (RelativeLayout) view.findViewById(R.id.rl_grid_video);
		rlGridSoft     = (RelativeLayout) view.findViewById(R.id.rl_grid_soft);
		rlGridActivity = (RelativeLayout) view.findViewById(R.id.rl_grid_activity);
		rlGridExchange = (RelativeLayout) view.findViewById(R.id.rl_grid_exchange);
		rlGridFlow     = (RelativeLayout) view.findViewById(R.id.rl_grid_flow);
		rlGridGame     = (RelativeLayout) view.findViewById(R.id.rl_grid_game);
		
		rlGridVideo.setOnClickListener(this);
		rlGridSoft.setOnClickListener(this);
		rlGridActivity.setOnClickListener(this);
		rlGridExchange.setOnClickListener(this);
		rlGridFlow.setOnClickListener(this);
		rlGridGame.setOnClickListener(this);
		
		adaptView(view);
		
		isLogin = false;
		
		currentAccount = WeFlowApplication.getAppInstance().getAccountInfo();
		loginView(true);
		
		if(currentAccount != null) {
			if("1".equals(currentAccount.getIsregistration())) {
				ivSignIn.setEnabled(false);
			} else {
				ivSignIn.setEnabled(true);
			}
		}

	}
	
	
	private void adaptView(View view) {
		// TODO Auto-generated method stub
		ViewUtils.setSize(ivSignIn, 112, 64);
		ViewUtils.setMarginRight(ivSignIn, 32);
		ViewUtils.setMarginTop(ivSignIn, 64);
		
		ViewUtils.setHeight(view.findViewById(R.id.rl_view_pager), 458);
		ViewUtils.setHeight(view.findViewById(R.id.rl_account), 106);
		
	}

	private void checkLogin() {
		isLogin = false;
		if (mainActivity != null) {
			if (currentAccount != null && currentAccount.getUserid() != null
					&& !currentAccount.getUserid().equals("")) {
				Log.e("XXX", "已登录");
				isLogin = true;
			}
		}
	}
	
	private void loginView(boolean needQuery) {
		
		checkLogin();
		
		if(llNotLogin == null || llLogin == null || mtvFlow == null || ivSignIn == null) return;
		
		
		if(isLogin) {
			//已登录
			llNotLogin.setVisibility(View.GONE);
			llLogin.setVisibility(View.VISIBLE);
			
			mtvFlow.showNumberWithAnimation(currentAccount.getFlowcoins(), 1000);
			
			if("1".equals(currentAccount.getIsregistration())) {
				ivSignIn.setEnabled(false);
			} else {
				ivSignIn.setEnabled(true);
			}
			
			loadConfig(currentAccount.getMakeflow(), currentAccount.getUseflow());
			if(needQuery || tvInFlow.getText().toString().equals("？"))
				Requester.queryAccountInfo(false, handler, currentAccount.getUserid());
		} else {
			//未登录
			llNotLogin.setVisibility(View.VISIBLE);
			llLogin.setVisibility(View.GONE);
			
		}
	}
	
	/**
	 * 读栏目配置
	 * @param makeConf
	 * @param UseConf
	 */
	private void loadConfig(String makeConf, String UseConf) {
		
	}
	
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}
	
	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		Log.d(TAG, "onResume");
		currentAccount = WeFlowApplication.getAppInstance().getAccountInfo();
		loginView(false);
		if(mtvFlow != null && isLogin) {
//			currentAccount = WeFlowApplication.getAppInstance().getAccountInfo();
			String newflow = currentAccount.getFlowcoins();
			mtvFlow.showNumberWithAnimation(newflow, 1000);
		}
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_title_left:
			/*PushMsgUtil pushmsg = new PushMsgUtil(handler, 0x88661256);
            String sendMSG = "{\"n_content\":\"\", " +
					"\"n_extras\": {\"msgtype\": \"1\", \"msgcontent\" : \"" + "你妹" + "\"," +
					"\"msghint\" : \"" + "点击查看" + "\"," + 
					"\"msgtitle\" : \"" + "有新消息" + "\"" +"}}";
            pushmsg.execute(sendMSG, "weflow, 15927130377");*/
			break;
		case 0xffeecc00:
			break;
		case 0xffeecc01:
		case 0xffeecc02:
		case 0xffeecc03:
			Intent makeFlowIntent = new Intent(getActivity(),MakeFlowActivity.class);
			makeFlowIntent.putExtra("isLogin", isLogin);
			makeFlowIntent.putExtra(ConStant.INTENT_MAKE_FLOW, v.getId() & 0xff);
			startActivity(makeFlowIntent);
			break;
		case 0xffeedd00:
			break;
		case 0xffeedd01:
		case 0xffeedd02:
		case 0xffeedd03:
		case 0xffeedd04:
			Intent expenseFlowIntent = new Intent(getActivity(),ExpenseFlowActivity.class);
			expenseFlowIntent.putExtra(ConStant.INTENT_EXPENSE_FLOW, v.getId() & 0xff);
			expenseFlowIntent.putExtra("isLogin", isLogin);
			startActivity(expenseFlowIntent);
			break;
		case R.id.iv_recomm_1:
			/*Intent recIntent1 = new Intent(getActivity(), WebViewActivity.class);
			recIntent1.putExtra("pageurl", "http://detail.amap.com/telecom/");
			startActivity(recIntent1);*/
			if(isLogin) {
				startActivity(new Intent(getActivity(), ScratchCardActivity.class));
			} else {
				startActivity(new Intent(getActivity(), LoginActivity.class));
			}
			break;
		case R.id.iv_recomm_2:
			/*Intent recIntent2 = new Intent(getActivity(), WebViewActivity.class);
			recIntent2.putExtra("pageurl", "http://detail.amap.com/telecom/");
			startActivity(recIntent2);*/
			if(isLogin) {
				startActivity(new Intent(getActivity(), ShakeShakeActivity.class));
			} else {
				startActivity(new Intent(getActivity(), LoginActivity.class));
			}
			break;
		case R.id.iv_login:
			startActivity(new Intent(getActivity(), LoginActivity.class));
			break;
		case R.id.iv_sign_in:
			AccountInfo accountInfo = WeFlowApplication.getAppInstance().getAccountInfo();
			if (accountInfo != null && accountInfo.getUserid() != null) {
				Requester.signIn(true, handler, accountInfo.getUserid());
			} else {
				startActivity(new Intent(getActivity(), LoginActivity.class));
			}
			break;
		case R.id.rl_grid_video:
			Intent videointent = new Intent(getActivity(),MakeFlowActivity.class);
			videointent.putExtra("isLogin", isLogin);
			videointent.putExtra(ConStant.INTENT_MAKE_FLOW, 0xffeecc01 & 0xff);
			startActivity(videointent);
			break;
		case R.id.rl_grid_soft:
			Intent softintent = new Intent(getActivity(),MakeFlowActivity.class);
			softintent.putExtra("isLogin", isLogin);
			softintent.putExtra(ConStant.INTENT_MAKE_FLOW, 0xffeecc02 & 0xff);
			startActivity(softintent);
			break;
		case R.id.rl_grid_activity:
			break;
		case R.id.rl_grid_exchange:
			break;
		case R.id.rl_grid_flow:
			break;
		case R.id.rl_grid_game:
			Intent gameintent = new Intent(getActivity(),MakeFlowActivity.class);
			gameintent.putExtra("isLogin", isLogin);
			gameintent.putExtra(ConStant.INTENT_MAKE_FLOW, 0xffeecc03 & 0xff);
			startActivity(gameintent);
			break;
		}
	}

	@Override
	public int getIndex() {
		return INDEX_HOMEPAGE;
	}
	
	@Override
	public void onShow() {
		Log.d(TAG, "onShow IN!");
		currentAccount = WeFlowApplication.getAppInstance().getAccountInfo();
		if(mtvFlow != null && isLogin) {
			mtvFlow.showNumberWithAnimation(currentAccount.getFlowcoins(), 1000);
		}
		loginView(true);
	}
	
	@Override
	public boolean handleMessage(Message msg) {
		// TODO Auto-generated method stub
		switch(msg.what) {
		case Requester.RESPONSE_TYPE_ACCOUNT_INFO:
			ptrScrollView.onRefreshComplete();
			if(msg.obj != null) {
				AccountInfoResp response = (AccountInfoResp) msg.obj;
				if("0".equals(response.status) || "0000".equals(response.status)) {
					if (response.flowcoins != null) {
						currentAccount.setFlowcoins(response.flowcoins);
						WeFlowApplication.getAppInstance().setFlowCoins(response.flowcoins);
						mtvFlow.showNumberWithAnimation(response.flowcoins, 1000);
					}
					tvPlain.setText(response.menumoney);
					
					if("1".equals(response.isregistration)) {
						ivSignIn.setEnabled(false);
					} else {
						ivSignIn.setEnabled(true);
					}
					
					float in  = 0;
					float out = 0;
					if(response.inflowleft != null && response.outflowleft != null) {
						try {
							in  = Float.parseFloat(response.inflowleft);
							out = Float.parseFloat(response.outflowleft);
						} catch(Exception e) {
							e.printStackTrace();
						}
					}
					
					tvInFlow.setText(FileUtils.getFlowSize((int)in));
//					tvOutFlow.setText(FileUtils.getFlowSize((int)out));
				}
			} else {
				PromptDialog.Alert(MainActivity.class, "您的网络不给力啊！");
			}
			break;
		case Requester.RESPONSE_TYPE_SIGN_IN:
			if (msg.obj != null) {
				SignInResp resp = (SignInResp) msg.obj;
				if(Requester.isSuccessed(resp.status)) {
					ivSignIn.setEnabled(false);
					WeFlowApplication.getAppInstance().setFlowCoins(resp.flowcoins);
					PromptDialog.Alert("签到成功，增加" + NumberUtils.convert2IntStr(resp.singleflowcoins) + "流量币");
					if(mtvFlow != null && isLogin) {
						currentAccount = WeFlowApplication.getAppInstance().getAccountInfo();
						mtvFlow.showNumberWithAnimation(currentAccount.getFlowcoins(), 1000);
						currentAccount.setIsregistration("1");
						WeFlowApplication.getAppInstance().PersistAccountInfo(currentAccount);
					}
					Requester.queryAccountInfo(false, handler, currentAccount.getUserid());
				} else if("2015".equals(resp.status)) {
					PromptDialog.Alert(MainActivity.class, "您已经签过到了！");
				}
			}
			break;
		}
		return false;
	}

	@Override
	public void onPullDownToRefresh(PullToRefreshBase<ScrollView> refreshView) {
		// TODO Auto-generated method stub
		if(isLogin) {
			Requester.queryAccountInfo(false, handler, currentAccount.getUserid());
		} else {
			handler.postDelayed(new Runnable() {
				
				@Override
				public void run() {
					// TODO Auto-generated method stub
					ptrScrollView.onRefreshComplete();
				}
			}, 10);
		}
	}

	@Override
	public void onPullUpToRefresh(PullToRefreshBase<ScrollView> refreshView) {
		// TODO Auto-generated method stub
		
	}

}
