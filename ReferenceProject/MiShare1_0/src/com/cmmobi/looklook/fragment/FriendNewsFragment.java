package com.cmmobi.looklook.fragment;

import java.util.ArrayList;
import java.util.List;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import cn.zipper.framwork.core.ZApplication;

import com.cmmobi.looklook.MainApplication;
import com.cmmobi.looklook.R;
import com.cmmobi.looklook.activity.DiaryPreviewActivity;
import com.cmmobi.looklook.activity.LookLookActivity;
import com.cmmobi.looklook.common.adapter.ContactsWithContentAdapter;
import com.cmmobi.looklook.common.gson.GsonResponse3.Contents;
import com.cmmobi.looklook.common.gson.GsonResponse3.MyDiary;
import com.cmmobi.looklook.common.gson.GsonResponse3.MyDiaryList;
import com.cmmobi.looklook.common.gson.GsonResponse3.friendNewsResponse;
import com.cmmobi.looklook.common.gson.Requester3;
import com.cmmobi.looklook.common.service.RemoteManager;
import com.cmmobi.looklook.common.view.pulltorefresh.PullToRefreshBase;
import com.cmmobi.looklook.common.view.pulltorefresh.PullToRefreshBase.Mode;
import com.cmmobi.looklook.common.view.pulltorefresh.PullToRefreshBase.OnRefreshListener2;
import com.cmmobi.looklook.common.view.pulltorefresh.PullToRefreshListView;
import com.cmmobi.looklook.info.profile.AccountInfo;
import com.cmmobi.looklook.info.profile.ActiveAccount;
import com.cmmobi.looklook.prompt.Prompt;

/**
 * 好友动太界面
 * 
 * @author youtian
 * 
 */

public class FriendNewsFragment extends TitleRootFragment implements OnItemClickListener, OnRefreshListener2<ListView> {

	private View contentView;
	private PullToRefreshListView xlv_news;
	ListView newsListView;

	private String userID;
	private AccountInfo accountInfo;

	private List<Contents> wrapUserList = new ArrayList<Contents>();

	private ContactsWithContentAdapter newsAdapter;
	
	private Context context;
	private Handler handler;
	private Boolean pullDown;
	private Boolean isHasNextPage = true;
	private static final int STOP_REFRESH = 0xfff1250;
	
