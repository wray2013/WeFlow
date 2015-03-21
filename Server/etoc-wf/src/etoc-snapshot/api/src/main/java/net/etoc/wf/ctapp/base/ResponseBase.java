/**
 * 创建时间
 * 2015年3月14日-下午12:47:43
 * 
 * 
 */
package net.etoc.wf.ctapp.base;

import java.io.Serializable;

/**
 * 
 * @author yuxuan
 *
 *         2015年3月14日 下午12:47:43
 * 
 * @version 1.0.0
 * 
 */
public class ResponseBase implements Serializable {
	/**
	 * serialVersionUID:TODO（用一句话描述这个变量表示什么）
	 *
	 * @since 1.0.0
	 */

	private static final long serialVersionUID = 421368944860988556L;
	private String status;
	private String message;

	/**
	 * status
	 *
	 * @return the status
	 * @since 1.0.0
	 */

	public String getStatus() {
		return status;
	}

	/**
	 * @param status
	 *            the status to set
	 */
	public void setStatus(String status) {
		this.status = status;
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
