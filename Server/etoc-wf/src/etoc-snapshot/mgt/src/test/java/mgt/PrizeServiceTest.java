package mgt;

import net.etoc.ct.service.WfPrizeService;

import org.apache.commons.lang.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;

@ActiveProfiles("test")
@ContextConfiguration(locations = { "classpath*:/net/etoc/spring/*.xml",
		"classpath:/spring/*.xml" })
public class PrizeServiceTest extends AbstractJUnit4SpringContextTests {
	@Autowired
	WfPrizeService service;

	private String serverurl = "http://123.56.104.49:7085/export/";

	@Before
	public void before() {
		String key = "spring.profiles.active";
		if (StringUtils.isEmpty(System.getProperty(key))) {
			System.setProperty(key, "test");
		}
	}

	@Test
	public void save() {
		/*
		 * WfPrizeDetail wd = new WfPrizeDetail(); wd.setAwardway("1");
		 * wd.setPrizeCount(500); wd.setPrizeName("100流量币");
		 * wd.setPrizeProba(100); wd.setPrizeProba(20); wd.setWeight(new
		 * BigDecimal(1)); service.saveOrupdate(wd);
		 */
	}

	@Test
	public void get() {
		for (int i = 0; i < 20; i++) {

		}
	}

	@Test
	public void update() {
	}

}
