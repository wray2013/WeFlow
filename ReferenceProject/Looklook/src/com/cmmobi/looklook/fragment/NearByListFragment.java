package com.cmmobi.looklook.fragment;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
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
import com.cmmobi.looklook.activity.FriendsNearByActivity;
import com.cmmobi.looklook.common.adapter.NearbyRecommendAdapter;
import com.cmmobi.looklook.common.gson.GsonResponse2;
import com.cmmobi.looklook.common.gson.GsonResponse2.MyDiary;
import com.cmmobi.looklook.common.gson.Requester2;
import com.cmmobi.looklook.common.view.ConViewPagerListView;
import com.cmmobi.looklook.common.view.pulltorefresh.PullToRefreshBase;
import com.cmmobi.looklook.common.view.pulltorefresh.PullToRefreshBase.OnRefreshListener2;
import com.cmmobi.looklook.info.profile.AccountInfo;
import com.cmmobi.looklook.info.profile.ActiveAccount;
import com.cmmobi.looklook.info.profile.DiaryManager;
import com.cmmobi.looklook.info.profile.TimeHelper;

public class NearByListFragment extends Fragment implements
		OnItemClickListener, Callback, OnRefreshListener2<ListView> {
	private final static String TAG = "NearByListFragment";
	protected View mContentView;
	
	FriendsNearByActivity mActivity;
	
	ConViewPagerListView friendsNearByListView;
	Handler handler;

	private int pageIndex = 5;
	private int remainIndex;
	
	private String userID;
	private AccountInfo accountInfo;

	private ArrayList<Object> NearByDiaryList = new ArrayList<Object>();

	private NearbyRecommendAdapter nearbyrecommendAdapter;

	private ListView realListView;
	
	protected DisplayMetrics dm = new DisplayMetrics();
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		dm = getActivity().getApplicationContext().getResources().getDisplayMetrics();
	}
	
	@Override
	public void onResume() {
		super.onResume();
		if(nearbyrecommendAdapter != null) {
			nearbyrecommendAdapter.notifyDataSetChanged();
		}
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mContentView = inflater.inflate(R.layout.fragment_nearby_recommend, null);
		return mContentView;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		userID = ActiveAccount.getInstance(ZApplication.getInstance()).getUID();
		accountInfo = AccountInfo.getInstance(userID);
		//friendsNearByManager = accountInfo.friendsNearByManager;

		friendsNearByListView = (ConViewPagerListView) mContentView
				.findViewById(R.id.friend_circle_list);

		friendsNearByListView.setShowIndicator(false);
		friendsNearByListView.setOnRefreshListener(this);

		realListView = friendsNearByListView.getRefreshableView();
		
		nearbyrecommendAdapter = new NearbyRecommendAdapter(getActivity(), realListView);

		realListView.setAdapter(nearbyrecommendAdapter);

		realListView.setOnItemClickListener(this);

		handler = new Handler(this);

		loadLoacalData();
	}

	private void loadLoacalData() {
		if (mActivity.list_nearby_data != null &&
				mActivity.list_nearby_data.size() > 0) {
			// diariesList.clear();
			// partsList.clear();
			for(int i=0; i<mActivity.list_nearby_data.size();i++) {
				NearByDiaryList.add(mActivity.list_nearby_data.get(i));
			}
			
			nearbyrecommendAdapter.setTimeLines(NearByDiaryList);
			nearbyrecommendAdapter.notifyDataSetChanged();
			
			//friendsNearByListView.setRefreshing(false);
		} else {
			Requester2.requestNearDiary(handler, mActivity.myLoc,  "1", "50", getPicWidth() + "", "");
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {

//		Toast.makeText(this.getActivity(), "点击的是第" + position + "项",
//				Toast.LENGTH_LONG).show();
		if(position > 0) {
			Object tmp = NearByDiaryList.get(position - 1);
			if(tmp instanceof MyDiary) {
				MyDiary tempDiary = (MyDiary) tmp;
				String diaryuuid=tempDiary.diaryuuid;
				DiaryManager.getInstance().setDetailDiaryList(covertMyDiaryList(NearByDiaryList));
				this.getActivity().startActivity(new Intent(this.getActivity(),DiaryDetailActivity.class)
				.putExtra(DiaryDetailActivity.INTENT_ACTION_DIARY_UUID, diaryuuid));
			}
		}
	}

	private ArrayList<MyDiary> covertMyDiaryList(ArrayList<Object> nearbyDiaries) {
		ArrayList<MyDiary> mydiaries = new ArrayList<MyDiary>();
		for(Object o : nearbyDiaries) {
			if(o instanceof MyDiary) {
				MyDiary d = (MyDiary) o;
				mydiaries.add(d);
			}
		}
		return mydiaries;
	}
	
	@Override
	public void onPullDownToRefresh(PullToRefreshBase refreshView) {
		pageIndex = 5;
		
		String label = DateUtils.formatDateTime(getActivity()
				.getApplicationContext(), TimeHelper.getInstance().now(),
				DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE
						| DateUtils.FORMAT_ABBREV_ALL);

		// Update the LastUpdatedLabel
		refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);

		Requester2.requestNearDiary(handler, mActivity.myLoc,  "1", "50", getPicWidth() + "", "");
//		new GetDataTask().execute();
	}

	@Override
	public void onPullUpToRefresh(PullToRefreshBase refreshView) {
		remainIndex = pageIndex;
		pageIndex ++;
		String label = DateUtils.formatDateTime(getActivity()
				.getApplicationContext(), TimeHelper.getInstance().now(),
				DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE
						| DateUtils.FORMAT_ABBREV_ALL);

		// Update the LastUpdatedLabel
		refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);

		Requester2.requestNearDiary(handler, mActivity.myLoc, pageIndex + "", "10", getPicWidth() + "", "");

//		pageIndex ++;
		//		new GetDataTask().execute();
	}

	@Override
	public boolean handleMessage(Message msg) {
		switch (msg.what) {
		case Requester2.RESPONSE_TYPE_NEAR_DIARY:

			if (msg.obj != null) {
				GsonResponse2.nearDiaryResponse response = (GsonResponse2.nearDiaryResponse) msg.obj;

				if ("0".equals(response.status) && response.diaries != null && response.diaries.length > 0) {
					if (5 == pageIndex) {
						NearByDiaryList.clear();
						mActivity.list_nearby_data.clear();
						if (null != response.diaries) {
							for (int i = 0; i < response.diaries.length; i++) {
								MyDiary myDiary = response.diaries[i];
								NearByDiaryList.add(myDiary);
								mActivity.list_nearby_data.add(myDiary);
							}
						}
//						pageIndex++;
					} else {
						if (null != response.diaries) {
							for (int i = 0; i < response.diaries.length; i++) {
								MyDiary myDiary = response.diaries[i];
								NearByDiaryList.add(myDiary);
								mActivity.list_nearby_data.add(myDiary);
							}
						}
//						pageIndex++;
					}
					nearbyrecommendAdapter.setTimeLines(NearByDiaryList);
					nearbyrecommendAdapter.notifyDataSetChanged();
				} else {
					pageIndex = remainIndex;
				}
			} else {
				pageIndex = remainIndex;
			}
			friendsNearByListView.onRefreshComplete();
			break;

		default:
			break;
		}
		return false;
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);

		Log.e(TAG, "NearByFragment - onAttach");

		try {
			mActivity = (FriendsNearByActivity) activity;
			// onGotoPageListener = (OnGotoPageListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString() + " must implement OnGotoPageListener");
		}
	}
	
	private class GetDataTask extends AsyncTask<Void, Void, String[]> {

		@Override
		protected String[] doInBackground(Void... params) {
			// Simulates a background job.
			try {
				Thread.sleep(4000);
			} catch (InterruptedException e) {
			}
			return null;
		}

		@Override
		protected void onPostExecute(String[] result) {

			// Call onRefreshComplete when the list has been refreshed.
			friendsNearByListView.onRefreshComplete();

			super.onPostExecute(result);
		}
	}
	
	private int getPicWidth() {
		Log.i("dan", "getPicWidth=" + 0.85 * dm.widthPixels);
		return (int) (0.85 * dm.widthPixels);
	}

}