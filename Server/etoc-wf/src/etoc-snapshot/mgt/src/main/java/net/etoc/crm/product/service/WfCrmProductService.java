/**
 * 创建时间
 * 2015年3月10日-下午11:41:48
 * 
 * 
 */
package net.etoc.crm.product.service;

import net.etoc.crm.product.entity.WfCrmProduct;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * 
 * @author yuxuan
 *
 *         2015年3月10日 下午11:41:48
 * 
 * @version 1.0.0
 * 
 */
public interface WfCrmProductService {
	void saveOrupdate(WfCrmProduct p);

	void delete(Integer id);

	WfCrmProduct findById(Integer id);

	public Page<WfCrmProduct> findBymerchantAndptype(String merchant,
			String ptype, Pageable pageable);

	Page<WfCrmProduct> findBymerchantAndptypeAndpbusinessid(String merchant,
			String ptype, String businessid, Pageable pageable);
}
