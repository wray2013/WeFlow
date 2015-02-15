package com.cmmobi.looklook.common.service.aidl;

import com.cmmobi.looklook.common.gson.GsonResponse3.ConfigHeart;
import com.cmmobi.looklook.common.gson.GsonResponse3.ConfigPromptMsg;
import com.google.gson.Gson;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * @author zhangwei
 */
public class Service2Main2Obj implements Parcelable {
	public String userid;
	public ConfigHeart[] heart;
	public ConfigPromptMsg[] promptmsg;
	
	
	public static final Parcelable.Creator<Service2Main2Obj> CREATOR = new Parcelable.Creator<Service2Main2Obj>() {
		public Service2Main2Obj createFromParcel(Parcel in) {
			return new Service2Main2Obj(in);
		}


		public Service2Main2Obj[] newArray(int size) {
			return new Service2Main2Obj[size];
		}
	};


	public Service2Main2Obj() {
	}


	private Service2Main2Obj(Parcel in) {
		readFromParcel(in);
	}

	public void readFromParcel(Parcel in) {
		Gson gson = new Gson();		
		
		userid = in.readString();
		
		heart = gson.fromJson(in.readString(), ConfigHeart[].class);
		
		promptmsg = gson.fromJson(in.readString(), ConfigPromptMsg[].class);
	}


	@Override
	public int describeContents() {
		return 0;
	}


	@Override
	public void writeToParcel(Parcel out, int flags) {
		Gson gson = new Gson();	
		
		out.writeString(userid);

		out.writeString(gson.toJson(heart));
		
		out.writeString(gson.toJson(promptmsg));
	}

}
