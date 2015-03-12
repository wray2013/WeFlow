package com.etoc.weflow.adapter;

import java.util.ArrayList;
import java.util.List;

import com.etoc.weflow.R;
import com.etoc.weflow.net.GsonResponseObject.*;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class MyBillAdapter extends BaseAdapter {

	private LayoutInflater inflater;
	
	private List<BillList> billList = new ArrayList<BillList>();
	
	
	public MyBillAdapter(Context context) {
		inflater = LayoutInflater.from(context);
	}
	
	public void setData(List<BillList> data) {
		// TODO Auto-generated method stub
		billList.clear();
		billList.addAll(data);
		notifyDataSetChanged();
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return billList.size();
	}

	@Override
	public BillList getItem(int position) {
		// TODO Auto-generated method stub
		return billList.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewHolder holder = null;
		Object object = getItem(position);
		if(object instanceof BillList) {
			BillList mybill = (BillList) object;
			if (convertView == null) {
				convertView = inflater.inflate(R.layout.fragment_bill_list_item, null);
				holder = new ViewHolder();
				holder.ivTypeIcon = (ImageView) convertView.findViewById(R.id.iv_type_icon);
				holder.tvTitle = (TextView) convertView.findViewById(R.id.tv_title);
				holder.tvCoins = (TextView) convertView.findViewById(R.id.tv_coins);
				holder.tvDate  = (TextView) convertView.findViewById(R.id.tv_date);
				initViews(holder);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			bindBill(holder, mybill);
		}
		return convertView;
	}
	
	private void initViews(ViewHolder holder) {
		// TODO Auto-generated method stub
	}
	
	private void bindBill(ViewHolder holder, BillList mybill) {
		// TODO Auto-generated method stub
		holder.ivTypeIcon.setBackgroundResource(R.drawable.ic_launcher);
		holder.tvTitle.setText(mybill.title);
		holder.tvCoins.setText(mybill.flowcoins);
		holder.tvDate.setText(mybill.time);
	}

	public class ViewHolder {
		ImageView ivTypeIcon;
		TextView tvTitle, tvCoins, tvDate;
	}

}
