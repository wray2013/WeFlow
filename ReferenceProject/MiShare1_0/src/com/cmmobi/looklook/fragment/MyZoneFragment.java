package com.cmmobi.looklook.fragment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.AbsListView.OnScrollListener;
import cn.zipper.framwork.core.ZBroadcastReceiver;
import cn.zipper.framwork.core.ZDialog;
import cn.zipper.framwork.core.ZLog;
import cn.zipper.framwork.io.network.ZNetworkStateDetector;

import com.cmmobi.looklook.R;
import com.cmmobi.looklook.activity.LookLookActivity;
import com.cmmobi.looklook.activity.SettingPersonalInfoActivity;
import com.cmmobi.looklook.activity.TagsChocieActivity;
import com.cmmobi.looklook.activity.login.MicShareWelcomeActivity;
import com.cmmobi.looklook.common.adapter.MyZoneListAdapter;
import com.cmmobi.looklook.common.gson.GsonResponse3;
import com.cmmobi.looklook.common.gson.GsonResponse3.MyBind;
import com.cmmobi.looklook.common.gson.GsonResponse3.MyDiary;
import com.cmmobi.looklook.common.gson.GsonResponse3.MyDiaryList;
import com.cmmobi.looklook.common.gson.GsonResponse3.homeResponse;
import com.cmmobi.looklook.common.gson.GsonResponse3.listMyDiaryResponse;
import com.cmmobi.looklook.common.gson.Requester3;
import com.cmmobi.looklook.common.utils.CmmobiClickAgentWrapper;
import com.cmmobi.looklook.common.utils.DateUtils;
import com.cmmobi.looklook.common.view.Xdialog;
import com.cmmobi.looklook.common.view.pulltorefresh.PullToRefreshBase;
import com.cmmobi.looklook.common.view.pulltorefresh.PullToRefreshBase.Mode;
import com.cmmobi.looklook.common.view.pulltorefresh.PullToRefreshBase.OnHeaderScrolledListener;
import com.cmmobi.looklook.common.view.pulltorefresh.PullToRefreshBase.OnPullEventListener;
import com.cmmobi.looklook.common.view.pulltorefresh.PullToRefreshBase.OnRefreshListener2;
import com.cmmobi.looklook.common.view.pulltorefresh.PullToRefreshBase.State;
import com.cmmobi.looklook.common.view.pulltorefresh.PullToRefreshListView;
import com.cmmobi.looklook.info.profile.AccountInfo;
import com.cmmobi.looklook.info.profile.ActiveAccount;
import com.cmmobi.looklook.info.profile.CommonInfo;
import com.cmmobi.looklook.info.profile.DiaryManager;
import com.cmmobi.looklook.info.profile.LoginSettingManager;
import com.cmmobi.looklook.info.profile.VshareDataEntities;
import com.cmmobi.looklook.info.weather.MyWeather;
import com.cmmobi.looklook.info.weather.MyWeatherInfo;
import com.cmmobi.looklook.offlinetask.OfflineTaskManager;
import com.cmmobivideo.wedget.XWgPreviewFrameLayout.onSizeChangedListener;

/**
 * @author wuxiang
 * @email wuxiang@cmmobi.com
 * @date 2013-10-21
 */
