package net.zkbc.framework.fep.security.entity;

import java.io.Serializable;
import java.util.List;

import com.google.common.base.Objects;

public class SimpleUser implements Serializable, User {

	private static final long serialVersionUID = -2860774249260045174L;

	private Long id;
	private String name;
	private String loginName;
	private String password;
	private String plainPassword;
	private String salt;
	private boolean disabled;
	private List<String> roleNames;

	@Override
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Override
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String getLoginName() {
		return loginName;
	}

	public void setLoginName(String loginName) {
		this.loginName = loginName;
	}

	@Override
	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	@Override
	public String getPlainPassword() {
		return plainPassword;
	}

	public void setPlainPassword(String plainPassword) {
		this.plainPassword = plainPassword;
	}

	@Override
	public String getSalt() {
		return salt;
	}

	public void setSalt(String salt) {
		this.salt = salt;
	}

	public boolean isDisabled() {
		return disabled;
	}

	public void setDisabled(boolean disabled) {
		this.disabled = disabled;
	}

	@Override
	public List<String> getRoleNames() {
		return roleNames;
	}

	public void setRoleNames(List<String> roleNames) {
		this.roleNames = roleNames;
	}

	@Override
	public String toString() {
		return loginName;
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(loginName);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof SimpleUser)) {
			return false;
		}

		SimpleUser that = (SimpleUser) obj;
		if (loginName != null && loginName.equals(that.loginName)) {
			return true;
		}

		return loginName == that.loginName;
	}
}