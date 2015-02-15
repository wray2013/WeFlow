package com.cmmobi.sns.api;

import android.content.Context;
import android.util.SparseArray;

import com.cmmobi.sns.constants.OAuthConstants;
import com.cmmobi.sns.exceptions.WeiboError;
import com.cmmobi.sns.keep.AccessTokenKeeper;
import com.cmmobi.sns.oauth.OAuth;
import com.cmmobi.sns.oauthv2.OAuthV2;
import com.cmmobi.sns.oauthv2.OAuthV2Client;
import com.cmmobi.sns.utils.ConfigUtil;
import com.cmmobi.sns.utils.SnsErrorParseUtils;
import com.cmmobi.sns.utils.ConfigUtil.SHARE_TO;
import com.tencent.weibo.api.TWFriendsAPI;
import com.tencent.weibo.api.TWTAPI;
import com.tencent.weibo.api.TWUserAPI;
import com.weibo.sdk.android.api.SWCommentsAPI;
import com.weibo.sdk.android.api.SWFriendshipsAPI;
import com.weibo.sdk.android.api.SWStatusesAPI;
import com.weibo.sdk.android.api.SWUsersAPI;
import com.weibo.sdk.android.api.WeiboAuthListener;
import com.weibo.sdk.android.api.WeiboDialog;
/**
 * 中传分享库（基于OAuth2.0协议实现）封装第三方开放平台(目前版本包括新浪微博，腾讯微博)分享api
 * 
 * @author xudongsheng
 *
 */
public class CmmobiSnsLib
{
	public static final String TAG = CmmobiSnsLib.class.getSimpleName();
	private static CmmobiSnsLib mSnsLib = null;
	private Context mContext;
    private SparseArray<OAuthV2> oAuthV2Map;
	//新浪微博api
	private SWStatusesAPI mSWStatusApi = null;
	private SWFriendshipsAPI mSWFriendshipsApi = null;
	private SWCommentsAPI mSWCommentsApi = null;
	private SWUsersAPI mSWUsersApi = null;
	//腾讯微博api
	private TWTAPI mTWTapi;
	private TWUserAPI mTWUserApi;
	private TWFriendsAPI mTWFriendsApi;
	
	public synchronized static CmmobiSnsLib getInstance(Context context) {
		if(mSnsLib == null){
			mSnsLib = new CmmobiSnsLib(context);
		}
		return mSnsLib;
	}
	
	public SparseArray<OAuthV2> getOAuthV2Map(){
		return this.oAuthV2Map;
	}
	
	public void updateOAuthV2Map(int curWeiboIndex,OAuthV2 oAuth){
		 if(oAuthV2Map == null){
	        oAuthV2Map = new SparseArray<OAuthV2>();
	      }
	      oAuthV2Map.put(curWeiboIndex, oAuth);
	}
	
	private CmmobiSnsLib(Context context) {	
		this.mContext = context;
	}
	
	/**
	 * 新浪微博授权
	 * @param listener
	 */
	public void sinaAuthorize(WeiboAuthListener listener){
		ConfigUtil conf = ConfigUtil.getInstance();
		conf.initSinaData();
		this.authorize(SHARE_TO.SINA.ordinal(), listener);
	}
	/**
	 * 进行微博认证
	 * @param context
	 * @param curWeiboIndex
	 * @param listener
	 */
	private void authorize( int curWeiboIndex,WeiboAuthListener listener) {
		startAuthDialog( curWeiboIndex, listener);
	}
	
	private void startAuthDialog(int curWeiboIndex, WeiboAuthListener listener) {	
	    String appKey = ConfigUtil.getInstance().getAppKey();
	    String appSecret = ConfigUtil.getInstance().getAppSecret();
	    String redirectUrl = ConfigUtil.getInstance().getRedirectUrl();
	    String authorizeUrl = ConfigUtil.getInstance().getAuthoriz_token_url();
	    OAuthV2 oAuth=new OAuthV2(redirectUrl);
        oAuth.setClientId(appKey);
        oAuth.setClientSecret(appSecret);
        oAuth.setAuthorizeUrl(authorizeUrl);
        oAuth.setCurWeiboIndex(curWeiboIndex);
        String urlStr = OAuthV2Client.generateImplicitGrantUrl(oAuth);
        /*如果用户在Dialog显示之前按了返回按钮,activity已经onDestory了，就会报出android.view.WindowManager$BadTokenException: Unable to add window --
        token android.os.BinderProxy@4479b390 is not valid; is your activity running?异常，在此处扑获
        异常，解决了此问题。*/        
        try{
        	 new WeiboDialog(mContext,urlStr, oAuth, listener).show();
        }catch(Exception e){
        	
        } 
	}
	
