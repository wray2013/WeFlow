package com.iflytek.msc;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

import android.util.Log;

import com.google.gson.Gson;

public class Dump {
	private static final String TAG = "Dump";
	
	private static void AppendLog2File(String objStr){
		File dataFile= new File( "/mnt/sdcard/msc/dump.log");
		if(!dataFile.getParentFile().exists()) {
			dataFile.getParentFile().mkdirs();
		}


		FileOutputStream mOutput = null;
		try {
			mOutput = new FileOutputStream(dataFile, true);
			FileChannel fcout = mOutput.getChannel();
			ByteBuffer wBuffer = ByteBuffer.wrap((objStr+"\n").getBytes());
			fcout.write(wBuffer);
			fcout.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 

	}
	
	public static int DebugLog(boolean flag) {

		Gson gson = new Gson();
		String t = "dump_DebugLog - flag:" + gson.toJson(flag, boolean.class) ;
		AppendLog2File(t);
		Log.e(TAG, t);
		
		int r =  MSC2.DebugLog(flag);
		
		String s = "dump_DebugLog - ret:" + gson.toJson(r, int.class);
		AppendLog2File(s);
		Log.w(TAG, s);
		return r;
	}
	
	public static int QTTSInit(byte abyte0[]) {

		Gson gson = new Gson();
		String t = "dump_QTTSInit - abyte0[]:" + gson.toJson(abyte0, byte[].class);
		AppendLog2File(t);
		Log.e(TAG,  t);
		
		int r =  MSC2.QTTSInit(abyte0);
		
		String s = "dump_QTTSInit - ret:" + gson.toJson(r, int.class);
		AppendLog2File(s);
		Log.w(TAG, s);
		return r;
	}

	public static char[] QTTSSessionBegin(byte abyte0[], MSCSessionInfo2 mscsessioninfo) {

		Gson gson = new Gson();
		String t = "dump_QTTSSessionBegin - abyte0[]:" + gson.toJson(abyte0, byte[].class) + ", mscsessioninfo:" +  gson.toJson(mscsessioninfo, MSCSessionInfo2.class);
		AppendLog2File(t);
		Log.e(TAG, t);
	
		char[] r = MSC2.QTTSSessionBegin(abyte0, mscsessioninfo);
		
		String s =  "dump_QTTSSessionBegin - ret:" + gson.toJson(r, char[].class);
		AppendLog2File(s);
		Log.w(TAG, s);
		return r;
	}
	
	public static int QTTSTextPut(char ac[], byte abyte0[]) {

		Gson gson = new Gson();
		String t = "dump_QTTSTextPut - ac[]:" + gson.toJson(ac, char[].class) + ", abyte0[]:" +  gson.toJson(abyte0, byte[].class);
		AppendLog2File(t);
		Log.e(TAG, t);
		
		int r =  MSC2.QTTSTextPut(ac, abyte0);
		
		String s = "dump_QTTSTextPut - ret:" + gson.toJson(r, int.class);
		AppendLog2File(s);
		Log.w(TAG, s);
		return r;
	}
	
	public static byte[] QTTSAudioGet(char ac[], MSCSessionInfo2 mscsessioninfo) {

		Gson gson = new Gson();
		String t = "dump_QTTSAudioGet - ac[]:" + gson.toJson(ac, char[].class) + ", mscsessioninfo:" +  gson.toJson(mscsessioninfo, MSCSessionInfo2.class);
		AppendLog2File(t);
		Log.e(TAG, t);
		
		byte[] r = MSC2.QTTSAudioGet(ac, mscsessioninfo);
		
		String s = "dump_QTTSAudioGet - ret:" + gson.toJson(r, byte[].class);
		AppendLog2File(s);
		Log.w(TAG, s);
		return r;
	}
	
	public static int QTTSGetParam(char ac[], byte abyte0[], MSCSessionInfo2 mscsessioninfo) {

		Gson gson = new Gson();
		String t = "dump_QTTSGetParam - ac[]:" + gson.toJson(ac, char[].class) + ", abyte0[]:" + gson.toJson(abyte0, byte[].class) + ", mscsessioninfo:" +  gson.toJson(mscsessioninfo, MSCSessionInfo2.class);
		AppendLog2File(t);
		Log.e(TAG, t);
		
		int r =  MSC2.QTTSGetParam(ac, abyte0, mscsessioninfo);
		
		String s = "dump_QTTSGetParam - ret:" + gson.toJson(r, int.class);
		AppendLog2File(s);
		Log.w(TAG, s);
		return r;
	}
	
	public static char[] QTTSAudioInfo(char ac[]) {

		Gson gson = new Gson();
		String t = "dump_QTTSAudioInfo - ac[]:" + gson.toJson(ac, char[].class);
		AppendLog2File(t);
		Log.e(TAG, t);
		
		char[] r = MSC2.QTTSAudioInfo(ac);
		
		String s = "dump_QTTSAudioInfo - ret:" + gson.toJson(r, char[].class);
		AppendLog2File(s);
		Log.w(TAG, s);
		return r;
	}
	
	
	public static int QTTSSessionEnd(char ac[], byte abyte0[]) {

		Gson gson = new Gson();
		String t = "dump_QTTSSessionEnd - ac[]:" + gson.toJson(ac, char[].class) + ", abyte0[]:" + gson.toJson(abyte0, byte[].class);
		AppendLog2File(t);
		Log.e(TAG, t);
		
		int r =  MSC2.QTTSSessionEnd(ac, abyte0);
		
		String s = "dump_QTTSSessionEnd - ret:" + gson.toJson(r, int.class);
		AppendLog2File(s);
		Log.w(TAG, s);
		return r;
	}
	
	public static int QTTSFini() {

		Gson gson = new Gson();
		String t = "dump_QTTSFini - void";
		AppendLog2File(t);
		Log.e(TAG, t);
		
		int r =  MSC2.QTTSFini();
		
		String s = "dump_QTTSFini - ret:" + gson.toJson(r, int.class);
		AppendLog2File(s);
		Log.w(TAG, s);
		return r;
	}
	
	
	public static int QISRInit(byte abyte0[]) {

		Gson gson = new Gson();
		String t = "dump_QISRInit - (String)abyte0:" + gson.toJson(abyte0!=null?new String(abyte0):"null", String.class);
		AppendLog2File(t);
		Log.e(TAG, t);
		
		int r =  MSC2.QISRInit(abyte0);
		
		String s = "dump_QISRInit - ret:" + gson.toJson(r, int.class);
		AppendLog2File(s);
		Log.w(TAG, s);
		return r;
	}
	
	public static char[] QISRSessionBegin(byte abyte0[], byte abyte1[], MSCSessionInfo2 mscsessioninfo) {

		Gson gson = new Gson();
		String t = "dump_QISRSessionBegin - (String)abyte0:" + gson.toJson(abyte0!=null?new String(abyte0):"null", String.class) + ", (String)abyte1:" + gson.toJson(abyte1!=null?new String(abyte1):"null", String.class) + ", mscsessioninfo:" + gson.toJson(mscsessioninfo, MSCSessionInfo2.class);
		AppendLog2File(t);
		Log.e(TAG, t);
		
		char[] r = MSC2.QISRSessionBegin(abyte0, abyte1, mscsessioninfo);
		
		String s= "dump_QISRSessionBegin - (String)ret:" + gson.toJson(r!=null?new String(r):"null", String.class) + 
				", mscsessioninfo:" + gson.toJson(mscsessioninfo, MSCSessionInfo2.class);
		AppendLog2File(s);
		Log.w(TAG, s);
		return r;
	}
	
	public static int QISRGetParam(char ac[], byte abyte0[], MSCSessionInfo2 mscsessioninfo) {

		Gson gson = new Gson();
		String t = "dump_QISRGetParam - (String)ac[]:" + gson.toJson(ac!=null?new String(ac):"null", String.class) + ", (String)abyte0[]:" + gson.toJson(abyte0!=null?new String(abyte0):"null", String.class) + ", mscsessioninfo:" + gson.toJson(mscsessioninfo, MSCSessionInfo2.class);
		AppendLog2File(t);
		Log.e(TAG, t);
		
		int r =  MSC2.QISRGetParam(ac, abyte0, mscsessioninfo);
		
		String s = "dump_QISRGetParam - ret:" + gson.toJson(r, int.class) + 
				", mscsessioninfo:" + gson.toJson(mscsessioninfo, MSCSessionInfo2.class);;
		AppendLog2File(s);
		Log.w(TAG, s);
		return r;
	}
	
	public static int QISRAudioWrite(char ac[], byte abyte0[], int i, int j, MSCSessionInfo2 mscsessioninfo) {

		Gson gson = new Gson();
		String t = "dump_QISRAudioWrite - (String)ac[]:" + gson.toJson(ac!=null?new String(ac):"null", String.class) + ", abyte0[]:(ignore)"   + 
				", i:" + i + ", j:" + j + ", mscsessioninfo:" + gson.toJson(mscsessioninfo, MSCSessionInfo2.class);
		AppendLog2File(t);
		Log.e(TAG, t);
		
		int r =  MSC2.QISRAudioWrite(ac, abyte0, i, j, mscsessioninfo);
		
		String s =  "dump_QISRAudioWrite - ret:" + gson.toJson(r, int.class) + 
				", mscsessioninfo:" + gson.toJson(mscsessioninfo, MSCSessionInfo2.class);
		AppendLog2File(s);
		Log.w(TAG, s);
		return r;
	}
	
	
	public static byte[] QISRGetResult(char ac[], MSCSessionInfo2 mscsessioninfo) {

		Gson gson = new Gson();
		String t = "dump_QISRGetResult - (String)ac[]:" + gson.toJson(ac!=null?new String(ac):"null", String.class) + ", mscsessioninfo:" + gson.toJson(mscsessioninfo, MSCSessionInfo2.class);
		AppendLog2File(t);
		Log.e(TAG, t);
		
		byte[] r =  MSC2.QISRGetResult(ac, mscsessioninfo);
		
		String s = "dump_QISRGetResult - (String)ret:" + gson.toJson(r!=null?new String(r):"null", String.class)  + 
				", mscsessioninfo:" + gson.toJson(mscsessioninfo, MSCSessionInfo2.class);
		AppendLog2File(s);
		Log.w(TAG, s);
		return r;
	}
	
	public static int QISRSessionEnd(char ac[], byte abyte0[]) {

		Gson gson = new Gson();
		String t = "dump_QISRSessionEnd - (String)ac[]:" + gson.toJson(ac!=null?new String(ac):"null", String.class) + ", (String)abyte0[]:" + gson.toJson(abyte0!=null?new String(abyte0):"null", String.class);
		AppendLog2File(t);
		Log.e(TAG, t);
		
		int r =  MSC2.QISRSessionEnd(ac, abyte0);
		
		String s = "dump_QISRSessionEnd - ret:" + gson.toJson(r, int.class);
		AppendLog2File(s);
		Log.w(TAG, s);
		return r;
	}

	public static int QISRFini() {
		Gson gson = new Gson();
		String t = "dump_QISRFini - void";
		AppendLog2File(t);
		Log.e(TAG, t);
		
		int r =  MSC2.QISRFini();
		
		String s = "dump_QISRFini - ret:" + gson.toJson(r, int.class);
		AppendLog2File(s);
		Log.w(TAG, s);
		return r;
	}
	
	public static int QISVFini() {
		Gson gson = new Gson();
		String t = "dump_QISVFini - void";
		AppendLog2File(t);
		Log.e(TAG, t);
		
		int r =  MSC2.QISVFini();
		
		String s = "dump_QISVFini - ret:" + gson.toJson(r, int.class);
		AppendLog2File(s);
		Log.w(TAG, s);
		return r;
	}
	
	
	public static int QMSPLogin(byte abyte0[], byte abyte1[], byte abyte2[]) {

		Gson gson = new Gson();
		String t = "dump_QMSPLogin - abyte0[]:" + gson.toJson(abyte0, byte[].class) + ", abyte1[]:" + gson.toJson(abyte1, byte[].class)  + ", abyte2[]:" + gson.toJson(abyte2, byte[].class);
		AppendLog2File(t);
		Log.e(TAG, t);
		
		int r =  MSC2.QMSPLogin(abyte0, abyte1, abyte2);
		
		String s = "dump_QMSPLogin - ret:" + gson.toJson(r, int.class);
		AppendLog2File(s);
		Log.w(TAG, s);
		return r;
	}
	
	public static byte[] QMSPUploadData(byte abyte0[], byte abyte1[], int i, byte abyte2[], MSCSessionInfo2 mscsessioninfo) {

		Gson gson = new Gson();
		String t = "dump_QMSPUploadData - abyte0[]:" + gson.toJson(abyte0, byte[].class) + ", abyte1[]:" + gson.toJson(abyte1, byte[].class) + 
				", i:" + i + ", abyte2[]:" + gson.toJson(abyte2, byte[].class) + ", mscsessioninfo:" +  gson.toJson(mscsessioninfo, MSCSessionInfo2.class);
		AppendLog2File(t);
		Log.e(TAG, t);
		
		byte[] r =  MSC2.QMSPUploadData(abyte0, abyte1, i, abyte2, mscsessioninfo);
		
		String s = "dump_QMSPUploadData - ret:" + gson.toJson(r, byte[].class);
		AppendLog2File(s);
		Log.w(TAG, s);
		return r;
	}
	
	public static byte[] QMSPDownloadData(byte abyte0[], MSCSessionInfo2 mscsessioninfo) {

		Gson gson = new Gson();
		String t = "dump_QMSPDownloadData - abyte0[]:" + gson.toJson(abyte0, byte[].class) + ", mscsessioninfo:" + gson.toJson(mscsessioninfo, MSCSessionInfo2.class);
		AppendLog2File(t);
		Log.e(TAG, t);
		
		byte[] r =  MSC2.QMSPDownloadData(abyte0, mscsessioninfo);
		
		String s = "dump_QMSPDownloadData - ret:" + gson.toJson(r, byte[].class);
		AppendLog2File(s);
		Log.w(TAG, s);
		return r;
	}
	
	public static byte[] QMSPSearch(byte abyte0[], byte abyte1[], MSCSessionInfo2 mscsessioninfo) {

		Gson gson = new Gson();
		String t = "dump_QMSPSearch - abyte0[]:" + gson.toJson(abyte0, byte[].class) + ", abyte1[]:" + gson.toJson(abyte1, byte[].class)  + ", mscsessioninfo:" + gson.toJson(mscsessioninfo, MSCSessionInfo2.class);
		AppendLog2File(t);
		Log.e(TAG, t);
		
		byte[] r =  MSC2.QMSPSearch(abyte0, abyte1, mscsessioninfo);
		
		String s = "dump_QMSPSearch - ret:" + gson.toJson(r, byte[].class);
		AppendLog2File(s);
		Log.w(TAG, s);
		return r;
	}
	
	public static int QMSPLogOut() {
		Gson gson = new Gson();
		String t = "dump_QMSPLogOut - void";
		AppendLog2File(t);
		Log.e(TAG, t);
		
		int r =  MSC2.QMSPLogOut();
		
		String s = "dump_QMSPLogOut - ret:" + gson.toJson(r, int.class);
		AppendLog2File(s);
		Log.w(TAG, s);
		return r;
	}
}
