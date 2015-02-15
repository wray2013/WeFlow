package com.cmmobi.looklook.common.utils;

import java.io.File;

import android.content.Context;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.cmmobi.looklook.common.constant.Constant;
import com.cmmobi.looklook.info.profile.ActiveAccount;
import com.cmmobi.looklook.info.profile.TimeHelper;
import com.cmmobivideo.workers.XTransCode;
import com.cmmobivideo.workers.XTransCode.TransCodeInfoListener;

import effect.XEffectTransCode;
import effect.XEffects;

public class EffectTransCodeUtil {
	private static final String TAG = "VideoEffectTransCodeUtil";
	private Handler handler;
	private XEffects mEffects;
	private XTransCode mTranscode;
	private String mInFileName;
	private String mOutFileName;
	private Context mContext;
	public final static int MAIN_VIDEO = 1;
	public final static int MAIN_AUDIO = 2;
	public final static int SHORT_AUDIO = 3;
	public final static int MAIN_IMAGE = 4;
	private int mediaType = MAIN_VIDEO;// 0 主视频 1主音频 2 辅音频 3 主图片
	
	
	public static final int HANDLER_PROCESS_EFFECTS_FINISH = 0x0023;
	public static final int HANDLER_PROCESS_EFFECTS_SCHEDULE = 0x0025;
	
	public EffectTransCodeUtil(Handler handler,XEffects effects,String infilename,Context context,int type) {
		this.handler = handler;
		this.mEffects = effects;
		mInFileName = infilename;
		mContext = context;
		mediaType = type;
		mTranscode = new XTransCode(mEffects,new MyTranCodeInfo());
	}
	
	public void start(String preffix) {
		if (mTranscode.getStatus() == XEffectTransCode.STATUS_UNKNOW || mTranscode.getStatus() == XEffectTransCode.STATUS_STOP) {
			long currentTime = TimeHelper.getInstance().now();
			String mediaID = String.valueOf(currentTime);
			String RECORD_FILE_DIR =  Constant.SD_STORAGE_ROOT + "/" + ActiveAccount.getInstance(mContext).getLookLookID() + "/" + preffix;
			mOutFileName = Environment.getExternalStorageDirectory() + RECORD_FILE_DIR + "/" + mediaID +  "/" + mediaID + ".mp4" ;
			File dstFile = new File(mOutFileName);
	        if(!dstFile.getParentFile().exists()) {
	        	dstFile.getParentFile().mkdirs();
			}
	        Log.d(TAG,"mInFileName = " + mInFileName + " mOutFileName = " + mOutFileName);
			mTranscode.open(mInFileName, mOutFileName);
			mTranscode.start();
		}
	}
	
	class MyTranCodeInfo implements TransCodeInfoListener{

		@Override
		public void OnSchedule(double percent) {
			// TODO Auto-generated method stub
			Message msg = new Message();
			msg.what = HANDLER_PROCESS_EFFECTS_SCHEDULE;
			msg.arg1 = mediaType;
			msg.arg2 = (int) (percent * 100);
			handler.sendMessage(msg);
			Log.i(TAG, "[OnSchedule] percent:"+percent);
		}

		@Override
		public void OnFinish() {
			// TODO Auto-generated method stub
			Message msg = new Message();
			msg.what = HANDLER_PROCESS_EFFECTS_FINISH;
			msg.obj = mOutFileName;
			msg.arg1 = mediaType;
			handler.sendMessage(msg);
			mTranscode.release();
			Log.d(TAG, "[OnFinish]");
		}
	}

}
