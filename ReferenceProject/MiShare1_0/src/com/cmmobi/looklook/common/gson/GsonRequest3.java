package com.cmmobi.looklook.common.gson;

import java.util.ArrayList;

import com.cmmobi.looklook.common.gson.GsonResponse3.UserObj;

/**
 * @author wangrui
 */

public class GsonRequest3 {

	//================================RIA 请 求 结 构================================
	
	//===2.1===
	
	//===2.2===
	
	//===2.3===
	
	//===2.4===
	
	public static class configInfo{
		String userid; // "9876543212345678",
		String os; //客户端系统类型 1IOS 2Andriod
		String mac; // 手机的mac地址
		String imei; // 手机imei（手机的唯一标识）
	}
	
	//===2.5===
	//2.5.1 获取标签(缓存到本地，每次刷新到数据替换本地)
	public static class taglistRequest {
		String userid; // "9876543212345678",
		String diaryid;
		String mac; // 手机的mac地址
		String imei; // 手机imei（手机的唯一标识）
	}
	//2.5.2 获取空间背景图片列表
	public static class getSpaceCoverListRequest {
		String userid; // "11111" // 当前用户id
		String pagesize; // 1条数
		String index; // “ddd_dd”索引
		String mac; // 手机的mac地址
		String imei; // 手机imei（手机的唯一标识）
	}
	//2.5.3 获取Socket信息(IP、PORT)
	public static class getSocketRequest {
		public String userid;
		public String mac; // 手机的mac地址
		public String imei; // 手机imei（手机的唯一标识）
	}
	//2.5.4 客户意见反馈
	public static class feedbackRequest {
		public String userid;
		public String equipmentid; // ”1234”//设备id
		public String commentcontent; // ”这个应用真好玩，帮了我大忙了！！哈哈”
		public String mac; // 手机的mac地址
		public String imei; // 手机imei（手机的唯一标识）
		public String os; //客户端操作系统
		public String phone_model; //客户端手机型号
	}
		
	//2.5.5 客户端抓取用户第三方互粉传送给服务器
	public static class postSNSFriendRequest {
		String userid;
		String snstype;
		String upload_type;// 上传类型，1：第一次上传，2：1+n次上传
		String mac; // 手机的mac地址
		String imei; // 手机imei（手机的唯一标识）
		postFollowItem[] snsfriends;
	}
	//2.5.6 举报日记
	public static class reportRequest {
		String userid;
		String diaryid;
		String content;
		String mac; // 手机的mac地址
		String imei; // 手机imei（手机的唯一标识）
	}
	//2.5.7 获取特效下载地址
	public static class getEffectsRequest {
		String userid; // 用户id
		String version; // 版本号
		String phone_type; // 1IOS,2ANDROIED(安卓)
		String mac; // 手机的mac地址
		String imei; // 手机imei（手机的唯一标识）
	}
	//2.5.8 获取天气
	public static class getWeatherRequest {
		String userid; // 用户ID
		String addresscode;// 地址的国际码
		String mac; // 手机的mac地址
		String imei; // 手机imei（手机的唯一标识）
	}
	//2.5.9 默认提示内容(需要统计出都有哪些提示内容)
	public static class getdefaultPromptMsgRequest {
		String userid; // 用户ID
		String type; //提示类型
		String mac; // 手机的mac地址
		String imei; // 手机imei（手机的唯一标识）
	}
	//2.5.10 发送通讯录(通讯录与第三方互粉合并,需要确定传送的电话号码数量)
	public static class postAddressBookRequest {
		String userid;
		String userphone; //用户本人手机号
		String mac; // 手机的mac地址
		String imei; // 手机imei（手机的唯一标识）
		AddrBook[] address_book;
	}
	//2.5.10 切换账号数据合并接口
	public static class mergerAccountRequest {
		public String userid;   //新登录用户ID
		public String userid_beMerged; //被合并用户ID
		public String mac; // 手机的mac地址
		public String imei; // 手机imei（手机的唯一标识）
	}
	//---2.5.11---
	//2.5.11.1 编辑用户信息
	public static class changeUserInfoRequest {
		String userid;
		String nickname; // 昵称
		String equipmentid;
		String sex; // 0男 1女 2未知
		String birthdate; //
		String address; // 北京朝阳”
		String signature;// tag; //”心情不错”//个人签名,base64编码,可为空
		String mac;// 手机的mac地址
		String imei;// 手机imei（手机的唯一标识）

	}
	//2.5.11.2 修改登录密码
	public static class passwordChangeRequest {
		String userid; // "9876543212345678",
		String oldpassword; //
		String newpassword;
		String pwd_type; //1登录；2手势
		String mac;// 手机的mac地址
		String imei;// 手机imei（手机的唯一标识）
	}
	//2.5.11.3 修改个性签名
	public static class moodRequest {
		String userid; // "9876543212345678",
		String signature; //个人签名
		String mac; // 手机的mac地址
		String imei; // 手机imei（手机的唯一标识）
	}
	//2.5.11.4 Looklook系统设置
	public static class diaryPrivacyRequest {
		String userid; // "9876543212345678",
		String sync_type; // 数据同步，1仅WIFI 2任何网络
		String mac; // 手机的mac地址
		String imei; // 手机imei（手机的唯一标识）
	}
	
	public static class autoFriendRequest {
		String userid; // "9876543212345678",
		String accept_friend_status; // //手机通讯录请求好友标记，1：自动，2：手动，默认为1即自动
		String mac; // 手机的mac地址
		String imei; // 手机imei（手机的唯一标识）
	}
	
