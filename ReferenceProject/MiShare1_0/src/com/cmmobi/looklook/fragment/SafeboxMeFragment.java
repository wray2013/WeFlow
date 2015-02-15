package com.cmmobi.looklook.fragment;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.cmmobi.looklook.R;
import com.cmmobi.looklook.activity.NewCommentsActivity;
import com.cmmobi.looklook.common.adapter.SafeboxMeListAdapter;
import com.cmmobi.looklook.common.gson.GsonResponse3.MyDiaryList;
import com.cmmobi.looklook.common.utils.CmmobiClickAgentWrapper;
import com.cmmobi.looklook.common.utils.DateUtils;
import com.cmmobi.looklook.common.view.Xdialog;
import com.cmmobi.looklook.common.view.pulltorefresh.PullToRefreshBase;
import com.cmmobi.looklook.common.view.pulltorefresh.PullToRefreshBase.OnRefreshListener2;
import com.cmmobi.looklook.common.view.pulltorefresh.PullToRefreshListView;
import com.cmmobi.looklook.fragment.MyZoneFragment.DiariesItem;
import com.cmmobi.looklook.fragment.MyZoneFragment.MyZoneItem;
import com.cmmobi.looklook.info.profile.DiaryManager;
import com.cmmobi.looklook.offlinetask.OfflineTaskManager;
import com.cmmobi.statistics.CmmobiClickAgent;

/**
 * @author wuxiang
 * @email wuxiang@cmmobi.com
 * @date 2013-11-6
 */
