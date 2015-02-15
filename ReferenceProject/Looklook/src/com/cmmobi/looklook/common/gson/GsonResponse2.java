package com.cmmobi.looklook.common.gson;

import java.io.Serializable;
import java.util.ArrayList;

import cn.zipper.framwork.core.ZApplication;

import com.cmmobi.looklook.MainApplication;
import com.cmmobi.looklook.activity.DiaryDetailActivity.DiaryType;
import com.cmmobi.looklook.common.gson.GsonRequest2.createStructureRequest;
import com.cmmobi.looklook.info.profile.AccountInfo;
import com.cmmobi.looklook.info.profile.ActiveAccount;
import com.cmmobi.looklook.info.profile.DiaryManager;
import com.cmmobi.looklook.info.profile.MediaValue;
import com.cmmobi.sns.oauthv2.OAuthV2;
import com.cmmobi.sns.utils.ConfigUtil.SHARE_TO;

/**
 * @author wangrui
 */
public class GsonResponse2 {

	// 1.手机UA协议
	public static class uaResponse {
		public String status;
		public String crm_status;
		public String equipmentid;
		public String ip;
	}

	// 2. 用户注册
	public static class registerResponse {
		public String status; // 0：成功，1：用户帐号已存在，用户信息没有
		public String crm_status;
		public String userid; // 用户ID
		public String nickname; // 用户昵称
		public String headimageurl;// portraiturl; //头像地址，可以为空
		public String sex; // 0男 1女 2未知
		public String address; // ”北京朝阳”
		public String birthdate;
		public String signature;// tag; //”心情不错”
		public String app_downloadurl; // http://t.cn/zj9WidU”//looklook官方下载地址
		public String privmsg_type;// 谁可以给我发私信，1全部人可见（黑名单人除外） 2关注人可见 4仅自己可见
		public String friends_type;// 谁可以看我的朋友关系，1全部人可见（黑名单人除外） 2关注人可见 4仅自己可见
		public String diary_type;// 谁可以看我内容（日记和评论），1全部人可见（黑名单人除外） 2关注人可见 4仅自己可见
		public String position_type;// 谁可以看我的位置，1全部人可见（黑名单人除外） 2关注人可见 4仅自己可见
		public String audio_type;// 谁可以听我的语音，1全部人可见（黑名单人除外） 2关注人可见 4仅自己可见
		public String launch_type; // 启动1观看模式 2摄影模式
		public String sysc_type;// 数据同步，1关闭 2仅WIFI 3任何网络
	}

	// 3. 修改用户信息
	public static class changeUserInfoResponse {
		public String status; // 0:成功，1：用户名已存在
		public String crm_status;
		public String headimageurl; // 头像地址 //portraiturl;
	}

	// 4. 修改登录密码、手势密码
	public static class passwordChangeResponse {
		public String status; // 0:成功，1：用户名已存在
		public String crm_status;
	}

	// 5. 获取手机验证码
	public static class checkNoResponse {
		public String status; // 0:成功
		public String crm_status;
	}

	// 6. 修改用户心情
	public static class moodResponse {
		public String status; // 0:成功
		public String crm_status;
	}

	// 7. 隐私设置
	public static class diaryPrivacyResponse {
		public String status; // 0:成功
		public String crm_status;
	}

	// 8. 添加手势密码
	public static class addGesturePasswordResponse {
		public String status; // 0:成功
		public String crm_status;
	}

	// 9. 判断用户是否有效（专供socket使用）
	public static class checkuserResponse {
		public String status; // 0:有效
		public String crm_status;
	}

	// 10. 用户登录
	public static class loginResponse {
		public String status; // 0：成功，1：第三方登录账号不存在,创建新账号, 2密码错误，3用户不存在
		public String crm_status;
		public String userid; // 用户ID
		public String nickname; // 用户昵称
		public String headimageurl;// portraiturl; //头像地址
		// public String watermark; //水印
		// public MySNS sns[];
		public MyBind binding[];// 已绑定信息
		public String sex;
		public String birthdate;
		public String address;
		public String signature;// tag; //可为空
		public String app_downloadurl; // "http://t.cn/zj9WidU"//looklook官方下载地址
		public String mood; // 心情
		public String privmsg_type;// 谁可以给我发私信，1全部人可见（黑名单人除外） 2关注人可见 4仅自己可见
		public String friends_type;// 谁可以看我的朋友关系，1全部人可见（黑名单人除外） 2关注人可见 4仅自己可见
		public String diary_type;// 谁可以看我内容（日记和评论），1全部人可见（黑名单人除外） 2关注人可见 4仅自己可见
		public String position_type;// 谁可以看我的位置，1全部人可见（黑名单人除外） 2关注人可见 4仅自己可见
		public String audio_type;// 谁可以听我的语音，1全部人可见（黑名单人除外） 2关注人可见 4仅自己可见
		public String audio_encrypt_type; // //加密类型， audio_type=2或4，有效
		public String launch_type; // 启动1观看模式 2摄影模式
		public String sysc_type;// 数据同步，1关闭 2仅WIFI 3任何网络
		public String gesturepassword;// 手势密码
		public String is_firstlogin; //0 未登录过 1已登陆过
	}

	// 11. 获取Socket IP 端口
	public static class getSocketResponse {
		public String status; // 0:成功，1：用户名已存在
		public String crm_status;
		public String ip;
		public String port;
		public String spacesize; // ”2048M”
		public String maxsize; // ”2048M”
		// public String videopath; //”/data/…/123.mp4”,服务器给定的path
	}

	// 12. 日记结构管理
	public static class createStructureResponse {
		public String status; // 0：成功
		public String crm_status;
		public String ip;
		public String port;
		public String spacesize;
		public String maxsize;
		public String diaryid;
		public String diaryuuid;
		public MyAttach attachs[];// 附件信息
	}

	// 13. 发布/取消发布
	public static class diaryPublishResponse {
		public String status; // 0:成功，1：失败
		public String crm_status;
	}

	// 14. 获取天气
	public static class getWeatherResponse {
		public String status; // 0:成功，1：失败
		public String crm_status;
		public MyWeatherDescription[] weather;// 天气描述集合
	}

	// 15. 分享
	public static class shareDiaryResponse {
		public String status; // 0:成功
		public String crm_status;
	}

	// 16. 我的日记
	public static class listMyDiaryResponse {
		public String status; // 0:成功
		public String crm_status;
		public String first_diary_time;
		public String last_diary_time;
		public String is_refresh;
		public String hasnextpage;
		public MyDiary[] diaries;
		public String removediarys;
	}

	// //16. 我的分享日记
	// public static class listMyPublishDiaryResponse{
	// public String status; //0:成功
	// public String first_diary_time;
	// public String last_diary_time;
	// public String is_refresh;
	// public String hasnextpage;
	// public MyDiary[] diaries;
	// public String removediarys;
	// }
	// 17. 标签日记列表
	public static class tagDiaryListResponse {
		public String status; // 0:成功
		public String crm_status;
		public String first_diary_time;
		public String last_diary_time;
		public String is_refresh;
		public String hasnextpage;
		public MyDiary[] diaries;
		public String removediarys;
	}

	// 18. 保险箱日记列表
	public static class listsafeboxResponse {
		public String status; // 0:成功
		public String crm_status;
		public String first_diary_time;
		public String last_diary_time;
		public String is_refresh;
		public String hasnextpage;
		public MyDiary[] diaries;
		public String removediarys;
	}

	// 19. 日记加入、删除保险箱
	public static class safeboxResponse {
		public String status; // 0:成功
		public String crm_status;
	}

	// 20. 编辑、添加日记文本（辅件）
	public static class attachContentResponse {
		public String status; // 0:成功
		public String crm_status;
	}

	// 21. 列表收藏日记
	public static class listCollectDiaryResponse {
		public String status; // 0:成功
		public String crm_status;
		public String first_diary_time;
		public String last_diary_time;
		public String is_refresh;
		public String hasnextpage;
		public MyDiary[] diaries;
		public String removediarys;
	}

	// 22. 收藏日记
	public static class addCollectDiaryResponse {
		public String status; // 0:成功
		public String crm_status;
	}

	// 23. 收藏删除
	public static class removeCollectDiaryResponse {
		public String status; // 0:成功
		public String crm_status;
	}

	// 24. 发送日志信息（客户端CDR话单），提醒产品要记录那些信息
	public static class postLogResponse {
		public String status; // 0成功，1失败,
		public String crm_status;
	}

	// 25. 意见反馈
	public static class feedbackResponse {
		public String status; // 0成功
		public String crm_status;
	}

	// 26. 绑定
	public static class bindingResponse {
		public String status;// 0成功，其他查看错误文档;23该邮箱已经被绑定;24该用户已经绑定一个邮箱;
								// 26指定第三方账户已经注册，或绑定其他账号;18手机验证码错误;
								// 19手机验证码已超时;21此手机号已经被绑定
		public String crm_status;
		
		public String binding_type; //1邮箱，2手机，3第三放平台
		public String snstype;     //1：新浪微博，2人人，6腾讯
		public String phone_type;//1为绑定主手机号，2绑定备用手机号
	}

	// 27. 发送第三方互粉
	public static class postSNSFriendResponse {
		public String status; // 0成功
		public String crm_status;
	}

