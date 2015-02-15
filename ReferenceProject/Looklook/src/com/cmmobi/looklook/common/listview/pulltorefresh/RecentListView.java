package com.cmmobi.looklook.common.listview.pulltorefresh;

import java.util.ArrayList;
import java.util.Date;

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
import cn.zipper.framwork.core.ZApplication;

import com.cmmobi.looklook.R;
import com.cmmobi.looklook.activity.DiaryDetailActivity;
import com.cmmobi.looklook.activity.HomepageCommentActivity;
import com.cmmobi.looklook.activity.HomepageOtherDiaryActivity;
import com.cmmobi.looklook.common.gson.GsonResponse2.MyDiary;
import com.cmmobi.looklook.common.gson.GsonResponse2.diarycommentlistItem;
import com.cmmobi.looklook.common.utils.CmmobiClickAgentWrapper;
import com.cmmobi.looklook.common.utils.DateUtils;
import com.cmmobi.looklook.common.view.ClockView;
import com.cmmobi.looklook.common.web.TackView;
import com.cmmobi.looklook.common.web.WebImageView;
import com.cmmobi.looklook.info.profile.ActiveAccount;
import com.cmmobi.looklook.info.profile.DiaryManager;
import com.cmmobi.looklook.info.profile.DiaryManager.FilterType;
import com.cmmobi.looklook.prompt.Prompt;

/**
 * @author wuxiang
 * @date 2013-6-1
 */
