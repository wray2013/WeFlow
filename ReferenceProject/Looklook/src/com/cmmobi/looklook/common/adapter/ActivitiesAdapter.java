package com.cmmobi.looklook.common.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.cmmobi.looklook.R;
import com.cmmobi.looklook.common.gson.GsonResponse2.activeListItem;
import com.cmmobi.looklook.common.utils.DateUtils;
import com.cmmobi.looklook.common.view.ClockView;
import com.cmmobi.looklook.info.profile.ActiveAccount;
import com.nostra13.universalimageloader.api.AnimateFirstDisplayListener;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;

public class ActivitiesAdapter extends BaseAdapter {

	private Context context;
	
	//使用开源的webimageloader
	private DisplayImageOptions options;
	protected ImageLoader imageLoader;
	private ImageLoadingListener animateFirstListener;

	// private List<String> urlList = new ArrayList<String>();
	private activeListItem[] activeList = new activeListItem[0];

	private LayoutInflater inflater;

	public ActivitiesAdapter(Context context) {
		this.context = context;
		this.inflater = LayoutInflater.from(context);
		
		animateFirstListener = new AnimateFirstDisplayListener();
		imageLoader = ImageLoader.getInstance();
		
		options = new DisplayImageOptions.Builder()
		.cacheInMemory(true)
		.cacheOnDisc(true)
		.displayer(new SimpleBitmapDisplayer())
		//.displayer(new CircularBitmapDisplayer()) 圆形图片
		//.displayer(new RoundedBitmapDisplayer(20)) 圆角图片
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

		ViewHolder viewHolder;
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.list_item_activities, null);
			viewHolder = new ViewHolder();

			viewHolder.activiesImageview = (ImageView) convertView
					.findViewById(R.id.activites_image);
			viewHolder.activitesName = (TextView) convertView
					.findViewById(R.id.activites_name);
			viewHolder.activitesDescribe = (TextView) convertView
					.findViewById(R.id.activites_miaoshu);
			viewHolder.timeLefTextView = (TextView) convertView
					.findViewById(R.id.time_left);
			viewHolder.iseffectiveTextView = (TextView) convertView
					.findViewById(R.id.iseffective);

			viewHolder.activitesClock = (ClockView) convertView
					.findViewById(R.id.activites_clock);

			convertView.setTag(viewHolder);

		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		activeListItem active = activeList[position];
		viewHolder.activitesName.setText(active.activename);
		
		/*viewHolder.activiesImageview.setImageUrl(active.picture, 1, false);*/
		if(active.picture!=null){	
			imageLoader.displayImage(active.picture, viewHolder.activiesImageview, options, animateFirstListener, ActiveAccount.getInstance(context).getUID(), 1);
		}
		
		viewHolder.activitesDescribe.setText(active.introduction);
		viewHolder.timeLefTextView.setText(active.starttime);
		Long time = DateUtils.stringToDate(active.starttime,
				DateUtils.DATE_FORMAT_NORMAL).getTime();
		viewHolder.activitesClock.setTime(time + "");

		if ("0".equals(active.iseffective)) {
			viewHolder.iseffectiveTextView.setText("活动进行中...");

		} else if ("1".equals(active.iseffective)) {
			viewHolder.iseffectiveTextView.setText("结束");

		} else if ("2".equals(active.iseffective)) {

			viewHolder.iseffectiveTextView.setText("未开始");
		}

		return convertView;
	}

	class ViewHolder {
		ImageView activiesImageview;
		TextView activitesName;
		TextView activitesDescribe;

		TextView timeLefTextView;

		ClockView activitesClock;

		TextView iseffectiveTextView;
	}
}
