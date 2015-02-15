package com.cmmobi.looklook.networktask;

/*
 * Author:Ray
 */

import java.util.ArrayList;
import java.util.Collections;
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
import cn.zipper.framwork.utils.ZStringUtils;

import com.cmmobi.looklook.MainApplication;
import com.cmmobi.looklook.common.gson.GsonResponse3.MicListItem;
import com.cmmobi.looklook.common.gson.GsonResponse3.MyDiary;
import com.cmmobi.looklook.fragment.SettingFragment;
import com.cmmobi.looklook.info.profile.AccountInfo;
import com.cmmobi.looklook.info.profile.ActiveAccount;
import com.cmmobi.looklook.info.profile.DiaryManager;
import com.cmmobi.looklook.info.profile.LoginSettingManager;
import com.cmmobi.looklook.info.profile.NetworkTaskInfo;
import com.cmmobi.looklook.info.profile.VshareDataEntities;
import com.cmmobi.looklook.misharetask.MiShareTask;
import com.cmmobi.looklook.misharetask.MiShareTaskManager;
import com.cmmobi.looklook.offlinetask.OfflineTaskManager;

public class NetworkTaskManager implements NetworkTaskManagerListener {

	private static final String TAG = "NetworkTaskManager";
	private static NetworkTaskManager manager;

	private static String uid; // 用户id，与账号绑定
	public static final String ACTION_UPDATE_NETWORKTASK = "action_update_networktask";
	public static final String ACTION_RESTART_NETWORKTASK = "action_restart_networktask";
	private List<INetworkTask> tasks;
	// public boolean isActive; //是否手动启动
	// private static AppFrontReceiver appFrontReceiver = null; //主程序是否在后台运行
	private NetConnectedReceiver myBroadcastReceiver = null;
	private SettingChangeReceiver settingChangeReceiver = null;

	// private NetworkRestartReceiver networkRestartReceiver = null;
	// private static NetworkTaskAlarmUtils amAlarm = null;

	private NetworkTaskManager() {
		init();
	}

	// 获取单例
	public static NetworkTaskManager getInstance(String uID) {
		Log.v(TAG, "NetworkTaskManager getInstance - uID:" + uID);
		if (uID != null && !uID.equals("")) {
			if (manager == null || !uID.equals(uid)) {
				Log.e(TAG, "new NetworkTaskManager()");
				manager = new NetworkTaskManager();
			}
			uid = uID;
		}
		return manager;
	}

	private void init() {
		Log.v(TAG, "NetworkTaskManager init");
		// isActive = false;
		if (settingChangeReceiver == null) {
			settingChangeReceiver = new SettingChangeReceiver();
			IntentFilter settingfilter = new IntentFilter();
			settingfilter.addAction(SettingFragment.BROADCAST_PRIVACY_CHANGED);
			LocalBroadcastManager.getInstance(MainApplication.getAppInstance())
					.registerReceiver(settingChangeReceiver, settingfilter);
		}
		if (myBroadcastReceiver == null) {
			Log.d("==WR==", "NetworkTaskManager init NetConnectedReceiver");
			myBroadcastReceiver = new NetConnectedReceiver();
			IntentFilter filter = new IntentFilter();
			filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
			filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
			filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
			filter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
			ZApplication.getInstance().registerReceiver(myBroadcastReceiver,
					filter);
		}
		/*
		 * if(networkRestartReceiver == null) { networkRestartReceiver = new
		 * NetworkRestartReceiver(); IntentFilter restartfilter = new
		 * IntentFilter(); restartfilter.addAction(ACTION_RESTART_NETWORKTASK);
		 * LocalBroadcastManager
		 * .getInstance(MainApplication.getAppInstance()).registerReceiver
		 * (networkRestartReceiver, restartfilter); } if (amAlarm == null) {
		 * amAlarm = new NetworkTaskAlarmUtils(); }
		 */
	}

