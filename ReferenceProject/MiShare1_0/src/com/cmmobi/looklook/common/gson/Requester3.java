package com.cmmobi.looklook.common.gson;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.util.EntityUtils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import cn.zipper.framwork.core.ZApplication;
import cn.zipper.framwork.core.ZLog;
import cn.zipper.framwork.device.ZScreen;
import cn.zipper.framwork.device.ZSimCardInfo;
import cn.zipper.framwork.io.network.ZHttp;
import cn.zipper.framwork.io.network.ZHttp2;
import cn.zipper.framwork.io.network.ZHttpReader;
import cn.zipper.framwork.io.network.ZHttpResponse;
import cn.zipper.framwork.io.network.ZNetworkStateDetector;
import cn.zipper.framwork.utils.MD5Util;
import cn.zipper.framwork.utils.ZStringUtils;
import cn.zipper.framwork.utils.ZThread;

import com.cmmobi.looklook.Config;
import com.cmmobi.looklook.MainApplication;
import com.cmmobi.looklook.common.constant.Constant;
import com.cmmobi.looklook.common.gson.GsonRequest3.AddrBook;
import com.cmmobi.looklook.common.gson.GsonRequest3.Attachs;
import com.cmmobi.looklook.common.gson.GsonRequest3.Deletedmsg;
import com.cmmobi.looklook.common.gson.GsonRequest3.Invate_users;
import com.cmmobi.looklook.common.gson.GsonRequest3.SNS;
import com.cmmobi.looklook.common.gson.GsonRequest3.postFollowItem;
import com.cmmobi.looklook.common.gson.GsonResponse3.UserObj;
import com.cmmobi.looklook.common.gson.GsonResponse3.createStructureResponse;
import com.cmmobi.looklook.common.gson.GsonResponse3.mergerAccountResponse;
import com.cmmobi.looklook.common.utils.MetaUtil;
import com.cmmobi.looklook.info.profile.AccountInfo;
import com.cmmobi.looklook.info.profile.ActiveAccount;
import com.cmmobi.looklook.info.profile.CommonInfo;
import com.cmmobi.looklook.info.profile.WrapUser;
import com.cmmobi.sns.api.CmmobiSnsLib;
import com.cmmobi.sns.oauthv2.OAuthV2;
import com.cmmobi.sns.utils.BitmapHelper;
import com.cmmobi.sns.utils.ConfigUtil.SHARE_TO;
import com.cmmobi.statistics.CmmobiClickAgent;
import com.google.gson.Gson;

/**
 * @author wangrui
 */

@SuppressWarnings("unused")
public final class Requester3 {

	public static final String REQUEST_HEADER = "requestapp=";

	//===2.1===
	
	//===2.2===
	
	//===2.3===
	
	//===2.4===
	public static final String RIA_INTERFACE_CONFIGINFO = "/user/configInfo";
	//===2.5===
	public static final String RIA_INTERFACE_TAGLIST = "/diary/taglist";
	public static final String RIA_INTERFACE_GET_SPACECOVER_LIST = "/getSpaceCoverList";
	public static final String RIA_INTERFACE_GET_SOCKET = "/user/getSocket";
	public static final String RIA_INTERFACE_FEEDBACK = "/feedback";
	public static final String RIA_INTERFACE_POST_SNSFRIEND = "/user/postSNSFriend";
	public static final String RIA_INTERFACE_REPORT = "/report";
	public static final String RIA_INTERFACE_GET_EFFECTS = "/getEffects";
	public static final String RIA_INTERFACE_GET_WEATHER = "/weather";
	public static final String RIA_INTERFACE_DEFAULT_PROMPTMSG = "/user/defaultPromptMsg";
	public static final String RIA_INTERFACE_POST_ADDRESSBOOK = "/user/postAddressBook";
	public static final String RIA_INTERFACE_CHANGE_USER_INFO = "/user/changeUserInfo";
	public static final String RIA_INTERFACE_PASSWORD_CHANGE = "/user/passwordChange";
	public static final String RIA_INTERFACE_MOOD = "/user/mood";
	public static final String RIA_INTERFACE_DIARY_PRIVACY = "/user/privacy";
	public static final String RIA_INTERFACE_AUTO_FRIEND = "/user/autofriend";
	public static final String RIA_INTERFACE_ADD_GESTURE_PASSWORD = "/user/addGesturepassword";
	public static final String RIA_INTERFACE_MY_PERSONAL_SORT = "/user/mypersonalsort";
	public static final String RIA_INTERFACE_SET_HEADIMAGE = "/user/setHeadImage";
	public static final String RIA_INTERFACE_MERGER_ACCOUNT = "/user/mergerAccount";
	//===2.6===
	public static final String RIA_INTERFACE_UA = "/ua";
	public static final String RIA_INTERFACE_LOGIN = "/user/login";
	public static final String RIA_INTERFACE_AUTOLOGIN = "/user/autoLogin";
	public static final String RIA_INTERFACE_REGISTER = "/user/register";
	public static final String RIA_INTERFACE_BINDING = "/binding";
	public static final String RIA_INTERFACE_CHANGE_BINDING = "/changeBinding";
	public static final String RIA_INTERFACE_VERIFY_SMS = "/user/verifySMS";
	public static final String RIA_INTERFACE_UNBIND = "/unbind";
	public static final String RIA_INTERFACE_CHECK_USERNAME = "/user/checkUserNameExist";
	public static final String RIA_INTERFACE_CHECK_NICKNAME = "/user/checkNickNameExist";
	public static final String RIA_INTERFACE_CHECK_USER = "/user/checkuser";
	public static final String RIA_INTERFACE_FORGET_PASSWORD = "/user/forgetPassword";
	public static final String RIA_INTERFACE_CHECKNO = "/user/getCheckNo";
	public static final String RIA_INTERFACE_UNSAFEBOX = "/user/unSafebox";
	
	public static final String RIA_INTERFACE_UPLOAD_PICTURE = "/upload?";
	//===2.7===
	//2.7.1.1
	public static final String RIA_INTERFACE_LIST_MY_DIARY = "/listMyDiary";
	public static final String RIA_INTERFACE_HOME = "/user/home";
	public static final String RIA_INTERFACE_LIST_COLLECT_DIARY = "/listCollectDiary";
	public static final String RIA_INTERFACE_MY_COMMENTLIST = "/newCommentList";
	public static final String RIA_INTERFACE_DIARY_COMMENTLIST = "/diaryCommentList";
//	public static final String RIA_INTERFACE_LIST_ENJOY_DIARY = "/user/listEnjoyDiary";
	public static final String RIA_INTERFACE_ATTENTIONLIST = "/attentionList";
	public static final String RIA_INTERFACE_FRIENDLIST = "/friendList";
	public static final String RIA_INTERFACE_LIST_COLLECT_DIARYID = "/listCollectDiaryid";
	public static final String RIA_INTERFACE_FORWARD_DIARY_ID = "/user/listEnjoyDiary";
	public static final String RIA_INTERFACE_OTHER_LIST_DIARY = "/otherlistMyDiary";
	public static final String RIA_INTERFACE_OTHER_HOME = "/user/otherhome";
	public static final String RIA_INTERFACE_CREATE_STRUCTURE = "/structureManager";
	public static final String RIA_INTERFACE_ADD_COLLECT_DIARY = "/collect/addcollectDiary";
	public static final String RIA_INTERFACE_REMOVE_COLLECT_DIARY = "/collect/removeCollectDiary";
	public static final String RIA_INTERFACE_DELETE_DIARY = "/deleteDiary";
	public static final String RIA_INTERFACE_SAFEBOX = "/diary/safebox";
	public static final String RIA_INTERFACE_GET_DIARY_URL = "/getDiaryUrl";
	public static final String RIA_INTERFACE_DIARY_INFO = "/diaryInfo";
	public static final String RIA_INTERFACE_MODIFY_TAG = "/diary/modTagsOrPosition";
	public static final String RIA_INTERFACE_DAIRY_SHARE_INFO = "/diaryInfoPage";
	public static final String RIA_INTERFACE_DAIRY_SHARE_PERMISSIONS = "/diaryPermissions";
	public static final String RIA_INTERFACE_MY_PUBLISH_HISTORY = "/mypublishhistory";//"/user/mypublishhistory";
	public static final String RIA_INTERFACE_CLEAN_PUBLISH_HISTORY = "/user/cleanpublishdiary";
	public static final String RIA_INTERFACE_DIARY_PUBLISH = "/diary/publish";
	public static final String RIA_INTERFACE_CANCEL_PUBLISH = "/cancelpublish";//"/diary/cancelpublish";
	public static final String RIA_INTERFACE_CANCEL_SHARE = "/diary/cancelShare";
	public static final String RIA_INTERFACE_SHARE_DIARY = "/shareDiary";
	public static final String RIA_INTERFACE_SET_SPACECOVER = "/user/setUserSpaceCover";
	public static final String RIA_INTERFACE_FRIEND_NEWS_LIST="/friendDiaryList";
	//===2.8===
	public static final String RIA_INTERFACE_MY_ATTENTIONLIST = "/user/myattentionlist";
	public static final String RIA_INTERFACE_MY_FANS_LIST = "/user/myfanslist";
	public static final String RIA_INTERFACE_MY_FRIENDS_LIST = "/user/myfriendslist";
	public static final String RIA_INTERFACE_GET_OFFICIAL_USERIDS = "/getOfficialUserids";
	public static final String RIA_INTERFACE_MY_BLACK_LIST = "/user/myBlacklist";
	public static final String RIA_INTERFACE_GET_DIARY_ENJOY = "/getDiaryEnjoyUsers";
	public static final String RIA_INTERFACE_SEARCH_USER = "/user/searchUser";
	public static final String RIA_INTERFACE_LIST_USER_SNS = "/user/listUserSNS";
	public static final String RIA_INTERFACE_LIST_USER_RECOMMEND = "/user/listUserRecommend";
	public static final String RIA_INTERFACE_COMMENT = "/comment";
	public static final String RIA_INTERFACE_SEND_MESSAGE = "/user/sendMessage";
	public static final String RIA_INTERFACE_DIARY_ENJOY = "/diary/enjoy";
	public static final String RIA_INTERFACE_DIARY_REPOST = "/diary/repost";
	public static final String RIA_INTERFACE_DELETE_ENJOY_DIARY = "/diary/deleteEnjoyDiary";
	public static final String RIA_INTERFACE_DELETE_COMMENT = "/deleteComment";
	public static final String RIA_INTERFACE_ATTENTION = "/user/attention";
	public static final String RIA_INTERFACE_CANCEL_ATTENTION = "/user/cancelattention";
	public static final String RIA_INTERFACE_SET_USERALIAS = "/user/setUserAlias";
	public static final String RIA_INTERFACE_SET_BLACKLIST = "/user/operateBlacklist";
	public static final String RIA_INTERFACE_ADD_FRIEND = "/user/friendRequest";
	public static final String RIA_INTERFACE_AGREE_FRIEND = "/user/requestAgree";
	public static final String RIA_INTERFACE_DELETE_FRIEND = "/user/friendDelete";
	public static final String RIA_INTERFACE_FRIEND_REQUEST_LIST = "/user/friendRequestList";
	public static final String RIA_INTERFACE_CLEAN_RECOMMEND = "/user/cleanRecommend";
	//===2.9===
	public static final String RIA_INTERFACE_CREATEMIC = "/createmic";
	//2.9.2 微享列表
	public static final String RIA_INTERFACE_MYMICLIST = "/mymiclist";//"/user/mymiclist";
	public static final String RIA_INTERFACE_MICLIST = "/miclist";
	public static final String RIA_INTERFACE_MYMICINFO = "/mymicinfo";
	public static final String RIA_INTERFACE_MYSUBMICLIST = "/mysubmiclist"; //"/user/mysubmiclist";
	public static final String RIA_INTERFACE_CLEANMIC = "/cleanmic"; ///user/cleanmic";
	public static final String RIA_INTERFACE_SAFEBOXMIC = "/safeboxmic";
	public static final String RIA_INTERFACE_SETUNDISTURB = "/setundisturb";
	//===2.9.7===
	public static final String RIA_INTERFACE_READ_MSG = "/readmsg";
	//===2.10===
	public static final String RIA_INTERFACE_GET_ACTIVELIST = "/active/activeList";
	public static final String RIA_INTERFACE_DIARY_ACTIVELIST = "/active/activeDiaryList";
	public static final String RIA_INTERFACE_JOIN_ACTIVE = "/active/joinActive";
	public static final String RIA_INTERFACE_GET_AWARD_DIARYLIST = "/active/getAwardDiaryList";
	public static final String RIA_INTERFACE_CANCEL_ACTIVE = "/active/cancleActive";
	//===2.11===
	public static final String RIA_INTERFACE_LIST_MESSAGE = "/user/listMessage";
	public static final String RIA_INTERFACE_DELETE_MESSAGE = "/user/messageDelete";
	public static final String RIA_INTERFACE_LIST_HISTORY_MESSAGE = "/user/listHistoryMessage";
	public static final String RIA_INTERFACE_CRM_CALLBACK = "/crm/callback";
	public static final String RIA_INTERFACE_PLAY_PAGE = "/playPage";
	public static final String RIA_INTERFACE_PHONE_BOOK = "/user/phoneBook";
	public static final String RIA_INTERFACE_CUSTOMER = "/customer";
	
	//2.6.1.7 微享评论列表
	public static final String RIA_INTERFACE_MICCOMMENTLIST ="/micCommentList";
	
	//通讯录邀请好友加入”原来“
	public static final String RIA_INTERFACE_INVATEPHONEADDRESS = "/user/invatePhoneAddress";
	
	// =========================================================================================

	//===2.1===
	
	//===2.2===
	
	//===2.3===
	
	//===2.4===
	public static final int RESPONSE_TYPE_CONFIGINFO = 0xffeef005;
	//===2.5===
	public static final int RESPONSE_TYPE_TAGLIST = 0xfffff003;
	public static final int RESPONSE_TYPE_GET_SPACECOVER_LIST = 0xfffff004;
	public static final int RESPONSE_TYPE_GET_SOCKET = 0xfffff005;
	public static final int RESPONSE_TYPE_FEEDBACK = 0xfffff006;
	public static final int RESPONSE_TYPE_POST_SNSFRIEND = 0xfffff007;
	public static final int RESPONSE_TYPE_REPORT = 0xfffff008;
	public static final int RESPONSE_TYPE_GET_EFFECTS = 0xfffff009;
	public static final int RESPONSE_TYPE_GET_WEATHER = 0xfffff010;
	public static final int RESPONSE_TYPE_DEFAULT_PROMPTMSG = 0xfffff011;
	public static final int RESPONSE_TYPE_POST_ADDRESSBOOK = 0xfffff012;
	public static final int RESPONSE_TYPE_CHANGE_USER_INFO = 0xfffff013;
	public static final int RESPONSE_TYPE_PASSWORD_CHANGE = 0xfffff014;
	public static final int RESPONSE_TYPE_MOOD = 0xfffff015;
	public static final int RESPONSE_TYPE_DIARY_PRIVACY = 0xfffff06;
	
	public static final int RESPONSE_TYPE_ADD_GESTURE_PASSWORD = 0xfffff017;
	public static final int RESPONSE_TYPE_MY_PERSONAL_SORT = 0xfffff018;
	public static final int RESPONSE_TYPE_SET_HEADIMAGE = 0xfffff019;
	public static final int RESPONSE_TYPE_MERGER_ACCOUNT = 0xffff5020;
	//===2.6===
	public static final int RESPONSE_TYPE_UA = 0xfffff020;
	public static final int RESPONSE_TYPE_LOGIN = 0xfffff021;
	public static final int RESPONSE_TYPE_AUTOLOGIN = 0xfffff022;
	public static final int RESPONSE_TYPE_REGISTER = 0xfffff023;
	public static final int RESPONSE_TYPE_BINDING = 0xfffff024;
	public static final int RESPONSE_TYPE_UNBIND = 0xfffff025;
	public static final int RESPONSE_TYPE_CHECK_USERNAME = 0xfffff026;
	public static final int RESPONSE_TYPE_CHECK_NICKNAME = 0xfffff027;
	public static final int RESPONSE_TYPE_CHECK_USER = 0xfffff028;
	public static final int RESPONSE_TYPE_FORGET_PASSWORD = 0xfffff029;
	public static final int RESPONSE_TYPE_CHECKNO = 0xfffff030;
	//===2.7===
	//2.7.1.1
	public static final int RESPONSE_TYPE_LIST_MY_DIARY = 0xfffff031;
	public static final int RESPONSE_TYPE_HOME = 0xfffff032;
	public static final int RESPONSE_TYPE_LIST_COLLECT_DIARY = 0xfffff033;
	public static final int RESPONSE_TYPE_MY_COMMENTLIST = 0xfffff034;
	public static final int RESPONSE_TYPE_DIARY_COMMENTLIST = 0xfffff035;
//	public static final int RESPONSE_TYPE_LIST_ENJOY_DIARY = 0xfffff036;
	public static final int RESPONSE_TYPE_ATTENTIONLIST = 0xfffff037;
	public static final int RESPONSE_TYPE_FRIENDLIST = 0xfffff038;
	public static final int RESPONSE_TYPE_LIST_COLLECT_DIARYID = 0xfffff039;
	public static final int RESPONSE_TYPE_FORWARD_DIARY_ID = 0xfffff03a;
	public static final int RESPONSE_TYPE_CREATE_STRUCTURE = 0xfffff040;
	public static final int RESPONSE_TYPE_ADD_COLLECT_DIARY = 0xfffff041;
	public static final int RESPONSE_TYPE_REMOVE_COLLECT_DIARY = 0xfffff042;
	public static final int RESPONSE_TYPE_DELETE_DIARY = 0xfffff043;
	public static final int RESPONSE_TYPE_SAFEBOX = 0xfffff044;
	public static final int RESPONSE_TYPE_GET_DIARY_URL = 0xfffff045;
	public static final int RESPONSE_TYPE_DIARY_INFO = 0xfffff046;
	public static final int RESPONSE_TYPE_MODIFY_TAG = 0xfffff047;
	public static final int RESPONSE_TYPE_DIARY_SHARE_INFO = 0xfffff048;
	public static final int RESPONSE_TYPE_DIARY_SHARE_PERMISSIONS = 0xfffff04a;
	public static final int RESPONSE_TYPE_MY_PUBLISH_HISTORY = 0xfffff049;
	public static final int RESPONSE_TYPE_CLEAN_PUBLISH_HISTORY = 0xfffff050;
	public static final int RESPONSE_TYPE_DIARY_PUBLISH = 0xfffff051;
	public static final int RESPONSE_TYPE_CANCEL_PUBLISH = 0xfffff052;
	public static final int RESPONSE_TYPE_CANCEL_SHARE = 0xfffff053;
	public static final int RESPONSE_TYPE_SHARE_DIARY = 0xfffff054;
	//===2.8===
	public static final int RESPONSE_TYPE_MY_ATTENTIONLIST = 0xfffff055;
	public static final int RESPONSE_TYPE_MY_FANS_LIST = 0xfffff056;
	public static final int RESPONSE_TYPE_MY_FRIENDS_LIST = 0xfffff057;
	public static final int RESPONSE_TYPE_GET_OFFICIAL_USERIDS = 0xfffff058;
	public static final int RESPONSE_TYPE_MY_BLACK_LIST = 0xfffff059;
	public static final int RESPONSE_TYPE_GET_DIARY_ENJOY = 0xfffff060;
	public static final int RESPONSE_TYPE_SEARCH_USER = 0xfffff061;
	public static final int RESPONSE_TYPE_LIST_USER_SNS = 0xfffff062;
	public static final int RESPONSE_TYPE_LIST_USER_RECOMMEND = 0xfffff063;
	public static final int RESPONSE_TYPE_COMMENT = 0xfffff064;
	public static final int RESPONSE_TYPE_SEND_MESSAGE = 0xfffff065;
	public static final int RESPONSE_TYPE_DIARY_ENJOY = 0xfffff066;
	public static final int RESPONSE_TYPE_DIARY_REPOST = 0xfffff067;
	public static final int RESPONSE_TYPE_DELETE_ENJOY_DIARY = 0xfffff068;
	public static final int RESPONSE_TYPE_DELETE_COMMENT = 0xfffff069;
	public static final int RESPONSE_TYPE_ATTENTION = 0xfffff070;
	public static final int RESPONSE_TYPE_CANCEL_ATTENTION = 0xfffff071;
	public static final int RESPONSE_TYPE_SET_USERALIAS = 0xfffff072;
	public static final int RESPONSE_TYPE_SET_BLACKLIST = 0xfffff073;
	//===2.9===
	public static final int RESPONSE_TYPE_CREATEMIC = 0xfffff074;
	//2.9.2 微享列表
	public static final int RESPONSE_TYPE_MYMICLIST = 0xfffff075;
	public static final int RESPONSE_TYPE_MYMICINFO = 0xfffff076;
	public static final int RESPONSE_TYPE_MYSUBMICLIST = 0xfffff077;
	public static final int RESPONSE_TYPE_CLEANMIC = 0xfffff078;
	//===2.10===
	public static final int RESPONSE_TYPE_GET_ACTIVELIST = 0xfffff079;
	public static final int RESPONSE_TYPE_DIARY_ACTIVELIST = 0xfffff080;
	public static final int RESPONSE_TYPE_JOIN_ACTIVE = 0xfffff081;
	public static final int RESPONSE_TYPE_GET_AWARD_DIARYLIST = 0xfffff082;
	public static final int RESPONSE_TYPE_CANCEL_ACTIVE = 0xfffff083;
	//===2.11===
	public static final int RESPONSE_TYPE_LIST_MESSAGE = 0xfffff084;
	public static final int RESPONSE_TYPE_DELETE_MESSAGE = 0xfffff085;
	public static final int RESPONSE_TYPE_LIST_HISTORY_MESSAGE = 0xfffff086;
	public static final int RESPONSE_TYPE_CRM_CALLBACK = 0xfffff087;
	public static final int RESPONSE_TYPE_PLAY_PAGE = 0xfffff088;
	
