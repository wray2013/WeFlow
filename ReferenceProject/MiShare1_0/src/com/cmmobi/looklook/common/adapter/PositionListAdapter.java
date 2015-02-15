package com.cmmobi.looklook.common.adapter;

import java.util.List;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cmmobi.looklook.R;
import com.cmmobi.looklook.info.location.POIAddressInfo;

public class PositionListAdapter extends ArrayAdapter<POIAddressInfo>{
	
	private static final String TAG = "PositionListAdapter";
	private Context context;
	List<POIAddressInfo> positions;
	LayoutInflater inflater;
	private OnClickListener clickListener;
	public int checkedPosition = -1;
	

	public PositionListAdapter(Context context, int resource,
			int textViewResourceId, List<POIAddressInfo> objects) {
		super(context, resource, textViewResourceId, objects);
		// TODO Auto-generated constructor stub
		this.context = context;
		positions = objects;
		inflater = LayoutInflater.from(context);
	}
	
	public void setClickListener(OnClickListener listener) {
		this.clickListener = listener;
	}
	
	public POIAddressInfo getCheckPositionStr() {
		Log.d(TAG,"checkedPosition = " + checkedPosition);
		if (checkedPosition >= 0 && checkedPosition < positions.size()) {
			return positions.get(checkedPosition);
		}
		return null;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final ViewHolder holder;
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.list_item_poi_position, null);
			holder = new ViewHolder();
			holder.posText = (TextView) convertView.findViewById(R.id.tv_position);
			holder.rl_row = (RelativeLayout) convertView.findViewById(R.id.rl_list_item_position);
			holder.imgView = (ImageView) convertView.findViewById(R.id.iv_edit_position_selected);
			holder.position = position;
			convertView.setTag(holder);
			
			//set click event
			if (clickListener != null) {
				holder.rl_row.setOnClickListener(clickListener);
			}
			
		} else {
            holder = (ViewHolder) convertView.getTag();
        }
		
		Log.d(TAG,"checkedPosition = " + checkedPosition + " position = " + position);
		
		if (checkedPosition != position) {
			holder.imgView.setImageResource(R.drawable.wzxz_normal);
		} else {
			holder.imgView.setImageResource(R.drawable.wzxz_selected);
		}

		holder.posText.setText(positions.get(position).position);
		holder.position = position;
		
		return convertView;
	}
	
    public static class ViewHolder {
        // CheckBox selectItemCheckBox ;
       TextView posText;
       RelativeLayout rl_row;
       ImageView imgView;
       public int position;
    }

}
