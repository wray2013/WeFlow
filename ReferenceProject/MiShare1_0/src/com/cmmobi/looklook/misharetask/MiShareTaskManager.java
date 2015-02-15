package com.cmmobi.looklook.misharetask;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

import cn.zipper.framwork.core.ZApplication;
import cn.zipper.framwork.core.ZBroadcastReceiver;
import cn.zipper.framwork.core.ZLog;
import cn.zipper.framwork.io.network.ZNetworkStateDetector;
import cn.zipper.framwork.utils.ZStringUtils;

import com.cmmobi.looklook.MainApplication;
import com.cmmobi.looklook.activity.LookLookActivity;
import com.cmmobi.looklook.common.gson.GsonResponse3.MicListItem;
import com.cmmobi.looklook.fragment.SettingFragment;
import com.cmmobi.looklook.info.profile.AccountInfo;
import com.cmmobi.looklook.info.profile.ActiveAccount;
import com.cmmobi.looklook.info.profile.LoginSettingManager;
import com.cmmobi.looklook.info.profile.MiShareTaskInfo;
import com.cmmobi.looklook.info.profile.VshareDataEntities;
import com.cmmobi.looklook.offlinetask.OfflineTaskManager;

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


public class MiShareTaskManager implements MiShareTaskManagerListener {

	private static final String TAG = "MiShareTaskManager";
	
	private static MiShareTaskManager manager;
	
	private static String uid; //用户id，与账号绑定
	
	private NetConnectedReceiver myBroadcastReceiver = null;
//	private SettingChangeReceiver settingChangeReceiver = null;
	
	// (微享uuid, 日记任务队列{})
//	private Map<String, List<INetworkTask>> miShareTasks; //需要持久化
//	private List<MiShareTask> miShareTasks; //需要持久化
	private ConcurrentLinkedQueue<MiShareTask> miShareTasks;//需要持久化
	
	private MiShareTaskManager() {
		init();
	}
	
	//获取单例
	public static MiShareTaskManager getInstance(String uID) {
		Log.v(TAG, "MiShareTaskManager getInstance - uID:" + uID);
		if(uID != null && !uID.equals("")) {
			if (manager == null || !uID.equals(uid)) {
				Log.e(TAG, "new MiShareTaskManager()");
				manager = new MiShareTaskManager();
			}
			uid = uID;
		}
		return manager;
	}
	
	private void init() {
		//注册网络设置监听、网络连接状态
		/*if(settingChangeReceiver == null) {
			settingChangeReceiver = new SettingChangeReceiver();
			IntentFilter settingfilter = new IntentFilter();
			settingfilter.addAction(SettingFragment.BROADCAST_PRIVACY_CHANGED);
			LocalBroadcastManager.getInstance(MainApplication.getAppInstance()).registerReceiver(settingChangeReceiver, settingfilter);
		}*/
		if (myBroadcastReceiver == null) {
			Log.d(TAG, "MiShareTaskManager init NetConnectedReceiver");
			myBroadcastReceiver = new NetConnectedReceiver();
			IntentFilter filter = new IntentFilter();
			filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
			filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
			filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
			filter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
			ZApplication.getInstance().registerReceiver(myBroadcastReceiver, filter);
		}
	}
	
	private synchronized ConcurrentLinkedQueue<MiShareTask> getMiShareTaskList() {
		if(miShareTasks == null) { //根据uid从AccountInfo中读取未完成的
			//TODO:
			ConcurrentLinkedQueue<MiShareTask> taskList = AccountInfo.getInstance(uid).readMiShareTaskInfo();
			if (taskList != null) {
				synchronized (taskList) {
					for (Iterator<MiShareTask> it = taskList.iterator(); it.hasNext();) {
						it.next().setMiShareManagerListener(this);
					}
				}
				miShareTasks = taskList;
				if(miShareTasks != null) Log.d(TAG, "获取微享任务数[" + miShareTasks.size() + "]");
			}
		}
		if(miShareTasks == null) {
			miShareTasks = new ConcurrentLinkedQueue<MiShareTask>();
		}
		return miShareTasks;
	}
	
