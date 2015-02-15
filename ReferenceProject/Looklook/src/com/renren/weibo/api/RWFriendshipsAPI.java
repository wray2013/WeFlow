package com.renren.weibo.api;

import com.cmmobi.sns.constants.APIConstants;
import com.cmmobi.sns.oauthv2.OAuthV2;
import com.weibo.sdk.android.api.BasicAPI;
import com.weibo.sdk.android.api.WeiboParameters;

/**
 *  * 此类封装了关系的接口，详情见<a href=http://wiki.dev.renren.com/wiki/Friends.getFriends">关系接口</a>
 * @author zhangwei@cmmobi.com
 *
 */
public class RWFriendshipsAPI extends BasicAPI {
	
	public RWFriendshipsAPI(String OAuthVersion) {
		super(OAuthVersion);
	}
	
	private static final String SERVER_URL_PRIX = APIConstants.RW_API_SERVER;
	
	/**
	 * 获取用户的双向关注列表，即互粉列表
	 * 
	 * http://wiki.dev.renren.com/wiki/V2/user/friend/list
	 * 
	 * @param uid 需要获取双向关注列表的用户UID。
	 * @param pageSize 页面大小。取值范围1-100，默认大小20
	 * @param pageNumber 页码。取值大于零，默认值为1
	 */
	public String bilateral(OAuthV2 oAuth, long uid, int pageSize, int pageNumber) throws Exception{

/*		QArrayList paramsList = new QArrayList();
		paramsList.add(new BasicNameValuePair("method", "friends.getFriends"));
		paramsList.add(new BasicNameValuePair("format", "json"));
		paramsList.add(new BasicNameValuePair("page", String.valueOf(page)));
		paramsList.add(new BasicNameValuePair("count", String.valueOf(count)));

		
		return requestAPI.postContent(SERVER_URL_PRIX, paramsList, oAuth);*/
		WeiboParameters params = new WeiboParameters();
		params.add("userId", uid);
		params.add("pageSize", pageSize);
		params.add("pageNumber", pageNumber);
		
		return requestAPI.getResource("https://api.renren.com/v2/user/friend/list", params, oAuth);
	}
	
	@Override
	public void setAPIBaseUrl(String apiBaseUrl) {
		
	}
}
