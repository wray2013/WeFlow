package com.weibo.sdk.android.api;

import com.cmmobi.sns.constants.APIConstants;
import com.cmmobi.sns.oauth.OAuth;
import com.tencent.weibo.api.BasicAPI;

/**
 * 该类封装了用户接口，详情请参考<a href="http://open.weibo.com/wiki/API%E6%96%87%E6%A1%A3_V2#.E7.94.A8.E6.88.B7">用户接口</a>
 * @author xiaowei6@staff.sina.com.cn
 */
public class SWUsersAPI extends BasicAPI {
	
	public SWUsersAPI(String OAuthVersion) {
		super(OAuthVersion);
	}

	private static final String SERVER_URL_PRIX = APIConstants.SW_API_SERVER + "/users";

	/**
	 * 根据用户ID获取用户信息
	 * @param uid 需要查询的用户ID。
	 * @param listener
	 */
	public String show(OAuth oAuth,long uid)throws Exception {
		WeiboParameters params = new WeiboParameters();
		params.add("uid", uid);
		return requestAPI.getResource(SERVER_URL_PRIX + "/show.json", params,
					oAuth);
		//request( SERVER_URL_PRIX + "/show.json", params, HTTPMETHOD_GET, listener);
	}

	/**
	 * 根据用户ID获取用户信息
	 * @param screen_name 需要查询的用户昵称。
	 * @param listener
	 */
	public String show(OAuth oAuth, String screen_name)throws Exception {
		WeiboParameters params = new WeiboParameters();
		params.add("screen_name", screen_name);
		return requestAPI.getResource(SERVER_URL_PRIX + "/show.json", params,
				oAuth);
		//request( SERVER_URL_PRIX + "/show.json", params, HTTPMETHOD_GET, listener);
	}

	@Override
	public void setAPIBaseUrl(String apiBaseUrl) {
		
	}
}
