package com.etoc.weflow.event;

import android.content.Context;
import android.content.Intent;

import com.etoc.weflow.service.PromtService;

import de.greenrobot.event.EventBus;

public class DialogUtils {
	private static boolean useService = false;
	
	public static void SendLoadingDialogStart(Context context) {
		if(useService){
			Intent intent = new Intent(context, PromtService.class);
			intent.putExtra(PromtService.KEY_TYPE, PromtService.KEY_TYPE_LOADING_START);
			context.startService(intent);
		}else{
			EventBus.getDefault().post(DialogEvent.LOADING_START);
		}
	}
	
	public static void SendLoadingDialogEnd(Context context) {
		if(useService){
			Intent intent = new Intent(context, PromtService.class);
			intent.putExtra(PromtService.KEY_TYPE, PromtService.KEY_TYPE_LOADING_END);
			context.startService(intent);
		}else{
			EventBus.getDefault().post(DialogEvent.LOADING_END);
		}
	}

}
