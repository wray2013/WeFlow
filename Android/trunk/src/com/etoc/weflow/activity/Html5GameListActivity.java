package com.etoc.weflow.activity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.etoc.weflow.R;
import com.etoc.weflow.adapter.H5GameListAdapter;
import com.etoc.weflow.net.GsonResponseObject.GameWrapper;
import com.etoc.weflow.net.GsonResponseObject.queryGameListResp;
import com.etoc.weflow.net.GsonResponseObject.queryGameParamResp;
import com.etoc.weflow.net.Requester;
import com.etoc.weflow.utils.DisplayUtil;
import com.google.gson.Gson;
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

public class Html5GameListActivity extends TitleRootActivity implements OnRefreshListener2<ListView>, OnItemClickListener {

	private PullToRefreshListView xlvH5Game;
	private ListView lvGameList;
	private H5GameListAdapter adapter;
	
	private List<GameWrapper> gamelist = new ArrayList<GameWrapper>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		hideRightButton();
		setTitleText("玩游戏");
		initViews();
	}
	
	private void initViews() {
		// TODO Auto-generated method stub
		xlvH5Game = (PullToRefreshListView) findViewById(R.id.xlv_h5game_list);
		xlvH5Game.setShowIndicator(false);
		xlvH5Game.setOnRefreshListener(this);
		
		lvGameList = xlvH5Game.getRefreshableView();
		lvGameList.setDividerHeight(DisplayUtil.getSize(this, 30));
		lvGameList.setOnItemClickListener(this);
		
		makeFakeData();
		
		adapter = new H5GameListAdapter(this);
		adapter.setData(gamelist);
		
		lvGameList.setAdapter(adapter);
		
		Requester.queryGameList(true, handler);
		
	}
	
	private String[] pics = {
			"http://s0.hao123img.com/res/r/image/2015-04-30/5d8c59dfe0cb17c3d9bb8ee78b85d4f4.jpg",
			"http://s0.hao123img.com/res/r/image/2015-04-30/403a67988371d778dfd9e1b52c9a4995.jpg",
			"http://s0.hao123img.com/res/r/image/2015-04-30/76ec0264614e70a00bda8682d581921e.jpg",
			"http://s0.hao123img.com/res/r/image/2015-05-02/1b678556c09f5fee4604680f7d6faa5a.jpg",
			"http://s0.hao123img.com/res/r/image/2015-04-29/31780b01f66b316251e3538cbfcfafac.jpg",
	};

	private void makeFakeData() {
		// TODO Auto-generated method stub
		for(int i = 0; i < 5; i ++) {
			GameWrapper wrapper = new GameWrapper();
			queryGameParamResp param = new queryGameParamResp();
			wrapper.gameid = "" + i;
			wrapper.gamename = "游戏" + (i + 1);
			wrapper.gamepic = pics[i];
			wrapper.gameurl = "http://113.57.243.18:8090/html5/index.html";
			param.status = "0";
			param.rangea = (500 + i) + "";
			param.rangeb = (520 + i) + "";
			param.amendment = "2";
			wrapper.params = param;
			
			gamelist.add(wrapper);
		}
	}

	@Override
	public boolean handleMessage(Message msg) {
		// TODO Auto-generated method stub
		handler.postDelayed(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				xlvH5Game.onRefreshComplete();
			}
		}, 5);
		switch(msg.what) {
		case Requester.RESPONSE_TYPE_GET_GAME_LIST:
			if (msg.obj != null) {
				queryGameListResp resp = (queryGameListResp) msg.obj;
				if("0000".equals(resp.status) || "0".equals(resp.status)) {
					if(resp.gamelist != null && resp.gamelist.length > 0) {
						gamelist = Arrays.asList(resp.gamelist);
						if(adapter != null) {
							adapter.setData(gamelist);
						}
					}
				}
			}
			break;
		}
		return false;
	}

	@Override
	public int subContentViewId() {
		// TODO Auto-generated method stub
		return R.layout.activity_h5gamelist;
	}

	@Override
	public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
		// TODO Auto-generated method stub
		Requester.queryGameList(false, handler);
	}

	@Override
	public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
		// TODO Auto-generated method stub
		handler.postDelayed(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				xlvH5Game.onRefreshComplete();
			}
		}, 5);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View v, int position,
			long id) {
		Object obj = parent.getAdapter().getItem(position);
		if(obj instanceof GameWrapper) {
			GameWrapper item = (GameWrapper) obj;
			// 有web跳转优先跳转web页
			if(item.gameurl != null && !item.gameurl.equals("")) {
				Intent webIntent = new Intent(this, Html5GameWebViewActivity.class);
				webIntent.putExtra("pageurl", item.gameurl);
				webIntent.putExtra("pagetitle", item.gamename);
				webIntent.putExtra("gameid", item.gameid);
				webIntent.putExtra("gameparam", new Gson().toJson(item.params));
				startActivity(webIntent);
				return;
			}
		}
	}

}
