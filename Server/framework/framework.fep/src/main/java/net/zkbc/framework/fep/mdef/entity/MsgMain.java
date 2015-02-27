package net.zkbc.framework.fep.mdef.entity;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name = "msg_main")
public class MsgMain {

	private String id;
	private String classific;
	private String description;
	private String encryptRequest;
	private String signRequest;
	private String encryptResponse;
	private String signResponse;

	private List<MsgRequest> requestElements;
	private List<MsgResponse> responseElements;

	@Id
	@Column(length = 100)
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	@Column(length = 20)
	public String getClassific() {
		return classific;
	}

	public void setClassific(String classific) {
		this.classific = classific;
	}

	@Column(length = 255)
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Column(length = 1)
	public String getEncryptRequest() {
		return encryptRequest;
	}

	public void setEncryptRequest(String encryptRequest) {
		this.encryptRequest = encryptRequest;
	}

	@Column(length = 1)
	public String getSignRequest() {
		return signRequest;
	}

	public void setSignRequest(String signRequest) {
		this.signRequest = signRequest;
	}

	@Column(length = 1)
	public String getEncryptResponse() {
		return encryptResponse;
	}

	public void setEncryptResponse(String encryptResponse) {
		this.encryptResponse = encryptResponse;
	}

	@Column(length = 1)
	public String getSignResponse() {
		return signResponse;
	}

	public void setSignResponse(String signResponse) {
		this.signResponse = signResponse;
	}

	@OneToMany
	@JoinColumn(name = "msgId")
	public List<MsgRequest> getRequestElements() {
		return requestElements;
	}

	public void setRequestElements(List<MsgRequest> requestElements) {
		this.requestElements = requestElements;
	}

	@OneToMany
	@JoinColumn(name = "msgId")
	public List<MsgResponse> getResponseElements() {
		return responseElements;
	}

	public void setResponseElements(List<MsgResponse> responseElements) {
		this.responseElements = responseElements;
	}

}