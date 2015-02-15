package com.cmmobi.looklook.info.profile;

import java.util.ArrayList;

import com.cmmobi.looklook.fragment.WrapUser;

public class ContactManager {
	public ArrayList<WrapUser> wrapUserList = new ArrayList<WrapUser>();

	public void refreshCache(ArrayList<WrapUser> list) {
		wrapUserList.clear();
		wrapUserList.addAll(list);
	}

	public ArrayList<WrapUser> getCache() {
		return wrapUserList;
	}
	
	public void addMember(WrapUser wrapUser) {
		if (!isMember(wrapUser.userid)) {
			wrapUserList.add(wrapUser);
		} else {
			removeMember(wrapUser.userid);
			wrapUserList.add(wrapUser);
		}
	}
	
	public void addMembers(ArrayList<WrapUser> list) {
		wrapUserList.addAll(list);
	}
	
	public void clearAttentions() {
		wrapUserList.clear();
	}
	
	public boolean isMember(String userid) {
		for (WrapUser user:wrapUserList) {
			if (userid.equals(user.userid)) {
				return true;
			}
		}
		return false;
	}
	
	public void removeMember(String userid) {
		for (WrapUser user:wrapUserList) {
			if (userid.equals(user.userid)) {
				wrapUserList.remove(user);
				break;
			}
		}
	}

}
