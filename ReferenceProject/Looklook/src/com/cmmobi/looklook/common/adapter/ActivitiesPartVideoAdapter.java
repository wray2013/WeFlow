package com.cmmobi.looklook.common.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import cn.zipper.framwork.core.ZApplication;

import com.cmmobi.looklook.R;
import com.cmmobi.looklook.activity.HomepageOtherDiaryActivity;
import com.cmmobi.looklook.common.gson.GsonResponse2.MyDiary;
import com.cmmobi.looklook.common.listview.pulltorefresh.DiaryListView;
import com.cmmobi.looklook.common.utils.DateUtils;
import com.cmmobi.looklook.common.view.ClockView;
import com.cmmobi.looklook.common.web.TackView;
import com.cmmobi.looklook.info.profile.ActiveAccount;
import com.nostra13.universalimageloader.api.AnimateFirstDisplayListener;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;
import com.nostra13.universalimageloader.core.display.CircularBitmapDisplayer;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;

public class ActivitiesPartVideoAdapter extends BaseAdapter {

	private Context context;
	private LayoutInflater inflater;
	private ArrayList<MyDiary> list;
	
	//使用开源的webimageloader
	private DisplayImageOptions options;
	private DisplayImageOptions options1;
	protected ImageLoader imageLoader;
	private ImageLoadingListener animateFirstListener;


	public ActivitiesPartVideoAdapter(Context context) {
		this.context = context;
		this.inflater = LayoutInflater.from(context);
		

		animateFirstListener = new AnimateFirstDisplayListener();
		imageLoader = ImageLoader.getInstance();
		
		options = new DisplayImageOptions.Builder()
		.cacheInMemory(true)
		.cacheOnDisc(true)
//		.displayer(new SimpleBitmapDisplayer())
		.showStubImage(R.drawable.moren_touxiang)
		.showImageForEmptyUri(R.drawable.moren_touxiang)
		.showImageOnFail(R.drawable.moren_touxiang)
		.displayer(new CircularBitmapDisplayer()) //圆形图片
		//.displayer(new RoundedBitmapDisplayer(20)) 圆角图片
		.build();
	
		options1 = new DisplayImageOptions.Builder()
		.cacheInMemory(true)
		.cacheOnDisc(true)
		.displayer(new SimpleBitmapDisplayer())
		.showStubImage(R.drawable.maptankuang_moren)
		.showImageForEmptyUri(R.drawable.maptankuang_moren)
		.showImageOnFail(R.drawable.maptankuang_moren)
		//.displayer(new CircularBitmapDisplayer()) 圆形图片
		//.displayer(new RoundedBitmapDisplayer(20)) 圆角图片
		.build();
	
	}
	
	public void setData(ArrayList<MyDiary> list) {
		this.list = list;
	}

	@Override
	public int getCount() {
		return list == null ? 0 : list.size();
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
			convertView = inflater.inflate(
					R.layout.list_item_activities_diary, null);

			viewHolder = new ViewHolder();

			viewHolder.circleItemClock = (ClockView) convertView
					.findViewById(R.id.circle_item_clock);
			viewHolder.circleItemTimeTextView = (TextView) convertView
					.findViewById(R.id.circle_item_time_textview);
			viewHolder.localTextView = (TextView) convertView
					.findViewById(R.id.local_textview);
			viewHolder.titleTextView = (TextView) convertView
					.findViewById(R.id.circle_item_title_textview);
			viewHolder.circleIconImageview = (ImageView) convertView
					.findViewById(R.id.circle_icon_imageview);
			viewHolder.circleIconImageview.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					String id = (String) v.getTag();
					if (null != id) {
						if (id.equals(ActiveAccount.getInstance(
								ZApplication.getInstance()).getUID())) {
//							Intent intent = new Intent(context,
//									HomepageMyselfDiaryActivity.class);
//							// intent.putExtra("userid", id);
//							context.startActivity(intent);
						} else {
							Intent intent = new Intent(context,
									HomepageOtherDiaryActivity.class);
							intent.putExtra("userid", id);
							context.startActivity(intent);
						}
					}
				}
			});
			viewHolder.coverImageView = (ImageView) convertView
					.findViewById(R.id.iv_pic);
			viewHolder.llDescription = (TextView) convertView
					.findViewById(R.id.ll_pic_description);// 文字描述
			viewHolder.llBiezhen = (TackView) convertView
					.findViewById(R.id.ll_biezhen);
			viewHolder.llBiezhen.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					String url=(String) v.getTag();
					if (null != url) {
						((TackView)v).setAudio(url,0);
					}
				}
			});
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		MyDiary myDiary = list.get(position);
		//viewHolder.circleIconImageview.setImageUrl(list.get(position).headimageurl, 1, true);
		if(myDiary!=null && myDiary.headimageurl!=null ){	
			imageLoader.displayImage(myDiary.headimageurl, viewHolder.circleIconImageview, options, animateFirstListener, ActiveAccount.getInstance(context).getUID(), 1);
			viewHolder.circleIconImageview.setTag(myDiary.userid);
		}
		
