package com.cmmobi.looklook.common.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import cn.zipper.framwork.core.ZApplication;

import com.baidu.platform.comapi.map.n;
import com.cmmobi.looklook.R;
import com.cmmobi.looklook.activity.FriendsSeacherActivity;
import com.cmmobi.looklook.activity.LookLookActivity;
import com.cmmobi.looklook.activity.OtherZoneActivity;
import com.cmmobi.looklook.activity.SettingPersonalInfoActivity;
import com.cmmobi.looklook.common.gson.GsonResponse3.addfriendResponse;
import com.cmmobi.looklook.common.gson.GsonResponse3.deleteFriendResponse;
import com.cmmobi.looklook.common.gson.Requester3;
import com.cmmobi.looklook.common.view.FriendsExpressionView;
import com.cmmobi.looklook.common.view.XEditDialog;
import com.cmmobi.looklook.info.profile.AccountInfo;
import com.cmmobi.looklook.info.profile.ActiveAccount;
import com.cmmobi.looklook.info.profile.WrapUser;
import com.cmmobi.looklook.prompt.Prompt;
import com.nostra13.universalimageloader.api.AnimateFirstDisplayListener;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;

public class FriendsSeacherAdapter extends BaseAdapter implements Callback {

	private LayoutInflater inflater;
	private Context context;
	private Handler handler;

	
	private ArrayList<WrapUser> users = new ArrayList<WrapUser>();
	
	//使用开源的webimageloader
	private DisplayImageOptions options;
	protected ImageLoader imageLoader;
	private ImageLoadingListener animateFirstListener;
	private String userID;
	private AccountInfo accountInfo;

	public FriendsSeacherAdapter(Context context) {
		this.context = context;
		this.inflater = LayoutInflater.from(context);
		handler = new Handler(this);
		
		animateFirstListener = new AnimateFirstDisplayListener();
		imageLoader = ImageLoader.getInstance();
		
		options = new DisplayImageOptions.Builder()
		.showStubImage(R.drawable.moren_touxiang)
		.showImageForEmptyUri(R.drawable.moren_touxiang)
		.showImageOnFail(R.drawable.moren_touxiang)
		.cacheInMemory(true)
		.cacheOnDisc(true)
		.build();
		userID = ActiveAccount.getInstance(ZApplication.getInstance()).getUID();
		accountInfo = AccountInfo.getInstance(userID);
	}

	public void setData(WrapUser[] users) {
		this.users.clear();
		for(int i=0; i< users.length; i++){
			this.users.add(users[i]);
		}
	}

	@Override
	public int getCount() {
		return null == users ? 0 : users.size();
	}

