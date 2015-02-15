package com.cmmobi.looklook.offlinetask;

import java.util.UUID;

import com.baidu.platform.comapi.map.t;
import com.cmmobi.looklook.common.gson.GsonResponse2.MyDiary;


/**
 * @author wuxiang
 * @email wuxiang@cmmobi.com
 * @date  2013-8-20
 */
public class TaskData {
	public String id;
	public String sourceID;//前置任务ID
	public Object request;
	public String diaryID;
	public String diaryuuid;
	public String diaryUserID;
	public int retryCount;
	public TaskType taskType;
	public long createTime;
	
	public TaskData(){
		id=UUID.randomUUID().toString();
	}
	
	@Override
	public boolean equals(Object object) {
		if (!(object instanceof TaskData)) {
			return false;
		}
		TaskData taskData = (TaskData) object;
		if (taskType!=null&&taskType.equals(taskData.taskType) && diaryuuid!=null&&diaryuuid.equals(taskData.diaryuuid)) {
			return true;
		}
		return false;
	}
	public String toString(){
		return "TaskData:"+"\n"+
				"id="+id+"\n"+
				"sourceID="+sourceID+"\n"+
				"taskType="+taskType+"\n"+
				"createTime="+createTime+"\n"+
				"retryCount="+retryCount+"\n"+
				"diaryuuid="+diaryuuid+"\n"+
				"request="+request+"\n";
	}
}
