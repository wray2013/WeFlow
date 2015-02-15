package com.cmmobi.looklook.info.profile;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import com.cmmobi.looklook.MainApplication;
import com.cmmobi.looklook.common.gson.GsonResponse2;
import com.cmmobi.looklook.common.gson.GsonResponse2.MyBind;
import com.cmmobi.looklook.common.gson.GsonResponse2.MyDiary;
import com.cmmobi.looklook.common.gson.WeiboResponse.RWUser;
import com.cmmobi.looklook.common.gson.WeiboResponse.RWUserInfo;
import com.cmmobi.looklook.common.gson.WeiboResponse.SWUserInfo;
import com.cmmobi.looklook.common.gson.WeiboResponse.TWUserInfo;
import com.cmmobi.looklook.common.gson.WeiboResponse.sinaUser;
import com.cmmobi.looklook.common.gson.WeiboResponse.tencentInfo;
import com.cmmobi.looklook.common.storage.AsyncAccountInfoSaver;
import com.cmmobi.looklook.common.storage.StorageManager;
import com.cmmobi.looklook.fragment.WrapUser;
import com.cmmobi.looklook.info.weather.MyWeather;
import com.cmmobi.looklook.networktask.CacheNetworkTask;
import com.cmmobi.looklook.networktask.DownloadNetworkTask;
import com.cmmobi.looklook.networktask.INetworkTask;
import com.cmmobi.looklook.networktask.UploadNetworkTask;
import com.cmmobi.looklook.offlinetask.TaskData;
import com.cmmobi.sns.api.CmmobiSnsLib;
import com.google.gson.Gson;

/**
 * 对应账户数据固化, 根据uid来定位对应用户的账户信息，基于storageManager;
 * 
 * @author zhangwei
 * */
public class AccountInfo  {
	private static transient final String AccountInfoKey = "AccountInfoKey_";// 需要确保全局唯一性
	private static transient AccountInfo ins = null;
	private static transient final String TAG = "AccountInfo";


	private transient Handler handler;
	// private static transient Context context = null;

	/**********************************************************/
	public String uid;
	public String nickname;
	public String sex; // 0男 1女 2未知
	public String birthdate;
	public String address;
	public String mood;
	public String headimageurl; // 头像地址
	public String signature; //个人签名
	public String userid; // 用户ID,若为空则说明没有登录
	public String app_downloadurl;
	public String watermark;
	
	public String zoneBackGround;//空间封面
	public String attendedCount;//关注数
	public String fansCount;//粉丝数
	
	public MyWeather myWeather;//天气
	
	// 多账号关联
	public ArrayList<GsonResponse2.MyBind> logins;
	// bind用
	//public SparseArray<OAuthV2> oAuthV2Map;
	
	public TWUserInfo twInfo;
	public SWUserInfo swInfo;
	public RWUserInfo rwInfo;
	//public RWUser rwInfo;

	public DiaryDataEntities dataEntities;
	public List<TaskData> taskDataList;
	
	// 日记发私信给最近联系人
	public ArrayList<WrapUser> recentContactsList = new ArrayList<WrapUser>();
	
	/**
	 * 封面背景缓存
	 */
	public ArrayList<Map<String, String>> backCoverPicCash;

	/**
	 * 
	 * 腾讯好友列表
	 **/
	public ArrayList<tencentInfo> tencent_friends;

	/**
	 * 
	 * 扫描时间（完成）
	 **/
	public long tencent_scan_time;
	
	public int tencent_scan_bool;

	/**
	 * 
	 * renren好友列表
	 **/
	public ArrayList<RWUser> renren_friends;

	/**
	 * 
	 * 扫描时间（完成）
	 **/
	public long renren_scan_time;
	
	public int renren_scan_bool;

	/**
	 * 
	 * 新浪好友列表
	 **/
	public ArrayList<sinaUser> sina_friends;

	/**
	 * 
	 * 扫描时间（完成）
	 **/
	public long sina_scan_time;
	
	public int sina_scan_bool;

	/**
	 * 媒体映射表
	 * */
	public MediaMapping mediamapping;
	
