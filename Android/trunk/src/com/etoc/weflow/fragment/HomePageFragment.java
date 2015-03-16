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
