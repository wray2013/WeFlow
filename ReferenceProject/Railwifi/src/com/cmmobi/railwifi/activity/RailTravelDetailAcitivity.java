package com.cmmobi.railwifi.activity;

import java.util.HashMap;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.opengl.Visibility;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cmmobi.railwifi.R;
import com.cmmobi.railwifi.dialog.PromptDialog;
import com.cmmobi.railwifi.fragment.RailTravelCheckOneFragment;
import com.cmmobi.railwifi.fragment.RailTravelCheckThreeFragment;
import com.cmmobi.railwifi.fragment.RailTravelCheckTwoFragment;
import com.cmmobi.railwifi.network.GsonResponseObject;
import com.cmmobi.railwifi.network.GsonResponseObject.LineInfo;
import com.cmmobi.railwifi.network.GsonResponseObject.newsElem;
import com.cmmobi.railwifi.network.Requester;
import com.cmmobi.railwifi.utils.CmmobiClickAgentWrapper;
import com.cmmobi.railwifi.utils.DisplayUtil;
import com.google.gson.Gson;

/**
 * @author youtian
 * @email youtian@cmmobi.com
 * @date  2014-11-18
 */
public class RailTravelDetailAcitivity extends TitleRootActivity {

	private String TAG = "RailTravelDetailAcitivity";
	private TextView tvFullName;
	private RelativeLayout rlTopInfo;
	private RelativeLayout rlPriceInfo;
	private TextView tvStart;
	private TextView tvAdultPrice;
	private TextView tvKidPrice;
	private ImageView ivLine;

	private LinearLayout llDetail;
	private LinearLayout llCheckTab;
	private Button btnCheckOne;
	private Button btnCheckTwo;
	private Button btnCheckThree;
	
	public Fragment currFragment;
	private GsonResponseObject.travelLineInfoResp lineInfoResp;
	
	private String lineid;
	
	public static int REQUEST_CODE_SUCCESS = 0X1234;
	
	@Override
	public int subContentViewId() {
		return R.layout.activity_railtravel_detail;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		lineid = this.getIntent().getStringExtra("lineid");
		
		setTitleText("");
		setRightButtonText("订购>>");
		
		tvFullName = (TextView) findViewById(R.id.tv_fullname);
		tvFullName.setTextSize(DisplayUtil.textGetSizeSp(this, 33));
		tvFullName.setTypeface(Typeface.MONOSPACE,Typeface.ITALIC);
		RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) tvFullName.getLayoutParams();
		lp.height = DisplayUtil.getSize(this, 60);
		lp.leftMargin = DisplayUtil.getSize(this, 24);
		tvFullName.setLayoutParams(lp);
		
		rlPriceInfo = (RelativeLayout)findViewById(R.id.rl_price_info);
		lp = (RelativeLayout.LayoutParams) rlPriceInfo.getLayoutParams();
		lp.height = DisplayUtil.getSize(this, 106);
		rlPriceInfo.setLayoutParams(lp);
		
		tvAdultPrice = (TextView) findViewById(R.id.tv_adult_price);
		tvAdultPrice.setPadding(0, 0, DisplayUtil.getSize(this, 35), DisplayUtil.getSize(this, 12));
		tvAdultPrice.setTextSize(DisplayUtil.textGetSizeSp(this, 27));
		
		tvKidPrice = (TextView) findViewById(R.id.tv_kid_price);
		
		tvKidPrice.setTextSize(DisplayUtil.textGetSizeSp(this, 27));
		
		tvStart = (TextView) findViewById(R.id.tv_start);
		tvStart.setPadding(DisplayUtil.getSize(this, 24), 0, 0, 0);
		tvStart.setTextSize(DisplayUtil.textGetSizeSp(this, 27));
		
		ivLine = (ImageView) findViewById(R.id.iv_line);
		lp = (RelativeLayout.LayoutParams) ivLine.getLayoutParams();
		lp.rightMargin = DisplayUtil.getSize(this, 12);
		lp.leftMargin = lp.rightMargin;
		ivLine.setLayoutParams(lp);
		
