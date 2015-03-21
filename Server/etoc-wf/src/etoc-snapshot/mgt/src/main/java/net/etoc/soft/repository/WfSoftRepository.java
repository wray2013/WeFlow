/**
 * 创建时间
 * 2015年3月19日-下午9:16:17
 * 
 * 
 */
package net.etoc.soft.repository;

import net.etoc.soft.entity.WfSoft;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 
 * @author yuxuan
 *
 *         2015年3月19日 下午9:16:17
 * 
 * @version 1.0.0
 * 
 */
public interface WfSoftRepository extends JpaRepository<WfSoft, Integer> {
	public Page<WfSoft> findByStype(String stype, Pageable pageable);
}
