package com.cmmobi.looklook.offlinetask;

/**
 * @author wuxiang
 * @email wuxiang@cmmobi.com
 * @date  2013-8-20
 */
public enum TaskType {
	/**加入保险箱*/
	SAFEBOX_ADD,
	/**移出保险箱*/
	SAFEBOX_REMOVE,
	/**日记多媒体文件上传*/
	DIARY_MEDIA_UPLOAD,
	/**私信语音文件上传*/
	PRIVATE_MSG_AUDIO_UPLOAD,
	/**视频封面上传*/
	VIDEO_COVER_UPLOAD,
	/**分享轨迹上传*/
	SHARE_TRACE_UPLOAD,
	/**获取日记分享url*/
	GET_DIARY_SHARE_URL,
	/**人人分享*/
	SHARE_TO_RENREN,
	/**腾讯分享*/
	SHARE_TO_TENCENT,
	/**新浪分享*/
	SHARE_TO_SINA,
	/**站内分享*/
	SHARE_TO_LOOKLOOK,
	/**设置日记权限*/
	SET_DIARY_SHARE_PERMISSIONS,
	/**微享*/
	V_SHARE,
	/**合并账户*/
	MERGER_ACCOUNT,
	/**发送私信*/
	SEND_PRIVATE_MSG,
	/**移除黑名单*/
	BLACK_REMOVE,
	/**移除关注*/
	ATTENDED_REMOVE,
	/**移除粉丝*/
	FANS_REMOVE,
	/**日记删除*/
	DIARY_REMOVE,
	/**修改日记位置信息或(和)标签信息*/
	POSITION_AND_TAG,
	/**收藏日记*/
	COLLECT_ADD,
	/**取消收藏*/
	COLLECT_CANCEL,
	/**添加评论*/
	COMMENT_ADD,
	/**删除评论*/
	COMMENT_DELETE,
	/**参加活动*/
	ACTIVE_ATTEND,
	/**取消活动*/
	ACTIVE_CANCEL,
}
