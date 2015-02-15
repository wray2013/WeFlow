package com.cmmobi.looklook.common.utils;

import java.util.HashMap;

import android.content.Context;
import android.text.TextUtils;

import com.cmmobi.statistics.CmmobiClickAgent;

/**
 *  中传 cdr
 * 
 * */
public class CmmobiClickAgentWrapper {
	private static final boolean open = true;
	
	public static void onResume(Context context){
		if(open){
			CmmobiClickAgent.onResume(context);
		}

	}
	
	public static void onPause(Context context){
		if(open){
			CmmobiClickAgent.onPause(context);
		}

	}
	
	public static void onStop(Context context){
		if(open){
			CmmobiClickAgent.onStop(context);
		}

	}
	
	public static void onEvent(Context context, String tag){
		if(open){
			CmmobiClickAgent.onEvent(context, tag);
		}

	}
	
	public static void onEvent(Context context, String id,HashMap<String, String> paramHashMap){
		if(open){
			CmmobiClickAgent.onEvent(context, id, paramHashMap);
		}

	}
	
	public static void onEvent(Context context, String tag, int count){
		if(open){
			CmmobiClickAgent.onEvent(context, tag, count);
		}

	}
	
	public static void onEvent(Context context, String tag, String label){
		if(open){
			CmmobiClickAgent.onEvent(context, tag, label);
		}

	}
	
	public static void onEvent(Context context, String tag, String label, int count){
		if(open){
			CmmobiClickAgent.onEvent(context, tag, label, count);
		}

	}
	
	public static void onEventDuration(Context context, String tag, long millis){
		if(open){
			CmmobiClickAgent.onEventDuration(context, tag, millis);
		}

	}
	
	public static void onEventDuration(Context context, String tag, String label,long millis){
		if(open){
			CmmobiClickAgent.onEventDuration(context, tag, label,millis);
		}

	}
	
	public static void onEventDuration(Context context, String tag, HashMap<String, String> paramHashMap, long millis){
		if(open){
			CmmobiClickAgent.onEventDuration(context, tag, paramHashMap, millis);
		}

	}
	
	public static void onEventBegin(Context context, String tag){
		if(open){
			CmmobiClickAgent.onEventBegin(context, tag);
		}

	}
	
	public static void onEventEnd(Context context, String tag){
		if(open){
			CmmobiClickAgent.onEventEnd(context, tag);
		}

	}
	
	public static void onEventBegin(Context context, String tag, String label){
		if(open){
			CmmobiClickAgent.onEventBegin(context, tag, label);
		}

	}
	
	public static void onEventEnd(Context context, String tag, String label){
		if(open){
			CmmobiClickAgent.onEventEnd(context, tag, label);
		}

	}

	public static void startUninstallObserver(Context ctx) {
		if(open){
			CmmobiClickAgent.startUninstallObserver(ctx);
		}
	}

	
	public static void setUserid(Context ctx, String userid) {
		if(open){
			CmmobiClickAgent.setUserid(ctx,userid);
		}
	}
	
	
	
	public static String getNetworkLabel(String network){
		String label = "";
		if(!TextUtils.isEmpty(network)){
			if("WIFI".equalsIgnoreCase(network)){
				label = "1";
			}else if("2G".equalsIgnoreCase(network)){
				label = "2";
			}else if("3G".equalsIgnoreCase(network)){
				label = "3";
			}else if("4G".equalsIgnoreCase(network)){
				label = "4";
			}
		}
		return label;
	}

	
}
