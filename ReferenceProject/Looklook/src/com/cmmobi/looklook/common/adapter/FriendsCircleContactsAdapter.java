package com.cmmobi.looklook.common.adapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.regex.Pattern;

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
import android.widget.SectionIndexer;
import android.widget.TextView;
import cn.zipper.framwork.core.ZApplication;

import com.cmmobi.looklook.R;
import com.cmmobi.looklook.activity.HomepageOtherDiaryActivity;
import com.cmmobi.looklook.common.gson.GsonResponse2;
import com.cmmobi.looklook.common.gson.Requester2;
import com.cmmobi.looklook.common.view.ContactsComparator;
import com.cmmobi.looklook.fragment.WrapUser;
import com.cmmobi.looklook.info.profile.AccountInfo;
import com.cmmobi.looklook.info.profile.ActiveAccount;
import com.cmmobi.looklook.info.profile.ContactManager;
import com.cmmobi.looklook.info.profile.FriendsBlackListManager;
import com.cmmobi.looklook.offlinetask.OfflineTaskManager;
import com.cmmobi.looklook.prompt.Prompt;
import com.nostra13.universalimageloader.api.AnimateFirstDisplayListener;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;
import com.nostra13.universalimageloader.core.display.CircularBitmapDisplayer;

public class FriendsCircleContactsAdapter extends BaseAdapter implements
		SectionIndexer, Callback {

	public static final String TAB_FUNS = "tab_funs";
	public static final String TAB_ATTENTION = "tab_attention";
	public static final String TAB_BLACK_LIST = "tab_black_list";
	private ArrayList<WrapUser> wrapUsers = new ArrayList<WrapUser>();
	private LayoutInflater inflater;

	private Context context;

	private String type;

	private ContactsComparator cmp;

	private Handler handler;
	private FriendsBlackListManager friendsBlackListManager;
	private AccountInfo accountInfo;
	private String userID;
	
	//使用开源的webimageloader
	private DisplayImageOptions options;
	protected ImageLoader imageLoader;
	private ImageLoadingListener animateFirstListener;

	public FriendsCircleContactsAdapter(Context context, String type) {
		this.context = context;
		this.inflater = LayoutInflater.from(context);
		this.handler = new Handler(this);
		this.type = type;
		cmp = new ContactsComparator();
		
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
	}

	@Override
	public int getCount() {
		return wrapUsers.size();
	}

	public void setData(ArrayList<WrapUser> wrapUsers) {
		if(wrapUsers == null || wrapUsers.isEmpty()){
			return;
		}
		/*for(int i=0; i<wrapUsers.size(); i++){
			if(!this.wrapUsers.contains(wrapUsers.get(i))){
				this.wrapUsers.add(wrapUsers.get(i));
			}
		}*/
		this.wrapUsers = wrapUsers;
		Collections.sort(this.wrapUsers, cmp);
		int size = this.wrapUsers.size();
		for(int i=0; i< size; i++){
			if(this.wrapUsers.lastIndexOf(this.wrapUsers.get(i)) != i){
				this.wrapUsers.remove(i);
				size --;
				i--;
			}
		}
	
	}

	public void setData(ArrayList<WrapUser> wrapUsers, String system) {
		if(wrapUsers == null || wrapUsers.isEmpty()){
			return;
		}/*
		for(int i=0; i<wrapUsers.size(); i++){
			if(!this.wrapUsers.contains(wrapUsers.get(i))){
				this.wrapUsers.add(wrapUsers.get(i));
			}
		}*/
		this.wrapUsers = wrapUsers;
		Collections.sort(this.wrapUsers, cmp);
		int size = this.wrapUsers.size();
		for(int i=0; i< size; i++){
			if(this.wrapUsers.lastIndexOf(this.wrapUsers.get(i)) != i){
				this.wrapUsers.remove(i);
				size --;
				i--;
			}
		}
//		WrapUser wrapUser = new WrapUser();
//		wrapUser.nickname = system;
//		wrapUsers.add(0, wrapUser);
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
		
		for (int i = 0; i < wrapUsers.size(); i++) {
			if (null == userid) {
				break;
			}
			if (userid.equals(wrapUsers.get(i).userid)) {
				
				userID = ActiveAccount.getInstance(ZApplication.getInstance()).getUID();
				accountInfo = AccountInfo.getInstance(userID);
				ContactManager blacklistContactManager = accountInfo.blackListContactManager;
				ContactManager attentionContactManager = accountInfo.attentionContactManager;
				
//				attentionContactManager.addMember(wrapUsers.get(i));
				blacklistContactManager.removeMember(wrapUsers.get(i).userid);
				wrapUsers.remove(i);
//				friendsBlackListManager = accountInfo.friendsBlackListManager;
//				friendsBlackListManager.refreshCache(wrapUsers);
				this.notifyDataSetChanged();
//				dataChange.dataChaged();
			}
		}
	}
	@Override
	public boolean handleMessage(Message msg) {

		switch (msg.what) {
		case Requester2.RESPONSE_TYPE_SET_BLACKLIST:
			GsonResponse2.operateblacklistResponse blacklistResponse = (GsonResponse2.operateblacklistResponse) msg.obj;
			if (blacklistResponse != null
					&& "0".equals(blacklistResponse.status)) {

				Prompt.Alert(context, "移出黑名单成功！");
				
			} else {
				
				Prompt.Alert(context, "移出黑名单失败！");
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
			convertView = inflater.inflate(R.layout.list_item_contacts, null);
			viewHolder = new ViewHolder();
			viewHolder.alphaTextView = (TextView) convertView
					.findViewById(R.id.alpha);
			viewHolder.contactsImageView = (ImageView) convertView
					.findViewById(R.id.contacts_image);
			viewHolder.contactsTextView = (TextView) convertView
					.findViewById(R.id.contacts_text);
			viewHolder.alphaImageView = (ImageView) convertView
					.findViewById(R.id.alpha_image);
			viewHolder.removeImageView = (ImageView) convertView
					.findViewById(R.id.remove_black_list);

			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		viewHolder.contactsImageView.setTag(wrapUsers.get(position).userid);
		viewHolder.contactsImageView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				if (null != v.getTag()) {
					Intent intent = new Intent(context,
							HomepageOtherDiaryActivity.class);
					intent.putExtra("userid", v.getTag() + "");
					context.startActivity(intent);
				}
			}
		});
		
		if (TAB_BLACK_LIST.equalsIgnoreCase(type)) {
			viewHolder.removeImageView.setVisibility(View.VISIBLE);

			viewHolder.removeImageView.setTag(wrapUsers.get(position).userid);
			viewHolder.removeImageView
					.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
//							Requester2.operateBlacklist(handler,
//									"" + v.getTag(), "2");
							OfflineTaskManager.getInstance().addBlackRemoveTask(v.getTag() + "", "2");
							updateUI(v.getTag() +"");
						}
					});
		} else {
			viewHolder.removeImageView.setVisibility(View.GONE);

		}

		String currentStr = "";
		String previewStr = "";
		if (TAB_ATTENTION.equalsIgnoreCase(type)) {
//			if (0 == position) {
//
//				viewHolder.contactsTextView.setText("系统功能");
//				viewHolder.contactsImageView
//						.setImageResource(R.drawable.system_setting);
//			} 
//			else
			
//			{

				viewHolder.contactsTextView
						.setText(wrapUsers.get(position).nickname);

				// viewHoder.contactsImageView
				// .setImageResource(R.drawable.temp_local_icon);
				// viewHoder.contactsImageView.setImageUrl(
				// R.drawable.moren_touxiang_2, 1,
				// wrapUsers.get(position).headimageurl);
				
				//viewHolder.contactsImageView.setImageUrl(wrapUsers.get(position).headimageurl, 1, true);
				if(wrapUsers.get(position)!=null && wrapUsers.get(position).headimageurl!=null){	
					imageLoader.displayImage(wrapUsers.get(position).headimageurl, viewHolder.contactsImageView, options, animateFirstListener, ActiveAccount.getInstance(context).getUID(), 1);
				}else{
					viewHolder.contactsImageView.setImageResource(R.drawable.moren_touxiang);
				}
				
				// 当前联系人的sortKey
				currentStr = getAlpha(wrapUsers.get(position).sortKey);
				// 上一个联系人的sortKey
				previewStr = (position - 1) >= 0 ? getAlpha(wrapUsers
						.get(position - 1).sortKey) : " ";
//			}

		} else {
			viewHolder.contactsTextView
					.setText(wrapUsers.get(position).nickname);
			//viewHolder.contactsImageView.setImageUrl(wrapUsers.get(position).headimageurl, 1, true);
			if(wrapUsers.get(position)!=null && wrapUsers.get(position).headimageurl!=null){	
				imageLoader.displayImage(wrapUsers.get(position).headimageurl, viewHolder.contactsImageView, options, animateFirstListener, ActiveAccount.getInstance(context).getUID(), 1);
			}else{
				viewHolder.contactsImageView.setImageResource(R.drawable.moren_touxiang);
			}
			
			// 当前联系人的sortKey
			currentStr = getAlpha(wrapUsers.get(position).sortKey);
			// 上一个联系人的sortKey
			previewStr = (position - 1) >= 0 ? getAlpha(wrapUsers
					.get(position - 1).sortKey) : " ";
		}

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

		return convertView;
	}

	class ViewHolder {

		TextView alphaTextView;
		ImageView contactsImageView;
		TextView contactsTextView;
		ImageView alphaImageView;

		ImageView removeImageView;
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
