package com.cmmobi.railwifi.activity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.PopupWindow.OnDismissListener;

import cn.trinea.android.common.util.NetWorkUtils;

import com.cmmobi.railwifi.MainApplication;
import com.cmmobi.railwifi.R;
import com.cmmobi.railwifi.adapter.GoodAdapter;
import com.cmmobi.railwifi.adapter.MoreAdapter;
import com.cmmobi.railwifi.dialog.PromptDialog;
import com.cmmobi.railwifi.event.RequestEvent;
import com.cmmobi.railwifi.fragment.RailServiceFragment;
import com.cmmobi.railwifi.network.GsonResponseObject;
import com.cmmobi.railwifi.network.Requester;
import com.cmmobi.railwifi.network.GsonResponseObject.BaseInfoResp;
import com.cmmobi.railwifi.utils.CmmobiClickAgentWrapper;
import com.cmmobi.railwifi.utils.DisplayUtil;
import com.cmmobi.railwifi.utils.StringUtils;
import com.cmmobi.railwifi.utils.ViewUtils;
import com.google.gson.Gson;

import de.greenrobot.event.EventBus;

public class OrderShoppingActivity extends TitleRootActivity implements Callback {
	private final String TAG = "OrderShoppingActivity";
	private RelativeLayout mFullScreen;
	private RelativeLayout rlNoNet;
	private ListView mlvSelector;
	private PopupWindow mMorePopupWindow;
	private GridView pullGridView = null;
	private RelativeLayout rlHistoryOder = null;
	private TextView tvPrice = null;
	private Handler handler = null;
	private GoodAdapter goodAdapter;
	private GsonResponseObject.orderListResp orderResp;
	public static Map<String,GoodAdapter.OrderGoodWrap> orderMap = new HashMap<String, GoodAdapter.OrderGoodWrap>();
	private List<GsonResponseObject.orderElem> orderElemList = new ArrayList<GsonResponseObject.orderElem>();
	private String currentCategory = "全部";
	private String [] categorys = null;
	private String trainNum = null;
	private RelativeLayout rlNoShopping = null;
	private TextView tvNoShopping = null;
	
	public static final String INTENT_ORDER_CONFIRM = "intent_order_confirm";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		initViews();
		handler = new Handler(this);
		
