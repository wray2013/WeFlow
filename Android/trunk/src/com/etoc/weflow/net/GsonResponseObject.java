package com.etoc.weflow.net;



public class GsonResponseObject {
	public static final int MEDIA_TYPE_NEWS = 1;
	public static final int MEDIA_TYPE_MOVIE = 2;
	public static final int MEDIA_TYPE_MUSIC = 3;
	public static final int MEDIA_TYPE_EBOOK = 4;
	public static final int MEDIA_TYPE_JOKE = 5;
	public static final int MEDIA_TYPE_TRAVEL = 6;
	public static final int MEDIA_TYPE_TOPICS = 7;

	public static class testResponse {
		public String status;// ":0;
		public String id;
	}
	
	/*public static class sendSMSResponse extends commonResponse{
		public boolean isSucceed() {
			return "0000".equals(code) || "2009".equals(code);
		}
	}*/
	
	public static class commonResponse {
		public String code;// ":0;
		public String message;// 
		
		public boolean isSucceed() {
			return "0000".equals(code);
		}
		
		public boolean isRunningLow() {
			return "2012".equals(code);
		}
	}
	
/*	public static class loginResponse extends commonResponse {
		public UserInfo user;//
	}
	
	public static class orderLargessResponse extends commonResponse {
		public String blance;
		public String rate;
	}
	
	public static class UserInfo {
		public String userid;
		public String phone;
		public String blance;
		public String rate;
	}
	
	public static class getAccInfoResponse {
		public String status;// ":0;
		public String suitename; //套餐名
		public String innerflow; //套餐内剩余流量
		public String outerflow; //套餐外剩余流量
	}
	
	public static class getAdvInfoResponse {
		public String status;// ":0;
		public AdvInfo[] banners;       //轮播图对应广告
		public AdvInfo[] newadvs;       //最新广告
		public AdvInfo[] recommendadvs; //推荐广告
	}
	
	public static class AdvInfo {
		public String advid;      //广告id
//		public String advtype;    //广告类型
		public String coverurl;   //封面
		public String videourl;   //视频广告url
		public String title;      //标题
		public String time;       //更新时间
		public String content;    //广告内容
		public String instruction;//活动说明
		public String flowaward;  //奖励流量币额度
	}
	
	public static class lotteryResponse extends commonResponse {
		public String phone;
		public String blance;
	}*/
	public static class getAccInfoResponse {
		public String status;// ":0;
		public String suitename; //套餐名
		public String innerflow; //套餐内剩余流量
		public String outerflow; //套餐外剩余流量
	}
	
	public static class getAdvInfoResponse {
		public String status;// ":0;
		public AdvInfo[] banners;       //轮播图对应广告
		public AdvInfo[] newadvs;       //最新广告
		public AdvInfo[] recommendadvs; //推荐广告
	}
	
	public static class AdvInfo {
		public String advid;      //广告id
//		public String advtype;    //广告类型
		public String coverurl;   //封面
		public String videourl;   //视频广告url
		public String title;      //标题
		public String time;       //更新时间
		public String content;    //广告内容
		public String instruction;//活动说明
		public String flowaward;  //奖励流量币额度
	}
	/****************************************************
	 *                      登录/注册
	 ****************************************************/
	//2.1.1 获取验证码
	public static class getAuthCodeResponse {
		public String status;
	}
	
	//2.1.3 验证验证码
	public static class verifyAuthCodeResponse {
		public String status;
	}
	//2.1.4 用户登录
	public static class loginResponse {
		public String status;
		public String userid;
		public String tel;
		public String flowcoins;
		public String isregistration;
		public String makeflow;
		public String useflow;
		public String nickname;
	}
	
	public static class registerResponse {
		public String status;
		public String userid;
		public String tel;
		public String flowcoins;
		public String isregistration;
		public String makeflow;
		public String useflow;
		public String nickname;
	}
	
	public static class resetPasswordResponse {
		public String status;
	}
	
	public static class autoLoginResponse {
		public String status;
		public String userid;
		public String tel;
		public String flowcoins;
		public String isregistration;
		public String makeflow;
		public String useflow;
		public String nickname;
	}
	
