/**
 * 
 */
package net.etoc.user.repository;

import net.etoc.user.entity.WfMgtUser;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author Administrator
 *
 */
public interface WfMgtUserRepository extends JpaRepository<WfMgtUser, Integer> {
	Page<WfMgtUser> findByNickname(String lastname, Pageable pageable);
}
