package com.cmmobi.railwifi.network;

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

import android.os.Handler;
import android.os.Message;
import android.util.Log;
import cn.trinea.android.common.util.NetWorkUtils;

import com.cmmobi.railwifi.Config;
import com.cmmobi.railwifi.event.RequestEvent;
import com.cmmobi.railwifi.network.GsonRequestObject.ReqGoodListElem;
import com.cmmobi.railwifi.network.GsonRequestObject.ReqGoodOrder;
import com.cmmobi.railwifi.network.GsonRequestObject.ReqOrderStatus;
import com.google.gson.Gson;

import de.greenrobot.event.EventBus;

public class Requester {
	private static final int isDebug = 0;
	
	///////////////////////////////////////Response code:
	
	//2.1.1 车上虚拟注册
	public static final int RESPONSE_TYPE_REGISTERINFO = 0xffee2101;
	public static final String RIA_INTERFACE_REGISTERINFO = "/rw/service/register.html";
	
	//2.1.2 车上修改用户基本信息
	public static final int RESPONSE_TYPE_UPDATEINFO = 0xffee2102;
	public static final String RIA_INTERFACE_UPDATEINFO = "/rw/user/updateuserinfo.html";
	
	//2.1.3 列车服务首页图
	public static final int RESPONSE_TYPE_SERVICE_BANPHOTO = 0xffee2103;
	public static final String RIA_INTERFACE_SERVICE_BANPHOTO = "/rw/service/bannerphoto.html";
	
	//2.1.4 获取铁路资讯列表
	public static final int RESPONSE_TYPE_NEWSLIST = 0xffee2104;
	public static final String RIA_INTERFACE_NEWSLIST = "/rw/service/newslist.html";
	
	//2.1.5 获取铁路资讯详情
	public static final int RESPONSE_TYPE_NEWSINFO = 0xffee2105;
	public static final String RIA_INTERFACE_NEWSINFO = "/rw/service/newsinfo.html";
	
	//2.1.6 温馨提示
	public static final int RESPONSE_TYPE_PROMPT = 0xffee2106;
	public static final String RIA_INTERFACE_PROMPT = "/rw/service/prompt.html";
	
	//2.1.7 路局简介
	public static final int RESPONSE_TYPE_INTRO = 0xffee2107;
	public static final String RIA_INTERFACE_INTRO = "/rw/service/intro.html";
	
	//2.1.8 获取订餐列表
	public static final int RESPONSE_TYPE_ORDERLIST = 0xffee2108;
	public static final String RIA_INTERFACE_ORDERLIST = "/rw/service/ordering.html";
	
	//2.1.9 获取城市风光信息
	public static final int RESPONSE_TYPE_CITYSCOPE = 0xffee2109;
	public static final String RIA_INTERFACE_CITYSCOPE = "/rw/city/cityscape.html";
	
	//2.1.10 获取影音首页图
	public static final int RESPONSE_TYPE_MEDIA_BANPHOTO = 0xffee2110;
	public static final String RIA_INTERFACE_MEDIA_BANPHOTO = "/rw/media/bannerphoto.html";
	
	//2.1.11 获取电影列表
	public static final int RESPONSE_TYPE_MEDIA_MOVIELIST = 0xffee2111;
	public static final String RIA_INTERFACE_MEDIA_MOVIELIST= "/rw/media/movielist.html";
	
	//2.1.12 获取电影详情
	public static final int RESPONSE_TYPE_MEDIA_MOVIEINFO = 0xffee2112;
	public static final String RIA_INTERFACE_MEDIA_MOVIEINFO= "/rw/media/moviedetails.html";
	
	//2.1.13 获取电子书列表
	public static final int RESPONSE_TYPE_MEDIA_BOOKLIST = 0xffee2113;
	public static final String RIA_INTERFACE_MEDIA_BOOKLIST = "/rw/media/ebooklist.html";
	
	//2.1.14 获取电子书详情
	public static final int RESPONSE_TYPE_MEDIA_BOOKINFO = 0xffe2114;
	public static final String RIA_INTERFACE_MEDIA_BOOKINFO = "/rw/media/ebookdetails.html";
	
	//2.1.15 获取音乐列表
	public static final int RESPONSE_TYPE_MEDIA_MUSICLIST = 0xffee2115;
	public static final String RIA_INTERFACE_MEDIA_MUSICLIST = "/rw/media/musiclist.html";
	
	//2.1.16 获取笑话列表
	public static final int RESPONSE_TYPE_MEDIA_JOKELIST = 0xffee2116;
	public static final String RIA_INTERFACE_MEDIA_JOKELIST = "/rw/media/jokelist.html";
	
