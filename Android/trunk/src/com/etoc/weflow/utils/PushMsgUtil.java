package com.etoc.weflow.utils;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;


/**
 * 调用远程api实现推送
 * @author Ray
 *
 */
public class PushMsgUtil extends Thread {
	public static final String TAG = "PushMsgUtil";
//	public static final String PUSH_URL = "https://api.jpush.cn:443/v2/push";
	public static final String PUSH_URL = "http://api.jpush.cn:8800/v2/push";
	
	private Handler handler;
	private int responseType;
	private String msg, tag;
	
	public PushMsgUtil(Handler handler, int responseType) {
		this.handler = handler;
		this.responseType = responseType;
	}
	
	public void execute(String msg, String tag) {
		this.msg = msg;
		this.tag = tag;
		this.start();
	}
	
	@Override
	public void run() {
		String out = "";
		Calendar ca = Calendar.getInstance();
//		String year = ca.get(Calendar.YEAR) + "";
//		String mon = String.format("%02d", ca.get(Calendar.MONTH));
//		String date = String.format("%02d", ca.get(Calendar.DATE));
		String hour = String.format("%02d", ca.get(Calendar.HOUR_OF_DAY));
		String min = String.format("%02d", ca.get(Calendar.MINUTE));
		String sec = String.format("%02d", ca.get(Calendar.SECOND));
		Random r = new Random();
		String random = r.nextInt(10) + "";
//		String mil = ca.get(Calendar.MILLISECOND) + "";
		String num = /*mon + date + */hour + min + sec + random;//0108174901x
		int no = Integer.parseInt(num.trim());
		Log.d(TAG, "num = " + no);
//		BasicNameValuePair name = new BasicNameValuePair("username", "test");  //用户名
		BasicNameValuePair sendno = new BasicNameValuePair("sendno", no + "");  // 发送编号。由开发者自己维护，标识一次发送请求
		BasicNameValuePair appkeys = new BasicNameValuePair("app_key", "cbc2a2081bfafc6ea8ed1174");  // 待发送的应用程序(appKey)，只能填一个。
		BasicNameValuePair receiver_type = new BasicNameValuePair("receiver_type", "2");
		BasicNameValuePair receiver_value = new BasicNameValuePair("receiver_value", tag);
		//验证串，用于校验发送的合法性。
		BasicNameValuePair verification_code = new BasicNameValuePair("verification_code", getVerificationCode(no, tag));
		//发送消息的类型：1 通知 2 自定义
		BasicNameValuePair msg_type = new BasicNameValuePair("msg_type", "1");
		BasicNameValuePair msg_content = new BasicNameValuePair("msg_content", msg);
		//目标用户终端手机的平台类型，如： android, ios 多个请使用逗号分隔。
		BasicNameValuePair platform = new BasicNameValuePair("platform", "android");
		List<BasicNameValuePair> datas = new ArrayList<BasicNameValuePair>();
//		datas.add(name);
		datas.add(sendno);
		datas.add(appkeys);
		datas.add(receiver_type);
		datas.add(receiver_value);
		datas.add(verification_code);
		datas.add(msg_type);
		datas.add(msg_content);
		datas.add(platform);
		try {
			HttpEntity entity = new UrlEncodedFormEntity(datas, "utf-8");
			HttpPost post = new HttpPost(PUSH_URL);
			post.setEntity(entity);
			HttpClient client = ClientUtil.getNewHttpClient();
			HttpResponse reponse = client.execute(post);
			HttpEntity resEntity = reponse.getEntity();
			out = EntityUtils.toString(resEntity);
			System.out.println(out);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		
		if (handler != null) {
			Message message = handler.obtainMessage(responseType, out);
			handler.sendMessage(message);
		} else {
			Log.e(TAG, "handler is null, data can not callback.");
		}
	}
	
	
	private static String getVerificationCode(int sendno, String receiverValue) {
		String password = "173bddfa94b6336e488c7c63";
		String receiverType = "2";
//		String md5Password = StringUtils.toMD5(password);; //password 是开发者Portal帐户的登录密码
		String input = String.valueOf(sendno) + receiverType + receiverValue + password;
		String verificationCode = MD5Utils.get32MD5Str(input);//StringUtils.toMD5(input);
//		Log.d(TAG, "v1 = " + MD5.get32MD5Str(input) + "\nv2 = " + StringUtils.toMD5(input));
		return verificationCode;
	}
	
	/*
	public static void main(String[] args) {
		String msg = "{\"n_title\":\"来点外卖\",\"n_content\":\"你好\"}";
		System.out.println(msg);
		PushMsgUtil.pushMsg(msg);
	}*/

}

