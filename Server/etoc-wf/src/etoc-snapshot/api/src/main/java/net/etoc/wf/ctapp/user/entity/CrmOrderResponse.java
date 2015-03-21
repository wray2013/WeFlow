/**
 * 创建时间
 * 2015年3月20日-下午9:05:01
 * 
 * 
 */
package net.etoc.wf.ctapp.user.entity;

import net.etoc.wf.ctapp.base.ResponseBase;

/**
 * 
 * @author yuxuan
 *
 *         2015年3月20日 下午9:05:01
 * 
 * @version 1.0.0
 * 
 */
public class CrmOrderResponse extends ResponseBase {
	/**
	 * serialVersionUID:TODO（用一句话描述这个变量表示什么）
	 *
	 * @since 1.0.0
	 */

	private static final long serialVersionUID = -1705165231157940818L;
	private String flowcoins;
	private String keycode;

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
	 * keycode
	 *
	 * @return the keycode
	 * @since 1.0.0
	 */

	public String getKeycode() {
		return keycode;
	}

	/**
	 * @param keycode
	 *            the keycode to set
	 */
	public void setKeycode(String keycode) {
		this.keycode = keycode;
	}

}
