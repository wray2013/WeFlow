package net.zkbc.framework.fep.commons.service;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.FileCopyUtils;

public class DefaultFileService implements FileService {

	private static final Logger LOG = LoggerFactory
			.getLogger(DefaultFileService.class);

	@Value("${app.annex.root}")
	private String root;

	public String getRoot() {
		return root;
	}

	public void setRoot(String root) {
		this.root = root;
	}

	@Override
	public String save(byte[] bytes, String fileType) throws IOException {

		File rootdir = new File(root);
		rootdir.mkdirs();

		String fileName = UUID.randomUUID().toString() + "." + fileType;

		File file = new File(rootdir, fileName);

		LOG.debug("FileCopyUtils.copy(bytes, {})", file.getAbsolutePath());

		FileCopyUtils.copy(bytes, file);

		return fileName;
	}
}
