package com.cmmobi.looklook.networktask;
/*
 * Author:Ray
 */

import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.net.wifi.WifiManager;
import android.os.Parcelable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import cn.zipper.framwork.core.ZApplication;
import cn.zipper.framwork.core.ZBroadcastReceiver;
import cn.zipper.framwork.core.ZLog;
import cn.zipper.framwork.io.network.ZNetworkStateDetector;

import com.cmmobi.looklook.activity.SettingActivity;
import com.cmmobi.looklook.common.gson.GsonResponse2.MyDiary;
import com.cmmobi.looklook.common.service.DiarySyncService;
//import com.cmmobi.looklook.common.service.PrivateMessageService;
import com.cmmobi.looklook.info.profile.AccountInfo;
import com.cmmobi.looklook.info.profile.ActiveAccount;
import com.cmmobi.looklook.info.profile.DiaryManager;
import com.cmmobi.looklook.info.profile.LoginSettingManager;
import com.cmmobi.looklook.info.profile.NetworkTaskInfo;
import com.cmmobi.looklook.offlinetask.OfflineTaskManager;


public class NetworkTaskManager implements NetworkTaskManagerListener {

	private static final String TAG = "NetworkTaskManager";
	private static NetworkTaskManager manager;
	
	private static String uid; //用户id，与账号绑定
	public static final String ACTION_UPDATE_NETWORKTASK = "action_update_networktask";
	public List<INetworkTask> tasks;
	public boolean isActive; //是否手动启动
//	private static AppFrontReceiver appFrontReceiver = null; //主程序是否在后台运行
	private NetConnectedReceiver myBroadcastReceiver = null;
	private SettingChangeReceiver settingChangeReceiver = null;
	private static NetworkTaskAlarmUtils amAlarm = null;

	private NetworkTaskManager() {
		init();
	}
	
	
	//获取单例
	public static NetworkTaskManager getInstance(String uID) {
		
		if(uID != null && !uID.equals("")) {
			if (manager == null || !uID.equals(uid)) {
				Log.e(TAG, "new NetworkTaskManager()");
				manager = new NetworkTaskManager();
			} else {
//				Log.e(TAG, "NetworkTaskManager getInstance" + uid);
			}
			uid = uID;
		}
		return manager;
	}
	
	private void init() {
		isActive = false;
		if(settingChangeReceiver == null) {
			settingChangeReceiver = new SettingChangeReceiver();
			IntentFilter settingfilter = new IntentFilter();
			settingfilter.addAction(SettingActivity.BROADCAST_PRIVACY_CHANGED);
			LocalBroadcastManager.getInstance(ZApplication.getInstance()).registerReceiver(settingChangeReceiver, settingfilter);
		}
		if (myBroadcastReceiver == null) {
			Log.d("==WR==", "NetworkTaskManager init NetConnectedReceiver");
			myBroadcastReceiver = new NetConnectedReceiver();
			IntentFilter filter = new IntentFilter();
			filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
			filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
			filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
			ZApplication.getInstance().getApplicationContext().registerReceiver(myBroadcastReceiver, filter);
		}
		if (amAlarm == null) {
			amAlarm = new NetworkTaskAlarmUtils();
		}
	}
	
	//获取当前任务列表
	//若task为空，读取固化信息
	public List<INetworkTask> getTaskList() {
		if(tasks == null) { //根据uid从AccountInfo中读取未完成的
			//TODO:
			List<INetworkTask> taskList = AccountInfo.getInstance(uid).readNetworkTaskInfo();
			if (taskList!=null) {
				for (INetworkTask task : taskList) {
					task.setTaskManagerListener(this);
				}
				tasks = taskList;
			}
		}
		if(tasks == null) {
			tasks = new ArrayList<INetworkTask>();
		}
		return tasks;
	}

	//获得当前任务数
	public int getTaskNum() {
		int num = 0;
		List<INetworkTask> tmptasks = getTaskList();
		num = tmptasks.size();
		return num;
	}
	
