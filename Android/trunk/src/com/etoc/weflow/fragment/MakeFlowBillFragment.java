/*
 * Copyright (C) 2013 Andreas Stuetz <andreas.stuetz@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.etoc.weflow.fragment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.etoc.weflow.R;
import com.etoc.weflow.WeFlowApplication;
import com.etoc.weflow.activity.MakeFlowActivity;
import com.etoc.weflow.adapter.MyBillAdapter;
import com.etoc.weflow.dao.AccountInfo;
import com.etoc.weflow.event.MakeFlowBillFragmentEvent;
import com.etoc.weflow.net.GsonResponseObject.AdvFlowRecordResp;
import com.etoc.weflow.net.GsonResponseObject.AdverInfo;
import com.etoc.weflow.net.GsonResponseObject.AppFlowRecordResp;
import com.etoc.weflow.net.GsonResponseObject.AwardInfoResp;
import com.etoc.weflow.net.GsonResponseObject.AwardRecordResp;
import com.etoc.weflow.net.GsonResponseObject.SoftInfoResp;
import com.etoc.weflow.net.Requester;
import com.etoc.weflow.net.GsonResponseObject.BillList;
import com.etoc.weflow.utils.DisplayUtil;
import com.etoc.weflow.utils.PType;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import de.greenrobot.event.EventBus;

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

public class MakeFlowBillFragment extends Fragment implements OnRefreshListener2<ListView>, Callback {

	private static final String ARG_POSITION = "position";

	private static final int POSITION_ADV      = 0; //看广告
	private static final int POSITION_SOFTWARE = 1; //下软件
	private static final int POSITION_GAME     = 2; //玩游戏
	
	private int position = POSITION_ADV;
	
	private PullToRefreshListView xlvMyBill;
	private ListView lvBillList;
	private MyBillAdapter adapter;
	
	private Handler myHandler;
	
	private List<BillList> billList = new ArrayList<BillList>();
	private int pageNumber = 0;
	
	public static MakeFlowBillFragment newInstance(int position) {
		MakeFlowBillFragment f = new MakeFlowBillFragment();
		Bundle b = new Bundle();
		b.putInt(ARG_POSITION, position);
		f.setArguments(b);
		return f;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		EventBus.getDefault().register(this);
		position = getArguments().getInt(ARG_POSITION);
		
		myHandler = new Handler(this);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		super.onCreateView(inflater, container, savedInstanceState);
		View v = inflater.inflate(R.layout.fragment_bill_list, null);
		initView(v);
		return v;
	}
	
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		EventBus.getDefault().unregister(this);
		super.onDestroy();
	}
	
	private void initView(View view) {
		xlvMyBill = (PullToRefreshListView) view.findViewById(R.id.xlv_mybill_list);
		xlvMyBill.setShowIndicator(false);
		xlvMyBill.setOnRefreshListener(this);
		
		lvBillList = xlvMyBill.getRefreshableView();
		lvBillList.setDividerHeight(DisplayUtil.getSize(getActivity(), 2));
		
		adapter = new MyBillAdapter(getActivity());
		adapter.setData(billList);
		lvBillList.setAdapter(adapter);
		
	}
	
	public void onEvent(MakeFlowBillFragmentEvent event) {
		Log.d("=AAA=","onEvent index = " + event.getIndex());
		AccountInfo acc = WeFlowApplication.getAppInstance().getAccountInfo();
		if(acc == null || acc.getUserid() == null || acc.getUserid().equals("")) {
			return;
		}
		switch(event.getIndex()) {
		case MakeFlowActivity.INDEX_ADVERTISEMENT:
			if (position == POSITION_ADV) {
				Requester.getAdvRecord(true, myHandler, 0 + "", acc.getUserid());
				pageNumber = 0;
			}
			break;
		case MakeFlowActivity.INDEX_APPRECOMM:
			if (position == POSITION_SOFTWARE) {
				Requester.getAppRecord(true, myHandler, 0 + "", acc.getUserid());
				pageNumber = 0;
			}
			break;
		case MakeFlowActivity.INDEX_PLAYGAME:
			if (position == POSITION_GAME) {
				Requester.getAwardRecord(true, myHandler, 0 + "", acc.getUserid());
				pageNumber = 0;
			}
			break;
		}
	}
	
	private static final long MONTH_TIME = 30 * 12 * 60 * 60 * 1000;
	private List<BillList> makeFakeData() {
		List<BillList> list = new ArrayList<BillList>();
		for(int i = 0; i < 10; i ++) {
			BillList item = new BillList();
			item.title = "观看监狱风云";
			int coins = i % 2 == 0 ? (20 + i) * -1: (20 + i);
			item.content = "到底是监狱风云还是澳门风云撒？";
			item.flowcoins = coins + "";
			item.time = (System.currentTimeMillis() - MONTH_TIME * i) + "";
			switch(position) {
			case POSITION_ADV:
				list.add(item);
				break;
			case POSITION_SOFTWARE:
				if(coins >= 0) {
					list.add(item);
				}
				break;
			case POSITION_GAME:
				if(coins < 0) {
					list.add(item);
				}
				break;
			}
		}
		billList.addAll(list);
		return list;
	}

	@Override
	public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
		// TODO Auto-generated method stub
		AccountInfo acc = WeFlowApplication.getAppInstance().getAccountInfo();
		if (acc == null || acc.getUserid() == null || acc.getUserid().equals("")) {
			return;
		}
		if(pageNumber == 0) pageNumber = 1;
		switch (position) {
		case POSITION_ADV:
			Requester.getAdvRecord(false, myHandler, pageNumber + "", acc.getUserid());
			break;
		case POSITION_SOFTWARE:
			Requester.getAppRecord(false, myHandler, pageNumber + "", acc.getUserid());
			break;
		case POSITION_GAME:
			Requester.getAwardRecord(false, myHandler, pageNumber + "", acc.getUserid());
			break;
		}
	}

	@Override
	public boolean handleMessage(Message msg) {
		// TODO Auto-generated method stub
		myHandler.postDelayed(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				xlvMyBill.onRefreshComplete();
			}
		}, 1000);
		switch(msg.what) {
		case Requester.RESPONSE_TYPE_ADV_RECORD:
			if (msg.obj != null) {
				AdvFlowRecordResp advResp = (AdvFlowRecordResp) msg.obj;
				if ("0".equals(advResp.status) || "0000".equals(advResp.status)) {
					if(advResp.recordlist != null && advResp.recordlist.length > 0) {
						List<AdverInfo> advlist = Arrays.asList(advResp.recordlist);
						List<BillList> blist = new ArrayList<BillList>();
						for(AdverInfo item : advlist) {
							PType type = PType.watch_movie;
							BillList bill = new BillList();
							bill.type = type.getValue();
							bill.productid = item.videoid;
							bill.title = item.title;
							bill.content = item.content;
							bill.flowcoins = item.flowcoins;
							bill.time = item.finishtime;
							blist.add(bill);
						}
						if(pageNumber == 0) {
							billList.clear();
						}
						billList.addAll(blist);
						adapter.setData(billList);
						pageNumber ++;
					} else {
						
					}
				}
			}
			break;
		case Requester.RESPONSE_TYPE_APP_FLOW_RECORD:
			if (msg.obj != null) {
				AppFlowRecordResp flowResp = (AppFlowRecordResp) msg.obj;
				if ("0".equals(flowResp.status) || "0000".equals(flowResp.status)) {
					if(flowResp.list != null && flowResp.list.length > 0) {
						List<SoftInfoResp> flowlist = Arrays.asList(flowResp.list);
						List<BillList> blist = new ArrayList<BillList>();
						for (SoftInfoResp item : flowlist) {
							PType type = PType.down_soft;
							BillList bill = new BillList();
							bill.type = type.getValue();
							bill.productid = item.appid;
							bill.title = item.title;
							bill.content = item.title;
							bill.flowcoins = item.flowcoins;
							bill.time = item.downloadfinishtime;
							blist.add(bill);
						}
						if(pageNumber == 0) {
							billList.clear();
						}
						billList.addAll(blist);
						adapter.setData(billList);
						pageNumber ++;
					} else {
						
					}
				}
			}
			break;
		case Requester.RESPONSE_TYPE_AWARD_RECORD:
			if (msg.obj != null) {
				AwardRecordResp awardResp = (AwardRecordResp) msg.obj;
				if ("0".equals(awardResp.status) || "0000".equals(awardResp.status)) {
					if(awardResp.recordlist != null && awardResp.recordlist.length > 0) {
						List<AwardInfoResp> awardlist = Arrays.asList(awardResp.recordlist);
						List<BillList> blist = new ArrayList<BillList>();
						for (AwardInfoResp item : awardlist) {
							PType type = PType.get_award;
							BillList bill = new BillList();
							bill.type = type.getValue();
							bill.title = item.title;
							bill.content = item.desc;
							bill.flowcoins = item.flowcoins;
							bill.time = item.time;
							blist.add(bill);
						}
						if(pageNumber == 0) {
							billList.clear();
						}
						billList.addAll(blist);
						adapter.setData(billList);
						pageNumber ++;
					} else {
						
					}
				}
			}
			break;
		}
		return false;
	}

}