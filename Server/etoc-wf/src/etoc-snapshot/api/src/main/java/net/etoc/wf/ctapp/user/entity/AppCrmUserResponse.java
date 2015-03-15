/**
 * 创建时间
 * 2015年3月14日-下午6:54:11
 * 
 * 
 */
package net.etoc.wf.ctapp.user.entity;

import net.etoc.wf.ctapp.base.ResponseBase;

/**
 * 
 * @author yuxuan
 *
 *         2015年3月14日 下午6:54:11
 * 
 * @version 1.0.0
 * 
 */
public class AppCrmUserResponse extends ResponseBase {

	private String userid;

	private String flowcoins; // 流量币

	private String yestdrate; // 昨日年化

	private String yestdincome; // 昨日收益

	private String isregistration; // 是否签到 1 已签 0 未签到

	private String makeflow = "1,2,3"; // 主页显示赚流量途径id列表

	private String useflow = "4,5,6"; // 主页显示赚流量途径id列表

	/**
	 * userid
	 *
	 * @return the userid
	 * @since 1.0.0
	 */

	public String getUserid() {
		return userid;
	}

	/**
	 * @param userid
	 *            the userid to set
	 */
	public void setUserid(String userid) {
		this.userid = userid;
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
	 * yestdrate
	 *
	 * @return the yestdrate
	 * @since 1.0.0
	 */

	public String getYestdrate() {
		return yestdrate;
	}

	/**
	 * @param yestdrate
	 *            the yestdrate to set
	 */
	public void setYestdrate(String yestdrate) {
		this.yestdrate = yestdrate;
	}

	/**
	 * yestdincome
	 *
	 * @return the yestdincome
	 * @since 1.0.0
	 */

	public String getYestdincome() {
		return yestdincome;
	}

	/**
	 * @param yestdincome
	 *            the yestdincome to set
	 */
	public void setYestdincome(String yestdincome) {
		this.yestdincome = yestdincome;
	}

	/**
	 * isregistration
	 *
	 * @return the isregistration
	 * @since 1.0.0
	 */

	public String getIsregistration() {
		return isregistration;
	}

	/**
	 * @param isregistration
	 *            the isregistration to set
	 */
	public void setIsregistration(String isregistration) {
		this.isregistration = isregistration;
	}

	/**
	 * makeflow
	 *
	 * @return the makeflow
	 * @since 1.0.0
	 */

	public String getMakeflow() {
		return makeflow;
	}

	/**
	 * @param makeflow
	 *            the makeflow to set
	 */
	public void setMakeflow(String makeflow) {
		this.makeflow = makeflow;
	}

	/**
	 * useflow
	 *
	 * @return the useflow
	 * @since 1.0.0
	 */

	public String getUseflow() {
		return useflow;
	}

	/**
	 * @param useflow
	 *            the useflow to set
	 */
	public void setUseflow(String useflow) {
		this.useflow = useflow;
	}

}
