package com.weibo.sdk.android.api;


public interface WeiboAuthListener {

   /**
    * 认证结束后将调用此方法
    * @param weiboIndex int
    */
	public void onComplete(int weiboIndex);

    /**
     * 当认证过程中捕获到WeiboException时调用
     * @param e WeiboException
     * @param weiboIndex int
     */
    public void onWeiboException(WeiboException e, int weiboIndex);

    /**
     * Oauth2.0认证过程中，当认证对话框中的webview接收数据出现错误时调用此方法
     * @param e WeiboDialogError
     * @param weiboIndex int
     */
    public void onError(WeiboDialogError e, int weiboIndex);

    /**
     * Oauth2.0认证过程中，如果认证窗口被关闭或认证取消时调用
     * @param weiboIndex int
     * 
     */
    public void onCancel(int weiboIndex);

}
