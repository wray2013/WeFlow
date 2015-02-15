package com.renren.weibo.api;

import java.io.File;

import org.apache.http.message.BasicNameValuePair;

import com.cmmobi.sns.constants.APIConstants;
import com.cmmobi.sns.oauthv2.OAuthV2;
import com.cmmobi.sns.weibo.utils.QArrayList;
import com.weibo.sdk.android.api.BasicAPI;
/**
 * 该类封装了微博接口，详情请参考<a href="http://wiki.dev.renren.com/wiki/Feed.publishFeed">微博接口</a>
 * @author xiaowei6@staff.sina.com.cn
 */
public class RWStatusesAPI extends BasicAPI {
	
	public RWStatusesAPI(String OAuthVersion) {
		super(OAuthVersion);
	}

	private static final String SERVER_URL_PRIX = APIConstants.RW_API_SERVER;
	/**
	 * 批量获取指定微博的转发数评论数
	 * 
	 * @param ids 需要获取数据的微博ID，最多不超过100个。
	 * @param listener
	 */
	public String count(OAuthV2 oAuth, String[] ids) throws Exception{
		return null;
	}

	/**
	 * xxx发表新鲜事，可以带图片，及点击图片后的url
	 * http://wiki.dev.renren.com/wiki/V2/share/url/put
	 * 上传照片至用户相册
	 * @param oAuth 授权
	 * @param comment 分享时用户的评论，评论字数不能超过500个字符
	 * @param url 分享资源的URL
	 */
	public String uploadUrlText(OAuthV2 oAuth, String title, String url)throws Exception {
		QArrayList paramsList = new QArrayList();		
		paramsList.add(new BasicNameValuePair("comment", title));
		paramsList.add(new BasicNameValuePair("url", url));
		return requestAPI.postContent("https://api.renren.com/v2/share/url/put", paramsList, oAuth);
	}
	
	/**
	 *  上传图片，返回ugc的id
	 * */
	public String uploadPicText(OAuthV2 oAuth, String title, String imagePath)throws Exception {
		QArrayList paramsList = new QArrayList();		
		paramsList.add(new BasicNameValuePair("description", title));
		
		if(new File(imagePath).exists()){
			QArrayList pic = new QArrayList();
			pic.add(new BasicNameValuePair("file", imagePath));
			return requestAPI.postFile("https://api.renren.com/v2/photo/upload", paramsList, pic, oAuth);
		}else{
			return requestAPI.postContent("https://api.renren.com/v2/share/url/put", paramsList, oAuth);
		}
		
		
	}
	
	
	/**
	 *  发表带图片的微博
	 * */
	public String ugcPut(OAuthV2 oAuth, String comment, String ugc_id, String share_type)throws Exception {
		QArrayList paramsList = new QArrayList();		
		paramsList.add(new BasicNameValuePair("comment", comment));
		paramsList.add(new BasicNameValuePair("ugcOwnerId", oAuth.getOpenid()));
		paramsList.add(new BasicNameValuePair("ugcId", ugc_id));
		paramsList.add(new BasicNameValuePair("ugcType", share_type));
		
		
		return requestAPI.postContent("https://api.renren.com/v2/share/ugc/put", paramsList, oAuth);
		
	}
	
	/**
	 * 发表新鲜事，可以带图片，及点击图片后的url
	 * @param oAuth 授权
	 * @param type 分享的类型：当分享优酷视频、站外链接等人人网站外内容时，url为必须参数。此时type只能是：链接为6、视频为10、音频为11。
	 *  日志为1、照片为2、链接为6、相册为8、视频为10、音频为11、分享为20。
	 * @param url 分享人人网站外内容的URL。
	 * @param comment 分享内容时，用户的评论内容。
	 * @param source_link 新鲜事中来源信息链接，连接的名字默认为App的名字。该参数为JSON格式，格式如下：{ "text": "App A", "href": "http://appa.com/path"。

	 */
	public String Share(OAuthV2 oAuth, String type, String url, String comment, String source_link)throws Exception {
		QArrayList paramsList = new QArrayList();
		paramsList.add(new BasicNameValuePair("method", "share.share"));
		paramsList.add(new BasicNameValuePair("format", "json"));
		paramsList.add(new BasicNameValuePair("type", type));
		paramsList.add(new BasicNameValuePair("url", url));
		paramsList.add(new BasicNameValuePair("comment", comment));
		paramsList.add(new BasicNameValuePair("source_link", source_link));

		
		return requestAPI.postContent(SERVER_URL_PRIX, paramsList, oAuth);
	}

	@Override
	public void setAPIBaseUrl(String apiBaseUrl) {
		
	}
}
