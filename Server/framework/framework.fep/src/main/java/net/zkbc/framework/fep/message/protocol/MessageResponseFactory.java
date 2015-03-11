package net.zkbc.framework.fep.message.protocol;

public interface MessageResponseFactory<T extends MessageResponse> {

	public T newResponse();

}
