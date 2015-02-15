package com.cmmobi.sns.oauthv2;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.text.TextUtils;
import android.util.Log;

import com.cmmobi.looklook.info.profile.TimeHelper;
import com.cmmobi.sns.api.QQMobileAuth;
import com.cmmobi.sns.oauth.OAuth;
import com.cmmobi.sns.oauth.OAuthConstants;
import com.cmmobi.sns.utils.ConfigUtil;
import com.cmmobi.sns.utils.ConfigUtil.SHARE_TO;


/**
 * OAuth version 2 认证参数实体类
 */
public class OAuthV2 extends OAuth implements Serializable{
    
    private static final long serialVersionUID = -4667312552797390709L;
	private static final String TAG = "OAuthV2";
    private String redirectUri = "null";// 授权回调地址
    private String clientId = "";// 申请应用时分配的app_key
    private String clientSecret="";//申请应用时分配到的app_secret
    private String responseType = "code";// code、token，默认为code
    private String type="default";//显示授权页的类型，默认授权页为pc授权页
    private String authorizeCode= null;//用来换取accessToken的授权码
    private String accessToken= null;
    private String expiresIn= null;//accessToken过期时间,时间段
	private long mExpiresTime = 0L; //时间点
    private String grantType= "authorization_code";//填authorization_code, 或refresh_token
    private String refreshToken= null;//刷新token
    private String authorize_url = "";
    
    private String nick;
    
    public OAuthV2() {
        super();
        this.oauthVersion=OAuthConstants.OAUTH_VERSION_2_A;
    }
    
    public OAuthV2(int snstype_index, String snsid, String sns_token, String sns_expiration_time, String sns_effective_time, String sns_refresh_token, String nick) {
        super();
        
        if(SHARE_TO.SINA.ordinal() == snstype_index){
        	this.openid = snsid;
    	    this.clientId = ConfigUtil.getInstance().sina_AppKey;
    	    this.redirectUri =  ConfigUtil.getInstance().sina_Redirect_url;
    	    this.clientSecret = ConfigUtil.getInstance().sina_AppSecret;
    	    this.authorize_url = ConfigUtil.getInstance().sina_Authoriz_token_url;
        }else if(SHARE_TO.TENC.ordinal() == snstype_index){
        	this.openid = snsid;
    	    this.clientId = ConfigUtil.getInstance().qq_AppKey;
    	    this.redirectUri =  ConfigUtil.getInstance().qq_Redirect_url;
    	    this.clientSecret = ConfigUtil.getInstance().qq_AppSecret;
    	    this.authorize_url = ConfigUtil.getInstance().qq_Authoriz_token_url;
        }else if(SHARE_TO.RENREN.ordinal() == snstype_index){
        	this.openid = snsid;
    	    this.clientId = ConfigUtil.getInstance().renren_AppKey;
    	    this.redirectUri =  ConfigUtil.getInstance().renren_Redirect_url;
    	    this.clientSecret = ConfigUtil.getInstance().renren_AppSecret;
    	    this.authorize_url = ConfigUtil.getInstance().renren_Authoriz_token_url;
        }else if(SHARE_TO.QQMOBILE.ordinal() == snstype_index){
        	this.openid = snsid;
    	    this.clientId = QQMobileAuth.QQMOBILE_APPKEY;
//    	    this.redirectUri =  ConfigUtil.getInstance().renren_Redirect_url;
//    	    this.clientSecret = ConfigUtil.getInstance().renren_AppSecret;
//    	    this.authorize_url = ConfigUtil.getInstance().renren_Authoriz_token_url;
        }

	    this.curWeiboIndex = snstype_index;
	    this.nick = nick;
        this.oauthVersion=OAuthConstants.OAUTH_VERSION_2_A;
        this.accessToken = sns_token;
        this.refreshToken = sns_refresh_token;
        //this.clientId = sns_openkey;
        this.expiresIn = sns_effective_time;
        try{
        	this.mExpiresTime = Long.parseLong(sns_expiration_time);
        }catch(NumberFormatException e){
        	e.printStackTrace();
        	this.mExpiresTime = 0;
        }
        

    }
    
    /**
     * @param redirectUri 认证成功后浏览器会被重定向到这个地址
     */
    public OAuthV2(String redirectUri) {
        super();
        this.redirectUri = redirectUri;
        this.oauthVersion=OAuthConstants.OAUTH_VERSION_2_A;
    }

    /**
     * @param clientId 应用申请到的APP KEY
     * @param clientSecret 应用申请到的APP SECRET
     * @param redirectUri 认证成功后浏览器会被重定向到这个地址
     */
    public OAuthV2(String clientId, String clientSecret, String redirectUri) {
        super();
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.redirectUri = redirectUri;
        this.oauthVersion=OAuthConstants.OAUTH_VERSION_2_A;
    }
    
	/**
	 *  AccessToken是否有效,如果accessToken为空或者expiresTime过期，返回false，否则返回true
	 *  @return 如果accessToken为空或者expiresTime过期，返回false，否则返回true
	 */
	public boolean isSessionValid() {
/*		return (!TextUtils.isEmpty(accessToken) && (mExpiresTime == 0 || (System
				.currentTimeMillis() < mExpiresTime)));*/
		return (!TextUtils.isEmpty(accessToken) && (TimeHelper.getInstance().now() < mExpiresTime));
	}
    
