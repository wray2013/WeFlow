package com.cmmobi.looklook.common.adapter;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cmmobi.looklook.R;
import com.cmmobi.looklook.common.gson.GsonResponse2;
import com.cmmobi.looklook.common.view.ClockView;
import com.cmmobi.looklook.common.view.ViewPagerDigit;

public class ActivitiesAwardAdapter extends BaseAdapter {

	private Context context;
	// private GsonResponse.timelineResponse timeResponse;
	private LayoutInflater inflater;

	public ActivitiesAwardAdapter(Context context) {
		this.context = context;
		this.inflater = LayoutInflater.from(context);
	}

	public void setData(GsonResponse2.timelineResponse timeResponse) {
		// this.timeResponse = timeResponse;
	}

	@Override
	public int getCount() {
		return 20;
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
			convertView = inflater.inflate(R.layout.list_item_friends_circle,
					null);
			viewHolder = new ViewHolder();

			// viewHolder.imageView = (ImageView) convertView
			// .findViewById(R.id.image);
			viewHolder.viewPager = (ViewPager) convertView
					.findViewById(R.id.tabcontent_vp);
			viewHolder.viewPagerDigit = (ViewPagerDigit) convertView
					.findViewById(R.id.dot);

			viewHolder.circleItemFav = (ImageView) convertView
					.findViewById(R.id.circle_item_fav);
			viewHolder.circleItemClock = (ClockView) convertView
					.findViewById(R.id.circle_item_clock);
			viewHolder.circleItemTimeTextView = (TextView) convertView
					.findViewById(R.id.circle_item_time_textview);
			viewHolder.friendsPin = (ImageView) convertView
					.findViewById(R.id.friends_pin);
			viewHolder.localTextView = (TextView) convertView
					.findViewById(R.id.local_textview);
//			viewHolder.recordTextView = (TextView) convertView
//					.findViewById(R.id.record_textview);
//			viewHolder.recordRelativeLayout = (RelativeLayout) convertView
//					.findViewById(R.id.record_rl);
//			viewHolder.videoImageView = (ImageView) convertView
//					.findViewById(R.id.video_imageview);
//			viewHolder.videoRelativeLayout = (RelativeLayout) convertView
//					.findViewById(R.id.video_rl);

			viewHolder.circleIconImageview = (ImageView) convertView
					.findViewById(R.id.circle_icon_imageview);

			viewHolder.circleIconImageview
					.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							Toast.makeText(context, "circleIcon",
									Toast.LENGTH_LONG).show();
						}
					});

			convertView.setTag(viewHolder);

		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		viewHolder.recordRelativeLayout.setVisibility(View.GONE);
		viewHolder.videoRelativeLayout.setVisibility(View.VISIBLE);
		viewHolder.localTextView.setVisibility(View.VISIBLE);
		viewHolder.friendsPin.setVisibility(View.VISIBLE);
		viewHolder.circleItemTimeTextView.setVisibility(View.VISIBLE);
		viewHolder.circleItemClock.setVisibility(View.VISIBLE);
		viewHolder.circleItemFav.setVisibility(View.VISIBLE);

		viewHolder.viewPager.setVisibility(View.GONE);
		viewHolder.viewPagerDigit.setVisibility(View.GONE);

		return convertView;
	}

	class ViewHolder {
		ViewPager viewPager;
		ViewPagerDigit viewPagerDigit;
		ImageView circleItemFav;
		ClockView circleItemClock;
		TextView circleItemTimeTextView;
		ImageView friendsPin;
		TextView localTextView;
		RelativeLayout recordRelativeLayout;
		TextView recordTextView;
		RelativeLayout videoRelativeLayout;
		ImageView videoImageView;

		ImageView circleIconImageview;
	}

}
