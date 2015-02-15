package com.cmmobi.looklook.common.utils;

import android.content.Context;

import com.umeng.analytics.MobclickAgent;

public class UmengclickAgentWrapper {
	static final boolean openUMeng = true;
	
	public static void setDebugMode(boolean debug){
		if(openUMeng){
			MobclickAgent.setDebugMode(debug);
		}

	}
	
	public static void updateOnlineConfig(Context context){
		if(openUMeng){
			MobclickAgent.updateOnlineConfig(context);
		}
	}
	
	public static void onResume(Context context){
		if(openUMeng){
			MobclickAgent.onResume(context);
		}
	}
	
	public static void onPause(Context context){
		if(openUMeng){
			MobclickAgent.onPause(context);
		}
	}
	
	public static void onEvent(Context context, String event){
		if(openUMeng){
		    MobclickAgent.onEvent(context, event);
		}
	}
	
	public static void onEvent(Context context, String event, String arg){
	    if(openUMeng){
			MobclickAgent.onEvent(context, event, arg);
	    }
	}
	
	public static void onEventBegin(Context context, String event){
	    if(openUMeng){
			MobclickAgent.onEventBegin(context, event);
	    }
	}

	public static void onEventBegin(Context context, String event, String arg){
	    if(openUMeng){
			MobclickAgent.onEventBegin(context, event, arg);
	    }
	}
	
	public static void onEventEnd(Context context, String event){
	    if(openUMeng){
			MobclickAgent.onEventEnd(context, event);
	    }
	}

	public static void onEventEnd(Context context, String event, String arg){
	    if(openUMeng){
			MobclickAgent.onEventEnd(context, event, arg);
	    }
	}
	
	public static void onEventDuration(Context context, String event, String arg, long duration){
	    if(openUMeng){
			MobclickAgent.onEventDuration(context, event, arg, duration);
	    }
	}
}
