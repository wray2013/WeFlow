package net.zkbc.framework.fep.commons.service;

import java.io.IOException;

public interface FileService {

	public String save(byte[] bytes, String fileType) throws IOException;

}