package com.cmmobi.looklook.common.gson;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.nio.charset.Charset;

import org.apache.http.HttpResponse;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.util.EntityUtils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.telephony.TelephonyManager;
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
import cn.zipper.framwork.utils.ZStringUtils;
import cn.zipper.framwork.utils.ZThread;

import com.baidu.mapapi.map.LocationData;
import com.cmmobi.looklook.Config;
import com.cmmobi.looklook.MainApplication;
import com.cmmobi.looklook.activity.FriendsActivity;
import com.cmmobi.looklook.common.constant.Constant;
import com.cmmobi.looklook.common.gson.GsonRequest2.AddrBook;
import com.cmmobi.looklook.common.gson.GsonRequest2.Attachs;
import com.cmmobi.looklook.common.gson.GsonRequest2.SNS;
import com.cmmobi.looklook.common.gson.GsonRequest2.createStructureRequest;
import com.cmmobi.looklook.common.gson.GsonRequest2.postFollowItem;
import com.cmmobi.looklook.common.gson.GsonRequest2.postWeibocountItem;
import com.cmmobi.looklook.common.gson.GsonResponse2.createStructureResponse;
import com.cmmobi.looklook.info.profile.ActiveAccount;
import com.cmmobi.looklook.info.profile.CommonInfo;
import com.cmmobi.sns.api.CmmobiSnsLib;
import com.cmmobi.sns.oauthv2.OAuthV2;
import com.cmmobi.sns.utils.BitmapHelper;
import com.cmmobi.sns.utils.ConfigUtil.SHARE_TO;
import com.cmmobi.statistics.CmmobiClickAgent;
import com.google.gson.Gson;
//import com.cmmobi.looklook.common.utils.Base64Utils;

/**
 * @author wangrui
 */

@SuppressWarnings("unused")
public final class Requester2 {

	public static final String REQUEST_HEADER = "requestapp=";

	public static final String RIA_INTERFACE_UA = "/ua";
	public static final String RIA_INTERFACE_REGISTER = "/user/register";
	public static final String RIA_INTERFACE_CHANGE_USER_INFO = "/user/changeUserInfo";
	public static final String RIA_INTERFACE_PASSWORD_CHANGE = "/user/passwordChange";
	public static final String RIA_INTERFACE_CHECKNO = "/user/getCheckNo";
	public static final String RIA_INTERFACE_MOOD = "/user/mood";
	public static final String RIA_INTERFACE_DIARY_PRIVACY = "/user/privacy";
	public static final String RIA_INTERFACE_ADD_GESTURE_PASSWORD = "/user/addGesturepassword";
	public static final String RIA_INTERFACE_CHECK_USER = "/user/checkuser";
	public static final String RIA_INTERFACE_LOGIN = "/user/login";
	// 10
	public static final String RIA_INTERFACE_GET_SOCKET = "/user/getSocket";
	public static final String RIA_INTERFACE_CREATE_STRUCTURE = "/structureManager";
	public static final String RIA_INTERFACE_DIARY_PUBLISH = "/diary/publish";
	public static final String RIA_INTERFACE_GET_WEATHER = "/weather";
	public static final String RIA_INTERFACE_SHARE_DIARY = "/shareDiary";
	public static final String RIA_INTERFACE_LIST_MY_DIARY = "/listMyDiary";
	public static final String RIA_INTERFACE_TAG_DIARY_LIST = "/tagDiaryList";
	public static final String RIA_INTERFACE_LIST_SAFEBOX = "/diary/listsafebox";
	// public static final String RIA_INTERFACE_LIST_PUBLISH_DIARY =
	// "/listMyPublishDiary";
	public static final String RIA_INTERFACE_SAFEBOX = "/diary/safebox";
	public static final String RIA_INTERFACE_ATTACH_CONTENT = "/diary/attachcontent";
	// 20
	public static final String RIA_INTERFACE_LIST_COLLECT_DIARY = "/listCollectDiary";
	public static final String RIA_INTERFACE_ADD_COLLECT_DIARY = "/collect/addcollectDiary";
	public static final String RIA_INTERFACE_REMOVE_COLLECT_DIARY = "/collect/removeCollectDiary";
	public static final String RIA_INTERFACE_POST_LOG = "/postLog";
	public static final String RIA_INTERFACE_FEEDBACK = "/feedback";
	public static final String RIA_INTERFACE_BINDING = "/binding";
	// public static final String RIA_INTERFACE_LIST_MESSAGE = "/listMessage";
	public static final String RIA_INTERFACE_POST_SNSFRIEND = "/user/postSNSFriend";
	public static final String RIA_INTERFACE_AUTOLOGIN = "/user/autoLogin";
	public static final String RIA_INTERFACE_REPORT = "/report";
	public static final String RIA_INTERFACE_POST_WEIBOCOUNT = "/postWeiboCount";
	// 30

	public static final String RIA_INTERFACE_FORGET_PASSWORD = "/user/forgetPassword";
	public static final String RIA_INTERFACE_DIARY_COMMENTLIST = "/diaryCommentList";
	public static final String RIA_INTERFACE_COMMENT = "/comment";
	public static final String RIA_INTERFACE_DELETE_DIARY = "/deleteDiary";
	public static final String RIA_INTERFACE_GET_DIARY_URL = "/getDiaryUrl";
	public static final String RIA_INTERFACE_DIARY_INFO = "/diaryInfo";
	public static final String RIA_INTERFACE_CHECK_USERNAME = "/user/checkUserNameExist";
	public static final String RIA_INTERFACE_CHECK_NICKNAME = "/user/checkNickNameExist";
	public static final String RIA_INTERFACE_SEARCH_USER = "/user/searchUser";
	public static final String RIA_INTERFACE_NEAR_DIARY = "/nearDiary";
	// 40
	public static final String RIA_INTERFACE_TAGLIST = "/diary/taglist";
	public static final String RIA_INTERFACE_ATTENTION = "/user/attention";
	public static final String RIA_INTERFACE_CANCEL_ATTENTION = "/user/cancelattention";
	public static final String RIA_INTERFACE_BAKEUP_ATTENTION = "/user/setUserAlias";
	public static final String RIA_INTERFACE_SET_BLACKLIST = "/user/operateBlacklist";
	public static final String RIA_INTERFACE_MY_ATTENTIONLIST = "/user/myattentionlist";

	public static final String RIA_INTERFACE_MY_FANS_LIST = "/user/myfanslist";
	public static final String RIA_INTERFACE_MY_BLACK_LIST = "/user/myBlacklist";
	public static final String RIA_INTERFACE_TIMELINE = "/timeline";
	public static final String RIA_INTERFACE_SEND_MESSAGE = "/user/sendMessage";
	// 50
	// public static final String RIA_INTERFACE_GET_PRIVATE_MESSAGE =
	// "/getPrivateMessage";
	public static final String RIA_INTERFACE_DIARY_RECOMMEND = "/diaryrecommend";
	public static final String RIA_INTERFACE_DIARY_ENJOY = "/diary/enjoy";
	// 53
	// 54
	public static final String RIA_INTERFACE_UNBIND = "/unbind";
	public static final String RIA_INTERFACE_DELETE_COMMENT = "/deleteComment";
	public static final String RIA_INTERFACE_SET_VIDEO_COVER = "/diary/setVideoCover";
	public static final String RIA_INTERFACE_SET_HEADIMAGE = "/user/setHeadImage";
	public static final String RIA_INTERFACE_GET_ACTIVELIST = "/active/activeList";
	public static final String RIA_INTERFACE_DIARY_ACTIVELIST = "/active/activeDiaryList";
	// 60
	// 61
	public static final String RIA_INTERFACE_SET_ACTIVEIMAGE = "/active/setActiveImage";
	public static final String RIA_INTERFACE_DELETE_MESSAGE = "/user/deleteMessage";
	public static final String RIA_INTERFACE_LIST_THIRDPLATFORM = "/user/listRecommendUser";
	public static final String RIA_INTERFACE_GET_MESSAGECOUNT = "/user/getMessageCount";
	public static final String RIA_INTERFACE_GET_DIARY_ENJOY = "/diary/getDiaryEnjoyUsers";
	public static final String RIA_INTERFACE_GET_DIARY_FORWORD = "/diary/getDiaryForwardUsers";
	public static final String RIA_INTERFACE_SET_SPACECOVER = "/user/setUserSpaceCover";
	public static final String RIA_INTERFACE_GET_SPACECOVER_LIST = "/getSpaceCoverList";
	// public static final String RIA_INTERFACE_LIST_ENJOY_DIARY =
	// "/user/listEnjoyDiary";
	// 70
	public static final String RIA_INTERFACE_FORWARD_DIARY_LIST = "/listEnjoyDiary";
	public static final String RIA_INTERFACE_FORWARD_DIARY_ID = "/user/listEnjoyDiary";
	public static final String RIA_INTERFACE_MODIFY_TAG = "/diary/modTagsOrPosition";
	public static final String RIA_INTERFACE_LIST_SHARE_DIARY = "/listMyPublishDiary";
	public static final String RIA_INTERFACE_HOME = "/user/home";
	public static final String RIA_INTERFACE_DELETE_AND_ENJOY = "/diary/deletepublishAndEnjoy";
	// 77
	// 78
	public static final String RIA_INTERFACE_GET_AWARD_DIARYLIST = "/active/getAwardDiaryList";
	// 80
	public static final String RIA_INTERFACE_JOIN_ACTIVE = "/active/joinActive";
	public static final String RIA_INTERFACE_NOTIFY_PRIVATE_MESSAGE = "/user/notifyPrivmsg";
	public static final String RIA_INTERFACE_NOTIFY_MESSAGE = "/user/notifyMessage";
	public static final String RIA_INTERFACE_POST_ADDRESSBOOK = "/user/postAddressBook";
	public static final String RIA_INTERFACE_DIARY_PERMISSION = "/user/diaryPermissions";
	public static final String RIA_INTERFACE_DIARY_SET_MOOD = "/diary/setMood";
	public static final String RIA_INTERFACE_LIST_MESSAGE = "/user/listMessage";
	public static final String RIA_INTERFACE_CLEAR_STRANGER_MESSAGE = "/user/clearStrangerMessage";
	public static final String RIA_INTERFACE_CRM_CALLBACK = "/crm/callback";
	public static final String RIA_INTERFACE_PLAY_PAGE = "/playPage";
    //90
	public static final String RIA_INTERFACE_GET_OFFICIAL_USERIDS = "/getOfficialUserids";
	public static final String RIA_INTERFACE_GET_MD5KEY = "/getMD5KEY";
	public static final String RIA_INTERFACE_LIST_COLLECT_DIARYID = "/listCollectDiaryid";
	public static final String RIA_INTERFACE_GET_EFFECTS = "/getEffects";
	public static final String RIA_INTERFACE_UPLOAD_PICTURE = "/upload?";
	//96
	public static final String RIA_INTERFACE_UNSAFEBOX = "/user/unSafebox";
	public static final String RIA_INTERFACE_CANCEL_ACTIVE = "/active/cancleActive";
	//100
	public static final String RIA_INTERFACE_PHONE_BOOK = "/user/phoneBook";
	public static final String RIA_INTERFACE_GETCLOUDSIZE = "/getCloudSize";
	public static final String RIA_INTERFACE_LIST_USER_SNS = "/user/listUserSNS";
	public static final String RIA_INTERFACE_LIST_USER_RECOMMEND = "/user/listUserRecommend";
	//
	// =========================================================================================

	public static final int RESPONSE_TYPE_UA = 0xfffff001;
	public static final int RESPONSE_TYPE_REGISTER = 0xfffff002;
	public static final int RESPONSE_TYPE_CHANGE_USER_INFO = 0xfffff003;
	public static final int RESPONSE_TYPE_PASSWORD_CHANGE = 0xfffff004;
	public static final int RESPONSE_TYPE_CHECKNO = 0xfffff005;
	public static final int RESPONSE_TYPE_MOOD = 0xfffff006;
	public static final int RESPONSE_TYPE_DIARY_PRIVACY = 0xfffff007;
	public static final int RESPONSE_TYPE_ADD_GESTURE_PASSWORD = 0xfffff008;
	public static final int RESPONSE_TYPE_CHECK_USER = 0xfffff009;
	public static final int RESPONSE_TYPE_LOGIN = 0xfffff010;
	// 10
	public static final int RESPONSE_TYPE_GET_SOCKET = 0xfffff011;
	public static final int RESPONSE_TYPE_CREATE_STRUCTURE = 0xfffff012;
	public static final int RESPONSE_TYPE_DIARY_PUBLISH = 0xfffff013;
	public static final int RESPONSE_TYPE_GET_WEATHER = 0xfffff014;
	public static final int RESPONSE_TYPE_SHARE_DIARY = 0xfffff015;
	public static final int RESPONSE_TYPE_LIST_MY_DIARY = 0xfffff016;
	public static final int RESPONSE_TYPE_TAG_DIARY_LIST = 0xfffff017;
	public static final int RESPONSE_TYPE_LIST_SAFEBOX = 0xfffff018;
	// public static final int RESPONSE_TYPE_LIST_PUBLISH_DIARY =
	// "/listMyPublishDiary";
	public static final int RESPONSE_TYPE_SAFEBOX = 0xfffff019;
	public static final int RESPONSE_TYPE_ATTACH_CONTENT = 0xfffff020;
	// 20
	public static final int RESPONSE_TYPE_LIST_COLLECT_DIARY = 0xfffff021;
	public static final int RESPONSE_TYPE_ADD_COLLECT_DIARY = 0xfffff022;
	public static final int RESPONSE_TYPE_REMOVE_COLLECT_DIARY = 0xfffff023;
	public static final int RESPONSE_TYPE_POST_LOG = 0xfffff024;
	public static final int RESPONSE_TYPE_FEEDBACK = 0xfffff025;
	public static final int RESPONSE_TYPE_BINDING = 0xfffff026;
	// public static final int RESPONSE_TYPE_LIST_MESSAGE = "/listMessage";
	public static final int RESPONSE_TYPE_POST_SNSFRIEND = 0xfffff027;
	public static final int RESPONSE_TYPE_AUTOLOGIN = 0xfffff028;
	public static final int RESPONSE_TYPE_REPORT = 0xfffff029;
	public static final int RESPONSE_TYPE_POST_WEIBOCOUNT = 0xfffff030;
	// 30

