package mgt;

import net.etoc.crm.user.service.AppUserService;
import net.etoc.user.service.WfMgtUserService;
import net.etoc.wf.core.util.PMerchant;
import net.etoc.wf.core.util.PType;
import net.etoc.wf.ctapp.user.entity.AppProductResponse.PhoneChargeListResp;

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
public class UserServiceTest extends AbstractJUnit4SpringContextTests {
	@Autowired
	WfMgtUserService wfMgtUserService;

	@Autowired
	AppUserService appUserService;

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
		 * for (int i = 0; i < 20; i++) { WfMgtUser u = new WfMgtUser();
		 * u.setNickname("aa " + i);
		 * u.setPassword(MD5.encodeByMd5AndSalt("111111")); u.setUsername("lo "
		 * + i); wfMgtUserService.saveORupateMgtUser(u); }
		 */
	}

	@Test
	public void get() {
		/*
		 * WfMgtUser u = wfMgtUserService.getMgtUserById(1);
		 * System.out.println(u.getNickname());
		 */

		PhoneChargeListResp pcl = appUserService.findAppProduct(
				PMerchant.fl_charge.getValue(), PType.change_tc.getValue());
		System.out.println(pcl);
	}

	@Test
	public void update() {
		/*
		 * WfMgtUser u = wfMgtUserService.getMgtUserById(1);
		 * u.setNickname("ljlkj"); wfMgtUserService.saveORupateMgtUser(u);
		 */
	}

	@Test
	public void del() {

		/*
		 * Page<WfMgtUser> rs = wfMgtUserService.findByNickname("aa", new
		 * PageRequest(1, 10, new Sort(Sort.Direction.DESC, "nickname")));
		 * System.out.println(rs);
		 */

	}
}