	public static class AccountInfoResp {
		public String status;
		public String flowcoins;
		public String yestdrate;
		public String yestdincome;
		public String isregistration;
		public String menumoney;
		public String menutype;
		public String inflowleft;
		public String outflowleft;
		public String nickname;
	}
	
	/****************************************************
	 *                      A.赚流量币
	 ****************************************************/
	// 2.3.1 广告首页视频
	public static class AdvListResp {
		public String status;
		public AdverInfo[] bannerlist;
		public AdverInfo[] newestlist;
		public AdverInfo[] wonderfullist;
	}
	//2.3.2 精彩广告列表(上拉加载更多精彩广告)
	public static class AdvListMoreResp {
		public String status;
		public String hasnextpage;
		public AdverInfo[] list;
	}
	// 2.3.4 看广告赚流量币
	public static class AdvFlowResp {
		public String status;
		public String flowcoins;
	}
	// 2.3.5 获取广告赚取流量币记录
	public static class AdvFlowRecordResp {
		public String status;
		public AdverInfo[] recordlist;
	}
	// 2.4.1 下载软件首页
	public static class AppHomeResp {
		public String status;
		public SoftInfoResp[] bannerlist;
		public SoftInfoResp[] applist;
	}
	// 2.4.2 下载软件列表(上拉加载更多)
	public static class AppListMoreResp {
		public String status;
		public String hasnextpage;
		public SoftInfoResp[] list;
	}
	// 2.4.4 下载软件赚流量币
	public static class AppFlowResp {
		public String status;
		public String flowcoins;
	}
	// 2.4.5 获取下载软件赚取流量币记录
	public static class AppFlowRecordResp {
		public String status;
		public SoftInfoResp[] list;
	}
	// 2.5.2 摇一摇赚取流量币
	public static class shakeflowResp {
		public String status;
		public AwardItemResp award;
		public String flowcoins;
	}
	public static class shakeConfigResp {
		public String status;
		public String cost;
	}
	// 2.5.3 刮刮卡赚取流量币
	public static class scratchflowResp {
		public String status;
		public AwardItemResp award;
		public String flowcoins;
	}
	public static class scratchConfigResp {
		public String status;
		public String cost;
	}
	// 2.5.4 玩游戏赚取流量币记录
	public static class AwardRecordResp {
		public String status;
		public AwardInfoResp[] recordlist;
	}
	
	public static class SoftInfoResp {
		public String appid;
		public String title;
		public String version;
		public String appicon;
		public String soft;
		public String appbannerpic;//banner展示图片地址
		public String apppreview;//软件预览图地址数组
		public String introduction;//软件产品介绍
		public String instruction;//赚流量币说明
		public String flowcoins;//流量币
		public String sharecoins;//分享赚流量币
		public String size;
		public String isdownloadfinished;//是否完成下载(赚币) 0-未完成 1-已完成
		public String downloadfinishtime;//下载完成时间ms
		public String issharefinished;//是否完成分享(赚币) 0-未完成 1-已完成
		public String sharefinishtime;//分享完成时间ms
	}
	
	public static class AwardInfoResp {
		public String prizeid;
		public String prizename;
		public String title;
		public String desc;
		public String time;
		public String flowcoins;//流量币
		public String awardway;
	}
	
	public static class AwardItemResp {
		public String prizeid;
		public String prizename;
		public String atimestart;
		public String atimeend;
	}
	/****************************************************
	 *                      B.花流量币
	 ****************************************************/
	public static class PhoneChargeListResp {
		public String status;
		public RechargePhoneResp[] chargelist;
	}
	
	/*public static class RechargePhoneResp {
		public String chargesid;//产品id
		public String money;// 充值面额
		public String cost;// 花费流量币
		public String type;// 类型
//		public String typename; //显示类型名   移动话费
//		public RechargeProduct[] products; //具体产品（面额）  10元
	}*/
	
	public static class RechargePhoneResp {
//		public String chargesid;//产品id
//		public String money;// 充值面额
//		public String cost;// 花费流量币
		public String type;// 类型
		public String typename; //显示类型名   移动话费
		public RechargeProduct[] products; //具体产品（面额）  10元
	}
	
	public static class RechargeProduct {
		public String chargesid;//产品id
		public String money;// 充值面额
		public String cost;// 花费流量币
	}
	
