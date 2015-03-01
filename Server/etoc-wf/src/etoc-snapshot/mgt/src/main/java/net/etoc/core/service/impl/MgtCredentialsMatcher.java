package net.etoc.core.service.impl;

import net.etoc.user.entity.User;
import net.etoc.user.service.UserService;
import net.etoc.wf.core.util.MD5;

import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authc.credential.CredentialsMatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

public class MgtCredentialsMatcher implements CredentialsMatcher {
	@Autowired
	private UserService UserService;

	@Override
	public boolean doCredentialsMatch(AuthenticationToken token,
			AuthenticationInfo info) {
		UsernamePasswordToken userToken = (UsernamePasswordToken) token;
		User user;
		boolean rs = false;
		try {
			user = UserService.getUserByNickname(userToken.getUsername());
			rs = info.getCredentials()
					.equals(MD5.encodeByMd5AndSalt(new String(userToken
							.getPassword())));
			if (!rs) {
				String union = StringUtils.isEmpty(user.getQqUId()) ? user
						.getWeiboUId() : user.getQqUId();
				rs = union.equals(new String(userToken.getPassword()));
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return rs;
	}
}
