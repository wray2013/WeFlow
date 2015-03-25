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
import com.etoc.weflow.activity.ExpenseFlowActivity;
import com.etoc.weflow.adapter.MyBillAdapter;
import com.etoc.weflow.dao.AccountInfo;
import com.etoc.weflow.event.ExpenseFlowBillFragmentEvent;
import com.etoc.weflow.net.GsonResponseObject.AdverInfo;
import com.etoc.weflow.net.GsonResponseObject.BillList;
import com.etoc.weflow.net.GsonResponseObject.CostFlowRecordResp;
import com.etoc.weflow.net.GsonResponseObject.RecordResp;
import com.etoc.weflow.net.Requester;
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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

public class ExpenseFlowBillFragment extends Fragment implements OnRefreshListener2<ListView>, Callback {

	private static final String ARG_POSITION = "position";

	private static final int POSITION_PHONEBILL = 0; //充话费
	private static final int POSITION_FLOWPKG   = 1; //流量包
	private static final int POSITION_GAME      = 2; //游戏币
	private static final int POSITION_GIFT      = 3; //礼品券
	
	private int position = POSITION_PHONEBILL;
	
	private PullToRefreshListView xlvMyBill;
	private ListView lvBillList;
	private MyBillAdapter adapter;
	
	private Handler myHandler;

	private List<BillList> billList = new ArrayList<BillList>();
	private int pageNumber = 0;
	
	public static ExpenseFlowBillFragment newInstance(int position) {
		ExpenseFlowBillFragment f = new ExpenseFlowBillFragment();
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
	
	public void onEvent(ExpenseFlowBillFragmentEvent event) {
		AccountInfo acc = WeFlowApplication.getAppInstance().getAccountInfo();
		if (acc == null || acc.getUserid() == null || acc.getUserid().equals("")) {
			return;
		}
		switch(event.getIndex()) {
		case ExpenseFlowActivity.INDEX_RECHARGE:
			if (position == POSITION_PHONEBILL) {
				Requester.getCostFlowRecord(true, myHandler, 0 + "", acc.getUserid(), PType.change_tc.getValue() + "," + PType.change_qq.getValue());
				pageNumber = 0;
			}
			break;
		case ExpenseFlowActivity.INDEX_FLOW:
			if (position == POSITION_FLOWPKG) {
				Requester.getCostFlowRecord(true, myHandler, 0 + "", acc.getUserid(), PType.change_wf.getValue());
				pageNumber = 0;
			}
			break;
		case ExpenseFlowActivity.INDEX_GAME:
			if (position == POSITION_GAME) {
				Requester.getCostFlowRecord(true, myHandler, 0 + "", acc.getUserid(), PType.change_gf.getValue() + "," + PType.recharge_gm.getValue());
				pageNumber = 0;
			}
			break;
		case ExpenseFlowActivity.INDEX_GIFT:
			if (position == POSITION_GIFT) {
				Requester.getCostFlowRecord(true, myHandler, 0 + "", acc.getUserid(), PType.bug_gf.getValue());
				pageNumber = 0;
			}
			break;
		}
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
	
	private static final long MONTH_TIME = 30 * 12 * 60 * 60 * 1000;
	private List<BillList> makeFakeData() {
		List<BillList> list = new ArrayList<BillList>();
		for(int i = 0; i < 10; i ++) {
			BillList item = new BillList();
			item.title = "观看监狱风云";
			int coins = i % 2 == 0 ? (20 + i) * -1: (20 + i);
			item.content = "成功兑换WOW15元点卡\nDHWO23AG3HAWZ21SQ\n请尽快使用";
			item.flowcoins = coins + "";
			item.time = (System.currentTimeMillis() - MONTH_TIME * i) + "";
			switch(position) {
			case POSITION_PHONEBILL:
			case POSITION_FLOWPKG:
			case POSITION_GAME:
			case POSITION_GIFT:
				list.add(item);
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
		case POSITION_PHONEBILL:
			Requester.getCostFlowRecord(false, myHandler, pageNumber + "", acc.getUserid(), PType.change_tc.getValue() + "," + PType.change_qq.getValue());
			break;
		case POSITION_FLOWPKG:
			Requester.getCostFlowRecord(false, myHandler, pageNumber + "", acc.getUserid(), PType.change_wf.getValue());
			break;
		case POSITION_GAME:
			Requester.getCostFlowRecord(false, myHandler, pageNumber + "", acc.getUserid(), PType.change_gf.getValue() + "," + PType.recharge_gm.getValue());
			break;
		case POSITION_GIFT:
			Requester.getCostFlowRecord(false, myHandler, pageNumber + "", acc.getUserid(), PType.bug_gf.getValue());
			break;
		}
	}

	@Override
	public boolean handleMessage(Message msg) {
		myHandler.postDelayed(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				xlvMyBill.onRefreshComplete();
			}
		}, 5);
		switch(msg.what) {
		case Requester.RESPONSE_TYPE_COST_FLOW_LIST:
			if (msg.obj != null) {
				CostFlowRecordResp recRep = (CostFlowRecordResp) msg.obj;
				if ("0".equals(recRep.status) || "0000".equals(recRep.status)) {
					if (recRep.list != null && recRep.list.length > 0) {
						List<RecordResp> reclist = Arrays.asList(recRep.list);
						List<BillList> blist = new ArrayList<BillList>();
						for(RecordResp item : reclist) {
							BillList bill = new BillList();
							bill.type = item.type;
							bill.productid = item.productid;
							bill.title = item.title;
							bill.content = item.title;
							bill.flowcoins = item.cost;
							bill.time = item.date;
							blist.add(bill);
						}
						if(pageNumber == 0) {
							billList.clear();
						}
						billList.addAll(blist);
						adapter.setData(billList);
						pageNumber ++;
					}
				}
			}
			break;
		}
		return false;
	}

}