package com.cmmobi.looklook.common.view;

import java.util.ArrayList;

import cn.zipper.framwork.core.ZApplication;

import com.cmmobi.looklook.R;
import com.cmmobi.looklook.common.gson.GsonResponse3.MyDiaryList;
import com.cmmobi.looklook.common.utils.DateUtils;
import com.cmmobi.looklook.common.utils.DisplayUtil;
import com.cmmobi.looklook.fragment.MyZoneFragment.DiariesItem;
import com.cmmobi.looklook.fragment.MyZoneFragment.MyZoneItem;
import com.cmmobi.looklook.fragment.MyZoneFragment.UserInfo;
import com.cmmobi.looklook.info.profile.AccountInfo;
import com.cmmobi.looklook.info.profile.ActiveAccount;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class MyZoneHeaderView extends RelativeLayout implements OnClickListener {

	private LayoutInflater inflater;
	
	protected DisplayMetrics dm = new DisplayMetrics();
	
	private String userID;
	private AccountInfo accountInfo;
	
	private ArrayList<MyZoneItem> myZoneItems=new ArrayList<MyZoneItem>();
	
	private DisplayImageOptions options;
	private DisplayImageOptions bgOptions;
	protected ImageLoader imageLoader;
	private ImageLoadingListener animateFirstListener;
	
	private View llUserinfo;
	private TextView tvToday;
	private ImageView ivHeadUrl, ivHeadBackground;
	private ImageView ivBackground;
	private TextView tvSignature;
	private TextView tvNickname;
	private ImageView ivSex;
	private View llComment;
	private TextView tvComment;
	private ImageView ivImportPic;
	private View footmark;
	private LinearLayout llSignature;
	private ImageView ivNoData;
	
	private Activity context;
	
	private static final String TAG = MyZoneHeaderView.class.getSimpleName();
	
	public MyZoneHeaderView(Context context, AttributeSet attrs, int defStyle, ArrayList<MyZoneItem> myZoneItems) {
		super(context, attrs, defStyle);
		if(context instanceof Activity) {
			this.context = (Activity) context;
			this.myZoneItems = myZoneItems;
			initView();
		}
	}
	
	public MyZoneHeaderView(Context context, AttributeSet attrs, ArrayList<MyZoneItem> myZoneItems) {
		super(context, attrs);
		if(context instanceof Activity) {
			this.context = (Activity) context;
			this.myZoneItems = myZoneItems;
			initView();
		}
	}
	
	public MyZoneHeaderView(Context context, ArrayList<MyZoneItem> myZoneItems) {
		super(context);
		if(context instanceof Activity) {
			this.context = (Activity) context;
			this.myZoneItems = myZoneItems;
			initView();
		}
	}
	
	private void commonInit() {
		
		userID= ActiveAccount.getInstance(ZApplication.getInstance()).getUID();
		accountInfo = AccountInfo.getInstance(userID);
		
		imageLoader = ImageLoader.getInstance();
		if(!imageLoader.isInited())
			imageLoader.init(ImageLoaderConfiguration.createDefault(context));
		
		int roundpx = dm.widthPixels * 3 / 640;
		
		options = new DisplayImageOptions.Builder()
		.showStubImage(R.drawable.kongjian_morentouxiang)
		.showImageForEmptyUri(R.drawable.kongjian_morentouxiang)
		.showImageOnFail(R.drawable.kongjian_morentouxiang)
		.cacheInMemory(true)
		.cacheOnDisc(true)
		.displayer(new RoundedBitmapDisplayer(roundpx <= 0 ? 3 : roundpx))
//		.displayer(new CircularBitmapDisplayer())
		.build();
		
		bgOptions = new DisplayImageOptions.Builder()
		.showStubImage(R.drawable.moren_kongjianfengmian)
		.showImageForEmptyUri(R.drawable.moren_kongjianfengmian)
		.showImageOnFail(R.drawable.moren_kongjianfengmian)
		.cacheInMemory(true)
		.cacheOnDisc(true)
		.displayer(new SimpleBitmapDisplayer())
//		.displayer(new CircularBitmapDisplayer())
		.build();
	}
	
	private void initView() {
		commonInit();
		
		View convertView;
		View vCheckedBackground;
		inflater = LayoutInflater.from(getContext());
		context.getWindowManager().getDefaultDisplay().getMetrics(dm);
		convertView = inflater.inflate(R.layout.include_my_zone_background, null);
		llUserinfo=convertView.findViewById(R.id.ll_userinfo);
		tvToday=(TextView) convertView.findViewById(R.id.tv_today);
		ivHeadUrl=(ImageView) convertView.findViewById(R.id.iv_head);
		ivHeadBackground = (ImageView) convertView.findViewById(R.id.iv_head_background);
		ivBackground=(ImageView) convertView.findViewById(R.id.iv_background);
		tvSignature=(TextView) convertView.findViewById(R.id.tv_signature);
		tvNickname=(TextView) convertView.findViewById(R.id.tv_nickname);
		ivSex=(ImageView) convertView.findViewById(R.id.iv_sex);
		llComment=convertView.findViewById(R.id.ll_comment);
		tvComment=(TextView) convertView.findViewById(R.id.tv_comment);
		ivImportPic=(ImageView) convertView.findViewById(R.id.iv_import_pic);
		llSignature = (LinearLayout) convertView.findViewById(R.id.ll_signature);
		vCheckedBackground=convertView.findViewById(R.id.rl_checked);
//		shareTrace=convertView.findViewById(R.id.iv_share_trace);
		footmark=convertView.findViewById(R.id.iv_footmark);
		ivNoData = (ImageView) convertView.findViewById(R.id.iv_nodata);
//		collect=convertView.findViewById(R.id.iv_collect);
//		extend_1=convertView.findViewById(R.id.iv_extend_1);
//		extend=convertView.findViewById(R.id.iv_extend);
//		ivExtendContent=convertView.findViewById(R.id.ll_extend_content);
//		ivWeatherIcon = (WebImageView) convertView.findViewById(R.id.iv_weather_icon);
//		tvWeatherTemp = (TextView) convertView.findViewById(R.id.tv_weather_temp);
//		tvWeatherRegion = (TextView) convertView.findViewById(R.id.tv_weather_region);
		android.view.ViewGroup.LayoutParams params=ivHeadBackground.getLayoutParams();
		params.height=dm.widthPixels * 15 / 64;
		params.width=params.height;
		ivHeadBackground.setLayoutParams(params);
		Log.e(TAG, "height = " + params.height + ";pixwidth = " + dm.widthPixels);
		
		int nicknamesize = DisplayUtil.px2sp(context, dm.widthPixels * 34 / 640);
		int signsize = DisplayUtil.px2sp(context, dm.widthPixels * 24 / 640);
		
		tvNickname.setTextSize(nicknamesize);
		tvSignature.setTextSize(signsize);
		
		llComment.setOnClickListener(this);
		ivBackground.setOnClickListener(this);
		llUserinfo.setOnClickListener(this);
		ivImportPic.setOnClickListener(this);
		footmark.setOnClickListener(this);
		
		if(this.myZoneItems!=null && this.myZoneItems.size()>1){
			ivNoData.setVisibility(View.GONE);
		}else{
			ivNoData.setVisibility(View.VISIBLE);
		}
		
		if(this.myZoneItems!=null && myZoneItems.size()>1){
			MyZoneItem myZoneItem=myZoneItems.get(1);
			if(myZoneItem instanceof DiariesItem){
				if("今天".equals(((DiariesItem) myZoneItem).strDate)){
					tvToday.setVisibility(View.GONE);
				}else{
					if(((DiariesItem) myZoneItem).diaryGroups.size()>0){
						MyDiaryList myDiaryList =((DiariesItem) myZoneItem).diaryGroups.get(0);
						if(DateUtils.isNum(myDiaryList.create_time)){
							long createTime=Long.parseLong(myDiaryList.create_time);
							long dtTime=System.currentTimeMillis()-createTime;
							long days=dtTime/(3600*1000*24);
							if(days>=7&&days<14){//超过一个星期
								tvToday.setVisibility(View.VISIBLE);
								tvToday.setHint(R.string.myzone_hint_2);
							}else if(days>=14){//超过两个星期
								tvToday.setVisibility(View.VISIBLE);
								tvToday.setHint(R.string.myzone_hint_3);
							}else{
								tvToday.setVisibility(View.VISIBLE);
								tvToday.setHint(R.string.myzone_hint_1);
							}
						}
					}
				}
			}
		}else{
			tvToday.setVisibility(View.VISIBLE);
			tvToday.setHint(R.string.myzone_hint_1);
		}
		android.view.ViewGroup.LayoutParams backgroundParams=ivBackground.getLayoutParams();
		/*if(h < 0) {
			Log.d("xxx", "gBackgroundParams is given value");
			h = backgroundParams.height;
		}*/
		backgroundParams.width=dm.widthPixels;
		backgroundParams.height=(int)(backgroundParams.width*0.6f);
		ivBackground.setLayoutParams(backgroundParams);
		android.widget.RelativeLayout.LayoutParams userinfoParams=(android.widget.RelativeLayout.LayoutParams) llUserinfo.getLayoutParams();
//		userinfoParams.topMargin=(int)(backgroundParams.height-(height*0.74f));
		userinfoParams.topMargin = backgroundParams.height - (int)(dm.widthPixels * 114 / 640);
		llUserinfo.setLayoutParams(userinfoParams);
		//布局初始化
		if(myZoneItems != null && myZoneItems.size() > 0) {
			MyZoneItem zoneItem=myZoneItems.get(0);
			if(zoneItem instanceof UserInfo){
				setUserInfo((UserInfo)zoneItem);
				
				final UserInfo userInfo = (UserInfo)zoneItem;
				ViewTreeObserver vto2 = ivHeadUrl.getViewTreeObserver();   
				vto2.addOnGlobalLayoutListener(new OnGlobalLayoutListener() { 
					@Override   
					public void onGlobalLayout() {
						if(userInfo.headUrl!=null)
							imageLoader.displayImageEx(userInfo.headUrl, ivHeadUrl, options, animateFirstListener, userID, 1);
					}   
				});
			}
		}
	}

	//设置用户信息
	private void setUserInfo(UserInfo userInfo){
		if(userInfo.headUrl!=null)
			imageLoader.displayImageEx(userInfo.headUrl, ivHeadUrl, options, animateFirstListener, userID, 1);
		if(userInfo.backgroundUrl!=null){
			imageLoader.displayImageEx(userInfo.backgroundUrl, ivBackground, bgOptions, animateFirstListener, userID, 1);
		}
		if(!TextUtils.isEmpty(userInfo.signature)){
			llSignature.setVisibility(View.VISIBLE);
			FriendsExpressionView.replacedExpressions(userInfo.signature, tvSignature);
		}else{
			llSignature.setVisibility(View.GONE);
			tvSignature.setText(null);
		}
		if(!TextUtils.isEmpty(userInfo.nickname)){
			FriendsExpressionView.replacedExpressions(userInfo.nickname, tvNickname);
		}else{
			tvNickname.setText(null);
		}
		if("0".equals(userInfo.sex)){
			ivSex.setImageResource(R.drawable.nan);
			ivSex.setVisibility(View.VISIBLE);
		}else if("1".equals(userInfo.sex)){
			ivSex.setImageResource(R.drawable.nv);
			ivSex.setVisibility(View.VISIBLE);
		}else{
			ivSex.setVisibility(View.GONE);
		}
		if(0==userInfo.msgNum){
			llComment.setVisibility(View.GONE);
		}else{
			llComment.setVisibility(View.VISIBLE);
			String strComment=context.getResources().getString(R.string.has_new_comment, userInfo.msgNum);
			tvComment.setText(strComment);
		}
	}
	
	@Override
	public void onClick(View v) {
		switch(v.getId()) {
		
		}
	}
	
	
}
