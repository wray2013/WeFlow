package com.cmmobi.looklook.info.profile;

import java.util.ArrayList;

import com.cmmobi.looklook.common.gson.GsonResponse2.MyDiary;

public class ActivitiesDiariesManager {


	public ArrayList<MyDiary> activityDiariyCache = new ArrayList<MyDiary>();
	
	public void refreshCache(ArrayList<MyDiary> timelineDiary) {
		activityDiariyCache.clear();
		activityDiariyCache.addAll(timelineDiary);
	}
	
	public ArrayList<MyDiary> getCahceDiaries() {
		return activityDiariyCache;
	}
}