	//2.1.17 获取笑话详情
	public static final int RESPONSE_TYPE_MEDIA_JOKEINFO = 0xffee2117;
	public static final String RIA_INTERFACE_MEDIA_JOKEINFO = "/rw/media/jokedetails.html";
	
	//2.1.18 获取推荐列表
	public static final int RESPONSE_TYPE_MEDIA_RECOMMANDLIST = 0xffee2118;
	public static final String RIA_INTERFACE_MEDIA_RECOMMANDLIST= "/rw/media/recommendlist.html";
	
	//2.1.19 获取推荐详情
	public static final int RESPONSE_TYPE_MEDIA_RECOMMANDINFO = 0xffee2119;
	public static final String RIA_INTERFACE_MEDIA_RECOMMANDINFO= "/rw/media/recommenddetails.html";
	
	//2.1.20 获取推荐人推荐内容列表
	public static final int RESPONSE_TYPE_MEDIA_RECOMMANDCONTENTLIST = 0xffee2120;
	public static final String RIA_INTERFACE_MEDIA_RECOMMANDCONTENTLIST= "/rw/media/recommendContentlist.html";
	
	//2.1.22 获取旅游线路列表
	public static final int RESPONSE_TYPE_TRAVEL_LINELIST = 0xffee2122;
	public static final String RIA_INTERFACE_TRAVEL_LINELIST= "/rw/travel/linelist.html";
	
	//2.1.23 旅游线路详情
	public static final int RESPONSE_TYPE_TRAVEL_LINEINFO = 0xffee2123;
	public static final String RIA_INTERFACE_TRAVEL_LINEINFO= "/rw/travel/linedetails.html";
	
	//2.1.24 旅游线路价格
	public static final int RESPONSE_TYPE_TRAVEL_LINEPRICE = 0xffee2124;
	public static final String RIA_INTERFACE_TRAVEL_LINEPRICE= "/rw/travel/lineprice.html";
	
	//2.1.25 支付成功显示接口
	public static final int RESPONSE_TYPE_TRAVEL_PAYOK_SHOW = 0xffee2125;
	public static final String RIA_INTERFACE_TRAVEL_PAYOK_SHOW = "/rw/travel/paysuccess.html";
	
	//2.1.26 支付请求服务器
	public static final int RESPONSE_TYPE_TRAVEL_PAY = 0xffee2126;
	public static final String RIA_INTERFACE_TRAVEL_PAY= "/rw/travel/pay.html";
	
	//2.1.27 请求服务器确认支付成功
	public static final int RESPONSE_TYPE_TRAVEL_PAYCONFIRM = 0xffee2127;
	public static final String RIA_INTERFACE_TRAVEL_PAYCONFIRM = "/rw/travel/payconfirm.html";
	
	//2.1.28 请求服务器PUSH接口
	public static final int RESPONSE_TYPE_PUSH_REQUEST = 0xffee2128;
	public static final String RIA_INTERFACE_PUSH_REQUEST = "/rw/pushrequest.html";
	
	//2.1.29 查询反馈类别接口
	public static final int RESPONSE_TYPE_FEEDBACK_LIST = 0xffee2129;
	public static final String RIA_INTERFACE_FEEDBACK_LIST = "/rw/feedbacklist.html";
	
	//2.1.30 反馈接口
	public static final int RESPONSE_TYPE_FEED_BACK = 0xffee2130;
	public static final String RIA_INTERFACE_FEED_BACK = "/rw/feedback.html";
	
	//2.1.31 满意度条目列表接口
	public static final int RESPONSE_TYPE_SURVEY_LIST = 0xffee2131;
	public static final String RIA_INTERFACE_SURVEY_LIST = "/rw/surveylist.html";
	
	//2.1.32 调查满意度接口
	public static final int RESPONSE_TYPE_SURVEY = 0xffee2132;
	public static final String RIA_INTERFACE_SURVEY = "/rw/survey.html";
	
	//2.1.33 获取标签接口
	public static final int RESPONSE_TYPE_LABEL_LIST = 0xffee2133;
	public static final String RIA_INTERFACE_LABEL_LIST = "/rw/getlabellist.html";
	
	//2.1.34 第三方来源弹出页面
	public static final int RESPONSE_TYPE_THIRD_PAGE = 0xffee2134;
	public static final String RIA_INTERFACE_THIRD_PAGE= "/rw/thirdpage.html";
	
	//2.1.38 商品提交订单接口
	public static final int RESPONSE_TYPE_GOOD_ORDER = 0xffee2138;
	public static final String RIA_INTERFACE_GOOD_ORDER = "/rw/service/goodorder.html";
	
	//2.1.40 请求服务器商品订单状态
	public static final int RESPONSE_TYPE_ORDER_STATUS = 0xffee2140;
	public static final String RIA_INTERFACE_ORDER_STATUS = "/rw/service/getorderstatus.html";
	
