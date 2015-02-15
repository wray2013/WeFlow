package com.cmmobi.railwifi.adapter;

import java.util.ArrayList;
import java.util.List;

import android.app.ActionBar.LayoutParams;
import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cmmobi.railwifi.R;
import com.cmmobi.railwifi.dao.HistoryOrderForm;
import com.cmmobi.railwifi.network.GsonRequestObject.ReqGoodListElem;
import com.cmmobi.railwifi.network.Requester;
import com.cmmobi.railwifi.utils.DateUtils;
import com.cmmobi.railwifi.utils.DisplayUtil;
import com.cmmobi.railwifi.utils.OrderFormItem;
import com.cmmobi.railwifi.utils.ViewUtils;
import com.cmmobi.railwifi.view.HistoryOrderFormView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class HistoryOrderFormAdapter extends BaseAdapter{

	private final String TAG = "HistoryOrderFormAdapter";
	private List<HistoryOrderForm> historyOrderList = null;
	private LayoutInflater inflater;
	private Context context;
	private Handler handler;
	public HistoryOrderFormAdapter(Context context,List<HistoryOrderForm> historyOrderList,Handler handler) {
		// TODO Auto-generated constructor stub
		this.context = context;
		inflater = LayoutInflater.from(context);
		this.historyOrderList = historyOrderList;
		this.handler = handler;
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return historyOrderList.size();
	}

	@Override
	public HistoryOrderForm getItem(int position) {
		// TODO Auto-generated method stub
		return historyOrderList.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewHolder holder = null;
		if (convertView == null) {
			convertView = inflater.inflate(
					R.layout.item_history_order_form, null);
			holder = new ViewHolder();
			holder.historyOrderFormView = (HistoryOrderFormView) convertView.findViewById(R.id.view_history_order_form);
			holder.tvTime = (TextView) convertView.findViewById(R.id.tv_order_time);
			holder.tvName = (TextView) convertView.findViewById(R.id.tv_order_user);
			holder.tvCellPhone = (TextView) convertView.findViewById(R.id.tv_order_tel);
			holder.tvRailNum = (TextView) convertView.findViewById(R.id.tv_order_rail_num);
			holder.tvSiteNum = (TextView) convertView.findViewById(R.id.tv_order_site_num);
			holder.tvSiteCount = (TextView) convertView.findViewById(R.id.tv_site_count);
			holder.tvTotalPrice = (TextView) convertView.findViewById(R.id.tv_total_price);
			holder.ivState = (ImageView) convertView.findViewById(R.id.iv_status);
			holder.rlCar = (RelativeLayout) convertView.findViewById(R.id.rl_eat_at_car);
			holder.rlSite = (RelativeLayout) convertView.findViewById(R.id.rl_eat_at_site);
			
			ViewUtils.setMarginTop(convertView.findViewById(R.id.rl_order_user_info), 30);
			ViewUtils.setMarginLeft(convertView.findViewById(R.id.rl_order_user_info), 30);
			ViewUtils.setMarginTop(convertView.findViewById(R.id.tv_order_time_label), 24);
			ViewUtils.setMarginTop(convertView.findViewById(R.id.tv_order_tel_label), 24);
			ViewUtils.setMarginLeft(convertView.findViewById(R.id.tv_order_site_num_label), 141);
			ViewUtils.setMarginTop(convertView.findViewById(R.id.rl_eat_position), 24);
			
			ViewUtils.setMarginTop(holder.ivState, 12);
			ViewUtils.setMarginRight(holder.ivState, 12);
			
			ViewUtils.setMarginTop(convertView.findViewById(R.id.view_mid_line), 27);
			ViewUtils.setMarginLeft(convertView.findViewById(R.id.view_mid_line), 11);
			ViewUtils.setMarginRight(convertView.findViewById(R.id.view_mid_line), 11);
			ViewUtils.setMarginLeft(convertView.findViewById(R.id.view_history_order_form), 30);
			ViewUtils.setMarginTop(convertView.findViewById(R.id.view_bottom_line), 34);
			ViewUtils.setMarginLeft(convertView.findViewById(R.id.view_bottom_line), 11);
			ViewUtils.setMarginRight(convertView.findViewById(R.id.view_bottom_line), 11);
			ViewUtils.setSize(convertView.findViewById(R.id.rl_total_price), LayoutParams.FILL_PARENT, 99);
			ViewUtils.setMarginRight(convertView.findViewById(R.id.tv_total_price), 30);
			
			((TextView) convertView.findViewById(R.id.tv_order_time_label)).setTextSize(DisplayUtil.textGetSizeSp(context, 30));
			holder.tvTime.setTextSize(DisplayUtil.textGetSizeSp(context, 30));
			((TextView) convertView.findViewById(R.id.tv_order_user_label)).setTextSize(DisplayUtil.textGetSizeSp(context, 30));
			holder.tvName.setTextSize(DisplayUtil.textGetSizeSp(context, 30));
			((TextView) convertView.findViewById(R.id.tv_order_tel_label)).setTextSize(DisplayUtil.textGetSizeSp(context, 30));
			holder.tvCellPhone.setTextSize(DisplayUtil.textGetSizeSp(context, 30));
			((TextView) convertView.findViewById(R.id.tv_order_rail_num_label)).setTextSize(DisplayUtil.textGetSizeSp(context, 30));
			holder.tvRailNum.setTextSize(DisplayUtil.textGetSizeSp(context, 30));
			((TextView) convertView.findViewById(R.id.tv_order_site_num_label)).setTextSize(DisplayUtil.textGetSizeSp(context, 30));
			holder.tvSiteNum.setTextSize(DisplayUtil.textGetSizeSp(context, 30));
			holder.tvTotalPrice.setTextSize(DisplayUtil.textGetSizeSp(context, 36));
			holder.tvSiteCount.setTextSize(DisplayUtil.textGetSizeSp(context, 36));
			((TextView) convertView.findViewById(R.id.tv_eat_position)).setTextSize(DisplayUtil.textGetSizeSp(context, 30));
			((TextView) convertView.findViewById(R.id.tv_eat_at_car)).setTextSize(DisplayUtil.textGetSizeSp(context, 30));
			((TextView) convertView.findViewById(R.id.tv_site_count_label)).setTextSize(DisplayUtil.textGetSizeSp(context, 30));
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder)convertView.getTag();
		}
		
		final HistoryOrderForm orderForm = historyOrderList.get(position);
		
		String cellPhone = orderForm.getTelephone();
		if (cellPhone != null && cellPhone.length() > 4) {
			cellPhone = cellPhone.substring(0, 3) + "****" + cellPhone.substring(cellPhone.length() - 4, cellPhone.length());
		}
		
		if ("0".equals(orderForm.getEat_position())) {
			holder.rlCar.setVisibility(View.GONE);
			holder.rlSite.setVisibility(View.VISIBLE);
		} else {
			holder.rlCar.setVisibility(View.VISIBLE);
			holder.rlSite.setVisibility(View.GONE);
		}
		holder.tvTime.setText(DateUtils.getStringFromMilli(orderForm.getOrder_time(),"yyyy-MM-dd HH:mm"));
		holder.tvName.setText(orderForm.getNick_name());
		holder.tvCellPhone.setText(cellPhone);
		holder.tvRailNum.setText(orderForm.getRail_num());
		holder.tvSiteNum.setText(orderForm.getSite_num());
		holder.tvTotalPrice.setText("总消费：" + orderForm.getTotal_price() + "元");
		holder.tvSiteCount.setText(orderForm.getSite_count() + "位");
		String contentStr = orderForm.getContent();
		Log.d(TAG,"contentStr = " + contentStr);
		final List<OrderFormItem> orderList = new Gson().fromJson(contentStr, new TypeToken<List<OrderFormItem>>(){}.getType());
		
		holder.ivState.setClickable(false);
		if ("0".equals(orderForm.getStatus())) {
			holder.ivState.setImageResource(R.drawable.state_icon_success);
		} else if ("2".equals(orderForm.getStatus())) {
			holder.ivState.setImageResource(R.drawable.state_icon_processing);
		} else if ("1".equals(orderForm.getStatus())) {
			holder.ivState.setImageResource(R.drawable.btn_send_again);
		} else if ("-1".equals(orderForm.getStatus())) {
			holder.ivState.setImageResource(R.drawable.btn_net_error);
		}
		
		if ("1".equals(orderForm.getStatus()) || "-1".equals(orderForm.getStatus())) {
			holder.ivState.setClickable(true);
			
			holder.ivState.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					List<ReqGoodListElem> reqList = new ArrayList<ReqGoodListElem>();
					for (OrderFormItem orderItem:orderList) {
						ReqGoodListElem elem = new ReqGoodListElem();
						elem.object_id = orderItem.id;
						elem.count = orderItem.orderNum;
						elem.type_id = orderItem.typeId;
						reqList.add(elem);
					}
					
					Requester.requestGoodOrder(handler,
							orderForm.getNick_name(),
							orderForm.getTelephone(), 
							orderForm.getRail_num(), 
							orderForm.getSite_num(),
							reqList.toArray(new ReqGoodListElem[0]), 
							orderForm.getTrain_num(),
							orderForm.getOrder_code(),
							orderForm.getEat_position(),
							orderForm.getSite_count());
				}
			});
		}
		holder.historyOrderFormView.setOrderFormItem(orderList);
		holder.historyOrderFormView.invalidate();
		return convertView;
	}
	
	public class ViewHolder {
		private TextView tvName;
		private TextView tvCellPhone;
		private TextView tvTime;
		private TextView tvRailNum;
		private TextView tvSiteNum;
		private TextView tvTotalPrice;
		private ImageView ivState;
		private TextView tvSiteCount;
		private RelativeLayout rlSite;
		private RelativeLayout rlCar;
		private HistoryOrderFormView historyOrderFormView;
	}

}
