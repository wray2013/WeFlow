package com.cmmobi.looklook.common.gson;

import java.io.IOException;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import android.content.Intent;
import android.os.Environment;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;
import cn.zipper.framwork.core.ZApplication;
import cn.zipper.framwork.utils.ZStringUtils;

import com.cmmobi.looklook.MainApplication;
import com.cmmobi.looklook.activity.LookLookActivity;
import com.cmmobi.looklook.common.gson.GsonRequest3.createStructureRequest;
import com.cmmobi.looklook.common.storage.SqliteDairyManager;
import com.cmmobi.looklook.common.utils.DateUtils;
import com.cmmobi.looklook.common.utils.MD5;
import com.cmmobi.looklook.info.profile.AccountInfo;
import com.cmmobi.looklook.info.profile.ActiveAccount;
import com.cmmobi.looklook.info.profile.DiaryManager;
import com.cmmobi.looklook.info.profile.MediaMapping;
import com.cmmobi.looklook.info.profile.MediaValue;
import com.cmmobi.looklook.info.profile.TimeHelper;
import com.cmmobi.looklook.info.profile.WrapUser;
import com.cmmobi.sns.oauthv2.OAuthV2;
import com.cmmobi.sns.utils.ConfigUtil.SHARE_TO;

import effect.Mp4Info;

/**
 * @author wangrui
 */
public class GsonResponse3 {

	// ================================RIA 响 应 结
	// 构================================
	// ===2.1===

	// ===2.2===

	// ===2.3===

	// ===2.4===

	// ===2.5===
	// 2.5.1 获取标签(缓存到本地，每次刷新到数据替换本地)
	public static class taglistResponse {
		public String status; // 0可用、1不可用
		public String crm_status;
		public taglistItem[] tags;
	}

	// 2.5.2 获取空间背景图片列表
	public static class getSpaceCoverListResponse {
		public String status; // 0成功
		public String crm_status;
		public String index; // ”ddd_dd_dd”
		public String hasnextpage; // 1有下一页，0没有下一页
		// 排序,按昵称拼音排序
		public getbackgroundlistItem[] backgrounds;
	}

	// 2.5.3 获取Socket信息(IP、PORT)
	public static class getSocketResponse {
		public String status; // 0:成功，1：用户名已存在
		public String crm_status;
		public String ip;
		public String port;
		public String spacesize; // ”2048M”
		public String maxsize; // ”2048M”
	}

	// 2.5.4 客户意见反馈
	public static class feedbackResponse {
		public String status; // 0成功
		public String crm_status;
	}

	// 2.5.5 客户端抓取用户第三方互粉传送给服务器
	public static class postSNSFriendResponse {
		public String status; // 0成功
		public String crm_status;
	}

	// 2.5.6 举报日记
	public static class reportResponse {
		public String status; // 0成功
		public String crm_status;
	}

	// 2.5.7 获取特效下载地址
	public static class getEffectResponse {
		public String status;
		public String crm_status;
		public Effects effects;
	}

	// 2.5.8 获取天气
	public static class getWeatherResponse {
		public String status; // 0:成功，1：失败
		public String crm_status;
		public MyWeatherDescription[] weather;// 天气描述集合
	}

	// 2.5.9 默认提示内容(需要统计出都有哪些提示内容)
	public static class getdefaultPromptMsgResponse {
		public String status; // 0:成功，1：失败
		public String crm_status;
		public String type;// 提示消息的类型
		public String msg;// 默认的提示消息
	}

	// 2.5.10 发送通讯录(通讯录与第三方互粉合并,需要确定传送的电话号码数量)
	public static class postAddressBookResponse {
		public String status; // 0成功
		public String crm_status;
	}

	// 2.5.10 切换账号数据合并接口
	public static class mergerAccountResponse {
		public String status; // 0成功
		public String crm_status;
		public String newUserid;
		public String oldUserid;
	}

	// ---2.5.11---
	// 2.5.11.1 编辑用户信息
	public static class changeUserInfoResponse {
		public String status; // 0:成功，1：用户名已存在
		public String crm_status;
		public String headimageurl; // 头像地址 //portraiturl;
	}

	// 2.5.11.2 修改登录密码
	public static class passwordChangeResponse {
		public String status; // 0:成功，1：用户名已存在
		public String crm_status;
	}

	// 2.5.11.3 修改个性签名
	public static class moodResponse {
		public String status; // 0:成功
		public String crm_status;
	}

	// 2.5.11.4 Looklook系统设置
	public static class diaryPrivacyResponse {
		public String status; // 0:成功
		public String crm_status;
	}
	
	// 2.5.11.4 Looklook系统设置
		public static class autoFriendResponse {
			public String status; // 0:成功
			public String crm_status;
		}

	// 2.5.11.5 设置手势密码
	public static class addGesturePasswordResponse {
		public String status; // 0:成功
		public String crm_status;
	}

	// 2.5.11.6 设置个性排序
	public static class mypersonalsortResponse {
		public String status; // 0:成功
		public String crm_status;
	}

	// 2.5.11.7 设置用户头像(供图片服务器访问，3.1 socket处理)
	public static class setHeadImageResponse {
		public String status; // 0成功
		public String imageurl; // ”http://iamge.looklook.cn/201212/03/543213609KJH76.jpg”
	}

	// ===2.6===
	// 2.6.1 启动调用接口(UA、升级)
	public static class uaResponse {
		public String status;
		public String crm_status;
		public String equipmentid;
		public String systime;// 系统时间
		public String ip;
		public String versionnumber;// 返回版本号
		public String type;// 0:已是最新版本 1:强制升级 2:普通升级
		public String filesize;// 该版本文件大小
		public String path;// 进行升级下载地址
		public String description;// 版本描述
		public String servertime;// 服务器返回时间
		public String mishare_no;// 服务器返回时间
		public String userid;// 服务器返回时间

	}

	// 2.6.2 用户登录返回数据集合
	// 2.6.3 用户登录(密码加密，与CRM确认)
	public static class loginResponse {
		public String status; // 0：成功，1：第三方登录账号不存在,创建新账号, 2密码错误，3用户不存在
		public String crm_status;
		public String userid; // 用户ID
		public String nickname; // 用户昵称
		public String headimageurl;// portraiturl; //头像地址
		public String sex;
		public String address;
		public String birthdate;
		public String signature;// tag; //可为空
		public String app_downloadurl; // "http://t.cn/zj9WidU"//looklook官方下载地址
		public String sync_type;// 数据同步，1关闭 2仅WIFI 3任何网络
		public String accept_friend_status;//手机通讯录请求好友标记，1：自动，2：手动，默认为1即自动
		public String gesturepassword;// 手势密码
		public String is_firstlogin; // 0 未登录过 1已登陆过
		public MyBind binding[];// 已绑定信息
		public String personalsort; // 首页面菜单顺序
		public String mishare_no; // 首页面菜单顺序
	}

	// 2.6.4 自动登录(同时设置deviceToken)
	public static class autoLoginResponse {
		public String status; // 0：成功，1：失败（非最新登录）
		public String crm_status;
		public String userid; // 用户ID
		public String nickname; // 用户昵称
		public String headimageurl;// portraiturl; //头像地址
		public String sex;
		public String address;
		public String birthdate;
		public String signature;// tag; //可为空
		public String app_downloadurl; // "http://t.cn/zj9WidU"//looklook官方下载地址
		public String sync_type;// 数据同步，1仅WIFI 2任何网络
		public String gesturepassword;// 手势密码
		public MyBind binding[];// 已绑定信息
		public String personalsort; // 首页面菜单顺序
	}