	//获得当前指定类型任务数
	public int getTaskNum(String type) {
		int num = 0;
		List<INetworkTask> tmptasks = getTaskList();
		for(Iterator<INetworkTask> it = tmptasks.iterator(); it.hasNext();) {
			if(it.next().info.taskType.equalsIgnoreCase(type)) {
				num ++;
			}
		}
		return num;
	}
	
	//判断当前是否已有任务
	public boolean hasTask() {
		return tasks!=null && tasks.size() > 0;
	}

	//判断是否有任务正在进行
	public boolean isTaskRunning() {
		boolean hasRunningTask = false;
		List<INetworkTask> list = getTaskList();
		for (Iterator<INetworkTask> it = list.iterator(); it.hasNext();) {
			INetworkTask tempTask = it.next();
			if (tempTask.getState() == INetworkTask.STATE_PREPARING
					|| tempTask.getState() == INetworkTask.STATE_RUNNING
					|| tempTask.getState() == INetworkTask.ACTION_RETRY) {
				hasRunningTask = true;
				break;
			}
		}
		return hasRunningTask;
	}
	
	//获取任务taskID
	public String getTaskID(INetworkTask t) {
		return t.getId();
	}
	
	//添加任务
	public void addTask_l(INetworkTask t, int i) {
		t.setTaskManagerListener(this);
		int index = getIndex(t);
		if (index > -1) {
			safeRemove(t);
			tasks.add(index, t);
		} else {
			tasks.add(i, t);
			Intent intent = new Intent(ACTION_UPDATE_NETWORKTASK);
			ZApplication.getInstance().sendLocalBroadcast(intent);
		}
	}
	
	//添加任务
	public void addTask_l(INetworkTask t) {
		t.setTaskManagerListener(this);
		int index = getIndex(t);
		if (index > -1) {
			safeRemove(t);
			//removeTask(t);
			tasks.add(index, t);
		} else {
			tasks.add(t);
			Intent intent = new Intent(ACTION_UPDATE_NETWORKTASK);
			ZApplication.getInstance().sendLocalBroadcast(intent);
//			index = getPriorityIndex() + 1;
		}
	}
	
	
	//修改任务
	//重复添加同一任务，将放入队首
	public void addTask(INetworkTask t) {
		if(t == null || t.info == null || t.info.diaryuuid == null) {
			Log.d(TAG, "invalid NetworkTask");
			return;
		}
		String uuid = t.info.diaryuuid;
		INetworkTask tmpTask = getTask(uuid);
		//并不在原任务中
		if(tmpTask == null) {
			addTask_l(t);
			return;
		//已存在相同uuid的原任务
		} else {
			//在原任务基础上修改部分信息，上传文件列表、任务总大小
			tmpTask.info.caMedias = t.info.caMedias;
			tmpTask.info.upMedias = t.info.upMedias;
			tmpTask.info.downMedias = t.info.downMedias;
			tmpTask.info.upRequest = t.info.upRequest;
			tmpTask.info.totalTaskSize = t.info.totalTaskSize;
			//如果是离线任务，直接替换后重新执行
			if(tmpTask.info.taskType.equals(INetworkTask.TASK_TYPE_CACHE) &&
					tmpTask.info.state != INetworkTask.STATE_COMPELETED) {
				OnTaskRemoved(tmpTask);
				addTask_l(tmpTask);
				tmpTask.waiting();
				startNextTask();
			}
			return;
			//TODO:能否直接生效，是否需要替换？
		}
	}
	
	public void priorityTask(INetworkTask t) {
		t.setTaskManagerListener(this);
		int index = getIndex(t);
		if (index > -1) {
			safeRemove(t);
		}
		tasks.add(0, t);
	}
	
	public void pauseTask(INetworkTask t) {
		t.pause();
		startNextTask();
	}
	
