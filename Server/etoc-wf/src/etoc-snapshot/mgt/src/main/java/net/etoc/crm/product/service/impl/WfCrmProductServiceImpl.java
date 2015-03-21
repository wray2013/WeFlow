/**
 * 创建时间
 * 2015年3月10日-下午11:42:55
 * 
 * 
 */
package net.etoc.crm.product.service.impl;

import net.etoc.crm.product.entity.WfCrmProduct;
import net.etoc.crm.product.repository.WfCrmProductRepository;
import net.etoc.crm.product.service.WfCrmProductService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 * 
 * @author yuxuan
 *
 *         2015年3月10日 下午11:42:55
 * 
 * @version 1.0.0
 * 
 */
@Service
public class WfCrmProductServiceImpl implements WfCrmProductService {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * net.etoc.crm.service.WfCrmProductService#saveOrupdate(net.etoc.crm.entity
	 * .WfCrmProduct)
	 */

	@Autowired
	private WfCrmProductRepository wfCrmProductRepository;

	@Override
	public void saveOrupdate(WfCrmProduct p) {
		// TODO Auto-generated method stub
		wfCrmProductRepository.save(p);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * net.etoc.crm.product.service.WfCrmProductService#delete(java.lang.Integer
	 * )
	 */
	@Override
	public void delete(Integer id) {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * net.etoc.crm.product.service.WfCrmProductService#findById(java.lang.Integer
	 * )
	 */
	@Override
	public WfCrmProduct findById(Integer id) {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.etoc.crm.product.service.WfCrmProductService#
	 * findBymerchantAndptypeAndpbusinessid(java.lang.String, java.lang.String,
	 * java.lang.String, org.springframework.data.domain.Pageable)
	 */
	@Override
	public Page<WfCrmProduct> findBymerchantAndptypeAndpbusinessid(
			String merchant, String ptype, String businessid, Pageable pageable) {
		// TODO Auto-generated method stub
		return wfCrmProductRepository.findByMerchantAndPtypeAndPbusinessid(
				merchant, ptype, businessid, pageable);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.etoc.crm.product.service.WfCrmProductService#
	 * findBymerchantAndptypeAndpbusinessid(java.lang.String, java.lang.String,
	 * java.lang.String, org.springframework.data.domain.Pageable)
	 */
	@Override
	public Page<WfCrmProduct> findBymerchantAndptype(String merchant,
			String ptype, Pageable pageable) {
		// TODO Auto-generated method stub
		return wfCrmProductRepository.findByMerchantAndPtype(merchant, ptype,
				pageable);
	}

}
