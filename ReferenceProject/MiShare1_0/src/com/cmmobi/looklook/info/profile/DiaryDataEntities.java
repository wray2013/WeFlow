package com.cmmobi.looklook.info.profile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.cmmobi.looklook.common.gson.GsonResponse3.MyDiary;
import com.cmmobi.looklook.common.gson.GsonResponse3.MyDiaryList;
import com.cmmobi.looklook.common.gson.GsonResponse3.MyDiaryids;
import com.cmmobi.looklook.common.gson.GsonResponse3.taglistItem;

/**
 * @author wuxiang
 * @email wuxiang@cmmobi.com
 * @date  2013-11-13
 */
public class DiaryDataEntities {

	/**
	 * 存储该用户所有日记结构
	 * key-diaryuuid
	 * value-日记结构
	 */
	//public HashMap<String,MyDiary> diariesMap=new HashMap<String,MyDiary>();
	
	/**
	 * 存储该用户所有日记组信息
	 */
	public ArrayList<MyDiaryList> dairyGroupList=new ArrayList<MyDiaryList>();
	
	/**
	 * 标签列表
	 */
	public ArrayList<taglistItem> tagsList = new ArrayList<taglistItem>();
	
	/**
	 * 本地最新日记更新时间
	 */
	public String myZoneFirstTime="";
	/**
	 * 本地最旧日记更新时间
	 */
	public String myZoneLastTime="";
	
	public ArrayList<MyDiaryids> praisedDiariesIDList = new ArrayList<MyDiaryids>();
	public ArrayList<MyDiaryids> collectDiariesIDList = new ArrayList<MyDiaryids>();
	
	
}
