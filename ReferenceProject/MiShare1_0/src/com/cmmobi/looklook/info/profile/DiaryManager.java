package com.cmmobi.looklook.info.profile;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.AbsoluteSizeSpan;
import android.util.Log;

import cn.zipper.framwork.utils.ZStringUtils;

import com.cmmobi.looklook.MainApplication;
import com.cmmobi.looklook.activity.DiaryPreviewActivity;
import com.cmmobi.looklook.common.gson.GsonResponse3;
import com.cmmobi.looklook.common.gson.GsonResponse3.MyDiary;
import com.cmmobi.looklook.common.gson.GsonResponse3.MyDiaryList;
import com.cmmobi.looklook.common.gson.GsonResponse3.MyDiaryids;
import com.cmmobi.looklook.common.gson.GsonResponse3.TAG;
import com.cmmobi.looklook.common.gson.GsonResponse3.taglistItem;
import com.cmmobi.looklook.common.storage.SqliteDairyManager;
import com.cmmobi.looklook.common.utils.DateUtils;
import com.cmmobi.looklook.common.utils.DisplayUtil;
import com.cmmobi.looklook.fragment.MyZoneFragment.DiariesItem;
import com.cmmobi.looklook.fragment.MyZoneFragment.MyZoneItem;
import com.cmmobi.looklook.fragment.MyZoneFragment.UserInfo;
import com.cmmobi.looklook.networktask.NetworkTaskManager;

/**
 * @author wuxiang
 * @email wuxiang@cmmobi.com
 * @date 2013-11-13
 */
public class DiaryManager {
	
	/**
	 * 从diaryGroup获取所有日记uuid，已逗号分隔
	 */
	public String getDiaryUUIDs(ArrayList<MyDiaryList> diaryGroup){
		String uuids="";
		for(int i=0;i<diaryGroup.size();i++){
			MyDiaryList diaryList=diaryGroup.get(i);
			if("0".equals(diaryList.isgroup)){//不是组
				MyDiary myDiary=findMyDiaryByUUID(diaryList.diaryuuid);
				if(myDiary!=null)
					uuids+=myDiary.diaryuuid+",";
			}else{//是组
				if(null==diaryList.contain)return null;
				String[] diaryids=diaryList.contain.split(",");
				if(diaryids!=null){
					for(int j=0;j<diaryids.length;j++){
						MyDiary myDiary=findMyDiaryByDiaryID(diaryids[j]);
						if(myDiary!=null)
							uuids+=myDiary.diaryuuid+",";
					}
				}
			}
		}
		if(uuids.endsWith(","))
			uuids=uuids.substring(0, uuids.length()-1);
		return uuids;
	}
	
	/**
	 * 更新日记标签
	 */
	public void updateDiaryTags(ArrayList<MyDiaryList> diaryGroup,String[] tagids){
		if(tagids!=null&&tagids.length>0&&diaryGroup!=null&&diaryGroup.size()>0){
			TAG[] tags=getTagsById(tagids);
			for(int i=0;i<diaryGroup.size();i++){
				MyDiaryList diaryList=diaryGroup.get(i);
				if("0".equals(diaryList.isgroup)){//不是组
					MyDiary myDiary=findMyDiaryByUUID(diaryList.diaryuuid);
					if(myDiary!=null)
						myDiary.tags=tags;
				}else{//是组
					if(null==diaryList.contain)return;
					String[] diaryids=diaryList.contain.split(",");
					if(diaryids!=null){
						for(int j=0;j<diaryids.length;j++){
							MyDiary myDiary=findMyDiaryByDiaryID(diaryids[j]);
							if(myDiary!=null)
								myDiary.tags=tags;
						}
					}
				}
			}
		}
	}
	
	//根据标签id获取标签
	private TAG[] getTagsById(String...tagids){
		if(null==tagids)return null;
		ArrayList<TAG> tags=new ArrayList<TAG>();
		for(int i=0;i<diaryDataEntities.tagsList.size();i++){
			taglistItem item=diaryDataEntities.tagsList.get(i);
			for(int j=0;j<tagids.length;j++){
				if(item!=null&&item.id.equals(tagids[j])){
					TAG tag=new TAG();
					tag.id=item.id;
					tag.name=item.name;
					tags.add(tag);
				}
			}
		}
		return tags.toArray(new TAG[tags.size()]);
	}
	
	/**
	 * 获取本地标签列表
	 */
	public List<taglistItem> getTags(){
		return diaryDataEntities.tagsList;
	}
	
	/**
	 * 缓存服务器标签列表
	 */
	public void addTagList(taglistItem...items){
		if(items!=null&&items.length>0){
			diaryDataEntities.tagsList.clear();
			for(int i=0;i<items.length;i++){
				diaryDataEntities.tagsList.add(items[i]);
			}
		}
	}
	
	/**
	 * 重置我的空间日记最后更新时间
	 */
	public void resetMyZoneLastDiaryTime(String last_diary_time){
		diaryDataEntities.myZoneLastTime=last_diary_time;
		//TODO 清除我的空间所有本地数据
	}
	
	/**
	 * 更新我的空间最新时间和最旧时间
	 */
	public void updateMyZoneDiaryTime(String first_diary_time,String last_diary_time){
		if(DateUtils.isNum(first_diary_time)){
			if(DateUtils.isNum(diaryDataEntities.myZoneFirstTime)){
				if(Long.parseLong(first_diary_time)>Long.parseLong(diaryDataEntities.myZoneFirstTime)){
					diaryDataEntities.myZoneFirstTime=first_diary_time;
				}
			}else{
				diaryDataEntities.myZoneFirstTime=first_diary_time;
			}
		}
		
		if(DateUtils.isNum(last_diary_time)){
			if(DateUtils.isNum(diaryDataEntities.myZoneLastTime)){
				if(Long.parseLong(last_diary_time)<Long.parseLong(diaryDataEntities.myZoneLastTime)){
					diaryDataEntities.myZoneLastTime=last_diary_time;
				}
			}else{
				diaryDataEntities.myZoneLastTime=last_diary_time;
			}
		}
	}
	
	/**
	 * 根据uuid找到日记组并替换日记组ID
	 */
	public void updateDiaryGroupIDByUUID(String diaryID,String diaryUUID){
		MyDiaryList diaryGroup=findDiaryGroupByUUID(diaryUUID);
		if(diaryGroup!=null)diaryGroup.diaryid=diaryID;
	}
	
