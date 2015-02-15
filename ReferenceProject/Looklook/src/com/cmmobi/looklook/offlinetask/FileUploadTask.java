package com.cmmobi.looklook.offlinetask;

import java.io.File;
import java.util.ArrayList;

import com.cmmobi.looklook.common.constant.Constant;
import com.cmmobi.looklook.offlinetask.UploadThread.UploadListener;

import android.util.Log;

/**
 * @author wuxiang
 * @email wuxiang@cmmobi.com
 * @date 2013-8-26
 */
public class FileUploadTask extends IOfflineTask implements UploadListener {

	private static final String TAG = "FilesUploadTask";
	
	private UploadThread uploadThread;

	@Override
	public void start() {
		if (request instanceof FileUploadInfo) {
			FileUploadInfo fileUploadInfos = (FileUploadInfo)request;
			FileUploadInfo uploadInfo=fileUploadInfos;
			HeaderInfo headerInfo=createHeader(uploadInfo);
			uploadThread=new UploadThread();
			uploadThread.setInfo(uploadInfo, headerInfo, false);
			uploadThread.setUploadListener(this);
			uploadThread.start();
			/*if(1==fileUploadInfos.length){//单文件上传
			}else if(fileUploadInfos.length>1){//多文件上传
				//TODO 多文件上传处理
			}else{
				Log.e(TAG, "fileUploadInfos is empty");
				if(listener!=null)listener.failed(this);
				if (listener != null)
					listener.failed(this);
			}*/
		} else {
			Log.e(TAG, "request can not cast FileUploadInfo[]");
			if(listener!=null)listener.failed(this);
			if (listener != null)
				listener.failed(this);
		}
	}

	// 生成头信息类
	private HeaderInfo createHeader(FileUploadInfo uploadInfo){
			File file=new File(uploadInfo.localFilePath);
			if(file!=null&&file.exists()){
				//生成头信息类
				HeaderInfo headerInfo=new HeaderInfo();
				headerInfo.attachmentid=uploadInfo.id;
				if(taskType==TaskType.PRIVATE_MSG_AUDIO_UPLOAD){
					headerInfo.businesstype=3;
					headerInfo.fileType=5;
					headerInfo.diaryid=uploadInfo.diaryID;
					headerInfo.fileLength=file.length();
					headerInfo.fileName=uploadInfo.uploadPath;
					headerInfo.isencrypt=0;
					headerInfo.nuid=0;
					headerInfo.over=0;
					headerInfo.rotation=0;
					headerInfo.type=1;
				}else{
					//TODO 其他类型处理
				}
				return headerInfo;
			}else{
				Log.e(TAG, "createHeader->file not found!");
				//物理文件未找到时，移除任务
				if(listener!=null)listener.failed(this);
				return null;
			}
		}

	@Override
	public void connectException(UploadThread uploadThread) {
		uploadThread.close();
		if(listener!=null)listener.failed(this);
	}

	@Override
	public void sendException(UploadThread uploadThread) {
		uploadThread.close();
		if(listener!=null)listener.failed(this);
	}

	@Override
	public void uploadComplete(UploadThread uploadThread) {
		uploadThread.close();
		if(listener!=null)listener.success(this);
	}
}