	public static final int RESPONSE_TYPE_FORGET_PASSWORD = 0xfffff031;
	public static final int RESPONSE_TYPE_DIARY_COMMENTLIST = 0xfffff032;
	public static final int RESPONSE_TYPE_COMMENT = 0xfffff033;
	public static final int RESPONSE_TYPE_DELETE_DIARY = 0xfffff034;
	public static final int RESPONSE_TYPE_GET_DIARY_URL = 0xfffff035;
	public static final int RESPONSE_TYPE_DIARY_INFO = 0xfffff036;
	public static final int RESPONSE_TYPE_CHECK_USERNAME = 0xfffff037;
	public static final int RESPONSE_TYPE_CHECK_NICKNAME = 0xfffff038;
	public static final int RESPONSE_TYPE_SEARCH_USER = 0xfffff039;
	public static final int RESPONSE_TYPE_NEAR_DIARY = 0xfffff040;
	// 40
	public static final int RESPONSE_TYPE_TAGLIST = 0xfffff041;
	public static final int RESPONSE_TYPE_ATTENTION = 0xfffff042;
	public static final int RESPONSE_TYPE_CANCEL_ATTENTION = 0xfffff043;
	public static final int RESPONSE_TYPE_BAKEUP_ATTENTION = 0xfffff044;
	public static final int RESPONSE_TYPE_SET_BLACKLIST = 0xfffff045;
	public static final int RESPONSE_TYPE_MY_ATTENTIONLIST = 0xfffff046;

	public static final int RESPONSE_TYPE_MY_FANS_LIST = 0xfffff047;
	public static final int RESPONSE_TYPE_MY_BLACK_LIST = 0xfffff048;
	public static final int RESPONSE_TYPE_TIMELINE = 0xfffff049;
	public static final int RESPONSE_TYPE_SEND_MESSAGE = 0xfffff050;
	// 50
	// public static final int RESPONSE_TYPE_GET_PRIVATE_MESSAGE =
	// "/getPrivateMessage";
	public static final int RESPONSE_TYPE_DIARY_RECOMMEND = 0xfffff051;
	public static final int RESPONSE_TYPE_DIARY_ENJOY = 0xfffff052;
	// 53
	// 54
	public static final int RESPONSE_TYPE_UNBIND = 0xfffff055;
	public static final int RESPONSE_TYPE_DELETE_COMMENT = 0xfffff056;
	public static final int RESPONSE_TYPE_SET_VIDEO_COVER = 0xfffff057;
	public static final int RESPONSE_TYPE_SET_HEADIMAGE = 0xfffff058;
	public static final int RESPONSE_TYPE_GET_ACTIVELIST = 0xfffff059;
	public static final int RESPONSE_TYPE_DIARY_ACTIVELIST = 0xfffff060;
	// 60
	// 61
	public static final int RESPONSE_TYPE_SET_ACTIVEIMAGE = 0xfffff062;
	public static final int RESPONSE_TYPE_DELETE_MESSAGE = 0xfffff063;
	public static final int RESPONSE_TYPE_LIST_THIRDPLATFORM = 0xfffff064;
	public static final int RESPONSE_TYPE_GET_MESSAGECOUNT = 0xfffff065;
	public static final int RESPONSE_TYPE_GET_DIARY_ENJOY = 0xfffff066;
	public static final int RESPONSE_TYPE_GET_DIARY_FORWORD = 0xfffff067;
	public static final int RESPONSE_TYPE_SET_SPACECOVER = 0xfffff068;
	public static final int RESPONSE_TYPE_GET_SPACECOVER_LIST = 0xfffff069;
	public static final int RESPONSE_TYPE_LIST_ENJOY_DIARY = 0xfffff070;
	// 70
	public static final int RESPONSE_TYPE_FORWARD_DIARY_LIST = 0xfffff071;
	public static final int RESPONSE_TYPE_FORWARD_DIARY_ID = 0xfffff072;
	public static final int RESPONSE_TYPE_MODIFY_TAG = 0xfffff073;
	public static final int RESPONSE_TYPE_LIST_SHARE_DIARY = 0xfffff074;
	public static final int RESPONSE_TYPE_HOME = 0xfffff075;
	public static final int RESPONSE_TYPE_DELETE_AND_ENJOY = 0xfffff076;
	// 77
	// 78
	public static final int RESPONSE_TYPE_GET_AWARD_DIARYLIST = 0xfffff079;
	// 80
	public static final int RESPONSE_TYPE_JOIN_ACTIVE = 0xfffff081;
	public static final int RESPONSE_TYPE_NOTIFY_PRIVATE_MESSAGE = 0xfffff082;
	public static final int RESPONSE_TYPE_NOTIFY_MESSAGE = 0xfffff083;
	public static final int RESPONSE_TYPE_POST_ADDRESSBOOK = 0xfffff084;
	public static final int RESPONSE_TYPE_DIARY_PERMISSION = 0xfffff085;
	public static final int RESPONSE_TYPE_DIARY_SET_MOOD = 0xfffff086;
	public static final int RESPONSE_TYPE_LIST_MESSAGE = 0xfffff087;
	public static final int RESPONSE_TYPE_CLEAR_STRANGER_MESSAGE = 0xfffff088;
	public static final int RESPONSE_TYPE_CRM_CALLBACK = 0xfffff089;
	public static final int RESPONSE_TYPE_PLAY_PAGE = 0xfffff090;
    //90
	public static final int RESPONSE_TYPE_GET_OFFICIAL_USERIDS = 0xfffff091;
	public static final int RESPONSE_TYPE_GET_MD5KEY = 0xfffff092;
	public static final int RESPONSE_TYPE_LIST_COLLECT_DIARYID = 0xfffff093;
	public static final int RESPONSE_TYPE_GET_EFFECTS = 0xfffff095;
	public static final int RESPONSE_TYPE_UPLOAD_PICTURE = 0xfffff196;
	//96
	public static final int RESPONSE_TYPE_UNSAFEBOX = 0xfffff096;
	public static final int RESPONSE_TYPE_CANCEL_ACTIVE = 0xfffff097;
	//100
	public static final int RESPONSE_TYPE_PHONE_BOOK = 0xfffff100;
	public static final int RESPONSE_TYPE_GETCLOUDSIZE = 0xfffff101;
	public static final int RESPONSE_TYPE_LIST_USER_SNS = 0xfffff102;
	public static final int RESPONSE_TYPE_LIST_USER_RECOMMEND = 0xfffff103;
	public static final String VALUE_EMPTY = "";
	public static String VALUE_IMEI = ZStringUtils.nullToEmpty(ZSimCardInfo.getIMEI());
	public static String VALUE_IMSI = ZStringUtils.nullToEmpty(ZSimCardInfo.getIMSI());
	public static String VALUE_MAC = ZStringUtils.nullToEmpty(ZSimCardInfo.getDeviceMac());
	public static final String VALUE_CLIENT_VERSION = "1_0_0";
	public static final String VALUE_WIFI = "WIFI";
	public static final String VALUE_GPRS = "GPRS";
	public static final String VALUE_3G = "3G";
	public static final String VALUE_CHANNEL_NUMBER = "looklook";
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
	private Requester2() {
	}

	// 需要Base64编码的字段: 用户名、用户昵称、用户密码、分享内容、评论内容、意见反馈、
	// 微博昵称、loc逆地理信息、19. videotitle 、水印：允许使用符号,标签名称,tag。

	/**
	 * 1. 提交手机UA协议;
	 * 
	 * @param handler
	 */
	public static void submitUA(Handler handler) {
		VALUE_IMEI = ZStringUtils.nullToEmpty(ZSimCardInfo.getIMEI());
		VALUE_MAC = ZStringUtils.nullToEmpty(ZSimCardInfo.getDeviceMac());
		VALUE_IMSI = ZStringUtils.nullToEmpty(ZSimCardInfo.getIMSI());
		
		GsonRequest2.uaRequest request = new GsonRequest2.uaRequest();
		request.basestationinfo = VALUE_EMPTY;
		request.cdr = VALUE_EMPTY;
		request.channel_number = VALUE_CHANNEL_NUMBER;
		request.clientversion = VALUE_CLIENT_VERSION;
		// request.columnsversion = VALUE_EMPTY;
		request.devicetype = ZSimCardInfo.getDeviceBrand() + " " + ZSimCardInfo.getDeviceName();
		ZLog.e("devicetype = " + request.devicetype);
		request.sdk_imei = CmmobiClickAgent.getImei(MainApplication.getAppInstance());
		if (CommonInfo.getInstance().equipmentid == null) {
			request.equipmentid = VALUE_EMPTY;
		} else {
			request.equipmentid = CommonInfo.getInstance().equipmentid;
		}

		// request.errorlog = VALUE_EMPTY;
		request.gps = VALUE_EMPTY;
		request.imei = ZSimCardInfo.getIMEI();
		request.imsi = ZSimCardInfo.getIMSI();
		request.internetway = getNetType();
		request.ip = "192.168.1.1";//ZStringUtils.nullToEmpty(ZNetworkStateDetector.getIpV4Address());
		request.mac = ZStringUtils.nullToEmpty(ZNetworkStateDetector.getMacAddress());
		request.resolution = VALUE_RESOLUTION;
		request.siteid = "3";
		request.systemversionid = VALUE_DEVICE_TYPE;

		Worker worker = new Worker(handler, RESPONSE_TYPE_UA,
				GsonResponse2.uaResponse.class);
		worker.execute(RIA_INTERFACE_UA, request);

	}

	/**
	 * 2. 用户注册协议;
	 * 
	 * @param handler
	 */
	public static void register(Handler handler, String nick, String username, String passwd, String check_no, String registertype) {
		GsonRequest2.registerRequest request = new GsonRequest2.registerRequest();
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
		request.password = passwd;
		request.sex = VALUE_SEX_UNKNOW;
		//request.tag = "今天10号线挤死我了");
		request.registertype = registertype;
		request.username = username; //key, mail addr,
		request.check_no = check_no;
				
		Worker worker = new Worker(handler, RESPONSE_TYPE_REGISTER, GsonResponse2.registerResponse.class);
		worker.execute(RIA_INTERFACE_REGISTER, request);
	}

	/**
	 * 3. 修改用户信息;
	 * 
	 * @param handler
	 * @param nickname
	 *            : 昵称;
	 * @param signature
	 *            : 心情;
	 * @param sex
	 *            : 性别;
	 * @param address
	 *            : 地址;
	 * @param birthdate
	 *            : 生日;
	 * @param mood
	 *            : 心情;
	 */
	public static void changeUserInfo(Handler handler, String nickname,
			String signature, String sex, String address,
			String birthdate, String mood) {
		GsonRequest2.changeUserInfoRequest request = new GsonRequest2.changeUserInfoRequest();
		request.address = address;
		request.birthdate = birthdate;
		request.equipmentid = CommonInfo.getInstance().equipmentid;
		request.nickname = nickname;
		request.sex = sex;
		request.signature = signature;
		request.mood_id = mood;
		request.userid = ActiveAccount.getInstance(ZApplication.getInstance())
				.getLookLookID();
		// request.username =
		// ActiveAccount.getInstance(ZApplication.getInstance()).username;
		request.imei = VALUE_IMEI;
		request.mac = VALUE_MAC;
		Worker worker = new Worker(handler, RESPONSE_TYPE_CHANGE_USER_INFO,
				GsonResponse2.changeUserInfoResponse.class);
		worker.execute(RIA_INTERFACE_CHANGE_USER_INFO, request);
	}

	/**
	 * 4. 修改登录密码、手势密码;
	 * 
	 * @param handler
	 * @param oldPassword
	 *            : 原始密码(当前密码);
	 * @param newPassword
	 *            : 新密码;
	 */
	public static void passwordChange(Handler handler, String oldPassword,
			String newPassword, String pwd_type) {
		GsonRequest2.passwordChangeRequest request = new GsonRequest2.passwordChangeRequest();
		request.newpassword = newPassword;
		request.oldpassword = oldPassword;
		request.pwd_type = pwd_type;
		request.userid = ActiveAccount.getInstance(ZApplication.getInstance())
				.getLookLookID();
		request.mac = VALUE_MAC;
		request.imei = VALUE_IMEI;
		Worker worker = new Worker(handler, RESPONSE_TYPE_PASSWORD_CHANGE,
				GsonResponse2.passwordChangeResponse.class);
		worker.execute(RIA_INTERFACE_PASSWORD_CHANGE, request);
	}

	/**
	 * 5. 获取手机验证码;
	 * 
	 * @param handler
	 * @param username
	 *            手机号;
	 */
	public static void getCheckNo(Handler handler, String username, String check_type) {
		GsonRequest2.CheckNoRequest request = new GsonRequest2.CheckNoRequest();
		request.username = username;
		request.check_type = check_type;
		request.mac = VALUE_MAC;
		request.imei = VALUE_IMEI;
		Worker worker = new Worker(handler, RESPONSE_TYPE_CHECKNO,
				GsonResponse2.checkNoResponse.class);
		worker.execute(RIA_INTERFACE_CHECKNO, request);
	}