	//2.5.11.5 设置手势密码
	public static class addGesturePasswordRequest {
		String userid; // "9876543212345678",
		String gesturepassword; // "123456" 手势密码
		String mac; // 手机的mac地址
		String imei; // 手机imei（手机的唯一标识）
	}
	//2.5.11.6 设置个性排序
	public static class mypersonalsortRequest {
		String userid; // "9876543212345678",
		String sort; //个性排序,11空间 12微享 13朋友 14订阅
		String mac; // 手机的mac地址
		String imei; // 手机imei（手机的唯一标识）
	}
	//2.5.11.7 设置用户头像(供图片服务器访问，3.1 socket处理)
	public static class setHeadImageRequest {
		String userid; // "9876543212345678",
		String imagepath; // ”201212/03/543213609KJH76.jpg” 头像路径
		String mac; // 手机的mac地址
		String imei; // 手机imei（手机的唯一标识）
	}
	//===2.6===
	//2.6.1 启动调用接口(UA、升级)
	public static class uaRequest {
		String imei; // "11111112222" 手机imei（手机的唯一标识）
		String imsi; // "460028105401115" 手机imsi（SIM卡的唯一标识）
		String sdk_imei; // looklook的sdk里的唯一编码,CmmobiClickAgent.getImei(context);
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
		String is_mishare_no; //是否提供微享号 1 是 0 否
		String mobiletype; // // 1 ios 2 Andriod
		String devicetoken; // // 设备token值  imei
		
