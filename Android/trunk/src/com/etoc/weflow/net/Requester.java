package com.etoc.weflow.net;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import com.etoc.weflow.Config;
import com.etoc.weflow.event.RequestEvent;
import com.etoc.weflow.net.GsonRequestObject.*;
import com.etoc.weflow.net.GsonResponseObject.*;
import com.etoc.weflow.utils.VMobileInfo;
import com.google.gson.Gson;

import de.greenrobot.event.EventBus;

import android.os.Handler;
import android.os.Message;
import android.util.Log;


public class Requester {
	private static final int isDebug = 1;
	
	///////////////////////////////////////Response code:
	public static final int RESPONSE_TYPE_TEST = 0xffee2000;
	public static final String RIA_INTERFACE_TEST = "/test/ct";
	
	//2.1.1 车上虚拟注册
	public static final int RESPONSE_TYPE_SENDSMS = 0xffee2100;
	public static final String RIA_INTERFACE_SENDSMS = "/vs/api/getAuthCode";
	
	public static final int RESPONSE_TYPE_LOGIN = 0xffee2101;
	public static final String RIA_INTERFACE_LOGIN = "/vs/api/user/login";
	
	public static final int RESPONSE_TYPE_ACC_INFO = 0xffee2102;
	public static final String RIA_INTERFACE_ACC_INFO = "/rw/service/getaccountinfo.html";
	
	public static final int RESPONSE_TYPE_ADV_INFO = 0xffee2103;
	public static final String RIA_INTERFACE_ADV_INFO = "/rw/service/getadvinfo.html";
	
	public static final int RESPONSE_TYPE_ORDER_LARGESS = 0xffee2104;
	public static final String RIA_INTERFACE_ORDER_LARGESS = "/interface/service/orderLargess";
	
	public static final int RESPONSE_TYPE_REGISTER = 0xffee2105;
	public static final String RIA_INTERFACE_REGISTER = "/vs/api/user/register";

	public static final int RESPONSE_TYPE_VERIFY_CODE = 0xffee2106;
	public static final String RIA_INTERFACE_VERIFY_CODE = "/vs/api/verifyAuthCode";
	
	public static final int RESPONSE_TYPE_RESET_PWD = 0xffee2107;
	public static final String RIA_INTERFACE_RESET_PWD = "/vs/api/user/resetPassword";
	
	public static final int RESPONSE_TYPE_AUTO_LOGIN = 0xffee2108;
	public static final String RIA_INTERFACE_AUTO_LOGIN = "/vs/api/user/autoLogin";
	
	public static final int RESPONSE_TYPE_QUERY_BANK = 0xffee2109;
	public static final String RIA_INTERFACE_QUERY_BANK = "/vs/api/user/queryBank";
	
	public static final int RESPONSE_TYPE_ACCOUNT_INFO = 0xffee2110;
	public static final String RIA_INTERFACE_ACCOUNT_INFO = "/vs/api/user/accountInfo";
	
	public static final int RESPONSE_TYPE_ADV_LIST = 0xffee2111;
	public static final String RIA_INTERFACE_ADV_LIST = "/vs/api/user/videoHome";
	
	public static final int RESPONSE_TYPE_ADV_MORE = 0xffee2112;
	public static final String RIA_INTERFACE_ADV_MORE = "/vs/api/user/wonderfulVideos";
	
	public static final int RESPONSE_TYPE_ADV_FLOW = 0xffee2113;
	public static final String RIA_INTERFACE_ADV_FLOW = "/vs/api/user/videoflow";
	
	public static final int RESPONSE_TYPE_ADV_RECORD = 0xffee2114;
	public static final String RIA_INTERFACE_ADV_RECORD = "/vs/api/user/videoflowrecord";
	
	public static final int RESPONSE_TYPE_APP_HOME = 0xffee2115;
	public static final String RIA_INTERFACE_APP_HOME = "/vs/api/user/appHome";
	
