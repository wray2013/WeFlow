/**
 * 创建时间
 * 2015年3月15日-上午11:45:46
 * 
 * 
 */
package net.etoc.user.controller;

import org.apache.shiro.web.filter.authc.FormAuthenticationFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 
 * @author yuxuan
 *
 *         2015年3月15日 上午11:45:46
 * 
 * @version 1.0.0
 * 
 */
@Controller
@RequestMapping(value = "/login")
public class LoginController {

	private static final Logger LOG = LoggerFactory
			.getLogger(LoginController.class);

	@RequestMapping(method = RequestMethod.GET)
	public String login() {
		LOG.debug("method=>login");

		return "login";
	}

	@RequestMapping(method = RequestMethod.POST)
	public String fail(
			@RequestParam(FormAuthenticationFilter.DEFAULT_USERNAME_PARAM) String userName,
			Model model) {
		LOG.debug("method=>fail");

		model.addAttribute(FormAuthenticationFilter.DEFAULT_USERNAME_PARAM,
				userName);

		return "login";
	}

}
