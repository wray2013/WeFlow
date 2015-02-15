package com.cmmobi.looklook.common.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import cn.zipper.framwork.core.ZApplication;
import cn.zipper.framwork.core.ZToast;

import com.cmmobi.looklook.R;
import com.cmmobi.looklook.activity.DiaryPreviewActivity;
import com.cmmobi.looklook.activity.FootMarkActivity;
import com.cmmobi.looklook.activity.MediaScanActivity;
import com.cmmobi.looklook.activity.NewCommentsActivity;
import com.cmmobi.looklook.activity.SettingPersonalInfoActivity;
import com.cmmobi.looklook.activity.SpaceCoverActivity;
import com.cmmobi.looklook.activity.login.MicShareUserLoginActivity;
import com.cmmobi.looklook.common.gson.GsonResponse3.MyBind;
import com.cmmobi.looklook.common.gson.GsonResponse3.MyDiary;
import com.cmmobi.looklook.common.gson.GsonResponse3.MyDiaryList;
import com.cmmobi.looklook.common.utils.CmmobiClickAgentWrapper;
import com.cmmobi.looklook.common.utils.DateUtils;
import com.cmmobi.looklook.common.utils.DisplayUtil;
import com.cmmobi.looklook.common.view.ContentThumbnailView;
import com.cmmobi.looklook.common.view.FriendsExpressionView;
import com.cmmobi.looklook.fragment.MyZoneFragment;
import com.cmmobi.looklook.fragment.MyZoneFragment.DiariesItem;
import com.cmmobi.looklook.fragment.MyZoneFragment.MyZoneItem;
import com.cmmobi.looklook.fragment.MyZoneFragment.UserInfo;
import com.cmmobi.looklook.fragment.ZoneBaseFragment;
import com.cmmobi.looklook.info.profile.AccountInfo;
import com.cmmobi.looklook.info.profile.ActiveAccount;
import com.cmmobi.looklook.info.profile.DiaryManager;
import com.cmmobi.looklook.info.profile.LoginSettingManager;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;

/**
 * @author wuxiang
 * @email wuxiang@cmmobi.com
 * @date 2013-10-29
 */
public class MyZoneListAdapter extends BaseAdapter implements OnLongClickListener,OnClickListener{

	private static final String TAG = MyZoneListAdapter.class.getSimpleName();
	private ZoneBaseFragment zoneBaseFragment;
	ArrayList<MyZoneItem> myZoneItems=new ArrayList<MyZoneItem>();
	private LayoutInflater inflater;
	protected DisplayMetrics dm = new DisplayMetrics();
	private Context context;
	private int layoutWidth;
	private int margin=5;
	private DisplayImageOptions options;
	private DisplayImageOptions bgOptions;
	protected ImageLoader imageLoader;
	private ImageLoadingListener animateFirstListener;
	private String userID;
	private AccountInfo accountInfo;
	private MyZoneFragment myZoneFragment;
	private int height;
	private String lastBackgroundUrl;
	private boolean isActiveRefreshing = false;
	private Animation waitanim;
	
	public MyZoneListAdapter(ZoneBaseFragment zoneBaseFragment,ArrayList<MyZoneItem> myZoneItems,MyZoneFragment myZoneFragment) {
		userID= ActiveAccount.getInstance(ZApplication.getInstance()).getUID();
		accountInfo = AccountInfo.getInstance(userID);
		this.zoneBaseFragment = zoneBaseFragment;
		this.myZoneFragment=myZoneFragment;
		context=myZoneFragment.getActivity();
		this.myZoneItems=myZoneItems;
		inflater = LayoutInflater.from(context);
		(myZoneFragment.getActivity()).getWindowManager().getDefaultDisplay()
		.getMetrics(dm);
		layoutWidth=(dm.widthPixels-margin*4)/3;
		height=BitmapFactory.decodeResource(context.getResources(), R.drawable.kongjian_morentouxiang).getHeight();
		imageLoader = ImageLoader.getInstance();
		if(!imageLoader.isInited())
			imageLoader.init(ImageLoaderConfiguration.createDefault(context));
		
		int roundpx = dm.widthPixels * 3 / 640;
		
		options = new DisplayImageOptions.Builder()
//		.showStubImage(/*R.drawable.kongjian_morentouxiang*/0)
		.showImageForEmptyUri(R.drawable.kongjian_morentouxiang)
		.showImageOnFail(R.drawable.kongjian_morentouxiang)
		.cacheInMemory(true)
		.cacheOnDisc(true)
		.displayer(new RoundedBitmapDisplayer(roundpx <= 0 ? 3 : roundpx))
//		.displayer(new CircularBitmapDisplayer())
		.build();
		
		bgOptions = new DisplayImageOptions.Builder()
		.showStubImage(/*R.drawable.moren_kongjianfengmian*/0)
		/*.showImageForEmptyUri(R.drawable.moren_kongjianfengmian)
		.showImageOnFail(R.drawable.moren_kongjianfengmian)*/
		.cacheInMemory(true)
		.cacheOnDisc(true)
		.displayer(new SimpleBitmapDisplayer())
//		.displayer(new CircularBitmapDisplayer())
		.build();
		
		LinearInterpolator lir = new LinearInterpolator();  
		waitanim = AnimationUtils.loadAnimation(context, R.anim.map_waiting_animation);
		waitanim.setInterpolator(lir);
	}
	
