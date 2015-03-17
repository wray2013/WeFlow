package com.cmmobi.railwifi.activity;

import java.util.ArrayList;
import java.util.List;

import com.cmmobi.railwifi.R;
import com.cmmobi.railwifi.dao.DaoMaster;
import com.cmmobi.railwifi.dao.DaoSession;
import com.cmmobi.railwifi.dao.ReceiptAddr;
import com.cmmobi.railwifi.dao.ReceiptAddrDao;
import com.cmmobi.railwifi.dao.DaoMaster.DevOpenHelper;
import com.cmmobi.railwifi.utils.DisplayUtil;
import com.cmmobi.railwifi.utils.ViewUtils;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.net.wifi.ScanResult;
import android.os.Bundle;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

public class ReceiptAddressActivity extends TitleRootActivity {

	private DaoMaster daoMaster;
	private DaoSession daoSession;
	private ReceiptAddrDao receiptAddrDao;
	private SQLiteDatabase db;
	
	private Button btnNewReceipt;
	private ListView lvAddr;
	private ReceiptListAdapter adapter;
	private List<ReceiptAddr> addrList = null;
	
	int itemHeight = 0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setTitleText("收货地址");
		hideRightButton();
		
		DevOpenHelper helper = new DaoMaster.DevOpenHelper(this, "railwifidb", null);
        db = helper.getWritableDatabase();
        daoMaster = new DaoMaster(db);
        daoSession = daoMaster.newSession();
        
        receiptAddrDao = daoSession.getReceiptAddrDao();
        addrList = receiptAddrDao.loadAll();
		
