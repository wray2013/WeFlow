package com.cmmobi.looklook.common.adapter;

import java.util.List;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.cmmobi.looklook.R;
import com.cmmobi.looklook.activity.TencentFriendWeiboActivity;
import com.cmmobi.looklook.common.gson.WeiboResponse.tencentInfo;
import com.cmmobi.looklook.common.web.WebImageView;
import com.cmmobi.looklook.info.profile.ActiveAccount;


public class TencentFriendListAdapter extends ArrayAdapter<tencentInfo> {
	
	private static final String TAG = "tencentFriendListAdapter";
	private Context context;
	private List<tencentInfo>  friends;
	private List<String>  to_invite;
	private LayoutInflater inflater ;
	private Handler handler;
	
	
	public TencentFriendListAdapter(Context context, Handler handler, int resource,
			int textViewResourceId, List<tencentInfo> objects, List<String> list_to_invite) {
		super(context, resource, textViewResourceId, objects);
		// TODO Auto-generated constructor stub
		this.context = context;
		this.handler = handler;
		this.friends = objects;
		this.to_invite = list_to_invite;
		this.inflater = LayoutInflater.from (context);
	}

    @Override
    public int getCount() {
          return friends.size();
   }

    @Override
    public tencentInfo getItem(int position) {
          return friends.get(position);
   }

    @Override
    public long getItemId(int position) {
          return position;
   }

	
	
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {

		//ViewHolder holder;
		WebImageView wv;
		TextView tv;
		CheckBox cb;
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.row_list_friend, null);

		} /*else {
            holder = (ViewHolder) convertView.getTag();
        }*/
		
		wv = (WebImageView) convertView.findViewById(R.id.wiv_row_list_friend_head);
		tv = (TextView) convertView.findViewById(R.id.tv_row_list_friend_nick);
		cb =  (CheckBox) convertView.findViewById(R.id.ck_row_list_friend_choose);
		
		tencentInfo item = getItem(position);
		
		Log.d(TAG, "getView pos:" + position + " nick:" + item.nick + " url:" + item.headurl + "/50" + " urllen:" + item.headurl.length());

		
		if(item.headurl!=null  && item.headurl.length()!=0){

			wv.setImageUrl(handler, R.drawable.zone_1,ActiveAccount.getInstance(context).getUID(), 1, item.headurl + "/50", false);
			
		}
		
		tv.setText(item.nick);
		cb.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				CheckBox cb = (CheckBox)v;  
				if(cb.isChecked()){
					to_invite.add(friends.get(position).name);
					if(to_invite.size()==1){
						Message msg = handler.obtainMessage(TencentFriendWeiboActivity.HANDLER_FLAG_ENABLE_INVITE);
						msg.sendToTarget();
					}
				}else{
					to_invite.remove(friends.get(position).name) ;
					if(to_invite.size()==0){
						Message msg = handler.obtainMessage(TencentFriendWeiboActivity.HANDLER_FLAG_DISABLE_INVITE);
						msg.sendToTarget();
					}
				}
				 
			}
		});


		//-- holder.nick.setText(item.nick);

		
		return convertView;
	}
	
    static class ViewHolder {
        // CheckBox selectItemCheckBox ;
       TextView nick;
       WebImageView head;
       CheckBox choose;
    }



}
