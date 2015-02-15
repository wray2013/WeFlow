package com.cmmobi.looklook.prompt;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;
import android.widget.ProgressBar;

import com.cmmobi.looklook.MainApplication;
import com.cmmobi.looklook.R;
import com.cmmobi.looklook.activity.HomeActivity;
import com.cmmobi.looklook.common.constant.Constant;
import com.cmmobi.looklook.common.view.Xdialog;


public class Prompt {
	private static final String TAG = "Prompt";
	private static Xdialog x_dialog = null;
	
	public static boolean checkPassword(String password) 
    {  
        try{            
            Pattern pattern = Pattern.compile("^[a-zA-Z0-9_\\-\\.]\\w{5,16}$");
            Matcher matcher = pattern.matcher(password);
            return matcher.matches();

        } 
        catch(Exception e){
            e.printStackTrace();
        }
        return false; 
    }
	
	
	public static boolean checkUserName(String username) 
    {  
        try{            
            Pattern pattern = Pattern.compile("^[A-Za-z0-9\u4e00-\u9fa5]\\w{0,20}$");
            Matcher matcher = pattern.matcher(username);
            return matcher.matches();

        } 
        catch(Exception e){
            e.printStackTrace();
        }
        return false; 
    }
	
	public static boolean checkPhoneNum(String PhoneNum) 
    {  
        try{            
            Pattern pattern = Pattern.compile("^((13[0-9])|(14[0-9])|(15[0-9])|(18[0-9]))\\d{8}$");
            Matcher matcher = pattern.matcher(PhoneNum);
            return matcher.matches();

        } 
        catch(Exception e){
            e.printStackTrace();
        }
        return false; 
    }
	
	public static boolean checkYZM(String num) 
    {  
        try{            
            Pattern pattern = Pattern.compile("^\\d{6}$");
            Matcher matcher = pattern.matcher(num);
            return matcher.matches();

        } 
        catch(Exception e){
            e.printStackTrace();
        }
        return false; 
    }

	
	public static boolean checkEmail(String email) 
    {  
        try{            
            Pattern pattern = Pattern.compile("^([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$");
            Matcher matcher = pattern.matcher(email);
            return matcher.matches();

        } 
        catch(Exception e){
            e.printStackTrace();
        }
        return false; 
    }
	
	public static void Alert(String content){
		Alert(MainApplication.getAppInstance(),  content);
	}
	
	
/*	public static void Alert(Context context, String content){
		int mId = 0x12345678;

		NotificationManager mNotificationManager =
		    (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

		Notification notification = new Notification(R.drawable.looklook_logo, "Something Happened", TimeHelper.getInstance().now());
		notification.setLatestEventInfo(context, null, null, null);
		notification.tickerText = content;
		mNotificationManager.notify(mId, notification);
		mNotificationManager.cancel(mId);
	}*/