	public static final int RESPONSE_TYPE_APP_LIST = 0xffee2116;
	public static final String RIA_INTERFACE_APP_LIST = "/vs/api/user/appList";
	
	public static final int RESPONSE_TYPE_APP_FLOW = 0xffee2117;
	public static final String RIA_INTERFACE_APP_FLOW = "/vs/api/user/appflow";
	
	public static final int RESPONSE_TYPE_APP_FLOW_RECORD = 0xffee2118;
	public static final String RIA_INTERFACE_APP_FLOW_RECORD = "/vs/api/user/appflowrecord";
	
	public static final int RESPONSE_TYPE_PHONE_CHARGE_LIST = 0xffee2119;
	public static final String RIA_INTERFACE_PHONE_CHARGE_LIST = "/vs/api/user/phoneChargeList";
	
	public static final int RESPONSE_TYPE_RECHARGE_PHONE = 0xffee2120;
	public static final String RIA_INTERFACE_RECHARGE_PHONE = "/vs/api/user/rechargePhone";
	
	public static final int RESPONSE_TYPE_QRECHARGE_LIST = 0xffee2121;
	public static final String RIA_INTERFACE_QRECHARGE_LIST = "/vs/api/user/QChargeList";
	
	public static final int RESPONSE_TYPE_RECHARGE_QQ = 0xffee2122;
	public static final String RIA_INTERFACE_RECHARGE_QQ = "/vs/api/user/rechargeQQ";
	
	public static final int RESPONSE_TYPE_GAME_PKG_LIST = 0xffee2123;
	public static final String RIA_INTERFACE_GAME_PKG_LIST = "/vs/api/user/gamePkgList";
	
	public static final int RESPONSE_TYPE_EXCHANGE_GAME_PKG = 0xffee2124;
	public static final String RIA_INTERFACE_EXCHANGE_GAME_PKG = "/vs/api/user/exchangeGamePkg";
	
	public static final int RESPONSE_TYPE_FLOW_PKG_LIST = 0xffee2125;
	public static final String RIA_INTERFACE_FLOW_PKG_LIST = "/vs/api/user/flowPkgList";
	
	public static final int RESPONSE_TYPE_EXCHANGE_FLOW_PKG = 0xffee2126;
	public static final String RIA_INTERFACE_EXCHANGE_FLOW_PKG = "/vs/api/user/exchangeGamePkg";
	
	public static final int RESPONSE_TYPE_GIFT_LIST = 0xffee2127;
	public static final String RIA_INTERFACE_GIFT_LIST = "/vs/api/user/giftList";
	
	public static final int RESPONSE_TYPE_EXCHANG_GIFT = 0xffee2128;
	public static final String RIA_INTERFACE_EXCHANG_GIFT = "/vs/api/user/exchangeGift";
	
	public static final int RESPONSE_TYPE_COST_FLOW_LIST = 0xffee2129;
	public static final String RIA_INTERFACE_COST_FLOW_LIST = "/vs/api/user/costFlowList";
	
	public static final int RESPONSE_TYPE_GAME_RECHARGE_LIST = 0xffee2130;
	public static final String RIA_INTERFACE_GAME_RECHARGE_LIST = "/vs/api/user/gameRechargeList";

	public static final int RESPONSE_TYPE_AWARD_RECORD = 0xffee2131;
	public static final String RIA_INTERFACE_AWARD_RECORD = "/vs/api/user/awardrecord";
	
	public static final int RESPONSE_TYPE_GAME_RECHARGE = 0xffee2132;
	public static final String RIA_INTERFACE_GAME_RECHARGE = "/vs/api/user/gamerecharge";
	
	public static final int RESPONSE_TYPE_SIGN_IN_LIST = 0xffee2133;
	public static final String RIA_INTERFACE_SIGN_IN_LIST = "/vs/api/user/signinlist";
	
	public static final int RESPONSE_TYPE_SIGN_IN = 0xffee2134;
	public static final String RIA_INTERFACE_SIGN_IN = "/vs/api/user/signin";
	
