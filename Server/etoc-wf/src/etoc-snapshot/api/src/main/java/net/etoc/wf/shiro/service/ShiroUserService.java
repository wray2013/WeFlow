package net.etoc.wf.shiro.service;

import net.etoc.wf.shiro.entity.ShiroUser;

public interface ShiroUserService {

	public ShiroUser findByLoginName(String loginName);

	public byte[] getSaltBytes(ShiroUser user);

	/**
	 * @param loginName
	 * @param host
	 */
	void log(String loginName, String host);

}
