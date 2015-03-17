package com.cmmobi.railwifi.adapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cmmobi.railwifi.R;
import com.cmmobi.railwifi.utils.DisplayUtil;

public class MoreAdapter extends BaseAdapter
{
	private Context mCtx;
	private ArrayList<String> listItems = new ArrayList<String>();
	
	public MoreAdapter(Context ctx, ArrayList<String> list)
	{
		mCtx = ctx;
		listItems.addAll(list);
	}
	
	public void setData(ArrayList<String> list){
		listItems.clear();
		listItems.addAll(list);
	}
	
	@Override
	public int getCount() {
		return listItems.size();
	}

	@Override
	public String getItem(int position) {
		return listItems.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ListItemsView item = null;
		if(convertView == null){
			convertView = LayoutInflater.from(mCtx).inflate(R.layout.more_popup_window_item, null);
			item = new ListItemsView();
			item.menuText = (TextView) convertView.findViewById(R.id.tv_item_title);
			int paddingSize = DisplayUtil.getSize(mCtx, 6);
			item.menuText.setPadding(paddingSize, paddingSize, paddingSize, paddingSize);
			LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) item.menuText.getLayoutParams();
			lp.height = DisplayUtil.getSize(mCtx, 70);
			item.menuText.setLayoutParams(lp);
			item.menuText.setTextSize(DisplayUtil.textGetSizeSp(mCtx, 35));
			convertView.setTag(item);
		}else{
			item = (ListItemsView)convertView.getTag();
		}
		item.menuText.setText(listItems.get(position));
		
		return convertView;
	}

	

	
	public final class ListItemsView{
		public TextView menuText;
	}
	
	
}