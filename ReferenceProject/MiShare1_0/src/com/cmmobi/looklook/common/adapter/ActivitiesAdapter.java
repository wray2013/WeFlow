package com.cmmobi.looklook.common.adapter;

import android.content.Context;
import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cmmobi.looklook.R;
import com.cmmobi.looklook.common.gson.GsonResponse3.activeListItem;
import com.cmmobi.looklook.common.utils.DateUtils;
import com.cmmobi.looklook.common.view.ClockView;
import com.cmmobi.looklook.info.profile.ActiveAccount;
import com.nostra13.universalimageloader.api.AnimateFirstDisplayListener;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;

/**
 * 活动列表的视频器
 * @author guoyang
 */
public class ActivitiesAdapter extends BaseAdapter {

	private Context context;
	
	//使用开源的webimageloader
	private DisplayImageOptions options;
	protected ImageLoader imageLoader;

	// private List<String> urlList = new ArrayList<String>();
	private activeListItem[] activeList = new activeListItem[0];

	private LayoutInflater inflater;

	public ActivitiesAdapter(Context context) {
		this.context = context;
		this.inflater = LayoutInflater.from(context);
		
		imageLoader = ImageLoader.getInstance();
		
		options = new DisplayImageOptions.Builder()
		.cacheInMemory(true)
		.cacheOnDisc(true)
		.displayer(new FadeInBitmapDisplayer(500))
		.build();
	}

	public void setData(activeListItem[] activeList) {
		this.activeList = new activeListItem[activeList.length];
		this.activeList = activeList;
	}

	@Override
	public int getCount() {
		return activeList.length;
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
			convertView = inflater.inflate(R.layout.activity_list_item, null);
			viewHolder = new ViewHolder();
			
			viewHolder.activites_image = (ImageView) convertView.findViewById(R.id.iv_activites_image);
			
			viewHolder.lly_left = (LinearLayout) convertView.findViewById(R.id.lly_activity_title_left);
			viewHolder.tv_name_left = (TextView) convertView.findViewById(R.id.tv_activ_name_left);
			viewHolder.tv_subname_left = (TextView) convertView.findViewById(R.id.tv_activ_subname_left);
			
			viewHolder.lly_right = (LinearLayout) convertView.findViewById(R.id.lly_activity_title_right);
			viewHolder.tv_name_right = (TextView) convertView.findViewById(R.id.tv_activ_name_right);
			viewHolder.tv_subname_right = (TextView) convertView.findViewById(R.id.tv_activ_subname_right);
			
			viewHolder.tv_name_down = (TextView) convertView.findViewById(R.id.tv_activ_name_down);
			
			viewHolder.imv_effective_ing = (ImageView) convertView.findViewById(R.id.imv_effective_ing);
			viewHolder.tv_activ_state = (TextView) convertView.findViewById(R.id.tv_activ_state);
			viewHolder.tv_desc = (TextView) convertView.findViewById(R.id.tv_desc);
			
			convertView.setTag(viewHolder);

		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		
		activeListItem active = activeList[position];
		
		viewHolder.lly_right.setVisibility(View.INVISIBLE);
		viewHolder.lly_left.setVisibility(View.INVISIBLE);
		
		if(position % 2 != 0){
			viewHolder.lly_right.setVisibility(View.VISIBLE);
			viewHolder.tv_name_right.setText(active.activename);
			viewHolder.tv_subname_right.setText(active.prize);
		}else{
			viewHolder.lly_left.setVisibility(View.VISIBLE);
			viewHolder.tv_name_left.setText(active.activename);
			viewHolder.tv_subname_left.setText(active.prize);
		}
		
		
		/*viewHolder.activiesImageview.setImageUrl(active.picture, 1, false);*/
		if(active.picture!=null){	
			imageLoader.displayImage(active.picture, viewHolder.activites_image, options, ActiveAccount.getInstance(context).getUID(), 1);
		}
		
		viewHolder.tv_name_down.setText(active.activename);
		viewHolder.tv_desc.setText(active.introduction);

		if ("0".equals(active.iseffective)) {
			viewHolder.tv_activ_state.setText("火热进行中");
			
			viewHolder.tv_activ_state.setTextColor(context.getResources().getColor(R.color.activ_tv_orange));
			
			viewHolder.imv_effective_ing.setVisibility(View.VISIBLE);
		} else if ("1".equals(active.iseffective)) {
			viewHolder.tv_activ_state.setText("结束");
			viewHolder.tv_activ_state.setTextColor(context.getResources().getColor(R.color.black));
			viewHolder.imv_effective_ing.setVisibility(View.GONE);
		} else if ("2".equals(active.iseffective)) {
			viewHolder.tv_activ_state.setText("未开始");
			viewHolder.tv_activ_state.setTextColor(context.getResources().getColor(R.color.black));
			viewHolder.imv_effective_ing.setVisibility(View.GONE);
		}else{
			viewHolder.tv_activ_state.setVisibility(View.INVISIBLE);
			viewHolder.imv_effective_ing.setVisibility(View.INVISIBLE);
		}

		return convertView;
	}

	class ViewHolder {
		ImageView activites_image;
		LinearLayout lly_left;
		TextView tv_name_left;
		TextView tv_subname_left;
		
		LinearLayout lly_right;
		TextView tv_name_right;
		TextView tv_subname_right;
		
		TextView tv_name_down;
		ImageView imv_effective_ing;
		TextView tv_activ_state;
		TextView tv_desc;
		
	}
}
