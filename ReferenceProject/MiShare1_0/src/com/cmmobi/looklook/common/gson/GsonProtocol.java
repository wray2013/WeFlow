package com.cmmobi.looklook.common.gson;


/**
 * 通信协议中公共的部分, 比如: 枚举常量, 公共结构;
 */
public final class GsonProtocol {
	
	public static final String EMPTY_VALUE = ""; // 空值;
	public static final String ZERO_VALUE = "0"; // 零值 (用来为时间, 尺寸等属性赋初值);
	
	public static final String RESPONSE_STATUS_OK = "0"; // 响应码: 成功;
	
	public static final int DIARY_SYNC_STATUS_NOT_SYNC = 0; // 日记同步状态: 0:未同步;
	public static final int DIARY_SYNC_STATUS_STRUCTURE_CREATED = 1; // 日记同步状态: 1:日记结构已创建;
	public static final int DIARY_SYNC_STATUS_UPLOADING = 2; // 日记同步状态: 2:上传中;
	public static final int DIARY_SYNC_STATUS_UPLOAD_DONE = 3; // 日记同步状态: 3:上传完成;
	public static final int DIARY_SYNC_STATUS_SYNC_DONE = 4; // 日记同步状态: 4:已同步;
	public static final int DIARY_SYNC_STATUS_DOWNLOADING = 5; // 日记同步状态: 5:下载中;
	public static final int DIARY_SYNC_STATUS_DOWNLOADING_DONE = 6; // 日记同步状态: 6:已下载;
	
	public static final String IS_ONLY_MICROSHARE_FALSE = "0"; // 普通日记;
	public static final String IS_ONLY_MICROSHARE_TRUE = "1"; // 微享;
	
	public static final String DIARY_STATUS_LOSE = "0"; // 日记状态: 0:无效 (已删除);
	public static final String DIARY_STATUS_NEW = "1"; // 日记状态: 1:新建;
	public static final String DIARY_STATUS_PUBLISH = "2"; // 日记状态: 2:已发布;
	
	public static final String JOIN_SAFEBOX_STATUS_NO = "0"; // 是否加入保险箱: 0:否;
	public static final String JOIN_SAFEBOX_STATUS_YES = "1"; // 是否加入保险箱: 1:是;
	
	public static final String DIARY_OPERATE_TYPE_NEW = "1"; // 日记操作类型: 1:新建;
	public static final String DIARY_OPERATE_TYPE_UPDATE = "2"; // 日记操作类型: 2:更新;
	public static final String DIARY_OPERATE_TYPE_COPY = "3"; // 日记操作类型: 3:保存副本(另存为);
	
	public static final String ATTACH_OPERATE_TYPE_ADD = "1"; // 附件操作类型: 1:增加;
	public static final String ATTACH_OPERATE_TYPE_UPDATE = "2"; // 附件操作类型: 2:更新;
	public static final String ATTACH_OPERATE_TYPE_DELETE = "3"; // 附件操作类型: 3:删除;
	
	public static final String ATTACH_TYPE_VIDEO = "1"; // 附件类型: 1:视频;
	public static final String ATTACH_TYPE_AUDIO = "2"; // 附件类型: 2:音频;
	public static final String ATTACH_TYPE_PICTURE = "3"; // 附件类型: 3:图片;
	public static final String ATTACH_TYPE_TEXT = "4"; // 附件类型: 4:文字;
	public static final String ATTACH_TYPE_VOICE = "5"; // 附件类型: 5:短语音;
	public static final String ATTACH_TYPE_VOICE_TEXT = "6"; // 附件类型: 6:语音便签;
	
	public static final String ATTACH_LEVEL_MAIN = "1"; // 内容级别: 1:主内容;
	public static final String ATTACH_LEVEL_SUB = "0"; // 内容级别: 0:辅内容;
	
	public static final String POSITION_SOURCE_GPS = "1"; // 位置来源: 1:GPS;
	public static final String POSITION_SOURCE_BASE_STATION = "2"; // 位置来源: 2:基站;
	
	public static final String SUFFIX_JPG = ".jpg"; // 后缀;
	public static final String SUFFIX_MP3 = ".mp3"; // 后缀;
	public static final String SUFFIX_MP4 = ".mp4"; // 后缀;
	public static final String SUFFIX_TEXT = ""; // 后缀;
	
	public static final String SEX_BOY = "0"; // 性别: 男;
	public static final String SEX_GIRL = "1"; // 性别: 女;
	public static final String SEX_UNKNOW = "2"; // 性别: 未知;
	
	public static final String POSITION_VISIABLE = "1";// 位置可见
	public static final String POSITION_INVISIABLE = "0";// 位置不可见
	public static final String TAG_UNCHANGED = "-1";// 标签不修改
	
	
	
	
	
	private GsonProtocol() { // 禁止构造;
	}
}