//		if(myDiary!=null && myDiary.attachs!=null && myDiary.attachs.length > 0){	
//			if (DateUtils.isNum(myDiary.attachs[0].pic_height)) {
//				RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, Integer.parseInt(myDiary.attachs[0].pic_height));
//				viewHolder.coverImageView.setLayoutParams(params);
//			}
//			imageLoader.displayImage(myDiary.attachs[0].videocover, viewHolder.coverImageView, options1, animateFirstListener, ActiveAccount.getInstance(context).getUID(), 1);
//		}
		viewHolder.titleTextView.setText(myDiary.nickname);
		
		viewHolder.localTextView.setText(myDiary.position);
		viewHolder.circleItemTimeTextView.setText(DateUtils.getStringFromMilli(
				myDiary.updatetimemilli, ""));
		viewHolder.circleItemClock.setTime(myDiary.updatetimemilli);
		
		int type = DiaryListView.getDiaryType(myDiary.attachs);// 获取日记类型

		if (myDiary != null && myDiary.attachs != null) {
			for (int l = 0; l < myDiary.attachs.length; l++) {
				
				int show_width=0;
				int show_heigh=0;
				if(DateUtils.isNum(myDiary.attachs[l].show_width))
					show_width=Integer.parseInt(myDiary.attachs[l].show_width);
				if(DateUtils.isNum(myDiary.attachs[l].show_height))
					show_heigh=Integer.parseInt(myDiary.attachs[l].show_height);
				
				String attachType = myDiary.attachs[l].attachtype;
				String attachLevel = myDiary.attachs[l].attachlevel;
				String playtime = myDiary.attachs[l].playtime;
				String textContent = myDiary.attachs[l].content;
				String videoCover = myDiary.attachs[l].videocover;

				String imageUrl = DiaryListView
						.getAttachUrl(myDiary.attachs[l].attachimage);
				String audioUrl = DiaryListView
						.getAttachUrl(myDiary.attachs[l].attachaudio);
				String mainText = textContent;

				if ("1".equals(attachLevel) && "1".equals(attachType)) {// 主内容为视频
					if (null != videoCover && videoCover.length() > 0) {
						imageLoader.displayImage(videoCover, viewHolder.coverImageView, options1, animateFirstListener, ActiveAccount.getInstance(context).getUID(), 1);

						android.view.ViewGroup.LayoutParams params = viewHolder.coverImageView
								.getLayoutParams();
						if (show_width != 0)
							params.width = show_width;
						if (show_heigh != 0)
							params.height = show_heigh;
						viewHolder.coverImageView.setLayoutParams(params);
						
					} else {
						viewHolder.coverImageView.setImageResource(R.drawable.maptankuang_moren);
					}
					
				}

				if ("0".equals(attachLevel) && "2".equals(attachType)) {// 辅内容为音频
					viewHolder.llBiezhen.setPlaytime(DateUtils
							.getPlayTime(playtime));
					if (null == audioUrl || 0 == audioUrl.length()) {
						audioUrl = myDiary.attachs[l].attachuuid;
					}
					viewHolder.llBiezhen.setTag(audioUrl);
				}

				if ("0".equals(attachLevel) && "4".equals(attachType)) {// 辅内容为文字
					viewHolder.llDescription.setText(mainText);
				}

				if (type == 0x101 || type == 0x1) {// 辅文字显示在主体上
					if ("0".equals(attachLevel) && "4".equals(attachType)) {// 辅内容为文字
						viewHolder.llDescription.setText(mainText);
					}
				}

			}
		}
		
		
		switch (type) {
		case 0x10000000:// 主体 视频
			// 主体文字
			viewHolder.llDescription.setVisibility(View.GONE);
			viewHolder.llBiezhen.setVisibility(View.GONE);
			break;
		case 0x10000100: {// 主体 视频+辅 音频
			viewHolder.llDescription.setVisibility(View.GONE);
			viewHolder.llBiezhen.setVisibility(View.VISIBLE);
			break;
		}
		case 0x10000101:// 主体 视频+辅 音频+文字

			viewHolder.llDescription.setVisibility(View.VISIBLE);
			viewHolder.llBiezhen.setVisibility(View.VISIBLE);
			break;
		case 0x10000001:// 主体 视频+文字
			viewHolder.llBiezhen.setVisibility(View.GONE);
			viewHolder.llDescription.setVisibility(View.VISIBLE);
			break;
		
		}
		
		
//		if (null != myDiary.introduction && myDiary.introduction.length() > 0) {
//			viewHolder.llDescription.setVisibility(View.VISIBLE);
//			viewHolder.llDescription.setText(myDiary.introduction);
//		} else {
//			viewHolder.llDescription.setVisibility(View.GONE);
//		}
		return convertView;
	}

	class ViewHolder {
		ClockView circleItemClock;
		TextView circleItemTimeTextView;
		TextView localTextView;

		TextView titleTextView;

		ImageView circleIconImageview;

		ImageView coverImageView;
		TextView llDescription;

		TackView llBiezhen;// 别针layout
	}
}