	/**
	 * 更新用户信息
	 */
	public void updateUserInfo(String headimageurl,String background,String nickname,String sex,String signature){
		if(headimageurl!=null&&headimageurl.length()>0&&!headimageurl.equals(accountInfo.headimageurl)){
			accountInfo.headimageurl=headimageurl;
		}
		if(background!=null&&background.length()>0&&!background.equals(accountInfo.zoneBackGround)){
			accountInfo.zoneBackGround=background;
		}
		if(nickname!=null&&nickname.length()>0&&!nickname.equals(accountInfo.nickname)){
			accountInfo.nickname=nickname;
		}
		if(sex!=null&&sex.length()>0&&!sex.equals(accountInfo.sex)){
			accountInfo.sex=sex;
		}
		if(signature!=null&&signature.length()>0&&!signature.equals(accountInfo.signature)){
			accountInfo.signature=signature;
		}
	}

	/**
	 * 保存日记组数据信息 如果本地已存在，则用新的替换本地的
	 */
	public void saveDiaryGroup(MyDiaryList... diaryGroup) {
		if (null == diaryGroup) {
			Log.d(TAG, "diaryGroup is null");
			return;
		}
		for (int i = 0; i < diaryGroup.length; i++) {
			MyDiaryList myDiaryGroup = diaryGroup[i];
			if (myDiaryGroup != null) {
				MyDiaryList localMyDiaryGroup = findDiaryGroupByUUID(myDiaryGroup.diaryuuid);
				if (localMyDiaryGroup != null) {
					diaryDataEntities.dairyGroupList.remove(localMyDiaryGroup);
				}
				MyDiary myDiary=findMyDiaryByUUID(myDiaryGroup.diaryuuid);
				if(myDiary!=null&&(null==myDiaryGroup.shareinfo||0==myDiaryGroup.shareinfo.length))
					myDiaryGroup.shareinfo=myDiary.shareinfo;
				diaryDataEntities.dairyGroupList.add(myDiaryGroup);
			}
		}
		sortDiaryGroup();
		
		notifyMyDiaryChanged();
	}

	/**
	 * 根据日记组uuid找日记组
	 */
	public MyDiaryList findDiaryGroupByUUID(String diaryGroupUUID) {
		for (int i = 0; i < diaryDataEntities.dairyGroupList.size(); i++) {
			MyDiaryList myDiaryGroup = diaryDataEntities.dairyGroupList.get(i);
			if (myDiaryGroup != null && diaryGroupUUID != null
					&& diaryGroupUUID.length() > 0
					&& diaryGroupUUID.equals(myDiaryGroup.diaryuuid))
				return myDiaryGroup;
		}
		return null;
	}
	
	/**
	 * 获取最近一条本地日记
	 * @return
	 */
	public MyDiary findResentOneDiary(){
		for (int i = 0; i < diaryDataEntities.dairyGroupList.size(); i++) {
			MyDiaryList myDiaryGroup = diaryDataEntities.dairyGroupList.get(i);
			return findMyDiaryByUUID(myDiaryGroup.diaryuuid);
		}
		return null;
	}
	
	/**
	 * 根据日记组id找日记组
	 */
	public MyDiaryList findDiaryGroupByID(String diaryGroupID) {
		for (int i = 0; i < diaryDataEntities.dairyGroupList.size(); i++) {
			MyDiaryList myDiaryGroup = diaryDataEntities.dairyGroupList.get(i);
			if (myDiaryGroup != null && diaryGroupID != null
					&& diaryGroupID.length() > 0
					&& diaryGroupID.equals(myDiaryGroup.diaryid))
				return myDiaryGroup;
		}
		return null;
	}
	
	/**
	 * 根据给定的日记组uuid找到日记组id，都号分隔,如果id为空时，不返回
	 */
	public String getDiaryGroupIDsByUUIDs(String uuids){
		String diaryIDs="";
		if(null==uuids||0==uuids.length())return diaryIDs;
		String[] uuidList=uuids.split(",");
		for(int i=0;i<uuidList.length;i++){
			MyDiaryList diaryGroup=findDiaryGroupByUUID(uuidList[i]);
			if(diaryGroup!=null&&diaryGroup.diaryid!=null&&diaryGroup.diaryid.length()>0){
				diaryIDs+=diaryGroup.diaryid;
				if(i<uuidList.length-1)
					diaryIDs+=",";
			}
		}
		return diaryIDs;
	}
	
	/**
	 * 根据给定的日记组uuid找到日记id，都号分隔,如果id为空时，不返回
	 */
	public String getDiaryIDsByUUIDs(String uuids){
		String diaryIDs="";
		if(null==uuids||0==uuids.length())return diaryIDs;
		String[] uuidList=uuids.split(",");
		for(int i=0;i<uuidList.length;i++){
			MyDiary myDiary=findMyDiaryByUUID(uuidList[i]);
			if(myDiary!=null&&myDiary.diaryid!=null&&myDiary.diaryid.length()>0){
				diaryIDs+=myDiary.diaryid;
				if(i<uuidList.length-1)
					diaryIDs+=",";
			}
		}
		return diaryIDs;
	}
	
	ArrayList<MyDiaryList> mDairyPreviewList = null;
	private int detailType;
	
	/**
	 * 进入详情页（预览页）是调用 
	 * type 0-我的空间 1-保险箱 2-他人空间
	 * 
	 */
	public void setDetailDiaryList(ArrayList<MyDiaryList> list, int detailType){
		mDairyPreviewList = list;
		this.detailType = detailType;
	}
	
	/**
	 * 足迹获取日记列表
	 */
	private ArrayList<MyDiary> footmarkDiaries=null;
	public void setFootmarkDiary(ArrayList<MyDiary> list){
		this.footmarkDiaries=list;
	}
	
	public ArrayList<MyDiary> getFootmarkDiaryList(){
		return footmarkDiaries;
	}
	
	private ArrayList<MyDiary> previewDiaries=null;
	public void setDetailDiary(ArrayList<MyDiary> list){
		this.previewDiaries=list;
	}
	
	/**
	 * detailType 类型为2时，获取他人空间页日记
	 */
	public ArrayList<MyDiary> getDiaryList(){
		return previewDiaries;
	}
	
	/**
	 * 获取进入详情页时传入的入口类型
	 */
	public int getDetailDiaryListType(){
		return detailType;
	}
	
	/**
	 * 根据进入详情页的入口类型取出日记列表
	 */
	public ArrayList<MyDiaryList> getDetailDiaryList(){
		if(mDairyPreviewList == null)
		{
			mDairyPreviewList = new ArrayList<MyDiaryList>();
		}
		switch (getDetailDiaryListType()) {
		case 0:
			
			break;

		default:
			
			break;
		}
		
		return mDairyPreviewList;
	}

