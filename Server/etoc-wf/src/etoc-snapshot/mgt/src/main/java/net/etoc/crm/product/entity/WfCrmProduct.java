/**
 * 创建时间
 * 2015年3月10日-下午11:08:20
 * 
 * 
 */
package net.etoc.crm.product.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OrderBy;
import javax.persistence.Table;

/**
 * 
 * @author yuxuan
 *
 *         2015年3月10日 下午11:08:20
 * 
 * @version 1.0.0
 * 
 */
@Entity
@Table(name = "wf_crm_product")
public class WfCrmProduct implements Serializable {

	/**
	 * serialVersionUID:TODO（用一句话描述这个变量表示什么）
	 *
	 * @since 1.0.0
	 */

	private static final long serialVersionUID = 8533910221495900753L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int id;

	@Column
	private String merchant;

	@Column
	private String ptype;

	@Column
	private String pbusinessid;

	@Column
	private String pbusiness;

	@Column
	private String childbusiness;

	@Column
	private BigDecimal pcount;

	@Column
	private String rule;

	@Column
	private Timestamp validTime;

	@Column(columnDefinition = "TIMESTAMP", insertable = false, updatable = false)
	@OrderBy("lastUpdateTime DESC")
	private Timestamp lastUpdateTime;

	@Column
	private String lastUpdateUser;

	@Column
	private String merchant_prodid;

	@Column
	private String status;

	/**
	 * id
	 *
	 * @return the id
	 * @since 1.0.0
	 */

	public int getId() {
		return id;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * merchant
	 *
	 * @return the merchant
	 * @since 1.0.0
	 */

	public String getMerchant() {
		return merchant;
	}

	/**
	 * @param merchant
	 *            the merchant to set
	 */
	public void setMerchant(String merchant) {
		this.merchant = merchant;
	}

	/**
	 * ptype
	 *
	 * @return the ptype
	 * @since 1.0.0
	 */

	public String getPtype() {
		return ptype;
	}

	/**
	 * @param ptype
	 *            the ptype to set
	 */
	public void setPtype(String ptype) {
		this.ptype = ptype;
	}

	/**
	 * pbusiness
	 *
	 * @return the pbusiness
	 * @since 1.0.0
	 */

	public String getPbusiness() {
		return pbusiness;
	}

	/**
	 * @param pbusiness
	 *            the pbusiness to set
	 */
	public void setPbusiness(String pbusiness) {
		this.pbusiness = pbusiness;
	}

	/**
	 * childbusiness
	 *
	 * @return the childbusiness
	 * @since 1.0.0
	 */

	public String getChildbusiness() {
		return childbusiness;
	}

	/**
	 * @param childbusiness
	 *            the childbusiness to set
	 */
	public void setChildbusiness(String childbusiness) {
		this.childbusiness = childbusiness;
	}

	/**
	 * pcount
	 *
	 * @return the pcount
	 * @since 1.0.0
	 */

	public BigDecimal getPcount() {
		return pcount;
	}

	/**
	 * @param pcount
	 *            the pcount to set
	 */
	public void setPcount(BigDecimal pcount) {
		this.pcount = pcount;
	}

	/**
	 * rule
	 *
	 * @return the rule
	 * @since 1.0.0
	 */

	public String getRule() {
		return rule;
	}

	/**
	 * @param rule
	 *            the rule to set
	 */
	public void setRule(String rule) {
		this.rule = rule;
	}

	/**
	 * validTime
	 *
	 * @return the validTime
	 * @since 1.0.0
	 */

	public Timestamp getValidTime() {
		return validTime;
	}

	/**
	 * @param validTime
	 *            the validTime to set
	 */
	public void setValidTime(Timestamp validTime) {
		this.validTime = validTime;
	}

	/**
	 * lastUpdateTime
	 *
	 * @return the lastUpdateTime
	 * @since 1.0.0
	 */

	public Timestamp getLastUpdateTime() {
		return lastUpdateTime;
	}

	/**
	 * @param lastUpdateTime
	 *            the lastUpdateTime to set
	 */
	public void setLastUpdateTime(Timestamp lastUpdateTime) {
		this.lastUpdateTime = lastUpdateTime;
	}

	/**
	 * lastUpdateUser
	 *
	 * @return the lastUpdateUser
	 * @since 1.0.0
	 */

	public String getLastUpdateUser() {
		return lastUpdateUser;
	}

	/**
	 * @param lastUpdateUser
	 *            the lastUpdateUser to set
	 */
	public void setLastUpdateUser(String lastUpdateUser) {
		this.lastUpdateUser = lastUpdateUser;
	}

	/**
	 * merchant_prodid
	 *
	 * @return the merchant_prodid
	 * @since 1.0.0
	 */

	public String getMerchant_prodid() {
		return merchant_prodid;
	}

	/**
	 * @param merchant_prodid
	 *            the merchant_prodid to set
	 */
	public void setMerchant_prodid(String merchant_prodid) {
		this.merchant_prodid = merchant_prodid;
	}

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
	 * pbusinessid
	 *
	 * @return the pbusinessid
	 * @since 1.0.0
	 */

	public String getPbusinessid() {
		return pbusinessid;
	}

	/**
	 * @param pbusinessid
	 *            the pbusinessid to set
	 */
	public void setPbusinessid(String pbusinessid) {
		this.pbusinessid = pbusinessid;
	}

}