		String clientversion; // 客户端版本号
		String version; //当前移动终端软件版本号
		String system; //移动终端软件属于哪个系统，系统信息在客户端升级下载平台进行维护，该参数为系统信息ID
		String productcode; //移动终端软件属于哪个产品，产品信息在客户端升级下载平台进行维护
		String channelcode; //移动终端软件属于哪个渠道，渠道信息在客户端升级下载平台进行维护，该参数为渠道信息唯一标识码
	}
	//2.6.2 用户登录返回数据集合
	//2.6.3 用户登录(密码加密，与CRM确认)
	public static class loginRequest {
		String username;// "zhangsan",//snstype>0时无这个值
		String password; // "asdf1234",//snstype>0时无这个值,密码加密
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
		String signature;// tag; //个人签名,base64编码,可为空,snstype>0时
		String logintype; // 1邮箱登录，2手机号登录snstype=0有效
		String access_token;// 第三方token值snstype>0时有效
		String sns_effective_time;// 过期时间snstype>0时有效（时间段）
		String sns_expiration_time; //第三方有效时间(到时间点)
		String refresh_token;// 刷新token,只有腾讯，人人有snstype>0时有效
		String snsname;// name第三方用户名，只有腾讯（腾讯字段name）snstype>0时有效
		String openkey;// 只有腾讯snstype>0时有效
		String mac; // 手机的mac地址
		String imei; // 手机imei（手机的唯一标识）
	}
	//2.6.4 自动登录(同时设置deviceToken)
	public static class autoLoginRequest {
		String userid;
		String devicetoken;
		String mac;
		String imei;
		String mobiletype;
	}
	//2.6.5 Looklook用户注册
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
	//2.6.6 绑定(邮箱、手机、第三放平台)
	public static class bindingRequest {
		String userid;
		String binding_type; // //1邮箱，2手机，3备用手机，4第三放平台
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
		String refresh_token;// 刷新token,只有腾讯，人人有snstype>0时有效
		String snsname;// name第三方用户名，只有腾讯（腾讯字段name）snstype>0时有效
		String openkey;// 只有腾讯snstype>0时有效
		String nickname; //第三方昵称snstype>0时有效
		String sns_expiration_time;  //第三方过期时间(到时间点)
		String sns_effective_time;  //第三方有效时间（时间段）
	}
	// 2.5.5切换手机绑定
	public static class changeBindRequest {
		String userid;
		String change_bind_type;//1.原手机号验证 2.新手机号切换
		String phone; // 手机号
		String check_no; // 验证码 
		String mac; // 手机的mac地址
		String imei; // 手机imei（手机的唯一标识）
		String equipmentid; // 设备id
		String gps; //是一个位置的坐标信息(gps信息包括经纬度)
		String sex;// 性别 0男 1女 2未知,snstype>0时
		String address;// snstype>0时，国标编码
		String birthdate;// 出生日期,snstype>0时
	}
	// 2.5.11 验证手机验证码
	public static class verifySMSRequest {
		String phone;// 手机号
		String check_no;// 验证码 更改后手机验证码
		String check_type;// 验证类型， 2 绑定手机 6 更改绑定新手机
		String mac;// 手机的mac地址
		String imei; // 手机imei（手机的唯一标识）
	}
	//2.6.7 解除绑定
	public static class unbindRequest {
		String userid; // "9876543212345678",
		String binding_type;
		String binding_info;
		String snstype;
		String snsuid;
		String equipmentid;
		String mac; // 手机的mac地址
		String imei; // 手机imei（手机的唯一标识）
	}
	//2.6.8 验证注册用户名是否可用(敏感词CRM or RIA调用接口处理，37、38考虑合并)
	public static class checkUserNameExistRequest {
		String equipmentid;
		String username; // base64编码
		String mac; // 手机的mac地址
		String imei; // 手机imei（手机的唯一标识）
	}
	//2.6.9 验证注册昵称是否可用--(第三方用户昵称规则？)
	public static class checkNickNameExistRequest {
		String nickname; // base64编码
		String mac; // 手机的mac地址
		String imei; // 手机imei（手机的唯一标识）
	}
	//2.6.10 判断用户是否有效(是否被拉黑)
	public static class checkUserRequest {
		String userid; // "9876543212345678",
		String mac; // "123456" 可为空
		String imei; // "123456" 可为空
	}
	//2.6.11 找回密码
	public static class forgetPasswordRequest {
		String username; // ”a@example.com”
		String registertype;// 1:手机号 2或空：邮箱
		String pwd_type;
		String equipmentid;
		String userid;
		String mac;
		String imei;
	}
	//2.6.12 获取手机验证码
	public static class CheckNoRequest {
		String username; // 手机号
		String check_type; // 2 绑定手机 5 更改绑定原手机 6 更改绑定新手机
		String mac;
		String imei;
	}
	// 2.6.13 解除保险箱
	// HTTP POST http://v.looklook.cn/vs/api/user/unSafebox?requestapp=
	public static class unSafeboxRequest {
		String userid; // 用户id
		String mac; // 手机的mac地址
		String imei; // 手机imei（手机的唯一标识）
	}
	//===2.7===
	//2.7.1.1 我的日记
	public static class listMyDiaryRequest {
		String pagesize; // 10，每页记录数
		String userid; // "11111" // 当前操作人
		String viewuserid; // ”22222” // 需要显示的视频用户id
		String diary_time; // 每页记录数，第一次请求为空
		String request_type;// 1新内容加载，2历史内容加载，第一次请求为空
		String diarywidth; // ”12313”, // 封面需要显示的宽度，可以为空
		String diaryheight; // ”22322” // 封面需要显示的高度，可以为空
		String mac; // 手机的mac地址
		String imei; // 手机imei(手机的唯一标识)
	}
	//2.7.1.2 个人空间(我的日记16区别在于一些附属信息)
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
	//2.7.1.3 获取收藏日记列表
	public static class listCollectDiaryRequest {
		String pagesize; //10，每页记录数
		String userid; // "11111" //当前登录用户数
		String viewuserid; //当前登录用户数
		String diary_time;//每页记录数，第一次请求为空
		String request_type;//1新内容加载，2历史内容加载，第一次请求为空
		String diarywidth; //”12313”,  // 封面需要显示的宽度，可以为空
		String diaryheight; //”22322”  // 封面需要显示的高度，可以为空
        String mac;    //手机的mac地址
		String imei;   //手机imei（手机的唯一标识）
	}
	//2.7.1.4 我的评论列表(按照时间戳处理)
	public static class MyCommentListRequest {
		String userid;
		String comment_type;// 1新内容加载，2历史内容加载，第一次请求为空
		String comment_time;// 1我收到的评论 2我发出的评论
		String request_type;//每页记录数，第一次请求为空
		String pagesize; //10，每页记录数
		String mac; // 手机的mac地址
		String imei; // 手机imei（手机的唯一标识）
		String sorttype;//  1表示最新的在前面 2表示最新的在后面
		String is_encrypt; //1：是 0： 否 通过标识显示保险箱和非保险箱列表
	}
	//2.7.1.5 日记评论列表(每条评论不带日记信息)
	public static class diaryCommentListRequest {
		String userid;
		String comment_time;// 每页记录数，第一次请求为空
		String request_type;// 1新内容加载，2历史内容加载，第一次请求为空
		String publishid;//分享日记ID
		String pagesize;// 页数
		String mac; // 手机的mac地址
		String imei; // 手机imei（手机的唯一标识）
		String sorttype;//排序方式
	}
//	//2.7.1.6 赞日记id列表
//	public static class EnjoyDiaryIDRequest {
//		String pagesize; // 10，每页记录数
//		String userid; // "11111" //当前登录用户数
//		String viewuserid; // 当前登录用户数
//		String diarywidth; // ”12313”, // 封面需要显示的宽度，可以为空
//		String diaryheight; // ”22322” // 封面需要显示的高度，可以为空
//		String diary_time;// 每页记录数，第一次请求为空
//		String mac; // 手机的mac地址
//		String imei; // 手机imei（手机的唯一标识）
//	}
	//2.7.1.7 订阅日记列表
	public static class attentionListRequest {
		String pagesize; // 10，每页记录数
		String userid; // "11111" //当前登录用户数
		String viewuserid; // 当前登录用户数
		String diary_time;// 每页记录数，第一次请求为空
		String request_type;// 1新内容加载，2历史内容加载，第一次请求为空
		String diarywidth; // ”12313”, // 封面需要显示的宽度，可以为空
		String diaryheight; // ”22322” // 封面需要显示的高度，可以为空
		String mac; // 手机的mac地址
		String imei; // 手机imei（手机的唯一标识）
	}
	//2.7.1.8 朋友日记列表
	public static class friendListRequest {
		String pagesize; // 10，每页记录数
		String userid; // "11111" //当前登录用户数
		String viewuserid; // 当前登录用户数
		String diary_time;// 每页记录数，第一次请求为空
		String request_type;// 1新内容加载，2历史内容加载，第一次请求为空
		String diarywidth; // ”12313”, // 封面需要显示的宽度，可以为空
		String diaryheight; // ”22322” // 封面需要显示的高度，可以为空
		String mac; // 手机的mac地址
		String imei; // 手机imei（手机的唯一标识）
	}
	//2.7.1.9 收藏日记id列表
	public static class listCollectDiaryidRequest {
		String pagesize; //10，每页记录数
		String userid; // "11111" //当前登录用户数
		String viewuserid; //当前登录用户数
		String diarywidth; //”12313”,  // 封面需要显示的宽度，可以为空
		String diaryheight; //”22322”  // 封面需要显示的高度，可以为空
		String diary_time;//每页记录数，第一次请求为空
		String mac;    //手机的mac地址
		String imei;   //手机imei（手机的唯一标识）
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
	
	//2.7.1.10 TA人分享内容列表
	public static class otherListMyDiaryRequest {
		String pagesize; //10，每页记录数
		String userid; // "11111" //当前登录用户数
		String viewuserid; //当前登录用户数
		String diarywidth; //”12313”,  // 封面需要显示的宽度，可以为空
		String diaryheight; //”22322”  // 封面需要显示的高度，可以为空
		String diary_time;//每页记录数，第一次请求为空
		String request_type;//1新内容加载，2历史内容加载，第一次请求为空
		String mac;    //手机的mac地址
		String imei;   //手机imei（手机的唯一标识）
	}
	
