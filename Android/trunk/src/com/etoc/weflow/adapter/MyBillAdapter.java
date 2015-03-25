package com.etoc.weflow.adapter;

import java.util.ArrayList;
import java.util.List;

import com.etoc.weflow.R;
import com.etoc.weflow.net.GsonResponseObject.*;
import com.etoc.weflow.utils.DateUtils;
import com.etoc.weflow.utils.NumberUtils;
import com.etoc.weflow.utils.PTypeTransfer;
import com.etoc.weflow.utils.ViewUtils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * 账单/记录 通用适配器
 * @author Ray
 *
 */
public class MyBillAdapter extends BaseAdapter {

	private LayoutInflater inflater;
	private Context ctx;
	
	private List<BillList> billList = new ArrayList<BillList>();
	
	
	public MyBillAdapter(Context context) {
		inflater = LayoutInflater.from(context);
		ctx = context;
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
				holder.tvSubTitle = (TextView) convertView.findViewById(R.id.tv_subtitle);
				holder.tvTitle = (TextView) convertView.findViewById(R.id.tv_title);
				holder.tvContent = (TextView) convertView.findViewById(R.id.tv_content);
				holder.tvCoins = (TextView) convertView.findViewById(R.id.tv_coins);
				holder.tvDate  = (TextView) convertView.findViewById(R.id.tv_date);
				initViews(convertView);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			bindBill(holder, mybill);
		}
		return convertView;
	}
	
	private void initViews(View View) {
		// TODO Auto-generated method stub
		ViewUtils.setHeight(View.findViewById(R.id.view_height), 145);
		ViewUtils.setHeight(View.findViewById(R.id.tv_subtitle), 65);
		ViewUtils.setMarginBottom(View.findViewById(R.id.tv_date), 20);
		ViewUtils.setMarginTop(View.findViewById(R.id.tv_title), 20);
		ViewUtils.setMarginLeft(View.findViewById(R.id.rl_bill_right_title), 32);
		ViewUtils.setMarginLeft(View.findViewById(R.id.tv_subtitle), 32);
		ViewUtils.setMarginRight(View.findViewById(R.id.tv_coins), 32);
		ViewUtils.setMarginRight(View.findViewById(R.id.tv_date), 32);
		
		ViewUtils.setTextSize(View.findViewById(R.id.tv_title), 30);
		ViewUtils.setTextSize(View.findViewById(R.id.tv_content), 30);
		ViewUtils.setTextSize(View.findViewById(R.id.tv_coins), 30);
		ViewUtils.setTextSize(View.findViewById(R.id.tv_date), 20);
	}
	
	private void bindBill(ViewHolder holder, BillList mybill) {
		// TODO Auto-generated method stub
		holder.ivTypeIcon.setBackgroundResource(R.drawable.ic_launcher);
		holder.tvTitle.setText(PTypeTransfer.getPTypeName(mybill.type));
		
		int coins = 0;
		if(mybill.flowcoins != null) {
			try {
				coins = NumberUtils.Str2Int(mybill.flowcoins);
				if(coins >= 0) {
					holder.tvCoins.setText("+" + coins + "流量币");
					holder.tvCoins.setTextColor(ctx.getResources().getColor(R.color.pagertab_color_orange));
				} else {
					holder.tvCoins.setText(coins + "流量币");
					holder.tvCoins.setTextColor(ctx.getResources().getColor(R.color.pagertab_color_green));
				}
			} catch(Exception e) {
				e.printStackTrace();
			}
		} else {
			holder.tvCoins.setText("");
		}
		
		if(mybill.content != null && !mybill.content.equals("")) {
			holder.tvContent.setText(mybill.content);
			holder.tvContent.setVisibility(View.VISIBLE);
		} else if(mybill.type != null && !mybill.type.equals("")) {
			String content = PTypeTransfer.getPTypeName(mybill.type);
			String coinIncome = coins == 0 ? "" : (coins > 0 ? ("赚取" + Math.abs(coins) + "流量币") : ("花费" + Math.abs(coins) + "流量币"));
			holder.tvContent.setText(content + "\"" + mybill.title + "\"" + coinIncome);
			holder.tvContent.setVisibility(View.VISIBLE);
		} else {	
			holder.tvContent.setText("空");
			holder.tvContent.setVisibility(View.INVISIBLE);
		}
		
		String currentYearMonth = DateUtils.getYMStringFromMilli(System.currentTimeMillis() + "");
		String YM = DateUtils.getYMStringFromMilli(mybill.time);
		int index = billList.indexOf(mybill);
		if(index > 0) {
			BillList lastItem = billList.get(index - 1);
			String LastYM = DateUtils.getYMStringFromMilli(lastItem.time);
			if(LastYM != null && !LastYM.equals("") && LastYM.equals(YM)) {
				showSubTitle(holder, false);
			} else {
				showSubTitle(holder, true);
			}
		} else {
			showSubTitle(holder, true);
		}
		holder.tvDate.setText(DateUtils.getStringFromMilli(mybill.time, DateUtils.DATE_FORMAT_NORMAL_1));
		
		String subtitle = DateUtils.getStringFromMilli(mybill.time, "M月");
		if (currentYearMonth.equals(YM)) {
			subtitle = "本月";
		} else if (!DateUtils.getStringFromMilli(
				System.currentTimeMillis() + "", "yyyy").equals(
				DateUtils.getStringFromMilli(mybill.time, "yyyy"))) {
			subtitle = DateUtils.getStringFromMilli(mybill.time, "yyyy年M月");
		}
		holder.tvSubTitle.setText(subtitle);
	}
	
	private void showSubTitle(ViewHolder holder, boolean isShow) {
		// TODO Auto-generated method stub
		holder.tvSubTitle.setVisibility(isShow ? View.VISIBLE : View.GONE);
	}


	public class ViewHolder {
		ImageView ivTypeIcon;
		TextView tvSubTitle, tvTitle, tvContent, tvCoins, tvDate;
	}

}
