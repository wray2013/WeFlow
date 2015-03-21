/**
 * 创建时间
 * 2015年3月18日-下午4:13:49
 * 
 * 
 */
package net.etoc.wf.ctapp.user.entity;

import net.etoc.wf.ctapp.base.RequestBase;

/**
 * 
 * @author yuxuan
 *
 *         2015年3月18日 下午4:13:49
 * 
 * @version 1.0.0
 * 
 */
public class AdvesterRequest extends RequestBase {
	/**
	 * serialVersionUID:TODO（用一句话描述这个变量表示什么）
	 *
	 * @since 1.0.0
	 */

	private static final long serialVersionUID = -7985399558675002235L;
	private Integer pageno;

	private Integer videoid;

	/**
	 * pageno
	 *
	 * @return the pageno
	 * @since 1.0.0
	 */

	public Integer getPageno() {
		return pageno;
	}

	/**
	 * @param pageno
	 *            the pageno to set
	 */
	public void setPageno(Integer pageno) {
		this.pageno = pageno;
	}

	/**
	 * videoid
	 *
	 * @return the videoid
	 * @since 1.0.0
	 */

	public Integer getVideoid() {
		return videoid;
	}

	/**
	 * @param videoid
	 *            the videoid to set
	 */
	public void setVideoid(Integer videoid) {
		this.videoid = videoid;
	}

}
