package net.etoc.user.entityl.vo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import net.etoc.user.entity.User;
import net.etoc.wf.shiro.entity.ShiroUser;

public class UserVO extends User implements Serializable, ShiroUser {

	private static final long serialVersionUID = -7529892007730101510L;

	@Override
	public boolean isDisabled() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getLoginName() {
		// TODO Auto-generated method stub
		return this.getNickName();
	}

	@Override
	public String getPassword() {
		// TODO Auto-generated method stub
		return this.getPasswd();
	}

	@Override
	public Collection<String> getRoleNames() {
		// TODO Auto-generated method stub
		List<String> roles = new ArrayList<String>();
		roles.add(this.getUserRoleId() + "");
		return roles;
	}

	@Override
	public Collection<String> getPermissionNames() {
		// TODO Auto-generated method stub
		List<String> roles = new ArrayList<String>();
		roles.add(this.getUserRoleId() + "");
		return roles;
	}

	@Override
	public String getSalt() {
		// TODO Auto-generated method stub
		return null;
	}

}
