package com.cmmobi.looklook.common.adapter;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cmmobi.looklook.MainApplication;
import com.cmmobi.looklook.R;
import com.cmmobi.looklook.activity.LookLookActivity;
import com.cmmobi.looklook.activity.OtherZoneActivity;
import com.cmmobi.looklook.common.gson.Requester3;
import com.cmmobi.looklook.common.utils.DateUtils;
import com.cmmobi.looklook.common.view.FriendsExpressionView;
import com.cmmobi.looklook.common.view.XEditDialog;
import com.cmmobi.looklook.common.web.WebImageView;
import com.cmmobi.looklook.info.profile.AccountInfo;
import com.cmmobi.looklook.info.profile.ActiveAccount;
import com.cmmobi.looklook.info.profile.ContactManager;
import com.cmmobi.looklook.info.profile.MessageWrapper;
import com.cmmobi.looklook.info.profile.WrapUser;

public class FriendsMessageAdapter extends BaseAdapter {
    //1私信，2活动，3推荐，4附近，5陌生人6LOOKLOOK官方
	public static final int HANDLER_FLAG_PRIMSG_USER =     0x36196971;
	public static final int HANDLER_FLAG_PRIMSG_ACTIVITY = 0x36196972;
	public static final int HANDLER_FLAG_PRIMSG_RECOMMAND = 0x36196973;
	public static final int HANDLER_FLAG_PRIMSG_NEARBY = 0x36196974;
	public static final int HANDLER_FLAG_PRIMSG_STRANGER = 0x36196975;

	private Context context;
	private Handler handler;

	/*private List<String> urlList = new ArrayList<String>();*/
	private List<MessageWrapper> list;

	private LayoutInflater inflater;
	private boolean hasStranger;
	private int unReadStrangerMsgNum;

	public FriendsMessageAdapter(Context context, Handler handler, List<MessageWrapper> list, boolean hasStranger, int unReadStrangerMsgNum) {
		this.context = context;
		this.inflater = LayoutInflater.from(context);
		this.handler = handler;
		this.list = filterMsgList(list);
		this.hasStranger = hasStranger;
		this.unReadStrangerMsgNum = unReadStrangerMsgNum;
		
		
		
	}
	
	@Override
	public void notifyDataSetChanged() {
		// TODO Auto-generated method stub
		super.notifyDataSetChanged();
		Intent msgIntent = new Intent(LookLookActivity.UPDATE_MASK);
		LocalBroadcastManager.getInstance(context).sendBroadcast(msgIntent);
	}
	
	public void setListData(List<MessageWrapper> list, boolean hasStranger, int unReadStrangerMsgNum){
		this.list = filterMsgList(list);
		this.hasStranger = hasStranger;
		this.unReadStrangerMsgNum = unReadStrangerMsgNum;
	}

	
	/**
	 * 过滤掉listview 不显示的数据
	 * @param list
	 * @return
	 */
	private ArrayList<MessageWrapper> filterMsgList(List<MessageWrapper> list){
		ArrayList<MessageWrapper> arr = new ArrayList<MessageWrapper>();
		if(list==null || list.size() == 0){
			return arr;
		}
		for(MessageWrapper item : list){
			if(item.toShow){
				arr.add(item);
			}
		}
		return arr;
	}
	
	@Override
	public int getCount() {
		if(hasStranger){
			return list.size() + 1;
		}else{
			return list.size();
		}
		
	}

	@Override
	public Object getItem(int position) {
		MessageWrapper item = null;
		if(hasStranger){
			if(position>0){
				item = list.get(position-1);
			}
		}else{
			item = list.get(position);
		}
		
		return item;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}
	
	public boolean hasStranger(){
		return hasStranger;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		ViewHolder viewHolder;
		MessageWrapper item = null;
		
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.list_item_friends_message, null);
			viewHolder = new ViewHolder();
			
			viewHolder.imageView = (WebImageView) convertView.findViewById(R.id.image);
			viewHolder.rl_hudong_jiaobiao = (RelativeLayout) convertView.findViewById(R.id.rl_hudong_jiaobiao);
			viewHolder.tv_hudong_jiaobiao = (TextView) convertView.findViewById(R.id.tv_hudong_jiaobiao);
			viewHolder.rl_stranger = (RelativeLayout) convertView.findViewById(R.id.rl_stranger);
			viewHolder.text_nickname = (TextView) convertView.findViewById(R.id.nickname);
			viewHolder.text_content = (TextView) convertView.findViewById(R.id.content);
			
