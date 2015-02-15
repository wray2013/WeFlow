package com.cmmobi.railwifi.alipay;

import java.lang.reflect.Field;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import com.alipay.sdk.app.PayTask;

public class PayInfo {
	public static final int RESPONSE_ALIPAY_RESULT = 0xaabbccde;
	public static final String TAG = "ALIPAYINFO";
	// 接口名称  不可空 固定值。
	public String service = "mobile.securitypay.pay";
	// 合作者身份ID  不可空 签约的支付宝账号对应的支付宝唯一用户号。以2088开头的16位纯数字组成。
	public String partner = Keys.DEFAULT_PARTNER;
	// 参数编码字符集   不可空  商户网站使用的编码格式，固定为utf-8 
	public String _input_charset = "UTF-8";
	// 签名方式   不可空  签名类型，目前仅支持RSA。必须最后设置
	public String sign_type = "";
	// 签名           不可空  请参见“9  签名机制”。倒数第二个设置，用RSA加密
	public String sign = "";
	// 服务器异步通知页面路径   可空
	public String notify_url = URLEncoder.encode("http://123.150.178.29:8888/rw/alipayCallback.html?order_no=");
	// 客户端号  可空  标识客户端
	public String app_id = "";
	// 客户端来源  可空 标识客户端来源。参数值内容
	/*约定如下：
	appenv=”system=客户端平
	台名^version=业务系统版
	本”，例如：
	appenv=”system=iphone^ve
	rsion=3.0.1.2” 
	appenv=”system=ipad^versi
	on=4.0.1.1” */
	public String appenv = "";
	// 商户网站唯一订单号  不可空  支付宝合作商户网站唯一订单号。
	public String out_trade_no = "";
	// 商品名称  不可空  商品的标题/交易标题/订单标 题/订单关键字等。该参数最长为128个汉字。
	public String subject = "";
	// 支付类型。不可空 默认值为：1（商品购买）。
	public String payment_type = "1"; 
	// 卖家支付宝账号 不可空 卖家支付宝账号（邮箱或手机号码格式）或其对应的支付宝唯一用户号（以2088开头的纯16位数字）。
	public String seller_id = Keys.DEFAULT_SELLER;
	// 总金额 不可空  该笔订单的资金总额，单位为RMB-Yuan。取值范围为[0.01，100000000.00]，精确到小数点后两位。
	public String total_fee = "";
	// 商品详情 不可空 对一笔交易的具体描述信息。如果是多种商品，请将商品描 述字符串累加传给body。
	public String body = "";
	// 未付款交易的超时时间 可为空
	/*设置未付款交易的超时时间，
	一旦超时，该笔交易就会自动
	被关闭。
	取值范围：1m～15d。
	m-分钟，h-小时，d-天，1c-当天（无论交易何时创建，都
	在0点关闭）。
	该参数数值不接受小数点，如
	1.5h，可转换为90m。*/
	public String it_b_pay = "10m";
	// 商品展示地址  可空 商品展示的超链接。预留参数
	public String show_url = "";
	// 授权令牌  可空  开放平台返回的包含账户信息的token（授权令牌，商户在一定时间内对支付宝某些服务的访问权限）。
	public String extern_token = "";
	public PayInfo setService(String service) {
		this.service = service;
		return this;
	}
	public PayInfo setPartner(String partner) {
		this.partner = partner;
		return this;
	}
	public PayInfo set_input_charset(String _input_charset) {
		this._input_charset = _input_charset;
		return this;
	}
	public PayInfo setSign_type(String sign_type) {
		this.sign_type = sign_type;
		return this;
	}
	public PayInfo setSign(String sign) {
		this.sign = sign;
		return this;
	}
	public PayInfo setNotify_url(String notify_url) {
		this.notify_url = notify_url;
		return this;
	}
	public PayInfo setApp_id(String app_id) {
		this.app_id = app_id;
		return this;
	}
	public PayInfo setAppenv(String appenv) {
		this.appenv = appenv;
		return this;
	}
	public PayInfo setOut_trade_no(String out_trade_no) {
		this.out_trade_no = out_trade_no;
		return this;
	}
	public PayInfo setSubject(String subject) {
		this.subject = subject;
		return this;
	}
	public PayInfo setPayment_type(String payment_type) {
		this.payment_type = payment_type;
		return this;
	}
	public PayInfo setSeller_id(String seller_id) {
		this.seller_id = seller_id;
		return this;
	}
	public PayInfo setTotal_fee(String total_fee) {
		this.total_fee = total_fee;
		return this;
	}
	public PayInfo setBody(String body) {
		this.body = body;
		return this;
	}
	public PayInfo setIt_b_pay(String it_b_pay) {
		this.it_b_pay = it_b_pay;
		return this;
	}
	public PayInfo setShow_url(String show_url) {
		this.show_url = show_url;
		return this;
	}
	public PayInfo setExtern_token(String extern_token) {
		this.extern_token = extern_token;
		return this;
	} 
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		StringBuilder sb = new StringBuilder();
		append(sb,"service", service);
		append(sb, "&partner", partner);
		append(sb, "&_input_charset", _input_charset);
		append(sb, "&sign_type", sign_type);
		append(sb, "&sign", sign);
		append(sb, "&notify_url",notify_url);
		append(sb, "&app_id", app_id);
		append(sb, "&appenv", appenv);
		append(sb, "&out_trade_no", out_trade_no);
		append(sb, "&subject",subject);
		append(sb, "&payment_type", payment_type);
		append(sb, "&seller_id", seller_id);
		append(sb, "&total_fee",total_fee);
		append(sb, "&body",body);
		append(sb, "&it_b_pay",it_b_pay);
		append(sb, "&show_url",show_url);
		append(sb, "&extern_token",extern_token);
		
		return sb.toString();
	}
	
	private void append(StringBuilder sb,String key,String value) {
		if (!TextUtils.isEmpty(value)) {
			sb.append(key + "=" + "\"" + value + "\"");
		}
	}
	
	public static String createOutTradeNo() {
		SimpleDateFormat format = new SimpleDateFormat("MMddHHmmss");
		Date date = new Date();
		String key = format.format(date);

		java.util.Random r = new java.util.Random();
		key += r.nextInt();
		key = key.substring(0, 15);
		return key;
	}
	
	public void setValue(String key,String value) {
		try {
			Field field=this.getClass().getField(key);
			field.set(this, value);
		} catch (NoSuchFieldException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void pay(final Activity context,final Handler handler,final String subject,final String body,final String price,final String trande_no) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
//				AliPay alipay = new AliPay(context, handler);
				// 构造PayTask 对象
				PayTask alipay = new PayTask(context);
				// 调用支付接口
				String notifyUrl = URLEncoder.encode("http://123.150.178.29:8888/rw/alipayCallback.html?order_no=" + trande_no);
				PayInfo payinfo = new PayInfo();
				payinfo.setSubject(subject)
					.setBody(body)
					.setTotal_fee(price)
					.setOut_trade_no(trande_no)
					.setNotify_url(notifyUrl);
				
				String sign = Rsa.sign(payinfo.toString(), Keys.PRIVATE);
				sign = URLEncoder.encode(sign);
				payinfo.setSign(sign).setSign_type("RSA");
				Log.d(TAG,"payInfo = " + payinfo.toString());
				String result = alipay.pay(payinfo.toString());
				
				Log.d(TAG, "result =  " + result);
				Result aliResult = new Result(result);
				Message msg = new Message();
				msg.what = RESPONSE_ALIPAY_RESULT;
				msg.obj = aliResult;
				handler.sendMessage(msg);
			}
		}).start();
	}

}
