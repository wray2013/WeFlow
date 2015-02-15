package com.cmmobi.looklook.common.view;

import java.io.ByteArrayOutputStream;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.cmmobi.looklook.MainApplication;
import com.cmmobi.looklook.R;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ImageSpan;

public class EmojiPaser {
	private static EmojiPaser ins;
//	private Context c;
	private HashMap<String, String> map;
	private String regExpStr;
	private Pattern pattern;
	public static EmojiPaser getInstance(){
		if(ins==null){
			ins = new EmojiPaser();
		}
		
		return ins;
	}
	
	private EmojiPaser(){
		String[] stringArray = MainApplication.getAppInstance().getResources().getStringArray(R.array.ios_emoji_array);
		map = new HashMap<String, String>();
		StringBuilder sb = new StringBuilder();
		if(stringArray!=null && stringArray.length>0){
			for(int index=0; index<stringArray.length; index++){
				String line = stringArray[index];
				String emoji_key = decode(line); //实际的表情utf8格式
				String emoji_value = "<e>" + line + "</e>";
				
				map.put(emoji_key, emoji_value);
				
				if(index!=0){
					sb.append("|");
				}
				
				sb.append(emoji_key);
				
			}
			
			regExpStr = sb.toString();
			pattern = Pattern.compile(regExpStr);
		}
	}
	
	/**
	 * 将带表情的文本（utf8格式）,转化为 <e>emoji(utf8)</e>格式的文本（utf8）
	 * */
	public String format(String inputText){
		if(pattern!=null){
			Matcher matcher = pattern.matcher(inputText);
			StringBuffer sbr = new StringBuffer();
			while (matcher.find()) {
				int start = matcher.toMatchResult().start();
				int end = matcher.toMatchResult().end();
				String key = inputText.substring(start, end);
				String replace = map.get(key);
				if(replace!=null){
				    matcher.appendReplacement(sbr, replace);
				}else{
				    matcher.appendReplacement(sbr, "<e>" + encode(key) + "</e>");
				}

			}
			matcher.appendTail(sbr);
			//System.out.println(sbr.toString());
			return sbr.toString();	
		}else{
			return null;
		}

	}
	
	/**
	 * 将EmojiParser解析出的"<e>xxxxx</e>"代码，渲染成图片
	 * @param content
	 * @param mContext
	 * @return
	 */
	public static SpannableStringBuilder convetToHtml(String content) {
		String regex = "\\<e\\>(.*?)\\</e\\>";
		Pattern pattern = Pattern.compile(regex);
		String emo = "";
		Resources resources = MainApplication.getAppInstance().getResources();
		Matcher matcher = pattern.matcher(content);
		SpannableStringBuilder sBuilder = new SpannableStringBuilder(content);
		Drawable drawable = null;
		ImageSpan span = null;
		while (matcher.find()) {
			emo = matcher.group();
			try {
				int id = resources.getIdentifier(
						"emoji_"+ emo.substring(emo.indexOf(">") + 1,emo.lastIndexOf("<")), "drawable",
						MainApplication.getAppInstance().getPackageName());
				if (id != 0) {
					drawable = resources.getDrawable(id);
					drawable.setBounds(0, 0, 24, 24);
					span = new ImageSpan(drawable);
					sBuilder.setSpan(span, matcher.start(), matcher.end(),Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
				}
			} catch (Exception e) {
				break;
			}
		}
		return sBuilder;
	}
	
	/* 
	* 16进制数字字符集 
	*/ 
	private static String hexString="0123456789abcdef"; 


	/*
	 * 将字符串编码成16进制数字,适用于所有字符（包括中文）
	 */
	public static String encode(String str) {
		// 根据默认编码获取字节数组
		byte[] bytes = str.getBytes();
		StringBuilder sb = new StringBuilder(bytes.length * 2);
		// 将字节数组中每个字节拆解成2位16进制整数
		for (int i = 0; i < bytes.length; i++) {
			sb.append(hexString.charAt((bytes[i] & 0xf0) >> 4));
			sb.append(hexString.charAt((bytes[i] & 0x0f) >> 0));
		}
		return sb.toString();
	}

	/* 
	* 将16进制数字解码成字符串,适用于所有字符（包括中文） 
	*/ 
	public static String decode(String bytes) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream(
				bytes.length() / 2);
		// 将每2位16进制整数组装成一个字节
		for (int i = 0; i < bytes.length(); i += 2)
			baos.write((hexString.indexOf(bytes.charAt(i)) << 4 | hexString
					.indexOf(bytes.charAt(i + 1))));
		String ret =  new String(baos.toByteArray(), Charset.forName("UTF8"));
		return ret;
	}
}
