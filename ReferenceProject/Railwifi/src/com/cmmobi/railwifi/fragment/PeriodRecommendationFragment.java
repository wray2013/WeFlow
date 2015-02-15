package com.cmmobi.railwifi.fragment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.cmmobi.railwifi.R;
import com.cmmobi.railwifi.adapter.RecommendationAdapter;
import com.cmmobi.railwifi.network.GsonResponseObject;
import com.cmmobi.railwifi.network.GsonResponseObject.AlumbElem;
import com.cmmobi.railwifi.network.GsonResponseObject.SubAlumbElem;
import com.cmmobi.railwifi.network.Requester;
import com.cmmobi.railwifi.utils.ViewUtils;

import de.greenrobot.event.EventBus;
import dev.dworks.libs.astickyheader.SimpleSectionedListAdapter;
import dev.dworks.libs.astickyheader.SimpleSectionedListAdapter.Section;
import dev.dworks.libs.astickyheader.ui.PinnedSectionListView;

/**
 * 本周推荐
 * @author wangjm@cmmobi.com
 *
 */
public class PeriodRecommendationFragment extends Fragment /*implements Callback */{
	
	private PinnedSectionListView listView;
	private RecommendationAdapter mAdapter;
	private ArrayList<Section> sections = new ArrayList<Section>();
	private Handler handler;
	
	private SimpleSectionedListAdapter simpleSectionedGridAdapter;
	List<SubAlumbElem> alumbElemList = new ArrayList<SubAlumbElem>();
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
//		handler = new Handler(this);
//		Requester.requestRecommendList(handler);
		mAdapter = new RecommendationAdapter(getActivity(), alumbElemList);
		
		simpleSectionedGridAdapter = new SimpleSectionedListAdapter(getActivity(), mAdapter,
				R.layout.item_recommendation_header, R.id.tv_recommendation_title,R.id.iv_tag);
		EventBus.getDefault().registerSticky(this);
	}
	
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		EventBus.getDefault().unregister(this);
		super.onDestroy();
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater,
			ViewGroup container, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view = inflater.inflate(R.layout.fragment_period_recommendation, null);
		
		initViews(view);
		return view;
	}
	
	private void initViews(View view) {
		listView = (PinnedSectionListView)view.findViewById(R.id.list);
		listView.setSelector(new ColorDrawable(Color.TRANSPARENT));
//		listView.setShadowVisible(false);
		ViewUtils.setWidth(view.findViewById(R.id.view_recommendation_title), 631);
		ViewUtils.setMarginRight(view.findViewById(R.id.view_recommendation_title), 12);
		listView.setAdapter(simpleSectionedGridAdapter);
		Log.d("=AAA=","PeriodRecommendation initViews in");
	}

	/*@Override
	public boolean handleMessage(Message msg) {
		// TODO Auto-generated method stub
		switch(msg.what) {
		case Requester.RESPONSE_TYPE_MEDIA_RECOMMANDLIST:
			Log.d("=AAA=","RESPONSE_TYPE_MEDIA_RECOMMANDLIST");
			if (msg.obj != null) {
				GsonResponseObject.recmmandListResp resp = (GsonResponseObject.recmmandListResp) msg.obj;
				List<SubAlumbElem> alumbElemList = new ArrayList<SubAlumbElem>();
				if ("0".equals(resp.status)) {
					GsonResponseObject.AlumbElem[] recommArray = resp.list;
					for (int i = 0; i < recommArray.length; i++) {
						SubAlumbElem[] alumbElemArray = recommArray[i].sublist;
						sections.add(new Section(alumbElemList.size(), recommArray[i].periods));
						Collections.addAll(alumbElemList, alumbElemArray);
					}
					mAdapter = new RecommendationAdapter(getActivity(),alumbElemList);
					
					SimpleSectionedListAdapter simpleSectionedGridAdapter = new SimpleSectionedListAdapter(getActivity(), mAdapter,
							R.layout.item_recommendation_header, R.id.tv_recommendation_title,R.id.iv_tag);
					simpleSectionedGridAdapter.setSections(sections.toArray(new Section[0]));
					mAdapter.setSectionAdapter(simpleSectionedGridAdapter);
					listView.setAdapter(simpleSectionedGridAdapter);
				}
			}
			break;
		}
		return false;
	}*/
	
	public void onEvent(GsonResponseObject.AlumbElem[] recommArray) {
		Log.d("=AAA=","PeriodRecom onEvent in");
		alumbElemList.clear();
		for (int i = 0; i < recommArray.length; i++) {
			SubAlumbElem[] alumbElemArray = recommArray[i].sublist;
			
			sections.add(new Section(alumbElemList.size(), recommArray[i].periods));
			Collections.addAll(alumbElemList, alumbElemArray);
		}
		
		simpleSectionedGridAdapter.setSections(sections.toArray(new Section[0]));
		mAdapter.setSectionAdapter(simpleSectionedGridAdapter);
		mAdapter.setSectionArray(sections);
		
		simpleSectionedGridAdapter.notifyDataSetChanged();
	}
	
}
