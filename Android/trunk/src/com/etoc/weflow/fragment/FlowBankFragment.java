package com.etoc.weflow.fragment;

import java.util.List;

import com.etoc.weflow.R;
import com.etoc.weflow.activity.DepositFlowActivity;
import com.etoc.weflow.activity.DrawFlowActivity;
import com.etoc.weflow.activity.MainActivity;
import com.etoc.weflow.activity.login.LoginActivity;
import com.etoc.weflow.dao.AccountInfo;
import com.etoc.weflow.dao.AccountInfoDao;
import com.etoc.weflow.dialog.PromptDialog;
import com.etoc.weflow.net.Requester;
import com.etoc.weflow.net.GsonResponseObject.testResponse;
import com.etoc.weflow.utils.ViewUtils;
import com.etoc.weflow.view.MagicTextView;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.TextView;

public class FlowBankFragment extends XFragment<Object>/*TitleRootFragment*/implements OnClickListener {

	private final String TAG = "FlowBankFragment";
	
	private DisplayMetrics dm = new DisplayMetrics();
	
	private MagicTextView mtvMoney;
	
	private TextView tvDrawFlow, tvSaveFlow;
	
	private MainActivity mainActivity = null;
	
	private boolean isLogin = false;
	
	/*@Override
	public int subContentViewId() {
		// TODO Auto-generated method stub
		return R.layout.fragment_flowbank;
	}*/
	
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
		View v = inflater.inflate(R.layout.fragment_flowbank, null);
		initView(v);
		return v;
	}
	
	private void initView(View view) {
		dm = getResources().getDisplayMetrics();
		
		mtvMoney = (MagicTextView) view.findViewById(R.id.mtv_total_money);
//		mtvMoney.setNumber(2600);
//		mtvMoney.showNumberWithAnimation(21985.2f, 1000);
		
		tvDrawFlow = (TextView) view.findViewById(R.id.tv_pop);
		tvSaveFlow = (TextView) view.findViewById(R.id.tv_save);
		
		tvDrawFlow.setOnClickListener(this);
		tvSaveFlow.setOnClickListener(this);
		
		viewAdapter(view);

		checkLogin();
	}
	
	private void checkLogin() {
		isLogin = false;
		if (mainActivity != null) {
			AccountInfoDao accountInfoDao = mainActivity.getAccountInfoDao();
			if (accountInfoDao != null && accountInfoDao.count() > 0) {
				List<AccountInfo> aiList = accountInfoDao.loadAll();
				AccountInfo current = aiList.get(0);
				if (current != null && current.getUserid() != null
						&& !current.getUserid().equals("")) {
					Log.e("XXX", "已登录");
					isLogin = true;
					return;
				}
			}
			popWarningDialog();
		}
	}
	
	private void viewAdapter(View view) {
		// TODO Auto-generated method stub
		ViewUtils.setHeight(view.findViewById(R.id.rl_bank_top), 275);
		ViewUtils.setWidth(view.findViewById(R.id.ll_bank_bottom), 658);
		ViewUtils.setHeight(view.findViewById(R.id.ll_bank_bottom), 112);
		ViewUtils.setHeight(view.findViewById(R.id.v_divider), 72);
		
		ViewUtils.setWidth(mtvMoney, 380);
		
		ViewUtils.setTextSize((TextView) view.findViewById(R.id.tv_yest_income), 105);
		ViewUtils.setTextSize(mtvMoney, 70);
		ViewUtils.setTextSize((TextView) view.findViewById(R.id.tv_pop),  38);
		ViewUtils.setTextSize((TextView) view.findViewById(R.id.tv_save), 38);
		
		
	}
	
	private void popWarningDialog() {
		PromptDialog.Dialog(getActivity(), "友情提示", "您尚未登录，暂时无法使用银行功能",
				"现在登录", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int arg1) {
						// TODO Auto-generated method stub
						startActivity(new Intent(getActivity(),
								LoginActivity.class));
					}
				});
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		Log.d(TAG, "onResume");
//		checkLogin();
		if(mtvMoney != null)
			mtvMoney.showNumberWithAnimation(2600, 1000);
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_title_left:
			break;
		case R.id.tv_pop:
//			Requester.test(handler);
			if(isLogin) {
				String[] values = new String[] {"14000", "18000", "60000"};
				Intent drawIntent = new Intent(getActivity(), DrawFlowActivity.class);
				drawIntent.putExtra("values", values);
				drawIntent.putExtra("total", 50000);
				startActivity(drawIntent);
				getActivity().overridePendingTransition(R.anim.slide_in_from_right, R.anim.slide_out_to_left);
			} else {
				popWarningDialog();
			}
			break;
		case R.id.tv_save:
			if(isLogin) {
				Intent depositIntent = new Intent(getActivity(), DepositFlowActivity.class);
				depositIntent.putExtra("minValue", 10000);
				depositIntent.putExtra("total", 50000);
				startActivity(depositIntent);
				getActivity().overridePendingTransition(R.anim.slide_in_from_right, R.anim.slide_out_to_left);
			} else {
				popWarningDialog();
			}
			break;
		}
	}
	
	@Override
	public boolean handleMessage(Message msg) {
		// TODO Auto-generated method stub
		switch(msg.what) {
		case Requester.RESPONSE_TYPE_TEST:
			if(msg.obj != null) {
				testResponse response = (testResponse) msg.obj;
				Log.d(TAG, "response = " + response.status + ";id = " + response.id);
				if(response.status != null && response.status.equals("0")) {
					
				}
			}
			break;
		}
		return false;
	}
	
	@Override
	public int getIndex() {
		return INDEX_BANK;
	}

	@Override
	public void onShow() {
		Log.d(TAG, "onShow IN!");
		if(isAdded()) {
			checkLogin();
		}
		if(mtvMoney != null)
			mtvMoney.showNumberWithAnimation(2600, 1000);
	}
	
}
