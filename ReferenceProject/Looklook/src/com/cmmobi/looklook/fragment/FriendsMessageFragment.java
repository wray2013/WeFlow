package com.cmmobi.looklook.fragment;

import java.util.List;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;

import com.cmmobi.looklook.MainApplication;
import com.cmmobi.looklook.R;
import com.cmmobi.looklook.activity.ActivitiesActivity;
import com.cmmobi.looklook.activity.FriendsActivity;
import com.cmmobi.looklook.activity.FriendsNearByActivity;
import com.cmmobi.looklook.activity.FriendsRecommendActivity;
import com.cmmobi.looklook.activity.FriendsSessionPrivateMessageActivity;
import com.cmmobi.looklook.activity.FriendsStrangerActivity;
import com.cmmobi.looklook.common.adapter.FriendsMessageAdapter;
import com.cmmobi.looklook.common.adapter.FriendsStrangerAdapter;
import com.cmmobi.looklook.common.gson.GsonResponse2.listMessageResponse;
import com.cmmobi.looklook.common.gson.Requester2;
import com.cmmobi.looklook.common.service.PrivateMessageService;
import com.cmmobi.looklook.info.profile.AccountInfo;
import com.cmmobi.looklook.info.profile.ActiveAccount;
import com.cmmobi.looklook.info.profile.MessageWrapper;
import com.cmmobi.looklook.info.profile.PrivateMessageManager;

public class FriendsMessageFragment extends Fragment implements Callback, OnItemLongClickListener, OnItemClickListener{	

	
	private static final String TAG = "FriendsMessageFragment";

	protected View mContentView;
	
	private FriendsActivity mActivity;

	//PrivateMessageMainListView messageListView;
	ListView messageListView;
	FriendsMessageAdapter friendsMessageAdapter;
	ActiveAccount aa;
	AccountInfo ai;
	PrivateMessageManager privateMsgManger;
	
