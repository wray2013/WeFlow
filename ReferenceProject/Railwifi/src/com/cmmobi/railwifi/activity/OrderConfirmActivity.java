package com.cmmobi.railwifi.activity;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cmmobi.common.tools.Info;
import com.cmmobi.railwifi.R;
import com.cmmobi.railwifi.adapter.GoodAdapter.OrderGoodWrap;
import com.cmmobi.railwifi.adapter.OrderConfirmAdapter;
import com.cmmobi.railwifi.dao.DaoMaster;
import com.cmmobi.railwifi.dao.DaoMaster.DevOpenHelper;
import com.cmmobi.railwifi.dao.DaoSession;
import com.cmmobi.railwifi.dao.HistoryOrderForm;
import com.cmmobi.railwifi.dao.HistoryOrderFormDao;
import com.cmmobi.railwifi.dialog.PromptDialog;
import com.cmmobi.railwifi.network.GsonRequestObject.ReqGoodListElem;
import com.cmmobi.railwifi.network.GsonRequestObject.ReqGoodOrder;
import com.cmmobi.railwifi.network.GsonRequestObject;
import com.cmmobi.railwifi.network.GsonResponseObject;
import com.cmmobi.railwifi.network.Requester;
import com.cmmobi.railwifi.utils.DisplayUtil;
import com.cmmobi.railwifi.utils.MyTextWatcher;
import com.cmmobi.railwifi.utils.OrderFormItem;
import com.cmmobi.railwifi.utils.StringUtils;
import com.cmmobi.railwifi.utils.ViewUtils;
import com.google.gson.Gson;

public class OrderConfirmActivity extends TitleRootActivity {

	private final String TAG = "OrderConfirmActivity";
	private EditText etName;
	private EditText etCellPhone;
	private EditText etRailNum;
	private EditText etSiteNum;
	private OrderConfirmAdapter orderConfirmAdapter = null;
	private ListView orderListView;
	private TextView tvTotalPrice;
	private RelativeLayout rlSite;
	private EditText etSiteCount;
	private ImageView ivAddNum;
	private ImageView ivMinusNum;
	
	private DaoMaster daoMaster;
	private DaoSession daoSession;
	private SQLiteDatabase db;
	private HistoryOrderFormDao historyOrderFormDao;
	private ImageView ivPhoneError = null;
	private ImageView ivSiteError = null;
	private ImageView ivNameError = null;
	private ImageView ivRailError = null;
	private RadioGroup rg = null;
	private final String KEY_NAME = "name";
	private final String KEY_CELLPHONE = "cellphone";
	private final String KEY_RAIL_NUM = "railnum";
	private final String KEY_SITE_NUM = "sitenum";
	private boolean isSubmit = false;
	