	/**
	 * 当登录时判断当前的网络状态
	 */
	public void updateLogin() {
		LoginSettingManager lsm = AccountInfo.getInstance(ActiveAccount
				.getInstance(MainApplication.getAppInstance()).getLookLookID()).setmanager;
		if (lsm != null) {
			String sync = lsm.getSync_type();
			// Settings wifi/any
			if (sync != null && !sync.equals("")) {
				// wifi网络
				if (sync.equals(SettingFragment.SYNC_TYPE_WIFI)) {
					Log.d("==WR==", "updateLogin Setting WIFI!");
					// 当前无网或非WIFI
					if (!ZNetworkStateDetector.isConnected()
							|| !ZNetworkStateDetector.isWifi()) {
						pauseAllTask();
					} else {
						startAllTask();
					}
					// 所有网络
				} else if (sync.equals(SettingFragment.SYNC_TYPE_ANY)) {
					Log.d("==WR==", "updateLogin Setting ANY!");
					startAllTask();
				} else {
					Log.d("==WR==", "updateLogin Setting value + [" + sync
							+ "]");
				}
			} else {
				Log.d("==WR==", "Unexpected Setting, sync is null or empty!");
			}
		}
	}

	// 获取当前任务列表
	// 若task为空，读取固化信息
	public List<INetworkTask> getTaskList() {
		if (tasks == null) { // 根据uid从AccountInfo中读取未完成的
			// TODO:
			List<INetworkTask> taskList = AccountInfo.getInstance(uid)
					.readNetworkTaskInfo();
			if (taskList != null) {
				synchronized (taskList) {
					for (Iterator<INetworkTask> it = taskList.iterator(); it
							.hasNext();) {
						it.next().setTaskManagerListener(this);
					}
				}
				tasks = Collections.synchronizedList(taskList);
			}
		}
		if (tasks == null) {
			tasks = Collections.synchronizedList(new ArrayList<INetworkTask>());
		}
		return tasks;
	}

	// 获得当前存活任务数
	public int getActiveTaskNum() {
		int num = 0;
		List<INetworkTask> tmptasks = getTaskList();
		synchronized (tmptasks) {
			for (Iterator<INetworkTask> it = tmptasks.iterator(); it.hasNext();) {
				if (it.next().getState() == INetworkTask.STATE_REMOVED) {
					it.remove();
				}
			}
		}
		num = tmptasks.size();
		return num;
	}

	// 获得当前指定类型任务数
	public int getTaskNum(String type) {
		int num = 0;
		List<INetworkTask> tmptasks = getTaskList();
		synchronized (tmptasks) {
			for (Iterator<INetworkTask> it = tmptasks.iterator(); it.hasNext();) {
				if (it.next().info.taskType.equalsIgnoreCase(type)) {
					num++;
				}
			}
		}
		return num;
	}

	// 判断当前是否已有任务
	public boolean hasTask() {
		return tasks != null && tasks.size() > 0;
	}

	// 判断是否有任务正在进行
	public boolean isTaskRunning() {
		boolean hasRunningTask = false;
		List<INetworkTask> list = getTaskList();
		synchronized (list) {
			for (Iterator<INetworkTask> it = list.iterator(); it.hasNext();) {
				INetworkTask tempTask = it.next();
				if (tempTask.getState() == INetworkTask.STATE_PREPARING
						|| tempTask.getState() == INetworkTask.STATE_RUNNING
						|| tempTask.getState() == INetworkTask.ACTION_RETRY) {
					hasRunningTask = true;
					break;
				}
			}
		}
		return hasRunningTask;
	}

	// 获取任务taskID
	public String getTaskID(INetworkTask t) {
		return t.getId();
	}

	/**
	 * 添加任务到指定列表位置，已废弃
	 * @param t
	 * @param i
	 */
	public void addTask_l(INetworkTask t, int i) {
		t.setTaskManagerListener(this);
		int index = getIndex(t);
		if (index > -1) {
			removeTaskFromList(t);
			synchronized (tasks) {
				tasks.add(index, t);
			}
		} else {
			synchronized (tasks) {
				tasks.add(i, t);
			}
			persistTask();
			Intent intent = new Intent(ACTION_UPDATE_NETWORKTASK);
			ZApplication.getInstance().sendLocalBroadcast(intent);
		}
	}