	public static class PhoneChargeResp {
		public String status;
		public String flowcoins;
		public String cardcode;
	}
	
	public static class QChargeListResp {
		public String status;
		public RechargeQQResp[] chargelist; //size must be 1
	}
	
	public static class RechargeQQResp {
//		public String chargesid;//产品id
//		public String qcoins;// 充值面额
//		public String cost;// 花费流量币
		public String type;// 类型
		public String typename; //显示类型名 腾讯QQ
		public QRechargeProduct[] products;
	}
	
	public static class QRechargeProduct {
		public String chargesid;//产品id
		public String money;// 充值面额
		public String cost;// 花费流量币
	}
	
	public static class QChargeResp {
		public String status;
		public String flowcoins;
		public String cardcode;
	}
	
	public static class GamePkgListResp {
		public String status;
		public GameGiftResp[] chargelist;
	}
	
	public static class GameGiftResp {
		/*public String gamepkgid;
		public String title;
		public String leave;
		public String icon;
		public String cost;*/
		public String type;// 类型
		public String typename; 
		public GameGiftProduct[] products;
		
	}
	
	public static class GameGiftProduct {
		public String chargesid;//产品id
		public String title;
		public String money;// 充值面额
		public String cost;// 花费流量币
		public String desc;
		public String icon;
	}
	
	public static class GamePkgResp {
		public String productid;
		public String cost;
		public String leave;
		public String icon;
	}
	
	public static class ExchangeGamePkgResp {
		public String status;
		public String gamecode;
		public String cardcode;
		public String flowcoins;
	}
	
	public static class GameChargeListResp {
		public String status;
		public GameChargeResp[] chargelist;
	}
	
	public static class GameChargeResp {
		public String type;
		public String typename;
		public GameChargeProductResp[] products;
	}
	
	public static class GameChargeProductResp {
		public String chargesid;
		public String money;
		public String cost;
	}
	
	public static class GameRechargeResp {
		public String status;
		public String flowcoins;
		public String cardcode;
	}
	
	public static class FlowPkgListResp {
		public String status;
		public MobileFlowResp[] chargelist;
	}
	
	public static class MobileFlowResp {
//		public String flowpkgid;
		public String type;// 类型
		public String typename; //显示类型名 夜间包、半年包
		public MobileFlowProduct[] products;
	}
	
	public static class MobileFlowProduct {
		public String chargesid;
		public String title;
		public String desc;
		public String imgsrc;
		public String cost;
	}
	
	public static class ExchangeFlowPkgResp {
		public String status;
		public String flowcoins;
		public String cardcode;
	}
	
	public static class GiftListResp {
		public String status;
		public GiftBannerResp[] bannerlist;
		public GiftResp[] chargelist;
	}
	
	public static class GiftBannerResp {
		public String giftid;
		public String imgsrc;
		public String gifturl;
	}
	public static class GiftResp {
//		public String giftid;
//		public String imgsrc;
//		public String gifturl;
//		public String giftdesc;
//		public String title;
//		public String flowcoins;//流量币
		public String type;// 类型
		public String typename; //显示类型名 夜间包、半年包
		public GiftProduct[] products;
	}
	
	public static class GiftProduct {
		public String chargesid;
		public String imgsrc;
		public String gifturl;
		public String desc;
		public String title;
		public String cost;//流量币
	}
	
	public static class ExchangeGiftResp {
		public String status;
		public String flowcoins;
		public String cardcode;
	}
	
	public static class CostFlowRecordResp {
		public String status;
		public RecordResp[] list;
	}
	
	public static class RecordResp {
		public String type;
		public String productid;
		public String cost;
		public String title;
		public String content;
		public String imgsrc;
		public String date;
		public String state;
		public String cardcode;
	}
	/****************************************************
	 *                      C.流量银行
	 ****************************************************/
	//2.7.1 查询流量银行
	public static class QueryBankResp {
		public String status;
		public String thresholdpop;
		public String thresholdpush;
		public String flowbankcoins;
		public String yestdincome;
		public String yestdrate;
		public String totalincome;
	}
	
