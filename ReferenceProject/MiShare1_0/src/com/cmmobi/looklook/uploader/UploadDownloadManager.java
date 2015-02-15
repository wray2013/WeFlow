package com.cmmobi.looklook.uploader;

import java.util.List;

import com.cmmobi.looklook.downloader.DownloadTask;
import com.cmmobi.looklook.info.profile.DownloadTaskInfo;
import com.cmmobi.looklook.info.profile.UploadTaskInfo;

public final class UploadDownloadManager implements OnTaskDoneListener {
	
	private static UploadDownloadManager manager;
	
	
	private UploadDownloadManager() {
	}
	
	public static UploadDownloadManager getInstance() {
		if (manager == null) {
			manager = new UploadDownloadManager();
		}
		return manager;
	}
	
	
	public List<UploadTask> getUploadTaskList() {
		List<UploadTask> uploadTaskList = UploadTaskInfo.getInstance().getTaskList();
		for (UploadTask task : uploadTaskList) {
			task.listener = this;
		}
		return uploadTaskList;
	}
	
	public List<DownloadTask> getDownloadTaskList() {
		List<DownloadTask> downloadTaskList = DownloadTaskInfo.getInstance().getTaskList();
		for (DownloadTask task : downloadTaskList) {
			task.listener = this;
		}
		return downloadTaskList;
	}
	
	
	public void persistUploadTask() {
		UploadTaskInfo.getInstance().persist();
	}
	
	public void persistDownloadTask() {
		DownloadTaskInfo.getInstance().persist();
	}
	
	
	public boolean hasUploadTask() {
		return getUploadTaskList().size() > 0;
	}
	
	public boolean hasDownloadTask() {
		return getDownloadTaskList().size() > 0;
	}
	
	/**
	 * 添加任务 (如果重复添加已存在的任务, 此任务将被放在第一个位置);
	 * @param task
	 */
	public void addUploadTask(UploadTask task) {
		task.listener = this;
		UploadTaskInfo.getInstance().addTask(task);
	}
	
	public void addDownloadTask(DownloadTask task) {
		task.listener = this;
		DownloadTaskInfo.getInstance().addTask(task);
	}
	
	
	public void removeUploadTask(UploadTask task) {
		UploadTaskInfo.getInstance().deleteTask(task);
	}
	
	public void removeDownloadTask(DownloadTask task) {
		DownloadTaskInfo.getInstance().deleteTask(task);
	}

	
	public void startUploadTask(UploadTask task) {
		boolean hasUploadingTask = false;
		
		List<UploadTask> list = getUploadTaskList();
		for (UploadTask tempTask : list) {
			if (tempTask.uploader.isUploading()) {
				hasUploadingTask = true;
				break;
			}
		}
		
		if (hasUploadingTask) {
			task.uploader.setStateToQueueing();
		} else {
			task.uploader.start();
		}
	}
	
	public void startDownloadTask(DownloadTask task) {
		boolean hasDownloadingTask = false;
		
		List<DownloadTask> list = getDownloadTaskList();
		for (DownloadTask tempTask : list) {
			if (tempTask.downloader.isDownloading()) {
				hasDownloadingTask = true;
				break;
			}
		}
		
		if (hasDownloadingTask) {
			task.downloader.setStateToQueueing();
		} else {
			task.downloader.start();
		}
	}
	
	
	public void startNextUploadTask() {
		List<UploadTask> list = getUploadTaskList();
		for (UploadTask tempTask : list) {
			if (tempTask.uploader.isQueueing()) {
				startUploadTask(tempTask);
				break;
			}
		}
	}
	
	public void startNextDownloadTask() {
		List<DownloadTask> list = getDownloadTaskList();
		for (DownloadTask tempTask : list) {
			if (tempTask.downloader.isQueueing()) {
				startDownloadTask(tempTask);
				break;
			}
		}
	}

	
	public void stopUploadTask(UploadTask task) {
		task.uploader.stop();
	}
	
	public void stopDownloadTask(DownloadTask task) {
		task.downloader.stop();
	}
	
	
	public void stopAll() {
		List<UploadTask> list = UploadTaskInfo.getInstance().getTaskList();
		for (UploadTask task : list) {
			stopUploadTask(task);
		}
		
		List<DownloadTask> list2 = DownloadTaskInfo.getInstance().getTaskList();
		for (DownloadTask task : list2) {
			stopDownloadTask(task);
		}
	}

	
	@Override
	public void OnUploadTaskDone() {
		startNextUploadTask();
	}

	@Override
	public void OnDownloadTaskDone() {
		startNextDownloadTask();
	}
	
	

}
