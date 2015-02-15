package com.cmmobi.looklook.common.gson;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Matrix;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import cn.zipper.framwork.core.ZLog;
import cn.zipper.framwork.io.network.ZHttp2;
import cn.zipper.framwork.io.network.ZHttpResponse;

import com.cmmobi.looklook.common.constant.Constant;
import com.cmmobi.looklook.common.gson.WeiboResponse.RWCommentsInfo;
import com.cmmobi.looklook.common.gson.WeiboResponse.SinaComments;
import com.cmmobi.looklook.common.gson.WeiboResponse.SinaCountComment;
import com.cmmobi.looklook.common.gson.WeiboResponse.TencentComments;
import com.cmmobi.looklook.common.gson.WeiboResponse.TencentCountComments;
import com.cmmobi.looklook.info.profile.AccountInfo;
import com.cmmobi.looklook.info.profile.ActiveAccount;
import com.cmmobi.looklook.info.profile.CommonInfo;
import com.cmmobi.looklook.info.profile.MediaValue;
import com.cmmobi.sns.api.CmmobiSnsLib;
import com.cmmobi.sns.exceptions.WeiboError;
import com.cmmobi.sns.utils.BitmapHelper;
import com.cmmobi.sns.utils.JsonParseUtils;
import com.google.gson.Gson;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.SendMessageToWX;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.tencent.mm.sdk.openapi.WXMediaMessage;
import com.tencent.mm.sdk.openapi.WXWebpageObject;

@SuppressWarnings("unused")
public final class WeiboRequester {
	
	


	public static final int SINA_INTERFACE_GET_ACCOUNTINFO = 0xefffff01;
	public static final int SINA_INTERFACE_FRIENDS_LIST = 0xefffff02;
	public static final int SINA_INTERFACE_PUBLISH_WEIBO = 0xefffff03;
	public static final int SINA_INTERFACE_GET_COMMENT = 0xefffff04;
	public static final int SINA_INTERFACE_DEL_WEIBO = 0xefffff05;
	public static final int SINA_INTERFACE_COUNT_WEIBO = 0xefffff06;
	public static final int SINA_INTERFACE_REPLY_COMMENT = 0xefffff07;
	
	public static final int RENREN_INTERFACE_GET_ACCOUNTINFO = 0xeffffe01;
	public static final int RENREN_INTERFACE_FRIENDS_LIST = 0xeffffe02;
	public static final int RENREN_INTERFACE_PUBLISH_WEIBO = 0xeffffe03;
	public static final int RENREN_INTERFACE_GET_COMMENT = 0xeffffe04;
	public static final int RENREN_INTERFACE_DEL_WEIBO = 0xeffffe05;
	public static final int RENREN_INTERFACE_COUNT_WEIBO = 0xeffffe06;
	public static final int RENREN_INTERFACE_REPLY_COMMENT = 0xeffffe07;
	public static final int RENREN_INTERFACE_GET_USERINFO = 0xeffffe08;
	
	public static final int TENCENT_INTERFACE_GET_ACCOUNTINFO = 0xeffffd01;
	public static final int TENCENT_INTERFACE_FRIENDS_LIST = 0xeffffd02;
	public static final int TENCENT_INTERFACE_PUBLISH_WEIBO = 0xeffffd03;
	public static final int TENCENT_INTERFACE_GET_COMMENT = 0xeffffd04;
	public static final int TENCENT_INTERFACE_DEL_WEIBO = 0xeffffd05;	
	public static final int TENCENT_INTERFACE_COUNT_WEIBO = 0xeffffd06;
	public static final int TENCENT_INTERFACE_REPLY_COMMENT = 0xeffffd07;
	
	public static final int WEIXIN_INTERFACE_SEND = 0xeffff801;
	private static final int TIMELINE_SUPPORTED_VERSION = 0x21020001;
	private static final String TAG = "WeiboRequester";
	
	public IWXAPI api;
	private static WeiboRequester ins;

	/**
	 * 禁止构造实例;
	 */
	private WeiboRequester(Context context) {
		api =  WXAPIFactory.createWXAPI(context, Constant.WX_APP_ID, false);
	}
	
	public static WeiboRequester getInstance(Context context){
		if(ins==null){
			ins = new WeiboRequester(context);
		}
		
		return ins;
	}
	
	
	/**
	 * 获取sina用户信息
	 * 
	 * @param handler
	 */
	public static boolean getSinaAccountInfo(Handler handler, String snsid) {

		int cmd = SINA_INTERFACE_GET_ACCOUNTINFO;
		String snstype = "1";
		CmmobiSnsLib csl = CmmobiSnsLib.getInstance();
		if(!csl.isSinaWeiboAuthorized()){
			return false;
		}
		GetAccounInfoWorker worker = new GetAccounInfoWorker(handler, WeiboResponse.SWUserInfo.class);
		worker.execute(snstype, snsid, cmd);

		return true;
	}
	
	/**
	 * 获取renren用户信息
	 * 
	 * @param handler
	 */
	public static boolean getRenrenAccountInfo(Handler handler, String snsid) {

		int cmd = RENREN_INTERFACE_GET_ACCOUNTINFO;
		String snstype = "2";
		CmmobiSnsLib csl = CmmobiSnsLib.getInstance();
		if(!csl.isRenrenWeiboAuthorized()){
			return false;
		}
		
		//GetAccounInfoWorker worker = new GetAccounInfoWorker(handler, WeiboResponse.RWUserInfo[].class);
		GetAccounInfoWorker worker = new GetAccounInfoWorker(handler, WeiboResponse.RWUserInfo.class);
		worker.execute(snstype, snsid, cmd);
		return true;

	}
	
	/**
	 * 获取Tencent用户信息
	 * 
	 * @param handler
	 */
	public static boolean getTencentAccountInfo(Handler handler, String snsid) {

		int cmd = TENCENT_INTERFACE_GET_ACCOUNTINFO;
		String snstype = "6";
		CmmobiSnsLib csl = CmmobiSnsLib.getInstance();
		if(!csl.isTencentWeiboAuthorized()){
			return false;
		}
		
		GetAccounInfoWorker worker = new GetAccounInfoWorker(handler, WeiboResponse.TWUserInfo.class);
		worker.execute(snstype, snsid, cmd);

		return true;
	}

	
	/*-----------------------------------------------------------------*/
	