	/**
	 * 6.	修改用户心情或修改个人签名;
	 * 
	 * @param handler
	 * @param mood_id
	 *            : 心情ID;
	 * @param signature
	 * 			  : 个人签名
	 */
	public static void changeMood(Handler handler, String mood_id, String signature) {
		GsonRequest2.moodRequest request = new GsonRequest2.moodRequest();
		request.mood_id = mood_id;
		request.userid = ActiveAccount.getInstance(ZApplication.getInstance())
				.getLookLookID();
		request.mac = VALUE_MAC;
		request.imei = VALUE_IMEI;
		request.signature = signature;
		Worker worker = new Worker(handler, RESPONSE_TYPE_MOOD,
				GsonResponse2.moodResponse.class);
		worker.execute(RIA_INTERFACE_MOOD, request);
	}

	/**
	 * 7. 隐私设置;
	 * 
	 * @param handler
	 * @param privmsg_type: 私信隐私;
	 * @param friends_type: 朋友关系隐私;
	 * @param diary_type: 日记隐私;
	 * @param position_type: 地理位置隐私;
	 * @param audio_type: 语音隐私;
	 * @param audio_encrypt_type: 语音加密类型;
	 * @param launch_type: 观看模式;
	 * @param sync_type: 数据同步;
	 */
	public static void setPrivacy(Handler handler, String privmsg_type,
			String friends_type, String diary_type, String position_type,
			String audio_type, String audio_encrypt_type, String launch_type,
			String sync_type) {
		GsonRequest2.diaryPrivacyRequest request = new GsonRequest2.diaryPrivacyRequest();
		request.privmsg_type = privmsg_type;
		request.friends_type = friends_type;
		request.diary_type = diary_type;
		request.position_type = position_type;
		request.audio_type = audio_type;
		request.audio_encrypt_type = audio_encrypt_type;
		request.launch_type = launch_type;
		request.sync_type = sync_type;
		request.userid = ActiveAccount.getInstance(ZApplication.getInstance())
				.getLookLookID();
		request.mac = VALUE_MAC;
		request.imei = VALUE_IMEI;
		Worker worker = new Worker(handler, RESPONSE_TYPE_DIARY_PRIVACY,
				GsonResponse2.diaryPrivacyResponse.class);
		worker.execute(RIA_INTERFACE_DIARY_PRIVACY, request);
	}

	/**
	 * 8. 添加手势密码;
	 * 
	 * @param handler
	 * @param gesturepassword
	 *            : 手势密码;
	 */
	public static void addGesturePassword(Handler handler,
			String gesturepassword) {
		GsonRequest2.addGesturePasswordRequest request = new GsonRequest2.addGesturePasswordRequest();
		request.gesturepassword = gesturepassword;
		request.userid = ActiveAccount.getInstance(ZApplication.getInstance())
				.getLookLookID();
		request.mac = VALUE_MAC;
		request.imei = VALUE_IMEI;
		Worker worker = new Worker(handler, RESPONSE_TYPE_ADD_GESTURE_PASSWORD,
				GsonResponse2.addGesturePasswordResponse.class);
		worker.execute(RIA_INTERFACE_ADD_GESTURE_PASSWORD, request);
	}

	/**
	 * 9. 判断用户是否有效（专供socket使用）;
	 * 
	 * @param handler
	 * @param mac
	 *            : 可为空;
	 * @param imei
	 *            : 可为空;
	 */
	public static void checkuser(Handler handler, String mac, String imei) {
		GsonRequest2.checkUserRequest request = new GsonRequest2.checkUserRequest();
		request.mac = VALUE_MAC;
		request.imei = VALUE_IMEI;
		request.userid = ActiveAccount.getInstance(ZApplication.getInstance())
				.getLookLookID();

		Worker worker = new Worker(handler, RESPONSE_TYPE_CHECK_USER,
				GsonResponse2.checkuserResponse.class);
		worker.execute(RIA_INTERFACE_CHECK_USER, request);
	}
	


	/**
	 * 10. 用户登录协议;
	 * 
	 * @param handler
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

		GsonRequest2.loginRequest request = new GsonRequest2.loginRequest();
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
			}
			
			if(oauth!=null){
				request.openkey = oauth.getOpenkey();
				request.access_token = oauth.getAccessToken();
				request.expires_in = oauth.getExpiresIn();
				request.expiration_time = String.valueOf(oauth.getLongExpiresTime());
				request.refresh_token = oauth.getRefreshToken();
			}
			
			
		} else {// looklook
			request.snstype = "0";
			request.username = acct.username;
			request.password = acct.password;
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
		request.sex = acct.sex;// uPI.sex;
		request.birthdate = acct.birthdate;
		request.address = acct.address;// base6
		// request.signature = acct.signature); // base64

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
				GsonResponse2.loginResponse.class);
		worker.execute(RIA_INTERFACE_LOGIN, request);
	}

	/**
	 * 11. 获取socket IP 端口;
	 * 
	 * @param handler
	 */
	public static void requestSocketIp(Handler handler) {
		GsonRequest2.getSocketRequest request = new GsonRequest2.getSocketRequest();
		// request.filetype = VALUE_FILE_TYPE;
		request.userid = ActiveAccount.getInstance(ZApplication.getInstance())
				.getLookLookID();
		request.mac = VALUE_MAC;
		request.imei = VALUE_IMEI;

		Worker worker = new Worker(handler, RESPONSE_TYPE_GET_SOCKET,
				GsonResponse2.getSocketResponse.class);
		worker.execute(RIA_INTERFACE_GET_SOCKET, request);
	}

	/**
	 * 12. 日记结构管理;
	 * 
	 * @param handler
	 * @param diaryid
	 *            ;
	 * @param diaryuuid
	 *            ;
	 * @param resourcediaryid
	 *            ; //原日记id，operate_diarytype=3有效
	 * @param operate_diarytype
	 *            ;//操作类型，1新建，2更新，3保存副本（另存为）
	 * @param tags
	 *            ;
	 * @param attachs
	 *            []; //附件集合
	 * @param logitude
	 *            ;
	 * @param latitude
	 *            ;
	 */
	public static GsonRequest2.createStructureRequest createStructure(Handler handler, String diaryid,
			String diaryuuid, String operate_diarytype, String resourcediaryid,String resourcediaryuuid,
			String logitude,String latitude,String tags,String userselectposition,String userselectlogitude,
			String userselectlatitude,String createtime,String addresscode,Attachs[] attachs) {
		GsonRequest2.createStructureRequest request = new GsonRequest2.createStructureRequest();
		request.diaryid = diaryid;// "3390";
		request.diaryuuid = diaryuuid;
		request.resourcediaryid = resourcediaryid;
		request.resourcediaryuuid = resourcediaryuuid;
		request.operate_diarytype = operate_diarytype;// 操作类型，1新建，2更新，3保存副本
		request.tags = tags;
		request.logitude = logitude;
		request.latitude = latitude;
		request.userselectlatitude = userselectlatitude;
		request.userselectlogitude = userselectlogitude;
		request.attachs = attachs;
		request.userid = ActiveAccount.getInstance(ZApplication.getInstance())
				.getLookLookID();
		request.addresscode = addresscode;
		request.createtime = createtime;
		request.userselectposition = userselectposition;
		request.mac = VALUE_MAC;
		request.imei = VALUE_IMEI;

		Worker worker = new Worker(handler, RESPONSE_TYPE_CREATE_STRUCTURE,
				GsonResponse2.createStructureResponse.class);
		worker.execute(RIA_INTERFACE_CREATE_STRUCTURE, request);
	
		return request;
	}
	
	public static GsonRequest2.createStructureRequest createEmptyStructure(Handler handler, String diaryid,
			String diaryuuid, String operate_diarytype, String resourcediaryid,String resourcediaryuuid,
			String logitude,String latitude,String tags,String userselectposition,String userselectlogitude,
			String userselectlatitude,String createtime,String addresscode,Attachs[] attachs) {
		GsonRequest2.createStructureRequest request = new GsonRequest2.createStructureRequest();
		request.diaryid = diaryid;// "3390";
		request.diaryuuid = diaryuuid;
		request.resourcediaryid = resourcediaryid;
		request.resourcediaryuuid = resourcediaryuuid;
		request.operate_diarytype = operate_diarytype;// 操作类型，1新建，2更新，3保存副本
		request.tags = tags;
		request.logitude = logitude;
		request.latitude = latitude;
		request.userselectlatitude = userselectlatitude;
		request.userselectlogitude = userselectlogitude;
		request.attachs = attachs;
		request.userid = ActiveAccount.getInstance(ZApplication.getInstance())
				.getLookLookID();
		request.createtime = createtime;
		request.addresscode = addresscode;
		request.userselectposition = userselectposition;
		request.mac = VALUE_MAC;
		request.imei = VALUE_IMEI;
		return request;
	}

	/**
	 * 13. 发布/取消发布;
	 * 
	 * @param handler
	 * @param diaryid
	 *            ;
	 * @param publish_type
	 *            ;
	 */
	public static void diaryPublish(Handler handler, String diaryid,
			String publish_type, String diary_type, String position_type,
			String audio_type) {
		GsonRequest2.diaryPublishRequest request = new GsonRequest2.diaryPublishRequest();
		request.diaryid = diaryid;// "3390";
		request.publish_type = publish_type;// 发布类型，1发布，2取消发布
		request.diary_type = diary_type;// "1";
		request.position_type = position_type;// "1";
		request.audio_type = audio_type;// "1";
		request.userid = ActiveAccount.getInstance(ZApplication.getInstance())
				.getLookLookID();

		request.mac = VALUE_MAC;
		request.imei = VALUE_IMEI;

		Worker worker = new Worker(handler, RESPONSE_TYPE_DIARY_PUBLISH,
				GsonResponse2.diaryPublishResponse.class);
		worker.execute(RIA_INTERFACE_DIARY_PUBLISH, request);
	}

	/**
	 * 14. 获取天气;
	 * 
	 * @param handler
	 * @param addresscode
	 *            ;
	 */
	public static void getWeather(Handler handler, String addresscode) {
		GsonRequest2.getWeatherRequest request = new GsonRequest2.getWeatherRequest();
		request.addresscode = addresscode;
		request.userid = ActiveAccount.getInstance(ZApplication.getInstance())
				.getLookLookID();
		request.mac = VALUE_MAC;
		request.imei = VALUE_IMEI;
		Worker worker = new Worker(handler, RESPONSE_TYPE_GET_WEATHER,
				GsonResponse2.getWeatherResponse.class);
		worker.execute(RIA_INTERFACE_GET_WEATHER, request);
	}

	/**
	 * 15. 分享;
	 * 
	 * @param handler
	 * @param diaryid
	 *            : 当前正要分享的日记id;
	 * @param position
	 * @param snscontent
	 * @param publishtype
	 * @param sns
	 *            []
	 */
	public static void shareDiary(Handler handler, String diaryid,
			String position, String snscontent,String longitude,String latitrde, SNS[] sns) {
		GsonRequest2.shareDiaryRequest request = new GsonRequest2.shareDiaryRequest();
		request.userid = ActiveAccount.getInstance(ZApplication.getInstance())
				.getLookLookID();
		request.diaryid = diaryid;
		request.position = position;
		request.longitude = longitude;
		request.latitude = latitrde;
		request.snscontent = snscontent;
		request.sns = sns;

		request.mac = VALUE_MAC;
		request.imei = VALUE_IMEI;
		Worker worker = new Worker(handler, RESPONSE_TYPE_SHARE_DIARY,
				GsonResponse2.shareDiaryResponse.class);
		worker.execute(RIA_INTERFACE_SHARE_DIARY, request);
	}

	/**
	 * 16. 我的日记;
	 * 
	 * @param handler
	 * @param viewUserId
	 *            : 当前正要浏览的用户的id;
	 */
	public static void requestMyDiary(Handler handler, String viewUserId,
			String diaryTime, String type, String cloud_type,
			String safebox_type,String diarywidth) {
		Log.d(TAG, "diaryTime=" + diaryTime);
		Log.d(TAG, "request_type=" + type);
		GsonRequest2.listMyDiaryRequest request = new GsonRequest2.listMyDiaryRequest();

		request.pagesize = "10";
		request.userid = ActiveAccount.getInstance(ZApplication.getInstance())
				.getLookLookID();

		// request.userid = "5358e7db0646f04a820bcb20ebc2e7818a70";
		// request.viewuserid = "5358e7db0646f04a820bcb20ebc2e7818a70";

		request.diarywidth = diarywidth;
		request.diaryheight = VALUE_EMPTY;

		request.diary_time = diaryTime;
		request.request_type = type;

		request.viewuserid = viewUserId;

		request.cloud_type = cloud_type;
		request.safebox_type = safebox_type;

		request.mac = VALUE_MAC;
		request.imei = VALUE_IMEI;

		Worker worker = new Worker(handler, RESPONSE_TYPE_LIST_MY_DIARY,
				GsonResponse2.listMyDiaryResponse.class);
		worker.execute(RIA_INTERFACE_LIST_MY_DIARY, request);
	}

	/**
	 * 17. 标签日记列表;
	 * 
	 * @param handler
	 * @param viewUserId
	 *            : 当前正要浏览的用户的id;
	 * 
	 */
	public static void tagDiaryList(Handler handler, String viewUserId,
			String diaryTime, String type, String tagid,String diarywidth,String diaryheight) {
		GsonRequest2.tagDiaryListRequest request = new GsonRequest2.tagDiaryListRequest();
		request.pagesize = "10";
		request.userid = ActiveAccount.getInstance(ZApplication.getInstance())
				.getLookLookID();
		request.diarywidth = VALUE_EMPTY;
		request.diaryheight = VALUE_EMPTY;

		request.diary_time = diaryTime;
		request.request_type = type;

		request.viewuserid = viewUserId;
		request.tagid = tagid;
		request.mac = VALUE_MAC;
		request.imei = VALUE_IMEI;

		Worker worker = new Worker(handler, RESPONSE_TYPE_TAG_DIARY_LIST,
				GsonResponse2.tagDiaryListResponse.class);
		worker.execute(RIA_INTERFACE_TAG_DIARY_LIST, request);
	}

