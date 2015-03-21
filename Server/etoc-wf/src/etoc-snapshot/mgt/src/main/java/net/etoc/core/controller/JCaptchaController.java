package net.etoc.core.controller;

import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.etoc.wf.shiro.filter.authc.JCaptchaValidateFilter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.octo.captcha.service.image.AbstractManageableImageCaptchaService;

@Controller
public class JCaptchaController {

	@Autowired
	private AbstractManageableImageCaptchaService captchaService;

	@RequestMapping(value = "/jcaptcha/updatePassword", method = RequestMethod.GET)
	public void getUpdatePasswordCaptcha(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		outputCaptcha("updatePassword", request, response);
	}

	private void outputCaptcha(String captchaType, HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		response.setDateHeader("Expires", 0L);
		response.setHeader("Cache-Control",
				"no-store, no-cache, must-revalidate");
		response.addHeader("Cache-Control", "post-check=0, pre-check=0");
		response.setHeader("Pragma", "no-cache");
		response.setContentType("image/jpeg");

		BufferedImage img = captchaService
				.getImageChallengeForID(JCaptchaValidateFilter.getCaptchaID(
						captchaType, request));

		ServletOutputStream out = response.getOutputStream();
		try {
			ImageIO.write(img, "jpg", out);
			out.flush();
		} finally {
			out.close();
		}
	}

}
