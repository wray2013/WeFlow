package net.zkbc.framework.fep.commons.controller;

import java.io.IOException;

import net.zkbc.framework.fep.commons.service.FileService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.google.common.io.Files;

@Controller
public class FileUploadController {

	@Autowired
	private FileService fileService;

	@RequestMapping(value = "/fileupload/{fileType}", method = RequestMethod.POST)
	@ResponseBody
	public String fileupload(@PathVariable("fileType") String fileType,
			@RequestBody byte[] bytes) {
		try {
			return fileService.save(bytes, fileType);
		} catch (IOException e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}

	@RequestMapping(value = "/formupload", method = RequestMethod.POST)
	@ResponseBody
	public String formupload(@RequestParam("file") MultipartFile mpf) {
		try {
			return fileupload(
					Files.getFileExtension(mpf.getOriginalFilename()),
					mpf.getBytes());
		} catch (IOException e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}

}