		rlTopInfo = (RelativeLayout)findViewById(R.id.rl_top_info);
		LinearLayout.LayoutParams llp = (LinearLayout.LayoutParams) rlTopInfo.getLayoutParams();
		llp.topMargin = DisplayUtil.getSize(this, 12);
		llp.bottomMargin = DisplayUtil.getSize(this, 21);
		rlTopInfo.setLayoutParams(llp);
		
		llDetail = (LinearLayout) findViewById(R.id.ll_details);
		//llDetail.setPadding(DisplayUtil.getSize(this, 12), 0, DisplayUtil.getSize(this, 12), DisplayUtil.getSize(this, 12));
		
		llCheckTab = (LinearLayout) findViewById(R.id.ll_check_tab);
		llCheckTab.setPadding(DisplayUtil.getSize(this, 12), 0, DisplayUtil.getSize(this, 12), 0);
		
		btnCheckOne = (Button) findViewById(R.id.btn_check_one);
		btnCheckOne.setOnClickListener(this);
		btnCheckTwo = (Button) findViewById(R.id.btn_check_two);
		btnCheckTwo.setOnClickListener(this);
		btnCheckThree = (Button) findViewById(R.id.btn_check_three);
		btnCheckThree.setOnClickListener(this);
		
		btnCheckOne.setTextSize(DisplayUtil.textGetSizeSp(this, 30));
		btnCheckTwo.setTextSize(DisplayUtil.textGetSizeSp(this, 30));
		btnCheckThree.setTextSize(DisplayUtil.textGetSizeSp(this, 30));
		