	/**
	 * 18. 保险箱日记列表;
	 * 
	 * @param handler
	 * @param viewUserId
	 *            : 当前正要浏览的用户的id;
	 * 
	 */
	public static void listSafeBox(Handler handler, String diaryTime,
			String type,String diarywidth,String diaryheight) {
		GsonRequest2.listsafeboxRequest request = new GsonRequest2.listsafeboxRequest();
		request.pagesize = "10";
		request.userid = ActiveAccount.getInstance(ZApplication.getInstance())
				.getLookLookID();
		request.diarywidth = diarywidth;
		request.diaryheight = diaryheight;

		request.diary_time = diaryTime;
		request.request_type = type;

		request.mac = VALUE_MAC;
		request.imei = VALUE_IMEI;

		Worker worker = new Worker(handler, RESPONSE_TYPE_LIST_SAFEBOX,
				GsonResponse2.listsafeboxResponse.class);
		worker.execute(RIA_INTERFACE_LIST_SAFEBOX, request);
	}

	/**
	 * 19. 日记加入、删除保险箱;
	 * 
	 * @param handler
	 * @param diaryid
	 *            //日记ID
	 * @param type
	 *            //类型，1加入保险箱 2移除保险箱
	 * 
	 */
	public static void safebox(Handler handler, String diaryid, String diaryuuid, String type) {
		GsonRequest2.safeboxRequest request = new GsonRequest2.safeboxRequest();
		request.userid = ActiveAccount.getInstance(ZApplication.getInstance())
				.getLookLookID();
		request.diaryid = diaryid;
		request.diaryuuid = diaryuuid;
		request.type = type;

		request.mac = VALUE_MAC;
		request.imei = VALUE_IMEI;

		Worker worker = new Worker(handler, RESPONSE_TYPE_SAFEBOX,
				GsonResponse2.safeboxResponse.class);
		worker.execute(RIA_INTERFACE_SAFEBOX, request);
	}

	/**
	 * 20. 编辑、添加日记文本（辅件）;
	 * 
	 * @param handler
	 * @param diaryid
	 *            //日记ID
	 * @param attachcontent
	 *            //日记辅内容文本、base64编码
	 * @param tags
	 *            //标签集合
	 * @param operate_diarytype
	 *            //操作类型，1覆盖，2新建，3副本
	 * 
	 */
	public static void attachContent(Handler handler, String diaryid,
			String attachcontent, String tags, String operate_diarytype) {
		GsonRequest2.attachContentRequest request = new GsonRequest2.attachContentRequest();
		request.userid = ActiveAccount.getInstance(ZApplication.getInstance())
				.getLookLookID();
		request.diaryid = diaryid;
		request.attachcontent = attachcontent;
		request.tags = tags;
		request.operate_diarytype = operate_diarytype;

		request.mac = VALUE_MAC;
		request.imei = VALUE_IMEI;

		Worker worker = new Worker(handler, RESPONSE_TYPE_ATTACH_CONTENT,
				GsonResponse2.attachContentResponse.class);
		worker.execute(RIA_INTERFACE_ATTACH_CONTENT, request);
	}

	/**
	 * 21. 列表收藏日记;
	 * 
	 * @param handler
	 * @param viewuserid
	 * @param request_time
	 *            //每页记录数，第一次请求为空
	 * @param request_type
	 *            //1新内容加载，2历史内容加载，第一次请求为空
	 * @param diarywidth
	 *            //日记封面需要显示的宽度，可以为空
	 * @param diaryheight
	 *            //日记封面需要显示的高度，可以为空
	 * 
	 */
	public static void listCollectDiary(Handler handler, String viewuserid,
			String request_time, String request_type, String diarywidth,
			String diaryheight) {
		GsonRequest2.listCollectDiaryRequest request = new GsonRequest2.listCollectDiaryRequest();
		request.userid = ActiveAccount.getInstance(ZApplication.getInstance())
				.getLookLookID();
		request.viewuserid = viewuserid;
		request.diary_time = request_time;
		request.request_type = request_type;
		request.diarywidth = diarywidth;
		request.diaryheight = diaryheight;

		request.mac = VALUE_MAC;
		request.imei = VALUE_IMEI;

		Worker worker = new Worker(handler, RESPONSE_TYPE_LIST_COLLECT_DIARY,
				GsonResponse2.listCollectDiaryResponse.class);
		worker.execute(RIA_INTERFACE_LIST_COLLECT_DIARY, request);
	}

	// /**
	// * 17. 我的分享日记;
	// *
	// * @param handler
	// * @param viewUserId
	// * : 当前正要浏览的用户的id;
	// */
	// public static void requestMyPublishDiary(Handler handler, String
	// viewUserId) {
	// GsonRequest2.listMyPublishDiaryRequest request = new
	// GsonRequest2.listMyPublishDiaryRequest();
	// request.pagesize = "10";
	// request.userid =
	// ActiveAccount.getInstance(ZApplication.getInstance()).getLookLookID();
	// request.diarywidth = VALUE_EMPTY;
	// request.diarywidth = VALUE_EMPTY;
	//
	// request.request_time = VALUE_EMPTY;
	// request.request_type = VALUE_EMPTY;
	//
	// request.viewuserid = viewUserId;
	//
	// Worker worker = new Worker(handler, RESPONSE_TYPE_LIST_PUBLISH_DIARY,
	// GsonResponse2.listMyPublishDiaryResponse.class);
	// worker.execute(RIA_INTERFACE_LIST_PUBLISH_DIARY, request);
	// }

	/**
	 * 22. 收藏日记;
	 * 
	 * @param handler
	 * @param diaryid
	 *            : 当前正要浏览的用户的id;
	 */
	public static void addcollectDiary(Handler handler, String diaryid) {
		GsonRequest2.addCollectDiaryRequest request = new GsonRequest2.addCollectDiaryRequest();
		request.userid = ActiveAccount.getInstance(ZApplication.getInstance())
				.getLookLookID();
		request.diaryid = diaryid;

		request.mac = VALUE_MAC;
		request.imei = VALUE_IMEI;

		Worker worker = new Worker(handler, RESPONSE_TYPE_ADD_COLLECT_DIARY,
				GsonResponse2.addCollectDiaryResponse.class);
		worker.execute(RIA_INTERFACE_ADD_COLLECT_DIARY, request);
	}

	/**
	 * 23. 收藏删除;
	 * 
	 * @param handler
	 * @param diaryids
	 *            : 多个日记ID，用逗号进行分隔;
	 */
	public static void removeCollectDiary(Handler handler, String diaryids) {
		GsonRequest2.removeCollectDiaryRequest request = new GsonRequest2.removeCollectDiaryRequest();
		request.userid = ActiveAccount.getInstance(ZApplication.getInstance())
				.getLookLookID();
		request.diaryids = diaryids;
		
		request.mac = VALUE_MAC;
		request.imei = VALUE_IMEI;
		
		Worker worker = new Worker(handler, RESPONSE_TYPE_REMOVE_COLLECT_DIARY,
				GsonResponse2.removeCollectDiaryResponse.class);
		worker.execute(RIA_INTERFACE_REMOVE_COLLECT_DIARY, request);
	}

	/**
	 * 24. 发送日志信息（客户端CDR话单），提醒产品要记录那些信息;
	 * 
	 * @param handler
	 * @param log
	 *            : CDR话单字符串;
	 */
	public static void postLog(Handler handler, String log) {
		GsonRequest2.postLogRequest request = new GsonRequest2.postLogRequest();
		request.userid = ActiveAccount.getInstance(ZApplication.getInstance())
				.getLookLookID();
		request.log = log;

		request.mac = VALUE_MAC;
		request.imei = VALUE_IMEI;

		Worker worker = new Worker(handler, RESPONSE_TYPE_POST_LOG,
				GsonResponse2.postLogResponse.class);
		worker.execute(RIA_INTERFACE_POST_LOG, request);
	}

	/**
	 * 25. 意见反馈;
	 * 
	 * @param handler
	 * @param feedbackContent
	 *            : 反馈内容;
	 */
	public static void feedback(Handler handler, String feedbackContent) {
		GsonRequest2.feedbackRequest request = new GsonRequest2.feedbackRequest();
		request.commentcontent = feedbackContent;
		request.equipmentid = CommonInfo.getInstance().equipmentid;
		request.userid = ActiveAccount.getInstance(ZApplication.getInstance())
				.getLookLookID();

		request.mac = VALUE_MAC;
		request.imei = VALUE_IMEI;

		Worker worker = new Worker(handler, RESPONSE_TYPE_FEEDBACK,
				GsonResponse2.feedbackResponse.class);
		worker.execute(RIA_INTERFACE_FEEDBACK, request);
	}
	
	

	
	



	/**
	 * 26. 绑定
	 * 
	 * @param handler
	 * @param binding_type : 绑定账户类型 : 1邮箱，2手机，3第三放平台
	 * @param phone_type : 1为绑定主手机号，2绑定备用手机号
	 * @param binding_info : 绑定账户信息 : 13823236666或123456@qq.com （binding_type!=3有效）
	 * @param snsuid: 微博账户id; binding_type=3有效
	 * @param snstype : 微博类型;binding_type=3有效，1：新浪微博，2人人，6腾讯
	 * @param check_no: 验证码，base64，binding_type=2有效
	 * @param sex: 性别 0男 1女 2未知,snstype>0时
	 * @param address: snstype>0时，国标编码
	 * @param birthdate: 出生日期,snstype>0时
	 * @param access_token: 第三方token值snstype>0时有效
	 * @param expires_in : 过期时间snstype>0时有效
	 * @param refresh_token : 刷新token,只有腾讯，人人有snstype>0时有效
	 * @param snsname  : name第三方用户名，只有腾讯（腾讯字段name）snstype>0时有效
	 * @param openkey  : 只有腾讯snstype>0时有效
	 * @param nickname : 第三方昵称snstype>0时有效
	 * @param expiration_time : 第三方有效时间(到时间点)
	 */
	public static void bindAccount(Handler handler, String binding_type,
			String phone_type, String binding_info, String snsuid,
			String snstype, String check_no, String sex, String address,
			String birthdate, String access_token, String expires_in,
			String expiration_time, String refresh_token, String snsname,
			String openkey, String nickname) {
		GsonRequest2.bindingRequest request = new GsonRequest2.bindingRequest();
		request.binding_type = binding_type;
		request.phone_type = phone_type;
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
		
		request.sex = sex;

		request.address = address;
		request.birthdate = birthdate;

		request.access_token = access_token;
		request.expires_in = expires_in;
		request.expiration_time = expiration_time;
		request.refresh_token = refresh_token;
		request.snsname = snsname;
		request.openkey = openkey;
		request.userid = ActiveAccount.getInstance(ZApplication.getInstance()).getLookLookID();
		request.mac = VALUE_MAC;
		request.imei = VALUE_IMEI;
		
		request.nickname = nickname;
		Worker worker = new Worker(handler, RESPONSE_TYPE_BINDING,
				GsonResponse2.bindingResponse.class);
		worker.execute(RIA_INTERFACE_BINDING, request);
	}

	/**
	 * 27. 发送第三方互粉
	 * 
	 * @param handler
	 * @param snstype
	 * @param upload_type
	 *            : 上传类型，1：第一次上传，2：1+n次上传
	 * @param mac
	 *            : 手机MAC地址
	 * @param imei
	 *            : 手机IMEI
	 * @param snsfriends
	 *            []
	 */
	public static void postSNSFriend(Handler handler, String snstype,
			String upload_type, postFollowItem[] snsfriends) {
		GsonRequest2.postSNSFriendRequest request = new GsonRequest2.postSNSFriendRequest();
		request.userid = ActiveAccount.getInstance(ZApplication.getInstance())
				.getLookLookID();
		request.snstype = snstype;
		request.upload_type = upload_type;
		request.snsfriends = snsfriends;
		request.mac = VALUE_MAC;
		request.imei = VALUE_IMEI;
		Worker worker = new Worker(handler, RESPONSE_TYPE_POST_SNSFRIEND,
				GsonResponse2.postSNSFriendResponse.class);
		worker.execute(RIA_INTERFACE_POST_SNSFRIEND, request);
	}

	/**
	 * 28. 自动登录设置deviceToken绑定
	 * 
	 * @param handler
	 * @param devicetoken
	 *            : 设备token
	 * @param mac
	 *            : MAC地址
	 * @param imei
	 *            : 手机IMEI
	 */
	public static void autoLogin(Handler handler, String devicetoken) {
		VALUE_IMEI = ZStringUtils.nullToEmpty(ZSimCardInfo.getIMEI());
		VALUE_MAC = ZStringUtils.nullToEmpty(ZSimCardInfo.getDeviceMac());
		VALUE_IMSI = ZStringUtils.nullToEmpty(ZSimCardInfo.getIMSI());
		VALUE_DEVICE_TYPE = "android:" + ZSimCardInfo.getSystemReleaseVersion();
		
		GsonRequest2.autoLoginRequest request = new GsonRequest2.autoLoginRequest();
		request.devicetoken = devicetoken;
		request.mac = ZSimCardInfo.getDeviceMac();
		request.imei = ZSimCardInfo.getIMEI();
		request.userid = ActiveAccount.getInstance(ZApplication.getInstance())
				.getLookLookID();

		Worker worker = new Worker(handler, RESPONSE_TYPE_AUTOLOGIN,
				GsonResponse2.autoLoginResponse.class);
		worker.execute(RIA_INTERFACE_AUTOLOGIN, request);
	}

	/**
	 * 29. 举报;
	 * 
	 * @param handler
	 * @param diaryid
	 *            : 日记ID;
	 * @param content
	 *            : 举报内容 注：可以为空
	 */
	public static void report(Handler handler, String diaryid, String content) {
		GsonRequest2.reportRequest request = new GsonRequest2.reportRequest();
		request.diaryid = diaryid;
		request.mac = ZSimCardInfo.getDeviceMac();
		request.imei = ZSimCardInfo.getIMEI();
		request.content = content;
		request.userid = ActiveAccount.getInstance(ZApplication.getInstance())
				.getLookLookID();

		Worker worker = new Worker(handler, RESPONSE_TYPE_REPORT,
				GsonResponse2.reportResponse.class);
		worker.execute(RIA_INTERFACE_REPORT, request);
	}

