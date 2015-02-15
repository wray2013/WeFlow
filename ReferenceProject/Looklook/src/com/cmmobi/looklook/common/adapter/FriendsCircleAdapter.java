package com.cmmobi.looklook.common.adapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.R.color;
import android.R.integer;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.Html;
import android.text.Html.ImageGetter;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import cn.zipper.framwork.core.ZApplication;

import com.cmmobi.looklook.R;
import com.cmmobi.looklook.activity.ActivitiesActivity;
import com.cmmobi.looklook.activity.ActivitiesDetailActivity;
import com.cmmobi.looklook.activity.DiaryDetailActivity;
import com.cmmobi.looklook.activity.FriendsNearByActivity;
import com.cmmobi.looklook.activity.FriendsRecommendActivity;
import com.cmmobi.looklook.activity.HomepageMyselfDiaryActivity;
import com.cmmobi.looklook.activity.HomepageOtherDiaryActivity;
import com.cmmobi.looklook.common.gson.GsonResponse2.MyActive;
import com.cmmobi.looklook.common.gson.GsonResponse2.MyAttachAudio;
import com.cmmobi.looklook.common.gson.GsonResponse2.MyAttachImage;
import com.cmmobi.looklook.common.gson.GsonResponse2.MyDiary;
import com.cmmobi.looklook.common.gson.GsonResponse2.deletepublishAndEnjoyResponse;
import com.cmmobi.looklook.common.gson.GsonResponse2.enjoyResponse;
import com.cmmobi.looklook.common.gson.GsonResponse2.timelinePart;
import com.cmmobi.looklook.common.gson.Requester2;
import com.cmmobi.looklook.common.listview.pulltorefresh.AbsRefreshView;
import com.cmmobi.looklook.common.listview.pulltorefresh.DiaryListView;
import com.cmmobi.looklook.common.utils.CmmobiClickAgentWrapper;
import com.cmmobi.looklook.common.utils.DateUtils;
import com.cmmobi.looklook.common.view.ClockView;
import com.cmmobi.looklook.common.view.ViewPagerDigit;
import com.cmmobi.looklook.common.web.TackView;
import com.cmmobi.looklook.fragment.FriendsCircleFragment;
import com.cmmobi.looklook.fragment.WrapTimeLineDiary;
import com.cmmobi.looklook.info.profile.AccountInfo;
import com.cmmobi.looklook.info.profile.ActiveAccount;
import com.cmmobi.looklook.info.profile.DiaryManager;
import com.cmmobi.looklook.prompt.Prompt;
import com.google.gson.Gson;
import com.nostra13.universalimageloader.api.AnimateFirstDisplayListener;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;
import com.nostra13.universalimageloader.core.display.CircularBitmapDisplayer;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;

public class FriendsCircleAdapter extends BaseAdapter implements Callback {

	private Context context;

	// private LinkedList<timelinePart> parts = new LinkedList<timelinePart>();
	// private LinkedList<MyDiary> diaries = new LinkedList<MyDiary>();
	private ArrayList<Object> timeLineList = new ArrayList<Object>();

	private ArrayList<Integer> partPositions = new ArrayList<Integer>();
	private LayoutInflater inflater;

	private Handler handler;

	// 使用开源的webimageloader
	private DisplayImageOptions options;
	private DisplayImageOptions options1;
	protected ImageLoader imageLoader;
	private ImageLoadingListener animateFirstListener;

	private ListView listView;

	private String userID;

	private AccountInfo accountInfo;

	private DiaryManager diaryManager;

	private AnimationSet mAnimationSet;
	// private int currentPartPosition ;
	// private int currentDiaryPostion;
	
	private int viewPagerHeight;
	public FriendsCircleAdapter(Context context, ListView listView) {
		this.context = context;
		this.inflater = LayoutInflater.from(context);
		// ImageLoader.initialize(context, null);
		this.handler = new Handler(this);
		this.listView = listView;
//		DisplayMetrics dm = new DisplayMetrics();
		DisplayMetrics dm = this.context.getApplicationContext().getResources().getDisplayMetrics();

//		viewPagerHeight = (int)((density/160.0F) * 100);
		
		viewPagerHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
				120, context.getResources().getDisplayMetrics());
		
		userID = ActiveAccount.getInstance(ZApplication.getInstance()).getUID();
		// for test
		// userID="5358e7db0646f04a820bcb20ebc2e7818a70";
		accountInfo = AccountInfo.getInstance(userID);
		diaryManager = DiaryManager.getInstance();

		animateFirstListener = new AnimateFirstDisplayListener();
		imageLoader = ImageLoader.getInstance();

		options = new DisplayImageOptions.Builder()
		.cacheInMemory(true)
		.cacheOnDisc(true)
//		.displayer(new SimpleBitmapDisplayer())
		.displayer(new CircularBitmapDisplayer()) //圆形图片
		//.displayer(new RoundedBitmapDisplayer(20)) 圆角图片
		.build();
	
		options1 = new DisplayImageOptions.Builder()
		.cacheInMemory(true)
		.cacheOnDisc(true)
		.displayer(new SimpleBitmapDisplayer())
		//.displayer(new CircularBitmapDisplayer()) 圆形图片
		//.displayer(new RoundedBitmapDisplayer(20)) 圆角图片
		.build();
	}

	// public void setDiaries(LinkedList<MyDiary> diaries) {
	// this.diaries = diaries;
	// }
	//
	// public void setParts(LinkedList<timelinePart> parts) {
	// this.parts = parts;
	// for (int i = 0; i < parts.size(); i++) {
	// partPositions.add(Integer.parseInt(parts.get(i).part_position) - 1);
	// }
	// }

	public void setTimeLines(ArrayList<Object> timeLineList) {
		this.timeLineList = timeLineList;
	};

	@Override
	public int getCount() {
		// return parts.size() + diaries.size();
		return timeLineList.size();
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

			viewHolder.pengyouquanMainLl = (LinearLayout) convertView
					.findViewById(R.id.pengyouquan_main_ll);
			viewHolder.viewPager = (ViewPager) convertView
					.findViewById(R.id.tabcontent_vp);
			
			viewHolder.viewPager.setLayoutParams(new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, viewPagerHeight + 2));
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
			viewHolder.titleTextView = (TextView) convertView
					.findViewById(R.id.circle_item_title_textview);
			viewHolder.circleIconImageview = (ImageView) convertView
					.findViewById(R.id.circle_icon_imageview);

			viewHolder.rlMainTextContent = (RelativeLayout) convertView
					.findViewById(R.id.rl_main_text_content);// 主体layout(文字)
			viewHolder.tvMainText = (TextView) convertView
					.findViewById(R.id.tv_main_text_content);// 主体中的文字
			viewHolder.tvDescription = (TextView) convertView
					.findViewById(R.id.tv_description);// 文字描述

			viewHolder.videoRelativeLayout = (RelativeLayout) convertView
					.findViewById(R.id.video_rl);

			viewHolder.ivVideoButton = (ImageView) convertView
					.findViewById(R.id.iv_video_play_button);// 视屏播放按钮

			viewHolder.whoRl = (RelativeLayout) convertView
					.findViewById(R.id.who_rl);
			viewHolder.whoIcon = (ImageView) convertView
					.findViewById(R.id.who_icon);
			
			viewHolder.whoIcon.setOnClickListener(new OnClickListener() {
				
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
			viewHolder.whoName = (TextView) convertView
					.findViewById(R.id.who_name);
			viewHolder.whoTime = (TextView) convertView
					.findViewById(R.id.who_time);

			// 新整合
			viewHolder.circleDiary = (RelativeLayout) convertView
					.findViewById(R.id.friends_circle_diary);

			// viewHolder.llDescription = convertView
			// .findViewById(R.id.ll_pic_description);// 文字描述layout
			viewHolder.llDescription = (TextView) convertView
					.findViewById(R.id.ll_pic_description);// 文字描述
			viewHolder.ivPic = (ImageView) convertView
					.findViewById(R.id.iv_pic);// 图片类型
			viewHolder.ivPic.setImageResource(R.drawable.maptankuang_moren);
			// viewHolder.llContent =
			// convertView.findViewById(R.id.ll_content);// 主体layout
			// viewHolder.rlMainTextContent = convertView
			// .findViewById(R.id.rl_main_text_content);// 主体layout(文字)
			viewHolder.tvMainText = (TextView) convertView
					.findViewById(R.id.tv_main_text_content);// 主体中的文字
			viewHolder.ivCidai = (ImageView) convertView
					.findViewById(R.id.iv_cidai);// 主体中的磁带图片

			viewHolder.llBiezhen = (TackView) convertView
					.findViewById(R.id.ll_biezhen);// 别针layout
			viewHolder.llBiezhen.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					String url=(String) v.getTag();
					if (null != url) {
						((TackView)v).setAudio(url,0);
					}
				}
			});

