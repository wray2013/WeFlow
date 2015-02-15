package com.cmmobi.looklook.info.profile;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;

import com.cmmobi.looklook.common.gson.GsonResponse2;
import com.cmmobi.looklook.common.gson.GsonResponse2.MyDiary;
import com.cmmobi.looklook.common.gson.GsonResponse2.activeDiaryListResponse;

public class ActivityDetailPartManager {

	public LinkedList<GsonResponse2.activeDiaryListResponse> activeDiaryListResponseList = new LinkedList<GsonResponse2.activeDiaryListResponse>();
	public String firstTime_savebox = "";
	public String lastTime_savebox = "";

	public void addActiveDiaryListResponseFirst(
			GsonResponse2.activeDiaryListResponse activeDiaryListResponse) {
		if ("1".equals(activeDiaryListResponse.is_refresh))// 是否重置数据
			activeDiaryListResponseList.clear();
		activeDiaryListResponseList.addFirst(activeDiaryListResponse);
	}

	public void addActiveDiaryListResponseEnd(activeDiaryListResponse res) {

		activeDiaryListResponseList.addLast(res);
	}

	public ArrayList<MyDiary> getMyDiary(int pageIndex) {
		ArrayList<MyDiary> diaries = new ArrayList<MyDiary>();

		int count = 0;
		for (int i = 0; i < activeDiaryListResponseList.size(); i++) {
			activeDiaryListResponse response = activeDiaryListResponseList
					.get(i);

			String first = response.first_user_time;
			String end = response.last_user_time;
			 if (0 == pageIndex)
			 firstTime_savebox = first;// 获取下拉刷新数据的时间戳 也就是日记列表中离现在最近的时间
			 lastTime_savebox = end;// 获取上拉加载数据的时间戳，也就是日记列表中离现在最远的时间
			if (null == response.diaries || 0 == response.diaries.length)
				continue;

			for (int j = 0; j < response.diaries.length; j++) {
				if (count >= (pageIndex) * 10) {
					for (int k = 0; k < diaries.size(); k++) {
						if (response.diaries[j].diaryid
								.equals(diaries.get(k).diaryid)) {
							diaries.remove(k);
							break;
						}
					}
					diaries.add(response.diaries[j]);
				}
				count++;
				if (count == (pageIndex + 1) * 10) {
					// 排序
					Collections.sort(diaries, new DiaryComparator());
					return diaries;
				}
			}
		}
		return diaries;
	}
	
	class DiaryComparator implements Comparator<MyDiary> {
        public int compare(MyDiary arg0, MyDiary arg1) {
            try {
				if (Long.parseLong(arg0.updatetimemilli)<Long.parseLong(arg1.updatetimemilli)) {
				    return 1;
				}
				if (Long.parseLong(arg0.updatetimemilli)==Long.parseLong(arg1.updatetimemilli)) {
				    return 0;
				}
			} catch (NumberFormatException e) {
				e.printStackTrace();
			}
            return -1;
        }
    }

	public String getReponseFirsttime() {
		return firstTime_savebox;
	}

	public String getReponseEndtime() {
		return lastTime_savebox;
	}
}
