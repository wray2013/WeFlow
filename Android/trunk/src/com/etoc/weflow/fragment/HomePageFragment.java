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
import com.etoc.weflow.activity.WebViewActivity;
import com.etoc.weflow.activity.login.LoginActivity;
import com.etoc.weflow.dao.AccountInfo;
import com.etoc.weflow.dao.AccountInfoDao;
import com.etoc.weflow.dialog.PromptDialog;
import com.etoc.weflow.net.GsonResponseObject.AccountInfoResp;
import com.etoc.weflow.net.GsonResponseObject.SignInResp;
import com.etoc.weflow.net.Requester;
import com.etoc.weflow.utils.ConStant;
import com.etoc.weflow.utils.FileUtils;
import com.etoc.weflow.utils.PushMsgUtil;
import com.etoc.weflow.utils.ViewUtils;
import com.etoc.weflow.view.MagicTextView;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshScrollView;

public class HomePageFragment extends XFragment<Object>/*TitleRootFragment*/implements OnClickListener, OnRefreshListener2<ScrollView> {

	private final String TAG = "HomePageFragment";
	
	private AccountInfo currentAccount = null;
	
	private boolean isLogin = false;
	
	private LinearLayout makeFLowLayout;
	private LinearLayout expenseFlowLayout;
	private LayoutInflater inflater;
	private int makeFlowId = 0xffeecc00;
	private int expenseFlowId = 0xffeedd00;
	
	//UI Component
	private PullToRefreshScrollView ptrScrollView;
	private ImageView ivRecA, ivRecB;
	private MagicTextView mtvFlow;
	private TextView tvCellPhone = null;
	private TextView tvPlain = null;
	private TextView tvPlainType = null;
	private TextView tvInFlow = null;
	private TextView tvOutFlow = null;
	private RelativeLayout rlLogin    = null;
	private RelativeLayout rlNotLogin = null;
	private TextView btnLogin = null;
	private ImageView ivSignIn = null;
	
