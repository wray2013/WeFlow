package com.cmmobi.looklook;


import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.Thread.UncaughtExceptionHandler;
import java.lang.reflect.Field;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Environment;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.cmmobi.looklook.info.profile.AccountInfo;
import com.cmmobi.looklook.info.profile.ActiveAccount;
import com.cmmobi.looklook.info.profile.TimeHelper;
 
public class CrashHandler implements UncaughtExceptionHandler {  
     
   public static final String TAG = "CrashHandler";  
     
   private Thread.UncaughtExceptionHandler mDefaultHandler;  

   private static CrashHandler INSTANCE = new CrashHandler();  

   private Context mContext;  
   
   //device info and crash stack
   private Map<String, String> infos = new HashMap<String, String>();  
 
   private DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");  
 
     
   private CrashHandler() {  
   }  
 
     
   public static CrashHandler getInstance() {  
       return INSTANCE;  
   }  
 
     
   public void init(Context context) {  
       mContext = context;  

       mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();  

       Thread.setDefaultUncaughtExceptionHandler(this);  
   }  
 
     
   @Override  
   public void uncaughtException(Thread thread, Throwable ex) {  
       if (!handleException(ex) && mDefaultHandler != null) {  
           
           mDefaultHandler.uncaughtException(thread, ex);  
       } else {  
           try {  
               Thread.sleep(3000);  
           } catch (InterruptedException e) {  
               Log.e(TAG, "error : ", e);  
           }  
           
           android.os.Process.killProcess(android.os.Process.myPid());  
           System.exit(0);  
       }  
   }  
 
   
   public static boolean isNetworkAvailable(Context context) {  
       ConnectivityManager mgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);  
       NetworkInfo[] info = mgr.getAllNetworkInfo();  
       if (info != null) {  
           for (int i = 0; i < info.length; i++) {  
               if (info[i].getState() == NetworkInfo.State.CONNECTED) {  
                   return true;  
               }  
           }  
       }
       return false;  
   } 
     
   private boolean handleException(Throwable ex) {
       if (ex == null) {  
           return false;  
       }
	   ex.printStackTrace();
       new Thread() {  
           @Override  
           public void run() {  
               Looper.prepare();  
               try {

                   Toast.makeText(mContext, "sorry, app crashed and logged into /sdcard/crash", Toast.LENGTH_LONG).show();  
                   Looper.loop(); 
				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
				} 
           }  
       }.start();  

       collectDeviceInfo(mContext);  
       
       saveCrashInfo2File(ex); 
       
       String UID = ActiveAccount.getInstance(MainApplication.getAppInstance()).getUID();
       if(UID!=null){
		  AccountInfo.getInstance(UID).persist();
	   }
       
/*       //save into sdcard
       Resources res = mContext.getResources(); 
       if(res.getBoolean(R.bool.crashlog_dump)){
    	   saveCrashInfo2File(ex);     
       }

       //post to server
       if(res.getBoolean(R.bool.crashlog_post) && isNetworkAvailable(mContext)){
    	   postCrash2server(ex);
       }*/
       
       //finish activities
       MainApplication.getAppInstance().cleanAllActivity();
       
       return true;  
   }  
     
     
   public void collectDeviceInfo(Context ctx) {  
       try {  
           PackageManager pm = ctx.getPackageManager();  
           PackageInfo pi = pm.getPackageInfo(ctx.getPackageName(), PackageManager.GET_ACTIVITIES);  
           if (pi != null) {  
               String versionName = pi.versionName == null ? "null" : pi.versionName;  
               String versionCode = pi.versionCode + "";  
               infos.put("versionName", versionName);  
               infos.put("versionCode", versionCode);  
           }  
       } catch (NameNotFoundException e) {  
           Log.e(TAG, "an error occured when collect package info", e);  
       }  
       Field[] fields = Build.class.getDeclaredFields();  
       for (Field field : fields) {  
           try {  
               field.setAccessible(true);  
               infos.put(field.getName(), field.get(null).toString());  
               Log.d(TAG, field.getName() + " : " + field.get(null));  
           } catch (Exception e) {  
               Log.e(TAG, "an error occured when collect crash info", e);  
           }  
       }  
   }  
   
   private String postCrash2server(Throwable ex){
       StringBuffer sb = new StringBuffer();  
       
       Writer writer = new StringWriter();  
       PrintWriter printWriter = new PrintWriter(writer);  
       ex.printStackTrace(printWriter);  
       Throwable cause = ex.getCause();  
       while (cause != null) {  
           cause.printStackTrace(printWriter);  
           cause = cause.getCause();  
       }  
       printWriter.close();  
       String result = writer.toString();  
       sb.append(result);  
       
       try{
    	  // ResponseWrapper resp = HttpRequestClient.performCrashlog(infos, sb.toString(), new Handler(), "");
/*    	   
    	   if(resp!=null && (0!=resp.getStatus())){
    		   Log.e(TAG, "an error occured while server  returned ...");  
    	   }*/
       }catch(Exception e){
    	   Log.e(TAG, "an error occured while post to server ...", e);  
       }
       
       return null;  
   }
 
     
   private String saveCrashInfo2File(Throwable ex) {  
         
       StringBuffer sb = new StringBuffer();  
       for (Map.Entry<String, String> entry : infos.entrySet()) {  
           String key = entry.getKey();  
           String value = entry.getValue();  
           sb.append(key + "=" + value + "\n");  
       }  
         
       Writer writer = new StringWriter();  
       PrintWriter printWriter = new PrintWriter(writer);  
       ex.printStackTrace(printWriter);  
       Throwable cause = ex.getCause();  
       while (cause != null) {  
           cause.printStackTrace(printWriter);  
           cause = cause.getCause();  
       }  
       printWriter.close();  
       String result = writer.toString();  
       sb.append(result);  
       try {  
           long timestamp = TimeHelper.getInstance().now();  
           String time = formatter.format(new Date());  
           String fileName = "crash-" + time + "-" + timestamp + ".log";  
           if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {  
               String path = "/sdcard/crash/";  
               File dir = new File(path);  
               if (!dir.exists()) {  
                   dir.mkdirs();  
               }  
               FileOutputStream fos = new FileOutputStream(path + fileName);  
               fos.write(sb.toString().getBytes());  
               fos.close();  
           }  
           return fileName;  
       } catch (Exception e) {  
           Log.e(TAG, "an error occured while writing file...", e);  
       }  
       return null;  
   }  
} 