public class SafeboxMeFragment extends SafeboxSubFragment implements
		OnRefreshListener2<ListView>,OnClickListener{
	private static final String TAG = SafeboxMeFragment.class.getSimpleName();
	private View contentView;
	private PullToRefreshListView xlvMySafebox;
	private ListView lvMyZoneList;
	private ArrayList<MyZoneItem> myZoneItems = new ArrayList<MyZoneFragment.MyZoneItem>();
	private SafeboxMeListAdapter mySafeboxListAdapter;
	private View llComment;
	private TextView tvComment;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		contentView = inflater.inflate(R.layout.safebox_me, null);
		xlvMySafebox = (PullToRefreshListView) contentView
				.findViewById(R.id.xlv_my_safebox);
		xlvMySafebox.setShowIndicator(false);
		xlvMySafebox.setOnRefreshListener(this);
		lvMyZoneList = xlvMySafebox.getRefreshableView();
		llComment=contentView.findViewById(R.id.ll_comment);
		tvComment=(TextView) contentView.findViewById(R.id.tv_comment);
		llComment.setOnClickListener(this);
		SafeboxContentFragment safeboxContentFragment=FragmentHelper.getInstance(getActivity()).getSafeboxContentFragment();
		mySafeboxListAdapter = new SafeboxMeListAdapter(safeboxContentFragment, myZoneItems);
		lvMyZoneList.setAdapter(mySafeboxListAdapter);
		DiaryManager.getInstance().setMySafeboxDataChangedListener(
				new MySafeboxMeDataChangedListener());
		loadData();
//		updateUnreadMsg(1);
		return contentView;
	}
	
	//更新未读消息栏
	public void updateUnreadMsg(int num){
		if(null==contentView)return;
		if(num>0){
			llComment.setVisibility(View.VISIBLE);
			tvComment.setText(getString(R.string.has_new_comment, num));
		}else{
			llComment.setVisibility(View.GONE);
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ll_comment:
			//跳转到消息页
			Log.d(TAG, "跳转到消息页");
			Intent intent = new Intent(getActivity(),NewCommentsActivity.class);
			intent.putExtra(NewCommentsActivity.REQ_PARAM_ENCRYPT, true);
			getActivity().startActivity(intent);
			break;

		default:
			break;
		}
		
	}

	// 进入页面和下拉刷新时加载数据
	private void loadData() {
		index = 0;
		loadLocalData();
	}

	@Override
	public void delete() {
		if (null == contentView)
			return;
		new Xdialog.Builder(getActivity())
				.setTitle("删除日记")
				.setMessage("确定要删除选中日记吗？")
				.setPositiveButton(android.R.string.ok,
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								ArrayList<MyDiaryList> removeList = mySafeboxListAdapter
										.getCheckedList();
								String removeIDs = getServreRemoveIDs(removeList);
								if (!"".equals(removeIDs) && removeIDs != null)
									OfflineTaskManager.getInstance()
											.addDiaryRemoveTask(removeIDs);
								// 删除选中日记
								diaryManager
										.removeDiaryGroupByDiaryGroupList(removeList);
								diaryManager.notifyMyDiaryChanged();
								// 更新日记列表
								updateViews(null);
								// 显示正常标题栏
								showNormalTitle();
								mySafeboxListAdapter.purgeCheckedView();
							}
						}).setNegativeButton(android.R.string.cancel, null)
				.create().show();
	}

	@Override
	public void removeSafebox() {
		ArrayList<MyDiaryList> removeList = mySafeboxListAdapter
				.getCheckedList();
		String uuids = getDiaryGroupUUIDs(removeList);
		if(!TextUtils.isEmpty(uuids)){
			int num=uuids.split(",").length;
			HashMap<String, String> hs=new HashMap<String, String>();
			hs.put("label", "1");
//			hs.put("labe12", uuids);
			hs.put("labe12", num+"");
			//2014-4-8 wuxiang
			CmmobiClickAgentWrapper.onEvent(getActivity(), "out_safe_box", hs);
			OfflineTaskManager.getInstance().addSafeboxRemoveTask("", uuids);
			diaryManager.removeSafebox(removeList);
			diaryManager.notifyMyDiaryChanged();
			// 更新日记列表
			mySafeboxListAdapter.purgeCheckedView();
			refreshLocalData(uuids);
			// 显示正常标题栏
			showNormalTitle();
		}
	}
	
	private void refreshLocalData(String uuids){
		for(int i=0;i<myZoneItems.size();i++){
			MyZoneItem myZoneItem=myZoneItems.get(i);
			if (myZoneItem instanceof DiariesItem) {
				ArrayList<MyDiaryList> diaryGroups=((DiariesItem) myZoneItem).diaryGroups;
				ArrayList<MyDiaryList> removeItems=new ArrayList<MyDiaryList>();
				for(int j=0;j<diaryGroups.size();j++){
					MyDiaryList myDiaryList=diaryGroups.get(j);
					if(uuids!=null&&uuids.contains(myDiaryList.diaryuuid))
						removeItems.add(myDiaryList);
				}
				diaryGroups.removeAll(removeItems);
			}
		}
		if(myZoneItems.size()<10)//显示数据不足10条时，重新获取本地数据
			loadLocalData();
	}

	@Override
	public void purgeCheckedView() {
		if(mySafeboxListAdapter!=null)
			mySafeboxListAdapter.purgeCheckedView();
	}

	// 加载本地数据
	private boolean loadLocalData() {
		myZoneItems.clear();
		ArrayList<MyZoneItem> localList = diaryManager.getMySafeboxItems(0);
		myZoneItems.addAll(localList);
		mySafeboxListAdapter.notifyDataSetChanged();
		return myZoneItems.size() > 1;
	}

	private int index;

	// 加载更多数据
	private void loadMore() {
		ArrayList<MyZoneItem> localList = diaryManager
				.getMySafeboxItems(++index);
		myZoneItems.clear();
		myZoneItems.addAll(localList);
		mySafeboxListAdapter.notifyDataSetChanged();
		if (localList.size() < DiaryManager.PAGE_SIZE * (index + 1)) {
			index--;
			Log.d(TAG, "no more data");
		}
	}

	private boolean isResume = true;

	@Override
	public void onPause() {
		isResume = false;
		super.onPause();
	}

	@Override
	public void onResume() {
		isResume = true;
		if (isVisible()) {
			updateItems();
		}
		super.onResume();
	}

	private boolean needUpdate = false;

	@Override
	public void updateViews(Object data) {
		if (null == contentView)
			return;
		if (!isResume || isHidden()) {
			Log.d(TAG, "update delay");
			needUpdate = true;
		} else {
			updateItems();
		}
	}

	private void updateItems() {
		Log.d(TAG, "updateItems");
////		ArrayList<MyZoneItem> updatedList = diaryManager
////				.updateSafeboxItems(getUpdateLastTime());
//		ArrayList<MyZoneItem> updatedList=diaryManager.getMySafeboxItems(0);
//		myZoneItems.clear();
//		myZoneItems.addAll(updatedList);
//		mySafeboxListAdapter.notifyDataSetChanged();
		loadData();
	}

	@Override
	public void onHiddenChanged(boolean hidden) {
		if (null == contentView)
			return;
		Log.d(TAG, "hidden=" + hidden);
		if (!hidden) {
			updateItems();
		}
		super.onHiddenChanged(hidden);
	}

	// 获取现有列表中最旧的日记更新时间
	private long getUpdateLastTime() {
		for (int i = myZoneItems.size() - 1; i >= 0; i--) {
			MyZoneItem zoneItem = myZoneItems.get(i);
			if (zoneItem instanceof DiariesItem) {
				ArrayList<MyDiaryList> myDiaryLists = ((DiariesItem) zoneItem).diaryGroups;
				if (myDiaryLists != null && myDiaryLists.size() > 0) {
					String updateTime = myDiaryLists
							.get(myDiaryLists.size() - 1).create_time;
					if (DateUtils.isNum(updateTime))
						return Long.parseLong(updateTime);
				}
			}
		}
		return 0;
	}
	
	public void showNormalTitle() {
		SafeboxContentFragment safeboxContentFragment=FragmentHelper.getInstance(getActivity()).getSafeboxContentFragment();
		safeboxContentFragment.showNormalTitle();
	}

	/**
	 * 删除选中日记（组）
	 */
	// 从指定列表中返回日记组id（不为空），已逗号分隔
	private String getServreRemoveIDs(ArrayList<MyDiaryList> removeList) {
		String serverIDs = "";
		if (removeList != null) {
			for (int i = 0; i < removeList.size(); i++) {
				MyDiaryList myDiaryList = removeList.get(i);
				if (myDiaryList != null && myDiaryList.diaryid != null
						&& myDiaryList.diaryid.length() > 0) {
					serverIDs += myDiaryList.diaryid;
					if (i < removeList.size() - 1) {
						serverIDs += ",";
					}
				}
			}
		}
		return serverIDs;
	}

	// 从指定列表中返回日记组uuid，逗号分隔
	private String getDiaryGroupUUIDs(ArrayList<MyDiaryList> removeList) {
		String uuids = "";
		if (removeList != null) {
			for (int i = 0; i < removeList.size(); i++) {
				MyDiaryList myDiaryList = removeList.get(i);
				if (myDiaryList != null) {
					uuids += myDiaryList.diaryuuid;
					if (i < removeList.size() - 1) {
						uuids += ",";
					}
				}
			}
		}
		return uuids;
	}

	@Override
	public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
		handler.post(new Runnable() {
			@Override
			public void run() {
				loadData();
				mySafeboxListAdapter.notifyDataSetChanged();
				xlvMySafebox.onRefreshComplete();
			}
		});
	}

	@Override
	public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
		handler.post(new Runnable() {
			@Override
			public void run() {
				loadMore();
				mySafeboxListAdapter.notifyDataSetChanged();
				xlvMySafebox.onRefreshComplete();
			}
		});
	}

	@Override
	public boolean handleMessage(Message msg) {
		// TODO Auto-generated method stub
		return false;
	}

	class MySafeboxMeDataChangedListener
			implements com.cmmobi.looklook.info.profile.DiaryManager.MySafeboxMeDataChangedListener {

		@Override
		public void safeboxDataChanged() {
			Log.d(TAG, "safeboxDataChanged");
			// 此方法可能被非UI线程调用
			handler.post(new Runnable() {
				@Override
				public void run() {
					updateViews(null);
				}
			});
		}
	}
}
