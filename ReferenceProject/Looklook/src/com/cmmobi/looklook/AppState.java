package com.cmmobi.looklook;

import com.cmmobi.looklook.common.gson.GsonResponse2;

public final class AppState {
	
	private static GsonResponse2.uaResponse uaResponse; //UA响应;
	private static GsonResponse2.registerResponse registerResponse; //注册响应;
	private static GsonResponse2.loginResponse loginResponse; //登录响应;
	
	
	
	
	public static GsonResponse2.registerResponse getRegisterResponse() {
		return registerResponse;
	}

	public static void setRegisterResponse(Object registerResponse) {
		AppState.registerResponse = (GsonResponse2.registerResponse) registerResponse;
	}
	
	public static GsonResponse2.loginResponse getLoginResponse() {
		return loginResponse;
	}

	public static void setLoginResponse(Object loginResponse) {
		AppState.loginResponse = (GsonResponse2.loginResponse) loginResponse;
	}

	public static GsonResponse2.uaResponse getUaResponse() {
		return uaResponse;
	}

	public static void setUaResponse(Object uaResponse) {
		AppState.uaResponse = (GsonResponse2.uaResponse) uaResponse;
	}

	
	/**
	 * 禁止构造实例;
	 */
	private AppState() {
	}
}