			viewHolder.strangerTextView = (TextView) convertView.findViewById(R.id.stranger);
			viewHolder.arrowImageView = (ImageView) convertView.findViewById(R.id.arrow);
			
			viewHolder.timeTextView = (TextView) convertView.findViewById(R.id.time);
			viewHolder.textLinearLayout= (LinearLayout) convertView.findViewById(R.id.text_ll);
			
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		
		if(hasStranger){
			if(position>0){
				item = list.get(position-1);
			}
		}else{
			item = list.get(position);
		}

		if(position==0 && hasStranger){

			convertView.setBackgroundColor(context.getResources().getColor(R.color.primsg_list_stranger_bg));
			
			viewHolder.imageView = (WebImageView) convertView.findViewById(R.id.image);
			viewHolder.tv_hudong_jiaobiao = (TextView) convertView.findViewById(R.id.tv_hudong_jiaobiao);
			viewHolder.rl_hudong_jiaobiao = (RelativeLayout) convertView.findViewById(R.id.rl_hudong_jiaobiao);
			viewHolder.rl_stranger = (RelativeLayout) convertView.findViewById(R.id.rl_stranger);
			viewHolder.text_nickname = (TextView) convertView.findViewById(R.id.nickname);
			viewHolder.text_content = (TextView) convertView.findViewById(R.id.content);
			
			viewHolder.strangerTextView = (TextView) convertView.findViewById(R.id.stranger);
			viewHolder.arrowImageView = (ImageView) convertView.findViewById(R.id.arrow);
			
			viewHolder.timeTextView = (TextView) convertView.findViewById(R.id.time);
			viewHolder.textLinearLayout= (LinearLayout) convertView.findViewById(R.id.text_ll);
			
			viewHolder.imageView.setImageResource(R.drawable.moshengren);
			viewHolder.strangerTextView.setVisibility(View.VISIBLE);
			viewHolder.arrowImageView.setVisibility(View.VISIBLE);
			viewHolder.timeTextView.setVisibility(View.GONE);
			viewHolder.textLinearLayout.setVisibility(View.GONE);
			
			if(unReadStrangerMsgNum>0){
				viewHolder.rl_hudong_jiaobiao.setVisibility(View.VISIBLE);
				viewHolder.tv_hudong_jiaobiao.setText(unReadStrangerMsgNum + "");
			}else{
				viewHolder.rl_hudong_jiaobiao.setVisibility(View.GONE);
			}
			
/*			viewHolder.rl_stranger.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					//Toast.makeText(context, "stranger click", Toast.LENGTH_SHORT).show();

					handler.obtainMessage(HANDLER_FLAG_PRIMSG_STRANGER).sendToTarget();
				}
			});*/
		}else{

			convertView.setBackgroundColor(context.getResources().getColor(R.color.transparent));
			
			viewHolder.strangerTextView.setVisibility(View.GONE);
			viewHolder.arrowImageView.setVisibility(View.GONE);
			viewHolder.timeTextView.setVisibility(View.VISIBLE);
			viewHolder.textLinearLayout.setVisibility(View.VISIBLE);
			
			if(item!=null){
				//viewHolder.imageView.setPortraiUrl(R.drawable.temp_local_icon, 4, item.headimageurl);
				//viewHolder.imageView.setImageResource(R.drawable.temp_local_icon);
				if(item.getUnreadMsgs()>0){
					viewHolder.rl_hudong_jiaobiao.setVisibility(View.VISIBLE);
					
					if(item.getUnreadMsgs()>99){
						viewHolder.tv_hudong_jiaobiao.setText("99+");
					}else{
						viewHolder.tv_hudong_jiaobiao.setText(item.getUnreadMsgs() + "");
					}
				}else{
					viewHolder.rl_hudong_jiaobiao.setVisibility(View.GONE);
				}
				
				
				if(item.headimageurl!=null){
					/*viewHolder.imageView.setLoadingDrawable(R.drawable.temp_local_icon);
					viewHolder.imageView.setImageUrl(item.headimageurl, 4, true);*/
					viewHolder.imageView.setImageUrl(R.drawable.icon_head_default, 4, item.headimageurl, false);
				}else{
					viewHolder.imageView.setImageResource(R.drawable.icon_head_default);
				}

				if(!TextUtils.isEmpty(item.markname)){
					//viewHolder.text_nickname.setText(item.markname);
					FriendsExpressionView.replacedExpressions(item.markname, viewHolder.text_nickname);
				}else{
					//viewHolder.text_nickname.setText(item.nickname);
					FriendsExpressionView.replacedExpressions(item.nickname, viewHolder.text_nickname);
				}
				
//				viewHolder.text_content.setText(item.content);
				FriendsExpressionView.replacedExpressions(item.content, viewHolder.text_content);
				
				if(item.lastTimeMill != 0){
					viewHolder.timeTextView.setText(DateUtils.getMyCommonListShowDate(new Date(item.lastTimeMill)));
				}else{
					viewHolder.timeTextView.setText("");
				}
				final MessageWrapper item_t = item;
				
				/*viewHolder.imageView.setOnClickListener(new View.OnClickListener() {
					
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						if("1".equals(item_t.act) || "6".equals(item_t.act)){
							launchOtherHomePage(item_t.other_userid);
						}
						
						final String other_userid = item_t.other_userid;
						
						String myuserid = ActiveAccount.getInstance(MainApplication.getAppInstance()).getUID();
						AccountInfo acc = AccountInfo.getInstance(myuserid);
						
						ContactManager friendsListContactManager=acc.friendsListName;
						WrapUser currUserInfo = friendsListContactManager.findUserByUserid(other_userid);
						
						if(currUserInfo == null){
							// 不是好友
							if(other_userid.equals(acc.serviceUser.userid)){
//								launchOtherHomePage(item_t.other_userid);
							}else{
								new XEditDialog.Builder(v.getContext())
								.setTitle(R.string.xeditdialog_title)
								.setPositiveButton(R.string.send, new OnClickListener() {
									@Override
									public void onClick(View v) {
										//加好友
										Requester3.addFriend(handler, other_userid, v.getTag().toString());
									}
								})
								.setNegativeButton(android.R.string.cancel, null)
								.create().show();
							}
						}else{
							// 是好友
							launchOtherHomePage(item_t.other_userid);
						}

					}
				});*/
				
/*				viewHolder.rl_stranger.setOnClickListener(new View.OnClickListener() {
					
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub

						//Toast.makeText(context, "item click:" + item_t.userid, Toast.LENGTH_SHORT).show();
						handler.obtainMessage(HANDLER_FLAG_PRIMSG_USER, item_t.userid).sendToTarget();
					}
				});*/
			}


		}