	/**
	 * 验证用户是否已通过新浪微博授权或需要重新授权
	 * 
	 * @return
	 */
	public boolean isSinaWeiboAuthorized(){
		OAuthV2 oAuth = (OAuthV2) this.acquireSinaOauth();
		return oAuth.isSessionValid();
	}
	/**
	 * 获取新浪微博用户Id
	 * @return
	 */
	public String getSinaWeiboUserId(){
		return this.acquireSinaOauth().getOpenid();
	}
	
	private OAuth acquireSinaOauth(){
		return this.acquireOauth(SHARE_TO.SINA.ordinal());
	}
	
	private OAuth acquireTencentWeiboOauth(){
		return this.acquireOauth(SHARE_TO.TENC.ordinal());
	}
	/**
	 * 获取OAuth对象
	 * @param curWeiboIndex
	 * @return
	 */
	private OAuth acquireOauth(int curWeiboIndex){
		OAuth oauth = null;
		//先从缓存中取
		if(this.oAuthV2Map != null && this.oAuthV2Map.size() > 0){
			oauth = this.oAuthV2Map.get(curWeiboIndex);
		}
		if(oauth == null){//若缓存中没有，则从文件中new出一个OAuth实例
			oauth = AccessTokenKeeper.readAccessToken(mContext, curWeiboIndex);
		}
		return oauth;
	}
	/**
	 * 指定一个图片URL地址抓取后上传并同时发布一条新微博，此方法会处理URLencode
	 * 
	 * @param status 要发布的微博文本内容，内容不超过140个汉字。
	 * @param imageUrl 图片的URL地址，必须以http开头。
	 * @param lat 纬度，有效范围：-90.0到+90.0，+表示北纬，默认为0.0。
	 * @param lon 经度，有效范围：-180.0到+180.0，+表示东经，默认为0.0。
	 * @throws Exception 
	 * @return true 分享成功 false 分享失败
	 */
	public boolean sinaUploadUrlText(String status, String imageUrl, String lat, String lon) throws Exception {
		if (null == mSWStatusApi) {
			this.mSWStatusApi = new SWStatusesAPI(OAuthConstants.OAUTH_VERSION_2_A);
		}
		String jsonResult = mSWStatusApi.uploadUrlText(acquireSinaOauth(), status, imageUrl, lat, lon);
		if(jsonResult != null){
			WeiboError tencentWeiboError = SnsErrorParseUtils.parseSinaWeiboError(jsonResult);
			if(tencentWeiboError != null){
				return false;
			}else{
				return true;
			}
		}
		return false;
	}
	
	/**
	 * 获取用户的双向关注列表，即互粉列表
	 * 
	 * @param uid 需要获取双向关注列表的用户UID。
	 * @param count 单页返回的记录条数，默认为50。
	 * @param page 返回结果的页码，默认为1。
	 * @throws Exception 
	 */
	public String getMySWFriendsList(long uid, int count, int page) throws Exception {
		if(null == mSWFriendshipsApi){
			mSWFriendshipsApi = new SWFriendshipsAPI(OAuthConstants.OAUTH_VERSION_2_A);
		}
		return mSWFriendshipsApi.bilateral(acquireSinaOauth(),uid, count, page);
	}
	/**
	 * 根据用户ID获取用户信息
	 * @param uid 需要查询的用户ID。
	 * @throws Exception 
	 */
	public String getSWUserInfo(long uid) throws Exception {
		if(null == mSWUsersApi){
			mSWUsersApi = new SWUsersAPI(OAuthConstants.OAUTH_VERSION_2_A);
		}
		return mSWUsersApi.show(acquireSinaOauth(), uid);
	}
	