	//2.1.41 请求服务器设备信息
	public static final int RESPONSE_TYPE_BASE_INFO = 0xffee2141;
	public static final String RIA_INTERFACE_BASE_INFO = "/rw/service/getbaseinfo.html";
	
	//2.1.42 播放器请求
	public static final int RESPONSE_TYPE_MOVIE_PLAY = 0xffee2142;
	public static final String RIA_INTERFACE_MOVIE_PLAY = "/rw/media/movieplay.html";

	//2.1.43 箩筐发现
	public static final int RESPONSE_TYPE_DISCOVER = 0xffee2143;
	public static final String RIA_INTERFACE_DISCOVER = "/rw/discover.html";
	
	//2.1.44 列车投诉与建议
	public static final int RESPONSE_TYPE_COMPLAINT = 0xffee2144;
	public static final String RIA_INTERFACE_COMPLAINT = "/rw/complaint.html";
	
	//2.1.45 获取音乐详情
	public static final int RESPONSE_TYPE_MUSIC_DETAILS = 0xffee2145;
	public static final String RIA_INTERFACE_MUSIC_DETAILS = "/rw/media/musicdetails.html";
	
	//2.1.46 获取搜狐视频信息
	public static final int RESPONSE_TYPE_SOHU_MOVIE = 0xffee2146;
	public static final String RIA_INTERFACE_SOHU_MOVIE = "/rw/sohumovie.html";
	
	//2.1.47 列车求助
	public static final int RESPONSE_TYPE_TRAIN_HELP = 0xffee2147;
	public static final String RIA_INTERFACE_TRAIN_HELP = "/rw/trainhelp.html";
	/**
	 * 2.1.1 车上虚拟注册
	 * */
	public static void requestRegisterInfo(Handler handler, String uuid, String nick_name, String telephone, String sex, String head_path, String birther) {
		GsonRequestObject.registerInfo request = new GsonRequestObject.registerInfo();
		request.uuid = uuid;
		request.os_type = "1"; //android

		request.nick_name = nick_name;
		request.telephone = telephone;
		request.sex = sex;
		request.head_path = head_path;
		request.birther = birther;

		PostWorker worker = new PostWorker(false, handler, RESPONSE_TYPE_REGISTERINFO, GsonResponseObject.registerInfoResp.class);
		worker.execute(RIA_INTERFACE_REGISTERINFO, request);
	}
	
	/**
	 * 2.1.2 车上修改用户基本信息
	 * @deprecated
	 * */
	public static void requestUpdateInfo(Handler handler, String uuid, String nick_name, String telephone, int sex, String head_path, String birther) {
		GsonRequestObject.updateuserInfo request = new GsonRequestObject.updateuserInfo();
		request.uuid = uuid;

		request.nick_name = nick_name;
		request.sex = String.valueOf(sex);
		request.head_path = head_path;

		PostWorker worker = new PostWorker(false, handler, RESPONSE_TYPE_UPDATEINFO, GsonResponseObject.updateInfoResp.class);
		worker.execute(RIA_INTERFACE_UPDATEINFO, request);
	}
	
	/**
	 * 2.1.3 列车服务首页图
	 * */
	public static void requestServiceBannerPhoto(Handler handler) {

		PostWorker worker = new PostWorker(false, handler, RESPONSE_TYPE_SERVICE_BANPHOTO, GsonResponseObject.serviceBannerphotoResp.class);
		worker.execute(RIA_INTERFACE_SERVICE_BANPHOTO, new Object());
	}
	
	
	/**
	 * 2.1.4 获取铁路资讯列表
	 * */
	public static void requestNewsList(Handler handler, int pageNo) {
		GsonRequestObject.commonPageNum request = new GsonRequestObject.commonPageNum();
		request.pageno = String.valueOf(pageNo);
		
		PostWorker worker = new PostWorker(false, handler, RESPONSE_TYPE_NEWSLIST, GsonResponseObject.newsListResp.class);
		worker.execute(RIA_INTERFACE_NEWSLIST, request);
	}

	
	/**
	 * 2.1.5 获取铁路资讯详情
	 * */
	public static void requestNewsInfo(Handler handler, String id) {
		GsonRequestObject.commonID request = new GsonRequestObject.commonID();
		request.object_id = id;
		
		PostWorker worker = new PostWorker(false, handler, RESPONSE_TYPE_NEWSINFO, GsonResponseObject.newsInfoContent.class);
		worker.execute(RIA_INTERFACE_NEWSINFO, request);
	}
	