	private static ProgressDialog progressDialog;
	public static void showProgressDialog(Context content){
		try {
			if(progressDialog!=null){
				progressDialog.dismiss();
				progressDialog=null;
			}
			progressDialog=new ProgressDialog(content);
			progressDialog.show();
			progressDialog.setCancelable(false);
			progressDialog.setCanceledOnTouchOutside(false);
			progressDialog.setContentView(new ProgressBar(content));
			new Handler().postDelayed(new Runnable() {
				@Override
				public void run() {
					dimissProgressDialog();
				}
			}, 1000*30);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void dimissProgressDialog(){
		if(progressDialog!=null)
			progressDialog.dismiss();
	}
	
	public static void Alert(Context context, String content){
		int mId = 0x12345678;
		NotificationCompat.Builder mBuilder =
		        new NotificationCompat.Builder(context)
		        .setSmallIcon(R.drawable.looklook_logo)
		        .setContentTitle("LookLook")
		        .setContentText(content)
		        .setAutoCancel(true);
		// Creates an explicit intent for an Activity in your app
		Intent resultIntent = new Intent(context, HomeActivity.class);

		// The stack builder object will contain an artificial back stack for the
		// started Activity.
		// This ensures that navigating backward from the Activity leads out of
		// your application to the Home screen.
		TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
		
		// Adds the back stack for the Intent (but not the Intent itself)
		stackBuilder.addParentStack(HomeActivity.class);
		
		// Adds the Intent that starts the Activity to the top of the stack
		stackBuilder.addNextIntent(resultIntent);
		PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT );
		mBuilder.setContentIntent(resultPendingIntent);
		NotificationManager mNotificationManager =
		    (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		// mId allows you to update the notification later on.
		Notification notification = mBuilder.build();
		notification.tickerText = content;
		mNotificationManager.notify(mId, notification);
		mNotificationManager.cancel(mId);
	}
	
	public static void Dialog(Context context, boolean hasCancel, String title, String msg, DialogInterface.OnClickListener listenerPositive){
		try{
			if(hasCancel){
				new Xdialog.Builder(context)
				.setTitle(title)
				.setMessage(msg)
				.setPositiveButton(android.R.string.ok, listenerPositive)
				.setNegativeButton(android.R.string.cancel, null)
				.create().show();
			}else{
				new Xdialog.Builder(context)
				.setTitle(title)
				.setMessage(msg)
				.setPositiveButton(android.R.string.ok, listenerPositive)
				.create().show();
			}
		}catch(Exception e){
			e.printStackTrace();
		}

	}
	
	public static void Dialog(Context context, boolean hasCancel, String title, String msg, DialogInterface.OnClickListener listenerPositive, DialogInterface.OnClickListener listenerNegative){
		try{
			if(x_dialog!=null){
				x_dialog.dismiss();
				x_dialog = null;
			}
			
			if(hasCancel){
				x_dialog = new Xdialog.Builder(context)
				.setTitle(title)
				.setMessage(msg)
				.setPositiveButton(android.R.string.ok, listenerPositive)
				.setNegativeButton(android.R.string.cancel, listenerNegative)
				.create();
				x_dialog.show();
			}else{
				x_dialog = new Xdialog.Builder(context)
				.setTitle(title)
				.setMessage(msg)
				.setPositiveButton(android.R.string.ok, listenerPositive)
				.create();
				x_dialog.show();
			}
		}catch(Exception e){
			e.printStackTrace();
		}



	}
	
	public static String GetStatus(String ria_status, String crm_status){
		String result = null;
		if(crm_status!=null && !crm_status.equals("")){
			result = GetCrmStatus(crm_status);
		}else if(ria_status!=null && !ria_status.equals("")){
			result = GetRIAStatus(ria_status);
		}else {
			result = "未知错误";
		}
		
		return result;
	}
	
	public static String GetCrmStatus(String crm_status){
		String info = null;
		int crm_index = -1;
		try{
			crm_index = Integer.parseInt(crm_status);
		}catch(Exception e){
			e.printStackTrace();
		}
		
		if(crm_index==-1){
			info = "Unknow";
		}else{
			info = Constant.CRM_STATUS[crm_index];
		}
		
		return info;
	}
	
	
	public static String GetRIAStatus(String ria_status){
		String info = null;
		if(ria_status==null){
			info = "Unknow";
		}else{
			info = Constant.STATUS_MAP.get(ria_status);
		}

		
		if(info==null){
			info = "Unknow";
		}
		
		return info;
	}
	
	public static boolean isAppOnFront(){
		Context context = MainApplication.getAppInstance();
		String packageName = context.getPackageName();
		ActivityManager activityManager = (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningTaskInfo> appTask = activityManager.getRunningTasks(1);
		if(appTask!=null && appTask.size()>0){
			if(appTask.get(0).topActivity.toString().contains(packageName)){
				Log.e(TAG, "isAppOnFront is true");
				return true;
			}else{
				Log.e(TAG, "isAppOnFront is false");
			    return false;
			}
			
		}else{
			Log.e(TAG, "isAppOnFront2 is false");
			return false;
		}
		

	}
	
}
