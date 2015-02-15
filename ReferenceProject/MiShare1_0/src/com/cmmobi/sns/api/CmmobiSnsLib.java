package com.cmmobi.sns.api;

import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import cn.zipper.framwork.core.ZApplication;
import cn.zipper.framwork.core.ZDialog;

import com.cmmobi.looklook.MainApplication;
import com.cmmobi.looklook.common.gson.GsonResponse3;
import com.cmmobi.looklook.info.profile.AccountInfo;
import com.cmmobi.looklook.info.profile.ActiveAccount;
import com.cmmobi.sns.constants.OAuthConstants;
import com.cmmobi.sns.exceptions.WeiboError;
import com.cmmobi.sns.keep.AccessTokenKeeper;
import com.cmmobi.sns.oauthv2.OAuthV2;
import com.cmmobi.sns.oauthv2.OAuthV2Client;
import com.cmmobi.sns.utils.ConfigUtil;
import com.cmmobi.sns.utils.ConfigUtil.SHARE_TO;
import com.cmmobi.sns.utils.JsonParseUtils;
import com.renn.rennsdk.RennClient;
import com.renn.rennsdk.RennClient.LoginListener;
import com.renren.weibo.api.RWCommentsAPI;
import com.renren.weibo.api.RWFriendshipsAPI;
import com.renren.weibo.api.RWStatusesAPI;
import com.renren.weibo.api.RWUsersAPI;
import com.sina.android.Weibo;
import com.sina.android.WeiboDialogError;
import com.sina.android.WeiboException;
import com.sina.android.sso.SsoHandler;
import com.sina.weibo.api.SWCommentsAPI;
import com.sina.weibo.api.SWFriendshipsAPI;
import com.sina.weibo.api.SWStatusesAPI;
import com.sina.weibo.api.SWUsersAPI;
import com.tencent.weibo.api.TWFriendsAPI;
import com.tencent.weibo.api.TWTAPI;
import com.tencent.weibo.api.TWUserAPI;
import com.tencent.weibo.sdk.android.component.sso.AuthHelper;
import com.tencent.weibo.sdk.android.component.sso.OnAuthListener;
import com.tencent.weibo.sdk.android.component.sso.WeiboToken;
import com.weibo.sdk.android.api.WeiboAuthListener;
import com.weibo.sdk.android.api.WeiboDialog;
/*import com.weibo.sdk.android.Oauth2AccessToken;
import com.weibo.sdk.android.Weibo;
import com.weibo.sdk.android.WeiboDialogError;
import com.weibo.sdk.android.WeiboException;*/
/*import com.weibo.sdk.android.sso.SsoHandler;*/
/**
 * 中传分享库（基于OAuth2.0协议实现）封装第三方开放平台(目前版本包括新浪微博，腾讯微博)分享api
 * 
 * @author xudongsheng
 * @author zhangwei
 *
 */
public class CmmobiSnsLib
{
	public static final String TAG = CmmobiSnsLib.class.getSimpleName();
	public static final String WEIBOUSERIDNULL = "WEIBOUSERIDNULL";
	private static CmmobiSnsLib mSnsLib = null;
	private Context mContext;
    private SparseArray<OAuthV2> oAuthV2Map;
    
	//新浪微博api
	private SWStatusesAPI mSWStatusApi = null;
	private SWFriendshipsAPI mSWFriendshipsApi = null;
	private SWCommentsAPI mSWCommentsApi = null;
	private SWUsersAPI mSWUsersApi = null;
	
	//腾讯微博api
	private TWTAPI mTWTapi;
	private TWUserAPI mTWUserApi;
	private TWFriendsAPI mTWFriendsApi;
	
	//人人微博api
	private RWStatusesAPI mRWStatusApi = null;
	private RWFriendshipsAPI mRWFriendshipsApi = null;
	private RWCommentsAPI mRWCommentsApi = null;
	private RWUsersAPI mRWUsersApi = null;
	
	//uid looklook定义的user id，区分不同的用户
	//private static String uid;
	
	
	public synchronized static CmmobiSnsLib getInstance() {
		if(mSnsLib!=null){
			return mSnsLib;
		}else{
			return getInstance(MainApplication.getAppInstance());
		}

	}
	
	public synchronized static CmmobiSnsLib getInstance(Context context) {
		//Log.e(TAG, "CmmobiSnsLib getInstance uid_arg:" + uid_arg + " this.uid:" + uid );
		if(mSnsLib == null ){
			mSnsLib = new CmmobiSnsLib(context);
		}/*else{
			if(uid.equals(uid_arg)){
				
			}else{
				mSnsLib.cleanup(context, uid_arg);
				mSnsLib = new CmmobiSnsLib(context, uid_arg);
			}
		}*/
		mSnsLib.SetContext(context);
		//mSnsLib.SetUid(uid_arg);
		
		return mSnsLib;
	}
	
	public SparseArray<OAuthV2> getOAuthV2Map(){
		return this.oAuthV2Map;
	}
	
	public void updateOAuthV2Map(int curWeiboIndex, OAuthV2 oAuth) {
		if(oAuth==null){
			Log.e(TAG, "updateOAuthV2Map - oAuth is null! return");
			return;
		}
		
		ActiveAccount aa = ActiveAccount.getInstance(mContext);
		
		if (oAuthV2Map == null) {
			Log.e(TAG, "updateOAuthV2Map - oAuthV2Map is null! read from accountInfo");
			oAuthV2Map = new SparseArray<OAuthV2>();

			if (aa.isLogin()) {
				// 将accountInfo中的logins同步到oAuthVMap
				int num = SHARE_TO.values().length;
				for (int i = 0; i < num; i++) {
					OAuthV2 oa = AccessTokenKeeper.readAccessToken(mContext, i);
					if (oa != null) {
						updateOAuthV2Map(i, oa);
					}

				}
			}
		}
		
		oAuthV2Map.put(curWeiboIndex, oAuth);
		
		if(aa.isLogin()) {  //将oAuthV2Map 同步到accountInfo
			AccountInfo.getInstance(aa.getLookLookID()).updateAccessToken(new GsonResponse3.MyBind(curWeiboIndex, oAuth));
		}
	}
	
	private CmmobiSnsLib(String uid_arg) {	
		this(ZApplication.getInstance());
	}
	
