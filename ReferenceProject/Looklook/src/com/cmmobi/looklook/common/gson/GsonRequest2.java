package com.cmmobi.looklook.common.gson;

import com.cmmobi.looklook.common.gson.GsonResponse2.PhoneBookUser;

import android.os.Handler;

/**
 * @author wangrui
 */

public class GsonRequest2 {

	// 1. 手机UA协议
	// HTTP POST http://v.looklook.cn/vs/api/ua
	public static class uaRequest {
		String imei; // "11111112222" 手机imei（手机的唯一标识）
		String imsi; // "460028105401115" 手机imsi（SIM卡的唯一标识）
		String resolution; // "480 x 800" 手机的分辨率
		String channel_number; // 渠道号
		String equipmentid; // 用于用户的手机登录服务器的唯一标识（设备id）
		String basestationinfo; // 基站信息{cid, lac, mcc, mnc}
		String gps; // 是一个位置的坐标信息(gps信息包括经纬度)
		String ip; // 手机ip地址
		String mac; // 手机的mac地址
		String devicetype; // 设备型号(设备名称)
		// 操作系统版本号 1:android1.6 2:ios 3:symbian V3 4:symbian V5
		String systemversionid;
		String internetway; // "WIFI" 联网方式
		String siteid; // 产品ID
		// String columnsversion;
		String cdr; // "aaaa,bbbb,dddd," Log日志
		// String errorlog; //" aaaa,bbbb,dddd"
		String clientversion; // 客户端版本号
		String sdk_imei;  //looklook的sdk里的唯一编码,   CmmobiClickAgent.getImei(context);
	}

	// 2. 用户注册
	// HTTP POST http://v.looklook.cn/vs/api/user/register
	// requestapp=
	public static class registerRequest {
		String equipmentid;
		String username; // 登录名（手机，email）,base64编码
		String password; // 密码,base64编码
		String nickname; // 昵称,base64编码
		String gps; // ”123.00:8733.00”
		String devicetoken; // "asdfasdfhasdkjaslfjasldfkalsfjad"//设备token值（推送信息唯一标识）
		String imei;
		String mobiletype; //1 ios 2 Andriod
		String sex; //0男 1女 2未知
		String address; //北京朝阳” 地址,base64编码
		String birthdate; //出生日期
		String signature;//tag; //”心情不错”//base64编码,可为空
		String registertype;//1:手机号 2或空：邮箱
		String check_no;//验证码，base64
		String mac;//mac地址
		String systemtype;//后台，手机端可以为空

	}

	// 3. 修改用户信息
	// HTTP POST /vs/api/user/changeUserInfo
	// requestapp=
	public static class changeUserInfoRequest {
		String userid;
		// String username;
		// //qwert@sample.com",//用户名,在用户名不存在时才会更新，否则返回status＝1,选填。输入为空时，不更新
		// String password;
		String nickname; // 昵称
		String equipmentid;
		String sex; // 0男 1女 2未知
		String birthdate; //
		String address; // 北京朝阳”
		String signature;// tag; //”心情不错”//个人签名,base64编码,可为空
		String mac;// 手机的mac地址
		String imei;// 手机imei（手机的唯一标识）
		String mood_id;

	}

	// 4. 修改登录密码、手势密码
	// HTTP POST http://v.looklook.cn/vs/api/user/passwordChange
	// requestapp=
	public static class passwordChangeRequest {
		String userid; // "9876543212345678",
		String oldpassword; //
		String newpassword;
		String pwd_type; // 1代表修改登录密码 2 代表修改手势密码
		String mac;// 手机的mac地址
		String imei;// 手机imei（手机的唯一标识）
	}

	// 5. 获取手机验证码
	// HTTP POST http://v.looklook.cn/vs/api/user/getCheckNo
	// requestapp=
	public static class CheckNoRequest {
		String username; // 手机号
		String check_type; // 验证类型，1注册，2绑定
		String mac;
		String imei;
	}

	// 6. 修改用户心情或个人签名
	// HTTP POST http://v.looklook.cn/vs/api/user/mood
	// requestapp=
	public static class moodRequest {
		String userid; // "9876543212345678",
		String mood_id; // 心情ID
		String signature; //个人签名
		String mac; // 手机的mac地址
		String imei; // 手机imei（手机的唯一标识）
	}

	// 7. 隐私设置
	// HTTP POST http://v.looklook.cn/vs/api/diary/privacy
	// requestapp=
	public static class diaryPrivacyRequest {
		String userid; // "9876543212345678",
		String privmsg_type; // 谁可以给我发私信，1全部人可见（黑名单人除外） 2关注人可见 4仅自己可见
		String friends_type; // 谁可以看我的朋友关系，1全部人可见（黑名单人除外） 2关注人可见 4仅自己可见
		String diary_type; // 谁可以看我内容（日记和评论），1全部人可见（黑名单人除外） 2关注人可见 4仅自己可见
		String position_type; // 谁可以看我的位置，1全部人可见（黑名单人除外） 2关注人可见 4仅自己可见
		String audio_type; // 谁可以听我的语音，1全部人可见（黑名单人除外） 2关注人可见 4仅自己可见
		String audio_encrypt_type;// 加密类型， audio_type=2或4，有效
		String launch_type; // 启动1观看模式 2摄影模式
		String sync_type; // 数据同步，1关闭 2仅WIFI 3任何网络
		String mac; // 手机的mac地址
		String imei; // 手机imei（手机的唯一标识）
	}

	// 8. 添加手势密码
	// HTTP POST http://v.looklook.cn/vs/api/user/addGesturepassword
	// requestapp=
	public static class addGesturePasswordRequest {
		String userid; // "9876543212345678",
		String gesturepassword; // "123456" 手势密码
		String mac; // 手机的mac地址
		String imei; // 手机imei（手机的唯一标识）
	}

	// 9. 判断用户是否有效（专供socket使用）
	// HTTP POST http://v.looklook.cn/vs/api/user/checkuser
	// requestapp=
	public static class checkUserRequest {
		String userid; // "9876543212345678",
		String mac; // "123456" 可为空
		String imei; // "123456" 可为空
	}

