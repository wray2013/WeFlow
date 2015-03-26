package com.etoc.weflow.activity;

import java.util.ArrayList;
import java.util.List;

import com.etoc.weflow.Config;
import com.etoc.weflow.R;
import com.etoc.weflow.WeFlowApplication;
import com.etoc.weflow.adapter.MyMessageAdapter;
import com.etoc.weflow.dao.AccountInfo;
import com.etoc.weflow.dao.MyMessage;
import com.etoc.weflow.net.GsonResponseObject.MessageList;
import com.etoc.weflow.utils.DisplayUtil;
import com.etoc.weflow.utils.PType;
import com.etoc.weflow.utils.PTypeTransfer;
import com.etoc.weflow.utils.RandomUtils;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class MyMessageActivity extends TitleRootActivity implements OnRefreshListener2<ListView> {

	private PullToRefreshListView xlvRefreshList;
	private ListView lvMsgListView;
	
	private MyMessageAdapter adapter;
	
	private static final int PAGE_SIZE = 10;
	private int pageNumber = 0;
	
	private List<MessageList> msgList = new ArrayList<MessageList>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		initViews();
		
		handler.postDelayed(new MyRunnable(pageNumber), 10);
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
		
//		msgList.addAll(makeFakeData());
		
		adapter.setData(msgList);
		
		lvMsgListView.setAdapter(adapter);
		lvMsgListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View v, int position,
					long id) {
				// TODO Auto-generated method stub
				MessageList item = (MessageList) parent.getAdapter().getItem(position);//msgList.get(position);
				// 有web跳转优先跳转web页
				if(item.pageurl != null && !item.pageurl.equals("")) {
					Intent webIntent = new Intent(MyMessageActivity.this, WebViewActivity.class);
					webIntent.putExtra("pageurl", item.pageurl);
					startActivity(webIntent);
					return;
				}
				
				if(item.type != null && !item.type.equals("")) {
					PType ptype = PTypeTransfer.getPType(item.type);
					Intent colIntent = PTypeTransfer.createColumnIntent(MyMessageActivity.this, ptype, true);
					startActivity(colIntent);
					return;
				}
			}
		});
	}
	
	/*
	 * 	public String msgid;
		public String type; //栏目类型
		public String picurl;
		public String title; //消息标题
		public String content; //消息体内容
		public String flowcoins;
		public String time; //消息毫秒时间
		public String pageurl; //跳转地址
		public String productid;
		public String extradata; //预留字段
	 */
	public static List<MessageList> makeFakeData() {
		List<MessageList> mlist = new ArrayList<MessageList>();
		for(int i = 0; i < 8; i ++) {
			MessageList item = new MessageList();
			item.msgid = "msg" + String.format("%05d", RandomUtils.getRandom(10000));
			item.type = i % 2 == 0 ? "" : String.format("%02d", i);
			item.picurl = Config.IMAGES[i + 41];
			item.title = "第" + i + "条消息";
			item.content = "第" + i + "条消息内容";
			item.flowcoins = i + "";
			item.time = System.currentTimeMillis() + "";
			item.pageurl = i % 2 == 0 ? "www.baidu.com" : "";
			item.productid = "";
			item.extradata = "";
			mlist.add(item);
		}
		return mlist;
	}
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()) {
		case R.id.btn_title_right:
			WeFlowApplication.getAppInstance().clearMyMessage();
			handler.postDelayed(new MyRunnable(0), 10);
			pageNumber = 0;
			break;
		}
		super.onClick(v);
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

	private class MyRunnable implements Runnable {
		
		private int page;
		
		public MyRunnable(int p) {
			this.page = p;
		}
		
		private int getCurrentSize() {
			return PAGE_SIZE * (page + 1);
		}
		
		@Override
		public void run() {
			// TODO Auto-generated method stub
			xlvRefreshList.onRefreshComplete();
			msgList.clear();
			AccountInfo info = WeFlowApplication.getAppInstance().getAccountInfo();
			if(info == null || info.getUserid() == null) {
				adapter.setData(msgList);
				return;
			}
			List<MyMessage> l = WeFlowApplication.getAppInstance().getMyMessageByUserid(info.getUserid());
			if(l != null) {
				for(MyMessage msg : l) {
					MessageList item = new MessageList();
					item.msgid = msg.getMsgid();
					item.userid = info.getUserid();
					item.type = msg.getType();
					item.picurl = msg.getPicurl();
					item.title = msg.getTitle();
					item.content = msg.getContent();
					item.flowcoins = msg.getFlowcoins();
					item.time = msg.getTime();
					item.pageurl = msg.getPageurl();
					item.productid = msg.getProductid();
					item.extradata = msg.getExtradata();
					msgList.add(item);
					if(msgList.size() >= getCurrentSize()) {
						break;
					}
				}
				if(msgList.size() < l.size()) {
					pageNumber ++;
				}
			}
			adapter.setData(msgList);
		}
	};
	
	
	@Override
	public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
		// TODO Auto-generated method stub
		pageNumber = 0;
		handler.postDelayed(new MyRunnable(pageNumber), 10);
	}

	@Override
	public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
		// TODO Auto-generated method stub
		handler.postDelayed(new MyRunnable(pageNumber), 10);
	}

}
