/**
 * 创建时间
 * 2015年3月9日-下午1:46:37
 * 
 * 
 */
package net.etoc.user.service;

import net.etoc.user.entity.WfMgtUser;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * 
 * @author yuxuan
 *
 *         2015年3月9日 下午1:46:37
 * 
 * @version 1.0.0
 * 
 */
public interface WfMgtUserService {

	/**
	 * 后台用户的保存或修改
	 * 
	 * @param user
	 *            void
	 * @exception
	 * @since 1.0.0
	 */
	void saveORupateMgtUser(WfMgtUser user);

	/**
	 * 后台用户查询
	 * 
	 * @param id
	 * @return WfMgtUser
	 * @exception
	 * @since 1.0.0
	 */
	WfMgtUser getMgtUserById(int id);

	/**
	 * 后台用户的删除
	 * 
	 * @param id
	 *            void
	 * @exception
	 * @since 1.0.0
	 */
	void deleteMgtUserById(int id);

	/**
	 * 分页查询
	 * 
	 * @param lastname
	 * @param pageable
	 * @return Page<WfMgtUser>
	 * @exception
	 * @since 1.0.0
	 */
	Page<WfMgtUser> findByNickname(String lastname, Pageable pageable);

}
