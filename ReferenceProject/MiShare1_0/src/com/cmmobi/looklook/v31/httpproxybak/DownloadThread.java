package com.cmmobi.looklook.v31.httpproxybak;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.util.Log;
/**
 * 下载模块，支持断点下载
 * @author Ray
 * 
 */
public class DownloadThread extends Thread {
	static private final String TAG="DownloadThread";
	private String mUrl;
	private String mPath;
	private long mDownloadSize;
	private int mTargetSize;
	private boolean mStop;
	private boolean mDownloading;
	private boolean mStarted;
	private boolean mError;
	
	public DownloadThread(String url, String savePath) {
		mUrl = url;
		mPath = savePath;
		
		//如果文件存在，则继续
		File file=new File(mPath);
		if(file.exists()){
			Log.d(TAG, "断点续传");
			mDownloadSize = file.length();
		}else{
			Log.d(TAG, "重新下载");
			mDownloadSize = 0;
		}
		
		try {
			mTargetSize = getTargetSize(url);
		} catch (Exception e) {
			Log.e(TAG, "failed to get download total size.");
			mTargetSize = 0;
			e.printStackTrace();
		}
		mStop = false;
		mDownloading = false;
		mStarted = false;
		mError=false;
	}

	@Override
	public void run() {
		mDownloading = true;
		download();
	}
	
	/** 启动下载线程 */
	public void startThread() {
		if (!mStarted) {
			this.start();

			// 只能启动一次
			mStarted = true;
		}
	}

	/** 停止下载线程*/
	public void stopThread() {
		mStop = true;
	}

	/** 是否正在下载 */
	public boolean isDownloading() {
		return mDownloading;
	}

	/**
	 * 是否下载异常
	 * @return
	 */
	public boolean isError(){
		return mError;
	}
	
	public long getDownloadedSize() {
		return mDownloadSize;
	}

	public long getTargetSize() {
		return mTargetSize;
	}
	
	/** 是否下载成功 */
	public boolean isDownloadSuccessed() {
		return (mDownloadSize != 0 && mDownloadSize >= mTargetSize);
	}

	public int getTargetSize(String mUrl) throws Exception {
		URL url = new URL(mUrl);
		HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
		return urlConnection.getContentLength();
	}
	
	private void download() {
		//下载成功则关闭
		if(isDownloadSuccessed()){
			Log.i(TAG,"...Download Successed...");
			return;
		}
		InputStream is = null;
		FileOutputStream os = null;
		if (mStop) {
			return;
		}
		try {
			URL url = new URL(mUrl);
			HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
			urlConnection.setRequestMethod("GET");
			urlConnection.setInstanceFollowRedirects(true);//允许重定向
			is = urlConnection.getInputStream();
			
			if(mDownloadSize==0 || urlConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {//全新文件
				os = new FileOutputStream(mPath);
				Log.i(TAG,"download file:"+mPath);
			}
			else if(urlConnection.getResponseCode() == HttpURLConnection.HTTP_PARTIAL) {//追加数据
				os = new FileOutputStream(mPath,true);
				Log.i(TAG,"append exists file:"+mPath);
			} else {
				Log.i(TAG,"ResponseCode"+urlConnection.getResponseCode());
			}
			int len = 0;
			byte[] bs = new byte[1024];
			if (mStop) {
				return;
			}
			while (!mStop //未强制停止
					&& mDownloadSize<mTargetSize //未下载足够
					&& ((len = is.read(bs)) != -1)) {//未全部读取
				os.write(bs, 0, len);
				mDownloadSize += len;
			}
			if (isDownloadSuccessed()) {
				Log.i(TAG,"下载成功");
				String newname = mPath.replace(".tmp", ".mp4");
				File compfile = new File(newname);
				File file = new File(mPath);
				Log.i(TAG, "...Rename to[" + newname + "]...");
				file.renameTo(compfile);
			}
		} catch (Exception e) {
			mError=true;
			Log.i(TAG,"download error:"+e.toString()+"");
			Log.i(TAG,Utils.getExceptionMessage(e));
		} finally {
			if (os != null) {
				try {
					os.close();
				} catch (IOException e) {}
			}

			if (is != null) {
				try {
					is.close();
				} catch (IOException e){}
			}
			
			mDownloading = false;
			
			//清除空文件
			File nullFile = new File(mPath);
			if(nullFile.exists() && nullFile.length()==0) {
				Log.i(TAG,"清除空文件");
				nullFile.delete();
			}
			Log.i(TAG,"mDownloadSize:"+mDownloadSize+",mTargetSize:"+mTargetSize);
		}
	}
}
