package com.cmmobi.looklook.v31;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

import com.cmmobi.looklook.R;
import com.cmmobi.looklook.common.adapter_del.MySafeboxListAdapter;
import com.cmmobi.looklook.common.view.pulltorefresh.PullToRefreshBase;
import com.cmmobi.looklook.common.view.pulltorefresh.PullToRefreshBase.OnRefreshListener2;
import com.cmmobi.looklook.common.view.pulltorefresh.PullToRefreshListView;

/**
 * @author wuxiang
 * @email wuxiang@cmmobi.com
 * @date 2013-10-24
 */
public class SafeboxActivity extends Activity implements
		OnRefreshListener2<ListView> {

	private static final String TAG = SafeboxActivity.class.getSimpleName();
	private PullToRefreshListView xlvMySafebox;
	private ListView lvMyZoneList;
	private View vTitle;
	private View vTitleChecked;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d(TAG, "SafeboxActivity onCreate");
		setContentView(R.layout.del_activity_safebox);
		vTitle = findViewById(R.id.ll_title);
		vTitleChecked = findViewById(R.id.ll_title_checked);
		xlvMySafebox = (PullToRefreshListView) findViewById(R.id.xlv_my_safebox);
		xlvMySafebox.setShowIndicator(false);
		xlvMySafebox.setOnRefreshListener(this);
		lvMyZoneList = xlvMySafebox.getRefreshableView();
		ArrayList<String> list = new ArrayList<String>();
		list.add("");
		list.add("");
		list.add("");
		list.add("");
		list.add("");
		list.add("");
		list.add("");
		list.add("");
		list.add("");
		list.add("");
		list.add("");
		list.add("");
		list.add("");
		list.add("");
		list.add("");
		list.add("");
		list.add("");
		list.add("");
		list.add("");
		list.add("");
		list.add("");
		list.add("");
		list.add("");
		list.add("");
		list.add("");
//		lvMyZoneList.setAdapter(new MySafeboxListAdapter(this, list));
	}

	@Override
	protected void onNewIntent(Intent intent) {
		Log.d(TAG, "SafeboxActivity onNewIntent");
		super.onNewIntent(intent);
	}

	// 显示菜单栏
	public void showSliding(View view) {
		sendBroadcast(new Intent(HomeSlidingActivity.ACTION_SHOW_MENU));
	}

	private boolean isCheckedShow;

	/**
	 * 显示正常标题栏
	 */
	public void showTitle() {
		vTitle.setVisibility(View.VISIBLE);
		vTitleChecked.setVisibility(View.GONE);
		isCheckedShow = false;
	}

	/**
	 * 显示日记选中后标题栏
	 */
	public void showCheckedTitle() {
		vTitle.setVisibility(View.GONE);
		vTitleChecked.setVisibility(View.VISIBLE);
		isCheckedShow = true;
	}

	/**
	 * 判断当前选中标题栏是否显示 true-显示 false-未显示
	 */
	public boolean isCheckedTitleShow() {
		return isCheckedShow;
	}

	@Override
	public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
		// TODO Auto-generated method stub

	}
}