	/**
	 *  上传/下载任务
	 * */
	public ArrayList<NetworkTaskInfo> tasks;
	/**
	 * 设置页数据
	 */
	public LoginSettingManager setmanager;
	
	/**
	 * @好友
	 */
	public ArrayList<RWUser> renrenFriendsList;
	public ArrayList<sinaUser> sinaFriendsList;
	public ArrayList<tencentInfo> tencentFriendsList;
	public ArrayList<RWUser> renrenRecentFriendsList;
	public ArrayList<sinaUser> sinaRecentFriendsList;
	public ArrayList<tencentInfo> tencentRecentFriendsList;
	/**
	 * 朋友圈
	 */
	public FriendsCircleManager friendsCircleManager;
	public FriendsFunsManager friendsFunsManager;
	public FriendsAddManager friendsAddManager;
	public FriendsAttentionManager friendsAttentionManager;
	public FriendsBlackListManager friendsBlackListManager;
	public ActivityListManager activityListManager;
	public ActivitiesDiariesManager activitiesDiariesManager;
	
	public ContactManager attentionContactManager;// 关注列表管理
	public ContactManager blackListContactManager;// 黑名单列表管理
	public OfficialUseridsManager officialUseridsManager; // 官方用户列表
	
	/**
	 *  消息，私信
	 *  
	 *  PrivateMessageManager -> messages (HashMap<String, MessageWrapper>)
	 *     MessageWrapper -> msgs (LinkedList<PrivateCommonMessage>); //私信
	 *        PrivateCommonMessage -> MyMessage r_msg + PrivateSendMessage s_msg
	 *             MyMessage -> PrivMsg privmsg 
	 *                    PrivMsg -> content + audiourl + MyDiary diaries
	 *                    
	 *             PrivateSendMessage -> content + audiourl
	 *  
	 * */
	public PrivateMessageManager privateMsgManger;
	public ActivityDetailAwardManager activityDetailAwardManager;
	/**
	 * 粉丝
	 */
	public String latestReqTime_fans;
	
	/**
	 * 关注
	 */
	public String latestReqTime_attention;
	/**********************************************************/
	
	/**
	 * 评论
	 */
	public ArrayList<GsonResponse2.diarycommentlistItem> commentList;
	
	public MyDiary myDiary;

	private AccountInfo() {

		mediamapping = new MediaMapping();
		logins = new ArrayList<GsonResponse2.MyBind>();
		dataEntities=new DiaryDataEntities();
		setmanager = new LoginSettingManager();
		tasks = new ArrayList<NetworkTaskInfo>();
		taskDataList=Collections.synchronizedList(new LinkedList<TaskData>());
		friendsCircleManager = new FriendsCircleManager();
		friendsFunsManager = new FriendsFunsManager();
		friendsAttentionManager = new FriendsAttentionManager();
		friendsBlackListManager = new FriendsBlackListManager();
		attentionContactManager = new ContactManager();
		blackListContactManager = new ContactManager();
		officialUseridsManager = new OfficialUseridsManager();
		friendsAddManager = new FriendsAddManager();
		activityListManager = new ActivityListManager();
		activitiesDiariesManager = new ActivitiesDiariesManager();
		
		privateMsgManger = new PrivateMessageManager();
		activityDetailAwardManager = new ActivityDetailAwardManager();
		backCoverPicCash = new ArrayList<Map<String, String>> ();
		
		commentList = new ArrayList<GsonResponse2.diarycommentlistItem>();
		myDiary = new MyDiary();
	}

	/**
	 * @author zhangwei {@literal 得到uid用户对应的AccountInfo实例，会尝试从磁盘中加载数据信息}
	 * */
	/*
	 * public static AccountInfo getInstance( String snstype, String snsid) {
	 * return getInstance(MainApplication.getAppInstance(), snstype + "_" +
	 * snsid); }
	 */

	/**
	 * @author zhangwei {@literal 得到uid用户对应的AccountInfo实例，会尝试从磁盘中加载数据信息}
	 * */
	public static AccountInfo getInstance(String uid) {
		return getInstance(MainApplication.getAppInstance(), uid);
	}