	/**
	 * 保存日记数据信息
	 */
	public void saveDiaries(List<MyDiaryList> diaryGroups,
			List<MyDiary> diaries) {
		saveDiary(diaries.toArray(new MyDiary[diaries.size()]));
		saveDiaryGroup(diaryGroups.toArray(new MyDiaryList[diaryGroups.size()]));
	}
	
	//日记更新通知
	public void notifyMyDiaryChanged(){
		if(myZoneDataChangedListener!=null)
			myZoneDataChangedListener.dataChanged();
	}
	
	//保险箱日记更新通知
	public void notifyMySafeboxChanged(){
		if(mySafeboxMeDataChangedListener!=null)
			mySafeboxMeDataChangedListener.safeboxDataChanged();
	}

	/**
	 * 添加单个日记组
	 */
	public void saveDiaries(MyDiaryList diaryGroup, MyDiary myDiary) {
		saveDiary(myDiary);
		saveDiaryGroup(diaryGroup);
	}

	/**
	 * 保存日记
	 */
	public void saveDiary(MyDiary... myDiaries) {
		if (null == myDiaries) {
			Log.e(TAG, "myDiaries is null");
		}
		for (int i = 0; i < myDiaries.length; i++) {
			MyDiary myDiary = myDiaries[i];
			MyDiary localDiary = SqliteDairyManager.getInstance().getDiaryByUUID(myDiary.diaryuuid);
			if(localDiary != null&&!localDiary.isSychorized())continue;
			if(localDiary!=null)
				localDiary.replaceMediaMapping(myDiary);
			SqliteDairyManager.getInstance().putDiary(myDiary);
		}
	}
	
	private void replaceParams(MyDiary newDiary,MyDiary oldDiary){
		newDiary.request=oldDiary.request;
		if(null==newDiary.tags||0==newDiary.tags.length)
			newDiary.tags=oldDiary.tags;
	}
	
	/**
	 * 更新日记sync_status状态
	 */
	public void updateDiarySyncStatus(int sync_status,MyDiary...myDiaries){
		if(null==myDiaries)return;
		for(int i=0;i<myDiaries.length;i++){
			if(myDiaries[i]!=null)
				myDiaries[i].sync_status=sync_status;
		}
	}
	
	/**
	 * 更新日记
	 */
	public void updateDiary(MyDiary... myDiaries){
		if (null == myDiaries) {
			Log.e(TAG, "myDiaries is null");
		}
		for (int i = 0; i < myDiaries.length; i++) {
			MyDiary myDiary = myDiaries[i];
			MyDiary localDiary = findMyDiaryByUUID(myDiary.diaryuuid);
			if (localDiary != null) {
//				replaceParams(myDiary,localDiary);
				localDiary.replaceMediaMapping(myDiary);
/*				synchronized (diaryDataEntities.diariesMap) {
					diaryDataEntities.diariesMap.remove(myDiary.diaryuuid);
				}*/
				SqliteDairyManager.getInstance().removeDiaryByUUID(myDiary.diaryuuid);

			}
			
/*			synchronized (diaryDataEntities.diariesMap) {
				diaryDataEntities.diariesMap.put(myDiary.diaryuuid, myDiary);
			}*/
			SqliteDairyManager.getInstance().putDiary(myDiary);
			Intent mIntent=new Intent(DiaryPreviewActivity.DIARY_EDIT_REFRESH);
			mIntent.putExtra(DiaryPreviewActivity.INTENT_ACTION_DIARY_UUID, myDiary.diaryuuid);
			LocalBroadcastManager.getInstance(null).sendBroadcast(mIntent);
		}
	}
	
	/**
	 * 根据日记uuid找日记, 请使用SqliteDairyManager.getInstance().getDiaryByUUID(String)
	 */
	public MyDiary findMyDiaryByUUID(String diaryUUID) {
/*		if (diaryUUID != null && diaryUUID.length() > 0){
			synchronized (diaryDataEntities.diariesMap) {
				return diaryDataEntities.diariesMap.get(diaryUUID);
			}
		}*/

		return SqliteDairyManager.getInstance().getDiaryByUUID(diaryUUID);
	}
	
/*	public Map<String,MyDiary> getDiaryMap() {
		return diaryDataEntities.diariesMap;
	}*/
	
	public MyDiary[] getDiaries(long ms) {
		ArrayList<MyDiary> diaries = new ArrayList<MyDiary>();
		ArrayList<MyDiary> footmarkdiaries = getFootmarkDiaryList();
		if(footmarkdiaries != null && footmarkdiaries.size() > 0)
		for(MyDiary myDiary : getFootmarkDiaryList()) {
			if(myDiary != null) {
				String shoottime = ZStringUtils.emptyToNull(myDiary.shoottime);
				if(shoottime == null) shoottime = "0";
				try {
					if(myDiary.join_safebox.equals("1") || myDiary.isDuplicated() ||
							!(ZStringUtils.nullToEmpty(myDiary.position_status)).equals("1") ||
							Long.parseLong(shoottime) < ms || myDiary.isMiShareDiary()) {
						continue;
					} 
					diaries.add(myDiary);
				} catch(NumberFormatException e) {
					e.printStackTrace();
				}
			}
		}
		Log.d(TAG, "Got " + diaries.size() + " diaries");
		return (MyDiary[]) diaries.toArray(new MyDiary[diaries.size()]);
		
//		return SqliteDairyManager.getInstance().getDiarys(ms);
/*		//Map<String, MyDiary> diaryMap = getDiaryMap();
		ArrayList<MyDiary> diaries = new ArrayList<MyDiary>();
		if (diaryDataEntities.diariesMap != null && diaryDataEntities.diariesMap.size() > 0) {
			synchronized (diaryDataEntities.diariesMap) {
				Iterator<Entry<String, MyDiary>> iter = diaryDataEntities.diariesMap.entrySet().iterator();
				while (iter.hasNext()) {
					Map.Entry<String, MyDiary> entry = (Map.Entry<String, MyDiary>) iter.next();
					MyDiary myDiary = entry.getValue();
					if(myDiary != null) {
//						Log.d(TAG, "filter time = " + ms + ";diaries millitime = " + myDiary.diarytimemilli);
						if(myDiary.join_safebox.equals("1") || myDiary.isDuplicated()
								|| Long.parseLong(myDiary.diarytimemilli) < ms) {
							continue;
						}
						diaries.add(myDiary);
					}
				}
			}

		}
		Log.d(TAG, "Got " + diaries.size() + " diaries");
		return (MyDiary[]) diaries.toArray(new MyDiary[diaries.size()]);*/
	}

