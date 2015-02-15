package com.cmmobi.sns.keep;

import android.content.Context;
import android.util.Log;

import com.cmmobi.looklook.common.gson.GsonResponse3;
import com.cmmobi.looklook.info.profile.AccountInfo;
import com.cmmobi.looklook.info.profile.ActiveAccount;
import com.cmmobi.sns.api.QQMobileAuth;
import com.cmmobi.sns.oauthv2.OAuthV2;
import com.cmmobi.sns.utils.ConfigUtil;
import com.cmmobi.sns.utils.ConfigUtil.SHARE_TO;

/**
 * 该类用于保存OAuthV2到sharepreference，并提供读取功能
 * @author zhangwei
 *
 */
public class AccessTokenKeeper {

	public static GsonResponse3.MyBind tmp = null;
	/**
	 * 保存accesstoken到SharedPreferences
	 * @param context Activity 上下文环境
	 * @param uid 用户uid
	 * @param oAuth OAuthV2
	 */
	public static void keepAccessToken(Context c, OAuthV2 oAuth) {
		int curWeiboIndex = oAuth.getCurWeiboIndex();
		keepAccessToken(c, oAuth, curWeiboIndex);
		
	}
	
	/**
	 * 保存accesstoken到SharedPreferences
	 * @param context Activity 上下文环境
	 * @param uid 用户uid
	 * @param oAuth OAuthV2
	 */
	public static void keepAccessToken(Context context, OAuthV2 oAuth, int curWeiboIndex) {
		if(oAuth==null){
			return;
		}
		
		GsonResponse3.MyBind a = new GsonResponse3.MyBind(curWeiboIndex, oAuth);
		ActiveAccount AA = ActiveAccount.getInstance(context);

		if (AA.isLogin()) {
			// 登录状态, 直接在AccountInfo中更新
			AccountInfo.getInstance(AA.getUID()).updateAccessToken(a);

		} else {
			// 没有登录，先记到tmp中，登录后（拿到userid后）再更新
			tmp = a;

			// not login, save it to account
			AA.snsid = oAuth.getOpenid();
			if (curWeiboIndex == SHARE_TO.SINA.ordinal()) {
				AA.snstype = "1";
			} else if (curWeiboIndex == SHARE_TO.TENC.ordinal()) {
				AA.snstype = "6";
			} else if (curWeiboIndex == SHARE_TO.RENREN.ordinal()) {
				AA.snstype = "2";
			} else if (curWeiboIndex == SHARE_TO.QQMOBILE.ordinal()) {
				AA.snstype = "13";
			}else {
				AA.snstype = "x";
			}

		}
	}
	
	/**
	 * 清空sharepreference
	 * @param context
	 * @param uid 用户uid
	 */
	public static void clear(Context context, String uid){

	}

	/**
	 * 从SharedPreferences读取accessstoken
	 * @param context
	 * @param uid 用户uid
	 * @return OAuthV2
	 */
	public static OAuthV2 readAccessToken(Context context, int curWeiboIndex){

		  Log.e("OAuthV2", "readAccessToken");
		  ActiveAccount AA = ActiveAccount.getInstance(context);
		  
		  OAuthV2 oAuth = null; //new OAuthV2();
			
		  if(AA.isLogin()){
			 //登录状态, 直接在AccountInfo中更新
			  String type = null;
			  if(curWeiboIndex == SHARE_TO.SINA.ordinal()){
				  type = "1";
			  }else if(curWeiboIndex == SHARE_TO.TENC.ordinal()){
				  type = "6";
			  }else if(curWeiboIndex == SHARE_TO.RENREN.ordinal()){
				  type = "2";
			  }else if(curWeiboIndex == SHARE_TO.QQMOBILE.ordinal()){
				  type = "13";
			  }
			  GsonResponse3.MyBind at = AccountInfo.getInstance(AA.getUID()).readAccessToken("3", "3", type);
			  if(at!=null){
				  oAuth = new OAuthV2();
				  oAuth.setOpenid(at.snsuid);
				  oAuth.setExpiresIn(at.sns_expiration_time, at.sns_expiration_time);
				  oAuth.setAccessToken(at.sns_token);
				  oAuth.setRefreshToken(at.sns_refresh_token);
				  oAuth.setNick(at.sns_nickname);
				  if("1".equals(at.snstype)){
					  oAuth.setClientId(ConfigUtil.getInstance().sina_AppKey); 
				  }else if("2".equals(at.snstype)){
					  oAuth.setClientId(ConfigUtil.getInstance().renren_AppKey); 
				  }else if("6".equals(at.snstype)){
					  oAuth.setClientId(ConfigUtil.getInstance().qq_AppKey); 
				  }else if("13".equals(at.snstype)){
					  oAuth.setClientId(QQMobileAuth.QQMOBILE_APPKEY); 
				  }
				  oAuth.setOpenkey(at.sns_openkey);
			  }
				
		  }else{
			 //没有登录，返回null
			 return null;
		  }
		  

		  return oAuth;
	}
	
	/**
	 * 更新全局缓存的oAuthV2Map
	 * @param context
	 * @param uid
	 * @param curWeiboIndex
	 * @param oAuth
	 */
	public static void updateGlobalOAuthCache(Context context, int curWeiboIndex, OAuthV2 oAuth){

	}

}