	// 10. 用户登录
	// HTTP POST/vs/api/user/login?
	// requestapp=
	public static class loginRequest {
		String username;// "zhangsan",//snstype>0时无这个值
		String password; // "asdf1234",//snstype>0时无这个值
		String snsuid; // 123456",//snstype=0时无这个值
		String equipmentid;
		String snstype; // ,//0站内登录、1新浪微博、6腾讯微博
		String devicetoken; // asdfasdfhasdkjaslfjasldfkalsfjad"//设备devicetoken，用于收消息，可以为空

		// 如果snstype>0 以下字段有效
		String nickname; // 昵称",//用户昵称
		String gps; // "123.00:8733.00”
		// String imei; //"67-0987786543”
		String mobiletype; // 1 ios 2 Andriod
		String sex; // snstype>0时
		String birthdate; // snstype>0时
		String address; // snstype>0时,
		String signature;// tag; //"心情不错"//可为空
		String logintype; // 1邮箱登录，2手机号登录snstype=0有效
		String access_token;// 第三方token值snstype>0时有效
		String expires_in;// 过期时间snstype>0时有效（时间段）
		String expiration_time; //第三方有效时间(到时间点)
		String refresh_token;// 刷新token,只有腾讯，人人有snstype>0时有效
		String snsname;// name第三方用户名，只有腾讯（腾讯字段name）snstype>0时有效
		String openkey;// 只有腾讯snstype>0时有效
		String mac; // 手机的mac地址
		String imei; // 手机imei（手机的唯一标识）
	}

	// 11. 获取Socket IP 端口
	// HTTP POST http://v.looklook.cn/vs/api/user/getSocket
	// requestapp=
	public static class getSocketRequest {
		public String userid;
		public String mac; // 手机的mac地址
		public String imei; // 手机imei（手机的唯一标识）
		// public String filetype; //”.mp4”
	}

	// 12. 日记结构管理
	// HTTP POST/vs/api/user/structureManager
	// requestapp=
	public static class createStructureRequest {
		public String userid; // 用户ID
		public String diaryid;
		public String diaryuuid;
		public String resourcediaryid;
		public String resourcediaryuuid;
		public String operate_diarytype;
		public String createtime;
		public Attachs attachs[];
		public String tags;
		public String logitude;
		public String latitude;
		public String userselectposition;
		public String userselectlogitude;
		public String userselectlatitude;
		public String position_source;
		public String addresscode;
		public String mac; // 手机的mac地址
		public String imei; // 手机imei（手机的唯一标识）
	}

	// 13. 发布/取消发布
	// HTTP POST/vs/api/diary/publish？Requestapp=
	public static class diaryPublishRequest {
		public String userid; // 用户ID
		public String diaryid;
		public String publish_type;// 发布类型，1发布，2取消发布
		public String diary_type;
		public String position_type;
		public String audio_type;
		public String mac; // 手机的mac地址
		public String imei; // 手机imei（手机的唯一标识）
	}

	// 14. 获取天气
	// HTTP POST/vs/api/diary/publish？
	// requestapp=
	public static class getWeatherRequest {
		String userid; // 用户ID
		String addresscode;// 地址的国际码
		String mac; // 手机的mac地址
		String imei; // 手机imei（手机的唯一标识）
	}

	// 15. 分享
	// HTTP POST/vs/api/shareDiary
	// requestapp=
	public static class shareDiaryRequest {
		public String userid; // 用户ID
		public String position;// 分享位置，base64编码,可能为空
		public String longitude;
		public String latitude;
		public String diaryid;
		public String snscontent;
		public String mac; // 手机的mac地址
		public String imei; // 手机imei（手机的唯一标识）
		public SNS[] sns;
		// String diary_type;
		// String position_type;
		// String audio_type;
	}

	// 16. 我的日记
	// HTTP POST http://v.looklook.cn/vs/api/listMyDiary
	// requestapp=
	public static class listMyDiaryRequest {
		String pagesize; // 10，每页记录数
		String userid; // "11111" // 当前操作人
		String viewuserid; // ”22222” // 需要显示的视频用户id
		String diary_time; // 每页记录数，第一次请求为空
		String request_type;// 1新内容加载，2历史内容加载，第一次请求为空
		String diarywidth; // ”12313”, // 封面需要显示的宽度，可以为空
		String diaryheight; // ”22322” // 封面需要显示的高度，可以为空
		String mac; // 手机的mac地址
		String imei; // 手机imei（手机的唯一标识）
		String cloud_type;// 1云端，2我的日记
		String safebox_type;// 1开启保险箱，2未开启保险箱，cloud_type=1有效，显示我的日记包括我的 保险箱内容
		//

	}

	// //16. 我的分享日记
	// //HTTP POST http://v.looklook.cn/vs/api/listMyPublishDiary
	// //requestapp=
	// public static class listMyPublishDiaryRequest{
	// String pagesize; //10，每页记录数
	// String userid; //"11111" // 当前操作人
	// String viewuserid; //”22222” // 需要显示的视频用户id
	// String request_time; //每页记录数，第一次请求为空
	// String request_type;//1新内容加载，2历史内容加载，第一次请求为空
	// String diarywidth; //”12313”, // 封面需要显示的宽度，可以为空
	// String diaryheight; //”22322” // 封面需要显示的高度，可以为空
	//
	// }

	// 17. 标签日记列表
	// HTTP POST http://v.looklook.cn/vs/api/tagDiaryList
	// requestapp=
	public static class tagDiaryListRequest {
		String pagesize; // 10，每页记录数
		String userid; // "11111" // 当前操作人
		String viewuserid; // ”22222” // 需要显示的视频用户id
		String diary_time; // 每页记录数，第一次请求为空
		String request_type;// 1新内容加载，2历史内容加载，第一次请求为空
		String diarywidth; // ”12313”, // 封面需要显示的宽度，可以为空
		String diaryheight; // ”22322” // 封面需要显示的高度，可以为空
		String mac; // 手机的mac地址
		String imei; // 手机imei（手机的唯一标识）
		String tagid; // 标签id
	}