    /**Authorize code grant方式中，Authorization阶段需要的参数*/
    public List<NameValuePair> getAuthorizationParamsList(OAuthV2 oAuth) {
        List<NameValuePair> paramsList = new ArrayList<NameValuePair>();
            paramsList.add(new BasicNameValuePair("client_id",clientId));
            paramsList.add(new BasicNameValuePair("response_type",  responseType));
            paramsList.add(new BasicNameValuePair("redirect_uri", redirectUri)); 
            //新浪微博需要增加下面一个参数
            if(oAuth.getCurWeiboIndex() == SHARE_TO.SINA.ordinal()){
            	paramsList.add(new BasicNameValuePair("display", "mobile")); 
            	paramsList.add(new BasicNameValuePair("forcelogin", "true")); 
            }else if(oAuth.getCurWeiboIndex() == SHARE_TO.RENREN.ordinal()){
            	paramsList.add(new BasicNameValuePair("x_renew", "true")); 
            }
            return paramsList;
    }
 
    /**Authorize code grant方式中，AccessToken阶段需要的参数*/
    public List<NameValuePair> getAccessTokenByCodeParamsList() {
        List<NameValuePair> paramsList = new ArrayList<NameValuePair>();
            paramsList.add(new BasicNameValuePair("client_id",clientId));
            paramsList.add(new BasicNameValuePair("client_secret",clientSecret));
            paramsList.add(new BasicNameValuePair("redirect_uri", redirectUri));
            paramsList.add(new BasicNameValuePair("grant_type",  "authorization_code"));
            paramsList.add(new BasicNameValuePair("code",  authorizeCode));
        return paramsList;
    }
    
    /**
     * 调用API时，需附带的OAuth鉴权信息
     * @return
     */
    public List<NameValuePair> getTokenParamsList() {
        List<NameValuePair> paramsList = new ArrayList<NameValuePair>();
            paramsList.add(new BasicNameValuePair("access_token",accessToken));
            paramsList.add(new BasicNameValuePair("oauth_consumer_key",clientId));
            paramsList.add(new BasicNameValuePair("openid",  openid));
            paramsList.add(new BasicNameValuePair("clientip", clientIP));
            paramsList.add(new BasicNameValuePair("oauth_version",  oauthVersion));
            paramsList.add(new BasicNameValuePair("scope", scope));
        return paramsList;
    }
    
    /**重定向地址*/
    public String getRedirectUri() {
        return redirectUri;
    }

    /**重定向地址*/
    public void setRedirectUri(String redirectUri) {
        this.redirectUri = redirectUri;
    }

    /**应用的APP KEY*/
    public String getClientId() {
        return clientId;
    }

    /**应用的APP KEY*/
    public void setClientId(String clientId) {
        this.clientId = clientId;
    }
    
    public String getAuthorizeUrl(){
    	return this.authorize_url;
    }
    
    public void setAuthorizeUrl(String authorizeUrl){
    	this.authorize_url = authorizeUrl;
    }

    /**授权类型*/
    public String getResponeType() {
        return responseType;
    }

    /**应用申请到的APP SECRET*/
    public String getClientSecret() {
        return clientSecret;
    }

    /**应用申请到的APP SECRET*/
    public void setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
    }

    /**授权类型*/
    public void setResponseType(String responseType) {
        this.responseType = responseType;
    }

    /** 显示授权页的类型，默认授权页为pc授权页 */
    public String getType() {
        return type;
    }

    /** 显示授权页的类型，默认授权页为pc授权页 */
    public void setType(String type) {
        this.type = type;
    }

    /**授权码*/
    public String getAuthorizeCode() {
        return authorizeCode;
    }

    /**授权码*/
    public void setAuthorizeCode(String authorizeCode) {
        this.authorizeCode = authorizeCode;
    }
    
    public String getNick() {
        return nick;
    }
    
    public void setNick(String nick) {
        this.nick = nick;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    /**accessToken过期时间,时间段*/
    public String getExpiresIn() {
        return expiresIn;
    }
    
    /**accessToken过期时间， 时间点*/
    public long getLongExpiresTime() {
        return mExpiresTime;
    }
    
    public String getExpiresTime() {
       return String.valueOf(mExpiresTime);
    }

    /**accessToken过期时间, 第一次获得授权时使用，以后不使用*/
    public void setExpiresIn(String expiresIn) {
        this.expiresIn = expiresIn;
        try{
        	 this.mExpiresTime = TimeHelper.getInstance().now() + Long.parseLong(expiresIn)*1000;
        }catch (Exception e){
        	Log.e(TAG, "setExpiresIn - invaild expiresIn : " + expiresIn);
        	e.printStackTrace();
        	this.mExpiresTime = 0L;
        }
    }
    
    /**accessToken过期时间, 数据的迁移时使用*/
    public void setExpiresIn(String expiresIn, long ExpiresTime) {
        this.expiresIn = expiresIn;
        this.mExpiresTime = ExpiresTime;
    }
    
    /**accessToken过期时间, 数据的迁移时使用*/
    public void setExpiresIn(String expiresIn, String ExpiresTime) {
    	long exptime = 0;
    	try{
    		exptime = Long.parseLong(ExpiresTime);
    	}catch(NumberFormatException  e){
    		Log.e(TAG, "setExpiresIn - invaild ExpiresTime : " + ExpiresTime);
    		e.printStackTrace();
    	}

    	setExpiresIn(expiresIn, exptime);
    	
    }
    

    /**确定请求的对象，authorization_code或refresh_token*/
    public String getGrantType() {
        return grantType;
    }

    /**确定请求的对象，authorization_code或refresh_token*/
    public void setGrantType(String grantType) {
        this.grantType = grantType;
    }

    /**刷新token*/
    public String getRefreshToken() {
        return refreshToken;
    }

    /**刷新token*/
    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }
   
}
