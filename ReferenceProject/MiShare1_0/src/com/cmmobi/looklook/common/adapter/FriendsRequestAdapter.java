package com.cmmobi.looklook.common.adapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.cmmobi.looklook.R;
import com.cmmobi.looklook.activity.OtherZoneActivity;
import com.cmmobi.looklook.activity.SettingPersonalInfoActivity;
import com.cmmobi.looklook.common.gson.GsonResponse3;
import com.cmmobi.looklook.common.gson.GsonResponse3.addfriendResponse;
import com.cmmobi.looklook.common.gson.Requester3;
import com.cmmobi.looklook.common.view.ContactsComparator;
import com.cmmobi.looklook.common.view.FriendsExpressionView;
import com.cmmobi.looklook.common.view.XEditDialog;
import com.cmmobi.looklook.fragment.VShareFragment;
import com.cmmobi.looklook.info.profile.AccountInfo;
import com.cmmobi.looklook.info.profile.ActiveAccount;
import com.cmmobi.looklook.info.profile.WrapUser;
import com.cmmobi.looklook.prompt.Prompt;
import com.cmmobi.looklook.receiver.UserDatasMessageReceiver.FriendsSortTask;
import com.cmmobi.sns.utils.PinYinUtil;
import com.nostra13.universalimageloader.api.AnimateFirstDisplayListener;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;

public class FriendsRequestAdapter extends BaseAdapter implements Callback {

	private List<WrapUser> wrapUsers = new ArrayList<WrapUser>();
	private LayoutInflater inflater;

	private Context context;

	private Handler handler;
	private AccountInfo accountInfo;
	
	//使用开源的webimageloader
	private DisplayImageOptions options;
	protected ImageLoader imageLoader;
	private ImageLoadingListener animateFirstListener;

	public FriendsRequestAdapter(Context context) {
		this.context = context;
		this.inflater = LayoutInflater.from(context);
		this.handler = new Handler(this);
		
		accountInfo = AccountInfo.getInstance(ActiveAccount.getInstance(context).getUID());
		
		animateFirstListener = new AnimateFirstDisplayListener();
		imageLoader = ImageLoader.getInstance();
		
		options = new DisplayImageOptions.Builder()
		.showStubImage(R.drawable.moren_touxiang)
		.showImageForEmptyUri(R.drawable.moren_touxiang)
		.showImageOnFail(R.drawable.moren_touxiang)
		.cacheInMemory(true)
		.cacheOnDisc(true)
		.build();
	}

	@Override
	public int getCount() {
		return wrapUsers.size();
	}
	public void setData(List<WrapUser> wrapUsers) {
		if(wrapUsers == null){
			return;
		}
		this.wrapUsers.clear();
		this.wrapUsers.addAll(wrapUsers);	
	}
	
	public void clearData(){
		this.wrapUsers.clear();
		this.notifyDataSetChanged();
	}

	@Override
	public WrapUser getItem(int position) {
		return wrapUsers.get(position);
	}

