package com.etoc.weflowdemo.dialog;

import java.lang.reflect.Field;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.etoc.weflowdemo.MainApplication;
import com.etoc.weflowdemo.R;
import com.etoc.weflowdemo.activity.MainActivity;
import com.etoc.weflowdemo.util.StringUtils;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;


public class PromptDialog {
	private static final String TAG = "Prompt";
	private static Xdialog x_dialog = null;
		
	public static boolean checkPhoneNum(String PhoneNum) 
    {  
        try{            
            Pattern pattern = Pattern.compile("^((13[0-9])|(14[0-9])|(15[0-9])|(17[0-9])|(18[0-9]))\\d{8}$");
            Matcher matcher = pattern.matcher(PhoneNum);
            return matcher.matches();

        } 
        catch(Exception e){
            e.printStackTrace();
        }
        return false; 
    }
	
	public static boolean checkSeatNum(String siteNum) {
		if (siteNum == null|| siteNum.length() == 0 || siteNum.length() > 3) {
			return false;
		} else {
			 try{            
		            Pattern pattern = Pattern.compile("^[0-9]*[0-9A-Za-z]$");
		            Matcher matcher = pattern.matcher(siteNum);
		            return matcher.matches();
		        } 
		        catch(Exception e){
		            e.printStackTrace();
		        }
		        return false; 
		}
	}
	
	public static boolean checkCarNum(String carNum) {
		if (carNum == null|| carNum.length() == 0 || carNum.length() > 3) {
			return false;
		} else if(carNum.contains(" ")){
			return false;
		}
		return true;
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
	
	public static Boolean checkPassport(String passport){
		if (passport == null|| passport.length() == 0) {
			return false;
		} 
		  try{            
	            Pattern pattern = Pattern.compile("^[0-9A-Za-z]+$");
	            Matcher matcher = pattern.matcher(passport);
	            return matcher.matches();
	        } 
	        catch(Exception e){
	            e.printStackTrace();
	        }
	        return false; 
	}
	
	public static Boolean checkName(String name){
		if (name == null|| name.length() <2) {
			return false;
		} 
		  try{            
	            Pattern pattern = Pattern.compile("^[\u4E00-\u9FA5A-Za-z]+$");
	            Matcher matcher = pattern.matcher(name);
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
	
	
	public static void Alert(Context context, String content){
		int mId = 0x12345678;
		NotificationCompat.Builder mBuilder =
		        new NotificationCompat.Builder(context)
		        .setSmallIcon(R.drawable.ic_launcher)
		        .setContentTitle("微流量")
		        .setContentText(content)
		        .setAutoCancel(true);
		// Creates an explicit intent for an Activity in your app
		Intent resultIntent = new Intent(context, MainActivity.class);

		// The stack builder object will contain an artificial back stack for the
		// started Activity.
		// This ensures that navigating backward from the Activity leads out of
		// your application to the Home screen.
		TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
		
		// Adds the back stack for the Intent (but not the Intent itself)
		stackBuilder.addParentStack(MainActivity.class);
		
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
	
	
	
	public static void Dialog(Context context, boolean hasCancel,
			boolean isCancelable, String title, String msg,
			DialogInterface.OnClickListener listenerPositive) {
		Dialog(context, hasCancel, isCancelable, false, title, msg, "确定", "取消",
				listenerPositive, null, false, null);
	}
	
	/*
	 * 按钮默认为“确定”，“取消”，取消没有监听
	 */
	public static void Dialog(Context context, boolean hasCancel, String title,
			String msg, DialogInterface.OnClickListener listenerPositive) {
		Dialog(context, hasCancel, title, msg, "确定", "取消", listenerPositive,
				null);
	}
	
	/*
	 * 只有确认按钮，须设置文字，点击消失
	 */
	public static void Dialog(Context context, String title, String msg, String ok){
		Dialog(context, false, title, msg, ok, null, null, null);
	}
	
	/*
	 * 只有确认按钮，须设置文字，点击消失，有点击事件监听
	 */
	public static void Dialog(Context context, String title, String msg, String ok, DialogInterface.OnClickListener listener){
		Dialog(context, false, title, msg, ok, null, listener, null);
	}
	
	
	/*
	 * 按钮默认为“确定”，“取消”，取消有监听
	 */
	public static void Dialog(Context context, boolean hasCancel, String title,
			String msg, DialogInterface.OnClickListener listenerPositive,
			DialogInterface.OnClickListener listenerNegative) {
		Dialog(context, hasCancel, title, msg, "确定", "取消", listenerPositive,
				listenerNegative);
	}
	
	public static void Dialog(Context context, boolean hasCancel, String title,
			String msg, String ok, String cancel,
			DialogInterface.OnClickListener listenerPositive,
			DialogInterface.OnClickListener listenerNegative) {
		Dialog(context, hasCancel, true, false, title, msg, ok, cancel,
				listenerPositive, listenerNegative, false, null);
	}

	/*
	 * 警告用对话框
	 */
	public static Xdialog Dialog(final Context context, final boolean hasCancel,
			final boolean isCancelable, boolean nodismiss, final String title, final String msg, final String ok,
			final String cancel, final DialogInterface.OnClickListener listenerPositive,
			final DialogInterface.OnClickListener listenerNegative, final boolean showIcon,
			final String info) {
//		dismissDialog();

		// TODO Auto-generated method stub
		try{
			if(hasCancel){
				x_dialog = new Xdialog(context)
				.setTitle(title)
				.setMessage(msg)
				.showIcon(showIcon)
				.setNoDismiss(nodismiss)
				.setCancelable(isCancelable)
				.setInfo(info)
				.setPositiveButton(ok, listenerPositive)
				.setNegativeButton(cancel, listenerNegative)
				.createX();

			}else{
				x_dialog = new Xdialog(context)
				.setTitle(title)
				.setMessage(msg)
				.showIcon(showIcon)
				.setNoDismiss(nodismiss)
				.setCancelable(isCancelable)
				.setInfo(info)
				.setPositiveButton(ok, listenerPositive)
				.createX();
			}
			
//			x_dialog.alertDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
//			x_dialog.alertDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ERROR);
			
			x_dialog.setCanceledOnTouchOutside(isCancelable);
			try {

				Field field = x_dialog.alertDialog.getClass().getDeclaredField("mAlert");
				field.setAccessible(true);
				// 获得mAlert变量的值
				Object obj = field.get(x_dialog.alertDialog);
				field = obj.getClass().getDeclaredField("mHandler");
				field.setAccessible(true);
				// 修改mHandler变量的值，使用新的ButtonHandler类
				field.set(obj, new ButtonHandler(x_dialog.alertDialog));
			} catch (Exception e) {
			}
			
			x_dialog.show();
			
		}catch(Exception e){
			e.printStackTrace();
		}
	
		


		return x_dialog;
	}
	
	public static void dismissDialog() {

		// TODO Auto-generated method stub
		if(x_dialog!=null){
			try{
				x_dialog.dismiss();
			}catch(Exception e){
				e.printStackTrace();
			}
		}
	
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