	// 18. 保险箱日记列表
	// HTTP POST http://v.looklook.cn/vs/api/diary/listsafebox
	// requestapp=
	public static class listsafeboxRequest {
		String pagesize; // 10，每页记录数
		String userid; // "11111" // 当前操作人
		String diary_time; // 每页记录数，第一次请求为空
		String request_type;// 1新内容加载，2历史内容加载，第一次请求为空
		String diarywidth; // ”12313”, // 封面需要显示的宽度，可以为空
		String diaryheight; // ”22322” // 封面需要显示的高度，可以为空
		String mac; // 手机的mac地址
		String imei; // 手机imei（手机的唯一标识）
	}

	// 19. 日记加入、删除保险箱
	// HTTP POST http://v.looklook.cn/vs/api/diary/safebox
	// requestapp=
	public static class safeboxRequest {
		public String userid;
		public String diaryid;
		public String diaryuuid;
		public String type;
		public String mac; // 手机的mac地址
		public String imei; // 手机imei（手机的唯一标识）
	}

	// 20. 编辑、添加日记文本（辅件）
	// HTTP POST http://v.looklook.cn/vs/api/diary/attachcontent
	// requestapp=
	public static class attachContentRequest {
		String userid;
		String diaryid;
		String attachcontent;// 日记辅内容文本、base64编码
		String tags;// 标签集合
		String operate_diarytype;// 操作类型，1覆盖，2新建，3副本
		String mac; // 手机的mac地址
		String imei; // 手机imei（手机的唯一标识）
	}

	// 21. 列表收藏日记
	// HTTP POST http://v.looklook.cn/vs/api/listCollectDiary
	// requestapp=
	public static class listCollectDiaryRequest {
		String pagesize; // 10，每页记录数
		String userid; // "11111" // 当前操作人
		String viewuserid; // ”22222” // 需要显示的视频用户id
		String diary_time; // 每页记录数，第一次请求为空
		String request_type;// 1新内容加载，2历史内容加载，第一次请求为空
		String diarywidth; // ”12313”, // 封面需要显示的宽度，可以为空
		String diaryheight; // ”22322” // 封面需要显示的高度，可以为空
		String mac; // 手机的mac地址
		String imei; // 手机imei（手机的唯一标识）
	}

	// 22. 收藏日记
	// HTTP POST http://v.looklook.cn/vs/api/collect/addcollectDiary
	// requestapp=
	public static class addCollectDiaryRequest {
		public String userid; // 用户ID
		public String diaryid; // 日记ID
		public String mac; // 手机的mac地址
		public String imei; // 手机imei（手机的唯一标识）
	}

	// 23. 收藏删除
	// HTTP POST http://v.looklook.cn/vs/api/collect/removeCollectDiary
	// requestapp=
	public static class removeCollectDiaryRequest {
		public String userid; // 用户ID
		public String diaryids; // 多个日记ID，用逗号进行分隔
		public String mac; // 手机的mac地址
		public String imei; // 手机imei（手机的唯一标识）
	}

	// 24. 发送日志信息（客户端CDR话单），提醒产品要记录那些信息
	// HTTP POST http://v.looklook.cn/vs/api/postLog
	// requestapp=
	public static class postLogRequest {
		String userid; // 用户ID
		String log; // "444，444，444，444" CDR话单字符串
		String mac; // 手机的mac地址
		String imei; // 手机imei（手机的唯一标识）
	}

	// 25. 意见反馈
	// HTTP POST http://v.looklook.cn/vs/api/feedback
	// requestapp=
	public static class feedbackRequest {
		String userid;
		String equipmentid; // ”1234”//设备id
		String commentcontent; // ”这个应用真好玩，帮了我大忙了！！哈哈”
		String mac; // 手机的mac地址
		String imei; // 手机imei（手机的唯一标识）
	}

	// 26. 绑定
	// HTTP POST http://v.looklook.cn/vs/api/binding
	// requestapp=
	public static class bindingRequest {
		String userid;
		String binding_type; // 1邮箱，2手机，3第三放平台
		String phone_type; // 1为绑定主手机号，2绑定备用手机号
		String binding_info; // "13823236666或123456@qq.com",binding_type!=3有效,1邮箱，2手机，3第三放平台
		String snstype;// binding_type=3,有效，微博类型号，1：新浪微博，2人人，6腾讯
		String snsuid; // binding_type=3,有效，//微博ID
		String check_no;// 验证码，base64，binding_type=2有效
		String mac; // 手机的mac地址
		String imei; // 手机imei（手机的唯一标识）
		String equipmentid;// 设备id
		String gps;// 是一个位置的坐标信息(gps信息包括经纬度)
		String sex;// 性别 0男 1女 2未知,snstype>0时
		String address;// snstype>0时，国标编码
		String birthdate;// 出生日期,snstype>0时
		String access_token;// 第三方token值snstype>0时有效
		String expires_in;// 过期时间snstype>0时有效
		String expiration_time; 
		String refresh_token;// 刷新token,只有腾讯，人人有snstype>0时有效
		String snsname;// name第三方用户名，只有腾讯（腾讯字段name）snstype>0时有效
		String openkey;// 只有腾讯snstype>0时有效
		String nickname; //第三方昵称snstype>0时有效
	}

	// 27. 发送第三方互粉
	// HTTP POST http://v.looklook.cn/vs/api/postSNSFriend
	// requestapp=
	public static class postSNSFriendRequest {
		String userid;
		String snstype;
		String upload_type;// 上传类型，1：第一次上传，2：1+n次上传
		String mac; // 手机的mac地址
		String imei; // 手机imei（手机的唯一标识）
		postFollowItem[] snsfriends;
	}

