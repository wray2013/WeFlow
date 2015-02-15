package com.cmmobi.looklook.activity;

import java.util.ArrayList;
import java.util.List;

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
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.PopupWindow;

import com.cmmobi.looklook.R;
import com.cmmobi.looklook.common.adapter.FriendsStrangerAdapter;
import com.cmmobi.looklook.common.service.CoreService;
import com.cmmobi.looklook.info.profile.AccountInfo;
import com.cmmobi.looklook.info.profile.ActiveAccount;
import com.cmmobi.looklook.info.profile.MessageWrapper;
import com.cmmobi.looklook.info.profile.PrivateMessageManager;

public class FriendsStrangerActivity extends TitleRootFragmentActivity implements OnItemLongClickListener, OnItemClickListener{

	private static final String TAG = null;
	//private PrivateMessageStrangerListView listView;
	private ListView listView;
	FriendsStrangerAdapter friendsStrangerAdapter;
	
	private ActiveAccount aa;
	private AccountInfo ai;
	private PrivateMessageManager privateMsgManger;
	
	PopupWindow popupWindow;
	
	private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
		  @Override
		  public void onReceive(Context context, Intent intent) {
			    boolean hasStrangerMsg = false;
			    boolean hasFriendMsg = false;
				int type;
			    Message msg = new Message();
			    
			    // Get extra data included in the Intent
				if(CoreService.ACTION_MESSAGE_DATA_UPDATE.equals(intent.getAction())){
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
	public int subContentViewId() {
		return R.layout.activity_friends_stranger;
	}


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setTitle("陌生人");
		setRightButtonText("清空");
		getRightButton().setTextColor(getResources().getColor(R.color.title_left_orange));
		setLeftLong2Home();
		
		//listView = (PrivateMessageStrangerListView) findViewById(R.id.stranger_message_list);
		listView = (ListView) findViewById(R.id.stranger_message_list);
		listView.setOnItemLongClickListener(this);
		//listView.setParams(handler);
		
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
		
		LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
			      new IntentFilter(CoreService.ACTION_MESSAGE_DATA_UPDATE));

	}
	
	@Override
	public void onResume() {
		super.onResume();
		
		if(friendsStrangerAdapter!=null){
			friendsStrangerAdapter.notifyDataSetChanged();
		}
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
		case CoreService.HANDLER_FLAG_MESSAGE_DATA_UPDATE:
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
				if(pmm!=null){
					pmm.remove(mw.other_userid);
					if(friendsStrangerAdapter!=null){
						friendsStrangerAdapter.removeElement(mw.other_userid);
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
		case R.id.btn_title_left:
			finish();
			break;
		case R.id.btn_comment_delete:
			MsgAlertDialogFragment dialog = MsgAlertDialogFragment.getMsgFDialog();
			if(dialog!=null){
				dialog.dismiss();
			}
			MessageWrapper item  = (MessageWrapper) v.getTag();
			handler.obtainMessage(FriendsStrangerAdapter.HANDLER_FLAG_MSG_DELETE, item).sendToTarget();
			break;
		case R.id.btn_title_right:
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
		MessageWrapper item = (MessageWrapper) parent.getAdapter().getItem(position);
		/*View view = LayoutInflater.from(this).inflate(
				R.layout.del_pup_list_item_friends_private_message,
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
			
		});*/
		
		showDelPrimMenu(item);
		
		return false;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		// TODO Auto-generated method stub
		final MessageWrapper item = (MessageWrapper) parent.getAdapter().getItem(position);
		handler.obtainMessage(FriendsStrangerAdapter.HANDLER_FLAG_PRIMSG_USER, item.other_userid).sendToTarget();
	
	}
	
	
	
	private void showDelPrimMenu(MessageWrapper item) {
		DialogFragment newFragment = MsgAlertDialogFragment.newInstance(this,item);
		newFragment.show(this.getSupportFragmentManager(), "dialog");
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
