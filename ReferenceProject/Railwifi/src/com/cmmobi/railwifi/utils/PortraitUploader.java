package com.cmmobi.railwifi.utils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.cmmobi.railwifi.MainApplication;
import com.cmmobi.railwifi.dao.DaoMaster;
import com.cmmobi.railwifi.dao.DaoMaster.DevOpenHelper;
import com.cmmobi.railwifi.dao.DaoSession;
import com.cmmobi.railwifi.dao.uploadPortraitDao;
import com.cmmobi.tcpfileupload.tcpFileUpload;
import com.cmmobi.tcpfileupload.tcpFileUpload.OnUploadlistener;
import com.cmmobi.tcpfileupload.tcpFileUpload.UploadInfo;

public class PortraitUploader implements OnUploadlistener {
	private final String TAG = "PortraitUploader";
	public static PortraitUploader ins = null;
	private String filePath;// 上传文件本地路径
	private String mnetname = "";// 上传文件服务器路径供断点续传用。
	private String hostaddr = "test1s.mishare.cn";// 服务器地址
	private int port = 7888;// 端口
	private int appKey = 7;
	private int retryTime = 0;
	tcpFileUpload tcpUpload;
	
	private uploadPortraitDao uploadPortraitDao;
	
	private PortraitUploader() {
		tcpUpload = new tcpFileUpload(hostaddr, port, appKey);
		
		tcpUpload.setUploadlistener(this);
		
		DevOpenHelper helper = new DaoMaster.DevOpenHelper(MainApplication.getAppInstance(), "railwifidb", null);
		SQLiteDatabase db = helper.getWritableDatabase();
		DaoMaster daoMaster = new DaoMaster(db);
		DaoSession daoSession = daoMaster.newSession();
        uploadPortraitDao = daoSession.getUploadPortraitDao();
	}
	
	public static PortraitUploader getInstance() {
		if (ins == null) {
			ins = new PortraitUploader();
		}
		return ins;
	}

	@Override
	public void onUpload(tcpFileUpload upload, UploadInfo info) {
		// TODO Auto-generated method stub
		int bend = 0;
		switch (info.state) {
		case tcpFileUpload.state_started://请求成功，正在上传数据
			mnetname = info.netname;
			Log.d(TAG,"请求成功，正在上传数据");
			break;
		case tcpFileUpload.state_connected://已经成功建立链接
			Log.d(TAG,"已经成功建立链接");
			break;
		case tcpFileUpload.state_connecterr://链接服务器失败，请检查网络
			Log.d(TAG,"链接服务器失败，请检查网络");
			break;
		case tcpFileUpload.state_connectouttime://链接超时
			while(retryTime < 5) {
				tcpUpload.reUpload();
				retryTime++;
			}
			Log.d(TAG,"链接超时");
			break;
		case tcpFileUpload.state_disconnect://链接断开
			Log.d(TAG,"链接断开");
			break;
		case tcpFileUpload.state_finish://整个任务结束
			mnetname = null;
			Log.d(TAG,"整个任务结束");
			break;
		case tcpFileUpload.state_stop://任务成功停止
			retryTime = 0;
			Log.d(TAG,"任务成功停止");
			break;
		case tcpFileUpload.state_uploaded://文件上传完成
			mnetname = null;
			Log.d(TAG,"文件上传完成");
			break;
		case tcpFileUpload.state_uploaderr://上传文件时出错
			while(retryTime < 5) {
				tcpUpload.reUpload();
				retryTime++;
			}
			Log.d(TAG,"上传文件时出错");
			break;
		}
		
		
//		((TextView)findViewById(R.id.textsizeid)).setText(info.done/1024 + "k/" + info.total/1024 + "k");
//		((TextView)findViewById(R.id.textspeedid)).setText((float)(Math.round(info.speed*100))/100 + "KB/s");
	}
	
	public void startUpload(String filePath,String netName) {
		retryTime = 0;
		tcpUpload.Stop();
		uploadPortraitDao.deleteAll();
		this.filePath = filePath;
		this.mnetname = netName;
		tcpUpload.Upload(filePath, filePath, -1, 1, 0);
		
	}
}
