/**
 * 创建时间
 * 2015年3月22日-下午3:37:56
 * 
 * 
 */
package net.etoc.ct.entity;

import java.io.Serializable;

/**
 * 
 * @author yuxuan
 *
 *         2015年3月22日 下午3:37:56
 * 
 * @version 1.0.0
 * 
 */
public class CtInfoResponse implements Serializable {
	/**
	 * serialVersionUID:TODO（用一句话描述这个变量表示什么）
	 *
	 * @since 1.0.0
	 */

	private static final long serialVersionUID = 592962011246995948L;
	private String status;
	private String version;
	private String type;
	private String filesize;
	private String filepath;
	private String description;
	private Long servertime;

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
	 * filesize
	 *
	 * @return the filesize
	 * @since 1.0.0
	 */

	public String getFilesize() {
		return filesize;
	}

	/**
	 * @param filesize
	 *            the filesize to set
	 */
	public void setFilesize(String filesize) {
		this.filesize = filesize;
	}

	/**
	 * filepath
	 *
	 * @return the filepath
	 * @since 1.0.0
	 */

	public String getFilepath() {
		return filepath;
	}

	/**
	 * @param filepath
	 *            the filepath to set
	 */
	public void setFilepath(String filepath) {
		this.filepath = filepath;
	}

	/**
	 * description
	 *
	 * @return the description
	 * @since 1.0.0
	 */

	public String getDescription() {
		return description;
	}

	/**
	 * @param description
	 *            the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * servertime
	 *
	 * @return the servertime
	 * @since 1.0.0
	 */

	public Long getServertime() {
		return servertime;
	}

	/**
	 * @param servertime
	 *            the servertime to set
	 */
	public void setServertime(Long servertime) {
		this.servertime = servertime;
	}

}
