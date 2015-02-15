package com.cmmobi.looklook.common.adapter;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SectionIndexer;
import android.widget.TextView;
import cn.zipper.framwork.core.ZApplication;

import com.baidu.platform.comapi.map.r;
import com.cmmobi.looklook.R;
import com.cmmobi.looklook.activity.FriendsSessionPrivateMessageActivity;
import com.cmmobi.looklook.common.utils.DensityUtil;
import com.cmmobi.looklook.common.view.FriendsExpressionView;
import com.cmmobi.looklook.info.profile.ActiveAccount;
import com.cmmobi.looklook.info.profile.WrapUser;
import com.nostra13.universalimageloader.api.AnimateFirstDisplayListener;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;

public class ContactsSortAdapter extends BaseAdapter implements
		SectionIndexer{
	private List<WrapUser> wrapUsers = new ArrayList<WrapUser>();
	private LayoutInflater inflater;

	private Context context;	
	//使用开源的webimageloader
	private DisplayImageOptions options;
	protected ImageLoader imageLoader;
	private ImageLoadingListener animateFirstListener;
	
    private String searchString = "";

	public ContactsSortAdapter(Context context) {
		this.context = context;
		this.inflater = LayoutInflater.from(context);
		
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

	public void setSearchString(String s){
		searchString = s.toLowerCase();
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
	public View getView(int position, View convertView, ViewGroup parent) {

		ViewHolder viewHolder = null;
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.list_item_contacts_sort, null);
			viewHolder = new ViewHolder();
			viewHolder.alphaTextView = (TextView) convertView
					.findViewById(R.id.alpha);
			viewHolder.contactsImageView = (ImageView) convertView
					.findViewById(R.id.contacts_image);
			viewHolder.contactsTextView = (TextView) convertView
					.findViewById(R.id.contacts_text);
			viewHolder.alphaImageView = (ImageView) convertView
					.findViewById(R.id.alpha_image);
			viewHolder.sendmessageTextView = (TextView) convertView.findViewById(R.id.tv_sendmessage);
			viewHolder.nicknameTextView = (TextView) convertView.findViewById(R.id.tv_nickname);
			viewHolder.lastlineImageView = (ImageView) convertView.findViewById(R.id.iv_lastline);
			viewHolder.rl_contact = (RelativeLayout) convertView.findViewById(R.id.rl_contact);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		

		viewHolder.sendmessageTextView.setTag(wrapUsers.get(position));
		viewHolder.sendmessageTextView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				WrapUser user = (WrapUser) v.getTag();
				Intent intent = new Intent(context,
						FriendsSessionPrivateMessageActivity.class);
				intent.putExtra("wrapuser", user.toString());// 压入数据
				context.startActivity(intent);
			}
		});
		
		

		if(wrapUsers.get(position).nickname !=null && ! wrapUsers.get(position).nickname.isEmpty()){
			if(wrapUsers.get(position).markname != null && !wrapUsers.get(position).markname.equals("")){
				if(searchString != null && !searchString.isEmpty()){
					if(wrapUsers.get(position).nickname.toLowerCase().contains(searchString)){
						/*viewHolder.contactsTextView
						.setText(wrapUsers.get(position).nickname);
						viewHolder.nicknameTextView.setText("备注名:" + wrapUsers.get(position).markname);*/
						FriendsExpressionView.replacedExpressions(wrapUsers.get(position).nickname, viewHolder.contactsTextView);
						FriendsExpressionView.replacedExpressions("备注名:" + wrapUsers.get(position).markname, viewHolder.nicknameTextView);
					}else{
						/*viewHolder.contactsTextView
						.setText(wrapUsers.get(position).markname);
						viewHolder.nicknameTextView.setText("昵称:" + wrapUsers.get(position).nickname);
						*/
						FriendsExpressionView.replacedExpressions(wrapUsers.get(position).markname, viewHolder.contactsTextView);
						FriendsExpressionView.replacedExpressions("昵称:" + wrapUsers.get(position).nickname, viewHolder.nicknameTextView);
					}
				}else{
					/*viewHolder.contactsTextView
					.setText(wrapUsers.get(position).markname);
					viewHolder.nicknameTextView.setText("昵称:" + wrapUsers.get(position).nickname);*/
					FriendsExpressionView.replacedExpressions(wrapUsers.get(position).markname, viewHolder.contactsTextView);
					FriendsExpressionView.replacedExpressions("昵称:" + wrapUsers.get(position).nickname, viewHolder.nicknameTextView);
				}
			}else{
				/*viewHolder.contactsTextView
				.setText(wrapUsers.get(position).nickname);*/
				viewHolder.nicknameTextView.setText("");
				FriendsExpressionView.replacedExpressions(wrapUsers.get(position).nickname, viewHolder.contactsTextView);
			}
		}else{
			if(wrapUsers.get(position).markname != null && !wrapUsers.get(position).markname.equals("")){
				FriendsExpressionView.replacedExpressions(wrapUsers.get(position).markname, viewHolder.contactsTextView);
				viewHolder.nicknameTextView.setText("");
			}else if(wrapUsers.get(position).telname !=null && !wrapUsers.get(position).telname.isEmpty()){
				viewHolder.nicknameTextView.setText("");
				viewHolder.contactsTextView.setText(wrapUsers.get(position).telname);
			}else{
				viewHolder.nicknameTextView.setText("");
				viewHolder.contactsTextView.setText(wrapUsers.get(position).micnum);
			}
		}

		if(wrapUsers.get(position)!=null && wrapUsers.get(position).headimageurl!=null){	
			imageLoader.displayImageEx(wrapUsers.get(position).headimageurl, viewHolder.contactsImageView, options, animateFirstListener, ActiveAccount.getInstance(context).getUID(), 1);
		}else{
			viewHolder.contactsImageView.setImageResource(R.drawable.moren_touxiang);
		}
		
		if(searchString !=null && !searchString.isEmpty()){
			viewHolder.alphaTextView.setVisibility(View.GONE);
			viewHolder.alphaImageView.setVisibility(View.GONE);
		}else{
			String currentStr = "";
			String previewStr = "";
			String nextStr = "";
			// 当前联系人的sortKey
			currentStr = getAlpha(wrapUsers.get(position).sortKey);
			// 上一个联系人的sortKey
			previewStr = (position - 1) >= 0 ? getAlpha(wrapUsers
					.get(position - 1).sortKey) : " ";
			nextStr = (position + 1) < getCount() ? getAlpha(wrapUsers
					.get(position + 1).sortKey) : " ";
		
	
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
			
			RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams)viewHolder.lastlineImageView.getLayoutParams();
			
			if(currentStr.equals(nextStr)){
				lp.setMargins(DensityUtil.dip2px(context, 75), 0, 0, 0);
				viewHolder.lastlineImageView.setLayoutParams(lp);
			}else{
				lp.setMargins(0, 0, 0, 0);
				viewHolder.lastlineImageView.setLayoutParams(lp);
			}
		}
		return convertView;
	}

	public class ViewHolder {

		TextView alphaTextView;
		ImageView contactsImageView;
		TextView contactsTextView;
		ImageView alphaImageView;
		TextView nicknameTextView;
		TextView sendmessageTextView;
		ImageView lastlineImageView;
		public RelativeLayout rl_contact;
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
