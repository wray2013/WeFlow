/**
 * 创建时间
 * 2015年3月21日-下午1:23:09
 * 
 * 
 */
package net.etoc.wf.ctapp.user.entity;

import java.util.List;

/**
 * 
 * @author yuxuan
 *
 *         2015年3月21日 下午1:23:09
 * 
 * @version 1.0.0
 * 
 */
public class AppProductResponse {
	public static class PhoneChargeListResp {
		private String status;
		private List<RechargePhoneResp> chargelist;

		/**
		 * status
		 *
		 * @return the status
		 * @since 1.0.0
		 */

		public String getStatus() {
			return status;
		}

		/**
		 * @param status
		 *            the status to set
		 */
		public void setStatus(String status) {
			this.status = status;
		}

		/**
		 * chargelist
		 *
		 * @return the chargelist
		 * @since 1.0.0
		 */

		public List<RechargePhoneResp> getChargelist() {
			return chargelist;
		}

		/**
		 * @param chargelist
		 *            the chargelist to set
		 */
		public void setChargelist(List<RechargePhoneResp> chargelist) {
			this.chargelist = chargelist;
		}

	}

	public static class RechargePhoneResp {
		private String type;// 类型
		private String typename; // 显示类型名 移动话费
		private List<RechargeProduct> products; // 具体产品（面额） 10元

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
		 * typename
		 *
		 * @return the typename
		 * @since 1.0.0
		 */

		public String getTypename() {
			return typename;
		}

		/**
		 * @param typename
		 *            the typename to set
		 */
		public void setTypename(String typename) {
			this.typename = typename;
		}

		/**
		 * products
		 *
		 * @return the products
		 * @since 1.0.0
		 */

		public List<RechargeProduct> getProducts() {
			return products;
		}

		/**
		 * @param products
		 *            the products to set
		 */
		public void setProducts(List<RechargeProduct> products) {
			this.products = products;
		}

	}

	public static class RechargeProduct {
		private String chargesid;// 产品id
		private String money;// 充值面额
		private String cost;// 话费流量币
		private String title;
		private String desc;
		private String state;
		private String cardcode;

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
		 * chargesid
		 *
		 * @return the chargesid
		 * @since 1.0.0
		 */

		public String getChargesid() {
			return chargesid;
		}

		/**
		 * @param chargesid
		 *            the chargesid to set
		 */
		public void setChargesid(String chargesid) {
			this.chargesid = chargesid;
		}

		/**
		 * money
		 *
		 * @return the money
		 * @since 1.0.0
		 */

		public String getMoney() {
			return money;
		}

		/**
		 * @param money
		 *            the money to set
		 */
		public void setMoney(String money) {
			this.money = money;
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
		 * desc
		 *
		 * @return the desc
		 * @since 1.0.0
		 */

		public String getDesc() {
			return desc;
		}

		/**
		 * @param desc
		 *            the desc to set
		 */
		public void setDesc(String desc) {
			this.desc = desc;
		}

	}
}
