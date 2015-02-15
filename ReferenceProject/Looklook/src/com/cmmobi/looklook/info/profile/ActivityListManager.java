package com.cmmobi.looklook.info.profile;

import com.cmmobi.looklook.common.gson.GsonResponse2.activeListItem;

public class ActivityListManager {

	private activeListItem[] activies;

	// public ArrayList<MyDiary> diaryList = new
	// ArrayList<GsonResponse2.MyDiary>();
	// public ArrayList<timelinePart> partList = new
	// ArrayList<GsonResponse2.timelinePart>();

	public void refreshCache(activeListItem[] list) {

		activies = null;
		activies = new activeListItem[list.length];
		System.arraycopy(list, 0, activies, 0, list.length);
	}

	public activeListItem[] getCache() {

		return activies;
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
