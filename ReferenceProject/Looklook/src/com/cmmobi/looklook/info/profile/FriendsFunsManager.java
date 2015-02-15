package com.cmmobi.looklook.info.profile;

import java.util.ArrayList;

import com.cmmobi.looklook.fragment.WrapUser;

public class FriendsFunsManager {

	private ArrayList<WrapUser> wrapUserList = new ArrayList<WrapUser>();

//	public ArrayList<MyDiary> diaryList = new ArrayList<GsonResponse2.MyDiary>();
//	public ArrayList<timelinePart> partList = new ArrayList<GsonResponse2.timelinePart>();

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
	// public ArrayList<MyDiary> getDiaryCache(int pageIndex) {
	//
	// ArrayList<MyDiary> diaries = new ArrayList<GsonResponse2.MyDiary>();
	//
	// timelineResponse response = serverTimeLineList.get(pageIndex);
	//
	// MyDiary[] myDiaries = response.diaries;
	// diaries.addAll(Arrays.asList(myDiaries));
	//
	//
	// return diaries;
	// }
	//
	// public ArrayList<timelinePart> getPartsCache(int pageIndex) {
	//
	// ArrayList<timelinePart> timelineParts = new
	// ArrayList<GsonResponse2.timelinePart>();
	//
	// timelineResponse response = serverTimeLineList.get(pageIndex);
	//
	// timelinePart[] parts = response.part;
	// timelineParts.addAll(Arrays.asList(parts));
	//
	//
	// return timelineParts;
	// }
}
