package com.cmmobi.looklook.offlinetask;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;

import com.cmmobi.looklook.common.utils.Base64Utils;
import com.cmmobi.looklook.info.profile.ActiveAccount;

import android.util.Log;

import cn.zipper.framwork.core.ZApplication;
import cn.zipper.framwork.core.ZLog;
import cn.zipper.framwork.io.file.ZFileReader;
import cn.zipper.framwork.utils.ZPercent;
import cn.zipper.framwork.utils.ZPercent.OnPercentChangedListener;

/**
 * @author wuxiang
 * @email wuxiang@cmmobi.com
 * @date  2013-8-26
 */
public class UploadThread extends Thread {

	private static final String TAG="UploadThread";
	private FileUploadInfo uploadInfo;
	private Socket socket;
	private DataOutputStream dataOutputStream;
	private DataInputStream dataInputStream;
	private HeaderInfo headerInfo;
	/**
	 * 是否未多文件上传 true-多文件上传，false-单文件上传
	 */
	private boolean isMulitFiles;
//	public UploadThread(FileUploadInfo uploadInfo,HeaderInfo headerInfo,boolean isMulitFiles){
//		this.uploadInfo=uploadInfo;
//		this.isMulitFiles=isMulitFiles;
//		this.headerInfo=headerInfo;
//	}
	
	public void setInfo(FileUploadInfo uploadInfo,HeaderInfo headerInfo,boolean isMulitFiles){
		this.uploadInfo=uploadInfo;
		this.isMulitFiles=isMulitFiles;
		this.headerInfo=headerInfo;
	}

	@Override
	public void run() {
		//建立socket连接
		initSocket();
		//发送数据
		sendData();
		super.run();
	}
	
	//初始化socket
	private void initSocket(){
		try {
			socket=new Socket();
			socket.setSoTimeout(10 * 1000);
			socket.connect(new InetSocketAddress(uploadInfo.ip, uploadInfo.port),
					10 * 1000);
			dataOutputStream = new DataOutputStream(
					socket.getOutputStream());
			dataInputStream = new DataInputStream(socket.getInputStream());
		} catch (Exception e) {
			e.printStackTrace();
			if(uploadListener!=null)uploadListener.connectException(this);
		}
	}
	
	public void close(){
		try {
			if (socket != null) {
				socket.close();
				socket = null;
			}
			if (dataInputStream != null) {
				dataInputStream.close();
				dataInputStream = null;
			}
			if (dataOutputStream != null) {
				dataOutputStream.close();
				dataOutputStream = null;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	//发送头信息
	private void sendData(){
		try {
			if(dataOutputStream!=null){
				if(null==headerInfo){
					Log.e(TAG, "headerInfo is null");
					return;
				}
				String header=headerInfo.getHeader();
				dataOutputStream.write(header.getBytes());
				String line = dataInputStream.readLine() + ";";
				Log.d(TAG, "sendHeader->line is "+line);
				if(line.equals("null;")) {
					Log.e(TAG, "sendHeader->line is null");
					if(uploadListener!=null)uploadListener.sendException(this);
					return;
				}
				String ip = getStringValue(line, "ip");
				int port = getIntValue(line, "port");
				int dataover = getIntValue(line, "dataover");
				if (dataover == 0) {
					Log.e(TAG, "头信息参数错误");
					return;
				} else if(dataover == 4) { 
					Log.e(TAG, "文件传输错误，重试！");
					if(uploadListener!=null)uploadListener.sendException(this);
					return;
				}else if (dataover == 2) {//文件上传完成
					Log.d(TAG, "file uploaded");
					if(uploadListener!=null)uploadListener.uploadComplete(this);
					return;
				} else if (1==dataover) {
					long position = Long.valueOf(getStringValue(line, "position"));
					//从指定位置开始写入文件数据
					if(writeDataByPosition(position)){//全部写入完成
						String line2 = dataInputStream.readLine() + ";";
						dataover = getIntValue(line2, "dataover");
						ZLog.e("<< Socket Write-Finish : " + line2);
						if(2==dataover){
							if(isMulitFiles){//多文件上传
								//TODO 多文件上传
							}else{//单文件上传
								//通知服务器文件上传完成
								dataOutputStream.write(headerInfo.getCompleteNofity().getBytes());
								String line3 = dataInputStream.readLine() + ";";
								dataover = getIntValue(line3, "dataover");
								Log.d(TAG, "line3="+line3);
								if(3==dataover){
									Log.d(TAG, "upload complete");
									if(uploadListener!=null)uploadListener.uploadComplete(this);
								}else{
									Log.d(TAG, "upload excetpion");
									if(uploadListener!=null)uploadListener.sendException(this);
								}
							}
						}
					}else{//写入失败
						Log.d(TAG, "write data excetpion");
						if(uploadListener!=null)uploadListener.sendException(this);
					}
				} 
			}
		} catch (IOException e) {
			e.printStackTrace();
			if(uploadListener!=null)uploadListener.sendException(this);
		}
	}
	
	//从指定位置写入数据
	private boolean writeDataByPosition(long position){
		FileInputStream fileInputStream=null;
		try {
			File file= new File(uploadInfo.localFilePath);
			fileInputStream=new FileInputStream(file);
			fileInputStream.skip(position);
			int blockSize=1024*2;
			byte[] buffer=new byte[blockSize];
			int readLen=0;
			while((readLen=fileInputStream.read(buffer))!=-1){
				byte[] trim = new byte[readLen];
				if(readLen<blockSize){
					System.arraycopy(buffer, 0, trim, 0, readLen);
					dataOutputStream.write(trim);
				}else{
					dataOutputStream.write(buffer);
				}
			}
			fileInputStream.close();
			return true;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			Log.d(TAG, "writeDataByPosition->file not found!");
			return false;
		} catch (IOException e) {
			e.printStackTrace();
			Log.d(TAG, "writeDataByPosition->IOException!");
			return false;
		}
	}
	
	private UploadListener uploadListener;
	public void setUploadListener(UploadListener uploadListener){
		this.uploadListener=uploadListener;
	}
	
	public interface UploadListener{
		void connectException(UploadThread uploadThread);
		void sendException(UploadThread uploadThread);
		void uploadComplete(UploadThread uploadThread);
	}
	
	/**
	 * 从socket响应中获取字段值;
	 * 
	 * @param response
	 * @param key
	 * @return
	 */
	private String getStringValue(String response, String key) {
		String value = null;
		int index = response.indexOf(key);
		if (index > -1) {
			value = response.substring(index + key.length() + 1,
					response.indexOf(";", index + key.length() + 1));
		}
		return value;
	}

	/**
	 * 从socket响应中获取字段值;
	 * 
	 * @param response
	 * @param key
	 * @return
	 */
	private int getIntValue(String response, String key) {
		int value = 0;
		String string = getStringValue(response, key);
		if (string != null) {
			value = Integer.valueOf(string);
		}
		return value;
	}
}