public class RecentListView extends AbsRefreshView<Object> implements
		OnClickListener {

	private static final String TAG = "RecentListView";

	public RecentListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public RecentListView(Context context) {
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
	private LinearLayout llEnshrinelist;
	private LinearLayout llPraiselist;
	private LinearLayout llCommentlist;

	private void init() {
		rootView = inflater.inflate(
				R.layout.activity_homepage_myself_recent_content, null);
		llShare = (LinearLayout) rootView.findViewById(R.id.ll_share);
		llEnshrine = (LinearLayout) rootView.findViewById(R.id.ll_enshrine);
		llPraise = (LinearLayout) rootView.findViewById(R.id.ll_praise);
		llComment = (LinearLayout) rootView.findViewById(R.id.ll_comment);
		ivShare=(ImageView) rootView.findViewById(R.id.iv_share);
		ivEnshrine=(ImageView) rootView.findViewById(R.id.iv_enshrine);
		ivPraise=(ImageView) rootView.findViewById(R.id.iv_praise);
		ivComment=(ImageView) rootView.findViewById(R.id.iv_comment);
		llShare.setOnClickListener(this);
		llEnshrine.setOnClickListener(this);
		llPraise.setOnClickListener(this);
		llComment.setOnClickListener(this);

		llSharelist = (LinearLayout) rootView.findViewById(R.id.ll_sharelist);
		llEnshrinelist = (LinearLayout) rootView
				.findViewById(R.id.ll_enshrinelist);
		llPraiselist = (LinearLayout) rootView.findViewById(R.id.ll_praiselist);
		llCommentlist = (LinearLayout) rootView
				.findViewById(R.id.ll_commentlist);
		addChild(rootView, 1);
	}
	
	public void setDefaultChecked(){
		Log.d(TAG, "setDefaultChecked");
		checkedView(FilterType.INTERACT_SHARE);
	}

	private FilterType type;
	
	public void clearType(){
		type=null;
	}
	public FilterType getRecentType(){
		return type;
	}

	public interface OnInitLinstener{
		void init();
	}
	
	private OnInitLinstener initLinstener;
	public void setOnInitLinstener(OnInitLinstener initLinstener){
		this.initLinstener=initLinstener;
	}
	
	private View Vtitle;
	private View vComment;
	public void setJiaoBiaoClearLayout(View Vtitle,View vComment){
		this.Vtitle=Vtitle;
		this.vComment=vComment;
	}

	public long startTime;
	private boolean shareFirst;
	private boolean enshrineFirst;
	private boolean praiseFirst;
	private boolean commentFirst;
	private void checkedView(FilterType type) {

		switch (type) {
		case INTERACT_SHARE:// 分享页
			Log.d(TAG, "SHARE");
			this.type = type;
			if(initLinstener!=null)initLinstener.init();
			Prompt.showProgressDialog(getContext());
			llShare.setBackgroundResource(R.drawable.qiehuan_xuanzhong_1);
			ivShare.setImageResource(R.drawable.qiehuan_fenxiang_2);
			ivEnshrine.setImageResource(R.drawable.qiehuan_shoucang);
			ivPraise.setImageResource(R.drawable.qiehuan_zan);
			ivComment.setImageResource(R.drawable.qiehuan_pinglun);
			llEnshrine.setBackgroundDrawable(null);
			llPraise.setBackgroundDrawable(null);
			llComment.setBackgroundDrawable(null);
			onEventDuration(FilterType.INTERACT_SHARE);
			llSharelist.setVisibility(View.VISIBLE);
			llEnshrinelist.setVisibility(View.GONE);
			llPraiselist.setVisibility(View.GONE);
			llCommentlist.setVisibility(View.GONE);
			CmmobiClickAgentWrapper.onEvent(getContext(), "ma_intetr", "1");
			break;
		case INTERACT_COLLECT:// 收藏页
			Log.d(TAG, "ENSHRINE");
			this.type = type;
			if(initLinstener!=null)initLinstener.init();
			Prompt.showProgressDialog(getContext());
			llShare.setBackgroundDrawable(null);
			llEnshrine.setBackgroundResource(R.drawable.qiehuan_xuanzhong_1);
			ivShare.setImageResource(R.drawable.qiehuan_fenxiang);
			ivEnshrine.setImageResource(R.drawable.qiehuan_shoucang_2);
			ivPraise.setImageResource(R.drawable.qiehuan_zan);
			ivComment.setImageResource(R.drawable.qiehuan_pinglun);
			llPraise.setBackgroundDrawable(null);
			llComment.setBackgroundDrawable(null);
			onEventDuration(FilterType.INTERACT_COLLECT);
			llSharelist.setVisibility(View.GONE);
			llEnshrinelist.setVisibility(View.VISIBLE);
			llPraiselist.setVisibility(View.GONE);
			llCommentlist.setVisibility(View.GONE);
			CmmobiClickAgentWrapper.onEvent(getContext(), "ma_intetr", "2");
			break;
		case INTERACT_PRAISE:// 赞页
			Log.d(TAG, "PRAISE");
			this.type = type;
			if(initLinstener!=null)initLinstener.init();
			Prompt.showProgressDialog(getContext());
			llShare.setBackgroundDrawable(null);
			llEnshrine.setBackgroundDrawable(null);
			llPraise.setBackgroundResource(R.drawable.qiehuan_xuanzhong_1);
			ivShare.setImageResource(R.drawable.qiehuan_fenxiang);
			ivEnshrine.setImageResource(R.drawable.qiehuan_shoucang);
			ivPraise.setImageResource(R.drawable.qiehuan_zan_2);
			ivComment.setImageResource(R.drawable.qiehuan_pinglun);
			llComment.setBackgroundDrawable(null);
			onEventDuration(FilterType.INTERACT_PRAISE);
			llSharelist.setVisibility(View.GONE);
			llEnshrinelist.setVisibility(View.GONE);
			llPraiselist.setVisibility(View.VISIBLE);
			llCommentlist.setVisibility(View.GONE);
			CmmobiClickAgentWrapper.onEvent(getContext(), "ma_intetr", "3");
			break;
		case INTERACT_COMMENT:// 评论页
			Log.d(TAG, "COMMENT");
			if(Vtitle!=null)Vtitle.setVisibility(View.INVISIBLE);
			if(vComment!=null)vComment.setVisibility(View.INVISIBLE);
			DiaryManager.commentCount=0;
			this.type = type;
			if(initLinstener!=null)initLinstener.init();
			Prompt.showProgressDialog(getContext());
			llShare.setBackgroundDrawable(null);
			llEnshrine.setBackgroundDrawable(null);
			llPraise.setBackgroundDrawable(null);
			llComment.setBackgroundResource(R.drawable.qiehuan_xuanzhong_1);
			ivShare.setImageResource(R.drawable.qiehuan_fenxiang);
			ivEnshrine.setImageResource(R.drawable.qiehuan_shoucang);
			ivPraise.setImageResource(R.drawable.qiehuan_zan);
			ivComment.setImageResource(R.drawable.qiehuan_pinglun_2);
			onEventDuration(FilterType.INTERACT_COMMENT);
			llSharelist.setVisibility(View.GONE);
			llEnshrinelist.setVisibility(View.GONE);
			llPraiselist.setVisibility(View.GONE);
			llCommentlist.setVisibility(View.VISIBLE);
			CmmobiClickAgentWrapper.onEvent(getContext(), "ma_intetr", "4");
			break;
		default:
			break;
		}
	}
	
	private void onEventDuration(FilterType filterType){
		if(View.VISIBLE==llSharelist.getVisibility()&&0!=startTime&&filterType!=FilterType.INTERACT_SHARE){
			long duration=System.currentTimeMillis()-startTime;
			Log.d(TAG, "llSharelist->duration="+duration);
			CmmobiClickAgentWrapper.onEventDuration(getContext(),"ma_intetr", "1", duration);
		}
		if(View.VISIBLE==llEnshrinelist.getVisibility()&&0!=startTime&&filterType!=FilterType.INTERACT_COLLECT){
			long duration=System.currentTimeMillis()-startTime;
			Log.d(TAG, "llEnshrinelist->duration="+duration);
			CmmobiClickAgentWrapper.onEventDuration(getContext(),"ma_intetr", "2", duration);
		}
			
		if(View.VISIBLE==llPraiselist.getVisibility()&&0!=startTime&&filterType!=FilterType.INTERACT_PRAISE){
			long duration=System.currentTimeMillis()-startTime;
			Log.d(TAG, "llPraiselist->duration="+duration);
			CmmobiClickAgentWrapper.onEventDuration(getContext(),"ma_intetr", "3", duration);
		}
			
		if(View.VISIBLE==llCommentlist.getVisibility()&&0!=startTime&&filterType!=FilterType.INTERACT_COMMENT){
			long duration=System.currentTimeMillis()-startTime;
			Log.d(TAG, "llCommentlist->duration="+duration);
			CmmobiClickAgentWrapper.onEventDuration(getContext(),"ma_intetr", "4", duration);
		}
		startTime=System.currentTimeMillis();
	}

	protected LinearLayout llShare;
	protected LinearLayout llEnshrine;
	protected LinearLayout llPraise;
	protected LinearLayout llComment;
	private ImageView ivShare;
	private ImageView ivEnshrine;
	private ImageView ivPraise;
	private ImageView ivComment;

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
		case R.id.iv_target_pic:
			if(v.getTag()!=null){
				String diaryID=v.getTag().toString();
//				DiaryManager.getInstance().setDetailDiaryList(getDiaryList());
				context.startActivity(new Intent(context,DiaryDetailActivity.class)
				.putExtra(DiaryDetailActivity.INTENT_ACTION_DIARY_ID, diaryID));
			}
			break;
		case R.id.iv_portrait:
			if(v.getTag()!=null){
				String userID=v.getTag().toString();
				String myuserID=DiaryManager.getInstance().getMyUserID();
				if(!userID.equals(myuserID)){
					context.startActivity(new Intent(context,HomepageOtherDiaryActivity.class)
					.putExtra(HomepageOtherDiaryActivity.INTENT_ACTION_USERID, userID));
				}
			}
			break;
		case R.id.rl_recent_comment:
			if(v.getTag()!=null){
				diarycommentlistItem item=(diarycommentlistItem) v.getTag();
				getContext().startActivity(new Intent(getContext(),HomepageCommentActivity.class).putExtra(HomepageCommentActivity.ACTION_DIARYID, item.diaryid));
			}
			break;
		case R.id.ll_biezhen:{//主体为图片或视频
			Log.d(TAG, "ll_biezhen");
			String url=(String) v.getTag();
			((TackView)v).setAudio(url,0);
			break;}
		case R.id.iv_recent_portrait:
			if(v.getTag()!=null){
				String otherUserID=v.getTag().toString();
				String myUserID = ActiveAccount.getInstance(ZApplication.getInstance()).getUID();
				if(!otherUserID.equals(myUserID)){
					context.startActivity(new Intent(context,HomepageOtherDiaryActivity.class)
					.putExtra(HomepageOtherDiaryActivity.INTENT_ACTION_USERID, otherUserID));
				}else{
					Log.d(TAG, "userID is self="+otherUserID);
				}
			}else{
				Log.e(TAG, "iv_recent_portrait->userid is null");
			}
			break;
		case R.id.ll_share:
			checkedView(FilterType.INTERACT_SHARE);
			break;
		case R.id.ll_enshrine:
			checkedView(FilterType.INTERACT_COLLECT);
			break;
		case R.id.ll_praise:
			checkedView(FilterType.INTERACT_PRAISE);
			break;
		case R.id.ll_comment:
			checkedView(FilterType.INTERACT_COMMENT);
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
		case INTERACT_COLLECT:
			collectList.clear();
			break;
		case INTERACT_PRAISE:
			praiseList.clear();
			break;
		case INTERACT_COMMENT:
			commentList.clear();
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
		case INTERACT_COLLECT:
			llEnshrinelist.removeAllViews();
			break;
		case INTERACT_PRAISE:
			llPraiselist.removeAllViews();
			break;
		case INTERACT_COMMENT:
			llCommentlist.removeAllViews();
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
					try {
						porcessDiaryLayout(i, v,myDiaries.get(i));
						llSharelist.addView(v);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
			break;
		}
		case INTERACT_COLLECT: {
			if(items!=null&&items instanceof ArrayList){
				ArrayList<MyDiary> myDiaries=(ArrayList<MyDiary>) items;
				for(int i=0;i<myDiaries.size();i++){
					View v = inflater.inflate(
							R.layout.activity_homepage_recent_diary_item, null);
					try {
						porcessDiaryLayout(i, v,myDiaries.get(i));
						llEnshrinelist.addView(v);
					} catch (Exception e) {
						e.printStackTrace();
					}
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
					try {
						porcessDiaryLayout(i, v,myDiaries.get(i));
						llPraiselist.addView(v);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
			break;
		}
		case INTERACT_COMMENT: {
			if(items!=null&&items instanceof ArrayList){
				ArrayList<diarycommentlistItem> myComments=(ArrayList<diarycommentlistItem>) items;
				for(int i=0;i<myComments.size();i++){
					View v = inflater.inflate(
							R.layout.activity_homepage_recent_comment_item, null);
					try {
						porcessCommentLayout(i, v,myComments.get(i));
						llCommentlist.addView(v);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
			break;
		}
		default:
			break;
		}
	}

	ArrayList<MyDiary> shareList=new ArrayList<MyDiary>();
	ArrayList<MyDiary> collectList=new ArrayList<MyDiary>();
	ArrayList<MyDiary> praiseList=new ArrayList<MyDiary>();
	ArrayList<diarycommentlistItem> commentList=new ArrayList<diarycommentlistItem>();
	
	private String firstTime_share="";
	private String firstTime_collect="";
	private String firstTime_praise="";
	private String firstTime_comment="";
	
	private String lastTime_share="";
	private String lastTime_collect="";
	private String lastTime_praise="";
	private String lastTime_comment="";
	private void addToDiaryList(MyDiary myDiary){
		switch (type) {
		case INTERACT_SHARE:
			if(0==shareList.size())
				firstTime_share=myDiary.updatetimemilli;
			lastTime_share=myDiary.updatetimemilli;
			shareList.add(myDiary);
			break;
		case INTERACT_COLLECT:
			if(0==collectList.size())
				firstTime_collect=myDiary.updatetimemilli;
			lastTime_collect=myDiary.updatetimemilli;
			collectList.add(myDiary);
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
	
	private void addToCommentList(diarycommentlistItem item){
		if(0==commentList.size())
			firstTime_comment=item.createtime;
		lastTime_comment=item.createtime;
		commentList.add(item);
	}
	
	public String getFirstTime(){
		switch (type) {
		case INTERACT_SHARE:
			return firstTime_share;
		case INTERACT_COLLECT:
			return firstTime_collect;
		case INTERACT_PRAISE:
			return firstTime_praise;
		case INTERACT_COMMENT:
			return firstTime_comment;
		default:
			return "";
		}
	}
	
	public String getLastTime(){
		switch (type) {
		case INTERACT_SHARE:
			return lastTime_share;
		case INTERACT_COLLECT:
			return lastTime_collect;
		case INTERACT_PRAISE:
			return lastTime_praise;
		case INTERACT_COMMENT:
			return lastTime_comment;
		default:
			return "";
		}
	}
	
	private ArrayList getDiaryList(){
		switch (type) {
		case INTERACT_SHARE:
			return shareList;
		case INTERACT_COLLECT:
			return collectList;
		case INTERACT_PRAISE:
			return praiseList;
		default:
			return commentList;
		}
	}
	
	/**
	 * 根据filterType获取相应日记列表
	 */
	public ArrayList<MyDiary> getDiaryList(FilterType filterType){
		switch (filterType) {
		case INTERACT_SHARE:
			return shareList;
		case INTERACT_COLLECT:
			return collectList;
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
		ivWeather.setImageUrl(0, 1, myDiary.weather, false);
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
			
			String imageUrl=DiaryListView.getAttachUrl(myDiary.attachs[l].attachimage);
			String audioUrl=DiaryListView.getAttachUrl(myDiary.attachs[l].attachaudio);
			if(imageUrl!=null&&imageUrl.length()>0&&imageUrl.startsWith("http")){
				imageUrl+="&width="+show_width+"&heigh="+show_heigh;
			}
			Log.d(TAG, "videoCover="+videoCover);
			Log.d(TAG, "imageUrl="+imageUrl);
			Log.d(TAG, "audioUrl="+audioUrl);
			String mainText=textContent;
			
			if("1".equals(attachLevel)&&"1".equals(attachType)){//主内容为视频
				ivPic.setImageUrl(0, 1, videoCover, false);
				ivPic.setTag(videoCover);
				android.view.ViewGroup.LayoutParams params=ivPic.getLayoutParams();
				if(show_width!=0)
					params.width=show_width;
				if(show_heigh!=0)
					params.height=show_heigh;
				ivPic.setLayoutParams(params);
			}
			if("1".equals(attachLevel)&&"3".equals(attachType)){//主内容为图片
				if(imageUrl!=null&&imageUrl.length()>0){
					ivPic.setImageUrl(0, 1, imageUrl, false);
					ivPic.setTag(imageUrl);
				}else{
					ivPic.setImageUrl(0, 1, myDiary.attachs[l].attachuuid, false);
					ivPic.setTag(myDiary.attachs[l].attachuuid);
				}
				android.view.ViewGroup.LayoutParams params=ivPic.getLayoutParams();
				if(show_width!=0)
					params.width=show_width;
				if(show_heigh!=0)
					params.height=show_heigh;
				ivPic.setLayoutParams(params);
			}
			if("1".equals(attachLevel)&&"2".equals(attachType)){//主内容为音频  附件类型，1视频、2音频、3图片、4文字
				tvMainText.setText(DateUtils.getPlayTime(playtime));
			}
			if("1".equals(attachLevel)&&"4".equals(attachType)){//主内容为文字
				replacedExpressions(mainText, tvMainText);
//				tvMainText.setText(mainText);
			}
			
			if("0".equals(attachLevel)&&"2".equals(attachType)){//辅内容为音频
				llBiezhen.setPlaytime(DateUtils.getPlayTime(playtime));
				if(null==audioUrl||0==audioUrl.length()){
					audioUrl=myDiary.attachs[l].attachuuid;
				}
				llBiezhen.setTag(audioUrl);
			}
			
			if("0".equals(attachLevel)&&"4".equals(attachType)){//辅内容为文字
//				tvDescription.setText(mainText);
				replacedExpressions(mainText, tvDescription);
			}
			
			if(type==0x101||type==0x1){//辅文字显示在主体上
				if("0".equals(attachLevel)&&"4".equals(attachType)){//辅内容为文字
//					tvMainText.setText(mainText);
					replacedExpressions(mainText, tvMainText);
				}
			}
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

	private void porcessCommentLayout(int index, View v,diarycommentlistItem item) {
		if(null==item){
			Log.d(TAG, "porcessCommentLayout->item is null");
			return;
		}
		addToCommentList(item);
		View container= v.findViewById(R.id.rl_recent_comment);
		container.setTag(item);
		container.setOnClickListener(this);
		WebImageView ivPortrait = (WebImageView) v.findViewById(R.id.iv_portrait);
		ivPortrait.setTag(item.userid);
		ivPortrait.setOnClickListener(this);
		TextView tvNickname = (TextView) v.findViewById(R.id.tv_nickname);
		TextView tvTargetText = (TextView) v.findViewById(R.id.tv_target_text);// 评论对象文本信息
		WebImageView ivTargetPic = (WebImageView) v.findViewById(R.id.iv_target_pic);// 评论对象图片信息
		ivTargetPic.setTag(item.diaryid);
		ivTargetPic.setOnClickListener(this);
		View forceground=v.findViewById(R.id.iv_target_pic_forceground);
		TextView tvTargetPicText = (TextView) v
				.findViewById(R.id.iv_target_pic_text);// 评论对象图片描述信息
		
		TextView tvCommentText = (TextView) v
				.findViewById(R.id.tv_comment_text);//文字评论

		TackView llCommentRec = (TackView) v.findViewById(R.id.ll_comment_rec);// 评论layout
		llCommentRec.setOnClickListener(this);
		llCommentRec.setBackground(R.drawable.btn_activity_homepage_recent_short_record);
		View llRecTranslate = v.findViewById(R.id.ll_rec_translate);// 录音tts文字layout
		TextView tvRecTranslate = (TextView) v
				.findViewById(R.id.tv_rec_translate);// 录音tts文字

		TextView tvCommentTime = (TextView) v
				.findViewById(R.id.tv_comment_time);// 评论时间
		
//		ivPortrait.setImageUrl(item.headimageurl, 2, false);
		ivPortrait.setImageUrl(0, 1, item.headimageurl, true);
		tvNickname.setText(item.nickname);
		try {
			if(item.createtime!=null)
				tvCommentTime.setText(DateUtils.getMyCommonShowDate(new Date(Long.parseLong(item.createtime))));
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
		
//		tvCommentText.setText(item.commentcontent);
		replacedExpressions(item.commentcontent, tvCommentText);
		llCommentRec.setPlaytime(DateUtils.getPlayTime(item.playtime));
		llCommentRec.setTag(item.audiourl);
		tvRecTranslate.setText(item.commentcontent);
		if("1".equals(item.commentway)){//文字
			llCommentRec.setVisibility(View.GONE);
			llRecTranslate.setVisibility(View.GONE);
		}
		if("2".equals(item.commentway)){//声音
			tvCommentText.setVisibility(View.GONE);
			llRecTranslate.setVisibility(View.GONE);
		}
		if("3".equals(item.commentway)){//文字加声音
			tvCommentText.setVisibility(View.GONE);
		}
		
//		tvTargetText.setText(item.content.content);
		replacedExpressions(item.content.content, tvTargetText);
//		tvTargetPicText.setText(item.content.content);
		replacedExpressions(item.content.content, tvTargetPicText);
//		ivTargetPic.setTag(item.content.audiourl);
		if("1".equals(item.content.content_type)){//视频
			if(item.content.imageurl!=null)
//				ivTargetPic.setImageUrl(item.content.imageurl, 1, false);
				ivTargetPic.setImageUrl(0, 1, item.content.imageurl, false);
			forceground.setVisibility(View.VISIBLE);
			tvTargetText.setVisibility(View.GONE);
			tvTargetPicText.setVisibility(View.GONE);
		}else if("3".equals(item.content.content_type)){//图片
			if(item.content.imageurl!=null)
//				ivTargetPic.setImageUrl(item.content.imageurl, 1, false);
				ivTargetPic.setImageUrl(0, 1, item.content.imageurl, false);
			tvTargetText.setVisibility(View.GONE);
			tvTargetPicText.setVisibility(View.GONE);
			forceground.setVisibility(GONE);
		}else if("2".equals(item.content.content_type)){//长录音
			ivTargetPic.setImageResource(R.drawable.yulankuang_luyin);
			tvTargetText.setVisibility(View.GONE);
			tvTargetPicText.setVisibility(View.GONE);
			forceground.setVisibility(GONE);
		}else if("6".equals(item.content.content_type)){//长录音+文字
			ivTargetPic.setImageResource(R.drawable.yulankuang_luyin);
			tvTargetText.setVisibility(View.GONE);
			forceground.setVisibility(GONE);
		}else if("4,8".contains(item.content.content_type)){//文字
			ivTargetPic.setVisibility(View.GONE);
			tvTargetPicText.setVisibility(View.GONE);
			forceground.setVisibility(GONE);
		}else if("5,9".contains(item.content.content_type)){//短录音、语音评论
			ivTargetPic.setImageResource(R.drawable.yulankuang_luyin);
			tvTargetText.setVisibility(View.GONE);
			tvTargetPicText.setVisibility(View.GONE);
			forceground.setVisibility(GONE);
		}else if("7".equals(item.content.content_type)||"10".equals(item.content.content_type)){//短录音、语音评论+文字
			ivTargetPic.setImageResource(R.drawable.yulankuang_luyin);
			tvTargetPicText.setVisibility(View.GONE);
			forceground.setVisibility(GONE);
		}
	}
}
