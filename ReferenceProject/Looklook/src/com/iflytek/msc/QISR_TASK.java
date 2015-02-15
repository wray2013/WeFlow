package com.iflytek.msc;

import java.util.concurrent.LinkedBlockingQueue;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.AsyncTask;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import cn.zipper.framwork.device.ZScreen;
import cn.zipper.framwork.device.ZSimCardInfo;

import com.cmmobi.looklook.MainApplication;
import com.cmmobi.looklook.common.gson.Requester2;
import com.google.gson.Gson;
import com.iflytek.msc.Result.SRResultObj;
import com.iflytek.msc.Result.WS;

public class QISR_TASK extends AsyncTask<LinkedBlockingQueue<AudioFrame>, SRResultObj, String> {
	public static final String QISR_RESULT_MSG = "MSC.QISR.MSG";
	public static final int HANDLER_QISR_RESULT_CLEAN = 0x34900384;
	public static final int HANDLER_QISR_RESULT_ADD = 0x34900385;
	public static final int HANDLER_QISR_RESULT_DONE = 0x34900386;
	
	private static final String TAG = "QISR_TASK";
	
	private int sampleRate;
	private String audioID;
	private int channelConfig;
	
	String IMEI;
	String IMSI;
	String MAC;
	String WAP_PROXY;
    String APP_PATH;
    String VERSION;
    int VERSION_CODE;
	
	LinkedBlockingQueue<AudioFrame> fifo;
	
	Gson gson;
	boolean RcvFin = false; //输入流程结束
	boolean noWrite = false; //该session不能再写，但可以尝试获取结果 
	
	boolean RegFin = false; //识别流程结束
	boolean hasResult = false; //有数据需要取
	
	boolean Abort = false; //遇到错误直接结束
	boolean exitSessionLoop = false;
	
	//
	final int STATUS_DATA = 0x8; //有输入
	final int STATUS_WRITE = 0x4; //可写audio
	final int STATUS_READ = 0x2;  //可取结果
	//final int STATUS_SESSION = 0x1; //session ok
	int status;
	MSCSessionInfo2 mscsessioninfo;
	MSCSessionInfo2 last_mscsessioninfo;
	//

	
	public QISR_TASK(int sampleRate, int channelConfig, String audioID){
		gson = new Gson();
		this.sampleRate = sampleRate;
		this.channelConfig = channelConfig;
		this.audioID = audioID;
	}
	
