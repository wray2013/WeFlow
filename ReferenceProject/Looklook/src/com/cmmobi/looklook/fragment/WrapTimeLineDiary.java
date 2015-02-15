package com.cmmobi.looklook.fragment;

import com.cmmobi.looklook.common.gson.GsonResponse2.Forward;
import com.cmmobi.looklook.common.gson.GsonResponse2.MyDiary;
import com.cmmobi.looklook.common.gson.GsonResponse2.TimelineDiary;

public class WrapTimeLineDiary {
	public MyDiary diary;
	public Forward forward;
	
	public boolean isZan ;
	
	
	public WrapTimeLineDiary(TimelineDiary timelineDiary) {
		this.diary = timelineDiary.diary;
		this.forward = timelineDiary.forward;
		
		this.isZan = false;
	}

	@Override
	public boolean equals(Object object) {
		if (!(object instanceof WrapTimeLineDiary)) {
			return false;
		}
		WrapTimeLineDiary myDiary = (WrapTimeLineDiary) object;
		if (this.diary.diaryid.equals(myDiary.diary.diaryid) && this.forward.forwarduserid.equals(myDiary.forward.forwarduserid)) {
			return true;
		}
		return false;
	}
}
