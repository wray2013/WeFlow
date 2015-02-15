package com.cmmobi.looklook.common.listview.pulltorefresh;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cmmobi.looklook.R;
import com.cmmobi.looklook.activity.DiaryDetailActivity;
import com.cmmobi.looklook.common.gson.GsonResponse2.MyDiary;
import com.cmmobi.looklook.common.utils.CmmobiClickAgentWrapper;
import com.cmmobi.looklook.common.utils.DateUtils;
import com.cmmobi.looklook.common.view.ClockView;
import com.cmmobi.looklook.common.web.TackView;
import com.cmmobi.looklook.common.web.WebImageView;
import com.cmmobi.looklook.info.profile.DiaryManager;
import com.cmmobi.looklook.info.profile.DiaryManager.FilterType;

/**
 * @author wuxiang
 * @date 2013-6-1
 */
public class OtherRecentListView extends AbsRefreshView<Object> implements
		OnClickListener {

	private static final String TAG = "OhterRecentListView";
	private String otherUserId = "";

	public OtherRecentListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public OtherRecentListView(Context context) {
		super(context);
		init();
	}
	
	public int getDiaryWidth(){
		return (int)((float)getScreenWidth()/1.3);
	}
	
	public int getDiaryHeight(){
		return (int)((float)getDiaryWidth()/((float)getScreenWidth()/getScreenHight()));
	}

	private View rootView;
	private LinearLayout llSharelist;
	private LinearLayout llPraiselist;

	private void init() {
		rootView = inflater.inflate(
				R.layout.activity_homepage_other_recent_content, null);
		llShare = (LinearLayout) rootView.findViewById(R.id.ll_share);
		llPraise = (LinearLayout) rootView.findViewById(R.id.ll_praise);
		ivShare = (ImageView) rootView.findViewById(R.id.iv_share);
		ivPraise = (ImageView) rootView.findViewById(R.id.iv_praise);
		llShare.setOnClickListener(this);
		llPraise.setOnClickListener(this);

		llSharelist = (LinearLayout) rootView.findViewById(R.id.ll_sharelist);
		llPraiselist = (LinearLayout) rootView.findViewById(R.id.ll_praiselist);
		addChild(rootView, 1);
	}
	
	public void setDefaultChecked(){
		checkedView(FilterType.INTERACT_SHARE);
	}

	private FilterType type;
	
	public FilterType getRecentType(){
		return type;
	}

	public interface OnInitLinstener{
		void init();
	}
	
	private OnInitLinstener initLinstener;
	
	public void setOnInitLinstener(OnInitLinstener initLinstener,String userId){
		this.initLinstener=initLinstener;
		otherUserId = userId;
	}

	private boolean shareFirst;
	private boolean praiseFirst;
	private void checkedView(FilterType type) {

		switch (type) {
		case INTERACT_SHARE:// 分享页
			Log.d(TAG, "SHARE");
			this.type = type;
			if(0==shareList.size())
				if(initLinstener!=null)initLinstener.init();
//			if(!shareFirst){
//				shareFirst=true;
//				if(initLinstener!=null)initLinstener.init();
//			}
			llShare.setBackgroundResource(R.drawable.qiehuan_xuanzhong_chang);
			llPraise.setBackgroundDrawable(null);
			ivShare.setImageResource(R.drawable.qiehuan_fenxiang_chang_checked);
			ivPraise.setImageResource(R.drawable.qiehuan_zan_chang_normal);

			llSharelist.setVisibility(View.VISIBLE);
			llPraiselist.setVisibility(View.GONE);
			break;
		case INTERACT_PRAISE:// 赞页
			Log.d(TAG, "PRAISE");
			this.type = type;
			if(0==praiseList.size())
				if(initLinstener!=null)initLinstener.init();
//			if(!praiseFirst){
//				praiseFirst=true;
//				if(initLinstener!=null)initLinstener.init();
//			}
			llShare.setBackgroundDrawable(null);
			llPraise.setBackgroundResource(R.drawable.qiehuan_xuanzhong_chang);
			ivShare.setImageResource(R.drawable.qiehuan_fenxiang_chang_normal);
			ivPraise.setImageResource(R.drawable.qiehuan_zan_chang_checked);

			llSharelist.setVisibility(View.GONE);
			llPraiselist.setVisibility(View.VISIBLE);
			break;
		default:
			break;
		}
	}

	protected LinearLayout llShare;
	protected LinearLayout llPraise;
	protected ImageView ivShare;
	protected ImageView ivPraise;

	class params {
		int lId = 0;
		int rId = 0;
		boolean isLeft = true;
		int lHeight = 0;
		int viewId = 999;

		public void reset() {
			lId = 0;
			rId = 0;
			isLeft = true;
			lHeight = 0;
			viewId = 999;
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ll_biezhen:{//主体为图片或视频
			Log.d(TAG, "ll_biezhen");
			String url=(String) v.getTag();
			((TackView)v).setAudio(url,0);
			break;}
		case R.id.ll_share:
			HashMap<String, String> map1 = new HashMap<String, String>();
			map1.put("label1", "1");
			map1.put("label2", otherUserId);
			CmmobiClickAgentWrapper.onEvent(context, "fa_intetr", map1);
			checkedView(FilterType.INTERACT_SHARE);
			break;
		case R.id.ll_praise:
			HashMap<String, String> map2 = new HashMap<String, String>();
			map2.put("label1", "2");
			map2.put("label2", otherUserId);
			CmmobiClickAgentWrapper.onEvent(context, "fa_intetr", map2);
			checkedView(FilterType.INTERACT_PRAISE);
			break;
		case R.id.rl_media_content:
			String diaryuuid=v.getTag(R.id.rl_media_content).toString();
			DiaryManager.getInstance().setDetailDiaryList(getDiaryList());
			context.startActivity(new Intent(context,DiaryDetailActivity.class)
			.putExtra(DiaryDetailActivity.INTENT_ACTION_DIARY_UUID, diaryuuid));
			break;
		case R.id.ll_comment_rec://录音
			if(v.getTag()!=null){
				String url=v.getTag().toString();
				((TackView)v).setAudio(url, 0);
			}
			break;
		default:
			break;
		}
	}

	@Override
	protected void initContent(Object items) {
		clearViewList();
		clearDataList();
		addView(items);
	}
	
	private void clearDataList(){
		switch (type) {
		case INTERACT_SHARE:
			shareList.clear();
			break;
		case INTERACT_PRAISE:
			praiseList.clear();
			break;
		default:
			break;
		}
	}
	
	public void clearViewList(){
		switch (type) {
		case INTERACT_SHARE:
			llSharelist.removeAllViews();
			break;

		case INTERACT_PRAISE:
			llPraiselist.removeAllViews();
			break;
		default:
			break;
		}
	}

	@Override
	protected void addView(Object items) {
		Log.d(TAG, "type="+type);
		switch (this.type) {
		case INTERACT_SHARE: {
			if(items!=null&&items instanceof ArrayList){
				ArrayList<MyDiary> myDiaries=(ArrayList<MyDiary>) items;
				for(int i=0;i<myDiaries.size();i++){
					View v = inflater.inflate(
							R.layout.activity_homepage_recent_diary_item, null);
					porcessDiaryLayout(i, v,myDiaries.get(i));
					llSharelist.addView(v);
				}
			}
			break;
		}
		case INTERACT_PRAISE: {
			if(items!=null&&items instanceof ArrayList){
				ArrayList<MyDiary> myDiaries=(ArrayList<MyDiary>) items;
				for(int i=0;i<myDiaries.size();i++){
					View v = inflater.inflate(
							R.layout.activity_homepage_recent_diary_item, null);
					porcessDiaryLayout(i, v,myDiaries.get(i));
					llPraiselist.addView(v);
				}
			}
			break;
		}
		default:
			break;
		}
	}

	ArrayList<MyDiary> shareList=new ArrayList<MyDiary>();

	ArrayList<MyDiary> praiseList=new ArrayList<MyDiary>();

	private String firstTime_share="";
	private String firstTime_praise="";
	private String lastTime_share="";
	private String lastTime_praise="";
	private void addToDiaryList(MyDiary myDiary){
		switch (type) {
		case INTERACT_SHARE:
			if(0==shareList.size())
				firstTime_share=myDiary.updatetimemilli;
			lastTime_share=myDiary.updatetimemilli;
			shareList.add(myDiary);
			break;

		case INTERACT_PRAISE:
			if(0==praiseList.size())
				firstTime_praise=myDiary.updatetimemilli;
			lastTime_praise=myDiary.updatetimemilli;
			praiseList.add(myDiary);
			break;
		default:
			break;
		}
	}
	
	public String getFirstTime(){
		switch (type) {
		case INTERACT_SHARE:
			return firstTime_share;
		case INTERACT_PRAISE:
			return firstTime_praise;
		default:
			return "";
		}
	}
	
	public String getLastTime(){
		switch (type) {
		case INTERACT_SHARE:
			return lastTime_share;

		case INTERACT_PRAISE:
			return lastTime_praise;

		default:
			return "";
		}
	}
	
	public ArrayList getDiaryList(){
		switch (type) {
		case INTERACT_SHARE:
			return shareList;
		case INTERACT_PRAISE:
			return praiseList;
		default:
			return null;
		}
	}
	
	private void porcessDiaryLayout(int index, View v,MyDiary myDiary) {
		if(null==myDiary){
			Log.d(TAG, "porcessDiaryLayout->myDiary is null");
			return;
		}
		int type=DiaryListView.getDiaryType(myDiary.attachs);//获取日记类型
		addToDiaryList(myDiary);
		WebImageView ivPortrait = (WebImageView) v.findViewById(R.id.iv_recent_portrait);
		TextView tvNickname = (TextView) v
				.findViewById(R.id.tv_recent_nickname);
		TextView tvTime = (TextView) v.findViewById(R.id.tv_recent_time);
		
		ClockView clockView=(ClockView) v.findViewById(R.id.rl_time);
		clockView.setTime(myDiary.updatetimemilli);
		
//		View llPosition = v.findViewById(R.id.ll_recent_position);// 位置layout
		ImageView ivPin=(ImageView) v.findViewById(R.id.iv_pin);
		TextView tvPosition = (TextView) v.findViewById(R.id.tv_position);
		
//		ivPortrait.setImageUrl(myDiary.headimageurl, 2, true);
		//ivPortrait.setPortraiUrl(0, 1, myDiary.headimageurl);
		ivPortrait.setImageUrl(0, 1, myDiary.headimageurl, true);
		ivPortrait.setTag(myDiary.userid);
		ivPortrait.setOnClickListener(this);
		tvNickname.setText(myDiary.nickname);
		if(myDiary.position!=null&&myDiary.position.length()>0){
			tvPosition.setText(myDiary.position);
		}else{
			ivPin.setVisibility(View.INVISIBLE);
		}
		if(myDiary.updatetimemilli!=null){
			tvTime.setText(DateUtils.getMyCommonShowDate(new Date(Long.parseLong(myDiary.updatetimemilli))));
		}

		View rlMediaContent = v.findViewById(R.id.rl_media_content);// 日记layout
		rlMediaContent.setOnClickListener(this);
		rlMediaContent.setTag(R.id.rl_media_content, myDiary.diaryuuid);
		View llDescription = v.findViewById(R.id.ll_pic_description);// 文字描述layout
		TextView tvDescription = (TextView) v.findViewById(R.id.tv_description);// 文字描述
		WebImageView ivPic = (WebImageView) v.findViewById(R.id.iv_pic);// 图片类型
		ImageView ivVideoButton = (ImageView) v.findViewById(R.id.iv_video_play_button);//视屏播放按钮
		View llContent = v.findViewById(R.id.ll_content);// 主体layout
		View rlMainTextContent = v.findViewById(R.id.rl_main_text_content);// 主体layout(文字)
		TextView tvMainText = (TextView) v
				.findViewById(R.id.tv_main_text_content);// 主体中的文字
		ImageView ivCidai = (ImageView) v.findViewById(R.id.iv_cidai);// 主体中的磁带图片

		TackView llBiezhen = (TackView) v.findViewById(R.id.ll_biezhen);// 别针layout
		llBiezhen.setOnClickListener(this);

		WebImageView ivWeather = (WebImageView) v.findViewById(R.id.iv_tianqi);// 天气图片
//		ivWeather.setImageUrl(myDiary.weather, 1, false);
		ivWeather.setImageUrl(R.drawable.tianqi_weizhi, 1, myDiary.weather, false);
//		ImageView ivStick = (ImageView) v.findViewById(R.id.iv_stick);// 天气图片下面的小棒子
		for(int l=0;l<myDiary.attachs.length;l++){
			int show_width=0;
			int show_heigh=0;
			if(DateUtils.isNum(myDiary.attachs[l].show_width))
				show_width=Integer.parseInt(myDiary.attachs[l].show_width);
			if(DateUtils.isNum(myDiary.attachs[l].show_height))
				show_heigh=Integer.parseInt(myDiary.attachs[l].show_height);
			Log.d(TAG, "show_width="+show_width);
			Log.d(TAG, "show_heigh="+show_heigh);
			
			String attachType=myDiary.attachs[l].attachtype;
			String attachLevel=myDiary.attachs[l].attachlevel;
			String playtime=myDiary.attachs[l].playtime;
			String textContent=myDiary.attachs[l].content;
			String videoCover=myDiary.attachs[l].videocover;
			
			String imageUrl=OtherDiaryListView.getAttachUrl(myDiary.attachs[l].attachimage);
			String audioUrl=OtherDiaryListView.getAttachUrl(myDiary.attachs[l].attachaudio);
			
			if(imageUrl!=null&&imageUrl.length()>0&&imageUrl.startsWith("http")){
				imageUrl+="&width="+show_width+"&heigh="+show_heigh;
			}
			Log.d(TAG, "videoCover="+videoCover);
			Log.d(TAG, "imageUrl="+imageUrl);
			Log.d(TAG, "audioUrl="+audioUrl);
			String mainText=textContent;
			
			if("1".equals(attachLevel)&&"1".equals(attachType)){//主内容为视频
				ivPic.setImageUrl(R.drawable.zhaopian_beijing_da, 1, videoCover, false);
				ivPic.setTag(videoCover);
			}
			if("1".equals(attachLevel)&&"3".equals(attachType)){//主内容为图片
				if(imageUrl!=null&&imageUrl.length()>0){
					ivPic.setImageUrl(R.drawable.zhaopian_beijing_da, 1, imageUrl, false);
					ivPic.setTag(imageUrl);
				}else{
					ivPic.setImageUrl(R.drawable.zhaopian_beijing_da, 1, myDiary.attachs[l].attachuuid, false);
					ivPic.setTag(myDiary.attachs[l].attachuuid);
				}
			}
			if("1".equals(attachLevel)&&"2".equals(attachType)){//主内容为音频  附件类型，1视频、2音频、3图片、4文字
				tvMainText.setText(DateUtils.getPlayTime(playtime));
			}
			if("1".equals(attachLevel)&&"4".equals(attachType)){//主内容为文字
				tvMainText.setText(mainText);
			}
			
			if("0".equals(attachLevel)&&"2".equals(attachType)){//辅内容为音频
				llBiezhen.setPlaytime(DateUtils.getPlayTime(playtime));
				if(null==audioUrl||0==audioUrl.length()){
					audioUrl=myDiary.attachs[l].attachuuid;
				}
				llBiezhen.setTag(audioUrl);
			}
			
			if("0".equals(attachLevel)&&"4".equals(attachType)){//辅内容为文字
				tvDescription.setText(mainText);
			}
			
			if(type==0x101||type==0x1){//辅文字显示在主体上
				if("0".equals(attachLevel)&&"4".equals(attachType)){//辅内容为文字
					tvMainText.setText(mainText);
				}
			}
			
			/*Log.d(TAG, "attachid="+myDiary.attachs[l].attachid);
			Log.d(TAG, "attachlevel="+myDiary.attachs[l].attachlevel);
			Log.d(TAG, "attachtimemilli="+myDiary.attachs[l].attachtimemilli);
			Log.d(TAG, "attachtype="+myDiary.attachs[l].attachtype);
			Log.d(TAG, "orshare="+myDiary.attachs[l].orshare);
			Log.d(TAG, "pic_height="+myDiary.attachs[l].pic_height);
			Log.d(TAG, "pic_width="+myDiary.attachs[l].pic_width);
			Log.d(TAG, "playtime="+myDiary.attachs[l].playtime);
			Log.d(TAG, "playtimes="+myDiary.attachs[l].playtimes);
			Log.d(TAG, "show_height="+myDiary.attachs[l].show_height);
			Log.d(TAG, "show_width="+myDiary.attachs[l].show_width);
			
			for(int m=0;m<myDiary.attachs[l].attachaudio.length;m++){
				Log.d(TAG, "audiotype="+myDiary.attachs[l].attachaudio[m].audiotype);
				Log.d(TAG, "audiourl="+myDiary.attachs[l].attachaudio[m].audiourl);
			}
			for(int m=0;m<myDiary.attachs[l].attachimage.length;m++){
				Log.d(TAG, "imagetype="+myDiary.attachs[l].attachimage[m].imagetype);
				Log.d(TAG, "imageurl="+myDiary.attachs[l].attachimage[m].imageurl);
			}
			for(int m=0;m<myDiary.attachs[l].videopath.length;m++){
				Log.d(TAG, "playvideourl="+myDiary.attachs[l].videopath[m].playvideourl);
				Log.d(TAG, "videotype="+myDiary.attachs[l].videopath[m].videotype);
			}*/
		}
		
		switch (type) {
		case 0x10000000://主体 视频
			rlMainTextContent.setVisibility(View.GONE);
			llDescription.setVisibility(View.GONE);
			llBiezhen.setVisibility(View.GONE);
			break;
		case 0x10000100:{//主体 视频+辅 音频
			rlMainTextContent.setVisibility(View.GONE);
			ivCidai.setVisibility(View.GONE);
			llDescription.setVisibility(View.GONE);
			android.widget.RelativeLayout.LayoutParams biezhenPar=new android.widget.RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			biezhenPar.addRule(RelativeLayout.ALIGN_PARENT_LEFT,RelativeLayout.TRUE);
			biezhenPar.addRule(RelativeLayout.ALIGN_BOTTOM,llContent.getId());
			llBiezhen.setLayoutParams(biezhenPar);
			break;}
		case 0x10000101://主体 视频+辅 音频+文字
			rlMainTextContent.setVisibility(View.GONE);
			ivCidai.setVisibility(View.GONE);
			break;
		case 0x10000001://主体 视频+文字
			rlMainTextContent.setVisibility(View.GONE);
			llBiezhen.setVisibility(View.GONE);
			break;
			
			
		case 0x1000000://主体 音频
			ivPic.setVisibility(View.GONE);
			llDescription.setVisibility(View.GONE);
			llBiezhen.setVisibility(View.GONE);
			ivVideoButton.setVisibility(View.GONE);
			break;
		case 0x1000100:{//主体 音频+辅 音频
			ivPic.setVisibility(View.GONE);
			llDescription.setVisibility(View.GONE);
			ivVideoButton.setVisibility(View.GONE);
			android.widget.RelativeLayout.LayoutParams biezhenPar=new android.widget.RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			biezhenPar.addRule(RelativeLayout.ALIGN_PARENT_LEFT,RelativeLayout.TRUE);
			biezhenPar.addRule(RelativeLayout.ALIGN_BOTTOM,llContent.getId());
			llBiezhen.setLayoutParams(biezhenPar);
			break;
			}
		case 0x1000101://主体 音频+辅 音频+文字
			ivPic.setVisibility(View.GONE);
			ivVideoButton.setVisibility(View.GONE);
			break;
		case 0x1000001://主体音频+文字
			ivPic.setVisibility(View.GONE);
			llBiezhen.setVisibility(View.GONE);
			ivVideoButton.setVisibility(View.GONE);
			break;
			
		case 0x100000://主体 图片
			ivVideoButton.setVisibility(View.GONE);
			rlMainTextContent.setVisibility(View.GONE);
			llDescription.setVisibility(View.GONE);
			llBiezhen.setVisibility(View.GONE);
			break;
		case 0x100001://主体 图片 +文字
			ivVideoButton.setVisibility(View.GONE);
			rlMainTextContent.setVisibility(View.GONE);
			llBiezhen.setVisibility(View.GONE);
			break;
		case 0x100100:{//主体 图片+辅 音频
			ivVideoButton.setVisibility(View.GONE);
			rlMainTextContent.setVisibility(View.GONE);
			llDescription.setVisibility(View.GONE);
			android.widget.RelativeLayout.LayoutParams biezhenPar=new android.widget.RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			biezhenPar.addRule(RelativeLayout.ALIGN_PARENT_LEFT,RelativeLayout.TRUE);
			biezhenPar.addRule(RelativeLayout.ALIGN_BOTTOM,llContent.getId());
			llBiezhen.setLayoutParams(biezhenPar);
			
			break;}
		case 0x100101://主体 图片+辅 音频+文字
			ivVideoButton.setVisibility(View.GONE);
			rlMainTextContent.setVisibility(View.GONE);
			break;
			
		case 0x10000://主体 文字
			ivVideoButton.setVisibility(View.GONE);
			ivPic.setVisibility(View.GONE);
			llDescription.setVisibility(View.GONE);
			llBiezhen.setVisibility(View.GONE);
			ivCidai.setVisibility(View.GONE);
			break;
		case 0x10001://主体 文字+文字
			ivVideoButton.setVisibility(View.GONE);
			ivPic.setVisibility(View.GONE);
			ivCidai.setVisibility(View.GONE);
			llBiezhen.setVisibility(View.GONE);
			break;
		case 0x10100:{//主体 文字+辅 音频
			ivVideoButton.setVisibility(View.GONE);
			ivPic.setVisibility(View.GONE);
			llBiezhen.setVisibility(View.GONE);
			ivCidai.setVisibility(View.GONE);
			llDescription.setVisibility(View.GONE);
			android.widget.RelativeLayout.LayoutParams biezhenPar=new android.widget.RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			biezhenPar.addRule(RelativeLayout.ALIGN_PARENT_LEFT,RelativeLayout.TRUE);
			biezhenPar.addRule(RelativeLayout.ALIGN_BOTTOM,llContent.getId());
			llBiezhen.setLayoutParams(biezhenPar);
			break;}
		case 0x10101://主体 文字+辅 音频+文字
			ivVideoButton.setVisibility(View.GONE);
			ivPic.setVisibility(View.GONE);
			ivCidai.setVisibility(View.GONE);
			break;
		case 0x100:{//辅 音频
			ivVideoButton.setVisibility(View.GONE);
			ivPic.setVisibility(View.GONE);
			llDescription.setVisibility(View.GONE);
			ivCidai.setVisibility(View.GONE);
			android.widget.RelativeLayout.LayoutParams biezhenPar=new android.widget.RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			biezhenPar.addRule(RelativeLayout.ALIGN_PARENT_LEFT,RelativeLayout.TRUE);
			biezhenPar.addRule(RelativeLayout.ALIGN_BOTTOM,llContent.getId());
			llBiezhen.setLayoutParams(biezhenPar);
			break;}
		case 0x101:{//辅 音频+文字
			ivVideoButton.setVisibility(View.GONE);
			ivPic.setVisibility(View.GONE);
			llDescription.setVisibility(View.GONE);
			ivCidai.setVisibility(View.GONE);
			android.widget.RelativeLayout.LayoutParams biezhenPar=new android.widget.RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			biezhenPar.addRule(RelativeLayout.ALIGN_PARENT_LEFT,RelativeLayout.TRUE);
			biezhenPar.addRule(RelativeLayout.ALIGN_BOTTOM,llContent.getId());
			llBiezhen.setLayoutParams(biezhenPar);
			break;}
		case 0x1://辅 文字
			ivVideoButton.setVisibility(View.GONE);
			ivPic.setVisibility(View.GONE);
			llBiezhen.setVisibility(View.GONE);
			llDescription.setVisibility(View.GONE);
			ivCidai.setVisibility(View.GONE);
			break;
		default:
			break;
		}
	}
}