	// 检测日记是否在已有的微享中
	public boolean isContainImportDiaryByDiaryUUID(String diaryuuid) {
		ConcurrentLinkedQueue<MiShareTask> tasks = getMiShareTaskList();
		synchronized (tasks) {
			for (Iterator<MiShareTask> it = tasks.iterator(); it.hasNext();) {
				MiShareTask tempTask = it.next();
				if(tempTask.isContainImportDiaryByUUID(diaryuuid)) {
					return true;
				}
			}
		}
		return false;
	}
	
	/**
	 * 获取包含一组diaryuuid对应日记的微享任务数
	 * @param diaryuuids 12345,67890,23456
	 * @return
	 */
	public int getContainDiaryNum(String diaryuuids) {
		if(ZStringUtils.nullToEmpty(diaryuuids).equals("")) {
			Log.i(TAG, "input diaryuuid is empty, are you sure?");
			return 0;
		}
		int tasknum = 0;
		String[] uuids = diaryuuids.split(",");
		for(String uuid : uuids) {
			ConcurrentLinkedQueue<MiShareTask> tasks = getMiShareTaskList();
			synchronized (tasks) {
				for (Iterator<MiShareTask> it = tasks.iterator(); it.hasNext();) {
					MiShareTask tempTask = it.next();
					if(tempTask.isContainAllDiaryByUUID(uuid)) {
						tasknum += 1;
						break;
					}
				}
			}
		}
		return tasknum;
	}
	
	public MiShareTask getMiShareTaskByDiary(String diaryuuid) {
		ConcurrentLinkedQueue<MiShareTask> tasks = getMiShareTaskList();
		synchronized (tasks) {
			for (Iterator<MiShareTask> it = tasks.iterator(); it.hasNext();) {
				MiShareTask tempTask = it.next();
				if(tempTask.isContainAllDiaryByUUID(diaryuuid)) {
					return tempTask;
				}
			}
		}
		return null;
	}
	
	public synchronized void persistMiShareTask() {
		String uid = ActiveAccount.getInstance(MainApplication.getAppInstance()).getLookLookID();
		if(uid == null || uid.equals("")) {
			return;
		}
		ConcurrentLinkedQueue<MiShareTaskInfo> infos = new ConcurrentLinkedQueue<MiShareTaskInfo>();
		if (miShareTasks != null) {
			synchronized (miShareTasks) {
				for (Iterator<MiShareTask> it = miShareTasks.iterator(); it.hasNext();) {
					infos.add(it.next().getMiShareInfo());
				}
			}
		}
		Log.e(TAG, "MiShareTask persist!");
		AccountInfo.getInstance(uid).updateMiShareTaskInfo(infos);
		AccountInfo.getInstance(uid).persist();
	}
	
	
	/**
	 * 通过微享UUID找到任务
	 * @param mishareuuid
	 * @return
	 */
	private synchronized MiShareTask getMiShareTaskByMiUUID(String mishareuuid) {
		ConcurrentLinkedQueue<MiShareTask> tasks = getMiShareTaskList();
		synchronized (tasks) {
			for (Iterator<MiShareTask> it = tasks.iterator(); it.hasNext();) {
				MiShareTask tempTask = it.next();
				if(tempTask.getMiShareID() != null && tempTask.getMiShareID().equalsIgnoreCase(mishareuuid)) {
					return tempTask;
				}
			}
		}
		return null;
	}
	
	public void AutoStartNewMiShareTask(MicListItem mishare) {
		MiShareTaskInfo info = new MiShareTaskInfo(mishare);
		MiShareTask task = new MiShareTask(info);
		addMiShareTask(task);
		Log.d(TAG, "New MiShareTask, Auto Start!");
		startMiShareTask(task);
	}
	
