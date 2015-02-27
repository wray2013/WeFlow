package net.zkbc.framework.fep.security.service;

import net.zkbc.framework.fep.security.entity.User;

public interface AccountService {

	User findUserByLoginName(String loginName);

}
