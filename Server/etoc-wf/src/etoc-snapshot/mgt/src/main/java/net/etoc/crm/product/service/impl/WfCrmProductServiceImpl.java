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

}