	/**
	 * 添加微享任务
	 * @param t
	 */
	public synchronized void addMiShareTask(MiShareTask t) {
		if(t == null || t.getMiShareID() == null || t.getMiShareID().equals("")) {
			Log.e(TAG, "invalid MiShareTask");
			return;
		}
		
		MiShareTask mtask = getMiShareTaskByMiUUID(t.getMiShareID());
		if(mtask != null) {
			Log.d(TAG, "The Task is Already In The MiShareTask List");
			return;
		}
		
		t.setMiShareManagerListener(this);
		if (miShareTasks != null) {
			synchronized (miShareTasks) {
				miShareTasks.add(t);
			}
		}
	}
	
	/**
	 * 启动微享任务
	 * @param t 微享任务
	 */
	private synchronized void startMiShareTask(MiShareTask t) {
		MiShareTask task = getMiShareTaskByMiUUID(t.getMiShareID());
		if(task != null) {
			if(task.isAllTaskCompleted()) {
				Log.d(TAG, "[startMiShareTask(MiShareTask t)] tasks are all completed");
				OnMiShareTaskCompleted(task);
			} else {
				task.start();
				if(task.checkNetStatus() && task.isMiShareTaskRunning()) {
					sendBroadcast(task.getMiShareID(), "0");//0 上传中 1上传失败 2上传成功
				} else {
					sendBroadcast(task.getMiShareID(), "1");
				}
			}
		}
	}
	
	/**
	 * 启动微享任务，微享界面选择时专用
	 * @param t 微享任务
	 */
	public synchronized void startMiShareTaskFromShareDiary(MiShareTask t, boolean isforce) {
		MiShareTask task = getMiShareTaskByMiUUID(t.getMiShareID());
		if(task != null) {
			task.setNetwork(isforce);
			if(task.isAllTaskCompleted()) {
				Log.d(TAG, "[startMiShareTask(MiShareTask t, boolean isforce)] tasks are all completed");
				OnMiShareTaskCompleted(task);
			} else {
				task.startAllFromShareDiary();
				if(task.checkNetStatus() && task.isMiShareTaskRunning()) {
					sendBroadcast(task.getMiShareID(), "0");//0 上传中 1上传失败 2上传成功
				} else {
					sendBroadcast(task.getMiShareID(), "1");
				}
			}
		}
	}
	
	/**
	 * 启动微享任务
	 * @param miuuid  微享uuid
	 * @param isforce 是否强制启动全部任务
	 */
	public synchronized void startMiShareTask(String miuuid, boolean isforce) {
		MiShareTask task = getMiShareTaskByMiUUID(miuuid);
		if(task != null) {
			if(task.isAllTaskCompleted()) {
				Log.d(TAG, "[startMiShareTask(String miuuid, boolean isforce)] tasks are all completed");
				OnMiShareTaskCompleted(task);
			} else {
				if(isforce) {
					task.startAll();
				} else {
					task.start();
				}
				if(task.checkNetStatus() && task.isMiShareTaskRunning()) {
					sendBroadcast(task.getMiShareID(), "0");//0 上传中 1上传失败 2上传成功
				} else {
					sendBroadcast(task.getMiShareID(), "1");
				}
			}
		}
	}
	
	/**
	 * 启动全部微享队列
	 */
	private synchronized void startAllMiShareTask() {
		ConcurrentLinkedQueue<MiShareTask> tasks = getMiShareTaskList();
		synchronized (tasks) {
			for (Iterator<MiShareTask> it = tasks.iterator(); it.hasNext();) {
				MiShareTask tempTask = it.next();
				if(tempTask.isAllTaskCompleted()) {
					Log.d(TAG, "[startAllMiShareTask] tasks are all completed");
					OnMiShareTaskCompleted(tempTask);
				} else {
					tempTask.startAll();
					sendBroadcast(tempTask.getMiShareID(), "0");
				}
			}
			/*int n = tasks.size();
			for(int i=n-1;i>=0;i--) {
				MiShareTask tempTask = tasks.get(i);
				if(tempTask.isAllTaskCompleted()) {
					Log.d(TAG, "[startAllMiShareTask] tasks are all completed");
					sendBroadcast(tempTask.getMiShareID(), "2");
					submitToOfflineTask(tempTask);
					// 完成微享任务后移除
					removeMiShareTask(tempTask.getMiShareID());
				} else {
					tempTask.start();
					sendBroadcast(tempTask.getMiShareID(), "0");
				}
			}*/
		}
	}
	