	public static String IMEI = VMobileInfo.getIMEI();
	public static String MAC  = VMobileInfo.getDeviceMac();
	
	public static void test(Handler handler) {
		testRequest request = new testRequest();
		request.imei = IMEI;
		request.mac  = MAC;
		PostWorker worker = new PostWorker(handler, RESPONSE_TYPE_TEST, testResponse.class);
		worker.execute(RIA_INTERFACE_TEST, request);
	}
	
	//2.1.1 获取验证码
	public static void sendSMS(Handler handler, String tel, String type) {
		getAuthCodeRequest request = new getAuthCodeRequest();
		request.tel  = tel;
		request.type = type;
		request.imei = IMEI;
		request.mac  = MAC;
		PostWorker worker = new PostWorker(handler, RESPONSE_TYPE_SENDSMS, getAuthCodeResponse.class);
		worker.execute(RIA_INTERFACE_SENDSMS, request);
	}
	
	//2.1.2 用户注册
	public static void register(Handler handler, String tel, String pass) {
		registerRequest request = new registerRequest();
		request.tel = tel;
		request.pwd = pass;
		request.imei = IMEI;
		request.mac  = MAC;
		PostWorker worker = new PostWorker(handler, RESPONSE_TYPE_REGISTER, registerResponse.class);
		worker.execute(RIA_INTERFACE_REGISTER, request);
	}
		
	//2.1.3 验证码验证
	public static void verifyAuthCode(Handler handler, String tel, String authcode, String type) {
		verifyAuthCodeRequest request = new verifyAuthCodeRequest();
		request.tel  = tel;
		request.authcode = authcode;
		request.type = type;
		request.imei = IMEI;
		request.mac  = MAC;
		PostWorker worker = new PostWorker(handler, RESPONSE_TYPE_VERIFY_CODE, verifyAuthCodeResponse.class);
		worker.execute(RIA_INTERFACE_VERIFY_CODE, request);
	}
	
	//2.1.4 用户登录
	public static void login(Handler handler, String tel, String pass) {
		loginRequest request = new loginRequest();
		request.tel = tel;
		request.pwd = pass;
		request.imei = IMEI;
		request.mac  = MAC;
		PostWorker worker = new PostWorker(handler, RESPONSE_TYPE_LOGIN, loginResponse.class);
		worker.execute(RIA_INTERFACE_LOGIN, request);
	}
	
	public static void autoLogin(Handler handler, String userid) {
		autoLogin(true, handler, userid);
	}
	
	//2.1.5 自动登录
	public static void autoLogin(boolean hasLoading, Handler handler, String userid) {
		autoLoginRequest request = new autoLoginRequest();
		request.userid = userid;
		request.imei = IMEI;
		request.mac  = MAC;
		PostWorker worker = new PostWorker(hasLoading, handler, RESPONSE_TYPE_AUTO_LOGIN, autoLoginResponse.class);
		worker.execute(RIA_INTERFACE_AUTO_LOGIN, request);
	}
	
	//2.1.6 重设密码
	public static void resetPassword(Handler handler, String tel, String newPass) {
		resetPasswordRequest request = new resetPasswordRequest();
		request.tel = tel;
		request.newpassword = newPass;
		request.imei = IMEI;
		request.mac  = MAC;
		PostWorker worker = new PostWorker(handler, RESPONSE_TYPE_RESET_PWD, resetPasswordResponse.class);
		worker.execute(RIA_INTERFACE_RESET_PWD, request);
	}
	
	
	public static void queryBank(Handler handler, String userid) {
		queryBankRequest request = new queryBankRequest();
		request.userid = userid;
		request.imei = IMEI;
		request.mac  = MAC;
		PostWorker worker = new PostWorker(handler, RESPONSE_TYPE_QUERY_BANK, QueryBankResp.class);
		worker.execute(RIA_INTERFACE_QUERY_BANK, request);
	}
	
