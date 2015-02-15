package com.cmmobi.looklook.activity;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import cn.zipper.framwork.core.ZActivity;
import cn.zipper.framwork.core.ZApplication;
import cn.zipper.framwork.core.ZDialog;
import cn.zipper.framwork.core.ZViewFinder;

import com.cmmobi.looklook.R;
import com.cmmobi.looklook.activity.SettingStorageManagerNotSynchronizedActivity.GetMoreDiaryTask;
import com.cmmobi.looklook.common.adapter.FriendListAdapter;
import com.cmmobi.looklook.common.adapter.FriendsAddAdapter;
import com.cmmobi.looklook.common.constant.Constant;
import com.cmmobi.looklook.common.gson.GsonRequest2;
import com.cmmobi.looklook.common.gson.GsonResponse2;
import com.cmmobi.looklook.common.gson.GsonResponse2.MyBind;
import com.cmmobi.looklook.common.gson.GsonResponse2.User;
import com.cmmobi.looklook.common.gson.GsonResponse2.listThirdPlatformUserItem;
import com.cmmobi.looklook.common.gson.GsonResponse2.listThirdPlatformUserResponse;
import com.cmmobi.looklook.common.gson.Requester2;
import com.cmmobi.looklook.common.gson.WeiboRequester;
import com.cmmobi.looklook.common.gson.WeiboResponse.RWUserInfo;
import com.cmmobi.looklook.common.gson.WeiboResponse.SWUserInfo;
import com.cmmobi.looklook.common.gson.WeiboResponse.TWUserInfo;
import com.cmmobi.looklook.common.utils.CmmobiClickAgentWrapper;
import com.cmmobi.looklook.common.utils.UmengclickAgentWrapper;
import com.cmmobi.looklook.common.view.pulltorefresh.PullToRefreshBase;
import com.cmmobi.looklook.common.view.pulltorefresh.PullToRefreshBase.OnRefreshListener;
import com.cmmobi.looklook.common.view.pulltorefresh.PullToRefreshBase.OnRefreshListener2;
import com.cmmobi.looklook.common.view.pulltorefresh.PullToRefreshListView;
import com.cmmobi.looklook.fragment.WrapRecommendUser;
import com.cmmobi.looklook.info.profile.AccountInfo;
import com.cmmobi.looklook.info.profile.ActiveAccount;
import com.cmmobi.looklook.info.profile.FriendsAddManager;
import com.cmmobi.looklook.info.profile.LoginSettingManager;
import com.cmmobi.looklook.prompt.Prompt;
import com.cmmobi.sns.api.CmmobiSnsLib;
import com.cmmobi.sns.oauthv2.OAuthV2;
import com.cmmobi.sns.utils.ConfigUtil.SHARE_TO;
import com.cmmobi.sns.utils.PinYinUtil;
import com.weibo.sdk.android.api.WeiboAuthListener;
import com.weibo.sdk.android.api.WeiboDialogError;
import com.weibo.sdk.android.api.WeiboException;

public class FriendAddRecommendActivity extends ZActivity implements OnItemClickListener, OnRefreshListener2<ListView>{
	private static final String TAG = "FriendAddRecommendActivity";
	private ListView lv_lookfriends;
	private PullToRefreshListView xlv_lookfriends;
	private FriendListAdapter list_friend_adapter;
	private FriendsAddAdapter friendsAddAdapter;
	private String timestamp;
	private int index;
	
	@Override
	public boolean handleMessage(Message msg) {
		// TODO Auto-generated method stub
		switch (msg.what) {
		case Requester2.RESPONSE_TYPE_LIST_USER_RECOMMEND:
			if(msg.obj != null){
				GsonResponse2.listUserRecommendResponse luRecommendResponse = (GsonResponse2.listUserRecommendResponse) msg.obj;
				if(luRecommendResponse!=null && luRecommendResponse.status!=null && luRecommendResponse.status.equals("0")){
					timestamp = luRecommendResponse.timestamp;
					index = friendsAddAdapter.getCount();
					friendsAddAdapter.addData(luRecommendResponse.users);
					friendsAddAdapter.notifyDataSetChanged();
					xlv_lookfriends.onRefreshComplete();
        			xlv_lookfriends.post(new Runnable() {

				        @Override
				        public void run() {
				            // TODO Auto-generated method stub
				        	xlv_lookfriends.getRefreshableView().setSelection(index);
				        }
				    });
				}else{
					if (luRecommendResponse != null
							&& luRecommendResponse.status.equals("200600")) {
						Prompt.Dialog(this, false, "提示",
								Constant.CRM_STATUS[Integer
										.parseInt(luRecommendResponse.crm_status)],
								null);
					} else {
						Prompt.Dialog(this, false, "提示", "操作失败，请稍后再试", null);
					}
				}
			}
			break;
		}
		return false;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.lv_activity_friends_back:
			finish();
			break;
		default:
			break;
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_friends_add_recommend);

		ZViewFinder finder = getZViewFinder();
		finder.setOnClickListener(R.id.lv_activity_friends_back, this);


		xlv_lookfriends = (PullToRefreshListView) findViewById(R.id.lv_activity_friends_add_list2);
		xlv_lookfriends.setShowIndicator(false);
		xlv_lookfriends.setOnRefreshListener(this);
		lv_lookfriends = xlv_lookfriends.getRefreshableView();

		friendsAddAdapter = new FriendsAddAdapter(this);

		lv_lookfriends.setAdapter(friendsAddAdapter);
		lv_lookfriends.setOnItemClickListener(this);
		Requester2.listUserRecommend(handler, "");
		
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
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {

		Intent intent = new Intent(this, HomepageOtherDiaryActivity.class);
		intent.putExtra("userid", friendsAddAdapter.getItem(position -1).userid);
		intent.putExtra("nickname", friendsAddAdapter.getItem(position-1).nickname);
		this.startActivity(intent);
	}

	@Override
	public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
		// TODO Auto-generated method stub
		index = 1;
		GetMoreDiaryTask taskall = new GetMoreDiaryTask(); 
		taskall.execute(1);
	}

	@Override
	public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
		// TODO Auto-generated method stub
		GetMoreDiaryTask taskall = new GetMoreDiaryTask();  
		taskall.execute(0);
	}
	
	  class GetMoreDiaryTask extends AsyncTask<Integer, Integer, String> {  
    	  
		    int tag;
	        @Override  
	        protected void onCancelled() {  
	            super.onCancelled();  
	        }  
	        @Override  
	        protected void onPostExecute(String result) { 
	        		if(tag != 0){
	        			xlv_lookfriends.onRefreshComplete();
	        			xlv_lookfriends.post(new Runnable() {

					        @Override
					        public void run() {
					            // TODO Auto-generated method stub
					        	xlv_lookfriends.getRefreshableView().setSelection(index);
					        }
					    });
	        		}	
	        }  
	        @Override  
	        protected void onPreExecute() {  

	        }  
	        @Override  
	        protected void onProgressUpdate(Integer... values) {  
	             
	        }
	        
			@Override
			protected String doInBackground(Integer... params) {
				// TODO Auto-generated method stub
	            try {  
	            	tag = params[0];
	            	if(tag == 0 && timestamp!=null && !timestamp.equals("")){
	            		Requester2.listUserRecommend(handler, timestamp);
	            	}
	            	
	            } catch (Exception e) {  
	                e.printStackTrace();  
	            }  
	            return null; 
			}  
	    }
}