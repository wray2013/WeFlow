/*
 * 
 *      zhangwei@cmmobi.com
 *      
 *      2013-03-28
 * */


package com.iflytek.msc;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.os.Handler;
import android.util.Log;

/**
 *  @author zhangwei 
 */
public class QTTS {
	public final static int QTTS_STATUS_UNINIT = 0;
	public final static int QTTS_STATUS_INIT = 1;
	public final static int QTTS_STATUS_SESSION = 2;
	public final static int QTTS_STATUS_PUTTEXT = 3;

	private String TAG = "QTTS";
	private String outputFile;
	private String sentences;
	private MSCSessionInfo2 sInfo;
	private char[] sessinID;
	private String appID;
	
	private Handler handler;

	private int status;

	private QTTS() {
		sInfo = new MSCSessionInfo2();
		status = QTTS_STATUS_UNINIT;
		appID = null;
	}

	public QTTS(Handler h, String ID) {
		this();
		appID = ID;
		handler = h;
	}
	public void Init() {
		if (appID == null) {
			Log.e(TAG, "Init - fail - appID is null");
			return;
		}

		int ret = MSC2.QTTSInit(("appid=" + appID).getBytes());

		if (ret == 0) {
			status = QTTS_STATUS_INIT;
			Log.d(TAG, "Init - OK");
		} else {
			Log.e(TAG, "Init - fail");
		}
	}

	public void Fini() {
		int ret = MSC2.QTTSFini();
		if (ret == 0) {
			// exec ok
			status = QTTS_STATUS_UNINIT;
			Log.d(TAG, "QTTSFini - OK");
		} else {
			Log.e(TAG, "QTTSFini - fail");
		}
	}

	public int getStatus() {
		return status;
	}

	public void Process(String path, String s) {
		outputFile = path;
		sentences = s;
		process();

	}

	public void Process(String s) {
		outputFile = "/sdcard/qtts.pcm";
		sentences = s;
		process();

	}

	// //////////////////private method ////////////////////

	// session should lock
	private void QTTSSessionBegin() {
		String arg = "ssm=1,ent=vivi21,auf=audio/L16;rate=16000,vcn=vinn,tte=UTF8";
		sessinID = MSC2.QTTSSessionBegin(arg.getBytes(), sInfo);

		if (sInfo.errorcode == 0) {
			status = QTTS_STATUS_SESSION;
			Log.d(TAG, "QTTSSessionBegin - OK");
		} else {
			Log.e(TAG, "QTTSSessionBegin - fail");
		}
	}

	private void QTTSTextPut() {
		if (sessinID == null || sentences == null) {
			Log.e(TAG, "QTTSTextPut - fail - args null");
		}

		int ret = MSC2.QTTSTextPut(sessinID, sentences.getBytes());
		if (ret == 0) {
			status = QTTS_STATUS_PUTTEXT;
			Log.d(TAG, "QTTSTextPut - OK");
		} else {
			Log.e(TAG, "QTTSTextPut - fail");
		}
	}

	private byte[] QTTSAudioGet() {
		if (sessinID == null || sInfo.errorcode != 0) {
			return null;
		}
		// status keep

		byte[] ret = MSC2.QTTSAudioGet(sessinID, sInfo);

		if (sInfo.errorcode != 0) {
			Log.e(TAG, "QTTSAudioGet error" + sInfo.errorcode);
		}

		return ret;
	}

	private void QTTSSessionEnd() {
		int ret = MSC2.QTTSSessionEnd(sessinID, null);
		if (ret == 0) {
			// exec ok
			status = QTTS_STATUS_INIT;
			Log.d(TAG, "QTTSSessionEnd - OK");
		} else {
			Log.e(TAG, "QTTSSessionEnd - fail");
		}
	}

	private void process() {

		QTTSSessionBegin();
		if (status != QTTS_STATUS_SESSION) {
			return;
		}

		QTTSTextPut();
		if (status != QTTS_STATUS_PUTTEXT) {
			return;
		}

		try {
			// open file
			File f = new File(outputFile);
			if (!f.exists()) {
				f.mkdirs();
				if (!f.createNewFile()) {
					f.delete();
					f.createNewFile();
				}
			}
			DataOutputStream out = new DataOutputStream(
					new BufferedOutputStream(new FileOutputStream(outputFile)));

			while (sInfo.errorcode == 0) {// pdf show synth_status != 2, todo
				byte[] buff = QTTSAudioGet();
				if(buff.length==0){
					break;
				}
				out.write(buff);
				Log.e(TAG, "write buff.length:" + buff.length);

			}

			out.close();

		} catch (FileNotFoundException e) {

			e.printStackTrace();
			Log.e(TAG, "FileNotFoundException");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			QTTSSessionEnd();
		}
	}

}