	//2.2.1 账户基本信息查询
	public static void queryAccountInfo(Handler handler, String userid) {
		queryAccountInfo(true, handler, userid);
	}
	
	//2.2.1 账户基本信息查询
	public static void queryAccountInfo(boolean hasLoading, Handler handler, String userid) {
		accountInfoRequest request = new accountInfoRequest();
		request.userid = userid;
		request.imei = IMEI;
		request.mac  = MAC;
		PostWorker worker = new PostWorker(hasLoading, handler, RESPONSE_TYPE_ACCOUNT_INFO, AccountInfoResp.class);
		worker.execute(RIA_INTERFACE_ACCOUNT_INFO, request);
	}
	
	//2.3.1 看广告首页列表
	public static void getAdvList(boolean hasLoading, Handler handler) {
		advHomeRequest request = new advHomeRequest();
		request.imei = IMEI;
		request.mac  = MAC;
		PostWorker worker = new PostWorker(hasLoading, handler, RESPONSE_TYPE_ADV_LIST, AdvListResp.class);
		worker.execute(RIA_INTERFACE_ADV_LIST, request);
	}
	
	//2.3.2 精彩广告列表(上拉加载更多精彩广告)
	public static void getMoreAdvList(boolean hasLoading, Handler handler, String pageno) {
		advMoreRequest request = new advMoreRequest();
		request.pageno = pageno;
		request.imei = IMEI;
		request.mac  = MAC;
		PostWorker worker = new PostWorker(hasLoading, handler, RESPONSE_TYPE_ADV_MORE, AdvListMoreResp.class);
		worker.execute(RIA_INTERFACE_ADV_MORE, request);
	}
	
	//2.3.4 看广告赚流量币
	public static void getAdvFlow(boolean hasLoading,Handler handler,String userid,String videoid) {
		advFlowRequest request = new advFlowRequest();
		request.imei = IMEI;
		request.mac = MAC;
		request.userid = userid;
		request.productid = videoid;
		PostWorker worker = new PostWorker(hasLoading, handler, RESPONSE_TYPE_ADV_FLOW, AdvFlowResp.class);
		worker.execute(RIA_INTERFACE_ADV_FLOW, request);
	}
	
	//2.3.5 获取广告赚取流量币记录
	public static void getAdvRecord(boolean hasLoading,Handler handler,String userid) {
		advFlowRecordRequest request = new advFlowRecordRequest();
		request.imei = IMEI;
		request.mac = MAC;
		request.userid = userid;
		
		PostWorker worker = new PostWorker(hasLoading, handler, RESPONSE_TYPE_ADV_RECORD, AdvFlowRecordResp.class);
		worker.execute(RIA_INTERFACE_ADV_RECORD, request);
	}
	
	//2.4.1 下载软件首页
	public static void getAppHome(boolean hasLoading,Handler handler) {
		appHomeRequest request = new appHomeRequest();
		request.imei = IMEI;
		request.mac = MAC;
		
		PostWorker worker = new PostWorker(hasLoading, handler, RESPONSE_TYPE_APP_HOME, AppHomeResp.class);
		worker.execute(RIA_INTERFACE_APP_HOME, request);
	}
	
	//2.4.2 下载软件列表(上拉加载更多)
	public static void getMoreAppList(boolean hasLoading, Handler handler, String pageno) {
		appListRequest request = new appListRequest();
		request.pageno = pageno;
		request.imei = IMEI;
		request.mac = MAC;
		PostWorker worker = new PostWorker(hasLoading, handler, RESPONSE_TYPE_APP_LIST, AppListMoreResp.class);
		worker.execute(RIA_INTERFACE_APP_LIST, request);
	}
	
	//2.4.4 下载软件赚流量币
	public static void getAppFlow(boolean hasLoading,Handler handler,String userid,String appid,String type) {
		appFlowRequest request = new appFlowRequest();
		request.imei = IMEI;
		request.mac = MAC;
		request.userid = userid;
		request.productid = appid;
		request.flowtype = type;
		PostWorker worker = new PostWorker(hasLoading, handler, RESPONSE_TYPE_APP_FLOW, AppFlowResp.class);
		worker.execute(RIA_INTERFACE_APP_FLOW, request);
	}
	
