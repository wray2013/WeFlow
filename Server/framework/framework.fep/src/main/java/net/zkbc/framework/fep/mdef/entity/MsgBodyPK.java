package net.zkbc.framework.fep.mdef.entity;

import java.io.Serializable;

public class MsgBodyPK implements Serializable {

	private static final long serialVersionUID = -609979819276992523L;

	private String msgId;
	private String id;

	public String getMsgId() {
		return msgId;
	}

	public void setMsgId(String msgId) {
		this.msgId = msgId;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	@Override
	public int hashCode() {
		return 31 * (31 + msgId.hashCode()) + id.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}

		if (!(obj instanceof MsgBodyPK)) {
			return false;
		}

		MsgBodyPK o = (MsgBodyPK) obj;

		return msgId.equals(o.msgId) && id.equals(o.id);
	}

}
