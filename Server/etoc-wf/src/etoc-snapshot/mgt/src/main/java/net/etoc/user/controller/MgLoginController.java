/**
 * 创建时间
 * 2015年3月15日-上午11:45:46
 * 
 * 
 */
package net.etoc.user.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

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
public class MgLoginController {
	@RequestMapping(method = RequestMethod.GET)
	public String login() {
		return "login";
	}
}