	//2.7.1.11 TA人空间
	public static class otherHomeRequest {
		String pagesize; //10，每页记录数
		String userid; // "11111" //当前登录用户数
		String viewuserid; //当前登录用户数
		String diarywidth; //”12313”,  // 封面需要显示的宽度，可以为空
		String diaryheight; //”22322”  // 封面需要显示的高度，可以为空
		String userbackgroundwidth;
		String userbackgroundheight;
		String diary_time;//每页记录数，第一次请求为空
		String request_type;//1新内容加载，2历史内容加载，第一次请求为空
        String mac;    //手机的mac地址
		String imei;   //手机imei（手机的唯一标识）
	}
	//---2.7.2---
	//2.7.2.1 日记结构管理(盛宏强)
	public static class createStructureRequest {
		public String userid; // 用户ID
		public String diaryid;
		public String diaryuuid;
		public String operate_diarytype;
		public String resourcediaryid;
		public String resourcediaryuuid;
		public String createtime;
		public Attachs attachs[];
		public String tags;
		public String mac; // 手机的mac地址
		public String imei; // 手机imei（手机的唯一标识）
		public String offset; //偏移量
		public String position_source;
		public String addresscode;
		public String position_status;
		public String shoottime; // 视频开始拍摄的时刻;
		
		public String isonlymic; // 是否是微享 (取值: GsonProtocol.IS_ONLY_MICROSHARE_XXXX);
		
		public String longitude_real; // "经度",//真实内容属性位置	，operate_diarytype=1或3有效--单词修改
		public String latitude_real; // "维度",//真实内容属性位置，operate_diarytype=1或3有效
		public String position_real; // "北京朝阳区",  //真实内容属性位置
		
		public String longitude; // "经度",  //导入内容位置
		public String latitude; // "维度",  //导入内容位置
		public String position; // "北京朝阳区",  //导入内容位置
		
		public String longitude_view; // "经度",  //客户端后续展示、编辑使用的位置信息，创建时和真实内容属性位置一致
		public String latitude_view; // "维度",  //客户端后续展示、编辑使用的位置信息，创建时和真实内容属性位置一致
		public String position_view; // "北京朝阳区",  //客户端后续展示、编辑使用的位置信息，创建时和真实内容属性位置一致
	}
	
