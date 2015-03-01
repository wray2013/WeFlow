package net.etoc.wf.shiro.filter.authc;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import net.etoc.wf.shiro.service.ShiroCaptchaService;

import org.apache.shiro.web.filter.AccessControlFilter;
import org.apache.shiro.web.util.WebUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import com.octo.captcha.service.CaptchaServiceException;

@Component("jcaptchaVf")
@Lazy(false)
public class JCaptchaValidateFilter extends AccessControlFilter {

	public static final String DEFAULT_JCAPTCHA_PARAM = "jcaptcha";

	public static String getCaptchaID(String captchaType,
			HttpServletRequest request) {
		return request.getSession().getId() + "_" + captchaType;
	}

	public static boolean isAccessDenied(HttpServletRequest request) {
		return Boolean.TRUE.equals(request.getAttribute(ACCESS_DENIED));
	}

	private static final String ACCESS_DENIED = JCaptchaValidateFilter.class
			+ ".ACCESS_DENIED";

	private String jcaptchaParam = DEFAULT_JCAPTCHA_PARAM;

	@Autowired(required = false)
	private ShiroCaptchaService captchaService;

	public String getJcaptchaParam() {
		return jcaptchaParam;
	}

	public void setJcaptchaParam(String jcaptchaParam) {
		this.jcaptchaParam = jcaptchaParam;
	}

	@Override
	protected boolean isAccessAllowed(ServletRequest request,
			ServletResponse response, Object mappedValue) throws Exception {

		HttpServletRequest req = WebUtils.toHttp(request);

		if (!"post".equalsIgnoreCase(req.getMethod())) {
			return true;
		}

		if (req.getSession(false) == null) {
			return false;
		}

		String[] captchaConfig = (String[]) mappedValue;

		String captchaType = captchaConfig[0];
		boolean last = captchaConfig.length > 1
				&& "true".equalsIgnoreCase(captchaConfig[1]);
		try {
			return captchaService.validateResponseForID(
					getCaptchaID(captchaType, req),
					req.getParameter(jcaptchaParam), last).booleanValue();
		} catch (CaptchaServiceException e) {
			return false;
		}
	}

	@Override
	protected boolean onAccessDenied(ServletRequest request,
			ServletResponse response) throws Exception {
		request.setAttribute(ACCESS_DENIED, Boolean.TRUE);

		return true;
	}

}
