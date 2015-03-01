package net.etoc.test;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/test")
public class TestController {
	@RequestMapping("/index")
	public String index() {
		System.out.println("come on");
		return "test";
	}
}
