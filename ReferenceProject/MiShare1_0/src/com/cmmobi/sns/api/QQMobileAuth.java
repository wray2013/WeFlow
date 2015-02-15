package com.cmmobi.sns.api;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Arrays;

import org.apache.http.conn.ConnectTimeoutException;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;
import cn.zipper.framwork.core.ZDialog;
import cn.zipper.framwork.io.network.ZHttp2;
import cn.zipper.framwork.io.network.ZHttpReader;
import cn.zipper.framwork.io.network.ZHttpResponse;

import com.cmmobi.looklook.R;
import com.cmmobi.sns.keep.AccessTokenKeeper;
import com.cmmobi.sns.oauthv2.OAuthV2;
import com.cmmobi.sns.utils.ConfigUtil.SHARE_TO;
import com.google.gson.Gson;
import com.tencent.open.HttpStatusException;
import com.tencent.open.NetworkUnavailableException;
import com.tencent.tauth.Constants;
import com.tencent.tauth.IRequestListener;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

public class QQMobileAuth implements Callback{

	
	// ==============instance========================
	
	private static QQMobileAuth instance = new QQMobileAuth();
	private QQMobileAuth() {
	}
	
	public static QQMobileAuth getInstance(Context ctx){
		if(context == null){
			context = ctx;
		}
		if(mTencent == null){
			mTencent = Tencent.createInstance(APP_ID, ctx);
		}
		if(mHandler == null){
			mHandler = new Handler(instance);
		}
		return instance;
	}
	
	// ==============instance========================
	
	
	private static final String TAG = "QQMobileAuth";
	
	public static final String APP_ID = "100476933";
	public static final String QQMOBILE_APPKEY = "07b7ac2ea9cdde78014fa872e0e0e8fd";
	
	private static Tencent mTencent;
	private static QQMobileAccInfo qqInfo;
	private String SCOPE = "all";
	
	private static Context context;
	private static Handler mHandler ;
	
	/**
	 * qq授权
	 * @param act
	 * @param listener
	 */
	public void authorize(Activity act ,QQMobleUiListener listener) {
		mTencent.logout(act);
		mTencent.login(act, SCOPE,listener);
	}
	
	/**
	 * 清除授权
	 * @param ctx
	 */
	public void cleanup(Context ctx){
		mTencent.logout(ctx);
	}
	
//	/**
//	 * qq授权页返回注册
//	 * @param requestCode
//	 * @param resultCode
//	 * @param data
//	 */
//	public void onActivityResult(int requestCode, int resultCode, Intent data) {
//		if(mTencent !=null){
//			mTencent.onActivityResult(requestCode, resultCode, data);
//		}
//	}
	

	/**
	 * 检查是否已经授权
	 * @return
	 */
	public boolean isQQAuthorized() {
		boolean ready = false;
		OAuthV2 mOauth = CmmobiSnsLib.getInstance().acquireQQMobileOauth();
		if(mOauth!=null){
			if(!mTencent.isSessionValid()){
				String OpenId = "";
				if(!TextUtils.isEmpty(mOauth.getOpenid())){
					OpenId = mOauth.getOpenid();
				}
				if(TextUtils.isEmpty(OpenId) && !TextUtils.isEmpty(mOauth.getOpenkey())){
					OpenId = mOauth.getOpenkey();
				}
				mTencent.setOpenId(OpenId);
				try {
					mTencent.setAccessToken(mOauth.getAccessToken(), mOauth.getExpiresIn());
				} catch (Exception e) {
				}
				
			}
			ready = mOauth.isSessionValid();
//			ready = mTencent.isSessionValid()
//                && mTencent.getOpenId() != null;
		}
        return ready;
    }
	
	
	public void shareTofeeds(final Activity act, String title , String summary,String url,String... imgUrls) {
//        if (isQQAuthorized()) {
        	
            Bundle params = new Bundle();
            
            params.putString(Tencent.SHARE_TO_QQ_TITLE, title);
            params.putString(Tencent.SHARE_TO_QQ_SUMMARY, summary);
            params.putString(Tencent.SHARE_TO_QQ_TARGET_URL, url);
            
            // 这个参数必须要传递.
            ArrayList<String> arr = new ArrayList<String>();
            if(imgUrls!=null&& imgUrls.length>0){
            	arr.addAll(Arrays.asList(imgUrls));
            }
            params.putStringArrayList(Tencent.SHARE_TO_QQ_IMAGE_URL, arr);
            
            
            mTencent.shareToQzone(act, params, new IUiListener(){
				@Override
				public void onCancel() {
					Log.e(TAG, "shareToQzone qqmobile onCancel");
				}
				@Override
				public void onComplete(JSONObject arg0) {
					Log.e(TAG, "shareToQzone qqmobile onComplete = " + arg0.toString());
				}
				@Override
				public void onError(UiError arg0) {
					Log.e(TAG, "shareToQzone qqmobile onError" + arg0.errorMessage);
				}
            });
            
//        }else{
//        	
//        	authorize(act, new QQMobleUiListener() {
//				@Override
//				public void qqMobleComplete() {
//					shareTofeeds(act, title, url);
//				}
//			});
//        }
    }
	
