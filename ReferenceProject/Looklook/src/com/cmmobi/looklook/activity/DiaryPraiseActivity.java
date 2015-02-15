/**
 * 
 */
package com.cmmobi.looklook.activity;

import java.util.ArrayList;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import cn.zipper.framwork.core.ZActivity;
import cn.zipper.framwork.core.ZApplication;
import cn.zipper.framwork.core.ZDialog;

import com.cmmobi.looklook.R;
import com.cmmobi.looklook.common.gson.GsonResponse2;
import com.cmmobi.looklook.common.gson.Requester2;
import com.cmmobi.looklook.common.listview.pulltorefresh.PullToRefreshBase.OnRefreshListener;
import com.cmmobi.looklook.common.listview.pulltorefresh.PullToRefreshListView;
import com.cmmobi.looklook.common.storage.StorageManager;
import com.cmmobi.looklook.common.utils.CmmobiClickAgentWrapper;
import com.cmmobi.looklook.common.utils.UmengclickAgentWrapper;
import com.cmmobi.looklook.common.web.WebImageView;
import com.cmmobi.looklook.info.profile.AccountInfo;
import com.cmmobi.looklook.info.profile.ActiveAccount;

/**
 * @author jiayunan
 * 
 * @create 2013-8-5
 */
public class DiaryPraiseActivity extends ZActivity implements
		OnItemClickListener, OnRefreshListener {

	private static final String TAG = "DiaryPraiseActivity";
	public static final String INTENT_ACTION_USERID = "userid";

	private static final boolean ISDEBUG = true;
	private LayoutInflater inflater;
	private PullToRefreshListView listView;
	private ListView lv;
	private ArrayList<GsonResponse2.getDiaryForwardUsers> dpList;
	private boolean isShowAllPraise = false;
	private String userID;
	private AccountInfo ai;
	private String otherUserID;
	private DiaryPraiseAdapter dpAdapter;
	private String diaryID;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_diary_praise);
		
		diaryID = getIntent().getStringExtra("diaryID");

		userID = ActiveAccount.getInstance(ZApplication.getInstance()).getUID();
		ai = AccountInfo.getInstance(userID);
		ZDialog.show(R.layout.progressdialog, true, true, this);
		Requester2.getDiaryForwardUsers(getHandler(), diaryID, null, null);

		otherUserID = getIntent().getStringExtra(INTENT_ACTION_USERID);

		isShowAllPraise = false;
		inflater = LayoutInflater.from(this);
		findViewById(R.id.iv_back).setOnClickListener(this);
		listView = (PullToRefreshListView) findViewById(R.id.lv_activity_diary_praise);
		listView.setOnRefreshListener(this);
		dpList = new ArrayList<GsonResponse2.getDiaryForwardUsers>();

		lv = listView.getRefreshableView();
	}

	@Override
	public void onResume() {
		super.onResume();
		UmengclickAgentWrapper.onResume(this);
		CmmobiClickAgentWrapper.onResume(this);
	}

	@Override
	public void onPause() {
		super.onPause();
		UmengclickAgentWrapper.onPause(this);
		CmmobiClickAgentWrapper.onPause(this);
	}

	@Override
	public void onStop(){
		super.onStop();
		CmmobiClickAgentWrapper.onStop(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.iv_back:
			finish();
			break;
		default:
			break;
		}
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position,
			long arg3) {
		if (!userID.equals(dpList.get(position).userid)) {
			Intent intent = new Intent(this, HomepageOtherDiaryActivity.class);
			intent.putExtra("userid", dpList.get(position).userid);
			intent.putExtra("nickname", dpList.get(position).nickname);
			this.startActivity(intent);
		}
	}

	@Override
	public boolean handleMessage(Message msg) {
		switch (msg.what) {
		case Requester2.RESPONSE_TYPE_GET_DIARY_FORWORD:
			ZDialog.dismiss();
			GsonResponse2.getDiaryForwardUsersResponse gdpResponse = (GsonResponse2.getDiaryForwardUsersResponse) msg.obj;
			dpList = new ArrayList<GsonResponse2.getDiaryForwardUsers>();
			if (gdpResponse != null && gdpResponse.status.equals("0")
					&& gdpResponse.forwords.length > 0) {
				for (int j = 0; j < gdpResponse.forwords.length; j++) {
					dpList.add(gdpResponse.forwords[j]);
				}
			} else {
				isShowAllPraise = true;
			}
			dpAdapter = new DiaryPraiseAdapter(dpList);
			lv.setAdapter(dpAdapter);
			lv.setOnItemClickListener(this);
			listView.onRefreshComplete();
			break;

		default:
			break;
		}
		return false;
	}

	class DiaryPraiseAdapter extends BaseAdapter {

		ArrayList<GsonResponse2.getDiaryForwardUsers> list;

		public DiaryPraiseAdapter(
				ArrayList<GsonResponse2.getDiaryForwardUsers> list2) {
			this.list = list2;
		}

		@Override
		public int getCount() {
			return list.size();
		}

		@Override
		public Object getItem(int position) {
			return list.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			GsonResponse2.getDiaryForwardUsers item = list.get(position);

			viewHolder holder;
			if (null == convertView) {
				convertView = inflater.inflate(
						R.layout.activity_diary_praise_item, null);
				holder = new viewHolder();
				holder.pic = (WebImageView) convertView
						.findViewById(R.id.zan_list_item_head);

				holder.name = (TextView) convertView.findViewById(R.id.tv_name);

			} else {
				holder = (viewHolder) convertView.getTag();
			}

			holder.pic.setImageUrl(R.drawable.moren_touxiang, 1,
					item.headimageurl, true);
			holder.name.setText(item.nickname);

			convertView.setTag(holder);
			return convertView;
		}
	}

	static class viewHolder {
		WebImageView pic;
		TextView name;
	}

	@Override
	protected void onDestroy() {
		persistMsg();
		super.onDestroy();
	}

	private void persistMsg() {
		if (ISDEBUG)
			Log.d(TAG, "persistMsg");
		if (KEY != null)
			StorageManager.getInstance().putItem(KEY, dpList, ArrayList.class);
	}

	private static final String FANS_PERSIST_KEY = "fans_persist_key";
	private static String KEY = null;

	/*
	 * private ArrayList<GsonResponse2.myattentionlistUsers> getMsg(){
	 * if(ISDEBUG)Log.d(TAG, "getMsg"); String
	 * uid=ActiveAccount.getInstance(this).getUID(); if(null==uid){ Log.e(TAG,
	 * "uid is null"); return new
	 * ArrayList<GsonResponse2.myattentionlistUsers>(); }
	 * KEY=uid+"_"+FANS_PERSIST_KEY;
	 * ArrayList<GsonResponse2.myattentionlistUsers> list =
	 * (ArrayList<GsonResponse2.myattentionlistUsers>)
	 * StorageManager.getInstance() .getItem(KEY, new
	 * TypeToken<ArrayList<GsonResponse2.myattentionlistUsers>>(){}.getType());
	 * if (null==list) { list=new
	 * ArrayList<GsonResponse2.myattentionlistUsers>(); } return list; }
	 */

	@Override
	public void onRefresh() {
		if (!isShowAllPraise) {
			if (otherUserID != null) {
				Requester2.getDiaryForwardUsers(getHandler(), diaryID, null,
						"1");
			} else {
				Requester2.getDiaryForwardUsers(getHandler(), diaryID, null,
						"2");
			}
		} else {
			listView.onRefreshComplete();
			Toast.makeText(DiaryPraiseActivity.this, "没有更多",
					Toast.LENGTH_SHORT).show();
		}
	}
}