	//2.7.2.2 收藏日记(22、23整合)
	public static class addCollectDiaryRequest {
		public String userid; // 用户ID
		public String diaryid; // 日记ID
		public String publishid; //分享日记ID
		public String mac; // 手机的mac地址
		public String imei; // 手机imei（手机的唯一标识）
	}
	//2.7.2.3 取消收藏
	public static class removeCollectDiaryRequest {
		public String userid; // 用户ID
		public String diaryid; // 日记ID
		public String publishid; // 多个分享日记ID，用逗号进行分隔
		public String mac; // 手机的mac地址
		public String imei; // 手机imei（手机的唯一标识）
	}
	//2.7.2.4 删除原日记
	public static class deleteDiaryRequest {
		public String userid;
		public String diaryids;// 日记ID集合，用","号分割
		public String changetomaindiaryuuids;//新主日记ID集合，用","号分割
		public String mac; // 手机的mac地址
		public String imei; // 手机imei（手机的唯一标识）
	}
	//2.7.2.5 日记放入、移出保险箱
	public static class safeboxRequest {
		public String userid;
		public String diaryids;
		public String diaryuuid;
		public String type; //类型，1加入保险箱 2移除保险箱
		//public String isprivate; //1是私密 把全部关系切断 0不是私密只把此内容加入保险箱 如果type==1则必填
		public String mac; // 手机的mac地址
		public String imei; // 手机imei（手机的唯一标识）
	}
	//2.7.2.6 获取日记分享URL
	public static class getDiaryUrlRequest {
		public String userid; //用户id
		public String diaryids;  //日记ID 如果是多个日记则会创建日记组
		public String diaryuuid;  //日记组时上传UUID
		public String publishid;//转发时候需要记录父节点ID
		public String type;//0原创 1转发
		public String content;       //分享内容
		public String share_type; //1新浪 2人人 5 QZONE空间 6腾讯 9微信朋友圈 10短信 11邮箱 12微信好友 100站内公开 101朋友圈 102私密分享 103微享
		public String os; //客户端操作系统，创建微享时导入多个日记生成日记组时需要
		public String phone_model; //创建微享时导入多个日记生成日记组时需要
		public String weather; //创建微享时导入多个日记生成日记组时需要
		public String weather_info; //创建微享时导入多个日记生成日记组时需要
		public String longitude; //自定义经度
		public String latitude; //自定义纬度
		public String postion; //位置
		public String mac; // 手机的mac地址
		public String imei; // 手机imei（手机的唯一标识）
		
	}
	//2.7.2.7 日记详情(宏强)
	public static class diaryInfoRequest {
		public String userid;
		public String diaryid;// 分享日记ID
		public String diarywidth; // 封面需要显示的宽度，可以为空
		public String diaryheight; // 封面需要显示的高度，可以为空
		public String mac; // 手机的mac地址
		public String imei; // 手机imei（手机的唯一标识）
	}
	//2.7.2.8 修改标签或位置(用户虚假位置信息)
	public static class modTagsOrPositionRequest {
		public String userid; // 用户ID
		public String diaryids;
		public String tags;// 标签集合
		public String logitude; // 经度
		public String latitude; // 维度
		public String mac; // 手机的mac地址
		public String imei; // 手机imei（手机的唯一标识）
		public String position; // 位置
		public String position_source; // 位置来源，1 GPS 2基站 ,可以为空
	}
	//2.7.2.9 详情页(分享)
	public static class diaryShareInfoRequest {
		public String userid;
		public String viewuserid;//显示用户ID
		public String diaryid;
		public String publishid;
		public String pagesize;
		public String mac; // 手机的mac地址
		public String imei; // 手机imei（手机的唯一标识）
	}
	//2.7.2.10 日记权限
	public static class diarySharePermissionsRequest{
		public String userid; //用户id
		public String diaryid; //日记id
		public String publish_status; //1仅自己 2 朋友可见 3站内容公开
		public String mac; // 手机的mac地址
		public String imei; // 手机imei（手机的唯一标识）
	}
	//---2.7.3---
	//2.7.3.1 分享记录
	public static class myPublishHistoryRequest {
		public String userid; // 用户ID
		public String diary_time;
		public String request_type; //1新内容加载，2历史内容加载，第一次请求为空
		public String pagesize;// 每页记录数
		public String mac; // 手机的mac地址
		public String imei; // 手机imei（手机的唯一标识）
	}
	//2.7.3.2 清除分享记录(4.0暂时不用)
	public static class cleanPublishDiaryRequest {
		public String userid; // 用户ID
		public String publishid; //分享日记ID
		public String type; //1单个  2全部
		public String mac; // 手机的mac地址
		public String imei; // 手机imei（手机的唯一标识）
	}
	//2.7.3.3 站内分享
	public static class diaryPublishRequest {
		public String userid; // 用户ID
		public String diaryids;
		public String diaryuuid;
		public String content;
		public String diary_type;
		public String position_type;
		public String iscreategroup; //0是不创建 1是创建 客户端传递0即可  后台视情况核定
		public String isofficial; // 1代表 官方推荐的  0代表普通的
		public String longitude; //自定义经度
		public String latitude; //自定义纬度
		public String postion; //位置
		public String mac; // 手机的mac地址
		public String imei; // 手机imei（手机的唯一标识）
	}
	//2.7.3.4 取消站内分享
	public static class cancelPublishRequest {
		public String userid; // 用户ID
		public String publishid; //分享id
		public String mac; // 手机的mac地址
		public String imei; // 手机imei（手机的唯一标识）
	}
	//2.7.3.5 删除第三方分享轨迹
	public static class cancelShareRequest {
		public String userid; // 用户ID
		public String diaryid; //日记id
		public String snstype; //微博类型
		public String mac; // 手机的mac地址
		public String imei; // 手机imei（手机的唯一标识）
	}
	//2.7.3.6 第三方社区分享
	public static class shareDiaryRequest {
		public String userid; // 用户ID
		public String diaryid;
		public String publishid; //分享ID
		public String snscontent;
		public String mac; // 手机的mac地址
		public String imei; // 手机imei（手机的唯一标识）
		public SNS[] sns;
		public String snsdes;
	}
	//===2.8===
	//---2.8.1---
	//2.8.1.1 关注列表
	public static class myattentionlistRequest {
		String userid; // "9876543212345678",
		String pagesize; // 10，每页记录数
		String user_time;
		String viewuserid;// 显示用户id
		String t_lastchange; //所有好友的最后一次更新时间 
		String mac; // 手机的mac地址
		String imei; // 手机imei（手机的唯一标识）
	}
	//2.8.1.2 粉丝列表
	public static class myfanslistRequest {
		String userid; // "9876543212345678",
		String pagesize; // 10，每页记录数
		String user_time;// 上次请求，最后一条记录的时间
		String viewuserid; // 显示用户id
		String mac; // 手机的mac地址
		String imei; // 手机imei（手机的唯一标识）
	}
	//2.8.1.3 朋友列表
	public static class myfriendslistRequest {
		String userid; // "9876543212345678",
		String pagesize; // 10，每页记录数
		String user_time;// 上次请求，最后一条记录的时间
		String viewuserid; // 显示用户id
		String mac; // 手机的mac地址
		String imei; // 手机imei（手机的唯一标识）
	}
	//2.8.1.4 获取官方账号列表
	public static class getOfficialUseridsRequest {
		String userid; // 用户id
		String mac; // 手机的mac地址
		String imei; // 手机imei（手机的唯一标识）
	}
	//2.8.1.5 黑名单列表
	public static class myblacklistRequest {
		String userid; // "9876543212345678",
		String pagesize; // 10，每页记录数
		String user_time;
		String viewuserid; // 显示用户id
		String mac; // 手机的mac地址
		String imei; // 手机imei（手机的唯一标识）
	}
	//2.8.1.6 获取日记赞人列表
	public static class getDiaryEnjoyUsersRequest {
		String userid; // "11111" // 当前用户id
		String diaryid; // “2432”
		String publishid; // 分享日记id
		String type; // 1 查看原日记的赞人列表 2查看某次分享的赞人列表
		String pagesize; // 10每页记录数
		String diary_time; //每页记录数，第一次请求为空
		String request_type; //1新内容加载，2历史内容加载，第一次请求为空
		String viewuserid;
		String mac; // 手机的mac地址
		String imei; // 手机imei（手机的唯一标识）
	}
	//2.8.1.7 搜索用户列表(除去系统黑名单)
	public static class searchUserRequest {
		String userid;
		String keyword; // 搜索的关键字
		String mac; // 手机的mac地址
		String imei; // 手机imei（手机的唯一标识）
		String type;	//搜索类型 1手机号 2微享号
	}
	//2.8.1.8 已加入looklook的第三方互为关注用户(包括手机通讯录、新浪、腾讯、人人)
	public static class listUserSNSRequest {
		public String userid; // 用户id
		public String mac; // 手机的mac地址
		public String imei; // 手机imei（手机的唯一标识）
		public String user_time; //上次请求最后一条记录时间戳
	}
	//2.8.1.9 系统推荐用户列表
	public static class listUserRecommendRequest {
		public String userid; // 用户id
		public String timestamp;  //客户端回传的时间戳，第一次为空
		public String mac; // 手机的mac地址
		public String imei; // 手机imei（手机的唯一标识）
	}
	//2.8.1.10 好友请求列表接口
	public static class friendRequestListRequest {
		public String userid; // 用户id
		public String user_time; //上次请求，最后一条记录的时间 
		public String mac; // 手机的mac地址
		public String imei; // 手机imei（手机的唯一标识）
	}
	//---2.8.2---
	//2.8.2.1 个人信息接口
	//2.8.2.2 日记评论/回复
	public static class commentRequest {
		public String userid;
		public String diaryid; //日记id
		public String publishid;// 分享日记ID
		public String commentcontent;// 评论内容 base64编码
		public String isreply;    // 1：评论，2：回复评论，3修改评论文字
		public String commentid;
		public String commentuuid;////评论UUID，当前评论uuid
		public String commenttype;
		public String comment_source;  //1普通分享评论  2微享评论
		public String mac; // 手机的mac地址
		public String imei; // 手机imei（手机的唯一标识）
	}
	//2.8.2.3 发消息 (是否发日记，不发日记looklook赞世界处理方式)
	public static class sendmessageRequest {
		public String userid; // "9876543212345678",
		public String target_userids;// 目标用户id
		public String content; // 内容
		public String privatemsgtype;
		public String uuid;
		public String mac; // 手机的mac地址
		public String imei; // 手机imei（手机的唯一标识）
	}
	//2.8.2.4 赞 (喜欢)
	public static class enjoyRequest {
		String userid; // "9876543212345678",
		String diaryid;
		String publishid; // 分享日记ID
		String mac; // 手机的mac地址
		String imei; // 手机imei（手机的唯一标识）
	}
	//2.8.2.5 转发
	public static class repostRequest {
		String userid; // "9876543212345678",
		String publishid; // 分享日记ID
		String content; // 转发内容，base64编码
		String mac; // 手机的mac地址
		String imei; // 手机imei（手机的唯一标识）
	}
	//2.8.2.6 取消赞
	public static class deleteEnjoyRequest {
		String userid; // "9876543212345678",
		String diaryid; //日记id
		String publishid; // 分享日记ID
		String mac; // 手机的mac地址
		String imei; // 手机imei（手机的唯一标识）
	}
	//2.8.2.7 删除评论(删除自己评论和删除对自己日记进行的评论)
	public static class deleteCommentRequest {
		public String userid; // "9876543212345678",
		public String publishid; // 分享日记ID
		public String commentid;
		public String commentuuid;
		public String mac; // 手机的mac地址
		public String imei; // 手机imei（手机的唯一标识）
	}
	//2.8.2.8 加关注
	public static class attentionRequest {
		String userid; // "9876543212345678",
		String attention_userid; //被关注用户ID
		String mac; // 手机的mac地址
		String imei; // 手机imei（手机的唯一标识）
	}
	//2.8.2.9 取消关注\删除粉丝
	public static class cancelattentionRequest {
		String userid; // "9876543212345678",
		String target_userid; //被关注用户ID
		String attention_type; //1:取消关注 2：删除粉丝
		String mac; // 手机的mac地址
		String imei; // 手机imei（手机的唯一标识）
	}
	//2.8.2.10 备注名修改
	public static class setUserAliasRequest {
		String userid; // "9876543212345678",
		String attention_userid; //关注用户ID
		String attention_mark; //关注人备注，一般是姓名，base64编码
		String mac; // 手机的mac地址
		String imei; // 手机imei（手机的唯一标识）
	}
	//2.8.2.11 设置黑名单(用户黑名单)
	public static class operateblacklistRequest {
		public String userid; // "9876543212345678",
		public String target_userid;
		public String operatetype; // 操作类型 1加入黑名单 2移出黑名单
		public String mac; // 手机的mac地址
		public String imei; // 手机imei（手机的唯一标识）
	}
	