	// 根据日记id找日记
	public MyDiary findMyDiaryByDiaryID(String diaryID) {
		return SqliteDairyManager.getInstance().getDiaryByID(diaryID);
/*		synchronized (diaryDataEntities.diariesMap) {
			Iterator<Entry<String, MyDiary>> iter = diaryDataEntities.diariesMap.entrySet().iterator();
			while (iter.hasNext()) {
				Map.Entry<String, MyDiary> entry = (Map.Entry<String, MyDiary>) iter
						.next();
				MyDiary myDiary = entry.getValue();
				if(myDiary!=null&&myDiary.diaryid!=null&&myDiary.diaryid.equals(diaryID))
					return myDiary;
			}
			return null;
		}*/
	}

	/**
	 * 根据日记uuid删除日记,网络任务清理未上传
	 */
	public void removeDiaryByUUIDFromNetworkTask(String diaryUUID) {
		MyDiary myDiary = SqliteDairyManager.getInstance().getDiaryByUUID(diaryUUID);

		if(myDiary!=null){
			// 删除单个日记组
			removeDiaryGroupByUUID(diaryUUID);
			
			// 从包含该日记的组中移出该日记信息
			removeDiaryFromDiaryGroup(myDiary.diaryid);
			
			SqliteDairyManager.getInstance().removeDiaryByUUID(diaryUUID);
			myDiary.clear();
		}

		
		
/*		MyDiary myDiary = findMyDiaryByUUID(diaryUUID);
		if (myDiary != null){
			synchronized (diaryDataEntities.diariesMap) {
				diaryDataEntities.diariesMap.remove(diaryUUID);
			}

			SqliteDairyManager.getInstance().removeDiaryByUUID(diaryUUID);
			
			// 删除单个日记组
			removeDiaryGroupByUUID(myDiary.diaryuuid);
			// 从包含该日记的组中移出该日记信息
			removeDiaryFromDiaryGroup(myDiary.diaryid);
		}*/
	}
	
	/**
	 * 根据日记uuid删除日记
	 */
	public void removeDiaryByUUID(String diaryUUID) {
		MyDiary myDiary = SqliteDairyManager.getInstance().getDiaryByUUID(diaryUUID);

		if(myDiary!=null){
			NetworkTaskManager.getInstance(myUserID).removeTask(myDiary.diaryuuid);
			// 删除单个日记组
			removeDiaryGroupByUUID(diaryUUID);
			
			// 从包含该日记的组中移出该日记信息
			removeDiaryFromDiaryGroup(myDiary.diaryid);
			SqliteDairyManager.getInstance().removeDiaryByUUID(diaryUUID);
			myDiary.clear();
		}
/*		MyDiary myDiary = findMyDiaryByUUID(diaryUUID);
		if (myDiary != null){
			synchronized (diaryDataEntities.diariesMap) {
				diaryDataEntities.diariesMap.remove(diaryUUID);
			}

			SqliteDairyManager.getInstance().removeDiary(myDiary.diaryuuid);
			// 删除单个日记组
			removeDiaryGroupByUUID(myDiary.diaryuuid);
			// 从包含该日记的组中移出该日记信息
			removeDiaryFromDiaryGroup(myDiary.diaryid);
			NetworkTaskManager.getInstance(myUserID).removeTask(myDiary.diaryuuid);
		}*/
	}
	
	//根据日记id删除日记
	private void removeDiaryByID(String diaryID){
		MyDiary myDiary = SqliteDairyManager.getInstance().getDiaryByID(diaryID);

		if(myDiary!=null){
			// 删除单个日记组
			removeDiaryGroupByUUID(myDiary.diaryuuid);
			
			// 从包含该日记的组中移出该日记信息
			removeDiaryFromDiaryGroup(myDiary.diaryid);
			
			NetworkTaskManager.getInstance(myUserID).removeTask(myDiary.diaryuuid);
			
			SqliteDairyManager.getInstance().removeDiaryByID(diaryID);
			myDiary.clear();
		}
/*		MyDiary myDiary = findMyDiaryByDiaryID(diaryID);
		if (myDiary != null){
			synchronized (diaryDataEntities.diariesMap) {
				diaryDataEntities.diariesMap.remove(myDiary.diaryuuid);
			}
			// 删除单个日记组
			removeDiaryGroupByUUID(myDiary.diaryuuid);
			// 从包含该日记的组中移出该日记信息
			removeDiaryFromDiaryGroup(myDiary.diaryid);
			NetworkTaskManager.getInstance(myUserID).removeTask(myDiary.diaryuuid);
		}*/
	}
	
	/**
	 * 清除diaryGroupList中包含的日记组信息
	 */
	public void removeDiaryGroupByDiaryGroupList(ArrayList<MyDiaryList> diaryGroupList){
		if(diaryGroupList!=null){
			for(int i=0;i<diaryGroupList.size();i++){
				MyDiaryList myDiaryList=diaryGroupList.get(i);
				if("1".equals(myDiaryList.isgroup)){
					diaryDataEntities.dairyGroupList.remove(myDiaryList);
				}else{
					removeDiaryByUUID(myDiaryList.diaryuuid);
				}
			}
		}
	}

	/**
	 * 根据日记组uuid删除日记组
	 */
	private void removeDiaryGroupByUUID(String diaryGroupUUID) {
		MyDiaryList myDiaryGroup = findDiaryGroupByUUID(diaryGroupUUID);
		if (myDiaryGroup != null)
			diaryDataEntities.dairyGroupList.remove(myDiaryGroup);
	}
	
	/**
	 * 根据日记ID删除日记
	 * removeDiaryids 如果是多个日记id，已逗号分隔
	 */
	public void removeDiaryByIDs(String removeDiaryids){
		if(removeDiaryids!=null&&removeDiaryids.length()>0){
			String[] diaryids=removeDiaryids.split(",");
			for(int i=0;i<diaryids.length;i++){
				removeDiaryByID(diaryids[i]);
			}
		}
	}
	
	/**
	 * 获取本地所有日记数
	 * @return
	 */
	public int getLocalDiarySum(){
		return diaryDataEntities.dairyGroupList.size();
	}

