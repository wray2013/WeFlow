package com.cmmobi.looklook.downloader;

import cn.zipper.framwork.utils.ZPercent;

import com.cmmobi.looklook.uploader.OnTaskDoneListener;

public final class DownloadTask {
	
	public String fileName; // 本地文件名;
	public String filePath; // 本地文件路径(例如:/mnt/sdcard/2012-01-25.mp4);
	public String url; // 文件地址;
	public long totalLength;
	public long downloadLength;
	
	public int state;
	
	public transient Downloader downloader;
	public transient ZPercent percent;
	public transient OnTaskDoneListener listener;
	
	public DownloadTask() {
		percent = new ZPercent(null);
		downloader = new Downloader(this);
	}
	
	public void addStep(long step) {
		downloadLength += step;
		percent.setCurrentValueStep(step, null);
	}
}
