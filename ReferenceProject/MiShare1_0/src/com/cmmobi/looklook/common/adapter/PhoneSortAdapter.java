package com.cmmobi.looklook.common.adapter;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import android.app.Activity;
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
import com.cmmobi.looklook.activity.FriendAddPhoneActivity;
import com.cmmobi.looklook.common.adapter.ShareLookLookFriendsAdapter.ViewHolder;
import com.cmmobi.looklook.common.gson.GsonResponse3.UserObj;
import com.cmmobi.looklook.common.view.Xdialog;
import com.cmmobi.looklook.info.profile.AccountInfo;
import com.cmmobi.looklook.info.profile.ActiveAccount;
import com.cmmobi.looklook.info.profile.WrapUser;
import com.nostra13.universalimageloader.api.AnimateFirstDisplayListener;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;

public class PhoneSortAdapter extends BaseAdapter implements
		SectionIndexer {

	private List<WrapUser> wrapUsers = new ArrayList<WrapUser>();
	private LayoutInflater inflater;

	private FriendAddPhoneActivity context;

	private AccountInfo accountInfo;
	private String userID;
	
	private List<Boolean> list_selected = new ArrayList<Boolean>();
	public List<WrapUser>  to_invite = new ArrayList<WrapUser>();
	
	private Boolean isShowLetter;

	public PhoneSortAdapter(FriendAddPhoneActivity context, Boolean isShowLetter) {
		this.context = context;
		this.inflater = LayoutInflater.from(context);
		userID = ActiveAccount.getInstance(ZApplication.getInstance()).getUID();
		accountInfo = AccountInfo.getInstance(userID);
		this.isShowLetter = isShowLetter;
	}

	@Override
	public int getCount() {
		return wrapUsers.size();
	}
	public void setData(List<WrapUser> wrapUsers, List<WrapUser> selected) {
		
		if(wrapUsers == null){
			return;
		}
		this.wrapUsers.clear();
		this.wrapUsers.addAll(wrapUsers);	
		this.to_invite.clear();
		this.list_selected.clear();
		
		for(int i=0; i< this.wrapUsers.size(); i++){
			boolean flag = false;
			for(WrapUser str : selected)
			{
				if(str.equals(wrapUsers.get(i)))
				{
					flag = true;
					if(!to_invite.contains(str)){
						this.to_invite.add(str);
					}
					break;
				}
			}
			this.list_selected.add(flag);
		}
	}

	@Override
	public WrapUser getItem(int position) {
		return wrapUsers.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		ViewHolder viewHolder = null;
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.list_item_phone_sort, null);
			viewHolder = new ViewHolder();
			viewHolder.alphaTextView = (TextView) convertView
					.findViewById(R.id.alpha);
			viewHolder.contactsTextView = (TextView) convertView
					.findViewById(R.id.contacts_text);
			viewHolder.alphaImageView = (ImageView) convertView
					.findViewById(R.id.alpha_image);
			viewHolder.inviteImageView = (ImageView) convertView
					.findViewById(R.id.iv_add_cancel);
			viewHolder.numTextView = (TextView) convertView.findViewById(R.id.tv_num);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		Boolean isInvite = false;
		for(int i=0; i< context.inviteUsers.size(); i++){
			if(wrapUsers.get(position).phonenum.equals(context.inviteUsers.get(i).phonenum)){
				isInvite = true;
				break;
			}
		}
		if(isInvite){
			viewHolder.inviteImageView.setImageResource(R.drawable.xuanzhong_invite_2);
		}else{
			viewHolder.inviteImageView.setImageResource(R.drawable.xuanzhong_invite_1);
		}
		
		viewHolder.contactsTextView.setText(wrapUsers.get(position).phonename);
		viewHolder.numTextView.setText(wrapUsers.get(position).phonenum);
		
		if(isShowLetter){

			String currentStr = "";
			String previewStr = "";
			
			// 当前联系人的sortKey
			currentStr = getAlpha(wrapUsers.get(position).sortKey);
			// 上一个联系人的sortKey
			previewStr = (position - 1) >= 0 ? getAlpha(wrapUsers
					.get(position - 1).sortKey) : " ";
		

			/**
			 * 判断显示#、A-Z的TextView隐藏与可见
			 */
			if (!previewStr.equals(currentStr)) { // 当前联系人的sortKey！=上一个联系人的sortKey，说明当前联系人是新组。
				viewHolder.alphaTextView.setVisibility(View.VISIBLE);
				viewHolder.alphaImageView.setVisibility(View.VISIBLE);
				viewHolder.alphaTextView.setText(currentStr);
			} else {
				viewHolder.alphaTextView.setVisibility(View.GONE);
				viewHolder.alphaImageView.setVisibility(View.GONE);
			}
		}else{
			viewHolder.alphaTextView.setVisibility(View.GONE);
			viewHolder.alphaImageView.setVisibility(View.GONE);
		}
		

		return convertView;
	}
	
	
	public void setItemSelected(View view,int position) {
		ViewHolder viewHolder = (ViewHolder)view.getTag();
		final WrapUser item = wrapUsers.get(position);
		if (viewHolder == null || item==null || (item.nickname==null && item.markname == null && item.phonename == null)) {
			return;
		}
		if(list_selected.get(position)){
			list_selected.set(position, false); //checked -> normal
			viewHolder.inviteImageView.setImageResource(R.drawable.xuanzhong_invite_1);
			for(int i=0; i< wrapUsers.size(); i++){
				if(item.equals(wrapUsers.get(i))){
					list_selected.set(i, false); 
				}
			}
		}else{
			if(to_invite.size() < 8)
			{
				list_selected.set(position, true); // normal -> checked
				viewHolder.inviteImageView.setImageResource(R.drawable.xuanzhong_invite_2);
				for(int i=0; i< wrapUsers.size(); i++){
					if(item.equals(wrapUsers.get(i))){
						list_selected.set(i, true); 
					}
				}
			}
			else
			{
				new Xdialog.Builder(context)
				.setMessage(context.getString(R.string.share_error_user_max))
				.setNegativeButton(android.R.string.ok, null)
				.create().show();
			}
		}
		
		to_invite.clear();
		for(int i=0; i< list_selected.size(); i++){
			if(list_selected.get(i) && !to_invite.contains(wrapUsers.get(i))){
				to_invite.add(wrapUsers.get(i));
			}
		}

	}

	public class ViewHolder {
		public TextView alphaTextView;
		public TextView contactsTextView;
		public ImageView alphaImageView;
		public ImageView inviteImageView;
		public TextView numTextView;
	}

		
	/**
	 * 提取英文的首字母，非英文字母用#代替。
	 * 
	 * @param str
	 * @return
	 */
	private String getAlpha(String str) {
		if (str == null) {
			return "#";
		}

		if (str.trim().length() == 0) {
			return "#";
		}

		char c = str.trim().substring(0, 1).charAt(0);
		// 正则表达式，判断首字母是否是英文字母
		Pattern pattern = Pattern.compile("^[A-Za-z]+$");
		if (pattern.matcher(c + "").matches()) {
			return (c + "").toUpperCase(); // 大写输出
		} else {
			return "#";
		}
	}

	@Override
	public Object[] getSections() {
		return null;
	}

	@Override
	public int getPositionForSection(int section) {
		for (int i = 0; i < wrapUsers.size(); i++) {
			char key = getAlpha(wrapUsers.get(i).sortKey).charAt(0);
			if (key == section) {
				return i;
			}
		}
		return -1;
	}

	@Override
	public int getSectionForPosition(int position) {
		return 0;
	}
}