	public void setPriority(INetworkTask t, boolean b) {
		int index = getIndex(t);
		if(index > 0 && b) {
			List<INetworkTask> list = getTaskList();
			for (Iterator<INetworkTask> it = list.iterator(); it.hasNext();) {
				INetworkTask tempTask = it.next();
				if (tempTask.getState() == INetworkTask.STATE_PREPARING
						|| tempTask.getState() == INetworkTask.STATE_RUNNING
						|| tempTask.getState() == INetworkTask.STATE_PAUSED
						|| tempTask.getState() == INetworkTask.ACTION_RETRY) {
					tempTask.pause();
					tempTask.waiting();
					tempTask.info.isPriority = false;
				}
			}
			priorityTask(t);
			//tasks.add(0, t);
			if(t.getState() != INetworkTask.STATE_WAITING) {
				t.waiting();
			}
			startNextTask();
		} else if(index == 0 && b) {
			if(t.getState() == INetworkTask.STATE_PAUSED) {
				t.waiting();
				startNextTask();
			}
		}
		t.info.isPriority = b;
		if(b) {
			isActive = b;
		}
	}
	
	public boolean getPriority(INetworkTask t) {
		return t.info.isPriority == true;
	}
	
	//获取任务在队列中的位置  n:位置索引  -1:未找到
	private int getIndex(INetworkTask t) {
		int index = -1;
		List<INetworkTask> tasks = getTaskList();
		for (Iterator<INetworkTask> it = tasks.iterator(); it.hasNext();) {
			INetworkTask temp = it.next();
			if (temp.getId().equals(t.getId())) {
				index = tasks.indexOf(temp);
				break;
			}
		}
		return index;
	}
	
	//日记uuid获取队列任务
	public INetworkTask getTask(String uuid) {
		List<INetworkTask> tasks = getTaskList();
		for (Iterator<INetworkTask> it = tasks.iterator(); it.hasNext();) {
			INetworkTask tempTask = it.next();
			if(tempTask.info != null && tempTask.info.diaryuuid.equalsIgnoreCase(uuid)) {
				return tempTask;
			}
		}
		return null;
	}
	
	//移除任务
	public synchronized void removeTask(String uuid) {
		if(uuid != null) {
			INetworkTask t = getTask(uuid);
			if(t != null) {
				removeTask(t);
			}
		}
	}
	
	public synchronized void removeTask(INetworkTask t) {
		t.remove();
		safeRemove(t);
	}
	
	private void safeRemove(INetworkTask t) {
		try {
			for (Iterator<INetworkTask> it = tasks.iterator(); it.hasNext();) {
				if (t.taskId.equals(it.next().taskId)) {
					it.remove();
				}
			}
		} catch (ConcurrentModificationException e) {
			Log.e("==WR==", "failed to remove Networktask!");
		}
	}
	
	//移除任务
	public void removeAllTask() {
		for (Iterator<INetworkTask> it = tasks.iterator(); it.hasNext();) {
			it.remove();
		}
		tasks.clear();
	}
	
	//开始任务
	public void startTask(INetworkTask t) {
		if (t != null) {
			boolean find = false;
			List<INetworkTask> list = getTaskList();
			for (Iterator<INetworkTask> it = list.iterator(); it.hasNext();) {
				INetworkTask tempTask = it.next();
				// 如果有正在进行中的任务
				if (tempTask.getState() == INetworkTask.STATE_PREPARING
						|| tempTask.getState() == INetworkTask.STATE_RUNNING
						|| tempTask.getState() == INetworkTask.STATE_EDITORING
						|| tempTask.getState() == INetworkTask.ACTION_RETRY) {
					t.waiting();
					find = true;
					break;
				}
			}
			if (!find) {
				t.start();
			}
		}
	}
	
	//开始任务
	public void startTask(String uuid) {
		boolean find = false;
		//找到队列任务
		INetworkTask t = getTask(uuid);
		if(t== null) {
			return;
		}
		List<INetworkTask> list = getTaskList();
		for (Iterator<INetworkTask> it = list.iterator(); it.hasNext();) {
			INetworkTask tempTask = it.next();
			//如果有正在进行中的任务
			if (tempTask.getState() ==  INetworkTask.STATE_PREPARING
					|| tempTask.getState() ==  INetworkTask.STATE_RUNNING
					|| tempTask.getState() ==  INetworkTask.STATE_EDITORING
					|| tempTask.getState() ==  INetworkTask.ACTION_RETRY
					&& !tempTask.info.diaryuuid.equals(uuid)) {
				t.waiting();
				find = true;
				break;
			}
		}
		if(!find) {
			t.start();
		}
	}
	
