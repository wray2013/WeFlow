package com.sina.weibo.api;

import java.io.File;

import org.apache.http.message.BasicNameValuePair;

import android.text.TextUtils;

import com.cmmobi.sns.constants.APIConstants;
import com.cmmobi.sns.oauthv2.OAuthV2;
import com.cmmobi.sns.weibo.utils.QArrayList;
import com.weibo.sdk.android.api.BasicAPI;
import com.weibo.sdk.android.api.WeiboParameters;
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
	 * @return 
	 * [ 
	 *   {
	 *     "id": "32817222",
	 *     "comments": "16",
	 *     "reposts": "38"
	 *   },
	 *   ...
	 *   ]
	 * @param ids 需要获取数据的微博ID，最多不超过100个。
	 * @param listener
	 */
	public String count(OAuthV2 oAuth, String[] ids) throws Exception{
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
	public String uploadUrlText(OAuthV2 oAuth,String status, String imageUrl, String lat, String lon)throws Exception {
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
	
	public String uploadPicText(OAuthV2 oAuth,String status, String imagePath, String lat, String lon)throws Exception {
		WeiboParameters params = new WeiboParameters();
		params.add("status", status);
		if (!TextUtils.isEmpty(lon)) {
			params.add("long", lon);
		}
		if (!TextUtils.isEmpty(lat)) {
			params.add("lat", lat);
		}
		
		if(new File(imagePath).exists()){
			QArrayList pic = new QArrayList();
			pic.add(new BasicNameValuePair("pic", imagePath));
			return requestAPI.postFile(SERVER_URL_PRIX + "/upload.json", params, pic, oAuth);
		}else{
			return requestAPI.postContent(SERVER_URL_PRIX + "/upload_url_text.json", params, oAuth);
		}

		//request( SERVER_URL_PRIX + "/upload_url_text.json", params, HTTPMETHOD_POST, listener);
	}

	
	/**
	 * 根据微博ID删除指定微博
	 * 
	 * @param id 要删除的微博文本内容。
	 */
	public String destroyWeibo(OAuthV2 oAuth, String weiboID)throws Exception {
		WeiboParameters params = new WeiboParameters();
		params.add("id", Long.valueOf(weiboID));

		return requestAPI.postContent(SERVER_URL_PRIX + "/destroy.json", params, oAuth);	
	}
	
	
	@Override
	public void setAPIBaseUrl(String apiBaseUrl) {
		
	}
}