	@Override
	public int subContentViewId() {
		// TODO Auto-generated method stub
		return R.layout.activity_order_confirm;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		orderConfirmAdapter = new OrderConfirmAdapter(this, OrderShoppingActivity.orderMap);
		initViews();
		
		setTitleText(getString(R.string.order_form_title));
		getRightButton().setBackgroundDrawable(null);
		getRightButton().setText("确认>>");
		getRightButton().setTextSize(DisplayUtil.textGetSizeSp(this, 33));
		
	}
	
	
	private void initViews() {
		orderListView = (ListView) findViewById(R.id.list_order_form);
		
		View headView = getLayoutInflater().inflate(R.layout.header_order_confirm, null);
		orderListView.addHeaderView(headView);
		
		etName = (EditText) findViewById(R.id.et_name);
		etCellPhone = (EditText) findViewById(R.id.et_celphome);
		ivPhoneError = (ImageView) findViewById(R.id.iv_phone_error);
		ivSiteError = (ImageView) findViewById(R.id.iv_site_error);
		ivNameError = (ImageView) findViewById(R.id.iv_name_error);
		ivRailError = (ImageView) findViewById(R.id.iv_rail_error);
		etRailNum = (EditText) findViewById(R.id.et_rail_num);
		etSiteNum = (EditText) findViewById(R.id.et_site_num);
		tvTotalPrice = (TextView) findViewById(R.id.tv_total_price);
		
		TextView tvName = (TextView) findViewById(R.id.tv_name);
		TextView tvCelphone = (TextView) findViewById(R.id.tv_celphone);
		final TextView tvRailNum = (TextView) findViewById(R.id.tv_rail_num);
		final TextView tvSiteNum = (TextView) findViewById(R.id.tv_site_num);
		tvName.setTextSize(DisplayUtil.textGetSizeSp(this, 36));
		tvCelphone.setTextSize(DisplayUtil.textGetSizeSp(this, 36));
		tvRailNum.setTextSize(DisplayUtil.textGetSizeSp(this, 36));
		tvSiteNum.setTextSize(DisplayUtil.textGetSizeSp(this, 36));
		
		rlSite = (RelativeLayout) findViewById(R.id.rl_site_num);
		etSiteCount = (EditText) findViewById(R.id.et_site_count);
		ivAddNum = (ImageView) findViewById(R.id.iv_site_num_plus);
		ivMinusNum = (ImageView) findViewById(R.id.iv_site_num_minus);
		
		ivAddNum.setOnClickListener(this);
		ivMinusNum.setOnClickListener(this);
		final View lineView = (View) findViewById(R.id.view_site_line);
		
		rg = (RadioGroup) findViewById(R.id.rg_eat_position);
		ViewUtils.setMarginLeft(rg, 22);
		ViewUtils.setMarginTop(rg, 30);
		rg.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				// TODO Auto-generated method stub
				switch(checkedId) {
				case R.id.rb_car:
					tvRailNum.setVisibility(View.GONE);
					etRailNum.setVisibility(View.GONE);
					tvSiteNum.setVisibility(View.GONE);
					etSiteNum.setVisibility(View.GONE);
					lineView.setVisibility(View.VISIBLE);
					rlSite.setVisibility(View.VISIBLE);
					break;
				case R.id.rb_site:
					tvRailNum.setVisibility(View.VISIBLE);
					etRailNum.setVisibility(View.VISIBLE);
					tvSiteNum.setVisibility(View.VISIBLE);
					etSiteNum.setVisibility(View.VISIBLE);
					lineView.setVisibility(View.GONE);
					rlSite.setVisibility(View.GONE);
					
					break;
				}
			}
		});
		