		if(!TextUtils.isEmpty(lineid)){
			Requester.requestTravelLineInfo(handler, lineid);
		}
	}
	
	
	

	
	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		
	}
	
	@Override
	public void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
	}

	
	
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		super.onClick(v);
		switch(v.getId()){
		case R.id.btn_title_right:
			CmmobiClickAgentWrapper.onEvent(this, "t_tra_line", "4");
			Intent infoIntent = new Intent(this, RailTravelOrderInfoActivity.class);
			if(lineInfoResp!=null && !TextUtils.isEmpty(lineid)){
				infoIntent.putExtra("lineinfo", new Gson().toJson(lineInfoResp, GsonResponseObject.travelLineInfoResp.class));
				infoIntent.putExtra("lineid", lineid);
			}
			startActivityForResult(infoIntent, REQUEST_CODE_SUCCESS);
			break;
		case R.id.btn_check_one:
		case R.id.btn_check_two:
		case R.id.btn_check_three:
			onCheckChanged(v.getId());
			break;
			default:{
				
			}	
		}
	}	
	
	
	private void onCheckChanged(int id) {
		FragmentManager fm = getSupportFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();
		if(currFragment != null){
			ft.hide(currFragment);
		}
		Fragment fragment = null;
		switch (id) {
		case R.id.btn_check_one:
			CmmobiClickAgentWrapper.onEvent(this, "t_tra_line", "1");
			fragment = fm.findFragmentByTag(RailTravelCheckOneFragment.class.getName());
			if (fragment == null) {
				fragment = new RailTravelCheckOneFragment();
				if(lineInfoResp!=null && "0".equals(lineInfoResp.status)){
					Bundle bundle = new Bundle();
					bundle.putString("services", lineInfoResp.services);
					bundle.putString("linepoint", lineInfoResp.linepoint);
					bundle.putString("img", lineInfoResp.in_img_path);
					fragment.setArguments(bundle);
				}
				ft.add(R.id.empty, fragment, RailTravelCheckOneFragment.class.getName());
			} else {
				ft.show(fragment);
			}
			currFragment = fragment;		
			btnCheckOne.setTextColor(Color.WHITE);
			btnCheckTwo.setTextColor(Color.parseColor("#212434"));
			btnCheckThree.setTextColor(Color.parseColor("#212434"));
			btnCheckOne.setBackgroundResource(R.drawable.table_left_selected_3);
			btnCheckTwo.setBackgroundResource(R.drawable.table_middle_default_3);
			btnCheckThree.setBackgroundResource(R.drawable.table_right_default_3);
			break;
		case R.id.btn_check_two:
			CmmobiClickAgentWrapper.onEvent(this, "t_tra_line", "2");
			fragment = fm.findFragmentByTag(RailTravelCheckTwoFragment.class.getName());
			if (fragment == null) {
				fragment = new RailTravelCheckTwoFragment();
				if(lineInfoResp!=null && "0".equals(lineInfoResp.status)){
					Bundle bundle = new Bundle();
					bundle.putString("info", new Gson().toJson(lineInfoResp, GsonResponseObject.travelLineInfoResp.class));
					fragment.setArguments(bundle);
				}
				ft.add(R.id.empty, fragment, RailTravelCheckTwoFragment.class.getName());
			} else {
				ft.show(fragment);
			}
			currFragment = fragment;

			btnCheckOne.setTextColor(Color.parseColor("#212434"));
			btnCheckTwo.setTextColor(Color.WHITE);
			btnCheckThree.setTextColor(Color.parseColor("#212434"));
			btnCheckOne.setBackgroundResource(R.drawable.table_left_default_3);
			btnCheckTwo.setBackgroundResource(R.drawable.table_middle_selected_3);
			btnCheckThree.setBackgroundResource(R.drawable.table_right_default_3);
			break;
		case R.id.btn_check_three:
			CmmobiClickAgentWrapper.onEvent(this, "t_tra_line", "3");
			fragment = fm.findFragmentByTag(RailTravelCheckThreeFragment.class.getName());
			if (fragment == null) {
				fragment = new RailTravelCheckThreeFragment();
				if(lineInfoResp!=null && "0".equals(lineInfoResp.status)){
					Bundle bundle = new Bundle();
					bundle.putString("notice", lineInfoResp.notice);
					bundle.putString("remind", lineInfoResp.remind);
					bundle.putString("attention", lineInfoResp.attention);
					fragment.setArguments(bundle);
				}
				ft.add(R.id.empty, fragment, RailTravelCheckThreeFragment.class.getName());
			} else {
				ft.show(fragment);
			}
			currFragment = fragment;

			btnCheckOne.setTextColor(Color.parseColor("#212434"));
			btnCheckTwo.setTextColor(Color.parseColor("#212434"));
			btnCheckThree.setTextColor(Color.WHITE);
			btnCheckOne.setBackgroundResource(R.drawable.table_left_default_3);
			btnCheckTwo.setBackgroundResource(R.drawable.table_middle_default_3);
			btnCheckThree.setBackgroundResource(R.drawable.table_right_selected_3);
			break;
		default:
			break;
		}
		
		
		try {
			ft.commitAllowingStateLoss();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		
	}
	
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	@Override
	public boolean handleMessage(Message msg) {
		// TODO Auto-generated method stub
		switch (msg.what) {
		case Requester.RESPONSE_TYPE_TRAVEL_LINEINFO:
			lineInfoResp = (GsonResponseObject.travelLineInfoResp) msg.obj;
			if(lineInfoResp != null && "0".equals(lineInfoResp.status)){
				findViewById(R.id.rl_content).setVisibility(View.VISIBLE);
				findViewById(R.id.rl_empty).setVisibility(View.GONE);
				getRightButton().setEnabled(true);
				
				setTitleText(lineInfoResp.name);
				tvFullName.setText(lineInfoResp.fullname + " ");
				tvStart.setText("出发地：" + lineInfoResp.startaddress);
				tvAdultPrice.setText(Html.fromHtml("成人价：<font color=\"#f65c00\">"+ lineInfoResp.adult_price + "元起/人"+"</font>"));
				tvKidPrice.setText(Html.fromHtml("儿童价：<font color=\"#f65c00\">"+ lineInfoResp.kid_price + "元起/人"+"</font>"));
				onCheckChanged(R.id.btn_check_one);
			}else{
				getRightButton().setEnabled(false);
				findViewById(R.id.rl_content).setVisibility(View.GONE);
				findViewById(R.id.rl_empty).setVisibility(View.VISIBLE);
			}
			break;
		default:
			break;
		}
		return false;
	}

	@Override
	protected void onActivityResult(int arg0, int arg1, Intent arg2) {
		// TODO Auto-generated method stub
		super.onActivityResult(arg0, arg1, arg2);
		if(arg0 == REQUEST_CODE_SUCCESS && arg1 == RESULT_OK){
			this.finish();
		}
	}
}