	Handler handler;
	
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
					    Log.d("receiver", "FriendsMessageFragment sendMessage");
				    	FriendsMessageFragment.this.handler.sendMessage(msg);
				    }
				}

				
		  }
	};

	private PopupWindow popupWindow;

	public static boolean heartBeatInterval = false;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		mContentView = inflater.inflate(R.layout.fragment_friends_message, null);

		//messageListView = (PrivateMessageMainListView) mContentView.findViewById(R.id.friend_message_list);
		messageListView = (ListView) mContentView.findViewById(R.id.friend_message_list);
		
		
		aa = ActiveAccount.getInstance(getActivity());
		if(aa.isLogin()){
			ai = AccountInfo.getInstance(aa.getLookLookID());
			privateMsgManger = ai.privateMsgManger;
			List<MessageWrapper> items = privateMsgManger.getListExceptStranger();
			friendsMessageAdapter = new FriendsMessageAdapter(mActivity, handler, items, privateMsgManger.hasStrangerMsg(), privateMsgManger.getStrangerUnReadMsg());
			messageListView.setAdapter(friendsMessageAdapter);
			messageListView.setOnItemLongClickListener(this);
			messageListView.setOnItemClickListener(this);
			if(friendsMessageAdapter!=null){
				friendsMessageAdapter.notifyDataSetChanged();
			}
			privateMsgManger = ai.privateMsgManger;
			Requester2.listMessage(handler, privateMsgManger.timemill, "20", "0", "", "","");
		}

		
		return mContentView;
	}
	
	@Override
	public void onResume(){
		super.onResume();
		aa = ActiveAccount.getInstance(getActivity());
		if(aa.isLogin()){
			ai = AccountInfo.getInstance(aa.getLookLookID());
			privateMsgManger = ai.privateMsgManger;
			List<MessageWrapper> items = privateMsgManger.getListExceptStranger();
			friendsMessageAdapter = new FriendsMessageAdapter(mActivity, handler, items, privateMsgManger.hasStrangerMsg(), privateMsgManger.getStrangerUnReadMsg());
			messageListView.setAdapter(friendsMessageAdapter);
			messageListView.setOnItemLongClickListener(this);
			messageListView.setOnItemClickListener(this);
			if(friendsMessageAdapter!=null){
				friendsMessageAdapter.notifyDataSetChanged();
			}			
		}
		
		heartBeatInterval  =  true; //5 sec

	}
	
	@Override
	public void onPause(){
		super.onPause();
		heartBeatInterval =  false; //30 sec
		
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			mActivity = (FriendsActivity) activity;
			handler = new Handler(this);
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString() + " FriendsMessageFragment ClassCastException");
		}
		
		LocalBroadcastManager.getInstance(mActivity).registerReceiver(mMessageReceiver,
			      new IntentFilter(PrivateMessageService.FLAG_MESSAGE_DATA_UPDATE));
	}
	
	@Override
	public void onDetach(){
		LocalBroadcastManager.getInstance(mActivity).unregisterReceiver(mMessageReceiver);
		
		super.onDetach();
	}

	@Override
	public boolean handleMessage(Message msg) {
		// TODO Auto-generated method stub
		switch(msg.what){
		case FriendsMessageAdapter.HANDLER_FLAG_PRIMSG_STRANGER:
			startActivity(new Intent(mActivity, FriendsStrangerActivity.class));
			break;
		case FriendsMessageAdapter.HANDLER_FLAG_PRIMSG_USER:
			if (msg.obj != null) {
				String userid = (String) (msg.obj);
				startActivity((new Intent(mActivity, FriendsSessionPrivateMessageActivity.class)).putExtra("userid", userid));
				/*				
				AccountInfo ai = AccountInfo.getInstance(ActiveAccount.getInstance(MainApplication.getAppInstance()).getLookLookID());
				if(ai.officialUseridsManager!=null && ai.officialUseridsManager.ContainUser(userid)){
					
			    }else{
			    	
			    }*/
				
			}
			break;
		case FriendsMessageAdapter.HANDLER_FLAG_PRIMSG_ACTIVITY:
			Intent intent_activity = new Intent(mActivity, ActivitiesActivity.class);
			mActivity.startActivity(intent_activity);
			break;
		case FriendsMessageAdapter.HANDLER_FLAG_PRIMSG_RECOMMAND:
			Intent intent_recommand = new Intent(mActivity, FriendsRecommendActivity.class);
			mActivity.startActivity(intent_recommand);
			break;
		case FriendsMessageAdapter.HANDLER_FLAG_PRIMSG_NEARBY:
			Intent intent_nearby = new Intent(mActivity, FriendsNearByActivity.class);
			mActivity.startActivity(intent_nearby);
			break;
		case FriendsStrangerAdapter.HANDLER_FLAG_MSG_DELETE:
			if(aa.isLogin() && msg.obj!=null){
				MessageWrapper mw = (MessageWrapper) msg.obj;
				ai = AccountInfo.getInstance(aa.getLookLookID());
				PrivateMessageManager pmm = ai.privateMsgManger;
				if(pmm!=null && pmm.messages!=null){
					pmm.messages.remove(mw.userid);
					if(friendsMessageAdapter!=null){
						friendsMessageAdapter.removeElement(mw.userid);
						friendsMessageAdapter.notifyDataSetChanged();	
					}
					mActivity.setMessageCount(pmm.getUnReadNum());
				}

				
			}
			break;
		case Requester2.RESPONSE_TYPE_LIST_MESSAGE:
			listMessageResponse obj = (listMessageResponse) msg.obj;
			if(obj!=null && obj.users!=null && obj.users.length>0){
				aa = ActiveAccount.getInstance(mActivity);
				if(aa.isLogin()){
					ai = AccountInfo.getInstance(aa.getLookLookID());
					privateMsgManger = ai.privateMsgManger;
					
					for(int i=0; i<obj.users.length; i++){
						privateMsgManger.addMessage(obj.users[i]);
					}
					
					List<MessageWrapper> items = privateMsgManger.getListExceptStranger();
					friendsMessageAdapter.setListData(items, privateMsgManger.hasStrangerMsg(), privateMsgManger.getStrangerUnReadMsg());
					friendsMessageAdapter.notifyDataSetChanged();

				}
			}
			break;
		case PrivateMessageService.HANDLER_FLAG_MESSAGE_DATA_UPDATE:
			List<MessageWrapper> items = privateMsgManger.getListExceptStranger();
			friendsMessageAdapter.setListData(items, privateMsgManger.hasStrangerMsg(), privateMsgManger.getStrangerUnReadMsg());
			friendsMessageAdapter.notifyDataSetChanged();
			break;
		}
		return false;
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
		if(item==null){
			return false;
		}
		
		View view = LayoutInflater.from(mActivity).inflate(
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

		MessageWrapper item = (MessageWrapper) parent.getAdapter().getItem(position);
		if(item!= null){
			Log.e(TAG, "onItemClick position:" + position + ", act: " + item.act);
		}

		
		if(friendsMessageAdapter!=null){
			if(friendsMessageAdapter.hasStranger()){
				if(position==0){
					handler.obtainMessage(FriendsMessageAdapter.HANDLER_FLAG_PRIMSG_STRANGER).sendToTarget();
				}else{
					//1私信，2活动，3推荐，4附近，5陌生人6LOOKLOOK官方
					if("1".equals(item.act) || "6".equals(item.act)){
						handler.obtainMessage(FriendsMessageAdapter.HANDLER_FLAG_PRIMSG_USER, item.userid).sendToTarget();
					}else if("2".equals(item.act)){
						item.consumeUnreadMsgs();
						handler.obtainMessage(FriendsMessageAdapter.HANDLER_FLAG_PRIMSG_ACTIVITY, item.userid).sendToTarget();
					}else if("3".equals(item.act)){
						item.consumeUnreadMsgs();
						handler.obtainMessage(FriendsMessageAdapter.HANDLER_FLAG_PRIMSG_RECOMMAND, item.userid).sendToTarget();
					}else if("4".equals(item.act)){
						item.consumeUnreadMsgs();
						handler.obtainMessage(FriendsMessageAdapter.HANDLER_FLAG_PRIMSG_NEARBY, item.userid).sendToTarget();
					}else{
						handler.obtainMessage(FriendsMessageAdapter.HANDLER_FLAG_PRIMSG_USER, item.userid).sendToTarget();
					}
					
				}
				
			}else{
				//handler.obtainMessage(FriendsMessageAdapter.HANDLER_FLAG_PRIMSG_USER, item.userid).sendToTarget();
				if("1".equals(item.act) || "6".equals(item.act)){
					handler.obtainMessage(FriendsMessageAdapter.HANDLER_FLAG_PRIMSG_USER, item.userid).sendToTarget();
				}else if("2".equals(item.act)){
					item.consumeUnreadMsgs();
					handler.obtainMessage(FriendsMessageAdapter.HANDLER_FLAG_PRIMSG_ACTIVITY, item.userid).sendToTarget();
				}else if("3".equals(item.act)){
					item.consumeUnreadMsgs();
					handler.obtainMessage(FriendsMessageAdapter.HANDLER_FLAG_PRIMSG_RECOMMAND, item.userid).sendToTarget();
				}else if("4".equals(item.act)){
					item.consumeUnreadMsgs();
					handler.obtainMessage(FriendsMessageAdapter.HANDLER_FLAG_PRIMSG_NEARBY, item.userid).sendToTarget();
				}else{
					handler.obtainMessage(FriendsMessageAdapter.HANDLER_FLAG_PRIMSG_USER, item.userid).sendToTarget();
				}
			}
		}
		
		
	}
	
}
