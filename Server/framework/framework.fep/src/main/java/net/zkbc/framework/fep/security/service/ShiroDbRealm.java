package net.zkbc.framework.fep.security.service;


import net.zkbc.framework.fep.security.entity.User;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.DisabledAccountException;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authc.credential.CredentialsMatcher;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.util.ByteSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
public class ShiroDbRealm extends AuthorizingRealm {

	@Autowired
	private AccountService accountService;

	@Autowired(required = false)
	@Override
	public void setCredentialsMatcher(CredentialsMatcher credentialsMatcher) {
		super.setCredentialsMatcher(credentialsMatcher);
	}

	@Override
	protected AuthenticationInfo doGetAuthenticationInfo(
			AuthenticationToken authcToken) throws AuthenticationException {

		String loginName = ((UsernamePasswordToken) authcToken).getUsername();

		User user = accountService.findUserByLoginName(loginName);
		if (user == null) {
			throw new UnknownAccountException();
		}

		if (user.isDisabled()) {
			throw new DisabledAccountException();
		}

		try {
			return new SimpleAuthenticationInfo(user, user.getPassword(),
					ByteSource.Util.bytes(getSaltBytes(user)), getName());
		} catch (DecoderException e) {
			throw new RuntimeException(e);
		}

	}

	@Override
	protected AuthorizationInfo doGetAuthorizationInfo(
			PrincipalCollection principals) {

		SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
		info.addRoles(((User) principals.getPrimaryPrincipal()).getRoleNames());

		return info;
	}

	private byte[] getSaltBytes(User user) throws DecoderException {
		String salt = user.getSalt();
		if (salt == null) {
			return null;
		}
		return Hex.decodeHex(salt.toCharArray());
	}

}
