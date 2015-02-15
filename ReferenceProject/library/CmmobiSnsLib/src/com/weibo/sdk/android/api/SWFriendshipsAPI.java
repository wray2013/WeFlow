package com.weibo.sdk.android.api;

import com.cmmobi.sns.constants.APIConstants;
import com.cmmobi.sns.oauth.OAuth;
import com.tencent.weibo.api.BasicAPI;

/**
 *  * 此类封装了关系的接口，详情见<a href=http://open.weibo.com/wiki/API%E6%96%87%E6%A1%A3_V2#.E5.85.B3.E7.B3.BB">关系接口</a>
 * @author xiaowei6@staff.sina.com.cn
 *
 */
public class SWFriendshipsAPI extends BasicAPI {
	
	public SWFriendshipsAPI(String OAuthVersion) {
		super(OAuthVersion);
	}
	private static final String SERVER_URL_PRIX = APIConstants.SW_API_SERVER + "/friendships";
	/**
	 * 获取用户的双向关注列表，即互粉列表
	 * 
	 * @param uid 需要获取双向关注列表的用户UID。
	 * @param count 单页返回的记录条数，默认为50。
	 * @param page 返回结果的页码，默认为1。
	 * @param listener
	 */
	public String bilateral(OAuth oAuth, long uid, int count, int page) throws Exception{
		WeiboParameters params = new WeiboParameters();
		params.add("uid", uid);
		params.add("count", count);
		params.add("page", page);
		return requestAPI.getResource(SERVER_URL_PRIX + "/friends/bilateral.json", params,
					oAuth);
//		request( SERVER_URL_PRIX + "/friends/bilateral.json", params, HTTPMETHOD_GET,
//				listener);
	}
	@Override
	public void setAPIBaseUrl(String apiBaseUrl) {
		
	}
}