	@Override
    protected void onPreExecute() {
		//os.system=Android,Dump.ver=2.0.1015.1034,dvc=460006244840287|357853043378992,appid=5150f897,mac=78:D6:F0:DE:4D:5E,wap_proxy=cmnet,
		//app.ver.name=2.0.1015.1034,net.mac=78:D6:F0:DE:4D:5E,os.imsi=460006244840287,app.name=����ؼ�,Dump.skin=default,
		//os.resolution=320*533,os.imei=357853043378992,app.path=/data/data/com.iflytek.mscdemo,usr=460006244840287|357853043378992,
		//app.ver.code=1,auth=1,timeout=20000
		
		IMEI = ZSimCardInfo.getIMEI();
		IMSI  = ZSimCardInfo.getIMSI();
		MAC = ZSimCardInfo.getDeviceMac();
		WAP_PROXY = Requester2.getNetType();
		
		PackageManager manager = MainApplication.getAppInstance().getPackageManager();
		String packagename = MainApplication.getAppInstance().getPackageName();

        APP_PATH = MainApplication.getAppInstance().getFilesDir().getParent();

        int VERSION_CODE = 0;
		try {
			PackageInfo info = manager.getPackageInfo(packagename, 0);
			VERSION = info.versionName;
			VERSION_CODE = info.versionCode;
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		
        //Wrap_QISRInit
		Wrap_QISRInit();
		
		sendMessage(HANDLER_QISR_RESULT_CLEAN, audioID, null); 
    }
	
	@Override
	protected String doInBackground(LinkedBlockingQueue<AudioFrame>... params) {
		// TODO Auto-generated method stub
		fifo = params[0];
		Log.e(TAG, "doInBackground Thread id:" + Thread.currentThread().getId());
		//check vaild
		if(fifo==null){
			Log.e(TAG, "QISR_TASK - doInBackground, fifo is null");
			return audioID;
		}
		
		RcvFin = false; //输入流程结束
		RegFin = false; //识别流程结束
		Abort = false; //遇到错误直接结束
		hasResult = false; //有识别结果需要get
		noWrite = false;
		//exitSessionLoop = false;
		
		AudioFrame af = null;
		char[] sessionID = null;
		mscsessioninfo = new MSCSessionInfo2();
		last_mscsessioninfo = new MSCSessionInfo2();
		
		String session_descrp = null;
		
		StringBuilder sb = new StringBuilder();
		sb.append("sub=iat");
		sb.append(",auf=audio/L16;");
		sb.append("rate=" + sampleRate);
		sb.append(",tte=gb2312");		
		sb.append(",dvc=" + IMSI + "|" + IMEI);
		sb.append(",mac=" + MAC);
		
		sb.append(",ssm=1");

		if(Requester2.VALUE_WIFI.equals(WAP_PROXY)){
			sb.append(",wap_proxy=wifi");
		}else{
			sb.append(",wap_proxy=cmnet");
		}

		if(sampleRate==16000){
			sb.append(",ent=sms16k");
		}else{
			sb.append(",ent=sms8k");
		}

		sb.append(",rse=utf-8,eos=18000,vad_timeout=30000,rst=json,vad_speech_tail=30000,aue=speex-wb");
		session_descrp = sb.toString();
		
		////////////////////////////////////////////////////////////////
		//init status
		status = D_add(status);
		
		//有输入data
		while(isData(status)){

			
			//open session
			//"sub=iat,auf=audio/L16;rate=16000,tte=gb2312,dvc=460006244840287|357853043378992,mac=78:D6:F0:DE:4D:5E,ssm=1,wap_proxy=wifi,ent=sms16k,rse=utf-8,eos=1800,vad_timeout=5000,rst=json,vad_speech_tail=1800,aue=speex-wb"
			sessionID = Dump.QISRSessionBegin(null, session_descrp.getBytes(), mscsessioninfo);
			status = W_add(status);
			status = R_sub(status);
			
			int GetResult_count = 0;
			while((isWrite(status) && isData(status))|| isRead(status)){
				//avoid read dead loop
				if(!(isWrite(status) && isData(status)) && isRead(status)){
					if(isData(status) && isRead(status) && !isWrite(status)){
						status = R_sub(status);
					}else{
						GetResult_count++;
						try {
							Log.e(TAG, "sleep: status:" + status);
							Thread.sleep(50);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						if(GetResult_count>10){
							status = R_sub(status);
							GetResult_count = 0;
						}
					}
					

				}else{
					GetResult_count=0; //reset
				}
				
				
				if(isWrite(status) && isData(status)){
					//get data
					try {
						Log.e(TAG, "fifo.take()");
						af = fifo.take();

					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					//write data, normal or  fin
					if(af.type==4){//fin, send last audio frame
						Dump.QISRAudioWrite(sessionID, af.data, af.len, 4, mscsessioninfo);
						status = D_sub(status);
						status = R_add(status);
					}else if(af.type>0){//normal
						Dump.QISRAudioWrite(sessionID, af.data, af.len, 2, mscsessioninfo);
					}

					//check sessionInfo
					status = checkStatus(status, mscsessioninfo);
					
					
					//Dump.QISRGetParam(sessionID, "volume".getBytes(), mscsessioninfo);
				}
				
				//check sessionInfo
				status = checkStatus(status, mscsessioninfo);

				
				if(isRead(status)){
					//get result
					byte[] r = Dump.QISRGetResult(sessionID, mscsessioninfo);
					if(r!=null && r.length>0){
						String s = new String(r);
						Result.SRResultObj obj = (Result.SRResultObj)gson.fromJson(s, Result.SRResultObj.class);
						Log.e(TAG, "Result.SRResultObj - " +  s);
						publishProgress(obj);
					}

				}
				
				///check sessionInfo
				status = checkStatus(status, mscsessioninfo);
				
			}
			
			//close session
			//Dump.QISRGetParam(sessionID, "upflow".getBytes(), mscsessioninfo);
			//Dump.QISRGetParam(sessionID, "downflow".getBytes(), mscsessioninfo);
			Dump.QISRSessionEnd(sessionID, "success".getBytes());
			

		}

		Dump.QISRFini();	

		//////////////////////////////////////////////////////////////////
		

		return audioID;
	}
	

	@Override  
	protected void onProgressUpdate(SRResultObj... progresses) {  
	     Log.i(TAG, "onProgressUpdate(Progress... progresses) called");  
	     SRResultObj  r = progresses[0]; 
	     StringBuilder sb = new StringBuilder();
	     boolean hasValue = false;
	     if(r!=null && r.ws!=null && r.ws.length>0){
	    	 for(WS w : r.ws){
	    		 if(w.cw!=null && w.cw.length>0){
	    			 if(w.cw[0].w!=null){
	    				 hasValue = true;
	    				 sb.append(w.cw[0].w);
	    			 }
	    		 }
	    	 }
	    	 
	    	 if(hasValue) {
	    		 sendMessage(HANDLER_QISR_RESULT_ADD, audioID,sb.toString()); 
	    	 }
	     }
	
	 }  



	@Override
    protected void onPostExecute(String audioID) {
		Dump.QISRFini();
		sendMessage(HANDLER_QISR_RESULT_DONE, audioID, null); 
		//Toast.makeText(MainApplication.getAppInstance(), "onPostExecute", Toast.LENGTH_LONG).show();
		
    }
	
	
	private void Wrap_QISRInit(){
		StringBuilder sb = new StringBuilder();
		sb.append("os.system=Android");
		sb.append(",msc.ver=" + VERSION);
		sb.append(",dvc=" + IMSI + "|" + IMEI);
		sb.append(",appid=5150f897");
		sb.append(",mac=" + MAC);
		if(Requester2.VALUE_WIFI.equals(WAP_PROXY)){
			sb.append(",wap_proxy=wifi");
		}else{
			sb.append(",wap_proxy=cmnet");
		}

		sb.append(",app.ver.name=" + VERSION);
		sb.append(",net.mac=" + MAC);
		sb.append(",os.imsi=" + IMSI);
		sb.append(",app.name=looklook");
		sb.append(",msc.skin=default");
		sb.append(",os.resolution=" + getDeviceResolution());
		sb.append(",os.imei=" + IMEI);
		
		sb.append(",app.path=" + APP_PATH);
		sb.append(",usr=" + IMSI + "|" + IMEI);
		sb.append(",app.ver.code=" + VERSION_CODE);
		sb.append(",auth=1,timeout=20000");
		
		Log.e(TAG, "QISRInit - " + sb.toString());
		Dump.QISRInit(sb.toString().getBytes());
	}
	
	private int D_sub(int s){
		Log.i(TAG, "D-"); 
		return (s|STATUS_DATA)^STATUS_DATA; //D-
	}
	
	private int D_add(int s){
		Log.i(TAG, "D+");
		return s|STATUS_DATA; //D+
	}
	
	
	private int W_sub(int s){
		Log.i(TAG, "W-");
		return (s|STATUS_WRITE)^STATUS_WRITE; //W-
	}
	
	private int W_add(int s){
		Log.i(TAG, "W+");
		return s|STATUS_WRITE; //W+
	}
	
	private int R_sub(int s){
		Log.i(TAG, "R-");
		return (s|STATUS_READ)^STATUS_READ; //R-
	}
	
	private int R_add(int s){
		Log.i(TAG, "R+");
		return s|STATUS_READ; //R+
	}


	
	private int checkStatus(int status2, MSCSessionInfo2 mscsessioninfo) {
		// TODO Auto-generated method stub
		isSessionInfoChanged(last_mscsessioninfo, mscsessioninfo);
		last_mscsessioninfo = mscsessioninfo;
		
		//epstatues
		if(mscsessioninfo.epstatues==3){
			status2 = W_sub(status2); //W-
		}else if(mscsessioninfo.epstatues==4){
			status2 = R_sub(status2); //R-
			status2 = W_sub(status2); //W-
		}else if(mscsessioninfo.epstatues==5){
			status2 = R_sub(status2); //R-
			status2 = W_sub(status2); //W-
		}else if(mscsessioninfo.epstatues==6){
			status2 = R_sub(status2); //R-
			status2 = W_sub(status2); //W-
		}else{
			//ok nop
		}
		
		//rsltstatus
		if(mscsessioninfo.rsltstatus==0){
			status2 = R_add(status2); //R+
		}else if(mscsessioninfo.rsltstatus==1){
			status2 = R_sub(status2); //R-
			status2 = W_sub(status2); //W-
		}else if(mscsessioninfo.rsltstatus==2){
			//status2 = R_sub(status2); //R-
		}else if(mscsessioninfo.rsltstatus==4){
			status2 = R_add(status2); //R+
		}else if(mscsessioninfo.rsltstatus==5){
			status2 = R_sub(status2); //R-
			status2 = W_sub(status2); //W-
		}else if(mscsessioninfo.rsltstatus==10){
			status2 = R_sub(status2); //R-
			status2 = W_sub(status2); //W-
		}else{
			//no-op
		}
		
		//sesstatus
		if(mscsessioninfo.sesstatus==0){
			status2 = R_add(status2); //R+
		}else{
			//no-op
		}
		
		
		//errcode
		if(mscsessioninfo.errorcode==10118){
			status2 = W_sub(status2); //W-
			status2 = R_sub(status2); //R-
		}else if(mscsessioninfo.errorcode!=0){
			status2 = R_sub(status2); //R-
			status2 = W_sub(status2); //W-
			status2 = R_sub(status2); //R-
		}else{
			//ok, nop
		}
		
		return status2;
	}

	
	//
	private boolean isData(int status){
		return (status&STATUS_DATA)!=0;
	}
	
	private boolean isWrite(int status){
		return (status&STATUS_WRITE)!=0;
	}
	
	private boolean isRead(int status){
		return (status&STATUS_READ)!=0;
	}
	
	
	private  String getDeviceResolution(){
		StringBuilder resolution = new StringBuilder();
		resolution.append(ZScreen.getWidth());
		resolution.append("*");
		resolution.append(ZScreen.getHeight());
		
		return resolution.toString();
	}
	
	private boolean isSessionInfoChanged(MSCSessionInfo2 _old, MSCSessionInfo2 _new){
		if(_old==null && _new!=null){
			Log.e(TAG, "_old==null && _new!=null");
			return true;
		}else if(_old!=null && _new==null){
			Log.e(TAG, "_old!=null && _new==null");
			return true;
		}else if(_old==null && _new==null){
			return false;
		}
		
		if((_old.errorcode != _new.errorcode) || (_old.epstatues != _new.epstatues) || (_old.rsltstatus != _new.rsltstatus) || (_old.sesstatus != _new.sesstatus)){
			Log.e(TAG, "SessionInfoChanged - old:" + gson.toJson(_old, MSCSessionInfo2.class) + ", new:" + gson.toJson(_new, MSCSessionInfo2.class));
			return true;
		}
		
		return false;
	}
	
	private void sendMessage(int type, String audioID , String result) {
		  Log.d(TAG, "sendMessage - type:" + type + ", audioID:" + audioID + ", result:" + result);
		  Intent intent = new Intent(QISR_RESULT_MSG);
		  // You can also include some extra data.
		  intent.putExtra("type", type);
		  intent.putExtra("audioID", audioID);
		  
		  if(result!=null){
			  intent.putExtra("content", result);
		  }

		  LocalBroadcastManager.getInstance(MainApplication.getAppInstance()).sendBroadcast(intent);
	}

	
	//
}