	private CmmobiSnsLib(Context context) {	
		//Log.e(TAG, "CmmobiSnsLib() uid_arg:" + uid_arg + " uid" + uid);
		this.mContext = context;
		//uid = uid_arg;
		
/*		if("tmp".equals(uid_arg)){
			return;
		}*/
		
		//load accessToken into Map:
		int num = SHARE_TO.values().length;
		for(int i=0; i<num; i++){
			//Log.e(TAG, "CmmobiSnsLib call readAccessToken, uid:" + uid_arg);
			OAuthV2 oa = AccessTokenKeeper.readAccessToken(context,  i);
			if(oa!=null){
				updateOAuthV2Map(i, oa);
			}
			
		}
		
	}
	
	private void SetContext(Context context) {	
		this.mContext = context;
	}
	
	private void SetUid(String uid) {	
		//this.uid = uid;
	}
	

	/**
	 * 进行微博认证
	 * @param context
	 * @param curWeiboIndex
	 * @param listener
	 */
	private void authorize( int curWeiboIndex,WeiboAuthListener listener) {
		startAuthDialog( curWeiboIndex, listener);
	}
	
	private void startAuthDialog(int curWeiboIndex, WeiboAuthListener listener) {	
	    String appKey = ConfigUtil.getInstance().getAppKey();
	    String appSecret = ConfigUtil.getInstance().getAppSecret();
	    String redirectUrl = ConfigUtil.getInstance().getRedirectUrl();
	    String authorizeUrl = ConfigUtil.getInstance().getAuthoriz_token_url();
	    OAuthV2 oAuth=new OAuthV2(redirectUrl);
        oAuth.setClientId(appKey);
        oAuth.setClientSecret(appSecret);
        oAuth.setAuthorizeUrl(authorizeUrl);
        oAuth.setCurWeiboIndex(curWeiboIndex);
        String urlStr = OAuthV2Client.generateImplicitGrantUrl(oAuth);
        Log.e(TAG, "urlStr:" + urlStr);
        /*如果用户在Dialog显示之前按了返回按钮,activity已经onDestory了，就会报出android.view.WindowManager$BadTokenException: Unable to add window --
        token android.os.BinderProxy@4479b390 is not valid; is your activity running?异常，在此处扑获
        异常，解决了此问题。*/        
        try{
             //CookieSyncManager.createInstance(mContext);
        	 new WeiboDialog(mContext,urlStr, oAuth, listener).show();
        }catch(Exception e){
        	
        } 
	}
	

/*****************************************************************************************************

    新浪微博分享api

*****************************************************************************************************/
	private Weibo mWeibo;
	public SsoHandler mSsoHandler;
	
    class AuthDialogListener implements  com.sina.android.WeiboAuthListener {

        @Override
        public void onComplete(Bundle values) {
            String token = values.getString("access_token");
            String expires_in = values.getString("expires_in");
            String uid = values.getString("uid");
            String remind_in = values.getString("remind_in");
            
            ConfigUtil conf = ConfigUtil.getInstance();
			mOauth.setOpenkey(conf.getAppKey());
			mOauth.setAccessToken(token);
			mOauth.setExpiresIn(expires_in);
			mOauth.setRefreshToken(remind_in);
			mOauth.setOpenid(uid);
	        mOauth.setStatus(0); 
	        
			AccessTokenKeeper.keepAccessToken(mContext, mOauth);
			
	        //授权成功后,将Oauth实例放到一个全局的Hashmap中，方便，分享时候取出
			CmmobiSnsLib.getInstance().updateOAuthV2Map(SHARE_TO.SINA.ordinal(), mOauth);

            mListener.onComplete(SHARE_TO.SINA.ordinal());
            
        }

        @Override
        public void onError(WeiboDialogError e) {
            //Toast.makeText(mContext, "Auth onError : " + e.getMessage(), Toast.LENGTH_LONG).show();
            CmmobiSnsLib.getInstance().authorize(SHARE_TO.SINA.ordinal(), mListener);
            //ZDialog.dismiss();
        }

        @Override
        public void onCancel() {
            //Toast.makeText(mContext, "Auth onCancel", Toast.LENGTH_LONG).show();
            //CmmobiSnsLib.getInstance().authorize(SHARE_TO.SINA.ordinal(), mListener);
            ZDialog.dismiss();
        }

        @Override
        public void onWeiboException(WeiboException e) {
            //Toast.makeText(mContext, "Auth exception : " + e.getMessage(), Toast.LENGTH_LONG).show();
            
            CmmobiSnsLib.getInstance().authorize(SHARE_TO.SINA.ordinal(), mListener);
            //ZDialog.dismiss();
        }

    }
	private void sina_sso_auth(WeiboAuthListener listener){
		mListener = listener;
	}
	
	/**
	 * 新浪微博授权
	 * @param listener
	 */
	public void sinaAuthorize(WeiboAuthListener listener){
		ConfigUtil conf = ConfigUtil.getInstance();
		conf.initSinaData();
		
	    String appKey = ConfigUtil.getInstance().getAppKey();
	    String appSecret = ConfigUtil.getInstance().getAppSecret();
	    String redirectUrl = ConfigUtil.getInstance().getRedirectUrl();
	    String authorizeUrl = ConfigUtil.getInstance().getAuthoriz_token_url();
	    mOauth = new OAuthV2(redirectUrl);
	    mOauth.setClientId(appKey);
	    mOauth.setClientSecret(appSecret);
	    mOauth.setAuthorizeUrl(authorizeUrl);
	    mOauth.setCurWeiboIndex(SHARE_TO.SINA.ordinal());
		
	    mWeibo = Weibo.getInstance(appKey, redirectUrl);

	    
        if(ActiveAccount.getInstance(mContext).isForceLogin){
        	this.authorize(SHARE_TO.SINA.ordinal(), listener);
        }else{
            mSsoHandler = new SsoHandler((Activity) mContext, mWeibo);
            mSsoHandler.authorize(new AuthDialogListener());
            sina_sso_auth(listener);
        }

		//this.authorize(SHARE_TO.SINA.ordinal(), listener);
	}
	
	/**
	 * 验证用户是否已通过新浪微博授权或需要重新授权
	 * 
	 * @return
	 */
	public boolean isSinaWeiboAuthorized(){
		OAuthV2 oAuth = (OAuthV2) this.acquireSinaOauth();
		return (oAuth!=null && oAuth.isSessionValid());
	}
	/**
	 * 获取新浪微博用户Id
	 * @return
	 */
	public String getSinaWeiboUserId(){		
		if(null == this.acquireSinaOauth() ){
			return "WEIBOUSERIDNULL";
		}else{			
			return this.acquireSinaOauth().getOpenid();
		}
	}
	
