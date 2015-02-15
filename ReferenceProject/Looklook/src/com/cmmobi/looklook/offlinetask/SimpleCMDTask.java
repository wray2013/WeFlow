package com.cmmobi.looklook.offlinetask;

import android.util.Log;

import com.cmmobi.looklook.common.gson.GsonResponse2;
import com.cmmobi.looklook.common.gson.Requester2.Worker;

/**
 * @author wuxiang
 * @email wuxiang@cmmobi.com
 * @date  2013-8-19
 */
public class SimpleCMDTask extends IOfflineTask {

	private static final String TAG="SimpleCMDTask";
	@Override
	public void start() {
		Log.d(TAG, "SimpleCMDTask start");
		isRunning=true;
		Worker worker = new Worker(handler, getResponseTag(taskType),
				getResponseClass(taskType));
		worker.execute(getRIATag(taskType), request);
	}
}