	private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
		  @Override
		  public void onReceive(Context context, Intent intent) {    
			    // Get extra data included in the Intent
				if(LookLookActivity.CONTACTS_REFRESH_DATA.equals(intent.getAction()) && LookLookActivity.FRIEND_NEWS_CHANGE.equals(intent.getExtras().get(LookLookActivity.CONTACTS_REFRESH_KEY))){			
					System.out.println("==== FriendNewsFragment ==");
					loadData();
				}}};
				
	private void loadData() {
		if(null==contentView)return;
		wrapUserList = accountInfo.friendNewsDataEntities.getCache();		
		newsAdapter.setData(wrapUserList);
		newsAdapter.notifyDataSetChanged();
		accountInfo.newFriendChange= 0;
		Intent msgIntent = new Intent(LookLookActivity.UPDATE_MASK);
		LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(msgIntent);
		accountInfo.privateMsgManger.hSubScript.t_friend_change = accountInfo.t_friendNews;
		RemoteManager.getInstance(MainApplication.getAppInstance()).CallService(accountInfo);
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		contentView = super.onCreateView(inflater, container, savedInstanceState);
		setTitle("好友动态");
		hideLeftButton();
		showRightButton();
		context = this.getActivity();

		handler = new Handler(this);
		userID = ActiveAccount.getInstance(ZApplication.getInstance()).getUID();
		accountInfo = AccountInfo.getInstance(userID);
		
		xlv_news = (PullToRefreshListView) contentView
				.findViewById(R.id.xlv_friendnews);
		newsListView = xlv_news.getRefreshableView();
		xlv_news.setShowIndicator(false);
		xlv_news.setOnRefreshListener(this);
		newsAdapter = new ContactsWithContentAdapter(getActivity());

		newsListView.setAdapter(newsAdapter);

		newsListView.setOnItemClickListener(this);	
		
		return contentView;
	}

	@Override
	public void onResume() {
		super.onResume();
		if(isVisible()){
			LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mMessageReceiver,
				      new IntentFilter(LookLookActivity.CONTACTS_REFRESH_DATA));
			loadData();		
		}
	}

	@Override
	public void onPause() {
		super.onPause();
	}

	
	@Override
	public void onHiddenChanged(boolean hidden) {
		// TODO Auto-generated method stub
		super.onHiddenChanged(hidden);
		System.out.println("==== onhiddenchanged ==friendnews= " + hidden);
		if(!hidden){
			LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mMessageReceiver,
				      new IntentFilter(LookLookActivity.CONTACTS_REFRESH_DATA));
			loadData();	
		}else{
			LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(mMessageReceiver);
		}
	}
	
	@Override
	public void onStop() {
		super.onStop();
		LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(mMessageReceiver);
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_title_right:
			showMenu();
			break;
		
		default:
			break;
		}
	}
	
	@Override
	public boolean handleMessage(Message msg) {
		// TODO Auto-generated method stub
		switch (msg.what) {
		case Requester3.RESPONSE_TYPE_FRIEND_NEWS_LIST:
			if (msg.obj != null) {
				friendNewsResponse friendList = (friendNewsResponse) msg.obj;
				if ("0".equals(friendList.status)) {
					
					if(friendList.removediarys!=null && !friendList.removediarys.isEmpty()){
						String[] diaryIds = friendList.removediarys.split(",");
						for(int i=0; i< diaryIds.length; i++){
							accountInfo.friendNewsDataEntities.removeMember(diaryIds[i]);
						}
					}
					
					if(pullDown){
						accountInfo.friendNewsDataEntities.clearList();
						for (int i = 0; i < friendList.contents.length; i++) {
							accountInfo.friendNewsDataEntities
									.insertMember(i, friendList.contents[i]);
						}
						accountInfo.t_friendNews = friendList.first_diary_time;
						RemoteManager.getInstance(this.getActivity()).CallService(accountInfo);
						accountInfo.friendNewsDataEntities.fristTime = friendList.first_diary_time;
					}else{
						for (int i = 0; i < friendList.contents.length; i++) {
							accountInfo.friendNewsDataEntities
									.addMember(friendList.contents[i]);
						}
						accountInfo.friendNewsDataEntities.lastTime = friendList.last_diary_time;
					}
					
					if("1".equals(friendList.hasnextpage)){
						isHasNextPage = true;
					}else{
						isHasNextPage = false;
					}
					
					loadData();
				}else{
					Prompt.Alert("操作失败，请稍后再试");
				}
			}else{
				Prompt.Alert("网络不给力，请稍后再试");
			}
			xlv_news.onRefreshComplete();
			break;
		case STOP_REFRESH:
			xlv_news.onRefreshComplete();
			break;
		default:
			break;
		}

		return false;
	}

	@Override
	public int subContentViewId() {
		// TODO Auto-generated method stub
		return R.layout.friend_news_contacts_list;
	}
	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		//日记如下：
		//wrapUserList.get(arg2).diaries;
		// 跳转预览，因为没有diarylist结构 所以临时建立一个
		MyDiary diary = wrapUserList.get(arg2 - 1).diaries;
		MyDiaryList tmpDiaryList = new MyDiaryList();
		tmpDiaryList.contain = diary.diaryid;
		tmpDiaryList.diaryid = diary.diaryid;
		tmpDiaryList.diaryuuid = diary.diaryuuid;
		tmpDiaryList.publishid = diary.publishid;
		tmpDiaryList.isgroup = "0";
		
		ArrayList<MyDiaryList> tmpList = new ArrayList<MyDiaryList>();
		tmpList.add(tmpDiaryList);
		ArrayList<MyDiary> tmpList2 = new ArrayList<MyDiary>();
		tmpList2.add(diary);
		
		Intent intent = new Intent(getActivity(), DiaryPreviewActivity.class);
		intent.putExtra(DiaryPreviewActivity.INTENT_ACTION_DIARY_UUID, diary.diaryuuid);
		diaryManager.setDetailDiaryList(tmpList, 2);
		diaryManager.setDetailDiary(tmpList2);
		startActivity(intent);
		
	}
	@Override
	public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
		// TODO Auto-generated method stub
		pullDown = true;
		isHasNextPage = true;
		Requester3.requestFriendNews(handler, "", "");
	}
	@Override
	public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
		// TODO Auto-generated method stub
		pullDown = false;
		if(isHasNextPage){
			if(xlv_news != null) {
				xlv_news.setRefreshingLabel(getString(R.string.pull_to_refresh_refreshing_label), Mode.PULL_FROM_END);
				xlv_news.setReleaseLabel(getString(R.string.pull_to_refresh_footer_release_label), Mode.PULL_FROM_END);
			}
			Requester3.requestFriendNews(handler, accountInfo.friendNewsDataEntities.lastTime, "2");
		}else{
			if(xlv_news != null) {
				xlv_news.setRefreshingLabel(getString(R.string.no_more_date), Mode.PULL_FROM_END);
				xlv_news.setReleaseLabel(getString(R.string.no_more_date), Mode.PULL_FROM_END);
			}
			handler.sendEmptyMessage(STOP_REFRESH);
		}
	}

	
}
