package com.cmmobi.looklook.common.adapter;

import java.util.List;

import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.cmmobi.looklook.R;
import com.cmmobi.looklook.common.gson.GsonResponse2.nearVideoItem;
import com.cmmobi.looklook.common.web.WebImageView;
import com.cmmobi.looklook.info.profile.ActiveAccount;


public class NearbyListAdapter extends ArrayAdapter<nearVideoItem> {
	
	private static final String TAG = "NearbyListAdapter";
	private Context context;
	private List<nearVideoItem>  items;
	private LayoutInflater inflater ;
	private Handler handler;
	
	
	public NearbyListAdapter(Context context,  Handler handler, int resource,
			int textViewResourceId, List<nearVideoItem> items) {
		super(context, resource, textViewResourceId, items);
		// TODO Auto-generated constructor stub
		this.context = context;
		this.items = items;
		this.inflater = LayoutInflater.from (context);
		this.handler = handler;
	}


	
	
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {

		final ViewHolder holder;
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.row_list_friend, null);
			holder = new ViewHolder();
			holder.videoPic = (WebImageView) convertView.findViewById(R.id.wiv_row_list_friend_head);
			holder.nick = (TextView) convertView.findViewById(R.id.tv_row_list_friend_nick);
			convertView.setTag(holder);
		} else {
            holder = (ViewHolder) convertView.getTag();
        }

		nearVideoItem item = getItem(position);
		holder.videoPic.setImageUrl(handler, R.drawable.zone_1, ActiveAccount.getInstance(context).getUID(), 1, item.videoimage, false);
		holder.nick.setText(item.nickname);
		
		
		return convertView;
	}
	
    static class ViewHolder {
        // CheckBox selectItemCheckBox ;
       TextView nick;
       WebImageView videoPic;
    }



}
