package com.cmmobi.looklook.fragment;

import java.io.File;
import java.util.ArrayList;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Environment;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import cn.zipper.framwork.core.ZApplication;

import com.cmmobi.looklook.R;
import com.cmmobi.looklook.activity.LookLookActivity;
import com.cmmobi.looklook.activity.SettingGesturePwdActivity;
import com.cmmobi.looklook.activity.SpaceCoverActivity;
import com.cmmobi.looklook.common.gson.GsonResponse3.MyDiary;
import com.cmmobi.looklook.common.service.CoreService;
import com.cmmobi.looklook.common.utils.CmmobiClickAgentWrapper;
import com.cmmobi.looklook.common.utils.DisplayUtil;
import com.cmmobi.looklook.common.view.XFeatureLayout;
import com.cmmobi.looklook.info.profile.AccountInfo;
import com.cmmobi.looklook.info.profile.ActiveAccount;
import com.cmmobi.looklook.info.profile.MediaValue;
import com.cmmobi.looklook.prompt.Prompt;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

/**
 * @author wuxiang
 * @email wuxiang@cmmobi.com
 * @date 2013-11-7
 */
public class ZoneBaseFragment extends XFragment implements OnClickListener{

	private static final String TAG = ZoneBaseFragment.class.getSimpleName();
	private View contentView;
	private XFragment currFragment;
	private View vTitle;
	private View vTitleChecked;
	private XFeatureLayout xFeatureLayout;

	private ImageView pushDianVShare;
	private ImageView pushDianMenu;// 菜单红色圆点
	private TextView tvPushDianMenuNum;// 菜单红色圆点
	
	private View[] titleBtn=new View[2];
	private View[] checkedBtn=new View[2];
	
	private ImageView ivAddSafebox;
	public ViewPager contentViewPager;
	private ContentPagerAdapter pagerAdapter;
	private TextView tv_myzone, tv_vshare;
	private RelativeLayout rlChecked;
	private View vchecked;
	
	private int distance = -1;
	private android.widget.RelativeLayout.LayoutParams lp = null;
	
	protected DisplayMetrics dm = new DisplayMetrics();
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		Log.d(TAG, "onCreateView");
		
		getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
		
		contentView = inflater.inflate(R.layout.activity_my_zone, null);
		vTitle = contentView.findViewById(R.id.ll_title);
		vTitleChecked = contentView.findViewById(R.id.ll_title_checked);
		
		titleBtn[0]=contentView.findViewById(R.id.ll_myzone);
		titleBtn[1]=contentView.findViewById(R.id.ll_vshare);
		
		LayoutParams params0=titleBtn[0].getLayoutParams();
		LayoutParams params1=titleBtn[1].getLayoutParams();
		params0.width=dm.widthPixels * 157 / 640;
		params1.width=params0.width;
		titleBtn[0].setLayoutParams(params0);
		titleBtn[1].setLayoutParams(params1);
		
		rlChecked = (RelativeLayout) contentView.findViewById(R.id.ll_checked);
		LayoutParams params2=rlChecked.getLayoutParams();
		params2.width=params0.width;
		rlChecked.setLayoutParams(params2);
		
		vchecked = contentView.findViewById(R.id.iv_checked);
		
		checkedBtn[0]=contentView.findViewById(R.id.iv_myzone_checked);
		checkedBtn[1]=contentView.findViewById(R.id.iv_vshare_checked);
		
		titleBtn[0].setOnClickListener(this);
		titleBtn[1].setOnClickListener(this);
		contentView.findViewById(R.id.btn_menu).setOnClickListener(this);
		contentView.findViewById(R.id.btn_back_checked)
				.setOnClickListener(this);
		ivAddSafebox=(ImageView) contentView.findViewById(R.id.btn_add_safebox);
		ivAddSafebox.setOnClickListener(this);
		
		contentView.findViewById(R.id.btn_add_tags).setOnClickListener(this);
		xFeatureLayout = (XFeatureLayout) contentView.findViewById(R.id.x_feature_layout);
		contentView.findViewById(R.id.btn_delete).setOnClickListener(this);
		pushDianVShare=(ImageView) contentView.findViewById(R.id.iv_dian_vshare);
		pushDianMenu = (ImageView) contentView.findViewById(R.id.iv_dian_menu);
		tvPushDianMenuNum = (TextView) contentView.findViewById(R.id.tv_dian_num);
		
		tv_myzone = (TextView) contentView.findViewById(R.id.tv_myzone);
		tv_vshare = (TextView) contentView.findViewById(R.id.tv_vshare);
		
