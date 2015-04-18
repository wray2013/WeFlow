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
import com.etoc.weflow.utils.DisplayUtil;
import com.etoc.weflow.utils.ViewUtils;

public class DrawFlowAdapter extends BaseAdapter {
	List<String> itemList = null;
	Context context;
	private LayoutInflater inflater;
	private int curselected = 0;

	public void setSelect(int pos) {
		curselected = pos;
	}

	public int getSelect() {
		return curselected;
	}
	
	public int getSelectValue() {
		int ret = -1;
		String selectedvalue = getItem(getSelect());
		if(selectedvalue != null && !selectedvalue.equals("")) {
			try {
				ret = Integer.parseInt(selectedvalue);
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
		return ret;
	}

	class DrawFlowViewHolder {
		TextView tvFlow;
		ImageView ivSelected;
	}

	public DrawFlowAdapter(Context context, List<String> list) {
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
	public String getItem(int position) {
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
		DrawFlowViewHolder holder = null;
		if (convertView == null) {
			holder = new DrawFlowViewHolder();
			convertView = inflater.inflate(R.layout.item_drawflow_grid, null);
			holder.tvFlow = (TextView) convertView
					.findViewById(R.id.tv_flow_num);
			holder.ivSelected = (ImageView) convertView
					.findViewById(R.id.iv_selected);

			ViewUtils.setMarginTop(holder.ivSelected, 8);
			ViewUtils.setMarginRight(holder.ivSelected, 8);

			holder.tvFlow.setTextSize(DisplayUtil.textGetSizeSp(context, 55));
			ViewUtils.setHeight(holder.tvFlow, 142);
			ViewUtils.setSize(holder.ivSelected, 54, 54);
			convertView.setTag(holder);

		} else {
			holder = (DrawFlowViewHolder) convertView.getTag();
		}

		String item = itemList.get(position);
		holder.tvFlow.setText(item);

		if (curselected == position) {
			holder.ivSelected.setVisibility(View.VISIBLE);
			/*holder.tvFlow.setTextColor(context.getResources().getColor(
					R.color.pagertab_color_orange));*/
			holder.tvFlow.setTextColor(context.getResources().getColor(
					R.color.red_text));
			holder.tvFlow.setBackgroundResource(R.drawable.shape_square_recentage_orange);
		} else {
			holder.ivSelected.setVisibility(View.GONE);
			holder.tvFlow.setTextColor(0xff000000);
			holder.tvFlow.setBackgroundResource(R.drawable.shape_square_recentage_grey);
		}

		return convertView;
	}
}
