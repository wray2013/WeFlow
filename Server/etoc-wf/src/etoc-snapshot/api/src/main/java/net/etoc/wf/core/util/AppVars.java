package net.etoc.wf.core.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component()
public class AppVars {

	private static AppVars instance;

	public static AppVars getInstance() {
		if (instance == null) {
			instance = BeanFactoryUtil.getContext().getBean(AppVars.class);
		}
		return instance;
	}

	@Value("${app.fs.root:/var/fs/bsd/}")
	public String fileSystemRoot;

	@Value("${app.shiro.hashAlgorithmName:SHA-1}")
	public String hashAlgorithmName;

	@Value("${app.shiro.hashIterations:1}")
	public String hashIterations;

	@Value("default")
	public String theme;

	@Value("${crm.url:http://113.57.243.18:8090/interface/service/}")
	public String crmUrl;

	@Value("${website.title:流量银行}")
	public String websiteTitle;

	@Value("${servicePhone:xxxxxxxxx}")
	public String servicePhone;

	/**
	 * servicePhone
	 *
	 * @return the servicePhone
	 * @since 1.0.0
	 */

	public String getServicePhone() {
		return servicePhone;
	}

	/**
	 * @param servicePhone
	 *            the servicePhone to set
	 */
	public void setServicePhone(String servicePhone) {
		this.servicePhone = servicePhone;
	}

	/**
	 * websiteTitle
	 *
	 * @return the websiteTitle
	 * @since 1.0.0
	 */

	public String getWebsiteTitle() {
		return websiteTitle;
	}

	/**
	 * @param websiteTitle
	 *            the websiteTitle to set
	 */
	public void setWebsiteTitle(String websiteTitle) {
		this.websiteTitle = websiteTitle;
	}

}
