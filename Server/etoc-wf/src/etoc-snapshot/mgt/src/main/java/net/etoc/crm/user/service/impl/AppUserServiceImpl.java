/**
 * 创建时间
 * 2015年3月14日-下午2:28:09
 * 
 * 
 */
package net.etoc.crm.user.service.impl;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import net.etoc.crm.product.entity.WfCrmProduct;
import net.etoc.crm.product.service.WfCrmProductService;
import net.etoc.crm.user.service.AppUserService;
import net.etoc.wf.core.util.AppVars;
import net.etoc.wf.core.util.JsonUtils;
import net.etoc.wf.ctapp.base.RequestBase;
import net.etoc.wf.ctapp.base.ResponseBase;
import net.etoc.wf.ctapp.base.RsCode;
import net.etoc.wf.ctapp.user.entity.AppCrmUserRequest;
import net.etoc.wf.ctapp.user.entity.AppCrmUserResponse;
import net.etoc.wf.ctapp.user.entity.AppProductResponse.PhoneChargeListResp;
import net.etoc.wf.ctapp.user.entity.AppProductResponse.RechargePhoneResp;
import net.etoc.wf.ctapp.user.entity.AppProductResponse.RechargeProduct;
import net.etoc.wf.ctapp.user.entity.AuthCodeRequest;
import net.etoc.wf.ctapp.user.entity.CrmFlowBankResponse;
import net.etoc.wf.ctapp.user.entity.CrmOderHisRequest;
import net.etoc.wf.ctapp.user.entity.CrmOrderHisResponse;
import net.etoc.wf.ctapp.user.entity.CrmOrderRequest;
import net.etoc.wf.ctapp.user.entity.CrmOrderResponse;

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
import com.google.common.collect.Maps;

/**
 * 
 * @author yuxuan
 *
 *         2015年3月14日 下午2:28:09
 * 
 * @version 1.0.0
 * 
 */
@Service
public class AppUserServiceImpl implements AppUserService {
	private static Logger logger = LoggerFactory
			.getLogger(AppUserServiceImpl.class);

	@Autowired
	private JsonUtils jsonUtils;

	@Autowired
	private RestTemplate restTemplate;