	//2.8.2.12 申请加好友
	public static class addfriendRequest {
		public String userid; // "9876543212345678",
		public String target_userid;
		public String request_msg; //验证信息 如 "不加我你会后悔的"；
		public String mac; // 手机的mac地址
		public String imei; // 手机imei（手机的唯一标识）
	}
	
	//2.8.2.13 同意好友请求
	public static class agreeFriendRequest {
		public String userid; // "9876543212345678",
		public String target_userid;
		public String mac; // 手机的mac地址
		public String imei; // 手机imei（手机的唯一标识）
	}
	
	//2.8.2.14 删除好友
	public static class deleteFriendRequest {
		public String userid; // "9876543212345678",
		public String target_userid;
		public String mac; // 手机的mac地址
		public String imei; // 手机imei（手机的唯一标识）
	}
	//===2.9===
	//2.9.1 创建微享
	public static class createMicRequest {
		public String userid; // 用户id
		public String diary_id; // 日记ID，如果是多个日记新建组微享多个日记id以“4433121,4433121”形式
		public String uuid;
		public String mic_title;
		public String content; // 微享内容
		public String os; //客户端操作系统，创建微享时导入多个日记生成日记组时需要
		public String phone_model; //手机型号,创建微享时导入多个日记生成日记组时需要
		public UserObj[] userobj; // 微享用户组IDS AAA|BBB|CCC|DDD
		public String longitude;
		public String latitude;
		public String position;
		public String position_status;//位置开关
		public String mac;
		public String imei;
		public String capsule;//时光胶囊标识0 不是 1是
		public String burn_after_reading;//阅后即焚标识0 不是 1是
		public String capsule_time;//"1272212321";时光胶囊开启时间

	}
	
	//2.9.2 微享列表
	public static class myMicListRequest {
		public String userid; // 用户id
		public String mic_time; //微享时间，第一次请求为空
		public String request_type; //1新内容加载，2历史内容加载，第一次请求为空
		public String pagesize; //10，每页记录数
		public String mac; // 手机的mac地址
		public String imei; // 手机imei(手机的唯一标识)	
		public String is_encrypt; //1：是 0： 否 通过标识显示保险箱和非保险箱列表
	}