	public OAuthV2 acquireSinaOauth(){
		return this.acquireOauth(SHARE_TO.SINA.ordinal());
	}
	
	public OAuthV2 acquireTencentWeiboOauth(){
		return this.acquireOauth(SHARE_TO.TENC.ordinal());
	}
	
	public OAuthV2 acquireQQMobileOauth(){
		return this.acquireOauth(SHARE_TO.QQMOBILE.ordinal());
	}
	
	
	/**
	 * 获取OAuth对象
	 * @param curWeiboIndex
	 * @return
	 */
	public OAuthV2 acquireOauth(int curWeiboIndex){
		OAuthV2 oauth = null;
		//先从缓存中取
		if(this.oAuthV2Map != null && this.oAuthV2Map.size() > 0){
			oauth = this.oAuthV2Map.get(curWeiboIndex);
		}
		if(oauth == null){//若缓存中没有，则从文件中new出一个OAuth实例
			//Log.e(TAG, "CmmobiSnsLib acquireOauth call readAccessToken, uid:" + uid);
			oauth = AccessTokenKeeper.readAccessToken(mContext, curWeiboIndex);
			if(oauth!=null){
				updateOAuthV2Map(curWeiboIndex, (OAuthV2) oauth);
			}
		}
		return oauth;
	}
	
	/**
	 * 获取OAuth对象
	 * @param curWeiboIndex
	 * @return
	 */
	public void removeOauth(int curWeiboIndex){
		//先从缓存中取
		if(this.oAuthV2Map != null && this.oAuthV2Map.size() > 0){
			this.oAuthV2Map.remove(curWeiboIndex);
		}

		AccountInfo ai = AccountInfo.getInstance(ActiveAccount.getInstance(MainApplication.getAppInstance()).getLookLookID());
		if(curWeiboIndex==SHARE_TO.SINA.ordinal()){
			ai.removeAccessToken("3", "3", "1");
		}else if(curWeiboIndex==SHARE_TO.RENREN.ordinal()){
			ai.removeAccessToken("3", "3", "2");
		}else if(curWeiboIndex==SHARE_TO.TENC.ordinal()){
			ai.removeAccessToken("3", "3", "6");
		}else if(curWeiboIndex==SHARE_TO.QQMOBILE.ordinal()){
			ai.removeAccessToken("3", "3", "13");
		}
		
	}
	
	
	/**
	 * 指定一个图片URL地址抓取后上传并同时发布一条新微博，此方法会处理URLencode
	 * 
	 * @param uid 用户uid
	 * @param status 要发布的微博文本内容，内容不超过140个汉字。
	 * @param imageUrl 图片的URL地址，必须以http开头。
	 * @param lat 纬度，有效范围：-90.0到+90.0，+表示北纬，默认为0.0。
	 * @param lon 经度，有效范围：-180.0到+180.0，+表示东经，默认为0.0。
	 * 
	 * @return idstr 字符串型的微博ID
	 */
	public String sinaUploadUrlText(String status, String imageUrl, String lat, String lon) throws Exception {
		if (null == mSWStatusApi) {
			this.mSWStatusApi = new SWStatusesAPI(OAuthConstants.OAUTH_VERSION_2_A);
		}
		String jsonResult = mSWStatusApi.uploadUrlText(acquireSinaOauth(), status, imageUrl, lat, lon);
		if(jsonResult != null){
			WeiboError WeiboError = JsonParseUtils.parseCommonWeiboError(jsonResult);
			if(WeiboError == null){
				return null;
			}else{
				return JsonParseUtils.getStringValue(jsonResult, "idstr");
			}
		}
		return null;
	}
	
	/**
	 * 上传本地图片
	 * */
	public String sinaUploadPicText(String status, String imagePath, String lat, String lon) throws Exception {
		if (null == mSWStatusApi) {
			this.mSWStatusApi = new SWStatusesAPI(OAuthConstants.OAUTH_VERSION_2_A);
		}
		String jsonResult = mSWStatusApi.uploadPicText(acquireSinaOauth(), status, imagePath, lat, lon);
		if(jsonResult != null){
			WeiboError WeiboError = JsonParseUtils.parseCommonWeiboError(jsonResult);
			if(WeiboError == null){
				return null;
			}else{
				return JsonParseUtils.getStringValue(jsonResult, "idstr");
			}
		}
		return null;
	}
	
	/**
	 * 获取用户的双向关注列表，即互粉列表
	 * @param uid looklook登录成功后返回的userid
	 * @param snsid 需要获取双向关注列表的用户snsid。
	 * @param count 单页返回的记录条数，默认为50。
	 * @param page 返回结果的页码，默认为1。
	 * @throws Exception 
	 */
	public String getMySWFriendsList(long snsid, int count, int page) throws Exception {
		if(null == mSWFriendshipsApi){
			mSWFriendshipsApi = new SWFriendshipsAPI(OAuthConstants.OAUTH_VERSION_2_A);
		}
		return mSWFriendshipsApi.bilateral(acquireSinaOauth(),snsid, count, page);
	}
	
	
	/**
	 * 根据用户ID获取用户信息
	 * @param uid looklook登录成功后返回的userid
	 * @param snsid 需要查询的用户snsid。
	 * @throws Exception 
	 */
	public String getSWUserInfo(long snsid) throws Exception {
		if(null == mSWUsersApi){
			mSWUsersApi = new SWUsersAPI(OAuthConstants.OAUTH_VERSION_2_A);
		}
		return mSWUsersApi.show(acquireSinaOauth(), snsid);
	}
	