	// 添加任务
	public void addTask_l(INetworkTask t) {
		t.setTaskManagerListener(this);
		int index = getIndex(t);
		if (index > -1) {
			removeTaskFromList(t);
			synchronized (tasks) {
				tasks.add(index, t);
			}
		} else {
			synchronized (tasks) {
				tasks.add(t);
			}
			persistTask();
			Intent intent = new Intent(ACTION_UPDATE_NETWORKTASK);
			ZApplication.getInstance().sendLocalBroadcast(intent);
			// index = getPriorityIndex() + 1;
		}
	}

	/**
	 * 添加/修改任务
	 * 重复添加同一任务将任务媒体文件覆盖
	 * @param t
	 */
	public void addTask(INetworkTask t) {
		if (t == null || t.info == null || t.getdiaryUUID() == null) {
			Log.d(TAG, "invalid NetworkTask");
			return;
		}
		String uuid = t.getdiaryUUID();
		INetworkTask tmpTask = findTaskByUUID(uuid);
		// 并不在原任务中
		if (tmpTask == null) {
			addTask_l(t);
			return;
			// 已存在相同uuid的原任务
		} else {
			// 在原任务基础上修改部分信息，上传文件列表、任务总大小
			tmpTask.info.caMedias = t.info.caMedias;
			tmpTask.info.upMedias = t.info.upMedias;
			tmpTask.info.downMedias = t.info.downMedias;
			tmpTask.info.upRequest = t.info.upRequest;
			tmpTask.info.totalTaskSize = t.info.totalTaskSize;
			// 如果是离线任务，直接替换后重新执行
			if (tmpTask.info.taskType.equals(INetworkTask.TASK_TYPE_CACHE)
					&& tmpTask.info.state != INetworkTask.STATE_COMPELETED) {
				OnTaskRemoved(tmpTask);
				addTask_l(tmpTask);
				tmpTask.waiting();
				startNextTask();
			}
			return;
		}
	}

	/**
	 * 任务优先放置队首
	 * @param t
	 */
	public void priorityTask(INetworkTask t) {
		t.setTaskManagerListener(this);
		int index = getIndex(t);
		if (index > -1) {
			removeTaskFromList(t);
		}
		synchronized (tasks) {
			tasks.add(0, t);
		}
	}

	public void pauseTask(INetworkTask t) {
		// setActiveUp(t, false);
		// 重置任务网络状态的判断条件
		MiShareSettings(t, 1);
		t.pause();
		startNextTask();
	}

	/**
	 * 置顶任务，同时将所有任务置为就绪，已废弃
	 * 
	 * @param t
	 *            任务对象
	 * @param b
	 *            任务UI上是否显示“优”
	 */
	public void setPriority2(INetworkTask t, boolean b) {
		int index = getIndex(t);
		if (index > 0 && b) {
			List<INetworkTask> list = getTaskList();
			synchronized (list) {
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
			}
			priorityTask(t);
			// tasks.add(0, t);
			if (t.getState() != INetworkTask.STATE_WAITING) {
				t.waiting();
			}
			startNextTask();
		} else if (index == 0 && b) {
			if (t.getState() == INetworkTask.STATE_PAUSED) {
				t.waiting();
				startNextTask();
			}
		}
		t.info.isPriority = b;
		if (b) {
			// isActive = b;
		}
	}

	/**
	 * 第三方分享日记。突破网络限制
	 * @param t
	 */
	public void DoShare(INetworkTask t) {
		if (t != null && t.info != null) {
			t.info.isFromShare = true;
			setPriority(t, true);
		}
	}

	/**
	 * 设置手动启动任务（忽略网络设置）
	 * 
	 * @param t
	 * @param isActive
	 */
	public void setActiveUp(INetworkTask t, boolean isActive) {
		if (t != null && t.info != null) {
			t.info.isActive = isActive;
		}
	}

	/**
	 * 微享启动任务的设置，可忽略网络设置
	 * 
	 * @param t
	 */
	public void MiShareSettings(INetworkTask t, int settings) {
		if (t != null && t.info != null) {
			t.info.miShareSettings = settings;
		}
	}

