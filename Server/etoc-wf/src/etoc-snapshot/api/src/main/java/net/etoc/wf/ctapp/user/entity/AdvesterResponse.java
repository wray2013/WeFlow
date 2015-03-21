/**
 * 创建时间
 * 2015年3月18日-下午9:16:52
 * 
 * 
 */
package net.etoc.wf.ctapp.user.entity;

import net.etoc.wf.ctapp.base.ResponseBase;

/**
 * 
 * @author yuxuan
 *
 *         2015年3月18日 下午9:16:52
 * 
 * @version 1.0.0
 * 
 */
public class AdvesterResponse extends ResponseBase {
	/**
	 * serialVersionUID:TODO（用一句话描述这个变量表示什么）
	 *
	 * @since 1.0.0
	 */

	private static final long serialVersionUID = -2975922212958808579L;
	private String title;
	private String content;
	private String cover;
	private String video;
	private String flowcoins;
	private String duration;
	private String publishtime;

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
	 * duration
	 *
	 * @return the duration
	 * @since 1.0.0
	 */

	public String getDuration() {
		return duration;
	}

	/**
	 * @param duration
	 *            the duration to set
	 */
	public void setDuration(String duration) {
		this.duration = duration;
	}

	/**
	 * publishtime
	 *
	 * @return the publishtime
	 * @since 1.0.0
	 */

	public String getPublishtime() {
		return publishtime;
	}

	/**
	 * @param publishtime
	 *            the publishtime to set
	 */
	public void setPublishtime(String publishtime) {
		this.publishtime = publishtime;
	}

}