	@Override
	public Object getItem(int position) {
		return position;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder = null;
		if (convertView == null) {
			convertView = inflater
					.inflate(R.layout.list_item_friends_add, null);
			viewHolder = new ViewHolder();
			viewHolder.iconImageView = (ImageView) convertView
					.findViewById(R.id.friend_icon_imageview);
			viewHolder.nameTextView = (TextView) convertView
					.findViewById(R.id.friend_name_textview);
			viewHolder.stateTextView = (TextView) convertView
					.findViewById(R.id.tv_state);
			viewHolder.messageTextView = (TextView) convertView.findViewById(R.id.tv_message);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		final WrapUser user = (WrapUser) users.get(position);
		//viewHolder.nameTextView.setText(user.nickname);
		FriendsExpressionView.replacedExpressions(user.nickname, viewHolder.nameTextView);
		viewHolder.messageTextView.setText(LookLookActivity.APP_NAME + "号：" + user.micnum);
		if(user!=null && user.headimageurl!=null){	
			imageLoader.displayImageEx(user.headimageurl, viewHolder.iconImageView, options, animateFirstListener, ActiveAccount.getInstance(context).getUID(), 1);
			viewHolder.iconImageView.setTag(user.userid);
		}else{
			viewHolder.iconImageView.setImageResource(R.drawable.moren_touxiang);
		}
		
		if(null == userID || !userID.equals(user.userid)){
			if ("1".equals(user.isfriend)) {
				viewHolder.stateTextView.setText("已添加");
				viewHolder.stateTextView.setTextColor(context.getResources().getColor(R.color.gray));
				viewHolder.stateTextView.setVisibility(View.VISIBLE);
			/*	viewHolder.stateTextView.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						if(accountInfo.friendsListName.findUserByUserid(user.userid) !=null){
							Intent intent = new Intent(context,
									OtherZoneActivity.class);
							intent.putExtra(OtherZoneActivity.OTHER_ZONE_USERID, user.userid);
							context.startActivity(intent);
							}
					}
				});
				viewHolder.iconImageView.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						if(accountInfo.friendsListName.findUserByUserid(user.userid) !=null){
							Intent intent = new Intent(context,
									OtherZoneActivity.class);
							intent.putExtra(OtherZoneActivity.OTHER_ZONE_USERID, user.userid);
							context.startActivity(intent);
							}
					}
				});*/
			} else {
				viewHolder.stateTextView.setText("添加");
				viewHolder.stateTextView.setTextColor(context.getResources().getColor(R.color.blue));
				viewHolder.stateTextView.setVisibility(View.VISIBLE);
				/*viewHolder.stateTextView.setTag(user);
				viewHolder.stateTextView.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						if(TextUtils.isEmpty(accountInfo.nickname)){
							//修改个信息
							Intent shareIntent = new Intent(context, SettingPersonalInfoActivity.class);
							context.startActivity(shareIntent);	
						}else{
							final WrapUser user = (WrapUser)v.getTag();
							new XEditDialog.Builder(context)
							.setTitle(R.string.xeditdialog_title)
							.setPositiveButton(R.string.send, new OnClickListener() {
								
								@Override
								public void onClick(View v) {
									//加好友
									Requester3.addFriend(handler, user.userid, v.getTag().toString());
								}
							})
							.setNegativeButton(android.R.string.cancel, null)
							.create().show();
						}
					}
				});
				
				viewHolder.iconImageView.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						if(TextUtils.isEmpty(accountInfo.nickname)){
							//修改个信息
							Intent shareIntent = new Intent(context, SettingPersonalInfoActivity.class);
							context.startActivity(shareIntent);	
						}else{
							final String userid = (String)v.getTag();
							new XEditDialog.Builder(context)
							.setTitle(R.string.xeditdialog_title)
							.setPositiveButton(R.string.send, new OnClickListener() {
								
								@Override
								public void onClick(View v) {
									//加好友
									Requester3.addFriend(handler, userid, v.getTag().toString());
								}
							})
							.setNegativeButton(android.R.string.cancel, null)
							.create().show();
						}
					}
				});*/
			}
		}else{
			viewHolder.stateTextView.setVisibility(View.GONE);
			/*viewHolder.stateTextView.setOnClickListener(null);
			viewHolder.iconImageView.setOnClickListener(null);*/
		}
		
		return convertView;
	}

	class ViewHolder {
		ImageView iconImageView;
		TextView nameTextView;
		TextView stateTextView;
		TextView messageTextView;
	}

	
	
	@Override
	public boolean handleMessage(Message msg) {
		switch (msg.what) {
		case Requester3.RESPONSE_TYPE_ADD_FRIEND:
			if (msg.obj != null) {
				addfriendResponse response = (addfriendResponse) msg.obj;
				if ("0".equals(response.status)) {
					if(response.target_userid == null){
						break;
					}
					for (int i = 0; i < users.size(); i++) {
						WrapUser user = users.get(i);
						if (response.target_userid.equals(user.userid)) {
							user.request_status = "2";
							user.update_time = System.currentTimeMillis() + "";
							accountInfo.friendsRequestList.insertMember(0, user);
							Prompt.Alert(context, "好友申请已发送");
							break;
						}
					}
				}
			}
			break;
		default:
			break;
		}
		return false;
	}

}