	public void resetTaskManager(String userid) {
		if (userid != null && !userid.equals("")) {
			uid = null;
			manager = null;
			tasks.clear();
			Log.d(TAG, "resetTaskManager! userid " + userid);
			if (settingChangeReceiver != null) {
				LocalBroadcastManager.getInstance(ZApplication.getInstance())
						.unregisterReceiver(settingChangeReceiver);
			}
			if (myBroadcastReceiver != null) {
				LocalBroadcastManager.getInstance(ZApplication.getInstance())
						.unregisterReceiver(myBroadcastReceiver);
			}
		}
	}
	
	//暂停所有任务
	public void pauseAllTask() {
		List<INetworkTask> list = getTaskList();
		for (Iterator<INetworkTask> it = list.iterator(); it.hasNext();) {
			INetworkTask tempTask = it.next();
			if (tempTask.getState() ==  INetworkTask.STATE_PREPARING
					|| tempTask.getState() ==  INetworkTask.STATE_RUNNING
					|| tempTask.getState() ==  INetworkTask.STATE_EDITORING
					|| tempTask.getState() ==  INetworkTask.STATE_WAITING
					|| tempTask.getState() ==  INetworkTask.ACTION_RETRY) {
				tempTask.pause();
			}
		}
		isActive = false;
	}
	
	public boolean getActive() {
		return this.isActive;
	}
	
	public void startAllTask(boolean isActive) {
		this.isActive = isActive;
		startAllTask();
	}
	
	//开始所有任务
	public synchronized void startAllTask() {
		List<INetworkTask> list = getTaskList();
		for (Iterator<INetworkTask> it = list.iterator(); it.hasNext();) {
			INetworkTask tempTask = it.next();
			if (tempTask.getState() ==  INetworkTask.STATE_PAUSED
					|| tempTask.getState() ==  INetworkTask.STATE_ERROR) {
				tempTask.waiting();
			}
		}
		startNextTask();
	}
	
	//开始下一个任务
	public void startNextTask(){
		List<INetworkTask> list = getTaskList();
		for (Iterator<INetworkTask> it = list.iterator(); it.hasNext();) {
			INetworkTask tempTask = it.next();
			//如果有正在进行中的任务
			if (tempTask.getState() ==  INetworkTask.STATE_PREPARING
					|| tempTask.getState() ==  INetworkTask.STATE_RUNNING
					|| tempTask.getState() ==  INetworkTask.STATE_EDITORING
					|| tempTask.getState() ==  INetworkTask.ACTION_RETRY) {
				break;
			}
			//执行waiting中的任务
			if (tempTask.getState() ==  INetworkTask.STATE_WAITING) {
				startTask(tempTask);
				break;
			}
		}
	}
	
	//固化任务
	public void persistTask() {
		List<NetworkTaskInfo> infos = new ArrayList<NetworkTaskInfo>();
		if(tasks != null) {
			for(Iterator<INetworkTask> it = tasks.iterator(); it.hasNext();) {
				infos.add(it.next().info);
			}
		}
		AccountInfo.getInstance(uid).updateNetworkTaskInfo(infos);
		//AccountInfo.getInstance(uid).persist();
	}

	public void persistTaskToSD() {
		persistTask();
		new Thread() {
			@Override
			public void run() {
				AccountInfo.getInstance(uid).persist();
			}
		}.start();
	}
	
	private UploadNetworkTask convert2uploadtask(INetworkTask task) {
		NetworkTaskInfo tmpinfo = task.info;
		if(!tmpinfo.taskType.equals(INetworkTask.TASK_TYPE_CACHE)) {
			return null;
		}
		tmpinfo.upMedias = tmpinfo.caMedias;
		tmpinfo.caMedias = null;
		
		UploadNetworkTask uptask = new UploadNetworkTask(tmpinfo);
		return uptask;
	}
	
	private void updateActiveState(INetworkTask t) {
		if(t != null) {
			int index = getIndex(t);
			int size  = getTaskNum();
			//最后一个任务
			if(index == size - 1) {
				this.isActive = false;
			}
		}
	}
	
