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
import com.cmmobi.looklook.common.gson.GsonResponse2;
import com.cmmobi.looklook.common.gson.GsonResponse2.User;
import com.cmmobi.looklook.common.web.WebImageView;
import com.cmmobi.looklook.info.profile.ActiveAccount;

public class FriendListAdapter extends ArrayAdapter<GsonResponse2.User> {
	private Context context;
	private List<User>  friends;
	private LayoutInflater inflater;
	private Handler handler;
	
	public FriendListAdapter(Context context, Handler handler, int resource,
			int textViewResourceId, List<User> objects) {
		super(context, resource, textViewResourceId, objects);
		// TODO Auto-generated constructor stub
		this.context = context;
		this.friends = objects;
		this.inflater = LayoutInflater.from (context);
		this.handler = handler;
	}

	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		final ViewHolder holder;
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.row_list_friend, null);
			holder = new ViewHolder();
			holder.head = (WebImageView) convertView.findViewById(R.id.wiv_row_list_friend_head);
			holder.nick = (TextView) convertView.findViewById(R.id.tv_row_list_friend_nick);
			convertView.setTag(holder);
		} else {
            holder = (ViewHolder) convertView.getTag();
        }

		User item = getItem(position);
		holder.head.setImageUrl(handler, R.drawable.zone_1, ActiveAccount.getInstance(context).getUID(), 1, item.portraiturl, true);
		holder.nick.setText(item.nickname);
		
		return convertView;
	}
	
    static class ViewHolder {
        // CheckBox selectItemCheckBox ;
       TextView nick;
       WebImageView head;
    }



}
