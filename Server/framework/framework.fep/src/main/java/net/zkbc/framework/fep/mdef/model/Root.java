package net.zkbc.framework.fep.mdef.model;

import java.util.List;

public class Root {

	private String javaPackage;

	private String project;

	private List<Message> messages;

	public String getJavaPackage() {
		return javaPackage;
	}

	public void setJavaPackage(String javaPackage) {
		this.javaPackage = javaPackage;
	}

	public String getProject() {
		return project;
	}

	public void setProject(String project) {
		this.project = project;
	}

	public List<Message> getMessages() {
		return messages;
	}

	public void setMessages(List<Message> messages) {
		this.messages = messages;
	}

	public String getJavaPackagePath() {
		return javaPackage.replaceAll("\\.", "/");
	}

	public String getObjcPrefix() {
		return project.toUpperCase();
	}
}
