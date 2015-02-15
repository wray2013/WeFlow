package com.cmmobi.sns.utils;

/**
 * 获取配置文件
 * @author xudongsheng
 *
 */
public  class ConfigUtil {
	
	private static ConfigUtil instance;
	
	 public static enum SHARE_TO
	  {
	    SINA, TENC, QZONE,RENR,KAIXIN,JIEPANG;
	  }	
	
	private int curWeiboIndex;
	private String appKey = "";
	private String appSecret = "";
	private String request_token_url = "";
	private String authoriz_token_url = "";
	private String access_token_url = ""; 
	private String redirect_url="";
	
	public static final String SINAW = "sina";
	public static final String QQW = "qq";
	public static final String ShareToSina = "sohu";
	public static final String ShareToTencent = "wangyi";
	
	//--------------------qq
	public final String qq_AppKey = "801209961";
	private final String qq_AppSecret = "4a6ec7ddcb4605a336559bbdd651c489";
	private final String qq_Redirect_url = "http://download.looklook.cn";//"http://weibo.com/u/2801678985";
//	private final String qq_Request_token_url = "https://open.t.qq.com/cgi-bin/request_token";
	private final String qq_Authoriz_token_url = "https://open.t.qq.com/cgi-bin/oauth2/authorize";
	private final String qq_Access_token_url = "https://open.t.qq.com/cgi-bin/oauth2/access_token";
	
	//---------------------sina
	private final String sina_AppKey = "2061544481";
	private final String sina_AppSecret = "626e879ba2c18e1a9c5f2fe7559ff667";
	private final String sina_Redirect_url = "http://download.looklook.cn";//"http://www.sina.com";
	//private final String sina_Request_token_url = "http://api.t.sina.com.cn/oauth/request_token";
	private final String sina_Authoriz_token_url = "https://open.weibo.cn/oauth2/authorize";
	//private final String sina_Access_token_url = "http://api.t.sina.com.cn/oauth/access_token";
	
//	//--------------------sohu
//	//API Key btBPDWJxYfTt0cGUzazy
//	private final String sohu_AppKey = "btBPDWJxYfTt0cGUzazy";
//	private final String sohu_AppSecret = "rruL^5)AvXs0CAvcp!KdL49r#bS6Zg9UQI)-%l6m";
//	private final String sohu_Request_token_url = "http://api.t.sohu.com/oauth/request_token";
//	private final String sohu_Authoriz_token_url = "http://api.t.sohu.com/oauth/authorize";
//	private final String sohu_Access_token_url = "http://api.t.sohu.com/oauth/access_token";
//	
//	//---------------------wangyi
//	private final String wangyi_AppKey = "auGQI7TIYStR469C";
//	private final String wangyi_AppSecret = "kDvUXEaA9Y0DEmlm7jdGAuCOtHgIfaHX";
//	private final String wangyi_Request_token_url = "http://api.t.163.com/oauth/request_token";
//	private final String wangyi_Authoriz_token_url = "http://api.t.163.com/oauth/authenticate";
//	private final String wangyi_Access_token_url = "http://api.t.163.com/oauth/access_token";
	
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
		setAppKey(qq_AppKey);
		setAppSecret(qq_AppSecret);
		this.setRedirectUrl(qq_Redirect_url);
		//setRequest_token_url(qq_Request_token_url);
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
		//setRequest_token_url(sina_Request_token_url);
		setAuthoriz_token_url(sina_Authoriz_token_url);
		//setAccess_token_url(sina_Access_token_url);
    }
	
	/**
	 * 初始化SOHU认证信息
	 */
//	public void initSohuData() {
//		setAppKey(sohu_AppKey);
//		setAppSecret(sohu_AppSecret);
//		setRequest_token_url(sohu_Request_token_url);
//		setAuthoriz_token_url(sohu_Authoriz_token_url);
//		setAccess_token_url(sohu_Access_token_url);
//    }
//	
//	/**
//	 * 初始化网易认证信息
//	 */
//	public void initWangyiData(){
//		setAppKey(wangyi_AppKey);
//		setAppSecret(wangyi_AppSecret);
//		setRequest_token_url(wangyi_Request_token_url);
//		setAuthoriz_token_url(wangyi_Authoriz_token_url);
//		setAccess_token_url(wangyi_Access_token_url);
//	}
	
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