	//==2.6.13=add==
	public static final int RESPONSE_TYPE_UNSAFEBOX = 0xfffff089;
	//==2.9.6=add==
	public static final int RESPONSE_TYPE_SAFEBOXMIC = 0xfffff090;
	//==2.9.7=add==
	public static final int RESPONSE_TYPE_READ_MSG = 0xffff0090;
	
	//==2.7.1.10=add==
	public static final int RESPONSE_TYPE_OTHER_LIST_DIARY=0xfffff090;
	public static final int RESPONSE_TYPE_OTHER_HOME=0xfffff0A0;
	// 2.8.2.12 
	public static final int RESPONSE_TYPE_ADD_FRIEND = 0xfffff0A1;
	public static final int RESPONSE_TYPE_AGREE_FRIEND = 0xfffff0A2;
	public static final int RESPONSE_TYPE_DELETE_FRIEND = 0xfffff0A3;
	//2.8.1.10
	public static final int RESPONSE_TYPE_FRIEND_REQUEST_LIST = 0xfffff0A4;
	
	public static final int RESPONSE_TYPE_UPLOAD_PICTURE = 0xfffff0A5;
	public static final int RESPONSE_TYPE_SET_SPACECOVER = 0xfffff0A6;
	
	public static final int RESPONSE_TYPE_REQUEST_NEWFRIENDS_LIST= 0xfffff0B0;
	public static final int RESPONSE_TYPE_FRIEND_NEWS_LIST= 0xfffff0B1;
	
	//2.6.1.7 微享评论列表
	public static final int RESPONSE_TYPE_MICCOMMENTLIST = 0xfffff0A7;
	
	//2.8.6 他人微享列表
	public static final int RESPONSE_TYPE_MICLIST = 0xfffff0A8;
	//免打扰
	public static final int RESPONSE_TYPE_SETUNDISTURB = 0xfffff0A9;
	// 2.5.5 切换手机绑定
	public static final int RESPONSE_TYPE_CHANGEBINDING = 0xfffff0AA;
	// 2.5.11 验证手机验证码
	public static final int RESPONSE_TYPE_VERIFYSMS = 0xfffff0AB;
	
	public static final int RESPONSE_TYPE_PHONE_BOOK = 0xfffff0AC;
	//2.11.7.0
	public static final int RESPONSE_TYPE_CUSTOMER = 0xfffff0AD;
	
	//清除新好友推荐
	public static final int RESPONSE_TYPE_CLEAN_RECOMMEND = 0xfffff0AE;
	
	public static final int RESPONSE_TYPE_INVATEPHONEADDRESS = 0xfffff0AF;
	
	public static final int RESPONSE_TYPE_AUTO_FRIEND = 0xfffff0B0;
	
	
	public static final String VALUE_EMPTY = "";
	public static String VALUE_IMEI = ZStringUtils.nullToEmpty(ZSimCardInfo.getIMEI());
	public static String VALUE_IMSI = ZStringUtils.nullToEmpty(ZSimCardInfo.getIMSI());
	public static String VALUE_MAC = ZStringUtils.nullToEmpty(ZSimCardInfo.getDeviceMac());
	public static final String VALUE_CLIENT_VERSION = "1_0_0";
	public static final String VALUE_WIFI = "WIFI";
	public static final String VALUE_GPRS = "GPRS";
	public static final String VALUE_3G = "3G";
	public static final String VALUE_CHANNEL_NUMBER = MetaUtil.getStringValue("CMMOBI_CHANNEL");
	public static final String VALUE_RESOLUTION = ZScreen.getWidth() + "x" + ZScreen.getHeight();
	public static String VALUE_DEVICE_TYPE = "android:" + ZSimCardInfo.getSystemReleaseVersion();
	public static final String VALUE_MOBILE_TYPE = "2";
	public static final String VALUE_SNS_TYPE = "0";
	public static final String VALUE_SEX_BOY = "0";
	public static final String VALUE_SEX_GIRL = "1";
	public static final String VALUE_SEX_UNKNOW = "2";
	public static final String VALUE_FILE_TYPE = ".mp4";

	private static final String TAG = "RIARequester";

	/**
	 * 禁止构造实例;
	 */
	private Requester3() {
	}

	// 需要Base64编码的字段: 用户名、用户昵称、用户密码、分享内容、评论内容、意见反馈、
	// 微博昵称、loc逆地理信息、19. videotitle 、水印：允许使用符号,标签名称,tag。

	//===2.1===
	
	//===2.2===
	
	//===2.3===
	
	//===2.4===
	//2.4.5 2.4.5 心跳配置及默认提示内容
	/**
	 * 2.4.5 心跳配置及默认提示内容
	 * @param handler
	 * @param diaryid
	 */
	public static void requestConfigInfo(Handler handler, String userid) {
		GsonRequest3.configInfo request = new GsonRequest3.configInfo();
		request.userid = userid;
		request.os = "2"; //android

		request.mac = VALUE_MAC;
		request.imei = VALUE_IMEI;

		Worker worker = new Worker(handler, RESPONSE_TYPE_CONFIGINFO, GsonResponse3.configInfoResponse.class);
		worker.execute(RIA_INTERFACE_CONFIGINFO, request);
	}
	
	//===2.5===
	//2.5.1 获取标签(缓存到本地，每次刷新到数据替换本地)
	/**
	 * 2.5.1 获取标签(缓存到本地，每次刷新到数据替换本地)
	 * @param handler
	 * @param diaryid
	 */
	public static void requestTagList(Handler handler, String diaryid) {
		GsonRequest3.taglistRequest request = new GsonRequest3.taglistRequest();
		request.userid = ActiveAccount.getInstance(ZApplication.getInstance())
				.getLookLookID();
		request.diaryid = diaryid;

		request.mac = VALUE_MAC;
		request.imei = VALUE_IMEI;

		Worker worker = new Worker(handler, RESPONSE_TYPE_TAGLIST, GsonResponse3.taglistResponse.class);
		worker.execute(RIA_INTERFACE_TAGLIST, request);
	}
	//2.5.2 获取空间背景图片列表
	/**
	 * 2.5.2 获取空间背景图片列表
	 * @param handler
	 * @param index
	 */
	public static void getSpaceCoverList(Handler handler, String index) {
		GsonRequest3.getSpaceCoverListRequest request = new GsonRequest3.getSpaceCoverListRequest();
		request.index = index;
		request.pagesize = "10000";
		request.userid = ActiveAccount.getInstance(ZApplication.getInstance())
				.getLookLookID();
		request.mac = VALUE_MAC;
		request.imei = VALUE_IMEI;

		Worker worker = new Worker(handler, RESPONSE_TYPE_GET_SPACECOVER_LIST,
				GsonResponse3.getSpaceCoverListResponse.class);
		worker.execute(RIA_INTERFACE_GET_SPACECOVER_LIST, request);
	}
	//2.5.3 获取Socket信息(IP、PORT)
	/**
	 * 2.5.3 获取Socket信息(IP、PORT)
	 * @param handler
	 */
	public static void requestSocketIp(Handler handler) {
		GsonRequest3.getSocketRequest request = new GsonRequest3.getSocketRequest();
		// request.filetype = VALUE_FILE_TYPE;
		request.userid = ActiveAccount.getInstance(ZApplication.getInstance())
				.getLookLookID();
		request.mac = VALUE_MAC;
		request.imei = VALUE_IMEI;

		Worker worker = new Worker(handler, RESPONSE_TYPE_GET_SOCKET,
				GsonResponse3.getSocketResponse.class);
		worker.execute(RIA_INTERFACE_GET_SOCKET, request);
	}
	//2.5.4 客户意见反馈
	/**
	 * 2.5.4 客户意见反馈
	 * @param handler
	 * @param feedbackContent
	 */
	public static void feedback(Handler handler, String feedbackContent) {
		GsonRequest3.feedbackRequest request = new GsonRequest3.feedbackRequest();
		request.commentcontent = feedbackContent;
		request.equipmentid = CommonInfo.getInstance().equipmentid;
		request.userid = ActiveAccount.getInstance(ZApplication.getInstance())
				.getLookLookID();

		request.mac = VALUE_MAC;
		request.imei = VALUE_IMEI;
		request.os = "android";
		request.phone_model = ZSimCardInfo.getDeviceName();

		Worker worker = new Worker(handler, RESPONSE_TYPE_FEEDBACK,
				GsonResponse3.feedbackResponse.class);
		worker.execute(RIA_INTERFACE_FEEDBACK, request);
	}
	//2.5.5 客户端抓取用户第三方互粉传送给服务器
	/**
	 * 2.5.5 客户端抓取用户第三方互粉传送给服务器
	 * @param handler
	 * @param snstype
	 * @param upload_type
	 * @param snsfriends
	 */
	public static void postSNSFriend(Handler handler, String snstype,
			String upload_type, postFollowItem[] snsfriends) {
		GsonRequest3.postSNSFriendRequest request = new GsonRequest3.postSNSFriendRequest();
		request.userid = ActiveAccount.getInstance(ZApplication.getInstance())
				.getLookLookID();
		request.snstype = snstype;
		request.upload_type = upload_type;
		request.snsfriends = snsfriends;
		request.mac = VALUE_MAC;
		request.imei = VALUE_IMEI;
		Worker worker = new Worker(handler, RESPONSE_TYPE_POST_SNSFRIEND,
				GsonResponse3.postSNSFriendResponse.class);
		worker.execute(RIA_INTERFACE_POST_SNSFRIEND, request);
	}
	//2.5.6 举报日记
	/**
	 * 2.5.6 举报日记
	 * @param handler
	 * @param diaryid
	 * @param content
	 */
	public static void report(Handler handler, String diaryid, String content) {
		GsonRequest3.reportRequest request = new GsonRequest3.reportRequest();
		request.diaryid = diaryid;
		request.mac = ZSimCardInfo.getDeviceMac();
		request.imei = ZSimCardInfo.getIMEI();
		request.content = content;
		request.userid = ActiveAccount.getInstance(ZApplication.getInstance())
				.getLookLookID();

		Worker worker = new Worker(handler, RESPONSE_TYPE_REPORT,
				GsonResponse3.reportResponse.class);
		worker.execute(RIA_INTERFACE_REPORT, request);
	}
	//2.5.7 获取特效下载地址
	/**
	 * 2.5.7 获取特效下载地址
	 * @param handler
	 * @param version
	 */
	public static void getEffects(Handler handler, String version){
		GsonRequest3.getEffectsRequest request = new GsonRequest3.getEffectsRequest();
		request.version = version;
		request.phone_type = "2";
		request.userid = ActiveAccount.getInstance(ZApplication.getInstance()).getLookLookID();
		request.mac = VALUE_MAC;
		request.imei = VALUE_IMEI;
		Worker worker = new Worker(handler, RESPONSE_TYPE_GET_EFFECTS, GsonResponse3.getEffectResponse.class);
		worker.execute(RIA_INTERFACE_GET_EFFECTS, request);
	}
	//2.5.8 获取天气
	/**
	 * 2.5.8 获取天气
	 * @param handler
	 * @param addresscode
	 */
	public static void getWeather(Handler handler, String addresscode) {
		GsonRequest3.getWeatherRequest request = new GsonRequest3.getWeatherRequest();
		request.addresscode = addresscode;
		request.userid = ActiveAccount.getInstance(ZApplication.getInstance())
				.getLookLookID();
		request.mac = VALUE_MAC;
		request.imei = VALUE_IMEI;
		Worker worker = new Worker(handler, RESPONSE_TYPE_GET_WEATHER,
				GsonResponse3.getWeatherResponse.class);
		worker.execute(RIA_INTERFACE_GET_WEATHER, request);
	}
	//2.5.9 默认提示内容(需要统计出都有哪些提示内容)
	/**
	 * 2.5.9 默认提示内容(需要统计出都有哪些提示内容)
	 * @param handler
	 * @param type
	 */
	public static void getDefaultPromptMsg(Handler handler, String type) {
		GsonRequest3.getdefaultPromptMsgRequest request = new GsonRequest3.getdefaultPromptMsgRequest();
		request.type = type;
		request.userid = ActiveAccount.getInstance(ZApplication.getInstance())
				.getLookLookID();
		request.mac = VALUE_MAC;
		request.imei = VALUE_IMEI;
		Worker worker = new Worker(handler, RESPONSE_TYPE_DEFAULT_PROMPTMSG,
				GsonResponse3.getdefaultPromptMsgResponse.class);
		worker.execute(RIA_INTERFACE_DEFAULT_PROMPTMSG, request);
	}
	//2.5.10 发送通讯录(通讯录与第三方互粉合并,需要确定传送的电话号码数量)
	/**
	 * 2.5.10 发送通讯录(通讯录与第三方互粉合并,需要确定传送的电话号码数量)
	 * @param handler
	 * @param address_book
	 */
	public static void postAddressBook(Handler handler, String userphone, AddrBook[] address_book) {
		GsonRequest3.postAddressBookRequest request = new GsonRequest3.postAddressBookRequest();
		request.userid = ActiveAccount.getInstance(ZApplication.getInstance())
				.getLookLookID();
		request.address_book = address_book;
		request.userphone = userphone;
		request.mac = VALUE_MAC;
		request.imei = VALUE_IMEI;

		Worker worker = new Worker(handler, RESPONSE_TYPE_POST_ADDRESSBOOK,
				GsonResponse3.postAddressBookResponse.class);
		worker.execute(RIA_INTERFACE_POST_ADDRESSBOOK, request);
	}
	
	/**
	 * 2.5.10   切换账号数据合并接口
	 * @param handler
	 * @param newUserid
	 * @param userid_beMerged
	 */
	public static void mergerAccount(Handler handler, String newUserid, String userid_beMerged) {
		GsonRequest3.mergerAccountRequest request = new GsonRequest3.mergerAccountRequest();
		request.userid = newUserid;
		request.userid_beMerged = userid_beMerged;
		request.mac = VALUE_MAC;
		request.imei = VALUE_IMEI;
		
		Worker worker = new Worker(handler, RESPONSE_TYPE_MERGER_ACCOUNT,
				GsonResponse3.mergerAccountResponse.class);
		worker.execute(RIA_INTERFACE_MERGER_ACCOUNT, request);
	}
	//---2.5.11---
	//2.5.11.1 编辑用户信息
	/**
	 * 2.5.11.1 编辑用户信息
	 * @param handler
	 * @param nickname
	 * @param signature
	 * @param sex
	 * @param address
	 * @param birthdate
	 */
	public static void changeUserInfo(Handler handler, String nickname,
			String signature, String sex, String address,
			String birthdate) {
		GsonRequest3.changeUserInfoRequest request = new GsonRequest3.changeUserInfoRequest();
		request.address = address;
		request.birthdate = birthdate;
		request.equipmentid = CommonInfo.getInstance().equipmentid;
		request.nickname = nickname;
		if(TextUtils.isEmpty(sex)){
			request.sex = "2";// uPI.sex;
		}else{
			request.sex = sex;// uPI.sex;
		}
		request.signature = signature;
		request.userid = ActiveAccount.getInstance(ZApplication.getInstance())
				.getLookLookID();
		// request.username =
		// ActiveAccount.getInstance(ZApplication.getInstance()).username;
		request.imei = VALUE_IMEI;
		request.mac = VALUE_MAC;
//		GsonRequest3.changeUserInfoRequest testRequest = new Gson().fromJson(new Gson().toJson(request), GsonRequest3.changeUserInfoRequest.class);
		Worker worker = new Worker(handler, RESPONSE_TYPE_CHANGE_USER_INFO,
				GsonResponse3.changeUserInfoResponse.class);
		worker.execute(RIA_INTERFACE_CHANGE_USER_INFO, request);
	}
	//2.5.11.2 修改登录密码
	/**
	 * 2.5.11.2 修改登录密码
	 * @param handler
	 * @param oldPassword
	 * @param newPassword
	 * @param pwd_type
	 */
	public static void passwordChange(Handler handler, String oldPassword,
			String newPassword, String pwd_type) {
		GsonRequest3.passwordChangeRequest request = new GsonRequest3.passwordChangeRequest();
		request.newpassword = MD5Util.getMD5String(newPassword).toLowerCase();
		request.oldpassword = MD5Util.getMD5String(oldPassword).toLowerCase();
		request.userid = ActiveAccount.getInstance(ZApplication.getInstance())
				.getLookLookID();
		request.mac = VALUE_MAC;
		request.imei = VALUE_IMEI;
		request.pwd_type = pwd_type;
		Worker worker = new Worker(handler, RESPONSE_TYPE_PASSWORD_CHANGE,
				GsonResponse3.passwordChangeResponse.class);
		worker.execute(RIA_INTERFACE_PASSWORD_CHANGE, request);
	}
	//2.5.11.3 修改个性签名
	/**
	 * 2.5.11.3 修改个性签名
	 * @param handler
	 * @param signature
	 */
	public static void changeMood(Handler handler, String signature) {
		GsonRequest3.moodRequest request = new GsonRequest3.moodRequest();
		request.userid = ActiveAccount.getInstance(ZApplication.getInstance())
				.getLookLookID();
		request.mac = VALUE_MAC;
		request.imei = VALUE_IMEI;
		request.signature = signature;
		Worker worker = new Worker(handler, RESPONSE_TYPE_MOOD,
				GsonResponse3.moodResponse.class);
		worker.execute(RIA_INTERFACE_MOOD, request);
	}
	//2.5.11.4 Looklook系统设置
	/**
	 * 2.5.11.4 Looklook系统设置
	 * @param handler
	 * @param sync_type
	 */
	public static void setPrivacy(Handler handler, String sync_type) {
		GsonRequest3.diaryPrivacyRequest request = new GsonRequest3.diaryPrivacyRequest();
		request.sync_type = sync_type;
		request.userid = ActiveAccount.getInstance(ZApplication.getInstance())
				.getLookLookID();
		request.mac = VALUE_MAC;
		request.imei = VALUE_IMEI;
		Worker worker = new Worker(handler, RESPONSE_TYPE_DIARY_PRIVACY,
				GsonResponse3.diaryPrivacyResponse.class);
		worker.execute(RIA_INTERFACE_DIARY_PRIVACY, request);
	}
	
