package com.cmmobi.looklook.activity;

import java.util.List;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Message;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;
import cn.zipper.framwork.core.ZActivity;
import cn.zipper.framwork.core.ZDialog;

import com.cmmobi.looklook.MainApplication;
import com.cmmobi.looklook.R;
import com.cmmobi.looklook.R.color;
import com.cmmobi.looklook.common.adapter.FriendsRequestAdapter;
import com.cmmobi.looklook.common.gson.GsonResponse3;
import com.cmmobi.looklook.common.gson.Requester3;
import com.cmmobi.looklook.common.service.RemoteManager;
import com.cmmobi.looklook.common.utils.CmmobiClickAgentWrapper;
import com.cmmobi.looklook.info.profile.AccountInfo;
import com.cmmobi.looklook.info.profile.ActiveAccount;
import com.cmmobi.looklook.info.profile.WrapUser;
import com.cmmobi.looklook.prompt.Prompt;

public class FriendsRequestActivity extends ZActivity implements
		OnClickListener {

	private ImageView iv_back;
	private ListView lv_requests;
	private FriendsRequestAdapter friendsRequestAdapter;
	private AccountInfo accountInfo;
	private static long sevenDays = 7*24*60*60*1000; 
	private PopupWindow pw_clear; //菜单
	private LayoutInflater inflater;
	private View currentView;
	private String currentUserid;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_friends_request);
		iv_back = (ImageView) findViewById(R.id.iv_back);
		lv_requests = (ListView) findViewById(R.id.lv_requests);
		friendsRequestAdapter = new FriendsRequestAdapter(this);
		lv_requests.setAdapter(friendsRequestAdapter);
		lv_requests.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				// TODO Auto-generated method stub
				currentView = arg1;
				currentUserid = friendsRequestAdapter.getItem(arg2).userid;
				arg1.setBackgroundColor(getResources().getColor(R.color.light_gray));	
				pw_clear.showAtLocation(findViewById(R.id.rl_requestfriends),
						Gravity.BOTTOM, 0, 0);
				return false;
			}
		});
		iv_back.setOnClickListener(this);
		iv_back.setOnLongClickListener(new View.OnLongClickListener() {
			@Override
			public boolean onLongClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(FriendsRequestActivity.this,LookLookActivity.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
				FriendsRequestActivity.this.finish();
				return false;
			}
		});
		
		accountInfo = AccountInfo.getInstance(ActiveAccount.getInstance(this).getUID());
		List<WrapUser> friendlist = accountInfo.friendsRequestList.getCache();
		for(int i=0; i< friendlist.size(); i++){
			if(Long.parseLong(friendlist.get(i).update_time)- System.currentTimeMillis() > sevenDays){
				friendlist.remove(i);
				i--;
			}
		}
		friendsRequestAdapter.setData(friendlist);
		friendsRequestAdapter.notifyDataSetChanged();
		inflater = this.getLayoutInflater();
		initClearChoice();
		//accountInfo.t_friendsRequestList = accountInfo.lt_friendsRequestList;
	}

	//显示清除选择界面
	private void initClearChoice(){
		View view = inflater.inflate(R.layout.activity_vshare_list_clear_menu ,
				null);
		pw_clear = new PopupWindow(view, LayoutParams.FILL_PARENT,
				LayoutParams.FILL_PARENT, true);
		pw_clear.setBackgroundDrawable(getResources().getDrawable(
				R.color.transparent));

		pw_clear.setOnDismissListener(new OnDismissListener() {
			
			@Override
			public void onDismiss() {
				// TODO Auto-generated method stub
				currentView.setBackgroundColor(Color.TRANSPARENT);
			}
		});
		view.findViewById(R.id.btn_joinsafe).setVisibility(View.GONE);
		Button btnClear = (Button)view.findViewById(R.id.btn_clear);
		btnClear.setOnClickListener(this);
		btnClear.setText("删除");
		btnClear.setTextColor(Color.RED);
		view.findViewById(R.id.btn_cancel).setOnClickListener(this);
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
	public void onStop() {
		super.onStop();
		accountInfo.privateMsgManger.hSubScript.t_friendrequest = accountInfo.t_friendsRequestList;
		RemoteManager.getInstance(MainApplication.getAppInstance()).CallService(accountInfo);
		CmmobiClickAgentWrapper.onStop(this);
	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.iv_back:
			this.finish();
			break;
		case R.id.btn_clear:
			if(pw_clear.isShowing()){
				pw_clear.dismiss();
			}
			Requester3.cleanRecommend(handler, currentUserid);
			ZDialog.show(R.layout.progressdialog, false, true, this);
			break;
		case R.id.btn_cancel:
			if(pw_clear.isShowing()){
				pw_clear.dismiss();
			}
			break;
		default:
			break;
		}
	}

	@Override
	public boolean handleMessage(Message msg) {

		switch (msg.what) {
		case Requester3.RESPONSE_TYPE_CLEAN_RECOMMEND:
			ZDialog.dismiss();
			GsonResponse3.cleanRecommendResponse cres = (GsonResponse3.cleanRecommendResponse) msg.obj;
			if(cres !=null && "0".equals(cres.status)){
				friendsRequestAdapter.removeUser(currentUserid);
				accountInfo.friendsRequestList.removeMemberByUserid(currentUserid);
			}else{
				Prompt.Alert("网络状况不佳，请稍后再试");
			}
		default:
			break;
		}

		return false;
	}
}