	/**
	 * 根据微博ID返回某条微博的评论列表
	 * @param id 需要查询的微博ID。
	 * @param since_id 若指定此参数，则返回ID比since_id大的评论（即比since_id时间晚的评论），默认为0。
	 * @param max_id 若指定此参数，则返回ID小于或等于max_id的评论，默认为0。
	 * @param count 单页返回的记录条数，默认为50
	 * @param page 返回结果的页码，默认为1。
	 * @param filter_by_author 作者筛选类型，0：全部、1：我关注的人、2：陌生人，默认为0。
	 * @throws Exception 
	 */
	public String getSWCommentList(long id, long since_id, long max_id, int count, int page) throws Exception {
		if(null == this.mSWCommentsApi){
			mSWCommentsApi = new SWCommentsAPI(OAuthConstants.OAUTH_VERSION_2_A);
		}
		return mSWCommentsApi.show(acquireSinaOauth(), id, since_id, max_id, count, page);
	}
	/**
	 * 回复一条评论
	 * 
	 * @param cid 需要回复的评论ID。
	 * @param id 需要评论的微博ID。
	 * @param comment 回复评论内容，内容不超过140个汉字。
	 * @param without_mention 回复中是否自动加入“回复@用户名”，true：是、false：否，默认为false。
	 * @param comment_ori 当评论转发微博时，是否评论给原微博，false：否、true：是，默认为false。
	 * @throws Exception 
	 */
	public String replyToSinaComment(long cid, long id, String comment, boolean without_mention,
			boolean comment_ori) throws Exception {
		if(null == this.mSWCommentsApi){
			mSWCommentsApi = new SWCommentsAPI(OAuthConstants.OAUTH_VERSION_2_A);
		}
		return mSWCommentsApi.reply(acquireSinaOauth(), cid, id, comment, without_mention, comment_ori);
	}
	/**
	 * 对一条微博进行评论
	 * 
	 * @param comment 评论内容，内容不超过140个汉字。
	 * @param id 需要评论的微博ID。
	 * @param comment_ori 当评论转发微博时，是否评论给原微博
	 * @throws Exception 
	 */
	public String commentOnSW(String comment, long id, boolean comment_ori) throws Exception {
		if(null == this.mSWCommentsApi){
			mSWCommentsApi = new SWCommentsAPI(OAuthConstants.OAUTH_VERSION_2_A);
		}
		return mSWCommentsApi.create(acquireSinaOauth(),comment, id, comment_ori);
	}
	/**
	 * 批量获取指定微博的转发数评论数
	 * 
	 * @param ids 需要获取数据的微博ID，最多不超过100个。
	 * @throws Exception 
	 */
	public String countsOfSW(String[] ids) throws Exception {
		if (null == mSWStatusApi) {
			this.mSWStatusApi = new SWStatusesAPI(OAuthConstants.OAUTH_VERSION_2_A);
		}
		return mSWStatusApi.count(acquireSinaOauth(), ids);
	}
	
	
/*****************************************************************************************************

                                      腾讯微博分享api

*****************************************************************************************************/
	/**
	 * 腾讯微博授权
	 * @param context
	 */
	public void tencentWeiboAuthorize(WeiboAuthListener listener){
		ConfigUtil conf = ConfigUtil.getInstance();
		conf.initQqData();
		this.authorize(SHARE_TO.TENC.ordinal(), listener);
	}
	
	/**
	 * 验证用户是否已通过腾讯微博授权或需要重新授权
	 * 
	 * @return
	 */
	public boolean isTencentWeiboAuthorized(){
		OAuthV2 oAuth = (OAuthV2) this.acquireTencentWeiboOauth();
		return oAuth.isSessionValid();
	}
	/**
	 * 获取腾讯微博用户Id
	 * @return
	 */
	public String getTencentWeiboUserId(){
		return this.acquireTencentWeiboOauth().getOpenid();
	}
	
