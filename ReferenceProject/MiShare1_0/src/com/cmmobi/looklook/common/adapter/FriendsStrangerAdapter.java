package com.cmmobi.looklook.common.adapter;

import java.util.Iterator;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cmmobi.looklook.R;
import com.cmmobi.looklook.activity.OtherZoneActivity;
import com.cmmobi.looklook.common.web.WebImageView;
import com.cmmobi.looklook.info.profile.MessageWrapper;

public class FriendsStrangerAdapter extends BaseAdapter {
	public final static int HANDLER_FLAG_PRIMSG_USER =     0x46196972;
	public static final int HANDLER_FLAG_MSG_DELETE = 0x46196973;
	
	
	private static final String TAG = "FriendsStrangerAdapter";
	

	
	private Context context;
	private Handler handler;

	private List<MessageWrapper> list;

	private LayoutInflater inflater;

	public FriendsStrangerAdapter(Context context, Handler handler, List<MessageWrapper>  list) {
		this.context = context;
		this.handler = handler;
		this.list = list;
		this.inflater = LayoutInflater.from(context);

	}
	
	public void setlistData(List<MessageWrapper> list){
		this.list = list;
	}
	

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final MessageWrapper item = list.get(position);
		if(item==null){
			return convertView;
		}
		
		ViewHolder viewHolder;
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.list_item_friends_message, null);
			viewHolder = new ViewHolder();

			viewHolder.imageView = (WebImageView) convertView.findViewById(R.id.image);
			viewHolder.rl_hudong_jiaobiao = (RelativeLayout) convertView.findViewById(R.id.rl_hudong_jiaobiao);
			viewHolder.tv_hudong_jiaobiao = (TextView) convertView.findViewById(R.id.tv_hudong_jiaobiao);
			viewHolder.rl_stranger = (RelativeLayout) convertView.findViewById(R.id.rl_stranger);
			viewHolder.text_nickname = (TextView) convertView.findViewById(R.id.nickname);
			viewHolder.text_content = (TextView) convertView.findViewById(R.id.content);
			
			viewHolder.arrowImageView = (ImageView) convertView.findViewById(R.id.arrow);
			viewHolder.textLinearLayout= (LinearLayout) convertView.findViewById(R.id.text_ll);



		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		
		if(item.getUnreadMsgs()>0){
			viewHolder.rl_hudong_jiaobiao.setVisibility(View.VISIBLE);
			viewHolder.tv_hudong_jiaobiao.setText(item.getUnreadMsgs() + "");
		}else{
			viewHolder.rl_hudong_jiaobiao.setVisibility(View.GONE);
		}
		
		if(item.headimageurl!=null && item.headimageurl.length()>0){
			viewHolder.imageView.setImageUrl(R.drawable.icon_head_default, 4, item.headimageurl, false);
		}else{
			viewHolder.imageView.setImageResource(R.drawable.icon_head_default);
		}

		if(!TextUtils.isEmpty(item.markname)){
			viewHolder.text_nickname.setText(item.markname);
		}else{
			viewHolder.text_nickname.setText(item.nickname);
		}
		
		viewHolder.text_content.setText(item.content);
		
		viewHolder.imageView.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				//Toast.makeText(context, "item head click:" + item.userid, Toast.LENGTH_SHORT).show();
				launchOtherHomePage(item.other_userid, item.nickname);
			}
		} );
		
/*		viewHolder.rl_stranger.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				//Toast.makeText(context, "item click:" + item.userid, Toast.LENGTH_SHORT).show();
				handler.obtainMessage(HANDLER_FLAG_PRIMSG_USER, item.userid).sendToTarget();
			}
		});*/
		
		convertView.setTag(viewHolder);

		return convertView;
	}
	
	public void removeElement(String userid) {
		// TODO Auto-generated method stub
		if(list!=null){
			Iterator<MessageWrapper> it = list.iterator();

			while(it.hasNext()){
				MessageWrapper elem = it.next();
				if(elem.other_userid!=null && elem.other_userid.equals(userid)){
					it.remove();
				}
			}
		}
	}

	private void launchOtherHomePage(String userid, String nick) {
		// TODO Auto-generated method stub
		if(userid!=null){
//			Intent intent = new Intent(context, HomepageOtherDiaryActivity.class);
//			intent.putExtra(HomepageOtherDiaryActivity.INTENT_ACTION_USERID, userid);
//			intent.putExtra(HomepageOtherDiaryActivity.INTENT_ACTION_NICKNAME, nick);
//			context.startActivity(intent);
			Intent intent = new Intent(context, OtherZoneActivity.class);
			intent.putExtra(OtherZoneActivity.OTHER_ZONE_USERID, userid);
			context.startActivity(intent);
		}
	}

	class ViewHolder {

		public TextView tv_hudong_jiaobiao;
		public RelativeLayout rl_hudong_jiaobiao;
		RelativeLayout rl_stranger;
		WebImageView imageView;
		TextView text_nickname;
		TextView text_content;

		ImageView arrowImageView;
		LinearLayout textLinearLayout;
	}

}
