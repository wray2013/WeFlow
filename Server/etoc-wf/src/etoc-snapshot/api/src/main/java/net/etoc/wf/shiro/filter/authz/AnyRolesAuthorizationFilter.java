package net.etoc.wf.shiro.filter.authz;

import java.io.IOException;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.filter.authz.AuthorizationFilter;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

@Component("anyRoles")
@Lazy(false)
public class AnyRolesAuthorizationFilter extends AuthorizationFilter {

	public boolean isAccessAllowed(ServletRequest request,
			ServletResponse response, Object mappedValue) throws IOException {

		String[] roles = (String[]) mappedValue;
		if (roles == null || roles.length == 0) {
			return true;
		}

		Subject subject = getSubject(request, response);

		for (String role : roles) {
			if (subject.hasRole(role)) {
				return true;
			}
		}

		return false;
	}

}
