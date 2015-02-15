package com.cmmobi.looklook.common.adapter;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.text.Html;
import android.text.Html.ImageGetter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import cn.zipper.framwork.core.ZApplication;

import com.cmmobi.looklook.R;
import com.cmmobi.looklook.activity.HomepageMyselfDiaryActivity;
import com.cmmobi.looklook.activity.HomepageOtherDiaryActivity;
import com.cmmobi.looklook.common.gson.GsonResponse2.MyAttachAudio;
import com.cmmobi.looklook.common.gson.GsonResponse2.MyAttachImage;
import com.cmmobi.looklook.common.gson.GsonResponse2.MyDiary;
import com.cmmobi.looklook.common.gson.GsonResponse2.deletepublishAndEnjoyResponse;
import com.cmmobi.looklook.common.gson.GsonResponse2.diaryAttach;
import com.cmmobi.looklook.common.gson.GsonResponse2.enjoyResponse;
import com.cmmobi.looklook.common.gson.Requester2;
import com.cmmobi.looklook.common.listview.pulltorefresh.AbsRefreshView;
import com.cmmobi.looklook.common.listview.pulltorefresh.DiaryListView;
import com.cmmobi.looklook.common.utils.DateUtils;
import com.cmmobi.looklook.common.view.ClockView;
import com.cmmobi.looklook.common.web.TackView;
import com.cmmobi.looklook.common.web.WebImageView;
import com.cmmobi.looklook.info.profile.ActiveAccount;
import com.cmmobi.looklook.info.profile.DiaryManager;
import com.cmmobi.looklook.prompt.Prompt;
import com.nostra13.universalimageloader.core.ImageLoader;

public class NearbyRecommendAdapter extends BaseAdapter implements Callback {

	private Context context;

	// private LinkedList<timelinePart> parts = new LinkedList<timelinePart>();
	// private LinkedList<MyDiary> diaries = new LinkedList<MyDiary>();
	private ArrayList<Object> timeLineList = new ArrayList<Object>();

	private LayoutInflater inflater;

	private Handler handler;
	
	// 使用开源的webimageloader
//	private DisplayImageOptions options;
	protected ImageLoader imageLoader;
//	private ImageLoadingListener animateFirstListener;

	private ListView listView;

	private DiaryManager diaryManager;
	
	private AnimationSet mAnimationSet;
	
	public NearbyRecommendAdapter(Context context, ListView listView) {
		this.context = context;
		this.inflater = LayoutInflater.from(context);
		this.handler = new Handler(this);
		this.listView = listView;
		
		// for test
		// userID="5358e7db0646f04a820bcb20ebc2e7818a70";
		diaryManager = DiaryManager.getInstance();

//		animateFirstListener = new AnimateFirstDisplayListener();
		imageLoader = ImageLoader.getInstance();

//		options = new DisplayImageOptions.Builder()
//				.showStubImage(R.drawable.temp_local_icon)
//				.showImageForEmptyUri(R.drawable.temp_local_icon)
//				.showImageOnFail(R.drawable.temp_local_icon)
//				.cacheInMemory(true).cacheOnDisc(true)
//				.displayer(new CircularBitmapDisplayer()) // 圆形图片
//				.build();
	}

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
			convertView = inflater.inflate(R.layout.list_item_nearby_recommend,
					null);
			viewHolder = new ViewHolder();

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
			viewHolder.circleIconImageview = (WebImageView) convertView
					.findViewById(R.id.circle_icon_imageview);

			viewHolder.rlMainTextContent = (RelativeLayout) convertView
					.findViewById(R.id.rl_main_text_content);// 主体layout(文字)
			viewHolder.videoRelativeLayout = (RelativeLayout) convertView
					.findViewById(R.id.video_rl);

			viewHolder.ivVideoButton = (ImageView) convertView
					.findViewById(R.id.iv_video_play_button);// 视屏播放按钮
			
			// 新整合
			viewHolder.circleDiary = (RelativeLayout) convertView
					.findViewById(R.id.friends_circle_diary);