	/**
	 * 置顶任务，仅开始当前任务，其他任务状态不变
	 * 
	 * @param t
	 *            任务对象
	 * @param b
	 *            任务UI上是否显示“优”
	 */
	public void setPriority(INetworkTask t, boolean b) {
		int index = getIndex(t);
		if (index < 0) {
			Log.e("==WR==", "the task is not found in current task list.");
			return;
		}
		if (index > 0 && b) {
			List<INetworkTask> list = getTaskList();
			synchronized (list) {
				for (Iterator<INetworkTask> it = list.iterator(); it.hasNext();) {
					INetworkTask tempTask = it.next();
					tempTask.info.isPriority = false;
					if (tempTask.getState() == INetworkTask.STATE_PREPARING
							|| tempTask.getState() == INetworkTask.STATE_RUNNING
							|| tempTask.getState() == INetworkTask.ACTION_RETRY) {
						tempTask.pause();
						tempTask.waiting();
						break;
					}
				}
			}
			// 移至队首
			priorityTask(t);
		}
		t.info.isPriority = b;
		if (t.getState() == INetworkTask.STATE_RUNNING) {
			return;
		} else if (t.getState() != INetworkTask.STATE_WAITING) {
			t.waiting();
		}
		startNextTask();
		if (b) {
			// isActive = b;
		}
	}

	/**
	 * 获取任务在队列中的位置 n:位置索引 -1:未找到
	 * @param t
	 * @return
	 */
	private synchronized int getIndex(INetworkTask t) {
		int index = -1;
		List<INetworkTask> tasks = getTaskList();
		synchronized (tasks) {
			for (Iterator<INetworkTask> it = tasks.iterator(); it.hasNext();) {
				INetworkTask temp = it.next();
				if (temp.getId().equals(t.getId())) {
					index = tasks.indexOf(temp);
					break;
				}
			}
		}
		return index;
	}

	/**
	 * 日记uuid获取队列任务
	 * @param uuid
	 * @return
	 */
	public INetworkTask findTaskByUUID(String uuid) {
		List<INetworkTask> tasks = getTaskList();
		synchronized (tasks) {
			for (Iterator<INetworkTask> it = tasks.iterator(); it.hasNext();) {
				INetworkTask tempTask = it.next();
				if (tempTask.info != null
						&& tempTask.info.diaryuuid.equalsIgnoreCase(uuid)) {
					return tempTask;
				}
			}
		}
		return null;
	}

	/**
	 * 获取上传任务剩余大小（字节）
	 * @param t
	 * @return
	 */
	public long getUploadRemainSize(INetworkTask t) {
		if (t != null && t.info != null
				&& t.info.taskType.equals(INetworkTask.TASK_TYPE_UPLOAD)) {
			long total = t.info.totalTaskSize;
			long uploaded = t.info.uploadedLength;
			return total - uploaded;// xxMb
		}
		return 0;
	}

	/**
	 * 从空间移除任务
	 */
	public synchronized void removeTask(String uuid) {
		if (uuid != null) {
			INetworkTask t = findTaskByUUID(uuid);
			if (t != null) {
				removeTask(t);
			}
			
			MiShareTask mitask = MiShareTaskManager.getInstance(uid).getMiShareTaskByDiary(uuid);
			if (mitask != null) {
				mitask.removeDiaryFromMiShareTask(uuid);
			}
			
			// 从微享列表中删除
			VshareDataEntities misharedata = AccountInfo.getInstance(uid).vshareLocalDataEntities;
			if (misharedata != null) {
				misharedata.removeDiaryByUUID(uuid);
				Log.d(TAG, "从微享列表中删除该日记");
				Intent intent = new Intent(MicListItem.INTENT_STATUS_CHANGE);
				intent.putExtra(MicListItem.BUNDLE_SHARE_UUID, "OnlyForRefresh");
				intent.putExtra(MicListItem.BUNDLE_SHARE_STATUS, "5");
				LocalBroadcastManager
						.getInstance(MainApplication.getInstance())
						.sendBroadcast(intent);
				Log.d(TAG, "向微享列表发广播");
			}
		}
	}

	/**
	 * 移除网络任务（状态机）
	 * 
	 * @param t
	 */
	public synchronized void removeTask(INetworkTask t) {
		if (t != null) {
			t.remove();
		}
	}

	/**
	 * 从任务列表中移除
	 * 
	 * @param t
	 */
	private synchronized void removeTaskFromList(INetworkTask t) {
		if (tasks != null) {
			synchronized (tasks) {
				for (Iterator<INetworkTask> it = tasks.iterator(); it.hasNext();) {
					if (t.taskId.equals(it.next().taskId)) {
						it.remove();
					}
				}
			}
		}
	}