	/**
	 * 根据微博ID返回某条微博的评论列表
	 * @param id 需要查询的微博ID。
	 * @param since_id 若指定此参数，则返回ID比since_id大的评论（即比since_id时间晚的评论），默认为0。
	 * @param max_id 若指定此参数，则返回ID小于或等于max_id的评论，默认为0。
	 * @param count 单页返回的记录条数，默认为50
	 * @param page 返回结果的页码，默认为1。
	 * @param filter_by_author 作者筛选类型，0：全部、1：我关注的人、2：陌生人，默认为0。
	 * @throws Exception 
	 */
	public String getSWCommentList(long id, long since_id, long max_id, int count, int page) throws Exception {
		if(null == this.mSWCommentsApi){
			mSWCommentsApi = new SWCommentsAPI(OAuthConstants.OAUTH_VERSION_2_A);
		}
		return mSWCommentsApi.show(acquireSinaOauth(), id, since_id, max_id, count, page);
	}
	
	
	/**
	 * 回复一条评论
	 * 
	 * @param uid looklook登录成功后返回的userid
	 * @param cid 需要回复的评论ID。
	 * @param id 需要评论的微博ID。
	 * @param comment 回复评论内容，内容不超过140个汉字。
	 * @param without_mention 回复中是否自动加入“回复@用户名”，true：是、false：否，默认为false。
	 * @param comment_ori 当评论转发微博时，是否评论给原微博，false：否、true：是，默认为false。
	 * @throws Exception 
	 */
	public String replyToSinaComment( long cid, long id, String comment, boolean without_mention,
			boolean comment_ori) throws Exception {
		if(null == this.mSWCommentsApi){
			mSWCommentsApi = new SWCommentsAPI(OAuthConstants.OAUTH_VERSION_2_A);
		}
		return mSWCommentsApi.reply(acquireSinaOauth(), cid, id, comment, without_mention, comment_ori);
	}
	
	
	/**
	 * 对一条微博进行评论
	 * 
	 * @param uid looklook登录成功后返回的userid
	 * @param comment 评论内容，内容不超过140个汉字。
	 * @param id 需要评论的微博ID。
	 * @param comment_ori 当评论转发微博时，是否评论给原微博
	 * @throws Exception 
	 */
	public String commentOnSW( String comment, long id, boolean comment_ori) throws Exception {
		if(null == this.mSWCommentsApi){
			mSWCommentsApi = new SWCommentsAPI(OAuthConstants.OAUTH_VERSION_2_A);
		}
		return mSWCommentsApi.create(acquireSinaOauth(),comment, id, comment_ori);
	}
	
	
	/**
	 * 批量获取指定微博的转发数评论数
	 * 
	 * @param uid looklook登录成功后返回的userid
	 * @param ids 需要获取数据的微博ID，最多不超过100个。
	 * @throws Exception 
	 */
	public String countsOfSW(String[] ids) throws Exception {
		if (null == mSWStatusApi) {
			this.mSWStatusApi = new SWStatusesAPI(OAuthConstants.OAUTH_VERSION_2_A);
		}
		return mSWStatusApi.count(acquireSinaOauth(), ids);
	}
	
	/**
	 * 删除一条微博
	 * 
	 * @param weiboID 要删除的微博ID 
	 * @return 删除是否成功
	 */
	public boolean sinaDel(String weiboID) throws Exception {
		if (null == mSWStatusApi) {
			this.mSWStatusApi = new SWStatusesAPI(OAuthConstants.OAUTH_VERSION_2_A);
		}
		String jsonResult = mSWStatusApi.destroyWeibo(acquireSinaOauth(), weiboID);
		if(jsonResult != null){
			WeiboError weiboError = JsonParseUtils.parseCommonWeiboError(jsonResult);
			if(weiboError == null){
				return false;
			}else{
				return weiboError.getErrorCode()==0;
			}
		}
		return false;
	}
	
	
/*****************************************************************************************************

                                      腾讯微博分享api

*****************************************************************************************************/
	private static OAuthV2 mOauth;
	private static WeiboAuthListener mListener;
	
	private void tencent_sso_auth(long appid, String app_secket, WeiboAuthListener listener) {
		//final Context context = mContext;
		mListener = listener;
		
		AuthHelper.register(CmmobiSnsLib.this.mContext, appid, app_secket, new OnAuthListener() {

			@Override
			public void onWeiBoNotInstalled() {
				//Toast.makeText(mContext, "onWeiBoNotInstalled", 1000).show();
				CmmobiSnsLib.getInstance().authorize(SHARE_TO.TENC.ordinal(), mListener);
				//ZDialog.dismiss();
			}

			@Override
			public void onWeiboVersionMisMatch() {
				//Toast.makeText(mContext, "onWeiBoNotInstalled", 1000).show();
				CmmobiSnsLib.getInstance().authorize(SHARE_TO.TENC.ordinal(), mListener);
				//ZDialog.dismiss();
			}

			@Override
			public void onAuthFail(int result, String err) {
				//Toast.makeText(mContext, "result : " + result, 1000).show();
				ZDialog.dismiss();
			}

			@Override
			public void onAuthPassed(String name, WeiboToken token) {
				//Toast.makeText(mContext, "passed", 1000).show();

/*				Util.saveSharePersistent(context, "ACCESS_TOKEN", token.accessToken);
				Util.saveSharePersistent(context, "EXPIRES_IN", String.valueOf(token.expiresIn));
				Util.saveSharePersistent(context, "OPEN_ID", token.openID);

				Util.saveSharePersistent(context, "REFRESH_TOKEN", "");
				Util.saveSharePersistent(context, "CLIENT_ID", Util.getConfig().getProperty("APP_KEY"));
				Util.saveSharePersistent(context, "AUTHORIZETIME",
						String.valueOf(TimeHelper.getInstance().now() / 1000l));
*/

				ConfigUtil conf = ConfigUtil.getInstance();
				
				mOauth.setClientId(conf.getAppKey());
				mOauth.setOpenkey(conf.getAppKey());
				mOauth.setRefreshToken(token.refreshToken);
				mOauth.setAccessToken(token.accessToken);
				mOauth.setExpiresIn(String.valueOf(token.expiresIn));
				mOauth.setOpenid(token.openID);
		        mOauth.setStatus(0);
		        
				AccessTokenKeeper.keepAccessToken(mContext, mOauth);
				
		        //授权成功后,将Oauth实例放到一个全局的Hashmap中，方便，分享时候取出
				CmmobiSnsLib.getInstance().updateOAuthV2Map(SHARE_TO.TENC.ordinal(), mOauth);

				mListener.onComplete(SHARE_TO.TENC.ordinal());
				
				AuthHelper.unregister(CmmobiSnsLib.this.mContext);
			}
		});

		AuthHelper.auth(mContext, "");
	}
	
	
	/**
	 * 腾讯微博授权
	 * @param context
	 */
	public void tencentWeiboAuthorize(WeiboAuthListener listener){
		ConfigUtil conf = ConfigUtil.getInstance();
		conf.initQqData();
		
	    String appKey = ConfigUtil.getInstance().getAppKey();
	    String appSecret = ConfigUtil.getInstance().getAppSecret();
	    String redirectUrl = ConfigUtil.getInstance().getRedirectUrl();
	    String authorizeUrl = ConfigUtil.getInstance().getAuthoriz_token_url();
	    mOauth = new OAuthV2(redirectUrl);
	    mOauth.setClientId(appKey);
	    mOauth.setClientSecret(appSecret);
	    mOauth.setAuthorizeUrl(authorizeUrl);
	    mOauth.setCurWeiboIndex(SHARE_TO.TENC.ordinal());
	    
//	    if(ActiveAccount.getInstance(mContext).isForceLogin){
//	    	this.authorize(SHARE_TO.TENC.ordinal(), listener);
//	    }else{
//	    	tencent_sso_auth(Long.valueOf(appKey), appSecret, listener);
//	    }
		
		this.authorize(SHARE_TO.TENC.ordinal(), listener);
	}
	
