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
		String tel;  //
		String mac; // 手机的mac地址
		String imei; // 手机imei（手机的唯一标识）
	}
	
	public static class loginRequest {
		String tel;
		String code;
		String mac; // 手机的mac地址
		String imei; // 手机imei（手机的唯一标识）
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
	
}