	/**
	 * 不支持本地图片分享
	 */
	@Deprecated
	public void shareTofeedsLocalImg(final Activity act, final String title ,final String summary,final String url, final String... imgUrls) {
		
		ZDialog.show(R.layout.progressdialog, false, true, act);
		
		if(imgUrls!=null && imgUrls.length>0){
			
			doSyncUploadPic(imgUrls[0], true);
			
		}else{
			shareTofeeds(act, title, summary, url, imgUrls);
		}
		
		
	}
	
	/**
	 * 同步方法完成图片上传
	 * @param filePath
	 * @param needFeed
	 * @return
	 */
	private void doSyncUploadPic(String filePath , boolean needFeed) {
		Bundle params = new Bundle();

		byte[] buff = null;
		try {
			InputStream is = new FileInputStream(filePath);
			ByteArrayOutputStream outSteam = new ByteArrayOutputStream();
			byte[] buffer = new byte[1024];
			int len = 0;
			while ((len = is.read(buffer)) != -1) {
				outSteam.write(buffer, 0, len);
			}
			outSteam.close();
			is.close();
			buff = outSteam.toByteArray();
		} catch (IOException e) {
			e.printStackTrace();
		}
		params.putByteArray("picture", buff);// 必须.上传照片的文件名以及图片的内容（在发送请求时，图片内容以二进制数据流的形式发送，
		params.putString("title","" + new File(filePath).getName());// 照片的命名，必须以.jpg, .gif,.png,.jpeg,.bmp。
		params.putString("mobile", "1");   //   手机相册
		params.putString("format", "json");//   手机相册
		if(needFeed){
			params.putString("needfeed", "1");// 0：不发feed； 1：发feed
		}else{
			params.putString("needfeed", "0");// 0：不发feed； 1：发feed
		}
//		params.putString("photodesc", "QQ登陆SDK：UploadPic测试" + new Date());// 照片描述，注意照片描述不能超过200个字符。
		
		mTencent.requestAsync(Constants.GRAPH_UPLOAD_PIC, params,
				Constants.HTTP_POST,
				new BaseApiListener("upload_pic", true), null);
		
	}
	
	
	/**
	 * 获取用户信息
	 * @param handler
	 * @param snsid
	 * @param cmd
	 */
	public static void getQQMobileAccInfo(final Handler handler, String snsid,final int cmd) {
//		final GsonResponse3.MyBind bind = AccessTokenKeeper.tmp;
		final OAuthV2 bind = CmmobiSnsLib.getInstance().acquireQQMobileOauth();
		
//		if(qqInfo != null){
//			Message message = handler.obtainMessage(cmd, qqInfo);
//			handler.sendMessage(message);
//		}else{
			new Thread(){
				public void run() {
					
					String Url = "https://graph.qq.com/user/get_user_info";
					
					String param =  "access_token="        +bind.getAccessToken()
								  + "&oauth_consumer_key=" +APP_ID
								  + "&openid="             +bind.getOpenid();
					
					String result = "";
					try {
						
						ZHttp2 http2 = new ZHttp2();
						ZHttpResponse httpResponse = http2.post(Url, param.getBytes());
						ZHttpReader reader = new ZHttpReader(httpResponse.getInputStream(), null);
						result = new String(reader.readAll(0));
						
					} catch (Exception e) {
						e.printStackTrace();
					}
					Gson gson = new Gson();
					QQMobileAccInfo accInfo = gson.fromJson(result, QQMobileAccInfo.class);
					if(accInfo !=null){
						qqInfo = accInfo;
					}
					Message message = handler.obtainMessage(cmd, accInfo);
					handler.sendMessage(message);
				}
			}.start();
//		}
	}
	
//	/**
//	 * 获取登陆成功的授权信息
//	 * @return
//	 */
//	public OAuthV2 acquireQQMobileOauth(){
//		if(ready()){
//			return CmmobiSnsLib.getInstance().acquireOauth(SHARE_TO.QQMOBILE.ordinal());
//		}
//		return null;
//	}
	
	
	/**
	 * 登陆时的回调
	 */
	public static abstract class QQMobleUiListener implements IUiListener {