	//2.8.6 他人微享列表
	public static class MicListRequest {
		public String userid; // 用户id
		public String viewuserid; //他人用户ID
		public String mic_time; //微享时间，第一次请求为空
		public String request_type; //1新内容加载，2历史内容加载，第一次请求为空
		public String pagesize; //10，每页记录数
		public String mac; // 手机的mac地址
		public String imei; // 手机imei(手机的唯一标识)
	}

	//2.9.3 微享内容详情页
	public static class myMicInfoRequest {
		public String userid; // 用户id
		public String publishid; //微享ID
		public String mac; // 手机的mac地址
		public String imei; // 手机imei(手机的唯一标识)	
		public String micuserid; //微享创建人id
		public String pagesize; //微享评论请求数量
	}
	
	//2.6.1.7 微享评论列表
	public static class MicCommentsRequest {
		public String userid; // 用户id
		public String comment_time; //每页记录数，第一次请求为空
		public String request_type;  //1新内容加载，2历史内容加载，第一次请求为空
		public String publishid; //微享ID
		public String mac; // 手机的mac地址
		public String imei; // 手机imei(手机的唯一标识)	
		public String sorttype; // 1表示最新的在前面 2表示最新的在后面
		public String pagesize; //微享评论请求数量
	}

	//2.9.4 微享预览页
	public static class mySubMicListRequest {
		public String userid; // 用户id
		public UserObj[] userobj;
		public String mac; // 手机的mac地址
		public String imei; // 手机imei(手机的唯一标识)	
		public String is_encrypt;
		public String mic_time;  //微享时间，第一次请求为空
		public String request_type;   //1新内容加载，2历史内容加载，第一次请求为空
		public String pagesize;
	}
		
	//2.9.5 微享清屏
	public static class cleanMicRequest {
		public String userid; // 用户id
		public String publishid; //微享ID
		public String mac; // 手机的mac地址
		public String imei; // 手机imei(手机的唯一标识)		
	}
	//2.9.6 微享放入保险箱
	public static class safeboxmicRequest {
		public String userid; // 用户id
		public String publishid; //微享ID
		public String mic_safebox; //保险箱加入移除标识，1加入 0移除
		public String mac; // 手机的mac地址
		public String imei; // 手机imei(手机的唯一标识)	
	}
	//2.9.7 私信|消息阅读回调接口
	public static class readmsgRequest {
		public String userid; // 用户id
		public String viewuserid; //被阅读用户
		public String mac; // 手机的mac地址
		public String imei; // 手机imei(手机的唯一标识)	
	}
	//免打扰
	public static class setundisturbRequest {
		public String userid; //用户ID
		public String publishid; //微享ID
		public String is_undisturb; //免打挠 1：是 0： 否
		public String mac;  //手机的mac地址
		public String imei; // 手机imei(手机的唯一标识)	
	}
	
	//===2.10===
    //2.10.1 获取活动列表(缓存)
	public static class activeListRequest {
		String userid; // "9876543212345678",
		String activetype; // 0所有活动 1有效活动
		String diaryid; // 当前视频id
		String mac; // 手机的mac地址
		String imei; // 手机imei（手机的唯一标识）
	}
	//2.10.2 参与活动日记列表
	public static class activeDiaryListRequest {
		String pagesize; // 10，每页记录数
		String userid; // "11111" // 当前操作人
		String diarywidth; // "12313”, // 封面需要显示的宽度，可以为空
		String diaryheight; // ”22322” // 封面需要显示的高度，可以为空
		String diary_time;
		String request_type; // 1新内容加载，2历史内容加载，第一次请求为空
		String activeid; // ”123213”
		String mac; // 手机的mac地址
		String imei; // 手机imei（手机的唯一标识）
	}
	//2.10.3 参加活动
	public static class joinActiveRequest {
		public String userid;
		public String diaryid;
		public String activeid;
		public String mac; // 手机的mac地址
		public String imei; // 手机imei（手机的唯一标识）
	}
	//2.10.4 活动中奖日记列表
	public static class getAwardDiaryListRequest {
		String userid;
		String activeid;
		String diarywidth;
		String diaryheight;
		String mac; // 手机的mac地址
		String imei; // 手机imei（手机的唯一标识）
	}
	//2.10.5 取消参加活动
	public static class cancelActiveRequest {
		String userid;
		String diaryid;
		String activeid;
		String mac; // 手机的mac地址
		String imei; // 手机imei（手机的唯一标识）
	}
	//===2.11===
	//2.11.1 心跳,新消息列表(需要确定都有哪些信息)
	public static class listMessageRequest {
		String userid;
		String os;        //系统类型 1 ios 2 android
		String timemilli; // "1324689755413", 返回数据最后一条消息创建时间
		String pagesize; // 记录数
		String messagetype;// 空是全查或0.1私信，2活动，3推荐，4附近，5陌生人
		String mac; // 手机的mac地址
		String imei; // 手机imei（手机的唯一标识）
		String commentid; //评论id，第一次传空，以后每次回传
		String commentid_safebox; //评论id，第一次传空，以后回传（保险箱内）         // add new 
		String t_zone_mic; //微享新动态时间戳
		String t_zone_miccomment; //微享新评论时间戳                                                               
		String t_safebox_miccomment; //微享新评论时间戳(保险箱内)        // add new
		
		String t_friend; //通讯录朋友列表时间戳
		String t_friend_change; //通讯录朋友列表动态时间戳
		String t_friendrequest; //好友请求列表时间戳
		String t_push;      //push时间戳
		
