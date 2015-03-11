package net.zkbc.framework.fep.security.service;

import java.io.Serializable;

import net.zkbc.framework.fep.security.entity.User;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.DisabledAccountException;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.mgt.SessionsSecurityManager;
import org.apache.shiro.session.ExpiredSessionException;
import org.apache.shiro.session.InvalidSessionException;
import org.apache.shiro.session.Session;
import org.apache.shiro.session.mgt.DefaultSessionManager;
import org.apache.shiro.session.mgt.ValidatingSession;
import org.apache.shiro.session.mgt.eis.SessionDAO;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
public class ShiroSecurityManager {

	private static final String CONCURRENT = ShiroSecurityManager.class
			.getName() + ".concurrent";

	private static final Logger LOG = LoggerFactory
			.getLogger(ShiroSecurityManager.class);

	@Autowired
	private SessionsSecurityManager securityManager;

	public boolean isExpired(Serializable sessionId) {
		if (sessionId == null) {
			return false;
		}

		try {
			return isExpired(getSessionDAO().readSession(sessionId));
		} catch (Throwable t) {
			LOG.debug(t.getMessage(), t);
			return false;
		}
	}

	public boolean isAuthenticated(Serializable sessionId) {
		if (sessionId == null) {
			return false;
		}

		return getSubject(sessionId).isAuthenticated();
	}

	public boolean isConcurrent(Serializable sessionId) {
		if (sessionId == null) {
			return false;
		}

		Session session = getSubject(sessionId).getSession();
		if (session == null) {
			return false;
		}

		Object concurrent = session.getAttribute(CONCURRENT);
		if (concurrent == null || !(concurrent instanceof Boolean)) {
			return false;
		}

		return (Boolean) concurrent;
	}

	public User getLoginUser(Serializable sessionId) {
		if (!isAuthenticated(sessionId)) {
			return null;
		}

		PrincipalCollection principals = getSubject(sessionId).getPrincipals();
		if (principals != null && !principals.isEmpty()) {
			return (User) principals.getPrimaryPrincipal();
		}

		return null;
	}

	public Serializable login(String loginName, String password, String host)
			throws Exception {
		UsernamePasswordToken token = new UsernamePasswordToken(loginName,
				password, host);

		Subject subject = getSubject(null);
		try {
			subject.login(token);
		} catch (InvalidSessionException e) {
			subject.logout();
			subject.login(token);
		} catch (UnknownAccountException e) {
			throw new AuthenticationException("用户不存在.", e);
		} catch (DisabledAccountException e) {
			throw new AuthenticationException("用户已被锁定.", e);
		} catch (IncorrectCredentialsException e) {
			throw new AuthenticationException("用户名或密码错误.", e);
		} catch (AuthenticationException e) {
			throw e;
		}

		Session session = subject.getSession();
		Serializable sessionId = session.getId();

		LOG.debug("Session with id [{}] startTimestamp:{}", sessionId, session
				.getStartTimestamp().getTime());

		try {
			Thread.sleep(100);
		} catch (Throwable ignored) {
		}

		session.touch();

		LOG.debug("Session with id [{}] lastAccessTime:{}", sessionId, session
				.getLastAccessTime().getTime());

		processConcurrentSessions(sessionId);

		return sessionId;
	}

	public void logout(Serializable sessionId) {
		getSubject(sessionId).logout();
	}

	private void processConcurrentSessions(Serializable currentSessionId) {
		try {
			User currentUser = getLoginUser(currentSessionId);
			if (currentUser == null) {
				return;
			}

			for (Session session : getSessionDAO().getActiveSessions()) {
				Serializable sessionId = session.getId();
				if (sessionId.equals(currentSessionId)) {
					continue;
				}

				User user = getLoginUser(sessionId);
				if (user == null) {
					continue;
				}

				if (user.getLoginName().equals(currentUser.getLoginName())) {
					if (!isExpired(session)) {
						session.setAttribute(CONCURRENT, true);
					}
				}
			}
		} catch (Throwable t) {
			LOG.debug(t.getMessage(), t);
		}
	}

	private SessionDAO getSessionDAO() {
		DefaultSessionManager sessionManager = (DefaultSessionManager) securityManager
				.getSessionManager();
		SessionDAO sessionDAO = sessionManager.getSessionDAO();
		return sessionDAO;
	}

	private boolean isExpired(Session session) {
		if (!(session instanceof ValidatingSession)) {
			session.touch();
			return false;
		}

		try {
			((ValidatingSession) session).validate();
		} catch (ExpiredSessionException e) {
			return true;
		} catch (InvalidSessionException e) {
			return true;
		}

		session.touch();

		return false;
	}

	private Subject getSubject(Serializable sessionId) {
		SecurityUtils.setSecurityManager(securityManager);

		return new Subject.Builder().sessionId(sessionId).buildSubject();
	}

}
