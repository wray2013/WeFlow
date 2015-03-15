/**
 * 创建时间
 * 2015年3月14日-下午12:33:24
 * 
 * 
 */
package net.etoc.wf.ctapp.user.entity;

import net.etoc.wf.ctapp.base.RequestBase;

/**
 * 
 * @author yuxuan
 *
 *         2015年3月14日 下午12:33:24
 * 
 * @version 1.0.0
 * 
 */
public class AuthCodeRequest extends RequestBase {
	/**
	 * serialVersionUID:TODO（用一句话描述这个变量表示什么）
	 *
	 * @since 1.0.0
	 */

	private static final long serialVersionUID = -7073798560055188599L;
	/**
	 * serialVersionUID:TODO（用一句话描述这个变量表示什么）
	 *
	 * @since 1.0.0
	 */

	private String type;

	private String authcode;

	/**
	 * authcode
	 *
	 * @return the authcode
	 * @since 1.0.0
	 */

	public String getAuthcode() {
		return authcode;
	}

	/**
	 * @param authcode
	 *            the authcode to set
	 */
	public void setAuthcode(String authcode) {
		this.authcode = authcode;
	}

	/**
	 * type
	 *
	 * @return the type
	 * @since 1.0.0
	 */

	public String getType() {
		return type;
	}

	/**
	 * @param type
	 *            the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}

}
