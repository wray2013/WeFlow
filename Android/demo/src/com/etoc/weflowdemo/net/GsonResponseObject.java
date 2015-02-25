package com.etoc.weflowdemo.net;



public class GsonResponseObject {
	public static final int MEDIA_TYPE_NEWS = 1;
	public static final int MEDIA_TYPE_MOVIE = 2;
	public static final int MEDIA_TYPE_MUSIC = 3;
	public static final int MEDIA_TYPE_EBOOK = 4;
	public static final int MEDIA_TYPE_JOKE = 5;
	public static final int MEDIA_TYPE_TRAVEL = 6;
	public static final int MEDIA_TYPE_TOPICS = 7;

	public static class sendSMSResponse {
		public String status;// ":0;
	}
	
	public static class loginResponse {
		public String status;// ":0;
		public String uuid;  //用户唯一标示
		public String tel;   //
		public String pts;   //剩余流量币
		public String rate;  //年化收益率(%)
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
}
