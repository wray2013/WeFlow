package com.cmmobi.looklook.networktask;

import java.util.UUID;

import android.content.Intent;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.HandlerThread;
import android.util.Log;
import cn.zipper.framwork.core.ZApplication;
import cn.zipper.framwork.core.ZLog;
import cn.zipper.framwork.io.network.ZNetworkStateDetector;

import com.cmmobi.looklook.MainApplication;
import com.cmmobi.looklook.fragment.SettingFragment;
import com.cmmobi.looklook.info.profile.AccountInfo;
import com.cmmobi.looklook.info.profile.ActiveAccount;
import com.cmmobi.looklook.info.profile.LoginSettingManager;
import com.cmmobi.looklook.info.profile.NetworkTaskInfo;

public abstract class INetworkTask implements Callback {

	public static final int STATE_WAITING = 0;// 等待其他任务完成
	public static final int STATE_PREPARING = 1;//
	public static final int STATE_RUNNING = 2;//
	public static final int STATE_PAUSED = 3;// 任务暂停
	public static final int STATE_EDITING = 4;// 等待用户编辑
	public static final int STATE_COMPELETED = 5;
	public static final int STATE_REMOVED = 6;
	public static final int STATE_ERROR = 7;
	public static final int STATE_COLLECT = 8; //收藏
	public static final int STATE_PUBLISH = 9; //发布
	public static final int ACTION_RETRY = 10;

	public static final int TASK_ERROR_UNKNOWN = -1;
	public static final int TASK_ERROR_TIMEOUT = 0;
	public static final int TASK_ERROR_SERVER_ERROR  = 1;
	public static final int TASK_ERROR_NOT_GETSOCKET = 2;
	public static final int TASK_ERROR_NOT_PUBLISHED = 3;
	public static final int TASK_ERROR_NOT_COLLECTED = 4;
	public static final int TASK_ERROR_COVER_UPLOAD_FAIL = 5;
	
	public static final String ACTION_NOTIFY = "action_notify";
	public static final String ACTION_TASK_PERCENT_NOTIFY = "action_task_percent_notify";
	public static final String ACTION_TASK_REMOVE_NOTIFY = "action_task_remove_notify";
	
	//非日记类型任务状态广播
	public static final String ACTION_TASK_STATE_CHANGE = "action_task_state_change";
	
	public static final String TASK_TYPE_DOWNLOAD = "download";
	public static final String TASK_TYPE_UPLOAD = "upload";
	public static final String TASK_TYPE_CACHE = "cache"; //不完整的上传任务
	
	protected transient Handler handler;
	protected transient HandlerThread handlerThread;
	protected NetworkTaskManagerListener taskmanagerListener, mitaskmanagerListener;

//	public int state = STATE_WAITING;
	public int errcode = TASK_ERROR_UNKNOWN;
	
	protected String taskId;
	public NetworkTaskInfo info;
	
	private boolean threadLocked = false;
	
	public INetworkTask(NetworkTaskInfo info) {
		this.info = info;
		taskId = UUID.randomUUID().toString().replace("-", "");
		if(this.info != null && this.info.diaryuuid == null) {
			this.info.diaryuuid = UUID.randomUUID().toString().replace("-", "");
		}
/*		handlerThread = new HandlerThread("NetworkTaskThread");
//		handlerThread.setPriority(Thread.MIN_PRIORITY);// 给工作者线程低优先级
		handlerThread.start();
		handler = new Handler(handlerThread.getLooper(), this);*/
	}
	
	//=========================== Public Function ===========================
	
	public void start() {
		ZLog.e(" task start");
		threadLocked = true;
		startThread();
		if (getState() == STATE_WAITING 
				|| getState() == STATE_PAUSED
				|| getState() == STATE_EDITING) {
			//如果prepare过了  可能不需要重新进入STATE_PREPARING
			//比如在running的过程中pause了
			//再start,应该直接进入running状态
			
			if (!info.isPrepareDone) {
				translateToState(STATE_PREPARING);				
			} else {
				translateToState(STATE_RUNNING, 1);		
			}
		} else if(getState() == STATE_ERROR) {
			translateToState(STATE_WAITING);
		}
	}

	public void pause() {
		ZLog.e(" task pause");
		threadLocked = false;
		startThread();
		if (getState() == STATE_RUNNING 
				|| getState() == STATE_WAITING
				|| getState() == STATE_PREPARING
				|| getState() == STATE_EDITING
                || getState() == STATE_ERROR
				|| getState() == ACTION_RETRY) {
			translateToState(STATE_PAUSED);
		}
	}

	public void waiting() {
		startThread();
		translateToState(STATE_WAITING);
	}

	public void remove() {
		threadLocked = false;
		startThread();
		translateToState(STATE_REMOVED);
	}