//			viewHolder.ivWeather = (ImageView) convertView
//					.findViewById(R.id.iv_tianqi);// 天气图片
			viewHolder.ivStick = (ImageView) convertView
					.findViewById(R.id.iv_stick);// 天气图片下面的小棒子
			//

			viewHolder.viewPagerAdapter = new ViewPagerAdapter();

			convertView.setTag(viewHolder);

		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		if (timeLineList.get(position) instanceof timelinePart) {

			timelinePart part = (timelinePart) timeLineList.get(position);

			viewHolder.pengyouquanMainLl.setBackgroundColor(color.transparent);
			viewHolder.circleDiary.setVisibility(View.GONE);
			viewHolder.pengyouquanMainLl.setVisibility(View.GONE);
			viewHolder.localTextView.setVisibility(View.GONE);
			viewHolder.friendsPin.setVisibility(View.GONE);
			viewHolder.circleItemTimeTextView.setVisibility(View.GONE);
			viewHolder.circleItemClock.setVisibility(View.GONE);
			viewHolder.circleItemFav.setVisibility(View.GONE);

			viewHolder.viewPager.setVisibility(View.VISIBLE);
			viewHolder.viewPagerDigit.setVisibility(View.VISIBLE);

			viewHolder.viewPager.setOffscreenPageLimit(3);

			if (null != part.diaries && part.diaries.length > 0) {
				viewHolder.viewPager.setAdapter(new ViewPagerAdapterDiary(
						part.diaries));

				viewHolder.viewPager.setCurrentItem(part.diaries.length * 100);
				viewHolder.viewPager.setPageMargin(0);
				viewHolder.viewPagerDigit.setViewPager(viewHolder.viewPager,
						part.diaries.length);

			} else if (null != part.actives && part.actives.length > 0) {

				viewHolder.viewPager.setAdapter(viewHolder.viewPagerAdapter);

				viewHolder.viewPagerAdapter.setData(part.actives);
				viewHolder.viewPager.setCurrentItem(part.actives.length * 100);
				viewHolder.viewPager.setPageMargin(0);
				viewHolder.viewPagerDigit.setViewPager(viewHolder.viewPager,
						part.actives.length);
			}

			/*
			 * viewHolder.circleIconImageview.setImageUrl(part.partimgurl, 1,
			 * true);
			 */
			if (part != null && part.partimgurl != null) {
				imageLoader.displayImage(part.partimgurl,
						viewHolder.circleIconImageview, options,
						animateFirstListener, ActiveAccount
								.getInstance(context).getUID(), 1);
			} else {
				viewHolder.circleIconImageview
						.setImageResource(R.drawable.moren_touxiang);
			}

			// viewHolder.circleIconImageview.setImageUrl(0, 1, part.partimgurl,
			// true);
			viewHolder.titleTextView.setText(part.part_name);
			viewHolder.circleIconImageview.setTag(part.part_name);
			viewHolder.circleIconImageview
					.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							if (v.getTag().equals("活动")) {
								Intent intent = new Intent(context,
										ActivitiesActivity.class);
								context.startActivity(intent);
							} else if (v.getTag().equals("推荐")) {
								Intent intent = new Intent(context,
										FriendsRecommendActivity.class);
								intent.putExtra("label", 1);
								intent.putExtra("type", 2);
								CmmobiClickAgentWrapper.onEvent(context, "recommend", 1);
								context.startActivity(intent);

							} else if (v.getTag().equals("附近")) {
								Intent intent = new Intent(context,
										FriendsNearByActivity.class);
								context.startActivity(intent);

							}
						}
					});

		} else if (timeLineList.get(position) instanceof WrapTimeLineDiary) {

			WrapTimeLineDiary timelineDiary = (WrapTimeLineDiary) timeLineList
					.get(position);

			viewHolder.whoRl.setVisibility(View.VISIBLE);

			if (null != timelineDiary && null != timelineDiary.forward
					&& null != timelineDiary.forward.forwarduserid
					&& !"".equals(timelineDiary.forward.forwarduserid)) {
				viewHolder.pengyouquanMainLl
						.setBackgroundResource(R.drawable.pengyouquan2);

				timelineDiary.isZan = true;

				if (timelineDiary.forward != null
						&& timelineDiary.forward.forwardheadimage != null) {
					imageLoader.displayImage(
							timelineDiary.forward.forwardheadimage,
							viewHolder.circleIconImageview, options,
							animateFirstListener,
							ActiveAccount.getInstance(context).getUID(), 1);
					viewHolder.titleTextView
							.setText(timelineDiary.forward.forwardnickname);
					
				} else {
					viewHolder.circleIconImageview
							.setImageResource(R.drawable.moren_touxiang);
				}

				viewHolder.circleIconImageview
						.setTag(timelineDiary.forward.forwarduserid);
				
				if (timelineDiary.diary != null
						&& timelineDiary.diary.headimageurl != null) {
					imageLoader.displayImage(timelineDiary.diary.headimageurl,
							viewHolder.whoIcon, options, animateFirstListener,
							ActiveAccount.getInstance(context).getUID(), 1);
					viewHolder.whoName.setText(timelineDiary.diary.nickname);
					viewHolder.whoTime
							.setText(DateUtils.getStringFromMilli(timelineDiary.diary.diarytimemilli, ""));
				} else {
					viewHolder.whoIcon
							.setImageResource(R.drawable.moren_touxiang);
				}
				viewHolder.whoIcon.setTag(timelineDiary.diary.userid);

			} else {

				viewHolder.pengyouquanMainLl
						.setBackgroundResource(R.drawable.transparent);
				viewHolder.whoRl.setVisibility(View.GONE);

				if (timelineDiary.diary != null
						&& timelineDiary.diary.headimageurl != null) {
					imageLoader.displayImage(timelineDiary.diary.headimageurl,
							viewHolder.circleIconImageview, options,
							animateFirstListener,
							ActiveAccount.getInstance(context).getUID(), 1);
				} else {
					viewHolder.circleIconImageview
							.setImageResource(R.drawable.moren_touxiang);
				}

				viewHolder.titleTextView.setText(timelineDiary.diary.nickname);
				viewHolder.circleIconImageview
						.setTag(timelineDiary.diary.userid);
			}

			if (diaryManager.isPraise(timelineDiary.diary)) {
				viewHolder.circleItemFav.setImageResource(R.drawable.zan_2);
			} else {
				viewHolder.circleItemFav.setImageResource(R.drawable.zan_1);
			}

			int type = DiaryListView.getDiaryType(timelineDiary.diary.attachs);// 获取日记类型

			viewHolder.circleDiary.setVisibility(View.VISIBLE);
			viewHolder.pengyouquanMainLl.setVisibility(View.VISIBLE);
			viewHolder.localTextView.setVisibility(View.VISIBLE);
			viewHolder.friendsPin.setVisibility(View.VISIBLE);
			viewHolder.circleItemTimeTextView.setVisibility(View.VISIBLE);
			viewHolder.circleItemClock.setVisibility(View.VISIBLE);
			viewHolder.circleItemFav.setVisibility(View.VISIBLE);

			viewHolder.viewPager.setVisibility(View.GONE);
			viewHolder.viewPagerDigit.setVisibility(View.GONE);

			viewHolder.circleItemFav.setTag(timelineDiary.diary);
			viewHolder.circleItemFav.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					MyDiary timeLineDiaryTemp = (MyDiary) v.getTag();
					if (userID.equals(timeLineDiaryTemp.userid)) {
						return; 
					}
					if (diaryManager.isPraise(timeLineDiaryTemp)) {
						Requester2.deletepublishAndEnjoy(handler,
								timeLineDiaryTemp.publishid,
								timeLineDiaryTemp.diaryid);

					} else {
						Requester2.enjoyandforward(handler,
								timeLineDiaryTemp.diaryid,
								timeLineDiaryTemp.publishid);

					}
					// Toast.makeText(context, "赞成功！！！",
					// Toast.LENGTH_LONG).show();
				}
			});
			if (timelineDiary.diary != null
					&& timelineDiary.diary.attachs != null) {
//				viewHolder.ivWeather.setImageBitmap(null);
				viewHolder.tvMainText.setText(null);
				viewHolder.llDescription.setText(null);
//				if(timelineDiary.diary.weather!=null&&timelineDiary.diary.weather.length()>0){
//					imageLoader.displayImage(timelineDiary.diary.weather, viewHolder.ivWeather, options1, animateFirstListener, ActiveAccount.getInstance(context).getUID(), 1);
//				}
				for (int l = 0; l < timelineDiary.diary.attachs.length; l++) {

					
					int show_width=0;
					int show_heigh=0;
					if(DateUtils.isNum(timelineDiary.diary.attachs[l].show_width))
						show_width=Integer.parseInt(timelineDiary.diary.attachs[l].show_width);
					if(DateUtils.isNum(timelineDiary.diary.attachs[l].show_height))
						show_heigh=Integer.parseInt(timelineDiary.diary.attachs[l].show_height);

					
					String attachType = timelineDiary.diary.attachs[l].attachtype;
					String attachLevel = timelineDiary.diary.attachs[l].attachlevel;
					String playtime = timelineDiary.diary.attachs[l].playtime;
					String videoCover = timelineDiary.diary.attachs[l].videocover;
					
					String belongtype = "1";
					if(timelineDiary.diary.userid != null && timelineDiary.diary.userid.equals(ActiveAccount.getInstance(context).getUID())){
						belongtype = "0";
					}
					String imageUrl = getAttachUrl(timelineDiary.diary.attachs[l].attachimage, belongtype);
					String audioUrl = DiaryListView
							.getAttachUrl(timelineDiary.diary.attachs[l].attachaudio);
					String videoUrl = DiaryListView
							.getAttachUrl(timelineDiary.diary.attachs[l].attachvideo);
					String mainText = timelineDiary.diary.attachs[l].content;
					

					if(imageUrl!=null&&imageUrl.length()>0&&imageUrl.startsWith("http")){
						imageUrl+="&width="+show_width+"&heigh="+show_heigh;
					}
					if ("1".equals(attachLevel) && "1".equals(attachType)) {// 主内容为视频
//						viewHolder.ivPic.setImageUrl(
//								R.drawable.maptankuang_moren, 1, videoCover,
//								false);
//						viewHolder.ivPic.setTag(videoCover);
//						if (null != videoCover && videoCover.length() > 0) {
//							videoCover+="&width="+show_width+"&heigh="+show_heigh;
//							imageLoader.displayImage(videoCover, viewHolder.ivPic, options2, animateFirstListener, ActiveAccount.getInstance(context).getUID(), 1);
//
//						} else {
//							viewHolder.ivPic.setImageResource(R.drawable.maptankuang_moren);
//						}
						
						android.view.ViewGroup.LayoutParams params = viewHolder.ivPic
								.getLayoutParams();
						if (show_width != 0)
							params.width = show_width;
						if (show_heigh != 0)
							params.height = show_heigh;
//						viewHolder.videoRelativeLayout.setLayoutParams(params);
						viewHolder.ivPic.setLayoutParams(params);
						if (null != videoCover && videoCover.length() > 0) {
							
							imageLoader.displayImage(videoCover, viewHolder.ivPic, options1, animateFirstListener, ActiveAccount.getInstance(context).getUID(), 1);
							
						} else {
							viewHolder.ivPic.setImageResource(R.drawable.maptankuang_moren);
						}
						
					}
					
					
					if ("1".equals(attachLevel) && "3".equals(attachType)) {// 主内容为图片
//						int show_width=0;
//						int show_heigh=0;
//						if(DateUtils.isNum(timelineDiary.diary.attachs[l].show_width))
//							show_width=Integer.parseInt(timelineDiary.diary.attachs[l].show_width);
//						if(DateUtils.isNum(timelineDiary.diary.attachs[l].show_height))
//							show_heigh=Integer.parseInt(timelineDiary.diary.attachs[l].show_height);
//
//						if(imageUrl!=null&&imageUrl.length()>0&&imageUrl.startsWith("http")){
//							imageUrl+="&width="+show_width+"&heigh="+show_heigh;
//						}
						if (imageUrl != null && imageUrl.length() > 0) {
//							viewHolder.ivPic.setImageUrl(
//									R.drawable.maptankuang_moren, 1,
//									imageUrl, false);
//							viewHolder.ivPic.setTag(imageUrl);
							
//							if (null != imageUrl && imageUrl.length() > 0) {
//								imageUrl +="&width="+show_width+"&heigh="+show_heigh;
//								imageLoader.displayImage(imageUrl, viewHolder.ivPic, options2, animateFirstListener, ActiveAccount.getInstance(context).getUID(), 1);
//
//							} else {
//								viewHolder.ivPic.setImageResource(R.drawable.maptankuang_moren);
//							}
							android.view.ViewGroup.LayoutParams params = viewHolder.ivPic
									.getLayoutParams();
							if (show_width != 0)
								params.width = show_width;
							if (show_heigh != 0)
								params.height = show_heigh;
							imageLoader.displayImage(imageUrl, viewHolder.ivPic, options1, animateFirstListener, ActiveAccount.getInstance(context).getUID(), 1);
//							viewHolder.videoRelativeLayout.setLayoutParams(params);
//							viewHolder.ivPic.setLayoutParams(params);
//							if (null != videoCover && videoCover.length() > 0) {
//
//								
//							} else {
//								viewHolder.ivPic.setImageResource(R.drawable.maptankuang_moren);
//							}
						} else {
//							viewHolder.ivPic.setImageUrl(
//									R.drawable.maptankuang_moren, 1,
//									timelineDiary.diary.attachs[l].attachuuid,
//									false);
//							viewHolder.ivPic
//									.setTag(timelineDiary.diary.attachs[l].attachuuid);
							if (null != timelineDiary.diary && null != timelineDiary.diary.attachs[l] && null != timelineDiary.diary.attachs[l].attachuuid && timelineDiary.diary.attachs[l].attachuuid.length() > 0) {
								imageLoader.displayImage(timelineDiary.diary.attachs[l].attachuuid, viewHolder.ivPic, options1, animateFirstListener, ActiveAccount.getInstance(context).getUID(), 1);

							} else {
								viewHolder.ivPic.setImageResource(R.drawable.maptankuang_moren);
							}
						}
					}
					if ("1".equals(attachLevel) && "2".equals(attachType)) {// 主内容为音频
																			// 附件类型，1视频、2音频、3图片、4文字
						viewHolder.tvMainText.setText(DateUtils
								.getPlayTime(playtime));
					}
					if ("1".equals(attachLevel) && "4".equals(attachType)) {// 主内容为文字
//						viewHolder.tvMainText.setText(mainText);
						replacedExpressions(mainText, viewHolder.tvMainText);
					}

					if ("0".equals(attachLevel) && "2".equals(attachType)) {// 辅内容为音频
						viewHolder.llBiezhen.setPlaytime(DateUtils
								.getPlayTime(playtime));
						if (null == audioUrl || 0 == audioUrl.length()) {
							audioUrl = timelineDiary.diary.attachs[l].attachuuid;
						}
						viewHolder.llBiezhen.setTag(audioUrl);
					}

					if ("0".equals(attachLevel) && "4".equals(attachType)) {// 辅内容为文字
//						viewHolder.llDescription.setText(mainText);
						replacedExpressions(mainText, viewHolder.llDescription);
					}

					if (type == 0x101 || type == 0x1) {// 辅文字显示在主体上
						if ("0".equals(attachLevel) && "4".equals(attachType)) {// 辅内容为文字
//							viewHolder.tvMainText.setText(mainText);
							replacedExpressions(mainText, viewHolder.tvMainText);
						}
					}

				}
			}
			
			String showType = null;
			switch (type) {
			case 0x10000000:// 主体 视频
				// 主体文字
				viewHolder.rlMainTextContent.setVisibility(View.GONE);
				viewHolder.ivCidai.setVisibility(View.GONE);
				viewHolder.llDescription.setVisibility(View.GONE);
				viewHolder.llBiezhen.setVisibility(View.GONE);

				viewHolder.videoRelativeLayout.setVisibility(View.VISIBLE);
				viewHolder.ivVideoButton.setVisibility(View.VISIBLE);
				showType = "视频";
				break;
			case 0x10000100: {// 主体 视频+辅 音频
				viewHolder.rlMainTextContent.setVisibility(View.GONE);
				viewHolder.ivCidai.setVisibility(View.GONE);
				viewHolder.llDescription.setVisibility(View.GONE);
				
				android.widget.RelativeLayout.LayoutParams biezhenPar = new android.widget.RelativeLayout.LayoutParams(
						LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
				biezhenPar.addRule(RelativeLayout.ALIGN_PARENT_LEFT,
						RelativeLayout.TRUE);
				biezhenPar.addRule(RelativeLayout.ALIGN_BOTTOM,
						viewHolder.videoRelativeLayout.getId());
				viewHolder.llBiezhen.setLayoutParams(biezhenPar);
				viewHolder.llBiezhen.setVisibility(View.VISIBLE);
				viewHolder.videoRelativeLayout.setVisibility(View.VISIBLE);
				viewHolder.ivVideoButton.setVisibility(View.VISIBLE);
				showType = "视频";
				break;
			}
			case 0x10000101:{// 主体 视频+辅 音频+文字
				viewHolder.rlMainTextContent.setVisibility(View.GONE);
				viewHolder.ivCidai.setVisibility(View.GONE);

				android.widget.RelativeLayout.LayoutParams biezhenPar = new android.widget.RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
				biezhenPar.addRule(RelativeLayout.ALIGN_PARENT_LEFT,RelativeLayout.TRUE);
				biezhenPar.addRule(RelativeLayout.ALIGN_BOTTOM,viewHolder.videoRelativeLayout.getId());
				biezhenPar.bottomMargin= -34;
				viewHolder.llBiezhen.setLayoutParams(biezhenPar);
				viewHolder.llBiezhen.setVisibility(View.VISIBLE);
				viewHolder.llDescription.setVisibility(View.VISIBLE);

				viewHolder.videoRelativeLayout.setVisibility(View.VISIBLE);
				viewHolder.ivVideoButton.setVisibility(View.VISIBLE);
				showType = "视频";
				break;}
			case 0x10000001:// 主体 视频+文字
				viewHolder.rlMainTextContent.setVisibility(View.GONE);
				viewHolder.ivCidai.setVisibility(View.GONE);

				viewHolder.llBiezhen.setVisibility(View.GONE);
				viewHolder.llDescription.setVisibility(View.VISIBLE);

				viewHolder.videoRelativeLayout.setVisibility(View.VISIBLE);
				viewHolder.ivVideoButton.setVisibility(View.VISIBLE);
				showType = "视频";
				break;

			case 0x1000000:// 主体 音频
				viewHolder.rlMainTextContent.setVisibility(View.VISIBLE);
				viewHolder.ivCidai.setVisibility(View.VISIBLE);

				viewHolder.llDescription.setVisibility(View.GONE);
				viewHolder.llBiezhen.setVisibility(View.GONE);

				viewHolder.videoRelativeLayout.setVisibility(View.GONE);
				viewHolder.ivVideoButton.setVisibility(View.GONE);
				showType = "录音";
				break;
			case 0x1000100: {// 主体 音频+辅 音频
				viewHolder.rlMainTextContent.setVisibility(View.VISIBLE);
				viewHolder.ivCidai.setVisibility(View.VISIBLE);

				viewHolder.llDescription.setVisibility(View.GONE);
				 android.widget.RelativeLayout.LayoutParams biezhenPar=new
				 android.widget.RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
				 LayoutParams.WRAP_CONTENT);
				 biezhenPar.addRule(RelativeLayout.ALIGN_PARENT_LEFT,RelativeLayout.TRUE);
				 biezhenPar.addRule(RelativeLayout.ALIGN_BOTTOM,viewHolder.rlMainTextContent.getId());
				 viewHolder.llBiezhen.setLayoutParams(biezhenPar);
				 viewHolder.llBiezhen.setVisibility(View.VISIBLE);
				viewHolder.ivVideoButton.setVisibility(View.GONE);
				viewHolder.videoRelativeLayout.setVisibility(View.GONE);
				showType = "录音";
				break;
			}
			case 0x1000101:{// 主体 音频+辅 音频+文字

				viewHolder.rlMainTextContent.setVisibility(View.VISIBLE);
				viewHolder.ivCidai.setVisibility(View.VISIBLE);

				viewHolder.llDescription.setVisibility(View.VISIBLE);
				android.widget.RelativeLayout.LayoutParams biezhenPar = new android.widget.RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
				biezhenPar.addRule(RelativeLayout.ALIGN_PARENT_LEFT,RelativeLayout.TRUE);
				biezhenPar.addRule(RelativeLayout.ALIGN_BOTTOM,viewHolder.rlMainTextContent.getId());
				biezhenPar.bottomMargin= -34;
				viewHolder.llBiezhen.setLayoutParams(biezhenPar);
				viewHolder.llBiezhen.setVisibility(View.VISIBLE);

				viewHolder.videoRelativeLayout.setVisibility(View.GONE);
				viewHolder.ivVideoButton.setVisibility(View.GONE);
				showType = "录音";
				break;}
			case 0x1000001:// 主体音频+文字
				viewHolder.rlMainTextContent.setVisibility(View.VISIBLE);
				viewHolder.ivCidai.setVisibility(View.VISIBLE);

				viewHolder.llDescription.setVisibility(View.GONE);
				viewHolder.llBiezhen.setVisibility(View.GONE);

				viewHolder.videoRelativeLayout.setVisibility(View.GONE);
				viewHolder.ivVideoButton.setVisibility(View.GONE);
				showType = "录音";
				break;

			case 0x100000:// 主体 图片
				viewHolder.rlMainTextContent.setVisibility(View.GONE);
				viewHolder.ivCidai.setVisibility(View.GONE);

				viewHolder.llDescription.setVisibility(View.GONE);
				viewHolder.llBiezhen.setVisibility(View.GONE);

				viewHolder.videoRelativeLayout.setVisibility(View.VISIBLE);
				viewHolder.ivVideoButton.setVisibility(View.GONE);
				showType = "图片";
				break;
			case 0x100001:// 主体 图片 +文字
				viewHolder.rlMainTextContent.setVisibility(View.GONE);
				viewHolder.ivCidai.setVisibility(View.GONE);
				viewHolder.llBiezhen.setVisibility(View.GONE);
				viewHolder.videoRelativeLayout.setVisibility(View.VISIBLE);
				viewHolder.llDescription.setVisibility(View.VISIBLE);
				viewHolder.ivVideoButton.setVisibility(View.GONE);
				showType = "图片";

				break;
			case 0x100100: {// 主体 图片+辅 音频
				viewHolder.rlMainTextContent.setVisibility(View.GONE);
				viewHolder.ivCidai.setVisibility(View.GONE);
				viewHolder.llDescription.setVisibility(View.GONE);
				 android.widget.RelativeLayout.LayoutParams biezhenPar=new
				 android.widget.RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
				 LayoutParams.WRAP_CONTENT);
				 biezhenPar.addRule(RelativeLayout.ALIGN_PARENT_LEFT,RelativeLayout.TRUE);
				 biezhenPar.addRule(RelativeLayout.ALIGN_BOTTOM,viewHolder.videoRelativeLayout.getId());
				 viewHolder.llBiezhen.setLayoutParams(biezhenPar);
				 viewHolder.llBiezhen.setVisibility(View.VISIBLE);
				viewHolder.videoRelativeLayout.setVisibility(View.VISIBLE);
				viewHolder.ivVideoButton.setVisibility(View.GONE);
				showType = "图片";
				break;
			}
			case 0x100101:{// 主体 图片+辅 音频+文字
				viewHolder.rlMainTextContent.setVisibility(View.GONE);
				viewHolder.ivCidai.setVisibility(View.GONE);
				viewHolder.llDescription.setVisibility(View.VISIBLE);
				viewHolder.videoRelativeLayout.setVisibility(View.VISIBLE);
				android.widget.RelativeLayout.LayoutParams biezhenPar = new android.widget.RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
				biezhenPar.addRule(RelativeLayout.ALIGN_PARENT_LEFT,RelativeLayout.TRUE);
				biezhenPar.addRule(RelativeLayout.ALIGN_BOTTOM,viewHolder.videoRelativeLayout.getId());
				biezhenPar.bottomMargin= -34;
//				biezhenPar.bottomMargin= - dip2px(context, 17);
				viewHolder.llBiezhen.setLayoutParams(biezhenPar);
				viewHolder.llBiezhen.setVisibility(View.VISIBLE);
				viewHolder.ivVideoButton.setVisibility(View.GONE);
				showType = "图片";
				break;}

			case 0x10000:// 主体 文字
				viewHolder.rlMainTextContent.setVisibility(View.VISIBLE);
				viewHolder.ivCidai.setVisibility(View.GONE);
				viewHolder.llDescription.setVisibility(View.GONE);

				viewHolder.llBiezhen.setVisibility(View.GONE);

				viewHolder.videoRelativeLayout.setVisibility(View.GONE);
				viewHolder.ivVideoButton.setVisibility(View.GONE);
				showType = "日记";
				break;
			case 0x10001:// 主体 文字+文字
				viewHolder.rlMainTextContent.setVisibility(View.VISIBLE);
				viewHolder.ivCidai.setVisibility(View.GONE);
				viewHolder.llDescription.setVisibility(View.VISIBLE);

				viewHolder.llBiezhen.setVisibility(View.GONE);

				viewHolder.videoRelativeLayout.setVisibility(View.GONE);
				viewHolder.ivVideoButton.setVisibility(View.GONE);
				showType = "日记";
				break;
			case 0x10100: {// 主体 文字+辅 音频
				viewHolder.rlMainTextContent.setVisibility(View.VISIBLE);
				viewHolder.ivCidai.setVisibility(View.GONE);
				viewHolder.llDescription.setVisibility(View.GONE);
				viewHolder.videoRelativeLayout.setVisibility(View.GONE);
				viewHolder.ivVideoButton.setVisibility(View.GONE);
				 android.widget.RelativeLayout.LayoutParams biezhenPar=new
				 android.widget.RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
				 LayoutParams.WRAP_CONTENT);
				 biezhenPar.addRule(RelativeLayout.ALIGN_PARENT_LEFT,RelativeLayout.TRUE);
				 biezhenPar.addRule(RelativeLayout.ALIGN_BOTTOM,viewHolder.rlMainTextContent.getId());
				 viewHolder.llBiezhen.setLayoutParams(biezhenPar);
				 viewHolder.llBiezhen.setVisibility(View.VISIBLE);
				showType = "日记";
				break;
			}
			case 0x10101:// 主体 文字+辅 音频+文字
				viewHolder.rlMainTextContent.setVisibility(View.VISIBLE);
				viewHolder.ivCidai.setVisibility(View.GONE);
				viewHolder.llDescription.setVisibility(View.VISIBLE);

				viewHolder.llBiezhen.setVisibility(View.VISIBLE);

				viewHolder.videoRelativeLayout.setVisibility(View.GONE);
				viewHolder.ivVideoButton.setVisibility(View.GONE);
				showType = "日记";
				break;
			case 0x100: {// 辅 音频
				viewHolder.rlMainTextContent.setVisibility(View.VISIBLE);
				viewHolder.llDescription.setVisibility(View.GONE);
				viewHolder.ivCidai.setVisibility(View.VISIBLE);

				viewHolder.videoRelativeLayout.setVisibility(View.GONE);
				viewHolder.ivVideoButton.setVisibility(View.GONE);
				 android.widget.RelativeLayout.LayoutParams biezhenPar=new
				 android.widget.RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
				 LayoutParams.WRAP_CONTENT);
				 biezhenPar.addRule(RelativeLayout.ALIGN_PARENT_LEFT,RelativeLayout.TRUE);
				 biezhenPar.addRule(RelativeLayout.ALIGN_BOTTOM,viewHolder.rlMainTextContent.getId());
				 viewHolder.llBiezhen.setLayoutParams(biezhenPar);
				 viewHolder.llBiezhen.setVisibility(View.VISIBLE);
				showType = "日记";
				break;
			}
			case 0x101: {// 辅 音频+文字
				viewHolder.rlMainTextContent.setVisibility(View.VISIBLE);
				viewHolder.ivCidai.setVisibility(View.GONE);
				viewHolder.llDescription.setVisibility(View.GONE);

				viewHolder.videoRelativeLayout.setVisibility(View.GONE);
				viewHolder.ivVideoButton.setVisibility(View.GONE);
				 android.widget.RelativeLayout.LayoutParams biezhenPar=new
				 android.widget.RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
				 LayoutParams.WRAP_CONTENT);
				 biezhenPar.addRule(RelativeLayout.ALIGN_PARENT_LEFT,RelativeLayout.TRUE);
				 biezhenPar.addRule(RelativeLayout.ALIGN_BOTTOM,viewHolder.rlMainTextContent.getId());
				 viewHolder.llBiezhen.setLayoutParams(biezhenPar);
				 viewHolder.llBiezhen.setVisibility(View.VISIBLE);
				showType = "日记";
				break;
			}
			case 0x1:// 辅 文字
				viewHolder.rlMainTextContent.setVisibility(View.VISIBLE);
				viewHolder.ivCidai.setVisibility(View.GONE);
				viewHolder.llDescription.setVisibility(View.GONE);

				viewHolder.llBiezhen.setVisibility(View.GONE);

				viewHolder.videoRelativeLayout.setVisibility(View.GONE);
				viewHolder.ivVideoButton.setVisibility(View.GONE);
				showType = "日记";
				break;
			default:
				showType = "日记";
				break;
			}

			viewHolder.circleIconImageview
					.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {

							String id = (String) v.getTag();
							if (null != id) {
								if (id.equals(userID)) {
									return;
//									Intent intent = new Intent(context,
//											HomepageMyselfDiaryActivity.class);
//									// intent.putExtra("userid", id);
//									context.startActivity(intent);
								} else {
									Intent intent = new Intent(context,
											HomepageOtherDiaryActivity.class);
									intent.putExtra("userid", id);
									context.startActivity(intent);
								}
							}
						}
					});

			viewHolder.circleItemClock
					.setTime(timelineDiary.diary.updatetimemilli);
			if(null != timelineDiary && null != timelineDiary.forward
					&& null != timelineDiary.forward.forwarduserid
					&& !"".equals(timelineDiary.forward.forwarduserid)){
				viewHolder.circleItemTimeTextView
					.setText(DateUtils.getStringFromMilli(
							timelineDiary.diary.updatetimemilli, "") + " 赞了此" + showType);
			}else{
				viewHolder.circleItemTimeTextView
				.setText(DateUtils.getStringFromMilli(
						timelineDiary.diary.updatetimemilli, ""));
			}
			viewHolder.localTextView.setText(timelineDiary.diary.position);