	/**
	 * 2.1.6 温馨提示
	 * */
	public static void requestPrompt(Handler handler) {
		
		PostWorker worker = new PostWorker(false, handler, RESPONSE_TYPE_PROMPT, GsonResponseObject.commonContent.class);
		worker.execute(RIA_INTERFACE_PROMPT, new Object());
	}
	
	/**
	 * 2.1.7 路局简介
	 * */
	public static void requestIntro(Handler handler) {
		
		PostWorker worker = new PostWorker(false, handler, RESPONSE_TYPE_INTRO, GsonResponseObject.commonContent.class);
		worker.execute(RIA_INTERFACE_INTRO, new Object());
	}
	
	/**
	 * 2.1.8 获取订餐列表
	 * */
	public static void requestOrderList(Handler handler,String train_num) {	
		GsonRequestObject.ReqOrderList request = new GsonRequestObject.ReqOrderList();
		request.train_num = train_num;
		
		PostWorker worker = new PostWorker(true, handler, RESPONSE_TYPE_ORDERLIST, GsonResponseObject.orderListResp.class);
		worker.execute(RIA_INTERFACE_ORDERLIST, request);
	}
	
	
	/**
	 * 2.1.9 获取城市风光信息
	 * */
	public static void requestCityScape(Handler handler) {
		PostWorker worker = new PostWorker(false, handler, RESPONSE_TYPE_CITYSCOPE, GsonResponseObject.cityScopeListResp.class);
		worker.execute(RIA_INTERFACE_CITYSCOPE, new Object());
	}
	
	/**
	 * 2.1.10 获取影音首页图
	 * */
	public static void requestMediaBanPhoto(Handler handler) {
		PostWorker worker = new PostWorker(false, handler, RESPONSE_TYPE_MEDIA_BANPHOTO, GsonResponseObject.serviceBannerphotoResp.class);
		worker.execute(RIA_INTERFACE_MEDIA_BANPHOTO, new Object());
	}
	
	/**
	 * 2.1.11 获取电影列表
	 * */
	public static void requestMovieList(Handler handler, int pageNo, String label) {
		GsonRequestObject.commonPageNum request = new GsonRequestObject.commonPageNum();
		request.pageno = String.valueOf(pageNo);
		if(label!=null){
			request.label = label;
		}

		
		PostWorker worker = new PostWorker(false, handler, RESPONSE_TYPE_MEDIA_MOVIELIST, GsonResponseObject.mediaListResp.class);
		worker.execute(RIA_INTERFACE_MEDIA_MOVIELIST, request);
	}
	
	/**
	 * 2.1.12 获取电影详情
	 * */
	public static void requestMovieInfo(Handler handler, String id) {
		GsonRequestObject.commonMediaID request = new GsonRequestObject.commonMediaID();
		request.media_id = id;
		
		PostWorker worker = new PostWorker(false, handler, RESPONSE_TYPE_MEDIA_MOVIEINFO, GsonResponseObject.mediaDetailInfoResp.class);
		worker.execute(RIA_INTERFACE_MEDIA_MOVIEINFO, request);
	}
	
	/**
	 * 2.1.13 获取电子书列表
	 * @deprecated
	 * */
	public static void requestBookList(Handler handler, int pageNo) {
		GsonRequestObject.commonPageNum request = new GsonRequestObject.commonPageNum();
		request.pageno = String.valueOf(pageNo);
		
		PostWorker worker = new PostWorker(false, handler, RESPONSE_TYPE_MEDIA_BOOKLIST, GsonResponseObject.mediaListResp.class);
		worker.execute(RIA_INTERFACE_MEDIA_BOOKLIST, request);
	}
	
	/**
	 * 2.1.14 获取电子书详情
	 * @deprecated
	 * */
	public static void requestBookInfo(Handler handler, String id) {
		GsonRequestObject.commonID request = new GsonRequestObject.commonID();
		request.object_id = id;
		
		PostWorker worker = new PostWorker(false, handler, RESPONSE_TYPE_MEDIA_BOOKINFO, GsonResponseObject.mediaDetailInfoResp.class);
		worker.execute(RIA_INTERFACE_MEDIA_BOOKINFO, request);
	}
	
	
	/**
	 * 2.1.15 获取音乐列表
	 * */
	public static void requestMusicList(Handler handler, int pageNo, String label) {
		GsonRequestObject.commonPageNum request = new GsonRequestObject.commonPageNum();
		request.pageno = String.valueOf(pageNo);
		request.label = label;
		PostWorker worker = new PostWorker(false, handler, RESPONSE_TYPE_MEDIA_MUSICLIST, GsonResponseObject.musicListResp.class);
		worker.execute(RIA_INTERFACE_MEDIA_MUSICLIST, request);
	}
	
