package net.zkbc.framework.fep.push.service;

import java.util.Properties;

import net.zkbc.framework.fep.push.protocol.MessagingMessage;


public interface Pusher {

	public void connect(String username, String password,
			int keepAliveInterval, MessagingMessage willMessage,
			boolean useSSL, Properties sslProperties);

	public void disconnect();

	public void push(MessagingMessage msg);

	public boolean support(MessagingMessage msg);

}
