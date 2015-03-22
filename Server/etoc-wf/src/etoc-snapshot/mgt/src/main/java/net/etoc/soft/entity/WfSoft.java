/**
 * 创建时间
 * 2015年3月19日-下午2:34:14
 * 
 * 
 */
package net.etoc.soft.entity;

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
 *         2015年3月19日 下午2:34:14
 * 
 * @version 1.0.0
 * 
 */
@Entity
@Table(name = "wf_soft")
public class WfSoft implements Serializable {

	private static final long serialVersionUID = 2486196042586443752L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int appid;

	@Column
	private String title;

	@Column
	private String version;

	@Column
	private String appicon;

	@Column
	private String soft;

	@Column
	private String packagename;

	@Column
	private String apppreview;

	@Column
	private String introduction;

	@Column
	private String instruction;

	@Column
	private BigDecimal flowcoins;

	@Column
	private BigDecimal sharecoins;

	@Column
	private Long size;

	@Column
	private String stype;

	@Column
	private String appbannerpic;

	@Column(columnDefinition = "TIMESTAMP", insertable = false, updatable = false)
	@OrderBy("lastUpdateTime DESC")
	private Timestamp lastUpdateTime;

	@Column
	private String lastUpdateUser;

	@Column
	private String status;

	private String downloadfinishtime;

	/**
	 * downloadfinishtime
	 *
	 * @return the downloadfinishtime
	 * @since 1.0.0
	 */

	public String getDownloadfinishtime() {
		return downloadfinishtime;
	}

	/**
	 * @param downloadfinishtime
	 *            the downloadfinishtime to set
	 */
	public void setDownloadfinishtime(String downloadfinishtime) {
		this.downloadfinishtime = downloadfinishtime;
	}

	/**
	 * appid
	 *
	 * @return the appid
	 * @since 1.0.0
	 */

	public int getAppid() {
		return appid;
	}

	/**
	 * @param appid
	 *            the appid to set
	 */
	public void setAppid(int appid) {
		this.appid = appid;
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
	 * version
	 *
	 * @return the version
	 * @since 1.0.0
	 */

	public String getVersion() {
		return version;
	}

	/**
	 * @param version
	 *            the version to set
	 */
	public void setVersion(String version) {
		this.version = version;
	}

	/**
	 * appicon
	 *
	 * @return the appicon
	 * @since 1.0.0
	 */

	public String getAppicon() {
		return appicon;
	}

	/**
	 * @param appicon
	 *            the appicon to set
	 */
	public void setAppicon(String appicon) {
		this.appicon = appicon;
	}

	/**
	 * apppreview
	 *
	 * @return the apppreview
	 * @since 1.0.0
	 */

	public String getApppreview() {
		return apppreview;
	}

	/**
	 * @param apppreview
	 *            the apppreview to set
	 */
	public void setApppreview(String apppreview) {
		this.apppreview = apppreview;
	}

	/**
	 * introduction
	 *
	 * @return the introduction
	 * @since 1.0.0
	 */

	public String getIntroduction() {
		return introduction;
	}

	/**
	 * @param introduction
	 *            the introduction to set
	 */
	public void setIntroduction(String introduction) {
		this.introduction = introduction;
	}

	/**
	 * instruction
	 *
	 * @return the instruction
	 * @since 1.0.0
	 */

	public String getInstruction() {
		return instruction;
	}

	/**
	 * @param instruction
	 *            the instruction to set
	 */
	public void setInstruction(String instruction) {
		this.instruction = instruction;
	}

	/**
	 * flowcoins
	 *
	 * @return the flowcoins
	 * @since 1.0.0
	 */

	public BigDecimal getFlowcoins() {
		return flowcoins;
	}

	/**
	 * @param flowcoins
	 *            the flowcoins to set
	 */
	public void setFlowcoins(BigDecimal flowcoins) {
		this.flowcoins = flowcoins;
	}

	/**
	 * sharecoins
	 *
	 * @return the sharecoins
	 * @since 1.0.0
	 */

	public BigDecimal getSharecoins() {
		return sharecoins;
	}

	/**
	 * @param sharecoins
	 *            the sharecoins to set
	 */
	public void setSharecoins(BigDecimal sharecoins) {
		this.sharecoins = sharecoins;
	}

	/**
	 * size
	 *
	 * @return the size
	 * @since 1.0.0
	 */

	public Long getSize() {
		return size;
	}

	/**
	 * @param size
	 *            the size to set
	 */
	public void setSize(Long size) {
		this.size = size;
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
	 * soft
	 *
	 * @return the soft
	 * @since 1.0.0
	 */

	public String getSoft() {
		return soft;
	}

	/**
	 * @param soft
	 *            the soft to set
	 */
	public void setSoft(String soft) {
		this.soft = soft;
	}

	/**
	 * packagename
	 *
	 * @return the packagename
	 * @since 1.0.0
	 */

	public String getPackagename() {
		return packagename;
	}

	/**
	 * @param packagename
	 *            the packagename to set
	 */
	public void setPackagename(String packagename) {
		this.packagename = packagename;
	}

	/**
	 * stype
	 *
	 * @return the stype
	 * @since 1.0.0
	 */

	public String getStype() {
		return stype;
	}

	/**
	 * @param stype
	 *            the stype to set
	 */
	public void setStype(String stype) {
		this.stype = stype;
	}

	/**
	 * appbannerpic
	 *
	 * @return the appbannerpic
	 * @since 1.0.0
	 */

	public String getAppbannerpic() {
		return appbannerpic;
	}

	/**
	 * @param appbannerpic
	 *            the appbannerpic to set
	 */
	public void setAppbannerpic(String appbannerpic) {
		this.appbannerpic = appbannerpic;
	}

}
