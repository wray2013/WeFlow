package net.zkbc.framework.fep.security;

import net.zkbc.framework.fep.security.entity.User;

public interface PasswordEncoder {

	String getEntryptPassword(User user);

}
