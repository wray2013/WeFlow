package net.zkbc.framework.fep.message.service;

import net.zkbc.framework.fep.message.protocol.MessageRequest;
import net.zkbc.framework.fep.message.protocol.MessageResponse;

public interface MessageService<REQUEST extends MessageRequest, RESPONSE extends MessageResponse> {

	public void execute(REQUEST request, RESPONSE response);

}
