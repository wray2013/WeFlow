package com.etoc.weflow.fragment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.etoc.weflow.R;
import com.etoc.weflow.WeFlowApplication;
import com.etoc.weflow.activity.login.LoginActivity;
import com.etoc.weflow.adapter.MoreAdapter;
import com.etoc.weflow.dao.AccountInfo;
import com.etoc.weflow.dao.DaoMaster;
import com.etoc.weflow.dao.DaoMaster.DevOpenHelper;
import com.etoc.weflow.dao.DaoSession;
import com.etoc.weflow.dao.FrequentPhone;
import com.etoc.weflow.dao.FrequentPhoneDao;
import com.etoc.weflow.dao.FrequentPhoneDao.Properties;
import com.etoc.weflow.dialog.OrderDialog;
import com.etoc.weflow.dialog.PromptDialog;
import com.etoc.weflow.event.RechargeEvent;
import com.etoc.weflow.net.GsonResponseObject;
import com.etoc.weflow.net.GsonResponseObject.PhoneChargeListResp;
import com.etoc.weflow.net.GsonResponseObject.PhoneChargeResp;
import com.etoc.weflow.net.GsonResponseObject.RechargePhoneResp;
import com.etoc.weflow.net.GsonResponseObject.RechargeProduct;
import com.etoc.weflow.net.Requester;
import com.etoc.weflow.utils.ConStant;
import com.etoc.weflow.utils.DisplayUtil;
import com.etoc.weflow.utils.NumberUtils;
import com.etoc.weflow.utils.StringUtils;
import com.etoc.weflow.utils.ViewUtils;

import de.greenrobot.dao.query.QueryBuilder;
import de.greenrobot.event.EventBus;

public class RechargePhoneFragment extends Fragment implements OnClickListener, Callback {
	private View mView;
	GridView gvPhoneMenu = null;
	RechargePhoneAdapter adapter = null;
	TextView tvCostCoins = null;
	TextView tvCommit = null;
	EditText etPhone = null;
	TextView tvTypes = null;
	RelativeLayout rlTypes = null;
	List<RechargePhoneResp> itemList = new ArrayList<GsonResponseObject.RechargePhoneResp>();
	List<RechargeProduct> productList = new ArrayList<RechargeProduct>();
	List<String> typeStrList = new ArrayList<String>();
	View typeView = null;
	ListView typeListView = null;
	PopupWindow typePopupWindow;
	
	MoreAdapter typeAdapter = null;
	private FrequentPhoneDao phoneDao;
	SQLiteDatabase db;
	private Handler handler;
	private ImageView ivContact = null;
	public static final int REQUEST_CONTACT_PICK = 0xddbb;
	public static final String TELECOM = "1";
	public static final String UNICOM = "2";
	public static final String MOBILE = "3";
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		EventBus.getDefault().register(this);
		
