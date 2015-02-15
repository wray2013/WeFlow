package com.cmmobi.looklook.misharetask;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.os.HandlerThread;
import android.util.Log;


import cn.zipper.framwork.io.network.ZNetworkStateDetector;
import cn.zipper.framwork.utils.ZStringUtils;
import cn.zipper.framwork.utils.ZThread;

import com.cmmobi.looklook.common.gson.GsonResponse3.MicListItem;
import com.cmmobi.looklook.common.gson.GsonResponse3.MyDiary;
import com.cmmobi.looklook.common.gson.GsonResponse3.VshareDiary;
import com.cmmobi.looklook.info.profile.AccountInfo;
import com.cmmobi.looklook.info.profile.DiaryManager;
import com.cmmobi.looklook.info.profile.MiShareTaskInfo;
import com.cmmobi.looklook.info.profile.NetworkTaskInfo;
import com.cmmobi.looklook.info.profile.VshareDataEntities;
import com.cmmobi.looklook.networktask.CacheNetworkTask;
import com.cmmobi.looklook.networktask.INetworkTask;
import com.cmmobi.looklook.networktask.NetworkTaskManager;
import com.cmmobi.looklook.networktask.NetworkTaskManagerListener;
import com.cmmobi.looklook.networktask.UploadNetworkTask;

/**
 * 微享任务
 * @author Ray
 *   包含多个日记任务，这里仅实现控制日记任务开启（重启）与移除（跳过、删除、完成后移除）
 *
 */
public class MiShareTask implements NetworkTaskManagerListener {
	
	private transient static final String TAG = "MiShareTask";

	public static final int MISHARE_TASK_STATE_UNKNOWN = 0;
	public static final int MISHARE_TASK_STATE_RUNNING = 1;
	public static final int MISHARE_TASK_STATE_PAUSED = 2;
	public static final int MISHARE_TASK_STATE_COMPELETED = 3;
	public static final int MISHARE_TASK_STATE_REMOVED = 4;
	public static final int MISHARE_TASK_STATE_ERROR = 5;
	
	public static final int MISHARE_TASK_STATE_PREPARE = 6;
	
	private String mitaskid;
	
	private int mistate;
	
	private boolean isAnyNetwork; // false-仅wifi true-任意网络
	
	private MiShareTaskInfo info;
	private List<INetworkTask> diarytasks; //微享任务下包含的导入日记任务
	
	private MiShareTaskManagerListener listener;
	
//	private ExecutorService executorService;
//	private transient HandlerThread handlerThread;

	public MiShareTask(MiShareTaskInfo taskinfo) {
		diarytasks = Collections.synchronizedList(new ArrayList<INetworkTask>());
		mistate = MISHARE_TASK_STATE_UNKNOWN;
//		executorService = Executors.newFixedThreadPool(5);
		
		if(taskinfo != null) {
			this.mitaskid = UUID.randomUUID().toString().replace("-", "");
			this.info = taskinfo;
			this.isAnyNetwork = true;
			if(info.remainDiaryuuids == null || info.remainDiaryuuids.size() <= 0) {
				Log.e(TAG, "MiShareTask无日记任务或已经完成");
				mistate = MISHARE_TASK_STATE_COMPELETED;
			}
			List<NetworkTaskInfo> infos = info.networktaskinfos;
			synchronized (infos) {
				for(Iterator<NetworkTaskInfo> it = infos.iterator(); it.hasNext();) {
					NetworkTaskInfo tempinfo = it.next();
					if(tempinfo != null && tempinfo.taskType.equals(INetworkTask.TASK_TYPE_UPLOAD)) {
						UploadNetworkTask uploadtask = new UploadNetworkTask(tempinfo);
						uploadtask.setMiTaskManagerListener(this);
						diarytasks.add(uploadtask);
//						executorService.submit(uploadtask.xxx());
					} else if(tempinfo != null && tempinfo.taskType.equals(INetworkTask.TASK_TYPE_CACHE)) {
						CacheNetworkTask cachetask = new CacheNetworkTask(tempinfo);
						cachetask.setMiTaskManagerListener(this);
						diarytasks.add(cachetask);
//						executorService.submit(cachetask.xxx());
					}
				}
			}
			if (info != null && info.remainDiaryuuids != null
					&& info.remainDiaryuuids.size() > 0) {
				synchronized (info.remainDiaryuuids) {
					for (String uuid : info.remainDiaryuuids) {
						// 若为空间日记任务
						INetworkTask tempTask = NetworkTaskManager.getInstance(info.userid).findTaskByUUID(uuid);
						if(tempTask != null) {
							tempTask.setMiTaskManagerListener(this);
						}
					}
				}
			}
			
//			handlerThread = new HandlerThread("MiShareTaskThread");
//			handlerThread.start();
			
		} else {
			Log.e(TAG, "MiShareTaskInfo is NULL");
		}
	}
	