	// 28. 自动登录设置deviceToken绑定
	public static class autoLoginResponse {
		public String status; // 0：成功，1：失败（非最新登录）
		public String crm_status;
		public String userid; // 用户ID
		public String nickname; // 用户昵称
		public String headimageurl;// portraiturl; //头像地址
		public MyBind binding[];// 已绑定信息
		public String sex;
		public String birthdate;
		public String address;
		public String signature;// tag; //可为空
		public String app_downloadurl; // "http://t.cn/zj9WidU"//looklook官方下载地址
		public String mood; // 心情
		public String privmsg_type;// 谁可以给我发私信，1全部人可见（黑名单人除外） 2关注人可见 4仅自己可见
		public String friends_type;// 谁可以看我的朋友关系，1全部人可见（黑名单人除外） 2关注人可见 4仅自己可见
		public String diary_type;// 谁可以看我内容（日记和评论），1全部人可见（黑名单人除外） 2关注人可见 4仅自己可见
		public String position_type;// 谁可以看我的位置，1全部人可见（黑名单人除外） 2关注人可见 4仅自己可见
		public String audio_type;// 谁可以听我的语音，1全部人可见（黑名单人除外） 2关注人可见 4仅自己可见
		public String launch_type; // 启动1观看模式 2摄影模式
		public String sysc_type;// 数据同步，1关闭 2仅WIFI 3任何网络
		public String gesturepassword;// 手势密码
	}

	// 29. 举报
	public static class reportResponse {
		public String status; // 0成功
		public String crm_status;
	}

	// 30. 给后台发第三方社区转发数、评论数、回复数
	public static class postWeiboCountResponse {
		public String status; // 0成功
		public String crm_status;
	}

	// 31. 忘记密码
	public static class forgetPasswordResponse {
		public String status; // 0：处理成功,1：没有这个账号
		public String crm_status;
	}

	// 32. 日记评论列表
	public static class diaryCommentListResponse {
		public String status; // 0：处理成功,
		public String crm_status;
		public String first_comment_time;// 第一条记录的时间
		public String last_comment_time; // 最后一条记录的时间
		public String is_refresh;
		public String hasnextpage;
		public diarycommentlistItem[] comments;
		public String removecommenids; //删除评论id评论
	}

	// 33. 发表/回复评论
	public static class commentResponse {
		public String status; // 0：处理成功,
		public String crm_status;
		public String audiopath;
		public String commentid;
		public String ip;
		public String port;
	}

	// 34. 删除日记
	public static class deleteDiaryResponse {
		public String status; // 0：处理成功,
		public String crm_status;
	}

	// 35. 获取日记分享URL
	public static class getDiaryUrlResponse {
		public String status; // 0：处理成功
		public String crm_status;
		public platformUrls[] platformurls;
		public String shareimageurl;
	}

	// 36. 日记详情
	public static class diaryInfoResponse {
		public String status; // 0：处理成功
		public String crm_status;
		public MyDiary diaries;
	}

	// 37. 验证注册用户名是否可用
	public static class checkUserNameExistResponse {
		public String status; // 0可用、1不可用
		public String crm_status;
	}

	// 38. 验证注册昵称是否可用------（第三方用户昵称规则？）
	public static class checkNickNameExistResponse {
		public String status; // 0可用、1不可用
		public String crm_status;
	}

	// 39. 搜索用户列表（除去系统黑名单）
	public static class searchUserResponse {
		public String status; // 0可用、1不可用
		public String crm_status;
		public searchUsers[] users;
	}

	// 40. 搜索附近日记（除自己日记外、系统黑名单）
	public static class nearDiaryResponse {
		public String status; // 0可用、1不可用
		public String crm_status;
		public MyDiary[] diaries;
	}

	// 41. 获取标签
	public static class taglistResponse {
		public String status; // 0可用、1不可用
		public String crm_status;
		public taglistItem[] tags;
	}

	// 42. 关注
	public static class attentionResponse {
		public String status; // 0成功
		public String crm_status;
		public String attention_userid; //被关注用户ID
	}

	// 43. 取消关注\删除粉丝
	public static class cancelattentionResponse {
		public String status; // 0成功
		public String crm_status;
		public String targer_userid;
	}

	// 44. 关注人备注
	public static class markattentionResponse {
		public String status; // 0成功
		public String crm_status;
	}

	// 45. 设置黑名单（用户黑名单）
	public static class operateblacklistResponse {
		public String status; // 0成功
		public String targer_userid;
		public String crm_status;
	}

	// 46. 我的关注列表
	public static class myattentionlistResponse {
		public String status; // 0成功
		public String crm_status;
		public String user_time;
		public String is_refresh;
		public String hasnextpage;
		public String removeusers;
		public myattentionlistUsers[] users;
	}

	// 47. 我的粉丝
	public static class myfanslistResponse {
		public String status; // 0成功
		public String crm_status;
		public String user_time;
		public String hasnextpage;
		public String removeusers;
		public myfanslistUsers[] users;
	}

	// 48. 用户黑名单列表
	public static class myblacklistResponse {
		public String status; // 0成功
		public String crm_status;
		public String user_time;
		public String hasnextpage;
		public String removeusers;
		public myblacklistUsers[] users;
	}

	// 49. 朋友圈
	public static class timelineResponse {
		public String status; // 0成功
		public String crm_status;
		public timelinePart[] parts;
//		public MyDiary[] diaries;
		public TimelineDiary[] diaries;
	}

	public static class TimelineDiary{
		public MyDiary diary;
		public Forward forward;
	}
	public class Forward {
		 public String forwarduserid;
		 public String forwardtime;
         public String forwardnickname;
         public String forwardheadimage; 
	}
	// 50. 发私信
	public static class sendmessageResponse {
		public String status; // 0成功
		public String crm_status;
		public String audiopath;
		public String privatemsgid;
		public String servertime;
		public String ip;
		public String port;
		public String uuid;
	}

	// //50. 获取私信
	// public static class getprivatemessageResponse{
	// public String status; //0成功
	// public getPrivateMessage[] privatemsg;
	// }

	// 51. 日记推荐
	public static class diaryrecommendResponse {
		public String status; // 0成功
		public String crm_status;
		public String first_user_time;
		public String last_user_time;
		public String is_refresh;
		public String hasnextpage;
		public MyDiary[] diaries;
		public String removeusers;
	}

	// 52. 喜欢(赞)并转发
	public static class enjoyResponse {
		public String status; // 0成功
		public String crm_status;
		public String diaryid;
	}

	// 55. 解除绑定
	public static class unbindResponse {
		public String status;// 状态，0：成功,25该用户未绑定指定邮箱,
								// 22该用户未绑定指定手机号27该用户未绑定指定第三方账户
		public String crm_status;
		public String binding_type; //1邮箱，2手机，3第三放平台
		public String snstype;     //1：新浪微博，2人人，6腾讯
		public String phone_type;//1为绑定主手机号，2绑定备用手机号
	}

	// 56. 删除评论（删除自己评论和删除对自己视频进行的评论）
	public static class deleteCommentResponse {
		public String status; // 0成功
		public String crm_status;
	}

	// 57. 设置视频封面（供图片服务器访问）
	public static class setVideoCoverResponse {
		public String status; // 0成功
		public String crm_status;
		public String imageurl; // ”http://iamge.looklook.cn/201212/03/543213609KJH76.jpg”
	}

	// 58. 设置用户头像（供图片服务器访问）
	public static class setHeadImageResponse {
		public String status; // 0成功
		public String crm_status;
		public String imageurl; // ”http://iamge.looklook.cn/201212/03/543213609KJH76.jpg”
	}

	// 59. 获取活动列表（缓存）
	public static class activeListResponse {
		public String status; // 0成功
		public String crm_status;
		public activeListItem[] active;
	}

	// 60. 参与活动日记列表
	public static class activeDiaryListResponse {
		public String status; // 0成功
		public String crm_status;
		public String first_user_time;
		public String last_user_time;
		public String is_refresh;
		public String hasnextpage;
		public MyDiary[] diaries;
		public String removeusers;
	}

	// 62. 设置活动图片（供图片服务器访问）
	public static class setActiveImageResponse {
		public String status; // 0成功
		public String crm_status;
		public String imageurl; // "http://iamge.looklook.cn/201212/03/543213609KJH76.jpg"
	}

	// 63. 删除消息
	public static class deleteMessageResponse {
		public String status; // 0成功
		public String crm_status;
	}

	// 64. 第三方互为关注的用户（以在LOOKLOOK注册的用户）
	public static class listThirdPlatformUserResponse {
		public String status; // 0成功
		public String crm_status;
		public String index; // ”ddd_dd_dd”
		public String hasnextpage; // 1有下一页，0没有下一页
		public String timestamp;//用于回传的时间戳
		// 排序,按昵称拼音排序
		public listThirdPlatformUserItem[] users;
	}

	// 65. 获取消息数
	public static class getMessageCountResponse {
		public String status; // 0成功
		public String crm_status;
		public String server_time; // 服务器时间
		public String message_count; // 消息数量
		public String stranger_message_count; // 陌生人消息数量
		public String mac;// 手机的mac地址
		public String imei;// 手机imei（手机的唯一标识）
	}

	// 66. 获取日记赞人列表（3.0暂时不做，调转发人列表）
	public static class getDiaryEnjoyUsersResponse {
		public String status; // 0成功
		public String crm_status;
		public String index; // ”ddd_dd_dd”
		public String hasnextpage; // 1有下一页，0没有下一页
		// 排序,按昵称拼音排序
		public getDiaryEnjoyUsers[] enjoies;
	}

