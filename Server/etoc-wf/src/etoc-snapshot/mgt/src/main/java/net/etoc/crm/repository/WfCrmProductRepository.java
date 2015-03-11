/**
 * 创建时间
 * 2015年3月10日-下午11:40:02
 * 
 * 
 */
package net.etoc.crm.repository;

import net.etoc.crm.entity.WfCrmProduct;

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

}
