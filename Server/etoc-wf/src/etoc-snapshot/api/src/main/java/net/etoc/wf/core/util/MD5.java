package net.etoc.wf.core.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 加密类
 *
 */
public class MD5 {
	private static Logger logger = LoggerFactory.getLogger(MD5.class);

	// 加的盐
	private static final String SALT = "ZXRvY3dmYXBwc2VydmVy";

	public static String EncoderByMd5(String buf) {
		try {
			MessageDigest digist = MessageDigest.getInstance("MD5");
			byte[] rs = digist.digest(buf.getBytes());
			StringBuffer digestHexStr = new StringBuffer();
			for (int i = 0; i < 16; i++) {
				digestHexStr.append(byteHEX(rs[i]));
			}
			return digestHexStr.toString();
		} catch (NoSuchAlgorithmException e) {
			logger.error(e.getMessage(), e);
		}
		return null;

	}

	public static void main(String args[]) {
		System.out.println(MD5.encodeByMd5AndSalt("passsdfsword"));

	}

	/**
	 * 加盐的md5值。这样即使被拖库，仍然可以有效抵御彩虹表攻击
	 * 
	 * @param inbuf
	 *            需做md5的字符串
	 * @return
	 * 
	 */
	public static String encodeByMd5AndSalt(String inbuf) {
		return EncoderByMd5(EncoderByMd5(inbuf) + SALT);
	}

	public static String byteHEX(byte ib) {
		char[] Digit = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A',
				'B', 'C', 'D', 'E', 'F' };
		char[] ob = new char[2];
		ob[0] = Digit[(ib >>> 4) & 0X0F];
		ob[1] = Digit[ib & 0X0F];
		String s = new String(ob);
		return s;
	}

}
