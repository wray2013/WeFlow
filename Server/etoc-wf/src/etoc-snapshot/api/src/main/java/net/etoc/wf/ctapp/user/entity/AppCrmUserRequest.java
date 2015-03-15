/**
 * 创建时间
 * 2015年3月14日-下午6:43:58
 * 
 * 
 */
package net.etoc.wf.ctapp.user.entity;

import net.etoc.wf.ctapp.base.RequestBase;

/**
 * 
 * @author yuxuan
 *
 *         2015年3月14日 下午6:43:58
 * 
 * @version 1.0.0
 * 
 */
public class AppCrmUserRequest extends RequestBase {

	/**
	 * serialVersionUID:TODO（用一句话描述这个变量表示什么）
	 *
	 * @since 1.0.0
	 */

	private static final long serialVersionUID = -1762554830989958126L;

	private String password;

	private String newpassword; // 重置密码

	private String authcode; // 手机验证码

	/**
	 * password
	 *
	 * @return the password
	 * @since 1.0.0
	 */

	public String getPassword() {
		return password;
	}

	/**
	 * @param password
	 *            the password to set
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * newpassword
	 *
	 * @return the newpassword
	 * @since 1.0.0
	 */

	public String getNewpassword() {
		return newpassword;
	}

	/**
	 * @param newpassword
	 *            the newpassword to set
	 */
	public void setNewpassword(String newpassword) {
		this.newpassword = newpassword;
	}

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

}
