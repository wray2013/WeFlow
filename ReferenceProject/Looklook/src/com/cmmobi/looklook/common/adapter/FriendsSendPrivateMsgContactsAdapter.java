package com.cmmobi.looklook.common.adapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.regex.Pattern;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.cmmobi.looklook.R;
import com.cmmobi.looklook.activity.PersonFansActivity;
import com.cmmobi.looklook.common.view.ContactsComparator;
import com.cmmobi.looklook.common.web.WebImageView;
import com.cmmobi.looklook.fragment.WrapSelectedUser;
import com.cmmobi.looklook.info.profile.ActiveAccount;
import com.nostra13.universalimageloader.api.AnimateFirstDisplayListener;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;
import com.nostra13.universalimageloader.core.display.CircularBitmapDisplayer;

public class FriendsSendPrivateMsgContactsAdapter extends BaseAdapter implements
		SectionIndexer {

	public static final String TAG = "FriendsSendPrivateMsgContactsAdapter";
	public static final String TAB_ATTENTION = "tab_attention";
	private ArrayList<WrapSelectedUser> wrapUsers = new ArrayList<WrapSelectedUser>();
	private LayoutInflater inflater;

	private Context context;

	private String type;
	private String preStr;// 列表最前面一栏不需要A-Z排序的名称
	protected ImageLoader imageLoader;
	private ImageLoadingListener animateFirstListener;
	private DisplayImageOptions options;

	public FriendsSendPrivateMsgContactsAdapter(Context context, String type) {
		this.context = context;
		this.inflater = LayoutInflater.from(context);
		this.type = type;
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

	public void setData(ArrayList<WrapSelectedUser> wrapUsers) {
		this.wrapUsers = wrapUsers;

		Collections.sort(this.wrapUsers, new ContactsComparator());
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

		ViewHolder viewHoder = null;
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.list_item_send_privatemsg_contacts, null);
			viewHoder = new ViewHolder();
			viewHoder.alphaTextView = (TextView) convertView
					.findViewById(R.id.alpha);
			viewHoder.contactsImageView = (ImageView) convertView
					.findViewById(R.id.contacts_image);
			viewHoder.contactsTextView = (TextView) convertView
					.findViewById(R.id.contacts_text);
			viewHoder.alphaImageView = (ImageView) convertView
					.findViewById(R.id.alpha_image);
			viewHoder.removeImageView = (ImageView) convertView
					.findViewById(R.id.remove_black_list);
			viewHoder.selectImgView = (ImageView) convertView
					.findViewById(R.id.iv_privatemsg_contacts_selected);

			convertView.setTag(viewHoder);
		} else {
			viewHoder = (ViewHolder) convertView.getTag();
		}

	    viewHoder.removeImageView.setVisibility(View.GONE);


		String currentStr = "";
		String previewStr = "";
		
		Log.d(TAG,"length = " + wrapUsers.size() + " position = " + position);
		viewHoder.contactsTextView
				.setText(wrapUsers.get(position).nickname);

		if (preStr != null && preStr.equals(wrapUsers.get(position).sortKey)) {
			currentStr = wrapUsers.get(position).sortKey;
			previewStr = (position - 1) >= 0 ? wrapUsers
					.get(position - 1).sortKey : " ";
		} else {
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
			viewHoder.alphaTextView.setVisibility(View.VISIBLE);
			viewHoder.alphaImageView.setVisibility(View.VISIBLE);
			viewHoder.alphaTextView.setText(currentStr);
		} else {
			viewHoder.alphaTextView.setVisibility(View.GONE);
			viewHoder.alphaImageView.setVisibility(View.GONE);
		}
		
		if (wrapUsers.get(position).isSelected) {
			viewHoder.selectImgView.setImageResource(R.drawable.xuanzhong_2);
		} else {
			viewHoder.selectImgView.setImageResource(R.drawable.xuanzhong_1);
		}
		
		WrapSelectedUser item = wrapUsers.get(position);
		if(item!=null && item.headimageurl!=null && item.headimageurl.length()>0){	
			imageLoader.displayImage(item.headimageurl, viewHoder.contactsImageView, options, animateFirstListener, ActiveAccount.getInstance(context).getUID(), 1);
		}else{
			viewHoder.contactsImageView.setImageResource(R.drawable.moren_touxiang);
		}

		return convertView;
	}
	
	public void setHeaderList(LinkedList<WrapSelectedUser> wrapUsers,String preStr) {
		this.preStr = preStr;
		this.wrapUsers.addAll(0, wrapUsers);
	}

	class ViewHolder {
		TextView alphaTextView;
		ImageView contactsImageView;
		TextView contactsTextView;
		ImageView alphaImageView;
		ImageView removeImageView;
		ImageView selectImgView;
	}
	
	public void setItemSelected(View view,int position) {
		ViewHolder holder = (ViewHolder)view.getTag();
		if (holder == null) {
			return;
		}
		if (wrapUsers.get(position).isSelected) {
			holder.selectImgView.setImageResource(R.drawable.xuanzhong_1);
			wrapUsers.get(position).isSelected = false;
		} else {
			holder.selectImgView.setImageResource(R.drawable.xuanzhong_2);
			wrapUsers.get(position).isSelected = true;
		}
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
