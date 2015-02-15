package com.cmmobi.looklook.common.service;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import com.baidu.platform.comapi.map.r;
import com.cmmobi.looklook.MainApplication;
import com.cmmobi.looklook.common.gson.GsonResponse3.HeartPush;
import com.cmmobi.looklook.common.gson.GsonResponse3.MessageUser;
import com.cmmobi.looklook.common.service.aidl.Main2ServiceObj;
import com.cmmobi.looklook.common.service.aidl.MainCallBack;
import com.cmmobi.looklook.common.service.aidl.MainUpdate;
import com.cmmobi.looklook.common.service.aidl.Service2Main2Obj;
import com.cmmobi.looklook.common.service.aidl.Service2MainObj;
import com.cmmobi.looklook.info.profile.AccountInfo;
import com.cmmobi.looklook.info.profile.ActiveAccount;
import com.cmmobi.looklook.info.profile.CommonInfo;
import com.cmmobi.looklook.info.profile.PrivateMessageManager;
import com.cmmobi.looklook.info.profile.TimeHelper;
import com.cmmobi.looklook.receiver.CmmobiPushReceiver;
import com.google.gson.Gson;

public class RemoteManager {
	protected static final String TAG = "RemoteManager";
	private Context context;
	private static RemoteManager ins;
	
	private MainUpdate mService;
	private ServiceConnection mConnection = new ServiceConnection() {
		public void onServiceConnected(ComponentName className, IBinder service) {
			Log.v(TAG, "connect service");
			//Toast.makeText(context, "connect service ok", Toast.LENGTH_SHORT).show();
			mService = MainUpdate.Stub.asInterface(service);
			try {
				mService.registerUpdateCall(mCallback);
			} catch (RemoteException e) {

			}
		}


		public void onServiceDisconnected(ComponentName className) {
			Log.v(TAG, "disconnect service");
			Toast.makeText(context, "disconnect service", Toast.LENGTH_SHORT).show();
			mService = null;
		}
	};
	
	private MainCallBack mCallback = new MainCallBack.Stub() {

		@Override
		public void UpdateCallBack(Service2MainObj obj) throws RemoteException {
			// TODO Auto-generated method stub
			if(obj!=null && obj.userid!=null){
				ActiveAccount aa = ActiveAccount.getInstance(context);
				if(aa.isLogin() && aa.getLookLookID().equals(obj.userid)){
					AccountInfo ai = AccountInfo.getInstance(obj.userid);
					PrivateMessageManager pmm = ai.privateMsgManger;
					//pmm.u
					if(obj.servertime!=0){
						Log.e(TAG, "TimeHelper - RemoteManager:" + obj.servertime );
						TimeHelper.getInstance().syncServerTime(obj.servertime);
						Intent intent = new Intent(CoreService.BRODCAST_SYNC_TIME_DONE);
						context.sendBroadcast(intent);			
					}
					
					if(obj.mus!=null && obj.mus.length>0){
						for(int i=0; i<obj.mus.length; i++){
							if(obj.mus[i]!=null){
								pmm.addMessage(obj.mus[i]);
							}
						}
		            	obj.hasFriendMsg = true;
					}
					Log.e(TAG, "====readedMessages == " + obj.readedMessages);
					Log.e(TAG, "====readedMessages ==unreadnum 1 " + pmm.getUnReadNum());
					pmm.updateReadedMessageById(obj.readedMessages);
					Log.e(TAG, "====readedMessages ==unreadnum 2 " + pmm.getUnReadNum());
		
					
					pmm.heartUpdateCommentnum(obj.commentnum);
					pmm.heartUpdateSafeboxCommentnum(obj.commentnum_safebox);
					
					pmm.timemill = obj.last_timemilli;
					
					pmm.hSubScript.t_push = obj.t_push;
					
					pmm.commentNum = obj.commentnum;
					pmm.commentnum_safebox = obj.commentnum_safebox;
					
					pmm.hSubScript.new_zonemicnum = obj.new_zonemicnum;
					pmm.hSubScript.new_safeboxmicnum = obj.new_safeboxmicnum;
					pmm.hSubScript.new_friend_change = obj.new_friend_change;
					pmm.hSubScript.new_friend = obj.new_friend;
					pmm.hSubScript.new_requestnum = obj.new_requestnum;
					pmm.hSubScript.friendnum = obj.friendnum;
					
		        	//4. new_friend_change
		        	try {
		        		int c = 0;
						if(obj.new_friend_change!=null){
							c = Integer.parseInt(obj.new_friend_change);
							if(c >= 0){
								ai.newFriendChange = c;
								System.out.println("==fan=== new_friend_change =====" + c);
							}
						}
						
		        	}catch(Exception e){
		        		e.printStackTrace();
					}
		        	
		        	//5. new_friend
		        	try {
		        		int c = 0;
						if(obj.new_friend!=null){
							c = Integer.parseInt(obj.new_friend);
							if(c >= 0){
								ai.newFriend = c;
								System.out.println("==fan=== new_friend =====" + c);
							}
						}
						
		        	}catch(Exception e){
		        		e.printStackTrace();
					}
		        	
		        	//6. new_requestnum  
		        	try {
		        		int c = 0;
						if(obj.new_requestnum!=null){
							c = Integer.parseInt(obj.new_requestnum);
							if(c >= 0){
								ai.newFriendRequestCount = c;
								System.out.println("==fan=== new_requestnum =====" + c);
							}
						}
						
		        	}catch(Exception e){
		        		e.printStackTrace();
					}
		        	
		        	try {
		        		int c = 0;
		    			
		    			c = 0;
		    			if(obj.new_zonemicnum!=null){
		    				c = Integer.parseInt(obj.new_zonemicnum);
		    				if(c >= 0){
		    					ai.newZoneMicCount = c + ai.newZoneMicCount;
		    					System.out.println("===== newZoneMicCount =====" + c + ", " + ai.newZoneMicCount);
		    				}
		    			}
		    			
		    			c = 0;
		    			if(obj.new_safeboxmicnum!=null){
		    				c = Integer.parseInt(obj.new_safeboxmicnum);
		    				if(c >= 0){
		    					ai.new_safeboxmicnum = c + ai.new_safeboxmicnum;
		    					System.out.println("===== new_safeboxmicnum =====" + c + ", " + ai.new_safeboxmicnum);
		    				}
		    			}
		    			
		    		} catch (Exception e) {
		    			// TODO: handle exception
		    			e.printStackTrace();
		    		}
		        	
		        	/*//8. send push
		        	if(obj.push!=null && obj.push.length>0 ){
		        		Intent pushIntent = new Intent(CmmobiPushReceiver.ACTION_CMMOBI_PUSH_RECEIVER);
		        		pushIntent.putExtra(CmmobiPushReceiver.PUSH_JSON, new Gson().toJson(obj.push));
//		        		LocalBroadcastManager.getInstance(this).sendBroadcast(pushIntent);			
		        		context.sendBroadcast(pushIntent);
		        	}*/
		        
		    		SendBrodcast(obj);
					RemoteManager.getInstance(context).CallService(ai);
				}
				
			}
		}

		@Override
		public void Update2CallBack(Service2Main2Obj obj)
				throws RemoteException {
			// TODO Auto-generated method stub
			if(obj!=null && obj.userid!=null){
				CommonInfo ci = CommonInfo.getInstance();
				ci.heart = obj.heart;
				ci.promptmsg = obj.promptmsg;
			}
		}

	};
	