	/**
	 * 验证用户是否已通过腾讯微博授权或需要重新授权
	 * 
	 * @param uid looklook登录成功后返回的userid
	 * @return
	 */
	public boolean isTencentWeiboAuthorized(){
		OAuthV2 oAuth = (OAuthV2) this.acquireTencentWeiboOauth();
		return (oAuth!=null && oAuth.isSessionValid());
	}
	/**
	 * 获取腾讯微博用户Id
	 * 
	 * @param uid looklook登录成功后返回的userid
	 * @return
	 */
	public String getTencentWeiboUserId(){
		if(null == this.acquireTencentWeiboOauth()){
			return "WEIBOUSERIDNULL";
		}else{	
		    return this.acquireTencentWeiboOauth().getOpenid();
		}
	}
	
	/**
	 * 发表一条带图片的微博
	 * 
	 * @param uid looklook登录成功后返回的userid
	 * @param content  微博内容
	 * @param clientip 用户IP(以分析用户所在地)
	 * @param picpath 可以是本地图片路径 或 网络地址
	 * @return
	 * @throws Exception
     * @see <a href="http://wiki.open.t.qq.com/index.php/%E5%BE%AE%E5%8D%9A%E7%9B%B8%E5%85%B3/%E5%8F%91%E8%A1%A8%E4%B8%80%E6%9D%A1%E5%B8%A6%E5%9B%BE%E7%89%87%E7%9A%84%E5%BE%AE%E5%8D%9A">腾讯微博开放平台上关于此条API的文档1-本地图片</a>
     * @see <a href="http://wiki.open.t.qq.com/index.php/%E5%BE%AE%E5%8D%9A%E7%9B%B8%E5%85%B3/%E7%94%A8%E5%9B%BE%E7%89%87URL%E5%8F%91%E8%A1%A8%E5%B8%A6%E5%9B%BE%E7%89%87%E7%9A%84%E5%BE%AE%E5%8D%9A">腾讯微博开放平台上关于此条API的文档2-网络图片</a>
	 */
	public String twUploadUrlText(String content,
			String clientip, String picpath) throws Exception {
		if(mTWTapi == null){
			mTWTapi= new TWTAPI(OAuthConstants.OAUTH_VERSION_2_A);
		}
		String format = "json";
		String jsonResult =  mTWTapi.addPic(acquireTencentWeiboOauth(), format, content, clientip, "", "", picpath, "");	
		if(jsonResult != null){
			WeiboError weiboError = JsonParseUtils.parseCommonWeiboError(jsonResult);
			if(weiboError == null){
				return null;
			}else{
				JSONObject data = JsonParseUtils.getJSONObjectValue(jsonResult, "data");
				if(data==null){
					return  null;
				}
				
				try{
					long id = data.getLong("id");
					return String.valueOf(id);
				}catch(Exception e){
					return  null;
				}

			}
		}
		return null;
	}
	
    /**
	 * 腾讯微博获取自己的资料
	 * 
	 * @param uid looklook登录成功后返回的userid
	 * @return
	 * @throws Exception
     * @see <a href="http://wiki.open.t.qq.com/index.php/%E5%B8%90%E6%88%B7%E7%9B%B8%E5%85%B3/%E8%8E%B7%E5%8F%96%E8%87%AA%E5%B7%B1%E7%9A%84%E8%AF%A6%E7%BB%86%E8%B5%84%E6%96%99">腾讯微博开放平台上关于此条API的文档</a>
	 */
	public String getTWUserInfo() throws Exception {
		if(mTWUserApi == null){
			mTWUserApi = new TWUserAPI(OAuthConstants.OAUTH_VERSION_2_A);
		}
		String format = "json";
		return mTWUserApi.info(acquireTencentWeiboOauth(), format);
	}
	
	/**
	 * 获取用户的双向关注列表，即互粉列表
	 * 
	 * @param uid looklook登录成功后返回的userid
	 * @param reqnum  请求个数(1-30)
	 * @param startindex 起始位置（第一页填0，继续向下翻页：填：【reqnum*（page-1）】）
     * @param install  过滤安装应用好友（可选） <br>
     *                           0-不考虑该参数，1-获取已安装应用好友，2-获取未安装应用好友 
	 * @return
	 * @throws Exception
     * @see <a href="http://wiki.open.t.qq.com/index.php/%E5%B8%90%E6%88%B7%E7%9B%B8%E5%85%B3/%E6%88%91%E6%94%B6%E5%90%AC%E7%9A%84%E4%BA%BA%E5%88%97%E8%A1%A8">腾讯微博开放平台上关于此条API的文档</a>
	 */
	public String getMyTWFriendsList(String name, String reqnum,
			String startindex) throws Exception {
		if(null == mTWFriendsApi){
			mTWFriendsApi = new TWFriendsAPI(OAuthConstants.OAUTH_VERSION_2_A);
		}
		String install = "0";//install  过滤安装应用好友（可选)0-不考虑该参数，1-获取已安装应用好友，2-获取未安装应用好友 
		return mTWFriendsApi.mutualList(acquireTencentWeiboOauth(), name, "json", reqnum, startindex, install);
	}
	