	// 67. 获取日记转发人列表
	public static class getDiaryForwardUsersResponse {
		public String status; // 0成功
		public String crm_status;
		public String first_user_time;
		public String last_user_time;
		public String is_refresh;
		public String hasnextpage;
		public String removeusers;
		public getDiaryForwardUsers[] forwords;
	}

	// 68. 设置空间背景图片
	public static class setUserSpaceCoverResponse {
		public String status; // 0成功
		public String crm_status;
		public String imageurl; // ”http://iamge.looklook.cn/201212/03/543213609KJH76.jpg”
	}

	// 69. 获取空间背景图片列表
	public static class getSpaceCoverListResponse {
		public String status; // 0成功
		public String crm_status;
		public String index; // ”ddd_dd_dd”
		public String hasnextpage; // 1有下一页，0没有下一页
		// 排序,按昵称拼音排序
		public getbackgroundlistItem[] backgrounds;
	}

	// 71. 转发日记列表(3.0获取赞日记列表)
	public static class forwardDiaryListResponse {
		public String status; // 0成功
		public String crm_status;
		public String first_diary_time;
		public String last_diary_time;
		public String hasnextpage;
		public String is_refresh;
		public MyDiary[] diaries;
		public String removediarys;
	}

	// 72. 转发日记id列表(3.0获取赞日记id列表)
	public static class forwardDiaryIDResponse {
		public String status; // 0成功
		public String crm_status;
		public String diary_time;
		public MyDiaryids[] diarieids;
	}

	// 73. 修改标签或位置(用户虚假位置信息)
	public static class modTagsOrPositionResponse {
		public String status; // 0成功
		public String crm_status;
	}

	// 74. 分享日记列表（显示自己的日记，分享到第三方的日记）-------增量更新
	public static class shareEnjoyDiaryResponse {
		public String status; // 0成功
		public String crm_status;
		public String first_diary_time;
		public String last_diary_time;
		public String is_refresh;
		public String hasnextpage;
		public MyDiary[] diaries;
		public String removediarys;
	}

	// 75. 个人主页
	public static class homeResponse {
		public String hasnextpage;//0 没有下一页 1 有下一页
		public String status; // 0成功
		public String crm_status;
		public String first_diary_time;
		public String last_diary_time;
		public String is_refresh;

		public String isattention;
		public String viewisattention;
		public String fanscount;
		public String attentioncount;

		public String newfanscount;
		public String background;
		public String headimageurl;
		public String nickname;

		public String signature;
		public String sex;
		public String moodurl;
		public MyDiary[] diaries;
		public String removediarys;
	}

	// 76. 删除转发和赞日记
	public static class deletepublishAndEnjoyResponse {
		public String status; // 0成功
		public String crm_status;
		
		public String diaryid;
	}

	// 79. 活动中奖日记列表
	public static class getAwardDiaryListResponse {
		public String status; // 0成功
		public String crm_status;
		public getAwardDiaryListItem[] awards;
//		public String removediarys;
	}

	// 81. 参加活动
	public static class joinActiveResponse {
		public String status; // 0成功
		public String crm_status;
	}

	// 82. 通知私信浏览完成（只用于日记，视频等）
	public static class notifyPrivmsgResponse {
		public String status; // 0成功
		public String crm_status;
	}

	// 83. 通知看过消息
	public static class notifyMessageResponse {
		public String status; // 0成功
		public String crm_status;
	}

	// 84. 发送通讯录
	public static class postAddressBookResponse {
		public String status; // 0成功
		public String crm_status;
	}

	// 85. 查看日记权限
	public static class diaryPermissionsResponse {
		public String status; // 0成功
		public String crm_status;
	}

	// 86. 设置日记心情
	public static class setMoodResponse {
		public String status; // 0成功
		public String crm_status;
	}

	// 87. 新消息列表
	public static class listMessageResponse {
		public String status; // 0:成功
		public String crm_status; //当status=200600，crm_status有效，crm状态请看（点击我）
		public String last_timemilli; //"1324689755413",  //最后一条消息创建时间
		public String server_time; //"1324689755413",  //服务器时间
		public String hasnextpage; //"1",  //1有下一页，0没有下一页
		public String commentid; //""，  //评论id
		public String commentnum; //""，  //评论数，当评论数返回N时，客户端显示点
		public String fansnum;//"10"，  //当前用户粉丝数
		public String attentionnum; //"10"，  //当前用户关注数

		public MessageUser[] users;
		public MyDiary[] diaries;
	}

	// 88. 清除陌生人消息
	public static class clearStrangerMessageResponse {
		public String status; // 0成功
		public String crm_status;
	}

	// 89. Crm回调，通知激活
	public static class crmCallbackResponse {
		public String status;
		public String crm_status;
	}

	// 90. www播放页（重新整理，改成JSON格式）
	public static class playPageResponse {
		public String json;
	}

	//91. 获取官方用户列表
	public static class getOfficialUseridsResponse {
		public String status;
		public String crm_status;
		public MyUserids[] userids;
	}
	
	//92. 获取MD5加密KEY
	public static class getMD5keyResponse {
		public String status;
		public String crm_status;
		public String json;
	}
	
	//93.	收藏日记id列表
	public static class listCollectDiaryidResponse{
		public String status;
		public String crm_status;
		public String diary_time;//第一条记录的时间
		public MyDiaryids[] diarieids;
	}
	
	// 95. 获取特效下载地址
	public static class getEffectResponse {
		public String status;
		public String crm_status;
		public Effects effects;
	}
	
	// 96.	解除保险箱
	public static class unSafeboxResponse {
		public String status;
		public String crm_status;
	}
	
	// 97.	取消活动
	public static class cancleActiveResponse {
		public String status;
		public String crm_status;
	}

	//100.	用户通讯录列表
	public static class phoneBookResponse {
		public String status;
		public String crm_status;
		public PhoneBookUser[] users;
	}
	
	public class PhoneBookUser {
		public String userid ;  //"userid": "11111",//好友ID
		public String isjoin ;  //"isjoin": "1"，//是否已加入looklook 1 已加入 0 未加入 
		public String headimageurl;  //"headimageurl":"http://…jpg",//头像URL，可为空
		public String nickname;  //"nickname":"昵称",//昵称（如果未加入则为通讯录中的联系人名称）
		public String phonenum;  //"phonenum"："13728654426", //通讯录中的手机号
		public String sex;  //"sex":"0" , // 0男 1女 2未知
		public String isattention;  //"isattention"  : 0   // 我是否关注他，0 未关注  1是已关注
		public String isattentionme;  //"isattentionme"  : 0   // 是否关注我，0 未关注  1是已关注
		@Override
		public boolean equals(Object object) {
			// TODO Auto-generated method stub
			if (!(object instanceof PhoneBookUser)) {
				return false;
			}
			PhoneBookUser phoneBookUser = (PhoneBookUser) object;
			if (this.phonenum.equals(phoneBookUser.phonenum)) {
				return true;
			}
			return false;
		}
	
	}
	
	//101.	获取云端空间大小
	public static class getCloudSizeResponse {
		public String status;
		public String crm_status;
		public String cloud_size;
	}
	
	// 102.	第三方互为关注的用户列表（并且已经注册looklook的用户）
	public static class listUserSNSResponse {
		public String status;
		public String crm_status;
		public UserSNS[] users;
	}
	
	public class UserSNS {
		 public String userid;  //"userid": "11111",//好友ID
		 public String headimageurl;  //"headimageurl":"http://…jpg",//头像URL，可为空
		 public String nickname;  //"nickname":"昵称",//昵称
		 public String sex;  //"sex":"0" , // 0男 1女 2未知
		 public String isattention;  //"isattention"  : 0   // 我是否关注他，0 未关注  1是已关注
		 public String isattentionme;  //"isattentionme"  : 0   // 是否关注我，0 未关注  1是已关注

		@Override
		public boolean equals(Object object) {
			// TODO Auto-generated method stub
			if (!(object instanceof UserSNS)) {
				return false;
			}
			UserSNS userSNS = (UserSNS) object;
			if (this.userid.equals(userSNS.userid)) {
				return true;
			}
			return false;
		}
	}
	
	//103.	后台推荐用户列表
	public static class listUserRecommendResponse{
		public String status;
		public String crm_status;
		public String timestamp; //用于回传的时间戳
		public UserSNS[] users;
	}
	
	public class Effects {
		public String effectsid; // 特效id
		public String effectsurl; // 特效url
		public String effectsname; // 特效名称
		public String weight; // 权重值
	}
	
	public class User {
		public String userid;
		public String portraiturl; // 头像URL，可为空
		public String nickname;
		public subUser[] tags;
		public String[] activeid;
		public String friendcount; // 1,好友数
		public String videocount; // 12,视频数
		public String category; // ”家人”, 好友分类只对好友列表有效
		public String categoryid; // 1111, 好友分类ID
		public String sex; // 0男，1女， 2未知
		public String isattention; // 0 未关注 1是已关注
		public String signature; // ”个性签名” , 是编码的，需要解码

	}

	public class subUser {
		public String subitem;
	}

