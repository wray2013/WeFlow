package com.cmmobi.looklook.receiver;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import cn.zipper.framwork.core.ZApplication;
import cn.zipper.framwork.core.ZBroadcastReceiver;

import com.cmmobi.looklook.R;
import com.cmmobi.looklook.info.profile.ActiveAccount;
import com.cmmobi.looklook.networktask.INetworkTask;
import com.cmmobi.looklook.networktask.NetworkTaskManager;
import com.cmmobi.looklook.prompt.Prompt;

public class NetworkTaskAlarmReceiver extends ZBroadcastReceiver{

	public static final String ACTION_NETWORKTASK_ALARM = "ACTION_NETWORKTASK_ALARM";
	
	@Override
	public void onReceive(Context context, Intent intent) {
		String userID = ActiveAccount.getInstance(ZApplication.getInstance()).getUID();
		if (userID != null && !"".equals(userID)) {
			// TODO Auto-generated method stub
			int taskNum = NetworkTaskManager.getInstance(userID).getTaskNum(
					INetworkTask.TASK_TYPE_UPLOAD)
					+ NetworkTaskManager.getInstance(userID).getTaskNum(INetworkTask.TASK_TYPE_CACHE);
			Log.d("NetworkTaskAlarmReceiver",
					"NetworkTaskAlarmReceiver taskNum = " + taskNum);
			if (taskNum > 0) {
				// 弹框提示用户是否回到looklook继续任务
				Prompt.Alert(context.getString(R.string.prompt_network_alarm));
				NetworkTaskManager.getInstance(userID).pauseAllTask();
			}
		}
	}

}
