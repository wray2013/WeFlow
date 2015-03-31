package com.etoc.weflow.utils;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import android.util.Base64;

public class DESDecryptAPIUtil {
	private final static char[] HEX = "0123456789abcdef".toCharArray();
	
/*	public static void main(String args[]) throws Exception {
		DESDecryptAPIUtil des = new DESDecryptAPIUtil();

		//固定值：418703564FBBB3F01C88179D6FC786B950C290C51
		String key = toHex("418703564FBBB3F01C88179D6FC786B950C290C51");
		
		//我返回的加密的keycard
		String keycard = "el6mFxNJMWA+vu/xSCEvj2DBc78uKaK1GWZKzShVTvMswYhh6wXGeg==";
		
		//你需要解密
		String deStr = des.decryptDES(keycard, key);
	
		//解密后的真正卡密
		System.out.println("明文:" + deStr);
	
	}*/
	
	/**
	 * @param kye=订单号+密钥
	 * @return 加密后key(截取8位)
	 * @throws Exception
	 */
	public static String toHex(String kye) throws NoSuchAlgorithmException, UnsupportedEncodingException {
		
		MessageDigest digest = MessageDigest.getInstance("MD5");
		byte[] bys = kye.getBytes("utf-8");
		byte[] hash = digest.digest(bys);
		
		char[] chs = new char[hash.length * 2];
		for (int i = 0, k = 0; i < hash.length; i++) {
			chs[k++] = HEX[(hash[i] & 0xf0) >> 4];
			chs[k++] = HEX[hash[i] & 0xf];
		}
		return new String(chs).substring(4, 12);
	}
	
	/**
	 * @param encryptString 要加密的明码
	 * @param encryptKey 加密的密钥
	 * @return 加密后的暗码
	 * @throws Exception
	 */
	public static String encryptDES(String encryptString, String encryptKey) throws Exception {//加密  
	    IvParameterSpec zeroIv = new IvParameterSpec(new byte[8]);  
	    SecretKeySpec key = new SecretKeySpec(encryptKey.getBytes(), "DES");  
	    Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");  
	    cipher.init(Cipher.ENCRYPT_MODE, key, zeroIv);  
	    byte[] encryptedData = cipher.doFinal(encryptString.getBytes());
	    String encrStr = new String(Base64.encode(encryptedData,  Base64.DEFAULT), "UTF-8");
	    return encrStr;  
	}  
	  
	/**
	 * @param decryptString 需要解密的暗码
	 * @param encryptkey 加密的密钥
	 * @return 接密后的明码
	 * @throws Exception
	 */
	public static String decryptDES(String decryptString, String decryptKey) throws Exception {//解密  
	    byte[] byteMi = Base64.decode(decryptString.getBytes(), Base64.DEFAULT);/*new BASE64Decoder().decodeBuffer(decryptString)*/;  
	    IvParameterSpec zeroIv = new IvParameterSpec(new byte[8]);  
	    SecretKeySpec key = new SecretKeySpec(decryptKey.getBytes(), "DES");  
	    Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");  
	    cipher.init(Cipher.DECRYPT_MODE, key, zeroIv);  
	    byte decryptedData[] = cipher.doFinal(byteMi);  
	    return new String(decryptedData);  
	} 
	
	public static String decryptDES(String decryptString) {
		String desStr = decryptString;
		try {
			String key = toHex("418703564FBBB3F01C88179D6FC786B950C290C51");
			desStr = decryptDES(decryptString, key);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return desStr;
	}
	
}