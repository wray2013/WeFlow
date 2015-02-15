package com.cmmobi.looklook.info.profile;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.text.TextUtils;

import com.cmmobi.looklook.common.gson.GsonResponse3.Contents;
import com.cmmobi.looklook.common.gson.GsonResponse3.MicListItem;
import com.cmmobi.looklook.common.gson.GsonResponse3.MyDiary;
import com.cmmobi.looklook.common.gson.GsonResponse3.VshareDiary;

/**
 * @author youtian
 * @email youtian@cmmobi.com
 * @date 2014-4-02
 */
public class FriendNewsDataEntities {

	/*
	 * value-一条好友动态结构
	 */
	public ArrayList<Contents> friendNewsList=new ArrayList<Contents>();
	
	public String fristTime = "";
	public String lastTime = "";
	
	public void refreshCache(ArrayList<Contents> list) {
		friendNewsList.clear();
		friendNewsList.addAll(list);
	}
	

	public List<Contents> getCache() {
		return friendNewsList;
	}
	
	public void clearList() {
		friendNewsList.clear();
	}
	
	public void addMember(Contents item) {
		if(item != null){
		if (!isMember(item.diaries.diaryid)) {
			friendNewsList.add(item);
		} else {
			removeMember(item.diaries.diaryid);
			friendNewsList.add(item);
		}
		}
	}
	
	public void insertMember(int i, Contents item) {
		if(item != null){
			if (!isMember(item.diaries.diaryid)) {
				friendNewsList.add(i, item);
			} else {
				removeMember(item.diaries.diaryid);
				friendNewsList.add(i, item);
			}
		}
	}
	
	
	public boolean isMember(String diaryid) {
		for (Contents item:friendNewsList) {
			if (diaryid.equals(item.diaries.diaryid)) {
				return true;
			}
		}
		return false;
	}
	
	public Contents findMember(String diaryid) {
		for (Contents item:friendNewsList) {
			if (diaryid.equals(item.diaries.diaryid)) {
				return item;
			}
		}
		return null;
	}
	
	public void removeMember(String diaryid) {
		for (Contents item:friendNewsList) {
			if (diaryid.equals(item.diaries.diaryid)) {
				friendNewsList.remove(item);
				break;
			}
		}
	}
	
	public void removeUserNews(String userid){
		for(int i=0; i<friendNewsList.size(); i++){
			if(userid.trim().equals(friendNewsList.get(i).diaries.userid.trim())){
				friendNewsList.remove(i);
				i--;
			}
		}
	}
}