		initView();
		
	}
	
	
	private void initView() {
		// TODO Auto-generated method stub
		btnNewReceipt = (Button) findViewById(R.id.btn_new_receipt);
		btnNewReceipt.setOnClickListener(this);
		ViewUtils.setHeight(findViewById(R.id.rl_new_receipt), 160);
		ViewUtils.setHeight(btnNewReceipt, 100);
		ViewUtils.setWidth(btnNewReceipt, 480);
		ViewUtils.setMarginTop(btnNewReceipt, 32);
		ViewUtils.setTextSize(btnNewReceipt, 32);
		
		lvAddr = (ListView) findViewById(R.id.lv_receipt_addr);
		lvAddr.setDividerHeight(DisplayUtil.getSize(this, 2));
		lvAddr.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {
				// TODO Auto-generated method stub
				Object object = parent.getAdapter().getItem(position);
				if(object instanceof ReceiptAddr) {
					ReceiptAddr item = (ReceiptAddr) object;
					String[] result = new String[]{"", "", "", "", ""};
					result[0] = item.getPlace();
					result[1] = item.getAddr();
					result[2] = item.getName();
					result[3] = item.getTel();
					result[4] = item.getCode();
					Intent i = new Intent(ReceiptAddressActivity.this, NewReceiptAddressActivity.class);
					i.putExtra("addressmodify", result);
					i.putExtra("addressid", position);
					startActivityForResult(i, UserInfoAcitivity.REQUEST_CODE_ADDRESS);
				}
			}
		});
		ViewUtils.setMarginLeft(lvAddr, 12);
		ViewUtils.setMarginRight(lvAddr, 12);
		
		makeFakeData();
		
		adapter = new ReceiptListAdapter(this);
		adapter.setData(addrList);
		
		lvAddr.setAdapter(adapter);
		setListViewHeightBasedOnChildren(lvAddr);
	}
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()) {
		case R.id.btn_new_receipt:
			Intent i = new Intent(this, NewReceiptAddressActivity.class);
			startActivityForResult(i, UserInfoAcitivity.REQUEST_CODE_ADDRESS);
			break;
		}
		super.onClick(v);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(resultCode != RESULT_OK) return;
		if(UserInfoAcitivity.REQUEST_CODE_ADDRESS == requestCode && data !=null && data.getStringArrayExtra("receipt")!=null) {
			String[] result = data.getExtras().getStringArray("receipt");
			int pos = data.getIntExtra("position", -1);
			ReceiptAddr addr = null;
			if(pos >= 0 && adapter != null) { //修改原有信息
				addr = adapter.getItem(pos);
				addr.setPlace(result[0]);
				addr.setAddr(result[1]);
				addr.setName(result[2]);
				addr.setTel(result[3]);
				addr.setCode(result[4]);
				receiptAddrDao.update(addr);
			} else {
				addr = new ReceiptAddr();
				addr.setPlace(result[0]);
				addr.setAddr(result[1]);
				addr.setName(result[2]);
				addr.setTel(result[3]);
				addr.setCode(result[4]);
				addrList.add(addr);
				receiptAddrDao.insertOrReplace(addr);
			}

			if(adapter != null){
//				adapter.notifyDataSetChanged();
				adapter.setData(addrList);
				setListViewHeightBasedOnChildren(lvAddr);
			}
		}
		
		super.onActivityResult(requestCode, resultCode, data);
	}

	private void makeFakeData() {
		
		/*addrList.clear();
		ReceiptAddr addr1 = new ReceiptAddr();
		addr1.name = "汪家明";
		addr1.tel  = "18544444444";
		addr1.addr = "广东省/广州市/荔湾区";
		addr1.detail = "广州大道中东方花苑大厦B座17楼03A室";
		addrList.add(addr1);
		
		ReceiptAddr addr2 = new ReceiptAddr();
		addr2.name = "张伟";
		addr2.tel  = "18555555555";
		addr2.addr = "北京/朝阳区";
		addr2.detail = "八里庄东里莱锦文化创意产业园CN11";
		addrList.add(addr2);
		
		ReceiptAddr addr3 = new ReceiptAddr();
		addr3.name = "尤甜";
		addr3.tel  = "18566666666";
		addr3.addr = "湖北省/武汉市/汉阳区";
		addr3.detail = "龟北路汉阳造文化创意产业园9#1层";
		addrList.add(addr3);*/
	}

	@Override
	public boolean handleMessage(Message msg) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int subContentViewId() {
		// TODO Auto-generated method stub
		return R.layout.activity_receipt_address;
	}

	/***
     * 动态设置listview的高度
     * 
     * @param listView
     */
    public void setListViewHeightBasedOnChildren(ListView listView) {
//    	if(!TAG_ON) return;
        BaseAdapter listAdapter = (BaseAdapter) listView.getAdapter();
        if (listAdapter == null || listAdapter.getCount() <= 0) {
            return;
        }
        
        if(itemHeight <= 0) {
        	/*View listItem = listAdapter.getView(0, null, listView);
        	listItem.measure(0, 0);
        	itemHeight = listItem.getMeasuredHeight();*/
        	itemHeight = DisplayUtil.getSize(this, 140);
        }
        
        int totalHeight = 0;
        /*for (int i = 0; i < listAdapter.getCount(); i++) {
        	View listItem = listAdapter.getView(i, null, listView);
        	listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }*/
        totalHeight = listAdapter.getCount() * itemHeight;
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight
                + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        // params.height += 5;// if without this statement,the listview will be
        // a
        // little short
        // listView.getDividerHeight()获取子项间分隔符占用的高度
        // params.height最后得到整个ListView完整显示需要的高度
        listView.setLayoutParams(params);
    }
    
	private class ReceiptListAdapter extends BaseAdapter {

		private LayoutInflater inflater;
		private List<ReceiptAddr> list = new ArrayList<ReceiptAddr>();
		
		public ReceiptListAdapter(Context ctx) {
			inflater = LayoutInflater.from(ctx);
		}
		
		public void setData(List<ReceiptAddr> l) {
			list.clear();
			list.addAll(l);
			notifyDataSetChanged();
		}
		
		@Override
		public int getCount() {
			return list.size();
		}

		@Override
		public ReceiptAddr getItem(int position) {
			return list.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder = null;
			Object object = getItem(position);
			if(object instanceof ReceiptAddr) {
				ReceiptAddr item = (ReceiptAddr) object;
				if (convertView == null) {
					convertView = inflater.inflate(R.layout.activity_receipt_address_item, null);
					holder = new ViewHolder();
					holder.tvHeight = (TextView) convertView.findViewById(R.id.tv_h);
					holder.tvName = (TextView) convertView.findViewById(R.id.tv_name);
					holder.tvTel  = (TextView) convertView.findViewById(R.id.tv_tel);
					holder.tvAddr   = (TextView) convertView.findViewById(R.id.tv_addr);
					
					ViewUtils.setTextSize(holder.tvName, 32);
					ViewUtils.setTextSize(holder.tvTel, 32);
					ViewUtils.setTextSize(holder.tvAddr, 28);
					
					ViewUtils.setMarginTop(holder.tvName, 15);
					ViewUtils.setMarginTop(holder.tvTel, 15);
					ViewUtils.setMarginTop(holder.tvAddr, 15);
					
					ViewUtils.setMarginLeft(holder.tvName, 8);
					ViewUtils.setMarginLeft(holder.tvAddr, 8);
					ViewUtils.setHeight(holder.tvHeight, 140);
					
					convertView.setTag(holder);
				} else {
					holder = (ViewHolder) convertView.getTag();
				}
				bindReceipt(holder, item);
			}
			return convertView;
		}
		
		private void bindReceipt(ViewHolder holder, ReceiptAddr item) {
			// TODO Auto-generated method stub
			holder.tvName.setText(item.getName());
			holder.tvTel.setText(item.getTel());
			String totaladdr = item.getPlace();
			if(item.getAddr() != null && !item.getAddr().equals("")) {
				totaladdr = item.getPlace() + " " + item.getAddr();
			}
			holder.tvAddr.setText(totaladdr);
		}

		public class ViewHolder {
			TextView tvName, tvTel, tvAddr, tvHeight;
		}
		
	}
	
}
