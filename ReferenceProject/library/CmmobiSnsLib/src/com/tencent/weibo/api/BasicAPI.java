package com.tencent.weibo.api;

import com.cmmobi.sns.constants.APIConstants;
import com.cmmobi.sns.constants.OAuthConstants;
import com.cmmobi.sns.oauthv2.OAuthV2Request;
import com.cmmobi.sns.weibo.utils.QHttpClient;

/**
 * API类的通用部分，所有第三方分享平台api都要继承该类
 * @author xudongsheng
 *
 */
public abstract class BasicAPI {
    
    protected TWRequestAPI requestAPI;
    protected String apiBaseUrl=null;

    public BasicAPI(String OAuthVersion){
      if(OAuthVersion == OAuthConstants.OAUTH_VERSION_2_A){
            requestAPI = new OAuthV2Request();
            apiBaseUrl=APIConstants.TW_API_V2_BASE_URL;
        }
    }
    
    public BasicAPI(String OAuthVersion, QHttpClient qHttpClient){
    	if(OAuthVersion == OAuthConstants.OAUTH_VERSION_2_A){
            requestAPI = new OAuthV2Request(qHttpClient);
            apiBaseUrl=APIConstants.TW_API_V2_BASE_URL;
        }
    }
    
    public void shutdownConnection(){
        requestAPI.shutdownConnection();
    }

    public String getAPIBaseUrl() {
        return apiBaseUrl;
    }

    public abstract  void setAPIBaseUrl(String apiBaseUrl);
    
}