	/**
	 * 获取日记宽度
	 */
	public String getDiaryWidth(){
		return layoutWidth+"";
	}
	
	public void setRefreshing(boolean isrefresh) {
		isActiveRefreshing = isrefresh;
	}
	
	public void setList(ArrayList<MyZoneItem> myZoneItems){
		this.myZoneItems=myZoneItems;
	}

	@Override
	public int getCount() {
		return myZoneItems.size();
	}

	@Override
	public Object getItem(int position) {
		return myZoneItems.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}
	
	public View getItem(){
		return convertView;
	}

	private View convertView;
	private View vCheckedBackground;
	private boolean isHeadShow = false;
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (0 == position) {// 空间背景布局
			ViewHolderUserInfo viewHolderUserInfo=null;
			if(null==convertView||null==convertView.getTag(R.layout.include_my_zone_background)){
				viewHolderUserInfo=new ViewHolderUserInfo();
				convertView = inflater.inflate(R.layout.include_my_zone_background,
						null);
				
				viewHolderUserInfo.llUserinfo=convertView.findViewById(R.id.ll_userinfo);
				viewHolderUserInfo.tvToday=(TextView) convertView.findViewById(R.id.tv_today);
				viewHolderUserInfo.ivHeadUrl=(ImageView) convertView.findViewById(R.id.iv_head);
				viewHolderUserInfo.ivHeadBackground = (ImageView) convertView.findViewById(R.id.iv_head_background);
				viewHolderUserInfo.ivBackground=(ImageView) convertView.findViewById(R.id.iv_background);
				viewHolderUserInfo.tvSignature=(TextView) convertView.findViewById(R.id.tv_signature);
				viewHolderUserInfo.tvNickname=(TextView) convertView.findViewById(R.id.tv_nickname);
				viewHolderUserInfo.ivSex=(ImageView) convertView.findViewById(R.id.iv_sex);
				viewHolderUserInfo.llComment=convertView.findViewById(R.id.ll_comment);
				viewHolderUserInfo.tvComment=(TextView) convertView.findViewById(R.id.tv_comment);
				viewHolderUserInfo.ivImportPic=(ImageView) convertView.findViewById(R.id.iv_import_pic);
				viewHolderUserInfo.llSignature = (LinearLayout) convertView.findViewById(R.id.ll_signature);
				vCheckedBackground=convertView.findViewById(R.id.rl_checked);
//				viewHolderUserInfo.shareTrace=convertView.findViewById(R.id.iv_share_trace);
				viewHolderUserInfo.footmark=convertView.findViewById(R.id.iv_footmark);
				viewHolderUserInfo.ivNoData = (ImageView) convertView.findViewById(R.id.iv_nodata);
				viewHolderUserInfo.ivLoading = (ImageView) convertView.findViewById(R.id.iv_waiting);
//				viewHolderUserInfo.collect=convertView.findViewById(R.id.iv_collect);
//				viewHolderUserInfo.extend_1=convertView.findViewById(R.id.iv_extend_1);
//				viewHolderUserInfo.extend=convertView.findViewById(R.id.iv_extend);
//				viewHolderUserInfo.ivExtendContent=convertView.findViewById(R.id.ll_extend_content);
//				viewHolderUserInfo.ivWeatherIcon = (WebImageView) convertView.findViewById(R.id.iv_weather_icon);
//				viewHolderUserInfo.tvWeatherTemp = (TextView) convertView.findViewById(R.id.tv_weather_temp);
//				viewHolderUserInfo.tvWeatherRegion = (TextView) convertView.findViewById(R.id.tv_weather_region);
				convertView.setTag(R.layout.include_my_zone_background, viewHolderUserInfo);
			}else{
				viewHolderUserInfo=(ViewHolderUserInfo) convertView.getTag(R.layout.include_my_zone_background);
			}
			
//			if(viewHolderUserInfo.ivLoading != null) {
//				viewHolderUserInfo.ivLoading.setVisibility(View.VISIBLE);
//				viewHolderUserInfo.ivLoading.startAnimation(waitanim);
//			}
			
			LayoutParams params=viewHolderUserInfo.ivHeadBackground.getLayoutParams();
			params.height=dm.widthPixels * 15 / 64;
			params.width=params.height;
			viewHolderUserInfo.ivHeadBackground.setLayoutParams(params);
			Log.e(TAG, "height = " + params.height + ";pixwidth = " + dm.widthPixels);
			
			int nicknamesize = DisplayUtil.px2sp(context, dm.widthPixels * 34 / 640);
			int signsize = DisplayUtil.px2sp(context, dm.widthPixels * 24 / 640);
			
			viewHolderUserInfo.tvNickname.setTextSize(nicknamesize);
			viewHolderUserInfo.tvSignature.setTextSize(signsize);
			
			
			android.widget.RelativeLayout.LayoutParams commentParams=(android.widget.RelativeLayout.LayoutParams)viewHolderUserInfo.llComment.getLayoutParams();
			commentParams.topMargin=dm.widthPixels * 62 / 640;
			viewHolderUserInfo.llComment.setLayoutParams(commentParams);
			
			viewHolderUserInfo.llComment.setOnClickListener(this);
			viewHolderUserInfo.ivBackground.setOnClickListener(this);
			viewHolderUserInfo.llUserinfo.setOnClickListener(this);
			viewHolderUserInfo.ivImportPic.setOnClickListener(this);
			viewHolderUserInfo.footmark.setOnClickListener(this);
			
			if(this.myZoneItems!=null && this.myZoneItems.size()>1){
				viewHolderUserInfo.ivNoData.setVisibility(View.GONE);
			}else{
				viewHolderUserInfo.ivNoData.setVisibility(View.VISIBLE);
			}
			
			if(myZoneItems.size()>1){
				MyZoneItem myZoneItem=myZoneItems.get(1);
				if(myZoneItem instanceof DiariesItem){
					if("今天".equals(((DiariesItem) myZoneItem).strDate)){
						viewHolderUserInfo.tvToday.setVisibility(View.GONE);
					}else{
						if(((DiariesItem) myZoneItem).diaryGroups.size()>0){
							MyDiaryList myDiaryList =((DiariesItem) myZoneItem).diaryGroups.get(0);
							if(DateUtils.isNum(myDiaryList.create_time)){
								long createTime=Long.parseLong(myDiaryList.create_time);
								long dtTime=System.currentTimeMillis()-createTime;
								long days=dtTime/(3600*1000*24);
								if(days>=7&&days<14){//超过一个星期
									viewHolderUserInfo.tvToday.setVisibility(View.VISIBLE);
									viewHolderUserInfo.tvToday.setHint(R.string.myzone_hint_2);
								}else if(days>=14){//超过两个星期
									viewHolderUserInfo.tvToday.setVisibility(View.VISIBLE);
									viewHolderUserInfo.tvToday.setHint(R.string.myzone_hint_3);
								}else{
									viewHolderUserInfo.tvToday.setVisibility(View.VISIBLE);
									viewHolderUserInfo.tvToday.setHint(R.string.myzone_hint_1);
								}
							}
						}
					}
				}
			}else{
				viewHolderUserInfo.tvToday.setVisibility(View.VISIBLE);
				viewHolderUserInfo.tvToday.setHint(R.string.myzone_hint_1);
			}
			LayoutParams backgroundParams=viewHolderUserInfo.ivBackground.getLayoutParams();
			if(h < 0) {
				h = /*backgroundParams.height*/dm.widthPixels * 372 / 640;
				Log.d("xxx", "gBackgroundParams is given value = " + h);
			}
			backgroundParams.width=dm.widthPixels;
			backgroundParams.height=(int)(backgroundParams.width*0.6f);
			if(currentHeight > 0) {
				backgroundParams.height = currentHeight;
			}
			viewHolderUserInfo.ivBackground.setLayoutParams(backgroundParams);
			android.widget.RelativeLayout.LayoutParams userinfoParams=(android.widget.RelativeLayout.LayoutParams) viewHolderUserInfo.llUserinfo.getLayoutParams();
//			userinfoParams.topMargin=(int)(backgroundParams.height-(height*0.74f));
			userinfoParams.topMargin = backgroundParams.height - (int)(dm.widthPixels * 114 / 640);
			viewHolderUserInfo.llUserinfo.setLayoutParams(userinfoParams);
			//布局初始化
			MyZoneItem zoneItem=myZoneItems.get(0);
			if(zoneItem instanceof UserInfo){
				setUserInfo((UserInfo)zoneItem,viewHolderUserInfo);
				
				final UserInfo userInfo = (UserInfo)zoneItem;
				final ViewHolderUserInfo holder = viewHolderUserInfo;
				ViewTreeObserver vto2 = viewHolderUserInfo.ivHeadUrl.getViewTreeObserver();   
				vto2.addOnGlobalLayoutListener(new OnGlobalLayoutListener() { 
					@Override   
					public void onGlobalLayout() {
						if(userInfo.headUrl!=null && !isHeadShow) {
							imageLoader.displayImageEx(userInfo.headUrl, holder.ivHeadUrl, options, animateFirstListener, userID, 1);
							isHeadShow = true;
						}
					}   
				});
			}
		} else {// 每天日记布局
			ViewHolder holder = null;
			ThumbnailViewHolder thumbnailViewHolder=null;
			if (convertView == null||null==convertView.getTag()) {
				convertView = inflater.inflate(
						R.layout.include_my_zone_diary_item, null);
				holder = new ViewHolder();
				thumbnailViewHolder=new ThumbnailViewHolder();
				holder.tvDate=(TextView) convertView.findViewById(R.id.tv_date);
				holder.tlDiaries=(TableLayout) convertView.findViewById(R.id.tl_diary_items);
				convertView.setTag(holder);
				holder.tlDiaries.setTag(thumbnailViewHolder);
//				for(int i=0;i<2;i++){
//				}
				TableRow tr=new TableRow(context);
				for(int j=0;j<3;j++){
					ContentThumbnailView view=new ContentThumbnailView(context);
					tr.addView(view);
					LayoutParams params=view.getLayoutParams();
					params.height=layoutWidth;
					params.width=layoutWidth;
					view.setLayoutParams(params);
					switch (j) {
					case 0:
						view.setPadding(0, margin, margin, margin);
						break;
					case 2:
						view.setPadding(margin, margin, 0, margin);
						break;
					case 1:
					default:
						view.setPadding(margin, margin, margin, margin);
					}
					view.setOnLongClickListener(this);
					view.setOnClickListener(this);
					thumbnailViewHolder.thumbnailViews.add(view);
				}
				holder.tlDiaries.addView(tr);
			} else {
				holder = (ViewHolder) convertView.getTag();
				thumbnailViewHolder=(ThumbnailViewHolder) holder.tlDiaries.getTag();
			}
			
			MyZoneItem myZoneItem=myZoneItems.get(position);
			if(myZoneItem instanceof DiariesItem){
				DiariesItem diariesItem=(DiariesItem) myZoneItem;
				ArrayList<MyDiaryList> myDiaryGroups=diariesItem.diaryGroups;
				ArrayList<MyDiary[]> diaries=diariesItem.diaries;
				int itemSize=diaries.size();
				//当可重用个数大于当前需要显示的元素时，删除多余的
				int length=thumbnailViewHolder.thumbnailViews.size();
				for(int i=itemSize;i<length;i++){
					ContentThumbnailView contentThumbnailView=thumbnailViewHolder.thumbnailViews.get(i);
					contentThumbnailView.setVisibility(View.GONE);
				}
				
				int totalRows=itemSize%3==0?itemSize/3:itemSize/3+1;
				int count=0;
				int trCount=holder.tlDiaries.getChildCount();
				for(int i=0;i<(itemSize>length?length:itemSize);i++){
					ContentThumbnailView contentThumbnailView=thumbnailViewHolder.thumbnailViews.get(i);
					contentThumbnailView.setVisibility(View.VISIBLE);
					contentThumbnailView.setContentDiaries(myDiaryGroups.get(count).getFilterShareInfo(), myDiaryGroups.get(count).join_safebox, diaries.get(count));
					contentThumbnailView.setTag(myDiaryGroups.get(count));
					if(checkedList.contains(myDiaryGroups.get(count))){//设置选中状态
						contentThumbnailView.setViewSelected(true);
					}else{
						contentThumbnailView.setViewSelected(false);
					}
					count++;
				}
				/*//现有行中新增元素
				for(int i=0;i<trCount;i++){
					for(int j=0;j<3;j++){
						int itemCount=thumbnailViewHolder.thumbnailViews.size();
						if(i*3+(j+1)>itemCount&&itemCount<itemSize){//新增元素
							ContentThumbnailView view=new ContentThumbnailView(context);
							TableRow tr= (TableRow) holder.tlDiaries.getChildAt(i);
							tr.addView(view);
							LayoutParams params=view.getLayoutParams();
							params.height=layoutWidth;
							params.width=layoutWidth;
							view.setLayoutParams(params);
							view.setPadding(margin, margin, margin, margin);
							view.setOnLongClickListener(this);
							view.setOnClickListener(this);
							view.setContentDiaries(myDiaryGroups.get(count).getFilterShareInfo(),myDiaryGroups.get(count).join_safebox, diaries.get(count));
							view.setTag(myDiaryGroups.get(count));
							if(checkedList.contains(myDiaryGroups.get(count))){//设置选中状态
								view.setViewSelected(true);
							}else{
								view.setViewSelected(false);
							}
							thumbnailViewHolder.thumbnailViews.add(view);
							count++;
						}
					}
				}
				//新增行中增加新元素
				for(int i=0;i<totalRows-trCount;i++){
					TableRow tr=new TableRow(context);
					for(int j=0;j<3;j++){
						if(thumbnailViewHolder.thumbnailViews.size()<itemSize){
							ContentThumbnailView view=new ContentThumbnailView(context);
							tr.addView(view);
							LayoutParams params=view.getLayoutParams();
							params.height=layoutWidth;
							params.width=layoutWidth;
							view.setLayoutParams(params);
							view.setPadding(margin, margin, margin, margin);
							view.setOnLongClickListener(this);
							view.setOnClickListener(this);
							view.setContentDiaries(myDiaryGroups.get(count).getFilterShareInfo(), myDiaryGroups.get(count).join_safebox, diaries.get(count));
							view.setTag(myDiaryGroups.get(count));
							if(checkedList.contains(myDiaryGroups.get(count))){//设置选中状态
								view.setViewSelected(true);
							}else{
								view.setViewSelected(false);
							}
							count++;
							thumbnailViewHolder.thumbnailViews.add(view);
						}
					}
					holder.tlDiaries.addView(tr);
				}*/
				if("今天".equals(diariesItem.strDate)){
					holder.tvDate.setVisibility(View.GONE);
				}else{
					if(position-1>0){
						MyZoneItem last=myZoneItems.get(position-1);
						if(last instanceof DiariesItem){
							if(!((DiariesItem) last).strDate.equals(diariesItem.strDate)){
								holder.tvDate.setVisibility(View.VISIBLE);
//								holder.tvDate.setText(diariesItem.strDate);
								holder.tvDate.setText(diariesItem.textStyle);
							}else{
								holder.tvDate.setVisibility(View.GONE);
							}
						}
					}else{
						holder.tvDate.setVisibility(View.VISIBLE);
//						holder.tvDate.setText(diariesItem.strDate);
						holder.tvDate.setText(diariesItem.textStyle);
					}
				}
			}
		}
		this.convertView=convertView;
		return convertView;
	}
	