	/**
	 * 2.1.16 获取笑话列表
	 * */
	public static void requestJokeList(Handler handler, int pageNo,String label) {
		GsonRequestObject.commonPageNum request = new GsonRequestObject.commonPageNum();
		request.pageno = String.valueOf(pageNo);
		request.label = label;
		PostWorker worker = new PostWorker(false, handler, RESPONSE_TYPE_MEDIA_JOKELIST, GsonResponseObject.mediaListResp.class);
		worker.execute(RIA_INTERFACE_MEDIA_JOKELIST, request);
	}
	
	/**
	 * 2.1.17 获取笑话详情
	 * */
	public static void requestJokeInfo(Handler handler, String id) {
		GsonRequestObject.commonMediaID request = new GsonRequestObject.commonMediaID();
		request.media_id = id;
		
		PostWorker worker = new PostWorker(false, handler, RESPONSE_TYPE_MEDIA_JOKEINFO, GsonResponseObject.JokDetailInfoResp.class);
		worker.execute(RIA_INTERFACE_MEDIA_JOKEINFO, request);
	}
	
	
	/**
	 * 2.1.18 获取推荐列表
	 * */
	public static void requestRecommendList(Handler handler) {
		PostWorker worker = new PostWorker(false, handler, RESPONSE_TYPE_MEDIA_RECOMMANDLIST, GsonResponseObject.recmmandListResp.class);
		worker.execute(RIA_INTERFACE_MEDIA_RECOMMANDLIST, new Object());
	}
	
	/**
	 * 2.1.19 获取推荐详情
	 * */
	public static void requestRecommendInfo(Handler handler, String id) {
		GsonRequestObject.commonID request = new GsonRequestObject.commonID();
		request.object_id = id;
		
		PostWorker worker = new PostWorker(false, handler, RESPONSE_TYPE_MEDIA_RECOMMANDINFO, GsonResponseObject.recmmandInfoResp.class);
		worker.execute(RIA_INTERFACE_MEDIA_RECOMMANDINFO, request);
	}
	
	/**
	 * 2.1.20 获取推荐人推荐内容列表
	 * @deprecated
	 * */
	public static void requestRecommendContentList(Handler handler, String user_id, int pageNo) {
		GsonRequestObject.useridPageNum request = new GsonRequestObject.useridPageNum();
		request.user_id = user_id;
		request.pageno = String.valueOf(pageNo);
		
		PostWorker worker = new PostWorker(false, handler, RESPONSE_TYPE_MEDIA_RECOMMANDCONTENTLIST, GsonResponseObject.recmmandContentListResp.class);
		worker.execute(RIA_INTERFACE_MEDIA_RECOMMANDCONTENTLIST, request);
	}
	
	/**
	 * 2.1.22 获取旅游线路列表
	 * */
	public static void requestTravelLineList(Handler handler, String name , int pageNo) {
		GsonRequestObject.namePageNum request = new GsonRequestObject.namePageNum();
		request.pageno = String.valueOf(pageNo);
		request.name = name;
		
		PostWorker worker = new PostWorker(false, handler, RESPONSE_TYPE_TRAVEL_LINELIST, GsonResponseObject.travelLineListResp.class);
		worker.execute(RIA_INTERFACE_TRAVEL_LINELIST, request);
	}
	
	
	/**
	 * 2.1.23 旅游线路详情
	 * */
	public static void requestTravelLineInfo(Handler handler, String line_id) {
		GsonRequestObject.commonLineID request = new GsonRequestObject.commonLineID();
		request.line_id = line_id;
		
		PostWorker worker = new PostWorker(false, handler, RESPONSE_TYPE_TRAVEL_LINEINFO, GsonResponseObject.travelLineInfoResp.class);
		worker.execute(RIA_INTERFACE_TRAVEL_LINEINFO, request);
	}
	
	/**
	 * 2.1.24 旅游线路价格
	 * */
	public static void requestTravelLinePrice(Handler handler, String line_id) {
		GsonRequestObject.commonLineID request = new GsonRequestObject.commonLineID();
		request.line_id = line_id;
		
		PostWorker worker = new PostWorker(false, handler, RESPONSE_TYPE_TRAVEL_LINEPRICE, GsonResponseObject.travelLinePriceResp.class);
		worker.execute(RIA_INTERFACE_TRAVEL_LINEPRICE, request);
	}
	
