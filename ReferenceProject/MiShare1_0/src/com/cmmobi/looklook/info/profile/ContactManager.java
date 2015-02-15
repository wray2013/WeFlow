package com.cmmobi.looklook.info.profile;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import com.baidu.platform.comapi.map.u;
import com.google.gson.Gson;

import android.R.integer;
import android.text.TextUtils;


public class ContactManager {
	public ArrayList<WrapUser> wrapUserList = new ArrayList<WrapUser>();

	public void refreshCache(ArrayList<WrapUser> list) {
		wrapUserList.clear();
		wrapUserList.addAll(list);
	}

	public List<WrapUser> getCache() {
		return wrapUserList;
	}
	
	public void addMember(WrapUser wrapUser) {
		if(wrapUser == null) return;
		if (!wrapUserList.contains(wrapUser)) {
			wrapUserList.add(wrapUser);
		} else {
			removeMember(wrapUser);
			wrapUserList.add(wrapUser);
		}
	}
	
	public void insertMember(int i, WrapUser wrapUser) {
		if(wrapUser == null) return;
		if (!isMember(wrapUser)) {
			wrapUserList.add(i, wrapUser);
		} else {
			removeMember(wrapUser);
			wrapUserList.add(i, wrapUser);
		}
	}
	
	public void addMembers(List<WrapUser> list) {
		wrapUserList.addAll(list);
	}
	
	public void clearList() {
		wrapUserList.clear();
	}
	
	public boolean isMember(WrapUser wrapUser) {
		if (wrapUserList.contains(wrapUser)) {
				return true;
		}
		return false;
	}
	
	public boolean isMemberByPhone(WrapUser wrapUser) {
		if(wrapUser == null) return false;
		if(findUserPhone(wrapUser) != null) {
				return true;
		}
		return false;
	}
	
	public boolean isMemberByUserid(String userid) {
		if (findUserByUserid(userid)!=null) {
				return true;
		}
		return false;
	}
	
	public WrapUser findUserByUserid(String userid){
		if(userid == null || userid.isEmpty()) return null;		
		for (WrapUser user:wrapUserList) {
			if (userid.equals(user.userid)) {
				return user;
			}
		}
		return null;
	}
	
	public boolean isContainByPhone(String phonenum){
		if(TextUtils.isEmpty(phonenum)) return false;
		for (WrapUser user:wrapUserList) {
			if (phonenum.equals(user.phonenum)) {
				return true;
			}
		}
		return false;
	}
	
	public WrapUser findUserPhone(WrapUser wrapUser){
		if(wrapUser == null) return null;
		for (WrapUser user:wrapUserList) {
			if (wrapUser.phonename.equals(user.phonename) && wrapUser.phonenum.equals(user.phonenum)) {
				return user;
			}
		}
		return null;
	}
	
	//好友列表里的新增的人，是否在原通讯录中能找到
	public WrapUser findNewUserPhone(WrapUser wrapUser){
		if(wrapUser == null) return null;
		for (WrapUser user:wrapUserList) {
			if (wrapUser.telephone!=null && !wrapUser.telephone.isEmpty() && wrapUser.telephone.equals(user.phonenum)) {
				return user;
			}
		}
		return null;
	}
	
	public void removeMember(WrapUser wrapUser) {
		if(wrapUser == null) return;
		WrapUser user = findUserByUserid(wrapUser.userid);
		if(user != null){
			wrapUserList.remove(user);
		}else{
			user = findUserPhone(wrapUser);
			if(user !=null ){
				wrapUserList.remove(user);
			}
		}
	}
	
	public void removeMemberByUserid(String userid){
		WrapUser user = findUserByUserid(userid);
		if(user != null){
			wrapUserList.remove(user);
		}
	}

	public void addRecentContact(int index, WrapUser wrapUser){
		if(wrapUser == null) return;
		WrapUser user = new Gson().fromJson(wrapUser.toString(), WrapUser.class);
		user.isRecent = true;
		insertMember(index, user);
		for(int i = wrapUserList.size()-1; i>7; i--){
			wrapUserList.remove(i);
		}
	}
	
