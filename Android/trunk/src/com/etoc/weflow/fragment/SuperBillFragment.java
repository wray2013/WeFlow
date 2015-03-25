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
import com.etoc.weflow.activity.MyBillListActivity;
import com.etoc.weflow.adapter.MyBillAdapter;
import com.etoc.weflow.dao.AccountInfo;
import com.etoc.weflow.dialog.PromptDialog;
import com.etoc.weflow.event.FlowBillFragmentEvent;
import com.etoc.weflow.net.GsonResponseObject.MyBillListResp;
import com.etoc.weflow.net.Requester;
import com.etoc.weflow.net.GsonResponseObject.BillList;
import com.etoc.weflow.utils.DisplayUtil;
import com.etoc.weflow.utils.NumberUtils;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;

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

public class SuperBillFragment extends Fragment implements OnRefreshListener2<ListView>, Callback {

	private static final String ARG_POSITION = "position";

	private static final int POSITION_TOTAL  = 0;
	private static final int POSITION_INCOME = 1;
	private static final int POSITION_PAY    = 2;
	
	private int position = POSITION_TOTAL;
	
	private PullToRefreshListView xlvMyBill;
	private ListView lvBillList;
	private MyBillAdapter adapter;
	
	private Handler myHandler;
	
	private List<BillList> billList = new ArrayList<BillList>();
	private int pageNumber = 0;

	public static SuperBillFragment newInstance(int position) {
		SuperBillFragment f = new SuperBillFragment();
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
	
	public void onEvent(FlowBillFragmentEvent event) {
		AccountInfo acc = WeFlowApplication.getAppInstance().getAccountInfo();
		if (acc == null || acc.getUserid() == null || acc.getUserid().equals("")) {
			return;
		}
		
		switch(event.getIndex()) {
		case MyBillListActivity.INDEX_BILL_ALL:
			if (position == POSITION_TOTAL) {
				Requester.getBillList(true, myHandler, 0 + "", acc.getUserid());
				pageNumber = 0;
			}
			break;
		case MyBillListActivity.INDEX_BILL_MAKE:
			if (position == POSITION_INCOME) {
				Requester.getBillList(true, myHandler, 0 + "", acc.getUserid());
				pageNumber = 0;
			}
			break;
		case MyBillListActivity.INDEX_BILL_EXPENSE:
			if (position == POSITION_PAY) {
				Requester.getBillList(true, myHandler, 0 + "", acc.getUserid());
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
		adapter.setData(billList/*makeFakeData()*/);
		lvBillList.setAdapter(adapter);
		
	}
	
	private static final long MONTH_TIME = 30 * 12 * 60 * 60 * 1000;
	private List<BillList> makeFakeData() {
		List<BillList> list = new ArrayList<BillList>();
		for(int i = 0; i < 10; i ++) {
			BillList item = new BillList();
			item.title = "兑换游戏礼包";
			int coins = i % 2 == 0 ? (20 + i) * -1: (20 + i);
			item.flowcoins = coins + "";
			item.content = "成功兑换WOW15元点卡\nDHWO23AG3HAWZ21SQ\n请尽快使用";
			item.time = (System.currentTimeMillis() - MONTH_TIME * i) + "";
			switch(position) {
			case POSITION_TOTAL:
				list.add(item);
				break;
			case POSITION_INCOME:
				if(coins >= 0) {
					list.add(item);
				}
				break;
			case POSITION_PAY:
				if(coins < 0) {
					list.add(item);
				}
				break;
			}
		}
		return list;
	}

	@Override
	public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
		// TODO Auto-generated method stub
		myHandler.postDelayed(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				xlvMyBill.onRefreshComplete();
			}
		}, 5);
		AccountInfo acc = WeFlowApplication.getAppInstance().getAccountInfo();
		if (acc == null || acc.getUserid() == null || acc.getUserid().equals("")) {
			return;
		}
		if(pageNumber == 0) pageNumber = 1;
		switch (position) {
		case MyBillListActivity.INDEX_BILL_ALL:
			if (position == POSITION_TOTAL) {
				Requester.getBillList(true, myHandler, pageNumber + "", acc.getUserid());
			}
			break;
		case MyBillListActivity.INDEX_BILL_MAKE:
			if (position == POSITION_INCOME) {
				Requester.getBillList(true, myHandler, pageNumber + "", acc.getUserid());
			}
			break;
		case MyBillListActivity.INDEX_BILL_EXPENSE:
			if (position == POSITION_PAY) {
				Requester.getBillList(true, myHandler, pageNumber + "", acc.getUserid());
			}
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
		}, 5);
		switch(msg.what) {
		case Requester.RESPONSE_TYPE_MY_BILL:
			if (msg.obj != null) {
				MyBillListResp billResp = (MyBillListResp) msg.obj;
				if ("0".equals(billResp.status) || "0000".equals(billResp.status)) {
					if (billResp.list != null && billResp.list.length > 0) {
						List<BillList> blist = Arrays.asList(billResp.list);
						if(pageNumber == 0) {
							billList.clear();
						}
						if(blist != null) {
							for(BillList item : blist) {
								String coins = item.flowcoins;
								int c = NumberUtils.Str2Int(coins);
								switch(position) {
								case POSITION_TOTAL:
									billList.add(item);
									break;
								case POSITION_INCOME:
									if(c >= 0) {
										billList.add(item);
									}
									break;
								case POSITION_PAY:
									if(c < 0) {
										billList.add(item);
									}
									break;
								}
							}
						} else {
							blist = new ArrayList<BillList>();
							billList.addAll(blist);
						}
						adapter.setData(billList);
						pageNumber ++;
					}
				} else {
					PromptDialog.Alert(MyBillListActivity.class, "您的网络部给力啊！");
				}
			}
			break;
		}
		
		return false;
	}

}