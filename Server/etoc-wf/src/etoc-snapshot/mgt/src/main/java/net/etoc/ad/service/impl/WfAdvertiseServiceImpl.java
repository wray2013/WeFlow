/**
 * 创建时间
 * 2015年3月17日-下午10:41:55
 * 
 * 
 */
package net.etoc.ad.service.impl;

import java.sql.Timestamp;
import java.util.List;

import net.etoc.ad.entity.WfAdvertise;
import net.etoc.ad.repository.WfAdvertiseRepository;
import net.etoc.ad.service.WfAdvertiseService;
import net.etoc.crm.user.service.AppUserService;
import net.etoc.wf.core.util.JsonUtils;
import net.etoc.wf.ctapp.user.entity.CrmOderHisRequest;
import net.etoc.wf.ctapp.user.entity.CrmOrderHisResponse;
import net.etoc.wf.ctapp.user.entity.OrderRel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.common.collect.Lists;

/**
 * 
 * @author yuxuan
 *
 *         2015年3月17日 下午10:41:55
 * 
 * @version 1.0.0
 * 
 */
@Service
public class WfAdvertiseServiceImpl implements WfAdvertiseService {
	private static Logger logger = LoggerFactory
			.getLogger(WfAdvertiseServiceImpl.class);

	@Autowired
	private WfAdvertiseRepository wfAdvertiseRepository;

	@Autowired
	private RestTemplate restTemplate;

	@Autowired
	private AppUserService appUserService;

	@SuppressWarnings("rawtypes")
	@Autowired
	private JsonUtils jsonUtils;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * net.etoc.ad.service.WfAdvertiseService#saveorupdate(net.etoc.ad.entity
	 * .WfAdvertise)
	 */
	@Override
	public void saveorupdate(WfAdvertise ad) {
		// TODO Auto-generated method stub
		wfAdvertiseRepository.save(ad);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.etoc.ad.service.WfAdvertiseService#getAdById(int)
	 */
	@Override
	public WfAdvertise getAdById(int id) {
		// TODO Auto-generated method stub
		return wfAdvertiseRepository.findOne(id);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.etoc.ad.service.WfAdvertiseService#deleteById(int)
	 */
	@Override
	public void deleteById(int id) {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * net.etoc.ad.service.WfAdvertiseService#getAdByPage(org.springframework
	 * .data.domain.Pageable, java.lang.String)
	 */
	@Override
	public Page<WfAdvertise> findByRtype(String rtype, int page, int size) {

		// TODO Auto-generated method stub
		Pageable pageable = new PageRequest(page, size, new Sort(
				Sort.Direction.DESC, "publishtime"));
		Timestamp ts = new Timestamp(System.currentTimeMillis());
		return wfAdvertiseRepository
				.findByRtypeAndVtimestartBeforeAndVtimeendAfter(rtype, ts, ts,
						pageable);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.etoc.ad.service.WfAdvertiseService#findListByIds(java.util.List)
	 */
	@Override
	public List<WfAdvertise> findListByIds(String methodSuffix,
			CrmOderHisRequest ar) throws RestClientException,
			JsonProcessingException {
		// TODO Auto-generated method stub
		CrmOrderHisResponse crmresult = appUserService.querySubProdList(
				methodSuffix, ar);
		if (crmresult == null) {
			return null;
		}
		List<OrderRel> list = crmresult.getList();
		List<Integer> ids = Lists.newArrayList();
		for (OrderRel bo : list) {
			ids.add(Integer.valueOf(bo.getProductid()));
		}
		return wfAdvertiseRepository.findAll(ids);
	}
}