	/**
	 * 获取单条微博的点评列表
	 * 
	 * @param uid looklook登录成功后返回的userid
	 * @param rootid 转发或回复的微博根结点id（源微博id）
	 * @param pageflag 分页标识（0：第一页，1：向下翻页，2：向上翻页）
	 * @param pagetime 本页起始时间（第一页：填0，向上翻页：填上一次请求返回的第一条记录时间，向下翻页：填上一次请求返回的最后一条记录时间）
	 * @param reqnum 每次请求记录的条数（1-100条）
	 * @param twitterid 翻页用，第1-100条填0，继续向下翻页，填上一次请求返回的最后一条记录id
	 * @return
	 * @throws Exception
     * @see <a href="http://wiki.open.t.qq.com/index.php/%E5%BE%AE%E5%8D%9A%E7%9B%B8%E5%85%B3/%E8%8E%B7%E5%8F%96%E5%8D%95%E6%9D%A1%E5%BE%AE%E5%8D%9A%E7%9A%84%E8%BD%AC%E5%8F%91%E6%88%96%E7%82%B9%E8%AF%84%E5%88%97%E8%A1%A8">腾讯微博开放平台上关于此条API的文档</a>
	 */
	public String getTWCommentList(String rootid,
			String pageflag, String pagetime, String reqnum,
			String twitterid) throws Exception {
		if(null == this.mTWTapi){
			mTWTapi = new TWTAPI(OAuthConstants.OAUTH_VERSION_2_A);
		}
		String flag = "1"; //flag  标识。0－转播列表 1－点评列表 2－点评与转播列表 
		return mTWTapi.reList(acquireTencentWeiboOauth(), "json", flag, rootid, pageflag, pagetime, reqnum, twitterid);
	}
	
	/**
	 * 回复一条微博
	 * 
	 * @param uid looklook登录成功后返回的userid
	 * @param content  微博内容
	 * @param clientip 用户IP(以分析用户所在地)
	 * @param reid 回复的父结点微博id
	 * @return
	 * @throws Exception
     * @see <a href="http://wiki.open.t.qq.com/index.php/%E5%BE%AE%E5%8D%9A%E7%9B%B8%E5%85%B3/%E5%9B%9E%E5%A4%8D%E4%B8%80%E6%9D%A1%E5%BE%AE%E5%8D%9A%EF%BC%88%E5%8D%B3%E5%AF%B9%E8%AF%9D%EF%BC%89">腾讯微博开放平台上关于此条API的文档</a>
	 */
	public String replyToTwComment(String content,
			String clientip, String reid) throws Exception {
		if(null == this.mTWTapi){
			mTWTapi = new TWTAPI(OAuthConstants.OAUTH_VERSION_2_A);
		}
		String format = "json"; //format 返回数据的格式 是（json或xml）
		return mTWTapi.comment(acquireTencentWeiboOauth(), format, content, clientip, reid);
	}
	
	/**
	 * 点评一条微博
	 * 
	 * @param uid looklook登录成功后返回的userid
	 * @param content  微博内容
	 * @param clientip 用户IP(以分析用户所在地)
	 * @param reid 点评父结点微博id
	 * @return
	 * @throws Exception
     * @see <a href="http://wiki.open.t.qq.com/index.php/%E5%BE%AE%E5%8D%9A%E7%9B%B8%E5%85%B3/%E7%82%B9%E8%AF%84%E4%B8%80%E6%9D%A1%E5%BE%AE%E5%8D%9A">腾讯微博开放平台上关于此条API的文档</a>
	 */
	public String commentOnTW(String content,
			String clientip, String reid) throws Exception {
		if(null == this.mTWTapi){
			mTWTapi = new TWTAPI(OAuthConstants.OAUTH_VERSION_2_A);
		}
		return mTWTapi.comment(acquireTencentWeiboOauth(), "json", content, clientip, reid);
	}
	
	
	/**
	 * 获取微博当前已被转播次数
	 * 
	 * http://wiki.open.t.qq.com/index.php/API%E6%96%87%E6%A1%A3/%E5%BE%AE%E5%8D%9A%E6%8E%A5%E5%8F%A3/%E8%BD%AC%E6%92%AD%E6%95%B0%E6%88%96%E7%82%B9%E8%AF%84%E6%95%B0
	 * 
	 * @param ids 微博ID列表，用“,”隔开
	 * @param flag 0－获取转发计数，1－获取点评计数 2－两者都获取
	 * 
	 * @return
	 * 
	 *       { 
	 *         errcode : 0, 
	 *         msg : ok, 
	 *         ret : 0, 
	 *         data : { 
	 *         				id : { 
	 *         						count : xxx,
	 *         						mcount : xxx 
	 *         					 } 
	 *                 }, 
	 *         seqid : xxx 
	 *        }
	 * 
	 * 
	 * @throws Exception
	 * @see <a href=
	 *      "http://wiki.open.t.qq.com/index.php/%E5%BE%AE%E5%8D%9A%E7%9B%B8%E5%85%B3/%E8%BD%AC%E6%92%AD%E6%95%B0%E6%88%96%E7%82%B9%E8%AF%84%E6%95%B0"
	 *      >腾讯微博开放平台上关于此条API的文档<a>
	 */
	public String countsOfTW( String ids, String flag) throws Exception {
		if(null == this.mTWTapi){
			mTWTapi = new TWTAPI(OAuthConstants.OAUTH_VERSION_2_A);
		}
		return mTWTapi.reCount(acquireTencentWeiboOauth(), "json", ids, flag);
	}
	
	
	/**
	 * 删除一条微博
	 * 
	 * @param weiboID 要删除的微博ID
	 * @return
	 * @throws Exception
     * @see <a href="http://wiki.open.t.qq.com/index.php/API%E6%96%87%E6%A1%A3/%E5%BE%AE%E5%8D%9A%E6%8E%A5%E5%8F%A3/%E5%88%A0%E9%99%A4%E4%B8%80%E6%9D%A1%E5%BE%AE%E5%8D%9A">腾讯微博开放平台上关于此条API的文档2-删除一条微博</a>
	 */
	public boolean twDel(String weiboID) throws Exception {
		if(mTWTapi == null){
			mTWTapi= new TWTAPI(OAuthConstants.OAUTH_VERSION_2_A);
		}
		String format = "json";
		String jsonResult =  mTWTapi.del(acquireTencentWeiboOauth(),format, weiboID);	
		if(jsonResult != null){
			WeiboError weiboError = JsonParseUtils.parseCommonWeiboError(jsonResult);
			if(weiboError == null){
				return false;
			}else{
				return weiboError.getErrorCode()==0;
			}
		}
		return false;
	}

/*****************************************************************************************************

   人人微博分享api

*****************************************************************************************************/
	public RennClient rennClient;
	
