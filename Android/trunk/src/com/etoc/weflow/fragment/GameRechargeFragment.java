package com.etoc.weflow.fragment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.app.ActionBar.LayoutParams;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.etoc.weflow.R;
import com.etoc.weflow.WeFlowApplication;
import com.etoc.weflow.activity.login.LoginActivity;
import com.etoc.weflow.adapter.MoreAdapter;
import com.etoc.weflow.dao.AccountInfo;
import com.etoc.weflow.dialog.OrderDialog;
import com.etoc.weflow.dialog.PromptDialog;
import com.etoc.weflow.event.GameCoinEvent;
import com.etoc.weflow.net.GsonResponseObject.ExchangeFlowPkgResp;
import com.etoc.weflow.net.GsonResponseObject.GameChargeListResp;
import com.etoc.weflow.net.GsonResponseObject.GameChargeProductResp;
import com.etoc.weflow.net.GsonResponseObject.GameChargeResp;
import com.etoc.weflow.net.GsonResponseObject.GameRechargeResp;
import com.etoc.weflow.net.Requester;
import com.etoc.weflow.utils.ConStant;
import com.etoc.weflow.utils.DisplayUtil;
import com.etoc.weflow.utils.NumberUtils;
import com.etoc.weflow.utils.StringUtils;
import com.etoc.weflow.utils.ViewUtils;

import de.greenrobot.event.EventBus;

public class GameRechargeFragment extends Fragment implements Callback, OnClickListener {
	
	private Handler handler = null;
	private View mView;
	private PopupWindow gamePopupWindow;
	private PopupWindow coinsPopupWindow;
	private MoreAdapter gameAdapter;
	private MoreAdapter coinsAdapter;
	private ListView gameListView;
	private ListView coinsListView;
	private View gameView;
	private View coinsView;
	private List<String> gameStrList = new ArrayList<String>();
	private List<String> coinStrList = new ArrayList<String>();
	private List<GameChargeResp> gameChargeList = new ArrayList<GameChargeResp>();
	private List<GameChargeProductResp> productList = new ArrayList<GameChargeProductResp>();
	private RelativeLayout rlGameType = null;
	private RelativeLayout rlCoins = null;
	private EditText etAccount;
	private TextView tvGameType = null;
	private TextView tvGameCoins = null;
	private TextView tvFlowCoins = null;
	private TextView tvBtnOrder = null;
	private int selectGameType = -1;
	
