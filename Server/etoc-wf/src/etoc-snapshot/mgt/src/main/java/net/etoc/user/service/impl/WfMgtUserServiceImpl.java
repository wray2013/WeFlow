/**
 * 创建时间
 * 2015年3月9日-下午1:59:04
 * 
 * 
 */
package net.etoc.user.service.impl;

import net.etoc.user.entity.WfMgtUser;
import net.etoc.user.repository.WfMgtUserRepository;
import net.etoc.user.service.WfMgtUserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 * 
 * @author yuxuan
 *
 *         2015年3月9日 下午1:59:04
 * 
 * @version 1.0.0
 * 
 */
@Service
public class WfMgtUserServiceImpl implements WfMgtUserService {
	@Autowired
	private WfMgtUserRepository wfMgtUserRepository;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * net.etoc.user.service.WfMgtUserService#saveMgtUser(net.etoc.user.entity
	 * .WfMgtUser)
	 */
	@Override
	public void saveORupateMgtUser(WfMgtUser user) {
		// TODO Auto-generated method stub
		wfMgtUserRepository.save(user);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.etoc.user.service.WfMgtUserService#getMgtUserById(int)
	 */
	@Override
	public WfMgtUser getMgtUserById(int id) {
		// TODO Auto-generated method stub
		return wfMgtUserRepository.findOne(id);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.etoc.user.service.WfMgtUserService#deleteMgtUserById(int)
	 */
	@Override
	public void deleteMgtUserById(int id) {
		// TODO Auto-generated method stub
		wfMgtUserRepository.delete(id);
	}

	@Override
	public Page<WfMgtUser> findByNickname(String lastname, Pageable pageable) {
		// TODO Auto-generated method stub

		return wfMgtUserRepository.findAll(pageable);
	}
}
