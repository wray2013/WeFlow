package net.etoc.test;

import java.io.IOException;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Maps;

@Controller
@RequestMapping("/test")
public class TestController {
	@RequestMapping("/index")
	public String index() {
		System.out.println("come on");
		return "test";
	}

	@ResponseBody
	@RequestMapping("/ct")
	public Map<String, String> ct(String json) throws JsonParseException,
			JsonMappingException, IOException {
		ObjectMapper om = new ObjectMapper();
		/*
		 * Map<String, Object> rs = om.readValue(json, new
		 * TypeReference<Map<String, Object>>() { });
		 */
		/*
		 * Tvo tt = om.readValue(json, Tvo.class); System.out.println(tt);
		 */
		/*
		 * System.out.println(json); System.out.println(sign);
		 */
		// System.out.println(MD5.encodeByMd5AndSalt(json));
		/*
		 * Map<String, String> m = Maps.newHashMap(); m.put("errcode", "999");
		 * m.put("msg", "这是一个测试返回"); WfMgtUser u = new WfMgtUser();
		 * u.setId(111); u.setNickname("ljj");
		 */

		/*
		 * WfMgtUser u = new WfMgtUser();
		 * 
		 * u.setId(111); u.setNickname("ljj");
		 */

		/*
		 * ObjectMapper om = new ObjectMapper(); try { Tvo t = om.readValue(p,
		 * Tvo.class); System.out.println(t.getJson());
		 * System.out.println(t.getSign()); } catch (JsonParseException e) { //
		 * TODO Auto-generated catch block e.printStackTrace(); } catch
		 * (JsonMappingException e) { // TODO Auto-generated catch block
		 * e.printStackTrace(); } catch (IOException e) { // TODO Auto-generated
		 * catch block e.printStackTrace(); }
		 */

		Map<String, String> rb = Maps.newHashMap();
		rb.put("code", "ss");
		rb.put("message", "记录看见了");

		return rb;

	}
}
