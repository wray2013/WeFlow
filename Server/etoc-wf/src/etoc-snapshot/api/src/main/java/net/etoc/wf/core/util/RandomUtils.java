package net.etoc.wf.core.util;

import java.security.SecureRandom;
import java.util.Random;
import java.util.UUID;

/**
 * 封装各种生成唯一性.
 * 
 * 
 */
public class RandomUtils {

	private static SecureRandom random = new SecureRandom();

	/**
	 * 封装JDK自带的UUID, 通过Random数字生成, 中间有-分割.
	 */
	public static String uuid() {
		return UUID.randomUUID().toString();
	}

	/**
	 * 封装JDK自带的UUID, 通过Random数字生成, 中间无-分割.
	 */
	public static String uuid2() {
		return UUID.randomUUID().toString().replaceAll("-", "");
	}

	/**
	 * 使用SecureRandom随机生成Long.
	 */
	public static long randomLong() {
		return Math.abs(random.nextLong());
	}

	/**
	 * 创建指定数量的随机字符串
	 * 
	 * @param numberFlag
	 *            是否是纯数字
	 * @param length
	 * @return
	 */
	public static String createRandom(boolean numberFlag, int length) {
		StringBuffer retStr = new StringBuffer();
		String strTable = numberFlag ? "1234567890"
				: "1234567890abcdefghijkmnpqrstuvwxyz";
		int len = strTable.length();
		for (int i = 0; i < length; i++) {
			double dblR = Math.random() * len;
			int intR = (int) Math.floor(dblR);
			retStr.append(strTable.charAt(intR));
		}

		return retStr.toString();
	}

	/**
	 * @Title: createRnageRndom
	 * @Description: TODO(生成一个int型 范围固定的随机数)
	 * @param @param max
	 * @param @param min
	 * @param @return 设定文件
	 * @return int 返回类型
	 * @throws
	 */
	public static int createRnageRndom(int min, int max) {
		Random random = new Random();
		// random.nextInt(max) 生成一个 [0,max]之间的随机数 然后对(max-min+1)取模
		// 以生成[10,20]随机数为例，首先生成0-20的随机数，然后对(20-10+1)取模得到[0-10]之间的随机数，然后加上min=10，最后生成的是10-20的随机数
		int rs = random.nextInt(max) % (max - min + 1) + min;
		return rs;
	}
}
