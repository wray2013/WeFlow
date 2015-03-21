package mgt;

import java.math.BigDecimal;

import net.etoc.ad.entity.WfAdvertise;
import net.etoc.ad.service.WfAdvertiseService;

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
public class AdvertiseServiceTest extends AbstractJUnit4SpringContextTests {
	@Autowired
	WfAdvertiseService service;

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

		for (int i = 0; i < 10; i++) {
			WfAdvertise p = new WfAdvertise();
			p.setCover(serverurl + "a.jpg");
			p.setVideo(serverurl + "b.mp4");
			p.setTitle("变形金刚" + i);
			p.setFlowcoins(new BigDecimal(100));
			p.setDuration(100l);
			p.setIsfinished(0);
			if (i % 2 == 0) {
				p.setRtype("0");
			} else {
				p.setRtype("1");
			}
			service.saveorupdate(p);
		}

	}

	// @Test
	public void get() {

		WfAdvertise bo = service.getAdById(2);
		System.out.println(bo.getTitle());

	}

	@Test
	public void update() {
		/*
		 * WfMgtUser u = wfMgtUserService.getMgtUserById(1);
		 * u.setNickname("ljlkj"); wfMgtUserService.saveORupateMgtUser(u);
		 */
	}

}
