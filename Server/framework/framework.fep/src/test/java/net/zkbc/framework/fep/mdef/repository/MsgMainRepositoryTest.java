package net.zkbc.framework.fep.mdef.repository;

import javax.sql.DataSource;

import net.zkbc.framework.fep.mdef.repository.MsgMainRepository;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;


@ActiveProfiles("test")
@ContextConfiguration(locations = { "classpath*:/mobile/framework/**/applicationContext.xml" })
public class MsgMainRepositoryTest extends
		AbstractTransactionalJUnit4SpringContextTests {
	@Autowired
	private DataSource dataSource;
	@Autowired
	private MsgMainRepository msgMainRepository;

	@Test
	public void findAll() {
		msgMainRepository.findAll();
	}

}
