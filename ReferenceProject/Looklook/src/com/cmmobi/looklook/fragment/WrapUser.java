package com.cmmobi.looklook.fragment;

import com.google.gson.Gson;

public class WrapUser {

	public String userid; // 好友ID
	public String headimageurl; // "http://…jpg", 头像URL，可为空
	public String nickname; // 昵称
	public String diarycount; // 日记数
	public String attentioncount; // 关注数
	public String fanscount;
	public String sex; // 0男 1女 2未知
	public String signature; // "jdfdf"个人签名（base64编码）
	public String sortKey;
	
	@Override
	public String toString() {
		Gson gson = new Gson();
		return gson.toJson(this);
	}
	
	@Override
	public boolean equals(Object o) {
		// TODO Auto-generated method stub
		if (o == null) {
			 return false;
		}
		WrapUser user = (WrapUser)o;
		if (user.userid == null) {
			return false;
		}
		return user.userid.equals(this.userid);
	}
	
	
}