	//2.4.5 获取下载软件赚取流量币记录
	public static void getAppRecord(boolean hasLoading,Handler handler,String userid) {
		appFlowRecordRequest request = new appFlowRecordRequest();
		request.imei = IMEI;
		request.mac = MAC;
		request.userid = userid;
		
		PostWorker worker = new PostWorker(hasLoading, handler, RESPONSE_TYPE_APP_FLOW_RECORD, AppFlowRecordResp.class);
		worker.execute(RIA_INTERFACE_APP_FLOW_RECORD, request);
	}
	
	//2.5.4 玩游戏赚取流量币记录
	public static void getAwardRecord(boolean hasLoading,Handler handler,String userid) {
		awardRecordRequest request = new awardRecordRequest();
		request.imei = IMEI;
		request.mac = MAC;
		request.userid = userid;
		
		PostWorker worker = new PostWorker(hasLoading, handler, RESPONSE_TYPE_AWARD_RECORD, AwardRecordResp.class);
		worker.execute(RIA_INTERFACE_AWARD_RECORD, request);
	}
	
	//2.6.1 获取话费充值列表
	public static void getPhoneChargeList(boolean hasLoading,Handler handler) {
		phoneChargeListRequest request = new phoneChargeListRequest();
		request.imei = IMEI;
		request.mac = MAC;
		
		PostWorker worker = new PostWorker(hasLoading, handler, RESPONSE_TYPE_PHONE_CHARGE_LIST, PhoneChargeListResp.class);
		worker.execute(RIA_INTERFACE_PHONE_CHARGE_LIST, request);
	}
	
	//2.6.2 话费充值
	public static void rechargePhone(boolean hasLoading,Handler handler,String userid,String phone,String productid) {
		rechargePhoneRequest request = new rechargePhoneRequest();
		request.imei = IMEI;
		request.mac = MAC;
		request.productid = productid;
		request.tel = phone;
		request.userid = userid;
		
		PostWorker worker = new PostWorker(hasLoading, handler, RESPONSE_TYPE_RECHARGE_PHONE, PhoneChargeResp.class);
		worker.execute(RIA_INTERFACE_RECHARGE_PHONE, request);
	}
	
	//2.6.4 Q币充值列表
	public static void getQChargeList(boolean hasLoading,Handler handler) {
		QChargeListRequest request = new QChargeListRequest();
		request.imei = IMEI;
		request.mac = MAC;
		
		PostWorker worker = new PostWorker(hasLoading, handler, RESPONSE_TYPE_QRECHARGE_LIST, QChargeListResp.class);
		worker.execute(RIA_INTERFACE_QRECHARGE_LIST, request);
	}
	
	//2.6.5 Q币充值
	public static void rechargeQQ(boolean hasLoading,Handler handler,String userid,String qq,String productid) {
		rechargeQQRequest request = new rechargeQQRequest();
		request.imei = IMEI;
		request.mac = MAC;
		request.productid = productid;
		request.qqnum = qq;
		request.userid = userid;
		
		PostWorker worker = new PostWorker(hasLoading, handler, RESPONSE_TYPE_RECHARGE_QQ, QChargeResp.class);
		worker.execute(RIA_INTERFACE_RECHARGE_QQ, request);
	}
	
	//2.6.6 获取游戏礼包兑换列表
	public static void getGamePkgList(boolean hasLoading,Handler handler) {
		gamePkgListRequest request = new gamePkgListRequest();
		request.imei = IMEI;
		request.mac = MAC;
		
		PostWorker worker = new PostWorker(hasLoading, handler, RESPONSE_TYPE_GAME_PKG_LIST, GamePkgListResp.class);
		worker.execute(RIA_INTERFACE_GAME_PKG_LIST, request);
	}
	
