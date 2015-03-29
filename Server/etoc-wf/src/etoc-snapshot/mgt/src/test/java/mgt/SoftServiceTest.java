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

	private String serverurl = "http://113.57.180.175:8090/export/";

	@Before
	public void before() {
		String key = "spring.profiles.active";
		if (StringUtils.isEmpty(System.getProperty(key))) {
			System.setProperty(key, "test");
		}
	}

	@Test
	public void save() {
		StringBuffer sb = new StringBuffer();
		String s = "折800(原淘800)客户端是中国最大的独立团购导航网站团800网(www.tuan800.com)为手机用户推出的超值网购应用。聚合淘宝、聚划算、天猫等9块9包邮淘品，及京东、当当、苏宁易购、亚马逊等给力折扣信息。虽然更名，但还是给力的商品，还是独家的折扣，折800网址为http://www.zhe800.com。客户端用户还有专享超值淘品，每日更新，每天实惠。";
		WfSoft p = new WfSoft();
		p.setAppicon("http://p4.qhimg.com/t0126f80f67aed87c3d.png");
		sb.append("http://p4.qhimg.com/t017ed324d7fbe52ecb.jpg,");
		sb.append("http://p4.qhimg.com/t0179d78fc0b95edf37.jpg,");
		sb.append("http://p4.qhimg.com/t0164c1e5e47def93f4.jpg,");
		sb.append("http://p4.qhimg.com/t01ff198cde9c32f277.jpg");
		p.setApppreview(sb.toString());
		p.setIntroduction("专业编辑选商品，每天几百款商品不停息更新推荐。");
		p.setInstruction(s);
		p.setFlowcoins(new BigDecimal(20));
		p.setSharecoins(new BigDecimal(20));
		p.setSize("9.02M");
		p.setSoft(serverurl + "z800.apk");
		p.setStatus("1");
		p.setTitle("折800");
		p.setPackagename("com.tuan800.tao800");

		service.saveOrupdate(p);

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
