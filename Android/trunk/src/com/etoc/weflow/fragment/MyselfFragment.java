package com.etoc.weflow.fragment;

import java.util.List;

import com.etoc.weflow.R;
import com.etoc.weflow.activity.AccountActivity;
import com.etoc.weflow.activity.CaptureActivity;
import com.etoc.weflow.activity.DownloadManageActivity;
import com.etoc.weflow.activity.FeedBackActivity;
import com.etoc.weflow.activity.MainActivity;
import com.etoc.weflow.activity.SettingsActivity;
import com.etoc.weflow.activity.SignInActivity;
import com.etoc.weflow.activity.ExpenseFlowActivity;
import com.etoc.weflow.activity.MakeFlowActivity;
import com.etoc.weflow.activity.MyBillListActivity;
import com.etoc.weflow.activity.login.LoginActivity;
import com.etoc.weflow.dao.AccountInfo;
import com.etoc.weflow.dao.AccountInfoDao;
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
	private boolean isLogin = true;
	
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
		ViewUtils.setMarginBottom(tvLogin, 36);
		ViewUtils.setTextSize(tvLogin, 30);
		tvLogin.setOnClickListener(this);
		rlAccountInfo.setOnClickListener(this);
		
		view.findViewById(R.id.rl_me_msg).setOnClickListener(this);
		view.findViewById(R.id.rl_me_bill).setOnClickListener(this);
		view.findViewById(R.id.rl_me_download).setOnClickListener(this);
		view.findViewById(R.id.rl_me_sign).setOnClickListener(this);
		view.findViewById(R.id.rl_me_feedback).setOnClickListener(this);
		view.findViewById(R.id.rl_me_settings).setOnClickListener(this);
		
		ViewUtils.setHeight(view.findViewById(R.id.rl_me_top), 222);
		ViewUtils.setHeight(rlAccountInfo, 144);
		ViewUtils.setSize(view.findViewById(R.id.iv_head), 112,112);
		ViewUtils.setMarginBottom(view.findViewById(R.id.iv_head), 16);
		ViewUtils.setMarginLeft(view.findViewById(R.id.iv_head), 32);
		
		ViewUtils.setHeight(view.findViewById(R.id.rl_me_msg), 112);
		ViewUtils.setHeight(view.findViewById(R.id.rl_me_bill), 112);
		ViewUtils.setHeight(view.findViewById(R.id.rl_me_download), 112);
		ViewUtils.setHeight(view.findViewById(R.id.rl_me_sign), 112);
		ViewUtils.setHeight(view.findViewById(R.id.rl_me_feedback), 112);
		ViewUtils.setHeight(view.findViewById(R.id.rl_me_settings), 112);
		
		
		ViewUtils.setTextSize((TextView) view.findViewById(R.id.tv_flow_hint), 35);
		ViewUtils.setTextSize((TextView) view.findViewById(R.id.tv_flow_value), 35);
		ViewUtils.setTextSize((TextView) view.findViewById(R.id.tv_flow_paper_hint), 35);
		ViewUtils.setTextSize((TextView) view.findViewById(R.id.tv_flow_paper), 35);
		
		ViewUtils.setTextSize((TextView) view.findViewById(R.id.tv_msg), 35);
		ViewUtils.setTextSize((TextView) view.findViewById(R.id.tv_bill), 35);
		ViewUtils.setTextSize((TextView) view.findViewById(R.id.tv_sign), 35);
		ViewUtils.setTextSize((TextView) view.findViewById(R.id.tv_feedback), 35);
		ViewUtils.setTextSize((TextView) view.findViewById(R.id.tv_settings), 35);
		ViewUtils.setTextSize((TextView) view.findViewById(R.id.tv_download), 35);
		
		ViewUtils.setMarginLeft((TextView) view.findViewById(R.id.tv_flow_hint), 52);
		ViewUtils.setMarginLeft((TextView) view.findViewById(R.id.tv_flow_paper_hint), 52);
		
		ViewUtils.setMarginRight((TextView) view.findViewById(R.id.tv_flow_value), 25);
		ViewUtils.setMarginRight((TextView) view.findViewById(R.id.tv_flow_paper), 25);
		
		ViewUtils.setMarginLeft(view.findViewById(R.id.tv_msg), 48);
		ViewUtils.setMarginLeft(view.findViewById(R.id.tv_bill), 48);
		ViewUtils.setMarginLeft(view.findViewById(R.id.tv_download), 48);
		ViewUtils.setMarginLeft(view.findViewById(R.id.tv_sign), 48);
		ViewUtils.setMarginLeft(view.findViewById(R.id.tv_feedback), 48);
		ViewUtils.setMarginLeft(view.findViewById(R.id.tv_settings), 48);
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
		
		checkLogin();
	}
	
	
	private void checkLogin() {
//		isLogin = false;
		if (mainActivity != null) {
			AccountInfoDao accountInfoDao = mainActivity.getAccountInfoDao();
			if (accountInfoDao != null && accountInfoDao.count() > 0) {
				List<AccountInfo> aiList = accountInfoDao.loadAll();
				AccountInfo current = aiList.get(0);
				if (current != null && current.getUserid() != null
						&& !current.getUserid().equals("")) {
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
	}
	
	@Override
	public void onClick(View v) {
		if(!isLogin) {
			startActivity(new Intent(getActivity(), LoginActivity.class));
			return;
		}
		switch (v.getId()) {
		case R.id.btn_title_left:
			
			break;
		case R.id.rl_me_bill:
			startActivity(new Intent(getActivity(), MyBillListActivity.class));
			break;
		case R.id.rl_me_sign:
			startActivity(new Intent(getActivity(), SignInActivity.class));
			break;
		case R.id.rl_me_feedback:
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
			if (isLogin) {
				startActivity(new Intent(getActivity(), AccountActivity.class));
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
	}
	
}