			// viewHolder.llDescription = convertView
			// .findViewById(R.id.ll_pic_description);// 文字描述layout
			viewHolder.llDescription = (TextView) convertView
					.findViewById(R.id.ll_pic_description);// 文字描述
			viewHolder.ivPic = (WebImageView) convertView
					.findViewById(R.id.iv_pic);// 图片类型
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
			viewHolder.ivWeather = (WebImageView) convertView
					.findViewById(R.id.iv_tianqi);// 天气图片
			viewHolder.ivStick = (ImageView) convertView
					.findViewById(R.id.iv_stick);// 天气图片下面的小棒子
			//
			convertView.setTag(viewHolder);

		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		if (timeLineList.get(position) instanceof MyDiary) {

			MyDiary diary = (MyDiary) timeLineList.get(position);
			int type = DiaryListView.getDiaryType(diary.attachs);// 获取日记类型

			viewHolder.circleDiary.setVisibility(View.VISIBLE);
			viewHolder.localTextView.setVisibility(View.VISIBLE);
			viewHolder.friendsPin.setVisibility(View.VISIBLE);
			viewHolder.circleItemTimeTextView.setVisibility(View.VISIBLE);
			viewHolder.circleItemClock.setVisibility(View.VISIBLE);
			viewHolder.circleItemFav.setVisibility(View.VISIBLE);
			if (diaryManager.isPraise(diary)) {
				viewHolder.circleItemFav.setImageResource(R.drawable.zan_2);
			} else {
				viewHolder.circleItemFav.setImageResource(R.drawable.zan_1);
			}
			viewHolder.circleItemFav.setTag(diary);
			viewHolder.circleItemFav.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					MyDiary tempDiary = (MyDiary) v.getTag();
					if (diaryManager.isPraise(tempDiary)) {
						Requester2.deletepublishAndEnjoy(handler, tempDiary.publishid,
								tempDiary.diaryid);
					} else {
						Requester2.enjoyandforward(handler, tempDiary.diaryid,
								tempDiary.publishid);
					}
				}
			});
