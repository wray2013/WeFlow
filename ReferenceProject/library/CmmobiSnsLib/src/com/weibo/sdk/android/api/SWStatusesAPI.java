package com.weibo.sdk.android.api;

import com.cmmobi.sns.constants.APIConstants;
import com.cmmobi.sns.oauth.OAuth;
import com.tencent.weibo.api.BasicAPI;

import android.text.TextUtils;
/**
 * 该类封装了微博接口，详情请参考<a href="http://open.weibo.com/wiki/API%E6%96%87%E6%A1%A3_V2#.E5.BE.AE.E5.8D.9A">微博接口</a>
 * @author xiaowei6@staff.sina.com.cn
 */
public class SWStatusesAPI extends BasicAPI {
	
	public SWStatusesAPI(String OAuthVersion) {
		super(OAuthVersion);
	}

	private static final String SERVER_URL_PRIX = APIConstants.SW_API_SERVER + "/statuses";
	/**
	 * 批量获取指定微博的转发数评论数
	 * 
	 * @param ids 需要获取数据的微博ID，最多不超过100个。
	 * @param listener
	 */
	public String count(OAuth oAuth, String[] ids) throws Exception{
		WeiboParameters params = new WeiboParameters();
		StringBuilder strb = new StringBuilder();
		for (String id : ids) {
			strb.append(id).append(",");
		}
		strb.deleteCharAt(strb.length() - 1);
		params.add("ids", strb.toString());
		 return requestAPI.getResource(SERVER_URL_PRIX + "/count.json", params,
					oAuth);
		//request( SERVER_URL_PRIX + "/count.json", params, HTTPMETHOD_GET, listener);
	}

	/**
	 * 指定一个图片URL地址抓取后上传并同时发布一条新微博，此方法会处理URLencode
	 * 
	 * @param status 要发布的微博文本内容，内容不超过140个汉字。
	 * @param imageUrl 图片的URL地址，必须以http开头。
	 * @param lat 纬度，有效范围：-90.0到+90.0，+表示北纬，默认为0.0。
	 * @param lon 经度，有效范围：-180.0到+180.0，+表示东经，默认为0.0。
	 * @param listener
	 */
	public String uploadUrlText(OAuth oAuth,String status, String imageUrl, String lat, String lon)throws Exception {
		WeiboParameters params = new WeiboParameters();
		params.add("status", status);
		params.add("url", imageUrl);
		if (!TextUtils.isEmpty(lon)) {
			params.add("long", lon);
		}
		if (!TextUtils.isEmpty(lat)) {
			params.add("lat", lat);
		}
		 return requestAPI.postContent(SERVER_URL_PRIX + "/upload_url_text.json", params,
					oAuth);
		//request( SERVER_URL_PRIX + "/upload_url_text.json", params, HTTPMETHOD_POST, listener);
	}

	@Override
	public void setAPIBaseUrl(String apiBaseUrl) {
		
	}
}