	private static AccountInfo getInstance(Context c, String uid) {
		// context = c;
		if (uid == null || uid.equals("")) {
			Log.e(TAG, "getInstance - uid is null");
			Exception e = new Exception();
			e.printStackTrace();

			uid = ActiveAccount.getInstance(c).getLookLookID();
		}

		// 检查uid是否一致，若一致则命中，直接返回ins, 否则说明切换用户
		if (ins == null || (uid!=null && !uid.equals(ins.uid))) {
			if (ins != null && !uid.equals(ins.uid)) {
/*				Exception e = new Exception();
				e.printStackTrace();*/
				Log.e("AccountInfo", "ins.uid!=uid " + ins.uid + " uid" + uid);
			}
			AccountInfo a = (AccountInfo) StorageManager.getInstance().getItem(
					AccountInfoKey + uid, AccountInfo.class);

			if (a != null) {
				ins = a;
			} else {
				ins = new AccountInfo();
				ins.uid = uid;
			}
		}

		return ins;
	}

	public void updateNetworkTaskInfo(List<NetworkTaskInfo> info) {
		if(info != null) {
			tasks.clear();
			for(int i=0; i<info.size(); i++) {
				tasks.add(info.get(i));
			}
		}
	}
//	
	public List<INetworkTask> readNetworkTaskInfo() {
		List<INetworkTask> t = new ArrayList<INetworkTask>();
		for(NetworkTaskInfo i : tasks) {
			if(i.taskType.equals(INetworkTask.TASK_TYPE_DOWNLOAD)) {
				t.add(new DownloadNetworkTask(i));
			} else if(i.taskType.equals(INetworkTask.TASK_TYPE_UPLOAD)) {
				t.add(new UploadNetworkTask(i));
			} else if(i.taskType.equals(INetworkTask.TASK_TYPE_CACHE)) {
				t.add(new CacheNetworkTask(i));
			}
		}
		return t;
	}
	
	/**                授权、登录、绑定、解绑相关流程
	 * 
	 * 备注： CmmobiSnsLib中的oAuthV2Map，操作微博api的数据结构
	 *       accoutinfo中的logins， 用于持久化授权信息
	 * 
	 * 1. 程序启动，加载accoutinfo。CmmobiSnsLib的构造中会检查登录状态，若:
	 * 1) 已登录，则将accountinfo的logins复制到CmmobiSnsLib中的oAuthV2Map
	 * 2）未登录，直接新建空的
	 * 
	 * 2. 已登录时， binding操作，授权成功后
	 * 1)把授权信息存储到CmmobiSnsLib的oAuthV2Map里
	 * 2)把授权信息存储（更新）到 accountinfo的logins里
	 * 
	 * 3. 未登录，登录操作，成功后先记在AccessTokenKeeper.tmp中（这时没有accountinfo），等登录成功后调用 ActiveAccount中updateLogin：
	 * 1） 将response中的binding信息同步到oAuthV2Map和logins
	 * 2） （调用该函数）将AccessTokenKeeper.tmp同步到logins
	 * 
	 * 4. 解绑，等待服务器返回解绑成功后：
	 * 1） 移除accountinfo中logins里对应CmmobiAccessToken
	 * 2） 移除 CmmobiSnsLib中的oAuthV2Map对应的OAuthV2
	 * */
	public void updateAccessToken(GsonResponse2.MyBind at){

		if (at != null) {
			Iterator<GsonResponse2.MyBind> it = logins.iterator();
			while(it.hasNext()){
				GsonResponse2.MyBind a = it.next();
				if ("3".equals(at.binding_type) && at.binding_type.equals(a.binding_type)) {
					if(at.snstype!=null && at.snstype.equals(a.snstype)){
						it.remove();
					}
				}else if ("1".equals(at.binding_type) && at.binding_type.equals(a.binding_type)) {
					it.remove();
				}else if ("2".equals(at.binding_type) && at.binding_type.equals(a.binding_type)) {
					if(at.binding_info!=null && at.binding_info.equals(a.binding_info)){
						it.remove();
					}
				}
			}			
			logins.add(at);
		}

	}
	