//String debugText = "";
			if(diary!=null && diary.attachs!=null){
				viewHolder.tvMainText.setText(null);
				viewHolder.llDescription.setText(null);
				for (int l = 0; l < diary.attachs.length; l++) {
					int show_width=0;
					int show_heigh=0;
					if(DateUtils.isNum(diary.attachs[l].show_width))
						show_width=Integer.parseInt(diary.attachs[l].show_width);
					if(DateUtils.isNum(diary.attachs[l].show_height))
						show_heigh=Integer.parseInt(diary.attachs[l].show_height);
					Log.d("==WR==", "NearbyRecommend List show_width=" + show_width);
					Log.d("==WR==", "NearbyRecommend List show_heigh=" + show_heigh);
					
					String attachType = diary.attachs[l].attachtype;
					String attachLevel = diary.attachs[l].attachlevel;
					String playtime = diary.attachs[l].playtime;

					String imageUrl = getAttachUrl(diary.attachs[l].attachimage);
					String audioUrl = getAttachUrl(diary.attachs[l].attachaudio);
					String videoCover = getAttachUrl(diary.attachs[l]);
					
					if(imageUrl!=null&&imageUrl.length()>0&&imageUrl.startsWith("http")){
						imageUrl+="&width="+show_width+"&heigh="+show_heigh;
					}
					
					String mainText = diary.attachs[l].content;
					viewHolder.tvMainText.setText("");

					if ("1".equals(attachLevel) && "1".equals(attachType)) {// 主内容为视频
						viewHolder.ivPic.setImageUrl(R.drawable.shuaxin_background, 1, videoCover, false);
						viewHolder.ivPic.setTag(videoCover);
					}
					if ("1".equals(attachLevel) && "2".equals(attachType)) {// 主内容为音频
																			// 附件类型，1视频、2音频、3图片、4文字
						viewHolder.tvMainText.setText(DateUtils.getPlayTime(playtime));
					}
					if ("1".equals(attachLevel) && "3".equals(attachType)) {// 主内容为图片
						viewHolder.ivPic.setImageUrl(R.drawable.shuaxin_background, 1, imageUrl, false);
						if (imageUrl != null && imageUrl.length() > 0) {
							viewHolder.ivPic.setImageUrl(
									R.drawable.zhaopian_beijing_da, 1,
									imageUrl, false);
							viewHolder.ivPic.setTag(imageUrl);
						} else {
							viewHolder.ivPic.setImageUrl(
									R.drawable.zhaopian_beijing_da, 1,
									diary.attachs[l].attachuuid,
									false);
							viewHolder.ivPic
									.setTag(diary.attachs[l].attachuuid);
						}
					}
					if ("1".equals(attachLevel) && "4".equals(attachType)) {// 主内容为文字

						replacedExpressions(mainText, viewHolder.tvMainText);
					}
					if ("0".equals(attachLevel) && "2".equals(attachType)) {// 辅内容为音频
						viewHolder.llBiezhen.setPlaytime(DateUtils
								.getPlayTime(playtime));
						if (null == audioUrl || 0 == audioUrl.length()) {
							audioUrl = diary.attachs[l].attachuuid;
						}
						viewHolder.llBiezhen.setTag(audioUrl);
					}
					if ("0".equals(attachLevel) && "4".equals(attachType)) {// 辅内容为文字
//						viewHolder.llDescription.setText(mainText);
						replacedExpressions(mainText, viewHolder.llDescription);
					}
					
					if (type == 0x101 || type == 0x1/* || type == 0x100101 || type == 0x100001*/) {// 辅文字显示在主体上
						if ("0".equals(attachLevel) && "4".equals(attachType)) {// 辅内容为文字
//							viewHolder.tvMainText.setText(mainText);
							replacedExpressions(mainText, viewHolder.tvMainText);
						}
					}
//					debugText = mainText;
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
			case 0x10000101:// 主体 视频+辅 音频+文字
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
				break;
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
				android.widget.RelativeLayout.LayoutParams biezhenPa = new android.widget.RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
				biezhenPa.addRule(RelativeLayout.ALIGN_PARENT_LEFT,
						RelativeLayout.TRUE);
				biezhenPa.addRule(RelativeLayout.ALIGN_BOTTOM,
						viewHolder.rlMainTextContent.getId());
				viewHolder.llBiezhen.setLayoutParams(biezhenPa);
				viewHolder.llBiezhen.setVisibility(View.VISIBLE);
				viewHolder.ivVideoButton.setVisibility(View.GONE);
				viewHolder.videoRelativeLayout.setVisibility(View.GONE);
				showType = "录音";
				break;
			}
			case 0x1000101:// 主体 音频+辅 音频+文字
				viewHolder.rlMainTextContent.setVisibility(View.VISIBLE);
				viewHolder.ivCidai.setVisibility(View.VISIBLE);

				viewHolder.llDescription.setVisibility(View.VISIBLE);
				android.widget.RelativeLayout.LayoutParams biezhenPa = new android.widget.RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
				biezhenPa.addRule(RelativeLayout.ALIGN_PARENT_LEFT,RelativeLayout.TRUE);
				biezhenPa.addRule(RelativeLayout.ALIGN_BOTTOM,viewHolder.rlMainTextContent.getId());
				biezhenPa.bottomMargin= -34;
				viewHolder.llBiezhen.setLayoutParams(biezhenPa);
				viewHolder.llBiezhen.setVisibility(View.VISIBLE);

				viewHolder.videoRelativeLayout.setVisibility(View.GONE);
				viewHolder.ivVideoButton.setVisibility(View.GONE);
				showType = "录音";
				break;
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
			case 0x100100: // 主体 图片+辅 音频
				viewHolder.rlMainTextContent.setVisibility(View.GONE);
				viewHolder.ivCidai.setVisibility(View.GONE);
				viewHolder.llDescription.setVisibility(View.GONE);
				android.widget.RelativeLayout.LayoutParams biezhenP = new android.widget.RelativeLayout.LayoutParams(
						LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
				biezhenP.addRule(RelativeLayout.ALIGN_PARENT_LEFT,
						RelativeLayout.TRUE);
				biezhenP.addRule(RelativeLayout.ALIGN_BOTTOM,
						viewHolder.videoRelativeLayout.getId());
				viewHolder.llBiezhen.setLayoutParams(biezhenP);
				viewHolder.llBiezhen.setVisibility(View.VISIBLE);
				viewHolder.videoRelativeLayout.setVisibility(View.VISIBLE);
				viewHolder.ivVideoButton.setVisibility(View.GONE);
				showType = "图片";
				break;
			
			case 0x100101:// 主体 图片+辅 音频+文字
				viewHolder.rlMainTextContent.setVisibility(View.GONE);
				viewHolder.ivCidai.setVisibility(View.GONE);
				viewHolder.llDescription.setVisibility(View.VISIBLE);
				viewHolder.videoRelativeLayout.setVisibility(View.VISIBLE);
				android.widget.RelativeLayout.LayoutParams biezhen = new android.widget.RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
				biezhen.addRule(RelativeLayout.ALIGN_PARENT_LEFT,RelativeLayout.TRUE);
				biezhen.addRule(RelativeLayout.ALIGN_BOTTOM,viewHolder.videoRelativeLayout.getId());
				biezhen.bottomMargin= -34;
//				biezhenPar.bottomMargin= - dip2px(context, 17);
				viewHolder.llBiezhen.setLayoutParams(biezhen);
				viewHolder.llBiezhen.setVisibility(View.VISIBLE);
				viewHolder.ivVideoButton.setVisibility(View.GONE);
				showType = "图片";
				break;

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
				android.widget.RelativeLayout.LayoutParams biezhenText = new android.widget.RelativeLayout.LayoutParams(
						LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
				biezhenText.addRule(RelativeLayout.ALIGN_PARENT_LEFT,
						RelativeLayout.TRUE);
				biezhenText.addRule(RelativeLayout.ALIGN_BOTTOM,
						viewHolder.rlMainTextContent.getId());
				viewHolder.llBiezhen.setLayoutParams(biezhenText);
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
				android.widget.RelativeLayout.LayoutParams biezhenAudio = new android.widget.RelativeLayout.LayoutParams(
						LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
				biezhenAudio.addRule(RelativeLayout.ALIGN_PARENT_LEFT,
						RelativeLayout.TRUE);
				biezhenAudio.addRule(RelativeLayout.ALIGN_BOTTOM,
						viewHolder.rlMainTextContent.getId());
				viewHolder.llBiezhen.setLayoutParams(biezhenAudio);
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
				android.widget.RelativeLayout.LayoutParams biezhenText = new android.widget.RelativeLayout.LayoutParams(
						LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
				biezhenText.addRule(RelativeLayout.ALIGN_PARENT_LEFT,
						RelativeLayout.TRUE);
				biezhenText.addRule(RelativeLayout.ALIGN_BOTTOM,
						viewHolder.rlMainTextContent.getId());
				viewHolder.llBiezhen.setLayoutParams(biezhenText);
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
			/*
			case 0x10000000:// 主体 视频
				viewHolder.ivPic.setVisibility(View.VISIBLE);
				viewHolder.ivCidai.setVisibility(View.GONE);
				viewHolder.tvMainText.setVisibility(View.GONE);
				viewHolder.llDescription.setVisibility(View.GONE);
				viewHolder.llBiezhen.setVisibility(View.GONE);
				break;
			case 0x1000000:// 主体 音频
				viewHolder.tvMainText.setVisibility(View.VISIBLE);
				viewHolder.ivCidai.setVisibility(View.VISIBLE);
				viewHolder.ivPic.setVisibility(View.GONE);
				viewHolder.llDescription.setVisibility(View.GONE);
				viewHolder.llBiezhen.setVisibility(View.GONE);

				break;
			case 0x100000:// 主体 图片
				viewHolder.ivPic.setVisibility(View.VISIBLE);
				viewHolder.ivCidai.setVisibility(View.GONE);
				viewHolder.tvMainText.setVisibility(View.GONE);
				viewHolder.llDescription.setVisibility(View.GONE);
				viewHolder.llBiezhen.setVisibility(View.GONE);
				break;
			case 0x10000:// 主体 文字
				viewHolder.tvMainText.setVisibility(View.VISIBLE);
				viewHolder.ivPic.setVisibility(View.GONE);
				viewHolder.llDescription.setVisibility(View.GONE);
				viewHolder.llBiezhen.setVisibility(View.GONE);
				viewHolder.ivCidai.setVisibility(View.GONE);
				break;
			case 0x10000100: {// 主体 视频+辅 音频
				viewHolder.ivPic.setVisibility(View.VISIBLE);
				viewHolder.tvMainText.setVisibility(View.GONE);
				viewHolder.ivCidai.setVisibility(View.GONE);
				viewHolder.llDescription.setVisibility(View.GONE);
				viewHolder.llBiezhen.setVisibility(View.VISIBLE);
				android.widget.RelativeLayout.LayoutParams biezhenPar = new android.widget.RelativeLayout.LayoutParams(
						LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
//				biezhenPar.addRule(RelativeLayout.ALIGN_PARENT_LEFT,
//						RelativeLayout.TRUE);
//				biezhenPar.addRule(RelativeLayout.ALIGN_BOTTOM,
//						viewHolder.llContent.getId());
				viewHolder.llBiezhen.setLayoutParams(biezhenPar);
				break;
			}
			case 0x10000101:// 主体 视频+辅 音频+文字
				viewHolder.ivPic.setVisibility(View.VISIBLE);
				viewHolder.tvMainText.setVisibility(View.GONE);
				viewHolder.ivCidai.setVisibility(View.GONE);
				viewHolder.llDescription.setVisibility(View.VISIBLE);
				viewHolder.llBiezhen.setVisibility(View.VISIBLE);
				break;
			case 0x1000100: {// 主体 音频+辅 音频
				viewHolder.tvMainText.setVisibility(View.VISIBLE);
				viewHolder.ivPic.setVisibility(View.GONE);
				viewHolder.llDescription.setVisibility(View.GONE);
				viewHolder.llBiezhen.setVisibility(View.VISIBLE);
				android.widget.RelativeLayout.LayoutParams biezhenPar = new android.widget.RelativeLayout.LayoutParams(
						LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
//				biezhenPar.addRule(RelativeLayout.ALIGN_PARENT_LEFT,
//						RelativeLayout.TRUE);
//				biezhenPar.addRule(RelativeLayout.ALIGN_BOTTOM,
//						viewHolder.llContent.getId());
				viewHolder.llBiezhen.setLayoutParams(biezhenPar);
				break;
			}
			case 0x1000101:// 主体 音频+辅 音频+文字
				viewHolder.tvMainText.setVisibility(View.VISIBLE);
				viewHolder.llBiezhen.setVisibility(View.VISIBLE);
				viewHolder.ivCidai.setVisibility(View.VISIBLE);
				viewHolder.llDescription.setVisibility(View.GONE);
				viewHolder.ivPic.setVisibility(View.GONE);
				break;
			case 0x100100: {// 主体 图片+辅 音频
				viewHolder.ivPic.setVisibility(View.VISIBLE);
				viewHolder.tvMainText.setVisibility(View.GONE);
				viewHolder.llDescription.setVisibility(View.GONE);
				viewHolder.llBiezhen.setVisibility(View.VISIBLE);
				android.widget.RelativeLayout.LayoutParams biezhenPar = new android.widget.RelativeLayout.LayoutParams(
						LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
//				biezhenPar.addRule(RelativeLayout.ALIGN_PARENT_LEFT,
//						RelativeLayout.TRUE);
//				biezhenPar.addRule(RelativeLayout.ALIGN_BOTTOM,
//						viewHolder.llContent.getId());
				viewHolder.llBiezhen.setLayoutParams(biezhenPar);

				break;
			}
			case 0x100101:// 主体 图片+辅 音频+文字
				viewHolder.ivPic.setVisibility(View.VISIBLE);
				viewHolder.ivCidai.setVisibility(View.GONE);
				viewHolder.tvMainText.setVisibility(View.GONE);
				viewHolder.llDescription.setVisibility(View.VISIBLE);
				viewHolder.llBiezhen.setVisibility(View.VISIBLE);

				break;
			// case 0x10100://主体 文字+辅 音频
			//
			// break;
			// case 0x10101://主体 文字+辅 音频+文字
			//
			// break;
			// case 0x1000://辅 视频
			//
			// break;
			// case 0x100://辅 音频
			//
			// break;
			case 0x101: {// 辅 音频+文字
				viewHolder.ivPic.setVisibility(View.GONE);
				viewHolder.llDescription.setVisibility(View.GONE);
				viewHolder.ivCidai.setVisibility(View.GONE);
				viewHolder.llBiezhen.setVisibility(View.VISIBLE);
				android.widget.RelativeLayout.LayoutParams biezhenPar = new android.widget.RelativeLayout.LayoutParams(
						LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
//				biezhenPar.addRule(RelativeLayout.ALIGN_PARENT_LEFT,
//						RelativeLayout.TRUE);
//				biezhenPar.addRule(RelativeLayout.ALIGN_BOTTOM,
//						viewHolder.llContent.getId());
				viewHolder.llBiezhen.setLayoutParams(biezhenPar);
				break;
			}
			// case 0x10://辅 图片
			//
			// break;
			case 0x1:// 辅 文字
				viewHolder.ivPic.setVisibility(View.GONE);
				viewHolder.llBiezhen.setVisibility(View.GONE);
				viewHolder.llDescription.setVisibility(View.VISIBLE);
				viewHolder.ivCidai.setVisibility(View.GONE);

				break;

			default:
				break;
			*/
			}
//			Log.d("==WR==", "mainText = " + debugText);
//			Log.d("==WR==", "userid = " + diary.userid + ";viewHolder.tvMainText = " + viewHolder.tvMainText.getText() + ";type = " + type);
//			Log.d("==WR==", "userid = " + diary.userid + ";viewHolder.llDescription = " + viewHolder.llDescription.getText() + ";type = " + type);
			
			viewHolder.circleIconImageview.setImageUrl(R.drawable.moren_touxiang, 1, diary.headimageurl, true);
//			viewHolder.circleIconImageview.setImageUrl(0, 1,
//					diary.headimageurl, true);
			viewHolder.titleTextView.setText(diary.nickname);
			viewHolder.circleIconImageview.setTag(diary.userid);
			viewHolder.circleIconImageview
					.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							String id = (String) v.getTag();
							Toast.makeText(context, "id="+id, Toast.LENGTH_LONG).show();
							if (id == null) return;
							if (id.equals(ActiveAccount.getInstance(ZApplication.getInstance()).getLookLookID())) {
								Intent intent = new Intent(context,
										HomepageMyselfDiaryActivity.class);
								// intent.putExtra("userid", id);
								context.startActivity(intent);
							} else {
								Intent intent = new Intent(context,
										HomepageOtherDiaryActivity.class);
								intent.putExtra("userid", id);
								context.startActivity(intent);
							}

						}
					});

			viewHolder.circleItemClock.setTime(diary.updatetimemilli);
			viewHolder.circleItemTimeTextView.setText(DateUtils
					.getStringFromMilli(diary.updatetimemilli, ""));
			viewHolder.localTextView.setText(diary.position);
			if(diary.position == null || diary.position.equals("")) {
				viewHolder.friendsPin.setVisibility(View.GONE);
			}
			viewHolder.ivWeather.setImageUrl(R.drawable.tianqi_weizhi, 1, diary.weather, false);

		}
		return convertView;
	}

	class ViewHolder {
		ImageView circleItemFav;
		ClockView circleItemClock;
		TextView circleItemTimeTextView;
		ImageView friendsPin;
		TextView localTextView;

		TextView titleTextView;

		WebImageView circleIconImageview;
		
		RelativeLayout videoRelativeLayout;
		ImageView ivVideoButton;
		// 新整合
		RelativeLayout circleDiary;

		TextView llDescription;
//		TextView tvDescription;// 文字描述
		WebImageView ivPic;// 图片类型
//		View llContent; // 主体layout
		View rlMainTextContent;// 主体layout(文字)
		TextView tvMainText;// 主体中的文字
		ImageView ivCidai;// 主体中的磁带图片

		TackView llBiezhen;// 别针layout

		WebImageView ivWeather;// 天气图片
		ImageView ivStick;// 天气图片下面的小棒子
		//
	}

	private String getAttachUrl(MyAttachImage[] attachImages) {
		if (null == attachImages || 0 == attachImages.length) {
			return null;
		}
		// 根据类型返回需要的url
		return attachImages[0].imageurl;
	}

	private String getAttachUrl(MyAttachAudio[] attachAudios) {
		if (null == attachAudios || 0 == attachAudios.length) {
			return null;
		}
		// 根据类型返回需要的url
		return attachAudios[0].audiourl;
	}
	
	private String getAttachUrl(diaryAttach attach) {
		if (null == attach) {
			return null;
		}
		// 根据类型返回需要的url
		return attach.videocover;
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

							if (timeLineList.get(i) instanceof MyDiary) {
								MyDiary diary = (MyDiary) timeLineList
										.get(i);
								if (response.diaryid.equals(diary.diaryid)) {
									diaryManager
											.addPraiseDiaryID(diary.diaryid);
									diaryManager.addDiaryToPraise(diary);
									diaryManager.myPraiseDataChanged();
									int visiblePos = listView
											.getFirstVisiblePosition();
									int visibleLast = listView
											.getLastVisiblePosition();

//									if (!(i < visiblePos || i > visibleLast)) {

										int offset = i - visiblePos;
										View view = listView.getChildAt(offset +1);
										Log.d("==WR==", "offset = " + offset + ";listview size = " + listView.getCount());
										if (null != view) {
											ViewHolder viewHolder = (ViewHolder) view
													.getTag();
											if (null != viewHolder) {
												Log.d("==WR==", "Zan!");
												viewHolder.circleItemFav
														.setImageResource(R.drawable.zan_2);
												ScaleImageAnimation(viewHolder.circleItemFav);
												Prompt.Alert(context, "赞成功！");
												break;
											}
										}
//									}
								}
							}
						}
					}
				} else {
					Prompt.Alert(context, "赞失败！status " + response == null ? "null" : response.status);
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

							if (timeLineList.get(i) instanceof MyDiary) {
								MyDiary diary = (MyDiary) timeLineList
										.get(i);
								if (response.diaryid.equals(diary.diaryid)) {
									diaryManager
											.removePraiseDiaryID(diary.diaryid);
									diaryManager
											.removePraiseDiaryByID(diary.diaryid);
									diaryManager.myPraiseDataChanged();

									int visiblePos = listView
											.getFirstVisiblePosition();
									int visibleLast = listView
											.getLastVisiblePosition();

//									if (!(i < visiblePos || i > visibleLast)) {

										int offset = i - visiblePos;
										View view = listView.getChildAt(offset + 1);
										Log.d("==WR==", "offset = " + offset + ";listview size = " + listView.getCount());
										if (null != view) {
											ViewHolder viewHolder = (ViewHolder) view
													.getTag();
											if (null != viewHolder) {
												Log.d("==WR==", "Delete Zan!");
												viewHolder.circleItemFav
														.setImageResource(R.drawable.zan_1);
//												ScaleImageAnimation(viewHolder.circleItemFav);
												Prompt.Alert(context, "取消赞成功！");
												break;
											}
										}
//									}
								}
							}
						}
					}
				} else {
					Prompt.Alert(context, "取消赞失败！status " + response == null ? "null" : response.status);
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
//			Pattern pattern = Pattern.compile("\\[[\\u4e00-\\u9fa5]*?\\]");
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
