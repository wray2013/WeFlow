/**
 * 创建时间
 * 2015年3月22日-下午2:45:26
 * 
 * 
 */
package net.etoc.ct.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * 
 * @author yuxuan
 *
 *         2015年3月22日 下午2:45:26
 * 
 * @version 1.0.0
 * 
 */
@Entity
@Table(name = "wf_ct_info")
public class WfCtInfo implements Serializable {
	/**
	 * serialVersionUID:TODO（用一句话描述这个变量表示什么）
	 *
	 * @since 1.0.0
	 */

	private static final long serialVersionUID = 511960941033421008L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int id;

	@Column(name = "version")
	private String version;

	@Column(name = "FILE_PATH")
	private String filePath;

	@Column(name = "FILE_SIZE")
	private String fileSize;

	@Column(name = "description")
	private String description;

	@Column(name = "type")
	private String type;

	@Column(name = "channel")
	private String channel;

	/**
	 * channel
	 *
	 * @return the channel
	 * @since 1.0.0
	 */

	public String getChannel() {
		return channel;
	}

	/**
	 * @param channel
	 *            the channel to set
	 */
	public void setChannel(String channel) {
		this.channel = channel;
	}

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
	 * filePath
	 *
	 * @return the filePath
	 * @since 1.0.0
	 */

	public String getFilePath() {
		return filePath;
	}

	/**
	 * @param filePath
	 *            the filePath to set
	 */
	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	/**
	 * fileSize
	 *
	 * @return the fileSize
	 * @since 1.0.0
	 */

	public String getFileSize() {
		return fileSize;
	}

	/**
	 * @param fileSize
	 *            the fileSize to set
	 */
	public void setFileSize(String fileSize) {
		this.fileSize = fileSize;
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

}