		public abstract void qqMobleComplete();
		
        @Override
        public void onComplete(JSONObject response) {
        	
        	if(response == null){
        		throw new RuntimeException();
        	}
        	
        	String access_token = response.optString("access_token");
        	String openid = response.optString("openid");
        	String expires_in = response.optString("expires_in");
        	
        	OAuthV2 mOauth = new OAuthV2();
        	mOauth.setClientId(APP_ID);
        	mOauth.setAccessToken(access_token);
        	mOauth.setOpenid(openid);
        	mOauth.setOpenkey(openid);
//        	mOauth.setRefreshToken(access_token); // qq没有这个值
        	mOauth.setExpiresIn(expires_in);
	        mOauth.setStatus(0); 
	        mOauth.setCurWeiboIndex(SHARE_TO.QQMOBILE.ordinal());
        	
	        AccessTokenKeeper.keepAccessToken(context, mOauth);
			
	        //授权成功后,将Oauth实例放到一个全局的Hashmap中，方便，分享时候取出
			CmmobiSnsLib.getInstance().updateOAuthV2Map(SHARE_TO.QQMOBILE.ordinal(), mOauth);
        	
			Log.d(TAG, "qqmobile onComplete");
			
			qqMobleComplete();
        }
        

        @Override
        public void onError(UiError e) {
        	Log.d(TAG, "qqmobile onError");
        }
        @Override
        public void onCancel() {
        	Log.d(TAG, "qqmobile onCancel");
        }
    }
	
	
	public class QQMobileAccInfo {
		public String ret;
		public String msg;
		public String nickname;
		public String gender;
		public String figureurl;
		public String figureurl_1;
		public String figureurl_2;
		public String figureurl_qq_1;
		public String figureurl_qq_2;
		public String is_yellow_vip;
		public String vip;
		public String yellow_vip_level;
		public String level;
		public String is_yellow_year_vip;
	}
	
	
	
	
	private class BaseApiListener implements IRequestListener {
        private String mScope = "all";
        private Boolean mNeedReAuth = false;

        public BaseApiListener(String scope, boolean needReAuth) {
            mScope = scope;
            mNeedReAuth = needReAuth;
        }

        @Override
        public void onComplete(final JSONObject response, Object state) {
            showResult("IRequestListener.onComplete:", response.toString());
            
            try {
                int ret = response.getInt("ret");
                if(ret == 0 && mScope.equals("upload_pic")){
                	Message msg = new Message();
                	msg.what = 4;
                	msg.obj = response.toString();
                	mHandler.sendMessage(msg);
                } else if (ret == 100031){
                	Toast.makeText(context, "第三方应用没有对该api操作的权限,请发送邮件进行OpenAPI权限申请", 1).show();
                }
            } catch (JSONException e) {
                showResult("IRequestListener.onComplete:", response.toString());
            }
        }

        @Override
        public void onIOException(final IOException e, Object state) {
            showResult("IRequestListener.onIOException:", e.getMessage());
        }

        @Override
        public void onMalformedURLException(final MalformedURLException e,
                Object state) {
            showResult("IRequestListener.onMalformedURLException", e.toString());
        }

        @Override
        public void onJSONException(final JSONException e, Object state) {
            showResult("IRequestListener.onJSONException:", e.getMessage());
        }

        @Override
        public void onConnectTimeoutException(ConnectTimeoutException arg0,
                Object arg1) {
            showResult("IRequestListener.onConnectTimeoutException:", arg0.getMessage());

        }

        @Override
        public void onSocketTimeoutException(SocketTimeoutException arg0,
                Object arg1) {
            showResult("IRequestListener.SocketTimeoutException:", arg0.getMessage());
        }

        @Override
        public void onUnknowException(Exception arg0, Object arg1) {
            showResult("IRequestListener.onUnknowException:", arg0.getMessage());
        }

        @Override
        public void onHttpStatusException(HttpStatusException arg0, Object arg1) {
            showResult("IRequestListener.HttpStatusException:", arg0.getMessage());
        }

        @Override
        public void onNetworkUnavailableException(NetworkUnavailableException arg0, Object arg1) {
            showResult("IRequestListener.onNetworkUnavailableException:", arg0.getMessage());
        }
    }

    private void showResult(final String base, final String msg) {
    	Log.d(TAG, "qqmobile :" +msg);
    	Toast.makeText(context, base + ":" + msg, 1).show();
    }

	@Override
	public boolean handleMessage(Message msg) {
		switch (msg.what) {
		case 4: // 上传成功
			
			break;
		default:
			break;
		}
		return false;
	}
	
}