	public static class MyBind {
		public MyBind(int curWeiboIndex, OAuthV2 oAuth) {
			// TODO Auto-generated constructor stub
			binding_type = "3";
			binding_info = "3";
			if(curWeiboIndex==SHARE_TO.SINA.ordinal()){
				snstype = "1";
			}else if(curWeiboIndex==SHARE_TO.RENREN.ordinal()){
				snstype = "2";
			}else if(curWeiboIndex==SHARE_TO.TENC.ordinal()){
				snstype = "6";
			}
			
			snsuid = oAuth.getOpenid();
			sns_nickname =  oAuth.getNick();
			sns_token = oAuth.getAccessToken();
			sns_expiration_time = oAuth.getExpiresTime();
			sns_effective_time = oAuth.getExpiresIn();
			sns_openkey = oAuth.getOpenkey();
			sns_refresh_token = oAuth.getRefreshToken();
			

		}
		public MyBind() {
			// TODO Auto-generated constructor stub
		}
		public String binding_type; // 1邮箱，2手机，3第三放平台
		public String binding_info; // binding_type!=3,1邮箱，2手机，3第三放平台 4备用手机
		public String snstype; // binding_type=3,有效，微博类型号，1：新浪微博，2人人，6腾讯
		public String snsuid; // binding_type=3,有效，//微博ID
		public String email_status; // 邮箱激活状态，1激活，0未激活,binding_type=2,有效
		public String sns_nickname;//":"第三方昵称"，  //第三方昵称
		public String sns_token;//":"第三方token"，  //第三方token
		public String sns_expiration_time;//":"第三方过期时间"，  //第三方过期时间(到时间点)
		public String sns_effective_time;//":"第三方有效时间"，  //第三方有效时间（时间段）
		public String sns_openkey;//":"第三方openkey"，  //第三方openkey,只有腾讯有效
		public String sns_refresh_token;//":"第三方刷新token"  //第三方刷新token,只有腾讯有效

	}

	public class MyAttach {
		public String attachid; // 附件id
		public String attachuuid; // 客户端创建日记附件的uuid，确定日记附件在客户端的唯一标识
		public String path; // 附件物理存放地址
	}
	
	public class MyDiaryids {
		public String diaryid; //日记id
		public String publish_type; //1有效，2删除
	}
	
	public class MyUserids {
		public String userid;  //用户id
		public String nickname;//昵称
		public String headimageurl;//头像地址
		public String type; //类型
	}
	
	public static class MyDiary{
		public int sync_status;//0-未同步  1-日记结构已创建 2-上传中 3-上传完成  4-已同步 5-下载中 6-已下载
//		public int src_type;//0-我的相册 1-其他（为此状态时认为日记sync_status>=4）
		public String join_safebox;//是否加入保险箱，1是 0否
		public String weather; // 天气
		public String weather_info;//天气描述
		public String mood; // 心情
		public String diaryid; //日记ID
		public String diaryuuid;
		public String weight;
		public String nickname;
		public String userid;  //用户id
		public String diarytimemilli;//日记建立毫秒数
		public String updatetimemilli;//日记修改毫秒数
		public String introduction;//简介，base64编码
		public String diary_status;// 0无效（删除） 1新建 2 发布
		public String publish_status;//1全部人可见 2关注人可见 3指定人可见 4仅自己可见
		public String position_status;//1全部人可见 2关注人可见 4仅自己可见，PUBLISH_status=1有效
		public String publishid;//微薄表id
		public ShareSNSTrace[] sns;//分享轨迹
		public String snscollect_sina;// 所有分享到新浪的微博id
		public String snscollect_tencent;// 所有分享到腾讯的微博id总和
		public String iscollect; // 1：已收藏，0：未收藏

		public TAG[] tags; // 选择标签信息

		public String position; // 位置信息，base64编码
		public String longitude; // 经度
		public String latitude; // 纬度
		public String sex; // 0男，1女， 2未知
		public String signature; // 个性签名,base64编码的
		// public String sharestatus;
		public String enjoycount; // 喜欢数
		public String commentcount;// 评论数
		public String forwardcount; // 转发数
		public String collectcount;// 收藏数
		public String shorturl;// 日记短连接
		public diaryAttach[] attachs;// 每种附件类型分开记录
		public MyActive active; // 活动
		public MyDuplicate[] duplicate; // 副本
		public String resourcediaryid;// 原日记id
		public String resourceuuid;// 原日记uuid

		public String size;

		public String headimageurl;
		
		public createStructureRequest request;
		
		@Override
		public boolean equals(Object object) {
			if (!(object instanceof MyDiary)) {
				return false;
			}
			MyDiary myDiary = (MyDiary) object;
			if (this.diaryuuid.equals(myDiary.diaryuuid)) {
				return true;
			}
			return false;
		}

		@Override
		public String toString() {
			return "MyDiary [diaryid=" + diaryid + "]";
		}
		
		public String getTagIds() {
			String tagIds = "";
			if (tags != null) {
				for (int i = 0;i < tags.length;i++) {
					tagIds += tags[i].id;
					if (i != tags.length - 1) {
						tagIds += ",";
					}
				}
			}
			return tagIds;
		}
		
		public void setTags(String tags) {
			if (tags == null || "".equals(tags)) {
				this.tags = null;
			}
			String [] ids = tags.split(",");
			if (ids == null || ids.length == 0) {
				this.tags = null;
			}
			ArrayList<taglistItem> tagList = DiaryManager.getInstance().getTags();
			if (tagList == null || tagList.size() == 0) {
				return;
			}
			ArrayList<TAG> arrayTagList = new ArrayList<GsonResponse2.TAG>();
			for(String tagid:ids) {
				for (taglistItem item:tagList) {
					if (tagid.equals(item.id)) {
						TAG tag = new TAG();
						tag.id = tagid;
						tag.name = item.name;
						arrayTagList.add(tag);
					}
				}
			}
			
			if (arrayTagList.size() > 0) {
				TAG [] tagArrays = arrayTagList.toArray(new TAG[arrayTagList.size()]);
				this.tags = tagArrays;
			}
			
		}
		
		public DiaryType getDiaryType(){
			if(attachs!=null&&attachs.length>0){
				for(int i=0;i<attachs.length;i++){
					diaryAttach attach=attachs[i];
					if("1".equals(attach.attachtype)&&"1".equals(attach.attachlevel)){//音频+辅内容
						return DiaryType.VEDIO;
					}
					if("2".equals(attach.attachtype)&&"1".equals(attach.attachlevel)){//音频+辅内容
						return DiaryType.AUDIO;
					}
					if("3".equals(attach.attachtype)&&"1".equals(attach.attachlevel)){//音频+辅内容
						return DiaryType.PICTURE;
					}

				}
			}
			
			if(attachs!=null&&attachs.length>0){
				for(int i=0;i<attachs.length;i++){
					diaryAttach attach=attachs[i];
					if("4".equals(attach.attachtype)){//音频+辅内容
						return DiaryType.TEXT;
					}
				}
			}
			
			
			return null;
		}
		
		public String getTextContent(){
			if(attachs!=null&&attachs.length>0){
				for(int i=0;i<attachs.length;i++){
					diaryAttach attach=attachs[i];
					if("4".equals(attach.attachtype)&&"1".equals(attach.attachlevel)){//文本+辅内容
						return attach.content;
					}
				}
			}
			return null;
		}
		
		public String getImageUrl(){
			if(attachs!=null&&attachs.length>0){
				for(int i=0;i<attachs.length;i++){
					diaryAttach attach=attachs[i];
					if("3".equals(attach.attachtype)&&"1".equals(attach.attachlevel)){//图片+辅内容
						MyAttachImage[] attachImage=attach.attachimage;
						if(attachImage!=null&&attachImage.length>0){
							//TODO 是否需要根据隐私设置给不同的url
							return attachImage[0].imageurl;
						}
					}
				}
			}
			return null;
		}
		
		public diaryAttach getAttach(String attachuuid) {
			if(attachs!=null&&attachs.length>0){
				for(int i=0;i<attachs.length;i++){
					diaryAttach attach=attachs[i];
					if (attach.attachuuid.equals(attachuuid)) {
						return attach;
					}
				}
			}
			return null;
		}
		
		//获取短录音url
		public String getShortRecUrl(){
			if(attachs!=null&&attachs.length>0){
				for(int i=0;i<attachs.length;i++){
					diaryAttach attach=attachs[i];
					if("2".equals(attach.attachtype)&&"0".equals(attach.attachlevel)){//短音频+辅内容
						MyAttachAudio[] attachAudio=attach.attachaudio;
						if(attachAudio!=null&&attachAudio.length>0){
							//TODO 是否需要根据隐私设置给不同的url
							if(attachAudio[0].audiourl!=null&&attachAudio[0].audiourl.length()>0){
								return attachAudio[0].audiourl;
							}else{
								return attach.attachuuid;
							}
						}
					}
				}
			}
			return null;
		}
		
		//获取长录音url
		public  String getLongRecUrl(){
			if(attachs!=null&&attachs.length>0){
				for(int i=0;i<attachs.length;i++){
					diaryAttach attach=attachs[i];
					if("2".equals(attach.attachtype)&&"1".equals(attach.attachlevel)){//长音频+主内容
						MyAttachAudio[] attachAudio=attach.attachaudio;
						if(attachAudio!=null&&attachAudio.length>0){
							//TODO 是否需要根据隐私设置给不同的url
							if(attachAudio[0].audiourl!=null&&attachAudio[0].audiourl.length()>0){
								return attachAudio[0].audiourl;
							}else{
								return attach.attachuuid;
							}
						}
					}
				}
			}
			return null;
		}
		
