package com.cmmobi.looklook.fragment;

import com.cmmobi.looklook.info.profile.WrapUser;

public class WrapSelectedUser extends WrapUser {
	public boolean isSelected = false;
	
	public WrapSelectedUser(WrapUser user,boolean isSelected) {
		// TODO Auto-generated constructor stub
		this.diarycount = user.diarycount;
		this.headimageurl = user.headimageurl;
		this.nickname = user.nickname;
		this.sex = user.sex;
		this.signature = user.signature;
		this.sortKey = user.sortKey;
		this.userid = user.userid;
		this.isSelected = isSelected;
	}
	
	public WrapSelectedUser() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean equals(Object o) {
		// TODO Auto-generated method stub
		if (o == null) {
			 return false;
		}
		WrapSelectedUser user = (WrapSelectedUser)o;
		if (user.userid == null) {
			return false;
		}
		return user.userid.equals(this.userid);
	}
	
	
}