		/*int myzoneText = DisplayUtil.px2sp(getActivity(), dm.widthPixels * 31 / 640);
		int vshareText = myzoneText;
		
		tv_myzone.setTextSize(myzoneText);
		tv_vshare.setTextSize(vshareText);*/
		
//		viewpager_content
		contentViewPager=(ViewPager) contentView.findViewById(R.id.viewpager_content);
		pagerAdapter=new ContentPagerAdapter(getFragmentManager());
		contentViewPager.setAdapter(pagerAdapter);
		contentViewPager.setOffscreenPageLimit(2);
		contentViewPager.setOnPageChangeListener(new OnPageChangeListener() {
			@Override
			public void onPageScrollStateChanged(int arg0) { }

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
				
				rlChecked.setVisibility(View.VISIBLE);
				checkedHide();
				
				if(distance < 0) {
					int tw = rlChecked.getMeasuredWidth();
					distance = tw;
//					Log.e(TAG, "tw = " + tw);
				}
				lp = (android.widget.RelativeLayout.LayoutParams) rlChecked.getLayoutParams();
				if(lp != null) {
//					Log.e(TAG, "margin left = " + lp.leftMargin + "; margin top = " + lp.topMargin + "; margin start = " + lp.getMarginStart());
				}
				else {
					Log.e(TAG, "lp is null!");
				}
				
				lp.leftMargin = (int) (distance * arg1);
				rlChecked.setLayoutParams(lp);
				
//				Log.e(TAG, "arg0 = " + arg0 + "; arg1 = " + arg1 + "; arg2 = " + arg2);
				if(arg1 >= 1 || arg1 <= 0) {
					switch(arg0) {
					case 0:
						checkedMyZone();
						break;
					case 1:
						checkedVShare();
						break;
					default:
						break;
					}
				}
			}

			@Override
			public void onPageSelected(int position) {
				Log.e(TAG, "onPageSelected " + position);
				switch (position) {
				case 0:{
					Log.e(TAG, "tv_myzone size = " + tv_myzone.getTextSize());
					checkedMyZone();
					showVideoBtn();
					((VShareFragment)pagerAdapter.getItem(1)).onInvisible();
					if(isAdded())
					((LookLookActivity)getActivity()).getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_NONE);
					break;}
				case 1:{
					checkedVShare();
					hideVideoBtn();
					((VShareFragment)pagerAdapter.getItem(1)).onVisible();
					((LookLookActivity)getActivity()).getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
					break;}
				default:
					break;
				}
			}
		});
		contentViewPager.setCurrentItem(1);
		((LookLookActivity)getActivity()).getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_NONE);
		
		IntentFilter filter=new IntentFilter();
		filter.addAction(CoreService.ACTION_MESSAGE_DATA_UPDATE);
		filter.addAction(ACTION_ZONEBASE_USERINFO);
		filter.addAction(MenuFragment.ACTION_SAFEBOX_MODE_CHANGED);
		filter.addAction(LookLookActivity.UPDATE_MASK);
		filter.addAction(SpaceCoverActivity.ACTION_SPACECOVER_UPLOAD_COMPLETED);
		receiver=new myReceiver();
		LocalBroadcastManager.getInstance(getActivity()).registerReceiver(receiver, filter);
		select(0);
		return contentView;
	}

	private int currentPosition;
	public XFragment getXFragment() {
		return currFragment;
	}
	
	public int getCurrentSelect() {
		if(contentViewPager != null) {
			return contentViewPager.getCurrentItem();
		}
		return 0;
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ll_myzone:{
			select(0);
//			((LookLookActivity)getActivity()).initConfig();
			break;}
		case R.id.ll_vshare:{
			select(1);
			break;}
		case R.id.btn_menu:
			showMenu();
			updateMasks();
			break;
		case R.id.btn_back_checked:
			showNormalTitle();
			purgeCheckedView();
			break;
		case R.id.btn_add_safebox:
			//判断保险箱是否创建
			if(safeboxIsCreated()) {
				addToSafebox();
			} else {
				//启动创建保险箱流程
				startSafeboxCreateActivity(PARAM);
			}
			break;
		case R.id.btn_add_tags:
			addTags();
			break;
		case R.id.btn_delete:
			deleteDiary();
			break;
		default:
			break;
		}
	}
	
	//0-我 1-微享
	public void select(int which){
		if(null==contentView)return;
		if(0==which){//我
			enterMyZone();
			checkedMyZone();
			showVideoBtn();
		}else if(1==which){//微享
			enterVShare();
			checkedVShare();
			hideVideoBtn();
		}else{
			Log.d(TAG, "select error "+which);
		}
	}
	
	//显示拍摄按钮
	private void showVideoBtn(){
		if(xFeatureLayout!=null)
			xFeatureLayout.setVisibility(View.VISIBLE);
	}
	
	//隐藏拍摄按钮
	private void hideVideoBtn(){
		if(xFeatureLayout!=null)
			xFeatureLayout.setVisibility(View.GONE);
	}
	
	private void addToSafebox(){
		MyZoneFragment myZoneFragment = FragmentHelper.getInstance(
				getActivity()).getMyZoneFragment();
		myZoneFragment.addToSafebox();
	}
	
	private void addTags(){
		MyZoneFragment myZoneFragment = FragmentHelper.getInstance(
				getActivity()).getMyZoneFragment();
		myZoneFragment.addTags();
	}

	private boolean isCheckedShow;

	public void hideNormalTitle() {
		if (vTitle.getVisibility() == View.VISIBLE)
			vTitle.setVisibility(View.GONE);
	}

	public void showNormalTitle() {
		if (vTitle.getVisibility() == View.GONE)
			vTitle.setVisibility(View.VISIBLE);
		if(vTitleChecked.getVisibility()==View.VISIBLE)
			vTitleChecked.setVisibility(View.GONE);
		isCheckedShow=false;
		xFeatureLayout.setVisibility(View.VISIBLE);
	}

	// 清除日记选中状态
	private void purgeCheckedView() {
		MyZoneFragment myZoneFragment = FragmentHelper.getInstance(
				getActivity()).getMyZoneFragment();
		myZoneFragment.purgeCheckedView();
	}
	
	private void deleteDiary(){
		MyZoneFragment myZoneFragment = FragmentHelper.getInstance(
				getActivity()).getMyZoneFragment();
		myZoneFragment.deleteDiary();
	}
	
	private void updateUserInfo(){
		MyZoneFragment myZoneFragment = FragmentHelper.getInstance(
				getActivity()).getMyZoneFragment();
		myZoneFragment.updateUserInfo();
		MenuFragment menuFragment=(MenuFragment) getActivity().getSupportFragmentManager()
				.findFragmentById(R.id.menu_frame);
		if(menuFragment!=null)
			menuFragment.updateHead();
	}

	/**
	 * 显示日记选中后标题栏
	 */
	public void showCheckedTitle() {
		vTitle.setVisibility(View.GONE);
		vTitleChecked.setVisibility(View.VISIBLE);
		isCheckedShow = true;
		ivAddSafebox.setBackgroundResource(R.drawable.btn_add_safebox);
		xFeatureLayout.setVisibility(View.GONE);
	}

	/**
	 * 判断当前选中标题栏是否显示 true-显示 false-未显示
	 */
	public boolean isCheckedTitleShow() {
		return isCheckedShow;
	}

	/**
	 * 收拢+号按钮
	 */
	public void hideFeaturelist() {
	}

	/**
	 * 切换内容区fragment
	 *//*
	@Override
	public void switchContent(final XFragment fragment) {
		if (null == fragment)
			return;
		FragmentTransaction ft = getActivity().getSupportFragmentManager()
				.beginTransaction();
		if (currFragment != null)
			ft.hide(currFragment);
		if (null == getActivity().getSupportFragmentManager()
				.findFragmentByTag(fragment.getClass().getName())) {
			ft.add(R.id.zone_content, fragment, fragment.getClass().getName());
		} else {
			ft.show(fragment);
		}
		currFragment = fragment;
		ft.commitAllowingStateLoss();
	}*/

	// 进入空间界面
	public void enterMyZone() {
//		switchContent(FragmentHelper.getInstance(getActivity()).getMyZoneFragment());
		contentViewPager.setCurrentItem(0);
//		((VShareFragment)pagerAdapter.getItem(1)).onInvisible();
	}

	// 进入微享界面
	public void enterVShare() {
//		switchContent(FragmentHelper.getInstance(getActivity()).getVShareFragment());
		contentViewPager.setCurrentItem(1);
//		((VShareFragment)pagerAdapter.getItem(1)).onVisible();
	}

	// 隐藏标签
	private void checkedHide() {
		checkedBtn[0].setVisibility(View.INVISIBLE);
		checkedBtn[1].setVisibility(View.INVISIBLE);
	}
	
	// 空间选中
	private void checkedMyZone() {
		rlChecked.setVisibility(View.INVISIBLE);
		checkedBtn[0].setVisibility(View.VISIBLE);
		checkedBtn[1].setVisibility(View.INVISIBLE);
	}

	// 微享选中
	private void checkedVShare() {
		rlChecked.setVisibility(View.INVISIBLE);
		checkedBtn[1].setVisibility(View.VISIBLE);
		checkedBtn[0].setVisibility(View.INVISIBLE);
	}

	// 显示微享角标
	private void showDianVShare() {
		pushDianVShare.setVisibility(View.VISIBLE);
	}

	// 隐藏微享角标
	public void dimissDianVShare() {
		pushDianVShare.setVisibility(View.INVISIBLE);
	}

	// 显示菜单角标
	private void showDianMenu() {
		//如果侧滑栏中有角标显示，则显示菜单栏角标
		tvPushDianMenuNum.setVisibility(View.GONE);
		pushDianMenu.setVisibility(View.VISIBLE);
		pushDianMenu.setImageResource(R.drawable.jiaobiao_1/*push_dian*/);
	}
	
	// 显示菜单角标
	private void showDianMenu(int num) {
		if(num>0){
			//如果侧滑栏中有角标显示，则显示菜单栏角标
			tvPushDianMenuNum.setVisibility(View.VISIBLE);
			pushDianMenu.setVisibility(View.VISIBLE);
			int resid=0;
			String text=num+"";
			if(num>9){
				if(num>99){
					text="99+";
				}
				resid=R.drawable.jiaobiao_2;
			}else{
				resid=R.drawable.jiaobiao_1;
			}
			pushDianMenu.setImageResource(resid);
			tvPushDianMenuNum.setText(text);
		}
	}
	
	

	// 隐藏菜单角标
	private void dimissDianMenu() {
		//如果侧滑栏中有没有角标显示，则隐藏菜单栏角标
		tvPushDianMenuNum.setVisibility(View.GONE);
		pushDianMenu.setVisibility(View.GONE);
	}

	@Override
	public boolean handleMessage(Message msg) {
		// TODO Auto-generated method stub
		return false;
	}
	@Override
	public void onHiddenChanged(boolean hidden) {
		FragmentHelper.getInstance(getActivity())
		.getMyZoneFragment().onHiddenChanged(hidden);
		super.onHiddenChanged(hidden);
	}
	
	/*private void dragView(int dragViewWhich,View view){
//		int l=titleBtn[dragViewWhich].getLeft();
//		int t=titleBtn[dragViewWhich].getTop();
//		int r=titleBtn[dragViewWhich].getRight();
//		int b=titleBtn[dragViewWhich].getBottom();
//		int width=titleBtn[dragViewWhich].getWidth();
//		int height=titleBtn[dragViewWhich].getHeight();
		int l=view.getLeft();
		int width=view.getWidth();
		int height=view.getHeight();
		initDragViewContent(width, height, l, getDragContent(dragViewWhich), dragViewWhich==titleOrderManager.selectedIndex, hasMask(dragViewWhich),dragViewWhich);
		dimissItem(dragViewWhich);
		view.setOnTouchListener(new XDragTouchListener(dragViewWhich));
	}*/
	/*private View dragViewLayout;
	private TextView tvDragViewContent;
	private View vDragViewBG;
	private ImageView ivDragViewMask;
	//拖动布局初始化
	private void initDragView(View container){
		dragViewLayout=container.findViewById(R.id.fl_draglayout);
		tvDragViewContent=(TextView) container.findViewById(R.id.tv_dragView_content);
		vDragViewBG=container.findViewById(R.id.rl_dragview_bg);
		ivDragViewMask=(ImageView) container.findViewById(R.id.iv_dragview_dian);
	}*/
	
	/*
	 * 初始化拖动布局内容
	 * width-宽度
	 * height-高度
	 * content-文字内容
	 * isSelected-true 显示背景 false 隐藏背景
	 * hasMask- true 显示角标 false 隐藏角标
	 */
	/*private void initDragViewContent(int width,int height,int l,String content,boolean isSelected,boolean hasMask,int which){
		dragViewLayout.setVisibility(View.VISIBLE);
		LayoutParams lp=new LayoutParams(width, height);
//		lp.setMargins(l, t, l+width, t+height);
		lp.leftMargin=l;
		lp.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);
		dragViewLayout.setLayoutParams(lp);
		tvDragViewContent.setText(content);
		if(isSelected){
			vDragViewBG.setVisibility(View.VISIBLE);
			if(0==which){
				vDragViewBG.setBackgroundResource(R.drawable.qiehuan1_2);
			}else{
				vDragViewBG.setBackgroundResource(R.drawable.qiehuan1_3);
			}
			tvDragViewContent.setTextColor(Color.WHITE);
		}else{
			vDragViewBG.setVisibility(View.INVISIBLE);
			tvDragViewContent.setTextColor(Color.parseColor("#0C80FF"));
		}
		if(hasMask){
			ivDragViewMask.setVisibility(View.VISIBLE);
		}else{
			ivDragViewMask.setVisibility(View.GONE);
		}
	}*/
	
	/*//移动拖拽布局
	private void move(int x){
		dragViewLayout.layout(x-dragViewLayout.getWidth()/2, dragViewLayout.getTop(), x+dragViewLayout.getWidth()/2, dragViewLayout.getBottom());
	}*/
	
	/*//获取长按按钮文字
	private String getDragContent(int which){
		return titleBtn[which].getText().toString();
	}*/
	
	/*//交换位置
	private void swap(int src,int target){
		if(src==titleOrderManager.selectedIndex){
			titleOrderManager.selectedIndex=target;
		}else if(target==titleOrderManager.selectedIndex){
			titleOrderManager.selectedIndex=src;
		}
		String[] order=sort.split(",");
		ArrayList<String> orderlist=new ArrayList<String>();
		orderlist.addAll(Arrays.asList(order));
		String srcid=orderlist.get(src);
		String targetid=orderlist.get(target);
		orderlist.remove(src);
		orderlist.add(src, targetid);
		orderlist.remove(target);
		orderlist.add(target,srcid);
		sort="";
		for(int i=0;i<orderlist.size();i++){
			sort+=orderlist.get(i);
			if(i!=orderlist.size()-1)
				sort+=",";
		}
		updateTitle();
		//比较排序是否有变动，如果有变动，通知服务器
		if(accountInfo.personalsort!=null&&!accountInfo.personalsort.equals(sort)){
			Requester3.setPersonalSort(handler, sort);
			accountInfo.personalsort=sort;
		}
	}
	
	private void updateTitle(){
		titleOrderManager.init();
//		select(LookLookActivity.index);
		select(titleOrderManager.selectedIndex);
		updateMasks();
		dragViewLayout.setVisibility(View.GONE);
	}
	
	//判断给点项角标是否显示 true-显示 false-未显示
	private boolean hasMask(int which){
		switch (which) {
		case 0:
			return View.VISIBLE==titleMark[0].getVisibility();
		case 1:
			return View.VISIBLE==titleMark[1].getVisibility();
		default:
			break;
		}
		return false;
	}
	
	public static final int TITLETYPE_ZONE=11;
	public static final int TITLETYPE_VSHARE=12;
	private static final int TITLETYPE_FRIENDS=13;
	private static final int TITLETYPE_SUBSCRIBE=14;
	
	//选中第几个按钮 which 0 空间按钮 1微享按钮 2朋友按钮 3 订阅按钮
	public void select(int which){
		if(null==contentView)return;
		ArrayList<TitleOrder> titleOrders=titleOrderManager.titleOrders;
		if(null==titleOrders||titleIDs.length!=titleOrders.size()||which<0||which>titleIDs.length-1){
			Log.e(TAG, "select init error");
			return;
		}
		titleOrderManager.selectedIndex=which;
		titleOrderManager.checkDefault();
		TitleOrder titleOrder=titleOrders.get(which);
		if(titleOrder!=null){
			switch (titleOrder.TitleType) {
			case TITLETYPE_ZONE://进入空间
				enterMyZone();
				break;
			case TITLETYPE_VSHARE://进入微享
				enterWeiShare();
				dimissDianVShare();
				break;
//			case TITLETYPE_FRIENDS://进入朋友
//				enterFriend();
//				dimissDianFriend();
//				break;
//			case TITLETYPE_SUBSCRIBE://进入订阅
//				enterSubscribe();
//				dimissDianSubscribe();
//				break;

			default:
				break;
			}
		}
	}*/
	
	/*public void enterItem(int type){
		if(null==contentView)return;
		for(int i=0;i<titleOrderManager.titleOrders.size();i++){
			TitleOrder titleOrder=titleOrderManager.titleOrders.get(i);
			if(type==titleOrder.TitleType){
				select(i);
				return;
			}
		}
	}*/
	
	@Override
	public void stopCallBack(Object data) {
		// TODO Auto-generated method stub
		xFeatureLayout.stopShortRecoder();
		Log.d(TAG,"stopCallBack in");
		super.stopCallBack(data);
	}
	
	/*//更新文字选中后的颜色
	private void updateSelectTxtColor(int which){
		for(int i=0;i<titleBtn.length;i++){
			if(which==i){
				titleBtn[i].setTextColor(Color.WHITE);
			}else{
				titleBtn[i].setTextColor(Color.parseColor("#0C80FF"));
			}
		}
	}*/
	
	/*//显示角标 which 0-空间 1-微享 2-朋友 3-订阅
	private void showMark(int which){
		ArrayList<TitleOrder> titleOrders=titleOrderManager.titleOrders;
		if(null==titleOrders||titleIDs.length!=titleOrders.size()||which<0||which>titleIDs.length){
			Log.e(TAG, "showMark init error");
			return;
		}
		TitleOrder titleOrder=titleOrders.get(which);
		if(titleOrder!=null){
			switch (titleOrder.TitleType) {
			case TITLETYPE_ZONE://显示空间页角标
				titleMark[0].setVisibility(View.VISIBLE);
				break;
			case TITLETYPE_VSHARE://显示微享页角标
				titleMark[1].setVisibility(View.VISIBLE);
				break;
			case TITLETYPE_FRIENDS://显示朋友页角标
				pushDianFriend.setVisibility(View.VISIBLE);
				break;
			case TITLETYPE_SUBSCRIBE://显示订阅页角标
				pushDianSubscribe.setVisibility(View.VISIBLE);
				break;

			default:
				break;
			}
		}
	}*/
	
	
	/*//隐藏角标 which 0-空间 1-微享 2-朋友 3-订阅
	private void dimissMark(int which){
		ArrayList<TitleOrder> titleOrders=titleOrderManager.titleOrders;
		if(null==titleOrders||titleIDs.length!=titleOrders.size()||which<0||which>titleIDs.length){
			Log.e(TAG, "showMark init error");
			return;
		}
		TitleOrder titleOrder=titleOrders.get(which);
		if(titleOrder!=null){
			switch (titleOrder.TitleType) {
			case TITLETYPE_ZONE://隐藏空间页角标
				titleMark[0].setVisibility(View.GONE);
				break;
			case TITLETYPE_VSHARE://隐藏微享页角标
				titleMark[1].setVisibility(View.GONE);
				break;
			case TITLETYPE_FRIENDS://隐藏朋友页角标
				pushDianFriend.setVisibility(View.GONE);
				break;
			case TITLETYPE_SUBSCRIBE://隐藏订阅页角标
				pushDianSubscribe.setVisibility(View.GONE);
				break;

			default:
				break;
			}
		}
	}*/
	
	private MenuFragment getMenuFragment(){
		if(getActivity()!=null)
			return ((LookLookActivity)getActivity()).menuFragment;
		return null;
	}
	
	private MyZoneFragment getMyZoneFragment(){
		return FragmentHelper.getInstance(getActivity()).getMyZoneFragment();
	}
	
	//更新所有角标状态
	public void updateMasks(){
		if(null==contentView)return;
//		commentnum;
//		new_fansnum
//		new_activenum;
//		new_snsfriendnum;
		//更新侧滑栏角标
		dimissDianVShare();
		int unReadMsg=accountInfo.privateMsgManger.getUnReadNum();
		int unCommentNum=accountInfo.privateMsgManger.getUnReadCommentNum();
		int contactsNum = accountInfo.newFriendRequestCount;// + accountInfo.newFriendUpdateCount
		System.out.println("unReadMsg" + unReadMsg + ", unCommentNum" + unCommentNum + ", contactsNum" + contactsNum);
		if((unReadMsg+contactsNum + accountInfo.newFriendChange)>0){
			showDianMenu();//显示带红点角标
		}else{
			dimissDianMenu();
		}
//		getMenuFragment().updateCommentMark(unCommentNum);
//		getMenuFragment().updateFriendsRecentMark(unCommentNum);
		MenuFragment menuFragment=getMenuFragment();
		if(menuFragment!=null){
			getMenuFragment().updateContactMark(contactsNum);
			getMenuFragment().updateFriendsRecentMark(accountInfo.newFriendChange);
			getMenuFragment().updatePrivateMsgMark(unReadMsg);
		}else{
			Log.e(TAG, "menuFragment is null");
		}
		getMyZoneFragment().updateComment(unCommentNum);
//		if(hasStrangerMsg||hasFriendMsg){
//			getMenuFragment().updatePrivateMsgMark(true);
//		}else{
//			getMenuFragment().updatePrivateMsgMark(false);
//		}
		
//		getMenuFragment().updatePrivateMsgMark(num)
		/*new_zonefriend = accountInfo.newFansCount + accountInfo.newRecommendCount + accountInfo.newSNSFriendsCount;
		if(new_zonefriend>0){
			showDianFriend();
		}else{
			dimissDianFriend();
		}*/
		/*
		if(new_zonesubscribe>0){
			showDianSubscribe();
		}else{
			dimissDianSubscribe();
		}*/
		if(accountInfo.newZoneMicCount>0){
			showDianVShare();
		}else{
			dimissDianVShare();
		}
		/*if(friendnum>0){
			showDianFriend();
		}else{
			dimissDianFriend();
		}*/
	}
	
