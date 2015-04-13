/**
 * 创建时间
 * 2015年3月22日-下午3:36:40
 * 
 * 
 */
package net.etoc.ct.entity;

/**
 * 
 * @author yuxuan
 *
 *         2015年3月22日 下午3:36:40
 * 
 * @version 1.0.0
 * 
 */
public class CtInfoRequest {
	private String devicetype;
	private String internetway;
	private String appversion;
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
	 * devicetype
	 *
	 * @return the devicetype
	 * @since 1.0.0
	 */

	public String getDevicetype() {
		return devicetype;
	}

	/**
	 * @param devicetype
	 *            the devicetype to set
	 */
	public void setDevicetype(String devicetype) {
		this.devicetype = devicetype;
	}

	/**
	 * internetway
	 *
	 * @return the internetway
	 * @since 1.0.0
	 */

	public String getInternetway() {
		return internetway;
	}

	/**
	 * @param internetway
	 *            the internetway to set
	 */
	public void setInternetway(String internetway) {
		this.internetway = internetway;
	}

	/**
	 * appversion
	 *
	 * @return the appversion
	 * @since 1.0.0
	 */

	public String getAppversion() {
		return appversion;
	}

	/**
	 * @param appversion
	 *            the appversion to set
	 */
	public void setAppversion(String appversion) {
		this.appversion = appversion;
	}

}
