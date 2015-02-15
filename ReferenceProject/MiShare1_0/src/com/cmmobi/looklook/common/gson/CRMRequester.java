package com.cmmobi.looklook.common.gson;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import cn.zipper.framwork.core.ZLog;
import cn.zipper.framwork.device.ZSimCardInfo;
import cn.zipper.framwork.io.network.ZHttp2;
import cn.zipper.framwork.io.network.ZHttpReader;
import cn.zipper.framwork.io.network.ZHttpResponse;
import cn.zipper.framwork.utils.ZStringUtils;

import com.cmmobi.looklook.Config;
import com.cmmobi.looklook.MainApplication;
import com.cmmobi.looklook.common.gson.CRM_Object.versionCheckRequest;
import com.cmmobi.looklook.common.utils.MetaUtil;
import com.google.gson.Gson;

public class CRMRequester {

	public static final String REQUEST_HEADER = "requestapp=";

	public static final String APP_INTERFACE_GET_RECOMMEND = "/recommendInterface/getRecommend.do";
	
	public static final String APP_INTERFACE_VERSION_CHECK = "/updateInterface/versionJson.do";

	// =========================================================================================
	// 1
	// 2
	public static final int RESPONSE_TYPE_GET_RECOMMEND = 0xfe100001;
	
	public static final int RESPONSE_TYPE_VERSION_CHECK = 0xfe100002;
	
	public static final int RESPONSE_TYPE_APK_URL = 0xfe100003;

	public static final String TAG = "CRMRequester";

	// =========================================================================================

	// 1. 移动终端软件获取推荐应用列表
	public static void getRecommend(Handler handler, String systemcode,
			String productcode) {
		CRM_Object.getRecommendRequest request = new CRM_Object.getRecommendRequest();
		request.systemcode = systemcode;
		request.productcode = productcode;

		Worker worker = new Worker(handler,
				RESPONSE_TYPE_GET_RECOMMEND,
				CRM_Object.getRecommendResponse.class);
		worker.execute(APP_INTERFACE_GET_RECOMMEND, request);
	}
	
	// 2. check版本
	public static void checkVersion(Handler handler) {
		versionCheckRequest request = new versionCheckRequest();
		request.channelcode = MetaUtil.getStringValue("CMMOBI_CHANNEL");
		request.imei = ZStringUtils.nullToEmpty(ZSimCardInfo.getIMEI());
		request.productcode = String.valueOf(MetaUtil.getIntValue("CMMOBI_APPKEY"));
		request.system = "101";
        PackageManager manager = MainApplication.getInstance().getPackageManager();
        String packagename = MainApplication.getInstance().getPackageName();
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


		Worker worker = new Worker(handler,
				RESPONSE_TYPE_VERSION_CHECK,
				CRM_Object.versionCheckResponse.class);
		worker.execute(APP_INTERFACE_VERSION_CHECK, request);
	}
	
	
	// 3. 获取最终的url
	public static void getApkUrl(Handler handler, String req_url ) {
		GetUrlWork worker = new GetUrlWork(handler,  RESPONSE_TYPE_APK_URL, String.class);
		worker.execute(req_url);
	}



	/**
	 * ------------------------------------------------------------------------
	 * 内部类, 用来获取推荐应用列表;
	 * 
	 */
	private static class Worker extends AsyncTask<Object, Void, Object> {
		private Handler handler;
		private Class<?> cls;
		private Gson gson;
		private int responseType;

		/**
		 * 
		 * @param handler
		 * @param responseType : 响应类型;
		 * @param cls : 响应对象的class类型;
		 */
		public Worker(Handler handler, int responseType, Class<?> cls) {
			this.handler = handler;
			this.responseType = responseType;
			this.cls = cls;
			this.gson = new Gson();
		}

		@Override
		protected Object doInBackground(Object... params) {
			Object object = null;
			ZHttp2 http2 = null;
			ZHttpReader reader = null;

			try {
				String url = Config.SERVER_URL_CRM + (String) params[0];

				String json = REQUEST_HEADER + gson.toJson(params[1]);

				ZLog.e(">> Request2 ("
						+ params[1].getClass().getSimpleName() + "): "
						+ json);

				http2 = new ZHttp2();
				ZHttpResponse httpResponse = http2.post(url,
						json.getBytes());
				reader = new ZHttpReader(httpResponse.getInputStream(),
						null);
				String string = new String(reader.readAll(0));
				object = gson.fromJson(string, cls);
				ZLog.e("<< Response2 (" + cls.getSimpleName() + "): "
						+ string);
				/*
				 * if (object != null) { decodeBase64(responseType, object);
				 * saveData(responseType, object); }
				 */
			} catch (Exception e) {
				e.printStackTrace();

			} finally {
				if (http2 != null) {
					http2.close();
				}
				if (reader != null) {
					reader.close();
				}

			}
		

			return object;
		}

		@Override
		protected void onPostExecute(Object object) {
			super.onPostExecute(object);
			if (object != null) {
				if (handler != null) {
					Message message = handler.obtainMessage(responseType, object);
					handler.sendMessage(message);
				} else {
					ZLog.alert();
					ZLog.e("handler is null, data can not callback.");
				}
			}


		}

	}
	
	
	/**
	 * ------------------------------------------------------------------------
	 * 内部类, 用来与RIA交换数据;
	 * 
	 * @author Sunshine
	 */
	public static class GetUrlWork extends Thread {
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
		public GetUrlWork(Handler handler, int responseType, Class<?> cls) {
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

			Object object = null;
			ZHttp2 http2 = null;
			ZHttpReader reader = null;

			boolean b = false;

			try {
				String url = requestUrl;

				ZLog.e(">> Request2 (getApkUrl) :" + url);

				http2 = new ZHttp2();
				ZHttpResponse httpResponse = http2.get(url);
				reader = new ZHttpReader(httpResponse.getInputStream(), null);
				String string = new String(reader.readAll(0));
				
				object = string; //gson.fromJson(string, cls);
				ZLog.e("<< Response2 (" + cls.getSimpleName() + "): " + string);

			} catch (Exception e) {
				e.printStackTrace();
				b = true;

			} finally {
				if (http2 != null) {
					http2.close();
				}
				if (reader != null) {
					reader.close();
				}

			}

			if (handler != null) {
				Message message = handler.obtainMessage(responseType, object);
				handler.sendMessage(message);
			} else {
				ZLog.alert();
				ZLog.e("handler is null, data can not callback.");
			}
		
		
		}


	}


}
