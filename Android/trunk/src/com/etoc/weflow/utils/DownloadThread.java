package com.etoc.weflow.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import com.etoc.weflow.Config;
import com.etoc.weflow.service.PushService;


import android.app.ProgressDialog;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;

public class DownloadThread extends Thread {

	private Handler handler;
	private String url;
	private ProgressDialog pDialog;
	private boolean mStop;
	
	public DownloadThread(Handler handler, ProgressDialog pd) {
		this.handler = handler;
		this.pDialog = pd;
		this.mStop = false;
	}
	
	public void excute(String url) {
		this.url = url;
		this.start();
	}
	
	public void stopThread() {
		this.mStop = true;
	}
	
	public boolean isStop() {
		return mStop;
	}
	
	@Override
	public void run() {
        HttpClient client = new DefaultHttpClient();
        HttpGet get = new HttpGet(url);
        HttpResponse response;
        try {
            response = client.execute(get);
            HttpEntity entity = response.getEntity();
            long length = entity.getContentLength();
            InputStream is =  entity.getContent();
            FileOutputStream fileOutputStream = null;
            if(is != null){
            	File fileDir = new File(Config.DOWNLOAD_FOLDER);
                if(!fileDir.exists()) {
                	fileDir.mkdirs();
                }
                File file = new File(Config.DOWNLOAD_FOLDER, PushService.UPDATE_SERVERAPK);
                if(file.exists()) {
                	file.delete();
                }
                file.createNewFile();
                
                fileOutputStream = new FileOutputStream(file);
                byte[] b = new byte[1024];
                int charb = -1;
                int count = 0;
                while((charb = is.read(b))!=-1){
                	if(mStop) {
                		break;
                	}
                    fileOutputStream.write(b, 0, charb);
                    count += charb;
                    pDialog.setProgress((int) (count * 100 / length));
                }
            }
            fileOutputStream.flush();
            if(fileOutputStream!=null){
                fileOutputStream.close();
            }
            if(!mStop) {
            	Message message = handler.obtainMessage(PushService.APK_DOWNLOADED_MSG);
                handler.sendMessage(message);
            }
        }  catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        mStop = true;
	}
}
