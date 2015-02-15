package com.cmmobi.looklook.common.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;

import com.cmmobi.looklook.common.gson.WeiboRequester;
import com.cmmobi.looklook.common.gson.WeiboResponse.RWCommentsInfo;
import com.cmmobi.looklook.common.gson.WeiboResponse.Renrenfriend;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * Copyright (C) ......
 * All rights reserved.
 * @package com.cmmobi.looklook.common.utils
 * @filename RenrenCmmLoader.java
 * @summary 人人评论用户信息加载
 * @author lanhai
 * @date 2014-2-17
 * @version 1.0
 */
public class RenrenCmmLoader implements Callback
{
	private static RenrenCmmLoader singlteton = null;
	
	public static final int HANDLER_RENREN_USER = 1;
	
	// 自己的handler
	private Handler mHandler = null;

	private Context mContext = null;
	// 外部handler
	private Handler mTmpHandler = null;
	
	private Map<Long, TaskInfo> mCommentInfoList = new HashMap<Long, TaskInfo>();
	
	private List<String[]> taskIds;
	private int mCurrent = 0;
	
	public static class TaskInfo
	{
		public String imageUrl = null;
		public String name = null;
		
		TaskInfo(String imageUrl, String name)
		{
			this.imageUrl = imageUrl;
			this.name = name;
		}
	}
	
	public static RenrenCmmLoader getInstance()
	{
		if (singlteton == null) {
			synchronized (ImageLoader.class) {
				if (singlteton == null) {
					singlteton = new RenrenCmmLoader();
				}
			}
		}
		return singlteton;
	}
	
	private RenrenCmmLoader()
	{
		mHandler = new Handler(this);
	}
	
	public void loadCmmInfo(Context context, Handler handler, LinkedList<RWCommentsInfo> comments)
	{
		mContext = context;
		
		mTmpHandler = handler;
		
		ArrayList<RWCommentsInfo> list = new ArrayList<RWCommentsInfo>(comments);
		
		ArrayList<String> tmp = new ArrayList<String>();
		
		for(RWCommentsInfo info : list)
		{
			if(info != null)
			{
				for(int i = 0; i < info.response.length; i++)
				{
					mCommentInfoList.put(info.response[i].authorId, null);
					tmp.add(String.valueOf(info.response[i].authorId));
				}
			}
		}
		
		taskIds = new ArrayList<String[]>();
		int i = 0;
		int j = 0;
		ArrayList<String> sub = new ArrayList<String>();
		for(; i < tmp.size(); i++)
		{
			String id = tmp.get(i);
			sub.add(id);
			j++;
			if(j >= 40)
			{
				taskIds.add(sub.toArray(new String[sub.size()]));
				sub = new ArrayList<String>();
				j = j - 40;
			}
			else if(i == tmp.size() - 1)
			{
				taskIds.add(sub.toArray(new String[j]));
			}
		}
		mCurrent = 0;
		startLoad();
	}
	
	private void startLoad()
	{
		if(mCurrent < taskIds.size())
		{
			WeiboRequester.getRenrenUserInfo(mContext, mHandler, taskIds.get(mCurrent));
		}
		else
		{
			Message tag = mTmpHandler.obtainMessage(HANDLER_RENREN_USER);
			tag.obj = mCommentInfoList;
			mTmpHandler.sendMessage(tag);
		}
	}
	
	@Override
	public boolean handleMessage(Message msg)
	{
		switch(msg.what)
		{
		case WeiboRequester.RENREN_INTERFACE_GET_USERINFO:
			mCurrent ++;
			if(msg.obj != null)
			{
				Renrenfriend result = (Renrenfriend)msg.obj;
				if(result.response != null)
				{
					for(int i = 0; i < result.response.length; i++)
					{
						TaskInfo info = new TaskInfo(result.response[i].avatar[0].url, result.response[i].name);
						mCommentInfoList.put(result.response[i].id, info);
					}
				}
				if(mCurrent < taskIds.size())
				{
					WeiboRequester.getRenrenUserInfo(mContext, mHandler, taskIds.get(mCurrent));
				}
				else
				{
					Message tag = mTmpHandler.obtainMessage(HANDLER_RENREN_USER);
					tag.obj = mCommentInfoList;
					mTmpHandler.sendMessage(tag);
				}
			}
			break;
		}
		return false;
	}
	
}
