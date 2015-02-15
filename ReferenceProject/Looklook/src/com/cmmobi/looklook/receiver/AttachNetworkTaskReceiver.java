package com.cmmobi.looklook.receiver;

import java.util.Iterator;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.cmmobi.looklook.common.constant.Constant.SEND_MSG_STATUS;
import com.cmmobi.looklook.info.profile.AccountInfo;
import com.cmmobi.looklook.info.profile.ActiveAccount;
import com.cmmobi.looklook.info.profile.MessageWrapper;
import com.cmmobi.looklook.info.profile.PrivateCommonMessage;
import com.cmmobi.looklook.info.profile.PrivateMessageManager;
import com.cmmobi.looklook.networktask.INetworkTask;

public class AttachNetworkTaskReceiver extends BroadcastReceiver {
	private static final String TAG = "AttachNetworkTaskReceiver";
	
	public static final String ACTION_ATTACH_TASK_UPDATE = "ACTION_ATTACH_TASK_UPDATE";
	public static final int ATTACH_TASK_TYPE = 0x7296193;
	
	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		String action = intent.getAction();
		if (INetworkTask.ACTION_TASK_STATE_CHANGE.equals(action)) {
			Log.d(TAG, "ACTION_TASK_STATE_CHANGE");
			/**
			 * 	public static final int STATE_COMPELETED = 5;
			 *  public static final int STATE_REMOVED = 6;
			 * */
			int state  = intent.getIntExtra("taskState", INetworkTask.STATE_WAITING);
			String attachid = intent.getStringExtra("attachid");
			String userid = intent.getStringExtra("taskUID");
			
			if(state!=INetworkTask.STATE_COMPELETED && state!=INetworkTask.STATE_ERROR){
				return;
			}
			
			ActiveAccount aa = ActiveAccount.getInstance(context);
			AccountInfo ai = AccountInfo.getInstance(aa.getLookLookID());
			PrivateMessageManager pmm = ai.privateMsgManger;
			if(pmm!=null && pmm.messages!=null){
				
				MessageWrapper mw = pmm.messages.get(userid);
				if(mw!=null && mw.msgs!=null){
					Iterator<PrivateCommonMessage> iterator = mw.msgs.iterator();
					while(iterator.hasNext()){
						PrivateCommonMessage pcm = iterator.next();
						if(pcm.getUUID()!=null){
							if(pcm.getUUID().equals(attachid)){
								if(state==INetworkTask.STATE_COMPELETED){
									pcm.updateSendMsgStatus(SEND_MSG_STATUS.ALL_DONE);
								}else if(state==INetworkTask.STATE_ERROR){
									pcm.updateSendMsgStatus(SEND_MSG_STATUS.UPLOAD_FAIL);
								}
								
							}
						}
							
					}
						
					//send broadcast to activity
					Intent intent_to_send = new Intent(ACTION_ATTACH_TASK_UPDATE);
					intent.putExtra("type", ATTACH_TASK_TYPE);
					// You can also include some extra data.
					//intent_to_send.putExtra("message", "This is my message!");
					Log.e(TAG, "onReceive send ACTION_ATTACH_TASK_UPDATE" );
					LocalBroadcastManager.getInstance(context).sendBroadcast(intent_to_send);
				}

				
			}
		} 

	}

}
