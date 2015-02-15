package com.cmmobi.sns.api;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.cmmobi.looklook.common.gson.WeiboResponse;
import com.cmmobi.looklook.common.gson.WeiboResponse.RWUser;
import com.cmmobi.looklook.common.gson.WeiboResponse.sinaUser;
import com.cmmobi.looklook.common.gson.WeiboResponse.tencentInfo;
import com.cmmobi.looklook.info.profile.AccountInfo;
import com.cmmobi.looklook.info.profile.TimeHelper;
import com.google.gson.Gson;

/**
 * 
 * @author zhangwei
 * */
public class WeiboFriendsScanTask extends AsyncTask<String, Void, Integer> {
	public final static int HANDLER_FLAG_WEIBOSCAN_DONE = 0x34d3d001;
	public final static String BRAODCAST_TAG_WEIBOSCAN_DONE = "BRAODCAST_TAG_WEIBOSCAN_DONE";
	private Handler handler;
	private Context context;
	
	private final String TAG = "WeiboFriendsScanTask";

	public WeiboFriendsScanTask(Context context, Handler handler) {
		// TODO Auto-generated constructor stub
		this.handler = handler;
		this.context = context;
	}

	@Override
	protected Integer doInBackground(String... params) {
		// TODO Auto-generated method stub
		String uid = params[0];
		String type = params[1];
		
		Gson gson = new Gson();
		Log.e(TAG, "doInBackground uid:" + uid + " type:" + type);
		CmmobiSnsLib csl = CmmobiSnsLib.getInstance(context);
		AccountInfo uPI = AccountInfo.getInstance(uid);

		ArrayList<sinaUser> sina_friends = null;
		ArrayList<RWUser> renren_friends = null;
		ArrayList<tencentInfo> tencent_friends = null;

		// -1异常 0 未授权 1已授权
		int ret = 0;

		if ("sina".equals(type)) {
			// 尝试取sina好友数据
			if (csl.isSinaWeiboAuthorized()) {
				Log.e(TAG, "sina Authorized start fetching friends");
				ret = 1;
				sina_friends = new ArrayList<sinaUser>();
				for (int page = 1;; page++) {
					String result = null;
					try {
						result = csl.getMySWFriendsList(Long.valueOf(csl.getSinaWeiboUserId()), 30,
								page);
						WeiboResponse.SinaFriends object = (WeiboResponse.SinaFriends) gson
								.fromJson(result, WeiboResponse.SinaFriends.class);
						if (object.users == null || object.users.length == 0) {
							break;
						} else {
							for (int i = 0; i < object.users.length; i++) {
								sina_friends.add(object.users[i]);
							}
						}

					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						ret = -1;
						break;
					}

				}
				
				if(ret==1){
					Log.e(TAG, "sina firends scan done, num:" + sina_friends.size());
					uPI.sina_friends = sina_friends;
					uPI.sina_scan_time = TimeHelper.getInstance().now();
					uPI.sina_scan_bool ++;
					//uPI.persist();
				}


			}
		} else if ("renren".equals(type)) {
			// 尝试取renren好友数据
			if (csl.isRenrenWeiboAuthorized()) {
				Log.e(TAG, "renren Authorized start fetching friends");
				ret = 1;
				renren_friends = new ArrayList<RWUser>();
				for (int page = 1;; page++) {
					String result = null;
					try {
						result = csl.getMyRWFriendsList(Long.valueOf(csl.getRenrenWeiboUserId()), 20,
								page);
						WeiboResponse.Renrenfriend object = (WeiboResponse.Renrenfriend) gson
								.fromJson(result,
										WeiboResponse.Renrenfriend.class);
						if (object == null || object.response==null ||object.response.length<1 ) {
							break;
						} else {
							for (int i = 0; i < object.response.length; i++) {
								if(object.response[i]!=null){
									renren_friends.add(object.response[i]);
								}

							}
						}

					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						ret = -1;
						break;
					}

				}
				
				if(ret==1){
					Log.e(TAG, "renren firends scan done, num:" + renren_friends.size());
					uPI.renren_friends = renren_friends;
					uPI.renren_scan_time = TimeHelper.getInstance().now();
					uPI.renren_scan_bool ++;
				}


			}
		} else if ("tencent".equals(type)) {
			// 尝试取tencent好友数据
			if (csl.isTencentWeiboAuthorized()) {
				Log.e(TAG, "tencent Authorized start fetching friends");
				ret = 1;
				String tc_openid = csl.getTencentWeiboUserId();
				uPI = AccountInfo.getInstance(uid);

				if (uPI.twInfo == null || uPI.twInfo.data == null
						|| uPI.twInfo.data.name == null) {
					uPI.getTwInfo(tc_openid);
				}

				tencent_friends = new ArrayList<tencentInfo>();
				for (int page = 1;; page++) {
					String result = null;
					try {
						result = csl.getMyTWFriendsList(uPI.twInfo.data.name, "30",
								String.valueOf(30 * (page - 1)));

						WeiboResponse.TencentFriends object = (WeiboResponse.TencentFriends) gson
								.fromJson(result,
										WeiboResponse.TencentFriends.class);
						if (object.data==null || object.data.info == null
								|| object.data.info.length == 0) {
							break;
						} else {
							for (int i = 0; i < object.data.info.length; i++) {
								tencent_friends.add(object.data.info[i]);
							}
						}

					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						ret = -1;
						break;
					}

				}
				
				if(ret==1){
					Log.e(TAG, "tencent firends scan done, num:" + tencent_friends.size());
					uPI.tencent_friends = tencent_friends;
					uPI.tencent_scan_time = TimeHelper.getInstance().now();
					uPI.tencent_scan_bool ++;
					//uPI.persist();
				}

			}
		}

		return ret;
	}
	
	@Override
    protected void onPostExecute(Integer result) {
		Log.e(TAG, "onPostExecute result:" + result);
		Message msg = handler.obtainMessage(HANDLER_FLAG_WEIBOSCAN_DONE, result);
		handler.sendMessage(msg);
		Intent msgIntent = new Intent(BRAODCAST_TAG_WEIBOSCAN_DONE);
		LocalBroadcastManager.getInstance(context).sendBroadcast(msgIntent);
    }

}