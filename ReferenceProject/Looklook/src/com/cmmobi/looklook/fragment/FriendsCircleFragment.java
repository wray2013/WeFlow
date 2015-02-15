package com.cmmobi.looklook.fragment;

import java.util.ArrayList;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.text.format.DateUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import cn.zipper.framwork.core.ZApplication;

import com.cmmobi.looklook.R;
import com.cmmobi.looklook.activity.DiaryDetailActivity;
import com.cmmobi.looklook.common.adapter.FriendsCircleAdapter;
import com.cmmobi.looklook.common.gson.GsonResponse2;
import com.cmmobi.looklook.common.gson.GsonResponse2.MyDiary;
import com.cmmobi.looklook.common.gson.GsonResponse2.timelinePart;
import com.cmmobi.looklook.common.gson.Requester2;
import com.cmmobi.looklook.common.view.ConViewPagerListView;
import com.cmmobi.looklook.common.view.pulltorefresh.PullToRefreshBase;
import com.cmmobi.looklook.common.view.pulltorefresh.PullToRefreshBase.OnRefreshListener2;
import com.cmmobi.looklook.info.profile.AccountInfo;
import com.cmmobi.looklook.info.profile.ActiveAccount;
import com.cmmobi.looklook.info.profile.DiaryManager;
import com.cmmobi.looklook.info.profile.FriendsCircleManager;
import com.cmmobi.looklook.info.profile.TimeHelper;