	/**
	 * 获取sina微博互粉好友列表
	 * 
	 * @param handler
	 */
	public static boolean getSinaFriendList(Context c, Handler handler, int num, int page) {

		int cmd = SINA_INTERFACE_FRIENDS_LIST;
		String snstype = "1";
		CmmobiSnsLib csl = CmmobiSnsLib.getInstance();
		if(!csl.isSinaWeiboAuthorized()){
			return false;
		}
		GetFriendListWorker worker = new GetFriendListWorker(handler, WeiboResponse.SinaFriends.class);
		worker.execute(c, snstype, cmd, num, page);

		return true;
	}
	
	/**
	 * 获取renren微博互粉好友列表
	 * 
	 * @param handler
	 */
	public static boolean getRenrenFriendList(Context c, Handler handler, int num, int page) {

		int cmd = RENREN_INTERFACE_FRIENDS_LIST;
		String snstype = "2";
		CmmobiSnsLib csl = CmmobiSnsLib.getInstance();
		if(!csl.isRenrenWeiboAuthorized()){
			return false;
		}
		
		GetFriendListWorker worker = new GetFriendListWorker(handler, WeiboResponse.Renrenfriend.class);
		worker.execute(c, snstype, cmd, num, page);

		return true;
	}
	
	/**
	 * 获取renren微博互粉好友列表
	 * 
	 * @param handler
	 */
	public static boolean getRenrenUserInfo(Context c, Handler handler, String[] ids) {

		int cmd = RENREN_INTERFACE_GET_USERINFO;
		String snstype = "2";
		CmmobiSnsLib csl = CmmobiSnsLib.getInstance();
		if(!csl.isRenrenWeiboAuthorized()){
			return false;
		}
		
		GetUserInfoWorker worker = new GetUserInfoWorker(handler, WeiboResponse.Renrenfriend.class);
		worker.execute(c, snstype, cmd, ids);

		return true;
	}
	
	/**
	 * 获取tencent微博互粉好友列表
	 * 
	 * @param handler
	 */
	public static boolean getTencentFriendList(Context c, Handler handler, int num, int page) {

		int cmd = TENCENT_INTERFACE_FRIENDS_LIST;
		String snstype = "6";
		CmmobiSnsLib csl = CmmobiSnsLib.getInstance();
		if(!csl.isTencentWeiboAuthorized()){
			return false;
		}
		
		GetFriendListWorker worker = new GetFriendListWorker(handler, WeiboResponse.TencentFriends.class);
		worker.execute(c, snstype, cmd, num, page);

		return true;
	}
	
	/*-----------------------------------------------------------------*/
	
	/**
	 * 发布一条sina微博（带网络图片）
	 * 
	 * @param handler
	 */
	public static boolean publishSinaWeibo(Context c, Handler handler, String content, String picUrl, boolean needDownload) {

		int cmd = SINA_INTERFACE_PUBLISH_WEIBO;
		String snstype = "1";
		CmmobiSnsLib csl = CmmobiSnsLib.getInstance();
		if(!csl.isSinaWeiboAuthorized()){
			return false;
		}
		PublishWeiboWorker worker = new PublishWeiboWorker(handler);
		worker.execute(c, snstype, cmd, content, picUrl, needDownload);

		return true;
	}
	
	/**
	 * 发布一条人人微博（带网络图片）
	 * 
	 * @param handler
	 */
	public static boolean publishRenrenWeibo(Context c, Handler handler, String content, String picUrl, boolean needDownload) {

		int cmd = RENREN_INTERFACE_PUBLISH_WEIBO;
		String snstype = "2";
		CmmobiSnsLib csl = CmmobiSnsLib.getInstance();
		if(!csl.isRenrenWeiboAuthorized()){
			return false;
		}
		PublishWeiboWorker worker = new PublishWeiboWorker(handler);
		worker.execute(c, snstype, cmd, content, picUrl, needDownload);

		return true;
	}

	
	/**
	 * 发布一条腾讯微博（带网络图片）
	 * 
	 * @param handler
	 */
	public static boolean publishTencentWeibo(Context c, Handler handler, String content, String picUrl, boolean needDownload) {

		int cmd = TENCENT_INTERFACE_PUBLISH_WEIBO;
		String snstype = "6";
		CmmobiSnsLib csl = CmmobiSnsLib.getInstance();
		if(!csl.isTencentWeiboAuthorized()){
			return false;
		}
		PublishWeiboWorker worker = new PublishWeiboWorker(handler);
		worker.execute(c, snstype, cmd, content, picUrl, needDownload);

		return true;
	}
	
	/*-----------------------------------------------------------------*/
	
	/**
	 * 获取sina微博评论列表,每页默认50个记录
	 * 
	 * @param handler
	 */
	public static boolean GetSinaComment(Context c, Handler handler, String weiboid, String count, String page) {

		int cmd = SINA_INTERFACE_GET_COMMENT;
		String snstype = "1";
		CmmobiSnsLib csl = CmmobiSnsLib.getInstance();
		if(!csl.isSinaWeiboAuthorized()){
			return false;
		}
		GetCommentWeiboWorker worker = new GetCommentWeiboWorker(handler, SinaComments.class);
		worker.execute(c, snstype, cmd, weiboid, count, page);
		
		return true;
	}
	
	/**
	 * 获取renren微博评论列表,每页默认50个记录
	 * 
	 * @param handler
	 */
	public static boolean GetRenrenComment(Context c, Handler handler, String weiboid, String count, String page) {

		int cmd = RENREN_INTERFACE_GET_COMMENT;
		String snstype = "2";
		CmmobiSnsLib csl = CmmobiSnsLib.getInstance();
		if(!csl.isRenrenWeiboAuthorized()){
			return false;
		}
		GetCommentWeiboWorker worker = new GetCommentWeiboWorker(handler, RWCommentsInfo.class);
		worker.execute(c, snstype, cmd, weiboid, count, page);
		
		return true;
	}
	
