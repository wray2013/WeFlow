package com.cmmobi.looklook.fragment;

import java.util.ArrayList;

import android.content.Context;
import android.os.Bundle;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.AbsListView.OnScrollListener;

import com.cmmobi.looklook.R;
import com.cmmobi.looklook.common.adapter.SubcribeContentAdapter;
import com.cmmobi.looklook.common.view.pulltorefresh.PullToRefreshBase;
import com.cmmobi.looklook.common.view.pulltorefresh.PullToRefreshBase.OnRefreshListener2;
import com.cmmobi.looklook.common.view.pulltorefresh.PullToRefreshListView;

/**
 * @author wuxiang
 * @email wuxiang@cmmobi.com
 * @date  2013-10-21
 */
public class MyZoneSubscribeFragment extends XFragment implements
OnRefreshListener2<ListView>,OnScrollListener{

	private View contentView;
	private PullToRefreshListView ll_my_subscribe_list;
	private ListView mySubscribeContent;
	private SubcribeContentAdapter scadapter;
	private Context context;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		contentView=inflater.inflate(R.layout.fragment_my_zone_subscribe, null);

		context = getActivity();
		
		ll_my_subscribe_list = (PullToRefreshListView ) contentView.findViewById(R.id.ll_my_subscribe_list);
		ll_my_subscribe_list.setShowIndicator(false);
		ll_my_subscribe_list.setOnRefreshListener(this);
		mySubscribeContent = ll_my_subscribe_list.getRefreshableView();
		
		ArrayList<String> list = new ArrayList<String>();
		list.add("A");
		list.add("B");
		list.add("C");
		list.add("D");
		
		ZoneBaseFragment zoneBaseFragment=FragmentHelper.getInstance(getActivity()).getZoneBaseFragment();
		scadapter = new SubcribeContentAdapter(zoneBaseFragment, list);
		
		mySubscribeContent.setAdapter(scadapter);
		return contentView;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}


	private View lastItem;
	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		/*View v=scadapter.getItem();
		if(null==v)return;
		int[] location=new int[2];
		v.getLocationInWindow(location);
		if(!v.equals(lastItem)){
			lastItem=v;
			lastItem.setTag(R.id.xlv_my_zone, null);
		}
		if(null==lastItem.getTag(R.id.xlv_my_zone)){
			lastItem.setTag(R.id.xlv_my_zone, location[1]);
			return;
		}
		int lastY=(Integer) lastItem.getTag(R.id.xlv_my_zone);
		 if(location[1]>lastY){
			 handler.post(new Runnable() {
				
				@Override
				public void run() {
					showTitle();
				}
			});
		 }else if(location[1]<lastY){
			 handler.post(new Runnable() {
					
					@Override
					public void run() {
						hideTitle();
					}
				});
		 }
		 lastItem.setTag(R.id.xlv_my_zone, location[1]);
		 */
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
		handler.post(new Runnable() {
			@Override
			public void run() {
				scadapter.notifyDataSetChanged();
				ll_my_subscribe_list.onRefreshComplete();
			}
		});		
	}

	@Override
	public boolean handleMessage(Message msg) {
		// TODO Auto-generated method stub
		return false;
	}
	

	@Override
	public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
		handler.post(new Runnable() {
			@Override
			public void run() {
				scadapter.notifyDataSetChanged();
				ll_my_subscribe_list.onRefreshComplete();
			}
		});		
	}
}
