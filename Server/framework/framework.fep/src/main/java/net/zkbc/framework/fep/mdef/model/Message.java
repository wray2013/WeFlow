package net.zkbc.framework.fep.mdef.model;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.util.StringUtils;

public class Message {

	private String id;
	private String description;
	private boolean encryptRequest;
	private boolean signRequest;
	private boolean encryptResponse;
	private boolean signResponse;
	private List<Field> requestFields;
	private List<FieldGroup> requestGroups;
	private List<Field> responseFields;
	private List<FieldGroup> responseGroups;
	private Root root;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return StringUtils.capitalize(id);
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public boolean isEncryptRequest() {
		return encryptRequest;
	}

	public void setEncryptRequest(boolean encryptRequest) {
		this.encryptRequest = encryptRequest;
	}

	public boolean isSignRequest() {
		return signRequest;
	}

	public void setSignRequest(boolean signRequest) {
		this.signRequest = signRequest;
	}

	public boolean isEncryptResponse() {
		return encryptResponse;
	}

	public void setEncryptResponse(boolean encryptResponse) {
		this.encryptResponse = encryptResponse;
	}

	public boolean isSignResponse() {
		return signResponse;
	}

	public void setSignResponse(boolean signResponse) {
		this.signResponse = signResponse;
	}

	public List<Field> getRequestFields() {
		return requestFields;
	}

	public void setRequestFields(List<Field> requestFields) {
		this.requestFields = requestFields;
	}

	public List<FieldGroup> getRequestGroups() {
		return requestGroups;
	}

	public void setRequestGroups(List<FieldGroup> requestGroups) {
		this.requestGroups = requestGroups;
	}

	public List<Field> getResponseFields() {
		return responseFields;
	}

	public void setResponseFields(List<Field> responseFields) {
		this.responseFields = responseFields;
	}

	public List<FieldGroup> getResponseGroups() {
		return responseGroups;
	}

	public void setResponseGroups(List<FieldGroup> responseGroups) {
		this.responseGroups = responseGroups;
	}

	public Set<String> getValidators() {
		Set<String> validators = new HashSet<String>();
		for (Field field : requestFields) {
			if (field.getVaId1() != null) {
				validators.add(field.getVaId1().substring(1));
			}
			if (field.getVaId2() != null) {
				validators.add(field.getVaId2().substring(1));
			}
		}
		for (FieldGroup group : requestGroups) {
			for (Field field : group.getFields()) {
				if (field.getVaId1() != null) {
					validators.add(field.getVaId1().substring(1));
				}
				if (field.getVaId2() != null) {
					validators.add(field.getVaId2().substring(1));
				}
			}
		}

		return validators;
	}

	public String getJavaPackage() {
		return root.getJavaPackage();
	}

	public String getJavaPackagePath() {
		return root.getJavaPackagePath();
	}

	public String getProject() {
		return root.getProject();
	}

	public String getObjcPrefix() {
		return root.getObjcPrefix();
	}

	public Message getMessage() {
		return this;
	}

	public Root getRoot() {
		return root;
	}

	public void setRoot(Root root) {
		this.root = root;
	}

}