		//获取长录音url
		public  String getVideoUrl(){
			if(attachs!=null&&attachs.length>0){
				for(int i=0;i<attachs.length;i++){
					diaryAttach attach=attachs[i];
					if("1".equals(attach.attachtype)&&"1".equals(attach.attachlevel)){//视频+主内容
						MyAttachVideo[] attachVideo=attach.attachvideo;
						if(attachVideo!=null&&attachVideo.length>0){
							//TODO 是否需要根据隐私设置给不同的url
							if(attachVideo[0].playvideourl!=null&&attachVideo[0].playvideourl.length()>0){
								return attachVideo[0].playvideourl;
							} else {
								return attach.attachuuid;
							}
						}
					}
				}
			}
			return null;
		}
		
		public String getVideoCoverUrl() {
			if(attachs!=null&&attachs.length>0){
				for(int i=0;i<attachs.length;i++){
					diaryAttach attach=attachs[i];
					if("1".equals(attach.attachtype)&&"1".equals(attach.attachlevel)){//视频+主内容
						return attach.videocover;
					}
				}
			}
			return null;
		}
		
		public void setVideoCoverUrl(String coverUrl) {
			if(attachs!=null&&attachs.length>0){
				for(int i=0;i<attachs.length;i++){
					diaryAttach attach=attachs[i];
					if("1".equals(attach.attachtype)&&"1".equals(attach.attachlevel)){//视频+主内容
						attach.videocover = coverUrl;
					}
				}
			}
		}
		
		//获取图片url
		private String getImageUrlForCountSize(){
			String uid = ActiveAccount.getInstance(MainApplication.getInstance()).getUID();
			if(attachs!=null&&attachs.length>0){
				for(int i=0;i<attachs.length;i++){
					diaryAttach attach=attachs[i];
					if("3".equals(attach.attachtype)&&"1".equals(attach.attachlevel)){//音频+辅内容
						MyAttachImage[] attachImage=attach.attachimage;
						if(attachImage!=null&&attachImage.length>0){
							if(attachImage.length>1){
								for(int j=0;j<attachImage.length;j++){
									if(uid != null && uid.equals(userid)){//自己的日记显示无标图片
										if("0".equals(attachImage[j].imagetype)){
											if(attachImage[j].imageurl!=null&&attachImage[j].imageurl.length()>0){
												return attachImage[j].imageurl;
											}else{
												return attach.attachuuid;
											}
										}
									}else{//他人日记显示有标图片
										if("1".equals(attachImage[j].imagetype)){
											if(attachImage[j].imageurl!=null&&attachImage[j].imageurl.length()>0){
												return attachImage[j].imageurl;
											}else{
												return attach.attachuuid;
											}
										}
									}
								}
							}
							
							if(attachImage[0].imageurl!=null&&attachImage[0].imageurl.length()>0){
								return attachImage[0].imageurl;
							}else{
								return attach.attachuuid;
							}
						}
					}
				}
			}
			return null;
		}
		
		public String getDiarySize() {
			String userID = ActiveAccount.getInstance(ZApplication.getInstance()).getUID();
			int diarySize = 0;
			String videoUrl = getVideoUrl();
			if (videoUrl != null) {
				MediaValue mediaValue = AccountInfo.getInstance(userID).mediamapping.getMedia(userID, videoUrl);
				if (mediaValue != null) {
					diarySize += mediaValue.realSize;
				}
			}
			
			String longRecUrl = getLongRecUrl();
			if (longRecUrl != null) {
				MediaValue mediaValue = AccountInfo.getInstance(userID).mediamapping.getMedia(userID, longRecUrl);
				if (mediaValue != null) {
					diarySize += mediaValue.realSize;
				}
			}
			
			String imageUrl = getImageUrlForCountSize();
			System.out.println("====diary imageurl====" + imageUrl );
			if (imageUrl != null) {
				MediaValue mediaValue = AccountInfo.getInstance(userID).mediamapping.getMedia(userID, imageUrl);
				if (mediaValue != null) {
					diarySize += mediaValue.realSize;
				}
			}
			
			String shortRecUrl = getShortRecUrl();
			if (shortRecUrl != null) {
				MediaValue mediaValue = AccountInfo.getInstance(userID).mediamapping.getMedia(userID, shortRecUrl);
				if (mediaValue != null) {
					diarySize += mediaValue.realSize;
				}
			}
			
			/*String videoCoverUrl = getVideoCoverUrl();
				if (videoCoverUrl != null) {
					MediaValue mediaValue = AccountInfo.getInstance(userID).mediamapping.getMedia(userID, videoCoverUrl);
					if (mediaValue != null) {
						diarySize += mediaValue.realSize;
					}
				}*/
			
			
			return String.valueOf(diarySize);
//			if (size == null || "".equals(size)) {
//				
//			} else {
//				return "0";
//			}
			
		}
		/**
		 * 清除日记物理文件
		 */
		public void clear(){
			String userID = ActiveAccount.getInstance(ZApplication.getInstance()).getUID();
			String videoUrl = getVideoUrl();
			if (videoUrl != null) {
				MediaValue mediaValue = AccountInfo.getInstance(userID).mediamapping.getMedia(userID, videoUrl);
				if (mediaValue != null) {
					AccountInfo.getInstance(userID).mediamapping.deleteDiskFile(mediaValue);
				}
			}
			
			String longRecUrl = getLongRecUrl();
			if (longRecUrl != null) {
				MediaValue mediaValue = AccountInfo.getInstance(userID).mediamapping.getMedia(userID, longRecUrl);
				if (mediaValue != null) {
					AccountInfo.getInstance(userID).mediamapping.deleteDiskFile(mediaValue);
				}
			}
			
			String imageUrl = getImageUrl();
			if (imageUrl != null) {
				MediaValue mediaValue = AccountInfo.getInstance(userID).mediamapping.getMedia(userID, imageUrl);
				if (mediaValue != null) {
					AccountInfo.getInstance(userID).mediamapping.deleteDiskFile(mediaValue);
				}
			}
			
			String shortRecUrl = getShortRecUrl();
			if (shortRecUrl != null) {
				MediaValue mediaValue = AccountInfo.getInstance(userID).mediamapping.getMedia(userID, shortRecUrl);
				if (mediaValue != null) {
					AccountInfo.getInstance(userID).mediamapping.deleteDiskFile(mediaValue);
				}
			}
			
			String videoCoverUrl = getVideoCoverUrl();
			if (videoCoverUrl != null) {
				MediaValue mediaValue = AccountInfo.getInstance(userID).mediamapping.getMedia(userID, videoCoverUrl);
				if (mediaValue != null) {
					AccountInfo.getInstance(userID).mediamapping.deleteDiskFile(mediaValue);
				}
			}
			sync_status=4;//物理文件删除后,sync_status变成已同步
		}
	}

	static public class PrivMsg {
		public String content;
		public String privmsg_type;// //私信类型 --- 1代表纯文字 2代表语音  3代表日记   4语音加文字
		public String audiourl;// 语音地址
		public String playtime; //h:m:s 
		public MyDiary diaries;
	}

	static public class MyMessage {
		public MyMessage(){
			privmsg = new PrivMsg();
		}
		public String messageid;
		public String timemill;
		public String act;
		public String content;
		/*public MyDiary[] diaries;*/
		public PrivMsg privmsg;
	}

	public class MessageUser {
		public String userid; // 发送者用户ID
		public String nickname;// 昵称，base64编码,关注人如果有备注显示备注名称
		public String headimageurl;// 头像URL，可能为空
		public String isattention;
		public String sex;
		public String signature;
		public MyMessage[] message;
	}

	public class MySNS {
		public String snstype; // 微博类型号，0:looklook, 1：新浪微博, 2:人人, 3:开心, 4:街旁,
								// 5:qzone空间, 6:腾讯微博, 7:搜狐微博, 9:微信
		public String snsid; // 微博ID
	}

	public class ShareSNS {
		public String id;
		public String content; // ”分享的内容1111”, base64
		public ShareInfo[] shareinfo;
		public String sharetime; // ”2012-12-12 19:20:45” 分享时间

	}

	public class MyActive {
		public String activeid;
		public String activename;
		public String starttime;
		public String endtime;
		public String introduction;
		public String add_way;
		public String rule;
		public String prize;
		public String picture;
		public String isjoin;
		public String iseffective;
	}

	public class MyDuplicate {
		public String diaryid;
		public String duplicatename;// 副本名称，base64编码
	}

	public static class diaryAttach {
		public String attachid;
		public String attachuuid;
		public String attachtype;//附件类型，1视频、2音频、3图片、4文字
		public String attachlevel;//附件级别,区分是主文件还是辅助文件。1主内容、0辅内容

		public MyAttachImage[] attachimage;

		public String playtime;

		public MyAttachAudio[] attachaudio;

		public String attachtimemilli;
		public String playtimes;
		public String orshare;
		public String content;// 内容

		public MyAttachVideo[] attachvideo;

		public String videocover;
		public String pic_width;
		public String pic_height;
		public String show_width;
		public String show_height;

	}

