package net.zkbc.framework.fep.security.entity;

import java.io.Serializable;
import java.util.List;

public interface User {

	public Serializable getId();

	public String getName();

	public String getLoginName();

	public String getPassword();

	public String getPlainPassword();

	public String getSalt();

	public boolean isDisabled();

	public List<String> getRoleNames();

}