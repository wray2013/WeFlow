/**
 * 创建时间
 * 2015年4月1日-下午7:46:46
 * 
 * 
 */
package net.etoc.wf.ctapp.user.entity;

import net.etoc.wf.ctapp.base.ResponseBase;

/**
 * 
 * @author yuxuan
 *
 *         2015年4月1日 下午7:46:46
 * 
 * @version 1.0.0
 * 
 */
public class GameConfigResponse extends ResponseBase {
	/**
	 * serialVersionUID:TODO（用一句话描述这个变量表示什么）
	 *
	 * @since 1.0.0
	 */

	private static final long serialVersionUID = 5407053995703995403L;
	private String cost;

	/**
	 * cost
	 *
	 * @return the cost
	 * @since 1.0.0
	 */

	public String getCost() {
		return cost;
	}

	/**
	 * @param cost
	 *            the cost to set
	 */
	public void setCost(String cost) {
		this.cost = cost;
	}

}