	/**
	 * 2.1.25 支付成功显示接口
	 * */
	public static void requestTravelPayShow(Handler handler, String line_id) {
		GsonRequestObject.commonLineID request = new GsonRequestObject.commonLineID();
		request.line_id = line_id;
		
		PostWorker worker = new PostWorker(false, handler, RESPONSE_TYPE_TRAVEL_PAYOK_SHOW, GsonResponseObject.travelPayShowResp.class);
		worker.execute(RIA_INTERFACE_TRAVEL_PAYOK_SHOW, request);
	}
	
	
	/**
	 * 2.1.26 支付请求服务器
	 * */
	public static void requestTravelPay(Handler handler, String uuid, String line_id, String start_date, 
			String price, String adult_count, String kid_count, String contacts, 
			String contact_telephone, String contacts_email, String contacts_card) {
		GsonRequestObject.travelPayReq request = new GsonRequestObject.travelPayReq();
		request.uuid = uuid;
		request.line_id = line_id;
		request.start_date = start_date;
		request.price = price;
		request.adult_count = adult_count;
		request.kid_count = kid_count;
		request.contacts = contacts;
		request.contact_telephone = contact_telephone;
		request.contacts_email = contacts_email;
		request.contacts_card = contacts_card;
		
		PostWorker worker = new PostWorker(true, handler, RESPONSE_TYPE_TRAVEL_PAY, GsonResponseObject.travePayResp.class);
		worker.execute(RIA_INTERFACE_TRAVEL_PAY, request);
	}
	
	/**
	 * 2.1.27 请求服务器确认支付成功
	 * */
	public static void requestTravelLineConfirm(Handler handler, String order_no) {
		GsonRequestObject.orderID request = new GsonRequestObject.orderID();
		request.order_no = order_no;
		
		PostWorker worker = new PostWorker(true, handler, RESPONSE_TYPE_TRAVEL_PAYCONFIRM, GsonResponseObject.payConfirmResp.class);
		worker.execute(RIA_INTERFACE_TRAVEL_PAYCONFIRM, request);
	}
	
	
	/**
	 * 2.1.28 请求服务器PUSH接口
	 * */
	public static void requestPush(Handler handler, String send_uuid, String target_uuid, String title, String content, String tag) {
		GsonRequestObject.pushReq request = new GsonRequestObject.pushReq();
		request.send_uuid = send_uuid;
		request.title = title;
		request.content = content;
		if(target_uuid==null){
			request.push_type = "3";
			request.tag = tag;
		}else{
			request.target_uuid = target_uuid;
			request.push_type = "1";
		}
		
		PostWorker worker = new PostWorker(true, handler, RESPONSE_TYPE_PUSH_REQUEST, GsonResponseObject.commonContent.class);
		worker.execute(RIA_INTERFACE_PUSH_REQUEST, request);
	}
	
	/**
	 * 2.1.29 查询反馈类别接口
	 * */
	public static void requestFeedBackList(Handler handler) {
		PostWorker worker = new PostWorker(false, handler, RESPONSE_TYPE_FEEDBACK_LIST, GsonResponseObject.feedbacklistResp.class);
		worker.execute(RIA_INTERFACE_FEEDBACK_LIST, new Object());
	}
	
	/**
	 * 2.1.30 反馈接口
	 * */
	public static void requestFeedBack(Handler handler, String uuid,String type, String feedbacktypeid, String content,String train_num) {
		GsonRequestObject.feedBackReq request = new GsonRequestObject.feedBackReq();
		request.uuid = uuid;
		request.type = type;
		request.feedbacktypeid = feedbacktypeid;
		request.content = content;
		request.train_num = train_num;
		
		PostWorker worker = new PostWorker(false, handler, RESPONSE_TYPE_FEED_BACK, GsonResponseObject.commonContent.class);
		worker.execute(RIA_INTERFACE_FEED_BACK, request);
	}
	
	/**
	 * 2.1.31 满意度条目列表接口
	 * */
	public static void requestSurveyList(Handler handler, String train_num) {
		GsonRequestObject.surveyListReq request = new GsonRequestObject.surveyListReq();
		request.train_num = train_num;
		PostWorker worker = new PostWorker(false, handler, RESPONSE_TYPE_SURVEY_LIST, GsonResponseObject.surveylistResp.class);
		worker.execute(RIA_INTERFACE_SURVEY_LIST, request);
	}
	
	/**
	 * 2.1.32 调查满意度接口
	 * */
	public static void requestSurvey(Handler handler, String name, String telephone,/*String type,String content,*/String train_num, String survey) {
		GsonRequestObject.surveyReq request = new GsonRequestObject.surveyReq();
		request.telephone = telephone;
		request.name = name;
//		request.type = type;
//		request.content = content;
		request.train_num = train_num;
		request.survey = survey;
		PostWorker worker = new PostWorker(false, handler, RESPONSE_TYPE_SURVEY, GsonResponseObject.surveyResp.class);
		worker.execute(RIA_INTERFACE_SURVEY, request);
	}
	
