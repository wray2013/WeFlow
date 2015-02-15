package com.cmmobi.looklook.activity;

import java.util.ArrayList;
import java.util.List;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import cn.zipper.framwork.core.ZActivity;

import com.cmmobi.looklook.R;
import com.cmmobi.looklook.common.adapter.FriendsSessionPrivateMessageAdapter;
import com.cmmobi.looklook.common.adapter.FriendsStrangerAdapter;
import com.cmmobi.looklook.common.constant.Constant.SEND_MSG_STATUS;
import com.cmmobi.looklook.common.gson.Requester2;
import com.cmmobi.looklook.common.service.PrivateMessageService;
import com.cmmobi.looklook.common.utils.CmmobiClickAgentWrapper;
import com.cmmobi.looklook.common.utils.UmengclickAgentWrapper;
import com.cmmobi.looklook.info.profile.AccountInfo;
import com.cmmobi.looklook.info.profile.ActiveAccount;
import com.cmmobi.looklook.info.profile.MessageWrapper;
import com.cmmobi.looklook.info.profile.PrivateCommonMessage;
import com.cmmobi.looklook.info.profile.PrivateMessageManager;

public class FriendsStrangerActivity extends ZActivity implements OnItemLongClickListener, OnItemClickListener{

	private static final String TAG = null;
	//private PrivateMessageStrangerListView listView;
	private ListView listView;
	FriendsStrangerAdapter friendsStrangerAdapter;
	
	private ActiveAccount aa;
	private AccountInfo ai;
	private PrivateMessageManager privateMsgManger;
	
	private ImageButton contacts_back_btn;
	private ImageButton contacts_remove_btn;
	
	PopupWindow popupWindow;
	
