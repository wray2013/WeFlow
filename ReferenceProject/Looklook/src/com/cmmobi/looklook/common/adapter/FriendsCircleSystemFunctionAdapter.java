package com.cmmobi.looklook.common.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.cmmobi.looklook.R;
import com.cmmobi.looklook.common.gson.GsonResponse2.MyUserids;
import com.cmmobi.looklook.info.profile.ActiveAccount;
import com.nostra13.universalimageloader.api.AnimateFirstDisplayListener;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;
import com.nostra13.universalimageloader.core.display.CircularBitmapDisplayer;

public class FriendsCircleSystemFunctionAdapter extends BaseAdapter {
	private LayoutInflater inflater;
	private ArrayList<MyUserids> userids;
	
	//使用开源的webimageloader
	private DisplayImageOptions options;
	protected ImageLoader imageLoader;
	private ImageLoadingListener animateFirstListener;
	private Context context;

	public FriendsCircleSystemFunctionAdapter(Context context) {
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
		//.displayer(new SimpleBitmapDisplayer())
		.displayer(new CircularBitmapDisplayer()) //圆形图片
		//.displayer(new RoundedBitmapDisplayer(20)) 圆角图片
		.build();
	}

	@Override
	public int getCount() {
		return userids == null ? 0 : userids.size();
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
			convertView = inflater.inflate(R.layout.list_item_contacts, null);
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
			viewHoder.contactsArrow = (ImageView) convertView
					.findViewById(R.id.contacts_arrow);

			convertView.setTag(viewHoder);
		} else {
			viewHoder = (ViewHolder) convertView.getTag();
		}

		viewHoder.removeImageView.setVisibility(View.GONE);
		viewHoder.contactsArrow.setVisibility(View.VISIBLE);
		viewHoder.alphaTextView.setVisibility(View.GONE);

		viewHoder.contactsTextView.setText(userids.get(position).nickname);
		
		//viewHoder.contactsImageView.setImageUrl(userids[position].headimageurl, 1, true);
		if(userids!=null && userids.size()>0 && userids.get(position)!=null && userids.get(position).headimageurl!=null){	
			imageLoader.displayImage(userids.get(position).headimageurl, viewHoder.contactsImageView, options, animateFirstListener, ActiveAccount.getInstance(context).getUID(), 1);
		}else{
			viewHoder.contactsImageView.setImageResource(R.drawable.moren_touxiang);
		}
		//		switch (position) {
//		case 0:
//
//			break;
//		case 1:
//
//			viewHoder.contactsImageView
//					.setImageResource(R.drawable.temp_tuijian);
//			break;
//		case 2:
//
//			viewHoder.contactsImageView
//					.setImageResource(R.drawable.temp_huodong);
//			break;
//		case 3:
//
//			viewHoder.contactsImageView
//					.setImageResource(R.drawable.temp_looklook);
//			break;
//
//		default:
//			break;
//		}

		return convertView;
	}

	class ViewHolder {

		TextView alphaTextView;
		ImageView contactsImageView;
		TextView contactsTextView;
		ImageView alphaImageView;

		ImageView removeImageView;

		ImageView contactsArrow;
	}

	public void setData(ArrayList<MyUserids> userids) {

		this.userids = userids;
	}

}
