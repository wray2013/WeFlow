/**
 * 创建时间
 * 2015年3月20日-下午9:00:55
 * 
 * 
 */
package net.etoc.wf.ctapp.user.entity;

import net.etoc.wf.ctapp.base.RequestBase;

/**
 * 
 * @author yuxuan
 *
 *         2015年3月20日 下午9:00:55
 * 
 * @version 1.0.0
 * 
 */
public class CrmOrderRequest extends RequestBase {
	/**
	 * serialVersionUID:TODO（用一句话描述这个变量表示什么）
	 *
	 * @since 1.0.0
	 */

	private static final long serialVersionUID = 6082168295407192636L;
	private String userid;
	private String productid;
	private String acctid;

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

	/**
	 * productid
	 *
	 * @return the productid
	 * @since 1.0.0
	 */

	public String getProductid() {
		return productid;
	}

	/**
	 * @param productid
	 *            the productid to set
	 */
	public void setProductid(String productid) {
		this.productid = productid;
	}

	/**
	 * acctid
	 *
	 * @return the acctid
	 * @since 1.0.0
	 */

	public String getAcctid() {
		return acctid;
	}

	/**
	 * @param acctid
	 *            the acctid to set
	 */
	public void setAcctid(String acctid) {
		this.acctid = acctid;
	}

}
