package com.cmmobi.looklook.fragment;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.PopupWindow;

import com.cmmobi.looklook.R;
import com.cmmobi.looklook.activity.FriendsSessionPrivateMessageActivity;
import com.cmmobi.looklook.activity.FriendsStrangerActivity;
import com.cmmobi.looklook.activity.LookLookActivity;
import com.cmmobi.looklook.common.adapter.FriendsMessageAdapter;
import com.cmmobi.looklook.common.adapter.FriendsStrangerAdapter;
import com.cmmobi.looklook.common.gson.GsonResponse3.listMessageResponse;
import com.cmmobi.looklook.common.gson.Requester3;
import com.cmmobi.looklook.common.service.CoreService;
import com.cmmobi.looklook.common.storage.SqlMessageWrapperManager;
import com.cmmobi.looklook.info.profile.AccountInfo;
import com.cmmobi.looklook.info.profile.ActiveAccount;
import com.cmmobi.looklook.info.profile.MessageWrapper;
import com.cmmobi.looklook.info.profile.PrivateMessageManager;
/**
 * 私信
 */
public class FriendsMessageFragment extends TitleRootFragment implements  OnItemLongClickListener, OnItemClickListener{	

	
	private static final String TAG = "FriendsMessageFragment";

	private Activity mActivity;

	//PrivateMessageMainListView messageListView;
	ListView messageListView;
	FriendsMessageAdapter friendsMessageAdapter;
	ActiveAccount aa;
	AccountInfo ai;
	PrivateMessageManager privateMsgManger;
	
	private List<MessageWrapper> data = new ArrayList<MessageWrapper>();
	