	/**
	 * 发表一条带图片的微博
	 * 
	 * @param content  微博内容
	 * @param clientip 用户IP(以分析用户所在地)
	 * @param picpath 可以是本地图片路径 或 网络地址
	 * @return
	 * @throws Exception
     * @see <a href="http://wiki.open.t.qq.com/index.php/%E5%BE%AE%E5%8D%9A%E7%9B%B8%E5%85%B3/%E5%8F%91%E8%A1%A8%E4%B8%80%E6%9D%A1%E5%B8%A6%E5%9B%BE%E7%89%87%E7%9A%84%E5%BE%AE%E5%8D%9A">腾讯微博开放平台上关于此条API的文档1-本地图片</a>
     * @see <a href="http://wiki.open.t.qq.com/index.php/%E5%BE%AE%E5%8D%9A%E7%9B%B8%E5%85%B3/%E7%94%A8%E5%9B%BE%E7%89%87URL%E5%8F%91%E8%A1%A8%E5%B8%A6%E5%9B%BE%E7%89%87%E7%9A%84%E5%BE%AE%E5%8D%9A">腾讯微博开放平台上关于此条API的文档2-网络图片</a>
	 */
	public boolean twUploadUrlText(String content,
			String clientip, String picpath) throws Exception {
		if(mTWTapi == null){
			mTWTapi= new TWTAPI(OAuthConstants.OAUTH_VERSION_2_A);
		}
		String format = "json";
		String jsonResult =  mTWTapi.addPic(acquireTencentWeiboOauth(), format, content, clientip, "", "", picpath, "");	
		if(jsonResult != null){
			WeiboError tencentWeiboError = SnsErrorParseUtils.parseTecentWeiboError(jsonResult);
			if(tencentWeiboError != null){
				return false;
			}else{
				return true;
			}
		}
		return false;
	}
	
    /**
	 * 腾讯微博获取自己的资料
	 * @return
	 * @throws Exception
     * @see <a href="http://wiki.open.t.qq.com/index.php/%E5%B8%90%E6%88%B7%E7%9B%B8%E5%85%B3/%E8%8E%B7%E5%8F%96%E8%87%AA%E5%B7%B1%E7%9A%84%E8%AF%A6%E7%BB%86%E8%B5%84%E6%96%99">腾讯微博开放平台上关于此条API的文档</a>
	 */
	public String twUserInfo() throws Exception {
		if(mTWUserApi == null){
			mTWUserApi = new TWUserAPI(OAuthConstants.OAUTH_VERSION_2_A);
		}
		String format = "json";
		return mTWUserApi.info(acquireTencentWeiboOauth(), format);
	}
	
	/**
	 * 获取用户的双向关注列表，即互粉列表
	 * 
	 * @param reqnum  请求个数(1-30)
	 * @param startindex 起始位置（第一页填0，继续向下翻页：填：【reqnum*（page-1）】）
     * @param install  过滤安装应用好友（可选） <br>
     *                           0-不考虑该参数，1-获取已安装应用好友，2-获取未安装应用好友 
	 * @return
	 * @throws Exception
     * @see <a href="http://wiki.open.t.qq.com/index.php/%E5%B8%90%E6%88%B7%E7%9B%B8%E5%85%B3/%E6%88%91%E6%94%B6%E5%90%AC%E7%9A%84%E4%BA%BA%E5%88%97%E8%A1%A8">腾讯微博开放平台上关于此条API的文档</a>
	 */
	public String getMyTWFriendsList(String reqnum,
			String startindex) throws Exception {
		if(null == mTWFriendsApi){
			mTWFriendsApi = new TWFriendsAPI(OAuthConstants.OAUTH_VERSION_2_A);
		}
		String install = "0";//install  过滤安装应用好友（可选)0-不考虑该参数，1-获取已安装应用好友，2-获取未安装应用好友 
		return mTWFriendsApi.mutualList(acquireTencentWeiboOauth(), "json", reqnum, startindex, install);
	}
	
