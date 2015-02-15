package com.cmmobi.looklook.info.profile;

import java.util.ArrayList;
import java.util.List;

import cn.zipper.framwork.core.ZApplication;

import com.cmmobi.looklook.common.storage.StorageManager;
import com.cmmobi.looklook.uploader.UploadTask;

public final class UploadTaskInfo {

	private static transient final String UploadTaskKey = "UploadTaskKey_LookLook"; // 需要确保全局唯一性;
	private static transient UploadTaskInfo instance;

	private List<UploadTask> tasks;

	private UploadTaskInfo() {
	}

	public static UploadTaskInfo getInstance() {
		if (instance == null) {
			instance = (UploadTaskInfo) StorageManager.getInstance().getItem(getKey(),
					UploadTaskInfo.class);
			if (instance == null) {
				instance = new UploadTaskInfo();
				instance.tasks = new ArrayList<UploadTask>();
			}
			for (UploadTask task : instance.tasks) {
				task.percent.setMaxValue(task.totalLength);
				task.percent.setCurrentValue(task.uploadedLength, null);
				task.uploader.state = task.state;
			}
		}
		return instance;
	}

	public List<UploadTask> getTaskList() {
		return tasks;
	}

	/**
	 * 添加/更新UploadTask, 并更新持久化数据;
	 * @param task
	 */
	public void addTask(UploadTask task) {
		int index = getIndex(task);
		if (index > -1) {
			tasks.remove(index);
		} else {
			index = 0;
		}
		tasks.add(index, task);
		persist();
	}

	/**
	 * 删除指定的UploadTask, 并更新持久化数据;
	 * @param task
	 */
	public void deleteTask(UploadTask task) {
		int index = getIndex(task);
		if (index > -1) {
			tasks.remove(index);
			persist();
		}
	}

	/**
	 * 清空所有task;
	 */
	public void deleteAllTask() {
		tasks.clear();
		persist();
	}

	/**
	 * 持久化UploadTaskInfo对象;
	 */
	public void persist() {
		StorageManager.getInstance().putItem(
				getKey(), instance, UploadTaskInfo.class);
	}

	/**
	 * 获取UploadTask在list中的位置;
	 * @param task
	 * @return
	 */
	private int getIndex(UploadTask task) {
		int index = -1;

		for (int i = 0; i < tasks.size(); i++) {
			UploadTask temp = tasks.get(i);
			if (temp.uploadFileName.equalsIgnoreCase(task.uploadFileName)) {
				index = i;
				break;
			}
		}
		return index;
	}

	/**
	 * 获取与账户关联的key;
	 * @return
	 */
	private static String getKey() {
		String key = ActiveAccount.getInstance(ZApplication.getInstance()).getUID()
				+ "_" + UploadTaskKey;
		return key;
	}

}