//		((TextView) findViewById(R.id.tv_name)).setTextSize(DisplayUtil.textGetSizeSp(this, 30));
//		((TextView) findViewById(R.id.tv_celphone)).setTextSize(DisplayUtil.textGetSizeSp(this, 30));
//		((TextView) findViewById(R.id.tv_rail_num)).setTextSize(DisplayUtil.textGetSizeSp(this, 30));
//		((TextView) findViewById(R.id.tv_site_num)).setTextSize(DisplayUtil.textGetSizeSp(this, 30));
//		ViewUtils.setMarginTop(findViewById(R.id.tv_name), 14);	
//		ViewUtils.setSize(etName, 518, 66);
//		ViewUtils.setSize(etCellPhone, 518, 66);
//		ViewUtils.setSize(etRailNum, 236, 66);
//		ViewUtils.setSize(etSiteNum, 248, 66);
//		ViewUtils.setMarginLeft(etSiteNum, 24);
		ViewUtils.setMarginRight(ivPhoneError, 12);
		ViewUtils.setMarginRight(ivSiteError, 12);
		ViewUtils.setMarginRight(ivNameError, 12);
		ViewUtils.setMarginRight(ivRailError, 12);
		ViewUtils.setMarginLeft(findViewById(R.id.rl_user_info), 12);
		ViewUtils.setMarginTop(findViewById(R.id.rl_list_view), 12);
		ViewUtils.setMarginTop(findViewById(R.id.tv_name), 24);
		ViewUtils.setSize(findViewById(R.id.tv_name), 150, 99);
		ViewUtils.setSize(findViewById(R.id.tv_celphone), 150, 99);
		ViewUtils.setSize(findViewById(R.id.tv_rail_num), 150, 99);
		ViewUtils.setSize(findViewById(R.id.tv_site_num), 150, 99);
		
		etName.setTextSize(DisplayUtil.textGetSizeSp(this, 36));
		etCellPhone.setTextSize(DisplayUtil.textGetSizeSp(this, 36));
		etRailNum.setTextSize(DisplayUtil.textGetSizeSp(this, 36));
		etSiteNum.setTextSize(DisplayUtil.textGetSizeSp(this, 36));
		
		ViewUtils.setSize(etName, 522, 99);
		ViewUtils.setSize(etCellPhone, 522, 99);
		ViewUtils.setSize(etRailNum, 522, 99);
		ViewUtils.setSize(etSiteNum, 522, 99);
		
		etName.addTextChangedListener(new NameTextWatcher(etName));
		etCellPhone.addTextChangedListener(new PhoneTextWatcher(etCellPhone));
		etRailNum.addTextChangedListener(new MyTextWatcher(etRailNum));
		etSiteNum.addTextChangedListener(new MyTextWatcher(etSiteNum));
		
		
		orderConfirmAdapter.setTotalPriceTextView(tvTotalPrice);
		orderListView.setAdapter(orderConfirmAdapter);
	}
	
	class PhoneTextWatcher extends MyTextWatcher {

		public PhoneTextWatcher(EditText et) {
			super(et);
			// TODO Auto-generated constructor stub
		}
		
		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
			// TODO Auto-generated method stub
			ivPhoneError.setVisibility(View.GONE);
			super.onTextChanged(s, start, before, count);
		}
	}
	
	class NameTextWatcher extends MyTextWatcher {

		public NameTextWatcher(EditText et) {
			super(et);
			// TODO Auto-generated constructor stub
		}
		
		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
			// TODO Auto-generated method stub
			ivNameError.setVisibility(View.GONE);
			super.onTextChanged(s, start, before, count);
		}
	}
	
	class SiteNumTextWatcher extends MyTextWatcher {

		public SiteNumTextWatcher(EditText et) {
			super(et);
			// TODO Auto-generated constructor stub
		}
		
		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
			// TODO Auto-generated method stub
			ivSiteError.setVisibility(View.GONE);
			String editable = et.getText().toString();   
	        String str = RailTravelOrderInfoActivity.stringIDFilter(editable.toString()); 
	        if(!editable.equals(str)){ 
	        	et.setText(str); 
	            //设置新的光标所在位置   
	        	et.setSelection(str.length()); 
	        } 
			super.onTextChanged(s, start, before, count);
		}
		
	}
	
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		if (orderConfirmAdapter != null) {
			orderConfirmAdapter.notifyDataSetChanged();
			orderConfirmAdapter.updateTotalPrice();
		}
		
		SharedPreferences sharedPreferences= getSharedPreferences("order_info", 
				Activity.MODE_PRIVATE); 
		
		String name = sharedPreferences.getString(KEY_NAME, "");
		String cellPhone = sharedPreferences.getString(KEY_CELLPHONE, "");
		String railNum = sharedPreferences.getString(KEY_RAIL_NUM, "");
		String siteNum = sharedPreferences.getString(KEY_SITE_NUM, "");
		
		etName.setText(name);
		etCellPhone.setText(cellPhone);
		etRailNum.setText(railNum);
		etSiteNum.setText(siteNum);
		
		super.onResume();
	}
	
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		if (!isSubmit) {
			SharedPreferences mySharedPreferences = getSharedPreferences("order_info", 
					Activity.MODE_PRIVATE); 
			SharedPreferences.Editor editor = mySharedPreferences.edit();
			if (!isEditEmpty(etName)) {
				editor.putString(KEY_NAME, etName.getText().toString());
			}
			
			if (!isEditEmpty(etCellPhone)) {
				editor.putString(KEY_CELLPHONE, etCellPhone.getText().toString());
			}
			
			if (!isEditEmpty(etRailNum)) {
				editor.putString(KEY_RAIL_NUM, etRailNum.getText().toString());
			}
			
			if (!isEditEmpty(etSiteNum)) {
				editor.putString(KEY_SITE_NUM, etSiteNum.getText().toString());
			}
			editor.commit();
		}
		
		super.onPause();
	}
	
	
	public boolean isEditEmpty(EditText et) {
		if (et != null) {
			return StringUtils.isEmpty(et.getText().toString());
		}
		
		return true;
	}

	@Override
	public boolean handleMessage(Message msg) {
		// TODO Auto-generated method stub
		switch (msg.what) {
		case Requester.RESPONSE_TYPE_GOOD_ORDER:
			/*if (msg.obj != null) {
				GsonResponseObject.GoodOrderResp resp = (GsonResponseObject.GoodOrderResp) msg.obj;
				if ("0".equals(resp.status)) {
					List<OrderFormItem> formItemList = new ArrayList<OrderFormItem>();
					
					double totalPrice = 0;
					if (OrderShoppingActivity.orderMap != null) {
						
						Iterator<Map.Entry<String, OrderGoodWrap>> it = OrderShoppingActivity.orderMap.entrySet().iterator();  
						while (it.hasNext()) {  
						    Map.Entry<String, OrderGoodWrap> entry = it.next();
						    OrderGoodWrap wrap = entry.getValue();
						    totalPrice += wrap.num * Double.parseDouble(wrap.goodItem.price);
						    OrderFormItem formItem = new OrderFormItem();
						    formItem.id = wrap.goodItem.object_id;
						    formItem.name = wrap.goodItem.name;
						    formItem.price = wrap.goodItem.price;
						    formItem.orderNum = "" + wrap.num;
						    formItem.totalPrice = "" + wrap.num * Double.parseDouble(wrap.goodItem.price);
						    formItemList.add(formItem);
						}
					}
					String contentStr = null;
					if (formItemList.size() > 0) {
						contentStr = new Gson().toJson(formItemList);
					}
					
					if (contentStr != null) {
						HistoryOrderForm historyOrderForm = new HistoryOrderForm(null, 
								Info.getDevId(OrderConfirmActivity.this), 
								Info.getDevId(OrderConfirmActivity.this),
								etRailNum.getText().toString(), 
								etSiteNum.getText().toString(), 
								etName.getText().toString(), 
								etCellPhone.getText().toString(), 
								"" + System.currentTimeMillis(), 
								"" + totalPrice, 
								contentStr);
						DevOpenHelper helper = new DaoMaster.DevOpenHelper(OrderConfirmActivity.this, "railwifidb", null);
						db = helper.getWritableDatabase();
						daoMaster = new DaoMaster(db);
						daoSession = daoMaster.newSession();
						historyOrderFormDao = daoSession.getHistoryOrderFormDao();
						historyOrderFormDao.insert(historyOrderForm);
						db.close();
					}
					OrderShoppingActivity.orderMap.clear();
					
					PromptDialog.Dialog(this, false, false,"预定成功", "您所订购的物品稍后给您送到!", new OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							finish();
						}
					});
				} else {
					PromptDialog.Dialog(this, "预定失败", "当前网络状态不佳", "稍后再试");
				}
			} else {
				PromptDialog.Dialog(this, "预定失败", "当前网络状态不佳", "稍后再试");
			}*/
			break;
		}
		return false;
	}
	
	private static long lastClickTime;

	public static boolean isFastDoubleClick()
	{
		long time = System.currentTimeMillis();
		long timeD = time - lastClickTime;
		if (0 < timeD && timeD < 800)
		{
			return true;
		}
		lastClickTime = time;
		return false;
	}
	
	private void addTicket(EditText edit) {
		if (TextUtils.isEmpty(edit.getText().toString())) {
			edit.setText("1");
		} else {
			int num = Integer.parseInt(edit.getText().toString());
			edit.setText("" + (num + 1));
		}
		edit.clearFocus();
	}
	
	private void minusTicket(EditText edit) {
		if (TextUtils.isEmpty(edit.getText().toString())) {
			return;
		} else {
			int num = Integer.parseInt(edit.getText().toString());
			if (num > 1) {
				edit.setText("" + (num - 1));
			} else {
				edit.setText("");
			}
		}
		edit.clearFocus();
	}
	
	private int getSiteCount() {
		int num = 0;
		if (!TextUtils.isEmpty(etSiteCount.getText().toString())) {
			num = Integer.parseInt(etSiteCount.getText().toString());
		}
		return num;
	}
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		final InputMethodManager imm = (InputMethodManager) this
				  .getSystemService(Context.INPUT_METHOD_SERVICE);
		switch(v.getId()) {
		case R.id.iv_site_num_plus:
			addTicket(etSiteCount);
			break;
		case R.id.iv_site_num_minus:
			minusTicket(etSiteCount);
			break;
		case R.id.btn_title_right:
			if (isFastDoubleClick()) {
				return;
			}
			if (TextUtils.isEmpty(etName.getText())) {
				Log.d(TAG,"Name is empty");
				etName.requestFocus();
				etName.setHintTextColor(0xffc60606);
				imm.showSoftInput(etName, 0);
				return;
			}else if(!PromptDialog.checkName(etName.getText().toString().trim())){
				etName.requestFocus();
				imm.showSoftInput(etName, 0);
				ivNameError.setVisibility(View.VISIBLE);
				return;
			}
			if (TextUtils.isEmpty(etCellPhone.getText())) {
				Log.d(TAG,"cellPhone is empty");
				etCellPhone.requestFocus();
				etCellPhone.setHintTextColor(0xffc60606);
				imm.showSoftInput(etCellPhone, 0);
				return;
			} else if (!PromptDialog.checkPhoneNum(etCellPhone.getText().toString())) {
				etCellPhone.requestFocus();
				ivPhoneError.setVisibility(View.VISIBLE);
				imm.showSoftInput(etCellPhone, 0);
				return;
			}
			
			
			String eatPosition = "0";
//			if (rg.getCheckedRadioButtonId() == R.id.rb_site) {
				eatPosition = "0";
				if (TextUtils.isEmpty(etRailNum.getText())) {
					Log.d(TAG,"railway num is empty");
					etRailNum.requestFocus();
					etRailNum.setHintTextColor(0xffc60606);
					imm.showSoftInput(etRailNum, 0);
					return;
				} else if(!PromptDialog.checkCarNum(etRailNum.getText().toString())){
					etRailNum.requestFocus();
					ivRailError.setVisibility(View.VISIBLE);
					imm.showSoftInput(etRailNum, 0);
				}
				
				if (TextUtils.isEmpty(etSiteNum.getText())) {
					Log.d(TAG,"site num is null");
					etSiteNum.requestFocus();
					etSiteNum.setHintTextColor(0xffc60606);
					imm.showSoftInput(etSiteNum, 0);
					return;
				} else if (!PromptDialog.checkSeatNum(etSiteNum.getText().toString())) {
					etSiteNum.requestFocus();
					ivSiteError.setVisibility(View.VISIBLE);
					imm.showSoftInput(etSiteNum, 0);
					return;
				}
			/*} else {
				eatPosition = "1";
				if (getSiteCount() == 0) {
					PromptDialog.Dialog(this, false, "温馨提示", "请至少添加一个餐位", new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							handler.postDelayed(new Runnable() {
								
								@Override
								public void run() {
									// TODO Auto-generated method stub
									etSiteCount.requestFocus();
									etSiteCount.setHintTextColor(0xffc60606);
									imm.showSoftInput(etSiteCount, 0);
								}
							}, 100);
						}
					});
					return;
				}
			}*/
			String orderNum = Info.getDevId(OrderConfirmActivity.this) + System.currentTimeMillis();
			ArrayList<ReqGoodListElem> goodList = new ArrayList<ReqGoodListElem>();
			if (OrderShoppingActivity.orderMap != null) {
				Iterator<Map.Entry<String, OrderGoodWrap>> it = OrderShoppingActivity.orderMap.entrySet().iterator();  
				while (it.hasNext()) {  
				    Map.Entry<String, OrderGoodWrap> entry = it.next();
				    OrderGoodWrap wrap = entry.getValue();
				    ReqGoodListElem item = new ReqGoodListElem();
				    item.object_id = wrap.goodItem.object_id;
				    item.count = "" + wrap.num;
				    item.type_id = wrap.goodItem.type_id;
				    goodList.add(item);
				}
			}
			
			String trainNum = MainActivity.train_num;
			GsonRequestObject.ReqGoodOrder request = new GsonRequestObject.ReqGoodOrder();
			request.contacts = etName.getText().toString();
			request.contacts_telephone = etCellPhone.getText().toString();
			request.customer_seat = etSiteNum.getText().toString();
			request.train_num = trainNum;
			request.customer_car = etRailNum.getText().toString();
			request.order_code = orderNum;
			request.list = goodList.toArray(new ReqGoodListElem[0]);
			request.order_type = eatPosition;
			request.people_num = etSiteCount.getText().toString();
			
			
			List<OrderFormItem> formItemList = new ArrayList<OrderFormItem>();
			
			double totalPrice = 0;
			if (OrderShoppingActivity.orderMap != null) {
				
				Iterator<Map.Entry<String, OrderGoodWrap>> it = OrderShoppingActivity.orderMap.entrySet().iterator();  
				while (it.hasNext()) {  
				    Map.Entry<String, OrderGoodWrap> entry = it.next();
				    OrderGoodWrap wrap = entry.getValue();
				    totalPrice += wrap.num * Double.parseDouble(wrap.goodItem.price);
				    OrderFormItem formItem = new OrderFormItem();
				    formItem.id = wrap.goodItem.object_id;
				    formItem.name = wrap.goodItem.name;
				    formItem.price = wrap.goodItem.price;
				    formItem.orderNum = "" + wrap.num;
				    formItem.typeId = wrap.goodItem.type_id;
				    formItem.totalPrice = "" + wrap.num * Double.parseDouble(wrap.goodItem.price);
				    formItemList.add(formItem);
				}
			}
			String contentStr = null;
			if (formItemList.size() > 0) {
				contentStr = new Gson().toJson(formItemList);
			}
			
			if (contentStr != null) {
				HistoryOrderForm historyOrderForm = new HistoryOrderForm(null, 
						Info.getDevId(OrderConfirmActivity.this), 
						Info.getDevId(OrderConfirmActivity.this),
						etRailNum.getText().toString(), 
						etSiteNum.getText().toString(), 
						etName.getText().toString(), 
						etCellPhone.getText().toString(), 
						"" + System.currentTimeMillis(), 
						"" + totalPrice,
						trainNum,
						orderNum,
						"2",
						contentStr,
						eatPosition,
						etSiteCount.getText().toString());
				DevOpenHelper helper = new DaoMaster.DevOpenHelper(OrderConfirmActivity.this, "railwifidb", null);
				db = helper.getWritableDatabase();
				daoMaster = new DaoMaster(db);
				daoSession = daoMaster.newSession();
				historyOrderFormDao = daoSession.getHistoryOrderFormDao();
				historyOrderFormDao.insert(historyOrderForm);
				db.close();
			}
			OrderShoppingActivity.orderMap.clear();
			
			/*Requester.requestGoodOrder(handler, 
					etName.getText().toString(), 
					etCellPhone.getText().toString(), 
					etRailNum.getText().toString(), 
					etSiteNum.getText().toString(), 
					goodList.toArray(new ReqGoodListElem[0]),
					MainActivity.train_num,orderNum,eatPosition,etSiteCount.getText().toString());*/
			
			imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
			Intent historyIntent = new Intent(this, HistoryOrderFormActivity.class);
			String requestStr = new Gson().toJson(request);
			historyIntent.putExtra("request", requestStr);
			startActivity(historyIntent);
			
			SharedPreferences mySharedPreferences = getSharedPreferences("order_info", 
					Activity.MODE_PRIVATE); 
			SharedPreferences.Editor editor = mySharedPreferences.edit();
			editor.remove(KEY_NAME);
			editor.remove(KEY_CELLPHONE);
			editor.remove(KEY_RAIL_NUM);
			editor.remove(KEY_SITE_NUM);
			editor.commit();
			isSubmit = true;
			finish();
			break;
		}
		super.onClick(v);
	}
}
