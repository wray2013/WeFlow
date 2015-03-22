package com.etoc.weflow.net;

public class GsonRequestObject {

	public static class testRequest {
		String mac; // 手机的mac地址
		String imei; // 手机imei（手机的唯一标识）
	}
	//example:
/*	public static class configInfo{
		String userid; // "9876543212345678",
		String os; //客户端系统类型 1IOS 2Andriod
		String mac; // 手机的mac地址
		String imei; // 手机imei（手机的唯一标识）
	}
	
	public static class sendSMSRequest {
		String phone;  //用户号码
//		String mac; // 手机的mac地址
//		String imei; // 手机imei（手机的唯一标识）
		String channelid;//渠道ID
		String transid;// 交易流水
	}
	
	public static class loginRequest {
		String phone;  //用户号码
//		String mac; // 手机的mac地址
//		String imei; // 手机imei（手机的唯一标识）
		String channelid;//渠道ID
		String transid;// 交易流水
		String weixinid;//微信号
		String authcode;//验证码
	}
	
	public static class getAccInfoRequest {
		String uuid;
		String mac; // 手机的mac地址
		String imei; // 手机imei（手机的唯一标识）
	}
	
	public static class getAdvInfoRequest {
		String uuid;
		String mac; // 手机的mac地址
		String imei; // 手机imei（手机的唯一标识）
	}
	
	public static class orderLargessRequest {
		String channelid;//渠道ID
		String transid;// 交易流水
		String phone;// 用户手机号
		String productid;//产品id
		String opertype;//操作类型 N：订购 D : 退订  C: 充值
	}
	
	public static class getAwardInfoRequest {
		String uuid;
		String mac; // 手机的mac地址
		String imei; // 手机imei（手机的唯一标识）
	}
	
	public static class lotteryRequest {
		String phone;  //用户号码
		String channelid;//渠道ID
		String transid;// 交易流水
		String productid;//产品ID
		String opertype;//操作类型
//		String mac; // 手机的mac地址
//		String imei; // 手机imei（手机的唯一标识）
	}*/
	
	
	/****************************************************
	 *                      登录/注册
	 ****************************************************/
	//2.1.1 获取验证码
	public static class getAuthCodeRequest {
		String tel;  //用户号码
		String type; //验证码类型 1 注册 2修改密码
		String mac; // 手机的mac地址
		String imei; // 手机imei（手机的唯一标识）
	}
	
	//2.1.2 用户注册
	public static class registerRequest {
		String tel;  //用户号码
		String pwd; //密码，加密MD5
		String mac; // 手机的mac地址
		String imei; // 手机imei（手机的唯一标识）
	}
	
	//2.1.3 验证码验证
	public static class verifyAuthCodeRequest {
		String tel;  //用户号码
		String authcode; //验证码
		String type; //验证码类型 1 注册 2修改密码
		String mac; // 手机的mac地址
		String imei; // 手机imei（手机的唯一标识）
	}
	
	//2.1.4 用户登录
	public static class loginRequest {
		String tel;  //用户号码
		String pwd; //密码
		String mac; // 手机的mac地址
		String imei; // 手机imei（手机的唯一标识）
	}
	
	//2.1.5 自动登录
	public static class autoLoginRequest {
		String userid;  //用户号码
		String mac; // 手机的mac地址
		String imei; // 手机imei（手机的唯一标识）
	}
	
	//2.1.6 重设密码
	public static class resetPasswordRequest {
		String tel;  //用户号码
		String newpassword; //密码
		String mac; // 手机的mac地址
		String imei; // 手机imei（手机的唯一标识）
	}
	
	//2.2.1 账户基本信息查询
	public static class accountInfoRequest {
		String userid;  //用户号码
		String mac; // 手机的mac地址
		String imei; // 手机imei（手机的唯一标识）
	}
	
	//2.2.2 活动推荐
	public static class recommendActRequest {
		String userid;  //用户号码
		String pageno;  //分页
		String mac; // 手机的mac地址
		String imei; // 手机imei（手机的唯一标识）
	}
	
	/****************************************************
	 *                      A.赚流量币
	 ****************************************************/
	