	// 28. 自动登录设置deviceToken绑定
	// HTTP POST http://v.looklook.cn/vs/api/user/autoLogin
	// requestapp=
	public static class autoLoginRequest {
		String userid;
		String devicetoken;
		String mac;
		String imei;
	}

	// 29. 举报
	// HTTP POST http://v.looklook.cn/vs/api/report
	// requestapp=
	public static class reportRequest {
		String userid;
		String diaryid;
		String content;
		String mac; // 手机的mac地址
		String imei; // 手机imei（手机的唯一标识）
	}

	// 30. 给后台发第三方社区转发数、评论数、回复数
	// HTTP POST http://v.looklook.cn/vs/api/postWeiboCount
	// requestapp=
	public static class postWeiboCountRequest {
		String userid;
		String diaryid;
		String mac; // 手机的mac地址
		String imei; // 手机imei（手机的唯一标识）
		postWeibocountItem[] sns;
	}

	// 31. 忘记密码
	// HTTP POST http://v.looklook.cn/vs/api/user/forgetPassword
	// requestapp=
	public static class forgetPasswordRequest {
		String username; // ”a@example.com”
		String registertype;// 1:手机号 2或空：邮箱
		String pwd_type;
		String equipmentid;
		String userid;
		String mac;
		String imei;
	}

	// 32. 日记评论列表
	// HTTP POST http://v.looklook.cn/vs/api/diaryCommentList
	// requestapp=
	public static class diaryCommentListRequest {
		String userid;
		String comment_time;// 每页记录数，第一次请求为空
		String request_type;// 1新内容加载，2历史内容加载，第一次请求为空
		String diaryid;// 日记ID
		String viewuserid;// 用户显示ID
		String pagesize;// 页数
		String mac; // 手机的mac地址
		String imei; // 手机imei（手机的唯一标识）
	}

	// 33. 发表/回复评论
	// HTTP POST http://v.looklook.cn/vs/api/comment？
	// requestapp=
	public static class commentRequest {
		public String userid;
		public String diaryid;// 日记ID
		public String commentcontent;// 评论内容 base64编码
		public String isreply;
		public String commentid;
		public String commenttype;
		public String mac; // 手机的mac地址
		public String imei; // 手机imei（手机的唯一标识）
		public String commentuuid;////评论UUID，当前评论uuid
	}

	// 34. 删除日记
	// HTTP POST http://v.looklook.cn/vs/api/deleteDiary？
	// requestapp=
	public static class deleteDiaryRequest {
		public String userid;
		public String diaryids;// 日记ID集合，用","号分割
		public String changetomaindiaryuuids;//新主日记ID集合，用","号分割
		public String mac; // 手机的mac地址
		public String imei; // 手机imei（手机的唯一标识）
	}

	// 35. 获取日记分享URL
	// HTTP POST http://v.looklook.cn/vs/api/getDiaryUrl
	// requestapp=
	public static class getDiaryUrlRequest {
		public String diaryid;// 日记ID
		public String mac; // 手机的mac地址
		public String imei; // 手机imei（手机的唯一标识）
	}

	// 36. 日记详情
	// HTTP POST http://v.looklook.cn/vs/api/diaryInfo
	// requestapp=
	public static class diaryInfoRequest {
		String userid;
		String diaryid;// 日记id
		String diarywidth; // 封面需要显示的宽度，可以为空
		String diaryheight; // 封面需要显示的高度，可以为空
		String mac; // 手机的mac地址
		String imei; // 手机imei（手机的唯一标识）
	}

	// 37. 验证注册用户名是否可用
	// HTTP POST http://v.looklook.cn/vs/api/user/checkUserNameExist
	// requestapp=
	public static class checkUserNameExistRequest {
		String equipmentid;
		String username; // base64编码
		// String registertype; //1:手机号 2或空：邮箱
		String mac; // 手机的mac地址
		String imei; // 手机imei（手机的唯一标识）
	}

	// 38. 验证注册昵称是否可用------（第三方用户昵称规则？）
	// HTTP POST http://v.looklook.cn/vs/api/user/checkNickNameExist
	// requestapp=
	public static class checkNickNameExistRequest {
		// String equipmentid;
		String nickname; // base64编码
		// String registertype; //1:手机号 2或空：邮箱
		String mac; // 手机的mac地址
		String imei; // 手机imei（手机的唯一标识）
	}

	// 39. 搜索用户列表（除去系统黑名单）
	// HTTP POST http://v.looklook.cn/vs/api/searchUser
	// requestapp=
	public static class searchUserRequest {
		String userid;
		String keyword; // 搜索的关键字
		String pageno; // 页数
		String pagesize; // 每页条数
		String mac; // 手机的mac地址
		String imei; // 手机imei（手机的唯一标识）
	}

	// 40. 搜索附近日记（除自己日记外、系统黑名单）
	// HTTP POST http://v.looklook.cn/vs/api/nearDiary
	// requestapp=
	public static class nearDiaryRequest {
		String pageno;
		String pagesize;
		String userid;
		String longitude;
		String latitude;
		String diarywidth;
		String diaryheight;
		String mac; // 手机的mac地址
		String imei; // 手机imei（手机的唯一标识）
	}

	// 41. 获取标签
	// HTTP POST http://v.looklook.cn/vs/api/video/taglist?requestapp=
	public static class taglistRequest {
		String userid; // "9876543212345678",
		String diaryid;
		String mac; // 手机的mac地址
		String imei; // 手机imei（手机的唯一标识）
	}

	// 42. 关注
	// HTTP POST http://v.looklook.cn/vs/api/user/attention?requestapp=
	public static class attentionRequest {
		String userid; // "9876543212345678",
		String attention_userid;
		String mac; // 手机的mac地址
		String imei; // 手机imei（手机的唯一标识）
	}

	// 43. 取消关注\删除粉丝
	// HTTP POST http://v.looklook.cn/vs/api/user/cancelattention?requestapp=
	public static class cancelattentionRequest {
		public String userid; // "9876543212345678",
		public String target_userid;
		public String attention_type; // 1:取消关注 2：删除粉丝
		public String mac; // 手机的mac地址
		public String imei; // 手机imei（手机的唯一标识）
	}