	private GameChargeProductResp selectProduct = null;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		EventBus.getDefault().register(this);
		handler = new Handler(this);
	}
	
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		EventBus.getDefault().unregister(this);
		super.onDestroy();
	}
	
	public void onEvent(GameCoinEvent event) {
		if (event == GameCoinEvent.GAMERECHARGE) {
			if (gameChargeList.size() == 0) {
				Requester.getGameChargeList(true, handler);
			}
		}
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		if(mView != null){
		    ViewGroup parent = (ViewGroup) mView.getParent();  
		    if (parent != null) {  
		        parent.removeView(mView);  
		    }   
		    return mView;
		}
		super.onCreateView(inflater, container, savedInstanceState);
		
		View view = inflater.inflate(R.layout.fragment_game_recharge, null);
		mView = view;
		initView(view);
		initPopupWindow();
		
		if (gameChargeList.size() == 0) {
			Requester.getGameChargeList(true, handler);
		}
		return view;
	}
	
	private void initView(View view) {
		rlGameType = (RelativeLayout) view.findViewById(R.id.rl_game_type);
		rlCoins = (RelativeLayout) view.findViewById(R.id.rl_game_coins);
		etAccount = (EditText) view.findViewById(R.id.et_game_account);
		
		tvGameType = (TextView) view.findViewById(R.id.tv_game_name);
		tvGameCoins = (TextView) view.findViewById(R.id.tv_game_coins);
		tvFlowCoins = (TextView) view.findViewById(R.id.tv_cost_flowcoins);
		tvBtnOrder = (TextView) view.findViewById(R.id.tv_btn_order);
		tvBtnOrder.setOnClickListener(this);
		rlGameType.setOnClickListener(this);
		rlCoins.setOnClickListener(this);
		ViewUtils.setSize(tvBtnOrder, 552, 96);
		ViewUtils.setMarginBottom(tvBtnOrder, 48);
		
		ViewUtils.setHeight(rlGameType, 112);
		ViewUtils.setMarginLeft(rlGameType, 32);
		ViewUtils.setMarginRight(rlGameType, 32);
		ViewUtils.setHeight(rlCoins, 112);
		ViewUtils.setHeight(etAccount, 112);
		ViewUtils.setMarginTop(etAccount, 48);
		ViewUtils.setMarginTop(rlCoins, 48);
		ViewUtils.setTextSize(tvGameType, 32);
		ViewUtils.setTextSize(tvGameCoins, 32);
		ViewUtils.setTextSize(view.findViewById(R.id.tv_game_select), 32);
		ViewUtils.setMarginLeft(view.findViewById(R.id.tv_game_select), 32);
		ViewUtils.setTextSize(view.findViewById(R.id.tv_game_coins_label), 32);
		ViewUtils.setMarginLeft(view.findViewById(R.id.tv_game_coins_label), 24);
		ViewUtils.setTextSize(view.findViewById(R.id.tv_cost_flowcoins_label), 32);
		ViewUtils.setMarginTop(view.findViewById(R.id.tv_cost_flowcoins_label), 68);
		ViewUtils.setTextSize(tvFlowCoins, 36);
		ViewUtils.setMarginTop(tvFlowCoins, 36);
		
		ViewUtils.setSize(view.findViewById(R.id.view_arraw_coins), 72, 72);
		ViewUtils.setSize(view.findViewById(R.id.view_arraw), 72, 72);
	}
	
	@Override
	public boolean handleMessage(Message msg) {
		// TODO Auto-generated method stub
		switch (msg.what) {
		case Requester.RESPONSE_TYPE_GAME_RECHARGE_LIST:
			if (msg.obj != null) {
				GameChargeListResp resp = (GameChargeListResp) msg.obj;
				if(resp.status.equals("0000") || resp.status.equals("0")) {
					if (resp.chargelist != null && resp.chargelist.length > 0) {
						gameChargeList.clear();
						Collections.addAll(gameChargeList, resp.chargelist);
						
						for (GameChargeResp item:gameChargeList) {
							gameStrList.add(item.typename);
							Log.d("=AAA=","item.typename = " + item.typename);
						}
						
						gameAdapter.notifyDataSetChanged();
						Log.d("=AAA=","gameAdapter count = " + gameAdapter.getCount());
					}
				}
			}
			break;
		case Requester.RESPONSE_TYPE_GAME_RECHARGE:
			if (msg.obj != null) {
				GameRechargeResp chargeResp = (GameRechargeResp) msg.obj;
				if (Requester.isSuccessed(chargeResp.status)) {
//					PromptDialog.Alert("订购成功");
					OrderDialog.Dialog(getActivity(), "兑换码：" + chargeResp.cardcode+ "\n请尽快使用");
					WeFlowApplication.getAppInstance().setFlowCoins(chargeResp.flowcoins);
					/*if (!StringUtils.isEmpty(chargeResp.cardcode)) {
						PromptDialog.Dialog(getActivity(), "温馨提示", "订购成功，兑换码: " + chargeResp.cardcode + "\n请尽快使用", "确定");
					}*/
				} else if (Requester.isProcessed(chargeResp.status)){
//					PromptDialog.Alert("订购已处理");
					OrderDialog.Dialog(getActivity(), "订购已受理");
					WeFlowApplication.getAppInstance().setFlowCoins(chargeResp.flowcoins);
				} else if (Requester.isLowFlow(chargeResp.status)) {
					OrderDialog.Dialog(getActivity(), ConStant.LOW_FLOW, true);
//					PromptDialog.Alert(ConStant.LOW_FLOW);
				}  else {
					OrderDialog.Dialog(getActivity(), ConStant.ORDER_FAIL, true);
//					PromptDialog.Alert(ConStant.ORDER_FAIL);
				}
			}
			break;
		}
		return false;
	}
	
	private void initPopupWindow() {
		gameAdapter = new MoreAdapter(getActivity(), gameStrList);
		
		coinsAdapter = new MoreAdapter(getActivity(), coinStrList);
		gameView = getActivity().getLayoutInflater().inflate(R.layout.item_select_popup_window,
				null);
		gameListView = (ListView) gameView.findViewById(R.id.lv_selector);
		gameListView.setAdapter(gameAdapter);
		
		coinsView = getActivity().getLayoutInflater().inflate(R.layout.item_select_popup_window,
				null);
		coinsListView = (ListView) coinsView.findViewById(R.id.lv_selector);
		coinsListView.setAdapter(coinsAdapter);
		
		gamePopupWindow = createPopupWindow(gameView);
		coinsPopupWindow = createPopupWindow(coinsView);
		
		gameListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {
				// TODO Auto-generated method stub
				if (position == selectGameType) {
					gamePopupWindow.dismiss();
					return;
				}
				selectGameType = position;
				GameChargeResp resp = gameChargeList.get(position);
				if (resp.products != null && resp.products.length > 0) {
					coinStrList.clear();
					productList.clear();
					for (GameChargeProductResp item:resp.products) {
						productList.add(item);
						coinStrList.add(item.money);
					}
					tvGameCoins.setText("");
					coinsAdapter.notifyDataSetChanged();
					gamePopupWindow.dismiss();
				}
				tvGameType.setText(resp.typename);
			}
		});
		
		coinsListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {
				// TODO Auto-generated method stub
				selectProduct = productList.get(position);
				tvFlowCoins.setText(NumberUtils.convert2IntStr(selectProduct.cost) + "流量币");
				tvGameCoins.setText(selectProduct.money);
				coinsPopupWindow.dismiss();
			}
		});
		
	}
	
	private PopupWindow createPopupWindow(View view) {
		
		DisplayMetrics dm = new DisplayMetrics();
		dm = WeFlowApplication.getAppInstance().getResources()
				.getDisplayMetrics();
		final PopupWindow mMorePopupWindow = new PopupWindow(view, DisplayUtil.getSize(WeFlowApplication.getAppInstance(), 656), LayoutParams.WRAP_CONTENT, true);
		mMorePopupWindow.setBackgroundDrawable(getResources().getDrawable(R.drawable.drawable_white));
		mMorePopupWindow.setOutsideTouchable(true);
		mMorePopupWindow.setOnDismissListener(new OnDismissListener() {
			
			@Override
			public void onDismiss() {
				// TODO Auto-generated method stub
			}
		});
		
		return mMorePopupWindow;
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()) {
		case R.id.tv_btn_order:
			if (StringUtils.isEmpty(tvGameCoins.getText().toString())) {
				PromptDialog.Dialog(getActivity(), "温馨提示", "请选择充值面额", "确定");
				return;
			}
			if (StringUtils.isEmpty(etAccount.getText().toString())) {
				PromptDialog.Dialog(getActivity(), "温馨提示", "请输入游戏帐号", "确定");
				return;
			}
			AccountInfo accountInfo = WeFlowApplication.getAppInstance().getAccountInfo();
			if (accountInfo != null && accountInfo.getUserid() != null) {
				Requester.rechargeGame(true, handler, accountInfo.getUserid(), selectProduct.chargesid,etAccount.getText().toString());
			} else {
				startActivity(new Intent(getActivity(), LoginActivity.class));
			}
			break;
		case R.id.rl_game_type:
			if (gamePopupWindow == null) {
				return;
			}
			if (gamePopupWindow.isShowing()) {
				gamePopupWindow.dismiss();
			} else {
//				gamePopupWindow.showAtLocation(rlGameType, Gravity.CENTER_HORIZONTAL|Gravity.BOTTOM, 0, DisplayUtil.getSize(getActivity(), 112));
				gamePopupWindow.showAsDropDown(rlGameType);
				int height = ViewUtils.getListHeight(gameListView, DisplayUtil.getSize(getActivity(), 96));
				if (height > DisplayUtil.getScreenHeight(getActivity()) / 2) {
					ViewUtils.setHeightPixel(gameListView,DisplayUtil.getScreenHeight(getActivity()) / 2);
				} else {
					ViewUtils.setHeightPixel(gameListView,height);
				}
			}
			break;
		case R.id.rl_game_coins:
			if (coinsPopupWindow == null || coinStrList.size() == 0) {
				return;
			}
			if (coinsPopupWindow.isShowing()) {
				coinsPopupWindow.dismiss();
			} else {
//				gamePopupWindow.showAtLocation(rlGameType, Gravity.CENTER_HORIZONTAL|Gravity.BOTTOM, 0, DisplayUtil.getSize(getActivity(), 112));
				coinsPopupWindow.showAsDropDown(rlCoins);
				int height = ViewUtils.getListHeight(coinsListView, DisplayUtil.getSize(getActivity(), 96));
				Log.d("=AAA=","height = " + height);
				if (height > DisplayUtil.getScreenHeight(getActivity()) / 2) {
					ViewUtils.setHeightPixel(coinsListView,DisplayUtil.getScreenHeight(getActivity()) / 2);
				} else {
					ViewUtils.setHeightPixel(coinsListView,height);
				}
			}
			break;
		}
	}

}
