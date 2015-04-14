package com.etoc.weflow.fragment;

import java.util.List;

import com.etoc.weflow.R;
import com.etoc.weflow.WeFlowApplication;
import com.etoc.weflow.activity.AccountActivity;
import com.etoc.weflow.activity.CaptureActivity;
import com.etoc.weflow.activity.DownloadManageActivity;
import com.etoc.weflow.activity.FeedBackActivity;
import com.etoc.weflow.activity.MainActivity;
import com.etoc.weflow.activity.MyMessageActivity;
import com.etoc.weflow.activity.SettingsActivity;
import com.etoc.weflow.activity.SignInActivity;
import com.etoc.weflow.activity.ExpenseFlowActivity;
import com.etoc.weflow.activity.MakeFlowActivity;
import com.etoc.weflow.activity.MyBillListActivity;
import com.etoc.weflow.activity.login.LoginActivity;
import com.etoc.weflow.dao.AccountInfo;
import com.etoc.weflow.dao.AccountInfoDao;
import com.etoc.weflow.utils.NumberUtils;
import com.etoc.weflow.utils.ViewUtils;

import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class MyselfFragment extends XFragment<Object>/*TitleRootFragment*/implements OnClickListener {

	private final String TAG = "MyselfFragment";
	
	private RelativeLayout rlMyBill;
	
	private MainActivity mainActivity = null;
	private TextView tvLogin = null;
	private RelativeLayout rlAccountInfo = null;
	private boolean isLogin = false;
	private TextView tvFlowValue = null;
	
	private AccountInfo currentAccount = null;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreateView(inflater, container, savedInstanceState);
		if(getActivity() instanceof MainActivity) {
			mainActivity = (MainActivity) getActivity();
		} else {
			Log.e("XXX", "wrong attached activity " + getActivity().getClass().getName());
		}
		View v = inflater.inflate(R.layout.fragment_myself, null);
		initView(v);
		return v;
	}
	
	private void initView(View view) {
		rlMyBill = (RelativeLayout) view.findViewById(R.id.rl_me_bill);
		rlMyBill.setOnClickListener(this);
		tvLogin = (TextView) view.findViewById(R.id.tv_login_btn);
		
		rlAccountInfo = (RelativeLayout) view.findViewById(R.id.rl_account_info);
		ViewUtils.setSize(tvLogin, 242, 72);
		ViewUtils.setMarginLeft(tvLogin, 94);
		ViewUtils.setTextSize(tvLogin, 30);
		tvLogin.setOnClickListener(this);
		rlAccountInfo.setOnClickListener(this);
		
		tvFlowValue = (TextView)view.findViewById(R.id.tv_flow_value);
		
		view.findViewById(R.id.rl_me_msg).setOnClickListener(this);
		view.findViewById(R.id.rl_me_bill).setOnClickListener(this);
		view.findViewById(R.id.rl_me_download).setOnClickListener(this);
		view.findViewById(R.id.rl_me_sign).setOnClickListener(this);
		view.findViewById(R.id.rl_me_feedback).setOnClickListener(this);
		view.findViewById(R.id.rl_me_settings).setOnClickListener(this);
		
		ViewUtils.setHeight(view.findViewById(R.id.rl_me_top), 306);
		ViewUtils.setHeight(rlAccountInfo, 202);
		ViewUtils.setSize(view.findViewById(R.id.iv_head), 130,130);
		ViewUtils.setMarginBottom(view.findViewById(R.id.iv_head), 16);
		ViewUtils.setMarginLeft(view.findViewById(R.id.iv_head), 32);
		
		ViewUtils.setHeight(view.findViewById(R.id.rl_me_msg), 112);
		ViewUtils.setHeight(view.findViewById(R.id.rl_me_bill), 112);
		ViewUtils.setHeight(view.findViewById(R.id.rl_me_download), 112);
		ViewUtils.setHeight(view.findViewById(R.id.rl_me_sign), 112);
		ViewUtils.setHeight(view.findViewById(R.id.rl_me_feedback), 112);
		ViewUtils.setHeight(view.findViewById(R.id.rl_me_settings), 112);
		
		
		ViewUtils.setTextSize((TextView) view.findViewById(R.id.tv_flow_hint), 35);
		ViewUtils.setTextSize(tvFlowValue, 35);
		ViewUtils.setTextSize((TextView) view.findViewById(R.id.tv_flow_paper_hint), 35);
		ViewUtils.setTextSize((TextView) view.findViewById(R.id.tv_flow_paper), 35);
		
		ViewUtils.setTextSize((TextView) view.findViewById(R.id.tv_msg), 35);
		ViewUtils.setTextSize((TextView) view.findViewById(R.id.tv_bill), 35);
		ViewUtils.setTextSize((TextView) view.findViewById(R.id.tv_sign), 35);
		ViewUtils.setTextSize((TextView) view.findViewById(R.id.tv_feedback), 35);
		ViewUtils.setTextSize((TextView) view.findViewById(R.id.tv_settings), 35);
		ViewUtils.setTextSize((TextView) view.findViewById(R.id.tv_download), 35);
		
		ViewUtils.setMarginLeft(view.findViewById(R.id.iv_me_flow_coin), 32);
		ViewUtils.setMarginLeft(view.findViewById(R.id.iv_me_flow_gift), 12);
		ViewUtils.setSize(view.findViewById(R.id.iv_me_flow_coin), 72, 72);
		ViewUtils.setSize(view.findViewById(R.id.iv_me_flow_gift), 72, 72);
		
		ViewUtils.setMarginRight((TextView) view.findViewById(R.id.tv_flow_value), 25);
		ViewUtils.setMarginRight((TextView) view.findViewById(R.id.tv_flow_paper), 25);
		
		ViewUtils.setSize(view.findViewById(R.id.iv_me_msg), 72, 72);
		ViewUtils.setSize(view.findViewById(R.id.iv_me_bill), 72, 72);
		ViewUtils.setSize(view.findViewById(R.id.iv_me_download), 72, 72);
		ViewUtils.setSize(view.findViewById(R.id.iv_me_sign), 72, 72);
		ViewUtils.setSize(view.findViewById(R.id.iv_me_feedback), 72, 72);
		ViewUtils.setSize(view.findViewById(R.id.iv_me_settings), 72, 72);
		
		ViewUtils.setMarginLeft(view.findViewById(R.id.tv_msg), 24);
		ViewUtils.setMarginLeft(view.findViewById(R.id.tv_bill), 24);
		ViewUtils.setMarginLeft(view.findViewById(R.id.tv_download), 24);
		ViewUtils.setMarginLeft(view.findViewById(R.id.tv_sign), 24);
		ViewUtils.setMarginLeft(view.findViewById(R.id.tv_feedback), 24);
		ViewUtils.setMarginLeft(view.findViewById(R.id.tv_settings), 24);
		ViewUtils.setMarginLeft(view.findViewById(R.id.view_divide_h_center), 32);
		ViewUtils.setMarginRight(view.findViewById(R.id.view_divide_h_center), 32);
		ViewUtils.setPaddingLeft(view.findViewById(R.id.rl_me_msg), 32);
		ViewUtils.setPaddingLeft(view.findViewById(R.id.rl_me_bill), 32);
		ViewUtils.setPaddingLeft(view.findViewById(R.id.rl_me_download), 32);
		ViewUtils.setPaddingLeft(view.findViewById(R.id.rl_me_sign), 32);
		ViewUtils.setPaddingLeft(view.findViewById(R.id.rl_me_feedback), 32);
		ViewUtils.setPaddingLeft(view.findViewById(R.id.rl_me_settings), 32);
		ViewUtils.setMarginLeft(view.findViewById(R.id.view_divide_h_centerb2), 32);
		ViewUtils.setMarginRight(view.findViewById(R.id.view_divide_h_centerb2), 32);
		ViewUtils.setMarginTop(view.findViewById(R.id.rl_me_center_a), 20);
		ViewUtils.setMarginTop(view.findViewById(R.id.rl_me_center_b), 20);
		ViewUtils.setMarginTop(view.findViewById(R.id.rl_me_bottom), 20);

		loginView();
	}
	
	
	private void loginView() {
		// TODO Auto-generated method stub
		checkLogin();
		if(tvLogin == null) return;
		
		if(isLogin) {
			tvLogin.setText(currentAccount.getTel());
			tvLogin.setClickable(false);
			tvLogin.setBackgroundResource(0);
		} else {
			tvLogin.setText("开通流量钱包");
			tvLogin.setClickable(true);
			tvLogin.setBackgroundResource(R.drawable.bg_round_login);
			tvFlowValue.setText("0");
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
	
	
	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		Log.d(TAG, "onResume");
		AccountInfo accountInfo = WeFlowApplication.getAppInstance().getAccountInfo();
		if (accountInfo != null && accountInfo.getFlowcoins() != null) {
			tvFlowValue.setText(NumberUtils.convert2IntStr(accountInfo.getFlowcoins()));
		}
		loginView();
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_title_left:
			
			break;
		case R.id.rl_me_msg:
			if(!isLogin) {
				startActivity(new Intent(getActivity(), LoginActivity.class));
				return;
			}
			startActivity(new Intent(getActivity(), MyMessageActivity.class));
			break;
		case R.id.rl_me_bill:
			if(!isLogin) {
				startActivity(new Intent(getActivity(), LoginActivity.class));
				return;
			}
			startActivity(new Intent(getActivity(), MyBillListActivity.class));
			break;
		case R.id.rl_me_sign:
			if(!isLogin) {
				startActivity(new Intent(getActivity(), LoginActivity.class));
				return;
			}
			startActivity(new Intent(getActivity(), SignInActivity.class));
			break;
		case R.id.rl_me_feedback:
			if(!isLogin) {
				startActivity(new Intent(getActivity(), LoginActivity.class));
				return;
			}
			startActivity(new Intent(getActivity(), FeedBackActivity.class));
			break;
		case R.id.rl_me_settings:
			startActivity(new Intent(getActivity(), SettingsActivity.class));
			break;
		case R.id.rl_me_download:
			startActivity(new Intent(getActivity(),DownloadManageActivity.class));
			break;
		case R.id.tv_login_btn:
			startActivity(new Intent(getActivity(), LoginActivity.class));
			break;
		case R.id.rl_account_info:
			if(!isLogin) {
				startActivity(new Intent(getActivity(), LoginActivity.class));
				return;
			}
			if (isLogin) {
				Intent accountIntent = new Intent(getActivity(), AccountActivity.class);
				accountIntent.putExtra("tel", currentAccount.getTel());
				startActivity(accountIntent);
			}
			break;
		}
	}
	
	@Override
	public int getIndex() {
		return INDEX_ME;
	}

	@Override
	public void onShow() {
		Log.d(TAG, "onShow IN!");
		loginView();
	}
	
}
