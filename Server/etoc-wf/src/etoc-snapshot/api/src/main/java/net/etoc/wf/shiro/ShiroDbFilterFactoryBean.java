package net.etoc.wf.shiro;

import java.io.IOException;
import java.util.Map.Entry;

import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import net.etoc.wf.shiro.entity.ShiroResource;
import net.etoc.wf.shiro.filter.ShiroFilterConfig;
import net.etoc.wf.shiro.service.ShiroResourceService;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.util.AntPathMatcher;
import org.apache.shiro.util.PatternMatcher;
import org.apache.shiro.web.filter.mgt.FilterChainManager;
import org.apache.shiro.web.filter.mgt.FilterChainResolver;
import org.apache.shiro.web.filter.mgt.PathMatchingFilterChainResolver;
import org.apache.shiro.web.mgt.WebSecurityManager;
import org.apache.shiro.web.servlet.AbstractShiroFilter;
import org.apache.shiro.web.util.WebUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StringUtils;

import com.google.common.collect.LinkedHashMultimap;

public class ShiroDbFilterFactoryBean extends ShiroFilterFactoryBean implements
		PathFilter {

	private static transient final Logger log = LoggerFactory
			.getLogger(ShiroDbFilterFactoryBean.class);
	@Value("${app.shiro.includes:/**}")
	private String includes;
	@Value("${app.shiro.excludes:/api/mobile/**,/api/scripts/init.jsp}")
	private String excludes;
	@Autowired
	private ShiroResourceService resourceService;

	private PatternMatcher pathMatcher = new AntPathMatcher();
	private AbstractShiroFilter instance;
	private String basicChainDefinitions;

	@Override
	public void setFilterChainDefinitions(String definitions) {
		basicChainDefinitions = definitions;
		initFilterChainDefinitions();
	}

	@Override
	public boolean isAccessAllowed(String path) {
		Subject subject = SecurityUtils.getSubject();

		for (Entry<String, String> entry : getFilterChainDefinitionMap()
				.entrySet()) {
			if (pathMatcher.matches(entry.getKey(), path)) {
				return ShiroFilterConfig.matches(entry.getValue(), subject);
			}
		}
		return true;
	}

	@Override
	public Object getObject() throws Exception {
		if (instance == null) {
			instance = createInstance();
		}
		return instance;
	}

	@SuppressWarnings("rawtypes")
	public Class getObjectType() {
		return SpringShiroFilter.class;
	}

	public void init() {
		initFilterChainDefinitions();
		try {
			instance = createInstance();
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}

	protected AbstractShiroFilter createInstance() throws Exception {

		log.debug("Creating Shiro Filter instance.");

		SecurityManager securityManager = getSecurityManager();
		if (securityManager == null) {
			String msg = "SecurityManager property must be set.";
			throw new BeanInitializationException(msg);
		}

		if (!(securityManager instanceof WebSecurityManager)) {
			String msg = "The security manager does not implement the WebSecurityManager interface.";
			throw new BeanInitializationException(msg);
		}

		FilterChainManager manager = createFilterChainManager();

		// Expose the constructed FilterChainManager by first wrapping it in a
		// FilterChainResolver implementation. The AbstractShiroFilter
		// implementations
		// do not know about FilterChainManagers - only resolvers:
		PathMatchingFilterChainResolver chainResolver = new PathMatchingFilterChainResolver();
		chainResolver.setFilterChainManager(manager);

		// Now create a concrete ShiroFilter instance and apply the acquired
		// SecurityManager and built
		// FilterChainResolver. It doesn't matter that the instance is an
		// anonymous inner class
		// here - we're just using it because it is a concrete
		// AbstractShiroFilter instance that accepts
		// injection of the SecurityManager and FilterChainResolver:
		SpringShiroFilter springShiroFilter = new SpringShiroFilter(
				(WebSecurityManager) securityManager, chainResolver);
		if (!StringUtils.isEmpty(includes)) {
			springShiroFilter.includes = includes.split(",");
		}
		if (!StringUtils.isEmpty(excludes)) {
			springShiroFilter.excludes = excludes.split(",");
		}
		return springShiroFilter;
	}

	private void initFilterChainDefinitions() {
		LinkedHashMultimap<String, String> cache = LinkedHashMultimap.create();
		for (ShiroResource resource : resourceService.findAll()) {
			String[] configStrings = ShiroFilterConfig
					.buildConfigStrings(resource.getPermission());
			for (String configString : configStrings) {
				cache.put(resource.getPattern(), configString);
			}
		}

		String sep = System.getProperty("line.separator");

		StringBuffer buf = new StringBuffer(basicChainDefinitions);
		for (String pattern : cache.keySet()) {
			String permission = ShiroFilterConfig.merge(cache.get(pattern));
			buf.append(sep).append(pattern).append("=").append(permission);
		}

		super.setFilterChainDefinitions(buf.toString());
	}

	private static final class SpringShiroFilter extends AbstractShiroFilter {

		private String[] includes;
		private String[] excludes;
		private PatternMatcher pathMatcher = new AntPathMatcher();

		protected SpringShiroFilter(WebSecurityManager webSecurityManager,
				FilterChainResolver resolver) {
			super();
			if (webSecurityManager == null) {
				throw new IllegalArgumentException(
						"WebSecurityManager property cannot be null.");
			}
			setSecurityManager(webSecurityManager);
			if (resolver != null) {
				setFilterChainResolver(resolver);
			}
		}

		@Override
		protected boolean isEnabled(ServletRequest request,
				ServletResponse response) throws ServletException, IOException {
			String requestURI = WebUtils.getPathWithinApplication(WebUtils
					.toHttp(request));

			for (String pattern : excludes) {
				if (pathMatcher.matches(pattern, requestURI)) {
					return false;
				}
			}

			if (includes.length > 0) {
				for (String pattern : includes) {
					if (pathMatcher.matches(pattern, requestURI)) {
						return true;
					}
				}
				return false;
			}

			return super.isEnabled(request, response);
		}

	}
}