	public static class MyAttachImage {
		public String imageurl;
		public String imagetype;
		public String imagesize;//附件大小（单位B）
	}

	public static class MyAttachAudio {
		public String audiourl;
		public String audiotype;
		public String audiosize;//附件大小（单位B）
	}

	public static class MyAttachVideo {
		public String videotype;
		public String playvideourl;// 长连接
		public String videosize;//附件大小（单位B）
	}

	public static class ShareSNSTrace {
		public String snscontent; // ”分享的内容1111”, base64
		public ShareInfo[] shareinfo;
		public String sharetime; // ”2012-12-12 19:20:45” 分享时间

	}

	public class platformUrls {
		public String snstype; // 社区类型
		public String url; // 链接地址

	}

	public static class MyWeatherDescription {
		public String description;// 天气描述
		public String weatherurl; // 天气图片地址
		public String date; // 日期
	}
	public class MyVideo {
		public String videoid;
		public String videoimage; // "http://.../123.jpg"
		public String videosharepath; // ”http://.../123.mp4”, 播放视频连接
		public String videotimemilli; // 视频建立毫秒数
		public String playtimes; // 播放次数
		public String createtime;
		public String videolength;
		public String sharetime;
		public String orshare; // 1:分享、2:未分享
		public String snscontent;// ”终于见到演员中的乔丹了”, 分享信息
		public ShareSNS[] sns;
		public String snscollect_sina; // ”1231,12312,12312” 所有分享到新浪的微博id
		public String snscollect_tencent; // ”12312,1231,12312” // 所有分享到腾讯的微博id

		// 下面的字段在视频详情里用到
		public String position; // "东八里庄公交站"
		public String commentcount; // 1111,评论数
		public String forwardcount; // 11121, 转发数
		public String collectcount; // 11, 收藏数
		public VideoPath[] videopath;
		public String iscollect; // 1：已收藏，0：未收藏
		public TAG[] tags;

		public String pic_width; // ”2131”, 封面本身的宽
		public String pic_height; // ”1231”, 封面本身的高
		public String show_width; // 封面显示的宽
		public String show_height; // 封面显示的高
		public String sex; // 0男，1女， 2未知
		public String signature; // 是编码的，需要解码
		public String sharestatus; // 1公开 2好友可见 4私密
		public String enjoycount; // 123123
		public String longitude; // "80.234"
		public String latitude; // 90.234

	}

	public class FriendVideo {
		public String userid; // 好友ID
		public String portraiturl; // ”http://…jpg”,好友头像URL
		public String nickname; // ”好友昵称”,好友昵称
		public String videoid; // 视频ID
		public String videoimage; // "http://.../123.jpg"
		public String videosharepath; // ”http://.../123.mp4”, 分享的视频连接
		public String videotimemilli; // in ms
		public String playtimes; // 40 //播放次数
		public String createtime;
		public String videolength;
		public String sharetime;

		// 分享信息
		public String snscontent; // 终于见到演员中的乔丹了
		public ShareSNS[] sns;
		public String snscollect_sina; // ”1231,12312,12312” 所有分享到新浪的微博id
		public String snscollect_tencent; // ”12312,1231,12312” // 所有分享到腾讯的微博id

		// 下面的字段在视频详情里用到
		public String position; // "东八里庄公交站"
		public String commentcount; // 1111,评论数
		public String forwardcount; // 11121, 转发数
		public String collectcount; // 11, 收藏数
		public VideoPath[] videopath;
		public String iscollect; // 1：已收藏，0：未收藏
		public TAG[] tags;

		public String pic_width; // ”2131”, 封面本身的宽
		public String pic_height; // ”1231”, 封面本身的高
		public String show_width; // 封面显示的宽
		public String show_height; // 封面显示的高
		public String sex; // 0男，1女， 2未知
		public String signature; // 是编码的，需要解码
		public String sharestatus; // 1公开 2好友可见 4私密
		public String enjoycount; // 123123
		public String longitude; // "80.234"
		public String latitude; // 90.234

	}

	public static class TAG {
		public String name; // ”标签1”
		public String id; //
	}

	public class nearVideoItem {

		// capture net:
		public String type;
		public String signature; // base64
		public String status;
		public String nickname; // base64
		public String sex;
		public String userid; //
		public String portraiturl;
		public String videoid;
		public String filetype;
		public VideoPath[] videopath;
		public String longitude; // "125.1234",//分享经度，可能为空
		public String latitude; // "16.1234”",//分享纬度可能为空
		public String position; // "东八里庄公交站" base64
		public String isforward;
		public String snscontent; // 终于见到演员中的乔丹了, base64
		public ShareSNS[] sns;
		public TAG[] tags; // TAG[]
		public String[] activeid; // --- to modiry !!!!!!!!
		public String commentcount; // 1111,评论数
		public String forwardcount; // 11121, 转发数
		public String videocontent; // base64
		public String forward_signature;
		public String forward_userid;
		public String forward_portraiturl;
		public String forward_nickname;
		public String forward_sex;
		public String forward_content;
		public String snscollect_renren;
		public String snscollect_kaixin;
		public String enjoycount;
		public String snscollect_sina; // "123445,"
		public String snscollect_tencent;
		public String sharestatus;
		public String isattention;
		public String pic_width; // "360"
		public String pic_height; // "480"
		public String show_width; // "320"
		public String show_height; // "427"
		public String distance; // "0"
		public String overstatus; // ""
		public String videoname;
		public String iscollect; // "0"
		public String collectcount; // "0"
		public String videoimage; // "http://...jpg"
		public String videosharepath; // "http://...mp4"
		public String videotimemilli; // "2013-02-04 22:43:57"
		public String playtimes; // "0"
		public String createtime;
		public String videolength;
		public String sharetime;
		public String orshare; // "1"

		// / --doc define
		/*
		 * public String userid; public String portraiturl;
		 * //http://…jpg,//好友头像URL public String nickname; //好友昵称 public String
		 * videoid; //视频ID public String videoimage; //"http://.../123.jpg"
		 * public String videosharepath; //”http://.../123.mp4”, //分享的视频连接
		 * public String videotimemilli; //11111111,//毫秒数 public String
		 * playtimes; //播放次数 public String longitude; //"125.1234",//分享经度，可能为空
		 * public String latitude; //"16.1234”",//分享纬度可能为空 public String
		 * distance; //视频与手机之间的绝对距离，单位：米 public String videolength; //”1:1:1”
		 * 
		 * //分享信息 public String snscontent; //终于见到演员中的乔丹了 ShareSNS[] sns; public
		 * String snscollect_sina; //”1231,12312,12312” 所有分享到新浪的微博id public
		 * String snscollect_tencent; //”12312,1231,12312” // 所有分享到腾讯的微博id
		 * 
		 * //下面的字段在视频详情里用到 public String position; //"东八里庄公交站" public String
		 * commentcount; //1111,评论数 public String forwardcount; //11121, 转发数
		 * VideoPath[] videopath; public String iscollect; //1：已收藏，0：未收藏 TAG[]
		 * tags;
		 * 
		 * public String pic_width; //”2131”, 封面本身的宽 public String pic_height;
		 * //”1231”, 封面本身的高 public String show_width; //封面显示的宽 public String
		 * show_height; //封面显示的高 public String enjoycount; //123123
		 */

	}

	// 分享信息
	public static class ShareInfo {

		public String snsid; // ”111111”, snstype为0的时候没有值
		public String snstype; // 0 本地 // 1新浪 // 6腾讯
		public String weiboid; // ”1212121”, snstype为0的时候没有
	}

	public class VideoPath {
		public String type; // 1高清、2普清、0原视频
		public String transcodeurl;
		public String videolongpath; // ”http://.../123.mp4” 长连接
	}

	public class getUserRemindItem {
		public String videoid;
		public String videotitle;// ”终于见到演员中的乔丹了”,第一个分享的内容，可为空
		public String promptcontent; // ”您这个视频被点击了100次”（100提醒、500、1000、2000、5000、10000）
	}

	public class listFriendNoteItem {
		public String read; // 1未读 2已读
		public String noteid; // 消息ID
		public String userid; // "11111",//好友ID
		public String nickname; // ”昵称”,//好友昵称
		public String portraiturl; // ”http://…jpg”,//头像URL，可能为空
		public String from; // ”新浪微博”,//来源
		public String timemill; // 1111111112121,//发布时间的毫秒数
		public String act; // 0：加入looklook，1：添加好友，2：发视频 3：文字提醒 4：评论提醒
		public String video; // ”http://…mpg”, 视频地址，只有act=2才有值
		public String videoid; // 11111,//视频ID
		public String videocover; // "http://..jpg",视频封面
		public String isattention; // 0 未关注 1是已关注
		public String signature; // ”个性签名” 是编码的，需要解码
		public String sex; //
		public String commentid; // act为4的时候生效
	}

	public class listFriendCategoryItem {
		public String categoryid; // 分类ID
		public String name; // 家人, 分类名
	}

	public class commentlistItem {
		public String userid; // 评论用户ID
		public String portraiturl; // ”http://…jpg”, 头像URL
		public String nickname; // ”昵称”,//昵称
		public String videoid;
		public String commentcontent;// ”好视频”, 评论
		public String commentid; // 1111, 评论ID
		public String createtime; // ”1616516519” 时间戳
		public String signature; // ”个性签名” 是编码的，需要解码
		public String sex; // 0男，1女， 2未知
		public String isattention; // 0 未关注 1是已关注
	}