	/**
	 * 获取tencent微博评论列表,每页默认50个记录
	 * 
	 * @param handler
	 */
	public static boolean GetTencentComment(Context c, Handler handler, String weiboid, String count, String page) {

		int cmd = TENCENT_INTERFACE_GET_COMMENT;
		String snstype = "6";
		CmmobiSnsLib csl = CmmobiSnsLib.getInstance();
		if(!csl.isTencentWeiboAuthorized()){
			return false;
		}
		GetCommentWeiboWorker worker = new GetCommentWeiboWorker(handler, TencentComments.class);
		worker.execute(c, snstype, cmd, weiboid, count, page);
		
		return true;
	}
	
	
	/*-----------------------------------------------------------------*/
	/**
	 * 对一条sina微博的评论进行回复
	 * 
	 * @param handler
	 */
	public static boolean replySinaComment(Context c, Handler handler, String weiboID, String commentID, String content) {

		int cmd = SINA_INTERFACE_REPLY_COMMENT;
		String snstype = "1";
		CmmobiSnsLib csl = CmmobiSnsLib.getInstance();
		if(!csl.isSinaWeiboAuthorized()){
			return false;
		}
		ReplyCommentWeiboWorker worker = new ReplyCommentWeiboWorker(handler);
		worker.execute(c, snstype, cmd, weiboID, commentID, content);

		return true;
	}
	
	/**
	 * 对一条人人微博的评论进行回复
	 * 
	 * @param handler
	 */
	public static boolean replyRenrenComment(Context c, Handler handler, String weiboID, String commentID, String content) {

		int cmd = RENREN_INTERFACE_REPLY_COMMENT;
		String snstype = "2";
		CmmobiSnsLib csl = CmmobiSnsLib.getInstance();
		if(!csl.isRenrenWeiboAuthorized()){
			return false;
		}
		ReplyCommentWeiboWorker worker = new ReplyCommentWeiboWorker(handler);
		worker.execute(c, snstype, cmd, weiboID, commentID, content);

		return true;
	}

	
	/**
	 * 对一条腾讯微博的评论进行回复
	 * 
	 * @param handler
	 */
	public static boolean replyTencentComment(Context c, Handler handler, String weiboID, String commentID, String content) {

		int cmd = TENCENT_INTERFACE_REPLY_COMMENT;
		String snstype = "6";
		CmmobiSnsLib csl = CmmobiSnsLib.getInstance();
		if(!csl.isTencentWeiboAuthorized()){
			return false;
		}
		ReplyCommentWeiboWorker worker = new ReplyCommentWeiboWorker(handler);
		worker.execute(c, snstype, cmd, weiboID, commentID, content);

		return true;
	}
	
	/*-----------------------------------------------------------------*/
	/**
	 * 删除一条sina微博
	 * 
	 * @param handler
	 */
	public static boolean delSinaWeibo(Context c, Handler handler, String weiboID) {

		int cmd = SINA_INTERFACE_DEL_WEIBO;
		String snstype = "1";
		CmmobiSnsLib csl = CmmobiSnsLib.getInstance();
		if(!csl.isSinaWeiboAuthorized()){
			return false;
		}
		
		DelWeiboWorker worker = new DelWeiboWorker(handler);
		worker.execute(c, snstype, cmd, weiboID);
		
		return true;

	}
	
	/**
	 * 删除一条renren微博
	 * 
	 * @param handler
	 */
	public static boolean delRenrenWeibo(Context c, Handler handler, String weiboID) {

		int cmd = RENREN_INTERFACE_DEL_WEIBO;
		String snstype = "2";
		
		CmmobiSnsLib csl = CmmobiSnsLib.getInstance();
		if(!csl.isRenrenWeiboAuthorized()){
			return false;
		}
		
		DelWeiboWorker worker = new DelWeiboWorker(handler);
		worker.execute(c, snstype, cmd, weiboID);

		return true;
	}
	
	
	/**
	 * 删除一条tencent微博
	 * 
	 * @param handler
	 */
	public static boolean delTencentWeibo(Context c, Handler handler, String weiboID) {

		int cmd = TENCENT_INTERFACE_DEL_WEIBO;
		String snstype = "6";
		
		CmmobiSnsLib csl = CmmobiSnsLib.getInstance();
		if(!csl.isTencentWeiboAuthorized()){
			return false;
		}
		
		DelWeiboWorker worker = new DelWeiboWorker(handler);
		worker.execute(c, snstype, cmd, weiboID);

		return true;
	}
	
	/*-----------------------------------------------------------------*/
	/**
	 * 获取sina微博评论数
	 * 
	 * @param handler
	 */
	public static boolean countSinaComment(Context c, Handler handler, String[] weiboid) {

		int cmd = SINA_INTERFACE_GET_COMMENT;
		String snstype = "1";
		
		CmmobiSnsLib csl = CmmobiSnsLib.getInstance();
		if(!csl.isSinaWeiboAuthorized()){
			return false;
		}
		
		GetCountWeiboWorker worker = new GetCountWeiboWorker(handler, SinaCountComment[].class);
		worker.execute(c, snstype, cmd, weiboid);
		
		return true;
	}
	
	/**
	 * 获取renren微博评论数量,暂无api支持
	 * 
	 * @param handler
	 */
	public static boolean countRenrenComment(Context c, Handler handler, String[] weiboid) {

		int cmd = RENREN_INTERFACE_GET_COMMENT;
		String snstype = "2";
		
		
		CmmobiSnsLib csl = CmmobiSnsLib.getInstance();
		if(!csl.isRenrenWeiboAuthorized()){
			return false;
		}
		
		//GetCountWeiboWorker worker = new GetCountWeiboWorker(handler, .class);
		//worker.execute(c, snstype, cmd, weiboid);
		
		return true;
	}
	
	
	/**
	 * 获取tencent微博评论数量
	 * 
	 * @param handler
	 */
	public static boolean countTencentComment(Context c, Handler handler, String[] weiboid) {

		int cmd = TENCENT_INTERFACE_GET_COMMENT;
		String snstype = "6";
		
		CmmobiSnsLib csl = CmmobiSnsLib.getInstance();
		if(!csl.isTencentWeiboAuthorized()){
			return false;
		}
		
		GetCountWeiboWorker worker = new GetCountWeiboWorker(handler, TencentCountComments.class);
		worker.execute(c, snstype, cmd, weiboid);
		
		return true;
	}
	