//	String sort="11,12,13,14";
	/*String sort="11,12";
	
	class TitleOrderManager{
		
		private ArrayList<TitleOrder> titleOrders=new ArrayList<ZoneBaseFragment.TitleOrder>();
		private int selectedIndex=0;
		
		public TitleOrderManager(){
			sort=accountInfo.personalsort;
		}
		
		public void init(){
			titleOrders.clear();
			String[] sortList=sort.split(",");
			for(int i=0;i<titleIDs.length;i++){
				TitleOrder zoneTitle=new TitleOrder();
				zoneTitle.id=titleIDs[i];
				zoneTitle.TitleType=Integer.parseInt(sortList[i]);
				zoneTitle.hasMsg=true;
				titleOrders.add(zoneTitle);
			}
			update();
		}
		
		*//**
		 * 设置选中项
		 *//*
		public void checkDefault(){
			switch (selectedIndex) {
			case 0://空间
				checkedMyZone();
				break;
			case 1://微享
				checkedVShare();
				break;
			case 2://朋友
				checkedFriend();
				break;
			case 3://订阅
				checkedSubscribe();
				break;
			default:
				break;
			}
			updateSelectTxtColor(selectedIndex);
		}
		
		*//**
		 * 更新每个按钮的顺序
		 *//*
		public ArrayList<TitleOrder> update(){
			for(int i=0;i<titleOrders.size();i++){
				TitleOrder titleOrder=titleOrders.get(i);
				titleBtn[i].setVisibility(View.VISIBLE);
				switch (titleOrder.TitleType) {
				case TITLETYPE_ZONE:
					titleBtn[i].setText(R.string.zonebase_zone);
					break;
				case TITLETYPE_VSHARE:
					titleBtn[i].setText(R.string.zonebase_vshare);
					break;
				case TITLETYPE_FRIENDS:
					titleBtn[i].setText(R.string.zonebase_friend);
					break;
				case TITLETYPE_SUBSCRIBE:
					titleBtn[i].setText(R.string.zonebase_subscribe);
					break;
				default:
					break;
				}
			}
			return titleOrders;
		}
		
		*//**
		 * 根据id获取按钮类型
		 *//*
		public int getType(long id){
			//TODO 
			return 0;
		}
	}*/
	
	/*class TitleOrder{
		public long id;//按钮ID
		public int TitleType;//0-空间 1-微享 2-朋友 3-订阅
		public boolean hasMsg;//是否有消息角标显示
	}
	
	class XDragTouchListener implements OnTouchListener{
		//记录原始中心点坐标
		private int anchorsPosition[]=new int[titleIDs.length];
		private int which;
		public XDragTouchListener(int which){
			this.which=which;
			for(int i=0;i<titleIDs.length;i++){
				anchorsPosition[i]=titleBtn[i].getLeft()+titleBtn[i].getWidth()/2;
			}
		}
		  
        public boolean onTouch(View v, MotionEvent event) {  
            int action = event.getAction();  
            int x = (int) event.getRawX();  
            switch (action) { 
            case MotionEvent.ACTION_MOVE: 
            	move(x);
                break;  
            case MotionEvent.ACTION_UP:  
            	stopDrag(v);
                break;  
            }  
            return true;  
        }  
		
		//停止拖动
		private void stopDrag(View v){
			v.setOnTouchListener(null);
			int currPosition=dragViewLayout.getLeft()+dragViewLayout.getWidth()/2;
			int currAchor=anchorsPosition[which];
			if(currPosition>currAchor){//右移
				for(int i=anchorsPosition.length-1;i>which;i--){
					if(currPosition>anchorsPosition[i]){
						swap(which,i);
						return;
					}
				}
			}else if(currPosition<currAchor){//左移
				for(int i=0;i<anchorsPosition.length;i++){
					if(currPosition<anchorsPosition[i]){
						swap(which,i);
						return;
					}
				}
			}
			updateTitle();
		}
	}*/
	
	/**
	 * 个人信息广播action
	 */
	public static final String ACTION_ZONEBASE_USERINFO="ACTION_ZONEBASE_USERINFO";
	/**
	 * 私信角标重置广播
	 */
	public static final String ACTION_ZONEBASE_PRIVATEMSG="ACTION_ZONEBASE_PRIVATEMSG";
	/**
	 * 评论角标重置广播
	 */
	public static final String ACTION_ZONEBASE_COMMENT="ACTION_ZONEBASE_COMMENT";
	/**
	 * 通讯录角标重置广播
	 */
	public static final String ACTION_ZONEBASE_CONTACT="ACTION_ZONEBASE_CONTACT_NEW_FANSNUM";
	
	
	private myReceiver receiver;
