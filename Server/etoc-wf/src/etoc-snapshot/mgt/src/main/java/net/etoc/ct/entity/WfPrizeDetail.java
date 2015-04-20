/**
 * 创建时间
 * 2015年3月22日-下午8:47:55
 * 
 * 
 */
package net.etoc.ct.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Version;

/**
 * 
 * @author yuxuan
 *
 *         2015年3月22日 下午8:47:55
 * 
 * @version 1.0.0
 * 
 */
@Entity
@Table(name = "wf_prize_detail")
public class WfPrizeDetail implements Serializable {

	/**
	 * serialVersionUID:TODO（用一句话描述这个变量表示什么）
	 *
	 * @since 1.0.0
	 */

	private static final long serialVersionUID = 955980058950298015L;
	@Id
	@Column(name = "PRIZE_ID")
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int prizeid;

	@Column(name = "PRIZE_COUNT")
	private Integer prizeCount;

	@Column(name = "PRIZE_NAME")
	private String prizename;

	@Column(name = "PRIZE_PROBA")
	private BigDecimal prizeProba;

	@Column(name = "PRIZE_REMARK")
	private String prizeRemark;

	@Version
	@Column(name = "version")
	private Integer version;

	private Integer weight;

	@Column
	private String awardway;

	@Column
	private Timestamp atimestart;

	@Column
	private Timestamp atimeend;

	/**
	 * prizeCount
	 *
	 * @return the prizeCount
	 * @since 1.0.0
	 */

	public Integer getPrizeCount() {
		return prizeCount;
	}

	/**
	 * @param prizeCount
	 *            the prizeCount to set
	 */
	public void setPrizeCount(Integer prizeCount) {
		this.prizeCount = prizeCount;
	}

	/**
	 * prizeProba
	 *
	 * @return the prizeProba
	 * @since 1.0.0
	 */

	public BigDecimal getPrizeProba() {
		return prizeProba;
	}

	/**
	 * @param prizeProba
	 *            the prizeProba to set
	 */
	public void setPrizeProba(BigDecimal prizeProba) {
		this.prizeProba = prizeProba;
	}

	/**
	 * prizeRemark
	 *
	 * @return the prizeRemark
	 * @since 1.0.0
	 */

	public String getPrizeRemark() {
		return prizeRemark;
	}

	/**
	 * @param prizeRemark
	 *            the prizeRemark to set
	 */
	public void setPrizeRemark(String prizeRemark) {
		this.prizeRemark = prizeRemark;
	}

	/**
	 * version
	 *
	 * @return the version
	 * @since 1.0.0
	 */

	public Integer getVersion() {
		return version;
	}

	/**
	 * @param version
	 *            the version to set
	 */
	public void setVersion(Integer version) {
		if (version == null) {
			this.version = 0;
		} else {
			this.version = version;
		}

	}

	/**
	 * weight
	 *
	 * @return the weight
	 * @since 1.0.0
	 */

	public Integer getWeight() {
		return weight;
	}

	/**
	 * @param weight
	 *            the weight to set
	 */
	public void setWeight(Integer weight) {
		this.weight = weight;
	}

	/**
	 * atimestart
	 *
	 * @return the atimestart
	 * @since 1.0.0
	 */

	public Timestamp getAtimestart() {
		return atimestart;
	}

	/**
	 * @param atimestart
	 *            the atimestart to set
	 */
	public void setAtimestart(Timestamp atimestart) {
		this.atimestart = atimestart;
	}

	/**
	 * awardway
	 *
	 * @return the awardway
	 * @since 1.0.0
	 */

	public String getAwardway() {
		return awardway;
	}

	/**
	 * @param awardway
	 *            the awardway to set
	 */
	public void setAwardway(String awardway) {
		this.awardway = awardway;
	}

	/**
	 * atimeend
	 *
	 * @return the atimeend
	 * @since 1.0.0
	 */

	public Timestamp getAtimeend() {
		return atimeend;
	}

	/**
	 * @param atimeend
	 *            the atimeend to set
	 */
	public void setAtimeend(Timestamp atimeend) {
		this.atimeend = atimeend;
	}

	/**
	 * prizeid
	 *
	 * @return the prizeid
	 * @since 1.0.0
	 */

	public int getPrizeid() {
		return prizeid;
	}

	/**
	 * @param prizeid
	 *            the prizeid to set
	 */
	public void setPrizeid(int prizeid) {
		this.prizeid = prizeid;
	}

	/**
	 * prizename
	 *
	 * @return the prizename
	 * @since 1.0.0
	 */

	public String getPrizename() {
		return prizename;
	}

	/**
	 * @param prizename
	 *            the prizename to set
	 */
	public void setPrizename(String prizename) {
		this.prizename = prizename;
	}

}
