package mgt;

import net.etoc.ct.repository.WfCtInfoRepository;
import net.etoc.ct.service.WfCtInfoService;

import org.apache.commons.lang.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;

@ActiveProfiles("develop")
@ContextConfiguration(locations = { "classpath*:/net/etoc/spring/*.xml",
		"classpath:/spring/*.xml" })
public class CtServiceTest extends AbstractJUnit4SpringContextTests {
	@Autowired
	WfCtInfoService service;
	@Autowired
	WfCtInfoRepository dao;

	private String serverurl = "http://123.56.104.49:7085/export/";

	@Before
	public void before() {
		String key = "spring.profiles.active";
		if (StringUtils.isEmpty(System.getProperty(key))) {
			System.setProperty(key, "develop");
		}
	}

	// @Test
	public void save() {
		/*
		 * WfCtInfo bo = new WfCtInfo(); bo.setDescription("流量客户端");
		 * bo.setFilePath("http://xxxxxx/xxx/xx"); bo.setFileSize("122222");
		 * bo.setVersion("1"); service.saveOrupdate(bo);
		 */
	}

	@Test
	public void get() {
		System.out.println(dao.findByChannel("etoc").getChannel()
				+ "====================");
	}

	@Test
	public void update() {
	}

}