	public void removeUser(String userid){
		if(userid == null || userid.isEmpty()){
			return;
		}
		for(int i=0; i< wrapUsers.size(); i++){
			if(userid.equals(wrapUsers.get(i).userid)){
				wrapUsers.remove(i);
				notifyDataSetChanged();
				break;
			}
		}
	}
	
	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public boolean handleMessage(Message msg) {

		switch (msg.what) {
		case Requester3.RESPONSE_TYPE_AGREE_FRIEND:
			GsonResponse3.agreeFriendResponse response = (GsonResponse3.agreeFriendResponse) msg.obj;
			if(response!=null && "0".equals(response.status)){
				Prompt.Alert(context, "加好友成功!");
				for (int i = 0; i < wrapUsers.size(); i++) {
					if (null == response.target_userid) {
						break;
					}
					if (response.target_userid.equals(wrapUsers.get(i).userid)) {
						wrapUsers.get(i).request_status = "4";
						AccountInfo accountInfo = AccountInfo.getInstance(ActiveAccount.getInstance(context).getUID());
						accountInfo.friendsListName.addMember(wrapUsers.get(i));
						FriendsSortTask task = new FriendsSortTask();
						task.execute();
						this.notifyDataSetChanged();
					}
				}
				if(response.user_time!=null && !response.user_time.isEmpty()){
					accountInfo.t_friendsRequestList = response.user_time;
				}
			}else{
				Prompt.Alert(context, "加好友失败, 网络不给力!");
			}
			break;
		case Requester3.RESPONSE_TYPE_ADD_FRIEND:
			addfriendResponse aresponse = (addfriendResponse) msg.obj;
			if (msg.obj != null && "0".equals(aresponse.status)) {
				if(aresponse.target_userid == null){
						break;
					}
					for (int i = 0; i < wrapUsers.size(); i++) {
						WrapUser user = wrapUsers.get(i);
						if (aresponse.target_userid.equals(user.userid)) {
							Prompt.Alert(context, "好友申请已发送");
							wrapUsers.get(i).request_status = "2";
							notifyDataSetChanged();
							break;
						}
					}
				}else{
					Prompt.Alert(context, "加好友失败, 网络不给力!");
				}
			break;
		default:
			break;
		}
		return false;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		ViewHolder viewHolder = null;
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.list_item_friends_request, null);
			viewHolder = new ViewHolder();
			viewHolder.iv_portrait = (ImageView) convertView
					.findViewById(R.id.iv_portrait);
			viewHolder.tv_nickname = (TextView) convertView
					.findViewById(R.id.tv_name);
			viewHolder.tv_message = (TextView) convertView.findViewById(R.id.tv_message);
			viewHolder.tv_agree = (TextView) convertView
					.findViewById(R.id.tv_agree);
			viewHolder.tv_info = (TextView) convertView.findViewById(R.id.tv_info);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		viewHolder.iv_portrait.setTag(wrapUsers.get(position).userid);
		viewHolder.iv_portrait.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				if (null != v.getTag()) {
					Intent intent = new Intent(context,
							OtherZoneActivity.class);
					intent.putExtra(OtherZoneActivity.OTHER_ZONE_USERID, v.getTag() + "");
					context.startActivity(intent);
				}
			}
		});
		
		viewHolder.tv_agree.setTag(wrapUsers.get(position));
		//request_status":"1"  //请求状态 1添加 2等待验证 3接受 4已添加
		if("1".equals(wrapUsers.get(position).request_status)){
			viewHolder.tv_agree.setText("添加");
			viewHolder.tv_agree.setTextColor(context.getResources().getColor(R.color.blue));
			viewHolder.tv_agree.setOnClickListener(new OnClickListener() {
				
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
		}else if("2".equals(wrapUsers.get(position).request_status)){
			viewHolder.tv_agree.setText("等待验证");
			viewHolder.tv_agree.setTextColor(context.getResources().getColor(R.color.gray));
		}else if("3".equals(wrapUsers.get(position).request_status)){
			viewHolder.tv_agree.setText("接受");
			viewHolder.tv_agree.setTextColor(context.getResources().getColor(R.color.green));
			viewHolder.tv_agree.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if(TextUtils.isEmpty(accountInfo.nickname)){
						//修改个信息
						Intent shareIntent = new Intent(context, SettingPersonalInfoActivity.class);
						context.startActivity(shareIntent);	
					}else{		
						final WrapUser user = (WrapUser)v.getTag();
						Requester3.agreeFriendRequest(handler, user.userid);
					}
				}
			});
			if(wrapUsers.get(position).requestmsg!=null && wrapUsers.get(position).requestmsg.isEmpty()){
				viewHolder.tv_message.setText("请求添加为好友");
			}else{
				viewHolder.tv_message.setText(wrapUsers.get(position).requestmsg);
			}
		}else if("4".equals(wrapUsers.get(position).request_status)){
			viewHolder.tv_agree.setText("已添加");
			viewHolder.tv_agree.setTextColor(context.getResources().getColor(R.color.gray));
			viewHolder.tv_agree.setOnClickListener(null);
		}else{
			viewHolder.tv_agree.setText("");
			viewHolder.tv_agree.setOnClickListener(null);
		}
		
		//viewHolder.tv_nickname.setText(wrapUsers.get(position).nickname);
		if(wrapUsers.get(position).nickname != null && !wrapUsers.get(position).nickname.isEmpty()){
			FriendsExpressionView.replacedExpressions(wrapUsers.get(position).nickname, viewHolder.tv_nickname);
		}else if(wrapUsers.get(position).telname != null && !wrapUsers.get(position).telname.isEmpty()){
			FriendsExpressionView.replacedExpressions(wrapUsers.get(position).telname, viewHolder.tv_nickname);
		}else{
			viewHolder.tv_nickname.setText("");
		}
		if("2".equals(wrapUsers.get(position).source)){
			//viewHolder.tv_info.setText("手机联系人：" + wrapUsers.get(position).telname);
			FriendsExpressionView.replacedExpressions("手机联系人：" + wrapUsers.get(position).telname, viewHolder.tv_info);
		}else{
			viewHolder.tv_info.setText(" ");
		}
		if(wrapUsers.get(position)!=null && wrapUsers.get(position).headimageurl!=null){	
			imageLoader.displayImageEx(wrapUsers.get(position).headimageurl, viewHolder.iv_portrait, options, animateFirstListener, ActiveAccount.getInstance(context).getUID(), 1);
		}else{
			viewHolder.iv_portrait.setImageResource(R.drawable.moren_touxiang);
		}
		return convertView;
	}

	class ViewHolder {
		ImageView iv_portrait;
		TextView tv_nickname;
		TextView tv_message;
		TextView tv_agree;
		TextView tv_info;
	}

}