	public class diarycommentlistItem {
		public String userid; // 评论用户ID
		public String headimageurl; // ”http://…jpg”, 头像URL
		public String nickname; // ”昵称”,//昵称
		public String diaryid;
		public String commentcontent;// ”好视频”, 评论
		public String commentid; // 1111, 评论ID
		public String createtime; // ”1616516519” 时间戳
		public String signature; // ”个性签名” 是编码的，需要解码
		public String sex; // 0男，1女， 2未知
		public String isattention; // 0 未关注 1是已关注
		public String audiourl;
		public String playtime; //语音播放时长
		public String commentway;
		public String commenttype;
		public CommentContent content;
		public String commentuuid;
	}
	
	public class CommentContent {
		public String content_type; //内容类型：1日记视频、2日记长录音、3日记图片、4日记文字、5日记短录音、6日记长录音加文字、7日记短录音加文字、8评论文字、9评论语音、10评论语音加文字
		public String imageurl; //日记视频封面，日记图片
		public String audiourl; //日记长录音，日记短录音，评论语音
		public String content; //日记文字内容，评论文字内容
	}

	public class getVideoUrlItem {
		public String snstype; // //社区类型,looklook
		public String url; // ”http://host:port/path/0/…mp4”
	}

	public class listMyNetVideoItem {
		public String videoid;
		public String overstatus; // 0 已传完 1未传完
		public String videoimage; // "http://.../123.jpg"
		public String videosharepath; // ”http://.../123.mp4”, 播放视频连接
		public String videotimemilli; // 视频建立毫秒数
		public String playtimes; // 播放次数
		public String createtime;
		public String videolength;
		public String sharetime;
		public String orshare; // 1:分享、2:未分享

		// 分享信息
		public String snscontent; // "终于见到演员中的乔丹了"
		public ShareSNS[] sns;
		public String snscollect_sina; // ”1231,12312,12312” 所有分享到新浪的微博id
		public String snscollect_tencent; // ”12312,1231,12312” // 所有分享到腾讯的微博id

		// 下面的字段在视频详情里用到
		public String position; // "东八里庄公交站"
		public String commentcount; // 1111,评论数
		public String forwardcount; // 11121, 转发数
		public String collectcount; // 11, 收藏数
		public VideoPath[] videopath;
		public String iscollect; // 1：已收藏，0：未收藏
		public TAG[] tags;

		public String pic_width; // ”2131”, 封面本身的宽
		public String pic_height; // ”1231”, 封面本身的高
		public String show_width; // 封面显示的宽
		public String show_height; // 封面显示的高
		public String sex; // 0男，1女， 2未知
		public String signature; // 是编码的，需要解码
		public String sharestatus; // 1公开 2好友可见 4私密
		public String enjoycount; // 123123
		public String longitude; // "80.234"
		public String latitude; // 90.234

	}

	public class searchUserItem {
		public String userid;
		public String portraiturl; // ”http://…jpg”, 头像URL，可为空
		public String nickname; // ”昵称”,//昵称
		public String friendcount; // 好友数
		public String videocount; // 视频数
		public String isfriend; // 0不是好友，1是好友
		public String sex; // 0男，1女， 2未知
		public String signature; // ”个性签名” 是编码的，需要解码
	}

	public class searchUsers {
		public String userid;
		public String headimageurl; // ”http://…jpg”, 头像URL，可为空
		public String nickname; // ”昵称”,//昵称
		public String diarycount; // 日记数
		public String attentioncount; // 关注数
		public String fanscount; // 粉丝数
		public String sex; // 0男，1女， 2未知
		public String signature; // ”个性签名” 是编码的，需要解码
		public String isattention; // 我是否关注他，0 未关注 1是已关注
		public String isattentionme; // 是否关注我，0 未关注 1是已关注

	}

	public class taglistItem {
		public String id;
		public String name; // 娱乐
		public String checked; // 1:被选中，0：未被选中
	}

	public class myattentionlistItem {
		public String userid; // 好友ID
		public String portraiturl; // "http://…jpg", 头像URL，可为空
		public String nickname; // 昵称
		public String friendcount; // 他的好友数
		public String videocount; // 视频数
		public String sex; // 0男 1女 2未知
		public String signature; // "jdfdf"个人签名（base64编码）
	}

	public class myattentionlistUsers {
		public String userid; // 好友ID
		public String headimageurl; // "http://…jpg", 头像URL，可为空
		public String nickname; // 昵称
		public String diarycount; // 日记数
		public String attentioncount; // 关注数
		public String fanscount;
		public String sex; // 0男 1女 2未知
		public String signature; // "jdfdf"个人签名（base64编码）
	}

	public class myfanslistItem {
		public String userid; // 好友ID
		public String portraiturl; // 头像URL，可为空
		public String nickname; // 昵称
		public String friendcount; // 他的好友数
		public String videocount; // 视频数
		public String sex; // 0男 1女 2未知
		public String isattention; // 0 未关注 1是已关注
		public String signature; // 个人签名（base64编码）
	}

	public class myfanslistUsers {
		public String userid; // 好友ID
		public String headimageurl; // "http://…jpg", 头像URL，可为空
		public String nickname; // 昵称
		public String diarycount; // 日记数
		public String attentioncount; // 关注数
		public String fanscount;
		public String isattention;
		public String sex; // 0男 1女 2未知
		public String signature; // "jdfdf"个人签名（base64编码）
	}

	public class myblacklistUsers {
		public String userid; // 好友ID
		public String headimageurl; // "http://…jpg", 头像URL，可为空
		public String nickname; // 昵称
		public String diarycount; // 日记数
		public String friendcount; // 他的好友数
		public String sex; // 0男 1女 2未知
		public String signature; // "jdfdf"个人签名（base64编码）
	}

	public class timelineItem {
		public String userid; // 好友ID
		public String portraiturl; // 好友头像URL
		public String nickname; // 好友昵称
		public String forward_userid; // 好友ID
		public String forward_portraiturl; // ”http://…jpg”,//好友头像URL
		public String forward_nickname; // 好友昵称
		public String forward_content; // 终于见到演员中的乔丹了
		public String forward_sex; // 0男 1女 2未知
		public String forward_signature; // ”个性签名” //是编码的，需要解码
		public String isforward; // 1是转发 0不是转发
		public String videoid; // 视频ID
		public String videoimage; // "http://.../123.jpg"
		public String videosharepath; // ”http://.../123.mp4”, //分享的视频连接
		public String videotimemilli; // 毫秒数
		public String playtimes; // 播放次数
		public String createtime;
		public String videolength;
		public String sharetime;
		// 分享信息
		public String snscontent;// "终于见到演员中的乔丹了"
		public ShareSNS[] sns;

		public String snscollect_sina; // ”1231,12312,12312” 所有分享到新浪的微博id
		public String snscollect_tencent; // ”12312,1231,12312” // 所有分享到腾讯的微博id

		// 下面的字段在视频详情里用到
		public String position; // "东八里庄公交站"
		public String commentcount; // 1111,评论数
		public String forwardcount; // 11121, 转发数
		public VideoPath[] videopath;
		public String iscollect; // 1：已收藏，0：未收藏
		public TAG[] tags;

		public String pic_width; // ”2131”, 封面本身的宽
		public String pic_height; // ”1231”, 封面本身的高
		public String show_width; // 封面显示的宽
		public String show_height; // 封面显示的高
		public String sex; // "0" 0男 1女 2未知
		public String isattention; // 0 未关注 1是已关注
		public String signature; // ”个性签名” //是编码的，需要解码
		public String enjoycount; // 123123
	}

	public class timelinePart {
		public String part_type; // 栏目类型：1推荐，2附近，3活动
		public String part_name; // 栏目名称
		public String part_count; // 栏目数量
		public String part_position; // 定位第几条显示
		public String partimgurl; // ”http://…jpg”
		public MyActive[] actives;
		public MyDiary[] diaries;

		@Override
		public String toString() {
			return "timelinePart [part_position=" + part_position + "]";
		}
	}

	public class getprivatemessageItem {
		public String userid; // 发送信息人ID
		public String content; // ”的佛教网i俄方年圣诞节覅违反 ”
		public String portraiturl; // 头像URL，可为空
		public String nickname; // 昵称
		public String isattention; // 是否关注
		public String sex; // ”男”
		public String signature; // ”个人签名”
		public String type; // 1代表纯文字 2代表视频加文字
		public String videoimage; //
		public VideoPath[] videopath;

	}

	public class getPrivateMessage {
		public String userid; // 发送信息人ID
		public String content; // ”的佛教网i俄方年圣诞节覅违反 ”
		public String headimageurl; // 头像URL，可为空
		public String nickname; // 昵称
		public String isattention; // 是否关注
		public String sex; // ”男”
		public String signature; // ”个人签名”
		public String privmsg_type; // 私信类型 --- 1代表纯文字 2代表日记 3代表语音
		public String audiourl; // 语音地址
		public MyDiary[] diaries;

	}

	public class videorecommendItem {
		public String userid;
		public String portraiturl;
		public String nickname; // 好友昵称
		public String videoid; // 视频ID
		public String videoimage; // "http://.../123.jpg"
		public String videosharepath; // ”http://.../123.mp4”, //分享的视频连接
		public String videotimemilli; // 毫秒数
		public String playtimes; // 40,//播放次数
		public String createtime;
		public String videolength;
		public String sharetime;

