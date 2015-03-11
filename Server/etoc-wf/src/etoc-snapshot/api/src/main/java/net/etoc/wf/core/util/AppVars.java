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
	public Integer hashIterations;

}
