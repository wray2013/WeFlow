/**
 * 创建时间
 * 2015年3月20日-下午9:30:11
 * 
 * 
 */
package net.etoc.wf.ctapp.user.entity;

import java.io.Serializable;

/**
 * 
 * @author yuxuan
 *
 *         2015年3月20日 下午9:30:11
 * 
 * @version 1.0.0
 * 
 */
public class OrderRel implements Serializable {

	/**
	 * serialVersionUID:TODO（用一句话描述这个变量表示什么）
	 *
	 * @since 1.0.0
	 */

	private static final long serialVersionUID = -7335443839988146467L;

	private String type;

	private String productid;

	private String cost;

	private String title;

	private String date;

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

	/**
	 * title
	 *
	 * @return the title
	 * @since 1.0.0
	 */

	public String getTitle() {
		return title;
	}

	/**
	 * @param title
	 *            the title to set
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * date
	 *
	 * @return the date
	 * @since 1.0.0
	 */

	public String getDate() {
		return date;
	}

	/**
	 * @param date
	 *            the date to set
	 */
	public void setDate(String date) {
		this.date = date;
	}

}