		DevOpenHelper helper = new DaoMaster.DevOpenHelper(WeFlowApplication.getAppInstance(), "weflowdb", null);
		db = helper.getWritableDatabase();
		DaoMaster daoMaster = new DaoMaster(db);
		DaoSession daoSession = daoMaster.newSession();
        phoneDao = daoSession.getFrequentPhoneDao();
        handler = new Handler(this);
	}
	
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		if (db != null) {
			db.close();
		}
		EventBus.getDefault().unregister(this);
		super.onDestroy();
	}
	
	public void onEvent(RechargeEvent event) {
		if (event == RechargeEvent.RECHARGE_PHONE) {
			if (itemList.size() == 0) {
				Requester.getPhoneChargeList(true, handler);
			}
		}
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		Log.d("=AAA=","this = " + this);
		if(mView != null){
		    ViewGroup parent = (ViewGroup) mView.getParent();  
		    if (parent != null) {  
		        parent.removeView(mView);  
		    }   
		    return mView;
		}
		
		super.onCreateView(inflater, container, savedInstanceState);
		View v = inflater.inflate(R.layout.fragment_recharge_phone, null);
		mView = v;
		initView(v);
		initPopupWindow();
		return v;
	}
	
	private void initPopupWindow() {
		typeAdapter = new MoreAdapter(getActivity(), typeStrList);
		
		typeView = getActivity().getLayoutInflater().inflate(R.layout.item_select_popup_window,
				null);
		typeListView = (ListView) typeView.findViewById(R.id.lv_selector);
		typeListView.setAdapter(typeAdapter);
		
		typePopupWindow = createPopupWindow(typeView);
		
		typeListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {
				// TODO Auto-generated method stub
				RechargePhoneResp resp = itemList.get(position);
				if (resp.products != null && resp.products.length > 0) {
					productList.clear();
					for (RechargeProduct item:resp.products) {
						productList.add(item);
					}
					typeAdapter.notifyDataSetChanged();
					typePopupWindow.dismiss();
				}
				tvTypes.setText(resp.typename);
				tvCostCoins.setText(NumberUtils.convert2IntStr(adapter.getSelectCost()) + "流量币");
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
	
	private void initView(View view) {
		gvPhoneMenu = (GridView) view.findViewById(R.id.gv_menu);
		
//		int [] moneys = {10,20,30,50,100,200};
		
		/*if (itemList.size() == 0) {
			for (int i = 0;i < 6;i++) {
				RechargePhoneResp resp = new RechargePhoneResp();
				resp.chargesid = "" + i;
				resp.money = moneys[i] + "元";
				resp.cost = moneys[i] * 100 + "";
				itemList.add(resp);
			}
		}*/
		
		adapter = new RechargePhoneAdapter(getActivity(), productList);
		gvPhoneMenu.setAdapter(adapter);
		
		gvPhoneMenu.setSelector(new ColorDrawable(Color.TRANSPARENT));
		
		gvPhoneMenu.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id ) {
				// TODO Auto-generated method stub
				adapter.setSelect(position);
				adapter.notifyDataSetChanged();
				tvCostCoins.setText(NumberUtils.convert2IntStr(adapter.getSelectCost()) + "流量币");
			}
		});
		
		tvCostCoins = (TextView) view.findViewById(R.id.tv_cost_coins);
		ViewUtils.setMarginTop(tvCostCoins, 24);
		ViewUtils.setMarginTop(view.findViewById(R.id.tv_cost_label), 32);
		ViewUtils.setTextSize(view.findViewById(R.id.tv_cost_label), 32);
		ViewUtils.setTextSize(tvCostCoins, 36);