	// 移除任务
	public synchronized void removeAllTask() {
		/*
		 * synchronized (tasks) { tasks.clear(); }
		 */
		DiaryManager diarymanager = DiaryManager.getInstance();
		if (tasks != null) {
			synchronized (tasks) {
				for (Iterator<INetworkTask> it = tasks.iterator(); it.hasNext();) {
					INetworkTask tempTask = it.next();
					if (tempTask != null && tempTask.info != null) {
						diarymanager
								.removeDiaryByUUIDFromNetworkTask(tempTask.info.diaryuuid);
					}
				}
				tasks.clear();
			}
		}
		persistTask();
		diarymanager.notifyMyDiaryChanged();
		Intent intent = new Intent(ACTION_UPDATE_NETWORKTASK);
		ZApplication.getInstance().sendLocalBroadcast(intent);
	}

	// 开始任务
	public void startTask(INetworkTask t) {
		if (t != null) {
			boolean find = false;
			List<INetworkTask> list = getTaskList();
			synchronized (list) {
				for (Iterator<INetworkTask> it = list.iterator(); it.hasNext();) {
					INetworkTask tempTask = it.next();
					// 如果有正在进行中的任务
					if (tempTask.getState() == INetworkTask.STATE_PREPARING
							|| tempTask.getState() == INetworkTask.STATE_RUNNING
							|| tempTask.getState() == INetworkTask.STATE_EDITING
							|| tempTask.getState() == INetworkTask.ACTION_RETRY) {
						t.waiting();
						find = true;
						break;
					}
				}
			}
			if (!find) {
				t.start();
			}
		}
	}

	// 开始任务
	public void startTask(String uuid) {
		boolean find = false;
		// 找到队列任务
		INetworkTask t = findTaskByUUID(uuid);
		if (t == null) {
			return;
		}
		List<INetworkTask> list = getTaskList();
		synchronized (list) {
			for (Iterator<INetworkTask> it = list.iterator(); it.hasNext();) {
				INetworkTask tempTask = it.next();
				// 如果有正在进行中的任务
				if (tempTask.getState() == INetworkTask.STATE_PREPARING
						|| tempTask.getState() == INetworkTask.STATE_RUNNING
						|| tempTask.getState() == INetworkTask.STATE_EDITING
						|| tempTask.getState() == INetworkTask.ACTION_RETRY
						&& !tempTask.info.diaryuuid.equals(uuid)) {
					t.waiting();
					find = true;
					break;
				}
			}
		}
		if (!find) {
			t.start();
		}
	}

	public void clearTasksIfNecessary() {
		if (uid != null && !uid.equals("")) {
			synchronized (tasks) {
				tasks.clear();
				tasks = null;
			}
		}
	}

	public void resetTaskManager(String userid) {
		if (userid != null && !userid.equals("")) {
			uid = null;
			manager = null;
			synchronized (tasks) {
				tasks.clear();
			}
			persistTask();
			Log.d(TAG, "resetTaskManager! userid " + userid);
			unregisterReceiver();
		}
	}

	// 暂停所有任务
	public void pauseAllTask() {
		List<INetworkTask> list = getTaskList();
		synchronized (list) {
			for (Iterator<INetworkTask> it = list.iterator(); it.hasNext();) {
				INetworkTask tempTask = it.next();
				if (tempTask.getState() == INetworkTask.STATE_PREPARING
						|| tempTask.getState() == INetworkTask.STATE_RUNNING
						|| tempTask.getState() == INetworkTask.STATE_EDITING
						|| tempTask.getState() == INetworkTask.STATE_WAITING
						|| tempTask.getState() == INetworkTask.ACTION_RETRY) {
					tempTask.pause();
				}
			}
		}
		// isActive = false;
	}

	public void shutdown() {
		unregisterReceiver();
		pauseAllTask();
		persistTask();
		manager = null;
	}

