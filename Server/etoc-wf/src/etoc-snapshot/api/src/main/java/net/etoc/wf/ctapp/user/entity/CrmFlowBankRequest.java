/**
 * 创建时间
 * 2015年3月21日-下午8:58:54
 * 
 * 
 */
package net.etoc.wf.ctapp.user.entity;

import net.etoc.wf.ctapp.base.RequestBase;

/**
 * 
 * @author yuxuan
 *
 *         2015年3月21日 下午8:58:54
 * 
 * @version 1.0.0
 * 
 */
public class CrmFlowBankRequest extends RequestBase {
	private String opertype;
	private String flowcoins;
	private String userid;

	/**
	 * opertype
	 *
	 * @return the opertype
	 * @since 1.0.0
	 */

	public String getOpertype() {
		return opertype;
	}

	/**
	 * @param opertype
	 *            the opertype to set
	 */
	public void setOpertype(String opertype) {
		this.opertype = opertype;
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

	/**
	 * userid
	 *
	 * @return the userid
	 * @since 1.0.0
	 */

	public String getUserid() {
		return userid;
	}

	/**
	 * @param userid
	 *            the userid to set
	 */
	public void setUserid(String userid) {
		this.userid = userid;
	}

}
