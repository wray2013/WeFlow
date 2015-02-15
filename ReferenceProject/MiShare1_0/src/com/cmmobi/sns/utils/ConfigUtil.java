package com.cmmobi.sns.utils;

/**
 * 获取配置文件
 * @author xudongsheng
 * @author zhangwei
 *
 */
public  class ConfigUtil {
	
	private static ConfigUtil instance;
	
	 public static enum SHARE_TO
	  {
	    SINA, TENC, /*QZONE,*/RENREN,KAIXIN,JIEPANG,QQMOBILE;
	  }	
	
	private int curWeiboIndex;
	private String appID = "";
	private String appKey = "";
	private String appSecret = "";
	private String request_token_url = "";
	private String authoriz_token_url = "";
	private String access_token_url = ""; 
	private String redirect_url="";
	
	public static final String SINAW = "SINAW";
	public static final String RNRNW = "RNRNW";
	public static final String TENCENTW = "TENCENTW";
	
	public static final String ShareToSina = "shareToSina";
	public static final String ShareToRenren = "ShareToRenren";
	public static final String ShareToTencent = "ShareToTencent";
	
	public static final String GetCommentFromSina = "GetCommentFromSina";
	public static final String GetCommentFromRenren = "GetCommentFromRenren";
	public static final String GetCommentFromTencent = "GetCommentFromTencent";
	
	//--------------------qq
	/**
	 * 微享正式版：
	 * App Key： 801471705
	 * App Secret： 4e4a193661a413807eabf6bed1d1ca71
	 * 
	 * looklook：
	 * App Key：801209961
	 * App Secret：4a6ec7ddcb4605a336559bbdd651c489
	 * */
//	public final String qq_AppID = "801209961";
//	public final String qq_AppKey = "801209961";
//	public final String qq_AppSecret = "4a6ec7ddcb4605a336559bbdd651c489";
	public final String qq_AppID = "801471705";
	public final String qq_AppKey = "801471705";
	public final String qq_AppSecret = "4e4a193661a413807eabf6bed1d1ca71";
	public final String qq_Redirect_url = "http://download.looklook.cn";//"http://weibo.com/u/2801678985";
	public final String qq_Authoriz_token_url = "https://open.t.qq.com/cgi-bin/oauth2/authorize";
	public final String qq_Access_token_url = "https://open.t.qq.com/cgi-bin/oauth2/access_token";
	
	//---------------------sina
	/**
	 * 微享正式版：
	 * App Key：2089751234 
	 * App Secret：d9119dbddd9556bc5623cc7fb7638c87
	 * looklook：
	 * APP ID：  562147941
	 * App Key：2061544481 
	 * App Secret：626e879ba2c18e1a9c5f2fe7559ff667
	 * */
//	public final String sina_AppKey = "2061544481";
//	public final String sina_AppSecret = "626e879ba2c18e1a9c5f2fe7559ff667";
	public final String sina_AppKey = "2089751234";
	public final String sina_AppSecret = "d9119dbddd9556bc5623cc7fb7638c87";
	public final String sina_Redirect_url = "http://download.looklook.cn";//"http://www.sina.com";
	public final String sina_Authoriz_token_url = "https://api.weibo.com/oauth2/authorize"; //"https://open.weibo.cn/oauth2/authorize";

	//--------------------renren
	//API Key 
	/**
	 * 微享正式版：
	 * APP ID：246790
	 * API KEY：94076420462e444faf231e0ef7a938d7
	 * Secret Key：de91ad3bc8394235a25e4aec7c21127f
	 * 
	 * looklook：
	 * APP ID：234279
	 * API KEY：7018caa2bc644619bce8d99633de44d3
	 * Secret Key：9c50439ff7074cfa9997702175ef046e
	 * */
//	public final String renren_APPID = "234279";
//	public final String renren_AppKey = "6b1016db20c540e78bd1b20be4c707a3";
//	public final String renren_AppSecret = "4723a695c09e4ddebbe8d87393d95fb4";
	public final String renren_APPID = "246790";
	public final String renren_AppKey = "94076420462e444faf231e0ef7a938d7";
	public final String renren_AppSecret = "de91ad3bc8394235a25e4aec7c21127f";
	public final String renren_Redirect_url = "http://graph.renren.com/oauth/login_success.html"; //"http://download.looklook.cn";//"http://www.sina.com";
	public final String renren_Authoriz_token_url = "https://graph.renren.com/oauth/authorize";

	
    public static synchronized ConfigUtil getInstance() {
        if (instance == null) {
            instance = new ConfigUtil();
        }
        return instance;
    }
	
	private ConfigUtil(){
		
	}
	
	/**
	 * 初始化QQ认证信息
	 */
	public void initQqData() {
		setCurWeiboIndex(SHARE_TO.TENC.ordinal());
		setAppID(qq_AppID);
		setAppKey(qq_AppKey);
		setAppSecret(qq_AppSecret);
		this.setRedirectUrl(qq_Redirect_url);
		setAuthoriz_token_url(qq_Authoriz_token_url);
		setAccess_token_url(qq_Access_token_url);
    }
	
	/**
	 * 初始化SINA认证信息
	 */
	public void initSinaData() {
		setCurWeiboIndex(SHARE_TO.SINA.ordinal());
		setAppKey(sina_AppKey);
		setAppSecret(sina_AppSecret);
		this.setRedirectUrl(sina_Redirect_url);
		setAuthoriz_token_url(sina_Authoriz_token_url);
    }
	
	/**
	 * 初始化人人认证信息
	 */
	public void initRenrenData(){
		setCurWeiboIndex(SHARE_TO.RENREN.ordinal());
		setAppID(renren_APPID);
		setAppKey(renren_AppKey);
		setAppSecret(renren_AppSecret);
		this.setRedirectUrl(renren_Redirect_url);
		setAuthoriz_token_url(renren_Authoriz_token_url);
	}
	

	public int getCurWeiboIndex() {
    	return curWeiboIndex;
    }
	
	/**
	 * 设置当前操作的weibo
	 * 		不同的weibo请求存在着差异
	 * @param curWeibo
	 */
	public void setCurWeiboIndex(int curWeiboIndex) {
    	this.curWeiboIndex = curWeiboIndex;
    }
	
	public String getAppKey() {
    	return appKey;
    }

	public void setAppKey(String appKey) {
    	this.appKey = appKey;
    }
	
	public String getAppID(){
		return appID;
	}
	
	public void setAppID(String appID){
		this.appID = appID;
	}

	public String getAppSecret() {
    	return appSecret;
    }

	public void setAppSecret(String appSecret) {
    	this.appSecret = appSecret;
    }
	
	public String getRedirectUrl() {
    	return this.redirect_url ;
    }

	public void setRedirectUrl(String redirectUrl) {
    	this.redirect_url = redirectUrl;
    }
	
	public String getRequest_token_url() {
    	return request_token_url;
    }

	public void setRequest_token_url(String requestTokenUrl) {
    	request_token_url = requestTokenUrl;
    }
	
	public String getAuthoriz_token_url() {
    	return authoriz_token_url;
    }

	public void setAuthoriz_token_url(String authorizTokenUrl) {
    	authoriz_token_url = authorizTokenUrl;
    }

	public String getAccess_token_url() {
    	return access_token_url;
    }

	public void setAccess_token_url(String accessTokenUrl) {
    	access_token_url = accessTokenUrl;
    }
}
