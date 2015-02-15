package com.cmmobi.looklook.info.profile;

import java.util.ArrayList;
import java.util.Iterator;

import com.cmmobi.looklook.common.gson.GsonResponse2.MyUserids;

public class OfficialUseridsManager {
	public ArrayList<MyUserids> MyUseridsList = new ArrayList<MyUserids>();

	public void refreshCache(ArrayList<MyUserids> list) {
		MyUseridsList.clear();
		MyUseridsList.addAll(list);
	}
	
	public boolean ContainUser(String userid) {
		if(MyUseridsList!=null && MyUseridsList.size()>0){
			Iterator<MyUserids> iterator = MyUseridsList.iterator();
			while(iterator.hasNext()){
				MyUserids item = iterator.next();
				if(item!=null && item.userid!=null && item.userid.equals(userid)){
					return true;
				}
			}
		}
		
		return false;
	}

	public ArrayList<MyUserids> getCache() {
		return MyUseridsList;
	}
	
	public void addMember(MyUserids MyUserids) {
		if (!isMember(MyUserids.userid)) {
			MyUseridsList.add(MyUserids);
		} else {
			removeMember(MyUserids.userid);
			MyUseridsList.add(MyUserids);
		}
	}
	
	public void addMembers(ArrayList<MyUserids> list) {
		MyUseridsList.addAll(list);
	}
	
	public void clearAttentions() {
		MyUseridsList.clear();
	}
	
	public boolean isMember(String userid) {
		for (MyUserids user:MyUseridsList) {
			if (userid.equals(user.userid)) {
				return true;
			}
		}
		return false;
	}
	
	public void removeMember(String userid) {
		for (MyUserids user:MyUseridsList) {
			if (userid.equals(user.userid)) {
				MyUseridsList.remove(user);
				break;
			}
		}
	}

}
