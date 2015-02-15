package com.cmmobi.looklook.common.adapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.cmmobi.looklook.R;
import com.cmmobi.looklook.activity.TencentFriendWeiboActivity;
import com.cmmobi.looklook.common.adapter.FriendsSendPrivateMsgContactsAdapter.ViewHolder;
import com.cmmobi.looklook.common.view.TencentComparator;
import com.cmmobi.looklook.fragment.WrapTencentUser;
import com.cmmobi.looklook.info.profile.ActiveAccount;
import com.nostra13.universalimageloader.api.AnimateFirstDisplayListener;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;

public class FriendsTencentAdapter extends BaseAdapter implements SectionIndexer {
	private static final String TAG = "FriendsTencentAdapter";

	private Context context;
	private Handler handler;
	
	private List<String>  to_invite;

	private LayoutInflater inflater;

	private ArrayList<WrapTencentUser> data;
	
	//使用开源的webimageloader
	private DisplayImageOptions options;
	protected ImageLoader imageLoader;
	private ImageLoadingListener animateFirstListener;

	private ArrayList<Boolean> list_selected;

	public FriendsTencentAdapter(Context context, Handler handler, List<String> list_to_invite) {
		this.context = context;
		this.handler = handler;
		this.to_invite = list_to_invite;
		this.list_selected = new ArrayList<Boolean>();

		this.inflater = LayoutInflater.from(context);
		/*ImageLoader.initialize(context, null);*/
		
		animateFirstListener = new AnimateFirstDisplayListener();
		imageLoader = ImageLoader.getInstance();
		
		options = new DisplayImageOptions.Builder()
		.showStubImage(R.drawable.zone_1)
		.showImageForEmptyUri(R.drawable.zone_1)
		.showImageOnFail(R.drawable.zone_1)
		.cacheInMemory(true)
		.cacheOnDisc(true)
		.displayer(new SimpleBitmapDisplayer())
		//.displayer(new CircularBitmapDisplayer()) 圆形图片
		//.displayer(new RoundedBitmapDisplayer(20)) 圆角图片
		.build();
	}

	@Override
	public int getCount() {
		return null != data ? data.size() : 0;
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
	public View getView(final int position, View convertView, ViewGroup parent) {

		final ViewHolder viewHolder;
		if (convertView == null) {
			convertView = inflater
					.inflate(R.layout.list_item_friends_sns, null);
			viewHolder = new ViewHolder();
			viewHolder.alphaTextView = (TextView) convertView
					.findViewById(R.id.alpha);
			viewHolder.contactsImageView = (ImageView) convertView
					.findViewById(R.id.contacts_image);
			viewHolder.contactsTextView = (TextView) convertView
					.findViewById(R.id.contacts_text);
			viewHolder.alphaImageView = (ImageView) convertView
					.findViewById(R.id.alpha_image);
			viewHolder.ck_row_list_friend_choose = (ImageView) convertView.findViewById(R.id.ck_row_list_friend_choose);

			viewHolder.rl_all = (RelativeLayout) convertView.findViewById(R.id.rl_all);


		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		
		final WrapTencentUser item = data.get(position);
		
		if(item==null){
			return null;
		}
		
		String currentStr = "";
		String previewStr = "";
		// 当前联系人的sortKey
		currentStr = getAlpha(item.sortKey);
		// 上一个联系人的sortKey
		previewStr = (position - 1) >= 0 ? getAlpha(data.get(position - 1).sortKey) : " ";
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
		//viewHolder.contactsTextView.setText(item.name);
	    viewHolder.contactsTextView.setText(data.get(position).nick);
		
/*		if(item.headurl!=null  && item.headurl.length()!=0){
			viewHolder.contactsImageView.setImageUrl(R.drawable.zone_1, 1, item.headurl + "/50", false);
		}else{
			viewHolder.contactsImageView.setImageResource(R.drawable.zone_1);
		}*/
	    
	    if(item.headurl!=null  && item.headurl.length()!=0){
	    	imageLoader.displayImage(item.headurl  + "/50", viewHolder.contactsImageView, options, animateFirstListener, ActiveAccount.getInstance(context).getUID(), 1);
		}else{
			viewHolder.contactsImageView.setImageResource(R.drawable.zone_1);
		}
	    
		if(list_selected.get(position)){
		    viewHolder.ck_row_list_friend_choose.setImageResource(R.drawable.xuanzhong_2); //checked
		}else{
		    viewHolder.ck_row_list_friend_choose.setImageResource(R.drawable.xuanzhong_1); //normal
		}

		
/*		viewHolder.rl_all.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub 
				if(list_selected.get(position)){
					list_selected.set(position, false); //checked -> normal
				}else{
					list_selected.set(position, true); // normal -> checked
				}
				String state = (String) viewHolder.ck_row_list_friend_choose.getTag();
				if("checked".equals(state)){
					to_invite.remove(item.name) ;
					if(to_invite.size()==0){
						Message msg = handler.obtainMessage(TencentFriendWeiboActivity.HANDLER_FLAG_DISABLE_INVITE);
						msg.sendToTarget();
					}
					viewHolder.ck_row_list_friend_choose.setTag("normal");
					viewHolder.ck_row_list_friend_choose.setImageResource(R.drawable.xuanzhong_1);
				}else{
					to_invite.add(item.name);
					if(to_invite.size()==1){
						Message msg = handler.obtainMessage(TencentFriendWeiboActivity.HANDLER_FLAG_ENABLE_INVITE);
						msg.sendToTarget();
					}
					viewHolder.ck_row_list_friend_choose.setTag("checked");
					viewHolder.ck_row_list_friend_choose.setImageResource(R.drawable.xuanzhong_2);
				}
				 
			}
		});*/
		
		
		convertView.setTag(viewHolder);
		return convertView;
	}
	
	public void setItemSelected(View view,int position) {
		ViewHolder viewHolder = (ViewHolder)view.getTag();
		final WrapTencentUser item = data.get(position);
		if (viewHolder == null || item==null || item.name==null) {
			return;
		}
		if(list_selected.get(position)){
			list_selected.set(position, false); //checked -> normal
			viewHolder.ck_row_list_friend_choose.setImageResource(R.drawable.xuanzhong_1);

		}else{
			list_selected.set(position, true); // normal -> checked
			viewHolder.ck_row_list_friend_choose.setImageResource(R.drawable.xuanzhong_2);

		}
		
		to_invite.clear();
		for(int i=0; i< list_selected.size(); i++){
			if(list_selected.get(i)){
				to_invite.add(data.get(i).name);
			}
		}

		if(to_invite.size()<1){
			Message msg = handler.obtainMessage(TencentFriendWeiboActivity.HANDLER_FLAG_DISABLE_INVITE);
			msg.sendToTarget();
		}else{
			Message msg = handler.obtainMessage(TencentFriendWeiboActivity.HANDLER_FLAG_ENABLE_INVITE);
			msg.sendToTarget();
		}
	}

	class ViewHolder {
		TextView alphaTextView;
		ImageView contactsImageView;
		TextView contactsTextView;
		ImageView alphaImageView;
		ImageView ck_row_list_friend_choose;
		RelativeLayout rl_all;
	}

	public void setData(ArrayList<WrapTencentUser> list_friend_data) {

		this.data = list_friend_data;
		
		this.list_selected.clear();
		for(int i=0; i< list_friend_data.size();  i++){
			this.list_selected.add(false);
		}
		
		Collections.sort(this.data, new TencentComparator());

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
		for (int i = 0; i < data.size(); i++) {
			char key = getAlpha(data.get(i).sortKey).charAt(0);
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
