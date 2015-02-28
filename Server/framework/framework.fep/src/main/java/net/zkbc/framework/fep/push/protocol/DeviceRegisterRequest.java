package net.zkbc.framework.fep.push.protocol;

import net.zkbc.framework.fep.message.protocol.MessageRequest;


public class DeviceRegisterRequest extends MessageRequest {
	private String deviceToken;
	private String deviceid;

	public String getDeviceToken() {
		return deviceToken;
	}

	public void setDeviceToken(String deviceToken) {
		this.deviceToken = deviceToken;
	}

	public String getDeviceid() {
		return deviceid;
	}

	public void setDeviceid(String deviceid) {
		this.deviceid = deviceid;
	}
}