	//更新日记状态
	private void updateDiaryWrapper(NetworkTaskInfo info, int state) {
		//TODO 上传完成后通过心跳获取日记同步状态,下载后更新日记状态
		//sync_status : 0-未同步  1-日记结构已创建 2-上传中 3-上传完成  4-已同步 5-下载中 6-已下载
		DiaryManager diarymanager = DiaryManager.getInstance();
		MyDiary myLocalDiary = diarymanager.findLocalDiaryByUuid(info.diaryuuid);
		if(myLocalDiary != null) {
			//1.离线上传任务
			if(INetworkTask.TASK_TYPE_CACHE.equalsIgnoreCase(info.taskType)) {
				switch (state) {
				case INetworkTask.STATE_COMPELETED:
					myLocalDiary.sync_status = 1;
					//固化状态
//					persistTaskToSD();
					break;
				default:
					break;
				}
				
			//2.上传任务
			} else if(INetworkTask.TASK_TYPE_UPLOAD.equalsIgnoreCase(info.taskType)) {
				switch (state) {
				case INetworkTask.STATE_COMPELETED:
					myLocalDiary.sync_status = 3;
					//心跳获取是否同步
					Intent intent = new Intent(ZApplication.getInstance(), DiarySyncService.class);
					intent.putExtra("diaryid", info.diaryid);
					ZApplication.getInstance().startService(intent);
					//固化状态
//					persistTaskToSD();
					break;
				case INetworkTask.STATE_REMOVED:
					myLocalDiary.sync_status = 1;
					break;
				case INetworkTask.ACTION_RETRY:
				case INetworkTask.STATE_EDITORING:
				case INetworkTask.STATE_ERROR:
				case INetworkTask.STATE_PAUSED:
				case INetworkTask.STATE_PREPARING:
				case INetworkTask.STATE_RUNNING:
				case INetworkTask.STATE_WAITING:
					myLocalDiary.sync_status = 2;
					break;
				default:
					break;
				}
				
			//3.下载任务
			} else if(INetworkTask.TASK_TYPE_DOWNLOAD.equalsIgnoreCase(info.taskType)) {
				switch (state) {
				case INetworkTask.STATE_COMPELETED:
					myLocalDiary.sync_status = 6;
					//固化状态
//					persistTaskToSD();
					break;
				case INetworkTask.STATE_REMOVED:
					myLocalDiary.sync_status = 4;
					break;
				default:
					myLocalDiary.sync_status = 5;
					break;
				}
			}
		}
	}
	
	//===================以下方法都是在线程中运行,不能直接进行UI操作============================
	@Override
	public void OnTaskStateChange(INetworkTask task, int state) {
		if(task.info != null && task.info.diaryuuid != null) {
			updateDiaryWrapper(task.info, state);
		}
		if (state == INetworkTask.STATE_COMPELETED) {
			if(task.info.taskType.equals(INetworkTask.TASK_TYPE_CACHE)) {
				// 需要转变成正常的uploadtask
				UploadNetworkTask tmptask = convert2uploadtask(task);
				int index = getIndex(task);
				OnTaskRemoved(task);// 完成的任务直接移除任务队列
				addTask_l(tmptask, index);
				tmptask.waiting();
			} else {
				// TODO:登记到MediaMapping
				// write2mapping(task);
				OnTaskRemoved(task);// 完成的任务直接移除任务队列
			}
			updateActiveState(task);
			startNextTask();
		} else if(state == INetworkTask.STATE_ERROR) {
			//OnTaskRemoved(task);
			//Toast.makeText(null, "task error! file deleted", Toast.LENGTH_LONG).show();
			//task.remove();//移除任务，删除文件
			task.pause();
			updateActiveState(task);
			startNextTask();
		} else if(state == INetworkTask.STATE_REMOVED) {
			updateActiveState(task);
			startNextTask();
		}
		
		if (state != INetworkTask.ACTION_RETRY) {
			persistTask();	
		}

//		Intent intent = new Intent(INetworkTask.ACTION_NOTIFY);
//		ZApplication.getInstance().sendLocalBroadcast(intent);
		
		Intent intent2 = new Intent(ACTION_UPDATE_NETWORKTASK);
		ZApplication.getInstance().sendLocalBroadcast(intent2);
	}

