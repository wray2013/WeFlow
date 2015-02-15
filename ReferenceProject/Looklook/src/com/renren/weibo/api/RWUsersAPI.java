package com.renren.weibo.api;

import com.cmmobi.sns.constants.APIConstants;
import com.cmmobi.sns.oauthv2.OAuthV2;
import com.weibo.sdk.android.api.BasicAPI;
import com.weibo.sdk.android.api.WeiboParameters;

/**
 * 该类封装了用户接口，详情请参考<a href="http://open.weibo.com/wiki/API%E6%96%87%E6%A1%A3_V2#.E7.94.A8.E6.88.B7">用户接口</a>
 * @author xiaowei6@staff.sina.com.cn
 */
public class RWUsersAPI extends BasicAPI {
	
	public RWUsersAPI(String OAuthVersion) {
		super(OAuthVersion);
	}

	private static final String SERVER_URL_PRIX = APIConstants.RW_API_SERVER;

	/**
	 * 根据用户ID获取用户信息
	 * http://wiki.dev.renren.com/wiki/V2/user/get
	 * @param snsid 需要查询的用户ID。

	 */
	public String show(OAuthV2 oAuth, long snsid)throws Exception {

/*		QArrayList paramsList = new QArrayList();
		paramsList.add(new BasicNameValuePair("uids", String.valueOf(uid)));
		paramsList.add(new BasicNameValuePair("method", "users.getInfo"));
		paramsList.add(new BasicNameValuePair("format", "json"));

		
		return requestAPI.postContent(SERVER_URL_PRIX, paramsList, oAuth);*/
		WeiboParameters params = new WeiboParameters();
		params.add("userId", snsid);

		
		String ret = requestAPI.getResource("https://api.renren.com/v2/user/get", params, oAuth);
	
		return ret;
	}


	@Override
	public void setAPIBaseUrl(String apiBaseUrl) {
		
	}
}
