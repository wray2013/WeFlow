package com.cmmobi.looklook.common.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import cn.zipper.framwork.core.ZApplication;

import com.cmmobi.looklook.R;
import com.cmmobi.looklook.activity.HomepageMyselfDiaryActivity;
import com.cmmobi.looklook.activity.HomepageOtherDiaryActivity;
import com.cmmobi.looklook.common.gson.GsonResponse2.attentionResponse;
import com.cmmobi.looklook.common.gson.GsonResponse2.cancelattentionResponse;
import com.cmmobi.looklook.common.gson.GsonResponse2.searchUsers;
import com.cmmobi.looklook.common.gson.Requester2;
import com.cmmobi.looklook.fragment.WrapUser;
import com.cmmobi.looklook.info.profile.AccountInfo;
import com.cmmobi.looklook.info.profile.ActiveAccount;
import com.cmmobi.looklook.info.profile.ContactManager;
import com.cmmobi.looklook.prompt.Prompt;
import com.nostra13.universalimageloader.api.AnimateFirstDisplayListener;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;
import com.nostra13.universalimageloader.core.display.CircularBitmapDisplayer;

public class FriendsSeacherAdapter extends BaseAdapter implements Callback {

	private LayoutInflater inflater;
	private Context context;
	private Handler handler;

	private searchUsers[] users;
	
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
		//.displayer(new SimpleBitmapDisplayer())
		.displayer(new CircularBitmapDisplayer()) //圆形图片
		//.displayer(new RoundedBitmapDisplayer(20)) 圆角图片
		.build();
		userID = ActiveAccount.getInstance(ZApplication.getInstance()).getUID();
	}

	public void setData(searchUsers[] users) {
		this.users = users;
	}

	@Override
	public int getCount() {
		return null == users ? 0 : users.length;
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
			viewHolder.relationImageView = (ImageView) convertView
					.findViewById(R.id.friend_relation_imageview);

			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		searchUsers user = (searchUsers) users[position];

		viewHolder.nameTextView.setText(user.nickname);
		
		//viewHolder.iconImageView.setImageUrl(user.headimageurl, 1, true);
		
		if(user!=null && user.headimageurl!=null){	
			imageLoader.displayImage(user.headimageurl, viewHolder.iconImageView, options, animateFirstListener, ActiveAccount.getInstance(context).getUID(), 1);
			viewHolder.iconImageView.setTag(user.userid);
		}else{
			viewHolder.iconImageView.setImageResource(R.drawable.moren_touxiang);
		}
		
		if (null != user) {
			if (null != userID && userID.equals(user.userid)) {
				viewHolder.relationImageView.setBackgroundResource(R.color.transparent);
			
			} else {
				
				if ("1".equals(user.isattention)) {
					viewHolder.relationImageView.setBackgroundResource(R.drawable.btn_activity_friends_add_mutual_jian);
					
				} else {
					viewHolder.relationImageView.setBackgroundResource(R.drawable.btn_activity_friends_add_mutual_jia);
				}
				viewHolder.relationImageView.setTag(user);
				viewHolder.relationImageView.setOnClickListener(onClickListener);
//			if ("1".equals(user.isattention) && "1".equals(user.isattentionme)) {
//				viewHolder.relationImageView.setBackgroundDrawable(context
//						.getResources().getDrawable(
//								R.drawable.btn_activity_friends_add_mutual));
//			} else if ("1".equals(user.isattention)
//					&& "0".equals(user.isattentionme)) {
//				viewHolder.relationImageView
//						.setBackgroundDrawable(context
//								.getResources()
//								.getDrawable(
//										R.drawable.btn_activity_friends_add_mutual_jian));
//			} else {
//				viewHolder.relationImageView
//						.setBackgroundDrawable(context
//								.getResources()
//								.getDrawable(
//										R.drawable.btn_activity_friends_add_mutual_jia));
//
//				viewHolder.relationImageView.setTag(user);
//				viewHolder.relationImageView
//						.setOnClickListener(onClickListener);
//			}
				
			}
			
		}
		return convertView;
	}

	class ViewHolder {
		
		ImageView iconImageView;
		TextView nameTextView;
		ImageView relationImageView;
	}

	@Override
	public boolean handleMessage(Message msg) {

		switch (msg.what) {
		case Requester2.RESPONSE_TYPE_ATTENTION:

			if (msg.obj != null) {

				attentionResponse tagList = (attentionResponse) msg.obj;
				
				accountInfo = AccountInfo.getInstance(userID);
				ContactManager attentionContactManager = accountInfo.attentionContactManager;
				

				if ("0".equals(tagList.status)) {
					for (int i = 0; i < users.length; i++) {
						searchUsers user = users[i];
						if (tagList.attention_userid.equals(user.userid)) {
							user.isattention = "1";
							
							WrapUser wrapUser = new WrapUser();
							
							wrapUser.userid = user.userid; // 好友ID
							wrapUser.headimageurl = user.headimageurl; // "http://…jpg", 头像URL，可为空
							wrapUser.nickname = user.nickname; // 昵称
							wrapUser.diarycount = user.diarycount; // 日记数
							wrapUser.attentioncount = user.attentioncount; // 关注数
							wrapUser.fanscount = user.fanscount;
							wrapUser.sex = user.sex; // 0男 1女 2未知
							wrapUser.signature = user.signature; // "jdfdf"个人签名（base64编码）
							
							attentionContactManager.addMember(wrapUser); 
							break;
						}
					}
				} else if ("138117".equals(tagList.status)) {
					Prompt.Alert(context, "对方粉丝数达到上限");
				} else if ("138116".equals(tagList.status)) {
					Prompt.Alert(context, "关注数达到上限");
				}
				notifyDataSetChanged();
			}

			break;
		case Requester2.RESPONSE_TYPE_CANCEL_ATTENTION:

			if (msg.obj != null) {

				cancelattentionResponse response = (cancelattentionResponse) msg.obj;

				accountInfo = AccountInfo.getInstance(userID);
				ContactManager attentionContactManager = accountInfo.attentionContactManager;

				if ("0".equals(response.status)) {
					for (int i = 0; i < users.length; i++) {
						searchUsers user = users[i];
						if (response.targer_userid.equals(user.userid)) {
							user.isattention = "0";
							
							attentionContactManager.removeMember(user.userid);
							break;
						}
					}
				} else {

				}
				notifyDataSetChanged();
			}

			break;
		default:
			break;
		}
		return false;
	}

	OnClickListener onClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			
			searchUsers user = (searchUsers) v.getTag();

			if (null != user && "0".equals(user.isattention)) {
				Requester2.attention(handler, user.userid);
				
			} else if (null != user && "1".equals(user.isattention)) {
				Requester2.cancelAttention(handler, user.userid, "1");
			}
		}
	};

}
