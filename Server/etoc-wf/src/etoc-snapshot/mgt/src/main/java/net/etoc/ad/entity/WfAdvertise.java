/**
 * 创建时间
 * 2015年3月10日-下午11:08:20
 * 
 * 
 */
package net.etoc.ad.entity;

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
@Table(name = "wf_advertise")
public class WfAdvertise implements Serializable {

	/**
	 * serialVersionUID:TODO（用一句话描述这个变量表示什么）
	 *
	 * @since 1.0.0
	 */

	private static final long serialVersionUID = 8533910221495900753L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int videoid;

	@Column
	private String title;

	@Column
	private String cover;

	@Column
	private String video;

	@Column
	private BigDecimal flowcoins;

	@Column
	private Long duration;

	@Column
	private Integer isfinished;

	@Column
	private Timestamp finishtime;

	@Column
	private Timestamp publishtime;

	@Column
	private Timestamp vtimestart;

	@Column
	private Timestamp vtimeend;

	@Column(columnDefinition = "TIMESTAMP", insertable = false, updatable = false)
	@OrderBy("lastUpdateTime DESC")
	private Timestamp lastUpdateTime;

	@Column
	private String lastUpdateUser;

	@Column
	private String rtype;

	@Column
	private String status;

	@Column
	private String content;

	/**
	 * videoid
	 *
	 * @return the videoid
	 * @since 1.0.0
	 */

	public int getVideoid() {
		return videoid;
	}

	/**
	 * @param videoid
	 *            the videoid to set
	 */
	public void setVideoid(int videoid) {
		this.videoid = videoid;
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
	 * cover
	 *
	 * @return the cover
	 * @since 1.0.0
	 */

	public String getCover() {
		return cover;
	}

	/**
	 * @param cover
	 *            the cover to set
	 */
	public void setCover(String cover) {
		this.cover = cover;
	}

	/**
	 * video
	 *
	 * @return the video
	 * @since 1.0.0
	 */

	public String getVideo() {
		return video;
	}

	/**
	 * @param video
	 *            the video to set
	 */
	public void setVideo(String video) {
		this.video = video;
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
	 * duration
	 *
	 * @return the duration
	 * @since 1.0.0
	 */

	public Long getDuration() {
		return duration;
	}

	/**
	 * @param duration
	 *            the duration to set
	 */
	public void setDuration(Long duration) {
		this.duration = duration;
	}

	/**
	 * isfinished
	 *
	 * @return the isfinished
	 * @since 1.0.0
	 */

	public Integer getIsfinished() {
		return isfinished;
	}

	/**
	 * @param isfinished
	 *            the isfinished to set
	 */
	public void setIsfinished(Integer isfinished) {
		this.isfinished = isfinished;
	}

	/**
	 * finishtime
	 *
	 * @return the finishtime
	 * @since 1.0.0
	 */

	public Timestamp getFinishtime() {
		return finishtime;
	}

	/**
	 * @param finishtime
	 *            the finishtime to set
	 */
	public void setFinishtime(Timestamp finishtime) {
		this.finishtime = finishtime;
	}

	/**
	 * publishtime
	 *
	 * @return the publishtime
	 * @since 1.0.0
	 */

	public Timestamp getPublishtime() {
		return publishtime;
	}

	/**
	 * @param publishtime
	 *            the publishtime to set
	 */
	public void setPublishtime(Timestamp publishtime) {
		this.publishtime = publishtime;
	}

	/**
	 * vtimestart
	 *
	 * @return the vtimestart
	 * @since 1.0.0
	 */

	public Timestamp getVtimestart() {
		return vtimestart;
	}

	/**
	 * @param vtimestart
	 *            the vtimestart to set
	 */
	public void setVtimestart(Timestamp vtimestart) {
		this.vtimestart = vtimestart;
	}

	/**
	 * vtimeend
	 *
	 * @return the vtimeend
	 * @since 1.0.0
	 */

	public Timestamp getVtimeend() {
		return vtimeend;
	}

	/**
	 * @param vtimeend
	 *            the vtimeend to set
	 */
	public void setVtimeend(Timestamp vtimeend) {
		this.vtimeend = vtimeend;
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
	 * rtype
	 *
	 * @return the rtype
	 * @since 1.0.0
	 */

	public String getRtype() {
		return rtype;
	}

	/**
	 * @param rtype
	 *            the rtype to set
	 */
	public void setRtype(String rtype) {
		this.rtype = rtype;
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

}
