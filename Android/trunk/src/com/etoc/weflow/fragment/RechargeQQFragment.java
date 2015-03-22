package com.etoc.weflow.fragment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.support.v4.app.Fragment;
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
import android.widget.TextView;

import com.etoc.weflow.R;
import com.etoc.weflow.WeFlowApplication;
import com.etoc.weflow.activity.login.LoginActivity;
import com.etoc.weflow.dao.DaoMaster;
import com.etoc.weflow.dao.DaoMaster.DevOpenHelper;
import com.etoc.weflow.dao.AccountInfo;
import com.etoc.weflow.dao.DaoSession;
import com.etoc.weflow.dao.FrequentQQ;
import com.etoc.weflow.dao.FrequentQQDao;
import com.etoc.weflow.dao.FrequentQQDao.Properties;
import com.etoc.weflow.event.RechargeEvent;
import com.etoc.weflow.net.GsonResponseObject;
import com.etoc.weflow.net.GsonResponseObject.QChargeListResp;
import com.etoc.weflow.net.GsonResponseObject.QRechargeProduct;
import com.etoc.weflow.net.GsonResponseObject.RechargePhoneResp;
import com.etoc.weflow.net.GsonResponseObject.RechargeQQResp;
import com.etoc.weflow.net.Requester;
import com.etoc.weflow.utils.DisplayUtil;
import com.etoc.weflow.utils.ViewUtils;

import de.greenrobot.dao.query.QueryBuilder;
import de.greenrobot.event.EventBus;

public class RechargeQQFragment extends Fragment implements OnClickListener, Callback{

	private View mView;
	GridView gvQQMenu = null;
	RechargeQQAdapter adapter = null;
	TextView tvCostCoins = null;
	List<QRechargeProduct> itemList = new ArrayList<GsonResponseObject.QRechargeProduct>();
	TextView tvCommit = null;
	private FrequentQQDao qqDao;
	private EditText etQQ;
	SQLiteDatabase db;
	private Handler handler = null;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		Log.d("=AAA=","qq onCreate");
		super.onCreate(savedInstanceState);
		handler = new Handler(this);
		EventBus.getDefault().register(this);
		DevOpenHelper helper = new DaoMaster.DevOpenHelper(WeFlowApplication.getAppInstance(), "weflowdb", null);
		db = helper.getWritableDatabase();
		DaoMaster daoMaster = new DaoMaster(db);
		DaoSession daoSession = daoMaster.newSession();
        qqDao = daoSession.getFrequentQQDao();
        
        if (itemList.size() == 0) {
			Requester.getQChargeList(true, handler);
		}
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
		if (event == RechargeEvent.RECHARGE_QQ) {
			Log.d("=AAA=","RechargeQQFragment onEvent size = " + itemList.size());
			if (itemList.size() == 0) {
				Requester.getQChargeList(true, handler);
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
		View v = inflater.inflate(R.layout.fragment_recharge_qq, null);
		initView(v);
		mView = v;
		return v;
	}
	
	private void initView(View view) {
		gvQQMenu = (GridView) view.findViewById(R.id.gv_menu);
		
		/*int [] moneys = {1,5,10,20,50,100};
		
		if (itemList.size() == 0) {
			for (int i = 0;i < 6;i++) {
				RechargePhoneResp resp = new RechargePhoneResp();
				resp.chargesid = "" + i;
				resp.money = moneys[i] + "Q币";
				resp.cost = moneys[i] * 100 + "";
				itemList.add(resp);
			}
		}*/
		
		adapter = new RechargeQQAdapter(getActivity(), itemList);
		gvQQMenu.setAdapter(adapter);
		
		gvQQMenu.setSelector(new ColorDrawable(Color.TRANSPARENT));
		
		gvQQMenu.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id ) {
				// TODO Auto-generated method stub
				adapter.setSelect(position);
				adapter.notifyDataSetChanged();
				tvCostCoins.setText(adapter.getSelectCost());
			}
		});
		
		tvCostCoins = (TextView) view.findViewById(R.id.tv_cost_coins);
		ViewUtils.setMarginRight(tvCostCoins, 16);
//		tvCostCoins.setTextSize(DisplayUtil.textGetSizeSp(getActivity(), 32));
//		tvCostCoins.setText(adapter.getSelectCost());
		
		tvCommit = (TextView) view.findViewById(R.id.tv_btn_order);
		tvCommit.setOnClickListener(this);
		ViewUtils.setSize(tvCommit, 552, 96);
		ViewUtils.setMarginBottom(tvCommit, 68);
		tvCommit.setTextSize(DisplayUtil.textGetSizeSp(getActivity(), 32));
		
		etQQ = (EditText) view.findViewById(R.id.et_qq);
		ViewUtils.setMarginLeft(view.findViewById(R.id.rl_input_phone), 32);
		ViewUtils.setMarginRight(view.findViewById(R.id.rl_input_phone), 32);
		ViewUtils.setHeight(view.findViewById(R.id.rl_input_phone), 112);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()) {
		case R.id.tv_btn_order:
			QueryBuilder<FrequentQQ> build = qqDao.queryBuilder();
			build.where(Properties.Qq_num.eq(etQQ.getText().toString()));
			if (build.buildCount().count() ==0) {
				Log.d("=AAA=","buildCount = 0");
				qqDao.insert(new FrequentQQ(etQQ.getText().toString()));
			}
			
			AccountInfo accountInfo = WeFlowApplication.getAppInstance().getAccountInfo();
			if (accountInfo != null) {
				Requester.rechargeQQ(true, handler, accountInfo.getUserid(), etQQ.getText().toString(), adapter.getSelectId());
			} else {
				startActivity(new Intent(getActivity(), LoginActivity.class));
			}
			break;
		}
	}
	
	class RechargeQQAdapter extends BaseAdapter {

//		List<RechargeQQResp> itemList = null;
		List<QRechargeProduct> productList = null;
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
		
		class RechargeViewHolder {
			TextView tvMoney;
			ImageView ivSelected;
		}
		
		public RechargeQQAdapter(Context context,List<QRechargeProduct> list) {
			// TODO Auto-generated constructor stub
			this.context = context;
			inflater = LayoutInflater.from(context);
			
			this.productList = list;
			
//			this.itemList = list;
		}
		
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return itemList.size();
		}

		@Override
		public QRechargeProduct getItem(int position) {
			// TODO Auto-generated method stub
			return productList.get(position);
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
			
			QRechargeProduct item = productList.get(position);
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

	@Override
	public boolean handleMessage(Message msg) {
		// TODO Auto-generated method stub
		switch (msg.what) {
		case Requester.RESPONSE_TYPE_QRECHARGE_LIST:
			if (msg.obj != null) {
				QChargeListResp resp = (QChargeListResp) msg.obj;
				if(resp.status.equals("0000") || resp.status.equals("0")) {
					itemList.clear();
					if (resp.chargelist != null && resp.chargelist.length > 0) {
						Collections.addAll(itemList, resp.chargelist[0].products);
						
						adapter.notifyDataSetChanged();
						
						tvCostCoins.setTextSize(DisplayUtil.textGetSizeSp(getActivity(), 32));
						tvCostCoins.setText(adapter.getSelectCost());
					}
				}
			}
			break;
		}
		return false;
	}
}