	protected void startThread() {
		if(handlerThread == null) {
			handlerThread = new HandlerThread("NetworkTaskThread");
	//		handlerThread.setPriority(Thread.MIN_PRIORITY);// 给工作者线程低优先级
			handlerThread.start();
			handler = new Handler(handlerThread.getLooper(), this);
			Log.d("==WR==", "----- startThread ----- new HandlerThread : " + handlerThread.getThreadId());
		}
	}
	
	protected void stopThread() {
		if (handlerThread != null && !threadLocked) {
			Log.d("==WR==", "----- stopThread ----- old HandlerThread : " + handlerThread.getThreadId());
			handlerThread.quit();
			handlerThread = null;
//			handler = null;
		}
		threadLocked = false;
	}
	//=============================================================================
	
	/**
	 * 带来源的状态迁移 Only for Debug
	 * @param nextState
	 * @param entry
	 */
	protected void translateToState(int nextState, int entry) {
		ZLog.e("Entry[" + entry + "] " + "translate state=" + getState() + " to nextState=" + nextState);
		translateToState(nextState);
	}
	
	/**
	 * 状态迁移
	 * @param nextState
	 */
	protected void translateToState(int nextState) {
		ZLog.e("Diary[" + info.diaryuuid + "] " + "translate state=" + getState() + " to nextState=" + nextState);
		
		int tmpState = getState();
		info.state = nextState;
		
		// 如果即将完成或被移除，解锁
		if(nextState == STATE_COMPELETED || nextState == STATE_REMOVED) {
			Log.d("==WR==", "即将完成或被移除，解锁!");
			threadLocked = false;
		}
		
		// 状态迁移
		if (handler != null)
			handler.sendEmptyMessage(nextState);
		
		//状态值未改变，不通知状态改变
		if(tmpState == nextState) { 
			return;
		}
		
		// 微享任务回调
		if (mitaskmanagerListener != null) {
			mitaskmanagerListener.OnTaskStateChange(this, nextState);
		}
		// 普通日记任务回调
		if (taskmanagerListener != null) {
			taskmanagerListener.OnTaskStateChange(this, nextState);
		}
		
		// 为适配私信发送广播，附带状态等信息
		Intent intent = new Intent(ACTION_TASK_STATE_CHANGE);	
		intent.putExtra("taskState", nextState);
		intent.putExtra("taskErrorCode", errcode);
		intent.putExtra("diaryuuid", info.diaryuuid);
		intent.putExtra("taskUID",  this.info.userid);
		intent.putExtra("attachid", this.info.otherAttachid);
		ZApplication.getInstance().sendLocalBroadcast(intent);
		
	}

	public void setTaskManagerListener(NetworkTaskManagerListener l) {
		taskmanagerListener = l;
	}
	
	public void setMiTaskManagerListener(NetworkTaskManagerListener l) {
		mitaskmanagerListener = l;
	}

	public int getState() {
		return info.state;//state;
	}

	public String getId() {
		return taskId;
	}
	
	public String getdiaryID() {
		return info.diaryid;
	}

	public String getdiaryUUID() {
		return info.diaryuuid;
	}
	
	/**
	 * 判断网络条件是否符合上传
	 * @return
	 */
	public boolean canTaskRun() {
		boolean ret = false;
		if (ZNetworkStateDetector.isAvailable()
				&& ZNetworkStateDetector.isConnected()) {
			String currentUID = ActiveAccount.getInstance(MainApplication.getAppInstance()).getLookLookID();
			if(currentUID != null && !currentUID.equals("")) {
				if(this.info.isPriority || this.info.isFromShare/*NetworkTaskManager.getInstance(currentUID).getActive()*/
						|| this.info.miShareSettings == 0 || this.info.isActive) {
					Log.d("==WR==", "主动启动任务，忽略网络设置！");
					return true;
				}
			} else {
				Log.d("==WR==", "uid = " + currentUID + ";不应启动任务!");
				return false;
			}
			// setting params
			LoginSettingManager lsm = AccountInfo.getInstance(currentUID).setmanager;

			if (lsm != null) {
				String sync = lsm.getSync_type();
				Log.d("==WR==", "设置信息 sync = " + sync);
				if (sync != null && !sync.equals("")) {
					// wifi网络
					if (sync.equals(SettingFragment.SYNC_TYPE_WIFI)) {
						if(ZNetworkStateDetector.isWifi()) {
							ret = true;
						}
						// any
					} else if (sync.equals(SettingFragment.SYNC_TYPE_ANY)) {
						ret = true;
					}
				}
			} else {
				Log.d("==WR==", "LoginSettingManager is null");
			}
		}
		return ret;
	}
	
}