		setTitleTextAndDrawable(currentCategory, R.drawable.top_close);
//		setRightButtonBackground(R.drawable.btn_collect);
//		setRightButtonBackground(null);
		getRightButton().setBackgroundDrawable(null);
		getRightButton().setText("提交订单>>");
		getRightButton().setTextSize(DisplayUtil.textGetSizeSp(this, 33));
		getRightButton().setEnabled(false);
		Requester.requestBaseInfo(handler);
		/*if (!StringUtils.isEmpty(MainActivity.train_num)){
			Requester.requestOrderList(handler,MainActivity.train_num);
		} else {
			SharedPreferences sharedPreferences= getSharedPreferences("base_info", 
					Activity.MODE_PRIVATE); 
			MainActivity.train_num = sharedPreferences.getString("train_num", null);
			MainActivity.dev_id = sharedPreferences.getString("dev_id", null);
			if (!StringUtils.isEmpty(MainActivity.train_num)) {
				Requester.requestOrderList(handler,MainActivity.train_num);
			} else {
				PromptDialog.Dialog(this, "获取菜单失败", "当前网络状态不佳，未获取到列车号", "稍后再试");
			}
		}*/
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		if (goodAdapter != null) {
			goodAdapter.notifyDataSetChanged();
			goodAdapter.updateTotalPrice();
		}
		if (orderMap.size() == 0) {
			getRightButton().setEnabled(false);
		}
		super.onResume();
	}
	
	private void initViews() {
		rlNoNet = (RelativeLayout) findViewById(R.id.rl_no_network);
		mFullScreen = (RelativeLayout) findViewById(R.id.rl_full_screen);
		rlHistoryOder = (RelativeLayout) findViewById(R.id.rl_history_order);
		tvPrice = (TextView) findViewById(R.id.tw_total_price);
		pullGridView = (GridView) findViewById(R.id.pgv_good_gridview);
		pullGridView.setSelector(new ColorDrawable(Color.TRANSPARENT));
		
		rlNoShopping = (RelativeLayout) findViewById(R.id.rl_cannot_shopping);
		tvNoShopping = (TextView) findViewById(R.id.tv_no_shopping);
		
		tvNoShopping.setTextSize(DisplayUtil.textGetSizeSp(this, 44));
		
		rlHistoryOder.setOnClickListener(this);
		ViewUtils.setMarginLeft(rlHistoryOder, 21);
		ViewUtils.setMarginTop(rlHistoryOder, 21);
		ViewUtils.setMarginBottom(rlHistoryOder, 21);
		ViewUtils.setMarginRight(tvPrice, 20);
		ViewUtils.setMarginLeft(pullGridView, 12);
		ViewUtils.setMarginRight(pullGridView, 12);
		ViewUtils.setMarginTop(pullGridView, 12);
		tvPrice.setTextSize(DisplayUtil.textGetSizeSp(this, 36));
	}
	
	MoreAdapter moreAdapter;
	public void initPopupwindow(String[] citys) {
		
		View view = getLayoutInflater().inflate(R.layout.more_popup_window,
				null);
		mlvSelector = (ListView) view.findViewById(R.id.lv_selector);
		ArrayList<String> list = new ArrayList<String>();
		Collections.addAll(list,citys);
		moreAdapter = new MoreAdapter(this, list);
		mlvSelector.setAdapter(moreAdapter);
		mlvSelector.setOnKeyListener(new OnKeyListener() {
			@Override
			public boolean onKey(View arg0, int arg1, KeyEvent arg2) {
				// TODO Auto-generated method stub
				if (arg2.getKeyCode() == KeyEvent.KEYCODE_MENU
						&& arg2.getAction() == KeyEvent.ACTION_UP
						&& mMorePopupWindow.isShowing()) {
					mMorePopupWindow.dismiss();
				}
				return false;
			}
		});
		mlvSelector.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				if (moreAdapter.getItem(arg2).equals("全部")) {
					//to get data
					goodAdapter.setData(orderElemList);
					goodAdapter.notifyDataSetChanged();
					CmmobiClickAgentWrapper.onEvent(OrderShoppingActivity.this, "t_food_sort","1");
				} else {
					//to get data
					if (moreAdapter.getItem(arg2).equals("美食")) {
						CmmobiClickAgentWrapper.onEvent(OrderShoppingActivity.this, "t_food_sort","2");
					} else if (moreAdapter.getItem(arg2).equals("零食")) {
						CmmobiClickAgentWrapper.onEvent(OrderShoppingActivity.this, "t_food_sort","3");
					}
					goodAdapter.setData(getCategoryGoods(moreAdapter.getItem(arg2)));
					goodAdapter.notifyDataSetChanged();
				}
				
				
				
				currentCategory = moreAdapter.getItem(arg2);
				mMorePopupWindow.dismiss();
			}
		});
		
		DisplayMetrics dm = new DisplayMetrics();
		dm = getApplicationContext().getResources()
				.getDisplayMetrics();
		mMorePopupWindow = new PopupWindow(view, DisplayUtil.getSize(this, 338), LayoutParams.WRAP_CONTENT, true);
		mMorePopupWindow.setBackgroundDrawable(getResources()
				.getDrawable(R.drawable.pop_window_bg));
		mMorePopupWindow.setOutsideTouchable(true);
		mMorePopupWindow.setOnDismissListener(new OnDismissListener() {
			
			@Override
			public void onDismiss() {
				// TODO Auto-generated method stub
				setTitleTextAndDrawable(currentCategory, R.drawable.top_close);
			}
		});
	}
	
	ArrayList<String> getPopupWindowData(){
		ArrayList<String> cities = new ArrayList<String>();
		if(categorys == null) return cities;
		for(int i=0; i<categorys.length;i++){
			if(currentCategory.equals(categorys[i])){
				continue;
			}else{
				cities.add(categorys[i]);
			}
		}
		return cities;
	}
	
	void updatePopupWindow(ArrayList<String> data){
		moreAdapter.setData(data);
		moreAdapter.notifyDataSetChanged();
	}

	@Override
	public boolean handleMessage(Message msg) {
		// TODO Auto-generated method stub
		switch (msg.what) {
		case Requester.RESPONSE_TYPE_ORDERLIST:
			if(msg.obj != null) {
				orderResp = (GsonResponseObject.orderListResp) msg.obj;
				if ("0".equals(orderResp.status)) {
					if (orderResp.list.length != 0) {
						Collections.addAll(orderElemList,orderResp.list);
						goodAdapter = new GoodAdapter(this, orderElemList, orderMap);
						goodAdapter.setTotalPriceTextView(tvPrice);
						pullGridView.setAdapter(goodAdapter);
						goodAdapter.notifyDataSetChanged();
						categorys = orderResp.name;
						initPopupwindow(getPopupWindowData().toArray(new String[0]));
					} else {
						rlNoShopping.setVisibility(View.VISIBLE);
					}
				} else {
					EventBus.getDefault().post(RequestEvent.RESP_NULL);
					rlNoNet.setVisibility(View.VISIBLE);
				}
			} else {
				rlNoNet.setVisibility(View.VISIBLE);
			}
			break;
		case Requester.RESPONSE_TYPE_BASE_INFO:
			if (msg.obj != null) {
				BaseInfoResp resp = (BaseInfoResp) msg.obj;
				if ("0".equals(resp.status)) {
					trainNum = resp.train_num;
					if (trainNum != null) {
						Requester.requestOrderList(handler,trainNum);
					} else {
						rlNoNet.setVisibility(View.VISIBLE);
					}
				} else {
					Toast.makeText(MainApplication.getAppInstance(), "检测您当前不在列车上...", Toast.LENGTH_LONG).show();
//					PromptDialog.Dialog(this, "温馨提示", "网络错误 ：" + resp.status, "确定");
					rlNoNet.setVisibility(View.VISIBLE);
				}
			} else {
				rlNoNet.setVisibility(View.VISIBLE);
			}
			break;
		}
		return false;
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		orderMap.clear();
		super.onDestroy();
	}

	@Override
	public int subContentViewId() {
		// TODO Auto-generated method stub
		return R.layout.activity_order_shopping;
	}
	
	private List<GsonResponseObject.orderElem> getCategoryGoods(String type) {
		List<GsonResponseObject.orderElem> list = new ArrayList<GsonResponseObject.orderElem>();
		for (GsonResponseObject.orderElem item:orderElemList) {
			if (type.equals(item.type)) {
				list.add(item);
			}
		}
		return list;
	}
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		super.onClick(v);
		switch (v.getId()) {
		case R.id.tv_title:
		case R.id.iv_title:
			if (mMorePopupWindow == null) {
				return;
			}
			if (mMorePopupWindow.isShowing()) {
				mMorePopupWindow.dismiss();
			} else {
				CmmobiClickAgentWrapper.onEvent(OrderShoppingActivity.this, "t_food","4");
				updatePopupWindow(getPopupWindowData());
				mMorePopupWindow.showAtLocation(mFullScreen, Gravity.CENTER_HORIZONTAL|Gravity.TOP, 0, DisplayUtil.getSize(this, 96+24));
				//mMorePopupWindow.showAsDropDown(getTvTitle(), -DisplayUtil.getSize(this, 338/2), -3);
				setTitleTextAndDrawable(currentCategory, R.drawable.top_open);
			}
			break;
		case R.id.btn_title_right:
			CmmobiClickAgentWrapper.onEvent(this, "t_food","3");
			Intent confirmIntent = new Intent(this, OrderConfirmActivity.class);
			startActivity(confirmIntent);
			break;
		case R.id.rl_history_order:
			Intent historyIntent = new Intent(this, HistoryOrderFormActivity.class);
			startActivity(historyIntent);
			break;
		}
	}
}
