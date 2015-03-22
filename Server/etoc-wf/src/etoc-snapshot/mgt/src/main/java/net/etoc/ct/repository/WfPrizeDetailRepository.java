/**
 * 创建时间
 * 2015年3月22日-下午8:53:30
 * 
 * 
 */
package net.etoc.ct.repository;

import java.util.List;

import net.etoc.ct.entity.WfPrizeDetail;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * 
 * @author yuxuan
 *
 *         2015年3月22日 下午8:53:30
 * 
 * @version 1.0.0
 * 
 */
public interface WfPrizeDetailRepository extends
		JpaRepository<WfPrizeDetail, Integer> {
	@Query("select prize from WfPrizeDetail prize where prize.prizeCount > 0 and prize.awardway =:awardway order by prize.weight asc")
	public List<WfPrizeDetail> findPrize(@Param("awardway") String awardway);

	@Query("select sum(obj.prizeProba) from WfPrizeDetail obj where obj.prizeCount > 0 and obj.awardway =:awardway  ")
	List<Object> findTotalRange(@Param("awardway") String awardway);

}
