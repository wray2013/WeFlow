package com.cmmobi.looklook.common.adapter;

import java.util.ArrayList;
import java.util.Arrays;

import android.content.Context;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import cn.zipper.framwork.core.ZApplication;

import com.cmmobi.looklook.R;
import com.cmmobi.looklook.common.gson.GsonResponse2.attentionResponse;
import com.cmmobi.looklook.common.gson.GsonResponse2.cancelattentionResponse;
import com.cmmobi.looklook.common.gson.GsonResponse2;
import com.cmmobi.looklook.common.gson.Requester2;
import com.cmmobi.looklook.fragment.WrapRecommendUser;
import com.cmmobi.looklook.info.profile.AccountInfo;
import com.cmmobi.looklook.info.profile.ActiveAccount;
import com.cmmobi.looklook.info.profile.ContactManager;
import com.cmmobi.looklook.prompt.Prompt;
import com.nostra13.universalimageloader.api.AnimateFirstDisplayListener;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;
import com.nostra13.universalimageloader.core.display.CircularBitmapDisplayer;

public class FriendsAddAdapter extends BaseAdapter implements Callback {

	private LayoutInflater inflater;
	private Context context;
	private Handler handler;
	
	//使用开源的webimageloader
	private DisplayImageOptions options;
	protected ImageLoader imageLoader;
	private ImageLoadingListener animateFirstListener;

	//private ArrayList<WrapRecommendUser> wrapUsers = new ArrayList<WrapRecommendUser>();
	
	private ArrayList<GsonResponse2.UserSNS> users = new ArrayList<GsonResponse2.UserSNS>();
	private String userID;
	private ContactManager attentionContactManager;
	AccountInfo accountInfo;
	public FriendsAddAdapter(Context context) {
		this.context = context;
		this.inflater = LayoutInflater.from(context);
		handler = new Handler(this);
		
		animateFirstListener = new AnimateFirstDisplayListener();
		imageLoader = ImageLoader.getInstance();
		
		
		userID = ActiveAccount.getInstance(ZApplication.getInstance()).getUID();
		accountInfo = AccountInfo.getInstance(userID);
		attentionContactManager = accountInfo.attentionContactManager;
		
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
	}

/*	public void setData(ArrayList<WrapRecommendUser> wrapUserList) {
		this.wrapUsers = wrapUserList;
	}*/

	public void addData(GsonResponse2.UserSNS[] users){
		if(users != null){
			for(int i=0; i< users.length; i++){
				if(!this.users.contains(users[i]) && ! attentionContactManager.isMember(users[i].userid) && !userID.equals(users[i].userid)){
				this.users.add(users[i]);
				}
			}
		}
	}
	
	
	@Override
	public int getCount() {
		//return null == wrapUsers ? 0 : wrapUsers.size();
		return null == users ? 0 : users.size();
	}

	@Override
	public GsonResponse2.UserSNS getItem(int position) {
		return users.get(position);
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
		//WrapRecommendUser user = (WrapRecommendUser) wrapUsers.get(position);

		GsonResponse2.UserSNS user = users.get(position);
		
		viewHolder.nameTextView.setText(user.nickname);
		
		/*viewHolder.iconImageView.setImageUrl(user.headimageurl, 1, true);*/
		if(user!=null && user.headimageurl!=null){	
			imageLoader.displayImage(user.headimageurl, viewHolder.iconImageView, options, animateFirstListener, ActiveAccount.getInstance(context).getUID(), 1);
		}else{
			viewHolder.iconImageView.setImageResource(R.drawable.moren_touxiang);
		}
		
		if ("1".equals(user.isattention)) {
			viewHolder.relationImageView.setBackgroundDrawable(context
					.getResources().getDrawable(
							R.drawable.btn_activity_friends_add_mutual_jian));
		} else {
			viewHolder.relationImageView.setBackgroundDrawable(context
					.getResources().getDrawable(
							R.drawable.btn_activity_friends_add_mutual_jia));

		}
		viewHolder.relationImageView.setTag(user);
		viewHolder.relationImageView.setOnClickListener(onClickListener);
//		if ("1".equals(user.isattention) && "1".equals(user.isattentionme)) {
//			viewHolder.relationImageView.setBackgroundDrawable(context
//					.getResources().getDrawable(
//							R.drawable.btn_activity_friends_add_mutual));
//		} else if ("1".equals(user.isattention)
//				&& "0".equals(user.isattentionme)) {
//			viewHolder.relationImageView.setBackgroundDrawable(context
//					.getResources().getDrawable(
//							R.drawable.btn_activity_friends_add_mutual_jian));
//		} else {
//			viewHolder.relationImageView.setBackgroundDrawable(context
//					.getResources().getDrawable(
//							R.drawable.btn_activity_friends_add_mutual_jia));
//
//			viewHolder.relationImageView.setTag(user);
//			viewHolder.relationImageView.setOnClickListener(onClickListener);
//		}
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

				if ("0".equals(tagList.status)) {
					for (int i = 0; i < users.size(); i++) {
						GsonResponse2.UserSNS user = users.get(i);
						if (tagList.attention_userid.equals(user.userid)) {
//							user.isattention = "1";
							WrapRecommendUser wrapRecommendUser = new WrapRecommendUser();
							wrapRecommendUser.userid = user.userid;
							wrapRecommendUser.headimageurl = user.headimageurl;
							wrapRecommendUser.nickname = user.nickname;
							wrapRecommendUser.sex = user.sex;
							wrapRecommendUser.isattention = user.isattention;
							wrapRecommendUser.isattentionme = user.isattentionme;
							
							attentionContactManager.addMember(wrapRecommendUser);
							Prompt.Alert(context, "添加关注成功！");
							users.remove(i);
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

				if ("0".equals(response.status)) {
					for (int i = 0; i < users.size(); i++) {
						GsonResponse2.UserSNS user = users.get(i);
						if (response.targer_userid.equals(user.userid)) {
							user.isattention = "0";
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
			GsonResponse2.UserSNS user = (GsonResponse2.UserSNS) v.getTag();

			if (null != user && "0".equals(user.isattention)) {
				boolean isBlackList = accountInfo.blackListContactManager.isMember(user.userid);
				if(isBlackList){
					Prompt.Dialog(context, false, "提示", "请先解除黑名单再添加好友", null);
				}else{
					Requester2.attention(handler, user.userid);
				}
			} else if (null != user && "1".equals(user.isattention)) {
				Requester2.cancelAttention(handler, user.userid, "1");
			}
		}
	};

}
