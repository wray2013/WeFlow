package com.cmmobi.looklook.info.profile;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import android.text.TextUtils;

import com.cmmobi.looklook.common.gson.GsonResponse3.MicListItem;
import com.cmmobi.looklook.common.gson.GsonResponse3.VshareDiary;

/**
 * @author youtian
 * @email youtian@cmmobi.com
 * @date 2013-12-03
 */
public class VshareDataEntities {

	/**
	 * 存储该用户所有日记结构
	 * value-一条微享结构
	 */
	public ArrayList<MicListItem> vShareList=new ArrayList<MicListItem>();
	
	public void refreshCache(ArrayList<MicListItem> list) {
		vShareList.clear();
		vShareList.addAll(list);
	}
	
	public int getContainDiaryNum(String diaryuuids){
		int num=0;
		if(!TextUtils.isEmpty(diaryuuids)){
			String[] uuids=diaryuuids.split(",");
			if(uuids!=null&&uuids.length>0){
				for(String uuid:uuids){
					for(MicListItem micListItem:vShareList){
						if(micListItem.diarys!=null&&micListItem.diarys.length>0){
							for(VshareDiary myDiary:micListItem.diarys){
								if(myDiary.diaryuuid.equals(uuid)){
									num++;
									break;
								}
							}
						}
					}
				}
			}
		}
		return num;
	}
	
	/**
	 * 根据日记uuid删除微享中对应的日记，如果该日记为此微享最后一条，则删除该微享
	 * @param diaryuuid
	 */
	public synchronized void removeDiaryByUUID(String diaryuuid){
		if(!TextUtils.isEmpty(diaryuuid)){
			ArrayList<MicListItem> removedList=new ArrayList<MicListItem>();
			for(MicListItem micListItem:vShareList){
				if(micListItem.diarys!=null&&micListItem.diarys.length>0){
					for(VshareDiary myDiary:micListItem.diarys){
						if(myDiary.diaryuuid.equals(diaryuuid)){
							List<VshareDiary> list= Arrays.asList(micListItem.diarys);
							ArrayList<VshareDiary> aList=new ArrayList<VshareDiary>();
							aList.addAll(list);
							aList.remove(myDiary);
							if(0==aList.size()){//该微享中没有日记数据了，删除该条微享
								removedList.add(micListItem);
								break;
							}
							micListItem.diarys=list.toArray(new VshareDiary[aList.size()]);
							break;
						}
					}
				}
			}
			if(removedList.size()>0)
				vShareList.removeAll(removedList);
		}
	}
	
	
	/**
	 * 根据uuid更新publishid
	 */
	public void updatePublishidByuuid(String uuid,String publishid){
		for(MicListItem item:vShareList){
			if(item!=null&&!TextUtils.isEmpty(uuid)&&uuid.equals(item.uuid)){
				item.publishid=publishid;
			}
		}
	}

	public List<MicListItem> getCache() {
		return vShareList;
	}
	
	public void clearList() {
		vShareList.clear();
	}
	
	public void addMember(MicListItem item) {
		if(item != null){
		if (!isMember(item.publishid)) {
			vShareList.add(item);
		} else {
			removeMember(item.publishid);
			vShareList.add(item);
		}
		}
	}
	
	public void insertMember(int i, MicListItem item) {
		if(item != null){
			if (!isMember(item.publishid)) {
				vShareList.add(i, item);
			} else {
				removeMember(item.publishid);
				vShareList.add(i, item);
			}
		}
	}
	
	public void insertMemberUuid(int i, MicListItem item) {
		if(item != null){
			if (!isMemberUuid(item.uuid)) {
				vShareList.add(i, item);
			} else {
				removeMemberUuid(item.uuid);
				vShareList.add(i, item);
			}
		}
	}
	
	public boolean isMemberUuid(String uuid) {
		for (MicListItem item:vShareList) {
			if (uuid.equals(item.uuid)) {
				return true;
			}
		}
		return false;
	}
	
	public void removeMemberUuid(String uuid) {
		for (MicListItem item:vShareList) {
			if (uuid.equals(item.uuid)) {
				vShareList.remove(item);
				break;
			}
		}
	}
	
	public boolean isMember(String publishid) {
		for (MicListItem item:vShareList) {
			if (publishid.equals(item.publishid)) {
				return true;
			}
		}
		return false;
	}
	
	public MicListItem findMember(String publishid) {
		for (MicListItem item:vShareList) {
			if (publishid.equals(item.publishid)) {
				return item;
			}
		}
		return null;
	}
	
	public void removeMember(String publishid) {
		for (MicListItem item:vShareList) {
			if (publishid.equals(item.publishid)) {
				vShareList.remove(item);
				break;
			}
		}
	}
	
	public MicListItem findMemberuuid(String uuid) {
		for (MicListItem item:vShareList) {
			if (uuid.equals(item.uuid)) {
				return item;
			}
		}
		return null;
	}
	
	public void removeMemberuuid(String uuid) {
		for (MicListItem item:vShareList) {
			if (uuid.equals(item.uuid)) {
				vShareList.remove(item);
				break;
			}
		}
	}
	
	public void updateStutas(String uuid, String status){
		for (MicListItem item:vShareList) {
			if (uuid.equals(item.uuid)) {
				item.setUpload_status(status);
				break;
			}
		}
	}
	
	public ArrayList<MicListItem> removeDuplicateWithOrder() {  
		Set set = new HashSet();  
		ArrayList<MicListItem> newList = new ArrayList<MicListItem>();  
		Iterator iter = vShareList.iterator(); 
		while (iter.hasNext()) {  
		      Object element = iter.next();  
		       if (set.add(element))  
		           newList.add((MicListItem)element);  
		}  
		return newList;  
	}  

}
