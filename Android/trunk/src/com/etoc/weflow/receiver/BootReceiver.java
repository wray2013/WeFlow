package com.etoc.weflow.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BootReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		if (intent.getAction().equals("android.intent.action.PACKAGE_ADDED")) {   
            String packageName = intent.getDataString();   
            System.out.println("安装了:" +packageName + "包名的程序");     
        }   
        //接收卸载广播  
        if (intent.getAction().equals("android.intent.action.PACKAGE_REMOVED")) {   
            String packageName = intent.getDataString();   
            System.out.println("卸载了:"  + packageName + "包名的程序");
 
        }
	}

}
