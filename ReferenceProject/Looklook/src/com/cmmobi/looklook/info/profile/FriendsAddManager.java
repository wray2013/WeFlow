package com.cmmobi.looklook.info.profile;

import java.util.ArrayList;

import com.cmmobi.looklook.fragment.WrapRecommendUser;

public class FriendsAddManager {

	private ArrayList<WrapRecommendUser> wrapUserList = new ArrayList<WrapRecommendUser>();

	// public ArrayList<MyDiary> diaryList = new
	// ArrayList<GsonResponse2.MyDiary>();
	// public ArrayList<timelinePart> partList = new
	// ArrayList<GsonResponse2.timelinePart>();

	public void refreshCache(ArrayList<WrapRecommendUser> list) {

		wrapUserList.clear();
		wrapUserList.addAll(list);
	}

	public ArrayList<WrapRecommendUser> getCache() {

		return wrapUserList;
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
