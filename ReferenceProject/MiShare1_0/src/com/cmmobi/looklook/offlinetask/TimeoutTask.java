package com.cmmobi.looklook.offlinetask;

import java.util.List;

import android.util.Log;
import cn.zipper.framwork.core.ZApplication;

import com.cmmobi.looklook.info.profile.AccountInfo;
import com.cmmobi.looklook.info.profile.ActiveAccount;
import com.cmmobi.looklook.info.profile.TimeHelper;
import com.cmmobi.looklook.offlinetask.IOfflineTask.TaskListener;

/**
 * @author wuxiang
 * @email wuxiang@cmmobi.com
 * @date 2013-8-27
 */
public class TimeoutTask extends Thread {

	private static final String TAG = "TimeoutTask";
	private TaskListener taskListener;

	public TimeoutTask(TaskListener taskListener) {
		this.taskListener = taskListener;
	}
	
	private boolean isRun=true;
	public void cancel(){
		isRun=false;
	}

	@Override
	public void run() {
		try {
			Log.d(TAG, "TimeoutTask run");
			while (isRun) {
				sleep(5000);
				if(taskListener!=null)taskListener.hasTask();
				String userid = ActiveAccount.getInstance(
						ZApplication.getInstance()).getUID();
				if(userid!=null){
					AccountInfo accountInfo = AccountInfo.getInstance(userid);
					List<TaskData> taskList = accountInfo.taskDataList;
					for (int i = 0; i < taskList.size(); i++) {
						// 找出超时的私信任务
						TaskData data = taskList.get(i);
						if ((TaskType.SEND_PRIVATE_MSG == data.taskType || TaskType.PRIVATE_MSG_AUDIO_UPLOAD == data.taskType)
								&& data.createTime != 0
								&& TimeHelper.getInstance().now() - data.createTime > 60000) {
							taskListener.timeout(data);
						}
					}
				}
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		super.run();
	}
}
