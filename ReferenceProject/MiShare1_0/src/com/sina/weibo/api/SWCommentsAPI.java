package com.sina.weibo.api;

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
public class SWCommentsAPI extends BasicAPI {
	
	 private static final String SERVER_URL_PRIX = APIConstants.SW_API_SERVER + "/comments";
	 
      /**
	     * 使用完毕后，请调用 shutdownConnection() 关闭自动生成的连接管理器
	     * @param OAuthVersion 根据OAuthVersion，配置通用请求参数
	     */
	    public SWCommentsAPI(String OAuthVersion) {
	        super(OAuthVersion);
	    }

	    /**
	     * @param OAuthVersion 根据OAuthVersion，配置通用请求参数
	     * @param qHttpClient 使用已有的连接管理器
	     */
	    public SWCommentsAPI(String OAuthVersion, QHttpClient qHttpClient) {
	        super(OAuthVersion, qHttpClient);
	    }
	/**
	 * 根据微博ID返回某条微博的评论列表
	 * http://open.weibo.com/wiki/2/comments/show
	 * @param id 需要查询的微博ID。
	 * @param since_id 若指定此参数，则返回ID比since_id大的评论（即比since_id时间晚的评论），默认为0。
	 * @param max_id 若指定此参数，则返回ID小于或等于max_id的评论，默认为0。
	 * @param count 单页返回的记录条数，默认为50
	 * @param page 返回结果的页码，默认为1。
	 * @param filter_by_author 作者筛选类型，0：全部、1：我关注的人、2：陌生人，默认为0。
	 */
	public String show(OAuthV2 oAuth,long id, long since_id, long max_id, int count, int page) throws Exception{
		QArrayList paramsList = new QArrayList();
		paramsList.add(new BasicNameValuePair("id", String.valueOf(id)));
		paramsList.add(new BasicNameValuePair("since_id", String.valueOf(since_id)));
		paramsList.add(new BasicNameValuePair("max_id", String.valueOf(max_id)));
		paramsList.add(new BasicNameValuePair("count", String.valueOf(count)));
		paramsList.add(new BasicNameValuePair("page", String.valueOf(page)));
		paramsList.add(new BasicNameValuePair("filter_by_author", "0"));
	    return requestAPI.getResource(SERVER_URL_PRIX + "/show.json", paramsList,
				oAuth);
	}
	/**
	 * 对一条微博进行评论
	 * 
	 * @param comment 评论内容，内容不超过140个汉字。
	 * @param id 需要评论的微博ID。
	 * @param comment_ori 当评论转发微博时，是否评论给原微博
	 * @param listener
	 */
	public String create(OAuthV2 oAuth,String comment, long id, boolean comment_ori) throws Exception{
		WeiboParameters params = new WeiboParameters();
		params.add("comment", comment);
		params.add("id", id);
		if (comment_ori) {
			params.add("comment_ori", 0);
		} else {
			params.add("comment_ori", 1);
		}
		 return requestAPI.postContent(SERVER_URL_PRIX + "/create.json", params,
					oAuth);
	}
	
	/**
	 * 回复一条评论
	 * 
	 * @param cid 需要回复的评论ID。
	 * @param id 需要评论的微博ID。
	 * @param comment 回复评论内容，内容不超过140个汉字。
	 * @param without_mention 回复中是否自动加入“回复@用户名”，true：是、false：否，默认为false。
	 * @param comment_ori 当评论转发微博时，是否评论给原微博，false：否、true：是，默认为false。
	 * @param listener
	 */
	public String reply(OAuthV2 oAuth, long cid, long id, String comment, boolean without_mention,
			boolean comment_ori) throws Exception{
		WeiboParameters params = new WeiboParameters();
		params.add("cid", cid);
		params.add("id", id);
		params.add("comment", comment);
		if (without_mention) {
			params.add("without_mention", 1);
		} else {
			params.add("without_mention", 0);
		}
		if (comment_ori) {
			params.add("comment_ori", 1);
		} else {
			params.add("comment_ori", 0);
		}
		 return requestAPI.postContent(SERVER_URL_PRIX + "/reply.json", params,
					oAuth);
	}
	
	/**
	 * 批量删除评论，每次最多20条
	 * 
	 * @param cids 需要删除的评论ID列表,需要删除的评论ID，用半角逗号隔开，最多20个。
	 */
	public String destroy_batch(OAuthV2 oAuth, long[] cids) throws Exception{
		WeiboParameters params = new WeiboParameters();
		if(cids.length>0){
			StringBuilder sb = new StringBuilder();
			for(int i=0; i< cids.length; i++){
				sb.append(cids[i]);
				if(i!=cids.length-1){
					sb.append(",");
				}
			}
			params.add("cids", sb.toString());

			return requestAPI.postContent(SERVER_URL_PRIX + "/destroy_batch.json", params,
						oAuth);
		}
		
		return null;


	}

	@Override
	public void setAPIBaseUrl(String apiBaseUrl) {
		
	}
}