	/**
	 * 30. 给后台发第三方社区转发数、评论数、回复数;
	 * 
	 * @param handler
	 * @param diaryid
	 *            : 日记ID;
	 * @param sns
	 */
	public static void postWeiboCount(Handler handler, String diaryid,
			postWeibocountItem[] sns) {
		GsonRequest2.postWeiboCountRequest request = new GsonRequest2.postWeiboCountRequest();
		request.diaryid = diaryid;
		request.mac = ZSimCardInfo.getDeviceMac();
		request.imei = ZSimCardInfo.getIMEI();
		request.sns = sns;
		request.userid = ActiveAccount.getInstance(ZApplication.getInstance())
				.getLookLookID();

		Worker worker = new Worker(handler, RESPONSE_TYPE_POST_WEIBOCOUNT,
				GsonResponse2.postWeiboCountResponse.class);
		worker.execute(RIA_INTERFACE_POST_WEIBOCOUNT, request);
	}

	/**
	 * 31. 忘记密码;
	 * 
	 * @param handler
	 * @param username : 邮箱地址或手机号;
	 */
	public static void requestForgetPassword(Handler handler, String username, String registertype, String pwd_type) {
		GsonRequest2.forgetPasswordRequest request = new GsonRequest2.forgetPasswordRequest();
		request.username = username;

		request.registertype = registertype;
		request.pwd_type = pwd_type;
		request.equipmentid = CommonInfo.getInstance().equipmentid;
		request.mac = VALUE_MAC;
		request.imei = VALUE_IMEI;
		request.userid = ActiveAccount.getInstance(ZApplication.getInstance())
				.getLookLookID();

		Worker worker = new Worker(handler, RESPONSE_TYPE_FORGET_PASSWORD,
				GsonResponse2.forgetPasswordResponse.class);
		worker.execute(RIA_INTERFACE_FORGET_PASSWORD, request);
	}

	/**
	 * 32. 日记评论列表;
	 * 
	 * @param handler
	 * @param diaryid
	 *            ;
	 */
	public static void diaryCommentList(Handler handler, String comment_time, String request_type,
			String diaryid,String viewUserID){
		GsonRequest2.diaryCommentListRequest request = new GsonRequest2.diaryCommentListRequest();
		request.pagesize = "10";
		request.diaryid = diaryid;
		request.comment_time = comment_time;
		request.request_type = request_type;

		request.mac = VALUE_MAC;
		request.imei = VALUE_IMEI;
		request.userid = ActiveAccount.getInstance(ZApplication.getInstance()).getLookLookID();
		request.viewuserid=viewUserID;
		
		Worker worker = new Worker(handler, RESPONSE_TYPE_DIARY_COMMENTLIST, GsonResponse2.diaryCommentListResponse.class);
		worker.execute(RIA_INTERFACE_DIARY_COMMENTLIST, request);
	}

	/**
	 * 33. 发表/回复评论;
	 * 
	 * @param handler
	 * @param commentContent
	 *            : 评论内容;
	 * @param commentId
	 *            : 评论id;
	 * @param isReply
	 *            : 0: 评论. 1: 回复评论;
	 * @param diaryid
	 *            : 日记id;
	 */
	public static void comment(Handler handler, String commentContent,
			String commentId, String isReply, String diaryid, String commentType, String commentuuid) {
		GsonRequest2.commentRequest request = new GsonRequest2.commentRequest();
		request.commentcontent = commentContent;
		request.commentid = commentId;
		request.isreply = isReply;
		request.userid = ActiveAccount.getInstance(ZApplication.getInstance())
				.getLookLookID();
		request.diaryid = diaryid;
		request.commenttype = commentType;
		request.commentuuid = commentuuid;

		request.mac = VALUE_MAC;
		request.imei = VALUE_IMEI;

		Worker worker = new Worker(handler, RESPONSE_TYPE_COMMENT,
				GsonResponse2.commentResponse.class);
		worker.execute(RIA_INTERFACE_COMMENT, request);
	}

	/**
	 * 34. 删除日记;
	 * 
	 * @param handler
	 * @param diaryids
	 *            ;
	 */
	public static void deleteDiary(Handler handler, String diaryids) {
		GsonRequest2.deleteDiaryRequest request = new GsonRequest2.deleteDiaryRequest();
		request.diaryids = diaryids;
		request.changetomaindiaryuuids="-1";//已此通知服务器查询副本顶主本
		request.mac = VALUE_MAC;
		request.imei = VALUE_IMEI;
		request.userid = ActiveAccount.getInstance(ZApplication.getInstance())
				.getLookLookID();

		Worker worker = new Worker(handler, RESPONSE_TYPE_DELETE_DIARY,
				GsonResponse2.deleteDiaryResponse.class);
		worker.execute(RIA_INTERFACE_DELETE_DIARY, request);
	}

	/**
	 * 35. 获取日记分享URL;
	 * 
	 * @param handler
	 * @param diaryid
	 *            ;
	 */
	public static void getDiaryUrl(Handler handler, String diaryid) {
		GsonRequest2.getDiaryUrlRequest request = new GsonRequest2.getDiaryUrlRequest();
		request.diaryid = diaryid;

		request.mac = VALUE_MAC;
		request.imei = VALUE_IMEI;

		Worker worker = new Worker(handler, RESPONSE_TYPE_GET_DIARY_URL,
				GsonResponse2.getDiaryUrlResponse.class);
		worker.execute(RIA_INTERFACE_GET_DIARY_URL, request);
	}

	/**
	 * 36. 日记详情;
	 * 
	 * @param handler
	 * @param diaryid
	 *            : 日记id;
	 * @param diarywidth
	 *            :
	 * @param diaryheight
	 *            :
	 */
	public static void getDiaryinfo(Handler handler, String diaryid,
			String diarywidth, String diaryheight) {
		GsonRequest2.diaryInfoRequest request = new GsonRequest2.diaryInfoRequest();
		request.diaryid = diaryid;
		request.diarywidth = diarywidth;
		request.userid = ActiveAccount.getInstance(ZApplication.getInstance())
				.getLookLookID();
		request.diaryheight = diaryheight;

		request.mac = VALUE_MAC;
		request.imei = VALUE_IMEI;

		Worker worker = new Worker(handler, RESPONSE_TYPE_DIARY_INFO,
				GsonResponse2.diaryInfoResponse.class);
		worker.execute(RIA_INTERFACE_DIARY_INFO, request);
	}

	/**
	 * 37. 验证注册用户名是否可用;
	 * 
	 * @param handler
	 * @param username
	 *            ;
	 */
	public static void checkUserNameExist(Handler handler, String username) {
		GsonRequest2.checkUserNameExistRequest request = new GsonRequest2.checkUserNameExistRequest();
		request.username = username;
		request.equipmentid = CommonInfo.getInstance().equipmentid;
		request.mac = VALUE_MAC;
		request.imei = VALUE_IMEI;

		Worker worker = new Worker(handler, RESPONSE_TYPE_CHECK_USERNAME,
				GsonResponse2.checkUserNameExistResponse.class);
		worker.execute(RIA_INTERFACE_CHECK_USERNAME, request);
	}

	/**
	 * 38. 验证注册昵称是否可用------（第三方用户昵称规则？）;
	 * 
	 * @param handler
	 * @param nickname
	 *            ;
	 */
	public static void checkNickNameExist(Handler handler, String nickname) {
		GsonRequest2.checkNickNameExistRequest request = new GsonRequest2.checkNickNameExistRequest();
		request.nickname = nickname;
		request.mac = VALUE_MAC;
		request.imei = VALUE_IMEI;

		Worker worker = new Worker(handler, RESPONSE_TYPE_CHECK_NICKNAME,
				GsonResponse2.checkNickNameExistResponse.class);
		worker.execute(RIA_INTERFACE_CHECK_NICKNAME, request);
	}

	/**
	 * 39. 搜索用户列表（除去系统黑名单）;
	 * 
	 * @param handler
	 */
	public static void searchUser(Handler handler, String keyword,
			String pageno, String pagesize) {
		GsonRequest2.searchUserRequest request = new GsonRequest2.searchUserRequest();
		request.keyword = keyword;
		request.pageno = pageno;
		request.pagesize = pagesize;
		request.userid = ActiveAccount.getInstance(ZApplication.getInstance())
				.getLookLookID();
		request.mac = VALUE_MAC;
		request.imei = VALUE_IMEI;

		Worker worker = new Worker(handler, RESPONSE_TYPE_SEARCH_USER,
				GsonResponse2.searchUserResponse.class);
		worker.execute(RIA_INTERFACE_SEARCH_USER, request);
	}

	/**
	 * 40. 搜索附近日记（除自己日记外、系统黑名单）;
	 * 
	 * @param handler
	 */
	public static void requestNearDiary(Handler handler, LocationData myLoc,
			String pageno, String pagesize, String diarywidth, String diaryheight) {
		GsonRequest2.nearDiaryRequest request = new GsonRequest2.nearDiaryRequest();
		if (myLoc != null) {
			request.latitude = String.valueOf(myLoc.latitude);// ;"39.904963";
			request.longitude = String.valueOf(myLoc.longitude);// "116.410029";
		}
		request.pageno = pageno;
		request.pagesize = pagesize;
		request.userid = ActiveAccount.getInstance(ZApplication.getInstance())
				.getLookLookID();
		request.diarywidth = diarywidth;
		request.diaryheight = diaryheight;

		request.mac = VALUE_MAC;
		request.imei = VALUE_IMEI;

		Worker worker = new Worker(handler, RESPONSE_TYPE_NEAR_DIARY,
				GsonResponse2.nearDiaryResponse.class);
		worker.execute(RIA_INTERFACE_NEAR_DIARY, request);
	}

	/**
	 * 41. 获取标签（缓存到本地，每次刷新到数据替换本地）;
	 * 
	 * @param handler
	 * @param diaryid
	 *            : 日记id;
	 */
	public static void requestTagList(Handler handler, String diaryid) {
		GsonRequest2.taglistRequest request = new GsonRequest2.taglistRequest();
		request.userid = ActiveAccount.getInstance(ZApplication.getInstance())
				.getLookLookID();
		request.diaryid = diaryid;

		request.mac = VALUE_MAC;
		request.imei = VALUE_IMEI;

		Worker worker = new Worker(handler, RESPONSE_TYPE_TAGLIST,
				GsonResponse2.taglistResponse.class);
		worker.execute(RIA_INTERFACE_TAGLIST, request);
	}

	/**
	 * 42. 关注;
	 * 
	 * @param handler
	 * @param diaryid
	 *            : 日记id;
	 */
	public static void attention(Handler handler, String attention_userid) {
		GsonRequest2.attentionRequest request = new GsonRequest2.attentionRequest();
		request.userid = ActiveAccount.getInstance(ZApplication.getInstance())
				.getLookLookID();
		request.attention_userid = attention_userid;

		request.mac = VALUE_MAC;
		request.imei = VALUE_IMEI;

		Worker worker = new Worker(handler, RESPONSE_TYPE_ATTENTION,
				GsonResponse2.attentionResponse.class);
		worker.execute(RIA_INTERFACE_ATTENTION, request);
	}

	/**
	 * 43. 取消关注\删除粉丝;
	 * 
	 * @param handler
	 */
	public static void cancelAttention(Handler handler, String target_userid,
			String attention_type) {
		GsonRequest2.cancelattentionRequest request = new GsonRequest2.cancelattentionRequest();
		request.target_userid = target_userid;
		request.attention_type = attention_type;
		request.mac = VALUE_MAC;
		request.imei = VALUE_IMEI;
		request.userid = ActiveAccount.getInstance(ZApplication.getInstance())
				.getLookLookID();

		Worker worker = new Worker(handler, RESPONSE_TYPE_CANCEL_ATTENTION,
				GsonResponse2.cancelattentionResponse.class);
		worker.execute(RIA_INTERFACE_CANCEL_ATTENTION, request);
	}

	/**
	 * 44. 关注人备注;
	 * 
	 * @param handler
	 */
	public static void markAttention(Handler handler, String attention_mark,
			String attention_userid) {
		GsonRequest2.markattentionRequest request = new GsonRequest2.markattentionRequest();
		request.attention_mark = attention_mark;
		request.attention_userid = attention_userid;
		request.mac = VALUE_MAC;
		request.imei = VALUE_IMEI;
		request.userid = ActiveAccount.getInstance(ZApplication.getInstance())
				.getLookLookID();

		Worker worker = new Worker(handler, RESPONSE_TYPE_BAKEUP_ATTENTION,
				GsonResponse2.markattentionResponse.class);
		worker.execute(RIA_INTERFACE_BAKEUP_ATTENTION, request);
	}

	/**
	 * 45. 设置黑名单（用户黑名单）;
	 * 
	 * @param handler
	 */
	public static void operateBlacklist(Handler handler, String target_userid,
			String operatetype) {
		GsonRequest2.operateblacklistRequest request = new GsonRequest2.operateblacklistRequest();
		request.target_userid = target_userid;
		request.operatetype = operatetype;
		request.mac = VALUE_MAC;
		request.imei = VALUE_IMEI;
		request.userid = ActiveAccount.getInstance(ZApplication.getInstance())
				.getLookLookID();

		Worker worker = new Worker(handler, RESPONSE_TYPE_SET_BLACKLIST,
				GsonResponse2.operateblacklistResponse.class);
		worker.execute(RIA_INTERFACE_SET_BLACKLIST, request);
	}