	//2.6.7 游戏礼包兑换
	public static void exchangeGamePkg(boolean hasLoading,Handler handler,String userid,String productid) {
		exchangeGamePkgRequest request = new exchangeGamePkgRequest();
		request.imei = IMEI;
		request.mac = MAC;
		request.gamepkgid = productid;
		request.userid = userid;
		
		PostWorker worker = new PostWorker(hasLoading, handler, RESPONSE_TYPE_EXCHANGE_GAME_PKG, ExchangeGamePkgResp.class);
		worker.execute(RIA_INTERFACE_EXCHANGE_GAME_PKG, request);
	}
	
	//2.6.8 游戏充值列表
	public static void getGameChargeList(boolean hasLoading,Handler handler) {
		GameChargeListRequest request = new GameChargeListRequest();
		request.imei = IMEI;
		request.mac = MAC;
		
		PostWorker worker = new PostWorker(hasLoading, handler, RESPONSE_TYPE_GAME_RECHARGE_LIST, GameChargeListResp.class);
		worker.execute(RIA_INTERFACE_GAME_RECHARGE_LIST, request);
	}
	
	//2.6.9 游戏充值
	public static void rechargeGame(boolean hasLoading,Handler handler,String userid,String productid) {
		GameRechargeRequest request = new GameRechargeRequest();
		request.imei = IMEI;
		request.mac = MAC;
		request.acctid = productid;
		request.userid = userid;
		
		PostWorker worker = new PostWorker(hasLoading, handler, RESPONSE_TYPE_GAME_RECHARGE, GameRechargeResp.class);
		worker.execute(RIA_INTERFACE_GAME_RECHARGE, request);
	}
	
	//2.6.10 获取流量包列表
	public static void getFlowPkgList(boolean hasLoading,Handler handler) {
		flowPkgListRequest request = new flowPkgListRequest();
		request.imei = IMEI;
		request.mac = MAC;
		
		PostWorker worker = new PostWorker(hasLoading, handler, RESPONSE_TYPE_FLOW_PKG_LIST, FlowPkgListResp.class);
		worker.execute(RIA_INTERFACE_FLOW_PKG_LIST, request);
	}
	
	//2.6.11 兑换流量包
	public static void exchangeFlowPkg(boolean hasLoading,Handler handler,String userid,String productid) {
		exchangeFlowPkgRequest request = new exchangeFlowPkgRequest();
		request.imei = IMEI;
		request.mac = MAC;
		request.flowpkgid = productid;
		request.userid = userid;
		
		PostWorker worker = new PostWorker(hasLoading, handler, RESPONSE_TYPE_EXCHANGE_FLOW_PKG, ExchangeFlowPkgResp.class);
		worker.execute(RIA_INTERFACE_EXCHANGE_FLOW_PKG, request);
	}
	
	//2.6.12 获取礼券兑换列表
	public static void getGiftList(boolean hasLoading,Handler handler) {
		giftListRequest request = new giftListRequest();
		request.imei = IMEI;
		request.mac = MAC;
		
		PostWorker worker = new PostWorker(hasLoading, handler, RESPONSE_TYPE_GIFT_LIST, GiftListResp.class);
		worker.execute(RIA_INTERFACE_GIFT_LIST, request);
	}
	
	//2.6.13 兑换礼券
	public static void exchangeGift(boolean hasLoading,Handler handler,String userid,String productid) {
		exchangeGiftRequest request = new exchangeGiftRequest();
		request.imei = IMEI;
		request.mac = MAC;
		request.giftid = productid;
		request.userid = userid;
		
		PostWorker worker = new PostWorker(hasLoading, handler, RESPONSE_TYPE_EXCHANG_GIFT, ExchangeGiftResp.class);
		worker.execute(RIA_INTERFACE_EXCHANG_GIFT, request);
	}
	
