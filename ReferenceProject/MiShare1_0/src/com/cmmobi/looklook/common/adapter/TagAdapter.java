package com.cmmobi.looklook.common.adapter;

import java.util.List;
import java.util.Map;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.cmmobi.looklook.R;
import com.cmmobi.looklook.common.gson.GsonResponse3.taglistItem;

public class TagAdapter extends BaseAdapter {

	private final String TAG = "TagAdapter";
	private Context context;
	private LayoutInflater inflater;
	private List<taglistItem> tagList;
	private OnClickListener listener = null;

	public TagAdapter(Context context, List<taglistItem> data,OnClickListener l) {
		this.context = context;
		tagList = data;
		inflater = LayoutInflater.from(context);
		listener = l;
	}

	@Override
	public int getCount() {
		Log.d(TAG,"getCount = " + tagList.size());
		return tagList.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}
	
	public void setItemClickListener(OnClickListener l) {
		listener = l;
	}
	

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;

		if (convertView == null) {
			convertView = inflater.inflate(
					R.layout.activity_media_tag_item, null);
			holder = new ViewHolder();
			holder.tv = (TextView)convertView.findViewById(R.id.tv_edit_media_tag);
			holder.ll = (LinearLayout) convertView.findViewById(R.id.ll_edit_media_tag_item);
			
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		
		holder.tv.setTextColor(Color.parseColor("#7D7D7D"));
		
		if (tagList.get(position).checked.equals(String.valueOf("1"))) {
			Log.d(TAG,"========position = " + position);
			holder.tv.setTextColor(Color.WHITE);
			convertView.setBackgroundResource(R.drawable.bianji_biaoqian_selected);
		} else {
			convertView.setBackgroundResource(R.drawable.bianji_biaoqian_normal);
		}
		
		holder.position = position;
		holder.tv.setText(tagList.get(position).name);
		holder.ll.setOnClickListener(listener);
		return convertView;
	}

	public class ViewHolder {
		public LinearLayout ll;
		public TextView tv;
		public int position;
	}
}