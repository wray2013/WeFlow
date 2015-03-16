package com.etoc.weflow.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.etoc.weflow.R;
import com.etoc.weflow.activity.ExpenseFlowActivity;
import com.etoc.weflow.activity.MakeFlowActivity;
import com.etoc.weflow.activity.WebViewActivity;
import com.etoc.weflow.activity.login.LoginActivity;
import com.etoc.weflow.utils.ConStant;
import com.etoc.weflow.utils.ViewUtils;
import com.etoc.weflow.view.MagicTextView;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.nostra13.universalimageloader.core.ImageLoader;

public class HomePageFragment extends XFragment<Object>/*TitleRootFragment*/implements OnClickListener, OnRefreshListener2<ListView> {

	private final String TAG = "HomePageFragment";
	
	private LinearLayout makeFLowLayout;
	private LinearLayout expenseFlowLayout;
	private LayoutInflater inflater;
	private int makeFlowId = 0xffeecc00;
	private int expenseFlowId = 0xffeedd00;
	
	//UI Component
	private ImageView ivRecA, ivRecB;
	private MagicTextView mtvFlow;
	private TextView tvCellPhone = null;
	private TextView tvPlain = null;
	private TextView tvPlainType = null;
	private TextView tvInFlow = null;
	private TextView tvOutFlow = null;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreateView(inflater, container, savedInstanceState);
		View v = inflater.inflate(R.layout.fragment_homepage, null);
		initView(v);
		return v;
	}
	
	private void initView(View view) {
		inflater = LayoutInflater.from(getActivity());
		makeFLowLayout = (LinearLayout) view.findViewById(R.id.ll_make_flow);
		expenseFlowLayout =(LinearLayout) view.findViewById(R.id.ll_expense_flow);
		mtvFlow = (MagicTextView) view.findViewById(R.id.mtv_flow);
		
		mtvFlow.showNumberWithAnimation(98, 1000);
		
		tvCellPhone = (TextView) view.findViewById(R.id.tv_phone_num);
		tvPlain = (TextView) view.findViewById(R.id.tv_plans);
		tvPlainType = (TextView) view.findViewById(R.id.tv_plans_type);
		tvInFlow = (TextView) view.findViewById(R.id.tv_plans_in_left);
		tvOutFlow = (TextView) view.findViewById(R.id.tv_plans_out_left);
		
		
		ViewUtils.setTextSize(tvCellPhone, 32);
		ViewUtils.setTextSize(view.findViewById(R.id.tv_phone_num_hint), 32);
		ViewUtils.setHeight(view.findViewById(R.id.rl_user_phone), 58);
		ViewUtils.setMarginTop(view.findViewById(R.id.rl_user_phone), 20);
		ViewUtils.setSize(view.findViewById(R.id.rl_flow_account), 206,206);
		ViewUtils.setMarginTop(view.findViewById(R.id.rl_flow_account), 20);
		ViewUtils.setMarginTop(mtvFlow, 20);
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
		
		ViewUtils.setHeight(view.findViewById(R.id.ll_flow_change), 361);
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
		String [] expenseFlows = {"花流量","充值","订流量包","花流量币","换礼券"};
		for (int i = 0;i < 4;i++) {
			RelativeLayout layout = (RelativeLayout) inflater.inflate(R.layout.include_flow_buttons_stub,null);
			
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.MATCH_PARENT);
			layout.setLayoutParams(params);
			ViewUtils.setSize(layout.findViewById(R.id.view_space), 178, 180);
			ViewUtils.setMarginRight(layout, 2);
			layout.setId(makeFlowId + i);
			layout.setOnClickListener(this);
			TextView tvName = (TextView) layout.findViewById(R.id.tv_flow_name);
			tvName.setText(makeFlows[i]);
			ImageView ivModules = (ImageView) layout.findViewById(R.id.iv_flow_image);
			ViewUtils.setSize(ivModules, 68, 68);
			ViewUtils.setMarginBottom(tvName, 26);
			ViewUtils.setTextSize(tvName, 28);
			makeFLowLayout.addView(layout);
		}
		
		for (int i = 0;i < 5;i++) {
			RelativeLayout layout = (RelativeLayout) inflater.inflate(R.layout.include_flow_buttons_stub,null);
			
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.MATCH_PARENT);
			layout.setLayoutParams(params);
			ViewUtils.setSize(layout.findViewById(R.id.view_space), 178, 180);
			ViewUtils.setMarginRight(layout, 2);
			layout.setId(expenseFlowId + i);
			layout.setOnClickListener(this);
			TextView tvName = (TextView) layout.findViewById(R.id.tv_flow_name);
			tvName.setText(expenseFlows[i]);
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
	}
	
	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		Log.d(TAG, "onResume");
		if(mtvFlow != null)
			mtvFlow.showNumberWithAnimation(98, 1000);
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_title_left:
			
			break;
		case 0xffeecc00:
		case 0xffeecc01:
		case 0xffeecc02:
		case 0xffeecc03:
			Intent makeFlowIntent = new Intent(getActivity(),MakeFlowActivity.class);
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
			startActivity(expenseFlowIntent);
			break;
		case R.id.iv_recomm_1:
			Intent recIntent1 = new Intent(getActivity(), WebViewActivity.class);
			recIntent1.putExtra("pageurl", "http://detail.amap.com/telecom/");
			startActivity(recIntent1);
			break;
		case R.id.iv_recomm_2:
			Intent recIntent2 = new Intent(getActivity(), WebViewActivity.class);
			recIntent2.putExtra("pageurl", "http://detail.amap.com/telecom/");
			startActivity(recIntent2);
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
		if(mtvFlow != null)
			mtvFlow.showNumberWithAnimation(98, 1000);
	}

	@Override
	public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
		// TODO Auto-generated method stub
		
	}

}
