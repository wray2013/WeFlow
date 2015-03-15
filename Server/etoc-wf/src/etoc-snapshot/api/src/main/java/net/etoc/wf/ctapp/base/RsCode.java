/**
 * 创建时间
 * 2015年3月14日-下午1:08:07
 * 
 * 
 */
package net.etoc.wf.ctapp.base;

/**
 * 
 * @author yuxuan
 *
 *         2015年3月14日 下午1:08:07
 * 
 * @version 1.0.0
 * 
 */
public enum RsCode {
	SignFail("9999", "验签失败");

	private String code;
	private String message;

	private RsCode(String code, String message) {
		this.code = code;
		this.message = message;
	}

	/**
	 * code
	 *
	 * @return the code
	 * @since 1.0.0
	 */

	public String getCode() {
		return code;
	}

	/**
	 * @param code
	 *            the code to set
	 */
	public void setCode(String code) {
		this.code = code;
	}

	/**
	 * message
	 *
	 * @return the message
	 * @since 1.0.0
	 */

	public String getMessage() {
		return message;
	}

	/**
	 * @param message
	 *            the message to set
	 */
	public void setMessage(String message) {
		this.message = message;
	}

}