	/**
	 * 主动移除微享任务
	 * @param miuuid
	 */
	public synchronized void removeMiShareTask(String miuuid) {
		MiShareTask task = getMiShareTaskByMiUUID(miuuid);
		if(task != null) {
			task.remove();
		}
	}
	
	/**
	 * 暂停微享任务
	 * @param miuuid
	 */
	public void pauseMiShareTask(String miuuid) {
		MiShareTask task = getMiShareTaskByMiUUID(miuuid);
		if(task != null) {
			task.pause();
			sendBroadcast(task.getMiShareID(), "1");
		}
	}
	
	/**
	 * 暂停所有微享任务
	 */
	private synchronized void pauseAllMiShareTask() {
		ConcurrentLinkedQueue<MiShareTask> tasks = getMiShareTaskList();
		synchronized (tasks) {
			for (Iterator<MiShareTask> it = tasks.iterator(); it.hasNext();) {
				MiShareTask tempTask = it.next();
				tempTask.pause();
				sendBroadcast(tempTask.getMiShareID(), "1");
			}
		}
	}
	
	
	//***************************************************************************
	
	
	public class NetConnectedReceiver extends ZBroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			// 这个监听wifi的连接状态即是否连上了一个有效无线路由，
			// 当上边广播的状态是WifiManager.WIFI_STATE_DISABLING，和WIFI_STATE_DISABLED的时候，根本不会接到这个广播。
			// 在上边广播接到广播是WifiManager.WIFI_STATE_ENABLED状态的同时也会接到这个广播，
			// 当然刚打开wifi肯定还没有连接到有效的无线
			String uid = ActiveAccount.getInstance(context).getLookLookID();
			if(uid == null || uid.equals("")) {
				return;
			}
			if (WifiManager.NETWORK_STATE_CHANGED_ACTION.equals(intent.getAction())) {
				Parcelable parcelableExtra = intent
						.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
				if (null != parcelableExtra) {
					NetworkInfo networkInfo = (NetworkInfo) parcelableExtra;
					State state = networkInfo.getState();
					boolean isConnected = state == State.CONNECTED;// 当然，这边可以更精确的确定状态
					if (isConnected) {
						Log.d(TAG, "WIFI connected, Start Task Queue!");
//						startAllTask();
						startAllMiShareTask();
					}
				}
			} else if("android.net.conn.CONNECTIVITY_CHANGE".equals(intent.getAction())) {
				//GPRS/3G
				if(ZNetworkStateDetector.isAvailable() 
						&& ZNetworkStateDetector.isMobile()) {
					Log.d(TAG, "GPRS/3G Connected, startAllTask!");
//					startAllTask();
					startAllMiShareTask();
				//无网
				} else if(!ZNetworkStateDetector.isAvailable() 
						&& !ZNetworkStateDetector.isConnected()){
					Log.d(TAG, "Network is not available, pauseAllTask!");
//					pauseAllTask();
					pauseAllMiShareTask();
				}
			}
		}
	}
	
