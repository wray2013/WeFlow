package com.cmmobi.looklook.info.profile;

import java.util.ArrayList;

import com.cmmobi.looklook.fragment.WrapTimeLineDiary;

public class FriendsCircleManager {

//	public ArrayList<GsonResponse2.timelineResponse> serverTimeLineList = new ArrayList<GsonResponse2.timelineResponse>();

	public ArrayList<WrapTimeLineDiary> timelineDiariyCache = new ArrayList<WrapTimeLineDiary>();
	
	public void refreshCache(ArrayList<WrapTimeLineDiary> timelineDiary) {
		timelineDiariyCache.clear();
		timelineDiariyCache.addAll(timelineDiary);
	}
	
	public ArrayList<WrapTimeLineDiary> getCahceDiaries() {
		return timelineDiariyCache;
	}
//	public ArrayList<MyDiary> diaryList = new ArrayList<GsonResponse2.MyDiary>();
//	public ArrayList<timelinePart> partList = new ArrayList<GsonResponse2.timelinePart>();
	

//	public void refreshCache(timelineResponse response) {
//
//		serverTimeLineList.clear();
//		serverTimeLineList.add(response);
//	}
//
//	public ArrayList<GsonResponse2.timelineResponse> getCache() {
//
//		return serverTimeLineList;
//	}

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