	private void renren_sso_auth(ConfigUtil conf, WeiboAuthListener listener){
		mListener = listener;
		
	    String appKey = conf.getAppKey();
	    String appSecret = conf.getAppSecret();
	    String redirectUrl = conf.getRedirectUrl();
	    String authorizeUrl = conf.getAuthoriz_token_url();
	    mOauth = new OAuthV2(redirectUrl);
	    mOauth.setClientId(appKey);
	    mOauth.setClientSecret(appSecret);
	    mOauth.setAuthorizeUrl(authorizeUrl);
	    mOauth.setCurWeiboIndex(SHARE_TO.RENREN.ordinal());
		
		rennClient = RennClient.getInstance(CmmobiSnsLib.this.mContext);
		rennClient.init(conf.getAppID(), conf.getAppKey(), conf.getAppSecret());
		rennClient
				.setScope("read_user_blog read_user_photo read_user_status read_user_album "
						+ "read_user_comment read_user_share publish_blog publish_share "
						+ "send_notification photo_upload status_update create_album "
						+ "publish_comment publish_feed");
		// rennClient
		// .setScope("read_user_blog read_user_photo read_user_status read_user_album "
		// + "read_user_comment publish_blog publish_share "
		// + "send_notification photo_upload status_update create_album "
		// + "publish_feed");
		// rennClient.setScope("read_user_blog read_user_status");
		rennClient.setTokenType("bearer");
		
		rennClient.setLoginListener(new LoginListener() {
			@Override
			public void onLoginSuccess() {
				// TODO Auto-generated method stub
/*				Toast.makeText(CmmobiSnsLib.this.mContext, "登录成功",
						Toast.LENGTH_SHORT).show();*/
				com.renn.rennsdk.AccessToken at = rennClient.getAccessToken();
				ConfigUtil conf = ConfigUtil.getInstance();
				
				mOauth.setOpenkey(conf.getAppKey());
				mOauth.setAccessToken(at.accessToken);
				mOauth.setRefreshToken(at.refreshToken);
				mOauth.setExpiresIn(String.valueOf(rennClient.getAccessToken().expiresIn));
				mOauth.setOpenid(String.valueOf(rennClient.getUid()));
		        mOauth.setStatus(0);
		        
				AccessTokenKeeper.keepAccessToken(mContext, mOauth);
				
		        //授权成功后,将Oauth实例放到一个全局的Hashmap中，方便，分享时候取出
				CmmobiSnsLib.getInstance().updateOAuthV2Map(SHARE_TO.RENREN.ordinal(), mOauth);

				mListener.onComplete(SHARE_TO.RENREN.ordinal());
			}

			@Override
			public void onLoginCanceled() {
				ZDialog.dismiss();
			}
		});
		rennClient.login((Activity) CmmobiSnsLib.this.mContext);
	}
	
	/**
	 * 人人微博授权
	 * @param listener
	 */
	public void renrenAuthorize(WeiboAuthListener listener){
		ConfigUtil conf = ConfigUtil.getInstance();
		conf.initRenrenData();
		
		if(ActiveAccount.getInstance(mContext).isForceLogin){
			this.authorize(SHARE_TO.RENREN.ordinal(), listener);
		}else{
			renren_sso_auth(conf, listener);
		}
		
		//this.authorize(SHARE_TO.RENREN.ordinal(), listener);
	}
	
	/**
	 * 验证用户是否已通过人人微博授权或需要重新授权
	 * 
	 * @return
	 */
	public boolean isRenrenWeiboAuthorized(){
		OAuthV2 oAuth = (OAuthV2) this.acquireRenrenOauth();
		return (oAuth!=null && oAuth.isSessionValid());
	}
	
	/**
	 * 获取人人微博用户Id
	 * @return
	 */
	public String getRenrenWeiboUserId(){
		if(null == this.acquireRenrenOauth()){
			return WEIBOUSERIDNULL;
		}else{	
			return this.acquireRenrenOauth().getOpenid();
		}
	}
	
	public OAuthV2 acquireRenrenOauth(){
		return this.acquireOauth(SHARE_TO.RENREN.ordinal());
	}
	

	/**
	 * 指定一个图片URL地址抓取后上传并同时发布一条人人微博，此方法会处理URLencode
	 * http://wiki.dev.renren.com/wiki/V2/share/url/put
	 * @param uid 用户uid
	 * @param status 要发布的微博文本内容，内容不超过140个汉字。
	 * @throws Exception 
	 * @return true 分享成功 false 分享失败
	 */
	public String renrenUploadUrlText(String message, String imageUrl) throws Exception {
		if (null == mRWStatusApi) {
			this.mRWStatusApi = new RWStatusesAPI(OAuthConstants.OAUTH_VERSION_2_A);
		}
		
		String jsonResult = mRWStatusApi.uploadUrlText(acquireRenrenOauth(), message, imageUrl);
		if(jsonResult != null){
			WeiboError weiboError = JsonParseUtils.parseCommonWeiboError(jsonResult);
			if(weiboError == null){
				return null;
			}else{
				JSONObject response = JsonParseUtils.getJSONObjectValue(jsonResult, "response");
				if(response==null){
					return  null;
				}
				
				try{
					long id = response.getLong("id");
					return String.valueOf(id);
				}catch(Exception e){
					return  null;
				}

			}
		}
		return null;
	}
	
	public String renrenUploadPicText(String message, String imagePath) throws Exception {
		if (null == mRWStatusApi) {
			this.mRWStatusApi = new RWStatusesAPI(OAuthConstants.OAUTH_VERSION_2_A);
		}
		
		String jsonResult = mRWStatusApi.uploadPicText(acquireRenrenOauth(), message, imagePath);
		if(jsonResult != null){
			WeiboError weiboError = JsonParseUtils.parseCommonWeiboError(jsonResult);
			if(weiboError == null){
				return null;
			}else{
				JSONObject response = JsonParseUtils.getJSONObjectValue(jsonResult, "response");
				if(response==null){
					return  null;
				}
				
				try{
					long id = response.getLong("id");
					return String.valueOf(id);
				}catch(Exception e){
					return  null;
				}

			}
		}
		return null;
	}
	
	public String renrenPutUGC(String message, String ugc_id, String type) throws Exception {
		if (null == mRWStatusApi) {
			this.mRWStatusApi = new RWStatusesAPI(OAuthConstants.OAUTH_VERSION_2_A);
		}
		
		String jsonResult = mRWStatusApi.ugcPut(acquireRenrenOauth(), message, ugc_id, type);
		if(jsonResult != null){
			WeiboError weiboError = JsonParseUtils.parseCommonWeiboError(jsonResult);
			if(weiboError == null){
				return null;
			}else{
				JSONObject response = JsonParseUtils.getJSONObjectValue(jsonResult, "response");
				if(response==null){
					return  null;
				}
				
				try{
					long id = response.getLong("id");
					return String.valueOf(id);
				}catch(Exception e){
					return null;
				}

			}
		}
		return null;
	}
	
