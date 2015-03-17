package com.cmmobi.railwifi.adapter;

import java.util.ArrayList;
import java.util.List;

import com.cmmobi.railwifi.R;
import com.cmmobi.railwifi.network.GsonResponseObject.TrainInfo;
import com.cmmobi.railwifi.utils.ViewUtils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class MileageRecordAdapter extends BaseAdapter {

	private LayoutInflater inflater;
	
	private List<TrainInfo> recList = new ArrayList<TrainInfo>();
	
	public MileageRecordAdapter(Context context) {
		inflater = LayoutInflater.from(context);
	}
	
	public void setData(List<TrainInfo> data) {
		// TODO Auto-generated method stub
		recList.clear();
		recList.addAll(data);
		notifyDataSetChanged();
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return recList.size();
	}

	@Override
	public TrainInfo getItem(int position) {
		// TODO Auto-generated method stub
		return recList.get(position);
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
		
		if(object instanceof TrainInfo) {
			final TrainInfo TrainInfo = (TrainInfo) object;
			if (convertView == null) {
				convertView = inflater.inflate(R.layout.activity_mileage_record_list_item, null);
				holder = new ViewHolder();
				holder.llRecordLeft = (LinearLayout) convertView.findViewById(R.id.ll_record_left);
				holder.llRecordMid  = (LinearLayout) convertView.findViewById(R.id.ll_record_mid);
				holder.tvHeight  = (TextView) convertView.findViewById(R.id.tv_h);
				holder.tvSetout  = (TextView) convertView.findViewById(R.id.tv_setout);
				holder.tvArrived = (TextView) convertView.findViewById(R.id.tv_arrive_at);
				holder.tvMileage = (TextView) convertView.findViewById(R.id.tv_mileage);
				holder.tvDuration= (TextView) convertView.findViewById(R.id.tv_duration);
				holder.tvDate    = (TextView) convertView.findViewById(R.id.tv_date);
				holder.tvTrainNo = (TextView) convertView.findViewById(R.id.tv_train_no);
				holder.tvPts     = (TextView) convertView.findViewById(R.id.tv_pts);
				
				initView(holder);
				
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			bindRecord(holder, TrainInfo);
		}
		return convertView;
	}
	
	private void initView(ViewHolder holder) {
		// TODO Auto-generated method stub
		ViewUtils.setHeight(holder.tvHeight, 168);
		ViewUtils.setMarginLeft(holder.llRecordLeft, 20);
		ViewUtils.setMarginLeft(holder.llRecordMid, 90);
	}

	private void bindRecord(ViewHolder holder, TrainInfo TrainInfo) {
		// TODO Auto-generated method stub
		holder.tvSetout.setText(TrainInfo.starting);
		holder.tvArrived.setText(TrainInfo.ending);
		holder.tvMileage.setText(TrainInfo.mileage);
		holder.tvDuration.setText(TrainInfo.hours);
		holder.tvDate.setText(TrainInfo.date);
		holder.tvTrainNo.setText(TrainInfo.train_num);
		holder.tvPts.setText(TrainInfo.points);
	}

	public class ViewHolder {
		LinearLayout llRecordLeft, llRecordMid;
		TextView tvHeight, tvSetout, tvArrived, tvMileage, tvDuration, tvDate, tvTrainNo, tvPts;
	}

}