	// 44. 关注人备注
	// HTTP POST http://v.looklook.cn/vs/api/user/cancelattention?requestapp=
	public static class markattentionRequest {
		String userid; // "9876543212345678",
		String attention_userid;
		String attention_mark; // "张三" 关注人备注，一般是姓名，base64编码
		String mac; // 手机的mac地址
		String imei; // 手机imei（手机的唯一标识）
	}

	// 45. 设置黑名单（用户黑名单）
	// HTTP POST http://v.looklook.cn/vs/api/user/operateblacklist?
	// requestapp=
	public static class operateblacklistRequest {
		public String userid; // "9876543212345678",
		public String target_userid;
		public String operatetype; // 操作类型 1加入黑名单 2移出黑名单
		public String mac; // 手机的mac地址
		public String imei; // 手机imei（手机的唯一标识）
	}

	// 46. 我的关注列表
	// HTTP POST http://v.looklook.cn/vs/api/user/myattentionlist?requestapp=
	public static class myattentionlistRequest {
		String userid; // "9876543212345678",
		String pagesize; // 10，每页记录数
		// String sex; //1 // 0男，1女， “”是全部
		String user_time;
		String viewuserid;// 显示用户id
		String mac; // 手机的mac地址
		String imei; // 手机imei（手机的唯一标识）
	}

	// 47. 我的粉丝
	// HTTP POST http://v.looklook.cn/vs/api/user/myfanslist?requestapp=
	public static class myfanslistRequest {
		String userid; // "9876543212345678",
		String pagesize; // 10，每页记录数
		// String sex; //1 // 0男，1女， “”是全部
		String user_time;// 上次请求，最后一条记录的时间
		String viewuserid; // 显示用户id
		String mac; // 手机的mac地址
		String imei; // 手机imei（手机的唯一标识）
	}

	// 48. 用户黑名单列表
	// HTTP POST http://v.looklook.cn/vs/api/user/myblacklist?requestapp=
	public static class myblacklistRequest {
		String userid; // "9876543212345678",
		String pagesize; // 10，每页记录数
		// String sex; //1 // 0男，1女， “”是全部
		String user_time;
		String viewuserid; // 显示用户id
		String mac; // 手机的mac地址
		String imei; // 手机imei（手机的唯一标识）
	}

	// 49. 朋友圈
	// HTTP POST http://v.looklook.cn/vs/api/timeline?requestapp=
	public static class timelineRequest {
		String userid; // "9876543212345678",
		String pageno; // 1,页号
		String pagesize; // 10，每页记录数

		String diarywidth; // ”12313”, // 封面需要显示的宽度，可以为空
		String diaryheight; // ”22322” // 封面需要显示的高度，可以为空

		String longitude;
		String latitude;
		
		String mac; // 手机的mac地址
		String imei; // 手机imei（手机的唯一标识）
	}

	// 50. 发私信
	// HTTP POST http://v.looklook.cn/vs/api/sendmessage?requestapp=
	public static class sendmessageRequest {
		public String userid; // "9876543212345678",
		public String target_userids;// 目标用户id
		public String content; // 内容
		public String privatemsgtype;
		public String diaryid;
		public String mac; // 手机的mac地址
		public String imei; // 手机imei（手机的唯一标识）
		public String uuid;
	}

	// 50. 获取私信
	// HTTP POST http://v.looklook.cn/vs/api/getprivatemessage?requestapp=
	public static class getprivatemessageRequest {
		String userid; // "9876543212345678",
		String receive_userid;
	}

	// 51. 日记推荐
	// HTTP POST http://v.looklook.cn/vs/api/videorecommend?requestapp=
	public static class diaryrecommendRequest {
		String userid; // "9876543212345678",
		String pagesize; // 10，每页记录数
		String diarywidth; // ”12313”, // 封面需要显示的宽度，可以为空
		String diaryheight; // ”22322” // 封面需要显示的高度，可以为空
		String pageno;//页码，第几页
//		String diary_time; // 每页记录数，第一次请求为空
//		String request_type; // 1新内容加载，2历史内容加载，第一次请求为空
		String mac; // 手机的mac地址
		String imei; // 手机imei（手机的唯一标识）
	}

	// 52. 喜欢(赞)并转发
	// HTTP POST http://v.looklook.cn/vs/api/enjoy?requestapp=
	public static class enjoyRequest {
		String userid; // "9876543212345678",
		String diaryid;
		String publishid;
		String mac; // 手机的mac地址
		String imei; // 手机imei（手机的唯一标识）
	}

	// 55. 解除绑定
	// HTTP POST http://v.looklook.cn/vs/api/user/unbind?requestapp=
	public static class unbindRequest {
		String userid; // "9876543212345678",
		String binding_type;
		String phone_type;
		String binding_info;
		String snstype;
		String snsuid;
		String mac; // 手机的mac地址
		String imei; // 手机imei（手机的唯一标识）
		String equipmentid;
	}

	// 56. 删除评论（删除自己评论和删除对自己视频进行的评论）
	// HTTP POST http://v.looklook.cn/vs/api/user/deleteComment?requestapp=
	public static class deleteCommentRequest {
		public String userid; // "9876543212345678",
		public String diaryid;
		public String commentid;
		public String mac; // 手机的mac地址
		public String imei; // 手机imei（手机的唯一标识）
		public String commentuuid;
	}

	// 57. 设置视频封面（供图片服务器访问）
	// HTTP POST http://v.looklook.cn/vs/api/diary/setVideoCover
	// requestapp=
	public static class setVideoCoverRequest {
		String attachid; // 附件id
		String imagepath; // ”201212/03/543213609KJH76.jpg” //视频路径
		String width;
		String height;
		String mac; // 手机的mac地址
		String imei; // 手机imei（手机的唯一标识）
	}