	public static void publishWeiXin(Context c, Handler handler, String title, String content, String jumpurl, String pic_url, boolean sendTimeline){
		Log.e(TAG, "publishWeiXin - title:" + title + ", content:" + content + ", jumpurl:" + jumpurl + ", pic_url" + pic_url + ", sendTimeline:" + sendTimeline);
		int cmd = WEIXIN_INTERFACE_SEND;
		WeixinWorker worker = new WeixinWorker(handler);
		worker.execute(c, cmd, title, content, jumpurl, pic_url, sendTimeline);
	}

///////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * ------------------------------------------------------------------------
	 * 内部类, 获取个人信息;
	 * 
	 * @author zhangwei
	 */
	private static class GetAccounInfoWorker extends Thread {
		private Handler handler;
		private Class<?> cls;
		private Gson gson;
		
		String snstype;  
		String snsid;
		int cmd;


		/**
		 * 
		 * @param handler
		 * @param responseType: 响应类型;
		 * @param cls: 响应对象的class类型;
		 */
		public GetAccounInfoWorker(Handler handler, Class<?> cls) {
			this.handler = handler;
			this.cls = cls;
			this.gson = new Gson();
		}

		public void execute(String snstype, String snsid, int cmd) {
			// TODO Auto-generated method stub
			this.snstype = snstype;
			this.snsid = snsid;
			this.cmd = cmd;
			
			this.start();
		}

		@Override
		public void run(){

			Object object = null;

			//String uid = snstype + "_" + snsid;
			try {
				//ZLog.e(">> WeiboRequest (snstype:" + snstype + " cmd:" + cmd + "uid:" + uid + ")");
				CmmobiSnsLib csl = CmmobiSnsLib.getInstance();
				if(snstype.equals("1")){ //sina
					object = gson.fromJson(csl.getSWUserInfo(Long.valueOf(snsid)), cls);
				}else if(snstype.equals("2")){ //renren
					object = gson.fromJson(csl.getRWUserInfo(Long.valueOf(snsid)), cls);
				}else if(snstype.equals("6")){ //tencent
					object = gson.fromJson(csl.getTWUserInfo(), cls);
				}
				
				//ZLog.e("<< WeiboResponse (" + cls.getSimpleName() + "): " + result);
				
			} catch (Exception e) {
				e.printStackTrace();
			}

			if (handler != null) {
				Message message = handler.obtainMessage(cmd, object);
				handler.sendMessage(message);
			} else {
				ZLog.alert();
				ZLog.e("handler is null, data can not callback.");
			}
		
		}


	}
	
	/**
	 * ------------------------------------------------------------------------
	 * 内部类, 获取微博好友列表;
	 * 
	 * @author zhangwei
	 */
	private static class GetFriendListWorker extends Thread {
		private Handler handler;
		private Class<?> cls;
		private Gson gson;
		
		Context context;
		String snstype;
		int cmd;
		int num;
		int page;

		/**
		 * 
		 * @param handler
		 * @param responseType: 响应类型;
		 * @param cls: 响应对象的class类型;
		 */
		public GetFriendListWorker(Handler handler, Class<?> cls) {
			this.handler = handler;
			this.cls = cls;
			this.gson = new Gson();
		}

		public void execute(Context c, String snstype, int cmd, int num, int page) {
			// TODO Auto-generated method stub
			this.context = c;
			this.snstype = snstype;
			this.cmd = cmd;
			this.num = num;
			this.page = page;
			
			this.start();
		}

		@Override
		public void run(){

			Object object = null;
			String result = null;
			


			try {
				
				ActiveAccount acct = ActiveAccount.getInstance(context);
				String uid = acct.getUID();//acct.snstype + "_" + acct.snsid;
				ZLog.e(">> WeiboRequest (snstype:" + snstype + " cmd:" + cmd + "uid:" + uid + ")");
				CmmobiSnsLib csl = CmmobiSnsLib.getInstance();
				
				if(snstype.equals("1")){
					if (!csl.getSinaWeiboUserId().equals(CmmobiSnsLib.WEIBOUSERIDNULL)) {
						result = csl.getMySWFriendsList(
								Long.valueOf(csl.getSinaWeiboUserId()), num,
								page);
						object = gson.fromJson(result, cls);
					}
					
				}else if(snstype.equals("2")){
					if (!csl.getRenrenWeiboUserId().equals(CmmobiSnsLib.WEIBOUSERIDNULL)) {
						result = csl.getMyRWFriendsList(
								Long.valueOf(csl.getRenrenWeiboUserId()), num,
								page);
						object = gson.fromJson(result, cls);
					}
					
				}else if(snstype.equals("6")){
					String tc_openid = csl.getTencentWeiboUserId();		
					AccountInfo uPI = AccountInfo.getInstance(uid);
					
					if (!tc_openid.equals(CmmobiSnsLib.WEIBOUSERIDNULL)) {
						if (uPI.twInfo == null || uPI.twInfo.data == null
								|| uPI.twInfo.data.name == null) {
							uPI.getTwInfo(tc_openid);
						}
						result = csl.getMyTWFriendsList(uPI.twInfo.data.name,
								String.valueOf(num),
								String.valueOf(num * (page - 1)));
						object = gson.fromJson(result, cls);
					}
				}
				
				ZLog.e("<< WeiboResponse (" + cls.getSimpleName() + "): " + result);
				
			} catch (Exception e) {
				e.printStackTrace();
			}

			if (handler != null) {
				Message message = handler.obtainMessage(cmd, object);
				handler.sendMessage(message);
			} else {
				ZLog.alert();
				ZLog.e("handler is null, data can not callback.");
			}
		
		}

	}

	
	/**
	 * ------------------------------------------------------------------------
	 * 内部类, 获取微博好友列表;
	 * 
	 * @author zhangwei
	 */
	private static class PublishWeiboWorker extends Thread {
		private Handler handler;
		private Gson gson;
		
