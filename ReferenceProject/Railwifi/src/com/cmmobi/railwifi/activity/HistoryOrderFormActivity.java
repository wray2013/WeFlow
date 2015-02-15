package com.cmmobi.railwifi.activity;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cmmobi.railwifi.R;
import com.cmmobi.railwifi.adapter.HistoryOrderFormAdapter;
import com.cmmobi.railwifi.dao.DaoMaster;
import com.cmmobi.railwifi.dao.DaoMaster.DevOpenHelper;
import com.cmmobi.railwifi.dao.DaoSession;
import com.cmmobi.railwifi.dao.HistoryOrderForm;
import com.cmmobi.railwifi.dao.HistoryOrderFormDao;
import com.cmmobi.railwifi.network.GsonRequestObject.ReqGoodOrder;
import com.cmmobi.railwifi.network.GsonResponseObject;
import com.cmmobi.railwifi.network.Requester;
import com.cmmobi.railwifi.utils.CmmobiClickAgentWrapper;
import com.cmmobi.railwifi.utils.DisplayUtil;
import com.cmmobi.railwifi.utils.ViewUtils;
import com.google.gson.Gson;
import com.nostra13.universalimageloader.api.MyImageLoader;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;

public class HistoryOrderFormActivity extends TitleRootActivity {

	private DaoMaster daoMaster;
	private DaoSession daoSession;
	private SQLiteDatabase db;
	private HistoryOrderFormDao historyOrderFormDao;
	private List<HistoryOrderForm> historyOrderList;
	private HistoryOrderFormAdapter historyOrderAdapter;
	private ListView listView;
	private RelativeLayout rlEmpty;
	private	Button btnGoShopping;
	private ImageView ivEmpty;
	
	public HistoryOrderForm findOrder(String orderNum) {
		for (HistoryOrderForm item:historyOrderList) {
			if (orderNum.equals(item.getOrder_code())) {
				return item;
			}
		}
		return null;
	}
	
	@Override
	public boolean handleMessage(Message msg) {
		// TODO Auto-generated method stub
		switch(msg.what) {
		case Requester.RESPONSE_TYPE_GOOD_ORDER:
			if (msg.obj != null) {
				GsonResponseObject.GoodOrderResp resp = (GsonResponseObject.GoodOrderResp) msg.obj;
//				List<HistoryOrderForm> orderList = historyOrderFormDao.queryBuilder().where(Properties.Order_code.eq(resp.order_code)).list();
				HistoryOrderForm item = findOrder(resp.order_code);
				if ("0".equals(resp.status)) {
//					if (orderList != null && orderList.size() > 0) {
//						orderList.get(0).setStatus("0");
//					}
					
					item.setStatus("0");
				} else {
//					if (orderList != null && orderList.size() > 0) {
//						orderList.get(0).setStatus("0");
//					}
					item.setStatus(resp.status);
				}
				historyOrderFormDao.update(item);
				historyOrderAdapter.notifyDataSetChanged();
			} else {
				
			}
			break;
		case Requester.RESPONSE_TYPE_ORDER_STATUS:
			if (msg.obj != null) {
				GsonResponseObject.OrderStatusResp resp = (GsonResponseObject.OrderStatusResp) msg.obj;
//				List<HistoryOrderForm> orderList = historyOrderFormDao.queryBuilder().where(Properties.Order_code.eq(resp.order_code)).list();
				HistoryOrderForm item = findOrder(resp.order_code);
				if ("0".equals(resp.status)) {
//					if (orderList != null && orderList.size() > 0) {
//						orderList.get(0).setStatus("0");
//					}
					
					item.setStatus("0");
				} else {
//					if (orderList != null && orderList.size() > 0) {
//						orderList.get(0).setStatus("0");
//					}
					if (item.getStatus().equals(resp.status)) {
						break;
					}
					item.setStatus(resp.status);
				}
				historyOrderFormDao.update(item);
				historyOrderAdapter.notifyDataSetChanged();
			} else {
				
			}
			break;
		}
		return false;
	}

