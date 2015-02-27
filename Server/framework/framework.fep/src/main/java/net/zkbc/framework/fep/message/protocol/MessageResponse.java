package net.zkbc.framework.fep.message.protocol;

public class MessageResponse extends Message {

	protected static final int SC_TIMEOUT = -200;
	protected static final int SC_FAIL = -1;

	protected int statusCode;
	protected String statusMessage;

	public int getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(int statusCode) {
		this.statusCode = statusCode;
	}

	public String getStatusMessage() {
		return statusMessage;
	}

	public void setStatusMessage(String statusMessage) {
		this.statusMessage = statusMessage;
	}

	public boolean hasError() {
		return statusCode < 0;
	}

	public void fail(String msg) {
		statusCode = SC_FAIL;
		statusMessage = msg;
	}

	public void timeout() {
		statusCode = SC_TIMEOUT;
	}

}
