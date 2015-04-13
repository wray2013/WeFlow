/**
 * 创建时间
 * 2015年3月20日-下午9:21:16
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
 *         2015年3月20日 下午9:21:16
 * 
 * @version 1.0.0
 * 
 */
public class CrmOrderHisResponse extends ResponseBase {
	/**
	 * serialVersionUID:TODO（用一句话描述这个变量表示什么）
	 *
	 * @since 1.0.0
	 */

	private static final long serialVersionUID = 4700932584860550071L;
	private List<OrderRel> list;

	/**
	 * list
	 *
	 * @return the list
	 * @since 1.0.0
	 */

	public List<OrderRel> getList() {
		return list;
	}

	/**
	 * @param list
	 *            the list to set
	 */
	public void setList(List<OrderRel> list) {
		this.list = list;
	}

}
