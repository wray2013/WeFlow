package com.etoc.weflow.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.etoc.weflow.R;
import com.etoc.weflow.net.GsonResponseObject.RechargePhoneResp;
import com.etoc.weflow.utils.DisplayUtil;
import com.etoc.weflow.utils.ViewUtils;

public class RechargeAdapter extends BaseAdapter {

	List<RechargePhoneResp> itemList = null;
	Context context;
	private LayoutInflater inflater;
	private int curselected = 0;
	
	public void setSelect(int pos) {
		curselected = pos;
	}
	
	class RechargeViewHolder {
		TextView tvMoney;
		ImageView ivSelected;
	}
	
	public RechargeAdapter(Context context,List<RechargePhoneResp> list) {
		// TODO Auto-generated constructor stub
		this.context = context;
		inflater = LayoutInflater.from(context);
		this.itemList = list;
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return itemList.size();
	}

	@Override
	public RechargePhoneResp getItem(int position) {
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
		
		RechargePhoneResp item = itemList.get(position);
		holder.tvMoney.setText(item.money + "元");
		
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