	private void SendBrodcast(Service2MainObj obj){
		Intent msgIntent = new Intent(CoreService.ACTION_MESSAGE_DATA_UPDATE);
		msgIntent.putExtra("hasFriendMsg", obj.hasFriendMsg);
    	msgIntent.putExtra("type", CoreService.HANDLER_FLAG_MESSAGE_DATA_UPDATE);
		msgIntent.putExtra("new_zonemicnum", obj.new_zonemicnum);          //微享新动态数时间戳
		msgIntent.putExtra("new_friend_change", obj.new_friend_change);        //好友新动态数
		msgIntent.putExtra("new_requestnum", obj.new_requestnum);        //新增好友请求数
		msgIntent.putExtra("friendnum", obj.friendnum);            //当前朋友数
    	msgIntent.putExtra("commentnum", obj.commentnum); //评论数，当评论数返回N时，客户端显示点
    	msgIntent.putExtra("new_friend", obj.new_friend);
    	
    	LocalBroadcastManager.getInstance(context).sendBroadcast(msgIntent);
    	MainApplication.getAppInstance().sendBroadcast(msgIntent);
	}
	
	
	public RemoteManager(Context context) {
		// TODO Auto-generated constructor stub

	}
	
	public static RemoteManager getInstance(Context context){
		if(ins==null){
			ins = new RemoteManager(context);
		}
		ins.context = context;
		return ins;
	}
	
	public void init(){
		Bundle args = new Bundle();
		Intent intent = new Intent();
		intent.setAction("com.cmmobi.looklook.common.service.CoreService");
		intent.putExtras(args);
		context.bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
	}
	
	public void uninit(){
		context.unbindService(mConnection);
	}
	

	public void CallService(AccountInfo accountInfo) {
		// TODO Auto-generated method stub
		if(accountInfo!=null && accountInfo.privateMsgManger!=null){
			PrivateMessageManager pmm = accountInfo.privateMsgManger;
			Main2ServiceObj obj = new Main2ServiceObj();
			obj.userid = accountInfo.userid;
			obj.micshareid = accountInfo.mishare_no;
			obj.timemill = pmm.timemill;
			obj.commentid = pmm.commentid;
			obj.commentid_safebox = pmm.commentid_safebox;
			obj.t_zone_mic = pmm.hSubScript.t_zone_mic;
			obj.t_zone_miccomment = pmm.hSubScript.t_zone_miccomment;
			obj.t_safebox_miccomment = pmm.hSubScript.t_safebox_miccomment;
			obj.t_friend = pmm.hSubScript.t_friend;
			obj.t_friend_change = pmm.hSubScript.t_friend_change;
			obj.t_friendrequest = pmm.hSubScript.t_friendrequest;
			obj.t_push = pmm.hSubScript.t_push;

			Log.v(TAG, "CallService - userid:" + obj.userid  + ", micshare:" + obj.micshareid + ", timemill:" + obj.timemill + ", commentid:" + obj.commentid);
			try {
				if(mService!=null){
					mService.invokCallBack(obj);
				}

			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}else{
			Log.v(TAG, "CallService - null");
			try {
				if(mService!=null){
					mService.invokCallBack(null);
				}

			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}	
	}

}
