package com.cmmobi.looklook.common.service.aidl;
/*****************Copyright (C), 2010-2015, FORYOU Tech. Co., Ltd.********************/

import android.os.Parcel;
import android.os.Parcelable;

/**
 * @Filename: Main2ServiceObj.java
 * @Author: slcao
 * @CreateDate: 2011-5-16
 * @Description: description of the new class
 * @Others: comments
 * @ModifyHistory:
 */
public class Main2ServiceObj implements Parcelable {
	public String userid;
	public String micshareid;
	public String timemill; //pmm.timemill
	public String commentid;
	public String commentid_safebox;
	public String t_zone_mic; //pmm.hSubScript.t_zone_mic,
	public String t_zone_miccomment; //pmm.hSubScript.t_zone_miccomment
	public String t_safebox_miccomment;
	public String t_friend; //pmm.hSubScript.t_friend,
	public String t_friend_change; //pmm.hSubScript.t_friend_change,
	public String t_friendrequest; //pmm.hSubScript.t_friendrequest,
	public String t_push; //pmm.hSubScript.t_push,
	
	// del
	//public String t_attention; //pmm.hSubScript.t_attention,
	//public String t_fans; //pmm.hSubScript.t_fans,
	//public String t_recommend; //pmm.hSubScript.t_recommend,
	//public String t_snsfriend; //pmm.hSubScript.t_snsfriend,
	
	public static final Parcelable.Creator<Main2ServiceObj> CREATOR = new Parcelable.Creator<Main2ServiceObj>() {
		public Main2ServiceObj createFromParcel(Parcel in) {
			return new Main2ServiceObj(in);
		}


		public Main2ServiceObj[] newArray(int size) {
			return new Main2ServiceObj[size];
		}
	};


	public Main2ServiceObj() {
	}


	private Main2ServiceObj(Parcel in) {
		readFromParcel(in);
	}

	public void readFromParcel(Parcel in) {
		userid = in.readString();
		timemill = in.readString();
		commentid = in.readString();
		commentid_safebox = in.readString();
		t_zone_mic = in.readString();
		t_zone_miccomment = in.readString();
		t_safebox_miccomment = in.readString();
		t_friend = in.readString();
		t_friend_change = in.readString();
		t_friendrequest = in.readString();
		t_push = in.readString();
		
		//del
		//t_attention = in.readString();
		//t_fans = in.readString();
		//t_recommend = in.readString();
		//t_snsfriend = in.readString();
		
	}


	@Override
	public int describeContents() {
		return 0;
	}


	@Override
	public void writeToParcel(Parcel out, int flags) {
		out.writeString(userid);
		out.writeString(timemill);
		out.writeString(commentid);
		out.writeString(commentid_safebox);
		out.writeString(t_zone_mic);
		out.writeString(t_zone_miccomment);
		out.writeString(t_safebox_miccomment);
		out.writeString(t_friend);
		out.writeString(t_friend_change);
		out.writeString(t_friendrequest);
		out.writeString(t_push);
		
		//del
		//out.writeString(t_attention);
		//out.writeString(t_fans);
		//out.writeString(t_recommend);
		//out.writeString(t_snsfriend);
		
	}




}