	@Override
	public int subContentViewId() {
		// TODO Auto-generated method stub
		return R.layout.activity_history_order_form;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		setTitleText("历史订单");
		initViews();
		
		getRightButton().setBackgroundDrawable(null);

		String requestStr = getIntent().getStringExtra("request");
		ReqGoodOrder request = new Gson().fromJson(requestStr, ReqGoodOrder.class);
		String orderNum = null;
		if (request != null) {
			Requester.requestGoodOrder(handler, 
					request.contacts, 
					request.contacts_telephone, 
					request.customer_car, 
					request.customer_seat, 
					request.list, 
					request.train_num, 
					request.order_code,
					request.order_type,
					request.people_num);
			orderNum = request.order_code;
		}
		
		DevOpenHelper helper = new DaoMaster.DevOpenHelper(this, "railwifidb", null);
		db = helper.getWritableDatabase();
		daoMaster = new DaoMaster(db);
		daoSession = daoMaster.newSession();
		historyOrderFormDao = daoSession.getHistoryOrderFormDao();
		
		historyOrderList = historyOrderFormDao.loadAll();
		
		if (historyOrderList == null || historyOrderList.size() == 0) {
			CmmobiClickAgentWrapper.onEvent(this, "empty_2");
			rlEmpty.setVisibility(View.VISIBLE);
		} else {
			Collections.sort(historyOrderList,new Comparator<HistoryOrderForm>() {

				@Override
				public int compare(HistoryOrderForm lhs, HistoryOrderForm rhs) {
					// TODO Auto-generated method stub
					return -1 * lhs.getOrder_time().compareTo(rhs.getOrder_time());
				}
			});
			
			for (HistoryOrderForm item:historyOrderList) {
				if ("1".equals(item.getStatus()) && "-1".equals(item.getStatus())) {
					Requester.requestOrderStatus(handler, item.getOrder_code());
				}
			}
			
			historyOrderAdapter = new HistoryOrderFormAdapter(this, historyOrderList,handler);
			listView.setAdapter(historyOrderAdapter);
		}
	}
	
	private void initViews() {
		// TODO Auto-generated method stub
		listView = (ListView) findViewById(R.id.lv_history_order);
		ivEmpty = (ImageView) findViewById(R.id.iv_empty);
		
		try {
			ivEmpty.setImageResource(R.drawable.image_order);
		} catch (Error e) {
			e.printStackTrace();
		}
		View headView = getLayoutInflater().inflate(R.layout.header_histrory_order_list, null);
		listView.addHeaderView(headView);
		ViewUtils.setMarginTop(findViewById(R.id.iv_order_form), 16);
		ViewUtils.setMarginLeft(findViewById(R.id.iv_order_form), 30);
		ViewUtils.setMarginTop(findViewById(R.id.tv_order_info), 24);
		ViewUtils.setMarginLeft(findViewById(R.id.tv_order_info), 21);
		ViewUtils.setMarginTop(findViewById(R.id.view_bottom), 12);
		((TextView) findViewById(R.id.tv_order_info)).setTextSize(DisplayUtil.textGetSizeSp(this, 33));
		
		ViewUtils.setMarginLeft(listView, 12);
		ViewUtils.setMarginRight(listView, 12);
		listView.setDividerHeight(24);
		
		rlEmpty = (RelativeLayout) findViewById(R.id.rl_empty);
		btnGoShopping = (Button) findViewById(R.id.btn_go_shopping);
		
		TextView tvEmpty = (TextView) findViewById(R.id.tv_empty_go_shopping);
		ViewUtils.setMarginTop(tvEmpty, 24);
		tvEmpty.setTextSize(DisplayUtil.textGetSizeSp(this, 48));
		btnGoShopping.setTextSize(DisplayUtil.textGetSizeSp(this, 33));
		ViewUtils.setSize(btnGoShopping, 321, 125);
		ViewUtils.setHeight(findViewById(R.id.rl_bottom), 176);
		
		btnGoShopping.setOnClickListener(this);
	}
	
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()) {
		case R.id.btn_go_shopping:
			finish();
			break;
		}
		super.onClick(v);
	}
	

}