	/**
	 * 	发布分享，例如分享视频、音乐、链接、日志、相册、照片等
	 * http://wiki.dev.renren.com/wiki/Share.share
	 * @param url 分享人人网站外内容的URL。
	 * @param type 分享的类型：日志为1、照片为2、链接为6、相册为8、视频为10、音频为11、分享为20。
	 * @param comment 分享内容时，用户的评论内容。
	 * @param source_link 新鲜事中来源信息链接，连接的名字默认为App的名字。该参数为JSON格式，格式如下：{ "text": "App A", "href": "http://appa.com/path"。
	 * @throws Exception 
	 * @return true 分享成功 false 分享失败
	 * 
	 * 注意:
		当分享日志、照片、相册等人人网站内内容时，ugc_id和user_id为必须参数。
		当分享优酷视频、站外链接等人人网站外内容时，url为必须参数。此时type只能是：链接为6、视频为10、音频为11。
		当基于现存分享再次进行分享时（可以获取到分享的ID），type只能是：分享20，ugc_id和user_id为必须参数。
		例如：
		分享站内照片需要传递以下参数：
		type: 2
		ugc_id: 1234567
		user_id: 7654321
		分享站外视频：
		type: 10
		url: http://www.youku.com/12132123.html
		基于现存分享再次分享：
		type: 20
		ugc_id: 分享的ID
		user_id: 分享所有者的ID
	 */
	public boolean renrenShareVideo(String url, int type, String comment, String source_link) throws Exception {
		if (null == mRWStatusApi) {
			this.mRWStatusApi = new RWStatusesAPI(OAuthConstants.OAUTH_VERSION_2_A);
		}
		
		String jsonResult = mRWStatusApi.Share(acquireRenrenOauth(), "10", url, comment, source_link );
		Log.e(TAG, "jsonResult:" + jsonResult);
		if(jsonResult != null){
			WeiboError weiboError = JsonParseUtils.parseCommonWeiboError(jsonResult);
			if(weiboError == null){
				return false;
			}else{
				return weiboError.getErrorCode()==0;
			}
		}
		return false;
	}
	
	/**
	 * 获取人人用户的双向关注列表，即互粉列表
	 * @param snsid 需要获取双向关注列表的用户snsid。
	 * @param pageSize 页面大小。取值范围1-100，默认大小20
	 * @param pageNumber 页码。取值大于零，默认值为1
	 */
	public String getMyRWFriendsList(long snsid, int pageSize, int pageNumber) throws Exception {
		if(null == mRWFriendshipsApi){
			mRWFriendshipsApi = new RWFriendshipsAPI(OAuthConstants.OAUTH_VERSION_2_A);
		}
		
		
		return mRWFriendshipsApi.bilateral(acquireRenrenOauth(), snsid, pageSize, pageNumber);
	}
	/**
	 * 根据用户ID获取用户信息
	 * @param snsid 需要查询的用户snsid。
	 * @throws Exception 
	 */
	public String getRWUserInfo(long snsid) throws Exception {
		if(null == mRWUsersApi){
			mRWUsersApi = new RWUsersAPI(OAuthConstants.OAUTH_VERSION_2_A);
		}
		return mRWUsersApi.show(acquireRenrenOauth(), snsid);
	}
	
	/**
	 * 根据用户ID获取用户信息
	 * @param snsid 需要查询的用户snsid。
	 * @throws Exception 
	 */
	public String batchRWUserInfo(String[] snsids) throws Exception {
		if(null == mRWUsersApi){
			mRWUsersApi = new RWUsersAPI(OAuthConstants.OAUTH_VERSION_2_A);
		}
		return mRWUsersApi.show(acquireRenrenOauth(), snsids);
	}
	
	/**
	 * 根据人人微博ID返回某条微博的评论列表
	 * @param uid looklook登录成功后返回的userid
	 * @param id 需要查询的微博ID。
	 * */
	public String getRWCommentList(String snsid, String weiboid, int page, int count) throws Exception {
		if(null == this.mRWCommentsApi){
			mRWCommentsApi = new RWCommentsAPI(OAuthConstants.OAUTH_VERSION_2_A);
		}
		return mRWCommentsApi.show(acquireRenrenOauth(), Long.valueOf(snsid), Long.valueOf(weiboid), page, count );
	}
	

	
	/**
	 * 对一条人人日志进行评论
	 * @param id 日志id
	 * @param snsid 自己的人人id
	 * @param comment 评论内容
	 * @throws Exception 
	 */
	public String replyToRwComment(long snsid, long id, String comment) throws Exception {
		if(null == this.mRWCommentsApi){
			mRWCommentsApi = new RWCommentsAPI(OAuthConstants.OAUTH_VERSION_2_A);
		}
		return mRWCommentsApi.reply(acquireRenrenOauth(), snsid, id, comment);
	}
	
	/**
	 * 批量获取指定人人微博的转发数评论数
	 * 
	 * @param uid looklook登录成功后返回的userid
	 * @param ids 需要获取数据的微博ID，最多不超过100个。
	 * @throws Exception 
	 */
	public String countsOfRW( String[] ids) throws Exception {
		if (null == mRWStatusApi) {
			this.mRWStatusApi = new RWStatusesAPI(OAuthConstants.OAUTH_VERSION_2_A);
		}
		return mRWStatusApi.count(acquireRenrenOauth(), ids);
	}
	
	/************************************************************************************/
	
	
	/**
	 * 将default（null）的用户微博授权信息写入到对应uid的文件，一般在登陆成功后
	 * 
	 * @param Context 上下文，用于写文件
	 * @param uid looklook登录成功后返回的userid
	 */
/*	public void postProcess(Context c, String arg_uid){
		int num = SHARE_TO.values().length;
		for(int i=0; i<num; i++){
			OAuthV2 oa = oAuthV2Map.get(i);
			if(oa!=null){
				NewAccessTokenKeeper.keepAccessToken(c, oa, i);
			}
			
		}
		//将原来的tmp改为授权后的uid
		uid = arg_uid;
	}*/
	
	/**
	 * 注销，将内存中的map信息和CmmobiSnsLib实例释放，不影响磁盘上的授权信息！
	 * 
	 * @param Context 上下文，用于写文件
	 * @param uid looklook登录成功后返回的userid
	 */
	public void cleanup(Context c, String uid_arg){
		mSnsLib.SetUid(null);
		if(oAuthV2Map!=null){
			Log.e(TAG, "oAuthV2Map clean");
			oAuthV2Map.clear();
		}
		
		mSnsLib = null; //release object
	}
}