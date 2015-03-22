/**
 * 创建时间
 * 2015年3月19日-下午9:27:03
 * 
 * 
 */
package net.etoc.soft.service.impl;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import net.etoc.crm.user.service.AppUserService;
import net.etoc.soft.entity.WfSoft;
import net.etoc.soft.repository.WfSoftRepository;
import net.etoc.soft.service.WfSoftService;
import net.etoc.wf.ctapp.user.entity.CrmOderHisRequest;
import net.etoc.wf.ctapp.user.entity.CrmOrderHisResponse;
import net.etoc.wf.ctapp.user.entity.OrderRel;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * 
 * @author yuxuan
 *
 *         2015年3月19日 下午9:27:03
 * 
 * @version 1.0.0
 * 
 */
@Service
public class WfSoftServiceImpl implements WfSoftService {
	@Autowired
	private WfSoftRepository repository;

	@Autowired
	private AppUserService appUserService;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * net.etoc.soft.service.WfSoftService#saveOrupdate(net.etoc.soft.entity
	 * .WfSoft)
	 */
	@Override
	public void saveOrupdate(WfSoft soft) {
		repository.save(soft);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.etoc.soft.service.WfSoftService#delete(java.lang.Integer)
	 */
	@Override
	public void delete(Integer id) {
		repository.delete(id);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.etoc.soft.service.WfSoftService#findById(java.lang.Integer)
	 */
	@Override
	public WfSoft findById(Integer id) {
		// TODO Auto-generated method stub
		return repository.findOne(id);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.etoc.soft.service.WfSoftService#findByPage(int, int)
	 */
	@Override
	public Page<WfSoft> findByPage(String stype, int page, int size) {
		// TODO Auto-generated method stub
		Pageable p = new PageRequest(page, size);
		return repository.findByStype(stype, p);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.etoc.soft.service.WfSoftService#findListByIds(java.lang.String,
	 * net.etoc.wf.ctapp.user.entity.CrmOderHisRequest)
	 */
	@Override
	public List<WfSoft> findListByIds(String methodSuffix, CrmOderHisRequest ar)
			throws RestClientException, JsonProcessingException {
		// TODO Auto-generated method stub
		CrmOrderHisResponse crmresult = appUserService.querySubProdList(
				methodSuffix, ar);
		if (crmresult == null) {
			return null;
		}
		List<OrderRel> list = crmresult.getList();
		if (list == null || list.size() == 0) {
			return null;
		}
		List<Integer> ids = Lists.newArrayList();
		Map<String, OrderRel> tmp = Maps.newHashMap();
		for (OrderRel bo : list) {
			ids.add(Integer.valueOf(bo.getProductid()));
			tmp.put(bo.getProductid(), bo);
		}

		List<WfSoft> rs = repository.findAll(ids);
		OrderRel or = null;
		for (WfSoft ws : rs) {
			or = tmp.get(ws.getAppid());
			ws.setFlowcoins(new BigDecimal(or.getCost()));
			ws.setDownloadfinishtime(or.getDate());
			ws.setTitle(or.getTitle());
		}
		return rs;
	}

}
