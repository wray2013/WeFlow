/**
 * 创建时间
 * 2015年3月22日-上午10:25:24
 * 
 * 
 */
package net.etoc.wf.ctapp.user.entity;

import java.util.List;

import net.etoc.wf.ctapp.base.ResponseBase;

/**
 * 
 * @author yuxuan
 *
 *         2015年3月22日 上午10:25:24
 * 
 * @version 1.0.0
 * 
 */
public class CrmBillResponse extends ResponseBase {

	/**
	 * serialVersionUID:TODO（用一句话描述这个变量表示什么）
	 *
	 * @since 1.0.0
	 */

	private static final long serialVersionUID = -1420085229103384936L;
	private List<OrderRel.Bill> list;

	/**
	 * list
	 *
	 * @return the list
	 * @since 1.0.0
	 */

	public List<OrderRel.Bill> getList() {
		return list;
	}

	/**
	 * @param list
	 *            the list to set
	 */
	public void setList(List<OrderRel.Bill> list) {
		this.list = list;
	}

}