	//2.3.1 看广告首页列表
	public static class advHomeRequest {
		String mac; // 手机的mac地址
		String imei; // 手机imei（手机的唯一标识）
	}
	
	//2.3.2 精彩广告列表(上拉加载更多精彩广告)
	public static class advMoreRequest {
		String pageno;  //分页
		String mac; // 手机的mac地址
		String imei; // 手机imei（手机的唯一标识）
	}
	
	//2.3.4 看广告赚流量币
	public static class advFlowRequest {
		String userid;  //用户号码
		String productid; //广告id
		String mac; // 手机的mac地址
		String imei; // 手机imei（手机的唯一标识）
	}
	
	//2.3.5 获取广告赚取流量币记录
	public static class advFlowRecordRequest {
		String userid;  //用户号码
		String mac; // 手机的mac地址
		String imei; // 手机imei（手机的唯一标识）
		String page;
	}
	
	//2.4.1 下载软件首页
	public static class appHomeRequest {
		String mac; // 手机的mac地址
		String imei; // 手机imei（手机的唯一标识）
	}
	
	//2.4.2 下载软件列表(上拉加载更多)
	public static class appListRequest {
		String pageno;  //分页
		String mac; // 手机的mac地址
		String imei; // 手机imei（手机的唯一标识）
	}
	
	//2.4.4 下载软件赚流量币
	public static class appFlowRequest {
		String userid;  //用户号码
		String productid; //软件id，后台决定是填productid还是多一层索引
		String flowtype;  //赚币类型 0-下载 1-分享
		String mac; // 手机的mac地址
		String imei; // 手机imei（手机的唯一标识）
	}
	
	//2.4.5 获取下载软件赚取流量币记录
	public static class appFlowRecordRequest {
		String userid;  //用户号码
		String appid; //软件id
		String mac; // 手机的mac地址
		String imei; // 手机imei（手机的唯一标识）
		String page;
	}
	
	//2.5.1 获取奖品列表
	public static class awardListRequest {
		String awardway;  //获奖途径 1 摇一摇 2刮刮卡
		String mac; // 手机的mac地址
		String imei; // 手机imei（手机的唯一标识）
	}
	
	//2.5.2 摇一摇赚取流量币
	public static class shakeFlowRequest {
		String userid;  //用户号码
		String mac; // 手机的mac地址
		String imei; // 手机imei（手机的唯一标识）
	}
	
	//2.5.3 刮刮卡赚取流量币
	public static class scratchFlowRequest {
		String userid;  //用户号码
		String mac; // 手机的mac地址
		String imei; // 手机imei（手机的唯一标识）
	}
	
	//2.5.4 玩游戏赚取流量币记录
	public static class awardRecordRequest {
		String userid;  //用户号码
		String mac; // 手机的mac地址
		String imei; // 手机imei（手机的唯一标识）
		String page;
	}
	
	/****************************************************
	 *                      B.花流量币
	 ****************************************************/
	//2.6.1 获取话费充值列表
	public static class phoneChargeListRequest {
		String mac; // 手机的mac地址
		String imei; // 手机imei（手机的唯一标识）
	}
	
	//2.6.2 话费充值
	public static class rechargePhoneRequest {
		String userid;  //用户号码
		String acctid;  //用户号码
		String productid;  //产品id 10元话费
		String mac; // 手机的mac地址
		String imei; // 手机imei（手机的唯一标识）
	}
	
	//2.6.4 Q币充值列表
	public static class QChargeListRequest {
		String mac; // 手机的mac地址
		String imei; // 手机imei（手机的唯一标识）
	}
	
	//2.6.5 Q币充值
	public static class rechargeQQRequest {
		String userid;  //用户号码
		String acctid;  //QQ号
		String productid;  //产品id 10元话费
		String mac; // 手机的mac地址
		String imei; // 手机imei（手机的唯一标识）
	}
	
	//2.6.6 获取游戏礼包兑换列表
	public static class gamePkgListRequest {
		String mac; // 手机的mac地址
		String imei; // 手机imei（手机的唯一标识）
	}
	
	//2.6.7 游戏礼包兑换
	public static class exchangeGamePkgRequest {
		String userid;  //用户号码
		String gamepkgid;  //游戏礼包id
		String mac; // 手机的mac地址
		String imei; // 手机imei（手机的唯一标识）
	}
	
