package com.cmmobi.looklook.fragment;

import java.util.ArrayList;

import android.content.Context;
import android.os.Bundle;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;

import com.cmmobi.looklook.R;
import com.cmmobi.looklook.common.adapter.FriendsContentAdapter;
import com.cmmobi.looklook.common.adapter.SubcribeContentAdapter;
import com.cmmobi.looklook.common.view.pulltorefresh.PullToRefreshBase;
import com.cmmobi.looklook.common.view.pulltorefresh.PullToRefreshBase.OnRefreshListener;
import com.cmmobi.looklook.common.view.pulltorefresh.PullToRefreshBase.OnRefreshListener2;
import com.cmmobi.looklook.common.view.pulltorefresh.PullToRefreshListView;

/**
 * @author wuxiang
 * @email wuxiang@cmmobi.com
 * @date  2013-10-21
 */
public class FriendsFragment extends XFragment implements OnRefreshListener2<ListView>{

	private View contentView;
	private PullToRefreshListView ll_my_friend_List;
	private ListView friendContentList;
	private FriendsContentAdapter fcadapter;
	private Context context;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		contentView=inflater.inflate(R.layout.fragment_friends, null);

		context = getActivity();
		
		ll_my_friend_List = (PullToRefreshListView ) contentView.findViewById(R.id.ll_my_friend_List);
		ll_my_friend_List.setShowIndicator(false);
		ll_my_friend_List.setOnRefreshListener(this);
		friendContentList = ll_my_friend_List.getRefreshableView();
		
		ArrayList<String> list = new ArrayList<String>();
		list.add("A");
		list.add("B");
		list.add("C");
		list.add("D");
		
		//ZoneBaseFragment zoneBaseFragment=FragmentHelper.getInstance(getActivity()).getZoneBaseFragment();
		fcadapter = new FriendsContentAdapter(this, list);
				
		friendContentList.setAdapter(fcadapter);
		return contentView;
	}

	@Override
	public boolean handleMessage(Message msg) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
		handler.post(new Runnable() {
			@Override
			public void run() {
				fcadapter.notifyDataSetChanged();
				ll_my_friend_List.onRefreshComplete();
			}
		});		
	}

	@Override
	public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
		handler.post(new Runnable() {
			@Override
			public void run() {
				fcadapter.notifyDataSetChanged();
				ll_my_friend_List.onRefreshComplete();
			}
		});
	}

}