	@Override
	public void notifyPrecentChange(INetworkTask task, long t, long c) {
		Intent intent = new Intent(INetworkTask.ACTION_TASK_PERCENT_NOTIFY);
		ZApplication.getInstance().sendLocalBroadcast(intent);
	}

	@Override
	public void OnTaskRemoved(INetworkTask task) {
		Log.d("debug", "OnTaskRemoved " + task.info.state);
		clearDataIfNecessary(task);//如果需要，清除本地文件
		int index = getIndex(task);
		safeRemove(task);//移除任务队列
		Intent intent = new Intent(INetworkTask.ACTION_TASK_REMOVE_NOTIFY);
		ZLog.e("need to remove "+ index);
		if (index > -1) {
			intent.putExtra("taskid", index);
			ZApplication.getInstance().sendLocalBroadcast(intent);
		}
	}

	private void clearDataIfNecessary(INetworkTask task) {
		if (task != null && task.info != null
				&& task.info.state == INetworkTask.STATE_REMOVED
				&& task.info.taskType.equals(INetworkTask.TASK_TYPE_DOWNLOAD)) {
			//clear
//			Log.d("debug", "clear data when state = " + task.info.state);
			task.info.clearDownloadData();
		}
	}
	
	public boolean getCurPublish(INetworkTask task) {
		boolean isPublish = false;
		if(task != null && task.info != null && task.info != null && !INetworkTask.TASK_TYPE_DOWNLOAD.equals(task.info.taskType)) {
			//TODO:
			isPublish = OfflineTaskManager.getInstance().hasShareToLooklookTask(task.info.diaryuuid);
			task.info.isPublish = isPublish;
		}
		return isPublish;
	}
	
	public class NetConnectedReceiver extends ZBroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			// 这个监听wifi的连接状态即是否连上了一个有效无线路由，
			// 当上边广播的状态是WifiManager.WIFI_STATE_DISABLING，和WIFI_STATE_DISABLED的时候，根本不会接到这个广播。
			// 在上边广播接到广播是WifiManager.WIFI_STATE_ENABLED状态的同时也会接到这个广播，
			// 当然刚打开wifi肯定还没有连接到有效的无线
			Log.d(TAG, "NetConnectedReceiver intent" + intent.getAction());
			if (WifiManager.NETWORK_STATE_CHANGED_ACTION.equals(intent.getAction())) {
				Parcelable parcelableExtra = intent
						.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
				if (null != parcelableExtra) {
					NetworkInfo networkInfo = (NetworkInfo) parcelableExtra;
					State state = networkInfo.getState();
					boolean isConnected = state == State.CONNECTED;// 当然，这边可以更精确的确定状态
					if (isConnected) {
						Log.d(TAG, "WIFI connected, Start Task Queue!");
						startAllTask();
					}
				}
			}
		}
	}
	
	public class SettingChangeReceiver extends ZBroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			if(SettingActivity.BROADCAST_PRIVACY_CHANGED.equals(intent.getAction())) {
				LoginSettingManager lsm = AccountInfo.getInstance(ActiveAccount
						.getInstance(context).getLookLookID()).setmanager;
				if(lsm != null) {
					String sync = lsm.getSysc_type();
					// Settings wifi/any
					if (sync != null && !sync.equals("")) {
						// wifi网络
						if (sync.equals(SettingActivity.SYSC_TYPE_WIFI)) {
							Log.d("==WR==", "Setting WIFI!");
							//当前无网或非WIFI
							if(!ZNetworkStateDetector.isConnected() ||
									!ZNetworkStateDetector.isWifi()) {
								pauseAllTask();
							} else {
								startAllTask();
							}
						// 所有网络
						} else if (sync.equals(SettingActivity.SYSC_TYPE_ANY)) {
							Log.d("==WR==", "Setting ANY!");
							startAllTask();
						} else {
							Log.d("==WR==", "Setting value + [" + sync + "]");
						}
					} else {
						Log.d("==WR==", "Unexpected Setting, sync is null or empty!");
					}
				}
			}
		}
	}
	
}
