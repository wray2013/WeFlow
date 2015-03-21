/**
 * 创建时间
 * 2015年3月21日-下午7:10:54
 * 
 * 
 */
package net.etoc.wf.ctapp.user.entity;

import java.io.Serializable;

/**
 * 
 * @author yuxuan
 *
 *         2015年3月21日 下午7:10:54
 * 
 * @version 1.0.0
 * 
 */
public class CrmFlowBankResponse implements Serializable {
	/**
	 * serialVersionUID:TODO（用一句话描述这个变量表示什么）
	 *
	 * @since 1.0.0
	 */

	private static final long serialVersionUID = -4227293571080222078L;
	private String status;
	private String message;
	private String thresholdpop;
	private String thresholdpush;
	private String flowbankcoins;
	private String yestdincome;
	private String yestdrate;
	private String totalincome;

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
	 * thresholdpop
	 *
	 * @return the thresholdpop
	 * @since 1.0.0
	 */

	public String getThresholdpop() {
		return thresholdpop;
	}

	/**
	 * @param thresholdpop
	 *            the thresholdpop to set
	 */
	public void setThresholdpop(String thresholdpop) {
		this.thresholdpop = thresholdpop;
	}

	/**
	 * thresholdpush
	 *
	 * @return the thresholdpush
	 * @since 1.0.0
	 */

	public String getThresholdpush() {
		return thresholdpush;
	}

	/**
	 * @param thresholdpush
	 *            the thresholdpush to set
	 */
	public void setThresholdpush(String thresholdpush) {
		this.thresholdpush = thresholdpush;
	}

	/**
	 * flowbankcoins
	 *
	 * @return the flowbankcoins
	 * @since 1.0.0
	 */

	public String getFlowbankcoins() {
		return flowbankcoins;
	}

	/**
	 * @param flowbankcoins
	 *            the flowbankcoins to set
	 */
	public void setFlowbankcoins(String flowbankcoins) {
		this.flowbankcoins = flowbankcoins;
	}

	/**
	 * yestdincome
	 *
	 * @return the yestdincome
	 * @since 1.0.0
	 */

	public String getYestdincome() {
		return yestdincome;
	}

	/**
	 * @param yestdincome
	 *            the yestdincome to set
	 */
	public void setYestdincome(String yestdincome) {
		this.yestdincome = yestdincome;
	}

	/**
	 * yestdrate
	 *
	 * @return the yestdrate
	 * @since 1.0.0
	 */

	public String getYestdrate() {
		return yestdrate;
	}

	/**
	 * @param yestdrate
	 *            the yestdrate to set
	 */
	public void setYestdrate(String yestdrate) {
		this.yestdrate = yestdrate;
	}

	/**
	 * totalincome
	 *
	 * @return the totalincome
	 * @since 1.0.0
	 */

	public String getTotalincome() {
		return totalincome;
	}

	/**
	 * @param totalincome
	 *            the totalincome to set
	 */
	public void setTotalincome(String totalincome) {
		this.totalincome = totalincome;
	}

}
