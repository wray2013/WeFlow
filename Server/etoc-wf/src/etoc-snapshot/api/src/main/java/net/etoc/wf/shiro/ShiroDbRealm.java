package net.etoc.wf.shiro;

import net.etoc.wf.shiro.entity.ShiroUser;
import net.etoc.wf.shiro.service.ShiroUserService;

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
import org.apache.shiro.cache.Cache;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.SimplePrincipalCollection;
import org.apache.shiro.util.ByteSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ShiroDbRealm extends AuthorizingRealm {
	private static final Logger log = LoggerFactory
			.getLogger(ShiroDbRealm.class);
	@Autowired
	private ShiroUserService shiroUserService;

	@Autowired(required = false)
	@Override
	public void setCredentialsMatcher(CredentialsMatcher credentialsMatcher) {
		super.setCredentialsMatcher(credentialsMatcher);
	}

	public void clearCachedAuthorizationInfo(Object principal) {
		clearCachedAuthorizationInfo(new SimplePrincipalCollection(principal,
				getName()));
	}

	public void clearAllCachedAuthorizationInfo() {
		Cache<Object, AuthorizationInfo> cache = getAuthorizationCache();
		if (cache != null) {
			cache.clear();
		}
	}

	@Override
	protected AuthenticationInfo doGetAuthenticationInfo(
			AuthenticationToken authcToken) throws AuthenticationException {
		UsernamePasswordToken token = (UsernamePasswordToken) authcToken;
		String loginName = token.getUsername();
		String host = token.getHost();
		log.info("userName=[{}],host=[{}]开始登录", loginName, host);
		ShiroUser loginUser = shiroUserService.findByLoginName(loginName);
		if (loginUser == null) {
			throw new UnknownAccountException();
		}

		if (loginUser.isDisabled()) {
			throw new DisabledAccountException();
		}
		ByteSource salt = ByteSource.Util.bytes(shiroUserService
				.getSaltBytes(loginUser));
		shiroUserService.log(loginName, host);
		return new SimpleAuthenticationInfo(loginName, loginUser.getPassword(),
				salt, getName());

	}

	@Override
	protected AuthorizationInfo doGetAuthorizationInfo(
			PrincipalCollection principals) {
		String loginName = (String) principals.getPrimaryPrincipal();
		log.info("userName=[{}]登录成功后获取角色和权限信息", loginName);

		SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();

		ShiroUser loginUser = shiroUserService.findByLoginName(loginName);

		if (loginUser.getRoleNames() != null) {
			info.addRoles(loginUser.getRoleNames());
		}

		if (loginUser.getPermissionNames() != null) {
			info.addStringPermissions(loginUser.getPermissionNames());
		}
		log.info("roleNames=[],stringPermissions=[{}]", info.getRoles(),
				info.getStringPermissions());
		return info;
	}

}
