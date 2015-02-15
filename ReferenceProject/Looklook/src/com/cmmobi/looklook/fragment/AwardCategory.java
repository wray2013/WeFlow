package com.cmmobi.looklook.fragment;

import com.cmmobi.looklook.common.gson.GsonResponse2.MyDiary;

public class AwardCategory {

	private String awardname;
	private MyDiary[] diaries;

	public String getAwardname() {
		return awardname;
	}

	public void setAwardname(String awardname) {
		this.awardname = awardname;
	}

	public void addItem(MyDiary[] diaries) {
		this.diaries = diaries;
	}

	public Object getItem(int position) {

		if (0 == position) {
			return awardname;
		} else {
			return diaries[position - 1];
		}
	}
	
	public int getItemCount () {
		return diaries.length + 1;
	}
}