	private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
		  @Override
		  public void onReceive(Context context, Intent intent) {
			    boolean hasStrangerMsg = false;
			    boolean hasFriendMsg = false;
				int type;
			    Message msg = new Message();
			    
			    // Get extra data included in the Intent
				if(PrivateMessageService.FLAG_MESSAGE_DATA_UPDATE.equals(intent.getAction())){
					hasStrangerMsg = intent.getBooleanExtra("hasStrangerMsg", false);
					hasFriendMsg = intent.getBooleanExtra("hasFriendMsg", false);
					type = intent.getIntExtra("type", -1);
					
				    msg.what = type;
				    if(hasStrangerMsg || hasFriendMsg){
					    Log.d("receiver", "FriendsStrangerActivity sendMessage");
					    FriendsStrangerActivity.this.handler.sendMessage(msg);
				    }
				}

				
		  }
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_friends_stranger);
		
		
		
		//listView = (PrivateMessageStrangerListView) findViewById(R.id.stranger_message_list);
		listView = (ListView) findViewById(R.id.stranger_message_list);
		listView.setOnItemLongClickListener(this);
		//listView.setParams(handler);
		contacts_back_btn = (ImageButton) findViewById(R.id.contacts_back_btn);
		contacts_remove_btn = (ImageButton) findViewById(R.id.contacts_remove_btn);
		
		aa = ActiveAccount.getInstance(this);
		if(aa.isLogin()){
			ai = AccountInfo.getInstance(aa.getLookLookID());
			privateMsgManger = ai.privateMsgManger;
			List<MessageWrapper> items = privateMsgManger.getListForStranger();
			friendsStrangerAdapter = new FriendsStrangerAdapter(this, handler, items);
			listView.setAdapter(friendsStrangerAdapter);
			listView.setOnItemLongClickListener(this);
			listView.setOnItemClickListener(this);
		}
		
		contacts_back_btn.setOnClickListener(this);
		contacts_remove_btn.setOnClickListener(this);
		
		LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
			      new IntentFilter(PrivateMessageService.FLAG_MESSAGE_DATA_UPDATE));

	}
	
	@Override
	public void onResume() {
		super.onResume();
		UmengclickAgentWrapper.onResume(this);
		CmmobiClickAgentWrapper.onResume(this);
		
		if(friendsStrangerAdapter!=null){
			friendsStrangerAdapter.notifyDataSetChanged();
		}
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
	public boolean handleMessage(Message msg) {
		// TODO Auto-generated method stub
		switch(msg.what){
		case FriendsStrangerAdapter.HANDLER_FLAG_PRIMSG_USER:
			if (msg.obj != null) {
				startActivity((new Intent(this,
						FriendsSessionPrivateMessageActivity.class)).putExtra(
						"userid", (String) (msg.obj)));
			}
			break;
		case PrivateMessageService.HANDLER_FLAG_MESSAGE_DATA_UPDATE:
			List<MessageWrapper> items = privateMsgManger.getListForStranger();
			if(friendsStrangerAdapter!=null){
				friendsStrangerAdapter.setlistData(items);
				friendsStrangerAdapter.notifyDataSetChanged();
			}

			break;
		case FriendsStrangerAdapter.HANDLER_FLAG_MSG_DELETE:
			if(aa.isLogin() && msg.obj!=null){
				MessageWrapper mw = (MessageWrapper) msg.obj;
				ai = AccountInfo.getInstance(aa.getLookLookID());
				PrivateMessageManager pmm = ai.privateMsgManger;
				if(pmm!=null && pmm.messages!=null){
					pmm.messages.remove(mw.userid);
					if(friendsStrangerAdapter!=null){
						friendsStrangerAdapter.removeElement(mw.userid);
						friendsStrangerAdapter.notifyDataSetChanged();	
					}

				}
				
			}
			break;
		}
		return false;
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()){
		case R.id.contacts_back_btn:
			finish();
			break;
		case R.id.contacts_remove_btn:
			//88.	清除陌生人消息
			//Requester2.clearStrangerMessage(getHandler());
			privateMsgManger.cleanUpStrangers();
			friendsStrangerAdapter.setlistData(new  ArrayList<MessageWrapper>());
			friendsStrangerAdapter.notifyDataSetChanged();
			break;
		}
		
	}
	
	@Override
	public void onDestroy(){
		super.onDestroy();
		LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
		
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View v,
			int position, long id) {
		// TODO Auto-generated method stub
		Log.e(TAG, "onItemLongClick position:" + position);
		//del_pos = position - 1;
		//if(del_pos<0){
		//	return false;
		//}
		final MessageWrapper item = (MessageWrapper) parent.getAdapter().getItem(position);
		View view = LayoutInflater.from(this).inflate(
				R.layout.pup_list_item_friends_private_message,
				null);
		//view.findViewById(R.id.pop_item_friends).setOnClickListener(this);
		
		popupWindow = new PopupWindow(view,
					LayoutParams.WRAP_CONTENT,
					LayoutParams.WRAP_CONTENT){
				
			};
		popupWindow.setBackgroundDrawable(new BitmapDrawable());
		popupWindow.setFocusable(true);
		popupWindow.setTouchable(true);
		popupWindow.setOutsideTouchable(true);

		// popupWindow.showAsDropDown(viewHolder.vedioRelativeLayout,
		// -40, 0);
		int[] location = new int[2];
		RelativeLayout rl_stranger = (RelativeLayout) v.findViewById(R.id.rl_stranger);
		rl_stranger.getLocationInWindow(location);

		popupWindow.showAtLocation(rl_stranger, 0, location[0]  + 100 , location[1] - 80);

		view.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				//Toast.makeText(FriendsSessionPrivateMessageActivity.this, "del it position：" + del_pos, Toast.LENGTH_SHORT).show();
				handler.obtainMessage(FriendsStrangerAdapter.HANDLER_FLAG_MSG_DELETE, item).sendToTarget();
				popupWindow.dismiss();
			}
			
		});
		
		return false;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		// TODO Auto-generated method stub
		final MessageWrapper item = (MessageWrapper) parent.getAdapter().getItem(position);
		handler.obtainMessage(FriendsStrangerAdapter.HANDLER_FLAG_PRIMSG_USER, item.userid).sendToTarget();
	
	}

}
