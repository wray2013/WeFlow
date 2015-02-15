package com.cmmobi.looklook.info.profile;

import android.content.Context;
import cn.zipper.framwork.core.ZApplication;

public class MessageManager {
	private static final String TAG = "MessageManager";
	private static MessageManager ins = null;
	
	Context mContext;
	
	private MessageManager(){
		mContext = ZApplication.getInstance();
	}
	
	public static MessageManager getInstance(){
		if(ins==null){
			ins = new MessageManager();
		}
		
		return ins;
	}
	
	

}
