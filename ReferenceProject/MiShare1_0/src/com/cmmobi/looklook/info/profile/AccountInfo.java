package com.cmmobi.looklook.info.profile;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

import com.cmmobi.looklook.MainApplication;
import com.cmmobi.looklook.common.gson.GsonResponse3;
import com.cmmobi.looklook.common.gson.GsonResponse3.MyBind;
import com.cmmobi.looklook.common.gson.GsonResponse3.MyCommentListItem;
import com.cmmobi.looklook.common.gson.GsonResponse3.diarycommentlistItem;
import com.cmmobi.looklook.common.gson.WeiboResponse.RWUser;
import com.cmmobi.looklook.common.gson.WeiboResponse.RWUserInfo;
import com.cmmobi.looklook.common.gson.WeiboResponse.SWUserInfo;
import com.cmmobi.looklook.common.gson.WeiboResponse.TWUserInfo;
import com.cmmobi.looklook.common.gson.WeiboResponse.sinaUser;
import com.cmmobi.looklook.common.gson.WeiboResponse.tencentInfo;
import com.cmmobi.looklook.common.storage.AsyncAccountInfoSaver;
import com.cmmobi.looklook.common.storage.StorageManager;
import com.cmmobi.looklook.common.utils.CountryBean;
import com.cmmobi.looklook.info.weather.MyWeather;
import com.cmmobi.looklook.misharetask.MiShareTask;
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
	public static transient final String AccountInfoKey = "AccountInfoKey_";// 需要确保全局唯一性
	private static transient AccountInfo ins = null;
	private static transient final String TAG = "AccountInfo";


	private transient Handler handler;
	// private static transient Context context = null;

	/**********************************************************/
	//public String uid;
	public String userid; // 用户ID,若为空则说明没有登录
	public String mishare_no; // 微享号
	
	public String nickname;
	public String sex; // 0男 1女 2未知
	public String birthdate;
	public String address;
//	public String mood;    // 登陆自动登陆无此参数（3.1）.
	public String headimageurl; // 头像地址
	public String signature; //个人签名

	public String app_downloadurl;
	public String watermark;
	
	public String zoneBackGround;//空间封面
	public String attendedCount;//关注数
	public String fansCount;//粉丝数
	public String personalsort="11,12";//首页菜单顺序
	
	public MyWeather myWeather;//天气
	public int weathertype;
	
	// 多账号关联
	public ArrayList<GsonResponse3.MyBind> logins;
	// bind用
	//public SparseArray<OAuthV2> oAuthV2Map;
	
	public TWUserInfo twInfo;
	public SWUserInfo swInfo;
	public RWUserInfo rwInfo;
	//public RWUser rwInfo;

	public DiaryDataEntities dataEntities;
	public List<TaskData> taskDataList;
	
	//微享缓存
	public VshareDataEntities vshareDataEntities;
	public VshareDataEntities vshareLocalDataEntities;
	public String vSharePublishId ;
	
	//意见反馈缓存
	public String feedback;
	
	
	// 常用国家列表
	public ArrayList<CountryBean> frequentCountry = new ArrayList<CountryBean>();// 常用位置列表
	
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
	public List<NetworkTaskInfo> tasks;
	
	/**
	 * 边下边播缓存文件信息
	 */
	public ArrayList<Map<String, String>> cachefileinfo;
	
	/**
	 *  微享上传任务
	 * */
	public ConcurrentLinkedQueue<MiShareTaskInfo> mitasks;
	
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

	
	//通讯录
	public ContactManager friendsListName; //朋友列表，按名称排序
	public ContactManager friendsRequestList; //新好友列表
	public ContactManager phoneUsers; //手机通讯录
	public Boolean isPhonePromt = false; //访问通讯录提示是否弹过
	public ContactManager recentContactManager; //最近联系人
	
	public WrapUser serviceUser;
	//通讯录请求时间(用于更新记录)
	public String t_friendsList = "";
	public String t_friendsRequestList = "";
	public String t_friendNews = "";

	//心跳记录
	public int newFriendChange; //是否有朋友动态
	public int newFriendRequestCount; //新好友个数
	public int newFriend; //是否有新好友
	public int newZoneMicCount; //新增微享数
	public int new_safeboxmicnum; //新增微享数
	
	public FriendNewsDataEntities friendNewsDataEntities;
	
