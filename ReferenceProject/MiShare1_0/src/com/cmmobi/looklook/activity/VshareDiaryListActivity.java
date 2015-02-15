package com.cmmobi.looklook.activity;

import java.util.ArrayList;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import cn.zipper.framwork.core.ZActivity;
import com.cmmobi.looklook.R;
import com.cmmobi.looklook.common.adapter.VshareDiaryListAdapter;
import com.cmmobi.looklook.common.gson.GsonResponse3.MyDiary;
import com.cmmobi.looklook.common.gson.GsonResponse3.MyDiaryList;
import com.cmmobi.looklook.common.utils.CmmobiClickAgentWrapper;
import com.cmmobi.looklook.common.view.pulltorefresh.PullToRefreshBase;
import com.cmmobi.looklook.common.view.pulltorefresh.PullToRefreshBase.OnRefreshListener2;
import com.cmmobi.looklook.common.view.pulltorefresh.PullToRefreshListView;
import com.cmmobi.looklook.dialog.ShareDialog;
import com.cmmobi.looklook.fragment.MyZoneFragment;
import com.cmmobi.looklook.fragment.MyZoneFragment.MyZoneItem;
import com.cmmobi.looklook.info.profile.DiaryManager;
import com.google.gson.Gson;

/**
 * 微享选择日记列表
 * @author youtian
 * @email youtian@cmmobi.com
 * @date  2013-11－18
 * 
 */
public class VshareDiaryListActivity extends ZActivity implements OnRefreshListener2<ListView>{

	private ImageView iv_back;
	private TextView tv_commit;
	private ListView lv_DiaryList;
	private VshareDiaryListAdapter diaryListAdapter;
	private ArrayList<MyZoneItem> myZoneItems=new ArrayList<MyZoneFragment.MyZoneItem>();
	protected DiaryManager diaryManager;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_vshare_diarylist);
		//inflater = LayoutInflater.from(this);
		iv_back = (ImageView) findViewById(R.id.iv_back);
		iv_back.setOnClickListener(this);
		iv_back.setOnLongClickListener(new View.OnLongClickListener() {
			
			@Override
			public boolean onLongClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(VshareDiaryListActivity.this,LookLookActivity.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
				VshareDiaryListActivity.this.finish();
				return false;
			}
		});
		
		tv_commit = (TextView) findViewById(R.id.tv_commit);
		tv_commit.setOnClickListener(this);
		
		lv_DiaryList = (ListView) findViewById(R.id.lv_diaries);
		lv_DiaryList.setStackFromBottom(false);
		
		diaryListAdapter = new VshareDiaryListAdapter(this, myZoneItems);
		lv_DiaryList.setAdapter(diaryListAdapter);
		
		diaryManager =DiaryManager.getInstance();
		loadLocalData();
		
	}

	//加载本地数据
	private boolean loadLocalData(){
		myZoneItems.clear();
		ArrayList<MyZoneItem> localList=diaryManager.getMyZoneItems(0);
		myZoneItems.addAll(localList);
		myZoneItems.remove(0);
		diaryListAdapter.notifyDataSetChanged();
		return myZoneItems.size()>0;
	}

	@Override
	public boolean handleMessage(Message msg) {
		// TODO Auto-generated method stub
		return false;
	}


	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.iv_back:
			this.setResult(RESULT_CANCELED);
			this.finish();
			break;
		case R.id.tv_commit:
			if(diaryListAdapter.getCheckedDiary()!=null && diaryListAdapter.getCheckedDiary().size()==1){
				Intent ret = new Intent();
				MyDiaryList myDiaryList = (MyDiaryList)diaryListAdapter.getCheckedDiary().get(0);
				MyDiary [] diaryGroup = null;
				if(myDiaryList.contain!=null && !myDiaryList.contain.equals("")){
					String[] diaryIds = myDiaryList.contain.split(",");
					diaryGroup = new MyDiary[diaryIds.length];
					for(int i=0; i<diaryIds.length; i++){
						diaryGroup[i] = diaryManager.findMyDiaryByDiaryID(diaryIds[i]);
					}
				}else if(myDiaryList.diaryid!=null && !myDiaryList.diaryid.equals("")){
					diaryGroup = new MyDiary[1];
					diaryGroup[0] = diaryManager.findMyDiaryByDiaryID(myDiaryList.diaryid);
				}
				Intent shareIntent = new Intent(this, ShareDiaryActivity.class);
				shareIntent.putExtra(DiaryPreviewActivity.INTENT_ACTION_DIARYLIST_STRING, new Gson().toJson(myDiaryList));
				if(diaryGroup!=null){
					String json = new Gson().toJson(diaryGroup);
					shareIntent.putExtra(DiaryPreviewActivity.INTENT_ACTION_DIARY_STRING, json);
				}
				shareIntent.putExtra(ShareDialog.TYPY_SHARE, ShareDialog.TYPE_SHARE_V);
				startActivity(shareIntent);
				
				this.setResult(RESULT_OK, ret);
			}
			this.finish();
			break;
		default:
			break;
		}
	}


	@Override
	public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void onResume() {
		super.onResume();
		CmmobiClickAgentWrapper.onResume(this);
	}

	@Override
	public void onPause() {
		super.onPause();
		CmmobiClickAgentWrapper.onPause(this);
	}

	@Override
	public void onStop(){
		super.onStop();
		CmmobiClickAgentWrapper.onStop(this);
	}

	
}