	/**
	 * 根据日记id从包含该日记的组中移出该日记信息
	 */
	private void removeDiaryFromDiaryGroup(String diaryID) {
		if (diaryID != null && diaryID.length() > 0) {
			for (int i = 0; i < diaryDataEntities.dairyGroupList.size(); i++) {
				MyDiaryList diaryGroup = diaryDataEntities.dairyGroupList
						.get(i);
				if ("1".equals(diaryGroup.isgroup)
						&& diaryGroup.contain != null
						&& diaryGroup.contain.length() > 0) {
					diaryGroup.contain=diaryGroup.contain.replace(diaryID, "");
					// 当此组中不包含日记时，删除此日记组
					if (diaryGroup.contain.matches(",*")) {
						diaryDataEntities.dairyGroupList.remove(i);
						return;
					}
					if(diaryGroup.contain.startsWith(","))
						diaryGroup.contain=diaryGroup.contain.replaceFirst(",", "");
					if(diaryGroup.contain.contains(",,"))
						diaryGroup.contain=diaryGroup.contain.replaceFirst(",,", ",");
				}
			}
		}
	}

	/**
	 * 获取我的空间本地最新日记更新时间
	 */
	public String getMyZoneFirstTime() {
		return diaryDataEntities.myZoneFirstTime;
	}

	/**
	 * 获取我的空间本地最旧日记更新时间
	 */
	public String getMyZoneLastTime() {
		return diaryDataEntities.myZoneLastTime;
	}

	/**
	 * 获取保险箱list列表
	 */
	public ArrayList<MyZoneItem> getMySafeboxItems(int index) {
		ArrayList<MyZoneItem> itemList = new ArrayList<MyZoneItem>();
		String lastDate = "";
		String lastCreateTime="";
		DiariesItem diariesItem = null;
		Date today=new Date();
		for (int i = 0; i < diaryDataEntities.dairyGroupList.size(); i++) {
			MyDiaryList diaryGroup = diaryDataEntities.dairyGroupList.get(i);
			if("0".equals(diaryGroup.join_safebox))continue;
			String date=getMyZoneShowDate(diaryGroup.create_time,today);
			if("".equals(lastDate)||!lastDate.equals(date)||(diariesItem!=null&&diariesItem.diaryGroups.size()>2)){
				if(itemList.size()==PAGE_SIZE*(index+1))break;
				diariesItem = new DiariesItem();
				itemList.add(diariesItem);
			}
			diariesItem.strDate = date;
			diariesItem.textStyle=getMyZoneShowDateStyle(lastCreateTime, diaryGroup.create_time, today);
			if("1".equals(diaryGroup.isgroup)){
				diariesItem.diaries.add(getDiariesByDiaryIDs(diaryGroup.contain));
			}else{
				diariesItem.diaries.add(getDiariesByUUID(diaryGroup.diaryuuid));
			}
			diariesItem.diaryGroups.add(diaryGroup);
			lastDate = date;
			lastCreateTime=diaryGroup.create_time;
		}
		
		return itemList;
	}
	
	/**
	 * 获取我的空间页list列表
	 */
	public ArrayList<MyZoneItem> getMyZoneItems(int index) {
		ArrayList<MyZoneItem> itemList = new ArrayList<MyZoneItem>();
		// 获取本地用户信息
		UserInfo userInfo = new UserInfo();
		if (accountInfo != null) {
			userInfo.headUrl = accountInfo.headimageurl;
			userInfo.backgroundUrl = accountInfo.zoneBackGround;
			userInfo.nickname = accountInfo.nickname;
			userInfo.signature = accountInfo.signature;
			userInfo.sex = accountInfo.sex;
			userInfo.weathertype = accountInfo.weathertype;
			userInfo.myweather = accountInfo.myWeather;
			userInfo.msgNum = accountInfo.privateMsgManger.getUnReadCommentNum();;
		}
		itemList.add(userInfo);
		
		String lastDate = "";
		String lastCreateTime="";
		DiariesItem diariesItem = null;
		Date today=new Date();
		for (int i = 0; i < diaryDataEntities.dairyGroupList.size(); i++) {
			// 按天分组
			MyDiaryList diaryGroup = diaryDataEntities.dairyGroupList.get(i);
			if("1".equals(diaryGroup.join_safebox))continue;
			String date=getMyZoneShowDate(diaryGroup.create_time,today);
			//
			if("".equals(lastDate)||!lastDate.equals(date)||(diariesItem!=null&&diariesItem.diaryGroups.size()>2)){
				if(itemList.size()==PAGE_SIZE*(index+1))break;
				diariesItem = new DiariesItem();
				itemList.add(diariesItem);
			}
			diariesItem.strDate = date;
			diariesItem.textStyle=getMyZoneShowDateStyle(lastCreateTime, diaryGroup.create_time, today);
			if("1".equals(diaryGroup.isgroup)){
				diariesItem.diaries.add(getDiariesByDiaryIDs(diaryGroup.contain));
			}else{
				diariesItem.diaries.add(getDiariesByUUID(diaryGroup.diaryuuid));
			}
			diariesItem.diaryGroups.add(diaryGroup);
			lastDate = date;
			lastCreateTime=diaryGroup.create_time;
		}

		return itemList;
	}
	
	
	
	/**
	 * 获取我的空间页list列表
	 */
	public ArrayList<MyZoneItem> getShareZoneItems(int index) {
		ArrayList<MyZoneItem> itemList = new ArrayList<MyZoneItem>();
		int count = 0;
		
		String lastDate = "";
		DiariesItem diariesItem = null;
		Date today=new Date();
		for (int i = 0; i < diaryDataEntities.dairyGroupList.size(); i++) {
			// 按天分组
			MyDiaryList diaryGroup = diaryDataEntities.dairyGroupList.get(i);
			if("1".equals(diaryGroup.join_safebox))continue;
			// 需求变更 不显示组内容
			if("1".equals(diaryGroup.isgroup))continue;
			String date=getMyZoneShowDate(diaryGroup.create_time,today);
			if("".equals(lastDate)||!lastDate.equals(date)||(diariesItem!=null&&diariesItem.diaryGroups.size()>2)){
//				if(itemList.size()==PAGE_SIZE*(index+1))break;
				diariesItem = new DiariesItem();
				itemList.add(diariesItem);
			}
			diariesItem.strDate = date;
			if("1".equals(diaryGroup.isgroup)){
				diariesItem.diaries.add(getDiariesByDiaryIDs(diaryGroup.contain));
			}else{
//				MyDiary[] tmp = getDiariesByUUID(diaryGroup.diaryuuid);
//				// 没有diaryid的日记不显示
//				if(tmp[0] != null && tmp[0].diaryid != null && !tmp[0].diaryid.equals(""))
//				{
					diariesItem.diaries.add(getDiariesByUUID(diaryGroup.diaryuuid));
//				}
			}
			diariesItem.diaryGroups.add(diaryGroup);
			lastDate = date;
		}

		return itemList;
	}
	
