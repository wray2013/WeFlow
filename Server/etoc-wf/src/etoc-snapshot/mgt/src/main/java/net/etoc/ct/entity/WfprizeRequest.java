/**
 * 创建时间
 * 2015年3月22日-下午9:32:52
 * 
 * 
 */
package net.etoc.ct.entity;

import net.etoc.wf.ctapp.base.RequestBase;

/**
 * 
 * @author yuxuan
 *
 *         2015年3月22日 下午9:32:52
 * 
 * @version 1.0.0
 * 
 */
public class WfprizeRequest extends RequestBase {
	/**
	 * serialVersionUID:TODO（用一句话描述这个变量表示什么）
	 *
	 * @since 1.0.0
	 */

	private static final long serialVersionUID = 8480192914771773158L;
	private String awardway;

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

}
