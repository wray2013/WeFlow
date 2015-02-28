package net.zkbc.framework.fep.push.protocol;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;

public class MessagingMessage {

	private String destinationName;
	private String destinationType;
	private byte[] payload;
	private int qos;
	private boolean retained;
	private boolean duplicate;
	private String message;

	public String getDestinationName() {
		return destinationName;
	}

	public void setDestinationName(String destinationName) {
		this.destinationName = destinationName;
	}

	public String getDestinationType() {
		return destinationType;
	}

	public void setDestinationType(String destinationType) {
		this.destinationType = destinationType;
	}

	public byte[] getPayload() {
		return payload;
	}

	public void setPayload(byte[] payload) {
		this.payload = payload;
	}

	public int getQos() {
		return qos;
	}

	public void setQos(int qos) {
		this.qos = qos;
	}

	public boolean isRetained() {
		return retained;
	}

	public void setRetained(boolean retained) {
		this.retained = retained;
	}

	public boolean isDuplicate() {
		return duplicate;
	}

	public void setDuplicate(boolean duplicate) {
		this.duplicate = duplicate;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public byte[] getMessagePayload() {
		if (payload == null && message != null) {
			try {
				return message.getBytes("UTF-8");
			} catch (UnsupportedEncodingException ignored) {
			}
		}

		return payload;
	}

	@Override
	public int hashCode() {
		return safeHash(destinationName) + //
				31 * safeHash(payload) + //
				37 * qos + //
				((retained) ? 43 : 0) + //
				((duplicate) ? 47 : 0) + //
				53 * safeHash(message);
	}

	private int safeHash(Object o) {
		return (o == null) ? 0 : o.hashCode();
	}

	private int safeHash(byte[] b) {
		return (b == null) ? 0 : Arrays.hashCode(b);
	}

	@Override
	public boolean equals(Object o) {
		if (o == null) {
			return false;
		}
		if (!(o instanceof MessagingMessage)) {
			return false;
		}
		MessagingMessage that = (MessagingMessage) o;
		return safeEquals(this.destinationName, that.destinationName) && //
				safeEquals(this.payload, that.payload) && //
				(this.qos == that.qos) && //
				(this.duplicate == that.duplicate) && //
				(this.retained == that.retained) && //
				safeEquals(this.message, that.message);
	}

	private boolean safeEquals(Object o1, Object o2) {
		if ((o1 == null) && (o2 == null)) { // both null - OK
			return true;
		}
		if ((o1 == null) || (o2 == null)) { // only one null
			return false;
		}
		return o1.equals(o2);
	}

	private boolean safeEquals(byte[] b1, byte[] b2) {
		if ((b1 == null) && (b2 == null)) { // both null - OK
			return true;
		}
		if ((b1 == null) || (b2 == null)) { // only one null
			return false;
		}
		return Arrays.equals(b1, b2);
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("DestinationName: {");
		builder.append(destinationName);
		builder.append('}');
		builder.append(" Payload hash: {");
		builder.append(safeHash(payload));
		builder.append('}');
		builder.append(" Quality of Service :");
		builder.append(qos);
		builder.append(" Retained : ");
		builder.append(retained);
		builder.append(" Duplicate : ");
		builder.append(duplicate);
		builder.append(" Message : ");
		builder.append(message);
		builder.append('}');
		return builder.toString();
	}
}