public class FriendsCircleFragment extends Fragment implements
		OnItemClickListener, Callback, OnRefreshListener2<ListView> {
	protected View mContentView;

	ConViewPagerListView friendsCircleListView;
	Handler handler;

	private int pageIndex = 1;

	private String userID;
	private AccountInfo accountInfo;

	private FriendsCircleManager friendsCircleManager;
	private ArrayList<Object> timeLineList = new ArrayList<Object>();

	private FriendsCircleAdapter friendsCircleAdapter;

	private int remainIndex;
	protected DisplayMetrics dm = new DisplayMetrics();
	private ListView realListView;

	public static final String FRIENDSCIRCLE = "friendsCircle";
	int showwidth;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		dm = getActivity().getApplicationContext().getResources().getDisplayMetrics();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mContentView = inflater.inflate(R.layout.fragment_friends_circle, null);
		return mContentView;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		userID = ActiveAccount.getInstance(ZApplication.getInstance()).getUID();
		accountInfo = AccountInfo.getInstance(userID);
		friendsCircleManager = accountInfo.friendsCircleManager;

		friendsCircleListView = (ConViewPagerListView) mContentView
				.findViewById(R.id.friend_circle_list);

		friendsCircleListView.setShowIndicator(false);
		friendsCircleListView.setOnRefreshListener(this);
		friendsCircleListView.setFilterTouchEvents(false);


		realListView = friendsCircleListView.getRefreshableView();
		friendsCircleAdapter = new FriendsCircleAdapter(getActivity(), realListView);
		realListView.setAdapter(friendsCircleAdapter);

		realListView.setOnItemClickListener(this);

		handler = new Handler(this);

		loadLocalData();
	}

	@Override
	public void onResume() {
//		realListView.setSelection(0);
//		friendsCircleListView.setRefreshing(false);
		super.onResume();
	}
	private void loadLocalData() {
		ArrayList<WrapTimeLineDiary> cache = friendsCircleManager.getCahceDiaries();
		if (null != cache && cache.size() > 0) {

			// diariesList.clear();
			// partsList.clear();
			timeLineList.clear();

			for (int i = 0; i < cache.size(); i++) {
				WrapTimeLineDiary myDiary = cache.get(i);

				timeLineList.add(myDiary);

			}
			friendsCircleAdapter.setTimeLines(timeLineList);
			friendsCircleAdapter.notifyDataSetChanged();

		}
		pageIndex = 1;
		DisplayMetrics dm = this.getResources().getDisplayMetrics();
		showwidth = (int)(dm.widthPixels - (dm.density * 60 + 0.5f));
		
		Requester2.requestMyTimeLine(handler, "1", "10", getPicWidth() + "", "");
	}
	private int getPicWidth() {
		Log.i("dan", "getPicWidth=" + 0.85 * dm.widthPixels);
		return (int) (0.85 * dm.widthPixels);
	}
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {

		ArrayList<MyDiary> diaryList = new ArrayList<MyDiary>();
		for (int i = 0; i < timeLineList.size(); i++) {

			if (timeLineList.get(i) instanceof WrapTimeLineDiary) {
				MyDiary diary = ((WrapTimeLineDiary) timeLineList.get(i)).diary;
				diaryList.add(diary);
			}
		}
		if (timeLineList.get((int) id) instanceof WrapTimeLineDiary) {

			MyDiary diary = ((WrapTimeLineDiary) (timeLineList.get((int) id))).diary;

			// DiaryManager.getInstance().setDetailDiaryList(itemsList);
			// MainApplication application = (MainApplication) getActivity()
			// .getApplication();
			// application.setTimeLineList(diaryList);

			DiaryManager.getInstance().setDetailDiaryList(diaryList);
			Intent intent = new Intent(getActivity(), DiaryDetailActivity.class);
			intent.putExtra(DiaryDetailActivity.INTENT_ACTION_DIARY_UUID,
					diary.diaryuuid);
			intent.putExtra(FriendsCircleFragment.FRIENDSCIRCLE,
					FriendsCircleFragment.FRIENDSCIRCLE);
			getActivity().startActivity(intent);

		}

	}

	@Override
	public void onPullDownToRefresh(PullToRefreshBase refreshView) {
		pageIndex = 1;

		String label = DateUtils.formatDateTime(getActivity()
				.getApplicationContext(), TimeHelper.getInstance().now(),
				DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE
						| DateUtils.FORMAT_ABBREV_ALL);

		refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);

		Requester2.requestMyTimeLine(handler, "1", "10", getPicWidth() + "", "");
	}

	@Override
	public void onPullUpToRefresh(PullToRefreshBase refreshView) {
		remainIndex = pageIndex;
		
		pageIndex++;
		String label = DateUtils.formatDateTime(getActivity()
				.getApplicationContext(), TimeHelper.getInstance().now(),
				DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE
						| DateUtils.FORMAT_ABBREV_ALL);

		refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);

		Requester2.requestMyTimeLine(handler, pageIndex + "", "10", getPicWidth() + "", "");

	}

	@Override
	public boolean handleMessage(Message msg) {
		switch (msg.what) {
		case Requester2.RESPONSE_TYPE_TIMELINE:

			if (msg.obj != null) {
				GsonResponse2.timelineResponse response = (GsonResponse2.timelineResponse) msg.obj;

				if ("0".equals(response.status) && (null != response.diaries && response.diaries.length > 0 || null != response.parts && response.parts.length >0)) {
					if (1 == pageIndex) {
						// friendsCircleManager.refreshCache(response);
						// diariesList.clear();
						// partsList.clear();

						timeLineList.clear();
						if (null != response.diaries
								&& response.diaries.length > 0) {

							ArrayList<WrapTimeLineDiary> timelineDiaries = new ArrayList<WrapTimeLineDiary>();
							for (int i = 0; i < response.diaries.length; i++) {
								WrapTimeLineDiary tempTimeLineDiary = new WrapTimeLineDiary(response.diaries[i]);
								if (!timeLineList.contains(tempTimeLineDiary)) {
									timelineDiaries.add(tempTimeLineDiary);
								}
							}
							friendsCircleManager.refreshCache(timelineDiaries);

							for (int i = 0; i < response.diaries.length; i++) {
								
								WrapTimeLineDiary wrapTimeLineDiary = new WrapTimeLineDiary(response.diaries[i]);
								if (!timeLineList.contains(wrapTimeLineDiary)) {
									
									timeLineList.add(wrapTimeLineDiary);
								}

							}
						}
						if (null != response.parts) {

							for (int j = 0; j < response.parts.length; j++) {

								timelinePart part = response.parts[j];

								int tempPosition = Integer
										.parseInt(part.part_position) - 1;
								timeLineList.add(tempPosition > timeLineList
										.size() ? timeLineList.size()
										: tempPosition, part);
							}
						}
//						pageIndex++;
					} else {
						if (null != response.diaries
								&& response.diaries.length > 0) {

							ArrayList<WrapTimeLineDiary> timelineDiaries = new ArrayList<WrapTimeLineDiary>();
							for (int i = 0; i < response.diaries.length; i++) {
								
								WrapTimeLineDiary temp = new WrapTimeLineDiary(response.diaries[i]);
								if (!timeLineList.contains(temp)) {
									timelineDiaries.add(temp);
								}
							}
							friendsCircleManager.refreshCache(timelineDiaries);

							for (int i = 0; i < response.diaries.length; i++) {
								WrapTimeLineDiary wrapTimeLineDiary = new WrapTimeLineDiary(response.diaries[i]);
								if (!timeLineList.contains(wrapTimeLineDiary)) {
									
									timeLineList.add(wrapTimeLineDiary);
								}

							}
						}
						if (null != response.parts) {

							for (int j = 0; j < response.parts.length; j++) {

								timelinePart part = response.parts[j];

								int tempPosition = Integer
										.parseInt(part.part_position) - 1;
								timeLineList.add(tempPosition > timeLineList
										.size() ? timeLineList.size()
										: tempPosition, part);
							}
						}
//						pageIndex ++;
					}
					// else {
					// if (null != response.diaries
					// && response.diaries.length > 0) {
					// for (int i = 0; i < response.diaries.length; i++) {
					// MyDiary myDiary = response.diaries[i].diary;
					//
					// /**
					// * 去重
					// *
					// * if (timeLineList.contains(myDiary)) { for
					// * (int j = 0; j < timeLineList.size(); j++) {
					// * if (timeLineList.get(j) instanceof MyDiary) {
					// * if (myDiary.diaryid .equals(((MyDiary)
					// * timeLineList .get(j)).diaryid)) {
					// * timeLineList.remove(j); } } } }
					// */
					// timeLineList.add(myDiary);
					// }
					// }
					// if (null != response.parts && response.parts.length > 0)
					// {
					// for (int j = 0; j < response.parts.length; j++) {
					//
					// timelinePart part = response.parts[j];
					//
					// if (null != part) {
					// int tempPosition = Integer
					// .parseInt(part.part_position) - 1;
					// timeLineList
					// .add(tempPosition > timeLineList
					// .size() ? timeLineList
					// .size() : tempPosition,
					// part);
					// }
					// }
					// }
					// }
					// pageIndex++;

					// for (int i = 0; i < timeLineList.size(); i++) {
					// if (null == timeLineList.get(i)) {
					// timeLineList.remove(i);
					// }
					// }

					friendsCircleAdapter.setTimeLines(timeLineList);
					friendsCircleAdapter.notifyDataSetChanged();
				} else {

					pageIndex = remainIndex;
				}
			} else {
				pageIndex = remainIndex;
			}
			friendsCircleListView.onRefreshComplete();
			break;

		default:
			break;
		}
		return false;
	}
}