public class MyZoneFragment extends XFragment implements
		OnRefreshListener2<ListView>, OnHeaderScrolledListener<ListView>, OnPullEventListener<ListView>{
	private static final String TAG = MyZoneFragment.class.getSimpleName();

	private View contentView;
	private PullToRefreshListView xlvMyZone;
	private ListView lvMyZoneList;
	private MyZoneListAdapter zoneListAdapter;
	//public static final String userID="fcfc615c0550f040be0b8790017bf81093a8";
	private ArrayList<MyZoneItem> myZoneItems=new ArrayList<MyZoneFragment.MyZoneItem>();

    private boolean isFirstOpen = true;
	private boolean isfromRefresh = false;
	private WeatherUpdateReceiver updateWeatherReceiver;
	
	private static DisplayMetrics dm = new DisplayMetrics();
	
	private static final int HANDLER_LOAD_UA = 568901;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		//for test
		Log.d(TAG, "container="+container);
		getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
		contentView = inflater.inflate(R.layout.fragment_my_zone, null);
		xlvMyZone = (PullToRefreshListView) contentView
				.findViewById(R.id.xlv_my_zone);
		xlvMyZone.setShowIndicator(false);
		xlvMyZone.setOnRefreshListener(this);
		xlvMyZone.setOnHeaderScrolledListener(this);
		xlvMyZone.setOnPullEventListener(this);
		xlvMyZone.setPullDownSilenceEnabled(true);
		xlvMyZone.setScrollEmptyView(false);
		xlvMyZone.setPullToRefreshOverScrollEnabled(false);
//		xlvMyZone.setShowViewWhileRefreshing(false);
		lvMyZoneList = xlvMyZone.getRefreshableView();
		//xlvMyZone.setOverScrollMode(View.OVER_SCROLL_ALWAYS);
		//lvMyZoneList.setOverScrollMode(ListView.OVER_SCROLL_ALWAYS);
		lvMyZoneList.setStackFromBottom(false);
		diaryManager.setMyZoneDataChangedListener(new MyZoneDataChangedListener());
		ZoneBaseFragment zoneBaseFragment=FragmentHelper.getInstance(getActivity()).getZoneBaseFragment();
		zoneListAdapter=new MyZoneListAdapter(zoneBaseFragment, myZoneItems,this);
		lvMyZoneList.setAdapter(zoneListAdapter);
//		loadData();
//		setListViewHeightBasedOnChildren(lvMyZoneList, zoneListAdapter);
		/*xlvMyZone.setOnScrollListener(new OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if(firstVisibleItem==0){
                    Log.e("log", "滑到顶部");
                }
                if(visibleItemCount+firstVisibleItem==totalItemCount){
                    Log.e("log", "滑到底部");
                }
            }
        });*/
		IntentFilter intentfilter = new IntentFilter();
		intentfilter.addAction(MyWeatherInfo.ACTION_UPDATE_WEATHERINFO);
		intentfilter.addAction(ACTION_ADD_TAGS);
		updateWeatherReceiver = new WeatherUpdateReceiver();
		LocalBroadcastManager.getInstance(getActivity()).registerReceiver(updateWeatherReceiver, intentfilter);
//		MyWeatherInfo.getInstance(MainApplication.getAppInstance()).updateWeather(false);
		isFirstOpen = true;
		
		verifyUseridAndDialog();
		
		return contentView;
	}
	
	
    public static void setListViewHeightBasedOnChildren(ListView listView, MyZoneListAdapter adapter) {
    	MyZoneListAdapter listAdapter = adapter; 
        if (listAdapter == null) {
            // pre-condition
            return;
        }

        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            Log.d("ImageList", "totalHeight = " + totalHeight + ";ItemHeight = " + listItem.getMeasuredHeight());
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
    }
	
	private void verifyUseridAndDialog(){
		String userid = ActiveAccount.getInstance(this.getActivity()).getLookLookID();
		if(ActiveAccount.verifyUseridSuccess(userid)){
			loadData();
			LocalBroadcastManager.getInstance(this.getActivity()).sendBroadcast(new Intent(SafeboxVShareFragment.ACTION_VSHARE_CHANGED));
			return;
		}
		
		// 没有加载完
		if(!MicShareWelcomeActivity.UaLoadEnd){
			ZDialog.show(R.layout.progressdialog, false, true, MyZoneFragment.this.getActivity());
			handler.sendEmptyMessageDelayed(HANDLER_LOAD_UA, 100);
			return;
		}
		
		Xdialog dialog = new Xdialog.Builder(this.getActivity())
		.setMessage("网络不给力啊\n请确认网络连接后重试")
		.setNegativeButton("重试", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				String userid = ActiveAccount.getInstance(MyZoneFragment.this.getActivity()).getLookLookID();
				if(!ActiveAccount.verifyUseridSuccess(userid)){
					ZDialog.show(R.layout.progressdialog, false, true, MyZoneFragment.this.getActivity());
					Requester3.submitUA(getHandler());
				}
			}
		}).create();
		dialog.setCancelable(false);
		dialog.show();
	}
	
	/**
	 * 更新用户信息
	 */
	public void updateUserInfo(){
		if(null==contentView)return;
		if(myZoneItems.size()>0&&myZoneItems.get(0) instanceof UserInfo ){
			((UserInfo)myZoneItems.get(0)).backgroundUrl=accountInfo.zoneBackGround;
			((UserInfo)myZoneItems.get(0)).headUrl=accountInfo.headimageurl;
			((UserInfo)myZoneItems.get(0)).nickname=accountInfo.nickname;
			((UserInfo)myZoneItems.get(0)).sex=accountInfo.sex;
			((UserInfo)myZoneItems.get(0)).signature=accountInfo.signature;
			zoneListAdapter.notifyDataSetChanged();
		}
	}
	
	/**
	 * 更新空间背景图
	 */
	public void updateBackground(){
		if(null==contentView)return;
		if(myZoneItems.size()>0&&myZoneItems.get(0) instanceof UserInfo ){
			((UserInfo)myZoneItems.get(0)).backgroundUrl=accountInfo.zoneBackGround;
			zoneListAdapter.notifyDataSetChanged();
		}
	}
	
	/**
	 * 更新天气、位置信息
	 */
	public void updateWeatherInfo(int type){
		if(null==contentView)return;
		if(myZoneItems.size()>0&&myZoneItems.get(0) instanceof UserInfo ){
			((UserInfo)myZoneItems.get(0)).myweather=accountInfo.myWeather;
			((UserInfo)myZoneItems.get(0)).weathertype=type;
			accountInfo.weathertype = type;
			zoneListAdapter.notifyDataSetChanged();
		}
	}
	
	//进入页面和下拉刷新时加载数据
	public void loadData(){
		//UA请求返回成功后重新请求日记列表
		//FragmentHelper.getInstance(getActivity()).getMyZoneFragment().loadData();
		if(null==contentView)return;
		initUserInfo();
		index=0;
		loadLocalData();
		requestServer(true);
	}
	
	private int index;
	//加载更多数据
	private void loadMore(){
//		int startNum=getMyZoneDiaryGroupSum();//myZoneItems第一项为userinfo
		ArrayList<MyZoneItem> localList=diaryManager.getMyZoneItems(++index);
		myZoneItems.clear();
		myZoneItems.addAll(localList);
		zoneListAdapter.notifyDataSetChanged();
		if(localList.size()<DiaryManager.PAGE_SIZE*(index+1)){
			index--;
			Log.d(TAG, "no more data");
			if(xlvMyZone != null) {
				xlvMyZone.setNoMoreData(getActivity(), false);
			}
		} else {
			if(xlvMyZone != null) {
				xlvMyZone.setNoMoreData(getActivity(), true);
			}
		}
		if(diaryManager.getLocalDiarySum()>7)
			bundleMobilePrompt();
		bundleMobilePrompt(System.currentTimeMillis());
	}
	
	//获取以显示的日记组的总数
	private int getMyZoneDiaryGroupSum(){
		int sum=0;
		for(int i=myZoneItems.size()-1;i>=0;i--){
			MyZoneItem zoneItem=myZoneItems.get(i);
			if(zoneItem instanceof DiariesItem){
				ArrayList<MyDiaryList> myDiaryLists=((DiariesItem) zoneItem).diaryGroups;
				if(myDiaryLists!=null&&myDiaryLists.size()>0){
					sum+=myDiaryLists.size();
				}
			}
		}
		return sum;
	}
	
	//请求网络数据 isFirst-true请求最新数据 false-请求历史数据
	private void requestServer(boolean isFirst){
//		String diaryWidth=zoneListAdapter.getDiaryWidth();
		zoneListAdapter.setRefreshing(true);
		if(isFirst){//请求最新数据
			zoneListAdapter.startLoading();
			String firstTime=diaryManager.getMyZoneFirstTime();
			Requester3.homePage(handler, userID, firstTime, "1", "", "", "", "", true);
		}else{//请求历史数据
			String lastTime=diaryManager.getMyZoneLastTime();
			Requester3.requestMyDiary(handler, userID, lastTime, "2", "", "", true);
		}
	}
	
	//加载本地数据
	private boolean loadLocalData(){
		myZoneItems.clear();
		ArrayList<MyZoneItem> localList=diaryManager.getMyZoneItems(0);
		myZoneItems.addAll(localList);
		
		if(diaryManager.getLocalDiarySum()>7)
			bundleMobilePrompt();
		bundleMobilePrompt(System.currentTimeMillis());
		zoneListAdapter.notifyDataSetChanged();
		return myZoneItems.size()>1;
	}
	
	private static String IS_BUNDLE_MOBILE="IS_BUNDLE_MOBILE";
	private static String IS_BUNDLE_MOBILE_INSTALL_TIMEMILLI="IS_BUNDLE_MOBILE_INSTALL_TIMEMILLI";
	//绑定手机提示
	private void bundleMobilePrompt(){
		LoginSettingManager lsm = accountInfo.setmanager;
		MyBind mb=null;
		if(lsm!=null)
			mb= lsm.getBinding_type(LoginSettingManager.BINDING_TYPE_PHONE, "");
		if(mb!=null)return;//已绑定手机号的不提示绑定信息
		if(getActivity()== null) return;
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
		if(0==sp.getInt(IS_BUNDLE_MOBILE, 0)){
			sp.edit().putInt(IS_BUNDLE_MOBILE, 1).commit();
			new Xdialog.Builder(getActivity())
			.setMessage(R.string.myzone_bundle_mobile_content)
			.setPositiveButton(R.string.myzone_bundle_mobile_ok, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					//2014-4-8 wuxiang
//					CmmobiClickAgentWrapper.onEvent(getActivity(), "finshed_blingding2");
					getActivity().startActivity(new Intent(getActivity(),SettingPersonalInfoActivity.class).putExtra(EXTRA_FROM_PROMPT, "EXTRA_FROM_PROMPT"));
				}
			})
			.setNegativeButton(R.string.myzone_bundle_mobile_cancel, null)
			.create().show();
		}
	}
	
	public static final String EXTRA_FROM_PROMPT="EXTRA_FROM_PROMPT";
	//绑定手机提示
	private void bundleMobilePrompt(long timemilli){
		LoginSettingManager lsm = accountInfo.setmanager;
		MyBind mb=null;
		if(lsm!=null)
			mb= lsm.getBinding_type(LoginSettingManager.BINDING_TYPE_PHONE, "");
		if(mb!=null)return;//已绑定手机号的不提示绑定信息
		if(getActivity()== null) return;
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
		if(-1==sp.getLong(IS_BUNDLE_MOBILE_INSTALL_TIMEMILLI, 0)){//只弹一次
			return;
		}else if(0==sp.getLong(IS_BUNDLE_MOBILE_INSTALL_TIMEMILLI, 0)){//记录应用第一次启动时间
			sp.edit().putLong(IS_BUNDLE_MOBILE_INSTALL_TIMEMILLI, timemilli).commit();
		}else{
			if(15*24*3600*1000l<timemilli-sp.getLong(IS_BUNDLE_MOBILE_INSTALL_TIMEMILLI, 0)){//大于15天，弹绑定手机提示
				sp.edit().putLong(IS_BUNDLE_MOBILE_INSTALL_TIMEMILLI, -1).commit();
				new Xdialog.Builder(getActivity())
				.setMessage(R.string.myzone_bundle_mobile_content)
				.setPositiveButton(R.string.myzone_bundle_mobile_ok, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						//2014-4-8 wuxiang
//						CmmobiClickAgentWrapper.onEvent(getActivity(), "finshed_blingding2");
						getActivity().startActivity(new Intent(getActivity(),SettingPersonalInfoActivity.class).putExtra(EXTRA_FROM_PROMPT, "EXTRA_FROM_PROMPT"));
					}
				})
				.setNegativeButton(R.string.myzone_bundle_mobile_cancel, null)
				.create().show();
			}
		}
	}
	
	/*	2.	加入保险箱的内容在我的空间页不显示
		3.	单内容与组内容加入保险箱单独处理.
		如A为单内容 B为组内容,B包含A.
		将A加为保险箱,则B不加入保险箱
		将B加入保险箱,则A不加入保险箱
		4.	放置非微享内容到保险箱时,提示用户"此内容放入保险箱后是否需要取消分享状态",用户点击"否",此内容被放入保险箱且我在我的空间看不到.但Ta人在我的空间可以看到.我在我的朋友圈看不到.Ta人在我的朋友圈可以看到
		移出保险箱后，我在我的空间可以看到，我在我的朋友圈可以看到
		5.	对于微享的内容加入保险箱,不提示用户"此内容放入保险箱后是否需要取消分享状态",只要放入保险箱,此内容在我的空间页不显示
		6.	对于内容分享过多次．如公开微享朋友．将此内容加入保险箱时，提示用户"此内容放入保险箱后是否需要取消分享状态",用户点击"是"，需要取消内容的公开与朋友分享记录，微享分享记录不取消
*/
	/**
	 * 加入保险箱
	 */
	public void addToSafebox(){
		if(null==contentView)return;
//		if(isPrompt()){
//			new Xdialog.Builder(getActivity())
//			.setTitle("提示")
//			.setMessage("此内容放入保险箱后是否需要取消分享状态?")
//			.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
//				@Override
//				public void onClick(DialogInterface dialog, int which) {
//					addSafeboxRequest("1");
//				}
//			})
//			.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
//				@Override
//				public void onClick(DialogInterface dialog, int which) {
//					addSafeboxRequest("0");
//				}
//			})
//			.create().show();
//		}else{
//		}
		addSafeboxRequest("0");
	}
	
	//请求服务器加入保险箱
	private void addSafeboxRequest(String isprivate){
		ArrayList<MyDiaryList> removeList=zoneListAdapter.getCheckedList();
		String uuids=getDiaryGroupUUIDs(removeList);
		if(!TextUtils.isEmpty(uuids)){
			int num=uuids.split(",").length;
			HashMap<String, String> hs=new HashMap<String, String>();
			hs.put("label", "1");
//			hs.put("labe12", uuids);
			hs.put("labe12", num+"");
			//2014-4-8 wuxiang
			CmmobiClickAgentWrapper.onEvent(getActivity(), "save_in_safebox", hs);
			OfflineTaskManager.getInstance().addSafeboxAddTask("", uuids);
//			OfflineTaskManager.getInstance().addSafeboxRemoveTask("", uuids);
			//更新日记保险箱状态
			diaryManager.addSafebox(removeList);
			diaryManager.notifyMySafeboxChanged();
//			diaryManager.removeSafebox(removeList);
			//更新日记列表
			updateViews(null);
			//显示正常标题栏
			showNormalTitle();
			purgeCheckedView();
		}
	}
	
	//判断加入保险箱时是否弹出提示框 分享次数-微享次数>0 弹，其他一律不弹
	private boolean isPrompt(){
		ArrayList<MyDiaryList> removeList=zoneListAdapter.getCheckedList();
		for(int i=0;i<removeList.size();i++){
			MyDiaryList diaryList=removeList.get(i);
			MyDiary myDiary=diaryManager.findMyDiaryByUUID(diaryList.diaryuuid);
			if(DateUtils.isNum(myDiary.share_mic_count)&&DateUtils.isNum(myDiary.share_count)&&(Integer.parseInt(myDiary.share_count)-Integer.parseInt(myDiary.share_mic_count)>0))//微享过
				return true;
		}
		return false;
	}
	
	/**
	 * 添加标签
	 */
	public void addTags(){
		if(null==contentView)return;
		startActivity(new Intent(getActivity(),TagsChocieActivity.class));
	}

	/**
	 * 删除选中日记（组）
	 */
	public void deleteDiary(){
		if(null==contentView)return;
		//获取正在微享的日记项
		int vshareItems=0;
		VshareDataEntities localVShareData=AccountInfo.getInstance(userID).vshareLocalDataEntities;
		if(localVShareData!=null){
			vshareItems=localVShareData.getContainDiaryNum(getDiaryGroupUUIDs(zoneListAdapter.getCheckedList()));
		}
		if(!isAdded() || getActivity()==null) return;
		if(0==vshareItems){
			new Xdialog.Builder(getActivity())
			.setMessage(getString(R.string.myzone_delete_diary))
			.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					ArrayList<MyDiaryList> removeList=zoneListAdapter.getCheckedList();
					String removeIDs=getServreRemoveIDs(removeList);
					if(!"".equals(removeIDs)&&removeIDs!=null)
						OfflineTaskManager.getInstance().addDiaryRemoveTask(removeIDs);
					//删除选中日记
					diaryManager.removeDiaryGroupByDiaryGroupList(removeList);
					diaryManager.notifyMySafeboxChanged();
					//更新日记列表
					updateViews(null);
					//显示正常标题栏
					showNormalTitle();
					purgeCheckedView();
				}
			})
			.setNegativeButton(android.R.string.cancel, null)
			.create().show();
		}else{
			String msg=getString(R.string.myzone_delete_diary_with_vshare, vshareItems);
			SpannableStringBuilder style=new SpannableStringBuilder(msg);
			if(vshareItems>9){
				style.setSpan(new ForegroundColorSpan(Color.parseColor("#0C80FF")),10,12,Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
			}else{
				style.setSpan(new ForegroundColorSpan(Color.parseColor("#0C80FF")),10,11,Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
			}
			new Xdialog.Builder(getActivity())
			.setMessage(style)
			.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					ArrayList<MyDiaryList> removeList=zoneListAdapter.getCheckedList();
					String removeIDs=getServreRemoveIDs(removeList);
					if(!"".equals(removeIDs)&&removeIDs!=null)
						OfflineTaskManager.getInstance().addDiaryRemoveTask(removeIDs);
					//删除选中日记
					diaryManager.removeDiaryGroupByDiaryGroupList(removeList);
					diaryManager.notifyMySafeboxChanged();
					//更新日记列表
					updateViews(null);
					//显示正常标题栏
					showNormalTitle();
					purgeCheckedView();
				}
			})
			.setNegativeButton(android.R.string.cancel, null)
			.create().show();
		}
	}
	
	//从指定列表中返回日记组uuid，逗号分隔
	private String getDiaryGroupUUIDs(ArrayList<MyDiaryList> removeList){
		String uuids="";
		if(removeList!=null){
			for(int i=0;i<removeList.size();i++){
				MyDiaryList myDiaryList=removeList.get(i);
				if(myDiaryList!=null){
					uuids+=myDiaryList.diaryuuid;
					if(i<removeList.size()-1){
						uuids+=",";
					}
				}
			}
		}
		return uuids;
	}
	
	//从指定列表中返回日记组id（不为空），已逗号分隔
	private String getServreRemoveIDs(ArrayList<MyDiaryList> removeList){
		String serverIDs="";
		if(removeList!=null){
			for(int i=0;i<removeList.size();i++){
				MyDiaryList myDiaryList=removeList.get(i);
				if(myDiaryList!=null&&myDiaryList.diaryid!=null&&myDiaryList.diaryid.length()>0){
					serverIDs+=myDiaryList.diaryid;
					if(i<removeList.size()-1){
						serverIDs+=",";
					}
				}
			}
		}
		return serverIDs;
	}
	
	
	
	//获取现有列表中最旧的日记更新时间
	private long getUpdateLastTime(){
		for(int i=myZoneItems.size()-1;i>=0;i--){
			MyZoneItem zoneItem=myZoneItems.get(i);
			if(zoneItem instanceof DiariesItem){
				ArrayList<MyDiaryList> myDiaryLists=((DiariesItem) zoneItem).diaryGroups;
				if(myDiaryLists!=null&&myDiaryLists.size()>0){
					String updateTime=myDiaryLists.get(myDiaryLists.size()-1).create_time;
					if(DateUtils.isNum(updateTime))
						return Long.parseLong(updateTime);
				}
			}
		}
		return 0;
	}

	private int msgNum;
	
	/**
	 * 更新空间背景上面的 “您有X条新评论”
	 */
	public void updateComment(int num){
		if(myZoneItems.size()>0&&myZoneItems.get(0) instanceof UserInfo ){
			((UserInfo)myZoneItems.get(0)).msgNum=num;
			this.msgNum=num;
			zoneListAdapter.notifyDataSetChanged();
		}
	}
	
	private boolean isXdlgShowing = false;
	
	/**
	 * 更新主页天气、位置
	 * @param isActive
	 *   是否来自主动更新
	 */
	private void setWeather(boolean isActive) {
		if(getActivity() == null) {
			Log.d(TAG, "getActivity() is null");
			return;
		}
		Xdialog xdlg = new Xdialog.Builder(getActivity())
		.setTitle("提醒")
		.setMessage("请打开定位服务，并允许looklook使用定位服务")
		.setPositiveButton(android.R.string.ok,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog,
							int which) {
						isXdlgShowing = false;
					}
				}).create();
		MyWeather weather = accountInfo.myWeather;
		// I.无GPS和基站
		if (!ZNetworkStateDetector.isGpsOpened2() && !ZNetworkStateDetector.isConnected()) {
			// 1.每次进入app
			if (isFirstOpen) {
				Log.d(TAG, "每次进入app");
				if(!isXdlgShowing) {
					xdlg.show();
					isXdlgShowing = true;
				}
			}
			//2.无GPS，有天气缓存
			if(hasWeatherCache(weather)) {
				Log.d(TAG, "无GPS,有天气缓存");
				updateWeatherInfo(2);
			//3.无GPS，无天气缓存
			} else {
				Log.d(TAG, "无GPS,无天气缓存");
				updateWeatherInfo(1);
				if(!isXdlgShowing && isActive && !isfromRefresh) {
					xdlg.show();
					isXdlgShowing = true;
				}
			}
			
		// II.开启GPS或基站(WIFI)
		} else {
			Log.d(TAG, "开启了GPS或网络");
			updateWeatherInfo(2);
		}
		isfromRefresh = false;
	}
	
	/**
	 * 是否存在天气缓存
	 * @param weather
	 * @return
	 */
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
	}
	
	//显示标题栏
	private void showNormalTitle(){
		ZoneBaseFragment zoneBaseFragment=FragmentHelper.getInstance(getActivity()).getZoneBaseFragment();
		zoneBaseFragment.showNormalTitle();
	}
	
	//判断是否是选中状态
	private boolean getIsChecked(){
		ZoneBaseFragment zoneBaseFragment=FragmentHelper.getInstance(getActivity()).getZoneBaseFragment();
		return zoneBaseFragment.isCheckedTitleShow();
	}
	
	//隐藏标题栏
	private void hideNormalTitle(){
		ZoneBaseFragment zoneBaseFragment=FragmentHelper.getInstance(getActivity()).getZoneBaseFragment();
		zoneBaseFragment.hideNormalTitle();
	}
	
	private void hideFeaturelist(){
		ZoneBaseFragment zoneBaseFragment=FragmentHelper.getInstance(getActivity()).getZoneBaseFragment();
	}

	//清除日记选中状态
	public void purgeCheckedView(){
		if(null==contentView)return;
		//list中清除选中状态
		zoneListAdapter.purgeCheckedView();
	}

	@Override
	public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
		
