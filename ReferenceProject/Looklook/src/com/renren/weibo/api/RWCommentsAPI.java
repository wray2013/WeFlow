package com.renren.weibo.api;

import org.apache.http.message.BasicNameValuePair;

import com.cmmobi.sns.constants.APIConstants;
import com.cmmobi.sns.oauthv2.OAuthV2;
import com.cmmobi.sns.weibo.utils.QArrayList;
import com.cmmobi.sns.weibo.utils.QHttpClient;
import com.weibo.sdk.android.api.BasicAPI;
import com.weibo.sdk.android.api.WeiboParameters;
/**
 * 此类封装了评论的接口，详情见<a href="http://open.weibo.com/wiki/API%E6%96%87%E6%A1%A3_V2#.E8.AF.84.E8.AE.BA">评论接口</a>
 * @author xiaowei6@staff.sina.com.cn
 *
 */
public class RWCommentsAPI extends BasicAPI {
	
	 private static final String SERVER_URL_PRIX = APIConstants.RW_API_SERVER;
	 
      /**
	     * 使用完毕后，请调用 shutdownConnection() 关闭自动生成的连接管理器
	     * @param OAuthVersion 根据OAuthVersion，配置通用请求参数
	     */
	    public RWCommentsAPI(String OAuthVersion) {
	        super(OAuthVersion);
	    }

	    /**
	     * @param OAuthVersion 根据OAuthVersion，配置通用请求参数
	     * @param qHttpClient 使用已有的连接管理器
	     */
	    public RWCommentsAPI(String OAuthVersion, QHttpClient qHttpClient) {
	        super(OAuthVersion, qHttpClient);
	    }
	    
	    
	/**
	 * 根据微博ID返回某条微博的评论列表
	 * @param id 日志id
	 * @param uid 用户的ID
	 * @param page 支分页的页数，默认值为1
	 * @param count 每页显示的日志的数量, 缺省值为20
	 * 
	 * @return [
 {
  “id”:123,
  “uid”:123,
  “name”:”姓名”,
  “headurl”:””,
  “time”:””,
  “content”:””,
  “is_whisper”:1
 },
 {
  “id”:123,
  “uid”:123,
  “name”:”姓名”,
  “headurl”:””,
  “time”:””,
  “content”:””,
  “is_whisper”:1
 }
]

	 */
	    
    /**
     * 
     * 获取renren微博评论列表
     *  @param oAuth 授权
     *  @param entryOwnerId 评论对象所有者的ID
     *  @param entryId 被评论对象的ID
     *  @param commentType 评论的类型 
     *  @param pageSize 页面大小。取值范围1-100，默认大小20
     *  @param pageNumber  页码。取值大于零，默认值为1
     *  
     * */
	public String show(OAuthV2 oAuth, long entryOwnerId, long entryId, int pageNumber, int pageSize) throws Exception{
/*		QArrayList paramsList = new QArrayList();
		paramsList.add(new BasicNameValuePair("method", "blog.getComments"));
		paramsList.add(new BasicNameValuePair("format", "json"));
		paramsList.add(new BasicNameValuePair("id", String.valueOf(id)));
		paramsList.add(new BasicNameValuePair("uid", String.valueOf(uid)));
		paramsList.add(new BasicNameValuePair("page", String.valueOf(page)));
		paramsList.add(new BasicNameValuePair("count", String.valueOf(count)));
		
		return requestAPI.postContent(SERVER_URL_PRIX, paramsList, oAuth);*/
		
		WeiboParameters params = new WeiboParameters();
		params.add("pageSize", pageSize);
		params.add("pageNumber", pageNumber);
		params.add("commentType", "BLOG");
		params.add("entryOwnerId", entryOwnerId);
		params.add("entryId", entryId);

		
		String ret = requestAPI.getResource("https://api.renren.com/v2/comment/list", params, oAuth);

		return ret;
	}

	
	/**
	 * 对日志进行回复
	 * 
	 * @param id 日志id
	 * @param uid 用户的ID
	 * @param content 评论的内容
	 */
	public String reply(OAuthV2 oAuth, int id, String uid, String content) throws Exception{
		QArrayList paramsList = new QArrayList();
		paramsList.add(new BasicNameValuePair("method", "blog.addComment"));
		paramsList.add(new BasicNameValuePair("format", "json"));
		paramsList.add(new BasicNameValuePair("id", String.valueOf(id)));
		paramsList.add(new BasicNameValuePair("uid", String.valueOf(uid)));
		paramsList.add(new BasicNameValuePair("content", content));

		
		return requestAPI.postContent(SERVER_URL_PRIX, paramsList, oAuth);
	}

	@Override
	public void setAPIBaseUrl(String apiBaseUrl) {
		
	}
}
