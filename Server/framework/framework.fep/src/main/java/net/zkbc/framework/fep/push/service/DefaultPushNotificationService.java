package net.zkbc.framework.fep.push.service;

import java.util.List;
import java.util.Properties;

import net.zkbc.framework.fep.push.protocol.MessagingMessage;


public class DefaultPushNotificationService implements PushNotificationService {

	private String username;
	private String password;
	private int keepAliveInterval;
	private MessagingMessage willMessage;
	private boolean useSSL;
	private Properties sslProperties;

	private List<Pusher> pusherList;

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public int getKeepAliveInterval() {
		return keepAliveInterval;
	}

	public void setKeepAliveInterval(int keepAliveInterval) {
		this.keepAliveInterval = keepAliveInterval;
	}

	public MessagingMessage getWillMessage() {
		return willMessage;
	}

	public void setWillMessage(MessagingMessage willMessage) {
		this.willMessage = willMessage;
	}

	public boolean isUseSSL() {
		return useSSL;
	}

	public void setUseSSL(boolean useSSL) {
		this.useSSL = useSSL;
	}

	public Properties getSslProperties() {
		return sslProperties;
	}

	public void setSslProperties(Properties sslProperties) {
		this.sslProperties = sslProperties;
	}

	public List<Pusher> getPusherList() {
		return pusherList;
	}

	public void setPusherList(List<Pusher> pusherList) {
		this.pusherList = pusherList;
	}

	@Override
	public void push(MessagingMessage msg) {
		for (Pusher pusher : pusherList) {
			if (!pusher.support(msg)) {
				continue;
			}

			pusher.connect(username, password, keepAliveInterval, willMessage,
					useSSL, sslProperties);
			pusher.push(msg);
		}
	}

}
