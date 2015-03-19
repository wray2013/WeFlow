package com.etoc.weflow.download;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.URL;

import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;

import android.util.Log;

import com.etoc.weflow.WeFlowApplication;
import com.etoc.weflow.dao.DownloadHistory;
import com.etoc.weflow.utils.ConStant;
import com.etoc.weflow.utils.NetworkTypeUtility;
import com.etoc.weflow.utils.SpaceUtils;
import com.nostra13.universalimageloader.utils.StorageUtils;


public class DownloadItem implements Runnable {
    public final static int TIME_OUT = 30000;
    private final static int BUFFER_SIZE = 1024 * 8;
	private static final String TAG = "DownloadItem";

	private WeFlowApplication context;

	private File downloadDir;
	
//	public long seqNo;        //序号，递增,id
	
	public DownloadType downloadType; //电影、音乐、app
	public DownloadStatus downloadStatus; //下载的各种阶段状态
	public int downloadSize; //已下载字节数
	public int wholeSize;    //实际大小
	
//	public long startTs; //任务开始时间戳
//	public long endTs; //任务完成时间戳
	
	public String url;    //下载url
	public String path;    //下载路径（绝对路径）
	
	public String source;
	public String data;
	public String mediaId;

	public String title;    //title
	public String detail;    //detail
	public String picUrl;    //封面图片url
	public int speedBps;

	private AndroidHttpClient client;
    private HttpGet httpGet;
    private HttpResponse response;

	private int totalSize;
	private File downloadFile;
	
	private RandomAccessFile outputStream;
	public boolean cancel = false;
	private boolean isUserPause = true;
	private boolean is3GPermis = false;
//	public long seqNo;

	

	public DownloadItem(){
		cancel = false;
		context = WeFlowApplication.getAppInstance();
		downloadDir = StorageUtils.getOwnCacheDirectory(context, ConStant.getDownloadCachePath());
		if(!downloadDir.exists()){
			downloadDir.mkdirs();
		}
	}

	public DownloadItem(String mediaid, String title,String url, String picUrl, String descrp,DownloadType type, String source, String data) {
		this();
		this.isUserPause = false;
		this.mediaId = mediaid;
		this.title = title;
		this.url = url;
		this.picUrl = picUrl;
		this.detail = descrp;
		this.downloadType = type;
		this.source = source;
		this.data = data;
	}

	public DownloadItem(DownloadHistory item) {
		// TODO Auto-generated constructor stub
		this();
		
//		seqNo = item.getSeqNo();
		
		switch(item.getDownloadType()){
		case 1:
			downloadType = DownloadType.MOVIE;
			break;
		case 2:
			downloadType = DownloadType.MUSIC;
			break;
		case 3:
			downloadType = DownloadType.BOOK;
			break;
		case 4:
			downloadType = DownloadType.APP;
			break;
		default:
			downloadType = DownloadType.APP;
			break;
		}
		
		//WAIT=1,PERPARE=2,RUN=3,PAUSE=4,DONE=5,FAIL=6
		switch(item.getDownloadStatus()){
		case 1:
			downloadStatus = DownloadStatus.WAIT;
			break;
		case 2:
			downloadStatus = DownloadStatus.PERPARE;
			break;
		case 3:
			downloadStatus = DownloadStatus.RUN;
			break;
		case 4:
			downloadStatus = DownloadStatus.PAUSE;
			break;
		case 5:
			downloadStatus = DownloadStatus.DONE;
			break;
		case 6:
			downloadStatus = DownloadStatus.FAIL;
			break;
		
		}
		
		downloadSize = item.getDownloadSize();
		wholeSize = item.getWholeSize();
		path = item.getPath();
		
		title = item.getTitle();
		detail = item.getDetail();
		
		url = item.getUrl();
		picUrl = item.getPicUrl();
		source = item.getSource();
		mediaId = item.getMediaId();
		
		data = item.getData();

	}
	

	
	