	//获取空间页日记显示格式时间
	public String getMyZoneShowDate(String timemilli,Date today){
		String year=DateUtils.dateToString(today, "yyyy");
		if(year.equals(DateUtils.getStringFromMilli(timemilli,"yyyy"))){
			String day=DateUtils.dateToString(today, DateUtils.DATE_FORMAT_NORMAL_1);
			String yesterday=DateUtils.getStringFromMilli((today.getTime()-24l*3600l*1000l)+"", DateUtils.DATE_FORMAT_NORMAL_1);
			if(day.equals(DateUtils.getStringFromMilli(timemilli, DateUtils.DATE_FORMAT_NORMAL_1))){
				return "今天";
			}else if(yesterday.equals(DateUtils.getStringFromMilli(timemilli, DateUtils.DATE_FORMAT_NORMAL_1))){
				return "昨天";
			}else{
				return DateUtils.getStringFromMilli(timemilli, DateUtils.DATE_FORMAT_DAY_WEEK);
			}
		}else{
			return DateUtils.getStringFromMilli(timemilli,
					DateUtils.DATE_FORMAT_YEAR_DAY);
		}
	}
	
	public SpannableStringBuilder getMyZoneShowDateStyle(String lastCreateTime,String nowCreateTime,Date today){
		String year=DateUtils.dateToString(today, "yyyy");
		SpannableStringBuilder style=new SpannableStringBuilder();
		if(year.equals(DateUtils.getStringFromMilli(nowCreateTime,"yyyy"))){
			String day=DateUtils.dateToString(today, DateUtils.DATE_FORMAT_NORMAL_1);
			String yesterday=DateUtils.getStringFromMilli((today.getTime()-24l*3600l*1000l)+"", DateUtils.DATE_FORMAT_NORMAL_1);
//			int weekday=new Date(Long.parseLong(nowCreateTime)).getDay();
//			int weekdays=7-weekday;
			if(day.equals(DateUtils.getStringFromMilli(nowCreateTime, DateUtils.DATE_FORMAT_NORMAL_1))){
				style.append("今天");
				style.setSpan(as, 0, 2, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
			}else if(yesterday.equals(DateUtils.getStringFromMilli(nowCreateTime, DateUtils.DATE_FORMAT_NORMAL_1))){
				style.append("昨天");
				style.setSpan(as, 0, 2, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
			}else if(today.getTime()-new Date(Long.parseLong(nowCreateTime)).getTime()<7*24*3600*1000L){//显示星期几的天数
//				String d=DateUtils.getStringFromMilli(nowCreateTime, "E");
				String d=DateUtils.getStringweekday(nowCreateTime);
				style.append(d);
				style.setSpan(as, 2, 3, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
			}else{
				String d=DateUtils.getStringFromMilli(nowCreateTime, DateUtils.DATE_FORMAT_DAY_1);
				getMonthAndDayStyle(d,style,0);
			}
		}else{
			String lastYear=DateUtils.getStringFromMilli(nowCreateTime,"yyyy年");
			String lastYearAppence=DateUtils.getStringFromMilli(lastCreateTime,"yyyy年");
			String lastMD=DateUtils.getStringFromMilli(nowCreateTime,DateUtils.DATE_FORMAT_DAY_1);
//			if("".equals(lastCreateTime)||!lastYearAppence.equals(lastYear)){
//				style.append(lastYear);
//				style.append("\n");
//				int yearLen=style.length();
//				getMonthAndDayStyle(lastMD,style,yearLen);
//			}else{
//			}
			getMonthAndDayStyle(lastMD,style,0);
		}
		return style;
	}
	
	private AbsoluteSizeSpan as=new AbsoluteSizeSpan(DisplayUtil.sp2dp(MainApplication.getAppInstance(), 25), true);
	private SpannableStringBuilder getMonthAndDayStyle(String d,SpannableStringBuilder style,int offset){
		if('0'==d.charAt(3)){//天<10
			if(d.startsWith("0")){//月份<10
				d=d.replaceAll("0", "");
				style.append(d);
				style.setSpan(as, offset+2, offset+3, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
			}else{//月份>10 去掉天前面的0
				d=d.substring(0, 3)+d.substring(4, 5);
				style.append(d);
				style.setSpan(as, offset+3, offset+4, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
			}
		}else{//天>10
			if(d.startsWith("0")){//月份<10
				d=d.replaceFirst("0", "");
				style.append(d);
				style.setSpan(as, offset+2, offset+4, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
			}else{//月份>10
				style.append(d);
				style.setSpan(as, offset+3, offset+5, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
			}
		}
		return style;
	}
	
	//根据日记uuid返回日记数组
	private MyDiary[] getDiariesByUUID(String... uuids){
		if(uuids!=null){
			MyDiary[] myDiaries=new MyDiary[uuids.length];
			for(int i=0;i<myDiaries.length;i++){
				myDiaries[i]=findMyDiaryByUUID(uuids[i]);
			}
			return myDiaries;
		}
		return null;
	}
	
	//根据日记id返回日记数组  diaryIDs已逗号分隔
	private MyDiary[] getDiariesByDiaryIDs(String diaryIDs){
		if(diaryIDs!=null&&diaryIDs.length()>0){
			String[] diaryIDList=diaryIDs.split(",");
			if(diaryIDList!=null){
				MyDiary[] myDiaries=new MyDiary[diaryIDList.length];
				for(int i=0;i<myDiaries.length;i++){
					myDiaries[i]=findMyDiaryByDiaryID(diaryIDList[i]);
				}
				return myDiaries;
			}
		}
		return null;
	}
	
	/**
	 * 将给定的日记组加入保险箱，如果是单个日记，则同时修改日记保险箱状态
	 */
	public void addSafebox(ArrayList<MyDiaryList> diaryGroups){
		if(null==diaryGroups)return;
		for(int i=0;i<diaryGroups.size();i++){
			MyDiaryList diaryGroup=diaryGroups.get(i);
			diaryGroup.join_safebox="1";
			MyDiary myDiary=findMyDiaryByUUID(diaryGroup.diaryuuid);
			if(myDiary!=null)
				myDiary.join_safebox="1";
		}
		//通知保险箱日记更新
		notifyMySafeboxChanged();
	}
	
	
	/**
	 * 注销保险箱时移出所有保险箱日记
	 */
	public void removeAllSafebox(){
		for(int i=0;i<diaryDataEntities.dairyGroupList.size();i++){
			MyDiaryList diaryGroup=diaryDataEntities.dairyGroupList.get(i);
			if(diaryGroup!=null){
				MyDiary myDiary=findMyDiaryByUUID(diaryGroup.diaryuuid);
				if(myDiary!=null){
					if("1".equals(myDiary.join_safebox)){
						myDiary.join_safebox="0";
						diaryGroup.join_safebox="0";
					}
				}else{
					Log.e(TAG, "myDiary is null");
				}
			}else{
				Log.e(TAG, "diaryGroup is null");
			}
		}
		notifyMySafeboxChanged();
		notifyMyDiaryChanged();
	}
	
	/**
	 * 将注销之前保险箱中的日记重新加入保险箱
	 *//*
	public void addPreviewsToSafebox(){
		for(int i=0;i<diaryDataEntities.dairyGroupList.size();i++){
			MyDiaryList diaryGroup=diaryDataEntities.dairyGroupList.get(i);
			MyDiary myDiary=findMyDiaryByUUID(diaryGroup.diaryuuid);
			if(myDiary!=null){
				if("2".equals(myDiary.join_safebox)){
					myDiary.join_safebox="1";
					diaryGroup.join_safebox="1";
				}
			}else{
				Log.d(TAG, "myDiary is null");
			}
		}
		notifyMySafeboxChanged();
		notifyMyDiaryChanged();
	}*/
	
	/**
	 * 将给定的日记组移出保险箱，如果是单个日记，则同时修改日记保险箱状态
	 */
	public void removeSafebox(ArrayList<MyDiaryList> diaryGroups){
		if(null==diaryGroups)return;
		for(int i=0;i<diaryGroups.size();i++){
			MyDiaryList diaryGroup=diaryGroups.get(i);
			diaryGroup.join_safebox="0";
//			if(!"1".equals(diaryGroup.isgroup)){
//			}
			MyDiary myDiary=findMyDiaryByUUID(diaryGroup.diaryuuid);
			if(myDiary!=null){
				myDiary.join_safebox="0";
			}else{
				Log.d(TAG, "myDiary is null");
			}
		}
	}

	/**
	 * 根据原有日记列表更新保险箱日记
	 * lastTime 取出lastTime时间之后的所有日记
	 */
	public ArrayList<MyZoneItem> updateSafeboxItems(
			long lastTime) {
		ArrayList<MyZoneItem> itemList = new ArrayList<MyZoneItem>();
		if(0==lastTime)
			return getMyZoneItems(0);
		int count=0;
		String lastDate = "";
		String lastCreateTime="";
		DiariesItem diariesItem = null;
		Date today=new Date();
		for (int i = 0; i < diaryDataEntities.dairyGroupList.size(); i++) {
			// 按天分组
			MyDiaryList diaryGroup = diaryDataEntities.dairyGroupList.get(i);
			if("0".equals(diaryGroup.join_safebox))continue;
			if(!DateUtils.isNum(diaryGroup.create_time)){
				Log.e(TAG, "diaryGroup.create_time is not a number");
				continue;
			}
			String date = getMyZoneShowDate(diaryGroup.create_time,today);
			if("".equals(lastDate)||!lastDate.equals(date)||(diariesItem!=null&&diariesItem.diaryGroups.size()>2)){
				if (Long.parseLong(diaryGroup.create_time)<lastTime||count >= PAGE_SIZE)break;
				diariesItem = new DiariesItem();
				itemList.add(diariesItem);
			}
			diariesItem.strDate = date;
			diariesItem.textStyle=getMyZoneShowDateStyle(lastCreateTime,diaryGroup.create_time,today);
			if("1".equals(diaryGroup.isgroup)){
				diariesItem.diaries.add(getDiariesByDiaryIDs(diaryGroup.contain));
			}else{
				diariesItem.diaries.add(getDiariesByUUID(diaryGroup.diaryuuid));
			}
			diariesItem.diaryGroups.add(diaryGroup);
			lastDate = date;
			lastCreateTime=diaryGroup.create_time;
		}
		return itemList;
	}
	
	/**
	 * 根据原有日记列表更新日记
	 * lastTime 取出lastTime时间之后的所有日记
	 */
	public ArrayList<MyZoneItem> updateMyZoneItems(
			long lastTime) {
		ArrayList<MyZoneItem> itemList = new ArrayList<MyZoneItem>();
		if(0==lastTime)
			return getMyZoneItems(0);
		int count=0;
		// 获取本地用户信息
		UserInfo userInfo = new UserInfo();
		if (accountInfo != null) {
			userInfo.headUrl = accountInfo.headimageurl;
			userInfo.backgroundUrl = accountInfo.zoneBackGround;
			userInfo.nickname = accountInfo.nickname;
			userInfo.signature = accountInfo.signature;
			userInfo.sex = accountInfo.sex;
			userInfo.weathertype = accountInfo.weathertype;
			userInfo.myweather = accountInfo.myWeather;
			userInfo.msgNum = accountInfo.privateMsgManger.getUnReadCommentNum();;
		}
		count++;
		itemList.add(userInfo);
					
		String lastDate = "";
		String lastCreateTime="";
		DiariesItem diariesItem = null;
		Date today=new Date();
		for (int i = 0; i < diaryDataEntities.dairyGroupList.size(); i++) {
			// 按天分组
			MyDiaryList diaryGroup = diaryDataEntities.dairyGroupList.get(i);
			if("1".equals(diaryGroup.join_safebox))continue;
			if(!DateUtils.isNum(diaryGroup.create_time)){
				Log.e(TAG, "diaryGroup.create_time is not a number");
				continue;
			}
			String date = getMyZoneShowDate(diaryGroup.create_time,today);
			if (Long.parseLong(diaryGroup.create_time)<lastTime||count >= PAGE_SIZE)break;
			if("".equals(lastDate)||!lastDate.equals(date)||(diariesItem!=null&&diariesItem.diaryGroups.size()>2)){
				diariesItem = new DiariesItem();
				itemList.add(diariesItem);
			}
			diariesItem.strDate = date;
			diariesItem.textStyle=getMyZoneShowDateStyle(lastCreateTime,diaryGroup.create_time,today);
			if("1".equals(diaryGroup.isgroup)){
				diariesItem.diaries.add(getDiariesByDiaryIDs(diaryGroup.contain));
			}else{
				diariesItem.diaries.add(getDiariesByUUID(diaryGroup.diaryuuid));
			}
			diariesItem.diaryGroups.add(diaryGroup);
			lastDate = date;
			lastCreateTime=diaryGroup.create_time;
		}
		return itemList;
	}

	// 日记组排序
	private void sortDiaryGroup() {
		Collections.sort(diaryDataEntities.dairyGroupList,
				new DiaryGroupComparator());
	}

	public static class DiaryGroupComparator implements Comparator<MyDiaryList> {
		public int compare(MyDiaryList arg0, MyDiaryList arg1) {
			try {
				if (Long.parseLong(arg0.create_time) < Long
						.parseLong(arg1.create_time)) {
					return 1;
				}
				if (Long.parseLong(arg0.create_time) == Long
						.parseLong(arg1.create_time)) {
					return 0;
				}
			} catch (NumberFormatException e) {
				e.printStackTrace();
			}
			return -1;
		}
	}

	public static final int PAGE_SIZE = 10;

	private static final String TAG = DiaryManager.class.getSimpleName();

	private DiaryManager() {
	}

	private DiaryDataEntities diaryDataEntities;
	private AccountInfo accountInfo;
	private String myUserID;

	private void init() {
		myUserID = ActiveAccount.getInstance(MainApplication.getAppInstance())
				.getUID();
		if (myUserID != null) {
			accountInfo = AccountInfo.getInstance(myUserID);
			diaryDataEntities = accountInfo.dataEntities;
		} else {
			Log.e(TAG, "myUserID is null");
		}
	}

	private static DiaryManager diaryManager;

	public static synchronized DiaryManager getInstance() {
		if (null == diaryManager) {
			diaryManager = new DiaryManager();
		}
		diaryManager.init();
		return diaryManager;
	}
	
	private MyZoneDataChangedListener myZoneDataChangedListener;
	public void setMyZoneDataChangedListener(MyZoneDataChangedListener myZoneDataChangedListener){
		this.myZoneDataChangedListener=myZoneDataChangedListener;
	}
	
	private MySafeboxMeDataChangedListener mySafeboxMeDataChangedListener;
	public void setMySafeboxDataChangedListener(MySafeboxMeDataChangedListener mySafeboxMeDataChangedListener){
		this.mySafeboxMeDataChangedListener=mySafeboxMeDataChangedListener;
	}
	
	public interface MyZoneDataChangedListener{
		void dataChanged();
	}
	
	public interface MySafeboxMeDataChangedListener{
		void safeboxDataChanged();
	}
	
	/**
	 * 将日记ID添加到收藏表中
	 */
	public void addCollectDiaryID(String diaryid){
		boolean isExist=false;
		for(int i=0;i<diaryDataEntities.collectDiariesIDList.size();i++){
			MyDiaryids myDiaryids=diaryDataEntities.collectDiariesIDList.get(i);
			if(diaryid.equals(myDiaryids.diaryid)){
				isExist=true;
			}
		}
		if(!isExist){
			MyDiaryids diaryids=new GsonResponse3().new MyDiaryids();
			diaryids.diaryid=diaryid;
//			diaryids.publish_type="1";
			diaryDataEntities.collectDiariesIDList.add(diaryids);
		}
	}
	
	/**
	 * 将日记ID从收藏列表移出
	 */
	public void removeCollectDiaryID(String diaryid){
		for(int i=0;i<diaryDataEntities.collectDiariesIDList.size();i++){
			MyDiaryids myDiaryids=diaryDataEntities.collectDiariesIDList.get(i);
			if(diaryid.equals(myDiaryids.diaryid)){
				diaryDataEntities.collectDiariesIDList.remove(i);
				break;
			}
		}
	}
	
	/**
	 * 将日记ID添加到赞列表中
	 */
	public void addPraiseDiaryID(String diaryid, String publishid){
		boolean isExist=false;
		for(int i=0;i<diaryDataEntities.praisedDiariesIDList.size();i++){
			MyDiaryids myDiaryids=diaryDataEntities.praisedDiariesIDList.get(i);
			if(diaryid.equals(myDiaryids.diaryid) && publishid.equals(myDiaryids.publishid)){
				isExist=true;
			}
		}
		if(!isExist){
			MyDiaryids diaryids=new GsonResponse3().new MyDiaryids();
			diaryids.diaryid=diaryid;
			diaryids.publishid= publishid;
			diaryDataEntities.praisedDiariesIDList.add(diaryids);
		}
	}
	
	/**
	 * 移出赞日记ID列表中指定日记
	 */
	public void removePraiseDiaryID(String diaryid, String publishid){
		for(int i=0;i<diaryDataEntities.praisedDiariesIDList.size();i++){
			MyDiaryids myDiaryids=diaryDataEntities.praisedDiariesIDList.get(i);
			if(diaryid.equals(myDiaryids.diaryid) && publishid.equals(myDiaryids.publishid)){
				diaryDataEntities.praisedDiariesIDList.remove(i);
				break;
			}
		}
	}
	
	public List<MyDiaryids> getCollectDiariesIDList() {
		return diaryDataEntities.collectDiariesIDList;
	}

	public List<MyDiaryids> getPraisedDiariesIDList() {
		return diaryDataEntities.praisedDiariesIDList;
	}
	
	//判断当前日记是否收藏
	public boolean isEnshrine(String id){
		for(int i=0;i<diaryDataEntities.collectDiariesIDList.size();i++){
			if(id.equals(diaryDataEntities.collectDiariesIDList.get(i).diaryid)
					/*&&"1".equals(diaryDataEntities.collectDiariesIDList.get(i).publish_type)*/){
				return true;
			}
		}
		return false;
	}
	
	//判断当前日记是否赞过
	public boolean isPraise(String id, String publishid){
		for(int i=0;i<diaryDataEntities.praisedDiariesIDList.size();i++){
			if(id.equals(diaryDataEntities.praisedDiariesIDList.get(i).diaryid) && publishid.equals(diaryDataEntities.praisedDiariesIDList.get(i).publishid)
					/*&&"1".equals(diaryDataEntities.praisedDiariesIDList.get(i).publish_type)*/){
				return true;
			}
		}
		return false;
	}
	
	private MyDiaryList mMyDiaryListBuf = new MyDiaryList();
	private ArrayList<MyDiary> mMyDiaryBuf = new ArrayList<MyDiary>();
	
	public MyDiaryList getmMyDiaryListBuf()
	{
		return mMyDiaryListBuf;
	}

	public void setmMyDiaryListBuf(MyDiaryList mMyDiaryListBuf)
	{
		this.mMyDiaryListBuf = mMyDiaryListBuf;
	}

	public ArrayList<MyDiary> getmMyDiaryBuf()
	{
		return mMyDiaryBuf;
	}

	public void setmMyDiaryBuf(ArrayList<MyDiary> mMyDiaryBuf)
	{
		this.mMyDiaryBuf = mMyDiaryBuf;
	}
	

}
