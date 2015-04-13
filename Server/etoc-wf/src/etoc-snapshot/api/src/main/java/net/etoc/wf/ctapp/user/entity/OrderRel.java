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

	private String cardcode;
	private String keycode;

	private String state;

	/**
	 * state
	 *
	 * @return the state
	 * @since 1.0.0
	 */

	public String getState() {
		return state;
	}

	/**
	 * @param state
	 *            the state to set
	 */
	public void setState(String state) {
		this.state = state;
	}

	/**
	 * cardcode
	 *
	 * @return the cardcode
	 * @since 1.0.0
	 */

	public String getCardcode() {
		return cardcode;
	}

	/**
	 * @param cardcode
	 *            the cardcode to set
	 */
	public void setCardcode(String cardcode) {
		this.cardcode = cardcode;
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

	public static class Bill {
		private String type;
		private String productid;
		private String title;
		private String flowcoins;
		private String content;
		private String time;

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
		 * content
		 *
		 * @return the content
		 * @since 1.0.0
		 */

		public String getContent() {
			return content;
		}

		/**
		 * @param content
		 *            the content to set
		 */
		public void setContent(String content) {
			this.content = content;
		}

		/**
		 * time
		 *
		 * @return the time
		 * @since 1.0.0
		 */

		public String getTime() {
			return time;
		}

		/**
		 * @param time
		 *            the time to set
		 */
		public void setTime(String time) {
			this.time = time;
		}

	}
}
