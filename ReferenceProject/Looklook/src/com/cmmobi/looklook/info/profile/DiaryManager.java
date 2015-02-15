package com.cmmobi.looklook.info.profile;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import android.content.Intent;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import cn.zipper.framwork.core.ZApplication;
import cn.zipper.framwork.core.ZLog;

import com.cmmobi.looklook.MainApplication;
import com.cmmobi.looklook.activity.DiaryDetailActivity;
import com.cmmobi.looklook.activity.HomeActivity;
import com.cmmobi.looklook.common.gson.GsonRequest2;
import com.cmmobi.looklook.common.gson.GsonRequest2.Attachs;
import com.cmmobi.looklook.common.gson.GsonResponse2;
import com.cmmobi.looklook.common.gson.GsonResponse2.MyAttachAudio;
import com.cmmobi.looklook.common.gson.GsonResponse2.MyAttachImage;
import com.cmmobi.looklook.common.gson.GsonResponse2.MyAttachVideo;
import com.cmmobi.looklook.common.gson.GsonResponse2.MyDiary;
import com.cmmobi.looklook.common.gson.GsonResponse2.MyDiaryids;
import com.cmmobi.looklook.common.gson.GsonResponse2.MyDuplicate;
import com.cmmobi.looklook.common.gson.GsonResponse2.ShareInfo;
import com.cmmobi.looklook.common.gson.GsonResponse2.ShareSNSTrace;
import com.cmmobi.looklook.common.gson.GsonResponse2.diaryAttach;
import com.cmmobi.looklook.common.gson.GsonResponse2.diaryCommentListResponse;
import com.cmmobi.looklook.common.gson.GsonResponse2.diarycommentlistItem;
import com.cmmobi.looklook.common.gson.GsonResponse2.forwardDiaryListResponse;
import com.cmmobi.looklook.common.gson.GsonResponse2.homeResponse;
import com.cmmobi.looklook.common.gson.GsonResponse2.listCollectDiaryResponse;
import com.cmmobi.looklook.common.gson.GsonResponse2.listMyDiaryResponse;
import com.cmmobi.looklook.common.gson.GsonResponse2.shareEnjoyDiaryResponse;
import com.cmmobi.looklook.common.gson.GsonResponse2.taglistItem;
import com.cmmobi.looklook.common.gson.Requester2;
import com.cmmobi.looklook.common.utils.DateUtils;
import com.cmmobi.looklook.info.weather.MyWeather;
import com.cmmobi.looklook.networktask.NetworkTaskManager;

/**
 * @author wuxiang
 * @email wuxiang@cmmobi.com
 * @date 2013-6-9
 */
public class DiaryManager {

	private static final String TAG="DiaryManager";
	public static final String DIARY_EDIT_DONE = "DIARY.EDIT.DONG";
	public static final String DIARY_LIST_EDIT_DONE = "diary_list_edit_done";
	public static final String ACTION_DIARY_SYNCHRONIZED = "action_diary_synchronized";
	public static final int PAGE_SIZE=10;
	public static int commentCount=0;
	private ArrayList<MyDiary> detailDiaryList=new ArrayList<MyDiary>();
	
	public void setDetailDiaryList(List<MyDiary> detailDiaryList){
		this.detailDiaryList.clear();
//		List<MyDiary> tempList = new ArrayList<MyDiary>();
//		for(int i=0;i<detailDiaryList.size();i++){
//			MyDiary myDiary=detailDiaryList.get(i);
//			if(diaryDataEntities.diaryList.contains(myDiary)){
//				ArrayList<MyDiary> duplicateDiaries=findDuplicateDiaries(myDiary);
//				if(duplicateDiaries!=null&&duplicateDiaries.size()>0){
//					tempList.addAll(duplicateDiaries);
//				}
//			}
//		}
//		detailDiaryList.addAll(tempList);
		this.detailDiaryList.addAll(detailDiaryList);
	}
	
	/**
	 * @description 根据diary的uuid刷新列表数据，更新后的数据放在detailDiaryList中
	 * @param 需要更新的列表
	 */
	public void resetDetailDiaryList(List<MyDiary> detailDiaryList){
		this.detailDiaryList.clear();
		List<MyDiary> tempList = new ArrayList<MyDiary>();
		for (int i = 0; i < detailDiaryList.size(); i++)
		{
			MyDiary myDiary = detailDiaryList.get(i);
			for (int j = 0; j < diaryDataEntities.diaryList.size(); j++)
			{
				MyDiary baseDiary = diaryDataEntities.diaryList.get(j);
				if (myDiary.diaryuuid.equals(baseDiary.diaryuuid))
				{
					tempList.add(baseDiary);
				}
			}
		}
		this.detailDiaryList.addAll(tempList);
	}
	
	public ArrayList<MyDiary> getDetailDiaryList(){
		return detailDiaryList;
	}
	
	private DiaryManager(){
	}
	
	private DiaryDataEntities diaryDataEntities;
	
	private void init(){
		AccountInfo accountInfo = AccountInfo.getInstance(getMyUserID());
		diaryDataEntities= accountInfo.dataEntities;
	}
	
	private static DiaryManager diaryManager;
	public static synchronized DiaryManager getInstance(){
		if(null==diaryManager){
			diaryManager=new DiaryManager();
		}
		diaryManager.init();
		return diaryManager;
	}
	
	/**
	 * 存储服务器标签列表
	 */
	public void addTagList(taglistItem[] items){
		if(items!=null&&items.length>0){
			diaryDataEntities.tagsList.clear();
			for(int i=0;i<items.length;i++){
				diaryDataEntities.tagsList.add(items[i]);
			}
		}
	}
	
	/**
	 * 获取本地存储标签列表
	 */
	public ArrayList<taglistItem> getTags(){
		return diaryDataEntities.tagsList;
	}
	