	/**
	 * 46. 获取关注列表;
	 * 
	 * @param handler
	 */
	public static void requestAttentionList(Handler handler, String user_time,
			String viewuserid, String pageSize) {
		GsonRequest2.myattentionlistRequest request = new GsonRequest2.myattentionlistRequest();
		request.user_time = user_time;
		request.viewuserid = viewuserid;
		request.pagesize = pageSize; // 10;

		request.mac = VALUE_MAC;
		request.imei = VALUE_IMEI;

		request.userid = ActiveAccount.getInstance(ZApplication.getInstance())
				.getLookLookID();

		Worker worker = new Worker(handler, RESPONSE_TYPE_MY_ATTENTIONLIST,
				GsonResponse2.myattentionlistResponse.class);
		worker.execute(RIA_INTERFACE_MY_ATTENTIONLIST, request);
	}

	/**
	 * 47. 获取我的粉丝列表;
	 * 
	 * @param handler
	 */
	public static void requestMyFansList(Handler handler, String user_time, String uerid) {
		GsonRequest2.myfanslistRequest request = new GsonRequest2.myfanslistRequest();
		request.user_time = user_time;
		request.pagesize = "10";
		request.viewuserid = uerid;
		request.mac = VALUE_MAC;
		request.imei = VALUE_IMEI;
		request.userid = ActiveAccount.getInstance(ZApplication.getInstance())
				.getLookLookID();

		Worker worker = new Worker(handler, RESPONSE_TYPE_MY_FANS_LIST,
				GsonResponse2.myfanslistResponse.class);
		worker.execute(RIA_INTERFACE_MY_FANS_LIST, request);
	}

	/**
	 * 48. 用户黑名单列表;
	 * 
	 * @param handler
	 */
	public static void myBlacklist(Handler handler, String user_time,
			String viewuserid,String pageSize) {
		GsonRequest2.myblacklistRequest request = new GsonRequest2.myblacklistRequest();
		request.user_time = user_time;
		request.pagesize = pageSize;
		request.viewuserid = viewuserid;
		request.mac = VALUE_MAC;
		request.imei = VALUE_IMEI;
		request.userid = ActiveAccount.getInstance(ZApplication.getInstance())
				.getLookLookID();

		Worker worker = new Worker(handler, RESPONSE_TYPE_MY_BLACK_LIST,
				GsonResponse2.myblacklistResponse.class);
		worker.execute(RIA_INTERFACE_MY_BLACK_LIST, request);
	}

	/**
	 * 49. 朋友圈;
	 * 
	 * @param handler
	 * @param pageno
	 *            : 页号
	 * @param pagesize
	 *            : 每页记录数
	 * @param myLoc
	 *            : 分享经纬度，可能为空
	 * @param diarywidth
	 *            : 封面需要显示的宽度，可以为空
	 * @param diaryheight
	 *            : 封面需要显示的高度，可以为空
	 * @param near_pageno
	 *            : 附近页码，空默认1
	 * @param near_pagesize
	 *            : 附近每页记录数，空默认3
	 * @param recomment_pageno
	 *            : 推荐页码，空默认1
	 * @param recomment_pagesize
	 *            : 推荐每页记录数，空默认3
	 * @param atctive_pageno
	 *            : 活动页码，空默认1
	 * @param atctive_pagesize
	 *            : 活动每页记录数，空默认3 ---包括：我自己发布的日记，我关注人的日记、我关注人的转发日记
	 *            ---不包括：我做的第三方分享日记，即第三方分享不插入微博表数据
	 */
	public static void requestMyTimeLine(Handler handler, String pageno,
			String pagesize, String diarywidth, String diaryheight) {
		GsonRequest2.timelineRequest request = new GsonRequest2.timelineRequest();
		request.pageno = pageno;
		request.pagesize = "10";
		request.diarywidth = diarywidth;
		request.diaryheight = diaryheight;

		request.mac = VALUE_MAC;
		request.imei = VALUE_IMEI;
		CommonInfo ci = CommonInfo.getInstance();
		if (ci.myLoc != null) {
			request.longitude = String.valueOf(ci.myLoc.longitude);
			request.latitude = String.valueOf(ci.myLoc.latitude);
		}
		
		request.userid = ActiveAccount.getInstance(ZApplication.getInstance())
				.getLookLookID();

		Worker worker = new Worker(handler, RESPONSE_TYPE_TIMELINE,
				GsonResponse2.timelineResponse.class);
		worker.execute(RIA_INTERFACE_TIMELINE, request);
	}

	/**
	 * 50. 发私信;
	 * 
	 * @param handler
	 * @param content : 私信内容;
	 * @param targetUserIds : 目标用户id;
	 */
	public static void sendMessage(Handler handler, String diaryid,
			String content, String targetUserIds, String privatemsgtype, String uuid) {
		GsonRequest2.sendmessageRequest request = new GsonRequest2.sendmessageRequest();
		request.content = content;
		request.target_userids = targetUserIds;
		request.diaryid = diaryid;
		request.privatemsgtype = privatemsgtype;
		request.mac = VALUE_MAC;
		request.imei = VALUE_IMEI;
		request.userid = ActiveAccount.getInstance(ZApplication.getInstance())
				.getLookLookID();
		request.uuid = uuid;

		Worker worker = new Worker(handler, RESPONSE_TYPE_SEND_MESSAGE,
				GsonResponse2.sendmessageResponse.class);
		worker.execute(RIA_INTERFACE_SEND_MESSAGE, request);
	}

	/**
	 * 51. 日记推荐 (有标签则按标签推荐, 没有则按时间推荐);
	 * 
	 * @param handler
	 */
	public static void requestDiaryRecommend(Handler handler,
			String pageno, String pagesize, String diarywidth, String diaryheight) {
		GsonRequest2.diaryrecommendRequest request = new GsonRequest2.diaryrecommendRequest();
		request.pageno = pageno;
//		request.request_type = "1";
		request.pagesize = pagesize;
		request.mac = VALUE_MAC;
		request.imei = VALUE_IMEI;
		request.userid = ActiveAccount.getInstance(ZApplication.getInstance())
				.getLookLookID();
		request.diaryheight = diaryheight;
		request.diarywidth = diarywidth;

		Worker worker = new Worker(handler, RESPONSE_TYPE_DIARY_RECOMMEND,
				GsonResponse2.diaryrecommendResponse.class);
		worker.execute(RIA_INTERFACE_DIARY_RECOMMEND, request);
	}

	/**
	 * 52. 喜欢(赞)并转发;
	 * 
	 * @param handler
	 * @param diaryid
	 */
	public static void enjoyandforward(Handler handler, String diaryid,
			String publishid) {
		GsonRequest2.enjoyRequest request = new GsonRequest2.enjoyRequest();
		request.diaryid = diaryid;
		request.publishid = publishid;

		request.mac = VALUE_MAC;
		request.imei = VALUE_IMEI;

		request.userid = ActiveAccount.getInstance(ZApplication.getInstance())
				.getLookLookID();
		Worker worker = new Worker(handler, RESPONSE_TYPE_DIARY_ENJOY,
				GsonResponse2.enjoyResponse.class);
		worker.execute(RIA_INTERFACE_DIARY_ENJOY, request);
	}

	// 53
	// 54
	/**
	 * 55. 解除绑定;
	 * 
	 * @param handler
	 */
	public static void unbind(Handler handler, String binding_type,
			String phone_type, String binding_info, String snstype,
			String snsuid) {
		GsonRequest2.unbindRequest request = new GsonRequest2.unbindRequest();
		request.binding_type = binding_type;
		request.phone_type = phone_type;
		request.binding_info = binding_info;
		request.snstype = snstype;
		request.snsuid = snsuid; //.toLowerCase();

		request.equipmentid = CommonInfo.getInstance().equipmentid;
		request.mac = VALUE_MAC;
		request.imei = VALUE_IMEI;

		request.userid = ActiveAccount.getInstance(ZApplication.getInstance())
				.getLookLookID();
		Worker worker = new Worker(handler, RESPONSE_TYPE_UNBIND,
				GsonResponse2.unbindResponse.class);
		worker.execute(RIA_INTERFACE_UNBIND, request);
	}

	/**
	 * 56. 删除评论（删除自己评论和删除对自己日记进行的评论）;
	 * 
	 * @param handler
	 */
	public static void deleteComment(Handler handler, String diaryid,
			String commentid, String commentuuid) {
		GsonRequest2.deleteCommentRequest request = new GsonRequest2.deleteCommentRequest();
		request.diaryid = diaryid;
		request.commentid = commentid;
		request.commentuuid = commentuuid;

		request.mac = VALUE_MAC;
		request.imei = VALUE_IMEI;

		request.userid = ActiveAccount.getInstance(ZApplication.getInstance())
				.getLookLookID();
		Worker worker = new Worker(handler, RESPONSE_TYPE_DELETE_COMMENT,
				GsonResponse2.deleteCommentResponse.class);
		worker.execute(RIA_INTERFACE_DELETE_COMMENT, request);
	}

	/**
	 * 57. 设置视频封面（供图片服务器访问）;
	 * 
	 * @param handler
	 */
	public static void setVideoCover(Handler handler, String attachid,
			String imagepath) {
		GsonRequest2.setVideoCoverRequest request = new GsonRequest2.setVideoCoverRequest();
		request.attachid = attachid;
		request.imagepath = imagepath;
		request.width = "320";
		request.height = "640";

		request.mac = VALUE_MAC;
		request.imei = VALUE_IMEI;

		Worker worker = new Worker(handler, RESPONSE_TYPE_SET_VIDEO_COVER,
				GsonResponse2.setVideoCoverResponse.class);
		worker.execute(RIA_INTERFACE_SET_VIDEO_COVER, request);
	}

	/**
	 * 58. 设置用户头像（供图片服务器访问）;
	 * 
	 * @param handler
	 * @param imageURL
	 */
	public static void requestsetHeadImage(Handler handler, String imageURL) {
		GsonRequest2.setHeadImageRequest request = new GsonRequest2.setHeadImageRequest();
		request.imagepath = imageURL;

		request.mac = VALUE_MAC;
		request.imei = VALUE_IMEI;

		request.userid = ActiveAccount.getInstance(ZApplication.getInstance())
				.getLookLookID();

		Worker worker = new Worker(handler, RESPONSE_TYPE_SET_HEADIMAGE,
				GsonResponse2.setHeadImageResponse.class);
		worker.execute(RIA_INTERFACE_SET_HEADIMAGE, request);
	}

	/**
	 * 59. 获取活动列表（缓存）;
	 * 
	 * @param handler
	 * @param activeType
	 *            : 0: 所有活动. 1: 有效活动;
	 * @param videoId
	 *            : 视频id;
	 */
	public static void requestActiveList(Handler handler, String activeType,
			String diaryid) {
		GsonRequest2.activeListRequest request = new GsonRequest2.activeListRequest();
		request.activetype = activeType;
		request.userid = ActiveAccount.getInstance(ZApplication.getInstance())
				.getLookLookID();
		request.diaryid = diaryid;
		request.mac = VALUE_MAC;
		request.imei = VALUE_IMEI;

		Worker worker = new Worker(handler, RESPONSE_TYPE_GET_ACTIVELIST,
				GsonResponse2.activeListResponse.class);
		worker.execute(RIA_INTERFACE_GET_ACTIVELIST, request);
	}