		// 分享信息
		public String snscontent;// "终于见到演员中的乔丹了"
		public ShareSNS[] sns;

		public String snscollect_sina; // ”1231,12312,12312” 所有分享到新浪的微博id
		public String snscollect_tencent; // ”12312,1231,12312” // 所有分享到腾讯的微博id

		// 下面的字段在视频详情里用到
		public String longitude;
		public String latitude;
		public String position; // "东八里庄公交站" base64
		public String commentcount; // 1111,评论数
		public String forwardcount; // 11121, 转发数
		public VideoPath[] videopath;
		public String iscollect; // 1：已收藏，0：未收藏
		public TAG[] tags;
		public String[] activeid;
		public String location_status;
		public String videocontent; // base64

		public String pic_width; // ”2131”, 封面本身的宽
		public String pic_height; // ”1231”, 封面本身的高
		public String show_width; // 封面显示的宽
		public String show_height; // 封面显示的高
		public String enjoycount; // 123123
	}

	public class commentlistforuserItem {
		public String userid; // 评论用户ID
		public String portraiturl; // 头像URL
		public String nickname; // 昵称
		public String videoid;
		public String commentcontent; // ”好视频”//评论
		public String commentid; //
	}

	public class listFriendIdItem {
		public String userid;
	}

	public class activeListItem implements Serializable {
		public String activeid; //活动ID
		public String activename; //活动名称
		public String starttime; //开始时间
		public String endtime; //结束时间
		public String introduction; //简介
		public String add_way; //参与方式
		public String rule; //活动规则
		public String prize; //奖品
		public String picture; //图片地址
		public String isjoin; //1代表参加  0 代表未参加
		public String iseffective; //"1结束 0可以参与 2未开始"
		
	}

	public class activeVideoListItem {
		public String videoid;
		public String videoimage; // "http://.../123.jpg"
		public String videosharepath; // ”http://.../123.mp4”, 播放视频连接
		public String videotimemilli; // 视频建立毫秒数
		public String playtimes; // 播放次数
		public String createtime;
		public String videolength;
		public String sharetime;
		public String orshare;// 1:分享、2:未分享

		// 分享信息
		public String snscontent;// "终于见到演员中的乔丹了"
		public ShareSNS[] sns;

		public String snscollect_sina; // ”1231,12312,12312” 所有分享到新浪的微博id
		public String snscollect_tencent; // ”12312,1231,12312” // 所有分享到腾讯的微博id

		// 下面的字段在视频详情里用到
		public String position; // "东八里庄公交站"
		public String commentcount; // 1111,评论数
		public String forwardcount; // 11121, 转发数
		public VideoPath[] videopath;
		public String iscollect; // 1：已收藏，0：未收藏
		public TAG[] tags;

		public String pic_width; // ”2131”, 封面本身的宽
		public String pic_height; // ”1231”, 封面本身的高
		public String show_width; // 封面显示的宽
		public String show_height; // 封面显示的高
		public String sex; // 0男，1女， 2未知
		public String signature; // ”个性签名” //是编码的，需要解码
		public String sharestatus; // 1公开 2好友可见 4私密
	}

	public class subAwardItem {
		public String userid; // 好友ID
		public String portraiturl; // 头像URL，可为空
		public String nickname; // 昵称
		public String friendcount; // 好友数
		public String videocount; // 视频数
		public String category; // ”家人”//好友分类只对好友列表有效
		public String categoryid; // 好友分类ID
		public String sex; // 0男，1女， 2未知
		public String isattention; // 0 未关注 1是已关注
		public String signature; // ”个性签名” //是编码的，需要解码
	}

	public class subAwardVideoItem {
		public String userid; // 好友ID
		public String portraiturl; // 头像URL，可为空
		public String nickname; // 昵称
		public String videoid;
		public String videoimage; // "http://.../123.jpg"
		public String videosharepath; // ”http://.../123.mp4”, //分享的视频连接
		public String videotimemilli; // 毫秒数
		public String playtimes; // 播放次数
		public String createtime;
		public String videolength;
		public String sharetime;

		// 分享信息
		public String snscontent;// "终于见到演员中的乔丹了"
		public ShareSNS[] sns;

		public String snscollect_sina; // ”1231,12312,12312” 所有分享到新浪的微博id
		public String snscollect_tencent; // ”12312,1231,12312” // 所有分享到腾讯的微博id

		// 下面的字段在视频详情里用到
		public String position; // "东八里庄公交站"
		public String commentcount; // 1111,评论数
		public String forwardcount; // 11121, 转发数
		public VideoPath[] videopath;
		public String iscollect; // 1：已收藏，0：未收藏
		public TAG[] tags;

		public String pic_width; // ”2131”, 封面本身的宽
		public String pic_height; // ”1231”, 封面本身的高
		public String show_width; // 封面显示的宽
		public String show_height; // 封面显示的高

		public String sex;
		public String enjoycount;
	}

	public class getAwardUserListItem {
		public String awarded;
		public String awardname; // 奖项名称
		public subAwardItem[] items;

	}

	public class getAwardDiaryListItem {
		public String awardid;
		public String awardname; // 奖项名称
		public MyDiary[] diaries;
		
		public String getAwardname() {
			return awardname;
		}

		public void setAwardname(String awardname) {
			this.awardname = awardname;
		}

		public void addItem(MyDiary[] diaries) {
			this.diaries = diaries;
		}

		public Object getItem(int position) {

			if (0 == position) {
				return awardname;
			} else {
				return diaries[position - 1];
			}
		}
		
		public int getItemCount () {
			if (diaries.length > 0) {
				return diaries.length + 1;
			}
			return 0;
		}
	}

	public class listThirdPlatformUserItem {
		public String userid; // 好友ID
		public String headimageurl; // ”http://…jpg”,//头像URL，可为空
		public String nickname; // 昵称
		public String diarycount; // 日记数
		public String attentioncount; // 关注数
		public String fanscount; // 粉丝数
		public String sex; // 0男，1女， 2未知
		public String signature; // ”个性签名” //是编码的，需要解码
		public String isattention;// 我是否关注他，0 未关注 1是已关注
		public String isattentionme;// 是否关注我，0 未关注 1是已关注
	}

	public class getVideoEnjoyUsersItem {
		public String userid; // 用户ID
		public String portraiturl; // 头像URL，可为空
		public String nickname; // 昵称
		public String friendcount; // 好友数
		public String videocount; // 视频数
		public String isfriend; // 0不是好友，1是好友
		public String sex; // 0男，1女， 2未知
		public String signature; // 是编码的，需要解码
		public String enjoycount; // 喜欢人总数
	}

	public class getDiaryEnjoyUsers {
		public String userid; // 用户ID
		public String headimageurl; // 头像URL，可为空
		public String nickname; // 昵称
		public String diaryocount; // 用户发布日记数
		public String sex; // 0男，1女， 2未知
		public String signature; // 是编码的，需要解码
		public String enjoycount; // 喜欢人总数
	}

	public class getVideoForwardUsersItem {
		public String userid; // 用户ID
		public String portraiturl; // 头像URL
		public String nickname; // 昵称
		public String friendcount; // 好友数
		public String videocount; // 视频数
		public String isfriend; // 0不是好友，1是好友
		public String sex; // 0男，1女， 2未知
		public String signature; // ”个性签名” //是编码的，需要解码
	}

	public class getDiaryForwardUsers {
		public String userid; // 用户ID
		public String update_time;// 修改时间
		public String headimageurl; // 头像URL
		public String nickname; // 昵称
		public String diarycount; // 日记数
		public String sex; // 0男，1女， 2未知
		public String signature; // ”个性签名” //是编码的，需要解码
	}

	public class getbackgroundlistItem {
		public String spacecoverurl; // 图片路径
		public String backgroundpath; // 头像URL，可为空
	}

	public class listEnjoyVideoItem {
		public String userid; // 好友ID
		public String portraiturl; // 好友头像URL
		public String nickname; // 好友昵称
		public String videoid; // 视频ID
		public String videoimage; // "http://.../123.jpg"
		public String videosharepath; // ”http://.../123.mp4”, //分享的视频连接
		public String videotimemilli; // 毫秒数
		public String playtimes; // 播放次数
		public String createtime;
		public String videolength;
		public String sharetime;

		// 分享信息
		public String snscontent;// "终于见到演员中的乔丹了"
		public ShareSNS[] sns;

		public String snscollect_sina; // ”1231,12312,12312” 所有分享到新浪的微博id
		public String snscollect_tencent; // ”12312,1231,12312” // 所有分享到腾讯的微博id

		// 下面的字段在视频详情里用到
		public String position; // "东八里庄公交站"
		public String commentcount; // 1111,评论数
		public String forwardcount; // 11121, 转发数
		public VideoPath[] videopath;
		public String iscollect; // 1：已收藏，0：未收藏
		public TAG[] tags;

		public String pic_width; // ”2131”, 封面本身的宽
		public String pic_height; // ”1231”, 封面本身的高
		public String show_width; // 封面显示的宽
		public String show_height; // 封面显示的高

		public String enjoycount; //
		public String isattention; // 0 未关注 1是已关注
	}
	
	public class uploadPictrue{
		public String status;
		public String imageurl;
	}
}
