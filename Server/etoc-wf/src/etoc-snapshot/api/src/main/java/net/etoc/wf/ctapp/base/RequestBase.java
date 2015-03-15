/**
 * 创建时间
 * 2015年3月14日-下午12:26:17
 * 
 * 
 */
package net.etoc.wf.ctapp.base;

import java.io.Serializable;

import net.etoc.wf.core.util.RandomUtils;

import org.springframework.util.StringUtils;

/**
 * 
 * @author yuxuan
 *
 *         2015年3月14日 下午12:26:17
 * 
 * @version 1.0.0
 * 
 */
public class RequestBase implements Serializable {
	/**
	 * serialVersionUID:TODO（用一句话描述这个变量表示什么）
	 *
	 * @since 1.0.0
	 */

	private static final long serialVersionUID = 5349107211488567L;
	private String userid;
	private String tel;
	private String mac;
	private String imei;
	private String sign;
	private String channelid;
	private String transid;

	public RequestBase() {
		if (StringUtils.isEmpty(channelid)) {
			this.channelid = "app";
		}
		if (StringUtils.isEmpty(transid)) {
			this.transid = RandomUtils.uuid2();
		}
	}

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
	 * tel
	 *
	 * @return the tel
	 * @since 1.0.0
	 */

	public String getTel() {
		return tel;
	}

	/**
	 * @param tel
	 *            the tel to set
	 */
	public void setTel(String tel) {
		this.tel = tel;
	}

	/**
	 * mac
	 *
	 * @return the mac
	 * @since 1.0.0
	 */

	public String getMac() {
		return mac;
	}

	/**
	 * @param mac
	 *            the mac to set
	 */
	public void setMac(String mac) {
		this.mac = mac;
	}

	/**
	 * imei
	 *
	 * @return the imei
	 * @since 1.0.0
	 */

	public String getImei() {
		return imei;
	}

	/**
	 * @param imei
	 *            the imei to set
	 */
	public void setImei(String imei) {
		this.imei = imei;
	}

	/**
	 * sign
	 *
	 * @return the sign
	 * @since 1.0.0
	 */

	public String getSign() {
		return sign;
	}

	/**
	 * @param sign
	 *            the sign to set
	 */
	public void setSign(String sign) {
		this.sign = sign;
	}

	/**
	 * channelid
	 *
	 * @return the channelid
	 * @since 1.0.0
	 */

	public String getChannelid() {
		return channelid;
	}

	/**
	 * @param channelid
	 *            the channelid to set
	 */
	public void setChannelid(String channelid) {
		this.channelid = channelid;

	}

	/**
	 * transid
	 *
	 * @return the transid
	 * @since 1.0.0
	 */

	public String getTransid() {
		return transid;
	}

	/**
	 * @param transid
	 *            the transid to set
	 */
	public void setTransid(String transid) {
		this.transid = transid;

	}

}
