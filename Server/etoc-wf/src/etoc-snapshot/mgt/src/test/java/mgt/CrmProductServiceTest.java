package mgt;

import net.etoc.crm.product.service.WfCrmProductService;

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
public class CrmProductServiceTest extends AbstractJUnit4SpringContextTests {
	@Autowired
	WfCrmProductService WfCrmProductService;

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
		 * WfCrmProduct p = new WfCrmProduct(); p.setMerchant("11");
		 * WfCrmProductService.saveOrupdate(p);
		 */
	}

	@Test
	public void get() {
		/*
		 * WfMgtUser u = wfMgtUserService.getMgtUserById(1);
		 * System.out.println(u.getNickname());
		 */
	}

	@Test
	public void update() {
		/*
		 * WfMgtUser u = wfMgtUserService.getMgtUserById(1);
		 * u.setNickname("ljlkj"); wfMgtUserService.saveORupateMgtUser(u);
		 */
	}

}
