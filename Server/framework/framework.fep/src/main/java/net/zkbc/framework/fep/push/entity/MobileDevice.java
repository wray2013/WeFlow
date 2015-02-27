package net.zkbc.framework.fep.push.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Entity
@SequenceGenerator(name = "PnsDeviceSeq", sequenceName = "PNSDEVICE_SEQUENCE")
@Table(name = "pns_device")
public class MobileDevice {

	@Id
	@GeneratedValue(generator = "PnsDeviceSeq")
	private Long id;

	private String deviceid;

	private String deviceToken;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getDeviceid() {
		return deviceid;
	}

	public void setDeviceid(String deviceid) {
		this.deviceid = deviceid;
	}

	public String getDeviceToken() {
		return deviceToken;
	}

	public void setDeviceToken(String deviceToken) {
		this.deviceToken = deviceToken;
	}

}
