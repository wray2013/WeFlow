package net.etoc.core.service.impl;

import net.etoc.user.service.WfMgtUserService;
import net.etoc.wf.core.util.MD5;

import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authc.credential.CredentialsMatcher;
import org.springframework.beans.factory.annotation.Autowired;

public class MgtCredentialsMatcher implements CredentialsMatcher {
	@Autowired
	private WfMgtUserService UserService;

	@Override
	public boolean doCredentialsMatch(AuthenticationToken token,
			AuthenticationInfo info) {
		UsernamePasswordToken userToken = (UsernamePasswordToken) token;
		boolean rs = false;
		try {
			rs = info.getCredentials()
					.equals(MD5.encodeByMd5AndSalt(new String(userToken
							.getPassword())));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return rs;
	}
}