	// 58. 设置用户头像（供图片服务器访问）
	// HTTP POST http://v.looklook.cn/vs/api/user/setHeadImage
	// requestapp=
	public static class setHeadImageRequest {
		String userid; // "9876543212345678",
		String imagepath; // ”201212/03/543213609KJH76.jpg” 头像路径
		String mac; // 手机的mac地址
		String imei; // 手机imei（手机的唯一标识）
	}

	// 59. 获取活动列表（缓存）
	// HTTP POST http://v.looklook.cn/vs/api/active/activeList?requestapp=
	public static class activeListRequest {
		String userid; // "9876543212345678",
		String activetype; // 0所有活动 1有效活动
		String diaryid; // 当前视频id
		String mac; // 手机的mac地址
		String imei; // 手机imei（手机的唯一标识）
	}

	// 60. 参与活动日记列表
	// HTTP POST http://v.looklook.cn/vs/api/active/activeDiaryList?requestapp=
	public static class activeDiaryListRequest {
		String pagesize; // 10，每页记录数
		String userid; // "11111" // 当前操作人
		String diarywidth; // "12313”, // 封面需要显示的宽度，可以为空
		String diaryheight; // ”22322” // 封面需要显示的高度，可以为空
		String diary_time;
		String active_id; // ”123213”
		String request_type; // 1新内容加载，2历史内容加载，第一次请求为空
		String mac; // 手机的mac地址
		String imei; // 手机imei（手机的唯一标识）
	}

	// 62. 设置活动图片（供图片服务器访问）
	// HTTP POST http://v.looklook.cn/vs/api/active/setActiveImage
	// requestapp=
	public static class setActiveImageRequest {
		String activeid; // "11111" // 当前操作人
		String imagepath; // ”123213”
		String mac; // 手机的mac地址
		String imei; // 手机imei（手机的唯一标识）
	}

	// 63. 删除消息
	// HTTP POST http://v.looklook.cn/vs/api/user/deleteMessage?
	// requestapp=
	public static class deleteMessageRequest {
		String userid; // "11111" // 当前用户id
		String messageid; // 当前评论id
		String msg_type;
		String mac; // 手机的mac地址
		String imei; // 手机imei（手机的唯一标识）
	}

	// 64. 第三方互为关注的用户（以在LOOKLOOK注册的用户）
	// HTTP POST
	// http://v.looklook.cn/vs/api/user/listThirdPlatformUser?requestapp=
	public static class listThirdPlatformUserRequest {
		String userid; // "11111" // 当前用户id
		String pagesize; //
		String index; // ”ddd_ddd_dd”//第一页为空
		String timestamp; //客户端回传的时间戳，第一次为空
		String mac; // 手机的mac地址
		String imei; // 手机imei（手机的唯一标识）
	}

	// 65. 获取消息数
	// HTTP POST http://v.looklook.cn/vs/api/getMessageCount?requestapp=
	public static class getMessageCountRequest {
		String userid; // "11111" // 当前用户id
	}

	// 66. 获取日记赞人列表（3.0暂时不做，调转发人列表）
	// HTTP POST
	// http://v.looklook.cn/vs/api/diary/getDiaryEnjoyUsers?requestapp=
	public static class getDiaryEnjoyUsersRequest {
		String userid; // "11111" // 当前用户id
		String diaryid; // “2432”
		String pagesize; // 10每页记录数
		String index; // ”22322”
		String mac; // 手机的mac地址
		String imei; // 手机imei（手机的唯一标识）
	}

	// 67. 获取日记转发人列表
	// HTTP POST http://v.looklook.cn/vs/api/diary/getDiaryForwardUsers?
	// requestapp=
	public static class getDiaryForwardUsersRequest {
		String userid; // "11111" // 当前用户id
		String diaryid; // “2432”
		String pagesize; // 10每页记录数
		String user_time; // 每页记录数，第一次请求为空
		String request_type; // 1新内容加载，2历史内容加载，第一次请求为空
		String mac; // 手机的mac地址
		String imei; // 手机imei（手机的唯一标识）
	}

	// 68. 设置空间背景图片
	// HTTP POST http://v.looklook.cn/vs/api/user/setUserSpaceCover
	// requestapp=
	public static class setUserSpaceCoverRequest {
		String userid; // "11111" // 当前用户id
		String imagepath; // ”201212/03/543213609KJH76.jpg”
		String mac; // 手机的mac地址
		String imei; // 手机imei（手机的唯一标识）
	}

	// 69. 获取空间背景图片列表
	// HTTP POST http://v.looklook.cn/vs/api/user/getSpaceCoverList?requestapp=
	public static class getSpaceCoverListRequest {
		String userid; // "11111" // 当前用户id
		String pagesize; // 1条数
		String index; // “ddd_dd”索引
		String mac; // 手机的mac地址
		String imei; // 手机imei（手机的唯一标识）
	}

	// 71. 转发日记列表(3.0获取赞日记列表)
	// HTTP POST http://v.looklook.cn/vs/api/user/listEnjoyDiary?requestapp=
	public static class forwardDiarylistRequest {
		String pagesize; // 10，每页记录数
		String diary_time;// 每页记录数，第一次请求为空
		String userid; // "11111" //当前登录用户数
		String request_type; // 1新内容加载，2历史内容加载，第一次请求为空
		String viewuserid; // 当前登录用户数
		String diarywidth; // ”12313”, // 封面需要显示的宽度，可以为空
		String diaryheight; // ”22322” // 封面需要显示的高度，可以为空
		String mac; // 手机的mac地址
		String imei; // 手机imei（手机的唯一标识）
	}

	// 72. 转发日记id列表(3.0获取赞日记id列表)
	// HTTP POST http://v.looklook.cn/vs/api/user/listEnjoyDiary?requestapp=
	public static class forwardDiaryIDRequest {
		String pagesize; // 10，每页记录数
		String diary_time;// 每页记录数，第一次请求为空
		String userid; // "11111" //当前登录用户数
		String viewuserid; // 当前登录用户数
		String diarywidth; // ”12313”, // 封面需要显示的宽度，可以为空
		String diaryheight; // ”22322” // 封面需要显示的高度，可以为空
		String mac; // 手机的mac地址
		String imei; // 手机imei（手机的唯一标识）
	}