	// 2.6.5 Looklook用户注册
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
		public String sync_type;// 数据同步，1仅WIFI 2任何网络
		public String is_firstlogin; // 0 未登录过 1已登陆过
		public MyBind binding[];// 已绑定信息
	}

	// 2.5.5切换手机号绑定
	public static class changeBindingResponse {
		public String status; // 0:成功
		public String crm_status;
	}

	// 2.5.5切换手机号绑定
	public static class verifySMSResponse {
		public String status; // 0:成功
		public String crm_status;
	}

	// 2.6.6 绑定(邮箱、手机、第三放平台)
	public static class bindingResponse {
		public String status;// 0成功，其他查看错误文档;23该邮箱已经被绑定;24该用户已经绑定一个邮箱;
								// 26指定第三方账户已经注册，或绑定其他账号;18手机验证码错误;
								// 19手机验证码已超时;21此手机号已经被绑定
		public String crm_status;
		public String snstype; // 1：新浪微博，2人人，6腾讯
		public String binding_type; // 1邮箱，2手机，3备用手机，4第三放平台
	}

	// 2.6.7 解除绑定
	public static class unbindResponse {
		public String status;// 状态，0：成功,25该用户未绑定指定邮箱,
								// 22该用户未绑定指定手机号27该用户未绑定指定第三方账户
		public String crm_status;
		public String snstype; // 1：新浪微博，2人人，6腾讯
		public String binding_type; // 1邮箱，2手机，3备用手机，4第三放平台
	}

	// 2.6.8 验证注册用户名是否可用(敏感词CRM or RIA调用接口处理，37、38考虑合并)
	public static class checkUserNameExistResponse {
		public String status; // 0可用、1不可用
		public String crm_status;
	}

	// 2.6.9 验证注册昵称是否可用--(第三方用户昵称规则？)
	public static class checkNickNameExistResponse {
		public String status; // 0可用、1不可用
		public String crm_status;
	}

	// 2.6.10 判断用户是否有效(是否被拉黑)
	public static class checkuserResponse {
		public String status; // 0:有效
		public String crm_status; // 当status=200600，crm_status有效
	}

	// 2.6.11 找回密码
	public static class forgetPasswordResponse {
		public String status; // 0：处理成功,1：没有这个账号
		public String crm_status;
	}

	// 2.6.12 获取手机验证码
	public static class checkNoResponse {
		public String status; // 0:成功
		public String crm_status;
	}

	// 2.6.13 解除保险箱
	public static class unSafeboxResponse {
		public String status;
		public String crm_status;
	}

	// ===2.7===
	// 2.7.1.1 我的日记
	public static class listMyDiaryResponse {
		public String status; // 0:成功
		public String crm_status;
		public String first_diary_time;
		public String last_diary_time;
		public String is_refresh;
		public String hasnextpage;
		public MyDiaryList[] diaryids;// 日记列表
		public MyDiary[] diaries;
		public String removediarys;
	}

	// 2.7.1.2 个人空间(我的日记16区别在于一些附属信息)
	public static class homeResponse {
		public String status; // 0成功
		public String userid;// 用户userid
		public String crm_status;
		public String first_diary_time;
		public String last_diary_time;
		public String is_refresh;
		public String isattention;// 0 未关注 1是已关注/当前用户是否关注显示用户
		public String isblacklist;// 0 不是我的黑名单 1是黑名单(当前用户是不是显示用户的黑名单)
		public String isviewblacklist;// 0 不是我的黑名单 1是黑名单(显示用户是不是当前用户的黑名单)
		public String viewisattention;
		public String background;
		public String misharecount;// 他人空间微享的数值，我的空间此字段为空
		public String headimageurl;
		public String nickname;
		public String signature;
		public String sex;
		public String hasnextpage;// 0 没有下一页 1 有下一页
		public MyDiaryList[] diaryids;// 日记列表
		public MyDiary[] diaries;
		public String removediarys;
	}

	// 2.7.1.3 获取收藏日记列表
	public static class listCollectDiaryResponse {
		public String status;
		public String crm_status;
		public String first_diary_time;// 第一条记录的时间
		public String last_diary_time;// 最后一条记录的时间
		public String is_refresh;
		public String hasnextpage;// 0 没有下一页 1 有下一页
		public CollectDiary[] listcontents;
		public String removediarys;
	}

	public static class CollectDiary {
		public String type_content; // 转发内容
		public String type_nickname;// 转发昵称
		public String type_headimageurl;// 转发头像地址
		public String type;// 1转发 2 订阅 0 正常
		public String update_time;// 最后更新时间
		public String nickmarkname;// 备注
		public String share_updatetime;// 分享更新时间
		public OfficalSub[] looklook; // 官方订阅，需要@的数据
		public DiaryInfo diaryinfo;
	}

	public static class OfficalSub {
		public String looklook; // 官方订阅，需要@的数据
		public String nickname;// 昵称",
		public String userid;// 用户id

	}

	public static class DiaryInfo {
		public String sharecontent;// 分享内容文字
		public MyDiary[] diaries;
		public DiaryRelation diaryid; // 日记信息
		public ArrayList<EnjoyHead> enjoyheadurl;// 赞人头像列表
		public ArrayList<ShareCommentList> commentlist; //
	}

	public static class ShareCommentList {
		public String publishid;// 分享日记id
		public String share_time;// 分享时间
		public String share_status;// 分享类型 // 1新浪 2人人 5 qzone空间 6腾讯 9微信朋友圈 10短信
									// 11邮箱 12微信好友 100站内公开 101朋友圈 103微享
		public String comment_count;// 评论条数
		public String snsid;// 用户第三方账户的ID
		public String weiboid;// //微博ID
		public ArrayList<CollectionComment> comments; // 收藏评论
	}

	public static class CollectionComment {
		public String userid;// 评论用户ID
		public String headimageurl;// …jpg",//头像URL
		public String nickname;// 昵称
		public String nickmarkname;// 备注
		public String publishid;// 日记ID
		public String diaryid;// 日记ID
		public String commentcontent;// 评论
		public String commentid;// 评论ID，当前评论id
		public String commentuuid;// 评论UUID，当前评论uuid
		public String createtime; // 时间戳
		public String signature;// 是编码的，需要解码
		public String sex;// 0男，1女， 2未知
		public String isattention;// 0 未关注 1是已关注
		public String audiourl;// ...../jj.mp3" // 语音地址
		public String playtime;// 语音播放时长
		public String commentway;// 评论方式1、文字 2、声音3、声音加文字
		public String commenttype;// 评论类型：1、评论 2回复
		public String replynickname;// 被回复人昵称
		public String replymarkname;// 被回复人备注

	}

	// 2.7.1.4 我的评论列表(按照时间戳处理)
	public static class MyCommentListResponse {
		public String status; // 0：处理成功,
		public String crm_status;
		public String first_comment_time;// 第一条记录的时间
		public String last_comment_time; // 最后一条记录的时间
		public String is_refresh;
		public String hasnextpage;
		public MyCommentListItem[] comments;
		public String removecommenids; // 删除评论id评论
	}

	// 2.7.1.5 日记评论列表(每条评论不带日记信息)
	public static class diaryCommentListResponse {
		public String status; // 0：处理成功,
		public String crm_status;
		public String first_comment_time;// 第一条记录的时间
		public String last_comment_time; // 最后一条记录的时间
		public String is_refresh;
		public String hasnextpage;
		public diarycommentlistItem[] commentsr;
		public DiaryDetailComment[] comments;
		public String removecommenids; // 删除评论id评论
	}

	// //2.7.1.6 赞日记id列表
	// public static class EnjoyDiaryIDResponse {
	// public String status; // 0成功
	// public String crm_status;
	// public String diary_time;
	// public MyDiaryids[] diarieids;
	// }
	// 2.7.1.7 订阅日记列表
	public static class attentionListResponse {
		public String status; // 0成功
		public String crm_status;
		public MyAttention looklook;
		public String first_diary_time;// 第一条记录的时间
		public String last_diary_time;// 最后一条记录的时间
		public String is_refresh;
		public String hasnextpage;
		public MyDiary[] diaries;
		public String removediarys;
	}

	// 2.7.1.8 朋友日记列表
	public static class friendListResponse {
		public String status; // 0成功
		public String crm_status;
		public String first_diary_time;// 第一条记录的时间
		public String last_diary_time;// 最后一条记录的时间
		public String is_refresh;
		public String hasnextpage;
		public MyDiary[] diaries;
		public String removediarys;
	}

	// 2.7.1.9 收藏日记id列表
	public static class listCollectDiaryidResponse {
		public String status;
		public String crm_status;
		public String diary_time;// 第一条记录的时间
		public MyDiaryids[] diarieids;
	}

	// 72. 转发日记id列表(3.0获取赞日记id列表)
	public static class forwardDiaryIDResponse {
		public String status; // 0成功
		public String crm_status;
		public String diary_time;
		public MyDiaryids[] diarieids;
	}

	// ---2.7.2---
	// 2.7.2.1 日记结构管理(盛宏强)
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

	// 2.7.2.2 收藏日记(22、23整合)
	public static class addCollectDiaryResponse {
		public String status; // 0:成功
		public String crm_status;
	}

	// 2.7.2.3 取消收藏
	public static class removeCollectDiaryResponse {
		public String status; // 0:成功
		public String crm_status;
	}

	// 2.7.2.4 删除原日记
	public static class deleteDiaryResponse {
		public String status; // 0：处理成功,
		public String crm_status;
	}

	// 2.7.2.5 日记放入、移出保险箱
	public static class safeboxResponse {
		public String status; // 0:成功
		public String crm_status;
	}

	// 2.7.2.6 获取日记分享URL
	public static class getDiaryUrlResponse {
		public String status; // 0：处理成功
		public String crm_status;
		public shareImageUrl[] shareimageurl;
		public platformUrls[] platformurls;
		public String diaryid; // 如果是日记组返回日记组的ID
		public String diaryuuid; // 日记组时上传UUID
		public String publishid; // 返回日记分享ID

	}

	// 2.7.2.7 日记详情(宏强)
	public static class diaryInfoResponse {
		public String status; // 0：处理成功
		public String crm_status;
		public MyDiary diaries;
	}

	// 2.7.2.8 修改标签或位置(用户虚假位置信息)
	public static class modTagsOrPositionResponse {
		public String status; // 0成功
		public String crm_status;
	}

	// 2.7.2.9 详情页(分享)
	public static class diaryShareInfoResponse {
		public String status; // 0成功
		public String crm_status;
		public String diaryid;
		public String sharecontent; // "分享内容文字"
		public DiaryRelation diaryidr;
		public MyDiary[] diaries;
		public EnjoyHead[] enjoyheadurl; // 赞人头像列表
		public DiaryDetailCommentList[] commentlistr;
		public DiaryDetailComment[] commentlist;
	}

	// 2.7.2.10 日记权限
	public static class diarySharePermissionsResponse {
		public String status; // 0成功
		public String crm_status;
	}

	// ---2.7.3---
	// 2.7.3.1 分享记录
	public static class myPublishHistoryResponse {
		public String status; // 0成功
		public String crm_status;
		public String first_diary_time; // 第一条记录的时间
		public String last_diary_time; // 最后一条记录的时间
		public String is_refresh; // 是否强制刷新，1刷新，0不刷新
		public String hasnextpage; // 1有下一页，0没有下一页
		public String removepublishids; // "1,2,3" 返回删除分享的id集合 以逗号分开,客户端做同步
		public HistoryList[] historylist;
	}

	// 2.7.3.2 清除分享记录(4.0暂时不用)
	public static class cleanPublishDiaryResponse {
		public String status; // 0成功
		public String crm_status;
	}

	// 2.7.3.3 站内分享
	public static class diaryPublishResponse {
		public String status; // 0:成功，1：失败
		public String crm_status;
		public String diaryid;
		public String diaryuuid;
		public String publishid;

	}

	// 2.7.3.4 取消站内分享
	public static class cancelPublishResponse {
		public String status; // 0:成功，1：失败
		public String crm_status;
	}

	// 2.7.3.5 删除第三方分享轨迹
	public static class cancelShareResponse {
		public String status; // 0:成功，1：失败
		public String crm_status;
	}

	// 2.7.3.6 第三方社区分享
	public static class shareDiaryResponse {
		public String status; // 0:成功
		public String crm_status;
	}

	// ===2.8===
	// ---2.8.1---
	// 2.8.1.1 关注列表
	public static class myattentionlistResponse {
		public String status; // 0成功
		public String crm_status;
		public String user_time;
		public String hasnextpage;
		public WrapUser[] users;
		public String removeusers;
	}

	// 2.8.1.2 粉丝列表
	public static class myfanslistResponse {
		public String status; // 0成功
		public String crm_status;
		public String user_time;
		public String hasnextpage;
		public WrapUser[] users;
		public String removeusers;
	}

	// 2.8.1.3 朋友列表
	public static class myfriendslistResponse {
		public String status; // 0成功
		public String crm_status;
		public String user_time;
		public String hasnextpage;
		public WrapUser[] users;
		public String removeusers;
	}

	// 2.8.1.4 获取官方账号列表
	public static class getOfficialUseridsResponse {
		public String status;
		public String crm_status;
		public MyUserids[] userids;
	}

	// 2.8.1.5 黑名单列表
	public static class myblacklistResponse {
		public String status; // 0成功
		public String crm_status;
		public String user_time;
		public String hasnextpage;
		public WrapUser[] users;
		public String removeusers;
	}

	// 2.8.1.6 获取日记赞人列表
	public static class getDiaryEnjoyUsersResponse {
		public String status; // 0成功
		public String crm_status;
		public String first_diary_time; // 第一条记录的时间
		public String last_diary_time; // 最后一条记录的时间
		public String is_refresh; // 是否强制刷新，1刷新，0不刷新
		public String hasnextpage; // 1有下一页，0没有下一页
		// 排序,按时间升序排序
		public getDiaryEnjoyUsers[] enjoies;
	}

	// 2.8.1.7 搜索用户列表(除去系统黑名单)
	public static class searchUserResponse {
		public String status; // 0可用、1不可用
		public String crm_status;
		public WrapUser[] users;
	}

	// 2.8.1.8 已加入looklook的第三方互为关注用户(包括手机通讯录、新浪、腾讯、人人)
	public static class listUserSNSResponse {
		public String status;
		public String crm_status;
		public String user_time; // 最后一条记录时间戳
		public WrapUser[] users;
	}

	// 2.8.1.9 系统推荐用户列表
	public static class listUserRecommendResponse {
		public String status;
		public String crm_status;
		public String timestamp; // 用于回传的时间戳
		public WrapUser[] users;
	}

	// 2.8.1.10 好友请求列表接口
	public static class friendRequestListResponse {
		public String status; // 0：正常，
		public String crm_status; // 当status=200600，crm_status有效，crm状态请看（点击我）
		public String user_time; // 当页，最后一条记录的时间
		public WrapUser[] users; // 排序,按昵称拼音排序
		public String removeusers;
	}

	/*
	 * public static class friendRequestItem{ public String userid; //好友ID
	 * public String headimageurl; //…jpg",//头像URL，可为空 public String nickname;
	 * //昵称 public String requestmsg; //验证信息 public String sex; // 0男 1女 2未知
	 * public String signature; //个人签名 public String update_time; //更新时间 }
	 */
	// ---2.8.2---
	// 2.8.2.1 个人信息接口
	// 2.8.2.2 日记评论/回复
	public static class commentResponse {
		public String status; // 0：处理成功,
		public String crm_status;
		public String audiopath;
		public String commentid;
		public String commentuuid;
		public String ip;
		public String port;
	}

	// 2.8.2.3 发消息 (是否发日记，不发日记looklook赞世界处理方式)
	public static class sendmessageResponse {
		public String status; // 0成功
		public String crm_status;
		public String audiopath;
		public String uuid;// 客户端的私信uuid，如果因为网络原因，不知道是否发送成功，服务端可以判断，这条私信是否发送过并成功
		public String privatemsgid;
		public String servertime;
		public String ip;
		public String port;
	}

	// 2.8.2.4 赞 (喜欢)
	public static class enjoyResponse {
		public String status; // 0成功
		public String crm_status;
	}

	// 2.8.2.5 转发
	public static class repostResponse {
		public String status; // 0成功
		public String crm_status;
	}

	// 2.8.2.6 取消赞
	public static class deleteEnjoyResponse {
		public String status; // 0成功
		public String crm_status;
	}

	// 2.8.2.7 删除评论(删除自己评论和删除对自己日记进行的评论)
	public static class deleteCommentResponse {
		public String status; // 0成功
		public String crm_status;
	}

	// 2.8.2.8 加关注
	public static class attentionResponse {
		public String status; // 0成功
		public String crm_status;
		public String attention_userid; // 被关注用户ID
	}

	// 2.8.2.9 取消关注\删除粉丝
	public static class cancelattentionResponse {
		public String status; // 0成功
		public String crm_status;
		public String target_userid; // 回显目标用户id
	}

	// 2.8.2.10 备注名修改
	public static class setUserAliasResponse {
		public String status; // 0成功
		public String crm_status;
	}

	// 2.8.2.11 设置黑名单(用户黑名单)
	public static class operateblacklistResponse {
		public String status; // 0成功
		public String crm_status;
		public String target_userid;
	}

	// 2.8.2.12 申请加好友
	public static class addfriendResponse {
		public String status; // 0成功
		public String crm_status;
		public String target_userid;
	}

	// 2.8.2.13 同意好友请求
	public static class agreeFriendResponse {
		public String status; // 0成功
		public String crm_status;
		public String target_userid;
		public String user_time;
	}

	// 2.8.2.14 删除好友
	public static class deleteFriendResponse {
		public String status; // 0成功
		public String crm_status;
		public String target_userid;
	}

	// ===2.9===
	// 2.9.1 创建微享
	public static class createMicResponse {
		public String status; // 0成功
		public String crm_status;
		public String publishid; // 日记id
		public String uuid;
	}

	// 2.9.2 微享列表
	public static class myMicListResponse {
		public String status; // 0：成功,其他查看错误文档
		public String crm_status; // 当status=200600，crm_status有效，crm状态请看（点击我）
		public String first_comment_time; // 第一条记录的时间
		public String last_comment_time; // 最后一条记录的时间
		public String is_refresh; // 是否强制刷新，1刷新，0不刷新
		public String hasnextpage; // 1有下一页，0没有下一页
		public MicListItem[] showmiclist;
	}

	// 他人微享列表
	public static class MicListResponse {
		public String status; // 0：成功,其他查看错误文档
		public String crm_status; // 当status=200600，crm_status有效，crm状态请看（点击我）
		public String first_comment_time; // 第一条记录的时间
		public String last_comment_time; // 最后一条记录的时间
		public String is_refresh; // 是否强制刷新，1刷新，0不刷新
		public String hasnextpage; // 1有下一页，0没有下一页
		public MicListItem[] showmiclist;
	}

	// 2.9.3 微享内容详情页
	public static class myMicInfoResponse {
		public String status;
		public String crm_status;
		public UserObj[] userobj; // 用户列表
		public MiShareinfo mishareinfo; // 微享内容
		public DiaryDetailComment[] comments;
	}

	// 2.6.1.7 微享评论列表
	public static class MicCommentsResponse {
		public String status; // 状态
		public String crm_status; // 当status=200600，crm_status有效，crm状态请看（点击我）
		public String first_comment_time; // 第一条记录的时间
		public String last_comment_time; // 最后一条记录的时间
		public String is_refresh; // 是否强制刷新，1刷新，0不刷新
		public String hasnextpage; // 1有下一页，0没有下一页
		public DiaryDetailComment[] comments;

	}

	public static class MiShareinfo {
		public String mic_headimageurl; // 微享用户头像
		public String mic_nickname; // 微享用户昵称
		public String nickmarkname; // "备注名"
		public String mic_title; // 微享主题
		public String content; // 微享描述
		public String micuserid; // 微享创建人id
		public String create_time;// 创建时间
		public String position;// 位置
		public String position_status;// 位置状态 1可见 0不可见
		public MyDiary[] diaryinfo;
	}

	// 2.9.4 微享预览页
	public static class mySubMicListResponse {
		public String status;
		public String crm_status;
		public String first_comment_time; // 第一条记录的时间
		public String last_comment_time; // 最后一条记录的时间
		public String is_refresh; // 是否强制刷新，1刷新，0不刷新
		public String hasnextpage;
		public UserObj[] userobj;
		public MicListItem[] showmiclist;
	}

	// 2.9.5 微享清屏
	public static class cleanMicResponse {
		public String status;
		public String crm_status;
	}

	// 2.9.6 微享放入保险箱
	public static class safeboxmicResponse {
		public String status;
		public String crm_status;
	}

	// 2.9.7 私信|消息阅读回调接口
	public static class readmsgResponse {
		public String status;
		public String crm_status;
	}

	// 免打扰
	public static class setundisturbResponse {
		public String status;
		public String crm_status;
	}

	// ===2.10===
	// 2.10.1 获取活动列表(缓存)
	public static class activeListResponse {
		public String status; // 0成功
		public String crm_status;
		public activeListItem[] active;
	}

	// 2.10.2 参与活动日记列表
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

	// 2.10.3 参加活动
	public static class joinActiveResponse {
		public String status; // 0成功
		public String crm_status;
	}

	// 2.10.4 活动中奖日记列表
	public static class getAwardDiaryListResponse {
		public String status; // 0成功
		public String crm_status;
		public getAwardDiaryListItem[] awards;
	}

	// 2.10.5 取消参加活动
	public static class cancelActiveResponse {
		public String status; // 0成功
		public String crm_status;
	}

	// ===2.11===
	// 2.11.1 心跳,新消息列表(需要确定都有哪些信息)
	public static class listMessageResponse {
		public String status; // 0:成功
		public String crm_status; // 当status=200600，crm_status有效，crm状态请看（点击我）
		public String last_timemilli; // "1324689755413", //最后一条消息创建时间
		public String t_push; // "1324689755413", //push时间戳
		public String server_time; // "1324689755413", //服务器时间
		public String hasnextpage; // "1", //1有下一页，0没有下一页
		public String commentnum; // ""， //评论数，当评论数返回N时，客户端显示点
		public String commentnum_safebox; // 评论数(保险箱内)

		public String new_zonemicnum; // 微享新动态数时间戳
		public String new_safeboxmicnum; // 微享是否有新动态数 1有 0没有(保险箱内)
		public String new_friend_change; // 好友新动态数
		public String new_friend; // 是否有新通讯录好友 1有 0没有
		public String new_requestnum; // 新增好友请求数
		public String friendnum; // 通讯录所有朋友总数

		public MessageUser[] users;
		public HeartPush[] push;
		
		public String readedMessages; //已读的消息id，多个之间使用逗号隔开
	}

	// 2.11.2 通知服务器消息已删除
	public static class deleteMessageResponse {
		public String status; // 0成功
		public String crm_status;
	}

	// 2.11.3 私信历史消息列表
	public static class listHistoryMessageResponse {
		public String status; // 0成功
		public String crm_status;
		public String last_timemilli;
		public String hasnextpage;
		public MyMessage[] message;
	}

	// 2.11.4 crm回调，通知激活
	public static class crmCallbackResponse {
		public String status;
		public String crm_status;
	}

	// 2.11.5 www播放页(重新整理，改成JSON格式)
	public static class playPageResponse {
		public String json;
	}

	// 2.11.6 手机通讯录列表(需要确认表结构是否修改)
	public static class phoneBookResponse {
		public String status;
		public String crm_status;
		public WrapUser[] users;
	}

	// 2.11.7 0客服接口
	public static class customerResponse {
		public String status;
		public String crm_status;
		public String userid; // 好友ID
		public String headimageurl; // 头像URL，可为空
		public String nickname; // 昵称（如果未加入则为通讯录中的联系人名称）
		public String sex; // 0男 1女 2未知

	}
	
	//2.12.1 通讯录邀请好友加入”原来“
	public static class invatePhoneAddressResponse {
		public String status;
		public String crm_status;
	}

	// 2.7.1.4 新好友接口
	public static class newFriendsResponse {
		public String status;
		public String crm_status;
		public String user_time;
		public NewFriends[] users;
	}

	public static class friendNewsResponse {
		public String status;
		public String crm_status;
		public String first_diary_time;
		public String last_diary_time;
		public String is_refresh;
		public String hasnextpage;
		public Contents[] contents;
		public String removediarys;
	}

	public static class cleanRecommendResponse {
		public String status;
		public String crm_status;
	}

	public static class Contents {
		public String nickmarkname;
		public MyDiary diaries;
	}

	// ================================重 要 结 构================================

	public static class NewFriends {
		public String userid;
		public String headimageurl;
		public String nickname;
		public String telname;
		public String requestmsg;
		public String source;
		public String sex;
		public String update_time;
		public String request_status;
	}

	public class MessageUser {
		public String userid; // 发送者用户ID
		public String nickname;// 昵称，base64编码,关注人如果有备注显示备注名称
		public String markname;// 备注，base64编码,关注人如果有备注显示备注名称
		public String headimageurl;// 头像URL，可能为空
		public String isattention;
		public String sex;
		public String signature;
		public String usertype;  //如果字段为5则说明是客服
		public MyMessage[] message;
	}

	static public class MyMessage {
		public MyMessage() {
			privmsg = new PrivMsg();
		}

		public String messageid;
		public String timemill;
		public String isread; // 1未读 2已读
		public String act; // 1消息
		public String content;
		public String isowner; // 是否是自己发出的消息 1是 2 否
		public boolean notReadLocalAudio; // 对方的消息是否本地已阅读
		public PrivMsg privmsg;
	}

	static public class PrivMsg {
		public String content;
		public String privmsg_type;// //私信类型 --- 1代表纯文字 2代表语音 3代表日记 4语音加文字
		public String audiourl;// 语音地址
		public String playtime; // h:m:s
	}

	public class UserList {
		public String headimageurl; // 头像URL，可为空
		public String nickname; // 昵称
		public String userid; // 用户id
	}

	public class getDiaryEnjoyUsers {
		public String userid; // 用户ID
		public String headimageurl; // 头像URL，可为空
		public String nickname; // 昵称
		public String sex; // 0男，1女， 2未知
		public String signature; // 是编码的，需要解码
	}

	public class DiaryIdsHistoryList {
		public String publishid; // 分享ID
		public String sharecontent; // "分享内容文字"
		public String userid; // "分享的userid"
		public String diaryid; // 日记ID，如果是组，为组id
		public String isgroup; // 是否是组日记
		public String update_time; // 最后更新时间
		public String contain; // 组内包含的日记
	}

	public class HistoryList {
		public DiaryIdsHistoryList[] diaryids; // 分享列表
		public String is_encrypt; // 是否加入保险箱
		public String diary_type; // 日记类型，1视频 2音频 3图片 4文字 5内容组
		public String playtime; // 播放时长
		public String create_time; // 创建时间，时间戳
		public String publish_time; // 分享时间，时间戳
		public String publish_type; // 1新浪 2人人 6腾讯 10短信 11邮箱 9微信私信 9微信朋友圈
									// 100站内公开 101朋友圈 102私密分享 103微享
		public MicPics[] mic_pics;
		public MyDiary[] diaries;
		public String publish_content;// 分享内容
		public String mic_users;
	}

	public class HistoryMessage {
		public String messageid;
		public String content;
		public String isowner; // 是否是自己发出的消息 1是 2 否
		public String privmsg_type; // 私信类型 --- 1代表纯文字 2代表语音 3代表日记 4语音加文字
		public String audiourl; // 语音地址
		public String playtime; // 语音播放时长
	}

	public class MicPics {
		public String headimageurl;
	}

	public class MyAttention {
		public String publishid;
		public AttentionComment[] content;
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

		public int getItemCount() {
			if (diaries.length > 0) {
				return diaries.length + 1;
			}
			return 0;
		}
	}

	public class activeListItem implements Serializable {
		public String activeid; // 活动ID
		public String activename; // 活动名称
		public String starttime; // 开始时间
		public String endtime; // 结束时间
		public String introduction; // 简介
		public String add_way; // 参与方式
		public String rule; // 活动规则
		public String prize; // 奖品
		public String picture; // 图片地址
		public String isjoin; // 1代表参加 0 代表未参加
		public String iseffective; // "1结束 0可以参与 2未开始"
		public String range; // 1视频 2图片 3音频 4便签 多种，则用都好隔开例如：1,2 视频和图片
	}

	// 2.7.1.5 我的评论列表--评论条目
	public class MyCommentListItem {

		public MyDiaryList[] diaryids;// 日记列表
		public MyDiary[] diaries;
		// public MyCommentDiaries diaries;
		public String publishid;// 分享ID
		public String diaryid;// diaryiD
		public String userid;// 评论用户ID
		public String headimageurl;// 头像URL
		public String nickname;// 昵称
		public String nickmarkname;// 备注
		public String commentcontent;// 评论
		public String commentid;// 评论ID，当前评论id
		public String commentuuid;// 评论UUID，当前评论uuid
		public String createtime; // 时间戳
		public String signature;// 是编码的，需要解码
		public String sex;// 0男，1女， 2未知
		public String isattention;// 0 未关注 1是已关注
		public String audiourl;// 语音地址
		public String playtime;// 语音播放时长
		public String commentway;// 评论方式1、文字 2、声音3、声音加文字
		public String commenttype;// 评论类型：1、评论 2回复
		public String replynickname;// 被回复人昵称
		public String replymarkname;// 被回复人备注
	}

	public class MyCommentDiaries {
		public String diaryid;// 如果是组，此id为组的id，如果不是组，为日记id
		public String isgroup;// 是否是组 1是 0不是
		public MyDiary[] diarylist;
	}

	public class diarycommentlistItem {
		public String userid; // 评论用户ID
		public String headimageurl; // ”http://…jpg”, 头像URL
		public String nickname; // ”昵称”,//昵称
		public String nickmarkname; // 备注
		public String publishid;
		public String diaryid;
		public String commentcontent;// ”好视频”, 评论
		public String commentid; // 1111, 评论ID
		public String commentuuid;
		public String createtime; // ”1616516519” 时间戳
		public String signature; // ”个性签名” 是编码的，需要解码
		public String sex; // 0男，1女， 2未知
		public String isattention; // 0 未关注 1是已关注
		public String audiourl;
		public String playtime; // 语音播放时长
		public String commentway;// 评论方式1、文字 2、声音3、声音加文字
		public String commenttype;// 评论类型：1、评论 2回复
		public String replynickname; // 被回复人昵称
		public String replymarkname; // 被回复人备注
		public ReplyComment replycomment;
		public CommentContent content;
	}

	public class ReplyComment {
		public String userid; // 评论用户ID
		public String headimageurl; // ”http://…jpg”, 头像URL
		public String nickname; // ”昵称”,//昵称
		public String commentcontent;// ”好视频”, 评论
		public String commentid; // 1111, 评论ID
		public String createtime; // ”1616516519” 时间戳
		public String signature; // ”个性签名” 是编码的，需要解码
		public String sex; // 0男，1女， 2未知
		public String isattention; // 0 未关注 1是已关注
		public String audiourl;
		public String playtime; // 语音播放时长
	}

	public class CommentContent {
		public String content_type; // 内容类型：1日记视频、2日记长录音、3日记图片、4日记文字、5日记短录音、6日记长录音加文字、7日记短录音加文字、8评论文字、9评论语音、10评论语音加文字
		public String imageurl; // 日记视频封面，日记图片
		public String audiourl; // 日记长录音，日记短录音，评论语音
		public String content; // 日记文字内容，评论文字内容
	}

	public class AttentionComment {
		public String description; // 文字描述
		public String nickname; // 昵称
		public String userid; // 日记id
	}

	public static class MyBind {
		public MyBind(int curWeiboIndex, OAuthV2 oAuth) {
			// TODO Auto-generated constructor stub
			binding_type = "3";
			binding_info = "3";
			if (curWeiboIndex == SHARE_TO.SINA.ordinal()) {
				snstype = "1";
			} else if (curWeiboIndex == SHARE_TO.RENREN.ordinal()) {
				snstype = "2";
			} else if (curWeiboIndex == SHARE_TO.TENC.ordinal()) {
				snstype = "6";
			} else if (curWeiboIndex == SHARE_TO.QQMOBILE.ordinal()) {
				snstype = "13";
			}

			snsuid = oAuth.getOpenid();
			sns_nickname = oAuth.getNick();
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
		public String country_code;// 手机号国际码
		public String email_status; // 邮箱激活状态，1激活，0未激活,binding_type=2,有效
		public String sns_nickname;// ":"第三方昵称"， //第三方昵称
		public String sns_token;// ":"第三方token"， //第三方token
		public String sns_expiration_time;// ":"第三方过期时间"， //第三方过期时间(到时间点)
		public String sns_effective_time;// ":"第三方有效时间"， //第三方有效时间（时间段）
		public String sns_openkey;// ":"第三方openkey"， //第三方openkey,只有腾讯有效
		public String sns_refresh_token;// ":"第三方刷新token" //第三方刷新token,只有腾讯有效

	}

	public static class MyWeatherDescription {
		public String description;// 天气描述
		public String weatherurl; // 天气图片地址
		public String date; // 日期
	}

	public class Effects {
		public String effectsid; // 特效id
		public String effectsurl; // 特效url
		public String effectsname; // 特效名称
		public String weight; // 权重值
	}

	public class getbackgroundlistItem {
		public String spacecoverurl; // 图片路径
		public String backgroundpath; // 头像URL，可为空
	}

	public class Cover {
		public String picture;
	}

	public static class UserObj implements Parcelable {
		public String userid;
		public String user_tel;
		public String headimageurl; // 头像URL，可为空
		public String user_telname; // 用户集合
		public String mic_source;// 1点击加号 2@通讯录

		@Override
		public int describeContents() {
			return 0;
		}

		@Override
		public boolean equals(Object o) {
			// TODO Auto-generated method stub
			if (o == null) {
				return false;
			}
			UserObj user = (UserObj) o;
			if (user.userid == null && this.userid == null) {
				if (user.user_tel.equals(this.user_tel)
						&& user.user_tel.equals(this.user_tel)) {
					return true;
				} else {
					return false;
				}
			} else if (user.userid == null || this.userid == null) {
				return false;
			} else {
				return user.userid.equals(this.userid);
			}
		}

		@Override
		public void writeToParcel(Parcel dest, int flags) {
			dest.writeString(userid);
			dest.writeString(user_tel);
			dest.writeString(headimageurl);
			dest.writeString(user_telname);
			dest.writeString(mic_source);
		}

		public static final Parcelable.Creator<UserObj> CREATOR = new Parcelable.Creator<UserObj>() {

			@Override
			public UserObj createFromParcel(Parcel source) {
				UserObj obj = new UserObj();
				obj.userid = source.readString();
				obj.user_tel = source.readString();
				obj.headimageurl = source.readString();
				obj.user_telname = source.readString();
				obj.mic_source = source.readString();

				return obj;
			}

			@Override
			public UserObj[] newArray(int size) {
				return new UserObj[size];
			}
		};
	}

	public static class VshareDiary {
		public String diaryid; // 日记ID
		public String diaryuuid; // 日记UUID
		public String imageurl; // 封面,1张图或多张图的URL，音频此字段为空
		public String type; // 1视频、2音频 3图片(如果是单内容有效)
		public String playtime; // 播放时长(如果是单内容有效)
	}

	public static class MicListItem {
		public String publishid; // 微享ID
		public String uuid; // 微享UUID
		public String micuserid; // 微享创建人id
		public String headimageurl; // 头像URL，可为空
		public String mic_safebox; // 是否加入保险箱，1是 0否
		public String mic_title; // 微享主题
		public String content; // 微享内容
		public String commentnum; // 未读评论数；
		public String is_undisturb; // 免打挠 1：是 0： 否
		public String create_time; // 创建时间
		public String update_time;// 最后更新时间
		public String newcomment; // 最新评论
		public String micusernames; // 单个微享参与人名称拼接
		public String capsule; //时光胶囊标识 0 不是 1是
		public String burn_after_reading; //阅后即焚标识0 不是 1是
		public String capsule_time; //时光胶囊开启时间
		public String is_clear; //微享已读标记 1：是 0： 否
		public UserObj[] userobj;
		public VshareDiary[] diarys; // 本微享中所有日记（本地及已上传的）的缩略图所需信息
		public MyDiary[] diarysLocal; // 导入生成的本地日记，待上传
		private String upload_status; // 0 上传中 1上传失败 2上传成功3微享成功
		public String latitude;
		public String longitude;
		public String position;
		public String position_status;


		public static final String INTENT_STATUS_CHANGE = "intent_status_change";
		public static final String BUNDLE_SHARE_UUID = "bundle_share_uuid";
		public static final String BUNDLE_SHARE_STATUS = "bundle_share_status";

		public void setUpload_status(String status) {
			upload_status = status;
			Intent intent = new Intent(INTENT_STATUS_CHANGE);
			intent.putExtra(BUNDLE_SHARE_UUID, uuid);
			intent.putExtra(BUNDLE_SHARE_STATUS, status);
			LocalBroadcastManager.getInstance(MainApplication.getInstance())
					.sendBroadcast(intent);
		}

		public String getUpload_status() {
			return upload_status;
		}
	}

	public class MyAttach {
		public String attachid; // 附件id
		public String attachuuid; // 客户端创建日记附件的uuid，确定日记附件在客户端的唯一标识
		public String path; // 附件物理存放地址
	}

	public class MyDiaryids {
		public String diaryid; // 分享ID
		public String publishid; // 分享日记ID
	}

	public class MyUserids {
		public String userid; // 用户id
		public String nickname;// 昵称
		public String type; // 类型
		public String headimageurl;// 头像地址
	}

	public static class MyDiaryList {
		public String diaryid;
		public String diaryuuid;
		public String publishid;// 分享ID，评论 收藏 赞动作都是对这个id而言
		public String sharecontent;// 分享内容文字
		public String userid;// 分享的userid
		public String isgroup;// 是否是组日记 1是组 0不是组
		public String join_safebox;// 是否加入保险箱，1是 0否
		public String create_time;// 创建时间
		public String update_time;// 最后更新时间
		public String contain;// 组内包含的日记
		public shareInfo[] shareinfo;

		public void addShareInfo(shareInfo info) {
			if (info != null) {
				LinkedList<shareInfo> list = new LinkedList<GsonResponse3.shareInfo>();
				if (shareinfo != null && shareinfo.length > 0) {
					list.addAll(Arrays.asList(shareinfo));
				}
				list.addFirst(info);
				shareinfo = list.toArray(new shareInfo[list.size()]);
			}
		}

		public void removeShareInfo(shareInfo info) {
			if (info != null && shareinfo != null) {
				LinkedList<shareInfo> list = new LinkedList<GsonResponse3.shareInfo>();
				if (shareinfo != null && shareinfo.length > 0) {
					list.addAll(Arrays.asList(shareinfo));
				}
				Iterator<shareInfo> ite = list.iterator();
				while (ite.hasNext()) {
					shareInfo share = ite.next();
					if (share.share_status.equals(info.share_status)) {
						ite.remove();
					}
				}
				shareinfo = list.toArray(new shareInfo[list.size()]);
			}
		}

		// 获取过滤之后的用于显示在空间页的分享状态
		public shareInfo[] getFilterShareInfo() {
			/*
			 * ArrayList<shareInfo> infos = new
			 * ArrayList<GsonResponse3.shareInfo>(); if (shareinfo != null) {
			 * for (int i = 0; i < shareinfo.length; i++) { if
			 * ("100".equals(shareinfo[i].share_status)) continue; if
			 * ("5".equals(shareinfo[i].share_status)) continue; if
			 * ("9".equals(shareinfo[i].share_status)) continue; if
			 * ("10".equals(shareinfo[i].share_status)) continue; if
			 * ("11".equals(shareinfo[i].share_status)) continue; if
			 * ("12".equals(shareinfo[i].share_status)) continue; boolean
			 * isExisted = false; for (int j = 0; j < infos.size(); j++) { if
			 * (infos.get(j).isSame(shareinfo[i])) { isExisted = true; break; }
			 * } if (!isExisted) infos.add(shareinfo[i]); } if (infos.size() >
			 * 0) return infos.toArray(new shareInfo[infos.size()]); }
			 */
			MyDiary myDiary = DiaryManager.getInstance().findMyDiaryByUUID(
					diaryuuid);
			if (myDiary != null && "2".equals(myDiary.publish_status)) {
				shareInfo info = new shareInfo();
				info.publishid = publishid;
				info.share_time = String
						.valueOf(TimeHelper.getInstance().now());
				info.share_status = "101";
				return new shareInfo[] { info };
			}
			return null;
		}
	}

	public static class MyDiary /* implements Cloneable */{
		public int sync_status;// 0-未同步 1-日记结构已创建 2-上传中 3-上传完成 4-已同步 5-下载中 6-已下载
		public boolean isMicroShare;
		public String join_safebox;// 是否加入保险箱，1是 0否
		public String weather; // 天气
		public String weather_info;// 天气描述
		public String birthday;// 生日
		public String diaryid; // 日记ID
		public String resourcediaryid;// 原日记id
		public String resourceuuid;// 原日记uuid
		public String nickname;
		public String headimageurl;
		public String userid; // 用户id
		public String diaryuuid;
		public String diarytimemilli;// 日记建立毫秒数
		public String updatetimemilli;// 日记修改毫秒数
		public String shoottime;// 日记拍摄时间
		public String diary_source_type;////日记类型 1客户端拍摄、2产品平台、3客户端微享导入 4 官方内容
		public String diary_status;// 0无效（删除） 1新建 2 发布
		public String publish_status; // 1仅自己 2 朋友可见 3站内容公开
		public String diary_type; // 日记类型 1单内容，2，组内容
		public String share_count;// 分享次数
		public String share_mic_count;
		public TAG[] tags; // 选择标签信息
		public String offset;// 偏移量
		public String position_source;// 位置来源，1 GPS 2基站
		public String sex; // 0男，1女， 2未知
		public String position_status;// 1 可见 0 不可见
		public String signature; // 个性签名,base64编码的
		public String shareimageurl;// 图片url，当注附件为视频时：视频封面url,当附件为照片时：照片的url
		public platformUrls[] platformurls;
		public shareInfo[] shareinfo;
		public DiaryAttach attachs;
		public MyActive active;
		public String publishid; // 发布表id
		public createStructureRequest request;

		/*
		 * public String longitude_real; // "经度",//真实内容属性位置
		 * ，operate_diarytype=1或3有效--单词修改 public String latitude_real; //
		 * "维度",//真实内容属性位置，operate_diarytype=1或3有效 public String position_real;
		 * // "北京朝阳区", //真实内容属性位置
		 * 
		 * public String longitude; // "经度", //导入内容位置 public String latitude; //
		 * "维度", //导入内容位置 public String position; // "北京朝阳区", //导入内容位置
		 */
		public String longitude_view; // "经度",
										// //客户端后续展示、编辑使用的位置信息，创建时和真实内容属性位置一致
		public String latitude_view; // "维度", //客户端后续展示、编辑使用的位置信息，创建时和真实内容属性位置一致
		public String position_view; // "北京朝阳区",
										// //客户端后续展示、编辑使用的位置信息，创建时和真实内容属性位置一致

		public MyDiary() {
			// Log.e("zhw", "zhw - MyDiary()");
		}

		public MyDiary(String uuid) {
			// Log.e("zhw", "zhw - MyDiary(uuid) " + uuid);
			SqliteDairyManager sdm = SqliteDairyManager.getInstance();
			MyDiary myDiary = sdm.getDiaryByUUID(uuid);

			// this = myDiary.clone();

			this.active = myDiary.active;
			this.attachs = myDiary.attachs;
			this.birthday = myDiary.birthday;
			this.diary_status = myDiary.diary_status;
			this.diary_type = myDiary.diary_type;
			this.diaryid = myDiary.diaryid;
			this.diarytimemilli = myDiary.diarytimemilli;
			this.diaryuuid = myDiary.diaryuuid;
			this.headimageurl = myDiary.headimageurl;
			this.join_safebox = myDiary.join_safebox;
			this.latitude_view = myDiary.latitude_view;
			this.longitude_view = myDiary.longitude_view;
			this.nickname = myDiary.nickname;
			this.offset = myDiary.offset;
			this.platformurls = myDiary.platformurls;
			this.position_view = myDiary.position_view;
			this.position_source = myDiary.position_source;
			this.position_status = myDiary.position_status;
			this.publish_status = myDiary.publish_status;
			this.request = myDiary.request;

			this.resourcediaryid = myDiary.resourcediaryid;
			this.resourceuuid = myDiary.resourceuuid;
			this.sex = myDiary.sex;
			this.share_count = myDiary.share_count;
			this.share_mic_count = myDiary.share_mic_count;
			this.shareimageurl = myDiary.shareimageurl;
			this.shareinfo = myDiary.shareinfo;
			this.signature = myDiary.signature;
			this.sync_status = myDiary.sync_status;
			this.tags = myDiary.tags;
			this.updatetimemilli = myDiary.updatetimemilli;
			this.userid = myDiary.userid;
			this.weather = myDiary.weather;
			this.weather_info = myDiary.weather_info;
			
			this.shoottime = myDiary.shoottime;
			this.publishid = myDiary.publishid;
			this.diary_source_type = myDiary.diary_source_type;

		}

		/*
		 * public void addShareInfo(shareInfo info){ if(info!=null){
		 * LinkedList<shareInfo> list=new LinkedList<GsonResponse3.shareInfo>();
		 * if(shareinfo!=null&&shareinfo.length>0){
		 * list.addAll(Arrays.asList(shareinfo)); } list.addFirst(info);
		 * shareinfo=list.toArray(new shareInfo[list.size()]); } }
		 */

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

		public void addShareInfo(shareInfo info) {
			if (info != null) {
				LinkedList<shareInfo> list = new LinkedList<GsonResponse3.shareInfo>();
				if (shareinfo != null && shareinfo.length > 0) {
					list.addAll(Arrays.asList(shareinfo));
				}
				list.addFirst(info);
				shareinfo = list.toArray(new shareInfo[list.size()]);
			}
		}

		/**
		 * 判断日记是否已同步完成
		 * 
		 * @return
		 */
		public boolean isSychorized() {
			if (TextUtils.isEmpty(diaryid))
				return false;
			AuxAttach[] auxAttachs = getAssistAttach();
			if (auxAttachs != null && auxAttachs.length > 0) {// 辅内容上传完成时，表示所有内容全部同步完
				if (auxAttachs[0] != null
						&& !"4".equals(auxAttachs[0].attachtype)
						&& !TextUtils.isEmpty(auxAttachs[0].attachurl)
						&& !auxAttachs[0].attachurl.startsWith("http"))
					return false;
				return true;
			} else {// 无辅内容时，主内容为文字或url为http地址时，表示内容同步上传完
				if ("4".equals(getDiaryMainType()))
					return true;
				if ((attachs.levelattach.attachurl != null && attachs.levelattach.attachurl
						.startsWith("http")))
					return true;
			}
			return false;
		}

		@Override
		public String toString() {
			SqliteDairyManager sdm = SqliteDairyManager.getInstance();
			sdm.putDiary(this);
			return diaryuuid;
		}

		public boolean isModified() {
			if (!"".equals(ZStringUtils.nullToEmpty(resourceuuid))
					&& diaryuuid.equals(ZStringUtils.nullToEmpty(resourceuuid))) {
				return true;
			}
			return false;
		}

		public boolean isMiShareDiary() {
			return isMicroShare;
		}

		public boolean isDuplicated() {
			if (!"".equals(ZStringUtils.nullToEmpty(resourceuuid))
					&& !diaryuuid
							.equals(ZStringUtils.nullToEmpty(resourceuuid))) {
				return true;
			}
			return false;
		}

		public void setDiaryTag(String tagsString) {
			ArrayList<TAG> tagList = new ArrayList<TAG>();

			if (!TextUtils.isEmpty(tagsString)) {
				String[] tagIdsList = tagsString.split(",");
				DiaryManager diarymanager = DiaryManager.getInstance();
				List<taglistItem> tagStrList = diarymanager.getTags();

				for (String id : tagIdsList) {
					for (taglistItem item : tagStrList) {
						if (id.equals(item.id)) {
							TAG tag = new TAG();
							tag.id = item.id;
							tag.name = item.name;
							tagList.add(tag);
						}
					}
				}
			}

			if (tagList.size() != 0) {
				tags = tagList.toArray(new TAG[tagList.size()]);
			}
		}

		public boolean isCreated() {
			return sync_status > GsonProtocol.DIARY_SYNC_STATUS_NOT_SYNC;
		}

		public boolean isVideoDiary() {
			String type = getDiaryMainType();
			return "1".equals(type);
		}

		public boolean isPicDiary() {
			String type = getDiaryMainType();
			return "3".equals(type);
		}

		public String getTagIds() {
			String tagIds = "";
			if (tags != null) {
				for (int i = 0; i < tags.length; i++) {
					tagIds += tags[i].id;
					if (i != tags.length - 1) {
						tagIds += ",";
					}
				}
			}
			return tagIds;
		}

		public int getTagSize() {
			if (tags != null) {
				return tags.length;
			}
			return 0;
		}

		public void setTags(String tags) {

		}

		/**
		 * 替换日记映射关系
		 */
		public void replaceMediaMapping(MyDiary myDiary) {
			if (null == myDiary)
				return;
			String myMainUrl = getMainUrl();
			String otherMainUrl = myDiary.getMainUrl();

			String myVideoCover = attachs.videocover;
			String otherVideoCover = myDiary.attachs.videocover;

			if (!TextUtils.isEmpty(myVideoCover)
					&& !TextUtils.isEmpty(otherVideoCover)) {
				relpaceMediaMapping(myVideoCover, otherVideoCover, true);// 此处创建一个缩略图映射
				if (!myVideoCover.startsWith("http")
						&& "1".equals(getDiaryMainType())) {
					String urlSmall = getSmallUrl(otherVideoCover);
					if (!TextUtils.isEmpty(urlSmall))
						relpaceMediaMapping(otherVideoCover, urlSmall, false);
				}
			}

			if (!TextUtils.isEmpty(myMainUrl)
					&& !TextUtils.isEmpty(otherMainUrl)) {
				relpaceMediaMapping(myMainUrl, otherMainUrl, true);
				// 如果myMainUrl是非http路径且otherMainUrl是http路径时且是图片类型时，建立本地缩略图映射
				if (!myMainUrl.startsWith("http")
						&& "3".equals(getDiaryMainType())) {
					String urlSmall = getSmallUrl(otherMainUrl);
					if (!TextUtils.isEmpty(urlSmall))
						relpaceMediaMapping(otherMainUrl, urlSmall, false);
				}
			}

			AuxAttach[] auxAttachs = getAssistAttach();
			if (null == auxAttachs)
				return;
			for (int i = 0; i < auxAttachs.length; i++) {
				AuxAttach myAuxAttach = auxAttachs[i];
				AuxAttach otherAuxAttach = myDiary
						.getAuxAttachByUUID(myAuxAttach.attachuuid);
				if (!TextUtils.isEmpty(myAuxAttach.attachurl)
						&& otherAuxAttach != null
						&& !TextUtils.isEmpty(otherAuxAttach.attachurl)) {
					relpaceMediaMapping(myAuxAttach.attachurl,
							otherAuxAttach.attachurl, true);
				}
			}
		}

		/**
		 * 账户合并时替换映射关系
		 * 
		 * @param oldAI
		 * @param newAI
		 */
		public void replaceMediaMapping(AccountInfo oldAI, AccountInfo newAI) {
			String myMainUrl = getMainUrl();

			String myVideoCover = attachs.videocover;

			if (!TextUtils.isEmpty(myVideoCover)) {
				relpaceMediaMapping(myVideoCover, oldAI, newAI);
			}

			if (!TextUtils.isEmpty(myMainUrl)) {
				relpaceMediaMapping(myMainUrl, oldAI, newAI);
			}

			AuxAttach[] auxAttachs = getAssistAttach();
			if (null == auxAttachs)
				return;
			for (int i = 0; i < auxAttachs.length; i++) {
				AuxAttach myAuxAttach = auxAttachs[i];
				if (!TextUtils.isEmpty(myAuxAttach.attachurl)) {
					relpaceMediaMapping(myAuxAttach.attachurl, oldAI, newAI);
				}
			}
		}

		// 根据辅附件uuid查找辅附件
		public AuxAttach getAuxAttachByUUID(String uuid) {
			AuxAttach[] auxAttachs = getAssistAttach();
			if (auxAttachs != null) {
				for (int i = 0; i < auxAttachs.length; i++) {
					AuxAttach auxAttach = auxAttachs[i];
					if (uuid != null && uuid.equals(auxAttach.attachuuid))
						return auxAttach;
				}
			}
			return null;
		}

		public AuxAttach getAuxAttach() {
			AuxAttach[] auxAttachs = getAssistAttach();
			if (auxAttachs != null) {
				if (auxAttachs.length > 0) {
					return auxAttachs[0];
				}
			}
			return null;
		}

		public String getAuxAttachPath() {
			AuxAttach attach = getAuxAttach();
			if (attach == null) {
				return null;
			}
			return getAuxAttachPath(attach.attachuuid);
		}

		// 根据辅附件uuid查找附件路径
		public String getAuxAttachPath(String uuid) {
			AuxAttach auxAttach = getAuxAttachByUUID(uuid);
			String userID = ActiveAccount.getInstance(
					MainApplication.getAppInstance()).getUID();

			if (auxAttach == null) {
				return null;
			}
			String key = null;
			MediaValue mediaValue = null;
			if (ZStringUtils.emptyToNull(auxAttach.attachurl) != null) {
				key = auxAttach.attachurl;
			} else {
				key = auxAttach.attachuuid;
			}
			mediaValue = AccountInfo.getInstance(userID).mediamapping.getMedia(
					userID, key);

			if (mediaValue != null) {
				return Environment.getExternalStorageDirectory()
						+ mediaValue.localpath;
			}
			return null;
		}

		// 获得辅附件
		private AuxAttach[] getAssistAttach() {
			if (attachs != null && attachs.attach != null)
				return attachs.attach;
			return null;
		}

		/**
		 * 获取辅附件文字描述
		 * 
		 * @return
		 */
		public String getAssistAttachTexTContent() {
			AuxAttach[] attachs = getAssistAttach();
			if (attachs != null && attachs.length > 0)
				return attachs[0].content;
			return "";
		}

		private String getAssisstAttachUrl() {
			if (attachs != null && attachs.attach != null
					&& attachs.attach.length > 0 && attachs.attach[0] != null
					&& !TextUtils.isEmpty(attachs.attach[0].attachurl))
				return attachs.attach[0].attachurl;
			return null;

		}

		// 替换原有映射关系
		public static void relpaceMediaMapping(String original, String replace,
				boolean isDelOriginal) {
			if (original != null && replace != null
					&& !original.equals(replace)) {
				String uid = ActiveAccount.getInstance(
						MainApplication.getAppInstance()).getUID();
				if (uid != null) {
					MediaValue mv = AccountInfo.getInstance(uid).mediamapping
							.getMedia(uid, original);
					if (mv != null) {
						mv.url = replace;
						// 私信页可能还会用到原有映射关系
						if (isDelOriginal)
							AccountInfo.getInstance(uid).mediamapping.delMedia(
									uid, original);
						AccountInfo.getInstance(uid).mediamapping.setMedia(uid,
								replace, mv);
					}
				}
			} else {
				Log.d("mydiary",
						"relpaceMediaMapping original==replace don't replace");
			}
		}

		/**
		 * 账号合并时用新UID替换旧UID
		 * 
		 * @param uri
		 * @param oldUID
		 * @param newUID
		 */
		public static void relpaceMediaMapping(String uri, AccountInfo oldAI,
				AccountInfo newAI) {
			try {
				if (oldAI != null && !TextUtils.isEmpty(uri) && newAI != null) {
					// 找旧UID中的mediavalue
					MediaValue mv = oldAI.mediamapping.getMedia(oldAI.userid,
							uri);
					if (mv != null) {
						// 删除原映射
						oldAI.mediamapping.delMedia(oldAI.userid, uri);
						// 替换userid
						mv.UID = newAI.userid;
						// 替换本地路径
						if (!TextUtils.isEmpty(mv.localpath)) {
							String oldLocalpath = mv.localpath;
							// 替换userid目录名
							String newLocalpath = mv.localpath.replace(
									oldAI.userid, newAI.userid);
							// 替换文件名
							String oldFileName = MD5
									.encode((oldAI.userid + uri).getBytes());
							String newFileName = MD5
									.encode((newAI.userid + uri).getBytes());
							newLocalpath = newLocalpath.replace(oldFileName,
									newFileName);
							// 移动文件
							MediaMapping
									.renameFile(LookLookActivity.SDCARD_PATH
											+ oldLocalpath,
											LookLookActivity.SDCARD_PATH
													+ newLocalpath);
							mv.localpath = newLocalpath;
						}
						newAI.mediamapping.setMedia(newAI.userid, uri, mv);
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			/*
			 * if(original!=null&&replace!=null&&!original.equals(replace)){
			 * String uid = ActiveAccount.getInstance(
			 * MainApplication.getAppInstance()).getUID(); if (uid != null) {
			 * MediaValue mv = AccountInfo.getInstance(uid).mediamapping
			 * .getMedia(uid, original); if (mv != null) { mv.url=replace; //
			 * 私信页可能还会用到原有映射关系
			 * AccountInfo.getInstance(uid).mediamapping.delMedia(uid,
			 * original);
			 * AccountInfo.getInstance(uid).mediamapping.setMedia(uid, replace,
			 * mv); } } }else{ Log.d("mydiary",
			 * "relpaceMediaMapping original==replace don't replace"); }
			 */
		}

		/**
		 * 获取主附件文字 如果主附件类型与文字无关，则返回null
		 */
		public String getMainTextContent() {
			if (attachs != null && attachs.levelattach != null) {
				return attachs.levelattach.content;
			}
			return null;
		}

		/**
		 * 获取日记主附件类型 附件类型，1视频、2音频、3图片、4文字、5 短录音、6(短录音+文字)
		 */
		public String getDiaryMainType() {
			if (attachs != null && attachs.levelattach != null) {
				return attachs.levelattach.attachtype;
			}
			return null;
		}

		public boolean isNoteDiary() {
			String type = getDiaryMainType();
			return "4".equals(type) || "5".equals(type) || "6".equals(type);
		}

		/**
		 * 获取封面
		 */
		public String getVideoCoverUrl() {
			if (attachs != null && attachs.levelattach != null)
				return attachs.videocover;
			return null;
		}

		/**
		 * 获取视频封面（缩略图）
		 */
		public String getVideoCoverUrl_s() {
			String coverUrl = getVideoCoverUrl();
			 if("1".equals(getDiaryMainType())){
			 coverUrl=getSmallUrl(coverUrl);
			 }
			return coverUrl;
		}

		/**
		 * 主附件为图片时，获取缩略图地址
		 * 
		 * @return
		 */
		public String getMainUrl_s() {
			String url = getMainUrl();
			 if("3".equals(getDiaryMainType())){
			 url=getSmallUrl(url);
			 }
			return url;
		}

		// 获取缩略图url
		private String getSmallUrl(String url) {
			if (!TextUtils.isEmpty(url) && url.startsWith("http")
					/*&& url.endsWith(".jpg")*/)
				 return url.replace(".jpg", "_s.jpg");
			return url;
		}

		public void setVideoCoverUrl(String videoCover) {
			if (attachs != null) {
				attachs.videocover = videoCover;
			}
		}

		/**
		 * 获取封面路径
		 */
		public String getVideoCoverPath() {
			String url = getVideoCoverUrl();
			String userID = ActiveAccount.getInstance(
					MainApplication.getAppInstance()).getUID();
			if (url == null) {
				return null;
			}
			MediaValue mediaValue = AccountInfo.getInstance(userID).mediamapping
					.getMedia(userID, url);

			if (mediaValue != null) {
				return Environment.getExternalStorageDirectory()
						+ mediaValue.localpath;
			}

			return null;
		}

		/**
		 * 获取主内容播放时长
		 */
		public String getMainPlaytime() {
			if (attachs != null && attachs.levelattach != null)
				return attachs.levelattach.playtime;
			return null;
		}

		/**
		 * 获取主附件url 当附件为图片时，如果是自己的日记，返回无标图片，如果是别人日记，返回有标图标
		 */
		public String getMainUrl() {
			if (attachs != null && attachs.levelattach != null) {
				// if("3".equals(getDiaryMainType())){//自己的日记+图片
				// return attachs.levelattach.attachurl_pic;
				// }else{
				// }
				return attachs.levelattach.attachurl;
			}
			return "";
		}

		public void setMainUrl(String mainUrl) {
			if (attachs != null && attachs.levelattach != null) {
				attachs.levelattach.attachurl = mainUrl;
			}
		}

		public String getMainHsmUrl() {
			String url = "";

			if (attachs != null && attachs.levelattach != null
					&& !TextUtils.isEmpty(attachs.levelattach.attachurl_hsm)) {
				url = attachs.levelattach.attachurl_hsm;
			}

			return url;
		}

		/**
		 * 修改主附件
		 * 
		 * @param filePath
		 * @param content
		 */
		public void modifyMainAttach(String filePath, String content) {
			if (attachs != null && attachs.levelattach != null) {
				MainAttach mainAttach = attachs.levelattach;
				mainAttach.attachuuid = DiaryController.getNextUUID();
				if ("3".equals(getDiaryMainType())) {
					mainAttach.attachurl = "file://" + filePath;
				} else {
					mainAttach.attachurl = "file://" + filePath;
					if (filePath != null) {
						double total = new Mp4Info(filePath).totaltime;
						if (total < 1.0) {
							total = 1.0;
						}
						int playtime = (int) total;
						mainAttach.playtime = "" + playtime;
					}
				}

				if (!"".equals(ZStringUtils.nullToEmpty(content))) {
					if ("5".equals(attachs.levelattach.attachtype)) {
						attachs.levelattach.attachtype = GsonProtocol.ATTACH_TYPE_VOICE_TEXT;
					}
				} else {
					if ("6".equals(attachs.levelattach.attachtype)) {
						attachs.levelattach.attachtype = GsonProtocol.ATTACH_TYPE_VOICE;
					}
				}
				mainAttach.content = content;

				if (ZStringUtils.emptyToNull(filePath) != null) {
					DiaryController.saveToMediaMapping(getDiaryMainType(),
							filePath, mainAttach.attachurl);
				}
			}
		}

		/**
		 * 修改视频封面
		 */
		public void modifyVideoCover(String coverPath) {
			// String relativePath = DiaryController.getRelativePath(coverPath);
			if (attachs != null) {
				attachs.videocover = "file://" + coverPath;
				DiaryController.saveToMediaMapping(
						GsonProtocol.ATTACH_TYPE_PICTURE, coverPath,
						attachs.videocover);
			}
		}

		/**
		 * 获取主附件文件的绝对路径
		 * 
		 * @return
		 */
		public String getMainPath() {
			String userID = ActiveAccount.getInstance(
					ZApplication.getInstance()).getUID();
			String key = getMainUrl();

			Log.e("mydiary", "getMainUrl = " + key);

			MediaValue mediaValue = null;
			MainAttach mainAttach = null;

			if (attachs != null && attachs.levelattach != null) {
				mainAttach = attachs.levelattach;
			}
			if (ZStringUtils.emptyToNull(key) == null) {
				if (mainAttach != null) {
					key = mainAttach.attachuuid;
				} else {
					return null;
				}
			}
			mediaValue = AccountInfo.getInstance(userID).mediamapping.getMedia(
					userID, key);

			if (mediaValue != null) {
				Log.e("mydiary",
						"local path = "
								+ Environment.getExternalStorageDirectory()
								+ mediaValue.localpath);
				return Environment.getExternalStorageDirectory()
						+ mediaValue.localpath;
			}
			return null;
		}

		public String getDiaryDate() {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd");
			return sdf.format(new Date(Long.parseLong(updatetimemilli)));
		}

		// 获取图片url
		private String getImageUrlForCountSize() {
			return null;
		}

		public String getDiarySize() {
			String userID = ActiveAccount.getInstance(
					ZApplication.getInstance()).getUID();
			int diarySize = 0;
			// 主附件大小
			String mainUrl = getMainUrl();
			if (mainUrl != null) {
				MediaValue mediaValue = AccountInfo.getInstance(userID).mediamapping
						.getMedia(userID, mainUrl);
				if (mediaValue != null) {
					diarySize += mediaValue.realSize;
				}
			}
			// 辅附件大小
			String AuxUrl = getAssisstAttachUrl();
			if (AuxUrl != null) {
				MediaValue mediaValue = AccountInfo.getInstance(userID).mediamapping
						.getMedia(userID, AuxUrl);
				if (mediaValue != null) {
					diarySize += mediaValue.realSize;
				}
			}
			// 封面大小
			String videoCoverUrl = getVideoCoverUrl();
			if (videoCoverUrl != null) {
				MediaValue mediaValue = AccountInfo.getInstance(userID).mediamapping
						.getMedia(userID, videoCoverUrl);
				if (mediaValue != null) {
					diarySize += mediaValue.realSize;
				}
			}

			return String.valueOf(diarySize);
		}

		/*
		 * public String getDiarySize() { String userID =
		 * ActiveAccount.getInstance( ZApplication.getInstance()).getUID(); int
		 * diarySize = 0; String videoUrl = getVideoUrl(); if (videoUrl != null)
		 * { MediaValue mediaValue =
		 * AccountInfo.getInstance(userID).mediamapping .getMedia(userID,
		 * videoUrl); if (mediaValue != null) { diarySize +=
		 * mediaValue.realSize; } }
		 * 
		 * String longRecUrl = getLongRecUrl(); if (longRecUrl != null) {
		 * MediaValue mediaValue = AccountInfo.getInstance(userID).mediamapping
		 * .getMedia(userID, longRecUrl); if (mediaValue != null) { diarySize +=
		 * mediaValue.realSize; } }
		 * 
		 * String imageUrl = getImageUrlForCountSize();
		 * System.out.println("====diary imageurl====" + imageUrl); if (imageUrl
		 * != null) { MediaValue mediaValue =
		 * AccountInfo.getInstance(userID).mediamapping .getMedia(userID,
		 * imageUrl); if (mediaValue != null) { diarySize +=
		 * mediaValue.realSize; } }
		 * 
		 * String shortRecUrl = getShortRecUrl(); if (shortRecUrl != null) {
		 * MediaValue mediaValue = AccountInfo.getInstance(userID).mediamapping
		 * .getMedia(userID, shortRecUrl); if (mediaValue != null) { diarySize
		 * += mediaValue.realSize; } }
		 * 
		 * 
		 * String videoCoverUrl = getVideoCoverUrl(); if (videoCoverUrl != null)
		 * { MediaValue mediaValue =
		 * AccountInfo.getInstance(userID).mediamapping.getMedia(userID,
		 * videoCoverUrl); if (mediaValue != null) { diarySize +=
		 * mediaValue.realSize; } }
		 * 
		 * 
		 * return String.valueOf(diarySize); // if (size == null ||
		 * "".equals(size)) { // // } else { // return "0"; // }
		 * 
		 * }
		 */

		/**
		 * 清除日记物理文件
		 */
		public void clear() {
			String userID = ActiveAccount.getInstance(
					ZApplication.getInstance()).getUID();
			String videoUrl = getVideoCoverUrl();
			if (videoUrl != null) {
				AccountInfo.getInstance(userID).mediamapping.delMedia(userID,
						videoUrl, true);
			}

			String mainUrl = getMainUrl();
			if (mainUrl != null) {
				AccountInfo.getInstance(userID).mediamapping.delMedia(userID,
						mainUrl, true);
			}

			String assisstAttachUrl = getAssisstAttachUrl();
			if (assisstAttachUrl != null) {
				AccountInfo.getInstance(userID).mediamapping.delMedia(userID,
						assisstAttachUrl, true);
			}
			sync_status = 4;// 物理文件删除后,sync_status变成已同步
		}
	}

	public static class TAG {
		public String name; // ”标签1”
		public String id; //
	}

	public static class taglistItem {
		public String id;
		public String name; // 娱乐
		public String checked; // 1:被选中，0：未被选中
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
	}

	public static class DiaryAttach {
		public MainAttach levelattach;// 主附件
		public AuxAttach[] attach;// 辅附件
		public String videocover;
		public String pic_width;
		public String pic_height;
		public String show_width;
		public String show_height;
	}

	// 主附件
	public static class MainAttach {
		public String attachid;
		public String attachtype;// 附件类型，1视频、2音频、3图片、4文字、5短录音、6文字+短录音
		public String attachuuid;
		public String attachurl; // 1. uuid, 2.2014_03_14_xxx.jpg 3.
									// http://125.xxx.xxx.xx/cover/xxx.jpg
		public String attachurl_hsm;
		// public String attachurl_pic;//无标图片
		public String attachsize;
		public String subattachid; // 具体子表的附件ID, 提供给WWW;
		public String playtime;
		public String attachtimemilli;
		public String playtimes;
		public String content;// 内容
		public String video_angle; // 视频角度
	}

	// 辅附件
	public static class AuxAttach {
		public String attachid;
		public String attachtype;// 附件类型，1视频、2音频、3图片、4文字
		public String attachuuid;
		public String attachurl;
		public String attachurl_hsm;
		public String attachsize;
		public String subattachid;
		public String playtime;
		public String attachtimemilli;
		public String playtimes;
		public String content;// 内容
	}

	public class shareImageUrl {
		public String imagetype; // 照片,视频
		public String playtime; // 如果是音频短语音便签、
		public String content; // 文字
		public String imageurl; // url

	}

	public class platformUrls {
		public String snstype; // 社区类型
		public String url; // 链接地址
	}

	public static class shareInfo {
		public String publishid;// 分享ID
		public String share_time;// 分享时间
		public String share_status;// 分享类型 (1-12为第三方分享)1新浪 2人人 5 qzone空间 6腾讯
									// 9微信朋友圈 10短信 11邮箱 12微信好友 100站内公开 101朋友圈
									// 103微享

		public boolean isSame(shareInfo info) {
			if (null == info)
				return false;
			if (DateUtils.isNum(share_status)
					&& DateUtils.isNum(info.share_status)) {
				int status = Integer.parseInt(share_status);
				int targetStatus = Integer.parseInt(info.share_status);
				if (status == targetStatus)
					return true;
				if (status <= 12 && targetStatus <= 12)
					return true;
				return false;
			}
			Log.e("shareInfo", "share_status error " + share_status + " "
					+ info.share_status);
			return false;
		}

	}

	// 分享详情头像
	public static class EnjoyHead {
		public String userid; // "用户ID"
		public String headimageurl; // "头像链接地址"
	}

	/*
	 * public static class DiaryDetailEnjoy{ public String userid; //用户ID public
	 * String headimageurl; //"头像链接地址" public String nickname; //昵称 public
	 * String sex; // 0男，1女， 2未知
	 * 
	 * }
	 */

	public static class DiaryDetailComment {
		public String publishid; // 分享ID
		public String diaryid;
		public String userid; // 评论用户ID
		public String headimageurl; // 头像URL
		public String nickname; // 昵称
		public String commentcontent; // 评论
		public String commentid; // 评论ID，当前评论id
		public String commentuuid; // 评论UUID，当前评论uuid
		public String createtime; // 时间戳
		public String signature; // 是编码的，需要解码
		public String sex; // 0男，1女， 2未知
		public String isattention; // 0 未关注 1是已关注
		public String audiourl; // 语音地址
		public String playtime; // 语音播放时长
		public String commentway; // 评论方式1、文字 2、声音3、声音加文字
		public String commenttype; // 评论类型：1、评论 2回复
		public String replynickname; // 被回复人昵称
		public String replymarkname; // 被回复人的备注名"
		public String nickmarkname; // 备注名
	}

	public static class DiaryDetailCommentList {
		public String publishid; // 分享ID
		public String share_time; // 分享时间
		public String share_status; // 分享类型
		public String comment_count; // 评论条数
		public String snsid; // 用户第三方账户的ID
		public String weiboid; // 微博ID
		public DiaryDetailComment[] comments;
	}

	public static class DiaryRelation {
		public String userid; // userID
		public String diaryid; // 日记ID
		public String diaryuuid; // 日记UUID
		public String join_safebox; // 是否加入保险箱，1是 0否
		public String isgroup; // 是否是组日记
		public String create_time; // 创建时间
		public String update_time; // 最后更新时间
		public String contain; // 1,2,3,4,9"， //组内包含的日记
	}

	/*
	 * //附录5 日记详情 public static class DiaryDetail{ public String sharecontent;
	 * //"分享内容文字" public DiaryRelation diaryid; //日记列表 public MyDiary[] diaries;
	 * //详情请看附录1 public DiaryDetailEnjoy[] enjoyheadurl; //赞人头像列表 public
	 * DiaryDetailCommentList[] commentlist; //评论列表 }
	 */

	// 附录2 他人空间列表,分享列表
	public static class OtherZoneDiaryIds {
		public String publishid; // 分享ID，评论 收藏 赞动作都是对这个id而言
		public String sharecontent; // "分享内容文字"
		public String userid; // "分享的userid"
		public String diaryid; // 日记ID，如果是组，为组id
		public String isgroup; // "日记类型 0单内容，1，组内容
		public String create_time; // 创建时间
		public String contain; // 组内包含的日记
	}

	// 68. 设置空间背景图片
	public static class setUserSpaceCoverResponse {
		public String status; // 0成功
		public String crm_status;
		public String imageurl; // ”http://iamge.looklook.cn/201212/03/543213609KJH76.jpg”
	}

	public class uploadPictrue {
		public String status;
		public String imageurl;
	}

	// 心跳push结构
	public class HeartPush {
		public String type;
		public String public_id;
		public String title;
		public String content;
		public String nick_name;

		// 微享详情页需要
		public String is_encrypt; // 微享是否已放入保险箱
		public String micuserid; // 微享创建人id
		// public String mic_users; // 微享用户所有人员
		public UserObj[] userobj;

		public String is_undisturb; // 免打扰 1 是， 0否

	}

	public static class configInfoResponse {
		public String status;
		public ConfigHeart[] heart;
		public ConfigPromptMsg[] promptmsg;
	}

	public class ConfigHeart {
		public String starttime; // 起始时间,单位分钟
		public String endtime; // 结束时间,单位分钟
		public String interval; // 单位秒
		public String interval_wifi; // 单位秒
		public String remind_type; // 提醒类型，1有角标、有声音 2仅角标
	}

	public class ConfigPromptMsg {
		public String type; // 提示消息类型
		public String msg; // 提示消息
	}

}