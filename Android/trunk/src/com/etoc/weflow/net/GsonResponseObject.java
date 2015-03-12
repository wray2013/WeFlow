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
	}
	
	public static class sendSMSResponse extends commonResponse{
		public boolean isSucceed() {
			return "0000".equals(code) || "2009".equals(code);
		}
	}
	
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
	
	public static class loginResponse extends commonResponse {
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
	}
	
	/****************************************************
	 *                      登录/注册
	 ****************************************************/
	/****************************************************
	 *                      A.赚流量币
	 ****************************************************/
	public static class SoftInfoResp {
		public String appid;
		public String title;
		public String version;
		public String appicon;
		public String appbannerpic;//banner展示图片地址
		public String[] apppreview;//软件预览图地址数组
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
	/****************************************************
	 *                      B.花流量币
	 ****************************************************/
	public static class RechargePhoneResp {
		public String chargesid;//产品id
		public String money;// 充值面额
		public String cost;// 话费流量币
		
	}
	
	public static class MobileFlowResp {
		public String flowpkgid;
		public String title;
		public String desc;
		public String imgsrc;
		public String cost;
	}
	
	public static class ExchangeGiftResp {
		public String giftid;
		public String imgsrc;
		public String gifturl;
		public String giftdesc;
		public String title;
		public String flowcoins;//流量币
	}
	/****************************************************
	 *                      C.流量银行
	 ****************************************************/
	/****************************************************
	 *                      D.发现
	 ****************************************************/
	/****************************************************
	 *                      E.我的
	 ****************************************************/
	//2.9.2 我的账单
	public static class MyBillListResp {
		public String status;
		public BillList[] myBills;
	}
	
	
	
	public static class BillList {
		public String type;
		public String productid;
		public String title;
		public String flowcoins;
		public String time;
	}
	
}
