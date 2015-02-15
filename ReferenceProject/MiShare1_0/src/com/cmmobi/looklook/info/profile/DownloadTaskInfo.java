package com.cmmobi.looklook.info.profile;

import java.util.ArrayList;
import java.util.List;

import cn.zipper.framwork.core.ZApplication;

import com.cmmobi.looklook.common.storage.StorageManager;
import com.cmmobi.looklook.downloader.DownloadTask;

public final class DownloadTaskInfo {

	private static transient final String DownloadTaskKey = "DownloadTaskKey_LookLook"; // 需要确保全局唯一性;
	private static transient DownloadTaskInfo instance;

	private ArrayList<DownloadTask> tasks;

	private DownloadTaskInfo() {
	}

	public static DownloadTaskInfo getInstance() {
		if (instance == null) {
			instance = (DownloadTaskInfo) StorageManager.getInstance().getItem(getKey(),
					DownloadTaskInfo.class);
			if (instance == null) {
				instance = new DownloadTaskInfo();
				instance.tasks = new ArrayList<DownloadTask>();
			}
			for (DownloadTask task : instance.tasks) {
				task.percent.setMaxValue(task.totalLength);
				task.percent.setCurrentValue(task.downloadLength, null);
				task.downloader.state = task.state;
			}
		}
		return instance;
	}

	public List<DownloadTask> getTaskList() {
		return tasks;
	}

	/**
	 * 添加/更新DownloadTask, 并更新持久化数据;
	 * @param task
	 */
	public void addTask(DownloadTask task) {
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
	 * 删除指定的DownloadTask, 并更新持久化数据;
	 * @param task
	 */
	public void deleteTask(DownloadTask task) {
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
	 * 持久化DownloadTaskInfo对象;
	 */
	public void persist() {
		StorageManager.getInstance().putItem(
				getKey(), instance, DownloadTaskInfo.class);
	}

	/**
	 * 获取DownloadTask在list中的位置;
	 * @param task
	 * @return
	 */
	private int getIndex(DownloadTask task) {
		int index = -1;

		for (int i = 0; i < tasks.size(); i++) {
			DownloadTask temp = tasks.get(i);
			if (temp.fileName.equalsIgnoreCase(task.fileName)) {
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
				+ "_" + DownloadTaskKey;
		return key;
	}

}
