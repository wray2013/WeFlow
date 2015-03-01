package com.etoc.weflowdemo.net;

public class GsonRequestObject {

	//example:
	public static class configInfo{
		String userid; // "9876543212345678",
		String os; //客户端系统类型 1IOS 2Andriod
		String mac; // 手机的mac地址
		String imei; // 手机imei（手机的唯一标识）
	}
	
	public static class sendSMSRequest {
		String phone;  //用户号码
//		String mac; // 手机的mac地址
//		String imei; // 手机imei（手机的唯一标识）
		String channelid;//渠道ID
		String transid;// 交易流水
	}
	
	public static class loginRequest {
		String phone;  //用户号码
//		String mac; // 手机的mac地址
//		String imei; // 手机imei（手机的唯一标识）
		String channelid;//渠道ID
		String transid;// 交易流水
		String weixinid;//微信号
		String authcode;//验证码
	}
	
	public static class getAccInfoRequest {
		String uuid;
		String mac; // 手机的mac地址
		String imei; // 手机imei（手机的唯一标识）
	}
	
	public static class getAdvInfoRequest {
		String uuid;
		String mac; // 手机的mac地址
		String imei; // 手机imei（手机的唯一标识）
	}
	
	public static class getAwardInfoRequest {
		String uuid;
		String mac; // 手机的mac地址
		String imei; // 手机imei（手机的唯一标识）
	}
	
	public static class lotteryRequest {
		String phone;  //用户号码
		String channelid;//渠道ID
		String transid;// 交易流水
		String productid;//产品ID
		String opertype;//操作类型
//		String mac; // 手机的mac地址
//		String imei; // 手机imei（手机的唯一标识）
	}
	
}

