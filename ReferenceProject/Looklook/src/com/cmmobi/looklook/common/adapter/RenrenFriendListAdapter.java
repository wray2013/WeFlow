package com.cmmobi.looklook.common.adapter;

import java.util.List;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.cmmobi.looklook.R;
import com.cmmobi.looklook.activity.RenrenFriendWeiboActivity;
import com.cmmobi.looklook.common.gson.WeiboResponse.RWUser;
import com.cmmobi.looklook.common.web.WebImageView;
import com.cmmobi.looklook.info.profile.ActiveAccount;


public class RenrenFriendListAdapter extends ArrayAdapter<RWUser> {
	
	private static final String TAG = "RenrenFriendListAdapter";
	private Context context;
	private List<RWUser>  friends;
	private List<String>  to_invite;
	private LayoutInflater inflater ;
	private Handler handler;
	
	
	public RenrenFriendListAdapter(Context context,  Handler handler, int resource,
			int textViewResourceId, List<RWUser> friends, List<String> list_to_invite) {
		super(context, resource, textViewResourceId, friends);
		// TODO Auto-generated constructor stub
		this.context = context;
		this.friends = friends;
		this.inflater = LayoutInflater.from (context);
		this.to_invite = list_to_invite;
		this.handler = handler;
	}


	
	
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {

		final ViewHolder holder;
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.row_list_friend, null);
			holder = new ViewHolder();
			holder.head = (WebImageView) convertView.findViewById(R.id.wiv_row_list_friend_head);
			holder.nick = (TextView) convertView.findViewById(R.id.tv_row_list_friend_nick);
			holder.choose = (CheckBox) convertView.findViewById(R.id.ck_row_list_friend_choose);
			convertView.setTag(holder);
		} else {
            holder = (ViewHolder) convertView.getTag();
        }

		RWUser item = getItem(position);
		if(item.avatar!=null && item.avatar.length>0 && item.avatar[0]!=null &&  item.avatar[0].url!=null){
			holder.head.setImageUrl(handler, R.drawable.zone_1, ActiveAccount.getInstance(context).getUID(), 1, item.avatar[0].url, false);
		}
		
		holder.nick.setText(item.name);
		
		holder.choose.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				CheckBox cb = (CheckBox)v;  
				if(cb.isChecked()){
					to_invite.add(friends.get(position).name);
					if(to_invite.size()==1){
						Message msg = handler.obtainMessage(RenrenFriendWeiboActivity.HANDLER_FLAG_ENABLE_INVITE);
						msg.sendToTarget();
					}
				}else{
					to_invite.remove(friends.get(position).name) ;
					if(to_invite.size()==0){
						Message msg = handler.obtainMessage(RenrenFriendWeiboActivity.HANDLER_FLAG_DISABLE_INVITE);
						msg.sendToTarget();
					}
				}
				 
			}
		});
		
		return convertView;
	}
	
    static class ViewHolder {
        // CheckBox selectItemCheckBox ;
       TextView nick;
       WebImageView head;
       CheckBox choose;
    }



}
