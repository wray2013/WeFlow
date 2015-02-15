package com.cmmobi.looklook.common.service.aidl;
/*****************Copyright (C), 2010-2015, FORYOU Tech. Co., Ltd.********************/


import com.cmmobi.looklook.common.gson.GsonResponse3.HeartPush;
import com.cmmobi.looklook.common.gson.GsonResponse3.MessageUser;
import com.google.gson.Gson;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * @author zhangwei
 */
public class Service2MainObj implements Parcelable {
	public String userid;
	public String micshareid;
	public MessageUser[] mus;
//	public boolean hasStrangerMsg;
	public boolean hasFriendMsg;
	
	public String new_zonemicnum;
	public String new_friend_change;
	public String new_requestnum;
	
	public String friendnum;
	public String commentnum;
	
	public String last_timemilli;
	
	public String t_push;
	public String commentnum_safebox;
	public String new_safeboxmicnum;
	public String new_friend;
	
	
	public long servertime;
	
	public HeartPush[] push;
	
	public String readedMessages;
	
	public static final Parcelable.Creator<Service2MainObj> CREATOR = new Parcelable.Creator<Service2MainObj>() {
		public Service2MainObj createFromParcel(Parcel in) {
			return new Service2MainObj(in);
		}


		public Service2MainObj[] newArray(int size) {
			return new Service2MainObj[size];
		}
	};


	public Service2MainObj() {
	}


	private Service2MainObj(Parcel in) {
		readFromParcel(in);
	}

	public void readFromParcel(Parcel in) {
		Gson gson = new Gson();		
		userid = in.readString();
		micshareid = in.readString();
		mus = gson.fromJson(in.readString(), MessageUser[].class);
		
		new_zonemicnum = in.readString();
		new_friend_change = in.readString();
		new_requestnum = in.readString();
		
		friendnum = in.readString();
		commentnum = in.readString();
		
		t_push = in.readString();
		commentnum_safebox = in.readString();
		new_safeboxmicnum = in.readString();
		new_friend = in.readString();
		
		servertime = in.readLong();
		last_timemilli = in.readString();
		
		push = gson.fromJson(in.readString(), HeartPush[].class);
		readedMessages = in.readString();
	}


	@Override
	public int describeContents() {
		return 0;
	}


	@Override
	public void writeToParcel(Parcel out, int flags) {
		Gson gson = new Gson();	
		out.writeString(userid);
		out.writeString(micshareid);
		out.writeString(gson.toJson(mus));
		
		out.writeString(new_zonemicnum);
		out.writeString(new_friend_change);
		out.writeString(new_requestnum);
		
		out.writeString(friendnum);
		out.writeString(commentnum);
		
		out.writeString(t_push);
		out.writeString(commentnum_safebox);
		out.writeString(new_safeboxmicnum);
		out.writeString(new_friend);
		
		out.writeLong(servertime);
		out.writeString(last_timemilli);
		
		out.writeString(gson.toJson(push));
		out.writeString(readedMessages);
	}




}