	//2.4.8.5 微享系统通讯录自动好友设置
	/**
	 * 2.4.8.5 微享系统通讯录自动好友设置
	 * @param handler
	 * @param acceptFriendType
	 */
	public static void setAutoFriend(Handler handler, String acceptFriendType) {
		GsonRequest3.autoFriendRequest request = new GsonRequest3.autoFriendRequest();
		request.accept_friend_status = acceptFriendType;
		request.userid = ActiveAccount.getInstance(ZApplication.getInstance())
				.getLookLookID();
		request.mac = VALUE_MAC;
		request.imei = VALUE_IMEI;
		Worker worker = new Worker(handler, RESPONSE_TYPE_AUTO_FRIEND,
				GsonResponse3.autoFriendResponse.class);
		worker.execute(RIA_INTERFACE_AUTO_FRIEND, request);
	}
	//2.5.11.5 设置手势密码
	/**
	 * 2.5.11.5 设置手势密码
	 * @param handler
	 * @param gesturepassword
	 */
	public static void addGesturePassword(Handler handler,
			String gesturepassword) {
		GsonRequest3.addGesturePasswordRequest request = new GsonRequest3.addGesturePasswordRequest();
		request.gesturepassword = gesturepassword;
		request.userid = ActiveAccount.getInstance(ZApplication.getInstance())
				.getLookLookID();
		request.mac = VALUE_MAC;
		request.imei = VALUE_IMEI;
		Worker worker = new Worker(handler, RESPONSE_TYPE_ADD_GESTURE_PASSWORD,
				GsonResponse3.addGesturePasswordResponse.class);
		worker.execute(RIA_INTERFACE_ADD_GESTURE_PASSWORD, request);
	}
	//2.5.11.6 设置个性排序
	/**
	 * 2.5.11.6 设置个性排序
	 * @param handler
	 * @param sort
	 */
	public static void setPersonalSort(Handler handler, String sort) {
		GsonRequest3.mypersonalsortRequest request = new GsonRequest3.mypersonalsortRequest();
		request.sort = sort;
		request.userid = ActiveAccount.getInstance(ZApplication.getInstance())
				.getLookLookID();
		request.mac = VALUE_MAC;
		request.imei = VALUE_IMEI;
		Worker worker = new Worker(handler, RESPONSE_TYPE_MY_PERSONAL_SORT,
				GsonResponse3.mypersonalsortResponse.class);
		worker.execute(RIA_INTERFACE_MY_PERSONAL_SORT, request);
	}
	//2.5.11.7 设置用户头像(供图片服务器访问，3.1 socket处理)
	/**
	 * 2.5.11.7 设置用户头像(供图片服务器访问，3.1 socket处理)
	 * @param handler
	 * @param imageURL
	 */
	public static void setHeadImage(Handler handler, String imageURL) {
		GsonRequest3.setHeadImageRequest request = new GsonRequest3.setHeadImageRequest();
		request.imagepath = imageURL;

		request.mac = VALUE_MAC;
		request.imei = VALUE_IMEI;

		request.userid = ActiveAccount.getInstance(ZApplication.getInstance())
				.getLookLookID();

		Worker worker = new Worker(handler, RESPONSE_TYPE_SET_HEADIMAGE,
				GsonResponse3.setHeadImageResponse.class);
		worker.execute(RIA_INTERFACE_SET_HEADIMAGE, request);
	}
	//===2.6===
	//2.6.1 启动调用接口(UA、升级)
	/**
	 * 2.6.1 启动调用接口(UA、升级)
	 * @param handler
	 */
	public static void submitUA(Handler handler) {
		VALUE_IMEI = ZStringUtils.nullToEmpty(ZSimCardInfo.getIMEI());
		VALUE_MAC = ZStringUtils.nullToEmpty(ZSimCardInfo.getDeviceMac());
		VALUE_IMSI = ZStringUtils.nullToEmpty(ZSimCardInfo.getIMSI());
		
		GsonRequest3.uaRequest request = new GsonRequest3.uaRequest();
		request.basestationinfo = VALUE_EMPTY;
		request.channel_number = VALUE_CHANNEL_NUMBER;
		request.clientversion = VALUE_CLIENT_VERSION;
		// request.columnsversion = VALUE_EMPTY;
		request.devicetype = ZSimCardInfo.getDeviceBrand() + " " + ZSimCardInfo.getDeviceName();
		ZLog.e("devicetype = " + request.devicetype);
		request.sdk_imei = CmmobiClickAgent.getSDKId(MainApplication.getAppInstance());
		if (CommonInfo.getInstance().equipmentid == null) {
			request.equipmentid = VALUE_EMPTY;
		} else {
			request.equipmentid = CommonInfo.getInstance().equipmentid;
		}

		// request.errorlog = VALUE_EMPTY;
		request.gps = VALUE_EMPTY;
		request.imei =  ZStringUtils.nullToEmpty(ZSimCardInfo.getIMEI());
		request.imsi = ZSimCardInfo.getIMSI();
		request.internetway = getNetType();
		request.ip = "192.168.1.1";//ZStringUtils.nullToEmpty(ZNetworkStateDetector.getIpV4Address());
		request.mac = ZStringUtils.nullToEmpty(ZNetworkStateDetector.getMacAddress());
		request.resolution = VALUE_RESOLUTION;
		request.siteid = "3";
		request.systemversionid = VALUE_DEVICE_TYPE;
		
		String userid = ActiveAccount.getInstance(MainApplication.getAppInstance()).getLookLookID();
		if(!ActiveAccount.verifyUseridSuccess(userid)){
			request.is_mishare_no = "1";
		}else{
			request.is_mishare_no = "0";
		}
		request.mobiletype = VALUE_MOBILE_TYPE;
		request.devicetoken = ZSimCardInfo.getIMEI();
		// 合并版本信息
		request.channelcode = MetaUtil.getStringValue("CMMOBI_CHANNEL");
		request.productcode = String.valueOf(MetaUtil.getIntValue("CMMOBI_APPKEY"));
		request.system = "101";
		try {
			String packagename = MainApplication.getInstance().getPackageName();
			PackageInfo info = MainApplication.getInstance().getPackageManager()
					           .getPackageInfo(packagename, 0);
	        String version = info.versionName;
	        version.replace('.', '_');
			request.version = version.replace('.', '_');
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		
		Worker worker = new Worker(handler, RESPONSE_TYPE_UA,
				GsonResponse3.uaResponse.class);
		worker.execute(RIA_INTERFACE_UA, request);

	}
	//2.6.2 用户登录返回数据集合
	//2.6.3 用户登录(密码加密，与CRM确认)
	/**
	 * 2.6.3 用户登录(密码加密，与CRM确认)
	 * @param context
	 * @param handler
	 * @param acct
	 * @param logintype
	 */
	public static void login(Context context, Handler handler,
			ActiveAccount acct, String logintype) {
		VALUE_IMEI = ZStringUtils.nullToEmpty(ZSimCardInfo.getIMEI());
		VALUE_MAC = ZStringUtils.nullToEmpty(ZSimCardInfo.getDeviceMac());
		VALUE_IMSI = ZStringUtils.nullToEmpty(ZSimCardInfo.getIMSI());
		VALUE_DEVICE_TYPE = "android:" + ZSimCardInfo.getSystemReleaseVersion();
		
		if (acct == null) {
			return;
		}

		GsonRequest3.loginRequest request = new GsonRequest3.loginRequest();
		request.snsuid = acct.snsid; //.toLowerCase();

		/* String uid = acct.userid; */

		if (!"0".equals(acct.snstype)) {// weibo
			request.snstype = acct.snstype;
			request.snsname = acct.snsname;
			OAuthV2 oauth = null;
			if(request.snstype.equals("1")){//sina
				oauth = CmmobiSnsLib.getInstance().acquireOauth(SHARE_TO.SINA.ordinal());
			}else if(request.snstype.equals("2")){//renren
				oauth = CmmobiSnsLib.getInstance().acquireOauth(SHARE_TO.RENREN.ordinal());
			}else if(request.snstype.equals("6")){//tencent
				oauth = CmmobiSnsLib.getInstance().acquireOauth(SHARE_TO.TENC.ordinal());
			}else if(request.snstype.equals("13")){//qqmobile
				oauth = CmmobiSnsLib.getInstance().acquireOauth(SHARE_TO.QQMOBILE.ordinal());
			}
			
			if(oauth!=null){
				request.openkey = oauth.getOpenkey();
				request.access_token = oauth.getAccessToken();
				request.sns_effective_time = oauth.getExpiresIn();
				request.sns_expiration_time = String.valueOf(oauth.getLongExpiresTime());
				request.refresh_token = oauth.getRefreshToken();
			}
			
			
		} else {// looklook
			request.snstype = "0";
			request.username = acct.username;
			request.password = MD5Util.getMD5String(acct.password).toLowerCase();
			request.logintype = logintype;
		}

		CommonInfo ci = CommonInfo.getInstance();
		if(ci.myLoc!=null){
			request.gps=String.valueOf(ci.myLoc.longitude) + ":" + String.valueOf(ci.myLoc.latitude);
		}else{
			request.gps = "";
		}
		// AccountInfo uPI = acct.getAccountInfo(acct.getUID());

		// Log.e(TAG, "login uPI.nickName:" + uPI.nickname);
		Log.e(TAG, "login acct.nickName:" + acct.nickname);
		request.nickname = acct.nickname; // base64
		if(TextUtils.isEmpty(acct.sex)){
			request.sex = "2";// uPI.sex;
		}else{
			request.sex = acct.sex;// uPI.sex;
		}
		request.birthdate = acct.birthdate;
		request.address = acct.address;// base6
//        request.signature = signature; // base64

		request.devicetoken = ZSimCardInfo.getIMEI();
		request.equipmentid = CommonInfo.getInstance().equipmentid;// AppState.getUaResponse().equipmentid;
																	// ---
																	// npe!!!
		//request.mac = VALUE_MAC;
		//request.imei = VALUE_IMEI;
		
		request.imei = ZSimCardInfo.getIMEI();
		
		request.mac = ZStringUtils.nullToEmpty(ZNetworkStateDetector.getMacAddress());
		
		request.mobiletype = VALUE_MOBILE_TYPE;

		/*
		 * if (!request.snstype.equals("0")) { request.nickname = VALUE_EMPTY;
		 * request.gps = VALUE_EMPTY; request.sex = VALUE_EMPTY;
		 * request.birthdate = VALUE_EMPTY; request.address = VALUE_EMPTY;
		 * request.tag = VALUE_EMPTY; request.snsid = VALUE_EMPTY; }
		 */

		Worker worker = new Worker(handler, RESPONSE_TYPE_LOGIN,
				GsonResponse3.loginResponse.class);
		worker.execute(RIA_INTERFACE_LOGIN, request);
	}
	//2.6.4 自动登录(同时设置deviceToken)
	/**
	 * 2.6.4 自动登录(同时设置deviceToken)
	 * @param handler
	 * @param devicetoken
	 */
	public static void autoLogin(Handler handler, String devicetoken) {
		VALUE_IMEI = ZStringUtils.nullToEmpty(ZSimCardInfo.getIMEI());
		VALUE_MAC = ZStringUtils.nullToEmpty(ZSimCardInfo.getDeviceMac());
		VALUE_IMSI = ZStringUtils.nullToEmpty(ZSimCardInfo.getIMSI());
		VALUE_DEVICE_TYPE = "android:" + ZSimCardInfo.getSystemReleaseVersion();
		
		GsonRequest3.autoLoginRequest request = new GsonRequest3.autoLoginRequest();
		request.devicetoken = devicetoken;
		request.mac = ZSimCardInfo.getDeviceMac();
		request.imei = ZSimCardInfo.getIMEI();
		request.userid = ActiveAccount.getInstance(ZApplication.getInstance())
				.getLookLookID();
		request.mobiletype = VALUE_MOBILE_TYPE;

		Worker worker = new Worker(handler, RESPONSE_TYPE_AUTOLOGIN,
				GsonResponse3.autoLoginResponse.class);
		worker.execute(RIA_INTERFACE_AUTOLOGIN, request);
	}
	//2.6.5 Looklook用户注册
	/**
	 * 2.6.5 Looklook用户注册
	 * @param handler
	 * @param nick
	 * @param username
	 * @param passwd
	 * @param check_no
	 * @param registertype
	 */
	public static void register(Handler handler, String nick, String username, String passwd, String check_no, String registertype) {
		GsonRequest3.registerRequest request = new GsonRequest3.registerRequest();
		//request.address = VALUE_EMPTY);
		//request.birthdate = "1990-06-16");
		request.devicetoken = ZSimCardInfo.getIMEI();
		//request.equipmentid = AppState.getUaResponse().equipmentid;
		request.equipmentid = CommonInfo.getInstance().equipmentid;
		
		CommonInfo ci = CommonInfo.getInstance();
		
		if(ci.myLoc!=null){
			request.gps=String.valueOf(ci.myLoc.longitude) + ":" + String.valueOf(ci.myLoc.latitude);
		}else{
			request.gps = "";
		}
		
		request.imei = VALUE_IMEI;
		request.mac  = VALUE_MAC;
		request.mobiletype = VALUE_MOBILE_TYPE;
		//request.nickname = "测试昵称1");
		request.nickname = nick;
		//request.password = "123456");
		request.password = MD5Util.getMD5String(passwd).toLowerCase();
		request.sex = VALUE_SEX_UNKNOW;
		//request.tag = "今天10号线挤死我了");
		request.registertype = registertype;
		request.username = username; //key, mail addr,
		request.check_no = check_no;
				
		Worker worker = new Worker(handler, RESPONSE_TYPE_REGISTER, GsonResponse3.registerResponse.class);
		worker.execute(RIA_INTERFACE_REGISTER, request);
	}
	//2.6.6 绑定(邮箱、手机、第三放平台)
	/**
	 * 2.6.6 绑定(邮箱、手机、第三放平台)
	 * @param handler
	 * @param binding_type
	 * @param binding_info
	 * @param snsuid
	 * @param snstype
	 * @param check_no
	 * @param sex
	 * @param address
	 * @param birthdate
	 * @param access_token
	 * @param sns_effective_time
	 * @param sns_expiration_time
	 * @param refresh_token
	 * @param snsname
	 * @param openkey
	 * @param nickname
	 */
	public static void bindAccount(Handler handler, String binding_type, String binding_info, String snsuid,
			String snstype, String check_no, String sex, String address,
			String birthdate, String access_token, String sns_effective_time,
			String sns_expiration_time, String refresh_token, String snsname,
			String openkey, String nickname) {
		GsonRequest3.bindingRequest request = new GsonRequest3.bindingRequest();
		request.binding_type = binding_type;
		request.binding_info = binding_info;

		request.snstype = snstype;
		request.snsuid = snsuid; //.toLowerCase();

		request.check_no = check_no;
		request.equipmentid = CommonInfo.getInstance().equipmentid;

		CommonInfo ci = CommonInfo.getInstance();
		
		if(ci.myLoc!=null){
			request.gps=String.valueOf(ci.myLoc.longitude) + ":" + String.valueOf(ci.myLoc.latitude);
		}else{
			request.gps = "";
		}
		
		if(TextUtils.isEmpty(sex)){
			request.sex = "2";// uPI.sex;
		}else{
			request.sex = sex;// uPI.sex;
		}
		request.address = address;
		request.birthdate = birthdate;

		request.access_token = access_token;
		request.sns_effective_time = sns_effective_time;
		request.sns_expiration_time = sns_expiration_time;
		request.refresh_token = refresh_token;
		request.snsname = snsname;
		request.openkey = openkey;
		request.userid = ActiveAccount.getInstance(ZApplication.getInstance()).getLookLookID();
		request.mac = VALUE_MAC;
		request.imei = VALUE_IMEI;
		
		request.nickname = nickname;
		Worker worker = new Worker(handler, RESPONSE_TYPE_BINDING,
				GsonResponse3.bindingResponse.class);
		worker.execute(RIA_INTERFACE_BINDING, request);
	}
	
	//2.5.4绑定(邮箱、手机、第三放平台)
	/**
	 * 2.6.6 绑定(邮箱、手机、第三放平台)
	 * @param handler
	 * @param binding_type
	 * @param binding_info
	 * @param snsuid
	 * @param snstype
	 * @param check_no
	 * @param sex
	 * @param address
	 * @param birthdate
	 */
	public static void bindPhone(Handler handler, String binding_type, String binding_info,
			String check_no, String sex, String address,String birthdate) {
		GsonRequest3.bindingRequest request = new GsonRequest3.bindingRequest();
		request.binding_type = binding_type;
		request.binding_info = binding_info;
		request.check_no = check_no;
		request.equipmentid = CommonInfo.getInstance().equipmentid;

		CommonInfo ci = CommonInfo.getInstance();
		
		if(ci.myLoc!=null){
			request.gps=String.valueOf(ci.myLoc.longitude) + ":" + String.valueOf(ci.myLoc.latitude);
		}else{
			request.gps = "";
		}
		
		if(TextUtils.isEmpty(sex)){
			request.sex = "2";// uPI.sex;
		}else{
			request.sex = sex;// uPI.sex;
		}
		request.address = address;
		request.birthdate = birthdate;

		request.userid = ActiveAccount.getInstance(ZApplication.getInstance()).getLookLookID();
		request.mac = VALUE_MAC;
		request.imei = VALUE_IMEI;
		
		Worker worker = new Worker(handler, RESPONSE_TYPE_BINDING,
				GsonResponse3.bindingResponse.class);
		worker.execute(RIA_INTERFACE_BINDING, request);
	}
	
	//2.5.5 切换手机绑定
	/**
	 * 2.6.6 绑定(邮箱、手机、第三放平台)
	 * @param handler
	 * @param binding_type
	 * @param binding_info
	 * @param snsuid
	 * @param snstype
	 * @param check_no
	 * @param sex
	 * @param address
	 * @param birthdate
	 */
	public static void changeBinding(Handler handler, String binding_type, String binding_info,
			String check_no, String sex, String address,String birthdate) {
		GsonRequest3.changeBindRequest request = new GsonRequest3.changeBindRequest();
		request.change_bind_type = binding_type;
		request.phone = binding_info;
		request.check_no = check_no;
		request.equipmentid = CommonInfo.getInstance().equipmentid;

		CommonInfo ci = CommonInfo.getInstance();
		
		if(ci.myLoc!=null){
			request.gps=String.valueOf(ci.myLoc.longitude) + ":" + String.valueOf(ci.myLoc.latitude);
		}else{
			request.gps = "";
		}
		
		if(TextUtils.isEmpty(sex)){
			request.sex = "2";// uPI.sex;
		}else{
			request.sex = sex;// uPI.sex;
		}
		request.address = address;
		request.birthdate = birthdate;

		request.userid = ActiveAccount.getInstance(ZApplication.getInstance()).getLookLookID();
		request.mac = VALUE_MAC;
		request.imei = VALUE_IMEI;
		
		Worker worker = new Worker(handler, RESPONSE_TYPE_CHANGEBINDING,
				GsonResponse3.changeBindingResponse.class);
		worker.execute(RIA_INTERFACE_CHANGE_BINDING, request);
	}
	
	// 2.5.11 验证手机验证码
	public static void verifySMS (Handler handler,String phone,String check_no,String check_type) {
		GsonRequest3.verifySMSRequest request = new GsonRequest3.verifySMSRequest();
		request.phone = phone;
		request.check_no = check_no;
		request.check_type = check_type;

		request.mac = VALUE_MAC;
		request.imei = VALUE_IMEI;
		
		Worker worker = new Worker(handler, RESPONSE_TYPE_VERIFYSMS,
				GsonResponse3.verifySMSResponse.class);
		worker.execute(RIA_INTERFACE_VERIFY_SMS, request);
	}
	
	
	//2.6.1.3 好友动态接口
	public static void requestFriendNews(Handler handler, String diary_time, String request_type) {
		GsonRequest3.friendNewsRequest request = new GsonRequest3.friendNewsRequest();
		request.userid = ActiveAccount.getInstance(ZApplication.getInstance())
				.getLookLookID();
		request.pagesize = "30";
		request.diary_time = diary_time;
		request.request_type = request_type;
		request.diarywidth="480";
		request.diaryheight="480";
		
		request.mac = VALUE_MAC;
		request.imei = VALUE_IMEI;
		
		Worker worker = new Worker(handler, RESPONSE_TYPE_FRIEND_NEWS_LIST, GsonResponse3.friendNewsResponse.class);
		worker.execute(RIA_INTERFACE_FRIEND_NEWS_LIST, request);
	}
		
	//2.7.2.12 清除新好友推荐
	public static void cleanRecommend(Handler handler, String target_userid) {
		GsonRequest3.cleanRecommendRequest request = new GsonRequest3.cleanRecommendRequest();
		request.userid = ActiveAccount.getInstance(ZApplication.getInstance())
				.getLookLookID();
		request.target_userid = target_userid;
		
		request.mac = VALUE_MAC;
		request.imei = VALUE_IMEI;
		
		Worker worker = new Worker(handler, RESPONSE_TYPE_CLEAN_RECOMMEND, GsonResponse3.cleanRecommendResponse.class);
		worker.execute(RIA_INTERFACE_CLEAN_RECOMMEND, request);
	}
	
	//2.6.7 解除绑定
	/**
	 * 2.6.7 解除绑定
	 * @param handler
	 * @param binding_type
	 * @param binding_info
	 * @param snstype
	 * @param snsuid
	 */
	public static void unbind(Handler handler, String binding_type,
			String binding_info, String snstype, String snsuid) {
		GsonRequest3.unbindRequest request = new GsonRequest3.unbindRequest();
		request.binding_type = binding_type;
		request.binding_info = binding_info;
		request.snstype = snstype;
		request.snsuid = snsuid; //.toLowerCase();

		request.equipmentid = CommonInfo.getInstance().equipmentid;
		request.mac = VALUE_MAC;
		request.imei = VALUE_IMEI;

		request.userid = ActiveAccount.getInstance(ZApplication.getInstance())
				.getLookLookID();
		Worker worker = new Worker(handler, RESPONSE_TYPE_UNBIND,
				GsonResponse3.unbindResponse.class);
		worker.execute(RIA_INTERFACE_UNBIND, request);
	}
	//2.6.8 验证注册用户名是否可用(敏感词CRM or RIA调用接口处理，37、38考虑合并)
	/**
	 * 2.6.8 验证注册用户名是否可用(敏感词CRM or RIA调用接口处理，37、38考虑合并)
	 * @param handler
	 * @param username
	 */
	public static void checkUserNameExist(Handler handler, String username) {
		GsonRequest3.checkUserNameExistRequest request = new GsonRequest3.checkUserNameExistRequest();
		request.username = username;
		request.equipmentid = CommonInfo.getInstance().equipmentid;
		request.mac = VALUE_MAC;
		request.imei = VALUE_IMEI;

		Worker worker = new Worker(handler, RESPONSE_TYPE_CHECK_USERNAME,
				GsonResponse3.checkUserNameExistResponse.class);
		worker.execute(RIA_INTERFACE_CHECK_USERNAME, request);
	}
	//2.6.9 验证注册昵称是否可用--(第三方用户昵称规则？)
	/**
	 * 2.6.9 验证注册昵称是否可用--(第三方用户昵称规则？)
	 * @param handler
	 * @param nickname
	 */
	public static void checkNickNameExist(Handler handler, String nickname) {
		GsonRequest3.checkNickNameExistRequest request = new GsonRequest3.checkNickNameExistRequest();
		request.nickname = nickname;
		request.mac = VALUE_MAC;
		request.imei = VALUE_IMEI;

		Worker worker = new Worker(handler, RESPONSE_TYPE_CHECK_NICKNAME,
				GsonResponse3.checkNickNameExistResponse.class);
		worker.execute(RIA_INTERFACE_CHECK_NICKNAME, request);
	}
	//2.6.10 判断用户是否有效(是否被拉黑)
	/**
	 * 2.6.10 判断用户是否有效(是否被拉黑)
	 * @param handler
	 * @param mac
	 * @param imei
	 */
	public static void checkuser(Handler handler, String mac, String imei) {
		GsonRequest3.checkUserRequest request = new GsonRequest3.checkUserRequest();
		request.mac = VALUE_MAC;
		request.imei = VALUE_IMEI;
		request.userid = ActiveAccount.getInstance(ZApplication.getInstance())
				.getLookLookID();

		Worker worker = new Worker(handler, RESPONSE_TYPE_CHECK_USER,
				GsonResponse3.checkuserResponse.class);
		worker.execute(RIA_INTERFACE_CHECK_USER, request);
	}
	//2.6.11 找回密码
	/**
	 * 2.6.11 找回密码
	 * @param handler
	 * @param username
	 * @param registertype
	 * @param pwd_type
	 */
	public static void requestForgetPassword(Handler handler, String username, String registertype, String pwd_type) {
		GsonRequest3.forgetPasswordRequest request = new GsonRequest3.forgetPasswordRequest();
		request.username = username;

		request.registertype = registertype;
		request.pwd_type = pwd_type;
		request.equipmentid = CommonInfo.getInstance().equipmentid;
		request.mac = VALUE_MAC;
		request.imei = VALUE_IMEI;
		request.userid = ActiveAccount.getInstance(ZApplication.getInstance())
				.getLookLookID();

		Worker worker = new Worker(handler, RESPONSE_TYPE_FORGET_PASSWORD,
				GsonResponse3.forgetPasswordResponse.class);
		worker.execute(RIA_INTERFACE_FORGET_PASSWORD, request);
	}
	//2.6.12 获取手机验证码
	/**
	 * 2.6.12 获取手机验证码
	 * @param handler
	 * @param username
	 * @param check_type
	 */
	public static void getCheckNo(Handler handler, String username, String check_type) {
		GsonRequest3.CheckNoRequest request = new GsonRequest3.CheckNoRequest();
		request.username = username;
		request.check_type = check_type;
		request.mac = VALUE_MAC;
		request.imei = VALUE_IMEI;
		Worker worker = new Worker(handler, RESPONSE_TYPE_CHECKNO,
				GsonResponse3.checkNoResponse.class);
		worker.execute(RIA_INTERFACE_CHECKNO, request);
	}
	/**
	 * 2.6.13 解除保险箱
	 */
	public static void unSafebox(Handler handler){
		GsonRequest3.unSafeboxRequest request = new GsonRequest3.unSafeboxRequest();
		request.userid = ActiveAccount.getInstance(ZApplication.getInstance()).getLookLookID();
		request.mac = VALUE_MAC;
		request.imei = VALUE_IMEI;
		Worker worker = new Worker(handler, RESPONSE_TYPE_UNSAFEBOX, GsonResponse3.unSafeboxResponse.class);
		worker.execute(RIA_INTERFACE_UNSAFEBOX, request);
	}
	
	
	//===2.7===
	//2.7.1.1 我的日记
	/**
	 * 2.7.1.1 我的日记
	 * 
	 * @param handler
	 * @param viewUserId : 当前正要浏览的用户的id;
	 */
	public static void requestMyDiary(Handler handler, String viewUserId,
			String diaryTime, String requestType, String diarywidth,
			String diaryheight) {
		Log.d(TAG, "diaryTime=" + diaryTime);
		Log.d(TAG, "request_type=" + requestType);
		GsonRequest3.listMyDiaryRequest request = new GsonRequest3.listMyDiaryRequest();

		request.pagesize = "50";
		request.userid = ActiveAccount.getInstance(ZApplication.getInstance())
				.getLookLookID();
//		request.userid = MyZoneFragment.userID;

		// request.userid = "5358e7db0646f04a820bcb20ebc2e7818a70";
		// request.viewuserid = "5358e7db0646f04a820bcb20ebc2e7818a70";

		request.diarywidth = diarywidth;
		request.diaryheight = VALUE_EMPTY;

		request.diary_time = diaryTime;
		request.request_type = requestType;

		request.viewuserid = viewUserId;

		request.mac = VALUE_MAC;
		request.imei = VALUE_IMEI;

		Worker worker = new Worker(handler, RESPONSE_TYPE_LIST_MY_DIARY,
				GsonResponse3.listMyDiaryResponse.class);
		worker.execute(RIA_INTERFACE_LIST_MY_DIARY, request);
	}
	
	public static void requestMyDiary(Handler handler, String viewUserId,
			String diaryTime, String requestType, String diarywidth,
			String diaryheight, boolean delay) {
		Log.d(TAG, "diaryTime=" + diaryTime);
		Log.d(TAG, "request_type=" + requestType);
		GsonRequest3.listMyDiaryRequest request = new GsonRequest3.listMyDiaryRequest();

		request.pagesize = "50";
		request.userid = ActiveAccount.getInstance(ZApplication.getInstance())
				.getLookLookID();
//		request.userid = MyZoneFragment.userID;

		// request.userid = "5358e7db0646f04a820bcb20ebc2e7818a70";
		// request.viewuserid = "5358e7db0646f04a820bcb20ebc2e7818a70";

		request.diarywidth = diarywidth;
		request.diaryheight = VALUE_EMPTY;

		request.diary_time = diaryTime;
		request.request_type = requestType;

		request.viewuserid = viewUserId;

		request.mac = VALUE_MAC;
		request.imei = VALUE_IMEI;

		Worker worker = new Worker(handler, RESPONSE_TYPE_LIST_MY_DIARY,
				GsonResponse3.listMyDiaryResponse.class);
		worker.execute(RIA_INTERFACE_LIST_MY_DIARY, request, delay);
	}
	//2.7.1.2 个人空间(我的日记16区别在于一些附属信息)
	/**
	 * 2.7.1.2 个人空间(我的日记16区别在于一些附属信息)
	 * @param handler
	 * @param viewuserid
	 * @param diary_time
	 * @param request_type
	 * @param diarywidth
	 * @param diaryheight
	 * @param userbackgroundwidth
	 * @param userbackgroundheight
	 */
	public static void homePage(Handler handler, String viewuserid,
			String diary_time, String request_type,String diarywidth,String diaryheight,String userbackgroundwidth,String userbackgroundheight) {
		GsonRequest3.homeRequest request = new GsonRequest3.homeRequest();
		request.pagesize = "50";
		request.viewuserid = viewuserid;
		request.diary_time = diary_time;
		request.request_type = request_type;
		request.diarywidth=diarywidth;
		request.diaryheight=diaryheight;
		request.userbackgroundwidth=userbackgroundwidth;
		request.userbackgroundheight=userbackgroundheight;
		request.userid = ActiveAccount.getInstance(ZApplication.getInstance())
				.getLookLookID();
		//request.userid = MyZoneFragment.userID;
		request.mac = VALUE_MAC;
		request.imei = VALUE_IMEI;

		Worker worker = new Worker(handler, RESPONSE_TYPE_HOME,
				GsonResponse3.homeResponse.class);
		worker.execute(RIA_INTERFACE_HOME, request);
	}
	
	public static void homePage(Handler handler, String viewuserid,
			String diary_time, String request_type,String diarywidth,String diaryheight,String userbackgroundwidth,String userbackgroundheight, boolean delay) {
		GsonRequest3.homeRequest request = new GsonRequest3.homeRequest();
		request.pagesize = "50";
		request.viewuserid = viewuserid;
		request.diary_time = diary_time;
		request.request_type = request_type;
		request.diarywidth=diarywidth;
		request.diaryheight=diaryheight;
		request.userbackgroundwidth=userbackgroundwidth;
		request.userbackgroundheight=userbackgroundheight;
		request.userid = ActiveAccount.getInstance(ZApplication.getInstance())
				.getLookLookID();
		//request.userid = MyZoneFragment.userID;
		request.mac = VALUE_MAC;
		request.imei = VALUE_IMEI;

		Worker worker = new Worker(handler, RESPONSE_TYPE_HOME,
				GsonResponse3.homeResponse.class);
		worker.execute(RIA_INTERFACE_HOME, request, delay);
	}
	//2.7.1.3 获取收藏日记列表
	/**
	 * 2.7.1.3 获取收藏日记列表
	 * @param handler
	 * @param viewuserid
	 * @param request_time
	 * @param request_type
	 * @param diarywidth
	 * @param diaryheight
	 */
	public static void listCollectDiary(Handler handler, String viewuserid,
			String request_time, String request_type, String diarywidth,
			String diaryheight) {
		GsonRequest3.listCollectDiaryRequest request = new GsonRequest3.listCollectDiaryRequest();
		request.pagesize = "10";
		request.userid = ActiveAccount.getInstance(ZApplication.getInstance()).getLookLookID();
		request.viewuserid = viewuserid;
		request.diary_time = request_time;
		request.request_type = request_type;
		request.diarywidth = diarywidth;
		request.diaryheight = diaryheight;

		request.mac = VALUE_MAC;
		request.imei = VALUE_IMEI;

		Worker worker = new Worker(handler, RESPONSE_TYPE_LIST_COLLECT_DIARY,
				GsonResponse3.listCollectDiaryResponse.class);
		worker.execute(RIA_INTERFACE_LIST_COLLECT_DIARY, request);
	}
	//2.7.1.4 我的评论列表(按照时间戳处理)
	/**
	 * 2.7.1.4 我的评论列表(按照时间戳处理) (非保险箱)
	 * @param handler
	 * @param comment_time
	 * @param request_type
	 * @param diaryid
	 * @param viewUserID
	 */
	public static void MyCommentList(Handler handler, String comment_type, String comment_time, String request_type,String pagesize){
		String is_encrypt = "0";
		MyCommentList(handler, comment_type, comment_time, request_type, pagesize, is_encrypt);
	}
	/**
	 * 2.7.1.4 我的评论列表(按照时间戳处理) (可选保险箱)
	 * @param handler
	 * @param comment_time
	 * @param request_type
	 * @param diaryid
	 * @param viewUserID
	 */
	public static void MyCommentList(Handler handler, String comment_type, String comment_time, String request_type,String pagesize,String is_encrypt){
		GsonRequest3.MyCommentListRequest request = new GsonRequest3.MyCommentListRequest();
		request.pagesize = pagesize;
		request.comment_type = comment_type;
		request.comment_time = comment_time;
		request.request_type = request_type;
		
		request.mac = VALUE_MAC;
		request.imei = VALUE_IMEI;
		request.sorttype = "1";
		request.userid = ActiveAccount.getInstance(ZApplication.getInstance()).getLookLookID();
		request.is_encrypt = is_encrypt;
		
		Worker worker = new Worker(handler, RESPONSE_TYPE_MY_COMMENTLIST, GsonResponse3.MyCommentListResponse.class);
		worker.execute(RIA_INTERFACE_MY_COMMENTLIST, request);
	}
	//2.7.1.5 日记评论列表(每条评论不带日记信息)
	/**
	 * 2.7.1.5 日记评论列表(每条评论不带日记信息)
	 * @param handler
	 * @param comment_time
	 * @param request_type
	 * @param diaryid
	 * @param viewUserID
	 */
	public static void diaryCommentList(Handler handler, String comment_time, String request_type,
			String publishid){
		GsonRequest3.diaryCommentListRequest request = new GsonRequest3.diaryCommentListRequest();
		request.pagesize = "10";
		request.publishid = publishid;
		request.comment_time = comment_time;
		request.request_type = request_type;
		request.sorttype = "1";

		request.mac = VALUE_MAC;
		request.imei = VALUE_IMEI;
		request.userid = ActiveAccount.getInstance(ZApplication.getInstance()).getLookLookID();
//		request.userid = "fcfc615c0550f040be0b8790017bf81093a8";
		
		Worker worker = new Worker(handler, RESPONSE_TYPE_DIARY_COMMENTLIST, GsonResponse3.diaryCommentListResponse.class);
		worker.execute(RIA_INTERFACE_DIARY_COMMENTLIST, request);
	}
	//2.7.1.6 赞日记id列表
	/**
	 * 2.7.1.6 赞日记id列表
	 * @param handler
	 * @param viewuserid
	 * @param diary_time
	 * @param request_type
	 * @param diarywidth
	 * @param diaryheight
	 */
//	public static void listEnjoyDiary(Handler handler, String viewuserid,
//			String diary_time, String diarywidth,String diaryheight) {
//		GsonRequest3.EnjoyDiaryIDRequest request = new GsonRequest3.EnjoyDiaryIDRequest();
//		request.pagesize = "10";
//		request.viewuserid = viewuserid;
//		request.diary_time = diary_time;
//		request.diarywidth=diarywidth;
//		request.diaryheight=diaryheight;
//
//		request.userid = ActiveAccount.getInstance(ZApplication.getInstance())
//				.getLookLookID();
//		request.mac = VALUE_MAC;
//		request.imei = VALUE_IMEI;
//
//		Worker worker = new Worker(handler, RESPONSE_TYPE_LIST_ENJOY_DIARY,
//				GsonResponse3.EnjoyDiaryIDResponse.class);
//		worker.execute(RIA_INTERFACE_LIST_ENJOY_DIARY, request);
//	}
	//2.7.1.7 订阅日记列表
	/**
	 * 2.7.1.7 订阅日记列表
	 * @param handler
	 * @param user_time
	 * @param viewuserid
	 * @param pageSize
	 */
	public static void requestAttentionList(Handler handler, String diary_time, String request_type,
			String diarywidth, String diaryheight, String viewuserid, String pageSize) {
		GsonRequest3.attentionListRequest request = new GsonRequest3.attentionListRequest();
		request.diary_time = diary_time;
		request.request_type = request_type;
		request.viewuserid = viewuserid;
		request.pagesize = pageSize; // 10;
		request.diarywidth=diarywidth;
		request.diaryheight=diaryheight;
		
		request.mac = VALUE_MAC;
		request.imei = VALUE_IMEI;

		request.userid = ActiveAccount.getInstance(ZApplication.getInstance())
				.getLookLookID();

		Worker worker = new Worker(handler, RESPONSE_TYPE_ATTENTIONLIST,
				GsonResponse3.attentionListResponse.class);
		worker.execute(RIA_INTERFACE_ATTENTIONLIST, request);
	}
	//2.7.1.8 朋友日记列表
	/**
	 * 2.7.1.8 朋友日记列表
	 * @param handler
	 * @param user_time
	 * @param viewuserid
	 * @param pageSize
	 */
	public static void requestFriendList(Handler handler, String diary_time, String request_type,
			String diarywidth, String diaryheight, String viewuserid, String pageSize) {
		GsonRequest3.friendListRequest request = new GsonRequest3.friendListRequest();
		request.diary_time = diary_time;
		request.request_type = request_type;
		request.viewuserid = viewuserid;
		request.pagesize = pageSize; // 10;
		request.diarywidth=diarywidth;
		request.diaryheight=diaryheight;
		
		request.mac = VALUE_MAC;
		request.imei = VALUE_IMEI;

		request.userid = ActiveAccount.getInstance(ZApplication.getInstance())
				.getLookLookID();

		Worker worker = new Worker(handler, RESPONSE_TYPE_FRIENDLIST,
				GsonResponse3.friendListResponse.class);
		worker.execute(RIA_INTERFACE_FRIENDLIST, request);
	}
	//2.7.1.9 收藏日记id列表
	/**
	 * 2.7.1.9 收藏日记id列表
	 * @param handler
	 * @param diary_time
	 * @param viewuserid
	 */
	public static void listCollectDiaryid(Handler handler, String diary_time,
			String viewuserid) {
		GsonRequest3.listCollectDiaryidRequest request = new GsonRequest3.listCollectDiaryidRequest();
		request.pagesize = "10";
		request.diary_time = diary_time;
		request.viewuserid = viewuserid;
		//request.diarywidth = "";
		//request.diaryheight = "";
		
		request.userid = ActiveAccount.getInstance(ZApplication.getInstance()).getLookLookID();
		request.mac = VALUE_MAC;
		request.imei = VALUE_IMEI;
		Worker worker = new Worker(handler, RESPONSE_TYPE_LIST_COLLECT_DIARYID, GsonResponse3.listCollectDiaryidResponse.class);
		worker.execute(RIA_INTERFACE_LIST_COLLECT_DIARYID, request);
	}
	
	/**
	 * 72. 转发日记id列表(3.0获取赞日记id列表);
	 * 
	 * @param handler
	 */
	public static void forwardDiaryIDList(Handler handler, String diary_time,
			String viewuserid) {
		GsonRequest3.forwardDiaryIDRequest request = new GsonRequest3.forwardDiaryIDRequest();
		request.pagesize = "10";
		request.diary_time = diary_time;
		request.viewuserid = viewuserid;
		// request.diarywidth = "";
		// request.diaryheight = "";

		request.userid = ActiveAccount.getInstance(ZApplication.getInstance())
				.getLookLookID();
		request.mac = VALUE_MAC;
		request.imei = VALUE_IMEI;

		Worker worker = new Worker(handler, RESPONSE_TYPE_FORWARD_DIARY_ID,
				GsonResponse3.forwardDiaryIDResponse.class);
		worker.execute(RIA_INTERFACE_FORWARD_DIARY_ID, request);
	}
	
	//2.7.1.10 TA人分享内容列表
	/**
	 * 2.7.1.10 TA人分享内容列表
	 * @param handler
	 * @param diary_time
	 * @param viewuserid
	 */
	public static void otherListMyDiary(Handler handler, String diary_time,String request_type,
			String diarywidth, String diaryheight, String viewuserid) {
		GsonRequest3.otherListMyDiaryRequest request = new GsonRequest3.otherListMyDiaryRequest();
		request.pagesize = "10";
		request.diary_time = diary_time;
		request.viewuserid = viewuserid;
		request.diarywidth = diarywidth;
		request.diaryheight = diaryheight;
		request.request_type=request_type;
		request.userid = ActiveAccount.getInstance(ZApplication.getInstance()).getLookLookID();
		request.mac = VALUE_MAC;
		request.imei = VALUE_IMEI;
		//FIXME homeResponse可能需要修改或增加字段
		Worker worker = new Worker(handler, RESPONSE_TYPE_OTHER_LIST_DIARY, GsonResponse3.listMyDiaryResponse.class);
		worker.execute(RIA_INTERFACE_OTHER_LIST_DIARY, request);
	}
	//2.7.1.11 TA人空间
		/**
		 * 2.7.1.11 TA人空间
		 * @param handler
		 * @param diary_time
		 * @param viewuserid
		 */
		public static void otherHome(Handler handler, String diary_time,String request_type,
				String diarywidth, String diaryheight, String userbackgroundwidth,String userbackgroundheight,String viewuserid) {
			GsonRequest3.otherHomeRequest request = new GsonRequest3.otherHomeRequest();
			request.pagesize = "10";
			request.diary_time = diary_time;
			request.viewuserid = viewuserid;
			request.userid = ActiveAccount.getInstance(ZApplication.getInstance()).getLookLookID();
//			request.viewuserid = OtherZoneActivity.othertestuserid;
//			request.userid = OtherZoneActivity.mytestuserid;
			request.diarywidth = diarywidth;
			request.diaryheight = diaryheight;
			request.request_type=request_type;
			request.userbackgroundwidth=userbackgroundwidth;
			request.userbackgroundheight=userbackgroundheight;
			request.mac = VALUE_MAC;
			request.imei = VALUE_IMEI;
			//FIXME homeResponse可能需要修改或增加字段
			Worker worker = new Worker(handler, RESPONSE_TYPE_OTHER_HOME, GsonResponse3.homeResponse.class);
			worker.execute(RIA_INTERFACE_OTHER_HOME, request);
		}
	
	//---2.7.2---
	//2.7.2.1 日记结构管理(盛宏强)
	/**
	 * 2.7.2.1 日记结构管理(盛宏强)
	 * @param handler
	 * @param diaryid
	 * @param diaryuuid
	 * @param operate_diarytype
	 * @param resourcediaryid
	 * @param resourcediaryuuid
	 * @param logitude
	 * @param latitude
	 * @param tags
	 * @param userselectposition
	 * @param userselectlogitude
	 * @param userselectlatitude
	 * @param createtime
	 * @param addresscode
	 * @param attachs
	 * @return
	 */
	public static GsonRequest3.createStructureRequest createStructure(
			Handler handler, 
			String diaryid, 
			String diaryuuid, 
			String operate_diarytype, 
			String resourcediaryid, 
			String resourcediaryuuid, 
			String tags, 
			String createtime, 
			String addresscode, 
			String position_status, 
			String isonlymic, 
			String longitude_real, // "经度",//真实内容属性位置	，operate_diarytype=1或3有效--单词修改
			String latitude_real, // "维度",//真实内容属性位置，operate_diarytype=1或3有效
			String position_real, // "北京朝阳区",  //真实内容属性位置
			String longitude, // "经度",  //导入内容位置
			String latitude, // "维度",  //导入内容位置
			String position, // "北京朝阳区",  //导入内容位置
			String longitude_view, // "经度",  //客户端后续展示、编辑使用的位置信息，创建时和真实内容属性位置一致
			String latitude_view, // "维度",  //客户端后续展示、编辑使用的位置信息，创建时和真实内容属性位置一致
			String position_view, 
			String shoot_time, 
			Attachs[] attachs) {
		
		GsonRequest3.createStructureRequest request = new GsonRequest3.createStructureRequest();
		request.diaryid = diaryid;// "3390";
		request.diaryuuid = diaryuuid;
		request.resourcediaryid = resourcediaryid;
		request.resourcediaryuuid = resourcediaryuuid;
		request.operate_diarytype = operate_diarytype;// 操作类型，1新建，2更新，3保存副本
		request.tags = tags;
		request.position_status = position_status;
		request.attachs = attachs;
		request.userid = ActiveAccount.getInstance(ZApplication.getInstance()).getLookLookID();
		request.addresscode = addresscode;
		request.createtime = createtime;
		request.mac = VALUE_MAC;
		request.imei = VALUE_IMEI;
		request.isonlymic = isonlymic;
		request.longitude_real = longitude_real;
		request.latitude_real = latitude_real;
		request.position_real = position_real;
		request.longitude = longitude;
		request.latitude = latitude;
		request.position = position;
		request.longitude_view = longitude_view;
		request.latitude_view = latitude_view;
		request.position_view = position_view;
		request.shoottime = shoot_time;

		Worker worker = new Worker(handler, RESPONSE_TYPE_CREATE_STRUCTURE,
				GsonResponse3.createStructureResponse.class);
		worker.execute(RIA_INTERFACE_CREATE_STRUCTURE, request);
	
		return request;
	}
	
	public static GsonRequest3.createStructureRequest createEmptyStructure(
			Handler handler, 
			String diaryid,
			String diaryuuid, 
			String operate_diarytype, 
			String resourcediaryid,
			String resourcediaryuuid,
			String tags,
			String createtime,
			String addresscode,
			String longitude_real, // "经度",//真实内容属性位置	，operate_diarytype=1或3有效--单词修改
			String latitude_real, // "维度",//真实内容属性位置，operate_diarytype=1或3有效
			String position_real, // "北京朝阳区",  //真实内容属性位置
			String longitude, // "经度",  //导入内容位置
			String latitude, // "维度",  //导入内容位置
			String position, // "北京朝阳区",  //导入内容位置
			String longitude_view, // "经度",  //客户端后续展示、编辑使用的位置信息，创建时和真实内容属性位置一致
			String latitude_view, // "维度",  //客户端后续展示、编辑使用的位置信息，创建时和真实内容属性位置一致
			String position_view, 
			Attachs[] attachs) {
		
		GsonRequest3.createStructureRequest request = new GsonRequest3.createStructureRequest();
		request.diaryid = diaryid;// "3390";
		request.diaryuuid = diaryuuid;
		request.resourcediaryid = resourcediaryid;
		request.resourcediaryuuid = resourcediaryuuid;
		request.operate_diarytype = operate_diarytype;// 操作类型，1新建，2更新，3保存副本
		request.tags = tags;
		request.attachs = attachs;
		request.userid = ActiveAccount.getInstance(ZApplication.getInstance()).getLookLookID();
		request.createtime = createtime;
		request.addresscode = addresscode;
		request.mac = VALUE_MAC;
		request.imei = VALUE_IMEI;
		request.longitude_real = longitude_real;
		request.latitude_real = latitude_real;
		request.position_real = position_real;
		request.longitude = longitude;
		request.latitude = latitude;
		request.position = position;
		request.longitude_view = longitude_view;
		request.latitude_view = latitude_view;
		request.position_view = position_view;
		
		return request;
	}
	//2.7.2.2 收藏日记(22、23整合)
	/**
	 * 2.7.2.2 收藏日记(22、23整合)
	 * @param handler
	 * @param diaryid
	 */
	public static void addcollectDiary(Handler handler, String diaryid, String publishid) {
		GsonRequest3.addCollectDiaryRequest request = new GsonRequest3.addCollectDiaryRequest();
		request.userid = ActiveAccount.getInstance(ZApplication.getInstance())
				.getLookLookID();
		request.diaryid = diaryid;
		request.publishid = publishid;

		request.mac = VALUE_MAC;
		request.imei = VALUE_IMEI;

		Worker worker = new Worker(handler, RESPONSE_TYPE_ADD_COLLECT_DIARY,
				GsonResponse3.addCollectDiaryResponse.class);
		worker.execute(RIA_INTERFACE_ADD_COLLECT_DIARY, request);
	}
	//2.7.2.3 取消收藏
	/**
	 * 2.7.2.3 取消收藏
	 * @param handler
	 * @param diaryids
	 */
	public static void removeCollectDiary(Handler handler, String diaryid, String publishid) {
		GsonRequest3.removeCollectDiaryRequest request = new GsonRequest3.removeCollectDiaryRequest();
		request.userid = ActiveAccount.getInstance(ZApplication.getInstance())
				.getLookLookID();
		request.diaryid = diaryid;
		request.publishid = publishid;
		
		request.mac = VALUE_MAC;
		request.imei = VALUE_IMEI;
		
		Worker worker = new Worker(handler, RESPONSE_TYPE_REMOVE_COLLECT_DIARY,
				GsonResponse3.removeCollectDiaryResponse.class);
		worker.execute(RIA_INTERFACE_REMOVE_COLLECT_DIARY, request);
	}
	//2.7.2.4 删除原日记
	/**
	 * 2.7.2.4 删除原日记
	 * @param handler
	 * @param diaryids
	 */
	public static void deleteDiary(Handler handler, String diaryids) {
		GsonRequest3.deleteDiaryRequest request = new GsonRequest3.deleteDiaryRequest();
		request.diaryids = diaryids;
		request.changetomaindiaryuuids="-1";//已此通知服务器查询副本顶主本
		request.mac = VALUE_MAC;
		request.imei = VALUE_IMEI;
		request.userid = ActiveAccount.getInstance(ZApplication.getInstance())
				.getLookLookID();

		Worker worker = new Worker(handler, RESPONSE_TYPE_DELETE_DIARY,
				GsonResponse3.deleteDiaryResponse.class);
		worker.execute(RIA_INTERFACE_DELETE_DIARY, request);
	}
	//2.7.2.5 日记放入、移出保险箱
	/**
	 * 2.7.2.5 日记放入、移出保险箱
	 * @param handler
	 * @param diaryid
	 * @param diaryuuid
	 * @param type
	 */
	public static void safebox(Handler handler, String diaryids, String diaryuuid,
			String type) {
		GsonRequest3.safeboxRequest request = new GsonRequest3.safeboxRequest();
		request.userid = ActiveAccount.getInstance(ZApplication.getInstance())
				.getLookLookID();
		request.diaryids = diaryids;
		request.diaryuuid = diaryuuid;
		request.type = type;

		request.mac = VALUE_MAC;
		request.imei = VALUE_IMEI;

		Worker worker = new Worker(handler, RESPONSE_TYPE_SAFEBOX,
				GsonResponse3.safeboxResponse.class);
		worker.execute(RIA_INTERFACE_SAFEBOX, request);
	}
	//2.7.2.6 获取日记分享URL
	/**
	 * 2.7.2.6 获取日记分享URL
	 * @param handler
	 * @param diaryid
	 */
	public static void getDiaryUrl(Handler handler, String diaryids, String diaryuuid, String publishid, String type, String content,
			String share_type, String weather, String weather_info, String longitude, String latitude, String position) {
		GsonRequest3.getDiaryUrlRequest request = new GsonRequest3.getDiaryUrlRequest();
		request.userid = ActiveAccount.getInstance(ZApplication.getInstance())
				.getLookLookID(); //用户id
		request.diaryids = diaryids;  //日记ID 如果是多个日记则会创建日记组
		request.diaryuuid = diaryuuid;  //日记组时上传UUID
		request.publishid = publishid; //转发时候需要记录父节点ID
		request.type = type; //0原创 1转发
		request.content = content;       //分享内容
		request.share_type = share_type; //1新浪 2人人 5 QZONE空间 6腾讯 9微信朋友圈 10短信 11邮箱 12微信好友 100站内公开 101朋友圈 102私密分享 103微享
		request.weather = weather;
		request.weather_info = weather_info;
		request.os = "android";
		request.phone_model = ZSimCardInfo.getDeviceName();
		request.longitude = longitude; //自定义经度
		request.latitude = latitude; //自定义纬度
		request.postion = position; //位置
		
		request.mac = VALUE_MAC;
		request.imei = VALUE_IMEI;

		Worker worker = new Worker(handler, RESPONSE_TYPE_GET_DIARY_URL,
				GsonResponse3.getDiaryUrlResponse.class);
		worker.execute(RIA_INTERFACE_GET_DIARY_URL, request);
	}
	//2.7.2.7 日记详情(宏强)
	/**
	 * 2.7.2.7 日记详情(宏强)
	 * @param handler
	 * @param diaryid
	 * @param diarywidth
	 * @param diaryheight
	 */
	public static void getDiaryinfo(Handler handler, String diaryid,
			String diarywidth, String diaryheight) {
		GsonRequest3.diaryInfoRequest request = new GsonRequest3.diaryInfoRequest();
		request.diaryid = diaryid;
		request.diarywidth = diarywidth;
		request.userid = ActiveAccount.getInstance(ZApplication.getInstance())
				.getLookLookID();
		request.diaryheight = diaryheight;

		request.mac = VALUE_MAC;
		request.imei = VALUE_IMEI;

		Worker worker = new Worker(handler, RESPONSE_TYPE_DIARY_INFO,
				GsonResponse3.diaryInfoResponse.class);
		worker.execute(RIA_INTERFACE_DIARY_INFO, request);
	}
	//2.7.2.8 修改标签或位置(用户虚假位置信息)
	/**
	 * 2.7.2.8 修改标签或位置(用户虚假位置信息)
	 * @param handler
	 * @param diaryid
	 * @param tags
	 * @param position
	 */
	public static void modTagsOrPosition(Handler handler, String diaryid,
			String tags, String position) {
		GsonRequest3.modTagsOrPositionRequest request = new GsonRequest3.modTagsOrPositionRequest();
		request.diaryids = diaryid;
		request.tags = tags;
		request.position = position;

		request.userid = ActiveAccount.getInstance(ZApplication.getInstance())
				.getLookLookID();
		request.mac = VALUE_MAC;
		request.imei = VALUE_IMEI;

		Worker worker = new Worker(handler, RESPONSE_TYPE_MODIFY_TAG,
				GsonResponse3.modTagsOrPositionResponse.class);
		worker.execute(RIA_INTERFACE_MODIFY_TAG, request);
	}
	//2.7.2.10 设置日记权限
	public static void setDiarySharePermissions(Handler handler, String diaryid, String publish_status) {
		GsonRequest3.diarySharePermissionsRequest request = new GsonRequest3.diarySharePermissionsRequest();
		request.userid = ActiveAccount.getInstance(ZApplication.getInstance())
				.getLookLookID();
		request.diaryid = diaryid;
		request.publish_status = publish_status;
		request.mac = VALUE_MAC;
		request.imei = VALUE_IMEI;

		Worker worker = new Worker(handler, RESPONSE_TYPE_DIARY_SHARE_PERMISSIONS,
				GsonResponse3.diarySharePermissionsResponse.class);
		worker.execute(RIA_INTERFACE_DAIRY_SHARE_PERMISSIONS, request);
	}
	//2.7.29 详情页(宏强)
	/**
	 * 2.7.2.9 详情页（分享）
	 * @param handler
	 * @param diaryid
	 * @param diarywidth
	 * @param diaryheight
	 */
	public static void getDiaryShareInfo(Handler handler, String diaryid, String publishid, String viewuserid) {
		GsonRequest3.diaryShareInfoRequest request = new GsonRequest3.diaryShareInfoRequest();
		request.userid = ActiveAccount.getInstance(ZApplication.getInstance())
				.getLookLookID();
		request.diaryid = diaryid;
		request.publishid = publishid;
		request.viewuserid = viewuserid;
		request.pagesize = "30";
		request.mac = VALUE_MAC;
		request.imei = VALUE_IMEI;

		Worker worker = new Worker(handler, RESPONSE_TYPE_DIARY_SHARE_INFO,
				GsonResponse3.diaryShareInfoResponse.class);
		worker.execute(RIA_INTERFACE_DAIRY_SHARE_INFO, request);
	}
	//---2.7.3---
	//2.7.3.1 分享记录
	/**
	 * 2.7.3.1 分享记录
	 * @param handler
	 * @param "diary_time":"2232212321",  //每页记录数，第一次请求为空
	 * @param "request_type":"1",  //1新内容加载，2历史内容加载，第一次请求为空
	 * @param "pagesize":10，每页记录数
	 */
	public static void getMyPublishHistory(Handler handler, String diary_time, String request_type, String pagesize) {
		GsonRequest3.myPublishHistoryRequest request = new GsonRequest3.myPublishHistoryRequest();
		request.diary_time = diary_time;
		request.request_type = request_type;
		request.pagesize = pagesize;
		request.userid = ActiveAccount.getInstance(ZApplication.getInstance()).getLookLookID();

		request.mac = VALUE_MAC;
		request.imei = VALUE_IMEI;

		Worker worker = new Worker(handler, RESPONSE_TYPE_MY_PUBLISH_HISTORY,
				GsonResponse3.myPublishHistoryResponse.class);
		worker.execute(RIA_INTERFACE_MY_PUBLISH_HISTORY, request);
	}
	//2.7.3.2 清除分享记录(4.0暂时不用)
	/**
	 * 2.7.3.2 清除分享记录(4.0暂时不用)
	 * @param handler
	 * @param publishid
	 * @param type
	 */
	public static void cleanPublishHistory(Handler handler, String publishid, String type) {
		GsonRequest3.cleanPublishDiaryRequest request = new GsonRequest3.cleanPublishDiaryRequest();
		request.publishid = publishid;
		request.type = type;
		request.userid = ActiveAccount.getInstance(ZApplication.getInstance())
				.getLookLookID();

		request.mac = VALUE_MAC;
		request.imei = VALUE_IMEI;

		Worker worker = new Worker(handler, RESPONSE_TYPE_CLEAN_PUBLISH_HISTORY,
				GsonResponse3.cleanPublishDiaryResponse.class);
		worker.execute(RIA_INTERFACE_CLEAN_PUBLISH_HISTORY, request);
	}
	//2.7.3.3 站内分享
	/**
	 * 2.7.3.3 站内分享
	 * @param handler
	 * @param diaryid
	 * @param publish_type
	 * @param diary_type
	 * @param position_type
	 * @param audio_type
	 */
	public static void diaryPublish(Handler handler, String diaryids, String diaryuuid, String content,
			String diary_type, String position_type, String longitude, String latitude,
			String postion) {
		GsonRequest3.diaryPublishRequest request = new GsonRequest3.diaryPublishRequest();
		
		request.diaryids = diaryids;
		request.diaryuuid = diaryuuid;
		request.content = content;
		request.diary_type = diary_type;
		request.position_type = position_type;
		request.iscreategroup = "0"; //0是不创建 1是创建 客户端传递0即可  后台视情况核定
		request.isofficial = "0"; // 1代表 官方推荐的  0代表普通的
		request.longitude = longitude; //自定义经度
		request.latitude = latitude; //自定义纬度
		request.postion = postion; //位置
		
		request.userid = ActiveAccount.getInstance(ZApplication.getInstance())
				.getLookLookID();

		request.mac = VALUE_MAC;
		request.imei = VALUE_IMEI;

		Worker worker = new Worker(handler, RESPONSE_TYPE_DIARY_PUBLISH,
				GsonResponse3.diaryPublishResponse.class);
		worker.execute(RIA_INTERFACE_DIARY_PUBLISH, request);
	}
	//2.7.3.4 取消站内分享
	/**
	 * 2.7.3.4 取消站内分享
	 * @param handler
	 * @param publishid
	 */
	public static void cancelPublish(Handler handler, String publishid) {
		GsonRequest3.cancelPublishRequest request = new GsonRequest3.cancelPublishRequest();
		request.publishid = publishid;
		request.userid = ActiveAccount.getInstance(ZApplication.getInstance()).getLookLookID();


		request.mac = VALUE_MAC;
		request.imei = VALUE_IMEI;

		Worker worker = new Worker(handler, RESPONSE_TYPE_CANCEL_PUBLISH,
				GsonResponse3.cancelPublishResponse.class);
		worker.execute(RIA_INTERFACE_CANCEL_PUBLISH, request);
	}
	//2.7.3.5 删除第三方分享轨迹
	/**
	 * 2.7.3.5 删除第三方分享轨迹
	 * @param handler
	 * @param diaryid
	 * @param snstype
	 */
	public static void cancelShare(Handler handler, String diaryid, String snstype) {
		GsonRequest3.cancelShareRequest request = new GsonRequest3.cancelShareRequest();
		request.diaryid = diaryid;
		request.snstype = snstype;
		//request.userid = MyZoneFragment.userID;
		request.userid = ActiveAccount.getInstance(ZApplication.getInstance()).getLookLookID();

		request.mac = VALUE_MAC;
		request.imei = VALUE_IMEI;

		Worker worker = new Worker(handler, RESPONSE_TYPE_CANCEL_SHARE,
				GsonResponse3.cancelShareResponse.class);
		worker.execute(RIA_INTERFACE_CANCEL_SHARE, request);
	}
	//2.7.3.6 第三方社区分享
	/**
	 * 2.7.3.6 第三方社区分享
	 * @param handler
	 * @param diaryid
	 * @param position
	 * @param snscontent
	 * @param longitude
	 * @param latitrde
	 * @param sns
	 */
	public static void shareDiary(Handler handler, String diaryid,
			String publishid, String snscontent,SNS[] sns) {
		
		GsonRequest3.shareDiaryRequest request = new GsonRequest3.shareDiaryRequest();
		request.userid = ActiveAccount.getInstance(ZApplication.getInstance())
				.getLookLookID();
		request.diaryid = diaryid;
		request.publishid = publishid; //分享ID
		request.snscontent = snscontent;
		request.sns = sns;

		request.mac = VALUE_MAC;
		request.imei = VALUE_IMEI;
		Worker worker = new Worker(handler, RESPONSE_TYPE_SHARE_DIARY,
				GsonResponse3.shareDiaryResponse.class);
		worker.execute(RIA_INTERFACE_SHARE_DIARY, request);
	}
	//===2.8===
	//---2.8.1---
	//2.8.1.1 关注列表
	/**
	 * 2.8.1.1 关注列表
	 * @param handler
	 * @param user_time
	 * @param viewuserid
	 * @param pageSize
	 */
	public static void requestMyAttentionList(Handler handler, String user_time, String userid, String t_lastchange) {
		GsonRequest3.myattentionlistRequest request = new GsonRequest3.myattentionlistRequest();
		request.user_time = user_time;
		request.viewuserid = userid;
		request.pagesize = "10";
		request.mac = VALUE_MAC;
		request.imei = VALUE_IMEI;
		request.t_lastchange = t_lastchange;

		request.userid = ActiveAccount.getInstance(ZApplication.getInstance())
				.getLookLookID();

		Worker worker = new Worker(handler, RESPONSE_TYPE_MY_ATTENTIONLIST,
				GsonResponse3.myattentionlistResponse.class);
		worker.execute(RIA_INTERFACE_MY_ATTENTIONLIST, request);
	}
	//2.8.1.2 粉丝列表
	/**
	 * 2.8.1.2 粉丝列表
	 * @param handler
	 * @param user_time
	 * @param uerid
	 */
	public static void requestMyFansList(Handler handler, String user_time, String userid) {
		GsonRequest3.myfanslistRequest request = new GsonRequest3.myfanslistRequest();
		request.user_time = user_time;
		request.pagesize = "10";
		request.viewuserid = userid;
		request.mac = VALUE_MAC;
		request.imei = VALUE_IMEI;
		request.userid = ActiveAccount.getInstance(ZApplication.getInstance())
				.getLookLookID();

		Worker worker = new Worker(handler, RESPONSE_TYPE_MY_FANS_LIST,
				GsonResponse3.myfanslistResponse.class);
		worker.execute(RIA_INTERFACE_MY_FANS_LIST, request);
	}
	//2.8.1.3 朋友列表
	/**
	 * 2.8.1.3 朋友列表
	 * @param handler
	 * @param user_time
	 * @param uerid
	 */
	public static void requestMyFriendsList(Handler handler, String user_time, String userid) {
		GsonRequest3.myfriendslistRequest request = new GsonRequest3.myfriendslistRequest();
		request.user_time = user_time;
		request.pagesize = "30";
		request.viewuserid = userid;
		request.mac = VALUE_MAC;
		request.imei = VALUE_IMEI;
		request.userid = ActiveAccount.getInstance(ZApplication.getInstance())
				.getLookLookID();

		Worker worker = new Worker(handler, RESPONSE_TYPE_MY_FRIENDS_LIST,
				GsonResponse3.myfriendslistResponse.class);
		worker.execute(RIA_INTERFACE_MY_FRIENDS_LIST, request);
	}
	//2.8.1.4 获取官方账号列表
	/**
	 * 2.8.1.4 获取官方账号列表
	 * @param handler
	 */
	public static void getOfficialUserids(Handler handler) {
		GsonRequest3.getOfficialUseridsRequest request = new GsonRequest3.getOfficialUseridsRequest();
		request.userid = ActiveAccount.getInstance(ZApplication.getInstance())
				.getLookLookID();
		request.mac = VALUE_MAC;
		request.imei = VALUE_IMEI;
		Worker worker = new Worker(handler, RESPONSE_TYPE_GET_OFFICIAL_USERIDS,
				GsonResponse3.getOfficialUseridsResponse.class);
		worker.execute(RIA_INTERFACE_GET_OFFICIAL_USERIDS, request);
	}
	//2.8.1.5 黑名单列表
	/**
	 * 2.8.1.5 黑名单列表
	 * @param handler
	 * @param user_time
	 * @param viewuserid
	 * @param pageSize
	 */
	public static void myBlacklist(Handler handler, String user_time,
			String viewuserid) {
		GsonRequest3.myblacklistRequest request = new GsonRequest3.myblacklistRequest();
		request.user_time = user_time;
		request.pagesize = "10";
		request.viewuserid = viewuserid;
		request.mac = VALUE_MAC;
		request.imei = VALUE_IMEI;
		request.userid = ActiveAccount.getInstance(ZApplication.getInstance())
				.getLookLookID();

		Worker worker = new Worker(handler, RESPONSE_TYPE_MY_BLACK_LIST,
				GsonResponse3.myblacklistResponse.class);
		worker.execute(RIA_INTERFACE_MY_BLACK_LIST, request);
	}
	//2.8.1.6 获取日记赞人列表
	/**
	 * 2.8.1.6 获取日记赞人列表
	 * @param handler
	 * @param diaryid
	 * @param index
	 */
	public static void getDiaryEnjoyUsers(Handler handler, String diaryid, String publishid,
			String type, String pagesize, String diary_time, String request_type) {
		GsonRequest3.getDiaryEnjoyUsersRequest request = new GsonRequest3.getDiaryEnjoyUsersRequest();
		request.diaryid = diaryid;
		request.publishid = publishid;
		request.type = type;
		request.pagesize = pagesize;
		request.diary_time = diary_time; //每页记录数，第一次请求为空
		request.request_type = request_type; //1新内容加载，2历史内容加载，第一次请求为空
		request.mac = VALUE_MAC;
		request.imei = VALUE_IMEI;
		request.viewuserid = ActiveAccount.getInstance(ZApplication.getInstance())
				.getLookLookID();

		request.userid = ActiveAccount.getInstance(ZApplication.getInstance())
				.getLookLookID();

		Worker worker = new Worker(handler, RESPONSE_TYPE_GET_DIARY_ENJOY,
				GsonResponse3.getDiaryEnjoyUsersResponse.class);
		worker.execute(RIA_INTERFACE_GET_DIARY_ENJOY, request);
	}
	//2.8.1.7 搜索用户列表(除去系统黑名单)
	/**
	 * 2.8.1.7 搜索用户列表(除去系统黑名单)
	 * @param handler
	 * @param keyword
	 * @param type
	 */
	public static void searchUser(Handler handler, String keyword, String type) {
		GsonRequest3.searchUserRequest request = new GsonRequest3.searchUserRequest();
		request.keyword = keyword;
		request.userid = ActiveAccount.getInstance(ZApplication.getInstance())
				.getLookLookID();
		request.mac = VALUE_MAC;
		request.imei = VALUE_IMEI;
		request.type = type;

		Worker worker = new Worker(handler, RESPONSE_TYPE_SEARCH_USER,
				GsonResponse3.searchUserResponse.class);
		worker.execute(RIA_INTERFACE_SEARCH_USER, request);
	}
	//2.8.1.8 已加入looklook的第三方互为关注用户(包括手机通讯录、新浪、腾讯、人人)
	/**
	 * 2.8.1.8 已加入looklook的第三方互为关注用户(包括手机通讯录、新浪、腾讯、人人)
	 * @param handler
	 */
	public static void listUserSNS(Handler handler, String usertime){
		GsonRequest3.listUserSNSRequest request = new GsonRequest3.listUserSNSRequest();
		request.userid = ActiveAccount.getInstance(ZApplication.getInstance()).getLookLookID();
		request.mac = VALUE_MAC;
		request.imei = VALUE_IMEI;
		request.user_time = usertime;
		Worker worker = new Worker(handler, RESPONSE_TYPE_LIST_USER_SNS, GsonResponse3.listUserSNSResponse.class);
		worker.execute(RIA_INTERFACE_LIST_USER_SNS, request);
	}
	//2.8.1.9 系统推荐用户列表
	/**
	 * 2.8.1.9 系统推荐用户列表
	 * @param handler
	 * @param timestamp
	 */
	public static void listUserRecommend(Handler handler , String timestamp){
		GsonRequest3.listUserRecommendRequest request = new GsonRequest3.listUserRecommendRequest();
		request.userid = ActiveAccount.getInstance(ZApplication.getInstance()).getLookLookID();
		request.mac = VALUE_MAC;
		request.imei = VALUE_IMEI;
		request.timestamp = timestamp;
		Worker worker = new Worker(handler, RESPONSE_TYPE_LIST_USER_RECOMMEND, GsonResponse3.listUserRecommendResponse.class);
		worker.execute(RIA_INTERFACE_LIST_USER_RECOMMEND, request);
	}
	
	//2.8.1.10 好友请求列表接口
	/**
	 * 2.8.1.10 好友请求列表接口
	 * @param handler
	 * @param pagesize
	 * @param user_time
	 */
	public static void friendRequestList(Handler handler, String user_time){
		GsonRequest3.friendRequestListRequest request = new GsonRequest3.friendRequestListRequest();
		request.userid = ActiveAccount.getInstance(ZApplication.getInstance()).getLookLookID();
		request.mac = VALUE_MAC;
		request.imei = VALUE_IMEI;
		request.user_time = user_time;
		Worker worker = new Worker(handler, RESPONSE_TYPE_FRIEND_REQUEST_LIST, GsonResponse3.friendRequestListResponse.class);
		worker.execute(RIA_INTERFACE_FRIEND_REQUEST_LIST, request);
	}
	
	//---2.8.2---
	//2.8.2.1 个人信息接口
	//2.8.2.2 日记评论/回复
	/**
	 * 2.8.2.2 日记评论/回复
	 * @param handler
	 * @param commentContent
	 * @param commentId
	 * @param isReply
	 * @param diaryid
	 * @param commentType
	 * @param commentuuid
	 */
	public static void comment(Handler handler, String commentContent,
			String commentId, String isReply, String publishid, String commentType, String commentuuid, String diaryid, String comment_source) {
		GsonRequest3.commentRequest request = new GsonRequest3.commentRequest();
		request.commentcontent = commentContent;
		request.commentid = commentId;
		request.isreply = isReply;
		request.userid = ActiveAccount.getInstance(ZApplication.getInstance())
				.getLookLookID();
		request.publishid = publishid;
		request.commenttype = commentType;
		request.commentuuid = commentuuid;
		request.diaryid = diaryid;
		request.comment_source = comment_source;
		request.mac = VALUE_MAC;
		request.imei = VALUE_IMEI;

		Worker worker = new Worker(handler, RESPONSE_TYPE_COMMENT,
				GsonResponse3.commentResponse.class);
		worker.execute(RIA_INTERFACE_COMMENT, request);
	}
	//2.8.2.3 发消息 (是否发日记，不发日记looklook赞世界处理方式)
	/**
	 * 2.8.2.3 发消息 (是否发日记，不发日记looklook赞世界处理方式)
	 * @param handler
	 * @param diaryid
	 * @param content
	 * @param targetUserIds
	 * @param privatemsgtype
	 * @param uuid
	 */
	public static void sendMessage(Handler handler, String content,
			String targetUserIds, String privatemsgtype, String uuid) {
		GsonRequest3.sendmessageRequest request = new GsonRequest3.sendmessageRequest();
		request.content = content;
		request.target_userids = targetUserIds;
		request.privatemsgtype = privatemsgtype;
		request.mac = VALUE_MAC;
		request.imei = VALUE_IMEI;
		request.userid = ActiveAccount.getInstance(ZApplication.getInstance())
				.getLookLookID();
		request.uuid = uuid;

		Worker worker = new Worker(handler, RESPONSE_TYPE_SEND_MESSAGE,
				GsonResponse3.sendmessageResponse.class);
		worker.execute(RIA_INTERFACE_SEND_MESSAGE, request);
	}
	//2.8.2.4 赞 (喜欢)
	/**
	 * 2.8.2.4 赞 (喜欢)
	 * @param handler
	 * @param diaryid
	 * @param publishid
	 */
	public static void enjoy(Handler handler, String diaryid,
			String publishid) {
		GsonRequest3.enjoyRequest request = new GsonRequest3.enjoyRequest();
		request.diaryid = diaryid;
		request.publishid = publishid;

		request.mac = VALUE_MAC;
		request.imei = VALUE_IMEI;

		request.userid = ActiveAccount.getInstance(ZApplication.getInstance())
				.getLookLookID();
		Worker worker = new Worker(handler, RESPONSE_TYPE_DIARY_ENJOY,
				GsonResponse3.enjoyResponse.class);
		worker.execute(RIA_INTERFACE_DIARY_ENJOY, request);
	}
	//2.8.2.5 转发
	/**
	 * 2.8.2.5 转发
	 * @param handler
	 * @param publishid
	 * @param content
	 */
	public static void repost(Handler handler, String publishid, String content) {
		GsonRequest3.repostRequest request = new GsonRequest3.repostRequest();
		request.publishid = publishid;
		request.content = content;
		
		request.mac = VALUE_MAC;
		request.imei = VALUE_IMEI;

		request.userid = ActiveAccount.getInstance(ZApplication.getInstance())
				.getLookLookID();
		Worker worker = new Worker(handler, RESPONSE_TYPE_DIARY_REPOST,
				GsonResponse3.repostResponse.class);
		worker.execute(RIA_INTERFACE_DIARY_REPOST, request);
	}
	//2.8.2.6 取消赞
	/**
	 * 2.8.2.6 取消赞
	 * @param handler
	 * @param publishid
	 */
	public static void deleteEnjoy(Handler handler, String diaryid, String publishid) {
		GsonRequest3.deleteEnjoyRequest request = new GsonRequest3.deleteEnjoyRequest();
		request.publishid = publishid;
		request.diaryid = diaryid;
		request.mac = VALUE_MAC;
		request.imei = VALUE_IMEI;

		request.userid = ActiveAccount.getInstance(ZApplication.getInstance())
				.getLookLookID();
		Worker worker = new Worker(handler, RESPONSE_TYPE_DELETE_ENJOY_DIARY,
				GsonResponse3.deleteEnjoyResponse.class);
		worker.execute(RIA_INTERFACE_DELETE_ENJOY_DIARY, request);
	}
	//2.8.2.7 删除评论(删除自己评论和删除对自己日记进行的评论)
	/**
	 * 2.8.2.7 删除评论(删除自己评论和删除对自己日记进行的评论)
	 * @param handler
	 * @param publishid
	 * @param commentid
	 * @param commentuuid
	 */
	public static void deleteComment(Handler handler, String publishid,
			String commentid, String commentuuid) {
		GsonRequest3.deleteCommentRequest request = new GsonRequest3.deleteCommentRequest();
		request.publishid = publishid;
		request.commentid = commentid;
		request.commentuuid = commentuuid;

		request.mac = VALUE_MAC;
		request.imei = VALUE_IMEI;

		request.userid = ActiveAccount.getInstance(ZApplication.getInstance())
				.getLookLookID();
		Worker worker = new Worker(handler, RESPONSE_TYPE_DELETE_COMMENT,
				GsonResponse3.deleteCommentResponse.class);
		worker.execute(RIA_INTERFACE_DELETE_COMMENT, request);
	}
	//2.8.2.8 加关注
	/**
	 * 2.8.2.8 加关注
	 * @param handler
	 * @param attention_userid
	 */
	public static void attention(Handler handler, String attention_userid) {
		GsonRequest3.attentionRequest request = new GsonRequest3.attentionRequest();
		request.userid = ActiveAccount.getInstance(ZApplication.getInstance())
				.getLookLookID();
		request.attention_userid = attention_userid;

		request.mac = VALUE_MAC;
		request.imei = VALUE_IMEI;

		Worker worker = new Worker(handler, RESPONSE_TYPE_ATTENTION,
				GsonResponse3.attentionResponse.class);
		worker.execute(RIA_INTERFACE_ATTENTION, request);
	}
	//2.8.2.9 取消关注\删除粉丝
	/**
	 * 2.8.2.9 取消关注\删除粉丝
	 * @param handler
	 * @param target_userid
	 * @param attention_type
	 */
	public static void cancelAttention(Handler handler, String target_userid,
			String attention_type) {
		GsonRequest3.cancelattentionRequest request = new GsonRequest3.cancelattentionRequest();
		request.target_userid = target_userid;
		request.attention_type = attention_type;
		request.mac = VALUE_MAC;
		request.imei = VALUE_IMEI;
		request.userid = ActiveAccount.getInstance(ZApplication.getInstance())
				.getLookLookID();

		Worker worker = new Worker(handler, RESPONSE_TYPE_CANCEL_ATTENTION,
				GsonResponse3.cancelattentionResponse.class);
		worker.execute(RIA_INTERFACE_CANCEL_ATTENTION, request);
	}
	//2.8.2.10 备注名修改
	/**
	 * 2.8.2.10 备注名修改
	 * @param handler
	 * @param attention_userid
	 * @param attention_mark
	 */
	public static void setUserAlias(Handler handler, String attention_userid,
			String attention_mark) {
		GsonRequest3.setUserAliasRequest request = new GsonRequest3.setUserAliasRequest();
		request.attention_userid = attention_userid;
		request.attention_mark = attention_mark;
		request.mac = VALUE_MAC;
		request.imei = VALUE_IMEI;
		request.userid = ActiveAccount.getInstance(ZApplication.getInstance())
				.getLookLookID();

		Worker worker = new Worker(handler, RESPONSE_TYPE_SET_USERALIAS,
				GsonResponse3.setUserAliasResponse.class);
		worker.execute(RIA_INTERFACE_SET_USERALIAS, request);
	}
	//2.8.2.11 设置黑名单(用户黑名单)
	/**
	 * 2.8.2.11 设置黑名单(用户黑名单)
	 * @param handler
	 * @param target_userid
	 * @param operatetype
	 */
	public static void operateBlacklist(Handler handler, String target_userid,
			String operatetype) {
		GsonRequest3.operateblacklistRequest request = new GsonRequest3.operateblacklistRequest();
		request.target_userid = target_userid;
		request.operatetype = operatetype;
		request.mac = VALUE_MAC;
		request.imei = VALUE_IMEI;
		request.userid = ActiveAccount.getInstance(ZApplication.getInstance())
				.getLookLookID();
		
		Worker worker = new Worker(handler, RESPONSE_TYPE_SET_BLACKLIST,
				GsonResponse3.operateblacklistResponse.class);
		worker.execute(RIA_INTERFACE_SET_BLACKLIST, request);
	}
	/**
	 * 2.8.2.12 申请加好友
	 * @param handler
	 * @param target_userid
	 * @param request_msg
	 */
	public static void addFriend(Handler handler, String target_userid,
			String request_msg) {
		GsonRequest3.addfriendRequest request = new GsonRequest3.addfriendRequest();
		request.target_userid = target_userid;
		request.request_msg = request_msg;
		request.mac = VALUE_MAC;
		request.imei = VALUE_IMEI;
		request.userid = ActiveAccount.getInstance(ZApplication.getInstance())
				.getLookLookID();
		Worker worker = new Worker(handler, RESPONSE_TYPE_ADD_FRIEND,
				GsonResponse3.addfriendResponse.class);
		worker.execute(RIA_INTERFACE_ADD_FRIEND, request);
	}
	
	/**
	 * 2.8.2.13 同意好友请求
	 * @param handler
	 * @param target_userid
	 * @param request_msg
	 */
	public static void agreeFriendRequest(Handler handler, String target_userid) {
		GsonRequest3.agreeFriendRequest request = new GsonRequest3.agreeFriendRequest();
		request.target_userid = target_userid;
		request.mac = VALUE_MAC;
		request.imei = VALUE_IMEI;
		request.userid = ActiveAccount.getInstance(ZApplication.getInstance())
				.getLookLookID();
		
		Worker worker = new Worker(handler, RESPONSE_TYPE_AGREE_FRIEND,
				GsonResponse3.agreeFriendResponse.class);
		worker.execute(RIA_INTERFACE_AGREE_FRIEND, request);
	}
	
	/**
	 * 2.8.2.14 删除好友
	 * @param handler
	 * @param target_userid
	 * @param request_msg
	 */
	public static void deleteFriendRequest(Handler handler, String target_userid) {
		GsonRequest3.deleteFriendRequest request = new GsonRequest3.deleteFriendRequest();
		request.target_userid = target_userid;
		request.mac = VALUE_MAC;
		request.imei = VALUE_IMEI;
		request.userid = ActiveAccount.getInstance(ZApplication.getInstance())
				.getLookLookID();
		Worker worker = new Worker(handler, RESPONSE_TYPE_DELETE_FRIEND,
				GsonResponse3.deleteFriendResponse.class);
		worker.execute(RIA_INTERFACE_DELETE_FRIEND, request);
	}
	//===2.9===
	//2.9.1 创建微享
	/**
	 * 2.9.1 创建微享
	 * @param handler
	 * @param diary_id
	 * @param publishid
	 * @param content
	 * @param weather
	 * @param weather_info
	 * @param position
	 */
	public static void createMic(Handler handler, String diary_id, String uuid, String mic_title, String content,
			UserObj[] userobj, String longitude, String latitude, String position, String capsule, String burn_after_reading, String capsule_time) {
		GsonRequest3.createMicRequest request = new GsonRequest3.createMicRequest();
		request.userid = ActiveAccount.getInstance(ZApplication.getInstance()).getLookLookID();
		request.diary_id = diary_id;
		request.uuid = uuid;
		request.mic_title = mic_title;
		request.content = content; // 微享内容
		request.userobj = userobj; // 微享用户组IDS AAA|BBB|CCC|DDD
		request.capsule = capsule;
		request.burn_after_reading = burn_after_reading;
		request.capsule_time = capsule_time;
		
	
//		CommonInfo ci = CommonInfo.getInstance();
//		if (ci.myLoc != null) {
//			request.longitude = String.valueOf(ci.myLoc.longitude);
//			request.latitude = String.valueOf(ci.myLoc.latitude);
//		}
		
		request.longitude = longitude;
		request.latitude = latitude;
		request.position = position;
		
		request.os = "android";
		request.phone_model = ZSimCardInfo.getDeviceName();
		
		request.mac = VALUE_MAC;
		request.imei = VALUE_IMEI;
		Worker worker = new Worker(handler, RESPONSE_TYPE_CREATEMIC,
				GsonResponse3.createMicResponse.class);
		worker.execute(RIA_INTERFACE_CREATEMIC, request);
	}
	//2.9.2 微享列表
	/**
	 * 2.9.2 微享列表
	 */
	public static void myMicList(Handler handler, String mic_time, String request_type, String is_encrypt){
		GsonRequest3.myMicListRequest request = new GsonRequest3.myMicListRequest();
		request.userid = ActiveAccount.getInstance(ZApplication.getInstance()).getLookLookID();
		request.mac = VALUE_MAC;
		request.imei = VALUE_IMEI;
		request.mic_time = mic_time;
		request.request_type = request_type;
		request.pagesize = "30";
		request.is_encrypt = is_encrypt;
		Worker worker = new Worker(handler, RESPONSE_TYPE_MYMICLIST, GsonResponse3.myMicListResponse.class);
		worker.execute(RIA_INTERFACE_MYMICLIST, request);
	}
	
	//2.8.6 他人微享列表
	public static void MicList(Handler handler, String view_user, String mic_time, String request_type){
		GsonRequest3.MicListRequest request = new GsonRequest3.MicListRequest();
		request.userid = ActiveAccount.getInstance(ZApplication.getInstance()).getLookLookID();
		request.mac = VALUE_MAC;
		request.imei = VALUE_IMEI;
		request.mic_time = mic_time;
		request.request_type = request_type;
		request.pagesize = "30";
		request.viewuserid = view_user;
		Worker worker = new Worker(handler, RESPONSE_TYPE_MICLIST, GsonResponse3.MicListResponse.class);
		worker.execute(RIA_INTERFACE_MICLIST, request);
	}
	
	//2.9.3 微享内容详情页
	/**
	 * 2.9.3 微享内容详情页
	 * @param handler
	 * @param publishid
	 * @param userids
	 */
	public static void myMicInfo(Handler handler, String publishid, String micuserid){
		GsonRequest3.myMicInfoRequest request = new GsonRequest3.myMicInfoRequest();
		request.userid = ActiveAccount.getInstance(ZApplication.getInstance()).getLookLookID();
		request.mac = VALUE_MAC;
		request.imei = VALUE_IMEI;
		request.publishid = publishid;
		request.micuserid = micuserid;
		request.pagesize = "30";
		Worker worker = new Worker(handler, RESPONSE_TYPE_MYMICINFO, GsonResponse3.myMicInfoResponse.class);
		worker.execute(RIA_INTERFACE_MYMICINFO, request);
	}
	
	//2.6.1.7 微享评论列表
	public static void MicComments(Handler handler, String publishid, String comment_time, String request_type){
		GsonRequest3.MicCommentsRequest request = new GsonRequest3.MicCommentsRequest();
		request.userid = ActiveAccount.getInstance(ZApplication.getInstance()).getLookLookID();
		request.mac = VALUE_MAC;
		request.imei = VALUE_IMEI;
		request.publishid = publishid;
		request.comment_time = comment_time;
		request.pagesize = "30";
		request.request_type = request_type;
		request.sorttype = "1";
		Worker worker = new Worker(handler, RESPONSE_TYPE_MICCOMMENTLIST, GsonResponse3.MicCommentsResponse.class);
		worker.execute(RIA_INTERFACE_MICCOMMENTLIST, request);
	}
	
	//2.9.4 微享预览页
	/**
	 * 2.9.4 微享预览页
	 * @param handler
	 * @param pageno
	 * @param pagesize
	 * @param sortby
	 * @param mic_users
	 */
	public static void mySubMicList(Handler handler, UserObj[] userobj, String is_encrypt, String mic_time, String request_type){
		GsonRequest3.mySubMicListRequest request = new GsonRequest3.mySubMicListRequest();
		request.userid = ActiveAccount.getInstance(ZApplication.getInstance()).getLookLookID();
		request.mac = VALUE_MAC;
		request.imei = VALUE_IMEI;
		request.userobj = userobj;
		request.pagesize = "30";
		request.mic_time = mic_time;
		request.request_type = request_type;
		request.is_encrypt = is_encrypt;
		Worker worker = new Worker(handler, RESPONSE_TYPE_MYSUBMICLIST, GsonResponse3.mySubMicListResponse.class);
		worker.execute(RIA_INTERFACE_MYSUBMICLIST, request);
	}
	
	//2.9.5 微享清屏
	/**
	 * 2.9.5 微享清屏
	 * @param handler
	 * @param publishid
	 */
	public static void cleanMic(Handler handler, String publishid){
		GsonRequest3.cleanMicRequest request = new GsonRequest3.cleanMicRequest();
		request.userid = ActiveAccount.getInstance(ZApplication.getInstance()).getLookLookID();
		request.mac = VALUE_MAC;
		request.imei = VALUE_IMEI;
		request.publishid = publishid;
		Worker worker = new Worker(handler, RESPONSE_TYPE_CLEANMIC, GsonResponse3.cleanMicResponse.class);
		worker.execute(RIA_INTERFACE_CLEANMIC, request);
	}
	
	//2.9.6 微享放入保险箱
	/**
	 * 2.9.6 微享放入保险箱
	 * @param handler
	 * @param publishid
	 */
	public static void safeboxmic(Handler handler, String publishid,String join_safebox){
		GsonRequest3.safeboxmicRequest request = new GsonRequest3.safeboxmicRequest();
		request.userid = ActiveAccount.getInstance(ZApplication.getInstance()).getLookLookID();
		request.mac = VALUE_MAC;
		request.imei = VALUE_IMEI;
		request.publishid = publishid;
		request.mic_safebox =join_safebox;
		Worker worker = new Worker(handler, RESPONSE_TYPE_SAFEBOXMIC, GsonResponse3.safeboxmicResponse.class);
		worker.execute(RIA_INTERFACE_SAFEBOXMIC, request);
	}
	
	/**
	 * 2.9.7 微享放入保险箱
	 * @param handler
	 * @param publishid
	 */
	public static void reportReadMsg(Handler handler, String userid_view){
		GsonRequest3.readmsgRequest request = new GsonRequest3.readmsgRequest();
		request.userid = ActiveAccount.getInstance(ZApplication.getInstance()).getLookLookID();
		request.viewuserid = userid_view;
		request.mac = VALUE_MAC;
		request.imei = VALUE_IMEI;
		Worker worker = new Worker(handler, RESPONSE_TYPE_READ_MSG, GsonResponse3.readmsgResponse.class);
		worker.execute(RIA_INTERFACE_READ_MSG, request);
	}
	
	//2.8.7 免打扰
	public static void setundisturb(Handler handler, String publishid, String is_undisturb){
		GsonRequest3.setundisturbRequest request = new GsonRequest3.setundisturbRequest();
		request.userid = ActiveAccount.getInstance(ZApplication.getInstance()).getLookLookID();
		request.mac = VALUE_MAC;
		request.imei = VALUE_IMEI;
		request.publishid = publishid;
		request.is_undisturb = is_undisturb;
		Worker worker = new Worker(handler, RESPONSE_TYPE_SETUNDISTURB, GsonResponse3.setundisturbResponse.class);
		worker.execute(RIA_INTERFACE_SETUNDISTURB, request);
	}
	
	//===2.10===
    //2.10.1 获取活动列表(缓存)
	/**
	 * 2.10.1 获取活动列表(缓存)
	 * @param handler
	 * @param activeType
	 * @param diaryid
	 */
	public static void requestActiveList(Handler handler, String activeType,
			String diaryid) {
		GsonRequest3.activeListRequest request = new GsonRequest3.activeListRequest();
		request.activetype = activeType;
		request.userid = ActiveAccount.getInstance(ZApplication.getInstance())
				.getLookLookID();
		request.diaryid = diaryid;
		request.mac = VALUE_MAC;
		request.imei = VALUE_IMEI;

		Worker worker = new Worker(handler, RESPONSE_TYPE_GET_ACTIVELIST,
				GsonResponse3.activeListResponse.class);
		worker.execute(RIA_INTERFACE_GET_ACTIVELIST, request);
	}
	//2.10.2 参与活动日记列表
	/**
	 * 2.10.2 参与活动日记列表
	 * @param handler
	 * @param activeId
	 * @param diary_time
	 * @param pageno
	 * @param width
	 * @param height
	 * @param request_type
	 */
	public static void requestActiveDiaryList(Handler handler, String activeid,
			String diary_time, String pageno, String width, String height,
			String request_type) {
		GsonRequest3.activeDiaryListRequest request = new GsonRequest3.activeDiaryListRequest();
		request.activeid = activeid;
		request.pagesize = "10";
		request.userid = ActiveAccount.getInstance(ZApplication.getInstance())
				.getLookLookID();
		request.diaryheight = height + "";
		request.diarywidth = width + "";
		request.diary_time = diary_time;
		request.request_type = request_type;
		request.mac = VALUE_MAC;
		request.imei = VALUE_IMEI;

		Worker worker = new Worker(handler, RESPONSE_TYPE_DIARY_ACTIVELIST,
				GsonResponse3.activeDiaryListResponse.class);
		worker.execute(RIA_INTERFACE_DIARY_ACTIVELIST, request);
	}
	//2.10.3 参加活动
	/**
	 * 2.10.3 参加活动
	 * @param handler
	 * @param diaryid
	 * @param activeid
	 */
	public static void joinActive(Handler handler, String diaryid,
			String activeid) {
		GsonRequest3.joinActiveRequest request = new GsonRequest3.joinActiveRequest();
		request.userid = ActiveAccount.getInstance(ZApplication.getInstance())
				.getLookLookID();
		request.diaryid = diaryid;
		request.activeid = activeid;
		request.mac = VALUE_MAC;
		request.imei = VALUE_IMEI;

		Worker worker = new Worker(handler, RESPONSE_TYPE_JOIN_ACTIVE,
				GsonResponse3.joinActiveResponse.class);
		worker.execute(RIA_INTERFACE_JOIN_ACTIVE, request);
	}
	//2.10.4 活动中奖日记列表
	/**
	 * 2.10.4 活动中奖日记列表
	 * @param handler
	 * @param activeId
	 * @param diarywidth
	 * @param diaryheight
	 */
	public static void requestAwardDiaryList(Handler handler, String activeid,
			String diarywidth, String diaryheight) {
		GsonRequest3.getAwardDiaryListRequest request = new GsonRequest3.getAwardDiaryListRequest();
		request.activeid = activeid;
		request.userid = ActiveAccount.getInstance(ZApplication.getInstance())
				.getLookLookID();
		request.diarywidth = diarywidth;
		request.diaryheight = diaryheight;
		request.mac = VALUE_MAC;
		request.imei = VALUE_IMEI;

		Worker worker = new Worker(handler, RESPONSE_TYPE_GET_AWARD_DIARYLIST,
				GsonResponse3.getAwardDiaryListResponse.class);
		worker.execute(RIA_INTERFACE_GET_AWARD_DIARYLIST, request);
	}
	//2.10.5 取消参加活动
	/**
	 * 2.10.5 取消参加活动
	 * @param handler
	 * @param diaryid
	 * @param activeid
	 */
	public static void cancleActive(Handler handler, String diaryid, String activeid){
		GsonRequest3.cancelActiveRequest request = new GsonRequest3.cancelActiveRequest();
		request.userid = ActiveAccount.getInstance(ZApplication.getInstance()).getLookLookID();
		request.diaryid = diaryid;
		request.activeid = activeid;
		request.mac = VALUE_MAC;
		request.imei = VALUE_IMEI;
		Worker worker = new Worker(handler, RESPONSE_TYPE_CANCEL_ACTIVE, GsonResponse3.cancelActiveResponse.class);
		worker.execute(RIA_INTERFACE_CANCEL_ACTIVE, request);
	}
	//===2.11===
	//2.11.1 心跳,新消息列表(需要确定都有哪些信息)
	/**
	 * 2.11.1 心跳,新消息列表(需要确定都有哪些信息)
	 * @param handler
	 * @param timemilli
	 * @param pagesize
	 * @param messagetype
	 * @param commentid
	 * @param diaryids
	 * @param diarywidth
	 */
	public static void listMessage(Handler handler, String userid, String timemilli,
			String pagesize, String messagetype, String commentid,String commentid_safebox,
			String t_zone_mic,String t_friend,String t_safebox_miccomment,
			String t_friendrequest, String t_friend_change,String t_push, String t_zone_miccomment) {
		
		GsonRequest3.listMessageRequest request = new GsonRequest3.listMessageRequest();
		request.userid = userid;
		request.os = "2";   // 系统类型 1 ios 2 android
		if(timemilli!=null && !timemilli.equals("") && !timemilli.equals("0") ){
			request.timemilli = timemilli;
		}

		request.pagesize = pagesize;
		request.messagetype = messagetype;
		request.commentid = commentid;
		request.commentid_safebox = commentid_safebox;   // new add
		
		request.t_zone_mic=t_zone_mic;
		request.t_zone_miccomment = t_zone_miccomment;
		request.t_safebox_miccomment = t_safebox_miccomment;   // new add
		
		request.t_friend = t_friend;
		request.t_friend_change = t_friend_change;
		request.t_friendrequest = t_friendrequest;
		Log.d("renfan", "request "+t_friendrequest);
		request.t_push = t_push;
		
		//del
		//request.t_attention=t_attention;
		//request.t_fans = t_fans;
		//request.t_recommend=t_recommend;
		//request.t_snsfriend = t_snsfriend;

		request.mac = VALUE_MAC;
		request.imei = VALUE_IMEI;

		Worker worker = new Worker(handler, RESPONSE_TYPE_LIST_MESSAGE,
				GsonResponse3.listMessageResponse.class);
		worker.execute(RIA_INTERFACE_LIST_MESSAGE, request);
	}
	//2.11.2 通知服务器消息已删除
	/**
	 * 2.11.2 通知服务器消息已删除 删除多条
	 * @param handler
	 * @param messageid
	 * @param msg_type
	 */
	public static void deleteMessage(Handler handler, ArrayList<Deletedmsg> deletedmsg) {
		GsonRequest3.deleteMessageRequest request = new GsonRequest3.deleteMessageRequest();
		request.deletedmsg = deletedmsg;

		request.mac = VALUE_MAC;
		request.imei = VALUE_IMEI;

		Worker worker = new Worker(handler, RESPONSE_TYPE_DELETE_MESSAGE,
				GsonResponse3.deleteMessageResponse.class);
		worker.execute(RIA_INTERFACE_DELETE_MESSAGE, request);
	}
	//2.11.2 通知服务器消息已删除
	/**
	 * 2.11.2 通知服务器消息已删除 删除一条
	 * @param handler
	 * @param messageid
	 * @param msg_type
	 */
	public static void deleteMessage(Handler handler, String messageid,
			String messagetype, String target_userid) {
		GsonRequest3.deleteMessageRequest request = new GsonRequest3.deleteMessageRequest();
		
		ArrayList<Deletedmsg> arr = new ArrayList<Deletedmsg>();
		Deletedmsg dmsg = new Deletedmsg();
		dmsg.messageid = messageid;
		dmsg.messagetype = messagetype;
		dmsg.target_userid = target_userid;
		dmsg.userid = ActiveAccount.getInstance(ZApplication.getInstance()).getLookLookID();
		arr.add(dmsg);
		
		request.deletedmsg = arr;
		request.mac = VALUE_MAC;
		request.imei = VALUE_IMEI;
		
		Worker worker = new Worker(handler, RESPONSE_TYPE_DELETE_MESSAGE,
				GsonResponse3.deleteMessageResponse.class);
		worker.execute(RIA_INTERFACE_DELETE_MESSAGE, request);
	}

	//2.11.3 私信历史消息列表
	/**
	 * 2.11.3 私信历史消息列表
	 * @param handler
	 * @param target_userid
	 * @param timemilli
	 * @param pagesize
	 */
	public static void listHistoryMessage(Handler handler, String target_userid,
			String timemilli, String pagesize) {
		GsonRequest3.listHistoryMessageRequest request = new GsonRequest3.listHistoryMessageRequest();
		request.target_userid = target_userid;
		if(timemilli ==null || "0".equals(timemilli) ){
			request.timemilli = "";
		}else{
			request.timemilli = timemilli;
		}
		request.pagesize = pagesize;

		request.mac = VALUE_MAC;
		request.imei = VALUE_IMEI;
		request.userid = ActiveAccount.getInstance(ZApplication.getInstance())
				.getLookLookID();

		Worker worker = new Worker(handler, RESPONSE_TYPE_LIST_HISTORY_MESSAGE,
				GsonResponse3.listHistoryMessageResponse.class);
		worker.execute(RIA_INTERFACE_LIST_HISTORY_MESSAGE, request);
	}
	//2.11.4 crm回调，通知激活
	/**
	 * 2.11.4 crm回调，通知激活
	 * @param handler
	 * @param bindname
	 */
	public static void crmCallback(Handler handler, String bindname, String type) {
		GsonRequest3.crmCallbackRequest request = new GsonRequest3.crmCallbackRequest();
		request.userid = ActiveAccount.getInstance(ZApplication.getInstance())
				.getLookLookID();
		request.bindname = bindname;
		request.type = type;
		
		Worker worker = new Worker(handler, RESPONSE_TYPE_CRM_CALLBACK,
				GsonResponse3.crmCallbackResponse.class);
		worker.execute(RIA_INTERFACE_CRM_CALLBACK, request);
	}
	//2.11.5 www播放页(重新整理，改成JSON格式)
	/**
	 * 2.11.5 www播放页(重新整理，改成JSON格式)
	 * @param handler
	 * @param shortUrl
	 */
	public static void playPage(Handler handler, String shortUrl) {
		GsonRequest3.playPageRequest request = new GsonRequest3.playPageRequest();
		request.shortUrl = shortUrl;
		Worker worker = new Worker(handler, RESPONSE_TYPE_PLAY_PAGE,
				GsonResponse3.playPageResponse.class);
		worker.execute(RIA_INTERFACE_PLAY_PAGE, request);
	}
	//2.11.6 手机通讯录列表(需要确认表结构是否修改)
	/**
	 * 2.11.6 手机通讯录列表(需要确认表结构是否修改)
	 * @param handler
	 */
	public static void phoneBook(Handler handler){
		GsonRequest3.phoneBookRequest request = new GsonRequest3.phoneBookRequest();
		request.userid = ActiveAccount.getInstance(ZApplication.getInstance()).getLookLookID();
		request.mac = VALUE_MAC;
		request.imei = VALUE_IMEI;
		Worker worker = new Worker(handler, RESPONSE_TYPE_PHONE_BOOK, GsonResponse3.phoneBookResponse.class);
		worker.execute(RIA_INTERFACE_PHONE_BOOK, request);
	}
	
	//2.11.7 0客服接口
	/**
	 * 2.11.7 0客服接口
	 */
	public static void customer(Handler handler){
		GsonRequest3.customerRequest request = new GsonRequest3.customerRequest();
		request.userid = ActiveAccount.getInstance(ZApplication.getInstance()).getLookLookID();
		request.mac = VALUE_MAC;
		request.imei = VALUE_IMEI;
		Worker worker = new Worker(handler, RESPONSE_TYPE_CUSTOMER, GsonResponse3.customerResponse.class);
		worker.execute(RIA_INTERFACE_CUSTOMER, request);
	}
	
	//2.12.1 通讯录邀请好友加入”原来“
	/**
	 * 2.12.1 通讯录邀请好友加入”原来“
	 */
	public static void invatePhoneAddress(Handler handler, Invate_users[] users){
		GsonRequest3.invatePhoneAddressRequest request = new GsonRequest3.invatePhoneAddressRequest();
		request.userid = ActiveAccount.getInstance(ZApplication.getInstance()).getLookLookID();
		request.invate_users = users;
		request.mac = VALUE_MAC;
		request.imei = VALUE_IMEI;
		Worker worker = new Worker(handler, RESPONSE_TYPE_INVATEPHONEADDRESS, GsonResponse3.invatePhoneAddressResponse.class);
		worker.execute(RIA_INTERFACE_INVATEPHONEADDRESS, request);
	}
	
	
	//========================================================================
	
	/**
	 * 检测网络类型: 2G/3G/WIFI;
	 * 
	 * @return
	 */
	public static String getNetType() {
		String result = VALUE_EMPTY;
		if (ZNetworkStateDetector.isWifi()) {
			result = VALUE_WIFI;
		} else {
			int type = ZNetworkStateDetector.getMobileType();
			switch (type) { // 没验证过这种2G/3G的检测方式是否正确, 暂时这样实现;
			case TelephonyManager.NETWORK_TYPE_UMTS:
			case TelephonyManager.NETWORK_TYPE_HSDPA:
			case TelephonyManager.NETWORK_TYPE_HSPA:
			case TelephonyManager.NETWORK_TYPE_HSUPA:
			case TelephonyManager.NETWORK_TYPE_EVDO_0:
			case TelephonyManager.NETWORK_TYPE_EVDO_A:
				result = VALUE_3G;
				break;

			case TelephonyManager.NETWORK_TYPE_CDMA:
			case TelephonyManager.NETWORK_TYPE_EDGE:
			case TelephonyManager.NETWORK_TYPE_GPRS:
				result = VALUE_GPRS;
				break;
			}
		}
		return result;
	}

	/**
	 * 保存关键数据 (包括公用数据和账户数据) (应该是Base64解码后的);
	 * 
	 * @param responseType
	 * @param object
	 */
	private static void saveData(int responseType, Object object) {
		switch (responseType) {
		case Requester3.RESPONSE_TYPE_UA: // 提交UA响应;
			if (object != null) {
				GsonResponse3.uaResponse response = (GsonResponse3.uaResponse) object;
				CommonInfo.getInstance().equipmentid = response.equipmentid;
				CommonInfo.getInstance().ip = response.ip;

				// AppState.setUaResponse(response);
			}
			break;

		case Requester3.RESPONSE_TYPE_REGISTER: // 请求注册响应;

			break;

		case Requester3.RESPONSE_TYPE_LOGIN: // 请求登录响应;
//			GsonResponse3.loginResponse response = (GsonResponse3.loginResponse) object;
//			ActiveAccount.getInstance(ZApplication.getInstance())
//					.setLookLookID(response.userid);
			break;

		case Requester3.RESPONSE_TYPE_GET_SOCKET: // 请求socket地址响应;

			break;

		case Requester3.RESPONSE_TYPE_BINDING:

			break;

		}
	}

	/**
	 * ------------------------------------------------------------------------
	 * 内部类, 用来与RIA交换数据;
	 * 
	 * @author Sunshine
	 */
	public static class Worker extends Thread {
		private Handler handler;
		private Class<?> cls;
		private Gson gson;
		private int responseType;
		private String ria_command_id;
		private Object request;
		private boolean extra=false;// only for homepage

		/**
		 * 
		 * @param handler
		 * @param responseType: 响应类型;
		 * @param cls: 响应对象的class类型;
		 */
		public Worker(Handler handler, int responseType, Class<?> cls) {
			this.handler = handler;
			this.responseType = responseType;
			this.cls = cls;
			this.gson = new Gson();
		}

		public void execute(String ria_command_id, Object request) {
			// TODO Auto-generated method stub
			this.ria_command_id = ria_command_id;
			this.request = request;
			this.start();
			
		}
		
		public void execute(String ria_command_id, Object request, boolean extra) {
			this.extra = extra;
			execute(ria_command_id, request);
		}

		@Override
		public void run(){

			Object object = null;
			ZHttp2 http2 = null;
			ZHttpReader reader = null;

			int retryTimes = 0;
			boolean b = false;

			try {
				String url = Config.SERVER_URL_TEST + ria_command_id;
				// System.out.println("url--->" + url);
				// String url = "http://172.16.1.217:8080/vs/api";
				// //http://172.16.1.217:8080/vs/api
				
				/*************
				 * 增加调试主机地址.之后移除.
				 * *****************/
				url = debugHostUrl(url,ria_command_id);
				String json = gson.toJson(request);
				
				ZLog.e(">> Request2 ("
						+ request.getClass().getSimpleName() + "): "
						+ json);
				
				json = REQUEST_HEADER + URLEncoder.encode(json, "UTF-8");

//				ZLog.e(">> Request2 ("
//						+ request.getClass().getSimpleName() + "): "
//						+ json);

				http2 = new ZHttp2();
				ZHttpResponse httpResponse = http2.post(url,
						json.getBytes("UTF-8"));
				reader = new ZHttpReader(httpResponse.getInputStream(),
						null);
				String string = new String(reader.readAll(0));
				
				/*FriendsActivity.saveLog(string);*/
				
				object = gson.fromJson(string, cls);
				ZLog.e("<< Response2 (" + cls.getSimpleName() + "): " + string);

			} catch (Exception e) {
				e.printStackTrace();
				Log.e(TAG, "ria_command_id:" + ria_command_id);
				b = true;

			} finally {
				if (http2 != null) {
					http2.close();
				}
				if (reader != null) {
					reader.close();
				}

				if (responseType == RESPONSE_TYPE_CREATE_STRUCTURE) {
					if (object == null) {
						GsonResponse3.createStructureResponse response = new createStructureResponse();
						response.diaryuuid = ((GsonRequest3.createStructureRequest)request).diaryuuid;
						Log.d("=AAA=","uuid = " + response.diaryuuid);
						response.status = "-1";
						object = response;
					} else {
						Log.d("=AAA=","responseuuid = " + ((GsonResponse3.createStructureResponse)object).diaryuuid + "requestuuid = " +  ((GsonRequest3.createStructureRequest)request).diaryuuid);
						if (((GsonResponse3.createStructureResponse)object).diaryuuid == null || "".equals(((GsonResponse3.createStructureResponse)object).diaryuuid)){
							((GsonResponse3.createStructureResponse)object).diaryuuid = ((GsonRequest3.createStructureRequest)request).diaryuuid;
							Log.d("=AAA=","uuid = " + ((GsonResponse3.createStructureResponse)object).diaryuuid);
						}
					}
				}
				
				if(responseType == RESPONSE_TYPE_MERGER_ACCOUNT) {
					if (object != null) {
						GsonResponse3.mergerAccountResponse response = new mergerAccountResponse();
						response.crm_status = ((GsonResponse3.mergerAccountResponse)object).crm_status;
						response.status = ((GsonResponse3.mergerAccountResponse)object).status;
						response.newUserid = ((GsonRequest3.mergerAccountRequest)request).userid;
						response.oldUserid = ((GsonRequest3.mergerAccountRequest)request).userid_beMerged;
						object = response;
					}
				}

				if (extra
						&& (responseType == RESPONSE_TYPE_HOME || responseType == RESPONSE_TYPE_LIST_MY_DIARY)) {
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				
			}

			if (object != null) {
				try {
					saveData(responseType, object);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			if (handler != null) {
				Message message = handler.obtainMessage(responseType, object);
				try {
					handler.sendMessage(message);
				} catch (Exception e) {
					ZLog.alert();
					ZLog.e("Perhaps sending message to a Handler on a dead thread.");
				}
			} else {
				ZLog.alert();
				ZLog.e("handler is null, data can not callback.");
			}
		
		
		}

		/**
		 * 测试主机地址
		 * @param url
		 * @param ria_command_id
		 * @return
		 */
		private String debugHostUrl(String url,String ria_command_id) {
			// 
//			if(RIA_INTERFACE_PASSWORD_CHANGE.equals(ria_command_id)
//					|| RIA_INTERFACE_LOGIN.equals(ria_command_id) 
//					|| RIA_INTERFACE_REGISTER.equals(ria_command_id)){
//				url = "http://172.16.1.100:7074/vs/api" + ria_command_id;
//			}
			return url;
		}


	}
	
//	public static void uploadPicture(Handler handler,String filePath,String type,String attachid){
////		filePath="/mnt/sdcard/Pictures/yuyin.jpg";
//		HttpPoster poster=new HttpPoster(handler,RESPONSE_TYPE_UPLOAD_PICTURE,GsonResponse3.uploadPictrue.class);
//		GsonRequest3.uploadPicture requester=new GsonRequest3.uploadPicture();
//		requester.upload_pic_type=type;
//		requester.userid = ActiveAccount.getInstance(ZApplication.getInstance()).getLookLookID();
//		requester.attachid = attachid;
//		requester.mac = VALUE_MAC;
//		requester.imei = VALUE_IMEI;
//		poster.execute(Config.SERVER_URL_PICTURE+RIA_INTERFACE_UPLOAD_PICTURE, requester, filePath);
//		
//	}

	/**
	 * ------------------------------------------------------------------------
	 * 内部类, 用来向图片服务器上传图片;
	 * 
	 * @author Sunshine
	 */
	private static class HttpPoster extends Thread {

		private Handler handler;
		private Class<?> cls;
		private Gson gson;
		private int responseType;
		private String json;
		private String url;
		private String filePath;

		public HttpPoster(Handler handler, int responseType, Class<?> cls) {
			this.handler = handler;
			this.responseType = responseType;
			this.cls = cls;
			this.gson = new Gson();
		}
		
		public void execute(String url, Object request, String filePath) {
			// TODO Auto-generated method stub
			this.url = url;
			this.filePath=filePath;
			this.json = gson.toJson(request);
			this.start();
			
		}

		@Override
		public void run(){

			Object object = null;
			ZHttp http = null;

			int retryTimes = 0;
			boolean b = false;
			
			if(!new File(filePath).exists()){
				if(filePath.startsWith("http://")){
					ZHttp2 http2 = new ZHttp2();
					ZHttpResponse httpResponse = http2.get(filePath);
					FileOutputStream stream;
					try {
						File file = new File(Environment.getExternalStorageDirectory() + Constant.SD_STORAGE_ROOT + "/tmp/pic/" + "tmp.jpg");
						if(file.exists()){
							file.delete();
						}
						
						if(!file.getParentFile().exists()){
							file.getParentFile().mkdirs();
						}
						
						Bitmap thumb = BitmapHelper.getBitmapFromInputStream(httpResponse.getInputStream());
						
						stream = new FileOutputStream(file);
						thumb.compress(CompressFormat.JPEG, 50, stream);
						filePath = file.getAbsolutePath();
					} catch (FileNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
				}
			}

			while (true) {
				try {

					MultipartEntity entity = new MultipartEntity();
					entity.addPart("requestapp", new StringBody(json, Charset.forName("UTF-8")));
					entity.addPart("photo", new FileBody(new File(filePath)));
//					entity.addPart("photo", new ByteArrayBody(data,""));

					http = new ZHttp();
					HttpResponse response = http.post(url, entity);

					byte[] bytes = EntityUtils.toByteArray(response.getEntity());
					String string = new String(bytes, "UTF-8");
					Log.e(TAG, "uploadPicture response="+string);
					object = gson.fromJson(string, cls);

				} catch (Exception e) {
					e.printStackTrace();
					b = true;

				} finally {
					if (http != null) {
						http.shutdown();
					}
					if (b && retryTimes < 3) {
						retryTimes++;
						ZThread.sleep(1500 * retryTimes + 500);// 500, 2000,
																// 3500, 5000
					} else {
						break;
					}
				}
			}

			if (handler != null) {
				Message message = handler.obtainMessage(responseType, object);
				handler.sendMessage(message);
			} else {
				ZLog.alert();
				ZLog.e("handler is null, data can not callback.");
			}
		
		}
	}
	
	/**
	 * 68. 设置空间背景图 (从背景图列表中选择的);
	 * 
	 * @param handler
	 * @param imageUrl
	 *            : 从列表中选择的图片的url;
	 */
	public static void setUserSpaceCover(Handler handler, String imageUrl) {
		GsonRequest3.setUserSpaceCoverRequest request = new GsonRequest3.setUserSpaceCoverRequest();
		request.imagepath = imageUrl;
		request.userid = ActiveAccount.getInstance(ZApplication.getInstance())
				.getLookLookID();
		request.mac = VALUE_MAC;
		request.imei = VALUE_IMEI;

		Worker worker = new Worker(handler, RESPONSE_TYPE_SET_SPACECOVER,
				GsonResponse3.setUserSpaceCoverResponse.class);
		worker.execute(RIA_INTERFACE_SET_SPACECOVER, request);
	}
	
	
/*	public static void uploadPicture(Handler handler,String filePath,String type,String attachid){
//		filePath="/mnt/sdcard/Pictures/yuyin.jpg";
		HttpPoster poster=new HttpPoster(handler,RESPONSE_TYPE_UPLOAD_PICTURE,GsonResponse3.uploadPictrue.class);
		GsonRequest3.uploadPicture requester=new GsonRequest3.uploadPicture();
		requester.upload_pic_type=type;
		requester.userid = ActiveAccount.getInstance(ZApplication.getInstance()).getLookLookID();
		requester.attachid = attachid;
		requester.mac = VALUE_MAC;
		requester.imei = VALUE_IMEI;
		poster.execute(Config.SERVER_URL_PICTURE+RIA_INTERFACE_UPLOAD_PICTURE, requester, filePath);
		
	}*/
}