package com.etoc.weflow.version;


import com.google.gson.Gson;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;

public class CheckUpdate {

	private CheckUpdate() {
	}
	
	private static CheckUpdate instance = null;
	
	public static CheckUpdate getInstance(Activity actv){
		mactivity = actv;
		if(instance == null){
			instance = new CheckUpdate();
		}
		return instance;
	}
	
	
	public static final String REQUEST_HEADER = "requestapp";
	public static final String APPCODE = "16";
	
	
	private static Activity mactivity;
	private boolean force_upgrade = false;
	
	CheckUpdateTask task;
	
	public void update(){
		
		if (task==null ||
	    		task.getStatus().equals(AsyncTask.Status.FINISHED)) {
	    	task = new CheckUpdateTask();
	    	task.execute("update");
	    }else{
	    	Log.e("UpdateService", "last task still running");
	    }
	}
	
	
	
	private class CheckUpdateTask extends AsyncTask<String,Void,String>{

		@Override
		protected String doInBackground(String... params) {
			
			Gson gson = new Gson();
			
			try{

				/*VersionCheckRequest vcr = new VersionCheckRequest();
				vcr.imei = VMobileInfo.getIMEI();
				vcr.productcode = MetaUtil.getStringValue(WeFlowApplication.getInstance(), CwifiConstant.CMMOBI_APPKEY);
				vcr.system = "101";
				vcr.version = AppInfo.getVersion();
				vcr.channelcode = MetaUtil.getStringValue(WeFlowApplication.getInstance(), CwifiConstant.CMMOBI_CHANNEL);
				
				HashMap<String, String> map = new HashMap<String, String>();
				map.put(REQUEST_HEADER, gson.toJson(vcr));
				
				String string  = HttpClientUtility.getResponseStr(mactivity, HostConst.versionCheckUrl, map,HttpConst.POST, -1, -1);
				AppLogger.e("<< Response (VersionCheckResponse): " + string);*/
				
				return "";//string;
			}catch(Exception e){
				e.printStackTrace();
			}
			
			return "";
		}
		
		protected void onPostExecute(String result) {  

			/*if(TextUtils.isEmpty(result)){
				return;
			}
			
			VersionCheckResponse versionBean = null;
			try {
				versionBean = new Gson().fromJson(result, VersionCheckResponse.class);
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			if(versionBean == null){
				return;
			}
			final String path = versionBean.path;
			
			if("0".equals(versionBean.type) ){
				return;
			}
			
			if("1".equals(versionBean.type)){
				force_upgrade = true;
			}
			
			versionBean.versionnumber = versionBean.versionnumber.replaceAll("_", ".");
			
			String str = "发现新版本"+versionBean.versionnumber+"，请更新"+ (force_upgrade?"\n若不更新将无法继续使用":"");
			if(TextUtils.isEmpty(versionBean.description)){
				versionBean.description = str;
			}
			Builder builder = new AlertDialog.Builder(mactivity);
			builder.setTitle("系统更新").setMessage(versionBean.description);// 设置内容
		    builder.setPositiveButton("确定",// 设置确定按钮
		    		new DialogInterface.OnClickListener() {
		                @Override
		                public void onClick(DialogInterface dialog,int which) {
		                	dialog.dismiss();
		                	
		                	// 网页下载
		                	Intent intent = new Intent();        
		                	intent.setAction(Intent.ACTION_VIEW);    
		                	intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); 
		                	Uri content_url = Uri.parse(path);   
		                	intent.setData(content_url);           
		                	mactivity.startActivity(intent);
		                	
		                	
//		                	UpdateVerService down = new UpdateVerService(versionBean.path, mactivity);
//		                	down.startDownLoad();
		                	
		                	if(force_upgrade){
		        				mactivity.finish(); //强制更新就让他推出下载更�?
		                	}
		                }
		            }).setOnCancelListener(new OnCancelListener() {
						
						@Override
						public void onCancel(DialogInterface dialog) {
							if(force_upgrade){
		        				mactivity.finish(); //强制更新就让他推出下载更�?
		                	}
						}
					});
    		if(!force_upgrade){
    			builder.setNegativeButton("取消",   
    			            new DialogInterface.OnClickListener() {  
    			                public void onClick(DialogInterface dialog,
    			                        int whichButton) {
    			                	dialog.dismiss();
    			                	if(force_upgrade){
    			        				mactivity.finish();
    			                	}
    			                }
    			            });// 创建
    		}
//    		builder.show();
    		AlertDialog alertdialog = builder.create();
    		alertdialog.setCanceledOnTouchOutside(false);
    		alertdialog.show();*/
		}  
		
	}
	
	
}
