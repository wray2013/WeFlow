package com.cmmobi.looklook.common.adapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cmmobi.looklook.R;
import com.cmmobi.looklook.common.view.SinaComparator;
import com.cmmobi.looklook.fragment.WrapSinaUser;
import com.cmmobi.looklook.info.profile.ActiveAccount;
import com.nostra13.universalimageloader.api.AnimateFirstDisplayListener;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;

public class FriendsSinaAdapter extends BaseAdapter {
	private Context context;
	private Handler handler;
	
	private List<String>  to_invite;

	private LayoutInflater inflater;

	private ArrayList<WrapSinaUser> data;
	
	//使用开源的webimageloader
	private DisplayImageOptions options;
	protected ImageLoader imageLoader;
	private ImageLoadingListener animateFirstListener;

	private ArrayList<Boolean> list_selected;

	public FriendsSinaAdapter(Context context, Handler handler, List<String> list_to_invite) {
		this.context = context;
		this.handler = handler;
		this.to_invite = list_to_invite;
		this.list_selected = new ArrayList<Boolean>();

		this.inflater = LayoutInflater.from(context);
		/*ImageLoader.initialize(context, null);*/
		
		animateFirstListener = new AnimateFirstDisplayListener();
		imageLoader = ImageLoader.getInstance();
		
		options = new DisplayImageOptions.Builder()
		.showStubImage(R.drawable.moren_touxiang)
		.showImageForEmptyUri(R.drawable.moren_touxiang)
		.showImageOnFail(R.drawable.moren_touxiang)
		.cacheInMemory(true)
		.cacheOnDisc(true)
		.displayer(new SimpleBitmapDisplayer())
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
			viewHolder.iv_portrait = (ImageView) convertView
					.findViewById(R.id.iv_portrait);
			viewHolder.tv_nickname = (TextView) convertView
					.findViewById(R.id.tv_nickname);
			viewHolder.iv_choose = (ImageView) convertView.findViewById(R.id.iv_choose);

			viewHolder.rl_all = (RelativeLayout) convertView.findViewById(R.id.rl_all);


		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		
		final WrapSinaUser item = data.get(position);
		
		if(item==null){
			return null;
		}
		
		viewHolder.tv_nickname.setText(item.screen_name);
		
		
		if(item.profile_image_url!=null && item.profile_image_url.length()>0){	
			imageLoader.displayImageEx(item.profile_image_url, viewHolder.iv_portrait, options, animateFirstListener, ActiveAccount.getInstance(context).getUID(), 1);
		}else{
			viewHolder.iv_portrait.setImageResource(R.drawable.moren_touxiang);
		}
	    
		if(list_selected.get(position)){
		    viewHolder.iv_choose.setVisibility(View.VISIBLE); //checked
		}else{
		    viewHolder.iv_choose.setVisibility(View.GONE);
		}
		
		convertView.setTag(viewHolder);
		return convertView;
	}
	
	public void setItemSelected(View view,int position) {
		ViewHolder viewHolder = (ViewHolder)view.getTag();
		final WrapSinaUser item = data.get(position);
		if (viewHolder == null || item==null || item.name==null) {
			return;
		}
		if(list_selected.get(position)){
			list_selected.set(position, false); //checked -> normal
			viewHolder.iv_choose.setVisibility(View.GONE);

		}else{
			list_selected.set(position, true); // normal -> checked
			viewHolder.iv_choose.setVisibility(View.VISIBLE);

		}
		
		to_invite.clear();
		for(int i=0; i< list_selected.size(); i++){
			if(list_selected.get(i)){
				to_invite.add(data.get(i).name);
			}
		}

/*		if(to_invite.size()<1){
			Message msg = handler.obtainMessage(FriendAddSinaWeiboActivity.HANDLER_FLAG_DISABLE_INVITE);
			msg.sendToTarget();
		}else{
			Message msg = handler.obtainMessage(FriendAddSinaWeiboActivity.HANDLER_FLAG_ENABLE_INVITE);
			msg.sendToTarget();
		}*/
	}

	class ViewHolder {
		ImageView iv_portrait;
		TextView tv_nickname;
		ImageView iv_choose;
		RelativeLayout rl_all;
	}

	public void setData(ArrayList<WrapSinaUser> list_friend_data, ArrayList<String> selected) {

		this.data = list_friend_data;
		this.to_invite.clear();
		this.list_selected.clear();
		Collections.sort(this.data, new SinaComparator());
		
		for(int i=0; i< this.data.size();  i++){
			String id = data.get(i).name;
			boolean flag = false;
			for(String str : selected)
			{
				if(id != null && id.equals(str))
				{
					flag = true;
					this.to_invite.add(str);
					break;
				}
			}
			this.list_selected.add(flag);
		}

	}
}
