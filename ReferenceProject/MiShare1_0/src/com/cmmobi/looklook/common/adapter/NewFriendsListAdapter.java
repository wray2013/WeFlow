package com.cmmobi.looklook.common.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.SectionIndexer;
import android.widget.TextView;
import cn.zipper.framwork.core.ZApplication;

import com.cmmobi.looklook.R;
import com.cmmobi.looklook.common.gson.GsonResponse3.NewFriends;
import com.cmmobi.looklook.info.profile.ActiveAccount;
import com.nostra13.universalimageloader.api.AnimateFirstDisplayListener;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;

public class NewFriendsListAdapter extends BaseAdapter implements
		SectionIndexer {

	private List<NewFriends> puserlist = new ArrayList<NewFriends>();
	private LayoutInflater inflater;

	private Context context;

	private String userID;
	
	//使用开源的webimageloader
	private DisplayImageOptions options;
	protected ImageLoader imageLoader;
	private ImageLoadingListener animateFirstListener;

	public NewFriendsListAdapter(Context context) {
		this.context = context;
		this.inflater = LayoutInflater.from(context);
		
		animateFirstListener = new AnimateFirstDisplayListener();
		imageLoader = ImageLoader.getInstance();
		
		userID = ActiveAccount.getInstance(ZApplication.getInstance()).getUID();
		
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
		return puserlist.size();
	}
	
	public void setData(List<NewFriends> plist) {
		if(plist == null){
			return;
		}
		this.puserlist.clear();
		this.puserlist.addAll(plist);	
	}

	@Override
	public Object getItem(int position) {
		return position;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	public void updateUI(String userid) {
		
		
	}


	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		ViewHolder viewHolder = null;
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.newfriends_item, null);
			viewHolder = new ViewHolder();
			viewHolder.contactsNickname = (TextView) convertView
					.findViewById(R.id.contacts_nickname);
			viewHolder.contactsImageView = (ImageView) convertView
					.findViewById(R.id.contacts_image);
			viewHolder.friendMessage = (TextView) convertView
					.findViewById(R.id.tv_friends_message);
			viewHolder.contactName = (TextView) convertView
					.findViewById(R.id.tv_contact_name);
			viewHolder.userStatus = (TextView) convertView.findViewById(R.id.tv_userstatus);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		viewHolder.contactsImageView.setTag(puserlist.get(position).userid);
		viewHolder.friendMessage.setText(puserlist.get(position).requestmsg);
		viewHolder.contactsNickname.setText(puserlist.get(position).nickname);
		viewHolder.contactName.setText(puserlist.get(position).requestmsg);
		
		String request_status=puserlist.get(position).request_status;
		String request_str = "";
		if("1".equals(request_status)) {
			request_str = "添加";
		} else if("2".equals(request_status)) {
			request_str = "等待验证";
		} else if("3".equals(request_status)) {
			request_str = "接受";
		} else {
			request_str = "已添加";
		}
		viewHolder.userStatus.setText(request_str);
		

		if(puserlist.get(position)!=null && puserlist.get(position).headimageurl!=null){	
			imageLoader.displayImageEx(puserlist.get(position).headimageurl, viewHolder.contactsImageView, options, animateFirstListener, ActiveAccount.getInstance(context).getUID(), 1);
		}else{
			viewHolder.contactsImageView.setImageResource(R.drawable.moren_touxiang);
		}

		return convertView;
	}

	class ViewHolder {

		ImageView contactsImageView;
		TextView contactsNickname;
		TextView friendMessage;
		TextView contactName;
		TextView userStatus;
	}

	@Override
	public Object[] getSections() {
		return null;
	}

	@Override
	public int getPositionForSection(int section) {
		return -1;
	}

	@Override
	public int getSectionForPosition(int position) {
		return 0;
	}
}
