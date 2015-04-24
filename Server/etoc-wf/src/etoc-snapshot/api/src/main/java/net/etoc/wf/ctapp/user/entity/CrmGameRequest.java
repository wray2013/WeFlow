/**
 * 创建时间
 * 2015年4月24日-下午9:58:17
 * 
 * 
 */
package net.etoc.wf.ctapp.user.entity;

import net.etoc.wf.ctapp.base.RequestBase;

/**
 * 
 * @author yuxuan
 *
 *         2015年4月24日 下午9:58:17
 * 
 * @version 1.0.0
 * 
 */
public class CrmGameRequest extends RequestBase {
	/**
	 * serialVersionUID:TODO（用一句话描述这个变量表示什么）
	 *
	 * @since 1.0.0
	 */

	private static final long serialVersionUID = -4376997020890902864L;
	private String gameid;
	private String eventid;
	private String flowcoin;

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

	/**
	 * eventid
	 *
	 * @return the eventid
	 * @since 1.0.0
	 */

	public String getEventid() {
		return eventid;
	}

	/**
	 * @param eventid
	 *            the eventid to set
	 */
	public void setEventid(String eventid) {
		this.eventid = eventid;
	}

	/**
	 * flowcoin
	 *
	 * @return the flowcoin
	 * @since 1.0.0
	 */

	public String getFlowcoin() {
		return flowcoin;
	}

	/**
	 * @param flowcoin
	 *            the flowcoin to set
	 */
	public void setFlowcoin(String flowcoin) {
		this.flowcoin = flowcoin;
	}

}
