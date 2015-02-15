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

		WeiboParameters params = new WeiboParameters();
		params.add("userId", snsid);

		
		String ret = requestAPI.getResource("https://api.renren.com/v2/user/get", params, oAuth);
	
		return ret;
	}
	
	/**
	 * 根据用户ID获取用户信息
	 * http://wiki.dev.renren.com/wiki/V2/user/get
	 * @param snsid 需要查询的用户ID。

	 */
	public String show(OAuthV2 oAuth, String[] snsids)throws Exception {

		WeiboParameters params = new WeiboParameters();
		StringBuilder sb = new StringBuilder(); 
		if(snsids==null){
			return null;
		}
		if(snsids.length>1){
			sb.append(snsids[0]);
			for(int index=1; index<snsids.length; index++){
				sb.append(",");
				sb.append(snsids[index]);
			}
		}else{
			sb.append(snsids[0]);
		}

		
		params.add("userIds", sb.toString());

		
		String ret = requestAPI.getResource("https://api.renren.com/v2/user/batch", params, oAuth);
	
		return ret;
	}


	@Override
	public void setAPIBaseUrl(String apiBaseUrl) {
		
	}
}