		//del
		//String t_attention; //通讯录订阅列表时间戳
		//String t_fans; //新增粉丝数时间戳
		//String t_recommend; //后台推荐好友数
		//String t_snsfriend; //新增第三方好友数时间戳
		
//		String timemilli_fans; // 新增粉丝数时间戳
//		String timemilli_active; // 新增活动数时间戳
//		String timemilli_snsfriend; //新增第三方好友数时间戳
//		String timemilli_zone_friend; //朋友圈新动态数时间戳
//		String timemilli_zone_subscribe; //订阅新动态时间戳
//		String timemilli_zone_mic; //微享新动态时间戳
	}
	//2.11.2 通知服务器消息已删除
	public static class deleteMessageRequest {
		ArrayList<Deletedmsg> deletedmsg;
		String mac; // 手机的mac地址
		String imei; // 手机imei（手机的唯一标识）
	}
	//2.11.2 通知服务器消息已删除
	public static class Deletedmsg {
		String userid; // "11111" // 当前用户id
		String target_userid; //目标用户id
		String messageid; // 当前评论id
		String messagetype; //1 删除自己发送的消息 2 删除收到的消息
	}
	//2.11.3 私信历史消息列表
	public static class listHistoryMessageRequest {
		String userid; // "11111" // 当前用户id
		String target_userid; // 目标用户id
		String timemilli; // 返回数据最后一条消息创建时间，回传
		String pagesize; // 记录数
		String mac; // 手机的mac地址
		String imei; // 手机imei（手机的唯一标识）
	}
	//2.11.4 crm回调，通知激活
	public static class crmCallbackRequest {
		String userid; // 用户id
		String bindname;// 绑定名称，base64编码
		String type; //1为注册2为绑定
	}
	//2.11.5 www播放页(重新整理，改成JSON格式)
	public static class playPageRequest {
		String shortUrl;// 短连接
	}
	//2.11.6 手机通讯录列表(需要确认表结构是否修改)
	public static class phoneBookRequest{
		public String userid; // 用户id
		public String mac; // 手机的mac地址
		public String imei; // 手机imei（手机的唯一标识）
	}
	//2.11.7 0客服接口
	public static class customerRequest{
		public String userid; // 用户id
		public String mac; // 手机的mac地址
		public String imei; // 手机imei（手机的唯一标识）
	}
	
	//2.12.1 通讯录邀请好友加入”原来“
	public static class invatePhoneAddressRequest{
		public String userid; // 用户id
		public String mac; // 手机的mac地址
		public Invate_users[] invate_users;
		public String imei; // 手机imei（手机的唯一标识）
	}
	
	public static class Invate_users{
		public String user_phone; //联系人电话号
		public String user_name; // 联系人姓名,base64编码
	}
	
	//2.7.1.4 新好友列表
	public static class newFriendsRequest{
		public String userid;
		public String user_time;
		public String mac;
		public String imei;
	}
	
	//2.6.1.3 好友动态
	public static class friendNewsRequest {
		public String userid;
		public String pagesize;
		public String diary_time;
		public String request_type;
		public String diarywidth;
		public String diaryheight;
		public String mac;
		public String imei;
	}
	
	//清除新好友推荐
	public static class cleanRecommendRequest {
		public String userid;
		public String target_userid;
		public String mac;
		public String imei;
	}
	
	//================================重 要 结 构================================
	
	public static class SNS {
		public String snstype; // 微博类型
		public String snsid; // 微博用户id
		public String weiboid; // 微博id
	}
	
	public static class Attachs {
		public boolean issynchronized;//App内部使用的值, 用来标记附件是否已经与服务器同步 (即使是同步过的附件, 再进行更新时此值也要先设置成false);
		public String attachid;
		public String attachuuid;
		public String content;
		public String attach_type; // "附件类型",  //1视频 2音频 3图片 4文字5语音便签;
		public String level;
		public String attach_longitude;
		public String attach_latitude;
		public String suffix;
		public String Operate_type;
		public String filepath;//App内部使用的值, 用来存储附件物理文件的地址 (Operate_type == GsonProtocol.ATTACH_OPERATE_TYPE_UPDATE时有效);
		
		public boolean isAddOperate() {
			return Operate_type.equals(GsonProtocol.ATTACH_OPERATE_TYPE_ADD);
		}
		
		public boolean isDeleteOperate() {
			return Operate_type.equals(GsonProtocol.ATTACH_OPERATE_TYPE_DELETE);
		}
		
		public boolean isUpdateOperate() {
			return Operate_type.equals(GsonProtocol.ATTACH_OPERATE_TYPE_UPDATE);
		}
		
		public boolean isSynchronized() {
			return issynchronized;
		}
		
		public boolean isMainAttach() {
			return level.equals(GsonProtocol.ATTACH_LEVEL_MAIN);
		}
	}
	
	public static class AddrBook {
		public String phone_num;// 联系人电话号,多个电话号码，逗号隔开
		public String phone_name;// 联系人姓名,base64编码
		@Override
		public boolean equals(Object object) {
			// TODO Auto-generated method stub
			if (!(object instanceof AddrBook)) {
				return false;
			}
			AddrBook addrBook = (AddrBook) object;
			if (this.phone_name.equals(addrBook.phone_name) && this.phone_num.equals(addrBook.phone_num)) {
				return true;
			}
			return false;
		}
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
	
	public static class uploadPicture{
		String upload_pic_type;//1用户头像，2视频封面，3用户上传壁纸4心情图片5	www 大图 小图6	后台客户壁纸7活动图片8 栏目头像9天气图片(1、2、3返回imageurl返回url，3-8返回imageurl返回路径)
		String userid;//用户id,supload_pic_type=1或2、3有效
		String attachid;//附件id，supload_pic_type=2有效
		String imei;//手机唯一标识，supload_pic_type=1或2、3有效
		String mac;//mac地址，supload_pic_type=1或2、3有效
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
	
}
