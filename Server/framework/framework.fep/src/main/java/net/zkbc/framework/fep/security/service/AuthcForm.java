package net.zkbc.framework.fep.security.service;

public interface AuthcForm<REQUEST, RESPONSE> {

	public String getLoginName(REQUEST request);

	public String getPassword(REQUEST request);

	public void onAuthException(REQUEST request, RESPONSE response, Exception e);

	public void onAuthSuccess(REQUEST request, RESPONSE response);

}
