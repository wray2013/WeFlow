package com.cmmobi.looklook.common.constant;

import java.util.HashMap;

import com.cmmobi.looklook.activity.LookLookActivity;

public class Constant {
	
	public final static String SD_STORAGE_ROOT = "/.looklook"; //不包括sdcard的路径
	public final static int HANDLER_FLAG_LISTVIEW_UPDATE = 0xbebe0001; 
	public static enum SEND_MSG_STATUS {MSG_INIT, REC_DONE, REG_DONE, RIA_DONE, ALL_DONE, RIA_FAIL, UPLOAD_FAIL}; 

	//public static final String WX_APP_ID = "wx6cd8998bc0d1c26e"; //产品环境
	public static final String WX_APP_ID = "wxe194363d947f9529"; //产品环境looklook-懂你的生活记录
	//public static final String WX_APP_ID = "wx81673cc28d8241d4"; //产品环境 微享-懂你的生活记录
	//public static final String WX_APP_ID = "wx87ae10b9a792a445";  //zhangwei测试环境
	
	public static boolean open_strict_mode = false;
	
	/**
	 * UDP 心跳间隔5s
	 */
	public static final long HEARTBEAT_INTERVAL = 5 * 1000;
	
//	public static final String UDP_HOST = "192.168.100.114";
//	public static final int UDP_PORT = 8668;

	//public static final long MEDIA_CACHE_LIMIT = 1024 * 1024 * 1024;
	
	public static final long DIARY_CACHE_LIMIT = 100;
	public static final long MEDIA_CACHE_LIMIT = 100;
	
	public  static HashMap<String, String> STATUS_MAP = new HashMap<String, String>(){/**
		 * 
		 */
		private static final long serialVersionUID = -126211633708279218L;

	{ 
		put("138000", "客户端参数问题"); 
		put("138100", "客户端参数类型问题");
		put("138101", "客户端参数有空值");
		put("138102", "数据不存在");
		put("138103", "密码少于六位");
		put("138104", "设备id不存在");
		put("138105", "客户端日志格式有问题");
		put("138106", "客户被禁言");
		put("138107", "客户黑名单");
		put("138108", "数据类型问题");
		put("138109", "用户审核不通过");
		put("138110", "对官方用户违规操作");
		put("138111", "客户端参数不匹配");
		put("138112", "ip、mac或imei被屏蔽");
		put("138113", "签名错误");
		put("138114", "昵称已经存在");
		put("138115", "未关注");
		put("138116", "关注数达到上限");
		put("138117", "粉丝数达到上限");
		put("138118", "手势密码小于3位");
		put("138119", "没有权限");
		put("138120", "此日记已参加过活动");
		put("138122", "该" + LookLookActivity.APP_NAME +"号没有绑定手机号");
		
		put("200000", "服务器问题");
		put("200100", "数据库异常");
		put("200200", "应用层连接CRM异常");
		put("200210", "应用层加密签名问题");
		put("200211", "应用层解密签名问题");
		put("200300", "写文件异常问题");
		put("200101", "SQL语法错误");
		put("200102", "数据库连接超时");
		put("200400", "base64解码错误");
		put("200201", "crm为知状态");
		put("200500", "客户资源达到上限错误");
		put("200600", "请查看crm状态");
		}
	};
	
	public static String[] CRM_STATUS = {
		"OK",        //0
		"账号已存在", //1
		"系统繁忙", //2	系统错误
		"用户名或者密码错误", //3
		"用户ID为空", //4
		"用户名为空", //5 登陆名为空
		"用户密码为空", //6
		"用户名不存在", //7用户名不存在, 用户名或者密码错误
		"找回密码已过期", //8
		"系统繁忙", //9	CRM系统报错
		"系统繁忙", //10产品对象不存在
		"系统繁忙", //11移动终端对象不存在
		"系统繁忙", //12保存用户失败
		"密码不能小于6位", //13	
		"系统繁忙", //14，产品标示为空 服务器内部错误
		"系统繁忙", //15， 设备ID为空
		"第三方用户类型错误", //16	
		"手机号未注册", //17	
		"手机验证码错误", //:18	
		"手机验证码验证超时", //19	
		"第三方用户首次登录", //20	
		"手机号已经被绑定", //21	
		"手机号和对应用户未绑定",  //22	
		"邮箱已经被绑定", //23	
		"用户已有绑定邮箱", //24	
		"邮箱和对应用户未绑定", //25	
		"账号已经被绑定", //26	第三方账号已经被绑定
		"第三方 和指定用户 未绑定", //27	
		"手机号号码无效", //28	
		"系统繁忙", //29	
		"短信请求太快", //30	
		"短信内容超过最大限制" , //31	
		"短信内容异常", //32	
		"短信发送异常" , //33	
		"手机验证码错误", //34	手机验证码不存在
		"手机绑定类型错误", //35	
		"用户已经绑定了其他手机", //36	
		"该帐户唯一，不可解绑", //37	
		"ip鉴权失败", //38
		"邮件地址不存在或者异常" //39
	};
	
	
	public  static HashMap<String, String> MAIL_MAP = new HashMap<String, String>(){

	/**
		 * 
		 */
		private static final long serialVersionUID = 3380250772783732113L;

	{ 
		put("qq.com", "http://m.mail.qq.com/"); 
		put("163.com", "http://mail.163.com/");
		put("sina.com", "http://mail.sina.com.cn/");
		put("126.com", "http://mail.126.com/");
		put("sohu.com", "http://mail.sohu.com/");
		put("yohoo.com", "http://mail.yahoo.com/");
		put("hotmail.com", "http://mail.hotmail.com/");
		}
	};


}
