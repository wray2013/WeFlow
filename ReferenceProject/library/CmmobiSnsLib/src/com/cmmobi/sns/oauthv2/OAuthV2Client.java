package com.cmmobi.sns.oauthv2;

import android.content.Context;
import android.util.Log;
import android.util.SparseArray;

import com.cmmobi.sns.api.CmmobiSnsLib;
import com.cmmobi.sns.constants.OAuthConstants;
import com.cmmobi.sns.exceptions.OAuthClientException;
import com.cmmobi.sns.keep.AccessTokenKeeper;
import com.cmmobi.sns.utils.ConfigUtil.SHARE_TO;
import com.cmmobi.sns.weibo.utils.QHttpClient;
import com.cmmobi.sns.weibo.utils.QStrOperate;

/**
 * 工具类 OAuth version 2 认证授权以及签名相关<br>
 * 如需自定制http管理器请使用 <pre>OAuthV2Client.setQHttpClient(QHttpClient qHttpClient)</pre> <br>
 * 为本工具类指定http管理器
 */
public class OAuthV2Client{

    private static String TAG = OAuthV2Client.class.getSimpleName();
    
    private static QHttpClient Q_HTTP_CLIENT=new QHttpClient();
   
    private OAuthV2Client(){
    }
    
    /**
     * 使用Authorization code方式鉴权时，合成转向授权页面的url
     * 
     * @param oAuth
     * @return
     */
    public  static  String generateAuthorizationURL(OAuthV2 oAuth) {

        oAuth.setResponseType("code");
        String queryString = QStrOperate.getQueryString(oAuth.getAuthorizationParamsList(oAuth));
        Log.i(TAG,"authorization queryString = "+queryString);
        
        String urlWithQueryString=OAuthConstants.OAUTH_V2_AUTHORIZE_URL + "?"+queryString;
        Log.i(TAG,"url with queryString = "+ urlWithQueryString);
        
        return urlWithQueryString;
    }
    
    /**
     * 使用Authorization code方式鉴权时，直接将授权码的参数记录到OAuth类中
     * @param authorizeCode
     * @param openid
     * @param openkey
     * @param oAuth
     * @return
     */
    public  static boolean setAuthorization(String authorizeCode,String openid,String openkey,OAuthV2 oAuth){
        if ((!QStrOperate.hasValue(authorizeCode)) ||
                (!QStrOperate.hasValue(openid)) ||
                (!QStrOperate.hasValue(openkey))) {
            return false;
        }
        oAuth.setAuthorizeCode(authorizeCode);
        oAuth.setOpenid(openid);
        oAuth.setOpenkey(openkey);
        return true;
    }
    
    /**
     * 使用Authorization code方式鉴权时，请求用户授权后，解析开放平台返回的参数是否包含授权码等信息
     * 
     * @param responseData 格式：code=CODE&openid=OPENID&openkey=OPENKEY
     * @param oAuth
     * @return
     */
     public  static boolean parseAuthorization(String responseData, OAuthV2 oAuth) {
         oAuth.setStatus(2);//假设出错
         if (!QStrOperate.hasValue(responseData)) {
            return false;
        }

        oAuth.setMsg(responseData);
        String[] tokenArray = responseData.split("&");
        
        Log.i(TAG, "parseToken response=>> tokenArray.length = "+tokenArray.length);
        
        if (tokenArray.length < 3) {
            return false;
        }

        String strAuthorizeCode = tokenArray[0];
        String strOpenid = tokenArray[1];
        String strOpenkey = tokenArray[2];

        String[] authorizeCode = strAuthorizeCode.split("=");
        if (authorizeCode.length < 2) {
            return false;
        }
        oAuth.setAuthorizeCode(authorizeCode[1]);

        String[] openid = strOpenid.split("=");
        if (openid.length < 2) {
            return false;
        }
        oAuth.setOpenid(openid[1]);
         
        String[] openkey = strOpenkey.split("=");
        if (openkey.length < 2) {
            return false;
        }
        oAuth.setOpenkey(openkey[1]);
        oAuth.setStatus(0);//没有出错
        return true;
    }
    