//	private int commentnum;
//	private int new_fansnum;
//	private int new_activenum;
//	private int new_snsfriendnum;
//	private int new_zonefriend;
//	private int new_zonesubscribe;
//	private int new_zonemic;
//	private int friendnum;
//	private boolean hasStrangerMsg;
//	private boolean hasFriendMsg;
	private static final String PARAM="from_zonebase";
	private class myReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			String action=intent.getAction();
			String param=intent.getStringExtra(SettingGesturePwdActivity.ACTION_PARAM);
			if(CoreService.ACTION_MESSAGE_DATA_UPDATE.equals(action)){//角标
				Log.d(TAG, "ACTION_MESSAGE_DATA_UPDATE");
//				String strcommentnum=intent.getStringExtra("commentnum");
//				String strnew_fansnum=intent.getStringExtra("new_fansnum");
//				String strnew_activenum=intent.getStringExtra("new_activenum");
//				String strnew_snsfriendnum=intent.getStringExtra("new_snsfriendnum");
//				String strnew_zonefriend=intent.getStringExtra("new_zonefriend");
//				String strnew_zonesubscribe=intent.getStringExtra("new_zonesubscribe");
				/*String strnew_zonemic=intent.getStringExtra("new_zonemicnum");*/
//				String strfriendnum=intent.getStringExtra("friendnum");
//				hasStrangerMsg=intent.getBooleanExtra("hasStrangerMsg", false);
//				hasFriendMsg=intent.getBooleanExtra("hasFriendMsg", false);
//				if(DateUtils.isNum(strcommentnum))
//					commentnum+=Integer.parseInt(strcommentnum);
//				if(DateUtils.isNum(strnew_fansnum))
//					new_fansnum+=Integer.parseInt(strnew_fansnum);
//				if(DateUtils.isNum(strnew_activenum))
//					new_activenum+=Integer.parseInt(strnew_activenum);
//				if(DateUtils.isNum(strnew_snsfriendnum))
//					new_snsfriendnum+=Integer.parseInt(strnew_snsfriendnum);
//				if(DateUtils.isNum(strnew_zonefriend))
//					new_zonefriend+=Integer.parseInt(strnew_zonefriend);
//				if(DateUtils.isNum(strnew_zonesubscribe))
//					new_zonesubscribe+=Integer.parseInt(strnew_zonesubscribe);
				/*if(DateUtils.isNum(strnew_zonemic))
					new_zonemic+=Integer.parseInt(strnew_zonemic);*/
//				if(DateUtils.isNum(strfriendnum))
//					friendnum+=Integer.parseInt(strfriendnum);
				updateMasks();
			}else if(ACTION_ZONEBASE_USERINFO.equals(action)){//个人信息
				Log.d(TAG, "ACTION_ZONEBASE_USERINFO");
				updateUserInfo();
			}else if(MenuFragment.ACTION_SAFEBOX_MODE_CHANGED.equals(action)&&PARAM.equals(param)){//保险箱创建成功
				addToSafebox();
			}else if(LookLookActivity.UPDATE_MASK.equals(action)){//角标
				updateMasks();
			}else if(LookLookActivity.CONTACTS_REFRESH_DATA.equals(action)){
				updateMasks();
			} else if (SpaceCoverActivity.ACTION_SPACECOVER_UPLOAD_COMPLETED.equals(action)) {
				String picurl = intent.getStringExtra("picurl");
				String pathString = intent.getStringExtra("filepath");
				Log.d(TAG, "picurl=" + picurl);
				// Requester3.setUserSpaceCover(handler, shortpath);
				if (pathString != null && picurl != null) {
					/**
					 * 2、注册MediaMapping
					 */
					// 上传图片成功,将录音文件在mediamapping中注册
					MediaValue mv = new MediaValue();
					long fileSize = (new File(pathString)).length();
					mv.Belong = 1;
					mv.MediaType = 2;
					mv.localpath = pathString.replace(Environment
							.getExternalStorageDirectory().getPath(), "");
					mv.url = picurl;
					mv.UID = userID;
					mv.realSize = fileSize;
					mv.totalSize = fileSize;
					mv.Sync = 1;
					mv.SyncSize = fileSize;
					AccountInfo.getInstance(userID).mediamapping.setMedia(userID, picurl, mv);
					Log.d(TAG, "LocalPath=" + mv.localpath);
					Log.d(TAG, "HTTPPath=" + picurl + "");
					//2014-4-8 wuxiang
					Log.d(TAG, "switch_background 0");
					CmmobiClickAgentWrapper.onEvent(getActivity(), "switch_background", "0");
				} else {
					Prompt.Alert(getString(R.string.prompt_network_error));
					return;
				}

				// 更换封面成功保存缓存
				String userID = ActiveAccount.getInstance(
						ZApplication.getInstance()).getUID();
				AccountInfo.getInstance(userID).zoneBackGround = picurl;
				Prompt.Alert("更换封面成功");
				updateUserInfo();
			}
		}
	}
	@Override
	public void onDestroy() {
		Log.d(TAG,this.getClass().getName()+" onDestroy");
		if(receiver!=null)
			LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(receiver);
		super.onDestroy();
	}
	
	
	public class ContentPagerAdapter extends FragmentPagerAdapter {
		
		private ArrayList<XFragment> mFragments;
		public ContentPagerAdapter(FragmentManager fm) {
			super(fm);
			mFragments = new ArrayList<XFragment>();
			mFragments.add(FragmentHelper.getInstance(getActivity()).getMyZoneFragment());
			mFragments.add(FragmentHelper.getInstance(getActivity()).getVShareFragment());
		}

		@Override
		public int getCount() {
			return mFragments.size();
		}

		@Override
		public Fragment getItem(int position) {
			return mFragments.get(position);
		}

	}
	
}
