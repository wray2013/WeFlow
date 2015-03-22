/**
 * 创建时间
 * 2015年3月21日-下午9:45:13
 * 
 * 
 */
package net.etoc.wf.ctapp.user.entity;

import java.io.Serializable;

/**
 * 
 * @author yuxuan
 *
 *         2015年3月21日 下午9:45:13
 * 
 * @version 1.0.0
 * 
 */
public class CrmFlowStoreAndPopResponse implements Serializable {

	/**
	 * serialVersionUID:TODO（用一句话描述这个变量表示什么）
	 *
	 * @since 1.0.0
	 */

	private static final long serialVersionUID = -4882291363664135099L;

	private String status;
	private String message;
	private String bankcoins;
	private String flowcoins;

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

	/**
	 * bankcoins
	 *
	 * @return the bankcoins
	 * @since 1.0.0
	 */

	public String getBankcoins() {
		return bankcoins;
	}

	/**
	 * @param bankcoins
	 *            the bankcoins to set
	 */
	public void setBankcoins(String bankcoins) {
		this.bankcoins = bankcoins;
	}

	/**
	 * flowcoins
	 *
	 * @return the flowcoins
	 * @since 1.0.0
	 */

	public String getFlowcoins() {
		return flowcoins;
	}

	/**
	 * @param flowcoins
	 *            the flowcoins to set
	 */
	public void setFlowcoins(String flowcoins) {
		this.flowcoins = flowcoins;
	}

}
