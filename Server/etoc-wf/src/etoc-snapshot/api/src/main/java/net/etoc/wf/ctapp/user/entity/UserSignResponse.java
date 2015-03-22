/**
 * 创建时间
 * 2015年3月22日-下午5:38:03
 * 
 * 
 */
package net.etoc.wf.ctapp.user.entity;

import net.etoc.wf.ctapp.base.ResponseBase;

/**
 * 
 * @author yuxuan
 *
 *         2015年3月22日 下午5:38:03
 * 
 * @version 1.0.0
 * 
 */
public class UserSignResponse extends ResponseBase {
	private String signcount;
	private String signlist;

	/**
	 * signcount
	 *
	 * @return the signcount
	 * @since 1.0.0
	 */

	public String getSigncount() {
		return signcount;
	}

	/**
	 * @param signcount
	 *            the signcount to set
	 */
	public void setSigncount(String signcount) {
		this.signcount = signcount;
	}

	/**
	 * signlist
	 *
	 * @return the signlist
	 * @since 1.0.0
	 */

	public String getSignlist() {
		return signlist;
	}

	/**
	 * @param signlist
	 *            the signlist to set
	 */
	public void setSignlist(String signlist) {
		this.signlist = signlist;
	}

}