	public void setMiShareManagerListener(MiShareTaskManagerListener l) {
		listener = l;
	}
	
	/**
	 * 判断微享导入的日记中是否存在给定的diaryuuid的日记
	 * @param diaryuuid
	 * @return
	 */
	public boolean isContainImportDiaryByUUID(String diaryuuid) {
		if(diarytasks != null && diarytasks.size() > 0) {
			synchronized (diarytasks) {
				for(Iterator<INetworkTask> it = diarytasks.iterator(); it.hasNext();) {
					INetworkTask temptask = it.next();
					if(diaryuuid != null && diaryuuid.equals(temptask.getdiaryUUID())) {
						return true;
					}
				}
			}
		}
		return false;
	}
	
	/**
	 * 判断微享包含的全部日记中是否存在给定的diaryuuid的日记
	 * @param diaryuuid
	 * @return
	 */
	public boolean isContainAllDiaryByUUID(String diaryuuid) {
		if (!ZStringUtils.nullToEmpty(diaryuuid).equals("") && info != null
				&& info.allDiaryuuids != null && info.allDiaryuuids.size() > 0) {
			synchronized (info.allDiaryuuids) {
				for (String uuid : info.allDiaryuuids) {
					// bingo
					if (uuid.equals(diaryuuid)) {
						return true;
					}
				}
			}
		}
		return false;
	}
	
	public MiShareTaskInfo getMiShareInfo() {
		return info;
	}
	
	/*
	public String getMiTaskID() {
		return mitaskid;
	}
	*/
	
	public String getMiShareID() {
		if(info != null) {
			return info.mishareuuid;
		}
		return null;
	}
	
	public void setNetwork(boolean net) {
		isAnyNetwork = net;
	}
	
	public boolean getNetwork() {
		return isAnyNetwork;
	}
	
	/**
	 * 该微享任务是否正在上传中（包含的所有任务无暂停或错误状态的则为running）
	 * @return
	 */
	public boolean isMiShareTaskRunning() {
		
		if (info != null && info.remainDiaryuuids != null
				&& info.remainDiaryuuids.size() > 0) {
			synchronized (info.remainDiaryuuids) {
				for (String uuid : info.remainDiaryuuids) {
					// 1.若为空间日记任务
					INetworkTask tempTask = NetworkTaskManager.getInstance(info.userid).findTaskByUUID(uuid);
					if(tempTask != null) {
						/*if(tempTask.getState() == INetworkTask.STATE_RUNNING ||
								tempTask.getState() == INetworkTask.STATE_WAITING ||
								tempTask.getState() == INetworkTask.ACTION_RETRY) {
							Log.d(TAG, "有空间日记子任务正在上传");
							return true;
						}*/
						if(tempTask.getState() == INetworkTask.STATE_ERROR ||
								tempTask.getState() == INetworkTask.STATE_PAUSED) {
							Log.d(TAG, "有空间日记子任务暂停/错误");
							return false;
						}
					}
				}
			}
			// 2.若为导入任务
			if(diarytasks != null && diarytasks.size() > 0) {
				synchronized (diarytasks) {
					for(Iterator<INetworkTask> it = diarytasks.iterator(); it.hasNext();) {
						INetworkTask temptask = it.next();
						/*if(temptask.getState() == INetworkTask.STATE_RUNNING ||
								temptask.getState() == INetworkTask.STATE_WAITING ||
								temptask.getState() == INetworkTask.ACTION_RETRY) {
							Log.d(TAG, "有导入日记子任务正在上传");
							return true;
						}*/
						if(temptask.getState() == INetworkTask.STATE_ERROR ||
								temptask.getState() == INetworkTask.STATE_PAUSED) {
							Log.d(TAG, "有导入日记子任务暂停/错误");
							return false;
						}
					}
				}
			}
		}
//		Log.d(TAG, "没有子任务正在上传，应为暂停！");
		return true;
	}
	