/*		handler.post(new Runnable() {
			
			@Override
			public void run() {
				xlvMyZone.onRefreshComplete();
			}
		});*/
		
		isfromRefresh = true;
		loadData();
		updateComment(msgNum);
	}

	@Override
	public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
		loadMore();
		updateComment(msgNum);
		handler.post(new Runnable() {
			
			@Override
			public void run() {
				xlvMyZone.onRefreshComplete();
				
			}
		});
	}
	
	private void onRefreshComplete(final long delay) {
		if(xlvMyZone != null) {
			handler.post(new Runnable() {
				
				@Override
				public void run() {
					try {
						Thread.sleep(delay);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					xlvMyZone.onRefreshComplete();
					
				}
			});
		}
	}
	
	@Override
	public void onHeaderScrolled(PullToRefreshBase<ListView> refreshView,
			int value, Mode direction, State state) {
		// TODO Auto-generated method stub
		
		if(direction == Mode.PULL_FROM_START) {
//			Log.d("xxx", "onHeaderScrolled value = " + value + "; State = " + state.name());
			
			if(state == State.REFRESHING) {
				zoneListAdapter.enlargeBackgroudView(value);
//				zoneListAdapter.enlargeBackgroudView(Math.max(Math.abs(value), 50));
				return;
			}
			if(value <= 0 || state == State.RESET) {
				zoneListAdapter.enlargeBackgroudView(value);
				return;
			}
		}
	}
	
	@Override
	public boolean handleMessage(Message msg) {
		switch (msg.what) {
		case HANDLER_LOAD_UA:   //welcome page load ua. 未获取userid
			
			if(!MicShareWelcomeActivity.UaLoadEnd){
				handler.sendEmptyMessageDelayed(HANDLER_LOAD_UA, 100);
			}else{
				ZDialog.dismiss();
				verifyUseridAndDialog();
			}
			
			break;
		case Requester3.RESPONSE_TYPE_UA:   // ua  获取userid
			ZDialog.dismiss();
			GsonResponse3.uaResponse uaResponse = (GsonResponse3.uaResponse) msg.obj;
			// 之前登陆过 需完成自动登陆
			if(uaResponse!=null){
				if(uaResponse.equipmentid!=null){
					CommonInfo.getInstance().equipmentid = uaResponse.equipmentid;
				}
				// 完成用户数据更新
				String micshare = uaResponse.mishare_no;
				String userid = uaResponse.userid;
				if(TextUtils.isEmpty(userid)){
					verifyUseridAndDialog();
				}else{
					CmmobiClickAgentWrapper.setUserid(this.getActivity(), userid);
					ActiveAccount.getInstance(this.getActivity()).updateMicShareNo(userid, micshare);
					//initUserInfo();
					loadData();
					((LookLookActivity)getActivity()).initConfig();
				}
			}else{
				verifyUseridAndDialog();
			}
			break;
		case Requester3.RESPONSE_TYPE_HOME:{//个人主页
			try {
//				zoneListAdapter.enlargeBackgroudView(0);
				if(null==msg.obj){
					xlvMyZone.onRefreshComplete();
					zoneListAdapter.stopLoading();
					return false;
				}
				homeResponse response=(homeResponse) msg.obj;
				if("0".equals(response.status)&&userID.equals(response.userid)){
//				if("0".equals(response.status)){
					if("1".equals(response.is_refresh))
						diaryManager.resetMyZoneLastDiaryTime(response.last_diary_time);
					diaryManager.updateUserInfo(response.headimageurl,response.background,response.nickname,response.sex,response.signature);
					diaryManager.updateMyZoneDiaryTime(response.first_diary_time,response.last_diary_time);
					diaryManager.removeDiaryByIDs(response.removediarys);
					if(response.diaryids!=null&&response.diaries!=null){
						diaryManager.updateDiarySyncStatus(4,response.diaries);//更新sync_status状态为已同步
						diaryManager.saveDiaries(Arrays.asList(response.diaryids), Arrays.asList(response.diaries));
					}
					if("1".equals(response.hasnextpage)&&"0".equals(response.is_refresh)){
						//当有下一页时，继续想上请求最新日记
						requestServer(true);
					}else{
						requestServer(false);
						Log.d(TAG, "RESPONSE_TYPE_HOME no more data");
					}
				}else{
					xlvMyZone.onRefreshComplete();
					Log.e(TAG, "RESPONSE_TYPE_HOME error! status="+response.status +" userID="+userID +" response.userid="+response.userid);
					zoneListAdapter.stopLoading();
				}
			} catch (Exception e) {
				e.printStackTrace();
				zoneListAdapter.stopLoading();
			}
			break;}
		case Requester3.RESPONSE_TYPE_LIST_MY_DIARY:{//日记列表
//			zoneListAdapter.enlargeBackgroudView(0);
			if(null==msg.obj){
				xlvMyZone.onRefreshComplete();
				return false;
			}
			listMyDiaryResponse response=(listMyDiaryResponse) msg.obj;
			if("0".equals(response.status)){
				if(response.diaryids!=null&&response.diaries!=null&&response.diaries.length>0&&!userID.equals(response.diaries[0].userid)){
					return false;//他人空间日记可能会和自己空间有冲突，用userid加以区分
				}
				if("1".equals(response.is_refresh))
					diaryManager.resetMyZoneLastDiaryTime(response.last_diary_time);
				diaryManager.updateMyZoneDiaryTime(response.first_diary_time,response.last_diary_time);
				diaryManager.removeDiaryByIDs(response.removediarys);
				diaryManager.saveDiaries(Arrays.asList(response.diaryids), Arrays.asList(response.diaries));
				if("1".equals(response.hasnextpage)){
					requestServer(false);
				}else{
					xlvMyZone.onRefreshComplete();
					Log.d(TAG, "RESPONSE_TYPE_LIST_MY_DIARY no more data");
				}
			}else{
				xlvMyZone.onRefreshComplete();
				Log.e(TAG, "RESPONSE_TYPE_LIST_MY_DIARY error! status="+response.status);
			}
			zoneListAdapter.stopLoading();
			break;}

		default:
			break;
		}
		return false;
	}
	
	
	public static class MyZoneItem{}
	
	public static class UserInfo extends MyZoneItem{
		public String headUrl;
		public String backgroundUrl;
		public String nickname;
		public String backname;
		public String signature;
		public String sex; // 0男 1女 2未知
		public int msgNum;//最新评论数
		public String userid;
		public String misharecount;//我和他的微享数
		
		public String isblacklist;// 0 不是我的黑名单  1是黑名单(显示用户是不是当前用户的黑名单)
		public String isattention;// 0 未关注  1是已关注/显示用户是否关注当前用户
		
		public MyWeather myweather;
		public int weathertype;
		
	}
	
	public static class DiariesItem extends MyZoneItem{
		public String strDate;//每项开头时间
		public SpannableStringBuilder textStyle;
		public ArrayList<MyDiaryList> diaryGroups=new ArrayList<MyDiaryList>();//日记组
		public ArrayList<MyDiary[]> diaries=new ArrayList<MyDiary[]>();//每项中的日记，有可能是单个日记，有可能是日记数组
	}
	
	public static class DiaryShareItem extends MyZoneItem{
		public String strDate;//每项开头时间
		public SpannableStringBuilder textStyle;
		public String shareContent;//分享内容
		public MyDiaryList diaryGroup;//日记组
		public MyDiary[] diaries;//每项中的日记，有可能是单个日记，有可能是日记数组
	}
	
	
	class MyZoneDataChangedListener implements com.cmmobi.looklook.info.profile.DiaryManager.MyZoneDataChangedListener{

		@Override
		public void dataChanged() {
			Log.d(TAG, "dateChanged");
			//此方法可能被非UI线程调用
			handler.post(new Runnable() {
				@Override
				public void run() {
					updateViews(null);
				}
			});
		}
	}
	
	private boolean isResume=true;
	
	@Override
	public void onPause() {
		Log.d(TAG, "onPause");
		isResume=false;
		super.onPause();
	}

	@Override
	public void onResume() {
		isResume=true;
		if(needUpdate){
			updateItems();
		}
		super.onResume();
	}
	
	private boolean needUpdate=false;
	@Override
	public void updateViews(Object data) {
		if(null==contentView)return;
		if(!isResume||isHidden()){
			Log.d(TAG, "update delay");
			needUpdate=true;
		}else{
			updateItems();
		}
	}
	
	private void updateItems(){
		Log.d(TAG, "updateItems");
		
		/*handler.post(new Runnable() {
			@Override
			public void run() {
				xlvMyZone.onRefreshComplete();
			}
		});*/
		
		ArrayList<MyZoneItem> updatedList=diaryManager.updateMyZoneItems(getUpdateLastTime());
		myZoneItems.clear();
		myZoneItems.addAll(updatedList);
		zoneListAdapter.notifyDataSetChanged();
		
		if(diaryManager.getLocalDiarySum()>7)
			bundleMobilePrompt();
		bundleMobilePrompt(System.currentTimeMillis());
	}
	
	@Override
	public void onHiddenChanged(boolean hidden) {
		if(null==contentView)return;
		Log.d(TAG, "hidden="+hidden);
		if(!hidden&&needUpdate){
			needUpdate=false;
			updateItems();
		}
		super.onHiddenChanged(hidden);
	}

	public static final String ACTION_ADD_TAGS="ACTION_ADD_TAGS";
	private class WeatherUpdateReceiver extends ZBroadcastReceiver {
		private WeatherUpdateReceiver() {
			addAction(MyWeatherInfo.ACTION_UPDATE_WEATHERINFO);
		}
		@Override
		public void onReceive(Context context, Intent intent) {
			String action=intent.getAction();
			if(MyWeatherInfo.ACTION_UPDATE_WEATHERINFO.equals(action)) {//天气、位置
				boolean isactive = intent.getBooleanExtra("isActive", false);
				ZLog.e("ACTION_UPDATE_WEATHERINFO isactive = " + isactive);
				setWeather(isactive);
				isFirstOpen = false;
			}else if(ACTION_ADD_TAGS.equals(action)){
				String[] tagids=intent.getStringArrayExtra("tagids");
				String temTagids="";
				for(int i=0;i<tagids.length;i++){
					temTagids+=tagids[i];
					if(i!=tagids.length-1)
						temTagids+=",";
				}
				//更新本地日记标签
				ArrayList<MyDiaryList> myDiaryList=(ArrayList<MyDiaryList>) zoneListAdapter.getCheckedList().clone();
				diaryManager.updateDiaryTags(myDiaryList,tagids);
				String diaryuuids=diaryManager.getDiaryUUIDs(myDiaryList);
				//请求服务器
				OfflineTaskManager.getInstance().addPositionOrTagTask("", diaryuuids, temTagids, "");
				//显示正常标题栏
				showNormalTitle();
				purgeCheckedView();
			}
		}
	}
	
	
	
	@Override
	public void onDestroy() {
		Log.d(TAG,this.getClass().getName()+" onDestroy");
		super.onDestroy();
	}

	@Override
	public void onDestroyView() {
		LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(updateWeatherReceiver);
		super.onDestroyView();
	}

	@Override
	public void onPullEvent(PullToRefreshBase<ListView> refreshView,
			State state, Mode direction) {
		// TODO Auto-generated method stub
//		Log.d(TAG, "Current State: " + state.name() + ";Direction: " + direction.name());
		if(direction == Mode.PULL_FROM_START) {
			if(state == State.RESET) {
//				zoneListAdapter.enlargeBackgroudView(0);
				zoneListAdapter.setRefreshing(false);
			}
		}
	}
}