		Context context;
		String snstype;
		int cmd;
		String content; //要发布的微博内容 <=140汉字
		String picUrl; //要发布的网络图片
		boolean needDownload; //要发布的本地图片
		
		private String fetchPic(String picUrl){
			Bitmap thumb = null;
			String uid = ActiveAccount.getInstance(context).getLookLookID();
			MediaValue mv = AccountInfo.getInstance(uid).mediamapping.getMedia(uid, picUrl);
			if(MediaValue.checkMediaAvailable(mv)){
				try {
					File file = new File(Environment.getExternalStorageDirectory() + mv.localpath);
					thumb = BitmapHelper.getBitmapFromInputStream(new FileInputStream(file));
					
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();

				}
			}
			
			if(thumb==null){
				ZHttp2 http2 = new ZHttp2();
				ZHttpResponse httpResponse = http2.get(picUrl);

				thumb = BitmapHelper.getBitmapFromInputStream(httpResponse.getInputStream());
	
				//thumb = Bitmap.createBitmap(thumb, 0, 0, 50, 50);
			}
			
			if(thumb!=null){
				Matrix matrix = new Matrix();         
				int width = thumb.getWidth();// 获取资源位图的宽         
				int height = thumb.getHeight();// 获取资源位图的高         
				float w = (float) 0.5;//(float) (100.0 / thumb.getWidth());         
				float h = (float) 0.5;//(float) (100.0 / thumb.getHeight());  
				Log.e(TAG, "run - thumb width:" + width + ", height:" + height + ", w:" + w + ", h:" + h);
				matrix.postScale(w, h);// 获取缩放比例                     
				// 根据缩放比例获取新的位图      
				thumb = Bitmap.createBitmap(thumb, 0, 0, width, height, matrix, true); 
			}
			
			if(thumb==null){
				return null;
			}else{
				FileOutputStream stream;
				try {
					File file = new File(Environment.getExternalStorageDirectory() + Constant.SD_STORAGE_ROOT + "/" + uid + "/pic/" + "tmp.jpg");
					if(file.exists()){
						file.delete();
					}
					
					if(!file.getParentFile().exists()){
						file.getParentFile().mkdirs();
					}
					
					stream = new FileOutputStream(file);
					thumb.compress(CompressFormat.JPEG, 50, stream);
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				File file = new File(Environment.getExternalStorageDirectory() + Constant.SD_STORAGE_ROOT + "/" + uid + "/pic/" + "tmp.jpg");
				if(file.exists()){
					return file.getAbsolutePath();
				}else{
					return null;
				}

			}
		}


		/**
		 * 
		 * @param handler
		 * @param responseType: 响应类型;
		 * @param cls: 响应对象的class类型;
		 */
		public PublishWeiboWorker(Handler handler) {
			this.handler = handler;
			this.gson = new Gson();
		}

		public void execute(Context c, String snstype, int cmd, String content, String picUrl, boolean needDownload) {
			// TODO Auto-generated method stub
			this.context = c;
			this.snstype = snstype;
			this.cmd = cmd;
			this.content = content;
			this.picUrl = picUrl;
			this.needDownload = needDownload;
			
			this.start();
		}

		@Override
		public void run(){
			String result = null;
			String snsid = null;
		
			try {

				ActiveAccount acct = ActiveAccount.getInstance(context);
				String uid = acct.getUID();
				ZLog.e(">> WeiboRequest (snstype:" + snstype + ", cmd:" + cmd + ", content:" + content + ", picUrl:" + picUrl +")");
				CmmobiSnsLib csl = CmmobiSnsLib.getInstance();
				
				if(snstype.equals("1")){
					
					// 本地图片就上传
					if(new File(picUrl).exists()){
						result = csl.sinaUploadPicText(content, picUrl, null, null);
					}else{
						result = csl.sinaUploadUrlText(content, picUrl, null, null);
					}
/*					if(!needDownload){
						result = csl.sinaUploadUrlText(content, picUrl, null, null);
					}else{
						String picPath = fetchPic(picUrl);
						if(picPath==null){
							result = csl.sinaUploadUrlText(content, picUrl, null, null);
						}else{
							result = csl.sinaUploadPicText(content, picPath, null, null);
						}

					}*/

					snsid = csl.getSinaWeiboUserId();
					
				}else if(snstype.equals("2")){
					String ugc_id = null;
					// 本地图片就上传
					if(new File(picUrl).exists()){
						
						ugc_id = csl.renrenUploadPicText(content, picUrl);
						result = ugc_id;
						
					}else{
						String picPath = fetchPic(picUrl);
						if(picPath==null){
							/*						ugc_id = csl.renrenUploadUrlText(content, picUrl);
						if(ugc_id!=null){
							result = csl.renrenPutUGC(content, ugc_id, "TYPE_SHARE");
						}*/
						}else{
							ugc_id = csl.renrenUploadPicText(content, picPath);
							result = ugc_id;
							/*						if(ugc_id!=null){
							result = csl.renrenPutUGC(content, ugc_id, "TYPE_PHOTO");
						}*/
						}
					}

					
					snsid = csl.getRenrenWeiboUserId();
					
				}else if(snstype.equals("6")){
					AccountInfo uPI = AccountInfo.getInstance(uid);
					// 本地图片就上传
					if(new File(picUrl).exists()){
						
						result = csl.twUploadUrlText(content, CommonInfo.getInstance().ip, picUrl);
					}else{
						
						if(!needDownload){
							result = csl.twUploadUrlText(content, CommonInfo.getInstance().ip, picUrl);
						}else{
							String picPath = fetchPic(picUrl);
							result = csl.twUploadUrlText(content, CommonInfo.getInstance().ip, picPath);
						}
						
					}
					
					snsid = csl.getTencentWeiboUserId();

				}
				
				//ZLog.e("<< WeiboResponse (" + cls.getSimpleName() + "): " + result);
				
			} catch (Exception e) {
				e.printStackTrace();
			}

			if (handler != null) {
				Message message = handler.obtainMessage(cmd, result);
				if(snsid!=null){
					Bundle b = new Bundle();
					b.putString("snsid", snsid);
					message.setData(b);
				}

				handler.sendMessage(message);
			} else {
				ZLog.alert();
				ZLog.e("handler is null, data can not callback.");
			}
		
		}
		

	}
	
	
	/**
	 * ------------------------------------------------------------------------
	 * 内部类, 获取微博评论列表;
	 * 
	 * @author zhangwei
	 */
	private static class GetCommentWeiboWorker extends Thread {
		private Handler handler;
		private Class<?> cls;
		