		convertView.setTag(viewHolder);
		return convertView;
	}

	private void launchOtherHomePage(String userid) {
		// TODO Auto-generated method stub
		if(userid!=null){
//			Intent intent = new Intent(context, HomepageOtherDiaryActivity.class);
//			intent.putExtra(HomepageOtherDiaryActivity.INTENT_ACTION_USERID, userid);
//			context.startActivity(intent);
			Intent intent = new Intent(context, OtherZoneActivity.class);
			intent.putExtra(OtherZoneActivity.OTHER_ZONE_USERID, userid);
			context.startActivity(intent);
		}
	}

	class ViewHolder {
		public RelativeLayout rl_hudong_jiaobiao;
		TextView tv_hudong_jiaobiao;
		WebImageView imageView;
		RelativeLayout rl_stranger;
		TextView text_nickname;
		TextView text_content;

		TextView strangerTextView;
		ImageView arrowImageView;
		TextView timeTextView;
		LinearLayout textLinearLayout;
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
	
	public void hideElement(String userid) {
		// TODO Auto-generated method stub
		if(list!=null){
			Iterator<MessageWrapper> it = list.iterator();
			
			while(it.hasNext()){
				MessageWrapper elem = it.next();
				if(elem.other_userid!=null && elem.other_userid.equals(userid)){
					// 更新消息显示状态  del
					elem.setToShow(false);
				}
			}
		}
	}
}