	//返回本地已用过的标签名字列表
	public ArrayList<String> getCheckedTags(boolean isIncludeSaveBox){
		ArrayList<String> checkedTags=new ArrayList<String>();
		for(int i=0;i<diaryDataEntities.diaryList.size();i++){
			MyDiary myDiary=diaryDataEntities.diaryList.get(i);
			if(myDiary.tags!=null&&myDiary.tags.length>0){
				for(int j=0;j<myDiary.tags.length;j++){
					if(!checkedTags.contains(myDiary.tags[j].name)&&isIncludeSavebox(myDiary, isIncludeSaveBox)){
						checkedTags.add(myDiary.tags[j].name);
					}
				}
			}
		}
		return checkedTags;
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
			MyDiaryids diaryids=new GsonResponse2().new MyDiaryids();
			diaryids.diaryid=diaryid;
			diaryids.publish_type="1";
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
	public void addPraiseDiaryID(String diaryid){
		boolean isExist=false;
		for(int i=0;i<diaryDataEntities.praisedDiariesIDList.size();i++){
			MyDiaryids myDiaryids=diaryDataEntities.praisedDiariesIDList.get(i);
			if(diaryid.equals(myDiaryids.diaryid)){
				isExist=true;
			}
		}
		if(!isExist){
			MyDiaryids diaryids=new GsonResponse2().new MyDiaryids();
			diaryids.diaryid=diaryid;
			diaryids.publish_type="1";
			diaryDataEntities.praisedDiariesIDList.add(diaryids);
		}
	}
	
	/**
	 * 移出赞日记ID列表中指定日记
	 */
	public void removePraiseDiaryID(String diaryid){
		for(int i=0;i<diaryDataEntities.praisedDiariesIDList.size();i++){
			MyDiaryids myDiaryids=diaryDataEntities.praisedDiariesIDList.get(i);
			if(diaryid.equals(myDiaryids.diaryid)){
				diaryDataEntities.praisedDiariesIDList.remove(i);
				break;
			}
		}
	}
	
	// 修改日记心情
	public void modifyMood(String diaryuuid,String mood){
		for(int i=0;i<diaryDataEntities.diaryList.size();i++){
			MyDiary myDiary = diaryDataEntities.diaryList.get(i);
			if(diaryuuid!=null&&diaryuuid.equals(myDiary.diaryuuid)){
				myDiary.mood=mood;
				break;
			}
		}
	}
	
	/**
	 * 修改日记 diary_status的值
	 * 1新建 2 发布
	 */
	public void modifyDiaryPublishStatus(String diaryuuid,String status){
		for(int i=0;i<diaryDataEntities.diaryList.size();i++){
			MyDiary myDiary = diaryDataEntities.diaryList.get(i);
			if(diaryuuid.equals(myDiary.diaryuuid)){
				myDiary.diary_status=status;
				myUnSynchronizeDataChanged();
				break;
			}
		}
	}
	
	public MyDiary findLocalDiary(String diaryID) {
		for (Iterator<MyDiary> it = diaryDataEntities.diaryList.iterator();it.hasNext();) {
			MyDiary myDiary = it.next();
			if (diaryID!=null&&(diaryID.equals(myDiary.diaryid) || diaryID.equals(myDiary.diaryuuid))) {
				return myDiary;
			}
		}
		return null;
	}
	
	public ArrayList<MyDiary> syncDiaryList(ArrayList<MyDiary> diaryList){
		ArrayList<MyDiary> myDiaries=new ArrayList<GsonResponse2.MyDiary>();
		for(int i=0;i<diaryList.size();i++){
			MyDiary temp=diaryList.get(i);
			MyDiary myDiary=findLocalDiaryByUuid(temp.diaryuuid);
			if(myDiary!=null)
				myDiaries.add(myDiary);
		}
		return myDiaries;
	}
	
	public MyDiary findLocalDiaryByUuid(String diaryUUID) {
		for (MyDiary myDiary:diaryDataEntities.diaryList) {
			if (myDiary.diaryuuid.equals(diaryUUID)) {
				return myDiary;
			}
		}
		return null;
	}
	
	//判断当前日记是否收藏
	public boolean isEnshrine(MyDiary myDiary){
		for(int i=0;i<diaryDataEntities.collectDiariesIDList.size();i++){
			if(myDiary.diaryid.equals(diaryDataEntities.collectDiariesIDList.get(i).diaryid)&&"1".equals(diaryDataEntities.collectDiariesIDList.get(i).publish_type)){
				return true;
			}
		}
		return false;
	}
	
	//判断当前日记是否赞过
	public boolean isPraise(MyDiary myDiary){
		for(int i=0;i<diaryDataEntities.praisedDiariesIDList.size();i++){
			if(myDiary.diaryid.equals(diaryDataEntities.praisedDiariesIDList.get(i).diaryid)&&"1".equals(diaryDataEntities.praisedDiariesIDList.get(i).publish_type)){
				return true;
			}
		}
		return false;
	}
	
	public void addCommentDiaryResponse(diaryCommentListResponse commentResponse) {
		if(commentResponse!=null&&commentResponse.comments!=null&&commentResponse.comments.length>0){
			diaryDataEntities.commentDiaryList.clear();
			diaryDataEntities.commentDiaryList.addAll(Arrays.asList(commentResponse.comments));
		}else{
			Log.e(TAG, "addCommentDiaryResponse failed");
		}
	}
	
	public void addPraiseDiaryResponse(String userID,forwardDiaryListResponse diaryResponse) {
		if(userID!=null&&diaryResponse!=null&&diaryResponse.diaries!=null&&diaryResponse.diaries.length>0){
			ArrayList<MyDiary> diaries=new ArrayList<GsonResponse2.MyDiary>();
			diaries.addAll(Arrays.asList(diaryResponse.diaries));
			diaryDataEntities.praiseDiaryMap.put(userID, diaries);
		}else{
			Log.e(TAG, "addPraiseDiaryResponse failed");
		}
	}
	
	public void addCollectDiaryResponse(listCollectDiaryResponse diaryResponse) {
		if(diaryResponse!=null&&diaryResponse.diaries!=null&&diaryResponse.diaries.length>0){
			diaryDataEntities.collectDiaryList.clear();
			diaryDataEntities.collectDiaryList.addAll(Arrays.asList(diaryResponse.diaries));
		}else{
			Log.e(TAG, "addCollectDiaryResponse failed");
		}
	}
	
	public void addShareDiaryResponse(String userID,shareEnjoyDiaryResponse diaryResponse) {
		if(userID!=null&&diaryResponse!=null&&diaryResponse.diaries!=null&&diaryResponse.diaries.length>0){
			ArrayList<MyDiary> diaries=new ArrayList<GsonResponse2.MyDiary>();
			diaries.addAll(Arrays.asList(diaryResponse.diaries));
			diaryDataEntities.shareDiaryMap.put(userID, diaries);
		}else{
			Log.e(TAG, "addShareDiaryResponse failed");
		}
	}
	
	/**
	 * 保存增量日记数据
	 * diaryResponse-需要保存的日记数据对象
	 * isNotifyUI-true 通知UI数据更新 false-不通知UI
	 */
	public synchronized void saveDiaries(Object diaryResponse,boolean isNotifyUI) {
		if(null==diaryResponse){
			Log.e(TAG, "saveDiaries failed");
			return;
		}
		//1.是否重置数据
		String removediariesTemp=null;
		MyDiary[] diariesTemp=null;
		if(diaryResponse instanceof homeResponse){
			homeResponse response=(homeResponse) diaryResponse;
			removediariesTemp=response.removediarys;
			diariesTemp=response.diaries;
			setDiarySync(diariesTemp,4);
		}else if(diaryResponse instanceof listMyDiaryResponse){
			listMyDiaryResponse response=(listMyDiaryResponse) diaryResponse;
			removediariesTemp=response.removediarys;
			diariesTemp=response.diaries;
			setDiarySync(diariesTemp,4);
		}else if(diaryResponse instanceof MyDiary){
			MyDiary myDiary=(MyDiary)diaryResponse;
			Log.d(TAG, "saveDiaries->myDiary.diaryid="+myDiary.diaryid);
			Log.d(TAG, "saveDiaries->myDiary.diaryuuid="+myDiary.diaryuuid);
			Log.d(TAG, "saveDiaries->myDiary.sync_status="+myDiary.sync_status);
			Log.d(TAG, "saveDiaries->myDiary.resourceuuid"+myDiary.resourceuuid);
			Log.d(TAG, "saveDiaries->myDiary.updatetimemilli="+myDiary.updatetimemilli);
			Log.d(TAG, "saveDiaries->myDiary.diarytimemilli="+myDiary.diarytimemilli);
			diariesTemp=new MyDiary[1];
			diariesTemp[0]=myDiary;
		}else{
			Log.e(TAG, "saveDiaries-> diaryResponse is unkown class type");
		}
		//2.根据removeids去重
		if(removediariesTemp!=null){
			String[] removediaries=removediariesTemp.split(",");
			removeDiaryByIDs(removediaries);
		}
		if(diariesTemp!=null&&diariesTemp.length>0){
			//3.根据日记ID去重
			removeDuplicateDiaries(diariesTemp);
			diaryDataEntities.diaryList.addAll(Arrays.asList(diariesTemp));
			//4.日记排序
			synchronized (diaryDataEntities.diaryList) {
				Collections.sort(diaryDataEntities.diaryList, new DiaryComparator());
			}
			if(isNotifyUI){
				//更新UI
				Log.d(TAG, "diaryDataEntities.diaryList="+diaryDataEntities.diaryList);
				myHomepageDataChanged();
				myUnSynchronizeDataChanged();
				myTagDataChanged();
				mySaveBoxDataChanged();
				myShareDataChanged();
			}
		}
	}
	
	//设置日记同步状态
	public void setDiarySync(MyDiary[] myDiaries, int sync_status){
		if(myDiaries!=null&&myDiaries.length>0){
			for(int i=0;i<myDiaries.length;i++){
				myDiaries[i].sync_status=sync_status;
			}
		}
	}
	
	public void diaryDataChanged(String diaryuuid) {
		myHomepageDataChanged();
		myUnSynchronizeDataChanged();
		myTagDataChanged();
		Intent intent = new Intent(DIARY_EDIT_DONE);
		intent.putExtra(DiaryDetailActivity.INTENT_ACTION_DIARY_UUID, diaryuuid);
		LocalBroadcastManager.getInstance(MainApplication.getAppInstance()).sendBroadcast(intent);
	}
	
	public void diaryDataChanged() {
		myHomepageDataChanged();
		myUnSynchronizeDataChanged();
		myTagDataChanged();
	}
	
	public void diaryListChanged(String diaryUUID) {
		myHomepageDataChanged();
		myUnSynchronizeDataChanged();
		myTagDataChanged();
		Intent intent = new Intent(DIARY_LIST_EDIT_DONE);
		intent.putExtra(DiaryDetailActivity.INTENT_ACTION_DIARY_UUID, diaryUUID);
		LocalBroadcastManager.getInstance(MainApplication.getAppInstance()).sendBroadcast(intent);
	}
	
	public void removeLocalDiaryByUUID(String diaryUUID, Boolean isNotifyUI) {
		for (MyDiary diary:diaryDataEntities.diaryList) {
			if (diary.diaryuuid != null && diary.diaryuuid.equals(diaryUUID)) {
				diaryDataEntities.diaryList.remove(diary);
				if(isNotifyUI)
					diaryPageNotify();
				return;
			}
		}
	}
	
	//设置本地日记最新时间
	public void setMyDiaryFirstTime(String firsttime){
		if(DateUtils.isNum(firsttime)){
			if(DateUtils.isNum(diaryDataEntities.mydiaryFirsttime)){
				if(Long.parseLong(firsttime)>Long.parseLong(diaryDataEntities.mydiaryFirsttime)){
					diaryDataEntities.mydiaryFirsttime=firsttime;
				}
			}else{
				diaryDataEntities.mydiaryFirsttime=firsttime;
			}
		}
	}
	
	//设置本地日记最旧时间
	public void setMyDiaryLastTime(String lasttime){
		if(DateUtils.isNum(lasttime)){
			if(DateUtils.isNum(diaryDataEntities.mydiaryLasttime)){
				if(Long.parseLong(lasttime)<Long.parseLong(diaryDataEntities.mydiaryLasttime)){
					diaryDataEntities.mydiaryLasttime=lasttime;
				}
			}else{
				diaryDataEntities.mydiaryLasttime=lasttime;
			}
		}
	}
	
	//获取自己日记最新时间
	public String getMyDiaryFirstTime(){
		return diaryDataEntities.mydiaryFirsttime;
	}
	
	//获取自己日记的最老时间
	public String getMyDiaryLastTime(){
		return diaryDataEntities.mydiaryLasttime;
	}

	/**
	 * filterType 日记过滤类型
	 * userID 用户ID
	 * startNum 从第几条开始去 如第一次从0开始取，第二次从10开始取，第三次可能从15开始取
	 * isIncludeSaveBox true-包含保险箱日记 false-不包含保险箱日记
	 * 返回值为0时，说明本地没有获取到日记数据
	 */
	public Object getDiary(FilterType filterType,String userID,int startNum,boolean isIncludeSaveBox) {
		Log.d(TAG, "getDiary FilterType="+filterType);
		switch (filterType) {
		case ALL:
			return getMyHomepageDiary(userID,startNum,isIncludeSaveBox);
		case SYNCHRONIZED:
			return getSynchronizedDiary(startNum,isIncludeSaveBox);
		case SAVEBOX:
			return getSaveboxDiary(userID,startNum);
		case UNSYNCHRONIZED:
			return getUnSyncDiary(startNum,isIncludeSaveBox);
		case TAG:
			return getTagDiary(userID, startNum, isIncludeSaveBox);
		case SHARED:
			return getSharedDiary(startNum, isIncludeSaveBox);
//		case COLLECTED:
//			return getCollectDiary();
//		case PRAISE:
//			return getPraiseDiary(userID);
//		case COMMENT:
//			return getCommentDiary();
		default:
			break;
		}
		return null;
	}
	
	//根据uuid找到日记
	public ArrayList<MyDiary> getDiaryListByUUID(ArrayList<String> uuidList){
		ArrayList<MyDiary> myDiaries=new ArrayList<GsonResponse2.MyDiary>();
		if(uuidList!=null){
			for(int i=0;i<diaryDataEntities.diaryList.size();i++){
				MyDiary myDiary=diaryDataEntities.diaryList.get(i);
				if(uuidList.contains(myDiary.diaryuuid)&&!myDiaries.contains(myDiary)){
					myDiaries.add(myDiary);
				}
			}
		}
		return myDiaries;
	}
	
	public void updateUserInfo(String userID,homeResponse response){
		if(response!=null&&userID!=null){
			String myUserID = ActiveAccount.getInstance(ZApplication.getInstance()).getUID();
			if(userID.equals(myUserID)){
				AccountInfo accountInfo = AccountInfo.getInstance(userID);
				accountInfo.zoneBackGround=response.background;
				accountInfo.mood=response.moodurl;
				accountInfo.attendedCount=response.attentioncount;
				accountInfo.fansCount=response.fanscount;
				accountInfo.nickname=response.nickname;
				accountInfo.headimageurl=response.headimageurl;
				accountInfo.signature=response.signature;
				accountInfo.sex=response.sex;
			}else{
				OtherUserInfo otherUserInfo=new OtherUserInfo();
				otherUserInfo.zoneBackGround=response.background;
				otherUserInfo.mood=response.moodurl;
				otherUserInfo.attendedCount=response.attentioncount;
				otherUserInfo.fansCount=response.fanscount;
				otherUserInfo.nickname=response.nickname;
				otherUserInfo.headimageurl=response.headimageurl;
				otherUserInfo.signature=response.signature;
				otherUserInfo.sex=response.sex;
				diaryDataEntities.otherUserInfos.put(userID, otherUserInfo);
			}
		}
	}
	
	//获取心情
	public OtherUserInfo getOtherUserInfo(String userID){
		String myUserID = ActiveAccount.getInstance(ZApplication.getInstance()).getUID();
		if(!myUserID.equals(userID)){
			OtherUserInfo otherUserInfo=diaryDataEntities.otherUserInfos.get(userID);
			if(otherUserInfo!=null){
				return otherUserInfo;
			}
		}
		return null;
	}
	
	//获取评论
	public ArrayList<diarycommentlistItem> getCommentDiary(){
		return diaryDataEntities.commentDiaryList;
	}
	
	//获取赞日记
	public ArrayList<MyDiary> getPraiseDiary(String userID){
		return diaryDataEntities.praiseDiaryMap.get(userID);
	}
	
	//获取收藏日记
	public ArrayList<MyDiary> getCollectDiary(){
		return diaryDataEntities.collectDiaryList;
	}
	
	//获取互动分享日记列表
	public ArrayList<MyDiary> getShareDiary(String userID){
		return diaryDataEntities.shareDiaryMap.get(userID);
	}
	
	//获取保险箱日记
	private ArrayList<MyDiary> getSaveboxDiary(String userID,int startNum){
		ArrayList<MyDiary> diaries=new ArrayList<MyDiary>();
		int count=0;
		for(int i=0;i<diaryDataEntities.diaryList.size();i++){
			MyDiary myDiary=diaryDataEntities.diaryList.get(i);
			if(userID.equals(myDiary.userid)&&"1".equals(myDiary.join_safebox)){
				if(count>=startNum&&count<(startNum+PAGE_SIZE)){
					diaries.add(myDiary);
				}
				if(count==(startNum+PAGE_SIZE-1))
					break;
				count++;
			}
		}
		return diaries;
	}
	
	//获取标签日记
	private ArrayList<MyDiary> getTagDiary(String userID,int startNum,boolean isIncludeSaveBox){
		ArrayList<MyDiary> diaries=new ArrayList<MyDiary>();
		int count=0;
		for(int i=0;i<diaryDataEntities.diaryList.size();i++){
			MyDiary myDiary=diaryDataEntities.diaryList.get(i);
			com.cmmobi.looklook.common.gson.GsonResponse2.TAG[] tags=myDiary.tags;
			if(userID.equals(myDiary.userid)&&tags!=null&&tag!=null&&tags.length>0&&isIncludeSavebox(myDiary,isIncludeSaveBox)){
				for(int j=0;j<tags.length;j++){
					if(tag.name.equals(tags[j].name)){
						if(count>=startNum&&count<(startNum+PAGE_SIZE)){
							diaries.add(myDiary);
						}
						if(count==(startNum+PAGE_SIZE-1))
							break;
						count++;
					}
				}
			}
		}
		return diaries;
	}
	
	//获取已分享日记(日记页已分享选项 只筛选本地)
	private ArrayList<MyDiary> getSharedDiary(int startNum,boolean isIncludeSaveBox){
		ArrayList<MyDiary> diaries=new ArrayList<MyDiary>();
		int count=0;
		for(int i=0;i<diaryDataEntities.diaryList.size();i++){
			MyDiary myDiary=diaryDataEntities.diaryList.get(i);
			if(myDiary.userid.equals(getMyUserID())&&"2".equals(myDiary.diary_status)&&isIncludeSavebox(myDiary,isIncludeSaveBox)){
				if(count>=startNum&&count<(startNum+PAGE_SIZE)){
					diaries.add(myDiary);
				}
				if(count==(startNum+PAGE_SIZE-1))
					break;
				count++;
			}
		}
		return diaries;
	}
	
	//获取未同步日记（未上传到服务器的日记或已上传到服务器但没有创建软连接）
	public ArrayList<MyDiary> getUnSyncDiary(int startNum,boolean isIncludeSaveBox){
		ArrayList<MyDiary> diaries=new ArrayList<MyDiary>();
		int count=0;
		for(int i=0;i<diaryDataEntities.diaryList.size();i++){
			MyDiary myDiary=diaryDataEntities.diaryList.get(i);
			if(myDiary.sync_status<4&&isIncludeSavebox(myDiary,isIncludeSaveBox)){
				if(count>=startNum&&count<(startNum+PAGE_SIZE)){
					//记录该页日记最早时间
					diaries.add(myDiary);
				}
				if(count==(startNum+PAGE_SIZE-1))
					break;
				count++;
			}
		}
		return diaries;
	}
	
	//获取未同步日记（未上传到服务器的日记或已上传到服务器但没有创建软连接）
	public ArrayList<MyDiary> getUnSyncDiaryStorage(int startNum , boolean isIncludeSaveBox){
		ArrayList<MyDiary> diaries=new ArrayList<MyDiary>();
		int count=0;
		for(int i=0;i<diaryDataEntities.diaryList.size();i++){
			MyDiary myDiary=diaryDataEntities.diaryList.get(i);
			if(myDiary.userid.equals(getMyUserID())&&myDiary.sync_status<4 && isIncludeSavebox(myDiary,isIncludeSaveBox)){
				if(count>=startNum&&count<(startNum+PAGE_SIZE)){
					//记录该页日记最早时间
					diaries.add(myDiary);
				}
				if(count==(startNum+PAGE_SIZE-1))
					break;
				count++;
			}
		}
		return diaries;
	}
	
	/**
	 * 获取未同步日记物理文件总大小 
	 */
	public long getUnSyncDiaryTotalSize(boolean isIncludeSaveBox){
		long totalSize=0;
		for(int i=0;i<diaryDataEntities.diaryList.size();i++){
			MyDiary myDiary=diaryDataEntities.diaryList.get(i);
			if(myDiary.userid.equals(getMyUserID())&&myDiary.sync_status<4 && isIncludeSavebox(myDiary,isIncludeSaveBox)){
				totalSize+=Long.parseLong(myDiary.getDiarySize());
			}
		}
		Log.d(TAG, "getUnSyncDiaryTotalSize->totalSize="+totalSize);
		return totalSize;
	}
	
	/**
	 * 获取已同步日记物理文件总大小
	 */
	public long getSyncDiaryTotalSize(Boolean isIncludeSaveBox){
		long totalSize=0;
		for(int i=0;i<diaryDataEntities.diaryList.size();i++){
			MyDiary myDiary=diaryDataEntities.diaryList.get(i);
			if(myDiary.userid.equals(getMyUserID())&&6==myDiary.sync_status&&isIncludeSavebox(myDiary,isIncludeSaveBox)){
				String size=myDiary.getDiarySize();
				if(size!=null&&DateUtils.isNum(size))
					totalSize+=Long.parseLong(size);
			}
		}
		Log.d(TAG, "getSyncDiaryTotalSize->totalSize="+totalSize);
		return totalSize;
	}
	
	//清楚自己的全部日记
	public void clearMydiaries(){
		String userid=getMyUserID();
		ArrayList<MyDiary> removeDiaryList=new ArrayList<GsonResponse2.MyDiary>();
		for(int i=0;i<diaryDataEntities.diaryList.size();i++){
			MyDiary myDiary=diaryDataEntities.diaryList.get(i);
			if(userid!=null&&userid.equals(myDiary.userid)){
				removeDiaryList.add(myDiary);
			}
		}
		for(int i=0;i<removeDiaryList.size();i++){
			removeDiaryByUUID(removeDiaryList.get(i).diaryuuid, false);
		}
	}
	 
	/**
	 * isHalf true-清除一半 false-全部清除  涉及物理文件删除操作，请在非UI线程执行
	 */
	public void removeSyncDiaryFiles(boolean isHalf){
		ArrayList<MyDiary> allSyncDiaries=new ArrayList<GsonResponse2.MyDiary>();
		for(int i=0;i<diaryDataEntities.diaryList.size();i++){
			MyDiary myDiary=diaryDataEntities.diaryList.get(i);
			if(myDiary.userid.equals(getMyUserID())&&6==myDiary.sync_status&&isIncludeSavebox(myDiary,false)){
				allSyncDiaries.add(myDiary);
			}
		}
		for(int i=0;i<allSyncDiaries.size();i++){
			if(isHalf)
				if(i==allSyncDiaries.size()/2)return;
			allSyncDiaries.get(i).clear();
		}
	}
	
	 //获取我的相册数据
	private ArrayList<MyDiary> getMyHomepageDiary(String userID,int startNum,boolean isIncludeSaveBox){
		Log.d(TAG, "getMyHomepageDiary");
		ArrayList<MyDiary> diaries=new ArrayList<MyDiary>();
		int count=0;
		for(int i=0;i<diaryDataEntities.diaryList.size();i++){
			MyDiary myDiary=diaryDataEntities.diaryList.get(i);
			if(myDiary!=null&&userID!=null&&myDiary.userid.equals(userID)&&isIncludeSavebox(myDiary,isIncludeSaveBox)){
				if(0==count)//记录最新日记时间
					firstTime_homepage=myDiary.diarytimemilli;
				if(count>=startNum&&count<(startNum+PAGE_SIZE)){
					//记录该页日记最早时间
					if(myDiary.diarytimemilli!=null&&DateUtils.isNum(myDiary.diarytimemilli)){
						if(lastTime_homepage!=null&&DateUtils.isNum(lastTime_homepage)){
							long lLastTime=Long.parseLong(lastTime_homepage);
							long lUpdatetime=Long.parseLong(myDiary.diarytimemilli);
							if(lUpdatetime<lLastTime)
								lastTime_homepage=myDiary.diarytimemilli;
						}else{
							lastTime_homepage=myDiary.diarytimemilli;
						}
					}
					diaries.add(myDiary);
				}
				if(count==(startNum+PAGE_SIZE-1))
					break;
				count++;
			}
		}
		return diaries;
	}
	
	//获取已同步日记列表(多媒体文件已下载到本地的日记)
	public ArrayList<MyDiary> getSynchronizedDiary(int startNum,boolean isIncludeSaveBox){
		ArrayList<MyDiary> diaries=new ArrayList<MyDiary>();
		int count=0;
		for(int i=0;i<diaryDataEntities.diaryList.size();i++){
			MyDiary myDiary=diaryDataEntities.diaryList.get(i);
			if(myDiary.userid.equals(getMyUserID())&&6==myDiary.sync_status&&isIncludeSavebox(myDiary,isIncludeSaveBox)){
				if(count>=startNum&&count<(startNum+PAGE_SIZE)){
					//记录该页日记最早时间
					diaries.add(myDiary);
				}
				if(count==(startNum+PAGE_SIZE-1))
					break;
				count++;
			}
		}
		return diaries;
	}
	
	/*//获取未上传日记，不包含保险箱中的内容
	private ArrayList<MyDiary> getUnuploadNotJoinSafeDiary(int pageIndex){
		ArrayList<MyDiary> diaries=new ArrayList<MyDiary>();
		int count=0;
		for(int j=0;j<diaryDataEntities.localDiaryList.size();j++){
			if(diaryDataEntities.localDiaryList.get(j).myDiary.join_safebox != null && diaryDataEntities.localDiaryList.get(j).myDiary.join_safebox.equals("1")){
				continue;
			}
			if(diaryDataEntities.localDiaryList.get(j).status<4){
				if(count>=(pageIndex)*10&&count<(pageIndex+1)*10){
					diaries.add(diaryDataEntities.localDiaryList.get(j).myDiary);
				}else if(count >= (pageIndex+1)*10){
					break;
				}
				count++;
			}
		}
		Collections.sort(diaries, new DiaryComparator());
		return diaries;
	}*/
	/**
	 * 获取他人主页日记列表
	 * @param pageIndex
	 * @param userid
	 * @return
	 */
	public ArrayList<MyDiary> getOtherHomePageDiary(int pageIndex,String userid) {
		ArrayList<MyDiary> diaries=new ArrayList<MyDiary>();
		int count=0;
		for(int i=0;i<diaryDataEntities.diaryList.size();i++){
			MyDiary myDiary=diaryDataEntities.diaryList.get(i);
			if(userid.equals(myDiary.userid)){
				if(0==count)//记录最新日记时间
					firstTime_otherpage=myDiary.diarytimemilli;
				if(count>=pageIndex*PAGE_SIZE&&count<((pageIndex+1)*PAGE_SIZE)){
					//记录该页日记最早时间
					if (myDiary.diarytimemilli != null
							&& DateUtils.isNum(myDiary.diarytimemilli)) {
						if (lastTime_otherpage != null
								&& DateUtils.isNum(lastTime_otherpage)) {
							long lLastTime = Long.parseLong(lastTime_otherpage);
							long lUpdatetime = Long
									.parseLong(myDiary.diarytimemilli);
							if (lUpdatetime < lLastTime)
								lastTime_otherpage = myDiary.diarytimemilli;
						} else {
							lastTime_otherpage = myDiary.diarytimemilli;
						}
					}
					diaries.add(myDiary);
				}
				if(count==((pageIndex+1)*PAGE_SIZE-1))
					break;
				count++;
			}
		}
		return diaries;
	}
	
	/**
	 * 本地获取他人主页分享日记列表
	 * @param pageIndex
	 * @param userid
	 * @return
	 *//*
	public ArrayList<MyDiary> getOtherShareDiary(int pageIndex,String userid) {
		ArrayList<MyDiary> diaries=new ArrayList<MyDiary>();
		if (!diaryDataEntities.otherShareDiaryResponseMap.containsKey(userid)) {
			return null;
		}
		int len= diaryDataEntities.otherShareDiaryResponseMap.get(userid).size();
		if(pageIndex>=len||pageIndex<0){
			Log.e(TAG, "getAllDiary->pageIndex>=len||pageIndex<0");
			return null;
		}
		
		shareEnjoyDiaryResponse response=diaryDataEntities.otherShareDiaryResponseMap.get(userid).get(pageIndex);
		String first=response.first_user_time;
		String end=response.last_user_time;
		Log.d(TAG, "first="+first);
		Log.d(TAG, "end="+end);
		if(0==pageIndex) {//获取下拉刷新数据的时间戳 也就是日记列表中离现在最近的时间
			firstTime_othershare=first;
		}
		lastTime_othershare=end;//获取上拉加载数据的时间戳，也就是日记列表中离现在最远的时间
		
		MyDiary[] myDiaries=response.diaries;
		diaries.addAll(Arrays.asList(myDiaries));
		//排序
		Collections.sort(diaries, new DiaryComparator());
		return diaries;
	}*/
	
	/**
	 * 本地获取他人主页赞日记列表
	 * @param pageIndex
	 * @param userid
	 * @return
	 *//*
	public ArrayList<MyDiary> getOtherForwardDiary(int pageIndex,String userid) {
		ArrayList<MyDiary> diaries=new ArrayList<MyDiary>();
		if (!diaryDataEntities.otherForwardDiaryResponseMap.containsKey(userid)) {
			return null;
		}
		int len= diaryDataEntities.otherForwardDiaryResponseMap.get(userid).size();
		if(pageIndex>=len||pageIndex<0){
			Log.e(TAG, "getAllDiary->pageIndex>=len||pageIndex<0");
			return null;
		}
		
		forwardDiaryListResponse response=diaryDataEntities.otherForwardDiaryResponseMap.get(userid).get(pageIndex);
		String first=response.first_user_time;
		String end=response.last_user_time;
		Log.d(TAG, "first="+first);
		Log.d(TAG, "end="+end);
		if(0==pageIndex) {//获取下拉刷新数据的时间戳 也就是日记列表中离现在最近的时间
			firstTime_otherPraise=first;
		}
		lastTime_otherPraise=end;//获取上拉加载数据的时间戳，也就是日记列表中离现在最远的时间
		
		MyDiary[] myDiaries=response.diaries;
		diaries.addAll(Arrays.asList(myDiaries));
		//排序
		Collections.sort(diaries, new DiaryComparator());
		return diaries;
	}*/
	
//	/**
//	 * 过滤出list中除本地以外的日记ID
//	 */
//	public String getServerDiaryID(ArrayList<String> list) {
//		if(list!=null&&list.size()>0) {
//			int len=list.size();
//			String diaryServer="";
//			for(int i=0;i<len;i++){
//				int localLen=diaryDataEntities.localDiaryList.size();
//				for(int j=0;j<localLen;j++){
//					DiaryWrapper localDiary=diaryDataEntities.localDiaryList.get(j);
//					if(!(localDiary.myDiary.diaryid.equals(list.get(i))&&"1".equals(localDiary.status))){
//						diaryServer=list.get(i)+","+diaryServer;
//					}
//				}
//			}
//			if(diaryServer!=null&&diaryServer.endsWith(","))
//				diaryServer.substring(0, diaryServer.length()-1);
//			return diaryServer;
//		}
//		return null;
//	}
	
	private taglistItem tag;
	public void setTag(taglistItem tag){
		this.tag=tag;
	}
	
	public taglistItem getTag(){
		return tag;
	}
	
	//获取日记对应的第三方分享ID
	public ArrayList<ArrayList<String>> getSNSShareId(MyDiary myDiary){
		if(null==myDiary){
			Log.e(TAG, "getSNSShareId->myDiary is null");
			return null;
		}
		ArrayList<String> sinaList=new ArrayList<String>();
		ArrayList<String> tencentList=new ArrayList<String>();
		ArrayList<String> renrenList=new ArrayList<String>();
		ShareSNSTrace[] snsTrace=myDiary.sns;
		if(null==snsTrace||0==snsTrace.length)return null;
		for(int k=0;k<snsTrace.length;k++){
			ShareInfo[] shareInfo=snsTrace[k].shareinfo;
			if(null==shareInfo||0==shareInfo.length)continue;
			for(int l=0;l<shareInfo.length;l++){
				if("1".equals(shareInfo[l].snstype)){//新浪
					sinaList.add(shareInfo[l].weiboid);
				}
				if("2".equals(shareInfo[l].snstype)){//人人
					renrenList.add(shareInfo[l].weiboid);
				}
				if("3".equals(shareInfo[l].snstype)){//腾讯
					tencentList.add(shareInfo[l].weiboid);
				}
			}
		}
		ArrayList<ArrayList<String>> shareIdList=new ArrayList<ArrayList<String>>();
		shareIdList.add(sinaList);
		shareIdList.add(tencentList);
		shareIdList.add(renrenList);
		return shareIdList;
	}
	
	//将日记添加到收藏日记列表
	public void addDiaryToCollect(MyDiary diary) {
		if(diaryDataEntities.collectDiaryList!=null&&diaryDataEntities.collectDiaryList.size()>0){
			for(int i=0;i<diaryDataEntities.collectDiaryList.size();i++){
				MyDiary myDiary=diaryDataEntities.collectDiaryList.get(i);
				if(myDiary.diaryid.equals(diary.diaryid)){
					diaryDataEntities.collectDiaryList.remove(i);
					break;
				}
			}
		}
		diaryDataEntities.collectDiaryList.add(diary);
		myCollectDataChanged();
	}
	//将日记添加到赞日记列表
	public void addDiaryToPraise(MyDiary diary) {
		if(null==diary)return;
		String userID = ActiveAccount.getInstance(ZApplication.getInstance()).getUID();
		ArrayList<MyDiary> diaries=diaryDataEntities.praiseDiaryMap.get(userID);
		if(diaries!=null&&diaries.size()>0){
			for(int i=0;i<diaries.size();i++){
				if(diaries.get(i).diaryid.equals(diary.diaryid)){
					diaries.remove(i);
					break;
				}
			}
			diaries.add(diary);
		}else{
			diaries=new ArrayList<GsonResponse2.MyDiary>();
			diaries.add(diary);
		}
		myPraiseDataChanged();
	}
	
	/*//添加日记到分享列表
	public void addDiaryToShare(MyDiary diary){
		diary.diary_src=5;
		// 添加到个人主页
		boolean isAdded=false;
		removeduplicateMyShareData(diary);
		long diaryTime = Long.parseLong(diary.updatetimemilli);
		for (int i = 0; i < diaryDataEntities.myShareDiaryResponseList.size(); i++) {
			shareEnjoyDiaryResponse response = diaryDataEntities.myShareDiaryResponseList
					.get(i);
			long first = Long.parseLong(response.first_user_time);
			long last = Long.parseLong(response.last_user_time);
			Log.d(TAG, "first=" + first);
			Log.d(TAG, "last=" + last);
			Log.d(TAG, "diaryTime=" + diaryTime);
			ArrayList<MyDiary> tempList = new ArrayList<MyDiary>();
			if (diaryTime <= first && diaryTime >= last) {
				tempList.addAll(Arrays.asList(response.diaries));
				tempList.add(diary);
				Collections.sort(tempList, new DiaryComparator());
				response.diaries = tempList
						.toArray(new MyDiary[tempList.size()]);
				myShareDataChanged();
				isAdded=true;
			}
		}
		
		if(!isAdded){
			shareEnjoyDiaryResponse collectDiaryResponse=new GsonResponse2.shareEnjoyDiaryResponse();
			collectDiaryResponse.diaries=new GsonResponse2.MyDiary[1];
			collectDiaryResponse.diaries[0]=diary;
			collectDiaryResponse.first_user_time=diary.updatetimemilli;
			collectDiaryResponse.last_user_time=diary.updatetimemilli;
			addShareDiaryResponseFirst(collectDiaryResponse);
			myShareDataChanged();
		}
	}*/
	
	/*//添加日记到标签list
	private void addDiaryToTag(MyDiary diary){
		diary.diary_src=3;
		// 添加到标签
		long diaryTime = Long.parseLong(diary.updatetimemilli);
		for (int i = 0; i < diaryDataEntities.myTagDiaryResponseList.size(); i++) {
			tagDiaryListResponse response = diaryDataEntities.myTagDiaryResponseList
					.get(i);
			long first = Long.parseLong(response.first_diary_time);
			long last = Long.parseLong(response.last_diary_time);
			Log.d(TAG, "first=" + first);
			Log.d(TAG, "last=" + last);
			Log.d(TAG, "diaryTime=" + diaryTime);
			ArrayList<MyDiary> tempList = new ArrayList<MyDiary>();
			if (diaryTime <= first && diaryTime >= last) {
				tempList.addAll(Arrays.asList(response.diaries));
				tempList.add(diary);
				myTagDataChanged();
				Collections.sort(tempList, new DiaryComparator());
				response.diaries = tempList
						.toArray(new MyDiary[tempList.size()]);
			}

		}
	}*/
	
	/*//添加到保险箱
	private void addDiaryToSavebox(MyDiary diary){
		diary.diary_src=4;
		//添加到保险箱
		boolean isAdded=false;
		removeduplicateMySaveBoxData(diary);
		long diaryTime = Long.parseLong(diary.diarytimemilli);
		for (int i = 0; i < diaryDataEntities.mySaveboxResponseList.size(); i++) {
			listsafeboxResponse response = diaryDataEntities.mySaveboxResponseList
					.get(i);
			if(response.first_diary_time!=null&&response.first_diary_time.length()>0&&response.last_diary_time!=null&&response.last_diary_time.length()>0){
				long first = Long.parseLong(response.first_diary_time);
				long last = Long.parseLong(response.last_diary_time);
				ArrayList<MyDiary> tempList = new ArrayList<MyDiary>();
				if (diaryTime <= first && diaryTime >= last) {
					tempList.addAll(Arrays.asList(response.diaries));
					tempList.add(diary);
					Collections.sort(tempList, new DiaryComparator());
					response.diaries = tempList
							.toArray(new MyDiary[tempList.size()]);
					mySaveBoxDataChanged();
					isAdded=true;
				}
			}
		}
		if(!isAdded){
			listsafeboxResponse collectDiaryResponse=new GsonResponse2.listsafeboxResponse();
			collectDiaryResponse.diaries=new GsonResponse2.MyDiary[1];
			collectDiaryResponse.diaries[0]=diary;
			collectDiaryResponse.first_diary_time=diary.updatetimemilli;
			collectDiaryResponse.last_diary_time=diary.updatetimemilli;
			addSaveboxResponseFirst(collectDiaryResponse);
			mySaveBoxDataChanged();
		}
		
	}*/
	
	
//	/**
//	 * 删除本地日记
//	 */
//	public void removeLocalDiaryByID(ArrayList<String> diaryIDs){
//		if(diaryIDs!=null&&diaryIDs.size()>0){
//			for(int i=0;i<diaryIDs.size();i++){
//				removeLocalDiaryByID(diaryIDs.get(i));
//			}
//			myUnpublishDataChanged();
//			myHomepageDataChanged();
//		}
//	}
//	
//	/**
//	 * 删除myhomepage日记
//	 */
//	public void removeServerDiaryByID(ArrayList<String> diaryIDs){
//		if(diaryIDs!=null&&diaryIDs.size()>0){
//			for(int i=0;i<diaryIDs.size();i++){
//				removeMyHomePageByID(diaryIDs.get(i));
//			}
//			myHomepageDataChanged();
//			myPublishDataChanged();
//		}
//	}
	
	/*public void removeDiaryAndNotify(ArrayList<String> diaryIDs){
		if(diaryIDs!=null&&diaryIDs.size()>0){
			for(int i=0;i<diaryIDs.size();i++){
				removeLocalDiaryByID(diaryIDs.get(i));
				removeMyHomePageByID(diaryIDs.get(i));
				removeTagDiaryByID(diaryIDs.get(i));
				removeSaveboxByID(diaryIDs.get(i));
				removeShareDiaryByID(diaryIDs.get(i));
				removePraiseDiaryByID(diaryIDs.get(i));
				removeCollectDiaryByID(diaryIDs.get(i));
			}
			myUnuploadDataChanged();
			myHomepageDataChanged();
			myPublishDataChanged();
			myTagDataChanged();
			mySaveBoxDataChanged();
			myShareDataChanged();
			myPraiseDataChanged();
			myCollectDataChanged();
		}
	}*/
	
	/*public void removeDiaryAndNotify(String diaryID){
		if(diaryID!=null){
			removeDiaryByID(diaryID);
			myUnuploadDataChanged();
			myHomepageDataChanged();
			myPublishDataChanged();
			myTagDataChanged();
			mySaveBoxDataChanged();
			myShareDataChanged();
			myPraiseDataChanged();
			myCollectDataChanged();
		}
	}*/
	
	public synchronized void removeDiaryByID(String diaryID,boolean isNotifyUI){
		if(diaryID!=null){
			ArrayList<MyDiary> temp=new ArrayList<MyDiary>();
			for(int i=0;i<diaryDataEntities.diaryList.size();i++){
				MyDiary diary=diaryDataEntities.diaryList.get(i);
				if(diaryID.equals(diary.diaryid)){
					temp.add(diary);
				}
			}
			for(int i=0;i<temp.size();i++){
				MyDiary myDiary=temp.get(i);
				diaryDataEntities.diaryList.remove(myDiary);
				NetworkTaskManager.getInstance(getMyUserID()).removeTask(myDiary.diaryuuid);
			}
			if(isNotifyUI)
				diaryPageNotify();
		}
	}
	
	public synchronized void removeDiaryByUUID(String diaryuuid,boolean isNotifyUI){
		if(diaryuuid!=null){
			ArrayList<MyDiary> temp=new ArrayList<MyDiary>();
			for(int i=0;i<diaryDataEntities.diaryList.size();i++){
				MyDiary diary=diaryDataEntities.diaryList.get(i);
				if(diaryuuid.equals(diary.diaryuuid)){
					temp.add(diary);
				}
			}
			for(int i=0;i<temp.size();i++){
				MyDiary myDiary=temp.get(i);
				diaryDataEntities.diaryList.remove(myDiary);
				NetworkTaskManager.getInstance(getMyUserID()).removeTask(myDiary.diaryuuid);
			}
			if(isNotifyUI)
				diaryPageNotify();
		}
	}
	
	private void removeDiaryByIDs(String[] diaryIDs){
		if(diaryIDs!=null&&diaryIDs.length>0){
			for(int i=0;i<diaryIDs.length;i++){
				removeDiaryByID(diaryIDs[i],false);
			}
		}
	}
	
	public void removeDiaryByIDs(ArrayList<String> diaryIDs){
		if(diaryIDs!=null&&diaryIDs.size()>0){
			for(int i=0;i<diaryIDs.size();i++){
				removeDiaryByID(diaryIDs.get(i),false);
			}
			diaryPageNotify();
		}
	}
	
	public void removeDiaryByUUIDs(ArrayList<String> diaryuuids){
		if(diaryuuids!=null&&diaryuuids.size()>0){
			for(int i=0;i<diaryuuids.size();i++){
				removeDiaryByUUID(diaryuuids.get(i),false);
			}
			diaryPageNotify();
		}
	}
	
	public boolean createDiaryStructureById(String diaryID,Handler handler) {
		MyDiary diary = findLocalDiary(diaryID);
		if (diary != null) {
			Attachs mainAudioAttach = null;
			Attachs videoAttach = null;
			Attachs pictureAttach = null;
			Attachs shortAudioAttach = null;
			Attachs textAttach = null;
			ArrayList<Attachs> attachList = new ArrayList<Attachs>();
			String userID = ActiveAccount
					.getInstance(MainApplication.getAppInstance()).getUID();
			
			diaryAttach[] attachs = diary.attachs;
			ArrayList<diaryAttach> diaryAttachList = new ArrayList<GsonResponse2.diaryAttach>();
			
			if (attachs != null) {
				for (diaryAttach attach:attachs) {
					if ("1".equals(attach.attachtype))  {
						String attachType = "0";
						String keyStr = null;
						MediaValue mediaValue = null;
						if (attach.attachvideo != null && attach.attachvideo.length > 0 ) {
							for (MyAttachVideo videoAttach_video:attach.attachvideo) {
								if (videoAttach_video.playvideourl != null && !"".equals(videoAttach_video.playvideourl)) {
									keyStr = videoAttach_video.playvideourl;
									
									mediaValue = AccountInfo.getInstance(userID).mediamapping.getMedia(userID, keyStr);
									if (mediaValue != null) {
										attachType = videoAttach_video.videotype;
										break;
									}
								}
							}
						} 
						
						if (keyStr == null) {
							keyStr = attach.attachuuid;
							mediaValue = AccountInfo.getInstance(userID).mediamapping.getMedia(userID, keyStr);
						}
						
						if (mediaValue != null) {
							String videoAttachPath = HomeActivity.SDCARD_PATH + mediaValue.path;
							if (isFileExists(videoAttachPath)) {
								AccountInfo.getInstance(userID).mediamapping.setMedia(userID, attach.attachuuid, mediaValue);
								fillAttachByDiaryAttach(diaryAttachList,attachList,videoAttach, attach, ".mp4",attachType);
							}
						}
					} else if ("2".equals(attach.attachtype) && "1".equals(attach.attachlevel)) {
						String keyStr = null;
						String attachType = "1";
						MediaValue mediaValue = null;
						if (attach.attachaudio != null && attach.attachaudio.length > 0 ) {
							for(MyAttachAudio audioAttach:attach.attachaudio) {
								if ("1".equals(audioAttach.audiotype) && audioAttach.audiourl != null && !"".equals(audioAttach.audiourl)) {
									keyStr = audioAttach.audiourl;
									mediaValue = AccountInfo.getInstance(userID).mediamapping.getMedia(userID, keyStr);
									if (mediaValue != null) {
										attachType = audioAttach.audiotype;
										break;
									}
								}
							}
						} 
						
						if (keyStr == null) {
							keyStr = attach.attachuuid;
							mediaValue = AccountInfo.getInstance(userID).mediamapping.getMedia(userID, keyStr);
						}
						
						if (mediaValue != null) {
							String mainAudioAttachPath = HomeActivity.SDCARD_PATH + mediaValue.path;
							if (isFileExists(mainAudioAttachPath)) {
								AccountInfo.getInstance(userID).mediamapping.setMedia(userID, attach.attachuuid, mediaValue);
								fillAttachByDiaryAttach(diaryAttachList,attachList,mainAudioAttach, attach, ".mp4",attachType);
							}
						}
						
					} else if ("2".equals(attach.attachtype) && "0".equals(attach.attachlevel)){
						String keyStr = null;
						String attachType = "1";
						MediaValue mediaValue = null;
						if (attach.attachaudio != null && attach.attachaudio.length > 0 ) {
							for(MyAttachAudio audioAttach:attach.attachaudio) {
								if ("1".equals(audioAttach.audiotype) && audioAttach.audiourl != null && !"".equals(audioAttach.audiourl)) {
									keyStr = audioAttach.audiourl;
									mediaValue = AccountInfo.getInstance(userID).mediamapping.getMedia(userID, keyStr);
									if (mediaValue != null) {
										attachType = audioAttach.audiotype;
										break;
									}
								}
							}
						} 
						
						if (keyStr == null) {
							keyStr = attach.attachuuid;
							mediaValue = AccountInfo.getInstance(userID).mediamapping.getMedia(userID, keyStr);
						}
						
						if (mediaValue != null) {
							String shortAudioAttachPath = HomeActivity.SDCARD_PATH + mediaValue.path;
							if (isFileExists(shortAudioAttachPath)) {
								AccountInfo.getInstance(userID).mediamapping.setMedia(userID, attach.attachuuid, mediaValue);
								fillAttachByDiaryAttach(diaryAttachList,attachList,shortAudioAttach, attach, ".mp4",attachType);
							}
						}
					} else if ("3".equals(attach.attachtype)) {
						String keyStr = null;
						String attachType = "0";
						if (attach.attachimage != null && attach.attachimage.length > 0) {
							for (MyAttachImage imageAttach:attach.attachimage) {
								if ("0".equals(imageAttach.imagetype) && imageAttach.imageurl != null && !"".equals(imageAttach.imageurl)) {
									keyStr = imageAttach.imageurl;
									attachType = imageAttach.imagetype;
								}
							}
						} 
						
						if (keyStr == null) {
							keyStr = attach.attachuuid;
						}
						
						MediaValue mediaValue = AccountInfo.getInstance(userID).mediamapping.getMedia(userID, keyStr);
						if (mediaValue != null) {
							String videoAttachPath = HomeActivity.SDCARD_PATH + mediaValue.path;
							if (isFileExists(videoAttachPath)) {
								AccountInfo.getInstance(userID).mediamapping.setMedia(userID, attach.attachuuid, mediaValue);
								fillAttachByDiaryAttach(diaryAttachList,attachList,pictureAttach, attach, ".jpg",attachType);
							}
						}
					} else if ("4".equals(attach.attachtype)) {
						if (attach.content != null && !"".equals(attach.content)) {
							fillAttachByDiaryAttach(diaryAttachList,attachList,textAttach, attach, "","");
						}
					} 
				}
				diaryAttach [] diaryAttachs = diaryAttachList.toArray(new diaryAttach[diaryAttachList.size()]);
				diary.attachs = diaryAttachs;
				Attachs[] attachArray = attachList.toArray(new Attachs[attachList.size()]);
				String addressCode = "";
				AccountInfo account = AccountInfo.getInstance(userID);
				MyWeather weather = account.myWeather;
				if (weather != null) {
					addressCode = weather.addrCode;
				}
				if (attachs != null && attachs.length > 0) {
					GsonRequest2.createStructureRequest createStructure = Requester2.createEmptyStructure(handler, "", diary.diaryuuid, "1",
							"", "", diary.longitude, diary.latitude, diary.getTagIds(),
							diary.position, diary.longitude, diary.latitude,
							diary.diarytimemilli,addressCode, attachArray);
					createNewDiary(diaryAttachs,diary,createStructure);
					return true;
				}
			}
		}
		return false;
	}
	
	private void createNewDiary(diaryAttach[] diaryAttachs,MyDiary oldDiary,GsonRequest2.createStructureRequest createStructure) {
		GsonResponse2.MyDiary diary = new GsonResponse2.MyDiary();
		String userID = ActiveAccount.getInstance(ZApplication.getInstance())
				.getLookLookID();
		AccountInfo account = AccountInfo.getInstance(userID);
		diary.weather_info = oldDiary.weather_info;
		diary.weather = oldDiary.weather;
		diary.mood = oldDiary.mood;
		diary.userid = userID;
//		diary.diaryid = oldDiary.diaryuuid;// diaryID为空时将diaryuuid赋值给diaryID
		diary.diaryuuid = oldDiary.diaryuuid;
		diary.diarytimemilli = oldDiary.diarytimemilli;
		diary.updatetimemilli = oldDiary.updatetimemilli;
		diary.nickname = account.nickname;
		diary.publish_status = account.setmanager.getDiary_type();
		diary.position = oldDiary.position;
		diary.latitude = oldDiary.latitude;
		diary.longitude = oldDiary.longitude;
		diary.sex = ActiveAccount.getInstance(ZApplication.getInstance()).sex;
		diary.signature = account.signature;
		diary.headimageurl = account.headimageurl;
		diary.join_safebox = "0";
		diary.attachs = diaryAttachs;
		diary.request = createStructure;
		diary.sync_status = 0;
		diary.diary_status = "0";
		diary.resourcediaryid = "";
		diary.resourceuuid = "";
		diaryManager.saveDiaries(diary, true);
		
		// 删除副本日记对应主本日记中的副本字段数组中的对应项
		MyDiary originerDiary = diaryManager.findLocalDiary(oldDiary.resourcediaryid);
		if (originerDiary != null) {
			MyDuplicate[] duplicates = originerDiary.duplicate;
			int dupLen = 0;
			if (duplicates != null) {
				dupLen = duplicates.length;
			}
			
			if (dupLen > 0) {
				ArrayList<MyDuplicate> newDupList = new ArrayList<GsonResponse2.MyDuplicate>();
				int i = 0;
				for (i = 0;i < dupLen;i++) {
					if (!oldDiary.diaryid.equals(duplicates[i].diaryid)) {
						newDupList.add(duplicates[i]);
					}
				}
				MyDuplicate[] newDups = newDupList.toArray(new MyDuplicate[newDupList.size()]);
				originerDiary.duplicate = newDups;
			}
		}
		
		removeDiaryByID(oldDiary.diaryid, true);
	}
	
	private void fillAttachByDiaryAttach(ArrayList<diaryAttach> diaryAttachList,ArrayList<Attachs> attachList,Attachs attach,diaryAttach diaryAtc,String suffix,String attachType) {
		if (diaryAtc == null) {
			return;
		}
		attach = new Attachs();
		attach.attachid = "";
		attach.attachuuid = diaryAtc.attachuuid;
		attach.attach_type = diaryAtc.attachtype;
		attach.content = diaryAtc.content;
		if ("1".equals(attach.attach_type)) {
			attach.video_type = attachType;
			GsonResponse2.MyAttachVideo[] videoAttachs = new MyAttachVideo[1];
			videoAttachs[0] = new MyAttachVideo();
			videoAttachs[0].videotype = attachType;
			diaryAtc.attachvideo = videoAttachs;
		} else if ("2".equals(attach.attach_type)) {
			attach.audio_type = attachType;
			GsonResponse2.MyAttachAudio[] audioAttachs = new MyAttachAudio[1];
			audioAttachs[0] = new MyAttachAudio();
			audioAttachs[0].audiotype = attachType;
			diaryAtc.attachaudio = audioAttachs;
		} else if ("3".equals(attach.attach_type)) {
			GsonResponse2.MyAttachImage[] imageAttachs = new MyAttachImage[1];
			imageAttachs[0] = new MyAttachImage();
			imageAttachs[0].imagetype = attachType;
			diaryAtc.attachimage = imageAttachs;
			attach.photo_type = attachType;
		}
		attach.level = diaryAtc.attachlevel;
		if (CommonInfo.getInstance().myLoc != null) {
			attach.attach_latitude = String.valueOf(CommonInfo.getInstance().myLoc.latitude);
			attach.attach_logitude = String.valueOf(CommonInfo.getInstance().myLoc.longitude);
		}
		attach.suffix = suffix;
		attach.Operate_type = "1";
		attachList.add(attach);
		diaryAttachList.add(diaryAtc);
	}
	
	/*//根据日记ID获取该日记副本ID列表
	public ArrayList<String> getDiaryDuplicateIDs(String diaryID){
		ArrayList<String> diaryIDs=new ArrayList<String>();
		for(int i=0;i<diaryDataEntities.diaryList.size();i++){
			MyDiary myDiary=diaryDataEntities.diaryList.get(i);
			if(diaryID!=null&&diaryID.equals(myDiary.diaryid)){
				diaryIDs.add(diaryID);
				if(myDiary.duplicate!=null&&myDiary.duplicate.length>0){
					for(int j=0;j<myDiary.duplicate.length;j++){
						diaryIDs.add(myDiary.duplicate[j].diaryid);
					}
				}
			}
		}
		return diaryIDs;
	}*/
	
	/*public void modifyLocalDiaryByStructure(GsonResponse2.createStructureResponse structureResponse) {
		if (structureResponse != null && "0".equals(structureResponse.status)) {
			MyDiary localDiary = findLocalDiaryByUuid(structureResponse.diaryuuid);
			if (structureResponse.diaryid != null && !"".equals(structureResponse.diaryid)) {
				localDiary.diaryid = structureResponse.diaryid;
			}
			
			int attachsSize = 0;
			if (structureResponse.attachs != null) {
				attachsSize = structureResponse.attachs.length;
			}
			if (structureResponse.attachs != null && attachsSize > 0) {// 创建日记结构成功
				for (int i = 0;i < attachsSize; i++) {
					MyAttach myAttach = structureResponse.attachs[i];
					diaryAttach attach = localDiary.getAttach(myAttach.attachuuid);
					if (attach != null) {
						fillDiaryAttach(attach,myAttach);
					}
				}
			}
			diaryDataChanged();
		}
	}*/
	
	/*private void fillDiaryAttach(diaryAttach attach,MyAttach myAttach) {
		attach.attachid = myAttach.attachid;
		if ("1".equals(attach.attachtype)) {
			if (attach.attachvideo != null && attach.attachvideo.length > 0) {
				attach.attachvideo[0].playvideourl = myAttach.path;
			}
		} else if ("2".equals(attach.attachtype)) {
			if (attach.attachaudio != null && attach.attachaudio.length > 0) {
				attach.attachaudio[0].audiotype = myAttach.path;
			}
		} else if ("3".equals(attach.attachtype)) {
			if (attach.attachimage != null && attach.attachimage.length > 0) {
				attach.attachimage[0].imagetype = myAttach.path;
			}
		}
	}*/
	
	private boolean isFileExists(String filepath) {
		File file = new File(filepath);
		return file.exists();
	}
	
	private void diaryPageNotify(){
		myHomepageDataChanged();
		myUnSynchronizeDataChanged();
		mySaveBoxDataChanged();
		myTagDataChanged();
		myShareDataChanged();
	}
	
	/**
	 * 清除保险箱数据
	 */
	public void clearSaveBoxData(){
		//TODO 清除保险箱数据
	}
	
	//根据日记ID删除收藏日记
	public void removeCollectDiaryByID(String diaryID){
		if(diaryDataEntities.collectDiaryList!=null&&diaryDataEntities.collectDiaryList.size()>0){
			for(int i=0;i<diaryDataEntities.collectDiaryList.size();i++){
				MyDiary myDiary=diaryDataEntities.collectDiaryList.get(i);
				if(myDiary.diaryid.equals(diaryID)){
					diaryDataEntities.collectDiaryList.remove(i);
					break;
				}
			}
			myCollectDataChanged();
		}
	}
	
	public String getMyUserID(){
		return ActiveAccount.getInstance(ZApplication.getInstance()).getUID();
	}
	
	//根据日记ID删除赞日记
	public void removePraiseDiaryByID(String diaryID){
		ArrayList<MyDiary> diaries= diaryDataEntities.praiseDiaryMap.get(getMyUserID());
		if(diaries!=null&&diaries.size()>0){
			for(int i=0;i<diaries.size();i++){
				if(diaries.get(i).diaryid.equals(diaryID)){
					diaries.remove(i);
					break;
				}
			}
			myPraiseDataChanged();
		}
	}
	
	public class DiaryComparator implements Comparator<MyDiary> {
        public int compare(MyDiary arg0, MyDiary arg1) {
            try {
				if (Long.parseLong(arg0.diarytimemilli)<Long.parseLong(arg1.diarytimemilli)) {
				    return 1;
				}
				if (Long.parseLong(arg0.diarytimemilli)==Long.parseLong(arg1.diarytimemilli)) {
				    return 0;
				}
			} catch (NumberFormatException e) {
				e.printStackTrace();
			}
            return -1;
        }
    }
	
	private String firstTime_homepage="";
	private String firstTime_otherpage = "";
	
	/**
	 * 获取下拉刷新数据的时间戳 也就是日记列表中离现在最近的时间
	 */
	public String getReponseFirsttime(FilterType filterType){
		switch (filterType) {
		case ALL:
			return firstTime_homepage;
		case OTHERPAGE:
			return firstTime_otherpage;
		default:
			break;
		}
		return null;
	}
	
	private String lastTime_homepage="";
	private String lastTime_otherpage="";
	/**
	 * 获取上拉加载数据的时间戳，也就是日记列表中离现在最远的时间
	 */
	public String getReponseEndtime(FilterType filterType){
		switch (filterType) {
		case ALL:
			return lastTime_homepage;
		case OTHERPAGE:
			return lastTime_otherpage;
		default:
			break;
		}
		return null;
	}

	public enum FilterType {
		/**全部日记*/
		ALL, 
		/**已同步日记*/
		SYNCHRONIZED,
		/**未同步日记*/
		UNSYNCHRONIZED,
		/**保险箱日记*/
		SAVEBOX, 
		/**标签*/
		/**已分享日记（只是用于日记页 已分享 选项）*/
		SHARED, 
		TAG,
		/**他人日记*/
		OTHERPAGE,
		/**互动分享*/
		INTERACT_SHARE,
		/**互动收藏*/
		INTERACT_COLLECT,
		/**互动赞*/
		INTERACT_PRAISE,
		/**互动评论*/
		INTERACT_COMMENT,
		
		/**
		 * 无操作状态
		 */
		NULL
	};
	
	
	private ArrayList<MydiaryDataChangedListener> dataChangedListenerList=new ArrayList<MydiaryDataChangedListener>();
	public void addDataChangedListener(MydiaryDataChangedListener mydiaryDataChangedListener){
		dataChangedListenerList.add(mydiaryDataChangedListener);
	}
	
	public void removeDataChangedListener(MydiaryDataChangedListener mydiaryDataChangedListener){
		dataChangedListenerList.remove(mydiaryDataChangedListener);
	}
	
	private void myHomepageDataChanged(){
		for(int i=0;i<dataChangedListenerList.size();i++){
			dataChangedListenerList.get(i).myHomepageDataChanged();
		}
	}
	
	private void mySynchronizeDataChanged(){
		for(int i=0;i<dataChangedListenerList.size();i++){
			dataChangedListenerList.get(i).myPublishDataChanged();
		}
	}
	
	private void mySaveBoxDataChanged(){
		for(int i=0;i<dataChangedListenerList.size();i++){
			dataChangedListenerList.get(i).mySaveBoxDataChanged();
		}
	}
	
	//未上传
	private void myUnSynchronizeDataChanged(){
		for(int i=0;i<dataChangedListenerList.size();i++){
			dataChangedListenerList.get(i).myUnuploadDataChanged();
		}
	}
	
	private void myTagDataChanged(){
		for(int i=0;i<dataChangedListenerList.size();i++){
			dataChangedListenerList.get(i).myTagDataChanged();
		}
	}
	
	private void myShareDataChanged(){
		for(int i=0;i<dataChangedListenerList.size();i++){
			dataChangedListenerList.get(i).myShareDataChanged();
		}
	}
	
	public void myCollectDataChanged(){
		for(int i=0;i<dataChangedListenerList.size();i++){
			dataChangedListenerList.get(i).myCollectDataChanged();
		}
	}
	
	public void myPraiseDataChanged(){
		for(int i=0;i<dataChangedListenerList.size();i++){
			dataChangedListenerList.get(i).myPraiseDataChanged();
		}
	}
	
	//日记去重
	private void removeDuplicateDiaries(MyDiary... myDiaries){
		if(myDiaries!=null&&myDiaries.length>0){
			for(int i=0;i<myDiaries.length;i++){
				removeDiary(myDiaries[i]);
			}
			//此方法被调用，表明本地有日记被替换，通知详情页更新日记
			LocalBroadcastManager.getInstance(MainApplication.getAppInstance()).sendBroadcast(new Intent(ACTION_DIARY_SYNCHRONIZED));
		}
	}
	
	private void removeDiary(MyDiary myDiary){
		if(myDiary!=null){
			ArrayList<MyDiary> temp=new ArrayList<MyDiary>();
			for(int i=0;i<diaryDataEntities.diaryList.size();i++){
				MyDiary diary=diaryDataEntities.diaryList.get(i);
				if(myDiary.diaryuuid.equals(diary.diaryuuid)){
					temp.add(diary);
					myDiary.diary_status=diary.diary_status;
					myDiary.publish_status=diary.publish_status;
					myDiary.position_status=diary.position_status;
					myDiary.join_safebox=diary.join_safebox;
					String longAudioUrl=getLongAudioUrl(diary);
					String shortAudioUrl=getShortAudioUrl(diary);
					String imageUrl=getImageUrl(diary);
//					String highVideoUrl=getHighVideoUrl(diary);
//					String lowVideoUrl=getLowVideoUrl(diary);
					String mappedVideoUrl=getMapedVideoUrl(diary);
					String videoCoverUrl=getVideoCoverUrl(diary);
					if(myDiary.sync_status>=4&&myDiary.attachs!=null&&myDiary.attachs.length>0){
						myDiary.sync_status=diary.sync_status;
						for(int j=0;j<myDiary.attachs.length;j++){
							diaryAttach attach=myDiary.attachs[j];
							if("1".equals(attach.attachlevel)&&attach.attachaudio!=null&&attach.attachaudio.length>0){
								for(int k=0;k<attach.attachaudio.length;k++){
									if("1".equals(attach.attachaudio[k].audiotype)&&longAudioUrl!=null){
										relpaceMedaiMapping(longAudioUrl,attach.attachaudio[k].audiourl);
										myDiary.sync_status=6;
										Log.d(TAG, "使用服务器长录音url替换本地长录音url 服务器长录音url："+attach.attachaudio[k].audiourl +"\n 本地长录音url："+longAudioUrl);
									}
								}
							}
							if("0".equals(attach.attachlevel)&&attach.attachaudio!=null&&attach.attachaudio.length>0){
								for(int k=0;k<attach.attachaudio.length;k++){
									if("1".equals(attach.attachaudio[k].audiotype)&&shortAudioUrl!=null){
										relpaceMedaiMapping(shortAudioUrl,attach.attachaudio[k].audiourl);
										myDiary.sync_status=6;
										Log.d(TAG, "使用服务器短录音url替换本地短录音url 服务器短录音url："+attach.attachaudio[k].audiourl +"\n 本地短录音url："+shortAudioUrl);
									}
								}
							}
							if("1".equals(attach.attachlevel)&&attach.attachimage!=null&&attach.attachimage.length>0){
								for(int k=0;k<attach.attachimage.length;k++){
									if("0".equals(attach.attachimage[k].imagetype)&&imageUrl!=null){
										relpaceMedaiMapping(imageUrl,attach.attachimage[k].imageurl);
										myDiary.sync_status=6;
										Log.d(TAG, "使用服务器图片url替换本地图片url 服务器图片url："+attach.attachimage[k].imageurl +"\n 本地图片url："+imageUrl);
									}
								}
							}
							if("1".equals(attach.attachlevel)&&attach.attachvideo!=null&&attach.attachvideo.length>0){
								for(int k=0;k<attach.attachvideo.length;k++){
									/*if("0".equals(attach.attachvideo[k].videotype)&&highVideoUrl!=null){
										relpaceMedaiMapping(highVideoUrl,attach.attachvideo[k].playvideourl);
									}
									if("1".equals(attach.attachvideo[k].videotype)&&lowVideoUrl!=null){
										relpaceMedaiMapping(lowVideoUrl,attach.attachvideo[k].playvideourl);
									}*/
//									if(highVideoUrl!=null){
//										relpaceMedaiMapping(highVideoUrl,attach.attachvideo[k].playvideourl);
//										myDiary.sync_status=6;
//									}
//									if(lowVideoUrl!=null){
//										relpaceMedaiMapping(lowVideoUrl,attach.attachvideo[k].playvideourl);
//										myDiary.sync_status=6;
//									}
									if(mappedVideoUrl!=null){
										relpaceMedaiMapping(mappedVideoUrl,attach.attachvideo[k].playvideourl);
										myDiary.sync_status=6;
										Log.d(TAG, "使用服务器视频url替换本地视频url 服务器视频url："+attach.attachvideo[k].playvideourl +"\n 本地视频url："+mappedVideoUrl);
										break;
									}
								}
							}
							if("1".equals(attach.attachlevel)&&"1".equals(attach.attachtype)&&videoCoverUrl!=null){
								if(attach.videocover!=null&&attach.videocover.length()>0){
									relpaceMedaiMapping(videoCoverUrl,attach.videocover);
									Log.d(TAG, "使用服务器封面url替换本地封面url 服务器封面url："+attach.videocover +"\n 本地封面url："+videoCoverUrl);
								}else{
									attach.videocover=videoCoverUrl;
									Log.d(TAG, "服务器封面url未空，使用本地封面 本地封面url："+videoCoverUrl);
								}
							}
						}
					}
				}
			}
			for(int i=0;i<temp.size();i++){
				diaryDataEntities.diaryList.remove(temp.get(i));
			}
		}
	}
	
	private String getVideoCoverUrl(MyDiary myDiary){
		if(myDiary.attachs!=null&&myDiary.attachs.length>0){
			for(int j=0;j<myDiary.attachs.length;j++){
				diaryAttach attach=myDiary.attachs[j];
				if("1".equals(attach.attachlevel)&&attach.videocover!=null&&attach.videocover.length()>0){
					return attach.videocover;
				}
			}
		}
		return null;
	}
	
	//获取日记长音频url
	private String getLongAudioUrl(MyDiary myDiary){
		if(myDiary.attachs!=null&&myDiary.attachs.length>0){
			for(int j=0;j<myDiary.attachs.length;j++){
				diaryAttach attach=myDiary.attachs[j];
				if("1".equals(attach.attachlevel)&&attach.attachaudio!=null&&attach.attachaudio.length>0){
					for(int k=0;k<attach.attachaudio.length;k++){
						if("1".equals(attach.attachaudio[k].audiotype)){
							return attach.attachaudio[k].audiourl;
						}
					}
				}
			}
		}
		return null;
	}
	//获取日记短音频url
	private String getShortAudioUrl(MyDiary myDiary){
		if(myDiary.attachs!=null&&myDiary.attachs.length>0){
			for(int j=0;j<myDiary.attachs.length;j++){
				diaryAttach attach=myDiary.attachs[j];
				if("0".equals(attach.attachlevel)&&attach.attachaudio!=null&&attach.attachaudio.length>0){
					for(int k=0;k<attach.attachaudio.length;k++){
						if("1".equals(attach.attachaudio[k].audiotype)){
							return attach.attachaudio[k].audiourl;
						}
					}
				}
			}
		}
		return null;
	}
	//获取日记原图片url
	private String getImageUrl(MyDiary myDiary){
		if(myDiary.attachs!=null&&myDiary.attachs.length>0){
			for(int j=0;j<myDiary.attachs.length;j++){
				diaryAttach attach=myDiary.attachs[j];
				if("1".equals(attach.attachlevel)&&attach.attachimage!=null&&attach.attachimage.length>0){
					for(int k=0;k<attach.attachimage.length;k++){
						if("0".equals(attach.attachimage[k].imagetype)){
							return attach.attachimage[k].imageurl;
						}
					}
				}
			}
		}
		return null;
	}
	
	//获取本地视频映射url
	private String getMapedVideoUrl(MyDiary myDiary){
		if(myDiary.attachs!=null&&myDiary.attachs.length>0){
			for(int j=0;j<myDiary.attachs.length;j++){
				diaryAttach attach=myDiary.attachs[j];
				if("1".equals(attach.attachlevel)&&attach.attachvideo!=null&&attach.attachvideo.length>0){
					for(int k=0;k<attach.attachvideo.length;k++){
						String url="";
						if(attach.attachvideo[k].playvideourl!=null&&attach.attachvideo[k].playvideourl.length()>0){
							url=attach.attachvideo[k].playvideourl;
						}else{
							url=attach.attachuuid;
						}
						String uid = ActiveAccount
								.getInstance(MainApplication.getAppInstance()).getUID();
						MediaValue mv = AccountInfo.getInstance(uid).mediamapping.getMedia(uid,
								url);
						if (MediaValue.checkMediaAvailable(mv, 5)) {
							Log.d(TAG, "getMapedVideoUrl map found url="+url);
							return url;
						}
					}
				}
			}
		}
		return null;
	}
	
	//获取日记高清原视频url
	private String getHighVideoUrl(MyDiary myDiary){
		if(myDiary.attachs!=null&&myDiary.attachs.length>0){
			for(int j=0;j<myDiary.attachs.length;j++){
				diaryAttach attach=myDiary.attachs[j];
				if("1".equals(attach.attachlevel)&&attach.attachvideo!=null&&attach.attachvideo.length>0){
					for(int k=0;k<attach.attachvideo.length;k++){
						if("0".equals(attach.attachvideo[k].videotype)){
							return attach.attachvideo[k].playvideourl;
						}
					}
				}
			}
		}
		return null;
	}
	//获取日记普清原视频url
	private String getLowVideoUrl(MyDiary myDiary){
		if(myDiary.attachs!=null&&myDiary.attachs.length>0){
			for(int j=0;j<myDiary.attachs.length;j++){
				diaryAttach attach=myDiary.attachs[j];
				if("1".equals(attach.attachlevel)&&attach.attachvideo!=null&&attach.attachvideo.length>0){
					for(int k=0;k<attach.attachvideo.length;k++){
						if("1".equals(attach.attachvideo[k].videotype)){
							return attach.attachvideo[k].playvideourl;
						}
					}
				}
			}
		}
		return null;
	}
	
	//替换原有映射关系
	private void relpaceMedaiMapping(String original,String replace){
		String uid = ActiveAccount
				.getInstance(MainApplication.getAppInstance()).getUID();
		if(uid!=null){
			MediaValue mv = AccountInfo.getInstance(uid).mediamapping.getMedia(uid,
					original);
			ZLog.printObject("=====" + mv + "======");
			if(mv!=null){
				//私信页可能还会用到原有映射关系
//				AccountInfo.getInstance(uid).mediamapping.delMedia(uid, original);
				AccountInfo.getInstance(uid).mediamapping.setMedia(uid, replace, mv);
			}
		}
	}
	
	public ArrayList<MyDiary> getMyhomepageDiaryByList(ArrayList<MyDiary> diaries,boolean isIncludeSaveBox){
		ArrayList<MyDiary> res=new ArrayList<MyDiary>();
		Log.d(TAG, "diaries="+diaries);
		if(diaries!=null&&diaries.size()>0){
			Collections.sort(diaries, new DiaryComparator());
			MyDiary lastDiary=diaries.get(diaries.size()-1);
			if(lastDiary.diarytimemilli!=null&&DateUtils.isNum(lastDiary.diarytimemilli)){
				long updateTime=Long.parseLong(lastDiary.diarytimemilli);
				//取出最后一条之前所有的
				for(int i=0;i<diaryDataEntities.diaryList.size();i++){
					MyDiary myDiary=diaryDataEntities.diaryList.get(i);
					long localUpdateTime=Long.parseLong(myDiary.diarytimemilli);
					if(localUpdateTime>=updateTime&&myDiary.userid.equals(getMyUserID())&&isIncludeSavebox(myDiary,isIncludeSaveBox)){
						res.add(myDiary);
						if(PAGE_SIZE==res.size())return res;
					}
				}
			}
		}
		if(0==res.size())
			res=getMyHomepageDiary(getMyUserID(), 0, isIncludeSaveBox);
		Log.d(TAG, "res="+res);
		return res;
	}
	
	private boolean isIncludeSavebox(MyDiary myDiary,boolean isIncludeSaveBox){
		if(!isIncludeSaveBox&&"1".equals(myDiary.join_safebox)){
			return false;
		}
		return true;
//		return (isIncludeSaveBox&&"1".equals(myDiary.join_safebox))||(!isIncludeSaveBox&&("0".equals(myDiary.join_safebox)||null==myDiary.join_safebox));
	}
	
	public ArrayList<MyDiary> getUnsynchronizeDiaryByList(ArrayList<MyDiary> diaries,boolean isIncludeSaveBox){
		ArrayList<MyDiary> res=new ArrayList<MyDiary>();
		if(diaries!=null&&diaries.size()>0){
			Collections.sort(diaries, new DiaryComparator());
			MyDiary lastDiary=diaries.get(diaries.size()-1);
			if(lastDiary.diarytimemilli!=null&&lastDiary.diarytimemilli.length()>0){
				long updateTime=Long.parseLong(lastDiary.diarytimemilli);
				//取出最后一条之前所有的
				for(int i=0;i<diaryDataEntities.diaryList.size();i++){
					MyDiary myDiary=diaryDataEntities.diaryList.get(i);
					long localUpdateTime=Long.parseLong(myDiary.diarytimemilli);
					if(myDiary.sync_status<4&&localUpdateTime>=updateTime&&myDiary.userid.equals(getMyUserID())&&isIncludeSavebox(myDiary,isIncludeSaveBox)){
						res.add(myDiary);
						if(PAGE_SIZE==res.size())return res;
					}
				}
			}
		}
		if(0==res.size())
			res=getUnSyncDiary(0, isIncludeSaveBox);
		return res;
	}
	public ArrayList<MyDiary> getmySaveBoxDiaryByList(ArrayList<MyDiary> diaries){
		ArrayList<MyDiary> res=new ArrayList<MyDiary>();
		if(diaries!=null&&diaries.size()>0){
			Collections.sort(diaries, new DiaryComparator());
			MyDiary lastDiary=diaries.get(diaries.size()-1);
			if(lastDiary.diarytimemilli!=null&&lastDiary.diarytimemilli.length()>0){
				long updateTime=Long.parseLong(lastDiary.diarytimemilli);
				//取出最后一条之前所有的
				for(int i=0;i<diaryDataEntities.diaryList.size();i++){
					MyDiary myDiary=diaryDataEntities.diaryList.get(i);
					long localUpdateTime=Long.parseLong(myDiary.diarytimemilli);
					if(localUpdateTime>=updateTime&&myDiary.userid.equals(getMyUserID())&&"1".equals(myDiary.join_safebox)){
						res.add(myDiary);
						if(PAGE_SIZE==res.size())return res;
					}
				}
			}
		}
		if(0==res.size())
			res=getSaveboxDiary(getMyUserID(), 0);
		return res;
	}
	
	public ArrayList<MyDiary> getmyTagDiaryByList(ArrayList<MyDiary> diaries,boolean isIncludeSaveBox){
		ArrayList<MyDiary> res=new ArrayList<MyDiary>();
		if(diaries!=null&&diaries.size()>0){
			Collections.sort(diaries, new DiaryComparator());
			MyDiary lastDiary=diaries.get(diaries.size()-1);
			if(lastDiary.diarytimemilli!=null&&lastDiary.diarytimemilli.length()>0){
				long updateTime=Long.parseLong(lastDiary.diarytimemilli);
				//取出最后一条之前所有的
				for(int i=0;i<diaryDataEntities.diaryList.size();i++){
					MyDiary myDiary=diaryDataEntities.diaryList.get(i);
					long localUpdateTime=Long.parseLong(myDiary.diarytimemilli);
					com.cmmobi.looklook.common.gson.GsonResponse2.TAG[] tags=myDiary.tags;
					if(localUpdateTime>=updateTime&&myDiary.userid.equals(getMyUserID())&&isIncludeSavebox(myDiary,isIncludeSaveBox)&&tags!=null&&tags.length>0&&tag!=null){
						for(int j=0;j<tags.length;j++){
							if(tag.name.equals(tags[j].name)){
								res.add(myDiary);
								if(PAGE_SIZE==res.size())return res;
							}
						}
					}
				}
			}
		}
		if(0==res.size())
			res=getTagDiary(getMyUserID(), 0, isIncludeSaveBox);
		return res;
	}
	
	public ArrayList<MyDiary> getMySharedDiaryByList(ArrayList<MyDiary> diaries,boolean isIncludeSaveBox){
		ArrayList<MyDiary> res=new ArrayList<MyDiary>();
		if(diaries!=null&&diaries.size()>0){
			Collections.sort(diaries, new DiaryComparator());
			MyDiary lastDiary=diaries.get(diaries.size()-1);
			if(lastDiary.diarytimemilli!=null&&lastDiary.diarytimemilli.length()>0){
				long updateTime=Long.parseLong(lastDiary.diarytimemilli);
				//取出最后一条之前所有的
				for(int i=0;i<diaryDataEntities.diaryList.size();i++){
					MyDiary myDiary=diaryDataEntities.diaryList.get(i);
					long localUpdateTime=Long.parseLong(myDiary.diarytimemilli);
					if(localUpdateTime>=updateTime&&myDiary.userid.equals(getMyUserID())&&"2".equals(myDiary.diary_status)&&isIncludeSavebox(myDiary,isIncludeSaveBox)){
						res.add(myDiary);
						if(PAGE_SIZE==res.size())return res;
					}
				}
			}
		}
		if(0==res.size())
			res=getSharedDiary(0, isIncludeSaveBox);
		return res;
	}
	
	public interface MydiaryDataChangedListener{
		void myHomepageDataChanged();
		void myPublishDataChanged();
		void mySaveBoxDataChanged();
		void myUnuploadDataChanged();
		void myTagDataChanged();
		void myShareDataChanged();
		void myCollectDataChanged();
		void myPraiseDataChanged();
	}

	public ArrayList<String> getRemoveSinaIdList() {
		return diaryDataEntities.removeSinaIdList;
	}
	public ArrayList<String> getRemoveTencentIdList() {
		return diaryDataEntities.removeTencentIdList;
	}

	public ArrayList<String> getRemoveRenrenIdList() {
		return diaryDataEntities.removeRenrenIdList;
	}

	public ArrayList<MyDiaryids> getCollectDiariesIDList() {
		return diaryDataEntities.collectDiariesIDList;
	}

	public ArrayList<MyDiaryids> getPraisedDiariesIDList() {
		return diaryDataEntities.praisedDiariesIDList;
	}
	
	/**
	 * @description 根据id增加微博分享轨迹
	 * @param diaryid 微博id
	 * @param snsTrace 轨迹详情
	 */
	public void addSnsTrace(String diaryuuid, ShareSNSTrace snsTrace)
	{
		MyDiary diary = findLocalDiary(diaryuuid);
		if(diary == null)
		{
			diary = findLocalDiaryByUuid(diaryuuid);
		}
		
		if(diary != null)
		{
			if(diary.sns == null)
			{
				diary.sns = new ShareSNSTrace[0];
			}
			ShareSNSTrace[] copyArray = Arrays.copyOf(diary.sns, (diary.sns.length+1));
			copyArray[diary.sns.length] = snsTrace;
			diary.sns = copyArray;
		}
		
//		Iterator<MyDiary> ite = diaryDataEntities.diaryList.iterator();
//		// 增加一个逻辑，当本地不存在该日记的时候，新增一条，对应在推荐页面能保持状态
//		boolean flag = false;
//		while(ite.hasNext())
//		{
//			MyDiary diary = ite.next();
//			if(diary.diaryid.equals(myDiary.diaryid))
//			{
//				
//				if(diary.sns == null)
//				{
//					diary.sns = new ShareSNSTrace[0];
//				}
//				ShareSNSTrace[] copyArray = Arrays.copyOf(diary.sns, (diary.sns.length+1));
//				copyArray[diary.sns.length] = snsTrace;
//				diary.sns = copyArray;
//				
//				flag = true;
//				
//				//也许不能break
////				break;
//			}
//		}
//		
//		if(!flag)
//		{
//			MyDiary myDiary = new MyDiary();
//			if(myDiary.sns == null)
//			{
//				myDiary.sns = new ShareSNSTrace[0];
//			}
//			ShareSNSTrace[] copyArray = Arrays.copyOf(myDiary.sns, (myDiary.sns.length+1));
//			copyArray[myDiary.sns.length] = snsTrace;
//			myDiary.sns = copyArray;
//			diaryDataEntities.diaryList.add(myDiary);
//		}
		
	}
	
	/**
	 * @description 根据id删除sns分享
	 * @param weiboid 微博id
	 */
	public void deleteSnsTrace(String diaryuuid, String snsId)
	{
		Iterator<MyDiary> ite = diaryDataEntities.diaryList.iterator();
		while(ite.hasNext())
		{
			MyDiary diary = ite.next();
			if(diary.diaryuuid != null && diary.diaryuuid.equals(diaryuuid))
			{
				
				if(diary.sns == null)
				{
					return;
				}
				else
				{
					List<ShareSNSTrace> simList = Arrays.asList(diary.sns);
					ArrayList<ShareSNSTrace> tmpList = new ArrayList<ShareSNSTrace>(simList);
					Iterator<ShareSNSTrace> snsIte = tmpList.iterator();
					while(snsIte.hasNext())
					{
						ShareSNSTrace trace = snsIte.next();
						boolean flag = false;
						for(int i = 0; i < trace.shareinfo.length; i++)
						{
							if(trace.shareinfo[i].snsid != null &&
									trace.shareinfo[i].snsid.equals(snsId))
							{
								flag = true;
							}
						}
						if(flag)
						{
							snsIte.remove();
						}
					}
					diary.sns = tmpList.toArray(new ShareSNSTrace[tmpList.size()]);
				}
			}
		}
	}
	
	/**
	 * @description 根据id删除looklook分享
	 * @param diaryid 微博id
	 */
	public void deleteSnsTraceForLooklook(String diaryid)
	{
		Iterator<MyDiary> ite = diaryDataEntities.diaryList.iterator();
		while(ite.hasNext())
		{
			MyDiary diary = ite.next();
			if(diary.diaryid.equals(diaryid))
			{
				
				if(diary.sns == null)
				{
					return;
				}
				else
				{
					List<ShareSNSTrace> simList = Arrays.asList(diary.sns);
					ArrayList<ShareSNSTrace> tmpList = new ArrayList<ShareSNSTrace>(simList);
					Iterator<ShareSNSTrace> snsIte = tmpList.iterator();
					while(snsIte.hasNext())
					{
						ShareSNSTrace trace = snsIte.next();
						boolean flag = false;
						for(int i = 0; i < trace.shareinfo.length; i++)
						{
							if(trace.shareinfo[i].snstype.equals("0"))
							{
								flag = true;
							}
						}
						if(flag)
						{
							snsIte.remove();
						}
					}
					diary.sns = tmpList.toArray(new ShareSNSTrace[tmpList.size()]);
				}
			}
		}
	}
	
	/**
	 * @description 根据id更新微博分享轨迹
	 * @param diaryid
	 * @return 轨迹
	 */
	public ShareSNSTrace[] getSnsTrace(String diaryid)
	{
		Iterator<MyDiary> ite = diaryDataEntities.diaryList.iterator();
		while(ite.hasNext())
		{
			MyDiary diary = ite.next();
			if(diary.diaryid.equals(diaryid))
			{
				
				return diary.sns;
			}
		}
		return null;
	}
	
	/**
	 * @description 返回指定id的分享类型
	 * @param diary 日记id
	 * @return 类型 1全部人可见（黑名单人除外） 2关注人可见  4仅自己可见
	 */
	public String getPublishShareStatus(String diaryuuid)
	{
		Iterator<MyDiary> ite = diaryDataEntities.diaryList.iterator();
		while(ite.hasNext())
		{
			MyDiary diary = ite.next();
			if(diary.diaryuuid.equals(diaryuuid))
			{
				
				return diary.publish_status;
			}
		}
		return null;
	}
	
	/**
	 * @description 返回指定id的位置分享类型
	 * @param diary 日记id
	 * @return 类型 1全部人可见（黑名单人除外） 2关注人可见  4仅自己可见
	 */
	public String getPositionShareStatus(String diaryuuid)
	{
		Iterator<MyDiary> ite = diaryDataEntities.diaryList.iterator();
		while(ite.hasNext())
		{
			MyDiary diary = ite.next();
			if(diary.diaryuuid.equals(diaryuuid))
			{
				
				return diary.position_status;
			}
		}
		return null;
	}
	
	/**
	 * @description 保持分享类型
	 * @param 日记id
	 * @param 分享类型
	 * @param 位置分享类型
	 */
	public void setShareStatus(String diaryuuid, String diaryStatus, String positinStatus)
	{
		Iterator<MyDiary> ite = diaryDataEntities.diaryList.iterator();
		while(ite.hasNext())
		{
			MyDiary diary = ite.next();
			if(diary.diaryuuid.equals(diaryuuid))
			{
				diary.publish_status = diaryStatus;
				diary.position_status = positinStatus;
			}
		}
	}
	
	/**
	 * @description 删除本地日记中对应的分享轨迹
	 * @param sns_type 第三方类型
	 * @return void
	 */
	public void removeShareStatus(String snstype)
	{
		String myUserID = ActiveAccount.getInstance(ZApplication.getInstance()).getUID();
		if(diaryDataEntities.diaryList.isEmpty()){
			return;
		}
		Iterator<MyDiary> ite = diaryDataEntities.diaryList.iterator();
		while(ite.hasNext())
		{
			MyDiary diary = ite.next();
			if(diary.userid.equals(myUserID))
			{
				if(diary.sns == null){
					continue;
				}
				for(int i=0; i< diary.sns.length; i++){
					if(diary.sns[i].shareinfo == null){
						continue;
					}
					for(int j= 0; j < diary.sns[i].shareinfo.length; j++){
						if(diary.sns[i].shareinfo[j].snstype.equals(snstype)){
							if(diary.sns.length == 1 && diary.sns[i].shareinfo.length == 1){
								diary.sns = null;
								break;
							}else if (diary.sns.length > 1 && diary.sns[i].shareinfo.length == 1){
								ShareSNSTrace[] shareSNSTraces = new ShareSNSTrace[diary.sns.length -1];
								int n = 0;
								for(int m=0 ; m<diary.sns.length; m++){
									if(m != i){
										shareSNSTraces[n] = diary.sns[m];
										n++;
									}
								}
								diary.sns = shareSNSTraces;
								i--;
								break;
							}else{
								ShareInfo[] shareInfos = new ShareInfo[diary.sns[i].shareinfo.length -1];
								int n = 0;
								for(int m=0 ; m<diary.sns[i].shareinfo.length; m++){
									if(m != j){
										shareInfos[n] = diary.sns[i].shareinfo[m];
										n++;
									}
								}
								diary.sns[i].shareinfo = shareInfos;
								j--;
							}
						}
					}
				}
			}
		}
	
	}
		
	/**
	 * @description 判断diaryID对应的本地日记中对应的分享轨迹是否为空
	 * @param sns_type 第三方类型
	 * @return void
	 */
	public boolean getShareStatusByDiaryId(String diaryID)
	{
		String myUserID = ActiveAccount.getInstance(ZApplication.getInstance()).getUID();
		if(diaryDataEntities.diaryList.isEmpty()){
			return false;
		}
		MyDiary diary = findLocalDiary(diaryID);
		if(diary == null || ! diary.userid.equals(myUserID) || diary.sns == null || diary.sns.length == 0){
			return false;
		}
		for(int i=0; i< diary.sns.length; i++){
			if(diary.sns[i].shareinfo == null || diary.sns[i].shareinfo.length == 0){
				continue;
			}
			for(int j=0; j<diary.sns[i].shareinfo.length; j++){
				if(diary.sns[i].shareinfo[j] != null && !diary.sns[i].shareinfo[j].snstype.equals("0")){
					return true;
				}
			}
		}
		return false;	
	}
	//注销保险箱时，将保险箱的内容移出
		/*public void moveSaveboxDiaryToNormal(String userID){
			for(int i=0;i<diaryDataEntities.diaryList.size();i++){
				MyDiary myDiary=diaryDataEntities.diaryList.get(i);
				if(userID.equals(myDiary.userid)&&"1".equals(myDiary.join_safebox)){
					myDiary.join_safebox = "0";
				}
			}
		}*/
}