	// 2.7.2 存流量币
	public static class bankStoreResp {
		public String status;
		public String flowcoins;
		public String bankcoins;
	}
	
	// 2.7.3 取流量币
	public static class bankPopResp {
		public String status;
		public String flowcoins;
		public String bankcoins;
	}
	/****************************************************
	 *                      D.发现
	 ****************************************************/
	/****************************************************
	 *                      E.我的
	 ****************************************************/
	//2.9.2 我的账单
	public static class MyBillListResp {
		public String status;
		public BillList[] list;
	}
	
	//2.9.3意见反馈
	public static class FeedBackResp {
		public String status;
	}
	
	/****************************************************
	 *                      F.附录-主要数据结构
	 ****************************************************/
	public static class AdverInfo {
		public String videoid;      //广告id
//		public String advtype;    //广告类型
		public String title;      //标题
		public String cover;   //封面
		public String video;   //视频广告url
		
		public String flowcoins;  //流量币
		public String content;    //内容描述
		public String instruction; //活动介绍
		public String duration;   //广告时长s
		public String isfinished;   //是否完成观看(赚币) 0-未完成 1-已完成
		
		public String finishtime;      //完成时间ms
		public String publishtime;   //发布时间ms
		public String vtimestart;   //活动有效期开始ms
		public String vtimeend;    //活动有效期结束ms
	}
	
	public static class BillList {
		public String type;
		public String productid;
		public String title;
		public String content;
		public String flowcoins;
		public String time;
		public String state;    //订购状态
		public String cardcode; //卡密
	}
	
	public static class MessageList {
		public String userid;
		public String msgid;
		public String type; //栏目类型
		public String picurl;
		public String title; //消息标题
		public String content; //消息体内容
		public String flowcoins;
		public String time; //消息毫秒时间
		public String pageurl; //跳转地址
		public String productid;
		public String extradata; //预留字段
	}
	
	// 2.9.4升级接口
	public static class UpdateResp {
		public String status;//": 0,  //状态：0成功,失败查看错误文档 
		//升级信息
		public String version;//":"1.0.2";//返回最新版本号
		public String type;// ":"2";//0:已是最新版本   1:强制升级  2:普通升级
		public String filesize;//":"0";//该版本文件大小
		public String filepath;// ":" http://192.168.100.113:8899/download/xxx ";//进行升级下载地址
		public String description;// ":"更新了xxx，解决了xxx";//版本描述
		public String servertime;// ":"1344393182968";//服务器返回时间

	}
	
	// 2.9.5签到列表
	public static class SignInListResp {
		public String status;
		public String signlist;
		public String signflowcoins;
		public String signcount;
	}
	
	// 2.9.6签到
	public static class SignInResp {
		public String status;
		public String flowcoins;
		public String signflowcoins;
		public String singleflowcoins;
		public String signlist;
		public String signcount;
	}
	
	public static class PushMsgResp {
		public String msgtype; // 1 收到消息推送   2其他
		public String msgtitle; // 有新消息
		public String msgcontent; // 欢迎使用流量钱包
		public String msghint; // 点击查看
		public MessageList msglist;
	}
	
	public static class HomePageBannerWrapper{
		public String picurl;// 图片地址
		public String type;// 跳转类型
		public String sourceurl;// 跳转网页url
		public String sourcetitle;// 网页标题栏标题，当type类型为12时有效。
	}
	
	public static class HomePageBannerResp {
		public String status;
		public HomePageBannerWrapper[] bannerlist;
	}
	
	/****************************************************
	 *                      F.游戏相关
	 ****************************************************/
	public static class queryGameListResp {
		public String status;
		public GameWrapper[] gamelist;
	}
	
	public static class GameWrapper{
		public String gameid;// 游戏id
		public String gamename;// 游戏名称
		public String gamepic;// 游戏图片地址
		public String gameurl;// 游戏源地址
		public queryGameParamResp gameparams;//游戏参数
	}
	
	public static class queryGameParamResp {
		public String status;
		public String rangea; //允许范围A
		public String rangeb; //允许范围B
		public String amendment; //修正系数
		public String type;
	}
	
	public static class orderGameResp {
		public String status;
		public String flowcoins;
	}
	
	public static class NickNameResp {
		public String status;
	}
	
}