		private Gson gson;
		
		Context context;
		String snstype;
		int cmd;
		String weiboid; //要发布的微博内容 <=140汉字
		String count; //要发布的网络图片
		String page;


		/**
		 * 
		 * @param handler
		 * @param responseType: 响应类型;
		 * @param cls: 响应对象的class类型;
		 */
		public GetCommentWeiboWorker(Handler handler, Class<?> cls) {
			this.handler = handler;
			this.gson = new Gson();
			this.cls = cls;
		}

		public void execute(Context c, String snstype, int cmd,
				String weiboid, String count, String page) {
			// TODO Auto-generated method stub
			this.context = c;
			this.snstype = snstype;
			this.cmd = cmd;
			this.weiboid = weiboid;
			this.count = count;
			this.page = page;
			
			this.start();
			
		}

		@Override
		public void run(){

			Object object = null;
			String result = null;
			
			try {

				ActiveAccount acct = ActiveAccount.getInstance(context);
				String uid = acct.getUID();
				//ZLog.e(">> WeiboRequest (snstype:" + snstype + " cmd:" + cmd + "uid:" + uid + ")");
				CmmobiSnsLib csl = CmmobiSnsLib.getInstance();
				
				if(snstype.equals("1")){
					result = csl.getSWCommentList(Long.valueOf(weiboid), 0, 0, Integer.valueOf(count), Integer.valueOf(page));
					object = gson.fromJson(result, cls);
				
				}else if(snstype.equals("2")){
					if(!csl.getRenrenWeiboUserId().equals(CmmobiSnsLib.WEIBOUSERIDNULL)){
						result = csl.getRWCommentList(csl.getRenrenWeiboUserId(),  weiboid, Integer.valueOf(page), Integer.valueOf(count));
						object = gson.fromJson(result, cls);
					}
					
					
				}else if(snstype.equals("6")){
					result = csl.getTWCommentList(weiboid, "0", "0", String.valueOf(count), "0");
					object = gson.fromJson(result, cls);
				}
				
				//ZLog.e("<< WeiboResponse (" + cls.getSimpleName() + "): " + result);
				
			} catch (Exception e) {
				e.printStackTrace();
			}

			if (handler != null) {
				Message message = handler.obtainMessage(cmd, object);
				handler.sendMessage(message);
			} else {
				ZLog.alert();
				ZLog.e("handler is null, data can not callback.");
			}
		
		}
		

	}
	
	/**
	 * ------------------------------------------------------------------------
	 * 内部类, 获取微博好友列表;
	 * 
	 * @author zhangwei
	 */
	private static class GetUserInfoWorker extends Thread {
		private Handler handler;
		private Class<?> cls;
		private Gson gson;
		
		Context context;
		String snstype;
		int cmd;
		private String[] ids;

		/**
		 * 
		 * @param handler
		 * @param responseType: 响应类型;
		 * @param cls: 响应对象的class类型;
		 */
		public GetUserInfoWorker(Handler handler, Class<?> cls) {
			this.handler = handler;
			this.cls = cls;
			this.gson = new Gson();
		}

		public void execute(Context c, String snstype, int cmd, String[] ids) {
			// TODO Auto-generated method stub
			this.context = c;
			this.snstype = snstype;
			this.cmd = cmd;
			this.ids = ids;
			
			this.start();
		}

		@Override
		public void run(){

			Object object = null;
			String result = null;
			


			try {
				
				ActiveAccount acct = ActiveAccount.getInstance(context);
				String uid = acct.getUID();//acct.snstype + "_" + acct.snsid;
				ZLog.e(">> WeiboRequest (snstype:" + snstype + " cmd:" + cmd + "uid:" + uid + ")");
				CmmobiSnsLib csl = CmmobiSnsLib.getInstance();
				
				if(snstype.equals("1")){
					
				}else if(snstype.equals("2")){
					if (!csl.getRenrenWeiboUserId().equals(CmmobiSnsLib.WEIBOUSERIDNULL)) {
						result = csl.batchRWUserInfo(ids);
						object = gson.fromJson(result, cls);
					}
					
				}else if(snstype.equals("6")){
					
				}
				
				ZLog.e("<< WeiboResponse (" + cls.getSimpleName() + "): " + result);
				
			} catch (Exception e) {
				e.printStackTrace();
			}

			if (handler != null) {
				Message message = handler.obtainMessage(cmd, object);
				handler.sendMessage(message);
			} else {
				ZLog.alert();
				ZLog.e("handler is null, data can not callback.");
			}
		
		}

	}
	
	/**
	 * ------------------------------------------------------------------------
	 * 内部类, 对指定的一条微博评论进行回复
	 * 
	 * @author zhangwei
	 */
	private static class ReplyCommentWeiboWorker extends Thread {
		private Handler handler;
		private Gson gson;
		
		Context context;
		String snstype;
		int cmd;

		String weiboID;
		String commentID;
		String content; //要发布的评论内容 <=140汉字
		
		/**
		 * 
		 * @param handler
		 * @param responseType: 响应类型;
		 * @param cls: 响应对象的class类型;
		 */
		public ReplyCommentWeiboWorker(Handler handler) {
			this.handler = handler;
			this.gson = new Gson();
		}

		public void execute(Context c, String snstype, int cmd, String weiboID, String commentID, String content) {
			// TODO Auto-generated method stub
			this.context = c;
			this.snstype = snstype;
			this.cmd = cmd;
			this.weiboID = weiboID;
			this.commentID = commentID;
			this.content = content;
			
			this.start();
		}