//		tvCostCoins.setTextSize(DisplayUtil.textGetSizeSp(getActivity(), 32));
//		tvCostCoins.setText(adapter.getSelectCost());
		rlTypes = (RelativeLayout) view.findViewById(R.id.rl_types);
		tvTypes = (TextView) view.findViewById(R.id.tv_types);
		rlTypes.setOnClickListener(this);
		ViewUtils.setHeight(rlTypes, 112);
		ViewUtils.setTextSize(tvTypes, 32);
		ViewUtils.setTextSize(view.findViewById(R.id.tv_types_label), 32);
		ViewUtils.setMarginLeft(view.findViewById(R.id.tv_types_label), 24);
		ViewUtils.setMarginTop(rlTypes, 24);
		ViewUtils.setMarginTop(gvPhoneMenu, 24);
		
		ViewUtils.setSize(view.findViewById(R.id.view_arraw_coins), 72, 72);
		
		
		tvCommit = (TextView) view.findViewById(R.id.tv_btn_order);
		tvCommit.setOnClickListener(this);
		ViewUtils.setSize(tvCommit, 552, 96);
		ViewUtils.setMarginBottom(tvCommit, 48);
		tvCommit.setTextSize(DisplayUtil.textGetSizeSp(getActivity(), 32));
		
		ViewUtils.setMarginLeft(view.findViewById(R.id.rl_input_phone), 32);
		ViewUtils.setMarginRight(view.findViewById(R.id.rl_input_phone), 32);
		ViewUtils.setHeight(view.findViewById(R.id.rl_input_phone), 112);
		
		etPhone = (EditText) view.findViewById(R.id.et_phone);
		ivContact = (ImageView) view.findViewById(R.id.iv_contact_btn);
		ViewUtils.setSize(ivContact, 72, 72);
		ivContact.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.tv_btn_order:
			if (!PromptDialog.checkPhoneNum(etPhone.getText().toString())) {
				PromptDialog.Dialog(getActivity(), "温馨提示", "手机格式错误", "确定");
				return;
			}
				
			QueryBuilder<FrequentPhone> build = phoneDao.queryBuilder();
			build.where(Properties.Phone_num.eq(etPhone.getText().toString()));
			if (build.buildCount().count() ==0) {
				Log.d("=AAA=","buildCount = 0");
				phoneDao.insert(new FrequentPhone(etPhone.getText().toString()));
			}
			AccountInfo accountInfo = WeFlowApplication.getAppInstance().getAccountInfo();
			if (accountInfo != null && accountInfo.getUserid() != null) {
				Requester.rechargePhone(true, handler, accountInfo.getUserid(), etPhone.getText().toString(), adapter.getSelectId());
			} else {
				startActivity(new Intent(getActivity(), LoginActivity.class));
			}
			
			
			break;
		case R.id.iv_contact_btn:
			Intent intent = new Intent();
	        intent.setAction(Intent.ACTION_PICK);
	        intent.setData(ContactsContract.Contacts.CONTENT_URI);
	        startActivityForResult(intent, REQUEST_CONTACT_PICK);
			break;
		case R.id.rl_types:
			
			if (typePopupWindow == null) {
				return;
			}
			if (typePopupWindow.isShowing()) {
				typePopupWindow.dismiss();
			} else {
				typePopupWindow.showAsDropDown(rlTypes);
				int height = ViewUtils.getListHeight(typeListView, DisplayUtil.getSize(getActivity(), 96));
				Log.d("=AAA=","height = " + height);
				if (height > DisplayUtil.getScreenHeight(getActivity()) / 2) {
					ViewUtils.setHeightPixel(typeListView,DisplayUtil.getScreenHeight(getActivity()) / 2);
				} else {
					ViewUtils.setHeightPixel(typeListView,height);
				}
			}
			break;
		}
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		if(resultCode != Activity.RESULT_OK || data == null) return;
		if ((requestCode & 0xffff) == REQUEST_CONTACT_PICK) {
			Uri result = data.getData();
			String contactId = result.getLastPathSegment();
			String contactnumber = getPhoneContacts(contactId);
//			Toast.makeText(WeFlowApplication.getAppInstance(), "phone number = " + contactnumber, Toast.LENGTH_LONG).show();
			etPhone.setText(contactnumber);
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
	
	@SuppressWarnings("deprecation")
	private String getPhoneContacts(String contactId) {
		Cursor cursor = null;
		String number = "";
		try {
			Uri uri = Phone.CONTENT_URI;
			cursor = WeFlowApplication.getAppInstance().getContentResolver().query(uri, null, Phone.CONTACT_ID + "=" + contactId , null, null);
			if (cursor.moveToFirst()) {
				number = cursor.getString(cursor.getColumnIndex(Phone.NUMBER));
				number = number.replace(" ", "");
				// Toast.makeText(this, name, Toast.LENGTH_LONG).show();
			} else {
				Toast.makeText(WeFlowApplication.getAppInstance(), "No contact found.", Toast.LENGTH_LONG)
						.show();
			}
		} catch (Exception e) {
			number = "";
			e.printStackTrace();
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
		return number;
	}

	@Override
	public boolean handleMessage(Message msg) {
		// TODO Auto-generated method stub
		switch(msg.what) {
		case Requester.RESPONSE_TYPE_PHONE_CHARGE_LIST:
			if (msg.obj != null) {
				PhoneChargeListResp resp = (PhoneChargeListResp)msg.obj;
				if(resp.status.equals("0000") || resp.status.equals("0")) {
					itemList.clear();
					productList.clear();
					typeStrList.clear();
					if (resp.chargelist != null && resp.chargelist.length > 0) {
						Collections.addAll(itemList, resp.chargelist);
						
						for (RechargePhoneResp item:itemList) {
							typeStrList.add(item.typename);
							Log.d("=AAA=","item.typename = " + item.typename);
						}
						RechargePhoneResp item = itemList.get(0);
						tvTypes.setText(item.typename);
						if (item.products != null && item.products.length > 0) {
							Collections.addAll(productList, item.products);
						}
					}
					adapter.notifyDataSetChanged();
					if(isAdded()) {
						tvCostCoins.setTextSize(DisplayUtil.textGetSizeSp(getActivity(), 32));
					}
					tvCostCoins.setText(NumberUtils.convert2IntStr(adapter.getSelectCost())+ "流量币");
				}
				
			}
			break;
		case Requester.RESPONSE_TYPE_RECHARGE_PHONE:
			if (msg.obj != null) {
				PhoneChargeResp chargeResp = (PhoneChargeResp) msg.obj;
				if (Requester.isSuccessed(chargeResp.status)) {
//					PromptDialog.Alert("订购成功");
					WeFlowApplication.getAppInstance().setFlowCoins(chargeResp.flowcoins);
					OrderDialog.Dialog(getActivity(), "已成功充值" + adapter.getSelectMoney() + "元话费", true);
					/*if (!StringUtils.isEmpty(chargeResp.cardcode)) {
						OrderDialog.Dialog(getActivity(), "兑换码: " + chargeResp.cardcode + "\n请尽快使用");
//						PromptDialog.Dialog(getActivity(), "温馨提示", "订购成功，兑换码: " + chargeResp.cardcode + "\n请尽快使用", "确定");
					}*/
				} else if (Requester.isProcessed(chargeResp.status)){
//					PromptDialog.Alert("订购已处理");
					OrderDialog.Dialog(getActivity(), "充值已受理");
					WeFlowApplication.getAppInstance().setFlowCoins(chargeResp.flowcoins);
				} else if (Requester.isLowFlow(chargeResp.status)) {
					OrderDialog.Dialog(getActivity(), ConStant.LOW_FLOW, true);
//					PromptDialog.Alert(ConStant.LOW_FLOW);
				} else {
					OrderDialog.Dialog(getActivity(), ConStant.ORDER_FAIL, true);
//					PromptDialog.Alert(ConStant.ORDER_FAIL);
				}
			}
			break;
		}
		return false;
	}
	
	
	class RechargePhoneAdapter extends BaseAdapter {

		List<RechargeProduct> itemList = null;
		Context context;
		private LayoutInflater inflater;
		private int curselected = 0;
		
		public void setSelect(int pos) {
			curselected = pos;
		}
		
		public int getSelect() {
			return curselected;
		}
		
		public String getSelectCost() {
			return getItem(getSelect()).cost;
		}
		
		public String getSelectId() {
			return getItem(getSelect()).chargesid;
		}
		
		public String getSelectMoney() {
			return getItem(getSelect()).money;
		}
		
		class RechargeViewHolder {
			TextView tvMoney;
			ImageView ivSelected;
		}
		
		public RechargePhoneAdapter(Context context,List<RechargeProduct> list) {
			// TODO Auto-generated constructor stub
			this.context = context;
			inflater = LayoutInflater.from(context);
			this.itemList = list;
		}
		
		public void setData(List<RechargeProduct> list) {
			this.itemList = list;
		}
		
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return itemList.size();
		}

		@Override
		public RechargeProduct getItem(int position) {
			// TODO Auto-generated method stub
			return itemList.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			RechargeViewHolder holder = null;
			if (convertView == null) {
				holder = new RechargeViewHolder();
				convertView = inflater.inflate(R.layout.item_recharge_grid, null);
				holder.tvMoney = (TextView) convertView.findViewById(R.id.tv_recharge_num);
				holder.ivSelected = (ImageView) convertView.findViewById(R.id.iv_selected);
				
				ViewUtils.setMarginTop(holder.ivSelected, 8);
				ViewUtils.setMarginRight(holder.ivSelected, 8);
				
				holder.tvMoney.setTextSize(DisplayUtil.textGetSizeSp(context, 34));
				ViewUtils.setHeight(holder.tvMoney, 121);
				ViewUtils.setSize(holder.ivSelected, 54, 54);
				convertView.setTag(holder);
				
			} else {
				holder = (RechargeViewHolder) convertView.getTag();
			}
			
			RechargeProduct item = itemList.get(position);
			holder.tvMoney.setText(item.money);
			
			if (curselected == position) {
				holder.ivSelected.setVisibility(View.VISIBLE);
				holder.tvMoney.setTextColor(context.getResources().getColor(R.color.pagertab_color_green));
			} else {
				holder.ivSelected.setVisibility(View.GONE);
				holder.tvMoney.setTextColor(0xff000000);
			}
			
			return convertView;
		}
	}

}
