package com.cmmobi.railwifi.network;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.cmmobi.common.tools.MetaUtil;
import com.cmmobi.railwifi.Config;
import com.cmmobi.railwifi.MainApplication;
import com.cmmobi.railwifi.event.RequestEvent;
import com.cmmobi.railwifi.network.CRM_Object.versionCheckRequest;
import com.cmmobi.railwifi.utils.SimCardInfoUtils;
import com.cmmobi.railwifi.utils.StringUtils;
import com.google.gson.Gson;

import de.greenrobot.event.EventBus;

public class CRMRequester {

	public static final String REQUEST_HEADER = "requestapp=";

	
	public static final String APP_INTERFACE_VERSION_CHECK = "/updateInterface/versionJson.do";

	// =========================================================================================
	
	public static final int RESPONSE_TYPE_VERSION_CHECK = 0xfe100002;
	
	public static final int RESPONSE_TYPE_APK_URL = 0xfe100003;

	public static final String TAG = "CRMRequester";

	// =========================================================================================
	
	// 2. check版本
	public static void checkVersion(Handler handler) {
		versionCheckRequest request = new versionCheckRequest();
		request.channelcode = MetaUtil.getStringValue(MainApplication.getAppInstance(), "CMMOBI_CHANNEL");
		request.imei = StringUtils.nullToEmpty(SimCardInfoUtils.getIMEI());
		request.productcode = String.valueOf(MetaUtil.getIntValue(MainApplication.getAppInstance(), "CMMOBI_APPKEY"));
		request.system = "101";
        PackageManager manager = MainApplication.getAppInstance().getPackageManager();
        String packagename = MainApplication.getAppInstance().getPackageName();
        PackageInfo info;
		try {
			info = manager.getPackageInfo(packagename, 0);
	        String version = info.versionName;
	        version.replace('.', '_');
			request.version = version.replace('.', '_');
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


		PostReqWorker worker = new PostReqWorker(handler,
				RESPONSE_TYPE_VERSION_CHECK,
				CRM_Object.versionCheckResponse.class);
		worker.execute(APP_INTERFACE_VERSION_CHECK, request);
	}
	
	
	// 3. 获取最终的url
	public static void getApkUrl(Handler handler, String req_url ) {
		GetReqWork worker = new GetReqWork(handler,  RESPONSE_TYPE_APK_URL, String.class);
		worker.execute(req_url);
	}



	/**
	 * ------------------------------------------------------------------------
	 * 内部类, 用来获取推荐应用列表;
	 * 
	 */
	public static class PostReqWorker extends Thread {
		private static final String TAG = "PostReqWorker";
		private Handler handler;
		private Class<?> cls;
		private Gson gson;
		private int responseType;
		private String ria_command_id;
		private Object request;

		/**
		 * 
		 * @param handler
		 * @param responseType: 响应类型;
		 * @param cls: 响应对象的class类型;
		 */
		public PostReqWorker(Handler handler, int responseType, Class<?> cls) {
			this.handler = handler;
			this.responseType = responseType;
			this.cls = cls;
			this.gson = new Gson();
		}

		public void execute(String ria_command_id, Object request) {
			// TODO Auto-generated method stub
			this.ria_command_id = ria_command_id;
			this.request = request;
			this.start();
			
		}
		

		@Override
		public void run(){
			EventBus.getDefault().post(RequestEvent.LOADING_START);
			String url = Config.SERVER_CRM_URL + ria_command_id;
			Log.v(TAG, "PostReqWorker url--->" + url);
			
		    HttpPost httpPostRequest = new HttpPost(url);
		    DefaultHttpClient httpClient = new DefaultHttpClient();
		    ArrayList<NameValuePair> parameters = new ArrayList<NameValuePair>();

		    String json = gson.toJson(request);
		    
		    parameters.add(new BasicNameValuePair("requestapp", json));
			
		    String ret_entity_str = null;
		    
		    try {
				httpPostRequest.setEntity(new UrlEncodedFormEntity(parameters, HTTP.UTF_8));
//				httpPostRequest.setEntity(new StringEntity("requestapp=" + json, HTTP.UTF_8));
				HttpResponse localHttpResponse = httpClient.execute(httpPostRequest);
			    
				if(localHttpResponse!=null){
				    ret_entity_str = EntityUtils.toString(localHttpResponse.getEntity());
				    Log.v(TAG, "ret_entity_str:" + ret_entity_str);
				}

			} catch (UnsupportedEncodingException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		    Object object = null;
		    if(ret_entity_str!=null){
		    	try{
		    		object = gson.fromJson(ret_entity_str, cls);
		    	}catch(Exception e){
		    		e.printStackTrace();
		    	}
		    }else{

		    	EventBus.getDefault().post(RequestEvent.RESP_NULL);

		    }

			if (handler != null) {
				if(object!=null){
					Message message = handler.obtainMessage(responseType, object);
					try {
						handler.sendMessage(message);
					} catch (Exception e) {
						Log.e(TAG, "Perhaps sending message to a Handler on a dead thread - ria_command_id:" + ria_command_id);
					}
				}else{
					Log.e(TAG, "object is null - ria_command_id:" + ria_command_id);
				}
				

			} else {
				Log.e(TAG, "handler is null, data can not callback - ria_command_id:" + ria_command_id);
			}
			
		    EventBus.getDefault().post(RequestEvent.LOADING_END);
			
		}

	}
	
	
	/**
	 * ------------------------------------------------------------------------
	 * 内部类, 用来与RIA交换数据;
	 * 
	 * @author Sunshine
	 */
	public static class GetReqWork extends Thread {
		private Handler handler;
		private Class<?> cls;
		private Gson gson;
		private int responseType;
		private String requestUrl;

		/**
		 * 
		 * @param handler
		 * @param responseType: 响应类型;
		 * @param cls: 响应对象的class类型;
		 */
		public GetReqWork(Handler handler, int responseType, Class<?> cls) {
			this.handler = handler;
			this.responseType = responseType;
			this.cls = cls;
			this.gson = new Gson();
		}

		public void execute(String requestUrl) {
			// TODO Auto-generated method stub
			this.requestUrl = requestUrl;
			this.start();
			
		}

		@Override
		public void run(){

			Log.v(TAG, "GetReqWork url--->" + requestUrl);
			
			HttpGet httpGetRequest = new HttpGet(requestUrl);
		    DefaultHttpClient httpClient = new DefaultHttpClient();
			
		    String ret_entity_str = null;
		    
		    try {
				HttpResponse localHttpResponse = httpClient.execute(httpGetRequest);
			    
				if(localHttpResponse!=null){
				    ret_entity_str = EntityUtils.toString(localHttpResponse.getEntity());
				    Log.v(TAG, "ret_entity_str:" + ret_entity_str);
				}

			} catch (UnsupportedEncodingException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		    Object object = null;
		    if(ret_entity_str!=null){
				object = gson.fromJson(ret_entity_str, cls);
		    }

			
			if (handler != null) {
				if(object!=null){
					Message message = handler.obtainMessage(responseType, object);
					try {
						handler.sendMessage(message);
					} catch (Exception e) {
						Log.e(TAG, "Perhaps sending message to a Handler on a dead thread - requestUrl:" + requestUrl);
					}
				}else{
					Log.e(TAG, "object is null - requestUrl:" + requestUrl);
				}
				

			} else {
				Log.e(TAG, "handler is null, data can not callback - requestUrl:" + requestUrl);
			}
			
		}


	}


}