	/**
	 * 获取微享任务包含的所有未上传日记的附件大小（含视频日记封面）
	 * @return
	 */
	public long getMiShareSize() {
		long size = 0;
		if (info != null && info.allDiaryuuids != null
				&& info.allDiaryuuids.size() > 0) {
			DiaryManager diaryManager = DiaryManager.getInstance();
			synchronized (info.allDiaryuuids) {
				for (String uuid : info.allDiaryuuids) {
					MyDiary d = diaryManager.findMyDiaryByUUID(uuid);
					//如果已上传
					if(d == null || d.isSychorized()) {
						continue;
					}
					try {
						long s = Long.parseLong(d.getDiarySize());
						size += s;
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
			size += info.importsize;
		}
		return size;
	}
	
	/**
	 * 开启此微享任务
	 */
	public void start() {
		// 不符合网络配置
		if(!checkNetStatus()) {
			if(listener != null && mistate != MISHARE_TASK_STATE_PAUSED) {
				listener.OnMiShareTaskStateChange(this, MISHARE_TASK_STATE_PAUSED);
				mistate = MISHARE_TASK_STATE_PAUSED;
			}
			return;
		}
		
		if(mistate == MISHARE_TASK_STATE_COMPELETED) {
			if(listener != null) {
				mistate = MISHARE_TASK_STATE_COMPELETED;
				listener.OnMiShareTaskCompleted(this);
			}
			return;
		}
		
		if(diarytasks != null && diarytasks.size() > 0) {
//			pause_l();
			synchronized (diarytasks) {
				mistate = MISHARE_TASK_STATE_RUNNING;
				for(Iterator<INetworkTask> it = diarytasks.iterator(); it.hasNext();) {
					INetworkTask temptask = it.next();
					temptask.info.miShareSettings = 0;
					temptask.start();
				}
			}
		}
	}
	
	/**
	 * 启动微享下所有子任务（包括空间任务）
	 */
	public void startAll() {
		
		// 不符合网络配置
		if(!checkNetStatus()) {
			if(listener != null && mistate != MISHARE_TASK_STATE_PAUSED) {
				listener.OnMiShareTaskStateChange(this, MISHARE_TASK_STATE_PAUSED);
				mistate = MISHARE_TASK_STATE_PAUSED;
			}
			return;
		}
		
		start();
		
		if (info != null && info.remainDiaryuuids != null
				&& info.remainDiaryuuids.size() > 0) {
			synchronized (info.remainDiaryuuids) {
				for (String uuid : info.remainDiaryuuids) {
					INetworkTask tempTask = NetworkTaskManager.getInstance(info.userid).findTaskByUUID(uuid);
					if(tempTask != null) {
						NetworkTaskManager.getInstance(info.userid).MiShareSettings(tempTask, 0);//0-任何网络 1-仅wifi
//						NetworkTaskManager.getInstance(info.userid).setPriority(tempTask, true);
						int statecode = tempTask.getState();
						Log.d(TAG, "tempTask state = " + statecode);
						if (statecode != INetworkTask.STATE_RUNNING
								&& statecode != INetworkTask.STATE_PREPARING
								&& statecode != INetworkTask.STATE_WAITING
								&& statecode != INetworkTask.ACTION_RETRY) {
							NetworkTaskManager.getInstance(info.userid).startTask(tempTask);
						}
					}
				}
			}
		}
	}
	
	/**
	 * 从微享页启动微享下所有子任务（包括空间任务）
	 */
	public void startAllFromShareDiary() {
		if (!ZNetworkStateDetector.isAvailable()
				|| !ZNetworkStateDetector.isConnected()) {
			MishareTaskPriority();
		}
		// 不符合网络配置
		if(!checkNetStatus()) {
			if(listener != null && mistate != MISHARE_TASK_STATE_PAUSED) {
				listener.OnMiShareTaskStateChange(this, MISHARE_TASK_STATE_PAUSED);
				mistate = MISHARE_TASK_STATE_PAUSED;
			}
			return;
		}
		start();
		MishareTaskPriority();
	}
	
	public void MishareTaskPriority() {
		if (info != null && info.remainDiaryuuids != null
				&& info.remainDiaryuuids.size() > 0) {
			synchronized (info.remainDiaryuuids) {
				for (String uuid : info.remainDiaryuuids) {
					INetworkTask tempTask = NetworkTaskManager.getInstance(info.userid).findTaskByUUID(uuid);
					if(tempTask != null) {
						NetworkTaskManager.getInstance(info.userid).MiShareSettings(tempTask, 0);//0-任何网络 1-仅wifi
						NetworkTaskManager.getInstance(info.userid).setPriority(tempTask, true);
					}
				}
			}
		}
	}
	
	/**
	 * 开始前的短暂暂停
	 */
	private void pause_l() {
		if(diarytasks != null && diarytasks.size() > 0) {
			synchronized (diarytasks) {
				mistate = MISHARE_TASK_STATE_PREPARE;
				for(Iterator<INetworkTask> it = diarytasks.iterator(); it.hasNext();) {
					INetworkTask temptask = it.next();
					if(temptask.getState() == INetworkTask.STATE_ERROR
							|| temptask.getState() == INetworkTask.STATE_RUNNING 
							|| temptask.getState() == INetworkTask.STATE_WAITING
							|| temptask.getState() == INetworkTask.STATE_PREPARING
							|| temptask.getState() == INetworkTask.STATE_EDITING
							|| temptask.getState() == INetworkTask.ACTION_RETRY) {
						temptask.pause();
					}
				}
			}
		}
	}
	
	public void pause() {
		if(diarytasks != null && diarytasks.size() > 0) {
			synchronized (diarytasks) {
				for(Iterator<INetworkTask> it = diarytasks.iterator(); it.hasNext();) {
					INetworkTask temptask = it.next();
					if(temptask.getState() == INetworkTask.STATE_ERROR
							|| temptask.getState() == INetworkTask.STATE_RUNNING 
							|| temptask.getState() == INetworkTask.STATE_WAITING
							|| temptask.getState() == INetworkTask.STATE_PREPARING
							|| temptask.getState() == INetworkTask.STATE_EDITING
							|| temptask.getState() == INetworkTask.ACTION_RETRY) {
						temptask.pause();
					}
				}
				mistate = MISHARE_TASK_STATE_PAUSED;
			}
		}
	}
	
	/**
	 * 移除此微享任务
	 */
	public void remove() {
		if(diarytasks != null) {
			synchronized (diarytasks) {
				for(Iterator<INetworkTask> it = diarytasks.iterator(); it.hasNext();) {
					INetworkTask temptask = it.next();
					// 不再接收该日记任务的广播
					temptask.setMiTaskManagerListener(null);
					temptask.remove();
				}
			}
			diarytasks.clear();
		}
		info.remainDiaryuuids.clear();
//		if (handlerThread != null) {
//			handlerThread.quit();
//			handlerThread = null;
//		}
		mistate = MISHARE_TASK_STATE_REMOVED;
		if(listener != null) {
			listener.OnMiShareTaskRemoved(this);
		}
/*		if(executorService != null) {
			executorService.shutdown();
		}*/
	}
	
	/**
	 * Only Be Used For HomePage
	 * 仅包含空间日记
	 * 从微享任务队列中移除的操作放到后面的OnTaskRemoved回调中，这里只修改微享创建请求
	 * @param diaryuuid
	 */
	public void removeDiaryFromMiShareTask(String diaryuuid) {
		String diaryid = getDiaryidByUUID(diaryuuid);
		diaryid = ZStringUtils.nullToEmpty(diaryid);
		// 找到
		if(!diaryid.equals("")) {
			Log.d(TAG, "删除任务:" + diaryuuid);
			removeMishareDiaryids(diaryid);
			if(isAllTaskCompleted()) {
				if(listener != null) {
					listener.OnMiShareTaskCompleted(this);
				}
			}
		} else {
			removeDiaryFromMiShareTaskByUUID(diaryuuid);
		}
	}
	
	@Deprecated
	public void removeDiaryFromMiShareTaskByUUID(String diaryuuid) {
		// 找到
		if(!diaryuuid.equals("")) {
			Log.d(TAG, "删除任务ByUUID:" + diaryuuid);
			removeMishareDiaryids(diaryuuid);
			if(isAllTaskCompleted()) {
				if(listener != null) {
					listener.OnMiShareTaskCompleted(this);
				}
			}
		}
	}
	
	/**
	 * 从所有微享里找出对应的diaryid，请求uuid本身为diaryid的也直接返回diaryid
	 * @param diaryuuid
	 * @return
	 */
	private String getDiaryidByUUID(String diaryuuid) {
		String diaryid = "";
		VshareDataEntities localVShareData=AccountInfo.getInstance(info.userid).vshareLocalDataEntities;
		if(localVShareData!=null){
			List<MicListItem> milist = Collections.synchronizedList(localVShareData.getCache());
			if (milist != null) {
				synchronized (milist) {
					for (MicListItem item : milist) {
						if (item.diarys != null && item.diarys.length > 0) {
							for (VshareDiary myDiary : item.diarys) {
								if(ZStringUtils.nullToEmpty(myDiary.diaryuuid).equals(diaryuuid) ||
										ZStringUtils.nullToEmpty(myDiary.diaryid).equals(diaryuuid)) {
									diaryid = ZStringUtils.nullToEmpty(myDiary.diaryid);
									return diaryid;
								}
							}
						}
					}
				}
			}
		}
		return diaryid;
	}
	
	/**
	 * 从diarytasks中跳过指定diaryuuid的日记任务
	 * @param diaryuuid
	 */
	public void skipDiaryFromMiShareTask(String diaryuuid) {
		
	}
	
	/**
	 * 判断微享任务下所有的子任务是否都已经完成
	 * @return
	 */
	public boolean isAllTaskCompleted() {
		if(diarytasks != null && diarytasks.size() > 0) {
			Log.d(TAG, "剩余导入日记任务数 " + diarytasks.size());
			return false;
		}
		if(info != null && info.remainDiaryuuids != null
				&& info.remainDiaryuuids.size() > 0) {
			DiaryManager diaryManager = DiaryManager.getInstance();
			synchronized (info.remainDiaryuuids) {
				for(Iterator<String> it = info.remainDiaryuuids.iterator(); it.hasNext();) {
					String uuid = it.next();
					MyDiary mydiary = diaryManager.findMyDiaryByUUID(uuid);
					//没找到日记或者找到日记但没有同步
					if(mydiary == null) {
						Log.e(TAG, "未找到空间日记，还存在导入日记未上传，等待");
						return false;
					}
					// 空间日记任务未完成
					if(!mydiary.isSychorized()) {
						Log.e(TAG, "找到日记但还没有同步，等待");
						return false;
					// 空间日记任务已完成，尝试更新请求参数id列表
					} else {
						Log.d(TAG, "空间日记任务已完成，尝试更新请求参数id列表");
						updateMishareDiaryids(mydiary.diaryuuid, mydiary.diaryid);
					}
				}
			}
		}
		Log.d(TAG, "C剩余日记任务数 " + info.remainDiaryuuids.size());
		return true;
	}

	@Override
	public void OnTaskRemoved(INetworkTask task) {
		Log.d(TAG, "子任务" + task.getdiaryUUID() + "被移除");
		// 该日记任务移除出队列
		if(diarytasks != null) {
			synchronized (diarytasks) {
				diarytasks.remove(task);
			}
		}
		Log.d(TAG, "A剩余子任务数 " + info.remainDiaryuuids.size());
		info.remainDiaryuuids.remove(task.getdiaryUUID());
		Log.d(TAG, "B剩余子任务数 " + info.remainDiaryuuids.size());
		//TODO:判断此微享任务是否全部完成
		if(isAllTaskCompleted()) {
			Log.d(TAG, "微享任务" + this.getMiShareID() + "完成");
			mistate = MISHARE_TASK_STATE_COMPELETED;
			if(listener != null) {
				listener.OnMiShareTaskCompleted(this);
			}
		}
		
	}

	@Override
	public void OnTaskStateChange(INetworkTask task, int state) {
		// TODO Auto-generated method stub
		if (task != null && task.info != null) {
			switch(state) {
			case INetworkTask.STATE_COMPELETED:
				if(task.info.taskType.equals(INetworkTask.TASK_TYPE_CACHE)) {
					INetworkTask tmptask = null;
					// 若是导入的日记
					if(diarytasks.contains(task)) {
						Log.d(TAG, "导入离线任务转换成普通任务");
						// 需要转变成正常的uploadtask
						tmptask = NetworkTaskManager.convert2uploadtask(task);
						tmptask.setMiTaskManagerListener(this);
						String oldDiaryuuid = task.getdiaryUUID();
						String newDiaryid = tmptask.getdiaryID();
						updateMishareDiaryids(oldDiaryuuid, newDiaryid);
						diarytasks.add(tmptask);
	//					executorService.submit(tmptask.getThread());
//						task.remove();
						OnTaskRemoved(task);
						tmptask.waiting();
						tmptask.start();
					}
				} else {
					updateMishareDiaryids(task.getdiaryUUID(), task.getdiaryID());
					Log.d(TAG, "日记任务【uuid:" + task.getdiaryUUID() + " ,id:" + task.getdiaryID() + "】已完成，将移除出微享任务队列");
					OnTaskRemoved(task);
				}
				break;
			case INetworkTask.STATE_ERROR:
				Log.d(TAG, "任务出错，准备暂停……");
				if(listener != null && mistate != MISHARE_TASK_STATE_ERROR) {
					listener.OnMiShareTaskStateChange(this, MISHARE_TASK_STATE_ERROR);
					mistate = MISHARE_TASK_STATE_ERROR;
				}
				break;
			case INetworkTask.STATE_PAUSED:
				if(mistate != MISHARE_TASK_STATE_PREPARE) {
					Log.d(TAG, "任务暂停");
					pause();
					if(listener != null && mistate != MISHARE_TASK_STATE_PAUSED) {
						listener.OnMiShareTaskStateChange(this, MISHARE_TASK_STATE_PAUSED);
						mistate = MISHARE_TASK_STATE_PAUSED;
					}
				}
				break;
			default:
				if(listener != null) {
//				Log.d(TAG, "任务状态迁移-->[" + state + "]");
					listener.OnMiShareTaskStateChange(this, MISHARE_TASK_STATE_UNKNOWN);
					mistate = MISHARE_TASK_STATE_UNKNOWN;
				}
				break;
			}
		}
	}

	@Override
	public void notifyPrecentChange(INetworkTask task, long t, long c) {
		// TODO Auto-generated method stub
		
	}
	
	private void updateMishareDiaryids(String olduuid, String newid) {
		if(this.info != null && this.info.mishareParams != null
				&& this.info.mishareParams.diaryid != null) {
			synchronized (this.info.mishareParams.diaryid) {
				if(this.info.mishareParams.diaryid.contains(olduuid)) {
					this.info.mishareParams.diaryid.remove(olduuid);
					this.info.mishareParams.diaryid.add(newid);
				}
			}
		}
	}

	public void removeMishareDiaryids(String diaryid) {
		if(this.info != null && this.info.mishareParams != null
				&& this.info.mishareParams.diaryid != null) {
			synchronized (this.info.mishareParams.diaryid) {
				this.info.mishareParams.diaryid.remove(diaryid);
			}
		}
	}
	
	public boolean checkNetStatus() {
		boolean isconnected = ZNetworkStateDetector.isAvailable()
				&& ZNetworkStateDetector.isConnected();
		if (isconnected) {
			// wifi网络或者设置的任意条件
			if(ZNetworkStateDetector.isWifi() || getNetwork()) {
				Log.e(TAG, "微享时用户选择【" + (getNetwork() ? "立即上传或默认" : "仅wifi下上传") + 
						"】当前网络【" + (ZNetworkStateDetector.isWifi() ? "WIFI" : "非WIFI") +
						"】========符合网络设置，继续微享任务。");
				return true;
			}
		}
		Log.e(TAG, "微享时用户选择【" + (getNetwork() ? "立即上传" : "仅wifi下上传") + 
				"】当前网络【" + (isconnected ? "网络不可用" : (ZNetworkStateDetector.isWifi() ? "WIFI" : "非WIFI")) +
				"】========不符合网络设置，微享任务暂停。");
		return false;
	}
	
}
