/**
 * 创建时间
 * 2015年4月24日-下午10:12:14
 * 
 * 
 */
package net.etoc.wf.ctapp.user.entity;

import net.etoc.wf.ctapp.base.RequestBase;

/**
 * 
 * @author yuxuan
 *
 *         2015年4月24日 下午10:12:14
 * 
 * @version 1.0.0
 * 
 */
public class CrmGameParamRequest extends RequestBase {
	private String gameid;

	/**
	 * gameid
	 *
	 * @return the gameid
	 * @since 1.0.0
	 */

	public String getGameid() {
		return gameid;
	}

	/**
	 * @param gameid
	 *            the gameid to set
	 */
	public void setGameid(String gameid) {
		this.gameid = gameid;
	}

}
