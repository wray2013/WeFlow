/**
 * 创建时间
 * 2015年3月12日-下午10:27:33
 * 
 * 
 */
package net.etoc.test;

import java.io.Serializable;

/**
 * 
 * @author yuxuan
 *
 *         2015年3月12日 下午10:27:33
 * 
 * @version 1.0.0
 * 
 */
public class Tvo implements Serializable {
	private String json;
	private String sign;

	/**
	 * json
	 *
	 * @return the json
	 * @since 1.0.0
	 */

	public String getJson() {
		return json;
	}

	/**
	 * @param json
	 *            the json to set
	 */
	public void setJson(String json) {
		this.json = json;
	}

	/**
	 * sign
	 *
	 * @return the sign
	 * @since 1.0.0
	 */

	public String getSign() {
		return sign;
	}

	/**
	 * @param sign
	 *            the sign to set
	 */
	public void setSign(String sign) {
		this.sign = sign;
	}

}
