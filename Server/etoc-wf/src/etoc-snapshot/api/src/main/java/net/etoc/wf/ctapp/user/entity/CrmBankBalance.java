/**
 * 创建时间
 * 2015年3月22日-下午1:21:12
 * 
 * 
 */
package net.etoc.wf.ctapp.user.entity;

import net.etoc.wf.ctapp.base.RequestBase;

/**
 * 
 * @author yuxuan
 *
 *         2015年3月22日 下午1:21:12
 * 
 * @version 1.0.0
 * 
 */
public class CrmBankBalance extends RequestBase {
	/**
	 * serialVersionUID:TODO（用一句话描述这个变量表示什么）
	 *
	 * @since 1.0.0
	 */

	private static final long serialVersionUID = 8683444596498968505L;
	private String userid;

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