    /**
     * 使用Authorization code方式鉴权时，用授权码换取Access Token
     * 
     * @param oAuth
     * @return
     * @throws Exception
     */
//    public  static boolean accessToken(OAuthV2 oAuth) throws Exception {
//        if(null==Q_HTTP_CLIENT){
//            throw new OAuthClientException("1001");
//        }
//        Log.i(TAG, "AuthorizeCode = "+oAuth.getAuthorizeCode()+
//                "\nOpenid = "+oAuth.getOpenid()+ "\nOpenkey ="+oAuth.getOpenkey());
//        
//        String url = OAuthConstants.OAUTH_V2_GET_ACCESS_TOKEN_URL;
//
//        String queryString = QStrOperate.getQueryString(oAuth.getAccessTokenByCodeParamsList());
//        Log.i(TAG,"authorization queryString = "+queryString);
//        
//        String responseData = Q_HTTP_CLIENT.httpGet(url, queryString);
//        Log.i(TAG,"authorization responseData = "+responseData);
//        
//        if (!parseAccessToken(responseData, oAuth)) {// Access Token 授权不通过
//            oAuth.setStatus(3);
//            return false;
//        }else{
//            return true;
//        }
//    }
    
    /**
     * 使用Implicit grant方式鉴权时，合成转向授权页面的url
     * @param oAuth
     * @return 
     */
    public static String generateImplicitGrantUrl(OAuthV2 oAuth){
      oAuth.setResponseType("token");
      String queryString = QStrOperate.getQueryString(oAuth.getAuthorizationParamsList(oAuth));
      Log.i(TAG,"authorization queryString = "+queryString);
      
      String urlWithQueryString=oAuth.getAuthorizeUrl() + "?"+queryString;
      Log.i(TAG,"url with queryString = "+ urlWithQueryString);
      
      return urlWithQueryString;
    }
    
    
    /**
     * 得到服务器返回的包含access token等的回应包后，解析存储到OAuth类中
     * 
     * @param responseData 格式：access_token=ACCESS_TOKEN&expires_in=60&name=NAME
     * @param oAuth
     * @return
     */
//     public  static boolean parseAccessToken(String responseData, OAuthV2 oAuth){
//        if (!QStrOperate.hasValue(responseData)) {
//            return false;
//        }
//
//        oAuth.setMsg(responseData);
//        String[] tokenArray = responseData.split("&");
//        
//        Log.i(TAG, "parseToken response=>> tokenArray.length = "+tokenArray.length);
//
//        if (tokenArray.length < 2) {
//            return false;
//        }
//
//        String strAccessToken = tokenArray[0];
//        String strExpiresIn = tokenArray[1];
//
//        String[] accessToken = strAccessToken.split("=");
//        if (accessToken.length < 2) {
//            return false;
//        }
//        oAuth.setAccessToken(accessToken[1]);
//         
//        String[] expiresIn = strExpiresIn.split("=");
//        if (expiresIn.length < 2) {
//            return false;
//        }
//        oAuth.setExpiresIn(expiresIn[1]);
//        
//        return true;
//    }