	/**
	 * 2.1.33 获取标签接口
	 * */
	public static void requestLabelList(Handler handler, int type) {
		GsonRequestObject.commonType request = new GsonRequestObject.commonType();
		request.type = String.valueOf(type);

		PostWorker worker = new PostWorker(false, handler, RESPONSE_TYPE_LABEL_LIST, GsonResponseObject.labelListResp.class);
		worker.execute(RIA_INTERFACE_LABEL_LIST, request);
	}
	
	/**
	 * 2.1.34 第三方来源弹出页面
	 * */
	public static void requestThirdPage(Handler handler, String object_id) {
		GsonRequestObject.ReqThirdPage request = new GsonRequestObject.ReqThirdPage();
		request.object_id = object_id;
		request.os_type = "1";

		PostWorker worker = new PostWorker(false, handler, RESPONSE_TYPE_THIRD_PAGE, GsonResponseObject.commonContent.class);
		worker.execute(RIA_INTERFACE_THIRD_PAGE, request);
	}
	
	/**
	 * 2.1.38 商品提交订单接口
	 * */
	public static void requestGoodOrder(Handler handler, String contacts,String contacts_telephone,String customer_car,String customer_seat,ReqGoodListElem[] list,String train_num,String order_code,String type,String people_num) {
		GsonRequestObject.ReqGoodOrder request = new GsonRequestObject.ReqGoodOrder();
		request.contacts = contacts;
		request.contacts_telephone = contacts_telephone;
		request.customer_car = customer_car;
		request.customer_seat = customer_seat;
		request.train_num = train_num;
		request.order_code = order_code;
		request.order_type = type;
		request.people_num = people_num;
		request.list = list;
		
		PostWorker worker = new PostWorker(true, handler, RESPONSE_TYPE_GOOD_ORDER, GsonResponseObject.GoodOrderResp.class);
		worker.execute(RIA_INTERFACE_GOOD_ORDER, request);
	}
	
	//2.1.40 请求服务器商品订单状态
	public static void requestOrderStatus(Handler handler,String order_code) {
		GsonRequestObject.ReqOrderStatus request = new GsonRequestObject.ReqOrderStatus();
		request.order_code = order_code;
		
		PostWorker worker = new PostWorker(true, handler, RESPONSE_TYPE_ORDER_STATUS, GsonResponseObject.OrderStatusResp.class);
		worker.execute(RIA_INTERFACE_ORDER_STATUS, request);
	}
	
	//2.1.41 请求服务器设备信息
	public static void requestBaseInfo(Handler handler) {
		GsonRequestObject.ReqBaseInfo request = new GsonRequestObject.ReqBaseInfo();
		request.ip = NetWorkUtils.getLocalIpAddress();
		PostWorker worker = new PostWorker(false, handler, RESPONSE_TYPE_BASE_INFO, GsonResponseObject.BaseInfoResp.class);
		worker.execute(RIA_INTERFACE_BASE_INFO, request);
	}
	
	//2.1.42 播放器请求
	public static void requestMoviePlay(Handler handler, String media_id, String movie_type) {
		GsonRequestObject.ReqMediaPlay request = new GsonRequestObject.ReqMediaPlay();
		request.media_id = media_id;
		request.movie_type = movie_type;
		PostWorker worker = new PostWorker(false, handler, RESPONSE_TYPE_MOVIE_PLAY, GsonResponseObject.commonContent.class);
		worker.execute(RIA_INTERFACE_MOVIE_PLAY, request);
	}
	
	//2.1.43 箩筐发现
	public static void requestDiscover(Handler handler) {
		PostWorker worker = new PostWorker(false, handler, RESPONSE_TYPE_DISCOVER, GsonResponseObject.DiscoverResp.class);
		worker.execute(RIA_INTERFACE_DISCOVER, new Object());
	}
	
	//2.1.44 列车投诉与建议
	public static void requestComplaint(Handler handler,String name,String phone,String type,String content,String train_num) {
		GsonRequestObject.ReqComplaint request = new GsonRequestObject.ReqComplaint();
		request.name = name;
		request.telephone = phone;
		request.content = content;
		request.type = type;
		request.train_num = train_num;
		
		PostWorker worker = new PostWorker(false, handler, RESPONSE_TYPE_COMPLAINT, GsonResponseObject.commonContent.class);
		worker.execute(RIA_INTERFACE_COMPLAINT, request);
		
	}
	
	//2.1.45 获取音乐详情
	public static void requestMusicDetail(Handler handler, String mediaId){
		GsonRequestObject.commonMediaID request = new GsonRequestObject.commonMediaID();
		request.media_id = mediaId;
		
		PostWorker worker = new PostWorker(false, handler, RESPONSE_TYPE_MUSIC_DETAILS, GsonResponseObject.musicDetailResp.class);
		worker.execute(RIA_INTERFACE_MUSIC_DETAILS, request);
	}
	
