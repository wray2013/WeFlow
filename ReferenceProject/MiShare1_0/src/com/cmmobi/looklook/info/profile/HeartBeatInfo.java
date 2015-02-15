package com.cmmobi.looklook.info.profile;

import android.content.Context;
import android.util.Log;

import com.cmmobi.looklook.common.service.aidl.Main2ServiceObj;
import com.cmmobi.looklook.common.storage.StorageManager;

public class HeartBeatInfo {
	private static transient final String HeartBeatInfoKey = "HeartBeatInfoKey_";// 需要确保全局唯一性
	private static transient final String TAG = "HeartBeatInfo";
	private static transient HeartBeatInfo ins = null;
	/******************************************************************/
	public String userid; // 用户ID,若为空则说明没有登录
	
	public String micshareid;
	public String timemill; //pmm.timemill
	public String commentid;
	public String t_zone_mic; //pmm.hSubScript.t_zone_mic,
	public String t_friend; //pmm.hSubScript.t_friend,
	//public String t_attention; //pmm.hSubScript.t_attention,
	//public String t_fans; //pmm.hSubScript.t_fans,
	//public String t_recommend; //pmm.hSubScript.t_recommend,
	//public String t_snsfriend; //pmm.hSubScript.t_snsfriend,
	public String t_friendrequest; //pmm.hSubScript.t_friendrequest,
	public String t_friend_change; //pmm.hSubScript.t_friend_change,
	public String t_push; //pmm.hSubScript.t_push,
	public String t_zone_miccomment; //pmm.hSubScript.t_zone_miccomment
	
	public String commentid_safebox;
	public String t_safebox_miccomment;
	
	private HeartBeatInfo(){
		userid = null;
		
	}
	
	/**
	 * 
	 * */
	public static HeartBeatInfo getInstance(Context c, String userid){
		if(ins==null || (userid!=null && !userid.equals(ins.userid))){
			HeartBeatInfo a = (HeartBeatInfo) StorageManager.getInstance().getItem(
					HeartBeatInfoKey + userid, HeartBeatInfo.class);

			if (a != null) {
				ins = a;
			} else {
				ins = new HeartBeatInfo();
				ins.userid = userid;
			}
		}

		return ins;
	}
	
	synchronized public void reload(){
		HeartBeatInfo a = (HeartBeatInfo) StorageManager.getInstance().getItem(
				HeartBeatInfoKey + userid, HeartBeatInfo.class);
		
		if (a != null) {
			ins = a;	
		}
	}
	
	
	synchronized public void persist() {
		Log.e(TAG, "HeartBeatInfo: " + userid + " persist!");
		if(userid!=null){
			try{
				StorageManager.getInstance().putItem(HeartBeatInfoKey + userid, ins, HeartBeatInfo.class);
			}catch(Exception e){
				e.printStackTrace();
			}
		}
	}

	public boolean setMain2ServerObj(Main2ServiceObj obj) {
		// TODO Auto-generated method stub
		boolean ret = false;
		this.commentid = obj.commentid;
		this.commentid_safebox = obj.commentid_safebox;
		this.micshareid = obj.micshareid;
		//this.t_attention = obj.t_attention;
		//this.t_fans = obj.t_fans;
		this.t_friend = obj.t_friend;
		this.t_friend_change = obj.t_friend_change;
		this.t_friendrequest = obj.t_friendrequest;
		this.t_push = obj.t_push;
		//this.t_recommend = obj.t_recommend;
		//this.t_snsfriend = obj.t_snsfriend;
		this.t_zone_mic = obj.t_zone_mic;
		this.t_zone_miccomment = obj.t_zone_miccomment;
		this.t_safebox_miccomment = obj.t_safebox_miccomment;
		this.timemill = obj.timemill;
		if(obj.userid!=null && !obj.userid.equals(this.userid)){
			ret = true;
		}
		this.userid = obj.userid;
		
		return ret;
	}

	public boolean isLogin() {
		// TODO Auto-generated method stub
		return userid!=null && !userid.equals(ActiveAccount.TEMP_USERID);
	}

}