    /**
     * 得到服务器返回的包含access token等的回应包后，解析存储到OAuth类中
     * 
     * @param responseData 格式：access_token=ACCESS_TOKEN&expires_in=60&openid= OPENID &openkey= OPENKEY
     * @param oAuth
     * @return
     */
     public  static boolean parseAccessTokenAndOpenId(Context context, String responseData, OAuthV2 oAuth){
         /* 腾讯微博
          * http://weibo.com/u/2801678985#
          * access_token=a64a9a0e9d4ecdfe7f7b03a4af56c34b&
       	 expires_in=604800&
       	 openid=E5B0880AD0D0B5C5F6850BF2E89434FC&
       	 openkey=74E058E91F8B3BD0775FA6EFA8664B81&
       	 refresh_token=5cf742b9dc9acb13f98b04ad328d3977&
       	 name=looklook6197&
       	 nick=looklook*/
       	/* 新浪微博
       	 URL = http://www.sina.com/#access_token=2.00jgYbDD8tBWPCea304e3ac4o5uJ8C&
       		 remind_in=157679999&
       		 expires_in=157679999&
       		 uid=2801678985
       	*/
    	 int curWeiboIndex = oAuth.getCurWeiboIndex();
         oAuth.setStatus(3);//假设出错
         if (!QStrOperate.hasValue(responseData)) {
             return false;
         }
         oAuth.setMsg(responseData);
         String[] tokenArray = responseData.split("&");
         Log.i(TAG, "parseToken response=>> tokenArray.length = "+tokenArray.length);
         if (tokenArray.length < 4) {
             return false;
         }
         String strAccessToken = tokenArray[0];
         String strExpiresIn = "";
         String  strOpenid = "";
         String strOpenKey = "";
         if(curWeiboIndex == SHARE_TO.SINA.ordinal()){  //新浪微博
            strExpiresIn = tokenArray[2];
            strOpenid = tokenArray[3];
            Log.i(TAG, "---sina weibo, strAccessToken--strExpiresIn---strOpenid"
           		 + strAccessToken + strExpiresIn + strOpenid);
           // ---sina weibo, strAccessToken--strExpiresIn---strOpenidaccess_token=2.00jgYbDD8tBWPCea304e3ac4o5uJ8Cexpires_in=157610118uid=2801678985
         }else if(curWeiboIndex == SHARE_TO.TENC.ordinal()){//腾讯微博
        	 strExpiresIn = tokenArray[1];
        	 strOpenid = tokenArray[2];
        	 strOpenKey = tokenArray[3];
        	 Log.i(TAG, "---tencent weibo, strAccessToken--strExpiresIn---strOpenid---strOpenKey"
            		 + strAccessToken + strExpiresIn + strOpenid + strOpenKey);
         }
//         ---tencent weibo, strAccessToken--strExpiresIn---strOpenid---
//         strOpenKeyaccess_token=ff6b438e6eb50c88a88691e6c60b8ec6
//         expires_in=604800openid=E5B0880AD0D0B5C5F6850BF2E89434FCopenkey=1FEB31AC093C7925D2A4C8A98F9B9D4B      
         String[] accessToken = strAccessToken.split("=");
         if (accessToken.length < 2) {
             return false;
         }
         oAuth.setAccessToken(accessToken[1]);
          
         String[] expiresIn = strExpiresIn.split("=");
         if (expiresIn.length < 2) {
             return false;
         }
         oAuth.setExpiresIn(expiresIn[1]);

         String[] openid = strOpenid.split("=");
         if (openid.length < 2) {
             return false;
         }
         oAuth.setOpenid(openid[1]);
         if(QStrOperate.hasValue(strOpenKey)){
        	 String[] openkey = strOpenKey.split("=");
             if (openkey.length < 2) {
                 return false;
             }
             oAuth.setOpenkey(openkey[1]);  
         }
         
         //持久化
        AccessTokenKeeper.keepAccessToken(context, oAuth);
        //授权成功后,将Oauth实例放到一个全局的Hashmap中，方便，分享时候取出
        CmmobiSnsLib.getInstance(context).updateOAuthV2Map(curWeiboIndex, oAuth);
        oAuth.setStatus(0);
        return true;
    }

    public static QHttpClient getQHttpClient() {
        return Q_HTTP_CLIENT;
    }

    public static void setQHttpClient(QHttpClient qHttpClient) {
        Q_HTTP_CLIENT = qHttpClient;
    }


}