	private void unregisterReceiver() {
		if (settingChangeReceiver != null) {
			LocalBroadcastManager.getInstance(ZApplication.getInstance())
					.unregisterReceiver(settingChangeReceiver);
		}
		if (myBroadcastReceiver != null) {
			LocalBroadcastManager.getInstance(ZApplication.getInstance())
					.unregisterReceiver(myBroadcastReceiver);
		}
		/*
		 * if(networkRestartReceiver != null) {
		 * LocalBroadcastManager.getInstance(ZApplication.getInstance())
		 * .unregisterReceiver(networkRestartReceiver); }
		 */
	}

	// public boolean getActive() {
	// return this.isActive;
	// }

	public void startAllTask(boolean isActive) {
		// this.isActive = isActive;
		startAllTask();
	}

	// 开始所有任务
	public synchronized void startAllTask() {
		List<INetworkTask> list = getTaskList();
		synchronized (list) {
			for (Iterator<INetworkTask> it = list.iterator(); it.hasNext();) {
				INetworkTask tempTask = it.next();
				if (tempTask.getState() == INetworkTask.STATE_PAUSED
						|| tempTask.getState() == INetworkTask.STATE_ERROR) {
					tempTask.waiting();
				}
			}
		}
		startNextTask();
	}

	// 开始下一个任务
	public void startNextTask() {
		List<INetworkTask> list = getTaskList();
		synchronized (list) {
			for (Iterator<INetworkTask> it = list.iterator(); it.hasNext();) {
				INetworkTask tempTask = it.next();
				// 如果有正在进行中的任务
				if (tempTask.getState() == INetworkTask.STATE_PREPARING
						|| tempTask.getState() == INetworkTask.STATE_RUNNING
						|| tempTask.getState() == INetworkTask.STATE_EDITING
						|| tempTask.getState() == INetworkTask.ACTION_RETRY) {
					break;
				}
				// 执行waiting中的任务
				if (tempTask.getState() == INetworkTask.STATE_WAITING) {
					startTask(tempTask);
					break;
				}
			}
		}
	}

	/**
	 * 杀进程后重启任务，已废弃
	 */
	public synchronized void restartNextTask() {
		List<INetworkTask> list = getTaskList();
		synchronized (list) {
			for (Iterator<INetworkTask> it = list.iterator(); it.hasNext();) {
				INetworkTask tempTask = it.next();
				// 如果有正在进行中的任务
				if (tempTask.getState() == INetworkTask.STATE_PREPARING
						|| tempTask.getState() == INetworkTask.STATE_RUNNING
						|| tempTask.getState() == INetworkTask.STATE_EDITING
						|| tempTask.getState() == INetworkTask.ACTION_RETRY) {
					tempTask.waiting();
				}
			}
		}
		startNextTask();
	}

	// 固化任务
	public void persistTask() {
		/*
		 * String uid =
		 * ActiveAccount.getInstance(MainApplication.getAppInstance(
		 * )).getLookLookID(); if(uid == null || uid.equals("")) { return; }
		 */
		List<NetworkTaskInfo> infos = new ArrayList<NetworkTaskInfo>();
		if (tasks != null) {
			synchronized (tasks) {
				for (Iterator<INetworkTask> it = tasks.iterator(); it.hasNext();) {
					infos.add(it.next().info);
				}
			}
		}
		AccountInfo.getInstance(uid).updateNetworkTaskInfo(infos);
		AccountInfo.getInstance(uid).persist();
	}

	public static UploadNetworkTask convert2uploadtask(INetworkTask task) {
		NetworkTaskInfo tmpinfo = task.info;
		if (!tmpinfo.taskType.equals(INetworkTask.TASK_TYPE_CACHE)) {
			return null;
		}
		tmpinfo.upMedias = tmpinfo.caMedias;
		tmpinfo.caMedias = null;

		UploadNetworkTask uptask = new UploadNetworkTask(tmpinfo);
		return uptask;
	}

	/*
	 * private void updateActiveState(INetworkTask t) { if(t != null) { int
	 * index = getIndex(t); int size = getTaskNum(); //最后一个任务 if(index == size -
	 * 1) { this.isActive = false; } } }
	 */

