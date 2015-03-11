package net.etoc.wf.core.util;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.util.StringUtils;
import org.springframework.web.context.ContextLoader;

public class BeanFactoryUtil {

	private static ApplicationContext appCtx;

	public static ApplicationContext getContext() {
		ApplicationContext ctx = ContextLoader
				.getCurrentWebApplicationContext();
		if (ctx == null) {
			System.out.println("实例化spring容器");
			if (appCtx == null) {
				String key = "spring.profiles.active";
				if (StringUtils.isEmpty(System.getProperty(key))) {
					System.setProperty(key, "production");
				}
				appCtx = new ClassPathXmlApplicationContext(new String[] {
						"classpath*:/net/etoc/spring/*.xml",
						"classpath:/spring/*.xml" });
			}
			ctx = appCtx;
		}
		return ctx;
	}
}