	// 73. 修改标签或位置
	// HTTP POST http://v.looklook.cn/vs/api/diary/modTagsOrPosition?requestapp=
	public static class modTagsOrPositionRequest {
		public String userid; // 用户ID
		public String diaryid;
		public String tags;// 标签集合
		public String logitude; // 经度
		public String latitude; // 维度
		public String mac; // 手机的mac地址
		public String imei; // 手机imei（手机的唯一标识）
		public String position; // 位置
	}

	// 74. 分享日记列表（显示自己的日记，分享到第三方的日记）-------增量更新
	// HTTP POST http://v.looklook.cn/vs/api/user/listEnjoyDiary?requestapp=
	public static class shareEnjoyDiaryRequest {
		String pagesize; // 10，每页记录数
		String userid; // "11111" //当前登录用户数
		String viewuserid; // 当前登录用户数
		String diarywidth; // ”12313”, // 封面需要显示的宽度，可以为空
		String diaryheight; // ”22322” // 封面需要显示的高度，可以为空
		String diary_time;
		String request_type;// 1新内容加载，2历史内容加载，第一次请求为空
		String mac; // 手机的mac地址
		String imei; // 手机imei（手机的唯一标识）
	}

	// 75. 个人主页
	// HTTP POST http://v.looklook.cn/vs/api/user/home?requestapp=
	public static class homeRequest {
		String pagesize; // 10，每页记录数
		String userid; // "11111" //当前登录用户数
		String viewuserid; // 当前登录用户数
		String diarywidth; // ”12313”, // 封面需要显示的宽度，可以为空
		String diaryheight; // ”22322” // 封面需要显示的高度，可以为空
		String userbackgroundwidth;
		String userbackgroundheight;
		String diary_time;
		String request_type;
		String mac; // 手机的mac地址
		String imei; // 手机imei（手机的唯一标识）
	}

	// 76. 删除转发和赞日记
	// HTTP POST
	// http://v.looklook.cn/vs/api/diary/deletepublishAndEnjoy?requestapp=
	public static class deletepublishAndEnjoyRequest {
		String userid; // "11111" // 当前用户id
		String publishid;
		String diaryid;
		String mac; // 手机的mac地址
		String imei; // 手机imei（手机的唯一标识）
	}

	// 77. 活动中奖日记列表
	// HTTP POST
	// http://v.looklook.cn/vs/api/active/getAwardDiaryList?requestapp=
	public static class getAwardDiaryListRequest {
		String userid;
		String active_id;
		String diarywidth;
		String diaryheight;
		String mac; // 手机的mac地址
		String imei; // 手机imei（手机的唯一标识）
	}

	// 81. 参加活动
	// HTTP POST http://v.looklook.cn/vs/api/diary/joinActive?requestapp=
	public static class joinActiveRequest {
		public String userid;
		public String diaryid;
		public String activeid;
		public String mac; // 手机的mac地址
		public String imei; // 手机imei（手机的唯一标识）
	}

	// 82. 通知私信浏览完成（只用于日记，视频等）
	// HTTP POST http://v.looklook.cn/vs/api/user/notifyPrivmsg?requestapp=
	public static class notifyPrivmsgRequest {
		String userid;
		String diaryid;
		String mac; // 手机的mac地址
		String imei; // 手机imei（手机的唯一标识）
	}

	// 83. 通知看过消息
	// HTTP POST http://v.looklook.cn/vs/api/user/notifyMessage?requestapp=
	public static class notifyMessageRequest {
		String userid;
		String messageids;// 消息id集合
		String mac; // 手机的mac地址
		String imei; // 手机imei（手机的唯一标识）
	}

	// 84. 发送通讯录
	// HTTP POST http://v.looklook.cn/vs/api/user/postAddressBook?requestapp=
	public static class postAddressBookRequest {
		String userid;
		String mac; // 手机的mac地址
		String imei; // 手机imei（手机的唯一标识）
		AddrBook[] address_book;
	}

	// 85. 查看日记权限
	// HTTP POST http://v.looklook.cn/vs/api/user/diaryPermissions?requestapp=
	public static class diaryPermissionsRequest {
		String userid;
		String diaryid;
		String mac; // 手机的mac地址
		String imei; // 手机imei（手机的唯一标识）
	}

	// 86. 设置日记心情
	// HTTP POST http://v.looklook.cn/vs/api/diary/setMood?requestapp=
	public static class setMoodRequest {
		String userid;
		String diaryid;// 日记id
		String mood_id;// 心情id
		String mac; // 手机的mac地址
		String imei; // 手机imei（手机的唯一标识）
	}

	// 87. 新消息列表
	// HTTP POST http://v.looklook.cn/vs/api/listMessage
	// requestapp=
	public static class listMessageRequest {
		String userid;
		String timemilli; // "1324689755413", 返回数据最后一条消息创建时间
		// String pageno; //页码
		String pagesize; // 记录数
		String messagetype;// 空是全查或0.1私信，2活动，3推荐，4附近，5陌生人
		String mac; // 手机的mac地址
		String imei; // 手机imei（手机的唯一标识）
		String commentid; //评论id，第一次传空，以后每次回传
		String diaryids; //日记id集合，每个日记id以，分隔
		String diarywidth;
	}

	// 88. 清除陌生人消息
	// HTTP POST
	// http://v.looklook.cn/vs/api/user/clearStrangerMessage?requestapp=
	public static class clearStrangerMessageRequest {
		String userid;
		String mac; // 手机的mac地址
		String imei; // 手机imei（手机的唯一标识）
	}

	// 89. Crm回调，通知激活
	// HTTP POST http://v.looklook.cn/vs/api/playPage?requestapp=
	public static class crmCallbackRequest {
		String userid; // 用户id
		String bindname;// 绑定名称，base64编码
	}

