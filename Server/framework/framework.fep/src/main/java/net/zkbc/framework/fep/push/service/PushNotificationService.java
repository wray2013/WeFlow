package net.zkbc.framework.fep.push.service;

import net.zkbc.framework.fep.push.protocol.MessagingMessage;

public interface PushNotificationService {

	public void push(MessagingMessage msg);

}