	/**
	 * 获取单条微博的点评列表
	 * 
	 * @param rootid 转发或回复的微博根结点id（源微博id）
	 * @param pageflag 分页标识（0：第一页，1：向下翻页，2：向上翻页）
	 * @param pagetime 本页起始时间（第一页：填0，向上翻页：填上一次请求返回的第一条记录时间，向下翻页：填上一次请求返回的最后一条记录时间）
	 * @param reqnum 每次请求记录的条数（1-100条）
	 * @param twitterid 翻页用，第1-100条填0，继续向下翻页，填上一次请求返回的最后一条记录id
	 * @return
	 * @throws Exception
     * @see <a href="http://wiki.open.t.qq.com/index.php/%E5%BE%AE%E5%8D%9A%E7%9B%B8%E5%85%B3/%E8%8E%B7%E5%8F%96%E5%8D%95%E6%9D%A1%E5%BE%AE%E5%8D%9A%E7%9A%84%E8%BD%AC%E5%8F%91%E6%88%96%E7%82%B9%E8%AF%84%E5%88%97%E8%A1%A8">腾讯微博开放平台上关于此条API的文档</a>
	 */
	public String getTWCommentList(String rootid,
			String pageflag, String pagetime, String reqnum,
			String twitterid) throws Exception {
		if(null == this.mTWTapi){
			mTWTapi = new TWTAPI(OAuthConstants.OAUTH_VERSION_2_A);
		}
		String flag = "1"; //flag  标识。0－转播列表 1－点评列表 2－点评与转播列表 
		return mTWTapi.reList(acquireTencentWeiboOauth(), "json", flag, rootid, pageflag, pagetime, reqnum, twitterid);
	}
	/**
	 * 回复一条微博
	 * 
	 * @param content  微博内容
	 * @param clientip 用户IP(以分析用户所在地)
	 * @param reid 回复的父结点微博id
	 * @return
	 * @throws Exception
     * @see <a href="http://wiki.open.t.qq.com/index.php/%E5%BE%AE%E5%8D%9A%E7%9B%B8%E5%85%B3/%E5%9B%9E%E5%A4%8D%E4%B8%80%E6%9D%A1%E5%BE%AE%E5%8D%9A%EF%BC%88%E5%8D%B3%E5%AF%B9%E8%AF%9D%EF%BC%89">腾讯微博开放平台上关于此条API的文档</a>
	 */
	public String replyToTwComment(String content,
			String clientip, String reid) throws Exception {
		if(null == this.mTWTapi){
			mTWTapi = new TWTAPI(OAuthConstants.OAUTH_VERSION_2_A);
		}
		String format = "json"; //format 返回数据的格式 是（json或xml）
		return mTWTapi.reply(acquireTencentWeiboOauth(), format, content, clientip, reid);
	}
	/**
	 * 点评一条微博
	 * 
	 * @param content  微博内容
	 * @param clientip 用户IP(以分析用户所在地)
	 * @param reid 点评父结点微博id
	 * @return
	 * @throws Exception
     * @see <a href="http://wiki.open.t.qq.com/index.php/%E5%BE%AE%E5%8D%9A%E7%9B%B8%E5%85%B3/%E7%82%B9%E8%AF%84%E4%B8%80%E6%9D%A1%E5%BE%AE%E5%8D%9A">腾讯微博开放平台上关于此条API的文档</a>
	 */
	public String commentOnTW(String content,
			String clientip, String reid) throws Exception {
		if(null == this.mTWTapi){
			mTWTapi = new TWTAPI(OAuthConstants.OAUTH_VERSION_2_A);
		}
		return mTWTapi.comment(acquireTencentWeiboOauth(), "json", content, clientip, reid);
	}
	/**
	 * 获取微博当前已被转播次数
	 * 
	 * @param ids 微博ID列表，用“,”隔开
	 * @param flag  0－获取转发计数，1－获取点评计数 2－两者都获取
 
	 * @return
	 * @throws Exception
     * @see <a href="http://wiki.open.t.qq.com/index.php/%E5%BE%AE%E5%8D%9A%E7%9B%B8%E5%85%B3/%E8%BD%AC%E6%92%AD%E6%95%B0%E6%88%96%E7%82%B9%E8%AF%84%E6%95%B0">腾讯微博开放平台上关于此条API的文档<a>
	 */
	public String countsOfTW(String ids, String flag) throws Exception {
		if(null == this.mTWTapi){
			mTWTapi = new TWTAPI(OAuthConstants.OAUTH_VERSION_2_A);
		}
		return mTWTapi.reCount(acquireTencentWeiboOauth(), "json", ids, flag);
	}
}