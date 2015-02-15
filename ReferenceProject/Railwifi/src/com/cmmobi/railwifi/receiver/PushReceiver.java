package com.cmmobi.railwifi.receiver;

import java.util.List;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

import com.cmmobi.push.RichItem;
import com.cmmobi.railwifi.dialog.DialogUtils;
import com.example.listener.recevier.CmmobiPushRecevier;

/**
 * 自定义接收器
 */
public class PushReceiver extends CmmobiPushRecevier{

	private static final String TAG = "PushReceiver";

	/**
     * 接收透传消息的函数。
     * 
     * @param context  	上下文
     * @param appId    	产品id
     * @param userId   	用户id
     * @param msgId    	消息id
     * @param title     标题
     * @param content   消息内容
     * @param items	 	富媒体数据
     * @param timeStamp 时间戳
     */
	@Override
	public void onMessage(Context context, String appId, String userId,
			String msgId, int notifyId, boolean prompt, String title, String content,
			List<RichItem> items, String timeStamp) {
		// TODO Auto-generated method stub
		if (prompt) {
			// notification通知
			Log.v(TAG, "onMessage(Notify) msgId:" + msgId + ", notifyId:" +notifyId+ ", title:" + title + ", content:" + content);
			DialogUtils.SendCallHelpDialog(context, title, content, msgId, notifyId);
		} else {
			// 透传消息
			Log.v(TAG, "onMessage(Msg) msgId:" + msgId + ", not prompt, title:" + title + ", content:" + content);
		}

	}

	
	/**
     * 接收通知点击的函数。注：推送通知被用户点击前，应用无法通过接口获取通知的内容。
     * 
     * @param context	上下文
     * @param appId		产品id
     * @param userId	用户id
     * @param msgId		消息id
     * @param title		推送的通知的标题
     * @param content	推送的通知的内容
     * @param items		富媒体数据
     * @param timeStamp	时间戳
     */
	@Override
	public void onNotificationClicked(final Context context, String appId, String userId, final String msgId, final String title, final String content,
			List<RichItem> items, String timeStamp) {
		// TODO Auto-generated method stub
		Log.v(TAG, "onNotificationClicked - msgId:" +msgId + ", title:" + title + ", content:" + content);
		new Handler().postDelayed(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				DialogUtils.SendCallHelpDialog(context, title, content, msgId, -1);
			}
		}, 5);
		

	}
	
}

