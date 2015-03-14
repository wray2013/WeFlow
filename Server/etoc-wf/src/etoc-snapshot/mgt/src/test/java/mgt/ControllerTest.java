/**
 * 创建时间
 * 2015年3月12日-下午1:07:14
 * 
 * 
 */
package mgt;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

/**
 * 
 * @author yuxuan
 *
 *         2015年3月12日 下午1:07:14
 * 
 * @version 1.0.0
 * 
 */
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(locations = { "classpath*:/net/etoc/spring/*.xml",
		"classpath:/spring/*.xml" })
public class ControllerTest {
	@Autowired
	private WebApplicationContext webApplicationContext;
	private MockMvc mockmvc;

	@Before
	public void init() {
		mockmvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
				.build();
	}

	@Test
	public void printBeans() {
		String[] beans = webApplicationContext.getBeanDefinitionNames();
		for (String bean : beans) {
			System.out.println(bean);
		}
	}

	@Test
	public void nomarlRequest() throws Exception {
		mockmvc.perform(
				MockMvcRequestBuilders.post("/test").param("id", "123")
						.param("un", "321"))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.view().name("result"));
	}

	/* 测试将数据以JSON格式写入请求体发送的请求 */
	@Test
	public void testGet() throws Exception {
		/*
		 * mockmvc.perform( MockMvcRequestBuilders .post("/user/getUser")
		 * .contentType("application/json") .content(
		 * JsonUtil.convertObjectToJsonBytes(new User(22,
		 * "werwr")))).andExpect(status().isOk());
		 */
	}

	/* 测试将数据以JSON格式写入请求体发送的请求 */
	@Test
	public void testGetAll() throws IOException, Exception {
		/*
		 * List<User> list = new ArrayList<User>(); list.add(new User(23,
		 * "你爱我")); list.add(new User(25, "我不爱你")); mockMvc.perform(
		 * post("/user/getUsers").contentType(
		 * JsonUtil.APPLICATION_JSON_UTF8).content(
		 * JsonUtil.convertObjectToJsonBytes(list))).andExpect(
		 * status().isOk());
		 */
	}

	/* 测试文件上传发送的请求 */
	@Test
	public void testUpload() throws Exception {
		/*
		 * MockMultipartFile file = new MockMultipartFile("file", "orig.txt",
		 * null, "bar".getBytes());
		 * mockMvc.perform(fileUpload("/user/upload").file(file)).andExpect(
		 * status().isOk());
		 */
	}
}
