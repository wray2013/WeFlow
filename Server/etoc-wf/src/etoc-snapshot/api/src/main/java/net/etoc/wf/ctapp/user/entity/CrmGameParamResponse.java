/**
 * 创建时间
 * 2015年4月24日-下午10:13:10
 * 
 * 
 */
package net.etoc.wf.ctapp.user.entity;

import net.etoc.wf.ctapp.base.ResponseBase;

/**
 * 
 * @author yuxuan
 *
 *         2015年4月24日 下午10:13:10
 * 
 * @version 1.0.0
 * 
 */
public class CrmGameParamResponse extends ResponseBase {
	/**
	 * serialVersionUID:TODO（用一句话描述这个变量表示什么）
	 *
	 * @since 1.0.0
	 */

	private static final long serialVersionUID = 1916090864520441338L;
	private String rangea;
	private String rangeb;
	private String amendment;

	/**
	 * rangea
	 *
	 * @return the rangea
	 * @since 1.0.0
	 */

	public String getRangea() {
		return rangea;
	}

	/**
	 * @param rangea
	 *            the rangea to set
	 */
	public void setRangea(String rangea) {
		this.rangea = rangea;
	}

	/**
	 * rangeb
	 *
	 * @return the rangeb
	 * @since 1.0.0
	 */

	public String getRangeb() {
		return rangeb;
	}

	/**
	 * @param rangeb
	 *            the rangeb to set
	 */
	public void setRangeb(String rangeb) {
		this.rangeb = rangeb;
	}

	/**
	 * amendment
	 *
	 * @return the amendment
	 * @since 1.0.0
	 */

	public String getAmendment() {
		return amendment;
	}

	/**
	 * @param amendment
	 *            the amendment to set
	 */
	public void setAmendment(String amendment) {
		this.amendment = amendment;
	}

}