	//2.1.46 获取搜狐视频信息
	public static void requestSohuMovie(Handler handler, String mediaId){
		GsonRequestObject.commonMediaID request = new GsonRequestObject.commonMediaID();
		request.media_id = mediaId;
		
		PostWorker worker = new PostWorker(false, handler, RESPONSE_TYPE_SOHU_MOVIE, GsonResponseObject.sohuMovieResp.class);
		worker.execute(RIA_INTERFACE_SOHU_MOVIE, request);
	}
	
	//2.1.47 列车求助
	public static void requestTrainHelp(Handler handler, String name, String telephone, String certificatetype, String no, String helptype, String customer_car, String customer_seat, String content, String train_num){
		GsonRequestObject.ReqTrainHelp request = new GsonRequestObject.ReqTrainHelp();
		request.name = name;
		request.telephone = telephone;
		request.certificatetype = certificatetype;
		request.no = no;
		request.helptype = helptype;
		request.customer_car = customer_car;
		request.customer_seat = customer_seat;
		request.content = content;
		request.train_num = train_num;
		PostWorker worker = new PostWorker(false, handler, RESPONSE_TYPE_TRAIN_HELP, GsonResponseObject.TrainHelpResp.class);
		worker.execute(RIA_INTERFACE_TRAIN_HELP, request);
	}
	
	public static class PostWorker extends Thread {
		private static final String TAG = "Requester";
		private Handler handler;
		private Class<?> cls;
		private Gson gson;
		private int responseType;
		private String ria_command_id;
		private Object request;
		private boolean use_dc;

		/**
		 * 
		 * @param handler
		 * @param responseType: 响应类型;
		 * @param cls: 响应对象的class类型;
		 */
		public PostWorker(boolean ria_type, Handler handler, int responseType, Class<?> cls) {
			this.use_dc = ria_type;
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
			if(!RIA_INTERFACE_MOVIE_PLAY.equals(ria_command_id)){
				EventBus.getDefault().post(RequestEvent.LOADING_START);
			}
			String url = (use_dc?Config.SERVER_DC_URL:Config.SERVER_RIA_URL) + ria_command_id;
			System.out.println("url--->" + url + ", responseType:" + responseType + ", request:" + gson.toJson(request));
		    Log.v(TAG,"request url--->" + url + ", responseType:" + responseType + ", request:" + gson.toJson(request));
			String ret_entity_str = null;
		    Object object = null;
		    
			if(isDebug==1){
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				object = FakeData.map.get(ria_command_id);
//				Log.v(TAG, "object:" + gson.toJson(object));
				if (responseType == RESPONSE_TYPE_DISCOVER) {
//					GsonResponseObject.DiscoverResp r43 = FakeData.createDiscoverResp();
					
//					object = r43;
				}
				
			}
			
			if(object==null){

			    HttpPost httpPostRequest = new HttpPost(url);
			    DefaultHttpClient httpClient = new DefaultHttpClient();
			    ArrayList<NameValuePair> parameters = new ArrayList<NameValuePair>();

			    String json = gson.toJson(request);
			    
			    parameters.add(new BasicNameValuePair("requestapp", json));
				
			    
			    try {
					httpPostRequest.setEntity(new UrlEncodedFormEntity(parameters, HTTP.UTF_8));
				   
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
		    	}catch(Exception e){
		    		e.printStackTrace();
		    	}
		    } else{
		    	Log.v(TAG, "request url--->" + url  + "*******ret_entity_str:" + ret_entity_str);
		    	EventBus.getDefault().post(RequestEvent.RESP_NULL);

		    }
		    
		    if (responseType == RESPONSE_TYPE_GOOD_ORDER) {
		    	if (object == null) {
		    		object = new GsonResponseObject.GoodOrderResp();
		    		((GsonResponseObject.GoodOrderResp) object).status = "-1";
		    	}
		    	((GsonResponseObject.GoodOrderResp) object).order_code = ((ReqGoodOrder) request).order_code;
	    	} else if (responseType == RESPONSE_TYPE_ORDER_STATUS) {
	    		if (object == null) {
	    			object = new GsonResponseObject.OrderStatusResp();
	    			((GsonResponseObject.OrderStatusResp) object).status = "-1";
	    		}
	    		Log.d(TAG,"RESPONSE_TYPE_ORDER_STATUS object = " + object);
	    		((GsonResponseObject.OrderStatusResp) object).order_code = ((ReqOrderStatus) request).order_code;
	    	}

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
			
			if(!RIA_INTERFACE_MOVIE_PLAY.equals(ria_command_id)){
				EventBus.getDefault().post(RequestEvent.LOADING_END);
			}
		
		
		}



	}

}