		@Override
		public void run(){
			String result = null;
			String snsid = null;
			boolean statusOK = true;
			try {
				ActiveAccount acct = ActiveAccount.getInstance(context);
				String uid = acct.getUID();
				
				ZLog.e(">> WeiboRequest (snstype:" + snstype + ", cmd:" + cmd + ",weiboID:" + weiboID + ", commentID:" + commentID + ", content:" + content + ")");
				
				CmmobiSnsLib csl = CmmobiSnsLib.getInstance();
				
				if(snstype.equals("1")){
					//snsid = csl.getSinaWeiboUserId();
					result = csl.replyToSinaComment(Long.valueOf(commentID), Long.valueOf(weiboID), content, false, false);

					if(result != null){
						WeiboError weiboError = JsonParseUtils.parseCommonWeiboError(result);
						if(weiboError == null){
							statusOK = false;
						}else{
							statusOK = weiboError.getErrorCode()==0;
						}
					}else{
						statusOK = false;
					}

				}else if(snstype.equals("2")){
					snsid = csl.getRenrenWeiboUserId();
					result = csl.replyToRwComment(Long.valueOf(snsid), Long.valueOf(weiboID), content);
					statusOK = true;
				}else if(snstype.equals("6")){
					result = csl.replyToTwComment(content, CommonInfo.getInstance().ip, commentID);
					if(result != null){
						WeiboError weiboError = JsonParseUtils.parseCommonWeiboError(result);
						if(weiboError == null){
							statusOK = false;
						}else{
							statusOK = weiboError.getErrorCode()==0;
						}
					}else{
						statusOK = false;
					}
				}
				
				ZLog.e("<< WeiboResponse :"  + result);
				
			} catch (Exception e) {
				e.printStackTrace();
			}

			if (handler != null) {
				Message message = handler.obtainMessage(cmd);
				message.obj = statusOK;
				handler.sendMessage(message);
			} else {
				ZLog.alert();
				ZLog.e("handler is null, data can not callback.");
			}
		
		}
		

	}
	
	
	/**
	 * ------------------------------------------------------------------------
	 * 内部类, 删除微博;
	 * 
	 * @author zhangwei
	 */
	private static class DelWeiboWorker extends Thread {
		private Handler handler;
		private Gson gson;
		
		Context context;
		String snstype;
		int cmd;
		String weiboID; //要删除的微博ID


		/**
		 * 
		 * @param handler
		 * @param responseType: 响应类型;
		 * @param cls: 响应对象的class类型;
		 */
		public DelWeiboWorker(Handler handler) {
			this.handler = handler;
			this.gson = new Gson();
		}

		public void execute(Context c, String snstype, int cmd, String weiboID) {
			// TODO Auto-generated method stub
			this.context =c;
			this.snstype = snstype;
			this.cmd = cmd;
			this.weiboID = weiboID;
			
			this.start();
		}

		@Override
		public void run(){
			boolean result = false;
			
			try {

				ActiveAccount acct = ActiveAccount.getInstance(context);
				String uid = acct.getUID();
				//ZLog.e(">> WeiboRequest (snstype:" + snstype + " cmd:" + cmd + "uid:" + uid + ")");
				CmmobiSnsLib csl = CmmobiSnsLib.getInstance();
				
				if(snstype.equals("1")){
					//result = csl.sinaUploadUrlText(content, picUrl, null, null);
					result = csl.sinaDel(weiboID);
					
				}else if(snstype.equals("2")){
					//result = csl.renrenUploadUrlText(content, picUrl);
					
				}else if(snstype.equals("6")){
					result = csl.twDel(weiboID);
				}
				
				//ZLog.e("<< WeiboResponse (" + cls.getSimpleName() + "): " + result);
				
			} catch (Exception e) {
				e.printStackTrace();
			}

			if (handler != null) {
				Message message = handler.obtainMessage(cmd, result);
				handler.sendMessage(message);
			} else {
				ZLog.alert();
				ZLog.e("handler is null, data can not callback.");
			}
		
		}


	}
	
	
	/**
	 * ------------------------------------------------------------------------
	 * 内部类, 获取微博评论数;
	 * 
	 * @author zhangwei
	 */
	private static class GetCountWeiboWorker extends Thread {
		private Handler handler;
		private Class<?> cls;
		
		private Gson gson;
		
		Context context;
		String snstype;
		int cmd;
		String[] weiboid; //要获取的微博IDString数组



		/**
		 * 
		 * @param handler
		 * @param responseType: 响应类型;
		 * @param cls: 响应对象的class类型;
		 */
		public GetCountWeiboWorker(Handler handler, Class<?> cls) {
			this.handler = handler;
			this.gson = new Gson();
			this.cls = cls;
		}

		public void execute(Context c, String snstype, int cmd, String[] weiboid) {
			// TODO Auto-generated method stub
			this.context = c;
			this.snstype = snstype;
			this.cmd = cmd;
			this.weiboid = weiboid;
			this.start();
		}
		
		

		@Override
		public void run(){

			Object object = null;
			String result = null;

			try {

				ActiveAccount acct = ActiveAccount.getInstance(context);
				String uid = acct.getUID();
				//ZLog.e(">> WeiboRequest (snstype:" + snstype + " cmd:" + cmd + "uid:" + uid + ")");
				CmmobiSnsLib csl = CmmobiSnsLib.getInstance();
				
				if(snstype.equals("1")){
					result = csl.countsOfSW(weiboid);
					object = gson.fromJson(result, cls);
				
				}else if(snstype.equals("2")){
					if(!csl.getRenrenWeiboUserId().equals(CmmobiSnsLib.WEIBOUSERIDNULL)){
						result = csl.countsOfRW(weiboid);
						object = gson.fromJson(result, cls);
					}
					
					
				}else if(snstype.equals("6")){
					StringBuilder weibo_str = new StringBuilder();
					if(weiboid.length>0){
						for(int i=0; i<weiboid.length; i++){
							weibo_str.append(weiboid[i]);
							if(i!=weiboid.length-1){
								weibo_str.append(",");
							}
						}
						result = csl.countsOfTW(weibo_str.toString(), "1");
						object = gson.fromJson(result, cls);
					}

				}
				
				//ZLog.e("<< WeiboResponse (" + cls.getSimpleName() + "): " + result);
				
			} catch (Exception e) {
				e.printStackTrace();
			}

			if (handler != null) {
				Message message = handler.obtainMessage(cmd, object);
				handler.sendMessage(message);
			} else {
				ZLog.alert();
				ZLog.e("handler is null, data can not callback.");
			}
		
		}
		
	}
	

