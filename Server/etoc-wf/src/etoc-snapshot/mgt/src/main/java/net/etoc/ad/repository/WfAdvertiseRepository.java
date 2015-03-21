/**
 * 创建时间
 * 2015年3月17日-下午10:29:07
 * 
 * 
 */
package net.etoc.ad.repository;

import java.sql.Timestamp;

import net.etoc.ad.entity.WfAdvertise;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 
 * @author yuxuan
 *
 *         2015年3月17日 下午10:29:07
 * 
 * @version 1.0.0
 * 
 */
public interface WfAdvertiseRepository extends
		JpaRepository<WfAdvertise, Integer> {
	Page<WfAdvertise> findByRtypeAndVtimestartBeforeAndVtimeendAfter(
			String rtype, Timestamp vtimestart, Timestamp vtimeend,
			Pageable pageable);
}