	private MainActivity mainActivity = null;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
//		super.onCreateView(inflater, container, savedInstanceState);
		if(getActivity() instanceof MainActivity) {
			mainActivity = (MainActivity) getActivity();
		} else {
			Log.e("XXX", "wrong attached activity " + getActivity().getClass().getName());
		}
		View v = inflater.inflate(R.layout.fragment_homepage, null);
		initView(v);
		return v;
	}
	
	private void initView(View view) {
		inflater = LayoutInflater.from(getActivity());
		makeFLowLayout = (LinearLayout) view.findViewById(R.id.ll_make_flow);
		expenseFlowLayout =(LinearLayout) view.findViewById(R.id.ll_expense_flow);
		ptrScrollView = (PullToRefreshScrollView) view.findViewById(R.id.ptr_scroll_view);
		ptrScrollView.setPullLabel("下拉加载最新");
		ptrScrollView.setReleaseLabel("释放加载");
		ptrScrollView.setOnRefreshListener(this);
		
		mtvFlow = (MagicTextView) view.findViewById(R.id.mtv_flow);
		
		ivSignIn = (ImageView) view.findViewById(R.id.iv_sign_in);
		ivSignIn.setOnClickListener(this);
		
//		mtvFlow.showNumberWithAnimation(98, 1000);
		
		tvCellPhone = (TextView) view.findViewById(R.id.tv_phone_num);
		tvPlain = (TextView) view.findViewById(R.id.tv_plans);
		tvPlainType = (TextView) view.findViewById(R.id.tv_plans_type);
		tvInFlow = (TextView) view.findViewById(R.id.tv_plans_in_left);
		tvOutFlow = (TextView) view.findViewById(R.id.tv_plans_out_left);
		rlNotLogin = (RelativeLayout) view.findViewById(R.id.rl_not_login);
		rlLogin = (RelativeLayout) view.findViewById(R.id.rl_title_top);
		btnLogin = (TextView) view.findViewById(R.id.tv_login_btn);
		btnLogin.setOnClickListener(this);
		
		ViewUtils.setSize(ivSignIn, 112, 64);
		ViewUtils.setMarginRight(ivSignIn, 32);
		ViewUtils.setMarginTop(ivSignIn, 64);
		ViewUtils.setTextSize(tvCellPhone, 32);
		ViewUtils.setHeight(view.findViewById(R.id.rl_title_top), 432);
		ViewUtils.setHeight(rlNotLogin, 432);
		ViewUtils.setSize(view.findViewById(R.id.iv_image), 138, 138);
		ViewUtils.setSize(btnLogin, 242, 72);
		ViewUtils.setTextSize(btnLogin,36);
		ViewUtils.setMarginTop(view.findViewById(R.id.iv_image), 80);
		ViewUtils.setMarginBottom(btnLogin, 56);
		
		ViewUtils.setTextSize(view.findViewById(R.id.tv_phone_num_hint), 32);
		ViewUtils.setHeight(view.findViewById(R.id.rl_user_phone), 58);
		ViewUtils.setMarginTop(view.findViewById(R.id.rl_user_phone), 20);
		ViewUtils.setSize(view.findViewById(R.id.rl_flow_account), 206,206);
		ViewUtils.setMarginTop(view.findViewById(R.id.rl_flow_account), 20);
		ViewUtils.setMarginTop(mtvFlow, 52);
		ViewUtils.setTextSize(mtvFlow, 56);
		ViewUtils.setTextSize(view.findViewById(R.id.tv_flow_text), 28);
		ViewUtils.setMarginBottom(view.findViewById(R.id.tv_flow_text), 40);
		ViewUtils.setMarginTop(view.findViewById(R.id.view_user_line_2), 20);
		
		ViewUtils.setHeight(view.findViewById(R.id.ll_account_desc), 112);
		ViewUtils.setTextSize(tvPlain, 28);
		ViewUtils.setTextSize(tvPlainType, 28);
		ViewUtils.setMarginBottom(view.findViewById(R.id.view_1dp_width), 32);
		ViewUtils.setMarginTop(view.findViewById(R.id.view_1dp_width), 32);
		ViewUtils.setTextSize(tvInFlow, 26);
		ViewUtils.setTextSize(tvOutFlow, 26);
		
		ViewUtils.setHeight(view.findViewById(R.id.ll_flow_change), 401);
		ViewUtils.setMarginTop(view.findViewById(R.id.rl_activity_recomm), 18);
		ViewUtils.setMarginTop(view.findViewById(R.id.tv_recomm_label), 40);
		ViewUtils.setMarginLeft(view.findViewById(R.id.tv_recomm_label), 32);
		ViewUtils.setTextSize(view.findViewById(R.id.tv_recomm_label), 30);
		ViewUtils.setMarginTop(view.findViewById(R.id.view_line), 26);
		ViewUtils.setMarginRight(view.findViewById(R.id.view_line), 32);
		ViewUtils.setHeight(view.findViewById(R.id.ll_recomm), 190);
		ViewUtils.setMarginTop(view.findViewById(R.id.ll_recomm), 18);
		ViewUtils.setMarginBottom(view.findViewById(R.id.ll_recomm), 8);
		ViewUtils.setMarginLeft(view.findViewById(R.id.iv_recomm_2), 16);
		
		
		String [] makeFlows = {"赚流量","看视频","下软件","玩游戏"};
		String [] expenseFlows = {"花流量","充值","订流量包","换游戏币","换礼券"};
		int [] makeflowsIds = {R.drawable.make_flow,R.drawable.watch_video,R.drawable.download_apk,R.drawable.play_game};
		int [] expenseFlowsIds = {R.drawable.expense_flow,R.drawable.recharge,R.drawable.order_flow_pkg,R.drawable.exchange_game_coins,R.drawable.exchange_gift};
		for (int i = 0;i < 4;i++) {
			RelativeLayout layout = (RelativeLayout) inflater.inflate(R.layout.include_flow_buttons_stub,null);
			
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.MATCH_PARENT);
			layout.setLayoutParams(params);
			ViewUtils.setSize(layout.findViewById(R.id.view_space), 178, 200);
			ViewUtils.setMarginRight(layout, 2);
			layout.setId(makeFlowId + i);
			layout.setOnClickListener(this);
			TextView tvName = (TextView) layout.findViewById(R.id.tv_flow_name);
			tvName.setText(makeFlows[i]);
			ImageView ivModules = (ImageView) layout.findViewById(R.id.iv_flow_image);
			ViewUtils.setSize(ivModules, 96, 96);
			ivModules.setImageResource(makeflowsIds[i]);
			ViewUtils.setMarginBottom(tvName, 32);
			ViewUtils.setMarginTop(ivModules, 40);
			ViewUtils.setTextSize(tvName, 28);
			makeFLowLayout.addView(layout);
		}
		
		for (int i = 0;i < 5;i++) {
			RelativeLayout layout = (RelativeLayout) inflater.inflate(R.layout.include_flow_buttons_stub,null);
			
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.MATCH_PARENT);
			layout.setLayoutParams(params);
			ViewUtils.setSize(layout.findViewById(R.id.view_space), 178, 200);
			ViewUtils.setMarginRight(layout, 2);
			layout.setId(expenseFlowId + i);
			layout.setOnClickListener(this);
			TextView tvName = (TextView) layout.findViewById(R.id.tv_flow_name);
			tvName.setText(expenseFlows[i]);
			ImageView ivModules = (ImageView) layout.findViewById(R.id.iv_flow_image);
			ViewUtils.setSize(ivModules, 96, 96);
			ivModules.setImageResource(expenseFlowsIds[i]);
			ViewUtils.setMarginBottom(tvName, 32);
			ViewUtils.setMarginTop(ivModules, 40);
			ViewUtils.setTextSize(tvName, 28);
			expenseFlowLayout.addView(layout);
		}
		
		ivRecA = (ImageView) view.findViewById(R.id.iv_recomm_1);
		ivRecA.setOnClickListener(this);
		ivRecB = (ImageView) view.findViewById(R.id.iv_recomm_2);
		ivRecB.setOnClickListener(this);
		
		ivRecA.setBackgroundResource(R.drawable.scratch_banner);
		ivRecB.setBackgroundResource(R.drawable.shake_banner);
		/*ImageLoader.getInstance().displayImage("http://detail.amap.com/telecom/images/AMAP_05.jpg", ivRecA);
		ImageLoader.getInstance().displayImage("http://detail.amap.com/telecom/images/AMAP_05.jpg", ivRecB);*/
		isLogin = false;
		
		/*AccountInfoDao accountInfoDao = mainActivity.getAccountInfoDao();
		if(accountInfoDao != null && accountInfoDao.count() > 0) {
			List<AccountInfo> aiList = accountInfoDao.loadAll();
			currentAccount = aiList.get(0);
			if(currentAccount != null && currentAccount.getUserid() != null && !currentAccount.getUserid().equals("")) {
				isLogin = true;
				//TODO:请求套餐
				Requester.queryAccountInfo(false, handler, currentAccount.getUserid());
			}
		}*/
		loginView();
		
		if(currentAccount != null) {
			if("1".equals(currentAccount.getIsregistration())) {
				ivSignIn.setEnabled(false);
			} else {
				ivSignIn.setEnabled(true);
			}
		}

	}
	
	private void checkLogin() {
		isLogin = false;
		if (mainActivity != null) {
			AccountInfoDao accountInfoDao = mainActivity.getAccountInfoDao();
			if (accountInfoDao != null && accountInfoDao.count() > 0) {
				List<AccountInfo> aiList = accountInfoDao.loadAll();
				currentAccount = aiList.get(0);
				if (currentAccount != null && currentAccount.getUserid() != null
						&& !currentAccount.getUserid().equals("")) {
					Log.e("XXX", "已登录");
					isLogin = true;
				}
			}
		}
	}
	
	private void loginView() {
		
		if(rlNotLogin == null || rlLogin == null ||
				tvCellPhone == null || mtvFlow == null) return;
		
		checkLogin();
		
		if(isLogin) {
			//已登录
			rlNotLogin.setVisibility(View.GONE);
			rlLogin.setVisibility(View.VISIBLE);
			
			tvCellPhone.setText(currentAccount.getTel());
			mtvFlow.showNumberWithAnimation(currentAccount.getFlowcoins(), 1000);
			
			if("0".equals(currentAccount.getIsregistration())) { //未签到
				
			} else {
				
			}
			
			loadConfig(currentAccount.getMakeflow(), currentAccount.getUseflow());
			Requester.queryAccountInfo(false, handler, currentAccount.getUserid());
		} else {
			//未登录
			rlNotLogin.setVisibility(View.VISIBLE);
			rlLogin.setVisibility(View.GONE);
			
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
		loginView();
		if(mtvFlow != null && isLogin) {
			currentAccount = WeFlowApplication.getAppInstance().getAccountInfo();
			mtvFlow.showNumberWithAnimation(currentAccount.getFlowcoins(), 1000);
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
            pushmsg.execute(sendMSG, "weflow");*/
			break;
		case 0xffeecc00:
		case 0xffeecc01:
		case 0xffeecc02:
		case 0xffeecc03:
			Intent makeFlowIntent = new Intent(getActivity(),MakeFlowActivity.class);
			makeFlowIntent.putExtra("isLogin", isLogin);
			makeFlowIntent.putExtra(ConStant.INTENT_MAKE_FLOW, v.getId() & 0xff);
			startActivity(makeFlowIntent);
			break;
		case 0xffeedd00:
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
			startActivity(new Intent(getActivity(), ScratchCardActivity.class));
			break;
		case R.id.iv_recomm_2:
			/*Intent recIntent2 = new Intent(getActivity(), WebViewActivity.class);
			recIntent2.putExtra("pageurl", "http://detail.amap.com/telecom/");
			startActivity(recIntent2);*/
			startActivity(new Intent(getActivity(), ShakeShakeActivity.class));
			break;
		case R.id.tv_login_btn:
			startActivity(new Intent(getActivity(), LoginActivity.class));
			break;
		case R.id.iv_sign_in:
			AccountInfo accountInfo = WeFlowApplication.getAppInstance().getAccountInfo();
			if (accountInfo != null) {
				Requester.signIn(true, handler, accountInfo.getUserid());
			}
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
		if(mtvFlow != null && isLogin) {
			currentAccount = WeFlowApplication.getAppInstance().getAccountInfo();
			mtvFlow.showNumberWithAnimation(currentAccount.getFlowcoins(), 1000);
		}
		loginView();
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
						WeFlowApplication.setFlowCoins(response.flowcoins);
						mtvFlow.showNumberWithAnimation(response.flowcoins, 1000);
					}
					tvPlain.setText(response.menumoney);
					tvPlainType.setText(response.menutype);
					
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
					tvOutFlow.setText(FileUtils.getFlowSize((int)out));
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
					WeFlowApplication.setFlowCoins(resp.signflowcoins);
					PromptDialog.Alert("签到成功，增加" + resp.signflowcoins + "流量币");
					if(mtvFlow != null && isLogin) {
						currentAccount = WeFlowApplication.getAppInstance().getAccountInfo();
						mtvFlow.showNumberWithAnimation(currentAccount.getFlowcoins(), 1000);
					}
				} else if("2016".equals(resp.status)) {
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