//	private WebImageView ivWeatherIcon;// 天气图标
//	private TextView tvWeatherTemp;// 天气温度
//	private TextView tvWeatherRegion;// 天气区域
    
	@Override
	public void notifyDataSetChanged() {
		// TODO Auto-generated method stub
		super.notifyDataSetChanged();

	}

	private boolean needRefreshBg(UserInfo userInfo) {
		boolean need = false;
		if(userInfo.backgroundUrl!=null) {
			//主动刷新
			if(isActiveRefreshing) {
				//url和上次一样
				if(lastBackgroundUrl != null && lastBackgroundUrl.equalsIgnoreCase(userInfo.backgroundUrl)) {
//					ZToast.showLong("url和上次一样，不刷新！");
				} else {
//					ZToast.showLong("url和上次不一样，要刷新！");
					need = true;
				}
			} else {
//				ZToast.showLong("非下拉刷新，要刷新！");
				need = true;
			}
		}
		return need;
	}
	
	//设置用户信息
	private void setUserInfo(UserInfo userInfo,ViewHolderUserInfo viewHolderUserInfo){
		gHolderUserInfo = viewHolderUserInfo;
		if(userInfo.headUrl!=null && !NotUpdatePortrait)
			imageLoader.displayImageEx(userInfo.headUrl, viewHolderUserInfo.ivHeadUrl, options, animateFirstListener, userID, 1);
		if(needRefreshBg(userInfo)){
			imageLoader.displayImageEx(userInfo.backgroundUrl, viewHolderUserInfo.ivBackground, bgOptions, animateFirstListener, userID, 1);
			lastBackgroundUrl = userInfo.backgroundUrl;
		}
		isActiveRefreshing = false;
		NotUpdatePortrait = false;
		if(!TextUtils.isEmpty(userInfo.signature)){
			viewHolderUserInfo.llSignature.setVisibility(View.VISIBLE);
			FriendsExpressionView.replacedExpressions(userInfo.signature, viewHolderUserInfo.tvSignature);
		}else{
			viewHolderUserInfo.llSignature.setVisibility(View.GONE);
			viewHolderUserInfo.tvSignature.setText(null);
		}
		if(!TextUtils.isEmpty(userInfo.nickname)){
			FriendsExpressionView.replacedExpressions(userInfo.nickname, viewHolderUserInfo.tvNickname);
		}else{
			viewHolderUserInfo.tvNickname.setText(null);
		}
		if("0".equals(userInfo.sex)){
			viewHolderUserInfo.ivSex.setImageResource(R.drawable.nan);
			viewHolderUserInfo.ivSex.setVisibility(View.VISIBLE);
		}else if("1".equals(userInfo.sex)){
			viewHolderUserInfo.ivSex.setImageResource(R.drawable.nv);
			viewHolderUserInfo.ivSex.setVisibility(View.VISIBLE);
		}else{
			viewHolderUserInfo.ivSex.setVisibility(View.GONE);
		}
		if(0==userInfo.msgNum){
			viewHolderUserInfo.llComment.setVisibility(View.GONE);
		}else{
			viewHolderUserInfo.llComment.setVisibility(View.VISIBLE);
			String strComment=context.getResources().getString(R.string.has_new_comment, userInfo.msgNum);
			viewHolderUserInfo.tvComment.setText(strComment);
		}
		
//		if(userInfo.weathertype > 0 && userInfo.myweather != null)
//			showWeather(userInfo.weathertype, userInfo.myweather, viewHolderUserInfo);
	}
	
	private Boolean NotUpdatePortrait = false;
	private ViewHolderUserInfo gHolderUserInfo = null;
	private int h = -1, currentHeight = -1;
	public void enlargeBackgroudView(int value) {
//		Log.d("xxx", "enlargeBackgroudView value " + value);
		
		float delta = Math.abs(value);
		
		if (null != gHolderUserInfo && null != gHolderUserInfo.ivBackground) {
			LayoutParams backgroundParams=gHolderUserInfo.ivBackground.getLayoutParams();
			currentHeight = (int) (h + delta * 2);
			int critical = dm.heightPixels * 16 / 25;
			currentHeight = currentHeight > critical ? critical : currentHeight;
			backgroundParams.height = currentHeight;
//			Log.d("xxx", "backgroundParams.height value " + backgroundParams.height);
			gHolderUserInfo.ivBackground.setLayoutParams(backgroundParams);
			
			android.widget.RelativeLayout.LayoutParams userinfoParams=(android.widget.RelativeLayout.LayoutParams) gHolderUserInfo.llUserinfo.getLayoutParams();
			userinfoParams.topMargin = backgroundParams.height - (int)(dm.widthPixels * 114 / 640);
			gHolderUserInfo.llUserinfo.setLayoutParams(userinfoParams);
			
			android.widget.RelativeLayout.LayoutParams commentParams=(android.widget.RelativeLayout.LayoutParams) gHolderUserInfo.llComment.getLayoutParams();
			commentParams.topMargin = backgroundParams.height - (int)(dm.widthPixels * 310 / 640);
			gHolderUserInfo.llComment.setLayoutParams(commentParams);
		}
	}
	
	public void startLoading() {
		Log.d(TAG, "startLoading");
		if(gHolderUserInfo != null && gHolderUserInfo.ivLoading != null) {
			gHolderUserInfo.ivLoading.setVisibility(View.VISIBLE);
			gHolderUserInfo.ivLoading.startAnimation(waitanim);
		}
	}
	
	public void stopLoading() {
		Log.d(TAG, "stopLoading");
		if(gHolderUserInfo != null && gHolderUserInfo.ivLoading != null) {
			gHolderUserInfo.ivLoading.clearAnimation();
			gHolderUserInfo.ivLoading.setVisibility(View.GONE);
		}
	}
	
	/**
	 * 清楚选中状态
	 */
	public void purgeCheckedView(){
		checkedList.clear();
		NotUpdatePortrait = true;
		notifyDataSetChanged();
		if(vCheckedBackground!=null)
			vCheckedBackground.setVisibility(View.GONE);
	}
	
	/**
	 * 获取选中列表
	 */
	public ArrayList<MyDiaryList> getCheckedList(){
		return checkedList;
	}
	

	private ArrayList<MyDiaryList> checkedList=new ArrayList<MyDiaryList>(); 
	@Override
	public boolean onLongClick(View v) {
		if(v instanceof ContentThumbnailView){
			//1.判断v是否选中
			//2.未选中时，设置选中，同时记录选中数据
			if(!((ContentThumbnailView) v).getViewSelected()){
				((ContentThumbnailView) v).setViewSelected(true);
				if(v.getTag()!=null&&v.getTag() instanceof MyDiaryList)
					checkedList.add((MyDiaryList)v.getTag());
			}
			if(!zoneBaseFragment.isCheckedTitleShow()){
				zoneBaseFragment.showCheckedTitle();
				if(vCheckedBackground!=null)
					vCheckedBackground.setVisibility(View.VISIBLE);
			}
		}
		return true;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ll_comment:
			context.startActivity(new Intent(context,NewCommentsActivity.class));
			break;
		case R.id.ll_userinfo:
			//如果昵称或手机号未填写，则弹出选项，否则直接跳转个人信息页
			userID= ActiveAccount.getInstance(ZApplication.getInstance()).getUID();
			accountInfo = AccountInfo.getInstance(userID);
			if(accountInfo!=null){
				LoginSettingManager lsm = accountInfo.setmanager;
				MyBind mb=null;
				if(lsm!=null)
					mb= lsm.getBinding_type(LoginSettingManager.BINDING_TYPE_PHONE, "");
				if(mb!=null||!TextUtils.isEmpty(accountInfo.nickname)){
					context.startActivity(new Intent(context,SettingPersonalInfoActivity.class));
				}else{
					showHeadClick();
				}
			}else{
				Log.e(TAG, "account info exception!!!");
			}
			break;
		case R.id.btn_cancel:
			if(mPopupWindow!=null)
				mPopupWindow.dismiss();
			break;
		case R.id.btn_login:
			//登陆
			if(mPopupWindow!=null){
				mPopupWindow.dismiss();
			}
			context.startActivity(new Intent(context,MicShareUserLoginActivity.class));
			break;
		case R.id.btn_complete_userinfo:
			//完善用户信息
			if(mPopupWindow!=null){
				mPopupWindow.dismiss();
			}
			context.startActivity(new Intent(context,SettingPersonalInfoActivity.class));
			break;
//		case R.id.iv_extend:
//			Log.d(TAG, "iv_extend click");
//			ivExtendContent.setVisibility(View.VISIBLE);
//			ivExtend.setVisibility(View.GONE);
//			break;
//		case R.id.iv_extend_1:
//			ivExtendContent.setVisibility(View.GONE);
//			ivExtend.setVisibility(View.VISIBLE);
//			break;
//		case R.id.iv_share_trace:
//			Intent intent = new Intent(context, ShareTraceActivity.class);
//			context.startActivity(intent);
//			break;
		case R.id.iv_footmark:
			
			Log.d("==WR==", "我的足迹埋点【userid:" + userID + "】");
			CmmobiClickAgentWrapper.onEvent(context, "foot_print", userID);
			
			DiaryManager diarymanager = DiaryManager.getInstance();
			diarymanager.setFootmarkDiary(getMyZoneDiaries());
			Intent fm = new Intent(context,FootMarkActivity.class);
			fm.putExtra("uid", userID);
			context.startActivity(fm);
			Log.d(TAG, "iv_footmark click");
			break;
//		case R.id.iv_collect:
//			Log.d(TAG, "iv_collect");
////			zoneBaseFragment.switchContent(FragmentHelper.getInstance((FragmentActivity)context).getCollectionsFragment());
//			Intent cin = new Intent(context, CollectionsActivity.class);
//			CmmobiClickAgentWrapper.onEvent(context, "favorite_page");
//			context.startActivity(cin);
//			break;
//		case R.id.iv_weather_icon:
//			MyWeatherInfo.getInstance(ZApplication.getInstance()).updateWeather(true);
//			break;
		case R.id.iv_background:
			Log.d(TAG, "iv_background");
			context.startActivity(new Intent(context,SpaceCoverActivity.class));
			break;
		case R.id.iv_import_pic:{
			Intent intent = new Intent();
			intent.setClass(context, MediaScanActivity.class);
			intent.putExtra(MediaScanActivity.INTENT_SCAN_MODE, MediaScanActivity.MODE_PIC_NORMAL);
			context.startActivity(intent);
			//2014-4-8 wuxiang
			CmmobiClickAgentWrapper.onEvent(context, "import");
			break;}
		default:
			break;
		}
		
		if(v instanceof ContentThumbnailView){
			if(((ContentThumbnailView) v).getViewSelected()){
				((ContentThumbnailView) v).setViewSelected(false);
				checkedList.remove(v.getTag());
				if(0==checkedList.size()){
					zoneBaseFragment.showNormalTitle();
					if(vCheckedBackground!=null)
						vCheckedBackground.setVisibility(View.GONE);
				}
			}else{
				if(checkedList.size()>0){//选中删除模式
					((ContentThumbnailView) v).setViewSelected(true);
					if(v.getTag()!=null&&v.getTag() instanceof MyDiaryList)
						checkedList.add((MyDiaryList)v.getTag());
				}else{//未选中状态，单击跳转到详情
					Log.d(TAG, "onClick to detail tag="+v.getTag());
					if(v.getTag()!=null){
						MyDiaryList diaryGroup=(MyDiaryList) v.getTag();
						String groupUUID=diaryGroup.diaryuuid;
						ArrayList<MyDiaryList> diaryGroups=(ArrayList<MyDiaryList>) getDiaryGroup().clone();
						DiaryManager.getInstance().setDetailDiaryList(diaryGroups, 0);
						
						Intent intent = new Intent(context, DiaryPreviewActivity.class);
						intent.putExtra(DiaryPreviewActivity.INTENT_ACTION_DIARY_UUID, groupUUID);
						context.startActivity(intent);
					}
				}
			}
		}
	}
	
	private PopupWindow mPopupWindow;
	private void showHeadClick(){
		View view = inflater.inflate(R.layout.activity_myzone_headclick_menu ,
				null);
		mPopupWindow = new PopupWindow(view, LayoutParams.FILL_PARENT,
				LayoutParams.FILL_PARENT, true);
		mPopupWindow.setBackgroundDrawable(context.getResources().getDrawable(
				R.color.transparent));
		view.findViewById(R.id.btn_cancel).setOnClickListener(this);
		view.findViewById(R.id.btn_login).setOnClickListener(this);
		view.findViewById(R.id.btn_complete_userinfo).setOnClickListener(this);
		mPopupWindow.showAtLocation(myZoneFragment.getActivity().findViewById(R.id.ll_myzone), Gravity.BOTTOM, 0, 0);
	}
	
	private ArrayList<MyDiaryList> getDiaryGroup(){
		ArrayList<MyDiaryList> diaryGroups=new ArrayList<MyDiaryList>();
		if(myZoneItems!=null){
			for(int i=0;i<myZoneItems.size();i++){
				MyZoneItem item=myZoneItems.get(i);
				if(item instanceof DiariesItem){
					diaryGroups.addAll(((DiariesItem) item).diaryGroups);
				}
			}
		}
		return diaryGroups;
		
	}

	static class ViewHolder {
		TextView tvDate;
		TableLayout tlDiaries;
	}
	
	static class ViewHolderUserInfo{
		View llUserinfo;
		TextView tvToday;
		ImageView ivHeadUrl, ivHeadBackground;
		ImageView ivBackground;
		TextView tvSignature;
		TextView tvNickname;
		ImageView ivSex;
		View llComment;
		TextView tvComment;
		ImageView ivImportPic;
//		View shareTrace;
		View footmark;
		LinearLayout llSignature;
		ImageView ivNoData, ivLoading;
//		View collect;
//		View extend_1;
//		View extend;
//		View ivExtendContent;
//		WebImageView ivWeatherIcon;
//		TextView tvWeatherTemp;
//		TextView tvWeatherRegion;
	}
	
	static class ThumbnailViewHolder{
		ArrayList<ContentThumbnailView> thumbnailViews=new ArrayList<ContentThumbnailView>();
	}
	
	public ArrayList<MyDiary> getMyZoneDiaries() {
		ArrayList<MyDiary> wholeDiaries = new ArrayList<MyDiary>();
		for (int i = myZoneItems.size() - 1; i >= 0; i--) {
			MyZoneItem zoneItem = myZoneItems.get(i);
			if (zoneItem instanceof DiariesItem) {
				ArrayList<MyDiary[]> myDiaryLists=((DiariesItem) zoneItem).diaries;
				if(myDiaryLists!=null&&myDiaryLists.size()>0) {
					for(MyDiary[] diaries : myDiaryLists) {
						if(diaries != null && diaries.length > 0) {
							for(MyDiary tmpDiary : diaries) {
								wholeDiaries.add(tmpDiary);
							}
						}
					}
				}
			}
		}
		return wholeDiaries;
	}
	/*
	 * type 
	 *  1.??? 
	 *  2.未能获取位置 /天气 
	 *  3.27°C/36°C 汉阳区(武汉市)
	 
	public void showWeather(int type, MyWeather weather,ViewHolderUserInfo viewHolderUserInfo) {
		if (viewHolderUserInfo.tvWeatherRegion == null || viewHolderUserInfo.tvWeatherTemp == null
				|| viewHolderUserInfo.ivWeatherIcon == null) {
			return;
		}
		switch(type) {
		case 0:
			Log.d(TAG, "无位置信息，无法获取天气");
//			tvWeatherRegion.setText("???");
//			tvWeatherTemp.setTextSize(11);
//			tvWeatherTemp.setText("? °C/? °C");
//			ivWeatherIcon.setImageResource(R.drawable.tianqi_weizhi);
			viewHolderUserInfo.tvWeatherRegion.setVisibility(View.GONE);
			viewHolderUserInfo.tvWeatherTemp.setVisibility(View.GONE);
			viewHolderUserInfo.ivWeatherIcon.setVisibility(View.GONE);
			break;
		case 1:
			Log.d(TAG, "未打开GPS与基站(wifi)定位");
			viewHolderUserInfo.tvWeatherRegion.setVisibility(View.VISIBLE);
			viewHolderUserInfo.tvWeatherTemp.setVisibility(View.VISIBLE);
			viewHolderUserInfo.ivWeatherIcon.setVisibility(View.VISIBLE);
			viewHolderUserInfo.tvWeatherRegion.setText("未能获取");
			viewHolderUserInfo.tvWeatherTemp.setTextSize(10);
			viewHolderUserInfo.tvWeatherTemp.setText("位置信息");
			viewHolderUserInfo.ivWeatherIcon.setImageResource(R.drawable.del_tianqi_sorry);
			break;
		case 2:
			viewHolderUserInfo.tvWeatherRegion.setVisibility(View.VISIBLE);
			viewHolderUserInfo.tvWeatherTemp.setVisibility(View.VISIBLE);
			viewHolderUserInfo.ivWeatherIcon.setVisibility(View.VISIBLE);
			if(hasWeatherCache(weather)) {
				Log.d(TAG, "有天气缓存");
				String description = weather.desc[0].description;
				String weatherurl = weather.desc[0].weatherurl;
				String city = weather.city;
				String district = weather.district;
				if(district != null && city != null) {
					Log.d(TAG, "有地理位置");
//					String region = district + "(" + city + ")";
					String region = city + district;
					Log.d(TAG, "weatherurl=" + weatherurl + ";region=" + region + ";description=" + description);
					viewHolderUserInfo.tvWeatherRegion.setText(region);
					viewHolderUserInfo.tvWeatherTemp.setText(description);
					LayoutParams lp = viewHolderUserInfo.ivWeatherIcon.getLayoutParams();
					lp.width = dm.widthPixels / 10;
					lp.height = dm.widthPixels / 10;
					viewHolderUserInfo.ivWeatherIcon.setLayoutParams(lp);
					viewHolderUserInfo.ivWeatherIcon.setImageUrl(R.drawable.del_tianqi_sorry, 1, weatherurl, false);
				} else {
					Log.d(TAG, "无地理位置");
					viewHolderUserInfo.tvWeatherRegion.setVisibility(View.GONE);
					viewHolderUserInfo.tvWeatherTemp.setVisibility(View.GONE);
					viewHolderUserInfo.ivWeatherIcon.setVisibility(View.GONE);
//					//真实未获取到位置信息
//					tvWeatherRegion.setText("未能获取");
//					tvWeatherTemp.setTextSize(10);
//					tvWeatherTemp.setText("位置信息");
//					ivWeatherIcon.setImageResource(R.drawable.tianqi_sorry);
				}
			} else {
				Log.d(TAG, "无天气信息");
				viewHolderUserInfo.tvWeatherRegion.setVisibility(View.GONE);
				viewHolderUserInfo.tvWeatherTemp.setVisibility(View.GONE);
				viewHolderUserInfo.ivWeatherIcon.setVisibility(View.GONE);
			}
			break;
		default:
			break;
		}
//		notifyDataSetChanged();
	}
	
	*//**
	 * 是否存在天气缓存
	 * @param weather
	 * @return
	 *//*
	private boolean hasWeatherCache(MyWeather weather) {
		boolean bRet = false;
		if(weather == null) {
			weather = accountInfo.myWeather;
		}
		if (weather != null && weather.desc != null && weather.desc.length > 0
				&& !"".equals(weather.desc[0].description)) {
			bRet = true;
		}
		return bRet;
	}*/
	
	
}