	// 90. www播放页（重新整理，改成JSON格式）
	// HTTP POST http://v.looklook.cn/vs/api/playPage?requestapp=
	public static class playPageRequest {
		String shortUrl;// 短连接
	}
	
	//91. 获取官方用户列表
	// HTTP POST http://v.looklook.cn/vs/api/getOfficialUserids?requestapp=
	public static class getOfficialUseridsRequest {
		String userid; // 用户id
		String mac; // 手机的mac地址
		String imei; // 手机imei（手机的唯一标识）
	}
	
	//92. 获取MD5加密KEY
	// HTTP POST http://v.looklook.cn/vs/api/getMD5KEY?requestapp=
	public static class getMD5keyRequest {
		String mac; // 手机的mac地址
		String imei; // 手机imei（手机的唯一标识）
	}
	
	//93.	收藏日记id列表
	//HTTP POST  http://v.looklook.cn/vs/api/getEffects?requestapp=
	public static class listCollectDiaryidRequest {
		String pagesize; //10，每页记录数
		String diary_time;//每页记录数，第一次请求为空
		String userid; // "11111" //当前登录用户数
		String viewuserid; //当前登录用户数
		String diarywidth; //”12313”,  // 封面需要显示的宽度，可以为空
		String diaryheight; //”22322”  // 封面需要显示的高度，可以为空
        String mac;    //手机的mac地址
		String imei;   //手机imei（手机的唯一标识）
	}
	
	// 95. 获取特效下载地址
	// HTTP POST http://v.looklook.cn/vs/api/listCollectDiaryid?requestapp=
	public static class getEffectsRequest {
		String userid; // 用户id
		String version; // 版本号
		String phone_type; // 1IOS,2ANDROIED(安卓)
		String mac; // 手机的mac地址
		String imei; // 手机imei（手机的唯一标识）
	}

	// 96.	解除保险箱
	// HTTP POST http://v.looklook.cn/vs/api/user/unSafebox?requestapp=
	public static class unSafeboxRequest {
		String userid; // 用户id
		String mac; // 手机的mac地址
		String imei; // 手机imei（手机的唯一标识）
	}
		
	// 97.	取消活动
	// HTTP POST http://v.looklook.cn/vs/api/active/cancleActive?requestapp=
	public static class cancleActiveRequest {
		public String userid; // 用户id
		public String diaryid; //日记id
		public String activeid; //活动id
		public String mac; // 手机的mac地址
		public String imei; // 手机imei（手机的唯一标识）
	}
	
	//100.	用户通讯录列表
	public static class phoneBookRequest{
		public String userid; // 用户id
		public String mac; // 手机的mac地址
		public String imei; // 手机imei（手机的唯一标识）
	}
	
	//101.	获取云端空间大小
	//HTTP POST http://v.looklook.cn/vs/api/getCloudSize？requestapp=
	public static class getCloudSizeRequest {
		public String userid; // 用户id
		public String mac; // 手机的mac地址
		public String imei; // 手机imei（手机的唯一标识）
	}
	
	// 102.	第三方互为关注的用户列表（并且已经注册looklook的用户）
	public static class listUserSNSRequest {
		public String userid; // 用户id
		public String mac; // 手机的mac地址
		public String imei; // 手机imei（手机的唯一标识）
	}
	
	//103.	后台推荐用户列表
	public static class listUserRecommendRequest {
		public String userid; // 用户id
		public String timestamp;  //客户端回传的时间戳，第一次为空
		public String mac; // 手机的mac地址
		public String imei; // 手机imei（手机的唯一标识）
	}
	
	public static class Attachs {
		public String attachid;
		public String attachuuid;
		public String content;
		public String attach_type; // "附件类型",  //1视频 2音频 3图片 4文字;
		public String audio_type;
		public String video_type;
		public String photo_type; // 图片类别，1有标 0无标 attach_type=3有效;
		public String level;
		public String attach_logitude;
		public String attach_latitude;
		public String suffix;
		public String Operate_type;
	}

	public static class AddrBook {
		public String phone_num;// 联系人电话号
		public String phone_work;// 联系人工作电话号
		public String phone_name;// 联系人姓名,base64编码
		@Override
		public boolean equals(Object object) {
			// TODO Auto-generated method stub
			if (!(object instanceof AddrBook)) {
				return false;
			}
			AddrBook addrBook = (AddrBook) object;
			if (this.phone_name.equals(addrBook.phone_name)) {
				return true;
			}
			return false;
		}
		
	}

	public static class SNS {
		public String snstype; // 微博类型
		public String snsid; // 微博用户id
		public String weiboid; // 微博id
	}

	public class VideoItems {
		String videoid; //
	}

	public static class postFollowItem {
		public postFollowItem(String snsuid, String snsphotourl, String snssex, String createtime, String nickname){
			this.snsuid = snsuid;
			this.snsphotourl = snsphotourl;
			this.snssex = snssex;
			this.createtime = createtime;
			this.nickname = nickname;
		}
		
		String snsuid;
		String snsphotourl; // ”http://……”,//头像，可以为空
		String snssex; // 性别，0男 1女 2未知
		String createtime; // ”2012-1-2”,//在微博创建时间，可以为空
		String nickname; // "goodboy"
	}

	public class postWeibocountItem {
		String snstype; // 1,//微博代号，1：新浪微博，6：腾讯微博
		String forwardcount; // 1212,//转发数
		String commentcount; // 111,//评论数
		String collectioncount; // 1112//收藏数

	}
	
	public static class uploadPicture{
		String upload_pic_type;//1用户头像，2视频封面，3用户上传壁纸4心情图片5	www 大图 小图6	后台客户壁纸7活动图片8 栏目头像9天气图片(1、2、3返回imageurl返回url，3-8返回imageurl返回路径)
		String userid;//用户id,supload_pic_type=1或2、3有效
		String attachid;//附件id，supload_pic_type=2有效
		String imei;//手机唯一标识，supload_pic_type=1或2、3有效
		String mac;//mac地址，supload_pic_type=1或2、3有效
	}

}