	public void addRecentContact(WrapUser wrapUser){
		if(wrapUser == null) return;
		addRecentContact(0, wrapUser);
	}
		
	public WrapUser findRecentContact(WrapUser wrapUser){
		if(wrapUser == null) return null;
		for(int i = 0; i< wrapUserList.size(); i++){
			if(wrapUser.userid!=null && !wrapUser.userid.isEmpty() && wrapUserList.get(i).userid!=null && !wrapUserList.get(i).userid.isEmpty() && wrapUserList.get(i).userid.equals(wrapUser.userid)){
				return wrapUserList.get(i);
			}else if(wrapUser.telephone!=null && !wrapUser.telephone.isEmpty() && wrapUserList.get(i).phonenum!=null && !wrapUserList.get(i).phonenum.isEmpty() && wrapUserList.get(i).phonenum.equals(wrapUser.telephone)){
				return wrapUserList.get(i);
			}
		}
		return null;
	}
	
	public static class SortByUpdateTime implements Comparator {
		 public int compare(Object o1, Object o2) {
		  WrapUser s1 = (WrapUser) o1;
		  WrapUser s2 = (WrapUser) o2;
		 
		  try {
			  if(TextUtils.isEmpty(s1.update_time)&&TextUtils.isEmpty(s2.update_time)){
				  return 0;
			  }else if(TextUtils.isEmpty(s1.update_time)){
				  return 1;
			  }else if(TextUtils.isEmpty(s2.update_time)){
				  return -1;
			  }else {
				  return s2.update_time.compareTo(s1.update_time);
			  }
			 
			} catch (Exception e) {
				// TODO: handle exception
			}
		 return 0;
		 }
		}
	
	
	public List<String> getStrings(){
		List<String> strings = new ArrayList<String>();
		for(int i=0; i< wrapUserList.size(); i++){
			if(!strings.contains(wrapUserList.get(i).nickname)){
				strings.add(wrapUserList.get(i).nickname);
			}
			
			if(wrapUserList.get(i).markname!=null && !wrapUserList.get(i).markname.isEmpty() && !strings.contains(wrapUserList.get(i).markname)){
				strings.add(wrapUserList.get(i).markname);
			}
			
			strings.add(wrapUserList.get(i).micnum);
		}
		return strings;
	}
	
	public ArrayList<WrapUser> getSearchUsers(String search){
		ArrayList<WrapUser> users = new ArrayList<WrapUser>();
		for(int i=0; i< wrapUserList.size(); i++){
			if(wrapUserList.get(i).markname!=null && wrapUserList.get(i).markname.toLowerCase().contains(search.toLowerCase())){
				users.add(wrapUserList.get(i));
				continue;
			}
			
			if(wrapUserList.get(i).nickname!=null && wrapUserList.get(i).nickname.toLowerCase().contains(search.toLowerCase())){
				users.add(wrapUserList.get(i));
				continue;
			}
		
			if(wrapUserList.get(i).telname!=null && wrapUserList.get(i).telname.toLowerCase().contains(search.toLowerCase())){
				users.add(wrapUserList.get(i));
				continue;
			}
			if(wrapUserList.get(i).micnum!=null && wrapUserList.get(i).micnum.toLowerCase().contains(search.toLowerCase())){
				users.add(wrapUserList.get(i));
				continue;
			}
		}
		
/*		ArrayList<WrapUser> usersSort = new ArrayList<WrapUser>();
		for(int i=0; i< users.size(); i++){
			if(users.get(i).markname!=null && ! users.get(i).markname.isEmpty()){
				usersSort.add(users.get(i));
			}
		}
		for(int i=0; i< users.size(); i++){
			if(users.get(i).markname ==null || users.get(i).markname.isEmpty()){
				usersSort.add(users.get(i));
			}
		}
		return usersSort;*/
		return users;
	}
}
