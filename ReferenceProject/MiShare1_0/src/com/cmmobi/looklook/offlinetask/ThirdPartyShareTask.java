package com.cmmobi.looklook.offlinetask;

import com.cmmobi.looklook.common.gson.WeiboRequester;

/**
 * @author wuxiang
 * @email wuxiang@cmmobi.com
 * @date  2013-8-26
 */
public class ThirdPartyShareTask extends IOfflineTask {

	@Override
	public void start() {
		if(request instanceof ThirdPartyRequest){
			ThirdPartyRequest thirdPartyRequest=(ThirdPartyRequest) request;
			switch (taskType) {
			case SHARE_TO_RENREN:
				WeiboRequester.publishRenrenWeibo(context, handler, thirdPartyRequest.content+thirdPartyRequest.urlContent, thirdPartyRequest.picUrl, true);
				break;
			case SHARE_TO_SINA:
				WeiboRequester.publishSinaWeibo(context, handler, thirdPartyRequest.content+thirdPartyRequest.urlContent, thirdPartyRequest.picUrl, true);
				break;
			case SHARE_TO_TENCENT:
				WeiboRequester.publishTencentWeibo(context, handler, thirdPartyRequest.content+thirdPartyRequest.urlContent, thirdPartyRequest.picUrl, true);
				break;
			default:
				break;
			}
		}
	}

}