//			viewHolder.ivWeather.setImageUrl(R.drawable.tianqi_weizhi, 1,
//					timelineDiary.diary.weather, false);

//			if (timelineDiary.diary.weather != null
//					&& timelineDiary.diary.weather.length() > 0) {
//				imageLoader.displayImage(timelineDiary.diary.weather,
//						viewHolder.ivWeather, options, animateFirstListener,
//						ActiveAccount.getInstance(context).getUID(), 1);
//			} else {
//				viewHolder.ivWeather.setImageResource(R.drawable.tianqi_weizhi);
//			}
		}
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

		TextView titleTextView;

		ImageView circleIconImageview;

		ViewPagerAdapter viewPagerAdapter;

		RelativeLayout videoRelativeLayout;
		ImageView ivVideoButton;

		// 新整合
		RelativeLayout circleDiary;
//		ImageView ivBg;
		TextView llDescription;
		// TextView tvDescription;// 文字描述
		ImageView ivPic;// 图片类型
		// View llContent; // 主体layout
		View rlMainTextContent;// 主体layout(文字)
		TextView tvMainText;// 主体中的文字
		TextView tvDescription;
		ImageView ivCidai;// 主体中的磁带图片

		TackView llBiezhen;// 别针layout

		ImageView ivWeather;// 天气图片
		ImageView ivStick;// 天气图片下面的小棒子

		LinearLayout pengyouquanMainLl;

		RelativeLayout whoRl;
		ImageView whoIcon;
		TextView whoName;
		TextView whoTime;

	}

	class ViewPagerAdapterDiary extends PagerAdapter {
		// ArrayList<Integer> list;
		MyDiary[] diaries;

		// String path =
		// "http://192.168.100.114:7076/pr/api/showPicture?imagetype=2&type=1&imageurl=";

		// public ViewPagerAdapter(ArrayList<Integer> list) {
		// this.list = list;
		// }

		public ViewPagerAdapterDiary() {
		}

		public ViewPagerAdapterDiary(MyDiary[] diaries) {
			this.diaries = diaries;
		}

		@Override
		public int getCount() {
			// return list.size();
			return Integer.MAX_VALUE;
		}

		public void setData(MyDiary[] diaries) {
			this.diaries = diaries;
		}

		@Override
		public float getPageWidth(int position) {
			return 0.85f;
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			View view = inflater.inflate(
					R.layout.friends_circle_viewpager_imageview, null);
			ImageView imageView = (ImageView) view
					.findViewById(R.id.viewpager_imageview);

			imageView.setTag(diaries == null ? 0 : position % diaries.length);
			imageView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					if (null != diaries && diaries.length > 0) {
						int p = (Integer) v.getTag();

						MyDiary diary = (MyDiary) (diaries[p]);

						DiaryManager.getInstance().setDetailDiaryList(
								Arrays.asList(diaries));
						Intent intent = new Intent(context,
								DiaryDetailActivity.class);
						intent.putExtra(
								DiaryDetailActivity.INTENT_ACTION_DIARY_UUID,
								diary.diaryuuid);
						intent.putExtra(FriendsCircleFragment.FRIENDSCIRCLE,
								FriendsCircleFragment.FRIENDSCIRCLE);
						context.startActivity(intent);
					}
				}
			});
			if (diaries != null) {
				if (diaries[position % diaries.length].attachs != null
						&& diaries[position % diaries.length].attachs.length > 0) {
					if (diaries[position % diaries.length].attachs[0].attachimage != null
							&& diaries[position % diaries.length].attachs[0].attachimage.length > 0) {
						
						if (null != diaries
								&& null != diaries[position % diaries.length]
								&& null != diaries[position % diaries.length].attachs
								&& diaries[position % diaries.length].attachs.length > 0
								&& null != diaries[position % diaries.length].attachs[0].attachimage
								&& diaries[position % diaries.length].attachs[0].attachimage.length > 0
								&& null != diaries[position % diaries.length].attachs[0].attachimage[0].imageurl
								&& diaries[position % diaries.length].attachs[0].attachimage[0].imageurl
										.length() > 0) {
							imageLoader
									.displayImage(
											diaries[position % diaries.length].attachs[0].attachimage[0].imageurl,
											imageView, options1,
											animateFirstListener, ActiveAccount
													.getInstance(context)
													.getUID(), 1);
						} else {
							imageView
									.setImageResource(R.drawable.maptankuang_moren);
						}

					} else if (diaries[position % diaries.length].attachs[0].videocover != null
							&& diaries[position % diaries.length].attachs[0].videocover.length() > 0) {
							imageLoader
									.displayImage(
											diaries[position % diaries.length].attachs[0].videocover,
											imageView, options1,
											animateFirstListener, ActiveAccount
													.getInstance(context)
													.getUID(), 1);
						} else {
							imageView
									.setImageResource(R.drawable.maptankuang_moren);
						}

//						imageView
//								.setImageUrl(
//										0,
//										1,
//										diaries == null ? ""
//												: diaries[position
//														% diaries.length].attachs[0].attachimage[0].imageurl,
//										false);
				}
			}
			// ;
			// mImageWorker.loadImage(list.get(position % list.size()),
			// imageView);
			container.addView(view);
			return view;
		}

		@Override
		public boolean isViewFromObject(View view, Object object) {
			return view == object;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			container.removeView((View) object);
		}
	}

	class ViewPagerAdapter extends PagerAdapter {
		// ArrayList<Integer> list;
		MyActive[] actives;

		// String path =
		// "http://192.168.100.114:7076/pr/api/showPicture?imagetype=2&type=1&imageurl=";

		// public ViewPagerAdapter(ArrayList<Integer> list) {
		// this.list = list;
		// }

		public ViewPagerAdapter() {
		}

		public ViewPagerAdapter(MyActive[] actives) {
			this.actives = actives;
		}

		@Override
		public int getCount() {
			// return list.size();
			return Integer.MAX_VALUE;
		}

		public void setData(MyActive[] actives) {
			this.actives = actives;
		}

		@Override
		public float getPageWidth(int position) {
			return 0.85f;
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			View view = inflater.inflate(
					R.layout.friends_circle_viewpager_imageview, null);
			ImageView imageView = (ImageView) view
					.findViewById(R.id.viewpager_imageview);

			imageView.setTag(actives == null ? 0 : position % actives.length);
			imageView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					int p = (Integer) v.getTag();
					String activeString = new Gson().toJson(actives[p]);
					context.startActivity(new Intent(context,
							ActivitiesDetailActivity.class).putExtra(
							"comefromfriendscircle",
							activeString));
				}
			});
			// imageView.setImageUrl(actives == null ? "" : actives[position
			// % actives.length].picture, 1, false);