	//2.6.14 花流量币记录
	public static void getCostFlowRecord(boolean hasLoading,Handler handler,String userid,String type) {
		costFlowRecordRequest request = new costFlowRecordRequest();
		request.imei = IMEI;
		request.mac = MAC;
		request.userid = userid;
		request.type = type;
		
		PostWorker worker = new PostWorker(hasLoading, handler, RESPONSE_TYPE_COST_FLOW_LIST, CostFlowRecordResp.class);
		worker.execute(RIA_INTERFACE_COST_FLOW_LIST, request);
	}
	
	//2.9.5 签到列表
	public static void getSignInList(boolean hasLoading,Handler handler,String userid) {
		SignInListRequest request = new SignInListRequest();
		request.imei = IMEI;
		request.mac = MAC;
		request.userid = userid;
		
		PostWorker worker = new PostWorker(hasLoading, handler, RESPONSE_TYPE_SIGN_IN_LIST, SignInListResp.class);
		worker.execute(RIA_INTERFACE_SIGN_IN_LIST, request);
	}
	
	//2.9.10 签到
	public static void signIn(boolean hasLoading,Handler handler,String userid) {
		SignInRequest request = new SignInRequest();
		request.imei = IMEI;
		request.mac = MAC;
		request.userid = userid;
		
		PostWorker worker = new PostWorker(hasLoading, handler, RESPONSE_TYPE_SIGN_IN, SignInResp.class);
		worker.execute(RIA_INTERFACE_SIGN_IN, request);
	}
	
/*	public static void sendSMS(Handler handler, String tel) {
		sendSMSRequest request = new sendSMSRequest();
		request.phone  = tel;
		request.channelid = "app";
		request.transid = "" + System.currentTimeMillis();
		PostWorker worker = new PostWorker(handler, RESPONSE_TYPE_SENDSMS, sendSMSResponse.class);
		worker.execute(RIA_INTERFACE_SENDSMS, request);
	}
	
	public static void login(Handler handler, String tel, String code) {
		loginRequest request = new loginRequest();
		request.authcode = code;
		request.channelid = "app";
		request.transid = "" + System.currentTimeMillis();
		request.phone = tel;
		request.weixinid = "Wang_JM";
		PostWorker worker = new PostWorker(handler, RESPONSE_TYPE_LOGIN, loginResponse.class);
		worker.execute(RIA_INTERFACE_LOGIN, request);
	}
	
	public static void getAccInfo(Handler handler, String uuid) {
		getAccInfoRequest request = new getAccInfoRequest();
		request.uuid = uuid;
		request.imei = IMEI;
		request.mac  = MAC;
		PostWorker worker = new PostWorker(handler, RESPONSE_TYPE_ACC_INFO, getAccInfoResponse.class);
		worker.execute(RIA_INTERFACE_ACC_INFO, request);
	}
	
	public static void getAdvInfo(Handler handler, String uuid) {
		getAdvInfoRequest request = new getAdvInfoRequest();
		request.uuid = uuid;
		request.imei = IMEI;
		request.mac  = MAC;
		PostWorker worker = new PostWorker(handler, RESPONSE_TYPE_ADV_INFO, getAdvInfoResponse.class);
		worker.execute(RIA_INTERFACE_ADV_INFO, request);
	}
	
	public static void orderLargess(Handler handler,String phone,String type,String product) {
		orderLargessRequest request = new orderLargessRequest();
		request.channelid = "app";
		request.transid = "" + System.currentTimeMillis();
		request.productid = product;
		request.phone = phone;
		request.opertype = type;
		PostWorker worker = new PostWorker(handler, RESPONSE_TYPE_ORDER_LARGESS, orderLargessResponse.class);
		worker.execute(RIA_INTERFACE_ORDER_LARGESS, request);
	}*/
	
	public static class PostWorker extends Thread {
		private static final String TAG = "Requester";
		private Handler handler;
		private Class<?> cls;
		private Gson gson;
		private int responseType;
		private String ria_command_id;
		private Object request;
		private boolean hasLoading = true;

