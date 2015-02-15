package com.cmmobi.sns.keep;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.SparseArray;

import com.cmmobi.sns.api.CmmobiSnsLib;
import com.cmmobi.sns.constants.OAuthConstants;
import com.cmmobi.sns.oauthv2.OAuthV2;
import com.cmmobi.sns.utils.ConfigUtil;
import com.cmmobi.sns.utils.ConfigUtil.SHARE_TO;
import com.cmmobi.sns.weibo.utils.QStrOperate;
/**
 * 该类用于保存OAuthV2到sharepreference，并提供读取功能
 * @author xudongsheng
 *
 */
public class AccessTokenKeeper {
	private static final String PREFERENCES_NAME = "com_weibo_sdk_android";
	/**
	 * 保存accesstoken到SharedPreferences
	 * @param context Activity 上下文环境
	 * @param oAuth OAuthV2
	 */
	public static void keepAccessToken(Context context, OAuthV2 oAuth) {
		SharedPreferences pref = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_APPEND);
		Editor editor = pref.edit();
		int curWeiboIndex = oAuth.getCurWeiboIndex();
		if(curWeiboIndex == SHARE_TO.SINA.ordinal()){
			editor.putString("sw_accessToken",oAuth.getAccessToken());
			editor.putString("sw_expiresIn",oAuth.getExpiresIn());
			editor.putString("sw_openId",oAuth.getOpenid());
		}else if(curWeiboIndex == SHARE_TO.TENC.ordinal()){
			editor.putString("tw_accessToken",oAuth.getAccessToken());
			editor.putString("tw_expiresIn",oAuth.getExpiresIn());
			editor.putString("tw_openId",oAuth.getOpenid());
		}
		editor.commit();
	}
	/**
	 * 清空sharepreference
	 * @param context
	 */
	public static void clear(Context context){
	    SharedPreferences pref = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_APPEND);
	    Editor editor = pref.edit();
	    editor.clear();
	    editor.commit();
	}

	/**
	 * 从SharedPreferences读取accessstoken
	 * @param context
	 * @return OAuthV2
	 */
	public static OAuthV2 readAccessToken(Context context, int curWeiboIndex){
		  OAuthV2 oAuth=new OAuthV2();
		  SharedPreferences pref = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_APPEND);
			if(curWeiboIndex == SHARE_TO.SINA.ordinal()){
				oAuth.setAccessToken(pref.getString("sw_accessToken", ""));
				oAuth.setExpiresIn(pref.getString("sw_expiresIn", ""));
				oAuth.setOpenid(pref.getString("sw_openId", ""));
			}else if(curWeiboIndex == SHARE_TO.TENC.ordinal()){
				oAuth.setAccessToken(pref.getString("tw_accessToken", ""));
				oAuth.setOauthVersion(OAuthConstants.OAUTH_VERSION_2_A);
				oAuth.setClientId(ConfigUtil.getInstance().qq_AppKey);
				oAuth.setExpiresIn(pref.getString("tw_expiresIn", ""));
				oAuth.setOpenid(pref.getString("tw_openId", ""));
			}
			if(QStrOperate.hasValue(oAuth.getAccessToken()) && QStrOperate.hasValue(oAuth.getOpenid())){
				updateGlobalOAuthCache(context, curWeiboIndex, oAuth);
			}
		  return oAuth;
	}
	/**
	 * 更新全局缓存的oAuthV2Map
	 * @param context
	 * @param curWeiboIndex
	 * @param oAuth
	 */
	public static void updateGlobalOAuthCache(Context context,int curWeiboIndex, OAuthV2 oAuth){
		CmmobiSnsLib snsLib = CmmobiSnsLib.getInstance(context);
		snsLib.updateOAuthV2Map(curWeiboIndex, oAuth);
	}

}