/*	public class SettingChangeReceiver extends ZBroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			if(SettingFragment.BROADCAST_PRIVACY_CHANGED.equals(intent.getAction())) {
				LoginSettingManager lsm = AccountInfo.getInstance(ActiveAccount
						.getInstance(context).getLookLookID()).setmanager;
				if(lsm != null) {
					String sync = lsm.getSync_type();
					// Settings wifi/any
					if (sync != null && !sync.equals("")) {
						// wifi网络
						if (sync.equals(SettingFragment.SYNC_TYPE_WIFI)) {
							Log.d(TAG, "Setting WIFI!");
							//当前无网或非WIFI
							if(!ZNetworkStateDetector.isConnected() ||
									!ZNetworkStateDetector.isWifi()) {
//								pauseAllTask();
								pauseAllMiShareTask();
							} else {
//								startAllTask();
								startAllMiShareTask();
							}
						// 所有网络
						} else if (sync.equals(SettingFragment.SYNC_TYPE_ANY)) {
							Log.d(TAG, "Setting ANY!");
//							startAllTask();
							startAllMiShareTask();
						} else {
							Log.d(TAG, "Setting value + [" + sync + "]");
						}
					} else {
						Log.d(TAG, "Unexpected Setting, sync is null or empty!");
					}
				}
			}
		}
	}*/
	

	@Override
	public void OnMiShareTaskRemoved(MiShareTask task) {
		// TODO Auto-generated method stub
		Log.d(TAG, "微享上传任务【" + task.getMiShareID() + "】被移除，OnMiShareTaskRemoved");
		if (miShareTasks != null) {
			synchronized (miShareTasks) {
				miShareTasks.remove(task);
			}
		}
		persistMiShareTask();
	}

	@Override
	public void OnMiShareTaskStateChange(MiShareTask task, int state) {
		// TODO Auto-generated method stub
//		Log.d(TAG, "OnMiShareTaskStateChange state = " + state);
		if(state == MiShareTask.MISHARE_TASK_STATE_PAUSED ||
				state == MiShareTask.MISHARE_TASK_STATE_ERROR) {
			Log.d(TAG, "MiShareTask paused!");
			sendBroadcast(task.getMiShareID(), "1"); //0 上传中 1上传失败 2上传成功
		}
		persistMiShareTask();
	}

	@Override
	public void OnMiShareTaskCompleted(MiShareTask task) {
		// TODO Auto-generated method stub
		Log.d(TAG, "OnMiShareTaskCompleted,微享上传任务【" + task.getMiShareID() + "】已完成！");
		if(task != null && task.getMiShareInfo() != null) {
			// 完成微享任务后移除
			removeMiShareTask(task.getMiShareID());
			sendBroadcast(task.getMiShareID(), "2"); //0 上传中 1上传失败 2上传成功
			submitToOfflineTask(task);
		}
		persistMiShareTask();
	}
	
	private void submitToOfflineTask(MiShareTask task) {
		MiShareTaskInfo info = task.getMiShareInfo();
		if(info != null && info.mishareParams != null && info.mishareParams.diaryid != null) {
			if (info.mishareParams.diaryid.size() > 0) {
				Log.e(TAG, "发送创建微享请求至离线任务");
				ZLog.printObject(info.mishareParams);
				OfflineTaskManager.getInstance().addVShareTask(
						combineDiaryids(info.mishareParams.diaryid),
						info.mishareParams.mishareid,
						info.mishareParams.content,
						info.mishareParams.misharetitle,
						info.mishareParams.userobj,
						info.mishareParams.longitude,
						info.mishareParams.latitude,
						info.mishareParams.position,
						info.mishareParams.position_status,
						info.mishareParams.capsule,
						info.mishareParams.burn_after_reading,
						info.mishareParams.capsule_time);
			} else {
				Log.e(TAG, "请求diaryids为空，是否已删除该微享？？？");
				// 删除
				VshareDataEntities localVShareData=AccountInfo.getInstance(info.userid).vshareLocalDataEntities;
				if(localVShareData!=null) {
					localVShareData.removeMemberUuid(task.getMiShareID());
					// 刷新
				}
			}
		}
	}
	
	private String combineDiaryids(List<String> diaryid) {
		String ret = "+";
		if (diaryid != null) {
			synchronized (diaryid) {
				for (String split : diaryid) {
					ret += ("," + split);
				}
			}
			ret = ret.replace("+,", "");
		}
		return ret;
	}
	
	private void sendBroadcast(String mishareuuid, String status) {
		Log.e(TAG, "发送广播[uuid:" + mishareuuid + " ====== status:" + status + "]");
		AccountInfo.getInstance(uid).vshareLocalDataEntities.updateStutas(mishareuuid, status);
	}
}