		public PostWorker(boolean hasLoading, Handler handler, int responseType, Class<?> cls) {
			this.hasLoading = hasLoading;
			this.handler = handler;
			this.responseType = responseType;
			this.cls = cls;
			this.gson = new Gson();
		}
		/**
		 * 
		 * @param handler
		 * @param responseType: 响应类型;
		 * @param cls: 响应对象的class类型;
		 */
		public PostWorker(/*boolean ria_type, */Handler handler, int responseType, Class<?> cls) {
//			this.use_dc = ria_type;
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
		public void run() {
			if(hasLoading)
				EventBus.getDefault().post(RequestEvent.LOADING_START);
			String url = /*(use_dc?Config.SERVER_DC_URL:Config.SERVER_RIA_URL)*/Config.SERVER_URL + ria_command_id;
			System.out.println("url--->" + url + ", responseType:" + responseType + ", request:" + gson.toJson(request));
		    Log.v(TAG,"request url--->" + url + ", responseType:" + responseType + ", request:" + gson.toJson(request));
			String ret_entity_str = null;
		    Object object = null;
		    
			if(isDebug==1){
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				object = FakeData.map.get(ria_command_id);
				
			}
			
			if(object==null){

			    HttpPost httpPostRequest = new HttpPost(url);
//			    httpPostRequest.addHeader("Content-Type", "application/json");
			    DefaultHttpClient httpClient = new DefaultHttpClient();
			    ArrayList<NameValuePair> parameters = new ArrayList<NameValuePair>();

			    String json = gson.toJson(request);
			    
			    String saltstr = MD5.encodeByMd5AndSalt(json);
			    
			    parameters.add(new BasicNameValuePair("json", json));
			    parameters.add(new BasicNameValuePair("sign", saltstr));
			    
//			    String securityJson = "{\"json\": \"" + json + "\", \"sign\":\"" + saltstr + "\"}";
			    
			    try {
					httpPostRequest.setEntity(new UrlEncodedFormEntity(parameters, HTTP.UTF_8));
					//httpPostRequest.setEntity(new StringEntity(securityJson/*json*/, HTTP.UTF_8));

					httpClient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 10000);
					httpClient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 10000);
					
					HttpResponse localHttpResponse = httpClient.execute(httpPostRequest);
				    
					if(localHttpResponse!=null){
					    ret_entity_str = EntityUtils.toString(localHttpResponse.getEntity());
					    Log.v(TAG, "ret_entity_str:" + ret_entity_str);
					    System.out.println("ret_entity_str:" + ret_entity_str);
					}

				} catch (UnsupportedEncodingException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			
			}
			
		    if(ret_entity_str!=null){
		    	try{
		    		object = gson.fromJson(ret_entity_str, cls);
		    		
		    		/*if (responseType == RESPONSE_TYPE_ORDER_LARGESS) {
		    			orderLargessResponse resp = (orderLargessResponse) object;
		    			if (resp != null && resp.blance != null) {
		    				WeFlowApplication.totalFlow = Integer.parseInt(resp.blance);
		    			}
		    		}*/
		    	}catch(Exception e){
		    		e.printStackTrace();
		    	}
		    } else{
		    	Log.v(TAG, "request url--->" + url  + "*******ret_entity_str:" + ret_entity_str);
		    	EventBus.getDefault().post(RequestEvent.RESP_NULL);

		    }
		    if(hasLoading)
		    	EventBus.getDefault().post(RequestEvent.LOADING_END);
		    
			if (handler != null) {
				if(object==null){
					Log.e(TAG, "object is null - ria_command_id:" + ria_command_id);
				}
				
				Message message = handler.obtainMessage(responseType, object);
				try {
					handler.sendMessage(message);
				} catch (Exception e) {
					Log.e(TAG, "Perhaps sending message to a Handler on a dead thread - ria_command_id:" + ria_command_id);
				}

			} else {
				Log.e(TAG, "handler is null, data can not callback - ria_command_id:" + ria_command_id);
			}
			
			
		}

	}

}
