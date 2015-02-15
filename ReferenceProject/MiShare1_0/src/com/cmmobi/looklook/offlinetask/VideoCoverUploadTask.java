package com.cmmobi.looklook.offlinetask;

import java.io.File;

import com.cmmobi.looklook.common.gson.VideoCoverUploader;
import com.cmmobi.looklook.offlinetask.VideoCoverUploaderThread.VideoCoverUploadListener;

import android.util.Log;

public class VideoCoverUploadTask extends IOfflineTask implements VideoCoverUploadListener{

	private static final String TAG = "VideoCoverUploadTask";
	
	private VideoCoverUploaderInfo uploadThread;
	
	@Override
	public void start() {
		// TODO Auto-generated method stub
		if (request instanceof VideoCoverUploaderInfo) {
			VideoCoverUploaderInfo upload = new VideoCoverUploaderInfo();
			upload.filetype = "4";
			upload.mFilePath = ((VideoCoverUploaderInfo) request).mFilePath;
			upload.rotation = 0;
			upload.diaryid = ((VideoCoverUploaderInfo) request).diaryid;
			upload.attachmentid = ((VideoCoverUploaderInfo) request).attachmentid;
			upload.businesstype = "6";
			VideoCoverUploaderThread videoCoverUploader = new VideoCoverUploaderThread(upload);
			videoCoverUploader.setUploadListener(this);
			videoCoverUploader.excute();
		}
	}
	
	@Override
	public void connectException(VideoCoverUploaderThread uploadThread) {
		uploadThread.closeSocket();
		if(listener!=null)listener.failed(this);
	}

	@Override
	public void sendException(VideoCoverUploaderThread uploadThread) {
		uploadThread.closeSocket();
		if(listener!=null)listener.failed(this);
	}

	@Override
	public void uploadComplete(VideoCoverUploaderThread uploadThread) {
		uploadThread.closeSocket();
		successResult = uploadThread.picUrl;
		if(listener!=null)listener.success(this);
	}

}