	/**
	 * 60. 获取参与活动的日记列表;
	 * 
	 * @param handler
	 * @param activeId
	 *            : 活动id;
	 */
	public static void requestActiveDiaryList(Handler handler, String activeId,
			String diary_time, String pageno, String width, String height,
			String request_type) {
		GsonRequest2.activeDiaryListRequest request = new GsonRequest2.activeDiaryListRequest();
		request.active_id = activeId;
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
				GsonResponse2.activeDiaryListResponse.class);
		worker.execute(RIA_INTERFACE_DIARY_ACTIVELIST, request);
	}

	// 61
	/**
	 * 62. 设置活动图片（供图片服务器访问）;
	 * 
	 * @param handler
	 * @param imageURL
	 */
	public static void setActiveImage(Handler handler, String imagepath,
			String activeid) {
		GsonRequest2.setActiveImageRequest request = new GsonRequest2.setActiveImageRequest();
		request.activeid = activeid;
		request.imagepath = imagepath;

		request.mac = VALUE_MAC;
		request.imei = VALUE_IMEI;

		Worker worker = new Worker(handler, RESPONSE_TYPE_SET_ACTIVEIMAGE,
				GsonResponse2.setActiveImageResponse.class);
		worker.execute(RIA_INTERFACE_SET_ACTIVEIMAGE, request);
	}

	/**
	 * 63. 删除消息;
	 * 
	 * @param handler
	 * @param imageURL
	 */
	public static void deleteMessage(Handler handler, String messageid,
			String msg_type) {
		GsonRequest2.deleteMessageRequest request = new GsonRequest2.deleteMessageRequest();
		request.messageid = messageid;
		request.msg_type = msg_type;

		request.mac = VALUE_MAC;
		request.imei = VALUE_IMEI;
		request.userid = ActiveAccount.getInstance(ZApplication.getInstance())
				.getLookLookID();

		Worker worker = new Worker(handler, RESPONSE_TYPE_DELETE_MESSAGE,
				GsonResponse2.deleteMessageResponse.class);
		worker.execute(RIA_INTERFACE_DELETE_MESSAGE, request);
	}

	/**
	 * 64. 第三方互为关注的用户（包括后台推荐的20个用户以在LOOKLOOK注册的用户）;
	 * 
	 * @param handler
	 * @param imageURL
	 */
	public static void listRecommendUser(Handler handler, String index, String timestamp) {
		GsonRequest2.listThirdPlatformUserRequest request = new GsonRequest2.listThirdPlatformUserRequest();
		request.index = index;
		request.pagesize = "10";
		request.timestamp = timestamp;
		
		request.mac = VALUE_MAC;
		request.imei = VALUE_IMEI;
		request.userid = ActiveAccount.getInstance(ZApplication.getInstance())
				.getLookLookID();

		Worker worker = new Worker(handler, RESPONSE_TYPE_LIST_THIRDPLATFORM,
				GsonResponse2.listThirdPlatformUserResponse.class);
		worker.execute(RIA_INTERFACE_LIST_THIRDPLATFORM, request);
	}

	/**
	 * 65. 获取消息数;
	 * 
	 * @param handler
	 */
	public static void requestMessageCount(Handler handler) {
		GsonRequest2.getMessageCountRequest request = new GsonRequest2.getMessageCountRequest();
		request.userid = ActiveAccount.getInstance(ZApplication.getInstance())
				.getLookLookID();

		Worker worker = new Worker(handler, RESPONSE_TYPE_GET_MESSAGECOUNT,
				GsonResponse2.getMessageCountResponse.class);
		worker.execute(RIA_INTERFACE_GET_MESSAGECOUNT, request);
	}

	/**
	 * 66. 获取日记赞人列表（3.0暂时不做，调转发人列表）;
	 * 
	 * @param handler
	 */
	public static void getDiaryEnjoyUsers(Handler handler, String diaryid,
			String index) {
		GsonRequest2.getDiaryEnjoyUsersRequest request = new GsonRequest2.getDiaryEnjoyUsersRequest();
		request.diaryid = diaryid;
		request.index = index;
		request.pagesize = "10";
		request.mac = VALUE_MAC;
		request.imei = VALUE_IMEI;

		request.userid = ActiveAccount.getInstance(ZApplication.getInstance())
				.getLookLookID();

		Worker worker = new Worker(handler, RESPONSE_TYPE_GET_DIARY_ENJOY,
				GsonResponse2.getDiaryEnjoyUsersResponse.class);
		worker.execute(RIA_INTERFACE_GET_DIARY_ENJOY, request);
	}

	/**
	 * 67. 获取日记转发人列表;
	 * 
	 * @param handler
	 */
	public static void getDiaryForwardUsers(Handler handler, String diaryid,
			String user_time, String request_type) {
		GsonRequest2.getDiaryForwardUsersRequest request = new GsonRequest2.getDiaryForwardUsersRequest();
		request.diaryid = diaryid;
		request.user_time = user_time;
		request.request_type = request_type;
		request.pagesize = "10";
		request.mac = VALUE_MAC;
		request.imei = VALUE_IMEI;

		request.userid = ActiveAccount.getInstance(ZApplication.getInstance())
				.getLookLookID();

		Worker worker = new Worker(handler, RESPONSE_TYPE_GET_DIARY_FORWORD,
				GsonResponse2.getDiaryForwardUsersResponse.class);
		worker.execute(RIA_INTERFACE_GET_DIARY_FORWORD, request);
	}

	/**
	 * 68. 设置空间背景图 (从背景图列表中选择的);
	 * 
	 * @param handler
	 * @param imageUrl
	 *            : 从列表中选择的图片的url;
	 */
	public static void setUserSpaceCover(Handler handler, String imageUrl) {
		GsonRequest2.setUserSpaceCoverRequest request = new GsonRequest2.setUserSpaceCoverRequest();
		request.imagepath = imageUrl;
		request.userid = ActiveAccount.getInstance(ZApplication.getInstance())
				.getLookLookID();
		request.mac = VALUE_MAC;
		request.imei = VALUE_IMEI;

		Worker worker = new Worker(handler, RESPONSE_TYPE_SET_SPACECOVER,
				GsonResponse2.setUserSpaceCoverResponse.class);
		worker.execute(RIA_INTERFACE_SET_SPACECOVER, request);
	}

	/**
	 * 69. 获取空间背景图片列表;
	 * 
	 * @param handler
	 */
	public static void getSpaceCoverList(Handler handler, String index) {
		GsonRequest2.getSpaceCoverListRequest request = new GsonRequest2.getSpaceCoverListRequest();
		request.index = index;
		request.pagesize = "10000";
		request.userid = ActiveAccount.getInstance(ZApplication.getInstance())
				.getLookLookID();
		request.mac = VALUE_MAC;
		request.imei = VALUE_IMEI;

		Worker worker = new Worker(handler, RESPONSE_TYPE_GET_SPACECOVER_LIST,
				GsonResponse2.getSpaceCoverListResponse.class);
		worker.execute(RIA_INTERFACE_GET_SPACECOVER_LIST, request);
	}

	// 70
	/**
	 * 71. 转发日记列表(3.0获取赞日记列表);
	 * 
	 * @param handler
	 */
	public static void forwardDiaryList(Handler handler, String request_type,
			String diary_time, String viewuserid,String diarywidth,String diaryheight) {
		GsonRequest2.forwardDiarylistRequest request = new GsonRequest2.forwardDiarylistRequest();
		request.pagesize = "10";
		request.request_type = request_type;
		request.diary_time = diary_time;
		request.viewuserid = viewuserid;
		request.diarywidth = diarywidth;
		request.diaryheight = diaryheight;

		request.userid = ActiveAccount.getInstance(ZApplication.getInstance())
				.getLookLookID();
		request.mac = VALUE_MAC;
		request.imei = VALUE_IMEI;

		Worker worker = new Worker(handler, RESPONSE_TYPE_FORWARD_DIARY_LIST,
				GsonResponse2.forwardDiaryListResponse.class);
		worker.execute(RIA_INTERFACE_FORWARD_DIARY_LIST, request);
	}

	/**
	 * 72. 转发日记id列表(3.0获取赞日记id列表);
	 * 
	 * @param handler
	 */
	public static void forwardDiaryIDList(Handler handler, String diary_time,
			String viewuserid) {
		GsonRequest2.forwardDiaryIDRequest request = new GsonRequest2.forwardDiaryIDRequest();
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
				GsonResponse2.forwardDiaryIDResponse.class);
		worker.execute(RIA_INTERFACE_FORWARD_DIARY_ID, request);
	}

	/**
	 * 73. 修改标签或位置(用户虚假位置信息);
	 * 
	 * @param handler
	 */
	public static void modTagsOrPosition(Handler handler, String diaryid,
			String tags, String position) {
		GsonRequest2.modTagsOrPositionRequest request = new GsonRequest2.modTagsOrPositionRequest();
		request.diaryid = diaryid;
		request.tags = tags;
		request.position = position;

		request.userid = ActiveAccount.getInstance(ZApplication.getInstance())
				.getLookLookID();
		request.mac = VALUE_MAC;
		request.imei = VALUE_IMEI;

		Worker worker = new Worker(handler, RESPONSE_TYPE_MODIFY_TAG,
				GsonResponse2.modTagsOrPositionResponse.class);
		worker.execute(RIA_INTERFACE_MODIFY_TAG, request);
	}

	/**
	 * 74. 分享日记列表（显示自己的日记，分享到第三方的日记）-------增量更新;
	 * 
	 * @param handler
	 */
	public static void listEnjoyDiary(Handler handler, String viewuserid,
			String diary_time, String request_type,String diarywidth,String diaryheight) {
		GsonRequest2.shareEnjoyDiaryRequest request = new GsonRequest2.shareEnjoyDiaryRequest();
		request.pagesize = "10";
		request.viewuserid = viewuserid;
		request.diary_time = diary_time;
		request.request_type = request_type;
		request.diarywidth=diarywidth;
		request.diaryheight=diaryheight;

		request.userid = ActiveAccount.getInstance(ZApplication.getInstance())
				.getLookLookID();
		request.mac = VALUE_MAC;
		request.imei = VALUE_IMEI;

		Worker worker = new Worker(handler, RESPONSE_TYPE_LIST_SHARE_DIARY,
				GsonResponse2.shareEnjoyDiaryResponse.class);
		worker.execute(RIA_INTERFACE_LIST_SHARE_DIARY, request);
	}

	/**
	 * 75. 个人主页;
	 * 
	 * @param handler
	 */
	public static void homePage(Handler handler, String viewuserid,
			String diary_time, String request_type,String diarywidth,String diaryheight,String userbackgroundwidth,String userbackgroundheight) {
		GsonRequest2.homeRequest request = new GsonRequest2.homeRequest();
		request.pagesize = "20";
		request.viewuserid = viewuserid;
		request.diary_time = diary_time;
		request.request_type = request_type;
		request.diarywidth=diarywidth;
		request.diaryheight=diaryheight;
		request.userbackgroundwidth=userbackgroundwidth;
		request.userbackgroundheight=userbackgroundheight;
		request.userid = ActiveAccount.getInstance(ZApplication.getInstance())
				.getLookLookID();
		request.mac = VALUE_MAC;
		request.imei = VALUE_IMEI;

		Worker worker = new Worker(handler, RESPONSE_TYPE_HOME,
				GsonResponse2.homeResponse.class);
		worker.execute(RIA_INTERFACE_HOME, request);
	}

	/**
	 * 76. 删除转发和赞日记;
	 * 
	 * @param handler
	 */
	public static void deletepublishAndEnjoy(Handler handler, String publishid,
			String diaryid) {
		GsonRequest2.deletepublishAndEnjoyRequest request = new GsonRequest2.deletepublishAndEnjoyRequest();
		request.publishid = publishid;
		request.diaryid = diaryid;

		request.userid = ActiveAccount.getInstance(ZApplication.getInstance())
				.getLookLookID();
		request.mac = VALUE_MAC;
		request.imei = VALUE_IMEI;

		Worker worker = new Worker(handler, RESPONSE_TYPE_DELETE_AND_ENJOY,
				GsonResponse2.deletepublishAndEnjoyResponse.class);
		worker.execute(RIA_INTERFACE_DELETE_AND_ENJOY, request);
	}

	// 77
	// 78
	/**
	 * 79. 活动中奖日记列表;
	 * 
	 * @param handler
	 * @param activeId
	 *            : 活动id;
	 * @param videoWidth
	 *            : 视频封面宽;
	 * @param videoHeight
	 *            : 视频封面高;
	 */
	public static void requestAwardDiaryList(Handler handler, String activeId,
			String diarywidth, String diaryheight) {
		GsonRequest2.getAwardDiaryListRequest request = new GsonRequest2.getAwardDiaryListRequest();
		request.active_id = activeId;
		request.userid = ActiveAccount.getInstance(ZApplication.getInstance())
				.getLookLookID();
		request.diarywidth = diarywidth;
		request.diaryheight = diaryheight;
		request.mac = VALUE_MAC;
		request.imei = VALUE_IMEI;

		Worker worker = new Worker(handler, RESPONSE_TYPE_GET_AWARD_DIARYLIST,
				GsonResponse2.getAwardDiaryListResponse.class);
		worker.execute(RIA_INTERFACE_GET_AWARD_DIARYLIST, request);
	}

	// 80
	/**
	 * 81. 参加活动;
	 * 
	 * @param handler
	 */
	public static void joinActive(Handler handler, String diaryid,
			String activeid) {
		GsonRequest2.joinActiveRequest request = new GsonRequest2.joinActiveRequest();
		request.userid = ActiveAccount.getInstance(ZApplication.getInstance())
				.getLookLookID();
		request.diaryid = diaryid;
		request.activeid = activeid;
		request.mac = VALUE_MAC;
		request.imei = VALUE_IMEI;

		Worker worker = new Worker(handler, RESPONSE_TYPE_JOIN_ACTIVE,
				GsonResponse2.joinActiveResponse.class);
		worker.execute(RIA_INTERFACE_JOIN_ACTIVE, request);
	}

	/**
	 * 82. 通知私信浏览完成（只用于日记，视频等）;
	 * 
	 * @param handler
	 */
	public static void notifyPrivmsg(Handler handler, String diaryid) {
		GsonRequest2.notifyPrivmsgRequest request = new GsonRequest2.notifyPrivmsgRequest();
		request.userid = ActiveAccount.getInstance(ZApplication.getInstance())
				.getLookLookID();
		request.diaryid = diaryid;
		request.mac = VALUE_MAC;
		request.imei = VALUE_IMEI;

		Worker worker = new Worker(handler,
				RESPONSE_TYPE_NOTIFY_PRIVATE_MESSAGE,
				GsonResponse2.notifyPrivmsgResponse.class);
		worker.execute(RIA_INTERFACE_NOTIFY_PRIVATE_MESSAGE, request);
	}

	/**
	 * 83. 通知看过消息;
	 * 
	 * @param handler
	 */
	public static void notifyMessage(Handler handler, String messageids) {
		GsonRequest2.notifyMessageRequest request = new GsonRequest2.notifyMessageRequest();
		request.userid = ActiveAccount.getInstance(ZApplication.getInstance())
				.getLookLookID();
		request.messageids = messageids;
		request.mac = VALUE_MAC;
		request.imei = VALUE_IMEI;

		Worker worker = new Worker(handler, RESPONSE_TYPE_NOTIFY_MESSAGE,
				GsonResponse2.notifyMessageResponse.class);
		worker.execute(RIA_INTERFACE_NOTIFY_MESSAGE, request);
	}

	/**
	 * 84. 发送通讯录;
	 * 
	 * @param handler
	 */
	public static void postAddressBook(Handler handler, AddrBook[] address_book) {
		GsonRequest2.postAddressBookRequest request = new GsonRequest2.postAddressBookRequest();
		request.userid = ActiveAccount.getInstance(ZApplication.getInstance())
				.getLookLookID();
		request.address_book = address_book;
		request.mac = VALUE_MAC;
		request.imei = VALUE_IMEI;

		Worker worker = new Worker(handler, RESPONSE_TYPE_POST_ADDRESSBOOK,
				GsonResponse2.postAddressBookResponse.class);
		worker.execute(RIA_INTERFACE_POST_ADDRESSBOOK, request);
	}

	/**
	 * 85. 查看日记权限;
	 * 
	 * @param handler
	 */
	public static void diaryPermissions(Handler handler, String diaryid) {
		GsonRequest2.diaryPermissionsRequest request = new GsonRequest2.diaryPermissionsRequest();
		request.userid = ActiveAccount.getInstance(ZApplication.getInstance())
				.getLookLookID();
		request.diaryid = diaryid;
		request.mac = VALUE_MAC;
		request.imei = VALUE_IMEI;

		Worker worker = new Worker(handler, RESPONSE_TYPE_DIARY_PERMISSION,
				GsonResponse2.diaryPermissionsResponse.class);
		worker.execute(RIA_INTERFACE_DIARY_PERMISSION, request);
	}

	/**
	 * 86. 设置日记心情;
	 * 
	 * @param handler
	 */
	public static void setMood(Handler handler, String diaryid, String mood_id) {
		GsonRequest2.setMoodRequest request = new GsonRequest2.setMoodRequest();
		request.userid = ActiveAccount.getInstance(ZApplication.getInstance())
				.getLookLookID();
		request.diaryid = diaryid;
		request.mood_id = mood_id;
		request.mac = VALUE_MAC;
		request.imei = VALUE_IMEI;

		Worker worker = new Worker(handler, RESPONSE_TYPE_DIARY_SET_MOOD,
				GsonResponse2.setMoodResponse.class);
		worker.execute(RIA_INTERFACE_DIARY_SET_MOOD, request);
	}

	/**
	 * 87. 新消息列表;
	 * 
	 * @param handler
	 */

	public static void listMessage(Handler handler, String timemilli,
			String pagesize, String messagetype, String commentid, String diaryids,String diarywidth) {
		GsonRequest2.listMessageRequest request = new GsonRequest2.listMessageRequest();
		request.userid = ActiveAccount.getInstance(ZApplication.getInstance())
				.getLookLookID();
		//request.userid = "20fd4b060f6c304df20a97f0d161473d7bf1";
		// request.pageno = pageno;
		if(timemilli!=null && !timemilli.equals("") && !timemilli.equals("0") ){
			request.timemilli = timemilli;
		}

		request.diarywidth=diarywidth;
		request.pagesize = pagesize;
		request.messagetype = messagetype;
		request.commentid = commentid;
		request.diaryids = diaryids;
		request.mac = VALUE_MAC;
		request.imei = VALUE_IMEI;

		Worker worker = new Worker(handler, RESPONSE_TYPE_LIST_MESSAGE,
				GsonResponse2.listMessageResponse.class);
		worker.execute(RIA_INTERFACE_LIST_MESSAGE, request);
	}

	/**
	 * 88. 清除陌生人消息;
	 * 
	 * @param handler
	 */
	public static void clearStrangerMessage(Handler handler) {
		GsonRequest2.clearStrangerMessageRequest request = new GsonRequest2.clearStrangerMessageRequest();
		request.userid = ActiveAccount.getInstance(ZApplication.getInstance())
				.getLookLookID();
		request.mac = VALUE_MAC;
		request.imei = VALUE_IMEI;

		Worker worker = new Worker(handler,
				RESPONSE_TYPE_CLEAR_STRANGER_MESSAGE,
				GsonResponse2.clearStrangerMessageResponse.class);
		worker.execute(RIA_INTERFACE_CLEAR_STRANGER_MESSAGE, request);
	}

	/**
	 * 89. Crm回调，通知激活;
	 * 
	 * @param handler
	 */
	public static void crmCallback(Handler handler, String bindname) {
		GsonRequest2.crmCallbackRequest request = new GsonRequest2.crmCallbackRequest();
		request.userid = ActiveAccount.getInstance(ZApplication.getInstance())
				.getLookLookID();
		request.bindname = bindname;

		Worker worker = new Worker(handler, RESPONSE_TYPE_CRM_CALLBACK,
				GsonResponse2.crmCallbackResponse.class);
		worker.execute(RIA_INTERFACE_CRM_CALLBACK, request);
	}

	/**
	 * 90. www播放页（重新整理，改成JSON格式）;
	 * 
	 * @param handler
	 */
	public static void playPage(Handler handler, String shortUrl) {
		GsonRequest2.playPageRequest request = new GsonRequest2.playPageRequest();
		request.shortUrl = shortUrl;
		Worker worker = new Worker(handler, RESPONSE_TYPE_PLAY_PAGE,
				GsonResponse2.playPageResponse.class);
		worker.execute(RIA_INTERFACE_PLAY_PAGE, request);
	}

	/**
	 * 91.	获取官方用户列表;
	 * 
	 * @param handler
	 */
	public static void getOfficialUserids(Handler handler) {
		GsonRequest2.getOfficialUseridsRequest request = new GsonRequest2.getOfficialUseridsRequest();
		request.userid = ActiveAccount.getInstance(ZApplication.getInstance())
				.getLookLookID();
		request.mac = VALUE_MAC;
		request.imei = VALUE_IMEI;
		Worker worker = new Worker(handler, RESPONSE_TYPE_GET_OFFICIAL_USERIDS,
				GsonResponse2.getOfficialUseridsResponse.class);
		worker.execute(RIA_INTERFACE_GET_OFFICIAL_USERIDS, request);
	}
	
	/**
	 * 92.	获取MD5加密KEY;
	 * 
	 * @param handler
	 */
	public static void getMD5KEY(Handler handler) {
		GsonRequest2.getMD5keyRequest request = new GsonRequest2.getMD5keyRequest();
		request.mac = VALUE_MAC;
		request.imei = VALUE_IMEI;
		Worker worker = new Worker(handler, RESPONSE_TYPE_GET_MD5KEY,
				GsonResponse2.getMD5keyResponse.class);
		worker.execute(RIA_INTERFACE_GET_MD5KEY, request);
	}
	
	/**
	 * 93.	收藏日记id列表;
	 * @param handler
	 */
	public static void listCollectDiaryid(Handler handler, String diary_time,
			String viewuserid) {
		GsonRequest2.listCollectDiaryidRequest request = new GsonRequest2.listCollectDiaryidRequest();
		request.pagesize = "10";
		request.diary_time = diary_time;
		request.viewuserid = viewuserid;
		//request.diarywidth = "";
		//request.diaryheight = "";
		
		request.userid = ActiveAccount.getInstance(ZApplication.getInstance()).getLookLookID();
		request.mac = VALUE_MAC;
		request.imei = VALUE_IMEI;
		Worker worker = new Worker(handler, RESPONSE_TYPE_LIST_COLLECT_DIARYID, GsonResponse2.listCollectDiaryidResponse.class);
		worker.execute(RIA_INTERFACE_LIST_COLLECT_DIARYID, request);
	}
	
	/**
	 * 95. 获取特效下载地址
	 */
	public static void getEffects(Handler handler, String version){
		GsonRequest2.getEffectsRequest request = new GsonRequest2.getEffectsRequest();
		request.version = version;
		request.phone_type = "2";
		request.userid = ActiveAccount.getInstance(ZApplication.getInstance()).getLookLookID();
		request.mac = VALUE_MAC;
		request.imei = VALUE_IMEI;
		Worker worker = new Worker(handler, RESPONSE_TYPE_GET_EFFECTS, GsonResponse2.getEffectResponse.class);
		worker.execute(RIA_INTERFACE_GET_EFFECTS, request);
	}
	
	/**
	 * 96.	解除保险箱
	 */
	public static void unSafebox(Handler handler){
		GsonRequest2.unSafeboxRequest request = new GsonRequest2.unSafeboxRequest();
		request.userid = ActiveAccount.getInstance(ZApplication.getInstance()).getLookLookID();
		request.mac = VALUE_MAC;
		request.imei = VALUE_IMEI;
		Worker worker = new Worker(handler, RESPONSE_TYPE_UNSAFEBOX, GsonResponse2.unSafeboxResponse.class);
		worker.execute(RIA_INTERFACE_UNSAFEBOX, request);
	}
	
	/**
	 * 97.	取消活动
	 */
	public static void cancleActive(Handler handler, String diaryid, String activeid){
		GsonRequest2.cancleActiveRequest request = new GsonRequest2.cancleActiveRequest();
		request.userid = ActiveAccount.getInstance(ZApplication.getInstance()).getLookLookID();
		request.diaryid = diaryid;
		request.activeid = activeid;
		request.mac = VALUE_MAC;
		request.imei = VALUE_IMEI;
		Worker worker = new Worker(handler, RESPONSE_TYPE_CANCEL_ACTIVE, GsonResponse2.cancleActiveResponse.class);
		worker.execute(RIA_INTERFACE_CANCEL_ACTIVE, request);
	}
	
	/**
	 * 100.	用户通讯录列表
	 */
	public static void phoneBook(Handler handler){
		GsonRequest2.phoneBookRequest request = new GsonRequest2.phoneBookRequest();
		request.userid = ActiveAccount.getInstance(ZApplication.getInstance()).getLookLookID();
		request.mac = VALUE_MAC;
		request.imei = VALUE_IMEI;
		Worker worker = new Worker(handler, RESPONSE_TYPE_PHONE_BOOK, GsonResponse2.phoneBookResponse.class);
		worker.execute(RIA_INTERFACE_PHONE_BOOK, request);
	}
	
	/**
	 * 101.	获取云端空间大小
	 */
	public static void getCloudSize(Handler handler){
		GsonRequest2.getCloudSizeRequest request = new GsonRequest2.getCloudSizeRequest();
		request.userid = ActiveAccount.getInstance(ZApplication.getInstance()).getLookLookID();
		request.mac = VALUE_MAC;
		request.imei = VALUE_IMEI;
		Worker worker = new Worker(handler, RESPONSE_TYPE_GETCLOUDSIZE, GsonResponse2.getCloudSizeResponse.class);
		worker.execute(RIA_INTERFACE_GETCLOUDSIZE, request);
	}
	
	/**
	 * 102.	第三方互为关注的用户列表（并且已经注册looklook的用户）
	 */
	public static void listUserSNS(Handler handler){
		GsonRequest2.listUserSNSRequest request = new GsonRequest2.listUserSNSRequest();
		request.userid = ActiveAccount.getInstance(ZApplication.getInstance()).getLookLookID();
		request.mac = VALUE_MAC;
		request.imei = VALUE_IMEI;
		Worker worker = new Worker(handler, RESPONSE_TYPE_LIST_USER_SNS, GsonResponse2.listUserSNSResponse.class);
		worker.execute(RIA_INTERFACE_LIST_USER_SNS, request);
	}
	
	/**
	 * 103.	后台推荐用户列表
	 */
	public static void listUserRecommend(Handler handler , String timestamp){
		GsonRequest2.listUserRecommendRequest request = new GsonRequest2.listUserRecommendRequest();
		request.userid = ActiveAccount.getInstance(ZApplication.getInstance()).getLookLookID();
		request.mac = VALUE_MAC;
		request.imei = VALUE_IMEI;
		request.timestamp = timestamp;
		Worker worker = new Worker(handler, RESPONSE_TYPE_LIST_USER_RECOMMEND, GsonResponse2.listUserRecommendResponse.class);
		worker.execute(RIA_INTERFACE_LIST_USER_RECOMMEND, request);
	}
	
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
		case Requester2.RESPONSE_TYPE_UA: // 提交UA响应;
			if (object != null) {
				GsonResponse2.uaResponse response = (GsonResponse2.uaResponse) object;
				CommonInfo.getInstance().equipmentid = response.equipmentid;
				CommonInfo.getInstance().ip = response.ip;

				// AppState.setUaResponse(response);
			}
			break;

		case Requester2.RESPONSE_TYPE_REGISTER: // 请求注册响应;

			break;

		case Requester2.RESPONSE_TYPE_LOGIN: // 请求登录响应;
			GsonResponse2.loginResponse response = (GsonResponse2.loginResponse) object;
			ActiveAccount.getInstance(ZApplication.getInstance())
					.setLookLookID(response.userid);
			break;

		case Requester2.RESPONSE_TYPE_GET_SOCKET: // 请求socket地址响应;

			break;

		case Requester2.RESPONSE_TYPE_TIMELINE:

			break;

		case Requester2.RESPONSE_TYPE_BINDING:

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

				String json = REQUEST_HEADER + gson.toJson(request);

				ZLog.e(">> Request2 ("
						+ request.getClass().getSimpleName() + "): "
						+ json);

				http2 = new ZHttp2();
				ZHttpResponse httpResponse = http2.post(url,
						json.getBytes());
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
						GsonResponse2.createStructureResponse response = new createStructureResponse();
						response.diaryuuid = ((createStructureRequest)request).diaryuuid;
						Log.d("=AAA=","uuid = " + response.diaryuuid);
						response.status = "-1";
						object = response;
					} else {
						Log.d("=AAA=","responseuuid = " + ((GsonResponse2.createStructureResponse)object).diaryuuid + "requestuuid = " +  ((createStructureRequest)request).diaryuuid);
						if (((GsonResponse2.createStructureResponse)object).diaryuuid == null || "".equals(((GsonResponse2.createStructureResponse)object).diaryuuid)){
							((GsonResponse2.createStructureResponse)object).diaryuuid = ((createStructureRequest)request).diaryuuid;
							Log.d("=AAA=","uuid = " + ((GsonResponse2.createStructureResponse)object).diaryuuid);
						}
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
				handler.sendMessage(message);
			} else {
				ZLog.alert();
				ZLog.e("handler is null, data can not callback.");
			}
		
		
		}


	}
	
	public static void uploadPicture(Handler handler,String filePath,String type,String attachid){
//		filePath="/mnt/sdcard/Pictures/yuyin.jpg";
		HttpPoster poster=new HttpPoster(handler,RESPONSE_TYPE_UPLOAD_PICTURE,GsonResponse2.uploadPictrue.class);
		GsonRequest2.uploadPicture requester=new GsonRequest2.uploadPicture();
		requester.upload_pic_type=type;
		requester.userid = ActiveAccount.getInstance(ZApplication.getInstance()).getLookLookID();
		requester.attachid = attachid;
		requester.mac = VALUE_MAC;
		requester.imei = VALUE_IMEI;
		poster.execute(Config.SERVER_URL_PICTURE+RIA_INTERFACE_UPLOAD_PICTURE, requester, filePath);
		
	}

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

}