//			imageView.setImageUrl(0, 1, actives == null ? "" : actives[position
//					% actives.length].picture, false);
			
			if (null != actives && actives.length > 0
					&& null != actives[position % actives.length]
					&& null != actives[position % actives.length].picture
					&& actives[position % actives.length].picture.length() > 0) {
				imageLoader.displayImage(
						actives[position % actives.length].picture, imageView,
						options1, animateFirstListener, ActiveAccount
								.getInstance(context).getUID(), 1);
			} else {
				imageView.setImageResource(R.drawable.maptankuang_moren);
			}
			// ;
			// mImageWorker.loadImage(list.get(position % list.size()),
			// imageView);
			container.addView(view);
			return view;
		}

		@Override
		public boolean isViewFromObject(View view, Object object) {
			return view == object;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			container.removeView((View) object);
		}
	}

	private String getAttachUrl(MyAttachImage[] attachImages, String type) {
		if (null == attachImages || 0 == attachImages.length) {
			return null;
		}
		if(type == null || type.equals("")){
			type = "1";
		}
		if(attachImages.length>1){
			for(int i=0;i<attachImages.length;i++){
				if(type.equals(attachImages[i].imagetype)){
					return attachImages[i].imageurl;
				}
			}
		}
		// TODO 根据类型返回需要的url
		return attachImages[0].imageurl;
	}

	private String getAttachUrl(MyAttachAudio[] attachAudios) {
		if (null == attachAudios || 0 == attachAudios.length) {
			return null;
		}
		// TODO 根据类型返回需要的url
		return attachAudios[0].audiourl;
	}

	private void ScaleImageAnimation(View v) {
		//增加点击放大效果
		AnimationSet animationSet = new AnimationSet(true);   
		if(mAnimationSet!=null && mAnimationSet != animationSet){   
			ScaleAnimation scaleAnimation = new ScaleAnimation(2,0.5f,2,0.5f,   
					Animation.RELATIVE_TO_PARENT,0.5f,   //使用动画播放图片   
					Animation.RELATIVE_TO_PARENT,0.5f);   
			scaleAnimation.setDuration(500);   
			mAnimationSet.addAnimation(scaleAnimation);   
//			mAnimationSet.setFillAfter(false); //让其保持动画结束时的状态。   
			v.startAnimation(mAnimationSet);   
		}   
		ScaleAnimation scaleAnimation = new ScaleAnimation(1,2f,1,2f,   
				Animation.RELATIVE_TO_SELF,0.5f,    
				Animation.RELATIVE_TO_SELF,0.5f);   
		scaleAnimation.setDuration(500);   
		animationSet.addAnimation(scaleAnimation);   
//		animationSet.setFillAfter(true);    
		v.startAnimation(animationSet);   
		mAnimationSet = animationSet;
	}
	
	@Override
	public boolean handleMessage(Message msg) {
		switch (msg.what) {
		case Requester2.RESPONSE_TYPE_DIARY_ENJOY:
			if (msg.obj != null) {
				enjoyResponse response = (enjoyResponse) msg.obj;

				if (null != response && "0".equals(response.status)) {

					for (int i = 0; i < timeLineList.size(); i++) {
						if (null != response.diaryid) {

							if (timeLineList.get(i) instanceof WrapTimeLineDiary) {
								WrapTimeLineDiary temp = (WrapTimeLineDiary) timeLineList
										.get(i);
								if (response.diaryid.equals(temp.diary.diaryid)) {
									diaryManager
											.addPraiseDiaryID(temp.diary.diaryid);
									diaryManager.addDiaryToPraise(temp.diary);
									diaryManager.myPraiseDataChanged();
									notifyDataSetChanged();
									int visiblePos = listView.getFirstVisiblePosition();
									int offset = i - visiblePos;
									View view = listView.getChildAt(offset +1);
									Log.d("==WR==", "offset = " + offset + ";listview size = " + listView.getCount());
									if (null != view) {
										ViewHolder viewHolder = (ViewHolder) view
												.getTag();
										if (null != viewHolder) {
											viewHolder.circleItemFav.setImageResource(R.drawable.zan_2);
											ScaleImageAnimation(viewHolder.circleItemFav);
											break;
										}
									}
								}
							}
						}
					}
					Prompt.Alert(context, "赞成功！");

				}
			} else {
				Prompt.Alert(context, "赞失败！");
			}
			break;
		case Requester2.RESPONSE_TYPE_DELETE_AND_ENJOY:
			if (msg.obj != null) {
				deletepublishAndEnjoyResponse response = (deletepublishAndEnjoyResponse) msg.obj;

				if (null != response && "0".equals(response.status)) {
					for (int i = 0; i < timeLineList.size(); i++) {
						if (null != response.diaryid) {

							if (timeLineList.get(i) instanceof WrapTimeLineDiary) {
								WrapTimeLineDiary temp = (WrapTimeLineDiary) timeLineList
										.get(i);
								if (response.diaryid.equals(temp.diary.diaryid)) {
									diaryManager
											.removePraiseDiaryID(temp.diary.diaryid);
									diaryManager
											.removePraiseDiaryByID(temp.diary.diaryid);
									diaryManager.myPraiseDataChanged();

									notifyDataSetChanged();
								}
							}

							Prompt.Alert(context, "取消赞成功！");
						}
					}
				}
			} else {
				Prompt.Alert(context, "取消赞失败！");
			}
			break;
		default:
			break;
		}

		return false;
	}
	
	 /** 
	  * 根据手机的分辨率从 dp 的单位 转成为 px(像素) 
	  */  
	public static int dip2px(Context context, float dpValue) {  
	    final float scale = context.getResources().getDisplayMetrics().density;  
	    return (int) (dpValue * scale + 0.5f);  
	}  

	
	// textview中显示文字+表情
			protected void replacedExpressions(String expressionText, TextView tv) {

				if(tv == null)
				{
					return;
				}
				
				if(expressionText == null)
				{
					tv.setText("");
					return;
				}
				
				tv.setText(null);
				
				ArrayList<String> list = getTextExpressions(expressionText);
				Log.d("replacedExpressions", "list="+list);
				if (list != null && list.size() > 0) {
					int len = list.size();
					int expStart = 0;
					int expEnd = 0;
					tv.setText(null);
					for (int i = 0; i < len; i++) {
						String exp = list.get(i);
						if(AbsRefreshView.EXPHM.get(exp)!=null){
							int expSrc=AbsRefreshView.EXPHM.get(exp);
							expEnd = expressionText.indexOf(exp, expStart);
							exp=exp.replace("[", "");
							exp=exp.replace("]", "");
							expressionText = expressionText.replaceFirst("\\[" + exp
									+ "\\]", "");
							tv.append(expressionText, expStart, expEnd);
							tv.append(Html.fromHtml("<img src='" +expSrc + "'/>",
									imageGetter, null));
							expStart = expEnd;
						}else{
							expStart = 0;
							expEnd = 0;
						}
					}
					tv.append(expressionText, expStart, expressionText.length());
				} else {
					Log.d("replacedExpressions", "expressionText="+expressionText);
					Log.d("replacedExpressions", "tv.getText()="+tv.getText());
					tv.append(expressionText);
				}
			}
			
			// TextView的getter
			private ImageGetter imageGetter = new ImageGetter() {

				@Override
				public Drawable getDrawable(String source) {
					int id = Integer.parseInt(source);
					Drawable drawable = context.getResources().getDrawable(id);
					drawable.setBounds(0, 0, drawable.getIntrinsicWidth() / 2,
							drawable.getIntrinsicHeight() / 2);
					return drawable;
				}
			};
			
			// 取出edittext字符串中的表情字段
			private ArrayList<String> getTextExpressions(String expressionText) {
				if (expressionText != null && expressionText.length() > 0) {
					ArrayList<String> list = new ArrayList<String>();
//					Pattern pattern = Pattern.compile("\\[[\\u4e00-\\u9fa5]*?\\]");
					Pattern pattern = Pattern.compile("\\[[\\u4e00-\\u9fa5a-zA-Z]*?\\]");
					Matcher matcher = pattern.matcher(expressionText);
					while (matcher.find()) {
						list.add(matcher.group());
					}
					return list;
				}
				return null;
			}
	

}