	private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
		  @Override
		  public void onReceive(Context context, Intent intent) {
			    boolean hasStrangerMsg = false;
			    boolean hasFriendMsg = false;
			    
			    // Get extra data included in the Intent
				if(CoreService.ACTION_MESSAGE_DATA_UPDATE.equals(intent.getAction())){
					hasStrangerMsg = intent.getBooleanExtra("hasStrangerMsg", false);
					hasFriendMsg = intent.getBooleanExtra("hasFriendMsg", false);
					int type = intent.getIntExtra("type", -1);
					
					Message msg = new Message();
				    msg.what = type;
				    if(hasStrangerMsg || hasFriendMsg){
					    Log.d("receiver", "FriendsMessageFragment sendMessage");
				    	FriendsMessageFragment.this.handler.sendMessage(msg);
				    }
				}else if(LookLookActivity.CONTACTS_REFRESH_DATA.equals(intent.getAction()) 
						&& LookLookActivity.FRIEND_LIST_CHANGE.equals(intent.getExtras().get(LookLookActivity.CONTACTS_REFRESH_KEY))){			
					// 联系人变更
					doResume();
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
	public int subContentViewId() {
		return R.layout.fragment_friends_message;
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View mContentView = super.onCreateView(inflater, container, savedInstanceState);
		setTitle("消息");
		setLeftButtonText("清空");
		
		//messageListView = (PrivateMessageMainListView) mContentView.findViewById(R.id.friend_message_list);
		messageListView = (ListView) mContentView.findViewById(R.id.friend_message_list);
		
		
		aa = ActiveAccount.getInstance(getActivity());
		if(aa.isLogin()){
			ai = AccountInfo.getInstance(aa.getLookLookID());
			privateMsgManger = ai.privateMsgManger;
			checkStrangerToFriend(this.getActivity());
			data.clear();
			data.addAll(privateMsgManger.getAllMessageList());
			friendsMessageAdapter = new FriendsMessageAdapter(mActivity, handler, data, privateMsgManger.hasStrangerMsg(), privateMsgManger.getStrangerUnReadMsg());
			messageListView.setAdapter(friendsMessageAdapter);
			messageListView.setOnItemLongClickListener(this);
			messageListView.setOnItemClickListener(this);
			if(friendsMessageAdapter!=null){
				friendsMessageAdapter.notifyDataSetChanged();
			}
			privateMsgManger = ai.privateMsgManger;
//			Requester2.listMessage(handler, privateMsgManger.timemill, "20", "0", "", "","");
			Requester3.listMessage(handler, aa.getLookLookID(), privateMsgManger.timemill, "20", "1", "", "",
					privateMsgManger.hSubScript.t_zone_mic,
					privateMsgManger.hSubScript.t_friend,
					privateMsgManger.hSubScript.t_safebox_miccomment,
					privateMsgManger.hSubScript.t_friendrequest,
					privateMsgManger.hSubScript.t_friend_change,
					privateMsgManger.hSubScript.t_push,
					privateMsgManger.hSubScript.t_zone_miccomment
					);
		}
		
		return mContentView;
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_title_right:
			showMenu();
			break;
		case R.id.btn_title_left: // 清空
			
			if(friendsMessageAdapter.hasStranger()){
				privateMsgManger.cleanUpStrangers();
			}
			
			privateMsgManger.hideUpExceptStrangers();
			
			if(friendsMessageAdapter!=null){
				data.clear();
				data.addAll(privateMsgManger.getAllMessageList());
				friendsMessageAdapter.setListData(data, privateMsgManger.hasStrangerMsg(), privateMsgManger.getStrangerUnReadMsg());
				friendsMessageAdapter.notifyDataSetChanged();
			}
			
			Intent msgIntent = new Intent(LookLookActivity.UPDATE_MASK);
			LocalBroadcastManager.getInstance(this.getActivity()).sendBroadcast(msgIntent);
			
			break;
		case R.id.btn_comment_delete:
			MsgAlertDialogFragment dialog = MsgAlertDialogFragment.getMsgFDialog();
			if(dialog!=null){
				dialog.dismiss();
			}
			MessageWrapper item  = (MessageWrapper) v.getTag();
			handler.obtainMessage(FriendsStrangerAdapter.HANDLER_FLAG_MSG_DELETE, item).sendToTarget();
			break;
		default:
			break;
		}
	}
	
	@Override
	public void onResume(){
		super.onResume();
		heartBeatInterval = true;
		doResume();
	}
	
	@Override
	public void onHiddenChanged(boolean hidden) {
		super.onHiddenChanged(hidden);
		
		if(hidden){
			heartBeatInterval = false;
		}else{
			heartBeatInterval  =  true; //5 sec
			doResume();
		}
		
	}
	
	private void doResume() {
		aa = ActiveAccount.getInstance(getActivity());
		if(aa.isLogin()){
			
			// 存在可能当前fragment被回收了，但是onhiddenchanged会被调用。触发doresume
			if(messageListView == null){
				return;
			}
			ai = AccountInfo.getInstance(aa.getLookLookID());
			privateMsgManger = ai.privateMsgManger;
			checkStrangerToFriend(this.getActivity());
			data.clear();
			data.addAll(privateMsgManger.getAllMessageList());
			friendsMessageAdapter = new FriendsMessageAdapter(mActivity, handler, data, privateMsgManger.hasStrangerMsg(), privateMsgManger.getStrangerUnReadMsg());
			messageListView.setAdapter(friendsMessageAdapter);
			messageListView.setOnItemLongClickListener(this);
			messageListView.setOnItemClickListener(this);
			if(friendsMessageAdapter!=null){
				friendsMessageAdapter.notifyDataSetChanged();
			}			
		}
	}
	@Override
	public void onPause(){
		super.onPause();
		heartBeatInterval =  false; //30 sec
		
	}


	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			mActivity = activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString() + " FriendsMessageFragment ClassCastException");
		}
		
		LocalBroadcastManager.getInstance(mActivity).registerReceiver(mMessageReceiver,
			      new IntentFilter(CoreService.ACTION_MESSAGE_DATA_UPDATE));
		LocalBroadcastManager.getInstance(mActivity).registerReceiver(mMessageReceiver,
				new IntentFilter(LookLookActivity.CONTACTS_REFRESH_DATA));
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
//		case FriendsMessageAdapter.HANDLER_FLAG_PRIMSG_ACTIVITY:
//			Intent intent_activity = new Intent(mActivity, ActivitiesActivity.class);
//			mActivity.startActivity(intent_activity);
//			break;
//		case FriendsMessageAdapter.HANDLER_FLAG_PRIMSG_RECOMMAND:
//			Intent intent_recommand = new Intent(mActivity, FriendsRecommendActivity.class);
//			mActivity.startActivity(intent_recommand);
//			break;
//		case FriendsMessageAdapter.HANDLER_FLAG_PRIMSG_NEARBY:
//			Intent intent_nearby = new Intent(mActivity, FriendsNearByActivity.class);
//			mActivity.startActivity(intent_nearby);
//			break;
		case FriendsStrangerAdapter.HANDLER_FLAG_MSG_DELETE:
			if(aa.isLogin() && msg.obj!=null){
				MessageWrapper mw = (MessageWrapper) msg.obj;
				ai = AccountInfo.getInstance(aa.getLookLookID());
				PrivateMessageManager pmm = ai.privateMsgManger;
				if(pmm!=null /*&& pmm.messages!=null*/){
					/*pmm.messages.remove(mw.userid);*/
					pmm.hideMsg(mw.other_userid);
					if(friendsMessageAdapter!=null){
						friendsMessageAdapter.hideElement(mw.other_userid);
						data.clear();
						data.addAll(privateMsgManger.getAllMessageList());
						friendsMessageAdapter.setListData(data, privateMsgManger.hasStrangerMsg(), privateMsgManger.getStrangerUnReadMsg());
						friendsMessageAdapter.notifyDataSetChanged();	
					}
					Intent msgIntent = new Intent(LookLookActivity.UPDATE_MASK);
					LocalBroadcastManager.getInstance(this.getActivity()).sendBroadcast(msgIntent);
//					mActivity.setMessageCount(pmm.getUnReadNum());
				}

				
			}
			break;
		case Requester3.RESPONSE_TYPE_LIST_MESSAGE:
			listMessageResponse obj = (listMessageResponse) msg.obj;
			if(obj!=null && obj.users!=null && obj.users.length>0){
				aa = ActiveAccount.getInstance(mActivity);
				if(aa.isLogin()){
					ai = AccountInfo.getInstance(aa.getLookLookID());
					privateMsgManger = ai.privateMsgManger;
					
					for(int i=0; i<obj.users.length; i++){
						privateMsgManger.addMessage(obj.users[i]);
					}
					
					data.clear();
					data.addAll(privateMsgManger.getAllMessageList());
					friendsMessageAdapter.setListData(data, privateMsgManger.hasStrangerMsg(), privateMsgManger.getStrangerUnReadMsg());
					friendsMessageAdapter.notifyDataSetChanged();

				}
			}
			break;
		case CoreService.HANDLER_FLAG_MESSAGE_DATA_UPDATE:
			data.clear();
			data.addAll(privateMsgManger.getAllMessageList());
			friendsMessageAdapter.setListData(data, privateMsgManger.hasStrangerMsg(), privateMsgManger.getStrangerUnReadMsg());
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
		
		if(friendsMessageAdapter.hasStranger() && position ==0){
			return false;
		}
		MessageWrapper item = (MessageWrapper) parent.getAdapter().getItem(position);
		
		showDelPrimMenu(item);
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
						handler.obtainMessage(FriendsMessageAdapter.HANDLER_FLAG_PRIMSG_USER, item.other_userid).sendToTarget();
					}else if("2".equals(item.act)){
						item.consumeUnreadMsgs();
						handler.obtainMessage(FriendsMessageAdapter.HANDLER_FLAG_PRIMSG_ACTIVITY, item.other_userid).sendToTarget();
					}else if("3".equals(item.act)){
						item.consumeUnreadMsgs();
						handler.obtainMessage(FriendsMessageAdapter.HANDLER_FLAG_PRIMSG_RECOMMAND, item.other_userid).sendToTarget();
					}else if("4".equals(item.act)){
						item.consumeUnreadMsgs();
						handler.obtainMessage(FriendsMessageAdapter.HANDLER_FLAG_PRIMSG_NEARBY, item.other_userid).sendToTarget();
					}else{
						handler.obtainMessage(FriendsMessageAdapter.HANDLER_FLAG_PRIMSG_USER, item.other_userid).sendToTarget();
					}
					
				}
				
			}else{
				//handler.obtainMessage(FriendsMessageAdapter.HANDLER_FLAG_PRIMSG_USER, item.userid).sendToTarget();
				if("1".equals(item.act) || "6".equals(item.act)){
					handler.obtainMessage(FriendsMessageAdapter.HANDLER_FLAG_PRIMSG_USER, item.other_userid).sendToTarget();
				}else if("2".equals(item.act)){
					item.consumeUnreadMsgs();
					handler.obtainMessage(FriendsMessageAdapter.HANDLER_FLAG_PRIMSG_ACTIVITY, item.other_userid).sendToTarget();
				}else if("3".equals(item.act)){
					item.consumeUnreadMsgs();
					handler.obtainMessage(FriendsMessageAdapter.HANDLER_FLAG_PRIMSG_RECOMMAND, item.other_userid).sendToTarget();
				}else if("4".equals(item.act)){
					item.consumeUnreadMsgs();
					handler.obtainMessage(FriendsMessageAdapter.HANDLER_FLAG_PRIMSG_NEARBY, item.other_userid).sendToTarget();
				}else{
					handler.obtainMessage(FriendsMessageAdapter.HANDLER_FLAG_PRIMSG_USER, item.other_userid).sendToTarget();
				}
			}
		}
		
		
	}

	
	/**
	 * 检查墨陌生人是否加入到好友中
	 * 如果加入则更新状态
	 */
	private void checkStrangerToFriend(Context ctx){
		ArrayList<String> strangers = privateMsgManger.getStrangerUserid();
		for(int i=0;i<strangers.size();i++){
			String userid = strangers.get(i);
			privateMsgManger.checkStrangerNeed2Friend(ctx,userid);
		}
	}
	
	
	private void showDelPrimMenu(MessageWrapper item) {
		DialogFragment newFragment = MsgAlertDialogFragment.newInstance(this,item);
		newFragment.show(this.getFragmentManager(), "dialog");
	}
	
	public static class MsgAlertDialogFragment extends DialogFragment {

		private OnClickListener listener;
		private MessageWrapper item;
		private static MsgAlertDialogFragment frag;
		public static MsgAlertDialogFragment newInstance(OnClickListener listener,MessageWrapper item){
			if(frag == null){
				frag = new MsgAlertDialogFragment();
			}
			frag.listener = listener;
			frag.item = item;
			return frag;
		}
        public static MsgAlertDialogFragment getMsgFDialog(){
        	if(frag == null){
        		return null;
        	}
        	return frag;
        }
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
        	
        	final Dialog d = new Dialog(getActivity(), R.style.dialog_theme); 
        	
        	LayoutInflater inflater = LayoutInflater.from(getActivity());
        	View v = null;
    		v = inflater.inflate(R.layout.dialogfragment_comment_operate_two, null);
    		Button delete = (Button) v.findViewById(R.id.btn_comment_delete);
    		delete.setTag(item);
    		delete.setOnClickListener(listener);
    		Button cancel = (Button) v.findViewById(R.id.btn_comment_cancel);
    		cancel.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View v){
					d.dismiss();
				}
			});
        	
        	LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT); 
        	d.addContentView(v, params);
        	
    		Window window = d.getWindow();
    		window.setGravity(Gravity.BOTTOM);
    		android.view.WindowManager.LayoutParams p = window.getAttributes();
			p.width = LayoutParams.MATCH_PARENT; // 设置屏幕宽度
//			p.alpha = 0.9f; // 增加一点按钮透明
			window.setAttributes(p);      //设置生效
    		return d;
        }
    }
	
	
}
