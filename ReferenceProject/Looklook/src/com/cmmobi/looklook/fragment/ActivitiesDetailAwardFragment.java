package com.cmmobi.looklook.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.RelativeLayout;
import android.widget.AdapterView.OnItemClickListener;
import cn.zipper.framwork.core.ZApplication;

import com.cmmobi.looklook.R;
import com.cmmobi.looklook.activity.ActivitiesDetailActivity;
import com.cmmobi.looklook.activity.DiaryDetailActivity;
import com.cmmobi.looklook.common.adapter.ActivitiesPartAdapter;
import com.cmmobi.looklook.common.gson.GsonResponse2.MyDiary;
import com.cmmobi.looklook.common.gson.GsonResponse2.activeListItem;
import com.cmmobi.looklook.common.gson.GsonResponse2.getAwardDiaryListItem;
import com.cmmobi.looklook.common.gson.GsonResponse2.getAwardDiaryListResponse;
import com.cmmobi.looklook.common.gson.Requester2;
import com.cmmobi.looklook.common.view.InScrollViewListView;
import com.cmmobi.looklook.common.view.pulltorefresh.PullToRefreshBase.Mode;
import com.cmmobi.looklook.info.profile.AccountInfo;
import com.cmmobi.looklook.info.profile.ActiveAccount;
import com.cmmobi.looklook.info.profile.ActivityDetailAwardManager;
import com.cmmobi.looklook.info.profile.DiaryManager;

public class ActivitiesDetailAwardFragment extends Fragment implements Callback, OnItemClickListener {
	private View contentView;
	private InScrollViewListView activitiesPartListView;
	private String userID;
	private AccountInfo accountInfo;
	private ActivityDetailAwardManager activityDetailAwardManager;

	private Handler handler;
	private ActivitiesDetailActivity activitiesDetailActivity;
	private activeListItem activeItem;
	private ActivitiesPartAdapter activitiesPartAdapter;
	protected DisplayMetrics dm = new DisplayMetrics();
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
		activityDetailAwardManager = accountInfo.activityDetailAwardManager;

		activitiesDetailActivity = (ActivitiesDetailActivity) getActivity();

		this.activeItem = activitiesDetailActivity.activeItem;
		
		activitiesDetailActivity.scrollView.setMode(Mode.DISABLED);

		handler = new Handler(this);

		activitiesPartListView = (InScrollViewListView) contentView
				.findViewById(R.id.activities_part_list);
		activitiesPartListView.setOnItemClickListener(this);
		activitiesPartAdapter = new ActivitiesPartAdapter(
				getActivity());

		activitiesPartListView.setAdapter(activitiesPartAdapter);
		
		Requester2.requestAwardDiaryList(handler, activeItem.activeid, getPicWidth() + "", "");

	}

	@Override
	public boolean handleMessage(Message msg) {
		switch (msg.what) {
		case Requester2.RESPONSE_TYPE_GET_AWARD_DIARYLIST:
			if (msg.obj != null) {
				getAwardDiaryListResponse awardDiaryList = (getAwardDiaryListResponse) msg.obj;

				if ("0".equals(awardDiaryList.status) || "".equals(awardDiaryList.status)) {

					getAwardDiaryListItem[] awardList = awardDiaryList.awards;
					
					activitiesPartAdapter.setData(awardList);
					activitiesPartAdapter.notifyDataSetChanged();
				} else {

				}
			}

			break;

		default:
			break;
		}
		return false;

	}
	
	private int getPicWidth() {
		return (int) (0.85 * dm.widthPixels);
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub
		
		MyDiary diary = (MyDiary)activitiesPartAdapter.getItem(arg2);
		Intent intent = new Intent(getActivity(), DiaryDetailActivity.class);
		intent.putExtra(DiaryDetailActivity.INTENT_ACTION_DIARY_UUID,
				diary.diaryuuid);
		intent.putExtra(FriendsCircleFragment.FRIENDSCIRCLE,
				FriendsCircleFragment.FRIENDSCIRCLE);
		getActivity().startActivity(intent);
	}
}