	// 更新日记状态
	private void updateDiaryWrapper(NetworkTaskInfo info, int state) {
		// TODO 上传完成后通过心跳获取日记同步状态,下载后更新日记状态
		// sync_status : 0-未同步 1-日记结构已创建 2-上传中 3-上传完成 4-已同步 5-下载中 6-已下载
		DiaryManager diarymanager = DiaryManager.getInstance();
		MyDiary myLocalDiary = diarymanager.findMyDiaryByUUID(info.diaryuuid);
		if (myLocalDiary != null) {
			// 1.离线上传任务
			if (INetworkTask.TASK_TYPE_CACHE.equalsIgnoreCase(info.taskType)) {
				switch (state) {
				case INetworkTask.STATE_COMPELETED:
					myLocalDiary.sync_status = 1;
					// 固化状态
					// persistTaskToSD();
					break;
				default:
					break;
				}

				// 2.上传任务
			} else if (INetworkTask.TASK_TYPE_UPLOAD
					.equalsIgnoreCase(info.taskType)) {
				switch (state) {
				case INetworkTask.STATE_COMPELETED:
					myLocalDiary.sync_status = 3;
					// 心跳获取是否同步
					// Intent intent = new Intent(ZApplication.getInstance(),
					// DiarySyncService.class);
					// intent.putExtra("diaryid", info.diaryid);
					// ZApplication.getInstance().startService(intent);
					// 固化状态
					// persistTaskToSD();
					break;
				case INetworkTask.STATE_REMOVED:
					myLocalDiary.sync_status = 1;
					break;
				case INetworkTask.ACTION_RETRY:
				case INetworkTask.STATE_EDITING:
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

				// 3.下载任务
			} else if (INetworkTask.TASK_TYPE_DOWNLOAD
					.equalsIgnoreCase(info.taskType)) {
				switch (state) {
				case INetworkTask.STATE_COMPELETED:
					myLocalDiary.sync_status = 6;
					// 固化状态
					// persistTaskToSD();
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

	// ===================以下方法都是在线程中运行,不能直接进行UI操作============================
	@Override
	public void OnTaskStateChange(INetworkTask task, int state) {
		if (task.info != null && task.info.diaryuuid != null) {
			//更新日记状态
			updateDiaryWrapper(task.info, state);
		}
		if (state == INetworkTask.STATE_COMPELETED) {
			
			OnTaskRemoved(task);// 完成的任务直接移除任务队列
			
			if (task.info.taskType.equals(INetworkTask.TASK_TYPE_CACHE)) {
				// 需要转变成正常的uploadtask
				UploadNetworkTask tmptask = convert2uploadtask(task);
				addTask_l(tmptask/*, index*/);
				MiShareTask mitask = MiShareTaskManager.getInstance(uid).getMiShareTaskByDiary(task.getdiaryUUID());
				if(mitask != null) {
					tmptask.setMiTaskManagerListener(mitask);
				}
				tmptask.waiting();
			} else {
				// TODO:登记到MediaMapping
				// write2mapping(task);
//				OnTaskRemoved(task);// 完成的任务直接移除任务队列
			}
			startNextTask();
			
		} else if (state == INetworkTask.STATE_ERROR) {
			// 任务出错，置为暂停状态
			task.pause();
			startNextTask();
		} else if (state == INetworkTask.STATE_REMOVED) {
			// 被移除，开始下一个任务
			startNextTask();
		}

		if (state != INetworkTask.ACTION_RETRY) {
			persistTask();
		}

		Intent intent = new Intent(ACTION_UPDATE_NETWORKTASK);
		ZApplication.getInstance().sendLocalBroadcast(intent);
	}

	@Override
	public void notifyPrecentChange(INetworkTask task, long t, long c) {
		Intent intent = new Intent(INetworkTask.ACTION_TASK_PERCENT_NOTIFY);
		ZApplication.getInstance().sendLocalBroadcast(intent);
	}

	@Override
	public void OnTaskRemoved(INetworkTask task) {
		Log.d("debug", "OnTaskRemoved " + task.info.state);
		clearDataIfNecessary(task);// 如果需要，清除本地文件
		removeTaskFromList(task);// 移除任务队列
		persistTask();
		
		int index = getIndex(task);
		ZLog.e("need to remove " + index);
		if (index > -1) {
			Intent intent = new Intent(INetworkTask.ACTION_TASK_REMOVE_NOTIFY);
			intent.putExtra("taskid", index);
			ZApplication.getInstance().sendLocalBroadcast(intent);
			ZApplication.getInstance().sendLocalBroadcast(new Intent(ACTION_UPDATE_NETWORKTASK));
		}
	}

	/**
	 * 删除下载任务时清除本地文件
	 * @param task
	 */
	private void clearDataIfNecessary(INetworkTask task) {
		if (task != null && task.info != null
				&& task.info.state == INetworkTask.STATE_REMOVED
				&& task.info.taskType.equals(INetworkTask.TASK_TYPE_DOWNLOAD)) {
			// clear
			// Log.d("debug", "clear data when state = " + task.info.state);
			task.info.clearDownloadData();
		}
	}

	public class NetConnectedReceiver extends ZBroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			// 这个监听wifi的连接状态即是否连上了一个有效无线路由，
			// 当上边广播的状态是WifiManager.WIFI_STATE_DISABLING，和WIFI_STATE_DISABLED的时候，根本不会接到这个广播。
			// 在上边广播接到广播是WifiManager.WIFI_STATE_ENABLED状态的同时也会接到这个广播，
			// 当然刚打开wifi肯定还没有连接到有效的无线
			String userid = ActiveAccount.getInstance(context).getLookLookID();
			// Log.d(TAG, "NetConnectedReceiver intent = " + intent.getAction()
			// + "UserID = " + uid);
			if (userid == null || userid.equals("") || !userid.equals(ZStringUtils.nullToEmpty(uid))) {
				return;
			}
			if (WifiManager.NETWORK_STATE_CHANGED_ACTION.equals(intent
					.getAction())) {
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
			} else if ("android.net.conn.CONNECTIVITY_CHANGE".equals(intent
					.getAction())) {
				// GPRS/3G
				if (ZNetworkStateDetector.isAvailable()
						&& ZNetworkStateDetector.isMobile()) {
					Log.d(TAG, "GPRS/3G Connected, startAllTask!");
					startAllTask();
					/*
					 * LoginSettingManager lsm =
					 * AccountInfo.getInstance(ActiveAccount
					 * .getInstance(context).getLookLookID()).setmanager; if(lsm
					 * != null) { String sync = lsm.getSync_type(); if (sync !=
					 * null && !sync.equals("")) { if
					 * (sync.equals(SettingFragment.SYNC_TYPE_ANY)) { Log.d(TAG,
					 * "Not Wifi Network But Setting ANY, startAllTask!");
					 * startAllTask(); } } }
					 */
					// 无网
				} else if (!ZNetworkStateDetector.isAvailable()
						&& !ZNetworkStateDetector.isConnected()) {
					Log.d(TAG, "Network is not available, pauseAllTask!");
					pauseAllTask();
				}
			}
		}
	}

	public class SettingChangeReceiver extends ZBroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (SettingFragment.BROADCAST_PRIVACY_CHANGED.equals(intent
					.getAction())) {
				LoginSettingManager lsm = AccountInfo.getInstance(ActiveAccount
						.getInstance(context).getLookLookID()).setmanager;
				if (lsm != null) {
					String sync = lsm.getSync_type();
					// Settings wifi/any
					if (sync != null && !sync.equals("")) {
						// wifi网络
						if (sync.equals(SettingFragment.SYNC_TYPE_WIFI)) {
							Log.d("==WR==", "Setting WIFI!");
							// 当前无网或非WIFI
							if (!ZNetworkStateDetector.isConnected()
									|| !ZNetworkStateDetector.isWifi()) {
								pauseAllTask();
							} else {
								startAllTask();
							}
							// 所有网络
						} else if (sync.equals(SettingFragment.SYNC_TYPE_ANY)) {
							Log.d("==WR==", "Setting ANY!");
							startAllTask();
						} else {
							Log.d("==WR==", "Setting value + [" + sync + "]");
						}
					} else {
						Log.d("==WR==",
								"Unexpected Setting, sync is null or empty!");
					}
				}
			}
		}
	}

/*	public class NetworkRestartReceiver extends ZBroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			if (ACTION_RESTART_NETWORKTASK.equals(intent.getAction())) {
				Log.d(TAG, "Restart NetworkTask!");
				restartNextTask();
			}
		}

	}*/
}
