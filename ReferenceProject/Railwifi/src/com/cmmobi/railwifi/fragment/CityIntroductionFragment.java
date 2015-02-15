package com.cmmobi.railwifi.fragment;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.RelativeLayout;

import com.cmmobi.railwifi.R;
import com.cmmobi.railwifi.adapter.CityIntroAdapter;
import com.cmmobi.railwifi.network.GsonResponseObject;
import com.cmmobi.railwifi.network.Requester;

@SuppressLint("ValidFragment")
public class CityIntroductionFragment extends TitleRootFragment implements OnClickListener {
	private GridView grid_view;
	private CityIntroAdapter adapter;
	private List<GsonResponseObject.cityScopeElem> data;
	private RelativeLayout rlNoNet;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = super.onCreateView(inflater, container, savedInstanceState);
		initViews(view);
		handler.postDelayed(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				Requester.requestCityScape(getHandler());
			}
		}, 150);
		
		return view;
	}
	
	public void requestCity() {
		if (data == null || data.size() == 0) {
			Requester.requestCityScape(getHandler());
		}
	}
	
	private void initViews(View view) {
		setTitleText("城市风采");
		setLeftButtonBackground(R.drawable.btn_navigation);
		hideRightButton();

		rlNoNet = (RelativeLayout)view.findViewById(R.id.rl_no_network);
		adapter = new CityIntroAdapter(getActivity());
		data = new ArrayList<GsonResponseObject.cityScopeElem>();
		adapter.setData(data);
		
		grid_view  = (GridView) view.findViewById(R.id.gv_list);
		
//		ViewUtils.setMarginTop(grid_view, 12);
//		ViewUtils.setMarginLeft(grid_view, 12);
//		ViewUtils.setMarginRight(grid_view, 12);
		grid_view.setAdapter(adapter);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()) {
		case R.id.btn_title_left:
			getSlidingMenu().toggle();
			break;
		}
	}

	@Override
	public int subContentViewId() {
		// TODO Auto-generated method stub
		return R.layout.fragment_city_introdution;
	}
	
	@Override
	public boolean handleMessage(Message msg) {
		// TODO Auto-generated method stub
		switch(msg.what){
		case Requester.RESPONSE_TYPE_CITYSCOPE:
			GsonResponseObject.cityScopeListResp r9 = (GsonResponseObject.cityScopeListResp)(msg.obj);
			if(r9!=null && "0".equals(r9.status)){
				if(r9.list!=null && r9.list.length>0){
					data.clear();
					for(int i=0; i<r9.list.length; i++){
						data.add(r9.list[i]);
					}
					
					adapter.notifyDataSetChanged();
					rlNoNet.setVisibility(View.GONE);
				}
			} else {
				rlNoNet.setVisibility(View.VISIBLE);
			}
			break;
		}
		return super.handleMessage(msg);
	}

}
