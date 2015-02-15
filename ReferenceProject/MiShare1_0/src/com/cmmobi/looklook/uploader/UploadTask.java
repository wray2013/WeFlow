package com.cmmobi.looklook.uploader;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import cn.zipper.framwork.core.ZLog;
import cn.zipper.framwork.utils.ZPercent;

public final class UploadTask {
	
	public List<String> filePaths; // 要上传到服务器的文件列表;
	
	public String name; // 本地上传任务名;
	public String uploadFileName; // 从服务器获取到的上传文件名;
	public String ip;
	public String uuid; // sourceId;
	public String type; // "1" / "n";
	
	public int state;
	public int port;
	public int uploadedFileNumber; // 已经上传过的文件编号;
	public int rotation;
	public int percentValue;
	public long totalLength;
	public long uploadedLength;
	public long uploadedFileBlock;
	
	
	public transient boolean isAllFileDone;
	public transient Uploader uploader;
	public transient ZPercent percent;
	public transient OnTaskDoneListener listener;
	
	
	public UploadTask() {
		percent = new ZPercent(null);
		uploader = new Uploader(this);
	}
	
	public void addStep(long step) {
		uploadedLength += step;
		percent.setCurrentValueStep(step, null);
		percentValue = percent.getPercentInt();
	}
	
	public void addFilePath(String path) {
		if (filePaths == null) {
			filePaths = new ArrayList<String>();
		}
		
		File file = new File(path);
		if (file.exists()) {
			filePaths.add(path);
			totalLength += file.length();
			percent.setMaxValue(totalLength);
			percent.setCurrentValue(uploadedLength, null);
			if (uploader != null && uploader.isWaiting()) {
				uploader.start();
			}
		} else {
			ZLog.alert();
			ZLog.e("File Not Exists : " + path);
		}
	}
}
