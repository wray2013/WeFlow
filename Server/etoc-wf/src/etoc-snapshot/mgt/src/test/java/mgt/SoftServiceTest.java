package mgt;

import java.math.BigDecimal;

import net.etoc.soft.entity.WfSoft;
import net.etoc.soft.service.WfSoftService;

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
public class SoftServiceTest extends AbstractJUnit4SpringContextTests {
	@Autowired
	WfSoftService service;

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
			WfSoft p = new WfSoft();
			p.setAppicon("http://192.168.100.114:7076/pr/xxx.jpg");
			p.setApppreview("http://192.168.100.114:7076/pr/xxx1.jpg,http://192.168.100.114:7076/pr/xxx2.jpg");
			p.setIntroduction("支持多人语音、视频");
			p.setInstruction("1、下载apk并安装\n2、打开应用联网使用即可赚取10个流量币");
			p.setFlowcoins(new BigDecimal(20));
			p.setSharecoins(new BigDecimal(20));
			p.setSize(65992l);
			p.setSoft("http://xxx.xxx.xx.xx/sss.apk");
			p.setStatus("1");

			service.saveOrupdate(p);
		}

	}

	@Test
	public void get() {

	}

	@Test
	public void update() {
		/*
		 * WfMgtUser u = wfMgtUserService.getMgtUserById(1);
		 * u.setNickname("ljlkj"); wfMgtUserService.saveORupateMgtUser(u);
		 */
	}

}