/*	//微享
	public String t_zoneMicList;
	public String lt_zoneMicList;*/
	
	//分享记录
	//public ArrayList<HistoryList> myHistoryLists;
	
	//我发出的评论
	public ArrayList<MyCommentListItem> mySendComments;
	//我收到的评论
	public ArrayList<MyCommentListItem> myAcceptComments;
	//我收到的评论
	public ArrayList<MyCommentListItem> myAcceptSafeboxComments;
	//全部评论
	public ArrayList<diarycommentlistItem> allComments;
	
	// 缓存收到的评论的第1条记录时间
	public String accept_first_comment_time = "";
	
	public String accept_first_safebox_comment_time = "";
	// 缓存收到的评论的第1条记录时间
	public String send_first_comment_time = "";
	
	
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
//	public ArrayList<GsonResponse2.diarycommentlistItem> commentList;
	
//	public MyDiary myDiary;

	private AccountInfo() {

		mediamapping = new MediaMapping();
		logins = new ArrayList<GsonResponse3.MyBind>();
		dataEntities=new DiaryDataEntities();
		vshareDataEntities = new VshareDataEntities();
		vshareLocalDataEntities = new VshareDataEntities();
		friendNewsDataEntities = new FriendNewsDataEntities();
		setmanager = new LoginSettingManager();
		tasks = Collections.synchronizedList(new ArrayList<NetworkTaskInfo>());
		mitasks = new ConcurrentLinkedQueue<MiShareTaskInfo>();
		taskDataList=Collections.synchronizedList(new LinkedList<TaskData>());
//		friendsCircleManager = new FriendsCircleManager();

		//通讯录列表
		friendsListName = new ContactManager(); //朋友列表，按名称排序
		friendsRequestList = new ContactManager(); //好友请求列表
		phoneUsers = new ContactManager(); //手机通讯录
		recentContactManager = new ContactManager();
//		activityListManager = new ActivityListManager();
//		activitiesDiariesManager = new ActivitiesDiariesManager();
		
		privateMsgManger = new PrivateMessageManager(userid);
		activityDetailAwardManager = new ActivityDetailAwardManager();
		backCoverPicCash = new ArrayList<Map<String, String>> ();
		cachefileinfo = new ArrayList<Map<String, String>> ();
//		commentList = new ArrayList<GsonResponse2.diarycommentlistItem>();
//		myDiary = new MyDiary();
		serviceUser = new WrapUser();
		//myHistoryLists = new ArrayList<GsonResponse3.HistoryList>();
		
		mySendComments = new ArrayList<GsonResponse3.MyCommentListItem>();
		myAcceptComments = new ArrayList<GsonResponse3.MyCommentListItem>();
		myAcceptSafeboxComments = new ArrayList<GsonResponse3.MyCommentListItem>();
		allComments = new ArrayList<GsonResponse3.diarycommentlistItem>();
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

	private static AccountInfo getInstance(Context c, String userid) {
		// context = c;
		if (userid == null || userid.equals("")) {
			Log.e(TAG, "getInstance - uid is null");
			Exception e = new Exception();
			e.printStackTrace();

			userid = ActiveAccount.getInstance(c).getLookLookID();
		}

		// 检查uid是否一致，若一致则命中，直接返回ins, 否则说明切换用户
		if (ins == null || (userid!=null && !userid.equals(ins.userid))) {
			if (ins != null && !userid.equals(ins.userid)) {
/*				Exception e = new Exception();
				e.printStackTrace();*/
				Log.e("AccountInfo", "ins.userid!=userid " + ins.userid + " userid:" + userid);
			}
			AccountInfo a = (AccountInfo) StorageManager.getInstance().getItem(
					AccountInfoKey + userid, AccountInfo.class);

			if (a != null) {
				ins = a;
			} else {
				ins = new AccountInfo();
				ins.userid = userid;
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
			Log.d("==WR==", "Network persist in AccountInfo , size " + tasks.size() + ";userid = " + userid);
		}
	}
//	
	public List<INetworkTask> readNetworkTaskInfo() {
		List<INetworkTask> t = Collections.synchronizedList(new ArrayList<INetworkTask>());
		for(NetworkTaskInfo i : tasks) {
			if (i.state != INetworkTask.STATE_REMOVED) {
				if (i.taskType.equals(INetworkTask.TASK_TYPE_DOWNLOAD)) {
					t.add(new DownloadNetworkTask(i));
				} else if (i.taskType.equals(INetworkTask.TASK_TYPE_UPLOAD)) {
					t.add(new UploadNetworkTask(i));
				} else if (i.taskType.equals(INetworkTask.TASK_TYPE_CACHE)) {
					t.add(new CacheNetworkTask(i));
				}
			}
		}
		Log.d("==WR==", "Network read from AccountInfo , size " + tasks.size());
		return t;
	}
	
	public void updateMiShareTaskInfo(ConcurrentLinkedQueue<MiShareTaskInfo> info) {
		if(info != null) {
			mitasks.clear();
			/*for(int i=0; i<info.size(); i++) {
				mitasks.add(info.get(i));
			}*/
			synchronized (info) {
				Iterator<MiShareTaskInfo> it = info.iterator();
				while(it.hasNext()) {
					mitasks.add(it.next());
				}
			}
			Log.d("==WR==", "MiShare tasks persist in AccountInfo , size " + mitasks.size());
		}
	}
	
	public ConcurrentLinkedQueue<MiShareTask> readMiShareTaskInfo() {
		ConcurrentLinkedQueue<MiShareTask> t = new ConcurrentLinkedQueue<MiShareTask>();
		for(MiShareTaskInfo i : mitasks) {
			if (i.state != MiShareTask.MISHARE_TASK_STATE_REMOVED) {
				t.add(new MiShareTask(i));
			}
		}
		Log.d("==WR==", "MiShare tasks read from AccountInfo , size " + mitasks.size());
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
	public void updateAccessToken(GsonResponse3.MyBind at){

		if (at != null) {
			Iterator<GsonResponse3.MyBind> it = logins.iterator();
			while(it.hasNext()){
				GsonResponse3.MyBind a = it.next();
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
	

	public GsonResponse3.MyBind readAccessToken(String binding_type, String binding_info, String snstype) {
		 Iterator<GsonResponse3.MyBind> it = logins.iterator();
		 while (it.hasNext()) {
			 GsonResponse3.MyBind item = it.next();
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
		 Iterator<GsonResponse3.MyBind> it = logins.iterator();
		 while (it.hasNext()) {
			 GsonResponse3.MyBind item = it.next();
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
		Log.e(TAG, "AccountInfo: " + userid + " persist!");
//		try{
//			StorageManager.getInstance().putItem(AccountInfoKey + userid, ins, AccountInfo.class);
//		}catch(Exception e){
//			e.printStackTrace();
//		}
		AccountInfo tmp = ins;
		AsyncAccountInfoSaver.getInstance().save(AccountInfoKey + userid, tmp, AccountInfo.class);
	}
	
	synchronized public void cleanMemoryCache(){
		ins = null;
		System.gc();
	}

	/**
	 * @author zhangwei {@literal 清空uid用户对应AccountInfo数据}
	 * */
	synchronized public void clean() {
		StorageManager.getInstance().deleteItem(AccountInfoKey + userid);
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
				logins = new ArrayList<GsonResponse3.MyBind>();
			}
			logins.clear();
			
			for(MyBind myBind : binding){
				if(myBind!=null){
					logins.add(myBind);
				}

			}

		}
		
	}

	public void updateLogin() {
		// TODO Auto-generated method stub
		//privateMsgManger = new PrivateMessageManager(userid);
		privateMsgManger.updateUser(userid);
	}


}
