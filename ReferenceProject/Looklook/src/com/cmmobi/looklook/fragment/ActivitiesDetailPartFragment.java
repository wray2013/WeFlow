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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ScrollView;
import cn.zipper.framwork.core.ZApplication;

import com.cmmobi.looklook.R;
import com.cmmobi.looklook.activity.ActivitiesDetailActivity;
import com.cmmobi.looklook.activity.DiaryDetailActivity;
import com.cmmobi.looklook.common.adapter.ActivitiesPartVideoAdapter;
import com.cmmobi.looklook.common.gson.GsonResponse2;
import com.cmmobi.looklook.common.gson.GsonResponse2.MyDiary;
import com.cmmobi.looklook.common.gson.GsonResponse2.activeDiaryListResponse;
import com.cmmobi.looklook.common.gson.GsonResponse2.activeListItem;
import com.cmmobi.looklook.common.gson.Requester2;
import com.cmmobi.looklook.common.view.InScrollViewListView;
import com.cmmobi.looklook.common.view.pulltorefresh.PullToRefreshBase;
import com.cmmobi.looklook.common.view.pulltorefresh.PullToRefreshBase.Mode;
import com.cmmobi.looklook.common.view.pulltorefresh.PullToRefreshBase.OnRefreshListener2;
import com.cmmobi.looklook.info.profile.AccountInfo;
import com.cmmobi.looklook.info.profile.ActiveAccount;
import com.cmmobi.looklook.info.profile.ActivitiesDiariesManager;
import com.cmmobi.looklook.info.profile.DiaryManager;
import com.cmmobi.looklook.info.profile.TimeHelper;

public class ActivitiesDetailPartFragment extends Fragment implements Callback, OnItemClickListener, OnRefreshListener2<ScrollView> {
	private View contentView;
	private InScrollViewListView activitiesPartListView;
	private String userID;
	private AccountInfo accountInfo;
	private ActivitiesDiariesManager activitiesDiariesManager;

	private Handler handler;
	private ActivitiesDetailActivity activitiesDetailActivity;
	private activeListItem activeItem;
	private ActivitiesPartVideoAdapter activitiesPartAdapter;
	private activeDiaryListResponse res;
	private boolean isDown = true;
	protected DisplayMetrics dm = new DisplayMetrics();
	private ArrayList<MyDiary> list = new ArrayList<GsonResponse2.MyDiary>();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		dm = getActivity().getApplicationContext().getResources().getDisplayMetrics();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		contentView = inflater.inflate(
				R.layout.fragment_activities_detail_part, null);

		return contentView;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		userID = ActiveAccount.getInstance(ZApplication.getInstance()).getUID();
		accountInfo = AccountInfo.getInstance(userID);
		activitiesDiariesManager = accountInfo.activitiesDiariesManager;
		
		activitiesDetailActivity = (ActivitiesDetailActivity) getActivity();

		this.activeItem = activitiesDetailActivity.activeItem;

		activitiesDetailActivity.scrollView.setMode(Mode.BOTH);

		activitiesDetailActivity.scrollView.setOnRefreshListener(this);

		handler = new Handler(this);

		activitiesPartListView = (InScrollViewListView) contentView
				.findViewById(R.id.activities_part_list);

		activitiesPartAdapter = new ActivitiesPartVideoAdapter(getActivity());

		activitiesPartListView.setAdapter(activitiesPartAdapter);

		activitiesPartListView.setOnItemClickListener(this);
//		getLocalRecentDiary();
		
//		recentPageIndex = 0; 
//		getServerRecentDiary();
//		loadLocalData() ;
		
		isDown = true;
		Requester2.requestActiveDiaryList(handler, this.activeItem.activeid,
				"", 10 + "", getPicWidth() + "", "", "1");
	}

	@Override
	public boolean handleMessage(Message msg) {
		switch (msg.what) {
		case Requester2.RESPONSE_TYPE_DIARY_ACTIVELIST:
			if (msg.obj != null) {
				res = (activeDiaryListResponse) msg.obj;

				if ("0".equals(res.status) && res.diaries != null
						&& res.diaries.length > 0) {
					if (true == isDown) {// 为刷新数据，添加至首部
						
						ArrayList<GsonResponse2.MyDiary> tempList = new ArrayList<GsonResponse2.MyDiary>();
						for (int i = 0; i < res.diaries.length; i++) {
							tempList.add(res.diaries[i]);
						}
						activitiesDiariesManager.refreshCache(tempList);

						list = tempList;
						activitiesPartAdapter.setData(list);
						activitiesPartAdapter.notifyDataSetChanged();
						isDown = false;
					} else {// 为加载数据，添加至尾部
						
						ArrayList<GsonResponse2.MyDiary> tempList = new ArrayList<GsonResponse2.MyDiary>();
						for (int i = 0; i < res.diaries.length; i++) {
							tempList.add(res.diaries[i]);
						}
						list.addAll(tempList);
						activitiesPartAdapter.setData(list);
						activitiesPartAdapter.notifyDataSetChanged();
					}
				} else {
				}
			} else {
			}

			activitiesDetailActivity.scrollView.onRefreshComplete();
			break;

		default:
			break;
		}
		return false;

	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {

		MyDiary diary = list.get((int)id);

		DiaryManager.getInstance().setDetailDiaryList(list);
		Intent intent = new Intent(getActivity(), DiaryDetailActivity.class);
		intent.putExtra(DiaryDetailActivity.INTENT_ACTION_DIARY_UUID,
				diary.diaryuuid);
		intent.putExtra(FriendsCircleFragment.FRIENDSCIRCLE,
				FriendsCircleFragment.FRIENDSCIRCLE);
		getActivity().startActivity(intent);
	}

	@Override
	public void onPullDownToRefresh(PullToRefreshBase<ScrollView> refreshView) {
		String label = DateUtils.formatDateTime(getActivity()
				.getApplicationContext(), TimeHelper.getInstance().now(),
				DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE
						| DateUtils.FORMAT_ABBREV_ALL);

		refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);

		isDown = true;
		Requester2.requestActiveDiaryList(handler, this.activeItem.activeid,
				"", 10 + "", getPicWidth() + "", "", "1");
	}

	@Override
	public void onPullUpToRefresh(PullToRefreshBase<ScrollView> refreshView) {
		String label = DateUtils.formatDateTime(getActivity()
				.getApplicationContext(), TimeHelper.getInstance().now(),
				DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE
						| DateUtils.FORMAT_ABBREV_ALL);

		refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);

		isDown = false;
		if (null != list && list.size() > 0) {
			Requester2.requestActiveDiaryList(handler, this.activeItem.activeid,
					list.get(list.size() - 1).updatetimemilli, 10 + "", getPicWidth() + "", "", "2");
		}
	}
	private int getPicWidth() {
		return (int) (0.85 * dm.widthPixels);
	}
}
