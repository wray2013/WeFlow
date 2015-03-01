package net.etoc.core.service.impl;

import net.etoc.user.entityl.vo.UserVO;
import net.etoc.wf.core.util.AppVars;
import net.etoc.wf.shiro.entity.ShiroUser;
import net.etoc.wf.shiro.service.ShiroUserService;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.crypto.hash.SimpleHash;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("shiroUserService")
public class UserServiceImpl implements ShiroUserService {

	private static final Logger LOG = LoggerFactory
			.getLogger(UserServiceImpl.class);

	private static final String LOGIN_USER = UserServiceImpl.class.getName()
			+ ".loginUser";

	@Autowired
	AppVars appVars;

	public UserVO getLoginUser() {
		Subject subject = SecurityUtils.getSubject();
		if (!subject.isAuthenticated()) {
			return null;
		}

		Session session = subject.getSession(true);
		if (session == null) {
			return null;
		}

		UserVO loginUser = (UserVO) session.getAttribute(LOGIN_USER);
		if (loginUser == null) {
			loginUser = findByLoginName((String) subject.getPrincipal());
			session.setAttribute(LOGIN_USER, loginUser);
		}

		return loginUser;
	}

	@Override
	public UserVO findByLoginName(String loginName) {

		return null;
	}

	@Override
	public byte[] getSaltBytes(ShiroUser user) {
		return null;
	}

	public String encodeUserPassword(String plainPassword, UserVO user) {
		SimpleHash hash = new SimpleHash(appVars.hashAlgorithmName,
				plainPassword, getSaltBytes(user), appVars.hashIterations);
		return hash.toString();
	}

	@Override
	public void log(String loginName, String host) {

	}

}