	@Autowired
	private WfCrmProductService wfCrmProductService;

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.etoc.crm.user.service.AppUserService#getAuthCode(java.util.Map)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public ResponseBase getAuthCode(String methodSuffix, AuthCodeRequest ar)
			throws RestClientException, JsonProcessingException {
		String rb = restTemplate.postForObject(AppVars.getInstance().crmUrl
				+ methodSuffix, jsonUtils.getJsonResult(ar), String.class);
		logger.info("访问CRM服务器,[{}] 返回的结果 [{}]", AppVars.getInstance().crmUrl
				+ methodSuffix, rb);

		try {
			return (ResponseBase) jsonUtils.getResult(rb, ResponseBase.class);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			logger.error("访问CRM服务器 [{}]  出现错误 [{}]",
					AppVars.getInstance().crmUrl + methodSuffix, e.getMessage());
			e.printStackTrace();
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.etoc.crm.user.service.AppUserService#register(java.lang.String,
	 * net.etoc.wf.ctapp.user.entity.AppCrmUserRequest)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public AppCrmUserResponse loginORregister(String methodSuffix,
			AppCrmUserRequest ar) throws RestClientException,
			JsonProcessingException {
		/*
		 * AppCrmUserResponse rb = restTemplate.postForObject(
		 * AppVars.getInstance().crmUrl + methodSuffix,
		 * jsonUtils.getJsonResult(ar), AppCrmUserResponse.class);
		 */

		String rb = restTemplate.postForObject(AppVars.getInstance().crmUrl
				+ methodSuffix, jsonUtils.getJsonResult(ar), String.class);
		logger.info("访问CRM服务器,[{}] 返回的结果 [{}]", AppVars.getInstance().crmUrl
				+ methodSuffix, rb);

		try {
			return (AppCrmUserResponse) jsonUtils.getResult(rb,
					AppCrmUserResponse.class);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			logger.error("访问CRM服务器 [{}]  出现错误 [{}]",
					AppVars.getInstance().crmUrl + methodSuffix, e.getMessage());
			e.printStackTrace();
		}
		return null;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * net.etoc.crm.user.service.AppUserService#accountInfo(java.lang.String,
	 * net.etoc.wf.ctapp.user.entity.AppCrmUserRequest)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public AppCrmUserResponse accountInfo(String methodSuffix,
			AppCrmUserRequest ar) throws RestClientException,
			JsonProcessingException {
		/*
		 * // TODO Auto-generated method stub AppCrmUserResponse rb =
		 * restTemplate.postForObject( AppVars.getInstance().crmUrl +
		 * methodSuffix, jsonUtils.getJsonResult(ar), AppCrmUserResponse.class);
		 * 
		 * return rb;
		 */

		String rb = restTemplate.postForObject(AppVars.getInstance().crmUrl
				+ methodSuffix, jsonUtils.getJsonResult(ar), String.class);
		logger.info("访问CRM服务器,[{}] 返回的结果 [{}]", AppVars.getInstance().crmUrl
				+ methodSuffix, rb);

		try {
			return (AppCrmUserResponse) jsonUtils.getResult(rb,
					AppCrmUserResponse.class);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			logger.error("访问CRM服务器 [{}]  出现错误 [{}]",
					AppVars.getInstance().crmUrl + methodSuffix, e.getMessage());
			e.printStackTrace();
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public CrmOrderResponse orderLargess(String methodSuffix, CrmOrderRequest ar)
			throws RestClientException, JsonProcessingException {

		String rb = restTemplate.postForObject(AppVars.getInstance().crmUrl
				+ methodSuffix, jsonUtils.getJsonResult(ar), String.class);
		logger.info("访问CRM服务器,[{}] 返回的结果 [{}]", AppVars.getInstance().crmUrl
				+ methodSuffix, rb);

		try {
			return (CrmOrderResponse) jsonUtils.getResult(rb,
					CrmOrderResponse.class);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			logger.error("访问CRM服务器 [{}]  出现错误 [{}]",
					AppVars.getInstance().crmUrl + methodSuffix, e.getMessage());
			e.printStackTrace();
		}
		return null;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * net.etoc.crm.user.service.AppUserService#querySubProdList(java.lang.String
	 * , net.etoc.wf.ctapp.user.entity.CrmOderHisRequest)
	 */
	@Override
	public CrmOrderHisResponse querySubProdList(String methodSuffix,
			CrmOderHisRequest ar) throws RestClientException,
			JsonProcessingException {
		// TODO Auto-generated method stub
		String rb = restTemplate.postForObject(AppVars.getInstance().crmUrl
				+ methodSuffix, jsonUtils.getJsonResult(ar), String.class);
		logger.info("访问CRM服务器,[{}] 返回的结果 [{}]", AppVars.getInstance().crmUrl
				+ methodSuffix, rb);

		try {
			return (CrmOrderHisResponse) jsonUtils.getResult(rb,
					CrmOrderHisResponse.class);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			logger.error("访问CRM服务器 [{}]  出现错误 [{}]",
					AppVars.getInstance().crmUrl + methodSuffix, e.getMessage());
			e.printStackTrace();
		}
		return null;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * net.etoc.crm.user.service.AppUserService#findAppProduct(java.lang.String,
	 * java.lang.String, java.lang.String)
	 */
	@Override
	public PhoneChargeListResp findAppProduct(String merchant, String ptype) {
		Pageable p = new PageRequest(0, 200, new Sort(Sort.Direction.ASC,
				"pbusinessid"));
		Page<WfCrmProduct> page = wfCrmProductService.findBymerchantAndptype(
				merchant, ptype, p);
		if (page == null || page.getContent() == null
				|| page.getContent().size() == 0) {
			return null;
		}

		Map<String, List<WfCrmProduct>> groupMap = Maps.newHashMap();
		List<WfCrmProduct> ct = Lists.newArrayList();
		for (WfCrmProduct tmp : page.getContent()) {
			if (!groupMap.containsKey(tmp.getPbusinessid())) {
				ct.add(tmp);
				groupMap.put(tmp.getPbusinessid(), ct);
			}
		}
		List<WfCrmProduct> boList = null;
		RechargeProduct tmp = null;
		List<RechargeProduct> lrp = null;
		RechargePhoneResp resp = null;
		List<RechargePhoneResp> lresp = Lists.newArrayList();
		;
		PhoneChargeListResp result = new PhoneChargeListResp();
		for (Map.Entry<String, List<WfCrmProduct>> m : groupMap.entrySet()) {
			boList = m.getValue();
			lrp = Lists.newArrayList();
			resp = new RechargePhoneResp();
			resp.setType(m.getKey());
			for (WfCrmProduct bo : boList) {
				tmp = new RechargeProduct();
				tmp.setChargesid(bo.getId() + "");
				tmp.setCost(bo.getPcount() + "");
				tmp.setMoney(bo.getChildbusiness());
				lrp.add(tmp);
				resp.setTypename(bo.getPbusiness());
			}
			resp.setProducts(lrp);
			lresp.add(resp);
		}
		result.setChargelist(lresp);
		result.setStatus(RsCode.OK.getCode());
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * net.etoc.crm.user.service.AppUserService#queryBlance(java.lang.String,
	 * net.etoc.wf.ctapp.base.RequestBase)
	 */
	@Override
	public CrmFlowBankResponse queryBlance(String methodSuffix, RequestBase ar)
			throws RestClientException, JsonProcessingException {
		// TODO Auto-generated method stub
		String rb = restTemplate.postForObject(AppVars.getInstance().crmUrl
				+ methodSuffix, jsonUtils.getJsonResult(ar), String.class);
		logger.info("访问CRM服务器,[{}] 返回的结果 [{}]", AppVars.getInstance().crmUrl
				+ methodSuffix, rb);

		try {
			return (CrmFlowBankResponse) jsonUtils.getResult(rb,
					CrmFlowBankResponse.class);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			logger.error("访问CRM服务器 [{}]  出现错误 [{}]",
					AppVars.getInstance().crmUrl + methodSuffix, e.getMessage());
			e.printStackTrace();
		}
		return null;
	}
}