	/**
	 * 
	 * [条件检查]
	 * 1）check network
	 * 2）磁盘空间检查，剩余空间至少wholeSize-realSize，若wholeSize为0则不检查
	 * 
	 * 1. WAIT阶段
	 * 默认进入run就设置为PERPARE
	 * 
	 * 2. PERPARE阶段
	 * 1）文件续传检查，得到还需传输多少字节。如果wholeSize等于实际大小则认为DONE
	 * 2) [条件检查], 若不通过则进入PAUSE
	 * 3）初始化httpclient
	 * 4）检查http code是否200，如果遇到资源找不到，没有权限等，则变成FAIL
	 * 5）进入RUN状态，检查长度是否和wholeSize一致，若不一致从头下载
	 * 
	 * 
	 * 3. RUN阶段
	 * 1） 若出现异常，进入PERPARE
	 * 2） 若没有异常下载完毕，且长度一致，进入DONE
	 * 3） 若长度不一致 ，打印并进入DONE*
	 * 
	 * 4. PAUSE阶段，在一个静态变量上wait住
	 * 1）外部唤醒，网络改变，或者重新
	 * 
	 * */
	@Override
	public void run() {
		// TODO Auto-generated method stub
		downloadStatus=DownloadStatus.WAIT; 
		Log.d(TAG, "DownloadItem.java run in");
		
		while(true){
			boolean flag = false;
			
			if(downloadStatus==DownloadStatus.WAIT){
				Log.d(TAG,"DownloadItem run Wait in");
				downloadStatus = EnterWait();
			}else if(downloadStatus == DownloadStatus.PERPARE){
				downloadStatus = EnterPerpare();
			}else if(downloadStatus == DownloadStatus.RUN){
				downloadStatus = EnterRun();
			}else if(downloadStatus == DownloadStatus.PAUSE){
				downloadStatus = EnterPause();
			} 
			
			if(downloadStatus == DownloadStatus.DONE){
				flag = true;
			}else if(downloadStatus == DownloadStatus.FAIL){
				flag = true;
			} else if (downloadStatus == DownloadStatus.USERPAUSE) {
				flag = true;
			}
			
			//用户中途删除任务，清除下载文件
			if(cancel){
				downloadStatus = DownloadStatus.FAIL;
				if(downloadFile!=null && downloadFile.exists()){
					downloadFile.delete();
				}else if(path!=null){
					File f = new File(path);
					if(f.isFile() && f.exists()){
						f.delete();
					}
				}
			}
			
			DownloadManager.getInstance().notifyStatusChanged(this, downloadStatus);
			
			if(flag || cancel){
				break;
			}
			
			
		}
		

	}
	
	public int copy(InputStream input, RandomAccessFile out)
			throws IOException {

		if (input == null || out == null) {
			client.close(); // must close client first
			client = null;
			out.close();
			input.close();
			return -1;
		}

		byte[] buffer = new byte[BUFFER_SIZE];

		BufferedInputStream in = new BufferedInputStream(input, BUFFER_SIZE);

		int count = 0, n = 0;
		downloadSize = (int) out.length();

		try {

			out.seek(out.length());
			
			long startTs = System.currentTimeMillis();
			long nowTs = startTs;
			int readNum = 0;

			while (true) {
				if(cancel || isUserPause){
					break;
				}
				
				n = in.read(buffer, 0, BUFFER_SIZE);
				if (n == -1) {
					break;
				}
				out.write(buffer, 0, n);
				count += n;
				
				readNum+=n;
				downloadSize += n;
				nowTs = System.currentTimeMillis();
				if(nowTs-startTs>1000){
					speedBps = (int) ((long)readNum * 1000 / (nowTs-startTs));
					
					
					readNum = 0;
					startTs = nowTs;
					
					DownloadManager.getInstance().notifyProgressChanged(this);
				}

			}
		} finally {
			client.close(); // must close client first
			client = null;
			out.close();
			in.close();
			input.close();
		}
		return count;

	}
	
