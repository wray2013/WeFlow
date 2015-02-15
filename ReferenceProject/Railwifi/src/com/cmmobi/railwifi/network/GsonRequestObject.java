package com.cmmobi.railwifi.network;

public class GsonRequestObject {

	//example:
	public static class configInfo{
		String userid; // "9876543212345678",
		String os; //客户端系统类型 1IOS 2Andriod
		String mac; // 手机的mac地址
		String imei; // 手机imei（手机的唯一标识）
	}
	
	//2.1.1 车上虚拟注册
	public static class registerInfo{
		String uuid;// : "8034959324809234" //客户端自动生成唯一ID，必传
		String os_type;// :0 //系统 （0IOS 1安卓 2其它）,
		String nick_name;// ":"游客001" //昵称
		String telephone;// ":"13899991919" //电话
		String sex;// ":1, //性别
		String head_path;// ":, //头像,自定义头像上传。
		String birther;// "："2000-01-01" //生日

	}
	
	//2.1.2 车上修改用户基本信息
	public static class updateuserInfo{
		String uuid;//":"98513458329489320", //用户id
		String nick_name;//":"游客001" //昵称
		String sex; //":1,  //性别
		String head_path; //":,  //头像,自定义头像上传
	}
	
	//2.1.4 获取铁路资讯列表
	//2.1.11 获取电影列表
	public static class commonPageNum{
		String pageno;
		String label;
	}
	
	//2.1.5 获取铁路资讯详情

	public static class commonID{
		String object_id;
	}
	
	//2.1.8 获取订餐列表
	public static class ReqOrderList{
		String train_num;//"T95"   //车次号
	}
	
	//2.1.12 获取电影详情
	public static class commonMediaID{
		String media_id;
	}
	
	//2.1.24 旅游线路价格
	public static class commonLineID{
		String line_id;
	}
	
	//2.1.20 获取推荐人推荐内容列表
	public static class useridPageNum extends commonPageNum{
		String user_id;
	}
	
	//2.1.22 获取旅游线路列表
	public static class namePageNum extends commonPageNum{
		String name;
	}
	
	//2.1.26 支付请求服务器
	public static class travelPayReq{
		String uuid; //": "12123123"  // 
		String line_id; //": 12  //"status": 0  //0成功，其他查看错误文档
		String start_date; //": "2014-7-4"  //时间戳
		String price; //": "9999"  //价格
		String adult_count; //": "2"  //成人人数
		String kid_count; //": "2"  //儿童人数
		String contacts; //": "张三"  //联系人
		String contact_telephone; //": "1230000000"  //联系人电话
		String contacts_email; //": "2@123.com"  //联系人电邮
		String contacts_card; //": "4201023010203"  //联系人证件号

	}
	
	public static class orderID{
		String order_no;
	}
	
	//2.1.28 请求服务器PUSH接口
	public static class pushReq{
		String push_type;// ": 1  //发送类型1.单发 3.群发
		String send_uuid; //": 0  //发送端UUID
		String target_uuid; //": 0  //接收端UUID 当群发时不需要填写
		String title; // ":"标题" //标题
		String content; // ":"发的内容" //内容
		String tag; //tag PUSHtag  车次号，群发的时候填
	}

	//2.1.30 反馈接口
	public static class feedBackReq{
		String uuid; // ": 1  //反馈人UUID
		String type; //": 0  //反馈类别（1，产品，2内容）
		String feedbacktypeid; //": 0  //反馈ID，多个用逗号分隔
		String content; // ":"发的内容" //内容
		String train_num;// "T95"   //车次号
	}
	
	//2.1.31 满意度条目列表接口
	public static class surveyListReq {
		String train_num;// "T95" //车次号
	}
		
	//2.1.32 调查满意度接口
	public static class surveyReq{
//		public String facility; // ": 1  设施状况 (1-5对应页面5颗星)
//		public String service; //1 //服务态度 (1-5对应页面5颗星)
//		public String impression; // 1 //总体印象 (1-5对应页面5颗星) 
		public String name;// "张三"
		public String telephone;// "12312131232"//电话
//		public String type;// 1 //"类型（1建议，2投诉）"
//		public String content; // ":"发的内容" //内容
		public String train_num;//"T95"   //车次号
		public String survey;
	}
	
	public static class surveyElem{
		public String surveytypeid; //" : 1 //类别ID
		public String value; //" : 1 //值（值（1好，2中，3差））
	}
	
	//2.1.33 获取标签接口
	public static class commonType{
		String type;
	}
	
	//2.1.34 第三方来源弹出页面
	public static class ReqThirdPage {
		public String object_id;//"1"// 来源ID
		public String os_type;//"1"//系统类型  0 IOS 1安卓 2其它
	}
	
	//2.1.38 商品提交订单接口
	public static class ReqGoodOrder{
		public String contacts;//"张三"  //联系人
		public String contacts_telephone;//"1230000000"  //联系人电话
		public String customer_car;//"T456"//顾客乘坐车厢号
		public String customer_seat;//"A69"//座位号
		public String train_num;//"T95"   //车次号
		public String order_code;//"234234234"  //客户端订单号 
		public String order_type;// 1.餐车 0.座位
		public String people_num; //  type为餐车时填写
		public ReqGoodListElem[] list;
	}
	
	//2.1.40 请求服务器商品订单状态
	public static class ReqOrderStatus{
		String order_code;//"234234234"  //0 订单号 
	}
	
	//2.1.41 请求服务器设备信息
	public static class ReqBaseInfo{
		String ip;//"25.36.12.223"
	}
	
	//2.1.42 播放器请求
	public static class ReqMediaPlay{
		String media_id; //:123, //电影ID
		String movie_type; //1.电影 2.城市风采
	}
	
	public static class ReqGoodListElem {
		public String object_id;//"1" //商品ID 
		public String count;//"1",//数量
		public String type_id;//"1" //美食 
	}
	
	public static class ReqComplaint {
		public String name;// "张三"
		public String telephone;// "12312131232"//电话
		public String type;// 1 //"类型（1建议，2投诉）"
		public String content; // ":"发的内容" //内容
		public String train_num;//"T95"   //车次号
	}
	
	//2.1.47 列车求助
	public static class ReqTrainHelp {
		public String name; //"张三"
		public String telephone; //电话
		public String certificatetype; //证件类型（1.身份证，2.护照）
		public String no; //证件号
		public String helptype; //（1重大疾病，2争斗纷争，3举报报警，4，其他）
		public String customer_car; //车厢
		public String customer_seat; //座位
		public String content; //内容
		public String train_num;  //车次号

	}
}

