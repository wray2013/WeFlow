/**
 * 创建时间
 * 2015年3月10日-下午11:40:02
 * 
 * 
 */
package net.etoc.crm.product.repository;

import net.etoc.crm.product.entity.WfCrmProduct;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 
 * @author yuxuan
 *
 *         2015年3月10日 下午11:40:02
 * 
 * @version 1.0.0
 * 
 */
public interface WfCrmProductRepository extends
		JpaRepository<WfCrmProduct, Integer> {
	public Page<WfCrmProduct> findByMerchantAndPtype(String merchant,
			String ptype, Pageable pageable);

	public Page<WfCrmProduct> findByMerchantAndPtypeAndPbusinessid(
			String merchant, String ptype, String businessid, Pageable pageable);
}