	private void Sleep(long timeMs){
		try {
			Thread.sleep(timeMs);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private boolean checkNetWork(){
		return NetworkTypeUtility.isWifi(WeFlowApplication.getAppInstance()) || is3GPermis;
	}
	
	private boolean checkStorage(){
		return SpaceUtils.getAvailSpaceOfSDCard() - (wholeSize-downloadSize)>10*1024*1024;
	}
	
	private DownloadStatus EnterWait(){
		Log.v(TAG, "EnterWait - " /*+ "downloadDir:" + downloadDir.getPath()*/ + ", downloadSize:" + downloadSize + ", wholeSize:" + wholeSize + ", url:" + url + ", path:" + path + ", cancel:" + cancel);
		if(cancel){
			return DownloadStatus.FAIL;
		}
		
		EnterPerpare();
		
		return DownloadStatus.PERPARE;
	}
	
	private DownloadStatus EnterPerpare(){
		Log.d(TAG, "EnterPerpare - " /*+ "downloadDir:" + downloadDir.getPath()*/ + ", downloadSize:" + downloadSize + ", wholeSize:" + wholeSize + ", url:" + url + ", path:" + path + ", cancel:" + cancel);
		if(cancel){
			return DownloadStatus.FAIL;
		}
		
		if(wholeSize==downloadSize){
			return DownloadStatus.DONE;
		}
		
		int badTry = 3;
		while (badTry > 0) {
			
			if (isUserPause) {
				Log.d(TAG,"EnterPerpare isUserPause true");
				DownloadStatus r = DownloadStatus.USERPAUSE;
				r.setReason(DownloadStatus.REASON_USER_PAUSE);
				return r;
			} else if (!checkNetWork()) {
				Log.d(TAG,"EnterPrepare checkNetWork in");
				DownloadStatus r = DownloadStatus.PAUSE;
				r.setReason(DownloadStatus.REASON_NETWORK_NO_WIFI);
				return r;
			}else if(!checkStorage()){
				DownloadStatus r = DownloadStatus.PAUSE;
				r.setReason(DownloadStatus.REASON_STORAGE_NO_SPACE);
				return r;
			} else {

				try {
					client = AndroidHttpClient.newInstance("DownloadTask");
					httpGet = new HttpGet(url);
					response = client.execute(httpGet);
					StatusLine sl = response.getStatusLine();
					if (sl != null && sl.getStatusCode() > 399) {
						return DownloadStatus.FAIL;
					}
					
					totalSize = (int) response.getEntity().getContentLength();
					
					if(downloadFile==null){
						if(path!=null && !path.equals("")){
							downloadFile = new File(path);
						}else{
					        String fileName = new File(new URL(url).getFile()).getName();
					        downloadFile = new File(downloadDir, fileName);
					        path = downloadFile.getAbsolutePath();
					        if(downloadFile.exists()){
					        	downloadSize = (int) downloadFile.length();
					        }
						}
					}

					Log.d(TAG,"EnterPrepare totalSize = " + totalSize + " wholeSize = " + wholeSize);
					if (totalSize <= 0) {
						return DownloadStatus.WAIT;
					} else if(wholeSize<=0){
						wholeSize = totalSize;
					}else if(wholeSize!=totalSize && totalSize > 0){
						downloadFile.delete(); //重新下载
						wholeSize = totalSize;
						downloadSize  = 0;
					}else if(downloadSize == totalSize){
						return DownloadStatus.DONE;
					}
					
				} catch (ClientProtocolException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					return DownloadStatus.FAIL;
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					badTry--;
					Sleep(2000);
					continue;
				}finally{
					client.close();
				}

				return DownloadStatus.RUN;
			}

		}
		
		return DownloadStatus.PAUSE;
	}
	
	 /**
	  * 3. RUN阶段
	 * 1） 若出现异常，进入PERPARE
	 * 2） 若没有异常下载完毕，且长度一致，进入DONE
	 * 3） 若长度不一致 ，打印并进入DONE*
	 * 
	 * 4. PAUSE阶段，在一个静态变量上wait住
	 * 1）外部唤醒，网络改变，或者重新
	 * */
	private DownloadStatus EnterRun(){
		Log.d(TAG, "EnterRun - " /*+ "downloadDir:" + downloadDir.getPath()*/ + ", downloadSize:" + downloadSize + ", wholeSize:" + wholeSize + ", url:" + url + ", path:" + path + ", cancel:" + cancel);
		if(cancel){
			return DownloadStatus.FAIL;
		}
		
		if(downloadFile==null){
			return DownloadStatus.PERPARE;
		}else{
			if(downloadFile.exists()){
				httpGet.addHeader("Range", "bytes=" + downloadFile.length() + "-");
			}
			
			try {
				
				
	            client = AndroidHttpClient.newInstance("DownloadTask");
				response = client.execute(httpGet);
				
				outputStream = new RandomAccessFile(downloadFile, "rw");
				InputStream input = response.getEntity().getContent();
		        int bytesCopied = copy(input, outputStream);
		        
		        if(bytesCopied<0){
		        	return DownloadStatus.PERPARE;
		        }else if(downloadSize==wholeSize){
		        	/*downloadSize = wholeSize;*/
		        	return DownloadStatus.DONE;
		        }else{
		        	return DownloadStatus.PERPARE;
		        }
		        
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return DownloadStatus.FAIL;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return DownloadStatus.PERPARE;
			} 
		}
		
	}
	
	/**
	 * 
	 * 4. PAUSE阶段，在一个静态变量上wait住
	 * 1）外部唤醒，网络改变，或者重新
	 * 
	 * */
	private DownloadStatus EnterPause(){
		Log.d(TAG, "EnterPause - " /*+ "downloadDir:" + downloadDir.getPath()*/ + ", downloadSize:" + downloadSize + ", wholeSize:" + wholeSize + ", url:" + url + ", path:" + path + ", cancel:" + cancel);
		if(cancel){
			return DownloadStatus.FAIL;
		}
		
		synchronized (DownloadManager.globeLock) {
			try {
				DownloadManager.globeLock.wait(30000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		return DownloadStatus.PERPARE;
	}
	
	public void pause() {
		is3GPermis = false;
		isUserPause = true;
	}
	
	public void resume() {
		isUserPause = false;
		downloadStatus = DownloadStatus.USERRESUME;
		DownloadManager.getInstance().notifyStatusChanged(this, downloadStatus);
	}
	
	public boolean isPaused() {
		return isUserPause;
	}
	
	public void permis3G() {
		is3GPermis = true;
	}

}