	//2.6.8 游戏充值列表
	public static class GameChargeListRequest {
		String mac; // 手机的mac地址
		String imei; // 手机imei（手机的唯一标识）
	}
	
	//2.6.9 游戏充值
	public static class GameRechargeRequest {
		String userid;  //用户号码
		String acctid;  //游戏礼包id
		String mac; // 手机的mac地址
		String imei; // 手机imei（手机的唯一标识）
	}
	
	//2.6.10 获取流量包列表
	public static class flowPkgListRequest {
		String type; //流量包类型 1 流量包 2 夜间包 3 定向包
		String mac; // 手机的mac地址
		String imei; // 手机imei（手机的唯一标识）
	}
		
	//2.6.11 兑换流量包
	public static class exchangeFlowPkgRequest {
		String userid;  //用户号码
		String flowpkgid;  //流量包id
		String mac; // 手机的mac地址
		String imei; // 手机imei（手机的唯一标识）
	}
	
	//2.6.12 获取礼券兑换列表
	public static class giftListRequest {
		String mac; // 手机的mac地址
		String imei; // 手机imei（手机的唯一标识）
	}
	
	//2.6.13 兑换礼券
	public static class exchangeGiftRequest {
		String userid;  //用户号码
		String giftid;  //礼券id
		String mac; // 手机的mac地址
		String imei; // 手机imei（手机的唯一标识）
	}
	
	//2.6.14 花流量币记录
	public static class costFlowRecordRequest {
		String userid;  //用户号码
		String type;  //消费类型
		String mac; // 手机的mac地址
		String imei; // 手机imei（手机的唯一标识）
		String pageno;
	}
	
	/****************************************************
	 *                      C.流量银行
	 ****************************************************/
	//2.7.1 查询流量银行
	public static class queryBankRequest {
		String userid;  //用户号码
		String mac; // 手机的mac地址
		String imei; // 手机imei（手机的唯一标识）
	}
	
	//2.7.2 存流量币
	public static class storeFlowRequest {
		String userid;  //用户号码
		String flowcoins;  //账户存入银行流量币额度
		String mac; // 手机的mac地址
		String imei; // 手机imei（手机的唯一标识）
	}
	
	//2.7.3 取流量币
	public static class popFlowRequest {
		String userid;  //用户号码
		String flowcoins;  //账户从银行取出流量币额度
		String mac; // 手机的mac地址
		String imei; // 手机imei（手机的唯一标识）
	}
	
	/****************************************************
	 *                      D.发现
	 ****************************************************/
	
	/****************************************************
	 *                      E.我的
	 ****************************************************/
	//2.9.1 消息列表(待定，是否有消息类型？)
	public static class msgListRequest {
		String userid;  //用户号码
		String mac; // 手机的mac地址
		String imei; // 手机imei（手机的唯一标识）
	}
	
	//2.9.2 我的账单
	public static class billListRequest {
		String userid;  //用户号码
		String mac; // 手机的mac地址
		String imei; // 手机imei（手机的唯一标识）
		String page;
//		String type; 
	}
	
	//2.9.3 意见反馈
	public static class feedBackRequest {
		String userid;  //用户号码
		String type;  //类型 1 建议 2 投诉
		String content;  //内容
		String mac; // 手机的mac地址
		String imei; // 手机imei（手机的唯一标识）
	}
	
	//2.9.4 检查更新
	public static class uaRequest {
		String userid;  //用户号码
		String resolution;
		String channel;
		String appver;
		String deviceType;
		String internetway;
		String imsi;
		String mac; // 手机的mac地址
		String imei; // 手机imei（手机的唯一标识）
	}
	
	// 2.9.5 签到列表
	public static class SignInListRequest {
		String userid;  //用户号码
		String mac; // 手机的mac地址
		String imei; // 手机imei（手机的唯一标识）
	}
	
	// 2.9.6 签到
	public static class SignInRequest {
		String userid;  //用户号码
		String mac; // 手机的mac地址
		String imei; // 手机imei（手机的唯一标识）
	}
	
	
	
}