	/**
	 *  @return -1 没有安装微信， 0 安装微信但版本过低， 1 ok
	 * */
	public static int isWXAppSupportAPI(Context context) {
		IWXAPI api = WeiboRequester.getInstance(context).api;//WXAPIFactory.createWXAPI(context, Constant.WX_APP_ID, false);

		int ret = 0;
		if(api.isWXAppInstalled()){
			if(api.isWXAppSupportAPI()){
				ret = 1;
			}else{
				ret = 0;
			}
		}else{
			ret = -1;
		}
		return ret;
	} 
	
	
	/**
	 *  @return true 支持朋友圈， 不支持朋友圈
	 * */
	public static boolean isSupportTimeline(Context context){
		//IWXAPI api = WXAPIFactory.createWXAPI(context, Constant.WX_APP_ID, false);
		IWXAPI api = WeiboRequester.getInstance(context).api;
		int wxSdkVersion = api.getWXAppSupportAPI();
		if (wxSdkVersion >= TIMELINE_SUPPORTED_VERSION) {
			return true;
		} else {
			return false;
		}
	}
	
	public void registerApp(Context context){
		IWXAPI api = WeiboRequester.getInstance(context).api;
		api.registerApp(Constant.WX_APP_ID);
	}

	public void handleIntent(Context context, Intent intent, IWXAPIEventHandler handler){
		IWXAPI api = WeiboRequester.getInstance(context).api;
		api.handleIntent(intent, handler);
	}
 	
	/**
	 * ------------------------------------------------------------------------
	 * 内部类, 微信;
	 * 
	 * @author zhangwei
	 */
	private static class WeixinWorker extends Thread {
		private Handler handler;
		private Context context;
		private String title;
		private String content;
		private String jumpurl;
		private String pic_url;
		private boolean sendTimeline;
		private int cmd;

		/**
		 * 
		 * @param handler
		 * @param responseType: 响应类型;
		 * @param cls: 响应对象的class类型;
		 */
		public WeixinWorker(Handler handler) {
			this.handler = handler;
		}

		public void execute(Context c, int cmd, String title, String content, String jumpurl, String pic_url, boolean sendTimeline) {
			// TODO Auto-generated method stub
			this.context = c;
			this.cmd = cmd;
			
			
			this.title = title;
			this.content = content;
			this.jumpurl = jumpurl;
			this.pic_url = pic_url;
			this.sendTimeline = sendTimeline;

			this.start();
		}
		
		

		@Override
		public void run(){

			Object object = null;
			String result = null;

			try {

				ActiveAccount acct = ActiveAccount.getInstance(context);
				String uid = acct.getUID();
				object = sendWeixinMsg(context, uid, title, content, pic_url, jumpurl, sendTimeline);

				Log.e(TAG, "WeixinWorker - run ret:" + object);
			} catch (Exception e) {
				e.printStackTrace();
			}

			if (handler != null) {
				Message message = handler.obtainMessage(cmd, object);
				handler.sendMessage(message);
			} else {
				ZLog.alert();
				ZLog.e("handler is null, data can not callback.");
			}
		
		}
		
		public  boolean sendWeixinMsg(Context context, String uid, String title, String content, String pic_url, String jump_url, boolean sendTimeline){
			boolean ret = false;
			
			//IWXAPI api = WXAPIFactory.createWXAPI(context, Constant.WX_APP_ID, false);
			IWXAPI api = WeiboRequester.getInstance(context).api;
			api.registerApp(Constant.WX_APP_ID); 
			
			WXWebpageObject webpage = new WXWebpageObject();
			if(jump_url==null){
				jump_url = "http://www.looklook.com";
			}
			webpage.webpageUrl = jump_url;
			WXMediaMessage msg = new WXMediaMessage(webpage);
			msg.title = title;
			msg.description = content;
			
			Bitmap thumb = null;
			MediaValue mv = AccountInfo.getInstance(uid).mediamapping.getMedia(uid, pic_url);
			if(MediaValue.checkMediaAvailable(mv, 2)){
				try {
					File file = new File(Environment.getExternalStorageDirectory() + mv.localpath);
					thumb = BitmapHelper.getBitmapFromInputStream(new FileInputStream(file));
					
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();

				}
			}
			
			if(thumb==null){
				ZHttp2 http2 = new ZHttp2();
				ZHttpResponse httpResponse = http2.get(pic_url);

				thumb = BitmapHelper.getBitmapFromInputStream(httpResponse.getInputStream());
	
				//thumb = Bitmap.createBitmap(thumb, 0, 0, 50, 50);
			}
			
			if(thumb!=null){
				Matrix matrix = new Matrix();         
				int width = thumb.getWidth();// 获取资源位图的宽         
				int height = thumb.getHeight();// 获取资源位图的高         
				float w = (float) (50.0 / thumb.getWidth());         
				float h = (float) (50.0 / thumb.getHeight());  
				Log.e(TAG, "run - thumb width:" + width + ", height:" + height + ", w:" + w + ", h:" + h);
				matrix.postScale(w, h);// 获取缩放比例                     
				// 根据缩放比例获取新的位图      
				thumb = Bitmap.createBitmap(thumb, 0, 0, width, height, matrix, true); 
				msg.setThumbImage(thumb);
			}

			
			//Bitmap thumb = BitmapFactory.decodeResource(getResources(), R.drawable.send_music_thumb_backup);

			SendMessageToWX.Req req = new SendMessageToWX.Req();
			req.transaction = buildTransaction("webpage");
			req.message = msg;
			if(sendTimeline){
				req.scene = SendMessageToWX.Req.WXSceneTimeline;
			}else{
				req.scene = SendMessageToWX.Req.WXSceneSession;
			}
			
			ret = api.sendReq(req);
			
			return ret;
		}
		
		private String buildTransaction(final String type) {
			return (type == null) ? String.valueOf(System.currentTimeMillis()) : type + System.currentTimeMillis();
		}
		
	}
}