	public GsonResponse2.MyBind readAccessToken(String binding_type, String binding_info, String snstype) {
		 Iterator<GsonResponse2.MyBind> it = logins.iterator();
		 while (it.hasNext()) {
			 GsonResponse2.MyBind item = it.next();
			 if ("3".equals(binding_type) && binding_type.equals(item.binding_type)) {
				 if(snstype!=null && snstype.equals(item.snstype)){
					return item; 
				 }

			 }else if ( "1".equals(binding_type) && binding_type.equals(item.binding_type)) {
				 return item;

			 }else if ( "2".equals(binding_type) && binding_type.equals(item.binding_type)) {
				 if(binding_info!=null && binding_info.equals(item.binding_info)){
					 return item;
				 }

			 }
		 }

		return null;
	}
	
	public void removeAccessToken(String binding_type, String binding_info, String snstype) {
		 Iterator<GsonResponse2.MyBind> it = logins.iterator();
		 while (it.hasNext()) {
			 GsonResponse2.MyBind item = it.next();
			 if ("3".equals(binding_type) && binding_type.equals(item.binding_type)) {
				 if(snstype!=null && snstype.equals(item.snstype)){
					it.remove(); 
				 }

			 }else if ( ("1".equals(binding_type) || "2".equals(binding_type)) && binding_type.equals(item.binding_type)) {
				 if(binding_info==null){
					it.remove(); 
				 }else if(binding_info.equals(item.binding_info)){
					 it.remove(); 
				 }

			 }
		 }
	}

/**
	 * 
	 *  {@literal 将openid/snsid得到最新的TwInfo并覆盖到AccountInfo, 
	 *  前提：必须先通过授权才行
	 *  @author zhangwei
	 *  
	 * */
	public SWUserInfo getSwInfo(String openid) {
		Gson gson = new Gson();
		SWUserInfo sn_object = null;
		CmmobiSnsLib mSnsLib = CmmobiSnsLib.getInstance();
		try {
			sn_object = gson.fromJson(
					mSnsLib.getSWUserInfo(Long.valueOf(openid)),
					SWUserInfo.class);

			if (sn_object != null && sn_object.id > 0) {
				swInfo = sn_object;
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// persist();

		return sn_object;
	}

/**
	 * 
	 *  {@literal 将openid/snsid得到最新的TwInfo并覆盖到AccountInfo, 
	 *  前提：必须先通过授权才行
	 *  @author zhangwei
	 *  
	 * */
	public TWUserInfo getTwInfo(String openid) {
		Gson gson = new Gson();
		TWUserInfo tc_object = null;
		CmmobiSnsLib mSnsLib = CmmobiSnsLib.getInstance();
		try {

			tc_object = gson
					.fromJson(mSnsLib.getTWUserInfo(), TWUserInfo.class);

			if (tc_object != null && tc_object.errcode == 0
					&& tc_object.data != null) {
				twInfo = tc_object;
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// persist();

		return tc_object;
	}

	/**
	 * 
	 * {@literal 将uid用户对应的AccountInfo数据保存磁盘，一般在onPause中调用}
	 * 
	 * @author zhangwei
	 * 
	 * */
	synchronized public void persist() {
		Log.e(TAG, "AccountInfo: " + uid + " persist!");
/*		try{
			StorageManager.getInstance().putItem(AccountInfoKey + uid, ins, AccountInfo.class);
		}catch(Exception e){
			e.printStackTrace();
		}*/
		AsyncAccountInfoSaver.getInstance().save(AccountInfoKey + uid, ins, AccountInfo.class);
		

	}

	/**
	 * @author zhangwei {@literal 清空uid用户对应AccountInfo数据}
	 * */
	synchronized public void clean() {
		StorageManager.getInstance().deleteItem(AccountInfoKey + uid);
	}

/*	synchronized public AccountInfo clone() {
		AccountInfo o = null;
		try {
			o = (AccountInfo) super.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}

		return o;
	}*/
	
	public void syncBinding(MyBind[] binding) {
		// TODO Auto-generated method stub
		if(binding!=null){
			//del first
			if(logins==null){
				logins = new ArrayList<GsonResponse2.MyBind>();
			}
			logins.clear();
			
			for(MyBind myBind : binding){
				if(myBind!=null){
					logins.add(myBind);
				}

			}

		}
		
	}




}
