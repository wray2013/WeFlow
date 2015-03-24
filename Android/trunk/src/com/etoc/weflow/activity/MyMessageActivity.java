package com.etoc.weflow.activity;

import java.util.ArrayList;
import java.util.List;

import com.etoc.weflow.R;
import com.etoc.weflow.adapter.MyMessageAdapter;
import com.etoc.weflow.net.GsonResponseObject.MessageList;
import com.etoc.weflow.utils.DisplayUtil;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import android.os.Bundle;
import android.os.Message;
import android.widget.ListView;

public class MyMessageActivity extends TitleRootActivity implements OnRefreshListener2<ListView> {

	private PullToRefreshListView xlvRefreshList;
	private ListView lvMsgListView;
	
	private MyMessageAdapter adapter;
	
	private List<MessageList> msgList = new ArrayList<MessageList>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		initViews();
		
	}
	
	private void initViews() {
		// TODO Auto-generated method stub
		setTitleText("我的消息");
//		hideRightButton();
		setRightButtonText("清空");
		
		xlvRefreshList = (PullToRefreshListView) findViewById(R.id.xlv_mymessage_list);
		xlvRefreshList.setShowIndicator(false);
		xlvRefreshList.setOnRefreshListener(this);
		
		lvMsgListView = xlvRefreshList.getRefreshableView();
		lvMsgListView.setDividerHeight(DisplayUtil.getSize(this, 2));
		
		adapter = new MyMessageAdapter(this);
		adapter.setData(msgList);
		
		lvMsgListView.setAdapter(adapter);
	}

	@Override
	public boolean handleMessage(Message msg) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int subContentViewId() {
		// TODO Auto-generated method stub
		return R.layout.activity_msg_list;
	}

	private Runnable runnable = new Runnable() {
		
		@Override
		public void run() {
			// TODO Auto-generated method stub
			
		}
	};
	
	
	@Override
	public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
		// TODO Auto-generated method stub
		handler.postDelayed(runnable, 10);
	}

	@Override
	public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
		// TODO Auto-generated method stub
		
	}

}
